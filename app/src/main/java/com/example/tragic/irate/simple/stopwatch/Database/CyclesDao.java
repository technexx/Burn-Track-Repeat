package com.example.tragic.irate.simple.stopwatch.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.tragic.irate.simple.stopwatch.Database.DayStatClasses.DayHolder;
import com.example.tragic.irate.simple.stopwatch.Database.DayStatClasses.StatsForEachActivity;

import java.util.List;

@Dao
public interface
CyclesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertDay(DayHolder dayHolder);

    @Update
    void updateDayHolder(DayHolder dayHolder);

    @Query("DELETE from DayHolder")
    void deleteAllDayHolderEntries();

    @Query("SELECT * from DayHolder")
    List<DayHolder> loadAllDayHolderRows();

    @Query("SELECT * from DayHolder WHERE daySelectedId IS:listID")
    List<DayHolder> loadSingleDay(long listID);

    @Query("SELECT * from DayHolder WHERE daySelectedId IN (:listIDs)")
    List<DayHolder> loadMultipleDays(List<Integer> listIDs);

    @Query("DELETE from DayHolder WHERE daySelectedId IS:listID")
    void deleteSingleDay (long listID);

    @Query("DELETE from DayHolder WHERE daySelectedId IN (:listIDs)")
    void deleteMultipleDays(List<Integer> listIDs);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertStatsForEachActivityWithinCycle(StatsForEachActivity statsForEachActivity);

    @Update
    void updateStatsForEachActivity(StatsForEachActivity statsForEachActivity);

    @Delete
    void deleteStatsForEachActivity(StatsForEachActivity statsForEachActivity);

    @Query("SELECT * from StatsForEachActivity")
    List<StatsForEachActivity> loadAllActivitiesForAllDays();

    @Query("SELECT * from StatsForEachActivity WHERE uniqueIdTiedToTheSelectedActivity IS:uniqueId")
    List<StatsForEachActivity> loadActivitiesForSpecificDate(long uniqueId);

    @Query("SELECT * from StatsForEachActivity WHERE uniqueIdTiedToTheSelectedActivity IN (:uniqueIDs)")
    List<StatsForEachActivity> loadActivitiesForMultipleDays(List<Integer> uniqueIDs);

    @Query("DELETE from StatsForEachActivity WHERE uniqueIdTiedToTheSelectedActivity IS:listID")
    void deleteActivityStatsForSingleDay (long listID);

    @Query("DELETE from StatsForEachActivity WHERE uniqueIdTiedToTheSelectedActivity IN (:listIDs)")
    void deleteActivityStatsForMultipleDays (List<Integer> listIDs);

    @Query("DELETE from StatsForEachActivity")
    void deleteAllStatsForEachActivityEntries();

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

    @Query("DELETE from POMCYCLES WHERE cyclesCompleted AND totalWorkTime AND totalRestTime")
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