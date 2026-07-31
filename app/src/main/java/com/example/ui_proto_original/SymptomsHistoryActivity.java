package com.example.ui_proto_original;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class SymptomsHistoryActivity extends AppCompatActivity {

    private TextView calendarMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.symptoms_history);

        calendarMonth = findViewById(R.id.dateText);

    }

    private void setCalendarMonth(String date){
        calendarMonth.setText(date);
    }

    private void addSymptomDay(String date, HashMap<String, String> symptoms){
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        container.setTag(date + "Container");

        TextView dateText = new TextView(this);
        dateText.setText(date);
        dateText.setTextSize(20);
        container.addView(dateText);

        for (Map.Entry<String, String> entry : symptoms.entrySet()) {
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            TextView symptom = new TextView(this);
            symptom.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2.0f));
            symptom.setText(entry.getKey());
            symptom.setTextSize(16);
            symptom.setTextColor(Color.BLACK);
            symptom.setPadding(16,0,0,0);

            TextView severity = new TextView(this);
            severity.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
            severity.setGravity(Gravity.CENTER);
            severity.setText(entry.getValue());
            severity.setTextSize(16);
            severity.setTextColor(Color.BLACK);

            rowLayout.addView(symptom);
            rowLayout.addView(severity);
            container.addView(rowLayout);
        }

    }

}
