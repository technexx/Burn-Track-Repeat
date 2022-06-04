package com.example.tragic.irate.simple.stopwatch.Database.DailyStatClasses;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "DayHolder")
public class DayHolder {

    @PrimaryKey
    public long daySelectedId;
    public long uniqueIdTiedToYear;
    String date;

    long totalSetTime;
    long totalBreakTime;
    double totalCaloriesBurned;

    long totalWorkTime;
    long totalRestTime;

    int cyclesCompletedForModeOne;
    int cyclesCompletedForModeThree;

    public long getDayId() {
        return daySelectedId;
    }

    public void setDayId(long dayId) {
        this.daySelectedId = dayId;
    }

    public long getUniqueIdTiedToYear() {
        return uniqueIdTiedToYear;
    }

    public void setUniqueIdTiedToYear(long uniqueIdTiedToYear) {
        this.uniqueIdTiedToYear = uniqueIdTiedToYear;
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

    public int getCyclesCompletedForModeOne() {
        return cyclesCompletedForModeOne;
    }

    public void setCyclesCompletedForModeOne(int cyclesCompleted) {
        this.cyclesCompletedForModeOne = cyclesCompleted;
    }

    public long getTotalWorkTime() {
        return totalWorkTime;
    }

    public void setTotalWorkTime(long totalWorkTime) {
        this.totalWorkTime = totalWorkTime;
    }

    public long getTotalRestTime() {
        return totalRestTime;
    }

    public void setTotalRestTime(long totalRestTime) {
        this.totalRestTime = totalRestTime;
    }

    public int getCyclesCompletedForModeThree() {
        return cyclesCompletedForModeThree;
    }

    public void setCyclesCompletedForModeThree(int cyclesCompletedForModeThree) {
        this.cyclesCompletedForModeThree = cyclesCompletedForModeThree;
    }
}
