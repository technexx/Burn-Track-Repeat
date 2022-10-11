package com.example.tragic.irate.simple.stopwatch.Canvas;

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

    int gradientWidthAndHeight;
    int gradientShadeMovement;
    int gradientYAxisMovement;

    int mScreenHeight;

    public LapListCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
    }

    public void setScreenHeight(int height) {
        this.mScreenHeight = height;
    }

    public void setGradientWidth() {
        if (mScreenHeight <= 1920) {
            gradientWidthAndHeight = 240;
            gradientShadeMovement = 12;
            gradientYAxisMovement = 5;
        } else {
            gradientWidthAndHeight = 280;
            gradientShadeMovement = 10;
            gradientYAxisMovement = 7;
        }
    }

    private void drawOutlineBox() {
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);

        RectF rect = new RectF(0, 0, convertDensityPixelsToScalable(gradientWidthAndHeight), convertDensityPixelsToScalable(gradientWidthAndHeight));

        mCanvas.drawRoundRect(rect, 35, 35, mPaint);
    }

    private void drawGradientBox() {
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

    private int convertDensityPixelsToScalable(float pixels) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixels, getResources().getDisplayMetrics());
    }

    @Override
    public void onDraw(Canvas canvas) {
        this.mCanvas = canvas;
        drawGradientBox();
        drawOutlineBox();
    }
}