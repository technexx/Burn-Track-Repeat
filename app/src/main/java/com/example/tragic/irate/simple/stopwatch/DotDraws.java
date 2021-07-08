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
  Paint mPaintBox;
  Paint mPaintText;
  float mX;
  float mX2;
  float mY;
  float mY2;
  int mSetCount;
  int mBreakCount;
  int mSetReduce;
  int mBreakReduce;
  int mBreakOnlyCount;
  int mBreakOnlyReduce;

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

  sendPosition mSendPosition;
  sendAlpha mSendAlpha;
  boolean mGoingUpSets;
  boolean mGoingUpBreaks;
  boolean mGoingUpBreaksOnly;
  boolean mAddSubFade;
  boolean mFadeUp;

  public interface sendPosition {
    void sendPos(int pos);
  }

  public interface sendAlpha {
    void sendAlphaValue(int alpha);
  }

  //Todo: Make sure to instantiate this in TimerInterface if you want to use it.
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

    mPaintBox = new Paint();
    mPaintBox.setColor(Color.WHITE);
    mPaintBox.setStyle(Paint.Style.STROKE);
    mPaintBox.setStrokeWidth(6);
  }

  public void setMode(int mode) {
    mMode = mode;
  }

  //Called from Main and determines whether we are counting up or down.
  public void countingUpSets(boolean goingUpSets) {
    mGoingUpSets = goingUpSets;
  }

  //Called from Main and determines whether we are counting up or down.
  public void countingUpBreaks(boolean goingUpBreaks) {
    mGoingUpBreaks = goingUpBreaks;
  }

  public void countingUpBreaksOnly(boolean goingUpBreaksOnly) {
    mGoingUpBreaksOnly = goingUpBreaksOnly;
  }

  public void customDrawSet(int setCount, int setReduce, int fadeDone) {
    this.mSetCount = setCount; this.mSetReduce = setReduce; this.mFadeDone = fadeDone;
    invalidate();
  }

  public void customDrawBreak(int breakCount, int breakReduce) {
    this.mBreakCount = breakCount; this.mBreakReduce = breakReduce;
    invalidate();
  }

  public void breaksOnlyDraw(int breakOnlyCount, int breakOnlyReduce, int fadeDone) {
    this.mBreakOnlyCount = breakOnlyCount; this.mBreakOnlyReduce = breakOnlyReduce; this.mFadeDone = fadeDone;
    invalidate();
  }

  public void pomDraw(int pomDotCounter, ArrayList<Integer> pomTime, int fadeDone) {
    mPomTime = new ArrayList<>();
    for (int i=0; i<pomTime.size(); i++) mPomTime.add(convertSeconds(pomTime.get(i)/1000));
    this.mPomDotCounter = pomDotCounter; this.mFadeDone = fadeDone;
    setupPaint();
    invalidate();
  }
  //Updates list every time it is called w/ a String conversion of our long millis value.
  public void setTime(ArrayList<Integer> setTime) {
    mSetTime = new ArrayList<>();
    for (int i=0; i<setTime.size(); i++) {
      mSetTime.add(convertSeconds(setTime.get(i)/1000));
    }
  }

  public void breakTime(ArrayList<Integer> breakTime) {
    mBreakTime = new ArrayList<>();
    for (int i=0; i<breakTime.size(); i++) {
      mBreakTime.add(convertSeconds(breakTime.get(i)/1000));
    }
  }

  public void breakOnlyTime(ArrayList<Integer> breakOnlyTime) {
    mBreakOnlyTime = new ArrayList<>();
    for (int i=0; i<breakOnlyTime.size(); i++) {
      mBreakOnlyTime.add(convertSeconds(breakOnlyTime.get(i)/1000));
    }
  }

  public void setAlpha() {
    mAlpha = 255; cycle = 0;
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

    mX = 58; mY = 510; mX2 = 58; mY2 = 640;
    switch (mMode) {
      case 1:
        encloseDots(mY-10, mY+260);
        //Filled or stroked dots depending on count up/down.
        setDotStyle(mGoingUpSets);

        //Using mSetTime array size for loop instead of int constructor value.
        for (int i=0; i<mSetTime.size(); i++) {
          mPaint.setColor(Color.GREEN);
          if (mSetTime.size() - mSetReduce == i) {
            if (mFadeDone == 1) fadeDot();
          } else if (mSetReduce + i < mSetTime.size()){
            mPaint.setAlpha(100);
          } else mPaint.setAlpha(255);
          mCanvas.drawCircle(mX+20, mY+60, 55, mPaint);
          drawText(mSetTime, mX+16, mY+62, i);
          mX += 132;
        }
        for (int i=0; i<mBreakTime.size(); i++) {
          mPaint.setColor(Color.RED);
          if (mGoingUpBreaks) setDotStyle(true); else setDotStyle(false);
          if (mBreakTime.size() - mBreakReduce == i) {
            if (mFadeDone == 2) fadeDot();
          } else if (mBreakReduce + i <  mBreakTime.size()) {
            mPaint.setAlpha(100);
          } else mPaint.setAlpha(255);
          mCanvas.drawCircle(mX2+20, mY2+60, 55, mPaint);
          drawText(mBreakTime, mX2+16, mY2+62, i);
          mX2 += 132;
        }
        break;
      case 2:
        mX2 = 15;
        encloseDots(mY-30, mY+160);
        if (mGoingUpBreaksOnly) setDotStyle(true); else setDotStyle(false);

        for (int i=0; i<mBreakOnlyCount; i++) {
          mPaint.setColor(Color.RED);
          if (mBreakOnlyCount - mBreakOnlyReduce == i) {
            if (mFadeDone == 3) fadeDot();
          } else if (mBreakOnlyReduce + i <  mBreakOnlyCount) {
            mPaint.setAlpha(100);
          } else mPaint.setAlpha(255);
          mCanvas.drawRoundRect(mX2+7, mY2-130, mX2+115, mY2+5, 100, 100, mPaint);
          drawText(mBreakOnlyTime, mX2+60, mY2-60, i);
          mX2 += 132;
        }
        break;
      case 3:
        mX = 82; mX2=mX+125;
        encloseDots(mY-30, mY+160);
        //Fading last object drawn. Setting previous ones to "greyed out"
        for (int i=0; i<8; i++) {
          if (mPomDotCounter - 1 == i) pomFill(i, true, 255);
          else if (i - mPomDotCounter <=-2) pomFill(i, false, 100); else pomFill(i, false, 255);
        }
        break;
    }
  }

  public void pomFill(int i, boolean fade, int alpha) {
    switch (i) {
      case 0: case 2: case 4: case 6:
        mPaint.setColor(Color.GREEN);
        //Must be called AFTER color is changed, otherwise alpha will reset to 255.
        if (fade && mFadeDone == 4) fadeDot(); else mPaint.setAlpha(alpha);
        if (mAddSubFade) mPaintText.setAlpha(mAlpha2);
        mCanvas.drawCircle(mX, 575, 62, mPaint);
        if (mPomTime.size()!=0) drawText(mPomTime, mX, mY, i);
        mX+=260;
        break;
      case 1: case 3: case 5:
        mPaint.setColor(Color.RED);
        if (fade && mFadeDone == 4) fadeDot(); else mPaint.setAlpha(alpha);
        mCanvas.drawCircle(mX2, 575, 50 , mPaint);
        if (mAddSubFade) mPaintText.setAlpha(mAlpha2);
        if (mPomTime.size()!=0) drawText(mPomTime, mX2, mY, i);
        mX2=mX+130;
        break;
      case 7:
        mPaint.setColor(Color.RED);
        if (fade && mFadeDone == 4) fadeDot(); else mPaint.setAlpha(alpha);
        mCanvas.drawRect(mX-170, 520, mX-60, 630, mPaint);
        if (mAddSubFade) mPaintText.setAlpha(mAlpha2);
        if (mPomTime.size()!=0) drawText(mPomTime, mX2, mY, i);
        break;
    }
  }

  private void drawText(ArrayList<String> list, float x, float y, int i) {
    Typeface narrow = ResourcesCompat.getFont(getContext(), R.font.archivo_narrow);

    if (list.size() >0) {
      //If 2 or less chars in time string, fonts are as follows.
      if (list.get(i).length() <= 2) {
        if (list.get(i).length() == 1) {
          //Adds "0" to any single digit.
          String temp = list.get(i);
          temp = "0" + temp;
          list.set(i, temp);
        }
        mPaintText.setTypeface(Typeface.DEFAULT);
        switch (mMode) {
          case 1:
            mPaintText.setTextSize(70f);
            mCanvas.drawText(list.get(i), x-37, y+22, mPaintText);
            break;
          case 2:
            mPaintText.setTextSize(90f);
            mCanvas.drawText(list.get(i), x-52, y+28, mPaintText);
            break;
          case 3:
            switch (i) {
              case 0: case 2: case 4: case 6:
                mPaintText.setTextSize(70f);
                mCanvas.drawText(list.get(i), x-41, y+87, mPaintText);
                break;
              case 1: case 3: case 5:
                mPaintText.setTextSize(60f);
                mCanvas.drawText(list.get(i), x-35, y+85, mPaintText);
                break;
              case 7:
                mPaintText.setTextSize(70f);
                mCanvas.drawText(list.get(i), x-30, y+88, mPaintText);
                break;
            }
            break;
        }
      } else {
        //If 3 or more chars in time string, fonts are as follows.
        mPaintText.setTypeface(narrow);
        switch (mMode) {
          case 1:
            mPaintText.setTextSize(58f);
            mCanvas.drawText(list.get(i), x-43, y+17, mPaintText);
            break;
          case 2:
            mPaintText.setTextSize(65f);
            mCanvas.drawText(list.get(i), x-51, y+17, mPaintText);
            break;
          case 3:
            switch (i) {
              case 0: case 2: case 4: case 6:
                mPaintText.setTextSize(57f);
                mCanvas.drawText(list.get(i), x-58, y+87, mPaintText);
                break;
              case 1: case 3: case 5:
                mPaintText.setTextSize(50f);
                mCanvas.drawText(list.get(i), x-42, y+82, mPaintText);
                break;
              case 7:
                mPaintText.setTextSize(53f);
                mCanvas.drawText(list.get(i), x-39, y+87, mPaintText);
                break;
            }
        }
      }
    }
  }

  public void fadeDot() {
    mPaint.setAlpha(mAlpha);
    mSendAlpha.sendAlphaValue(mAlpha);
    if (mAlpha >=255) {
      mAlpha = 255;
      mFadeUp = false;
    }
    if (mAlpha>90 && !mFadeUp) mAlpha -=15; else {
      mFadeUp = true;
    }
    if (mFadeUp) mAlpha +=15;
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