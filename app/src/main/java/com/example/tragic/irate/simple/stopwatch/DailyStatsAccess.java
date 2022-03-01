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
    long totalDailySetTimeToSave;
    long totalDailyBreakTimeToSave;
    double totalDailyCaloriesBurnedToSave;

    List<String> totalActivitiesListForSelectedDay;
    List<Long> totalSetTimeListForEachActivityForSelectedDay;
    List<Long> totalBreakTimeListForEachActivityForSelectedDay;
    List<Double> totalCaloriesBurnedForEachActivityForSelectedDay;

    StatsForEachActivity retrievedStatForEachActivityInstanceForSpecificActivityWithinSelectedDay;
    long totalDailySetTimeForActivityToSave;
    long totalDailyBreakTimeForActivityToSave;
    double totalDailyCaloriesBurnedForActivityToSave;

    boolean activityExistsInDatabase;
    int activityPositionInDb;

    public DailyStatsAccess(Context context) {
        this.mContext = context;
        instantiateMainActivityAndDailyStatsDatabase();
        instantiateArrayListsOfActivitiesAndTheirStats();
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
            ActivitiesForEachDay activitiesForEachDay = new ActivitiesForEachDay();

            dayHolder.setDayId(daySelected);
            dayHolder.setDate(date);

            activitiesForEachDay.setUniqueDayIdPossessedByEachOfItsActivities(daySelected);
            dayHolder.setTotalSetTime(0);
            dayHolder.setTotalBreakTime(0);
            dayHolder.setTotalCaloriesBurned(0);

            cyclesDatabase.cyclesDao().insertDay(dayHolder);
            cyclesDatabase.cyclesDao().insertActivitiesForEachDay(activitiesForEachDay);
        }
    }

    public List<DayHolder> queryDayHolderListForSingleDay(int dayToRetrieve) {
        return cyclesDatabase.cyclesDao().loadSingleDay(dayToRetrieve);
    }

    public DayHolder queryAndSetDayHolderInstanceForSelectedDay(List<DayHolder> dayHolderList) {
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

    public void setTotalSetTimeFromActivity(long totalSetTime) {
        this.totalDailySetTimeToSave = totalSetTime;
    }

    public void setTotalBreakTimeFromActivity(long totalBreakTime) {
        this.totalDailyBreakTimeToSave = totalBreakTime;
    }

    public void setTotalCaloriesBurnedFromActivity(double totalCalories) {
        this.totalDailyCaloriesBurnedToSave = totalCalories;
    }

    public void updateTotalTimesAndCaloriesBurnedForCurrentDayFromDatabase(DayHolder dayHolder) {
        cyclesDatabase.cyclesDao().updateDayHolder(dayHolder);
    }




    public void checkIfActivityAlreadyExistsInDatabaseForSelectedDayAndSetActivityPosition(int daySelected) {
        //Retrieves for activities tied to specific date ID, since we only want to check against the activities selected for current day.
        List<StatsForEachActivity> statsForEachActivityList = cyclesDatabase.cyclesDao().loadActivitiesForSpecificDate(daySelected);

        for (int i=0; i<statsForEachActivityList.size(); i++) {
            if (mainActivity.getTdeeActivityStringFromArrayPosition().equals(statsForEachActivityList.get(i).getActivity())) {
                activityPositionInDb = i;
                activityExistsInDatabase = true;
            } else {
                activityExistsInDatabase = false;
            }
        }
    }

    //Since DayHolder's dayId and CycleStat's setUniqueDayIdPossessedByEachOfItsActivities are identical, we simply tie StatsForEachActivityWithinCycle's unique ID to that as well.
    public void insertTotalTimesAndCaloriesForEachActivityWithinASpecificDay(String activitySelected) {
        int dayOfYear = calendarValues.calendar.get(Calendar.DAY_OF_YEAR);

        //Nothing to do if activity already exists.
        if (!activityExistsInDatabase) {
            StatsForEachActivity statsForEachActivity = new StatsForEachActivity();
            statsForEachActivity.setUniqueIdTiedToTheSelectedActivity(dayOfYear);
            statsForEachActivity.setActivity(activitySelected);

            statsForEachActivity.setTotalSetTimeForEachActivity(0);
            statsForEachActivity.setTotalBreakTimeForEachActivity(0);
            statsForEachActivity.setTotalCaloriesBurnedForEachActivity(0);

            cyclesDatabase.cyclesDao().insertStatsForEachActivityWithinCycle(statsForEachActivity);
        }
    }

    public List<StatsForEachActivity> queryStatsForEachActivityForSingleDay(int dayToRetrieve) {
        return cyclesDatabase.cyclesDao().loadActivitiesForSpecificDate(dayToRetrieve);
    }

    public StatsForEachActivity queryAndSetStatsForEachActivityInstanceForSelectedActivity(List<StatsForEachActivity> statsForEachActivityList, int activityPosition) {
        return statsForEachActivityList.get(activityPosition);
    }

    public boolean doesActivityExistInDatabase() {
        return activityExistsInDatabase;
    }

    public int getActivityPosition() {
        return activityPositionInDb;
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

    public long getTotalSetTimeForSelectedActivity(StatsForEachActivity statsForEachActivity) {
        return statsForEachActivity.getTotalSetTimeForEachActivity();
    }

    public long getTotalBreakTimeForSelectedActivity(StatsForEachActivity statsForEachActivity) {
        return statsForEachActivity.getTotalBreakTimeForEachActivity();
    }

    public double getTotalCaloriesBurnedForSelectedActivity(StatsForEachActivity statsForEachActivity) {
        return statsForEachActivity.getTotalCaloriesBurnedForEachActivity();
    }

    public void setTotalSetTimeForSelectedActivity(long totalSetTimeForActivity) {
        this.totalDailySetTimeForActivityToSave = totalSetTimeForActivity;
    }

    public void setTotalBreakTimeForSelectedActivity(long totalBreakTimeForActivity) {
        this.totalDailyBreakTimeForActivityToSave = totalBreakTimeForActivity;
    }

    public void setTotalCaloriesBurnedForSelectedActivity(double caloriesBurnedForActivity) {
        this.totalDailyCaloriesBurnedForActivityToSave = caloriesBurnedForActivity;
    }


    public void updateTotalTimesAndCaloriesBurnedForSpecificActivityOnSpecificDayRunnable() {
        cyclesDatabase.cyclesDao().updateStatsForEachActivity(retrievedStatForEachActivityInstanceForSpecificActivityWithinSelectedDay);
    }



    //////////////////Daily Stats Fragment Methods/////////////////////////////////////////////

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

    private void instantiateArrayListsOfActivitiesAndTheirStats() {
        totalActivitiesListForSelectedDay = new ArrayList<>();
        totalSetTimeListForEachActivityForSelectedDay = new ArrayList<>();
        totalBreakTimeListForEachActivityForSelectedDay = new ArrayList<>();
        totalCaloriesBurnedForEachActivityForSelectedDay = new ArrayList<>();
    }
}