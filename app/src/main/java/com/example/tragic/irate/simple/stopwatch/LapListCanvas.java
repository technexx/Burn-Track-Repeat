package com.example.tragic.irate.simple.stopwatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

public class LapListCanvas extends View {
    Canvas mCanvas;
    Paint mPaint;
    int mMode;

    ScreenRatioLayoutChanger screenRatioLayoutChanger;
    int gradientWidthAndHeight;
    int gradientShadeMovement;
    int gradientYAxisMovement;

    public LapListCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        screenRatioLayoutChanger = new ScreenRatioLayoutChanger(context);
        setGradientWidth();
    }

    public void drawOutlineBox() {
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);

        RectF rect = new RectF(0, convertDensityPixelsToScalable(10), convertDensityPixelsToScalable(gradientWidthAndHeight), convertDensityPixelsToScalable(gradientWidthAndHeight));

        mCanvas.drawRoundRect(rect, 50, 50, mPaint);
    }

    public void drawGradientBox() {
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL);

        int nextYCoord = convertDensityPixelsToScalable(140);
        int alpha = 0;
        for (int i=0; i<22; i++) {
            mPaint.setAlpha(alpha);
            mCanvas.drawRect(0, nextYCoord, convertDensityPixelsToScalable(gradientWidthAndHeight), nextYCoord + convertDensityPixelsToScalable(gradientYAxisMovement), mPaint);

            nextYCoord += convertDensityPixelsToScalable(gradientYAxisMovement);
            alpha+=gradientShadeMovement;
        }
    }

    public void setGradientWidth() {
        if (screenRatioLayoutChanger.setScreenRatioBasedLayoutChanges()<1.8f) {
            gradientWidthAndHeight = 240;
            gradientShadeMovement = 12;
            gradientYAxisMovement = 5;
        } else {
            gradientWidthAndHeight = 280;
            gradientShadeMovement = 10;
            gradientYAxisMovement = 7;
        }
    }

    public int convertDensityPixelsToScalable(float pixels) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, pixels, getResources().getDisplayMetrics());
    }

    public void setMode(int mode) {
        mMode = mode;
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        this.mCanvas = canvas;
        if (mMode==4) {
            drawGradientBox();
            drawOutlineBox();
        } else mPaint.setColor(Color.BLACK);
    }
}