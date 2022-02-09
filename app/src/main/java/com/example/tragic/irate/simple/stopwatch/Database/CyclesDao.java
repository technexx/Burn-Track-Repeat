package com.example.tragic.irate.simple.stopwatch.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.tragic.irate.simple.stopwatch.Database.DayStatClasses.CycleStats;
import com.example.tragic.irate.simple.stopwatch.Database.DayStatClasses.DayHolder;
import com.example.tragic.irate.simple.stopwatch.Database.DayStatClasses.DayWithCycleStats;

import java.util.List;

@Dao
public interface
CyclesDao {

    @Transaction
    @Query("SELECT * from DayHolder")
    List<DayWithCycleStats> getDayWithCycleStats();

    @Query("SELECT * from CycleStats")
    List<CycleStats> loadAllCycleStats();

//    @Transaction
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void insertDaysWithCycleStats(DayWithCycleStats dayWithCycleStats);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertDay(DayHolder dayHolder);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertStats(CycleStats cycleStats);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertStatList(List<CycleStats> cycleStatsList);

    @Query("SELECT * from Cycles")
    List<Cycles> loadAllCycles();

    @Query("SELECT * from Cycles where id=:listID")
    List<Cycles> loadSingleCycle(int listID);

    @Query("SELECT * from PomCycles where id=:listID")
    List<PomCycles> loadSinglePomCycle(int listID);

    @Query("SELECT * from PomCycles")
    List<PomCycles> loadAllPomCycles();

    @Query("SELECT * from Cycles ORDER by title DESC")
    List<Cycles> loadCyclesAlphaEnd();

    @Query("SELECT * from Cycles ORDER by title ASC")
    List<Cycles> loadCyclesAlphaStart();

    @Query("SELECT * from CYCLES ORDER by timeAdded DESC")
    List<Cycles> loadCyclesMostRecent();

    @Query("SELECT * from CYCLES ORDER by timeAdded ASC")
    List<Cycles> loadCycleLeastRecent();

    @Query("SELECT * from CYCLES ORDER by itemCount DESC")
    List<Cycles> loadCyclesMostItems();

    @Query("SELECT * from CYCLES ORDER by itemCount ASC")
    List<Cycles> loadCyclesLeastItems();

    @Query("SELECT * from PomCycles ORDER by title DESC")
    List<PomCycles> loadPomAlphaEnd();

    @Query("SELECT * from PomCycles ORDER by title ASC")
    List<PomCycles> loadPomAlphaStart();

    @Query("SELECT * from POMCYCLES ORDER by timeAdded DESC")
    List<PomCycles> loadPomCyclesMostRecent();

    @Query("SELECT * from POMCYCLES ORDER by timeAdded ASC")
    List<PomCycles> loadPomCyclesLeastRecent();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertCycle(Cycles cycles);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertPomCycle(PomCycles pomCycles);

    @Query("UPDATE cycles set title=:newTitle WHERE id=:listID")
    void updateCustomTitle(String newTitle, int listID);

    @Query("DELETE from CYCLES where totalSetTime AND totalBreakTime AND cyclesCompleted")
    void deleteTotalTimesCycle();

    @Query("DELETE from POMCYCLES WHERE cyclesCompleted AND totalWorkTime AND totalBreakTime")
    void deleteTotalTimesPom();

    @Update
    void updateCycles(Cycles cycles);

    @Update
    void updatePomCycles(PomCycles pomCycles);

    @Delete
    void deleteCycle(Cycles cycles);

    @Delete
    void deletePomCycle(PomCycles pomCycles);

    @Query("DELETE from Cycles")
    void deleteAllCycles();

    @Query("DELETE from PomCycles")
    void deleteAllPomCycles();
}