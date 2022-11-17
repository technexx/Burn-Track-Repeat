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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
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
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
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


public class MainActivity extends AppCompatActivity implements SavedCycleAdapter.onPauseOrResumeListener, SavedCycleAdapter.onCycleClickListener, SavedCycleAdapter.onHighlightListener, SavedCycleAdapter.onTdeeModeToggle, SavedPomCycleAdapter.onPauseOrResumeListener, SavedPomCycleAdapter.onCycleClickListener, SavedPomCycleAdapter.onHighlightListener, CycleRoundsAdapter.onFadeFinished, CycleRoundsAdapter.onRoundSelected, SavedCycleAdapter.onResumeOrResetCycle, SavedPomCycleAdapter.onResumeOrResetCycle, RootSettingsFragment.onChangedSettings, SoundSettingsFragment.onChangedSoundSetting, ColorSettingsFragment.onChangedColorSetting, DailyStatsFragment.changeOnOptionsItemSelectedMenu, DailyStatsFragment.changeSortMenu, DotsAdapter.sendDotAlpha, PomDotsAdapter.sendPomDotAlpha {

  StateOfTimers stateOfTimers;
  LongToStringConverters longToStringConverters;

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
  View topOfMainActivityView;
  View bottomEditTitleDividerView;

  Calendar calendar;

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
  RecyclerView pomRoundRecycler;
  RecyclerView savedCycleRecycler;
  RecyclerView savedPomCycleRecycler;
  CycleRoundsAdapter cycleRoundsAdapter;

  SavedCycleAdapter savedCycleAdapter;
  SavedPomCycleAdapter savedPomCycleAdapter;

  LinearLayoutManager workoutCyclesRecyclerLayoutManager;
  LinearLayoutManager roundRecyclerOneLayoutManager;
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
  PopupWindow deleteCyclePopupWindow;
  PopupWindow editCyclesPopupWindow;
  PopupWindow aboutSettingsPopUpWindow;

  ConstraintLayout editPopUpLayout;

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

  int positionOfSelectedCycleForModeOne;
  int positionOfSelectedCycleForModeThree;
  int primaryIdOfCycleRowSelected;
  int primaryIdOfPomCycleRowSelected;

  int sortedPositionOfSelectedCycleForModeOne;
  int sortedPositionOfSelectedCycleForModeThree;

  String cycleTitle = "";
  List<Integer> receivedHighlightPositions;

  ImageButton addRoundToCycleButton;
  ImageButton subtractRoundFromCycleButton;
  ImageButton toggleInfinityRounds;
  ImageButton buttonToLaunchTimerFromEditPopUp;

  ArrayList<Integer> workoutTimeIntegerArray;
  ArrayList<String> convertedWorkoutTimeStringArray;
  ArrayList<String> workoutCyclesArray;
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
  ArrayList<Integer> workoutIntegerListOfRoundTypeForFirstAdapter;

  int setTimeValueEnteredWithKeypad;
  int breakTimeValueEnteredWithKeypad;
  int pomWorkValueEnteredWithKeyPad;
  int pomMiniBreakValueEnteredWithKeyPad;
  int pomFullBreakValueEnteredWithKeyPad;
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
  int roundSelectedPosition;

  int CYCLE_LAUNCHED_FROM_RECYCLER_VIEW = 0;
  int CYCLES_LAUNCHED_FROM_EDIT_POPUP = 1;

  PopupWindow timerPopUpWindow;
  View timerPopUpView;
  PopupWindow stopWatchPopUpWindow;
  View stopWatchPopUpView;

  ProgressBar progressBar;
  ProgressBar progressBarForPom;
  TextView timeLeftForCyclesTimer;
  TextView timeLeftForPomCyclesTimer;
  CountDownTimer modeOneCountdownTimer;
  CountDownTimer modeThreeCountDownTimer;
  TextView resetButtonForCycles;
  TextView resetButtonForPomCycles;
  ObjectAnimator objectAnimator;
  ObjectAnimator objectAnimatorPom;
  Animation endAnimationForCyclesTimer;
  Animation endAnimationForPomCyclesTimer;
  Animation endAnimationForStopwatch;

  TextView tracking_daily_stats_header_textView;
  TextView cycle_title_textView;
  TextView cycle_title_textView_with_activity;
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
  int currentProgressBarValueForModeOne = 10000;
  int currentProgressBarValueForModeThree = 10000;
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

  int startRoundsForModeOne;
  int numberOfRoundsLeftForModeOne;
  int currentRoundForModeOne;

  int startRoundsForModeThree;
  int numberOfRoundsLeftForModeThree;
  int currentRoundForModeThree;

  LinearLayoutManager lapRecyclerLayoutManager;

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

  Runnable runnableForSetAndBreakTotalTimes;
  Runnable runnableForWorkAndRestTotalTimes;
  Runnable infinityRunnableForDailyActivityTimer;

  Runnable runnableForRecyclerViewTimesForModeOne;
  Runnable runnableForRecyclerViewTimesForModeThree;

  Runnable globalNotficationsRunnable;

  int CYCLE_TIME_TO_ITERATE;
  int POM_CYCLE_TIME_TO_ITERATE;

  int CYCLE_SETS = 0;
  int CYCLE_BREAKS = 1;
  int POM_CYCLE_WORK = 0;
  int POM_CYCLE_REST = 2;

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
  Button addActivityConfirmButton;
  Button cancelActivityConfirmButton;

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

  String savedTotalSetTime;
  String savedTotalBreakTime;
  String savedTotalWorkTime;
  String savedTotalRestTime;
  String savedTotalDailyTimeString;
  String savedSingleActivityString;

  ActionBar mainActionBar;
  ActionBar settingsActionBar;

  boolean resetCycleTimeVarsWithinRunnable;

  //Todo: Copy cycle layout to /1920h
  //Todo: Total daily time/cals not super clear w/ out date as header.
  //Todo: Cycle can default to not tracking right after editing one to add activity.
  //Todo: Should vertically center title + round string in cycles recycler - looks too wide w/ few rounds + longer activity String.
  //Todo: Test fresh install add/sub cycles etc. and for Pom.

  //Todo: After adding current app screenshots, update resume on job sites.
  //Todo: Okay to release a 1.0.1 version!

  //Todo: Anim stuff when resuming app.
  //Todo: Sort launch positions error when sorting, now.
      //Todo: Sort puts list in X order, but all other queries use default order.
  //Todo: Adjust timer popUp margins for <1920h layout.
  //Todo: Total activity time can be 1 more than aggregate.
  //Todo: May have an issue w/ adding activities to databases if launching and iterating multiple ones in a row.

  //Todo: Ghosting at colons in editing activities.
      //Todo: Round list ghosting appears at end of round only on Pixel (as opposed to between "-" and end on Moto).
  //Todo: Moto colors in stats fragment looks off.

  //Todo: Change back pom cycle times to original (non-testing).
  //Todo: Deep test of all database stuff.
  //Todo: Test db saves/deletions/etc. on different years. Include food overwrites add/updates.
  //Todo: Test Moto G5 + low res nexus emulator.
  //Todo: Test minimized vibrations on <26 api. Test all vibrations/ringtones again.
  //Todo: Run code inspector for redundancies, etc.
  //Todo: Rename app, of course.
  //Todo: Backup cloud option.

  //Todo: Closing app briefly display notifications (onStop/onDestroy)
  //Todo: Had a bug of timer displaying and iterating +1 second from time listed in edit popUp's round.
  //Todo: Activity time runnable display will skip if removed/re-posted after in-transition day change.
  //Todo: Had a bug of iterating calories but not time.
      //Todo: Check updateDailyStatTextViewsIfTimerHasAlsoUpdated() if it happens again.
  //Todo: Had instance of exiting stats frag retaining its onOptionsSelected menu. Haven't been able to replicate.
  //Todo: Anim reset at end of cycle when clicking in and out of timer likely due to visibility set to GONE and VISIBLE again.

  //Todo: Stats for Pomodoro for future addition.
  //Todo: Option for ringtone selection.
  //Todo: Add Day/Night modes.
  //Todo: storeDailyTimesForCycleResuming() and setStoredDailyTimesForCycleResuming() commented out when using ms for daily stats.
  //Todo: Tablet display needs work.
      //Todo: Can be done later. Not meant for tablets.

  //Drawable height may sync w/ textView height for alignment.
  //We can also commit just specific files, remember!
  //REMINDER: Try next app w/ Kotlin + learn Kotlin.

  @Override
  public void onResume() {
    super.onResume();
    //Prevents crashing on resume.
    setVisible(true);

    dismissNotification = true;
    notificationManagerCompat.cancel(1);
    mHandler.removeCallbacks(globalNotficationsRunnable);

    mHandler.postDelayed(() -> {
      if (editCyclesPopupWindow.isShowing()) {
        inputMethodManager.hideSoftInputFromWindow(editCyclesPopupView.getWindowToken(), 0);
      }
      if (timerPopUpWindow.isShowing()) {
        inputMethodManager.hideSoftInputFromWindow(timerPopUpView.getWindowToken(), 0);
      }
      if (stopWatchPopUpWindow.isShowing()) {
        inputMethodManager.hideSoftInputFromWindow(stopWatchPopUpView.getWindowToken(), 0);
      }

      if (deleteCyclePopupWindow.isShowing()) {
        inputMethodManager.hideSoftInputFromWindow(deleteCyclePopupView.getWindowToken(), 0);
      }
      if (sortPopupWindow.isShowing()) {
        inputMethodManager.hideSoftInputFromWindow(sortCyclePopupView.getWindowToken(), 0);
      }
      if (addTdeePopUpWindow.isShowing()) {
        inputMethodManager.hideSoftInputFromWindow(addTDEEPopUpView.getWindowToken(), 0);
      }
      if (aboutSettingsPopUpWindow.isShowing()) {
        inputMethodManager.hideSoftInputFromWindow(aboutSettingsPopUpView.getWindowToken(), 0);
      }

    }, 300);


  }

  @Override
  public void onPause() {
    super.onPause();

//    timerPopUpWindow.setAnimationStyle(android.R.style.Animation);
//    overridePendingTransition(0, 0);

  }

  @Override
  public void onStop() {
    super.onStop();
    setVisible(false);

    dismissNotification = false;

    if (stateOfTimers.isModeOneTimerActive() || stateOfTimers.isModeThreeTimerActive() || stateOfTimers.isStopWatchTimerActive()) {
      //Shows even if paused and timer does not change.
      setNotificationValues();
      //Runnable to display in sync w/ timer change.
      globalNotficationsRunnable = notifcationsRunnable();
      mHandler.post(globalNotficationsRunnable);
    }

    mHandler.postDelayed(() -> {
      if (timerPopUpWindow.isShowing()) {
//        timerPopUpWindow.dismiss();
//        timerPopUpWindow.setAnimationStyle(R.style.WindowAnimation);
//        timerPopUpWindow.setAnimationStyle(android.R.style.Animation);
//        resumeOrResetCycle(RESUMING_CYCLE_FROM_ADAPTER);
      }
    }, 500);

  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    notificationManagerCompat.cancel(1);
    mHandler.removeCallbacks(globalNotficationsRunnable);

  }

  //Remember, this does not execute if we are dismissing a popUp.
  @Override
  public void onBackPressed() {
    if (!timerPopUpIsVisible && mainActivityFragmentFrameLayout.getVisibility() == View.INVISIBLE) {
      return;
    }

    if (timerPopUpWindow.isShowing()) {
      timerPopUpDismissalLogic();
    }

    if (editCyclesPopupWindow.isShowing()) {
      editCyclesPopUpDismissalLogic();
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
          toggleSortButtonBasedOnIfCycleIsActive();
        }

        if (rootSettingsFragment.isVisible()) {
          sortButton.setVisibility(View.VISIBLE);
        }

        setTypeOfOnOptionsSelectedMenu(DEFAULT_MENU);
        toggleSortMenuViewBetweenCyclesAndActivities(SORTING_CYCLES);
      }

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
        disableHighlightModeIfActive();
        break;
      case R.id.global_settings:
        launchGlobalSettingsFragment();
        disableHighlightModeIfActive();
        break;
      case R.id.delete_all_cycles:
        if (mode == 1 && workoutCyclesArray.size() == 0 || mode == 3 && pomArray.size() == 0) {
          showToastIfNoneActive("Nothing to delete!");
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
        if (dailyStatsFragment.getSelectedTab() == 2) {
          delete_all_text.setText(R.string.delete_activities_and_foods_from_selected_duration);
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
        if (dailyStatsFragment.getSelectedTab() == 2) {
          delete_all_text.setText(R.string.delete_all_activities_and_foods);
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

      sortButton.setVisibility(View.GONE);
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
            .setCustomAnimations(
                    R.anim.slide_in_from_left_short,  // enter
                    R.anim.slide_out_from_right,  // exit
                    R.anim.slide_in_from_left_short,   // popEnter (backstack)
                    R.anim.slide_out_from_right  // popExit (backstack))
            )
            .replace(R.id.settings_fragment_frameLayout, fragmentToReplace)
            .commit();
  }

  @Override
  public void changeSoundSetting(int typeOfRound, int settingNumber) {
    assignSoundSettingValues(typeOfRound, settingNumber);
  }

  @Override
  public void changeColorSetting(int receivedMode, int typeOFRound, int settingNumber) {
    cycleRoundsAdapter.setColorSettingsFromMainActivity(typeOFRound, settingNumber);

    if (receivedMode == 1) {
      savedCycleAdapter.setColorSettingsFromMainActivity(typeOFRound, settingNumber);
      savedCycleAdapter.notifyDataSetChanged();

      dotsAdapter.setColorSettingsFromMainActivity(typeOFRound, settingNumber);
    }

    if (receivedMode == 3) {
      savedPomCycleAdapter.setColorSettingsFromMainActivity(typeOFRound, settingNumber);
      savedPomCycleAdapter.notifyDataSetChanged();

      pomDotsAdapter.setColorSettingsFromMainActivity(typeOFRound, settingNumber);
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
  public void onPauseOrResume(boolean timerIsPaused) {
    if (mode == 1) {
      if (numberOfRoundsLeftForModeOne > 0) {
        if (timerIsPaused) {
          pauseAndResumeTimer(RESUMING_TIMER);
        } else {
          pauseAndResumeTimer(PAUSING_TIMER);
        }
        savedCycleAdapter.notifyDataSetChanged();
      }
    }

    if (mode == 3) {
      if (numberOfRoundsLeftForModeThree > 0) {
        if (timerIsPaused) {
          pauseAndResumePomodoroTimer(RESUMING_TIMER);
        } else {
          pauseAndResumePomodoroTimer(PAUSING_TIMER);
        }
      }
    }
    savedPomCycleAdapter.notifyDataSetChanged();
  }

  @Override
  public void ResumeOrResetCycle(int resumingOrResetting) {
    if (resumingOrResetting == RESUMING_CYCLE_FROM_ADAPTER) {
      resumeOrResetCycle(RESUMING_CYCLE_FROM_ADAPTER);
    } else if (resumingOrResetting == RESETTING_CYCLE_FROM_ADAPTER) {
      resumeOrResetCycle(RESETTING_CYCLE_FROM_ADAPTER);
    }
  }

  private void resumeOrResetCycle(int resumeOrReset) {
    if (resumeOrReset == RESUMING_CYCLE_FROM_ADAPTER) {
      if (mode == 1) {
        timeLeftForPomCyclesTimer.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        timeLeftForCyclesTimer.setVisibility(View.VISIBLE);
        progressBar.setProgress(currentProgressBarValueForModeOne);

        if (trackActivityWithinCycle) {
          setAllActivityTimesAndCaloriesToTextViews();
        } else {
          setTotalCycleTimeValuesToTextView();
//          setStoredSetAndBreakTimeOnCycleResume();
        }

        toggleViewsForTotalDailyAndCycleTimes(trackActivityWithinCycle);

        if (typeOfRound.get(currentRoundForModeOne) == 1 || typeOfRound.get(currentRoundForModeOne) == 2) {
          if (numberOfRoundsLeftForModeOne > 0) {
            timeLeftForCyclesTimer.setText(longToStringConverters.convertSecondsToMinutesBasedString(dividedMillisForTimerDisplay(setMillis)));
          }
          changeTextSizeWithoutAnimator(setMillis);
        } else {
          if (numberOfRoundsLeftForModeOne > 0) {
            timeLeftForCyclesTimer.setText(longToStringConverters.convertSecondsToMinutesBasedString(dividedMillisForTimerDisplay(breakMillis)));
          }
          changeTextSizeWithoutAnimator(breakMillis);
        }

        AsyncTask.execute(()-> {
          if (trackActivityWithinCycle && dailyStatsFragment.getHaveStatsBeenEditedForCurrentDay()) {
            insertActivityIntoDatabaseAndAssignItsValueToObjects();
            dailyStatsFragment.setStatsHaveBeenEditedForCurrentDay(false);

            runOnUiThread(()-> {
              retrieveTotalTimesAndCaloriesForSpecificActivityOnCurrentDayVariables();
              setAllActivityTimesAndCaloriesToTextViews();
            });
          }
        });
      }

      if (mode == 3) {
        progressBarForPom.setVisibility(View.VISIBLE);
        progressBarForPom.setProgress(currentProgressBarValueForModeThree);
        timeLeftForPomCyclesTimer.setVisibility(View.VISIBLE);
        setTotalCycleTimeValuesToTextView();
        // setStoredSetAndBreakTimeOnPomCycleResume();

        toggleViewsForTotalDailyAndCycleTimes(false);

        if (numberOfRoundsLeftForModeThree > 0) {
          timeLeftForPomCyclesTimer.setText(longToStringConverters.convertSecondsToMinutesBasedString(dividedMillisForTimerDisplay(pomMillis)));
        }
        changeTextSizeWithoutAnimator(pomMillis);

        savedPomCycleAdapter.setIsConfirmStringVisible(false);
      }

      timerPopUpWindow.showAtLocation(mainView, Gravity.NO_GRAVITY, 0, 0);

    } else if (resumeOrReset == RESETTING_CYCLE_FROM_ADAPTER) {
      if (mode == 1) {
        mHandler.removeCallbacks(runnableForRecyclerViewTimesForModeOne);
        savedCycleAdapter.removeCycleAsActive();
        savedCycleAdapter.notifyDataSetChanged();
        resetCyclesTimer();
      }
      if (mode == 3) {
        savedPomCycleAdapter.setIsConfirmStringVisible(false);

        mHandler.removeCallbacks(runnableForRecyclerViewTimesForModeThree);
        savedPomCycleAdapter.removeCycleAsActive();
        savedPomCycleAdapter.notifyDataSetChanged();
        resetPomCyclesTimer();
      }
    }

    toggleSortButtonBasedOnIfCycleIsActive();
  }

  private void deleteLastAccessedActivityCycleIfItHasZeroTime(int positionOfCycle) {
    if (dailyStatsAccess.getTotalSetTimeForSelectedActivity() == 0) {
      AsyncTask.execute(() -> {
        dailyStatsAccess.deleteTotalTimesAndCaloriesFromCurrentEntity();
      });
    }
  }

  private void toggleSortButtonBasedOnIfCycleIsActive() {
    if (mode == 1) {
      if (savedCycleAdapter.isCycleActive()) {
        sortButton.setEnabled(false);
      } else {
        sortButton.setEnabled(true);
      }
      if (mode==3) {
        if (savedPomCycleAdapter.isCycleActive()) {
          sortButton.setEnabled(false);
        } else {
          sortButton.setEnabled(true);
        }
      }
    }
    Log.i("testSort", "button is " + sortButton.isEnabled());
  }

//  private void storeDailyTimesForCycleResuming() {
//    savedTotalDailyTimeString = (String) dailyTotalTimeTextView.getText();
//    savedSingleActivityString = (String) dailyTotalTimeForSingleActivityTextView.getText();
//  }
//
//  private void setStoredDailyTimesOnCycleResume() {
//    dailyTotalTimeTextView.setText(savedTotalDailyTimeString);
//    dailyTotalTimeForSingleActivityTextView.setText(savedSingleActivityString);
//  }

  private void storeSetAndBreakTimeForCycleResuming() {
    if (mode==1) {
      savedTotalSetTime = (String) total_set_time.getText();
      savedTotalBreakTime = (String) total_break_time.getText();
    }
  }

  private void setStoredSetAndBreakTimeOnCycleResume() {
    if (mode==1) {
      total_set_time.setText(savedTotalSetTime);
      total_break_time.setText(savedTotalBreakTime);
    }
  }

  private void storeSetAndBreakTimeForPomCycleResuming() {
    savedTotalWorkTime = (String) total_set_time.getText();
    savedTotalRestTime = (String) total_break_time.getText();
  }

  private void setStoredSetAndBreakTimeOnPomCycleResume() {
    total_set_time.setText(savedTotalWorkTime);
    total_break_time.setText(savedTotalRestTime);
  }

  @Override
  public void toggleTdeeMode(int positionToToggle) {
    savedCycleAdapter.modifyActiveTdeeModeToggleList(positionToToggle);
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

  private void setPrimaryIdsForSelectedCycleEntity() {
    if (mode == 1) primaryIdOfCycleRowSelected = cycles.getId();
    if (mode == 3) primaryIdOfPomCycleRowSelected = pomCycles.getId();
  }

  @Override
  public void onCycleClick(int position) {
    isNewCycle = false;

    if (mode == 1) {
      positionOfSelectedCycleForModeOne = position;

      cycleHasActivityAssigned = savedCycleAdapter.getBooleanDeterminingIfCycleHasActivity(position);
      trackActivityWithinCycle = savedCycleAdapter.getBooleanDeterminingIfWeAreTrackingActivity(position);
    }
    if (mode == 3) {
      positionOfSelectedCycleForModeThree = position;
    }

    setCyclesOrPomCyclesEntityInstanceToSelectedListPosition(position);
    setPrimaryIdsForSelectedCycleEntity();


    if (cycleHasActivityAssigned) {
      retrieveCycleActivityPositionAndMetScoreFromCycleList();
    }

    populateCycleRoundAndRoundTypeArrayLists(false);

    dotsAdapter.notifyDataSetChanged();

    if (mode == 1) {
      savedCycleAdapter.removeCycleAsActive();
      launchTimerCycle(CYCLE_LAUNCHED_FROM_RECYCLER_VIEW);
    }
    if (mode == 3) {
      savedPomCycleAdapter.removeCycleAsActive();
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

  private void disableHighlightModeIfActive() {
    if (mode==1) {
      if (savedCycleAdapter.isHighlightModeActive()) {
        removeCycleHighlights();
        fadeEditCycleButtonsInAndOut(FADE_OUT_HIGHLIGHT_MODE);
      }
      if (mode==3) {
        if (savedPomCycleAdapter.isHighlightModeActive()) {
          removeCycleHighlights();
          fadeEditCycleButtonsInAndOut(FADE_OUT_HIGHLIGHT_MODE);
        }
      }
    }
  }

  @Override
  public void subtractionFadeHasFinished() {
    //When adapter fade on round has finished, execute method to remove the round from adapter list/holders and refresh the adapter display. If we click to remove another round before fade is done, fade gets cancelled, restarted on next position, and this method is also called to remove previous round.
    removeRound();

    if (workoutTimeIntegerArray.size() >= 8) {
      workoutStringListOfRoundValuesForFirstAdapter.clear();
      workoutIntegerListOfRoundTypeForFirstAdapter.clear();

      for (int i = 0; i < convertedWorkoutTimeStringArray.size(); i++) {
        Log.i("testAnim", "list size is " + convertedWorkoutTimeStringArray.size());
        workoutStringListOfRoundValuesForFirstAdapter.add(convertedWorkoutTimeStringArray.get(i));
        workoutIntegerListOfRoundTypeForFirstAdapter.add(typeOfRound.get(i));
      }

      cycleRoundsAdapter.notifyDataSetChanged();
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
      cycleRoundsAdapter.setIsRoundCurrentlySelectedBoolean(true);
      roundIsSelected = true;
      roundSelectedPosition = position;
    } else {
      cycleRoundsAdapter.setIsRoundCurrentlySelectedBoolean(false);
      roundIsSelected = false;
    }

    cycleRoundsAdapter.notifyDataSetChanged();
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

  private void firstTimeAppUseDisclaimerAlertDialog() {
    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setCancelable(false);
    alertDialog.setCanceledOnTouchOutside(false);

    alertDialog.setTitle("Disclaimer");
    alertDialog.setMessage(getString(R.string.disclaimer));

    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.accept),
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {

              }
            });

    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.decline),
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {

              }
            });

    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
      @Override
      public void onShow(DialogInterface dialog) {

        Button acceptButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
        Button declineButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);

        acceptButton.setOnClickListener(v-> {
          prefEdit.putBoolean("disclaimerHasBeenAccepted", true);
          prefEdit.apply();

          alertDialog.dismiss();
        });

        declineButton.setOnClickListener(v-> {
          AlertDialog alertDialogTwo = new AlertDialog.Builder(MainActivity.this).create();
          alertDialogTwo.setTitle("Warning:");
          alertDialogTwo.setMessage("You must accept the disclaimer terms before continuing!");
          alertDialogTwo.show();
        });
      }
    });

