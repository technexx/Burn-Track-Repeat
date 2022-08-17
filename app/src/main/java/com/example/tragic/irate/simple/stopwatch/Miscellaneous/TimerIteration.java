package com.example.tragic.irate.simple.stopwatch.Miscellaneous;

import android.util.Log;

public class TimerIteration {
    long mStableTime;
    long mCurrentTime;
    long mPreviousTotal;
    long mNewTotal;

    public TimerIteration() {
    }

    public void setStableTime (long stableTime) {
        mStableTime = stableTime;
    }

    public long getStableTime() {
        return mStableTime;
    }

    public void setCurrentTime(long currentTime) {
        this.mCurrentTime = currentTime;
    }

    public long getCurrentTime() {
        return mCurrentTime;
    }

    public long getDifference() {
        return mCurrentTime - mStableTime;
    }

    public long getPreviousTotal() {
        return mPreviousTotal;
    }

    public void setPreviousTotal(long previousTotal) {
        this.mPreviousTotal = previousTotal;
    }

    public long getNewTotal() {
        return mNewTotal;
    }

    public void setNewTotal(long newTotal) {
        this.mNewTotal = newTotal;
    }
}