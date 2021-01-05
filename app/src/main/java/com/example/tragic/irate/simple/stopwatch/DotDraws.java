package com.example.tragic.irate.simple.stopwatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class DotDraws extends View {
    private Paint mPaint;
    float mX;
    float mY;
    Canvas mCanvas;

    public DotDraws(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setWillNotDraw(false);
    }

    private void setupPaint(){
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void newDraw(float x, float y) {
        this.mX = x; this.mY = y;
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        mCanvas = canvas;
        super.onDraw(mCanvas);
        setupPaint();
        mCanvas.drawCircle(mX, mY, 35, mPaint);
    }
}