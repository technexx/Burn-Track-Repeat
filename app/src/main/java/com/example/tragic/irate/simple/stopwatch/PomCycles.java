package com.example.tragic.irate.simple.stopwatch;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "PomCycles")
public class PomCycles {
    @PrimaryKey (autoGenerate = true)

    public int id;
    public long workTime;
    public long miniBreak;
    public long fullBreak;
    public long timeAdded;

    public PomCycles() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getWorkTime() {
        return workTime;
    }

    public void setWorkTime(long workTime) {
        this.workTime = workTime;
    }

    public long getMiniBreak() {
        return miniBreak;
    }

    public void setMiniBreak(long miniBreak) {
        this.miniBreak = miniBreak;
    }

    public long getFullBreak() {
        return fullBreak;
    }

    public void setFullBreak(long fullBreak) {
        this.fullBreak = fullBreak;
    }

    public long getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(long timeAdded) {
        this.timeAdded = timeAdded;
    }
}
