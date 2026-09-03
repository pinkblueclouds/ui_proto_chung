package com.example.ui_proto_original;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class SymptomsAdapter extends RecyclerView.Adapter<SymptomsAdapter.ViewHolder> {

    private final List<SymptomDay> symptomDays;

    public SymptomsAdapter(List<SymptomDay> symptomDays) {
        this.symptomDays = symptomDays;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_symptom_day, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SymptomDay day = symptomDays.get(position);

        // Guard against null objects
        if (day == null || day.getFullSymptomList() == null) {
            return;
        }

        holder.itemView.setTag(day.getDate() + "Container");
        holder.textDate.setText(day.getDate());
        holder.symptomsContainer.removeAllViews();

        for (SymptomRecord record : day.getFullSymptomList()) {
            LinearLayout rowLayout = new LinearLayout(holder.itemView.getContext());
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            TextView symptom = new TextView(holder.itemView.getContext());
            symptom.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2.0f));
            symptom.setText(record.getSymptom());
            symptom.setTextSize(16);
            symptom.setTextColor(Color.BLACK);
            symptom.setPadding(16, 0, 0, 0);

            TextView severity = new TextView(holder.itemView.getContext());
            severity.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
            severity.setGravity(Gravity.CENTER);
            severity.setText(record.getSeverity());
            severity.setTextSize(16);
            severity.setTextColor(Color.BLACK);

            TextView timestamp = new TextView(holder.itemView.getContext());
            timestamp.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
            timestamp.setGravity(Gravity.CENTER);
            timestamp.setText(record.getTime());
            timestamp.setTextSize(16);
            timestamp.setTextColor(Color.BLACK);

            rowLayout.addView(symptom);
            rowLayout.addView(severity);
            rowLayout.addView(timestamp);
            holder.symptomsContainer.addView(rowLayout);
        }
    }

    @Override
    public int getItemCount() {
        return symptomDays.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textDate;
        LinearLayout symptomsContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textDate = itemView.findViewById(R.id.textDate);
            symptomsContainer = itemView.findViewById(R.id.symptomsContainer);
        }
    }


}