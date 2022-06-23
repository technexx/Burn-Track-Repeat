package com.example.tragic.irate.simple.stopwatch.Database.DailyCalorieClasses;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CalorieDayHolder")
public class CalorieDayHolder {

    @PrimaryKey
    public long calorieDaySelectedId;

    double foodConsumed;
    double foodPortion;
    double caloriesConsumed;

    public long getCalorieDaySelectedId() {
        return calorieDaySelectedId;
    }

    public void setCalorieDaySelectedId(long calorieDaySelectedId) {
        this.calorieDaySelectedId = calorieDaySelectedId;
    }

    public double getFoodConsumed() {
        return foodConsumed;
    }

    public void setFoodConsumed(double foodConsumed) {
        this.foodConsumed = foodConsumed;
    }

    public double getFoodPortion() {
        return foodPortion;
    }

    public void setFoodPortion(double foodPortion) {
        this.foodPortion = foodPortion;
    }

    public double getCaloriesConsumed() {
        return caloriesConsumed;
    }

    public void setCaloriesConsumed(double caloriesConsumed) {
        this.caloriesConsumed = caloriesConsumed;
    }


}
