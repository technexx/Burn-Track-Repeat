package com.example.tragic.irate.simple.stopwatch.Database.DailyStatClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.tragic.irate.simple.stopwatch.Database.CyclesDatabase;
import com.example.tragic.irate.simple.stopwatch.Database.DailyCalorieClasses.CaloriesForEachFood;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Stack;

public class DailyStatsAccess {
    Context mContext;
    CyclesDatabase cyclesDatabase;

    StatsForEachActivity mStatsForEachActivity;
    CaloriesForEachFood mCaloriesForEachFood;

    List<StatsForEachActivity> mStatsForEachActivityList;
    List<CaloriesForEachFood> mCaloriesForEachFoodList;

    List<String> totalActivitiesListForSelectedDuration;
    List<Long> totalSetTimeListForEachActivityForSelectedDuration;
    List<Double> totalCaloriesBurnedListForEachActivityForSelectedDuration;

    List<String> totalFoodStringListForSelectedDuration;
    List<Double> totalCaloriesConsumedListForSelectedDuration;

    long totalSetTimeForSelectedDuration;
    double totalActivityCaloriesForSelectedDuration;
    long totalUnassignedSetTimeForSelectedDuration;
    double totalUnassignedCaloriesForSelectedDuration;
    long totalAggregateTimeForSelectedDuration;
    double totalAggregateCaloriesForSelectedDuration;

    int numberOfDaysSelected;
    int firstDayOfDuration;
    int lastDayOfDuration;
    int valueAddedForFutureYears;

    int mYearSelectedForDurationStartDate;
    int mYearSelectedForDurationEndDate;
    Calendar mCalendarObjectSelectedFromFragment;

    String mFirstDayInDurationAsString;
    String mLastDayInDurationAsString;

    long mOldDayHolderId;

    int activityPositionInListForCurrentDay;
    int duplicateStringPosition;

    String mActivityString = "";
    double mMetScore;
    int mActivitySortMode = 1;

    String mFoodString = "";
    Double mCaloriesInFoodItem;
    int mCaloriesConsumedSortMode = 1;

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

    public void setActivitySortMode(int sortMode) {
        this.mActivitySortMode = sortMode;
    }

    public void setFoodConsumedSortMode(int sortMode) {
        this.mCaloriesConsumedSortMode = sortMode;
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

        switch (mCaloriesConsumedSortMode) {
            case 1:
                listToReturn = cyclesDatabase.cyclesDao().loadCaloriesForEachFoodByAToZName(listOfDays);
                break;
            case 2:
                listToReturn = cyclesDatabase.cyclesDao().loadCaloriesForEachFoodByZToAName(listOfDays);
                break;
            case 3:
                listToReturn = cyclesDatabase.cyclesDao().loadCaloriesForEachFoodByMostCaloriesBurned(listOfDays);
                break;
            case 4:
                listToReturn = cyclesDatabase.cyclesDao().loadCaloriesForEachFoodByLeastCaloriesBurned(listOfDays);
                break;

        }

        return listToReturn;
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
            mStatsForEachActivityList = assignStatsForEachActivityListBySortMode(integerListOfDaysSelected);
        } else {
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

        //For all non-custom durations, both start and end years will be the same.
        setYearSelectedForDurationStartDate(mCalendarObjectSelectedFromFragment.get(Calendar.YEAR));
        setYearSelectedForDurationEndDate(mCalendarObjectSelectedFromFragment.get(Calendar.YEAR));

        firstDayOfDuration = daySelected;
        lastDayOfDuration = daySelected;
        convertToStringAndSetFirstAndLastDurationDays(firstDayOfDuration, lastDayOfDuration);

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

        setYearSelectedForDurationStartDate(mCalendarObjectSelectedFromFragment.get(Calendar.YEAR));
        setYearSelectedForDurationEndDate(mCalendarObjectSelectedFromFragment.get(Calendar.YEAR));

        int firstAggregatedDayOfYearToUse = firstDayOfDuration + valueAddedForFutureYears;
        convertToStringAndSetFirstAndLastDurationDays(firstDayOfDuration, lastDayOfDuration);

        Log.i("testDate", "mYear var in duration retrieval method is is " + mYearSelectedForDurationStartDate);

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

        setYearSelectedForDurationStartDate(mCalendarObjectSelectedFromFragment.get(Calendar.YEAR));
        setYearSelectedForDurationEndDate(mCalendarObjectSelectedFromFragment.get(Calendar.YEAR));

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

    public void setAllDayAndStatListsForCustomDatesFromDatabase(List<CalendarDay> calendarDayList, int dayOfYear) {
        firstDayOfDuration = getDayOfYearFromCalendarDayList(calendarDayList.get(0));
        lastDayOfDuration = getDayOfYearFromCalendarDayList(calendarDayList.get(calendarDayList.size()-1));

        int firstAggregatedDayOfYearToUse = dayOfYear + valueAddedForFutureYears;

        setYearSelectedForDurationStartDate(calendarDayList.get(0).getYear());
        setYearSelectedForDurationEndDate(calendarDayList.get(calendarDayList.size()-1).getYear());

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

        setFullListOfActivityDaysFromDateSelection(firstAggregatedDayOfYearToUse, lastDayOfDuration);
        setFullListOfFoodDaysFromDateSelection(firstAggregatedDayOfYearToUse, lastDayOfDuration);

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

    public int getDayOfYearFromCalendarDayList(CalendarDay calendarDay){
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(calendarDay.getYear(), calendarDay.getMonth()-1, calendarDay.getDay());
        return calendar.get(Calendar.DAY_OF_YEAR);
    }

    private void convertToStringAndSetFirstAndLastDurationDays(int firstDay, int lastDay) {
        String firstDayString = firstDayInDurationDateString(firstDay);
        String lastDayString = lastDayInDurationDateString(lastDay);

        setFirstDayInDurationAsString(firstDayString);
        setLastDayInDurationAsString(lastDayString);
    }

    private String firstDayInDurationDateString(int day) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, day);
        calendar.set(Calendar.YEAR, mYearSelectedForDurationStartDate);

        Date date = calendar.getTime();

        return simpleDateFormat.format(date);
    }

