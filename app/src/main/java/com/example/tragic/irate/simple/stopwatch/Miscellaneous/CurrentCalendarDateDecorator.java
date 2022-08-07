package com.example.tragic.irate.simple.stopwatch.Miscellaneous;

import android.content.Context;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import androidx.appcompat.content.res.AppCompatResources;

import com.example.tragic.irate.simple.stopwatch.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.List;

public class CurrentCalendarDateDecorator implements DayViewDecorator {
    Context mContext;
    List<CalendarDay> mCurrentDayList;

    public CurrentCalendarDateDecorator(Context context) {
        this.mContext = context;
    }

    public void setCurrentDay(List<CalendarDay> currentDay) {
        this.mCurrentDayList = currentDay;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return mCurrentDayList.contains(day);
    }

    @Override
    public void decorate(DayViewFacade dayViewFacade) {
//        dayViewFacade.addSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.darker_red)));
        dayViewFacade.setBackgroundDrawable(AppCompatResources.getDrawable(mContext, R.drawable.current_calendar_day_circle));
    }
}
