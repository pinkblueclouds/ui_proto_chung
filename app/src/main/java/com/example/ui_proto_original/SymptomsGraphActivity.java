package com.example.ui_proto_original;

import android.app.AlertDialog;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Paint;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymptomsGraphActivity extends AppCompatActivity {

    private Spinner timeDropdown;
    private Spinner graphTypeDropdown;

    private List<String> options;
    private HashMap<String, Integer> dayConverter = new HashMap<>(Map.of("Past day", 1, "Past 3 days", 3, "Past 7 days", 7, "Past 2 weeks", 14, "Past Month", 30));
    private int graphSpan = 7;
    private int currentGraphTypePosition = 0; // 0 = Line Graph, 1 = Sparklines


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.symptoms_graph);

        timeDropdown = findViewById(R.id.timeDropdown);
        graphTypeDropdown = findViewById(R.id.graphTypeDropdown);

        setupTimeDropdown();
        setupGraphTypeDropdown();

        if (savedInstanceState == null) {
            switchFragment(new LineGraphFragment());
        }

    }


    private void setupTimeDropdown(){
        options = new ArrayList<>();
        //options.add("- Select -");
        options.add("Past day");
        options.add("Past 3 days");
        options.add("Past 7 days");
        options.add("Past 2 weeks");
        options.add("Past Month");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeDropdown.setAdapter(adapter);
        //Default graph span
        timeDropdown.setSelection(2);
        graphSpan = 7;

        timeDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                Toast.makeText(SymptomsGraphActivity.this, "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
                graphSpan = dayConverter.get(selectedItem);

                //Pass new timeframe to the current graph Fragment
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.graphContainer);
                if (currentFragment instanceof LineGraphFragment) {
                    ((LineGraphFragment) currentFragment).updateGraphSpan(graphSpan);
                } else if (currentFragment instanceof SparklineGraphFragment) {
                    ((SparklineGraphFragment) currentFragment).updateGraphSpan(graphSpan);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

    }

    public void setupGraphTypeDropdown() {
        String[] graphTypes = new String[]{"Line Graph", "Sparklines"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, graphTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        graphTypeDropdown.setAdapter(adapter);
        graphTypeDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == currentGraphTypePosition) return; // Prevent initial double-trigger

                currentGraphTypePosition = position;

                if (position == 0) {
                    switchFragment(new LineGraphFragment());
                } else if (position == 1) {
                    switchFragment(new SparklineGraphFragment());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void switchFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.graphContainer, fragment).commit();
    }

    /*
    private void setUpDateSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Symptoms:");

        LinearLayout layout = new LinearLayout(this);
        layout.setLayoutParams(new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 40, 40, 40);

        LinearLayout layout = new LinearLayout(this);


        builder.setView(layout);

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            // update graph
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    } */

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