    private String lastDayInDurationDateString(int day) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, day);
        calendar.set(Calendar.YEAR, mYearSelectedForDurationEndDate);

        Date date = calendar.getTime();

        return simpleDateFormat.format(date);
    }

    public void setCalendarObjectSelectedFromFragment(Calendar calendarObject) {
        this.mCalendarObjectSelectedFromFragment = calendarObject;
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

    public void setYearSelectedForDurationStartDate(int yearSelected) {
        this.mYearSelectedForDurationStartDate = yearSelected;
    }

    private int getYearSelectedForDurationStartDate() {
        return mYearSelectedForDurationStartDate;
    }

    public void setYearSelectedForDurationEndDate(int yearSelected) {
        this.mYearSelectedForDurationEndDate = yearSelected;
    }

    private int getYearSelectedForDurationEndDate() {
        return mYearSelectedForDurationEndDate;
    }

    public void setValueAddedToSelectedDaysForFutureYears(int valueToAdd) {
        this.valueAddedForFutureYears = valueToAdd;
    }

    public int getNumberOfDaysSelected() {
        return numberOfDaysSelected;
    }

    private void setFullListOfActivityDaysFromDateSelection(int firstDay, int sizeOfList) {
        for (int i=0; i<sizeOfList; i++) {
            mListOfActivityDaysSelected.add(firstDay + i);
        }
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

    private boolean checkIfTimeWillCapTotalForDay(long timeToAddOrReplace, long currentTotal) {
        return ((timeToAddOrReplace + currentTotal) >= (getTwentyFourHoursInMillis() -999));
    }

    public void deleteTotalTimesAndCaloriesForSelectedActivityForSelectedDays(int position) {
        String activityToDelete = totalActivitiesListForSelectedDuration.get(position);

        for (int i=0; i<mStatsForEachActivityList.size(); i++) {
//            Log.i("testDelete", "times in list during deletion are " + mStatsForEachActivityList.get(i).getTotalSetTimeForEachActivity());
//            Log.i("testDelete", "calories in list during deletion are " + mStatsForEachActivityList.get(i).getTotalCaloriesBurnedForEachActivity());

            if (mStatsForEachActivityList.get(i).getActivity().equalsIgnoreCase(activityToDelete)) {
                mStatsForEachActivity = mStatsForEachActivityList.get(i);
                cyclesDatabase.cyclesDao().deleteStatsForEachActivity(mStatsForEachActivity);
            }
        }
    }

    public void deleteTotalTimesAndCaloriesFromCurrentEntity() {
        cyclesDatabase.cyclesDao().deleteStatsForEachActivity(mStatsForEachActivity);
    }

    public void setStatsForEachActivityEntityFromPosition(int position) {
        mStatsForEachActivity = mStatsForEachActivityList.get(position);
    }

    public void setMetScoreFromSpinner(double metScore) {
        this.mMetScore = metScore;
    }

    public void setMetScoreFromDatabaseList(int position) {
        mMetScore = mStatsForEachActivityList.get(position).getMetScore();

        String activity = mStatsForEachActivityList.get(position).getActivity();
        Log.i("testEdit", "mStats activity in position is " + activity);
        Log.i("testEdit", "mStats MET score is " + mMetScore);

    }

    public void setIsActivityCustomBoolean(boolean isCustom) {
        this.mIsActivityCustom = isCustom;
    }

    public void setStatForEachActivityListForForSingleDayFromDatabase(int dayToRetrieve) {
        List<Integer> singleDayList = Collections.singletonList(dayToRetrieve);
        mStatsForEachActivityList = cyclesDatabase.cyclesDao().loadActivitiesForMultipleDays(singleDayList);
        for (int i = 0; i<mStatsForEachActivityList.size(); i++) {
            Log.i("testUpdate", "mStats list times retrieved are " + mStatsForEachActivityList.get(i).getTotalSetTimeForEachActivity());
        }
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
        if (mStatsForEachActivityList.size() > 0) {
            mStatsForEachActivity = mStatsForEachActivityList.get(mStatsForEachActivityList.size()-1);
        }
    }

    public void setActivityPositionInListForCurrentDayForNewActivity() {
        if (mStatsForEachActivityList.size() > 0) {
            activityPositionInListForCurrentDay = mStatsForEachActivityList.size()-1;
        }
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

    public void setActivityString(String activityString) {
        this.mActivityString = activityString;
    }

    public String getActivityStringVariable() {
        return mActivityString;
    }

    public long getTotalSetTimeForSelectedActivity() {
        return mStatsForEachActivity.getTotalSetTimeForEachActivity();
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
//                Log.i("testUpdate", "mStats list times are " + mStatsForEachActivityList.get(i).getTotalSetTimeForEachActivity());
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

                    long timeToAdd = mStatsForEachActivityList.get(i).getTotalSetTimeForEachActivity();

                    totalSetTimeListForEachActivityForSelectedDuration.add(timeToAdd);

                    double caloriesToAdd = mStatsForEachActivityList.get(i).getTotalCaloriesBurnedForEachActivity();
                    totalCaloriesBurnedListForEachActivityForSelectedDuration.add(caloriesToAdd);

                } else {
                    totalSetTimeListForEachActivityForSelectedDuration.set(duplicateStringPosition, combinedSetTimeFromExistingAndRepeatingPositions(i));
                    totalCaloriesBurnedListForEachActivityForSelectedDuration.set(duplicateStringPosition, combinedActivityCaloriesFromExistingAndRepeatingPositions(i));
                }
            }
        }
    }


    private long roundUpMillisValues(long millisToRound) {
        long remainder = millisToRound%1000;

        if (remainder != 0) {
            return millisToRound += (1000-remainder);
        } else {
            return millisToRound;
        }
    }

    private long roundDownMillisValues(long millisToRound) {
        return millisToRound -= (millisToRound%1000);
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
        iteratingValue = roundDownMillisValues(iteratingValue);
        long presentValue =  totalSetTimeListForEachActivityForSelectedDuration.get(duplicateStringPosition);
        return iteratingValue + presentValue;
    }

    private double combinedActivityCaloriesFromExistingAndRepeatingPositions(int position) {
        double iteratingValue = mStatsForEachActivityList.get(position).getTotalCaloriesBurnedForEachActivity();
        double presentValue =  totalCaloriesBurnedListForEachActivityForSelectedDuration.get(duplicateStringPosition);
        return iteratingValue + presentValue;
    }

    public void setTotalSetTimeVariableForSelectedDuration() {
        long valueToReturn = 0;

        for (int i=0; i<totalSetTimeListForEachActivityForSelectedDuration.size(); i++) {
            valueToReturn += totalSetTimeListForEachActivityForSelectedDuration.get(i);
            valueToReturn = roundDownMillisValues(valueToReturn);
        }

        totalSetTimeForSelectedDuration = valueToReturn;
    }

    public long getTotalActivityTimeForSelectedDuration() {
        long valueToReturn = 0;

        for (int i=0; i<totalSetTimeListForEachActivityForSelectedDuration.size(); i++) {
            valueToReturn += totalSetTimeListForEachActivityForSelectedDuration.get(i);
            valueToReturn = roundDownMillisValues(valueToReturn);
        }

        return valueToReturn;
    }

    public void setTotalCaloriesVariableForSelectedDuration() {
        double valueToReturn = 0;

        for (int i=0; i<totalCaloriesBurnedListForEachActivityForSelectedDuration.size(); i++) {
            valueToReturn += totalCaloriesBurnedListForEachActivityForSelectedDuration.get(i);
            valueToReturn = roundDownCalories(valueToReturn);
        }

        totalActivityCaloriesForSelectedDuration = valueToReturn;
    }

    public double getTotalCaloriesBurnedForSelectedDuration() {
        return totalActivityCaloriesForSelectedDuration;
    }

    private double roundDownCalories(double calories) {
        return Math.floor(calories);
    }

    public void setUnassignedDailyTotalTime() {
        totalUnassignedSetTimeForSelectedDuration = setZeroLowerBoundsOnLongValue(totalAggregateTimeForSelectedDuration - totalSetTimeForSelectedDuration);

        if (totalUnassignedSetTimeForSelectedDuration <= (getTwentyFourHoursInMillis() - 999)) {
            totalUnassignedSetTimeForSelectedDuration = roundUpMillisValues(totalUnassignedSetTimeForSelectedDuration);
        }
    }

    public long getUnassignedSetTimeForSelectedDuration() {
        return totalUnassignedSetTimeForSelectedDuration;
    }

    public void setUnassignedTotalCalories() {
        totalUnassignedCaloriesForSelectedDuration = bmrCaloriesBurned() * decimalPercentageOfUnAssignedTime();
    }

    public int bmrCaloriesBurned() {
        int savedBmr = sharedPreferences.getInt("savedBmr", 2772);
        return savedBmr * numberOfDaysSelected;
    }

    private double decimalPercentageOfUnAssignedTime() { ;
        double remainingTime = (double) totalSetTimeForSelectedDuration / totalAggregateTimeForSelectedDuration;
        return 1 - remainingTime;
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
        mStatsForEachActivity = new StatsForEachActivity();
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
}