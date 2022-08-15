package com.example.tragic.irate.simple.stopwatch.Miscellaneous;

public class TimerIteration {
    long mStableTime;
    long mCurrentTime;
    long mIteratedTime;

    public TimerIteration() {
    }

    public void setStableTime (long stableTime) {
        this.mStableTime = stableTime;
    }

    public void setCurrentTime(long currentTime) {
        this.mCurrentTime = currentTime;
    }

    public long getIteratedTime() {
        return mIteratedTime;
    }

    public void setIterateTime() {
        mIteratedTime =  mCurrentTime - mStableTime;
    }
}