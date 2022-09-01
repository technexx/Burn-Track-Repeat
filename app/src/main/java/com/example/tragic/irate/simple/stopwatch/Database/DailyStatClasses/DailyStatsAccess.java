package com.example.tragic.irate.simple.stopwatch.Database.DailyStatClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.tragic.irate.simple.stopwatch.Database.CyclesDatabase;
import com.example.tragic.irate.simple.stopwatch.Database.DailyCalorieClasses.CalorieDayHolder;
import com.example.tragic.irate.simple.stopwatch.Database.DailyCalorieClasses.CaloriesForEachFood;
import com.example.tragic.irate.simple.stopwatch.Database.DailyStatClasses.DayHolder;
import com.example.tragic.irate.simple.stopwatch.Database.DailyStatClasses.StatsForEachActivity;
import com.example.tragic.irate.simple.stopwatch.Miscellaneous.LongToStringConverters;
import com.example.tragic.irate.simple.stopwatch.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class DailyStatsAccess {
    Context mContext;
    CyclesDatabase cyclesDatabase;

    DayHolder mDayHolder;
    StatsForEachActivity mStatsForEachActivity;
    CaloriesForEachFood mCaloriesForEachFood;

    List<DayHolder> mDayHolderList;
    List<StatsForEachActivity> mStatsForEachActivityList;
    List<CaloriesForEachFood> mCaloriesForEachFoodList;

    List<String> totalActivitiesListForSelectedDuration;
    List<Long> totalSetTimeListForEachActivityForSelectedDuration;
    List<Double> totalCaloriesBurnedListForEachActivityForSelectedDuration;

    List<String> totalFoodStringListForSelectedDuration;
    List<Double> totalCaloriesConsumedListForSelectedDuration;
    double totalCaloriesConsumedForSelectedDuration;

    long totalSetTimeForSelectedDuration;
    double totalActivityCaloriesForSelectedDuration;
    long totalUnassignedSetTimeForSelectedDuration;
    double totalUnassignedCaloriesForSelectedDuration;
    long totalAggregateTimeForSelectedDuration;
    double totalAggregateCaloriesForSelectedDuration;

    int mDaySelectedFromCalendar;
    int numberOfDaysSelected;
    int firstDayOfDuration;
    int lastDayOfDuration;
    int valueAddedForFutureYears;

    String mSingleDayAsString;
    String mFirstDayInDurationAsString;
    String mLastDayInDurationAsString;

    long mOldDayHolderId;
    boolean doesDayExistInDatabase;

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

    List<Integer> mListOfActivityDaysSelected;
    List<Integer> mListOfActivityDaysWithPopulatedRows;
    List<Integer> mListOfActivityDaysWithEmptyRows;

    List<Integer> mListOfFoodDaysSelected;
    List<Integer> mListOfFoodDaysWithPopulatedRows;
    List<Integer> mListOfFoodDaysWithEmptyRows;

    boolean mIsActivityCustom;
    double mCaloriesBurnedPerHour;

    public DailyStatsAccess(Context context) {
        this.mContext = context;
        instantiateDailyStatsDatabase();
        instantiateEntitiesAndTheirLists();
        instantiateArrayLists();
        instantiateMiscellaneousClasses();
    }

    public Set<String> getDuplicateActivityAndUniqueIdRows() {
        List<StatsForEachActivity> statsForEachActivityList = cyclesDatabase.cyclesDao().loadAllStatsForEachActivityRows();
        List<Long> idList = new ArrayList<>();
        List<String> activityList = new ArrayList<>();

        Set<Long> idSetToPopulate = new HashSet<>();
        Set<Integer> uniqueIdSet = new HashSet<>();

        Set<String> activitySetToPopulate = new HashSet<>();
        Set<String> duplicateStringSet = new HashSet<>();

        for (int i=0; i<statsForEachActivityList.size(); i++) {
            idList.add(statsForEachActivityList.get(i).getStatsForActivityId());
            activityList.add(statsForEachActivityList.get(i).getActivity());
        }

        for (long idToAdd: idList) {
            if (!idSetToPopulate.add(idToAdd)) {
                uniqueIdSet.add((int)idToAdd);
            }
        }

        List<Integer> integerListOfUniqueIds = new ArrayList();
        integerListOfUniqueIds.addAll(uniqueIdSet);

        List<StatsForEachActivity> statsForEachActivityListWithOnlyUniqueIds = cyclesDatabase.cyclesDao().loadActivitiesForMultipleDays(integerListOfUniqueIds);
        List<String> stringListOfUniqueIdDays = new ArrayList<>();

        for (int i=0; i<statsForEachActivityListWithOnlyUniqueIds.size(); i++) {
            stringListOfUniqueIdDays.add(statsForEachActivityListWithOnlyUniqueIds.get(i).getActivity());
        }

        for (String stringToTest: stringListOfUniqueIdDays) {
            if (activitySetToPopulate.add(stringToTest)) {
                duplicateStringSet.add(stringToTest);
            }
        }
        return duplicateStringSet;
    }



    public void setDoesDayExistInDatabase(boolean doesExist) {
        this.doesDayExistInDatabase = doesExist;
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

    public List<DayHolder> getDayHolderList() {
        return mDayHolderList;
    }

    public List<StatsForEachActivity> getStatsForEachActivityList() {
        return mStatsForEachActivityList;
    }

    public List<StatsForEachActivity> getStatsForEachActivityListForSelectedDay(int dayId) {
        return cyclesDatabase.cyclesDao().loadActivitiesForSpecificDate(dayId);
    }

    public List<CaloriesForEachFood> getCaloriesForEachFoodList() {
        return mCaloriesForEachFoodList;
    }

    public void setActivityListsForDatabaseObjectsForStatsFragment(List<Integer> integerListOfDaysSelected) {
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
        setActivityListsForDatabaseObjectsForStatsFragment(singleItemList);
        setFoodListsForDatabaseObjects(singleItemList);

        numberOfDaysSelected = 1;
    }

    public void setAllDayAndStatListsForWeek(int dayOfWeek, int dayOfYear) {
        firstDayOfDuration = dayOfYear - (dayOfWeek - 1);
        lastDayOfDuration = firstDayOfDuration + 6;
        int firstAggregatedDayOfYearToUse = firstDayOfDuration + valueAddedForFutureYears;

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

        setActivityListsForDatabaseObjectsForStatsFragment(mListOfActivityDaysWithPopulatedRows);
        setFoodListsForDatabaseObjects(mListOfFoodDaysWithPopulatedRows);

        numberOfDaysSelected = 7;
    }

    public void setAllDayAndStatListsForMonth(int dayOfMonth, int numberOfDaysInMonth, int dayOfYear) {
        firstDayOfDuration = dayOfYear - (dayOfMonth-1);
        lastDayOfDuration = firstDayOfDuration + (numberOfDaysInMonth-1);
        int firstAggregatedDayOfYearToUse = firstDayOfDuration + valueAddedForFutureYears;

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

        setActivityListsForDatabaseObjectsForStatsFragment(mListOfActivityDaysWithPopulatedRows);
        setFoodListsForDatabaseObjects(mListOfFoodDaysWithPopulatedRows);

        numberOfDaysSelected = numberOfDaysInMonth;
    }

    public void setAllDayAndStatListsForYearFromDatabase(int daysInYear, boolean yearToDate) {
        firstDayOfDuration = 1;

        if (yearToDate) {
            lastDayOfDuration = getCurrentDayOfYear();
            numberOfDaysSelected = getCurrentDayOfYear();
        } else {
            lastDayOfDuration = daysInYear;
            numberOfDaysSelected = daysInYear;
        }

        int firstAggregatedDayOfYearToUse = firstDayOfDuration + valueAddedForFutureYears;

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

        setActivityListsForDatabaseObjectsForStatsFragment(mListOfActivityDaysWithPopulatedRows);
        setFoodListsForDatabaseObjects(mListOfFoodDaysWithPopulatedRows);
    }

    public void setAllDayAndStatListsForCustomDatesFromDatabase(List<CalendarDay> calendarDayList, int dayOfYear) {
        firstDayOfDuration = getDayOfYearFromCalendarDayList(calendarDayList.get(0));
        lastDayOfDuration = getDayOfYearFromCalendarDayList(calendarDayList.get(calendarDayList.size()-1));
        int firstAggregatedDayOfYearToUse = dayOfYear + valueAddedForFutureYears;

        convertToStringAndSetFirstAndLastDurationDays(firstDayOfDuration, lastDayOfDuration);

        clearAllActivityAndFoodIntegerDayLists();

        setFullListOfActivityDaysFromDateSelection(firstAggregatedDayOfYearToUse, calendarDayList.size());
        setFullListOfFoodDaysFromDateSelection(firstAggregatedDayOfYearToUse, calendarDayList.size());

        for (int i=0; i<calendarDayList.size(); i++) {
            int dayFromList = getDayOfYearFromCalendarDayList(calendarDayList.get(i));
            setPopulatedAndEmptyListsOfActivityDays(dayFromList);
            setPopulatedAndEmptyListsOfFoodDays(dayFromList);
        }

        setActivityListsForDatabaseObjectsForStatsFragment(mListOfActivityDaysWithPopulatedRows);
        setFoodListsForDatabaseObjects(mListOfFoodDaysWithPopulatedRows);

        numberOfDaysSelected = calendarDayList.size();

    }

    public int getNumberOfDaysSelected() {
        return numberOfDaysSelected;
    }

    public void setDaySelectedFromCalendar(int daySelected) {
        this.mDaySelectedFromCalendar = daySelected;
    }

    private void setFullListOfActivityDaysFromDateSelection(int firstDay, int sizeOfList) {
        for (int i=0; i<sizeOfList; i++) {
            mListOfActivityDaysSelected.add(firstDay + i);
        }
        Log.i("testInsert", "list of days selected is " + mListOfActivityDaysSelected);
    }

    private void setFullListOfFoodDaysFromDateSelection(int firstDay, int sizeOfList) {
        for (int i=0; i<sizeOfList; i++) {
            mListOfFoodDaysSelected.add(firstDay + i);
        }
    }

    private void clearFullListOfActivityDays() {
        mListOfActivityDaysSelected.clear();
    }

    private void clearFullListOfFoodFays() {
        mListOfFoodDaysSelected.clear();
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

    public void setValueAddedToSelectedDaysForFutureYears(int valueToAdd) {
//        Log.i("testInsert", "value added is " + valueToAdd);
        this.valueAddedForFutureYears = valueToAdd;
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

    public void deleteMultipleFoodEntries(List<Long> listOfEntries) {
        cyclesDatabase.cyclesDao().deleteCaloriesForEachFoodForMultipleDays(listOfEntries);
    }

    public void deleteAllFoodEntries() {
        cyclesDatabase.cyclesDao().deleteAllCaloriesForEachFoodEntries();
    }

    public List<Integer> getListOfActivityDaysSelected() {
        return mListOfActivityDaysSelected;
    }

    public boolean doesActivityExistsForSpecificDay() {
        for (int i=0; i<mStatsForEachActivityList.size(); i++) {
            if (mActivityString.equalsIgnoreCase(mStatsForEachActivityList.get(i).getActivity())) {
                return true;
            }
        }
        return false;
    }

    public boolean doesActivityExistInDatabaseForSelectedDay(int idToIterate) {
        for (int i=0; i<mStatsForEachActivityList.size(); i++) {
            if (mStatsForEachActivityList.get(i).getUniqueIdTiedToTheSelectedActivity()==idToIterate) {
                if (mActivityString.equalsIgnoreCase(mStatsForEachActivityList.get(i).getActivity())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void insertStatsForEachActivityRow(long daySelected, long setTime, double caloriesBurned) {
        mStatsForEachActivity = new StatsForEachActivity();

        mStatsForEachActivity.setUniqueIdTiedToTheSelectedActivity(daySelected);
        mStatsForEachActivity.setActivity(mActivityString);
        mStatsForEachActivity.setMetScore(mMetScore);
        mStatsForEachActivity.setTotalSetTimeForEachActivity(setTime);
        mStatsForEachActivity.setTotalCaloriesBurnedForEachActivity(caloriesBurned);

        mStatsForEachActivity.setCaloriesPerHour(mCaloriesBurnedPerHour);
        mStatsForEachActivity.setIsCustomActivity(mIsActivityCustom);

        cyclesDatabase.cyclesDao().insertStatsForEachActivity(mStatsForEachActivity);

        Log.i("testInsert", "day id inserted is " + daySelected);
    }

    //Used by Main.
    public void updateTotalTimesAndCaloriesForEachActivityForSelectedDay(long setTime, double caloriesBurned) {
        mStatsForEachActivity.setTotalSetTimeForEachActivity(setTime);
        mStatsForEachActivity.setTotalCaloriesBurnedForEachActivity(caloriesBurned);

        cyclesDatabase.cyclesDao().updateStatsForEachActivity(mStatsForEachActivity);
    }

    public void updateTotalTimesAndCaloriesForEachActivityFromDayId(long day, long setTime, double caloriesBurned) {
        for (int i=0; i<mStatsForEachActivityList.size(); i++) {
            if (mStatsForEachActivityList.get(i).getUniqueIdTiedToTheSelectedActivity()==day && mStatsForEachActivityList.get(i).getActivity().equalsIgnoreCase(mActivityString)) {
                mStatsForEachActivity = mStatsForEachActivityList.get(i);

                mStatsForEachActivity.setUniqueIdTiedToTheSelectedActivity(day);
                mStatsForEachActivity.setTotalSetTimeForEachActivity(setTime);
                mStatsForEachActivity.setTotalCaloriesBurnedForEachActivity(caloriesBurned);

                cyclesDatabase.cyclesDao().updateStatsForEachActivity(mStatsForEachActivity);
            }
        }
    }

    public void deleteTotalTimesAndCaloriesForSelectedActivityForSelectedDays(int position) {
        String activityToDelete = totalActivitiesListForSelectedDuration.get(position);

        for (int i=0; i<mStatsForEachActivityList.size(); i++) {
            if (mStatsForEachActivityList.get(i).getActivity().equalsIgnoreCase(activityToDelete)) {
                mStatsForEachActivity = mStatsForEachActivityList.get(i);
                cyclesDatabase.cyclesDao().deleteStatsForEachActivity(mStatsForEachActivity);
            }
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

    public void setIsActivityCustomBoolean(boolean isCustom) {
        this.mIsActivityCustom = isCustom;
    }

    public void setDoesDayExistInDatabaseBoolean(int daySelected) {
        List<DayHolder> dayHolderList = cyclesDatabase.cyclesDao().loadSingleDay(daySelected);
        if (dayHolderList.size()==0) {
            doesDayExistInDatabase = false;
        } else {
            doesDayExistInDatabase = true;
        }
        Log.i("testDayInsert", "doesDayExist boolean is " + doesDayExistInDatabase);
    }

    public void insertTotalTimesAndCaloriesBurnedForSpecificDayWithZeroedOutTimesAndCalories(int daySelected) {
        String date = getDateString();

        mDayHolder = new DayHolder();

        mDayHolder.setDayId(daySelected);
        mDayHolder.setDate(date);

        mDayHolder.setTotalSetTime(0);
        mDayHolder.setTotalBreakTime(0);
        mDayHolder.setTotalCaloriesBurned(0);

        cyclesDatabase.cyclesDao().insertDay(mDayHolder);
    }

    public void assignDayHolderInstanceForSelectedDay(int selectedDay) {
        mDayHolderList = cyclesDatabase.cyclesDao().loadSingleDay(selectedDay);
        mDayHolder = mDayHolderList.get(0);
    }

    public void insertTotalTimesAndCaloriesBurnedForSelectedDays(long setTime, double caloriesBurned) {
        List<Integer> listToPullDaysFrom = new ArrayList<>();

        listToPullDaysFrom = new ArrayList<>(mListOfActivityDaysSelected);

        for (int i = 0; i < listToPullDaysFrom.size(); i++) {
            mDayHolder = new DayHolder();
            int daySelected = listToPullDaysFrom.get(i);

            mDayHolder.setDayId(daySelected);
            mDayHolder.setTotalSetTime(setTime);
            mDayHolder.setTotalCaloriesBurned(caloriesBurned);

            cyclesDatabase.cyclesDao().insertDay(mDayHolder);
        }
    }

    //Used by Main.
    public void updateTotalTimesAndCaloriesForSelectedDay(long setTime, double caloriesBurned) {
        mDayHolder.setTotalSetTime(setTime);
        mDayHolder.setTotalCaloriesBurned(caloriesBurned);

        Log.i("testUpdate", "dayHolder Id updating is " + mDayHolder.getDayId());
        Log.i("testUpdate", "updated dayHolder set time in minutes are " + setTime/1000/60);

        cyclesDatabase.cyclesDao().updateDayHolder(mDayHolder);
    }

    //Used by Stats Fragment.
    public void updateTotalTimesAndCaloriesForMultipleDays(long setTime, double caloriesBurned) {
        for (int i=0; i<mDayHolderList.size(); i++) {
            mDayHolder = mDayHolderList.get(i);

            mDayHolder.setTotalSetTime(setTime);
            mDayHolder.setTotalCaloriesBurned(caloriesBurned);

            cyclesDatabase.cyclesDao().updateDayHolder(mDayHolder);
        }
    }


    public void setStatForEachActivityListForForSingleDayFromDatabase(int dayToRetrieve) {
        List<Integer> singleDayList = Collections.singletonList(dayToRetrieve);
        mStatsForEachActivityList = cyclesDatabase.cyclesDao().loadActivitiesForMultipleDays(singleDayList);
    }

    //Used by Main only.
    public void insertTotalTimesAndCaloriesForEachActivityWithinASpecificDayWithZeroedOutTimesAndCalories(int selectedDay) {
        mStatsForEachActivity = new StatsForEachActivity();

        mStatsForEachActivity.setUniqueIdTiedToTheSelectedActivity(selectedDay);
        mStatsForEachActivity.setActivity(mActivityString);
        mStatsForEachActivity.setMetScore(mMetScore);

        mStatsForEachActivity.setIsCustomActivity(mIsActivityCustom);
        mStatsForEachActivity.setCaloriesPerHour(mCaloriesBurnedPerHour);

        mStatsForEachActivity.setTotalSetTimeForEachActivity(0);
        mStatsForEachActivity.setTotalBreakTimeForEachActivity(0);
        mStatsForEachActivity.setTotalCaloriesBurnedForEachActivity(0);

        cyclesDatabase.cyclesDao().insertStatsForEachActivity(mStatsForEachActivity);
    }

    public void loadAllActivitiesToStatsListForSpecificDay(int daySelected) {
        mStatsForEachActivityList = cyclesDatabase.cyclesDao().loadActivitiesForSpecificDate(daySelected);
    }

    public void assignPositionOfRecentlyAddedRowToStatsEntity() {
        mStatsForEachActivity = mStatsForEachActivityList.get(mStatsForEachActivityList.size()-1);
    }

    public void setActivityPositionInListForCurrentDayForNewActivity() {
        activityPositionInListForCurrentDay = mStatsForEachActivityList.size()-1;
    }

    public void setActivityPositionInListForCurrentDayForExistingActivity() {
        for (int i=0; i<mStatsForEachActivityList.size(); i++) {
            if (mActivityString.equals(mStatsForEachActivityList.get(i).getActivity())) {
                activityPositionInListForCurrentDay = i;
                return;
            }
        }
    }

    public void assignPositionOfActivityListForRetrieveActivityToStatsEntity() {
        mStatsForEachActivity = mStatsForEachActivityList.get(activityPositionInListForCurrentDay);
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

    public String getActivityStringVariable() {
        return mActivityString;
    }

    public String getActivityStringFromSelectedActivity() {
        return mStatsForEachActivity.getActivity();
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

    public long getTotalActivityTimeForAllActivitiesOnASelectedDay(int day) {
        long valueToReturn = 0;

        for (int i=0; i<mStatsForEachActivityList.size(); i++) {
            if (mStatsForEachActivityList.get(i).getUniqueIdTiedToTheSelectedActivity()==day) {
                valueToReturn += mStatsForEachActivityList.get(i).getTotalSetTimeForEachActivity();
            }
        }

        return valueToReturn;
    }

    public double getTotalCaloriesBurnedForAllActivitiesOnASingleDay(int day) {
        double valueToReturn = 0;

        for (int i=0; i<mStatsForEachActivityList.size(); i++) {
            if (mStatsForEachActivityList.get(i).getUniqueIdTiedToTheSelectedActivity()==day) {
                valueToReturn += mStatsForEachActivityList.get(i).getTotalCaloriesBurnedForEachActivity();
            }
        }

        return valueToReturn;
    }

    public void clearStatsForEachActivityArrayLists() {
        totalActivitiesListForSelectedDuration.clear();
        totalSetTimeListForEachActivityForSelectedDuration.clear();
        totalCaloriesBurnedListForEachActivityForSelectedDuration.clear();
    }

    public void setTotalActivityStatsForSelectedDaysToArrayLists() {
        for (int i=0; i<mStatsForEachActivityList.size(); i++) {
            if (mStatsForEachActivityList.get(i).getActivity()!=null) {
                if (!doesTotalActivitiesListContainSelectedString(mStatsForEachActivityList.get(i).getActivity())) {

                    totalActivitiesListForSelectedDuration.add(mStatsForEachActivityList.get(i).getActivity());
                    totalSetTimeListForEachActivityForSelectedDuration.add(mStatsForEachActivityList.get(i).getTotalSetTimeForEachActivity());

                    double caloriesToAdd = roundDownCalories(mStatsForEachActivityList.get(i).getTotalCaloriesBurnedForEachActivity());
                    totalCaloriesBurnedListForEachActivityForSelectedDuration.add(caloriesToAdd);
                } else {
                    totalSetTimeListForEachActivityForSelectedDuration.set(duplicateStringPosition, combinedSetTimeFromExistingAndRepeatingPositions(i));
                    totalCaloriesBurnedListForEachActivityForSelectedDuration.set(duplicateStringPosition, combinedActivityCaloriesFromExistingAndRepeatingPositions(i));
                }
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
        long iteratingValue = mStatsForEachActivityList.get(position).getTotalSetTimeForEachActivity();
        long presentValue =  totalSetTimeListForEachActivityForSelectedDuration.get(duplicateStringPosition);
        return iteratingValue + presentValue;
    }

    private double combinedActivityCaloriesFromExistingAndRepeatingPositions(int position) {
        double iteratingValue = roundDownCalories(mStatsForEachActivityList.get(position).getTotalCaloriesBurnedForEachActivity());
        double presentValue =  totalCaloriesBurnedListForEachActivityForSelectedDuration.get(duplicateStringPosition);
        return iteratingValue + presentValue;
    }

    public void setTotalSetTimeVariableForSelectedDuration() {
        long valueToReturn = 0;

        for (int i=0; i<totalSetTimeListForEachActivityForSelectedDuration.size(); i++) {
            valueToReturn += totalSetTimeListForEachActivityForSelectedDuration.get(i);
        }

        totalSetTimeForSelectedDuration = valueToReturn;
    }

    public long getTotalActivityTimeForSelectedDuration() {
        long valueToReturn = 0;

        for (int i=0; i<totalSetTimeListForEachActivityForSelectedDuration.size(); i++) {
            valueToReturn += totalSetTimeListForEachActivityForSelectedDuration.get(i);
        }

        return valueToReturn;
    }

    public void setTotalCaloriesVariableForSelectedDuration() {
        double valueToReturn = 0;

        for (int i=0; i<totalCaloriesBurnedListForEachActivityForSelectedDuration.size(); i++) {
            valueToReturn += totalCaloriesBurnedListForEachActivityForSelectedDuration.get(i);
        }

        totalActivityCaloriesForSelectedDuration = valueToReturn;
    }

    public double getTotalCaloriesBurnedForSelectedDuration() {
        double valueToReturn = 0;

        for (int i=0; i<totalCaloriesBurnedListForEachActivityForSelectedDuration.size(); i++) {
            valueToReturn += totalCaloriesBurnedListForEachActivityForSelectedDuration.get(i);
        }

        return valueToReturn;
    }

    private double roundDownCalories(double calories) {
        return Math.floor(calories);
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

    public long getUnassignedSetTimeForSelectedDuration() {
        return totalUnassignedSetTimeForSelectedDuration;
    }

    public void setUnassignedTotalCalories() {
        totalUnassignedCaloriesForSelectedDuration = bmrCaloriesBurned() * decimalPercentageOfUnAssignedTime();
    }

    public double getUnassignedCaloriesForSelectedDuration() {
        return totalUnassignedCaloriesForSelectedDuration;
    }

    public long getTwentyFourHoursInMillis() {
        long twoHours = 7200000;
        return twoHours * 12;
    }

    public void setAggregateTimeForSelectedDuration() {
        totalAggregateTimeForSelectedDuration = getTwentyFourHoursInMillis() * numberOfDaysSelected;
    }

    public long getAggregateTimeForSelectedDuration() {
        return totalAggregateTimeForSelectedDuration;
    }

    public void setAggregateCaloriesForSelectedDuration() {
        totalAggregateCaloriesForSelectedDuration = totalActivityCaloriesForSelectedDuration + getUnassignedCaloriesForSelectedDuration();
    }

    public double getAggregateCaloriesForSelectedDuration() {
        return totalAggregateCaloriesForSelectedDuration;
    }

    //Here we iterate through each DAY, in getAggregatedActivityTime...() we iterate through activity times to get an aggregate for each day here.
    public List<Long> getListOfUnassignedTimeForMultipleDays() {
        List<Long> listOfAssignedTimes = getListOfAssignedTimesForMultipleDays();
        List<Long> listToReturn = new ArrayList<>();

        for (int i=0; i<listOfAssignedTimes.size(); i++) {
            long unassignedTimeForDay = getTwentyFourHoursInMillis() - listOfAssignedTimes.get(i);
            listToReturn.add(unassignedTimeForDay);
        }

        return listToReturn;
    }

    public List<Long> getListOfAssignedTimesForMultipleDays() {
        List<Long> listToReturn = new ArrayList<>();

        for (int i=0; i<mListOfActivityDaysSelected.size(); i++) {
            int dayFromList = mListOfActivityDaysSelected.get(i);

            long assignedTimeForDay = getAggregatedActivityTimeForSelectedActivity(dayFromList);
            listToReturn.add(assignedTimeForDay);
        }

        return listToReturn;
    }

    public long getAggregatedActivityTimeForSelectedActivity(int daySelected) {
        List<StatsForEachActivity> statsForEachActivityList = getStatsForEachActivityListForSelectedDay(daySelected);
        long valueToReturn = 0;

        for (int i=0; i<statsForEachActivityList.size(); i++) {
            valueToReturn += statsForEachActivityList.get(i).getTotalSetTimeForEachActivity();

            if (valueToReturn > getTwentyFourHoursInMillis()) {
                valueToReturn = getTwentyFourHoursInMillis();
            }
        }

        return valueToReturn;
    }

    public int bmrCaloriesBurned() {
        int savedBmr = sharedPreferences.getInt("savedBmr", 0);
        return savedBmr * numberOfDaysSelected;
    }

    private double decimalPercentageOfUnAssignedTime() {
        double remainingTime = (double) totalSetTimeForSelectedDuration / totalAggregateTimeForSelectedDuration;
        return 1 - remainingTime;
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

    public List<Integer> getListOfFoodDaysSelected() {
        return mListOfFoodDaysSelected;
    }

    public boolean doesFoodExistsInDatabaseForSelectedDayBoolean(String foodString) {
        for (int i=0; i<mCaloriesForEachFoodList.size(); i++) {
            if (mFoodString.equalsIgnoreCase(mCaloriesForEachFoodList.get(i).getTypeOfFood())) {
                return true;
            }
        }
        return false;
    }

    public boolean doesFoodExistInDatabaseForMultipleDaysBoolean(int idToIterate) {
        for (int i=0; i<mCaloriesForEachFoodList.size(); i++) {
            if (mCaloriesForEachFoodList.get(i).getUniqueIdTiedToEachFood()==idToIterate) {
                if (mFoodString.equalsIgnoreCase(mCaloriesForEachFoodList.get(i).getTypeOfFood())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void insertCaloriesAndEachFoodForSingleDay(long daySelected) {
        mCaloriesForEachFood = new CaloriesForEachFood();
        mCaloriesForEachFood.setUniqueIdTiedToEachFood(daySelected);
        mCaloriesForEachFood.setTypeOfFood(mFoodString);
        mCaloriesForEachFood.setCaloriesConsumedForEachFoodType(mCaloriesInFoodItem);

        cyclesDatabase.cyclesDao().insertCaloriesForEachFoodRow(mCaloriesForEachFood);
    }

    public void updateCaloriesAndEachFoodInDatabaseFromDayId(long day) {
        for (int i=0; i<mCaloriesForEachFoodList.size(); i++) {
            if (mCaloriesForEachFoodList.get(i).getUniqueIdTiedToEachFood()==day && mCaloriesForEachFoodList.get(i).getTypeOfFood().equalsIgnoreCase(mFoodString)) {
                mCaloriesForEachFood = mCaloriesForEachFoodList.get(i);

                mCaloriesForEachFood.setUniqueIdTiedToEachFood(day);
                mCaloriesForEachFood.setTypeOfFood(mFoodString);
                mCaloriesForEachFood.setCaloriesConsumedForEachFoodType(mCaloriesInFoodItem);

                cyclesDatabase.cyclesDao().updateCaloriesForEachFoodRow(mCaloriesForEachFood);
                return;
            }
        }
    }

    public void updateCaloriesAndEachFoodInDatabaseFromPosition(int position) {
        String foodToUpdate = totalFoodStringListForSelectedDuration.get(position);

        for (int i=0; i<mCaloriesForEachFoodList.size(); i++) {
            if (mCaloriesForEachFoodList.get(i).getTypeOfFood().equalsIgnoreCase(foodToUpdate)) {
                mCaloriesForEachFood = mCaloriesForEachFoodList.get(i);

                mCaloriesForEachFood.setTypeOfFood(mFoodString);
                mCaloriesForEachFood.setCaloriesConsumedForEachFoodType(mCaloriesInFoodItem);

                cyclesDatabase.cyclesDao().updateCaloriesForEachFoodRow(mCaloriesForEachFood);
            }
        }
    }

    public void deleteCaloriesAndEachFoodInDatabase(int position) {
        String foodToDelete = totalFoodStringListForSelectedDuration.get(position);

        for (int i=0; i<mCaloriesForEachFoodList.size(); i++) {
            if (mCaloriesForEachFoodList.get(i).getTypeOfFood().equals(foodToDelete)) {
                mCaloriesForEachFood = mCaloriesForEachFoodList.get(i);
                cyclesDatabase.cyclesDao().deleteCaloriesForEachFoodRow(mCaloriesForEachFood);
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

    public List<String> getTotalFoodStringListForSelectedDuration() {
        return totalFoodStringListForSelectedDuration;
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

    public void setTotalFoodConsumedForSelectedDaysToArrayLists() {
        clearFoodConsumedArrayLists();

        for (int i=0; i<mCaloriesForEachFoodList.size(); i++) {
            if (!doesTotalFoodConsumedListContainSelectedString(mCaloriesForEachFoodList.get(i).getTypeOfFood())) {

                totalFoodStringListForSelectedDuration.add(mCaloriesForEachFoodList.get(i).getTypeOfFood());

                double caloriesToAdd = roundDownCalories(mCaloriesForEachFoodList.get(i).getCaloriesConsumedForEachFoodType());
                totalCaloriesConsumedListForSelectedDuration.add(caloriesToAdd);
            } else {
                totalCaloriesConsumedListForSelectedDuration.set(duplicateStringPosition, combinedFoodConsumedCaloriesFromExistingAndRepeatingPositions(i));
            }
        }
    }

    public void clearFoodConsumedArrayLists() {
        totalFoodStringListForSelectedDuration.clear();
        totalCaloriesConsumedListForSelectedDuration.clear();
    }

    public boolean doesTotalFoodConsumedListContainSelectedString(String stringToCheck) {
        for (int i=0; i<totalFoodStringListForSelectedDuration.size(); i++) {
            if (totalFoodStringListForSelectedDuration.get(i).equalsIgnoreCase(stringToCheck)) {
                duplicateStringPosition = i;
                return true;
            }
        }
        return false;
    }

    private double combinedFoodConsumedCaloriesFromExistingAndRepeatingPositions(int position) {
        double iteratingValue = roundDownCalories(mCaloriesForEachFoodList.get(position).getCaloriesConsumedForEachFoodType());
        double presetValue = totalCaloriesConsumedListForSelectedDuration.get(duplicateStringPosition);
        return iteratingValue + presetValue;
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

        mCaloriesForEachFood = new CaloriesForEachFood();
        mCaloriesForEachFoodList = new ArrayList<>();
    }

    private void instantiateArrayLists() {
        totalActivitiesListForSelectedDuration = new ArrayList<>();
        totalSetTimeListForEachActivityForSelectedDuration = new ArrayList<>();
        totalCaloriesBurnedListForEachActivityForSelectedDuration = new ArrayList<>();

        totalFoodStringListForSelectedDuration = new ArrayList<>();
        totalCaloriesConsumedListForSelectedDuration = new ArrayList<>();

        mListOfActivityDaysSelected = new ArrayList<>();
        mListOfActivityDaysWithPopulatedRows = new ArrayList<>();
        mListOfActivityDaysWithEmptyRows = new ArrayList<>();

        mListOfFoodDaysSelected = new ArrayList<>();
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

    private void logIntegerAndEntityDayLists() {
        Log.i("testList", "total activity list is " + mListOfActivityDaysSelected);
        Log.i("testList", "populated activity list is " + mListOfActivityDaysWithPopulatedRows);
        Log.i("testList", "unpopulated activity list is " + mListOfActivityDaysWithEmptyRows);

        Log.i("testList", "total food list is " + mListOfFoodDaysSelected);
        Log.i("testList", "populated food list is " + mListOfFoodDaysWithPopulatedRows);
        Log.i("testList", "unpopulated food list is " + mListOfFoodDaysWithEmptyRows);
    }


    private void logSetTimeValues() {
        Log.i("testCals", "total assigned set is " + totalSetTimeForSelectedDuration);
        Log.i("testCals", "total aggregate set is " + totalAggregateTimeForSelectedDuration);
    }

    private void logActivityCalorieValues() {
        Log.i("testCals", "total assigned calories are " + totalActivityCaloriesForSelectedDuration);
        Log.i("testCals", "total unassigned calories are " + totalUnassignedCaloriesForSelectedDuration);
        Log.i("testCals", "total aggregate calories are " + totalAggregateCaloriesForSelectedDuration);
        Log.i("testCals", "decimal pct is " + decimalPercentageOfUnAssignedTime());
    }
}