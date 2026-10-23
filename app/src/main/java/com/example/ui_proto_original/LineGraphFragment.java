package com.example.ui_proto_original;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.renderer.YAxisRenderer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LineGraphFragment extends Fragment {

    private LineChart lineChart;
    private Button clearSelectionButton;
    private LinearLayout symptomsKeyCol1;
    private LinearLayout symptomsKeyCol2;

    private SymptomDatabaseHelper dbHelper;
    private int graphSpan = 7; //Default span
    private HashMap<String, Integer> keyColors = new HashMap<>();
    private ArrayList<String> selectedSymptoms = new ArrayList<>();

    public LineGraphFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_line_graph, container, false);

        lineChart = view.findViewById(R.id.lineChart);
        clearSelectionButton = view.findViewById(R.id.clearSelectionButton);
        symptomsKeyCol1 = view.findViewById(R.id.symptomsList1);
        symptomsKeyCol2 = view.findViewById(R.id.symptomsList2);

        dbHelper = new SymptomDatabaseHelper(requireContext());

        clearSelectionButton.setOnClickListener(v -> {
            selectedSymptoms.clear();
            updateClearButtonVisibility();
            loadSymptomsKey();
            loadChart();
        });

        initializeColorKey();
        loadSymptomsKey();
        loadChart();
        updateClearButtonVisibility();

        return view;
    }

    /**
     * Used to by SymptomsGraphActivity to update graph span
     * @param newSpan
     */
    public void updateGraphSpan(int newSpan) {
        this.graphSpan = newSpan;
        loadSymptomsKey();
        loadChart();
    }

    /**
     * Load the chart with data
     */
    public void loadChart() {
        if (lineChart == null) return;

        ArrayList<SymptomDay> symptomData = dbHelper.getSymptomDataForNDays(graphSpan);
        HashMap<String, HashMap<Double, Integer>> symptomList = splitBySymptom(symptomData);

        List<ILineDataSet> dataSets = new ArrayList<>();

        for (Map.Entry<String, HashMap<Double, Integer>> symptomSet : symptomList.entrySet()) {
            String symptomName = symptomSet.getKey();

            // Multi-symptom filter check
            if (!selectedSymptoms.isEmpty() && !selectedSymptoms.contains(symptomName)) {
                continue;
            }

            ArrayList<Entry> symptomLineEntries = new ArrayList<>();
            for (Map.Entry<Double, Integer> entry : symptomSet.getValue().entrySet()) {
                symptomLineEntries.add(new Entry(entry.getKey().floatValue(), entry.getValue().floatValue()));
            }
            symptomLineEntries.sort((e1, e2) -> Float.compare(e1.getX(), e2.getX()));

            // Segment gaps >= 1.0f
            List<List<Entry>> segments = new ArrayList<>();
            if (!symptomLineEntries.isEmpty()) {
                List<Entry> currentSegment = new ArrayList<>();
                currentSegment.add(symptomLineEntries.get(0));

                for (int i = 1; i < symptomLineEntries.size(); i++) {
                    Entry prev = symptomLineEntries.get(i - 1);
                    Entry curr = symptomLineEntries.get(i);

                    if ((curr.getX() - prev.getX()) >= 1.0f) {
                        segments.add(currentSegment);
                        currentSegment = new ArrayList<>();
                    }
                    currentSegment.add(curr);
                }
                segments.add(currentSegment);
            }

            Integer rawColor = keyColors.get(symptomName);
            int color = (rawColor != null) ? rawColor : Color.RED;

            for (List<Entry> segment : segments) {
                LineDataSet dataSet = new LineDataSet(segment, symptomName);
                dataSet.setColor(color);
                dataSet.setCircleColor(color);
                dataSet.setCircleHoleColor(color);
                dataSet.setCircleSize(5f);
                dataSet.setLineWidth(2f);
                dataSet.setDrawValues(false);
                dataSets.add(dataSet);
            }
        }

        lineChart.setData(new LineData(dataSets));

        // X-axis configuration
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum((float)(graphSpan + getCurrentTime()));
        xAxis.setLabelCount(graphSpan + 1, false);

        // X-axis labels
        if (graphSpan <= 7) {
            xAxis.setGranularity(1f);
        }
        else {
            xAxis.setGranularity(2f);
        }
        xAxis.setGranularityEnabled(true);
        xAxis.setValueFormatter(new ValueFormatter() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M-d");

            @Override
            public String getFormattedValue(float value) {
                int dayIndex = Math.round(value);

                if (dayIndex >= graphSpan) { return "Today"; }

                int daysAgo = graphSpan - dayIndex;
                if (daysAgo < 0) return "";

                if (graphSpan > 7 && daysAgo % 2 != 0) {
                    return ""; // Skip label for odd days
                }

                LocalDate targetDate = LocalDate.now().minusDays(daysAgo);
                return targetDate.format(formatter);
            }
        });

        lineChart.setAutoScaleMinMaxEnabled(false);

        // Y-axis configuration
        lineChart.getAxisRight().setEnabled(false);
        YAxis yAxis = lineChart.getAxisLeft();
        lineChart.getAxisLeft().setAxisMinimum(0f);
        lineChart.getAxisLeft().setAxisMaximum(10f);

        lineChart.setRendererLeftYAxis(new YAxisRenderer(lineChart.getViewPortHandler(), yAxis, lineChart.getTransformer(YAxis.AxisDependency.LEFT)) {
            private final Paint zonePaint = new Paint();

            @Override
            public void renderGridLines(Canvas c) {
                // Initialize paint once
                zonePaint.setStyle(Paint.Style.FILL);
                zonePaint.setColor(Color.argb(38, 255, 0, 0)); // Light red with 15% opacity

                // Convert target Y values (7 and 10) into absolute screen pixels
                float[] pts = new float[4];
                pts[1] = 7f;
                pts[3] = 10f;
                mTrans.pointValuesToPixel(pts);

                // Draw the precise background rectangle (pts[1] is y=10 top, pts[3] is y=7 bottom)
                c.drawRect(
                        mViewPortHandler.contentLeft(),
                        pts[3],
                        mViewPortHandler.contentRight(),
                        pts[1],
                        zonePaint
                );

                // Let the chart draw its original grid lines on top
                super.renderGridLines(c);
            }
        });

        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.animateX(1000);
        lineChart.invalidate();
    }

    private void initializeColorKey(){
        keyColors.put("chestPain", Color.RED);
        keyColors.put("breath", Color.GREEN);
        keyColors.put("bleeding", Color.BLUE);
        keyColors.put("fever", Color.YELLOW);
        keyColors.put("chills", ContextCompat.getColor(requireContext(), R.color.orange));
        keyColors.put("muscleAches", ContextCompat.getColor(requireContext(), R.color.purple));
        keyColors.put("soreThroat", Color.CYAN);
        keyColors.put("lossOfTaste", ContextCompat.getColor(requireContext(), R.color.magenta));
        keyColors.put("lossOfSmell", ContextCompat.getColor(requireContext(), R.color.pink));
        keyColors.put("headache", ContextCompat.getColor(requireContext(), R.color.teal));
        keyColors.put("rash", ContextCompat.getColor(requireContext(), R.color.lavender));
        keyColors.put("nausea", ContextCompat.getColor(requireContext(), R.color.brown));
        keyColors.put("vomiting", ContextCompat.getColor(requireContext(), R.color.maroon));
        keyColors.put("diarrhea", ContextCompat.getColor(requireContext(), R.color.beige));
        keyColors.put("confusion", ContextCompat.getColor(requireContext(), R.color.navy));
        keyColors.put("dizzy", ContextCompat.getColor(requireContext(), R.color.dark_grey));
    }

    private void loadSymptomsKey() {
        symptomsKeyCol1.removeAllViews();
        symptomsKeyCol2.removeAllViews();

        ArrayList<String> symptoms = dbHelper.getSymptomsForPastNDays(graphSpan);
        for (int i = 0; i < symptoms.size(); i++) {
            String symptomKey = symptoms.get(i);
            Integer rawColor = keyColors.get(symptomKey);
            int color = (rawColor != null) ? rawColor : Color.RED;

            LinearLayout row = createSymptomRow(turnKeyIntoDisplay(symptomKey), color, symptomKey);

            // Alternate columns
            if (i % 2 == 0) {
                symptomsKeyCol1.addView(row);
            } else {
                symptomsKeyCol2.addView(row);
            }
        }
    }

    private LinearLayout createSymptomRow(String symptomName, int color, String key) {
        LinearLayout rowContainer = new LinearLayout(requireContext());
        rowContainer.setOrientation(LinearLayout.HORIZONTAL);
        rowContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        rowContainer.setPadding(0, dpToPx(4), 0, dpToPx(4));

        View circle = new View(requireContext());
        int sizeInPx = dpToPx(16);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(sizeInPx, sizeInPx);
        layoutParams.setMargins(0, 0, dpToPx(8), 0);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        circle.setLayoutParams(layoutParams);
        circle.setBackgroundColor(color);

        TextView symptom = new TextView(requireContext());
        symptom.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
        symptom.setText(symptomName);
        symptom.setTextSize(16);

        if (selectedSymptoms.contains(key)) {
            symptom.setTypeface(null, android.graphics.Typeface.BOLD);
        } else {
            symptom.setTypeface(null, android.graphics.Typeface.NORMAL);
        }

        rowContainer.setOnClickListener(v -> {
            if (selectedSymptoms.contains(key)) {
                selectedSymptoms.remove(key);
            } else {
                selectedSymptoms.add(key);
            }
            updateClearButtonVisibility();
            loadSymptomsKey();
            loadChart();
        });

        rowContainer.addView(circle);
        rowContainer.addView(symptom);
        return rowContainer;
    }

    private void updateClearButtonVisibility() {
        if (clearSelectionButton == null) return;
        clearSelectionButton.setVisibility(!selectedSymptoms.isEmpty() ? View.VISIBLE : View.GONE);
    }

    // Helper stubs matching your codebase
    private double getCurrentTime() {
        LocalDateTime time = LocalDateTime.now();
        int hour = time.getHour();
        int minute = time.getMinute();
        return ((hour + minute / 60.0) / 24.0);
    }
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

    private int dpToPx(int dp) { return (int) (dp * getResources().getDisplayMetrics().density); }


}
