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

    List<StatsForEachActivity> statsForEachActivityListForTimerAccess;
    StatsForEachActivity mStatsForEachActivity;

    List<String> totalActivitiesListForSelectedDuration;
    List<Long> totalSetTimeListForEachActivityForSelectedDuration;
    List<Long> totalBreakTimeListForEachActivityForSelectedDuration;
    List<Double> totalCaloriesBurnedListForEachActivityForSelectedDuration;

    boolean activityExistsInDatabaseForSelectedDay;
    String mActivityString;
    int activityPositionInListForCurrentDay;
    int mOldActivityPositionInListForCurrentDay;

    List<DayHolder> mDayHolderList;
    List<StatsForEachActivity> mStatsForEachActivityListForFragmentAccess;
    int duplicateStringPosition;

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

    public void assignDayHolderInstanceForSelectedDay(int daySelected) {
        List<DayHolder> dayHolderList = cyclesDatabase.cyclesDao().loadSingleDay(daySelected);
        mDayHolder = dayHolderList.get(0);
    }

    public void setDayHolderAndStatForEachActivityListsForSelectedDay(int dayToRetrieve) {
        mDayHolderList = cyclesDatabase.cyclesDao().loadSingleDay(dayToRetrieve);
        mStatsForEachActivityListForFragmentAccess = cyclesDatabase.cyclesDao().loadActivitiesForSpecificDate(dayToRetrieve);
    }

    public void setAllDayAndStatListsForWeek(int dayOfWeek, int dayOfMonth, int dayOfYear) {
        List<Integer> daysOfWeekList = new ArrayList<>();

        int daysInWeek = 7;
        int firstDayInYearToAdd = 0;

        if (dayOfMonth<=7) {
            daysInWeek = 7 - (dayOfWeek - dayOfMonth);
            firstDayInYearToAdd = dayOfYear - (dayOfMonth-1);
        } else {
            firstDayInYearToAdd = dayOfYear - (dayOfWeek-1);
        }

        for (int i=0; i<daysInWeek; i++) {
            if (cyclesDatabase.cyclesDao().loadSingleDay(firstDayInYearToAdd + i).size()!=0) {
                daysOfWeekList.add(firstDayInYearToAdd + i);
            }
        }

        if (daysOfWeekList.size()>0) {
            mDayHolderList = cyclesDatabase.cyclesDao().loadMultipleDays(daysOfWeekList);
            mStatsForEachActivityListForFragmentAccess = cyclesDatabase.cyclesDao().loadActivitiesForMultipleDays(daysOfWeekList);
        } else {
            mDayHolderList = new ArrayList<>();
            mStatsForEachActivityListForFragmentAccess = new ArrayList<>();
        }
    }

    public void setAllDayAndStatListsForMonth(int dayOfMonth, int numberOfDaysInMonth, int dayOfYear) {
        List<Integer> daysOfMonthList = new ArrayList<>();

        int firstDayInYearToAdd = dayOfYear - (dayOfMonth-1);
        for (int i=0; i<numberOfDaysInMonth; i++) {
            if (cyclesDatabase.cyclesDao().loadSingleDay(firstDayInYearToAdd + i).size()!=0) {
                daysOfMonthList.add(firstDayInYearToAdd + i);
            }
        }

        if (daysOfMonthList.size()>0) {
            mDayHolderList = cyclesDatabase.cyclesDao().loadMultipleDays(daysOfMonthList);
            mStatsForEachActivityListForFragmentAccess = cyclesDatabase.cyclesDao().loadActivitiesForMultipleDays(daysOfMonthList);
        } else {
            mDayHolderList = new ArrayList<>();
            mStatsForEachActivityListForFragmentAccess = new ArrayList<>();
        }
    }

    public void setAllDayAndStatListsForYear(int daysInYear) {
        List<Integer> daysOfYearList = new ArrayList<>();

        //If days exists in database, add it to list of days in year list.
        for (int i=0; i<daysInYear; i++) {
            if (cyclesDatabase.cyclesDao().loadSingleDay(i+1).size()!=0) {
                daysOfYearList.add(i+1);
            }
        }

        if (daysOfYearList.size()>0) {
            mDayHolderList = cyclesDatabase.cyclesDao().loadMultipleDays(daysOfYearList);
            mStatsForEachActivityListForFragmentAccess = cyclesDatabase.cyclesDao().loadActivitiesForMultipleDays(daysOfYearList);
        } else {
            mDayHolderList = new ArrayList<>();
            mStatsForEachActivityListForFragmentAccess = new ArrayList<>();
        }
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

    public double getTotalCaloriesBurnedFromDayHolderList() {
        double valueToReturn = 0;

        for (int i=0; i<mDayHolderList.size(); i++) {
            valueToReturn += mDayHolderList.get(i).getTotalCaloriesBurned();
        }

        return valueToReturn;
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

    public void deleteDayHolderEntity(int daySelected) {
        cyclesDatabase.cyclesDao().deleteSingleDay(daySelected);
    }

    public void deleteAllDayHolderEntries() {
        cyclesDatabase.cyclesDao().deleteAllDayHolderEntries();
    }

    //Since DayHolder's dayId and CycleStat's setUniqueDayIdPossessedByEachOfItsActivities are identical, we simply tie StatsForEachActivityWithinCycle's unique ID to that as well.
    public void insertTotalTimesAndCaloriesForEachActivityWithinASpecificDay() {

        if (!activityExistsInDatabaseForSelectedDay) {
            StatsForEachActivity statsForEachActivity = new StatsForEachActivity();

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

        //This only returns true once, when our activity matches one in the database.
        for (int i=0; i<getStatForEachActivityListForForSingleDay().size(); i++) {
            if (mActivityString.equals(getStatForEachActivityListForForSingleDay().get(i).getActivity())) {
                activityPositionInListForCurrentDay = i;
                activityExistsInDatabaseForSelectedDay = true;
            }
        }
    }

    // statsForEachActivityListForTimerAccess's size will contain all activities for that day (uniqueID). Retrieving a specific activity is based on its position within that list.
    public void assignStatForEachActivityInstanceForSpecificActivityWithinSelectedDay(int daySelected) {
        //New database pull to account for most recent insertion.
        setStatForEachActivityListForForSingleDay(daySelected);

        if (activityExistsInDatabaseForSelectedDay) {
            mStatsForEachActivity = statsForEachActivityListForTimerAccess.get(activityPositionInListForCurrentDay);
        } else if (statsForEachActivityListForTimerAccess.size()>0) {
            //Fetches most recent db insertion as a reference to the new row that was just saved.
            int mostRecentEntry = statsForEachActivityListForTimerAccess.size()-1;
            mStatsForEachActivity = statsForEachActivityListForTimerAccess.get(mostRecentEntry);
        }
    }

    public void assignStatsForEachActivityInstanceForEditing(int position) {
        mStatsForEachActivity = mStatsForEachActivityListForFragmentAccess.get(position);
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
        statsForEachActivityListForTimerAccess = cyclesDatabase.cyclesDao().loadActivitiesForSpecificDate(dayToRetrieve);
    }

    public List<StatsForEachActivity> getStatForEachActivityListForForSingleDay() {
        return statsForEachActivityListForTimerAccess;
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

    public long getUniqueIdTiedToSpecificDayFromStatsForEachActivity() {
        return mStatsForEachActivity.getUniqueIdTiedToTheSelectedActivity();
    }

    public void deleteStatForEachActivityEntity(long dayToDelete) {
        cyclesDatabase.cyclesDao().deleteActivityStatsForSingleDay(dayToDelete);
    }

    public void deleteAllStatsForEachActivityEntries() {
        cyclesDatabase.cyclesDao().deleteAllStatsForEachActivityEntries();
    }

    public void setTotalActivityStatsForSelectedDaysToLists() {
        for (int i=0; i<mStatsForEachActivityListForFragmentAccess.size(); i++) {
            if (!doesTotalActivitiesListContainSelectedString(mStatsForEachActivityListForFragmentAccess.get(i).getActivity())) {
                totalActivitiesListForSelectedDuration.add(mStatsForEachActivityListForFragmentAccess.get(i).getActivity());
                totalSetTimeListForEachActivityForSelectedDuration.add(mStatsForEachActivityListForFragmentAccess.get(i).getTotalSetTimeForEachActivity());
                totalBreakTimeListForEachActivityForSelectedDuration.add(mStatsForEachActivityListForFragmentAccess.get(i).getTotalBreakTimeForEachActivity());
                totalCaloriesBurnedListForEachActivityForSelectedDuration.add(mStatsForEachActivityListForFragmentAccess.get(i).getTotalCaloriesBurnedForEachActivity());
            } else {
                totalSetTimeListForEachActivityForSelectedDuration.set(duplicateStringPosition, combinedSetTimeFromExistingAndRepeatingPositions(i));
                totalBreakTimeListForEachActivityForSelectedDuration.set(duplicateStringPosition, combinedBreakTimeFromExistingAndRepeatingPositions(i));
                totalCaloriesBurnedListForEachActivityForSelectedDuration.set(duplicateStringPosition, combinedCaloriesFromExistingAndRepeatingPositions(i));
            }
        }
    }

    private boolean doesTotalActivitiesListContainSelectedString(String stringToCheck) {
        for (int i=0; i<totalActivitiesListForSelectedDuration.size(); i++) {
            if (totalActivitiesListForSelectedDuration.get(i).equalsIgnoreCase(stringToCheck)) {
                duplicateStringPosition = i;
                return true;
            }
        }
        return false;
    }

    private long combinedSetTimeFromExistingAndRepeatingPositions(int position) {
        long iteratingValue = mStatsForEachActivityListForFragmentAccess.get(position).getTotalSetTimeForEachActivity();
        long presentValue =  totalSetTimeListForEachActivityForSelectedDuration.get(duplicateStringPosition);
        return iteratingValue + presentValue;
    }

    private long combinedBreakTimeFromExistingAndRepeatingPositions(int position) {
        long iteratingValue = mStatsForEachActivityListForFragmentAccess.get(position).getTotalBreakTimeForEachActivity();
        long presentValue =  totalBreakTimeListForEachActivityForSelectedDuration.get(duplicateStringPosition);
        return iteratingValue + presentValue;
    }

    private double combinedCaloriesFromExistingAndRepeatingPositions(int position) {
        double iteratingValue = mStatsForEachActivityListForFragmentAccess.get(position).getTotalCaloriesBurnedForEachActivity();
        double presentValue =  totalCaloriesBurnedListForEachActivityForSelectedDuration.get(duplicateStringPosition);
        return iteratingValue + presentValue;
    }

    public List<String> getTotalActivitiesListForSelectedDuration() {
        return totalActivitiesListForSelectedDuration;
    }

    public List<Long> getTotalSetTimeListForEachActivityForSelectedDuration() {
        return totalSetTimeListForEachActivityForSelectedDuration;
    }

    public List<Long> getTotalBreakTimeListForEachActivityForSelectedDuration() {
        return totalBreakTimeListForEachActivityForSelectedDuration;
    }

    public List<Double> getTotalCaloriesBurnedListForEachActivityForSelectedDuration() {
        return totalCaloriesBurnedListForEachActivityForSelectedDuration;
    }

    public void clearStatsForEachActivityArrayLists() {
        totalActivitiesListForSelectedDuration.clear();
        totalSetTimeListForEachActivityForSelectedDuration.clear();
        totalBreakTimeListForEachActivityForSelectedDuration.clear();
        totalCaloriesBurnedListForEachActivityForSelectedDuration.clear();
    }

    private void instantiateEntityLists() {
        mDayHolderList = new ArrayList<>();
        mStatsForEachActivityListForFragmentAccess = new ArrayList<>();
    }

    private void instantiateArrayListsOfActivitiesAndTheirStats() {
        totalActivitiesListForSelectedDuration = new ArrayList<>();
        totalSetTimeListForEachActivityForSelectedDuration = new ArrayList<>();
        totalBreakTimeListForEachActivityForSelectedDuration = new ArrayList<>();
        totalCaloriesBurnedListForEachActivityForSelectedDuration = new ArrayList<>();
    }

    public int getCurrentDayOfYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_YEAR);
    }
}