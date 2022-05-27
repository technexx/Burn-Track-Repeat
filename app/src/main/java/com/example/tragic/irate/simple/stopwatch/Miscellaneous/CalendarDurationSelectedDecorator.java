package com.example.tragic.irate.simple.stopwatch.Miscellaneous;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.text.style.ForegroundColorSpan;

import com.example.tragic.irate.simple.stopwatch.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.List;

public class CalendarDurationSelectedDecorator implements DayViewDecorator {
    Context mContext;
    List<CalendarDay> mCalendarDayList;

    public void setCalendarDayList(List<CalendarDay> calendarDayList) {
        this.mCalendarDayList = calendarDayList;
    }

    public CalendarDurationSelectedDecorator(Context context) {
        this.mContext = context;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return mCalendarDayList.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan((new ForegroundColorSpan(mContext.getResources().getColor(R.color.black))));
        view.setBackgroundDrawable(generateCircleDrawable(mContext.getResources().getColor(R.color.light_grey)));
    }

    private static Drawable generateCircleDrawable(final int color) {
        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
        drawable.getPaint().setColor(color);
        return drawable;
    }
}



