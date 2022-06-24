package com.example.tragic.irate.simple.stopwatch.Database.DailyStatClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.tragic.irate.simple.stopwatch.Database.CyclesDatabase;
import com.example.tragic.irate.simple.stopwatch.Database.DailyCalorieClasses.CalorieDayHolder;
import com.example.tragic.irate.simple.stopwatch.Database.DailyCalorieClasses.CaloriesForEachFood;
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
    CalorieDayHolder mCalorieDayHolder;
    CaloriesForEachFood mCaloriesForEachFood;

    List<DayHolder> mDayHolderList;
    List<StatsForEachActivity> mStatsForEachActivityList;
    List<CalorieDayHolder> mCalorieDayHolderList;
    List<CaloriesForEachFood> mCaloriesForEachFoodList;

    int oldStatsForEachActivityListSize;
    int newStatsForEachActivityListSize;

    List<String> totalActivitiesListForSelectedDuration;
    List<Long> totalSetTimeListForEachActivityForSelectedDuration;
    List<Double> totalCaloriesBurnedListForEachActivityForSelectedDuration;

    List<String> totalFoodStringListForSelectedDuration;
    List<Double> totalFoodPortionListForSelectedDuration;
    List<Double> totalCaloriesConsumedListForSelectedDuration;

    long totalSetTimeForSelectedDuration;
    double totalCaloriesForSelectedDuration;
    long totalUnassignedSetTimeForSelectedDuration;
    double totalUnassignedCaloriesForSelectedDuration;
    long totalAggregateTimeForSelectedDuration;
    double totalAggregateCaloriesForSelectedDuration;

    int typeOfFoodPositionInListForCurrentDay;

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
    int mActivitySortMode = 1;

    String mFoodString = "";
    Double mCaloriesInFoodItem;
    int mCalorieSortMode = 1;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor prefEdit;

    public DailyStatsAccess(Context context) {
        this.mContext = context;
        instantiateDailyStatsDatabase();
        instantiateEntitiesAndTheirLists();
        instantiateArrayListsOfTotalStatsForSelectedDurations();
        instantiateMiscellaneousClasses();
    }

    public boolean getDoesDayExistInDatabase() {
        return doesDayExistInDatabase;
    }

    public void setSortMode(int sortMode) {
        this.mActivitySortMode = sortMode;
    }

    public List<StatsForEachActivity> assignStatsForEachActivityListBySortMode(List<Integer> listOfDays) {
        List<StatsForEachActivity> listToReturn = new ArrayList<>();

        switch (mActivitySortMode) {
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

    public List<CaloriesForEachFood> assignCaloriesForEachFoodListBySortMode(List<Integer> listOfDays) {
        List<CaloriesForEachFood> listToReturn = new ArrayList<>();

        switch (mCalorieSortMode) {
            case 1:
                listToReturn = cyclesDatabase.cyclesDao().loadCaloriesForEachFoodByAToZName(listOfDays);
                break;
            case 2:
                listToReturn = cyclesDatabase.cyclesDao().loadCaloriesForEachFoodByZToAName(listOfDays);
                break;
            case 3:
                listToReturn = cyclesDatabase.cyclesDao().loadCaloriesForEachFoodByLargestPortion(listOfDays);
                break;
            case 4:
                listToReturn = cyclesDatabase.cyclesDao().loadCaloriesForEachFoodBySmallestPortion(listOfDays);
                break;
            case 5:
                listToReturn = cyclesDatabase.cyclesDao().loadCaloriesForEachFoodByMostCaloriesBurned(listOfDays);
                break;
            case 6:
                listToReturn = cyclesDatabase.cyclesDao().loadCaloriesForEachFoodByLeastCaloriesBurned(listOfDays);
                break;

        }

        return listToReturn;
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

    private void populateDayHolderAndStatsForEachActivityLists(List<Integer> integerListOfSelectedDays) {
        if (integerListOfSelectedDays.size()>0) {
            mDayHolderList = cyclesDatabase.cyclesDao().loadMultipleDays(integerListOfSelectedDays);
            mStatsForEachActivityList = assignStatsForEachActivityListBySortMode(integerListOfSelectedDays);
            mCalorieDayHolderList = cyclesDatabase.cyclesDao().loadMultiplCalorieDays(integerListOfSelectedDays);
            mCaloriesForEachFoodList = assignCaloriesForEachFoodListBySortMode(integerListOfSelectedDays);
        } else {
            mDayHolderList = new ArrayList<>();
            mStatsForEachActivityList = new ArrayList<>();
            mCalorieDayHolderList = new ArrayList<>();
            mCaloriesForEachFoodList = new ArrayList<>();
        }
    }

    public List<DayHolder> getDayHolderList() {
        return mDayHolderList;
    }

    public List<StatsForEachActivity> getStatsForEachActivityList() {
        return mStatsForEachActivityList;
    }

    public List<CalorieDayHolder> getCalorieDayHolderList() {
        return mCalorieDayHolderList;
    }

    public List<CaloriesForEachFood> getCaloriesForEachFoodList() {
        return mCaloriesForEachFoodList;
    }

    ///////////////////////////////////////////////////////////
    public List<String> getTotalFoodStringListForSelectedDuration() {
        return totalFoodStringListForSelectedDuration;
    }

    public List<Double> getTotalFoodPortionListForSelectedDuration() {
        return totalFoodPortionListForSelectedDuration;
    }


    public List<Double> getTotalCaloriesConsumedForSelectedDuration() {
       return totalCaloriesConsumedListForSelectedDuration;
    }

    public void setTotalCaloriesConsumedStatsForSelectedDayToArrayLists() {
        for (int i=0; i<mCaloriesForEachFoodList.size(); i++) {
            totalFoodStringListForSelectedDuration.add(mCaloriesForEachFoodList.get(i).getTypeOfFood());
            totalFoodPortionListForSelectedDuration.add(mCaloriesForEachFoodList.get(i).getPortionForEachFoodType());
            totalCaloriesConsumedListForSelectedDuration.add(mCaloriesForEachFoodList.get(i).getCaloriesConsumedForEachFoodType());
        }
    }

    public void clearCaloriesForEachFoodListArrayLists() {
        totalFoodStringListForSelectedDuration.clear();
        totalFoodPortionListForSelectedDuration.clear();
        totalCaloriesConsumedListForSelectedDuration.clear();
    }

    ////////////////////////////////////////////////////////////////////

    public void setDayHolderAndStatForEachActivityListsForSelectedDayFromDatabase(int dayToRetrieve) {
        mDayHolderList = cyclesDatabase.cyclesDao().loadSingleDay(dayToRetrieve);

        List<Integer> singleDayListForActivities = Collections.singletonList(dayToRetrieve);
        mStatsForEachActivityList = assignStatsForEachActivityListBySortMode(singleDayListForActivities);

        mCalorieDayHolderList = cyclesDatabase.cyclesDao().loadSingleCalorieDay(dayToRetrieve);

        List<Integer> singleDayListForCaloriesConsumed = Collections.singletonList(dayToRetrieve);
        mCaloriesForEachFoodList = assignCaloriesForEachFoodListBySortMode(singleDayListForCaloriesConsumed);

        convertToStringAndSetSingleDay(dayToRetrieve);

        numberOfDaysSelected = 1;
    }

    public void setAllDayAndStatListsForWeek(int dayOfWeek, int dayOfYear) {
        List<Integer> populatedDaysOfWeekList = new ArrayList<>();

        int firstDayOfDuration = dayOfYear - (dayOfWeek - 1);
        int lastDayOfDuration = firstDayOfDuration + 6;
        //Aggregated is used for database IDs only. Anything fetched from calendar should still fall w/ in standard year.
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

        numberOfDaysSelected = 7;
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

        numberOfDaysSelected = numberOfDaysInMonth;
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

        numberOfDaysSelected = daysInYear;
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

    public void updateTotalTimesAndCaloriesBurnedForCurrentDayFromDatabase() {
        cyclesDatabase.cyclesDao().updateDayHolder(mDayHolder);
    }

    public void deleteMultipleDayHolderEntries(List<Long> listOfEntries) {
        cyclesDatabase.cyclesDao().deleteMultipleDays(listOfEntries);
    }

    public void deleteAllDayHolderEntries() {
        cyclesDatabase.cyclesDao().deleteAllDayHolderEntries();
    }


    //Todo: Add insert/update/delete methods from DAO and call those in our fragment.
    //Todo: We may not even need DayHolder or CalorieDayHolder since we're getting all our total values from their sibling classes. Their IDs are represented by uniqueIDs and their dates can be fetched as a String from that ID.

    public void insertFoodAndItsCaloriesIntoDatabase(int daySelected) {
        mCaloriesForEachFood = new CaloriesForEachFood();

        mCaloriesForEachFood.setUniqueIdTiedToEachFood(daySelected);
        mCaloriesForEachFood.setTypeOfFood(mFoodString);
        mCaloriesForEachFood.setCaloriesConsumedForEachFoodType(mCaloriesInFoodItem);

        cyclesDatabase.cyclesDao().insertCaloriesForEachFoodRow(mCaloriesForEachFood);
    }

    public void setFoodString(String food) {
        this.mFoodString = food;
    }

    public void setCaloriesInFoodItem(double calories) {
        this.mCaloriesInFoodItem = calories;
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

    public void clearStatsForEachActivityArrayLists() {
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
        totalUnassignedCaloriesForSelectedDuration = bmrCaloriesBurned() * decimalPercentageOfUnAssignedTime();
    }

    public double getUnassignedDailyCalories() {
        return totalUnassignedCaloriesForSelectedDuration;
    }

    public void setAggregateDailyTime() {
        long twoHours = 7200000;
        long fullDay = twoHours * 12;
        totalAggregateTimeForSelectedDuration = fullDay * numberOfDaysSelected;
    }

    public long getAggregateDailyTime() {
        return totalAggregateTimeForSelectedDuration;
    }

    public void setAggregateDailyCalories() {
        totalAggregateCaloriesForSelectedDuration = totalCaloriesForSelectedDuration + getUnassignedDailyCalories();
    }

    public double getAggregateDailyCalories() {
        return totalAggregateCaloriesForSelectedDuration;
    }

    private int bmrCaloriesBurned() {
        int savedBmr = sharedPreferences.getInt("savedBmr", 0);
        return savedBmr * numberOfDaysSelected;
    }

    private double decimalPercentageOfUnAssignedTime() {
        double remainingTime = (double) totalSetTimeForSelectedDuration / totalAggregateTimeForSelectedDuration;
        return 1 - remainingTime;
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

    public List<Double> getTotalCaloriesBurnedListForEachActivityForSelectedDuration() {
        return totalCaloriesBurnedListForEachActivityForSelectedDuration;
    }

    private long setZeroLowerBoundsOnLongValue(long value) {
        if (value<0) {
            return 0;
        } else {
            return value;
        }
    }

    private void instantiateDailyStatsDatabase() {
        AsyncTask.execute(()->{
            cyclesDatabase = CyclesDatabase.getDatabase(mContext);
        });
    }

    private void instantiateEntitiesAndTheirLists() {
        mDayHolder = new DayHolder();
        mStatsForEachActivity = new StatsForEachActivity();
        mDayHolderList = new ArrayList<>();
        mStatsForEachActivityList = new ArrayList<>();

        mCalorieDayHolder = new CalorieDayHolder();
        mCaloriesForEachFood = new CaloriesForEachFood();
        mCalorieDayHolderList = new ArrayList<>();
        mCaloriesForEachFoodList = new ArrayList<>();
    }

    private void instantiateArrayListsOfTotalStatsForSelectedDurations() {
        totalActivitiesListForSelectedDuration = new ArrayList<>();
        totalSetTimeListForEachActivityForSelectedDuration = new ArrayList<>();
        totalCaloriesBurnedListForEachActivityForSelectedDuration = new ArrayList<>();

        totalFoodStringListForSelectedDuration = new ArrayList<>();
        totalFoodPortionListForSelectedDuration = new ArrayList<>();
        totalCaloriesConsumedListForSelectedDuration = new ArrayList<>();
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

    private void logSetTimeValues() {
        Log.i("testCals", "total assigned set is " + totalSetTimeForSelectedDuration);
        Log.i("testCals", "total aggregate set is " + totalAggregateTimeForSelectedDuration);
    }

    private void logCalorieValues() {
        Log.i("testCals", "total assigned calories are " + totalCaloriesForSelectedDuration);
        Log.i("testCals", "total unassigned calories are " + totalUnassignedCaloriesForSelectedDuration);
        Log.i("testCals", "total aggregate calories are " + totalAggregateCaloriesForSelectedDuration);
        Log.i("testCals", "decimal pct is " + decimalPercentageOfUnAssignedTime());

    }
}