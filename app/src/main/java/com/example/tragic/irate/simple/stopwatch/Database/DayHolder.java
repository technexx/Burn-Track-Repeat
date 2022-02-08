package com.example.tragic.irate.simple.stopwatch.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "DayHolder")
public class DayHolder {

    @PrimaryKey public long dayId;
    public String date;

}
