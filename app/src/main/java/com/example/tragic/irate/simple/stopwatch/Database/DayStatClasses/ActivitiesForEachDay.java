package com.example.tragic.irate.simple.stopwatch.Database.DayStatClasses;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "ActivitiesForEachDay")

//DATE ->>> ACTIVITY         ->>>>>>>>> TOTAL ACTIVITY SET TIME
//          TOTAL SET TIME              TOTAL ACTIVITY BREAK TIME
//          TOTAL BREAK TIME            TOTAL CALORIES BURNED

public class ActivitiesForEachDay {
    @PrimaryKey (autoGenerate = true)
    public long cycleStatsId;
    public long uniqueDayIdPossessedByEachOfItsActivities;
    String activity;

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

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }
}
