package com.example.ui_proto_original;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymptomsGraphActivity extends AppCompatActivity {

    private Spinner timeSpinner;
    private LinearLayout symptomsKey;
    private LineChart lineChart;
    SymptomDatabaseHelper dbHelper;

    private List<String> options;
    private HashMap<String, Integer> dayConverter = new HashMap<>(Map.of("Past 7 days", 7, "Past 2 weeks", 14, "Past Month", 30));
    private int graphSpan = 7;
    private HashMap<String, Integer> keyColors = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.symptoms_graph);

        timeSpinner = findViewById(R.id.timeDropdown);
        lineChart = findViewById(R.id.symptomsChart);
        symptomsKey = findViewById(R.id.symptomsList);

        initializeTimeDropdown();
        initializeColorKey();
        loadChart();
    }


    private void initializeTimeDropdown(){
        options = new ArrayList<>();
        //options.add("- Select -");
        options.add("Past 7 days");
        options.add("Past 2 weeks");
        options.add("Past Month");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(adapter);
        //Default graph span
        graphSpan = 7;

        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                Toast.makeText(SymptomsGraphActivity.this, "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
                symptomsKey.removeAllViews();
                graphSpan = dayConverter.get(selectedItem);
                addSymptomsToKey(dayConverter.get(options.get(position)));
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
        keyColors.put("chills", R.color.orange);
        keyColors.put("muscleAches", R.color.purple);
        keyColors.put("soreThroat", Color.CYAN);
        keyColors.put("lossOfTaste", R.color.magenta);
        keyColors.put("lossOfSmell", R.color.pink);
        keyColors.put("headache", R.color.teal);
        keyColors.put("rash", R.color.lavendar);
        keyColors.put("nausea", R.color.brown);
        keyColors.put("vomiting", R.color.maroon);
        keyColors.put("diarrhea", R.color.beige);
        keyColors.put("confusion", R.color.navy);
        keyColors.put("dizzy", R.color.dark_grey);
    }

    private void loadChart(){
        dbHelper = new SymptomDatabaseHelper(this);
        ArrayList<SymptomDay> symptomData = dbHelper.getSymptomDataForNDays(graphSpan);

        List<ILineDataSet> dataSets = new ArrayList<>();

        //split data to be sorted by symptom
        HashMap<String, HashMap<Double, Integer>> symptomList = splitBySymptom(symptomData);
        //add symptom lines by symptom
        for (Map.Entry<String, HashMap<Double, Integer>> symptomSet: symptomList.entrySet()) {
            ArrayList<Entry> symptomLineEntries = new ArrayList<>();
            for (Map.Entry<Double, Integer> entry : symptomSet.getValue().entrySet()) {
                symptomLineEntries.add(new Entry(entry.getKey().floatValue(), entry.getValue().floatValue()));
            }

            symptomLineEntries.sort((e1, e2) -> Float.compare(e1.getX(), e2.getX()));
            LineDataSet dataSet = new LineDataSet(symptomLineEntries, symptomSet.getKey());
            int color = keyColors.get(symptomSet.getKey());
            dataSet.setColor(color);
            dataSet.setCircleColor(color);
            dataSet.setCircleSize(5f) ;
            dataSet.setLineWidth(2f);

            dataSets.add(dataSet);
        }

        lineChart.setData(new LineData(dataSets));

        //X-axis configuration
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setDrawGridLines(false);

        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setAxisMinimum(0f);
        lineChart.getAxisLeft().setAxisMaximum(10f);

        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.animateX(1000);
        lineChart.invalidate();
    }

    private HashMap<String, HashMap<Double, Integer>> splitBySymptom(ArrayList<SymptomDay> symptomData) {
        HashMap<String, HashMap<Double, Integer>> symptomsList = new HashMap<>();

        for (SymptomDay dayData : symptomData) {
            for (SymptomRecord record : dayData.getFullSymptomList()) {
                // CRITICAL FIX: Safe lookup prevents NullPointerException
                symptomsList.computeIfAbsent(record.getSymptom(), k -> new HashMap<>())
                        .put(getDateTime(dayData.getDate(), record.getTime()), Integer.parseInt(record.getSeverity()));
            }
        }

        return symptomsList;
    }

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
        for(String symptom : symptoms) {
            LinearLayout row = addSymptom(symptom, keyColors.get(symptom));
            symptomsKey.addView(row);
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
        symptom.setText(symptomName);
        symptom.setTextSize(16);
        //symptom.setTextColor(R.color.black);

        rowContainer.addView(circle);
        rowContainer.addView(symptom);
        return rowContainer;
    }


    private String formatDateString(LocalDate date) {
        String month = date.getMonth().toString();
        String day = String.valueOf(date.getDayOfMonth());
        String year = String.valueOf(date.getYear());
        return month + " " + day + ", " + year;
    }
}
