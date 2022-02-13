package com.example.tragic.irate.simple.stopwatch.Database.DayStatClasses;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CycleStats")

//DATE ->>> ACTIVITY         ->>>>>>>>> TOTAL ACTIVITY SET TIME
//          TOTAL SET TIME              TOTAL ACTIVITY BREAK TIME
//          TOTAL BREAK TIME            TOTAL CALORIES BURNED

public class CycleStats {
    @PrimaryKey (autoGenerate = true)
    public long cycleStatsId;
    public long uniqueDayIdPossessedByEachOfItsActivities;

    public long totalSetTime;
    public long totalBreakTime;

    public String activity;
    public long totalTdeeSetTime;
    public long totalTdeeBreakTime;
    public double totalCaloriesBurned;

    public long getUniqueDayIdPossessedByEachOfItsActivities() {
        return uniqueDayIdPossessedByEachOfItsActivities;
    }

    public void setUniqueDayIdPossessedByEachOfItsActivities(long cycleStatsId) {
        this.uniqueDayIdPossessedByEachOfItsActivities = cycleStatsId;
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

    public double getTotalCaloriesBurned() {
        return totalCaloriesBurned;
    }

    public void setTotalCaloriesBurned(double totalCaloriesBurned) {
        this.totalCaloriesBurned = totalCaloriesBurned;
    }
}
