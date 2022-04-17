package com.example.tragic.irate.simple.stopwatch;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.example.tragic.irate.simple.stopwatch.Database.CyclesDatabase;
import com.example.tragic.irate.simple.stopwatch.Database.DayStatClasses.DayHolder;
import com.example.tragic.irate.simple.stopwatch.Database.DayStatClasses.StatsForEachActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DailyStatsAccess {
    Context mContext;
    CyclesDatabase cyclesDatabase;
    CalendarValues calendarValues = new CalendarValues();

    DayHolder mDayHolder;
    long mOldDayHolderId;

    List<StatsForEachActivity> statsForEachActivityListOfAllActivitiesForASpecificDate;
    StatsForEachActivity mStatsForEachActivity;

    List<String> totalActivitiesListForSelectedDay;
    List<Long> totalSetTimeListForEachActivityForSelectedDay;
    List<Long> totalBreakTimeListForEachActivityForSelectedDay;
    List<Double> totalCaloriesBurnedForEachActivityForSelectedDay;

    boolean activityExistsInDatabaseForSelectedDay;
    String mActivityString;
    int activityPositionInDb;
    int mOldActivityPositionInDb;

    public DailyStatsAccess(Context context) {
        this.mContext = context;
        instantiateDailyStatsDatabase();
        instantiateArrayListsOfActivitiesAndTheirStats();
    }

    private void instantiateDailyStatsDatabase() {
        AsyncTask.execute(()->{
            cyclesDatabase = CyclesDatabase.getDatabase(mContext);
        });
    }

    public boolean checkIfDayAlreadyExistsInDatabase(int daySelected) {
        boolean dayExists = false;

        List<DayHolder> dayHolderList = cyclesDatabase.cyclesDao().loadAllDayHolderRows();
        int dayHolderSize = dayHolderList.size();

        for (int i=0; i<dayHolderSize; i++) {
            long dayThatExistsInDatabase = dayHolderList.get(i).getDayId();
            if (daySelected==dayThatExistsInDatabase) {
                dayExists = true;
            }
        }
        return dayExists;
    }

    public void insertTotalTimesAndCaloriesBurnedOfCurrentDayIntoDatabase(int daySelected) {
        if (!checkIfDayAlreadyExistsInDatabase(daySelected)) {
            String date = calendarValues.getDateString();

            DayHolder dayHolder = new DayHolder();

            dayHolder.setDayId(daySelected);
            dayHolder.setDate(date);

            dayHolder.setTotalSetTime(0);
            dayHolder.setTotalBreakTime(0);
            dayHolder.setTotalCaloriesBurned(0);

            cyclesDatabase.cyclesDao().insertDay(dayHolder);
        }
    }

    public void assignDayHolderEntityRowFromSingleDay(int dayToRetrieve) {
        List<DayHolder> dayHolderList = cyclesDatabase.cyclesDao().loadSingleDay(dayToRetrieve);
        if (dayHolderList.size()>0) {
            this.mDayHolder = dayHolderList.get(0);
        } else {
            this.mDayHolder = new DayHolder();
        }
    }

    public DayHolder getDayHolderEntity() {
        return mDayHolder;
    }

    public long getOldDayHolderId() {
        return mOldDayHolderId;
    }

    public void setOldDayHolderId(int oldId) {
        this.mOldDayHolderId = oldId;
    }

    public long getDayIdFromDayHolder() {
        return mDayHolder.getDayId();
    }

    public void setTotalSetTimeFromDayHolder(long totalSetTime) {
        mDayHolder.setTotalSetTime(totalSetTime);
    }

    public long getTotalSetTimeFromDayHolder() {
        return mDayHolder.getTotalSetTime();
    }

    public void setTotalBreakTimeFromDayHolder(long totalBreakTime) {
        mDayHolder.setTotalBreakTime(totalBreakTime);
    }

    public long getTotalBreakTimeFromDayHolder() {
        return mDayHolder.getTotalBreakTime();
    }

    public void setTotalCaloriesBurnedFromDayHolder(double totalCaloriesBurned) {
        mDayHolder.setTotalCaloriesBurned(totalCaloriesBurned);
    }

    public double getTotalCaloriesBurnedFromDayHolder() {
        return mDayHolder.getTotalCaloriesBurned();
    }

    public void setCyclesCompletedForModeOne(int cyclesCompleted) {
        mDayHolder.setCyclesCompletedForModeOne(cyclesCompleted);
    }

    public int getCyclesCompletedForModeOne() {
        return mDayHolder.getCyclesCompletedForModeOne();
    }

    public void setTotalWorkTimeFromDayHolder(long totalWorkTime) {
        mDayHolder.setTotalWorkTime(totalWorkTime);
    }

    public long getTotalWorkTimeFromDayHolder() {
        return mDayHolder.getTotalWorkTime();
    }

    public void setTotalRestTimeFromDayHolder(long totalRestTime) {
        mDayHolder.setTotalRestTime(totalRestTime);
    }

    public long getTotalRestTimeFromDayHolder() {
        return mDayHolder.getTotalRestTime();
    }

    public void setCyclesCompletedForModeThree(int cyclesCompleted) {
        mDayHolder.setCyclesCompletedForModeThree(cyclesCompleted);
    }

    public int getCyclesCompletedForModeThree() {
        return mDayHolder.getCyclesCompletedForModeThree();
    }
    public void updateTotalTimesAndCaloriesBurnedForCurrentDayFromDatabase() {
        cyclesDatabase.cyclesDao().updateDayHolder(mDayHolder);
    }

    public void deleteDayHolderEntity(DayHolder dayHolder) {
        cyclesDatabase.cyclesDao().deleteDayHolder(dayHolder);
    }

    public void deleteAllDayHolderEntries() {
        cyclesDatabase.cyclesDao().deleteAllDayHolderEntries();
    }

    //Since DayHolder's dayId and CycleStat's setUniqueDayIdPossessedByEachOfItsActivities are identical, we simply tie StatsForEachActivityWithinCycle's unique ID to that as well.
    public void insertTotalTimesAndCaloriesForEachActivityWithinASpecificDay(String activitySelected) {

        if (!activityExistsInDatabaseForSelectedDay) {
            StatsForEachActivity statsForEachActivity = new StatsForEachActivity();

            //Since a new list with the new day's ID is created every day, this iterating ID will auto reset to 0 each day.
            statsForEachActivity.setIteratingIdsForSpecificDay(statsForEachActivityListOfAllActivitiesForASpecificDate.size());
            statsForEachActivity.setUniqueIdTiedToTheSelectedActivity(getCurrentDayOfYear());
            statsForEachActivity.setActivity(activitySelected);

            statsForEachActivity.setTotalSetTimeForEachActivity(0);
            statsForEachActivity.setTotalBreakTimeForEachActivity(0);
            statsForEachActivity.setTotalCaloriesBurnedForEachActivity(0);

            cyclesDatabase.cyclesDao().insertStatsForEachActivityWithinCycle(statsForEachActivity);

            Log.i("testInsert", "StatsForEachActivity inserted with day #" + getCurrentDayOfYear() + " and activity of " + activitySelected);
        }
    }

    public int getActivityPosition() {
        return activityPositionInDb;
    }

    public void setOldActivityPositionInDb(int oldActivityPositionInDb) {
        this.mOldActivityPositionInDb = oldActivityPositionInDb;
    }

    public int getOldActivityPosition() {
        return mOldActivityPositionInDb;
    }

    public void setStatForEachActivityListForForSingleDay(int dayToRetrieve) {
        statsForEachActivityListOfAllActivitiesForASpecificDate = cyclesDatabase.cyclesDao().loadActivitiesForSpecificDate(dayToRetrieve);
    }

    public List<StatsForEachActivity> getStatForEachActivityListForForSingleDay() {
        return statsForEachActivityListOfAllActivitiesForASpecificDate;
    }

    public void checkIfActivityExistsForSpecificDayAndSetBooleanAndPositionForIt() {
        activityPositionInDb = 0;
        activityExistsInDatabaseForSelectedDay = false;

        for (int i=0; i<getStatForEachActivityListForForSingleDay().size(); i++) {
            if (mActivityString.equals(getStatForEachActivityListForForSingleDay().get(i).getActivity())) {
                activityPositionInDb = i;
                activityExistsInDatabaseForSelectedDay = true;
            }
        }
    }

    //Todo: Test this. For new day, all unique activities added should be new.
    public void assignStatForEachActivityInstanceForSpecificActivityWithinSelectedDay() {
        //If activity exists, retrieve an instance of StatForEachActivity for its position. If not, create a new entity instance.
        if (activityExistsInDatabaseForSelectedDay) {
            mStatsForEachActivity = statsForEachActivityListOfAllActivitiesForASpecificDate.get(activityPositionInDb);
            Log.i("testAccess", "activity exists at position " + activityPositionInDb);
            Log.i("testAccess", "StatsForEachActivity List for day " + getCurrentDayOfYear() + " is " + statsForEachActivityListOfAllActivitiesForASpecificDate);
        } else {
            mStatsForEachActivity = new StatsForEachActivity();
            Log.i("testAccess", "New StatsForEachActivity created");
        }
    }

    public StatsForEachActivity getStatsForEachActivityEntity() {
        return mStatsForEachActivity;
    }

    public void setActivityString(String activityString) {
        Log.i("testAccess", "Activity String set is " + mActivityString);
        this.mActivityString = activityString;
    }

    public String getActivityString() {
        return mStatsForEachActivity.getActivity();
    }

    public void setTotalSetTimeForSelectedActivity(long totalSetTime) {
        mStatsForEachActivity.setTotalSetTimeForEachActivity(totalSetTime);
    }

    public long getTotalSetTimeForSelectedActivity() {
        return mStatsForEachActivity.getTotalSetTimeForEachActivity();
    }

    public void setTotalBreakTimeForSelectedActivity(long totalBreakTime) {
        mStatsForEachActivity.setTotalBreakTimeForEachActivity(totalBreakTime);
    }

    public long getTotalBreakTimeForSelectedActivity() {
        return mStatsForEachActivity.getTotalBreakTimeForEachActivity();
    }

    public void setTotalCaloriesBurnedForSelectedActivity(double totalCaloriesBurned) {
        mStatsForEachActivity.setTotalCaloriesBurnedForEachActivity(totalCaloriesBurned);
    }

    public double getTotalCaloriesBurnedForSelectedActivity() {
        return mStatsForEachActivity.getTotalCaloriesBurnedForEachActivity();
    }

    public void updateTotalTimesAndCaloriesBurnedForSpecificActivityOnSpecificDayRunnable() {
        cyclesDatabase.cyclesDao().updateStatsForEachActivity(mStatsForEachActivity);
    }

    public void deleteStatForEachActivityEntity() {
        cyclesDatabase.cyclesDao().deleteStatsForEachActivity(mStatsForEachActivity);
    }

    public void deleteAllStatsForEachActivityEntries() {
        cyclesDatabase.cyclesDao().deleteAllStatsForEachActivityEntries();
    }

    //////////////////Daily Stats Fragment Methods/////////////////////////////////////////////
    public void queryStatsForEachActivityForSelectedDay(int daySelected) {
        statsForEachActivityListOfAllActivitiesForASpecificDate = cyclesDatabase.cyclesDao().loadActivitiesForSpecificDate(daySelected);
    }

    public void clearArrayListsOfActivitiesAndTheirStats() {
        totalActivitiesListForSelectedDay.clear();
        totalSetTimeListForEachActivityForSelectedDay.clear();
        totalBreakTimeListForEachActivityForSelectedDay.clear();
        totalCaloriesBurnedForEachActivityForSelectedDay.clear();
    }

    public void populatePojoListsForDailyActivityStatsForSelectedDay() {
        assignTotalActivitiesListForOnSelectedDayToList();
        assignTotalSetTimeForEachActivityOnSelectedDayToList();
        assignTotalBreakTimeForEachActivityOnSelectedDayToList();
        assignTotalCaloriesForEachActivityOnSelectedDayToList();
    }

    private void assignTotalActivitiesListForOnSelectedDayToList() {
        for (int i=0; i<statsForEachActivityListOfAllActivitiesForASpecificDate.size(); i++) {
            totalActivitiesListForSelectedDay.add(statsForEachActivityListOfAllActivitiesForASpecificDate.get(i).getActivity());
        }
    }

    private void assignTotalSetTimeForEachActivityOnSelectedDayToList() {
        for (int i=0; i<statsForEachActivityListOfAllActivitiesForASpecificDate.size(); i++) {
            totalSetTimeListForEachActivityForSelectedDay.add(statsForEachActivityListOfAllActivitiesForASpecificDate.get(i).getTotalSetTimeForEachActivity());
        }
    }

    private void assignTotalBreakTimeForEachActivityOnSelectedDayToList() {
        for (int i=0; i<statsForEachActivityListOfAllActivitiesForASpecificDate.size(); i++) {
            totalBreakTimeListForEachActivityForSelectedDay.add(statsForEachActivityListOfAllActivitiesForASpecificDate.get(i).getTotalBreakTimeForEachActivity());
        }
    }

    private void assignTotalCaloriesForEachActivityOnSelectedDayToList() {
        for (int i=0; i<statsForEachActivityListOfAllActivitiesForASpecificDate.size(); i++) {
            totalCaloriesBurnedForEachActivityForSelectedDay.add(statsForEachActivityListOfAllActivitiesForASpecificDate.get(i).getTotalCaloriesBurnedForEachActivity());
        }
    }

    private void instantiateArrayListsOfActivitiesAndTheirStats() {
        totalActivitiesListForSelectedDay = new ArrayList<>();
        totalSetTimeListForEachActivityForSelectedDay = new ArrayList<>();
        totalBreakTimeListForEachActivityForSelectedDay = new ArrayList<>();
        totalCaloriesBurnedForEachActivityForSelectedDay = new ArrayList<>();
    }

    public int getCurrentDayOfYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_YEAR);
    }


