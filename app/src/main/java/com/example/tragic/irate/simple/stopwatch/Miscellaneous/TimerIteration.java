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

    //Todo: We just need a simple return method for difference between stable time and current time, and then add that to our total time.
    public long getDifference(long stableTime, long currentTime) {
        return currentTime - stableTime;
    }

}