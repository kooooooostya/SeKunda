package com.example.sekunda;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.sekunda.Data.DayPagerAdapter;

public class HistoryActivity extends AppCompatActivity {

    ViewPager mViewPager;
    DayPagerAdapter mDayPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mDayPagerAdapter = new DayPagerAdapter(getApplicationContext());
        mViewPager = findViewById(R.id.history_pager);
        mViewPager.setAdapter(mDayPagerAdapter);

        // TODO вывод статисктики красиво
    }
}
