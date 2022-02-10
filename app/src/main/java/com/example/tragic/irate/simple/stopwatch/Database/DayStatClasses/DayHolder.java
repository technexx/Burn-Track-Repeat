package com.example.tragic.irate.simple.stopwatch.Database.DayStatClasses;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "DayHolder")
public class DayHolder {

    @PrimaryKey (autoGenerate = true)
    //Can just use 1-365 (6).
    public long daySelectedId;
    public String date;
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
}
