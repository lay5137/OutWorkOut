package com.example.workout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class BodyWeightExerciseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bodyweight_exercise);

        Button configureRoutineButton = findViewById(R.id.create_routine);
        configureRoutineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BodyWeightExerciseActivity.this, ExerciseSelectionActivity.class);
                startActivity(intent);
            }
        });
    }
}