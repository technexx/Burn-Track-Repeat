package com.example.tragic.irate.simple.stopwatch.Database.DailyStatClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.tragic.irate.simple.stopwatch.Database.CyclesDatabase;
import com.example.tragic.irate.simple.stopwatch.Database.DailyStatClasses.DayHolder;
import com.example.tragic.irate.simple.stopwatch.Database.DailyStatClasses.StatsForEachActivity;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DailyStatsAccess {
    Context mContext;
    CyclesDatabase cyclesDatabase;

    DayHolder mDayHolder;
    StatsForEachActivity mStatsForEachActivity;
    List<DayHolder> mDayHolderList;

    List<StatsForEachActivity> mStatsForEachActivityList;

    int oldStatsForEachActivityListSize;
    int newStatsForEachActivityListSize;

    List<String> totalActivitiesListForSelectedDuration;
    List<Long> totalSetTimeListForEachActivityForSelectedDuration;
    List<Double> totalCaloriesBurnedListForEachActivityForSelectedDuration;

    long totalSetTimeForSelectedDuration;
    double totalCaloriesForSelectedDuration;
    long totalUnassignedSetTimeForSelectedDuration;
    double totalUnassignedCaloriesForSelectedDuration;
    long totalAggregateTimeForSelectedDuration;
    double totalAggregateCaloriesForSelectedDuration;

    int numberOfDaysSelected;

    String mSingleDayAsString;
    String mFirstDayInDurationAsString;
    String mLastDayInDurationAsString;

    long mOldDayHolderId;
    boolean doesDayExistInDatabase;
    boolean doesActivityExistsInDatabaseForSelectedDay;

    int activityPositionInListForCurrentDay;
    int mOldActivityPositionInListForCurrentDay;
    int duplicateStringPosition;

    String mActivityString = "";
    double mMetScore;
    int mSortMode = 1;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor prefEdit;

    public DailyStatsAccess(Context context) {
        this.mContext = context;
        instantiateDailyStatsDatabase();
        instantiateEntitiesAndTheirLists();
        instantiateArrayListsOfTotalStatsForSelectedDurations();
        instantiateMiscellaneousClasses();
    }

    private void instantiateDailyStatsDatabase() {
        AsyncTask.execute(()->{
            cyclesDatabase = CyclesDatabase.getDatabase(mContext);
        });
    }

    public boolean getDoesDayExistInDatabase() {
        return doesDayExistInDatabase;
    }

    public void assignDayHolderInstanceForSelectedDay(int daySelected) {
        List<DayHolder> dayHolderList = cyclesDatabase.cyclesDao().loadSingleDay(daySelected);

        if (dayHolderList.size()>0) {
            mDayHolder = dayHolderList.get(0);
        } else {
            mDayHolder = new DayHolder();
        }

        Log.i("testInsert", "dayHolder date used to create single object list is " + doesDayExistInDatabase);
        Log.i("testInsert", "dayHolder date assigned in entity from list is " + mDayHolder.getDate());
    }

    public void setSortMode(int sortMode) {
        this.mSortMode = sortMode;
    }

    public void checkIfDayAlreadyExistsInDatabaseAndSetBooleanForIt(int daySelected) {
        doesDayExistInDatabase = false;

        List<DayHolder> dayHolderList = cyclesDatabase.cyclesDao().loadAllDayHolderRows();
        int dayHolderSize = dayHolderList.size();

        for (int i=0; i<dayHolderSize; i++) {
            long dayThatExistsInDatabase = dayHolderList.get(i).getDayId();
            if (daySelected==dayThatExistsInDatabase) {
                doesDayExistInDatabase = true;
                return;
            }
        }
        Log.i("testInsert", "doesDayExist returned from check method is " + doesDayExistInDatabase);
    }

    public void insertTotalTimesAndCaloriesBurnedOfCurrentDayIntoDatabase(int daySelected) {
        if (!doesDayExistInDatabase) {
            String date = getDateString();

            mDayHolder = new DayHolder();

            mDayHolder.setDayId(daySelected);
            mDayHolder.setDate(date);

            mDayHolder.setTotalSetTime(0);
            mDayHolder.setTotalBreakTime(0);
            mDayHolder.setTotalCaloriesBurned(0);

            cyclesDatabase.cyclesDao().insertDay(mDayHolder);
        }

        Log.i("testInsert", "doesDayExist in insert method for daily totals is " + doesDayExistInDatabase);
    }



    public List<StatsForEachActivity> assignStatsForEachActivityListBySortMode(List<Integer> listOfDays) {
        List<StatsForEachActivity> listToReturn = new ArrayList<>();

        switch (mSortMode) {
            case 1:
                listToReturn = cyclesDatabase.cyclesDao().loadActivitiesByAToZTitle(listOfDays);
                break;
            case 2:
                listToReturn = cyclesDatabase.cyclesDao().loadActivitiesByZToATitle(listOfDays);
                break;
            case 3:
                listToReturn = cyclesDatabase.cyclesDao().loadActivitiesByMostTimeElapsed(listOfDays);
                break;
            case 4:
                listToReturn = cyclesDatabase.cyclesDao().loadActivitiesByLeastTimeElapsed(listOfDays);
                break;
            case 5:
                listToReturn = cyclesDatabase.cyclesDao().loadActivitiesByMostCaloriesBurned(listOfDays);
                break;
            case 6:
                listToReturn = cyclesDatabase.cyclesDao().loadActivitiesByLeastCaloriesBurned(listOfDays);
                break;
        }

        return listToReturn;
    }



    private void populateDayHolderAndStatsForEachActivityLists(List<Integer> integerListOfSelectedDays) {
        if (integerListOfSelectedDays.size()>0) {
            mDayHolderList = cyclesDatabase.cyclesDao().loadMultipleDays(integerListOfSelectedDays);
            mStatsForEachActivityList = assignStatsForEachActivityListBySortMode(integerListOfSelectedDays);
        } else {
            mDayHolderList = new ArrayList<>();
            mStatsForEachActivityList = new ArrayList<>();
        }
    }

    public List<DayHolder> getDayHolderList() {
        return mDayHolderList;
    }

    public List<StatsForEachActivity> getStatsForEachActivityList() {
        return mStatsForEachActivityList;
    }

    public void setDayHolderAndStatForEachActivityListsForSelectedDayFromDatabase(int dayToRetrieve) {
        mDayHolderList = cyclesDatabase.cyclesDao().loadSingleDay(dayToRetrieve);

        List<Integer> singleDayList = Collections.singletonList(dayToRetrieve);
        mStatsForEachActivityList = assignStatsForEachActivityListBySortMode(singleDayList);

        convertToStringAndSetSingleDay(dayToRetrieve);

        numberOfDaysSelected = 1;
        setAggregateDailyTime();
        setAggregateDailyCalories();
    }

    public void setAllDayAndStatListsForWeek(int dayOfWeek, int dayOfYear) {
        List<Integer> populatedDaysOfWeekList = new ArrayList<>();

        int firstDayOfDuration = dayOfYear - (dayOfWeek - 1);
        int lastDayOfDuration = firstDayOfDuration + 6;
        int firstAggregatedDayOfYearToUse = firstDayOfDuration + valueToAddToStartingDurationDayForFutureYears();

        convertToStringAndSetFirstAndLastDurationDays(firstDayOfDuration, lastDayOfDuration);

        for (int i=0; i<7; i++) {
            if (cyclesDatabase.cyclesDao().loadSingleDay(firstAggregatedDayOfYearToUse + i).size()!=0) {
                //If we have day 1 of year, but days 7 of week (when years cross over), value of firstAggregatedDay can be <0. This will ensure only days of week of current year are added. Done for month below as well.
                if (firstAggregatedDayOfYearToUse+i > 0) {
                    populatedDaysOfWeekList.add(firstAggregatedDayOfYearToUse + i);
                }
            }
        }

        populateDayHolderAndStatsForEachActivityLists(populatedDaysOfWeekList);

        numberOfDaysSelected = populatedDaysOfWeekList.size();
        setAggregateDailyTime();
        setAggregateDailyCalories();
    }

    public void setAllDayAndStatListsForMonth(int dayOfMonth, int numberOfDaysInMonth, int dayOfYear) {
        List<Integer> populatedDaysOfMonthList = new ArrayList<>();

        int firstDayOfDuration = dayOfYear - (dayOfMonth-1);
        int lastDayOfDuration = firstDayOfDuration + (numberOfDaysInMonth-1);
        int firstAggregatedDayOfYearToUse = firstDayOfDuration + valueToAddToStartingDurationDayForFutureYears();

        convertToStringAndSetFirstAndLastDurationDays(firstDayOfDuration, lastDayOfDuration);

        for (int i=0; i<numberOfDaysInMonth; i++) {
            if (cyclesDatabase.cyclesDao().loadSingleDay(firstAggregatedDayOfYearToUse + i).size()!=0) {
                if (firstAggregatedDayOfYearToUse + i >0 ) {
                    populatedDaysOfMonthList.add(firstAggregatedDayOfYearToUse + i);
                }
            }
        }

        populateDayHolderAndStatsForEachActivityLists(populatedDaysOfMonthList);

        numberOfDaysSelected = populatedDaysOfMonthList.size();
        setAggregateDailyTime();
        setAggregateDailyCalories();
    }

    public void setAllDayAndStatListsForYearFromDatabase(int daysInYear) {
        List<Integer> populatedDaysOfYearList = new ArrayList<>();

        int firstDayOfDuration = 1;
        int lastDayOfDuration = daysInYear;
        int firstAggregatedDayOfYearToUse = firstDayOfDuration + valueToAddToStartingDurationDayForFutureYears();

        convertToStringAndSetFirstAndLastDurationDays(firstDayOfDuration, lastDayOfDuration);

        //If days exists in database, add it to list of days in year list.
        for (int i=0; i<daysInYear; i++) {
            if (cyclesDatabase.cyclesDao().loadSingleDay(firstAggregatedDayOfYearToUse+i).size()!=0) {
                populatedDaysOfYearList.add(firstAggregatedDayOfYearToUse+i);
            }
        }

        populateDayHolderAndStatsForEachActivityLists(populatedDaysOfYearList);

        numberOfDaysSelected = populatedDaysOfYearList.size();
        setAggregateDailyTime();
        setAggregateDailyCalories();
    }

    public void setAllDayAndStatListsForCustomDatesFromDatabase(List<CalendarDay> calendarDayList, int dayOfYear) {
        List<Integer> populatedCustomDayList = new ArrayList<>();
        int firstDayOfDuration = dayOfYear;
        int lastDayOfDuration = dayOfYear;
        int firstAggregatedDayOfYearToUse = dayOfYear + valueToAddToStartingDurationDayForFutureYears();

        if (calendarDayList.size()>0) {
            firstDayOfDuration = getDayOfYearFromCalendarDayList(calendarDayList.get(0));
            lastDayOfDuration = getDayOfYearFromCalendarDayList(calendarDayList.get(calendarDayList.size()-1));
            firstAggregatedDayOfYearToUse = firstDayOfDuration + valueToAddToStartingDurationDayForFutureYears();
        }

        convertToStringAndSetFirstAndLastDurationDays(firstDayOfDuration, lastDayOfDuration);

        for (int i=0; i<calendarDayList.size(); i++) {
            int dayFromList = getDayOfYearFromCalendarDayList(calendarDayList.get(i));

            if (cyclesDatabase.cyclesDao().loadSingleDay(firstAggregatedDayOfYearToUse+i).size()!=0) {
                populatedCustomDayList.add(dayFromList);
            }
        }

        populateDayHolderAndStatsForEachActivityLists(populatedCustomDayList);

        numberOfDaysSelected = populatedCustomDayList.size();
        setAggregateDailyTime();
        setAggregateDailyCalories();
    }

    private void convertToStringAndSetSingleDay(int day) {
        String dayToSet = convertDayOfYearToFormattedString(day);
        setSingleDayAsString(dayToSet);
    }

    private void convertToStringAndSetFirstAndLastDurationDays(int firstDay,  int lastDay) {
        String firstDayString = convertDayOfYearToFormattedString(firstDay);
        String lastDayString = convertDayOfYearToFormattedString(lastDay);

        setFirstDayInDurationAsString(firstDayString);
        setLastDayInDurationAsString(lastDayString);
    }

    private void setSingleDayAsString(String day) {
        this.mSingleDayAsString = day;
    }

    public String getSingleDayAsString() {
        return mSingleDayAsString;
    }

    private void setFirstDayInDurationAsString(String day) {
        this.mFirstDayInDurationAsString = day;
    }

    public String getFirstDayInDurationAsString() {
        return mFirstDayInDurationAsString;
    }

    private void setLastDayInDurationAsString(String day) {
        this.mLastDayInDurationAsString = day;
    }

    public String getLastDayInDurationAsString() {
        return mLastDayInDurationAsString;
    }

    private String convertDayOfYearToFormattedString(int day) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(Calendar.DAY_OF_YEAR, day);
        Date date = calendar.getTime();
        return simpleDateFormat.format(date);
    }

    public int getDayOfYearFromCalendarDayList(CalendarDay calendarDay){
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(calendarDay.getYear(), calendarDay.getMonth()-1, calendarDay.getDay());
        return calendar.get(Calendar.DAY_OF_YEAR);
    }

    private int valueToAddToStartingDurationDayForFutureYears() {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());

        int year = calendar.get(Calendar.YEAR);
        int numberOfYearsAfter2022 = year - 2022;
        int totalValueToReturn = 0;

        for (int i=0; i<numberOfYearsAfter2022; i++) {
            calendar.set(2022+(i+1), 1, 1);
            int numberOfDaysInYear = calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
            totalValueToReturn += numberOfDaysInYear;
        }

        return totalValueToReturn;
    }

    public List<Integer> getListOfDaysWithAtLeastOneActivity() {
        Calendar calendar = Calendar.getInstance();
        int daysInYear = calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
        List<Integer> listToPopulate = new ArrayList<>();

        for (int i=0; i<daysInYear; i++) {
            if (cyclesDatabase.cyclesDao().loadActivitiesForSpecificDate(i+1).size()!=0) {
                listToPopulate.add(i+1);
            }
        }
        return listToPopulate;
    }

    public long getOldDayHolderId() {
        return mOldDayHolderId;
    }

    public void setOldDayHolderId(int oldId) {
        this.mOldDayHolderId = oldId;
    }

    public void setTotalSetTimeFromDayHolderEntity(long totalSetTime) {
        mDayHolder.setTotalSetTime(totalSetTime);
    }

    public long getTotalSetTimeFromDayHolderEntity() {
        return mDayHolder.getTotalSetTime();
    }

    public void setTotalBreakTimeFromDayHolderEntity(long totalBreakTime) {
        mDayHolder.setTotalBreakTime(totalBreakTime);
    }

    public long getTotalBreakTimeFromDayHolderEntity() {
        return mDayHolder.getTotalBreakTime();
    }

    public void setTotalCaloriesBurnedFromDayHolderEntity(double totalCaloriesBurned) {
        mDayHolder.setTotalCaloriesBurned(totalCaloriesBurned);
    }

    public double getTotalCaloriesBurnedFromDayHolderEntity() {
        return mDayHolder.getTotalCaloriesBurned();
    }

    public long getTotalWorkTimeFromDayHolderEntity() {
        return mDayHolder.getTotalWorkTime();
    }

    public long getTotalRestTimeFromDayHolderEntity() {
        return mDayHolder.getTotalRestTime();
    }

    public void updateTotalTimesAndCaloriesBurnedForCurrentDayFromDatabase() {
        cyclesDatabase.cyclesDao().updateDayHolder(mDayHolder);
    }

    public void deleteMultipleDayHolderEntries(List<Long> listOfEntries) {
        cyclesDatabase.cyclesDao().deleteMultipleDays(listOfEntries);
    }

    public void deleteAllDayHolderEntries() {
        cyclesDatabase.cyclesDao().deleteAllDayHolderEntries();
    }

    //Since DayHolder's dayId and CycleStat's setUniqueDayIdPossessedByEachOfItsActivities are identical, we simply tie StatsForEachActivityWithinCycle's unique ID to that as well.
    public void insertTotalTimesAndCaloriesForEachActivityWithinASpecificDay(int selectedDay) {

        if (!doesActivityExistsInDatabaseForSelectedDay) {
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

    public void setStatForEachActivityListForForSingleDayFromDatabase(int dayToRetrieve) {
        List<Integer> singleDayList = Collections.singletonList(dayToRetrieve);
        mStatsForEachActivityList = cyclesDatabase.cyclesDao().loadActivitiesForMultipleDays(singleDayList);
    }

    public void checkIfActivityExistsForSpecificDayAndSetBooleanForIt() {
        doesActivityExistsInDatabaseForSelectedDay = false;

        //This only returns true once, when our activity matches one in the database.
        for (int i=0; i<mStatsForEachActivityList.size(); i++) {
            if (mActivityString.equals(mStatsForEachActivityList.get(i).getActivity())) {
                doesActivityExistsInDatabaseForSelectedDay = true;
                return;
            }
        }
    }

    public void setActivityPositionInListForCurrentDay() {
        for (int i=0; i<mStatsForEachActivityList.size(); i++) {
            if (mActivityString.equals(mStatsForEachActivityList.get(i).getActivity())) {
                activityPositionInListForCurrentDay = i;
                return;
            }
        }
    }

    public void assignStatForEachActivityInstanceForSpecificActivityWithinSelectedDay() {
        //New database pull to account for most recent insertion.
        if (doesActivityExistsInDatabaseForSelectedDay) {
            mStatsForEachActivity = mStatsForEachActivityList.get(activityPositionInListForCurrentDay);
        } else if (mStatsForEachActivityList.size()>0) {
            //Fetches most recent db insertion as a reference to the new row that was just saved.
            int mostRecentEntryPosition = mStatsForEachActivityList.size()-1;
            mStatsForEachActivity = mStatsForEachActivityList.get(mostRecentEntryPosition);
        } else {
            mStatsForEachActivity = new StatsForEachActivity();
        }
    }

    public void assignStatsForEachActivityEntityForSinglePosition(int position) {
        mStatsForEachActivity = mStatsForEachActivityList.get(position);
    }

    public boolean getDoesActivityExistsInDatabaseForSelectedDay () {
        return doesActivityExistsInDatabaseForSelectedDay;
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

    public void deleteMultipleStatsForEachActivityEntries(List<Long> listOfEntries) {
        cyclesDatabase.cyclesDao().deleteActivityStatsForMultipleDays(listOfEntries);
    }

    public void deleteAllStatsForEachActivityEntries() {
        cyclesDatabase.cyclesDao().deleteAllStatsForEachActivityEntries();
    }

    public void setTotalActivityStatsForSelectedDaysToArrayLists() {
        clearStatsForEachActivityArrayLists();

        for (int i=0; i<mStatsForEachActivityList.size(); i++) {
            if (mStatsForEachActivityList.get(i).getActivity()!=null) {
                if (!doesTotalActivitiesListContainSelectedString(mStatsForEachActivityList.get(i).getActivity())) {
                    totalActivitiesListForSelectedDuration.add(mStatsForEachActivityList.get(i).getActivity());
                    totalSetTimeListForEachActivityForSelectedDuration.add(mStatsForEachActivityList.get(i).getTotalSetTimeForEachActivity());
                    totalCaloriesBurnedListForEachActivityForSelectedDuration.add(mStatsForEachActivityList.get(i).getTotalCaloriesBurnedForEachActivity());
                } else {
                    totalSetTimeListForEachActivityForSelectedDuration.set(duplicateStringPosition, combinedSetTimeFromExistingAndRepeatingPositions(i));
                    totalCaloriesBurnedListForEachActivityForSelectedDuration.set(duplicateStringPosition, combinedCaloriesFromExistingAndRepeatingPositions(i));
                }
            }
        }
    }

    private void clearStatValueAdapterArrayLists() {
        totalActivitiesListForSelectedDuration.clear();
        totalSetTimeListForEachActivityForSelectedDuration.clear();
        totalCaloriesBurnedListForEachActivityForSelectedDuration.clear();
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

    public int returnStatsForEachActivitySizeVariableByQueryingYearlyListOfActivities() {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        int daysInYear = calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
        int numberOfDaysWithAtLeastOneActivity = 0;

        List<Integer> daysOfYearList = new ArrayList<>();

        for (int i=0; i<daysInYear; i++) {
            if (cyclesDatabase.cyclesDao().loadSingleDay(i+1).size()!=0) {
                daysOfYearList.add(i+1);
            }
        }

        List<StatsForEachActivity> statsForEachActivityList = cyclesDatabase.cyclesDao().loadActivitiesForMultipleDays(daysOfYearList);
        for (int i=0; i<statsForEachActivityList.size(); i++) {
            if (statsForEachActivityList.get(i).getActivity()!=null) {
                numberOfDaysWithAtLeastOneActivity++;
            }
        }

        return numberOfDaysWithAtLeastOneActivity;
    }

    public void setOldStatsForEachActivityListSizeVariable(int valueToSet) {
        oldStatsForEachActivityListSize = valueToSet;
    }

    public int getOldStatsForEachActivityListSizeVariable() {
        return oldStatsForEachActivityListSize;
    }

    public void setNewStatsForEachActivityListSizeVariable(int valueToSet) {
        newStatsForEachActivityListSize = valueToSet;
    }

    public int getNewStatsForEachActivityListSizeVariable() {
        return newStatsForEachActivityListSize;
    }

    public long getTotalSetTimeVariableForDayHolder() {
        return totalSetTimeForSelectedDuration;
    }

    public void setTotalSetTimeVariableForDayHolder() {
        long valueToReturn = 0;

        for (int i=0; i<totalSetTimeListForEachActivityForSelectedDuration.size(); i++) {
            valueToReturn += totalSetTimeListForEachActivityForSelectedDuration.get(i);
        }

        totalSetTimeForSelectedDuration = valueToReturn;
    }

    public double getTotalCaloriesVariableForDayHolder() {
        return totalCaloriesForSelectedDuration;
    }

    public void setTotalCaloriesVariableForDayHolder() {
        double valueToReturn = 0;

        for (int i=0; i<totalCaloriesBurnedListForEachActivityForSelectedDuration.size(); i++) {
            valueToReturn += totalCaloriesBurnedListForEachActivityForSelectedDuration.get(i);
        }

        totalCaloriesForSelectedDuration = valueToReturn;
    }

    public long getTotalSetTimeFromDayHolderList() {
        long valueToReturn = 0;

        for (int i=0; i<mDayHolderList.size(); i++) {
            valueToReturn += mDayHolderList.get(i).getTotalSetTime();
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

    public void setUnassignedDailyTotalTime() {
        totalUnassignedSetTimeForSelectedDuration = setZeroLowerBoundsOnLongValue(totalAggregateTimeForSelectedDuration - totalSetTimeForSelectedDuration);
    }

    public long getUnassignedDailyTotalTime() {
        return totalUnassignedSetTimeForSelectedDuration;
    }

    public void setUnassignedTotalCalories() {
        totalUnassignedCaloriesForSelectedDuration = setZeroLowerBoundsOnDoubleValue(totalAggregateCaloriesForSelectedDuration - totalCaloriesForSelectedDuration);
    }

    public double getUnassignedDailyCalories() {
        return totalUnassignedCaloriesForSelectedDuration;
    }

    private void setAggregateDailyTime() {
        long twoHours = 7200000;
        long fullDay = twoHours * 12;
        totalAggregateTimeForSelectedDuration = fullDay * numberOfDaysSelected;
    }

    public long getAggregateDailyTime() {
        return totalAggregateTimeForSelectedDuration;
    }

    private void setAggregateDailyCalories() {
        int savedBmr = sharedPreferences.getInt("savedBmr", 0);
        totalAggregateCaloriesForSelectedDuration = savedBmr * numberOfDaysSelected;
    }

    public double getAggregateDailyCalories() {
        return totalAggregateCaloriesForSelectedDuration;
    }

    private long combinedSetTimeFromExistingAndRepeatingPositions(int position) {
        long iteratingValue = mStatsForEachActivityList.get(position).getTotalSetTimeForEachActivity();
        long presentValue =  totalSetTimeListForEachActivityForSelectedDuration.get(duplicateStringPosition);
        return iteratingValue + presentValue;
    }

    private double combinedCaloriesFromExistingAndRepeatingPositions(int position) {
        double iteratingValue = mStatsForEachActivityList.get(position).getTotalCaloriesBurnedForEachActivity();
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
        totalCaloriesBurnedListForEachActivityForSelectedDuration.clear();
    }

    private long setZeroLowerBoundsOnLongValue(long value) {
        if (value<0) {
            return 0;
        } else {
            return value;
        }
    }

    private double setZeroLowerBoundsOnDoubleValue(double value) {
        if (value<0) {
            return 0;
        } else {
            return value;
        }
    }

    private void instantiateEntitiesAndTheirLists() {
        mDayHolder = new DayHolder();
        mStatsForEachActivity = new StatsForEachActivity();
        mDayHolderList = new ArrayList<>();
        mStatsForEachActivityList = new ArrayList<>();
    }

    private void instantiateArrayListsOfTotalStatsForSelectedDurations() {
        totalActivitiesListForSelectedDuration = new ArrayList<>();
        totalSetTimeListForEachActivityForSelectedDuration = new ArrayList<>();
        totalCaloriesBurnedListForEachActivityForSelectedDuration = new ArrayList<>();
    }

    private void instantiateMiscellaneousClasses() {
        sharedPreferences = mContext.getApplicationContext().getSharedPreferences("pref", 0);
        prefEdit = sharedPreferences.edit();
    }

    private String getDateString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMMM d yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        return simpleDateFormat.format(calendar.getTime());
    }
}