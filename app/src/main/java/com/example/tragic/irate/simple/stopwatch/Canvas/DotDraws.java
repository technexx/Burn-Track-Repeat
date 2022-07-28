package com.example.tragic.irate.simple.stopwatch.Canvas;
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

import com.example.tragic.irate.simple.stopwatch.SettingsFragments.ChangeSettingsValues;
import com.example.tragic.irate.simple.stopwatch.R;
import com.example.tragic.irate.simple.stopwatch.Miscellaneous.ScreenRatioLayoutChanger;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DotDraws extends View {
  Context mContext;
  Canvas mCanvas;
  Paint mPaint;
  Paint mPaintBox;
  Paint mPaintText;
  Paint mPaintRoundNumbers;

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
  float mPhoneHeight;
  float mPhoneWidth;

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

    mRoundTimes = new ArrayList<>();
    mRoundType = new ArrayList<>();
    mPomTime = new ArrayList<>();
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

    mPaintRoundNumbers = new Paint();
    mPaintRoundNumbers.setAntiAlias(true);
    mPaintRoundNumbers.setColor(Color.WHITE);
    mPaintRoundNumbers.setTextSize(dpConvFloat(15f));

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
  }

  //Updates list every time it is called w/ a String conversion of our long millis value.
  public void updateWorkoutTimes(ArrayList<String> roundTimes, ArrayList<Integer> roundType) {
    //Populates our String Array of round times from the Integer Array of values received from Timer class.

    this.mRoundTimes = roundTimes;
    this.mRoundType = roundType;
  }

  public void pomDraw(int pomDotCounter, ArrayList<Integer> pomTime) {
    mPomTime = new ArrayList<>();
    for (int i=0; i<pomTime.size(); i++) mPomTime.add(convertSeconds(pomTime.get(i)/1000));
    this.mPomDotCounter = pomDotCounter;
  }

  public void reDraw() {
    invalidate();
  }

  public void resetDotAlpha() {
    mAlpha = 255; cycle = 0;
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

  public void encloseDots(float xStart, float xEnd, float topY, float botY) {
    mPaintBox.setStyle(Paint.Style.STROKE);
    mPaintBox.setAlpha(175);
    mCanvas.drawRoundRect(xStart, topY, xEnd, botY, 20, 20, mPaintBox);
    mPaintBox.setStyle(Paint.Style.FILL);
    mPaintBox.setAlpha(35);
    mCanvas.drawRoundRect(xStart, topY, xEnd, botY, 20, 20, mPaintBox);
  }

  public void receivePhoneDimensions(int height, int width) {
    mPhoneHeight = height / getResources().getDisplayMetrics().scaledDensity;
    mPhoneWidth = width / getResources().getDisplayMetrics().scaledDensity;
  }

  @Override
  public void onDraw(Canvas canvas) {
    this.mCanvas = canvas;

    int paddedWidth = (int) mPhoneWidth-dpConv(11);
    double circleCircumference = (double) paddedWidth/8;
    int circleRadius = (int) dpConv((float) circleCircumference/2) - dpConv(1);

    int xMovementOfCirclePositionBetweenEachDrawing = (int) dpConv((float) circleCircumference) + dpConv(2);
    int xMovementOfCircleTextBetweenEachDrawing = xMovementOfCirclePositionBetweenEachDrawing;
    int xMovementOfRoundNumberTextBetweenEachDrawing = xMovementOfCirclePositionBetweenEachDrawing - dpConv(1);

    int xStartingEnclosurePositionForAllRows = dpConv(2);
    int xEndingEnclosurePositionForAllRows = dpConv(mPhoneWidth-2);

    int yStartingEnclosurePositionWithOneRow = dpConv(40);
    int yEndingEnclosurePositionWithOneRow = dpConv(130);
    int yStartingEnclosurePositionWithTwoRows = dpConv(10);
    int yEndingEnclosurePositionWithTwoRows = dpConv(165);

    int xStartingPositionOfCircleForAllRows = dpConv(28);
    int xStartingPositionOfCircleTextForAllRows = dpConv(11);
    int xStartingPositionOfRoundNumberTextForAllRows = dpConv(25);

    int xCirclePositionForAllRows = dpConv(28);
    int yCirclePositionForOneRow = dpConv(76);
    int yCirclePositionForFirstOfTwoRows = dpConv(41);
    int yCirclePositionForSecondOfTwoRows = dpConv(115);

    int xCircleTextPositionForAllRows = dpConv(10);
    int yCircleTextPositionForOneRow = dpConv(88);
    int yCircleTextPositionForOneOfTwoRows= dpConv(53);
    int yCircleTextPositionForSecondOfTwoRows = dpConv(127);

    int xRoundNumberTextPositionForAllRows = dpConv(25);
    int yRoundNumberTextPositionForOneRow = dpConv(119);
    int yRoundNumberTextPositionForOneOfTwoRows = dpConv(82);
    int yRoundNumberTextPositionForSecondOfTwoRows = dpConv(157);

//    if (screenRatioLayoutChanger.setScreenRatioBasedLayoutChanges()>=1.8f) {
//      circleRadius = (int) dpConv((float) circleCircumference/2);
//      xMovementOfCirclePositionBetweenEachDrawing = (int) dpConv((float) circleCircumference) + dpConv(3);
//
//      xMovementOfCircleTextBetweenEachDrawing = xMovementOfCirclePositionBetweenEachDrawing;
//      xMovementOfRoundNumberTextBetweenEachDrawing = xMovementOfCirclePositionBetweenEachDrawing;
//
//      yStartingEnclosurePositionWithOneRow = dpConv(40);
//      yEndingEnclosurePositionWithOneRow = dpConv(140);
//      yStartingEnclosurePositionWithTwoRows = dpConv(10);
//      yEndingEnclosurePositionWithTwoRows = dpConv(170);
//
//      xCirclePositionForAllRows = dpConv(28);
//      yCirclePositionForOneRow = dpConv(82);
//      yCirclePositionForFirstOfTwoRows = dpConv(48);
//      yCirclePositionForSecondOfTwoRows = dpConv(120);
//
//      xCircleTextPositionForAllRows = dpConv(11);
//      yCircleTextPositionForOneRow = dpConv(92);
//      yCircleTextPositionForOneOfTwoRows= dpConv(58);
//      yCircleTextPositionForSecondOfTwoRows = dpConv(130);
//
//      xRoundNumberTextPositionForAllRows = dpConv(25);
//      yRoundNumberTextPositionForOneRow = dpConv(125);
//      yRoundNumberTextPositionForOneOfTwoRows = dpConv(88);
//      yRoundNumberTextPositionForSecondOfTwoRows = dpConv(160);
//    }

    Log.i("testDraw", "DotDraws is updating!");

    switch (mMode) {
      case 1:
        if (mRoundTimes.size()<=8) {
          encloseDots(xStartingEnclosurePositionForAllRows, xEndingEnclosurePositionForAllRows, yStartingEnclosurePositionWithOneRow, yEndingEnclosurePositionWithOneRow);
        } else {
          encloseDots(xStartingEnclosurePositionForAllRows, xEndingEnclosurePositionForAllRows, yStartingEnclosurePositionWithTwoRows, yEndingEnclosurePositionWithTwoRows);
        }

        for (int i=0; i<mRoundType.size(); i++) {
          //If type 1 or 2 (sets), color is green. Otherwise, color is red.
          if (mRoundType.get(i)==1 || mRoundType.get(i) ==2) {
            mPaint.setColor(SET_COLOR);
          } else {
            mPaint.setColor(BREAK_COLOR);
          }
          //if type 1 or 3 (counting down), dots are filled. Otherwise, they are hollow.
          if (mRoundType.get(i)==1 || mRoundType.get(i)==3) {
            setDotStyle(false);
          } else {
            setDotStyle(true);
          }
          //If the rounds remaining subtracted from our total rounds equals the position in its list we are drawing, fade that position (i.e. our current round).
          if (mRoundCount - mRoundsLeft == i) {
            //If we are in a "count up" round, also fade the text.
            if (mRoundType.get(i)==2 || mRoundType.get(i)==4) {
              fadeDot(true);
            } else {
              fadeDot(false);
            }
          }
          //if our remaining rounds added to our current position in the round list is less than the size of the list, mute that position's alpha (i.e. completed rounds).
          else if (mRoundsLeft + i < mRoundCount) {
            mPaint.setAlpha(105);
            //If position is a "count up" round, also mute the text's alpha.
            if (mRoundType.get(i)==2 || mRoundType.get(i)==4) {
              mPaintText.setAlpha(105);
            }
          }
          else {
            //If neither of the previous conditions are true, retain a static full alpha value (i.e. an unreached round).
            mPaint.setAlpha(255);
            //If position is a "count up" round, also retain the text's full alpha value.
            if (mRoundType.get(i)==2 || mRoundType.get(i)==4) {
              mPaintText.setAlpha(255);
            }
          }

          if (mRoundTimes.size()<=8) {
            //Draws dot, timer value, and round count.
            mCanvas.drawCircle(xCirclePositionForAllRows, yCirclePositionForOneRow, circleRadius, mPaint);
            drawText(mRoundTimes, xCircleTextPositionForAllRows, yCircleTextPositionForOneRow, i);
            mCanvas.drawText(String.valueOf(i+1), xRoundNumberTextPositionForAllRows, yRoundNumberTextPositionForOneRow, mPaintRoundNumbers);
          } else {
            //Different draw positions for each row if more than 8 rounds.
            if (i<=7) {
              mCanvas.drawCircle(xCirclePositionForAllRows, yCirclePositionForFirstOfTwoRows, circleRadius, mPaint);
              drawText(mRoundTimes, xCircleTextPositionForAllRows, yCircleTextPositionForOneOfTwoRows, i);
              mCanvas.drawText(String.valueOf(i+1), xRoundNumberTextPositionForAllRows, yRoundNumberTextPositionForOneOfTwoRows, mPaintRoundNumbers);
              //Resetting mX after 8th round so the second row begins on top of first.
            } else {
              if (i==8) {
                xCirclePositionForAllRows = xStartingPositionOfCircleForAllRows;
                xCircleTextPositionForAllRows = xStartingPositionOfCircleTextForAllRows;
                xRoundNumberTextPositionForAllRows = xStartingPositionOfRoundNumberTextForAllRows;
              }
              mCanvas.drawCircle(xCirclePositionForAllRows, yCirclePositionForSecondOfTwoRows, circleRadius, mPaint);
              drawText(mRoundTimes, xCircleTextPositionForAllRows, yCircleTextPositionForSecondOfTwoRows, i);

              mCanvas.drawText(String.valueOf(i+1), xRoundNumberTextPositionForAllRows, yRoundNumberTextPositionForSecondOfTwoRows, mPaintRoundNumbers);
              if (i==8) {
                xRoundNumberTextPositionForAllRows -= dpConv(5);
              }
            }
          }
          xCirclePositionForAllRows += xMovementOfCirclePositionBetweenEachDrawing;
          xCircleTextPositionForAllRows += xMovementOfCircleTextBetweenEachDrawing;
          xRoundNumberTextPositionForAllRows += xMovementOfRoundNumberTextBetweenEachDrawing + dpConv(0.5f);
        }
        break;
      case 3:
        double partsOfWidth = (double) paddedWidth/16;

        int pomRadiusLarge = (int) ((double) dpConv((int) partsOfWidth) * 1.10);
        int pomRadiusSmall = (int) ((double) pomRadiusLarge/1.25);

        xMovementOfCirclePositionBetweenEachDrawing = (int) ((double)dpConv((float) circleCircumference) * 1.03);
        int definedRectangleMovement = (int) ((double)dpConv((float) circleCircumference) * .98);

        xCirclePositionForAllRows = dpConv(28);
        yCirclePositionForOneRow = dpConv(76);

        int dotTextDrawForX = xCirclePositionForAllRows - pomRadiusSmall + dpConv(6);
        xRoundNumberTextPositionForAllRows = dpConv(24);

        int xRectangleStart = xCirclePositionForAllRows - dpConv(2);
        int xRectangleEnd = xRectangleStart + dpConv(44);
        int yRectangleTop = dpConv(15);
        int yRectangleBottom = dpConv(59);

        if (screenRatioLayoutChanger.setScreenRatioBasedLayoutChanges()>=1.8f) {
          xCirclePositionForAllRows = dpConv(28);
          yCirclePositionForOneRow = dpConv(78);

          dotTextDrawForX = xCirclePositionForAllRows - pomRadiusSmall + dpConv(4.5f);

          xRectangleStart = xCirclePositionForAllRows - dpConv(2);
          xRectangleEnd = xRectangleStart + (pomRadiusLarge*2) + dpConv(2);
          yRectangleTop = dpConv(54);
          yRectangleBottom = dpConv(102);
        }

        setDotStyle(false);
        encloseDots(xStartingEnclosurePositionForAllRows, xEndingEnclosurePositionForAllRows, yStartingEnclosurePositionWithOneRow , yEndingEnclosurePositionWithOneRow);
        //Fading last object drawn. Setting previous ones to "greyed out"
        for (int i=0; i<8; i++) {
          int alphaValue = 255;
          boolean fadeDot = false;

          if (mPomDotCounter == i) {
            alphaValue = 255;
            fadeDot = true;
          } else if (i - mPomDotCounter <=0) {
            alphaValue = 105;
            fadeDot = false;
          } else {
            alphaValue = 255;
            fadeDot = false;
          }

          switch (i) {
            case 0: case 2: case 4: case 6:
              mPaint.setColor(WORK_COLOR);
              //Must be called AFTER color is changed, otherwise alpha will reset to 255.
              if (fadeDot) fadeDot(false); else mPaint.setAlpha(alphaValue);
              mCanvas.drawCircle(xCirclePositionForAllRows, yCirclePositionForOneRow, pomRadiusLarge, mPaint);

              if (mAddSubFade) mPaintText.setAlpha(mAlpha2);
              drawText(mPomTime, dotTextDrawForX, yCircleTextPositionForOneRow, i);
              break;
            case 1: case 3: case 5:
              mPaint.setColor(MINI_BREAK_COLOR);
              if (fadeDot) fadeDot(false); else mPaint.setAlpha(alphaValue);
              mCanvas.drawCircle(xCirclePositionForAllRows, yCirclePositionForOneRow, pomRadiusSmall , mPaint);

              if (mAddSubFade) mPaintText.setAlpha(mAlpha2);
              drawText(mPomTime, dotTextDrawForX, yCircleTextPositionForOneRow, i);
              break;
            case 7:
              mPaint.setColor(FULL_BREAK_COLOR);
              if (fadeDot) fadeDot(false); else mPaint.setAlpha(alphaValue);
              mCanvas.drawRect(xRectangleStart, yRectangleTop, xRectangleEnd, yRectangleBottom, mPaint);

              if (mAddSubFade) mPaintText.setAlpha(mAlpha2);
              drawText(mPomTime, xRectangleStart + dpConv(8), yCircleTextPositionForOneRow, i);
              break;
          }
          if (i==7) {
            mCanvas.drawText(String.valueOf(i+1), xRoundNumberTextPositionForAllRows - dpConv(6), yRoundNumberTextPositionForOneRow, mPaintRoundNumbers);
          } else {
            mCanvas.drawText(String.valueOf(i+1), xRoundNumberTextPositionForAllRows, yRoundNumberTextPositionForOneRow, mPaintRoundNumbers);
          }

          xCirclePositionForAllRows += xMovementOfCirclePositionBetweenEachDrawing;

          dotTextDrawForX += xMovementOfCirclePositionBetweenEachDrawing;

          if (i==6) {
            dotTextDrawForX += dpConv(6);
            xMovementOfRoundNumberTextBetweenEachDrawing += dpConv(13);
          }

          xRectangleStart += definedRectangleMovement;
          xRectangleEnd += definedRectangleMovement;
          xRoundNumberTextPositionForAllRows += xMovementOfRoundNumberTextBetweenEachDrawing;
        }
        break;
    }
  }

  private void drawText(ArrayList<String> list, float x, float y, int i) {
    Typeface narrow = ResourcesCompat.getFont(getContext(), R.font.archivo_narrow);

    float modeOneTextSizeForLowDigits = dpConvFloat(30f);
    float modeOneTextSizeForMediumDigits = dpConvFloat(23f);
    float modeOneTextSizeForLargeDigits = dpConvFloat(19f);

    float modeThreeTextSizeForMediumDigits = dpConvFloat(20f);
    float modeThreeTextSizeForLargeDigits = dpConvFloat(20f);
    float modeThreeTextSizeForRectangle = dpConvFloat(21f);

    int xMediumMod = dpConv(1);
    int yMediumMod = dpConv(3);
    int xSmallMod = dpConv(2);
    int ySmallMod = dpConv(5);

    int xSmallCircleMod = dpConv(3);
    int xBigCircleMod = dpConv(7);
    int yBigCircleMod = dpConv(5);
    int ySmallCircleMod = dpConv(5);
    int xSquareMod = dpConv(7);
    int ySquareMod = dpConv(3);

    mPaintRoundNumbers.setTextSize(dpConvFloat(18f));

    if (screenRatioLayoutChanger.setScreenRatioBasedLayoutChanges()>=1.8f) {
      modeOneTextSizeForLowDigits = dpConvFloat(30f);
      modeOneTextSizeForMediumDigits = dpConvFloat(23f);
      modeOneTextSizeForLargeDigits = dpConvFloat(20f);

      modeThreeTextSizeForMediumDigits = dpConvFloat(20f);
      modeThreeTextSizeForLargeDigits = dpConvFloat(22f);
      modeThreeTextSizeForRectangle = dpConvFloat(24f);

      xMediumMod = dpConv(2);
      yMediumMod = dpConv(3);
      xSmallMod = dpConv(4);
      ySmallMod = dpConv(4);

      xBigCircleMod = dpConv(7);
      yBigCircleMod = dpConv(2);
      ySmallCircleMod = dpConv(3);
      xSquareMod = dpConv(7);
      ySquareMod = dpConv(3);

      mPaintRoundNumbers.setTextSize(dpConvFloat(20f));
    }

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
        mPaintText.setTextSize(modeOneTextSizeForLowDigits);
        mCanvas.drawText(list.get(i), x, y, mPaintText);
      } else if (list.get(i).length()<=4){
        mPaintText.setTypeface(narrow);
        switch (mMode) {
          case 1:
            mPaintText.setTextSize(modeOneTextSizeForMediumDigits);
            mCanvas.drawText(list.get(i), x-xMediumMod, y-yMediumMod, mPaintText);
            break;
          case 3:
            switch (i) {
              //4 length (X:XX) only used for mini breaks.
              case 1: case 3: case 5:
                mPaintText.setTextSize(modeThreeTextSizeForMediumDigits);
                mCanvas.drawText(list.get(i), x-xSmallCircleMod, y-ySmallCircleMod, mPaintText);
                break;
            }
        }
      } else {
        mPaintText.setTypeface(narrow);
        switch (mMode) {
          case 1:
            mPaintText.setTextSize(modeOneTextSizeForLargeDigits);
            mCanvas.drawText(list.get(i), x-xSmallMod, y-ySmallMod, mPaintText);
            break;
          case 3:
            switch (i) {
              case 0: case 2: case 4: case 6:
                mPaintText.setTextSize(modeThreeTextSizeForLargeDigits);
                mCanvas.drawText(list.get(i), x-xBigCircleMod, y-yBigCircleMod, mPaintText);
                break;
              case 7:
                mPaintText.setTextSize(modeThreeTextSizeForRectangle);
                mCanvas.drawText(list.get(i), x-xSquareMod, y-ySquareMod, mPaintText);
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
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixels, getResources().getDisplayMetrics());
  }

  public float dpConvFloat(float pixels) {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixels, getResources().getDisplayMetrics());
  }
}