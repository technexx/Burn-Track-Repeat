package com.example.tragic.irate.simple.stopwatch;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Handler;

public class DotDraws extends View {
    Canvas mCanvas;
    Paint mPaint;
    Paint mPaint2;
    Paint mPaint3;
    Paint mPaintBox;
    Paint mPaintText;
    float mX;
    float mX2;
    float mY;
    float mY2;
    long mSetCount;
    long mBreakCount;
    long mSetReduce;
    long mBreakReduce;
    long mBreakOnlyCount;
    long mBreakOnlyReduce;

    int mAlpha = 255;
    int mAlpha2;
    int cycle;
    int mFadeDone;
    int mMode;
    int mPomDotCounter;
    ArrayList<String> mSetTime;
    ArrayList<String> mBreakTime;
    ArrayList<String> mBreakOnlyTime;
    ArrayList<String> mPomTime;

    int savedCustomAlpha;
    int savedCustomCycle;
    int mPosX;
    int mPosY;
    boolean mDrawBox;
    int mListSize;
    int currentPos = -1;
    int previousPos = -1;
    int fadePos;
    sendPosition mSendPosition;
    sendAlpha mSendAlpha;
    int mOldMode;
    boolean mGoingUpSets;
    boolean mGoingUpBreaks;
    boolean mAddSubFade;
    boolean mFadingIn = true;

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

        mPaint2 = new Paint();
        mPaint2.setAntiAlias(true);
        mPaint2.setStyle(Paint.Style.FILL);
        mPaint2.setStrokeWidth(4);

        mPaint3 = new Paint();
        mPaint3.setAntiAlias(true);
        mPaint3.setStyle(Paint.Style.FILL);
        mPaint3.setStrokeWidth(4);

        mPaintText = new Paint();
        mPaintText.setAntiAlias(true);