//
//    private long retrievePrimaryIDForActivityFromStatsForEachActivityListForASpecificDay() {
//        long idToReturn = 0;
//
//        for (int i=0; i<statsForEachActivityListOfAllActivitiesForASpecificDate.size(); i++) {
//            if (statsForEachActivityListOfAllActivitiesForASpecificDate.get(i).getActivity().equalsIgnoreCase(mActivityString)) {
//                idToReturn = statsForEachActivityListOfAllActivitiesForASpecificDate.get(i).getIteratingIdsForSpecificDay();
//            }
//        }
//        return idToReturn;
//    }
//
//    //Todo: This is being used to assign a primary ID that mirrors the number of list indices, rather than an auto-generating one that will keep iterating even when rows are deleted.
//    public void assignActivityFromStatsForEachActivityListForASpecificDay() {
//        int iteratingId = (int) retrievePrimaryIDForActivityFromStatsForEachActivityListForASpecificDay();
//
//        if (statsForEachActivityListOfAllActivitiesForASpecificDate.size()>0) {
//            mStatsForEachActivity = statsForEachActivityListOfAllActivitiesForASpecificDate.get(iteratingId);
//        }
//
//        Log.i("testId", "returned ID is " + iteratingId);
//        Log.i("testId", "activity in row w/ returned ID is " + mStatsForEachActivity.getActivity());
//    }

//    private long getIteratingIdForSpecificDay() {
//        return mStatsForEachActivity.getIteratingIdsForSpecificDay();
//    }

}