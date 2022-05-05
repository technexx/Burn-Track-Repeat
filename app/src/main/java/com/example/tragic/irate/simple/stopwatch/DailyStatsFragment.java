package com.example.tragic.irate.simple.stopwatch;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tragic.irate.simple.stopwatch.Adapters.DailyStatsAdapter;
import com.example.tragic.irate.simple.stopwatch.Miscellaneous.LongToStringConverters;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class DailyStatsFragment extends Fragment implements DailyStatsAdapter.tdeeRowIsSelected{

    View mRoot;
    Calendar calendar;
    CalendarView calendarView;
    int daySelectedFromCalendar;
    LongToStringConverters longToStringConverters;

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

    View tdeeEditView;
    View tdeeEditPopUpAnchor;
    PopupWindow tdeeEditPopUpWindow;
    TextView tdeeEditActivityTextView;
    EditText tdeeSetTimeEditText;
    EditText tdeeBreakTimeEditText;
    TextView tdeeEditCaloriesTextView;

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.daily_stats_fragment_layout, container, false);
        mRoot = root;

        calendar = Calendar.getInstance(TimeZone.getDefault());
        calendarView = mRoot.findViewById(R.id.stats_calendar);
        dailyStatsAccess = new DailyStatsAccess(getActivity());
        ImageButton editTdeeStatsButton = mRoot.findViewById(R.id.edit_tdee_stats_button);

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
                    calendar = Calendar.getInstance(TimeZone.getDefault());
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

        editTdeeStatsButton.setOnClickListener(v-> {
            toggleEditModeInStatsAdapter();
        });

        return root;
    }

    private void statDurationSwitchModeLogic(int directionOfIteratingDuration) {
        AsyncTask.execute(()-> {
            iterateThroughStatDurationModeVariables(directionOfIteratingDuration);

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
            dailyStatsAccess.setAllDayAndStatListsForYear(calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
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
                dailyStatsAccess.setTotalActivityStatsForSelectedDaysToLists();
                dailyStatsAdapter.notifyDataSetChanged();
            });
        });
    }

    private void setDayHolderStatsTextViews() {
        String totalSetTime = convertSeconds(dailyStatsAccess.getTotalSetTimeFromDayHolderList());
        String totalBreakTime = convertSeconds(dailyStatsAccess.getTotalBreakTimeFromDayHolderList());
        double totalCaloriesBurned = dailyStatsAccess.getTotalCaloriesBurnedFromDayHolderList();

        statsTotalSetTimeTextView.setText(totalSetTime);
        statsTotalBreakTimeTextView.setText(totalBreakTime);
        statsTotalCaloriesBurnedTextView.setText(formatCalorieString(totalCaloriesBurned));
    }

    public int getDaySelectedFromCalendar() {
        return daySelectedFromCalendar;
    }

    private void toggleEditModeInStatsAdapter() {
        dailyStatsAdapter.toggleEditMode();
    }

    @Override
    public void tdeeRowSelection(int position) {
        if (tdeeEditPopUpWindow.isShowing()) {
            tdeeEditPopUpWindow.dismiss();
        } else {
            tdeeEditPopUpWindow.showAsDropDown(tdeeEditPopUpAnchor, 0, dpConv(-40), Gravity.TOP);
            setTdeeEditValuesToTextViews(position);
        }
    }

    private void setTdeeEditValuesToTextViews(int position) {
        tdeeEditActivityTextView.setText(getActivityStringFromSelectedPosition(position));
        tdeeSetTimeEditText.setText(longToStringConverters.convertSeconds(getActivitySetTimeFromSelectedPosition(position)));
        tdeeBreakTimeEditText.setText(longToStringConverters.convertSeconds(getActivityBreakTimeFromSelectedPosition(position)));
        tdeeEditCaloriesTextView.setText(formatCalorieString(getActivityCaloriesFromSelectedPosition(position)));
    }

    private String getActivityStringFromSelectedPosition(int position) {
        return dailyStatsAccess.totalActivitiesListForSelectedDuration.get(position);
    }

    private long getActivitySetTimeFromSelectedPosition(int position) {
        return dailyStatsAccess.totalSetTimeListForEachActivityForSelectedDuration.get(position);
    }

    private long getActivityBreakTimeFromSelectedPosition(int position) {
        return dailyStatsAccess.totalBreakTimeListForEachActivityForSelectedDuration.get(position);
    }

    private double getActivityCaloriesFromSelectedPosition(int position) {
        return dailyStatsAccess.totalCaloriesBurnedListForEachActivityForSelectedDuration.get(position);
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
        longToStringConverters = new LongToStringConverters();

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        tdeeEditView = inflater.inflate(R.layout.tdee_stats_edit_view, null);
        tdeeEditPopUpAnchor = mRoot.findViewById(R.id.tdee_edit_popUp_anchor);
        tdeeEditPopUpWindow = new PopupWindow(tdeeEditView, WindowManager.LayoutParams.MATCH_PARENT, dpConv(140), true);

        totalStatsHeaderTextView = mRoot.findViewById(R.id.total_stats_header);
        statsTotalSetTimeTextView = mRoot.findViewById(R.id.daily_stats_total_set_time_textView);
        statsTotalBreakTimeTextView = mRoot.findViewById(R.id.daily_stats_total_break_time_textView);
        statsTotalCaloriesBurnedTextView = mRoot.findViewById(R.id.daily_stats_total_calories_burned_textView);
        statDurationSwitcherButtonLeft = mRoot.findViewById(R.id.stat_duration_switcher_button_left);
        statDurationSwitcherButtonRight = mRoot.findViewById(R.id.stat_duration_switcher_button_right);

        tdeeEditActivityTextView = tdeeEditView.findViewById(R.id.activity_string_in_edit_popUp);
        tdeeSetTimeEditText = tdeeEditView.findViewById(R.id.set_time_editText);
        tdeeBreakTimeEditText = tdeeEditView.findViewById(R.id.break_time_editText);
        tdeeEditCaloriesTextView = tdeeEditView.findViewById(R.id.calories_string_in_edit_popUp);
    }

    private void instantiateRecyclerViewAndItsAdapter() {
        dailyStatsAdapter = new DailyStatsAdapter(getContext(), dailyStatsAccess.totalActivitiesListForSelectedDuration, dailyStatsAccess.totalSetTimeListForEachActivityForSelectedDuration, dailyStatsAccess.totalBreakTimeListForEachActivityForSelectedDuration, dailyStatsAccess.totalCaloriesBurnedListForEachActivityForSelectedDuration);
        dailyStatsAdapter.getSelectedTdeeRowPosition(DailyStatsFragment.this);

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

    private int dpConv(float pixels) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, pixels, getResources().getDisplayMetrics());
    }
}