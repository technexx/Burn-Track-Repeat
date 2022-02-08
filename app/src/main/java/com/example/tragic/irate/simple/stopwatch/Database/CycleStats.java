package com.example.tragic.irate.simple.stopwatch.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CycleTotalDailyStats")

//Todo: For a day (one class), each activity, with its time and calorie stats (second class).

public class CycleStats {
    @PrimaryKey public long cycleStatsId;

    //Total times for day irrespective of activity or lack thereof.
    public long totalSetTime;
    public long totalBreakTime;

    ///These will have to be concatenated Strings (can't access List class through Room) to get multiple entries. Each String should split into synchronous String[] objects.
    public String activity;
    public long totalTdeeSetTime;
    public long totalTdeeBreakTime;
    public long totalTdeeTime;
    public double totalCaloriesBurned;
}
