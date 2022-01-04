package com.example.tragic.irate.simple.stopwatch;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class DotDraws extends View {
  Context mContext;
  Canvas mCanvas;
  Paint mPaint;
  Paint mPaintBox;
  Paint mPaintText;
  Paint mPaintNumbers;
  float mX;
  float mX2;
  float mY;

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

  ChangeSettingsValues changeSettingsValues = new ChangeSettingsValues();
  int SET_COLOR;
  int BREAK_COLOR;
  int WORK_COLOR;
  int MINI_BREAK_COLOR;
  int FULL_BREAK_COLOR;

  ScreenRatioLayoutChanger screenRatioLayoutChanger;

  public interface sendAlpha {
    void sendAlphaValue(int alpha);
  }

  public void onAlphaSend(sendAlpha xSendAlpha)  {
    this.mSendAlpha = xSendAlpha;
  }

  public DotDraws(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    mContext = context;
    setFocusable(true);
    setWillNotDraw(false);

    setupPaint();
    screenRatioLayoutChanger = new ScreenRatioLayoutChanger(mContext);
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

  public void changeColorSetting(int typeOFRound, int settingNumber) {
    if (typeOFRound==1) SET_COLOR = changeSettingsValues.assignColor(settingNumber);
    if (typeOFRound==2) BREAK_COLOR = changeSettingsValues.assignColor(settingNumber);
    if (typeOFRound==3) WORK_COLOR = changeSettingsValues.assignColor(settingNumber);
    if (typeOFRound==4) MINI_BREAK_COLOR = changeSettingsValues.assignColor(settingNumber);
    if (typeOFRound==5) FULL_BREAK_COLOR = changeSettingsValues.assignColor(settingNumber);
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
    invalidate();
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

    mX = dpConv(20); mY= dpConv(200);
//    mX2 = dpConv(28);
    int circleRadius = dpConv(16);

    float encloseYStartOneRow = dpConv(40);
    float encloseYEndOneRow = dpConv(110);
    float encloseYStartTwoRows = dpConv(40);
    float encloseYEndTwoRows = dpConv(110);

    if (screenRatioLayoutChanger.setScreenRatioBasedLayoutChanges()>=1.8f) {
      encloseYStartOneRow = dpConv(40);
      encloseYEndOneRow = dpConv(140);
      encloseYStartTwoRows = dpConv(10);
      encloseYEndTwoRows = dpConv(170);

      mY = dpConv(220);
      circleRadius = dpConv(19);
    }

    switch (mMode) {
      case 1:
        if (mRoundTimes.size()<=8) {
          encloseDots(encloseYStartOneRow, encloseYEndOneRow);
        } else {
          encloseDots(encloseYStartTwoRows, encloseYEndTwoRows);
        }

        for (int i=0; i<mRoundTimes.size(); i++) {
          //If type 1 or 2 (sets), color is green. Otherwise, color is red.
          if (mRoundType.get(i)==1 || mRoundType.get(i) ==2) mPaint.setColor(SET_COLOR); else mPaint.setColor(BREAK_COLOR);
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
            mCanvas.drawCircle(mX, mY, circleRadius, mPaint);
            drawText(mRoundTimes, mX, mY, i);
            mCanvas.drawText(String.valueOf(i+1), mX, mY, mPaintNumbers);
//            mX += 110;
          } else {
            //Different draw positions for each row if more than 8 rounds.
            if (i<=7) {
              mCanvas.drawCircle(mX, mY, circleRadius, mPaint);
              drawText(mRoundTimes, mX, mY, i);
              mCanvas.drawText(String.valueOf(i+1), mX, mY, mPaintNumbers);
//              mX += 132;
              //Resetting mX after 8th round so the second row begins on top of first.
              if (i==7) mX = dpConv(20);
            } else {
              mCanvas.drawCircle(mX, mY, circleRadius, mPaint);
              drawText(mRoundTimes, mX, mY, i);
              //Position 8 (first pos, second row), has to be indented slightly.
              if (i==8) {
                mCanvas.drawText(String.valueOf(i+1), mX, mY, mPaintNumbers);
              } else {
                mCanvas.drawText(String.valueOf(i+1), mX, mY, mPaintNumbers);
              }
//              mX += 132;
            }
          }
          mX += dpConv(40);
        }
        break;
      case 3:
        setDotStyle(false);
//        mX = 82; mX2=mX+125;
        encloseDots(encloseYStartOneRow , encloseYEndOneRow);
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
    int pomRadiusLarge = dpConv(23);
    int pomRadiusSmall = dpConv(17);
    switch (i) {
      case 0: case 2: case 4: case 6:
        mPaint.setColor(WORK_COLOR);
        //Must be called AFTER color is changed, otherwise alpha will reset to 255.
        if (fade) fadeDot(false); else mPaint.setAlpha(alpha);
        if (mAddSubFade) mPaintText.setAlpha(mAlpha2);
        mCanvas.drawCircle(mX, mY, pomRadiusLarge, mPaint);
        if (mPomTime.size()!=0) drawText(mPomTime, mX, mY, i);
        break;
      case 1: case 3: case 5:
        mPaint.setColor(MINI_BREAK_COLOR);
        if (fade) fadeDot(false); else mPaint.setAlpha(alpha);
        mCanvas.drawCircle(mX, mY, pomRadiusSmall , mPaint);
        if (mAddSubFade) mPaintText.setAlpha(mAlpha2);
        if (mPomTime.size()!=0) drawText(mPomTime, mX, mY, i);
        break;
      case 7:
        mPaint.setColor(FULL_BREAK_COLOR);
        if (fade) fadeDot(false); else mPaint.setAlpha(alpha);
        mCanvas.drawRect(mX, dpConv(250), mX, dpConv(290), mPaint);
        if (mAddSubFade) mPaintText.setAlpha(mAlpha2);
        if (mPomTime.size()!=0) drawText(mPomTime, mX, mY, i);
        break;
    }
    mX += (dpConv(85));
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
            mCanvas.drawText(list.get(i), x, y, mPaintText);
            break;
          case 3:
            switch (i) {
              case 0: case 2: case 4: case 6:
                mPaintText.setTextSize(70f);
                mCanvas.drawText(list.get(i), x, y, mPaintText);
                break;
              case 1: case 3: case 5:
                mPaintText.setTextSize(60f);
                mCanvas.drawText(list.get(i), x, y, mPaintText);
                break;
              case 7:
                mPaintText.setTextSize(70f);
                mCanvas.drawText(list.get(i), x, y, mPaintText);
                break;
            }
            break;
        }
      } else if (list.get(i).length()<=4){
        //If 3 or more chars in time string, fonts are as follows.
        mPaintText.setTypeface(narrow);
        switch (mMode) {
          case 1:
            mPaintText.setTextSize(58f);
            mCanvas.drawText(list.get(i), x, y, mPaintText);
            break;
          case 3:
            switch (i) {
              case 0: case 2: case 4: case 6:
                mPaintText.setTextSize(57f);
                mCanvas.drawText(list.get(i), x, y, mPaintText);
                break;
              case 1: case 3: case 5:
                mPaintText.setTextSize(50f);
                mCanvas.drawText(list.get(i), x, y, mPaintText);
                break;
              case 7:
                mPaintText.setTextSize(53f);
                mCanvas.drawText(list.get(i), x, y, mPaintText);
                break;
            }
        }
      } else {
        mPaintText.setTypeface(narrow);
        switch (mMode) {
          case 1:
            mPaintText.setTextSize(48f);
            mCanvas.drawText(list.get(i), x, y, mPaintText);
            break;
          case 3:
            switch (i) {
              case 0: case 2: case 4: case 6:
                mPaintText.setTextSize(57f);
                mCanvas.drawText(list.get(i), x, y, mPaintText);
                break;
              case 1: case 3: case 5:
                mPaintText.setTextSize(50f);
                mCanvas.drawText(list.get(i), x, y, mPaintText);
                break;
              case 7:
                mPaintText.setTextSize(53f);
                mCanvas.drawText(list.get(i), x, y, mPaintText);
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

  public int dpConv(float pixels) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, pixels, getResources().getDisplayMetrics());
  }
}