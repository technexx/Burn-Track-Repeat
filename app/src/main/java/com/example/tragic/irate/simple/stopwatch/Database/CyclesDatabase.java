package com.example.tragic.irate.simple.stopwatch.Database;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.tragic.irate.simple.stopwatch.Database.DayStatClasses.DayHolder;
import com.example.tragic.irate.simple.stopwatch.Database.DayStatClasses.StatsForEachActivity;

@Database(entities = {Cycles.class, PomCycles.class, DayHolder.class, ActivitiesForEachDay.class, StatsForEachActivity.class, PomCycleStats.class}, version = 1, exportSchema =  false)

public abstract class CyclesDatabase extends RoomDatabase {
    private static CyclesDatabase INSTANCE;

    public static CyclesDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), CyclesDatabase.class, "cycles_database")
//                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    public abstract CyclesDao cyclesDao();
}