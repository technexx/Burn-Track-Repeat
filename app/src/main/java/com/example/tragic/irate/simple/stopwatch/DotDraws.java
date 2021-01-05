package com.example.tragic.irate.simple.stopwatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.annotation.Nullable;

public class DotDraws extends View {
    Paint mPaint;
    Animation anim;
    float mX;
    float mX2;
    float mY;
    float mY2;
    long mSetCount;
    long mBreakCount;

    public DotDraws(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setWillNotDraw(false);
    }

    private void setupPaint(){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void newDraw(long setCount, long breakCount) {
        this.mSetCount = setCount; this.mBreakCount = breakCount;
        setupPaint();
        invalidate();

        anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(300);
        anim.setStartOffset(20);
    }


    @Override
    public void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        mX = 80; mY = 600; mX2 = 80; mY2 = 710;
        for (int i=0; i<mSetCount; i++) {
            mPaint.setColor(Color.GREEN);
            canvas.drawCircle(mX, mY, 30, mPaint);
            mX+=85;
        }
        for (int i=0; i<mBreakCount; i++) {
            mPaint.setColor(Color.RED);
            canvas.drawCircle(mX2, mY2, 30, mPaint);
            mX2 +=85;
        }
    }
}