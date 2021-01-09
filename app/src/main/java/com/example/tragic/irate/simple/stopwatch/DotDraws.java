package com.example.tragic.irate.simple.stopwatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DotDraws extends View {
    Canvas mCanvas;
    Paint mPaint;
    float mX;
    float mX2;
    float mY;
    float mY2;
    long mSetCount;
    long mBreakCount;
    long mSetReduce;
    long mBreakReduce;

    int alpha = 255;
    int cycle;
    int mFadeDone;

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

    public void newDraw(long setCount, long breakCount, long setReduce, long breakReduce, int fadeDone) {
        this.mSetCount = setCount; this.mBreakCount = breakCount; this.mSetReduce = setReduce; this.mBreakReduce = breakReduce; this.mFadeDone = fadeDone;
        setupPaint();
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        this.mCanvas = canvas;
        mX = 80; mY = 760; mX2 = 80; mY2 = 870;

        for (int i=0; i<mSetCount-mSetReduce; i++) {
            mPaint.setColor(Color.GREEN);
            mPaint.setAlpha(255);
            mCanvas.drawCircle(mX, mY, 30, mPaint);
            mX += 85;
        }

        for (int i=0; i<mSetReduce; i++) {
            //Paint MUST be set here as well, since first loop does not execute w/ 1 set left.
            mPaint.setColor(Color.GREEN);
            if (mFadeDone == 1 && i==0) {
                fadeDot(mX, mY);
            } else {
                mPaint.setAlpha(100);
            }
            mCanvas.drawCircle(mX, mY, 30, mPaint);
            mX += 85;
        }

        for (int i=0; i<mBreakCount-mBreakReduce; i++) {
            mPaint.setColor(Color.RED);
            mPaint.setAlpha(255);
            mCanvas.drawCircle(mX2, mY2, 30, mPaint);
            mX2 +=85;
        }

        for (int i=0; i<mBreakReduce; i++) {
            mPaint.setColor(Color.RED);
            if (mFadeDone == 3 && i == 0) {
                fadeDot(mX2, mY2);
            } else {
                mPaint.setAlpha(100);
            }
            mCanvas.drawCircle(mX2, mY2, 30, mPaint);
            mX2+=85;
        }
    }

    public void fadeDot(float x, float y) {
        if (alpha >255) {
            alpha = 255;
        }
        mPaint.setAlpha(alpha);
        cycle++;
        if (cycle <10) {
            alpha -=25;
        } else {
            alpha +=25;
            if (cycle >19) {
                cycle = 0;
            }
        }
    }
}