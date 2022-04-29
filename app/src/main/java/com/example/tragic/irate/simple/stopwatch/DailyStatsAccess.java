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
    int activityPositionInListForCurrentDay;
    int mOldActivityPositionInListForCurrentDay;

    List<DayHolder> mDayHolderList;
    List<StatsForEachActivity> mStatsForEachActivityList;

    public DailyStatsAccess(Context context) {
        this.mContext = context;
        instantiateDailyStatsDatabase();
        instantiateEntityLists();
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

    public void assignDayHolderInstanceFromSingleDay(int dayToRetrieve) {
        List<DayHolder> dayHolderList = cyclesDatabase.cyclesDao().loadSingleDay(dayToRetrieve);
        if (dayHolderList.size()>0) {
            this.mDayHolder = dayHolderList.get(0);
        } else {
            this.mDayHolder = new DayHolder();
        }
        Log.i("testFetch", "Daily fetched!");
    }

    public void setDayHolderListForWeek(int dayOfWeek, int dayOfMonth, int dayOfYear) {
        List<Integer> daysOfWeekList = new ArrayList<>();

        int daysInWeek = 7;
        int firstDayInYearToAdd = 0;

        if (dayOfMonth<=7) {
            daysInWeek = 7 - (dayOfWeek - dayOfMonth);
            firstDayInYearToAdd = dayOfYear - (dayOfMonth-1);
        } else {
            firstDayInYearToAdd = (dayOfYear) - (dayOfWeek-1);
        }

        for (int i=0; i<daysInWeek; i++) {
            if (cyclesDatabase.cyclesDao().loadSingleDay(firstDayInYearToAdd + i).size()!=0) {
                daysOfWeekList.add(firstDayInYearToAdd + i);
            }
        }

        if (daysOfWeekList.size()>0) {
            mDayHolderList = cyclesDatabase.cyclesDao().loadMultipleDays(daysOfWeekList);
        }

        Log.i("testWeek", "DayHolder list for week is " + mDayHolderList);
        Log.i("testWeek", "mDayHolder instance ids are " + mDayHolder.getDayId());
    }

    public void setDayHolderListForMonth(int dayOfMonth, int numberOfDaysInMonth, int dayOfYear) {
        List<Integer> daysOfMonthList = new ArrayList<>();

        int firstDayInYearToAdd = dayOfYear - dayOfMonth;
        for (int i=0; i<numberOfDaysInMonth; i++) {
            if (cyclesDatabase.cyclesDao().loadSingleDay(firstDayInYearToAdd + i).size()!=0) {
                daysOfMonthList.add(firstDayInYearToAdd + i);
            }
        }

        if (daysOfMonthList.size()>0) {
            mDayHolderList = cyclesDatabase.cyclesDao().loadMultipleDays(daysOfMonthList);
        }

        Log.i("testMonth", "day of month is " + dayOfMonth);
        Log.i("testMonth", "day of year is " + dayOfYear);
        Log.i("testMonth", "DayHolder list for month is " + mDayHolderList);
    }

    public void setDayHolderListForYear(int daysInYear, int dayOfYear) {
        List<Integer> daysOfYearList = new ArrayList<>();

        for (int i=0; i<daysInYear; i++) {
            if (cyclesDatabase.cyclesDao().loadSingleDay(i+1).size()!=0) {
                daysOfYearList.add(i+1);
            }
        }

        if (daysOfYearList.size()>0) {
            mDayHolderList = cyclesDatabase.cyclesDao().loadMultipleDays(daysOfYearList);
        }

        Log.i("testYear", "days in year are " + daysInYear + " and day OF year is " + dayOfYear);
        Log.i("testYear", "DayHolder list for year is " + mDayHolderList);
    }

    public long getTotalSetTimeFromDayHolderList() {
        long valueToReturn = 0;

        for (int i=0; i<mDayHolderList.size(); i++) {
            valueToReturn += mDayHolderList.get(i).getTotalSetTime();
        }
        return valueToReturn;
    }

    public long getTotalBreakTimeFromDayHolderList() {
        long valueToReturn = 0;

        for (int i=0; i<mDayHolderList.size(); i++) {
            valueToReturn += mDayHolderList.get(i).getTotalBreakTime();
        }
        return valueToReturn;
    }

    public long getTotalCaloriesBurnedFromDayHolderList() {
        long valueToReturn = 0;

        for (int i=0; i<mDayHolderList.size(); i++) {
            valueToReturn += mDayHolderList.get(i).getTotalCaloriesBurned();
        }
        return valueToReturn;
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

    public long getTotalWorkTimeFromDayHolder() {
        return mDayHolder.getTotalWorkTime();
    }

    public long getTotalRestTimeFromDayHolder() {
        return mDayHolder.getTotalRestTime();
    }

    public void updateTotalTimesAndCaloriesBurnedForCurrentDayFromDatabase() {
        cyclesDatabase.cyclesDao().updateDayHolder(mDayHolder);
    }

    public void deleteDayHolderEntity(DayHolder dayHolder) {
        if (getDayHolderEntity()!=null) {
            cyclesDatabase.cyclesDao().deleteDayHolder(dayHolder);
        }
    }

    public void deleteAllDayHolderEntries() {
        if (getDayHolderEntity()!=null) {
            cyclesDatabase.cyclesDao().deleteAllDayHolderEntries();
        }
    }

    //Since DayHolder's dayId and CycleStat's setUniqueDayIdPossessedByEachOfItsActivities are identical, we simply tie StatsForEachActivityWithinCycle's unique ID to that as well.
    public void insertTotalTimesAndCaloriesForEachActivityWithinASpecificDay() {

        if (!activityExistsInDatabaseForSelectedDay) {
            StatsForEachActivity statsForEachActivity = new StatsForEachActivity();

            //Since a new list with the new day's ID is created every day, this iterating ID will auto reset to 0 each day.
//            statsForEachActivity.setIteratingIdsForSpecificDay(statsForEachActivityListOfAllActivitiesForASpecificDate.size());
            statsForEachActivity.setUniqueIdTiedToTheSelectedActivity(getCurrentDayOfYear());
            statsForEachActivity.setActivity(mActivityString);

            statsForEachActivity.setTotalSetTimeForEachActivity(0);
            statsForEachActivity.setTotalBreakTimeForEachActivity(0);
            statsForEachActivity.setTotalCaloriesBurnedForEachActivity(0);

            cyclesDatabase.cyclesDao().insertStatsForEachActivityWithinCycle(statsForEachActivity);
        }
    }


    public void updateTotalTimesAndCaloriesBurnedForSpecificActivityOnSpecificDayRunnable() {
        cyclesDatabase.cyclesDao().updateStatsForEachActivity(mStatsForEachActivity);
    }

    public void checkIfActivityExistsForSpecificDayAndSetBooleanAndPositionForIt() {
        activityPositionInListForCurrentDay = 0;
        activityExistsInDatabaseForSelectedDay = false;

        for (int i=0; i<getStatForEachActivityListForForSingleDay().size(); i++) {
            if (mActivityString.equals(getStatForEachActivityListForForSingleDay().get(i).getActivity())) {
                activityPositionInListForCurrentDay = i;
                activityExistsInDatabaseForSelectedDay = true;
            }
        }
    }

    public void assignStatForEachActivityInstanceForSpecificActivityWithinSelectedDay() {
        //New database pull to account for most recent insertion.
        int dayOfYear = calendarValues.calendar.get(Calendar.DAY_OF_YEAR);
        setStatForEachActivityListForForSingleDay(dayOfYear);

        //If activity exists, retrieve an instance of StatForEachActivity for its position. If not, create a new entity instance.
        if (activityExistsInDatabaseForSelectedDay) {
            mStatsForEachActivity = statsForEachActivityListOfAllActivitiesForASpecificDate.get(activityPositionInListForCurrentDay);
        } else if (statsForEachActivityListOfAllActivitiesForASpecificDate.size()>0) {
            //Fetches most recent db insertion as a reference to the new row that was just saved.
            int mostRecentEntry = 0;
            mostRecentEntry = statsForEachActivityListOfAllActivitiesForASpecificDate.size()-1;
            mStatsForEachActivity = statsForEachActivityListOfAllActivitiesForASpecificDate.get(mostRecentEntry);
        } else {
//            mStatsForEachActivity = new StatsForEachActivity();
        }
    }

    public int getActivityPosition() {
        return activityPositionInListForCurrentDay;
    }

    public void setOldActivityPositionInListForCurrentDay(int oldActivityPositionInListForCurrentDay) {
        this.mOldActivityPositionInListForCurrentDay = oldActivityPositionInListForCurrentDay;
    }

    public int getOldActivityPosition() {
        return mOldActivityPositionInListForCurrentDay;
    }

    public void setStatForEachActivityListForForSingleDay(int dayToRetrieve) {
        statsForEachActivityListOfAllActivitiesForASpecificDate = cyclesDatabase.cyclesDao().loadActivitiesForSpecificDate(dayToRetrieve);
    }

    public List<StatsForEachActivity> getStatForEachActivityListForForSingleDay() {
        return statsForEachActivityListOfAllActivitiesForASpecificDate;
    }

    public StatsForEachActivity getStatsForEachActivityEntity() {
        return mStatsForEachActivity;
    }

    public void setActivityStringFromSpinner(String activityString) {
        this.mActivityString = activityString;
    }

    public String getActivityStringFromSpinner() {
        return mActivityString;
    }

    public void setActivityStringForSelectedActivity(String activity) {
        mStatsForEachActivity.setActivity(activity);
    }

    public void setTotalSetTimeForSelectedActivity(long totalSetTime) {
        mStatsForEachActivity.setTotalSetTimeForEachActivity(totalSetTime);
    }

    public long getTotalSetTimeForSelectedActivity() {
        return mStatsForEachActivity.getTotalSetTimeForEachActivity();
    }

    public void setTotalCaloriesBurnedForSelectedActivity(double totalCaloriesBurned) {
        mStatsForEachActivity.setTotalCaloriesBurnedForEachActivity(totalCaloriesBurned);
    }

    public double getTotalCaloriesBurnedForSelectedActivity() {
        return mStatsForEachActivity.getTotalCaloriesBurnedForEachActivity();
    }

    public void deleteStatForEachActivityEntity() {
        if (getStatsForEachActivityEntity()!=null) {
            cyclesDatabase.cyclesDao().deleteStatsForEachActivity(mStatsForEachActivity);
        }
    }

    public void deleteAllStatsForEachActivityEntries() {
        if (getStatsForEachActivityEntity()!=null) {
            cyclesDatabase.cyclesDao().deleteAllStatsForEachActivityEntries();
        }
    }

    //////////////////Daily Stats Fragment Methods/////////////////////////////////////////////
    public void setStatsForEachActivityListForSelectedDay(int daySelected) {
        if (cyclesDatabase.cyclesDao().loadActivitiesForSpecificDate(daySelected).size()!= 0) {
            statsForEachActivityListOfAllActivitiesForASpecificDate = cyclesDatabase.cyclesDao().loadActivitiesForSpecificDate(daySelected);
        } else {
            statsForEachActivityListOfAllActivitiesForASpecificDate = new ArrayList<>();
        }
    }

    //Global list should be fine since it's re-queried when accessing a Cycle, but keep this in mind.
    public void setStatsForEachActivityInstanceFromList() {
        if (statsForEachActivityListOfAllActivitiesForASpecificDate.size()>0) {
            mStatsForEachActivity = statsForEachActivityListOfAllActivitiesForASpecificDate.get(0);
        } else {
            mStatsForEachActivity = new StatsForEachActivity();
        }
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

    private void instantiateEntityLists() {
        mDayHolderList = new ArrayList<>();
        mStatsForEachActivityList = new ArrayList<>();
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
}