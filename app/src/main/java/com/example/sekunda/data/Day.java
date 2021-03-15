package com.example.sekunda.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

class Day {

    private ArrayList<Business> mBusinessArrayList;
    private Calendar mCalendar;
    private int mAllSeconds;

    Day(Calendar calendar) {
        mCalendar = calendar;
        mBusinessArrayList = getFulledArrayList();
        mAllSeconds = findTotalTime();
    }

    private int findTotalTime(){
        int sum = 0;
        for(Business business: mBusinessArrayList){
            sum += business.getSeconds();
        }
        return sum;
    }

    String getAllTime(){
        String ans;
        StringBuilder stringBuffer = new StringBuilder();
        int hours = this.mAllSeconds / 3600;
        int minutes = this.mAllSeconds / 60 - hours * 60;
        int seconds = this.mAllSeconds % 60;
        if (hours > 0){
            stringBuffer.append(hours).append("h");
        }
        if (minutes >= 0) stringBuffer.append(minutes>9?minutes:"0"+ minutes).append("m:");
        if (seconds >= 0) stringBuffer.append(seconds).append("s");

        ans = stringBuffer.toString();
        return ans;
    }

    ArrayList<Business> getBusinessArrayList() {
        return mBusinessArrayList;
    }

    String getCalendarTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Business.DMY_PATTERN, Locale.ENGLISH);
        return simpleDateFormat.format(this.mCalendar.getTime());
    }

    private ArrayList<Business> getFulledArrayList(){
        //BusinessSQLiteOpenHelper sqLiteOpenHelper = new BusinessSQLiteOpenHelper(context);
//        <Business> businessArrayList = SeKaundaApplication.db.businessDao().
//                getFulledList(new SimpleDateFormat(Business.SHORT_PATTERN, Locale.ENGLISH).format(mCalendar));

        //TODO заглушка
        ArrayList businessArrayList = new ArrayList<Business>();
        //sqLiteOpenHelper.close();
        return businessArrayList;
    }

}
