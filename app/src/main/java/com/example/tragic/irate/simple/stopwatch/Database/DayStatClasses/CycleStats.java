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

    long totalActivitySetTime;
    long totalActivityBreakTime;
    double totalActivityCaloriesBurned;

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

    public long getTotalActivitySetTime() {
        return totalActivitySetTime;
    }

    public void setTotalActivitySetTime(long totalActivitySetTime) {
        this.totalActivitySetTime = totalActivitySetTime;
    }

    public long getTotalActivityBreakTime() {
        return totalActivityBreakTime;
    }

    public void setTotalActivityBreakTime(long totalActivityBreakTime) {
        this.totalActivityBreakTime = totalActivityBreakTime;
    }

    public double getTotalActivityCaloriesBurned() {
        return totalActivityCaloriesBurned;
    }

    public void setTotalActivityCaloriesBurned(double totalActivityCaloriesBurned) {
        this.totalActivityCaloriesBurned = totalActivityCaloriesBurned;
    }
}
