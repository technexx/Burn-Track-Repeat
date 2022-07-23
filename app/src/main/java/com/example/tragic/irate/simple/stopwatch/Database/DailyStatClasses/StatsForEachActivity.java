 package com.example.tragic.irate.simple.stopwatch.Database.DailyStatClasses;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "StatsForEachActivity")

public class StatsForEachActivity {
    @PrimaryKey (autoGenerate = true)
    public long statsForActivityId;
    public long uniqueIdTiedToYear;
    public long uniqueIdTiedToTheSelectedActivity;

    String activity;
    long totalSetTimeForEachActivity;
    long totalBreakTimeForEachActivity;
    double totalCaloriesBurnedForEachActivity;
    double metScore;

    public boolean customActivity;
    double caloriesPerHour;


    public long getStatsForActivityId() {
        return statsForActivityId;
    }

    public void setStatsForActivityId(long statsForActivityId) {
        this.statsForActivityId = statsForActivityId;
    }

    public long getUniqueIdTiedToYear() {
        return uniqueIdTiedToYear;
    }

    public void setUniqueIdTiedToYear(long uniqueIdTiedToYear) {
        this.uniqueIdTiedToYear = uniqueIdTiedToYear;
    }

    public long getUniqueIdTiedToTheSelectedActivity() {
        return uniqueIdTiedToTheSelectedActivity;
    }

    public void setUniqueIdTiedToTheSelectedActivity(long uniqueIdTiedToTheSelectedActivity) {
        this.uniqueIdTiedToTheSelectedActivity = uniqueIdTiedToTheSelectedActivity;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public long getTotalSetTimeForEachActivity() {
        return totalSetTimeForEachActivity;
    }

    public void setTotalSetTimeForEachActivity(long totalSetTimeForEachActivity) {
        this.totalSetTimeForEachActivity = totalSetTimeForEachActivity;
    }

    public long getTotalBreakTimeForEachActivity() {
        return totalBreakTimeForEachActivity;
    }

    public void setTotalBreakTimeForEachActivity(long totalBreakTimeForEachActivity) {
        this.totalBreakTimeForEachActivity = totalBreakTimeForEachActivity;
    }

    public double getTotalCaloriesBurnedForEachActivity() {
        return totalCaloriesBurnedForEachActivity;
    }

    public void setTotalCaloriesBurnedForEachActivity(double totalCaloriesBurnedForEachActivity) {
        this.totalCaloriesBurnedForEachActivity = totalCaloriesBurnedForEachActivity;
    }

    public double getMetScore() {
        return metScore;
    }

    public void setMetScore(double metScore) {
        this.metScore = metScore;
    }

    public double getCaloriesPerHour() {
        return caloriesPerHour;
    }

    public void setCaloriesPerHour(double caloriesPerHour) {
        this.caloriesPerHour = caloriesPerHour;
    }

    public boolean getIsCustomActivity() {
        return customActivity;
    }

    public void setIsCustomActivity(boolean customActivity) {
        this.customActivity = customActivity;
    }
}