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

    public long timeAdded;
    public long timeAccessed;
    public int itemCount;
    public int cyclesCompleted;
    public int totalSetTime;
    public int totalBreakTime;

    public boolean tdeeActivityExists;
    public int tdeeCatPosition;
    public int tdeeSubCatPosition;
    public int tdeeValuePosition;

    public long totalTdeeActivityTimeElapsed;
    public double totalCaloriesBurned;

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

    public void setTimeAdded(long timeAdded) {
        this.timeAdded = timeAdded;
    }

    public void setTimeAccessed(long timeAccessed) {
        this.timeAccessed = timeAccessed;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public int getCyclesCompleted() {
        return cyclesCompleted;
    }

    public void setCyclesCompleted(int cyclesCompleted) {
        this.cyclesCompleted = cyclesCompleted;
    }

    public int getTotalSetTime() {
        return totalSetTime;
    }

    public void setTotalSetTime(int totalSetTime) {
        this.totalSetTime = totalSetTime;
    }

    public int getTotalBreakTime() {
        return totalBreakTime;
    }

    public void setTotalBreakTime(int totalBreakTime) {
        this.totalBreakTime = totalBreakTime;
    }

    public long getTotalTdeeActivityTimeElapsed() {
        return totalTdeeActivityTimeElapsed;
    }

    public void setTotalTdeeActivityTimeElapsed(long totalTdeeActivityTimeElapsed) {
        this.totalTdeeActivityTimeElapsed = totalTdeeActivityTimeElapsed;
    }

    public double getTotalCaloriesBurned() {
        return totalCaloriesBurned;
    }

    public void setTotalCaloriesBurned(double totalCaloriesBurned) {
        this.totalCaloriesBurned = totalCaloriesBurned;
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
}
