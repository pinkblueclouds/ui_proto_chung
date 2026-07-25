package com.example.ui_proto_original;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class SymptomsActivity extends AppCompatActivity {

    private LinearLayout activeSymptomsContainer;
    private LinearLayout customSymptomsContainer;
    private Map<String, Integer> symptomValues = new HashMap<>();
    private Map<String, View> activeSymptomViews = new HashMap<>();
    private Map<String, LinearLayout> customSymptomContainers = new HashMap<>();
    private AtomicInteger customSymptomCounter = new AtomicInteger(0);

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

    }

    @Override
    protected void onResume() {
        super.onResume();
        startAutoSave();
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
        addChestPainButton.setOnClickListener(v -> addSymptom("Chest Pain", "chestPain", chestPainContainer, "Severe"));
        addBreathButton.setOnClickListener(v -> addSymptom("Shortness of Breath", "breath", breathContainer, "Severe"));
        addBleedingButton.setOnClickListener(v -> addSymptom("Bleeding", "bleeding", bleedingContainer, "Severe"));
        addFeverButton.setOnClickListener(v -> addSymptom("Fever", "fever", feverContainer, "Severe"));
        addChillsButton.setOnClickListener(v -> addSymptom("Chills", "chills", chillsContainer, "Severe"));
        addMuscleAchesButton.setOnClickListener(v -> addSymptom("Muscle Aches", "muscleAches", muscleAchesContainer, "Severe"));
        addSoreThroatButton.setOnClickListener(v -> addSymptom("Sore Throat", "soreThroat", soreThroatContainer, "Severe"));
        addLossOfTasteButton.setOnClickListener(v -> addSymptom("Loss of Taste", "lossOfTaste", lossOfTasteContainer, "Complete"));
        addLossOfSmellButton.setOnClickListener(v -> addSymptom("Loss of Smell", "lossOfSmell", lossOfSmellContainer, "Complete"));
        addHeadacheButton.setOnClickListener(v -> addSymptom("Headache", "headache", headacheContainer, "Severe"));
        addRashButton.setOnClickListener(v -> addSymptom("Rash", "rash", rashContainer, "Severe"));
        addNauseaButton.setOnClickListener(v -> addSymptom("Nausea", "nausea", nauseaContainer, "Severe"));
        addVomitingButton.setOnClickListener(v -> addSymptom("Vomiting", "vomiting", vomitingContainer, "Severe"));
        addDiarrheaButton.setOnClickListener(v -> addSymptom("Diarrhea", "diarrhea", diarrheaContainer, "Severe"));
        addConfusionButton.setOnClickListener(v -> addSymptom("Confusion", "confusion", confusionContainer, "Severe"));
        addDizzyButton.setOnClickListener(v -> addSymptom("Dizzy", "dizzy", dizzyContainer, "Severe"));
    }

    private void setupControlButtons() {
        saveButton.setOnClickListener(v -> saveData());
        clearButton.setOnClickListener(v -> clearData());
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

    private void addCustomSymptom(String symptomName, String maxLabel) {
        int customId = customSymptomCounter.incrementAndGet();
        String key = "custom_" + customId;

        // dupe check
        for (String existingKey : symptomValues.keySet()) {
            if (activeSymptomViews.containsKey(existingKey)) {
                View existingView = activeSymptomViews.get(existingKey);
                if (existingView != null && existingView instanceof ViewGroup) {
                    ViewGroup viewGroup = (ViewGroup) existingView;
                    if (viewGroup.getChildCount() > 0) {
                        View headerLayout = viewGroup.getChildAt(0);
                        if (headerLayout instanceof ViewGroup) {
                            ViewGroup headerGroup = (ViewGroup) headerLayout;
                            if (headerGroup.getChildCount() > 0) {
                                View nameView = headerGroup.getChildAt(0);
                                if (nameView instanceof TextView) {
                                    String existingName = ((TextView) nameView).getText().toString();
                                    if (existingName.equalsIgnoreCase(symptomName)) {
                                        Toast.makeText(this, "Symptom already exists in active symptoms", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        LinearLayout customContainer = createCustomSymptomContainer(symptomName, key, maxLabel);
        customSymptomsContainer.addView(customContainer);
        customSymptomContainers.put(key, customContainer);

        symptomValues.put(key, 0);

        addSymptom(symptomName, key, customContainer, maxLabel);

        Toast.makeText(this, "Added custom symptom: " + symptomName, Toast.LENGTH_SHORT).show();
    }

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

    private void addSymptom(String displayName, String key, LinearLayout originalContainer, String maxLabel) {
        if (activeSymptomViews.containsKey(key)) {
            Toast.makeText(this, displayName + " is already active", Toast.LENGTH_SHORT).show();
            return;
        }

        originalContainer.setVisibility(View.GONE);

        View symptomView = createExpandedSymptomView(displayName, key, maxLabel, originalContainer);

        activeSymptomsContainer.addView(symptomView, 0);

        activeSymptomViews.put(key, symptomView);
    }

    private View createExpandedSymptomView(String displayName, String key, String maxLabel, LinearLayout originalContainer) {
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

        TextView nameText = new TextView(this);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        nameText.setLayoutParams(nameParams);
        nameText.setText(displayName);
        nameText.setTextColor(getResources().getColor(android.R.color.black, null));
        nameText.setTextSize(18);
        nameText.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView valueDisplay = new TextView(this);
        LinearLayout.LayoutParams valueParams = new LinearLayout.LayoutParams(
                dpToPx(32), dpToPx(32));
        valueParams.setMargins(dpToPx(8), 0, dpToPx(8), 0);
        valueDisplay.setLayoutParams(valueParams);
        valueDisplay.setText("0");
        valueDisplay.setTextColor(getResources().getColor(android.R.color.white, null));
        valueDisplay.setTextSize(14);
        valueDisplay.setTypeface(null, android.graphics.Typeface.BOLD);
        valueDisplay.setGravity(android.view.Gravity.CENTER);
        valueDisplay.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark, null));

        Button removeButton = new Button(this);
        LinearLayout.LayoutParams removeParams = new LinearLayout.LayoutParams(
                dpToPx(32), dpToPx(32));
        removeButton.setLayoutParams(removeParams);
        removeButton.setText("X");
        removeButton.setTextSize(14);
        removeButton.setTextColor(getResources().getColor(android.R.color.white, null));
        removeButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark, null));
        removeButton.setPadding(0,0,0,0);
        removeButton.setOnClickListener(v -> removeSymptom(key, originalContainer));

        headerLayout.addView(nameText);
        headerLayout.addView(valueDisplay);
        headerLayout.addView(removeButton);

        SeekBar seekBar = new SeekBar(this);
        seekBar.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        seekBar.setMax(10);
        seekBar.setProgress(0);

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
        maxLabelText.setText(maxLabel);
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

        updateSliderAndDisplay(seekBar, valueDisplay, 0);

        mainContainer.addView(headerLayout);
        mainContainer.addView(seekBar);
        mainContainer.addView(labelsLayout);

        return mainContainer;
    }

    private void removeSymptom(String key, LinearLayout originalContainer) {
        View activeView = activeSymptomViews.get(key);
        if (activeView != null) {
            activeSymptomsContainer.removeView(activeView);
            activeSymptomViews.remove(key);
        }

        symptomValues.put(key, 0);
        originalContainer.setVisibility(View.VISIBLE);
    }

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

    private void saveData() {
        StringBuilder message = new StringBuilder("Manually saved symptoms: ");
        boolean hasActiveSymptoms = false;

        for (Map.Entry<String, Integer> entry : symptomValues.entrySet()) {
            if (entry.getValue() > 0) {
                if (hasActiveSymptoms) {
                    message.append(", ");
                }
                message.append(entry.getKey()).append("=").append(entry.getValue());
                hasActiveSymptoms = true;
            }
        }

        if (!hasActiveSymptoms) {
            message.append("No active symptoms");
        }

        Toast.makeText(this, message.toString(), Toast.LENGTH_LONG).show();

        Intent intent = new Intent(SymptomsActivity.this, MainActivity.class);
        intent.putExtra("SYMPTOMS_DATA", getSymptomValues());
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);

        // **DATA OUTPUT TO MAIN APP BUILD HERE**

    }

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

    private void addNewEntry() {
        showCustomSymptomDialog();
    }

    private HashMap<String, Integer> getSymptomValues(){
        HashMap<String, Integer> activeSymptoms = new HashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : symptomValues.entrySet()) {
            if (entry.getValue() != 0) {
                activeSymptoms.put(entry.getKey(), entry.getValue());
            }
        }
        return activeSymptoms;
    }
}