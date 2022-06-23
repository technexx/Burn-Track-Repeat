package com.example.tragic.irate.simple.stopwatch.Database.DailyCalorieClasses;

import android.content.Context;
import android.os.AsyncTask;

import com.example.tragic.irate.simple.stopwatch.Database.CyclesDatabase;
import com.example.tragic.irate.simple.stopwatch.Database.DailyStatClasses.DayHolder;
import com.example.tragic.irate.simple.stopwatch.Database.DailyStatClasses.StatsForEachActivity;

import java.util.ArrayList;
import java.util.List;

public class DailyCalorieAccess {
    Context mContext;
    CyclesDatabase cyclesDatabase;

    CalorieDayHolder calorieDayHolder;
    CaloriesForEachFood caloriesForEachFood;

    List<CalorieDayHolder> calorieDayHolderList;
    List<CaloriesForEachFood> caloriesForEachFoodList;

    double totalCaloriesConsumedForSelectedDuration;
    int typeOfFoodPositionInListForCurrentDay;
    int mSortMode = 1;

    //Todo: Should we use getDoesDayExistInDatabase() from DailyStats here?
    public DailyCalorieAccess(Context context) {
        this.mContext = context;
        instantiateDailyStatsDatabase();
        instantiateEntitiesAndTheirLists();
    }

    public void setSortMode(int sortMode) {
        this.mSortMode = sortMode;
    }

    public List<CaloriesForEachFood> assignCaloriesForEachFoodListBySortMode(List<Integer> listOfDays) {
        List<CaloriesForEachFood> listToReturn = new ArrayList<>();

        switch (mSortMode) {
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

    private void instantiateDailyStatsDatabase() {
        AsyncTask.execute(()->{
            cyclesDatabase = CyclesDatabase.getDatabase(mContext);
        });
    }

    private void instantiateEntitiesAndTheirLists() {
        calorieDayHolder = new CalorieDayHolder();
        caloriesForEachFood = new CaloriesForEachFood();


        calorieDayHolderList = new ArrayList<>();
        caloriesForEachFoodList = new ArrayList<>();
    }

}
