package burn.track.repeat.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import burn.track.repeat.Database.DailyCalorieClasses.CaloriesForEachFood;
import burn.track.repeat.Database.DailyStatClasses.StatsForEachActivity;

import java.util.List;

@Dao
public interface
CyclesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStatsForEachActivity(StatsForEachActivity statsForEachActivity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMultipleActivities (List<StatsForEachActivity> statsForEachActivitiesList);

    @Update
    void updateStatsForEachActivity(StatsForEachActivity statsForEachActivity);

    @Delete
    void deleteStatsForEachActivity(StatsForEachActivity statsForEachActivity);

    @Query("SELECT * from StatsForEachActivity")
    List<StatsForEachActivity> loadAllStatsForEachActivityRows();

    @Query("SELECT * from StatsForEachActivity WHERE uniqueIdTiedToTheSelectedActivity IS:uniqueId")
    List<StatsForEachActivity> loadActivitiesForSpecificDate(long uniqueId);

    @Query("SELECT * from StatsForEachActivity WHERE uniqueIdTiedToTheSelectedActivity IN (:uniqueIDs)")
    List<StatsForEachActivity> loadActivitiesForMultipleDays(List<Integer> uniqueIDs);

    @Query("SELECT * from StatsForEachActivity WHERE uniqueIdTiedToTheSelectedActivity IN (:uniqueIDs) ORDER by activity ASC")
    List<StatsForEachActivity> loadActivitiesByAToZTitle(List<Integer> uniqueIDs);

    @Query("SELECT * from StatsForEachActivity WHERE uniqueIdTiedToTheSelectedActivity IN (:uniqueIDs) ORDER by activity DESC")
    List<StatsForEachActivity> loadActivitiesByZToATitle(List<Integer> uniqueIDs);

    @Query("SELECT * from StatsForEachActivity WHERE uniqueIdTiedToTheSelectedActivity IN (:uniqueIDs) ORDER by totalSetTimeForEachActivity DESC")
    List<StatsForEachActivity> loadActivitiesByMostTimeElapsed(List<Integer> uniqueIDs);

    @Query("SELECT * from StatsForEachActivity WHERE uniqueIdTiedToTheSelectedActivity IN (:uniqueIDs) ORDER by totalSetTimeForEachActivity ASC")
    List<StatsForEachActivity> loadActivitiesByLeastTimeElapsed(List<Integer> uniqueIDs);

    @Query("SELECT * from StatsForEachActivity WHERE uniqueIdTiedToTheSelectedActivity IN (:uniqueIDs) ORDER by totalCaloriesBurnedForEachActivity DESC")
    List<StatsForEachActivity> loadActivitiesByMostCaloriesBurned(List<Integer> uniqueIDs);

    @Query("SELECT * from StatsForEachActivity WHERE uniqueIdTiedToTheSelectedActivity IN (:uniqueIDs) ORDER by totalCaloriesBurnedForEachActivity ASC")
    List<StatsForEachActivity> loadActivitiesByLeastCaloriesBurned(List<Integer> uniqueIDs);

    @Query("SELECT * from StatsForEachActivity WHERE uniqueIdTiedToTheSelectedActivity IN (:uniqueIDs) ORDER by timeAdded DESC")
    List<StatsForEachActivity> loadActivitiesByMostRecent(List<Integer> uniqueIDs);

    @Query("SELECT * from StatsForEachActivity WHERE uniqueIdTiedToTheSelectedActivity IN (:uniqueIDs) ORDER by timeAdded ASC")
    List<StatsForEachActivity> loadActivitiesByLeastRecent(List<Integer> uniqueIDs);

    @Query("DELETE from StatsForEachActivity WHERE uniqueIdTiedToTheSelectedActivity IS:listID")
    void deleteActivityStatsForSingleDay (long listID);

    @Query("DELETE from StatsForEachActivity WHERE uniqueIdTiedToTheSelectedActivity IN (:listIDs)")
    void deleteActivityStatsForMultipleDays (List<Long> listIDs);

    @Query("DELETE from StatsForEachActivity")
    void deleteAllStatsForEachActivityEntries();

    ///////////////////////////////////////////////////////////

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCaloriesForEachFoodRow(CaloriesForEachFood caloriesForEachFood);

    @Update
    void updateCaloriesForEachFoodRow(CaloriesForEachFood caloriesForEachFood);

    @Delete
    void deleteCaloriesForEachFoodRow(CaloriesForEachFood caloriesForEachFood);

    @Query("SELECT * from CaloriesForEachFood")
    List<CaloriesForEachFood> loadAllCaloriesForEachFoodRows();

    @Query("SELECT * from CaloriesForEachFood WHERE uniqueIdTiedToEachFood IS:uniqueId")
    List<CaloriesForEachFood> loadCaloriesForEachFoodForSpecificDay(long uniqueId);

    @Query("SELECT * from CaloriesForEachFood WHERE uniqueIdTiedToEachFood IN (:uniqueIDs)")
    List<CaloriesForEachFood> loadCaloriesForEachFoodForMultipleDays(List<Integer> uniqueIDs);

    @Query("SELECT * from CaloriesForEachFood")
    List<CaloriesForEachFood> loadCaloriesForEachFoodForAllDays();

    @Query("SELECT * from CaloriesForEachFood WHERE uniqueIdTiedToEachFood IN (:uniqueIDs) ORDER by typeOfFood ASC")
    List<CaloriesForEachFood> loadCaloriesForEachFoodByAToZName(List<Integer> uniqueIDs);

    @Query("SELECT * from CaloriesForEachFood WHERE uniqueIdTiedToEachFood IN (:uniqueIDs) ORDER by typeOfFood DESC")
    List<CaloriesForEachFood> loadCaloriesForEachFoodByZToAName(List<Integer> uniqueIDs);

    @Query("SELECT * from CaloriesForEachFood WHERE uniqueIdTiedToEachFood IN (:uniqueIDs) ORDER by caloriesConsumedForEachFoodType DESC")
    List<CaloriesForEachFood> loadCaloriesForEachFoodByMostCaloriesBurned(List<Integer> uniqueIDs);

    @Query("SELECT * from CaloriesForEachFood WHERE uniqueIdTiedToEachFood IN (:uniqueIDs) ORDER by caloriesConsumedForEachFoodType ASC")
    List<CaloriesForEachFood> loadCaloriesForEachFoodByLeastCaloriesBurned(List<Integer> uniqueIDs);

    @Query("SELECT * from CaloriesForEachFood WHERE uniqueIdTiedToEachFood IN (:uniqueIDs) ORDER by timeAdded DESC")
    List<CaloriesForEachFood> loadCaloriesForEachFoodByMostRecent(List<Integer> uniqueIDs);

    @Query("SELECT * from CaloriesForEachFood WHERE uniqueIdTiedToEachFood IN (:uniqueIDs) ORDER by timeAdded ASC")
    List<CaloriesForEachFood> loadCaloriesForEachFoodByLeastRecent(List<Integer> uniqueIDs);

    @Query("DELETE from CaloriesForEachFood WHERE uniqueIdTiedToEachFood IS:listID")
    void deleteCaloriesForEachFoodForSingleDay (long listID);

    @Query("DELETE from CaloriesForEachFood WHERE uniqueIdTiedToEachFood IN (:listIDs)")
    void deleteCaloriesForEachFoodForMultipleDays (List<Long> listIDs);

    @Query("DELETE from CaloriesForEachFood")
    void deleteAllCaloriesForEachFoodEntries();

    ////////////////////////////////////////////////////////////////////

    @Query("SELECT * from Cycles")
    List<Cycles> loadAllCycles();

    @Query("SELECT * from Cycles where id=:listID")
    List<Cycles> loadSingleCycle(int listID);

    @Query("SELECT * from PomCycles where id=:listID")
    List<PomCycles> loadSinglePomCycle(int listID);

    @Query("SELECT * from PomCycles")
    List<PomCycles> loadAllPomCycles();

    @Query("SELECT * from Cycles ORDER by timeAdded DESC")
    List<Cycles> loadCyclesByMostRecent();

    @Query("SELECT * from Cycles ORDER by timeAdded ASC")
    List<Cycles> loadCyclesByLeastRecent();

    @Query("SELECT * from Cycles ORDER by title ASC")
    List<Cycles> loadCyclesTitleAToZ();

    @Query("SELECT * from Cycles ORDER by title DESC")
    List<Cycles> loadCyclesTitleZToA();

    @Query("SELECT * from CYCLES ORDER by itemCount DESC")
    List<Cycles> loadCyclesMostItems();

    @Query("SELECT * from CYCLES ORDER by itemCount ASC")
    List<Cycles> loadCyclesLeastItems();

    @Query("SELECT * from CYCLES ORDER by activityString ASC")
    List<Cycles> loadCyclesActivityAToZ();

    @Query("SELECT * from CYCLES ORDER by activityString DESC")
    List<Cycles> loadCyclesActivityZToA();

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