package com.example.tragic.irate.simple.stopwatch;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tragic.irate.simple.stopwatch.Adapters.CycleRoundsAdapter;
import com.example.tragic.irate.simple.stopwatch.Adapters.CycleRoundsAdapterTwo;
import com.example.tragic.irate.simple.stopwatch.Adapters.DotsAdapter;
import com.example.tragic.irate.simple.stopwatch.Adapters.LapAdapter;
import com.example.tragic.irate.simple.stopwatch.Adapters.PomDotsAdapter;
import com.example.tragic.irate.simple.stopwatch.Adapters.SavedCycleAdapter;
import com.example.tragic.irate.simple.stopwatch.Adapters.SavedPomCycleAdapter;
import com.example.tragic.irate.simple.stopwatch.Canvas.LapListCanvas;
import com.example.tragic.irate.simple.stopwatch.Database.Cycles;
import com.example.tragic.irate.simple.stopwatch.Database.CyclesDatabase;
import com.example.tragic.irate.simple.stopwatch.Database.DailyCalorieClasses.CaloriesForEachFood;
import com.example.tragic.irate.simple.stopwatch.Database.DailyStatClasses.DailyStatsAccess;
import com.example.tragic.irate.simple.stopwatch.Database.DailyStatClasses.DailyStatsFragment;
import com.example.tragic.irate.simple.stopwatch.Database.DailyStatClasses.StatsForEachActivity;
import com.example.tragic.irate.simple.stopwatch.Database.PomCycles;
import com.example.tragic.irate.simple.stopwatch.Miscellaneous.CalorieIteration;
import com.example.tragic.irate.simple.stopwatch.Miscellaneous.LongToStringConverters;
import com.example.tragic.irate.simple.stopwatch.Miscellaneous.TDEEChosenActivitySpinnerValues;
import com.example.tragic.irate.simple.stopwatch.Miscellaneous.TextViewDisplaySync;
import com.example.tragic.irate.simple.stopwatch.Miscellaneous.TimerIteration;
import com.example.tragic.irate.simple.stopwatch.Miscellaneous.VerticalSpaceItemDecoration;
import com.example.tragic.irate.simple.stopwatch.SettingsFragments.DisclaimerFragment;
import com.example.tragic.irate.simple.stopwatch.SettingsFragments.ChangeSettingsValues;
import com.example.tragic.irate.simple.stopwatch.SettingsFragments.ColorSettingsFragment;
import com.example.tragic.irate.simple.stopwatch.SettingsFragments.RootSettingsFragment;
import com.example.tragic.irate.simple.stopwatch.SettingsFragments.SoundSettingsFragment;
import com.example.tragic.irate.simple.stopwatch.SettingsFragments.tdeeSettingsFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements SavedCycleAdapter.onCycleClickListener, SavedCycleAdapter.onHighlightListener, SavedCycleAdapter.onTdeeModeToggle, SavedPomCycleAdapter.onCycleClickListener, SavedPomCycleAdapter.onHighlightListener, CycleRoundsAdapter.onFadeFinished, CycleRoundsAdapterTwo.onFadeFinished, CycleRoundsAdapter.onRoundSelected, CycleRoundsAdapterTwo.onRoundSelectedSecondAdapter, SavedCycleAdapter.onResumeOrResetCycle, SavedPomCycleAdapter.onResumeOrResetCycle, RootSettingsFragment.onChangedSettings, SoundSettingsFragment.onChangedSoundSetting, ColorSettingsFragment.onChangedColorSetting, DailyStatsFragment.changeOnOptionsItemSelectedMenu, DailyStatsFragment.changeSortMenu, DotsAdapter.sendDotAlpha, PomDotsAdapter.sendPomDotAlpha {

  SharedPreferences sharedPreferences;
  SharedPreferences.Editor prefEdit;

  Menu onOptionsMenu;
  int mOnOptionsSelectedMenuType;
  int DEFAULT_MENU = 0;
  int STATS_MENU = 1;
  int FILLER_MENU = 2;
  int SETTINGS_MENU = 3;

  DailyStatsAccess dailyStatsAccess;
  FragmentManager fragmentManager;
  TabLayout savedCyclesTabLayout;
  TabLayout.Tab savedCyclesTab;
  View timerView;
  View mainView;
  View actionBarView;
  Calendar calendar;
  LongToStringConverters longToStringConverters;

  ImageButton fab;
  ImageButton stopWatchLaunchButton;
  TextView emptyCycleList;

  CyclesDatabase cyclesDatabase;
  Cycles cycles;
  PomCycles pomCycles;
  List<Cycles> cyclesList;
  List<PomCycles> pomCyclesList;
  boolean isNewCycle;

  RecyclerView roundRecycler;
  RecyclerView roundRecyclerTwo;
  RecyclerView savedCycleRecycler;
  RecyclerView savedPomCycleRecycler;
  CycleRoundsAdapter cycleRoundsAdapter;
  CycleRoundsAdapterTwo cycleRoundsAdapterTwo;
  View roundListDivider;
  SavedCycleAdapter savedCycleAdapter;
  SavedPomCycleAdapter savedPomCycleAdapter;

  LinearLayoutManager workoutCyclesRecyclerLayoutManager;
  LinearLayoutManager roundRecyclerOneLayoutManager;
  LinearLayoutManager roundRecyclerTwoLayoutManager;
  LinearLayoutManager pomCyclesRecyclerLayoutManager;

  View deleteCyclePopupView;

  View sortCyclePopupView;
  View sortActivitiesPopupView;
  View sortFoodConsumedPopupView;

  View savedCyclePopupView;
  View editCyclesPopupView;
  View settingsPopupView;
  View aboutSettingsPopUpView;

  View soundsViewInSettings;
  View colorsViewInSettings;
  View aboutViewInSettings;

  PopupWindow sortPopupWindow;
  PopupWindow savedCyclePopupWindow;
  PopupWindow deleteCyclePopupWindow;
  PopupWindow editCyclesPopupWindow;
  PopupWindow settingsPopupWindow;
  PopupWindow aboutSettingsPopUpWindow;

  EditText cycleNameEdit;
  TextView firstRoundTypeHeaderInEditPopUp;
  TextView secondRoundTypeHeaderInEditPopUp;
  TextView thirdRoundTypeHeaderInEditPopUp;
  TextView timerValueInEditPopUpTextView;
  TextView pomTimerValueInEditPopUpTextViewOne;
  TextView pomTimerValueInEditPopUpTextViewTwo;
  TextView pomTimerValueInEditPopUpTextViewThree;

  TextView number_one;
  TextView number_two;
  TextView number_three;
  TextView number_four;
  TextView number_five;
  TextView number_six;
  TextView number_seven;
  TextView number_eight;
  TextView number_nine;
  TextView number_zero;
  ImageButton deleteEditPopUpTimerNumbers;

  boolean isSavedInfinityOptionActiveForSets;
  boolean isSavedInfinityOptionActiveForBreaks;
  ArrayList<String> editPopUpTimerArray;
  ArrayList<String> editPopUpTimerArrayCapped;
  ArrayList<String> savedEditPopUpArrayForFirstHeaderModeOne;
  ArrayList<String> savedEditPopUpArrayForSecondHeaderModeOne;

  ArrayList<String> savedEditPopUpArrayForFirstHeaderModeThree;
  ArrayList<String> savedEditPopUpArrayForSecondHeaderModeThree;
  ArrayList<String> savedEditPopUpArrayForThirdHeader;

  TextView sortCycleTitleAToZ;
  TextView sortCycleTitleZtoA;
  TextView sortHigh;
  TextView sortLow;
  TextView sortActivityTitleAtoZ;
  TextView sortActivityTitleZToA;

  TextView sortActivityStatsAToZTextView;
  TextView sortActivityStatsZToATextView;
  TextView sortActivityStatsByMostTimeTextView;
  TextView sortActivityStatsByLeastTimeTextView;
  TextView sortActivityStatsByMostCaloriesTextView;
  TextView sortActivityStatsByLeastCaloriesTextView;

  TextView sortFoodConsumedStatsAToZTextView;
  TextView sortFoodConsumedStatsZToATextView;
  TextView sortFoodConsumedCaloriesByMostTextView;
  TextView sortFoodConsumedCaloriesByLeastTextView;

  TextView delete_all_text;
  Button delete_all_confirm;
  Button delete_all_cancel;
  TextView appHeader;
  ImageButton edit_highlighted_cycle;
  ImageButton delete_highlighted_cycle;
  ImageButton cancelHighlight;
  TextView sortButton;

  int mode = 1;
  int sortMode = 1;
  int sortModePom = 1;
  int sortHolder = 1;

  int sortModeForActivities;
  int sortModeForFoodConsumed;

  int positionOfSelectedCycle = 0;
  String cycleTitle = "";
  List<Integer> receivedHighlightPositions;

  ImageButton addRoundToCycleButton;
  ImageButton subtractRoundFromCycleButton;
  ImageButton toggleInfinityRounds;
  ImageButton buttonToLaunchTimerFromEditPopUp;

  ArrayList<Integer> workoutTime;
  ArrayList<String> convertedWorkoutTime;
  ArrayList<String> workoutCyclesArray;
  ArrayList<String> workoutTitle;
  ArrayList<String> workoutTitleArray;
  ArrayList<Boolean> tdeeIsBeingTrackedInCycleList;
  ArrayList<Boolean> tdeeActivityExistsInCycleList;
  ArrayList<String> workoutActivityStringArray;

  ArrayList<Integer> pomValuesTime;
  ArrayList<String> pomStringListOfRoundValues;
  ArrayList<String> pomTitleArray;
  ArrayList<String> pomArray;
  int ADDING_CYCLE = 1;
  int EDITING_CYCLE = 2;
  int DELETING_CYCLE = 3;

  Runnable globalSaveTotalTimesAndCaloriesInDatabaseRunnable;
  Runnable globalSaveTotalTimesOnPostDelayRunnableInASyncThread;

  ArrayList<Integer> typeOfRound;
  ArrayList<String> typeOfRoundArray;
  int roundType;
  ArrayList<String> workoutStringListOfRoundValuesForFirstAdapter;
  ArrayList<String> workoutStringListOfRoundValuesForSecondAdapter;
  ArrayList<Integer> workoutIntegerListOfRoundTypeForFirstAdapter;
  ArrayList<Integer> workoutIntegerListOfRoundTypeForSecondAdapter;

  int setValue;
  int breakValue;
  int pomValue1;
  int pomValue2;
  int pomValue3;
  int editHeaderSelected = 1;

  Handler mHandler;
  Handler mSavingCycleHandler;
  boolean currentlyEditingACycle;
  InputMethodManager inputMethodManager;

  AlphaAnimation fadeInForCustomActionBar;
  AlphaAnimation fadeOutForCustomActionBar;
  AlphaAnimation fadeInForEditCycleButton;
  AlphaAnimation fadeOutForEditCycleButton;
  Animation slideLeft;
  Animation slideInFromLeftLong;
  Animation slideOutFromLeftLong;
  Animation slideInFromLeftShort;
  Animation slideOutFromLeftShort;
  boolean isAnimationActive;

  ConstraintLayout.LayoutParams cycleTitleLayoutParams;
  ConstraintLayout.LayoutParams cyclesCompletedLayoutParams;
  ConstraintLayout.LayoutParams totalSetTimeHeaderLayoutParams;
  ConstraintLayout.LayoutParams totalBreakTimeHeaderLayoutParams;
  ConstraintLayout.LayoutParams progressBarLayoutParams;
  ConstraintLayout.LayoutParams timerTextViewLayoutParams;

  ConstraintLayout.LayoutParams roundRecyclerParentLayoutParams;
  ConstraintLayout.LayoutParams roundRecyclerOneLayoutParams;
  ConstraintLayout.LayoutParams roundRecyclerTwoLayoutParams;

  ConstraintLayout.LayoutParams firstRoundHeaderParams;
  ConstraintLayout.LayoutParams secondRoundHeaderParams;
  ConstraintLayout.LayoutParams thirdRoundHeaderParams;
  ConstraintLayout.LayoutParams secondEditHeaderParams;
  ConstraintLayout.LayoutParams deleteEditTimerNumbersParams;

  int FADE_IN_HIGHLIGHT_MODE = 1;
  int FADE_OUT_HIGHLIGHT_MODE = 2;
  int FADE_IN_EDIT_CYCLE = 3;
  int FADE_OUT_EDIT_CYCLE = 4;

  boolean subtractedRoundIsFading;
  boolean roundIsSelected;
  boolean consolidateRoundAdapterLists;
  int roundSelectedPosition;

  int CYCLE_LAUNCHED_FROM_RECYCLER_VIEW = 0;
  int CYCLES_LAUNCHED_FROM_EDIT_POPUP = 1;

  PopupWindow timerPopUpWindow;
  View timerPopUpView;
  PopupWindow stopWatchPopUpWindow;
  View stopWatchPopUpView;

  ProgressBar progressBar;
  TextView timeLeft;
  CountDownTimer timer;
  TextView reset;
  ObjectAnimator objectAnimator;
  ObjectAnimator objectAnimatorPom;
  Animation endAnimationForTimer;
  Animation endAnimationForStopwatch;

  TextView tracking_daily_stats_header_textView;
  TextView cycle_title_textView;
  TextView cycles_completed_textView;

  TextView laps_completed_textView;
  LapListCanvas lapListCanvas;
  RecyclerView lapRecycler;
  ImageButton new_lap;
  LapAdapter lapAdapter;
  ImageButton stopWatchPauseResumeButton;
  TextView stopWatchTimeTextView;
  TextView msTimeTextView;
  TextView empty_laps;
  TextView stopwatchReset;

  ConstraintLayout nonTrackingTimerHeaderLayout;
  ConstraintLayout trackingTimerHeaderLayout;

  TextView dailyTotalTimeTextViewHeader;
  TextView dailyTotalTimeTextView;
  TextView dailyTotalCaloriesTextViewHeader;
  TextView dailyTotalCaloriesTextView;

  TextView dailySingleActivityStringHeader;
  TextView dailyTotalTimeForSingleActivityTextViewHeader;
  TextView dailyTotalTimeForSingleActivityTextView;
  TextView dailyTotalCaloriesForSingleActivityTextViewHeader;
  TextView dailyTotalCaloriesForSingleActivityTextView;

  ImageButton next_round;
  ImageButton reset_total_cycle_times;

  int typeOfTotalTimeToDisplay;
  int TOTAL_CYCLE_TIMES = 0;
  int TOTAL_DAILY_TIMES = 1;

  TextView total_set_header;
  TextView total_break_header;
  TextView total_set_time;
  TextView total_break_time;

  boolean activeCycle;
  int PAUSING_TIMER = 1;
  int RESUMING_TIMER = 2;

  int RESUMING_CYCLE_FROM_ADAPTER = 1;
  int RESETTING_CYCLE_FROM_ADAPTER = 2;

  long setMillis;
  long breakMillis;

  int TOTAL_TIMES_FOR_CYCLE;
  int TOTAL_TIMES_FOR_DAY;

  long totalCycleSetTimeInMillis;
  long totalCycleBreakTimeInMillis;

  long totalCycleWorkTimeInMillis;
  long totalCycleRestTimeInMillis;

  long totalSetTimeForCurrentDayInMillis;
  double totalCaloriesBurnedForCurrentDay;

  long totalSetTimeForSpecificActivityForCurrentDayInMillis;
  double totalCaloriesBurnedForSpecificActivityForCurrentDay;

  String timeLeftValueHolder;

  long pomMillis;

  int maxProgress = 10000;
  int currentProgressBarValue = 10000;
  long setMillisUntilFinished;
  long breakMillisUntilFinished;

  long pomMillisUntilFinished;
  Runnable endFadeForModeOne;
  Runnable endFadeForModeThree;
  int pomDotCounter;

  double stopWatchMs;
  double stopWatchSeconds;
  double stopWatchMinutes;
  double savedLapStopWatchMs;

  String displayMs = "00";
  String displayTime = "0";
  String newEntries;
  String savedEntries;
  ArrayList<String> currentLapList;
  ArrayList<String> savedLapList;

  int cyclesCompleted;
  int lapsNumber;

  int startRounds;
  int numberOfRoundsLeft;
  int currentRound;

  boolean timerEnded;
  boolean timerDisabled;
  boolean timerIsPaused = true;
  boolean stopWatchIsPaused = true;
  boolean stopWatchTimerEnded;

  LinearLayoutManager lapRecyclerLayoutManager;
  ConstraintLayout roundRecyclerLayout;

  ValueAnimator sizeAnimator;
  ValueAnimator valueAnimatorDown;
  ValueAnimator valueAnimatorUp;
  AlphaAnimation fadeProgressIn;
  AlphaAnimation fadeProgressOut;

  VerticalSpaceItemDecoration verticalSpaceItemDecoration;
  boolean cyclesTextSizeHasChanged;
  boolean pomCyclesTextSizeHasChanged;
  boolean stopWatchTextSizeHasChanged;

  //  int receivedAlpha;
  float receivedDotAlpha;
  float receivedPomDotAlpha;
  View pauseResumeButton;

  Runnable infinityTimerForSetsRunnable;
  Runnable infinityTimerForBreaksRunnable;
  Runnable stopWatchTimerRunnable;

  Runnable infinityRunnableForCyclesTimer;
  Runnable infinityRunnableForPomCyclesTimer;
  Runnable infinityRunnableForDailyActivityTimer;

  Runnable globalNotficationsRunnable;

  int CYCLE_TIME_TO_ITERATE;

  int CYCLE_SETS = 0;
  int CYCLE_BREAKS = 1;
  int POM_CYCLE_WORK = 2;
  int POM_CYCLE_REST = 3;

  int TOTAL_DAILY_TIME = 0;
  int SINGLE_ACTIVITY_TIME = 1;

  long countUpMillisHolder;
  boolean makeCycleAdapterVisible;
  boolean timerPopUpIsVisible;

  NotificationManagerCompat notificationManagerCompat;
  NotificationCompat.Builder notificationManagerBuilder;
  static boolean dismissNotification = true;

  String oldCycleTitleString;
  ArrayList<String> oldCycleRoundListOne;
  ArrayList<String> oldCycleRoundListTwo;
  ArrayList<String> oldPomRoundList;
  Vibrator vibrator;

  int SOUND_SETTINGS;
  int COLOR_SETTINGS;
  int ABOUT_SETTINGS;

  long[] vibrationPatternForSets;
  long[] vibrationPatternForBreaks;

  int vibrationSettingForSets;
  int vibrationSettingForBreaks;
  boolean isLastRoundSoundContinuous;
  int vibrationSettingForWork;
  int vibrationSettingForMiniBreaks;
  boolean isFullBreakSoundContinuous;

  int setColor;
  int breakColor;
  int workColor;
  int miniBreakColor;
  int fullBreakColor;

  Uri ringToneUri;
  MediaPlayer mediaPlayer;
  AudioManager audioManager;

  DailyStatsFragment dailyStatsFragment;
  RootSettingsFragment rootSettingsFragment;
  SoundSettingsFragment soundSettingsFragment;
  ColorSettingsFragment colorSettingsFragment;
  tdeeSettingsFragment tdeeSettingsFragment;
  DisclaimerFragment disclaimerFragment;

  ChangeSettingsValues changeSettingsValues;

  FrameLayout mainActivityFragmentFrameLayout;
  FragmentTransaction ft;

  long stopWatchstartTime;
  long stopWatchTotalTime;
  long stopWatchTotalTimeHolder;

  long stopWatchNewLapTime;
  long stopWatchNewLapHolder;

  View.MeasureSpec measureSpec;
  int phoneHeight;
  int phoneWidth;

  TDEEChosenActivitySpinnerValues tDEEChosenActivitySpinnerValues;
  TextView addTDEEfirstMainTextView;
  ImageView removeTdeeActivityImageView;
  PopupWindow addTdeePopUpWindow;
  View addTDEEPopUpView;

  Spinner tdee_category_spinner;
  Spinner tdee_sub_category_spinner;
  ArrayAdapter tdeeCategoryAdapter;
  ArrayAdapter tdeeSubCategoryAdapter;

  int selectedTdeeCategoryPosition;
  int selectedTdeeSubCategoryPosition;

  TextView caloriesBurnedInTdeeAdditionTextView;
  TextView metScoreTextView;
  Button confirmActivityAdditionValues;

  boolean metricMode;
  String userGender;
  int userAge;
  int userWeight;
  int userHeight;

  int tdeeMinutesDuration;
  int tdeeSecondsDuration;
  double metScore;
  boolean cycleHasActivityAssigned;
  boolean trackActivityWithinCycle;
  int dayOfYear;

  int timerRunnableDelay = 50;
  String timerTextViewStringOne = "";
  String timerTextViewStringTwo = "";
  int delayBeforeTimerBeginsSyncingWithTotalTimeStats = 1000;

  int SORTING_CYCLES = 0;
  int SORTING_ACTIVITIES = 1;
  int SORTING_FOOD_CONSUMED = 2;
  int DISABLE_SORTING = -1;

  int ON_STATS_ACTIVITY_TAB = 0;
  int ON_STATS_FOOD_TAB = 1;

  Toast mToast;

  RecyclerView dotsRecycler;
  DotsAdapter dotsAdapter;
  ConstraintLayout dotsRecyclerLayout;
  GridView dotsGridView;

  RecyclerView pomDotsRecycler;
  PomDotsAdapter pomDotsAdapter;
  List<String> roundListForDots;

  ConstraintLayout.LayoutParams trackingHeaderLayoutParams;
  ConstraintLayout.LayoutParams nonTrackingHeaderLayoutParams;

  ConstraintLayout.LayoutParams dotsRecyclerLayoutParams;
  ConstraintLayout.LayoutParams dotsRecyclerConstraintLayout_LayoutParams;
  ConstraintLayout progressBarLayout;

  int DAY_MODE = 0;
  int NIGHT_MODE = 1;
  int colorThemeMode = NIGHT_MODE;

  String savedTotalDailyTimeString;
  String savedSingleActivityString;

  //Todo: Our String -> Int -> Long method needs to account for hours display, as well.
  //Todo: Cycles set/break time will also start 1 higher if dismissed/resumed from close to next second.
  //Todo: Cap @ test infinity rounds at 90 minutes.
  //Todo: If deleting stats and in reset/resume mode, stats will not show as reset in Timer.
  //Todo: Had two rows highlights (as in reset/resume) in Cycles.

  //Todo: Test minimized vibrations on <26 api
  //Todo: Test extra-large screens as well
  //Todo: Test w/ fresh install for all default values.
  //Todo: Test everything 10x. Incl. round selection/replacement.
  //Todo: Run code inspector for redundancies, etc.
  //Todo: Rename app, of course.
  //Todo: Backup cloud option.

  //Todo: Sub cat row in activity addition  + timer textView may not appear on first app launch (on moto g5).
  //Todo: Likely a more efficient way to handle disabling lap adapter animation.
  //Todo: Activity time runnable display will skip if removed/re-posted after in-transition day change.

  //Todo: Add Day/Night modes.

  //Todo: ***If still having sync issues w/ daily times it's because they can be higher than their textView shows (e.g. 25200 can still show as "24") because their textView only changes when timer changes. We have a String->Int->Long method now to correct this.
  //Todo: REMINDER, Try next app w/ Kotlin + learn Kotlin.

  @Override
  public void onResume() {
    super.onResume();
    setVisible(true);
    dismissNotification = true;
    notificationManagerCompat.cancel(1);

    mHandler.removeCallbacks(globalNotficationsRunnable);
  }

  @Override
  public void onPause() {
    super.onPause();
  }

  @Override
  public void onStop() {
    super.onStop();
    setVisible(false);
    if (timerPopUpWindow.isShowing() || stopWatchPopUpWindow.isShowing()) {
      dismissNotification = false;

      globalNotficationsRunnable = notifcationsRunnable();
      mHandler.post(globalNotficationsRunnable);
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  //Remember, this does not execute if we are dismissing a popUp.
  @Override
  public void onBackPressed() {
    if (!timerPopUpIsVisible && mainActivityFragmentFrameLayout.getVisibility() == View.INVISIBLE) {
      return;
    }

    if (rootSettingsFragment.isVisible() || dailyStatsFragment.isVisible()) {
      if (!isAnimationActive) {
        if (rootSettingsFragment.isVisible()) {
          mainActivityFragmentFrameLayout.startAnimation(slideOutFromLeftShort);
        }

        if (dailyStatsFragment.isVisible()) {
          dailyStatsFragment.executeTurnOffEditModeMethod();
          mainActivityFragmentFrameLayout.startAnimation(slideOutFromLeftShort);
          sortButton.setAlpha(1.0f);
          sortButton.setEnabled(true);
        }

        setTypeOfOnOptionsSelectedMenu(DEFAULT_MENU);
        toggleSortMenuViewBetweenCyclesAndActivities(SORTING_CYCLES);
      }

      sortButton.setVisibility(View.VISIBLE);
    }

    if (soundSettingsFragment.isVisible() || colorSettingsFragment.isVisible() || tdeeSettingsFragment.isVisible() || disclaimerFragment.isVisible()) {
      getSupportFragmentManager().beginTransaction()
              .setCustomAnimations(
                      0,  // enter
                      R.anim.slide_out_from_left_mid  // exit
              )
              .replace(R.id.settings_fragment_frameLayout, rootSettingsFragment)
              .commit();
      setEditPopUpTimerHeaders(1);
    }
  }

  @SuppressLint("NonConstantResourceId")
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.daily_stats:
        launchDailyStatsFragment();
        break;
      case R.id.global_settings:
        launchGlobalSettingsFragment();
        break;
      case R.id.delete_all_cycles:
        if (mode == 1 && workoutCyclesArray.size() == 0 || mode == 3 && pomArray.size() == 0) {
          showToastIfNoneActive("No cycles to delete!");
        } else {
          delete_all_text.setText(R.string.delete_all_cycles);
          deleteCyclePopupWindow.showAtLocation(mainView, Gravity.CENTER, 0, 0);
        }
        break;
      case R.id.delete_single_day_from_daily_stats:
        if (dailyStatsFragment.getSelectedTab() == 0) {
          delete_all_text.setText(R.string.delete_activities_from_selected_duration);
        }
        if (dailyStatsFragment.getSelectedTab() == 1) {
          delete_all_text.setText(R.string.delete_food_from_single_day);
        }

        deleteCyclePopupWindow.showAtLocation(mainView, Gravity.CENTER, 0, 0);
        break;
      case R.id.delete_all_days_from_daily_stats:
        if (dailyStatsFragment.getSelectedTab() == 0) {
          delete_all_text.setText(R.string.delete_all_activities);
        }
        if (dailyStatsFragment.getSelectedTab() == 1) {
          delete_all_text.setText(R.string.delete_all_foods);
        }

        deleteCyclePopupWindow.showAtLocation(mainView, Gravity.CENTER, 0, 0);
        break;
      case R.id.about_stats:
        dailyStatsFragment.launchAboutStatsPopUpWindow();
        break;
      case R.id.about_settings:
        aboutSettingsPopUpWindow.showAtLocation(mainView, Gravity.CENTER_VERTICAL, 0, 0);
    }
    return super.onOptionsItemSelected(item);
  }

  private void launchGlobalSettingsFragment() {
    if (mainActivityFragmentFrameLayout.getVisibility() == View.INVISIBLE) {
      mainActivityFragmentFrameLayout.startAnimation(slideInFromLeftShort);
      mainActivityFragmentFrameLayout.setVisibility(View.VISIBLE);

      if (rootSettingsFragment != null) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings_fragment_frameLayout, rootSettingsFragment)
                .commit();
      }
      sortButton.setVisibility(View.INVISIBLE);

      setTypeOfOnOptionsSelectedMenu(SETTINGS_MENU);
    }
  }

  private void launchDailyStatsFragment() {
    if (mainActivityFragmentFrameLayout.getVisibility() == View.INVISIBLE) {
      mainActivityFragmentFrameLayout.startAnimation(slideInFromLeftShort);
    }

    mainActivityFragmentFrameLayout.setVisibility(View.VISIBLE);

    getSupportFragmentManager().beginTransaction()
            .setCustomAnimations(
                    R.anim.slide_in_from_left,  // enter
                    R.anim.slide_out_from_right,  // exit
                    R.anim.slide_in_from_left_short,   // popEnter (backstack)
                    R.anim.slide_out_from_right  // popExit (backstack)
            )
            .replace(R.id.settings_fragment_frameLayout, dailyStatsFragment)
            .commit();

    setTypeOfOnOptionsSelectedMenu(STATS_MENU);
    toggleSortMenuViewBetweenCyclesAndActivities(SORTING_ACTIVITIES);

    mHandler.postDelayed(() -> {
      AsyncTask.execute(() -> {
        dailyStatsFragment.populateListsAndTextViewsFromEntityListsInDatabase();
      });
    }, 100);
  }

  private void deleteActivityStatsForSelectedDays() {
    List<StatsForEachActivity> statsForEachActivityList = dailyStatsFragment.getStatsForEachActivityList();
    List<Long> longListOfStatsForEachIdsToDelete = new ArrayList<>();

    for (int i = 0; i < statsForEachActivityList.size(); i++) {
      longListOfStatsForEachIdsToDelete.add(statsForEachActivityList.get(i).getUniqueIdTiedToTheSelectedActivity());
    }

    dailyStatsAccess.deleteMultipleStatsForEachActivityEntries(longListOfStatsForEachIdsToDelete);

    dailyStatsFragment.setNumberOfDaysWithActivitiesHasChangedBoolean(true);
    dailyStatsFragment.populateListsAndTextViewsFromEntityListsInDatabase();
  }

  private void deleteFoodStatsForSelectedDays() {
    List<CaloriesForEachFood> caloriesForEachFoodList = dailyStatsFragment.getCaloriesForEachFoodList();
    List<Long> longListOfFoodIdsToDelete = new ArrayList<>();

    for (int i = 0; i < caloriesForEachFoodList.size(); i++) {
      longListOfFoodIdsToDelete.add(caloriesForEachFoodList.get(i).getUniqueIdTiedToEachFood());
    }

    dailyStatsAccess.deleteMultipleFoodEntries(longListOfFoodIdsToDelete);
    dailyStatsFragment.populateListsAndTextViewsFromEntityListsInDatabase();
  }

  private void deleteActivityStatsForAllDays() {
    dailyStatsAccess.deleteAllStatsForEachActivityEntries();

    dailyStatsFragment.setNumberOfDaysWithActivitiesHasChangedBoolean(true);
    dailyStatsFragment.populateListsAndTextViewsFromEntityListsInDatabase();
  }

  private void deleteFoodStatsForAllDays() {
    dailyStatsAccess.deleteAllFoodEntries();
    dailyStatsFragment.populateListsAndTextViewsFromEntityListsInDatabase();
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    menu.clear();

    if (mOnOptionsSelectedMenuType == DEFAULT_MENU) {
      getMenuInflater().inflate(R.menu.main_options_menu, menu);
    }
    if (mOnOptionsSelectedMenuType == STATS_MENU) {
      getMenuInflater().inflate(R.menu.daily_stats_options_menu, menu);
    }
    if (mOnOptionsSelectedMenuType == FILLER_MENU) {
      getMenuInflater().inflate(R.menu.daily_stats_option_menu_comparison_tab, menu);
    }
    if (mOnOptionsSelectedMenuType == SETTINGS_MENU) {
      getMenuInflater().inflate(R.menu.settings_menu, menu);
    }
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public void onChangeOnOptionsMenu(int menuNumber) {
    mOnOptionsSelectedMenuType = menuNumber;
//    invalidateOptionsMenu();
  }

  @Override
  public void settingsData(int settingNumber) {
    Fragment fragmentToReplace = new Fragment();
    if (settingNumber == 1) fragmentToReplace = soundSettingsFragment;
    if (settingNumber == 2) fragmentToReplace = colorSettingsFragment;
    if (settingNumber == 3) fragmentToReplace = tdeeSettingsFragment;
    if (settingNumber == 4) fragmentToReplace = disclaimerFragment;

    getSupportFragmentManager().beginTransaction()
            .replace(R.id.settings_fragment_frameLayout, fragmentToReplace)
            .commit();
  }

  @Override
  public void changeSoundSetting(int typeOfRound, int settingNumber) {
    assignSoundSettingValues(typeOfRound, settingNumber);
  }

  @Override
  public void changeColorSetting(int receivedMode, int typeOFRound, int settingNumber) {
    cycleRoundsAdapter.changeColorSetting(typeOFRound, settingNumber);
    cycleRoundsAdapterTwo.changeColorSetting(typeOFRound, settingNumber);

    if (receivedMode == 1) {
      savedCycleAdapter.changeColorSetting(typeOFRound, settingNumber);
      savedCycleAdapter.notifyDataSetChanged();

      dotsAdapter.changeColorSetting(typeOFRound, settingNumber);
    }

    if (receivedMode == 3) {
      savedPomCycleAdapter.changeColorSetting(typeOFRound, settingNumber);
      savedPomCycleAdapter.notifyDataSetChanged();

      pomDotsAdapter.changeColorSetting(typeOFRound, settingNumber);
    }

    assignColorSettingValues(typeOFRound, settingNumber);
  }

  @Override
  public void sendAlphaValue(float alpha) {
    receivedDotAlpha = alpha;
  }

  @Override
  public void sendPomAlphaValue(float alpha) {
    receivedPomDotAlpha = alpha;
  }

  @Override
  public void ResumeOrResetCycle(int resumingOrResetting) {
    if (resumingOrResetting == RESUMING_CYCLE_FROM_ADAPTER) {
      resumeOrResetCycleFromAdapterList(RESUMING_CYCLE_FROM_ADAPTER);
    } else if (resumingOrResetting == RESETTING_CYCLE_FROM_ADAPTER) {
      resumeOrResetCycleFromAdapterList(RESETTING_CYCLE_FROM_ADAPTER);
    }
  }

  private void resumeOrResetCycleFromAdapterList(int resumeOrReset) {
    if (resumeOrReset == RESUMING_CYCLE_FROM_ADAPTER) {
      timerIsPaused = true;
      progressBar.setProgress(currentProgressBarValue);

      setTotalCycleTimeValuesToTextView();
      if (mode == 1 && trackActivityWithinCycle) {
        setAllActivityTimesAndCaloriesToTextViews();
        setStoredDailyTimesOnCycleResume();
      }

      AsyncTask.execute(() -> {
        if (trackActivityWithinCycle && dailyStatsFragment.getHaveStatsBeenEditedForCurrentDay()) {
          insertActivityIntoDatabaseAndAssignItsValueToObjects();
          dailyStatsFragment.setStatsHaveBeenEditedForCurrentDay(false);
        }

        runOnUiThread(() -> {
          if (mode == 1) {
            toggleViewsForTotalDailyAndCycleTimes(trackActivityWithinCycle);
            changeTextSizeWithoutAnimator(workoutTime.get(0));

            if (typeOfRound.get(currentRound) == 1 || typeOfRound.get(currentRound) == 2) {
              timeLeft.setText(convertSeconds(dividedMillisForTimerDisplay(setMillis)));
            } else {
              timeLeft.setText(convertSeconds(dividedMillisForTimerDisplay(breakMillis)));
            }

          }
          if (mode == 3) {
            changeTextSizeWithoutAnimator(pomValuesTime.get(0));
            toggleViewsForTotalDailyAndCycleTimes(false);
            timeLeft.setText(convertSeconds(dividedMillisForTimerDisplay(pomMillis)));
          }
          timerPopUpWindow.showAtLocation(mainView, Gravity.NO_GRAVITY, 0, 0);
        });
      });
    } else if (resumeOrReset == RESETTING_CYCLE_FROM_ADAPTER) {
      if (mode == 1) {
        savedCycleAdapter.removeActiveCycleLayout();
        savedCycleAdapter.notifyDataSetChanged();
      }
      if (mode == 3) {
        savedPomCycleAdapter.removeActiveCycleLayout();
        savedPomCycleAdapter.notifyDataSetChanged();
      }
      resetTimer();
    }
  }

  private void timerPopUpDismissalLogic() {
    timerDisabled = false;
    timerPopUpIsVisible = false;
    reset.setVisibility(View.INVISIBLE);

    if (mode == 1) {
      savedCycleAdapter.notifyDataSetChanged();
      pauseAndResumeTimer(PAUSING_TIMER);

      if (trackActivityWithinCycle) {
        storeDailyTimesForCycleResuming();
      }
    } else if (mode == 3) {
      pauseAndResumePomodoroTimer(PAUSING_TIMER);
      savedPomCycleAdapter.notifyDataSetChanged();
    }
    mHandler.removeCallbacksAndMessages(null);
  }

  private void storeDailyTimesForCycleResuming() {
    savedTotalDailyTimeString = (String) dailyTotalTimeTextView.getText();
    savedSingleActivityString = (String) dailyTotalTimeForSingleActivityTextView.getText();
  }

  private void setStoredDailyTimesOnCycleResume() {
    dailyTotalTimeTextView.setText(savedTotalDailyTimeString);
    dailyTotalTimeForSingleActivityTextView.setText(savedSingleActivityString);
  }

  @Override
  public void toggleTdeeMode(int positionToToggle) {
    savedCycleAdapter.modifyActiveTdeeModeToggleList(positionToToggle);
    savedCycleAdapter.setPositionToToggle(positionToToggle);
    savedCycleAdapter.notifyDataSetChanged();

    AsyncTask.execute(()-> {
      saveActivityTrackingToggleToDatabase(positionToToggle);
    });
  }

  private void saveActivityTrackingToggleToDatabase(int positionToggled) {
    int cycleId = cyclesList.get(positionToggled).getId();
    Cycles cyclesToUpdate = cyclesDatabase.cyclesDao().loadSingleCycle(cycleId).get(0);

    boolean newTrackingBoolean = savedCycleAdapter.getBooleanDeterminingIfWeAreTrackingActivity(positionToggled);
    cyclesToUpdate.setCurrentlyTrackingCycle(newTrackingBoolean);

    cyclesDatabase.cyclesDao().updateCycles(cyclesToUpdate);
  }

  @Override
  public void onCycleClick(int position) {
    isNewCycle = false;
    positionOfSelectedCycle = position;

    if (mode == 1) {
      cycleHasActivityAssigned = savedCycleAdapter.getBooleanDeterminingIfCycleHasActivity(position);
      trackActivityWithinCycle = savedCycleAdapter.getBooleanDeterminingIfWeAreTrackingActivity(position);
    }

    setCyclesOrPomCyclesEntityInstanceToSelectedListPosition(position);

    if (cycleHasActivityAssigned) {
      retrieveCycleActivityPositionAndMetScoreFromCycleList();
    }

    populateCycleRoundAndRoundTypeArrayLists();

    dotsAdapter.notifyDataSetChanged();

    if (mode == 1) {
      savedCycleAdapter.removeActiveCycleLayout();
      launchTimerCycle(CYCLE_LAUNCHED_FROM_RECYCLER_VIEW);
    }
    if (mode == 3) {
      savedPomCycleAdapter.removeActiveCycleLayout();
      launchPomTimerCycle(CYCLE_LAUNCHED_FROM_RECYCLER_VIEW);
    }
  }

  @Override
  public void onCycleHighlight(List<Integer> listOfPositions, boolean addButtons) {
    receivedHighlightPositions = listOfPositions;

    if (addButtons) {
      fadeEditCycleButtonsInAndOut(FADE_IN_HIGHLIGHT_MODE);
    }

    if (edit_highlighted_cycle.getAlpha() != 1 && receivedHighlightPositions.size() == 1) {
      edit_highlighted_cycle.setEnabled(true);
      edit_highlighted_cycle.setAlpha(1.0f);
    } else if (edit_highlighted_cycle.getAlpha() == 1 && receivedHighlightPositions.size() != 1) {
      edit_highlighted_cycle.setEnabled(false);
      edit_highlighted_cycle.setAlpha(0.4f);
    }
  }

  @Override
  public void subtractionFadeHasFinished() {
    //When adapter fade on round has finished, execute method to remove the round from adapter list/holders and refresh the adapter display. If we click to remove another round before fade is done, fade gets cancelled, restarted on next position, and this method is also called to remove previous round.
    removeRound();
    if (consolidateRoundAdapterLists) {
      //Adapters only need adjusting if second one is populated.
      if (workoutTime.size() >= 8) {
        workoutStringListOfRoundValuesForFirstAdapter.clear();
        workoutIntegerListOfRoundTypeForFirstAdapter.clear();
        workoutStringListOfRoundValuesForSecondAdapter.clear();
        workoutIntegerListOfRoundTypeForSecondAdapter.clear();
        for (int i = 0; i < workoutTime.size(); i++) {
          if (i <= 7) {
            workoutStringListOfRoundValuesForFirstAdapter.add(convertedWorkoutTime.get(i));
            workoutIntegerListOfRoundTypeForFirstAdapter.add(typeOfRound.get(i));
          } else {
            workoutStringListOfRoundValuesForSecondAdapter.add(convertedWorkoutTime.get(i));
            workoutIntegerListOfRoundTypeForSecondAdapter.add(typeOfRound.get(i));
          }
        }
        cycleRoundsAdapter.notifyDataSetChanged();
        cycleRoundsAdapterTwo.notifyDataSetChanged();
      }
      consolidateRoundAdapterLists = false;
    }
    //When fade animation for removing Pomodoro cycle is finished in adapter, its listener calls back here where we remove the cycle's values and update adapter w/ empty list.
    if (mode == 3) {
      pomValuesTime.clear();
      pomStringListOfRoundValues.clear();
      cycleRoundsAdapter.notifyDataSetChanged();
      cycleRoundsAdapter.disablePomFade();
      subtractRoundFromCycleButton.setClickable(true);
    }
  }

  @Override
  public void roundSelected(boolean selected, int position) {
    if (selected) {
      cycleRoundsAdapter.isRoundCurrentlySelected(true);
      cycleRoundsAdapterTwo.isRoundCurrentlySelected(false);
      roundIsSelected = true;
      roundSelectedPosition = position;
    } else {
      cycleRoundsAdapter.isRoundCurrentlySelected(false);
      roundIsSelected = false;
    }

    cycleRoundsAdapter.notifyDataSetChanged();
    cycleRoundsAdapterTwo.notifyDataSetChanged();
  }

  @Override
  public void roundSelectedSecondAdapter(boolean selected, int position) {
    if (selected) {
      cycleRoundsAdapter.isRoundCurrentlySelected(false);
      cycleRoundsAdapterTwo.isRoundCurrentlySelected(true);
      roundIsSelected = true;
      roundSelectedPosition = position;
    } else {
      cycleRoundsAdapterTwo.isRoundCurrentlySelected(false);
      roundIsSelected = false;
    }

    cycleRoundsAdapter.notifyDataSetChanged();
    cycleRoundsAdapterTwo.notifyDataSetChanged();
  }

  @Override
  public boolean onCreateOptionsMenu(final Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_options_menu, menu);
    return true;
  }

  private void setPhoneDimensions() {
    DisplayMetrics metrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(metrics);

    phoneHeight = metrics.heightPixels;
    phoneWidth = metrics.widthPixels;

    Log.i("testDimensions", "height is " + phoneHeight + " and width is " + phoneWidth);
  }

  private void setEditCyclesLayoutForDifferentHeights() {
    LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    if (phoneHeight <= 1920) {
      editCyclesPopupView = inflater.inflate(R.layout.editing_cycles_h1920, null);
    } else {
      editCyclesPopupView = inflater.inflate(R.layout.editing_cycles, null);
    }

    editCyclesPopupWindow = new PopupWindow(editCyclesPopupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true);

  }

  private void setTimerLayoutForDifferentHeights() {
    LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    if (phoneHeight <= 1920) {
      timerPopUpView = inflater.inflate(R.layout.timer_popup_h1920, null);
    } else {
      timerPopUpView = inflater.inflate(R.layout.timer_popup, null);
    }

    timerPopUpWindow = new PopupWindow(timerPopUpView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true);
  }

  private void setStopWatchLayoutForDifferentHeights() {
    LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    if (phoneHeight <= 1920) {
      stopWatchPopUpView = inflater.inflate(R.layout.stopwatch_popup_h1920, null);
    } else {
      stopWatchPopUpView = inflater.inflate(R.layout.stopwatch_popup, null);
    }

    stopWatchPopUpWindow = new PopupWindow(stopWatchPopUpView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true);
  }

  @SuppressLint({"UseCompatLoadingForDrawables", "ClickableViewAccessibility", "CommitPrefEdits", "CutPasteId"})
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    setPhoneDimensions();
    groupAllAppStartInstantiations();

    toggleDayAndNightModesForMain(colorThemeMode);
    toggleDayAndNightModesForTimer(colorThemeMode);

    stopWatchTimerRunnable = stopWatchRunnable();
    infinityTimerForSetsRunnable = infinityRunnableForSets();
    infinityTimerForBreaksRunnable = infinityRunnableForBreaks();

    addTDEEfirstMainTextView.setOnClickListener(v -> {
      inputMethodManager.hideSoftInputFromWindow(editCyclesPopupView.getWindowToken(), 0);

      View testView = editCyclesPopupView.findViewById(R.id.bottom_edit_title_divider);
      addTdeePopUpWindow.showAsDropDown(testView);
    });

    confirmActivityAdditionValues.setOnClickListener(v -> {
      addTdeePopUpWindow.dismiss();
      toggleEditPopUpViewsForAddingActivity(true);
    });

    removeTdeeActivityImageView.setOnClickListener(v -> {
      toggleEditPopUpViewsForAddingActivity(false);
    });

    fab.setOnClickListener(v -> {
      fabLogic();
      removeCycleHighlights();

    });

    sortButton.setOnClickListener(v -> {
      sortPopupWindow.showAtLocation(mainView, Gravity.END | Gravity.TOP, 0, 0);
    });

    timerPopUpWindow.setOnDismissListener(() -> {
      timerPopUpDismissalLogic();

      activateResumeOrResetOptionForCycle();
      replaceCycleListWithEmptyTextViewIfNoCyclesExist();
      toggleCycleAndPomCycleRecyclerViewVisibilities(false);

      toggleCustomActionBarButtonVisibilities(false);

      AsyncTask.execute(globalSaveTotalTimesAndCaloriesInDatabaseRunnable);
    });

    editCyclesPopupWindow.setOnDismissListener(() -> {
      editCyclesPopUpDismissalLogic();
      setDefaultEditRoundViews();

      replaceCycleListWithEmptyTextViewIfNoCyclesExist();
    });

    edit_highlighted_cycle.setOnClickListener(v -> {
      fadeEditCycleButtonsInAndOut(FADE_IN_EDIT_CYCLE);
      removeCycleHighlights();
      editHighlightedCycleLogic();

      if (mode == 1) cycles = cyclesList.get(positionOfSelectedCycle);
      if (mode == 3) pomCycles = pomCyclesList.get(positionOfSelectedCycle);

      clearRoundAndCycleAdapterArrayLists();

      populateCycleRoundAndRoundTypeArrayLists();
      populateRoundAdapterArraysForHighlightedCycle();

      setRoundRecyclerViewsWhenChangingAdapterCount(workoutTime);
      assignOldCycleValuesToCheckForChanges();
    });

    //Turns off our cycle highlight mode from adapter.
    cancelHighlight.setOnClickListener(v -> {
      removeCycleHighlights();
      fadeEditCycleButtonsInAndOut(FADE_OUT_HIGHLIGHT_MODE);
    });

    delete_highlighted_cycle.setOnClickListener(v -> {
      AsyncTask.execute(() -> {
        deleteHighlightedCycles();
      });

      delete_highlighted_cycle.setEnabled(false);
      fadeEditCycleButtonsInAndOut(FADE_OUT_HIGHLIGHT_MODE);

      if (mode == 1) savedCycleAdapter.removeHighlight();
      if (mode == 3) savedPomCycleAdapter.removeHighlight();

      showToastIfNoneActive("Deleted!");
    });

    //Selects all text when long clicking in editText.
    cycleNameEdit.setOnLongClickListener(v -> {
      cycleNameEdit.selectAll();
      return true;
    });

    firstRoundTypeHeaderInEditPopUp.setOnClickListener(v -> {
      setEditPopUpTimerHeaders(1);
    });

    secondRoundTypeHeaderInEditPopUp.setOnClickListener(v -> {
      setEditPopUpTimerHeaders(2);
    });

    thirdRoundTypeHeaderInEditPopUp.setOnClickListener(v -> {
      setEditPopUpTimerHeaders(3);
    });

    View.OnClickListener numberPadListener = view -> {
      TextView textButton = (TextView) view;

      if (mode == 1) {
        addToEditPopUpTimerArrays(editPopUpTimerArray, textButton);
      }

      if (mode == 3) {
        if (editHeaderSelected == 1)
          addToEditPopUpTimerArrays(savedEditPopUpArrayForFirstHeaderModeThree, textButton);
        if (editHeaderSelected == 2)
          addToEditPopUpTimerArrays(savedEditPopUpArrayForSecondHeaderModeThree, textButton);
        if (editHeaderSelected == 3)
          addToEditPopUpTimerArrays(savedEditPopUpArrayForThirdHeader, textButton);

      }
      setEditPopUpTimerStringValues();
    };

    number_one.setOnClickListener(numberPadListener);
    number_two.setOnClickListener(numberPadListener);
    number_three.setOnClickListener(numberPadListener);
    number_four.setOnClickListener(numberPadListener);
    number_five.setOnClickListener(numberPadListener);
    number_six.setOnClickListener(numberPadListener);
    number_seven.setOnClickListener(numberPadListener);
    number_eight.setOnClickListener(numberPadListener);
    number_nine.setOnClickListener(numberPadListener);
    number_zero.setOnClickListener(numberPadListener);

    deleteEditPopUpTimerNumbers.setOnClickListener(v -> {
      if (mode == 1) {
        if (editPopUpTimerArray.size() > 0) {
          editPopUpTimerArray.remove(editPopUpTimerArray.size() - 1);
        }
      }

      if (mode == 3) {
        if (editHeaderSelected == 1 && savedEditPopUpArrayForFirstHeaderModeThree.size() > 0) {
          savedEditPopUpArrayForFirstHeaderModeThree.remove(savedEditPopUpArrayForFirstHeaderModeThree.size() - 1);
        }
        if (editHeaderSelected == 2 && savedEditPopUpArrayForSecondHeaderModeThree.size() > 0) {
          savedEditPopUpArrayForSecondHeaderModeThree.remove(savedEditPopUpArrayForSecondHeaderModeThree.size() - 1);
        }
        if (editHeaderSelected == 3 && savedEditPopUpArrayForThirdHeader.size() > 0) {
          savedEditPopUpArrayForThirdHeader.remove(savedEditPopUpArrayForThirdHeader.size() - 1);
        }
      }

      setEditPopUpTimerStringValues();
    });

    //For moment, using arrows next to sets and breaks to determine which type of round we're adding.
    addRoundToCycleButton.setOnClickListener(v -> {
      adjustCustom(true);
    });

    subtractRoundFromCycleButton.setOnClickListener(v -> {
      adjustCustom(false);
    });

    toggleInfinityRounds.setOnClickListener(v -> {
      if (toggleInfinityRounds.getAlpha() == 1.0f) {
        toggleInfinityRounds.setAlpha(0.4f);
        if (editHeaderSelected == 1) isSavedInfinityOptionActiveForSets = false;
        if (editHeaderSelected == 2) isSavedInfinityOptionActiveForBreaks = false;
      } else {
        toggleInfinityRounds.setAlpha(1.0f);
        if (editHeaderSelected == 1) isSavedInfinityOptionActiveForSets = true;
        if (editHeaderSelected == 2) isSavedInfinityOptionActiveForBreaks = true;
      }
    });

    buttonToLaunchTimerFromEditPopUp.setOnClickListener(v -> {
      if (mode == 1) {
        launchTimerCycle(CYCLES_LAUNCHED_FROM_EDIT_POPUP);
      }
      if (mode == 3) {
        launchPomTimerCycle(CYCLES_LAUNCHED_FROM_EDIT_POPUP);
      }
    });

    //Listens to our fadeOut animation, and runs fadeIn when it's done.
    fadeProgressOut.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {
      }

      @Override
      public void onAnimationEnd(Animation animation) {
        progressBar.startAnimation(fadeProgressIn);
        timeLeft.startAnimation(fadeProgressIn);
        //Resets progressBar to max (full blue) value at same time we fade it back in (looks nicer).
        progressBar.setProgress(maxProgress);
      }

      @Override
      public void onAnimationRepeat(Animation animation) {
      }
    });

    endFadeForModeOne = new Runnable() {
      @Override
      public void run() {
        dotsAdapter.notifyDataSetChanged();

        if (receivedDotAlpha <= 0.3f) {
          dotsAdapter.setDotAlpha(0.3f);
          dotsAdapter.notifyDataSetChanged();

          mHandler.removeCallbacks(this);
        } else {
          mHandler.postDelayed(this, 50);
        }
      }
    };

    endFadeForModeThree = new Runnable() {
      @Override
      public void run() {
        pomDotsAdapter.notifyDataSetChanged();

        if (receivedPomDotAlpha <= 0.3f) {
          pomDotsAdapter.setPomDotAlpha(0.3f);
          pomDotsAdapter.notifyDataSetChanged();

          mHandler.removeCallbacks(this);
        } else {
          mHandler.postDelayed(this, 50);
        }
      }
    };

    next_round.setOnClickListener(v -> {
      AsyncTask.execute(new Runnable() {
        @Override
        public void run() {
//          cyclesDatabase.cyclesDao().deleteActivityStatsForSingleDay(280);
        }
      });

//      changeDayOfYear(280);

      if (mode == 1) {
        nextRound(true);
      }
      if (mode == 3) {
        nextPomRound(true);
      }
    });

    reset.setOnClickListener(v -> {
      AsyncTask.execute(globalSaveTotalTimesAndCaloriesInDatabaseRunnable);

      if (mode == 1) {
        resetTimer();
      }
      if (mode == 3) {
        if (reset.getText().toString().equals(getString(R.string.reset))) {
          reset.setText(R.string.confirm_cycle_reset);
        } else {
          resetTimer();
        }
      }
    });

    pauseResumeButton.setOnClickListener(v -> {
      if (!timerIsPaused) {
        if (mode == 1) {
          pauseAndResumeTimer(PAUSING_TIMER);
        }
        if (mode == 3) {
          pauseAndResumePomodoroTimer(PAUSING_TIMER);
        }
      } else {
        if (mode == 1) {
          pauseAndResumeTimer(RESUMING_TIMER);
        }
        if (mode == 3) {
          pauseAndResumePomodoroTimer(RESUMING_TIMER);
        }
      }
    });

    reset_total_cycle_times.setOnClickListener(v -> {
      delete_all_text.setText(R.string.delete_cycles_times_and_completed_cycles);
      deleteCyclePopupWindow.showAtLocation(mainView, Gravity.CENTER_HORIZONTAL, 0, -100);
    });

    delete_all_confirm.setOnClickListener(v -> {
      if (deleteCyclePopupWindow.isShowing()) {
        deleteCyclePopupWindow.dismiss();
      }
      Toast.makeText(getApplicationContext(), "Deleted!", Toast.LENGTH_SHORT).show();

      AsyncTask.execute(() -> {
        if (delete_all_text.getText().equals(getString(R.string.delete_cycles_times_and_completed_cycles))) {
          deleteTotalCycleTimes();
        }
        if (delete_all_text.getText().equals(getString(R.string.delete_activities_from_selected_duration))) {
          deleteActivityStatsForSelectedDays();
        }
        if (delete_all_text.getText().equals(getString(R.string.delete_all_activities))) {
          deleteActivityStatsForAllDays();
        }
        if (delete_all_text.getText().equals(getString(R.string.delete_food_from_single_day))) {
          deleteFoodStatsForSelectedDays();
        }
        if (delete_all_text.getText().equals(getString(R.string.delete_all_foods))) {
          deleteFoodStatsForAllDays();
        }
      });
    });

    delete_all_cancel.setOnClickListener(v -> {
      if (deleteCyclePopupWindow.isShowing()) deleteCyclePopupWindow.dismiss();
    });

    stopWatchLaunchButton.setOnClickListener(v -> {
      stopWatchLaunchLogic();
    });

    stopWatchPauseResumeButton.setOnClickListener(v -> {
      if (!stopWatchIsPaused) {
        pauseAndResumeStopwatch(PAUSING_TIMER);
      } else {
        pauseAndResumeStopwatch(RESUMING_TIMER);
      }
    });

    stopwatchReset.setOnClickListener(v -> {
      resetStopwatchTimer();
    });

    new_lap.setOnClickListener(v -> {
      newLapLogic();
    });

    stopWatchPopUpWindow.setOnDismissListener(() -> {
      getSupportActionBar().show();
      removeCycleHighlights();

      if (mode == 1) {
        savedCycleRecycler.setVisibility(View.VISIBLE);
      }
      if (mode == 3) {
        savedPomCycleRecycler.setVisibility(View.VISIBLE);
      }

      savedCyclesTabLayout.setVisibility(View.VISIBLE);
    });
  }

  private void stopWatchLaunchLogic() {
    setInitialTextSizeForStopWatch();

    stopWatchTimeTextView.setText(displayTime);
    msTimeTextView.setText(displayMs);
    laps_completed_textView.setText(getString(R.string.laps_completed, lapsNumber));

    stopWatchPopUpWindow.showAtLocation(mainView, Gravity.NO_GRAVITY, 0, 0);
  }

  private void setLapRecyclerListener() {
    lapRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (!lapAdapter.getHaveWeBegunScrolling()) {
          lapAdapter.setHaveWeBegunScrolling(true);
        }
      }
    });
  }

  @Override
  public void onChangeSortMenu(int typeOfSort) {
    if (typeOfSort == SORTING_ACTIVITIES) {
      sortPopupWindow.setContentView(sortActivitiesPopupView);
    }
    if (typeOfSort == SORTING_FOOD_CONSUMED) {
      sortPopupWindow.setContentView(sortFoodConsumedPopupView);
    }
    if (typeOfSort == DISABLE_SORTING) {
      sortButton.setVisibility(View.GONE);
      sortButton.setAlpha(0.3f);
      sortButton.setEnabled(false);
    } else {
      sortButton.setVisibility(View.VISIBLE);
      sortButton.setAlpha(1.0f);
      sortButton.setEnabled(true);
    }
  }

  private void toggleSortMenuViewBetweenCyclesAndActivities(int typeOfSort) {
    if (typeOfSort == SORTING_CYCLES) {
      sortPopupWindow.setContentView(sortCyclePopupView);
    }
    if (typeOfSort == SORTING_ACTIVITIES) {
      sortPopupWindow.setContentView(sortActivitiesPopupView);
    }
  }

  private void groupAllAppStartInstantiations() {
    instantiateGlobalClasses();
    removeAllTimerSharedPreferences();

    instantiateFragmentsAndTheirCallbacks();
    instantiatePopUpViewsAndWindows();
    instantiateTabLayouts();
    instantiateTabSelectionListeners();

    assignMainLayoutClassesToIds();
    assignEditPopUpLayoutClassesToTheirIds();
    assignTimerPopUpLayoutClassesToTheirIds();
    assignSortPopUpLayoutClassesToTheirIds();
    assignDeletePopUpLayoutClassesToTheirIds();
    assignTdeePopUpLayoutClassesToTheirIds();

    instantiateLayoutParameterObjects();
    instantiateArrayLists();

    instantiateDatabaseClassesViaASyncThreadAndFollowWithUIPopulationOfTheirComponents();

    instantiateLayoutManagers();
    instantiateRoundAdaptersAndTheirCallbacks();
    setRoundRecyclersOnAdaptersAndLayoutManagers();
    setRoundRecyclerViewsWhenChangingAdapterCount(workoutTime);
    setVerticalSpaceDecorationForCycleRecyclerViewBasedOnScreenHeight();
    instantiateLapAdapterAndSetRecyclerOnIt();

    retrieveAndImplementCycleSorting();
    instantiateAnimationAndColorMethods();

    instantiateNotifications();

    instantiateTdeeSpinnersAndSetThemOnAdapters();
    setTdeeSpinnerListeners();
    instantiateSaveTotalTimesAndCaloriesInDatabaseRunnable();
    instantiateSaveTotalTimesOnPostDelayRunnableInASyncThread();

    instantiateDotsRecyclerAndAdapter();

    setAllSortTextViewsOntoClickListeners();

    setDefaultLayoutTexts();
    setDefaultTimerValuesAndTheirEditTextViews();
    setDefaultLayoutVisibilities();

    setLapRecyclerListener();

    dotsAdapter.onAlphaSend(MainActivity.this);
    pomDotsAdapter.onPomAlphaSend(MainActivity.this);
    progressBar.setProgress(maxProgress);
  }

  private void instantiateGlobalClasses() {
    fragmentManager = getSupportFragmentManager();
    changeSettingsValues = new ChangeSettingsValues();
    tDEEChosenActivitySpinnerValues = new TDEEChosenActivitySpinnerValues(getApplicationContext());
    dailyStatsAccess = new DailyStatsAccess(getApplicationContext());
    longToStringConverters = new LongToStringConverters();

    mHandler = new Handler();
    mSavingCycleHandler = new Handler();
    inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    sharedPreferences = getApplicationContext().getSharedPreferences("pref", 0);
    prefEdit = sharedPreferences.edit();

    cycles = new Cycles();
    pomCycles = new PomCycles();

    objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", maxProgress, 0);
    objectAnimatorPom = ObjectAnimator.ofInt(progressBar, "progress", maxProgress, 0);
    Log.i("testProg", "prog bar value on activity launch is " + objectAnimator.getAnimatedValue());


    ringToneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    mediaPlayer = MediaPlayer.create(this, ringToneUri);
    vibrator = (Vibrator) getSystemService(getApplicationContext().VIBRATOR_SERVICE);

    mToast = new Toast(getApplicationContext());
  }

  private void instantiateTabLayouts() {
    savedCyclesTabLayout = findViewById(R.id.savedCyclesTabLayout);
    savedCyclesTabLayout.addTab(savedCyclesTabLayout.newTab().setText("Workouts"));
    savedCyclesTabLayout.addTab(savedCyclesTabLayout.newTab().setText("Pomodoro"));
  }

  private void instantiateTabSelectionListeners() {
    savedCyclesTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      @Override
      public void onTabSelected(TabLayout.Tab tab) {
        savedCyclesTab = tab;
        switch (savedCyclesTab.getPosition()) {
          case 0:
            mode = 1;
            cycleRoundsAdapter.setMode(1);
            dotsAdapter.setModeOneAlpha();

            savedCycleRecycler.setVisibility(View.VISIBLE);
            dotsRecycler.setVisibility(View.VISIBLE);
            savedPomCycleRecycler.setVisibility(View.GONE);
            pomDotsRecycler.setVisibility(View.GONE);
            break;
          case 1:
            mode = 3;
            cycleRoundsAdapter.setMode(3);
            pomDotsAdapter.setModeThreeAlpha();

            savedCycleRecycler.setVisibility(View.GONE);
            dotsRecycler.setVisibility(View.GONE);
            savedPomCycleRecycler.setVisibility(View.VISIBLE);
            pomDotsRecycler.setVisibility(View.VISIBLE);

            break;
        }
        replaceCycleListWithEmptyTextViewIfNoCyclesExist();
        setDefaultEditRoundViews();
        getTimerVariablesForEachMode();
      }

      @Override
      public void onTabUnselected(TabLayout.Tab tab) {
        savedCyclesTab = tab;
        tabUnselectedLogic();

        if (savedCyclesTab.getPosition() == 0) {
          if (savedCycleAdapter.isCycleHighlighted() == true) {
            removeCycleHighlights();
            savedCycleAdapter.notifyDataSetChanged();
          }
          dotsAdapter.saveModeOneAlpha();
        }
        if (savedCyclesTab.getPosition() == 1) {
          if (savedPomCycleAdapter.isCycleHighlighted() == true) {
            removeCycleHighlights();
            savedPomCycleAdapter.notifyDataSetChanged();
          }
          pomDotsAdapter.saveModeThreeAlpha();
        }
      }

      @Override
      public void onTabReselected(TabLayout.Tab tab) {
      }
    });
  }

  private void tabUnselectedLogic() {
    cycleTitle = "";

    if (!sortButton.isEnabled()) fadeEditCycleButtonsInAndOut(FADE_OUT_HIGHLIGHT_MODE);
    if (editCyclesPopupWindow.isShowing()) editCyclesPopupWindow.dismiss();

    receivedHighlightPositions.clear();
  }

  private void assignMainLayoutClassesToIds() {
    mainView = findViewById(R.id.main_layout);
    actionBarView = findViewById(R.id.custom_action_bar);

    getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    getSupportActionBar().setDisplayShowCustomEnabled(true);

    if (phoneHeight <= 1920) {
      getSupportActionBar().setCustomView(R.layout.custom_bar_h1920);
    } else {
      getSupportActionBar().setCustomView(R.layout.custom_bar);
    }

    fab = findViewById(R.id.fab);
    stopWatchLaunchButton = findViewById(R.id.stopwatch_launch_button);
    emptyCycleList = findViewById(R.id.empty_cycle_list);

    savedCycleRecycler = findViewById(R.id.cycle_list_recycler);
    savedPomCycleRecycler = findViewById(R.id.pom_list_recycler);

    appHeader = findViewById(R.id.app_header);
    edit_highlighted_cycle = findViewById(R.id.edit_highlighted_cycle);
    cancelHighlight = findViewById(R.id.cancel_highlight);
    delete_highlighted_cycle = findViewById(R.id.delete_highlighted_cycles);
    sortButton = findViewById(R.id.sortButton);
  }

  private void instantiateFragmentsAndTheirCallbacks() {
    dailyStatsFragment = new DailyStatsFragment();
    rootSettingsFragment = new RootSettingsFragment();
    soundSettingsFragment = new SoundSettingsFragment();
    colorSettingsFragment = new ColorSettingsFragment();
    tdeeSettingsFragment = new tdeeSettingsFragment();
    disclaimerFragment = new DisclaimerFragment();

    rootSettingsFragment.sendSettingsData(MainActivity.this);
    soundSettingsFragment.soundSetting(MainActivity.this);
    colorSettingsFragment.colorSetting(MainActivity.this);

    dailyStatsFragment.setOnOptionsMenu(MainActivity.this);
    dailyStatsFragment.setSortMenu(MainActivity.this);

    mainActivityFragmentFrameLayout = findViewById(R.id.settings_fragment_frameLayout);
    mainActivityFragmentFrameLayout.setVisibility(View.INVISIBLE);

    //Fragment is attached with an invisible FrameLayout. Can simply replace fragments as needed.
    getSupportFragmentManager().beginTransaction()
            .attach(rootSettingsFragment)
            .commit();
  }

  private void assignTdeePopUpLayoutClassesToTheirIds() {
    tdee_category_spinner = addTDEEPopUpView.findViewById(R.id.activity_category_spinner);
    tdee_sub_category_spinner = addTDEEPopUpView.findViewById(R.id.activity_sub_category_spinner);
    confirmActivityAdditionValues = addTDEEPopUpView.findViewById(R.id.add_activity_confirm_button);
    metScoreTextView = addTDEEPopUpView.findViewById(R.id.met_score_textView);
    metScoreTextView.setVisibility(View.GONE);
    caloriesBurnedInTdeeAdditionTextView = addTDEEPopUpView.findViewById(R.id.calories_burned_in_tdee_addition_popUp_textView);
  }

  private void assignEditPopUpLayoutClassesToTheirIds() {
    deleteEditPopUpTimerNumbers = editCyclesPopupView.findViewById(R.id.deleteEditPopUpTimerNumbers);
    number_one = editCyclesPopupView.findViewById(R.id.one_button);
    number_two = editCyclesPopupView.findViewById(R.id.two_button);
    number_three = editCyclesPopupView.findViewById(R.id.three_button);
    number_four = editCyclesPopupView.findViewById(R.id.four_button);
    number_five = editCyclesPopupView.findViewById(R.id.five_button);
    number_six = editCyclesPopupView.findViewById(R.id.six_button);
    number_seven = editCyclesPopupView.findViewById(R.id.seven_button);
    number_eight = editCyclesPopupView.findViewById(R.id.eight_button);
    number_nine = editCyclesPopupView.findViewById(R.id.nine_button);
    number_zero = editCyclesPopupView.findViewById(R.id.zero_button);

    cycleNameEdit = editCyclesPopupView.findViewById(R.id.cycle_name_edit);
    firstRoundTypeHeaderInEditPopUp = editCyclesPopupView.findViewById(R.id.firstRoundTypeHeaderInEditPopUp);
    secondRoundTypeHeaderInEditPopUp = editCyclesPopupView.findViewById(R.id.secondRoundTypeHeaderInEditPopUp);
    thirdRoundTypeHeaderInEditPopUp = editCyclesPopupView.findViewById(R.id.thirdRoundTypeHeaderInEditPopUp);
    timerValueInEditPopUpTextView = editCyclesPopupView.findViewById(R.id.timerValueInEditPopUpTextView);
    pomTimerValueInEditPopUpTextViewOne = editCyclesPopupView.findViewById(R.id.pomTimerValueInEditPopUpTextViewOne);
    pomTimerValueInEditPopUpTextViewTwo = editCyclesPopupView.findViewById(R.id.pomTimerValueInEditPopUpTextViewTwo);
    pomTimerValueInEditPopUpTextViewThree = editCyclesPopupView.findViewById(R.id.pomTimerValueInEditPopUpTextViewThree);

    addRoundToCycleButton = editCyclesPopupView.findViewById(R.id.addRoundToCycleButton);
    subtractRoundFromCycleButton = editCyclesPopupView.findViewById(R.id.subtractRoundFromCycleButton);
    toggleInfinityRounds = editCyclesPopupView.findViewById(R.id.toggle_infinity_rounds);
    buttonToLaunchTimerFromEditPopUp = editCyclesPopupView.findViewById(R.id.buttonToLaunchTimerFromEditPopUp);

    roundRecyclerLayout = editCyclesPopupView.findViewById(R.id.round_recycler_layout);
    roundRecyclerTwo = editCyclesPopupView.findViewById(R.id.round_list_recycler_two);
    roundListDivider = editCyclesPopupView.findViewById(R.id.round_list_divider);
    roundRecycler = editCyclesPopupView.findViewById(R.id.round_list_recycler);

    addTDEEfirstMainTextView = editCyclesPopupView.findViewById(R.id.tdee_add_textView);
    removeTdeeActivityImageView = editCyclesPopupView.findViewById(R.id.cancel_activity_for_cycle);
  }

  private void assignTimerPopUpLayoutClassesToTheirIds() {
    timerView = timerPopUpView.findViewById(R.id.main_timer_layout);

    reset = timerPopUpView.findViewById(R.id.reset);

    nonTrackingTimerHeaderLayout = timerPopUpView.findViewById(R.id.non_tracking_timer_stat_headers_layout);
    nonTrackingHeaderLayoutParams = (ConstraintLayout.LayoutParams) nonTrackingTimerHeaderLayout.getLayoutParams();

    trackingTimerHeaderLayout = timerPopUpView.findViewById(R.id.tracking_timer_header_layout);
    tracking_daily_stats_header_textView = timerPopUpView.findViewById(R.id.tracking_daily_stats_header_textView);
    trackingTimerHeaderLayout = timerPopUpView.findViewById(R.id.tracking_timer_header_layout);
    trackingHeaderLayoutParams = (ConstraintLayout.LayoutParams) trackingTimerHeaderLayout.getLayoutParams();

    cycle_title_textView = timerPopUpView.findViewById(R.id.cycle_title_textView);
    cycles_completed_textView = timerPopUpView.findViewById(R.id.cycles_completed_textView);

    total_set_header = timerPopUpView.findViewById(R.id.total_set_header);
    total_break_header = timerPopUpView.findViewById(R.id.total_break_header);
    total_set_time = timerPopUpView.findViewById(R.id.total_set_time);
    total_break_time = timerPopUpView.findViewById(R.id.total_break_time);

    dailySingleActivityStringHeader = timerPopUpView.findViewById(R.id.daily_single_activity_string_header);
    dailyTotalTimeTextViewHeader = timerPopUpView.findViewById(R.id.daily_total_time_textView_header);
    dailyTotalTimeTextView = timerPopUpView.findViewById(R.id.daily_total_time_textView);
    dailyTotalCaloriesTextViewHeader = timerPopUpView.findViewById(R.id.daily_total_calories_textView_header);
    dailyTotalCaloriesTextView = timerPopUpView.findViewById(R.id.daily_total_calories_textView);

    dailyTotalTimeTextViewHeader.setText(R.string.total_daily_time);
    dailyTotalCaloriesTextViewHeader.setText(R.string.total_daily_calories);

    dailyTotalTimeForSingleActivityTextViewHeader = timerPopUpView.findViewById(R.id.daily_total_time_for_single_activity_textView_header);
    dailyTotalTimeForSingleActivityTextView = timerPopUpView.findViewById(R.id.daily_total_time_for_single_activity_textView);
    dailyTotalCaloriesForSingleActivityTextViewHeader = timerPopUpView.findViewById(R.id.daily_total_calories_for_single_activity_textView_header);
    dailyTotalCaloriesForSingleActivityTextView = timerPopUpView.findViewById(R.id.daily_total_calories_for_single_activity_textView);

    dailyTotalTimeForSingleActivityTextViewHeader.setText(R.string.total_daily_time_for_single_activity);
    dailyTotalCaloriesForSingleActivityTextViewHeader.setText(R.string.total_daily_calories_for_single_activity);

    laps_completed_textView = stopWatchPopUpView.findViewById(R.id.laps_completed_textView);

    lapListCanvas = stopWatchPopUpView.findViewById(R.id.lapCanvas);
    lapListCanvas.setScreenHeight(phoneHeight);

    lapRecycler = stopWatchPopUpView.findViewById(R.id.lap_recycler);
    stopWatchPauseResumeButton = stopWatchPopUpView.findViewById(R.id.stopwatchPauseResumeButton);
    stopWatchTimeTextView = stopWatchPopUpView.findViewById(R.id.stopWatchTimeTextView);
    stopWatchTimeTextView.setTextSize(120f);
    new_lap = stopWatchPopUpView.findViewById(R.id.new_lap);
    msTimeTextView = stopWatchPopUpView.findViewById(R.id.msTimeTextView);
    empty_laps = stopWatchPopUpView.findViewById(R.id.empty_laps_text);
    empty_laps.setText(R.string.empty_laps_list);
    stopwatchReset = stopWatchPopUpView.findViewById(R.id.stopwatch_reset);

    progressBar = timerPopUpView.findViewById(R.id.progressBar);
    progressBarLayout = timerPopUpView.findViewById(R.id.timer_progress_bar_layout);
    timeLeft = timerPopUpView.findViewById(R.id.timeLeft);
    reset_total_cycle_times = timerPopUpView.findViewById(R.id.reset_total_cycle_times);
    pauseResumeButton = timerPopUpView.findViewById(R.id.pauseResumeButton);
    next_round = timerPopUpView.findViewById(R.id.next_round);

  }

  private void assignSortPopUpLayoutClassesToTheirIds() {
    sortCycleTitleAToZ = sortCyclePopupView.findViewById(R.id.sort_title_start);
    sortCycleTitleZtoA = sortCyclePopupView.findViewById(R.id.sort_title_end);
    ;
    sortHigh = sortCyclePopupView.findViewById(R.id.sort_number_high);
    sortLow = sortCyclePopupView.findViewById(R.id.sort_number_low);
    sortActivityTitleAtoZ = sortCyclePopupView.findViewById(R.id.sort_activity_ascending_from_cycles);
    sortActivityTitleZToA = sortCyclePopupView.findViewById(R.id.sort_activity_descending_from_cycles);

    sortActivityStatsAToZTextView = sortActivitiesPopupView.findViewById(R.id.sort_activity_name_start);
    sortActivityStatsZToATextView = sortActivitiesPopupView.findViewById(R.id.sort_activity_name_end);
    sortActivityStatsByMostTimeTextView = sortActivitiesPopupView.findViewById(R.id.sort_most_time);
    sortActivityStatsByLeastTimeTextView = sortActivitiesPopupView.findViewById(R.id.sort_least_time);
    sortActivityStatsByMostCaloriesTextView = sortActivitiesPopupView.findViewById(R.id.sort_calories_expended_most);
    sortActivityStatsByLeastCaloriesTextView = sortActivitiesPopupView.findViewById(R.id.sort_calories_expended_least);

    sortFoodConsumedStatsAToZTextView = sortFoodConsumedPopupView.findViewById(R.id.sort_food_name_start);
    sortFoodConsumedStatsZToATextView = sortFoodConsumedPopupView.findViewById(R.id.sort_food_name_end);
    sortFoodConsumedCaloriesByMostTextView = sortFoodConsumedPopupView.findViewById(R.id.sort_calories_consumed_most);
    sortFoodConsumedCaloriesByLeastTextView = sortFoodConsumedPopupView.findViewById(R.id.sort_calories_consumed_least);
  }

  private void assignDeletePopUpLayoutClassesToTheirIds() {
    delete_all_confirm = deleteCyclePopupView.findViewById(R.id.confirm_yes);
    delete_all_cancel = deleteCyclePopupView.findViewById(R.id.confirm_no);
    delete_all_text = deleteCyclePopupView.findViewById(R.id.delete_text);
  }

  private void instantiateDatabaseClassesViaASyncThreadAndFollowWithUIPopulationOfTheirComponents() {
    AsyncTask.execute(() -> {
      cyclesDatabase = CyclesDatabase.getDatabase(getApplicationContext());
      cyclesList = cyclesDatabase.cyclesDao().loadAllCycles();
      pomCyclesList = cyclesDatabase.cyclesDao().loadAllPomCycles();

      runOnUiThread(() -> {
        instantiateCycleAdaptersAndTheirCallbacks();
        clearAndRepopulateCycleAdapterListsFromDatabaseList(true);
        replaceCycleListWithEmptyTextViewIfNoCyclesExist();

        setDefaultUserSettings();
        setDefaultEditRoundViews();
        getTimerVariablesForEachMode();
      });
    });
  }

  private void instantiateCycleAdaptersAndTheirCallbacks() {
    savedCycleAdapter = new SavedCycleAdapter(getApplicationContext(), workoutCyclesArray, typeOfRoundArray, workoutTitleArray, tdeeActivityExistsInCycleList, tdeeIsBeingTrackedInCycleList, workoutActivityStringArray);
    savedCycleRecycler.setAdapter(savedCycleAdapter);
    savedCycleRecycler.setLayoutManager(workoutCyclesRecyclerLayoutManager);
    savedCycleAdapter.setTdeeToggle(MainActivity.this);
    savedCycleAdapter.setItemClick(MainActivity.this);
    savedCycleAdapter.setHighlight(MainActivity.this);
    savedCycleAdapter.setResumeOrResetCycle(MainActivity.this);

    savedCycleAdapter.setScreenHeight(phoneHeight);
    savedCycleAdapter.setDayOrNightMode(colorThemeMode);

    savedPomCycleAdapter = new SavedPomCycleAdapter(getApplicationContext(), pomArray, pomTitleArray);
    savedPomCycleRecycler.setAdapter(savedPomCycleAdapter);
    savedPomCycleRecycler.setLayoutManager(pomCyclesRecyclerLayoutManager);
    savedPomCycleAdapter.setItemClick(MainActivity.this);
    savedPomCycleAdapter.setHighlight(MainActivity.this);
    savedPomCycleAdapter.setResumeOrResetCycle(MainActivity.this);

  }

  private void instantiateLayoutManagers() {
    workoutCyclesRecyclerLayoutManager = new LinearLayoutManager(getApplicationContext());
    roundRecyclerOneLayoutManager = new LinearLayoutManager(getApplicationContext());
    roundRecyclerTwoLayoutManager = new LinearLayoutManager(getApplicationContext());
    pomCyclesRecyclerLayoutManager = new LinearLayoutManager(getApplicationContext());

    lapRecyclerLayoutManager = new LinearLayoutManager(getApplicationContext());
    lapRecyclerLayoutManager.setStackFromEnd(true);
    lapRecyclerLayoutManager.setReverseLayout(true);
  }

  private void instantiateRoundAdaptersAndTheirCallbacks() {
    cycleRoundsAdapter = new CycleRoundsAdapter(getApplicationContext(), workoutStringListOfRoundValuesForFirstAdapter, workoutIntegerListOfRoundTypeForFirstAdapter, pomStringListOfRoundValues);
    cycleRoundsAdapter.fadeFinished(MainActivity.this);
    cycleRoundsAdapter.selectedRound(MainActivity.this);
    cycleRoundsAdapter.setMode(mode);

    cycleRoundsAdapterTwo = new CycleRoundsAdapterTwo(getApplicationContext(), workoutStringListOfRoundValuesForSecondAdapter, workoutIntegerListOfRoundTypeForSecondAdapter);
    cycleRoundsAdapterTwo.fadeFinished(MainActivity.this);
    cycleRoundsAdapterTwo.selectedRoundSecondAdapter(MainActivity.this);

    cycleRoundsAdapter.setScreenHeight(phoneHeight);
    cycleRoundsAdapterTwo.setScreenHeight(phoneHeight);
  }

  private void setRoundRecyclersOnAdaptersAndLayoutManagers() {
    roundRecycler.setLayoutManager(roundRecyclerOneLayoutManager);
    roundRecycler.setAdapter(cycleRoundsAdapter);
    roundRecyclerTwo.setAdapter(cycleRoundsAdapterTwo);
    roundRecyclerTwo.setLayoutManager(roundRecyclerTwoLayoutManager);
  }

  private void setVerticalSpaceDecorationForCycleRecyclerViewBasedOnScreenHeight() {
    if (phoneHeight <= 1920) {
      verticalSpaceItemDecoration = new VerticalSpaceItemDecoration(
              -25);
    } else {
      verticalSpaceItemDecoration = new VerticalSpaceItemDecoration(
              -10);
    }
    roundRecycler.addItemDecoration(verticalSpaceItemDecoration);
    roundRecyclerTwo.addItemDecoration(verticalSpaceItemDecoration);
  }

  private void instantiateLapAdapterAndSetRecyclerOnIt() {
    lapAdapter = new LapAdapter(getApplicationContext(), currentLapList, savedLapList);
    lapRecycler.setAdapter(lapAdapter);
    lapRecycler.setLayoutManager(lapRecyclerLayoutManager);
  }

  private void instantiateDotsRecyclerAndAdapter() {
    roundListForDots = new ArrayList<>();
    for (int i = 0; i < 16; i++) {
      roundListForDots.add(String.valueOf(i + 1));
    }

    ArrayList<String> convertedWorkoutRoundList = convertMillisIntegerListToTimerStringList(workoutTime);

    GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 8);
    gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);

    GridLayoutManager gridLayoutManagerTwo = new GridLayoutManager(getApplicationContext(), 8);
    gridLayoutManager.setOrientation(RecyclerView.VERTICAL);

    dotsAdapter = new DotsAdapter(getApplicationContext(), convertedWorkoutRoundList, typeOfRound);
    dotsAdapter.setScreenHeight(phoneHeight);

    dotsRecycler = timerPopUpView.findViewById(R.id.dots_recyclerView);
    dotsRecyclerLayout = timerPopUpView.findViewById(R.id.dots_recycler_layout);

    dotsRecyclerConstraintLayout_LayoutParams = (ConstraintLayout.LayoutParams) dotsRecyclerLayout.getLayoutParams();
    dotsRecyclerLayoutParams = (ConstraintLayout.LayoutParams) dotsRecycler.getLayoutParams();

    dotsRecycler.setAdapter(dotsAdapter);
    dotsRecycler.setLayoutManager(gridLayoutManager);
    dotsRecycler.addItemDecoration(setVerticalSpaceItemDecoration(18));

    //Disables "ghost scrolling"
    dotsRecycler.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        return true;
      }
    });

    pomDotsAdapter = new PomDotsAdapter(getApplicationContext(), pomStringListOfRoundValues);
    pomDotsAdapter.setScreenHeight(phoneHeight);

    pomDotsRecycler = timerPopUpView.findViewById(R.id.pom_dots_recyclerView);
    pomDotsRecycler.setAdapter(pomDotsAdapter);
    pomDotsRecycler.setLayoutManager(gridLayoutManagerTwo);

    pomDotsRecycler.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        return true;
      }
    });
  }

  private VerticalSpaceItemDecoration setVerticalSpaceItemDecoration(int space) {
    return new VerticalSpaceItemDecoration(space);
  }

  //Weight on layout that contains recyclerView is not the same as weight on view itself.
  private void adjustDotRecyclerViewSize(int numberOfRows) {
    if (phoneHeight <= 1920) {
      if (numberOfRows <= 8) {
        dotsRecyclerLayoutParams.height = dpConv(80);
      } else {
        dotsRecyclerLayoutParams.height = dpConv(140);
      }
    } else {
      if (numberOfRows <= 8) {
        dotsRecyclerLayoutParams.height = dpConv(95);
      } else {
        dotsRecyclerLayoutParams.height = dpConv(160);
      }
    }
  }

  private int dpConv(float pixels) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixels, getResources().getDisplayMetrics());
  }

  private void instantiateAnimationAndColorMethods() {
    fadeInForCustomActionBar = new AlphaAnimation(0.0f, 1.0f);
    fadeInForCustomActionBar.setDuration(750);
    fadeInForCustomActionBar.setFillAfter(true);

    fadeOutForCustomActionBar = new AlphaAnimation(1.0f, 0.0f);
    fadeOutForCustomActionBar.setDuration(750);
    fadeOutForCustomActionBar.setFillAfter(true);

    fadeInForEditCycleButton = new AlphaAnimation(0.0f, 1.0f);
    fadeInForEditCycleButton.setDuration(750);
    fadeInForEditCycleButton.setFillAfter(true);

    fadeOutForEditCycleButton = new AlphaAnimation(1.0f, 0.0f);
    fadeOutForEditCycleButton.setDuration(750);
    fadeOutForEditCycleButton.setFillAfter(true);

    slideLeft = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_in_left);
    slideLeft.setDuration(400);

    slideInFromLeftLong = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_from_left);
    slideInFromLeftLong.setDuration(600);

    slideOutFromLeftLong = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_from_left);
    slideOutFromLeftLong.setDuration(600);

    slideInFromLeftShort = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_from_left);
    slideInFromLeftShort.setDuration(300);

    slideOutFromLeftShort = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_from_left);
    slideOutFromLeftShort.setDuration(300);

    slideOutFromLeftShort.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {
        isAnimationActive = true;
      }

      @Override
      public void onAnimationEnd(Animation animation) {
        mainActivityFragmentFrameLayout.setVisibility(View.INVISIBLE);
        isAnimationActive = false;
      }

      @Override
      public void onAnimationRepeat(Animation animation) {
      }
    });

    fadeOutForEditCycleButton.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {
      }

      @Override
      public void onAnimationEnd(Animation animation) {
        edit_highlighted_cycle.clearAnimation();
      }

      @Override
      public void onAnimationRepeat(Animation animation) {
      }
    });

    fadeProgressIn = new AlphaAnimation(0.3f, 1.0f);
    fadeProgressOut = new AlphaAnimation(1.0f, 0.3f);
    fadeProgressIn.setDuration(300);
    fadeProgressOut.setDuration(300);

    valueAnimatorDown = new ValueAnimator().ofFloat(90f, 70f);
    valueAnimatorUp = new ValueAnimator().ofFloat(70f, 90f);
    valueAnimatorDown.setDuration(2000);
    valueAnimatorUp.setDuration(2000);
  }

  private void instantiatePopUpViewsAndWindows() {
    LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    savedCyclePopupView = inflater.inflate(R.layout.saved_cycles_layout, null);
    deleteCyclePopupView = inflater.inflate(R.layout.delete_cycles_popup, null);
    sortCyclePopupView = inflater.inflate(R.layout.sort_cycles_popup, null);
    sortActivitiesPopupView = inflater.inflate(R.layout.sort_activities_popup, null);
    sortFoodConsumedPopupView = inflater.inflate(R.layout.sort_food_popup, null);
    addTDEEPopUpView = inflater.inflate(R.layout.daily_stats_add_popup_for_main_activity, null);
    aboutSettingsPopUpView = inflater.inflate(R.layout.about_settings_popup_layout, null);

    savedCyclePopupWindow = new PopupWindow(savedCyclePopupView, convertDensityPixelsToScalable(250), convertDensityPixelsToScalable(450), true);
    deleteCyclePopupWindow = new PopupWindow(deleteCyclePopupView, convertDensityPixelsToScalable(300), convertDensityPixelsToScalable(150), true);
    sortPopupWindow = new PopupWindow(sortCyclePopupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
    settingsPopupWindow = new PopupWindow(settingsPopupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true);
    addTdeePopUpWindow = new PopupWindow(addTDEEPopUpView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
    aboutSettingsPopUpWindow = new PopupWindow(aboutSettingsPopUpView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);

    setEditCyclesLayoutForDifferentHeights();
    setTimerLayoutForDifferentHeights();
    setStopWatchLayoutForDifferentHeights();

    savedCyclePopupWindow.setAnimationStyle(R.style.WindowAnimation);
    deleteCyclePopupWindow.setAnimationStyle(R.style.WindowAnimation);
    sortPopupWindow.setAnimationStyle(R.style.SlideTopAnimationWithoutAnimatedExit);
    editCyclesPopupWindow.setAnimationStyle(R.style.SlideFromLeftAnimationShort);
    settingsPopupWindow.setAnimationStyle(R.style.WindowAnimation);
    timerPopUpWindow.setAnimationStyle(R.style.WindowAnimation);
    stopWatchPopUpWindow.setAnimationStyle(R.style.SlideFromLeftAnimationShort);
    addTdeePopUpWindow.setAnimationStyle(R.style.WindowAnimation);
    aboutSettingsPopUpWindow.setAnimationStyle(R.style.SlideFromLeftAnimationShort);
  }

  private void instantiateArrayLists() {
    workoutTime = new ArrayList<>();
    convertedWorkoutTime = new ArrayList<>();
    workoutTitle = new ArrayList<>();
    workoutCyclesArray = new ArrayList<>();
    typeOfRound = new ArrayList<>();
    typeOfRoundArray = new ArrayList<>();
    pomArray = new ArrayList<>();

    workoutStringListOfRoundValuesForFirstAdapter = new ArrayList<>();
    workoutStringListOfRoundValuesForSecondAdapter = new ArrayList<>();
    workoutIntegerListOfRoundTypeForFirstAdapter = new ArrayList<>();
    workoutIntegerListOfRoundTypeForSecondAdapter = new ArrayList<>();

    currentLapList = new ArrayList<>();
    savedLapList = new ArrayList<>();

    pomValuesTime = new ArrayList<>();
    pomStringListOfRoundValues = new ArrayList<>();
    pomArray = new ArrayList<>();
    workoutTitleArray = new ArrayList<>();
    tdeeIsBeingTrackedInCycleList = new ArrayList<>();
    tdeeActivityExistsInCycleList = new ArrayList<>();
    workoutActivityStringArray = new ArrayList<>();

    pomTitleArray = new ArrayList<>();
    receivedHighlightPositions = new ArrayList<>();
    cyclesList = new ArrayList<>();
    pomCyclesList = new ArrayList<>();

    editPopUpTimerArray = new ArrayList<>();
    editPopUpTimerArrayCapped = new ArrayList<>();
    savedEditPopUpArrayForFirstHeaderModeOne = new ArrayList<>();
    savedEditPopUpArrayForSecondHeaderModeOne = new ArrayList<>();
    savedEditPopUpArrayForFirstHeaderModeThree = new ArrayList<>();
    savedEditPopUpArrayForSecondHeaderModeThree = new ArrayList<>();
    savedEditPopUpArrayForThirdHeader = new ArrayList<>();
  }

  private void instantiateLayoutParameterObjects() {
    cycleTitleLayoutParams = (ConstraintLayout.LayoutParams) cycle_title_textView.getLayoutParams();
    cyclesCompletedLayoutParams = (ConstraintLayout.LayoutParams) cycles_completed_textView.getLayoutParams();
    totalSetTimeHeaderLayoutParams = (ConstraintLayout.LayoutParams) total_set_header.getLayoutParams();
    totalBreakTimeHeaderLayoutParams = (ConstraintLayout.LayoutParams) total_break_header.getLayoutParams();
    progressBarLayoutParams = (ConstraintLayout.LayoutParams) progressBarLayout.getLayoutParams();
    timerTextViewLayoutParams = (ConstraintLayout.LayoutParams) timeLeft.getLayoutParams();

    roundRecyclerParentLayoutParams = (ConstraintLayout.LayoutParams) roundRecyclerLayout.getLayoutParams();
    roundRecyclerOneLayoutParams = (ConstraintLayout.LayoutParams) roundRecycler.getLayoutParams();
    roundRecyclerTwoLayoutParams = (ConstraintLayout.LayoutParams) roundRecyclerTwo.getLayoutParams();

    firstRoundHeaderParams = (ConstraintLayout.LayoutParams) firstRoundTypeHeaderInEditPopUp.getLayoutParams();
    secondRoundHeaderParams = (ConstraintLayout.LayoutParams) secondRoundTypeHeaderInEditPopUp.getLayoutParams();
    thirdRoundHeaderParams = (ConstraintLayout.LayoutParams) thirdRoundTypeHeaderInEditPopUp.getLayoutParams();

    secondEditHeaderParams = (ConstraintLayout.LayoutParams) secondRoundTypeHeaderInEditPopUp.getLayoutParams();
    deleteEditTimerNumbersParams = (ConstraintLayout.LayoutParams) deleteEditPopUpTimerNumbers.getLayoutParams();
  }

  private void setCustomActionBarDefaultViews() {
    edit_highlighted_cycle.setVisibility(View.INVISIBLE);
    delete_highlighted_cycle.setVisibility(View.INVISIBLE);
    cancelHighlight.setVisibility(View.INVISIBLE);
  }

  private void setDefaultLayoutVisibilities() {
    edit_highlighted_cycle.setVisibility(View.INVISIBLE);
    cancelHighlight.setVisibility(View.INVISIBLE);
    delete_highlighted_cycle.setVisibility(View.INVISIBLE);
    reset.setVisibility(View.INVISIBLE);
    new_lap.setAlpha(0.3f);
    roundListDivider.setVisibility(View.GONE);

    savedPomCycleRecycler.setVisibility(View.GONE);
    pomDotsRecycler.setVisibility(View.GONE);
  }

  private void setDefaultLayoutTexts() {
    confirmActivityAdditionValues.setText(R.string.okay);
    timerValueInEditPopUpTextView.setText("00:00");
    setTrackingDailyStatsHeaderTextView();

    total_set_header.setText(R.string.total_sets);
    total_break_header.setText(R.string.total_breaks);
    total_set_time.setText("0");
    total_break_time.setText("0");
    cycles_completed_textView.setText(R.string.cycles_done);
  }

  private void setTrackingDailyStatsHeaderTextView() {
    tracking_daily_stats_header_textView.setText(getString(R.string.tracking_daily_stats, getCurrentDateAsSlashFormattedString()));
  }

  private void retrieveAndImplementCycleSorting() {
    sortMode = sharedPreferences.getInt("sortMode", 1);
    sortModePom = sharedPreferences.getInt("sortModePom", 1);
    int checkMarkPosition = sharedPreferences.getInt("checkMarkPosition", 0);

    sortHolder = sortMode;
    highlightSortTextView();
  }

  private void instantiateTdeeSpinnersAndSetThemOnAdapters() {
    tdeeCategoryAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.tdee_category_spinner_layout, tDEEChosenActivitySpinnerValues.getCategoryList());
    tdeeCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    tdee_category_spinner.setAdapter(tdeeCategoryAdapter);

    tdeeSubCategoryAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.tdee_sub_category_spinner_layout);
    tdeeSubCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    tdee_sub_category_spinner.setAdapter(tdeeSubCategoryAdapter);
  }

  private void instantiateSaveTotalTimesAndCaloriesInDatabaseRunnable() {
    globalSaveTotalTimesAndCaloriesInDatabaseRunnable = new Runnable() {
      @Override
      public void run() {
        if (mode == 1) {
          if (!trackActivityWithinCycle) {
            cycles.setTotalSetTime(totalCycleSetTimeInMillis);
            cycles.setTotalBreakTime(totalCycleBreakTimeInMillis);
            cycles.setCyclesCompleted(cyclesCompleted);
            cyclesDatabase.cyclesDao().updateCycles(cycles);
          }
        }
        if (mode == 3) {
          pomCycles.setTotalWorkTime(totalCycleWorkTimeInMillis);
          pomCycles.setTotalRestTime(totalCycleRestTimeInMillis);
          pomCycles.setCyclesCompleted(cyclesCompleted);
          cyclesDatabase.cyclesDao().updatePomCycles(pomCycles);
        }

        if (cycleHasActivityAssigned) {
          createNewListOfActivitiesIfDayHasChanged();
        }

        if (trackActivityWithinCycle) {
          if (!isDailyActivityTimeMaxed()) {
            setAndUpdateStatsForEachActivityValuesInDatabase();
          }
        }

        if (!timerIsPaused) {
          mHandler.postDelayed(globalSaveTotalTimesOnPostDelayRunnableInASyncThread, 2000);
        } else {
          mHandler.removeCallbacks(globalSaveTotalTimesOnPostDelayRunnableInASyncThread);
        }
      }
    };
  }

  private void instantiateSaveTotalTimesOnPostDelayRunnableInASyncThread() {
    globalSaveTotalTimesOnPostDelayRunnableInASyncThread = new Runnable() {
      @Override
      public void run() {
        AsyncTask.execute(globalSaveTotalTimesAndCaloriesInDatabaseRunnable);
      }
    };
  }

  private void createNewListOfActivitiesIfDayHasChanged() {
    Calendar calendar = Calendar.getInstance(Locale.getDefault());
    dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

    if ((dailyStatsAccess.getOldDayHolderId() != dayOfYear)) {
      dailyStatsAccess.setOldDayHolderId(dayOfYear);
      dailyStatsAccess.setStatForEachActivityListForForSingleDayFromDatabase(dayOfYear);

      if (!dailyStatsAccess.doesActivityExistsForSpecificDay()) {
        dailyStatsAccess.insertTotalTimesAndCaloriesForEachActivityWithinASpecificDayWithZeroedOutTimesAndCalories(dayOfYear);
      }

      zeroOutDailyActivityTimeAndCalories();
      dailyStatsAccess.setStatForEachActivityListForForSingleDayFromDatabase(dayOfYear);
      dailyStatsAccess.setStatsForEachActivityEntityFromPosition(0);

      tracking_daily_stats_header_textView.setText(getString(R.string.tracking_daily_stats, getCurrentDateAsSlashFormattedString()));
    }
  }

  private void changeDayOfYear(int day) {
    this.dayOfYear = day;
  }

  private void zeroOutDailyActivityTimeAndCalories() {
    totalSetTimeForCurrentDayInMillis = 0;
    totalCaloriesBurnedForCurrentDay = 0;
    totalSetTimeForSpecificActivityForCurrentDayInMillis = 0;
    totalCaloriesBurnedForSpecificActivityForCurrentDay = 0;

    mHandler.removeCallbacks(infinityRunnableForDailyActivityTimer);
    infinityRunnableForDailyActivityTimer = infinityRunnableForDailyActivityTime();
    mHandler.post(infinityRunnableForDailyActivityTimer);
  }

  private boolean isDailyActivityTimeMaxed() {
    long dividedDailyTotal = totalSetTimeForCurrentDayInMillis / 1000 / 60;
    long dividedDailyCap = dailyStatsAccess.getTwentyFourHoursInMillis() / 1000 / 60;

    return dividedDailyTotal >= dividedDailyCap;
  }

  private void setAndUpdateStatsForEachActivityValuesInDatabase() {
    int currentActivityPosition = dailyStatsAccess.getActivityPosition();
    int oldActivityPosition = dailyStatsAccess.getOldActivityPosition();

    if (currentActivityPosition != oldActivityPosition) {
      dailyStatsAccess.setOldActivityPositionInListForCurrentDay(currentActivityPosition);
    }

    Log.i("testTime", "activity time before update is " + totalSetTimeForSpecificActivityForCurrentDayInMillis);

    Log.i("testTime", "activity time updated in database is " + getDailyActivityTimeFromTextView());
    Log.i("testTime", "total time being updated is " + totalSetTimeForCurrentDayInMillis);

    dailyStatsAccess.updateTotalTimesAndCaloriesForEachActivityForSelectedDay(getDailyActivityTimeFromTextView(), totalCaloriesBurnedForSpecificActivityForCurrentDay);

    StatsForEachActivity statsForEachActivity = dailyStatsAccess.getStatsForEachActivityEntity();
  }

  private long getDailyActivityTimeFromTextView() {
    String textView = (String) dailyTotalTimeForSingleActivityTextView.getText().toString();
    return convertStringToSecondsForTimerPopUp(textView) * 1000;
  }

  private int convertStringToSecondsForTimerPopUp(String timerString) {
    int totalMinutes = 0;
    int totalSeconds = 0;

    if (timerString.length() == 4) {
      totalMinutes = Integer.parseInt(timerString.substring(0, 1));
      totalSeconds = Integer.parseInt(timerString.substring(2, 3) + timerString.substring(3, 4));
    }

    if (timerString.length() == 5) {
      totalMinutes = Integer.parseInt(timerString.substring(0, 1) + timerString.substring(1, 2));
      totalSeconds = Integer.parseInt(timerString.substring(3, 4) + timerString.substring(4, 5));
    }

    if (totalSeconds > 60) {
      totalSeconds = totalSeconds % 60;
      totalMinutes += 1;
    }
    int totalTime = (totalMinutes * 60) + totalSeconds;
    return totalTime;
  }

  private void fabLogic() {
    cycleNameEdit.getText().clear();
    isNewCycle = true;

    clearRoundAndCycleAdapterArrayLists();

    assignOldCycleValuesToCheckForChanges();
    resetEditPopUpTimerHeaders();

    editPopUpTimerArray.clear();
    timerValueInEditPopUpTextView.setText("00:00");

    setTdeeSpinnersToDefaultValues();
    toggleEditPopUpViewsForAddingActivity(false);

    //For some reason, shownAsDropDown vs showAtLocation prevents soft kb displacing layout.
    editCyclesPopupWindow.showAsDropDown(savedCyclesTabLayout);
  }

  private View.OnClickListener cyclesSortOptionListener() {
    return view -> {
      TextView textButton = (TextView) view;

      if (textButton.getText().toString().equals("Cycle Title: A - Z")) sortHolder = 1;
      if (textButton.getText().toString().equals("Cycle Title: Z - A")) sortHolder = 2;
      if (textButton.getText().toString().equals("Round Count: Most")) sortHolder = 3;
      if (textButton.getText().toString().equals("Round Count: Least")) sortHolder = 4;
      if (textButton.getText().toString().equals("Activity Title: A - Z")) sortHolder = 5;
      if (textButton.getText().toString().equals("Activity Title: Z - A")) sortHolder = 6;

      if (mode == 1) sortMode = sortHolder;
      else if (mode == 3) sortModePom = sortHolder;

      unHighlightSortTextViews();
      highlightSortTextView();

      AsyncTask.execute(() -> {
        queryAndSortAllCyclesFromDatabase(true);

        runOnUiThread(() -> {
          clearAndRepopulateCycleAdapterListsFromDatabaseList(false);
          if (mode == 1) savedCycleAdapter.notifyDataSetChanged();
          if (mode == 3) savedPomCycleAdapter.notifyDataSetChanged();
        });
      });

      prefEdit.putInt("sortMode", sortMode);
      prefEdit.putInt("sortModePom", sortModePom);
      prefEdit.apply();

      sortPopupWindow.dismiss();
    };
  }

  private void highlightSortTextView() {
    int colorToHighlight = getResources().getColor(R.color.test_highlight);
    switch (sortHolder) {
      case 1:
        sortCycleTitleAToZ.setBackgroundColor(colorToHighlight);
        break;
      case 2:
        sortCycleTitleZtoA.setBackgroundColor(colorToHighlight);
        break;
      case 3:
        sortHigh.setBackgroundColor(colorToHighlight);
        break;
      case 4:
        sortLow.setBackgroundColor(colorToHighlight);
        break;
      case 5:
        sortActivityTitleAtoZ.setBackgroundColor(colorToHighlight);
        break;
      case 6:
        sortActivityTitleZToA.setBackgroundColor(colorToHighlight);
        break;
    }
  }

  private void unHighlightSortTextViews() {
    int noHighlight = Color.TRANSPARENT;
    sortCycleTitleAToZ.setBackgroundColor(noHighlight);
    sortCycleTitleZtoA.setBackgroundColor(noHighlight);
    sortHigh.setBackgroundColor(noHighlight);
    sortLow.setBackgroundColor(noHighlight);
    sortActivityTitleAtoZ.setBackgroundColor(noHighlight);
    sortActivityTitleZToA.setBackgroundColor(noHighlight);
  }

  private void queryAndSortAllCyclesFromDatabase(boolean useSortMode) {
    if (mode == 1) {
      if (useSortMode) {
        switch (sortMode) {
          case 1:
            cyclesList = cyclesDatabase.cyclesDao().loadCyclesTitleAToZ();
            break;
          case 2:
            cyclesList = cyclesDatabase.cyclesDao().loadCyclesTitleZToA();
            break;
          case 3:
            cyclesList = cyclesDatabase.cyclesDao().loadCyclesMostItems();
            break;
          case 4:
            cyclesList = cyclesDatabase.cyclesDao().loadCyclesLeastItems();
            break;
          case 5:
            cyclesList = cyclesDatabase.cyclesDao().loadCyclesActivityAToZ();
            break;
          case 6:
            cyclesList = cyclesDatabase.cyclesDao().loadCyclesActivityZToA();
            break;
        }
      } else {
        cyclesList = cyclesDatabase.cyclesDao().loadAllCycles();
      }
    }

    if (mode == 3) {
      if (useSortMode) {
        switch (sortModePom) {
          case 1:
            pomCyclesList = cyclesDatabase.cyclesDao().loadPomCyclesMostRecent();
            break;
          case 2:
            pomCyclesList = cyclesDatabase.cyclesDao().loadPomCyclesLeastRecent();
            break;
          case 3:
            pomCyclesList = cyclesDatabase.cyclesDao().loadPomAlphaStart();
            break;
          case 4:
            pomCyclesList = cyclesDatabase.cyclesDao().loadPomAlphaEnd();
            break;
        }
      } else {
        pomCyclesList = cyclesDatabase.cyclesDao().loadAllPomCycles();
      }
    }
  }

  private View.OnClickListener activitySortOptionListener() {
    return view -> {
      TextView textView = (TextView) view;

      unHighlightAllSortTextViewsForActivityStats();

      if (textView.getText().toString().equals("Activity Name: A - Z")) {
        sortModeForActivities = 1;
        highlightSelectedSortTextViewForStats(sortActivityStatsAToZTextView);
      }
      if (textView.getText().toString().equals("Activity Name: Z - A")) {
        sortModeForActivities = 2;
        highlightSelectedSortTextViewForStats(sortActivityStatsZToATextView);
      }
      if (textView.getText().toString().equals("Time: Most")) {
        sortModeForActivities = 3;
        highlightSelectedSortTextViewForStats(sortActivityStatsByMostTimeTextView);
      }
      if (textView.getText().toString().equals("Time: Least")) {
        sortModeForActivities = 4;
        highlightSelectedSortTextViewForStats(sortActivityStatsByLeastTimeTextView);
      }
      if (textView.getText().toString().equals("Calories: Most")) {
        sortModeForActivities = 5;
        highlightSelectedSortTextViewForStats(sortActivityStatsByMostCaloriesTextView);
      }
      if (textView.getText().toString().equals("Calories: Least")) {
        sortModeForActivities = 6;
        highlightSelectedSortTextViewForStats(sortActivityStatsByLeastCaloriesTextView);
      }

      AsyncTask.execute(() -> {
        dailyStatsFragment.setActivitySortMode(sortModeForActivities);
        dailyStatsFragment.sortActivityStatsAsACallFromMainActivity();
        runOnUiThread(() -> {
          sortPopupWindow.dismiss();
        });
      });
    };
  }

  private View.OnClickListener foodConsumedSortOptionListener() {
    return view -> {
      TextView textView = (TextView) view;

      unHighlightAllSortTextViewsForFoodConsumedStats();

      if (textView.getText().toString().equals("Food Name: A - Z")) {
        sortModeForFoodConsumed = 1;
        highlightSelectedSortTextViewForStats(sortFoodConsumedStatsAToZTextView);
      }
      if (textView.getText().toString().equals("Food Name: Z - A")) {
        sortModeForFoodConsumed = 2;
        highlightSelectedSortTextViewForStats(sortFoodConsumedStatsZToATextView);
      }
      if (textView.getText().toString().equals("Calories: Most")) {
        sortModeForFoodConsumed = 3;
        highlightSelectedSortTextViewForStats(sortFoodConsumedCaloriesByMostTextView);
      }
      if (textView.getText().toString().equals("Calories: Least")) {
        sortModeForFoodConsumed = 4;
        highlightSelectedSortTextViewForStats(sortFoodConsumedCaloriesByLeastTextView);
      }

      AsyncTask.execute(() -> {
        dailyStatsFragment.setFoodConsumedSortMode(sortModeForFoodConsumed);
        dailyStatsFragment.sortFoodConsumedStatsAsACallFromMainActivity();
        runOnUiThread(() -> {
          sortPopupWindow.dismiss();
        });
      });
    };
  }

  private void highlightSelectedSortTextViewForStats(TextView textView) {
    int colorToHighlight = getResources().getColor(R.color.test_highlight);
    textView.setBackgroundColor(colorToHighlight);
  }

  private void unHighlightAllSortTextViewsForActivityStats() {
    int noHighlight = Color.TRANSPARENT;
    sortActivityStatsAToZTextView.setBackgroundColor(noHighlight);
    sortActivityStatsZToATextView.setBackgroundColor(noHighlight);
    sortActivityStatsByMostTimeTextView.setBackgroundColor(noHighlight);
    sortActivityStatsByLeastTimeTextView.setBackgroundColor(noHighlight);
    sortActivityStatsByMostCaloriesTextView.setBackgroundColor(noHighlight);
    sortActivityStatsByLeastCaloriesTextView.setBackgroundColor(noHighlight);
  }

  private void unHighlightAllSortTextViewsForFoodConsumedStats() {
    int noHighlight = Color.TRANSPARENT;
    sortFoodConsumedStatsAToZTextView.setBackgroundColor(noHighlight);
    sortFoodConsumedStatsZToATextView.setBackgroundColor(noHighlight);
    sortFoodConsumedCaloriesByMostTextView.setBackgroundColor(noHighlight);
    sortFoodConsumedStatsZToATextView.setBackgroundColor(noHighlight);
  }

  private void setAllSortTextViewsOntoClickListeners() {
    sortCycleTitleAToZ.setOnClickListener(cyclesSortOptionListener());
    sortCycleTitleZtoA.setOnClickListener(cyclesSortOptionListener());
    sortHigh.setOnClickListener(cyclesSortOptionListener());
    sortLow.setOnClickListener(cyclesSortOptionListener());
    sortActivityTitleAtoZ.setOnClickListener(cyclesSortOptionListener());
    sortActivityTitleZToA.setOnClickListener(cyclesSortOptionListener());

    sortActivityStatsAToZTextView.setOnClickListener(activitySortOptionListener());
    sortActivityStatsZToATextView.setOnClickListener(activitySortOptionListener());
    sortActivityStatsByMostTimeTextView.setOnClickListener(activitySortOptionListener());
    sortActivityStatsByLeastTimeTextView.setOnClickListener(activitySortOptionListener());
    sortActivityStatsByMostCaloriesTextView.setOnClickListener(activitySortOptionListener());
    sortActivityStatsByLeastCaloriesTextView.setOnClickListener(activitySortOptionListener());

    sortFoodConsumedStatsAToZTextView.setOnClickListener(foodConsumedSortOptionListener());
    sortFoodConsumedStatsZToATextView.setOnClickListener(foodConsumedSortOptionListener());
    sortFoodConsumedCaloriesByMostTextView.setOnClickListener(foodConsumedSortOptionListener());
    sortFoodConsumedCaloriesByLeastTextView.setOnClickListener(foodConsumedSortOptionListener());
  }

  private void editHighlightedCycleLogic() {
    if (receivedHighlightPositions.size() == 0) {
      return;
    }

    editCyclesPopupWindow.showAsDropDown(savedCyclesTabLayout);
    currentlyEditingACycle = true;
    isNewCycle = false;

    positionOfSelectedCycle = (receivedHighlightPositions.get(0));

    cycleHasActivityAssigned = tdeeActivityExistsInCycleList.get(positionOfSelectedCycle);

    toggleEditPopUpViewsForAddingActivity(cycleHasActivityAssigned);

    if (cycleHasActivityAssigned) {
      setCyclesOrPomCyclesEntityInstanceToSelectedListPosition(positionOfSelectedCycle);
      retrieveCycleActivityPositionAndMetScoreFromCycleList();
    }

    String tdeeString = workoutActivityStringArray.get(positionOfSelectedCycle);
    setTdeeSpinnersToDefaultValues();
    addTDEEfirstMainTextView.setText(tdeeString);

    if (mode == 1) cycleTitle = workoutTitleArray.get(positionOfSelectedCycle);
    if (mode == 3) cycleTitle = pomTitleArray.get(positionOfSelectedCycle);

    cycleNameEdit.setText(cycleTitle);
  }

  private void populateRoundAdapterArraysForHighlightedCycle() {
    switch (mode) {
      case 1:
        for (int i = 0; i < workoutTime.size(); i++) {
          convertedWorkoutTime.add(convertSeconds(workoutTime.get(i) / 1000));
          if (i <= 7) {
            workoutStringListOfRoundValuesForFirstAdapter.add(convertSeconds(workoutTime.get(i) / 1000));
            workoutIntegerListOfRoundTypeForFirstAdapter.add(typeOfRound.get(i));
          }
          if (i >= 8) {
            workoutStringListOfRoundValuesForSecondAdapter.add(convertSeconds(workoutTime.get(i) / 1000));
            workoutIntegerListOfRoundTypeForSecondAdapter.add(typeOfRound.get(i));
          }
        }
        roundSelectedPosition = workoutTime.size() - 1;
        break;
      case 3:
        cycleRoundsAdapter.disablePomFade();

        pomStringListOfRoundValues.clear();
        for (int i = 0; i < pomValuesTime.size(); i++) {
          pomStringListOfRoundValues.add(convertSeconds(pomValuesTime.get(i) / 1000));
        }
        break;
    }
  }

  private void deleteHighlightedCycles() {
    if ((mode == 1 && cyclesList.size() == 0 || (mode == 3 && pomCyclesList.size() == 0))) {
      runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Nothing saved!", Toast.LENGTH_SHORT).show());
      return;
    }

    int cycleID = 0;
    if (mode == 1) {
      for (int i = 0; i < receivedHighlightPositions.size(); i++) {
        cycleID = cyclesList.get(receivedHighlightPositions.get(i)).getId();
        cycles = cyclesDatabase.cyclesDao().loadSingleCycle(cycleID).get(0);
        cyclesDatabase.cyclesDao().deleteCycle(cycles);
      }
    }
    if (mode == 3) {
      for (int i = 0; i < receivedHighlightPositions.size(); i++) {
        cycleID = pomCyclesList.get(receivedHighlightPositions.get(i)).getId();
        pomCycles = cyclesDatabase.cyclesDao().loadSinglePomCycle(cycleID).get(0);
        cyclesDatabase.cyclesDao().deletePomCycle(pomCycles);
      }
    }

    receivedHighlightPositions.clear();

    queryAndSortAllCyclesFromDatabase(false);

    runOnUiThread(() -> {
      clearAndRepopulateCycleAdapterListsFromDatabaseList(false);
      if (mode == 1) savedCycleAdapter.notifyDataSetChanged();
      if (mode == 3) savedPomCycleAdapter.notifyDataSetChanged();
      ;
      replaceCycleListWithEmptyTextViewIfNoCyclesExist();
    });
  }

  private void deleteAllCycles() {
    if (mode == 1) {
      if (cyclesList.size() > 0) {
        cyclesDatabase.cyclesDao().deleteAllCycles();
        queryAndSortAllCyclesFromDatabase(false);

        runOnUiThread(() -> {
          clearAndRepopulateCycleAdapterListsFromDatabaseList(false);
          savedCycleAdapter.notifyDataSetChanged();
        });
      }
    }
    if (mode == 3) {
      if (pomCyclesList.size() > 0) {
        cyclesDatabase.cyclesDao().deleteAllPomCycles();
        queryAndSortAllCyclesFromDatabase(false);

        runOnUiThread(() -> {
          clearAndRepopulateCycleAdapterListsFromDatabaseList(false);
          savedPomCycleAdapter.notifyDataSetChanged();
        });
      }
    }
  }

  private void deleteTotalCycleTimes() {
    if (mode == 1) cyclesDatabase.cyclesDao().deleteTotalTimesCycle();
    if (mode == 3) cyclesDatabase.cyclesDao().deleteTotalTimesPom();

    runOnUiThread(() -> {
      deleteCyclePopupWindow.dismiss();
      if (mode == 1) {
        totalCycleSetTimeInMillis = 0;
        totalCycleBreakTimeInMillis = 0;
      }
      if (mode == 3) {
        totalCycleWorkTimeInMillis = 0;
        totalCycleRestTimeInMillis = 0;
      }

      cyclesCompleted = 0;

      total_set_time.setText("0");
      total_break_time.setText("0");
      setCyclesCompletedTextView();
    });
  }

  private void setDefaultUserSettings() {
    retrieveUserStats();

    SharedPreferences prefShared = PreferenceManager.getDefaultSharedPreferences(this);

    String defaultSoundSettingForSets = prefShared.getString("soundSettingForSets", "vibrate_once");
    String defaultSoundSettingForBreaks = prefShared.getString("soundSettingForBreaks", "vibrate_once");
    boolean defaultSoundSettingForLastRound = prefShared.getBoolean("soundSettingForLastRound", true);

    String defaultSoundSettingForWork = prefShared.getString("soundSettingForWork", "vibrate_once");
    String defaultSoundSettingForMiniBreak = prefShared.getString("soundSettingForMiniBreaks", "vibrate_once");
    boolean defaultSoundSettingForFullBreak = prefShared.getBoolean("soundSettingForFullBreak", true);

    String defaultColorSettingForSets = prefShared.getString("colorSettingForSets", "green_setting");
    String defaultColorSettingForBreaks = prefShared.getString("colorSettingForBreaks", "red_setting");

    String defaultColorSettingForWork = prefShared.getString("colorSettingForWork", "green_setting");
    String defaultColorSettingForMiniBreak = prefShared.getString("colorSettingForMiniBreaks", "red_setting");
    String defaultColorSettingForFullBreak = prefShared.getString("colorSettingForFullBreak", "magenta_setting");

    vibrationSettingForSets = changeSettingsValues.assignSoundSettingNumericValue(defaultSoundSettingForSets);
    vibrationSettingForBreaks = changeSettingsValues.assignSoundSettingNumericValue(defaultSoundSettingForBreaks);
    isLastRoundSoundContinuous = changeSettingsValues.assignFinalRoundSwitchValue(defaultSoundSettingForLastRound);

    vibrationSettingForWork = changeSettingsValues.assignSoundSettingNumericValue(defaultSoundSettingForWork);
    vibrationSettingForMiniBreaks = changeSettingsValues.assignSoundSettingNumericValue(defaultSoundSettingForMiniBreak);
    isFullBreakSoundContinuous = changeSettingsValues.assignFinalRoundSwitchValue(defaultSoundSettingForFullBreak);

    int setColorNumericValue = changeSettingsValues.assignColorSettingNumericValue(defaultColorSettingForSets);
    int breakColorNumericValue = changeSettingsValues.assignColorSettingNumericValue(defaultColorSettingForBreaks);
    int workColorNumericValue = changeSettingsValues.assignColorSettingNumericValue(defaultColorSettingForWork);
    int miniBreakColorNumericValue = changeSettingsValues.assignColorSettingNumericValue(defaultColorSettingForMiniBreak);
    int fullBreakColorNumericValue = changeSettingsValues.assignColorSettingNumericValue(defaultColorSettingForFullBreak);
    setColor = changeSettingsValues.assignColor(setColorNumericValue);
    breakColor = changeSettingsValues.assignColor(breakColorNumericValue);
    workColor = changeSettingsValues.assignColor(workColorNumericValue);
    miniBreakColor = changeSettingsValues.assignColor(miniBreakColorNumericValue);
    fullBreakColor = changeSettingsValues.assignColor(fullBreakColorNumericValue);

    dotsAdapter.changeColorSetting(1, setColorNumericValue);
    dotsAdapter.changeColorSetting(2, breakColorNumericValue);
    pomDotsAdapter.changeColorSetting(3, workColorNumericValue);
    pomDotsAdapter.changeColorSetting(4, miniBreakColorNumericValue);
    pomDotsAdapter.changeColorSetting(5, fullBreakColorNumericValue);

    cycleRoundsAdapter.changeColorSetting(1, setColorNumericValue);
    cycleRoundsAdapter.changeColorSetting(2, breakColorNumericValue);
    cycleRoundsAdapter.changeColorSetting(3, workColorNumericValue);
    cycleRoundsAdapter.changeColorSetting(4, miniBreakColorNumericValue);
    cycleRoundsAdapter.changeColorSetting(5, fullBreakColorNumericValue);
    cycleRoundsAdapterTwo.changeColorSetting(1, setColorNumericValue);
    cycleRoundsAdapterTwo.changeColorSetting(2, setColorNumericValue);

    savedCycleAdapter.changeColorSetting(1, setColorNumericValue);
    savedCycleAdapter.changeColorSetting(2, breakColorNumericValue);

    savedPomCycleAdapter.changeColorSetting(3, workColorNumericValue);
    savedPomCycleAdapter.changeColorSetting(4, miniBreakColorNumericValue);
    savedPomCycleAdapter.changeColorSetting(5, fullBreakColorNumericValue);
  }

  private void retrieveUserStats() {
    SharedPreferences sp = getApplicationContext().getSharedPreferences("pref", 0);
    metricMode = sp.getBoolean("metricMode", false);
    userGender = sp.getString("tdeeGender", "male");
    userAge = sp.getInt("tdeeAge,", 18);
    userWeight = sp.getInt("tdeeWeight,", 150);
    userHeight = sp.getInt("tdeeHeight,", 66);
  }

  public void setTypeOfOnOptionsSelectedMenu(int menuType) {
    mOnOptionsSelectedMenuType = menuType;
  }

  private boolean areAllDaysEmptyOfActivities(List<StatsForEachActivity> statsForEachActivityList) {
    List<String> listOfActivities = new ArrayList<>();

    for (int i = 0; i < statsForEachActivityList.size(); i++) {
      listOfActivities.add(statsForEachActivityList.get(i).getActivity());
    }

    return listOfActivities.size() == 0;
  }

  private boolean areAllDaysEmptyOfFoods(List<CaloriesForEachFood> caloriesForEachFoodList) {
    List<String> listOfFoods = new ArrayList<>();

    for (int i = 0; i < caloriesForEachFoodList.size(); i++) {
      listOfFoods.add(caloriesForEachFoodList.get(i).getTypeOfFood());
    }

    return listOfFoods.size() == 0;
  }

  private void setEndOfRoundSounds(int vibrationSetting, boolean repeat) {
    long[] vibrationEffect = changeSettingsValues.getVibrationSetting(vibrationSetting);

    Log.i("testVib", "array size is " + vibrationEffect.length);

    if (vibrationSetting == 1 || vibrationSetting == 2 || vibrationSetting == 3) {
      if (vibrator.hasVibrator()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          AudioAttributes audioAttributes = new AudioAttributes.Builder()
                  .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                  .setUsage(AudioAttributes.USAGE_ALARM)
                  .build();
          if (repeat) {
            vibrator.vibrate(vibrationEffect, 0, audioAttributes);
          } else {
            vibrator.vibrate(vibrationEffect, -1, audioAttributes);
          }
        } else {
          if (repeat) {
            vibrator.vibrate(changeSettingsValues.getVibrationSetting(vibrationSetting), 0);
          } else {
            vibrator.vibrate(changeSettingsValues.getVibrationSetting(vibrationSetting), -1);

          }
        }
      }
    } else if (vibrationSetting == 4) {
      if (!repeat) {
        mediaPlayer.setLooping(false);
      } else {
        mediaPlayer.setLooping(true);
      }
      mediaPlayer.start();
    }
  }

  private void assignColorSettingValues(int typeOfRound, int settingsNumber) {
    int color = changeSettingsValues.assignColor(settingsNumber);
    switch (typeOfRound) {
      case 1:
        setColor = color;
        break;
      case 2:
        breakColor = color;
        break;
      case 3:
        workColor = color;
        break;
      case 4:
        miniBreakColor = color;
        break;
      case 5:
        fullBreakColor = color;
        break;
    }
  }

  private void assignSoundSettingValues(int typeOfRound, int settingNumber) {
    switch (typeOfRound) {
      case 1:
        vibrationSettingForSets = settingNumber;
        break;
      case 2:
        vibrationSettingForBreaks = settingNumber;
        break;
      case 4:
        vibrationSettingForWork = settingNumber;
        break;
      case 5:
        vibrationSettingForMiniBreaks = settingNumber;
        break;
    }
  }

  private void editCyclesPopUpDismissalLogic() {
    if (currentlyEditingACycle) {
      fadeEditCycleButtonsInAndOut(FADE_OUT_EDIT_CYCLE);
      currentlyEditingACycle = false;

      removeCycleHighlights();
    }

    fab.setEnabled(true);
    cycleRoundsAdapter.notifyDataSetChanged();
    cycleRoundsAdapterTwo.notifyDataSetChanged();
    roundListDivider.setVisibility(View.GONE);
  }

  private void assignOldCycleValuesToCheckForChanges() {
    oldCycleTitleString = cycleTitle;
    if (mode == 1) {
      oldCycleRoundListOne = new ArrayList<>(workoutStringListOfRoundValuesForFirstAdapter);
      oldCycleRoundListTwo = new ArrayList<>(workoutStringListOfRoundValuesForSecondAdapter);
    }
    if (mode == 3) {
      oldPomRoundList = new ArrayList<>(pomArray);
    }
  }

  private void setEditPopUpTimerHeaders(int headerToSelect) {
    if (headerToSelect == 1) {
      secondRoundTypeHeaderInEditPopUp.setTextColor(Color.GRAY);
      thirdRoundTypeHeaderInEditPopUp.setTextColor(Color.GRAY);
      editHeaderSelected = 1;

      if (mode == 1) {
        firstRoundTypeHeaderInEditPopUp.setTextColor(setColor);

        Drawable newDraw = ContextCompat.getDrawable(getApplicationContext(), R.drawable.infinity_medium_green);
        newDraw.setColorFilter(setColor, PorterDuff.Mode.SRC_IN);
        toggleInfinityRounds.setImageDrawable(newDraw);

        toggleInfinityModeAndSetRoundType();

        editPopUpTimerArray = savedEditPopUpArrayForFirstHeaderModeOne;
      }
      if (mode == 3) {
        firstRoundTypeHeaderInEditPopUp.setTextColor(workColor);

        pomTimerValueInEditPopUpTextViewOne.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));
        pomTimerValueInEditPopUpTextViewTwo.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        pomTimerValueInEditPopUpTextViewThree.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
      }
    }

    if (headerToSelect == 2) {
      firstRoundTypeHeaderInEditPopUp.setTextColor(Color.GRAY);
      thirdRoundTypeHeaderInEditPopUp.setTextColor(Color.GRAY);
      editHeaderSelected = 2;

      if (mode == 1) {
        secondRoundTypeHeaderInEditPopUp.setTextColor(breakColor);

        Drawable newDraw = ContextCompat.getDrawable(getApplicationContext(), R.drawable.infinity_medium_green);
        newDraw.setColorFilter(breakColor, PorterDuff.Mode.SRC_IN);
        toggleInfinityRounds.setImageDrawable(newDraw);
        toggleInfinityModeAndSetRoundType();

        editPopUpTimerArray = savedEditPopUpArrayForSecondHeaderModeOne;
      }
      if (mode == 3) {
        secondRoundTypeHeaderInEditPopUp.setTextColor(miniBreakColor);

        pomTimerValueInEditPopUpTextViewTwo.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));
        pomTimerValueInEditPopUpTextViewOne.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        pomTimerValueInEditPopUpTextViewThree.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
      }
    }

    if (headerToSelect == 3) {
      firstRoundTypeHeaderInEditPopUp.setTextColor(Color.GRAY);
      secondRoundTypeHeaderInEditPopUp.setTextColor(Color.GRAY);
      thirdRoundTypeHeaderInEditPopUp.setTextColor(fullBreakColor);

      pomTimerValueInEditPopUpTextViewThree.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));
      pomTimerValueInEditPopUpTextViewOne.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
      pomTimerValueInEditPopUpTextViewTwo.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

      editHeaderSelected = 3;
    }

    if (mode == 1) {
      String savedTimerString = convertedTimerArrayToString(editPopUpTimerArray);
      timerValueInEditPopUpTextView.setText(savedTimerString);

      int totalTime = convertStringToSecondsForEditPopUp(savedTimerString);
      setAndCapTimerValues(totalTime);
    }

    changeEditTimerTextViewColorIfNotEmpty();
  }

  private void toggleInfinityModeAndSetRoundType() {
    if (editHeaderSelected == 1) {
      if (isSavedInfinityOptionActiveForSets) {
        toggleInfinityRounds.setAlpha(1.0f);
        roundType = 2;
      } else {
        toggleInfinityRounds.setAlpha(0.4f);
        roundType = 1;
      }
      setAndCapTimerValues(setValue);
    }
    if (editHeaderSelected == 2) {
      if (isSavedInfinityOptionActiveForBreaks) {
        toggleInfinityRounds.setAlpha(1.0f);
        roundType = 4;
      } else {
        toggleInfinityRounds.setAlpha(0.4f);
        roundType = 3;
      }
      setAndCapTimerValues(breakValue);
    }
  }

  private void resetEditPopUpTimerHeaders() {
    editHeaderSelected = 1;
    roundType = 1;
    toggleInfinityRounds.setAlpha(0.4f);
  }


  private void changeEditTimerTextViewColorIfNotEmpty() {
    if (editPopUpTimerArray.size() > 0) {
      timerValueInEditPopUpTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));
    } else {
      timerValueInEditPopUpTextView.setTextColor(Color.WHITE);
    }
  }

  private void saveEditHeaderTimerStringValues() {
    if (mode == 1) {
      if (editHeaderSelected == 1)
        savedEditPopUpArrayForFirstHeaderModeOne = new ArrayList<>(editPopUpTimerArray);
      if (editHeaderSelected == 2)
        savedEditPopUpArrayForSecondHeaderModeOne = new ArrayList<>(editPopUpTimerArray);
    }
  }

  private void addToEditPopUpTimerArrays(ArrayList<String> arrayList, TextView textView) {
    if (arrayList.size() <= 3) {
      for (int i = 0; i < 10; i++) {
        if (textView.getText().equals(String.valueOf(i))) {
          arrayList.add(String.valueOf(i));
        }
      }
    }
  }

  private void setEditPopUpTimerStringValues() {
    String editPopUpTimerString = "";
    if (mode == 1) {
      editPopUpTimerString = convertedTimerArrayToString(editPopUpTimerArray);
      saveEditHeaderTimerStringValues();
      timerValueInEditPopUpTextView.setText(editPopUpTimerString);

      int totalTime = convertStringToSecondsForEditPopUp(editPopUpTimerString);
      setAndCapTimerValues(totalTime);
    }

    if (mode == 3) {
      int totalTimeOne = 0;
      int totalTimeTwo = 0;
      int totalTimeThree = 0;
      String editPopUptimerTextViewStringOne = "";
      String editPopUptimerTextViewStringTwo = "";
      String editPopUpTimerStringThree = "";

      editPopUptimerTextViewStringOne = convertedTimerArrayToString(savedEditPopUpArrayForFirstHeaderModeThree);
      totalTimeOne = convertStringToSecondsForEditPopUp(editPopUptimerTextViewStringOne);
      pomTimerValueInEditPopUpTextViewOne.setText(editPopUptimerTextViewStringOne);

      editPopUptimerTextViewStringTwo = convertedTimerArrayToString(savedEditPopUpArrayForSecondHeaderModeThree);
      totalTimeTwo = convertStringToSecondsForEditPopUp(editPopUptimerTextViewStringTwo);
      pomTimerValueInEditPopUpTextViewTwo.setText(editPopUptimerTextViewStringTwo);

      editPopUpTimerStringThree = convertedTimerArrayToString(savedEditPopUpArrayForThirdHeader);
      totalTimeThree = convertStringToSecondsForEditPopUp(editPopUpTimerStringThree);
      pomTimerValueInEditPopUpTextViewThree.setText(editPopUpTimerStringThree);

      setAndCapPomValuesForEditTimer(totalTimeOne, 1);
      setAndCapPomValuesForEditTimer(totalTimeTwo, 2);
      setAndCapPomValuesForEditTimer(totalTimeThree, 3);
    }

    changeEditTimerTextViewColorIfNotEmpty();
  }

  private void convertEditPopUpTimerArrayToStringValues(int chosenMode) {
    if (chosenMode == 1) {

    }
  }

  private String convertedTimerArrayToString(ArrayList<String> arrayToConvert) {
    ArrayList<String> timeLeft = new ArrayList<>();
    timeLeft = populateEditTimerArray(arrayToConvert);

    String editPopUpTimerString;
    switch (arrayToConvert.size()) {
      default:
      case 0:
      case 1:
        editPopUpTimerString = timeLeft.get(4) + timeLeft.get(3) + timeLeft.get(2) + timeLeft.get(1) + timeLeft.get(0);
        break;
      case 2:
        editPopUpTimerString = timeLeft.get(4) + timeLeft.get(3) + timeLeft.get(2) + timeLeft.get(0) + timeLeft.get(1);
        break;
      case 3:
        editPopUpTimerString = timeLeft.get(4) + timeLeft.get(0) + timeLeft.get(2) + timeLeft.get(1) + timeLeft.get(3);
        break;
      case 4:
        editPopUpTimerString = timeLeft.get(0) + timeLeft.get(1) + timeLeft.get(2) + timeLeft.get(3) + timeLeft.get(4);
        break;
    }
    return editPopUpTimerString;
  }

  private ArrayList<String> populateEditTimerArray(ArrayList<String> arrayToPopulate) {
    ArrayList<String> timeLeft = new ArrayList<>();
    timeLeft.add("0");
    timeLeft.add("0");
    timeLeft.add(":");
    timeLeft.add("0");
    timeLeft.add("0");

    for (int i = 0; i < arrayToPopulate.size(); i++) {
      //Third index of timeLeft list is colon, so we never want to replace it.
      if (i < 2) {
        timeLeft.set(i, arrayToPopulate.get(i));
      } else {
        timeLeft.set(i + 1, arrayToPopulate.get(i));
      }
    }
    return timeLeft;
  }

  private int convertStringToSecondsForEditPopUp(String timerString) {
    int totalMinutes = Integer.parseInt(timerString.substring(0, 1) + timerString.substring(1, 2));
    int totalSeconds = Integer.parseInt(timerString.substring(3, 4) + timerString.substring(4, 5));
    if (totalSeconds > 60) {
      totalSeconds = totalSeconds % 60;
      totalMinutes += 1;
    }
    int totalTime = (totalMinutes * 60) + totalSeconds;
    return totalTime;
  }

  private ArrayList<String> convertIntegerToStringArray(int timerInteger) {
    String minutes = String.valueOf(timerInteger / 60);
    String seconds = String.valueOf(timerInteger % 60);

    String indexOne = "0";
    String indexTwo = "0";
    String indexThree = "0";
    String indexFour = "0";

    int indexPlaces = 0;

    if (minutes.length() <= 1) {
      indexTwo = minutes;
    } else {
      indexOne = minutes.substring(0, 1);
      indexTwo = minutes.substring(1, 2);
    }

    if (seconds.length() <= 1) {
      indexFour = seconds;
    } else {
      indexThree = seconds.substring(0, 1);
      indexFour = seconds.substring(1, 2);
    }

    editPopUpTimerArrayCapped.clear();
    editPopUpTimerArrayCapped.add(indexOne);
    editPopUpTimerArrayCapped.add(indexTwo);
    editPopUpTimerArrayCapped.add(indexThree);
    editPopUpTimerArrayCapped.add(indexFour);

    if (timerInteger > 0) indexPlaces++;
    if (timerInteger > 9) indexPlaces++;
    if (timerInteger > 59) indexPlaces++;
    if (timerInteger > 599) indexPlaces++;

    ArrayList<String> newList = new ArrayList<>();
    for (int i = 0; i < indexPlaces; i++) {
      newList.add(editPopUpTimerArrayCapped.get(i + (4 - indexPlaces)));
    }

    return newList;
  }

  private void setEditPopUpArrayWithCappedValues(ArrayList<String> arrayToSet, int numberOfIndices) {
    arrayToSet.clear();
    for (int i = 0; i < numberOfIndices; i++) {
      arrayToSet.add(editPopUpTimerArrayCapped.get(i + (4 - numberOfIndices)));
    }
  }

  private void toggleCycleAndPomCycleRecyclerViewVisibilities(boolean launchingPopUp) {
    toggleDayAndNightModesForMain(colorThemeMode);

    if (launchingPopUp) {
      if (mode == 1) {
        savedCycleRecycler.setVisibility(View.GONE);
      }
      if (mode == 3) {
        savedPomCycleRecycler.setVisibility(View.GONE);
      }
      emptyCycleList.setVisibility(View.GONE);
    } else {
      if (mode == 1) {
        savedCycleRecycler.setVisibility(View.VISIBLE);
      }
      if (mode == 3) {
        savedPomCycleRecycler.setVisibility(View.VISIBLE);
      }
    }
  }

  //These broadcast the Pending Intents we have created. MUST BE DECLARED IN MANIFEST.
  public static class DismissReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      dismissNotification = true;
    }
  }

  private PendingIntent dismissNotificationIntent(Context context, int notificationId) {
    Intent dismissIntent = new Intent(context, DismissReceiver.class);
    dismissIntent.putExtra("Dismiss ID", notificationId);

    PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), notificationId, dismissIntent, 0);

    return dismissPendingIntent;
  }

  private void instantiateNotifications() {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= 26) {
      CharSequence name = "Timers";
      String description = "Timer Countdown";
      int importance = NotificationManager.IMPORTANCE_HIGH;
      NotificationChannel channel = new NotificationChannel("1", name, importance);
      channel.setDescription(description);

      // Register the channel with the system; you can't change the importance or other notification behaviors after this.
      NotificationManager notificationManager = getSystemService(NotificationManager.class);
      notificationManager.createNotificationChannel(channel);

      notificationManagerBuilder = new NotificationCompat.Builder(this, "1");
    } else {
      notificationManagerBuilder = new NotificationCompat.Builder(this);
    }

    notificationManagerBuilder.setSmallIcon(R.drawable.start_cycle);
    notificationManagerBuilder.setAutoCancel(false);
    notificationManagerBuilder.setPriority(Notification.PRIORITY_HIGH);
    notificationManagerBuilder.setDeleteIntent(dismissNotificationIntent(this, 1));

    PackageManager pm = getApplicationContext().getPackageManager();
    Intent pmIntent = pm.getLaunchIntentForPackage(getApplicationContext().getPackageName());
    pmIntent.setPackage(null);

    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, pmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    notificationManagerBuilder.setContentIntent(pendingIntent);

    notificationManagerCompat = NotificationManagerCompat.from(this);
  }

  private void setNotificationValues() {
    if (!dismissNotification) {
      String headerOne = "";
      String headerTwo = "";
      String bodyOne = "";
      String bodyTwo = "";

      if (timerPopUpWindow.isShowing() || stopWatchPopUpWindow.isShowing()) {

        if (timerPopUpWindow.isShowing()) {
          if (mode == 1) {
            if (typeOfRound.get(currentRound) == 1 || typeOfRound.get(currentRound) == 2) {
              headerOne = setNotificationHeader("Workout", "Set", timerIsPaused);
              bodyOne = setNotificationBody(numberOfRoundsLeft, startRounds, setMillis);
            } else {
              headerOne = setNotificationHeader("Workout", "Break", timerIsPaused);
              bodyOne = setNotificationBody(numberOfRoundsLeft, startRounds, breakMillis);
            }
          }

          if (mode == 3) {
            int numberOfRoundsLeft = 8 - pomDotCounter;
            switch (pomDotCounter) {
              case 0:
              case 2:
              case 4:
              case 6:
                headerOne = setNotificationHeader("Pomodoro", "Work", timerIsPaused);
                bodyOne = setNotificationBody(numberOfRoundsLeft, 8, pomMillis);
                break;
              case 1:
              case 3:
              case 5:
              case 7:
                headerOne = setNotificationHeader("Pomodoro", "Break", timerIsPaused);
                bodyOne = setNotificationBody(numberOfRoundsLeft, 8, pomMillis);
                break;
            }
          }
        }

        if (stopWatchPopUpWindow.isShowing()) {
          headerOne = getString(R.string.notification_stopwatch_header);
          bodyOne = convertTimerValuesToStringForNotifications((long) stopWatchSeconds);
        }

        notificationManagerBuilder.setStyle(new NotificationCompat.InboxStyle()
                .addLine(headerOne)
                .addLine(bodyOne)
        );

        Notification notification = notificationManagerBuilder.build();

        notificationManagerCompat.notify(1, notification);
      }
    }
  }

  public String setNotificationHeader(String selectedMode, String roundType, boolean paused) {
    if (paused) {
      return (getString(R.string.notification_text_header, selectedMode, roundType) + " - " + getString(R.string.paused));
    } else {
      return (getString(R.string.notification_text_header, selectedMode, roundType));
    }
  }

  private String setNotificationBody(int roundsLeft, int startRounds, long timeLeft) {
    String currentTimerRound = "";

    if (roundsLeft != 0) {
      currentTimerRound = String.valueOf(startRounds - roundsLeft + 1);
    } else {
      currentTimerRound = String.valueOf(startRounds);
    }

    String totalRounds = String.valueOf(startRounds);

    String timeRemaining = "";
    if (timeLeft < 100) {
      timeRemaining = "00:00";
    } else {
      long roundedTimeLeft = roundToNearestFullThousandth(timeLeft);
      timeRemaining = convertTimerValuesToStringForNotifications(roundedTimeLeft / 1000);
    }

    return getString(R.string.notification_text, currentTimerRound, totalRounds, timeRemaining, getUpOrDownArrowForNotifications());
  }

  private String getUpOrDownArrowForNotifications() {
    String stringToReturn = "";

    if (mode == 1) {
      if (typeOfRound.get(currentRound) == 1 || typeOfRound.get(currentRound) == 3) {
        stringToReturn = getString(R.string.arrow_down);
      } else {
        stringToReturn = getString(R.string.arrow_up);
      }
    }
    ;

    return stringToReturn;
  }

  private void activateResumeOrResetOptionForCycle() {
    if (mode == 1) {
      if (activeCycle) {
        if (isNewCycle) positionOfSelectedCycle = workoutCyclesArray.size() - 1;
        savedCycleAdapter.setActiveCycleLayout();
        savedCycleAdapter.setActiveCyclePosition(positionOfSelectedCycle);
        savedCycleAdapter.setNumberOfRoundsCompleted(startRounds - numberOfRoundsLeft);

        savedCycleAdapter.notifyDataSetChanged();
      }
      //If between rounds, post runnable for next round without starting timer or object animator.
//      if (!objectAnimator.isStarted()) mHandler.post(postRoundRunnableForFirstMode());

      prefEdit.putInt("savedProgressBarValueForModeOne", currentProgressBarValue);
      prefEdit.putString("timeLeftValueForModeOne", timeLeft.getText().toString());
      prefEdit.putInt("positionOfSelectedCycleForModeOne", positionOfSelectedCycle);
      prefEdit.putBoolean("modeOneTimerPaused", timerIsPaused);
      prefEdit.putBoolean("modeOneTimerEnded", timerEnded);
      prefEdit.putBoolean("modeOneTimerDisabled", timerDisabled);
    }

    if (mode == 3) {
      if (activeCycle) {
        if (isNewCycle) positionOfSelectedCycle = pomArray.size() - 1;
        savedPomCycleAdapter.setActiveCycleLayout();
        savedPomCycleAdapter.setActiveCyclePosition(positionOfSelectedCycle);
        savedPomCycleAdapter.setNumberOfRoundsCompleted(pomDotCounter);

        savedPomCycleAdapter.notifyDataSetChanged();
      }
//      if (!objectAnimatorPom.isStarted()) mHandler.post(postRoundRunnableForThirdMode());

      prefEdit.putInt("savedProgressBarValueForModeThree", currentProgressBarValue);
      prefEdit.putString("timeLeftValueForModeThree", timeLeft.getText().toString());
      prefEdit.putInt("positionOfSelectedCycleForModeThree", positionOfSelectedCycle);
      prefEdit.putBoolean("modeThreeTimerPaused", timerIsPaused);
      prefEdit.putBoolean("modeThreeTimerEnded", timerEnded);
      prefEdit.putBoolean("modeThreeTimerDisabled", timerDisabled);
    }
    prefEdit.apply();
  }

  public int convertDensityPixelsToScalable(float pixels) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixels, getResources().getDisplayMetrics());
  }

  private void replaceCycleListWithEmptyTextViewIfNoCyclesExist() {
    if (mode == 1) {
      if (workoutCyclesArray.size() != 0) {
        emptyCycleList.setVisibility(View.GONE);
      }
      else {
        emptyCycleList.setVisibility(View.VISIBLE);
      }
    }
    if (mode == 3) {
      if (pomArray.size() != 0) {
        emptyCycleList.setVisibility(View.GONE);
      } else {
        emptyCycleList.setVisibility(View.VISIBLE);
      }
    }
  }

  private void removeCycleHighlights() {
    if (mode == 1) {
      savedCycleAdapter.removeHighlight();
      savedCycleAdapter.notifyDataSetChanged();
    }
    if (mode == 3) {
      savedPomCycleAdapter.removeHighlight();
      savedPomCycleAdapter.notifyDataSetChanged();
    }
  }

  private void fadeEditCycleButtonsInAndOut(int typeOfFade) {
    if (typeOfFade != FADE_IN_EDIT_CYCLE) {
      delete_highlighted_cycle.setEnabled(true);
      cancelHighlight.setEnabled(true);
    }
    if (typeOfFade == FADE_OUT_EDIT_CYCLE) {
      delete_highlighted_cycle.setEnabled(false);
      sortButton.setEnabled(true);
      edit_highlighted_cycle.setEnabled(false);
      cancelHighlight.setEnabled(false);
    }
    if (typeOfFade == FADE_IN_HIGHLIGHT_MODE) {
      appHeader.startAnimation(fadeOutForCustomActionBar);
      sortButton.startAnimation(fadeOutForCustomActionBar);
      delete_highlighted_cycle.startAnimation(fadeInForCustomActionBar);
      cancelHighlight.startAnimation(fadeInForCustomActionBar);

      edit_highlighted_cycle.startAnimation(fadeInForEditCycleButton);

      sortButton.setEnabled(false);
      edit_highlighted_cycle.setEnabled(true);
      delete_highlighted_cycle.setEnabled(true);
      cancelHighlight.setEnabled(true);
    }
    if (typeOfFade == FADE_OUT_HIGHLIGHT_MODE || typeOfFade == FADE_IN_EDIT_CYCLE) {
      appHeader.startAnimation(fadeInForCustomActionBar);
      sortButton.startAnimation(fadeInForCustomActionBar);
      delete_highlighted_cycle.startAnimation(fadeOutForCustomActionBar);
      cancelHighlight.startAnimation(fadeOutForCustomActionBar);

      edit_highlighted_cycle.startAnimation(fadeOutForEditCycleButton);

      sortButton.setEnabled(true);
      edit_highlighted_cycle.setEnabled(false);
      delete_highlighted_cycle.setEnabled(false);
      cancelHighlight.setEnabled(false);
    }
  }

  private void toggleCustomActionBarButtonVisibilities(boolean highlightMode) {
    if (highlightMode) {
      appHeader.setVisibility(View.GONE);
      sortButton.setVisibility(View.GONE);
      edit_highlighted_cycle.setVisibility(View.VISIBLE);
      delete_highlighted_cycle.setVisibility(View.VISIBLE);
      cancelHighlight.setVisibility(View.VISIBLE);
    } else {
      appHeader.setVisibility(View.VISIBLE);
      sortButton.setVisibility(View.VISIBLE);
      edit_highlighted_cycle.setVisibility(View.GONE);
      delete_highlighted_cycle.setVisibility(View.GONE);
      cancelHighlight.setVisibility(View.GONE);
    }
  }

  private void clearRoundAndCycleAdapterArrayLists() {
    if (mode == 1) {
      convertedWorkoutTime.clear();
      workoutStringListOfRoundValuesForFirstAdapter.clear();
      workoutStringListOfRoundValuesForSecondAdapter.clear();
      workoutIntegerListOfRoundTypeForFirstAdapter.clear();
      workoutIntegerListOfRoundTypeForSecondAdapter.clear();

      workoutTime.clear();
      typeOfRound.clear();
    }

    if (mode == 3) {
      pomValuesTime.clear();
      pomStringListOfRoundValues.clear();
    }

    cycleRoundsAdapter.notifyDataSetChanged();
    cycleRoundsAdapterTwo.notifyDataSetChanged();
  }

  private void setAndCapTimerValues(int value) {
    switch (mode) {
      case 1:
        if (editHeaderSelected == 1) setValue = timerValueBoundsFormula(5, 5400, value);
        if (editHeaderSelected == 2) breakValue = timerValueBoundsFormula(5, 5400, value);
        break;
      case 3:
        pomValue1 = timerValueBoundsFormula(600, 3600, value);
        pomValue2 = timerValueBoundsFormula(180, 600, value);
        pomValue3 = timerValueBoundsFormula(900, 3600, value);
        break;
    }
  }

  private void setAndCapPomValuesForEditTimer(int value, int variableToCap) {
    if (variableToCap == 1) pomValue1 = timerValueBoundsFormula(600, 3600, value);
    if (variableToCap == 2) pomValue2 = timerValueBoundsFormula(180, 600, value);
    if (variableToCap == 3) pomValue3 = timerValueBoundsFormula(900, 3600, value);
  }

  public int timerValueBoundsFormula(int min, int max, int value) {
    if (value < min) return min;
    else if (value > max) return max;
    else return value;
  }

  private String convertSeconds(long totalSeconds) {
    DecimalFormat df = new DecimalFormat("00");
    long minutes;
    long remainingSeconds;

    if (totalSeconds >= 60) {
      minutes = totalSeconds / 60;
      remainingSeconds = totalSeconds % 60;
      return (minutes + ":" + df.format(remainingSeconds));
    } else {
      return String.valueOf(totalSeconds);
    }
  }

  public String convertTimerValuesToStringForNotifications(long totalSeconds) {
    DecimalFormat df = new DecimalFormat("00");
    long minutes;
    long remainingSeconds;
    minutes = totalSeconds / 60;

    remainingSeconds = totalSeconds % 60;

    if (totalSeconds >= 60) {
      String formattedSeconds = df.format(remainingSeconds);
      if (formattedSeconds.length() > 2) formattedSeconds = "0" + formattedSeconds;
      if (totalSeconds >= 600) {
        return (minutes + ":" + formattedSeconds);
      } else {
        return ("0" + minutes + ":" + formattedSeconds);
      }
    } else {
      String totalStringSeconds = String.valueOf(totalSeconds);

      if (totalStringSeconds.length() < 2) totalStringSeconds = "0" + totalStringSeconds;
      return "00:" + totalStringSeconds;
    }
  }

  private void animateTimerEnding() {
    endAnimationForTimer = new AlphaAnimation(1.0f, 0.0f);
    endAnimationForTimer.setDuration(300);
    endAnimationForTimer.setStartOffset(0);
    endAnimationForTimer.setRepeatMode(Animation.REVERSE);
    endAnimationForTimer.setRepeatCount(Animation.INFINITE);
    progressBar.startAnimation(endAnimationForTimer);
    timeLeft.startAnimation(endAnimationForTimer);
  }

  private void animateStopwatchEnding() {
    endAnimationForStopwatch = new AlphaAnimation(1.0f, 0.0f);
    endAnimationForStopwatch.setDuration(300);
    endAnimationForStopwatch.setStartOffset(0);
    endAnimationForStopwatch.setRepeatMode(Animation.REVERSE);
    endAnimationForStopwatch.setRepeatCount(Animation.INFINITE);
    stopWatchPauseResumeButton.startAnimation(endAnimationForStopwatch);
    stopWatchTimeTextView.startAnimation(endAnimationForStopwatch);
  }

  private void adjustCustom(boolean adding) {
    inputMethodManager.hideSoftInputFromWindow(editCyclesPopupView.getWindowToken(), 0);
    if (adding) {
      if (mode == 1) {
        toggleInfinityModeAndSetRoundType();

        switch (roundType) {
          case 1:
            addOrReplaceRounds(setValue, roundIsSelected);
            editPopUpTimerArray = convertIntegerToStringArray(setValue);
            setEditPopUpTimerStringValues();
            break;
          case 2:
            addOrReplaceRounds(0, roundIsSelected);
            break;
          case 3:
            addOrReplaceRounds(breakValue, roundIsSelected);
            editPopUpTimerArray = convertIntegerToStringArray(breakValue);
            setEditPopUpTimerStringValues();
            break;
          case 4:
            addOrReplaceRounds(0, roundIsSelected);
            break;
          default:
            return;
        }
      }
      if (mode == 3) {
        savedEditPopUpArrayForFirstHeaderModeThree = convertIntegerToStringArray(pomValue1);
        savedEditPopUpArrayForSecondHeaderModeThree = convertIntegerToStringArray(pomValue2);
        savedEditPopUpArrayForThirdHeader = convertIntegerToStringArray(pomValue3);

        setEditPopUpTimerStringValues();

        if (pomValuesTime.size() == 0) {
          for (int i = 0; i < 3; i++) {
            pomValuesTime.add(pomValue1 * 1000);
            pomValuesTime.add(pomValue2 * 1000);
          }
          pomValuesTime.add(pomValue1 * 1000);
          pomValuesTime.add(pomValue3 * 1000);
          for (int j = 0; j < pomValuesTime.size(); j++) {
            pomStringListOfRoundValues.add(convertSeconds(pomValuesTime.get(j) / 1000));
          }

          cycleRoundsAdapter.setPomFade(true);
          cycleRoundsAdapter.notifyDataSetChanged();
        } else {
//          showToastIfNoneActive("Pomodoro cycle already loaded!");
        }
      }
    } else {
      if (mode == 1) {
        if (subtractedRoundIsFading) {
          removeRound();
        }
        if (workoutTime.size() > 0) {
          if (roundSelectedPosition <= 7) {
            cycleRoundsAdapter.setFadeOutPosition(roundSelectedPosition);
            cycleRoundsAdapter.notifyDataSetChanged();
          } else {
            cycleRoundsAdapterTwo.setFadeOutPosition(roundSelectedPosition - 8);
            cycleRoundsAdapterTwo.notifyDataSetChanged();
          }
          subtractedRoundIsFading = true;
        } else {
          showToastIfNoneActive("No rounds to clear!");
        }
      } else if (mode == 3) {
        if (pomValuesTime.size() != 0) {
          cycleRoundsAdapter.setPomFade(false);
          cycleRoundsAdapter.notifyDataSetChanged();
          subtractRoundFromCycleButton.setClickable(false);
        } else {
          showToastIfNoneActive("No Pomodoro cycle to clear!");
        }
      }
    }
  }

  private void addOrReplaceRounds(int integerValue, boolean replacingValue) {
    if (mode == 1) {
      if (!replacingValue) {
        if (workoutTime.size() < 16) {
          workoutTime.add(integerValue * 1000);
          convertedWorkoutTime.add(convertSeconds(integerValue));
          typeOfRound.add(roundType);
          roundSelectedPosition = workoutTime.size() - 1;

          if (workoutTime.size() <= 8) {
            workoutStringListOfRoundValuesForFirstAdapter.add(convertedWorkoutTime.get(convertedWorkoutTime.size() - 1));
            workoutIntegerListOfRoundTypeForFirstAdapter.add(typeOfRound.get(typeOfRound.size() - 1));
            cycleRoundsAdapter.setFadeInPosition(roundSelectedPosition);
            cycleRoundsAdapter.notifyDataSetChanged();
          } else {
            workoutStringListOfRoundValuesForSecondAdapter.add(convertedWorkoutTime.get(convertedWorkoutTime.size() - 1));
            workoutIntegerListOfRoundTypeForSecondAdapter.add(typeOfRound.get(typeOfRound.size() - 1));
            cycleRoundsAdapterTwo.setFadeInPosition(roundSelectedPosition - 8);
            cycleRoundsAdapterTwo.notifyDataSetChanged();
          }
        } else {
          showToastIfNoneActive("Full!");
        }
        setRoundRecyclerViewsWhenChangingAdapterCount(workoutTime);
      } else {
        workoutTime.set(roundSelectedPosition, integerValue * 1000);
        convertedWorkoutTime.set(roundSelectedPosition, convertSeconds(integerValue));
        typeOfRound.set(roundSelectedPosition, roundType);
        if (roundSelectedPosition <= 7) {
          workoutStringListOfRoundValuesForFirstAdapter.set(roundSelectedPosition, convertedWorkoutTime.get(roundSelectedPosition));
          workoutIntegerListOfRoundTypeForFirstAdapter.set(roundSelectedPosition, typeOfRound.get(roundSelectedPosition));

          cycleRoundsAdapter.isRoundCurrentlySelected(false);
          cycleRoundsAdapter.setFadeInPosition(roundSelectedPosition);
          cycleRoundsAdapter.notifyDataSetChanged();
        } else {
          workoutStringListOfRoundValuesForSecondAdapter.set(roundSelectedPosition - 8, convertedWorkoutTime.get(roundSelectedPosition));
          workoutIntegerListOfRoundTypeForSecondAdapter.set(roundSelectedPosition - 8, typeOfRound.get(roundSelectedPosition));

          cycleRoundsAdapterTwo.isRoundCurrentlySelected(false);
          cycleRoundsAdapterTwo.setFadeInPosition(roundSelectedPosition - 8);
          cycleRoundsAdapterTwo.notifyDataSetChanged();
        }
        roundIsSelected = false;
      }
    }
  }

  private void removeRound() {
    if (mode == 1) {
      if (workoutTime.size() > 0) {
        if (workoutTime.size() - 1 >= roundSelectedPosition) {
          if (workoutTime.size() <= 8 || (roundIsSelected && roundSelectedPosition <= 7)) {
            if (workoutStringListOfRoundValuesForFirstAdapter.size() - 1 >= roundSelectedPosition) {
              workoutStringListOfRoundValuesForFirstAdapter.remove(roundSelectedPosition);
              workoutIntegerListOfRoundTypeForFirstAdapter.remove(roundSelectedPosition);
              cycleRoundsAdapter.setFadeOutPosition(-1);
              cycleRoundsAdapter.notifyDataSetChanged();
            }
          } else {
            if (workoutStringListOfRoundValuesForSecondAdapter.size() - 1 >= roundSelectedPosition - 8) {
              workoutStringListOfRoundValuesForSecondAdapter.remove(roundSelectedPosition - 8);
              workoutIntegerListOfRoundTypeForSecondAdapter.remove(roundSelectedPosition - 8);
              cycleRoundsAdapterTwo.setFadeInPosition(-1);
              cycleRoundsAdapterTwo.notifyDataSetChanged();
            }
          }

          typeOfRound.remove(roundSelectedPosition);
          workoutTime.remove(roundSelectedPosition);
          convertedWorkoutTime.remove(roundSelectedPosition);

          setRoundRecyclerViewsWhenChangingAdapterCount(workoutTime);

          subtractedRoundIsFading = false;
        }
        if (roundIsSelected) {
          if (roundSelectedPosition <= 7) {
            cycleRoundsAdapter.isRoundCurrentlySelected(false);
            cycleRoundsAdapter.notifyDataSetChanged();
          } else {
            cycleRoundsAdapterTwo.isRoundCurrentlySelected(false);
            cycleRoundsAdapterTwo.notifyDataSetChanged();
          }
          consolidateRoundAdapterLists = true;
          roundIsSelected = false;
        }
        roundSelectedPosition = workoutTime.size() - 1;
      } else {
        showToastIfNoneActive("Empty!");
      }
    }
  }

  private void setRoundRecyclerViewsWhenChangingAdapterCount(ArrayList<Integer> adapterList) {
    if (mode == 1) {
      if (adapterList.size() <= 8) {
        roundRecyclerTwo.setVisibility(View.GONE);
        roundListDivider.setVisibility(View.GONE);

        if (phoneHeight <= 1920) {
          roundRecyclerParentLayoutParams.width = convertDensityPixelsToScalable(180);
          roundRecyclerOneLayoutParams.leftMargin = convertDensityPixelsToScalable(15);
        } else {
          roundRecyclerParentLayoutParams.width = convertDensityPixelsToScalable(200);
          roundRecyclerOneLayoutParams.leftMargin = convertDensityPixelsToScalable(20);
        }
        roundRecyclerOneLayoutParams.rightMargin = 0;
      } else {
        roundRecyclerTwo.setVisibility(View.VISIBLE);
        roundListDivider.setVisibility(View.VISIBLE);

        if (phoneHeight <= 1920) {
          roundRecyclerParentLayoutParams.width = convertDensityPixelsToScalable(260);
          roundRecyclerOneLayoutParams.rightMargin = convertDensityPixelsToScalable(20);
          roundRecyclerOneLayoutParams.rightMargin = convertDensityPixelsToScalable(140);

        } else {
          roundRecyclerParentLayoutParams.width = convertDensityPixelsToScalable(260);
          roundRecyclerOneLayoutParams.rightMargin = convertDensityPixelsToScalable(150);
          roundRecyclerTwoLayoutParams.rightMargin = convertDensityPixelsToScalable(10);
        }
      }
    }
  }

  public String friendlyString(String altString) {
    altString = altString.replace("\"", "");
    altString = altString.replace("]", "");
    altString = altString.replace("[", "");
    altString = altString.replace(",", " - ");
    return altString;
  }

  private void clearAndRepopulateCycleAdapterListsFromDatabaseList(boolean forAllModes) {
    if (mode == 1 || forAllModes) {
      workoutCyclesArray.clear();
      typeOfRoundArray.clear();
      workoutTitleArray.clear();
      workoutActivityStringArray.clear();
      tdeeActivityExistsInCycleList.clear();
      tdeeIsBeingTrackedInCycleList.clear();

      for (int i = 0; i < cyclesList.size(); i++) {
        workoutTitleArray.add(cyclesList.get(i).getTitle());

        workoutCyclesArray.add(cyclesList.get(i).getWorkoutRounds());
        typeOfRoundArray.add(cyclesList.get(i).getRoundType());
        workoutActivityStringArray.add(cyclesList.get(i).getActivityString());
        tdeeActivityExistsInCycleList.add(cyclesList.get(i).getTdeeActivityExists());

        tdeeIsBeingTrackedInCycleList.add(cyclesList.get(i).getCurrentlyTrackingCycle());

      }
    }
    if (mode == 3 || forAllModes) {
      pomArray.clear();
      pomTitleArray.clear();

      for (int i = 0; i < pomCyclesList.size(); i++) {
        pomArray.add(pomCyclesList.get(i).getFullCycle());
        pomTitleArray.add(pomCyclesList.get(i).getTitle());
      }
    }
  }

  private void populateCycleRoundAndRoundTypeArrayLists() {
    switch (mode) {
      case 1:
        workoutTime.clear();
        typeOfRound.clear();
        if (workoutCyclesArray.size() - 1 >= positionOfSelectedCycle) {
          String[] fetchedRounds = workoutCyclesArray.get(positionOfSelectedCycle).split(" - ");
          String[] fetchedRoundType = typeOfRoundArray.get(positionOfSelectedCycle).split(" - ");

          for (int i = 0; i < fetchedRounds.length; i++) {
            workoutTime.add(Integer.parseInt(fetchedRounds[i]));
          }
          for (int j = 0; j < fetchedRoundType.length; j++) {
            typeOfRound.add(Integer.parseInt(fetchedRoundType[j]));
          }

          ArrayList<String> convertedWorkoutRoundList = convertMillisIntegerListToTimerStringList(workoutTime);

          adjustDotRecyclerViewSize(convertedWorkoutRoundList.size());
          dotsAdapter.setCycleRoundsAsStringsList(convertedWorkoutRoundList);
          dotsAdapter.setTypeOfRoundList(typeOfRound);
          dotsAdapter.notifyDataSetChanged();

          cycleTitle = workoutTitleArray.get(positionOfSelectedCycle);
        }

        break;
      case 3:
        pomValuesTime.clear();
        pomStringListOfRoundValues.clear();

        if (pomArray.size() - 1 >= positionOfSelectedCycle) {
          String[] fetchedPomCycle = pomArray.get(positionOfSelectedCycle).split(" - ");

          /////---------Testing pom round iterations---------------/////////
//          for (int i=0; i<8; i++) if (i%2!=0) pomValuesTime.add(5000); else pomValuesTime.add(7000);
          for (int i = 0; i < fetchedPomCycle.length; i++) {
            int integerValue = Integer.parseInt(fetchedPomCycle[i]);
            pomValuesTime.add(integerValue);
            pomStringListOfRoundValues.add(convertSeconds(integerValue / 1000));
          }

          pomDotsAdapter.setPomCycleRoundsAsStringsList(pomStringListOfRoundValues);
          pomDotsAdapter.updatePomDotCounter(pomDotCounter);
          pomDotsAdapter.notifyDataSetChanged();

          cycleTitle = pomTitleArray.get(positionOfSelectedCycle);
        }
        break;
    }
  }

  private void launchPomTimerCycle(int typeOfLaunch) {
    if (pomValuesTime.size() == 0) {
      showToastIfNoneActive("Cycle cannot be empty!");
      return;
    }

    if (savedPomCycleAdapter.isCycleActive()) {
      savedPomCycleAdapter.removeActiveCycleLayout();
      savedPomCycleAdapter.notifyDataSetChanged();
    }

    if (typeOfLaunch == CYCLES_LAUNCHED_FROM_EDIT_POPUP) {
      if (cycleNameEdit.getText().toString().isEmpty()) {
        cycleTitle = getCurrentDateAsFullTextString();
      } else {
        cycleTitle = cycleNameEdit.getText().toString();
      }
    }

    AsyncTask.execute(() -> {
      saveAddedOrEditedCycleASyncRunnable();
      queryAndSortAllCyclesFromDatabase(false);
      clearAndRepopulateCycleAdapterListsFromDatabaseList(false);

      if (!isNewCycle) {
        retrieveTotalSetAndBreakAndCompletedCycleValuesFromCycleList();
      } else {
        positionOfSelectedCycle = pomArray.size() - 1;
      }

      setCyclesOrPomCyclesEntityInstanceToSelectedListPosition(positionOfSelectedCycle);
      retrieveTotalSetAndBreakAndCompletedCycleValuesFromCycleList();
//      roundDownPomCycleWorkAndRestTimes();

      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          setTimerLaunchViews(typeOfLaunch);
          setTimerLaunchLogic(false);
        }
      });
    });
  }

  private void launchTimerCycle(int typeOfLaunch) {
    if (workoutTime.size() == 0) {
      showToastIfNoneActive("Cycle cannot be empty!");
      return;
    }

    Calendar calendar = Calendar.getInstance(Locale.getDefault());
    dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

    if (savedCycleAdapter.isCycleActive()) {
      savedCycleAdapter.removeActiveCycleLayout();
      savedCycleAdapter.notifyDataSetChanged();
    }

    AsyncTask.execute(() -> {
      if (typeOfLaunch == CYCLES_LAUNCHED_FROM_EDIT_POPUP) {
        if (cycleNameEdit.getText().toString().isEmpty()) {
          cycleTitle = getCurrentDateAsFullTextString();
        } else {
          cycleTitle = cycleNameEdit.getText().toString();
        }

        if (addTDEEfirstMainTextView.getText().equals(getString(R.string.add_activity))) {
          cycleHasActivityAssigned = false;
          trackActivityWithinCycle = false;
        } else {
          cycleHasActivityAssigned = true;
          trackActivityWithinCycle = true;
        }
      }

      if (typeOfLaunch == CYCLE_LAUNCHED_FROM_RECYCLER_VIEW) {
        cycleHasActivityAssigned = savedCycleAdapter.getBooleanDeterminingIfCycleHasActivity(positionOfSelectedCycle);
        trackActivityWithinCycle = savedCycleAdapter.getBooleanDeterminingIfWeAreTrackingActivity(positionOfSelectedCycle);

        if (mode == 1) savedCycleRecycler.startAnimation(slideOutFromLeftLong);
        if (mode == 3) savedPomCycleRecycler.startAnimation(slideOutFromLeftLong);
      }

      if (cycleHasActivityAssigned) {
        insertActivityIntoDatabaseAndAssignItsValueToObjects();
      }

      saveAddedOrEditedCycleASyncRunnable();
      queryAndSortAllCyclesFromDatabase(false);
      clearAndRepopulateCycleAdapterListsFromDatabaseList(false);

      if (isNewCycle) {
        positionOfSelectedCycle = workoutCyclesArray.size() - 1;
      }

      if (!trackActivityWithinCycle) {
        setCyclesOrPomCyclesEntityInstanceToSelectedListPosition(positionOfSelectedCycle);
        retrieveTotalSetAndBreakAndCompletedCycleValuesFromCycleList();
      }

      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          setTimerLaunchLogic(trackActivityWithinCycle);
          setTimerLaunchViews(typeOfLaunch);
        }
      });
    });
  }

  private void setTimerLaunchLogic(boolean trackingActivity) {
    toggleViewsForTotalDailyAndCycleTimes(trackingActivity);

    retrieveTotalDailySetAndBreakTimes();
    retrieveTotalTimesAndCaloriesForSpecificActivityOnCurrentDayVariables();

    clearRoundAndCycleAdapterArrayLists();
    populateCycleRoundAndRoundTypeArrayLists();

    resetTimer();

    if (trackingActivity) {
      setAllActivityTimesAndCaloriesToTextViews();
    } else {
      setTotalCycleTimeValuesToTextView();
    }

    Log.i("testTime", "activity total in timer launch logic is " + totalSetTimeForSpecificActivityForCurrentDayInMillis);
    Log.i("testTime", "daily total in timer launch logic is " + totalSetTimeForCurrentDayInMillis);
  }

  private void setTimerLaunchViews(int typeOfLaunch) {
    timerPopUpIsVisible = true;
    cycle_title_textView.setText(cycleTitle);

    if (mode == 1) {
      changeTextSizeWithoutAnimator(workoutTime.get(0));
    }
    if (mode == 3) {
      changeTextSizeWithoutAnimator(pomValuesTime.get(0));
    }

    if (editCyclesPopupWindow.isShowing()) {
      editCyclesPopupWindow.dismiss();
    }

    toggleCycleAndPomCycleRecyclerViewVisibilities(true);

    timerPopUpWindow.showAtLocation(mainView, Gravity.NO_GRAVITY, 0, 0);
  }

  private void insertActivityIntoDatabaseAndAssignItsValueToObjects() {
    dailyStatsAccess.setOldDayHolderId(dayOfYear);

    dailyStatsAccess.setActivityString(getTdeeActivityStringFromArrayPosition());
    dailyStatsAccess.setMetScoreFromSpinner(metScore);

    dailyStatsAccess.setStatForEachActivityListForForSingleDayFromDatabase(dayOfYear);

    if (dailyStatsAccess.doesActivityExistsForSpecificDay()) {
      dailyStatsAccess.setActivityPositionInListForCurrentDayForExistingActivity();
      dailyStatsAccess.assignPositionOfActivityListForRetrieveActivityToStatsEntity();
    } else {
      dailyStatsAccess.insertTotalTimesAndCaloriesForEachActivityWithinASpecificDayWithZeroedOutTimesAndCalories(dayOfYear);
      dailyStatsAccess.loadAllActivitiesToStatsListForSpecificDay(dayOfYear);
      dailyStatsAccess.assignPositionOfRecentlyAddedRowToStatsEntity();
      dailyStatsAccess.setActivityPositionInListForCurrentDayForNewActivity();
    }

    retrieveTotalDailySetAndBreakTimes();
    retrieveTotalTimesAndCaloriesForSpecificActivityOnCurrentDayVariables();
  }

  private void retrieveTotalDailySetAndBreakTimes() {
    if (mode == 1) {
      totalSetTimeForCurrentDayInMillis = dailyStatsAccess.getTotalActivityTimeForAllActivitiesOnASelectedDay(dayOfYear);
      totalCaloriesBurnedForCurrentDay = dailyStatsAccess.getTotalCaloriesBurnedForAllActivitiesOnASingleDay(dayOfYear);
    }

    Log.i("testTime", "daily total retrieved from db list during launch is " + totalSetTimeForCurrentDayInMillis);
  }

  private void retrieveTotalTimesAndCaloriesForSpecificActivityOnCurrentDayVariables() {
    totalSetTimeForSpecificActivityForCurrentDayInMillis = dailyStatsAccess.getTotalSetTimeForSelectedActivity();
    totalCaloriesBurnedForSpecificActivityForCurrentDay = dailyStatsAccess.getTotalCaloriesBurnedForSelectedActivity();

    Log.i("testTime", "activity total retrieved from db list during launch is " + totalSetTimeForSpecificActivityForCurrentDayInMillis);
  }

  private void retrieveTotalSetAndBreakAndCompletedCycleValuesFromCycleList() {
    if (mode == 1) {
      totalCycleSetTimeInMillis = cycles.getTotalSetTime();
      totalCycleBreakTimeInMillis = cycles.getTotalBreakTime();

      cyclesCompleted = cycles.getCyclesCompleted();
    }

    if (mode == 3) {
      totalCycleWorkTimeInMillis = pomCycles.getTotalWorkTime();
      totalCycleRestTimeInMillis = pomCycles.getTotalRestTime();

      cyclesCompleted = pomCycles.getCyclesCompleted();
    }
  }

  private void setCyclesOrPomCyclesEntityInstanceToSelectedListPosition(int position) {
    if (mode == 1) cycles = cyclesList.get(position);
    if (mode == 3) pomCycles = pomCyclesList.get(position);
  }

  //Getting toggle stat from adapter on timer launch and saving that. Retrieve w/ everything else coming back.
  private void saveAddedOrEditedCycleASyncRunnable() {
    Gson gson = new Gson();
    String workoutString = "";
    String roundTypeString = "";
    String pomString = "";

    int cycleID;
    if (mode == 1) {
      if (isNewCycle) {
        cycles = new Cycles();
      } else if (cyclesList.size() > 0) {
        cycleID = cyclesList.get(positionOfSelectedCycle).getId();
        cycles = cyclesDatabase.cyclesDao().loadSingleCycle(cycleID).get(0);
      }
      workoutString = gson.toJson(workoutTime);
      workoutString = friendlyString(workoutString);
      roundTypeString = gson.toJson(typeOfRound);
      roundTypeString = friendlyString(roundTypeString);

      if (cycleHasActivityAssigned) {
        cycles.setTdeeActivityExists(true);
        cycles.setTdeeCatPosition(selectedTdeeCategoryPosition);
        cycles.setTdeeSubCatPosition(selectedTdeeSubCategoryPosition);
        cycles.setActivityString(getTdeeActivityStringFromArrayPosition());

        if (!isNewCycle) {
          cycles.setCurrentlyTrackingCycle(trackActivityWithinCycle);
        } else {
          cycles.setCurrentlyTrackingCycle(true);
        }

      } else {
        cycles.setTdeeActivityExists(false);
        cycles.setTdeeCatPosition(0);
        cycles.setTdeeSubCatPosition(0);
        cycles.setActivityString(getString(R.string.add_activity));
        cycles.setCurrentlyTrackingCycle(false);
      }

      if (!workoutString.equals("")) {
        cycles.setWorkoutRounds(workoutString);
        cycles.setRoundType(roundTypeString);
        cycles.setTimeAccessed(System.currentTimeMillis());
        cycles.setItemCount(workoutTime.size());
        cycles.setTitle(cycleTitle);

        if (isNewCycle) {
          cycles.setTimeAdded(System.currentTimeMillis());
          cyclesDatabase.cyclesDao().insertCycle(cycles);
        } else {
          cyclesDatabase.cyclesDao().updateCycles(cycles);
        }
      }
    }
    if (mode == 3) {
      if (isNewCycle) {
        pomCycles = new PomCycles();
      } else if (pomCyclesList.size() > 0) {
        cycleID = pomCyclesList.get(positionOfSelectedCycle).getId();
        pomCycles = cyclesDatabase.cyclesDao().loadSinglePomCycle(cycleID).get(0);
      }

      pomString = gson.toJson(pomValuesTime);
      pomString = friendlyString(pomString);

      if (!pomString.equals("")) {
        pomCycles.setFullCycle(pomString);
        pomCycles.setTimeAccessed(System.currentTimeMillis());
        pomCycles.setTitle(cycleTitle);

        if (isNewCycle) {
          pomCycles.setTimeAdded(System.currentTimeMillis());
          cyclesDatabase.cyclesDao().insertPomCycle(pomCycles);
        } else {
          cyclesDatabase.cyclesDao().updatePomCycles(pomCycles);
        }
      }
    }

    runOnUiThread(() -> {
      cycle_title_textView.setText(cycleTitle);
    });
  }

  private double calculateCaloriesBurnedPerMinute(double metValue) {
    double weightConversion = userWeight;
    if (!metricMode) weightConversion = weightConversion / 2.205;
    double caloriesBurnedPerMinute = (metValue * 3.5 * weightConversion) / 200;
    return caloriesBurnedPerMinute;
  }

  private double calculateCaloriesBurnedPerSecond() {
    return calculateCaloriesBurnedPerMinute(metScore) / 60;
  }

  private double calculateCaloriesBurnedPerMillis() {
    return calculateCaloriesBurnedPerSecond() / 1000;
  }

  private double calculateCaloriesBurnedForElapsedTime(long elapsedTimeInSeconds) {
    return elapsedTimeInSeconds * calculateCaloriesBurnedPerMillis();
  }

  private void setCaloriesBurnedForAllActivitiesToGlobalVariable() {
    totalCaloriesBurnedForCurrentDay = calculateCaloriesBurnedForElapsedTime(totalSetTimeForCurrentDayInMillis);
  }

  private String formatCalorieString(double calories) {
    DecimalFormat df = new DecimalFormat("#.##");
    df.setMinimumFractionDigits(2);
    return df.format(calories);
  }

  private void roundCycleSetTimeDown() {
    totalCycleSetTimeInMillis = roundDownMillisValues(totalCycleSetTimeInMillis);
  }

  private void roundCycleBreakTimeDown() {
    totalCycleBreakTimeInMillis = roundDownMillisValues(totalCycleBreakTimeInMillis);
  }

  private void roundDailyStatTimesDown() {
    totalSetTimeForCurrentDayInMillis = roundDownMillisValues(totalSetTimeForCurrentDayInMillis);
    totalSetTimeForSpecificActivityForCurrentDayInMillis = roundDownMillisValues(totalSetTimeForSpecificActivityForCurrentDayInMillis);
  }

  private void roundDownPomCycleWorkTime() {
    totalCycleWorkTimeInMillis = roundDownMillisValues(totalCycleWorkTimeInMillis);
  }

  private void roundDownPomCycleRestTime() {
    totalCycleRestTimeInMillis = roundDownMillisValues(totalCycleRestTimeInMillis);
  }

  private long roundDownMillisValues(long millisToRound) {
    return millisToRound -= (millisToRound % 1000);
  }

  private long roundUpMillisValues(long millisToRound) {
    long remainder = millisToRound % 1000;
    return millisToRound += (1000 - remainder);
  }

  private double roundDownDoubleValuesToSyncCalories(double caloriesToRound) {
    caloriesToRound += 1;
    DecimalFormat df = new DecimalFormat("#");
    String truncatedCalorieString = df.format(caloriesToRound);

    return Double.parseDouble(truncatedCalorieString);
  }

  private long dividedMillisForTimerDisplay(long millis) {
    return (millis + 999) / 1000;
  }

  private long dividedMillisForTotalTimesDisplay(long millis) {
    return (millis) / 1000;
  }

  private long roundToNearestFullThousandth(long valueToRound) {
    long remainder = valueToRound % 1000;
    if (remainder <= 500) {
      return valueToRound -= remainder;
    } else {
      return valueToRound += (1000 - remainder);
    }
  }

  private void updateDailyStatTextViewsIfTimerHasAlsoUpdated(TextViewDisplaySync textViewDisplaySync) {
    textViewDisplaySync.setFirstTextView((String) timeLeft.getText());

    if (textViewDisplaySync.areTextViewsDifferent()) {
      textViewDisplaySync.setSecondTextView(textViewDisplaySync.getFirstTextView());
      setTotalDailyActivityTimeToTextView();
      setTotalSingleActivityTimeToTextView();
    }

    setTotalDailyCaloriesToTextView();
    setTotalSingleActivityCaloriesToTextView();
  }

  private void setAllActivityTimesAndCaloriesToTextViews() {
    setTotalDailyActivityTimeToTextView();
    setTotalDailyCaloriesToTextView();
    setTotalSingleActivityTimeToTextView();
    setTotalSingleActivityCaloriesToTextView();
    dailySingleActivityStringHeader.setText(getTdeeActivityStringFromArrayPosition());
  }

  private void setTotalDailyActivityTimeToTextView() {
//    long roundedTotalDailyActivityTime = roundToNearestFullThousandth(totalSetTimeForCurrentDayInMillis);
    dailyTotalTimeTextView.setText(longToStringConverters.convertMillisToHourBasedString(totalSetTimeForCurrentDayInMillis));
  }

  private void setTotalDailyCaloriesToTextView() {
    dailyTotalCaloriesTextView.setText(formatCalorieString(totalCaloriesBurnedForCurrentDay));
  }

  private void setTotalSingleActivityTimeToTextView() {
//    long roundedSingleActivityTime = roundToNearestFullThousandth(totalSetTimeForSpecificActivityForCurrentDayInMillis);
    dailyTotalTimeForSingleActivityTextView.setText(longToStringConverters.convertMillisToHourBasedString(totalSetTimeForSpecificActivityForCurrentDayInMillis));
  }

  private void setTotalSingleActivityCaloriesToTextView() {
    dailyTotalCaloriesForSingleActivityTextView.setText(formatCalorieString(totalCaloriesBurnedForSpecificActivityForCurrentDay));
  }

  private void updateCycleTimesTextViewsIfTimerHasAlsoUpdated(TextViewDisplaySync textViewDisplaySync) {
    textViewDisplaySync.setFirstTextView((String) timeLeft.getText());

    if (textViewDisplaySync.areTextViewsDifferent()) {
      textViewDisplaySync.setSecondTextView(textViewDisplaySync.getFirstTextView());

      setTotalCycleTimeValuesToTextView();
    }
  }

  private void setCyclesCompletedTextView() {
    cycles_completed_textView.setText(getString(R.string.cycles_done, cyclesCompleted));
  }

  private void setTotalCycleTimeValuesToTextView() {
    if (mode == 1) {
//      long roundedSetTime = roundToNearestFullThousandth(totalCycleSetTimeInMillis);
//      long roundedBreakTime = roundToNearestFullThousandth(totalCycleBreakTimeInMillis);

      total_set_time.setText(convertSeconds(dividedMillisForTotalTimesDisplay(totalCycleSetTimeInMillis)));
      total_break_time.setText(convertSeconds(dividedMillisForTotalTimesDisplay(totalCycleBreakTimeInMillis)));
    }
    if (mode == 3) {
//      long roundedWorkTime = roundToNearestFullThousandth(totalCycleWorkTimeInMillis);
//      long roundedRestTime = roundToNearestFullThousandth(totalCycleRestTimeInMillis);

      total_set_time.setText(convertSeconds(dividedMillisForTotalTimesDisplay(totalCycleWorkTimeInMillis)));
      total_break_time.setText(convertSeconds(dividedMillisForTotalTimesDisplay(totalCycleRestTimeInMillis)));
    }
  }

  private void setCycleTimeToIterate() {
    if (mode == 1) {
      if (typeOfRound.get(currentRound) == 1 || typeOfRound.get(currentRound) == 2) {
        CYCLE_TIME_TO_ITERATE = CYCLE_SETS;
      }
      if (typeOfRound.get(currentRound) == 3 || typeOfRound.get(currentRound) == 4) {
        CYCLE_TIME_TO_ITERATE = CYCLE_BREAKS;
      }
    }

    if (mode == 3) {
      switch (pomDotCounter) {
        case 0:
        case 2:
        case 4:
        case 6:
          CYCLE_TIME_TO_ITERATE = POM_CYCLE_WORK;
          break;
        case 1:
        case 3:
        case 5:
        case 7:
          CYCLE_TIME_TO_ITERATE = POM_CYCLE_REST;
          break;
      }
    }
  }

  private Runnable stopWatchRunnable() {
    TimerIteration timerIteration = new TimerIteration();
    timerIteration.setStableTime(System.currentTimeMillis());
    timerIteration.setPreviousTotal(stopWatchTotalTime);


    return new Runnable() {
      @Override
      public void run() {

        DecimalFormat df2 = new DecimalFormat("00");

        timerIteration.setCurrentTime(System.currentTimeMillis());
        long timeToIterate = timerIteration.getDifference();
        timerIteration.setNewTotal(timerIteration.getPreviousTotal() + timeToIterate);

        stopWatchTotalTime = timerIteration.getNewTotal();

        stopWatchSeconds = (int) (stopWatchTotalTime) / 1000;
        stopWatchMinutes = (int) stopWatchSeconds / 60;
        stopWatchMs = (stopWatchTotalTime % 1000) / 10;

        displayTime = convertSeconds((long) stopWatchSeconds);
        displayMs = df2.format(stopWatchMs);

        stopWatchTimeTextView.setText(displayTime);
        msTimeTextView.setText(displayMs);

        decreaseTextSizeForTimersForStopWatch(stopWatchTotalTime);

        if (stopWatchTotalTime < 3600000) {
          mHandler.postDelayed(this, 10);
        } else {
          animateStopwatchEnding();
          new_lap.setEnabled(false);

          stopWatchTimerEnded = true;
          mHandler.removeCallbacks(stopWatchTimerRunnable);
        }
      }
    };
  }

  private void newLapLogic() {
    if (lapAdapter.getItemCount() > 98) {
      return;
    }

    if (empty_laps.getVisibility() == View.VISIBLE) {
      empty_laps.setVisibility(View.INVISIBLE);
    }

    savedEntries = longToStringConverters.convertMillisStopWatchString(stopWatchTotalTime);

    if (savedLapList.size() > 0) {
      String retrievedLap = savedLapList.get(savedLapList.size() - 1);
      String[] splitLap = retrievedLap.split(":");
      int pulledMinute = Integer.parseInt(splitLap[0]) / 60;
      int convertedMinute = pulledMinute * 60 * 1000;
      int convertedSecond = Integer.parseInt(splitLap[1]) * 1000;
      int convertedMs = Integer.parseInt(splitLap[2]) * 10;

      int totalMs = convertedMinute + convertedSecond + convertedMs;

      int totalNewTime = ((int) stopWatchTotalTime - totalMs);

      newEntries = longToStringConverters.convertMillisStopWatchString(totalNewTime);
    } else {
      newEntries = savedEntries;
    }

    currentLapList.add(newEntries);
    savedLapList.add(savedEntries);
    lapRecyclerLayoutManager.scrollToPosition(savedLapList.size() - 1);

    lapAdapter.notifyDataSetChanged();

    lapsNumber++;
    lapAdapter.setHaveWeBegunScrolling(false);

    laps_completed_textView.setText(getString(R.string.laps_completed, lapsNumber));
  }

  private void updateNotificationsIfTimerTextViewHasChanged(TextViewDisplaySync textViewDisplaySync) {
    textViewDisplaySync.setFirstTextView((String) timeLeft.getText());

    if (textViewDisplaySync.areTextViewsDifferent()) {
      textViewDisplaySync.setSecondTextView(textViewDisplaySync.getFirstTextView());
      setNotificationValues();
    }
  }

  private Runnable notifcationsRunnable() {
    TextViewDisplaySync textViewDisplaySyncForNotifications = new TextViewDisplaySync();
    textViewDisplaySyncForNotifications.setFirstTextView((String) timeLeft.getText());
    textViewDisplaySyncForNotifications.setSecondTextView((String) timeLeft.getText());

    return new Runnable() {
      @Override
      public void run() {
        updateNotificationsIfTimerTextViewHasChanged(textViewDisplaySyncForNotifications);

        mHandler.postDelayed(this, 10);
      }
    };
  }

  private Runnable infinityRunnableForDailyActivityTime() {
    TimerIteration timerIteration = new TimerIteration();
    timerIteration.setStableTime(System.currentTimeMillis());

    timerIteration.setPreviousDailyTotal(totalSetTimeForCurrentDayInMillis);
    timerIteration.setPreviousActivityTotal(totalSetTimeForSpecificActivityForCurrentDayInMillis);

    CalorieIteration calorieIteration = new CalorieIteration();
    calorieIteration.setPreviousTotalCalories(totalCaloriesBurnedForCurrentDay);
    calorieIteration.setPreviousActivityCalories(totalCaloriesBurnedForSpecificActivityForCurrentDay);

    TextViewDisplaySync textViewDisplaySync = new TextViewDisplaySync();
    textViewDisplaySync.setFirstTextView((String) timeLeft.getText());
    textViewDisplaySync.setSecondTextView((String) timeLeft.getText());

    setNotificationValues();

    return new Runnable() {
      @Override
      public void run() {
        timerIteration.setCurrentTime(System.currentTimeMillis());

        long timeToIterate = timerIteration.getDifference();
        double caloriesToIterate = calculateCaloriesBurnedPerMillis() * timeToIterate;

        //If calories have not iterated in hundredths during tick, display will not update, which is why they won't be 100% sync'd at faster interval delays.
        timerIteration.setNewDailyTotal(timerIteration.getPreviousDailyTotal() + timeToIterate);
        timerIteration.setNewActivityTotal(timerIteration.getPreviousActivityTotal() + timeToIterate);
        totalSetTimeForCurrentDayInMillis = timerIteration.getNewDailyTotal();
        totalSetTimeForSpecificActivityForCurrentDayInMillis = timerIteration.getNewActivityTotal();

        calorieIteration.setNewTotalCalories(calorieIteration.getPreviousTotalCalories() + caloriesToIterate);
        calorieIteration.setNewActivityCalories(calorieIteration.getPreviousActivityCalories() + caloriesToIterate);
        totalCaloriesBurnedForCurrentDay = calorieIteration.getNewTotalCalories();
        totalCaloriesBurnedForSpecificActivityForCurrentDay = calorieIteration.getNewActivityCalories();

        updateDailyStatTextViewsIfTimerHasAlsoUpdated(textViewDisplaySync);

        mHandler.postDelayed(this, 10);
      }
    };
  }

  private Runnable infinityRunnableForCyclesTimer() {
    setCycleTimeToIterate();

    TimerIteration timerIteration = new TimerIteration();
    timerIteration.setStableTime(System.currentTimeMillis());

    TextViewDisplaySync textViewDisplaySync = new TextViewDisplaySync();
    textViewDisplaySync.setFirstTextView((String) timeLeft.getText());
    textViewDisplaySync.setSecondTextView((String) timeLeft.getText());

    if (CYCLE_TIME_TO_ITERATE == CYCLE_SETS) {
      timerIteration.setPreviousTotal(totalCycleSetTimeInMillis);
    }
    if (CYCLE_TIME_TO_ITERATE == CYCLE_BREAKS) {
      timerIteration.setPreviousTotal(totalCycleBreakTimeInMillis);
    }

    return new Runnable() {
      @Override
      public void run() {
        timerIteration.setCurrentTime(System.currentTimeMillis());
        long timeToIterate = timerIteration.getDifference();
        timerIteration.setNewTotal(timerIteration.getPreviousTotal() + timeToIterate);

        if (CYCLE_TIME_TO_ITERATE == CYCLE_SETS) {
          totalCycleSetTimeInMillis = timerIteration.getNewTotal();
        }
        if (CYCLE_TIME_TO_ITERATE == CYCLE_BREAKS) {
          totalCycleBreakTimeInMillis = timerIteration.getNewTotal();
        }

        updateCycleTimesTextViewsIfTimerHasAlsoUpdated(textViewDisplaySync);

        mHandler.postDelayed(this, 10);
      }
    };
  }

  //Runs work/rest time, which iterates up.
  private Runnable infinityRunnableForPomCyclesTimer() {
    setCycleTimeToIterate();

    TimerIteration timerIteration = new TimerIteration();
    timerIteration.setStableTime(System.currentTimeMillis());

    TextViewDisplaySync textViewDisplaySync = new TextViewDisplaySync();
    textViewDisplaySync.setFirstTextView((String) timeLeft.getText());
    textViewDisplaySync.setSecondTextView((String) timeLeft.getText());

    if (CYCLE_TIME_TO_ITERATE == POM_CYCLE_WORK) {
      timerIteration.setPreviousTotal(totalCycleWorkTimeInMillis);
    }
    if (CYCLE_TIME_TO_ITERATE == POM_CYCLE_REST) {
      timerIteration.setPreviousTotal(totalCycleRestTimeInMillis);
    }

    return new Runnable() {
      @Override
      public void run() {
        timerIteration.setCurrentTime(System.currentTimeMillis());
        long timeToIterate = timerIteration.getDifference();
        timerIteration.setNewTotal(timerIteration.getPreviousTotal() + timeToIterate);

        if (CYCLE_TIME_TO_ITERATE == POM_CYCLE_WORK) {
          totalCycleWorkTimeInMillis = timerIteration.getNewTotal();
        }
        if (CYCLE_TIME_TO_ITERATE == POM_CYCLE_REST) {
          totalCycleRestTimeInMillis = timerIteration.getNewTotal();
        }

        updateCycleTimesTextViewsIfTimerHasAlsoUpdated(textViewDisplaySync);

        mHandler.postDelayed(this, 10);
      }
    };
  }

  private Runnable infinityRunnableForSets() {
    if (valueAnimatorDown.isStarted()) {
      valueAnimatorDown.cancel();
    }

    setInitialTextSizeForTimers(0);

    TimerIteration timerIteration = new TimerIteration();
    timerIteration.setStableTime(System.currentTimeMillis());
    timerIteration.setPreviousTotal(setMillis);

    return new Runnable() {
      @Override
      public void run() {
        timerIteration.setCurrentTime(System.currentTimeMillis());
        long timeToIterate = timerIteration.getDifference();

        timerIteration.setNewTotal(timerIteration.getPreviousTotal() + timeToIterate);
        setMillis = timerIteration.getNewTotal();

        timeLeft.setText(convertSeconds(setMillis / 1000));

        if (workoutTime.size() >= numberOfRoundsLeft) {
          workoutTime.set(workoutTime.size() - numberOfRoundsLeft, (int) setMillis);
        }

        ArrayList<String> convertedWorkoutRoundList = convertMillisIntegerListToTimerStringList(workoutTime);

        dotsAdapter.setCycleRoundsAsStringsList(convertedWorkoutRoundList);
        dotsAdapter.notifyDataSetChanged();

        decreaseTextSizeForTimers(setMillis);
        mHandler.postDelayed(this, timerRunnableDelay);
      }
    };
  }

  private Runnable infinityRunnableForBreaks() {
    if (valueAnimatorDown.isStarted()) {
      valueAnimatorDown.cancel();
    }

    setInitialTextSizeForTimers(0);

    TimerIteration timerIteration = new TimerIteration();
    timerIteration.setStableTime(System.currentTimeMillis());
    timerIteration.setPreviousTotal(breakMillis);

    return new Runnable() {
      @Override
      public void run() {
        timerIteration.setCurrentTime(System.currentTimeMillis());
        long timeToIterate = timerIteration.getDifference();

        timerIteration.setNewTotal(timerIteration.getPreviousTotal() + timeToIterate);
        breakMillis = timerIteration.getNewTotal();

        timeLeft.setText(convertSeconds(breakMillis / 1000));

        if (workoutTime.size() >= numberOfRoundsLeft) {
          workoutTime.set(workoutTime.size() - numberOfRoundsLeft, (int) breakMillis);
        }

        ArrayList<String> convertedWorkoutRoundList = convertMillisIntegerListToTimerStringList(workoutTime);

        dotsAdapter.setCycleRoundsAsStringsList(convertedWorkoutRoundList);
        dotsAdapter.notifyDataSetChanged();

        decreaseTextSizeForTimers(breakMillis);
        mHandler.postDelayed(this, timerRunnableDelay);
      }
    };
  }

  private ArrayList<String> convertMillisIntegerListToTimerStringList(ArrayList<Integer> listToConvert) {
    ArrayList<String> listToReturn = new ArrayList<>();

    for (int i = 0; i < listToConvert.size(); i++) {
      listToReturn.add(convertSeconds(listToConvert.get(i) / 1000));
    }

    return listToReturn;
  }

  private void startSetTimer() {
    long startMillis = setMillis;
    long initialMillisValue = setMillis;
    setInitialTextSizeForTimers(setMillis);

    timer = new CountDownTimer(setMillis, timerRunnableDelay) {
      @Override
      public void onTick(long millisUntilFinished) {
        currentProgressBarValue = (int) objectAnimator.getAnimatedValue();
        setMillis = millisUntilFinished;
        timeLeft.setText(convertSeconds(dividedMillisForTimerDisplay(setMillis)));

        if (setMillis < 500) timerDisabled = true;

        increaseTextSizeForTimers(startMillis, setMillis);

        dotsAdapter.notifyDataSetChanged();
      }

      @Override
      public void onFinish() {
        nextRound(false);
      }
    }.start();
  }

  private void startBreakTimer() {
    long startMillis = breakMillis;
    setInitialTextSizeForTimers(breakMillis);
    long initialMillisValue = breakMillis;

    timer = new CountDownTimer(breakMillis, timerRunnableDelay) {
      @Override
      public void onTick(long millisUntilFinished) {
        currentProgressBarValue = (int) objectAnimator.getAnimatedValue();
        breakMillis = millisUntilFinished;
        timeLeft.setText(convertSeconds(dividedMillisForTimerDisplay(breakMillis)));
        if (breakMillis < 500) timerDisabled = true;

        increaseTextSizeForTimers(startMillis, breakMillis);

        dotsAdapter.notifyDataSetChanged();
      }

      @Override
      public void onFinish() {
        nextRound(false);
      }
    }.start();
  }

  private void startPomTimer() {
    long startMillis = pomMillis;
    setInitialTextSizeForTimers(pomMillis);
    long initialMillisValue = pomMillis;

    timer = new CountDownTimer(pomMillis, timerRunnableDelay) {
      @Override
      public void onTick(long millisUntilFinished) {

        currentProgressBarValue = (int) objectAnimatorPom.getAnimatedValue();
        pomMillis = millisUntilFinished;
        timeLeft.setText(convertSeconds(dividedMillisForTimerDisplay(pomMillis)));
        if (pomMillis < 500) timerDisabled = true;

        increaseTextSizeForTimers(startMillis, pomMillis);

        pomDotsAdapter.notifyDataSetChanged();
      }

      @Override
      public void onFinish() {
        nextPomRound(false);
      }
    }.start();
  }

  private void increaseTextSizeForTimers(long startMillis, long iteratingMillis) {
    if (startMillis < 60000) {
      return;
    }

    if (!getHasTextSizeChangedForTimers()) {
      if (iteratingMillis <= 59000) {
        changeTextSizeWithAnimator(valueAnimatorUp, timeLeft);
        setHasTextSizeChangedForTimers(true);
      }
    }
  }

  private void decreaseTextSizeForTimers(long millis) {
    if (!getHasTextSizeChangedForTimers()) {
      if (millis >= 60000) {
        changeTextSizeWithAnimator(valueAnimatorDown, timeLeft);
        setHasTextSizeChangedForTimers(true);
      }
    }
  }

  private void decreaseTextSizeForTimersForStopWatch(long millis) {
    if (!getHasTextSizeChangedForStopwatch()) {
      if (millis >= 60000) {
        changeTextSizeWithAnimator(valueAnimatorDown, stopWatchTimeTextView);
        setHasTextSizeChangedForStopWatch(true);
      }
    }
  }

  private boolean getHasTextSizeChangedForTimers() {
    if (mode == 1) {
      return cyclesTextSizeHasChanged;
    }
    if (mode == 3) {
      return pomCyclesTextSizeHasChanged;
    }
    return false;
  }

  private void setHasTextSizeChangedForTimers(boolean hasChanged) {
    if (mode == 1) {
      cyclesTextSizeHasChanged = hasChanged;
    }
    if (mode == 3) {
      pomCyclesTextSizeHasChanged = hasChanged;
    }
  }

  private boolean getHasTextSizeChangedForStopwatch() {
    return stopWatchTextSizeHasChanged;
  }

  private void setHasTextSizeChangedForStopWatch(boolean hasChanged) {
    stopWatchTextSizeHasChanged = hasChanged;
  }

  private void setInitialTextSizeForTimers(long millis) {
    if (valueAnimatorDown.isRunning()) {
      valueAnimatorDown.cancel();
    }
    if (valueAnimatorUp.isRunning()) {
      valueAnimatorUp.cancel();
    }

    if (millis >= 60000) {
      if (phoneHeight < 1920) {
        timeLeft.setTextSize(70f);
      } else {
        Log.i("testChange", "changing to smaller size!");
        timeLeft.setTextSize(90f);
      }
    } else {
      if (phoneHeight < 1920) {
        timeLeft.setTextSize(90f);
      } else {
        Log.i("testChange", "changing to larger size!");
        timeLeft.setTextSize(120f);
      }
    }
  }

  private void setInitialTextSizeForStopWatch() {
    if (valueAnimatorDown.isRunning()) {
      valueAnimatorDown.cancel();
    }

    if (phoneHeight < 1920) {
      stopWatchTimeTextView.setTextSize(90f);
    } else {
      stopWatchTimeTextView.setTextSize(120f);
    }
  }

  private void changeTextSizeWithAnimator(ValueAnimator va, TextView textView) {
    changeValueAnimatorNumbers();
    sizeAnimator = va;
    sizeAnimator.addUpdateListener(animation -> {
      float sizeChange = (float) va.getAnimatedValue();
      textView.setTextSize(sizeChange);
    });
    sizeAnimator.setRepeatCount(0);
    sizeAnimator.start();
  }

  private void changeValueAnimatorNumbers() {
    if (phoneHeight < 1920) {
      valueAnimatorDown.setFloatValues(90f, 70f);
      valueAnimatorUp.setFloatValues(70f, 90f);
    } else {
      valueAnimatorDown.setFloatValues(120f, 90f);
      valueAnimatorUp.setFloatValues(90f, 120f);
    }
  }

  private void changeTextSizeWithoutAnimator(long millis) {
    if (phoneHeight < 1920) {

      if (millis < 60000) {
        timeLeft.setTextSize(90f);
      } else {
        timeLeft.setTextSize(70f);
      }
    } else {
      if (millis < 60000) {
        timeLeft.setTextSize(120f);
      } else {
        timeLeft.setTextSize(90f);
      }
    }
  }

  private void nextRound(boolean endingEarly) {
    if (numberOfRoundsLeft == 0) {
      mHandler.removeCallbacks(endFadeForModeOne);
      resetTimer();
      return;
    }

    if (!endingEarly) {
      if (typeOfRound.get(currentRound) == 1 || typeOfRound.get(currentRound) == 3) {
        timeLeft.setText("0");
      }
      setNotificationValues();
    }

    globalNextRoundLogic();
    mHandler.post(endFadeForModeOne);

    if (endingEarly) {
      if (timer != null) timer.cancel();
      if (objectAnimator != null) objectAnimator.cancel();
      if (!activeCycle) activeCycle = true;
      progressBar.setProgress(0);
    }

    if (trackActivityWithinCycle) {
      setAllActivityTimesAndCaloriesToTextViews();
    } else {
      setTotalCycleTimeValuesToTextView();
    }

    boolean isAlertRepeating = false;

    if (numberOfRoundsLeft == 1 && isLastRoundSoundContinuous) {
      isAlertRepeating = true;
    }

    switch (typeOfRound.get(currentRound)) {
      case 1:
        setEndOfRoundSounds(vibrationSettingForSets, isAlertRepeating);
        break;
      case 2:
        mHandler.removeCallbacks(infinityTimerForSetsRunnable);
        setEndOfRoundSounds(vibrationSettingForSets, isAlertRepeating);
        break;
      case 3:
        setEndOfRoundSounds(vibrationSettingForBreaks, isAlertRepeating);
        break;
      case 4:
        mHandler.removeCallbacks(infinityTimerForBreaksRunnable);
        setEndOfRoundSounds(vibrationSettingForBreaks, isAlertRepeating);
        break;
    }

    removeActivityOrCycleTimeRunnables(trackActivityWithinCycle);

    numberOfRoundsLeft--;
    if (currentRound < typeOfRound.size() - 1) {
      currentRound++;
    }

    mHandler.postDelayed(postRoundRunnableForFirstMode(), 750);
  }

  private void nextPomRound(boolean endingEarly) {
    Log.i("testPom", "dot counter is " + pomDotCounter);
    if (pomDotCounter == 8) {
      mHandler.removeCallbacks(endFadeForModeThree);
      resetTimer();
      return;
    }

    if (!endingEarly) {
      timeLeft.setText("0");
      setNotificationValues();
    }

    globalNextRoundLogic();

    setTotalCycleTimeValuesToTextView();
    mHandler.post(endFadeForModeThree);

    removePomCycleTimeRunnable();

    if (pomDotCounter < 8) {
      pomDotCounter++;
    }

    if (endingEarly) {
      if (timer != null) timer.cancel();
      if (objectAnimatorPom != null) objectAnimatorPom.cancel();
      progressBar.setProgress(0);
    }

    switch (pomDotCounter) {
      case 0:
      case 2:
      case 4:
      case 6:
        setEndOfRoundSounds(vibrationSettingForWork, false);
        break;
      case 1:
      case 3:
      case 5:
        setEndOfRoundSounds(vibrationSettingForMiniBreaks, false);
        break;
      case 7:
        boolean isAlertRepeating = false;
        if (isFullBreakSoundContinuous) isAlertRepeating = true;
        setEndOfRoundSounds(vibrationSettingForMiniBreaks, isAlertRepeating);
    }

    mHandler.postDelayed(postRoundRunnableForThirdMode(), 750);

  }

  private void globalNextRoundLogic() {
    timerIsPaused = false;
    timerDisabled = true;
    setHasTextSizeChangedForTimers(false);

    progressBar.startAnimation(fadeProgressOut);
    timeLeft.startAnimation(fadeProgressOut);
    reset.setVisibility(View.INVISIBLE);

    currentProgressBarValue = 10000;
    next_round.setEnabled(false);

    enableOrDisableCycleResetButton(false);

    AsyncTask.execute(globalSaveTotalTimesAndCaloriesInDatabaseRunnable);
  }

  private Runnable postRoundRunnableForFirstMode() {
    return new Runnable() {
      @Override
      public void run() {
        dotsAdapter.updateCycleRoundCount(startRounds, numberOfRoundsLeft);
        dotsAdapter.resetModeOneAlpha();
        dotsAdapter.setModeOneAlpha();

        setMillis = 0;
        breakMillis = 0;
        timerIsPaused = false;

        if (numberOfRoundsLeft > 0) {
          switch (typeOfRound.get(currentRound)) {
            case 1:
              setMillis = workoutTime.get(workoutTime.size() - numberOfRoundsLeft);
              timeLeft.setText(convertSeconds(dividedMillisForTimerDisplay(setMillis)));

              if (!objectAnimator.isStarted()) {
                startObjectAnimatorAndTotalCycleTimeCounters();
                startSetTimer();
              }
              break;
            case 2:
              timeLeft.setText("0");
              //Do not want to consolidate infinityTimer runnable methods, since we only want its global re-instantiated here, not in our pause/resume option.
              infinityTimerForSetsRunnable = infinityRunnableForSets();
              mHandler.post(infinityTimerForSetsRunnable);
              break;
            case 3:
              breakMillis = workoutTime.get(workoutTime.size() - numberOfRoundsLeft);
              timeLeft.setText(convertSeconds(dividedMillisForTimerDisplay(breakMillis)));

              if (!objectAnimator.isStarted()) {
                startObjectAnimatorAndTotalCycleTimeCounters();
                startBreakTimer();
              }
              break;
            case 4:
              timeLeft.setText("0");
              infinityTimerForBreaksRunnable = infinityRunnableForBreaks();
              mHandler.post(infinityTimerForBreaksRunnable);
              break;
          }

          postActivityOrCycleTimeRunnables(trackActivityWithinCycle);

        } else {
          animateTimerEnding();
          currentRound = 0;
          timerEnded = true;
          cyclesCompleted++;
          setCyclesCompletedTextView();
        }
        timerDisabled = false;
        next_round.setEnabled(true);
      }
    };
  }

  ;

  private Runnable postRoundRunnableForThirdMode() {
    return new Runnable() {
      @Override
      public void run() {
        pomDotsAdapter.setPomCycleRoundsAsStringsList(pomStringListOfRoundValues);
        pomDotsAdapter.updatePomDotCounter(pomDotCounter);
        pomDotsAdapter.resetModeThreeAlpha();
        pomDotsAdapter.setModeThreeAlpha();
        dotsAdapter.notifyDataSetChanged();

        if (pomDotCounter <= 7) {
          pomMillis = pomValuesTime.get(pomDotCounter);
          timeLeft.setText(convertSeconds(dividedMillisForTimerDisplay(pomMillis)));
          if (!objectAnimatorPom.isStarted()) {
            startObjectAnimatorAndTotalCycleTimeCounters();
            startPomTimer();
          }
          postPomCycleTimeRunnable();
        } else {
          animateTimerEnding();
          progressBar.setProgress(0);
          timerEnded = true;
          setCyclesCompletedTextView();
        }
        timerDisabled = false;
        next_round.setEnabled(true);
      }
    };
  }

  private int totalValueAddedToSingleValueAndDividedBy1000ToInteger(long totalVar, long singleVar) {
    return (int) (totalVar += singleVar) / 1000;
  }

  private void pauseAndResumeTimer(int pausing) {
    if (!timerDisabled) {
      if (!timerEnded) {
        if (pausing == PAUSING_TIMER) {
          switch (typeOfRound.get(currentRound)) {
            case 1:
              setMillisUntilFinished = setMillis;
              break;
            case 2:
              mHandler.removeCallbacks(infinityTimerForSetsRunnable);
              break;
            case 3:
              breakMillisUntilFinished = breakMillis;
              break;
            case 4:
              mHandler.removeCallbacks(infinityTimerForBreaksRunnable);
              break;
          }

          timerPauseLogic();
          removeActivityOrCycleTimeRunnables(trackActivityWithinCycle);

        } else if (pausing == RESUMING_TIMER) {
          switch (typeOfRound.get(currentRound)) {
            case 1:
              if (objectAnimator.isPaused() || !objectAnimator.isStarted()) {
                startObjectAnimatorAndTotalCycleTimeCounters();
                startSetTimer();
              }
              break;
            case 2:
              infinityTimerForSetsRunnable = infinityRunnableForSets();
              if (!mHandler.hasCallbacks(infinityTimerForSetsRunnable)) {
                mHandler.post(infinityTimerForSetsRunnable);
              }
              break;
            case 3:
              if (objectAnimator.isPaused() || !objectAnimator.isStarted()) {
                startObjectAnimatorAndTotalCycleTimeCounters();
                startBreakTimer();
              }
              break;
            case 4:
              infinityTimerForBreaksRunnable = infinityRunnableForBreaks();
              if (!mHandler.hasCallbacks(infinityTimerForBreaksRunnable)) {
                mHandler.post(infinityTimerForBreaksRunnable);
              }
              break;
          }
          postActivityOrCycleTimeRunnables(trackActivityWithinCycle);

          timerResumeLogic();
        }
        AsyncTask.execute(globalSaveTotalTimesAndCaloriesInDatabaseRunnable);
      } else {
        resetTimer();
      }
    }
  }

  private void timerPauseLogic() {
    timerIsPaused = true;
    reset.setVisibility(View.VISIBLE);

    if (timer != null) timer.cancel();
    if (objectAnimator != null) objectAnimator.pause();
  }

  private void timerResumeLogic() {
    activeCycle = true;
    timerIsPaused = false;
    reset.setVisibility(View.INVISIBLE);
    enableOrDisableCycleResetButton(false);
  }

  private void postActivityOrCycleTimeRunnables(boolean trackingActivity) {
    if (trackingActivity) {
      if (!isDailyActivityTimeMaxed()) {
        infinityRunnableForDailyActivityTimer = infinityRunnableForDailyActivityTime();

        if (!mHandler.hasCallbacks(infinityRunnableForDailyActivityTimer)) {
          mHandler.post(infinityRunnableForDailyActivityTimer);
        }
      }
    } else {
      infinityRunnableForCyclesTimer = infinityRunnableForCyclesTimer();

      if (!mHandler.hasCallbacks(infinityRunnableForCyclesTimer)) {
        mHandler.post(infinityRunnableForCyclesTimer);
      }
    }
  }

  private void removeActivityOrCycleTimeRunnables(boolean trackingActivity) {
    if (trackingActivity) {
      mHandler.removeCallbacks(infinityRunnableForDailyActivityTimer);
    } else {
      mHandler.removeCallbacks(infinityRunnableForCyclesTimer);
    }
  }

  private void pauseAndResumePomodoroTimer(int pausing) {
    if (!timerDisabled) {
      if (!timerEnded) {
        if (pausing == PAUSING_TIMER) {
          pomMillisUntilFinished = pomMillis;
          pomTimerPauseLogic();
          removePomCycleTimeRunnable();
        } else if (pausing == RESUMING_TIMER) {
          if (objectAnimatorPom.isPaused() || !objectAnimatorPom.isStarted()) {
            startObjectAnimatorAndTotalCycleTimeCounters();
            startPomTimer();
          }
          pomTimerResumeLogic();
          postPomCycleTimeRunnable();
        }
        AsyncTask.execute(globalSaveTotalTimesAndCaloriesInDatabaseRunnable);
      } else {
        resetTimer();
      }
    }
  }

  private void pomTimerPauseLogic() {
    if (objectAnimatorPom != null) objectAnimatorPom.pause();
    if (timer != null) timer.cancel();

    timerIsPaused = true;
    reset.setText(R.string.reset);
    reset.setVisibility(View.VISIBLE);
  }

  private void pomTimerResumeLogic() {
    activeCycle = true;
    timerIsPaused = false;
    reset.setVisibility(View.INVISIBLE);
    enableOrDisableCycleResetButton(false);
  }

  private void postPomCycleTimeRunnable() {
    infinityRunnableForPomCyclesTimer = infinityRunnableForPomCyclesTimer();

    if (!mHandler.hasCallbacks(infinityRunnableForPomCyclesTimer)) {
      mHandler.post(infinityRunnableForPomCyclesTimer);
    }
  }

  private void removePomCycleTimeRunnable() {
    mHandler.removeCallbacks(infinityRunnableForPomCyclesTimer);
  }

  private void enableOrDisableCycleResetButton(boolean enable) {
    if (enable) {
      reset_total_cycle_times.setEnabled(true);
      reset_total_cycle_times.setAlpha(1.0f);
    } else {
      if (reset_total_cycle_times.isEnabled()) {
        reset_total_cycle_times.setEnabled(false);
        reset_total_cycle_times.setAlpha(0.3f);
      }
    }
  }

  private void pauseAndResumeStopwatch(int pausing) {
    if (!stopWatchTimerEnded) {
      if (pausing == PAUSING_TIMER) {
        stopWatchIsPaused = true;

        new_lap.setAlpha(0.3f);
        new_lap.setEnabled(false);

        stopwatchReset.setVisibility(View.VISIBLE);

        mHandler.removeCallbacks(stopWatchTimerRunnable);
      } else if (pausing == RESUMING_TIMER) {
        stopWatchIsPaused = false;
        stopWatchstartTime = System.currentTimeMillis();

        new_lap.setAlpha(1.0f);
        new_lap.setEnabled(true);

        stopwatchReset.setVisibility(View.INVISIBLE);

        stopWatchTimerRunnable = stopWatchRunnable();
        mHandler.post(stopWatchTimerRunnable);
      }
    } else {
      resetStopwatchTimer();
    }
  }

  private void resetStopwatchTimer() {
    stopWatchTimerEnded = false;
    stopWatchIsPaused = true;

    stopWatchstartTime = 0;
    stopWatchTotalTime = 0;
    stopWatchTotalTimeHolder = 0;

    stopWatchMs = 0;
    stopWatchSeconds = 0;
    stopWatchMinutes = 0;

    stopWatchTimeTextView.setAlpha(1);
    stopWatchTimeTextView.setText("0");
    msTimeTextView.setText("00");

    if (currentLapList.size() > 0) currentLapList.clear();
    if (savedLapList.size() > 0) savedLapList.clear();

    lapsNumber = 0;

    lapAdapter.notifyDataSetChanged();
    stopwatchReset.setVisibility(View.INVISIBLE);
    empty_laps.setVisibility(View.VISIBLE);
    setInitialTextSizeForStopWatch();

    if (endAnimationForStopwatch != null) {
      endAnimationForStopwatch.cancel();
      new_lap.setAlpha(0.3f);
      new_lap.setEnabled(false);
    }

    setHasTextSizeChangedForStopWatch(false);
  }

  private void startObjectAnimatorAndTotalCycleTimeCounters() {
    switch (mode) {
      case 1:
        if (typeOfRound.get(currentRound).equals(1)) {
          if (currentProgressBarValue == maxProgress) {
            timerIsPaused = false;
            instantiateAndStartObjectAnimator(setMillis);
          } else {
            setMillis = setMillisUntilFinished;
            if (objectAnimator != null) {
              objectAnimator.resume();
            }
          }
        } else if (typeOfRound.get(currentRound).equals(3)) {
          if (currentProgressBarValue == maxProgress) {
            timerIsPaused = false;
            instantiateAndStartObjectAnimator(breakMillis);
          } else {
            breakMillis = breakMillisUntilFinished;
            if (objectAnimator != null) {
              objectAnimator.resume();
            }
          }
        }
        break;
      case 3:
        if (currentProgressBarValue == maxProgress) {
          timerIsPaused = false;
          pomMillis = pomValuesTime.get(pomDotCounter);
          instantiateAndStartObjectAnimator(pomMillis);
        } else {
          pomMillis = pomMillisUntilFinished;
          if (objectAnimatorPom != null) objectAnimatorPom.resume();
        }
        break;
    }
  }

  private void instantiateAndStartObjectAnimator(long duration) {
    if (mode == 1) {
      objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", maxProgress, 0);
      objectAnimator.setInterpolator(new LinearInterpolator());
      objectAnimator.setDuration(duration);
      objectAnimator.start();
    }
    if (mode == 3) {
      objectAnimatorPom = ObjectAnimator.ofInt(progressBar, "progress", maxProgress, 0);
      objectAnimatorPom.setInterpolator(new LinearInterpolator());
      objectAnimatorPom.setDuration(duration);
      objectAnimatorPom.start();
    }
  }

  private void setDefaultTimerValuesAndTheirEditTextViews() {
    setValue = 30;
    breakValue = 30;
    pomValue1 = 1500;
    pomValue2 = 300;
    pomValue3 = 1200;

    String editPopUpTimerString = convertedTimerArrayToString(editPopUpTimerArray);
    timerValueInEditPopUpTextView.setText(editPopUpTimerString);

    savedEditPopUpArrayForFirstHeaderModeThree = convertIntegerToStringArray(pomValue1);
    savedEditPopUpArrayForSecondHeaderModeThree = convertIntegerToStringArray(pomValue2);
    savedEditPopUpArrayForThirdHeader = convertIntegerToStringArray(pomValue3);

    String convertedStringOne = convertedTimerArrayToString(savedEditPopUpArrayForFirstHeaderModeThree);
    String convertedStringTwo = convertedTimerArrayToString(savedEditPopUpArrayForSecondHeaderModeThree);
    String convertedStringThree = convertedTimerArrayToString(savedEditPopUpArrayForThirdHeader);

    pomTimerValueInEditPopUpTextViewOne.setText(convertedStringOne);
    pomTimerValueInEditPopUpTextViewTwo.setText(convertedStringTwo);
    pomTimerValueInEditPopUpTextViewThree.setText(convertedStringThree);
  }

  private void setDefaultEditRoundViews() {
    toggleInfinityRounds.setAlpha(0.4f);
    removeTdeeActivityImageView.setVisibility(View.INVISIBLE);
    setDefaultTimerValuesAndTheirEditTextViews();

    //Causes blank timeLeft on launching from edit popUp dismiss.
//    timeLeft.setText(timeLeftValueHolder);
    setEditPopUpTimerHeaders(1);

    switch (mode) {
      case 1:
        firstRoundHeaderParams.startToStart = R.id.edit_cycle_layout;
        firstRoundHeaderParams.endToStart = R.id.secondRoundTypeHeaderInEditPopUp;
        secondRoundHeaderParams.endToEnd = R.id.edit_cycle_layout;
        secondRoundHeaderParams.startToEnd = R.id.firstRoundTypeHeaderInEditPopUp;

        secondRoundTypeHeaderInEditPopUp.setText(R.string.break_time);
        firstRoundTypeHeaderInEditPopUp.setText(R.string.set_time);

        secondEditHeaderParams.rightMargin = 20;
        deleteEditTimerNumbersParams.rightMargin = convertDensityPixelsToScalable(76);
        deleteEditTimerNumbersParams.topMargin = convertDensityPixelsToScalable(12);

        thirdRoundTypeHeaderInEditPopUp.setVisibility(View.GONE);
        toggleInfinityRounds.setVisibility(View.VISIBLE);
        timerValueInEditPopUpTextView.setVisibility(View.VISIBLE);
        sortHigh.setVisibility(View.VISIBLE);
        sortLow.setVisibility(View.VISIBLE);
        sortActivityTitleAtoZ.setVisibility(View.VISIBLE);
        sortActivityTitleZToA.setVisibility(View.VISIBLE);

        timerValueInEditPopUpTextView.setVisibility(View.VISIBLE);
        pomTimerValueInEditPopUpTextViewOne.setVisibility(View.GONE);
        pomTimerValueInEditPopUpTextViewTwo.setVisibility(View.GONE);
        pomTimerValueInEditPopUpTextViewThree.setVisibility(View.GONE);
        addTDEEfirstMainTextView.setVisibility(View.VISIBLE);

        total_set_header.setText(R.string.total_sets);
        break;
      case 3:
        firstRoundTypeHeaderInEditPopUp.setTextColor(workColor);

        secondEditHeaderParams.rightMargin = 0;
        deleteEditTimerNumbersParams.rightMargin = convertDensityPixelsToScalable(8);
        deleteEditTimerNumbersParams.topMargin = convertDensityPixelsToScalable(8);

        firstRoundHeaderParams.startToStart = R.id.edit_cycle_layout;
        firstRoundHeaderParams.endToStart = R.id.secondRoundTypeHeaderInEditPopUp;
        secondRoundHeaderParams.endToStart = R.id.thirdRoundTypeHeaderInEditPopUp;
        secondRoundHeaderParams.startToEnd = R.id.firstRoundTypeHeaderInEditPopUp;
        thirdRoundHeaderParams.endToEnd = R.id.edit_cycle_layout;
        thirdRoundHeaderParams.startToEnd = R.id.secondRoundTypeHeaderInEditPopUp;

        firstRoundTypeHeaderInEditPopUp.setText(R.string.work_time);
        secondRoundTypeHeaderInEditPopUp.setText(R.string.small_break);
        thirdRoundTypeHeaderInEditPopUp.setText(R.string.long_break);

        timerValueInEditPopUpTextView.setVisibility(View.VISIBLE);
        toggleInfinityRounds.setVisibility(View.GONE);
        thirdRoundTypeHeaderInEditPopUp.setVisibility(View.VISIBLE);

        timerValueInEditPopUpTextView.setVisibility(View.INVISIBLE);
        pomTimerValueInEditPopUpTextViewOne.setVisibility(View.VISIBLE);
        pomTimerValueInEditPopUpTextViewTwo.setVisibility(View.VISIBLE);
        pomTimerValueInEditPopUpTextViewThree.setVisibility(View.VISIBLE);

        setRoundRecyclerViewsWhenChangingAdapterCount(workoutTime);

        sortHigh.setVisibility(View.GONE);
        sortLow.setVisibility(View.GONE);
        sortActivityTitleAtoZ.setVisibility(View.GONE);
        sortActivityTitleZToA.setVisibility(View.GONE);
        roundRecyclerTwo.setVisibility(View.GONE);
        roundListDivider.setVisibility(View.GONE);

        addTDEEfirstMainTextView.setVisibility(View.INVISIBLE);
        ;
        total_set_header.setText(R.string.total_work);

//        savedCycleRecycler.setVisibility(View.GONE);
//        savedPomCycleRecycler.setVisibility(View.VISIBLE);
        break;
    }
  }

  private void getTimerVariablesForEachMode() {
    if (mode == 1) {
      currentProgressBarValue = sharedPreferences.getInt("savedProgressBarValueForModeOne", 10000);
      timeLeftValueHolder = sharedPreferences.getString("timeLeftValueForModeOne", "");
      positionOfSelectedCycle = sharedPreferences.getInt("positionOfSelectedCycleForModeOne", 0);
      timerIsPaused = sharedPreferences.getBoolean("modeOneTimerPaused", false);
      timerEnded = sharedPreferences.getBoolean("modeOneTimerEnded", false);
      timerDisabled = sharedPreferences.getBoolean("modeOneTimerDisabled", false);
    }

    if (mode == 3) {
      currentProgressBarValue = sharedPreferences.getInt("savedProgressBarValueForModeThree", 10000);
      timeLeftValueHolder = sharedPreferences.getString("timeLeftValueForModeThree", "");
      positionOfSelectedCycle = sharedPreferences.getInt("positionOfSelectedCycleForModeThree", 0);
      timerIsPaused = sharedPreferences.getBoolean("modeThreeTimerPaused", false);
      timerEnded = sharedPreferences.getBoolean("modeThreeTimerEnded", false);
      timerDisabled = sharedPreferences.getBoolean("modeThreeTimerDisabled", false);
    }
  }

  private void resetTimer() {
    mHandler.removeCallbacks(infinityTimerForSetsRunnable);
    mHandler.removeCallbacks(infinityTimerForBreaksRunnable);

    setHasTextSizeChangedForTimers(false);

    vibrator.cancel();
    if (timer != null) timer.cancel();
    if (endAnimationForTimer != null) endAnimationForTimer.cancel();

    if (mediaPlayer.isPlaying()) {
      mediaPlayer.stop();
      mediaPlayer.reset();
    }

    activeCycle = false;
    timerIsPaused = true;
    timerEnded = false;
    timerDisabled = false;
    next_round.setEnabled(true);

    progressBar.setProgress(10000);
    currentProgressBarValue = 10000;
    delayBeforeTimerBeginsSyncingWithTotalTimeStats = 1000;

    reset.setText(R.string.reset);
    reset.setVisibility(View.INVISIBLE);
    enableOrDisableCycleResetButton(true);

    cycles_completed_textView.setText(R.string.cycles_done);

    toggleLayoutParamsForCyclesAndStopwatch();
    setCyclesCompletedTextView();

    clearAndRepopulateCycleAdapterListsFromDatabaseList(false);

    if (mode == 1) {
      if (workoutTime.size() > 0) {
        switch (typeOfRound.get(0)) {
          case 1:
            setMillis = workoutTime.get(0);
            timeLeft.setText(convertSeconds((dividedMillisForTimerDisplay(setMillis))));
            setInitialTextSizeForTimers(setMillis);
            break;
          case 2:
            setMillis = 0;
            timeLeft.setText("0");
            setInitialTextSizeForTimers(0);
            break;
          case 3:
            breakMillis = workoutTime.get(0);
            timeLeft.setText(convertSeconds(((dividedMillisForTimerDisplay(breakMillis)))));
            setInitialTextSizeForTimers(breakMillis);
            break;
          case 4:
            breakMillis = 0;
            timeLeft.setText("0");
            setInitialTextSizeForTimers(0);
            break;
        };

        for (int i = 0; i < workoutTime.size(); i++) {
          if (typeOfRound.get(i) == 2 || typeOfRound.get(i) == 4) {
            workoutTime.set(i, 0);
          }
        }

        currentRound = 0;
        startRounds = workoutTime.size();
        numberOfRoundsLeft = startRounds;

        ArrayList<String> convertedWorkoutRoundList = convertMillisIntegerListToTimerStringList(workoutTime);

        dotsAdapter.setCycleRoundsAsStringsList(convertedWorkoutRoundList);
        dotsAdapter.setTypeOfRoundList(typeOfRound);
        dotsAdapter.updateCycleRoundCount(startRounds, numberOfRoundsLeft);

        dotsAdapter.resetModeOneAlpha();
        dotsAdapter.setModeOneAlpha();
        dotsAdapter.notifyDataSetChanged();

        cyclesTextSizeHasChanged = false;

        roundCycleSetTimeDown();
        roundCycleBreakTimeDown();
        roundDailyStatTimesDown();

        if (objectAnimator != null) {
          objectAnimator.cancel();
        }

        if (mHandler.hasCallbacks(endFadeForModeOne)) {
          mHandler.removeCallbacks(endFadeForModeOne);
        }

        if (savedCycleAdapter.isCycleActive() == true) {
          savedCycleAdapter.removeActiveCycleLayout();
          savedCycleAdapter.notifyDataSetChanged();
        }
      }
    }

    if (mode == 3) {
      pomDotCounter = 0;
      if (pomValuesTime.size() > 0) {
        pomMillis = pomValuesTime.get(0);
        timeLeft.setText(convertSeconds(dividedMillisForTimerDisplay(pomMillis)));

        pomDotsAdapter.setPomCycleRoundsAsStringsList(pomStringListOfRoundValues);
        pomDotsAdapter.updatePomDotCounter(pomDotCounter);

        setInitialTextSizeForTimers(pomMillis);
      }

      pomCyclesTextSizeHasChanged = false;

      roundDownPomCycleWorkTime();
      roundDownPomCycleRestTime();

      if (objectAnimatorPom != null) {
        objectAnimatorPom.cancel();
      }

      if (mHandler.hasCallbacks(endFadeForModeThree)) {
        mHandler.removeCallbacks(endFadeForModeThree);
      }

      if (savedPomCycleAdapter.isCycleActive() == true) {
        savedPomCycleAdapter.removeActiveCycleLayout();
        savedPomCycleAdapter.notifyDataSetChanged();
      }

      pomDotsAdapter.resetModeThreeAlpha();
      pomDotsAdapter.setModeThreeAlpha();
      pomDotsAdapter.notifyDataSetChanged();
    }
  }

  private String getCurrentDateAsSlashFormattedString() {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
    calendar = Calendar.getInstance(Locale.getDefault());
    return simpleDateFormat.format(calendar.getTime());
  }

  private String getCurrentDateAsFullTextString() {
    calendar = Calendar.getInstance(Locale.getDefault());
    return (String.valueOf(calendar.getTime()));
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private void toggleLayoutParamsForCyclesAndStopwatch() {
    if (mode != 4) {
      cycleTitleLayoutParams.topMargin = convertDensityPixelsToScalable(15);
      cyclesCompletedLayoutParams.topMargin = convertDensityPixelsToScalable(12);
    } else {
      cyclesCompletedLayoutParams.topMargin = 0;
      cycleTitleLayoutParams.topMargin = -25;
    }
  }

  private void toggleEditPopUpViewsForAddingActivity(boolean activityExists) {
    if (mode == 1) {
      if (activityExists) {
        String activity = (String) tdee_sub_category_spinner.getSelectedItem();
        addTDEEfirstMainTextView.setText(activity);
        removeTdeeActivityImageView.setVisibility(View.VISIBLE);
        cycleHasActivityAssigned = true;
      } else {
        addTDEEfirstMainTextView.setText(R.string.add_activity);
        removeTdeeActivityImageView.setVisibility(View.INVISIBLE);
        cycleHasActivityAssigned = false;
      }
    }
  }

  private void toggleViewsForTotalDailyAndCycleTimes(boolean trackingCycle) {
    if (!trackingCycle) {
      cycle_title_textView.setVisibility(View.VISIBLE);
      tracking_daily_stats_header_textView.setVisibility(View.INVISIBLE);
      cycles_completed_textView.setVisibility(View.VISIBLE);
      reset_total_cycle_times.setVisibility(View.VISIBLE);

      total_set_header.setVisibility(View.VISIBLE);
      total_set_time.setVisibility(View.VISIBLE);
      total_break_header.setVisibility(View.VISIBLE);
      total_break_time.setVisibility(View.VISIBLE);

      dailyTotalTimeTextViewHeader.setVisibility(View.INVISIBLE);
      dailyTotalTimeTextView.setVisibility(View.INVISIBLE);
      dailyTotalCaloriesTextViewHeader.setVisibility(View.INVISIBLE);
      dailyTotalCaloriesTextView.setVisibility(View.INVISIBLE);

      dailySingleActivityStringHeader.setVisibility(View.INVISIBLE);
      dailyTotalTimeForSingleActivityTextViewHeader.setVisibility(View.INVISIBLE);
      dailyTotalTimeForSingleActivityTextView.setVisibility(View.INVISIBLE);
      dailyTotalCaloriesForSingleActivityTextViewHeader.setVisibility(View.INVISIBLE);
      dailyTotalCaloriesForSingleActivityTextView.setVisibility(View.INVISIBLE);
    } else {
      cycle_title_textView.setVisibility(View.GONE);
      tracking_daily_stats_header_textView.setVisibility(View.VISIBLE);
      cycles_completed_textView.setVisibility(View.INVISIBLE);
      reset_total_cycle_times.setVisibility(View.INVISIBLE);

      total_set_header.setVisibility(View.INVISIBLE);
      total_set_time.setVisibility(View.INVISIBLE);
      total_break_header.setVisibility(View.INVISIBLE);
      total_break_time.setVisibility(View.INVISIBLE);

      dailyTotalTimeTextViewHeader.setVisibility(View.VISIBLE);
      dailyTotalTimeTextView.setVisibility(View.VISIBLE);
      dailyTotalCaloriesTextViewHeader.setVisibility(View.VISIBLE);
      dailyTotalCaloriesTextView.setVisibility(View.VISIBLE);

      dailySingleActivityStringHeader.setVisibility(View.VISIBLE);
      dailyTotalTimeForSingleActivityTextViewHeader.setVisibility(View.VISIBLE);
      dailyTotalTimeForSingleActivityTextView.setVisibility(View.VISIBLE);
      dailyTotalCaloriesForSingleActivityTextViewHeader.setVisibility(View.VISIBLE);
      dailyTotalCaloriesForSingleActivityTextView.setVisibility(View.VISIBLE);
    }
  }

  public String getTdeeActivityStringFromArrayPosition() {
    ArrayList<String[]> subCategoryArray = tDEEChosenActivitySpinnerValues.getSubCategoryListOfStringArrays();
    String[] subCategoryList = subCategoryArray.get(selectedTdeeCategoryPosition);
    return (String) subCategoryList[selectedTdeeSubCategoryPosition];
  }

  private void tdeeCategorySpinnerTouchActions() {
    selectedTdeeCategoryPosition = tdee_category_spinner.getSelectedItemPosition();

    tdeeSubCategoryAdapter.clear();
    tdeeSubCategoryAdapter.addAll(tDEEChosenActivitySpinnerValues.getSubCategoryListOfStringArrays().get(selectedTdeeCategoryPosition));

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

  private void setTdeeSpinnersToDefaultValues() {
    tdee_category_spinner.setSelection(cycles.getTdeeCatPosition());
    tdee_sub_category_spinner.setSelection(cycles.getTdeeSubCatPosition());
  }

  private void retrieveCycleActivityPositionAndMetScoreFromCycleList() {
    selectedTdeeCategoryPosition = cycles.getTdeeCatPosition();
    selectedTdeeSubCategoryPosition = cycles.getTdeeSubCatPosition();

    metScore = retrieveMetScoreFromSubCategoryPosition();
  }

  private void setMetScoreTextViewInAddTdeePopUp() {
    metScore = retrieveMetScoreFromSubCategoryPosition();
    metScoreTextView.setText(getString(R.string.met_score, String.valueOf(metScore)));
  }

  private double retrieveMetScoreFromSubCategoryPosition() {
    String[] valueArray = tDEEChosenActivitySpinnerValues.getSubValueListOfStringArrays().get(selectedTdeeCategoryPosition);
    double preRoundedMet = Double.parseDouble(valueArray[selectedTdeeSubCategoryPosition]);

    return preRoundedMet;
  }

  private void setThirdMainTextViewInAddTdeePopUp() {
    String caloriesBurnedPerMinute = formatCalorieString(calculateCaloriesBurnedPerMinute(metScore));
    String caloriesBurnedPerHour = formatCalorieString(calculateCaloriesBurnedPerMinute(metScore) * 60);

    caloriesBurnedInTdeeAdditionTextView.setText(getString(R.string.two_line_concat, getString(R.string.calories_burned_per_minute, caloriesBurnedPerMinute), getString(R.string.calories_burned_per_hour, caloriesBurnedPerHour)));
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

  private void removeAllTimerSharedPreferences() {
    prefEdit.remove("savedProgressBarValueForModeOne");
    prefEdit.remove("timeLeftValueForModeOne");
    prefEdit.remove("positionOfSelectedCycleForModeOne");
    prefEdit.remove("modeOneTimerPaused");
    prefEdit.remove("modeOneTimerEnded");
    prefEdit.remove("modeOneTimerDisabled");
    prefEdit.remove("savedProgressBarValueForModeThree");
    prefEdit.remove("timeLeftValueForModeThree");
    prefEdit.remove("positionOfSelectedCycleForModeThree");
    prefEdit.remove("modeThreeTimerPaused");
    prefEdit.remove("modeThreeTimerEnded");
    prefEdit.remove("modeThreeTimerDisabled");
    prefEdit.apply();
  }

  private void showToastIfNoneActive(String message) {
    if (mToast != null) {
      mToast.cancel();
    }
    mToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
    mToast.show();
  }

  private void toggleDayAndNightModesForMain(int themeMode) {
    GradientDrawable fabDrawable = (GradientDrawable) fab.getBackground();
    fabDrawable.mutate();
    GradientDrawable stopWatchDrawable = (GradientDrawable) stopWatchLaunchButton.getBackground();

    if (themeMode == DAY_MODE) {
      mainView.setBackgroundColor(Color.WHITE);
      savedCyclesTabLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.darker_grey));
      savedCyclesTabLayout.setSelectedTabIndicatorColor(Color.BLACK);

      fabDrawable.setStroke(3, Color.BLACK);
      stopWatchDrawable.setStroke(3, Color.BLACK);
    }
    if (themeMode == NIGHT_MODE) {
      mainView.setBackgroundColor(Color.BLACK);
      savedCyclesTabLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.darker_grey));
      savedCyclesTabLayout.setSelectedTabIndicatorColor(Color.WHITE);

      fabDrawable.setStroke(3, Color.WHITE);
      stopWatchDrawable.setStroke(3, Color.WHITE);
    }
  }

  private void toggleDayAndNightModesForTimer(int themeMode) {
    Drawable resetCyclesDrawableUnwrapped = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.reset_24);
    Drawable resetCyclesDrawableWrapped = DrawableCompat.wrap(resetCyclesDrawableUnwrapped);

    Drawable nextRoundDrawableUnwrapped = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.double_arrow_large);
    Drawable nextRoundDrawableWrapped = DrawableCompat.wrap(nextRoundDrawableUnwrapped);

    if (themeMode == DAY_MODE) {
      timerView.setBackgroundColor(getColor(R.color.white));
      cycles_completed_textView.setTextColor(getColor(R.color.black));
      total_set_header.setTextColor(getColor(R.color.black));
      total_set_time.setTextColor(getColor(R.color.black));
      total_break_header.setTextColor(getColor(R.color.black));
      total_break_time.setTextColor(getColor(R.color.black));

      //      tracking_daily_stats_header_textView.setTextColor(getColor(R.color.black));
      dailyTotalTimeTextViewHeader.setTextColor(getColor(R.color.black));
      dailyTotalCaloriesTextViewHeader.setTextColor(getColor(R.color.black));
//      dailySingleActivityStringHeader.setTextColor(getColor(R.color.black));
      dailyTotalTimeForSingleActivityTextViewHeader.setTextColor(getColor(R.color.black));
      dailyTotalCaloriesForSingleActivityTextViewHeader.setTextColor(getColor(R.color.black));

      timeLeft.setTextColor(getColor(R.color.black));

      DrawableCompat.setTint(resetCyclesDrawableWrapped, Color.BLACK);
      reset_total_cycle_times.setBackgroundColor(Color.WHITE);
      DrawableCompat.setTint(nextRoundDrawableWrapped, Color.BLACK);
      next_round.setBackgroundColor(Color.WHITE);
    }

    if (themeMode == NIGHT_MODE){
      timerView.setBackgroundColor(getColor(R.color.black));
      cycles_completed_textView.setTextColor(getColor(R.color.white));
      total_set_header.setTextColor(getColor(R.color.white));
      total_set_time.setTextColor(getColor(R.color.white));
      total_break_header.setTextColor(getColor(R.color.white));
      total_break_time.setTextColor(getColor(R.color.white));

//      tracking_daily_stats_header_textView.setTextColor(getColor(R.color.white));
      dailyTotalTimeTextViewHeader.setTextColor(getColor(R.color.white));
      dailyTotalCaloriesTextViewHeader.setTextColor(getColor(R.color.white));
//      dailySingleActivityStringHeader.setTextColor(getColor(R.color.white));
      dailyTotalTimeForSingleActivityTextViewHeader.setTextColor(getColor(R.color.white));
      dailyTotalCaloriesForSingleActivityTextViewHeader.setTextColor(getColor(R.color.white));

      timeLeft.setTextColor(getColor(R.color.white));

      DrawableCompat.setTint(resetCyclesDrawableWrapped, Color.WHITE);
      reset_total_cycle_times.setBackgroundColor(Color.BLACK);
      DrawableCompat.setTint(nextRoundDrawableWrapped, Color.WHITE);
      next_round.setBackgroundColor(Color.BLACK);
    }

    reset_total_cycle_times.setImageDrawable(resetCyclesDrawableWrapped);
    next_round.setImageDrawable(nextRoundDrawableWrapped);

    dotsAdapter.setDayOrNightMode(themeMode);
  }

  private void toggleDayAndNightModeForStatsFragment() {

  }
}