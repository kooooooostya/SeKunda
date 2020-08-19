package com.example.sekunda.Data;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Business implements Comparable, Cloneable{
    private String mName;
    private int mSeconds;
    private Calendar mTimeStart;
    private boolean isComplete;

    public Business(String name, int seconds) {
        mName = name;
        mSeconds = seconds;
        mTimeStart = Calendar.getInstance();
        isComplete = false;

    }

    public Business(String name, int seconds, Calendar timeStart) {
        mName = name;
        mSeconds = seconds;
        mTimeStart = timeStart;
        isComplete = false;

    }
    public Business(String name, int seconds, Calendar timeStart, boolean isComplete) {
        mName = name;
        mSeconds = seconds;
        mTimeStart = timeStart;
        this.isComplete = isComplete;
    }

    public void setName(String name) {
        mName = name;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public Calendar getTimeStart() {
        return mTimeStart;
    }

    public String getName() {
        return mName;
    }

    public int getSeconds() {
        return mSeconds;
    }

    public void setSeconds(int seconds){
        this.mSeconds = seconds;
    }

    public void addOneSecond(){
        this.mSeconds++;
    }

    public String getTime(){
        String ans;
        StringBuilder stringBuffer = new StringBuilder("");
        int hours = this.mSeconds / 3600;
        int minutes = this.mSeconds / 60 - hours * 60;
        int seconds = this.mSeconds % 60;
        if (hours > 0){
            stringBuffer.append(hours).append("h");
        }
        if (minutes >= 0) stringBuffer.append(minutes>9?minutes:"0"+ minutes).append("m:");
        if (seconds >= 0) stringBuffer.append(seconds).append("s");

        ans = stringBuffer.toString();
        return ans;
    }

    public String getFullCalendarTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BusinessSQLiteOpenHelper.FULL_PATTERN, Locale.ENGLISH);
        return simpleDateFormat.format(this.mTimeStart.getTime());
    }
    // it is necessary to determine the current day in sql and select tasks for today
    public String getShortCalendarTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BusinessSQLiteOpenHelper.SHORT_PATTERN, Locale.ENGLISH);
        return simpleDateFormat.format(this.mTimeStart.getTime());
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Business){
           return this.mSeconds * 100 - ((Business) o).mSeconds * 100;
        }else return 0;
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        return new Business(this.mName, this.mSeconds, this.mTimeStart, isComplete);
    }
}
