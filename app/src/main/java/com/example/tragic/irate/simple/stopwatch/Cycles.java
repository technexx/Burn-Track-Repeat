package com.example.tragic.irate.simple.stopwatch;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "Cycles")
public class Cycles {
    @PrimaryKey (autoGenerate = true)

    public int id;
    public String sets;
    public String breaks;

    public Cycles() {
    }

    public Cycles(String sets, String breaks) {
        this.sets = sets; this.breaks = breaks;
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
}
