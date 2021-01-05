package com.example.tragic.irate.simple.stopwatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class DotDraws extends View {
    Paint mPaint;
    float mX;
    float mX2;
    float mY;
    float mY2;
    int mColor;
    int mSetCount;
    int mBreakCount;

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

    public void newDraw(float x, float y, int setCount, int breakCount) {
        this.mX = x; this.mY = y; this.mSetCount = setCount; this.mBreakCount = breakCount;
        setupPaint();
        invalidate();
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