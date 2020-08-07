package com.example.sekunda.Data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Business implements Comparable{
    private String mName;
    private int mSeconds;
    private Calendar mCalendar;

    public Business(String name, int seconds) {
        mName = name;
        mSeconds = seconds;
        mCalendar = Calendar.getInstance();
        Date date = new Date();
        mCalendar.setTime(date);
    }
    public Business(String name, int seconds, Calendar calendar) {
        mName = name;
        mSeconds = seconds;
        mCalendar = calendar;
    }

    public Calendar getCalendar() {
        return mCalendar;
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

    public String getCalendarTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        return simpleDateFormat.format(this.mCalendar.getTime());
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Business){
           return (int)(this.mSeconds * 100 - ((Business) o).mSeconds * 100) ;
        }else return 0;
    }
}
