package com.example.tragic.irate.simple.stopwatch;

import android.widget.CalendarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CalendarValues {

    Calendar calendar;

    int yearSelected;
    int monthSelected;
    int dayOfMonthSelected;
    int dayOfYearSelected;

    String simpleDate;

    public CalendarValues() {
        calendar = Calendar.getInstance();
        this.yearSelected = calendar.get(Calendar.YEAR);
        this.monthSelected = calendar.get(Calendar.MONTH);
        this.dayOfMonthSelected = calendar.get(Calendar.DAY_OF_MONTH);
        this.dayOfYearSelected = calendar.get(Calendar.DAY_OF_YEAR);
    }

    public String getDateString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMMM d yyyy", Locale.getDefault());
        return simpleDateFormat.format(calendar.getTime());
    }

    public int getDayOfYearSelected() {
        return dayOfYearSelected;
    }

    public int getYearSelected() {
        return yearSelected;
    }

    public int getMonthSelected() {
        return monthSelected;
    }

    public int getDayOfMonthSelected() {
        return dayOfMonthSelected;
    }
}
