package com.example.tragic.irate.simple.stopwatch;

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
    public int itemCount;

    public Cycles() {
    }

    public Cycles(String sets, String breaks) {
        this.sets = sets; this.breaks = breaks;
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

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
}
