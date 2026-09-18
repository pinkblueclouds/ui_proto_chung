package com.example.ui_proto_original;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
//import java.util.concurrent.atomic.AtomicInteger;

public class SymptomsActivity extends AppCompatActivity {

    private LinearLayout activeSymptomsContainer;
    private LinearLayout customSymptomsContainer;
    private Map<String, Integer> oldSymptomValues = new HashMap<>();
    private Map<String, Integer> symptomValues = new HashMap<>();
    private Map<String, View> activeSymptomViews = new HashMap<>();
    private Map<String, LinearLayout> customSymptomContainers = new HashMap<>();
    //private AtomicInteger customSymptomCounter = new AtomicInteger(0);

    private Button addChestPainButton, addBreathButton, addBleedingButton, addFeverButton;
    private Button addChillsButton, addMuscleAchesButton, addSoreThroatButton, addLossOfTasteButton;
    private Button addLossOfSmellButton, addHeadacheButton, addRashButton, addNauseaButton;
    private Button addVomitingButton, addDiarrheaButton, addConfusionButton, addDizzyButton;

    private Button saveButton, clearButton, addNewButton;

    private LinearLayout chestPainContainer, breathContainer, bleedingContainer, feverContainer;
    private LinearLayout chillsContainer, muscleAchesContainer, soreThroatContainer, lossOfTasteContainer;
    private LinearLayout lossOfSmellContainer, headacheContainer, rashContainer, nauseaContainer;
    private LinearLayout vomitingContainer, diarrheaContainer, confusionContainer, dizzyContainer;

    private Handler autoSaveHandler;
    private Runnable autoSaveRunnable;
    private static final int AUTO_SAVE_INTERVAL = 15000; // 15 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.symptoms_menu);

