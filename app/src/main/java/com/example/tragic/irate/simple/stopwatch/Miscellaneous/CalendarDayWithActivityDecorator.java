package com.example.tragic.irate.simple.stopwatch.Miscellaneous;

import android.content.Context;
import android.text.style.ForegroundColorSpan;

import com.example.tragic.irate.simple.stopwatch.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.ArrayList;
import java.util.List;

public class CalendarDayWithActivityDecorator implements DayViewDecorator {
    Context mContext;
    List<CalendarDay> mCalendarDayListWithAtLeastOneActivity;

    public CalendarDayWithActivityDecorator(Context context) {
        this.mContext = context;
        mCalendarDayListWithAtLeastOneActivity = new ArrayList<>();
    }

    public void setCalendarDayList(List<CalendarDay> calendarDayList) {
        this.mCalendarDayListWithAtLeastOneActivity = calendarDayList;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return mCalendarDayListWithAtLeastOneActivity.contains(day);
    }

    @Override
    public void decorate(DayViewFacade dayViewFacade) {
        dayViewFacade.addSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.green)));
    }
}
