package com.example.workout;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class SetRoutineActivity extends AppCompatActivity {

    private LinearLayout setContainer;
    private ArrayList<String> exercises;
    private int currentExerciseIndex = 0;
    private int setCount = 0;
    private ScrollView scrollView;

    private HashMap<String, ArrayList<SetData>> exerciseDataMap;
    private Chronometer chronometer;
    private boolean isChronometerRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_routine);

        setContainer = findViewById(R.id.exercise_set_container);
        scrollView = findViewById(R.id.scrollView);
        exercises = getIntent().getStringArrayListExtra("selectedExercises");
        exerciseDataMap = new HashMap<>();

        chronometer = findViewById(R.id.exercise_time);
        if (!isChronometerRunning) {
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
            isChronometerRunning = true;
        }

        Button addButton = findViewById(R.id.add_button);
        Button deleteButton = findViewById(R.id.delete_button);
        Button previousButton = findViewById(R.id.previous_button);
        Button nextButton = findViewById(R.id.next_button);
        Button finishButton = findViewById(R.id.finish_button);

        addButton.setOnClickListener(v -> addSet());

        deleteButton.setOnClickListener(v -> removeSet());

        previousButton.setOnClickListener(v -> {
            if (currentExerciseIndex > 0) {
                saveCurrentExerciseData();
                currentExerciseIndex--;
                showExercise();
            }
        });

        nextButton.setOnClickListener(v -> {
            if (currentExerciseIndex < exercises.size() - 1) {
                saveCurrentExerciseData();
                currentExerciseIndex++;
                showExercise();
            }
        });

        finishButton.setOnClickListener(v -> {
            chronometer.stop();
            isChronometerRunning = false;
            saveCurrentExerciseData();
            Intent intent = new Intent(SetRoutineActivity.this, SummaryActivity.class);
            intent.putExtra("exerciseDataMap", (Serializable) exerciseDataMap);
            startActivity(intent);
            finish();
        });

        showExercise();
    }

    private void showExercise() {
        TextView exerciseTitle = findViewById(R.id.exercise_title);
        exerciseTitle.setText(exercises.get(currentExerciseIndex));
        setContainer.removeAllViews();
        setCount = 0;

        ArrayList<SetData> sets = exerciseDataMap.get(exercises.get(currentExerciseIndex));
        if (sets != null) {
            for (SetData setData : sets) {
                addSet(setData.getReps(), setData.isCompleted());
            }
        }
    }

    private void saveCurrentExerciseData() {
        ArrayList<SetData> sets = new ArrayList<>();
        for (int i = 0; i < setContainer.getChildCount(); i++) {
            LinearLayout setLayout = (LinearLayout) setContainer.getChildAt(i);
            EditText repsEditText = (EditText) setLayout.getChildAt(1);
            CheckBox checkBox = (CheckBox) setLayout.getChildAt(2);
            String repsText = repsEditText.getText().toString().replace(" reps", "");
            boolean completed = checkBox.isChecked();
            sets.add(new SetData(repsText, completed));
        }
        exerciseDataMap.put(exercises.get(currentExerciseIndex), sets);
    }

    private void addSet() {
        addSet("0", false);
    }

    private void addSet(String reps, boolean completed) {
        setCount++;
        LinearLayout newSetLayout = createNewSetLayout(setCount, reps, completed);
        setContainer.addView(newSetLayout);

        // Add this to scroll to the bottom when a new set is added
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    private void removeSet() {
        if (setCount > 0) {
            setContainer.removeViewAt(setCount - 1);
            setCount--;
        }
    }

    private LinearLayout createNewSetLayout(int setNumber, String reps, boolean completed) {
        LinearLayout newSetLayout = new LinearLayout(this);
        newSetLayout.setOrientation(LinearLayout.HORIZONTAL);
        newSetLayout.setPadding(8, 8, 8, 8);
        newSetLayout.setGravity(Gravity.CENTER_VERTICAL);

        TextView setNumberView = new TextView(this);
        setNumberView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        setNumberView.setText(String.valueOf(setNumber));
        setNumberView.setTextSize(18);
        setNumberView.setGravity(Gravity.CENTER);
        setNumberView.setBackground(getResources().getDrawable(R.drawable.circle_background));
        setNumberView.setTextColor(getResources().getColor(android.R.color.white));

        EditText repsEditText = new EditText(this);
        repsEditText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2));
        repsEditText.setHint("0 reps");
        repsEditText.setTextSize(18);
        repsEditText.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        repsEditText.setPadding(16, 0, 0, 0);
        repsEditText.setGravity(Gravity.TOP);  // Text gravity to top
        repsEditText.setPadding(16, 8, 0, 0);  // Padding to adjust the text position
        repsEditText.addTextChangedListener(new RepsTextWatcher(repsEditText));
        repsEditText.setText(reps + " reps");

        CheckBox checkBox = new CheckBox(this);
        checkBox.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        checkBox.setGravity(Gravity.CENTER);
        checkBox.setChecked(completed);

        newSetLayout.addView(setNumberView);
        newSetLayout.addView(repsEditText);
        newSetLayout.addView(checkBox);

        return newSetLayout;
    }

    private class RepsTextWatcher implements TextWatcher {

        private EditText editText;

        RepsTextWatcher(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Do nothing
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Do nothing
        }

        @Override
        public void afterTextChanged(Editable s) {
            editText.removeTextChangedListener(this);
            String input = s.toString().replace(" reps", "");
            if (!input.isEmpty()) {
                editText.setText(input + " reps");
                editText.setSelection(input.length());
            }
            editText.addTextChangedListener(this);
        }
    }

}
