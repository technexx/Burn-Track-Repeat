package com.example.tragic.irate.simple.stopwatch;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.loader.content.AsyncTaskLoader;

import com.example.tragic.irate.simple.stopwatch.Database.CyclesDao_Impl;
import com.example.tragic.irate.simple.stopwatch.Database.CyclesDatabase;
import com.example.tragic.irate.simple.stopwatch.Database.DayStatClasses.ActivitiesForEachDay;
import com.example.tragic.irate.simple.stopwatch.Database.DayStatClasses.DayHolder;
import com.example.tragic.irate.simple.stopwatch.Database.DayStatClasses.StatsForEachActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DailyStatsAccess {
    MainActivity mainActivity;
    Context mContext;
    CyclesDatabase cyclesDatabase;
    CalendarValues calendarValues = new CalendarValues();

    List<StatsForEachActivity> statsForEachActivityList;
    boolean dayExistsInDatabase;

    List<String> totalActivitiesListForSelectedDay;
    List<Long> totalSetTimeListForEachActivityForSelectedDay;
    List<Long> totalBreakTimeListForEachActivityForSelectedDay;
    List<Double> totalCaloriesBurnedForEachActivityForSelectedDay;

    StatsForEachActivity retrievedStatForEachActivityInstanceForSpecificActivityWithinSelectedDay;
    int activityPositionInDb;

    public DailyStatsAccess(Context context) {
        this.mContext = context;
        instantiateMainActivityAndDailyStatsDatabase();
        instantiateArrayListsOfActivitiesAndTheirStats();
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
        if (checkIfDayAlreadyExistsInDatabase(daySelected)) {
            dayExistsInDatabase = true;
        } else {
            String date = calendarValues.getDateString();

            DayHolder dayHolder = new DayHolder();
            ActivitiesForEachDay activitiesForEachDay = new ActivitiesForEachDay();

            dayHolder.setDayId(daySelected);
            dayHolder.setDate(date);

            activitiesForEachDay.setUniqueDayIdPossessedByEachOfItsActivities(daySelected);
            dayHolder.setTotalSetTime(0);
            dayHolder.setTotalBreakTime(0);
            dayHolder.setTotalCaloriesBurned(0);

            cyclesDatabase.cyclesDao().insertDay(dayHolder);
            cyclesDatabase.cyclesDao().insertActivitiesForEachDay(activitiesForEachDay);

            dayExistsInDatabase = false;
        }
    }

    public boolean dayExistsInDatabase() {
        return dayExistsInDatabase;
    }

    public List<DayHolder> queryDayHolderListForSingleDay(int dayToRetrieve) {
        return cyclesDatabase.cyclesDao().loadSingleDay(dayToRetrieve);
    }

    public DayHolder queryAndSetGlobalDayHolderInstanceForSelectedDayFromDayHolderList(List<DayHolder> dayHolderList) {
        return dayHolderList.get(0);
    }

    public long getTotalSetTimeFromDayHolder(DayHolder dayHolder) {
        return dayHolder.getTotalSetTime();
    }

    public long getTotalBreakTimeFromDayHolder(DayHolder dayHolder) {
        return dayHolder.getTotalBreakTime();
    }

    public double getTotalCaloriesBurnedFromDayHolder(DayHolder dayHolder) {
        return dayHolder.getTotalCaloriesBurned();
    }

    public void updateTotalTimesAndCaloriesBurnedForCurrentDayFromDatabase(DayHolder dayHolder) {
        cyclesDatabase.cyclesDao().updateDayHolder(dayHolder);
    }


    public void checkIfActivityAlreadyExistsInDatabaseForSelectedDay (int daySelected) {

    }

    //Since DayHolder's dayId and CycleStat's setUniqueDayIdPossessedByEachOfItsActivities are identical, we simply tie StatsForEachActivityWithinCycle's unique ID to that as well.
    public void insertTotalTimesAndCaloriesForEachActivityWithinASpecificDay() {
        int dayOfYear = calendarValues.calendar.get(Calendar.DAY_OF_YEAR);

        //Retrieves for activities tied to specific date ID, since we only want to check against the activities selected for current day.
        List<StatsForEachActivity> statsForEachActivityList = cyclesDatabase.cyclesDao().loadActivitiesForSpecificDate(dayOfYear);

        for (int i=0; i<statsForEachActivityList.size(); i++) {
            if (mainActivity.getTdeeActivityStringFromArrayPosition().equals(statsForEachActivityList.get(i).getActivity())) {
                activityPositionInDb = i;
                return;
            }
        }

        StatsForEachActivity statsForEachActivity = new StatsForEachActivity();
        statsForEachActivity.setUniqueIdTiedToTheSelectedActivity(dayOfYear);
        statsForEachActivity.setActivity(getTdeeActivityStringFromArrayPosition());

        statsForEachActivity.setTotalSetTimeForEachActivity(0);
        statsForEachActivity.setTotalBreakTimeForEachActivity(0);
        statsForEachActivity.setTotalCaloriesBurnedForEachActivity(0);

        cyclesDatabase.cyclesDao().insertStatsForEachActivityWithinCycle(statsForEachActivity);
    }

    public void retrieveStatForEachActivityInstanceForSpecificActivityWithinSelectedDay() {
        int dayOfYear = calendarValues.calendar.get(Calendar.DAY_OF_YEAR);
        List<StatsForEachActivity> statsList = cyclesDatabase.cyclesDao().loadActivitiesForSpecificDate(dayOfYear);

        //If activity exists, retrieve an instance of StatForEachActivity for its position. If not, create a new entity instance.
        if (statsList.size() >= activityPositionInDb+1) {
            retrievedStatForEachActivityInstanceForSpecificActivityWithinSelectedDay = statsList.get(activityPositionInDb);
        } else {
            retrievedStatForEachActivityInstanceForSpecificActivityWithinSelectedDay = new StatsForEachActivity();
        }
    }

    public void updateTotalTimesAndCaloriesBurnedForSpecificActivityOnSpecificDayRunnable() {
        retrievedStatForEachActivityInstanceForSpecificActivityWithinSelectedDay.setTotalSetTimeForEachActivity(totalSetTimeForSpecificActivityForCurrentDayInMillis);
        retrievedStatForEachActivityInstanceForSpecificActivityWithinSelectedDay.setTotalBreakTimeForEachActivity(totalBreakTimeForSpecificActivityForCurrentDayInMillis);
        retrievedStatForEachActivityInstanceForSpecificActivityWithinSelectedDay.setTotalCaloriesBurnedForEachActivity(totalCaloriesBurnedForSpecificActivityForCurrentDay);
        cyclesDatabase.cyclesDao().updateStatsForEachActivity(retrievedStatForEachActivityInstanceForSpecificActivityWithinSelectedDay);
    }

    public void queryStatsForEachActivityForSelectedDay(int daySelected) {
        statsForEachActivityList = cyclesDatabase.cyclesDao().loadActivitiesForSpecificDate(daySelected);
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
        for (int i=0; i<statsForEachActivityList.size(); i++) {
            totalActivitiesListForSelectedDay.add(statsForEachActivityList.get(i).getActivity());
        }
    }

    private void assignTotalSetTimeForEachActivityOnSelectedDayToList() {
        for (int i=0; i<statsForEachActivityList.size(); i++) {
            totalSetTimeListForEachActivityForSelectedDay.add(statsForEachActivityList.get(i).getTotalSetTimeForEachActivity());
        }
    }

    private void assignTotalBreakTimeForEachActivityOnSelectedDayToList() {
        for (int i=0; i<statsForEachActivityList.size(); i++) {
            totalBreakTimeListForEachActivityForSelectedDay.add(statsForEachActivityList.get(i).getTotalBreakTimeForEachActivity());
        }
    }

    private void assignTotalCaloriesForEachActivityOnSelectedDayToList() {
        for (int i=0; i<statsForEachActivityList.size(); i++) {
            totalCaloriesBurnedForEachActivityForSelectedDay.add(statsForEachActivityList.get(i).getTotalCaloriesBurnedForEachActivity());
        }
    }

    private void instantiateMainActivityAndDailyStatsDatabase() {
        mainActivity = new MainActivity();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                cyclesDatabase = CyclesDatabase.getDatabase(mContext);
            }
        });
    }

    private void instantiateArrayListsOfActivitiesAndTheirStats() {
        totalActivitiesListForSelectedDay = new ArrayList<>();
        totalSetTimeListForEachActivityForSelectedDay = new ArrayList<>();
        totalBreakTimeListForEachActivityForSelectedDay = new ArrayList<>();
        totalCaloriesBurnedForEachActivityForSelectedDay = new ArrayList<>();
    }
}