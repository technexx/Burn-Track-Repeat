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
    int mMode;
    int mPomDot;

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

    public void setMode(int mode) {
        mMode = mode;
    }

    public void pomDraw(int pomDot, int fadeDone) {
        this.mPomDot = pomDot; this.mFadeDone = fadeDone;
        setupPaint();
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        this.mCanvas = canvas;
        mX = 60; mY = 655; mX2 = 60; mY2 = 765;
        if (mMode == 2) {
            mY2 = 695;
        }

        if (mMode != 2 && mMode !=4) {
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
                    fadeDot();
                } else {
                    mPaint.setAlpha(100);
                }
                mCanvas.drawCircle(mX, mY, 30, mPaint);
                mX += 85;
            }
        }

        if (mMode !=4) {
            for (int i=0; i<mBreakCount-mBreakReduce; i++) {
                mPaint.setColor(Color.RED);
                mPaint.setAlpha(255);
                if (mMode !=2) {
                    mCanvas.drawCircle(mX2, mY2, 30, mPaint);
                    mX2 +=85;
                } else {
                    mPaint.setColor(Color.GREEN);
                    mCanvas.drawCircle(mX2, mY2, 45, mPaint);
                    mX2 +=120;
                }
            }

            for (int i=0; i<mBreakReduce; i++) {
                if (mMode == 2) {
                    mPaint.setColor(Color.GREEN);
                } else {
                    mPaint.setColor(Color.RED);
                }
                if (mFadeDone == 3 && i == 0) {
                    fadeDot();
                } else {
                    mPaint.setAlpha(100);
                }
                if (mMode !=2) {
                    mCanvas.drawCircle(mX2, mY2, 30, mPaint);
                    mX2 +=85;
                } else {
                    mCanvas.drawCircle(mX2, mY2, 45, mPaint);
                    mX2 +=120;
                }
            }
        }

        if (mMode == 4) {
            mX = 115; mX2=mX+115;
//            mX = 115; mX2 = mX+200;

            //Fading last object drawn. Setting previous ones to "greyed out"
            for (int i=0; i<mPomDot; i++) {
                if (i == mPomDot-1) {
                    pomColor(i, true);
                } else {
                    mPaint.setAlpha(100);
                    pomColor(i, false);
                }
                if (i+1 == mPomDot) {
                    //Drawing all non-greyed objects.
                    for (int j=mPomDot; j<8; j++) {
                        mPaint.setAlpha(255);
                        pomColor(j, false);
                    }
                }
            }

//            //Drawing all non-greyed objects.
//            for (int i=0; i<8-mPomDot+1; i++) {
//                mPaint.setAlpha(255);
//                pomColor(i, true);
//            }
        }
    }

    public void fadeDot() {
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

    public void pomColor(int i, boolean fade) {
        switch (i+1) {
            case 1: case 3: case 5: case 7:
                mPaint.setColor(Color.GREEN);
                if (fade && mFadeDone == 1) {
                    fadeDot();
                }
                mCanvas.drawCircle(mX, 710, 60, mPaint);
                break;
            case 2: case 4: case 6:
                mPaint.setColor(Color.RED);
                if (fade && mFadeDone == 1) {
                    fadeDot();
                }
                mCanvas.drawCircle(mX2, 710, 30, mPaint);
                mX+=230;
                mX2=mX+115;
                break;
            case 8:
                mPaint.setColor(Color.RED);
                mCanvas.drawRect(mX+110, 655, mX+220, 765, mPaint);
        }
        Log.i("mxLog", "mX is " + mX);
        Log.i("mxLog", "mX2 is " + mX2);
    }
}