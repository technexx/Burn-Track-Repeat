package burn.track.repeat.Database;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import burn.track.repeat.Database.DailyCalorieClasses.CaloriesForEachFood;
import burn.track.repeat.Database.DailyStatClasses.StatsForEachActivity;

@Database(entities = {Cycles.class, PomCycles.class, StatsForEachActivity.class, CaloriesForEachFood.class}, version = 1, exportSchema =  false)

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