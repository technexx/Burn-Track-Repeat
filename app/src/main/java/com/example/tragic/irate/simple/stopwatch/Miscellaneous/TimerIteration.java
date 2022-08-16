package com.example.tragic.irate.simple.stopwatch.Miscellaneous;

public class TimerIteration {
    long mStableTime;
    long mCurrentTime;
    long mIteratedTime;
    long mPreviousTotal;
    long mNewTotal;

    //Todo: nextRound() needs logic.
    //Todo: For activity stats, include logic in runnables so pause/resume can remain untouched. Will have to include non-infinity runnables if doing it this way, though.
    public TimerIteration() {
    }

    public void setStableTime () {
        mStableTime = System.currentTimeMillis();
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