package com.example.ui_proto_original;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class HistoryTableActivity extends AppCompatActivity {

    private ArrayList<String> symptoms;

    private RecyclerView historyTable;

    private final int NUM_OF_PAST_DAYS = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        historyTable = findViewById(R.id.historyTable);
    }

    private void loadSymptomsFromDatabase() {
        SymptomDatabaseHelper dbHelper = new SymptomDatabaseHelper(this);

        symptoms = dbHelper.getSymptomsForPastNDays(NUM_OF_PAST_DAYS);

        LinearLayout symptomsContainer = new LinearLayout(this);
        symptomsContainer.setOrientation(LinearLayout.VERTICAL);
        symptomsContainer.setLayoutParams(new LinearLayout.LayoutParams(100, LinearLayout.LayoutParams.WRAP_CONTENT));

        for (String symptom : symptoms) {
            TextView row = new TextView(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
            row.setText(symptom);
            row.setTextColor(getColor(R.color.black));
            row.setTextSize(16);
            symptomsContainer.addView(row);
        }

    }

    private LinearLayout createDayColumn(HashMap<String,String> symptomsList) {
        LinearLayout column = new LinearLayout(this);
        column.setOrientation(LinearLayout.VERTICAL);
        column.setLayoutParams(new LinearLayout.LayoutParams(100, LinearLayout.LayoutParams.WRAP_CONTENT));

        for (String symptom : symptoms) {
            if (symptomsList.containsKey(symptom)) {
                TextView row = new TextView(this);
                row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                row.setText(symptomsList.get(symptom));
                row.setTextColor(getColor(R.color.black));
                row.setTextSize(14);
                column.addView(row);
            }
            else {
                TextView row = new TextView(this);
                row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                row.setTextColor(getColor(R.color.black));
                row.setTextSize(14);
                column.addView(row);
            }
        }

        return column;
    }
}
