package com.example.tragic.irate.simple.stopwatch.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.tragic.irate.simple.stopwatch.Cycles;

import java.util.List;

@Dao
public interface CyclesDao {

    @Query("SELECT * from Cycles WHERE Id=:listID")
    List<Cycles> loadCycleEntry (int listID);

    @Query("SELECT * from Cycles")
    List<Cycles> loadAllCycles();

    @Query("SELECT id from Cycles")
    List<Cycles> loadAllIds();

    @Insert
    void insertCycle(Cycles cycles);

    @Delete
    void deleteCycle(Cycles cycles);

    @Query("DELETE from Cycles")
    void deleteAll();


//    @Query("SELECT sets from Cycles WHERE Id=:listID")
//    List<Cycles> loadSingleSet (int listID);
//
//    @Query("SELECT breaks from Cycles WHERE Id=:listID")
//    List<Cycles> loadSingleBreak (int listID);
//
//
//    @Query("SELECT sets from Cycles")
//    List<Cycles> loadAllSets();
//
//    @Query("SELECT breaks from Cycles")
//    List<Cycles> loadAllBreaks();
//
}