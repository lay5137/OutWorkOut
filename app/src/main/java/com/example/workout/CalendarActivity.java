package com.example.workout;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class CalendarActivity extends AppCompatActivity {

    private ViewPager calendarViewPager;
    private CalendarPagerAdapter calendarPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarViewPager = findViewById(R.id.calendar_viewpager);
        calendarPagerAdapter = new CalendarPagerAdapter(getSupportFragmentManager());
        calendarViewPager.setAdapter(calendarPagerAdapter);

        // 중앙 페이지로 설정하여 현재 월을 표시
        calendarViewPager.setCurrentItem(CalendarPagerAdapter.START_POSITION);
    }
}
