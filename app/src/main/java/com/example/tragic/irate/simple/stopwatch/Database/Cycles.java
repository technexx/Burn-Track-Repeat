package com.example.tragic.irate.simple.stopwatch.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "Cycles")
public class Cycles {
    @PrimaryKey (autoGenerate = true)

    public int id;
    public String title;
    public String workoutRounds;
    public String roundType;

    public long totalSetTime;
    public long totalBreakTime;
    public int cyclesCompleted;

    public long timeAdded;
    public long timeAccessed;
    public int itemCount;

    public boolean tdeeActivityExists;
    public int tdeeCatPosition;
    public int tdeeSubCatPosition;
    public int tdeeValuePosition;

//    //Used for ease of viewing database entries.
    public String activityString;

    public Cycles() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWorkoutRounds() {
        return workoutRounds;
    }

    public void setWorkoutRounds(String workoutRounds) {
        this.workoutRounds = workoutRounds;
    }

    public String getRoundType() {
        return roundType;
    }

    public void setRoundType(String roundType) {
        this.roundType = roundType;
    }

    public long getTotalSetTime() {
        return totalSetTime;
    }

    public void setTotalSetTime(long totalSetTime) {
        this.totalSetTime = totalSetTime;
    }

    public long getTotalBreakTime() {
        return totalBreakTime;
    }

    public void setTotalBreakTime(long totalBreakTime) {
        this.totalBreakTime = totalBreakTime;
    }

    public int getCyclesCompleted() {
        return cyclesCompleted;
    }

    public void setCyclesCompleted(int cyclesCompleted) {
        this.cyclesCompleted = cyclesCompleted;
    }

    public void setTimeAdded(long timeAdded) {
        this.timeAdded = timeAdded;
    }

    public void setTimeAccessed(long timeAccessed) {
        this.timeAccessed = timeAccessed;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public boolean getTdeeActivityExists() {
        return tdeeActivityExists;
    }

    public void setTdeeActivityExists(boolean tdeeActivityExists) {
        this.tdeeActivityExists = tdeeActivityExists;
    }

    public int getTdeeCatPosition() {
        return tdeeCatPosition;
    }

    public void setTdeeCatPosition(int tdeeCatPosition) {
        this.tdeeCatPosition = tdeeCatPosition;
    }

    public int getTdeeSubCatPosition() {
        return tdeeSubCatPosition;
    }

    public void setTdeeSubCatPosition(int tdeeSubCatPosition) {
        this.tdeeSubCatPosition = tdeeSubCatPosition;
    }

    public int getTdeeValuePosition() {
        return tdeeValuePosition;
    }

    public void setTdeeValuePosition(int tdeeValuePosition) {
        this.tdeeValuePosition = tdeeValuePosition;
    }

    public String getActivityString() {
        return activityString;
    }

    public void setActivityString(String activityString) {
        this.activityString = activityString;
    }
}
