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
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import java.lang.reflect.Type;
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
    ArrayList<String> mPomTime;
    boolean mBreaksOnly;

    int savedCustomAlpha;
    int savedCustomCycle;
    int savedPomAlpha;
    int savedPomCycle;
    int mPosX;
    int mPosY;
    boolean mDrawBox;
    int mListSize;
    int currentPos = -1;
    int previousPos = -1;
    sendPosition mSendPosition;
    sendAlpha mSendAlpha;

    public interface sendPosition {
        void sendPos(int pos);
    }

    public interface sendAlpha {
        void sendAlphaValue(int alpha);
    }

    public void onPositionSelect(sendPosition xSendPosition) {
        this.mSendPosition = xSendPosition;
    }

    public void onAlphaSend(sendAlpha xSendAlpha) {
        this.mSendAlpha = xSendAlpha;
    }

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

    public void pomDraw(int pomDot, int fadeDone, ArrayList<Long> pomTime) {
        mPomTime = new ArrayList<>();
        for (int i=0; i<pomTime.size(); i++) {
            mPomTime.add(String.valueOf(pomTime.get(i)));
        }
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

    public void selectCycle(int posX, int posY, int size) {
        mPosX = posX; mPosY = posY; mListSize = size;
        if (posY >= 810 && posY <= 1000) {
            if (posX <= 110) currentPos = 0;
            if (posX > 110 && posX <= 220 && mListSize >= 2) currentPos = 1;
            if (posX > 220 && posX <= 330 && mListSize >= 3) currentPos = 2;
            if (posX > 330 && posX <= 440 && mListSize >= 4) currentPos = 3;
            if (posX > 440 && posX <= 550 && mListSize >= 5) currentPos = 4;
            if (posX > 550 && posX <= 660 && mListSize >= 6) currentPos = 5;
            if (posX > 660 && posX <= 770 && mListSize >= 7) currentPos = 6;
            if (posX > 770 && posX <= 880 && mListSize >= 8) currentPos = 7;
            if (posX > 880 && posX <= 990 && mListSize >= 9) currentPos = 8;
            if (posX > 990 && mListSize >= 10) currentPos = 9;
            Log.i("findpos", String.valueOf(posX));
            if (previousPos != currentPos) {
                mDrawBox = true;
            }
            else {
                mDrawBox = false;
                currentPos = -1;
            }
            previousPos = -1;
            mSendPosition.sendPos(currentPos);
        } else {
            currentPos = -1;
            mSendPosition.sendPos(currentPos);
        }

        invalidate();
    }

    public void setCycle(int pos) {
        mDrawBox = true;
        currentPos = pos;
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
            mPaint.setStyle(Paint.Style.FILL);
            for (int i=0; i<mSetCount; i++) {
                mPaint.setColor(Color.GREEN);
                if (mSetCount - mSetReduce == i) {
                    if (mFadeDone == 1) {
                        fadeDot();
                    }
                } else if (mSetReduce + i < mSetCount) {
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
            if (mListSize>0 && currentPos>=0) {
                mCanvas.drawRect((100*currentPos) +(8*currentPos) + 10, 425, (100*currentPos)+ 100 + (8*currentPos) + 10, 685, mPaint);
                previousPos = currentPos;
                currentPos = -1;
            }
            mPaint.setStyle(Paint.Style.FILL);
        }

        if (mMode == 1) {
            mPaint.setStyle(Paint.Style.FILL);
            for (int i=0; i<mBreakCount; i++) {
                if (mMode == 2) {
                    mPaint.setColor(Color.GREEN);
                } else {
                    mPaint.setColor(Color.RED);
                }
                if (mBreakCount - mBreakReduce == i) {
                    if (mFadeDone == 2) {
                        fadeDot();
                    }
                } else if (mBreakReduce + i <  mBreakCount) {
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
                    mCanvas.drawRoundRect(mX2-40, mY2-75, mX2+48, mY2+80, 45, 45, mPaint);
                    drawText(mBreakTime, mX2+5, mY2, i);
                    mX2+=107;
                }
            }
        }

        if (mMode == 2) {
            mX = 92; mX2=mX+125;
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
        mSendAlpha.sendAlphaValue(mAlpha);
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
        switch (i) {
            case 0: case 2: case 4: case 6:
                mPaint.setColor(Color.GREEN);
                if (fade && mFadeDone == 1) {
                    fadeDot2();
                }
                mCanvas.drawCircle(mX, 610, 60, mPaint);
                if (mPomTime.size()!=0) drawText(mPomTime, mX, mY, i);
                break;
            case 1: case 3: case 5:
                mPaint.setColor(Color.RED);
                if (fade && mFadeDone == 1) {
                    fadeDot2();
                }
                mCanvas.drawCircle(mX2, 610, 45 , mPaint);
                if (mPomTime.size()!=0) drawText(mPomTime, mX2, mY, i);
                mX+=250;
                mX2=mX+125;
                break;
            case 7:
                mPaint.setColor(Color.RED);
                if (fade && mFadeDone == 1) {
                    fadeDot2();
                }
                mCanvas.drawRect(mX+90, 555, mX+200, 665, mPaint);
                if (mPomTime.size()!=0) drawText(mPomTime, mX2, mY, i);
        }
        Log.i("pomtest", "size in pomDraw is " + mPomTime.size());

    }

    private void drawText(ArrayList<String> list, float x, float y, int i) {
        if (mMode == 1) {
            mPaintText.setColor(Color.BLACK);
            if (!mBreaksOnly)  {
                mPaintText.setTypeface(Typeface.DEFAULT);
            } else {
                Typeface tf = ResourcesCompat.getFont(getContext(), R.font.archivo_narrow);
                mPaintText.setTypeface(tf);
            }
            if (list.size() >0) {
                if (list.get(i).length() <= 2) {
                    if (list.get(i).length() == 1) {
                        //Adds "0" to any single digit.
                        String temp = list.get(i);
                        temp = "0" + temp;
                        list.set(i, temp);
                    }
                    if (!mBreaksOnly) {
                        mPaintText.setTextSize(60f);
                        mCanvas.drawText(list.get(i), x-37, y+22, mPaintText);
                    } else {
                        mPaintText.setTextSize(90f);
                        mCanvas.drawText(list.get(i), x-44, y+28, mPaintText);
                    }
                } else {
                    if (!mBreaksOnly) {
                        mPaintText.setTextSize(45f);
                        mCanvas.drawText(list.get(i), x-44, y+17, mPaintText);
                    } else {
                        Typeface tf = ResourcesCompat.getFont(getContext(), R.font.archivo_narrow_bold);
                        mPaintText.setTypeface(tf);
                        mPaintText.setTextSize(52f);
                        mCanvas.drawText(list.get(i), x-45, y+17, mPaintText);
                    }
                }
            }
        } else if (mMode==2) {
            switch (i) {
                case 0: case 2: case 4: case 6:
                    mPaintText.setTextSize(70f);
                    mCanvas.drawText(list.get(i), x-40, y+147, mPaintText);
                    break;
                case 1: case 3: case 5:
                    mPaintText.setTextSize(45f);
                    mCanvas.drawText(list.get(i), x-12, y+135, mPaintText);
                    break;
                case 7:
//                    mPaintText.setTextSize(70f);
                    mCanvas.drawText(list.get(i), x-23, y+147, mPaintText);
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