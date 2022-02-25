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

public class DailyStatsAccess extends MainActivity {
    MainActivity mainActivity;
    Context mContext;
    CyclesDatabase cyclesDatabase;
    CalendarValues calendarValues = new CalendarValues();

    List<StatsForEachActivity> statsForEachActivityList;

    List<String> totalActivitiesListForSelectedDay;
    List<Long> totalSetTimeListForEachActivityForSelectedDay;
    List<Long> totalBreakTimeListForEachActivityForSelectedDay;
    List<Double> totalCaloriesBurnedForEachActivityForSelectedDay;

    DayHolder retrievedDayHolderInstanceForSingleDay;
    long retrievedTotalSetTime;
    long retrievedTotalBreakTime;
    double retrievedTotalCaloriesBurned;

    StatsForEachActivity retrievedStatForEachActivityInstanceForSpecificActivityWithinSelectedDay;

    public DailyStatsAccess(Context context) {
        this.mContext = context;
        instantiateMainActivityAndDailyStatsDatabase();
        instantiateArrayListsOfActivitiesAndTheirStats();
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

    public void insertTotalTimesAndCaloriesBurnedOfCurrentDayIntoDatabase() {
        String date = calendarValues.getDateString();
        int dayOfYear = calendarValues.calendar.get(Calendar.DAY_OF_YEAR);

        //Check if current day already exists in db.
        List<DayHolder> dayHolderList = cyclesDatabase.cyclesDao().loadAllDayHolderRows();
        int dayHolderSize = dayHolderList.size();

        for (int i=0; i<dayHolderSize; i++) {
            long dayInRow = dayHolderList.get(i).getDayId();
            if (dayOfYear==dayInRow) {
                queryTotalTimesAndCaloriesBurnedFromSelectedDay(dayOfYear);
                Log.i("testInsert", "Returned from insertion because day already exists");
                return;
            }
        }

        DayHolder dayHolder = new DayHolder();
        ActivitiesForEachDay activitiesForEachDay = new ActivitiesForEachDay();

        dayHolder.setDayId(dayOfYear);
        dayHolder.setDate(date);

        activitiesForEachDay.setUniqueDayIdPossessedByEachOfItsActivities(dayOfYear);
        dayHolder.setTotalSetTime(0);
        dayHolder.setTotalBreakTime(0);
        dayHolder.setTotalCaloriesBurned(0);

        cyclesDatabase.cyclesDao().insertDay(dayHolder);
        cyclesDatabase.cyclesDao().insertActivitiesForEachDay(activitiesForEachDay);

        zeroOutIteratingTotalTimesAndCaloriesForDailyStats();

        Log.i("testInsert", "Day was inserted and is day " + dayOfYear);
    }

    public void queryTotalTimesAndCaloriesBurnedFromSelectedDay(int dayToRetrieve) {
        //Always a single row return, since only one exists per day of year.
        List<DayHolder> dayHolderList = cyclesDatabase.cyclesDao().loadSingleDay(dayToRetrieve);

        if (dayHolderList.size()>0) {
            retrievedTotalSetTime = dayHolderList.get(0).getTotalSetTime();
            retrievedTotalBreakTime = dayHolderList.get(0).getTotalBreakTime();
            retrievedTotalCaloriesBurned = dayHolderList.get(0).getTotalCaloriesBurned();
        } else {
            retrievedTotalSetTime = 0;
            retrievedTotalBreakTime = 0;
            retrievedTotalCaloriesBurned = 0;
        }
    }

    public void zeroOutIteratingTotalTimesAndCaloriesForDailyStats() {
        totalSetTimeForCurrentDayInMillis = 0;
        totalBreakTimeForCurrentDayInMillis = 0;
        totalCaloriesBurnedForCurrentDay = 0;
    }

    public void assignRetrievedTotalTimesAndCaloriesBurnedFromSelectedDayToIteratingVariables() {
        totalSetTimeForCurrentDayInMillis = retrievedTotalSetTime;
        totalBreakTimeForCurrentDayInMillis = retrievedTotalBreakTime;
        totalCaloriesBurnedForCurrentDay = retrievedTotalCaloriesBurned;
    }

    public void retrieveDayHolderListForSingleDay(int dayToRetrieve) {
        List<DayHolder> dayHolderList = cyclesDatabase.cyclesDao().loadSingleDay(dayToRetrieve);
        retrievedDayHolderInstanceForSingleDay = dayHolderList.get(0);
    }

    public void updateTotalTimesAndCaloriesBurnedForCurrentDayFromDatabase() {
        retrievedDayHolderInstanceForSingleDay.setTotalSetTime(totalSetTimeForCurrentDayInMillis);
        retrievedDayHolderInstanceForSingleDay.setTotalBreakTime(totalBreakTimeForCurrentDayInMillis);
        retrievedDayHolderInstanceForSingleDay.setTotalCaloriesBurned(totalCaloriesBurnedForCurrentDay);

        cyclesDatabase.cyclesDao().updateDayHolder(retrievedDayHolderInstanceForSingleDay);
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


    public void logTotalTimesForSelectedDay() {
        Log.i("testStats", "total set time is " + totalSetTimeForCurrentDayInMillis);
        Log.i("testStats", "total break time is " + totalBreakTimeForCurrentDayInMillis);
    }

    public void logTotalCaloriesForSelectedDay() {
        Log.i("testStats", "total calories time are " + totalCaloriesBurnedForCurrentDay);
    }

    public void logTotalTimesForSpecificActivityOnSelectedDay() {
        Log.i("testStats", "total set time for activity is " + totalSetTimeForSpecificActivityForCurrentDayInMillis);
        Log.i("testStats", "total break time for activity is " + totalBreakTimeForSpecificActivityForCurrentDayInMillis);
    }

    public void logTotalCaloriesForSpecificActivityOnSelectedDay() {
        Log.i("testStats", "total calories for activity are " + totalCaloriesBurnedForSpecificActivityForCurrentDay);
    }
}