package com.example.tragic.irate.simple.stopwatch.Miscellaneous;

public class TimerIteration {
    long mStableTime;
    long mCurrentTime;
    long mTimeToIterate;

    public TimerIteration(long stableTime) {
        this.mStableTime = stableTime;
    }

    public void setCurrentTime(long currentTime) {
        this.mCurrentTime = currentTime;
    }

    public long getIteratedTime() {
        return (mCurrentTime - mStableTime);
    }
}