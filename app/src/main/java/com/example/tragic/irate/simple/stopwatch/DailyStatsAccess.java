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
    StatsForEachActivity mStatsForEachActivity;
    List<DayHolder> mDayHolderList;
    List<StatsForEachActivity> statsForEachActivityListForTimerAccess;
    List<StatsForEachActivity> statsForEachActivityListForFragmentAccess;

    List<String> totalActivitiesListForSelectedDuration;
    List<Long> totalSetTimeListForEachActivityForSelectedDuration;
    List<Long> totalBreakTimeListForEachActivityForSelectedDuration;
    List<Double> totalCaloriesBurnedListForEachActivityForSelectedDuration;
    long totalSetTimeForSelectedDay;
    double totalCaloriesForSelectedDay;

    long mOldDayHolderId;
    boolean activityExistsInDatabaseForSelectedDay;

    int activityPositionInListForCurrentDay;
    int mOldActivityPositionInListForCurrentDay;
    int duplicateStringPosition;

    String mActivityString;
    double mMetScore;

    public DailyStatsAccess(Context context) {
        this.mContext = context;
        instantiateDailyStatsDatabase();
        instantiateEntitiesAndTheirLists();
        instantiateArrayListsOfTotalStatsForSelectedDurations();
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

            mDayHolder = new DayHolder();

            mDayHolder.setDayId(daySelected);
            mDayHolder.setDate(date);

            mDayHolder.setTotalSetTime(0);
            mDayHolder.setTotalBreakTime(0);
            mDayHolder.setTotalCaloriesBurned(0);

            cyclesDatabase.cyclesDao().insertDay(mDayHolder);
        }
    }

    public void assignDayHolderInstanceForSelectedDay(int daySelected) {
        List<DayHolder> dayHolderList = cyclesDatabase.cyclesDao().loadSingleDay(daySelected);
        if (dayHolderList.size()>0) {
            mDayHolder = dayHolderList.get(0);
        } else {
            mDayHolder = new DayHolder();
        }
    }

    public List<DayHolder> getDayHolderList() {
        return mDayHolderList;
    }

    public void setDayHolderAndStatForEachActivityListsForSelectedDayFromDatabase(int dayToRetrieve) {
        mDayHolderList = cyclesDatabase.cyclesDao().loadSingleDay(dayToRetrieve);
        statsForEachActivityListForFragmentAccess = cyclesDatabase.cyclesDao().loadActivitiesForSpecificDate(dayToRetrieve);
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
            statsForEachActivityListForFragmentAccess = cyclesDatabase.cyclesDao().loadActivitiesForMultipleDays(daysOfWeekList);
        } else {
            mDayHolderList = new ArrayList<>();
            statsForEachActivityListForFragmentAccess = new ArrayList<>();
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
            statsForEachActivityListForFragmentAccess = cyclesDatabase.cyclesDao().loadActivitiesForMultipleDays(daysOfMonthList);
        } else {
            mDayHolderList = new ArrayList<>();
            statsForEachActivityListForFragmentAccess = new ArrayList<>();
        }
    }

    public void setAllDayAndStatListsForYearFromDatabase(int daysInYear) {
        List<Integer> daysOfYearList = new ArrayList<>();

        //If days exists in database, add it to list of days in year list.
        for (int i=0; i<daysInYear; i++) {
            if (cyclesDatabase.cyclesDao().loadSingleDay(i+1).size()!=0) {
                daysOfYearList.add(i+1);
            }
        }

        if (daysOfYearList.size()>0) {
            mDayHolderList = cyclesDatabase.cyclesDao().loadMultipleDays(daysOfYearList);
            statsForEachActivityListForFragmentAccess = cyclesDatabase.cyclesDao().loadActivitiesForMultipleDays(daysOfYearList);
        } else {
            mDayHolderList = new ArrayList<>();
            statsForEachActivityListForFragmentAccess = new ArrayList<>();
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


    public void checkIfActivityExistsForSpecificDayAndSetBooleanAndPositionForIt(List<StatsForEachActivity> statsForEachActivityList) {
        activityPositionInListForCurrentDay = 0;
        activityExistsInDatabaseForSelectedDay = false;

        //This only returns true once, when our activity matches one in the database.
        for (int i=0; i<statsForEachActivityList.size(); i++) {
            if (mActivityString.equals(statsForEachActivityList.get(i).getActivity())) {
                activityPositionInListForCurrentDay = i;
                activityExistsInDatabaseForSelectedDay = true;
            }
        }
    }

    public boolean getActivityExistsInDatabaseForSelectedDay () {
        return activityExistsInDatabaseForSelectedDay;
    }

    //Since DayHolder's dayId and CycleStat's setUniqueDayIdPossessedByEachOfItsActivities are identical, we simply tie StatsForEachActivityWithinCycle's unique ID to that as well.
    public void insertTotalTimesAndCaloriesForEachActivityWithinASpecificDay(int selectedDay) {

        if (!activityExistsInDatabaseForSelectedDay) {
            mStatsForEachActivity = new StatsForEachActivity();

            mStatsForEachActivity.setUniqueIdTiedToTheSelectedActivity(selectedDay);
            mStatsForEachActivity.setActivity(mActivityString);
            mStatsForEachActivity.setMetScore(mMetScore);

            mStatsForEachActivity.setTotalSetTimeForEachActivity(0);
            mStatsForEachActivity.setTotalBreakTimeForEachActivity(0);
            mStatsForEachActivity.setTotalCaloriesBurnedForEachActivity(0);

            cyclesDatabase.cyclesDao().insertStatsForEachActivityWithinCycle(mStatsForEachActivity);
        }
    }

    public void updateTotalTimesAndCaloriesBurnedForSpecificActivityOnSpecificDayRunnable() {
        cyclesDatabase.cyclesDao().updateStatsForEachActivity(mStatsForEachActivity);
    }

    //statsForEachActivityListForTimerAccess's size will contain all activities for that day (uniqueID). Retrieving a specific activity is based on its position within that list.
    public void assignStatForEachActivityInstanceForSpecificActivityWithinSelectedDay(int daySelected) {
        //New database pull to account for most recent insertion.
        setStatForEachActivityListForForSingleDayFromDatabase(daySelected);

        if (activityExistsInDatabaseForSelectedDay) {
            mStatsForEachActivity = statsForEachActivityListForTimerAccess.get(activityPositionInListForCurrentDay);
        } else if (statsForEachActivityListForTimerAccess.size()>0) {
            //Fetches most recent db insertion as a reference to the new row that was just saved.
            int mostRecentEntry = statsForEachActivityListForTimerAccess.size()-1;
            mStatsForEachActivity = statsForEachActivityListForTimerAccess.get(mostRecentEntry);
        } else {
            mStatsForEachActivity = new StatsForEachActivity();
        }
    }

    public void assignStatsForEachActivityEntityForEditing(int position) {
        mStatsForEachActivity = statsForEachActivityListForFragmentAccess.get(position);
    }

    public List<StatsForEachActivity> getStatsForEachActivityListForFragmentAccess() {
        return statsForEachActivityListForFragmentAccess;
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

    public void setStatForEachActivityListForForSingleDayFromDatabase(int dayToRetrieve) {
        statsForEachActivityListForTimerAccess = cyclesDatabase.cyclesDao().loadActivitiesForSpecificDate(dayToRetrieve);
    }

    public List<StatsForEachActivity> getStatForEachActivityListForForSingleDayForTimerAccess() {
        return statsForEachActivityListForTimerAccess;
    }

    public void setLocalActivityStringVariable(String activityString) {
        this.mActivityString = activityString;
    }

    public String getActivityStringFromSpinner() {
        return mActivityString;
    }

    public void setActivityStringForSelectedActivityInStatsForEachActivityEntity(String activity) {
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

    public void setLocalMetScoreVariable(double metScore) {
        this.mMetScore = metScore;
    }

    public double getMetScoreForSelectedActivity() {
        return mStatsForEachActivity.getMetScore();
    }

    public void setTotalCaloriesBurnedForSelectedActivity(double totalCaloriesBurned) {
        mStatsForEachActivity.setTotalCaloriesBurnedForEachActivity(totalCaloriesBurned);
    }

    public double getTotalCaloriesBurnedForSelectedActivity() {
        return mStatsForEachActivity.getTotalCaloriesBurnedForEachActivity();
    }

    public void deleteStatsForEachActivityEntity() {
        cyclesDatabase.cyclesDao().deleteStatsForEachActivity(mStatsForEachActivity);
    }

    public void deleteStatForEachActivityEntityForSelectedDay(long dayToDelete) {
        cyclesDatabase.cyclesDao().deleteActivityStatsForSingleDay(dayToDelete);
    }

    public void deleteAllStatsForEachActivityEntries() {
        cyclesDatabase.cyclesDao().deleteAllStatsForEachActivityEntries();
    }

    public void setTotalActivityStatsForSelectedDaysToArrayLists() {
        for (int i=0; i<statsForEachActivityListForFragmentAccess.size(); i++) {
            if (statsForEachActivityListForFragmentAccess.get(i).getActivity()!=null) {
                if (!doesTotalActivitiesListContainSelectedString(statsForEachActivityListForFragmentAccess.get(i).getActivity())) {
                    totalActivitiesListForSelectedDuration.add(statsForEachActivityListForFragmentAccess.get(i).getActivity());
                    totalSetTimeListForEachActivityForSelectedDuration.add(statsForEachActivityListForFragmentAccess.get(i).getTotalSetTimeForEachActivity());
                    totalBreakTimeListForEachActivityForSelectedDuration.add(statsForEachActivityListForFragmentAccess.get(i).getTotalBreakTimeForEachActivity());
                    totalCaloriesBurnedListForEachActivityForSelectedDuration.add(statsForEachActivityListForFragmentAccess.get(i).getTotalCaloriesBurnedForEachActivity());
                } else {
                    totalSetTimeListForEachActivityForSelectedDuration.set(duplicateStringPosition, combinedSetTimeFromExistingAndRepeatingPositions(i));
                    totalBreakTimeListForEachActivityForSelectedDuration.set(duplicateStringPosition, combinedBreakTimeFromExistingAndRepeatingPositions(i));
                    totalCaloriesBurnedListForEachActivityForSelectedDuration.set(duplicateStringPosition, combinedCaloriesFromExistingAndRepeatingPositions(i));
                }
            }
        }
    }

    //Todo: These are returning 0.
    public long getTotalSetTimeVariableForDayHolder() {
        return totalSetTimeForSelectedDay;
    }

    public void setTotalSetTimeVariableForDayHolder() {
        long valueToReturn = 0;

        for (int i=0; i<totalSetTimeListForEachActivityForSelectedDuration.size(); i++) {
            valueToReturn += totalSetTimeListForEachActivityForSelectedDuration.get(i);
        }

        totalSetTimeForSelectedDay = valueToReturn;
    }

    public double getTotalCaloriesVariableForDayHolder() {
        return totalCaloriesForSelectedDay;
    }

    public void setTotalCaloriesVariableForDayHolder() {
        double valueToReturn = 0;

        for (int i=0; i<totalCaloriesBurnedListForEachActivityForSelectedDuration.size(); i++) {
            valueToReturn += totalCaloriesBurnedListForEachActivityForSelectedDuration.get(i);
        }

        totalCaloriesForSelectedDay = valueToReturn;
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
        long iteratingValue = statsForEachActivityListForFragmentAccess.get(position).getTotalSetTimeForEachActivity();
        long presentValue =  totalSetTimeListForEachActivityForSelectedDuration.get(duplicateStringPosition);
        return iteratingValue + presentValue;
    }

    private long combinedBreakTimeFromExistingAndRepeatingPositions(int position) {
        long iteratingValue = statsForEachActivityListForFragmentAccess.get(position).getTotalBreakTimeForEachActivity();
        long presentValue =  totalBreakTimeListForEachActivityForSelectedDuration.get(duplicateStringPosition);
        return iteratingValue + presentValue;
    }

    private double combinedCaloriesFromExistingAndRepeatingPositions(int position) {
        double iteratingValue = statsForEachActivityListForFragmentAccess.get(position).getTotalCaloriesBurnedForEachActivity();
        double presentValue =  totalCaloriesBurnedListForEachActivityForSelectedDuration.get(duplicateStringPosition);
        return iteratingValue + presentValue;
    }

    public List<String> getTotalActivitiesListForSelectedDuration() {
        return totalActivitiesListForSelectedDuration;
    }

    public List<Long> getTotalSetTimeListForEachActivityForSelectedDuration() {
        return totalSetTimeListForEachActivityForSelectedDuration;
    }


    public void clearStatsForEachActivityArrayLists() {
        totalActivitiesListForSelectedDuration.clear();
        totalSetTimeListForEachActivityForSelectedDuration.clear();
        totalBreakTimeListForEachActivityForSelectedDuration.clear();
        totalCaloriesBurnedListForEachActivityForSelectedDuration.clear();
    }

    private void instantiateEntitiesAndTheirLists() {
        mDayHolder = new DayHolder();
        mStatsForEachActivity = new StatsForEachActivity();
        mDayHolderList = new ArrayList<>();
        statsForEachActivityListForFragmentAccess = new ArrayList<>();
    }

    private void instantiateArrayListsOfTotalStatsForSelectedDurations() {
        totalActivitiesListForSelectedDuration = new ArrayList<>();
        totalSetTimeListForEachActivityForSelectedDuration = new ArrayList<>();
        totalBreakTimeListForEachActivityForSelectedDuration = new ArrayList<>();
        totalCaloriesBurnedListForEachActivityForSelectedDuration = new ArrayList<>();
    }
}