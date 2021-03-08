package com.example.tragic.irate.simple.stopwatch;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "CyclesBO")
public class CyclesBO {
    @PrimaryKey (autoGenerate = true)

    public int id;
    public String breaksOnly;
    public long timeAdded;
    public int itemCount;

    public CyclesBO() {
    }

    public CyclesBO(String breaksOnly) {
        this.breaksOnly = breaksOnly;
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

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
}
