package com.example.tragic.irate.simple.stopwatch.Database.DailyStatClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tragic.irate.simple.stopwatch.Adapters.CaloriesConsumedAdapter;
import com.example.tragic.irate.simple.stopwatch.Adapters.DailyStatsAdapter;
import com.example.tragic.irate.simple.stopwatch.Miscellaneous.CalendarDayDecorator;
import com.example.tragic.irate.simple.stopwatch.Miscellaneous.CalendarDurationSelectedDecorator;
import com.example.tragic.irate.simple.stopwatch.Miscellaneous.LongToStringConverters;

import com.example.tragic.irate.simple.stopwatch.R;
import com.example.tragic.irate.simple.stopwatch.Miscellaneous.TDEEChosenActivitySpinnerValues;
import com.google.android.material.tabs.TabLayout;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnRangeSelectedListener;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class DailyStatsFragment extends Fragment implements DailyStatsAdapter.tdeeEditedItemIsSelected, DailyStatsAdapter.tdeeActivityAddition, CaloriesConsumedAdapter.caloriesConsumedItemSelected, CaloriesConsumedAdapter.caloriesConsumedAddition {

    View mRoot;
    Calendar calendar;
    com.prolificinteractive.materialcalendarview.MaterialCalendarView calendarView;
    CalendarDayDecorator calendarDayDecorator;
    CalendarDurationSelectedDecorator calendarDurationSelectedDecorator;

    int daySelectedFromCalendar;
    CalendarDay daySelectedAsACalendarDayObject;

    TDEEChosenActivitySpinnerValues tdeeChosenActivitySpinnerValues;
    LongToStringConverters longToStringConverters;
    LayoutInflater inflater;

    DailyStatsAccess dailyStatsAccess;

    DailyStatsAdapter dailyStatsAdapter;
    RecyclerView dailyStatsRecyclerView;
    ConstraintLayout.LayoutParams dailyStatsRecyclerViewLayoutParams;

    CaloriesConsumedAdapter caloriesConsumedAdapter;
    RecyclerView caloriesConsumedRecyclerView;
    ConstraintLayout.LayoutParams caloriesConsumedRecyclerViewLayoutParams;

    View topOfRecyclerViewAnchor;
    View recyclerAndTotalStatsDivider;

    TabLayout caloriesComparisonTabLayout;

    TextView totalStatsHeaderTextView;
    ImageButton activityStatsDurationSwitcherButtonLeft;
    ImageButton activityStatsDurationSwitcherButtonRight;
    TextView activityStatsDurationRangeTextView;

    ImageButton dailyStatsExpandedButton;
    ImageButton editTdeeStatsButton;

    ConstraintLayout totalActivityStatsValuesTextViewLayout;
    ConstraintLayout.LayoutParams totalActivityStatsValuesTextViewsLayoutParams;
    TextView dailyStatsTotalSetTimeTextView;
    TextView dailyStatsTotalCaloriesBurnedTextView;

    ConstraintLayout totalFoodStatsValuesTextViewLayout;
    ConstraintLayout.LayoutParams totalFoodStatsValuesTextViewLayoutParams;
    TextView foodStatsTotalCaloriesConsumedTextView;

    ImageButton minimizeCalendarButton;
    boolean calendarIsMinimized;
    Animation slideOutToBottom;
    Animation slideInFromBottom;
    Animation slideOutToBottomNoAlphaChange;
    Animation slideInFromBottomNoAlphaChange;

    int currentStatDurationMode;
    int ITERATING_ACTIVITY_STATS_UP = 0;
    int ITERATING_ACTIVITY_STATS_DOWN = 1;

    int DAILY_STATS = 0;
    int WEEKLY_STATS = 1;
    int MONTHLY_STATS = 2;
    int YEARLY_STATS = 3;
    int CUSTOM_STATS = 4;
    boolean numberOfDaysWithActivitiesHasChanged;

    List<CalendarDay> customCalendarDayList;
    List<DayHolder> dayHolderList;
    List<StatsForEachActivity> statsForEachActivityList;

    View dailyStatsExpandedView ;
    PopupWindow dailyStatsExpandedPopUpWindow;

    View tdeeEditView;
    View popUpAnchorBottom;
    PopupWindow tdeeEditPopUpWindow;
    TextView activityTextViewInEditPopUp;
    EditText tdeeEditTextHours;
    EditText tdeeEditTextMinutes;
    EditText tdeeEditTextSeconds;
    TextView unassignedTimeInEditPopUpTextView;
    Button confirmActivityEditWithinPopUpButton;
    Button confirmActivityDeletionWithinEditPopUpButton;

    int mActivitySortMode;
    int mPositionToEdit;

    PopupWindow tdeeAddPopUpWindow;
    View addTDEEPopUpView;

    Spinner tdee_category_spinner;
    Spinner tdee_sub_category_spinner;
    ArrayAdapter<String> tdeeCategoryAdapter;
    ArrayAdapter<String> tdeeSubCategoryAdapter;

    TextView caloriesBurnedInTdeeAdditionTextView;
    TextView metScoreTextView;
    Button confirmActivityAddition;

    View addFoodView;
    PopupWindow addFoodPopUpWindow;
    EditText typeOfFoodEditText;
    EditText caloriesConsumedEditText;

    Button confirmCaloriesConsumedAdditionWithinPopUpButton;
    Button confirmCaloriesConsumedDeletionWithinPopUpButton;

    TextView totalConsumedCaloriesCompared;
    TextView totalExpendedCaloriesCompared;
    TextView totalExpendedCaloriesComparedBmr;
    TextView totalExpendedCaloriesComparedActivities;
    TextView totalCaloriesDifferenceCompared;

    int ADDING_FOOD = 0;
    int EDITING_FOOD = 1;

    ConstraintLayout caloriesComparedLayout;

    int selectedTdeeCategoryPosition;
    int selectedTdeeSubCategoryPosition;
    double metScore;

    ImageButton exitExpansionImageButton;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dailyStatsExpandedPopUpWindow.isShowing()) {
            dailyStatsExpandedPopUpWindow.dismiss();
        }
    }

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.daily_stats_fragment_layout, container, false);
        mRoot = root;

        dailyStatsAccess = new DailyStatsAccess(getContext());

        dailyStatsExpandedButton = mRoot.findViewById(R.id.daily_stats_expanded_button);
        editTdeeStatsButton = mRoot.findViewById(R.id.edit_tdee_stats_button);

        instantiateCalendarObjects();
        instantiateTextViewsAndMiscClasses();
        instantiateActivityRecyclerViewAndItsAdapter();
        instantiateCalorieConsumptionRecyclerAndItsAdapter();
        instantiateAnimations();

        instantiateActivityEditPopUpViews();
        instantiateAddPopUpViews();
        instantiateActivityAdditionSpinnersAndAdapters();
        setTdeeSpinnerListeners();

        instantiateCaloriesConsumedEditPopUpViews();
        instantiateCaloriesComparedViews();
        instantiateCalorieTabLayoutListenerAndViews();

        instantiateExpansionPopUpViews();
        setValueCappingTextWatcherOnEditTexts();
        setTextWatchersOnActivityEditTexts();

        AsyncTask.execute(()-> {
            daySelectedFromCalendar = aggregateDayIdFromCalendar();
            daySelectedAsACalendarDayObject = CalendarDay.from(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
            customCalendarDayList = Collections.singletonList(daySelectedAsACalendarDayObject);

            populateListsAndTextViewsFromEntityListsInDatabase();
            colorDaysWithAtLeastOneActivity();

            getActivity().runOnUiThread(()-> {
                setStatDurationViews(currentStatDurationMode);
            });
        });

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                AsyncTask.execute(()->{
                    calendar = Calendar.getInstance(Locale.getDefault());
                    calendar.set(date.getYear(), date.getMonth()-1, date.getDay());
                    customCalendarDayList = Collections.singletonList(date);

                    daySelectedFromCalendar = aggregateDayIdFromCalendar();
                    daySelectedAsACalendarDayObject = date;

                    calendarDateChangeLogic();
                    populateListsAndTextViewsFromEntityListsInDatabase();

                    getActivity().runOnUiThread(()-> {
                        setActivityStatsDurationRangeTextView();
                        setSelectionDayIfSelectingSingleDayFromCustomDuration();
                        enableActivityEditButtonIfDisabled();
                    });

                });
                Log.i("testCall", "onDateSelected called!");
            }
        });

        calendarView.setOnRangeSelectedListener(new OnRangeSelectedListener() {
            @Override
            public void onRangeSelected(@NonNull MaterialCalendarView widget, @NonNull List<CalendarDay> dates) {
                AsyncTask.execute(()->{
                    calendar = Calendar.getInstance(Locale.getDefault());
                    customCalendarDayList = dates;
                    daySelectedAsACalendarDayObject = dates.get(0);

                    calendar.set(daySelectedAsACalendarDayObject.getYear(), daySelectedAsACalendarDayObject.getMonth()-1, daySelectedAsACalendarDayObject.getDay());

                    daySelectedFromCalendar = aggregateDayIdFromCalendar();

                    populateListsAndTextViewsFromEntityListsInDatabase();

                    getActivity().runOnUiThread(()-> {
                        disableActivityEditButtonIfMoreThanOneDateSelected(dates);
                        convertAndSetDateRangeStringOnTextView();
                    });
                });
                Log.i("testCall", "onRangeSelected called!");
            }
        });

        dailyStatsExpandedButton.setOnClickListener(v-> {
            dailyStatsExpandedPopUpWindow.showAsDropDown(calendarView);

            calendarMinimizationLogic(true);
        });

        exitExpansionImageButton.setOnClickListener(v-> {
            dailyStatsExpandedPopUpWindow.dismiss();
        });

        activityStatsDurationSwitcherButtonLeft.setOnClickListener(v-> {
            statDurationSwitchModeLogic(ITERATING_ACTIVITY_STATS_DOWN);
        });

        activityStatsDurationSwitcherButtonRight.setOnClickListener(v-> {
            statDurationSwitchModeLogic(ITERATING_ACTIVITY_STATS_UP);
        });

        confirmActivityAddition.setOnClickListener(v-> {
            addActivityToStats();
        });

        editTdeeStatsButton.setOnClickListener(v-> {
            if (dailyStatsRecyclerView.isShown()) {
                dailyStatsAdapter.toggleEditMode();
            }
            if (caloriesConsumedRecyclerView.isShown()) {
                caloriesConsumedAdapter.toggleEditMode();
            }
        });

        confirmActivityEditWithinPopUpButton.setOnClickListener(v-> {
            editActivityStatsInDatabase();
            tdeeEditPopUpWindow.dismiss();
        });

        confirmActivityDeletionWithinEditPopUpButton.setOnClickListener(v-> {
            deleteActivity(mPositionToEdit);
            tdeeEditPopUpWindow.dismiss();
        });

        confirmCaloriesConsumedAdditionWithinPopUpButton.setOnClickListener(v-> {
            addOrEditFoodInStats();
        });

        confirmCaloriesConsumedDeletionWithinPopUpButton.setOnClickListener(v-> {
            deleteFoodInStatsIfInEditMode();
        });

        minimizeCalendarButton.setOnClickListener(v-> {
            calendarMinimizationLogic(false);
        });

        return root;
    }

    private void addOrEditFoodInStats() {
        if (caloriesConsumedAdapter.getAddingOrEditingFoodVariable()==ADDING_FOOD) {
            addFoodToStats();
        }
        if (caloriesConsumedAdapter.getAddingOrEditingFoodVariable()==EDITING_FOOD) {
            updateFoodInStats();
        }
    }

    private void deleteFoodInStatsIfInEditMode() {
        if (caloriesConsumedAdapter.getAddingOrEditingFoodVariable()==EDITING_FOOD) {
            deleteConsumedCalories(mPositionToEdit);
        }
    }

    private void setActivityStatsDurationRangeTextView() {
        if (currentStatDurationMode==DAILY_STATS) {
            setSingleDateStringOnTextView();
        } else {
            convertAndSetDateRangeStringOnTextView();
        }
    }

    private void calendarDateChangeLogic() {
        dailyStatsAdapter.turnOffEditMode();
        dailyStatsAdapter.getItemCount();

        caloriesConsumedAdapter.turnOffEditMode();
        caloriesConsumedAdapter.getItemCount();
    }

    private void disableActivityEditButtonIfMoreThanOneDateSelected(List<CalendarDay> calendarDayList) {
        if (calendarDayList.size()>1) {
            toggleEditStatsButton(false);
        }
    }

    private void enableActivityEditButtonIfDisabled() {
        if (!editTdeeStatsButton.isEnabled()) {
            toggleEditStatsButton(true);
        }
    }

    private void setSelectionDayIfSelectingSingleDayFromCustomDuration() {
        if (currentStatDurationMode==CUSTOM_STATS) {
            calendarView.setSelectedDate(daySelectedAsACalendarDayObject);
        }
    }

    public void populateListsAndTextViewsFromEntityListsInDatabase() {
        setDayAndStatsForEachActivityEntityListsForChosenDurationOfDays(currentStatDurationMode);

        getActivity().runOnUiThread(()-> {
            dailyStatsAccess.clearStatsForEachActivityArrayLists();

            dailyStatsAccess.setTotalActivityStatsForSelectedDaysToArrayLists();
            dailyStatsAccess.setTotalSetTimeVariableForDayHolder();
            dailyStatsAccess.setTotalCaloriesVariableForDayHolder();
            dailyStatsAdapter.notifyDataSetChanged();

            dailyStatsAccess.setTotalCaloriesConsumedStatsForSelectedDayToArrayLists();
            caloriesConsumedAdapter.notifyDataSetChanged();

            setTotalActivityStatsFooterTextViews();

            dailyStatsAccess.setAggregateDailyTime();
            dailyStatsAccess.setUnassignedTotalCalories();

            dailyStatsAccess.setAggregateDailyCalories();
            dailyStatsAccess.setUnassignedDailyTotalTime();

            setTotalCaloriesConsumedFooterTextViews();

            setTotalCaloriesComparedTextViews();
        });
    }

    private void setDayAndStatsForEachActivityEntityListsForChosenDurationOfDays(int mode) {
        if (mode==DAILY_STATS) {
            dailyStatsAccess.setDayHolderAndStatForEachActivityListsForSelectedDayFromDatabase(daySelectedFromCalendar);
        }
        if (mode==WEEKLY_STATS) {
            dailyStatsAccess.setAllDayAndStatListsForWeek(calendar.get(Calendar.DAY_OF_WEEK), calendar.get(Calendar.DAY_OF_YEAR));
        }
        if (mode==MONTHLY_STATS) {
            dailyStatsAccess.setAllDayAndStatListsForMonth((calendar.get(Calendar.DAY_OF_MONTH)), calendar.getActualMaximum(Calendar.DAY_OF_MONTH), calendar.get(Calendar.DAY_OF_YEAR));
        }
        if (mode==YEARLY_STATS) {
            dailyStatsAccess.setAllDayAndStatListsForYearFromDatabase(calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
        }
        if (mode==CUSTOM_STATS) {
            dailyStatsAccess.setAllDayAndStatListsForCustomDatesFromDatabase(customCalendarDayList, calendar.get(Calendar.DAY_OF_YEAR));
        }


        if (numberOfDaysWithActivitiesHasChanged) {
            colorDaysWithAtLeastOneActivity();
            numberOfDaysWithActivitiesHasChanged = false;
            Log.i("testchange", "called!");
        }

        setListsOfDayHolderAndStatsPrimaryIds();
    }

    public void setActivitySortMode(int sortMode) {
        this.mActivitySortMode = sortMode;
    }

    public void sortStatsAsACallFromMainActivity() {
        dailyStatsAccess.setActivitySortMode(mActivitySortMode);
        populateListsAndTextViewsFromEntityListsInDatabase();
    }

    private void setListsOfDayHolderAndStatsPrimaryIds() {
        dayHolderList = dailyStatsAccess.getDayHolderList();
        statsForEachActivityList = dailyStatsAccess.getStatsForEachActivityList();
    }

    public List<DayHolder> getDayHolderList() {
        return dayHolderList;
    }

    public List<StatsForEachActivity> getStatsForEachActivityList() {
        return statsForEachActivityList;
    }

    private void statDurationSwitchModeLogic(int directionOfIteratingDuration) {
        AsyncTask.execute(()-> {
            iterateThroughStatDurationModeVariables(directionOfIteratingDuration);
            populateListsAndTextViewsFromEntityListsInDatabase();

            getActivity().runOnUiThread(()-> {
                setStatDurationViews(currentStatDurationMode);

                dailyStatsAdapter.turnOffEditMode();
                dailyStatsAdapter.getItemCount();

                caloriesConsumedAdapter.turnOffEditMode();
                caloriesConsumedAdapter.getItemCount();
            });
        });
    }

    public void iterateThroughStatDurationModeVariables(int directionOfIteration) {
        if (directionOfIteration==ITERATING_ACTIVITY_STATS_UP) {
            if (currentStatDurationMode<4) {
                currentStatDurationMode++;
            } else {
                currentStatDurationMode=0;
            }
        } else if (directionOfIteration==ITERATING_ACTIVITY_STATS_DOWN) {
            if (currentStatDurationMode>0) {
                currentStatDurationMode--;
            } else {
                currentStatDurationMode=4;
            }
        }
    }

    private void setStatDurationViews(int mode) {
        toggleEditStatsButton(true);
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);

        if (mode==DAILY_STATS) {
            totalStatsHeaderTextView.setText(R.string.day_total_header);
            setSingleDateStringOnTextView();
        }
        if (mode==WEEKLY_STATS) {
            totalStatsHeaderTextView.setText(R.string.weekly_total_header);
            convertAndSetDateRangeStringOnTextView();
        }
        if (mode==MONTHLY_STATS) {
            totalStatsHeaderTextView.setText(R.string.monthly_total_header);
            convertAndSetDateRangeStringOnTextView();
        }
        if (mode==YEARLY_STATS) {
            totalStatsHeaderTextView.setText(R.string.yearly_total_header);
            convertAndSetDateRangeStringOnTextView();
        }
        if (mode==CUSTOM_STATS) {
            totalStatsHeaderTextView.setText(R.string.custom_total_header);
            calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE);
            convertAndSetDateRangeStringOnTextView();
        }

        calendarView.setSelectedDate(daySelectedAsACalendarDayObject);
    }

    private void setSingleDateStringOnTextView() {
        String dayToSet = dailyStatsAccess.getSingleDayAsString();
        activityStatsDurationRangeTextView.setText(dayToSet);
    }

    private void convertAndSetDateRangeStringOnTextView() {
        String firstDay = dailyStatsAccess.getFirstDayInDurationAsString();
        String lastDay = String.valueOf(dailyStatsAccess.getLastDayInDurationAsString());
        activityStatsDurationRangeTextView.setText(getString(R.string.date_duration_textView, firstDay, lastDay));
    }

    private void setTotalActivityStatsFooterTextViews() {
        String totalSetTime = longToStringConverters.convertSecondsForStatDisplay(dailyStatsAccess.getTotalSetTimeFromDayHolderList());
        double totalCaloriesBurned = dailyStatsAccess.getTotalCaloriesBurnedFromDayHolderList();

        dailyStatsTotalSetTimeTextView.setText(totalSetTime);
        dailyStatsTotalCaloriesBurnedTextView.setText(formatCalorieStringWithoutDecimals(totalCaloriesBurned));
    }

    private void toggleEditStatsButton(boolean buttonIsEnabled) {
        if (buttonIsEnabled) {
            editTdeeStatsButton.setEnabled(true);
            editTdeeStatsButton.setAlpha(1.0f);
        } else {
            editTdeeStatsButton.setEnabled(false);
            editTdeeStatsButton.setAlpha(0.3f);
        }
    }

    private int aggregateDayIdFromCalendar() {
        int currentDay = calendar.get(Calendar.DAY_OF_YEAR);
        int year = calendar.get(Calendar.YEAR);
        int additionModifier = (year - 2022) * 365;
        return currentDay + additionModifier;
    }

    public void colorDaysWithAtLeastOneActivity() {
        List<Integer> daysWithAtLeastOneActivityList = dailyStatsAccess.getListOfDaysWithAtLeastOneActivity();
        List<CalendarDay> calendarDayList = new ArrayList<>();
        Calendar calendarColoringObject = Calendar.getInstance(Locale.getDefault());

        for (int i=0; i<daysWithAtLeastOneActivityList.size(); i++) {
            calendarColoringObject.set(Calendar.DAY_OF_YEAR, daysWithAtLeastOneActivityList.get(i));

            calendarDayList.add(CalendarDay.from(calendarColoringObject.get(Calendar.YEAR), calendarColoringObject.get(Calendar.MONTH) + 1, calendarColoringObject.get(Calendar.DAY_OF_MONTH)));
        }

        getActivity().runOnUiThread(()->{
            calendarDayDecorator.setCalendarDayList(calendarDayList);
            calendarView.addDecorator(calendarDayDecorator);
        });
    }

    @Override
    public void onAddingActivity(int position) {
        tdeeAddPopUpWindow.showAsDropDown(topOfRecyclerViewAnchor, 0, dpToPxConv(0), Gravity.TOP);
    }

    private void addActivityToStats() {
        numberOfDaysWithActivitiesHasChanged = true;

        AsyncTask.execute(()-> {
            String activityToAdd = tdeeChosenActivitySpinnerValues.subCategoryListOfStringArrays.get(selectedTdeeCategoryPosition)[selectedTdeeSubCategoryPosition];

            dailyStatsAccess.setLocalActivityStringVariable(activityToAdd);
            dailyStatsAccess.setLocalMetScoreVariable(retrieveMetScoreFromSubCategoryPosition());
            dailyStatsAccess.checkIfActivityExistsForSpecificDayAndSetBooleanForIt();
            dailyStatsAccess.setActivityPositionInListForCurrentDay();

            if (!dailyStatsAccess.getDoesActivityExistsInDatabaseForSelectedDay()) {
                dailyStatsAccess.insertTotalTimesAndCaloriesForEachActivityWithinASpecificDay(daySelectedFromCalendar);
                dailyStatsAccess.insertTotalTimesAndCaloriesBurnedOfCurrentDayIntoDatabase(daySelectedFromCalendar);

                populateListsAndTextViewsFromEntityListsInDatabase();

                mPositionToEdit = dailyStatsAccess.getStatsForEachActivityList().size()-1;
                Log.i("testAdd", "position to edit is " + mPositionToEdit);

                getActivity().runOnUiThread(()-> {
                    dailyStatsAdapter.notifyDataSetChanged();
                    populateActivityEditPopUpWithNewRow();
                });
            } else {
                getActivity().runOnUiThread(()->{
                    Toast.makeText(getContext(), "Activity exists!", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void populateActivityEditPopUpWithNewRow() {
        replaceActivityAddPopUpWithEditPopUp();

        String activityToAdd = tdeeChosenActivitySpinnerValues.subCategoryListOfStringArrays.get(selectedTdeeCategoryPosition)[selectedTdeeSubCategoryPosition];

        activityTextViewInEditPopUp.setText(activityToAdd);
        zeroOutActivityEditPopUpEditTexts();
    }

    private void replaceActivityAddPopUpWithEditPopUp() {
        tdeeAddPopUpWindow.dismiss();

        setActivityEditPopUpTimeRemainingTextView();
        tdeeEditTextHours.requestFocus();
        tdeeEditPopUpWindow.showAsDropDown(topOfRecyclerViewAnchor, 0, dpToPxConv(0), Gravity.TOP);
    }

    @Override
    public void activityEditItemSelected(int position) {
        this.mPositionToEdit = position;
        launchActivityEditPopUpWithEditTextValuesSet(position);
    }

    private void launchActivityEditPopUpWithEditTextValuesSet(int position) {
        String activityString = dailyStatsAccess.getTotalActivitiesListForSelectedDuration().get(position);
        long timeToEditLongValue = dailyStatsAccess.getTotalSetTimeListForEachActivityForSelectedDuration().get(position);

        activityTextViewInEditPopUp.setText(activityString);
        setActivityEditPopUpEditTexts(timeToEditLongValue);
        setActivityEditPopUpTimeRemainingTextView();

        tdeeEditTextHours.requestFocus();
        tdeeEditPopUpWindow.showAsDropDown(topOfRecyclerViewAnchor, 0, dpToPxConv(0), Gravity.TOP);
    }

    private void setActivityEditPopUpTimeRemainingTextView() {
        String timeLeftInDay = longToStringConverters.convertSecondsForStatDisplay(dailyStatsAccess.getUnassignedDailyTotalTime());
        String timeLeftInDayConcatString = getString(R.string.day_time_remaining, timeLeftInDay);
        unassignedTimeInEditPopUpTextView.setText(timeLeftInDayConcatString);
    }

    private void editActivityStatsInDatabase() {
        AsyncTask.execute(()-> {
            dailyStatsAccess.assignDayHolderInstanceForSelectedDay(daySelectedFromCalendar);
            dailyStatsAccess.assignStatsForEachActivityEntityForSinglePosition(mPositionToEdit);

            long newActivityTime = getMillisValueToSaveFromEditTextString();
            long remainingDailyTime = dailyStatsAccess.getUnassignedDailyTotalTime();
            long timeInEditedRow = dailyStatsAccess.getTotalSetTimeListForEachActivityForSelectedDuration().get(mPositionToEdit);

            remainingDailyTime += timeInEditedRow;
            newActivityTime = cappedActivityTimeInMillis(newActivityTime, remainingDailyTime);

            double retrievedMetScore = dailyStatsAccess.getMetScoreForSelectedActivity();
            double caloriesBurnedPerSecond = calculateCaloriesBurnedPerSecond(retrievedMetScore);
            double newCaloriesForActivity = ((double) (newActivityTime/1000) * caloriesBurnedPerSecond);
            newCaloriesForActivity = roundDownDoubleValuesToSyncCalories(newCaloriesForActivity);

            dailyStatsAccess.setTotalSetTimeForSelectedActivity(newActivityTime);
            dailyStatsAccess.setTotalCaloriesBurnedForSelectedActivity(newCaloriesForActivity);
            dailyStatsAccess.updateTotalTimesAndCaloriesBurnedForSpecificActivityOnSpecificDayRunnable();

            dailyStatsAccess.setDayHolderAndStatForEachActivityListsForSelectedDayFromDatabase(daySelectedFromCalendar);
            dailyStatsAccess.setTotalActivityStatsForSelectedDaysToArrayLists();
            dailyStatsAccess.setTotalSetTimeVariableForDayHolder();
            dailyStatsAccess.setTotalCaloriesVariableForDayHolder();

            dailyStatsAccess.setTotalSetTimeFromDayHolderEntity(dailyStatsAccess.getTotalSetTimeVariableForDayHolder());
            dailyStatsAccess.setTotalCaloriesBurnedFromDayHolderEntity(dailyStatsAccess.getTotalCaloriesVariableForDayHolder());
            dailyStatsAccess.updateTotalTimesAndCaloriesBurnedForCurrentDayFromDatabase();

            populateListsAndTextViewsFromEntityListsInDatabase();

            getActivity().runOnUiThread(()-> {
                Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private long cappedActivityTimeInMillis(long activityTime, long remainingTime) {
        if (activityTime>remainingTime) {
            activityTime = remainingTime;
        }
        return activityTime;
    }

    private void deleteActivity(int position) {
        numberOfDaysWithActivitiesHasChanged = true;

        AsyncTask.execute(() -> {
            dailyStatsAccess.assignDayHolderInstanceForSelectedDay(daySelectedFromCalendar);
            dailyStatsAccess.assignStatsForEachActivityEntityForSinglePosition(position);
            dailyStatsAccess.deleteStatsForEachActivityEntity();

            dailyStatsAccess.setDayHolderAndStatForEachActivityListsForSelectedDayFromDatabase(daySelectedFromCalendar);
            dailyStatsAccess.setTotalActivityStatsForSelectedDaysToArrayLists();
            dailyStatsAccess.setTotalSetTimeVariableForDayHolder();
            dailyStatsAccess.setTotalCaloriesVariableForDayHolder();

            dailyStatsAccess.setTotalActivityStatsForSelectedDaysToArrayLists();
            dailyStatsAccess.setTotalSetTimeFromDayHolderEntity(dailyStatsAccess.getTotalSetTimeVariableForDayHolder());
            dailyStatsAccess.setTotalCaloriesBurnedFromDayHolderEntity(dailyStatsAccess.getTotalCaloriesVariableForDayHolder());

            populateListsAndTextViewsFromEntityListsInDatabase();

            getActivity().runOnUiThread(()-> {
                Toast.makeText(getContext(), "Deleted!", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void zeroOutActivityEditPopUpEditTexts() {
        tdeeEditTextHours.setText("");
        tdeeEditTextMinutes.setText("");
        tdeeEditTextSeconds.setText("");
    }

    private void setActivityEditPopUpEditTexts(long valueToSet) {
        String stringToSet = longToStringConverters.convertSecondsForEditPopUp(valueToSet);
        String[] splitString = stringToSet.split(":");

        if (splitString.length==3) {
            tdeeEditTextHours.setText(splitString[0]);
            tdeeEditTextMinutes.setText(splitString[1]);
            tdeeEditTextSeconds.setText(splitString[2]);
        } else {
            tdeeEditTextHours.setText(R.string.double_zeros);
            tdeeEditTextMinutes.setText(splitString[0]);
            tdeeEditTextSeconds.setText(splitString[1]);
        }

        //Todo: Should be set to setEditTextsToZeroIfEmpty() instead if being edited, not added.
        setEditTextsToZeroIfEmpty(tdeeEditTextHours);
        setEditTextsToZeroIfEmpty(tdeeEditTextMinutes);
        setEditTextsToZeroIfEmpty(tdeeEditTextSeconds);
    }

    private void setEditTextsToEmptyIfAtZero(EditText editText) {
        if (editText.getText().toString().equals("00")) {
            editText.setText("");
        }
    }

    private void setEditTextsToZeroIfEmpty(EditText editText) {
        if (editText.getText().toString().equals("")) {
            editText.setText("00");
        }
    }

    private long getMillisValueToSaveFromEditTextString() {
        long hours = setLongValueOfEditTextStrings(tdeeEditTextHours);
        long minutes = setLongValueOfEditTextStrings(tdeeEditTextMinutes);
        long seconds = setLongValueOfEditTextStrings(tdeeEditTextSeconds);

        if (seconds >= 60) {
            seconds = seconds % 60;
            minutes +=1;
        }

        if (minutes >= 60) {
            minutes = minutes %60;
            hours +=1;
        }

        long totalSeconds = ((hours*3600) + (minutes*60) + seconds) * 1000;
        return totalSeconds;
    }

    private long setLongValueOfEditTextStrings(EditText editText) {
        long valueToReturn;

        if (!editText.getText().toString().equals("")) {
            valueToReturn = Long.parseLong(editText.getText().toString());
        } else {
            valueToReturn = 0;
        }

        return valueToReturn;
    }

    private void setValueCappingTextWatcherOnEditTexts() {
        tdeeEditTextMinutes.addTextChangedListener(editTextWatcher(tdeeEditTextMinutes));
        tdeeEditTextSeconds.addTextChangedListener(editTextWatcher(tdeeEditTextSeconds));
    }

    private TextWatcher editTextWatcher(EditText editText) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!editText.getText().toString().equals("")) {
                    if (Long.parseLong(editText.getText().toString()) >=60) {
                        editText.setText("59");
                    }
                }
            }
        };
    }

    private void setTextWatchersOnActivityEditTexts() {
        tdeeEditTextHours.addTextChangedListener(alphaChangeIfActivityEditTextValueIsZeroTextWatcher(tdeeEditTextHours));
        tdeeEditTextMinutes.addTextChangedListener(alphaChangeIfActivityEditTextValueIsZeroTextWatcher(tdeeEditTextMinutes));
        tdeeEditTextSeconds.addTextChangedListener(alphaChangeIfActivityEditTextValueIsZeroTextWatcher(tdeeEditTextSeconds));
    }

    private TextWatcher alphaChangeIfActivityEditTextValueIsZeroTextWatcher(EditText editText) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editText.getText().toString().equals("")) {
                    editText.setAlpha(0.3f);
                } else {
                    editText.setAlpha(1.0f);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }

    private void setTdeeSpinnerListeners() {
        tdee_category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tdeeCategorySpinnerTouchActions();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        tdee_sub_category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tdeeSubCategorySpinnerTouchActions();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void tdeeCategorySpinnerTouchActions() {
        selectedTdeeCategoryPosition = tdee_category_spinner.getSelectedItemPosition();

        tdeeSubCategoryAdapter.clear();
        tdeeSubCategoryAdapter.addAll(tdeeChosenActivitySpinnerValues.subCategoryListOfStringArrays.get(selectedTdeeCategoryPosition));

        tdee_sub_category_spinner.setSelection(0);
        selectedTdeeSubCategoryPosition = 0;

        setMetScoreTextViewInAddTdeePopUp();
        setThirdMainTextViewInAddTdeePopUp();
    }

    private void tdeeSubCategorySpinnerTouchActions() {
        selectedTdeeCategoryPosition = tdee_category_spinner.getSelectedItemPosition();
        selectedTdeeSubCategoryPosition = tdee_sub_category_spinner.getSelectedItemPosition();

        setMetScoreTextViewInAddTdeePopUp();
        setThirdMainTextViewInAddTdeePopUp();
    }

    private void setMetScoreTextViewInAddTdeePopUp() {
        metScore = retrieveMetScoreFromSubCategoryPosition();
        metScoreTextView.setText(getString(R.string.met_score_single_line, String.valueOf(metScore)));
    }

    private double retrieveMetScoreFromSubCategoryPosition() {
        String[] valueArray = tdeeChosenActivitySpinnerValues.subValueListOfStringArrays.get(selectedTdeeCategoryPosition);
        double preRoundedMet = Double.parseDouble(valueArray[selectedTdeeSubCategoryPosition]);
        return preRoundedMet;
    }

    private void setThirdMainTextViewInAddTdeePopUp() {
        String caloriesBurnedPerMinute = formatCalorieString(calculateCaloriesBurnedPerMinute(metScore));
        String caloriesBurnedPerHour = formatCalorieString(calculateCaloriesBurnedPerMinute(metScore) * 60);

        caloriesBurnedInTdeeAdditionTextView.setText(getString(R.string.two_line_concat, getString(R.string.calories_burned_per_minute, caloriesBurnedPerMinute), getString(R.string.calories_burned_per_hour, caloriesBurnedPerHour)));
    }

    @Override
    public void onAddingFood(int position) {
        typeOfFoodEditText.requestFocus();
        addFoodPopUpWindow.showAsDropDown(topOfRecyclerViewAnchor, 0, dpToPxConv(0), Gravity.TOP);
    }

    private void addFoodToStats() {
        if (getFoodStringFromEditText().isEmpty()) {
            Toast.makeText(getContext(), "Must enter a food!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (getCaloriesForFoodItemFromEditText().isEmpty()) {
            Toast.makeText(getContext(), "Must enter a caloric value!", Toast.LENGTH_SHORT).show();
            return;
        }
            AsyncTask.execute(()-> {
            dailyStatsAccess.setFoodString(getFoodStringFromEditText());
            dailyStatsAccess.setCaloriesInFoodItem(Double.parseDouble(getCaloriesForFoodItemFromEditText()));

            dailyStatsAccess.insertCaloriesAndEachFoodIntoDatabase(daySelectedFromCalendar);
            populateListsAndTextViewsFromEntityListsInDatabase();

            getActivity().runOnUiThread(()-> {
                caloriesConsumedAdapter.notifyDataSetChanged();
                addFoodPopUpWindow.dismiss();
            });
        });
    }

    private String getFoodStringFromEditText() {
        return typeOfFoodEditText.getText().toString();
    }

    private String getCaloriesForFoodItemFromEditText() {
        return caloriesConsumedEditText.getText().toString();
    }

    @Override
    public void calorieRowIsSelected(int position) {
        this.mPositionToEdit = position;
        launchFoodEditPopUpWithEditTextValuesSet(position);
    }

    private void launchFoodEditPopUpWithEditTextValuesSet(int position) {
        String foodString = dailyStatsAccess.getTotalFoodStringListForSelectedDuration().get(position);
        double caloriesInFood = dailyStatsAccess.getTotalCaloriesConsumedListForSelectedDuration().get(position);
        String caloriesAsString = formatCalorieStringWithoutDecimals(caloriesInFood);

        typeOfFoodEditText.setText(foodString);
        caloriesConsumedEditText.setText(caloriesAsString);

        typeOfFoodEditText.requestFocus();
        addFoodPopUpWindow.showAsDropDown(topOfRecyclerViewAnchor, 0, dpToPxConv(0), Gravity.TOP);
    }

    private void updateFoodInStats() {
        AsyncTask.execute(()-> {
            dailyStatsAccess.assignCaloriesForEachFoodItemEntityForSinglePosition(mPositionToEdit);

            dailyStatsAccess.setFoodString(getFoodStringFromEditText());
            dailyStatsAccess.setCaloriesInFoodItem(Double.parseDouble(getCaloriesForFoodItemFromEditText()));
            dailyStatsAccess.updateCaloriesAndEachFoodInDatabase();

            populateListsAndTextViewsFromEntityListsInDatabase();

            getActivity().runOnUiThread(()-> {
                Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void deleteConsumedCalories(int position) {
        AsyncTask.execute(()-> {
            dailyStatsAccess.assignCaloriesForEachFoodItemEntityForSinglePosition(mPositionToEdit);
            dailyStatsAccess.deleteCaloriesAndEachFoodInDatabase();
            populateListsAndTextViewsFromEntityListsInDatabase();

            getActivity().runOnUiThread(()-> {
                Toast.makeText(getContext(), "Deleted!", Toast.LENGTH_SHORT).show();
                addFoodPopUpWindow.dismiss();
            });
        });
    }

    private void setTotalCaloriesConsumedFooterTextViews() {
        foodStatsTotalCaloriesConsumedTextView.setText(formatCalorieStringWithoutDecimals(dailyStatsAccess.getTotalCaloriesConsumedForSelectedDuration()));
    }

    private void setTotalCaloriesComparedTextViews() {
        double caloriesConsumed = dailyStatsAccess.getTotalCaloriesConsumedForSelectedDuration();
        double caloriesExpendedFromActivities = dailyStatsAccess.getTotalCaloriesBurnedFromDayHolderList();
        double caloriesExpendedFromBmr = dailyStatsAccess.getUnassignedDailyCalories();
        double totalCaloriesExpended = caloriesExpendedFromActivities + caloriesExpendedFromBmr;
        double caloriesDifference = Math.abs(totalCaloriesExpended - caloriesConsumed);

        totalConsumedCaloriesCompared.setText(formatCalorieStringWithoutDecimals(dailyStatsAccess.getTotalCaloriesConsumedForSelectedDuration()));

        totalExpendedCaloriesCompared.setText(formatCalorieStringWithoutDecimals(totalCaloriesExpended));
        totalExpendedCaloriesComparedBmr.setText(formatCalorieStringWithoutDecimals(dailyStatsAccess.getUnassignedDailyCalories()));
        totalExpendedCaloriesComparedActivities.setText(formatCalorieStringWithoutDecimals(caloriesExpendedFromActivities));

        String signToUse = getPlusOrMinusSignForDoubleDifference(caloriesConsumed, totalCaloriesExpended);
        int colorToUse = getTextColorForDoubleDifference(signToUse);

        totalCaloriesDifferenceCompared.setText(getString(R.string.double_placeholder, signToUse, formatCalorieStringWithoutDecimals(caloriesDifference)));
        totalCaloriesDifferenceCompared.setTextColor(ContextCompat.getColor(getContext(), colorToUse));
    }

    private String getPlusOrMinusSignForDoubleDifference(double firstValue, double secondValue) {
        if (firstValue > secondValue) {
            return "+";
        } else if (firstValue < secondValue) {
            return "-";
        } else {
            return "";
        }
    }

    private int getTextColorForDoubleDifference(String plusOrMinus) {
        if (plusOrMinus.equals("+")) {
            return R.color.greyed_red;
        } else {
            return R.color.green;
        }
    }

    private String formatCalorieString(double calories) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(calories);
    }

    private String formatCalorieStringWithoutDecimals(double calories) {
        DecimalFormat df = new DecimalFormat("#");
        df.setRoundingMode(RoundingMode.DOWN);
        return df.format(calories);
    }

    private double calculateCaloriesBurnedPerMinute(double metValue) {
        SharedPreferences sp = getContext().getSharedPreferences("pref", 0);
        boolean metricMode = sp.getBoolean("metricMode", false);
        int userWeight = sp.getInt("tdeeWeight,", 150);

        double weightConversion = userWeight;
        if (!metricMode) weightConversion = weightConversion / 2.205;
        double caloriesBurnedPerMinute = (metValue * 3.5 * weightConversion) / 200;
        return caloriesBurnedPerMinute;
    }

    private double calculateCaloriesBurnedPerSecond(double metValue) {
        return calculateCaloriesBurnedPerMinute(metValue) / 60;
    }

    private double roundDownDoubleValuesToSyncCalories(double caloriesToRound) {
        caloriesToRound += 1;
        DecimalFormat df = new DecimalFormat("#");
        String truncatedCalorieString = df.format(caloriesToRound);

        return Double.parseDouble(truncatedCalorieString);
    }

    private void calendarMinimizationLogic(boolean restoreOnly) {
        if (restoreOnly) {
            if (!calendarIsMinimized) {
                return;
            }
        }

        toggleCalendarMinimizationState();
        toggleCalendarMinimizationLayouts();
    }

    private void toggleCalendarMinimizationState() {
        calendarIsMinimized = !calendarIsMinimized;
    }

    private void toggleCalendarMinimizationLayouts() {
        if (caloriesComparisonTabLayout.getSelectedTabPosition()==0) {
            setCalendarMinimizationLayoutParams(dailyStatsRecyclerViewLayoutParams, totalActivityStatsValuesTextViewsLayoutParams);

            dailyStatsRecyclerView.setLayoutParams(dailyStatsRecyclerViewLayoutParams);
            totalActivityStatsValuesTextViewLayout.setLayoutParams(totalActivityStatsValuesTextViewsLayoutParams);
            totalFoodStatsValuesTextViewLayout.setVisibility(View.GONE);

            setCalendarMinimizationAnimations(totalActivityStatsValuesTextViewLayout);
        }

        if (caloriesComparisonTabLayout.getSelectedTabPosition()==1) {
            setCalendarMinimizationLayoutParams(caloriesConsumedRecyclerViewLayoutParams, totalFoodStatsValuesTextViewLayoutParams);

            caloriesConsumedRecyclerView.setLayoutParams(caloriesConsumedRecyclerViewLayoutParams);
            totalFoodStatsValuesTextViewLayout.setLayoutParams(totalFoodStatsValuesTextViewLayoutParams);
            totalActivityStatsValuesTextViewLayout.setVisibility(View.GONE);

            setCalendarMinimizationAnimations(totalFoodStatsValuesTextViewLayout);
        }
    }

    private void setCalendarMinimizationAnimations(ConstraintLayout layout) {
        if (!calendarIsMinimized) {
            layout.startAnimation(slideInFromBottomNoAlphaChange);
        } else {
            layout.startAnimation(slideOutToBottomNoAlphaChange);
        }
    }

    private void setCalendarMinimizationLayoutParams(ConstraintLayout.LayoutParams recyclerParams, ConstraintLayout.LayoutParams textViewParams) {
        if (!calendarIsMinimized) {
            minimizeCalendarButton.setImageResource(R.drawable.arrow_down_2);
            calendarView.startAnimation(slideInFromBottom);

            recyclerParams.height = dpToPxConv(280);
            recyclerParams.bottomToBottom = ConstraintLayout.LayoutParams.UNSET;

            textViewParams.bottomToTop = ConstraintLayout.LayoutParams.UNSET;
            textViewParams.bottomToBottom = ConstraintLayout.LayoutParams.UNSET;

            textViewParams.bottomToTop = R.id.stats_calendar;
        } else {
            minimizeCalendarButton.setImageResource(R.drawable.arrow_up_2);
            calendarView.startAnimation(slideOutToBottom);

            recyclerParams.height = 0;
            recyclerParams.bottomToBottom = R.id.daily_stats_fragment_parent_layout;

            textViewParams.bottomToTop = ConstraintLayout.LayoutParams.UNSET;
            textViewParams.bottomToBottom = ConstraintLayout.LayoutParams.UNSET;

            textViewParams.bottomToTop = R.id.minimize_calendarView_button;
        }
    }
    private void instantiateCalendarObjects() {
        calendar = Calendar.getInstance(Locale.getDefault());
        calendarView = mRoot.findViewById(R.id.stats_calendar);

        CalendarDay calendarDay = CalendarDay.from(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));

        calendarView.state().edit()
                .setMinimumDate(CalendarDay.from(2022, 1, 1))
                .setMaximumDate(calendarDay)
                .commit();
    }

    private void instantiateAddPopUpViews() {
        addTDEEPopUpView = inflater.inflate(R.layout.add_tdee_popup_for_stats_fragment, null);
        tdeeAddPopUpWindow = new PopupWindow(addTDEEPopUpView, WindowManager.LayoutParams.MATCH_PARENT, dpToPxConv(270), true);
        tdeeAddPopUpWindow.setAnimationStyle(R.style.SlideFromLeftAnimationShort);

        tdee_category_spinner = addTDEEPopUpView.findViewById(R.id.activity_category_spinner);
        tdee_sub_category_spinner = addTDEEPopUpView.findViewById(R.id.activity_sub_category_spinner);
        confirmActivityAddition = addTDEEPopUpView.findViewById(R.id.add_activity_confirm_button);

        metScoreTextView = addTDEEPopUpView.findViewById(R.id.met_score_textView);
        caloriesBurnedInTdeeAdditionTextView = addTDEEPopUpView.findViewById(R.id.calories_burned_in_tdee_addition_popUp_textView);

        confirmActivityAddition.setText(R.string.save);
        metScoreTextView.setTextSize(22);

        tdeeAddPopUpWindow.setOnDismissListener(()-> {
            dailyStatsAdapter.turnOffEditMode();
            dailyStatsAdapter.notifyDataSetChanged();
        });
    }

    private void instantiateActivityAdditionSpinnersAndAdapters() {
        tdeeCategoryAdapter = new ArrayAdapter<>(getContext(), R.layout.tdee_category_spinner_layout, tdeeChosenActivitySpinnerValues.category_list);
        tdeeCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tdee_category_spinner.setAdapter(tdeeCategoryAdapter);

        tdeeSubCategoryAdapter = new ArrayAdapter<>(getContext(), R.layout.tdee_sub_category_spinner_layout);
        tdeeSubCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        tdee_sub_category_spinner.setAdapter(tdeeSubCategoryAdapter);
    }

    private void instantiateActivityEditPopUpViews() {
        tdeeEditView = inflater.inflate(R.layout.daily_stats_edit_popup, null);
        tdeeEditPopUpWindow = new PopupWindow(tdeeEditView, WindowManager.LayoutParams.MATCH_PARENT, dpToPxConv(270), true);
        tdeeEditPopUpWindow.setAnimationStyle(R.style.SlideFromLeftAnimationShort);

        activityTextViewInEditPopUp = tdeeEditView.findViewById(R.id.activity_string_in_edit_popUp);
        tdeeEditTextHours = tdeeEditView.findViewById(R.id.tdee_editText_hours);
        tdeeEditTextMinutes = tdeeEditView.findViewById(R.id.tdee_editText_minutes);
        tdeeEditTextSeconds = tdeeEditView.findViewById(R.id.tdee_editText_seconds);
        unassignedTimeInEditPopUpTextView = tdeeEditView.findViewById(R.id.unassigned_time_textView);

        confirmActivityEditWithinPopUpButton = tdeeEditView.findViewById(R.id.confirm_activity_edit);
        confirmActivityDeletionWithinEditPopUpButton = tdeeEditView.findViewById(R.id.activity_delete_button);

        popUpAnchorBottom = mRoot.findViewById(R.id.tdee_edit_popUp_anchor_bottom);

        tdeeEditPopUpWindow.setOnDismissListener(()-> {
            dailyStatsAdapter.turnOffEditMode();
            dailyStatsAdapter.notifyDataSetChanged();
        });
    }

    private void instantiateCaloriesConsumedEditPopUpViews() {
        addFoodView = inflater.inflate(R.layout.add_calories_consumed_popup, null);
        addFoodPopUpWindow = new PopupWindow(addFoodView, WindowManager.LayoutParams.MATCH_PARENT, dpToPxConv(270), true);
        addFoodPopUpWindow.setAnimationStyle(R.style.SlideFromLeftAnimationShort);

        typeOfFoodEditText = addFoodView.findViewById(R.id.food_name_add_text);
        caloriesConsumedEditText = addFoodView.findViewById(R.id.add_calories_consumed_editText);

        confirmCaloriesConsumedAdditionWithinPopUpButton = addFoodView.findViewById(R.id.confirm_calories_consumed_add_button);
        confirmCaloriesConsumedDeletionWithinPopUpButton = addFoodView.findViewById(R.id.confirm_calories_consumed_delete_button);

        addFoodPopUpWindow.setOnDismissListener(()-> {
            caloriesConsumedAdapter.turnOffEditMode();
            caloriesConsumedAdapter.getItemCount();
            caloriesConsumedAdapter.notifyDataSetChanged();
        });
    }

    private void instantiateCaloriesComparedViews() {
        caloriesComparedLayout = mRoot.findViewById(R.id.calories_compared_layout);
        totalConsumedCaloriesCompared = mRoot.findViewById(R.id.total_consumed_calories_compared);
        totalExpendedCaloriesCompared = mRoot.findViewById(R.id.total_expended_calories_compared);
        totalExpendedCaloriesComparedBmr = mRoot.findViewById(R.id.total_expended_calories_compared_bmr);
        totalExpendedCaloriesComparedActivities = mRoot.findViewById(R.id.total_expended_calories_compared_activities);
        totalCaloriesDifferenceCompared = mRoot.findViewById(R.id.total_calories_difference_compared);
    }

    private void instantiateCalorieTabLayoutListenerAndViews() {
        setDefaultCalorieTabViewsForFirstTab();

        caloriesComparisonTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        dailyStatsRecyclerView.setVisibility(View.VISIBLE);
                        totalActivityStatsValuesTextViewLayout.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        caloriesConsumedRecyclerView.setVisibility(View.VISIBLE);
                        totalFoodStatsValuesTextViewLayout.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        caloriesComparedLayout.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                dailyStatsRecyclerView.setVisibility(View.GONE);
                caloriesConsumedRecyclerView.setVisibility(View.GONE);
                totalActivityStatsValuesTextViewLayout.setVisibility(View.GONE);
                totalFoodStatsValuesTextViewLayout.setVisibility(View.GONE);
                caloriesComparedLayout.setVisibility(View.GONE);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setDefaultCalorieTabViewsForFirstTab() {
        caloriesConsumedRecyclerView.setVisibility(View.GONE);
        totalFoodStatsValuesTextViewLayout.setVisibility(View.GONE);
        caloriesComparedLayout.setVisibility(View.GONE);
    }

    private void instantiateTextViewsAndMiscClasses() {
        tdeeChosenActivitySpinnerValues = new TDEEChosenActivitySpinnerValues(getActivity());
        longToStringConverters = new LongToStringConverters();
        inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        caloriesComparisonTabLayout = mRoot.findViewById(R.id.calorie_comparison_tab_layout);
        caloriesComparisonTabLayout.addTab(caloriesComparisonTabLayout.newTab().setText("Calories Expended"));
        caloriesComparisonTabLayout.addTab(caloriesComparisonTabLayout.newTab().setText("Calories Consumed"));
        caloriesComparisonTabLayout.addTab(caloriesComparisonTabLayout.newTab().setText("Calories Compared"));

        activityStatsDurationRangeTextView = mRoot.findViewById(R.id.duration_date_range_textView);
        activityStatsDurationSwitcherButtonLeft = mRoot.findViewById(R.id.stat_duration_switcher_button_left);
        activityStatsDurationSwitcherButtonRight = mRoot.findViewById(R.id.stat_duration_switcher_button_right);

        minimizeCalendarButton = mRoot.findViewById(R.id.minimize_calendarView_button);
        recyclerAndTotalStatsDivider =  mRoot.findViewById(R.id.recycler_and_total_stats_divider);
        topOfRecyclerViewAnchor = mRoot.findViewById(R.id.top_of_recyclerView_anchor);
        totalStatsHeaderTextView = mRoot.findViewById(R.id.activity_stats_duration_header_textView);

        totalActivityStatsValuesTextViewLayout = mRoot.findViewById(R.id.total_activity_stats_values_textView_layout);
        totalActivityStatsValuesTextViewsLayoutParams = (ConstraintLayout.LayoutParams) totalActivityStatsValuesTextViewLayout.getLayoutParams();
        dailyStatsTotalSetTimeTextView = mRoot.findViewById(R.id.daily_stats_total_set_time_textView);
        dailyStatsTotalCaloriesBurnedTextView = mRoot.findViewById(R.id.daily_stats_total_calories_burned_textView);

        totalFoodStatsValuesTextViewLayout = mRoot.findViewById(R.id.total_food_stats_values_textView_layout);
        totalFoodStatsValuesTextViewLayoutParams = (ConstraintLayout.LayoutParams) totalFoodStatsValuesTextViewLayout.getLayoutParams();
        foodStatsTotalCaloriesConsumedTextView = mRoot.findViewById(R.id.total_food_stats_calories_consumed);

        calendarDayDecorator = new CalendarDayDecorator(getContext());
        calendarDurationSelectedDecorator = new CalendarDurationSelectedDecorator(getContext());

        customCalendarDayList = new ArrayList<>();

        dayHolderList = new ArrayList<>();
        statsForEachActivityList = new ArrayList<>();
    }

    private void instantiateActivityRecyclerViewAndItsAdapter() {
        dailyStatsAdapter = new DailyStatsAdapter(getContext(), dailyStatsAccess.getTotalActivitiesListForSelectedDuration(), dailyStatsAccess.getTotalSetTimeListForEachActivityForSelectedDuration(), dailyStatsAccess.getTotalCaloriesBurnedListForEachActivityForSelectedDuration());

        dailyStatsAdapter.getSelectedTdeeItemPosition(DailyStatsFragment.this);
        dailyStatsAdapter.addActivityToDailyStats(DailyStatsFragment.this);

        dailyStatsRecyclerView = mRoot.findViewById(R.id.daily_stats_recyclerView);
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        dailyStatsRecyclerView.setLayoutManager(lm);
        dailyStatsRecyclerView.setAdapter(dailyStatsAdapter);

        dailyStatsRecyclerViewLayoutParams = (ConstraintLayout.LayoutParams) dailyStatsRecyclerView.getLayoutParams();

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(dailyStatsRecyclerView.getContext(), lm.getOrientation());
        dividerItemDecoration.setDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
        dailyStatsRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void instantiateCalorieConsumptionRecyclerAndItsAdapter() {
        caloriesConsumedAdapter = new CaloriesConsumedAdapter(getContext(), dailyStatsAccess.getTotalFoodStringListForSelectedDuration(), dailyStatsAccess.getTotalCaloriesConsumedListForSelectedDuration());

        caloriesConsumedAdapter.getSelectedCaloriesItemPosition(DailyStatsFragment.this);
        caloriesConsumedAdapter.addCaloriesToStats(DailyStatsFragment.this);

        caloriesConsumedRecyclerView = mRoot.findViewById(R.id.calories_consumed_recyclerView);
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        caloriesConsumedRecyclerView.setLayoutManager(lm);
        caloriesConsumedRecyclerView.setAdapter(caloriesConsumedAdapter);

        caloriesConsumedRecyclerViewLayoutParams = (ConstraintLayout.LayoutParams) caloriesConsumedRecyclerView.getLayoutParams();

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(caloriesConsumedRecyclerView.getContext(), lm.getOrientation());
        dividerItemDecoration.setDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
        caloriesConsumedRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void instantiateExpansionPopUpViews() {
        exitExpansionImageButton = mRoot.findViewById(R.id.daily_stats_expanded_button);
        dailyStatsExpandedView = inflater.inflate(R.layout.daily_stats_expanded_popup, null);
        dailyStatsExpandedPopUpWindow = new PopupWindow(dailyStatsExpandedView, WindowManager.LayoutParams.MATCH_PARENT, dpToPxConv(420), false);
        dailyStatsExpandedPopUpWindow.setAnimationStyle(R.style.SlideFromLeftAnimationShort);
    }

    private void instantiateAnimations() {
        slideOutToBottom = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_to_bottom);
        slideOutToBottom.setDuration(250);
        slideOutToBottom.setFillAfter(true);
        slideInFromBottom = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_from_bottom);
        slideInFromBottom.setDuration(250);
        slideInFromBottom.setFillAfter(true);

        slideOutToBottomNoAlphaChange = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_to_bottom_no_alpha_change);
        slideOutToBottomNoAlphaChange.setDuration(250);
//        slideOutToBottomNoAlphaChange.setFillAfter(true);
        slideInFromBottomNoAlphaChange = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_from_bottom_untouched_alpha);
        slideInFromBottomNoAlphaChange.setDuration(250);
//        slideInFromBottomNoAlphaChange.setFillAfter(true);

    }

    private int dpToPxConv(float pixels) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixels, getResources().getDisplayMetrics());
    }

    private void logTotalStatRows() {
        Log.i("testTotal", "assigned time is " + longToStringConverters.convertSecondsForStatDisplay(dailyStatsAccess.getTotalSetTimeFromDayHolderList()));
        Log.i("testTotal", "unassigned time is " + longToStringConverters.convertSecondsForStatDisplay(dailyStatsAccess.getUnassignedDailyTotalTime()));
        Log.i("testTotal", "aggregate time is " + longToStringConverters.convertSecondsForStatDisplay(dailyStatsAccess.getAggregateDailyTime()));

        Log.i("testTotal", "assigned calories are " + dailyStatsAccess.getTotalCaloriesBurnedFromDayHolderList());
        Log.i("testTotal", "unassigned calories are " + dailyStatsAccess.getUnassignedDailyCalories());
        Log.i("testTotal", "aggregate calories are " + dailyStatsAccess.getAggregateDailyCalories());
    }

    private void logEditPopUpTimes() {
        long newActivityTime = getMillisValueToSaveFromEditTextString();
        long remainingDailyTime = dailyStatsAccess.getUnassignedDailyTotalTime();
        long timeInEditedRow = dailyStatsAccess.getTotalSetTimeListForEachActivityForSelectedDuration().get(mPositionToEdit);

        newActivityTime = cappedActivityTimeInMillis(newActivityTime, remainingDailyTime);

        Log.i("testTime", "new time is " + longToStringConverters.convertSecondsForStatDisplay(newActivityTime));
        Log.i("testTime", "remaining time is " + longToStringConverters.convertSecondsForStatDisplay(remainingDailyTime));
        Log.i("testTime", "time in edited row is " + longToStringConverters.convertSecondsForStatDisplay(timeInEditedRow));
    }
}