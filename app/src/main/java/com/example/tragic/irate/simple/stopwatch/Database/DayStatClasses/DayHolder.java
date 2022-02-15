package com.example.tragic.irate.simple.stopwatch.Database.DayStatClasses;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "DayHolder")
public class DayHolder {

    @PrimaryKey
    //Can just use 1-365 (6).
    long daySelectedId;
    String date;
    long totalSetTime;
    long totalBreakTime;
    double totalCaloriesBurned;
    @Ignore List<CycleStats> cycleStatsList;

    public long getDayId() {
        return daySelectedId;
    }

    public void setDayId(long dayId) {
        this.daySelectedId = dayId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public List<CycleStats> getCycleStatsList() {
        return cycleStatsList;
    }

    public void setCycleStatsList(List<CycleStats> cycleStatsList) {
        this.cycleStatsList = cycleStatsList;
    }
}
