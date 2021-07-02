package com.example.tragic.irate.simple.stopwatch.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "PomCycles")
public class PomCycles {
    @PrimaryKey (autoGenerate = true)

    public int id;
    public String title;
    public String fullCycle;
    public long timeAdded;
    public long timeAccessed;
    public int cyclesCompleted;
    public int totalWorkTime;
    public int totalBreakTime;

    public PomCycles() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFullCycle() {
        return fullCycle;
    }

    public void setFullCycle(String fullCycle) {
        this.fullCycle = fullCycle;
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

    public int getCyclesCompleted() {
        return cyclesCompleted;
    }

    public void setCyclesCompleted(int cyclesCompleted) {
        this.cyclesCompleted = cyclesCompleted;
    }

    public int getTotalWorkTime() {
        return totalWorkTime;
    }

    public void setTotalWorkTime(int totalWorkTime) {
        this.totalWorkTime = totalWorkTime;
    }

    public int getTotalBreakTime() {
        return totalBreakTime;
    }

    public void setTotalBreakTime(int totalBreakTime) {
        this.totalBreakTime = totalBreakTime;
    }
}