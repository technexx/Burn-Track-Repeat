package com.example.tragic.irate.simple.stopwatch.Database.DayStatClasses;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "PomCycleStats")

public class PomCycleStats {
    @PrimaryKey
    public long pomCycleStatsId;

    public long totalSetTime;
    public long totalBreakTime;

    public long getPomCycleStatsId() {
        return pomCycleStatsId;
    }

    public void setPomCycleStatsId(long pomCycleStatsId) {
        this.pomCycleStatsId = pomCycleStatsId;
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
}
