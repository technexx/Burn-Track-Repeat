package com.example.tragic.irate.simple.stopwatch;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "CyclesBO")
public class CyclesBO {
    @PrimaryKey (autoGenerate = true)

    public int id;
    public String breaksOnly;

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
}
