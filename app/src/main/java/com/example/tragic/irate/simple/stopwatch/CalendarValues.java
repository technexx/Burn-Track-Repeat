package com.example.tragic.irate.simple.stopwatch;

import android.widget.CalendarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class CalendarValues {

    Calendar calendar;
    int dayOfYearSelected;

    public CalendarValues(){
        calendar = Calendar.getInstance();
        this.dayOfYearSelected = calendar.get(Calendar.DAY_OF_YEAR);
    }

    public String getDateString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMMM d yyyy", Locale.getDefault());
        return simpleDateFormat.format(calendar.getTime());
    }

    public int getCurrentDayOfYear() {
        calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_YEAR);
    }
}
