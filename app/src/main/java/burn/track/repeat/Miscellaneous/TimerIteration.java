package burn.track.repeat.Miscellaneous;

public class TimerIteration {
    long mStableTime;
    long mCurrentTime;

    long mPreviousTotal;
    long mNewTotal;

    long mPreviousDailyTotal;
    long mNewDailyTotal;

    long mPreviousActivityTotal;
    long mNewActivityTotal;

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

    public long getPreviousDailyTotal() {
        return mPreviousDailyTotal;
    }

    public void setPreviousDailyTotal(long previousTotal) {
        this.mPreviousDailyTotal = previousTotal;
    }

    public long getNewDailyTotal() {
        return mNewDailyTotal;
    }

    public void setNewDailyTotal(long newTotal) {
        this.mNewDailyTotal = newTotal;
    }

    public long getPreviousActivityTotal() {
        return mPreviousActivityTotal;
    }

    public void setPreviousActivityTotal(long previousActivityTotal) {
        this.mPreviousActivityTotal = previousActivityTotal;
    }

    public long getNewActivityTotal() {
        return mNewActivityTotal;
    }

    public void setNewActivityTotal(long newActivityTotal) {
        this.mNewActivityTotal = newActivityTotal;
    }
}