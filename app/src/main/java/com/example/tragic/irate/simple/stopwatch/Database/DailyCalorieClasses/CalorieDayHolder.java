package com.example.tragic.irate.simple.stopwatch.Database.DailyCalorieClasses;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CalorieDayHolder")
public class CalorieDayHolder {

    @PrimaryKey
    public long calorieDaySelectedId;

    double caloriesConsumed;

    public long getCalorieDaySelectedId() {
        return calorieDaySelectedId;
    }

    public void setCalorieDaySelectedId(long calorieDaySelectedId) {
        this.calorieDaySelectedId = calorieDaySelectedId;
    }

    public double getCaloriesConsumed() {
        return caloriesConsumed;
    }

    public void setCaloriesConsumed(double caloriesConsumed) {
        this.caloriesConsumed = caloriesConsumed;
    }


}
