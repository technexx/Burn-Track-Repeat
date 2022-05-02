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
    CalendarView calendarView;
    int daySelectedFromCalendar;

    DailyStatsAccess dailyStatsAccess;
    DailyStatsAdapter dailyStatsAdapter;
    RecyclerView dailyStatsRecyclerView;

    TextView totalStatsHeaderTextView;
    TextView statsTotalSetTimeTextView;
    TextView statsTotalBreakTimeTextView;
    TextView statsTotalCaloriesBurnedTextView;

    ImageButton statDurationSwitcherButtonLeft;
    ImageButton statDurationSwitcherButtonRight;

    int currentStatDurationMode;
    int DAILY_STATS = 0;
    int WEEKLY_STATS = 1;
    int MONTHLY_STATS = 2;
    int YEARLY_STATS = 3;

    int ITERATING_STATS_UP = 0;
    int ITERATING_STATS_DOWN = 1;

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.daily_stats_fragment_layout, container, false);
        mRoot = root;

        calendarView = mRoot.findViewById(R.id.stats_calendar);
        dailyStatsAccess = new DailyStatsAccess(getActivity());

        instantiateTextViewsAndMiscClasses();
        instantiateRecyclerViewAndItsAdapter();

        AsyncTask.execute(()-> {
            setDayAndStatListsForChosenDurationOfDays(currentStatDurationMode);

            getActivity().runOnUiThread(()-> {
                setStatDurationTextView(currentStatDurationMode);
                populateListsAndTextViewsFromEntityListsInDatabase();

            });
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                AsyncTask.execute(()-> {
                    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                    calendar.set(year, month, dayOfMonth);
                    daySelectedFromCalendar = calendar.get(Calendar.DAY_OF_YEAR);

                    setDayAndStatListsForChosenDurationOfDays(currentStatDurationMode);

                    getActivity().runOnUiThread(()-> {
                        populateListsAndTextViewsFromEntityListsInDatabase();
                    });
                });
            }
        });

        statDurationSwitcherButtonLeft.setOnClickListener(v-> {
            statDurationSwitchModeLogic(ITERATING_STATS_DOWN);
        });

        statDurationSwitcherButtonRight.setOnClickListener(v-> {
            statDurationSwitchModeLogic(ITERATING_STATS_UP);
        });

        return root;
    }

    private void statDurationSwitchModeLogic(int directionOfIteratingDuration) {
        AsyncTask.execute(()-> {
            iterateThroughStatDurationModeVariables(directionOfIteratingDuration);
            setDayAndStatListsForChosenDurationOfDays(currentStatDurationMode);

            getActivity().runOnUiThread(()-> {
                setStatDurationTextView(currentStatDurationMode);
                populateListsAndTextViewsFromEntityListsInDatabase();

            });
        });
    }

    public void iterateThroughStatDurationModeVariables(int directionOfIteration) {
        if (directionOfIteration==ITERATING_STATS_UP) {
            if (currentStatDurationMode<3) {
                currentStatDurationMode++;
            } else {
                currentStatDurationMode=0;
            }
        } else if (directionOfIteration==ITERATING_STATS_DOWN) {
            if (currentStatDurationMode>0) {
                currentStatDurationMode--;
            } else {
                currentStatDurationMode=3;
            }
        }
    }

    private void setDayAndStatListsForChosenDurationOfDays(int mode) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        if (mode==DAILY_STATS) {
            dailyStatsAccess.setDayHolderAndStatForEachActivityListsForSelectedDay(daySelectedFromCalendar);
        }
        if (mode==WEEKLY_STATS) {
            dailyStatsAccess.setAllDayAndStatListsForWeek(calendar.get(Calendar.DAY_OF_WEEK), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.DAY_OF_YEAR));
        }
        if (mode==MONTHLY_STATS) {
            dailyStatsAccess.setAllDayAndStatListsForMonth((calendar.get(Calendar.DAY_OF_MONTH)), calendar.getActualMaximum(Calendar.DAY_OF_MONTH), calendar.get(Calendar.DAY_OF_YEAR));
        }
        if (mode==YEARLY_STATS) {
            dailyStatsAccess.setAllDayAndStatListsForYear(calendar.getActualMaximum(Calendar.DAY_OF_YEAR), calendar.get(Calendar.DAY_OF_YEAR));
        }
    }

    private void setStatDurationTextView(int mode) {
        if (mode==DAILY_STATS) {
            totalStatsHeaderTextView.setText(R.string.day_total_header);
        }
        if (mode==WEEKLY_STATS) {
            totalStatsHeaderTextView.setText(R.string.weekly_total_header);
        }
        if (mode==MONTHLY_STATS) {
            totalStatsHeaderTextView.setText(R.string.monthly_total_header);
        }
        if (mode==YEARLY_STATS) {
            totalStatsHeaderTextView.setText(R.string.yearly_total_header);
        }
    }

    public void populateListsAndTextViewsFromEntityListsInDatabase() {
        AsyncTask.execute(()-> {
            setDayAndStatListsForChosenDurationOfDays(currentStatDurationMode);

            getActivity().runOnUiThread(()-> {
                setDayHolderStatsTextViews();

                dailyStatsAccess.clearStatsForEachActivityArrayLists();
                dailyStatsAccess.populateStatsForEachActivityArrayLists();
                dailyStatsAdapter.notifyDataSetChanged();
            });
        });
    }

    private void setDayHolderStatsTextViews() {
        String totalSetTime = convertSeconds(dailyStatsAccess.getTotalSetTimeFromDayHolderList());
        String totalBreakTime = convertSeconds(dailyStatsAccess.getTotalBreakTimeFromDayHolderList());
        double totalCaloriesBurned = dailyStatsAccess.getTotalCaloriesBurnedFromDayHolderList();

        statsTotalSetTimeTextView.setText(getString(R.string.daily_stats_string, getString(R.string.daily_set_time), totalSetTime));
        statsTotalBreakTimeTextView.setText(getString(R.string.daily_stats_string, getString(R.string.daily_break_time), totalBreakTime));
        statsTotalCaloriesBurnedTextView.setText(getString(R.string.daily_stats_string, getString(R.string.daily_calories_burned),formatCalorieString(totalCaloriesBurned)));
    }

    public int getDaySelectedFromCalendar() {
        return daySelectedFromCalendar;
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

    private void instantiateTextViewsAndMiscClasses() {
        totalStatsHeaderTextView = mRoot.findViewById(R.id.total_stats_header);
        statsTotalSetTimeTextView = mRoot.findViewById(R.id.daily_stats_total_set_time_textView);
        statsTotalBreakTimeTextView = mRoot.findViewById(R.id.daily_stats_total_break_time_textView);
        statsTotalCaloriesBurnedTextView = mRoot.findViewById(R.id.daily_stats_total_daily_time_and_calories_burned_textView);
        statDurationSwitcherButtonLeft = mRoot.findViewById(R.id.stat_duration_switcher_button_left);
        statDurationSwitcherButtonRight = mRoot.findViewById(R.id.stat_duration_switcher_button_right);
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

    private String formatCalorieString(double calories) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(calories);
    }
}