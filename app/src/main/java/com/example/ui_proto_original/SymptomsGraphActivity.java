package com.example.ui_proto_original;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Paint;
import androidx.core.content.ContextCompat;

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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymptomsGraphActivity extends AppCompatActivity {

    private Spinner timeSpinner;
    private LinearLayout symptomsKeyCol1;
    private LinearLayout symptomsKeyCol2;
    private LineChart lineChart;
    private Button clearSelection;
    SymptomDatabaseHelper dbHelper;

    private List<String> options;
    private HashMap<String, Integer> dayConverter = new HashMap<>(Map.of("Past day", 1, "Past 3 days", 3, "Past 7 days", 7, "Past 2 weeks", 14, "Past Month", 30));
    private int graphSpan = 7;
    private HashMap<String, Integer> keyColors = new HashMap<>();

    //What symptoms are on display in the graph
    private ArrayList<String> symptomsForDisplay = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.symptoms_graph);

        timeSpinner = findViewById(R.id.timeDropdown);
        lineChart = findViewById(R.id.symptomsChart);
        symptomsKeyCol1 = findViewById(R.id.symptomsList1);
        symptomsKeyCol2 = findViewById(R.id.symptomsList2);

        clearSelection = findViewById(R.id.clearSelectionButton);
        clearSelection.setOnClickListener(v -> {
            clearSelection();
            if (!symptomsForDisplay.isEmpty()) {
                clearSelection.setVisibility(View.VISIBLE);
            }
            else {
                clearSelection.setVisibility(View.GONE);
            }
        });

        initializeTimeDropdown();
        initializeColorKey();
        loadChart();
    }


    private void initializeTimeDropdown(){
        options = new ArrayList<>();
        //options.add("- Select -");
        options.add("Past day");
        options.add("Past 3 days");
        options.add("Past 7 days");
        options.add("Past 2 weeks");
        options.add("Past Month");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(adapter);
        //Default graph span
        timeSpinner.setSelection(2);
        graphSpan = 7;

        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                Toast.makeText(SymptomsGraphActivity.this, "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
                symptomsKeyCol1.removeAllViews();
                symptomsKeyCol2.removeAllViews();
                graphSpan = dayConverter.get(selectedItem);
                addSymptomsToKey(graphSpan);
                loadChart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

    }

    private void initializeColorKey(){
        keyColors.put("chestPain", Color.RED);
        keyColors.put("breath", Color.GREEN);
        keyColors.put("bleeding", Color.BLUE);
        keyColors.put("fever", Color.YELLOW);
        keyColors.put("chills", ContextCompat.getColor(this, R.color.orange));
        keyColors.put("muscleAches", ContextCompat.getColor(this, R.color.purple));
        keyColors.put("soreThroat", Color.CYAN);
        keyColors.put("lossOfTaste", ContextCompat.getColor(this, R.color.magenta));
        keyColors.put("lossOfSmell", ContextCompat.getColor(this, R.color.pink));
        keyColors.put("headache", ContextCompat.getColor(this, R.color.teal));
        keyColors.put("rash", ContextCompat.getColor(this, R.color.lavendar));
        keyColors.put("nausea", ContextCompat.getColor(this, R.color.brown));
        keyColors.put("vomiting", ContextCompat.getColor(this, R.color.maroon));
        keyColors.put("diarrhea", ContextCompat.getColor(this, R.color.beige));
        keyColors.put("confusion", ContextCompat.getColor(this, R.color.navy));
        keyColors.put("dizzy", ContextCompat.getColor(this, R.color.dark_grey));
    }

    /**
     * Load the chart based on symptom data for the allotted time
     */
    private void loadChart(){
        dbHelper = new SymptomDatabaseHelper(this);
        ArrayList<SymptomDay> symptomData = dbHelper.getSymptomDataForNDays(graphSpan);

        List<ILineDataSet> dataSets = new ArrayList<>();

        //split data to be sorted by symptom
        HashMap<String, HashMap<Double, Integer>> symptomList = splitBySymptom(symptomData);
        HashMap<String, HashMap<Double, Integer>> selectedSymptomList = new HashMap<>();
        if (symptomsForDisplay.isEmpty()) {
            selectedSymptomList = symptomList;
            clearSelection.setVisibility(View.GONE);
        }
        else {
            for (String symptom : symptomsForDisplay) {
                selectedSymptomList.put(symptom, symptomList.get(symptom));
            }
            clearSelection.setVisibility(View.VISIBLE);
        }

        for (Map.Entry<String, HashMap<Double, Integer>> symptomSet : selectedSymptomList.entrySet()) {
            String symptomName = symptomSet.getKey();

            // sort entries by symptom
            ArrayList<Entry> symptomLineEntries = new ArrayList<>();
            for (Map.Entry<Double, Integer> entry : symptomSet.getValue().entrySet()) {
                symptomLineEntries.add(new Entry(entry.getKey().floatValue(), entry.getValue().floatValue()));
            }
            symptomLineEntries.sort((e1, e2) -> Float.compare(e1.getX(), e2.getX()));

            // split data into segments
            List<List<Entry>> segments = new ArrayList<>();
            if (!symptomLineEntries.isEmpty()) {
                List<Entry> currentSegment = new ArrayList<>();
                currentSegment.add(symptomLineEntries.get(0));

                for (int i = 1; i < symptomLineEntries.size(); i++) {
                    Entry prev = symptomLineEntries.get(i - 1);
                    Entry curr = symptomLineEntries.get(i);

                    // Gap check: if greater than 1.0f, start a new segment
                    if ((curr.getX() - prev.getX()) >= 1.0f) {
                        segments.add(currentSegment);
                        currentSegment = new ArrayList<>();
                    }
                    currentSegment.add(curr);
                }
                segments.add(currentSegment);
            }

            int color = keyColors.get(symptomName);

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
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setValueFormatter(new ValueFormatter() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M-d");

            @Override
            public String getFormattedValue(float value) {
                int dayIndex = Math.round(value);

                if (dayIndex >= graphSpan) { return "Today"; }

                int daysAgo = graphSpan - dayIndex;
                if (daysAgo < 0) return "";

                LocalDate targetDate = LocalDate.now().minusDays(daysAgo);
                return targetDate.format(formatter);
            }
        });

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

    /**
     * Helper function to split data by symptom
     * @param symptomData
     * @return
     */
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

    /**
     * Takes the symptoms from the specified time period shown in the graph and displays them in the key
     * @param numberOfDays
     */
    private void addSymptomsToKey(int numberOfDays) {
        ArrayList<String> symptoms = dbHelper.getSymptomsForPastNDays(numberOfDays);
        int count = 0;
        for(String symptom : symptoms) {
            LinearLayout row = addSymptom(symptom, keyColors.get(symptom));
            if (count % 2 == 0) { symptomsKeyCol1.addView(row); }
            else { symptomsKeyCol2.addView(row); }
            count++;
        }
    }

    /**
     * Adds a symptom to the Symptom Key
     * @param symptomName
     * @param color
     * @return
     */
    private LinearLayout addSymptom(String symptomName, int color) {
        LinearLayout rowContainer = new LinearLayout(this);
        rowContainer.setOrientation(LinearLayout.HORIZONTAL);
        rowContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        // Color dot
        View circle = new View(this);
        circle.setBackgroundResource(R.drawable.small_circle);
        int sizeInDp = 16;
        int sizeInPx = (int) (sizeInDp * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(sizeInPx, sizeInPx);
        layoutParams.setMargins(0, 0, (int) (8 * getResources().getDisplayMetrics().density), 0);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        circle.setLayoutParams(layoutParams);
        circle.setBackgroundColor(color);

        // Symptom
        TextView symptom = new TextView(this);
        symptom.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 8.0f));
        symptom.setText(reformatSymptom(symptomName));
        symptom.setTextSize(16);

        boolean selected = symptomsForDisplay.contains(symptomName);
        if (selected) {
            symptom.setTypeface(null, Typeface.BOLD);
        }
        else {
            symptom.setTypeface(null, Typeface.NORMAL);
        }

        rowContainer.setOnClickListener(v -> {
            if (symptomsForDisplay.contains(symptomName)) {
                symptomsForDisplay.remove(symptomName);
            }
            else {
                symptomsForDisplay.add(symptomName);
            }

            // Refresh key to update bold states
            clearSymptomsKey();
            addSymptomsToKey(graphSpan);

            // Reload chart with updated multi-selection filter
            loadChart();
        });

        rowContainer.addView(circle);
        rowContainer.addView(symptom);
        return rowContainer;
    }

    private void clearSelection() {
        symptomsForDisplay.clear();
        clearSymptomsKey();
        addSymptomsToKey(graphSpan);
        loadChart();
    }

    private void clearSymptomsKey() {
        symptomsKeyCol1.removeAllViews();
        symptomsKeyCol2.removeAllViews();
    }

    private double getCurrentTime(){
        LocalDateTime time = LocalDateTime.now();
        int hour = time.getHour();
        int minute = time.getMinute();
        return ((hour + minute / 60.0) / 24.0);
    }


    private String formatDateString(LocalDate date) {
        String month = date.getMonth().toString();
        String day = String.valueOf(date.getDayOfMonth());
        String year = String.valueOf(date.getYear());
        return month + " " + day + ", " + year;
    }

    private String reformatSymptom(String symptom){
        String result = symptom.replaceAll("(?<!^)(?=[A-Z])", " ");
        return Character.toUpperCase(result.charAt(0)) + result.substring(1);
    }
}
