package com.example.tragic.irate.simple.stopwatch;

import android.animation.LayoutTransition;
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
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.content.AsyncTaskLoader;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tragic.irate.simple.stopwatch.Database.Cycles;
import com.example.tragic.irate.simple.stopwatch.Database.CyclesDatabase;
import com.example.tragic.irate.simple.stopwatch.Database.PomCycles;
import com.example.tragic.irate.simple.stopwatch.SettingsFragments.ColorSettingsFragment;
import com.example.tragic.irate.simple.stopwatch.SettingsFragments.RootSettingsFragment;
import com.example.tragic.irate.simple.stopwatch.SettingsFragments.SoundSettingsFragment;
import com.example.tragic.irate.simple.stopwatch.SettingsFragments.tdeeSettingsFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

@SuppressWarnings({"depreciation"})
public class MainActivity extends AppCompatActivity implements SavedCycleAdapter.onCycleClickListener, SavedCycleAdapter.onHighlightListener, SavedPomCycleAdapter.onCycleClickListener, SavedPomCycleAdapter.onHighlightListener, CycleRoundsAdapter.onFadeFinished, CycleRoundsAdapterTwo.onFadeFinished, CycleRoundsAdapter.onRoundSelected, CycleRoundsAdapterTwo.onRoundSelectedSecondAdapter, DotDraws.sendAlpha, SavedCycleAdapter.onResumeOrResetCycle, SavedPomCycleAdapter.onResumeOrResetCycle, RootSettingsFragment.onChangedSettings, SoundSettingsFragment.onChangedSoundSetting, ColorSettingsFragment.onChangedColorSetting {

  SharedPreferences sharedPreferences;
  SharedPreferences.Editor prefEdit;
  TabLayout tabLayout;
  View tabView;
  View mainView;
  View actionBarView;
  BlankCanvas blankCanvas;
  Calendar calendar;
  SimpleDateFormat simpleDateFormat;
  String date = "";

  ImageButton fab;
  ImageButton stopwatch;
  ImageView sortCheckMark;
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
  TextView delete_all_text;
  Button delete_all_confirm;
  Button delete_all_cancel;
  TextView appHeader;
  ImageButton edit_highlighted_cycle;
  ImageButton delete_highlighted_cycle;
  ImageButton cancelHighlight;
  TextView sortButton;

  int mode = 1;
  int savedMode = 1;
  int sortMode = 1;
  int sortModePom = 1;
  int sortHolder = 1;
  int positionOfSelectedCycle;
  String cycleTitle = "";
  List<String> receivedHighlightPositions;
  List<Integer> receivedHighlightPositionHolder;

  ImageButton addRoundToCycleButton;
  ImageButton subtractRoundFromCycleButton;
  ImageButton toggleInfinityRounds;
  ImageButton buttonToLaunchTimer;

  ArrayList<Integer> workoutTime;
  ArrayList<String> convertedWorkoutTime;
  ArrayList<String> workoutCyclesArray;
  ArrayList<String> workoutTitle;
  ArrayList<String> workoutTitleArray;
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

  ConstraintLayout.LayoutParams cycleTitleLayoutParams;
  ConstraintLayout.LayoutParams completedLapsLayoutParams;

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

  PopupWindow timerPopUpWindow;
  View timerPopUpView;

  ProgressBar progressBar;
  ImageView stopWatchView;
  TextView timeLeft;
  TextView msTime;
  CountDownTimer timer;
  TextView reset;
  ObjectAnimator objectAnimator;
  ObjectAnimator objectAnimatorPom;
  Animation endAnimation;

  TextView cycle_title_textView;
  TextView cycles_completed;
  ImageButton new_lap;
  ImageButton next_round;
  ImageButton reset_total_times;
  TextView total_set_header;
  TextView total_break_header;
  TextView total_set_time;
  TextView total_break_time;
  TextView empty_laps;
  TextView actvitiyStatsInTimerTextView;

  boolean activeCycle;
  int PAUSING_TIMER = 1;
  int RESUMING_TIMER = 2;

  int RESUMING_CYCLE_FROM_ADAPTER = 1;
  int RESETTING_CYCLE_FROM_ADAPTER = 2;

  long setMillis;
  long breakMillis;
  long cycleSetTimeForSingleRoundInMillis;
  long totalCycleSetTimeInMillis;
  long cycleBreakTimeForSingleRoundInMillis;
  long totalCycleBreakTimeInMillis;
  String timeLeftValueHolder;
  boolean resettingTotalTime;
  long roundedValueForTotalTimes;
  long timerDurationPlaceHolder;

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

  ObjectAnimator fadeInObj;
  ObjectAnimator fadeOutObj;
  RecyclerView lapRecycler;
  LapAdapter lapAdapter;
  LinearLayoutManager lapReyclerLayoutManager;
  ConstraintLayout roundRecyclerLayout;

  DotDraws dotDraws;
  LapListCanvas lapListCanvas;
  ValueAnimator sizeAnimator;
  ValueAnimator valueAnimatorDown;
  ValueAnimator valueAnimatorUp;
  AlphaAnimation fadeProgressIn;
  AlphaAnimation fadeProgressOut;

  VerticalSpaceItemDecoration verticalSpaceItemDecoration;
  boolean textSizeIncreased;

  ArrayList<String> setsArray;
  ArrayList<String> breaksArray;
  ArrayList<String> breaksOnlyArray;
  ArrayList<Integer> customSetTime;
  ArrayList<Integer> customBreakTime;
  ArrayList<Integer> breaksOnlyTime;
  ArrayList<Integer> zeroArraySets;
  ArrayList<Integer> zeroArrayBreaks;
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

  boolean aSettingsMenuIsVisible;
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

  RootSettingsFragment rootSettingsFragment;
  SoundSettingsFragment soundSettingsFragment;
  ColorSettingsFragment colorSettingsFragment;
  com.example.tragic.irate.simple.stopwatch.SettingsFragments.tdeeSettingsFragment tdeeSettingsFragment;

  ChangeSettingsValues changeSettingsValues;

  FrameLayout settingsFragmentFrameLayout;
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
  TextView addTDEEActivityTextView;
  ImageView removeTdeeActivityImageView;
  PopupWindow addTDEEPopUpWindow;
  View addTDEEPopUpView;

  Spinner tdee_category_spinner;
  Spinner tdee_sub_category_spinner;
  Spinner tdeeMinutesDurationSpinner;
  Spinner tdeeSecondsDurationSpinner;
  ArrayAdapter tdeeCategoryAdapter;
  ArrayAdapter tdeeSubCategoryAdapter;

  int selectedTdeeCategoryPosition;
  int selectedTdeeSubCategoryPosition;
  int selectedTdeeValuePosition;

  TextView caloriesBurnedInTdeeAdditionTextView;
  TextView metScoreTextView;

  boolean isMetric;
  String userGender;
  int userAge;
  int userWeight;
  int userHeight;

  int tdeeMinutesDuration;
  int tdeeSecondsDuration;
  double metScore;
  double burnedCaloriesInCurrentLoadingOfCycle;
  double burnedCaloriesInAllLoadingsOfCycle;
  Button confirmActivityAddition;
  boolean cycleHasActivityAssigned;

  long tdeeActivityTimeDurationPlaceHolder;
  long singleInstanceTdeeActivityTime;
  long totalTdeeActivityTime;

  //Todo: Test reset/resume option alternating between modes/tabs.
  //Todo: Total time bugs when switching cycles between modes.

  //Todo: Check sizes on long aspect for all layouts + menus.
  //Todo: Figure our layout params for checkmark.
  //Todo: Test all notifications.
  //Todo: We should put any index fetches inside conditionals, BUT make sure nothing (i.e. Timer popup) launches unless those values are fetched.
  //Todo: As an improvement to DotDraws: Make width of class xml a minimum that fills smaller phones, and then use that width as the maximum bounds of the canvas draw. That way, wider phones will simply have more horizontal padding and narrower will fill the screen.

  //Todo: Track calories by day, week, etc. For this, new db class creates an entry for each day (if a cycle burns calories that day), and saves that day's tdee time elapsed/calories, resetting their values once the next day is reached.
  //Todo: Run code inspector for redundancies, etc.
  //Todo: Rename app, of course.
  //Todo: Test layouts w/ emulator.
  //Todo: Test everything 10x.

