package com.example.ui_proto_original;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class SparklineAdapter extends RecyclerView.Adapter<SparklineAdapter.SparklineViewHolder> {

    private final List<SparklineItem> items = new ArrayList<>();
    private int graphSpan = 7;

    public void updateData(List<SparklineItem> newItems, int graphSpan) {
        this.items.clear();
        this.items.addAll(newItems);
        this.graphSpan = graphSpan;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SparklineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_symptom_sparkline, parent, false);
        return new SparklineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SparklineViewHolder holder, int position) {
        SparklineItem item = items.get(position);
        holder.bind(item, graphSpan);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class SparklineViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final LineChart sparklineChart;

        public SparklineViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.sparklineSymptomName);
            sparklineChart = itemView.findViewById(R.id.sparklineChart);
        }

        public void bind(SparklineItem item, int graphSpan) {
            tvName.setText(item.getDisplayName());

            setupSparklineChart(sparklineChart, item.getEntries(), item.getColor(), graphSpan);
        }

        private void setupSparklineChart(LineChart chart, ArrayList<Entry> entries, int lineColor, int graphSpan) {
            LineDataSet dataSet = new LineDataSet(entries, "");
            dataSet.setColor(lineColor);
            dataSet.setLineWidth(2f);
            dataSet.setDrawCircles(false);
            dataSet.setDrawValues(false);
            dataSet.setHighlightEnabled(false);
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

            chart.setData(new LineData(dataSet));

            // Strip chart decorations
            chart.getDescription().setEnabled(false);
            chart.getLegend().setEnabled(false);
            chart.setTouchEnabled(false);
            chart.setViewPortOffsets(0f, 0f, 0f, 0f);

            chart.getXAxis().setEnabled(false);
            chart.getAxisLeft().setEnabled(false);
            chart.getAxisRight().setEnabled(false);

            // Force uniform scales across rows
            chart.getAxisLeft().setAxisMinimum(0f);
            chart.getAxisLeft().setAxisMaximum(10f);

            chart.getXAxis().setAxisMinimum(0f);
            chart.getXAxis().setAxisMaximum((float) graphSpan);

            chart.invalidate();
        }
    }
}