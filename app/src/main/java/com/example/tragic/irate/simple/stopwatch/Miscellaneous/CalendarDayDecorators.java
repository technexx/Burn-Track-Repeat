package com.example.tragic.irate.simple.stopwatch.Miscellaneous;

import android.content.Context;
import android.text.style.ForegroundColorSpan;

import androidx.appcompat.content.res.AppCompatResources;

import com.example.tragic.irate.simple.stopwatch.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CalendarDayDecorators {

    public static class DaySelectedDecoration implements DayViewDecorator {

        Context mContext;
        List<CalendarDay> mCurrentDayList;

        public DaySelectedDecoration(Context context) {
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
            dayViewFacade.setBackgroundDrawable(Objects.requireNonNull(AppCompatResources.getDrawable(mContext, R.drawable.current_calendar_day_circle)));
        }
    }

    public static class ActivityDecoration implements DayViewDecorator {
        Context mContext;
        List<CalendarDay> mCalendarDayListWithAtLeastOneActivity;

        public ActivityDecoration(Context context) {
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

    public class FoodDecoration implements DayViewDecorator {

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return false;
        }

        @Override
        public void decorate(DayViewFacade view) {

        }
    }


}