        mPaintBox = new Paint();
        mPaintBox.setColor(Color.WHITE);
        mPaintBox.setStyle(Paint.Style.STROKE);
        mPaintBox.setStrokeWidth(6);
    }

    //Called from Main and determines whether we are counting up or down.
    public void countingUpSets(boolean goingUpSets) {
        mGoingUpSets = goingUpSets;
    }

    //Called from Main and determines whether we are counting up or down.
    public void countingUpBreaks(boolean goingUpBreaks) {
        mGoingUpBreaks = goingUpBreaks;
    }

    public void customDrawSet(long setCount, long setReduce, int fadeDone) {
        this.mSetCount = setCount; this.mSetReduce = setReduce; this.mFadeDone = fadeDone;
        invalidate();
    }

    public void customDrawBreak(long breakCount, long breakReduce) {
        this.mBreakCount = breakCount; this.mBreakReduce = breakReduce;
        invalidate();
    }

    public void breaksOnlyDraw(long breakOnlyCount, long breakOnlyReduce, int fadeDone) {
        this.mBreakOnlyCount = breakOnlyCount; this.mBreakOnlyReduce = breakOnlyReduce; this.mFadeDone = fadeDone;
        invalidate();
    }

    public void pomDraw(int pomDotCounter, ArrayList<Long> pomTime, int fadeDone) {
        mPomTime = new ArrayList<>();
        for (int i=0; i<pomTime.size(); i++) {
            mPomTime.add(String.valueOf(pomTime.get(i)));
        }
        this.mPomDotCounter = pomDotCounter; this.mFadeDone = fadeDone;
        setupPaint();
        invalidate();
    }

    public void setMode(int mode) {
        mOldMode = mMode;
        mMode = mode;
        if (mMode==4) invalidate();
    }

    //Updates list every time it is called w/ a String conversion of our long millis value.
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

    public void breakOnlyTime(ArrayList<Long> breakOnlyTime) {
        mBreakOnlyTime = new ArrayList<>();
        for (int i=0; i<breakOnlyTime.size(); i++) {
            mBreakOnlyTime.add(convertSeconds(breakOnlyTime.get(i)/1000));
        }
    }

    public void setAlpha() {
        mAlpha = 255; cycle = 0;
    }

    public void setFadePos(int pos) {
        fadePos = pos;
    }

    public void selectCycle(int posX, int posY, int size) {
        int base = 0;
        switch (mMode) {
            case 1:
                base = 140; break;
            case 2:
                base = 132; break;
        }
        mPosX = posX; mPosY = posY; mListSize = size;
        if (posY >= 750 && posY <= 1050) {
            if (posX <= base) currentPos = 0;
            else if (posX > base && posX <= base*2 && mListSize >= 2) currentPos = 1;
            else if (posX > base*2 && posX <= base*3 && mListSize >= 3) currentPos = 2;
            else if (posX > base*3 && posX <= base*4 && mListSize >= 4) currentPos = 3;
            else if (posX > base*4 && posX <= base*5 && mListSize >= 5) currentPos = 4;
            else if (posX > base*5 && posX <= base*6 && mListSize >= 6) currentPos = 5;
            else if (posX > base*6 && posX <= base*7 && mListSize >= 7) currentPos = 6;
            else if (posX > base*7 && posX <= base*8 && mListSize >= 8) currentPos = 7;
            //Used to reference the entire box.
            else currentPos = 100;
            //Only setting a fadePos if we are clicking with the box.
            fadePos = currentPos;
            //Only drawing new box if selected position (box) is different than current.
            if (previousPos != currentPos && currentPos!=100) mDrawBox = true; else mDrawBox = false;
            previousPos = -1;
            mSendPosition.sendPos(currentPos);
        } else {
            currentPos = -1;
            mSendPosition.sendPos(currentPos);
        }
        invalidate();
    }

    public void selectRound(int pos) {
        mDrawBox = true;
        currentPos = pos;
    }

    public void encloseDots(float topY, float botY) {
        mPaintBox.setStyle(Paint.Style.STROKE);
        mPaintBox.setAlpha(175);
        mCanvas.drawRoundRect(3, topY, 1078, botY, 20, 20, mPaintBox);
        mPaintBox.setStyle(Paint.Style.FILL);
        mPaintBox.setAlpha(35);
        mCanvas.drawRoundRect(3, topY, 1078, botY, 20, 20, mPaintBox);
    }

    //Used w/ mGoingUpSets and mGoingUpBreaks w/ in Canvas to change dot/dot text based on count up/ count down mode.
    public void setDotStyle(boolean countingUp) {
        if (countingUp) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaintText.setColor(Color.WHITE);
        } else {
            mPaint.setStyle(Paint.Style.FILL);
            mPaintText.setColor(Color.BLACK);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        setupPaint();
        this.mCanvas = canvas;

        Log.i("testFade", "fadePos is " + fadePos);

        //If fadePos returns 100 (no round selected), set it to most recently added round position.
        if (fadePos==100) {
            fadePos = mSetTime.size()-1;
        }

        mX = 58; mY = 490; mX2 = 58; mY2 = 620;
        switch (mMode) {
            case 1:
                encloseDots(mY-70, mY+200);
                //Filled or stroked dots depending on count up/down.
                setDotStyle(mGoingUpSets);
                //Using mSetTime array size for loop instead of int constructor value.
                for (int i=0; i<mSetTime.size(); i++) {
                    mPaint.setColor(Color.GREEN);
                    if (mSetTime.size() - mSetReduce == i) {
                        if (mFadeDone == 1) fadeDot();
                    } else if (mSetReduce + i < mSetTime.size()) {
                        mPaint.setAlpha(100);
                        //Todo: "OR" conditional keeps this executing every time.
                        //If fading out (subtracting rounds), cycle the alpha value on the received position, and set it to 255 on the rest.
                    } else if (!mFadingIn && (fadePos != 100 || fadePos != (mSetTime.size() - 1))) {
                        if (i != fadePos) mPaint.setAlpha(255);
                        else if (mAddSubFade) {
                            mPaint.setAlpha(mAlpha2);
                        }
                        //If fading out WITHOUT a round selected, set the position to be faded out on the newly added round/position, and set it to 255 on the rest.
                    } else {
                        if (mSetTime.size() - 1 != i) mPaint.setAlpha(255);
                        else if (mAddSubFade) {
                            Log.i("testFade", "true!");
                            mPaint.setAlpha(mAlpha2);
                        }
                    }
                    mCanvas.drawCircle(mX + 20, mY, 55, mPaint);
                    drawText(mSetTime, mX + 16, mY + 2, i);

                    mX += 132;
                }

                for (int i=0; i<mBreakTime.size(); i++) {
                    mPaint.setColor(Color.RED);
                    if (mGoingUpBreaks) setDotStyle(true); else setDotStyle(false);
                    if (mBreakTime.size() - mBreakReduce == i) {
                        if (mFadeDone == 2) fadeDot();
                    } else if (mBreakReduce + i <  mBreakTime.size()) {
                        mPaint.setAlpha(100);
                    } else if (mBreakTime.size()-1!=i) mPaint.setAlpha(255); else if (mAddSubFade && mFadingIn) {
                        mPaint.setAlpha(mAlpha2);
                        mPaintText.setAlpha(mAlpha2);
                    }
                    mCanvas.drawCircle(mX2+20, mY2, 55, mPaint);
                    drawText(mBreakTime, mX2+16, mY2+2, i);
                    mX2 += 132;

                    //Animation to remove a dot, cycling alpha values by using a post-delayed runnable from Main.
                    if (mAddSubFade && !mFadingIn && (mBreakTime.size()-1==i)) {
                        //At end of fade, passing in a -1 alpha value which we use to avoid drawing the dot (as it has been removed).
                        if (mAlpha2!=-1) {
                            mPaint.setAlpha(mAlpha2);
                            mPaintText.setAlpha(mAlpha2);
                            drawText(mBreakTime, mX2+16, mY2+2, i);
                            mCanvas.drawCircle(mX2+20, mY2, 55, mPaint);
                        }
                    }
                }
                break;
            case 2:
                mX2 = 15;
                encloseDots(mY-30, mY+160);
                if (mGoingUpBreaks) setDotStyle(true); else setDotStyle(false);

                for (int i=0; i<mBreakOnlyCount; i++) {
                    mPaint.setColor(Color.RED);
                    if (mBreakOnlyCount - mBreakOnlyReduce == i) {
                        if (mFadeDone == 3) fadeDot();
                    } else if (mBreakOnlyReduce + i <  mBreakOnlyCount) {
                        mPaint.setAlpha(100);
                    } else if (mBreakOnlyTime.size()-1!=i) mPaint.setAlpha(255); else if (mAddSubFade && mFadingIn) {
                        mPaint.setAlpha(mAlpha2);
                        mPaintText.setAlpha(mAlpha2);
                    };
                    mCanvas.drawRoundRect(mX2+7, mY2-130, mX2+115, mY2+5, 100, 100, mPaint);
                    drawText(mBreakOnlyTime, mX2+60, mY2-60, i);
                    mX2 += 132;

                    //Animation to remove a dot, cycling alpha values by using a post-delayed runnable from Main.
                    if (mAddSubFade && !mFadingIn && (mBreakOnlyTime.size()-1==i)) {
                        //At end of fade, passing in a -1 alpha value which we use to avoid drawing the dot (as it has been removed).
                        if (mAlpha2!=-1) {
                            mPaint.setAlpha(mAlpha2);
                            mPaintText.setAlpha(mAlpha2);
                            drawText(mBreakOnlyTime, mX2+60, mY2-60, i);
                            mCanvas.drawRoundRect(mX2+7, mY2-130, mX2+115, mY2+5, 100, 100, mPaint);
                        }
                    }
                }
                break;
            case 3:
                mX = 92; mX2=mX+125;
                encloseDots(mY-30, mY+150);
                //Fading last object drawn. Setting previous ones to "greyed out"
                for (int i=0; i<8; i++) {
                    if (mPomDotCounter - 1 == i) pomFill(i, true, 255);
                    else if (i - mPomDotCounter <=-2) pomFill(i, false, 100); else pomFill(i, false, 255);
                }
                break;
        }

        if (mDrawBox) {
            //Setting to false so that the selection box is always removed after a deletion.
            mDrawBox = false;
            int start = 0;
            int end = 0;
            int top = 0;
            int bottom = 0;
            switch (mMode) {
                case 1:
                    start = 10 + (currentPos*133); end = 145 + (currentPos*132); top = 425; bottom = 685;
                    if (currentPos>3) {
                        start = start - (currentPos/2);
                        end = end - (currentPos/2);
                    }
                    break;
                case 2:
                    start = 10 + (currentPos*135); end = 140 + (currentPos*135); top = 460; bottom = 650;
                    if (currentPos>1) {
                        start = start - (currentPos*3);
                        end = end - (currentPos*3);
                    }
                    break;
            }
            mPaintBox.setColor(Color.WHITE);
            mPaintBox.setStyle(Paint.Style.STROKE);
            mPaintBox.setStrokeWidth(8);
            if (mListSize>0 && currentPos>=0) {
                mCanvas.drawRect(start, top, end, bottom, mPaintBox);
                previousPos = currentPos;
                currentPos = -1;
            }
        }

        if (mMode==4) mCanvas.drawColor(Color.BLACK);
    }

    public void pomFill(int i, boolean fade, int alpha) {
        switch (i) {
            case 0: case 2: case 4: case 6:
                mPaint.setColor(Color.GREEN);
                //Must be called AFTER color is changed, otherwise alpha will reset to 255.
                if (fade && mFadeDone == 4) fadeDot(); else mPaint.setAlpha(alpha);
                if (mAddSubFade) mPaintText.setAlpha(mAlpha2);
                mCanvas.drawCircle(mX, 550, 60, mPaint);
                if (mPomTime.size()!=0) drawText(mPomTime, mX, mY, i);
                break;
            case 1: case 3: case 5:
                mPaint.setColor(Color.RED);
                if (fade && mFadeDone == 4) fadeDot(); else mPaint.setAlpha(alpha);
                mCanvas.drawCircle(mX2, 550, 45 , mPaint);
                if (mAddSubFade) mPaintText.setAlpha(mAlpha2);
                if (mPomTime.size()!=0) drawText(mPomTime, mX2, mY, i);
                mX+=250;
                mX2=mX+125;
                break;
            case 7:
                mPaint.setColor(Color.RED);
                if (fade && mFadeDone == 4) fadeDot(); else mPaint.setAlpha(alpha);
                mCanvas.drawRect(mX+90, 495, mX+200, 605, mPaint);
                if (mAddSubFade) mPaintText.setAlpha(mAlpha2);
                if (mPomTime.size()!=0) drawText(mPomTime, mX2, mY, i);
                break;
        }
    }

    private void drawText(ArrayList<String> list, float x, float y, int i) {
        Typeface narrow = ResourcesCompat.getFont(getContext(), R.font.archivo_narrow);

        if (mMode == 1 || mMode == 2) {
            if (list.size() >0) {
                if (list.get(i).length() <= 2) {
                    if (list.get(i).length() == 1) {
                        //Adds "0" to any single digit.
                        String temp = list.get(i);
                        temp = "0" + temp;
                        list.set(i, temp);
                    }

                    mPaintText.setTypeface(Typeface.DEFAULT);
                    if (mMode==1) {
                        mPaintText.setTextSize(70f);
                        mCanvas.drawText(list.get(i), x-37, y+22, mPaintText);
                    } else if (mMode==2){
                        mPaintText.setTextSize(90f);
                        mCanvas.drawText(list.get(i), x-52, y+28, mPaintText);
                    }
                } else {
                    mPaintText.setTypeface(narrow);
                    if (mMode==1) {
                        mPaintText.setTextSize(58f);
                        mCanvas.drawText(list.get(i), x-43, y+17, mPaintText);
                    } else if (mMode==2) {
                        mPaintText.setTextSize(65f);
                        mCanvas.drawText(list.get(i), x-51, y+17, mPaintText);
                    }
                }
            }
        } else if (mMode==3) {
            switch (i) {
                case 0: case 2: case 4: case 6:
                    mPaintText.setTextSize(70f);
                    mCanvas.drawText(list.get(i), x-40, y+87, mPaintText);
                    break;
                case 1: case 3: case 5:
                    mPaintText.setTextSize(45f);
                    mCanvas.drawText(list.get(i), x-12, y+75, mPaintText);
                    break;
                case 7:
                    mCanvas.drawText(list.get(i), x-23, y+87, mPaintText);
                    break;
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
        mSendAlpha.sendAlphaValue(mAlpha);
    }

    public void fadeDotDraw(int alpha, boolean fadingIn) {
        mAlpha2 = alpha;
        mFadingIn = fadingIn;
        mAddSubFade = true;
        invalidate();
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