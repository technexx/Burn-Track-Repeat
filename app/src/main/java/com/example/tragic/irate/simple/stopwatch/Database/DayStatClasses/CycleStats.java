package com.example.tragic.irate.simple.stopwatch.Database.DayStatClasses;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CycleStats")

//Todo: For a day (one class), each activity, with its time and calorie stats (second class).

public class CycleStats {
    @PrimaryKey public long cycleStatsId;

    //Total times for day irrespective of activity or lack thereof.
    public long totalSetTime;
    public long totalBreakTime;

    ///These will have to be concatenated Strings (can't access List class through Room) to get multiple entries. Each String should split into synchronous String[] objects.
    public String activity;
    public long totalTdeeSetTime;
    public long totalTdeeBreakTime;
    public long totalTdeeTime;
    public double totalCaloriesBurned;

    public long getCycleStatsId() {
        return cycleStatsId;
    }

    public void setCycleStatsId(long cycleStatsId) {
        this.cycleStatsId = cycleStatsId;
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

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public long getTotalTdeeSetTime() {
        return totalTdeeSetTime;
    }

    public void setTotalTdeeSetTime(long totalTdeeSetTime) {
        this.totalTdeeSetTime = totalTdeeSetTime;
    }

    public long getTotalTdeeBreakTime() {
        return totalTdeeBreakTime;
    }

    public void setTotalTdeeBreakTime(long totalTdeeBreakTime) {
        this.totalTdeeBreakTime = totalTdeeBreakTime;
    }

    public long getTotalTdeeTime() {
        return totalTdeeTime;
    }

    public void setTotalTdeeTime(long totalTdeeTime) {
        this.totalTdeeTime = totalTdeeTime;
    }

    public double getTotalCaloriesBurned() {
        return totalCaloriesBurned;
    }

    public void setTotalCaloriesBurned(double totalCaloriesBurned) {
        this.totalCaloriesBurned = totalCaloriesBurned;
    }
}
