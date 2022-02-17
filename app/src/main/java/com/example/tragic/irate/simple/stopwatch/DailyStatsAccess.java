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

import java.util.Calendar;
import java.util.List;

public class DailyStatsAccess extends MainActivity {
    MainActivity mainActivity;
    Context mContext;
    CyclesDatabase cyclesDatabase;
    CalendarValues calendarValues = new CalendarValues();

    public DailyStatsAccess(Context context) {
        this.mContext = context;
    }

    private Runnable insertTotalTimesAndCaloriesBurnedOfCurrentDayIntoDatabase() {
        return new Runnable() {
            @Override
            public void run() {
                String date = calendarValues.getDateString();
                int dayOfYear = calendarValues.calendar.get(Calendar.DAY_OF_YEAR);

                //Check if current day already exists in db.
                List<DayHolder> dayHolderList = cyclesDatabase.cyclesDao().loadAllDayHolderRows();
                int dayHolderSize = dayHolderList.size();

                for (int i=0; i<dayHolderSize; i++) {
                    long dayInRow = dayHolderList.get(i).getDayId();
                    if (dayOfYear==dayInRow) {
                        retrieveTotalTimesAndCaloriesBurnedOfCurrentDayFromDatabase();
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
            }
        };
    }

    private void retrieveTotalTimesAndCaloriesBurnedOfCurrentDayFromDatabase() {
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        //Always a single row return, since only one exists per day of year.
        List<DayHolder> dayHolderList = cyclesDatabase.cyclesDao().loadSingleDay(dayOfYear);

        totalSetTimeForCurrentDayInMillis = dayHolderList.get(0).getTotalSetTime();
        totalBreakTimeForCurrentDayInMillis = dayHolderList.get(0).getTotalBreakTime();
        totalCaloriesBurnedForCurrentDay = dayHolderList.get(0).getTotalCaloriesBurned();
    }

    private Runnable updateTotalTimesAndCaloriesBurnedForCurrentDayFromDatabaseRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
                List<DayHolder> dayHolderList = cyclesDatabase.cyclesDao().loadAllDayHolderRows();
                DayHolder dayHolder = dayHolderList.get(dayOfYear);

                dayHolder.setTotalSetTime(totalSetTimeForCurrentDayInMillis);
                dayHolder.setTotalBreakTime(totalBreakTimeForCurrentDayInMillis);
                dayHolder.setTotalCaloriesBurned(totalCaloriesBurnedForCurrentDay);

                cyclesDatabase.cyclesDao().updateDayHolder(dayHolder);
            }
        };
    }



    //Since DayHolder's dayId and CycleStat's setUniqueDayIdPossessedByEachOfItsActivities are identical, we simply tie StatsForEachActivityWithinCycle's unique ID to that as well.
    private Runnable insertTotalTimesAndCaloriesForEachActivityWithinASpecificDayRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                int dayOfYear = calendarValues.calendar.get(Calendar.DAY_OF_YEAR);

                //Retrieves for activities tied to specific date ID, since we only want to check against the activities selected for current day.
                List<StatsForEachActivity> statsForEachActivityList = cyclesDatabase.cyclesDao().loadActivitiesForSpecificDate(dayOfYear);

                for (int i=0; i<statsForEachActivityList.size(); i++) {
                    if (mainActivity.getTdeeActivityStringFromSavedArrayPosition().equals(statsForEachActivityList.get(i).getActivity())) {
                        activityPositionInDb = i;
                        retrieveTotalTimesAndCaloriesForActivityWithinASpecificDayRunnable(activityPositionInDb);
                        return;
                    }
                }

                StatsForEachActivity statsForEachActivity = new StatsForEachActivity();
                statsForEachActivity.setUniqueIdTiedToTheSelectedActivity(dayOfYear);
                statsForEachActivity.setActivity(getTdeeActivityStringFromSavedArrayPosition());


                statsForEachActivity.setTotalSetTimeForEachActivity(0);
                statsForEachActivity.setTotalBreakTimeForEachActivity(0);
                statsForEachActivity.setTotalCaloriesBurnedForEachActivity(0);

                cyclesDatabase.cyclesDao().insertStatsForEachActivityWithinCycle(statsForEachActivity);
            }
        };
    }

    private void retrieveTotalTimesAndCaloriesForActivityWithinASpecificDayRunnable(int activityPosition) {
        List<StatsForEachActivity> statsForEachActivityList = cyclesDatabase.cyclesDao().loadActivitiesForSpecificDate(activityPosition);
        StatsForEachActivity statsForEachActivity = statsForEachActivityList.get(0);
    }

    private Runnable updateTotalTimesAndCaloriesBurnedForSpecificActivityOnSpecificDayRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

                //This retrieves all rows tied key'd to the day we have selected.
                List<StatsForEachActivity> statsList = cyclesDatabase.cyclesDao().loadActivitiesForSpecificDate(dayOfYear);

                StatsForEachActivity statsForEachActivity = new StatsForEachActivity();
                if (statsList.size() >= activityPositionInDb+1) {
                    statsForEachActivity = statsList.get(activityPositionInDb);
                } else {
                    Log.e("Stats Database", "Activity position in StatsForEachActivityWithinCycle does not exist!");
                }

                statsForEachActivity.setTotalSetTimeForEachActivity(totalSetTimeForSpecificActivityForCurrentDayInMillis);
                statsForEachActivity.setTotalBreakTimeForEachActivity(totalBreakTimeForSpecificActivityForCurrentDayInMillis);
                statsForEachActivity.setTotalCaloriesBurnedForEachActivity(totalCaloriesBurnedForSpecificActivityForCurrentDay);
                cyclesDatabase.cyclesDao().updateStatsForEachActivity(statsForEachActivity);
            }
        };
    }

    private void testStatsForEachActivityInsertion() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                StatsForEachActivity statsForEachActivity = new StatsForEachActivity();
                List<StatsForEachActivity> statsForEachActivityList = cyclesDatabase.cyclesDao().loadAllActivitiesForAllDays();

                statsForEachActivity.setActivity("test" + statsForEachActivityList.size());
                cyclesDatabase.cyclesDao().insertStatsForEachActivityWithinCycle(statsForEachActivity);

                Log.i("testDb", "number of activities added is " + statsForEachActivityList.size());
                for (int i=0; i<statsForEachActivityList.size(); i++) {
                    Log.i("testDb", "activity name is " + statsForEachActivityList.get(i).getActivity());
                    Log.i("testDb", "unique ID is " + statsForEachActivityList.get(i).getUniqueIdTiedToTheSelectedActivity());
                }
            }
        });
    }

    private void testStatsForDayInsertion(boolean changingDate) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int dayOfYearChangingTestValue = calendar.get(Calendar.DAY_OF_YEAR);

                List<DayHolder> dayHolderList = cyclesDatabase.cyclesDao().loadAllDayHolderRows();
                int dayHolderSize = dayHolderList.size();

                for (int i=0; i<dayHolderSize; i++) {
                    if (changingDate) {
                        dayOfYearChangingTestValue = 10*i;
                    }

                    long dayInRow = dayHolderList.get(i).getDayId();
                    if (dayInRow==dayOfYearChangingTestValue) {
                        long dayID = cyclesDatabase.cyclesDao().loadAllDayHolderRows().get(i).getDayId();
                        Log.i("testdb", "dayholder contains dates " + dayID);
                    }
                }
            }
        });
    }

    private void logTotalTimesForSelectedDay() {
        Log.i("testStats", "total set time is " + totalSetTimeForCurrentDayInMillis);
        Log.i("testStats", "total break time is " + totalBreakTimeForCurrentDayInMillis);
    }

    private void logTotalCaloriesForSelectedDay() {
        Log.i("testStats", "total calories time are " + totalCaloriesBurnedForCurrentDay);
    }

    private void logTotalTimesForSpecificActivityOnSelectedDay() {
        Log.i("testStats", "total set time for activity is " + totalSetTimeForSpecificActivityForCurrentDayInMillis);
        Log.i("testStats", "total break time for activity is " + totalBreakTimeForSpecificActivityForCurrentDayInMillis);
    }

    private void logTotalCaloriesForSpecificActivityOnSelectedDay() {
        Log.i("testStats", "total calories for activity are " + totalCaloriesBurnedForSpecificActivityForCurrentDay);
    }

    public void instantiateDatabase() {
        mainActivity = new MainActivity();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                cyclesDatabase = CyclesDatabase.getDatabase(mContext);
            }
        });
    }
}
