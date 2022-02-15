package com.example.tragic.irate.simple.stopwatch.Database.DayStatClasses;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "CycleStats")

//DATE ->>> ACTIVITY         ->>>>>>>>> TOTAL ACTIVITY SET TIME
//          TOTAL SET TIME              TOTAL ACTIVITY BREAK TIME
//          TOTAL BREAK TIME            TOTAL CALORIES BURNED

public class CycleStats {
    @PrimaryKey (autoGenerate = true)
    long cycleStatsId;
    long uniqueDayIdPossessedByEachOfItsActivities;

    long totalSetTime;
    long totalBreakTime;
    double totalCaloriesBurned;

    public long getCycleStatsId() {
        return cycleStatsId;
    }

    public void setCycleStatsId(long cycleStatsId) {
        this.cycleStatsId = cycleStatsId;
    }

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

    public double getTotalCaloriesBurned() {
        return totalCaloriesBurned;
    }

    public void setTotalCaloriesBurned(double totalCaloriesBurned) {
        this.totalCaloriesBurned = totalCaloriesBurned;
    }
}
