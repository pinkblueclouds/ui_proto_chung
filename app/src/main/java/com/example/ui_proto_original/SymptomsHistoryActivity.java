package com.example.ui_proto_original;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymptomsHistoryActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private Calendar currentDisplayCalendar;
    private RecyclerView symptomsRecyclerView;
    private SymptomsAdapter symptomsAdapter;
    private final ArrayList<SymptomDay> symptomDaysList = new ArrayList<>();
    private Button graphButton;

    private final String[] monthsArray = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.symptoms_history);


        calendarView = findViewById(R.id.calendarView);
        calendarView.setMaxDate(System.currentTimeMillis());
        calendarView.post(() -> {
            View headerView = calendarView.findViewById(getResources().getIdentifier("month_view", "id", "android"));
            if (headerView instanceof TextView) {
                TextView monthTitle = (TextView) headerView;
                monthTitle.setTextSize(32);
                monthTitle.setTypeface(null, Typeface.BOLD);
                monthTitle.setTextColor(Color.BLACK);
            }
        });

        // Initialize RecyclerView
        symptomsRecyclerView = findViewById(R.id.symptomsRecyclerView);
        symptomsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        symptomsAdapter = new SymptomsAdapter(symptomDaysList);
        symptomsRecyclerView.setAdapter(symptomsAdapter);

        currentDisplayCalendar = Calendar.getInstance();
        currentDisplayCalendar.setTimeInMillis(calendarView.getDate());

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            currentDisplayCalendar.set(Calendar.YEAR, year);
            currentDisplayCalendar.set(Calendar.MONTH, month);
            currentDisplayCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        });

        graphButton = findViewById(R.id.symptomsGraph);
        graphButton.setOnClickListener(view -> {
            Intent intent = new Intent(SymptomsHistoryActivity.this, SymptomsGraphActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSymptomsFromDatabase();
    }

    /**
     * Load Symptoms from database into view
     */
    private void loadSymptomsFromDatabase() {
        SymptomDatabaseHelper databaseHelper = new SymptomDatabaseHelper(this);
        symptomDaysList.clear();
        List<SymptomDay> data = databaseHelper.getGroupedSymptomDays();

        if (data != null) {
            symptomDaysList.addAll(data);
        }

        if (symptomsAdapter != null) {
            symptomsAdapter.notifyDataSetChanged();;
        }
    }


    /**
     * Smoothly scrolls the RecyclerView to the position matching the selected calendar date
     */
    private void scrollToDate() {
        if (symptomDaysList.isEmpty()) {
            return;
        }

        String month = monthsArray[currentDisplayCalendar.get(Calendar.MONTH)];
        String targetDate = month + " " + currentDisplayCalendar.get(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < symptomDaysList.size(); i++) {
            if (symptomDaysList.get(i).getDate().equalsIgnoreCase(targetDate)) {
                symptomsRecyclerView.smoothScrollToPosition(i);
                break;
            }
        }
    }

}
