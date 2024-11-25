package burn.track.repeat.Database.DailyStatClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
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

import burn.track.repeat.Adapters.CaloriesConsumedAdapter;
import burn.track.repeat.Adapters.DailyStatsAdapter;
import burn.track.repeat.Database.DailyCalorieClasses.CaloriesForEachFood;
import burn.track.repeat.Miscellaneous.CalendarDayDecorators;
import burn.track.repeat.Miscellaneous.LongToStringConverters;


import burn.track.repeat.Miscellaneous.TDEEChosenActivitySpinnerValues;
import burn.track.repeat.R;

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

public class DailyStatsFragment extends Fragment implements DailyStatsAdapter.tdeeEditedItemIsSelected, DailyStatsAdapter.tdeeActivityAddition, CaloriesConsumedAdapter.caloriesConsumedEdit, CaloriesConsumedAdapter.caloriesConsumedAddition {

    Handler mHandler = new Handler();
    Runnable notifyDataSetChangedRunnable;
    View mRoot;
    SharedPreferences sharedPref;
    SharedPreferences.Editor prefEdit;
    Calendar mCalendar;
    com.prolificinteractive.materialcalendarview.MaterialCalendarView calendarView;


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
    View bottomOfRecyclerViewAnchor;
    View recyclerAndTotalStatsDivider;

    TabLayout caloriesComparisonTabLayout;
    TabLayout.Tab tabOne;
    TabLayout.Tab tabTwo;
    TabLayout.Tab tabThree;
    TextView tabOneTextView;
    TextView tabTwoTextView;
    TextView tabThreeTextView;

    TextView totalStatsHeaderTextView;
    ImageButton activityStatsDurationSwitcherButtonLeft;
    ImageButton activityStatsDurationSwitcherButtonRight;
    TextView activityStatsDurationRangeTextView;

    ImageButton dailyStatsExpandedButton;
    ImageButton editTdeeStatsButton;

    ConstraintLayout totalActivityStatsValuesTextViewLayout;
    ConstraintLayout.LayoutParams totalActivityStatsValuesTextViewsLayoutParams;

    TextView dailyStatsBmrTimeTextView;
    TextView dailyStatsBmrCaloriesTextView;
    TextView dailyStatsTotalActivityTimeTextView;
    TextView dailyStatsTotalActivityCaloriesBurnedTextView;
    TextView dailyTotalExpendedTimeElapsedTextView;
    TextView dailyTotalExpendedCaloriesBurnedTextView;

    ConstraintLayout totalFoodStatsValuesTextViewLayout;
    ConstraintLayout.LayoutParams totalFoodStatsValuesTextViewLayoutParams;
    TextView foodStatsTotalCaloriesConsumedTextView;

    ImageButton minimizeCalendarButton;
    boolean calendarIsMinimized;
    Animation slideOutCalendarToBottom;
    Animation slideInCalendarFromBottom;
    Animation fadeOutStatsBar;
    Animation fadeInStatsBar;

    Animation slideOutTotalStatValuesLayoutToBottom;
    Animation slideInTotalStatValuesLayoutFromBottom;

    int currentStatDurationMode;
    int ITERATING_ACTIVITY_STATS_UP = 0;
    int ITERATING_ACTIVITY_STATS_DOWN = 1;

    int DAILY_STATS = 0;
    int WEEKLY_STATS = 1;
    int MONTHLY_STATS = 2;
//    int YEAR_TO_DATE_STATS = 3;
//    int YEARLY_STATS = 4;
    int CUSTOM_STATS = 3;
    boolean numberOfDaysWithActivitiesHasChanged;

    int SPINNER_ACTIVITY = 0;
    int CUSTOM_ACTIVITY = 1;

    List<CalendarDay> customCalendarDayList;
    List<StatsForEachActivity> statsForEachActivityList;
    List<CaloriesForEachFood> caloriesForEachFoodList;

    View dailyStatsExpandedView ;
    PopupWindow dailyStatsExpandedPopUpWindow;

    PopupWindow addTdeePopUpWindow;
    View addTDEEPopUpView;

    Spinner tdee_category_spinner;
    Spinner tdee_sub_category_spinner;
    ArrayAdapter<String> tdeeCategoryAdapter;
    ArrayAdapter<String> tdeeSubCategoryAdapter;

    TextView caloriesBurnedInTdeeAdditionTextView;
    TextView metScoreTextView;
    Button addActivityConfirmButton;

    View tdeeEditView;
    View popUpAnchorBottom;
    PopupWindow tdeeEditPopUpWindow;

    PopupWindow customActivityPopUpWindow;
    View customActivityPopUpView;

    EditText addCustomActivityEditText;
    EditText addCustomCaloriesEditText;
    TextView addCustomCaloriesPerHourTextView;
    TextView addCustomCaloriesPerMinuteTextView;
    Button confirmCustomActivityAdditionValues;
    Button cancelCustomActivityValues;

    ConstraintLayout editActivityPopUpLayout;

    TextView activityInEditPopUpTextView;
    ConstraintLayout.LayoutParams activityInEditPopUpTextViewLayoutParams;

    ConstraintLayout editTextLayoutForActivityEdit;
    ConstraintLayout.LayoutParams editTextLayoutParamsForActivityEdit;
    EditText tdeeEditTextHours;
    EditText tdeeEditTextMinutes;
    EditText tdeeEditTextSeconds;
    TextView unassignedTimeInEditPopUpTextView;
    Button confirmActivityEditWithinPopUpButton;
    Button deleteActivityIfEditingRowWithinEditPopUpButton;
    TextView multipleDayWarningForActivitiesTextView;

    ConstraintLayout.LayoutParams confirmActivityEditWithinPopUpButtonLayoutParams;
    ConstraintLayout.LayoutParams deleteActivityIfEditingRowWithinEditPopUpButtonLayoutParams;

    int mPositionToEdit;
    int ADDING_ACTIVITY = 0;
    int EDITING_ACTIVITY = 1;

    boolean areWeAddingOrEditngACustomActivity;

    View caloriesConsumedAddAndEditView;
    PopupWindow caloriesConsumedAddAndEditPopUpWindow;
    TextView typeOfFoodHeaderTextView;
    TextView caloriesConsumedHeaderTextView;
    EditText typeOfFoodEditText;
    EditText caloriesConsumedEditText;
    TextView multipleDayEditWarningForFoodConsumedTextView;

    Button confirmCaloriesConsumedAdditionWithinPopUpButton;
    Button confirmCaloriesConsumedDeletionWithinPopUpButton;

    TextView totalExpendedCaloriesComparedBmrHeader;
    TextView totalExpendedCaloriesComparedActivitiesHeader;

    TextView totalConsumedCaloriesCompared;
    TextView totalExpendedCaloriesCompared;
    TextView totalExpendedCaloriesComparedBmr;
    TextView totalExpendedCaloriesComparedActivities;
    TextView totalCaloriesDifferenceCompared;
    TextView totalWeightDifferenceCompared;

    int ADDING_FOOD = 0;
    int EDITING_FOOD = 1;

    ConstraintLayout caloriesComparedLayout;

    int selectedTdeeCategoryPosition;
    int selectedTdeeSubCategoryPosition;
    double metScore;

    Toast mToast;

    boolean areActivityStatsSimplified;
    ConstraintLayout simplifiedStatsLayout;
    TextView simplifiedActivityLevelTextView;
    TextView simplifiedCaloriesBurnedTextView;

    DividerItemDecoration activityRecyclerDivider;

    boolean fragmentIsAttached;
    boolean statsHaveBeenEditedForCurrentDay;

    int mSelectedTab;
    int STATS_MENU = 1;
    int FILLER_MENU = 2;

    int mActivitySortMode;
    int mFoodConsumedSortMode;
    int SORTING_ACTIVITIES = 2;
    int SORTING_FOOD_CONSUMED = 3;
    int DISABLE_SORTING = -1;

    View aboutStatsPopUpView;
    PopupWindow aboutStatsPopUpWindow;

    changeOnOptionsItemSelectedMenu mChangeOnOptionsItemSelectedMenu;
    changeSortMenu mChangeSortMenu;

    int phoneHeight;
    int phoneWidth;

    InputMethodManager inputMethodManager;

    CalendarDayDecorators.ActivityDecoration activityDecoration;
    CalendarDayDecorators.DaySelectedDecoration daySelectedDecoration;

    public interface changeOnOptionsItemSelectedMenu {
        void onChangeOnOptionsMenu(int typeOfSort);
    }

    public void setOnOptionsMenu(changeOnOptionsItemSelectedMenu xChangeOnOptionsItemSelectedMenu) {
        this.mChangeOnOptionsItemSelectedMenu = xChangeOnOptionsItemSelectedMenu;
    }

    public interface changeSortMenu {
        void onChangeSortMenu(int menuNumber);
    }

    public void setSortMenu(changeSortMenu xChangeSortMenu) {
        this.mChangeSortMenu = xChangeSortMenu;
    }

