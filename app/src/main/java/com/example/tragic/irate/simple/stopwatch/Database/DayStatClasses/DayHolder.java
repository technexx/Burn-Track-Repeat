package com.example.tragic.irate.simple.stopwatch.Database.DayStatClasses;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "DayHolder")
public class DayHolder {

    @PrimaryKey
    //Can just use 1-365 (6).
    long daySelectedId;
    String date;
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
