package com.example.tragic.irate.simple.stopwatch.Database.DayStatClasses;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "DayHolder")
public class DayHolder {

    @PrimaryKey
    public long daySelectedId;
    String date;
    long totalSetTime;
    long totalBreakTime;
    double totalCaloriesBurned;
    @Ignore List<ActivitiesForEachDay> activitiesForEachDayList;

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

    public List<ActivitiesForEachDay> getActivitiesForEachDayList() {
        return activitiesForEachDayList;
    }

    public void setActivitiesForEachDayList(List<ActivitiesForEachDay> activitiesForEachDayList) {
        this.activitiesForEachDayList = activitiesForEachDayList;
    }
}
