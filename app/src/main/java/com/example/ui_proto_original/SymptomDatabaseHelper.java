package com.example.ui_proto_original;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SymptomDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "symptoms.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_SYMPTOMS = "symptoms_table";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_NAME = "symptom_name";
    private static final String COLUMN_SEVERITY = "severity";
    private static final String COLUMN_TIME = "time";

    public SymptomDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String createTableQuery = "CREATE TABLE " + TABLE_SYMPTOMS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " TEXT, " + COLUMN_NAME + " TEXT, " + COLUMN_SEVERITY + " TEXT, " + COLUMN_TIME + " TEXT)";
        database.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_SYMPTOMS);
        onCreate(database);
    }

    public void insertSymptom(String date, String symptom, String severity, String time) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_NAME, symptom);
        values.put(COLUMN_SEVERITY, severity);
        values.put(COLUMN_TIME, time);

        database.insert(TABLE_SYMPTOMS, null, values);
        database.close();
    }

    /**
     * Get a list of symptoms separated by date
     * @return
     */
    public List<SymptomDay> getGroupedSymptomDays() {
        Map<String, ArrayList<SymptomRecord>> groupedData = new LinkedHashMap<>();
        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_SYMPTOMS + " ORDER BY " + COLUMN_ID + " DESC";
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                String severity = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SEVERITY));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME));

                groupedData.putIfAbsent(date, new ArrayList<>());
                groupedData.get(date).add(new SymptomRecord(name, severity, time));
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        List<SymptomDay> resultList = new ArrayList<>();
        for (Map.Entry<String, ArrayList<SymptomRecord>> entry : groupedData.entrySet()) {
            resultList.add(new SymptomDay(entry.getKey(), entry.getValue()));
        }

        return resultList;
    }

    /**
     * Get the symptoms for a specific date
     * @param date
     * @return
     */
    public SymptomDay getSymptomsForDate(String date) {
        //Map<String, String> symptomsList = new HashMap<>();
        ArrayList<SymptomRecord> symptomsList = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_SYMPTOMS + " WHERE " + COLUMN_DATE + " = ?";
        Cursor cursor = database.rawQuery(selectQuery, new String[]{date});

        if (cursor.moveToFirst()) {
            do {
                String symptom = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                String severity = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SEVERITY));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME));
                symptomsList.add(new SymptomRecord(symptom,severity,timestamp));
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        // If no symptoms found for date
        if (symptomsList.isEmpty()) {
            return null;
        }

        return new SymptomDay(date, symptomsList);
    }

    /**
     * Get a list of symptoms from the specified past number of days
     * @param numberOfDays
     * @return
     */
    public ArrayList<String> getSymptomsForPastNDays(int numberOfDays) {
        ArrayList<String> recentSymptoms = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 0; i < numberOfDays; i++) {
            LocalDate date = today.minusDays(i);
            String formattedDate = formatDateString(date);
            SymptomDay dayData = getSymptomsForDate(formattedDate);

            // If there were symptoms for that day, check and add to list
            if (dayData != null) {
                //Map<String, String> symptoms = dayData.getSymptoms();
                ArrayList<String> symptoms = dayData.getSimplifiedSymptomList();
                for (String symptom : symptoms) {
                    if (!recentSymptoms.contains(symptom)) {
                        recentSymptoms.add(symptom);
                    }
                }
            }
        }

        return recentSymptoms;
    }



    /**
     * Helper to format LocalDate into "MONTH D, YYYY" (e.g. "AUGUST 14, 2026")
     * @param date
     * @return reformatted date
     */
    private String formatDateString(LocalDate date) {
        String month = date.getMonth().toString();
        String day = String.valueOf(date.getDayOfMonth());
        String year = String.valueOf(date.getYear());
        return month + " " + day + ", " + year;
    }

}
