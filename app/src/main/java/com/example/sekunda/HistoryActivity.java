package com.example.sekunda;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;


import com.example.sekunda.Data.DayPagerAdapter;

public class HistoryActivity extends AppCompatActivity {

    ViewPager mViewPager;
    DayPagerAdapter mDayPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = findViewById(R.id.history_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle(R.string.history_activity);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        mDayPagerAdapter = new DayPagerAdapter(getApplicationContext());
        mViewPager = findViewById(R.id.history_pager);
        mViewPager.setAdapter(mDayPagerAdapter);
    }
}
