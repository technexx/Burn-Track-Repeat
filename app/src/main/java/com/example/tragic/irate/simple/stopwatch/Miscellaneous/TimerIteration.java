package com.example.tragic.irate.simple.stopwatch.Miscellaneous;

public class TimerIteration {
    long mStableTime;
    long mCurrentTime;
    long mIteratedTime;
    long mPreviousTotal;
    long mNewTotal;

    public TimerIteration() {
    }

    public void setStableTime (long stableTime) {
        this.mStableTime = stableTime;
    }

    public long getStableTime() {
        return mStableTime;
    }

    public void setCurrentTime(long currentTime) {
        this.mCurrentTime = currentTime;
    }

    public long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public void setIteratedTime(long time) {
        this.mIteratedTime = time;
    }

    public long getIteratedTime() {
        return mIteratedTime;
    }

    public long getDifference(long stableTime, long currentTime) {
        return currentTime - stableTime;
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