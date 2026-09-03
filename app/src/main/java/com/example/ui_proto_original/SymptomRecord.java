package com.example.ui_proto_original;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SymptomRecord {
    private String symptom;
    private String severity;
    private String time;

    public SymptomRecord(String symptom, String severity, String time) {
        this.symptom = symptom;
        this.severity = severity;
        this.time = time;
    }

    public String getSymptom() {
        return symptom;
    }

    public String getSeverity() {
        return severity;
    }

    public String getTime() {
        return time;
    }
    /*
        int hour = time.getHour();
        int minute = time.getMinute();
        return String.valueOf(hour) + ":" + String.valueOf(minute);
     */

}