  //Todo: REMINDER, Try next app w/ Kotlin + learn Kotlin.
  //Todo: GAME: Word builder w/ letters costing different amount. Earn $ for it (mining/random spots?).
  //Todo: Explode World game w/ 24 hours per day (can only play that much each day).

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
    if (!timerPopUpIsVisible && settingsFragmentFrameLayout.getVisibility()==View.GONE) {
      moveTaskToBack(false);
      return;

    }
    if (rootSettingsFragment.isVisible()) {
      settingsFragmentFrameLayout.setVisibility(View.GONE);
      settingsFragmentFrameLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_anim));

      aSettingsMenuIsVisible = false;
    }

    if (soundSettingsFragment.isVisible() || colorSettingsFragment.isVisible() || tdeeSettingsFragment.isVisible()) {
      getSupportFragmentManager().beginTransaction()
              .addToBackStack(null)
              .replace(R.id.settings_fragment_frameLayout, rootSettingsFragment)
              .commit();
    }
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

  //For resume/reset onClicks within cycle adapter.
  @Override
  public void ResumeOrResetCycle(int resumingOrResetting) {
    if (resumingOrResetting==RESUMING_CYCLE_FROM_ADAPTER) {
      resumeOrResetCycleFromAdapterList(RESUMING_CYCLE_FROM_ADAPTER);
    }
    else if (resumingOrResetting==RESETTING_CYCLE_FROM_ADAPTER){
      resumeOrResetCycleFromAdapterList(RESETTING_CYCLE_FROM_ADAPTER);
    }
  }

  @Override
  public void onCycleClick(int position) {
    //Active cycle option will automatically be removed if accessing new cycle.
    if (mode==1) savedCycleAdapter.removeActiveCycleLayout();
    if (mode==3) savedPomCycleAdapter.removeActiveCycleLayout();
    isNewCycle = false;
    positionOfSelectedCycle = position;
    //Retrieves timer value lists from cycle adapter list by parsing its Strings, rather than querying database.
    populateCycleAdapterArrayList();
    //If clicking on a cycle to launch, it will always be an existing one, and we do not want to call a save method since it is unedited.
    launchTimerCycle(false);
    resetTimer();
  }

  @Override
  public void onCycleHighlight(List<String> listOfPositions, boolean addButtons) {
    //Receives list of cycle positions highlighted.
    receivedHighlightPositions = listOfPositions;
    //Sets "highlight mode" actionBar buttons to Visible if entering mode (i.e. selecting first item).
    if (addButtons) {
      //If at least one item in our cycle list, enable the edit button.
      edit_highlighted_cycle.setEnabled(listOfPositions.size() <= 1);
      //Fading app name text + sort button out, edit and delete buttons in.
      fadeEditCycleButtonsInAndOut(FADE_IN_HIGHLIGHT_MODE);
    }
    //Enables edit cycle button if we have exactly 1 row selected, disables otherwise.
    if (edit_highlighted_cycle.getAlpha()!=1 && receivedHighlightPositions.size()==1) {
      edit_highlighted_cycle.setAlpha(1.0f);
      edit_highlighted_cycle.setEnabled(true);
    } else if (edit_highlighted_cycle.getAlpha()==1 && receivedHighlightPositions.size()!=1) {
      edit_highlighted_cycle.setAlpha(0.4f);
      edit_highlighted_cycle.setEnabled(false);
    }
  }

  //This callback method works for both round adapters.
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

  @SuppressLint("NonConstantResourceId")
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.global_settings:
        launchGlobalSettings();
        break;
      case R.id.delete_all_cycles:
        if (mode==1 && workoutCyclesArray.size()==0 || mode==3 && pomArray.size()==0) {
          Toast.makeText(getApplicationContext(), "No cycles to delete!", Toast.LENGTH_SHORT).show();
        } else {
          delete_all_text.setText(R.string.delete_all_cycles);
          deleteCyclePopupWindow.showAtLocation(mainView, Gravity.CENTER, 0, 0);
        }
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @SuppressLint({"UseCompatLoadingForDrawables", "ClickableViewAccessibility", "CommitPrefEdits", "CutPasteId"})
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    groupAllAppStartInstantiations();

    infinityTimerForSets = infinityRunnableForSets();
    infinityTimerForBreaks = infinityRunnableForBreaks();
    cycleNameEdit.addTextChangedListener(cycleNameEditTextWatcher());

    addTDEEActivityTextView.setOnClickListener(v-> {
      View testView = editCyclesPopupView.findViewById(R.id.bottom_edit_title_divider);

//      addTDEEPopUpWindow.showAtLocation(mainView, Gravity.CENTER | Gravity.TOP, 0, 0);
      addTDEEPopUpWindow.showAsDropDown(testView);
    });

    confirmActivityAddition.setOnClickListener(v-> {
      addTDEEPopUpWindow.dismiss();
      toggleExistenceOfTdeeActivity(true);
    });

    removeTdeeActivityImageView.setOnClickListener(v-> {
      toggleExistenceOfTdeeActivity(false);
    });

    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
      @Override
      public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
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
      }

      @Override
      public void onTabUnselected(TabLayout.Tab tab) {
        tabUnselectedLogic();

        if (tab.getPosition()==0) {
          if (savedCycleAdapter.isCycleHighlighted()==true) {
            removeCycleHighlights();
            savedCycleAdapter.notifyDataSetChanged();
          }
        }
        if (tab.getPosition()==1) {
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

    fab.setOnClickListener(v -> {
      fabLogic();
    });

    stopwatch.setOnClickListener(v-> {
      stopWatchButtonLogic();
    });

    //Showing sort popup window.
    sortButton.setOnClickListener(v-> {
      moveSortCheckmark();
      sortPopupWindow.showAtLocation(mainView, Gravity.END|Gravity.TOP, 0, 0);
    });

    sortAlphaStart.setOnClickListener(individualSortOptionButtonsListener());
    sortAlphaEnd.setOnClickListener(individualSortOptionButtonsListener());
    sortRecent.setOnClickListener(individualSortOptionButtonsListener());
    sortNotRecent.setOnClickListener(individualSortOptionButtonsListener());
    sortHigh.setOnClickListener(individualSortOptionButtonsListener());
    sortLow.setOnClickListener(individualSortOptionButtonsListener());

    timerPopUpWindow.setOnDismissListener(() -> {
      timerPopUpDismissalLogic();

      activateResumeOrResetOptionForCycle();
      replaceCycleListWithEmptyTextViewIfNoCyclesExist();
      setViewsAndColorsToPreventTearingInEditPopUp(false);
      AsyncTask.execute(globalSaveTotalTimesAndCaloriesInDatabaseRunnable);
    });

    editCyclesPopupWindow.setOnDismissListener(() -> {
      editCyclesPopUpDismissalLogic();

      setViewsAndColorsToPreventTearingInEditPopUp(false);
      replaceCycleListWithEmptyTextViewIfNoCyclesExist();
    });

    ////--ActionBar Item onClicks START--////
    edit_highlighted_cycle.setOnClickListener(v-> {
      editHighlightedCycleButtonClickLogic();

      fadeEditCycleButtonsInAndOut(FADE_IN_EDIT_CYCLE);
      setViewsAndColorsToPreventTearingInEditPopUp(true);

      clearRoundAndCycleAdapterArrayLists();
      populateCycleAdapterArrayList();
      populateRoundAdapterArraysForHighlightedCycle();

      setRoundRecyclerViewsWhenChangingAdapterCount(workoutTime);
      removeHighlightFromCycle();

      assignOldCycleValuesToCheckForChanges();
      cycleNameEdit.setText(cycleTitle);
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

      if (mode==1) addToEditPopUpTimerArrays(editPopUpTimerArray, textButton);

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

    ////--EditCycles Menu Item onClicks START--////
    buttonToLaunchTimer.setOnClickListener(v-> {
      //Launched from editCyclePopUp and calls TimerInterface. First input controls whether it is a new cycle, and the second will always be true since a cycle launch should automatically save/update it in database.
      launchTimerCycle(true);
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

        if (mode==4) {
          timeLeft.setText(displayTime);
          msTime.setText(displayMs);
        }

        if (!textSizeIncreased && mode==4) {
          if (stopWatchSeconds > 59) {
            changeTextSizeWithAnimator(valueAnimatorDown, timeLeft);
            textSizeIncreased = true;
          }
        }

        mHandler.postDelayed(this, 10);
      }
    };

    next_round.setOnClickListener(v -> {
      nextRound(true);
    });

    reset.setOnClickListener(v -> {
      addAndRoundDownTotalCycleTimes();
      addAndRoundDownTdeeTimeAndTotalCalories();
      AsyncTask.execute(globalSaveTotalTimesAndCaloriesInDatabaseRunnable);

      if (mode != 3) {
        resetTimer();
      } else {
        if (reset.getText().equals(getString(R.string.reset)))
          reset.setText(R.string.confirm_cycle_reset);
        else {
          reset.setText(R.string.reset);
          resetTimer();
        }
      }
    });

    new_lap.setOnClickListener(v -> {
      newLap();
    });

    pauseResumeButton.setOnClickListener(v -> {
      if (mode!=4) if (!timerIsPaused) pauseAndResumeTimer(PAUSING_TIMER); else pauseAndResumeTimer(RESUMING_TIMER);
      else if (!stopWatchIsPaused) pauseAndResumeTimer(PAUSING_TIMER); else pauseAndResumeTimer(RESUMING_TIMER);
    });

    reset_total_times.setOnClickListener(v -> {
      delete_all_text.setText(R.string.delete_total_times);
      deleteCyclePopupWindow.showAtLocation(mainView, Gravity.CENTER_HORIZONTAL, 0, -100);
    });

    delete_all_confirm.setOnClickListener(v-> {
      if (delete_all_text.getText().equals(getString(R.string.delete_all_cycles))) {
        AsyncTask.execute(deleteAllCyclesASyncRunnable());
      } else if (delete_all_text.getText().equals(getString(R.string.delete_total_times))) {
        AsyncTask.execute(deleteTotalCycleTimesASyncRunnable());
      }
      if (deleteCyclePopupWindow.isShowing()) deleteCyclePopupWindow.dismiss();
    });

    delete_all_cancel.setOnClickListener(v -> {
      if (deleteCyclePopupWindow.isShowing()) deleteCyclePopupWindow.dismiss();
    });
  }

  private void groupAllAppStartInstantiations() {

    instantiateGlobalClasses();
    instantiateFragmentsAndTheirCallbacks();
    instantiatePopUpViewsAndWindows();
    instantiateTabLayouts();

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
  }

  private void instantiateGlobalClasses() {
    screenRatioLayoutChanger = new ScreenRatioLayoutChanger(getApplicationContext());
    changeSettingsValues = new ChangeSettingsValues();
    tDEEChosenActivitySpinnerValues = new TDEEChosenActivitySpinnerValues(getApplicationContext());

    mHandler = new Handler();
    mSavingCycleHandler = new Handler();
    inputMethodManager =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    sharedPreferences = getApplicationContext().getSharedPreferences("pref", 0);
    prefEdit = sharedPreferences.edit();

    cycles = new Cycles();
    pomCycles = new PomCycles();

    objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", (int) maxProgress, 0);
    objectAnimatorPom = ObjectAnimator.ofInt(progressBar, "progress", (int) maxProgress, 0);

    ringToneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    mediaPlayer = MediaPlayer.create(this, ringToneUri);
    vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

    calendar = Calendar.getInstance();
    simpleDateFormat = new SimpleDateFormat("EEE, MMMM d yyyy - hh:mma", Locale.getDefault());
  }

  private void instantiateTabLayouts() {
    tabLayout = findViewById(R.id.tabLayout);
    tabLayout.addTab(tabLayout.newTab().setText("Workouts"));
    tabLayout.addTab(tabLayout.newTab().setText("Pomodoro"));
  }

  private void tabUnselectedLogic() {
    cycleTitle = "";

    if (!sortButton.isEnabled()) fadeEditCycleButtonsInAndOut(FADE_OUT_HIGHLIGHT_MODE);
    if (editCyclesPopupWindow.isShowing()) editCyclesPopupWindow.dismiss();

    positionOfSelectedCycle = 0;
    receivedHighlightPositions.clear();
    receivedHighlightPositionHolder.clear();
  }

  private void assignMainLayoutClassesToIds() {
    mainView = findViewById(R.id.main_layout);
    tabView = findViewById(R.id.tabLayout);
    actionBarView = findViewById(R.id.custom_action_bar);

    getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    getSupportActionBar().setDisplayShowCustomEnabled(true);
    getSupportActionBar().setCustomView(R.layout.custom_bar);

    fab = findViewById(R.id.fab);
    stopwatch = findViewById(R.id.stopwatch_button);
    emptyCycleList = findViewById(R.id.empty_cycle_list);
    savedCycleRecycler = findViewById(R.id.cycle_list_recycler);
    savedPomCycleRecycler = findViewById(R.id.pom_list_recycler);
    blankCanvas = findViewById(R.id.blank_canvas);

    appHeader = findViewById(R.id.app_header);
    edit_highlighted_cycle = findViewById(R.id.edit_highlighted_cycle);
    delete_highlighted_cycle = findViewById(R.id.delete_highlighted_cycles);
    cancelHighlight = findViewById(R.id.cancel_highlight);
    sortButton = findViewById(R.id.sortButton);
  }

  private void instantiateFragmentsAndTheirCallbacks() {
    rootSettingsFragment = new RootSettingsFragment();
    soundSettingsFragment = new SoundSettingsFragment();
    colorSettingsFragment = new ColorSettingsFragment();
    tdeeSettingsFragment = new tdeeSettingsFragment();

    rootSettingsFragment.sendSettingsData(MainActivity.this);
    soundSettingsFragment.soundSetting(MainActivity.this);
    colorSettingsFragment.colorSetting(MainActivity.this);

    settingsFragmentFrameLayout = findViewById(R.id.settings_fragment_frameLayout);
    settingsFragmentFrameLayout.setVisibility(View.GONE);

    getSupportFragmentManager().beginTransaction()
            .add((R.id.settings_fragment_frameLayout), rootSettingsFragment)
            .commit();

  }

  private void assignTdeePopUpLayoutClassesToTheirIds() {
    tdee_category_spinner = addTDEEPopUpView.findViewById(R.id.activity_category_spinner);
    tdee_sub_category_spinner = addTDEEPopUpView.findViewById(R.id.activity_sub_category_spinner);
    confirmActivityAddition = addTDEEPopUpView.findViewById(R.id.add_activity_confirm_button);
    metScoreTextView = addTDEEPopUpView.findViewById(R.id.met_score_textView);
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
    buttonToLaunchTimer = editCyclesPopupView.findViewById(R.id.buttonToLaunchTimer);

    roundRecyclerLayout = editCyclesPopupView.findViewById(R.id.round_recycler_layout);
    roundRecyclerTwo = editCyclesPopupView.findViewById(R.id.round_list_recycler_two);
    roundListDivider = editCyclesPopupView.findViewById(R.id.round_list_divider);
    roundRecycler = editCyclesPopupView.findViewById(R.id.round_list_recycler);

    addTDEEActivityTextView = editCyclesPopupView.findViewById(R.id.tdee_add_textView);
    removeTdeeActivityImageView = editCyclesPopupView.findViewById(R.id.cancel_activity_for_cycle);
  }

  private void assignTimerPopUpLayoutClassesToTheirIds() {
    dotDraws = timerPopUpView.findViewById(R.id.dotdraws);
    lapListCanvas = timerPopUpView.findViewById(R.id.lapCanvas);
    reset = timerPopUpView.findViewById(R.id.reset);
    cycle_title_textView = timerPopUpView.findViewById(R.id.cycle_title_textView);

    cycles_completed = timerPopUpView.findViewById(R.id.cycles_completed);
    next_round = timerPopUpView.findViewById(R.id.next_round);
    new_lap = timerPopUpView.findViewById(R.id.new_lap);
    total_set_header = timerPopUpView.findViewById(R.id.total_set_header);
    total_break_header = timerPopUpView.findViewById(R.id.total_break_header);
    total_set_time = timerPopUpView.findViewById(R.id.total_set_time);
    total_break_time = timerPopUpView.findViewById(R.id.total_break_time);
    actvitiyStatsInTimerTextView = timerPopUpView.findViewById(R.id.activity_stats_in_timer_textView);

    progressBar = timerPopUpView.findViewById(R.id.progressBar);
    stopWatchView = timerPopUpView.findViewById(R.id.stopWatchView);
    timeLeft = timerPopUpView.findViewById(R.id.timeLeft);
    msTime = timerPopUpView.findViewById(R.id.msTime);
    lapRecycler = timerPopUpView.findViewById(R.id.lap_recycler);
    reset_total_times = timerPopUpView.findViewById(R.id.reset_total_times);
    empty_laps = timerPopUpView.findViewById(R.id.empty_laps_text);
    pauseResumeButton = timerPopUpView.findViewById(R.id.pauseResumeButton);
  }

  private void assignSortPopUpLayoutClassesToTheirIds() {
    sortAlphaStart = sortCyclePopupView.findViewById(R.id.sort_title_start);
    sortAlphaEnd = sortCyclePopupView.findViewById(R.id.sort_title_end);
    sortRecent = sortCyclePopupView.findViewById(R.id.sort_most_recent);
    sortNotRecent = sortCyclePopupView.findViewById(R.id.sort_least_recent);
    sortHigh = sortCyclePopupView.findViewById(R.id.sort_number_high);
    sortLow = sortCyclePopupView.findViewById(R.id.sort_number_low);
    sortCheckMark = sortCyclePopupView.findViewById(R.id.sortCheckMark);
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
        clearAndRepopulateCycleAdapterListsFromDatabaseObject(true);
        replaceCycleListWithEmptyTextViewIfNoCyclesExist();

        setDefaultUserSettings();
        setDefaultEditRoundViews();
      });
    });
  }

  private void instantiateCycleAdaptersAndTheirCallbacks() {
    savedCycleAdapter = new SavedCycleAdapter(getApplicationContext(), workoutCyclesArray, typeOfRoundArray, workoutTitleArray);
    savedCycleRecycler.setAdapter(savedCycleAdapter);
    savedCycleRecycler.setLayoutManager(workoutCyclesRecyclerLayoutManager);
    savedCycleAdapter.setItemClick(MainActivity.this);
    savedCycleAdapter.setHighlight(MainActivity.this);
    savedCycleAdapter.setResumeOrResetCycle(MainActivity.this);

    savedPomCycleAdapter = new SavedPomCycleAdapter(getApplicationContext(), pomArray, pomTitleArray);
    savedPomCycleRecycler.setAdapter(savedPomCycleAdapter);
    savedPomCycleRecycler.setLayoutManager(pomCyclesRecyclerLayoutManager);
    savedPomCycleAdapter.setItemClick(MainActivity.this);
    savedPomCycleAdapter.setHighlight(MainActivity.this);
    savedPomCycleAdapter.setResumeOrResetCycle(MainActivity.this);

    savedCycleAdapter.notifyDataSetChanged();
  }

  private void instantiateLayoutManagers() {
    workoutCyclesRecyclerLayoutManager = new LinearLayoutManager(getApplicationContext());
    roundRecyclerOneLayoutManager = new LinearLayoutManager(getApplicationContext());
    roundRecyclerTwoLayoutManager = new LinearLayoutManager(getApplicationContext());
    pomCyclesRecyclerLayoutManager = new LinearLayoutManager(getApplicationContext());

    lapReyclerLayoutManager = new LinearLayoutManager(getApplicationContext());
    lapReyclerLayoutManager.setStackFromEnd(true);
    lapReyclerLayoutManager.setReverseLayout(true);
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
    lapRecycler.setLayoutManager(lapReyclerLayoutManager);
  }

  private void instantiateAnimationAndColorMethods() {
    fadeIn = new AlphaAnimation(0.0f, 1.0f);
    fadeOut = new AlphaAnimation(1.0f, 0.0f);
    fadeIn.setDuration(750);
    fadeOut.setDuration(750);
    fadeIn.setFillAfter(true);
    fadeOut.setFillAfter(true);
    slideLeft =  AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_in_left);
    slideLeft.setDuration(400);

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
    sortCyclePopupView = inflater.inflate(R.layout.sort_popup, null);
    editCyclesPopupView = inflater.inflate(R.layout.editing_cycles, null);
    timerPopUpView = inflater.inflate(R.layout.timer_popup, null);
    addTDEEPopUpView = inflater.inflate(R.layout.add_tdee_popup, null);

    savedCyclePopupWindow = new PopupWindow(savedCyclePopupView, convertDensityPixelsToScalable(250), convertDensityPixelsToScalable(450), true);
    deleteCyclePopupWindow = new PopupWindow(deleteCyclePopupView, convertDensityPixelsToScalable(275), convertDensityPixelsToScalable(150), true);
    sortPopupWindow = new PopupWindow(sortCyclePopupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
    editCyclesPopupWindow = new PopupWindow(editCyclesPopupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true);
    settingsPopupWindow = new PopupWindow(settingsPopupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true);
    timerPopUpWindow = new PopupWindow(timerPopUpView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true);
    addTDEEPopUpWindow = new PopupWindow(addTDEEPopUpView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);

    savedCyclePopupWindow.setAnimationStyle(R.style.WindowAnimation);
    deleteCyclePopupWindow.setAnimationStyle(R.style.WindowAnimation);
    sortPopupWindow.setAnimationStyle(R.style.SlideTopAnimation);
    editCyclesPopupWindow.setAnimationStyle(R.style.WindowAnimation);
    settingsPopupWindow.setAnimationStyle(R.style.WindowAnimation);
    timerPopUpWindow.setAnimationStyle(R.style.WindowAnimation);
    addTDEEPopUpWindow.setAnimationStyle(R.style.WindowAnimation);
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
    pomTitleArray = new ArrayList<>();
    receivedHighlightPositions = new ArrayList<>();
    receivedHighlightPositionHolder = new ArrayList<>();
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
    completedLapsLayoutParams = (ConstraintLayout.LayoutParams) cycles_completed.getLayoutParams();

    roundRecyclerParentLayoutParams = (ConstraintLayout.LayoutParams) roundRecyclerLayout.getLayoutParams();
    roundRecyclerOneLayoutParams = (ConstraintLayout.LayoutParams) roundRecycler.getLayoutParams();
    roundRecyclerTwoLayoutParams = (ConstraintLayout.LayoutParams) roundRecyclerTwo.getLayoutParams();

    firstRoundHeaderParams = (ConstraintLayout.LayoutParams) firstRoundTypeHeaderInEditPopUp.getLayoutParams();
    secondRoundHeaderParams = (ConstraintLayout.LayoutParams) secondRoundTypeHeaderInEditPopUp.getLayoutParams();
    thirdRoundHeaderParams = (ConstraintLayout.LayoutParams) thirdRoundTypeHeaderInEditPopUp.getLayoutParams();

    secondEditHeaderParams = (ConstraintLayout.LayoutParams) secondRoundTypeHeaderInEditPopUp.getLayoutParams();
    deleteEditTimerNumbersParams = (ConstraintLayout.LayoutParams) deleteEditPopUpTimerNumbers.getLayoutParams();
  }

  private void setDefaultLayoutVisibilities() {
    blankCanvas.setVisibility(View.GONE);
    edit_highlighted_cycle.setVisibility(View.INVISIBLE);
    delete_highlighted_cycle.setVisibility(View.INVISIBLE);
    cancelHighlight.setVisibility(View.INVISIBLE);

    reset.setVisibility(View.INVISIBLE);
    empty_laps.setVisibility(View.INVISIBLE);

    stopWatchView.setVisibility(View.GONE);
    savedPomCycleRecycler.setVisibility(View.GONE);
    lapRecycler.setVisibility(View.GONE);
    new_lap.setVisibility(View.INVISIBLE);

    roundListDivider.setVisibility(View.GONE);
  }

  private void setDefaultLayoutTexts() {
    confirmActivityAddition.setText(R.string.okay);
    timerValueInEditPopUpTextView.setText("00:00");

    total_set_header.setText(R.string.total_sets);
    total_break_header.setText(R.string.total_breaks);
    total_set_time.setText("0");
    total_break_time.setText("0");
    cycles_completed.setText(R.string.cycles_done);
  }

  private void retrieveAndImplementCycleSorting() {
    sortMode = sharedPreferences.getInt("sortMode", 1);
    sortModePom = sharedPreferences.getInt("sortModePom", 1);
    int checkMarkPosition = sharedPreferences.getInt("checkMarkPosition", 0);

    sortHolder = sortMode;
    sortCheckMark.setY(checkMarkPosition);
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
        saveTotalSetAndBreakTimes();
        saveTotalTdeeTimeAndCalories();

        if (!timerIsPaused) {
          mHandler.postDelayed(globalSaveTotalTimesOnPostDelayRunnableInASyncThread, 1000);
        } else {
          mHandler.removeCallbacks(globalSaveTotalTimesOnPostDelayRunnableInASyncThread);
        }
      }
    };
  };

  private void instantiateSaveTotalTimesOnPostDelayRunnableInASyncThread() {
    globalSaveTotalTimesOnPostDelayRunnableInASyncThread = new Runnable() {
      @Override
      public void run() {
        AsyncTask.execute(globalSaveTotalTimesAndCaloriesInDatabaseRunnable);
        Log.i("testSave", "Executed!");
      }
    };
  }

  private void setTdeeSpinnerListeners() {
    tdee_category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        tdeeCategorySpinnerTouchActions();
        setNumberOfCaloriesBurnedPerMinuteInActivityAdditionPopUpTextView();
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });

    tdee_sub_category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        tdeeSubCategorySpinnerTouchActions();
        setNumberOfCaloriesBurnedPerMinuteInActivityAdditionPopUpTextView();
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });
  }

  private TextWatcher cycleNameEditTextWatcher() {
    return new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }
      @Override
      public void afterTextChanged(Editable s) {
        cycleTitle = cycleNameEdit.getText().toString();
      }
    };
  }

  private void fabLogic() {
    fab.setEnabled(false);
    buttonToLaunchTimer.setEnabled(true);
    cycleNameEdit.getText().clear();
    isNewCycle = true;

    clearRoundAndCycleAdapterArrayLists();
    editCyclesPopupWindow.showAsDropDown(tabLayout);
    setViewsAndColorsToPreventTearingInEditPopUp(true);
    toggleExistenceOfTdeeActivity(false);

    assignOldCycleValuesToCheckForChanges();
    resetEditPopUpTimerHeaders();
    editPopUpTimerArray.clear();
    zeroOutAllTotalTimeAndCalorieVariablesAndTheirTextViews();
    timerValueInEditPopUpTextView.setText("00:00");
  }

  private void stopWatchButtonLogic() {
    savedMode = mode;
    mode = 4;
    dotDraws.setMode(4);
    setInitialTextSizeForRounds(0);
    populateTimerUI();

    timerPopUpWindow.showAtLocation(mainView, Gravity.NO_GRAVITY, 0, 0);
  }

  private View.OnClickListener individualSortOptionButtonsListener() {
    return view -> {
      TextView textButton = (TextView) view;

      if (textButton.getText().toString().equals("Added: Most Recent")) sortHolder = 1;
      if (textButton.getText().toString().equals("Added: Least Recent")) sortHolder = 2;
      if (textButton.getText().toString().equals("Title: A - Z")) sortHolder = 3;
      if (textButton.getText().toString().equals("Title: Z - A")) sortHolder = 4;
      if (textButton.getText().toString().equals("Round Count: Most")) sortHolder = 5;
      if (textButton.getText().toString().equals("Round Count: Least")) sortHolder = 6;

      if (mode==1) sortMode = sortHolder; else if (mode==3) sortModePom = sortHolder;

      AsyncTask.execute(queryAndSortAllCyclesFromDatabaseRunnable());

      prefEdit.putInt("sortMode", sortMode);
      prefEdit.putInt("sortModePom", sortModePom);
      prefEdit.apply();
    };
  }

  private Runnable queryAndSortAllCyclesFromDatabaseRunnable() {
    return new Runnable() {
      @Override
      public void run() {
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
            savedCycleAdapter.notifyDataSetChanged();
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
            savedPomCycleAdapter.notifyDataSetChanged();
          });
        }
        runOnUiThread(()-> {
          clearAndRepopulateCycleAdapterListsFromDatabaseObject(false);
          moveSortCheckmark();
          sortPopupWindow.dismiss();
        });
      }
    };
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
    for (int i=0; i<receivedHighlightPositions.size(); i++) {
      positionOfSelectedCycle = Integer.parseInt(receivedHighlightPositions.get(i));
      receivedHighlightPositionHolder.add(positionOfSelectedCycle);
    }

    delete_highlighted_cycle.setEnabled(false);
    fadeEditCycleButtonsInAndOut(FADE_OUT_HIGHLIGHT_MODE);

    AsyncTask.execute(deleteHighlightedCyclesASyncRunnable());

    if (mode==1) savedCycleAdapter.removeHighlight();
    if (mode==3) savedPomCycleAdapter.removeHighlight();

    Toast.makeText(getApplicationContext(), "Deleted!", Toast.LENGTH_SHORT).show();
  }

  private Runnable deleteHighlightedCyclesASyncRunnable() {
    return new Runnable() {
      @Override
      public void run() {
        if ((mode==1 && cyclesList.size()==0 || (mode==3 && pomCyclesList.size()==0))) {
          runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Nothing saved!", Toast.LENGTH_SHORT).show());
          return;
        }

        int cycleID = 0;
        List<Integer> tempIdList = new ArrayList<>();
        tempIdList.addAll(receivedHighlightPositionHolder);

        loadCycleListsFromDatabase();
        if (mode==1) {
          for (int i=0; i<tempIdList.size(); i++) {
            cycleID = cyclesList.get(i).getId();
            cycles = cyclesDatabase.cyclesDao().loadSingleCycle(cycleID).get(0);
            cyclesDatabase.cyclesDao().deleteCycle(cycles);
          }
        }
        if (mode==3) {
          for (int i=0; i<tempIdList.size(); i++) {
            cycleID = pomCyclesList.get(i).getId();
            pomCycles = cyclesDatabase.cyclesDao().loadSinglePomCycle(cycleID).get(0);
            cyclesDatabase.cyclesDao().deletePomCycle(pomCycles);
          }
        }
        runOnUiThread(()->{
          zeroOutAllTotalTimeAndCalorieVariablesAndTheirTextViews();
          editCycleArrayLists(DELETING_CYCLE);
          replaceCycleListWithEmptyTextViewIfNoCyclesExist();
        });
      }
    };
  }

  private Runnable deleteAllCyclesASyncRunnable() {
    return new Runnable() {
      @Override
      public void run() {
        {
          loadCycleListsFromDatabase();
          if (mode==1) {
            if (cyclesList.size()>0) {
              cyclesDatabase.cyclesDao().deleteAllCycles();
              runOnUiThread(()->{
                //Clears adapter arrays and populates recyclerView with (nothing) since arrays are now empty. Also called notifyDataSetChanged().
                workoutCyclesArray.clear();
                typeOfRoundArray.clear();
                workoutTitleArray.clear();
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
            zeroOutAllTotalTimeAndCalorieVariablesAndTheirTextViews();
            replaceCycleListWithEmptyTextViewIfNoCyclesExist();
          });
        };
      }
    };
  }

  private Runnable deleteTotalCycleTimesASyncRunnable() {
    return new Runnable() {
      @Override
      public void run() {
        if (mode==1) cyclesDatabase.cyclesDao().deleteTotalTimesCycle();
        if (mode==3) cyclesDatabase.cyclesDao().deleteTotalTimesPom();

        runOnUiThread(()->{
          deleteCyclePopupWindow.dismiss();
          totalCycleSetTimeInMillis = 0;
          totalCycleBreakTimeInMillis = 0;

          cyclesCompleted = 0;
          resettingTotalTime = true;

          total_set_time.setText("0");
          total_break_time.setText("0");
          cycles_completed.setText(getString(R.string.cycles_done, "0"));
        });
      }
    };
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
    isMetric = sp.getBoolean("isMetric", false);
    userGender = sp.getString("tdeeGender", "male");
    userAge = sp.getInt("tdeeAge,", 18);
    userWeight = sp.getInt("tdeeWeight,", 150);
    userHeight = sp.getInt("tdeeHeight,", 66);
  }

  private void launchGlobalSettings() {
    settingsFragmentFrameLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_anim));
    settingsFragmentFrameLayout.setVisibility(View.VISIBLE);

    if (rootSettingsFragment !=null) {
      getSupportFragmentManager().beginTransaction()
              .setCustomAnimations(R.anim.slide_in_from_bottom, R.anim.slide_in_from_bottom)
              .attach(rootSettingsFragment)
              .commit();
    }
    aSettingsMenuIsVisible = true;
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
    buttonToLaunchTimer.setEnabled(false);
    reset.setVisibility(View.INVISIBLE);
    dotDraws.setMode(mode);

    if (mode!=4) {
      if (!timerIsPaused) pauseAndResumeTimer(PAUSING_TIMER);
      mHandler.removeCallbacksAndMessages(null);

      if (mode==1) {
        savedCycleRecycler.setVisibility(View.VISIBLE);
        savedCycleAdapter.notifyDataSetChanged();
      } else if (mode==3){
        savedPomCycleRecycler.setVisibility(View.VISIBLE);
        savedPomCycleAdapter.notifyDataSetChanged();
      }
      AsyncTask.execute(globalSaveTotalTimesAndCaloriesInDatabaseRunnable);
    } else {
      if (!stopWatchIsPaused) pauseAndResumeTimer(PAUSING_TIMER);
      //If dismissing stopwatch, switch to whichever non-stopwatch mode we were on before.
      mode = savedMode;
    }
  }

  private void editCyclesPopUpDismissalLogic() {
    if (currentlyEditingACycle) {
      saveCycleOnPopUpDismissIfEdited();

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
      AsyncTask.execute(saveAddedOrEditedCycleASyncRunnable());

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

  private void editHighlightedCycleButtonClickLogic() {
    editCyclesPopupWindow.showAsDropDown(tabLayout);
    buttonToLaunchTimer.setEnabled(true);
    currentlyEditingACycle = true;
    isNewCycle = false;
    positionOfSelectedCycle = Integer.parseInt(receivedHighlightPositions.get(0));
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
      }
    }

    if (headerToSelect == 3) {
      firstRoundTypeHeaderInEditPopUp.setTextColor(Color.GRAY);
      secondRoundTypeHeaderInEditPopUp.setTextColor(Color.GRAY);
      thirdRoundTypeHeaderInEditPopUp.setTextColor(fullBreakColor);
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
      String editPopUpTimerStringOne = "";
      String editPopUpTimerStringTwo = "";
      String editPopUpTimerStringThree = "";

      editPopUpTimerStringOne =  convertedTimerArrayToString(savedEditPopUpArrayForFirstHeaderModeThree);
      totalTimeOne = convertStringToSecondsValue(editPopUpTimerStringOne);
      pomTimerValueInEditPopUpTextViewOne.setText(editPopUpTimerStringOne);

      editPopUpTimerStringTwo = convertedTimerArrayToString(savedEditPopUpArrayForSecondHeaderModeThree);
      totalTimeTwo = convertStringToSecondsValue(editPopUpTimerStringTwo);
      pomTimerValueInEditPopUpTextViewTwo.setText(editPopUpTimerStringTwo);

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
    if (popUpIsActive) {
      //Prevents tearing when soft keyboard pushes up in editCycle popUp.
      if (mode==1) savedCycleRecycler.setVisibility(View.GONE);
      if (mode==3) savedPomCycleRecycler.setVisibility(View.GONE);
      emptyCycleList.setVisibility(View.GONE);
      mainView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.test_black));
    } else {
      //Resets Main's recyclerView visibility if we are not launching timer for smoother transition between popups.
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
      mainView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
    }

  }

  private void resumeOrResetCycleFromAdapterList(int resumeOrReset){
    if (resumeOrReset==RESUMING_CYCLE_FROM_ADAPTER) {
      progressBar.setProgress(currentProgressBarValue);
      timerPopUpWindow.showAtLocation(mainView, Gravity.NO_GRAVITY, 0, 0);
      //Sets paused boolean to true, so next timer click will resume.
      timerIsPaused = true;
      timeLeft.setText(retrieveTimerValueString());
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

  private void moveSortCheckmark() {
    ConstraintLayout.LayoutParams checkMarkParams =  (ConstraintLayout.LayoutParams) sortCheckMark.getLayoutParams();

    int markPosition = 0;
    switch (sortHolder) {
      case 1:
        markPosition = convertDensityPixelsToScalable(10); break;
      case 2:
        markPosition = convertDensityPixelsToScalable(42); break;
      case 3:
        markPosition = convertDensityPixelsToScalable(74); break;
      case 4:
        markPosition = convertDensityPixelsToScalable(106); break;
      case 5:
        markPosition = convertDensityPixelsToScalable(138); break;
      case 6:
        markPosition = convertDensityPixelsToScalable(170); break;
    }
    sortCheckMark.setY(markPosition);
    prefEdit.putInt("checkMarkPosition", markPosition);
    prefEdit.apply();
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

      if (timerPopUpIsVisible) {
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

        if (mode==4) {
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
      if (!objectAnimator.isStarted()) mHandler.post(postRoundRunnableForFirstMode());

      prefEdit.putInt("savedProgressBarValueForModeOne", currentProgressBarValue);
      prefEdit.putString("timeLeftValueForModeOne", timeLeft.getText().toString());
      prefEdit.putInt("positionOfSelectedCycleForModeOne", positionOfSelectedCycle);
      prefEdit.putBoolean("modeOneTimerPaused", timerIsPaused);
      prefEdit.putBoolean("modeOneTimerEnded", timerEnded);
      prefEdit.putBoolean("modeOneTimerDisabled", timerDisabled);
    }

    if (mode==3) {
      if (activeCycle) {
        if (isNewCycle) positionOfSelectedCycle = 7;
        savedPomCycleAdapter.showActiveCycleLayout(positionOfSelectedCycle, pomDotCounter);
      }
      if (!objectAnimatorPom.isStarted()) mHandler.post(postRoundRunnableForThirdMode());

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
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, pixels, getResources().getDisplayMetrics());
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
    }
    if (typeOfFade==FADE_IN_HIGHLIGHT_MODE) {
      edit_highlighted_cycle.startAnimation(fadeIn);
      delete_highlighted_cycle.startAnimation(fadeIn);
      cancelHighlight.startAnimation(fadeIn);
      appHeader.startAnimation(fadeOut);
      sortButton.startAnimation(fadeOut);
      sortButton.setEnabled(false);
      edit_highlighted_cycle.setEnabled(true);
      delete_highlighted_cycle.setEnabled(true);
    }
    if (typeOfFade==FADE_OUT_HIGHLIGHT_MODE) {
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

  //Conversion of Long->String for CIRCLE TIMER textViews.
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
        return startingMillis>=60000;
      } else {
        return startingMillis<=59000;
      }
    } else if (mode==3) {
      return startingMillis>=60000;
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

  private void newLap() {
    if (lapAdapter.getItemCount()>98) {
      return;
    }
    if (empty_laps.getVisibility()==View.VISIBLE) empty_laps.setVisibility(View.INVISIBLE);

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
    lapReyclerLayoutManager.scrollToPosition(savedLapList.size() - 1);

    lapAdapter.notifyDataSetChanged();

    lapsNumber++;
    cycles_completed.setText(getString(R.string.laps_completed, String.valueOf(lapsNumber)));

    lapAdapter.resetLapAnimation();
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
          Toast.makeText(getApplicationContext(), "Pomodoro cycle already loaded!", Toast.LENGTH_SHORT).show();
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
          Toast toast = Toast.makeText(getApplicationContext(), "No rounds to clear!", Toast.LENGTH_SHORT);
          toast.show();
        }
      } else if (mode==3) {
        if (pomValuesTime.size() != 0) {
          cycleRoundsAdapter.setPomFade(false);
          cycleRoundsAdapter.notifyDataSetChanged();
          subtractRoundFromCycleButton.setClickable(false);
        } else Toast.makeText(getApplicationContext(), "No Pomodoro cycle to clear!", Toast.LENGTH_SHORT).show();
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
          Toast.makeText(getApplicationContext(), "Full!", Toast.LENGTH_SHORT).show();
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
      } else Toast.makeText(getApplicationContext(), "Empty!", Toast.LENGTH_SHORT).show();
    }
  }

  private void setRoundRecyclerViewsWhenChangingAdapterCount(ArrayList<Integer> adapterList) {
    if (mode==1) {
      if (adapterList.size()<=8) {
        roundRecyclerTwo.setVisibility(View.GONE);
        roundListDivider.setVisibility(View.GONE);

        //16:9 is ~1.7.
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

  private void clearAndRepopulateCycleAdapterListsFromDatabaseObject(boolean forAllModes) {
    if (mode==1 || forAllModes) {
      workoutCyclesArray.clear();
      typeOfRoundArray.clear();
      workoutTitleArray.clear();
      for (int i=0; i<cyclesList.size(); i++) {
        workoutCyclesArray.add(cyclesList.get(i).getWorkoutRounds());
        workoutTitleArray.add(cyclesList.get(i).getTitle());
        typeOfRoundArray.add(cyclesList.get(i).getRoundType());
      }
    }
    if (mode==3 || forAllModes) {
      pomArray.clear();
      pomTitleArray.clear();
      for (int i=0; i<pomCyclesList.size(); i++) {
        pomArray.add(pomCyclesList.get(i).getFullCycle());
        pomTitleArray.add(pomCyclesList.get(i).getTitle());
      }
    }
  }

  private void editCycleArrayLists(int action) {
    if (action == ADDING_CYCLE) {
      if (mode==1) {
        workoutTitleArray.add(cycleTitle);
        typeOfRoundArray.add(roundTypeString);
        workoutCyclesArray.add(workoutString);
      }
      if (mode==3) {
        pomTitleArray.add(cycleTitle);
        pomArray.add(pomString);
      }
    }
    else if (action == EDITING_CYCLE) {
      if (mode==1) {
        workoutTitleArray.set(positionOfSelectedCycle, cycleTitle);
        workoutCyclesArray.set(positionOfSelectedCycle, workoutString);
        typeOfRoundArray.set(positionOfSelectedCycle, roundTypeString);
      }
      if (mode==3) {
        pomTitleArray.set(positionOfSelectedCycle, cycleTitle);
        pomArray.set(positionOfSelectedCycle, pomString);
      }
    } else if (action == DELETING_CYCLE) {
      int posToRemove = receivedHighlightPositionHolder.get(0);

      if (mode==1) {
        for (int i=0; i<receivedHighlightPositionHolder.size(); i++) {
          workoutTitleArray.remove(posToRemove);
          typeOfRoundArray.remove(posToRemove);
          workoutCyclesArray.remove(posToRemove);
          posToRemove = receivedHighlightPositionHolder.get(i);
          if (posToRemove>=1) {
            posToRemove--;
          }
        }
      }
      if (mode==3) {
        for (int i=0; i<receivedHighlightPositionHolder.size(); i++) {
          pomTitleArray.remove(posToRemove);
          pomArray.remove(posToRemove);
          posToRemove = receivedHighlightPositionHolder.get(i);
          if (posToRemove>=1) {
            posToRemove--;
          }
        }
      }

      receivedHighlightPositions.clear();
      receivedHighlightPositionHolder.clear();
    }

    runOnUiThread(()-> {
      if (mode==1) savedCycleAdapter.notifyDataSetChanged();
      if (mode==3) savedPomCycleAdapter.notifyDataSetChanged();
    });
  }

  private void populateCycleAdapterArrayList() {
    switch (mode) {
      case 1:
        workoutTime.clear();
        typeOfRound.clear();
        if (workoutCyclesArray.size()-1>=positionOfSelectedCycle) {
          String[] fetchedRounds = workoutCyclesArray.get(positionOfSelectedCycle).split(" - ");
          String[] fetchedRoundType = typeOfRoundArray.get(positionOfSelectedCycle).split(" - ");
          for (int i=0; i<fetchedRounds.length; i++) workoutTime.add(Integer.parseInt(fetchedRounds[i]));
          for (int j=0; j<fetchedRoundType.length; j++) typeOfRound.add(Integer.parseInt(fetchedRoundType[j]));
          cycleTitle = workoutTitleArray.get(positionOfSelectedCycle);
          buttonToLaunchTimer.setEnabled(true);
        }
        break;
      case 3:
        pomValuesTime.clear();
        if (pomArray.size()-1>=positionOfSelectedCycle) {
          String[] fetchedPomCycle = pomArray.get(positionOfSelectedCycle).split(" - ");
          cycleTitle = pomTitleArray.get(positionOfSelectedCycle);
          buttonToLaunchTimer.setEnabled(true);

          /////---------Testing pom round iterations---------------/////////
//          for (int i=0; i<8; i++) if (i%2!=0) pomValuesTime.add(5000); else pomValuesTime.add(7000);
          for (int i=0; i<fetchedPomCycle.length; i++) pomValuesTime.add(Integer.parseInt(fetchedPomCycle[i]));
        }
        break;
    }
  }

  private void launchTimerCycle(boolean saveToDB) {
    if ((mode==1 && workoutTime.size()==0) || (mode==3 && pomValuesTime.size()==0)) {
      Toast.makeText(getApplicationContext(), "Cycle cannot be empty!", Toast.LENGTH_SHORT).show();
      return;
    }
    editCyclesPopupWindow.dismiss();
    setViewsAndColorsToPreventTearingInEditPopUp(true);

    if (mode==1) {
      if (savedCycleAdapter.isCycleActive()==true) {
        savedCycleAdapter.removeActiveCycleLayout();
        savedCycleAdapter.notifyDataSetChanged();
      }
    }

    if (mode==3) {
      if (savedPomCycleAdapter.isCycleActive()==true) {
        savedPomCycleAdapter.removeActiveCycleLayout();
        savedPomCycleAdapter.notifyDataSetChanged();
      }
    }

    if (isNewCycle || saveToDB) {
      AsyncTask.execute(saveAddedOrEditedCycleASyncRunnable());
    } else {
      AsyncTask.execute(queryDatabaseAndRetrieveCycleTimesAndCaloriesRunnable());
    }

    makeCycleAdapterVisible = true;
    timerPopUpIsVisible = true;

    resetTimer();
    populateTimerUI();

    timerPopUpWindow.showAtLocation(mainView, Gravity.NO_GRAVITY, 0, 0);
  }

  private Runnable saveAddedOrEditedCycleASyncRunnable() {
    return new Runnable() {
      @Override
      public void run() {
        //Sets up Strings to save into database.
        Gson gson = new Gson();
        workoutString = "";
        roundTypeString = "";
        pomString = "";

        calendar = Calendar.getInstance();
        date = simpleDateFormat.format(calendar.getTime());

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
            cycles.setTdeeValuePosition(selectedTdeeValuePosition);
          } else {
            cycles.setTdeeActivityExists(false);
            cycles.setTdeeCatPosition(0);
            cycles.setTdeeValuePosition(0);
          }
          if (!workoutString.equals("")) {
            cycles.setWorkoutRounds(workoutString);
            cycles.setRoundType(roundTypeString);
            cycles.setTimeAccessed(System.currentTimeMillis());
            cycles.setItemCount(workoutTime.size());
            if (isNewCycle) {
              if (cycleTitle.isEmpty()) {
                cycleTitle = date;
              }
              cycles.setTimeAdded(System.currentTimeMillis());
              cycles.setTotalSetTime(0);
              cycles.setTotalBreakTime(0);
              cycles.setCyclesCompleted(0);
              cycles.setTitle(cycleTitle);

              cyclesDatabase.cyclesDao().insertCycle(cycles);
              editCycleArrayLists(ADDING_CYCLE);
            } else {
              cycles.setTitle(cycleTitle);
              cyclesDatabase.cyclesDao().updateCycles(cycles);
              editCycleArrayLists(EDITING_CYCLE);
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
              pomCycles.setTitle(cycleTitle);
              cyclesDatabase.cyclesDao().insertPomCycle(pomCycles);
              //Adding to adapter list.
              editCycleArrayLists(ADDING_CYCLE);
            } else {
              pomCycles.setTitle(cycleTitle);
              cyclesDatabase.cyclesDao().updatePomCycles(pomCycles);
              editCycleArrayLists(EDITING_CYCLE);
            }
          }
        }
        loadCycleListsFromDatabase();
        runOnUiThread(()-> cycle_title_textView.setText(cycleTitle));
      }
    };
  }

  private Runnable queryDatabaseAndRetrieveCycleTimesAndCaloriesRunnable() {
    return new Runnable() {
      @Override
      public void run() {
        if (mode==1) cyclesList = cyclesDatabase.cyclesDao().loadAllCycles();
        if (mode==3) pomCyclesList = cyclesDatabase.cyclesDao().loadAllPomCycles();
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            retrieveTotalSetAndBreakAndCycleValuesAndSetTheirTextViews();
            retrieveTdeeTimeAndCalorieStatsAndSetTheirTextView();
          }
        });
      }
    };
  }

  private void loadCycleListsFromDatabase() {
    if (mode==1) {
      cyclesList = cyclesDatabase.cyclesDao().loadAllCycles();
    }
    if (mode==3) {
      pomCyclesList = cyclesDatabase.cyclesDao().loadAllPomCycles();
    }
  }

  private void instantiateAndStartObjectAnimator(long duration) {
    if (mode==1) {
      objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", (int) maxProgress, 0);
      objectAnimator.setInterpolator(new LinearInterpolator());
      objectAnimator.setDuration(duration);
      objectAnimator.start();
    }
    if (mode==3) {
      objectAnimatorPom = ObjectAnimator.ofInt(progressBar, "progress", (int) maxProgress, 0);
      objectAnimatorPom.setInterpolator(new LinearInterpolator());
      objectAnimatorPom.setDuration(duration);
      objectAnimatorPom.start();
    }
  }

  private void startObjectAnimatorAndTotalCycleTimeCounters() {
    switch (mode) {
      case 1:
        if (typeOfRound.get(currentRound).equals(1)) {
          if (currentProgressBarValue==maxProgress) {
            timerDurationPlaceHolder = setMillis;
            tdeeActivityTimeDurationPlaceHolder = setMillis;
            timerIsPaused = false;
            instantiateAndStartObjectAnimator(setMillis);
          } else {
            setMillis = setMillisUntilFinished;
            if (objectAnimator != null) objectAnimator.resume();
          }
        } else if (typeOfRound.get(currentRound).equals(3)) {
          if (currentProgressBarValue==maxProgress) {
            timerDurationPlaceHolder = breakMillis;
            timerIsPaused = false;
            instantiateAndStartObjectAnimator(breakMillis);
          } else {
            breakMillis = breakMillisUntilFinished;
            if (objectAnimator != null) objectAnimator.resume();
          }
        }
        if (typeOfRound.get(currentRound).equals(2)) {
          cycleSetTimeForSingleRoundInMillis = setMillis;
          singleInstanceTdeeActivityTime = setMillis;
        }
        if (typeOfRound.get(currentRound).equals(4)) {
          cycleSetTimeForSingleRoundInMillis = breakMillis;
        }
        break;
      case 3:
        if (currentProgressBarValue==maxProgress){
          timerIsPaused = false;
          pomMillis = pomValuesTime.get(pomDotCounter);


          timerDurationPlaceHolder = pomMillis;
          instantiateAndStartObjectAnimator(pomMillis);
        } else {
          pomMillis = pomMillisUntilFinished;
          if (objectAnimatorPom != null) objectAnimatorPom.resume();
        }
        break;
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
        setMillis = (int) (countUpMillisHolder) +  (System.currentTimeMillis() - defaultProgressBarDurationForInfinityRounds);
        displayTotalTimesAndCalories();

        timeLeft.setText(convertSeconds((setMillis) / 1000));

        workoutTime.set(workoutTime.size() - numberOfRoundsLeft, (int) setMillis);
        dotDraws.updateWorkoutTimes(workoutTime, typeOfRound);

        setNotificationValues();
        mHandler.postDelayed(this, 50);
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
        breakMillis = (int) (countUpMillisHolder) +  (System.currentTimeMillis() - defaultProgressBarDurationForInfinityRounds);
        displayTotalTimesAndCalories();

        timeLeft.setText(convertSeconds((breakMillis) / 1000));

        workoutTime.set(workoutTime.size() - numberOfRoundsLeft, (int) breakMillis);
        dotDraws.updateWorkoutTimes(workoutTime, typeOfRound);

        setNotificationValues();
        mHandler.postDelayed(this, 50);
      }
    };
  }

  private void startSetTimer() {
    setInitialTextSizeForRounds(setMillis);
    boolean willWeChangeTextSize = checkIfRunningTextSizeChange(setMillis);

    timer = new CountDownTimer(setMillis, 50) {
      @Override
      public void onTick(long millisUntilFinished) {
        setNotificationValues();

        setCountDownTimerTickLogic(millisUntilFinished, setMillis);
        displayTotalTimesAndCalories();
        changeTextSizeOnTimerDigitCountTransitionForModeOne(breakMillis);

        dotDraws.reDraw();
      }

      @Override
      public void onFinish() {
        nextRound(false);
      }
    }.start();
  }

  private void startBreakTimer() {
    setInitialTextSizeForRounds(breakMillis);
    boolean willWeChangeTextSize = checkIfRunningTextSizeChange(breakMillis);

    timer = new CountDownTimer(breakMillis, 50) {
      @Override
      public void onTick(long millisUntilFinished) {
        setNotificationValues();

        setCountDownTimerTickLogic(millisUntilFinished, breakMillis);
        displayTotalTimesAndCalories();
        changeTextSizeOnTimerDigitCountTransitionForModeOne(breakMillis);

        dotDraws.reDraw();
      }

      @Override
      public void onFinish() {
        nextRound(false);
      }
    }.start();
  }

  private void startPomTimer() {
    setInitialTextSizeForRounds(pomMillis);
    boolean willWeChangeTextSize = checkIfRunningTextSizeChange(pomMillis);

    timer = new CountDownTimer(pomMillis, 50) {
      @Override
      public void onTick(long millisUntilFinished) {
        setNotificationValues();

        setCountDownTimerTickLogic(millisUntilFinished, pomMillis);
        displayTotalTimesAndCalories();
        changeTextSizeOnTimerDigitCountTransitionForModeThree();

        dotDraws.reDraw();
      }

      @Override
      public void onFinish() {
        nextRound(false);
      }
    }.start();
  }

  private void setCountDownTimerTickLogic(long classMillisUntilFinishedVariable, long typeOfRoundMillis) {
    if (mode==1) {
      currentProgressBarValue = (int) objectAnimator.getAnimatedValue();
      if (typeOfRoundMillis==setMillis) {
        setMillis = classMillisUntilFinishedVariable;
      }
      if (typeOfRoundMillis==breakMillis) {
        breakMillis = classMillisUntilFinishedVariable;
      }
    }
    if (mode==3) {
      currentProgressBarValue = (int) objectAnimatorPom.getAnimatedValue();
      pomMillis = classMillisUntilFinishedVariable;
    }
    timeLeft.setText(convertSeconds((typeOfRoundMillis + 999) / 1000));
    if (typeOfRoundMillis < 500) timerDisabled = true;
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

  private void nextRound(boolean endingEarly) {
    progressBar.startAnimation(fadeProgressOut);
    timeLeft.startAnimation(fadeProgressOut);
    currentProgressBarValue = 10000;
    reset.setVisibility(View.INVISIBLE);
    next_round.setEnabled(false);
    timerDisabled = true;

    addAndRoundDownTotalCycleTimes();
    addAndRoundDownTdeeTimeAndTotalCalories();
    AsyncTask.execute(globalSaveTotalTimesAndCaloriesInDatabaseRunnable);

    if (mode==1) {
      if (numberOfRoundsLeft==0) {
        mHandler.removeCallbacks(endFade);
        resetTimer();
        return;
      }
      mHandler.post(endFade);

      if (endingEarly) {
        if (timer != null) timer.cancel();
        if (objectAnimator != null) objectAnimator.cancel();
        progressBar.setProgress(0);
        if (!activeCycle) activeCycle = true;
      }

      boolean isAlertRepeating = false;
      switch (typeOfRound.get(currentRound)) {
        case 1:
          timeLeft.setText("0");
          changeTextSizeWithoutAnimator(true);
          total_set_time.setText(convertSeconds(totalCycleSetTimeInMillis/1000));

          if (numberOfRoundsLeft==1 && isLastRoundSoundContinuous) isAlertRepeating = true;
          setEndOfRoundSounds(vibrationSettingForSets, false);
          break;
        case 2:
          mHandler.removeCallbacks(infinityTimerForSets);
          total_set_time.setText(convertSeconds(totalCycleSetTimeInMillis/1000));

          if (numberOfRoundsLeft==1 && isLastRoundSoundContinuous) isAlertRepeating = true;
          setEndOfRoundSounds(vibrationSettingForSets, false);
          break;
        case 3:
          timeLeft.setText("0");
          changeTextSizeWithoutAnimator(true);
          total_break_time.setText(convertSeconds(totalCycleBreakTimeInMillis/1000));

          if (numberOfRoundsLeft==1 && isLastRoundSoundContinuous) isAlertRepeating = true;
          setEndOfRoundSounds(vibrationSettingForBreaks, false);
          break;
        case 4:
          mHandler.removeCallbacks(infinityTimerForBreaks);
          total_break_time.setText(convertSeconds(totalCycleBreakTimeInMillis/1000));

          if (numberOfRoundsLeft==1 && isLastRoundSoundContinuous) isAlertRepeating = true;
          setEndOfRoundSounds(vibrationSettingForBreaks, false);
          break;
      }

      numberOfRoundsLeft--;
      currentRound++;
      mHandler.postDelayed(postRoundRunnableForFirstMode(), 750);

      beginTimerForNextRound = true;
      timerDurationPlaceHolder = 0;
      tdeeActivityTimeDurationPlaceHolder = 0;
    }
    if (mode==3) {
      switch (pomDotCounter) {
        case 0: case 2: case 4: case 6:
          total_set_time.setText(convertSeconds(totalCycleSetTimeInMillis/1000));
          setEndOfRoundSounds(vibrationSettingForWork, false);
          break;
        case 1: case 3: case 5:
          total_break_time.setText(convertSeconds(totalCycleBreakTimeInMillis/1000));
          setEndOfRoundSounds(vibrationSettingForMiniBreaks, false);
          break;
        case 7:
          total_break_time.setText(convertSeconds(totalCycleBreakTimeInMillis/1000));

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
              timeLeft.setText(convertSeconds((setMillis + 999) / 1000));
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
              timeLeft.setText(convertSeconds((breakMillis + 999) / 1000));
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
          cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(cyclesCompleted)));
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
          timeLeft.setText(convertSeconds((pomMillis) / 1000));
          if (beginTimerForNextRound) {
            startObjectAnimatorAndTotalCycleTimeCounters();
            startPomTimer();
          }
        } else {
          animateEnding();
          progressBar.setProgress(0);
          timerEnded = true;
          cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(cyclesCompleted)));
        }
        timerDisabled = false;
        next_round.setEnabled(true);
      }
    };
  }

  private void retrieveTotalSetAndBreakAndCycleValuesAndSetTheirTextViews() {
    int totalSetTime = 0;
    int totalBreakTime = 0;

    if (mode == 1) {
      cycles = cyclesList.get(positionOfSelectedCycle);
      totalSetTime = cycles.getTotalSetTime();
      totalBreakTime = cycles.getTotalBreakTime();
    }

    if (mode == 3) {
      pomCycles = pomCyclesList.get(positionOfSelectedCycle);
      totalSetTime = pomCycles.getTotalWorkTime();
      totalBreakTime = pomCycles.getTotalBreakTime();
      cyclesCompleted = pomCycles.getCyclesCompleted();
    }

    totalCycleSetTimeInMillis = totalSetTime * 1000;
    totalCycleBreakTimeInMillis = totalBreakTime * 1000;
    total_set_time.setText(convertSeconds(totalSetTime));
    total_break_time.setText(convertSeconds(totalBreakTime));
    cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(cyclesCompleted)));
  }

  private void retrieveTdeeTimeAndCalorieStatsAndSetTheirTextView() {
    cycles = cyclesList.get(positionOfSelectedCycle);

    totalTdeeActivityTime = cycles.getTotalTdeeActivityTimeElapsed() * 1000;
    burnedCaloriesInAllLoadingsOfCycle = cycles.getTotalCaloriesBurned();
    selectedTdeeCategoryPosition = cycles.getTdeeCatPosition();
    selectedTdeeSubCategoryPosition = cycles.getTdeeSubCatPosition();
    selectedTdeeValuePosition = cycles.getTdeeValuePosition();
    metScore = retrieveMetScoreFromSubCategoryPosition();

    cycleHasActivityAssigned = cycles.getTdeeActivityExists();
    toggleTdeeTextViewVisibility();
    displayTotalTdeeTimeAndCaloriesAsUniqueVarAtCycleLaunchOnly();
  }

  private void toggleTdeeTextViewVisibility() {
    if (!cycleHasActivityAssigned) {
      actvitiyStatsInTimerTextView.setText(R.string.no_activity);
      actvitiyStatsInTimerTextView.setTextSize(24);
    } else {
      actvitiyStatsInTimerTextView.setTextSize(20);
    }
  }

  private void resetTotalTimesAndCalories() {
    if (resettingTotalTime) {
      if (mode==1) {
        switch (typeOfRound.get(currentRound)) {
          case 1:
            roundedValueForTotalTimes = 1000 - (setMillis%1000);
            timerDurationPlaceHolder = setMillis + roundedValueForTotalTimes;
            if (cycleHasActivityAssigned) {
              long roundedValueForTdeeTime = 1000 - (setMillis%1000);
              tdeeActivityTimeDurationPlaceHolder = setMillis + roundedValueForTdeeTime;
            }
            break;
          case 2:
            long remainder = setMillis%1000;
            timerDurationPlaceHolder = setMillis - remainder;
            if (cycleHasActivityAssigned) {
              remainder = setMillis%1000;
              tdeeActivityTimeDurationPlaceHolder = setMillis - remainder;
            }
            break;
          case 3:
            roundedValueForTotalTimes = 1000 - (breakMillis%1000);
            timerDurationPlaceHolder = breakMillis + roundedValueForTotalTimes;
            break;
          case 4:
            remainder = breakMillis%1000;
            timerDurationPlaceHolder = breakMillis - remainder;
            break;
        }
      }
      if (mode==3) {
        switch (pomDotCounter) {
          case 0: case 2: case 4: case 6:
            if (resettingTotalTime) {
              roundedValueForTotalTimes = 1000 - (pomMillis%1000);
              timerDurationPlaceHolder = pomMillis + roundedValueForTotalTimes;
            }
            break;
          case 1: case 3: case 5: case 7:
            if (resettingTotalTime) {
              roundedValueForTotalTimes = 1000 - (pomMillis%1000);
              timerDurationPlaceHolder = pomMillis + roundedValueForTotalTimes;
            }
            break;
        }
      }

      resettingTotalTime = false;
    }
  }

  private void zeroOutAllTotalTimeAndCalorieVariablesAndTheirTextViews() {
    if (mode==1) {
      roundedValueForTotalTimes = 0;
      timerDurationPlaceHolder = 0;

      cycleSetTimeForSingleRoundInMillis = 0;
      totalCycleSetTimeInMillis = 0;
      cycleBreakTimeForSingleRoundInMillis = 0;
      totalCycleBreakTimeInMillis = 0;

      tdeeActivityTimeDurationPlaceHolder = 0;
      singleInstanceTdeeActivityTime = 0;
      totalTdeeActivityTime = 0;

      total_set_time.setText("0");
      total_break_time.setText("0");
      cycles_completed.setText("0");
    }
  }

  private long iterateAndReturnTotalTimeForModeOne() {
    long valueToReturn = 0;

    switch (typeOfRound.get(currentRound)) {
      case 1:
        cycleSetTimeForSingleRoundInMillis = timerDurationPlaceHolder - setMillis;
        valueToReturn = (totalCycleSetTimeInMillis + cycleSetTimeForSingleRoundInMillis);
        break;
      case 2:
        cycleSetTimeForSingleRoundInMillis = setMillis - timerDurationPlaceHolder;
        valueToReturn = (totalCycleSetTimeInMillis + cycleSetTimeForSingleRoundInMillis);
        break;
      case 3:
        cycleBreakTimeForSingleRoundInMillis = timerDurationPlaceHolder - breakMillis;
        valueToReturn = (totalCycleBreakTimeInMillis + cycleBreakTimeForSingleRoundInMillis);
        break;
      case 4:
        cycleBreakTimeForSingleRoundInMillis = breakMillis - timerDurationPlaceHolder;
        valueToReturn = (totalCycleBreakTimeInMillis + cycleBreakTimeForSingleRoundInMillis);
        break;
    };
    return valueToReturn;
  }

  private String currentTotalTimeStringForModeOne() {
    return convertSeconds(iterateAndReturnTotalTimeForModeOne()/1000);
  }

  private void displayCurrentTotalTimeString() {
    if (mode==1) {
      switch (typeOfRound.get(currentRound)) {
        case 1: case 2:
          total_set_time.setText(currentTotalTimeStringForModeOne());
          break;
        case 3: case 4:
          total_break_time.setText(currentTotalTimeStringForModeOne());
          break;
      }
    }
    if (mode==3) {
      switch (pomDotCounter) {
        case 0: case 2: case 4: case 6:
          total_set_time.setText(currentTotalTimeStringForModeThree());
          break;
        case 1: case 3: case 5: case 7:
          total_break_time.setText(currentTotalTimeStringForModeThree());
          break;
      }
    }

  }

  private long iterateAndReturnTotalTimeForModeThree() {
    long valueToReturn = 0;

    switch (pomDotCounter) {
      case 0: case 2: case 4: case 6:
        cycleSetTimeForSingleRoundInMillis = timerDurationPlaceHolder - pomMillis;
        valueToReturn = (totalCycleSetTimeInMillis + cycleSetTimeForSingleRoundInMillis);
        break;
      case 1: case 3: case 5: case 7:
        cycleBreakTimeForSingleRoundInMillis = timerDurationPlaceHolder - pomMillis;
        valueToReturn = (totalCycleBreakTimeInMillis + cycleBreakTimeForSingleRoundInMillis);
        break;
    }
    return valueToReturn;
  }

  private String currentTotalTimeStringForModeThree() {
    return convertSeconds(iterateAndReturnTotalTimeForModeThree()/1000);
  }


  private long currentTotalTdeeTimeLongValue() {
    if (typeOfRound.get(currentRound)==1) {
      singleInstanceTdeeActivityTime = tdeeActivityTimeDurationPlaceHolder - setMillis;
    }
    if (typeOfRound.get(currentRound)==2) {
      singleInstanceTdeeActivityTime = setMillis - tdeeActivityTimeDurationPlaceHolder;
    }
    return (totalTdeeActivityTime + singleInstanceTdeeActivityTime);
  }

  private String currentTdeeStatString() {
    long elapstedTdeeTimeInSeconds = currentTotalTdeeTimeLongValue()/1000;
    String elapsedTdeeTimeString = convertSeconds(elapstedTdeeTimeInSeconds);

    burnedCaloriesInAllLoadingsOfCycle = calculateCaloriesBurnedPerSecond() * elapstedTdeeTimeInSeconds;

    return getString(R.string.tdee_activity_in_timer_stats, getTdeeActivityStringFromSavedArrayPosition(), elapsedTdeeTimeString, formatCalorieString(burnedCaloriesInAllLoadingsOfCycle));
  }

  private void displayTotalTimesAndCalories() {
    long remainder = 0;
    resetTotalTimesAndCalories();
    displayCurrentTotalTimeString();

    if (mode==1) {
      if (cycleHasActivityAssigned) {
        actvitiyStatsInTimerTextView.setText(currentTdeeStatString());
      }
    }
  }

  private void displayTotalTimesAsUniqueVarAtCycleLaunchOnly() {
    total_set_time.setText(convertSeconds(totalCycleSetTimeInMillis/1000));
    total_break_time.setText(convertSeconds(totalCycleBreakTimeInMillis/1000));
  }

  private void displayTotalTdeeTimeAndCaloriesAsUniqueVarAtCycleLaunchOnly() {
    if (cycleHasActivityAssigned) {
      String elapsedTdeeTimeString = convertSeconds(totalTdeeActivityTime/1000);

      actvitiyStatsInTimerTextView.setText(getString(R.string.tdee_activity_in_timer_stats, getTdeeActivityStringFromSavedArrayPosition(), elapsedTdeeTimeString, formatCalorieString(burnedCaloriesInAllLoadingsOfCycle)));
    }
  }

  private void addAndRoundDownTotalCycleTimes() {
    if (mode==1) {
      switch (typeOfRound.get(currentRound)) {
        case 1: case 2:
          totalCycleSetTimeInMillis += cycleSetTimeForSingleRoundInMillis + 100;
          totalCycleSetTimeInMillis = (totalCycleSetTimeInMillis/1000) * 1000;
          cycleSetTimeForSingleRoundInMillis = 0;
          break;
        case 3: case 4:
          totalCycleBreakTimeInMillis += cycleBreakTimeForSingleRoundInMillis + 100;
          totalCycleBreakTimeInMillis = (totalCycleBreakTimeInMillis / 1000) * 1000;
          cycleBreakTimeForSingleRoundInMillis = 0;
          break;
      }
    }
    if (mode==3) {
      switch (pomDotCounter) {
        case 0: case 2: case 4: case 6:
          totalCycleSetTimeInMillis += cycleSetTimeForSingleRoundInMillis + 100;
          totalCycleSetTimeInMillis = (totalCycleSetTimeInMillis/1000) * 1000;
          cycleSetTimeForSingleRoundInMillis = 0;
          break;
        case 1: case 3: case 5: case 7:
          totalCycleBreakTimeInMillis += cycleBreakTimeForSingleRoundInMillis + 100;
          totalCycleBreakTimeInMillis = (totalCycleBreakTimeInMillis/1000) * 1000;
          cycleBreakTimeForSingleRoundInMillis = 0;
          break;
      }
    }
  }

  private void addAndRoundDownTdeeTimeAndTotalCalories() {
    if (mode==1 && cycleHasActivityAssigned) {
      totalTdeeActivityTime += singleInstanceTdeeActivityTime + 100;
      totalTdeeActivityTime = (totalTdeeActivityTime/1000) * 1000;
      burnedCaloriesInAllLoadingsOfCycle += burnedCaloriesInCurrentLoadingOfCycle;
    }
  }

  private void saveTotalSetAndBreakTimes() {
    switch (mode) {
      case 1:
        int setTimeToSave = totalValueAddedToSingleValueAndDividedBy1000ToInteger(totalCycleSetTimeInMillis, cycleSetTimeForSingleRoundInMillis);
        int breakTimeToSave = totalValueAddedToSingleValueAndDividedBy1000ToInteger(totalCycleBreakTimeInMillis, cycleBreakTimeForSingleRoundInMillis);
        cycles.setTotalSetTime(setTimeToSave);
        cycles.setTotalBreakTime(breakTimeToSave);
        cycles.setCyclesCompleted(cyclesCompleted);

        cyclesDatabase.cyclesDao().updateCycles(cycles);
        break;
      case 3:
        int workTimeToSave = totalValueAddedToSingleValueAndDividedBy1000ToInteger(totalCycleSetTimeInMillis, cycleSetTimeForSingleRoundInMillis);
        breakTimeToSave = totalValueAddedToSingleValueAndDividedBy1000ToInteger(totalCycleBreakTimeInMillis, cycleBreakTimeForSingleRoundInMillis);

        pomCycles.setTotalWorkTime(workTimeToSave);
        pomCycles.setTotalBreakTime(breakTimeToSave);
        pomCycles.setCyclesCompleted(cyclesCompleted);
        cyclesDatabase.cyclesDao().updatePomCycles(pomCycles);
        break;
    }
  }

  private int totalValueAddedToSingleValueAndDividedBy1000ToInteger(long totalVar, long singleVar) {
    return (int) (totalVar += singleVar) / 1000;
  }

  private void saveTotalTdeeTimeAndCalories() {
    if (cycleHasActivityAssigned) {
      int tdeeTimeToSave = totalValueAddedToSingleValueAndDividedBy1000ToInteger(totalTdeeActivityTime, singleInstanceTdeeActivityTime);
      double caloriesBurnedToSave = burnedCaloriesInAllLoadingsOfCycle + burnedCaloriesInCurrentLoadingOfCycle;

      cycles.setTotalTdeeActivityTimeElapsed(tdeeTimeToSave);
      cycles.setTotalCaloriesBurned(caloriesBurnedToSave);

      cyclesDatabase.cyclesDao().updateCycles(cycles);
    }
  }

  private void pauseAndResumeTimer(int pausing) {
    if (!timerDisabled) {
      if (fadeInObj != null) fadeInObj.cancel();
      if (fadeOutObj != null) fadeOutObj.cancel();
      switch (mode) {
        case 1:
          if (!timerEnded) {
            if (pausing == PAUSING_TIMER) {
              timerIsPaused = true;
              reset.setVisibility(View.VISIBLE);
              if (timer != null) timer.cancel();
              if (objectAnimator != null) objectAnimator.pause();

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
              //Ensures any non-reset timer lets this be true.
              activeCycle = true;
              timerIsPaused = false;
              reset.setVisibility(View.INVISIBLE);

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
          } else resetTimer();
          break;
        case 3:
          if (reset.getText().equals(getString(R.string.confirm_cycle_reset)))
            reset.setText(R.string.reset);
          if (!timerEnded) {
            if (pausing == PAUSING_TIMER) {
              timerIsPaused = true;
              pomMillisUntilFinished = pomMillis;
              if (objectAnimatorPom != null) objectAnimatorPom.pause();
              if (timer != null) timer.cancel();
              reset.setVisibility(View.VISIBLE);
            } else if (pausing == RESUMING_TIMER) {
              if (!activeCycle) activeCycle = true;

              reset.setVisibility(View.INVISIBLE);
              timerIsPaused = false;
              startObjectAnimatorAndTotalCycleTimeCounters();
              startPomTimer();
            }
            AsyncTask.execute(globalSaveTotalTimesAndCaloriesInDatabaseRunnable);
          } else resetTimer();
          break;
        case 4:
          if (pausing == RESUMING_TIMER) {
            stopWatchstartTime = System.currentTimeMillis();

            if (fadeInObj != null) fadeInObj.cancel();
            reset.setVisibility(View.INVISIBLE);
            stopWatchIsPaused = false;
            new_lap.setAlpha(1.0f);
            new_lap.setEnabled(true);
            mHandler.post(stopWatchRunnable);
          } else if (pausing == PAUSING_TIMER) {
            stopWatchTotalTime = stopWatchTotalTime + (long) stopWatchSeconds;
            stopWatchTotalTimeHolder = stopWatchTotalTime;

            reset.setVisibility(View.VISIBLE);
            mHandler.removeCallbacks(stopWatchRunnable);
            stopWatchIsPaused = true;
            new_lap.setAlpha(0.3f);
            new_lap.setEnabled(false);
            break;
          }
      }
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

        total_set_header.setText(R.string.total_sets);
        currentProgressBarValue = sharedPreferences.getInt("savedProgressBarValueForModeOne", 0);
        timeLeftValueHolder = sharedPreferences.getString("timeLeftValueForModeOne", "");
        positionOfSelectedCycle = sharedPreferences.getInt("positionOfSelectedCycleForModeOne", 0);
        timerIsPaused = sharedPreferences.getBoolean("modeOneTimerPaused", false);
        timerEnded = sharedPreferences.getBoolean("modeOneTimerEnded", false);
        timerDisabled = sharedPreferences.getBoolean("modeOneTimerDisabled", false);
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
        total_set_header.setText(R.string.total_work);
        currentProgressBarValue = sharedPreferences.getInt("savedProgressBarValueForModeThree", 0);
        timeLeftValueHolder = sharedPreferences.getString("timeLeftValueForModeThree", "");
        positionOfSelectedCycle = sharedPreferences.getInt("positionOfSelectedCycleForModeThree", 0);
        timerIsPaused = sharedPreferences.getBoolean("modeThreeTimerPaused", false);
        timerEnded = sharedPreferences.getBoolean("modeThreeTimerEnded", false);
        timerDisabled = sharedPreferences.getBoolean("modeThreeTimerDisabled", false);
        break;
    }
  }

  private void populateTimerUI() {
    lapListCanvas.setMode(mode);
    beginTimerForNextRound = true;
    cycles_completed.setText(R.string.cycles_done);
    cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(cyclesCompleted)));
    if (cycleTitle.isEmpty()) {
      cycleTitle = date;
    }
    cycle_title_textView.setText(cycleTitle);

    dotDraws.resetDotAlpha();
    cycle_title_textView.setVisibility(View.VISIBLE);
    total_set_header.setVisibility(View.VISIBLE);
    total_set_time.setVisibility(View.VISIBLE);
    total_break_header.setVisibility(View.VISIBLE);
    total_break_time.setVisibility(View.VISIBLE);
    lapRecycler.setVisibility(View.INVISIBLE);
    next_round.setVisibility(View.VISIBLE);
    reset_total_times.setVisibility(View.VISIBLE);
    new_lap.setVisibility(View.INVISIBLE);
    msTime.setVisibility(View.INVISIBLE);
    blankCanvas.setVisibility(View.GONE);
    addTDEEActivityTextView.setVisibility(View.VISIBLE);
    cycleTitleLayoutParams.topMargin = convertDensityPixelsToScalable(30);
    completedLapsLayoutParams.topMargin = convertDensityPixelsToScalable(24);

    timerDurationPlaceHolder = 0;
    tdeeActivityTimeDurationPlaceHolder = 0;

    switch (mode) {
      case 1:
        for (int i=0; i<workoutTime.size(); i++) {
          if (typeOfRound.get(i)==2 || typeOfRound.get(i)==4) workoutTime.set(i, 0);
        }

        startRounds = workoutTime.size();
        numberOfRoundsLeft = startRounds;

        dotDraws.updateWorkoutTimes(workoutTime, typeOfRound);
        dotDraws.updateWorkoutRoundCount(startRounds, numberOfRoundsLeft);

        if (workoutTime.size()>0) {
          switch (typeOfRound.get(0)) {
            case 1:
              setMillis = workoutTime.get(0);
              timeLeft.setText(convertSeconds((setMillis + 999) / 1000));
              setInitialTextSizeForRounds(setMillis);
              break;
            case 3:
              breakMillis = workoutTime.get(0);
              timeLeft.setText(convertSeconds((breakMillis + 999) / 1000));
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
          toggleTdeeTextViewVisibility();
        }
        break;
      case 3:
        if (pomValuesTime.size() > 0) {
          pomMillis = pomValuesTime.get(0);
          timeLeft.setText(convertSeconds((pomMillis + 999) / 1000));
          dotDraws.pomDraw(pomDotCounter,pomValuesTime);
          setInitialTextSizeForRounds(pomMillis);
        }
        break;
      case 4:
        progressBar.setProgress(maxProgress);
        timeLeft.setVisibility(View.VISIBLE);
        timeLeft.setText(displayTime);
        msTime.setText(displayMs);
        cycles_completed.setText(R.string.laps_completed);
        cycles_completed.setText(getString(R.string.laps_completed, String.valueOf(lapsNumber)));
        //Views for stopwatch.
        total_set_header.setVisibility(View.INVISIBLE);
        total_set_time.setVisibility(View.INVISIBLE);
        total_break_header.setVisibility(View.INVISIBLE);
        total_break_time.setVisibility(View.INVISIBLE);
        lapRecycler.setVisibility(View.VISIBLE);
        next_round.setVisibility(View.INVISIBLE);
        reset_total_times.setVisibility(View.GONE);
        new_lap.setVisibility(View.VISIBLE);
        msTime.setVisibility(View.VISIBLE);
        setInitialTextSizeForRounds(0);
        completedLapsLayoutParams.topMargin = 0;
        cycleTitleLayoutParams.topMargin = -25;

        timerDisabled = false;
        cycle_title_textView.setVisibility(View.INVISIBLE);
        addTDEEActivityTextView.setVisibility(View.INVISIBLE);
        if (stopWatchIsPaused) reset.setVisibility(View.VISIBLE); else reset.setVisibility(View.INVISIBLE);
        break;
    }
  }

  private void resetTimer() {
    activeCycle = false;
    vibrator.cancel();
    dotDraws.resetDotAlpha();
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

    reset.setVisibility(View.INVISIBLE);

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
      case 4:
        stopWatchstartTime = 0;
        stopWatchTotalTime = 0;
        stopWatchTotalTimeHolder = 0;

        stopWatchMs = 0;
        stopWatchSeconds = 0;
        stopWatchMinutes = 0;

        timeLeft.setAlpha(1);
        timeLeft.setText("0");
        msTime.setText("00");
        if (currentLapList.size() > 0) currentLapList.clear();
        if (savedLapList.size() > 0) savedLapList.clear();
        lapsNumber = 0;
        cycles_completed.setText(getString(R.string.laps_completed, "0"));

        stopWatchIsPaused = true;
        lapAdapter.notifyDataSetChanged();
        empty_laps.setVisibility(View.VISIBLE);
        setInitialTextSizeForRounds(0);
        break;
    }
    if (mode!=4) populateTimerUI();
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

  private void toggleExistenceOfTdeeActivity(boolean activityExists) {
    if (activityExists) {
      String activity = (String) tdee_sub_category_spinner.getSelectedItem();
      addTDEEActivityTextView.setText(activity);
      cycleHasActivityAssigned = true;
      removeTdeeActivityImageView.setVisibility(View.VISIBLE);
    } else {
      addTDEEActivityTextView.setText(R.string.add_activity);
      cycleHasActivityAssigned = false;
      removeTdeeActivityImageView.setVisibility(View.INVISIBLE);
    }
  }

  private String getTdeeActivityStringFromSavedArrayPosition() {
    ArrayList<String[]> subCategoryArray = tDEEChosenActivitySpinnerValues.subCategoryListOfStringArrays;
    String[] subCategoryList = subCategoryArray.get(selectedTdeeCategoryPosition);
    return (String) subCategoryList[selectedTdeeSubCategoryPosition];
  }

  private String formatCalorieString(double calories) {
    DecimalFormat df = new DecimalFormat("#.#");
    return df.format(calories);
  }

  private double calculateCaloriesBurnedPerSecond() {
    return calculateCaloriesBurnedPerMinute(metScore) / 60;
  }

  private double calculateCaloriesBurnedPerMinute(double metValue) {
    double weightConversion = userWeight;
    if (!isMetric) weightConversion = weightConversion / 2.205;
    double caloriesBurnedPerMinute = (metValue * 3.5 * weightConversion) / 200;
    return caloriesBurnedPerMinute;
  }

  private void setNumberOfCaloriesBurnedPerMinuteInActivityAdditionPopUpTextView() {
    double caloriesBurnedPerMinute = calculateCaloriesBurnedPerMinute(metScore);
    DecimalFormat df = new DecimalFormat("#.#");
    String truncatedMinutes = df.format(caloriesBurnedPerMinute);
    caloriesBurnedInTdeeAdditionTextView.setText(getString(R.string.calories_burned_per_minute, truncatedMinutes));
  }

  private void setTdeeSubCategorySpinnerListFromRetrievedDatabaseValues() {
    tdeeSubCategoryAdapter.clear();
    tdeeSubCategoryAdapter.addAll(tDEEChosenActivitySpinnerValues.subCategoryListOfStringArrays.get(selectedTdeeCategoryPosition));
  }

  private void tdeeCategorySpinnerTouchActions() {
    selectedTdeeCategoryPosition = tdee_category_spinner.getSelectedItemPosition();
    tdeeSubCategoryAdapter.clear();
    tdeeSubCategoryAdapter.addAll(tDEEChosenActivitySpinnerValues.subCategoryListOfStringArrays.get(selectedTdeeCategoryPosition));

    tdee_sub_category_spinner.setSelection(0);

    metScore = retrieveMetScoreFromSubCategoryPosition();
    metScoreTextView.setText(getString(R.string.met_score, metScore));

    logTdeeCategoryPositions();
    logTdeeCategoryArraysSelected();
  }

  private void tdeeSubCategorySpinnerTouchActions() {
    selectedTdeeCategoryPosition = tdee_category_spinner.getSelectedItemPosition();

    selectedTdeeSubCategoryPosition = tdee_sub_category_spinner.getSelectedItemPosition();
    selectedTdeeValuePosition = selectedTdeeSubCategoryPosition;

    metScore = retrieveMetScoreFromSubCategoryPosition();
    metScoreTextView.setText(getString(R.string.met_score, metScore));

    logTdeeCategoryPositions();
    logTdeeCategoryArraysSelected();
  }

  private double retrieveMetScoreFromSubCategoryPosition() {
    String[] valueArray = tDEEChosenActivitySpinnerValues.subValueListOfStringArrays.get(selectedTdeeCategoryPosition);
    double preRoundedMet = Double.parseDouble(valueArray[selectedTdeeValuePosition]);
    return preRoundedMet;
  }


  private void logTdeeCategoryPositions() {
    Log.i("testRetrieve", "cat position is " + selectedTdeeCategoryPosition);
    Log.i("testRetrieve", "sub cat position " + selectedTdeeSubCategoryPosition);
    Log.i("testRetrieve", "value array position " + selectedTdeeValuePosition);
  }

  private void logTdeeCategoryArraysSelected() {
    ArrayList<String[]> valueArrayList = tDEEChosenActivitySpinnerValues.subValueListOfStringArrays;
    String[] selected = valueArrayList.get(selectedTdeeCategoryPosition);

    Log.i("testRetrieve", "value ARRAY LIST is " + Arrays.toString(selected));
  }

  private void logTdeeArraySizes() {
    for (int i=0; i<tDEEChosenActivitySpinnerValues.subCategoryListOfStringArrays.size(); i++) {
      String[] arrayToTest = tDEEChosenActivitySpinnerValues.subCategoryListOfStringArrays.get(i);
      Log.i("testTdeeArraySizes", "size of " + i + " is " + arrayToTest.length);
    }
  }

  ///////////Test methods///////////////////////////
  private void logTotalSetTimes() {
    Log.i("testTimes", "single set millis is " + cycleSetTimeForSingleRoundInMillis);
    Log.i("testTimes", "total set millis is " + totalCycleSetTimeInMillis);
  }

  private void logTotalBreakTimes() {
    Log.i("testTimes", "single break millis is " + cycleBreakTimeForSingleRoundInMillis);
    Log.i("testTimes", "total break millis is " + totalCycleBreakTimeInMillis);
  }

  private void logTotalTdeeTimes() {
    Log.i("testTimes", "single tdee millis is " + singleInstanceTdeeActivityTime);
    Log.i("testTimes", "total tdee millis is " + totalTdeeActivityTime);
  }
}