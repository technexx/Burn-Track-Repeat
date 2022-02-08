package com.example.tragic.irate.simple.stopwatch.Database;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class DayWithCycleStats {
    @Embedded public DayHolder dayHolder;
    @Relation(
            parentColumn = "dayId",
            entityColumn = "cycleStatsId"
    )

    public List<CycleStats> cycleStatsList;
}
