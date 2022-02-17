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
    public long activitiesForEachDayId;
    public long uniqueDayIdPossessedByEachOfItsActivities;
    //Used only to compare Strings in database fetch.
    String activity;

    public long getActivitiesForEachDayId() {
        return activitiesForEachDayId;
    }

    public void setActivitiesForEachDayId(long activitiesForEachDayId) {
        this.activitiesForEachDayId = activitiesForEachDayId;
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