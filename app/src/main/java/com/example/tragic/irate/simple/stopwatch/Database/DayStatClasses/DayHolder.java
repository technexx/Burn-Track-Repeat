package com.example.tragic.irate.simple.stopwatch.Database.DayStatClasses;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "DayHolder")
public class DayHolder {

    @PrimaryKey (autoGenerate = true)
    public long daySelectedId;
    public String date;

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
