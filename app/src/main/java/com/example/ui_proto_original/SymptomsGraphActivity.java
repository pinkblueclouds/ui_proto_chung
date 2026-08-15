package com.example.ui_proto_original;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymptomsGraphActivity extends AppCompatActivity {

    private Spinner timeSpinner;
    private LinearLayout symptomsKey;

    private List<String> options;
    private HashMap<String, Integer> dayConverter = new HashMap<>(Map.of("Past 7 days", 7, "Past 2 weeks", 14, "Past Month", 30));

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.symptoms_graph);

        timeSpinner = findViewById(R.id.timeDropdown);
        initializeTimeDropdown();

        symptomsKey = findViewById(R.id.symptomsList);

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

        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                Toast.makeText(SymptomsGraphActivity.this, "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
                symptomsKey.removeAllViews();
                addSymptomsToKey(dayConverter.get(options.get(position)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

    }


    /**
     * Takes the symptoms from the specified time period shown in the graph and displays them in the key
     * @param numberOfDays
     */
    private void addSymptomsToKey(int numberOfDays) {
        SymptomDatabaseHelper dbHelper = new SymptomDatabaseHelper(this);
        ArrayList<String> symptoms = dbHelper.getSymptomsForPastNDays(numberOfDays);
        for(String symptom : symptoms) {
            LinearLayout row = addSymptom(symptom, R.color.blue);
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
        circle.setBackgroundColor(getColor(color));

        // Symptom
        TextView symptom = new TextView(this);
        symptom.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 8.0f));
        symptom.setText(symptomName);
        symptom.setTextSize(16);
        symptom.setTextColor(R.color.black);

        rowContainer.addView(circle);
        rowContainer.addView(symptom);
        return rowContainer;
    }

}
