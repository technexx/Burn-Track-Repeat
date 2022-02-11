package com.example.tragic.irate.simple.stopwatch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.Calendar;

public class CycleStatsFragment extends Fragment {

    TextView cycleStatsHeader;

    TextView cycleStatsTotalSetTimeTextView;
    TextView cycleStatsTotalBreakTimeTextView;

    TextView cycleStatsActivityTextView;
    TextView cycleStatsActivityTotalSetTimeTextView;
    TextView cycleStatsActivityTotalBreakTimeTextView;
    TextView cycleStatsActivityTotalCaloriesBurnedTextView;


    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.cycle_stats_fragment_layout, container, false);

        cycleStatsHeader = root.findViewById(R.id.cycle_stats_header);

        cycleStatsTotalSetTimeTextView = root.findViewById(R.id.cycle_stats_total_set_time_textView);
        cycleStatsTotalBreakTimeTextView = root.findViewById(R.id.cycle_stats_total_break_time_textView);

        cycleStatsActivityTextView = root.findViewById(R.id.cycle_stats_activity_textView);
        cycleStatsActivityTotalSetTimeTextView = root.findViewById(R.id.cycle_stats_activity_total_set_time_textView);
        cycleStatsActivityTotalBreakTimeTextView = root.findViewById(R.id.cycle_stats_activity_total_break_time_textView);
        cycleStatsActivityTotalCaloriesBurnedTextView = root.findViewById(R.id.cycle_stats_activity_total_calories_burned_TextView);

        return root;
    }

    private void testCalendar() {
        cycleStatsHeader.setOnClickListener(v-> {
            CalendarView calendarView = new CalendarView(getContext());
        });
    }
}