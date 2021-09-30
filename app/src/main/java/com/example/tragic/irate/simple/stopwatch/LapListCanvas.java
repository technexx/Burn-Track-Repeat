package com.example.tragic.irate.simple.stopwatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

public class LapListCanvas extends View {
    Canvas mCanvas;
    Paint mPaint;

    public LapListCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(3);
    }

    public void drawGradientBox() {
        int nextYCoord = convertDensityPixelsToScalable(140);
        int alpha = 0;
        for (int i=0; i<22; i++) {
            mPaint.setAlpha(alpha);
            mCanvas.drawRect(0, nextYCoord, 700, nextYCoord + convertDensityPixelsToScalable(5), mPaint);
            nextYCoord += convertDensityPixelsToScalable(5);
            alpha+=12;
            Log.i("testpixels", "next y coord is " + nextYCoord);
        }
    }

    public int convertDensityPixelsToScalable(float pixels) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, pixels, getResources().getDisplayMetrics());
    }

    @Override
    public void onDraw(Canvas canvas) {
        this.mCanvas = canvas;
        drawGradientBox();
    }
}