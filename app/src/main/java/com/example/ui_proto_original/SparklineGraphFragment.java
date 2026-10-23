package com.example.ui_proto_original;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SparklineGraphFragment extends Fragment {

    private RecyclerView sparklineGraphContainer;
    private SparklineAdapter adapter;
    private SymptomDatabaseHelper dbHelper;
    private int graphSpan = 7; //Default timeframe



    public SparklineGraphFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sparkline_graph, container, false);
        sparklineGraphContainer = view.findViewById(R.id.sparklineChartContainerView);
        sparklineGraphContainer.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new SparklineAdapter();
        sparklineGraphContainer.setAdapter(adapter);

        dbHelper = new SymptomDatabaseHelper(requireContext());

        loadSparklineData();
        return view;
    }

    /**
     * Called when the parent Activity's Spinner selection changes
     */
    public void updateGraphSpan(int newSpan) {
        this.graphSpan = newSpan;
        if (isAdded()) {
            loadSparklineData();
        }
    }

    private void loadSparklineData() {
        if (adapter == null) return;

        ArrayList<SymptomDay> symptomData = dbHelper.getSymptomDataForNDays(graphSpan);
        HashMap<String, HashMap<Double, Integer>> symptomList = splitBySymptom(symptomData);

        ArrayList<SparklineItem> itemList = new ArrayList<>();

        for (Map.Entry<String, HashMap<Double, Integer>> entry : symptomList.entrySet()) {
            String key = entry.getKey();
            HashMap<Double, Integer> dataPoints = entry.getValue();

            ArrayList<Entry> chartEntries = new ArrayList<>();
            //int latestValue = 0;
            double maxDay = -1;

            for (Map.Entry<Double, Integer> point : dataPoints.entrySet()) {
                float x = point.getKey().floatValue();
                float y = point.getValue().floatValue();
                chartEntries.add(new Entry(x, y));

                if (point.getKey() > maxDay) {
                    maxDay = point.getKey();
                }
            }

            chartEntries.sort((e1, e2) -> Float.compare(e1.getX(), e2.getX()));

            itemList.add(new SparklineItem(key, turnKeyIntoDisplay(key), chartEntries, R.color.black));
        }

        // Pass structured list to the adapter
        adapter.updateData(itemList, graphSpan);
    }

    // Helper stubs matching project definitions
    private String turnKeyIntoDisplay(String key) { return key; }
    private HashMap<String, HashMap<Double, Integer>> splitBySymptom(ArrayList<SymptomDay> symptomData) {
        HashMap<String, HashMap<Double, Integer>> symptomsList = new HashMap<>();

        for (SymptomDay dayData : symptomData) {
            for (SymptomRecord record : dayData.getFullSymptomList()) {
                // prevents NullPointerException
                symptomsList.computeIfAbsent(record.getSymptom(), k -> new HashMap<>())
                        .put(getDateTime(dayData.getDate(), record.getTime()), Integer.parseInt(record.getSeverity()));
            }
        }

        return symptomsList;
    }

    /**
     * Convert the date of previous days
     * @param targetDate
     * @param timestamp
     * @return
     */
    private double getDateTime(String targetDate, String timestamp) {
        double day = 0.0;
        LocalDate currentDate = LocalDate.now();
        for (int i = graphSpan; i >= 0; i--) {
            if (formatDateString(currentDate.minusDays(i)).equals(targetDate)) {
                day = graphSpan - i;
                break;
            }
        }

        try {
            String[] parts = timestamp.split(":");
            int hour = Integer.parseInt(parts[0].trim());
            int minute = Integer.parseInt(parts[1].trim());
            day += ((hour + minute / 60.0) / 24.0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return day;
    }

    private String formatDateString(LocalDate date) {
        String month = date.getMonth().toString();
        String day = String.valueOf(date.getDayOfMonth());
        String year = String.valueOf(date.getYear());
        return month + " " + day + ", " + year;
    }


}
