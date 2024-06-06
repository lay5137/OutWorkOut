package com.example.workout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.example.workout.SetData;

public class SummaryActivity extends AppCompatActivity {

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        db = new DatabaseHelper(this);
        LinearLayout summaryContainer = findViewById(R.id.summary_container);
        Button homeButton = findViewById(R.id.home_button);

        Intent intent = getIntent();
        HashMap<String, ArrayList<SetData>> exerciseDataMap = (HashMap<String, ArrayList<SetData>>) intent.getSerializableExtra("exerciseDataMap");

        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        for (String exercise : exerciseDataMap.keySet()) {
            ArrayList<SetData> sets = exerciseDataMap.get(exercise);
            boolean hasCheckedSet = false;
            for (SetData setData : sets) {
                if (setData.isCompleted()) {
                    hasCheckedSet = true;
                    db.addExerciseRecord(currentDate, exercise, setData.getReps()); // 데이터베이스에 저장
                }
            }
            if (!hasCheckedSet) continue;

            TextView exerciseTitle = new TextView(this);
            exerciseTitle.setText(exercise);
            exerciseTitle.setTextSize(18);
            exerciseTitle.setTextColor(getResources().getColor(android.R.color.black));
            summaryContainer.addView(exerciseTitle);

            for (SetData setData : sets) {
                if (setData.isCompleted()) {
                    TextView setTextView = new TextView(this);
                    setTextView.setText(setData.getReps() + " reps");
                    setTextView.setTextSize(16);
                    setTextView.setTextColor(getResources().getColor(android.R.color.black));
                    summaryContainer.addView(setTextView);
                }
            }
        }

        homeButton.setOnClickListener(v -> {
            Intent homeIntent = new Intent(SummaryActivity.this, MainActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
            finish();
        });
    }
}
