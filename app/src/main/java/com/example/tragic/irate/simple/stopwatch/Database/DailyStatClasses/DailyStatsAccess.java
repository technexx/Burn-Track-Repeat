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
import com.example.tragic.irate.simple.stopwatch.Miscellaneous.LongToStringConverters;
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

    List<String> totalActivitiesListForSelectedDuration;
    List<Long> totalSetTimeListForEachActivityForSelectedDuration;
    List<Double> totalCaloriesBurnedListForEachActivityForSelectedDuration;

    List<String> totalFoodStringListForSelectedDuration;
    List<Double> totalCaloriesConsumedListForSelectedDuration;

    long totalSetTimeForSelectedDuration;
    double totalCaloriesForSelectedDuration;
    long totalUnassignedSetTimeForSelectedDuration;
    double totalUnassignedCaloriesForSelectedDuration;
    long totalAggregateTimeForSelectedDuration;
    double totalAggregateCaloriesForSelectedDuration;

    int mDaySelectedFromCalendar;
    int numberOfDaysSelected;
    boolean mAddingOrEditingSingleDay = true;

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

    List<Integer> mLongListOfActivityDaysSelected;
    List<Integer> mListOfActivityDaysWithPopulatedRows;
    List<Integer> mListOfActivityDaysWithEmptyRows;

    List<Integer> mLongListOfFoodDaysSelected;
    List<Integer> mListOfFoodDaysWithPopulatedRows;
    List<Integer> mListOfFoodDaysWithEmptyRows;

    LongToStringConverters longToStringConverters = new LongToStringConverters();

    public DailyStatsAccess(Context context) {
        this.mContext = context;
        instantiateDailyStatsDatabase();
        instantiateEntitiesAndTheirLists();
        instantiateArrayLists();
        instantiateMiscellaneousClasses();
    }

    public boolean getDoesDayExistInDatabase() {
        return doesDayExistInDatabase;
    }

    public void setActivitySortMode(int sortMode) {
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
    }

    public void assignStatsForEachActivityInstanceForSelectedDay(int daySelected) {
        List<StatsForEachActivity> statsForEachActivityList = cyclesDatabase.cyclesDao().loadActivitiesForSpecificDate(daySelected);

        if (statsForEachActivityList.size()>0) {
            mStatsForEachActivity = statsForEachActivityList.get(0);
        } else {
            mStatsForEachActivity = new StatsForEachActivity();
        }
        Log.i("testStats", "mStats instance time is " + mStatsForEachActivity.getTotalSetTimeForEachActivity());
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
    }

    public List<DayHolder> getDayHolderList() {
        return mDayHolderList;
    }

    public List<StatsForEachActivity> getStatsForEachActivityList() {
        return mStatsForEachActivityList;
    }

    public void setActivityListsForDatabaseObjects(List<Integer> integerListOfDaysSelected) {
        if (integerListOfDaysSelected.size()>0) {
            mDayHolderList = cyclesDatabase.cyclesDao().loadMultipleDays(integerListOfDaysSelected);
            mStatsForEachActivityList = assignStatsForEachActivityListBySortMode(integerListOfDaysSelected);
        } else {
            mDayHolderList = new ArrayList<>();
            mStatsForEachActivityList = new ArrayList<>();
        }
    }

    public void setFoodListsForDatabaseObjects(List<Integer> integerListOfDaysSelected) {
        if (integerListOfDaysSelected.size()>0) {
            mCaloriesForEachFoodList = assignCaloriesForEachFoodListBySortMode(integerListOfDaysSelected);
        } else {
            mCaloriesForEachFoodList = new ArrayList<>();
        }
    }

    public void setAllDayAndStatListsForSingleDay(int daySelected) {
        clearAllActivityAndFoodIntegerDayLists();

        setFullListOfActivityDaysFromDateSelection(daySelected, 1);
        setFullListOfFoodDaysFromDateSelection(daySelected, 1);

        setPopulatedAndEmptyListsOfActivityDays(daySelected);
        setPopulatedAndEmptyListsOfFoodDays(daySelected);

        List<Integer> singleItemList = Collections.singletonList(daySelected);
        setActivityListsForDatabaseObjects(singleItemList);
        setFoodListsForDatabaseObjects(singleItemList);

        numberOfDaysSelected = 1;
        logIntegerAndEntityDayLists();
    }

    public void setAllDayAndStatListsForWeek(int dayOfWeek, int dayOfYear) {
        int firstDayOfDuration = dayOfYear - (dayOfWeek - 1);
        int lastDayOfDuration = firstDayOfDuration + 6;
        int firstAggregatedDayOfYearToUse = firstDayOfDuration + valueToAddToStartingDurationDayForFutureYears();

        convertToStringAndSetFirstAndLastDurationDays(firstDayOfDuration, lastDayOfDuration);

        clearAllActivityAndFoodIntegerDayLists();

        setFullListOfActivityDaysFromDateSelection(firstAggregatedDayOfYearToUse, 7);
        setFullListOfFoodDaysFromDateSelection(firstAggregatedDayOfYearToUse, 7);

        for (int i=0; i<7; i++) {
            if (firstAggregatedDayOfYearToUse + i > 0 ) {
                int dayFromList = (firstAggregatedDayOfYearToUse + i);
                setPopulatedAndEmptyListsOfActivityDays(dayFromList);
                setPopulatedAndEmptyListsOfFoodDays(dayFromList);
            }
        }

        setActivityListsForDatabaseObjects(mListOfActivityDaysWithPopulatedRows);
        setFoodListsForDatabaseObjects(mListOfFoodDaysWithPopulatedRows);

        numberOfDaysSelected = 7;
        logIntegerAndEntityDayLists();
    }

    public void setAllDayAndStatListsForMonth(int dayOfMonth, int numberOfDaysInMonth, int dayOfYear) {
        int firstDayOfDuration = dayOfYear - (dayOfMonth-1);
        int lastDayOfDuration = firstDayOfDuration + (numberOfDaysInMonth-1);
        int firstAggregatedDayOfYearToUse = firstDayOfDuration + valueToAddToStartingDurationDayForFutureYears();

        convertToStringAndSetFirstAndLastDurationDays(firstDayOfDuration, lastDayOfDuration);

        clearAllActivityAndFoodIntegerDayLists();

        setFullListOfActivityDaysFromDateSelection(firstAggregatedDayOfYearToUse, numberOfDaysInMonth);
        setFullListOfFoodDaysFromDateSelection(firstAggregatedDayOfYearToUse, numberOfDaysInMonth);

        for (int i=0; i<numberOfDaysInMonth; i++) {
            if (firstAggregatedDayOfYearToUse + i > 0 ) {
                int dayFromList = (firstAggregatedDayOfYearToUse + i);
                setPopulatedAndEmptyListsOfActivityDays(dayFromList);
                setPopulatedAndEmptyListsOfFoodDays(dayFromList);
            }
        }

        setActivityListsForDatabaseObjects(mListOfActivityDaysWithPopulatedRows);
        setFoodListsForDatabaseObjects(mListOfFoodDaysWithPopulatedRows);

        numberOfDaysSelected = numberOfDaysInMonth;
        logIntegerAndEntityDayLists();
    }

    public void setAllDayAndStatListsForYearFromDatabase(int daysInYear, boolean yearToDate) {
        int firstDayOfDuration = 1;
        int lastDayOfDuration;

        if (yearToDate) {
            lastDayOfDuration = getCurrentDayOfYear();
        } else {
            lastDayOfDuration = daysInYear;
        }

        int firstAggregatedDayOfYearToUse = firstDayOfDuration + valueToAddToStartingDurationDayForFutureYears();

        convertToStringAndSetFirstAndLastDurationDays(firstDayOfDuration, lastDayOfDuration);

        clearAllActivityAndFoodIntegerDayLists();

        setFullListOfActivityDaysFromDateSelection(firstAggregatedDayOfYearToUse, daysInYear);
        setFullListOfFoodDaysFromDateSelection(firstAggregatedDayOfYearToUse, daysInYear);

        for (int i=0; i<daysInYear; i++) {
            if (firstAggregatedDayOfYearToUse + i > 0 ) {
                int dayFromList = (firstAggregatedDayOfYearToUse + i);
                setPopulatedAndEmptyListsOfActivityDays(dayFromList);
                setPopulatedAndEmptyListsOfFoodDays(dayFromList);
            }
        }

        setActivityListsForDatabaseObjects(mListOfActivityDaysWithPopulatedRows);
        setFoodListsForDatabaseObjects(mListOfFoodDaysWithPopulatedRows);

        numberOfDaysSelected = daysInYear;

        logIntegerAndEntityDayLists();
    }

    public void setAllDayAndStatListsForCustomDatesFromDatabase(List<CalendarDay> calendarDayList, int dayOfYear) {
        int firstDayOfDuration = getDayOfYearFromCalendarDayList(calendarDayList.get(0));
        int lastDayOfDuration = getDayOfYearFromCalendarDayList(calendarDayList.get(calendarDayList.size()-1));
        int firstAggregatedDayOfYearToUse = dayOfYear + valueToAddToStartingDurationDayForFutureYears();

        convertToStringAndSetFirstAndLastDurationDays(firstDayOfDuration, lastDayOfDuration);

        clearAllActivityAndFoodIntegerDayLists();

        setFullListOfActivityDaysFromDateSelection(firstAggregatedDayOfYearToUse, calendarDayList.size());
        setFullListOfFoodDaysFromDateSelection(firstAggregatedDayOfYearToUse, calendarDayList.size());

        for (int i=0; i<calendarDayList.size(); i++) {
            int dayFromList = getDayOfYearFromCalendarDayList(calendarDayList.get(i));
            setPopulatedAndEmptyListsOfActivityDays(dayFromList);
            setPopulatedAndEmptyListsOfFoodDays(dayFromList);
        }

        setActivityListsForDatabaseObjects(mListOfActivityDaysWithPopulatedRows);
        setFoodListsForDatabaseObjects(mListOfFoodDaysWithPopulatedRows);

        numberOfDaysSelected = calendarDayList.size();

        logIntegerAndEntityDayLists();
    }

    public int getNumberOfDaysSelected() {
        return numberOfDaysSelected;
    }

    public void setDaySelectedFromCalendar(int daySelected) {
        this.mDaySelectedFromCalendar = daySelected;
    }

    private void setFullListOfActivityDaysFromDateSelection(int firstDay, int sizeOfList) {
        for (int i=0; i<sizeOfList; i++) {
            mLongListOfActivityDaysSelected.add(firstDay + i);
        }
    }

    private void setFullListOfFoodDaysFromDateSelection(int firstDay, int sizeOfList) {
        for (int i=0; i<sizeOfList; i++) {
            mLongListOfFoodDaysSelected.add(firstDay + i);
        }
    }

    private void clearFullListOfActivityDays() {
        mLongListOfActivityDaysSelected.clear();
    }

    private void clearFullListOfFoodFays() {
        mLongListOfFoodDaysSelected.clear();
    }

    private void setPopulatedAndEmptyListsOfActivityDays(int dayToCheck) {
        if (cyclesDatabase.cyclesDao().loadActivitiesForSpecificDate(dayToCheck).size()!=0) {
            mListOfActivityDaysWithPopulatedRows.add(dayToCheck);
        } else {
            mListOfActivityDaysWithEmptyRows.add(dayToCheck);
        }
    }

    private void setPopulatedAndEmptyListsOfFoodDays(int dayToCheck) {
        if (cyclesDatabase.cyclesDao().loadCaloriesForEachFoodForSpecificDay(dayToCheck).size()!=0) {
            mListOfFoodDaysWithPopulatedRows.add(dayToCheck);
        } else {
            mListOfFoodDaysWithEmptyRows.add(dayToCheck);
        }
    }

    private void clearPopulateAndUnpopulatedActivityLists() {
        mListOfActivityDaysWithPopulatedRows.clear();
        mListOfActivityDaysWithEmptyRows.clear();
    }

    private void clearPopulatedAndUnpopulatedFoodLists() {
        mListOfFoodDaysWithPopulatedRows.clear();
        mListOfFoodDaysWithEmptyRows.clear();
    }

    private void clearAllActivityAndFoodIntegerDayLists () {
        clearFullListOfActivityDays();
        clearFullListOfFoodFays();
        clearPopulateAndUnpopulatedActivityLists();
        clearPopulatedAndUnpopulatedFoodLists();
    }

    public void convertToStringAndSetSingleDay(int day) {
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

    public long getTotalSetTimeFromDayHolderEntity() {
        return mDayHolder.getTotalSetTime();
    }

    public long getTotalBreakTimeFromDayHolderEntity() {
        return mDayHolder.getTotalBreakTime();
    }

    public double getTotalCaloriesBurnedFromDayHolderEntity() {
        return mDayHolder.getTotalCaloriesBurned();
    }

    public void deleteMultipleDayHolderEntries(List<Long> listOfEntries) {
        cyclesDatabase.cyclesDao().deleteMultipleDays(listOfEntries);
    }

    public void deleteAllDayHolderEntries() {
        cyclesDatabase.cyclesDao().deleteAllDayHolderEntries();
    }

    public void insertTotalTimesAndCaloriesForEachActivityWithinASpecificDayWithZeroedOutTimesAndCalories(int selectedDay) {
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

    public void insertTotalTimesAndCaloriesForEachActivityForSelectedDays(long setTime, double caloriesBurned) {
        List<Integer> listToPullDaysFrom = new ArrayList<>();

        if (mAddingOrEditingSingleDay) {
            listToPullDaysFrom = Collections.singletonList(mDaySelectedFromCalendar);
        } else {
            listToPullDaysFrom = new ArrayList<>(mLongListOfActivityDaysSelected);
        }

        for (int i=0; i<listToPullDaysFrom.size(); i++) {

            mStatsForEachActivity = new StatsForEachActivity();
            int daySelected = listToPullDaysFrom.get(i);

            if (mListOfActivityDaysWithPopulatedRows.contains(daySelected)) {
                for (int k=0; k<mStatsForEachActivityList.size(); k++) {
                    long uniqueIdToCheck = mStatsForEachActivityList.get(k).getUniqueIdTiedToTheSelectedActivity();
                    String activityToCheck = mStatsForEachActivityList.get(k).getActivity();

                    if (uniqueIdToCheck==daySelected && activityToCheck.equalsIgnoreCase(mActivityString)) {
                        long primaryId = mStatsForEachActivityList.get(k).getStatsForActivityId();
                        mStatsForEachActivity.setStatsForActivityId(primaryId);
                    }
                }
            }

            mStatsForEachActivity.setUniqueIdTiedToTheSelectedActivity(daySelected);
            mStatsForEachActivity.setActivity(mActivityString);
            mStatsForEachActivity.setMetScore(mMetScore);
            mStatsForEachActivity.setTotalSetTimeForEachActivity(setTime);
            mStatsForEachActivity.setTotalCaloriesBurnedForEachActivity(caloriesBurned);

            cyclesDatabase.cyclesDao().insertStatsForEachActivityWithinCycle(mStatsForEachActivity);
        }
    }

    public void setStatsForEachActivityEntityFromPosition(int position) {
        mStatsForEachActivity = mStatsForEachActivityList.get(position);
    }

    public void setMetScoreFromSpinner(double metScore) {
        this.mMetScore = metScore;
    }

    public void setMetScoreFromDatabaseList(int position) {
        mMetScore = mStatsForEachActivityList.get(position).getMetScore();
    }

    public void updateTotalTimesAndCaloriesForEachActivityForSelectedDay(long setTime, double caloriesBurned) {
        mStatsForEachActivity.setTotalSetTimeForEachActivity(setTime);
        mStatsForEachActivity.setTotalCaloriesBurnedForEachActivity(caloriesBurned);

        cyclesDatabase.cyclesDao().updateStatsForEachActivity(mStatsForEachActivity);
    }

    public void deleteTotalTimesAndCaloriesForEachActivityForSelectedDays(int position) {
        if (mAddingOrEditingSingleDay) {
            mStatsForEachActivity = mStatsForEachActivityList.get(position);
            cyclesDatabase.cyclesDao().deleteStatsForEachActivity(mStatsForEachActivity);
        } else {
            String activityToDelete = mStatsForEachActivityList.get(position).getActivity();

            for (int i=0; i<mStatsForEachActivityList.size(); i++) {
                if (mStatsForEachActivityList.get(i).getActivity().equals(activityToDelete)) {
                    mStatsForEachActivity = mStatsForEachActivityList.get(i);
                    cyclesDatabase.cyclesDao().deleteStatsForEachActivity(mStatsForEachActivity);
                }
            }
        }
    }

    public void insertTotalTimesAndCaloriesBurnedForSpecificDayWithZeroedOutTimesAndCalories(int daySelected) {
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
    }

    public void insertTotalTimesAndCaloriesBurnedForSelectedDays(long setTime, double caloriesBurned) {
        List<Integer> listToPullDaysFrom = new ArrayList<>();

        if (mAddingOrEditingSingleDay) {
            listToPullDaysFrom = Collections.singletonList(mDaySelectedFromCalendar);
        } else {
            listToPullDaysFrom = new ArrayList<>(mLongListOfActivityDaysSelected);
        }

        for (int i=0; i<listToPullDaysFrom.size(); i++) {
            mDayHolder = new DayHolder();
            int daySelected = listToPullDaysFrom.get(i);

            mDayHolder.setDayId(daySelected);
            mDayHolder.setTotalSetTime(setTime);
            mDayHolder.setTotalCaloriesBurned(caloriesBurned);

            cyclesDatabase.cyclesDao().insertDay(mDayHolder);
        }
    }

    public void setDayHolderEntityFromStatsForEachActivityDaySelection(int daySelected) {
        for (int i=0; i<mDayHolderList.size(); i++) {
            if (mDayHolderList.get(i).getDayId()==daySelected) {
                mDayHolder = mDayHolderList.get(i);
                Log.i("testdb", "day selected is " + daySelected + " and dayHolder time is " + mDayHolder.getTotalSetTime()/1000/60);
                return;
            }
        }
    }

    public void updateTotalTimesAndCaloriesForSelectedDay(long setTime, double caloriesBurned) {
        mDayHolder.setTotalSetTime(setTime);
        mDayHolder.setTotalCaloriesBurned(caloriesBurned);

        cyclesDatabase.cyclesDao().updateDayHolder(mDayHolder);
    }

    public void setAddingOrEditingSingleDayBoolean(boolean singleDay) {
        this.mAddingOrEditingSingleDay = singleDay;
    }

    public void setDayHolderListForSingleDayFromDatabase(int dayToRetrieve) {
        mDayHolderList = cyclesDatabase.cyclesDao().loadSingleDay(dayToRetrieve);
    }

    //Used by MainActivity.
    public void setStatForEachActivityListForForSingleDayFromDatabase(int dayToRetrieve) {
        List<Integer> singleDayList = Collections.singletonList(dayToRetrieve);
        mStatsForEachActivityList = cyclesDatabase.cyclesDao().loadActivitiesForMultipleDays(singleDayList);
    }

    public void checkIfActivityExistsForSpecificDayAndSetBooleanForIt() {
        doesActivityExistsInDatabaseForSelectedDay = false;

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

    public void setActivityString(String activityString) {
        this.mActivityString = activityString;
    }

    public long getTotalSetTimeForSelectedActivity() {
        return mStatsForEachActivity.getTotalSetTimeForEachActivity();
    }

    public long getTotalBreakTimeForSelectedActivity() {
        return mStatsForEachActivity.getTotalBreakTimeForEachActivity();
    }

    public double getMetScore() {
        return mMetScore;
    }

    public double getTotalCaloriesBurnedForSelectedActivity() {
        return mStatsForEachActivity.getTotalCaloriesBurnedForEachActivity();
    }

    public void deleteMultipleStatsForEachActivityEntries(List<Long> listOfEntries) {
        cyclesDatabase.cyclesDao().deleteActivityStatsForMultipleDays(listOfEntries);
    }

    public void deleteAllStatsForEachActivityEntries() {
        cyclesDatabase.cyclesDao().deleteAllStatsForEachActivityEntries();
    }

    public long getTotalActivityTimeForSingleDay(int day) {
        long valueToReturn = 0;

        for (int i=0; i<mStatsForEachActivityList.size(); i++) {
            if (mStatsForEachActivityList.get(i).getUniqueIdTiedToTheSelectedActivity()==day) {
                valueToReturn += mStatsForEachActivityList.get(i).getTotalSetTimeForEachActivity();
            }
        }

        return valueToReturn;
    }

    public double getTotalCaloriesBurnedForSingleDay(int day) {
        double valueToReturn = 0;

        for (int i=0; i<mStatsForEachActivityList.size(); i++) {
            if (mStatsForEachActivityList.get(i).getUniqueIdTiedToTheSelectedActivity()==day) {
                valueToReturn += mStatsForEachActivityList.get(i).getTotalCaloriesBurnedForEachActivity();
            }
        }

        return valueToReturn;
    }

    public long getTotalActivityTimeForSelectedDuration() {
        long valueToReturn = 0;

        for (int i=0; i<totalSetTimeListForEachActivityForSelectedDuration.size(); i++) {
            valueToReturn += totalSetTimeListForEachActivityForSelectedDuration.get(i);
        }

        return valueToReturn;
    }

    public double getTotalCaloriesBurnedForSelectedDuration() {
        double valueToReturn = 0;

        for (int i=0; i<totalCaloriesBurnedListForEachActivityForSelectedDuration.size(); i++) {
            valueToReturn += totalCaloriesBurnedListForEachActivityForSelectedDuration.get(i);
        }

        return valueToReturn;
    }

    // These are the lists pulled by recyclerView.
    public void setTotalActivityStatsForSelectedDaysToArrayLists() {
        clearStatsForEachActivityArrayLists();

        for (int i=0; i<mStatsForEachActivityList.size(); i++) {
            if (mStatsForEachActivityList.get(i).getActivity()!=null) {
                if (!doesTotalActivitiesListContainSelectedString(mStatsForEachActivityList.get(i).getActivity())) {


                    totalActivitiesListForSelectedDuration.add(mStatsForEachActivityList.get(i).getActivity());
                    totalSetTimeListForEachActivityForSelectedDuration.add(mStatsForEachActivityList.get(i).getTotalSetTimeForEachActivity());

                    double caloriesToAdd = roundDownCalories(mStatsForEachActivityList.get(i).getTotalCaloriesBurnedForEachActivity());
                    totalCaloriesBurnedListForEachActivityForSelectedDuration.add(caloriesToAdd);

                    Log.i("testTotal", "individual activity calories for dayHolder in Retrieval are " +  mStatsForEachActivityList.get(i).getTotalCaloriesBurnedForEachActivity());

                } else {
                    totalSetTimeListForEachActivityForSelectedDuration.set(duplicateStringPosition, combinedSetTimeFromExistingAndRepeatingPositions(i));
                    totalCaloriesBurnedListForEachActivityForSelectedDuration.set(duplicateStringPosition, combinedCaloriesFromExistingAndRepeatingPositions(i));
                }
            }
        }
    }

    private double roundDownCalories(double calories) {
        return Math.floor(calories);
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

    public void setTotalSetTimeVariableForSelectedDuration() {
        long valueToReturn = 0;

        for (int i=0; i<totalSetTimeListForEachActivityForSelectedDuration.size(); i++) {
            valueToReturn += totalSetTimeListForEachActivityForSelectedDuration.get(i);
            Log.i("testdb", "activity times being added are " + totalSetTimeListForEachActivityForSelectedDuration.get(i)/1000/60);
        }

        totalSetTimeForSelectedDuration = valueToReturn;
    }

    public long getTotalSetTimeForSelectedDuration() {
        return totalSetTimeForSelectedDuration;
    }

    public void setTotalCaloriesVariableForSelectedDuration() {
        double valueToReturn = 0;

        for (int i=0; i<totalCaloriesBurnedListForEachActivityForSelectedDuration.size(); i++) {
            valueToReturn += totalCaloriesBurnedListForEachActivityForSelectedDuration.get(i);
        }

        totalCaloriesForSelectedDuration = valueToReturn;
    }

    public double getTotalCalorieBurnedForSelectedDuration() {
        return totalCaloriesForSelectedDuration;
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
        double iteratingValue = roundDownCalories(mStatsForEachActivityList.get(position).getTotalCaloriesBurnedForEachActivity());
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

    public void insertCaloriesAndEachFoodIntoDatabase() {
        List<Integer> listToPullDaysFrom = new ArrayList<>();

        if (mAddingOrEditingSingleDay) {
            listToPullDaysFrom = Collections.singletonList(mDaySelectedFromCalendar);
        } else {
            listToPullDaysFrom = new ArrayList<>(mLongListOfActivityDaysSelected);
        }

        for (int i=0; i<listToPullDaysFrom.size(); i++) {
            mCaloriesForEachFood = new CaloriesForEachFood();
            int daySelected = listToPullDaysFrom.get(i);

            //This overwrites row if food String exists.
//            if (mListOfActivityDaysWithPopulatedRows.contains(daySelected)) {
//                for (int k=0; k<mCaloriesForEachFoodList.size(); k++) {
//                    long uniqueIdToCheck = mCaloriesForEachFoodList.get(k).getUniqueIdTiedToEachFood();
//                    String foodStringToCheck = mCaloriesForEachFoodList.get(k).getTypeOfFood();
//
//                    if (uniqueIdToCheck==daySelected && foodStringToCheck.equalsIgnoreCase(mFoodString)) {
//                        long primaryId = mCaloriesForEachFoodList.get(k).getCaloriesForEachFoodId();
//                        mCaloriesForEachFood.setCaloriesForEachFoodId(primaryId);
//                    }
//                }
//            }

            mCaloriesForEachFood.setUniqueIdTiedToEachFood(daySelected);
            mCaloriesForEachFood.setTypeOfFood(mFoodString);
            mCaloriesForEachFood.setCaloriesConsumedForEachFoodType(mCaloriesInFoodItem);

            cyclesDatabase.cyclesDao().insertCaloriesForEachFoodRow(mCaloriesForEachFood);
        }
    }

    public void updateCaloriesAndEachFoodInDatabase(int position, String food, double calories) {
        mCaloriesForEachFood = mCaloriesForEachFoodList.get(position);
        mCaloriesForEachFood.setTypeOfFood(food);
        mCaloriesForEachFood.setCaloriesConsumedForEachFoodType(calories);

        cyclesDatabase.cyclesDao().updateCaloriesForEachFoodRow(mCaloriesForEachFood);
    }

    public void deleteCaloriesAndEachFoodInDatabase(int position) {
        if (mAddingOrEditingSingleDay) {
            mCaloriesForEachFood = mCaloriesForEachFoodList.get(position);
            cyclesDatabase.cyclesDao().deleteCaloriesForEachFoodRow(mCaloriesForEachFood);
        } else {
            String foodToDelete = mCaloriesForEachFoodList.get(position).getTypeOfFood();

            for (int i=0; i<mCaloriesForEachFoodList.size(); i++) {
                if (mCaloriesForEachFoodList.get(i).getTypeOfFood().equals(foodToDelete)) {
                    mCaloriesForEachFood = mCaloriesForEachFoodList.get(i);
                    cyclesDatabase.cyclesDao().deleteCaloriesForEachFoodRow(mCaloriesForEachFood);
                }
            }
        }
    }

    public void setFoodString(String food) {
        this.mFoodString = food;
    }

    public String getFoodString() {
        return mFoodString;
    }

    public void setCaloriesInFoodItem(double calories) {
        this.mCaloriesInFoodItem = calories;
    }

    public void assignCaloriesForEachFoodItemEntityForSinglePosition(int position) {
        mCaloriesForEachFood = mCaloriesForEachFoodList.get(position);
    }

    public void setTotalFoodStringListForSelectedDuration() {
        totalFoodStringListForSelectedDuration.clear();

        for (int i=0; i<mCaloriesForEachFoodList.size(); i++) {
            totalFoodStringListForSelectedDuration.add(mCaloriesForEachFoodList.get(i).getTypeOfFood());
        }
    }

    public List<String> getTotalFoodStringListForSelectedDuration() {
        return totalFoodStringListForSelectedDuration;
    }

    public void setTotalCaloriesConsumedListForSelectedDuration() {
        totalCaloriesConsumedListForSelectedDuration.clear();

        for (int i=0; i<mCaloriesForEachFoodList.size(); i++) {
            totalCaloriesConsumedListForSelectedDuration.add(mCaloriesForEachFoodList.get(i).getCaloriesConsumedForEachFoodType());
        }
    }

    public List<Double> getTotalCaloriesConsumedListForSelectedDuration() {
        return totalCaloriesConsumedListForSelectedDuration;
    }

    public double getTotalCaloriesConsumedForSelectedDuration() {
        double valueToReturn = 0;
        for (int i=0; i<totalCaloriesConsumedListForSelectedDuration.size(); i++) {
            valueToReturn += totalCaloriesConsumedListForSelectedDuration.get(i);
        }
        return valueToReturn;
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

    private void instantiateArrayLists() {
        totalActivitiesListForSelectedDuration = new ArrayList<>();
        totalSetTimeListForEachActivityForSelectedDuration = new ArrayList<>();
        totalCaloriesBurnedListForEachActivityForSelectedDuration = new ArrayList<>();

        totalFoodStringListForSelectedDuration = new ArrayList<>();
        totalCaloriesConsumedListForSelectedDuration = new ArrayList<>();

        mLongListOfActivityDaysSelected = new ArrayList<>();
        mListOfActivityDaysWithPopulatedRows = new ArrayList<>();
        mListOfActivityDaysWithEmptyRows = new ArrayList<>();

        mLongListOfFoodDaysSelected = new ArrayList<>();
        mListOfFoodDaysWithPopulatedRows = new ArrayList<>();
        mListOfFoodDaysWithEmptyRows = new ArrayList<>();
    }

    private void instantiateMiscellaneousClasses() {
        sharedPreferences = mContext.getApplicationContext().getSharedPreferences("pref", 0);
        prefEdit = sharedPreferences.edit();
    }

    private int getCurrentDayOfYear() {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        return calendar.get(Calendar.DAY_OF_YEAR);
    }

    private String getDateString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMMM d yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        return simpleDateFormat.format(calendar.getTime());
    }

    private void logEntityLists() {
        Log.i("testList", "mStats list size is " + mCaloriesForEachFoodList.size());
        for (int i=0; i<mStatsForEachActivityList.size(); i++) {
            Log.i("testList", "mFood foods in list are " + mCaloriesForEachFoodList.get(i).getTypeOfFood());
        }

        Log.i("testList", "mFood list size is " + mStatsForEachActivityList.size());
        for (int i=0; i<mStatsForEachActivityList.size(); i++) {
            Log.i("testList", "mStats activities in list are " + mStatsForEachActivityList.get(i).getActivity());
        }
    }

    private void logIntegerAndEntityDayLists() {
        Log.i("testList", "total activity list is " + mLongListOfActivityDaysSelected);
        Log.i("testList", "populated activity list is " + mListOfActivityDaysWithPopulatedRows);
        Log.i("testList", "unpopulated activity list is " + mListOfActivityDaysWithEmptyRows);

        Log.i("testList", "total food list is " + mLongListOfFoodDaysSelected);
        Log.i("testList", "populated food list is " + mListOfFoodDaysWithPopulatedRows);
        Log.i("testList", "unpopulated food list is " + mListOfFoodDaysWithEmptyRows);
    }


    private void logSetTimeValues() {
        Log.i("testCals", "total assigned set is " + totalSetTimeForSelectedDuration);
        Log.i("testCals", "total aggregate set is " + totalAggregateTimeForSelectedDuration);
    }

    private void logActivityCalorieValues() {
        Log.i("testCals", "total assigned calories are " + totalCaloriesForSelectedDuration);
        Log.i("testCals", "total unassigned calories are " + totalUnassignedCaloriesForSelectedDuration);
        Log.i("testCals", "total aggregate calories are " + totalAggregateCaloriesForSelectedDuration);
        Log.i("testCals", "decimal pct is " + decimalPercentageOfUnAssignedTime());
    }
}