package com.example.tragic.irate.simple.stopwatch.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "Cycles")
public class Cycles {
    @PrimaryKey (autoGenerate = true)

    public int id;
    public String title;
    public String sets;
    public String breaks;
    public long timeAdded;
    public long timeAccessed;
    public int itemCount;
    public int cyclesCompleted;
    public int totalSetTime;
    public int totalBreakTime;


    public Cycles() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSets() {
        return sets;
    }

    public void setSets(String sets) {
        this.sets = sets;
    }

    public String getBreaks() {
        return breaks;
    }

    public void setBreaks(String breaks) {
        this.breaks = breaks;
    }

    public long getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(long timeAdded) {
        this.timeAdded = timeAdded;
    }

    public long getTimeAccessed() {
        return timeAccessed;
    }

    public void setTimeAccessed(long timeAccessed) {
        this.timeAccessed = timeAccessed;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public int getCyclesCompleted() {
        return cyclesCompleted;
    }

    public void setCyclesCompleted(int cyclesCompleted) {
        this.cyclesCompleted = cyclesCompleted;
    }

    public int getTotalSetTime() {
        return totalSetTime;
    }

    public void setTotalSetTime(int totalSetTime) {
        this.totalSetTime = totalSetTime;
    }

    public int getTotalBreakTime() {
        return totalBreakTime;
    }

    public void setTotalBreakTime(int totalBreakTime) {
        this.totalBreakTime = totalBreakTime;
    }
}
