package com.example.tragic.irate.simple.stopwatch;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tragic.irate.simple.stopwatch.Adapters.DailyStatsAdapter;
import com.example.tragic.irate.simple.stopwatch.Miscellaneous.LongToStringConverters;
import com.example.tragic.irate.simple.stopwatch.Miscellaneous.NumberFilter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class DailyStatsFragment extends Fragment implements DailyStatsAdapter.tdeeEditedItemIsSelected, DailyStatsAdapter.tdeeActivityAddition, DailyStatsAdapter.tdeeActivityDeletion {

    View mRoot;
    Calendar calendar;
    CalendarView calendarView;
    int daySelectedFromCalendar;

    TDEEChosenActivitySpinnerValues tdeeChosenActivitySpinnerValues;
    LongToStringConverters longToStringConverters;
    LayoutInflater inflater;

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
    View popUpAnchorBottom;
    View popUpAnchorLow;
    View popUpAnchorHigh;
    View recyclerAndTotalStatsDivider;

    ImageButton editTdeeStatsButton;
    PopupWindow tdeeEditPopUpWindow;
    TextView tdeeEditPopUpActivityTextView;
    EditText tdeeEditTextHours;
    EditText tdeeEditTextMinutes;
    EditText tdeeEditTextSeconds;
    ImageButton confirmEditWithinPopUpButton;

    int typeOfStatToEdit;
    int mPositionToEdit;
    int EDITING_SETS = 0;
    int EDITING_BREAKS = 1;

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

        calendar = Calendar.getInstance(TimeZone.getDefault());
        calendarView = mRoot.findViewById(R.id.stats_calendar);
        dailyStatsAccess = new DailyStatsAccess(getActivity());
        editTdeeStatsButton = mRoot.findViewById(R.id.edit_tdee_stats_button);

        instantiateTextViewsAndMiscClasses();
        instantiateRecyclerViewAndItsAdapter();

        instantiateEditPopUpViews();
        instantiateAddPopUpViews();
        instantiateActivityAdditionSpinnersAndAdapters();
        setTdeeSpinnerListeners();

        setValueCappingTextWatcherOnEditTexts();

        AsyncTask.execute(()-> {
            daySelectedFromCalendar = calendar.get(Calendar.DAY_OF_YEAR);

            setDayAndStatListsForChosenDurationOfDays(currentStatDurationMode);
            populateListsAndTextViewsFromEntityListsInDatabase();

            getActivity().runOnUiThread(()-> {
                setStatDurationTextViewAndEditButton(currentStatDurationMode);

            });
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                AsyncTask.execute(()-> {
                    calendar = Calendar.getInstance(TimeZone.getDefault());
                    calendar.set(year, month, dayOfMonth);
                    daySelectedFromCalendar = calendar.get(Calendar.DAY_OF_YEAR);

                    dailyStatsAdapter.turnOffEditMode();
                    dailyStatsAdapter.getItemCount();

                    setDayAndStatListsForChosenDurationOfDays(currentStatDurationMode);
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
            tdeeAddPopUpWindow.dismiss();
        });

        editTdeeStatsButton.setOnClickListener(v-> {
            toggleEditModeInStatsAdapter();
        });

        confirmEditWithinPopUpButton.setOnClickListener(v-> {
            updateDatabaseWithEditedStats();
            tdeeEditPopUpWindow.dismiss();
        });
        return root;
    }

    private void statDurationSwitchModeLogic(int directionOfIteratingDuration) {
        AsyncTask.execute(()-> {
            iterateThroughStatDurationModeVariables(directionOfIteratingDuration);
            populateListsAndTextViewsFromEntityListsInDatabase();

            getActivity().runOnUiThread(()-> {
                setStatDurationTextViewAndEditButton(currentStatDurationMode);

                dailyStatsAdapter.turnOffEditMode();
                dailyStatsAdapter.getItemCount();
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
            dailyStatsAccess.setDayHolderAndStatForEachActivityListsForSelectedDayFromDatabase(daySelectedFromCalendar);
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

    private void setStatDurationTextViewAndEditButton(int mode) {
        toggleEditStatsButton(false);

        if (mode==DAILY_STATS) {
            totalStatsHeaderTextView.setText(R.string.day_total_header);
            toggleEditStatsButton(true);
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

    private void toggleEditStatsButton(boolean buttonIsEnabled) {
        if (buttonIsEnabled) {
            editTdeeStatsButton.setEnabled(true);
            editTdeeStatsButton.setAlpha(1.0f);
        } else {
            editTdeeStatsButton.setEnabled(false);
            editTdeeStatsButton.setAlpha(0.3f);
        }
    }

    public void populateListsAndTextViewsFromEntityListsInDatabase() {
        setDayAndStatListsForChosenDurationOfDays(currentStatDurationMode);

        getActivity().runOnUiThread(()-> {
            setDayHolderStatsTextViews();

            dailyStatsAccess.clearStatsForEachActivityArrayLists();
            dailyStatsAccess.setTotalActivityStatsForSelectedDaysToLists();
            dailyStatsAdapter.notifyDataSetChanged();
        });
    }

    private void setDayHolderStatsTextViews() {
        String totalSetTime = longToStringConverters.convertSecondsForStatDisplay(dailyStatsAccess.getTotalSetTimeFromDayHolderList());
        String totalBreakTime = longToStringConverters.convertSecondsForStatDisplay(dailyStatsAccess.getTotalBreakTimeFromDayHolderList());
        double totalCaloriesBurned = dailyStatsAccess.getTotalCaloriesBurnedFromDayHolderList();

        statsTotalSetTimeTextView.setText(totalSetTime);
        statsTotalBreakTimeTextView.setText(totalBreakTime);
        statsTotalCaloriesBurnedTextView.setText(formatCalorieToString(totalCaloriesBurned));
    }

    public int getDaySelectedFromCalendar() {
        return daySelectedFromCalendar;
    }

    private void toggleEditModeInStatsAdapter() {
        dailyStatsAdapter.toggleEditMode();
    }

    @Override
    public void tdeeEditItemSelected(int typeOfEdit, int position) {
        this.mPositionToEdit = position;

        String activityString = dailyStatsAccess.getTotalActivitiesListForSelectedDuration().get(position);
        long timeToEditLongValue;

        if (typeOfEdit==0) {
            timeToEditLongValue = dailyStatsAccess.getTotalSetTimeListForEachActivityForSelectedDuration().get(position);
            typeOfStatToEdit = EDITING_SETS;
        } else {
            timeToEditLongValue = dailyStatsAccess.getTotalBreakTimeListForEachActivityForSelectedDuration().get(position);
            typeOfStatToEdit = EDITING_BREAKS;
        }

        tdeeEditPopUpActivityTextView.setText(activityString);
        setTdeeEditTexts(timeToEditLongValue);

        tdeeEditPopUpWindow.showAsDropDown(popUpAnchorLow, 0, 0);
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

    private void updateDatabaseWithEditedStats() {
        AsyncTask.execute(()-> {
            dailyStatsAccess.assignDayHolderInstanceForSelectedDay(daySelectedFromCalendar);
            dailyStatsAccess.assignStatsForEachActivityInstanceForEditing(mPositionToEdit);

            long oldStatValue;
            long newStatValue = getMillisValueToSaveFromEditTextString();

            if (typeOfStatToEdit==EDITING_SETS) {
                oldStatValue = dailyStatsAccess.getTotalSetTimeForSelectedActivity();

                long dayHolderSetValueToEdit = dailyStatsAccess.getTotalSetTimeFromDayHolder();
                long setTimeToSubtractForActivity = getDifferenceToSubtractBetweenOldAndEditedTimes(oldStatValue, newStatValue);

                double oldCaloriesFromActivity = dailyStatsAccess.getTotalCaloriesBurnedForSelectedActivity();
                double caloricMultiplier = getCaloricMultiplierForSeconds(oldCaloriesFromActivity, oldStatValue/1000);
                double newCaloriesForActivity = ((double) newStatValue/1000) * caloricMultiplier;

                double oldCaloriesFromDayHolder = dailyStatsAccess.getTotalCaloriesBurnedFromDayHolder();
                double caloriesToSubtractForDayHolder = getDifferenceToSubtractBetweenOldAndEditedCalories(oldCaloriesFromActivity, newCaloriesForActivity);

                dailyStatsAccess.setTotalSetTimeFromDayHolder(dayHolderSetValueToEdit - setTimeToSubtractForActivity);
                dailyStatsAccess.setTotalSetTimeForSelectedActivity(newStatValue);
                dailyStatsAccess.setTotalCaloriesBurnedForSelectedActivity(newCaloriesForActivity);
                dailyStatsAccess.setTotalCaloriesBurnedFromDayHolder(oldCaloriesFromDayHolder - caloriesToSubtractForDayHolder);
            } else if (typeOfStatToEdit==EDITING_BREAKS) {
                oldStatValue = dailyStatsAccess.getTotalBreakTimeForSelectedActivity();

                long dayHolderBreakValueToEdit = dailyStatsAccess.getTotalBreakTimeFromDayHolder();
                long editValueToSubtract = getDifferenceToSubtractBetweenOldAndEditedTimes(oldStatValue, newStatValue);
                dailyStatsAccess.setTotalBreakTimeFromDayHolder(dayHolderBreakValueToEdit - editValueToSubtract);
                dailyStatsAccess.setTotalBreakTimeForSelectedActivity(newStatValue);
            }

            dailyStatsAccess.updateTotalTimesAndCaloriesBurnedForCurrentDayFromDatabase();
            dailyStatsAccess.updateTotalTimesAndCaloriesBurnedForSpecificActivityOnSpecificDayRunnable();

            populateListsAndTextViewsFromEntityListsInDatabase();

            getActivity().runOnUiThread(()-> {
                Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private double getCaloricMultiplierForSeconds(double calories, long timeElapsed) {
        return calories/timeElapsed;
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

    private long getDifferenceToSubtractBetweenOldAndEditedTimes(long oldValue, long newValue) {
        return oldValue - newValue;
    }

    private double getDifferenceToSubtractBetweenOldAndEditedCalories(double oldValue, double newValue) {
        return oldValue - newValue;
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
                    if (Long.parseLong(editText.getText().toString()) >60) {
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

    @Override
    public void onAddingActivity(int position) {
        tdeeAddPopUpWindow.showAsDropDown(popUpAnchorBottom, 0, 0);
    }

    private void addActivityToStats() {
        AsyncTask.execute(()-> {
            String activityToAdd = tdeeChosenActivitySpinnerValues.subCategoryListOfStringArrays.get(selectedTdeeCategoryPosition)[selectedTdeeSubCategoryPosition];
            dailyStatsAccess.setActivityStringFromSpinner(activityToAdd);
            dailyStatsAccess.checkIfActivityExistsForSpecificDayAndSetBooleanAndPositionForIt(dailyStatsAccess.getStatsForEachActivityListForFragmentAccess());
            dailyStatsAccess.insertTotalTimesAndCaloriesForEachActivityWithinASpecificDay(daySelectedFromCalendar);
            populateListsAndTextViewsFromEntityListsInDatabase();

            getActivity().runOnUiThread(()-> {
                if (dailyStatsAccess.getActivityExistsInDatabaseForSelectedDay()) {
                    Toast.makeText(getContext(), "Activity exists!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public void onDeletingActivity(int position) {

    }

    private void deleteActivityFromStats() {
        AsyncTask.execute(() -> {
            dailyStatsAccess.assignDayHolderInstanceForSelectedDay(daySelectedFromCalendar);
            dailyStatsAccess.assignStatsForEachActivityInstanceForEditing(mPositionToEdit);
            dailyStatsAccess.deleteStatsForEachActivityEntity();
            populateListsAndTextViewsFromEntityListsInDatabase();
        });
    }

    private void toggleDeletionConfirmation() {

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

    private double calculateCaloriesBurnedPerMinute(double metValue) {
        SharedPreferences sp = getContext().getSharedPreferences("pref", 0);
        boolean metricMode = sp.getBoolean("metricMode", false);
        int userWeight = sp.getInt("tdeeWeight,", 150);

        double weightConversion = userWeight;
        if (!metricMode) weightConversion = weightConversion / 2.205;
        double caloriesBurnedPerMinute = (metValue * 3.5 * weightConversion) / 200;
        return caloriesBurnedPerMinute;
    }

    private void instantiateAddPopUpViews() {
        addTDEEPopUpView = inflater.inflate(R.layout.add_tdee_popup, null);
        tdeeAddPopUpWindow = new PopupWindow(addTDEEPopUpView, WindowManager.LayoutParams.MATCH_PARENT, dpToPxConv(340), true);
        tdeeAddPopUpWindow.setAnimationStyle(R.style.WindowAnimation);

        tdee_category_spinner = addTDEEPopUpView.findViewById(R.id.activity_category_spinner);
        tdee_sub_category_spinner = addTDEEPopUpView.findViewById(R.id.activity_sub_category_spinner);
        confirmActivityAddition = addTDEEPopUpView.findViewById(R.id.add_activity_confirm_button);

        metScoreTextView = addTDEEPopUpView.findViewById(R.id.met_score_textView);
        caloriesBurnedInTdeeAdditionTextView = addTDEEPopUpView.findViewById(R.id.calories_burned_in_tdee_addition_popUp_textView);

        confirmActivityAddition.setText(R.string.okay);
        metScoreTextView.setTextSize(22);

        tdeeAddPopUpWindow.setOnDismissListener(()-> {
            dailyStatsAdapter.turnOffEditMode();
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
        tdeeEditView = inflater.inflate(R.layout.daily_stats_edit_view, null);
        tdeeEditPopUpWindow = new PopupWindow(tdeeEditView, WindowManager.LayoutParams.MATCH_PARENT, dpToPxConv(140), true);

        popUpAnchorHigh = mRoot.findViewById(R.id.tdee_edit_popUp_anchor_high);
        popUpAnchorLow = mRoot.findViewById(R.id.tdee_edit_popUp_anchor_low);
        popUpAnchorBottom = mRoot.findViewById(R.id.tdee_edit_popUp_anchor_bottom);
        tdeeEditPopUpActivityTextView = tdeeEditView.findViewById(R.id.activity_string_in_edit_popUp);
        tdeeEditTextHours = tdeeEditView.findViewById(R.id.tdee_editText_hours);
        tdeeEditTextMinutes = tdeeEditView.findViewById(R.id.tdee_editText_minutes);
        tdeeEditTextSeconds = tdeeEditView.findViewById(R.id.tdee_editText_seconds);
        confirmEditWithinPopUpButton = tdeeEditView.findViewById(R.id.confirm_stats_edit);

        tdeeEditPopUpWindow.setOnDismissListener(()-> {
            dailyStatsAdapter.turnOffEditMode();
        });
    }

    private void instantiateTextViewsAndMiscClasses() {
        tdeeChosenActivitySpinnerValues = new TDEEChosenActivitySpinnerValues(getActivity());
        longToStringConverters = new LongToStringConverters();
        inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        totalStatsHeaderTextView = mRoot.findViewById(R.id.total_stats_header);
        statsTotalSetTimeTextView = mRoot.findViewById(R.id.daily_stats_total_set_time_textView);
        statsTotalBreakTimeTextView = mRoot.findViewById(R.id.daily_stats_total_break_time_textView);
        statsTotalCaloriesBurnedTextView = mRoot.findViewById(R.id.daily_stats_total_calories_burned_textView);
        statDurationSwitcherButtonLeft = mRoot.findViewById(R.id.stat_duration_switcher_button_left);
        statDurationSwitcherButtonRight = mRoot.findViewById(R.id.stat_duration_switcher_button_right);

        recyclerAndTotalStatsDivider = mRoot.findViewById(R.id.recycler_and_total_stats_divider);
    }

    private void instantiateRecyclerViewAndItsAdapter() {
        dailyStatsAdapter = new DailyStatsAdapter(getContext(), dailyStatsAccess.totalActivitiesListForSelectedDuration, dailyStatsAccess.totalSetTimeListForEachActivityForSelectedDuration, dailyStatsAccess.totalBreakTimeListForEachActivityForSelectedDuration, dailyStatsAccess.totalCaloriesBurnedListForEachActivityForSelectedDuration);

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

    private String formatCalorieToString(double calories) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(calories);
    }

    private int dpToPxConv(float pixels) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixels, getResources().getDisplayMetrics());
    }

    private int dpToSpConv(float pixels) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, pixels, getResources().getDisplayMetrics());
    }

    private void testEditing() {
//        Log.i("testCal", "old activity time is " + oldStatValue/1000);
//        Log.i("testCal", "new activity time is " + newStatValue/1000);
//
//        Log.i("testCal", "old dayHolder time is " + dayHolderSetValueToEdit/1000);
//        Log.i("testCal", "dayHolder value to subtract is " + setTimeToSubtractForActivity/1000);
//        Log.i("testCal", "new dayHolder time is " + (dayHolderSetValueToEdit - setTimeToSubtractForActivity)/1000);
//
//        Log.i("testCal", "activity cals are " + oldCaloriesFromActivity);
//        Log.i("testCal", "cal multiplier is " + caloricMultiplier);
//        Log.i("testCal", "new activity cals are " + newCaloriesForActivity);
//
//        Log.i("testCal", "old dayHolder cals are " + oldCaloriesFromDayHolder);
//        Log.i("testCal", "dayHolder cals to subtract are " + caloriesToSubtractForDayHolder);
//        Log.i("testCal", "new dayHolder cals are " + (oldCaloriesFromDayHolder - caloriesToSubtractForDayHolder));
    }
}