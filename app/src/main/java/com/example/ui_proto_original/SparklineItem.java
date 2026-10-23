package com.example.ui_proto_original;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;


public class SparklineItem {

    private final String symptomKey;
    private final String displayName;
    private final ArrayList<Entry> entries;
    private final int color;

    public SparklineItem(String symptomKey, String displayName, ArrayList<Entry> entries, int color) {
        this.symptomKey = symptomKey;
        this.displayName = displayName;
        this.entries = entries;
        this.color = color;
    }

    public String getSymptomKey() { return symptomKey; }
    public String getDisplayName() { return displayName; }
    public ArrayList<Entry> getEntries() { return entries; }
    public int getColor() { return color; }

}
