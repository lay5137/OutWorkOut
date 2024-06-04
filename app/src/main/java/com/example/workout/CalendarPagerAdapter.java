package com.example.workout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.Calendar;

public class CalendarPagerAdapter extends FragmentPagerAdapter {

    public static final int START_POSITION = Integer.MAX_VALUE / 2;

    public CalendarPagerAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Override
    public Fragment getItem(int position) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, position - START_POSITION);
        return CalendarFragment.newInstance(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE; // 매우 큰 수로 설정하여 무한 스크롤이 가능하게 함
    }
}