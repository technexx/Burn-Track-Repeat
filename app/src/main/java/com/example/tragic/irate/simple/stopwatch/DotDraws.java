package com.example.tragic.irate.simple.stopwatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

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
    boolean mReducing;
    long mSetReduce;
    long mBreakReduce;

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

    public void newDraw(long setCount, long breakCount, long setReduce, long breakReduce) {
        this.mSetCount = setCount; this.mBreakCount = breakCount; this.mSetReduce = setReduce; this.mBreakReduce = breakReduce;
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

        for (int i=0; i < mSetCount-mSetReduce; i++) {
            mPaint.setColor(Color.GREEN);
            canvas.drawCircle(mX, mY, 30, mPaint);
            mX += 85;
        }
        for (int i=0; i < mSetReduce; i++) {
            mPaint.setAlpha(50);
            canvas.drawCircle(mX, mY, 30, mPaint);
            mX += 85;
        }

        for (int i=0; i<mBreakCount-mBreakReduce; i++) {
            mPaint.setColor(Color.RED);
            canvas.drawCircle(mX2, mY2, 30, mPaint);
            mX2 +=85;
        }
        for (int i=0; i<mBreakReduce; i++) {
            mPaint.setAlpha(50);
            canvas.drawCircle(mX2, mY2, 30, mPaint);
            mX2 +=85;
        }
    }
}