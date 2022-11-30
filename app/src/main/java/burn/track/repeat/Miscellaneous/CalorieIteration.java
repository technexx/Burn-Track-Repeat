package burn.track.repeat.Miscellaneous;

public class CalorieIteration {
    long mCurrentTime;

    double mPreviousActivityCalories;
    double mNewActivityCalories;
    double mPreviousTotalCalories;
    double mNewTotalCalories;

    public CalorieIteration() {
    }

    public long getCurrentTime() {
        return mCurrentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.mCurrentTime = currentTime;
    }

    public double getPreviousActivityCalories() {
        return mPreviousActivityCalories;
    }

    public void setPreviousActivityCalories(double previousActivityCalories) {
        this.mPreviousActivityCalories = previousActivityCalories;
    }

    public double getNewActivityCalories() {
        return mNewActivityCalories;
    }

    public void setNewActivityCalories(double newActivityCalories) {
        this.mNewActivityCalories = newActivityCalories;
    }

    public double getPreviousTotalCalories() {
        return mPreviousTotalCalories;
    }

    public void setPreviousTotalCalories(double previousTotalCalories) {
        this.mPreviousTotalCalories = previousTotalCalories;
    }

    public double getNewTotalCalories() {
        return mNewTotalCalories;
    }

    public void setNewTotalCalories(double newTotalCalories) {
        this.mNewTotalCalories = newTotalCalories;
    }
}
