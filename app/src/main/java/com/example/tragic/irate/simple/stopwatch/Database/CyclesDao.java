package com.example.tragic.irate.simple.stopwatch.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.tragic.irate.simple.stopwatch.Cycles;
import com.example.tragic.irate.simple.stopwatch.CyclesBO;

import java.util.List;

@Dao
public interface CyclesDao {

    @Query("SELECT * from Cycles WHERE Id=:listID")
    List<Cycles> loadCycleEntry (int listID);

    @Query("SELECT * from Cycles")
    List<Cycles> loadAllCycles();

    @Query("SELECT id from Cycles")
    List<Cycles> loadAllIds();

//    @Query("SELECT sets AND breaks from Cycles")
//    List<Cycles> getCustomSetsAndBreaks();

//    @Query("SELECT breaksOnly from CyclesBO")
//    List<Cycles> getBreaksOnlyBreaks();

    @Insert
    void insertCycle(Cycles cycles);

    @Delete
    void deleteCycle(Cycles cycles);

    @Query("DELETE from Cycles")
    void deleteAll();

    @Query("SELECT * from CyclesBO where Id=:listID")
    List<CyclesBO> loadCyclesBOEntry (int listID);

    @Query("SELECT * from CyclesBO")
    List<CyclesBO> loadAllBOCycles();

    @Insert
    void insertBOCycle(CyclesBO cyclesBO);

    @Query("DELETE from CyclesBO")
    void deleteAllBO();
}