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
import androidx.recyclerview.widget.DividerItemDecoration;
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

        //Todo: Test insertion + update + deletion.
        Button testButton = root.findViewById(R.id.test_button);

        calendar = Calendar.getInstance();
        calendarView = mRoot.findViewById(R.id.stats_calendar);
        dailyStatsAccess = new DailyStatsAccess(getActivity());

        instantiateTextViewsAndMiscClasses();
        instantiateRecyclerViewAndItsAdapter();

        //On Fragment launch, queries + displays current day.
        queryDatabaseAndPopulatePojoListsAndUpdateRecyclerView(Calendar.DAY_OF_YEAR);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                calendar = Calendar.getInstance(TimeZone.getDefault());
                calendar.set(year, month, dayOfMonth);
                dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

                queryDatabaseAndPopulatePojoListsAndUpdateRecyclerView(dayOfYear);
            }
        });

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return root;
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

    private void queryDatabaseAndPopulatePojoListsAndUpdateRecyclerView(int dayToPopulate) {
        AsyncTask.execute(()-> {
            dailyStatsAccess.queryTotalTimesAndCaloriesBurnedFromSelectedDay(dayOfYear);
            dailyStatsAccess.queryStatsForEachActivityForSelectedDay(dayToPopulate);

            getActivity().runOnUiThread(()-> {
                dailyStatsAccess.clearArrayListsOfActivitiesAndTheirStats();
                populateDailyTotalTimesAndCaloriesTextViews();
                dailyStatsAccess.populatePojoListsForDailyActivityStatsForSelectedDay();
                dailyStatsAdapter.notifyDataSetChanged();
            });
        });
    }

    private void populateDailyTotalTimesAndCaloriesTextViews() {
        dailyStatsTotalSetTimeTextView.setText(getString(R.string.daily_stats_string, getString(R.string.daily_set_time), String.valueOf(dailyStatsAccess.totalSetTimeForCurrentDayInMillis)));

        dailyStatsTotalBreakTimeTextView.setText(getString(R.string.daily_stats_string, getString(R.string.daily_break_time), String.valueOf(dailyStatsAccess.totalBreakTimeForCurrentDayInMillis)));

        dailyStatsTotalCaloriesBurnedTextView.setText(getString(R.string.daily_stats_string, getString(R.string.daily_calories_burned), String.valueOf(dailyStatsAccess.totalCaloriesBurnedForCurrentDay)));

        Toast.makeText(getContext(), "Day is " + dayOfYear, Toast.LENGTH_SHORT).show();
    }

    private void updateActivitiesAndStatsListsAndAdapter() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

            }
        });
        dailyStatsAdapter.notifyDataSetChanged();
    }

    private void instantiateTextViewsAndMiscClasses() {
        dailyStatsTotalSetTimeTextView = mRoot.findViewById(R.id.daily_stats_total_set_time_textView);
        dailyStatsTotalBreakTimeTextView = mRoot.findViewById(R.id.daily_stats_total_break_time_textView);
        dailyStatsTotalCaloriesBurnedTextView = mRoot.findViewById(R.id.daily_stats_total_calories_burned_textView);
    }
}










/////////////////////////Db Query implementation of all of StatsForEachActivity//////////////////
//    private void instantiateArrayLists() {
//       totalActivitiesListForSelectedDay = new ArrayList<>();
//       totalSetTimeListForEachActivityForSelectedDay = new ArrayList<>();
//       totalBreakTimeListForEachActivityForSelectedDay = new ArrayList<>();
//       totalCaloriesBurnedForEachActivityForSelectedDay = new ArrayList<>();
//    }

//    private void queryEntireStatsForEachActivityEntity() {
//        dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
//        dailyStatsAccess.assignTotalActivitiesAndTheirStatsToEntityClassList();
//
//        for (int i=0; i<dailyStatsAccess.statsForEachActivityList.size(); i++) {
//            if (dailyStatsAccess.statsForEachActivityList.get(i).getUniqueIdTiedToTheSelectedActivity()==dayOfYear) {
//                totalActivitiesListForSelectedDay.add(dailyStatsAccess.statsForEachActivityList.get(i).getActivity());
//                totalSetTimeListForEachActivityForSelectedDay.add(dailyStatsAccess.statsForEachActivityList.get(i).getTotalSetTimeForEachActivity());
//                totalBreakTimeListForEachActivityForSelectedDay.add(dailyStatsAccess.statsForEachActivityList.get(i).getTotalBreakTimeForEachActivity());
//                totalCaloriesBurnedForEachActivityForSelectedDay.add(dailyStatsAccess.statsForEachActivityList.get(i).getTotalCaloriesBurnedForEachActivity());
//            }
//        }
//
//        dailyStatsAdapter = new DailyStatsAdapter(getContext(), totalActivitiesListForSelectedDay, totalSetTimeListForEachActivityForSelectedDay, totalBreakTimeListForEachActivityForSelectedDay, totalCaloriesBurnedForEachActivityForSelectedDay);
//    }
//}