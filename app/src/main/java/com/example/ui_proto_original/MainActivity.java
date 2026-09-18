package com.example.ui_proto_original;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private LinearLayout symptomsContainer;

    private Button history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        symptomsContainer = findViewById(R.id.activeSymptomsList);
        symptomsContainer.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SymptomsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        history = findViewById(R.id.historyButton);
        history.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SymptomsHistoryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null && intent.hasExtra("SYMPTOMS_DATA")) {
            @SuppressWarnings("unchecked")
            HashMap<String, String> activeSymptoms = (HashMap<String, String>) intent.getSerializableExtra("SYMPTOMS_DATA");

            if (activeSymptoms != null && activeSymptoms.isEmpty()){
                clearSymptoms();
            }
            else if (activeSymptoms != null) {
                addSymptoms(activeSymptoms);
            }
        }
        loadTodaySymptoms();
        if (symptomsContainer.getChildCount() == 0){
            addDefaultSymptomsText();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        clearSymptoms();
        loadTodaySymptoms();
        if (symptomsContainer.getChildCount() == 0){
            addDefaultSymptomsText();
        }
    }

    /**
     * Puts a default text in symptoms list if there are no active symptoms
     */
    private void addDefaultSymptomsText(){
        TextView text = new TextView(this);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        text.setLayoutParams(nameParams);
        text.setText(R.string.default_string);
        text.setTextColor(getResources().getColor(R.color.black, null));
        text.setTextSize(16);
        text.setTypeface(null, Typeface.ITALIC);
        text.setPadding(48,0,0,0);
        symptomsContainer.addView(text);
    }

    /**
     * Adds active symptoms to the list
     * @param activeSymptoms
     */
    protected void addSymptoms(Map<String, String> activeSymptoms){
        symptomsContainer.removeAllViews();
        /**
        for (Map.Entry<String, String> entry : activeSymptoms.entrySet()) {
            if (entry.getValue().equals("0")) {
                activeSymptoms.remove(entry.getKey(), entry.getValue());
            }
        } **/

        for (Map.Entry<String, String> entry : activeSymptoms.entrySet()) {
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            TextView symptom = new TextView(this);
            LinearLayout.LayoutParams symptomParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2.0f);
            symptom.setLayoutParams(symptomParams);
            symptom.setText(reformatSymptom(entry.getKey()));
            symptom.setTextSize(18);
            symptom.setTextColor(getResources().getColor(R.color.black, null));
            symptom.setPadding(48,0,0,0);

            TextView severity = new TextView(this);
            LinearLayout.LayoutParams severityParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            severity.setLayoutParams(severityParams);
            severity.setText(entry.getValue());
            severity.setTextSize(18);
            severity.setGravity(Gravity.CENTER);
            severity.setTextColor(getResources().getColor(R.color.black, null));

            rowLayout.addView(symptom);
            rowLayout.addView(severity);
            symptomsContainer.addView(rowLayout);
        }
    }

    /**
     * Clears symptomsContainer
     */
    protected void clearSymptoms(){
        symptomsContainer.removeAllViews();
        addDefaultSymptomsText();
    }

    /**
     * Load today's symptoms from database
     */
    private void loadTodaySymptoms() {
        SymptomDatabaseHelper dbHelper = new SymptomDatabaseHelper(this);

        SymptomDay symptomDay = dbHelper.getSymptomsForDate(getDate());

        symptomsContainer.removeAllViews();

        if (symptomDay != null) {
            addSymptoms(symptomDay.getRecentSymptoms());
        }
    }

    /**
     * Get today's date
     * @return date: as Month D, YYYY
     */
    private String getDate(){
        LocalDate date = LocalDate.now();
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