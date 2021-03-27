package com.example.tragic.irate.simple.stopwatch.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.tragic.irate.simple.stopwatch.Cycles;
import com.example.tragic.irate.simple.stopwatch.CyclesBO;
import com.example.tragic.irate.simple.stopwatch.PomCycles;

import java.util.List;

@Dao
public interface CyclesDao {

    @Query("SELECT * from Cycles")
    List<Cycles> loadAllCycles();

    @Query("SELECT * from Cycles where id=:listID")
    List<Cycles> loadSingleCycle(int listID);

    @Query("SELECT * from CyclesBO where id=:listID")
    List<CyclesBO> loadSingleCycleBO(int listID);

    @Query("SELECT * from CyclesBO")
    List<CyclesBO> loadAllBOCycles();

    @Query("SELECT * from PomCycles")
    List<PomCycles> loadAllPomCycles();

    @Query("SELECT * from CYCLES ORDER by timeAdded DESC")
    List<Cycles> loadCyclesMostRecent();

    @Query("SELECT * from CYCLES ORDER by timeAdded ASC")
    List<Cycles> loadCycleLeastRecent();

    @Query("SELECT * from CYCLES ORDER by itemCount DESC")
    List<Cycles> loadCyclesMostItems();

    @Query("SELECT * from CYCLES ORDER by itemCount ASC")
    List<Cycles> loadCyclesLeastItems();

    @Query("SELECT * from CYCLESBO ORDER by timeAdded DESC")
    List<CyclesBO> loadCyclesMostRecentBO();

    @Query("SELECT * from CYCLESBO ORDER by timeAdded ASC")
    List<CyclesBO> loadCycleLeastRecentBO();

    @Query("SELECT * from CYCLESBO ORDER by itemCount DESC")
    List<CyclesBO> loadCyclesMostItemsBO();

    @Query("SELECT * from CYCLESBO ORDER by itemCount ASC")
    List<CyclesBO> loadCyclesLeastItemsBO();

    @Query("SELECT * from POMCYCLES ORDER by timeAdded DESC")
    List<PomCycles> loadPomCyclesMostItems();

    @Query("SELECT * from POMCYCLES ORDER by timeAdded ASC")
    List<PomCycles> loadPomCyclesLeastItems();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertCycle(Cycles cycles);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertBOCycle(CyclesBO cyclesBO);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertPomCycle(PomCycles pomCycles);

    

    @Query("UPDATE cycles set title=:newTitle WHERE id=:listID")
    void updateCustomTitle(String newTitle, int listID);

    @Update
    void updateCycles(Cycles cycles);

    @Update
    void updateBOCycles(CyclesBO cyclesBO);

    @Delete
    void deleteCycle(Cycles cycles);

    @Delete
    void deleteBOCycle(CyclesBO cyclesBO);

    @Delete
    void deletePomCycle(PomCycles pomCycles);

    @Query("DELETE from Cycles")
    void deleteAll();

    @Query("DELETE from CyclesBO")
    void deleteAllBO();

    @Query("DELETE from PomCycles")
    void deleteAllPomCycles();
}