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
    public int cyclesCompleted;

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

    public int getCyclesCompleted() {
        return cyclesCompleted;
    }

    public void setCyclesCompleted(int cyclesCompleted) {
        this.cyclesCompleted = cyclesCompleted;
    }
}