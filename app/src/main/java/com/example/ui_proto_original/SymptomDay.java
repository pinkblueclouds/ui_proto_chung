package com.example.ui_proto_original;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SymptomDay {
    private String date;
    //private Map<String, String> symptoms;
    private ArrayList<SymptomRecord> symptoms;

    public SymptomDay(String date, ArrayList<SymptomRecord> symptoms){
        this.date = date;
        this.symptoms = symptoms;
    }

    public String getDate() {
        return date;
    }

    /*
    public Map<String,String> getSymptoms(){
        return symptoms;
    }
     */

    /**
     * Get a full list of symptoms with severity and timestamps
     * @return symptoms
     */
    public ArrayList<SymptomRecord> getFullSymptomList() {
        return symptoms;
    }

    /**
     * Get a simplified list with symptoms listed only once
     * @return symptoms
     */
    public ArrayList<String> getSimplifiedSymptomList() {
        ArrayList<String> simplified = new ArrayList<>();

        for (SymptomRecord record : symptoms) {
            if (!simplified.contains(record.getSymptom())) {
                simplified.add(record.getSymptom());
            }
        }

        return simplified;
    }

    public HashMap<String, String> getRecentSymptoms() {
        HashMap<String, String> symptomList = new HashMap<>();
        for (SymptomRecord record : symptoms) {
            symptomList.put(record.getSymptom(), record.getSeverity());
        }
        return symptomList;
    }
}
