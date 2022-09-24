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
//        Log.i("testSync", "first textView SET is " + firstTextView);
    }

    public String getSecondTextView() {
        return mSecondTextView;
    }

    public void setSecondTextView(String secondTextView) {
        this.mSecondTextView = secondTextView;
//        Log.i("testSync", "second textView SET is " + secondTextView);
    }

    public boolean areTextViewsDifferent() {
//        Log.i("testSync", "In comparison, first is " + mFirstTextView + " and second is " + mSecondTextView);
        return !mFirstTextView.equals(mSecondTextView);
    }
}