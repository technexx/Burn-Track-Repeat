package com.example.tragic.irate.simple.stopwatch;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
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
  Paint mPaintNumbers;
  float mX;
  float mX2;
  float mY;
  float mY2;

  int mRoundCount;
  int mRoundsLeft;
  ArrayList<String> mRoundTimes;
  ArrayList<Integer> mRoundType;
  ArrayList<String> mPomTime;

  int mAlpha = 255;
  int mAlpha2;
  int cycle;
  int mMode;
  int mPomDotCounter;

  sendAlpha mSendAlpha;
  boolean mAddSubFade;
  boolean mFadeUp;

  public interface sendAlpha {
    void sendAlphaValue(int alpha);
  }

  public void onAlphaSend(sendAlpha xSendAlpha) {
    this.mSendAlpha = xSendAlpha;
  }

  public DotDraws(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    setFocusable(true);
    setWillNotDraw(false);
    setupPaint();
  }

  public void setAlpha(int alpha) {
    this.mAlpha = alpha;
  }

  private void setupPaint(){
    mPaint = new Paint();
    mPaint.setAntiAlias(true);
    mPaint.setStyle(Paint.Style.FILL);
    mPaint.setStrokeWidth(4);

    mPaintText = new Paint();
    mPaintText.setAntiAlias(true);

    mPaintNumbers = new Paint();
    mPaintNumbers.setAntiAlias(true);
    mPaintNumbers.setColor(Color.WHITE);
    mPaintNumbers.setTextSize(40f);

    mPaintBox = new Paint();
    mPaintBox.setColor(Color.WHITE);
    mPaintBox.setStyle(Paint.Style.STROKE);
    mPaintBox.setStrokeWidth(6);
  }

  public void setMode(int mode) {
    mMode = mode;
  }

  public void updateWorkoutRoundCount(int roundCount, int roundsLeft) {
    this.mRoundCount = roundCount;  this.mRoundsLeft = roundsLeft;
    invalidate();
  }

  //Updates list every time it is called w/ a String conversion of our long millis value.
  public void updateWorkoutTimes(ArrayList<Integer> roundTimes, ArrayList<Integer> roundType) {
    //Populates our String Array of round times from the Integer Array of values received from Timer class.
    mRoundTimes = new ArrayList<>();
    for (int i=0; i<roundTimes.size(); i++) mRoundTimes.add(convertSeconds(roundTimes.get(i)/1000));
    //Sets our global mRoundType list to the one received from Timer class.
    mRoundType = roundType;
    invalidate();
  }

  public void pomDraw(int pomDotCounter, ArrayList<Integer> pomTime) {
    mPomTime = new ArrayList<>();
    for (int i=0; i<pomTime.size(); i++) mPomTime.add(convertSeconds(pomTime.get(i)/1000));
    this.mPomDotCounter = pomDotCounter;
    invalidate();
  }

  public void reDraw() {
    invalidate();
  }

  public void resetDotAlpha() {
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

  //Used to clear or filled dot for counting up/counting down.
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
    this.mCanvas = canvas;

    mX = 58; mY = 510; mX2 = 58; mY2 = 640;
    switch (mMode) {
      case 1:
        if (mRoundTimes.size()<=8) encloseDots(mY+130 , mY+335); else encloseDots(mY+70, mY+430);

        for (int i=0; i<mRoundTimes.size(); i++) {
          //If type 1 or 2 (sets), color is green. Otherwise, color is red.
          if (mRoundType.get(i)==1 || mRoundType.get(i) ==2) mPaint.setColor(Color.GREEN); else mPaint.setColor(Color.RED);
          //if type 1 or 3 (counting down), dots are filled. Otherwise, they are hollow.
          if (mRoundType.get(i)==1 || mRoundType.get(i)==3) setDotStyle(false); else setDotStyle(true);
          //If the rounds remaining subtracted from our total rounds equals the position in its list we are drawing, fade that position (i.e. our current round).
          if (mRoundCount - mRoundsLeft == i) {
            //If we are in a "count up" round, also fade the text.
            if (mRoundType.get(i)==2 || mRoundType.get(i)==4) fadeDot(true); else fadeDot(false);
          }
          //if our remaining rounds added to our current position in the round list is less than the size of the list, mute that position's alpha (i.e. completed rounds).
          else if (mRoundsLeft + i < mRoundCount) {
            mPaint.setAlpha(105);
            //If position is a "count up" round, also mute the text's alpha.
            if (mRoundType.get(i)==2 || mRoundType.get(i)==4) mPaintText.setAlpha(105);
          }
          else {
            //If neither of the previous conditions are true, retain a static full alpha value (i.e. an unreached round).
            mPaint.setAlpha(255);
            //If position is a "count up" round, also retain the text's full alpha value.
            if (mRoundType.get(i)==2 || mRoundType.get(i)==4) mPaintText.setAlpha(255);
          }

          if (mRoundTimes.size()<=8) {
            //Draws dot, timer value, and round count.
            mCanvas.drawCircle(mX+20, mY+210, 55, mPaint);
            drawText(mRoundTimes, mX+16, mY+212, i);
            mCanvas.drawText(String.valueOf(i+1), mX+5, mY+315, mPaintNumbers);
            mX += 132;
          } else {
            //Different draw positions for each row if more than 8 rounds.
            if (i<=7) {
              mCanvas.drawCircle(mX+20, mY+140, 55, mPaint);
              drawText(mRoundTimes, mX+16, mY+144, i);
              mCanvas.drawText(String.valueOf(i+1), mX+9, mY+235, mPaintNumbers);
              mX += 132;
              //Resetting mX after 8th round so the second row begins on top of first.
              if (i==7) mX = 58;
            } else {
              mCanvas.drawCircle(mX+20, mY+320, 55, mPaint);
              drawText(mRoundTimes, mX+16, mY+325, i);
              //Position 8 (first pos, second row), has to be indented slightly.
              if (i==8) mCanvas.drawText(String.valueOf(i+1), mX+9, mY+415, mPaintNumbers); else mCanvas.drawText(String.valueOf(i+1), mX-2, mY+415, mPaintNumbers);
              mX += 132;
            }
          }
        }
        break;
      case 3:
        mX = 82; mX2=mX+125;
        encloseDots(mY+130 , mY+335);
        //Fading last object drawn. Setting previous ones to "greyed out"
        for (int i=0; i<8; i++) {
          if (mPomDotCounter == i) pomFill(i, true, 255);
          else if (i - mPomDotCounter <=0) pomFill(i, false, 105); else pomFill(i, false, 255);
        }
        break;
    }
  }

  //Used to draw shape, text, and alpha values (including fades) on Pom objects. Called in canvas draw above.
  public void pomFill(int i, boolean fade, int alpha) {
    switch (i) {
      case 0: case 2: case 4: case 6:
        mPaint.setColor(Color.GREEN);
        //Must be called AFTER color is changed, otherwise alpha will reset to 255.
        if (fade) fadeDot(false); else mPaint.setAlpha(alpha);
        if (mAddSubFade) mPaintText.setAlpha(mAlpha2);
        mCanvas.drawCircle(mX, 740, 62, mPaint);
        if (mPomTime.size()!=0) drawText(mPomTime, mX, mY+165, i);
        mX+=260;
        break;
      case 1: case 3: case 5:
        mPaint.setColor(Color.RED);
        if (fade) fadeDot(false); else mPaint.setAlpha(alpha);
        mCanvas.drawCircle(mX2, 740, 50 , mPaint);
        if (mAddSubFade) mPaintText.setAlpha(mAlpha2);
        if (mPomTime.size()!=0) drawText(mPomTime, mX2, mY+165, i);
        mX2=mX+130;
        break;
      case 7:
        mPaint.setColor(Color.RED);
        if (fade) fadeDot(false); else mPaint.setAlpha(alpha);
        mCanvas.drawRect(mX-170, 685, mX-60, 795, mPaint);
        if (mAddSubFade) mPaintText.setAlpha(mAlpha2);
        if (mPomTime.size()!=0) drawText(mPomTime, mX2, mY+165, i);
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

  public void fadeDot(boolean fadeText) {
    mPaint.setAlpha(mAlpha);
    if (fadeText) mPaintText.setAlpha(mAlpha);
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