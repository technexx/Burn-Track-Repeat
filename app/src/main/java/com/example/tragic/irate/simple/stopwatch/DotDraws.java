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

import java.text.DecimalFormat;
import java.util.ArrayList;

public class DotDraws extends View {
    Canvas mCanvas;
    Paint mPaint;
    Paint mPaintText;
    float mX;
    float mX2;
    float mY;
    float mY2;
    long mSetCount;
    long mBreakCount;
    long mSetReduce;
    long mBreakReduce;

    int mAlpha = 255;
    int mAlpha2 = 255;
    int cycle;
    int cycle2;
    int mFadeDone;
    int mMode;
    int mPomDot;
    ArrayList<String> mSetTime;
    ArrayList<String> mBreakTime;
    boolean mBreaksOnly;

    int savedCustomAlpha;
    int savedCustomCycle;
    int savedPomAlpha;
    int savedPomCycle;

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
        mPaintText = new Paint();
        mPaintText.setAntiAlias(true);
        mPaintText.setStrokeWidth(5);
    }

    public void newDraw(long setCount, long breakCount, long setReduce, long breakReduce, int fadeDone) {
        this.mSetCount = setCount; this.mBreakCount = breakCount; this.mSetReduce = setReduce; this.mBreakReduce = breakReduce; this.mFadeDone = fadeDone;
        setupPaint();
        invalidate();
    }

    public void setMode(int mode) {
        mMode = mode;
    }

    public void setTime(ArrayList<Long> setTime) {
        mSetTime = new ArrayList<>();
        for (int i=0; i<setTime.size(); i++) {
            mSetTime.add(convertSeconds(setTime.get(i)/1000));
        }
    }

    public void breakTime(ArrayList<Long> breakTime) {
        mBreakTime = new ArrayList<>();
        for (int i=0; i<breakTime.size(); i++) {
            mBreakTime.add(convertSeconds(breakTime.get(i)/1000));
        }
    }

    public void pomDraw(int pomDot, int fadeDone) {
        this.mPomDot = pomDot; this.mFadeDone = fadeDone;
        setupPaint();
        invalidate();
    }

    public void breaksOnly(boolean breaksOnly) {
        this.mBreaksOnly = breaksOnly;
    }

    public void retrieveAlpha() {
        mAlpha = savedCustomAlpha;
        cycle = savedCustomCycle;
        mAlpha2 = savedPomAlpha;
        cycle2 = savedPomCycle;
    }

    @Override
    public void onDraw(Canvas canvas) {
        this.mCanvas = canvas;
        savedCustomAlpha = mAlpha;
        savedCustomCycle = cycle;
        savedPomAlpha = mAlpha2;
        savedPomCycle = cycle2;

        mX = 60; mY = 585; mX2 = 60; mY2 = 715;
        if (mBreaksOnly) {
            mX2 = 60; mY2 = 645;
        }

        if (mMode == 1 && !mBreaksOnly) {
            for (int i=0; i < mSetCount; i++) {
                mPaint.setColor(Color.GREEN);
                if (mSetReduce + i == mSetCount) {
                    if (mFadeDone == 1) {
                        fadeDot();
                    }
                } else if (mSetReduce + i >= mSetCount) {
                    mPaint.setAlpha(100);
                } else {
                    mPaint.setAlpha(255);
                }
                mCanvas.drawCircle(mX, mY, 45, mPaint);
                drawText(mSetTime, mX, mY, i);
                mX += 108;
            }
        }

        if (mMode == 1) {
            for (int i=0; i<mBreakCount; i++) {
                if (mMode == 2) {
                    mPaint.setColor(Color.GREEN);
                } else {
                    mPaint.setColor(Color.RED);
                }
                if (mBreakReduce + i == mBreakCount) {
                    if (mFadeDone == 2) {
                        fadeDot();
                    }
                } else if (mBreakReduce +i >= mBreakCount) {
                    mPaint.setAlpha(100);
                }
                else {
                    mPaint.setAlpha(255);
                }
                mCanvas.drawCircle(mX2, mY2, 45, mPaint);
                drawText(mBreakTime, mX2, mY2, i);
                mX2 += 108;
            }
        }

        if (mMode == 2) {
            mX = 115; mX2=mX+115;
            //Fading last object drawn. Setting previous ones to "greyed out"
            for (int i=0; i<mPomDot; i++) {
                if (i == mPomDot-1) {
                    pomDraw(i, true);
                } else {
                    mPaint.setAlpha(100);
                    pomDraw(i, false);
                }
                if (i+1 == mPomDot) {
                    //Drawing all non-greyed objects.
                    for (int j=mPomDot; j<8; j++) {
                        mPaint.setAlpha(255);
                        pomDraw(j, false);
                    }
                }
            }
        }
    }

    public void fadeDot() {
        if (mAlpha >255) mAlpha = 255;
        mPaint.setAlpha(mAlpha);
        cycle++;
        if (cycle <10) {
            mAlpha -=25;
        } else {
            mAlpha +=25;
            if (cycle >19) cycle = 0;
        }
        savedCustomAlpha = mAlpha;
        savedCustomCycle = cycle;
    }

    public void fadeDot2() {
        if (mAlpha2 >255) mAlpha2 = 255;
        mPaint.setAlpha(mAlpha2);
        cycle2++;
        if (cycle2 <10) {
            mAlpha2 -=25;
        } else {
            mAlpha2 +=25;
            if (cycle2 >19) cycle2 = 0;
        }
        savedPomAlpha = mAlpha2;
        savedPomCycle = cycle2;
    }

    public void pomDraw(int i, boolean fade) {
        switch (i+1) {
            case 1: case 3: case 5: case 7:
                mPaint.setColor(Color.GREEN);
                if (fade && mFadeDone == 1) {
                    fadeDot2();
                }
                mCanvas.drawCircle(mX, 650, 60, mPaint);
                break;
            case 2: case 4: case 6:
                mPaint.setColor(Color.RED);
                if (fade && mFadeDone == 1) {
                    fadeDot2();
                }
                mCanvas.drawCircle(mX2, 650, 30, mPaint);
                mX+=230;
                mX2=mX+115;
                break;
            case 8:
                mPaint.setColor(Color.RED);
                if (fade && mFadeDone == 1) {
                    fadeDot2();
                }
                mCanvas.drawRect(mX+110, 595, mX+220, 705, mPaint);
        }
    }

    private void drawText(ArrayList<String> list, float x, float y, int i) {
        if (mMode == 1) {
            mPaintText.setColor(Color.BLACK);
            if (list.size() >0) {
                if (list.get(i).length() <= 2) {
                    if (list.get(i).length() == 1) {
                        list.set(i, "05");
                    }
                    mPaintText.setTextSize(60f);
                    mCanvas.drawText(list.get(i), x-37, y+22, mPaintText);
                } else {
                    mPaintText.setTextSize(45f);
                    mCanvas.drawText(list.get(i), x-44, y+17, mPaintText);
                }
            }
        }
    }

    public String convertSeconds(long totalSeconds) {
        DecimalFormat df = new DecimalFormat("00");
        long minutes;
        long remainingSeconds;

        if (totalSeconds >=60) {
            minutes = totalSeconds/60;
            remainingSeconds = totalSeconds % 60;
            return (minutes + ":" + df.format(remainingSeconds));
        } else if (totalSeconds != 5) return String.valueOf(totalSeconds);
        else return "5";
    }
}