package com.example.tragic.irate.simple.stopwatch.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
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

    @Query("SELECT * from CYCLES ORDER by timeAdded DESC")
    List<Cycles> loadCyclesMostRecent();

    @Query("SELECT * from CYCLES ORDER by timeAdded ASC")
    List<Cycles> loadCycleLeastRecent();

    @Query("SELECT * from CYCLES ORDER by itemCount DESC")
    List<Cycles> loadCyclesMostItems();

    @Query("SELECT * from CYCLES ORDER by itemCount ASC")
    List<Cycles> loadCyclesLeastItems();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertCycle(Cycles cycles);

    @Delete
    void deleteCycle(Cycles cycles);

    @Delete
    void deleteBOCycle(CyclesBO cyclesBO);

    @Query("DELETE from Cycles")
    void deleteAll();

    @Query("SELECT * from CyclesBO where Id=:listID")
    List<CyclesBO> loadCyclesBOEntry (int listID);

    @Query("SELECT * from CyclesBO")
    List<CyclesBO> loadAllBOCycles();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertBOCycle(CyclesBO cyclesBO);

    @Query("DELETE from CyclesBO")
    void deleteAllBO();
}