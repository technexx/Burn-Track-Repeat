package com.example.tragic.irate.simple.stopwatch;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.example.tragic.irate.simple.stopwatch.Database.CyclesDatabase;
import com.example.tragic.irate.simple.stopwatch.Database.DayStatClasses.DayHolder;
import com.example.tragic.irate.simple.stopwatch.Database.DayStatClasses.StatsForEachActivity;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class DailyStatsAccess {
    Context mContext;
    CyclesDatabase cyclesDatabase;
    CalendarValues calendarValues = new CalendarValues();

    DayHolder mDayHolder;
    StatsForEachActivity mStatsForEachActivity;
    List<DayHolder> mDayHolderList;
    List<StatsForEachActivity> statsForEachActivityListForTimerAccess;
    List<StatsForEachActivity> statsForEachActivityListForFragmentAccess;
    int oldStatsForEachActivityListSize;
    int newStatsForEachActivityListSize;

    List<String> totalActivitiesListForSelectedDuration;
    List<Long> totalSetTimeListForEachActivityForSelectedDuration;
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

    List<Integer> daysInSelectedDurationList;
    List<Integer> oldDaysInSelectedDurationList;

    int WEEKLY_DURATION = 0;
    int MONTHLY_DURATION = 1;
    int YEARLY_DURATION = 2;

    public DailyStatsAccess(Context context) {
        this.mContext = context;
        instantiateDailyStatsDatabase();
        instantiateEntitiesAndTheirLists();
        instantiateArrayListsOfTotalStatsForSelectedDurations();
        daysInSelectedDurationList = new ArrayList<>();
        oldDaysInSelectedDurationList = new ArrayList<>();
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

    public void setDayHolderAndStatForEachActivityListsForSelectedDayFromDatabase(int dayToRetrieve) {
        mDayHolderList = cyclesDatabase.cyclesDao().loadSingleDay(dayToRetrieve);
        setDaysInSelectedDurationList(dayToRetrieve);
        statsForEachActivityListForFragmentAccess = cyclesDatabase.cyclesDao().loadActivitiesForSpecificDate(dayToRetrieve);
    }

    //Todo: firstDay will b0rk for multiple years since our first day is always 0. Need to begin w/ aggregate ID for week/month/year. Daily uses aggregateID already.
    public void setAllDayAndStatListsForWeek(int dayOfWeek, int dayOfYear) {
        List<Integer> daysOfWeekList = new ArrayList<>();
        int firstDayOfYearToUse = dayOfYear - (dayOfWeek - 1);

        for (int i=0; i<7; i++) {
            setDaysInSelectedDurationList(firstDayOfYearToUse + i);
            if (cyclesDatabase.cyclesDao().loadSingleDay(firstDayOfYearToUse + i).size()!=0) {
                daysOfWeekList.add(firstDayOfYearToUse + i);
            }
        }

        populateDayHolderAndStatsForEachActivityLists(daysOfWeekList);
    }

    public void setAllDayAndStatListsForMonth(int dayOfMonth, int numberOfDaysInMonth, int dayOfYear) {
        List<Integer> daysOfMonthList = new ArrayList<>();

        int firstDayInYearToAdd = dayOfYear - (dayOfMonth-1);

        for (int i=0; i<numberOfDaysInMonth; i++) {
            setDaysInSelectedDurationList(firstDayInYearToAdd + i);
            if (cyclesDatabase.cyclesDao().loadSingleDay(firstDayInYearToAdd + i).size()!=0) {
                daysOfMonthList.add(firstDayInYearToAdd + i);
            }
        }

        populateDayHolderAndStatsForEachActivityLists(daysOfMonthList);
    }

    public void setAllDayAndStatListsForYearFromDatabase(int daysInYear, int aggregateDayID) {
        List<Integer> daysOfYearList = new ArrayList<>();
        int firstDayInYearToAdd =

        //If days exists in database, add it to list of days in year list.
        for (int i=0; i<daysInYear; i++) {
            setDaysInSelectedDurationList(i+1);
            if (cyclesDatabase.cyclesDao().loadSingleDay(i+1).size()!=0) {
                daysOfYearList.add(i+1);
            }
        }

        populateDayHolderAndStatsForEachActivityLists(daysOfYearList);
    }

    public void setAllDayAndStatListsForCustomDatesFromDatabase(List<CalendarDay> calendarDayList) {
        List<Integer> nonNullDayList = new ArrayList<>();

        for (int i=0; i<calendarDayList.size(); i++) {
            int dayOfYear = getDayOfYearFromCalendarDayList(calendarDayList.get(i).getYear(), calendarDayList.get(i).getMonth(), calendarDayList.get(i).getDay());
            setDaysInSelectedDurationList(dayOfYear);

            if (cyclesDatabase.cyclesDao().loadSingleDay(dayOfYear).size()!=0) {
                nonNullDayList.add(dayOfYear);
            }
        }

        populateDayHolderAndStatsForEachActivityLists(nonNullDayList);
    }

    private void populateCalendarDayListWithSelectedDurationDays(int lengthOfDuration) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        List<CalendarDay> calendarDayList = new ArrayList<>();

        calendar.set(selectedCalendarDay.getYear(), selectedCalendarDay.getMonth()-1, selectedCalendarDay.getDay());

        LocalDate localDate = selectedCalendarDay.getDate();
        int firstDayInYearToAdd = localDate.getDayOfYear() - (localDate.getDayOfMonth()-1);

        for (int i=0; i<lengthOfDuration; i++) {
            CalendarDay objectToAdd = CalendarDay.from(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
            calendarDayList.add(objectToAdd);
        }
    }



    private int getFirstDayOfYearToAddForSelectedDuration(CalendarDay selectedCalendarDay, int duration) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        List<CalendarDay> calendarDayList = new ArrayList<>();

        calendar.set(selectedCalendarDay.getYear(), selectedCalendarDay.getMonth()-1, selectedCalendarDay.getDay());

        LocalDate localDate = selectedCalendarDay.getDate();

        if (duration==WEEKLY_DURATION) {
            return localDate.getDayOfYear() - (localDate.getDayOfMonth()-1);
        }
        if (duration==MONTHLY_DURATION) {
            return localDate.getDayOfYear() - (localDate.getDayOfMonth()-1);
        }
        if (duration==YEARLY_DURATION) {

        }
    }

    public int getDayOfYearFromCalendarDayList(int year, int month, int day){
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(year, month-1, day);
        return calendar.get(Calendar.DAY_OF_YEAR);
    }

    private void populateDayHolderAndStatsForEachActivityLists(List<Integer> integerListOfSelectedDays) {
        if (integerListOfSelectedDays.size()>0) {
            mDayHolderList = cyclesDatabase.cyclesDao().loadMultipleDays(integerListOfSelectedDays);
            statsForEachActivityListForFragmentAccess = cyclesDatabase.cyclesDao().loadActivitiesForMultipleDays(integerListOfSelectedDays);
        } else {
            mDayHolderList = new ArrayList<>();
            statsForEachActivityListForFragmentAccess = new ArrayList<>();
        }
    }

    public List<Integer> getActivityListForCalendarColoring() {
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

    public void clearDaysInSelectedDurationList() {
        daysInSelectedDurationList.clear();
    }
    public void setOldDaysInSelectedDurationList(List<Integer> listToAdd) {
        oldDaysInSelectedDurationList = new ArrayList<>(listToAdd);
    }

    public List<Integer> getOldDaysInSelectedDurationList() {
        return oldDaysInSelectedDurationList;
    }

    public void setDaysInSelectedDurationList(int dayToAdd) {
        daysInSelectedDurationList.add(dayToAdd);
    }

    public List<Integer> getDaysInSelectedDurationList() {
        return daysInSelectedDurationList;
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
        clearStatsForEachActivityArrayLists();

        for (int i=0; i<statsForEachActivityListForFragmentAccess.size(); i++) {
            if (statsForEachActivityListForFragmentAccess.get(i).getActivity()!=null) {
                if (!doesTotalActivitiesListContainSelectedString(statsForEachActivityListForFragmentAccess.get(i).getActivity())) {
                    totalActivitiesListForSelectedDuration.add(statsForEachActivityListForFragmentAccess.get(i).getActivity());
                    totalSetTimeListForEachActivityForSelectedDuration.add(statsForEachActivityListForFragmentAccess.get(i).getTotalSetTimeForEachActivity());
                    totalCaloriesBurnedListForEachActivityForSelectedDuration.add(statsForEachActivityListForFragmentAccess.get(i).getTotalCaloriesBurnedForEachActivity());
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

    public int returnStatsForEachActivitySizeVariableByQueryingYearlyListOfActivities() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
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
        totalCaloriesBurnedListForEachActivityForSelectedDuration = new ArrayList<>();
    }
}