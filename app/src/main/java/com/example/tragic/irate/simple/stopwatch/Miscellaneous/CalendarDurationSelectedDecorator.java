package com.example.tragic.irate.simple.stopwatch.Miscellaneous;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import com.example.tragic.irate.simple.stopwatch.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.List;

public class CalendarDurationSelectedDecorator implements DayViewDecorator {
    Context mContext;
    List<CalendarDay> mCalendarDayList;

    Drawable circleDrawable;
    Animation decorationAnimation;

    public void setCalendarDayList(List<CalendarDay> calendarDayList) {
        this.mCalendarDayList = calendarDayList;
    }

    public CalendarDurationSelectedDecorator(Context context) {
        this.mContext = context;
        instantiateAnimations();
        circleDrawable = generateCircleDrawable(mContext.getResources().getColor(R.color.light_grey));
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return mCalendarDayList.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan((new ForegroundColorSpan(mContext.getResources().getColor(R.color.black))));
        view.addSpan(new BackgroundColorSpan(mContext.getResources().getColor(R.color.blue)));
//        view.setBackgroundDrawable(circleDrawable);
    }

    private static Drawable generateCircleDrawable(final int color) {
        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
        drawable.getPaint().setColor(color);
        return drawable;
    }

    private void instantiateAnimations() {
        decorationAnimation = AnimationUtils.loadAnimation(mContext, R.anim.fade_in_anim);
        decorationAnimation.setDuration(200);
        decorationAnimation.setFillAfter(true);
    }
}