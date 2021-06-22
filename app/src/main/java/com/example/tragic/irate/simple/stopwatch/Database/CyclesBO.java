package com.example.tragic.irate.simple.stopwatch.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "CyclesBO")
public class CyclesBO {
    @PrimaryKey (autoGenerate = true)

    public int id;
    public String title;
    public String breaksOnly;
    public long timeAdded;
    public long timeAccessed;
    public int itemCount;
    public int totalBOTime;
    public int cyclesCompleted;

    public CyclesBO() {
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

    public String getBreaksOnly() {
        return breaksOnly;
    }

    public void setBreaksOnly(String breaksOnly) {
        this.breaksOnly = breaksOnly;
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

    public int getTotalBOTime() {
        return totalBOTime;
    }

    public void setTotalBOTime(int totalBOTime) {
        this.totalBOTime = totalBOTime;
    }

    public int getCyclesCompleted() {
        return cyclesCompleted;
    }

    public void setCyclesCompleted(int cyclesCompleted) {
        this.cyclesCompleted = cyclesCompleted;
    }
}
