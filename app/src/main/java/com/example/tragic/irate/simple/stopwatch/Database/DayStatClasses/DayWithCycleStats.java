package com.example.tragic.irate.simple.stopwatch.Database.DayStatClasses;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Relation;

import java.util.List;

public class DayWithCycleStats {
    @Embedded public DayHolder dayHolder;
    @Relation(
            parentColumn = "dayId",
            entityColumn = "cycleStatsId"
    )

    public List<CycleStats> cycleStatsList;

    public DayHolder getDayHolder() {
        return dayHolder;
    }

    public void setDayHolder(DayHolder dayHolder) {
        this.dayHolder = dayHolder;
    }

    public List<CycleStats> getCycleStatsList() {
        return cycleStatsList;
    }

    public void setCycleStatsList(List<CycleStats> cycleStatsList) {
        this.cycleStatsList = cycleStatsList;
    }
}