    alertDialog.show();
  }

  private void launchDisclaimerIfNotPreviouslyAgreedTo() {
    boolean agreementClicked = sharedPreferences.getBoolean("disclaimerHasBeenAccepted", false);

    if (!agreementClicked) {
      firstTimeAppUseDisclaimerAlertDialog();
    }
  }

  private void setPromptToLaunchUserSettingsOnFirstAppLaunch() {
    boolean hasAppBeenLaunchedBefore = sharedPreferences.getBoolean("hasAppBeenLaunchedBefore", false);

    if (!hasAppBeenLaunchedBefore) {
      emptyCycleList.setText(R.string.user_settings_prompt);
      emptyCycleList.setEnabled(true);
      prefEdit.putBoolean("hasAppBeenLaunchedBefore", true);
      prefEdit.apply();
    } else {
      emptyCycleList.setText(R.string.empty_cycle_list);
      emptyCycleList.setEnabled(false);
    }
  }

  private void clearCycleTitleEditTextFocusAndHideSoftKeyboard() {
    if (cycleNameEdit.hasFocus()) {
      cycleNameEdit.clearFocus();
      inputMethodManager.hideSoftInputFromWindow(editCyclesPopupView.getWindowToken(), 0);
    }
  }

  @SuppressLint({"UseCompatLoadingForDrawables", "ClickableViewAccessibility", "CommitPrefEdits", "CutPasteId"})
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

   Log.i("testVer", "ver is " + Build.VERSION.SDK_INT);

    setPhoneDimensions();
    groupAllAppStartInstantiations();

    launchDisclaimerIfNotPreviouslyAgreedTo();
    setPromptToLaunchUserSettingsOnFirstAppLaunch();

    mainView = findViewById(R.id.main_layout);
    topOfMainActivityView = findViewById(R.id.top_of_main_activity_view);
    bottomEditTitleDividerView = editCyclesPopupView.findViewById(R.id.bottom_edit_title_divider);

    toggleDayAndNightModesForMain(colorThemeMode);
    toggleDayAndNightModesForTimer(colorThemeMode);

    stopWatchTimerRunnable = stopWatchRunnable();
    infinityTimerForSetsRunnable = infinityRunnableForSetRounds();
    infinityTimerForBreaksRunnable = infinityRunnableForBreakRounds();

    runnableForRecyclerViewTimesForModeOne = runnableForRecyclerViewTimesForModeOne();
    runnableForRecyclerViewTimesForModeThree = runnableForRecyclerViewTimesForModeThree();

    emptyCycleList.setOnClickListener(v-> {
      mainActivityFragmentFrameLayout.startAnimation(slideInFromLeftShort);
      mainActivityFragmentFrameLayout.setVisibility(View.VISIBLE);

      if (rootSettingsFragment != null) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings_fragment_frameLayout, tdeeSettingsFragment)
                .commit();
      }
      sortButton.setVisibility(View.INVISIBLE);

      setTypeOfOnOptionsSelectedMenu(SETTINGS_MENU);

      mHandler.postDelayed(()-> {
        emptyCycleList.setText(R.string.empty_cycle_list);
        emptyCycleList.setEnabled(false);
      },500);
    });

    addTDEEfirstMainTextView.setOnClickListener(v -> {
      inputMethodManager.hideSoftInputFromWindow(editCyclesPopupView.getWindowToken(), 0);

      addTdeePopUpWindow.showAsDropDown(bottomEditTitleDividerView);

      clearCycleTitleEditTextFocusAndHideSoftKeyboard();
    });

    addActivityConfirmButton.setOnClickListener(v -> {
      addTdeePopUpWindow.dismiss();
      toggleEditPopUpViewsForAddingActivity(true);
    });

    cancelActivityConfirmButton.setOnClickListener(v -> {
      addTdeePopUpWindow.dismiss();
      toggleEditPopUpViewsForAddingActivity(false);
    });

    fab.setOnClickListener(v -> {
      fabLogic();
      removeCycleHighlights();
    });

    stopWatchLaunchButton.setOnClickListener(v -> {
      stopWatchLaunchLogic();
    });

    sortButton.setOnClickListener(v -> {
      sortPopupWindow.showAtLocation(mainView, Gravity.END | Gravity.TOP, 0, 0);
    });

    timerPopUpWindow.setOnDismissListener(() -> {
      activateResumeOrResetOptionForCycle();
      replaceCycleListWithEmptyTextViewIfNoCyclesExist();
      toggleCycleAndPomCycleRecyclerViewVisibilities(false);

      toggleCustomActionBarButtonVisibilities(false);

      timerPopUpDismissalLogic();
    });

    editPopUpLayout.setOnClickListener(v-> {
      clearCycleTitleEditTextFocusAndHideSoftKeyboard();
    });

    editCyclesPopupWindow.setOnDismissListener(() -> {
      editCyclesPopUpDismissalLogic();
      setDefaultEditRoundViews();

      replaceCycleListWithEmptyTextViewIfNoCyclesExist();
    });

    edit_highlighted_cycle.setOnClickListener(v -> {
      mHandler.postDelayed(() -> {
        fadeEditCycleButtonsInAndOut(FADE_IN_EDIT_CYCLE);
      },250);

      removeCycleHighlights();
      editHighlightedCycleLogic();

      if (mode == 1) {
        cycles = cyclesList.get(positionOfSelectedCycleForModeOne);
      }
      if (mode == 3) {
        pomCycles = pomCyclesList.get(positionOfSelectedCycleForModeThree);
      }

      clearRoundAndCycleAdapterArrayLists();

      populateCycleRoundAndRoundTypeArrayLists(false);
      populateRoundAdapterArraysForHighlightedCycle();

      assignOldCycleValuesToCheckForChanges();

      editPopUpTimerArray.clear();
      timerValueInEditPopUpTextView.setText("00:00");
      resetTimerKeypadValues();
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
    });

    cycleNameEdit.setOnLongClickListener(v -> {
      cycleNameEdit.selectAll();
//      inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
      inputMethodManager.showSoftInput(editCyclesPopupView.getRootView(), 0);
      return true;
    });

    firstRoundTypeHeaderInEditPopUp.setOnClickListener(v -> {
      setEditPopUpTimerHeaders(1);
      clearCycleTitleEditTextFocusAndHideSoftKeyboard();
    });

    secondRoundTypeHeaderInEditPopUp.setOnClickListener(v -> {
      setEditPopUpTimerHeaders(2);
      clearCycleTitleEditTextFocusAndHideSoftKeyboard();
    });

    thirdRoundTypeHeaderInEditPopUp.setOnClickListener(v -> {
      setEditPopUpTimerHeaders(3);
      clearCycleTitleEditTextFocusAndHideSoftKeyboard();
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
      clearCycleTitleEditTextFocusAndHideSoftKeyboard();
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

    clearCycleTitleEditTextFocusAndHideSoftKeyboard();

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
      clearCycleTitleEditTextFocusAndHideSoftKeyboard();
    });

    //For moment, using arrows next to sets and breaks to determine which type of round we're adding.
    addRoundToCycleButton.setOnClickListener(v -> {
      mHandler.postDelayed(()-> {
        if (mode==1) adjustRoundCountForModeOne(true);
        if (mode==3) adjustRoundCountForModeThree(true);
      }, 25);

      clearCycleTitleEditTextFocusAndHideSoftKeyboard();
    });

    subtractRoundFromCycleButton.setOnClickListener(v -> {
      mHandler.postDelayed(()-> {
        if (mode==1) adjustRoundCountForModeOne(false);
        if (mode==3) adjustRoundCountForModeThree(false);
        }, 25);

      clearCycleTitleEditTextFocusAndHideSoftKeyboard();
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
      clearCycleTitleEditTextFocusAndHideSoftKeyboard();

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
        if (mode==1) {
          progressBar.startAnimation(fadeProgressIn);
          progressBar.setProgress(maxProgress);
          timeLeftForCyclesTimer.startAnimation(fadeProgressIn);

        }
        if (mode==3) {
          progressBarForPom.startAnimation(fadeProgressIn);
          progressBarForPom.setProgress(maxProgress);
          timeLeftForPomCyclesTimer.startAnimation(fadeProgressIn);
        }
        //Resets progressBar to max (full blue) value at same time we fade it back in (looks nicer).
      }

      @Override
      public void onAnimationRepeat(Animation animation) {
      }
    });

    endFadeForModeOne = new Runnable() {
      @Override
      public void run() {
        if (receivedDotAlpha <= 0.3f) {
          dotsAdapter.setDotAlpha(0.3f);
          dotsAdapter.notifyDataSetChanged();

          mHandler.removeCallbacks(this);
        } else {
          dotsAdapter.notifyDataSetChanged();
          mHandler.postDelayed(this, 50);
        }
      }
    };

    endFadeForModeThree = new Runnable() {
      @Override
      public void run() {
        if (receivedPomDotAlpha <= 0.3f) {
          pomDotsAdapter.setPomDotAlpha(0.3f);
          pomDotsAdapter.notifyDataSetChanged();

          mHandler.removeCallbacks(this);
        } else {
          pomDotsAdapter.notifyDataSetChanged();
          mHandler.postDelayed(this, 50);
        }
      }
    };

    next_round.setOnClickListener(v -> {
      if (mode == 1) {
        nextRound(true);
      }
      if (mode == 3) {
        nextPomRound(true);
      }
    });

    resetButtonForCycles.setOnClickListener(v -> {
      AsyncTask.execute(globalSaveTotalTimesAndCaloriesInDatabaseRunnable);
      resetCyclesTimer();
    });

    resetButtonForPomCycles.setOnClickListener(v-> {
      if (resetButtonForPomCycles.getText().toString().equals(getString(R.string.reset))) {
        resetButtonForPomCycles.setText(R.string.confirm_cycle_reset);
      } else {
        resetPomCyclesTimer();
      }
    });

    pauseResumeButton.setOnClickListener(v -> {
      if (mode == 1) {
        if (!stateOfTimers.isModeOneTimerPaused()) {
          pauseAndResumeTimer(PAUSING_TIMER);
        } else {
          pauseAndResumeTimer(RESUMING_TIMER);
        }
      }
      if (mode == 3) {
        if (!stateOfTimers.isModeThreeTimerPaused()) {
          pauseAndResumePomodoroTimer(PAUSING_TIMER);
        } else {
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
          deleteTotalCycleTimesFromDatabaseAndZeroOutVars();
          resetCycleTimeVarsWithinRunnable = true;
        }
        if (delete_all_text.getText().equals(getString(R.string.delete_all_cycles))) {
          deleteAllCycles();
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

        if (delete_all_text.getText().equals(getString(R.string.delete_activities_and_foods_from_selected_duration))) {
          deleteActivityStatsForSelectedDays();
          deleteFoodStatsForSelectedDays();
        }
        if (delete_all_text.getText().equals(getString(R.string.delete_all_activities_and_foods))) {
          deleteActivityStatsForAllDays();
          deleteFoodStatsForAllDays();
        }
      });
    });

    delete_all_cancel.setOnClickListener(v -> {
      if (deleteCyclePopupWindow.isShowing()) deleteCyclePopupWindow.dismiss();
    });

    stopWatchPauseResumeButton.setOnClickListener(v -> {
      if (!stateOfTimers.isStopWatchTimerPaused()) {
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
//      removeCycleHighlights();

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
      sortButton.setAlpha(0.3f);
      sortButton.setEnabled(false);
    } else {
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

    retrieveAndImplementCycleSorting();
    instantiateDatabaseClassesViaASyncThreadAndFollowWithUIPopulationOfTheirComponents();

    instantiateLayoutManagers();
    instantiateRoundAdaptersAndTheirCallbacks();
    setRoundRecyclersOnAdaptersAndLayoutManagers();
    setVerticalSpaceDecorationForCycleRecyclerViewBasedOnScreenHeight();
    instantiateLapAdapterAndSetRecyclerOnIt();

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
    progressBarForPom.setProgress(maxProgress);
  }

  private void instantiateGlobalClasses() {
    stateOfTimers = new StateOfTimers();
    longToStringConverters = new LongToStringConverters();

    fragmentManager = getSupportFragmentManager();
    changeSettingsValues = new ChangeSettingsValues(getApplicationContext());
    tDEEChosenActivitySpinnerValues = new TDEEChosenActivitySpinnerValues(getApplicationContext());
    dailyStatsAccess = new DailyStatsAccess(getApplicationContext());

    mHandler = new Handler();
    mSavingCycleHandler = new Handler();
    inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    sharedPreferences = getApplicationContext().getSharedPreferences("pref", 0);
    prefEdit = sharedPreferences.edit();

    cycles = new Cycles();
    pomCycles = new PomCycles();

    objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", maxProgress, 0);
    objectAnimatorPom = ObjectAnimator.ofInt(progressBarForPom, "progressPom", maxProgress, 0);

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

            roundRecycler.setVisibility(View.VISIBLE);
            savedCycleRecycler.setVisibility(View.VISIBLE);
            dotsRecycler.setVisibility(View.VISIBLE);
            pomRoundRecycler.setVisibility(View.GONE);
            savedPomCycleRecycler.setVisibility(View.GONE);
            pomDotsRecycler.setVisibility(View.GONE);
            break;
          case 1:
            mode = 3;
            cycleRoundsAdapter.setMode(3);
            pomDotsAdapter.setModeThreeAlpha();

            roundRecycler.setVisibility(View.GONE);
            savedCycleRecycler.setVisibility(View.GONE);
            dotsRecycler.setVisibility(View.GONE);
            savedPomCycleRecycler.setVisibility(View.VISIBLE);
            pomDotsRecycler.setVisibility(View.VISIBLE);
            pomRoundRecycler.setVisibility(View.VISIBLE);

            break;
        }
        replaceCycleListWithEmptyTextViewIfNoCyclesExist();
        setDefaultEditRoundViews();
      }

      @Override
      public void onTabUnselected(TabLayout.Tab tab) {
        savedCyclesTab = tab;
        tabUnselectedLogic();

        if (savedCyclesTab.getPosition() == 0) {
          if (savedCycleAdapter.isHighlightModeActive()) {
            savedCycleAdapter.notifyDataSetChanged();
            fadeEditCycleButtonsInAndOut(FADE_OUT_HIGHLIGHT_MODE);
          }
          dotsAdapter.saveModeOneAlpha();
        }
        if (savedCyclesTab.getPosition() == 1) {
          if (savedPomCycleAdapter.isHighlightModeActive()) {
            savedPomCycleAdapter.notifyDataSetChanged();
            fadeEditCycleButtonsInAndOut(FADE_OUT_HIGHLIGHT_MODE);
          }
          pomDotsAdapter.saveModeThreeAlpha();
        }

        //Must execute after we check if highlight mode is active above.
        removeCycleHighlights();
      }

      @Override
      public void onTabReselected(TabLayout.Tab tab) {
      }
    });
  }

  private void tabUnselectedLogic() {
    cycleTitle = "";

    if (editCyclesPopupWindow.isShowing()) {
      editCyclesPopupWindow.dismiss();
    }

    receivedHighlightPositions.clear();
  }

  private void assignMainLayoutClassesToIds() {
    mainActionBar = getSupportActionBar();

    mainActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    mainActionBar.setDisplayShowCustomEnabled(true);

    if (phoneHeight <= 1920) {
      mainActionBar.setCustomView(R.layout.custom_bar_h1920);
    } else {
      mainActionBar.setCustomView(R.layout.custom_bar);
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
    addActivityConfirmButton = addTDEEPopUpView.findViewById(R.id.add_activity_confirm_button);
    cancelActivityConfirmButton = addTDEEPopUpView.findViewById(R.id.add_activity_cancel_button);
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

    editPopUpLayout = editCyclesPopupView.findViewById(R.id.edit_cycle_layout);
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

    roundRecycler = editCyclesPopupView.findViewById(R.id.round_recycler);
    pomRoundRecycler = editCyclesPopupView.findViewById(R.id.pom_round_recycler);

    addTDEEfirstMainTextView = editCyclesPopupView.findViewById(R.id.tdee_add_textView);
  }

  private void assignTimerPopUpLayoutClassesToTheirIds() {
    timerView = timerPopUpView.findViewById(R.id.main_timer_layout);

    resetButtonForCycles = timerPopUpView.findViewById(R.id.resetButtonForCycles);
    resetButtonForPomCycles = timerPopUpView.findViewById(R.id.resetButtonForPomCycles);

    nonTrackingTimerHeaderLayout = timerPopUpView.findViewById(R.id.non_tracking_timer_stat_headers_layout);
    nonTrackingHeaderLayoutParams = (ConstraintLayout.LayoutParams) nonTrackingTimerHeaderLayout.getLayoutParams();

    trackingTimerHeaderLayout = timerPopUpView.findViewById(R.id.tracking_timer_header_layout);
    tracking_daily_stats_header_textView = timerPopUpView.findViewById(R.id.tracking_daily_stats_header_textView);
    trackingTimerHeaderLayout = timerPopUpView.findViewById(R.id.tracking_timer_header_layout);
    trackingHeaderLayoutParams = (ConstraintLayout.LayoutParams) trackingTimerHeaderLayout.getLayoutParams();

    cycle_title_textView = timerPopUpView.findViewById(R.id.cycle_title_textView);
    cycle_title_textView_with_activity = timerPopUpView.findViewById(R.id.cycle_title_textView_with_activity);
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
    lapListCanvas.setGradientWidth();

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
    progressBarForPom = timerPopUpView.findViewById(R.id.progressBarForPom);
    timeLeftForCyclesTimer = timerPopUpView.findViewById(R.id.timeLeftForCyclesTimerTextView);
    timeLeftForPomCyclesTimer = timerPopUpView.findViewById(R.id.timeLeftForPomCyclesTimerTextView);
    reset_total_cycle_times = timerPopUpView.findViewById(R.id.reset_total_cycle_times);
//    reset_total_cycle_times.setVisibility(View.GONE);
    pauseResumeButton = timerPopUpView.findViewById(R.id.pauseResumeButton);
    next_round = timerPopUpView.findViewById(R.id.next_round);

  }

  private void assignSortPopUpLayoutClassesToTheirIds() {
    sortCycleTitleAToZ = sortCyclePopupView.findViewById(R.id.sort_title_start);
    sortCycleTitleZtoA = sortCyclePopupView.findViewById(R.id.sort_title_end);
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
      queryAndSortAllCyclesFromMenu();

      runOnUiThread(() -> {
        instantiateCycleAdaptersAndTheirCallbacks();
        clearAndRepopulateCycleAdapterListsFromDatabaseList(true);
        replaceCycleListWithEmptyTextViewIfNoCyclesExist();

        setDefaultUserSettings();
        setDefaultEditRoundViews();
      });
    });
  }

  private void instantiateCycleAdaptersAndTheirCallbacks() {
    savedCycleAdapter = new SavedCycleAdapter(getApplicationContext(), workoutCyclesArray, typeOfRoundArray, workoutTitleArray, tdeeActivityExistsInCycleList, tdeeIsBeingTrackedInCycleList, workoutActivityStringArray);
    savedCycleRecycler.setAdapter(savedCycleAdapter);
    savedCycleRecycler.setLayoutManager(workoutCyclesRecyclerLayoutManager);

    savedCycleAdapter.setTimerPauseOrResume(MainActivity.this);
    savedCycleAdapter.setTdeeToggle(MainActivity.this);
    savedCycleAdapter.setItemClick(MainActivity.this);
    savedCycleAdapter.setHighlight(MainActivity.this);
    savedCycleAdapter.setResumeOrResetCycle(MainActivity.this);

    savedCycleAdapter.setScreenHeight(phoneHeight);
    savedCycleAdapter.setDayOrNightMode(colorThemeMode);

    savedPomCycleAdapter = new SavedPomCycleAdapter(getApplicationContext(), pomArray, pomTitleArray);
    savedPomCycleRecycler.setAdapter(savedPomCycleAdapter);
    savedPomCycleRecycler.setLayoutManager(pomCyclesRecyclerLayoutManager);

    savedPomCycleAdapter.setTimerPauseOrResume(MainActivity.this);
    savedPomCycleAdapter.setItemClick(MainActivity.this);
    savedPomCycleAdapter.setHighlight(MainActivity.this);
    savedPomCycleAdapter.setResumeOrResetCycle(MainActivity.this);

  }

  private void instantiateLayoutManagers() {
    workoutCyclesRecyclerLayoutManager = new LinearLayoutManager(getApplicationContext());
    roundRecyclerOneLayoutManager = new LinearLayoutManager(getApplicationContext());
    pomCyclesRecyclerLayoutManager = new LinearLayoutManager(getApplicationContext());

    lapRecyclerLayoutManager = new LinearLayoutManager(getApplicationContext());
    lapRecyclerLayoutManager.setStackFromEnd(true);
    lapRecyclerLayoutManager.setReverseLayout(true);
  }

  private void instantiateRoundAdaptersAndTheirCallbacks() {
    cycleRoundsAdapter = new CycleRoundsAdapter(getApplicationContext(), workoutStringListOfRoundValuesForFirstAdapter, workoutIntegerListOfRoundTypeForFirstAdapter, pomStringListOfRoundValues);
    cycleRoundsAdapter.fadeFinished(MainActivity.this);
    cycleRoundsAdapter.selectedRound(MainActivity.this);

    cycleRoundsAdapter.setScreenHeight(phoneHeight);
  }

  private void setRoundRecyclersOnAdaptersAndLayoutManagers() {
    GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 8);
    gridLayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);

    GridLayoutManager gridLayoutManager2 = new GridLayoutManager(getApplicationContext(), 8);
    gridLayoutManager2.setOrientation(GridLayoutManager.HORIZONTAL);

    roundRecycler.setLayoutManager(gridLayoutManager);
    roundRecycler.setAdapter(cycleRoundsAdapter);
    roundRecycler.setNestedScrollingEnabled(false);
    roundRecycler.setHasFixedSize(true);

    pomRoundRecycler.setLayoutManager(gridLayoutManager2);
    pomRoundRecycler.setAdapter(cycleRoundsAdapter);

    //Disables "ghost scrolling"
    roundRecycler.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        return true;
      }
    });

    pomRoundRecycler.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        return true;
      }
    });
  }

  private void setVerticalSpaceDecorationForCycleRecyclerViewBasedOnScreenHeight() {
    if (phoneHeight <= 1920) {
      verticalSpaceItemDecoration = new VerticalSpaceItemDecoration(
              -25);
    } else {
      verticalSpaceItemDecoration = new VerticalSpaceItemDecoration(
              -10);
    }
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

    ArrayList<String> convertedWorkoutRoundList = convertMillisIntegerListToTimerStringList(workoutTimeIntegerArray);

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

  private void adjustDotRecyclerLayoutMargins() {
    if (mode == 1) {
      if (trackActivityWithinCycle) {
        dotsRecyclerConstraintLayout_LayoutParams.bottomMargin = dpConv(-8);
      } else {
        dotsRecyclerConstraintLayout_LayoutParams.bottomMargin = dpConv(32);
      }
    }
  }

  private int dpConv(float pixels) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixels, getResources().getDisplayMetrics());
  }

  private void instantiateAnimationAndColorMethods() {
    fadeInForCustomActionBar = new AlphaAnimation(0.0f, 1.0f);
    fadeInForCustomActionBar.setDuration(300);
    fadeInForCustomActionBar.setFillAfter(true);

    fadeOutForCustomActionBar = new AlphaAnimation(1.0f, 0.0f);
    fadeOutForCustomActionBar.setDuration(300);
    fadeOutForCustomActionBar.setFillAfter(true);

    fadeInForEditCycleButton = new AlphaAnimation(0.0f, 1.0f);
    fadeInForEditCycleButton.setDuration(300);
    fadeInForEditCycleButton.setFillAfter(true);

    fadeOutForEditCycleButton = new AlphaAnimation(1.0f, 0.0f);
    fadeOutForEditCycleButton.setDuration(300);
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

    deleteCyclePopupWindow = new PopupWindow(deleteCyclePopupView, convertDensityPixelsToScalable(300), convertDensityPixelsToScalable(150), true);
    sortPopupWindow = new PopupWindow(sortCyclePopupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
    addTdeePopUpWindow = new PopupWindow(addTDEEPopUpView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
    aboutSettingsPopUpWindow = new PopupWindow(aboutSettingsPopUpView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);

    setEditCyclesLayoutForDifferentHeights();
    setTimerLayoutForDifferentHeights();
    setStopWatchLayoutForDifferentHeights();

    deleteCyclePopupWindow.setAnimationStyle(R.style.WindowAnimation);
    sortPopupWindow.setAnimationStyle(R.style.SlideTopAnimationWithoutAnimatedExit);
    editCyclesPopupWindow.setAnimationStyle(R.style.SlideFromLeftAnimationShort);
    addTdeePopUpWindow.setAnimationStyle(R.style.SlideFromLeftAnimationShort);
    aboutSettingsPopUpWindow.setAnimationStyle(R.style.SlideFromLeftAnimationShort);

//    timerPopUpWindow.setAnimationStyle(R.style.SlideFromLeftAnimationShort);
    timerPopUpWindow.setAnimationStyle(R.style.WindowAnimation);
    stopWatchPopUpWindow.setAnimationStyle(R.style.SlideFromLeftAnimationShort);

  }

  private void instantiateArrayLists() {
    workoutTimeIntegerArray = new ArrayList<>();
    convertedWorkoutTimeStringArray = new ArrayList<>();
    workoutCyclesArray = new ArrayList<>();
    typeOfRound = new ArrayList<>();
    typeOfRoundArray = new ArrayList<>();
    pomArray = new ArrayList<>();

    workoutStringListOfRoundValuesForFirstAdapter = new ArrayList<>();
    workoutIntegerListOfRoundTypeForFirstAdapter = new ArrayList<>();

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
    stopwatchReset.setVisibility(View.INVISIBLE);
    new_lap.setAlpha(0.3f);

    pomRoundRecycler.setVisibility(View.GONE);
    savedPomCycleRecycler.setVisibility(View.GONE);
    pomDotsRecycler.setVisibility(View.GONE);

    timeLeftForCyclesTimer.setVisibility(View.GONE);
    timeLeftForPomCyclesTimer.setVisibility(View.GONE);
    progressBar.setVisibility(View.GONE);
    progressBarForPom.setVisibility(View.GONE);
    resetButtonForCycles.setVisibility(View.GONE);
    resetButtonForPomCycles.setVisibility(View.GONE);
  }

  private void setDefaultLayoutTexts() {
    addActivityConfirmButton.setText(R.string.okay);
    cancelActivityConfirmButton.setText(R.string.cancel);
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
        if (stateOfTimers.isModeOneTimerActive()) {
          if (!trackActivityWithinCycle) {
            setCycleValuesAndUpdateInDatabase();
          } else {
            if (!isDailyActivityTimeMaxed()) {
              setAndUpdateActivityTimeAndCaloriesInDatabase();
            }
          }

          if (cycleHasActivityAssigned) {
            createNewListOfActivitiesIfDayHasChanged();
          }

          if (!stateOfTimers.isModeOneTimerPaused()) {
            mHandler.postDelayed(globalSaveTotalTimesOnPostDelayRunnableInASyncThread, 2000);
          } else {
            mHandler.removeCallbacks(globalSaveTotalTimesOnPostDelayRunnableInASyncThread);
          }
        }

        if (stateOfTimers.isModeThreeTimerActive()) {
          setPomCyclesValuesAndUpdateInDatabase();

          if (!stateOfTimers.isModeThreeTimerPaused()) {
            mHandler.postDelayed(globalSaveTotalTimesOnPostDelayRunnableInASyncThread, 2000);
          } else {
            mHandler.removeCallbacks(globalSaveTotalTimesOnPostDelayRunnableInASyncThread);
          }
        }
      }
    };
  }

  private void setCycleValuesAndUpdateInDatabase() {
    cycles.setTotalSetTime(totalCycleSetTimeInMillis);
    cycles.setTotalBreakTime(totalCycleBreakTimeInMillis);
    cycles.setCyclesCompleted(cyclesCompleted);
    cyclesDatabase.cyclesDao().updateCycles(cycles);
  }

  private void setPomCyclesValuesAndUpdateInDatabase() {
    pomCycles.setTotalWorkTime(totalCycleWorkTimeInMillis);
    pomCycles.setTotalRestTime(totalCycleRestTimeInMillis);
    pomCycles.setCyclesCompleted(cyclesCompleted);
    cyclesDatabase.cyclesDao().updatePomCycles(pomCycles);
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

  private void setAndUpdateActivityTimeAndCaloriesInDatabase() {
    dailyStatsAccess.updateTotalTimesAndCaloriesForEachActivityForSelectedDay(totalSetTimeForSpecificActivityForCurrentDayInMillis, totalCaloriesBurnedForSpecificActivityForCurrentDay);
  }

  private void fabLogic() {
    cycleNameEdit.getText().clear();
    isNewCycle = true;

    clearRoundAndCycleAdapterArrayLists();

    assignOldCycleValuesToCheckForChanges();
    resetEditPopUpTimerHeaders();

    resetTimerKeypadValues();

    setTdeeSpinnersToDefaultValues();
    toggleEditPopUpViewsForAddingActivity(false);

    //For some reason, shownAsDropDown vs showAtLocation prevents soft kb displacing layout.
    editCyclesPopupWindow.showAsDropDown(savedCyclesTabLayout);
  }

  private void resetTimerKeypadValues() {
    timerValueInEditPopUpTextView.setText("00:00");

    if (mode == 1) {
      setTimeValueEnteredWithKeypad = 0;
      breakTimeValueEnteredWithKeypad = 0;

      savedEditPopUpArrayForFirstHeaderModeOne.clear();
      savedEditPopUpArrayForSecondHeaderModeOne.clear();
    }
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

      if (mode == 1) {
        sortMode = sortHolder;
      }
      if (mode == 3) {
        sortModePom = sortHolder;
      }

      unHighlightSortTextViews();
      highlightSortTextView();

      AsyncTask.execute(() -> {
        queryAndSortAllCyclesFromMenu();

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


    if (mode==1) {
      positionOfSelectedCycleForModeOne = (receivedHighlightPositions.get(0));

      cycleHasActivityAssigned = tdeeActivityExistsInCycleList.get(positionOfSelectedCycleForModeOne);
      toggleEditPopUpViewsForAddingActivity(cycleHasActivityAssigned);

      setCyclesOrPomCyclesEntityInstanceToSelectedListPosition(positionOfSelectedCycleForModeOne);
      setPrimaryIdsForSelectedCycleEntity();

      if (cycleHasActivityAssigned) {
        retrieveCycleActivityPositionAndMetScoreFromCycleList();
      }

      String tdeeString = workoutActivityStringArray.get(positionOfSelectedCycleForModeOne);
      setTdeeSpinnersToDefaultValues();
      addTDEEfirstMainTextView.setText(tdeeString);

      cycleTitle = workoutTitleArray.get(positionOfSelectedCycleForModeOne);
    }

    if (mode == 3) {
      positionOfSelectedCycleForModeThree = receivedHighlightPositions.get(0);
      cycleTitle = pomTitleArray.get(positionOfSelectedCycleForModeThree);
    }

    cycleNameEdit.setText(cycleTitle);
  }

  private void populateRoundAdapterArraysForHighlightedCycle() {
    switch (mode) {
      case 1:
        for (int i = 0; i < workoutTimeIntegerArray.size(); i++) {

          convertedWorkoutTimeStringArray.add(longToStringConverters.convertSecondsToMinutesBasedString(workoutTimeIntegerArray.get(i) / 1000));
          workoutStringListOfRoundValuesForFirstAdapter.add(longToStringConverters.convertSecondsToMinutesBasedString(workoutTimeIntegerArray.get(i) / 1000));
          workoutIntegerListOfRoundTypeForFirstAdapter.add(typeOfRound.get(i));
        }
        roundSelectedPosition = workoutTimeIntegerArray.size() - 1;
        break;
      case 3:
        cycleRoundsAdapter.disablePomFade();

        pomStringListOfRoundValues.clear();
        for (int i = 0; i < pomValuesTime.size(); i++) {
          pomStringListOfRoundValues.add(longToStringConverters.convertSecondsToMinutesBasedString(pomValuesTime.get(i) / 1000));
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
      runOnUiThread(()-> {
        removeCycleHighlights();
      });
    }
    if (mode == 3) {
      for (int i = 0; i < receivedHighlightPositions.size(); i++) {
        cycleID = pomCyclesList.get(receivedHighlightPositions.get(i)).getId();
        pomCycles = cyclesDatabase.cyclesDao().loadSinglePomCycle(cycleID).get(0);
        cyclesDatabase.cyclesDao().deletePomCycle(pomCycles);
      }
      runOnUiThread(()-> {
        removeCycleHighlights();
      });
    }

    receivedHighlightPositions.clear();

    queryAndSortAllCyclesFromMenu();

    runOnUiThread(() -> {
      delete_highlighted_cycle.setEnabled(false);
      fadeEditCycleButtonsInAndOut(FADE_OUT_HIGHLIGHT_MODE);

      clearAndRepopulateCycleAdapterListsFromDatabaseList(false);

      replaceCycleListWithEmptyTextViewIfNoCyclesExist();

      showToastIfNoneActive("Deleted!");
    });
  }

  private void deleteAllCycles() {
    if (mode == 1) {
      if (cyclesList.size() > 0) {

        cyclesDatabase.cyclesDao().deleteAllCycles();
        queryAndSortAllCyclesFromMenu();

        sortMode = 0;

        runOnUiThread(() -> {
          clearAndRepopulateCycleAdapterListsFromDatabaseList(false);
          savedCycleAdapter.notifyDataSetChanged();
        });
      }
    }
    if (mode == 3) {
      if (pomCyclesList.size() > 0) {
        cyclesDatabase.cyclesDao().deleteAllPomCycles();
        queryAndSortAllCyclesFromMenu();

        sortModePom = 0;

        runOnUiThread(() -> {
          clearAndRepopulateCycleAdapterListsFromDatabaseList(false);
          savedPomCycleAdapter.notifyDataSetChanged();
        });
      }
    }
  }

  private void deleteTotalCycleTimesFromDatabaseAndZeroOutVars() {
    if (mode == 1) cyclesDatabase.cyclesDao().deleteTotalTimesCycle();
    if (mode == 3) cyclesDatabase.cyclesDao().deleteTotalTimesPom();

    runOnUiThread(() -> {
      deleteCyclePopupWindow.dismiss();

      zeroOutTotalCycleTimes();
      cyclesCompleted = 0;

      total_set_time.setText("0");
      total_break_time.setText("0");
      setCyclesCompletedTextView();
    });
  }

  private void zeroOutTotalCycleTimes() {
    if (mode == 1) {
      totalCycleSetTimeInMillis = 0;
      totalCycleBreakTimeInMillis = 0;
    }
    if (mode == 3) {
      totalCycleWorkTimeInMillis = 0;
      totalCycleRestTimeInMillis = 0;
    }
  }

  private void setDefaultUserSettings() {
    retrieveUserStats();

    SharedPreferences prefShared = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

    String defaultSoundSettingForSets = prefShared.getString("soundSettingForSets", "vibrate_once");
    String defaultSoundSettingForBreaks = prefShared.getString("soundSettingForBreaks", "vibrate_twice");
    boolean defaultSoundSettingForLastRound = prefShared.getBoolean("soundSettingForLastRound", true);

    String defaultSoundSettingForWork = prefShared.getString("soundSettingForWork", "vibrate_once");
    String defaultSoundSettingForMiniBreak = prefShared.getString("soundSettingForMiniBreaks", "vibrate_twice");
    boolean defaultSoundSettingForFullBreak = prefShared.getBoolean("soundSettingForRestRound", true);

    String defaultColorSettingForSets = prefShared.getString("colorSettingForSets", "green_setting");
    String defaultColorSettingForBreaks = prefShared.getString("colorSettingForBreaks", "red_setting");

    String defaultColorSettingForWork = prefShared.getString("colorSettingForWork", "green_setting");
    String defaultColorSettingForMiniBreak = prefShared.getString("colorSettingForMiniBreaks", "red_setting");
    String defaultColorSettingForFullBreak = prefShared.getString("colorSettingForFullBreak", "cyan_setting");

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

    dotsAdapter.setColorSettingsFromMainActivity(1, setColorNumericValue);
    dotsAdapter.setColorSettingsFromMainActivity(2, breakColorNumericValue);
    pomDotsAdapter.setColorSettingsFromMainActivity(3, workColorNumericValue);
    pomDotsAdapter.setColorSettingsFromMainActivity(4, miniBreakColorNumericValue);
    pomDotsAdapter.setColorSettingsFromMainActivity(5, fullBreakColorNumericValue);

    cycleRoundsAdapter.setColorSettingsFromMainActivity(1, setColorNumericValue);
    cycleRoundsAdapter.setColorSettingsFromMainActivity(2, breakColorNumericValue);
    cycleRoundsAdapter.setColorSettingsFromMainActivity(3, workColorNumericValue);
    cycleRoundsAdapter.setColorSettingsFromMainActivity(4, miniBreakColorNumericValue);
    cycleRoundsAdapter.setColorSettingsFromMainActivity(5, fullBreakColorNumericValue);

    savedCycleAdapter.setColorSettingsFromMainActivity(1, setColorNumericValue);
    savedCycleAdapter.setColorSettingsFromMainActivity(2, breakColorNumericValue);

    savedPomCycleAdapter.setColorSettingsFromMainActivity(3, workColorNumericValue);
    savedPomCycleAdapter.setColorSettingsFromMainActivity(4, miniBreakColorNumericValue);
    savedPomCycleAdapter.setColorSettingsFromMainActivity(5, fullBreakColorNumericValue);
  }

  private void retrieveUserStats() {
    SharedPreferences sp = getApplicationContext().getSharedPreferences("pref", 0);
    metricMode = sp.getBoolean("metricMode", false);
    userGender = sp.getString("tdeeGender", "male");
    userAge = sp.getInt("tdeeAge,", 18);
    userWeight = sp.getInt("tdeeWeight,", 150);
    userHeight = sp.getInt("tdeeHeight,", 72);
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
    editCyclesPopupWindow.dismiss();

    if (currentlyEditingACycle) {
//      fadeEditCycleButtonsInAndOut(FADE_OUT_EDIT_CYCLE);
      currentlyEditingACycle = false;

      removeCycleHighlights();
    }

    fab.setEnabled(true);
    cycleRoundsAdapter.setIsRoundCurrentlySelectedBoolean(false);
    cycleRoundsAdapter.notifyDataSetChanged();
  }

  private void assignOldCycleValuesToCheckForChanges() {
    oldCycleTitleString = cycleTitle;
    if (mode == 1) {
      oldCycleRoundListOne = new ArrayList<>(workoutStringListOfRoundValuesForFirstAdapter);
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

        Drawable newDraw;

        if (phoneHeight <= 1920) {
          newDraw = ContextCompat.getDrawable(getApplicationContext(), R.drawable.infinity_medium_green);
        } else {
          newDraw = ContextCompat.getDrawable(getApplicationContext(), R.drawable.infinity_large_green);
        }

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

        Drawable newDraw;
        if (phoneHeight <= 1920) {
          newDraw = ContextCompat.getDrawable(getApplicationContext(), R.drawable.infinity_medium_red);
        } else {
          newDraw = ContextCompat.getDrawable(getApplicationContext(), R.drawable.infinity_large_red);
        }

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
      setAndCapTimerValues(setTimeValueEnteredWithKeypad);
    }
    if (editHeaderSelected == 2) {
      if (isSavedInfinityOptionActiveForBreaks) {
        toggleInfinityRounds.setAlpha(1.0f);
        roundType = 4;
      } else {
        toggleInfinityRounds.setAlpha(0.4f);
        roundType = 3;
      }
      setAndCapTimerValues(breakTimeValueEnteredWithKeypad);
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

  private void instantiateNotifications() {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= 26) {
      CharSequence name = "Timers";
      String description = "Timer Countdown";
      int importance = NotificationManager.IMPORTANCE_DEFAULT;
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
    //Higher priority will cause it to show at top of screen.
    notificationManagerBuilder.setPriority(Notification.PRIORITY_DEFAULT);
    notificationManagerBuilder.setDeleteIntent(dismissNotificationPendingIntent(this, 1));
    notificationManagerBuilder.setContentIntent(recallAppPendingIntent());

    notificationManagerCompat = NotificationManagerCompat.from(this);
  }

  private PendingIntent recallAppPendingIntent() {
    PackageManager pm = getApplicationContext().getPackageManager();

    Intent recallAppIntent = pm.getLaunchIntentForPackage(getApplicationContext().getPackageName());
    recallAppIntent.setPackage(null);

    PendingIntent recallAppPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, recallAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    return recallAppPendingIntent;
  }

  private PendingIntent dismissNotificationPendingIntent(Context context, int notificationId) {
    Intent dismissIntent = new Intent(context, DismissReceiver.class);
    dismissIntent.putExtra("testMethod", "launchTimer");
    dismissIntent.putExtra("Dismiss ID", notificationId);

    PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), notificationId, dismissIntent, 0);

    return dismissPendingIntent;
  }

  //These broadcast the Pending Intents we have created. MUST BE DECLARED IN MANIFEST.
  public static class DismissReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      dismissNotification = true;
    }
  }

  private void setNotificationValues() {
    if (!dismissNotification) {
      //Anything added (e.g. spaces) to these will cause unwanted empty vertical space in notifications.
      String headerOne = "";
      String headerTwo = "";
      String headerThree = "";
      String bodyOne = "";
      String bodyTwo = "";
      String bodyThree = "";

      if (stateOfTimers.isModeOneTimerActive()) {
        //Sets
        if ((typeOfRound.get(currentRoundForModeOne) == 1) || (typeOfRound.get(currentRoundForModeOne) == 2)) {
          headerOne = setNotificationHeader("Workout", "Set", stateOfTimers.isModeOneTimerPaused());
          bodyOne = setNotificationBody(numberOfRoundsLeftForModeOne, startRoundsForModeOne, setMillis) + " ";
        }
        //Breaks
        if ((typeOfRound.get(currentRoundForModeOne) == 3) || (typeOfRound.get(currentRoundForModeOne) == 4)) {
          headerOne = setNotificationHeader("Workout", "Break", stateOfTimers.isModeOneTimerPaused());
          bodyOne = setNotificationBody(numberOfRoundsLeftForModeOne, startRoundsForModeOne, breakMillis) + " ";
        }

        if (typeOfRound.get(currentRoundForModeOne) == 1 || typeOfRound.get(currentRoundForModeOne) == 3) {
          bodyOne = bodyOne + getString(R.string.arrow_down);
        }
        if (typeOfRound.get(currentRoundForModeOne) == 2|| typeOfRound.get(currentRoundForModeOne) == 4) {
          bodyOne = bodyOne + getString(R.string.arrow_up);
        }
      }

      if (stateOfTimers.isModeThreeTimerActive()) {
        switch (pomDotCounter) {
          case 0:
          case 2:
          case 4:
          case 6:
            headerTwo = setNotificationHeader("Pomodoro", "Work", stateOfTimers.isModeThreeTimerPaused());
            bodyTwo = setNotificationBody(numberOfRoundsLeftForModeThree, startRoundsForModeThree, pomMillis) + " " + getString(R.string.arrow_down);
            break;
          case 1:
          case 3:
          case 5:
          case 7:
            headerTwo = setNotificationHeader("Pomodoro", "Break", stateOfTimers.isModeThreeTimerPaused());
            bodyTwo = setNotificationBody(numberOfRoundsLeftForModeThree, startRoundsForModeThree, pomMillis) + " " + getString(R.string.arrow_down);
            break;
        }
      }

      if (!displayTime.equals("0") || displayMs.equals("0")) {
        headerThree = getString(R.string.notification_stopwatch_header);
        bodyThree = longToStringConverters.convertTimerValuesToStringForNotifications((long) stopWatchSeconds);
      }

      Spannable headerOneBold = new SpannableString(headerOne);
      headerOneBold.setSpan(new StyleSpan(Typeface.BOLD), 0, headerOne.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

      Spannable headerTwoBold = new SpannableString(headerTwo);
      headerTwoBold.setSpan(new StyleSpan(Typeface.BOLD), 0, headerTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

      Spannable headerThreeBold = new SpannableString(headerThree);
      headerThreeBold.setSpan(new StyleSpan(Typeface.BOLD), 0, headerThree.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

      notificationManagerBuilder.setStyle(new NotificationCompat.InboxStyle()
              .addLine(headerOneBold)
              .addLine(bodyOne)
              .addLine(headerTwoBold)
              .addLine(bodyTwo)
              .addLine(headerThreeBold)
              .addLine(bodyThree)
      );

      Notification notification = notificationManagerBuilder.build();
      notificationManagerCompat.notify(1, notification);
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
      timeRemaining = longToStringConverters.convertTimerValuesToStringForNotifications(dividedMillisForTimerDisplay(timeLeft));
    }

    return getString(R.string.notification_text, currentTimerRound, totalRounds, timeRemaining);
  }

  private Runnable notifcationsRunnable() {
    TextViewDisplaySync textViewDisplaySync = new TextViewDisplaySync();

    textViewDisplaySync.setModeOneFirstTextView((String) timeLeftForCyclesTimer.getText());
    textViewDisplaySync.setModeThreeFirstTextView((String) timeLeftForPomCyclesTimer.getText());
    textViewDisplaySync.setStopWatchFirstTextView((String) stopWatchTimeTextView.getText());

    return new Runnable() {
      @Override
      public void run() {
        updateNotificationsIfTimerTextViewHasChanged(textViewDisplaySync);

        mHandler.postDelayed(this, 50);
      }
    };
  }

  private String getUpOrDownArrowForNotifications() {
    String stringToReturn = "";

    if (typeOfRound.get(currentRoundForModeOne) == 1 || typeOfRound.get(currentRoundForModeOne) == 3) {
      stringToReturn = getString(R.string.arrow_down);
    } else {
      stringToReturn = getString(R.string.arrow_up);
    }

    return stringToReturn;
  }

  private void activateResumeOrResetOptionForCycle() {
    if (mode == 1) {
      if (stateOfTimers.isModeOneTimerActive()) {
        savedCycleAdapter.setCycleAsActive();
        savedCycleAdapter.setActiveCyclePosition(positionOfSelectedCycleForModeOne);
        savedCycleAdapter.setNumberOfRoundsCompleted(startRoundsForModeOne - numberOfRoundsLeftForModeOne);

        savedCycleAdapter.notifyDataSetChanged();
      }
    }

    if (mode == 3) {
      if (stateOfTimers.isModeThreeTimerActive()) {
        savedPomCycleAdapter.setCycleAsActive();
        savedPomCycleAdapter.setActiveCyclePosition(positionOfSelectedCycleForModeThree);
        savedPomCycleAdapter.setNumberOfRoundsCompleted(startRoundsForModeThree - numberOfRoundsLeftForModeThree);

        savedPomCycleAdapter.notifyDataSetChanged();
      }
    }
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
      savedCycleAdapter.exitHighlightMode();
      savedCycleAdapter.notifyDataSetChanged();
    }
    if (mode == 3) {
      savedPomCycleAdapter.exitHighlightMode();
      savedPomCycleAdapter.notifyDataSetChanged();
    }
  }

  private void fadeEditCycleButtonsInAndOut(int typeOfFade) {
    if (typeOfFade != FADE_IN_EDIT_CYCLE) {
      delete_highlighted_cycle.setEnabled(true);
      cancelHighlight.setEnabled(true);
    }
    if (typeOfFade == FADE_OUT_EDIT_CYCLE) {
      sortButton.setEnabled(true);
      delete_highlighted_cycle.setEnabled(false);
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
      convertedWorkoutTimeStringArray.clear();
      workoutStringListOfRoundValuesForFirstAdapter.clear();
      workoutIntegerListOfRoundTypeForFirstAdapter.clear();

      workoutTimeIntegerArray.clear();
      typeOfRound.clear();
    }

    if (mode == 3) {
      pomValuesTime.clear();
      pomStringListOfRoundValues.clear();
    }

    cycleRoundsAdapter.notifyDataSetChanged();
  }

  private void setAndCapTimerValues(int value) {
    switch (mode) {
      case 1:
        if (editHeaderSelected == 1) setTimeValueEnteredWithKeypad = timerValueBoundsFormula(5, 5400, value);
        if (editHeaderSelected == 2) breakTimeValueEnteredWithKeypad = timerValueBoundsFormula(5, 5400, value);
        break;
      case 3:
        pomWorkValueEnteredWithKeyPad = timerValueBoundsFormula(600, 5400, value);
        pomMiniBreakValueEnteredWithKeyPad = timerValueBoundsFormula(120, 600, value);
        pomFullBreakValueEnteredWithKeyPad = timerValueBoundsFormula(600, 3600, value);
        break;
    }
  }

  private void setAndCapPomValuesForEditTimer(int value, int variableToCap) {
    if (variableToCap == 1) pomWorkValueEnteredWithKeyPad = timerValueBoundsFormula(600, 5400, value);
    if (variableToCap == 2) pomMiniBreakValueEnteredWithKeyPad = timerValueBoundsFormula(180, 900, value);
    if (variableToCap == 3) pomFullBreakValueEnteredWithKeyPad = timerValueBoundsFormula(900, 5400, value);
  }

  public int timerValueBoundsFormula(int min, int max, int value) {
    if (value < min) return min;
    else if (value > max) return max;
    else return value;
  }

  private void loopProgressBarAnimationAtEndOfCycle() {
    if (mode==1) {
      endAnimationForCyclesTimer = new AlphaAnimation(1.0f, 0.0f);
      endAnimationForCyclesTimer.setDuration(300);
      endAnimationForCyclesTimer.setStartOffset(0);
      endAnimationForCyclesTimer.setRepeatMode(Animation.REVERSE);
      endAnimationForCyclesTimer.setRepeatCount(Animation.INFINITE);

      progressBar.startAnimation(endAnimationForCyclesTimer);
      timeLeftForCyclesTimer.startAnimation(endAnimationForCyclesTimer);

    }

    if (mode==3) {
      endAnimationForPomCyclesTimer = new AlphaAnimation(1.0f, 0.0f);
      endAnimationForPomCyclesTimer.setDuration(300);
      endAnimationForPomCyclesTimer.setStartOffset(0);
      endAnimationForPomCyclesTimer.setRepeatMode(Animation.REVERSE);
      endAnimationForPomCyclesTimer.setRepeatCount(Animation.INFINITE);

      progressBarForPom.startAnimation(endAnimationForPomCyclesTimer);
      timeLeftForPomCyclesTimer.startAnimation(endAnimationForPomCyclesTimer);
    }
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

  private void adjustRoundCountForModeOne(boolean adding) {
    inputMethodManager.hideSoftInputFromWindow(editCyclesPopupView.getWindowToken(), 0);

    if (adding) {
      toggleInfinityModeAndSetRoundType();

      switch (roundType) {
        case 1:
          addOrReplaceRounds(setTimeValueEnteredWithKeypad, roundIsSelected);
          editPopUpTimerArray = convertIntegerToStringArray(setTimeValueEnteredWithKeypad);
          setEditPopUpTimerStringValues();
          break;
        case 2:
          addOrReplaceRounds(0, roundIsSelected);
          break;
        case 3:
          addOrReplaceRounds(breakTimeValueEnteredWithKeypad, roundIsSelected);
          editPopUpTimerArray = convertIntegerToStringArray(breakTimeValueEnteredWithKeypad);
          setEditPopUpTimerStringValues();
          break;
        case 4:
          addOrReplaceRounds(0, roundIsSelected);
          break;
        default:
          return;
      }
    } else {
      if (subtractedRoundIsFading) {
        removeRound();
      }
      if (workoutTimeIntegerArray.size() > 0) {
        cycleRoundsAdapter.setIsRoundCurrentlySelectedBoolean(false);
        cycleRoundsAdapter.setFadeOutPosition(roundSelectedPosition);
        cycleRoundsAdapter.notifyDataSetChanged();

        subtractedRoundIsFading = true;
      } else {
//        showToastIfNoneActive("Nothing to clear!");
      }
    }
  }

  private void adjustRoundCountForModeThree(boolean adding) {
    if (adding) {
      savedEditPopUpArrayForFirstHeaderModeThree = convertIntegerToStringArray(pomWorkValueEnteredWithKeyPad);
      savedEditPopUpArrayForSecondHeaderModeThree = convertIntegerToStringArray(pomMiniBreakValueEnteredWithKeyPad);
      savedEditPopUpArrayForThirdHeader = convertIntegerToStringArray(pomFullBreakValueEnteredWithKeyPad);

      setEditPopUpTimerStringValues();

      if (pomValuesTime.size() == 0) {
        for (int i = 0; i < 3; i++) {
          pomValuesTime.add(pomWorkValueEnteredWithKeyPad * 1000);
          pomValuesTime.add(pomMiniBreakValueEnteredWithKeyPad * 1000);
        }
        pomValuesTime.add(pomWorkValueEnteredWithKeyPad * 1000);
        pomValuesTime.add(pomFullBreakValueEnteredWithKeyPad * 1000);
        for (int j = 0; j < pomValuesTime.size(); j++) {
          pomStringListOfRoundValues.add(longToStringConverters.convertSecondsToMinutesBasedString(pomValuesTime.get(j) / 1000));
        }

        cycleRoundsAdapter.setPomFade(true);
        cycleRoundsAdapter.notifyDataSetChanged();
      }
    }  else {
      if (pomValuesTime.size() != 0) {
        cycleRoundsAdapter.setPomFade(false);
        cycleRoundsAdapter.notifyDataSetChanged();
        subtractRoundFromCycleButton.setClickable(false);
      } else {
//        showToastIfNoneActive("Nothing to clear!");
      }
    }
  }

  private void addOrReplaceRounds(int integerValue, boolean replacingValue) {
    if (mode == 1) {
      if (!replacingValue) {
        if (workoutTimeIntegerArray.size() < 16) {
          workoutTimeIntegerArray.add(integerValue * 1000);
          convertedWorkoutTimeStringArray.add(longToStringConverters.convertSecondsToMinutesBasedString(integerValue));
          typeOfRound.add(roundType);
          roundSelectedPosition = workoutTimeIntegerArray.size() - 1;

          workoutStringListOfRoundValuesForFirstAdapter.add(convertedWorkoutTimeStringArray.get(convertedWorkoutTimeStringArray.size() - 1));
          workoutIntegerListOfRoundTypeForFirstAdapter.add(typeOfRound.get(typeOfRound.size() - 1));
          cycleRoundsAdapter.setFadeInPosition(roundSelectedPosition);
          cycleRoundsAdapter.notifyDataSetChanged();
        } else {
          showToastIfNoneActive("Full!");
        }
      } else {
        workoutTimeIntegerArray.set(roundSelectedPosition, integerValue * 1000);
        convertedWorkoutTimeStringArray.set(roundSelectedPosition, longToStringConverters.convertSecondsToMinutesBasedString(integerValue));
        typeOfRound.set(roundSelectedPosition, roundType);

        workoutStringListOfRoundValuesForFirstAdapter.set(roundSelectedPosition, convertedWorkoutTimeStringArray.get(roundSelectedPosition));
        workoutIntegerListOfRoundTypeForFirstAdapter.set(roundSelectedPosition, typeOfRound.get(roundSelectedPosition));

        cycleRoundsAdapter.setIsRoundCurrentlySelectedBoolean(false);
        cycleRoundsAdapter.setFadeInPosition(roundSelectedPosition);
        cycleRoundsAdapter.notifyDataSetChanged();

        roundIsSelected = false;
      }
    }
  }

  //Execute at end of fade animation or if another round is manipulated during a fade animation.
  private void removeRound() {
    if (mode == 1) {
      //Integer array covers both adapters.
      if (workoutTimeIntegerArray.size() > 0) {
        if (workoutTimeIntegerArray.size() - 1 >= roundSelectedPosition) {
          if (workoutStringListOfRoundValuesForFirstAdapter.size() - 1 >= roundSelectedPosition) {
            workoutStringListOfRoundValuesForFirstAdapter.remove(roundSelectedPosition);
            workoutIntegerListOfRoundTypeForFirstAdapter.remove(roundSelectedPosition);
            cycleRoundsAdapter.setFadeOutPosition(-1);
          }

          typeOfRound.remove(roundSelectedPosition);
          workoutTimeIntegerArray.remove(roundSelectedPosition);
          convertedWorkoutTimeStringArray.remove(roundSelectedPosition);

          subtractedRoundIsFading = false;
        }
        if (roundIsSelected) {
          cycleRoundsAdapter.setIsRoundCurrentlySelectedBoolean(false);
          roundIsSelected = false;
        }
        roundSelectedPosition = workoutTimeIntegerArray.size() - 1;
        cycleRoundsAdapter.notifyDataSetChanged();
      } else {
        showToastIfNoneActive("Empty!");
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

  private void launchTimerCycle(int typeOfLaunch) {
    if (workoutTimeIntegerArray.size() == 0) {
      showToastIfNoneActive("Cycle cannot be empty!");
      return;
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

        runOnUiThread(()-> {
          toggleCycleAndPomCycleRecyclerViewVisibilities(true);
        });
      }

      if (typeOfLaunch == CYCLE_LAUNCHED_FROM_RECYCLER_VIEW) {
        cycleHasActivityAssigned = savedCycleAdapter.getBooleanDeterminingIfCycleHasActivity(positionOfSelectedCycleForModeOne);
      }

      if (cycleHasActivityAssigned) {
        insertActivityIntoDatabaseAndAssignItsValueToObjects();
        trackActivityWithinCycle = savedCycleAdapter.getBooleanDeterminingIfWeAreTrackingActivity(positionOfSelectedCycleForModeOne);
      } else {
        trackActivityWithinCycle = false;
      }

      saveAddedOrEditedCycleASyncRunnable();
      queryAndSortAllCyclesFromMenu();
      clearAndRepopulateCycleAdapterListsFromDatabaseList(false);

      if (!trackActivityWithinCycle) {
        setCyclesOrPomCyclesEntityInstanceToSelectedListPosition(positionOfSelectedCycleForModeOne);
      }

      if (isNewCycle) {
        zeroOutTotalCycleTimes();
//        positionOfSelectedCycleForModeOne = workoutCyclesArray.size() - 1;
      } else {
        retrieveTotalSetAndBreakAndCompletedCycleValuesFromCycleList();
      }

      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          setTimerLaunchLogic(trackActivityWithinCycle);
          setTimerLaunchViews(typeOfLaunch);
          resetCyclesTimer();
        }
      });
    });

    timeLeftForCyclesTimer.setVisibility(View.VISIBLE);
    progressBar.setVisibility(View.VISIBLE);
    resetButtonForCycles.setVisibility(View.VISIBLE);

    Calendar calendar = Calendar.getInstance(Locale.getDefault());
    dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

    if (savedCycleAdapter.isCycleActive()) {
      savedCycleAdapter.removeCycleAsActive();
      savedCycleAdapter.notifyDataSetChanged();
    }
  }

  private void launchPomTimerCycle(int typeOfLaunch) {
    if (pomValuesTime.size() == 0) {
      showToastIfNoneActive("Cycle cannot be empty!");
      return;
    }

    timeLeftForPomCyclesTimer.setVisibility(View.VISIBLE);
    progressBarForPom.setVisibility(View.VISIBLE);
    resetButtonForPomCycles.setVisibility(View.VISIBLE);

    if (savedPomCycleAdapter.isCycleActive()) {
      savedPomCycleAdapter.removeCycleAsActive();
      savedPomCycleAdapter.notifyDataSetChanged();
    }

    if (typeOfLaunch == CYCLES_LAUNCHED_FROM_EDIT_POPUP) {
      if (cycleNameEdit.getText().toString().isEmpty()) {
        cycleTitle = getCurrentDateAsFullTextString();
      } else {
        cycleTitle = cycleNameEdit.getText().toString();
      }

      toggleCycleAndPomCycleRecyclerViewVisibilities(true);
    }

    AsyncTask.execute(() -> {
      saveAddedOrEditedCycleASyncRunnable();
      queryAndSortAllCyclesFromMenu();
      clearAndRepopulateCycleAdapterListsFromDatabaseList(false);

      if (isNewCycle) {
        zeroOutTotalCycleTimes();
        positionOfSelectedCycleForModeThree = pomArray.size() - 1;
      } else {
        retrieveTotalSetAndBreakAndCompletedCycleValuesFromCycleList();
      }

      setCyclesOrPomCyclesEntityInstanceToSelectedListPosition(positionOfSelectedCycleForModeThree);

      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          setTimerLaunchViews(typeOfLaunch);
          setTimerLaunchLogic(false);
          resetPomCyclesTimer();
        }
      });
    });
  }

  private void setTimerLaunchLogic(boolean trackingActivity) {
    toggleViewsForTotalDailyAndCycleTimes(trackingActivity);

    retrieveTotalDailySetAndBreakTimes();
    retrieveTotalTimesAndCaloriesForSpecificActivityOnCurrentDayVariables();

    clearRoundAndCycleAdapterArrayLists();
    populateCycleRoundAndRoundTypeArrayLists(true);

    if (trackingActivity) {
      setAllActivityTimesAndCaloriesToTextViews();
    } else {
      setTotalCycleTimeValuesToTextView();
    }
  }

  private void setTimerLaunchViews(int typeOfLaunch) {
    timerPopUpIsVisible = true;
    cycle_title_textView.setText(cycleTitle);
    cycle_title_textView_with_activity.setText(cycleTitle);

    adjustDotRecyclerLayoutMargins();

    if (mode == 1) {
      changeTextSizeWithoutAnimator(workoutTimeIntegerArray.get(0));
    }
    if (mode == 3) {
      changeTextSizeWithoutAnimator(pomValuesTime.get(0));
    }

    if (editCyclesPopupWindow.isShowing()) {
      editCyclesPopupWindow.dismiss();
    }

    timerPopUpWindow.showAtLocation(mainView, Gravity.NO_GRAVITY, 0, 0);

  }

  private void timerPopUpDismissalLogic() {
    timerPopUpWindow.dismiss();

    timerPopUpIsVisible = false;

    if (mode == 1) {
      stateOfTimers.setModeOneTimerDisabled(false);

      timeLeftForCyclesTimer.setVisibility(View.GONE);
      progressBar.setVisibility(View.GONE);
      resetButtonForCycles.setVisibility(View.GONE);

      savedCycleAdapter.notifyDataSetChanged();

      if (trackActivityWithinCycle) {
//        storeDailyTimesForCycleResuming();
        AsyncTask.execute(()-> {
          setAndUpdateActivityTimeAndCaloriesInDatabase();

          //This is used if we're converting our Strings in timer popUp to millis. Needed if we go back to rounding instead of using milliseconds when dismissing timer.
//          setAndUpdateActivityTimeAndCaloriesInDatabaseFromConvertedString();
        });
      }  else {
        storeSetAndBreakTimeForCycleResuming();
      }

      deleteLastAccessedActivityCycleIfItHasZeroTime(positionOfSelectedCycleForModeOne);

    } else if (mode == 3) {
      stateOfTimers.setModeThreeTimerDisabled(false);

      timeLeftForPomCyclesTimer.setVisibility(View.GONE);
      progressBarForPom.setVisibility(View.GONE);
      resetButtonForPomCycles.setVisibility(View.GONE);

      storeSetAndBreakTimeForPomCycleResuming();
      savedPomCycleAdapter.notifyDataSetChanged();
    }

    toggleSortButtonBasedOnIfCycleIsActive();

  }

  private void insertActivityIntoDatabaseAndAssignItsValueToObjects() {
    dailyStatsAccess.setOldDayHolderId(dayOfYear);

    dailyStatsAccess.setActivityString(getTdeeActivityStringFromArrayPosition());
    dailyStatsAccess.setMetScoreFromSpinner(metScore);

    dailyStatsAccess.setStatForEachActivityListForForSingleDayFromDatabase(dayOfYear);

    if (dailyStatsAccess.doesActivityExistsForSpecificDay()) {
      dailyStatsAccess.setActivityPositionInListForCurrentDayForExistingActivity();
      //We get an updated mStats entity here.
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
  }

  private void retrieveTotalTimesAndCaloriesForSpecificActivityOnCurrentDayVariables() {
    totalSetTimeForSpecificActivityForCurrentDayInMillis = dailyStatsAccess.getTotalSetTimeForSelectedActivity();
    totalCaloriesBurnedForSpecificActivityForCurrentDay = dailyStatsAccess.getTotalCaloriesBurnedForSelectedActivity();
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
  String pomString = "";

  private void saveAddedOrEditedCycleASyncRunnable() {
    Gson gson = new Gson();
    String workoutString = "";
    String roundTypeString = "";
    int cycleID;

    if (mode == 1) {
      if (isNewCycle) {
        cycles = new Cycles();
      } else if (cyclesList.size() > 0) {
        cycleID = cyclesList.get(positionOfSelectedCycleForModeOne).getId();
        cycles = cyclesDatabase.cyclesDao().loadSingleCycle(cycleID).get(0);
      }

      workoutString = gson.toJson(workoutTimeIntegerArray);
      workoutString = friendlyString(workoutString);
      roundTypeString = gson.toJson(typeOfRound);
      roundTypeString = friendlyString(roundTypeString);

      Log.i("testSave", "workout String being converted is " + workoutString);
      Log.i("testSave", "saving at position " + positionOfSelectedCycleForModeOne);
      Log.i("testSave", "integer array being saved is " + workoutTimeIntegerArray);

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
        cycles.setItemCount(workoutTimeIntegerArray.size());
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
        cycleID = pomCyclesList.get(positionOfSelectedCycleForModeThree).getId();
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

  private void queryAndSortAllCyclesFromMenu() {
    if (mode == 1) {
      switch (sortMode) {
        case 0:
          cyclesList = cyclesDatabase.cyclesDao().loadCyclesByLeastRecent();
          break;
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
    }

    if (mode == 3) {
      switch (sortModePom) {
        case 1:
          pomCyclesList = cyclesDatabase.cyclesDao().loadPomCyclesLeastRecent();
          break;
        case 2:
          pomCyclesList = cyclesDatabase.cyclesDao().loadPomCyclesMostRecent();
          break;
        case 3:
          pomCyclesList = cyclesDatabase.cyclesDao().loadPomAlphaStart();
          break;
        case 4:
          pomCyclesList = cyclesDatabase.cyclesDao().loadPomAlphaEnd();
          break;
      }
    }
  }

//When database sort order changes, our arrays are in a different order than the one we've set our position to fetch.
  private void clearAndRepopulateCycleAdapterListsFromDatabaseList(boolean forAllModes) {
    if (mode == 1 || forAllModes) {
      workoutCyclesArray.clear();
      typeOfRoundArray.clear();
      workoutTitleArray.clear();
      workoutActivityStringArray.clear();
      tdeeActivityExistsInCycleList.clear();
      tdeeIsBeingTrackedInCycleList.clear();

      if (isNewCycle) {
        int max = 0;

        for(int i=0; i<cyclesList.size(); i++) {
          if (cyclesList.get(i).getId() > max) {
            max = cyclesList.get(i).getId();
          }
        }

        primaryIdOfCycleRowSelected = max;
      }

      int cycleIdFromPreSortedPosition = primaryIdOfCycleRowSelected;

      //Fetches an updated position of our sorted rows by retrieving the position of our old position's primaryId.
      for (int i = 0; i<cyclesList.size(); i++) {
        if (cyclesList.get(i).getId() == cycleIdFromPreSortedPosition) {
          sortedPositionOfSelectedCycleForModeOne = i;
        }
      }


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

      int cycleIdFromPreSortedPosition = primaryIdOfPomCycleRowSelected;

      for (int i = 0; i<pomCyclesList.size(); i++) {
        if (pomCyclesList.get(i).getId() == cycleIdFromPreSortedPosition) {
          sortedPositionOfSelectedCycleForModeThree = i;
        }
      }

      for (int i = 0; i < pomCyclesList.size(); i++) {
        pomArray.add(pomCyclesList.get(i).getFullCycle());
        pomTitleArray.add(pomCyclesList.get(i).getTitle());
      }
    }
  }

//From the new order of our arrays, the original fetched position is incorrect.
  private void populateCycleRoundAndRoundTypeArrayLists(boolean executingFromTimerLaunch) {
    switch (mode) {
      case 1:
        workoutTimeIntegerArray.clear();
        typeOfRound.clear();

        //Todo: Here, we set the old var to the new var, so we shouldn't need the new one set anywhere else.
        //Sets our "old" global positional variable to the new, sorted one if necessary.
        if (executingFromTimerLaunch) {
          positionOfSelectedCycleForModeOne = sortedPositionOfSelectedCycleForModeOne;
        }

        String[] fetchedRounds = workoutCyclesArray.get(positionOfSelectedCycleForModeOne).split(" - ");
        String[] fetchedRoundType = typeOfRoundArray.get(positionOfSelectedCycleForModeOne).split(" - ");

        for (int i = 0; i < fetchedRounds.length; i++) {
          workoutTimeIntegerArray.add(Integer.parseInt(fetchedRounds[i]));
        }
        for (int j = 0; j < fetchedRoundType.length; j++) {
          typeOfRound.add(Integer.parseInt(fetchedRoundType[j]));
        }

        ArrayList<String> convertedWorkoutRoundList = convertMillisIntegerListToTimerStringList(workoutTimeIntegerArray);

        adjustDotRecyclerViewSize(convertedWorkoutRoundList.size());
        dotsAdapter.setCycleRoundsAsStringsList(convertedWorkoutRoundList);
        dotsAdapter.setTypeOfRoundList(typeOfRound);
        dotsAdapter.notifyDataSetChanged();

        Log.i("testLaunch", "string array in populateCycle method is " + workoutCyclesArray);
        Log.i("testLaunch", "fetched array from string in populateCycle method is " + workoutCyclesArray);
        Log.i("testLaunch", "integer array in populateCycle method is " + workoutTimeIntegerArray);

        cycleTitle = workoutTitleArray.get(positionOfSelectedCycleForModeOne);
        break;
      case 3:
        pomValuesTime.clear();
        pomStringListOfRoundValues.clear();

        if (executingFromTimerLaunch) {
          positionOfSelectedCycleForModeThree = sortedPositionOfSelectedCycleForModeThree;
        }

        if (pomArray.size() - 1 >= positionOfSelectedCycleForModeThree) {
          String[] fetchedPomCycle = pomArray.get(positionOfSelectedCycleForModeThree).split(" - ");

          /////---------Testing pom round iterations---------------/////////
//          for (int i=0; i<8; i++) if (i%2!=0) pomValuesTime.add(2000); else pomValuesTime.add(3000);

          for (int i = 0; i < fetchedPomCycle.length; i++) {
            int integerValue = Integer.parseInt(fetchedPomCycle[i]);
            pomValuesTime.add(integerValue);
            pomStringListOfRoundValues.add(longToStringConverters.convertSecondsToMinutesBasedString(integerValue / 1000));
          }

          pomDotsAdapter.setPomCycleRoundsAsStringsList(pomStringListOfRoundValues);
          pomDotsAdapter.updatePomDotCounter(pomDotCounter);
          pomDotsAdapter.notifyDataSetChanged();

          cycleTitle = pomTitleArray.get(positionOfSelectedCycleForModeThree);
        }
        break;
    }
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

  private long dividedMillisForTimerDisplay(long millis) {
    return (millis + 999) / 1000;
  }

//  private void updateDailyStatTextViewsIfTimerHasAlsoUpdated(TextViewDisplaySync textViewDisplaySync) {
//    textViewDisplaySync.setFirstTextView((String) timeLeft.getText());
//
//    if (textViewDisplaySync.areTextViewsDifferent()) {
//      textViewDisplaySync.setSecondTextView(textViewDisplaySync.getFirstTextView());
//      setTotalDailyActivityTimeToTextView();
//      setTotalSingleActivityTimeToTextView();
//    }
//
//    setTotalDailyCaloriesToTextView();
//    setTotalSingleActivityCaloriesToTextView();
//  }

  private void setAllActivityTimesAndCaloriesToTextViews() {
    setTotalDailyActivityTimeToTextView();
    setTotalDailyCaloriesToTextView();
    setTotalSingleActivityTimeToTextView();
    setTotalSingleActivityCaloriesToTextView();
    dailySingleActivityStringHeader.setText(getTdeeActivityStringFromArrayPosition());
  }

  private void setTotalDailyActivityTimeToTextView() {
    String timeInStringFormat = longToStringConverters.convertMillisToHourBasedStringForCycleTimesWithMilliseconds(totalSetTimeForCurrentDayInMillis);

    if (phoneHeight <= 1920) {
      dailyTotalTimeTextView.setText(changeDisplayOfStatsMillisValues(timeInStringFormat, 14));

    } else {
      dailyTotalTimeTextView.setText(changeDisplayOfStatsMillisValues(timeInStringFormat, 17));
    }
  }

  private void setTotalDailyCaloriesToTextView() {
    String calorieString = formatCalorieString(totalCaloriesBurnedForCurrentDay);

    if (phoneHeight <= 1920) {
      dailyTotalCaloriesTextView.setText(changeDisplayOfStatsMillisValues(calorieString, 14));

    } else {
      dailyTotalCaloriesTextView.setText(changeDisplayOfStatsMillisValues(calorieString, 17));
    }
  }

  private void setTotalSingleActivityTimeToTextView() {
    String timeInStringFormat = longToStringConverters.convertMillisToHourBasedStringForCycleTimesWithMilliseconds(totalSetTimeForSpecificActivityForCurrentDayInMillis);

    if (phoneHeight <= 1920) {
      dailyTotalTimeForSingleActivityTextView.setText(changeDisplayOfStatsMillisValues(timeInStringFormat, 14));

    } else {
      dailyTotalTimeForSingleActivityTextView.setText(changeDisplayOfStatsMillisValues(timeInStringFormat, 17));
    }
  }

  private void setTotalSingleActivityCaloriesToTextView() {
    String calorieString = formatCalorieString(totalCaloriesBurnedForSpecificActivityForCurrentDay);

    if (phoneHeight <= 1920) {
      dailyTotalCaloriesForSingleActivityTextView.setText(changeDisplayOfStatsMillisValues(calorieString, 14));
    } else {
      dailyTotalCaloriesForSingleActivityTextView.setText(changeDisplayOfStatsMillisValues(calorieString, 17));
    }
  }

  private CharSequence changeDisplayOfStatsMillisValues(String fullTimeString, int size) {
    String[] splitString = fullTimeString.split("\\.");
    String mainTime = splitString[0] + ".";
    String msTime = splitString[1];

    Spannable spannable = new SpannableString(msTime);
    spannable.setSpan(new AbsoluteSizeSpan(17, true), 0, 2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

    return TextUtils.concat(mainTime, spannable);
  }

  private void updateCycleTimesTextViewsIfTimerHasAlsoUpdated(TextViewDisplaySync textViewDisplaySync) {
    if (mode == 1) {
      textViewDisplaySync.setModeOneFirstTextView((String) timeLeftForCyclesTimer.getText());

      if (textViewDisplaySync.areModeOneTextViewsDifferent()) {
        textViewDisplaySync.setModeOneSecondTextView(textViewDisplaySync.getModeOneFirstTextView());

        setTotalCycleTimeValuesToTextView();
      }
    }

    if (mode == 3) {
      textViewDisplaySync.setModeThreeFirstTextView((String) timeLeftForPomCyclesTimer.getText());

      if (textViewDisplaySync.areModeThreeTextViewsDifferent()) {
        textViewDisplaySync.setModeThreeSecondTextView(textViewDisplaySync.getModeThreeFirstTextView());

        setTotalCycleTimeValuesToTextView();
      }
    }
  }

  private void updateRecyclerViewStringIfTimerHasAlsoUpdated(TextViewDisplaySync textViewDisplaySync) {
    if (mode == 1) {
      textViewDisplaySync.setModeOneFirstTextView((String) timeLeftForCyclesTimer.getText());

      if (textViewDisplaySync.areModeOneTextViewsDifferent()) {
        textViewDisplaySync.setModeOneSecondTextView(textViewDisplaySync.getModeOneFirstTextView());

        savedCycleAdapter.notifyDataSetChanged();

      }
    }

    if (mode == 3) {
      textViewDisplaySync.setModeThreeFirstTextView((String) timeLeftForPomCyclesTimer.getText());

      if (textViewDisplaySync.areModeThreeTextViewsDifferent()) {
        textViewDisplaySync.setModeThreeSecondTextView(textViewDisplaySync.getModeThreeFirstTextView());

        savedPomCycleAdapter.notifyDataSetChanged();
      }
    }
  }

  private void setCyclesCompletedTextView() {
    cycles_completed_textView.setText(getString(R.string.cycles_done, cyclesCompleted));
  }

  private void setTotalCycleTimeValuesToTextView() {
    if (mode == 1) {
      total_set_time.setText(longToStringConverters.convertMillisToHourBasedStringForCycleTimes(totalCycleSetTimeInMillis));
      total_break_time.setText(longToStringConverters.convertMillisToHourBasedStringForCycleTimes(totalCycleBreakTimeInMillis));
    }
    if (mode == 3) {
      total_set_time.setText(longToStringConverters.convertMillisToHourBasedStringForCycleTimes(totalCycleWorkTimeInMillis));
      total_break_time.setText(longToStringConverters.convertMillisToHourBasedStringForCycleTimes(totalCycleRestTimeInMillis));
    }
  }

  private void roundCycleTimeValuesToNearestThousandth() {
    totalCycleSetTimeInMillis = roundToNearestFullThousandth(totalCycleSetTimeInMillis);
    totalCycleBreakTimeInMillis = roundToNearestFullThousandth(totalCycleBreakTimeInMillis);
  }

  private void roundDownCycleTimeValues() {
    totalCycleSetTimeInMillis = roundDownMillisValues(totalCycleSetTimeInMillis);
    totalCycleBreakTimeInMillis = roundDownMillisValues(totalCycleBreakTimeInMillis);

  }

  private void roundDownPomCycleTimeValues() {
    totalCycleRestTimeInMillis = roundDownMillisValues(totalCycleRestTimeInMillis);
    totalCycleWorkTimeInMillis = roundDownMillisValues(totalCycleWorkTimeInMillis);
  }

  private void roundPomCycleTimeValuesToNearestThousandth() {
    totalCycleWorkTimeInMillis = roundToNearestFullThousandth(totalCycleWorkTimeInMillis);
    totalCycleRestTimeInMillis = roundToNearestFullThousandth(totalCycleRestTimeInMillis);
  }

  private void roundDailyStatTimesDown() {
    totalSetTimeForCurrentDayInMillis = roundDownMillisValues(totalSetTimeForCurrentDayInMillis);
    totalSetTimeForSpecificActivityForCurrentDayInMillis = roundDownMillisValues(totalSetTimeForSpecificActivityForCurrentDayInMillis);
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

  private long roundToNearestFullThousandth(long valueToRound) {
    long remainder = valueToRound % 1000;
    if (remainder <= 500) {
      return valueToRound -= remainder;
    } else {
      return valueToRound += (1000 - remainder);
    }
  }

  private void setCycleTimeToIterate() {
    if (mode == 1) {
      if (typeOfRound.get(currentRoundForModeOne) == 1 || typeOfRound.get(currentRoundForModeOne) == 2) {
        CYCLE_TIME_TO_ITERATE = CYCLE_SETS;
      }
      if (typeOfRound.get(currentRoundForModeOne) == 3 || typeOfRound.get(currentRoundForModeOne) == 4) {
        CYCLE_TIME_TO_ITERATE = CYCLE_BREAKS;
      }
    }

    if (mode == 3) {
      switch (pomDotCounter) {
        case 0: case 2: case 4: case 6:
          POM_CYCLE_TIME_TO_ITERATE = POM_CYCLE_WORK;
          break;
        case 1: case 3: case 5: case 7:
          POM_CYCLE_TIME_TO_ITERATE = POM_CYCLE_REST;
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

        displayTime = longToStringConverters.convertSecondsToMinutesBasedString((long) stopWatchSeconds);
        displayMs = df2.format(stopWatchMs);

        stopWatchTimeTextView.setText(displayTime);
        msTimeTextView.setText(displayMs);

//        decreaseTextSizeForTimersForStopWatch(stopWatchTotalTime);

        if (stopWatchTotalTime < 3600000) {
          mHandler.postDelayed(this, 10);
        } else {
          animateStopwatchEnding();
          new_lap.setEnabled(false);

          stateOfTimers.setStopWatchTimerEnded(true);
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
    textViewDisplaySync.setModeOneFirstTextView((String) timeLeftForCyclesTimer.getText());
    textViewDisplaySync.setModeThreeFirstTextView((String) timeLeftForPomCyclesTimer.getText());
    textViewDisplaySync.setStopWatchFirstTextView((String) stopWatchTimeTextView.getText());

    if (textViewDisplaySync.areModeOneTextViewsDifferent()) {
      textViewDisplaySync.setModeOneSecondTextView(textViewDisplaySync.getModeOneFirstTextView());
      setNotificationValues();
    }

    if (textViewDisplaySync.areModeThreeTextViewsDifferent()) {
      textViewDisplaySync.setModeThreeSecondTextView(textViewDisplaySync.getModeThreeFirstTextView());
      setNotificationValues();
    }

    if (textViewDisplaySync.areStopWatchTextViewsDifferent()) {
      textViewDisplaySync.setStopWatchSecondTextView(textViewDisplaySync.getStopWatchFirstTextView());
      setNotificationValues();
    }

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
    textViewDisplaySync.setModeOneFirstTextView((String) timeLeftForCyclesTimer.getText());
    textViewDisplaySync.setModeOneSecondTextView((String) timeLeftForCyclesTimer.getText());

    setNotificationValues();

    return new Runnable() {
      @Override
      public void run() {
        timerIteration.setCurrentTime(System.currentTimeMillis());

        long timeToIterate = timerIteration.getDifference();
        double caloriesToIterate = calculateCaloriesBurnedPerMillis() * timeToIterate;

        timerIteration.setNewDailyTotal(timerIteration.getPreviousDailyTotal() + timeToIterate);
        timerIteration.setNewActivityTotal(timerIteration.getPreviousActivityTotal() + timeToIterate);
        totalSetTimeForCurrentDayInMillis = timerIteration.getNewDailyTotal();
        totalSetTimeForSpecificActivityForCurrentDayInMillis = timerIteration.getNewActivityTotal();


        calorieIteration.setNewTotalCalories(calorieIteration.getPreviousTotalCalories() + caloriesToIterate);
        calorieIteration.setNewActivityCalories(calorieIteration.getPreviousActivityCalories() + caloriesToIterate);
        totalCaloriesBurnedForCurrentDay = calorieIteration.getNewTotalCalories();
        totalCaloriesBurnedForSpecificActivityForCurrentDay = calorieIteration.getNewActivityCalories();

        setAllActivityTimesAndCaloriesToTextViews();

        AsyncTask.execute(() -> {
          updateActiveTimerPopUpStatsIfEdited();
        });

        mHandler.postDelayed(this, 10);

        if ((trackActivityWithinCycle && isDailyActivityTimeMaxed())) {
          mHandler.removeCallbacks(infinityRunnableForDailyActivityTimer);
        }
      }
    };
  }

  private void updateActiveTimerPopUpStatsIfEdited() {
    if (trackActivityWithinCycle && dailyStatsFragment.getHaveStatsBeenEditedForCurrentDay()) {
      insertActivityIntoDatabaseAndAssignItsValueToObjects();
      dailyStatsFragment.setStatsHaveBeenEditedForCurrentDay(false);

      runOnUiThread(()-> {
        retrieveTotalTimesAndCaloriesForSpecificActivityOnCurrentDayVariables();
        setAllActivityTimesAndCaloriesToTextViews();

        mHandler.removeCallbacks(infinityRunnableForDailyActivityTimer);
        infinityRunnableForDailyActivityTimer = infinityRunnableForDailyActivityTime();
        mHandler.post(infinityRunnableForDailyActivityTimer);
      });
    }
  }

  private TimerIteration newTimerIterationInstance() {
    TimerIteration timerIteration = new TimerIteration();
    timerIteration.setStableTime(System.currentTimeMillis());

    return timerIteration;
  }

  private TextViewDisplaySync textViewDisplaySyncForModeOne() {
    TextViewDisplaySync textViewDisplaySync = new TextViewDisplaySync();
    textViewDisplaySync.setModeOneFirstTextView((String) timeLeftForCyclesTimer.getText());
    textViewDisplaySync.setModeOneSecondTextView((String) timeLeftForCyclesTimer.getText());

    return textViewDisplaySync;
  }

  private TextViewDisplaySync textViewDisplaySyncForModeThree() {
    TextViewDisplaySync textViewDisplaySync = new TextViewDisplaySync();
    textViewDisplaySync.setModeThreeFirstTextView((String) timeLeftForPomCyclesTimer.getText());
    textViewDisplaySync.setModeThreeSecondTextView((String) timeLeftForPomCyclesTimer.getText());

    return textViewDisplaySync;
  }

  private Runnable runnableForSetAndBreakTotalTimes() {
    setCycleTimeToIterate();

    TimerIteration timerIteration = newTimerIterationInstance();
    TextViewDisplaySync textViewDisplaySync = textViewDisplaySyncForModeOne();

    if (CYCLE_TIME_TO_ITERATE == CYCLE_SETS) {
      timerIteration.setPreviousTotal(totalCycleSetTimeInMillis);
    }
    if (CYCLE_TIME_TO_ITERATE == CYCLE_BREAKS) {
      timerIteration.setPreviousTotal(totalCycleBreakTimeInMillis);
    }

    return new Runnable() {
      @Override
      public void run() {
        if (resetCycleTimeVarsWithinRunnable) {
          timerIteration.setStableTime(System.currentTimeMillis());

          long remainder = timerIteration.getNewTotal() % 1000;
          long roundedRemainder = roundToNearestFullThousandth(remainder);
          timerIteration.setPreviousTotal(1000);

          resetCycleTimeVarsWithinRunnable = false;
        }

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

  private Runnable runnableForRecyclerViewTimesForModeOne() {
    ArrayList<Integer> integerArrayList = integerArrayOfRoundStringsForModeOne();

    TimerIteration timerIteration = newTimerIterationInstance();
    TextViewDisplaySync textViewDisplaySync = textViewDisplaySyncForModeOne();

    return new Runnable() {
      @Override
      public void run() {
        updateRecyclerViewStringIfTimerHasAlsoUpdated(textViewDisplaySync);

        iterateRecyclerViewTimesOnModeOne(integerArrayList);
        savedCycleAdapter.setNumberOfRoundsCompleted(startRoundsForModeOne - numberOfRoundsLeftForModeOne);

        mHandler.postDelayed(this, 50);
      }
    };
  }

  private void iterateRecyclerViewTimesOnModeOne(ArrayList<Integer> integerArrayList) {
    ArrayList<Integer> integerCycleArray = integerArrayList;

    int fetchedRoundType = typeOfRound.get(currentRoundForModeOne);
    workoutCyclesArray.set(positionOfSelectedCycleForModeOne, newRoundStringForModeOne(fetchedRoundType));
  }

  private String newRoundStringForModeOne(int typeOfRound) {
    Gson gson = new Gson();
    ArrayList<Integer> arrayListToConvert = integerArrayOfRoundStringsForModeOne();

    long millisValueRetrieved = 0;
    long millisValueToSet = 0;

    int currentRoundPosition = numberOfRoundsLeftForModeOne - (numberOfRoundsLeftForModeOne - currentRoundForModeOne);

    if (typeOfRound == 1 || typeOfRound == 2) {
      millisValueRetrieved = setMillis;
      if (typeOfRound == 1) {
        millisValueToSet = setMillis + 999;
      }
      if (typeOfRound == 2) {
        millisValueToSet = setMillis;
      }
    }

    if (typeOfRound == 3 || typeOfRound == 4) {
      millisValueRetrieved = breakMillis;
      if (typeOfRound == 3) {
        millisValueToSet = breakMillis + 999;
      }
      if (typeOfRound == 4) {
        millisValueToSet = breakMillis;
      }
    }

    if (typeOfRound == 1 || typeOfRound == 3) {
      if (millisValueRetrieved < 250) {
        millisValueToSet = 0;
      }
    }

    arrayListToConvert.set(currentRoundPosition, (int) millisValueToSet);

    String newRoundString = "";
    newRoundString = gson.toJson(arrayListToConvert);
    newRoundString = friendlyString(newRoundString);

    return newRoundString;
  }

  private ArrayList<Integer> integerArrayOfRoundStringsForModeOne() {
    String[] fetchedRounds = {};
    ArrayList<Integer> newIntegerArray = new ArrayList<>();

    if (workoutCyclesArray.size() - 1  >= positionOfSelectedCycleForModeOne) {
      fetchedRounds = workoutCyclesArray.get(positionOfSelectedCycleForModeOne).split(" - ");
    }

    for (int i = 0; i<fetchedRounds.length; i++) {
      newIntegerArray.add(Integer.parseInt(fetchedRounds[i]));
    }

    return newIntegerArray;
  }

  private Runnable runnableForRecyclerViewTimesForModeThree() {
    ArrayList<Integer> integerArrayList = integerArrayOfRoundStringsForModeThree();

    TimerIteration timerIteration = newTimerIterationInstance();
    TextViewDisplaySync textViewDisplaySync = textViewDisplaySyncForModeThree();

    return new Runnable() {
      @Override
      public void run() {
        updateRecyclerViewStringIfTimerHasAlsoUpdated(textViewDisplaySync);

        iterateRecyclerViewTimesOnModeThree(integerArrayList);
        savedPomCycleAdapter.setNumberOfRoundsCompleted(startRoundsForModeThree - numberOfRoundsLeftForModeThree);

        mHandler.postDelayed(this, 50);
      }
    };
  }

  private void iterateRecyclerViewTimesOnModeThree(ArrayList<Integer> integerArrayList) {
    ArrayList<Integer> integerCycleArray = integerArrayList;
    pomArray.set(positionOfSelectedCycleForModeThree, newRoundStringForModeThree());
  }

  private ArrayList<Integer> integerArrayOfRoundStringsForModeThree() {
    String[] fetchedRounds = {};
    ArrayList<Integer> newIntegerArray = new ArrayList<>();

    if (pomArray.size() - 1 >= positionOfSelectedCycleForModeThree)  {
      fetchedRounds = pomArray.get(positionOfSelectedCycleForModeThree).split(" - ");
    }

    for (int i = 0; i<fetchedRounds.length; i++) {
      newIntegerArray.add(Integer.parseInt(fetchedRounds[i]));
    }

    return newIntegerArray;
  }

  private String newRoundStringForModeThree() {
    Gson gson = new Gson();
    ArrayList<Integer> arrayListToConvert = integerArrayOfRoundStringsForModeThree();

    int currentRoundPosition = numberOfRoundsLeftForModeThree - (numberOfRoundsLeftForModeThree - currentRoundForModeThree);

    long millisValueRetrieved = pomMillis;
    long millisValueToSet = pomMillis +999;

    if (millisValueRetrieved < 250) {
      millisValueToSet = 0;
    }

    arrayListToConvert.set(currentRoundPosition, (int) millisValueToSet);

    String newRoundString = "";
    newRoundString = gson.toJson(arrayListToConvert);
    newRoundString = friendlyString(newRoundString);

    return newRoundString;
  }

  private Runnable runnableForWorkAndRestTotalTimes() {
    setCycleTimeToIterate();

    TimerIteration timerIteration = newTimerIterationInstance();
    TextViewDisplaySync textViewDisplaySync = textViewDisplaySyncForModeThree();

    if (POM_CYCLE_TIME_TO_ITERATE == POM_CYCLE_WORK) {
      timerIteration.setPreviousTotal(totalCycleWorkTimeInMillis);
    }
    if (POM_CYCLE_TIME_TO_ITERATE == POM_CYCLE_REST) {
      timerIteration.setPreviousTotal(totalCycleRestTimeInMillis);
    }

    return new Runnable() {
      @Override
      public void run() {
        if (resetCycleTimeVarsWithinRunnable) {
          timerIteration.setStableTime(System.currentTimeMillis());

          long remainder = timerIteration.getNewTotal() % 1000;
          long roundedRemainder = roundToNearestFullThousandth(remainder);
          timerIteration.setPreviousTotal(1000);

          resetCycleTimeVarsWithinRunnable = false;
        }

        timerIteration.setCurrentTime(System.currentTimeMillis());
        long timeToIterate = timerIteration.getDifference();
        timerIteration.setNewTotal(timerIteration.getPreviousTotal() + timeToIterate);

        if (POM_CYCLE_TIME_TO_ITERATE == POM_CYCLE_WORK) {
          totalCycleWorkTimeInMillis = timerIteration.getNewTotal();
        }
        if (POM_CYCLE_TIME_TO_ITERATE == POM_CYCLE_REST) {
          totalCycleRestTimeInMillis = timerIteration.getNewTotal();
        }

        updateCycleTimesTextViewsIfTimerHasAlsoUpdated(textViewDisplaySync);

        mHandler.postDelayed(this, 10);
      }
    };
  }

  private Runnable infinityRunnableForSetRounds() {
    if (valueAnimatorDown.isStarted()) {
      valueAnimatorDown.cancel();
    }

    if (mode==1) {
      setInitialTextSizeForTimers(0);
    }

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

        timeLeftForCyclesTimer.setText(longToStringConverters.convertSecondsToMinutesBasedString(setMillis/1000));

        if (workoutTimeIntegerArray.size() >= numberOfRoundsLeftForModeOne) {
          workoutTimeIntegerArray.set(workoutTimeIntegerArray.size() - numberOfRoundsLeftForModeOne, (int) setMillis);
        }

        ArrayList<String> convertedWorkoutRoundList = convertMillisIntegerListToTimerStringList(workoutTimeIntegerArray);

        dotsAdapter.setCycleRoundsAsStringsList(convertedWorkoutRoundList);
        dotsAdapter.notifyDataSetChanged();

        if (mode==1) {
          decreaseTextSizeForTimers(setMillis);
        }

        if (setMillis > 3600000) {
          setMillis = 3600000;
          nextRound(false);
          return;
        }

        mHandler.postDelayed(this, timerRunnableDelay);
      }
    };
  }

  private Runnable infinityRunnableForBreakRounds() {
    if (valueAnimatorDown.isStarted()) {
      valueAnimatorDown.cancel();
    }

    if (mode == 1) {
      setInitialTextSizeForTimers(0);
    }

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

        timeLeftForCyclesTimer.setText(longToStringConverters.convertSecondsToMinutesBasedString(breakMillis/1000));

        if (workoutTimeIntegerArray.size() >= numberOfRoundsLeftForModeOne) {
          workoutTimeIntegerArray.set(workoutTimeIntegerArray.size() - numberOfRoundsLeftForModeOne, (int) breakMillis);
        }

        ArrayList<String> convertedWorkoutRoundList = convertMillisIntegerListToTimerStringList(workoutTimeIntegerArray);

        dotsAdapter.setCycleRoundsAsStringsList(convertedWorkoutRoundList);
        dotsAdapter.notifyDataSetChanged();

        if (mode == 1) {
          decreaseTextSizeForTimers(breakMillis);
        }

        if (breakMillis > 3600000) {
          breakMillis = 3600000;
          nextRound(false);
          return;
        }
        mHandler.postDelayed(this, timerRunnableDelay);
      }
    };
  }

  private ArrayList<String> convertMillisIntegerListToTimerStringList(ArrayList<Integer> listToConvert) {
    ArrayList<String> listToReturn = new ArrayList<>();

    for (int i = 0; i < listToConvert.size(); i++) {
      listToReturn.add(longToStringConverters.convertSecondsToMinutesBasedString(listToConvert.get(i) / 1000));
    }

    return listToReturn;
  }

  private void startSetTimer() {
    long startMillis = setMillis;
    long initialMillisValue = setMillis;

    if (mode==1) {
      setInitialTextSizeForTimers(setMillis);
    }

    modeOneCountdownTimer = new CountDownTimer(setMillis, timerRunnableDelay) {
      @Override
      public void onTick(long millisUntilFinished) {
        currentProgressBarValueForModeOne = (int) objectAnimator.getAnimatedValue();

        setMillis = millisUntilFinished;
        timeLeftForCyclesTimer.setText(longToStringConverters.convertSecondsToMinutesBasedString(dividedMillisForTimerDisplay(setMillis)));

        if (setMillis < 500) {
          stateOfTimers.setModeOneTimerDisabled(true);
        }

        if (mode==1) {
          increaseTextSizeForTimers(startMillis, setMillis);
        }

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
    long initialMillisValue = breakMillis;

    if (mode == 1) {
      setInitialTextSizeForTimers(breakMillis);
    }

    modeOneCountdownTimer = new CountDownTimer(breakMillis, timerRunnableDelay) {
      @Override
      public void onTick(long millisUntilFinished) {
        currentProgressBarValueForModeOne = (int) objectAnimator.getAnimatedValue();
        breakMillis = millisUntilFinished;
        timeLeftForCyclesTimer.setText(longToStringConverters.convertSecondsToMinutesBasedString(dividedMillisForTimerDisplay(breakMillis)));
        if (breakMillis < 500) {
          stateOfTimers.setModeOneTimerDisabled(true);
        }

        if (mode == 1) {
          increaseTextSizeForTimers(startMillis, breakMillis);
        }

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
    long initialMillisValue = pomMillis;

    if (mode == 3) {
      setInitialTextSizeForTimers(pomMillis);
    }

    modeThreeCountDownTimer = new CountDownTimer(pomMillis, timerRunnableDelay) {
      @Override
      public void onTick(long millisUntilFinished) {

        currentProgressBarValueForModeThree = (int) objectAnimatorPom.getAnimatedValue();
        pomMillis = millisUntilFinished;

        timeLeftForPomCyclesTimer.setText(longToStringConverters.convertSecondsToMinutesBasedString(dividedMillisForTimerDisplay(pomMillis)));

        if (pomMillis < 500) {
          stateOfTimers.setModeThreeTimerDisabled(true);
        }

        if (mode == 3) {
          increaseTextSizeForTimers(startMillis, pomMillis);
          Log.i("testChange", "changing textSize!");
        }

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
        if (mode == 1){
          changeTextSizeWithAnimator(valueAnimatorUp, timeLeftForCyclesTimer);
        }
        if (mode == 3) {
          changeTextSizeWithAnimator(valueAnimatorUp, timeLeftForPomCyclesTimer);
        }
        setHasTextSizeChangedForTimers(true);
      }
    }
  }

  private void decreaseTextSizeForTimers(long millis) {
    if (!getHasTextSizeChangedForTimers()) {
      if (millis >= 60000) {
        if (mode == 1) {
          changeTextSizeWithAnimator(valueAnimatorDown, timeLeftForCyclesTimer);
        }
        if (mode == 3) {
          changeTextSizeWithAnimator(valueAnimatorDown, timeLeftForPomCyclesTimer);
        }
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
      if (phoneHeight <= 1920) {
        if (mode == 1) {
          timeLeftForCyclesTimer.setTextSize(70f);
        }
        if (mode == 3) {
          timeLeftForPomCyclesTimer.setTextSize(70f);
        }
      } else {
        if (mode == 1) {
          timeLeftForCyclesTimer.setTextSize(90f);
        }
        if (mode == 3) {
          timeLeftForPomCyclesTimer.setTextSize(90f);
        }
      }
    } else {
      if (phoneHeight <= 1920) {
        if (mode == 1) {
          timeLeftForCyclesTimer.setTextSize(90f);
        }
        if (mode == 3) {
          timeLeftForPomCyclesTimer.setTextSize(90f);
        }
      } else {
        if (mode == 1) {
          timeLeftForCyclesTimer.setTextSize(120f);
        }
        if (mode == 3) {
          timeLeftForPomCyclesTimer.setTextSize(120f);
        }
      }
    }
  }

  private void setInitialTextSizeForStopWatch() {
    if (valueAnimatorDown.isRunning()) {
      valueAnimatorDown.cancel();
    }

    if (phoneHeight <= 1920) {
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
    if (phoneHeight <= 1920) {
      valueAnimatorDown.setFloatValues(90f, 70f);
      valueAnimatorUp.setFloatValues(70f, 90f);
    } else {
      valueAnimatorDown.setFloatValues(120f, 90f);
      valueAnimatorUp.setFloatValues(90f, 120f);
    }
  }

  private void changeTextSizeWithoutAnimator(long millis) {
    if (phoneHeight <= 1920) {

      if (millis < 60000) {
        if (mode == 1) {
          timeLeftForCyclesTimer.setTextSize(90f);
        }
        if (mode == 3) {
          timeLeftForPomCyclesTimer.setTextSize(90f);
        }
      } else {
        if (mode == 1) {
          timeLeftForCyclesTimer.setTextSize(70f);
        }
        if (mode == 3) {
          timeLeftForPomCyclesTimer.setTextSize(70f);
        }
      }
    } else {
      if (millis < 60000) {
        if (mode == 1) {
          timeLeftForCyclesTimer.setTextSize(120f);
        }
        if (mode == 3) {
          timeLeftForPomCyclesTimer.setTextSize(120f);

        }
      } else {
        if (mode == 1) {
          timeLeftForCyclesTimer.setTextSize(90f);
        }
        if (mode == 3) {
          timeLeftForPomCyclesTimer.setTextSize(90f);
        }
      }
    }
  }

  private void nextRound(boolean endingEarly) {
    if (numberOfRoundsLeftForModeOne == 0) {
      mHandler.removeCallbacks(endFadeForModeOne);
      resetCyclesTimer();
      return;
    }

    //Refreshes so recyclerView timer iteration will display "0" for ending round. Needs to occur before round vars change below.
    savedCycleAdapter.notifyDataSetChanged();
    //Removed so next round in array does not display as "0".
    mHandler.removeCallbacks(runnableForRecyclerViewTimesForModeOne);
    removeActivityOrCycleTimeRunnables(trackActivityWithinCycle);

    stateOfTimers.setModeOneTimerPaused(false);
    stateOfTimers.setModeOneTimerDisabled(true);

    if (endingEarly) {
      if (modeOneCountdownTimer != null) modeOneCountdownTimer.cancel();
      if (objectAnimator != null) objectAnimator.cancel();
      progressBar.setProgress(0);
      savedCycleAdapter.setCycleAsActive();

      if (!trackActivityWithinCycle) {
        roundDownCycleTimeValues();
      }
    } else {
      if (typeOfRound.get(currentRoundForModeOne) == 1 || typeOfRound.get(currentRoundForModeOne) == 3) {
        timeLeftForCyclesTimer.setText("0");
      }
      if (!trackActivityWithinCycle) {
        roundCycleTimeValuesToNearestThousandth();
      }
    }

    if (mode==1) {
      timeLeftForCyclesTimer.startAnimation(fadeProgressOut);
      progressBar.startAnimation(fadeProgressOut);
    }

    globalNextRoundLogic();

    resetButtonForCycles.setVisibility(View.GONE);
    currentProgressBarValueForModeOne = maxProgress;

    mHandler.post(endFadeForModeOne);

    if (trackActivityWithinCycle) {
      setAllActivityTimesAndCaloriesToTextViews();
    } else {
      setTotalCycleTimeValuesToTextView();
    }

    boolean isAlertRepeating = false;

    if (numberOfRoundsLeftForModeOne == 1 && isLastRoundSoundContinuous) {
      isAlertRepeating = true;
    }

    switch (typeOfRound.get(currentRoundForModeOne)) {
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

    numberOfRoundsLeftForModeOne--;

    if (currentRoundForModeOne < startRoundsForModeOne - 1) {
      currentRoundForModeOne++;
    }

    mHandler.postDelayed(postRoundRunnableForFirstMode(), 750);
  }

  private void nextPomRound(boolean endingEarly) {
    if (numberOfRoundsLeftForModeThree == 0) {
      mHandler.removeCallbacks(endFadeForModeThree);
      resetPomCyclesTimer();
      return;
    }

    //Refreshes so recyclerView timer iteration will display "0" for ending round.
    savedPomCycleAdapter.notifyDataSetChanged();
    //Removed so next round in array does not display as "0".
    mHandler.removeCallbacks(runnableForRecyclerViewTimesForModeThree);

    stateOfTimers.setModeThreeTimerPaused(false);
    stateOfTimers.setModeThreeTimerDisabled(true);

    if (endingEarly) {
      roundDownPomCycleTimeValues();
    } else {
      timeLeftForPomCyclesTimer.setText("0");
//      setNotificationValues();
      roundPomCycleTimeValuesToNearestThousandth();
    }

    if (mode == 3) {
      timeLeftForPomCyclesTimer.startAnimation(fadeProgressOut);
      progressBarForPom.startAnimation(fadeProgressOut);
    }

    resetButtonForPomCycles.setVisibility(View.GONE);
    currentProgressBarValueForModeThree = maxProgress;

    mHandler.post(endFadeForModeThree);

    globalNextRoundLogic();
    setTotalCycleTimeValuesToTextView();
    removePomCycleTimeRunnable();

    if (endingEarly) {
      if (modeThreeCountDownTimer != null) modeThreeCountDownTimer.cancel();
      if (objectAnimatorPom != null) objectAnimatorPom.cancel();
      progressBarForPom.setProgress(0);
      savedPomCycleAdapter.setCycleAsActive();
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

    if (pomDotCounter < 8) {
      pomDotCounter++;
    }

    numberOfRoundsLeftForModeThree--;

    if (currentRoundForModeThree < startRoundsForModeThree - 1) {
      currentRoundForModeThree++;
    }

    mHandler.postDelayed(postRoundRunnableForThirdMode(), 750);

  }

  private void globalNextRoundLogic() {
    setHasTextSizeChangedForTimers(false);
    next_round.setEnabled(false);
    AsyncTask.execute(globalSaveTotalTimesAndCaloriesInDatabaseRunnable);
  }

  private Runnable postRoundRunnableForFirstMode() {
    return new Runnable() {
      @Override
      public void run() {
        dotsAdapter.updateCycleRoundCount(startRoundsForModeOne, numberOfRoundsLeftForModeOne);
        dotsAdapter.resetModeOneAlpha();
        dotsAdapter.setModeOneAlpha();

        //Re-posts after ends in nextRound().
        mHandler.post(runnableForRecyclerViewTimesForModeOne);

        if (mHandler.hasCallbacks(endFadeForModeOne)) {
          mHandler.removeCallbacks(endFadeForModeOne);
        }

        setMillis = 0;
        breakMillis = 0;

        if (numberOfRoundsLeftForModeOne > 0) {
          switch (typeOfRound.get(currentRoundForModeOne)) {
            case 1:
              setMillis = workoutTimeIntegerArray.get(workoutTimeIntegerArray.size() - numberOfRoundsLeftForModeOne);
              timeLeftForCyclesTimer.setText(longToStringConverters.convertSecondsToMinutesBasedString(dividedMillisForTimerDisplay(setMillis)));

              if (!objectAnimator.isStarted()) {
                modeOneStartObjectAnimator();
                startSetTimer();
              }

              if (trackActivityWithinCycle) {
                postActivityTimeRunnable();
              } else {
                postSetAndBreakTimeTotalRunnable();
              }
              break;
            case 2:
              if (mode==1) {
                timeLeftForCyclesTimer.setText("0");
              }
              //Do not want to consolidate infinityTimer runnable methods, since we only want its global re-instantiated here, not in our pause/resume option.
              infinityTimerForSetsRunnable = infinityRunnableForSetRounds();
              mHandler.post(infinityTimerForSetsRunnable);

              if (trackActivityWithinCycle) {
                postActivityTimeRunnable();
              } else {
                postSetAndBreakTimeTotalRunnable();
              }
              break;
            case 3:
              breakMillis = workoutTimeIntegerArray.get(workoutTimeIntegerArray.size() - numberOfRoundsLeftForModeOne);
              timeLeftForCyclesTimer.setText(longToStringConverters.convertSecondsToMinutesBasedString(dividedMillisForTimerDisplay(breakMillis)));

              if (!objectAnimator.isStarted()) {
                modeOneStartObjectAnimator();
                startBreakTimer();
              }

              if (trackActivityWithinCycle) {
                postSetAndBreakTimeTotalRunnable();
              }
              break;
            case 4:
              if (mode==1) {
                timeLeftForCyclesTimer.setText("0");
              }
              infinityTimerForBreaksRunnable = infinityRunnableForBreakRounds();
              mHandler.post(infinityTimerForBreaksRunnable);

              if (trackActivityWithinCycle) {
                postSetAndBreakTimeTotalRunnable();
              }
              break;
          }
        } else {
          stateOfTimers.setModeOneTimerEnded(true);
          loopProgressBarAnimationAtEndOfCycle();
          currentRoundForModeOne = 0;
          cyclesCompleted++;
          setCyclesCompletedTextView();
        }

        stateOfTimers.setModeOneTimerDisabled(false);
        next_round.setEnabled(true);
      }
    };
  }

  private Runnable postRoundRunnableForThirdMode() {
    return new Runnable() {
      @Override
      public void run() {
        pomDotsAdapter.setPomCycleRoundsAsStringsList(pomStringListOfRoundValues);
        pomDotsAdapter.updatePomDotCounter(pomDotCounter);
        pomDotsAdapter.resetModeThreeAlpha();
        pomDotsAdapter.setModeThreeAlpha();

        mHandler.post(runnableForRecyclerViewTimesForModeThree);

        if (mHandler.hasCallbacks(endFadeForModeThree)) {
          mHandler.removeCallbacks(endFadeForModeThree);
        }

        if (numberOfRoundsLeftForModeThree > 0) {
          pomMillis = pomValuesTime.get(currentRoundForModeThree);

          timeLeftForPomCyclesTimer.setText(longToStringConverters.convertSecondsToMinutesBasedString(dividedMillisForTimerDisplay(pomMillis)));

          if (!objectAnimatorPom.isStarted()) {
            modeThreeStartObjectAnimator();
            startPomTimer();
          }
          postPomCycleTimeRunnable();
        } else {
          currentRoundForModeThree = 0;
          progressBarForPom.setProgress(0);

          loopProgressBarAnimationAtEndOfCycle();
          setCyclesCompletedTextView();

          stateOfTimers.setModeThreeTimerEnded(true);
        }

        stateOfTimers.setModeThreeTimerDisabled(false);
        next_round.setEnabled(true);
      }
    };
  }

  private int totalValueAddedToSingleValueAndDividedBy1000ToInteger(long totalVar, long singleVar) {
    return (int) (totalVar += singleVar) / 1000;
  }

  private void pauseAndResumeTimer(int pausing) {
    if (!stateOfTimers.isModeOneTimerDisabled()) {
      if (!stateOfTimers.isModeOneTimerEnded()) {
        if (pausing == PAUSING_TIMER) {
          stateOfTimers.setModeOneTimerPaused(true);
          savedCycleAdapter.setTimerIsPaused(true);

          switch (typeOfRound.get(currentRoundForModeOne)) {
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

          resetButtonForCycles.setVisibility(View.VISIBLE);

          if (modeOneCountdownTimer != null) modeOneCountdownTimer.cancel();
          if (objectAnimator != null) objectAnimator.pause();

          mHandler.removeCallbacks(runnableForRecyclerViewTimesForModeOne);
          removeActivityOrCycleTimeRunnables(trackActivityWithinCycle);

        } else if (pausing == RESUMING_TIMER) {
          savedCycleAdapter.setTimerIsPaused(false);
          stateOfTimers.setModeOneTimerActive(true);
          stateOfTimers.setModeOneTimerPaused(false);

          runnableForRecyclerViewTimesForModeOne = runnableForRecyclerViewTimesForModeOne();
          mHandler.post(runnableForRecyclerViewTimesForModeOne);

          switch (typeOfRound.get(currentRoundForModeOne)) {
            case 1:
              if (objectAnimator.isPaused() || !objectAnimator.isStarted()) {
                modeOneStartObjectAnimator();
                startSetTimer();
              }

              if (trackActivityWithinCycle) {
                postActivityTimeRunnable();
              } else {
                postSetAndBreakTimeTotalRunnable();
              }
              break;
            case 2:
              infinityTimerForSetsRunnable = infinityRunnableForSetRounds();
              if (!mHandler.hasCallbacks(infinityTimerForSetsRunnable)) {
                mHandler.post(infinityTimerForSetsRunnable);
              }

              if (trackActivityWithinCycle) {
                postActivityTimeRunnable();
              } else {
                postSetAndBreakTimeTotalRunnable();
              }
              break;
            case 3:
              if (objectAnimator.isPaused() || !objectAnimator.isStarted()) {
                modeOneStartObjectAnimator();
                startBreakTimer();

                if (trackActivityWithinCycle) {
                  //Doing nothing.
                } else {
                  postSetAndBreakTimeTotalRunnable();
                }
              }
              break;
            case 4:
              infinityTimerForBreaksRunnable = infinityRunnableForBreakRounds();
              if (!mHandler.hasCallbacks(infinityTimerForBreaksRunnable)) {
                mHandler.post(infinityTimerForBreaksRunnable);

                if (trackActivityWithinCycle) {
                  //Doing nothing.
                } else {
                  postSetAndBreakTimeTotalRunnable();
                }
              }
              break;
          }

          savedCycleAdapter.setCycleAsActive();
          resetButtonForCycles.setVisibility(View.GONE);
        }
        AsyncTask.execute(globalSaveTotalTimesAndCaloriesInDatabaseRunnable);
      } else {
        resetCyclesTimer();
      }
    }
  }

  private void postActivityTimeRunnable() {
    if (!isDailyActivityTimeMaxed()) {
      infinityRunnableForDailyActivityTimer = infinityRunnableForDailyActivityTime();

      if (!mHandler.hasCallbacks(infinityRunnableForDailyActivityTimer)) {
        mHandler.post(infinityRunnableForDailyActivityTimer);
      }
    }
  }

  private void postSetAndBreakTimeTotalRunnable() {
    runnableForSetAndBreakTotalTimes = runnableForSetAndBreakTotalTimes();

    if (!mHandler.hasCallbacks(runnableForSetAndBreakTotalTimes)) {
      mHandler.post(runnableForSetAndBreakTotalTimes);
    }
  }

  private void removeActivityOrCycleTimeRunnables(boolean trackingActivity) {
    if (trackingActivity) {
      mHandler.removeCallbacks(infinityRunnableForDailyActivityTimer);
    } else {
      mHandler.removeCallbacks(runnableForSetAndBreakTotalTimes);
    }
  }

  private void pauseAndResumePomodoroTimer(int pausing) {
    if (!stateOfTimers.isModeThreeTimerDisabled()) {
      if (!stateOfTimers.isModeThreeTimerEnded()) {
        if (pausing == PAUSING_TIMER) {
          savedPomCycleAdapter.setTimerIsPaused(true);
          stateOfTimers.setModeThreeTimerPaused(true);

          mHandler.removeCallbacks(runnableForRecyclerViewTimesForModeThree);

          pomMillisUntilFinished = pomMillis;

          if (objectAnimatorPom != null) objectAnimatorPom.pause();
          if (modeThreeCountDownTimer != null) modeThreeCountDownTimer.cancel();

          resetButtonForPomCycles.setText(R.string.reset);
          resetButtonForPomCycles.setVisibility(View.VISIBLE);

          removePomCycleTimeRunnable();

        } else if (pausing == RESUMING_TIMER) {
          runnableForRecyclerViewTimesForModeThree = runnableForRecyclerViewTimesForModeThree();
          mHandler.post(runnableForRecyclerViewTimesForModeThree);

          savedPomCycleAdapter.setTimerIsPaused(false);
          stateOfTimers.setModeThreeTimerActive(true);
          stateOfTimers.setModeThreeTimerPaused(false);

          if (objectAnimatorPom.isPaused() || !objectAnimatorPom.isStarted()) {
            modeThreeStartObjectAnimator();
            startPomTimer();
          }

          savedPomCycleAdapter.setCycleAsActive();
          resetButtonForPomCycles.setVisibility(View.GONE);

          postPomCycleTimeRunnable();
        }
        AsyncTask.execute(globalSaveTotalTimesAndCaloriesInDatabaseRunnable);
      } else {
        resetPomCyclesTimer();
      }
    }
  }

  private void postPomCycleTimeRunnable() {
    runnableForWorkAndRestTotalTimes = runnableForWorkAndRestTotalTimes();

    if (!mHandler.hasCallbacks(runnableForWorkAndRestTotalTimes)) {
      mHandler.post(runnableForWorkAndRestTotalTimes);
    }
  }

  private void removePomCycleTimeRunnable() {
    mHandler.removeCallbacks(runnableForWorkAndRestTotalTimes);
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
    if (!stateOfTimers.isStopWatchTimerEnded()) {
      if (pausing == PAUSING_TIMER) {
        stateOfTimers.setStopWatchTimerPaused(true);

        new_lap.setAlpha(0.3f);
        new_lap.setEnabled(false);

        stopwatchReset.setVisibility(View.VISIBLE);

        mHandler.removeCallbacks(stopWatchTimerRunnable);
      } else if (pausing == RESUMING_TIMER) {
        stateOfTimers.setStopWatchTimerActive(true);
        stateOfTimers.setStopWatchTimerPaused(false);

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
    stateOfTimers.setStopWatchTimerActive(false);
    stateOfTimers.setStopWatchTimerEnded(false);
    stateOfTimers.setStopWatchTimerPaused(true);

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

  private void modeOneStartObjectAnimator() {
    if (typeOfRound.get(currentRoundForModeOne).equals(1)) {
      if (currentProgressBarValueForModeOne == maxProgress) {
        stateOfTimers.setModeOneTimerPaused(false);
        instantiateAndStartObjectAnimatorForModeOne(setMillis);
      } else {
        setMillis = setMillisUntilFinished;
        if (objectAnimator != null) {
          objectAnimator.resume();
        }
      }
    } else if (typeOfRound.get(currentRoundForModeOne).equals(3)) {
      if (currentProgressBarValueForModeOne == maxProgress) {
        stateOfTimers.setModeOneTimerPaused(false);
        instantiateAndStartObjectAnimatorForModeOne(breakMillis);
      } else {
        breakMillis = breakMillisUntilFinished;
        if (objectAnimator != null) {
          objectAnimator.resume();
        }
      }
    }
  }

  private void modeThreeStartObjectAnimator() {
    if (currentProgressBarValueForModeThree == maxProgress) {
      stateOfTimers.setModeThreeTimerPaused(false);
      pomMillis = pomValuesTime.get(currentRoundForModeThree);
      instantiateAndStartObjectAnimatorForModeThree(pomMillis);
    } else {
      pomMillis = pomMillisUntilFinished;
      if (objectAnimatorPom != null) {
        objectAnimatorPom.resume();
      }
    }
  }

  private void instantiateAndStartObjectAnimatorForModeOne(long duration) {
    objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", maxProgress, 0);
    objectAnimator.setInterpolator(new LinearInterpolator());
    objectAnimator.setDuration(duration);

    objectAnimator.start();
  }

  private void instantiateAndStartObjectAnimatorForModeThree(long duration) {
    objectAnimatorPom = ObjectAnimator.ofInt(progressBarForPom, "progress", maxProgress, 0);
    objectAnimatorPom.setInterpolator(new LinearInterpolator());
    objectAnimatorPom.setDuration(duration);
    objectAnimatorPom.start();
  }

  private void setDefaultTimerValuesAndTheirEditTextViews() {
    setTimeValueEnteredWithKeypad = 30;
    breakTimeValueEnteredWithKeypad = 30;
    pomWorkValueEnteredWithKeyPad = 1500;
    pomMiniBreakValueEnteredWithKeyPad = 300;
    pomFullBreakValueEnteredWithKeyPad = 1200;

    String editPopUpTimerString = convertedTimerArrayToString(editPopUpTimerArray);
    timerValueInEditPopUpTextView.setText(editPopUpTimerString);

    savedEditPopUpArrayForFirstHeaderModeThree = convertIntegerToStringArray(pomWorkValueEnteredWithKeyPad);
    savedEditPopUpArrayForSecondHeaderModeThree = convertIntegerToStringArray(pomMiniBreakValueEnteredWithKeyPad);
    savedEditPopUpArrayForThirdHeader = convertIntegerToStringArray(pomFullBreakValueEnteredWithKeyPad);

    String convertedStringOne = convertedTimerArrayToString(savedEditPopUpArrayForFirstHeaderModeThree);
    String convertedStringTwo = convertedTimerArrayToString(savedEditPopUpArrayForSecondHeaderModeThree);
    String convertedStringThree = convertedTimerArrayToString(savedEditPopUpArrayForThirdHeader);

    pomTimerValueInEditPopUpTextViewOne.setText(convertedStringOne);
    pomTimerValueInEditPopUpTextViewTwo.setText(convertedStringTwo);
    pomTimerValueInEditPopUpTextViewThree.setText(convertedStringThree);
  }

  private void setDefaultEditRoundViews() {
    toggleInfinityRounds.setAlpha(0.4f);
    setDefaultTimerValuesAndTheirEditTextViews();

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

        sortHigh.setVisibility(View.GONE);
        sortLow.setVisibility(View.GONE);
        sortActivityTitleAtoZ.setVisibility(View.GONE);
        sortActivityTitleZToA.setVisibility(View.GONE);

        addTDEEfirstMainTextView.setVisibility(View.INVISIBLE);
        total_set_header.setText(R.string.total_work);
        break;
    }
  }

  private void resetCyclesTimer() {
    stateOfTimers.setModeOneTimerPaused(true);
    stateOfTimers.setModeOneTimerEnded(false);
    stateOfTimers.setModeOneTimerDisabled(false);
    stateOfTimers.setModeOneTimerActive(false);

    cyclesTextSizeHasChanged = false;
    vibrator.cancel();
    next_round.setEnabled(true);

    removeActivityOrCycleTimeRunnables(trackActivityWithinCycle);

    if (mediaPlayer.isPlaying()) {
      mediaPlayer.stop();
      mediaPlayer.reset();
    }
    mediaPlayer = MediaPlayer.create(this, ringToneUri);

    clearAndRepopulateCycleAdapterListsFromDatabaseList(false);
    setCyclesCompletedTextView();

    currentProgressBarValueForModeOne = maxProgress;
    progressBar.setProgress(maxProgress);

    if (modeOneCountdownTimer != null) modeOneCountdownTimer.cancel();
    if (endAnimationForCyclesTimer != null) endAnimationForCyclesTimer.cancel();

    resetButtonForCycles.setText(R.string.reset);
    resetButtonForCycles.setVisibility(View.GONE);

    mHandler.removeCallbacks(infinityTimerForSetsRunnable);
    mHandler.removeCallbacks(infinityTimerForBreaksRunnable);
    mHandler.removeCallbacks(runnableForRecyclerViewTimesForModeOne);

    if (workoutTimeIntegerArray.size() > 0) {
      switch (typeOfRound.get(0)) {
        case 1:
          setMillis = workoutTimeIntegerArray.get(0);
          timeLeftForCyclesTimer.setText(longToStringConverters.convertSecondsToMinutesBasedString((dividedMillisForTimerDisplay(setMillis))));
          setInitialTextSizeForTimers(setMillis);
          break;
        case 2:
          setMillis = 0;
          timeLeftForCyclesTimer.setText("0");
          setInitialTextSizeForTimers(0);
          break;
        case 3:
          breakMillis = workoutTimeIntegerArray.get(0);
          timeLeftForCyclesTimer.setText(longToStringConverters.convertSecondsToMinutesBasedString(((dividedMillisForTimerDisplay(breakMillis)))));
          setInitialTextSizeForTimers(breakMillis);
          break;
        case 4:
          breakMillis = 0;
          timeLeftForCyclesTimer.setText("0");
          setInitialTextSizeForTimers(0);
          break;
      };

      for (int i = 0; i < workoutTimeIntegerArray.size(); i++) {
        if (typeOfRound.get(i) == 2 || typeOfRound.get(i) == 4) {
          workoutTimeIntegerArray.set(i, 0);
        }
      }

      currentRoundForModeOne = 0;
      startRoundsForModeOne = workoutTimeIntegerArray.size();
      numberOfRoundsLeftForModeOne = startRoundsForModeOne;

      ArrayList<String> convertedWorkoutRoundList = convertMillisIntegerListToTimerStringList(workoutTimeIntegerArray);

      dotsAdapter.setCycleRoundsAsStringsList(convertedWorkoutRoundList);
      dotsAdapter.setTypeOfRoundList(typeOfRound);
      dotsAdapter.updateCycleRoundCount(startRoundsForModeOne, numberOfRoundsLeftForModeOne);

      dotsAdapter.resetModeOneAlpha();
      dotsAdapter.setModeOneAlpha();
      dotsAdapter.notifyDataSetChanged();

      cyclesTextSizeHasChanged = false;

      roundDownCycleTimeValues();

      if (objectAnimator != null) {
        objectAnimator.cancel();
      }

      if (mHandler.hasCallbacks(endFadeForModeOne)) {
        mHandler.removeCallbacks(endFadeForModeOne);
      }

      savedCycleAdapter.removeCycleAsActive();

      if (savedCycleAdapter.isCycleActive() == true) {
        savedCycleAdapter.removeCycleAsActive();
        savedCycleAdapter.notifyDataSetChanged();
      }
    }
  }

  private void resetPomCyclesTimer() {
    startRoundsForModeThree = 8;
    currentRoundForModeThree = 0;
    numberOfRoundsLeftForModeThree = startRoundsForModeThree;

    stateOfTimers.setModeThreeTimerPaused(true);
    stateOfTimers.setModeThreeTimerEnded(false);
    stateOfTimers.setModeThreeTimerDisabled(false);
    stateOfTimers.setModeThreeTimerActive(false);

    pomCyclesTextSizeHasChanged = false;
    next_round.setEnabled(true);
    vibrator.cancel();

    if (mediaPlayer.isPlaying()) {
      mediaPlayer.stop();
      mediaPlayer.reset();
    }
    mediaPlayer = MediaPlayer.create(this, ringToneUri);

    clearAndRepopulateCycleAdapterListsFromDatabaseList(false);
    setCyclesCompletedTextView();

    removePomCycleTimeRunnable();

    mHandler.removeCallbacks(runnableForRecyclerViewTimesForModeThree);

    currentProgressBarValueForModeThree = maxProgress;
    progressBarForPom.setProgress(maxProgress);

    if (modeThreeCountDownTimer != null) modeThreeCountDownTimer.cancel();
    if (endAnimationForPomCyclesTimer != null) endAnimationForPomCyclesTimer.cancel();

    pomDotCounter = 0;

    if (pomValuesTime.size() > 0) {
      pomMillis = pomValuesTime.get(0);
      timeLeftForPomCyclesTimer.setText(longToStringConverters.convertSecondsToMinutesBasedString(dividedMillisForTimerDisplay(pomMillis)));

      pomDotsAdapter.setPomCycleRoundsAsStringsList(pomStringListOfRoundValues);
      pomDotsAdapter.updatePomDotCounter(pomDotCounter);

      setInitialTextSizeForTimers(pomMillis);
    }

    pomCyclesTextSizeHasChanged = false;

    resetButtonForPomCycles.setText(R.string.reset);
    resetButtonForPomCycles.setVisibility(View.GONE);

    roundDownPomCycleTimeValues();

    if (objectAnimatorPom != null) {
      objectAnimatorPom.cancel();
    }

    if (mHandler.hasCallbacks(endFadeForModeThree)) {
      mHandler.removeCallbacks(endFadeForModeThree);
    }

    savedPomCycleAdapter.removeCycleAsActive();

    if (savedPomCycleAdapter.isCycleActive() == true) {
      savedPomCycleAdapter.removeCycleAsActive();
      savedPomCycleAdapter.notifyDataSetChanged();
    }

    pomDotsAdapter.resetModeThreeAlpha();
    pomDotsAdapter.setModeThreeAlpha();
    pomDotsAdapter.notifyDataSetChanged();
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

  private void toggleEditPopUpViewsForAddingActivity(boolean activityExists) {
    if (mode == 1) {
      if (activityExists) {
        String activity = (String) tdee_sub_category_spinner.getSelectedItem();
        addTDEEfirstMainTextView.setText(activity);
        cycleHasActivityAssigned = true;
      } else {
        addTDEEfirstMainTextView.setText(R.string.add_activity);
        cycleHasActivityAssigned = false;
      }
    }
  }

  private void toggleViewsForTotalDailyAndCycleTimes(boolean trackingCycle) {
    if (!trackingCycle) {
      tracking_daily_stats_header_textView.setVisibility(View.INVISIBLE);

      cycle_title_textView.setVisibility(View.VISIBLE);
      cycle_title_textView_with_activity.setVisibility(View.INVISIBLE);

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
      tracking_daily_stats_header_textView.setVisibility(View.INVISIBLE);

      cycle_title_textView.setVisibility(View.GONE);
      cycle_title_textView_with_activity.setVisibility(View.VISIBLE);

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
      savedCyclesTabLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.alien_black));
      savedCyclesTabLayout.setSelectedTabIndicatorColor(Color.BLACK);

      fabDrawable.setStroke(3, Color.BLACK);
      stopWatchDrawable.setStroke(3, Color.BLACK);
    }
    if (themeMode == NIGHT_MODE) {
      mainView.setBackgroundColor(Color.BLACK);
      savedCyclesTabLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.alien_black));
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

      dailyTotalTimeTextViewHeader.setTextColor(getColor(R.color.black));
      dailyTotalCaloriesTextViewHeader.setTextColor(getColor(R.color.black));
      dailyTotalTimeForSingleActivityTextViewHeader.setTextColor(getColor(R.color.black));
      dailyTotalCaloriesForSingleActivityTextViewHeader.setTextColor(getColor(R.color.black));

      timeLeftForCyclesTimer.setTextColor(getColor(R.color.black));
      timeLeftForPomCyclesTimer.setTextColor(getColor(R.color.black));

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

      dailyTotalTimeTextViewHeader.setTextColor(getColor(R.color.white));
      dailyTotalCaloriesTextViewHeader.setTextColor(getColor(R.color.white));
      dailyTotalTimeForSingleActivityTextViewHeader.setTextColor(getColor(R.color.white));
      dailyTotalCaloriesForSingleActivityTextViewHeader.setTextColor(getColor(R.color.white));

      timeLeftForCyclesTimer.setTextColor(getColor(R.color.white));
      timeLeftForPomCyclesTimer.setTextColor(getColor(R.color.white));

      DrawableCompat.setTint(resetCyclesDrawableWrapped, Color.WHITE);
      reset_total_cycle_times.setBackgroundColor(Color.BLACK);
      DrawableCompat.setTint(nextRoundDrawableWrapped, Color.WHITE);
      next_round.setBackgroundColor(Color.BLACK);
    }

    reset_total_cycle_times.setImageDrawable(resetCyclesDrawableWrapped);
    next_round.setImageDrawable(nextRoundDrawableWrapped);

    dotsAdapter.setDayOrNightMode(themeMode);
    pomDotsAdapter.setDayOrNightMode(themeMode);
  }

  private void toggleDayAndNightModeForStatsFragment() {

  }

  //  private void setAndUpdateActivityTimeAndCaloriesInDatabaseFromConvertedString() {
//    dailyStatsAccess.updateTotalTimesAndCaloriesForEachActivityForSelectedDay(getDailyActivityTimeFromTextView(), totalCaloriesBurnedForSpecificActivityForCurrentDay);
//  }

//  private long getDailyActivityTimeFromTextView() {
//    String textView = (String) dailyTotalTimeForSingleActivityTextView.getText().toString();
//    return convertStringToSecondsForTimerPopUp(textView) * 1000;
//  }

//  private int convertStringToSecondsForTimerPopUp(String timerString) {
//    int hours = 0;
//    int minutes = 0;
//    int seconds = 0;
//    timerString = timerString.replace(":", "");
//
//    //Range is exclusive of last position.
//    if (timerString.length() == 3) {
//      minutes = Integer.parseInt(timerString.substring(0, 1));
//      seconds = Integer.parseInt(timerString.substring(1, 2) + timerString.substring(2, 3));
//    }
//
//    if (timerString.length() == 4) {
//      minutes = Integer.parseInt(timerString.substring(0, 1) + timerString.substring(1, 2));
//      seconds = Integer.parseInt(timerString.substring(2, 3) + timerString.substring(3, 4));
//    }
//
//    if (timerString.length() == 5) {
//      hours = Integer.parseInt(timerString.substring(0, 1));
//      minutes = Integer.parseInt(timerString.substring(1, 2) + timerString.substring(2, 3));
//      seconds = Integer.parseInt(timerString.substring(3, 4) + timerString.substring(4, 5));
//    }
//
//    if (timerString.length() == 6) {
//      hours = Integer.parseInt(timerString.substring(0, 1) + timerString.substring(1, 2));
//      minutes = Integer.parseInt(timerString.substring(2, 3) + timerString.substring(3, 4));
//      seconds = Integer.parseInt(timerString.substring(4, 5) + timerString.substring(5, 6));
//    }
//
//    if (seconds > 60) {
//      seconds = seconds % 60;
//      minutes += 1;
//    }
//
//    if (minutes>=60) {
//      hours = minutes/60;
//      minutes = minutes % 60;
//    }
//
//    int totalTime = (minutes * 60) + seconds;
//
//    if (hours > 0) {
//      totalTime += (hours * 60 * 60);
//    }
//
//    return totalTime;
//  }
}