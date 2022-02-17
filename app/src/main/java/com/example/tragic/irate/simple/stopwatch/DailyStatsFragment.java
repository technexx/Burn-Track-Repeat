package com.example.tragic.irate.simple.stopwatch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class DailyStatsFragment extends Fragment {

    CalendarView calendarView;
    int dayOfMonthSelected;
    int monthSelected;
    int yearSelected;

    RecyclerView dailyStatsRecyclerview;

    TextView dailyStatsTotalSetTimeTextView;
    TextView dailyStatsTotalBreakTimeTextView;
    TextView dailyStatsTotalCaloriesBurnedTextView;

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.daily_stats_fragment_layout, container, false);

        calendarView = root.findViewById(R.id.stats_calendar);
        dailyStatsRecyclerview = root.findViewById(R.id.daily_stats_recyclerView);

        dailyStatsTotalSetTimeTextView = root.findViewById(R.id.daily_stats_total_set_time_textView);
        dailyStatsTotalBreakTimeTextView = root.findViewById(R.id.daily_stats_total_break_time_textView);
        dailyStatsTotalCaloriesBurnedTextView = root.findViewById(R.id.daily_stats_total_calories_burned_textView);


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

            }
        });

        return root;
    }


    private void assignTotalTimeAndCaloriesForSelectedDayToTextView() {

    }
}