    @Override
    public void onResume() {
        super.onResume();

        mHandler.postDelayed(()-> {
            if (addTdeePopUpWindow.isShowing()) {
                inputMethodManager.hideSoftInputFromWindow(addTDEEPopUpView.getWindowToken(), 0);
            }
            if (tdeeEditPopUpWindow.isShowing()) {
                inputMethodManager.hideSoftInputFromWindow(tdeeEditView.getWindowToken(), 0);
            }
            if (caloriesConsumedAddAndEditPopUpWindow.isShowing()) {
                inputMethodManager.hideSoftInputFromWindow(caloriesConsumedAddAndEditView.getWindowToken(), 0);
            }
            if (aboutStatsPopUpWindow.isShowing()) {
                inputMethodManager.hideSoftInputFromWindow(aboutStatsPopUpView.getWindowToken(), 0);
            }
        }, 300);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dailyStatsExpandedPopUpWindow.isShowing()) {
            dailyStatsExpandedPopUpWindow.dismiss();
        }
    }

    private void setPhoneDimensions() {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        phoneHeight = metrics.heightPixels;
        phoneWidth = metrics.widthPixels;

        Log.i("testDimensions", "height is " + phoneHeight + " and width is " + phoneWidth);
    }

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        setPhoneDimensions();

        View root;

        if (phoneHeight <= 1920) {
            root = inflater.inflate(R.layout.daily_stats_fragment_layout_h1920, container, false);
        } else {
            root = inflater.inflate(R.layout.daily_stats_fragment_layout, container, false);
        }

        mRoot = root;
        fragmentIsAttached = true;

        mToast = new Toast(getContext());
        dailyStatsAccess = new DailyStatsAccess(getContext());

        dailyStatsExpandedButton = mRoot.findViewById(R.id.daily_stats_expanded_button);
        editTdeeStatsButton = mRoot.findViewById(R.id.edit_tdee_stats_button);

        activityDecoration = new CalendarDayDecorators.ActivityDecoration(getContext());
        daySelectedDecoration = new CalendarDayDecorators.DaySelectedDecoration(getContext());

        instantiateCalendarObjects();
        instantiateTextViewsAndMiscClasses();
        instantiateTabLayout();
        instantiateActivityRecyclerViewAndItsAdapter();
        instantiateCalorieConsumptionRecyclerAndItsAdapter();
        instantiateCalendarAnimations();

        instantiateActivityEditPopUpViews();
        instantiateAddPopUpViews();
        instantiateActivityAdditionSpinnersAndAdapters();
        setTdeeSpinnerListeners();

        instantiateCaloriesConsumedEditPopUpViews();
        instantiateCaloriesComparedViews();
        instantiateCalorieTabLayoutListenerAndViews();
        instantiateAboutStatsPopUpViews();

        instantiateExpansionPopUpViews();
        setValueCappingTextWatcherOnEditTexts();
        setTextWatchersOnActivityEditTexts();

        setCalendarAnimationListeners();

        areActivityStatsSimplified = sharedPref.getBoolean("areActivityStatsSimplified", false);
        toggleSimplifiedStatsButtonView(false);
        toggleSimplifiedStatViewsWithinActivityTab(false);
        setSimplifiedViewTextViews();

        AsyncTask.execute(()-> {
            int selectedDay = mCalendar.get(Calendar.DAY_OF_YEAR);
            int valueToAddForFutureYears = valueToAddForFutureYears();
            daySelectedFromCalendar = selectedDay + valueToAddForFutureYears;
            dailyStatsAccess.setCalendarObjectSelectedFromFragment(mCalendar);

            daySelectedAsACalendarDayObject = CalendarDay.from(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH) + 1, mCalendar.get(Calendar.DAY_OF_MONTH));
            customCalendarDayList = Collections.singletonList(daySelectedAsACalendarDayObject);

            populateListsAndTextViewsFromEntityListsInDatabase();
            colorDaysWithAtLeastOneActivity();

            getActivity().runOnUiThread(()-> {
                daySelectedDecoration.setCurrentDay(customCalendarDayList);
                calendarView.addDecorator(daySelectedDecoration);
                setStatDurationViews(currentStatDurationMode);
            });
        });

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                AsyncTask.execute(()->{
                    mCalendar = Calendar.getInstance(Locale.getDefault());
                    mCalendar.set(date.getYear(), date.getMonth()-1, date.getDay());

                    dailyStatsAccess.setCalendarObjectSelectedFromFragment(mCalendar);

                    int selectedDay = mCalendar.get(Calendar.DAY_OF_YEAR);
                    int valueToAddForFutureYears = valueToAddForFutureYears();
                    dailyStatsAccess.setValueAddedToSelectedDaysForFutureYears(valueToAddForFutureYears);

                    daySelectedFromCalendar = selectedDay + valueToAddForFutureYears;

                    daySelectedAsACalendarDayObject = date;

                    customCalendarDayList = Collections.singletonList(date);

                    calendarDateChangeLogic();
                    populateListsAndTextViewsFromEntityListsInDatabase();

                    getActivity().runOnUiThread(()-> {
                        setActivityStatsDurationRangeTextView();
                        setSelectionDayIfSelectingSingleDayFromCustomDuration();
                    });
                });
            }
        });

        calendarView.setOnRangeSelectedListener(new OnRangeSelectedListener() {
            @Override
            public void onRangeSelected(@NonNull MaterialCalendarView widget, @NonNull List<CalendarDay> dates) {
                AsyncTask.execute(()->{
                    mCalendar = Calendar.getInstance(Locale.getDefault());
                    customCalendarDayList = dates;
                    daySelectedAsACalendarDayObject = dates.get(0);

                    mCalendar.set(daySelectedAsACalendarDayObject.getYear(), daySelectedAsACalendarDayObject.getMonth()-1, daySelectedAsACalendarDayObject.getDay());

                    int selectedDay = mCalendar.get(Calendar.DAY_OF_YEAR);
                    int valueToAddForFutureYears = valueToAddForFutureYears();
                    dailyStatsAccess.setValueAddedToSelectedDaysForFutureYears(valueToAddForFutureYears);

                    daySelectedFromCalendar = selectedDay + valueToAddForFutureYears;
                    dailyStatsAccess.setCalendarObjectSelectedFromFragment(mCalendar);

                    dailyStatsAccess.setValueAddedToSelectedDaysForFutureYears(valueToAddForFutureYears());

                    populateListsAndTextViewsFromEntityListsInDatabase();

                    getActivity().runOnUiThread(()-> {
                        convertAndSetDateRangeStringOnTextView();
                    });
                });
            }
        });

        activityStatsDurationSwitcherButtonLeft.setOnClickListener(v-> {
            statDurationSwitchModeLogic(ITERATING_ACTIVITY_STATS_DOWN);
        });

        activityStatsDurationSwitcherButtonRight.setOnClickListener(v-> {
            statDurationSwitchModeLogic(ITERATING_ACTIVITY_STATS_UP);
        });

        addActivityConfirmButton.setOnClickListener(v-> {
            setNewActivityVariablesAndCheckIfActivityExists(false);
        });

        addCustomCaloriesPerHourTextView.setOnClickListener(v->{
            setTextStyleAndAlphaValuesOnTextViews(addCustomCaloriesPerHourTextView, true);
            setTextStyleAndAlphaValuesOnTextViews(addCustomCaloriesPerMinuteTextView, false);
        });

        confirmCustomActivityAdditionValues.setOnClickListener(v-> {
            customActivityPopUpWindow.dismiss();
            setNewActivityVariablesAndCheckIfActivityExists(true);
        });

        cancelCustomActivityValues.setOnClickListener(v-> {
            customActivityPopUpWindow.dismiss();
        });

        editTdeeStatsButton.setOnClickListener(v-> {
            if (dailyStatsRecyclerView.isShown()) {
                dailyStatsAdapter.toggleEditMode();
                dailyStatsAdapter.notifyDataSetChanged();
                scrollToBottomOfDailyStatsRecycler();
            }
            if (caloriesConsumedRecyclerView.isShown()) {
                caloriesConsumedAdapter.toggleEditMode();
                caloriesConsumedAdapter.notifyDataSetChanged();
                scrollToBottomOfCaloriesConsumedRecycler();
            }
        });

        confirmActivityEditWithinPopUpButton.setOnClickListener(v-> {
            if (dailyStatsAdapter.getAddingOrEditingActivityVariable()==ADDING_ACTIVITY) {
                addActivityStatsInDatabase(areWeAddingOrEditngACustomActivity);
            }
            if (dailyStatsAdapter.getAddingOrEditingActivityVariable()==EDITING_ACTIVITY) {
                editActivityStatsInDatabase();
            }
        });

        deleteActivityIfEditingRowWithinEditPopUpButton.setOnClickListener(v-> {
            if (dailyStatsAdapter.getAddingOrEditingActivityVariable()==EDITING_ACTIVITY) {
                deleteActivityFromStats(mPositionToEdit);
            }
            tdeeEditPopUpWindow.dismiss();
        });

        confirmCaloriesConsumedAdditionWithinPopUpButton.setOnClickListener(v-> {
            addOrEditFoodInStats();
        });

        confirmCaloriesConsumedDeletionWithinPopUpButton.setOnClickListener(v-> {
            deleteFoodInStatsIfInEditMode();
        });

        minimizeCalendarButton.setOnClickListener(v-> {
            calendarMinimizationButtonLogic(false);
        });

        dailyStatsExpandedButton.setOnClickListener(v-> {
            areActivityStatsSimplified = !areActivityStatsSimplified;

            prefEdit.putBoolean("areActivityStatsSimplified", areActivityStatsSimplified);
            prefEdit.apply();

            toggleSimplifiedStatsButtonView(areActivityStatsSimplified);

            if (caloriesComparisonTabLayout.getSelectedTabPosition()==0) {
                toggleSimplifiedStatViewsWithinActivityTab(areActivityStatsSimplified);
            }

            if (caloriesComparisonTabLayout.getSelectedTabPosition()==2) {
                toggleSimplifiedViewsWithinComparisonTab(areActivityStatsSimplified);
            }

            setTotalCaloriesComparedTextViews(false);
        });

        return root;
    }

    public void executeTurnOffEditModeMethod() {
        dailyStatsAdapter.turnOffEditMode();
        caloriesConsumedAdapter.turnOffEditMode();
    }

    public void setStatsHaveBeenEditedForCurrentDay(boolean haveBeenEdited) {
        statsHaveBeenEditedForCurrentDay = haveBeenEdited;
    }

    public boolean getHaveStatsBeenEditedForCurrentDay() {
        return statsHaveBeenEditedForCurrentDay;
    }

    public void setIsFragmentAttached(boolean attached) {
        this.fragmentIsAttached = attached;
    }
    public boolean getIsFragmentAttached() {
        return fragmentIsAttached;
    }

    private void toggleActivityEditingForMultipleDaysTextViews() {
        if (dailyStatsAccess.getNumberOfDaysSelected() > 1) {
            unassignedTimeInEditPopUpTextView.setVisibility(View.INVISIBLE);
            multipleDayWarningForActivitiesTextView.setVisibility(View.VISIBLE);
        } else {
            unassignedTimeInEditPopUpTextView.setVisibility(View.VISIBLE);
            multipleDayWarningForActivitiesTextView.setVisibility(View.INVISIBLE);
        }
    }

    private void toggleLayoutParamsForMultipleDays() {
        if (dailyStatsAccess.getNumberOfDaysSelected() > 1) {
            editTextLayoutParamsForActivityEdit.topToBottom = R.id.activity_string_in_edit_popUp;
            editTextLayoutParamsForActivityEdit.bottomToTop = R.id.multiple_day_warning_for_activities_textView;
            editTextLayoutParamsForActivityEdit.topMargin = dpToPxConv(0);
            editTextLayoutParamsForActivityEdit.bottomMargin = dpToPxConv(0);

            activityInEditPopUpTextViewLayoutParams.topMargin = dpToPxConv(20);
        } else {
            editTextLayoutParamsForActivityEdit.topToBottom = R.id.activity_string_in_edit_popUp;
            editTextLayoutParamsForActivityEdit.topMargin = dpToPxConv(20);
            editTextLayoutParamsForActivityEdit.bottomMargin = dpToPxConv(0);

            activityInEditPopUpTextViewLayoutParams.topMargin = dpToPxConv(50);

        }
    }

    private void toggleMultipleDayWarningTextViewForActivities(int addingOrEditing) {
        if (addingOrEditing == ADDING_ACTIVITY) {
            multipleDayWarningForActivitiesTextView.setText(R.string.multiple_day_activity_addition_warning);
        }
        if (addingOrEditing == EDITING_ACTIVITY) {
            multipleDayWarningForActivitiesTextView.setText(R.string.multiple_day_activity_edit_warning);
        }
    }

    private void toggleStringOnConfirmButtonForAddingOrEditingActivity(int addingOrEditing) {
        if (addingOrEditing == ADDING_ACTIVITY) {
            confirmActivityEditWithinPopUpButton.setText(R.string.save);
        }
        if (addingOrEditing == EDITING_ACTIVITY) {
            confirmActivityEditWithinPopUpButton.setText(R.string.update);
        }
    }

    private void toggleFoodConsumedEditingForMultipleDaysTextViews() {
        if (dailyStatsAccess.getNumberOfDaysSelected() > 1) {
            multipleDayEditWarningForFoodConsumedTextView.setVisibility(View.VISIBLE);
            setTextViewSizesForMultipleDayWarningForFoodConsumption(true);
        } else {
            multipleDayEditWarningForFoodConsumedTextView.setVisibility(View.GONE);
            setTextViewSizesForMultipleDayWarningForFoodConsumption(false);
        }
    }

    private void toggleMultipleDayWarningTextViewForFoodConsumption(int addingOrEditing) {
        if (addingOrEditing == ADDING_FOOD) {
            multipleDayEditWarningForFoodConsumedTextView.setText(R.string.multiple_food_addition_warning);
        }
        if (addingOrEditing == EDITING_FOOD) {
            multipleDayEditWarningForFoodConsumedTextView.setText(R.string.multiple_food_edit_warning);
        }
    }

    private void toggleStringOnConfirmButtonForAddingOrEditingFood(int addingOrEditing) {
        if (addingOrEditing == ADDING_FOOD) {
            confirmCaloriesConsumedAdditionWithinPopUpButton.setText(R.string.save);
        }
        if (addingOrEditing == EDITING_FOOD) {
            confirmCaloriesConsumedAdditionWithinPopUpButton.setText(R.string.update);
        }
    }

    private void setTextViewSizesForMultipleDayWarningForFoodConsumption(boolean warningIsVisible) {
        if (!warningIsVisible) {
            typeOfFoodHeaderTextView.setTextSize(28f);
            caloriesConsumedHeaderTextView.setTextSize(28f);
        } else {
            typeOfFoodHeaderTextView.setTextSize(24f);
            caloriesConsumedHeaderTextView.setTextSize(24f);
        }
    }

    private void toggleEditButtonView(boolean buttonDisabled) {
        if (!buttonDisabled) {
            editTdeeStatsButton.setAlpha(1.0f);
            editTdeeStatsButton.setEnabled(true);
        } else {
            editTdeeStatsButton.setAlpha(0.3f);
            editTdeeStatsButton.setEnabled(false);
        }
    }

    private void toggleSimplifiedStatsButtonView(boolean areSimplified) {
        dailyStatsExpandedButton.setVisibility(View.GONE);
        if (!areSimplified) {
            dailyStatsExpandedButton.setImageResource(R.drawable.collapse_1);
        } else {
            dailyStatsExpandedButton.setImageResource(R.drawable.expand_1);
        }
    }

    private void toggleSimplifiedStatViewsWithinActivityTab(boolean areSimplified) {
        if (!areSimplified) {
            dailyStatsRecyclerView.setVisibility(View.VISIBLE);
            totalActivityStatsValuesTextViewLayout.setVisibility(View.VISIBLE);
            simplifiedStatsLayout.setVisibility(View.INVISIBLE);
        } else {
            dailyStatsRecyclerView.setVisibility(View.INVISIBLE);
            totalActivityStatsValuesTextViewLayout.setVisibility(View.INVISIBLE);
            simplifiedStatsLayout.setVisibility(View.VISIBLE);
        }
    }

    private void toggleSimplifiedViewsWithinComparisonTab(boolean areSimplified) {
        if (!areSimplified) {
            totalExpendedCaloriesComparedBmrHeader.setVisibility(View.VISIBLE);
            totalExpendedCaloriesComparedActivitiesHeader.setVisibility(View.VISIBLE);
            totalExpendedCaloriesComparedActivities.setVisibility(View.VISIBLE);
            totalExpendedCaloriesComparedBmr.setVisibility(View.VISIBLE);
        } else {
            totalExpendedCaloriesComparedBmrHeader.setVisibility(View.INVISIBLE);
            totalExpendedCaloriesComparedActivitiesHeader.setVisibility(View.INVISIBLE);
            totalExpendedCaloriesComparedActivities.setVisibility(View.INVISIBLE);
            totalExpendedCaloriesComparedBmr.setVisibility(View.INVISIBLE);
        }
    }

    private void setSimplifiedViewTextViews() {
        int activityLevelPosition = sharedPref.getInt("activityLevelPosition", 0);

        String activityLevelString = sharedPref.getString("activityLevelString", getString(R.string.act_0));
        double caloriesExpended = dailyStatsAccess.bmrCaloriesBurned();

        simplifiedActivityLevelTextView.setText(activityLevelString);
        simplifiedCaloriesBurnedTextView.setText(formatDoubleToStringWithoutDecimals(caloriesExpended));
    }

    public void scrollToBottomOfDailyStatsRecycler() {
        dailyStatsRecyclerView.smoothScrollToPosition(dailyStatsAdapter.getItemCount()-1);
    }

    public void scrollToBottomOfCaloriesConsumedRecycler() {
        caloriesConsumedRecyclerView.smoothScrollToPosition(caloriesConsumedAdapter.getItemCount()-1);
    }

    private void addOrEditFoodInStats() {
        if (caloriesConsumedAdapter.getAddingOrEditingFoodVariable()==ADDING_FOOD) {
            addFoodToStats();
        }
        if (caloriesConsumedAdapter.getAddingOrEditingFoodVariable()==EDITING_FOOD) {
            editFoodInStats();
        }
    }

    private void deleteFoodInStatsIfInEditMode() {
        if (caloriesConsumedAdapter.getAddingOrEditingFoodVariable()==EDITING_FOOD) {
            deleteConsumedCalories(mPositionToEdit);
        } else {
            caloriesConsumedAddAndEditPopUpWindow.dismiss();
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
        dailyStatsAdapter.getItemCount();
        caloriesConsumedAdapter.getItemCount();

        dailyStatsAdapter.turnOffEditMode();
        caloriesConsumedAdapter.turnOffEditMode();
    }

    private void setSelectionDayIfSelectingSingleDayFromCustomDuration() {
        if (currentStatDurationMode==CUSTOM_STATS) {
            calendarView.setSelectedDate(daySelectedAsACalendarDayObject);
        }
    }

    //At present, we're keeping this query of both entities on each execution. They are intertwined in our access class.
    public void populateListsAndTextViewsFromEntityListsInDatabase() {
        setDayAndStatsForEachActivityEntityListsForChosenDurationOfDays(currentStatDurationMode);

        dailyStatsAccess.clearStatsForEachActivityArrayLists();
        dailyStatsAccess.setTotalActivityStatsForSelectedDaysToArrayLists();
        dailyStatsAccess.setTotalSetTimeVariableForSelectedDuration();
        dailyStatsAccess.setTotalCaloriesVariableForSelectedDuration();

        dailyStatsAccess.clearFoodConsumedArrayLists();
        dailyStatsAccess.setTotalFoodConsumedForSelectedDaysToArrayLists();

        dailyStatsAccess.setAggregateTimeForSelectedDuration();
        dailyStatsAccess.setUnassignedTotalCalories();

        dailyStatsAccess.setAggregateCaloriesForSelectedDuration();
        dailyStatsAccess.setUnassignedDailyTotalTime();

        dailyStatsAccess.setCalendarObjectSelectedFromFragment(mCalendar);

        if (numberOfDaysWithActivitiesHasChanged) {
            colorDaysWithAtLeastOneActivity();
            numberOfDaysWithActivitiesHasChanged = false;
        }

        getActivity().runOnUiThread(()-> {
            dailyStatsAdapter.notifyDataSetChanged();
            caloriesConsumedAdapter.notifyDataSetChanged();

            setTotalActivityStatsFooterTextViews();
            setTotalCaloriesConsumedFooterTextViews();

            setTotalCaloriesComparedTextViews(false);
        });
    }

    //Here is where we access database for both entities. We would have to changes our fetches based on tab selection in order to query for only selected tab, and then call populateListsAndTextViewsFromEntityListsInDatabase() on each tab switch.
    private void setDayAndStatsForEachActivityEntityListsForChosenDurationOfDays(int mode) {
        if (mode==DAILY_STATS) {
            dailyStatsAccess.setAllDayAndStatListsForSingleDay(mCalendar.get(Calendar.DAY_OF_YEAR));
        }
        if (mode==WEEKLY_STATS) {
            dailyStatsAccess.setAllDayAndStatListsForWeek(mCalendar.get(Calendar.DAY_OF_WEEK), mCalendar.get(Calendar.DAY_OF_YEAR));
        }
        if (mode==MONTHLY_STATS) {
            dailyStatsAccess.setAllDayAndStatListsForMonth((mCalendar.get(Calendar.DAY_OF_MONTH)), mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH), mCalendar.get(Calendar.DAY_OF_YEAR));
        }
//        if (mode==YEAR_TO_DATE_STATS) {
//            dailyStatsAccess.setAllDayAndStatListsForYearFromDatabase(mCalendar.getActualMaximum(Calendar.DAY_OF_YEAR), true);
//        }
//        if (mode==YEARLY_STATS) {
//            dailyStatsAccess.setAllDayAndStatListsForYearFromDatabase(mCalendar.getActualMaximum(Calendar.DAY_OF_YEAR), false);
//        }
        if (mode==CUSTOM_STATS) {
            dailyStatsAccess.setAllDayAndStatListsForCustomDatesFromDatabase(customCalendarDayList, mCalendar.get(Calendar.DAY_OF_YEAR));
        }

        setListOfStatsForEachActivity();
        setListOfFoods();
    }


    private void setStatDurationViews(int mode) {
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
//        if (mode==YEAR_TO_DATE_STATS) {
//            totalStatsHeaderTextView.setText(R.string.year_to_date_header);
//            convertAndSetDateRangeStringOnTextView();
//        }
//        if (mode==YEARLY_STATS) {
//            totalStatsHeaderTextView.setText(R.string.yearly_total_header);
//            convertAndSetDateRangeStringOnTextView();
//        }
        if (mode==CUSTOM_STATS) {
            totalStatsHeaderTextView.setText(R.string.custom_total_header);
            calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE);
            convertAndSetDateRangeStringOnTextView();
        }

        calendarView.setSelectedDate(daySelectedAsACalendarDayObject);
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
            if (currentStatDurationMode<3) {
                currentStatDurationMode++;
            } else {
                currentStatDurationMode=0;
            }
        } else if (directionOfIteration==ITERATING_ACTIVITY_STATS_DOWN) {
            if (currentStatDurationMode>0) {
                currentStatDurationMode--;
            } else {
                currentStatDurationMode=3;
            }
        }
    }

    public void colorDaysWithAtLeastOneActivity() {
        List<Integer> daysWithAtLeastOneActivityList = dailyStatsAccess.getListOfDaysWithAtLeastOneActivity();

        getActivity().runOnUiThread(()->{
            List<CalendarDay> calendarDayList = new ArrayList<>();
            Calendar calendarColoringObject = Calendar.getInstance(Locale.getDefault());

            for (int i=0; i<daysWithAtLeastOneActivityList.size(); i++) {
                calendarColoringObject.set(Calendar.DAY_OF_YEAR, daysWithAtLeastOneActivityList.get(i));

                calendarDayList.add(CalendarDay.from(calendarColoringObject.get(Calendar.YEAR), calendarColoringObject.get(Calendar.MONTH) + 1, calendarColoringObject.get(Calendar.DAY_OF_MONTH)));
            }

            activityDecoration.setCalendarDayList(calendarDayList);
            calendarView.addDecorator(activityDecoration);
        });
    }

    public void colorDaysWithAtLeastOneFood() {

    }

    private void setSingleDateStringOnTextView() {
        String dayToSet = dailyStatsAccess.getFirstDayInDurationAsString();
        activityStatsDurationRangeTextView.setText(dayToSet);
    }

    private void convertAndSetDateRangeStringOnTextView() {
        String firstDay = dailyStatsAccess.getFirstDayInDurationAsString();
        String lastDay = String.valueOf(dailyStatsAccess.getLastDayInDurationAsString());
        activityStatsDurationRangeTextView.setText(getString(R.string.date_duration_textView, firstDay, lastDay));
    }

    public void setNumberOfDaysWithActivitiesHasChangedBoolean(boolean numberOfDaysHaveChanged) {
        this.numberOfDaysWithActivitiesHasChanged = numberOfDaysHaveChanged;
    }

    public void setActivitySortMode(int sortMode) {
        this.mActivitySortMode = sortMode;
    }

    public void setFoodConsumedSortMode(int sortMode) {
        this.mFoodConsumedSortMode = sortMode;
    }

    public void sortActivityStatsAsACallFromMainActivity() {
        dailyStatsAccess.setActivitySortMode(mActivitySortMode);
        populateListsAndTextViewsFromEntityListsInDatabase();
    }

    public void sortFoodConsumedStatsAsACallFromMainActivity() {
        dailyStatsAccess.setFoodConsumedSortMode(mFoodConsumedSortMode);
        populateListsAndTextViewsFromEntityListsInDatabase();;
    }

    private void setListOfStatsForEachActivity() {
        statsForEachActivityList = dailyStatsAccess.getStatsForEachActivityList();
    }

    public List<StatsForEachActivity> getStatsForEachActivityList() {
        return statsForEachActivityList;
    }

    public void setListOfFoods() {
        caloriesForEachFoodList = dailyStatsAccess.getCaloriesForEachFoodList();
    }

    public List<CaloriesForEachFood> getCaloriesForEachFoodList() {
        return caloriesForEachFoodList;
    }

    private void setTotalActivityStatsFooterTextViews() {
        long totalActivityTime = dailyStatsAccess.getTotalActivityTimeForSelectedDuration();
        double totalActivityCalories = dailyStatsAccess.getTotalCaloriesBurnedForSelectedDuration();

        String activityTimeString = longToStringConverters.convertMillisToHourBasedString(totalActivityTime);
        String activityCalorieString = formatDoubleToStringWithoutDecimals(totalActivityCalories);

        long totalBmrTime = dailyStatsAccess.getUnassignedSetTimeForSelectedDuration();
        double totalBmrCalories = dailyStatsAccess.getUnassignedCaloriesForSelectedDuration();

        String bmrTimeString = longToStringConverters.convertMillisToHourBasedString(totalBmrTime);
        String bmrCaloriesString = formatDoubleToStringWithoutDecimals(totalBmrCalories);

        long totalExpendedTime = dailyStatsAccess.getAggregateTimeForSelectedDuration();
        double totalExpendedCalories = dailyStatsAccess.getAggregateCaloriesForSelectedDuration();

        String expendedTimeString = longToStringConverters.convertMillisToHourBasedString(totalExpendedTime);
        String expendedCaloriesString = formatDoubleToStringWithoutDecimals(totalExpendedCalories);

        dailyStatsTotalActivityTimeTextView.setText(activityTimeString);
        dailyStatsTotalActivityCaloriesBurnedTextView.setText(activityCalorieString);

        dailyStatsBmrTimeTextView.setText(bmrTimeString);
        dailyStatsBmrCaloriesTextView.setText(bmrCaloriesString);

        dailyTotalExpendedTimeElapsedTextView.setText(expendedTimeString);
        dailyTotalExpendedCaloriesBurnedTextView.setText(expendedCaloriesString);
    }

    private double roundDownCalories(double calories) {
        return Math.floor(calories);
    }

    private int valueToAddForFutureYears() {
        int additionModifier = 0;
        int year = mCalendar.get(Calendar.YEAR);
        int numberOfYearsToAdd = (year - 2022);

        for (int i=0; i<numberOfYearsToAdd; i++) {
            additionModifier += mCalendar.getActualMaximum(Calendar.DAY_OF_YEAR);
        }

        return additionModifier;
    }

    @Override
    public void onAddingActivity(int position) {
        if (dailyStatsAccess.getNumberOfDaysSelected()==1) {
            if (dailyStatsAccess.getUnassignedSetTimeForSelectedDuration() == 0) {
                showToastIfNoneActive("No time remaining in day!");
                return;
            }
        }

        setDefaultCustomActivityAdditionViews();
        toggleActivityEditingForMultipleDaysTextViews();
        toggleLayoutParamsForMultipleDays();

        addTdeePopUpWindow.showAsDropDown(topOfRecyclerViewAnchor, 0, dpToPxConv(0), Gravity.TOP);
    }

    private void setNewActivityVariablesAndCheckIfActivityExists(boolean customActivity) {
        AsyncTask.execute(()-> {
            String activityToAdd = "";

            if (!customActivity) {
                activityToAdd = tdeeChosenActivitySpinnerValues.getSubCategoryListOfStringArrays().get(selectedTdeeCategoryPosition)[selectedTdeeSubCategoryPosition];

                dailyStatsAccess.setMetScoreFromSpinner(retrieveMetScoreFromSubCategoryPosition());
            } else {
                activityToAdd = addCustomActivityEditText.getText().toString().trim();
            }

            dailyStatsAccess.setActivityString(activityToAdd);

            if (dailyStatsAccess.getNumberOfDaysSelected() > 1) {
                getActivity().runOnUiThread(()-> {
                    populateActivityEditPopUpWithNewRow();
                });
            } else {
                getActivity().runOnUiThread(()-> {
                    launchEditPopUpIfActivityDoesNotExistAndToastIfItDoes(dailyStatsAccess.doesActivityExistsForSpecificDay());
                });
            }
        });
    }

    private void launchEditPopUpIfActivityDoesNotExistAndToastIfItDoes(boolean activityExists) {
        if (!activityExists) {
            populateActivityEditPopUpWithNewRow();

            if (addTdeePopUpWindow.isShowing()) {
                addTdeePopUpWindow.dismiss();
            }
            if (customActivityPopUpWindow.isShowing()) {
                customActivityPopUpWindow.dismiss();
            }
        } else {
            showToastIfNoneActive("Activity exists!");
        }
    }

    private void addActivityStatsInDatabase(boolean customActivity) {
        AsyncTask.execute(()-> {
            long newActivityTime = 0;
            double newCaloriesBurned = 0;
            double caloriesBurnedPerHour;

            newActivityTime = getMillisValueToSaveFromEditTextString();

            if (newActivityTime==0) {
                getActivity().runOnUiThread(()-> {
                    showToastIfNoneActive("Time cannot be empty!");
                });
                return;
            }

            //These are the same across all days, so we can just set them once here.
            if (!customActivity) {
                //Activity String is already set from our spinner popUp.
                dailyStatsAccess.setIsActivityCustomBoolean(false);
            } else {
                dailyStatsAccess.setActivityString(addCustomActivityEditText.getText().toString().trim());
                dailyStatsAccess.setIsActivityCustomBoolean(true);
            }

            //Strings set in Access class above.
            String activityString = dailyStatsAccess.getActivityStringVariable();
            long finalNewActivityTime = newActivityTime;
            double finalNewCaloriesBurned = newCaloriesBurned;

            List<Integer> listOfActivityDaysSelected = dailyStatsAccess.getListOfActivityDaysSelected();
            List<Long> listOfUnassignedTimes = dailyStatsAccess.getListOfUnassignedTimeForMultipleDays();

            for (int i=0; i<listOfActivityDaysSelected.size(); i++) {
                int uniqueIdToCheck = dailyStatsAccess.getListOfActivityDaysSelected().get(i);
                long assignedTime = getAssignedTimesFromSpecificActivityForSelectedDays(uniqueIdToCheck, activityString);
                long unassignedTime = listOfUnassignedTimes.get(i);

                //New instance to be capped for each iteration of loop.
                finalNewActivityTime = newActivityTime;

                if (!customActivity) {
                    finalNewCaloriesBurned = calculateCaloriesFromMillisValueUsingMetScore(finalNewActivityTime);
                } else {
                    finalNewCaloriesBurned = calculateCaloriesForCustomActivityAddition(finalNewActivityTime);
                }

                if (!dailyStatsAccess.doesActivityExistInDatabaseForSelectedDay(uniqueIdToCheck)) {
                    finalNewActivityTime = cappedActivityTimeForStatInsertions(finalNewActivityTime, unassignedTime);
                    finalNewCaloriesBurned = activityCaloriesForSpinnerOrCustom(customActivity, finalNewActivityTime);

                    if (!isTimeToSaveZero(finalNewActivityTime)) {
                        dailyStatsAccess.insertStatsForEachActivityRow(uniqueIdToCheck, finalNewActivityTime, finalNewCaloriesBurned);
                    }

                } else {
                    finalNewActivityTime = cappedTimeForStatEdits(finalNewActivityTime, assignedTime, unassignedTime);
                    finalNewCaloriesBurned = activityCaloriesForSpinnerOrCustom(customActivity, finalNewActivityTime);

                    dailyStatsAccess.updateTotalTimesAndCaloriesForEachActivityFromDayId(uniqueIdToCheck, finalNewActivityTime, finalNewCaloriesBurned);
                }
            }

            numberOfDaysWithActivitiesHasChanged = true;

            populateListsAndTextViewsFromEntityListsInDatabase();

            getActivity().runOnUiThread(()-> {
                showToastIfNoneActive("Saved!");
                tdeeEditPopUpWindow.dismiss();
            });
        });
    }

    private double activityCaloriesForSpinnerOrCustom(boolean spinnerActiviy, long activityTime) {
        double valueToReturn = 0;

        if (!spinnerActiviy) {
            valueToReturn = calculateCaloriesFromMillisValueUsingMetScore(activityTime);
        } else {
            valueToReturn = calculateCaloriesForCustomActivityAddition(activityTime);
        }
        return valueToReturn;
    }

    private boolean isTimeToSaveZero(long activityTime) {
        return activityTime == 0;
    }

    private long getOldActivityTimeForSpecificActivityOnSelectedDay(int daySelected) {
        long valueToReturn = 0;

        List<StatsForEachActivity> statsForEachActivityListForDay = dailyStatsAccess.getStatsForEachActivityListForSelectedDay(daySelected);

        for (int i=0; i<statsForEachActivityListForDay.size(); i++) {
            if (statsForEachActivityListForDay.get(i).getActivity().equalsIgnoreCase(dailyStatsAccess.getActivityStringVariable())) {
                valueToReturn = statsForEachActivityListForDay.get(i).getTotalSetTimeForEachActivity();
            }
        }

        return valueToReturn;
    }

    private void populateActivityEditPopUpWithNewRow() {
        replaceActivityAddPopUpWithEmptyEditPopUp();

        String activityToAdd = dailyStatsAccess.getActivityStringVariable();

        activityInEditPopUpTextView.setText(activityToAdd);
        zeroOutActivityEditPopUpEditTexts();
        toggleMultipleDayWarningTextViewForActivities(ADDING_ACTIVITY);
        toggleStringOnConfirmButtonForAddingOrEditingActivity(ADDING_ACTIVITY);
    }

    private void replaceActivityAddPopUpWithEmptyEditPopUp() {
        addTdeePopUpWindow.dismiss();

        setActivityEditPopUpTimeRemainingTextView();
        toggleCancelOrDeleteButtonInEditPopUpTextView(ADDING_ACTIVITY);

        tdeeEditTextHours.requestFocus();

        tdeeEditPopUpWindow.showAsDropDown(topOfRecyclerViewAnchor, 0, dpToPxConv(-5));
    }

    @Override
    public void activityEditItemSelected(int position) {
        this.mPositionToEdit = position;
        launchActivityEditPopUpWithEditTextValuesSet(position);
        setDefaultCustomActivityAdditionViews();

        toggleActivityEditingForMultipleDaysTextViews();
        toggleLayoutParamsForMultipleDays();
        toggleMultipleDayWarningTextViewForActivities(EDITING_ACTIVITY);
        toggleStringOnConfirmButtonForAddingOrEditingActivity(EDITING_ACTIVITY);
    }

    private void editActivityStatsInDatabase() {
        AsyncTask.execute(()-> {
            String newActivityString = "";
            long activityTimeInEditText = 0;
            long newActivityTime = 0;
            double newCaloriesBurned = 0;

            dailyStatsAccess.setStatsForEachActivityEntityFromPosition(mPositionToEdit);
            //Received whenever + icon is clicked (from callback). Rows should already be sorted so doubt this is it.
            dailyStatsAccess.setMetScoreFromDatabaseList(mPositionToEdit);

            newActivityTime = getMillisValueToSaveFromEditTextString();

            if (newActivityTime == 0) {
                getActivity().runOnUiThread(()-> {
                    showToastIfNoneActive("Time cannot be empty!");
                });
                return;
            }

            String activityString = dailyStatsAccess.getTotalActivitiesListForSelectedDuration().get(mPositionToEdit);
            dailyStatsAccess.setActivityString(activityString);

            long finalNewActivityTime = newActivityTime;
            double finalNewCaloriesBurned = newCaloriesBurned;

            List<Integer> listOfActivityDaysSelected = dailyStatsAccess.getListOfActivityDaysSelected();
            List<Long> listOfUnassignedTimes = dailyStatsAccess.getListOfUnassignedTimeForMultipleDays();

            for (int i=0; i<listOfActivityDaysSelected.size(); i++) {
                int uniqueIdToCheck = dailyStatsAccess.getListOfActivityDaysSelected().get(i);
                long assignedTime = getAssignedTimesFromSpecificActivityForSelectedDays(uniqueIdToCheck, activityString);
                long unassignedTime = listOfUnassignedTimes.get(i);
                boolean isCustomActivity = getIsActivityCustomBooleanFromSpecificActivityForSelecteDays(uniqueIdToCheck, activityString);

                //New instance to be capped for each iteration of loop.
                finalNewActivityTime = newActivityTime;
                finalNewActivityTime = cappedTimeForStatEdits(finalNewActivityTime, assignedTime, unassignedTime);

                if (!isCustomActivity) {
                    finalNewCaloriesBurned = calculateCaloriesFromMillisValueUsingMetScore(finalNewActivityTime);
                } else {
                    double caloriesPerHour = getIsCaloriesPerHourFromSpecifiCustomActivityForSelecteDays(uniqueIdToCheck, activityString);
                    finalNewCaloriesBurned = calculateCaloriesForCustomActivityEdit(finalNewActivityTime, caloriesPerHour);
                }

                dailyStatsAccess.updateTotalTimesAndCaloriesForEachActivityFromDayId(uniqueIdToCheck, finalNewActivityTime, finalNewCaloriesBurned);
            }

            populateListsAndTextViewsFromEntityListsInDatabase();
            setStatsHaveBeenEditedForCurrentDay(true);

            getActivity().runOnUiThread(()-> {
                Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
                tdeeEditPopUpWindow.dismiss();
            });
        });
    }

    //These methods simply fetch the mStatsForEachActivity list that has been already pulled from database.
    private long getAssignedTimesFromSpecificActivityForSelectedDays(long uniqueId, String activity) {
        List<StatsForEachActivity> statsForEachActivityList = dailyStatsAccess.getStatsForEachActivityList();
        long valueToReturn = 0;

        for (int i=0; i<statsForEachActivityList.size(); i++) {
            if (statsForEachActivityList.get(i).getUniqueIdTiedToTheSelectedActivity()==uniqueId) {
                if (statsForEachActivityList.get(i).getActivity().equalsIgnoreCase(activity)) {
                    valueToReturn = statsForEachActivityList.get(i).getTotalSetTimeForEachActivity();
                }
            }
        }
        return valueToReturn;
    }

    private boolean getIsActivityCustomBooleanFromSpecificActivityForSelecteDays(long uniqueId, String activity) {
        List<StatsForEachActivity> statsForEachActivityList = dailyStatsAccess.getStatsForEachActivityList();
        boolean valueToReturn = false;

        for (int i=0; i<statsForEachActivityList.size(); i++) {
            if (statsForEachActivityList.get(i).getUniqueIdTiedToTheSelectedActivity()==uniqueId) {
                if (statsForEachActivityList.get(i).getActivity().equalsIgnoreCase(activity)) {
                    valueToReturn = statsForEachActivityList.get(i).getIsCustomActivity();
                }
            }
        }
        return valueToReturn;
    }

    private double getIsCaloriesPerHourFromSpecifiCustomActivityForSelecteDays(long uniqueId, String activity) {
        List<StatsForEachActivity> statsForEachActivityList = dailyStatsAccess.getStatsForEachActivityList();
        double valueToReturn = 0;

        for (int i=0; i<statsForEachActivityList.size(); i++) {
            if (statsForEachActivityList.get(i).getUniqueIdTiedToTheSelectedActivity()==uniqueId) {
                if (statsForEachActivityList.get(i).getActivity().equalsIgnoreCase(activity)) {
                    valueToReturn = statsForEachActivityList.get(i).getCaloriesPerHour();
                }
            }
        }
        return valueToReturn;
    }

    private void deleteActivityFromStats(int position) {
        dailyStatsAdapter.turnOffEditMode();
        numberOfDaysWithActivitiesHasChanged = true;

        AsyncTask.execute(() -> {
            dailyStatsAccess.deleteTotalTimesAndCaloriesForSelectedActivityForSelectedDays(position);

            populateListsAndTextViewsFromEntityListsInDatabase();

            setStatsHaveBeenEditedForCurrentDay(true);

            getActivity().runOnUiThread(()-> {
                Toast.makeText(getContext(), "Deleted!", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private double calculateCaloriesFromMillisValueUsingMetScore(long millis) {
        double newCaloriesForActivity = 0;

        double retrievedMetScore = dailyStatsAccess.getMetScore();
        double caloriesBurnedPerSecond = calculateCaloriesBurnedPerSecond(retrievedMetScore);
        newCaloriesForActivity = ((double) (millis/1000) * caloriesBurnedPerSecond);

        return newCaloriesForActivity;
    }

    private double calculateCaloriesForCustomActivityAddition(long activityTime) {
        double valueToReturn = 0;

        String editTextCalories = addCustomCaloriesEditText.getText().toString();
        double caloriesEntered = Double.parseDouble(editTextCalories);

        long hours = getHoursFromMillisValue(activityTime);
        valueToReturn = caloriesEntered * hours;

        return valueToReturn;
    }

    private long getMinutesFromMillisValue(long millis) {
        return (millis/1000) / 60;
    }

    private long getHoursFromMillisValue(long millis) {
        return (millis/1000) / 60 / 60;
    }

    private double calculateCaloriesForCustomActivityEdit(long timeInMillis, double caloriesPerHour) {
        double hourDivisor = 60;
        double hoursInActivity = (double) getMinutesFromMillisValue(timeInMillis) / hourDivisor;

        return caloriesPerHour * hoursInActivity;
    }

    private long cappedTimeForStatEdits(long activityTime, long assignedTime, long unassignedTime) {
        long modifiedRemainingTime = assignedTime + unassignedTime;

        if (activityTime > modifiedRemainingTime) {
            activityTime = modifiedRemainingTime;
//            activityTime = roundUpMillisValues(activityTime);
        }

        return activityTime;
    }

    private long cappedActivityTimeForStatInsertions(long activityTime, long remainingTime) {
        if (remainingTime > 0) {
            long twentyFourHours = dailyStatsAccess.getTwentyFourHoursInMillis();
            long currentTotalActivityTime = dailyStatsAccess.getTotalActivityTimeForSelectedDuration();

            if ((currentTotalActivityTime + activityTime) > twentyFourHours) {
                activityTime = twentyFourHours - currentTotalActivityTime;
            }
        } else {
            activityTime = 0;
        }

        return activityTime;
    }

    private long roundUpMillisValues(long millisToRound) {
        long remainder = millisToRound%1000;
        return millisToRound += (1000-remainder);
    }

    private double retrieveMetScoreFromSubCategoryPosition() {
        String[] valueArray = tdeeChosenActivitySpinnerValues.getSubValueListOfStringArrays().get(selectedTdeeCategoryPosition);
        double preRoundedMet = Double.parseDouble(valueArray[selectedTdeeSubCategoryPosition]);
        return preRoundedMet;
    }

    private void setMetScoreTextViewInAddTdeePopUp() {
        metScore = retrieveMetScoreFromSubCategoryPosition();
        metScoreTextView.setText(getString(R.string.met_score_single_line, String.valueOf(metScore)));
    }

    private void setDefaultCustomActivityAdditionViews() {
        addCustomActivityEditText.setText("");
        addCustomCaloriesEditText.setText("");
        setTextStyleAndAlphaValuesOnTextViews(addCustomCaloriesPerHourTextView, true);
        setTextStyleAndAlphaValuesOnTextViews(addCustomCaloriesPerMinuteTextView, false);
    }

    private void launchActivityEditPopUpWithEditTextValuesSet(int position) {
        String activityString = dailyStatsAccess.getTotalActivitiesListForSelectedDuration().get(position);
        long timeToEditLongValue = dailyStatsAccess.getTotalSetTimeListForEachActivityForSelectedDuration().get(position);

        activityInEditPopUpTextView.setText(activityString);
        setActivityEditPopUpEditTexts(timeToEditLongValue);
        setActivityEditPopUpTimeRemainingTextView();
        toggleCancelOrDeleteButtonInEditPopUpTextView(EDITING_ACTIVITY);

        tdeeEditTextHours.requestFocus();
        tdeeEditPopUpWindow.showAsDropDown(topOfRecyclerViewAnchor, 0, dpToPxConv(-5));
    }

    private void setActivityEditPopUpTimeRemainingTextView() {
        String timeLeftInDay = longToStringConverters.convertMillisToHourBasedString(dailyStatsAccess.getUnassignedSetTimeForSelectedDuration());
        String timeLeftInDayConcatString = getString(R.string.day_time_remaining, timeLeftInDay);
        unassignedTimeInEditPopUpTextView.setText(timeLeftInDayConcatString);
    }

    private void toggleCancelOrDeleteButtonInEditPopUpTextView(int addingOrEditing) {
        if (addingOrEditing==ADDING_ACTIVITY) {
            deleteActivityIfEditingRowWithinEditPopUpButton.setText(R.string.cancel);
        }
        if (addingOrEditing==EDITING_ACTIVITY) {
            deleteActivityIfEditingRowWithinEditPopUpButton.setText(R.string.delete);
        }
    }

    private void setTextStyleAndAlphaValuesOnTextViews(TextView textView, boolean textViewSelected) {
        if (textViewSelected) {
            textView.setAlpha(1.0f);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            textView.setAlpha(0.3f);
            textView.setTypeface(Typeface.DEFAULT);
        }
    }

    private void zeroOutActivityEditPopUpEditTexts() {
        tdeeEditTextHours.setText("");
        tdeeEditTextMinutes.setText("");
        tdeeEditTextSeconds.setText("");
    }

    private void setActivityEditPopUpEditTexts(long valueToSet) {
        if (dailyStatsAccess.getNumberOfDaysSelected()>1) {
            tdeeEditTextHours.setText("");
            tdeeEditTextMinutes.setText("");
            tdeeEditTextSeconds.setText("");
        } else {
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

            setEditTextsToZeroIfEmpty(tdeeEditTextHours);
            setEditTextsToZeroIfEmpty(tdeeEditTextMinutes);
            setEditTextsToZeroIfEmpty(tdeeEditTextSeconds);
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
                ArrayList<String> spinnerList = tdeeChosenActivitySpinnerValues.getCategoryList();
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

    private void setCustomActivityBoolean(boolean isThisACustomActivity) {
        areWeAddingOrEditngACustomActivity = isThisACustomActivity;
    }

    private void tdeeCategorySpinnerTouchActions() {
        selectedTdeeCategoryPosition = tdee_category_spinner.getSelectedItemPosition();

        tdeeSubCategoryAdapter.clear();
        tdeeSubCategoryAdapter.addAll(tdeeChosenActivitySpinnerValues.getSubCategoryListOfStringArrays().get(selectedTdeeCategoryPosition));

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

    private void setThirdMainTextViewInAddTdeePopUp() {
        String caloriesBurnedPerMinute = formatDoubleToStringWithDecimals(calculateCaloriesBurnedPerMinute(metScore));
        String caloriesBurnedPerHour = formatDoubleToStringWithDecimals(calculateCaloriesBurnedPerMinute(metScore) * 60);

        caloriesBurnedInTdeeAdditionTextView.setText(getString(R.string.two_line_concat, getString(R.string.calories_burned_per_minute, caloriesBurnedPerMinute), getString(R.string.calories_burned_per_hour, caloriesBurnedPerHour)));
    }

    @Override
    public void onAddingFood() {
        typeOfFoodEditText.requestFocus();
        confirmCaloriesConsumedDeletionWithinPopUpButton.setText(R.string.cancel);
        toggleFoodConsumedEditingForMultipleDaysTextViews();
        clearFoodConsumedEditTexts();

        toggleMultipleDayWarningTextViewForFoodConsumption(ADDING_FOOD);
        toggleStringOnConfirmButtonForAddingOrEditingFood(ADDING_FOOD);

        caloriesConsumedAddAndEditPopUpWindow.showAsDropDown(topOfRecyclerViewAnchor, 0, dpToPxConv(0), Gravity.TOP);
    }

    private void addFoodToStats() {
        if (getFoodStringFromEditText().isEmpty()) {
            showToastIfNoneActive("Must enter a food!");
            return;
        }
        if (getCaloriesForFoodItemFromEditText().isEmpty()) {
            showToastIfNoneActive("Must enter a caloric value!");
            return;
        }

        AsyncTask.execute(() -> {
            String foodString = getFoodStringFromEditText();
            dailyStatsAccess.setFoodString(getFoodStringFromEditText());
            dailyStatsAccess.setCaloriesInFoodItem(Double.parseDouble(getCaloriesForFoodItemFromEditText()));

            //Inserting with check to see if food exists. Only checking if on a single day.
            if (dailyStatsAccess.getNumberOfDaysSelected() == 1) {
                insertFoodIfItDoesNotExistAndReturnIfItDoesNot();
            } else if (dailyStatsAccess.getNumberOfDaysSelected() > 1) {
                for (int i=0; i<dailyStatsAccess.getListOfFoodDaysSelected().size(); i++) {
                    int uniqueDayIdToCheck = dailyStatsAccess.getListOfFoodDaysSelected().get(i);

                    if (!dailyStatsAccess.doesFoodExistInDatabaseForMultipleDaysBoolean(uniqueDayIdToCheck)) {
                        dailyStatsAccess.insertCaloriesAndEachFoodForSingleDay(uniqueDayIdToCheck);
                    } else {
                        dailyStatsAccess.updateCaloriesAndEachFoodInDatabaseFromDayId(uniqueDayIdToCheck);
                    }
                }

                populateListsAndTextViewsFromEntityListsInDatabase();

                getActivity().runOnUiThread(()-> {
                    caloriesConsumedAddAndEditPopUpWindow.dismiss();
                });
            }
        });
    }

    private void insertFoodIfItDoesNotExistAndReturnIfItDoesNot() {
        if (!dailyStatsAccess.doesFoodExistsInDatabaseForSelectedDayBoolean(getFoodStringFromEditText())) {
            dailyStatsAccess.insertCaloriesAndEachFoodForSingleDay(daySelectedFromCalendar);
            populateListsAndTextViewsFromEntityListsInDatabase();

            getActivity().runOnUiThread(() ->{
                showToastIfNoneActive("Added!");
                caloriesConsumedAddAndEditPopUpWindow.dismiss();
            });

        } else {
            getActivity().runOnUiThread(() ->{
                showToastIfNoneActive("Food exists!");
            });

            return;
        }
    }

    private String getFoodStringFromEditText() {
        return typeOfFoodEditText.getText().toString().trim();
    }

    private String getCaloriesForFoodItemFromEditText() {
        return caloriesConsumedEditText.getText().toString();
    }

    private void clearFoodConsumedEditTexts() {
        typeOfFoodEditText.setText("");
        caloriesConsumedEditText.setText("");
    }

    @Override
    public void editCaloriesConsumedRowSelected(int position) {
        this.mPositionToEdit = position;
        confirmCaloriesConsumedDeletionWithinPopUpButton.setText(R.string.delete);
        toggleFoodConsumedEditingForMultipleDaysTextViews();
        launchFoodEditPopUpWithEditTextValuesSet(position);

        toggleMultipleDayWarningTextViewForFoodConsumption(EDITING_FOOD);
        toggleStringOnConfirmButtonForAddingOrEditingFood(EDITING_FOOD);
    }

    private void launchFoodEditPopUpWithEditTextValuesSet(int position) {
        String foodString = dailyStatsAccess.getTotalFoodStringListForSelectedDuration().get(position);
        typeOfFoodEditText.setText(foodString);

        if (dailyStatsAccess.getNumberOfDaysSelected()>1) {
            caloriesConsumedEditText.setText("");
        } else {
            double caloriesInFood = dailyStatsAccess.getTotalCaloriesConsumedListForSelectedDuration().get(position);
            String caloriesAsString = formatDoubleToStringWithoutDecimals(caloriesInFood);
            caloriesConsumedEditText.setText(caloriesAsString);
        }

        typeOfFoodEditText.requestFocus();

        caloriesConsumedAddAndEditPopUpWindow.showAsDropDown(topOfRecyclerViewAnchor, 0, dpToPxConv(0), Gravity.TOP);
    }

    private void editFoodInStats() {
        if (getFoodStringFromEditText().isEmpty()) {
            showToastIfNoneActive("Must enter a food!");
            return;
        }
        if (getCaloriesForFoodItemFromEditText().isEmpty()) {
            showToastIfNoneActive("Must enter a caloric value!");
            return;
        }

        dailyStatsAccess.setFoodString(getFoodStringFromEditText());
        dailyStatsAccess.setCaloriesInFoodItem(Double.parseDouble(getCaloriesForFoodItemFromEditText()));

        AsyncTask.execute(()-> {
            dailyStatsAccess.assignCaloriesForEachFoodItemEntityForSinglePosition(mPositionToEdit);

            String food = getFoodStringFromEditText();
            double calories = Double.parseDouble(getCaloriesForFoodItemFromEditText());
            dailyStatsAccess.updateCaloriesAndEachFoodInDatabaseFromPosition(mPositionToEdit);

            populateListsAndTextViewsFromEntityListsInDatabase();

            getActivity().runOnUiThread(()-> {
                caloriesConsumedAddAndEditPopUpWindow.dismiss();
                Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void deleteConsumedCalories(int position) {
        AsyncTask.execute(()-> {
            dailyStatsAccess.deleteCaloriesAndEachFoodInDatabase(position);
            populateListsAndTextViewsFromEntityListsInDatabase();

            getActivity().runOnUiThread(()-> {
                Toast.makeText(getContext(), "Deleted!", Toast.LENGTH_SHORT).show();
                caloriesConsumedAddAndEditPopUpWindow.dismiss();
            });
        });
    }

    private void setTotalCaloriesConsumedFooterTextViews() {
        foodStatsTotalCaloriesConsumedTextView.setText(formatDoubleToStringWithoutDecimals(dailyStatsAccess.getTotalCaloriesConsumedForSelectedDuration()));
    }

    private void setTotalCaloriesComparedTextViews(boolean statsAreSimplified) {
        double caloriesConsumed = dailyStatsAccess.getTotalCaloriesConsumedForSelectedDuration();
        double caloriesExpendedFromActivities = dailyStatsAccess.getTotalCaloriesBurnedForSelectedDuration();
        double unassignedCalories = dailyStatsAccess.getUnassignedCaloriesForSelectedDuration();

        double totalCaloriesExpended = 0;

        if (!statsAreSimplified) {
            totalCaloriesExpended = caloriesExpendedFromActivities + unassignedCalories;
        } else {
            totalCaloriesExpended = dailyStatsAccess.bmrCaloriesBurned();
        }

        double caloriesDifference = Math.abs(totalCaloriesExpended - caloriesConsumed);
        double poundDifference = calculateWeightDifferenceFromCalories(caloriesDifference);
        double kilogramDifference = poundDifference * 0.45;

        totalConsumedCaloriesCompared.setText(formatDoubleToStringWithoutDecimals(dailyStatsAccess.getTotalCaloriesConsumedForSelectedDuration()));

        totalExpendedCaloriesCompared.setText(formatDoubleToStringWithoutDecimals(totalCaloriesExpended));
        totalExpendedCaloriesComparedBmr.setText(formatDoubleToStringWithoutDecimals(dailyStatsAccess.getUnassignedCaloriesForSelectedDuration()));
        totalExpendedCaloriesComparedActivities.setText(formatDoubleToStringWithoutDecimals(caloriesExpendedFromActivities));

        String signToUse = getPlusOrMinusSignForDoubleDifference(caloriesConsumed, totalCaloriesExpended);
        int colorToUse = getTextColorForDoubleDifference(signToUse);

        String calorieDifferenceString = formatDoubleToStringWithoutDecimals(caloriesDifference);
        totalCaloriesDifferenceCompared.setText(getString(R.string.double_placeholder, signToUse, formatDoubleToStringWithoutDecimals(caloriesDifference)));
        totalCaloriesDifferenceCompared.setTextColor(ContextCompat.getColor(getContext(), colorToUse));

        String poundDifferenceString = formatDoubleToStringWithDecimals(poundDifference);
        String kilogramDifferenceString = formatDoubleToStringWithDecimals(kilogramDifference);
        String concatWeightDifference = getString(R.string.four_strings_slash_divided, signToUse, poundDifferenceString, signToUse, kilogramDifferenceString);

        totalWeightDifferenceCompared.setText(concatWeightDifference);
        totalWeightDifferenceCompared.setTextColor(ContextCompat.getColor(getContext(), colorToUse));
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
            return R.color.dark_red;
        } else {
            return R.color.dark_green;
        }
    }

    private double calculateWeightDifferenceFromCalories(double calories) {
        return calories/3500;
    }

    private String formatDoubleToStringWithDecimals(double calories) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.DOWN);
        return df.format(calories);
    }

    private String formatDoubleToStringWithoutDecimals(double calories) {
        DecimalFormat df = new DecimalFormat("#");
        df.setRoundingMode(RoundingMode.DOWN);
        return df.format(calories);
    }

    private double calculateCaloriesBurnedPerMinute(double metValue) {
        sharedPref = getContext().getSharedPreferences("pref", 0);
        boolean metricMode = sharedPref.getBoolean("metricMode", false);
        int userWeight = sharedPref.getInt("tdeeWeight,", 150);

        double weightConversion = userWeight;
        if (!metricMode) {
            weightConversion = weightConversion / 2.205;
        }

        double caloriesBurnedPerMinute = (metValue * 3.5 * weightConversion) / 200;

        return caloriesBurnedPerMinute;
    }

    private double calculateCaloriesBurnedPerSecond(double metValue) {
        return calculateCaloriesBurnedPerMinute(metValue) / 60;
    }

    private void calendarMinimizationButtonLogic(boolean restoreOnly) {
        if (restoreOnly) {
            if (!calendarIsMinimized) {
                return;
            }
        }

        toggleCalendarMinimizationState();
        setCalendarMinimizationAnimations();
        toggleCalendarMinimizationLayouts();
    }

    private void toggleCalendarMinimizationState() {
        calendarIsMinimized = !calendarIsMinimized;
    }

    private void toggleCalendarMinimizationLayouts() {
        if (caloriesComparisonTabLayout.getSelectedTabPosition()==0) {
            setCalendarMinimizationLayoutConstraints(dailyStatsRecyclerViewLayoutParams, totalActivityStatsValuesTextViewsLayoutParams);

            dailyStatsRecyclerView.setLayoutParams(dailyStatsRecyclerViewLayoutParams);
            totalActivityStatsValuesTextViewLayout.setLayoutParams(totalActivityStatsValuesTextViewsLayoutParams);
            totalFoodStatsValuesTextViewLayout.setVisibility(View.GONE);
        }

        if (caloriesComparisonTabLayout.getSelectedTabPosition()==1) {
            setCalendarMinimizationLayoutConstraints(caloriesConsumedRecyclerViewLayoutParams, totalFoodStatsValuesTextViewLayoutParams);

            caloriesConsumedRecyclerView.setLayoutParams(caloriesConsumedRecyclerViewLayoutParams);
            totalFoodStatsValuesTextViewLayout.setLayoutParams(totalFoodStatsValuesTextViewLayoutParams);
            totalActivityStatsValuesTextViewLayout.setVisibility(View.GONE);
        }

        setCalendarMinimizationViews();
    }

    private void setCalendarMinimizationViews() {
        if (calendarIsMinimized) {
            setCalendarMinimizationButton(true);
            calendarView.setVisibility(View.INVISIBLE);
        } else {
            setCalendarMinimizationButton(false);
            calendarView.setVisibility(View.VISIBLE);

        }
    }

    private void setCalendarMinimizationAnimations() {
        if (calendarIsMinimized) {
            calendarView.startAnimation(slideOutCalendarToBottom);
        } else {
            calendarView.startAnimation(slideInCalendarFromBottom);
        }
        if (caloriesComparisonTabLayout.getSelectedTabPosition() == 0) {
            totalActivityStatsValuesTextViewLayout.startAnimation(fadeInStatsBar);
        }
        if (caloriesComparisonTabLayout.getSelectedTabPosition() == 1) {
            totalFoodStatsValuesTextViewLayout.startAnimation(fadeInStatsBar);
        }
    }

    private void setCalendarMinimizationButton(boolean minimizing) {
        if (minimizing) {
            if (phoneHeight <= 1920) {
                minimizeCalendarButton.setImageResource(R.drawable.arrow_up_small);
            } else {
                minimizeCalendarButton.setImageResource(R.drawable.arrow_up);
            }
        } else {
            if (phoneHeight <= 1920) {
                minimizeCalendarButton.setImageResource(R.drawable.arrow_down_small);
            } else {
                minimizeCalendarButton.setImageResource(R.drawable.arrow_down);
            }
        }
    }

    private void setCalendarMinimizationLayoutConstraints(ConstraintLayout.LayoutParams recyclerParams, ConstraintLayout.LayoutParams textViewParams) {
        if (!calendarIsMinimized) {
            textViewParams.bottomToTop = R.id.stats_calendar;

            recyclerParams.bottomToBottom = ConstraintLayout.LayoutParams.UNSET;
            recyclerParams.bottomToTop = R.id.total_activity_stats_values_textView_layout;
        } else {
            textViewParams.bottomToTop = ConstraintLayout.LayoutParams.UNSET;
            textViewParams.bottomToBottom = R.id.daily_stats_fragment_parent_layout;

            recyclerParams.bottomToTop = ConstraintLayout.LayoutParams.UNSET;
            recyclerParams.bottomToBottom = R.id.daily_stats_fragment_parent_layout;

        }
    }

    private void togggleTotalStatTextViewsWhenSwitchingTabs(int tabPositionSelected) {
        if (tabPositionSelected==0) {
            totalActivityStatsValuesTextViewLayout.setVisibility(View.VISIBLE);
            totalFoodStatsValuesTextViewLayout.setVisibility(View.GONE);
        }
        if (tabPositionSelected==1) {
            totalFoodStatsValuesTextViewLayout.setVisibility(View.VISIBLE);
            totalActivityStatsValuesTextViewLayout.setVisibility(View.GONE);
        }
    }

    //Custom CalendarDay class uses months w/ 1-12 vars, while Calendar uses 0-11, hence the adjustments.
    private void instantiateCalendarObjects() {
        mCalendar = Calendar.getInstance(Locale.getDefault());
        calendarView = mRoot.findViewById(R.id.stats_calendar);

        CalendarDay currentCalendarDay = CalendarDay.from(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH) + 1, mCalendar.get(Calendar.DAY_OF_MONTH));

        int currentYear = mCalendar.get(Calendar.YEAR);
        int currentMonth = mCalendar.get(Calendar.MONTH);
        int currentDay = mCalendar.get(Calendar.DAY_OF_MONTH);

        int maxYear = currentYear + 1;
        int maxMonth = currentMonth + 1;
        int maxDay = currentDay;

        if (currentMonth == Calendar.FEBRUARY) {
            maxDay = 28;
        }

        calendarView.state().edit()
                .setMinimumDate(CalendarDay.from(2022, 1, 1))
                .setMaximumDate(CalendarDay.from(maxYear, maxMonth, maxDay))
                .commit();
    }

    private void instantiateActivityAdditionSpinnersAndAdapters() {
        tdeeCategoryAdapter = new ArrayAdapter<>(getContext(), R.layout.tdee_category_spinner_layout, tdeeChosenActivitySpinnerValues.getCategoryList());
        tdeeCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tdee_category_spinner.setAdapter(tdeeCategoryAdapter);

        tdeeSubCategoryAdapter = new ArrayAdapter<>(getContext(), R.layout.tdee_sub_category_spinner_layout);
        tdeeSubCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        tdee_sub_category_spinner.setAdapter(tdeeSubCategoryAdapter);
    }

    private void instantiateAddPopUpViews() {
        addTDEEPopUpView = inflater.inflate(R.layout.daily_stats_add_popup_for_stats_fragment, null);
        addTdeePopUpWindow = new PopupWindow(addTDEEPopUpView, WindowManager.LayoutParams.MATCH_PARENT, dpToPxConv(270), true);
        addTdeePopUpWindow.setAnimationStyle(R.style.SlideFromLeftAnimationShort);

        tdee_category_spinner = addTDEEPopUpView.findViewById(R.id.activity_category_spinner);
        tdee_sub_category_spinner = addTDEEPopUpView.findViewById(R.id.activity_sub_category_spinner);
        addActivityConfirmButton = addTDEEPopUpView.findViewById(R.id.add_activity_confirm_button);

        metScoreTextView = addTDEEPopUpView.findViewById(R.id.met_score_textView);
        metScoreTextView.setVisibility(View.GONE);
        caloriesBurnedInTdeeAdditionTextView = addTDEEPopUpView.findViewById(R.id.calories_burned_in_tdee_addition_popUp_textView);

        addActivityConfirmButton.setText(R.string.save);
        metScoreTextView.setTextSize(22);

        addTdeePopUpWindow.setOnDismissListener(()-> {
            dailyStatsAdapter.turnOffEditMode();
            dailyStatsAdapter.notifyDataSetChanged();
        });
    }

    private void instantiateActivityEditPopUpViews() {
        tdeeEditView = inflater.inflate(R.layout.daily_stats_edit_popup, null);
        tdeeEditPopUpWindow = new PopupWindow(tdeeEditView, WindowManager.LayoutParams.MATCH_PARENT, dpToPxConv(300), true);
        tdeeEditPopUpWindow.setAnimationStyle(R.style.SlideFromLeftAnimationShort);

        customActivityPopUpView = inflater.inflate(R.layout.custom_activity_popup_layout, null);
        customActivityPopUpWindow = new PopupWindow(customActivityPopUpView, WindowManager.LayoutParams.MATCH_PARENT, dpToPxConv(270), true);

        addCustomActivityEditText = customActivityPopUpView.findViewById(R.id.add_custom_activity_edit_text);
        addCustomCaloriesEditText = customActivityPopUpView.findViewById(R.id.add_custom_calories_edit_text);
        addCustomCaloriesPerHourTextView = customActivityPopUpView.findViewById(R.id.add_custom_calories_edit_text_per_hour_textView);
        addCustomCaloriesPerMinuteTextView = customActivityPopUpView.findViewById(R.id.add_custom_calories_edit_text_per_minute_textView);
        confirmCustomActivityAdditionValues = customActivityPopUpView.findViewById(R.id.confirm_custom_activity_values);
        cancelCustomActivityValues = customActivityPopUpView.findViewById(R.id.cancel_custom_activity_values);

        addCustomCaloriesPerHourTextView.setText(R.string.per_hour);
        addCustomCaloriesPerMinuteTextView.setText(R.string.per_minute);

        editActivityPopUpLayout = tdeeEditView.findViewById(R.id.edit_activity_popUp_layout);

        activityInEditPopUpTextView = tdeeEditView.findViewById(R.id.activity_string_in_edit_popUp);
        activityInEditPopUpTextViewLayoutParams = (ConstraintLayout.LayoutParams) activityInEditPopUpTextView.getLayoutParams();

        editTextLayoutForActivityEdit = tdeeEditView.findViewById(R.id.edit_text_layout_for_activities);
        editTextLayoutParamsForActivityEdit = (ConstraintLayout.LayoutParams) editTextLayoutForActivityEdit.getLayoutParams();
        tdeeEditTextHours = tdeeEditView.findViewById(R.id.tdee_editText_hours);
        tdeeEditTextMinutes = tdeeEditView.findViewById(R.id.tdee_editText_minutes);
        tdeeEditTextSeconds = tdeeEditView.findViewById(R.id.tdee_editText_seconds);
        unassignedTimeInEditPopUpTextView = tdeeEditView.findViewById(R.id.unassigned_time_textView);

        confirmActivityEditWithinPopUpButton = tdeeEditView.findViewById(R.id.confirm_activity_edit_button);
        deleteActivityIfEditingRowWithinEditPopUpButton = tdeeEditView.findViewById(R.id.activity_delete_button);
        multipleDayWarningForActivitiesTextView = tdeeEditView.findViewById(R.id.multiple_day_warning_for_activities_textView);

        confirmActivityEditWithinPopUpButtonLayoutParams = (ConstraintLayout.LayoutParams) confirmActivityEditWithinPopUpButton.getLayoutParams();
        deleteActivityIfEditingRowWithinEditPopUpButtonLayoutParams = (ConstraintLayout.LayoutParams) deleteActivityIfEditingRowWithinEditPopUpButton.getLayoutParams();

        popUpAnchorBottom = mRoot.findViewById(R.id.tdee_edit_popUp_anchor_bottom);

        tdeeEditPopUpWindow.setOnDismissListener(()-> {
            dailyStatsAdapter.turnOffEditMode();
            dailyStatsAdapter.notifyDataSetChanged();
        });
    }

    private void instantiateCaloriesConsumedEditPopUpViews() {
        caloriesConsumedAddAndEditView = inflater.inflate(R.layout.calories_consumed_add_and_edit_popup, null);
        caloriesConsumedAddAndEditPopUpWindow = new PopupWindow(caloriesConsumedAddAndEditView, WindowManager.LayoutParams.MATCH_PARENT, dpToPxConv(300), true);
        caloriesConsumedAddAndEditPopUpWindow.setAnimationStyle(R.style.SlideFromLeftAnimationShort);

        typeOfFoodHeaderTextView = caloriesConsumedAddAndEditView.findViewById(R.id.add_food_name_header);
        caloriesConsumedHeaderTextView = caloriesConsumedAddAndEditView.findViewById(R.id.add_calories_consumed_header);
        typeOfFoodEditText = caloriesConsumedAddAndEditView.findViewById(R.id.food_name_add_text);
        caloriesConsumedEditText = caloriesConsumedAddAndEditView.findViewById(R.id.add_calories_consumed_editText);
        multipleDayEditWarningForFoodConsumedTextView = caloriesConsumedAddAndEditView.findViewById(R.id.multiple_day_edit_warning_for_food_consumed_textView);

        confirmCaloriesConsumedAdditionWithinPopUpButton = caloriesConsumedAddAndEditView.findViewById(R.id.confirm_calories_consumed_add_button);
        confirmCaloriesConsumedDeletionWithinPopUpButton = caloriesConsumedAddAndEditView.findViewById(R.id.confirm_calories_consumed_delete_button);

        caloriesConsumedAddAndEditPopUpWindow.setOnDismissListener(()-> {
            caloriesConsumedAdapter.turnOffEditMode();
            caloriesConsumedAdapter.getItemCount();
            caloriesConsumedAdapter.notifyDataSetChanged();
        });
    }

    private void instantiateAboutStatsPopUpViews() {
        aboutStatsPopUpView = inflater.inflate(R.layout.about_stats_popup_layout, null);
        aboutStatsPopUpWindow = new PopupWindow(aboutStatsPopUpView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        aboutStatsPopUpWindow.setAnimationStyle(R.style.SlideFromLeftAnimationShort);
    }

    public void launchAboutStatsPopUpWindow() {
        aboutStatsPopUpWindow.showAtLocation(topOfRecyclerViewAnchor, Gravity.TOP, 0, 0);
    }

    private void setDefaultCalorieTabViewsForFirstTab() {
        caloriesConsumedRecyclerView.setVisibility(View.GONE);
        totalFoodStatsValuesTextViewLayout.setVisibility(View.GONE);
        caloriesComparedLayout.setVisibility(View.GONE);
    }

    private void instantiateTabLayout() {
        caloriesComparisonTabLayout = mRoot.findViewById(R.id.calorie_comparison_tab_layout);

        if (phoneHeight <= 1920) {
            tabOne = caloriesComparisonTabLayout.newTab().setText("Calories Burned");
            tabTwo = caloriesComparisonTabLayout.newTab().setText("Calories Consumed");
            tabThree = caloriesComparisonTabLayout.newTab().setText("Calories Compared");
        } else {
            tabOne = caloriesComparisonTabLayout.newTab().setText("Calories Burned");
            tabTwo = caloriesComparisonTabLayout.newTab().setText("Calories Consumed");
            tabThree = caloriesComparisonTabLayout.newTab().setText("Calories Compared");
        }

        caloriesComparisonTabLayout.addTab(tabOne);
        caloriesComparisonTabLayout.addTab(tabTwo);
        caloriesComparisonTabLayout.addTab(tabThree);

        caloriesComparisonTabLayout.setBackgroundColor((ContextCompat.getColor(getContext(), R.color.retro_black)));

        for (int i=0; i<caloriesComparisonTabLayout.getTabCount(); i++) {
            setCustomTextViewOnTab(caloriesComparisonTabLayout.getTabAt(i));
        }
    }

    private void setCustomTextViewOnTab(TabLayout.Tab tabToChange) {
        TextView tabTextView = new TextView(getContext());
        tabToChange.setCustomView(tabTextView);

        tabTextView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
        tabTextView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;

        tabTextView.setText(tabToChange.getText());
        tabTextView.setTextSize(16);
        tabTextView.setGravity(Gravity.CENTER);

        if (phoneHeight <= 1920) {
            tabTextView.setMinLines(1);
            tabTextView.setTextSize(14);
        } else {
            tabTextView.setMinLines(2);
            tabTextView.setTextSize(16);
        }
    }

    private void toggleCustomTabTextViewHighlighting(int tabPosition) {
        TabLayout.Tab tab = new TabLayout.Tab();

        if (tabPosition == 0) tab = caloriesComparisonTabLayout.getTabAt(0);
        if (tabPosition == 1) tab = caloriesComparisonTabLayout.getTabAt(1);
        if (tabPosition == 2) tab = caloriesComparisonTabLayout.getTabAt(2);

        TextView tabTextView = (TextView) tab.getCustomView();
        tabTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.blue_2));

        tab.setCustomView(tabTextView);
    }

    private void resetCustomTabTextViewColors() {
        for (int i = 0; i<caloriesComparisonTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = caloriesComparisonTabLayout.getTabAt(i);
            TextView tabTextView = (TextView) tab.getCustomView();
            tabTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.lighter_grey));

            tab.setCustomView(tabTextView);
        }
    }

    private void instantiateCalorieTabLayoutListenerAndViews() {
        setDefaultCalorieTabViewsForFirstTab();
        toggleCustomTabTextViewHighlighting(0);

        caloriesComparisonTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (caloriesComparisonTabLayout.getSelectedTabPosition()) {
                    case 0:
                        dailyStatsRecyclerView.setVisibility(View.VISIBLE);
                        totalActivityStatsValuesTextViewLayout.setVisibility(View.VISIBLE);

                        toggleEditButtonView(false);
                        setTabSelected(0);

                        mChangeOnOptionsItemSelectedMenu.onChangeOnOptionsMenu(STATS_MENU);
                        mChangeSortMenu.onChangeSortMenu(SORTING_ACTIVITIES);

                        togggleTotalStatTextViewsWhenSwitchingTabs(0);
                        toggleCustomTabTextViewHighlighting(0);

                        break;
                    case 1:
                        caloriesConsumedRecyclerView.setVisibility(View.VISIBLE);
                        totalFoodStatsValuesTextViewLayout.setVisibility(View.VISIBLE);
                        simplifiedStatsLayout.setVisibility(View.INVISIBLE);

                        toggleEditButtonView(false);
                        setTabSelected(1);

                        mChangeOnOptionsItemSelectedMenu.onChangeOnOptionsMenu(STATS_MENU);
                        mChangeSortMenu.onChangeSortMenu(SORTING_FOOD_CONSUMED);

                        togggleTotalStatTextViewsWhenSwitchingTabs(1);
                        toggleCustomTabTextViewHighlighting(1);
                        break;
                    case 2:
                        caloriesComparedLayout.setVisibility(View.VISIBLE);
                        simplifiedStatsLayout.setVisibility(View.INVISIBLE);

                        toggleEditButtonView(true);
                        setTabSelected(2);

                        mChangeSortMenu.onChangeSortMenu(DISABLE_SORTING);

                        toggleCustomTabTextViewHighlighting(2);
                        break;
                }
                toggleCalendarMinimizationLayouts();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                dailyStatsRecyclerView.setVisibility(View.INVISIBLE);
                caloriesConsumedRecyclerView.setVisibility(View.INVISIBLE);
                totalActivityStatsValuesTextViewLayout.setVisibility(View.GONE);
                totalFoodStatsValuesTextViewLayout.setVisibility(View.GONE);
                caloriesComparedLayout.setVisibility(View.GONE);
                simplifiedStatsLayout.setVisibility(View.INVISIBLE);

                dailyStatsAdapter.turnOffEditMode();
                caloriesConsumedAdapter.turnOffEditMode();

                resetCustomTabTextViewColors();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setTabSelected(int selectedTab) {
        this.mSelectedTab = selectedTab;
    }

    public int getSelectedTab() {
        return mSelectedTab;
    }

    private void instantiateTextViewsAndMiscClasses() {
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        longToStringConverters = new LongToStringConverters();

        sharedPref = getContext().getSharedPreferences("pref", 0);
        prefEdit = sharedPref.edit();

        tdeeChosenActivitySpinnerValues = new TDEEChosenActivitySpinnerValues(getActivity());
        inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        activityStatsDurationRangeTextView = mRoot.findViewById(R.id.duration_date_range_textView);
        activityStatsDurationSwitcherButtonLeft = mRoot.findViewById(R.id.stat_duration_switcher_button_left);
        activityStatsDurationSwitcherButtonRight = mRoot.findViewById(R.id.stat_duration_switcher_button_right);

        minimizeCalendarButton = mRoot.findViewById(R.id.minimize_calendarView_button);
        recyclerAndTotalStatsDivider =  mRoot.findViewById(R.id.recycler_and_total_stats_divider);
        topOfRecyclerViewAnchor = mRoot.findViewById(R.id.top_of_recyclerView_anchor);
        totalStatsHeaderTextView = mRoot.findViewById(R.id.activity_stats_duration_header_textView);

        totalActivityStatsValuesTextViewLayout = mRoot.findViewById(R.id.total_activity_stats_values_textView_layout);
        totalActivityStatsValuesTextViewsLayoutParams = (ConstraintLayout.LayoutParams) totalActivityStatsValuesTextViewLayout.getLayoutParams();
        dailyStatsBmrTimeTextView = mRoot.findViewById(R.id.total_bmr_stats_time_textView);
        dailyStatsBmrCaloriesTextView = mRoot.findViewById(R.id.total_bmr_stats_calories_textView);
        dailyStatsTotalActivityTimeTextView = mRoot.findViewById(R.id.daily_stats_total_activity_time_textView);
        dailyStatsTotalActivityCaloriesBurnedTextView = mRoot.findViewById(R.id.daily_stats_total_activity_calories_burned_textView);
        dailyTotalExpendedTimeElapsedTextView = mRoot.findViewById(R.id.total_expended_stats_time_elapsed_textView);
        dailyTotalExpendedCaloriesBurnedTextView = mRoot.findViewById(R.id.total_expended_stats_calories_burned_textView);

        totalFoodStatsValuesTextViewLayout = mRoot.findViewById(R.id.total_food_stats_values_textView_layout);
        totalFoodStatsValuesTextViewLayoutParams = (ConstraintLayout.LayoutParams) totalFoodStatsValuesTextViewLayout.getLayoutParams();
        foodStatsTotalCaloriesConsumedTextView = mRoot.findViewById(R.id.total_food_stats_calories_consumed);

        simplifiedStatsLayout = mRoot.findViewById(R.id.simplified_stats_layout);
        simplifiedActivityLevelTextView = mRoot.findViewById(R.id.simplified_activity_level_textView);
        simplifiedCaloriesBurnedTextView = mRoot.findViewById(R.id.simplified_calories_burned_textView);

        customCalendarDayList = new ArrayList<>();

        statsForEachActivityList = new ArrayList<>();
    }

    private void instantiateExpansionPopUpViews() {
        dailyStatsExpandedView = inflater.inflate(R.layout.daily_stats_expanded_popup, null);
        dailyStatsExpandedPopUpWindow = new PopupWindow(dailyStatsExpandedView, WindowManager.LayoutParams.MATCH_PARENT, dpToPxConv(420), false);
        dailyStatsExpandedPopUpWindow.setAnimationStyle(R.style.SlideFromLeftAnimationShort);
    }

    private void instantiateActivityRecyclerViewAndItsAdapter() {
        dailyStatsAdapter = new DailyStatsAdapter(getContext(), dailyStatsAccess.getTotalActivitiesListForSelectedDuration(), dailyStatsAccess.getTotalSetTimeListForEachActivityForSelectedDuration(), dailyStatsAccess.getTotalCaloriesBurnedListForEachActivityForSelectedDuration());

        dailyStatsAdapter.setScreenHeight(phoneHeight);

        dailyStatsAdapter.getSelectedTdeeItemPosition(DailyStatsFragment.this);
        dailyStatsAdapter.addActivityToDailyStats(DailyStatsFragment.this);

        dailyStatsRecyclerView = mRoot.findViewById(R.id.daily_stats_recyclerView);
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        dailyStatsRecyclerView.setLayoutManager(lm);
        dailyStatsRecyclerView.setAdapter(dailyStatsAdapter);

        dailyStatsRecyclerViewLayoutParams = (ConstraintLayout.LayoutParams) dailyStatsRecyclerView.getLayoutParams();

        DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycler_view_divider_line));

        dailyStatsRecyclerView.addItemDecoration(itemDecorator);
    }

    private void instantiateCalorieConsumptionRecyclerAndItsAdapter() {
        caloriesConsumedAdapter = new CaloriesConsumedAdapter(getContext(), dailyStatsAccess.getTotalFoodStringListForSelectedDuration(), dailyStatsAccess.getTotalCaloriesConsumedListForSelectedDuration());

        caloriesConsumedAdapter.setScreenHeight(phoneHeight);

        caloriesConsumedAdapter.editConsumedCalories(DailyStatsFragment.this);
        caloriesConsumedAdapter.addCaloriesToStats(DailyStatsFragment.this);

        caloriesConsumedRecyclerView = mRoot.findViewById(R.id.calories_consumed_recyclerView);
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        caloriesConsumedRecyclerView.setLayoutManager(lm);
        caloriesConsumedRecyclerView.setAdapter(caloriesConsumedAdapter);

        caloriesConsumedRecyclerViewLayoutParams = (ConstraintLayout.LayoutParams) caloriesConsumedRecyclerView.getLayoutParams();

        DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycler_view_divider_line));

        caloriesConsumedRecyclerView.addItemDecoration(itemDecorator);
    }

    private void instantiateCaloriesComparedViews() {
        totalExpendedCaloriesComparedBmrHeader = mRoot.findViewById(R.id.total_expended_calories_compared_bmr_header);
        totalExpendedCaloriesComparedActivitiesHeader = mRoot.findViewById(R.id.total_expended_calories_compared_activities_header);

        caloriesComparedLayout = mRoot.findViewById(R.id.calories_compared_layout);
        totalConsumedCaloriesCompared = mRoot.findViewById(R.id.total_consumed_calories_compared);
        totalExpendedCaloriesCompared = mRoot.findViewById(R.id.total_expended_calories_compared);
        totalExpendedCaloriesComparedBmr = mRoot.findViewById(R.id.total_expended_calories_compared_bmr);
        totalExpendedCaloriesComparedActivities = mRoot.findViewById(R.id.total_expended_calories_compared_activities);
        totalCaloriesDifferenceCompared = mRoot.findViewById(R.id.total_calories_difference_compared);
        totalWeightDifferenceCompared = mRoot.findViewById(R.id.total_weight_difference_compared);
    }

    private void instantiateCalendarAnimations() {
        slideOutCalendarToBottom = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_to_bottom);
        slideOutCalendarToBottom.setDuration(250);
        slideInCalendarFromBottom = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_from_bottom);
        slideInCalendarFromBottom.setDuration(250);

        fadeInStatsBar = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_short);
        fadeInStatsBar.setDuration(500);
        fadeOutStatsBar = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out_short);

        slideOutTotalStatValuesLayoutToBottom = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_to_bottom_no_alpha_change);
        slideOutTotalStatValuesLayoutToBottom.setDuration(2000);
        slideInTotalStatValuesLayoutFromBottom = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_from_bottom_untouched_alpha);
        slideInTotalStatValuesLayoutFromBottom.setDuration(250);
    }

    private void setCalendarAnimationListeners() {
        slideOutCalendarToBottom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                calendarView.setVisibility(View.GONE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        slideInCalendarFromBottom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                calendarView.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private double roundUpCaloriesIfMoreThanZero(double calories) {
        if (calories > 0) {
            return Math.ceil(calories);
        } else {
            return calories;
        }
    }

    private long roundDownMillisValues(long millisToRound) {
        return millisToRound -= (millisToRound%1000);
    }

    private int dpToPxConv(float pixels) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixels, getResources().getDisplayMetrics());
    }

    private void showToastIfNoneActive (String message){
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        mToast.show();
    }


    private int dpConv(float pixels) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixels, getResources().getDisplayMetrics());
    }

}