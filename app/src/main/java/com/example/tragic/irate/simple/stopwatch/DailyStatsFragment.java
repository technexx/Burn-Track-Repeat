package com.example.tragic.irate.simple.stopwatch;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
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

import com.example.tragic.irate.simple.stopwatch.Adapters.DailyStatsAdapter;
import com.example.tragic.irate.simple.stopwatch.Database.DayStatClasses.DayHolder;
import com.example.tragic.irate.simple.stopwatch.Miscellaneous.CalendarDayDecorator;
import com.example.tragic.irate.simple.stopwatch.Miscellaneous.CalendarDurationSelectedDecorator;
import com.example.tragic.irate.simple.stopwatch.Miscellaneous.LongToStringConverters;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.OnRangeSelectedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class DailyStatsFragment extends Fragment implements DailyStatsAdapter.tdeeEditedItemIsSelected, DailyStatsAdapter.tdeeActivityAddition, DailyStatsAdapter.tdeeActivityDeletion {

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

    View recyclerAndTotalStatsDivider;

    TextView totalStatsHeaderTextView;
    TextView statsTotalSetTimeTextView;
    TextView statsTotalCaloriesBurnedTextView;

    ImageButton statDurationSwitcherButtonLeft;
    ImageButton statDurationSwitcherButtonRight;

    ImageButton minimizeCalendarButton;
    boolean calendarIsMinimized;
    Animation slideOutToBottom;
    Animation slideInFromBottom;

    int currentStatDurationMode;
    int DAILY_STATS = 0;
    int WEEKLY_STATS = 1;
    int MONTHLY_STATS = 2;
    int YEARLY_STATS = 3;
    int CUSTOM_STATS = 4;
    List<CalendarDay> customCalendarDayList = new ArrayList<>();

    int ITERATING_STATS_UP = 0;
    int ITERATING_STATS_DOWN = 1;

    View tdeeEditView;
    View popUpAnchorBottom;
    View popUpAnchorLow;
    View popUpAnchorHigh;

    ImageButton editTdeeStatsButton;
    PopupWindow tdeeEditPopUpWindow;
    TextView tdeeEditPopUpActivityTextView;
    EditText tdeeEditTextHours;
    EditText tdeeEditTextMinutes;
    EditText tdeeEditTextSeconds;
    ImageButton confirmEditWithinPopUpButton;
    ImageButton confirmDeletionWithinEditPopUpButton;

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

    int selectedTdeeCategoryPosition;
    int selectedTdeeSubCategoryPosition;
    double metScore;

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.daily_stats_fragment_layout, container, false);
        mRoot = root;

        dailyStatsAccess = new DailyStatsAccess(getActivity());
        editTdeeStatsButton = mRoot.findViewById(R.id.edit_tdee_stats_button);

        instantiateCalendarObjects();
        instantiateTextViewsAndMiscClasses();
        instantiateRecyclerViewAndItsAdapter();
        instantiateAnimations();

        instantiateEditPopUpViews();
        instantiateAddPopUpViews();
        instantiateActivityAdditionSpinnersAndAdapters();
        setTdeeSpinnerListeners();

        setValueCappingTextWatcherOnEditTexts();

        AsyncTask.execute(()-> {
            daySelectedFromCalendar = aggregateDayIdFromCalendar();
            daySelectedAsACalendarDayObject = CalendarDay.from(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));

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
                    calendar = Calendar.getInstance(TimeZone.getDefault());
                    calendar.set(date.getYear(), date.getMonth()-1, date.getDay());

                    daySelectedFromCalendar = aggregateDayIdFromCalendar();
                    daySelectedAsACalendarDayObject = CalendarDay.from(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));

                    calendarDateChangeLogic();
                    populateListsAndTextViewsFromEntityListsInDatabase();
                });
            }
        });

        calendarView.setOnRangeSelectedListener(new OnRangeSelectedListener() {
            @Override
            public void onRangeSelected(@NonNull MaterialCalendarView widget, @NonNull List<CalendarDay> dates) {
                AsyncTask.execute(()->{
                    calendar = Calendar.getInstance(TimeZone.getDefault());
                    customCalendarDayList = dates;
                    populateListsAndTextViewsFromEntityListsInDatabase();
                });
            }
        });

        statDurationSwitcherButtonLeft.setOnClickListener(v-> {
            statDurationSwitchModeLogic(ITERATING_STATS_DOWN);
        });

        statDurationSwitcherButtonRight.setOnClickListener(v-> {
            statDurationSwitchModeLogic(ITERATING_STATS_UP);
        });

        confirmActivityAddition.setOnClickListener(v-> {
            addActivityToStats();
        });

        editTdeeStatsButton.setOnClickListener(v-> {
            dailyStatsAdapter.toggleEditMode();
        });

        confirmEditWithinPopUpButton.setOnClickListener(v-> {
            updateDatabaseWithStats();
            tdeeEditPopUpWindow.dismiss();
        });

        confirmDeletionWithinEditPopUpButton.setOnClickListener(v-> {
            onDeletingActivity(mPositionToEdit);
        });

        minimizeCalendarButton.setOnClickListener(v-> {
            calendarMinimizationLogic();
        });

        return root;
    }



    private void calendarDateChangeLogic() {
        dailyStatsAdapter.turnOffEditMode();
        dailyStatsAdapter.getItemCount();

        dailyStatsAccess.setOldStatsForEachActivityListSizeVariable(dailyStatsAccess.returnStatsForEachActivitySizeVariableByQueryingYearlyListOfActivities());
        populateListsAndTextViewsFromEntityListsInDatabase();
    }

    public void populateListsAndTextViewsFromEntityListsInDatabase() {
        setDayAndStatsForEachActivityEntityListsForChosenDurationOfDays(currentStatDurationMode);

        getActivity().runOnUiThread(()-> {
            dailyStatsAccess.clearStatsForEachActivityArrayLists();

            dailyStatsAccess.setTotalActivityStatsForSelectedDaysToArrayLists();
            dailyStatsAccess.setTotalSetTimeVariableForDayHolder();
            dailyStatsAccess.setTotalCaloriesVariableForDayHolder();

            dailyStatsAdapter.notifyDataSetChanged();

            setDayHolderStatsTextViews();
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
            dailyStatsAccess.setAllDayAndStatListsForYearFromDatabase(calendar.getActualMaximum(Calendar.DAY_OF_YEAR), aggregateDayIdFromCalendar());
        }
        if (mode==CUSTOM_STATS) {
            dailyStatsAccess.setAllDayAndStatListsForCustomDatesFromDatabase(customCalendarDayList);
        }

        dailyStatsAccess.setNewStatsForEachActivityListSizeVariable(dailyStatsAccess.returnStatsForEachActivitySizeVariableByQueryingYearlyListOfActivities());

        if (hasNumberOfDaysWithAtLeastOneActivityChanged()) {
            colorDaysWithAtLeastOneActivity();
        }
    }

    private boolean hasNumberOfDaysWithAtLeastOneActivityChanged() {
        int oldValue = dailyStatsAccess.getOldStatsForEachActivityListSizeVariable();
        int newValue = dailyStatsAccess.getNewStatsForEachActivityListSizeVariable();
        return (oldValue != newValue);
    }

    private void statDurationSwitchModeLogic(int directionOfIteratingDuration) {
        dailyStatsAccess.clearDaysInSelectedDurationList();

        AsyncTask.execute(()-> {
            iterateThroughStatDurationModeVariables(directionOfIteratingDuration);
            populateListsAndTextViewsFromEntityListsInDatabase();

            getActivity().runOnUiThread(()-> {
                setStatDurationViews(currentStatDurationMode);

                dailyStatsAdapter.turnOffEditMode();
                dailyStatsAdapter.getItemCount();
            });
        });
    }

    private void setStatDurationViews(int mode) {
        toggleEditStatsButton(false);

        if (mode==DAILY_STATS) {
            totalStatsHeaderTextView.setText(R.string.day_total_header);
            toggleEditStatsButton(true);
            calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
            calendarView.setSelectedDate(daySelectedAsACalendarDayObject);

            calendarView.setEnabled(false);

        }
        if (mode==WEEKLY_STATS) {
            totalStatsHeaderTextView.setText(R.string.weekly_total_header);
            calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE);
            dailyStatsAccess.populateCalendarDayListWithSelectedDurationDays(calendar, WEEKLY_STATS);
        }
        if (mode==MONTHLY_STATS) {
            totalStatsHeaderTextView.setText(R.string.monthly_total_header);
            calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE);
            dailyStatsAccess.populateCalendarDayListWithSelectedDurationDays(calendar, MONTHLY_STATS);
        }
        if (mode==YEARLY_STATS) {
            totalStatsHeaderTextView.setText(R.string.yearly_total_header);
            calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE);
            dailyStatsAccess.populateCalendarDayListWithSelectedDurationDays(calendar, YEARLY_STATS);
        }
        if (mode==CUSTOM_STATS) {
            totalStatsHeaderTextView.setText(R.string.custom_total_header);
            calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE);

            calendarView.setSelectedDate(daySelectedAsACalendarDayObject);
        }

        if (mode!=DAILY_STATS) {
            CalendarDay startingCalendarDay = dailyStatsAccess.getFirstCalendarDayInDuration();
            CalendarDay endingCalendarDay = dailyStatsAccess.getLastCalendarDayInDuration();
            calendarView.selectRange(startingCalendarDay, endingCalendarDay);
        }
    }

    public void iterateThroughStatDurationModeVariables(int directionOfIteration) {
        if (directionOfIteration==ITERATING_STATS_UP) {
            if (currentStatDurationMode<4) {
                currentStatDurationMode++;
            } else {
                currentStatDurationMode=0;
            }
        } else if (directionOfIteration==ITERATING_STATS_DOWN) {
            if (currentStatDurationMode>0) {
                currentStatDurationMode--;
            } else {
                currentStatDurationMode=4;
            }
        }
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
        List<Integer> daysWithAtLeastOneActivityList = dailyStatsAccess.getActivityListForCalendarColoring();
        List<CalendarDay> calendarDayList = new ArrayList<>();

        for (int i=0; i<daysWithAtLeastOneActivityList.size(); i++) {
            calendar.set(Calendar.DAY_OF_YEAR, daysWithAtLeastOneActivityList.get(i));
            calendarDayList.add(CalendarDay.from(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
        }

        getActivity().runOnUiThread(()->{
            calendarDayDecorator.setCalendarDayList(calendarDayList);
            calendarView.addDecorator(calendarDayDecorator);
        });
    }

    private boolean areListsOfDayDurationsTheSame() {
        return dailyStatsAccess.getOldDaysInSelectedDurationList().equals(dailyStatsAccess.getDaysInSelectedDurationList());
    }

    private void setDayHolderStatsTextViews() {
        String totalSetTime = longToStringConverters.convertSecondsForStatDisplay(dailyStatsAccess.getTotalSetTimeFromDayHolderList());
        double totalCaloriesBurned = dailyStatsAccess.getTotalCaloriesBurnedFromDayHolderList();

        statsTotalSetTimeTextView.setText(totalSetTime);
        statsTotalCaloriesBurnedTextView.setText(formatCalorieStringWithoutDecimals(totalCaloriesBurned));
    }

    public int getDaySelectedFromCalendar() {
        return daySelectedFromCalendar;
    }

    @Override
    public void onAddingActivity(int position) {
        tdeeAddPopUpWindow.showAsDropDown(popUpAnchorBottom, 0, 0);
    }

    private void addActivityToStats() {
        AsyncTask.execute(()-> {
            String activityToAdd = tdeeChosenActivitySpinnerValues.subCategoryListOfStringArrays.get(selectedTdeeCategoryPosition)[selectedTdeeSubCategoryPosition];

            dailyStatsAccess.setLocalActivityStringVariable(activityToAdd);
            dailyStatsAccess.setLocalMetScoreVariable(retrieveMetScoreFromSubCategoryPosition());
            dailyStatsAccess.checkIfActivityExistsForSpecificDayAndSetBooleanAndPositionForIt(dailyStatsAccess.getStatsForEachActivityListForFragmentAccess());

            dailyStatsAccess.insertTotalTimesAndCaloriesForEachActivityWithinASpecificDay(daySelectedFromCalendar);
            dailyStatsAccess.insertTotalTimesAndCaloriesBurnedOfCurrentDayIntoDatabase(daySelectedFromCalendar);

            if (!dailyStatsAccess.getActivityExistsInDatabaseForSelectedDay()) {
                populateListsAndTextViewsFromEntityListsInDatabase();
                mPositionToEdit = dailyStatsAccess.getStatsForEachActivityListForFragmentAccess().size()-1;

                getActivity().runOnUiThread(()-> {
                    dailyStatsAdapter.notifyDataSetChanged();
                    populateEditPopUpWithNewRow();
                });
            } else {
                getActivity().runOnUiThread(()->{
                    Toast.makeText(getContext(), "Activity exists!", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public void tdeeEditItemSelected(int position) {
        this.mPositionToEdit = position;
        launchEditPopUp(position);
    }

    private void launchEditPopUp(int position) {
        String activityString = dailyStatsAccess.getTotalActivitiesListForSelectedDuration().get(position);
        long timeToEditLongValue = dailyStatsAccess.getTotalSetTimeListForEachActivityForSelectedDuration().get(position);

        tdeeEditPopUpActivityTextView.setText(activityString);
        setTdeeEditTexts(timeToEditLongValue);

        tdeeEditPopUpWindow.showAsDropDown(recyclerAndTotalStatsDivider, 0, 0, Gravity.TOP);
    }

    private void updateDatabaseWithStats() {
        AsyncTask.execute(()-> {
            dailyStatsAccess.assignDayHolderInstanceForSelectedDay(daySelectedFromCalendar);
            dailyStatsAccess.assignStatsForEachActivityEntityForEditing(mPositionToEdit);

            long newStatValue = getMillisValueToSaveFromEditTextString();
            double retrievedMetScore = dailyStatsAccess.getMetScoreForSelectedActivity();
            double caloriesBurnedPerSecond = calculateCaloriesBurnedPerSecond(retrievedMetScore);
            double newCaloriesForActivity = ((double) (newStatValue/1000) * caloriesBurnedPerSecond);
            newCaloriesForActivity = roundDownDoubleValuesToSyncCalories(newCaloriesForActivity);

            dailyStatsAccess.setTotalSetTimeForSelectedActivity(newStatValue);
            dailyStatsAccess.setTotalCaloriesBurnedForSelectedActivity(newCaloriesForActivity);
            dailyStatsAccess.updateTotalTimesAndCaloriesBurnedForSpecificActivityOnSpecificDayRunnable();

            dailyStatsAccess.setDayHolderAndStatForEachActivityListsForSelectedDayFromDatabase(daySelectedFromCalendar);
            dailyStatsAccess.setTotalActivityStatsForSelectedDaysToArrayLists();
            dailyStatsAccess.setTotalSetTimeVariableForDayHolder();
            dailyStatsAccess.setTotalCaloriesVariableForDayHolder();

            dailyStatsAccess.setTotalSetTimeFromDayHolder(dailyStatsAccess.getTotalSetTimeVariableForDayHolder());
            dailyStatsAccess.setTotalCaloriesBurnedFromDayHolder(dailyStatsAccess.getTotalCaloriesVariableForDayHolder());
            dailyStatsAccess.updateTotalTimesAndCaloriesBurnedForCurrentDayFromDatabase();

            populateListsAndTextViewsFromEntityListsInDatabase();

            getActivity().runOnUiThread(()-> {
                Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    public void onDeletingActivity(int position) {
        AsyncTask.execute(() -> {
            dailyStatsAccess.assignDayHolderInstanceForSelectedDay(daySelectedFromCalendar);
            dailyStatsAccess.assignStatsForEachActivityEntityForEditing(position);
            dailyStatsAccess.deleteStatsForEachActivityEntity();

            dailyStatsAccess.setDayHolderAndStatForEachActivityListsForSelectedDayFromDatabase(daySelectedFromCalendar);
            dailyStatsAccess.setTotalActivityStatsForSelectedDaysToArrayLists();
            dailyStatsAccess.setTotalSetTimeVariableForDayHolder();
            dailyStatsAccess.setTotalCaloriesVariableForDayHolder();

            dailyStatsAccess.setTotalActivityStatsForSelectedDaysToArrayLists();
            dailyStatsAccess.setTotalSetTimeFromDayHolder(dailyStatsAccess.getTotalSetTimeVariableForDayHolder());
            dailyStatsAccess.setTotalCaloriesBurnedFromDayHolder(dailyStatsAccess.getTotalCaloriesVariableForDayHolder());

            populateListsAndTextViewsFromEntityListsInDatabase();

            getActivity().runOnUiThread(()-> {
                tdeeEditPopUpWindow.dismiss();
                Toast.makeText(getContext(), "Deleted!", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void setTdeeEditTexts(long valueToSet) {
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
    }

    private long getMillisValueToSaveFromEditTextString() {
        setEditTextToZerosIfEmpty(tdeeEditTextHours);
        setEditTextToZerosIfEmpty(tdeeEditTextMinutes);
        setEditTextToZerosIfEmpty(tdeeEditTextSeconds);

        long hours = Long.parseLong(tdeeEditTextHours.getText().toString());
        long minutes = Long.parseLong(tdeeEditTextMinutes.getText().toString());
        long seconds = Long.parseLong(tdeeEditTextSeconds.getText().toString());

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

    private void setEditTextToZerosIfEmpty (EditText editText) {
        if (editText.getText().toString().equals("")) {
            editText.setText(R.string.double_zeros);
        }
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

    private void setValueCappingTextWatcherOnEditTexts() {
        tdeeEditTextMinutes.addTextChangedListener(editTextWatcher(tdeeEditTextMinutes));
        tdeeEditTextSeconds.addTextChangedListener(editTextWatcher(tdeeEditTextSeconds));
    }

    private void populateEditPopUpWithNewRow() {
        replaceAddPopUpWithEditPopUp();

        String activityToAdd = tdeeChosenActivitySpinnerValues.subCategoryListOfStringArrays.get(selectedTdeeCategoryPosition)[selectedTdeeSubCategoryPosition];

        tdeeEditPopUpActivityTextView.setText(activityToAdd);
        setTdeeEditTexts(0);
    }

    private void replaceAddPopUpWithEditPopUp() {
        tdeeAddPopUpWindow.dismiss();
        tdeeEditPopUpWindow.showAsDropDown(recyclerAndTotalStatsDivider, 0, 0, Gravity.TOP);
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
        setCaloriesBurnedTextViewInAddTdeePopUp();
    }

    private void tdeeSubCategorySpinnerTouchActions() {
        selectedTdeeCategoryPosition = tdee_category_spinner.getSelectedItemPosition();
        selectedTdeeSubCategoryPosition = tdee_sub_category_spinner.getSelectedItemPosition();

        setMetScoreTextViewInAddTdeePopUp();
        setCaloriesBurnedTextViewInAddTdeePopUp();
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

    private void setCaloriesBurnedTextViewInAddTdeePopUp() {
        String caloriesBurnedPerMinute = formatCalorieString(calculateCaloriesBurnedPerMinute(metScore));
        String caloriesBurnedPerHour = formatCalorieString(calculateCaloriesBurnedPerMinute(metScore) * 60);

        caloriesBurnedInTdeeAdditionTextView.setText(getString(R.string.two_line_concat, getString(R.string.calories_burned_per_minute, caloriesBurnedPerMinute), getString(R.string.calories_burned_per_hour, caloriesBurnedPerHour)));
    }

    private String formatCalorieString(double calories) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(calories);
    }

    private String formatCalorieStringWithoutDecimals(double calories) {
        DecimalFormat df = new DecimalFormat("#");
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

    private void toggleCalendarMinimizationState() {
        calendarIsMinimized = !calendarIsMinimized;
    }

    private void calendarMinimizationLogic() {
        toggleCalendarMinimizationState();
        toggleCalendarMinimizationLayouts();

        if (!calendarIsMinimized) {
            minimizeCalendarButton.setImageResource(R.drawable.arrow_down_2);
            calendarView.startAnimation(slideInFromBottom);

        } else {
            minimizeCalendarButton.setImageResource(R.drawable.arrow_up_2);
            calendarView.startAnimation(slideOutToBottom);
        }
    }

    private void toggleCalendarMinimizationLayouts() {
        ConstraintLayout.LayoutParams dailyStatsRecyclerViewLayoutParams = (ConstraintLayout.LayoutParams) dailyStatsRecyclerView.getLayoutParams();

        View recyclerAndTotalStatsDivider = mRoot.findViewById(R.id.recycler_and_total_stats_divider);
        ConstraintLayout.LayoutParams recyclerAndTotalStatsDividerLayoutParams = (ConstraintLayout.LayoutParams) recyclerAndTotalStatsDivider.getLayoutParams();

        ConstraintLayout totalStatsHeaderLayout = mRoot.findViewById(R.id.stats_total_header_layout);
        ConstraintLayout.LayoutParams totalStatsHeaderLayoutParams = (ConstraintLayout.LayoutParams) totalStatsHeaderLayout.getLayoutParams();

        ConstraintLayout totalStatsValuesTextViewsLayout = mRoot.findViewById(R.id.total_stats_values_textView_layout);
        ConstraintLayout.LayoutParams totalStatsValuesTextViewsLayoutParams = (ConstraintLayout.LayoutParams) totalStatsValuesTextViewsLayout.getLayoutParams();

        if (!calendarIsMinimized) {
            dailyStatsRecyclerViewLayoutParams.height = dpToPxConv(280);
            dailyStatsRecyclerViewLayoutParams.bottomToBottom = ConstraintLayout.LayoutParams.UNSET;

            recyclerAndTotalStatsDividerLayoutParams.bottomToTop = R.id.daily_stats_total_calories_burned_textView;
            recyclerAndTotalStatsDividerLayoutParams.topMargin = dpToPxConv(16);

            totalStatsHeaderLayoutParams.topToTop = ConstraintLayout.LayoutParams.UNSET;
            totalStatsHeaderLayoutParams.topToTop = R.id.daily_stats_fragment_parent_layout;
            totalStatsHeaderLayoutParams.startToStart = ConstraintLayout.LayoutParams.UNSET;
            totalStatsHeaderLayoutParams.startToStart = R.id.daily_stats_fragment_parent_layout;

            totalStatsValuesTextViewsLayoutParams.bottomToTop = ConstraintLayout.LayoutParams.UNSET;
            totalStatsValuesTextViewsLayoutParams.bottomToBottom = ConstraintLayout.LayoutParams.UNSET;

            totalStatsValuesTextViewsLayoutParams.bottomToTop = R.id.stats_calendar;
            totalStatsValuesTextViewsLayout.startAnimation(slideInFromBottom);
        } else {
            dailyStatsRecyclerViewLayoutParams.height = 0;
            dailyStatsRecyclerViewLayoutParams.bottomToBottom = R.id.daily_stats_fragment_parent_layout;

            totalStatsValuesTextViewsLayoutParams.bottomToTop = ConstraintLayout.LayoutParams.UNSET;
            totalStatsValuesTextViewsLayoutParams.bottomToBottom = ConstraintLayout.LayoutParams.UNSET;

            totalStatsValuesTextViewsLayoutParams.bottomToTop= R.id.minimize_calendarView_button;
        }

        dailyStatsRecyclerView.setLayoutParams(dailyStatsRecyclerViewLayoutParams);
        recyclerAndTotalStatsDivider.setLayoutParams(recyclerAndTotalStatsDividerLayoutParams);
        totalStatsHeaderLayout.setLayoutParams(totalStatsHeaderLayoutParams);
        totalStatsValuesTextViewsLayout.setLayoutParams(totalStatsValuesTextViewsLayoutParams);
    }

    private void instantiateCalendarObjects() {
        calendar = Calendar.getInstance(TimeZone.getDefault());
        calendarView = mRoot.findViewById(R.id.stats_calendar);

        CalendarDay calendarDay = CalendarDay.from(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        calendarView.setSelectedDate(calendarDay);

        calendarView.state().edit()
                .setMinimumDate(CalendarDay.from(2022, 1, 1))
                .setMaximumDate(calendarDay)
                .commit();
    }

    private void instantiateAddPopUpViews() {
        addTDEEPopUpView = inflater.inflate(R.layout.add_tdee_popup, null);
        tdeeAddPopUpWindow = new PopupWindow(addTDEEPopUpView, WindowManager.LayoutParams.MATCH_PARENT, dpToPxConv(340), true);
        tdeeAddPopUpWindow.setAnimationStyle(R.style.SlideBottomAnimation);

        tdee_category_spinner = addTDEEPopUpView.findViewById(R.id.activity_category_spinner);
        tdee_sub_category_spinner = addTDEEPopUpView.findViewById(R.id.activity_sub_category_spinner);
        confirmActivityAddition = addTDEEPopUpView.findViewById(R.id.add_activity_confirm_button);

        metScoreTextView = addTDEEPopUpView.findViewById(R.id.met_score_textView);
        caloriesBurnedInTdeeAdditionTextView = addTDEEPopUpView.findViewById(R.id.calories_burned_in_tdee_addition_popUp_textView);

        confirmActivityAddition.setText(R.string.okay);
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

    private void instantiateEditPopUpViews() {
        tdeeEditView = inflater.inflate(R.layout.daily_stats_edit_popup, null);
        tdeeEditPopUpWindow = new PopupWindow(tdeeEditView, WindowManager.LayoutParams.MATCH_PARENT, dpToPxConv(200), true);
        tdeeEditPopUpWindow.setAnimationStyle(R.style.SlideFromLeftAnimationShort);

        popUpAnchorHigh = mRoot.findViewById(R.id.tdee_edit_popUp_anchor_high);
        popUpAnchorLow = mRoot.findViewById(R.id.tdee_edit_popUp_anchor_low);
        popUpAnchorBottom = mRoot.findViewById(R.id.tdee_edit_popUp_anchor_bottom);
        tdeeEditPopUpActivityTextView = tdeeEditView.findViewById(R.id.activity_string_in_edit_popUp);
        tdeeEditTextHours = tdeeEditView.findViewById(R.id.tdee_editText_hours);
        tdeeEditTextMinutes = tdeeEditView.findViewById(R.id.tdee_editText_minutes);
        tdeeEditTextSeconds = tdeeEditView.findViewById(R.id.tdee_editText_seconds);
        confirmEditWithinPopUpButton = tdeeEditView.findViewById(R.id.confirm_activity_edit);
        confirmDeletionWithinEditPopUpButton = tdeeEditView.findViewById(R.id.activity_delete_button);

        tdeeEditPopUpWindow.setOnDismissListener(()-> {
            dailyStatsAdapter.turnOffEditMode();
            dailyStatsAdapter.notifyDataSetChanged();
        });
    }

    private void instantiateTextViewsAndMiscClasses() {
        tdeeChosenActivitySpinnerValues = new TDEEChosenActivitySpinnerValues(getActivity());
        longToStringConverters = new LongToStringConverters();
        inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        totalStatsHeaderTextView = mRoot.findViewById(R.id.total_stats_header);
        statsTotalSetTimeTextView = mRoot.findViewById(R.id.daily_stats_total_set_time_textView);
        statsTotalCaloriesBurnedTextView = mRoot.findViewById(R.id.daily_stats_total_calories_burned_textView);
        statDurationSwitcherButtonLeft = mRoot.findViewById(R.id.stat_duration_switcher_button_left);
        statDurationSwitcherButtonRight = mRoot.findViewById(R.id.stat_duration_switcher_button_right);
        minimizeCalendarButton = mRoot.findViewById(R.id.minimize_calendarView_button);
        recyclerAndTotalStatsDivider =  mRoot.findViewById(R.id.recycler_and_total_stats_divider);

        calendarDayDecorator = new CalendarDayDecorator(getContext());
        calendarDurationSelectedDecorator = new CalendarDurationSelectedDecorator(getContext());
    }

    private void instantiateRecyclerViewAndItsAdapter() {
        dailyStatsAdapter = new DailyStatsAdapter(getContext(), dailyStatsAccess.totalActivitiesListForSelectedDuration, dailyStatsAccess.totalSetTimeListForEachActivityForSelectedDuration, dailyStatsAccess.totalCaloriesBurnedListForEachActivityForSelectedDuration);

        dailyStatsAdapter.getSelectedTdeeItemPosition(DailyStatsFragment.this);
        dailyStatsAdapter.addActivityToDailyStats(DailyStatsFragment.this);
        dailyStatsAdapter.deleteActivityFromDailyStats(DailyStatsFragment.this);

        dailyStatsRecyclerView = mRoot.findViewById(R.id.daily_stats_recyclerView);
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        dailyStatsRecyclerView.setLayoutManager(lm);
        dailyStatsRecyclerView.setAdapter(dailyStatsAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(dailyStatsRecyclerView.getContext(), lm.getOrientation());
        dailyStatsRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void instantiateAnimations() {
        slideOutToBottom = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_to_bottom);
        slideOutToBottom.setDuration(200);
        slideOutToBottom.setFillAfter(true);
        slideInFromBottom = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_from_bottom);
        slideInFromBottom.setDuration(200);
        slideInFromBottom.setFillAfter(true);
    }

    private int dpToPxConv(float pixels) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixels, getResources().getDisplayMetrics());
    }
}