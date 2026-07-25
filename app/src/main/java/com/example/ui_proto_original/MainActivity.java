package com.example.ui_proto_original;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private LinearLayout symptomsContainer;

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



    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null && intent.hasExtra("SYMPTOMS_DATA")) {
            @SuppressWarnings("unchecked")
            HashMap<String, Integer> activeSymptoms = (HashMap<String, Integer>) intent.getSerializableExtra("SYMPTOMS_DATA");

            if (activeSymptoms != null && activeSymptoms.isEmpty()){
                clearSymptoms();
            }
            else if (activeSymptoms != null) {
                addSymptoms(activeSymptoms);
            }
        }
    }

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


    protected void addSymptoms(Map<String, Integer> activeSymptoms){
        symptomsContainer.removeAllViews();
        for (Map.Entry<String, Integer> entry : activeSymptoms.entrySet()) {
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
            severity.setText(String.valueOf(entry.getValue()));
            severity.setTextSize(18);
            severity.setGravity(Gravity.CENTER);
            severity.setTextColor(getResources().getColor(R.color.black, null));

            rowLayout.addView(symptom);
            rowLayout.addView(severity);
            symptomsContainer.addView(rowLayout);
        }

    }

    protected void clearSymptoms(){
        symptomsContainer.removeAllViews();
        addDefaultSymptomsText();
    }


    private String reformatSymptom(String symptom){
        String result = symptom.replaceAll("(?<!^)(?=[A-Z])", " ");
        return Character.toUpperCase(result.charAt(0)) + result.substring(1);
    }

}