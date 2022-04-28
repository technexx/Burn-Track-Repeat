package com.example.tragic.irate.simple.stopwatch;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tragic.irate.simple.stopwatch.Adapters.DailyStatsAdapter;
import com.example.tragic.irate.simple.stopwatch.Database.DayStatClasses.DayHolder;
import com.example.tragic.irate.simple.stopwatch.Database.DayStatClasses.StatsForEachActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class DailyStatsFragment extends Fragment {

    View mRoot;
    Calendar calendar;
    CalendarView calendarView;
    int daySelectedFromCalendar;

    DailyStatsAccess dailyStatsAccess;
    DailyStatsAdapter dailyStatsAdapter;
    RecyclerView dailyStatsRecyclerView;

    TextView dailyStatsTotalSetTimeTextView;
    TextView dailyStatsTotalBreakTimeTextView;
    TextView dailyStatsTotalCaloriesBurnedTextView;

    ImageButton dailyWeeklyMonthlySwitchButton;
    int currentStatDurationMode;
    int DAILY_STATS = 0;
    int WEEKLY_STATS = 1;
    int MONTHLY_STATS = 2;
    int YEARLY_STATS = 3;

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.daily_stats_fragment_layout, container, false);
        mRoot = root;

        calendarView = mRoot.findViewById(R.id.stats_calendar);
        dailyWeeklyMonthlySwitchButton = mRoot.findViewById(R.id.daily_weekly_monthly_switcher);

        dailyStatsAccess = new DailyStatsAccess(getActivity());

        instantiateTextViewsAndMiscClasses();
        instantiateRecyclerViewAndItsAdapter();
        queryDatabaseAndPopulatePojoListsAndUpdateRecyclerView(dailyStatsAccess.getCurrentDayOfYear());

        calendar = Calendar.getInstance(TimeZone.getDefault());
        daySelectedFromCalendar = calendar.get(Calendar.DAY_OF_YEAR);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);

                daySelectedFromCalendar = calendar.get(Calendar.DAY_OF_YEAR);
                queryDatabaseAndPopulatePojoListsAndUpdateRecyclerView(daySelectedFromCalendar);

                AsyncTask.execute(()-> {
                    setStatDurationModeViews(MONTHLY_STATS);
                });
                Log.i("testDate", "day of week is " + calendar.get(Calendar.DAY_OF_WEEK));
                Log.i("testDate", "day of month is " + calendar.get(Calendar.DAY_OF_MONTH));
                Log.i("testDate", "day of year is " + calendar.get(Calendar.DAY_OF_YEAR));
            }
        });

        return root;
    }

    public int getDaySelectedFromCalendar() {
        return daySelectedFromCalendar;
    }

    private void instantiateRecyclerViewAndItsAdapter() {
        dailyStatsAdapter = new DailyStatsAdapter(getContext(), dailyStatsAccess.totalActivitiesListForSelectedDay, dailyStatsAccess.totalSetTimeListForEachActivityForSelectedDay, dailyStatsAccess.totalBreakTimeListForEachActivityForSelectedDay, dailyStatsAccess.totalCaloriesBurnedForEachActivityForSelectedDay);

        dailyStatsRecyclerView = mRoot.findViewById(R.id.daily_stats_recyclerView);
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        dailyStatsRecyclerView.setLayoutManager(lm);
        dailyStatsRecyclerView.setAdapter(dailyStatsAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(dailyStatsRecyclerView.getContext(), lm.getOrientation());
        dailyStatsRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    public void queryDatabaseAndPopulatePojoListsAndUpdateRecyclerView(int dayToPopulate) {
        AsyncTask.execute(()-> {
            dailyStatsAccess.assignDayHolderInstanceFromSingleDay(dayToPopulate);
            dailyStatsAccess.setStatsForEachActivityListForSelectedDay(dayToPopulate);
            dailyStatsAccess.setStatsForEachActivityInstanceFromList();

            getActivity().runOnUiThread(()-> {
                populateDailyTotalTimesAndCaloriesTextViews();

                dailyStatsAccess.clearArrayListsOfActivitiesAndTheirStats();
                dailyStatsAccess.populatePojoListsForDailyActivityStatsForSelectedDay();
                dailyStatsAdapter.notifyDataSetChanged();
            });
        });
    }

    private void populateDailyTotalTimesAndCaloriesTextViews() {
        String totalSetTime = convertSeconds(dailyStatsAccess.getTotalSetTimeFromDayHolder());
        String totalBreakTime = convertSeconds(dailyStatsAccess.getTotalBreakTimeFromDayHolder());
        double totalCaloriesBurned = dailyStatsAccess.getTotalCaloriesBurnedFromDayHolder();

        dailyStatsTotalSetTimeTextView.setText(getString(R.string.daily_stats_string, getString(R.string.daily_set_time), totalSetTime));
        dailyStatsTotalBreakTimeTextView.setText(getString(R.string.daily_stats_string, getString(R.string.daily_break_time), totalBreakTime));
        dailyStatsTotalCaloriesBurnedTextView.setText(getString(R.string.daily_stats_string, getString(R.string.daily_calories_burned),formatCalorieString(totalCaloriesBurned)));
    }

    private void switchStatDurationMode() {
        if (currentStatDurationMode<3) {
            currentStatDurationMode++;
        } else {
            currentStatDurationMode=0;
        }
    }

    private void setStatDurationModeViews(int mode) {
        if (mode==DAILY_STATS) {
            dailyStatsAccess.assignDayHolderInstanceFromSingleDay(daySelectedFromCalendar);
        }
        if (mode==WEEKLY_STATS) {
            dailyStatsAccess.assignDayHolderInstanceFromWeek(calendar.get(Calendar.DAY_OF_WEEK), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.DAY_OF_YEAR));
        }
        if (mode==MONTHLY_STATS) {
            dailyStatsAccess.assignDayHolderInstanceFromMonth((calendar.get(Calendar.DAY_OF_WEEK)), calendar.getActualMaximum(Calendar.DAY_OF_MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        }
    }

    private void instantiateTextViewsAndMiscClasses() {
        dailyStatsTotalSetTimeTextView = mRoot.findViewById(R.id.daily_stats_total_set_time_textView);
        dailyStatsTotalBreakTimeTextView = mRoot.findViewById(R.id.daily_stats_total_break_time_textView);
        dailyStatsTotalCaloriesBurnedTextView = mRoot.findViewById(R.id.daily_stats_total_daily_time_and_calories_burned_textView);
    }

    private String formatCalorieString(double calories) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(calories);
    }

    private String convertSeconds(long totalSeconds) {
        DecimalFormat df = new DecimalFormat("00");
        long minutes;
        long remainingSeconds;
        totalSeconds = totalSeconds/1000;

        if (totalSeconds >=60) {
            minutes = totalSeconds/60;
            remainingSeconds = totalSeconds % 60;
            return (minutes + ":" + df.format(remainingSeconds));
        } else if (totalSeconds >=10) return "0:" + totalSeconds;
        else return "0:0" + totalSeconds;
    }
}