        initializeViews();
        setupAddButtonListeners();
        setupControlButtons();
        initializeSymptomValues();
        setupAutoSave();
        loadSymptomsFromDatabase();

    }

    @Override
    protected void onResume() {
        super.onResume();
        startAutoSave();
        loadSymptomsFromDatabase();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAutoSave();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAutoSave();
    }

    private void initializeViews() {
        activeSymptomsContainer = findViewById(R.id.activeSymptomsContainer);
        customSymptomsContainer = findViewById(R.id.customSymptomsContainer);

        addChestPainButton = findViewById(R.id.addChestPainButton);
        addBreathButton = findViewById(R.id.addBreathButton);
        addBleedingButton = findViewById(R.id.addBleedingButton);
        addFeverButton = findViewById(R.id.addFeverButton);
        addChillsButton = findViewById(R.id.addChillsButton);
        addMuscleAchesButton = findViewById(R.id.addMuscleAchesButton);
        addSoreThroatButton = findViewById(R.id.addSoreThroatButton);
        addLossOfTasteButton = findViewById(R.id.addLossOfTasteButton);
        addLossOfSmellButton = findViewById(R.id.addLossOfSmellButton);
        addHeadacheButton = findViewById(R.id.addHeadacheButton);
        addRashButton = findViewById(R.id.addRashButton);
        addNauseaButton = findViewById(R.id.addNauseaButton);
        addVomitingButton = findViewById(R.id.addVomitingButton);
        addDiarrheaButton = findViewById(R.id.addDiarrheaButton);
        addConfusionButton = findViewById(R.id.addConfusionButton);
        addDizzyButton = findViewById(R.id.addDizzyButton);

        chestPainContainer = findViewById(R.id.chestPainContainer);
        breathContainer = findViewById(R.id.breathContainer);
        bleedingContainer = findViewById(R.id.bleedingContainer);
        feverContainer = findViewById(R.id.feverContainer);
        chillsContainer = findViewById(R.id.chillsContainer);
        muscleAchesContainer = findViewById(R.id.muscleAchesContainer);
        soreThroatContainer = findViewById(R.id.soreThroatContainer);
        lossOfTasteContainer = findViewById(R.id.lossOfTasteContainer);
        lossOfSmellContainer = findViewById(R.id.lossOfSmellContainer);
        headacheContainer = findViewById(R.id.headacheContainer);
        rashContainer = findViewById(R.id.rashContainer);
        nauseaContainer = findViewById(R.id.nauseaContainer);
        vomitingContainer = findViewById(R.id.vomitingContainer);
        diarrheaContainer = findViewById(R.id.diarrheaContainer);
        confusionContainer = findViewById(R.id.confusionContainer);
        dizzyContainer = findViewById(R.id.dizzyContainer);

        saveButton = findViewById(R.id.saveButton);
        clearButton = findViewById(R.id.clearButton);
        addNewButton = findViewById(R.id.addNewButton);
    }

    private void setupAddButtonListeners() {
        addChestPainButton.setOnClickListener(v -> addSymptom("Chest Pain", "chestPain", chestPainContainer, "0"));
        addBreathButton.setOnClickListener(v -> addSymptom("Shortness of Breath", "breath", breathContainer, "0"));
        addBleedingButton.setOnClickListener(v -> addSymptom("Bleeding", "bleeding", bleedingContainer, "0"));
        addFeverButton.setOnClickListener(v -> addSymptom("Fever", "fever", feverContainer, "0"));
        addChillsButton.setOnClickListener(v -> addSymptom("Chills", "chills", chillsContainer, "0"));
        addMuscleAchesButton.setOnClickListener(v -> addSymptom("Muscle Aches", "muscleAches", muscleAchesContainer, "0"));
        addSoreThroatButton.setOnClickListener(v -> addSymptom("Sore Throat", "soreThroat", soreThroatContainer, "0"));
        addLossOfTasteButton.setOnClickListener(v -> addSymptom("Loss of Taste", "lossOfTaste", lossOfTasteContainer, "0"));
        addLossOfSmellButton.setOnClickListener(v -> addSymptom("Loss of Smell", "lossOfSmell", lossOfSmellContainer, "0"));
        addHeadacheButton.setOnClickListener(v -> addSymptom("Headache", "headache", headacheContainer, "0"));
        addRashButton.setOnClickListener(v -> addSymptom("Rash", "rash", rashContainer, "0"));
        addNauseaButton.setOnClickListener(v -> addSymptom("Nausea", "nausea", nauseaContainer, "0"));
        addVomitingButton.setOnClickListener(v -> addSymptom("Vomiting", "vomiting", vomitingContainer, "0"));
        addDiarrheaButton.setOnClickListener(v -> addSymptom("Diarrhea", "diarrhea", diarrheaContainer, "0"));
        addConfusionButton.setOnClickListener(v -> addSymptom("Confusion", "confusion", confusionContainer, "0"));
        addDizzyButton.setOnClickListener(v -> addSymptom("Dizzy", "dizzy", dizzyContainer, "0"));
    }

    private void setupControlButtons() {
        clearButton.setOnClickListener(v -> clearData());
        saveButton.setOnClickListener(v -> saveData());
        addNewButton.setOnClickListener(v -> showCustomSymptomDialog());
    }

    private void showCustomSymptomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Custom Symptom");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dpToPx(16), dpToPx(8), dpToPx(16), dpToPx(8));

        TextView nameLabel = new TextView(this);
        nameLabel.setText("Symptom Name:");
        nameLabel.setTextSize(16);
        nameLabel.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.black));
        nameLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        layout.addView(nameLabel);

        EditText symptomNameInput = new EditText(this);
        symptomNameInput.setTextSize(16);
        symptomNameInput.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        nameParams.setMargins(0, dpToPx(4), 0, dpToPx(16));
        symptomNameInput.setLayoutParams(nameParams);
        layout.addView(symptomNameInput);

        builder.setView(layout);

        builder.setPositiveButton("Add Symptom", (dialog, which) -> {
            String symptomName = symptomNameInput.getText().toString().trim();

            if (symptomName.isEmpty()) {
                Toast.makeText(this, "Please enter a symptom name", Toast.LENGTH_SHORT).show();
                return;
            }

            addCustomSymptom(symptomName, "Severe");
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Add a custom symptom to screen
     * @param symptomName
     * @param severity
     */
    private void addCustomSymptom(String symptomName, String severity) {
        String key = turnSymptomIntoKey(symptomName);
        // Dupe check
        if (symptomValues.containsKey(key)) {
            Toast.makeText(this, "Symptom already exists in active symptoms", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add custom symptom container
        LinearLayout customContainer = createCustomSymptomContainer(symptomName, key, severity);
        customSymptomsContainer.addView(customContainer);
        customSymptomContainers.put(key, customContainer);

        symptomValues.put(key, 0);
        addSymptom(symptomName, key, customContainer, severity);

        Toast.makeText(this, "Added custom symptom: " + symptomName, Toast.LENGTH_SHORT).show();

    }

    /**
     * Setup custom symptom container
     * @param symptomName
     * @param key
     * @param maxLabel
     * @return
     */
    private LinearLayout createCustomSymptomContainer(String symptomName, String key, String maxLabel) {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        containerParams.setMargins(0, 0, 0, dpToPx(1));
        container.setLayoutParams(containerParams);
        container.setBackgroundColor(getResources().getColor(android.R.color.white, null));
        container.setElevation(dpToPx(1));
        container.setPadding(dpToPx(6), dpToPx(6), dpToPx(6), dpToPx(6));

        TextView nameText = new TextView(this);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        nameText.setLayoutParams(nameParams);
        nameText.setText(symptomName);
        nameText.setTextColor(getResources().getColor(android.R.color.black, null));
        nameText.setTextSize(18);
        nameText.setTypeface(null, android.graphics.Typeface.BOLD);

        Button addButton = new Button(this);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                dpToPx(60), dpToPx(32));
        addButton.setLayoutParams(buttonParams);
        addButton.setText("+");
        addButton.setTextColor(getResources().getColor(android.R.color.white, null));
        addButton.setTextSize(10);
        addButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF4CAF50));
        addButton.setOnClickListener(v -> addSymptom(symptomName, key, container, maxLabel));

        Button deleteButton = new Button(this);
        LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(
                dpToPx(60), dpToPx(32)); //simlar button size
        deleteParams.setMargins(dpToPx(4), 0, 0, 0);
        deleteButton.setLayoutParams(deleteParams);
        deleteButton.setText("X");
        deleteButton.setTextColor(getResources().getColor(android.R.color.white, null));
        deleteButton.setTextSize(8);
        deleteButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFF44336));
        deleteButton.setOnClickListener(v -> deleteCustomSymptom(key, container));

        container.addView(nameText);
        container.addView(addButton);
        container.addView(deleteButton);

        return container;
    }

    private void deleteCustomSymptom(String key, LinearLayout container) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Custom Symptom");
        builder.setMessage("Are you sure you want to delete this custom symptom?");

        builder.setPositiveButton("Delete", (dialog, which) -> {
            if (activeSymptomViews.containsKey(key)) {
                View activeView = activeSymptomViews.get(key);
                activeSymptomsContainer.removeView(activeView);
                activeSymptomViews.remove(key);
            }

            customSymptomsContainer.removeView(container);
            customSymptomContainers.remove(key);

            symptomValues.remove(key);

            Toast.makeText(this, "Custom symptom deleted", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void setupAutoSave() {
        autoSaveHandler = new Handler(Looper.getMainLooper());
        autoSaveRunnable = new Runnable() {
            @Override
            public void run() {
                autoSaveData();
                autoSaveHandler.postDelayed(this, AUTO_SAVE_INTERVAL);
            }
        };
    }

    private void startAutoSave() {
        if (autoSaveHandler != null && autoSaveRunnable != null) {
            autoSaveHandler.postDelayed(autoSaveRunnable, AUTO_SAVE_INTERVAL);
        }
    }

    private void stopAutoSave() {
        if (autoSaveHandler != null && autoSaveRunnable != null) {
            autoSaveHandler.removeCallbacks(autoSaveRunnable);
        }
    }

    private void autoSaveData() {
        boolean hasActiveSymptoms = false;
        for (Map.Entry<String, Integer> entry : symptomValues.entrySet()) {
            if (entry.getValue() > 0) {
                hasActiveSymptoms = true;
                break;
            }
        }

        if (hasActiveSymptoms) {
            Toast.makeText(this, "Auto-saved", Toast.LENGTH_SHORT).show();

            // **AUTO-SAVE DATA OUTPUT TO MAIN APP BUILD HERE**
        }
    }

    private void initializeSymptomValues() {
        symptomValues.put("chestPain", 0);
        symptomValues.put("breath", 0);
        symptomValues.put("bleeding", 0);
        symptomValues.put("fever", 0);
        symptomValues.put("chills", 0);
        symptomValues.put("muscleAches", 0);
        symptomValues.put("soreThroat", 0);
        symptomValues.put("lossOfTaste", 0);
        symptomValues.put("lossOfSmell", 0);
        symptomValues.put("headache", 0);
        symptomValues.put("rash", 0);
        symptomValues.put("nausea", 0);
        symptomValues.put("vomiting", 0);
        symptomValues.put("diarrhea", 0);
        symptomValues.put("confusion", 0);
        symptomValues.put("dizzy", 0);
    }

    /**
     * Adds selected active symptom the container and tracks severity
     * @param displayName
     * @param key
     * @param symptomContainer
     * @param severity
     */
    private void addSymptom(String displayName, String key, LinearLayout symptomContainer, String severity) {
        if (activeSymptomViews.containsKey(key)) {
            Toast.makeText(this, displayName + " is already active", Toast.LENGTH_SHORT).show();
            return;
        }

        symptomContainer.setVisibility(View.GONE);
        symptomValues.put(key,Integer.parseInt(severity));

        View symptomView = createExpandedSymptomView(displayName, key, symptomContainer, Integer.parseInt(severity));

        activeSymptomsContainer.addView(symptomView, 0);
        activeSymptomViews.put(key, symptomView);
    }

    /**
     * Expanded container for active symptom so that severity can be indicated
     * @param displayName
     * @param key
     * @param originalContainer
     * @param severity
     * @return
     */
    private View createExpandedSymptomView(String displayName, String key, LinearLayout originalContainer, int severity) {
        LinearLayout mainContainer = new LinearLayout(this);
        mainContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams mainParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mainParams.setMargins(0, 0, 0, dpToPx(8));
        mainContainer.setLayoutParams(mainParams);
        mainContainer.setBackgroundColor(getResources().getColor(android.R.color.white, null));
        mainContainer.setElevation(dpToPx(2));
        mainContainer.setPadding(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4));

        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        headerLayout.setPadding(0, 0, 0, dpToPx(8));

        // Symptom
        TextView nameText = new TextView(this);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        nameText.setLayoutParams(nameParams);
        nameText.setText(displayName);
        nameText.setTextColor(getResources().getColor(android.R.color.black, null));
        nameText.setTextSize(18);
        nameText.setTypeface(null, android.graphics.Typeface.BOLD);

        // Severity numerical value
        TextView valueDisplay = new TextView(this);
        LinearLayout.LayoutParams valueParams = new LinearLayout.LayoutParams(
                dpToPx(32), dpToPx(32));
        valueParams.setMargins(dpToPx(8), 0, dpToPx(8), 0);
        valueDisplay.setLayoutParams(valueParams);
        String severityValue = (severity == 0)? "5" : String.valueOf(severity);
        valueDisplay.setText(severityValue);
        valueDisplay.setTextColor(getResources().getColor(android.R.color.white, null));
        valueDisplay.setTextSize(14);
        valueDisplay.setTypeface(null, android.graphics.Typeface.BOLD);
        valueDisplay.setGravity(android.view.Gravity.CENTER);
        valueDisplay.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark, null));

        // Remove symptom button
        Button removeButton = new Button(this);
        LinearLayout.LayoutParams removeParams = new LinearLayout.LayoutParams(
                dpToPx(32), dpToPx(32));
        removeButton.setLayoutParams(removeParams);
        removeButton.setText("X");
        removeButton.setTextSize(14);
        removeButton.setTextColor(getResources().getColor(R.color.black));
        removeButton.setBackgroundColor(getResources().getColor(R.color.white));
        removeButton.setPadding(0,0,0,0);
        removeButton.setOnClickListener(v -> removeSymptom(key, originalContainer));

        headerLayout.addView(nameText);
        headerLayout.addView(valueDisplay);
        headerLayout.addView(removeButton);

        // Drag bar to adjust severity
        SeekBar seekBar = new SeekBar(this);
        seekBar.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        seekBar.setMax(10);
        seekBar.setProgress((severity == 0)? 5 : severity);

        // Drag bar Labels
        LinearLayout labelsLayout = new LinearLayout(this);
        labelsLayout.setOrientation(LinearLayout.HORIZONTAL);
        labelsLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        labelsLayout.setPadding(0, dpToPx(4), 0, 0);

        TextView noneLabel = new TextView(this);
        LinearLayout.LayoutParams noneLabelParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        noneLabel.setLayoutParams(noneLabelParams);
        noneLabel.setText("None");
        noneLabel.setTextColor(getResources().getColor(android.R.color.darker_gray, null));
        noneLabel.setTextSize(12);
        noneLabel.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView maxLabelText = new TextView(this);
        LinearLayout.LayoutParams maxLabelParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        maxLabelText.setLayoutParams(maxLabelParams);
        maxLabelText.setText("Severe");
        maxLabelText.setTextColor(getResources().getColor(android.R.color.darker_gray, null));
        maxLabelText.setTextSize(12);
        maxLabelText.setTypeface(null, android.graphics.Typeface.BOLD);
        maxLabelText.setGravity(android.view.Gravity.END);

        labelsLayout.addView(noneLabel);
        labelsLayout.addView(maxLabelText);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                symptomValues.put(key, progress);
                updateSliderAndDisplay(seekBar, valueDisplay, progress);

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        updateSliderAndDisplay(seekBar, valueDisplay, (severity == 0)? 5 : severity);
        if (severity == 0) {
            symptomValues.put(key, 5);
        }

        mainContainer.addView(headerLayout);
        mainContainer.addView(seekBar);
        mainContainer.addView(labelsLayout);

        return mainContainer;
    }

    /**
     * Remove an active symptom
     * @param key
     * @param symptomContainer
     */
    private void removeSymptom(String key, LinearLayout symptomContainer) {
        View activeView = activeSymptomViews.get(key);
        if (activeView != null) {
            activeSymptomsContainer.removeView(activeView);
            activeSymptomViews.remove(key);
        }

        symptomValues.put(key, 0);
        symptomContainer.setVisibility(View.VISIBLE);
    }

    /**
     * Updating numerical slider display to show severity
     * @param slider
     * @param valueDisplay
     * @param progress
     */
    private void updateSliderAndDisplay(SeekBar slider, TextView valueDisplay, int progress) {
        int color;
        if (progress <= 3) {
            color = getResources().getColor(android.R.color.holo_green_dark, null);
        } else if (progress <= 7) {
            color = getResources().getColor(android.R.color.holo_orange_dark, null);
        } else {
            color = getResources().getColor(android.R.color.holo_red_dark, null);
        }

        slider.getProgressDrawable().setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);
        slider.getThumb().setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);

        valueDisplay.setText(String.valueOf(progress));
        valueDisplay.setBackgroundColor(color);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    /**
     * Saving symptom data when the saveButton is clicked
     */
    private void saveData() {
        StringBuilder message = new StringBuilder("Manually saved symptoms: ");
        boolean hasActiveSymptoms = false;
        // Tools for saving symptom data to Database
        String date = getDate();
        SymptomDatabaseHelper databaseHelper = new SymptomDatabaseHelper(SymptomsActivity.this);

        for (Map.Entry<String, Integer> entry : symptomValues.entrySet()) {
            if (entry.getValue() > 0 && !Objects.equals(entry.getValue(), oldSymptomValues.get(entry.getKey()))) {
                if (hasActiveSymptoms) {
                    message.append(", ");
                }
                message.append(entry.getKey()).append("=").append(entry.getValue());
                hasActiveSymptoms = true;
                // Save symptom to database
                if (entry.getValue() != 0) {
                    databaseHelper.insertSymptom(date, entry.getKey(), entry.getValue().toString(), getTime());
                }
            }
        }

        if (!hasActiveSymptoms) {
            message.append("No active symptoms");
        }

        Toast.makeText(this, message.toString(), Toast.LENGTH_LONG).show();

        // Return to main screen
        Intent intent = new Intent(SymptomsActivity.this, MainActivity.class);
        intent.putExtra("SYMPTOMS_DATA", getSymptomValues());
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);

    }

    /**
     * Clear selected symptom data
     */
    private void clearData() {
        activeSymptomsContainer.removeAllViews();
        activeSymptomViews.clear();

        for (Map.Entry<String, Integer> entry : symptomValues.entrySet()) {
            entry.setValue(0);
        }

        LinearLayout[] standardContainers = {
                chestPainContainer, breathContainer, bleedingContainer, feverContainer,
                chillsContainer, muscleAchesContainer, soreThroatContainer, lossOfTasteContainer,
                lossOfSmellContainer, headacheContainer, rashContainer, nauseaContainer,
                vomitingContainer, diarrheaContainer, confusionContainer, dizzyContainer
        };

        for (LinearLayout container : standardContainers) {
            if (container != null) {
                container.setVisibility(View.VISIBLE);
            }
        }

        for (LinearLayout container : customSymptomContainers.values()) {
            container.setVisibility(View.VISIBLE);
        }

        Toast.makeText(this, "All data cleared", Toast.LENGTH_SHORT).show();
    }

    /**
     * Getting the date in the standard format: Month D, YYYY
     * @return
     */
    private String getDate(){
        LocalDate date = LocalDate.now();
        String month = date.getMonth().toString();
        String day = String.valueOf(date.getDayOfMonth());
        String year = String.valueOf(date.getYear());
        return month + " " + day + ", " + year;
    }

    /**
     *  Get the time in the format HH:MM
     * @return timestamp
     */
    private String getTime(){
        LocalDateTime time = LocalDateTime.now();
        int hour = time.getHour();
        int minute = time.getMinute();
        return hour + ":" + minute;
    }


    /**
     * Gets list of active symptoms
     * @return activeSymptoms
     */
    private HashMap<String, String> getSymptomValues(){
        HashMap<String, String> activeSymptoms = new HashMap<String, String>();
        for (Map.Entry<String, Integer> entry : symptomValues.entrySet()) {
            if (entry.getValue() != 0) {
                activeSymptoms.put(entry.getKey(), entry.getValue().toString());
            }
        }
        return activeSymptoms;
    }

    /**
     * Loads any previous symptom day for the day
     */
    private void loadSymptomsFromDatabase() {
        clearData();
        SymptomDatabaseHelper dbHelper = new SymptomDatabaseHelper(this);

        SymptomDay symptomDay = dbHelper.getSymptomsForDate(getDate());
        if (symptomDay == null) {
            return;
        }

        // Based on the symptom records for the day, sets up the symptomValues List
        for (SymptomRecord record : symptomDay.getFullSymptomList()){
            oldSymptomValues.put(record.getSymptom(), Integer.parseInt(record.getSeverity()));
            symptomValues.put(record.getSymptom(), Integer.parseInt(record.getSeverity()));
        }

        for (Map.Entry<String, Integer> entry : symptomValues.entrySet()) {
            if (entry.getValue() != 0) {
                LinearLayout symptomContainer = getSymptomContainer(entry.getKey());
                /*
            // If it's not a default symptom and instead a custom symptom
            if (symptomContainer == null){
                symptomContainer = customSymptomContainers.get(entry.getKey());
            } */

                addSymptom(turnKeyIntoDisplay(entry.getKey()), entry.getKey(), symptomContainer, String.valueOf(entry.getValue()));
            }
        }

    }

    /**
     * Turns the key value in symptomValues to the Display Name
     * @param key
     * @return
     */
    private String turnKeyIntoDisplay(String key) {
        String result = key.replaceAll("(?<!^)(?=[A-Z])", " ");
        return Character.toUpperCase(result.charAt(0)) + result.substring(1);
    }

    /**
     * Returns the container for a given key
     * @param key
     * @return
     */
    private LinearLayout getSymptomContainer(@NonNull String key){
        LinearLayout container;
        switch (key) {
            case "chestPain": return chestPainContainer;
            case "shortnessOfBreath": return breathContainer;
            case "bleeding": return bleedingContainer;
            case "fever": return feverContainer;
            case "chills": return chillsContainer;
            case "muscleAches": return muscleAchesContainer;
            case "soreThroat": return soreThroatContainer;
            case "lossOfTaste": return lossOfTasteContainer;
            case "lossOfSmell": return lossOfSmellContainer;
            case "headache": return headacheContainer;
            case "rash": return rashContainer;
            case "nausea": return nauseaContainer;
            case "vomiting": return vomitingContainer;
            case "diarrhea": return diarrheaContainer;
            case "confusion": return confusionContainer;
            case "dizzy": return dizzyContainer;
            default: return null; // was a custom symptom
        }
    }


    /**
     * Turn the given symptom into a key for symptomValues
     * @param symptom
     * @return
     */
    private String turnSymptomIntoKey(String symptom){
        if (symptom == null || symptom.trim().isEmpty()) return "";

        String[] words = symptom.trim().split("[\\s\\-_+]+");
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            String word = words[i].replaceAll("[^a-zA-Z0-0]", ""); // Remove remaining special characters
            if (word.isEmpty()) continue;

            if (i == 0) {
                // First word: completely lowercase
                key.append(word.toLowerCase());
            } else {
                // Subsequent words: capitalize first letter, lower the rest
                key.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    key.append(word.substring(1).toLowerCase());
                }
            }
        }

        return key.toString();
    }
}