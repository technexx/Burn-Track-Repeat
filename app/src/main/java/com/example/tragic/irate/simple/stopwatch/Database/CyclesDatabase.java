package com.example.tragic.irate.simple.stopwatch.Database;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.tragic.irate.simple.stopwatch.Cycles;

@Database(entities = { Cycles.class}, version = 1, exportSchema =  false)

public abstract class CyclesDatabase extends RoomDatabase {
    private static CyclesDatabase INSTANCE;

    public static CyclesDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), CyclesDatabase.class, "cycles_database")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    public abstract CyclesDao cyclesDao();

}