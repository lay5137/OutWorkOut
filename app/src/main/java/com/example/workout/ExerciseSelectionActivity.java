package com.example.workout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ExerciseSelectionActivity extends AppCompatActivity {

    private LinearLayout exerciseContainer;
    private ArrayList<String> selectedExercises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_selection);

        exerciseContainer = findViewById(R.id.exercise_container);
        selectedExercises = new ArrayList<>();

        Button routineButton = findViewById(R.id.routine_button);
        routineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedExercises.clear();
                for (int i = 0; i < exerciseContainer.getChildCount(); i++) {
                    View child = exerciseContainer.getChildAt(i);
                    if (child instanceof CheckBox && ((CheckBox) child).isChecked()) {
                        selectedExercises.add(((CheckBox) child).getText().toString());
                    }
                }
                Intent intent = new Intent(ExerciseSelectionActivity.this, SetRoutineActivity.class);
                intent.putStringArrayListExtra("selectedExercises", selectedExercises);
                startActivity(intent);
            }
        });
    }
}