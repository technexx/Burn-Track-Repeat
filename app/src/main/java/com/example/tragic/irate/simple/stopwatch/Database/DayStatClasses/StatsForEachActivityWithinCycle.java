package com.example.tragic.irate.simple.stopwatch.Database.DayStatClasses;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "StatsForEachActivityWithinCycle")

public class StatsForEachActivityWithinCycle {
    @PrimaryKey(autoGenerate = true)
    public long statsForActivityId;
    public long uniqueActivityIdPossessedByEachOfItsStats;

    public long totalTdeeSetTime;
    public long totalTdeeBreakTime;
    public double totalCaloriesBurned;

    public long getStatsForActivityId() {
        return statsForActivityId;
    }

    public void setStatsForActivityId(long statsForActivityId) {
        this.statsForActivityId = statsForActivityId;
    }

    public long getUniqueActivityIdPossessedByEachOfItsStats() {
        return uniqueActivityIdPossessedByEachOfItsStats;
    }

    public void setUniqueActivityIdPossessedByEachOfItsStats(long uniqueActivityIdPossessedByEachOfItsStats) {
        this.uniqueActivityIdPossessedByEachOfItsStats = uniqueActivityIdPossessedByEachOfItsStats;
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
