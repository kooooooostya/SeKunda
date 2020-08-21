package com.example.sekunda.Data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.example.sekunda.R;

import java.util.ArrayList;
import java.util.Calendar;

public class DayPagerAdapter extends PagerAdapter {

    private ArrayList<Day> mDayArrayList;
    private Context mContext;
    private static final int PAGE_COUNT = 7;

    public DayPagerAdapter(Context context) {
        mContext = context;
        mDayArrayList = getFulledDayList();

    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View itemView = inflater.inflate(R.layout.pager_item, null);

        TextView textViewDay = itemView.findViewById(R.id.day_item_tv_day);
        TextView textViewTotalTime = itemView.findViewById(R.id.day_item_tv_all_time);
        RecyclerView recyclerView = itemView.findViewById(R.id.day_item_recycler);

        Day day = mDayArrayList.get(position);

        textViewDay.setText(day.getCalendarTime());
        textViewTotalTime.setText(day.getAllTime());
        BusinessRecyclerAdapter businessRecyclerAdapter;
        if (day.getBusinessArrayList().size() == 0){
            ArrayList<Business> arrayList = new ArrayList();
            arrayList.add(new Business("Nothing Today", 0));
            businessRecyclerAdapter =
                    new BusinessRecyclerAdapter(arrayList);
        }else {
            businessRecyclerAdapter =
                    new BusinessRecyclerAdapter(day.getBusinessArrayList());
        }


        recyclerView.setAdapter(businessRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));


        container.addView(itemView);

        return itemView;
    }

    private ArrayList<Day> getFulledDayList(){
        ArrayList<Day> dayArrayList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < PAGE_COUNT; i++){
            dayArrayList.add(new Day((Calendar)calendar.clone(), mContext));
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
        }
        return dayArrayList;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
