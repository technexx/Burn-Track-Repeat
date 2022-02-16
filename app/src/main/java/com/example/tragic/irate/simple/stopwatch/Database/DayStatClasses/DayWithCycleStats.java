package com.example.tragic.irate.simple.stopwatch.Database.DayStatClasses;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class DayWithCycleStats {
    @Embedded public DayHolder dayHolder;
    @Relation(
//            entity = DayHolder.class,
            parentColumn = "daySelectedId",
            entityColumn = "uniqueDayIdPossessedByEachOfItsActivities"
    )

    public List<ActivitiesForEachDay> activitiesForEachDayList;
}