package com.example.tragic.irate.simple.stopwatch.Miscellaneous;

import android.util.Log;

public class TextViewDisplaySync {
    String mFirstTextView = "";
    String mSecondTextView = "";

    public TextViewDisplaySync() {
    }

    public String getFirstTextView() {
        return mFirstTextView;
    }

    public void setFirstTextView(String firstTextView) {
        this.mFirstTextView = firstTextView;
    }

    public String getSecondTextView() {
        return mSecondTextView;
    }

    public void setSecondTextView(String secondTextView) {
        this.mSecondTextView = secondTextView;
    }

    public boolean areTextViewsDifferent() {
//        Log.i("testRun", "In comparison, first is " + mFirstTextView + " and second is " + mSecondTextView);
        return !mFirstTextView.equals(mSecondTextView);
    }
}