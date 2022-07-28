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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tragic.irate.simple.stopwatch.Adapters.CycleRoundsAdapter;
import com.example.tragic.irate.simple.stopwatch.Adapters.CycleRoundsAdapterTwo;
import com.example.tragic.irate.simple.stopwatch.Adapters.LapAdapter;
import com.example.tragic.irate.simple.stopwatch.Adapters.SavedCycleAdapter;
import com.example.tragic.irate.simple.stopwatch.Adapters.SavedPomCycleAdapter;
import com.example.tragic.irate.simple.stopwatch.Canvas.DotDraws;
import com.example.tragic.irate.simple.stopwatch.Canvas.LapListCanvas;
import com.example.tragic.irate.simple.stopwatch.Database.Cycles;
import com.example.tragic.irate.simple.stopwatch.Database.CyclesDatabase;
import com.example.tragic.irate.simple.stopwatch.Database.DailyStatClasses.DailyStatsAccess;
import com.example.tragic.irate.simple.stopwatch.Database.DailyStatClasses.DailyStatsFragment;
import com.example.tragic.irate.simple.stopwatch.Database.DailyStatClasses.DayHolder;
import com.example.tragic.irate.simple.stopwatch.Database.DailyStatClasses.StatsForEachActivity;
import com.example.tragic.irate.simple.stopwatch.Database.PomCycles;
import com.example.tragic.irate.simple.stopwatch.Miscellaneous.LongToStringConverters;
import com.example.tragic.irate.simple.stopwatch.Miscellaneous.ScreenRatioLayoutChanger;
import com.example.tragic.irate.simple.stopwatch.Miscellaneous.TDEEChosenActivitySpinnerValues;
import com.example.tragic.irate.simple.stopwatch.Miscellaneous.VerticalSpaceItemDecoration;
import com.example.tragic.irate.simple.stopwatch.SettingsFragments.ChangeSettingsValues;
import com.example.tragic.irate.simple.stopwatch.SettingsFragments.ColorSettingsFragment;
import com.example.tragic.irate.simple.stopwatch.SettingsFragments.RootSettingsFragment;
import com.example.tragic.irate.simple.stopwatch.SettingsFragments.SoundSettingsFragment;
import com.example.tragic.irate.simple.stopwatch.SettingsFragments.tdeeSettingsFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SavedCycleAdapter.onCycleClickListener, SavedCycleAdapter.onHighlightListener, SavedCycleAdapter.onTdeeModeToggle, SavedPomCycleAdapter.onCycleClickListener, SavedPomCycleAdapter.onHighlightListener, CycleRoundsAdapter.onFadeFinished, CycleRoundsAdapterTwo.onFadeFinished, CycleRoundsAdapter.onRoundSelected, CycleRoundsAdapterTwo.onRoundSelectedSecondAdapter, DotDraws.sendAlpha, SavedCycleAdapter.onResumeOrResetCycle, SavedPomCycleAdapter.onResumeOrResetCycle, RootSettingsFragment.onChangedSettings, SoundSettingsFragment.onChangedSoundSetting, ColorSettingsFragment.onChangedColorSetting {

  SharedPreferences sharedPreferences;
  SharedPreferences.Editor prefEdit;

  Menu onOptionsMenu;
  int mMenuType;
  int DEFAULT_MENU = 0;
  int DAILY_SETTINGS_MENU = 1;
  int SETTINGS_MENU = 2;

  DailyStatsAccess dailyStatsAccess;
  FragmentManager fragmentManager;
  TabLayout savedCyclesTabLayout;
  TabLayout.Tab savedCyclesTab;
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
  View sortStatsPopupView;
  View savedCyclePopupView;
  View editCyclesPopupView;
  View settingsPopupView;
  View soundsViewInSettings;
  View colorsViewInSettings;
  View aboutViewInSettings;

  PopupWindow sortPopupWindow;
  PopupWindow savedCyclePopupWindow;
  PopupWindow deleteCyclePopupWindow;
  PopupWindow editCyclesPopupWindow;
  PopupWindow settingsPopupWindow;

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

  TextView sortAlphaStart;
  TextView sortAlphaEnd;
  TextView sortRecent;
  TextView sortNotRecent;
  TextView sortHigh;
  TextView sortLow;

  TextView sortStatsAToZTextView;
  TextView sortStatsZToATextView;
  TextView sortStatsByMostTimeTextView;
  TextView sortStatsByLeastTimeTextView;
  TextView sortStatsByMostCaloriesTextView;
  TextView sortStatsByLeastCaloriesTextView;

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
  int sortModeForStats;
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
  String workoutString;
  String roundTypeString;
  String pomString;
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

  AlphaAnimation fadeIn;
  AlphaAnimation fadeOut;
  Animation slideLeft;
  Animation slideDailyStatsFragmentInFromLeft;
  Animation slideDailyStatsFragmentOutFromLeft;
  Animation slideGlobalSettingsFragmentInFromLeft;
  Animation slideGlobalSettingsFragmentOutFromLeft;

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
  Animation endAnimation;

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

  TextView dailySingleActivityStringHeader;
  TextView dailyTotalTimeTextViewHeader;
  TextView dailyTotalTimeTextView;
  TextView dailyTotalCaloriesTextViewHeader;
  TextView dailyTotalCaloriesTextView;

  TextView dailyTotalTimeForSinglefirstMainTextViewHeader;
  TextView dailyTotalTimeForSinglefirstMainTextView;
  TextView dailyTotalCaloriesForSinglefirstMainTextViewHeader;
  TextView dailyTotalCaloriesForSinglefirstMainTextView;

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
  long totalBreakTimeForCurrentDayInMillis;
  long totalWorkTimeForCurrentDayInMillis;
  long totalRestTimeForCurrentDayInMillis;
  double totalCaloriesBurnedForCurrentDay;

  long totalSetTimeForSpecificActivityForCurrentDayInMillis;
  long totalBreakTimeForSpecificActivityForCurrentDayInMillis;
  double totalCaloriesBurnedForSpecificActivityForCurrentDay;

  String timeLeftValueHolder;

  long pomMillis;

  int maxProgress = 10000;
  int currentProgressBarValue = 10000;
  long setMillisUntilFinished;
  long breakMillisUntilFinished;
  long pomMillisUntilFinished;
  Runnable endFade;
  int pomDotCounter;

  double stopWatchMs;
  double stopWatchSeconds;
  double stopWatchMinutes;
  String displayMs = "00";
  String displayTime = "0";
  String newEntries;
  String savedEntries;
  ArrayList<String> currentLapList;
  ArrayList<String> savedLapList;
  Runnable stopWatchRunnable;

  int cyclesCompleted;
  int lapsNumber;

  int startRounds;
  int numberOfRoundsLeft;
  int currentRound;

  boolean timerEnded;
  boolean timerDisabled;
  boolean timerIsPaused = true;
  boolean stopWatchIsPaused = true;

  LinearLayoutManager lapRecyclerLayoutManager;
  ConstraintLayout roundRecyclerLayout;

  DotDraws dotDraws;
  ValueAnimator sizeAnimator;
  ValueAnimator valueAnimatorDown;
  ValueAnimator valueAnimatorUp;
  AlphaAnimation fadeProgressIn;
  AlphaAnimation fadeProgressOut;

  VerticalSpaceItemDecoration verticalSpaceItemDecoration;
  boolean textSizeIncreased;

  int receivedAlpha;
  MaterialButton pauseResumeButton;

  public Runnable infinityTimerForSets;
  public Runnable infinityTimerForBreaks;

  long defaultProgressBarDurationForInfinityRounds;
  long countUpMillisHolder;
  boolean makeCycleAdapterVisible;
  boolean beginTimerForNextRound;
  boolean timerPopUpIsVisible;

  NotificationManagerCompat notificationManagerCompat;
  NotificationCompat.Builder builder;
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
  com.example.tragic.irate.simple.stopwatch.SettingsFragments.tdeeSettingsFragment tdeeSettingsFragment;

  ChangeSettingsValues changeSettingsValues;

  FrameLayout mainActivityFragmentFrameLayout;
  FragmentTransaction ft;

  long stopWatchstartTime;
  long stopWatchTotalTime;
  long stopWatchTotalTimeHolder;

  long stopWatchNewLapTime;
  long stopWatchNewLapHolder;

  ScreenRatioLayoutChanger screenRatioLayoutChanger;
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
  int SORTING_STATS = 1;

  boolean statsHaveBeenEdited;

  Toast mToast;

  //Todo: Deletions in stats frag not working. This is again due to multiple repeats of same activity, and likely a result of stuff being added through Cycles/Timer, not Fragment.
  //Todo: Adding 2:00:00 to stats frag shows as 2:00:07 in timer.
  //Todo: Sometimes save runnable runs when timer is not active.
  //Todo: White background tearing when launching stopwatch.
  //Todo: populateCycleAdapterArrayList() needs to call dotdraws.updateWorkoutTimes() even tho it should call before the lists are accessed/

  //Todo: Setting Tdee stuff should be clear/offer a prompt.
  //Todo: Green/Red for cal diff may want to reverse colors.
  //Todo: Longer total time/calorie values exceed width allowances - test w/ large numbers.
  //Todo: Add Day/Night modes.
  //Todo: Backup/export option for stats (if app is deleted).
 //Todo: Timer and Edit popUps have a lot of changes in /long that are not in /nonLong. Need to copy + paste + revamp.
  //Todo: Check sizes on long aspect for all layouts + menus.

  //Todo: Test dates from future years.
  //Todo: Consider a separate uniqueID for year in Daily + StatsForEach. Then we don't have to do this weird math stuff.
  //Todo: Test all notifications.
  //Todo: Add disclaimer about accuracy, for entertainment purposes, not medical advice, etc. Should pop up on app start.

  //Todo: Run code inspector for redundancies, etc.
  //Todo: Rename app, of course.
  //Todo: Test layouts w/ emulator.
  //Todo: Test everything 10x. Incl. round selection/replacement.

  //Todo: REMINDER, Try next app w/ Kotlin + learn Kotlin.

  @Override
  public void onResume() {
    super.onResume();
    setVisible(true);
    dismissNotification = true;
    notificationManagerCompat.cancel(1);
  }

  @Override
  public void onPause() {
    super.onPause();
  }

  @Override
  public void onStop() {
    super.onStop();
    setVisible(false);
    if (timerPopUpWindow.isShowing()) {
      dismissNotification = false;
      setNotificationValues();
    }
  }

  @Override
  public void onDestroy() {
    dismissNotification = true;
    notificationManagerCompat.cancel(1);
    AsyncTask.execute(globalSaveTotalTimesAndCaloriesInDatabaseRunnable);

    super.onDestroy();
  }

  @Override
  public void onBackPressed() {
    if (!timerPopUpIsVisible && mainActivityFragmentFrameLayout.getVisibility()==View.INVISIBLE) {
      return;
    }

    if (rootSettingsFragment.isVisible() || dailyStatsFragment.isVisible()) {

      if (rootSettingsFragment.isVisible()) {
        mainActivityFragmentFrameLayout.startAnimation(slideGlobalSettingsFragmentOutFromLeft);
      }

      if (dailyStatsFragment.isVisible()) {
        mainActivityFragmentFrameLayout.startAnimation(slideDailyStatsFragmentOutFromLeft);


        if (dailyStatsFragment.getHaveStatsBeenChangedBoolean()) {
          AsyncTask.execute(()-> {
            queryAllStatsEntitiesAndAssignTheirValuesToObjects();

            dailyStatsFragment.setHaveStatsBeenChangedBoolean(false);
            Log.i("testUpdate", "re-query!");
          });

        }
      }

      setTypeOFMenu(DEFAULT_MENU);
      toggleSortMenuViewBetweenCyclesAndStats(SORTING_CYCLES);
    }

    if (soundSettingsFragment.isVisible() || colorSettingsFragment.isVisible() || tdeeSettingsFragment.isVisible()) {
      getSupportFragmentManager().beginTransaction()
              .addToBackStack(null)
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
        if (mode==1 && workoutCyclesArray.size()==0 || mode==3 && pomArray.size()==0) {
          showToastIfNoneActive("No cycles to delete!");
        } else {
          delete_all_text.setText(R.string.delete_all_cycles);
          deleteCyclePopupWindow.showAtLocation(mainView, Gravity.CENTER, 0, 0);
        }
        break;
      case R.id.delete_single_day_from_daily_stats:
        delete_all_text.setText(R.string.delete_single_day_from_stats);
        deleteCyclePopupWindow.showAtLocation(mainView, Gravity.CENTER, 0, 0);
        break;
      case R.id.delete_all_days_from_daily_stats:
        delete_all_text.setText(R.string.delete_all_stats);
        deleteCyclePopupWindow.showAtLocation(mainView, Gravity.CENTER, 0, 0);
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    menu.clear();
    if (mMenuType==DEFAULT_MENU) {
      getMenuInflater().inflate(R.menu.main_options_menu, menu);
    }
    if (mMenuType==DAILY_SETTINGS_MENU) {
      getMenuInflater().inflate(R.menu.daily_stats_options_menu, menu);
    }
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public void settingsData(int settingNumber) {
    Fragment fragmentToReplace = new Fragment();
    if (settingNumber==1) fragmentToReplace = soundSettingsFragment;
    if (settingNumber==2) fragmentToReplace = colorSettingsFragment;
    if (settingNumber==3) fragmentToReplace = tdeeSettingsFragment;

    getSupportFragmentManager().beginTransaction()
            .addToBackStack (null)
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
    if (receivedMode==1) {
      savedCycleAdapter.changeColorSetting(typeOFRound, settingNumber);
      cycleRoundsAdapterTwo.changeColorSetting(typeOFRound, settingNumber);
      savedCycleAdapter.notifyDataSetChanged();
    }

    if (receivedMode==3) {
      savedPomCycleAdapter.changeColorSetting(typeOFRound, settingNumber);
      savedPomCycleAdapter.notifyDataSetChanged();
    }

    dotDraws.changeColorSetting(typeOFRound, settingNumber);
    assignColorSettingValues(typeOFRound, settingNumber);
  }


  @Override
  public void sendAlphaValue(int alpha) {
    receivedAlpha = alpha;
  }

  @Override
  public void ResumeOrResetCycle(int resumingOrResetting) {
    if (resumingOrResetting==RESUMING_CYCLE_FROM_ADAPTER) {
      resumeOrResetCycleFromAdapterList(RESUMING_CYCLE_FROM_ADAPTER);
    }
    else if (resumingOrResetting==RESETTING_CYCLE_FROM_ADAPTER){
      resumeOrResetCycleFromAdapterList(RESETTING_CYCLE_FROM_ADAPTER);
    }
  }

  public void setStatsHaveBeenEditedBoolean(boolean haveBeenEdited) {
    this.statsHaveBeenEdited = haveBeenEdited;
  }

  @Override
  public void toggleTdeeMode(int positionToToggle) {
    savedCycleAdapter.modifyActiveTdeeModeToggleList(positionToToggle);
    savedCycleAdapter.setPositionToToggle(positionToToggle);
    savedCycleAdapter.notifyDataSetChanged();
  }

  @Override
  public void onCycleClick(int position) {
    isNewCycle = false;
    positionOfSelectedCycle = position;
    cycleHasActivityAssigned = savedCycleAdapter.getBooleanDeterminingIfCycleHasActivity(position);
    trackActivityWithinCycle = savedCycleAdapter.retrieveActiveTdeeModeBoolean(position);

    setCyclesAndPomCyclesEntityInstanceToSelectedListPosition(position);

    if (cycleHasActivityAssigned) {
      retrieveCycleActivityPositionAndMetScoreFromCycleList();
    }

    populateCycleAdapterArrayList();

    if (mode==1) {
      savedCycleAdapter.removeActiveCycleLayout();
      launchTimerCycle(CYCLE_LAUNCHED_FROM_RECYCLER_VIEW);
    }
    if (mode==3) {
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
    if (edit_highlighted_cycle.getAlpha()!=1 && receivedHighlightPositions.size()==1) {
      edit_highlighted_cycle.setAlpha(1.0f);
      edit_highlighted_cycle.setEnabled(true);
    } else if (edit_highlighted_cycle.getAlpha()==1 && receivedHighlightPositions.size()!=1) {
      edit_highlighted_cycle.setAlpha(0.4f);
      edit_highlighted_cycle.setEnabled(false);
    }
  }

  @Override
  public void subtractionFadeHasFinished() {
    //When adapter fade on round has finished, execute method to remove the round from adapter list/holders and refresh the adapter display. If we click to remove another round before fade is done, fade gets cancelled, restarted on next position, and this method is also called to remove previous round.
    removeRound();
    if (consolidateRoundAdapterLists) {
      //Adapters only need adjusting if second one is populated.
      if (workoutTime.size()>=8){
        workoutStringListOfRoundValuesForFirstAdapter.clear();
        workoutIntegerListOfRoundTypeForFirstAdapter.clear();
        workoutStringListOfRoundValuesForSecondAdapter.clear();
        workoutIntegerListOfRoundTypeForSecondAdapter.clear();
        for (int i=0; i<workoutTime.size(); i++) {
          if (i<=7) {
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
    if (mode==3) {
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

  @SuppressLint({"UseCompatLoadingForDrawables", "ClickableViewAccessibility", "CommitPrefEdits", "CutPasteId"})
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    groupAllAppStartInstantiations();

    infinityTimerForSets = infinityRunnableForSets();
    infinityTimerForBreaks = infinityRunnableForBreaks();

    addTDEEfirstMainTextView.setOnClickListener(v-> {
      View testView = editCyclesPopupView.findViewById(R.id.bottom_edit_title_divider);

      addTdeePopUpWindow.showAsDropDown(testView);
    });

    confirmActivityAdditionValues.setOnClickListener(v-> {
      addTdeePopUpWindow.dismiss();
      toggleEditPopUpViewsForAddingActivity(true);
    });

    removeTdeeActivityImageView.setOnClickListener(v-> {
      toggleEditPopUpViewsForAddingActivity(false);
    });

    fab.setOnClickListener(v -> {
      fabLogic();
    });

    sortButton.setOnClickListener(v-> {
      sortPopupWindow.showAtLocation(mainView, Gravity.END|Gravity.TOP, 0, 0);
    });

    timerPopUpWindow.setOnDismissListener(() -> {
      timerPopUpDismissalLogic();

      activateResumeOrResetOptionForCycle();
      replaceCycleListWithEmptyTextViewIfNoCyclesExist();
      setViewsAndColorsToPreventTearingInEditPopUp(false);

      AsyncTask.execute(globalSaveTotalTimesAndCaloriesInDatabaseRunnable);

      logCyclesTitlesFromDatabase();
    });

    editCyclesPopupWindow.setOnDismissListener(() -> {
      editCyclesPopUpDismissalLogic();

      setViewsAndColorsToPreventTearingInEditPopUp(false);
      replaceCycleListWithEmptyTextViewIfNoCyclesExist();
    });

    edit_highlighted_cycle.setOnClickListener(v-> {
      setViewsAndColorsToPreventTearingInEditPopUp(true);
      fadeEditCycleButtonsInAndOut(FADE_IN_EDIT_CYCLE);
      removeHighlightFromCycle();
      editHighlightedCycleLogic();

      if (mode==1) cycles = cyclesList.get(positionOfSelectedCycle);
      if (mode==3) pomCycles = pomCyclesList.get(positionOfSelectedCycle);

//      clearAndRepopulateCycleAdapterListsFromDatabaseList(false);

      clearRoundAndCycleAdapterArrayLists();
      populateCycleAdapterArrayList();
      populateRoundAdapterArraysForHighlightedCycle();

      setRoundRecyclerViewsWhenChangingAdapterCount(workoutTime);
      assignOldCycleValuesToCheckForChanges();
    });

    //Turns off our cycle highlight mode from adapter.
    cancelHighlight.setOnClickListener(v-> {
      removeCycleHighlights();
      fadeEditCycleButtonsInAndOut(FADE_OUT_HIGHLIGHT_MODE);
    });

    delete_highlighted_cycle.setOnClickListener(v-> {
      deletingHighlightedCycleLogic();
    });

    //Selects all text when long clicking in editText.
    cycleNameEdit.setOnLongClickListener(v-> {
      cycleNameEdit.selectAll();
      return true;
    });

    firstRoundTypeHeaderInEditPopUp.setOnClickListener(v->{
      setEditPopUpTimerHeaders(1);
    });

    secondRoundTypeHeaderInEditPopUp.setOnClickListener(v-> {
      setEditPopUpTimerHeaders(2);
    });

    thirdRoundTypeHeaderInEditPopUp.setOnClickListener(v-> {
      setEditPopUpTimerHeaders(3);
    });

    View.OnClickListener numberPadListener = view -> {
      TextView textButton = (TextView) view;

      if (mode==1) {
        addToEditPopUpTimerArrays(editPopUpTimerArray, textButton);
      }

      if (mode==3) {
        if (editHeaderSelected==1) addToEditPopUpTimerArrays(savedEditPopUpArrayForFirstHeaderModeThree, textButton);
        if (editHeaderSelected==2) addToEditPopUpTimerArrays(savedEditPopUpArrayForSecondHeaderModeThree, textButton);
        if (editHeaderSelected==3) addToEditPopUpTimerArrays(savedEditPopUpArrayForThirdHeader, textButton);

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

    deleteEditPopUpTimerNumbers.setOnClickListener(v-> {
      if (mode==1) {
        if (editPopUpTimerArray.size()>0) {
          editPopUpTimerArray.remove(editPopUpTimerArray.size()-1);
        }
      }

      if (mode==3) {
        if (editHeaderSelected==1 && savedEditPopUpArrayForFirstHeaderModeThree.size()>0) {
          savedEditPopUpArrayForFirstHeaderModeThree.remove(savedEditPopUpArrayForFirstHeaderModeThree.size()-1);
        }
        if (editHeaderSelected==2 && savedEditPopUpArrayForSecondHeaderModeThree.size()>0) {
          savedEditPopUpArrayForSecondHeaderModeThree.remove(savedEditPopUpArrayForSecondHeaderModeThree.size()-1);
        }
        if (editHeaderSelected==3 && savedEditPopUpArrayForThirdHeader.size()>0){
          savedEditPopUpArrayForThirdHeader.remove(savedEditPopUpArrayForThirdHeader.size()-1);
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

    toggleInfinityRounds.setOnClickListener(v-> {
      if (toggleInfinityRounds.getAlpha()==1.0f) {
        toggleInfinityRounds.setAlpha(0.3f);
        if (editHeaderSelected==1) isSavedInfinityOptionActiveForSets = false;
        if (editHeaderSelected==2) isSavedInfinityOptionActiveForBreaks = false;
      } else {
        toggleInfinityRounds.setAlpha(1.0f);
        if (editHeaderSelected==1) isSavedInfinityOptionActiveForSets = true;
        if (editHeaderSelected==2) isSavedInfinityOptionActiveForBreaks = true;
      }
    });

    buttonToLaunchTimerFromEditPopUp.setOnClickListener(v-> {
      if (mode==1) {
        launchTimerCycle(CYCLES_LAUNCHED_FROM_EDIT_POPUP);
      }
      if (mode==3) {
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

    dotDraws.setMode(mode);
    dotDraws.onAlphaSend(MainActivity.this);
    progressBar.setProgress(maxProgress);

    endFade = new Runnable() {
      @Override
      public void run() {
        dotDraws.reDraw();
        if (receivedAlpha <= 105) {
          dotDraws.setAlpha(105);
          dotDraws.reDraw();
          mHandler.removeCallbacks(this);
        } else mHandler.postDelayed(this, 50);
      }
    };

    next_round.setOnClickListener(v -> {
      if (mode==1) {
        nextRound(true);
      }
      if (mode==3) {
        nextPomRound(true);
      }
    });

    reset.setOnClickListener(v -> {
      roundDownAllTotalTimeValuesToEnsureSyncing();
      AsyncTask.execute(globalSaveTotalTimesAndCaloriesInDatabaseRunnable);

      if (mode==1) {
        resetTimer();
      }
      if (mode==3) {
        reset.setText(R.string.confirm_cycle_reset);
      }

    });

    pauseResumeButton.setOnClickListener(v -> {
      if (!timerIsPaused) {
        if (mode==1) {
          pauseAndResumeTimer(PAUSING_TIMER);
        }
        if (mode==3) {
          pauseAndResumePomodoroTimer(PAUSING_TIMER);
        }
      } else {
        if (mode==1) {
          pauseAndResumeTimer(RESUMING_TIMER);
        }
        if (mode==3) {
          pauseAndResumePomodoroTimer(RESUMING_TIMER);
        }
      }
    });

    reset_total_cycle_times.setOnClickListener(v -> {
      delete_all_text.setText(R.string.delete_cycles_times_and_completed_cycles);
      deleteCyclePopupWindow.showAtLocation(mainView, Gravity.CENTER_HORIZONTAL, 0, -100);
    });

    delete_all_confirm.setOnClickListener(v-> {
      AsyncTask.execute(()-> {
        if (delete_all_text.getText().equals(getString(R.string.delete_all_cycles))) {
          deleteAllCycles();

        } else if (delete_all_text.getText().equals(getString(R.string.delete_cycles_times_and_completed_cycles))) {
          deleteTotalCycleTimes();

        } else if (delete_all_text.getText().equals(getString(R.string.delete_single_day_from_stats))) {
          deleteDailyStatsForSelectedDays();

        } else if (delete_all_text.getText().equals(getString(R.string.delete_all_stats))) {
          deleteDailyStatsForAllDays();
        }

        runOnUiThread(()-> {
          if (deleteCyclePopupWindow.isShowing()) deleteCyclePopupWindow.dismiss();
          Toast.makeText(getApplicationContext(), "Deleted!", Toast.LENGTH_SHORT).show();
        });
      });
    });

    delete_all_cancel.setOnClickListener(v -> {
      if (deleteCyclePopupWindow.isShowing()) deleteCyclePopupWindow.dismiss();
    });

    stopWatchLaunchButton.setOnClickListener(v-> {
      stopWatchLaunchLogic();
    });

    stopWatchPauseResumeButton.setOnClickListener(v-> {
      if (!stopWatchIsPaused) {
        pauseAndResumeStopwatch(PAUSING_TIMER);
      } else {
        pauseAndResumeStopwatch(RESUMING_TIMER);
        }
    });

    stopwatchReset.setOnClickListener(v-> {
      resetStopwatchTimer();
    });

    new_lap.setOnClickListener(v -> {
      newLapLogic();
    });

    stopWatchPopUpWindow.setOnDismissListener(()-> {
      getSupportActionBar().show();
      removeCycleHighlights();

      if (mode==1) {
        savedCycleRecycler.setVisibility(View.VISIBLE);
      }
      if (mode==3) {
        savedPomCycleRecycler.setVisibility(View.VISIBLE);
      }

      savedCyclesTabLayout.setVisibility(View.VISIBLE);
    });

    stopWatchRunnable = new Runnable() {
      @Override
      public void run() {
        setNotificationValues();

        DecimalFormat df2 = new DecimalFormat("00");

        stopWatchTotalTime = stopWatchTotalTimeHolder + (System.currentTimeMillis() - stopWatchstartTime);

        stopWatchSeconds = (int) (stopWatchTotalTime)/1000;
        stopWatchMinutes = (int) stopWatchSeconds/60;
        stopWatchMs = (stopWatchTotalTime%1000) / 10;

        displayTime = convertSeconds( (long)stopWatchSeconds);
        displayMs = df2.format(stopWatchMs);

        stopWatchTimeTextView.setText(displayTime);
        msTimeTextView.setText(displayMs);

        if (!textSizeIncreased && mode==4) {
          if (stopWatchSeconds > 59) {
            changeTextSizeWithAnimator(valueAnimatorDown, timeLeft);
            textSizeIncreased = true;
          }
        }

        mHandler.postDelayed(this, 10);
      }
    };
  }

  private void stopWatchLaunchLogic() {
    getSupportActionBar().hide();

    savedCycleRecycler.setVisibility(View.INVISIBLE);
    savedPomCycleRecycler.setVisibility(View.INVISIBLE);
    savedCyclesTabLayout.setVisibility(View.INVISIBLE);

    setInitialTextSizeForRounds(0);

    stopWatchTimeTextView.setText(displayTime);
    msTimeTextView.setText(displayMs);
    laps_completed_textView.setText(getString(R.string.laps_completed, lapsNumber));

    stopWatchPopUpWindow.showAtLocation(mainView, Gravity.NO_GRAVITY, 0, 0);
  }

  private void pauseAndResumeStopwatch (int pausing) {
    if (pausing == PAUSING_TIMER) {
      stopWatchIsPaused = true;

      stopWatchTotalTime = stopWatchTotalTime + (long) stopWatchSeconds;
      stopWatchTotalTimeHolder = stopWatchTotalTime;

      stopwatchReset.setVisibility(View.VISIBLE);

      new_lap.setAlpha(0.3f);
      new_lap.setEnabled(false);

      mHandler.removeCallbacks(stopWatchRunnable);

    } else if (pausing == RESUMING_TIMER) {
      stopWatchIsPaused = false;
      stopWatchstartTime = System.currentTimeMillis();

      new_lap.setAlpha(1.0f);
      new_lap.setEnabled(true);

      stopwatchReset.setVisibility(View.INVISIBLE);

      mHandler.post(stopWatchRunnable);
    }
  }

  private void newLapLogic() {
    if (lapAdapter.getItemCount()>98) {
      return;
    }

    if (empty_laps.getVisibility()==View.VISIBLE) {
      empty_laps.setVisibility(View.INVISIBLE);
    }

    savedEntries = String.format(Locale.getDefault(), "%02d:%02d:%02d", (int) stopWatchMinutes, (int) stopWatchSeconds, (int) stopWatchMs);

    if (savedLapList.size()>0) {
      String retrievedLap = savedLapList.get(savedLapList.size()-1);
      String[] splitLap = retrievedLap.split(":");
      int pulledMinute = Integer.parseInt(splitLap[0]) / 60;
      int convertedMinute = pulledMinute * 60 * 1000;
      int convertedSecond = Integer.parseInt(splitLap[1]) * 1000;
      int convertedMs = Integer.parseInt(splitLap[2]) * 10;

      int totalMs = convertedMinute + convertedSecond + convertedMs;

      int totalNewTime = ( (int) stopWatchTotalTime - totalMs);

      int newMinutes = (totalNewTime/1000) / 60;
      int newSeconds = (totalNewTime/1000) % 60;
      double newMS = ((double) totalNewTime%1000) / 10;

      newEntries = String.format(Locale.getDefault(), "%02d:%02d:%02d", (int) newMinutes, (int) newSeconds, (int) newMS);
    } else {
      newEntries = savedEntries;
    }

    currentLapList.add(newEntries);
    savedLapList.add(savedEntries);
    lapRecyclerLayoutManager.scrollToPosition(savedLapList.size() - 1);

    lapAdapter.notifyDataSetChanged();

    lapsNumber++;
    lapAdapter.resetLapAnimation();

    laps_completed_textView.setText(getString(R.string.laps_completed, lapsNumber));
  }

  private void resetStopwatchTimer() {
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
    empty_laps.setVisibility(View.VISIBLE);
    setInitialTextSizeForRounds(0);

    setNotificationValues();
  }

  private void toggleSortMenuViewBetweenCyclesAndStats(int typeOfSort) {
    if (typeOfSort==SORTING_CYCLES) {
      sortPopupWindow.setContentView(sortCyclePopupView);
    }
    if (typeOfSort==SORTING_STATS) {
      sortPopupWindow.setContentView(sortStatsPopupView);
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
    setVerticalSpaceDecorationForCycleRecyclerViewBasedOnAspectRatio();
    instantiateLapAdapterAndSetRecyclerOnIt();

    setDefaultLayoutTexts();
    retrieveAndImplementCycleSorting();

    setDefaultTimerValuesAndTheirEditTextViews();
    setDefaultLayoutVisibilities();
    instantiateAnimationAndColorMethods();
    sendPhoneResolutionToDotDrawsClass();

    instantiateNotifications();

    setVisualModificationsOnObjects();

    instantiateTdeeSpinnersAndSetThemOnAdapters();
    setTdeeSpinnerListeners();
    instantiateSaveTotalTimesAndCaloriesInDatabaseRunnable();
    instantiateSaveTotalTimesOnPostDelayRunnableInASyncThread();

    setAllSortTextViewsOntoClickListeners();
  }

  private void instantiateGlobalClasses() {
    fragmentManager = getSupportFragmentManager();
    screenRatioLayoutChanger = new ScreenRatioLayoutChanger(getApplicationContext());
    changeSettingsValues = new ChangeSettingsValues();
    tDEEChosenActivitySpinnerValues = new TDEEChosenActivitySpinnerValues(getApplicationContext());
    dailyStatsAccess = new DailyStatsAccess(getApplicationContext());
    longToStringConverters = new LongToStringConverters();

    mHandler = new Handler();
    mSavingCycleHandler = new Handler();
    inputMethodManager =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    sharedPreferences = getApplicationContext().getSharedPreferences("pref", 0);
    prefEdit = sharedPreferences.edit();

    cycles = new Cycles();
    pomCycles = new PomCycles();

    objectAnimator = ObjectAnimator.ofInt(progressBar, "progress",  maxProgress, 0);
    objectAnimatorPom = ObjectAnimator.ofInt(progressBar, "progress", maxProgress, 0);
    Log.i("testProg", "prog bar value on activity launch is " + objectAnimator.getAnimatedValue());


    ringToneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    mediaPlayer = MediaPlayer.create(this, ringToneUri);
    vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

    mToast = new Toast(getApplicationContext());
  }

  private void instantiateTabLayouts() {
    savedCyclesTabLayout = findViewById(R.id.savedCyclesTabLayout);
    savedCyclesTabLayout.addTab(savedCyclesTabLayout.newTab().setText("Workouts"));
    savedCyclesTabLayout.addTab(savedCyclesTabLayout.newTab().setText("Pomodoro"));
  }

  private void instantiateTabSelectionListeners() {
    savedCyclesTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
      @Override
      public void onTabSelected(TabLayout.Tab tab) {
        savedCyclesTab = tab;
        switch (savedCyclesTab.getPosition()) {
          case 0:
            mode = 1;
            cycleRoundsAdapter.setMode(1);
            dotDraws.setMode(1);
            break;
          case 1:
            mode = 3;
            cycleRoundsAdapter.setMode(3);
            dotDraws.setMode(3);
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

        if (savedCyclesTab.getPosition()==0) {
          if (savedCycleAdapter.isCycleHighlighted()==true) {
            removeCycleHighlights();
            savedCycleAdapter.notifyDataSetChanged();
          }
        }
        if (savedCyclesTab.getPosition()==1) {
          if (savedPomCycleAdapter.isCycleHighlighted()==true) {
            removeCycleHighlights();
            savedPomCycleAdapter.notifyDataSetChanged();
          }
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
    getSupportActionBar().setCustomView(R.layout.custom_bar);

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

    rootSettingsFragment.sendSettingsData(MainActivity.this);
    soundSettingsFragment.soundSetting(MainActivity.this);
    colorSettingsFragment.colorSetting(MainActivity.this);

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
    dotDraws = timerPopUpView.findViewById(R.id.dotdraws);
    reset = timerPopUpView.findViewById(R.id.reset);
    tracking_daily_stats_header_textView = timerPopUpView.findViewById(R.id.tracking_daily_stats_header_textView);
    cycle_title_textView = timerPopUpView.findViewById(R.id.cycle_title_textView);

    cycles_completed_textView = timerPopUpView.findViewById(R.id.cycles_completed_textView);

    total_set_header = timerPopUpView.findViewById(R.id.total_set_header);
    total_break_header = timerPopUpView.findViewById(R.id.total_break_header);
    total_set_time = timerPopUpView.findViewById(R.id.total_set_time);
    total_break_time = timerPopUpView.findViewById(R.id.total_break_time);

    dailySingleActivityStringHeader = timerPopUpView.findViewById(R.id.daily_single_activity_string_header);;
    dailyTotalTimeTextViewHeader = timerPopUpView.findViewById(R.id.daily_total_time_textView_header);
    dailyTotalTimeTextView = timerPopUpView.findViewById(R.id.daily_total_time_textView);
    dailyTotalCaloriesTextViewHeader = timerPopUpView.findViewById(R.id.daily_total_calories_textView_header);
    dailyTotalCaloriesTextView = timerPopUpView.findViewById(R.id.daily_total_calories_textView);

    dailyTotalTimeTextViewHeader.setText(R.string.total_daily_time);
    dailyTotalCaloriesTextViewHeader.setText(R.string.total_daily_calories);

    dailyTotalTimeForSinglefirstMainTextViewHeader = timerPopUpView.findViewById(R.id.daily_total_time_for_single_activity_textView_header);
    dailyTotalTimeForSinglefirstMainTextView = timerPopUpView.findViewById(R.id.daily_total_time_for_single_activity_textView);
    dailyTotalCaloriesForSinglefirstMainTextViewHeader = timerPopUpView.findViewById(R.id.daily_total_calories_for_single_activity_textView_header);
    dailyTotalCaloriesForSinglefirstMainTextView = timerPopUpView.findViewById(R.id.daily_total_calories_for_single_activity_textView);

    dailyTotalTimeForSinglefirstMainTextViewHeader.setText(R.string.total_daily_time_for_single_activity);
    dailyTotalCaloriesForSinglefirstMainTextViewHeader.setText(R.string.total_daily_calories_for_single_activity);

    laps_completed_textView = stopWatchPopUpView.findViewById(R.id.laps_completed_textView);

    lapListCanvas = stopWatchPopUpView.findViewById(R.id.lapCanvas);

    lapRecycler = stopWatchPopUpView.findViewById(R.id.lap_recycler);
    stopWatchPauseResumeButton = stopWatchPopUpView.findViewById(R.id.stopwatchPauseResumeButton);
    stopWatchTimeTextView = stopWatchPopUpView.findViewById(R.id.stopWatchTimeTextView);
    stopWatchTimeTextView.setTextSize(120f);
    new_lap =  stopWatchPopUpView.findViewById(R.id.new_lap);
    msTimeTextView = stopWatchPopUpView.findViewById(R.id.msTimeTextView);
    empty_laps = stopWatchPopUpView.findViewById(R.id.empty_laps_text);
    empty_laps.setText(R.string.empty_laps_list);
    stopwatchReset = stopWatchPopUpView.findViewById(R.id.stopwatch_reset);

    progressBar = timerPopUpView.findViewById(R.id.progressBar);
    timeLeft = timerPopUpView.findViewById(R.id.timeLeft);
    reset_total_cycle_times = timerPopUpView.findViewById(R.id.reset_total_cycle_times);
    pauseResumeButton = timerPopUpView.findViewById(R.id.pauseResumeButton);
    next_round = timerPopUpView.findViewById(R.id.next_round);

  }

  private void assignSortPopUpLayoutClassesToTheirIds() {
    sortAlphaStart = sortCyclePopupView.findViewById(R.id.sort_title_start);
    sortAlphaEnd = sortCyclePopupView.findViewById(R.id.sort_title_end);
    sortRecent = sortCyclePopupView.findViewById(R.id.sort_most_recent);
    sortNotRecent = sortCyclePopupView.findViewById(R.id.sort_least_recent);
    sortHigh = sortCyclePopupView.findViewById(R.id.sort_number_high);
    sortLow = sortCyclePopupView.findViewById(R.id.sort_number_low);

    sortStatsAToZTextView = sortStatsPopupView.findViewById(R.id.sort_activity_name_start);
    sortStatsZToATextView = sortStatsPopupView.findViewById(R.id.sort_activity_name_end);
    sortStatsByMostTimeTextView = sortStatsPopupView.findViewById(R.id.sort_most_time);
    sortStatsByLeastTimeTextView = sortStatsPopupView.findViewById(R.id.sort_least_time);
    sortStatsByMostCaloriesTextView = sortStatsPopupView.findViewById(R.id.sort_calories_most);
    sortStatsByLeastCaloriesTextView = sortStatsPopupView.findViewById(R.id.sort_calories_least);
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
  }

  private void setRoundRecyclersOnAdaptersAndLayoutManagers() {
    roundRecycler.setLayoutManager(roundRecyclerOneLayoutManager);
    roundRecycler.setAdapter(cycleRoundsAdapter);
    roundRecyclerTwo.setAdapter(cycleRoundsAdapterTwo);
    roundRecyclerTwo.setLayoutManager(roundRecyclerTwoLayoutManager);
  }

  private void setVerticalSpaceDecorationForCycleRecyclerViewBasedOnAspectRatio() {
    if (screenRatioLayoutChanger.setScreenRatioBasedLayoutChanges()>=1.8) {
      verticalSpaceItemDecoration = new VerticalSpaceItemDecoration(
              -10);
    } else {
      verticalSpaceItemDecoration = new VerticalSpaceItemDecoration(
              -25);
    }
    roundRecycler.addItemDecoration(verticalSpaceItemDecoration);
    roundRecyclerTwo.addItemDecoration(verticalSpaceItemDecoration);
  }

  private void instantiateLapAdapterAndSetRecyclerOnIt() {
    lapAdapter = new LapAdapter(getApplicationContext(), currentLapList, savedLapList);
    lapRecycler.setAdapter(lapAdapter);
    lapRecycler.setLayoutManager(lapRecyclerLayoutManager);
  }

  private void instantiateAnimationAndColorMethods() {
    fadeIn = new AlphaAnimation(0.0f, 1.0f);
    fadeIn.setDuration(750);
    fadeIn.setFillAfter(true);

    fadeOut = new AlphaAnimation(1.0f, 0.0f);
    fadeOut.setDuration(750);
    fadeOut.setFillAfter(true);

    slideLeft =  AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_in_left);
    slideLeft.setDuration(400);

    slideDailyStatsFragmentInFromLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_from_left);
    slideDailyStatsFragmentInFromLeft.setDuration(600);

    slideDailyStatsFragmentOutFromLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_from_left);
    slideDailyStatsFragmentOutFromLeft.setDuration(300);

    slideGlobalSettingsFragmentInFromLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_from_left);
    slideGlobalSettingsFragmentInFromLeft.setDuration(300);

    slideGlobalSettingsFragmentOutFromLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_from_left);
    slideGlobalSettingsFragmentOutFromLeft.setDuration(300);

    slideGlobalSettingsFragmentOutFromLeft.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {
      }

      @Override
      public void onAnimationEnd(Animation animation) {
        getSupportFragmentManager().beginTransaction()
                .remove(rootSettingsFragment)
                .commit();
        mainActivityFragmentFrameLayout.setVisibility(View.INVISIBLE);
      }

      @Override
      public void onAnimationRepeat(Animation animation) {
      }
    });

    slideDailyStatsFragmentOutFromLeft.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {
      }

      @Override
      public void onAnimationEnd(Animation animation) {
        getSupportFragmentManager().beginTransaction()
                .remove(dailyStatsFragment)
                .commit();
        mainActivityFragmentFrameLayout.setVisibility(View.INVISIBLE);
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
    sortCyclePopupView = inflater.inflate(R.layout.cycles_sort_popup, null);
    sortStatsPopupView = inflater.inflate(R.layout.stats_sort_popup, null);
    editCyclesPopupView = inflater.inflate(R.layout.editing_cycles, null);
    addTDEEPopUpView = inflater.inflate(R.layout.daily_stats_add_popup_for_main_activity, null);

    timerPopUpView = inflater.inflate(R.layout.timer_popup, null);
    stopWatchPopUpView = inflater.inflate(R.layout.stopwatch_popup, null);

    savedCyclePopupWindow = new PopupWindow(savedCyclePopupView, convertDensityPixelsToScalable(250), convertDensityPixelsToScalable(450), true);
    deleteCyclePopupWindow = new PopupWindow(deleteCyclePopupView, convertDensityPixelsToScalable(275), convertDensityPixelsToScalable(150), true);
    sortPopupWindow = new PopupWindow(sortCyclePopupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
    editCyclesPopupWindow = new PopupWindow(editCyclesPopupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true);
    settingsPopupWindow = new PopupWindow(settingsPopupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true);
    addTdeePopUpWindow = new PopupWindow(addTDEEPopUpView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);

    timerPopUpWindow = new PopupWindow(timerPopUpView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true);
    stopWatchPopUpWindow = new PopupWindow(stopWatchPopUpView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true);

    savedCyclePopupWindow.setAnimationStyle(R.style.WindowAnimation);
    deleteCyclePopupWindow.setAnimationStyle(R.style.WindowAnimation);
    sortPopupWindow.setAnimationStyle(R.style.SlideTopAnimationWithoutAnimatedExit);
    editCyclesPopupWindow.setAnimationStyle(R.style.WindowAnimation);
    settingsPopupWindow.setAnimationStyle(R.style.WindowAnimation);
    timerPopUpWindow.setAnimationStyle(R.style.WindowAnimation);
    stopWatchPopUpWindow.setAnimationStyle(R.style.WindowAnimation);
    addTdeePopUpWindow.setAnimationStyle(R.style.WindowAnimation);
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
    progressBarLayoutParams = (ConstraintLayout.LayoutParams) progressBar.getLayoutParams();
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
    savedPomCycleRecycler.setVisibility(View.GONE);
    new_lap.setAlpha(0.3f);
    roundListDivider.setVisibility(View.GONE);
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

  private void setVisualModificationsOnObjects() {
    pauseResumeButton.setBackgroundColor(Color.argb(0, 0, 0, 0));
    pauseResumeButton.setRippleColor(null);

    savedCycleRecycler.startAnimation(slideLeft);
  }

  private void instantiateTdeeSpinnersAndSetThemOnAdapters() {
    tdeeCategoryAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.tdee_category_spinner_layout, tDEEChosenActivitySpinnerValues.category_list);
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
        if (mode==1){
          cycles.setTotalSetTime(totalCycleSetTimeInMillis);
          cycles.setTotalBreakTime(totalCycleBreakTimeInMillis);
          cycles.setCyclesCompleted(cyclesCompleted);
          cyclesDatabase.cyclesDao().updateCycles(cycles);
        }

        if (mode==3) {
          pomCycles.setTotalWorkTime(totalCycleWorkTimeInMillis);
          pomCycles.setTotalRestTime(totalCycleRestTimeInMillis);
          pomCycles.setCyclesCompleted(cyclesCompleted);
          cyclesDatabase.cyclesDao().updatePomCycles(pomCycles);
        }

        createNewListOfActivitiesIfDayHasChanged();

        if (trackActivityWithinCycle) {
          setAndUpdateDayHolderValuesInDatabase();
          setAndUpdateStatsForEachActivityValuesInDatabase();
        }

        if (!timerIsPaused) {
          mHandler.postDelayed(globalSaveTotalTimesOnPostDelayRunnableInASyncThread, 2000);
        } else {
          mHandler.removeCallbacks(globalSaveTotalTimesOnPostDelayRunnableInASyncThread);
        }
      }
    };
  };

  private void createNewListOfActivitiesIfDayHasChanged() {
    Calendar calendar = Calendar.getInstance(Locale.getDefault());
    dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

    if ((dailyStatsAccess.getOldDayHolderId() != dayOfYear)) {
      dailyStatsAccess.setOldDayHolderId(dayOfYear);

      //Inserting new row into database for new day. Update methods below pull that from current day and update.
      dailyStatsAccess.insertTotalTimesAndCaloriesBurnedForSpecificDayWithZeroedOutTimesAndCalories(dayOfYear);

      dailyStatsAccess.setIsActivityCustomBoolean(false);
      dailyStatsAccess.insertTotalTimesAndCaloriesForEachActivityWithinASpecificDayWithZeroedOutTimesAndCalories(dayOfYear);
    }
  }

  private void setAndUpdateDayHolderValuesInDatabase() {
    dailyStatsAccess.assignDayHolderInstanceForSelectedDay(dayOfYear);
    dailyStatsAccess.updateTotalTimesAndCaloriesForSelectedDay(totalSetTimeForCurrentDayInMillis,totalCaloriesBurnedForCurrentDay);
  }

  private void setAndUpdateStatsForEachActivityValuesInDatabase() {
    int currentActivityPosition = dailyStatsAccess.getActivityPosition();
    int oldActivityPosition = dailyStatsAccess.getOldActivityPosition();

    if (currentActivityPosition != oldActivityPosition) {
      dailyStatsAccess.setOldActivityPositionInListForCurrentDay(currentActivityPosition);
    }

    dailyStatsAccess.setStatForEachActivityListForForSingleDayFromDatabase(dayOfYear);
    dailyStatsAccess.setDoesActivityExistsForSpecificDayBoolean();
    dailyStatsAccess.assignStatForEachActivityInstanceForSpecificActivityWithinSelectedDay();

    dailyStatsAccess.updateTotalTimesAndCaloriesForEachActivityForSelectedDay(totalSetTimeForSpecificActivityForCurrentDayInMillis, totalCaloriesBurnedForSpecificActivityForCurrentDay);

    Log.i("testActivity", "time being saved via Main is " + totalSetTimeForSpecificActivityForCurrentDayInMillis);
  }

  private void instantiateSaveTotalTimesOnPostDelayRunnableInASyncThread() {
    globalSaveTotalTimesOnPostDelayRunnableInASyncThread = new Runnable() {
      @Override
      public void run() {
        AsyncTask.execute(globalSaveTotalTimesAndCaloriesInDatabaseRunnable);
      }
    };
  }

  private void fabLogic() {
    cycleNameEdit.getText().clear();
    isNewCycle = true;

    clearRoundAndCycleAdapterArrayLists();
    setViewsAndColorsToPreventTearingInEditPopUp(true);

    assignOldCycleValuesToCheckForChanges();
    resetEditPopUpTimerHeaders();
    editPopUpTimerArray.clear();
    timerValueInEditPopUpTextView.setText("00:00");

    setTdeeSpinnersToDefaultValues();
    toggleEditPopUpViewsForAddingActivity(false);
    editCyclesPopupWindow.showAtLocation(mainView, Gravity.NO_GRAVITY, 0, 0);
  }

  private View.OnClickListener cyclesSortOptionListener() {
    return view -> {
      TextView textButton = (TextView) view;

      if (textButton.getText().toString().equals("Added: Most Recent")) sortHolder = 1;
      if (textButton.getText().toString().equals("Added: Least Recent")) sortHolder = 2;
      if (textButton.getText().toString().equals("Title: A - Z")) sortHolder = 3;
      if (textButton.getText().toString().equals("Title: Z - A")) sortHolder = 4;
      if (textButton.getText().toString().equals("Round Count: Most")) sortHolder = 5;
      if (textButton.getText().toString().equals("Round Count: Least")) sortHolder = 6;

      if (mode==1) sortMode = sortHolder; else if (mode==3) sortModePom = sortHolder;

      unHighlightSortTextViews();
      highlightSortTextView();

      AsyncTask.execute(()-> {
        queryAndSortAllCyclesFromDatabase();
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
        sortRecent.setBackgroundColor(colorToHighlight); break;
      case 2:
        sortNotRecent.setBackgroundColor(colorToHighlight); break;
      case 3:
        sortAlphaStart.setBackgroundColor(colorToHighlight); break;
      case 4:
        sortAlphaEnd.setBackgroundColor(colorToHighlight); break;
      case 5:
        sortHigh.setBackgroundColor(colorToHighlight); break;
      case 6:
        sortLow.setBackgroundColor(colorToHighlight); break;
    }
  }

  private void unHighlightSortTextViews() {
    int noHighlight = Color.TRANSPARENT;
    sortAlphaStart.setBackgroundColor(noHighlight);
    sortAlphaEnd.setBackgroundColor(noHighlight);
    sortRecent.setBackgroundColor(noHighlight);
    sortNotRecent.setBackgroundColor(noHighlight);
    sortHigh.setBackgroundColor(noHighlight);
    sortLow.setBackgroundColor(noHighlight);
  }

  private void queryAndSortAllCyclesFromDatabase() {
    if (mode==1) {
      switch (sortMode) {
        case 1: cyclesList = cyclesDatabase.cyclesDao().loadCyclesMostRecent(); break;
        case 2: cyclesList = cyclesDatabase.cyclesDao().loadCycleLeastRecent(); break;
        case 3: cyclesList = cyclesDatabase.cyclesDao().loadCyclesAlphaStart(); break;
        case 4: cyclesList = cyclesDatabase.cyclesDao().loadCyclesAlphaEnd(); break;
        case 5: cyclesList = cyclesDatabase.cyclesDao().loadCyclesMostItems(); break;
        case 6: cyclesList = cyclesDatabase.cyclesDao().loadCyclesLeastItems(); break;
      }
      runOnUiThread(()->{
        clearAndRepopulateCycleAdapterListsFromDatabaseList(false);
      });
    }

    if (mode==3) {
      switch (sortModePom) {
        case 1: pomCyclesList = cyclesDatabase.cyclesDao().loadPomCyclesMostRecent(); break;
        case 2: pomCyclesList = cyclesDatabase.cyclesDao().loadPomCyclesLeastRecent(); break;
        case 3: pomCyclesList = cyclesDatabase.cyclesDao().loadPomAlphaStart(); break;
        case 4: pomCyclesList = cyclesDatabase.cyclesDao().loadPomAlphaEnd(); break;
      }

      runOnUiThread(()->{
        clearAndRepopulateCycleAdapterListsFromDatabaseList(false);
      });
    }
  }

  private void clearAndRepopulateCycleAdapterListsFromDatabaseList(boolean forAllModes) {
    if (mode==1 || forAllModes) {
      workoutCyclesArray.clear();
      typeOfRoundArray.clear();
      workoutTitleArray.clear();
      workoutActivityStringArray.clear();
      tdeeActivityExistsInCycleList.clear();
      tdeeIsBeingTrackedInCycleList.clear();

      for (int i=0; i<cyclesList.size(); i++) {
        workoutTitleArray.add(cyclesList.get(i).getTitle());

        workoutCyclesArray.add(cyclesList.get(i).getWorkoutRounds());
        typeOfRoundArray.add(cyclesList.get(i).getRoundType());
        workoutActivityStringArray.add(cyclesList.get(i).getActivityString());
        tdeeActivityExistsInCycleList.add(cyclesList.get(i).getTdeeActivityExists());
        tdeeIsBeingTrackedInCycleList.add(cyclesList.get(i).getCurrentlyTrackingCycle());

        Log.i("testTitle", "title array for cycle list is " + workoutTitleArray.get(i));
//        Log.i("testToggle", "state saved retrieved for cycle list is " + tdeeIsBeingTrackedInCycleList.get(i));
      }

      savedCycleAdapter.notifyDataSetChanged();
    }
    if (mode==3 || forAllModes) {
      pomArray.clear();
      pomTitleArray.clear();
      for (int i=0; i<pomCyclesList.size(); i++) {
        pomArray.add(pomCyclesList.get(i).getFullCycle());
        pomTitleArray.add(pomCyclesList.get(i).getTitle());
      }
      savedPomCycleAdapter.notifyDataSetChanged();
    }
  }

  private View.OnClickListener statsSortOptionListener() {
    return view -> {
      TextView textView = (TextView) view;

      unHighlightAllSortTextViewsForStats();

      if (textView.getText().toString().equals("Activity Name: A - Z")) {
        sortModeForStats = 1;
        highlightSelectedSortTextViewForStats(sortStatsAToZTextView);
      }
      if (textView.getText().toString().equals("Activity Name: Z - A")) {
        sortModeForStats = 2;
        highlightSelectedSortTextViewForStats(sortStatsZToATextView);
      }
      if (textView.getText().toString().equals("Time: Most")) {
        sortModeForStats = 3;
        highlightSelectedSortTextViewForStats(sortStatsByMostTimeTextView);
      }
      if (textView.getText().toString().equals("Time: Least")) {
        sortModeForStats = 4;
        highlightSelectedSortTextViewForStats(sortStatsByLeastTimeTextView);
      }
      if (textView.getText().toString().equals("Calories: Most")) {
        sortModeForStats = 5;
        highlightSelectedSortTextViewForStats(sortStatsByMostCaloriesTextView);
      }
      if (textView.getText().toString().equals("Calories: Least")) {
        sortModeForStats = 6;
        highlightSelectedSortTextViewForStats(sortStatsByLeastCaloriesTextView);
      }

      AsyncTask.execute(()-> {
        dailyStatsFragment.setActivitySortMode(sortModeForStats);
        dailyStatsFragment.sortStatsAsACallFromMainActivity();
        runOnUiThread(()-> {
          sortPopupWindow.dismiss();
        });
      });
    };
  }

  private void highlightSelectedSortTextViewForStats(TextView textView) {
    int colorToHighlight = getResources().getColor(R.color.test_highlight);
    textView.setBackgroundColor(colorToHighlight);
  }

  private void unHighlightAllSortTextViewsForStats() {
    int noHighlight = Color.TRANSPARENT;
    sortStatsAToZTextView.setBackgroundColor(noHighlight);
    sortStatsZToATextView.setBackgroundColor(noHighlight);
    sortStatsByMostTimeTextView.setBackgroundColor(noHighlight);
    sortStatsByLeastTimeTextView.setBackgroundColor(noHighlight);
    sortStatsByMostCaloriesTextView.setBackgroundColor(noHighlight);
    sortStatsByLeastCaloriesTextView.setBackgroundColor(noHighlight);
  }

  private void setAllSortTextViewsOntoClickListeners() {
    sortRecent.setOnClickListener(cyclesSortOptionListener());
    sortNotRecent.setOnClickListener(cyclesSortOptionListener());
    sortAlphaStart.setOnClickListener(cyclesSortOptionListener());
    sortAlphaEnd.setOnClickListener(cyclesSortOptionListener());
    sortHigh.setOnClickListener(cyclesSortOptionListener());
    sortLow.setOnClickListener(cyclesSortOptionListener());

    sortStatsAToZTextView.setOnClickListener(statsSortOptionListener());
    sortStatsZToATextView.setOnClickListener(statsSortOptionListener());
    sortStatsByMostTimeTextView.setOnClickListener(statsSortOptionListener());
    sortStatsByLeastTimeTextView.setOnClickListener(statsSortOptionListener());
    sortStatsByMostCaloriesTextView.setOnClickListener(statsSortOptionListener());
    sortStatsByLeastCaloriesTextView.setOnClickListener(statsSortOptionListener());
  }

  private void editHighlightedCycleLogic() {
    editCyclesPopupWindow.showAsDropDown(savedCyclesTabLayout);
    currentlyEditingACycle = true;
    isNewCycle = false;

    positionOfSelectedCycle = (receivedHighlightPositions.get(0));
    cycleHasActivityAssigned = tdeeActivityExistsInCycleList.get(positionOfSelectedCycle);
    toggleEditPopUpViewsForAddingActivity(cycleHasActivityAssigned);

    if (cycleHasActivityAssigned) {
      setCyclesAndPomCyclesEntityInstanceToSelectedListPosition(positionOfSelectedCycle);
      retrieveCycleActivityPositionAndMetScoreFromCycleList();
    }

    String tdeeString = workoutActivityStringArray.get(positionOfSelectedCycle);
    setTdeeSpinnersToDefaultValues();
    addTDEEfirstMainTextView.setText(tdeeString);

    if (mode==1) cycleTitle = workoutTitleArray.get(positionOfSelectedCycle);
    if (mode==3) cycleTitle = pomTitleArray.get(positionOfSelectedCycle);

    cycleNameEdit.setText(cycleTitle);
  }

  private void populateRoundAdapterArraysForHighlightedCycle() {
    switch (mode) {
      case 1:
        for (int i=0; i<workoutTime.size(); i++) {
          convertedWorkoutTime.add(convertSeconds(workoutTime.get(i)/1000));
          if (i<=7) {
            workoutStringListOfRoundValuesForFirstAdapter.add(convertSeconds(workoutTime.get(i)/1000));
            workoutIntegerListOfRoundTypeForFirstAdapter.add(typeOfRound.get(i));
          }
          if (i>=8) {
            workoutStringListOfRoundValuesForSecondAdapter.add(convertSeconds(workoutTime.get(i)/1000));
            workoutIntegerListOfRoundTypeForSecondAdapter.add(typeOfRound.get(i));
          }
        }
        roundSelectedPosition = workoutTime.size()-1;
        break;
      case 3:
        cycleRoundsAdapter.disablePomFade();
        for (int i=0; i<pomValuesTime.size(); i++) pomStringListOfRoundValues.add(convertSeconds(pomValuesTime.get(i)/1000));
        break;
    }
  }

  private void removeHighlightFromCycle() {
    if (mode==1) {
      savedCycleAdapter.removeHighlight();
    } else if (mode==3) {
      savedPomCycleAdapter.removeHighlight();
    }
    cycleRoundsAdapter.notifyDataSetChanged();
    cycleRoundsAdapterTwo.notifyDataSetChanged();
  }

  private void deletingHighlightedCycleLogic() {
    AsyncTask.execute(()-> {
      deleteHighlightedCycles();
    });

    delete_highlighted_cycle.setEnabled(false);
    fadeEditCycleButtonsInAndOut(FADE_OUT_HIGHLIGHT_MODE);
    if (mode==1) savedCycleAdapter.removeHighlight();
    if (mode==3) savedPomCycleAdapter.removeHighlight();

    Toast.makeText(getApplicationContext(), "Deleted!", Toast.LENGTH_SHORT).show();
  }

  private void deleteHighlightedCycles() {
    if ((mode==1 && cyclesList.size()==0 || (mode==3 && pomCyclesList.size()==0))) {
      runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Nothing saved!", Toast.LENGTH_SHORT).show());
      return;
    }

    int cycleID = 0;
    if (mode==1) {
      for (int i=0; i<receivedHighlightPositions.size(); i++) {
        cycleID = cyclesList.get(receivedHighlightPositions.get(i)).getId();
        cycles = cyclesDatabase.cyclesDao().loadSingleCycle(cycleID).get(0);
        cyclesDatabase.cyclesDao().deleteCycle(cycles);
      }
    }
    if (mode==3) {
      for (int i=0; i<receivedHighlightPositions.size(); i++) {
        cycleID = pomCyclesList.get(receivedHighlightPositions.get(i)).getId();
        pomCycles = cyclesDatabase.cyclesDao().loadSinglePomCycle(cycleID).get(0);
        cyclesDatabase.cyclesDao().deletePomCycle(pomCycles);
      }
    }

    receivedHighlightPositions.clear();

    queryAndSortAllCyclesFromDatabase();

    runOnUiThread(()->{
      replaceCycleListWithEmptyTextViewIfNoCyclesExist();
    });
  }

  private void deleteAllCycles() {
    queryAndSortAllCyclesFromDatabase();

    if (mode==1) {
      if (cyclesList.size()>0) {
        cyclesDatabase.cyclesDao().deleteAllCycles();
        runOnUiThread(()->{
          //Clears adapter arrays and populates recyclerView with (nothing) since arrays are now empty. Also called notifyDataSetChanged().
          workoutCyclesArray.clear();
          typeOfRoundArray.clear();
          workoutTitleArray.clear();
          tdeeIsBeingTrackedInCycleList.clear();
          tdeeActivityExistsInCycleList.clear();
          workoutActivityStringArray.clear();
          savedCycleAdapter.notifyDataSetChanged();
        });
      }
    }
    if (mode==3) {
      if (pomCyclesList.size()>0) {
        cyclesDatabase.cyclesDao().deleteAllPomCycles();
        runOnUiThread(()->{
          pomArray.clear();
          pomTitleArray.clear();
          savedPomCycleAdapter.notifyDataSetChanged();
        });
      }
    }
    runOnUiThread(()-> {
      replaceCycleListWithEmptyTextViewIfNoCyclesExist();
    });
  };

  private void deleteTotalCycleTimes() {
    if (mode==1) cyclesDatabase.cyclesDao().deleteTotalTimesCycle();
    if (mode==3) cyclesDatabase.cyclesDao().deleteTotalTimesPom();

    runOnUiThread(()->{
      deleteCyclePopupWindow.dismiss();
      if (mode==1) {
        totalCycleSetTimeInMillis = 0;
        totalCycleBreakTimeInMillis = 0;
      }
      if (mode==3) {
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

    vibrationSettingForSets  = changeSettingsValues.assignSoundSettingNumericValue(defaultSoundSettingForSets);
    vibrationSettingForBreaks = changeSettingsValues.assignSoundSettingNumericValue(defaultSoundSettingForBreaks);
    isLastRoundSoundContinuous = changeSettingsValues.assignFinalRoundSwitchValue(defaultSoundSettingForLastRound);

    vibrationSettingForWork  = changeSettingsValues.assignSoundSettingNumericValue(defaultSoundSettingForWork);
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

    dotDraws.changeColorSetting(1, setColorNumericValue);
    dotDraws.changeColorSetting(2, breakColorNumericValue);
    dotDraws.changeColorSetting(3, workColorNumericValue);
    dotDraws.changeColorSetting(4, miniBreakColorNumericValue);
    dotDraws.changeColorSetting(5, fullBreakColorNumericValue);

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

  private void setTypeOFMenu(int menuType) {
    mMenuType = menuType;
  }

  private void launchGlobalSettingsFragment() {
    if (mainActivityFragmentFrameLayout.getVisibility()==View.INVISIBLE) {
      mainActivityFragmentFrameLayout.startAnimation(slideGlobalSettingsFragmentInFromLeft);
      mainActivityFragmentFrameLayout.setVisibility(View.VISIBLE);

      if (rootSettingsFragment !=null) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings_fragment_frameLayout, rootSettingsFragment)
                .commit();
      }
      sortButton.setVisibility(View.INVISIBLE);
    }
  }

  private void launchDailyStatsFragment() {
    if (mainActivityFragmentFrameLayout.getVisibility()==View.INVISIBLE) {
      mainActivityFragmentFrameLayout.startAnimation(slideDailyStatsFragmentInFromLeft);
      mainActivityFragmentFrameLayout.setVisibility(View.VISIBLE);

      fragmentManager.beginTransaction()
              .replace(R.id.settings_fragment_frameLayout, dailyStatsFragment)
              .commit();
      setTypeOFMenu(DAILY_SETTINGS_MENU);

      toggleSortMenuViewBetweenCyclesAndStats(SORTING_STATS);
    }
  }

  private void deleteDailyStatsForSelectedDays() {
    List<DayHolder> dayHolderList = dailyStatsFragment.getDayHolderList();
    List<StatsForEachActivity> statsForEachActivityList = dailyStatsFragment.getStatsForEachActivityList();

    if (areAllDaysEmptyOfActivities(statsForEachActivityList)) {
      runOnUiThread(()-> {
        showToastIfNoneActive("Nothing to delete!");
        return;
      });
    }

    List<Long> longListOfDayIdsToToDelete = new ArrayList<>();
    List<Long> longListOfStatsForEachIdsToDelete = new ArrayList<>();

    for (int i=0; i<dayHolderList.size(); i++) {
      longListOfDayIdsToToDelete.add(dayHolderList.get(i).getDayId());
    }

    for (int i=0; i<statsForEachActivityList.size(); i++) {
      longListOfStatsForEachIdsToDelete.add(statsForEachActivityList.get(i).getUniqueIdTiedToTheSelectedActivity());
    }

    dailyStatsAccess.deleteMultipleDayHolderEntries(longListOfDayIdsToToDelete);
    dailyStatsAccess.deleteMultipleStatsForEachActivityEntries(longListOfStatsForEachIdsToDelete);

    dailyStatsFragment.setNumberOfDaysWithActivitiesHasChangedBoolean(true);
    dailyStatsFragment.populateListsAndTextViewsFromEntityListsInDatabase();

    dailyStatsFragment.setHaveStatsBeenChangedBoolean(true);
  }

  private void deleteDailyStatsForAllDays() {
    dailyStatsAccess.deleteAllDayHolderEntries();
    dailyStatsAccess.deleteAllStatsForEachActivityEntries();

    dailyStatsFragment.setNumberOfDaysWithActivitiesHasChangedBoolean(true);
    dailyStatsFragment.populateListsAndTextViewsFromEntityListsInDatabase();

    dailyStatsFragment.setHaveStatsBeenChangedBoolean(true);
  }

  private boolean areAllDaysEmptyOfActivities(List<StatsForEachActivity> statsForEachActivityList) {
    List<String> listOfActivities = new ArrayList<>();

    for (int i=0; i<statsForEachActivityList.size(); i++) {
      listOfActivities.add(statsForEachActivityList.get(i).getActivity());
    }

    return listOfActivities.size()==0;
  }

  private void setEndOfRoundSounds(int vibrationSetting, boolean repeat) {
    switch (vibrationSetting) {
      case 2: case 3: case 4:
        if (repeat) {
          vibrator.vibrate(changeSettingsValues.getVibrationSetting(vibrationSetting), 0);
        } else {
          vibrator.vibrate(changeSettingsValues.getVibrationSetting(vibrationSetting), -1);
        }
        break;
      case 5:
        if (!repeat) {
          mediaPlayer.setLooping(false);
        } else {
          mediaPlayer.setLooping(true);
        }
        mediaPlayer.start();
        break;
    }
  }

  private void assignColorSettingValues(int typeOfRound, int settingsNumber) {
    int color = changeSettingsValues.assignColor(settingsNumber);
    switch (typeOfRound) {
      case 1:
        setColor = color; break;
      case 2:
        breakColor = color; break;
      case 3:
        workColor = color; break;
      case 4:
        miniBreakColor = color; break;
      case 5:
        fullBreakColor = color; break;
    }
  }

  private void assignSoundSettingValues(int typeOfRound, int settingNumber) {
    switch (typeOfRound) {
      case 0:
        vibrationSettingForSets = settingNumber; break;
      case 1:
        vibrationSettingForBreaks = settingNumber; break;
      case 3:
        vibrationSettingForWork = settingNumber; break;
      case 4:
        vibrationSettingForMiniBreaks = settingNumber; break;
    }
  }

  private void timerPopUpDismissalLogic() {
    timerDisabled = false;
    makeCycleAdapterVisible = false;
    timerPopUpIsVisible = false;
    beginTimerForNextRound = false;
    reset.setVisibility(View.INVISIBLE);
    dotDraws.setMode(mode);

    if (mode==1) {
      savedCycleRecycler.setVisibility(View.VISIBLE);
      savedCycleAdapter.notifyDataSetChanged();
      pauseAndResumeTimer(PAUSING_TIMER);
    } else if (mode==3){
      pauseAndResumePomodoroTimer(PAUSING_TIMER);
      savedPomCycleRecycler.setVisibility(View.VISIBLE);
      savedPomCycleAdapter.notifyDataSetChanged();
    }
    mHandler.removeCallbacksAndMessages(null);
  }

  private void editCyclesPopUpDismissalLogic() {
    if (currentlyEditingACycle) {
//      saveCycleOnPopUpDismissIfEdited();

      if (mode==1) {
        savedCycleAdapter.removeHighlight();
      } else if (mode==3) {
        savedPomCycleAdapter.removeHighlight();
      }
      fadeEditCycleButtonsInAndOut(FADE_OUT_EDIT_CYCLE);
      currentlyEditingACycle = false;
    }

    fab.setEnabled(true);
    cycleRoundsAdapter.notifyDataSetChanged();
    cycleRoundsAdapterTwo.notifyDataSetChanged();
    roundListDivider.setVisibility(View.GONE);
  }

  private void saveCycleOnPopUpDismissIfEdited() {
    boolean roundIsEdited = false;

    if (!timerPopUpWindow.isShowing()) {
      if (mode==1) {
        if (!workoutStringListOfRoundValuesForFirstAdapter.isEmpty()){
          if (!workoutStringListOfRoundValuesForFirstAdapter.equals(oldCycleRoundListOne) || !workoutStringListOfRoundValuesForSecondAdapter.equals(oldCycleRoundListTwo) || !cycleTitle.equals(oldCycleTitleString)) {
            roundIsEdited = true;
          }
        }
      }
      if (mode==3) {
        if (!pomArray.isEmpty()) {
          if (!pomArray.equals(oldPomRoundList) || !cycleTitle.equals(oldCycleTitleString)) {
            roundIsEdited = true;
          }
        }
      }
    }

    if (roundIsEdited) {
      if (currentlyEditingACycle) {
        ResumeOrResetCycle(RESETTING_CYCLE_FROM_ADAPTER);
      }

      //This already executes when timer is launched.
      AsyncTask.execute(()-> {
        saveAddedOrEditedCycleASyncRunnable();
        queryAndSortAllCyclesFromDatabase();
      });

      roundIsEdited = false;
      Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();
    }
  }

  private void assignOldCycleValuesToCheckForChanges() {
    oldCycleTitleString = cycleTitle;
    if (mode==1) {
      oldCycleRoundListOne = new ArrayList<>(workoutStringListOfRoundValuesForFirstAdapter);
      oldCycleRoundListTwo = new ArrayList<>(workoutStringListOfRoundValuesForSecondAdapter);
    }
    if (mode==3) {
      oldPomRoundList = new ArrayList<>(pomArray);
    }
  }

  private void setEditPopUpTimerHeaders(int headerToSelect) {
    if (headerToSelect == 1) {
      secondRoundTypeHeaderInEditPopUp.setTextColor(Color.GRAY);
      thirdRoundTypeHeaderInEditPopUp.setTextColor(Color.GRAY);
      editHeaderSelected = 1;

      if (mode==1) {
        firstRoundTypeHeaderInEditPopUp.setTextColor(setColor);

        Drawable newDraw = ContextCompat.getDrawable(getApplicationContext(), R.drawable.infinity_large_green);
        newDraw.setColorFilter(setColor, PorterDuff.Mode.SRC_IN);
        toggleInfinityRounds.setImageDrawable(newDraw);
        toggleInfinityModeAndSetRoundType();

        editPopUpTimerArray = savedEditPopUpArrayForFirstHeaderModeOne;
      }
      if (mode==3) {
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

      if (mode==1) {
        secondRoundTypeHeaderInEditPopUp.setTextColor(breakColor);

        Drawable newDraw = ContextCompat.getDrawable(getApplicationContext(), R.drawable.infinity_large_green);
        newDraw.setColorFilter(breakColor, PorterDuff.Mode.SRC_IN);
        toggleInfinityRounds.setImageDrawable(newDraw);
        toggleInfinityModeAndSetRoundType();

        editPopUpTimerArray = savedEditPopUpArrayForSecondHeaderModeOne;
      }
      if (mode==3) {
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

    if (mode==1) {
      String savedTimerString = convertedTimerArrayToString(editPopUpTimerArray);
      timerValueInEditPopUpTextView.setText(savedTimerString);

      int totalTime = convertStringToSecondsValue(savedTimerString);
      setAndCapTimerValues(totalTime);
    }

    changeEditTimerTextViewColorIfNotEmpty();
  }

  private void toggleInfinityModeAndSetRoundType() {
    if (editHeaderSelected==1) {
      if (isSavedInfinityOptionActiveForSets) {
        toggleInfinityRounds.setAlpha(1.0f);
        roundType = 2;
      } else {
        toggleInfinityRounds.setAlpha(0.3f);
        roundType = 1;
      }
      setAndCapTimerValues(setValue);
    }
    if (editHeaderSelected==2) {
      if (isSavedInfinityOptionActiveForBreaks) {
        toggleInfinityRounds.setAlpha(1.0f);
        roundType = 4;
      } else {
        toggleInfinityRounds.setAlpha(0.3f);
        roundType = 3;
      }
      setAndCapTimerValues(breakValue);
    }
  }

  private void resetEditPopUpTimerHeaders() {
    editHeaderSelected = 1;
    roundType = 1;
    toggleInfinityRounds.setAlpha(0.3f);
  }


  private void changeEditTimerTextViewColorIfNotEmpty() {
    if (editPopUpTimerArray.size()>0) {
      timerValueInEditPopUpTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));
    } else {
      timerValueInEditPopUpTextView.setTextColor(Color.WHITE);
    }
  }

  private void saveEditHeaderTimerStringValues() {
    if (mode==1) {
      if (editHeaderSelected == 1) savedEditPopUpArrayForFirstHeaderModeOne = new ArrayList<>(editPopUpTimerArray);
      if (editHeaderSelected == 2) savedEditPopUpArrayForSecondHeaderModeOne = new ArrayList<>(editPopUpTimerArray);
    }
  }

  private void addToEditPopUpTimerArrays(ArrayList<String> arrayList, TextView textView) {
    if (arrayList.size()<=3) {
      for (int i=0; i<10; i++)  {
        if (textView.getText().equals(String.valueOf(i))) {
          arrayList.add(String.valueOf(i));
        }
      }
    }
  }

  private void setEditPopUpTimerStringValues() {
    String editPopUpTimerString = "";
    if (mode==1) {
      editPopUpTimerString = convertedTimerArrayToString(editPopUpTimerArray);
      saveEditHeaderTimerStringValues();
      timerValueInEditPopUpTextView.setText(editPopUpTimerString);

      int totalTime = convertStringToSecondsValue(editPopUpTimerString);
      setAndCapTimerValues(totalTime);
    }

    if (mode==3) {
      int totalTimeOne = 0;
      int totalTimeTwo = 0;
      int totalTimeThree = 0;
      String editPopUptimerTextViewStringOne = "";
      String editPopUptimerTextViewStringTwo = "";
      String editPopUpTimerStringThree = "";

      editPopUptimerTextViewStringOne =  convertedTimerArrayToString(savedEditPopUpArrayForFirstHeaderModeThree);
      totalTimeOne = convertStringToSecondsValue(editPopUptimerTextViewStringOne);
      pomTimerValueInEditPopUpTextViewOne.setText(editPopUptimerTextViewStringOne);

      editPopUptimerTextViewStringTwo = convertedTimerArrayToString(savedEditPopUpArrayForSecondHeaderModeThree);
      totalTimeTwo = convertStringToSecondsValue(editPopUptimerTextViewStringTwo);
      pomTimerValueInEditPopUpTextViewTwo.setText(editPopUptimerTextViewStringTwo);

      editPopUpTimerStringThree = convertedTimerArrayToString(savedEditPopUpArrayForThirdHeader);
      totalTimeThree = convertStringToSecondsValue(editPopUpTimerStringThree);
      pomTimerValueInEditPopUpTextViewThree.setText(editPopUpTimerStringThree);

      setAndCapPomValuesForEditTimer(totalTimeOne, 1);
      setAndCapPomValuesForEditTimer(totalTimeTwo, 2);
      setAndCapPomValuesForEditTimer(totalTimeThree, 3);
    }

    changeEditTimerTextViewColorIfNotEmpty();
  }

  private void convertEditPopUpTimerArrayToStringValues(int chosenMode) {
    if (chosenMode==1) {

    }
  }

  public String convertedTimerArrayToString(ArrayList<String> arrayToConvert) {
    ArrayList<String> timeLeft = new ArrayList<>();
    timeLeft = populateEditTimerArray(arrayToConvert);

    String editPopUpTimerString;
    switch (arrayToConvert.size()) {
      default: case 0: case 1:
        editPopUpTimerString = timeLeft.get(4) + timeLeft.get(3) + timeLeft.get(2) + timeLeft.get(1) + timeLeft.get(0); break;
      case 2:
        editPopUpTimerString = timeLeft.get(4) + timeLeft.get(3) + timeLeft.get(2) + timeLeft.get(0) + timeLeft.get(1); break;
      case 3:
        editPopUpTimerString = timeLeft.get(4) + timeLeft.get(0) + timeLeft.get(2) + timeLeft.get(1) + timeLeft.get(3); break;
      case 4:
        editPopUpTimerString = timeLeft.get(0) + timeLeft.get(1) + timeLeft.get(2) + timeLeft.get(3) + timeLeft.get(4); break;
    }
    return editPopUpTimerString;
  }

  public ArrayList<String> populateEditTimerArray(ArrayList<String> arrayToPopulate) {
    ArrayList<String> timeLeft = new ArrayList<>();
    timeLeft.add("0");
    timeLeft.add("0");
    timeLeft.add(":");
    timeLeft.add("0");
    timeLeft.add("0");

    for (int i=0; i<arrayToPopulate.size(); i++) {
      //Third index of timeLeft list is colon, so we never want to replace it.
      if (i<2) {
        timeLeft.set(i, arrayToPopulate.get(i));
      } else {
        timeLeft.set(i+1, arrayToPopulate.get(i));
      }
    }
    return timeLeft;
  }

  public int convertStringToSecondsValue(String timerString) {
    int totalMinutes = Integer.parseInt(timerString.substring(0, 1) + timerString.substring(1, 2));
    int totalSeconds = Integer.parseInt(timerString.substring(3, 4) + timerString.substring(4, 5));
    if (totalSeconds>60) {
      totalSeconds = totalSeconds%60;
      totalMinutes +=1;
    }
    int totalTime = (totalMinutes*60) + totalSeconds;
    return totalTime;
  }

  public ArrayList<String> convertIntegerToStringArray(int timerInteger) {
    String minutes = String.valueOf(timerInteger/60);
    String seconds = String.valueOf(timerInteger%60);

    String indexOne = "0";
    String indexTwo = "0";
    String indexThree = "0";
    String indexFour = "0";

    int indexPlaces = 0;

    if (minutes.length()<=1) {
      indexTwo = minutes;
    } else {
      indexOne = minutes.substring(0, 1);
      indexTwo = minutes.substring(1, 2);
    }

    if (seconds.length()<=1) {
      indexFour = seconds;
    } else {
      indexThree = seconds.substring(0 ,1);
      indexFour = seconds.substring(1, 2);
    }

    editPopUpTimerArrayCapped.clear();
    editPopUpTimerArrayCapped.add(indexOne);
    editPopUpTimerArrayCapped.add(indexTwo);
    editPopUpTimerArrayCapped.add(indexThree);
    editPopUpTimerArrayCapped.add(indexFour);

    if (timerInteger>0) indexPlaces++;
    if (timerInteger>9) indexPlaces++;
    if (timerInteger>59) indexPlaces++;
    if (timerInteger>599) indexPlaces++;

    ArrayList<String> newList = new ArrayList<>();
    for (int i=0; i<indexPlaces; i++) {
      newList.add(editPopUpTimerArrayCapped.get(i+(4-indexPlaces)));
    }

    return newList;
  }

  private void setEditPopUpArrayWithCappedValues(ArrayList<String> arrayToSet, int numberOfIndices) {
    arrayToSet.clear();
    for (int i=0; i<numberOfIndices; i++) {
      arrayToSet.add(editPopUpTimerArrayCapped.get(i+(4-numberOfIndices)));
    }
  }

  private void setViewsAndColorsToPreventTearingInEditPopUp(boolean popUpIsActive) {
    mainView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

    if (popUpIsActive) {
      if (mode==1) savedCycleRecycler.setVisibility(View.GONE);
      if (mode==3) savedPomCycleRecycler.setVisibility(View.GONE);
      emptyCycleList.setVisibility(View.GONE);
    } else {
      if (!makeCycleAdapterVisible) {
        if (mode==1) {
          savedCycleAdapter.notifyDataSetChanged();
          savedCycleRecycler.setVisibility(View.VISIBLE);
        }
        if (mode==3) {
          savedPomCycleAdapter.notifyDataSetChanged();
          savedPomCycleRecycler.setVisibility(View.VISIBLE);
        }
      }
    }
  }

  private void resumeOrResetCycleFromAdapterList(int resumeOrReset){
    if (resumeOrReset==RESUMING_CYCLE_FROM_ADAPTER) {
      timerIsPaused = true;
      progressBar.setProgress(currentProgressBarValue);
      timeLeft.setText(retrieveTimerValueString());

      toggleViewsForTotalDailyAndCycleTimes(trackActivityWithinCycle);

      AsyncTask.execute(()-> {
        dailyStatsAccess.setActivityPositionInListForCurrentDay();

       runOnUiThread(()->{
         displayCycleOrDailyTotals();
         setCyclesCompletedTextView();
         toggleCycleTimeTextViewSizes(trackActivityWithinCycle);

         setAllActivityTimesAndCaloriesToTextViews();

         timerPopUpWindow.showAtLocation(mainView, Gravity.NO_GRAVITY, 0, 0);
       });
      });
    } else if (resumeOrReset==RESETTING_CYCLE_FROM_ADAPTER) {
      if (mode==1) {
        savedCycleAdapter.removeActiveCycleLayout();
        savedCycleAdapter.notifyDataSetChanged();
      }
      if (mode==3) {
        savedPomCycleAdapter.removeActiveCycleLayout();
        savedPomCycleAdapter.notifyDataSetChanged();
      }
      resetTimer();
    }
  }

  private String retrieveTimerValueString() {
    long millis;
    if (mode==1) {
      if (typeOfRound.get(currentRound)==1 || typeOfRound.get(currentRound)==2) {
        millis = setMillis;
      } else {
        millis = breakMillis;
      }
    } else {
      millis = pomMillis;
    }
    return (convertSeconds((millis + 999) / 1000));
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

  public String setNotificationHeader(String selectedMode, String roundType) {
    return (getString(R.string.notification_text_header, selectedMode, roundType));
  }

  private String setNotificationBody(int roundsLeft, int startRounds, long timeLeft) {
    String currentTimerRound = String.valueOf(startRounds-roundsLeft + 1);
    String totalRounds = String.valueOf(startRounds);

    String timeRemaining = "";
    timeRemaining = convertTimerValuesToString(((timeLeft-250) +1000) / 1000);

    return getString(R.string.notification_text, currentTimerRound, totalRounds, timeRemaining);
  }

  private void instantiateNotifications() {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= 26) {
      CharSequence name = "Timers";
      String description = "Timer Countdown";
      int importance = NotificationManager.IMPORTANCE_DEFAULT;
      NotificationChannel channel = new NotificationChannel("1", name, importance);
      channel.setDescription(description);

      // Register the channel with the system; you can't change the importance
      // or other notification behaviors after this
      NotificationManager notificationManager = getSystemService(NotificationManager.class);
      notificationManager.createNotificationChannel(channel);
      builder = new NotificationCompat.Builder(this, "1");
    } else {
      builder = new NotificationCompat.Builder(this);
    }

    builder.setSmallIcon(R.drawable.start_cycle);
    builder.setAutoCancel(false);
    builder.setPriority(Notification.PRIORITY_DEFAULT);
    builder.setDeleteIntent(dismissNotificationIntent(this, 1));

    PackageManager pm = getApplicationContext().getPackageManager();
    Intent pmIntent = pm.getLaunchIntentForPackage(getApplicationContext().getPackageName());
    pmIntent.setPackage(null);

    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, pmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    builder.setContentIntent(pendingIntent);

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
          if (mode==1) {
            if (typeOfRound.get(currentRound) == 1 || typeOfRound.get(currentRound) == 2) {
              headerOne = setNotificationHeader("Workout", "Set");
              bodyOne = setNotificationBody(numberOfRoundsLeft, startRounds, setMillis);
            } else {
              headerOne = setNotificationHeader("Workout", "Break");
              bodyOne = setNotificationBody(numberOfRoundsLeft, startRounds, breakMillis);
            }
          }

          if (mode==3) {
            int numberOfRoundsLeft = 8-pomDotCounter;
            switch (pomDotCounter) {
              case 0: case 2: case 4: case 6:
                headerOne = setNotificationHeader("Pomodoro", "Work");
                bodyOne = setNotificationBody(numberOfRoundsLeft, 8, pomMillis);
                break;
              case 1: case 3: case 5: case 7:
                headerOne = setNotificationHeader("Pomodoro", "Break");
                bodyOne = setNotificationBody(numberOfRoundsLeft, 8, pomMillis);
                break;
            }
          }
        }

        if (stopWatchPopUpWindow.isShowing()) {
          headerOne = getString(R.string.notification_stopwatch_header);
          bodyOne = convertTimerValuesToString((long) stopWatchSeconds);
        }

        builder.setStyle(new NotificationCompat.InboxStyle()
                .addLine(headerOne)
                .addLine(bodyOne)
        );

        notificationManagerCompat.notify(1, builder.build());
      }
    }
  }

  private void activateResumeOrResetOptionForCycle() {
    if (mode==1) {
      //Only shows restart/resume options of a cycle has been started.
      if (activeCycle) {
        if (isNewCycle) positionOfSelectedCycle = workoutCyclesArray.size()-1;
        savedCycleAdapter.showActiveCycleLayout(positionOfSelectedCycle, startRounds-numberOfRoundsLeft);
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

    if (mode==3) {
      if (activeCycle) {
        if (isNewCycle) positionOfSelectedCycle = pomArray.size()-1;
        savedPomCycleAdapter.showActiveCycleLayout(positionOfSelectedCycle, pomDotCounter);
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
    if (mode==1) if (workoutCyclesArray.size()!=0) emptyCycleList.setVisibility(View.GONE); else emptyCycleList.setVisibility(View.VISIBLE);
    if (mode==3) if (pomArray.size()!=0) emptyCycleList.setVisibility(View.GONE); else emptyCycleList.setVisibility(View.VISIBLE);
  }

  private void removeCycleHighlights() {
    if (mode==1) {
      savedCycleAdapter.removeHighlight();
      savedCycleAdapter.notifyDataSetChanged();
    }
    if (mode==3) {
      savedPomCycleAdapter.removeHighlight();
      savedPomCycleAdapter.notifyDataSetChanged();
    }
  }

  //Fades action bar buttons in/out depending on whether we are editing cycles or not.
  private void fadeEditCycleButtonsInAndOut(int typeOfFade) {
    //Clearing all animations. If we don't, their alphas tend to get reset.
    if (typeOfFade!=FADE_IN_EDIT_CYCLE) {
      edit_highlighted_cycle.clearAnimation();
      delete_highlighted_cycle.clearAnimation();
      cancelHighlight.clearAnimation();
      appHeader.clearAnimation();
      sortButton.clearAnimation();
      cancelHighlight.setEnabled(true);
    }
    if (typeOfFade==FADE_IN_HIGHLIGHT_MODE) {
      appHeader.startAnimation(fadeOut);
      edit_highlighted_cycle.startAnimation(fadeIn);
      delete_highlighted_cycle.startAnimation(fadeIn);
      cancelHighlight.startAnimation(fadeIn);
      sortButton.startAnimation(fadeOut);

      sortButton.setEnabled(false);
      edit_highlighted_cycle.setEnabled(true);
      delete_highlighted_cycle.setEnabled(true);
      cancelHighlight.setEnabled(true);
    }
    if (typeOfFade==FADE_OUT_HIGHLIGHT_MODE) {
      appHeader.startAnimation(fadeIn);
      edit_highlighted_cycle.startAnimation(fadeOut);
      delete_highlighted_cycle.startAnimation(fadeOut);
      appHeader.startAnimation(fadeIn);
      sortButton.startAnimation(fadeIn);

      cancelHighlight.startAnimation(fadeOut);
      cancelHighlight.setVisibility(View.GONE);

      sortButton.setEnabled(true);
      edit_highlighted_cycle.setEnabled(false);
      delete_highlighted_cycle.setEnabled(false);
      cancelHighlight.setEnabled(false);
    }
    if (typeOfFade==FADE_OUT_EDIT_CYCLE) {
      sortButton.setVisibility(View.VISIBLE);
      delete_highlighted_cycle.setVisibility(View.INVISIBLE);
      delete_highlighted_cycle.setEnabled(false);

      sortButton.setEnabled(true);
      edit_highlighted_cycle.setEnabled(false);
      delete_highlighted_cycle.setEnabled(false);
      cancelHighlight.setEnabled(false);
    }
  }

  private void clearRoundAndCycleAdapterArrayLists() {
    convertedWorkoutTime.clear();
    workoutStringListOfRoundValuesForFirstAdapter.clear();
    workoutStringListOfRoundValuesForSecondAdapter.clear();
    workoutIntegerListOfRoundTypeForFirstAdapter.clear();
    workoutIntegerListOfRoundTypeForSecondAdapter.clear();

    workoutTime.clear();
    typeOfRound.clear();
    pomValuesTime.clear();

    pomStringListOfRoundValues.clear();

    cycleRoundsAdapter.notifyDataSetChanged();
    cycleRoundsAdapterTwo.notifyDataSetChanged();
  }

  private void setAndCapTimerValues(int value) {
    switch (mode) {
      case 1:
        if (editHeaderSelected==1) setValue = timerValueBoundsFormula(5, 5400, value);
        if (editHeaderSelected==2) breakValue = timerValueBoundsFormula(5, 5400, value);
        break;
      case 3:
        pomValue1 = timerValueBoundsFormula(600, 3600, value);
        pomValue2 = timerValueBoundsFormula(180, 600, value);
        pomValue3 = timerValueBoundsFormula(900, 3600, value);
        break;
    }
  }

  private void setAndCapPomValuesForEditTimer(int value, int variableToCap) {
    if (variableToCap==1) pomValue1 = timerValueBoundsFormula(600, 3600, value);
    if (variableToCap==2) pomValue2 = timerValueBoundsFormula(180, 600, value);
    if (variableToCap==3) pomValue3 = timerValueBoundsFormula(900, 3600, value);
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

  public String convertTimerValuesToString(long totalSeconds) {
    DecimalFormat df = new DecimalFormat("00");
    long minutes;
    long remainingSeconds;
    minutes = totalSeconds / 60;

    remainingSeconds = totalSeconds % 60;
    if (totalSeconds >= 60) {
      String formattedSeconds = df.format(remainingSeconds);
      if (formattedSeconds.length() > 2) formattedSeconds = "0" + formattedSeconds;
      if (totalSeconds>=600) {
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

  private void animateEnding() {
    endAnimation = new AlphaAnimation(1.0f, 0.0f);
    endAnimation.setDuration(300);
    endAnimation.setStartOffset(0);
    endAnimation.setRepeatMode(Animation.REVERSE);
    endAnimation.setRepeatCount(Animation.INFINITE);
    progressBar.startAnimation(endAnimation);
    timeLeft.startAnimation(endAnimation);
  }

  private void adjustCustom(boolean adding) {
    inputMethodManager.hideSoftInputFromWindow(editCyclesPopupView.getWindowToken(), 0);
    if (adding) {
      if (mode==1) {
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
            Toast.makeText(getApplicationContext(), "Nada for now!", Toast.LENGTH_SHORT).show();
            return;
        }
      }
      if (mode==3) {
        savedEditPopUpArrayForFirstHeaderModeThree = convertIntegerToStringArray(pomValue1);
        savedEditPopUpArrayForSecondHeaderModeThree = convertIntegerToStringArray(pomValue2);
        savedEditPopUpArrayForThirdHeader = convertIntegerToStringArray(pomValue3);

        setEditPopUpTimerStringValues();

        if (pomValuesTime.size()==0) {
          for (int i = 0; i < 3; i++) {
            pomValuesTime.add(pomValue1 * 1000);
            pomValuesTime.add(pomValue2 * 1000);
          }
          pomValuesTime.add(pomValue1 * 1000);
          pomValuesTime.add(pomValue3 * 1000);
          for (int j=0; j<pomValuesTime.size(); j++) pomStringListOfRoundValues.add(convertSeconds(pomValuesTime.get(j)/1000));

          cycleRoundsAdapter.setPomFade(true);
          cycleRoundsAdapter.notifyDataSetChanged();
        } else {
          showToastIfNoneActive("Pomodoro cycle already loaded!");
        }
      }
    } else {
      if (mode==1) {
        if (subtractedRoundIsFading) {
          removeRound();
        }
        if (workoutTime.size()>0) {
          if (roundSelectedPosition<=7) {
            cycleRoundsAdapter.setFadeOutPosition(roundSelectedPosition);
            cycleRoundsAdapter.notifyDataSetChanged();
          } else {
            cycleRoundsAdapterTwo.setFadeOutPosition(roundSelectedPosition-8);
            cycleRoundsAdapterTwo.notifyDataSetChanged();
          }
          subtractedRoundIsFading = true;
        } else {
          showToastIfNoneActive("No rounds to clear!");
        }
      } else if (mode==3) {
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
    if (mode==1) {
      //If adding a round.
      if (!replacingValue) {
        if (workoutTime.size()<16) {
          workoutTime.add(integerValue * 1000);
          convertedWorkoutTime.add(convertSeconds(integerValue));
          typeOfRound.add(roundType);
          roundSelectedPosition = workoutTime.size()-1;

          if (workoutTime.size()<=8) {
            workoutStringListOfRoundValuesForFirstAdapter.add(convertedWorkoutTime.get(convertedWorkoutTime.size()-1));
            workoutIntegerListOfRoundTypeForFirstAdapter.add(typeOfRound.get(typeOfRound.size()-1));
            cycleRoundsAdapter.setFadeInPosition(roundSelectedPosition);
            cycleRoundsAdapter.notifyDataSetChanged();
          } else {
            workoutStringListOfRoundValuesForSecondAdapter.add(convertedWorkoutTime.get(convertedWorkoutTime.size()-1));
            workoutIntegerListOfRoundTypeForSecondAdapter.add(typeOfRound.get(typeOfRound.size()-1));
            cycleRoundsAdapterTwo.setFadeInPosition(roundSelectedPosition-8);
            cycleRoundsAdapterTwo.notifyDataSetChanged();
          }
        } else {
          showToastIfNoneActive("Full!");
        }

        setRoundRecyclerViewsWhenChangingAdapterCount(workoutTime);
      } else {
        workoutTime.set(roundSelectedPosition, integerValue*1000);
        convertedWorkoutTime.set(roundSelectedPosition, convertSeconds(integerValue));
        typeOfRound.set(roundSelectedPosition, roundType);
        if (roundSelectedPosition<=7) {
          workoutStringListOfRoundValuesForFirstAdapter.set(roundSelectedPosition, convertedWorkoutTime.get(roundSelectedPosition));
          workoutIntegerListOfRoundTypeForFirstAdapter.set(roundSelectedPosition, typeOfRound.get(roundSelectedPosition));

          cycleRoundsAdapter.isRoundCurrentlySelected(false);
          cycleRoundsAdapter.setFadeInPosition(roundSelectedPosition);
          cycleRoundsAdapter.notifyDataSetChanged();
        } else {
          workoutStringListOfRoundValuesForSecondAdapter.set(roundSelectedPosition-8, convertedWorkoutTime.get(roundSelectedPosition));
          workoutIntegerListOfRoundTypeForSecondAdapter.set(roundSelectedPosition-8, typeOfRound.get(roundSelectedPosition));

          cycleRoundsAdapterTwo.isRoundCurrentlySelected(false);
          cycleRoundsAdapterTwo.setFadeInPosition(roundSelectedPosition-8);
          cycleRoundsAdapterTwo.notifyDataSetChanged();
        }
        roundIsSelected = false;
      }
    }
  }

  private void removeRound() {
    if (mode==1) {
      if (workoutTime.size()>0) {
        if (workoutTime.size()-1>=roundSelectedPosition) {
          if (workoutTime.size()<=8 || (roundIsSelected && roundSelectedPosition<=7)) {
            if (workoutStringListOfRoundValuesForFirstAdapter.size()-1>=roundSelectedPosition) {
              workoutStringListOfRoundValuesForFirstAdapter.remove(roundSelectedPosition);
              workoutIntegerListOfRoundTypeForFirstAdapter.remove(roundSelectedPosition);
              cycleRoundsAdapter.setFadeOutPosition(-1);
              cycleRoundsAdapter.notifyDataSetChanged();
            }
          } else {
            if (workoutStringListOfRoundValuesForSecondAdapter.size()-1>=roundSelectedPosition-8) {
              workoutStringListOfRoundValuesForSecondAdapter.remove(roundSelectedPosition-8);
              workoutIntegerListOfRoundTypeForSecondAdapter.remove(roundSelectedPosition-8);
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
          if (roundSelectedPosition<=7) {
            cycleRoundsAdapter.isRoundCurrentlySelected(false);
            cycleRoundsAdapter.notifyDataSetChanged();
          } else {
            cycleRoundsAdapterTwo.isRoundCurrentlySelected(false);
            cycleRoundsAdapterTwo.notifyDataSetChanged();
          }
          consolidateRoundAdapterLists = true;
          roundIsSelected = false;
        }
        roundSelectedPosition = workoutTime.size()-1;
      } else {
        showToastIfNoneActive("Empty!");
      }
    }
  }

  private void setRoundRecyclerViewsWhenChangingAdapterCount(ArrayList<Integer> adapterList) {
    if (mode==1) {
      if (adapterList.size()<=8) {
        roundRecyclerTwo.setVisibility(View.GONE);
        roundListDivider.setVisibility(View.GONE);

        if (screenRatioLayoutChanger.setScreenRatioBasedLayoutChanges()<1.8f) {
          roundRecyclerParentLayoutParams.width = convertDensityPixelsToScalable(180);
          roundRecyclerOneLayoutParams.leftMargin = convertDensityPixelsToScalable(60);
        } else {
          roundRecyclerParentLayoutParams.width = convertDensityPixelsToScalable(200);
          roundRecyclerOneLayoutParams.leftMargin = convertDensityPixelsToScalable(20);
        }
        roundRecyclerOneLayoutParams.rightMargin = 0;
      } else {
        roundRecyclerTwo.setVisibility(View.VISIBLE);
        roundListDivider.setVisibility(View.VISIBLE);

        if (screenRatioLayoutChanger.setScreenRatioBasedLayoutChanges()<1.8f) {
          roundRecyclerParentLayoutParams.width = convertDensityPixelsToScalable(240);
          roundRecyclerOneLayoutParams.leftMargin = convertDensityPixelsToScalable(10);

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

  private void populateCycleAdapterArrayList() {
    switch (mode) {
      case 1:
        workoutTime.clear();
        typeOfRound.clear();
        if (workoutCyclesArray.size()-1>=positionOfSelectedCycle) {
          String[] fetchedRounds = workoutCyclesArray.get(positionOfSelectedCycle).split(" - ");
          String[] fetchedRoundType = typeOfRoundArray.get(positionOfSelectedCycle).split(" - ");

          for (int i=0; i<fetchedRounds.length; i++) {
            workoutTime.add(Integer.parseInt(fetchedRounds[i]));
          }
          for (int j=0; j<fetchedRoundType.length; j++) {
            typeOfRound.add(Integer.parseInt(fetchedRoundType[j]));
          }

          ArrayList<String> convertedWorkoutRoundList = convertMillisIntegerListToTimerStringList(workoutTime);
          dotDraws.updateWorkoutTimes(convertedWorkoutRoundList, typeOfRound);

          cycleTitle = workoutTitleArray.get(positionOfSelectedCycle);
        }
        break;

      case 3:
        pomValuesTime.clear();
        if (pomArray.size()-1>=positionOfSelectedCycle) {
          String[] fetchedPomCycle = pomArray.get(positionOfSelectedCycle).split(" - ");

          cycleTitle = pomTitleArray.get(positionOfSelectedCycle);

          /////---------Testing pom round iterations---------------/////////
//          for (int i=0; i<8; i++) if (i%2!=0) pomValuesTime.add(5000); else pomValuesTime.add(7000);
          for (int i=0; i<fetchedPomCycle.length; i++) {
            pomValuesTime.add(Integer.parseInt(fetchedPomCycle[i]));
          }
        }
        break;
    }
  }

  private void launchPomTimerCycle(int typeOfLaunch) {
    if (pomValuesTime.size()==0) {
      showToastIfNoneActive("Cycle cannot be empty!");
      return;
    }

    setTimerLaunchViews(typeOfLaunch);

    AsyncTask.execute(()-> {
      if (!isNewCycle) {
        retrieveTotalSetAndBreakAndCompletedCycleValuesFromCycleList();
      }

      saveAddedOrEditedCycleASyncRunnable();
      queryAndSortAllCyclesFromDatabase();

      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          setTimerLaunchLogic(false);
        }
      });
    });
  }

  private void launchTimerCycle(int typeOfLaunch) {
    if (workoutTime.size()==0) {
      showToastIfNoneActive("Cycle cannot be empty!");
      return;
    }

    setTimerLaunchViews(typeOfLaunch);

    Calendar calendar = Calendar.getInstance(Locale.getDefault());
    dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

    if (savedCycleAdapter.isCycleActive()) {
      savedCycleAdapter.removeActiveCycleLayout();
      savedCycleAdapter.notifyDataSetChanged();
    }

    AsyncTask.execute(()-> {
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
      }

      if (cycleHasActivityAssigned) {
        queryAllStatsEntitiesAndAssignTheirValuesToObjects();
      }

      if (!isNewCycle) {
        retrieveTotalSetAndBreakAndCompletedCycleValuesFromCycleList();
      }

      saveAddedOrEditedCycleASyncRunnable();
      queryAndSortAllCyclesFromDatabase();

      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          setTimerLaunchLogic(trackActivityWithinCycle);
        }
      });
    });
  }

  private void setTimerLaunchLogic(boolean trackingActivity) {
    displayCycleOrDailyTotals();
    toggleViewsForTotalDailyAndCycleTimes(trackingActivity);

    retrieveTotalDailySetAndBreakTimes();
    displayCycleOrDailyTotals();
    roundDownAllTotalTimeValuesToEnsureSyncing();

    resetTimer();
  }

  private void setTimerLaunchViews(int typeOfLaunch) {
    makeCycleAdapterVisible = true;
    timerPopUpIsVisible = true;
    setViewsAndColorsToPreventTearingInEditPopUp(true);

    cycle_title_textView.setText(cycleTitle);

    if (savedPomCycleAdapter.isCycleActive()) {
      savedPomCycleAdapter.removeActiveCycleLayout();
      savedPomCycleAdapter.notifyDataSetChanged();
    }

    if (editCyclesPopupWindow.isShowing()) {
      editCyclesPopupWindow.dismiss();
    }

    timerPopUpWindow.showAtLocation(mainView, Gravity.NO_GRAVITY, 0, 0);
  }

  private void queryAllStatsEntitiesAndAssignTheirValuesToObjects() {
    dailyStatsAccess.assignDayHolderInstanceForSelectedDay(dayOfYear);
    dailyStatsAccess.setDoesDayExistInDatabaseBoolean();
    dailyStatsAccess.insertTotalTimesAndCaloriesBurnedForSpecificDayWithZeroedOutTimesAndCalories(dayOfYear);

    assignValuesToTotalTimesAndCaloriesForCurrentDayVariables();

    dailyStatsAccess.setActivityString(getTdeeActivityStringFromArrayPosition());

    dailyStatsAccess.setStatForEachActivityListForForSingleDayFromDatabase(dayOfYear);
    dailyStatsAccess.setDoesActivityExistsForSpecificDayBoolean();

    dailyStatsAccess.setMetScoreFromSpinner(metScore);
    dailyStatsAccess.setIsActivityCustomBoolean(false);

    dailyStatsAccess.insertTotalTimesAndCaloriesForEachActivityWithinASpecificDayWithZeroedOutTimesAndCalories(dayOfYear);

    //Todo: If insertion of new activity (and starting 0 stats) happens above, we still need an updated retrieval of database. We are using a pre-newly-added-row entity + list instance.
    dailyStatsAccess.setStatForEachActivityListForForSingleDayFromDatabase(dayOfYear);
    dailyStatsAccess.setActivityPositionInListForCurrentDay();
    dailyStatsAccess.assignStatForEachActivityInstanceForSpecificActivityWithinSelectedDay();

    assignValuesToTotalTimesAndCaloriesForSpecificActivityOnCurrentDayVariables();
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

  private void assignValuesToTotalTimesAndCaloriesForCurrentDayVariables() {
    dailyStatsAccess.assignDayHolderInstanceForSelectedDay(dayOfYear);

    if (!dailyStatsAccess.getDoesDayExistInDatabase()) {
      totalSetTimeForCurrentDayInMillis = 0;
      totalBreakTimeForCurrentDayInMillis = 0;
      totalCaloriesBurnedForCurrentDay = 0;
    } else {
      calendar = Calendar.getInstance(Locale.getDefault());
      dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

      totalSetTimeForCurrentDayInMillis = dailyStatsAccess.getTotalSetTimeFromDayHolderEntity();
      totalBreakTimeForCurrentDayInMillis = dailyStatsAccess.getTotalBreakTimeFromDayHolderEntity();
      totalCaloriesBurnedForCurrentDay = dailyStatsAccess.getTotalCaloriesBurnedFromDayHolderEntity();

      totalSetTimeForCurrentDayInMillis = roundDownMillisValuesToSyncTimers(totalSetTimeForCurrentDayInMillis);
      totalBreakTimeForCurrentDayInMillis = roundDownMillisValuesToSyncTimers(totalBreakTimeForCurrentDayInMillis);
    }
  }

  private void assignValuesToTotalTimesAndCaloriesForSpecificActivityOnCurrentDayVariables() {
    Calendar calendar = Calendar.getInstance(Locale.getDefault());
    dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

    if (!dailyStatsAccess.getDoesActivityExistsInDatabaseForSelectedDay()) {
      totalSetTimeForSpecificActivityForCurrentDayInMillis = 0;
      totalBreakTimeForSpecificActivityForCurrentDayInMillis = 0;
      totalCaloriesBurnedForSpecificActivityForCurrentDay = 0;
    } else {
      totalSetTimeForSpecificActivityForCurrentDayInMillis = dailyStatsAccess.getTotalSetTimeForSelectedActivity();
      totalBreakTimeForSpecificActivityForCurrentDayInMillis = dailyStatsAccess.getTotalBreakTimeForSelectedActivity();
      totalCaloriesBurnedForSpecificActivityForCurrentDay = dailyStatsAccess.getTotalCaloriesBurnedForSelectedActivity();

      totalSetTimeForSpecificActivityForCurrentDayInMillis = roundDownMillisValuesToSyncTimers(totalSetTimeForSpecificActivityForCurrentDayInMillis);
      totalBreakTimeForSpecificActivityForCurrentDayInMillis = roundDownMillisValuesToSyncTimers(totalBreakTimeForSpecificActivityForCurrentDayInMillis);
    }

//    Log.i("testActivity", "does activity exist boolean is " + dailyStatsAccess.getDoesActivityExistsInDatabaseForSelectedDay());
//    Log.i("testActivity", "set time for activity pulled is " + totalSetTimeForSpecificActivityForCurrentDayInMillis);
  }

  private void setCyclesAndPomCyclesEntityInstanceToSelectedListPosition(int position) {
    if (mode==1) cycles = cyclesList.get(position);
    if (mode==3) pomCycles = pomCyclesList.get(position);
  }

  //Getting toggle stat from adapter on timer launch and saving that. Retrieve w/ everything else coming back.
  private void saveAddedOrEditedCycleASyncRunnable() {
    Gson gson = new Gson();
    workoutString = "";
    roundTypeString = "";
    pomString = "";

    int cycleID;
    if (mode==1) {
      if (isNewCycle) {
        cycles = new Cycles();
      } else if (cyclesList.size()>0) {
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
          boolean trackingToggle = (savedCycleAdapter.getBooleanDeterminingIfWeAreTrackingActivity(positionOfSelectedCycle));
          cycles.setCurrentlyTrackingCycle(trackingToggle);
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

        Log.i("testTitle", "title being saved is " + cycleTitle);

        if (isNewCycle) {
          cycles.setTimeAdded(System.currentTimeMillis());;
          cyclesDatabase.cyclesDao().insertCycle(cycles);
        } else {
          cyclesDatabase.cyclesDao().updateCycles(cycles);
        }

      }
    }
    if (mode==3) {
      if (isNewCycle) pomCycles = new PomCycles(); else if (pomCyclesList.size()>0) {
        cycleID = pomCyclesList.get(positionOfSelectedCycle).getId();
        pomCycles = cyclesDatabase.cyclesDao().loadSinglePomCycle(cycleID).get(0);
      }
      pomString = gson.toJson(pomValuesTime);
      pomString = friendlyString(pomString);

      if (!pomString.equals("")) {
        pomCycles.setFullCycle(pomString);
        pomCycles.setTimeAccessed(System.currentTimeMillis());
        if (isNewCycle) {
          pomCycles.setTimeAdded(System.currentTimeMillis());
          cyclesDatabase.cyclesDao().insertPomCycle(pomCycles);
        } else {
          cyclesDatabase.cyclesDao().updatePomCycles(pomCycles);
        }
        pomCycles.setTitle(cycleTitle);
      }
    }

    runOnUiThread(()-> {
      cycle_title_textView.setText(cycleTitle);
    });
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

  private void retrieveTotalDailySetAndBreakTimes() {
    if (mode == 1) {
      totalSetTimeForCurrentDayInMillis = dailyStatsAccess.getTotalSetTimeFromDayHolderEntity();
      totalBreakTimeForCurrentDayInMillis = dailyStatsAccess.getTotalBreakTimeFromDayHolderEntity();
    }
  }

  private void setCyclesCompletedTextView() {
    cycles_completed_textView.setText(getString(R.string.cycles_done, cyclesCompleted));
  }

  private void setTotalCycleTimeValuesToTextView() {
    if (mode==1) {
      total_set_time.setText(convertSeconds(totalCycleSetTimeInMillis/1000));
      total_break_time.setText(convertSeconds(totalCycleBreakTimeInMillis/1000));

      Log.i("testTime", "set millis is " + setMillis);
      Log.i("testTime", "total set time millis is " + totalCycleSetTimeInMillis);
      Log.i("testTime", "total divided time millis is " + dividedMillisForTimerDisplay(totalCycleSetTimeInMillis));
      Log.i("testTime", "divided total string time is " + convertSeconds(dividedMillisForTimerDisplay(totalCycleSetTimeInMillis)));
    }
    if (mode==3) {
      total_set_time.setText(convertSeconds(totalCycleWorkTimeInMillis/1000));
      total_break_time.setText(convertSeconds(totalCycleRestTimeInMillis/1000));
    }
  }

  private void setTotalDailyTimeToTextView() {
    dailyTotalTimeTextView.setText(longToStringConverters.convertMillisToHourBasedStringForTimer(totalSetTimeForCurrentDayInMillis));
  }

  private void setTotalDailyCaloriesToTextView() {
    dailyTotalCaloriesTextView.setText(formatCalorieString(totalCaloriesBurnedForCurrentDay));
  }

  private void setTotalActivityTimeToTextView() {
    dailyTotalTimeForSinglefirstMainTextView.setText(longToStringConverters.convertMillisToHourBasedStringForTimer(totalSetTimeForSpecificActivityForCurrentDayInMillis));
  }

  private void setTotalActivityCaloriesToTextView() {
    dailyTotalCaloriesForSinglefirstMainTextView.setText(formatCalorieString(totalCaloriesBurnedForSpecificActivityForCurrentDay));
  }

  private void setAllActivityTimesAndCaloriesToTextViews() {
    setTotalDailyTimeToTextView();
    setTotalDailyCaloriesToTextView();
    setTotalActivityTimeToTextView();
    setTotalActivityCaloriesToTextView();
    dailySingleActivityStringHeader.setText(getTdeeActivityStringFromArrayPosition());
  }

  private void iterationMethodsForTotalTimesAndCaloriesForSelectedDay() {
    if (trackActivityWithinCycle) {
      iterateTotalTimesForSelectedDay(timerRunnableDelay);
      iterateTotalTimesForSelectedActivity(timerRunnableDelay);

      iteratetotalCaloriesForSelectedDuration(timerRunnableDelay);
      iterateTotalCaloriesForSelectedActivity(timerRunnableDelay);
    } else {
      iterateTotalTimesForSelectedCycle(timerRunnableDelay);
    }
  }

  private void iterateTotalTimesForSelectedCycle(long millis) {
    if (mode==1) {
      switch (typeOfRound.get(currentRound)) {
        case 1: case 2:
          totalCycleSetTimeInMillis += millis;
          break;
        case 3: case 4:
          totalCycleBreakTimeInMillis += millis;
          break;
      };
    }
    if (mode==3) {
      switch (pomDotCounter) {
        case 0: case 2: case 4: case 6:
          totalCycleWorkTimeInMillis += millis;
          break;
        case 1: case 3: case 5: case 7:
          totalCycleRestTimeInMillis += millis;
          break;
      }
    }
  }

  private void iterateTotalTimesForSelectedDay(long millis) {
    if (mode==1) {
      switch (typeOfRound.get(currentRound)) {
        case 1: case 2:
          totalSetTimeForCurrentDayInMillis += millis;
          break;
        case 3: case 4:
          totalBreakTimeForCurrentDayInMillis += millis;
          break;
      };
    }
  }

  private void iterateTotalTimesForSelectedActivity(long millis) {
    switch (typeOfRound.get(currentRound)) {
      case 1: case 2:
        totalSetTimeForSpecificActivityForCurrentDayInMillis += millis;
        break;
      case 3: case 4:
        totalBreakTimeForSpecificActivityForCurrentDayInMillis += millis;
        break;
    };
  }

  private void iteratetotalCaloriesForSelectedDuration(long millis) {
    totalCaloriesBurnedForCurrentDay += calculateCaloriesBurnedPerTick(millis);
  }

  private void iterateTotalCaloriesForSelectedActivity(long millis) {
    totalCaloriesBurnedForSpecificActivityForCurrentDay += calculateCaloriesBurnedPerTick(millis);
  }

  private double calculateCaloriesBurnedPerTick(long millisTick) {
    return calculateCaloriesBurnedPerSecond() / millisTick;
  }

  private double calculateCaloriesBurnedPerSecond() {
    return calculateCaloriesBurnedPerMinute(metScore) / 60;
  }

  private double calculateCaloriesBurnedPerMinute(double metValue) {
    double weightConversion = userWeight;
    if (!metricMode) weightConversion = weightConversion / 2.205;
    double caloriesBurnedPerMinute = (metValue * 3.5 * weightConversion) / 200;
    return caloriesBurnedPerMinute;
  }

  private String formatCalorieString(double calories) {
    DecimalFormat df = new DecimalFormat("#.##");
    return df.format(calories);
  }

  private String getLastDisplayedTotalCaloriesString() {
    return dailyTotalTimeTextView.getText().toString();
  }

  private String getLastDisplayedTotalCalorieForSpecificActivityString() {
    return dailyTotalCaloriesTextView.getText().toString();
  }

  private boolean isTextViewVisible(TextView textView) {
    return textView.getVisibility()==View.VISIBLE;
  }

  private void setTotalCaloriesBurnedForCurrentDayValueToLastDisplayedTextView() {
    totalCaloriesBurnedForCurrentDay = Double.parseDouble(getLastDisplayedTotalCaloriesString());
  }

  private void roundDownAllTotalTimeValuesToEnsureSyncing() {
    totalCycleSetTimeInMillis = roundDownMillisValuesToSyncTimers(totalCycleSetTimeInMillis);
    totalCycleBreakTimeInMillis = roundDownMillisValuesToSyncTimers(totalCycleBreakTimeInMillis);
    totalSetTimeForCurrentDayInMillis = roundDownMillisValuesToSyncTimers(totalSetTimeForCurrentDayInMillis);
    totalBreakTimeForCurrentDayInMillis = roundDownMillisValuesToSyncTimers(totalBreakTimeForCurrentDayInMillis);

    Log.i("testTime", "rounded down total set time is " + totalCycleSetTimeInMillis);

    totalSetTimeForSpecificActivityForCurrentDayInMillis = roundDownMillisValuesToSyncTimers(totalSetTimeForSpecificActivityForCurrentDayInMillis);
    totalBreakTimeForSpecificActivityForCurrentDayInMillis = roundDownMillisValuesToSyncTimers(totalBreakTimeForSpecificActivityForCurrentDayInMillis);
  }

  private long roundDownMillisValuesToSyncTimers(long millisToRound) {
    return millisToRound - (millisToRound%1000);
  }

  private double roundDownDoubleValuesToSyncCalories(double caloriesToRound) {
    caloriesToRound += 1;
    DecimalFormat df = new DecimalFormat("#");
    String truncatedCalorieString = df.format(caloriesToRound);

    return Double.parseDouble(truncatedCalorieString);
  }

  private long dividedMillisForTimerDisplay(long millis) {
    return (millis+999)/1000;
  }

  private long dividedMillisForTotalTimesDisplay(long millis) {
    return millis/1000;
  }

  private void updateDailyStatTextViewsIfTimerHasAlsoUpdated() {
    timerTextViewStringOne = (String) timeLeft.getText();

    if (hasTimerTextViewChanged()) {
//      timerTextViewStringTwo = (String) timeLeft.getText();
      timerTextViewStringTwo = timerTextViewStringOne;

      displayCycleOrDailyTotals();
      setTotalDailyTimeToTextView();
      setTotalActivityTimeToTextView();
    }

    setTotalDailyCaloriesToTextView();
    setTotalActivityCaloriesToTextView();
  }

  private boolean hasTimerTextViewChanged() {
    return !timerTextViewStringTwo.equals(timerTextViewStringOne);
  }

  private void syncTimerTextViewStringsForBeginningOfRounds() {
    timerTextViewStringOne = (String) timeLeft.getText();
    timerTextViewStringTwo = (String) timeLeft.getText();
  }

  private void displayCycleOrDailyTotals() {
    if (typeOfTotalTimeToDisplay==TOTAL_CYCLE_TIMES) {
      setTotalCycleTimeValuesToTextView();
      setCyclesCompletedTextView();
    } else if (typeOfTotalTimeToDisplay==TOTAL_DAILY_TIMES){
      setAllActivityTimesAndCaloriesToTextViews();
    }
  }

  private Runnable infinityRunnableForSets() {
    return new Runnable() {
      @Override
      public void run() {
        if (setMillis>=60000 && !textSizeIncreased) {
          changeTextSizeWithAnimator(valueAnimatorDown, timeLeft);
          textSizeIncreased = true;
        }

//        setMillis = (int) (countUpMillisHolder) +  (System.currentTimeMillis() - defaultProgressBarDurationForInfinityRounds);
        setMillis += timerRunnableDelay;

        iterationMethodsForTotalTimesAndCaloriesForSelectedDay();
        timeLeft.setText(convertSeconds(setMillis/1000));

        if (setMillis>=1000) {
          updateDailyStatTextViewsIfTimerHasAlsoUpdated();
        }

        workoutTime.set(workoutTime.size() - numberOfRoundsLeft, (int) setMillis);
        ArrayList<String> convertedWorkoutRoundList = convertMillisIntegerListToTimerStringList(workoutTime);
        dotDraws.updateWorkoutTimes(convertedWorkoutRoundList, typeOfRound);

        setNotificationValues();
        mHandler.postDelayed(this, timerRunnableDelay);
      }
    };
  }

  private Runnable infinityRunnableForBreaks() {
    return new Runnable() {
      @Override
      public void run() {
        if (breakMillis>=60000 && !textSizeIncreased) {
          changeTextSizeWithAnimator(valueAnimatorDown, timeLeft);
          textSizeIncreased = true;
        }
//        breakMillis = (int) (countUpMillisHolder) +  (System.currentTimeMillis() - defaultProgressBarDurationForInfinityRounds);
        breakMillis += timerRunnableDelay;

        iterationMethodsForTotalTimesAndCaloriesForSelectedDay();
        timeLeft.setText(convertSeconds(breakMillis/1000));

        if (breakMillis>=1000) {
          updateDailyStatTextViewsIfTimerHasAlsoUpdated();
        }

        workoutTime.set(workoutTime.size() - numberOfRoundsLeft, (int) breakMillis);
        ArrayList<String> convertedWorkoutRoundList = convertMillisIntegerListToTimerStringList(workoutTime);

        dotDraws.updateWorkoutTimes(convertedWorkoutRoundList, typeOfRound);

        setNotificationValues();
        mHandler.postDelayed(this, timerRunnableDelay);
      }
    };
  }

  private ArrayList<String> convertMillisIntegerListToTimerStringList(ArrayList<Integer> listToConvert) {
    ArrayList<String> listToReturn = new ArrayList<>();

    for (int i=0; i<listToConvert.size(); i++) {
      listToReturn.add(convertSeconds(listToConvert.get(i)/1000));
    }

    return listToReturn;
  }

  private void startSetTimer() {
    boolean willWeChangeTextSize = checkIfRunningTextSizeChange(setMillis);
    long initialMillisValue = setMillis;
    setInitialTextSizeForRounds(setMillis);
    syncTimerTextViewStringsForBeginningOfRounds();

    timer = new CountDownTimer(setMillis, timerRunnableDelay) {
      @Override
      public void onTick(long millisUntilFinished) {
        currentProgressBarValue = (int) objectAnimator.getAnimatedValue();
        setMillis = millisUntilFinished;
        timeLeft.setText(convertSeconds(dividedMillisForTimerDisplay(setMillis)));

        if (setMillis < 500) timerDisabled = true;

        iterationMethodsForTotalTimesAndCaloriesForSelectedDay();
        updateDailyStatTextViewsIfTimerHasAlsoUpdated();

        changeTextSizeOnTimerDigitCountTransitionForModeOne(setMillis);
        dotDraws.reDraw();
        setNotificationValues();
      }

      @Override
      public void onFinish() {
        nextRound(false);
      }
    }.start();
  }

  private void startBreakTimer() {
    boolean willWeChangeTextSize = checkIfRunningTextSizeChange(breakMillis);
    setInitialTextSizeForRounds(breakMillis);
    long initialMillisValue = breakMillis;
    syncTimerTextViewStringsForBeginningOfRounds();

    timer = new CountDownTimer(breakMillis, timerRunnableDelay) {
      @Override
      public void onTick(long millisUntilFinished) {
        currentProgressBarValue = (int) objectAnimator.getAnimatedValue();
        breakMillis = millisUntilFinished;
        timeLeft.setText(convertSeconds(dividedMillisForTimerDisplay(breakMillis)));
        if (breakMillis < 500) timerDisabled = true;

        iterationMethodsForTotalTimesAndCaloriesForSelectedDay();
        updateDailyStatTextViewsIfTimerHasAlsoUpdated();

        changeTextSizeOnTimerDigitCountTransitionForModeOne(breakMillis);
        dotDraws.reDraw();
        setNotificationValues();
      }

      @Override
      public void onFinish() {
        nextRound(false);
      }
    }.start();
  }

  private void startPomTimer() {
    boolean willWeChangeTextSize = checkIfRunningTextSizeChange(pomMillis);
    setInitialTextSizeForRounds(pomMillis);
    long initialMillisValue = pomMillis;
    syncTimerTextViewStringsForBeginningOfRounds();

    timer = new CountDownTimer(pomMillis, timerRunnableDelay) {
      @Override
      public void onTick(long millisUntilFinished) {

        currentProgressBarValue = (int) objectAnimatorPom.getAnimatedValue();
        pomMillis = millisUntilFinished;
        timeLeft.setText(convertSeconds(dividedMillisForTimerDisplay(pomMillis)));
        if (pomMillis < 500) timerDisabled = true;

        iterateTotalTimesForSelectedCycle(timerRunnableDelay);
        updateDailyStatTextViewsIfTimerHasAlsoUpdated();

        changeTextSizeOnTimerDigitCountTransitionForModeThree();

        dotDraws.reDraw();
        setNotificationValues();
      }

      @Override
      public void onFinish() {
        nextPomRound(false);
      }
    }.start();
  }

  private void changeTextSizeOnTimerDigitCountTransitionForModeOne(long setOrBreakMillis) {
    if (!textSizeIncreased && mode==1) {
      if (checkIfRunningTextSizeChange(setOrBreakMillis)) {
        if (setOrBreakMillis < 59000) {
          changeTextSizeWithAnimator(valueAnimatorUp, timeLeft);
          textSizeIncreased = true;
        }
      }
    }
  }

  private void changeTextSizeOnTimerDigitCountTransitionForModeThree() {
    if (!textSizeIncreased && mode==3) {
      if (checkIfRunningTextSizeChange(pomMillis)) {
        if (pomMillis < 59000) {
          changeTextSizeWithAnimator(valueAnimatorUp, timeLeft);
          textSizeIncreased = true;
        }
      }
    }
  }

  private void setInitialTextSizeForRounds(long millis) {
    if (millis>59000) {
      if (screenRatioLayoutChanger.setScreenRatioBasedLayoutChanges()<1.8f) {
        timeLeft.setTextSize(70f);
      } else {
        timeLeft.setTextSize(90f);
      }
      textSizeIncreased = false;
    } else {
      if (screenRatioLayoutChanger.setScreenRatioBasedLayoutChanges()<1.8f) {
        timeLeft.setTextSize(90f);
      } else {
        timeLeft.setTextSize(120f);
      }
      if (mode==4) textSizeIncreased = false;
    }
  }

  public boolean checkIfRunningTextSizeChange(long startingMillis) {
    if (mode==1) {
      if (typeOfRound.get(currentRound)==1 || typeOfRound.get(currentRound)==3) {
        return startingMillis>60000;
      } else {
        return startingMillis<=60000;
      }
    } else if (mode==3) {
      return startingMillis>60000;
    } else {
      return false;
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
    if (screenRatioLayoutChanger.setScreenRatioBasedLayoutChanges()<1.8f) {
      valueAnimatorDown.setFloatValues(90f, 70f);
      valueAnimatorUp.setFloatValues(70f, 90f);
    } else {
      valueAnimatorDown.setFloatValues(120f, 90f);
      valueAnimatorUp.setFloatValues(90f, 120f);
    }
  }

  private void changeTextSizeWithoutAnimator(boolean sizeIncrease) {
    if (screenRatioLayoutChanger.setScreenRatioBasedLayoutChanges()<1.8f) {
      if (sizeIncrease) {
        timeLeft.setTextSize(90f);
      } else {
        timeLeft.setTextSize(70f);
      }
    } else {
      if (sizeIncrease) {
        timeLeft.setTextSize(120f);
      } else {
        timeLeft.setTextSize(90f);
      }
    }
  }

  private void globalNextRoundLogic() {
    setAllActivityTimesAndCaloriesToTextViews();

    progressBar.startAnimation(fadeProgressOut);
    timeLeft.startAnimation(fadeProgressOut);
    currentProgressBarValue = 10000;
    reset.setVisibility(View.INVISIBLE);
    next_round.setEnabled(false);

    roundDownAllTotalTimeValuesToEnsureSyncing();
    AsyncTask.execute(globalSaveTotalTimesAndCaloriesInDatabaseRunnable);
  }

  private void nextRound(boolean endingEarly) {
    globalNextRoundLogic();

    if (numberOfRoundsLeft==0) {
      mHandler.removeCallbacks(endFade);
      resetTimer();
      return;
    }
    mHandler.post(endFade);

    if (endingEarly) {
      if (timer != null) timer.cancel();
      if (objectAnimator != null) objectAnimator.cancel();
      if (!activeCycle) activeCycle = true;
      progressBar.setProgress(0);
    }

    boolean isAlertRepeating = false;

    switch (typeOfRound.get(currentRound)) {
      case 1:
        timeLeft.setText("0");
        changeTextSizeWithoutAnimator(true);
        total_set_time.setText(convertSeconds(dividedMillisForTotalTimesDisplay(totalCycleSetTimeInMillis)));

        if (numberOfRoundsLeft==1 && isLastRoundSoundContinuous) isAlertRepeating = true;
        setEndOfRoundSounds(vibrationSettingForSets, false);
        break;
      case 2:
        mHandler.removeCallbacks(infinityTimerForSets);
        total_set_time.setText(convertSeconds(dividedMillisForTotalTimesDisplay(totalCycleSetTimeInMillis)));

        if (numberOfRoundsLeft==1 && isLastRoundSoundContinuous) isAlertRepeating = true;
        setEndOfRoundSounds(vibrationSettingForSets, false);
        break;
      case 3:
        timeLeft.setText("0");
        changeTextSizeWithoutAnimator(true);
        total_break_time.setText(convertSeconds(dividedMillisForTotalTimesDisplay(totalCycleBreakTimeInMillis)));

        if (numberOfRoundsLeft==1 && isLastRoundSoundContinuous) isAlertRepeating = true;
        setEndOfRoundSounds(vibrationSettingForBreaks, false);
        break;
      case 4:
        mHandler.removeCallbacks(infinityTimerForBreaks);
        total_break_time.setText(convertSeconds(dividedMillisForTotalTimesDisplay(totalCycleBreakTimeInMillis)));

        if (numberOfRoundsLeft==1 && isLastRoundSoundContinuous) isAlertRepeating = true;
        setEndOfRoundSounds(vibrationSettingForBreaks, false);
        break;
    }

    numberOfRoundsLeft--;
    currentRound++;
    mHandler.postDelayed(postRoundRunnableForFirstMode(), 750);

    beginTimerForNextRound = true;
  }

  private void nextPomRound(boolean endingEarly) {
    globalNextRoundLogic();

    switch (pomDotCounter) {
      case 0: case 2: case 4: case 6:
        total_set_time.setText(convertSeconds(dividedMillisForTotalTimesDisplay(totalCycleWorkTimeInMillis)));
        setEndOfRoundSounds(vibrationSettingForWork, false);
        break;
      case 1: case 3: case 5:
        total_break_time.setText(convertSeconds(dividedMillisForTotalTimesDisplay(totalCycleRestTimeInMillis)));
        setEndOfRoundSounds(vibrationSettingForMiniBreaks, false);
        break;
      case 7:
        total_break_time.setText(convertSeconds(dividedMillisForTotalTimesDisplay(totalCycleRestTimeInMillis)));

        boolean isAlertRepeating = false;
        if (isFullBreakSoundContinuous) isAlertRepeating = true;
        setEndOfRoundSounds(vibrationSettingForMiniBreaks, isAlertRepeating);
    }

    if (pomDotCounter==8) {
      mHandler.removeCallbacks(endFade);
      resetTimer();
      return;
    }

    timeLeft.setText("0");
    mHandler.post(endFade);
    pomDotCounter++;

    mHandler.postDelayed(postRoundRunnableForThirdMode(), 750);

    if (endingEarly) {
      if (timer != null) timer.cancel();
      if (objectAnimatorPom != null) objectAnimatorPom.cancel();
      progressBar.setProgress(0);
    }

    beginTimerForNextRound = true;
  }

  private Runnable postRoundRunnableForFirstMode() {
    return new Runnable() {
      @Override
      public void run() {
        defaultProgressBarDurationForInfinityRounds = System.currentTimeMillis();
        dotDraws.updateWorkoutRoundCount(startRounds, numberOfRoundsLeft);
        dotDraws.resetDotAlpha();

        setMillis = 0;
        breakMillis = 0;
        countUpMillisHolder = 0;
        timerIsPaused = false;

        if (numberOfRoundsLeft>0) {
          switch (typeOfRound.get(currentRound)) {
            case 1:
              setMillis = workoutTime.get(workoutTime.size() - numberOfRoundsLeft);
              timeLeft.setText(convertSeconds(dividedMillisForTimerDisplay(setMillis)));
              if (beginTimerForNextRound) {
                startObjectAnimatorAndTotalCycleTimeCounters();
                startSetTimer();
              }
              break;
            case 2:
              timeLeft.setText("0");
              if (beginTimerForNextRound) {
                instantiateAndStartObjectAnimator(30000);
                mHandler.post(infinityTimerForSets);
              }
              break;
            case 3:
              breakMillis = workoutTime.get(workoutTime.size() - numberOfRoundsLeft);
              timeLeft.setText(convertSeconds(dividedMillisForTimerDisplay(breakMillis)));
              if (beginTimerForNextRound) {
                startObjectAnimatorAndTotalCycleTimeCounters();
                startBreakTimer();
              }
              break;
            case 4:
              timeLeft.setText("0");
              if (beginTimerForNextRound) {
                instantiateAndStartObjectAnimator(30000);
                mHandler.post(infinityTimerForBreaks);
              }
              break;
          }
        } else {
          animateEnding();
          progressBar.setProgress(0);
          currentRound = 0;
          timerEnded = true;
          cyclesCompleted++;
          setCyclesCompletedTextView();
        }
        timerDisabled = false;
        next_round.setEnabled(true);
      }
    };
  };

  private Runnable postRoundRunnableForThirdMode() {
    return new Runnable() {
      @Override
      public void run() {
        dotDraws.pomDraw(pomDotCounter, pomValuesTime);
        dotDraws.resetDotAlpha();

        if (pomDotCounter<=7) {
          pomMillis = pomValuesTime.get(pomDotCounter);
          timeLeft.setText(convertSeconds(dividedMillisForTimerDisplay(pomMillis)));
          if (beginTimerForNextRound) {
            startObjectAnimatorAndTotalCycleTimeCounters();
            startPomTimer();
          }
        } else {
          animateEnding();
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

  private void pauseAndResumePomodoroTimer(int pausing) {
    if (!timerDisabled) {
      if (!timerEnded) {
        reset.setText(R.string.reset);

        if (!timerEnded) {
          if (pausing == PAUSING_TIMER) {
            timerIsPaused = true;
            pomMillisUntilFinished = pomMillis;

            if (objectAnimatorPom != null) objectAnimatorPom.pause();
            if (timer != null) timer.cancel();

            reset.setVisibility(View.VISIBLE);
            reset_total_cycle_times.setEnabled(true);
          } else if (pausing == RESUMING_TIMER) {
            startObjectAnimatorAndTotalCycleTimeCounters();
            startPomTimer();

            activeCycle = true;
            timerIsPaused = false;
            reset.setVisibility(View.INVISIBLE);
            reset_total_cycle_times.setEnabled(false);
          }
          AsyncTask.execute(globalSaveTotalTimesAndCaloriesInDatabaseRunnable);
        }
      } else {
        resetTimer();
      }
    }
  }

  private void pauseAndResumeTimer(int pausing) {
    if (!timerDisabled) {
      if (!timerEnded) {
        if (pausing == PAUSING_TIMER) {
          timerIsPaused = true;
          reset.setVisibility(View.VISIBLE);

          if (timer != null) timer.cancel();
          if (objectAnimator != null) objectAnimator.pause();

          reset_total_cycle_times.setEnabled(true);

          switch (typeOfRound.get(currentRound)) {
            case 1:
              setMillisUntilFinished = setMillis;
              break;
            case 2:
              countUpMillisHolder = setMillis;
              mHandler.removeCallbacks(infinityTimerForSets);
              break;
            case 3:
              breakMillisUntilFinished = breakMillis;
              break;
            case 4:
              countUpMillisHolder = breakMillis;
              mHandler.removeCallbacks(infinityTimerForBreaks);
              break;
          }
        } else if (pausing == RESUMING_TIMER) {
          activeCycle = true;
          timerIsPaused = false;
          reset.setVisibility(View.INVISIBLE);
          reset_total_cycle_times.setEnabled(false);

          switch (typeOfRound.get(currentRound)) {
            case 1:
              startObjectAnimatorAndTotalCycleTimeCounters();
              startSetTimer();
              break;
            case 2:
              //Uses the current time as a base for our count-up rounds.
              defaultProgressBarDurationForInfinityRounds = System.currentTimeMillis();
              setMillis = countUpMillisHolder;
              mHandler.post(infinityTimerForSets);
              break;
            case 3:
              startObjectAnimatorAndTotalCycleTimeCounters();
              startBreakTimer();
              break;
            case 4:
              defaultProgressBarDurationForInfinityRounds = System.currentTimeMillis();
              breakMillis = countUpMillisHolder;
              mHandler.post(infinityTimerForBreaks);
              break;
          }
        }
        AsyncTask.execute(globalSaveTotalTimesAndCaloriesInDatabaseRunnable);
      } else {
        resetTimer();
      }
    }
  }

  private void startObjectAnimatorAndTotalCycleTimeCounters() {
    switch (mode) {
      case 1:
        if (typeOfRound.get(currentRound).equals(1)) {
          if (currentProgressBarValue==maxProgress) {
            timerIsPaused = false;
            instantiateAndStartObjectAnimator(setMillis);
          } else {
            setMillis = setMillisUntilFinished;
            if (objectAnimator != null) objectAnimator.resume();
          }
        } else if (typeOfRound.get(currentRound).equals(3)) {
          if (currentProgressBarValue==maxProgress) {
            timerIsPaused = false;
            instantiateAndStartObjectAnimator(breakMillis);
          } else {
            breakMillis = breakMillisUntilFinished;
            if (objectAnimator != null) objectAnimator.resume();
          }
        }
        break;
      case 3:
        if (currentProgressBarValue==maxProgress){
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
    if (mode==1) {
      objectAnimator = ObjectAnimator.ofInt(progressBar, "progress",maxProgress, 0);
      objectAnimator.setInterpolator(new LinearInterpolator());
      objectAnimator.setDuration(duration);
      objectAnimator.start();
    }
    if (mode==3) {
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
    toggleInfinityRounds.setAlpha(0.3f);
    removeTdeeActivityImageView.setVisibility(View.INVISIBLE);
    setDefaultTimerValuesAndTheirEditTextViews();

    timeLeft.setText(timeLeftValueHolder);
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
        savedCycleRecycler.setVisibility(View.VISIBLE);
        savedPomCycleRecycler.setVisibility(View.GONE);

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
        roundRecyclerTwo.setVisibility(View.GONE);
        roundListDivider.setVisibility(View.GONE);
        savedCycleRecycler.setVisibility(View.GONE);
        savedPomCycleRecycler.setVisibility(View.VISIBLE);
        addTDEEfirstMainTextView.setVisibility(View.INVISIBLE);

        total_set_header.setText(R.string.total_work);
        break;
    }
  }

  private void getTimerVariablesForEachMode() {
    if (mode==1) {
      currentProgressBarValue = sharedPreferences.getInt("savedProgressBarValueForModeOne", 10000);
      timeLeftValueHolder = sharedPreferences.getString("timeLeftValueForModeOne", "");
      positionOfSelectedCycle = sharedPreferences.getInt("positionOfSelectedCycleForModeOne", 0);
      timerIsPaused = sharedPreferences.getBoolean("modeOneTimerPaused", false);
      timerEnded = sharedPreferences.getBoolean("modeOneTimerEnded", false);
      timerDisabled = sharedPreferences.getBoolean("modeOneTimerDisabled", false);
    }

    if (mode==3) {
      currentProgressBarValue = sharedPreferences.getInt("savedProgressBarValueForModeThree", 10000);
      timeLeftValueHolder = sharedPreferences.getString("timeLeftValueForModeThree", "");
      positionOfSelectedCycle = sharedPreferences.getInt("positionOfSelectedCycleForModeThree", 0);
      timerIsPaused = sharedPreferences.getBoolean("modeThreeTimerPaused", false);
      timerEnded = sharedPreferences.getBoolean("modeThreeTimerEnded", false);
      timerDisabled = sharedPreferences.getBoolean("modeThreeTimerDisabled", false);
    }
  }

  private void populateTimerUI() {
    beginTimerForNextRound = true;
    cycles_completed_textView.setText(R.string.cycles_done);

//    dotDraws.resetDotAlpha();
    toggleLayoutParamsForCyclesAndStopwatch();
    setCyclesCompletedTextView();

    switch (mode) {
      case 1:
        for (int i=0; i<workoutTime.size(); i++) {
          if (typeOfRound.get(i)==2 || typeOfRound.get(i)==4) workoutTime.set(i, 0);
        }

        startRounds = workoutTime.size();
        numberOfRoundsLeft = startRounds;

        workoutTime.set(workoutTime.size() - numberOfRoundsLeft, (int) setMillis);
        ArrayList<String> convertedWorkoutRoundList = convertMillisIntegerListToTimerStringList(workoutTime);

        dotDraws.updateWorkoutTimes(convertedWorkoutRoundList, typeOfRound);

        dotDraws.updateWorkoutRoundCount(startRounds, numberOfRoundsLeft);

        if (workoutTime.size()>0) {
          switch (typeOfRound.get(0)) {
            case 1:
              setMillis = workoutTime.get(0);
              timeLeft.setText(convertSeconds((dividedMillisForTimerDisplay(setMillis))));
              setInitialTextSizeForRounds(setMillis);
              break;
            case 3:
              breakMillis = workoutTime.get(0);
              timeLeft.setText(convertSeconds(((dividedMillisForTimerDisplay(breakMillis)))));
              setInitialTextSizeForRounds(breakMillis);
              break;
            case 2:
            case 4:
              setMillis = 0;
              breakMillis = 0;
              timeLeft.setText("0");
              setInitialTextSizeForRounds(0);
              break;
          }
          toggleCycleTimeTextViewSizes(trackActivityWithinCycle);
        }
        break;
      case 3:
        if (pomValuesTime.size() > 0) {
          pomMillis = pomValuesTime.get(0);
          timeLeft.setText(convertSeconds(dividedMillisForTimerDisplay(pomMillis)));
          dotDraws.pomDraw(pomDotCounter,pomValuesTime);
          setInitialTextSizeForRounds(pomMillis);
        }
        toggleCycleTimeTextViewSizes(false);
        break;
    }

    dotDraws.resetDotAlpha();
  }

  private void resetTimer() {
    activeCycle = false;
    vibrator.cancel();
    if (timer != null) timer.cancel();
    if (endAnimation!=null) endAnimation.cancel();
    if (mediaPlayer.isPlaying()) {
      mediaPlayer.stop();
      mediaPlayer.reset();
    }

    timerIsPaused = true;
    timerEnded = false;
    timerDisabled = false;
    next_round.setEnabled(true);

    progressBar.setProgress(10000);
    currentProgressBarValue = 10000;
    delayBeforeTimerBeginsSyncingWithTotalTimeStats = 1000;

    reset.setVisibility(View.INVISIBLE);
    reset_total_cycle_times.setEnabled(true);

    switch (mode) {
      case 1:
        switch (typeOfRound.get(0)) {
          case 1: setMillis = workoutTime.get(0); break;
          case 2: setMillis = 0; break;
          case 3: breakMillis = workoutTime.get(0); break;
          case 4: breakMillis = 0; break;
        };
        countUpMillisHolder = 0;
        startRounds = workoutTime.size();
        numberOfRoundsLeft = startRounds;
        currentRound = 0;

        mHandler.removeCallbacks(infinityTimerForSets);
        mHandler.removeCallbacks(infinityTimerForBreaks);

        if (objectAnimator != null) objectAnimator.cancel();

        if (savedCycleAdapter.isCycleActive()==true) {
          savedCycleAdapter.removeActiveCycleLayout();
          savedCycleAdapter.notifyDataSetChanged();
        }
        break;
      case 3:
        pomDotCounter = 0;
        if (objectAnimatorPom != null) objectAnimatorPom.cancel();

        if (savedPomCycleAdapter.isCycleActive()==true) {
          savedPomCycleAdapter.removeActiveCycleLayout();
          savedPomCycleAdapter.notifyDataSetChanged();
        }
        break;
    }
    populateTimerUI();
    setNotificationValues();
  }

  private void sendPhoneResolutionToDotDrawsClass() {
    DisplayMetrics metrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(metrics);

    phoneHeight = metrics.heightPixels;
    phoneWidth = metrics.widthPixels;

    dotDraws.receivePhoneDimensions(phoneHeight, phoneWidth);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private void toggleLayoutParamsForCyclesAndStopwatch() {
    if (mode!=4) {
      cycleTitleLayoutParams.topMargin = convertDensityPixelsToScalable(30);
      cyclesCompletedLayoutParams.topMargin = convertDensityPixelsToScalable(12);
    } else {
      cyclesCompletedLayoutParams.topMargin = 0;
      cycleTitleLayoutParams.topMargin = -25;
    }
  }

  private void toggleEditPopUpViewsForAddingActivity(boolean activityExists) {
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
      dailyTotalTimeForSinglefirstMainTextViewHeader.setVisibility(View.INVISIBLE);
      dailyTotalTimeForSinglefirstMainTextView.setVisibility(View.INVISIBLE);
      dailyTotalCaloriesForSinglefirstMainTextViewHeader.setVisibility(View.INVISIBLE);
      dailyTotalCaloriesForSinglefirstMainTextView.setVisibility(View.INVISIBLE);

      setTotalCycleTimeValuesToTextView();
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
      dailyTotalTimeForSinglefirstMainTextViewHeader.setVisibility(View.VISIBLE);
      dailyTotalTimeForSinglefirstMainTextView.setVisibility(View.VISIBLE);
      dailyTotalCaloriesForSinglefirstMainTextViewHeader.setVisibility(View.VISIBLE);
      dailyTotalCaloriesForSinglefirstMainTextView.setVisibility(View.VISIBLE);

      setAllActivityTimesAndCaloriesToTextViews();
    }
  }

  private void toggleCycleTimeTextViewSizes(boolean trackingActivity) {
    if (!trackingActivity) {
      cycles_completed_textView.setTextSize(28);

      total_set_header.setTextSize(28);
      total_set_time.setTextSize(26);
      total_break_header.setTextSize(28);
      total_break_time.setTextSize(26);
    } else {
      cycles_completed_textView.setTextSize(24);

      total_set_header.setTextSize(22);
      total_set_time.setTextSize(20);
      total_break_header.setTextSize(22);
      total_break_time.setTextSize(20);
    }
  }

  public String getTdeeActivityStringFromArrayPosition() {
    ArrayList<String[]> subCategoryArray = tDEEChosenActivitySpinnerValues.subCategoryListOfStringArrays;
    String[] subCategoryList = subCategoryArray.get(selectedTdeeCategoryPosition);
    return (String) subCategoryList[selectedTdeeSubCategoryPosition];
  }

  private void tdeeCategorySpinnerTouchActions() {
    selectedTdeeCategoryPosition = tdee_category_spinner.getSelectedItemPosition();

    tdeeSubCategoryAdapter.clear();
    tdeeSubCategoryAdapter.addAll(tDEEChosenActivitySpinnerValues.subCategoryListOfStringArrays.get(selectedTdeeCategoryPosition));

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
//    cycles = cyclesList.get(positionOfSelectedCycle);

    selectedTdeeCategoryPosition = cycles.getTdeeCatPosition();
    selectedTdeeSubCategoryPosition = cycles.getTdeeSubCatPosition();

    metScore = retrieveMetScoreFromSubCategoryPosition();
  }

  private void setMetScoreTextViewInAddTdeePopUp() {
    metScore = retrieveMetScoreFromSubCategoryPosition();
    metScoreTextView.setText(getString(R.string.met_score, String.valueOf(metScore)));
  }

  private double retrieveMetScoreFromSubCategoryPosition() {
    String[] valueArray = tDEEChosenActivitySpinnerValues.subValueListOfStringArrays.get(selectedTdeeCategoryPosition);
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

  private void showToastIfNoneActive (String message){
    if (mToast != null) {
      mToast.cancel();
    }
    mToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
    mToast.show();
  }

  private void logCycleHighlights() {
    Log.i("testHighlight", "highlisted list is " + receivedHighlightPositions);
  }

  private void logCycleHighlightsRoundValues() {
    for (int i=0; i<receivedHighlightPositions.size(); i++) {
      Log.i("testHighlight", "workout rounds for highlighted cycles are " + workoutCyclesArray.get(receivedHighlightPositions.get(i)));
    }
  }

  private void logCyclesActivitiesFromDatabase() {
    AsyncTask.execute(()-> {
      for (int i=0; i<cyclesList.size(); i++) {
        Log.i("testCycleDb", "Activity String is " + cyclesList.get(i).getActivityString());
      }
    });
  }

  private void logCyclesTitlesFromDatabase() {
    AsyncTask.execute(()-> {
      for (int i=0; i<cyclesList.size(); i++) {
        Log.i("testCycleDb", "Title String is " + cyclesList.get(i).getTitle());
      }
    });
  }

  private void logStatsForEachActivityDatabase(boolean currentDayOnly) {
    AsyncTask.execute(()->{
      Calendar calendar = Calendar.getInstance(Locale.getDefault());
      dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

      List<StatsForEachActivity> listOfActivities = new ArrayList<>();
      List<DayHolder> listOfDays = new ArrayList<>();

      for (int i=0; i<listOfActivities.size(); i++) {
        Log.i("testStatsDb", "entry position is " + i);
        Log.i("testStatsDb", "Day ID is " + listOfActivities.get(i).getUniqueIdTiedToTheSelectedActivity());
        Log.i("testStatsDb", "Activity String is " + listOfActivities.get(i).getActivity());
        Log.i("testStatsDb", "Set time elapsed for activity is " + listOfActivities.get(i).getTotalSetTimeForEachActivity());
      }

      if (listOfActivities.size()==0) {
        Log.i("testStatsDb", "Empty list!");
      }

      for (int i=0; i<listOfDays.size(); i++) {
        Log.i("testStatsDb", "For day " + listOfDays.get(i).getDayId() + " total set time is " + listOfDays.get(i).getTotalSetTime());
      }
    });
  }
}