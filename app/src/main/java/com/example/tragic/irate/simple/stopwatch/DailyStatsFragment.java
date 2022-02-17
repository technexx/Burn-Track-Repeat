package com.example.tragic.irate.simple.stopwatch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class DailyStatsFragment extends Fragment {

//    DatePicker datePicker;
    CalendarView calendarView;
    int dayOfMonthSelected;
    int monthSelected;
    int yearSelected;

    TextView dailyStatsHeader;
    RecyclerView dailyStatsRecyclerview;

    TextView dailyStatsTotalSetTimeTextView;
    TextView dailyStatsTotalBreakTimeTextView;
    TextView dailyStatsTotalCaloriesBurnedTextView;

    //Todo: Should have recyclerView for multiple activities in day.
    //Todo: Totals for day should also be in recyclerView as last row. That way, they will automatically place themselves at bottom and fully aligned.

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.daily_stats_fragment_layout, container, false);

//        datePicker = root.findViewById(R.id.stats_date_picker);
        calendarView = root.findViewById(R.id.stats_calendar);
        dailyStatsHeader = root.findViewById(R.id.daily_stats_header);
        dailyStatsRecyclerview = root.findViewById(R.id.daily_stats_recyclerView);

        dailyStatsTotalSetTimeTextView = root.findViewById(R.id.daily_stats_total_set_time_textView);
        dailyStatsTotalBreakTimeTextView = root.findViewById(R.id.daily_stats_total_break_time_textView);
        dailyStatsTotalCaloriesBurnedTextView = root.findViewById(R.id.daily_stats_total_calories_burned_textView);

        testCalendar();

        return root;
    }

    private void testCalendar() {
        CalendarValues calendarValues = new CalendarValues();
        String date = calendarValues.getDateString();

        dailyStatsHeader.setText(date);

        yearSelected = calendarValues.getYearSelected();
        monthSelected = calendarValues.getMonthSelected();
        dayOfMonthSelected = calendarValues.getDayOfMonthSelected();
    }
}