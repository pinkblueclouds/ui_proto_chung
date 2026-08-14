package com.example.ui_proto_original;

import java.util.Map;

public class SymptomDay {
    private String date;
    private Map<String, String> symptoms;

    public SymptomDay(String date, Map<String, String> symptoms){
        this.date = date;
        this.symptoms = symptoms;
    }

    public String getDate() {
        return date;
    }

    public Map<String,String> getSymptoms(){
        return symptoms;
    }
}
