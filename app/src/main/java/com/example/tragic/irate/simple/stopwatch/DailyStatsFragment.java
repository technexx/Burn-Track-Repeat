package com.example.tragic.irate.simple.stopwatch;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tragic.irate.simple.stopwatch.Adapters.DailyStatsAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class DailyStatsFragment extends Fragment {

    View mRoot;
    DailyStatsAccess dailyStatsAccess;
    DailyStatsAdapter dailyStatsAdapter;
    Calendar calendar;
    CalendarView calendarView;
    int dayOfYear;
    RecyclerView dailyStatsRecyclerView;

    TextView dailyStatsTotalSetTimeTextView;
    TextView dailyStatsTotalBreakTimeTextView;
    TextView dailyStatsTotalCaloriesBurnedTextView;

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.daily_stats_fragment_layout, container, false);
        mRoot = root;

        instantiateTextViewsAndMiscClasses();
        dailyStatsAccess = new DailyStatsAccess(getActivity());

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                dailyStatsAccess.executeAllAssignToListMethods();

                dailyStatsAdapter = new DailyStatsAdapter(getContext(),dailyStatsAccess.totalActivitiesListForSelectedDay, dailyStatsAccess.totalSetTimeListForEachActivityForSelectedDay, dailyStatsAccess.totalBreakTimeListForEachActivityForSelectedDay, dailyStatsAccess.totalCaloriesBurnedForEachActivityForSelectedDay);

                dailyStatsRecyclerView = mRoot.findViewById(R.id.daily_stats_recyclerView);
                LinearLayoutManager lm = new LinearLayoutManager(getContext());
                dailyStatsRecyclerView.setLayoutManager(lm);
                dailyStatsRecyclerView.setAdapter(dailyStatsAdapter);
            }
        });


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                calendar = Calendar.getInstance(TimeZone.getDefault());
                calendar.set(year, month, dayOfMonth);
                dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

                assignTotalTimeAndCaloriesForSelectedDayToTextView();
            }
        });

        Button testButton = root.findViewById(R.id.test_button);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask.execute(dailyStatsAccess.insertTotalTimesAndCaloriesBurnedOfCurrentDayIntoDatabase());
            }
        });

        return root;
    }



    private void assignTotalTimeAndCaloriesForSelectedDayToTextView() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                dailyStatsAccess.retrieveTotalTimesAndCaloriesBurnedOfSelectedDayFromDatabase(dayOfYear);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dailyStatsTotalSetTimeTextView.setText(getString(R.string.daily_stats_string, getString(R.string.daily_set_time), String.valueOf(dailyStatsAccess.totalSetTimeForCurrentDayInMillis)));

                        dailyStatsTotalBreakTimeTextView.setText(getString(R.string.daily_stats_string, getString(R.string.daily_break_time), String.valueOf(dailyStatsAccess.totalBreakTimeForCurrentDayInMillis)));

                        dailyStatsTotalCaloriesBurnedTextView.setText(getString(R.string.daily_stats_string, getString(R.string.daily_calories_burned), String.valueOf(dailyStatsAccess.totalCaloriesBurnedForCurrentDay)));

                        Toast.makeText(getContext(), "Day is " + dayOfYear, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void instantiateTextViewsAndMiscClasses() {
        dailyStatsTotalSetTimeTextView = mRoot.findViewById(R.id.daily_stats_total_set_time_textView);
        dailyStatsTotalBreakTimeTextView = mRoot.findViewById(R.id.daily_stats_total_break_time_textView);
        dailyStatsTotalCaloriesBurnedTextView = mRoot.findViewById(R.id.daily_stats_total_calories_burned_textView);

        calendarView = mRoot.findViewById(R.id.stats_calendar);
    }
}