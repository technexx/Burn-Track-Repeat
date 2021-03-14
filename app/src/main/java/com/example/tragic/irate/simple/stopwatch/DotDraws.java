package com.example.tragic.irate.simple.stopwatch;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
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
import android.view.MotionEvent;
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
    int mBlankCount;

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
    int mPosX;
    int mPosY;
    boolean mDrawBox;

    public DotDraws(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setWillNotDraw(false);
    }

    private void setupPaint(){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(4);
        mPaintText = new Paint();
        mPaintText.setAntiAlias(true);
    }

    public void selectionPaint() {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(3);
        mPaint.setColor(Color.GRAY);
    }

    public void newDraw(long setCount, long breakCount, long setReduce, long breakReduce, int fadeDone) {
        this.mSetCount = setCount; this.mBreakCount = breakCount; this.mSetReduce = setReduce; this.mBreakReduce = breakReduce; this.mFadeDone = fadeDone;
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

    public void setAlpha() {
        switch (mMode) {
            case 1:
                mAlpha = 255; cycle = 0; break;
            case 2:
                mAlpha2 = 255; cycle2 = 0; break;
        }
    }

    public void retrieveAlpha() {
        mAlpha = savedCustomAlpha;
        cycle = savedCustomCycle;
        mAlpha2 = savedPomAlpha;
        cycle2 = savedPomCycle;
    }

    public void drawBlanks(int blankCount) {
        mBlankCount = blankCount;
        invalidate();
    }

    public void selectCycle(int posX, int posY) {
        mPosX = posX; mPosY = posY;
        mDrawBox = true;
        invalidate();
    }

    public int setBoxCoordinates(int x, int y){
        int pos = 0;
        //Y range is always the same.
        if (y>=750 && y<=1050) {
            if (x>100 && x<=200) pos = 1; if (x>200 && x<=300) pos = 2; if (x>300 && x<=400) pos = 3; if (x>400 && x<=500) pos = 4; if (x>500 && x<=600) pos = 5; if (x>600 && x<=700) pos = 6; if (x>700 && x<=800) pos = 7; if (x>800 && x<=900) pos = 8;if (x>900) pos = 9;
        }

        return pos;
    }

    @Override
    public void onDraw(Canvas canvas) {
        setupPaint();
        this.mCanvas = canvas;
        savedCustomAlpha = mAlpha;
        savedCustomCycle = cycle;
        savedPomAlpha = mAlpha2;
        savedPomCycle = cycle2;

        mX = 60; mY = 490; mX2 = 60; mY2 = 620;
        if (mBreaksOnly) mY2 = 535;

        if (mMode == 1 && !mBreaksOnly) {
            //Todo: Blank counts.
            for (int k=0; k<mBlankCount; k++) {
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(Color.GREEN);
                mCanvas.drawCircle(mX, mY, 45, mPaint);
                mX += 108;
                mPaint.setColor(Color.RED);
                mCanvas.drawCircle(mX2, mY2, 45, mPaint);
                mX2+=108;
            }

            for (int i=0; i<mSetCount; i++) {
                mPaint.setColor(Color.GREEN);
                mPaint.setStyle(Paint.Style.FILL);
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

        if (mDrawBox) {
            selectionPaint();
            int touchedPos = setBoxCoordinates(mPosX, mPosY);
            mCanvas.drawRect((100*touchedPos) +(8*touchedPos) + 10, 425, (100*touchedPos)+ 100 + (8*touchedPos) + 10, 685, mPaint);
            Log.i("touchTest", "pos is " + touchedPos + " and Y is " + mPosY);
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
                if (!mBreaksOnly) {
                    mCanvas.drawCircle(mX2, mY2, 45, mPaint);
                    drawText(mBreakTime, mX2, mY2, i);
                    mX2 += 108;
                } else {
                    mCanvas.drawOval(mX2-40, mY2-70, mX2+48, mY2+75, mPaint);
                    drawText(mBreakTime, mX2+5, mY2, i);
                    mX2+=107;
                }
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

        if (mMode==3) mCanvas.drawColor(Color.BLACK);
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
                mCanvas.drawCircle(mX, 540, 60, mPaint);
                break;
            case 2: case 4: case 6:
                mPaint.setColor(Color.RED);
                if (fade && mFadeDone == 1) {
                    fadeDot2();
                }
                mCanvas.drawCircle(mX2, 540, 30, mPaint);
                mX+=230;
                mX2=mX+115;
                break;
            case 8:
                mPaint.setColor(Color.RED);
                if (fade && mFadeDone == 1) {
                    fadeDot2();
                }
                mCanvas.drawRect(mX+110, 485, mX+220, 595, mPaint);
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