package com.example.workout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    private static final String ARG_YEAR = "year";
    private static final String ARG_MONTH = "month";

    private int year;
    private int month;
    private DatabaseHelper db;

    public static CalendarFragment newInstance(int year, int month) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_YEAR, year);
        args.putInt(ARG_MONTH, month);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            year = getArguments().getInt(ARG_YEAR);
            month = getArguments().getInt(ARG_MONTH);
        }
        db = new DatabaseHelper(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        CalendarView calendarView = view.findViewById(R.id.calendar_view);
        LinearLayout exerciseSummaryContainer = view.findViewById(R.id.exercise_summary_container);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        calendarView.setDate(calendar.getTimeInMillis(), false, false);

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            String date = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
            exerciseSummaryContainer.removeAllViews();
            HashMap<String, ArrayList<SetData>> exerciseDataMap = db.getExerciseRecords(date);
            if (exerciseDataMap.isEmpty()) {
                Toast.makeText(getContext(), "No exercise records for this date", Toast.LENGTH_SHORT).show();
            } else {
                for (String exercise : exerciseDataMap.keySet()) {
                    ArrayList<SetData> sets = exerciseDataMap.get(exercise);
                    TextView exerciseTitle = new TextView(getContext());
                    exerciseTitle.setText(exercise);
                    exerciseTitle.setTextSize(18);
                    exerciseTitle.setTextColor(getResources().getColor(android.R.color.black));
                    exerciseSummaryContainer.addView(exerciseTitle);

                    for (SetData setData : sets) {
                        TextView setTextView = new TextView(getContext());
                        setTextView.setText(setData.getReps() + " reps");
                        setTextView.setTextSize(16);
                        setTextView.setTextColor(getResources().getColor(android.R.color.black));
                        exerciseSummaryContainer.addView(setTextView);
                    }
                }
            }
        });

        return view;
    }
}
