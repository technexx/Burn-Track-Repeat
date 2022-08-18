package com.example.tragic.irate.simple.stopwatch.Miscellaneous;

import android.util.Log;
import android.widget.TextView;

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
        return !mFirstTextView.equals(mSecondTextView);
    }

}
