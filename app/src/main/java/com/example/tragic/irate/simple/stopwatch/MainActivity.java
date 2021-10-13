package com.example.tragic.irate.simple.stopwatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tragic.irate.simple.stopwatch.Database.Cycles;
import com.example.tragic.irate.simple.stopwatch.Database.CyclesBO;
import com.example.tragic.irate.simple.stopwatch.Database.CyclesDatabase;
import com.example.tragic.irate.simple.stopwatch.Database.PomCycles;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

@SuppressWarnings({"depreciation"})
public class MainActivity extends AppCompatActivity implements SavedCycleAdapter.onCycleClickListener, SavedCycleAdapter.onHighlightListener, SavedPomCycleAdapter.onCycleClickListener, SavedPomCycleAdapter.onHighlightListener, CycleRoundsAdapter.onFadeFinished, CycleRoundsAdapterTwo.onFadeFinished, CycleRoundsAdapter.onRoundSelected, CycleRoundsAdapterTwo.onRoundSelected, DotDraws.sendAlpha, SavedCycleAdapter.onResumeOrResetCycle, SavedPomCycleAdapter.onResumeOrResetCycle {

  ConstraintLayout cl;
  SharedPreferences sharedPreferences;
  SharedPreferences.Editor prefEdit;
  View tabView;
  View mainView;
  Gson gson;
  BlankCanvas blankCanvas;
  Calendar calendar;
  SimpleDateFormat simpleDateFormat;
  String date;

  ImageButton fab;
  ImageButton stopwatch;
  ImageView sortCheckMark;
  TextView emptyCycleList;

  CyclesDatabase cyclesDatabase;
  Cycles cycles;
  CyclesBO cyclesBO;
  PomCycles pomCycles;
  List<Cycles> cyclesList;
  List<CyclesBO> cyclesBOList;
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
  View deleteCyclePopupView;
  View sortCyclePopupView;
  View savedCyclePopupView;
  View editCyclesPopupView;
  View settingsPopupView;

  PopupWindow sortPopupWindow;
  PopupWindow savedCyclePopupWindow;
  PopupWindow deleteCyclePopupWindow;
  PopupWindow editCyclesPopupWindow;
  PopupWindow settingsPopupWindow;

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
  ImageView global_settings;
  TextView save_edit_cycle;
  TextView delete_edit_cycle;

  int mode = 1;
  int savedMode = 1;
  int sortMode = 1;
  int sortModePom = 1;
  int sortHolder = 1;
  int positionOfSelectedCycle;
  String cycleTitle = "";
  List<String> receivedHighlightPositions;

  TextView cycleNameText;
  EditText cycleNameEdit;
  TextView s1;
  TextView s2;
  TextView s3;
  EditText firstRowEditMinutes;
  EditText firstRowEditSeconds;
  TextView firstRowEditColon;
  TextView firstRowTextView;
  EditText secondRowEditMinutes;
  EditText secondRowEditSeconds;
  TextView secondRowEditColon;
  TextView secondRowTextView;
  EditText thirdRowEditMinutes;
  EditText thirdRowEditSeconds;
  TextView thirdRowEditColon;
  TextView thirdRowEditTextView;
  ImageView firstRowAddButton;
  ImageView firstRowSubtractButton;
  ImageView secondRowAddButton;
  ImageView secondRowSubtractButton;
  ImageButton thirdRowAddButton;
  ImageButton thirdRowSubtractButton;
  Button addRoundToCycleButton;
  Button SubtractRoundFromCycleButton;
  ImageView toggleInfinityRoundsForSets;
  ImageView toggleInfinityRoundsForBreaks;
  View anchorViewForEditRows;
  ImageButton buttonToLaunchTimer;

  ArrayList<Integer> workoutTime;
  ArrayList<String> convertedWorkoutTime;
  ArrayList<String> workoutCyclesArray;
  ArrayList<String> workoutTitle;
  ArrayList<String> workoutTitleArray;
  ArrayList<Integer> pomValuesTime;
  ArrayList<String> convertedPomList;
  ArrayList<String> pomTitleArray;
  ArrayList<String> pomArray;
  String workoutString;
  String roundTypeString;
  String pomString;
  int ADDING_CYCLE = 1;
  int EDITING_CYCLE = 2;
  int DELETING_CYCLE = 3;

  ArrayList<Integer> typeOfRound;
  ArrayList<String> typeOfRoundArray;
  boolean setsSelected = true;
  boolean breaksSelected;
  int roundType;
  ArrayList<String> roundHolderOne;
  ArrayList<String> roundHolderTwo;
  ArrayList<Integer> typeHolderOne;
  ArrayList<Integer> typeHolderTwo;

  int setValue;
  int breakValue;
  int pomValue1;
  int pomValue2;
  int pomValue3;
  //Edits must be longs because we divide w/ remainders on them.
  long editSetSeconds;
  long editSetMinutes;
  long editBreakMinutes;
  long editBreakSeconds;
  long editPomMinutesOne;
  long editPomSecondsOne;
  long editPomMinutesTwo;
  long editPomSecondsTwo;
  long editPomMinutesThree;
  long editPomSecondsThree;
  boolean incrementValues;
  int incrementTimer = 10;
  boolean minReached;
  boolean maxReached;
  float editTextViewPosX;
  boolean firstRowHighlighted;
  boolean secondRowHighlighted;

  Handler mHandler;
  Runnable valueSpeed;
  Runnable changeFirstValue;
  Runnable changeSecondValue;
  Runnable changeThirdValue;
  Runnable adjustRoundDelay;
  boolean activeEditListener;
  boolean currentlyEditingACycle;
  InputMethodManager inputMethodManager;

  AlphaAnimation fadeIn;
  AlphaAnimation fadeOut;
  View roundView;
  TextView round_count;
  TextView round_value;
  ConstraintLayout.LayoutParams recyclerLayoutOne;
  ConstraintLayout.LayoutParams recyclerLayoutTwo;
  ConstraintLayout.LayoutParams cycle_title_layout;
  ConstraintLayout.LayoutParams completedLapsParam;
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

  long currentProgressBarValueForInfinityRounds;
  long pomMillis;

  int maxProgress = 10000;
  int currentProgressBarValue = 10000;
  long setMillisUntilFinished;
  long breakMillisUntilFinished;
  long pomMillisUntilFinished;
  Runnable endFade;
  int pomDotCounter;

  double ms;
  double msConvert;
  double msConvert2;
  double msDisplay;
  double seconds;
  double minutes;
  double msReset;
  String newMs;
  String savedMs;
  String displayMs = "00";
  String displayTime = "0";
  String newEntries;
  String savedEntries;
  int newMsConvert;
  int savedMsConvert;
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
  LinearLayoutManager lapLayout;
  ConstraintLayout roundRecyclerLayout;

  DotDraws dotDraws;
  LapListCanvas lapListCanvas;
  ValueAnimator sizeAnimator;
  ValueAnimator valueAnimatorDown;
  ValueAnimator valueAnimatorUp;
  AlphaAnimation fadeProgressIn;
  AlphaAnimation fadeProgressOut;
  boolean textSizeIncreased;
  //Always true initially, since infinity mode starts at 0.
  boolean textSizeReduced = true;

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

  long countUpMillisSets;
  long countUpMillisBreaks;
  public Runnable infinityRunnableForSets;
  public Runnable infinityRunnableForBreaks;
  public Runnable postRoundRunnableForFirstMode;
  public Runnable postRoundRunnableForThirdMode;

  long defaultProgressBarDurationForInfinityRounds;
  long countUpMillisHolder;
  boolean makeCycleAdapterVisible;
  boolean beginTimerForNextRound;

  Runnable updateTotalTimesInDatabaseRunnable;
  Runnable queryAllCyclesFromDatabaseRunnableAndRetrieveTotalTimes;
  Runnable queryAndSortAllCyclesFromDatabaseRunnable;
  Runnable deleteTotalCycleTimesASyncRunnable;
  Runnable deleteSingleCycleASyncRunnable;
  Runnable deleteAllCyclesASyncRunnable;
  Runnable saveCyclesASyncRunnable;
  Runnable retrieveTotalCycleTimesFromDatabaseObjectRunnable;

  NotificationManagerCompat notificationManagerCompat;
  Notification.Builder builder;
  static boolean notificationDismissed = true;

  //Todo: Have notifications display 0 once round ends.
  //Todo: Add Pause/Reset buttons to notification menu.
  //Todo: Need diff. String returns/math for infinity rounds.
  //Todo: Diff. math needed for Pom roundsLeft in notifications.
  //Todo: Pom total times not working.
  //Todo: Use 0:00 for <60 second total times.
  //Todo: Selecting and de-selecting a specific round to replace still tries to replace old selection.
  //Todo: Blank title for first cycle creation on app launch bug is back.
  //Todo: 0/0 index exception on emulator when (1) Start Workout timer, (2) Start Pom Timer, (3) Try to resume Workout timer.
  //Todo: Spinners or right-to-left time population for creating timers (like Google's).
  //Todo: The different positioning in sort resolves once the popUp is shown.
  //Todo: Dotdraws will need sp -> dp for scale sizing.
  //Todo: Drop-down functionality for cycles when app is minimized (like Google's).
  //Todo: Color schemes.
  //Todo: More stats? E.g. total sets/breaks, total partial sets/breaks, etc./
  //Todo: Some other indication in edit mode that a cycle is part of db and not new (just an "editing" notation would work).
  //Todo: Use empty view space in edit mode for cycle stats (e.g. rounds completed, total times, etc.).
  //Todo: Add fade/ripple effects to buttons and other stuff that would like it.
  //Todo: Option to set "base" progressBar for count-up (options section in menu?). Simply change currentProgressBarValueForInfinityRounds.
  //Todo: Save total sets/breaks and completed by day option?
  //Todo: Infinity mode for Pom?
  //Todo: We should put any index fetches inside conditionals, BUT make sure nothing (i.e. Timer popup) launches unless those values are fetched.

  //Todo: editText round box diff. sizes in emulator. Need to work on layout in general.
  //Todo: Could long svg files be a lag contributor?
  //Todo: TDEE in sep popup w/ tabs.
  //Todo: Make sure sort checkmark positions work on different size screens.
  //Todo: Add taskbar notification for timers. Add vibrations.
  //Todo: Add color scheme options.
  //Todo: Rename app, of course.
  //Todo: Add onOptionsSelected dots for About, etc.
  //Todo: Repository for db. Look at Executor/other alternate thread methods. Would be MUCH more streamlined on all db calls, but might also bork order of operations when we need to call other stuff under UI thread right after.
  //Todo: Make sure number pad is dismissed when switching to stopwatch mode.
  //Todo: IMPORTANT: Resolve landscape mode vs. portrait. Set to portrait-only in manifest at present. Likely need a second layout for landscape mode. Also check that lifecycle is stable when re-orienting.
  //Todo: Test layouts w/ emulator.
  //Todo: Test everything 10x.

   //Todo: REMINDER, Try next app w/ Kotlin.

  //Todo: Dismissing Timer popUp (i.e. backPressed), auto pauses timer at moment, so notification object will not be updated.
  @Override
  public void onResume() {
    super.onResume();
    setVisible(true);
    notificationDismissed = true;
    notificationManagerCompat.cancel(1);
  }

  @Override
  public void onPause() {
    super.onPause();
    setVisible(false);
    //Todo: This!
    notificationDismissed = false;
    setNotificationValues();
  }

  @Override
  public void onBackPressed() {
    //Used to minimize activity. Will only be called if no popUps have focus.
    moveTaskToBack(false);
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

  //Gets the position clicked on from our saved cycle adapter.
  @Override
  public void onCycleClick(int position) {
    //Active cycle option will automatically be removed if accessing new cycle.
    if (mode==1) savedCycleAdapter.removeActiveCycleLayout();
    if (mode==3) savedPomCycleAdapter.removeActiveCycleLayout();
    isNewCycle = false;
    positionOfSelectedCycle = position;
    //Retrieves timer value lists from cycle adapter list by parsing its Strings, rather than querying database.
    populateRoundList();
    //If clicking on a cycle to launch, it will always be an existing one, and we do not want to call a save method since it is unedited.
    launchTimerCycle(false);
    resetTimer();
  }

  //Receives highlighted positions from our adapter.
  @Override
  public void onCycleHighlight(List<String> listOfPositions, boolean addButtons) {
    //Receives list of cycle positions highlighted.
    receivedHighlightPositions = listOfPositions;
    //Sets "highlight mode" actionBar buttons to Visible if entering mode (i.e. selecting first item).
    if (addButtons) {
      //If at least one item in our cycle list, enable the edit button.
      edit_highlighted_cycle.setEnabled(listOfPositions.size() <= 1);
      //Fading app name text + sort button out, edit and delete buttons in.
      fadeEditCycleButtonsIn(FADE_IN_HIGHLIGHT_MODE);
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
        roundHolderOne.clear();
        typeHolderOne.clear();
        roundHolderTwo.clear();
        typeHolderTwo.clear();
        for (int i=0; i<workoutTime.size(); i++) {
          if (i<=7) {
            roundHolderOne.add(convertedWorkoutTime.get(i));
            typeHolderOne.add(typeOfRound.get(i));
          } else {
            roundHolderTwo.add(convertedWorkoutTime.get(i));
            typeHolderTwo.add(typeOfRound.get(i));
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
      convertedPomList.clear();
      cycleRoundsAdapter.notifyDataSetChanged();
      cycleRoundsAdapter.disablePomFade();
      SubtractRoundFromCycleButton.setClickable(true);
    }
  }

  //This callback method works for both round adapters.
  //Receives position of selected round from adapter.
  @Override
  public void roundSelected(int position) {
    roundIsSelected = true;
    roundSelectedPosition = position;
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
          case R.id.delete_all_cycles:
            if (mode==1 && workoutCyclesArray.size()==0 || mode==3 && pomArray.size()==0) {
              Toast.makeText(getApplicationContext(), "No cycles to delete!", Toast.LENGTH_SHORT).show();
            } else {
              delete_all_text.setText(R.string.delete_all_cycles);
              deleteCyclePopupWindow.showAtLocation(cl, Gravity.CENTER, 0, 0);
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
    mainView = findViewById(R.id.main_layout);
    tabView = findViewById(R.id.tabLayout);

    TabLayout tabLayout = findViewById(R.id.tabLayout);
    tabLayout.addTab(tabLayout.newTab().setText("Workouts"));
    tabLayout.addTab(tabLayout.newTab().setText("Pomodoro"));

    gson = new Gson();
    fab = findViewById(R.id.fab);
    stopwatch = findViewById(R.id.stopwatch_button);
    emptyCycleList = findViewById(R.id.empty_cycle_list);
    savedCycleRecycler = findViewById(R.id.cycle_list_recycler);
    savedPomCycleRecycler = findViewById(R.id.pom_list_recycler);
    blankCanvas = findViewById(R.id.blank_canvas);
    blankCanvas.setVisibility(View.GONE);

    calendar = Calendar.getInstance();
    simpleDateFormat = new SimpleDateFormat("EEE, MMMM d yyyy - hh:mma", Locale.getDefault());
    simpleDateFormat.format(calendar.getTime());
    date = simpleDateFormat.format(calendar.getTime());

    LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    savedCyclePopupView = inflater.inflate(R.layout.saved_cycles_layout, null);
    deleteCyclePopupView = inflater.inflate(R.layout.delete_cycles_popup, null);
    sortCyclePopupView = inflater.inflate(R.layout.sort_popup, null);
    editCyclesPopupView = inflater.inflate(R.layout.editing_cycles, null);
    settingsPopupView = inflater.inflate(R.layout.sidebar_popup, null);
    timerPopUpView = inflater.inflate(R.layout.timer_popup, null);

    savedCyclePopupWindow = new PopupWindow(savedCyclePopupView, 800, 1200, true);
    deleteCyclePopupWindow = new PopupWindow(deleteCyclePopupView, 750, 375, true);
    sortPopupWindow = new PopupWindow(sortCyclePopupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
    editCyclesPopupWindow = new PopupWindow(editCyclesPopupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true);
    settingsPopupWindow = new PopupWindow(settingsPopupView, 600, 1540, true);
    timerPopUpWindow = new PopupWindow(timerPopUpView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true);

    savedCyclePopupWindow.setAnimationStyle(R.style.WindowAnimation);
    deleteCyclePopupWindow.setAnimationStyle(R.style.WindowAnimation);
    sortPopupWindow.setAnimationStyle(R.style.SlideTopAnimation);
    editCyclesPopupWindow.setAnimationStyle(R.style.WindowAnimation);
    settingsPopupWindow.setAnimationStyle(R.style.SlideLeftAnimation);
    timerPopUpWindow.setAnimationStyle(R.style.WindowAnimation);

    cl = findViewById(R.id.main_layout);
    roundView = inflater.inflate(R.layout.mode_one_rounds, null);
    round_count = roundView.findViewById(R.id.round_count);
    round_value = roundView.findViewById(R.id.workout_rounds);

    cycleNameText = editCyclesPopupView.findViewById(R.id.cycle_name_text);
    cycleNameEdit = editCyclesPopupView.findViewById(R.id.cycle_name_edit);
    s1 = editCyclesPopupView.findViewById(R.id.s1);
    s2 = editCyclesPopupView.findViewById(R.id.s2);
    s3 = editCyclesPopupView.findViewById(R.id.s3);
    firstRowEditMinutes = editCyclesPopupView.findViewById(R.id.firstRowEditMinutes);
    firstRowEditSeconds = editCyclesPopupView.findViewById(R.id.firstRowEditSeconds);
    firstRowEditColon = editCyclesPopupView.findViewById(R.id.firstRowEditColon);
    firstRowTextView = editCyclesPopupView.findViewById(R.id.firstRowTextView);
    secondRowEditMinutes = editCyclesPopupView.findViewById(R.id.secondRowEditMinutes);
    secondRowEditSeconds = editCyclesPopupView.findViewById(R.id.secondRowEditSeconds);
    secondRowEditColon = editCyclesPopupView.findViewById(R.id.secondRowEditColon);
    secondRowTextView = editCyclesPopupView.findViewById(R.id.secondRowTextView);
    thirdRowEditTextView = editCyclesPopupView.findViewById(R.id.thirdRowEditTextView);
    firstRowAddButton = editCyclesPopupView.findViewById(R.id.firstRowAddButton);
    firstRowSubtractButton = editCyclesPopupView.findViewById(R.id.firstRowSubtractButton);
    secondRowAddButton = editCyclesPopupView.findViewById(R.id.secondRowAddButton);
    secondRowSubtractButton = editCyclesPopupView.findViewById(R.id.secondRowSubtractButton);
    thirdRowAddButton = editCyclesPopupView.findViewById(R.id.thirdRowAddButton);
    thirdRowSubtractButton = editCyclesPopupView.findViewById(R.id.thirdRowSubtractButton);
    thirdRowEditMinutes = editCyclesPopupView.findViewById(R.id.thirdRowEditMinutes);
    thirdRowEditSeconds = editCyclesPopupView.findViewById(R.id.thirdRowEditSeconds);
    thirdRowEditColon = editCyclesPopupView.findViewById(R.id.thirdRowEditColon);
    addRoundToCycleButton = editCyclesPopupView.findViewById(R.id.addRoundToCycleButton);
    SubtractRoundFromCycleButton = editCyclesPopupView.findViewById(R.id.subtract_cycle);
    toggleInfinityRoundsForSets = editCyclesPopupView.findViewById(R.id.s1_up);
    toggleInfinityRoundsForBreaks = editCyclesPopupView.findViewById(R.id.s2_up);
    anchorViewForEditRows = editCyclesPopupView.findViewById(R.id.anchorViewForEditRows);
    buttonToLaunchTimer = editCyclesPopupView.findViewById(R.id.buttonToLaunchTimer);
    save_edit_cycle = editCyclesPopupView.findViewById(R.id.save_edit_cycle);
    delete_edit_cycle = editCyclesPopupView.findViewById(R.id.delete_edit_cycle);
    roundRecyclerLayout = editCyclesPopupView.findViewById(R.id.round_recycler_layout);
    toggleInfinityRoundsForSets.setAlpha(0.3f);
    toggleInfinityRoundsForBreaks.setAlpha(0.3f);

    sortAlphaStart = sortCyclePopupView.findViewById(R.id.sort_title_start);
    sortAlphaEnd = sortCyclePopupView.findViewById(R.id.sort_title_end);
    sortRecent = sortCyclePopupView.findViewById(R.id.sort_most_recent);
    sortNotRecent = sortCyclePopupView.findViewById(R.id.sort_least_recent);
    sortHigh = sortCyclePopupView.findViewById(R.id.sort_number_high);
    sortLow = sortCyclePopupView.findViewById(R.id.sort_number_low);
    sortCheckMark = sortCyclePopupView.findViewById(R.id.sortCheckMark);

    delete_all_confirm = deleteCyclePopupView.findViewById(R.id.confirm_yes);
    delete_all_cancel = deleteCyclePopupView.findViewById(R.id.confirm_no);
    delete_all_text = deleteCyclePopupView.findViewById(R.id.delete_text);

    //Custom Action Bar.
    getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    getSupportActionBar().setDisplayShowCustomEnabled(true);
    getSupportActionBar().setCustomView(R.layout.custom_bar);
    appHeader = findViewById(R.id.app_header);
    edit_highlighted_cycle = findViewById(R.id.edit_highlighted_cycle);
    delete_highlighted_cycle = findViewById(R.id.delete_highlighted_cycles);
    cancelHighlight = findViewById(R.id.cancel_highlight);
    sortButton = findViewById(R.id.sortButton);
    global_settings = findViewById(R.id.global_settings);
    edit_highlighted_cycle.setVisibility(View.INVISIBLE);
    delete_highlighted_cycle.setVisibility(View.INVISIBLE);
    cancelHighlight.setVisibility(View.INVISIBLE);
    cycleNameText.setVisibility(View.INVISIBLE);

    workoutTime = new ArrayList<>();
    convertedWorkoutTime = new ArrayList<>();
    workoutTitle = new ArrayList<>();
    workoutCyclesArray = new ArrayList<>();
    typeOfRound = new ArrayList<>();
    typeOfRoundArray = new ArrayList<>();

    roundHolderOne = new ArrayList<>();
    roundHolderTwo = new ArrayList<>();
    typeHolderOne = new ArrayList<>();
    typeHolderTwo = new ArrayList<>();

    pomValuesTime = new ArrayList<>();
    convertedPomList = new ArrayList<>();
    pomArray = new ArrayList<>();
    workoutTitleArray = new ArrayList<>();
    pomTitleArray = new ArrayList<>();
    //If highlighting cycles for deletion, contains all POSITIONS (not IDs) of cycles highlighted.
    receivedHighlightPositions = new ArrayList<>();
    //Database entity lists.
    cyclesList = new ArrayList<>();
    cyclesBOList = new ArrayList<>();
    pomCyclesList = new ArrayList<>();
    cycles = new Cycles();
    cyclesBO = new CyclesBO();
    pomCycles = new PomCycles();

    mHandler = new Handler();
    inputMethodManager =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    sharedPreferences = getApplicationContext().getSharedPreferences("pref", 0);
    prefEdit = sharedPreferences.edit();

    //Default values in WHOLE seconds. Multiplied * 1000 for our millis values.
    setValue = sharedPreferences.getInt("setValue", 30);
    breakValue = sharedPreferences.getInt("breakValue", 30);
    pomValue1 = sharedPreferences.getInt("pomValue1", 1500);
    pomValue2 = sharedPreferences.getInt("pomValue2", 300);
    pomValue3 = sharedPreferences.getInt("pomValue3", 900);
    sortMode = sharedPreferences.getInt("sortMode", 1);
    sortModePom = sharedPreferences.getInt("sortModePom", 1);

    sortHolder = sortMode;
    int checkMarkPosition = sharedPreferences.getInt("checkMarkPosition", 0);
    sortCheckMark.setY(checkMarkPosition);

    fadeIn = new AlphaAnimation(0.0f, 1.0f);
    fadeOut = new AlphaAnimation(1.0f, 0.0f);
    fadeIn.setDuration(750);
    fadeOut.setDuration(750);
    fadeIn.setFillAfter(true);
    fadeOut.setFillAfter(true);

    mHandler = new Handler();

    valueAnimatorDown = new ValueAnimator().ofFloat(90f, 70f);
    valueAnimatorUp = new ValueAnimator().ofFloat(70f, 90f);
    valueAnimatorDown.setDuration(2000);
    valueAnimatorUp.setDuration(2000);

    customSetTime = new ArrayList<>();
    customBreakTime = new ArrayList<>();
    breaksOnlyTime = new ArrayList<>();
    currentLapList = new ArrayList<>();
    savedLapList = new ArrayList<>();

    zeroArraySets = new ArrayList<>();
    zeroArrayBreaks = new ArrayList<>();
    setsArray = new ArrayList<>();
    breaksArray = new ArrayList<>();
    breaksOnlyArray = new ArrayList<>();
    pomArray = new ArrayList<>();

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
    total_set_header.setText(R.string.total_sets);
    total_break_header.setText(R.string.total_breaks);
    total_set_time.setText("0");
    total_break_time.setText("0");

    progressBar = timerPopUpView.findViewById(R.id.progressBar);
    stopWatchView = timerPopUpView.findViewById(R.id.stopWatchView);
    timeLeft = timerPopUpView.findViewById(R.id.timeLeft);
    msTime = timerPopUpView.findViewById(R.id.msTime);
    dotDraws = timerPopUpView.findViewById(R.id.dotdraws);
    lapRecycler = timerPopUpView.findViewById(R.id.lap_recycler);
    pauseResumeButton = timerPopUpView.findViewById(R.id.pauseResumeButton);
    pauseResumeButton.setBackgroundColor(Color.argb(0, 0, 0, 0));
    pauseResumeButton.setRippleColor(null);
    reset_total_times = timerPopUpView.findViewById(R.id.reset_total_times);
    empty_laps = timerPopUpView.findViewById(R.id.empty_laps_text);
    if (mode!=4) empty_laps.setVisibility(View.INVISIBLE);
    objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", (int) maxProgress, 0);
    objectAnimatorPom = ObjectAnimator.ofInt(progressBar, "progress", (int) maxProgress, 0);

    cycle_title_layout = (ConstraintLayout.LayoutParams) cycle_title_textView.getLayoutParams();
    completedLapsParam = (ConstraintLayout.LayoutParams) cycles_completed.getLayoutParams();

    stopWatchView.setVisibility(View.GONE);
    savedPomCycleRecycler.setVisibility(View.GONE);
    lapRecycler.setVisibility(View.GONE);
    new_lap.setVisibility(View.INVISIBLE);

    cycles_completed.setText(R.string.cycles_done);

    fadeIn = new AlphaAnimation(0.0f, 1.0f);
    fadeOut = new AlphaAnimation(1.0f, 0.0f);
    fadeIn.setDuration(1000);
    fadeOut.setDuration(1000);
    fadeIn.setFillAfter(true);
    fadeOut.setFillAfter(true);

    fadeProgressIn = new AlphaAnimation(0.3f, 1.0f);
    fadeProgressOut = new AlphaAnimation(1.0f, 0.3f);
    fadeProgressIn.setDuration(300);
    fadeProgressOut.setDuration(300);

    //Adapter and Recycler for round views within our editCycles popUp.
    LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext());
    LinearLayoutManager lm2 = new LinearLayoutManager(getApplicationContext());
    LinearLayoutManager lm3 = new LinearLayoutManager(getApplicationContext());
    LinearLayoutManager lm4 = new LinearLayoutManager(getApplicationContext());

    instantiateNotifications();

    AsyncTask.execute(() -> {
      cyclesDatabase = CyclesDatabase.getDatabase(getApplicationContext());
      cyclesList = cyclesDatabase.cyclesDao().loadAllCycles();
      pomCyclesList = cyclesDatabase.cyclesDao().loadAllPomCycles();

      runOnUiThread(() -> {
        clearAndRepopulateCycleAdapterListsFromDatabaseObject(true);
        replaceCycleListWithEmptyTextViewIfNoCyclesExist();

        //Instantiates saved cycle adapter w/ ALL list values, to be populated based on the mode we're on.
        savedCycleAdapter = new SavedCycleAdapter(getApplicationContext(), workoutCyclesArray, typeOfRoundArray, workoutTitleArray);
        savedCycleRecycler.setAdapter(savedCycleAdapter);
        savedCycleRecycler.setLayoutManager(lm);
        //Instantiating callbacks from adapter.
        savedCycleAdapter.setItemClick(MainActivity.this);
        savedCycleAdapter.setHighlight(MainActivity.this);
        savedCycleAdapter.setResumeOrResetCycle(MainActivity.this);

        savedPomCycleAdapter = new SavedPomCycleAdapter(getApplicationContext(), pomArray, pomTitleArray);
        savedPomCycleRecycler.setAdapter(savedPomCycleAdapter);
        savedPomCycleRecycler.setLayoutManager(lm4);
        //Instantiating callbacks from adapter.
        savedPomCycleAdapter.setItemClick(MainActivity.this);
        savedPomCycleAdapter.setHighlight(MainActivity.this);
        savedPomCycleAdapter.setResumeOrResetCycle(MainActivity.this);

        //Calling this by default, so any launch of Main will update our cycle list, since populateCycleList(), called after adapter is instantiated, is what populates our arrays.
        savedCycleAdapter.notifyDataSetChanged();
      });
    });

    //Our two cycle round adapters.
    cycleRoundsAdapter = new CycleRoundsAdapter(getApplicationContext(), roundHolderOne, typeHolderOne, convertedPomList);
    cycleRoundsAdapterTwo = new CycleRoundsAdapterTwo(getApplicationContext(), roundHolderTwo, typeHolderTwo);
    cycleRoundsAdapter.fadeFinished(MainActivity.this);
    cycleRoundsAdapterTwo.fadeFinished(MainActivity.this);
    cycleRoundsAdapter.selectedRound(MainActivity.this);
    cycleRoundsAdapterTwo.selectedRound(MainActivity.this);
    //Only first adapter is used for Pom mode, so only needs to be set here.
    cycleRoundsAdapter.setMode(mode);

    roundListDivider = editCyclesPopupView.findViewById(R.id.round_list_divider);
    roundListDivider.setVisibility(View.GONE);
    roundRecycler = editCyclesPopupView.findViewById(R.id.round_list_recycler);
    roundRecyclerTwo = editCyclesPopupView.findViewById(R.id.round_list_recycler_two);
    roundRecycler.setAdapter(cycleRoundsAdapter);
    roundRecyclerTwo.setAdapter(cycleRoundsAdapterTwo);
    roundRecycler.setLayoutManager(lm2);
    roundRecyclerTwo.setLayoutManager(lm3);

    //Rounds begin unpopulated, so remove second recycler view.
    roundRecyclerTwo.setVisibility(View.GONE);
    //Retrieves layout parameters for our recyclerViews. Used to adjust position based on size.
    recyclerLayoutOne = (ConstraintLayout.LayoutParams) roundRecycler.getLayoutParams();
    recyclerLayoutTwo = (ConstraintLayout.LayoutParams) roundRecyclerTwo.getLayoutParams();
    //Using exclusively programmatic layout params for round recyclerViews. Setting defaults. Second will never change.
    recyclerLayoutOne.leftMargin = 240;

    Animation slide_left = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_in_left);
    slide_left.setDuration(400);
    savedCycleRecycler.startAnimation(slide_left);

    //Sets all editTexts to GONE, and then populates them + textViews based on mode.
    removeEditTimerViews(false);
    defaultEditRoundViews();
    convertEditTime(true);
    setEditValues();

    //Listens to all editTexts for changes.
    TextWatcher editTextWatcher = new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }
      @Override
      public void afterTextChanged(Editable s) {
        //We set activeEditListener to FALSE to prevent setEditValues() from triggering when not desired. Right now, it's when we are using the +/- runnables to move our time.
        if (activeEditListener) setEditValues();
      }
    };

    //Watches editText title box and passes its value into the String that gets saved/updated in database.
    TextWatcher titleTextWatcher = new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }
      @Override
      public void afterTextChanged(Editable s) {
        cycleTitle = cycleNameEdit.getText().toString();
        //If round list in current mode is at least 1 and title has been changed, enable save button.
        if ((mode==1 && workoutTime.size()>0) || ((mode==3 && pomValuesTime.size()>0))) {
          if (!save_edit_cycle.isEnabled()) save_edit_cycle.setEnabled(true);
          if (save_edit_cycle.getAlpha()!=1) save_edit_cycle.setAlpha(1.0f);
        }
      }
    };

    cycleNameEdit.addTextChangedListener(titleTextWatcher);
    firstRowEditMinutes.addTextChangedListener(editTextWatcher);
    firstRowEditSeconds.addTextChangedListener(editTextWatcher);
    secondRowEditMinutes.addTextChangedListener(editTextWatcher);
    secondRowEditSeconds.addTextChangedListener(editTextWatcher);
    thirdRowEditMinutes.addTextChangedListener(editTextWatcher);
    thirdRowEditSeconds.addTextChangedListener(editTextWatcher);

    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
      @Override
      public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
          case 0:
            mode = 1;
            //Sets the recyclerView classes for each mode adapters.
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
        if (mode==1) {
          sortHigh.setVisibility(View.VISIBLE);
          sortLow.setVisibility(View.VISIBLE);
          savedCycleRecycler.setVisibility(View.VISIBLE);
          savedPomCycleRecycler.setVisibility(View.GONE);
          total_set_header.setText(R.string.total_sets);
          currentProgressBarValue = sharedPreferences.getInt("savedProgressBarValueForModeOne", 0);
          timeLeftValueHolder = sharedPreferences.getString("timeLeftValueForModeOne", "");
          positionOfSelectedCycle = sharedPreferences.getInt("positionOfSelectedCycleForModeOne", 0);
          timerIsPaused = sharedPreferences.getBoolean("modeOneTimerPaused", false);
          timerEnded = sharedPreferences.getBoolean("modeOneTimerEnded", false);
          timerDisabled = sharedPreferences.getBoolean("modeOneTimerDisabled", false);
        } else if (mode==3) {
          sortHigh.setVisibility(View.GONE);
          sortLow.setVisibility(View.GONE);
          roundRecyclerTwo.setVisibility(View.GONE);
          recyclerLayoutOne.leftMargin = 240;
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
        }
        //Sets all editTexts to GONE, and then populates them + textViews based on mode.
        removeEditTimerViews(false);
        defaultEditRoundViews();
      }

      @Override
      public void onTabUnselected(TabLayout.Tab tab) {
        //since modes use same String, clear it between tab switches.
        cycleTitle = "";
        //If in highlight mode (most easily denoted by enabled state of sortButton), exit out its view since we are switching tabs.
        if (!sortButton.isEnabled()) fadeEditCycleButtonsIn(FADE_OUT_HIGHLIGHT_MODE);
        //Dismisses editCycle popup when switching tabs.
        if (editCyclesPopupWindow.isShowing()) editCyclesPopupWindow.dismiss();
        //Turning highlight mode off since we are moving to a new tab.
        savedCycleAdapter.removeHighlight(true);
        savedPomCycleAdapter.removeHighlight(true);
        //Resets callback vars for clicked positions and highlighted positions when switching tabs.
        positionOfSelectedCycle = 0;
        receivedHighlightPositions.clear();
      }
      @Override
      public void onTabReselected(TabLayout.Tab tab) {
      }
    });

    global_settings.setOnClickListener(v-> {
      settingsPopupWindow.showAtLocation(cl, Gravity.NO_GRAVITY, 0, 240);
    });

      //Brings up editCycle popUp to create new Cycle.
    fab.setOnClickListener(v -> {
      fab.setEnabled(false);
      buttonToLaunchTimer.setEnabled(true);
      //Default disabled state of edited cycle save, if nothing has changed.
      save_edit_cycle.setEnabled(false);
      save_edit_cycle.setAlpha(0.3f);
      //Default row selection.
      resetRows();
      cycleNameEdit.getText().clear();
      isNewCycle = true;
      //Clears round adapter arrays so they can be freshly populated.
      clearRoundAdapterArrays();
      if (!cycleTitle.isEmpty()) cycleTitle = cycleNameEdit.getText().toString(); else cycleTitle = date;
      editCyclesPopupWindow.showAsDropDown(tabLayout);
    });

    stopwatch.setOnClickListener(v-> {
      savedMode = mode;
      mode = 4;
      dotDraws.setMode(4);
      setInitialTextSizeForRounds(0);
      populateTimerUI();
    });

    //Showing sort popup window.
    sortButton.setOnClickListener(v-> {
      moveSortCheckmark();
      sortPopupWindow.showAtLocation(cl, Gravity.END|Gravity.TOP, 0, 0);
    });

    //Uses single view for all sort buttons. Queries the appropriate cycle sort via the DAO and sets checkmark.
    View.OnClickListener sortListener = view -> {
      //Must run this first to get sort mode.
      //Casting View used by listener to textView, which we then check against its String value.
      TextView textButton = (TextView) view;
      //Handles checkmark views.
      if (textButton.getText().toString().equals("Added: Most Recent")) sortHolder = 1;
      if (textButton.getText().toString().equals("Added: Least Recent")) sortHolder = 2;
      if (textButton.getText().toString().equals("Title: A - Z")) sortHolder = 3;
      if (textButton.getText().toString().equals("Title: Z - A")) sortHolder = 4;
      if (textButton.getText().toString().equals("Round Count: Most")) sortHolder = 5;
      if (textButton.getText().toString().equals("Round Count: Least")) sortHolder = 6;
      //Assigns one of our sort modes to the sort style depending on which timer mode we're on.
      if (mode==1) sortMode = sortHolder; else if (mode==3) sortModePom = sortHolder;

      AsyncTask.execute(queryAndSortAllCyclesFromDatabaseRunnable);
      //Saves sort mode so it defaults to chosen whenever we create this activity.
      prefEdit.putInt("sortMode", sortMode);
      prefEdit.putInt("sortModePom", sortModePom);
      prefEdit.apply();
    };

    sortAlphaStart.setOnClickListener(sortListener);
    sortAlphaEnd.setOnClickListener(sortListener);
    sortRecent.setOnClickListener(sortListener);
    sortNotRecent.setOnClickListener(sortListener);
    sortHigh.setOnClickListener(sortListener);
    sortLow.setOnClickListener(sortListener);

    //Exiting timer popup always brings us back to popup-less Main, so change views accordingly.
    timerPopUpWindow.setOnDismissListener(() -> {
      timerDisabled = false;
      makeCycleAdapterVisible = false;
      beginTimerForNextRound = false;
      buttonToLaunchTimer.setEnabled(false);
      roundedValueForTotalTimes = 0;

      if (timerIsPaused) activateResumeOrResetOptionForCycle();
//      pauseAndResumeTimer(PAUSING_TIMER);

      AsyncTask.execute(updateTotalTimesInDatabaseRunnable);
      addAndRoundDownTotalCycleTimeFromPreviousRounds(false);

      //Removes runnable that begins next round.
      mHandler.removeCallbacksAndMessages(null);
      replaceCycleListWithEmptyTextViewIfNoCyclesExist();
      cl.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
      //Prevents timer from starting. Runnable will just populate values of next round.

      //If dismissing stopwatch, switch to whichever non-stopwatch mode we were on before.
      if (mode==4) mode = savedMode;
      dotDraws.setMode(mode);
      blankCanvas.setVisibility(View.INVISIBLE);
      //Since we don't update saved cycle list when launching timer (for aesthetic purposes), we do it here on exiting timer.
      if (mode==1) {
        savedCycleRecycler.setVisibility(View.VISIBLE);
        savedCycleAdapter.notifyDataSetChanged();
      } else if (mode==3){
        savedPomCycleRecycler.setVisibility(View.VISIBLE);
        savedPomCycleAdapter.notifyDataSetChanged();
      }
      blankCanvas.setVisibility(View.GONE);
    });

     editCyclesPopupView.setOnClickListener(v-> {
      //Caps and sets editText values when clicking outside (exiting) the editText box.
      convertEditTime(true);
      //Dismisses editText views if we click within the unpopulated area of popUp. Replaces them w/ textViews.
      removeEditTimerViews(true);
      //Hides soft keyboard by using a token of the current editCycleView.
      inputMethodManager.hideSoftInputFromWindow(editCyclesPopupView.getWindowToken(), 0);
    });

    editCyclesPopupView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
      @Override
      public void onSystemUiVisibilityChange(int visibility) {
        //Prevents tearing when soft keyboard pushes up in editCycle popUp.
        if (mode==1) savedCycleRecycler.setVisibility(View.GONE);
        if (mode==3) savedPomCycleRecycler.setVisibility(View.GONE);
        emptyCycleList.setVisibility(View.GONE);
        cl.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.dark_grey));
      }
    });

    //Because this window steals focus from our activity so it can use the soft keyboard, we are using this listener to perform the functions our onBackPressed override would normally handle when the popUp is active.
    editCyclesPopupWindow.setOnDismissListener(() -> {
      replaceCycleListWithEmptyTextViewIfNoCyclesExist();
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
      //Color reset to black, also for smooth transition to timer. Grey only necessary to prevent soft kb tearing.
      cl.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
      //Re-enables FAB button (disabled to prevent overlap when edit popup is active).
      fab.setEnabled(true);
      //If closing edit cycle popUp after editing a cycle, do the following.
      if (currentlyEditingACycle) {
        savedCycleAdapter.removeHighlight(true);
        //Calls method that sets views for our edit cycles mode.
        fadeEditCycleButtonsIn(FADE_OUT_EDIT_CYCLE);
        currentlyEditingACycle = false;
      }

      //Updates round adapters so lists show as cleared.
      cycleRoundsAdapter.notifyDataSetChanged();
      cycleRoundsAdapterTwo.notifyDataSetChanged();
      //Removed round divider.
      roundListDivider.setVisibility(View.GONE);
    });

    ////--ActionBar Item onClicks START--////
    edit_highlighted_cycle.setOnClickListener(v-> {
      //No potential index issues here, so enable timer start.
      buttonToLaunchTimer.setEnabled(true);
      currentlyEditingACycle = true;
      //Default row selection.
      resetRows();
      //Used when deciding whether to save a new cycle or retrieve/update a current one. Editing will always pull an existing one.
      isNewCycle = false;
      fadeEditCycleButtonsIn(FADE_IN_EDIT_CYCLE);
      //Displays edit cycles popUp.
      editCyclesPopupWindow.showAsDropDown(tabLayout);

      //Button is only active if list contains exactly ONE position (i.e. only one cycle is selected). Here, we set our retrieved position (same as if we simply clicked a cycle to launch) to the one passed in from our highlight.
      positionOfSelectedCycle = Integer.parseInt(receivedHighlightPositions.get(0));
      //Clears old array values.
      clearRoundAdapterArrays();
      //Uses this single position to retrieve cycle and populate timer arrays.
      populateRoundList();
      switch (mode) {
        case 1:
          //Populating String ArrayLists used to display rounds in editCycle popUp.
          for (int i=0; i<workoutTime.size(); i++) {
            //Aggregate list of rounds. Necessary since adjustCustom() uses it.
            convertedWorkoutTime.add(convertSeconds(workoutTime.get(i)/1000));
            //If 8 or less rounds, add to first round adapter's String Array (and roundType).
            if (i<=7) {
              roundHolderOne.add(convertSeconds(workoutTime.get(i)/1000));
              typeHolderOne.add(typeOfRound.get(i));
            }
            //If 9 or more rounds, add to second round adapter's String Array (and roundType).
            if (i>=8) {
              roundHolderTwo.add(convertSeconds(workoutTime.get(i)/1000));
              typeHolderTwo.add(typeOfRound.get(i));
            }
          }
          break;
        case 3:
          //Since our fade animation listener clears our timer and adapter lists, we disable it here when we need to pull up saved cycles.
          cycleRoundsAdapter.disablePomFade();
          for (int i=0; i<pomValuesTime.size(); i++) convertedPomList.add(convertSeconds(pomValuesTime.get(i)/1000));
          break;
      }
      runOnUiThread(()-> {
        //Changes view layout depending on +/- 8 round count.
        if (mode==1) {
          if (workoutTime.size()<=8) {
            roundRecyclerTwo.setVisibility(View.GONE);
            recyclerLayoutOne.leftMargin = 240;
            roundListDivider.setVisibility(View.GONE);
            savedCycleAdapter.removeHighlight(true);
          } else {
            roundRecyclerTwo.setVisibility(View.VISIBLE);
            recyclerLayoutOne.leftMargin = 5;
            roundListDivider.setVisibility(View.VISIBLE);
            savedPomCycleAdapter.removeHighlight(true);
          }
        }
        cycleNameText.setVisibility(View.INVISIBLE);
        cycleNameEdit.setVisibility(View.VISIBLE);
        cycleNameText.setText(cycleTitle);
        //Setting editText title.
        cycleNameEdit.setText(cycleTitle);
        //Updating adapter views.
        cycleRoundsAdapter.notifyDataSetChanged();
        cycleRoundsAdapterTwo.notifyDataSetChanged();
      });
      //Default disabled state of edited cycle save, if nothing has changed.
      save_edit_cycle.setEnabled(false);
      save_edit_cycle.setAlpha(0.3f);
    });

    //Turns off our cycle highlight mode from adapter.
    cancelHighlight.setOnClickListener(v-> {
      if (mode==1) {
        savedCycleAdapter.removeHighlight(true);
        savedCycleAdapter.notifyDataSetChanged();
      }
      if (mode==3) {
        savedPomCycleAdapter.removeHighlight(true);
        savedPomCycleAdapter.notifyDataSetChanged();
      }
      fadeEditCycleButtonsIn(FADE_OUT_HIGHLIGHT_MODE);
    });

    delete_highlighted_cycle.setOnClickListener(v-> {
      //Disables button until next highlight enables it (to prevent index errors).
      delete_highlighted_cycle.setEnabled(false);
      fadeEditCycleButtonsIn(FADE_OUT_HIGHLIGHT_MODE);

      //First query to get current list of rows.
      for (int i=0; i<receivedHighlightPositions.size(); i++) {
        positionOfSelectedCycle = Integer.parseInt(receivedHighlightPositions.get(i));
        //Using each received position, deletes the cycle from the database.
        AsyncTask.execute(deleteSingleCycleASyncRunnable);
        //Using each received position, deletes the cycle's values from its adapter arrays.
        editCycleArrayLists(DELETING_CYCLE);
      }
      //Since we have deleted every position in selected list, clear the list.
      receivedHighlightPositions.clear();
      cancelHighlight.setVisibility(View.INVISIBLE);
      edit_highlighted_cycle.setVisibility(View.INVISIBLE);
      delete_highlighted_cycle.setVisibility(View.INVISIBLE);
      appHeader.setVisibility(View.VISIBLE);

      if (mode==1) savedCycleAdapter.removeHighlight(true);
      if (mode==3) savedPomCycleAdapter.removeHighlight(true);
      Toast.makeText(getApplicationContext(), "Deleted!", Toast.LENGTH_SHORT).show();

    });

    delete_edit_cycle.setOnClickListener(v-> {
      //If on a saved edit cycle, delete it from database. Otherwise, simply clear the adapter array lists since nothing is actually saved.
      if (!isNewCycle) {
        positionOfSelectedCycle = Integer.parseInt(receivedHighlightPositions.get(0));
        AsyncTask.execute(deleteSingleCycleASyncRunnable);
        editCycleArrayLists(DELETING_CYCLE);
      } else clearRoundAdapterArrays();
      editCyclesPopupWindow.dismiss();
      Toast.makeText(getApplicationContext(), "Deleted!", Toast.LENGTH_SHORT).show();
    });

    delete_all_confirm.setOnClickListener(v-> {
      //Removes confirmation window.
      if (deleteCyclePopupWindow.isShowing()) deleteCyclePopupWindow.dismiss();
      AsyncTask.execute(() -> {
        if (delete_all_text.getText().toString().equals(getString(R.string.delete_total_times))) AsyncTask.execute(deleteTotalCycleTimesASyncRunnable);
        else if (delete_all_text.getText().toString().equals(getString(R.string.delete_all_cycles))) AsyncTask.execute(deleteAllCyclesASyncRunnable);
      });
    });

    delete_all_cancel.setOnClickListener(v-> {
      //Removes confirmation window.
      if (deleteCyclePopupWindow.isShowing()) deleteCyclePopupWindow.dismiss();
    });

    save_edit_cycle.setOnClickListener(v-> {
      AsyncTask.execute(saveCyclesASyncRunnable);
      //Disables save button. Any change in rounds or title will re-activate it.
      save_edit_cycle.setEnabled(false);
      save_edit_cycle.setAlpha(0.3f);
      if (mode==1) savedCycleAdapter.notifyDataSetChanged(); if (mode==3) savedPomCycleAdapter.notifyDataSetChanged();
      Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();
    });

    //When in highlight edit mode, clicking on the textView will remove it, replace it w/ editText field, give that field focus and bring up the soft keyboard.
    cycleNameText.setOnClickListener(v-> {
      cycleNameText.setVisibility(View.INVISIBLE);
      cycleNameEdit.setVisibility(View.VISIBLE);
      cycleNameEdit.requestFocus();
      inputMethodManager.showSoftInput(cycleNameEdit, 0);
    });

    //Selects all text when long clicking in editText.
    cycleNameEdit.setOnLongClickListener(v-> {
      cycleNameEdit.selectAll();
      return true;
    });

    //Caps and sets editText values. Only spot that takes focus outside of the view itself (above). Needs to be onTouch to register first click, and false so event is not consumed.
    cycleNameEdit.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction()==MotionEvent.ACTION_DOWN) {
          convertEditTime(true);
        }
        return false;
      }
    });

    s1.setOnClickListener(v->{
      if (mode==1) {
        //Toggles coloring and row selection.
        if (!firstRowHighlighted) {
          breaksSelected = false;
          setsSelected = true;
          firstRowHighlighted = true;
          secondRowHighlighted = false;
          //If first row is highlighted, second row should un-highlight.
          toggleInfinityRoundsForBreaks.setAlpha(0.3f);
          rowSelect(s1, firstRowTextView, firstRowEditMinutes, firstRowEditSeconds, firstRowEditColon, firstRowAddButton, firstRowSubtractButton, Color.GREEN);
          rowSelect(s2, secondRowTextView, secondRowEditMinutes, secondRowEditSeconds, secondRowEditColon, secondRowAddButton, secondRowSubtractButton, Color.WHITE);
        }
      }
    });

    s2.setOnClickListener(v-> {
      if (mode==1) {
        //Toggles coloring and row selection.
        if (!secondRowHighlighted) {
          setsSelected = false;
          breaksSelected = true;
          secondRowHighlighted = true;
          firstRowHighlighted = false;
          toggleInfinityRoundsForSets.setAlpha(0.3f);
          rowSelect(s2, secondRowTextView, secondRowEditMinutes, secondRowEditSeconds, secondRowEditColon, secondRowAddButton, secondRowSubtractButton, Color.RED);
          rowSelect(s1, firstRowTextView, firstRowEditMinutes, firstRowEditSeconds, firstRowEditColon, firstRowAddButton, firstRowSubtractButton, Color.WHITE);
        }
      }
    });

    toggleInfinityRoundsForSets.setOnClickListener(v -> {
      if (setsSelected) if (toggleInfinityRoundsForSets.getAlpha()==1.0f) toggleInfinityRoundsForSets.setAlpha(0.3f); else toggleInfinityRoundsForSets.setAlpha(1.0f);
    });

    toggleInfinityRoundsForBreaks.setOnClickListener(v -> {
      if (breaksSelected) if (toggleInfinityRoundsForBreaks.getAlpha()==1.0f) toggleInfinityRoundsForBreaks.setAlpha(0.3f); else toggleInfinityRoundsForBreaks.setAlpha(1.0f);
    });

    //For moment, using arrows next to sets and breaks to determine which type of round we're adding.
    addRoundToCycleButton.setOnClickListener(v -> {
      adjustCustom(true);
    });

    SubtractRoundFromCycleButton.setOnClickListener(v -> {
      adjustCustom(false);
    });

    //Grabs the x-axis value of textView, and uses that determine whether we replace it with the minutes or seconds editText.
    firstRowTextView.setOnTouchListener((v, event) -> {
      if (event.getAction()==MotionEvent.ACTION_DOWN) {
        editTextViewPosX = event.getX();
        editAndTextSwitch(true, 1);
      }
      return true;
    });

    secondRowTextView.setOnTouchListener((v, event) -> {
      if (event.getAction()==MotionEvent.ACTION_DOWN) {
        editTextViewPosX = event.getX();
        editAndTextSwitch(true, 2);
      }
      return true;
    });

    thirdRowEditTextView.setOnTouchListener((v, event) -> {
      if (event.getAction()==MotionEvent.ACTION_DOWN) {
        editTextViewPosX = event.getX();
        editAndTextSwitch(true, 3);
      }
      return true;
    });

    ////--EditCycles Menu Item onClicks START--////
    buttonToLaunchTimer.setOnClickListener(v-> {
      //Launched from editCyclePopUp and calls TimerInterface. First input controls whether it is a new cycle, and the second will always be true since a cycle launch should automatically save/update it in database.
      launchTimerCycle(true);
    });

    adjustRoundDelay = new Runnable() {
      @Override
      public void run() {
        addRoundToCycleButton.setClickable(true);
        SubtractRoundFromCycleButton.setClickable(true);
      }
    };

    changeFirstValue = new Runnable() {
      @Override
      public void run() {
        switch (mode) {
          case 1:
            if (incrementValues) setValue += 1; else setValue -= 1;
            prefEdit.putInt("setValue", setValue);
            break;
          case 3:
            if (incrementValues) pomValue1 += 5;
            else pomValue1 -= 5;
            break;
        }
        mHandler.postDelayed(this, incrementTimer * 10);
        setTimerValueBounds();
        fadeCap(firstRowTextView);
        editAndTextSwitch(false, 1);
        prefEdit.apply();
      }
    };

    changeSecondValue = new Runnable() {
      @Override
      public void run() {
        switch (mode) {
          case 1:
            if (incrementValues) breakValue += 1;
            else breakValue -= 1;
            break;
          case 3:
            if (incrementValues) pomValue2 += 5;
            else pomValue2 -= 5;
            break;
        }
        mHandler.postDelayed(this, incrementTimer * 10);
        setTimerValueBounds();
        fadeCap(secondRowTextView);
        editAndTextSwitch(false, 2);
        prefEdit.apply();
      }
    };

    changeThirdValue = new Runnable() {
      @Override
      public void run() {
        if (incrementValues) pomValue3 += 5;
        else pomValue3 -= 5;
        mHandler.postDelayed(this, incrementTimer*10);
        setTimerValueBounds();
        fadeCap(thirdRowEditTextView);
        editAndTextSwitch(false, 3);
        prefEdit.apply();
      }
    };

    valueSpeed = new Runnable() {
      @Override
      public void run() {
        if (incrementTimer > 1) incrementTimer -= 1;
        mHandler.postDelayed(this, 300);
      }
    };

    firstRowAddButton.setOnTouchListener((v, event) -> {
      incrementValues = true;
      setIncrements(event, changeFirstValue);
      convertEditTime(true);
      switch (mode) {
        case 1:
          firstRowTextView.setText(convertTimeToStringWithFullMinuteAndSecondValues(setValue));
          break;
        case 3:
          firstRowTextView.setText(convertTimeToStringWithFullMinuteAndSecondValues(pomValue1));
          break;
      }
      return true;
    });

    firstRowSubtractButton.setOnTouchListener((v, event) -> {
      incrementValues = false;
      setIncrements(event, changeFirstValue);
      convertEditTime(true);
      switch (mode) {
        case 1:
          firstRowTextView.setText(convertTimeToStringWithFullMinuteAndSecondValues(setValue));
          break;
        case 3:
          firstRowTextView.setText(convertTimeToStringWithFullMinuteAndSecondValues(pomValue1));
          break;
      }
      return true;
    });

    secondRowAddButton.setOnTouchListener((v, event) -> {
      incrementValues = true;
      setIncrements(event, changeSecondValue);
      convertEditTime(true);
      switch (mode) {
        case 1:
          secondRowTextView.setText(convertTimeToStringWithFullMinuteAndSecondValues(breakValue));
          break;
        case 3:
          secondRowTextView.setText(convertTimeToStringWithFullMinuteAndSecondValues(pomValue2));
          break;
      }
      return true;
    });

    secondRowSubtractButton.setOnTouchListener((v, event) -> {
      incrementValues = false;
      setIncrements(event, changeSecondValue);
      convertEditTime(true);
      switch (mode) {
        case 1:
          secondRowTextView.setText(convertTimeToStringWithFullMinuteAndSecondValues(breakValue));
          break;
        case 3:
          secondRowTextView.setText(convertTimeToStringWithFullMinuteAndSecondValues(pomValue2));
          break;
      }
      return true;
    });

    thirdRowAddButton.setOnTouchListener((v, event) -> {
      incrementValues = true;
      setIncrements(event, changeThirdValue);
      convertEditTime(true);
      thirdRowEditTextView.setText(convertTimeToStringWithFullMinuteAndSecondValues(pomValue3));
      return true;
    });

    thirdRowSubtractButton.setOnTouchListener((v, event) -> {
      incrementValues = false;
      setIncrements(event, changeThirdValue);
      convertEditTime(true);
      thirdRowEditTextView.setText(convertTimeToStringWithFullMinuteAndSecondValues(pomValue3));
      return true;
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

    sharedPreferences = getApplicationContext().getSharedPreferences("pref", 0);
    prefEdit = sharedPreferences.edit();

    //Draws dot display depending on which more we're on.
    dotDraws.setMode(mode);
    //Implements callback for end-of-round alpha fade on dots.
    dotDraws.onAlphaSend(MainActivity.this);

    //Recycler view for our stopwatch laps.
    lapLayout = new LinearLayoutManager(getApplicationContext());
    lapAdapter = new LapAdapter(getApplicationContext(), currentLapList, savedLapList);
    lapRecycler.setAdapter(lapAdapter);
    lapRecycler.setLayoutManager(lapLayout);

    //Sets all progress bars to their start value.
    progressBar.setProgress(maxProgress);

    //Used in all timers to smooth out end fade.
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

    //These two runnables act as our timers for "count up" rounds.
    infinityRunnableForSets = new Runnable() {
      @Override
      public void run() {
        currentProgressBarValue = (int) objectAnimator.getAnimatedValue();
        reduceTextSizeForInfinityRounds(countUpMillisSets);
        //Subtracting the current time from the base (start) time which was set in our pauseResume() method.
        countUpMillisSets = (int) (countUpMillisHolder) +  (System.currentTimeMillis() - defaultProgressBarDurationForInfinityRounds);
        total_set_time.setText(stringValueOfTotalCycleTime(2));

        //Subtracts the elapsed millis value from base 30000 used for count-up rounds.
        currentProgressBarValueForInfinityRounds = maxProgress - countUpMillisBreaks;
        timeLeft.setText(convertSeconds((countUpMillisSets) / 1000));
        //Updates workoutTime list w/ millis values for round counting up, and passes those into dotDraws so the dot text also iterates up.
        workoutTime.set(workoutTime.size() - numberOfRoundsLeft, (int) countUpMillisSets);
        dotDraws.updateWorkoutTimes(workoutTime, typeOfRound);
        //Once progressBar value hits 0, animate bar/text, reset bar's progress value to max, and restart the objectAnimator that uses it.
        if (progressBar.getProgress()<=0) {
          progressBar.startAnimation(fadeProgressOut);
          timeLeft.startAnimation(fadeProgressOut);
          objectAnimator.start();
        }
        mHandler.postDelayed(this, 50);
      }
    };

    infinityRunnableForBreaks = new Runnable() {
      @Override
      public void run() {
        currentProgressBarValue = (int) objectAnimator.getAnimatedValue();
        reduceTextSizeForInfinityRounds(countUpMillisBreaks);
        //Subtracting the current time from the base (start) time which was set in our pauseResume() method, then adding it to the saved value of our countUpMillis.
        countUpMillisBreaks = (int) (countUpMillisHolder) +  (System.currentTimeMillis() - defaultProgressBarDurationForInfinityRounds);
        total_break_time.setText(stringValueOfTotalCycleTime(4));

        //Subtracts the elapsed millis value from base 30000 used for count-up rounds.
        currentProgressBarValueForInfinityRounds = maxProgress - countUpMillisBreaks;
        timeLeft.setText(convertSeconds((countUpMillisBreaks) / 1000));
        //Updates workoutTime list w/ millis values for round counting up, and passes those into dotDraws so the dot text also iterates up.
        workoutTime.set(workoutTime.size() - numberOfRoundsLeft, (int) countUpMillisBreaks);
        dotDraws.updateWorkoutTimes(workoutTime, typeOfRound);
        //Temporary value for current round, using totalBreakMillis which is our permanent value.
        if (progressBar.getProgress()<=0) {
          progressBar.startAnimation(fadeProgressOut);
          timeLeft.startAnimation(fadeProgressOut);
          objectAnimator.start();
        }
        mHandler.postDelayed(this, 50);
      }
    };

    stopWatchRunnable = new Runnable() {
      @Override
      public void run() {
        DecimalFormat df2 = new DecimalFormat("00");
        //ms can never be more than 60/sec due to refresh rate.
        ms += 1;
        msReset += 1;
        msConvert += 1;
        msConvert2 += 1;
        msDisplay += 1;
        if (msConvert > 59) msConvert = 0;
        if (msConvert2 > 59) msConvert2 = 0;
        if (msDisplay > 59) msDisplay = 0;

        seconds = ms / 60;
        minutes = seconds / 60;

        newMs = df2.format((msConvert2 / 60) * 100);
        savedMs = df2.format((msConvert / 60) * 100);
        newMsConvert = Integer.parseInt(newMs);
        savedMsConvert = Integer.parseInt(savedMs);

        //Conversion to long solves +30 ms delay for display.
        displayMs = df2.format((msDisplay / 60) * 100);
        displayTime = convertSeconds( (long) seconds);

        if (textSizeIncreased && mode==4) {
          if (seconds > 4) {
            changeTextSize(valueAnimatorDown, timeLeft);
            textSizeIncreased = false;
          }
        }

        if (mode==4) timeLeft.setText(displayTime);
        msTime.setText(displayMs);

        mHandler.postDelayed(this, 10);
      }
    };

    //Disables button for 1 second after push. Re-enables it through runnable after that.
    next_round.setOnClickListener(v -> {
      nextRound(true);
    });

    reset.setOnClickListener(v -> {
      if (mode != 3) resetTimer();
      else {
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

    delete_all_cancel.setOnClickListener(v -> {
      //Removes our delete confirm popUp if we cancel.
      if (deleteCyclePopupWindow.isShowing()) deleteCyclePopupWindow.dismiss();
    });

    reset_total_times.setOnClickListener(v -> {
      delete_all_text.setText(R.string.delete_total_times);
      deleteCyclePopupWindow.showAtLocation(cl, Gravity.CENTER_HORIZONTAL, 0, -100);
    });

    postRoundRunnableForFirstMode = new Runnable() {
      @Override
      public void run() {
        //Updates dotDraws class w/ round count.
        dotDraws.updateWorkoutRoundCount(startRounds, numberOfRoundsLeft);
        //Resets the alpha value we use to fade dots back to 255 (fully opaque).
        dotDraws.resetDotAlpha();
        //Resetting values for count-up modes. Simpler to keep them out of switch statement.
        countUpMillisSets = 0;
        countUpMillisBreaks = 0;
        countUpMillisHolder = 0;
        defaultProgressBarDurationForInfinityRounds = System.currentTimeMillis();
        //Next round begins active by default, so we set our paused mode to false.
        timerIsPaused = false;
        //Executes next round based on which type is indicated in our typeOfRound list.
        if (numberOfRoundsLeft>0) {
          switch (typeOfRound.get(currentRound)) {
            case 1:
              setMillis = workoutTime.get(workoutTime.size() - numberOfRoundsLeft);
              timeLeft.setText(convertSeconds((setMillis + 999) / 1000));
              if (beginTimerForNextRound) {
                startObjectAnimator();
                startSetTimer();
              }
              break;
            case 2:
              timeLeft.setText("0");
              if (beginTimerForNextRound) {
                instantiateAndStartObjectAnimator(30000);
                mHandler.post(infinityRunnableForSets);
              }
              break;
            case 3:
              breakMillis = workoutTime.get(workoutTime.size() - numberOfRoundsLeft);
              timeLeft.setText(convertSeconds((breakMillis + 999) / 1000));
              if (beginTimerForNextRound) {
                startObjectAnimator();
                startBreakTimer();
              }
              break;
            case 4:
              timeLeft.setText("0");
              if (beginTimerForNextRound) {
                instantiateAndStartObjectAnimator(30000);
                mHandler.post(infinityRunnableForBreaks);
              }
              break;
          }
          //If number of rounds left is 0, do the following.
        } else {
          //Continuous animation for end of cycle.
          animateEnding();
          progressBar.setProgress(0);
          //Resets current round counter.
          currentRound = 0;
          //Used to call resetTimer() in pause/resume method. Separate than our disable method.
          timerEnded = true;
          //Adds to cycles completed counter.
          cyclesCompleted++;
          cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(cyclesCompleted)));
        }
        //Re-enables timer clicks, which are disabled for a brief period right before and after round timer ends.
        timerDisabled = false;
        //Re-enables the button that calls this method, now that it has completed.
        next_round.setEnabled(true);
      }
    };

    postRoundRunnableForThirdMode = new Runnable() {
      @Override
      public void run() {
        dotDraws.pomDraw(pomDotCounter, pomValuesTime);
        dotDraws.resetDotAlpha();

        if (pomDotCounter<=7) {
          pomMillis = pomValuesTime.get(pomDotCounter);
          timeLeft.setText(convertSeconds((pomMillis) / 1000));
          if (beginTimerForNextRound) {
            startObjectAnimator();
            startPomTimer();
          }
        } else {
          //Continuous animation for end of cycle.
          animateEnding();
          progressBar.setProgress(0);
          timerEnded = true;
          cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(cyclesCompleted)));
        }
        timerDisabled = false;
        next_round.setEnabled(true);
      }
    };

    updateTotalTimesInDatabaseRunnable = new Runnable() {
      @Override
      public void run() {
        switch (mode) {
          case 1:
            cycles.setTotalSetTime((int) totalCycleSetTimeInMillis / 1000);
            cycles.setTotalBreakTime((int) totalCycleBreakTimeInMillis/1000);
            cycles.setCyclesCompleted(cyclesCompleted);
            cyclesDatabase.cyclesDao().updateCycles(cycles);
            break;
          case 3:
            pomCycles.setTotalWorkTime((int) totalCycleSetTimeInMillis / 1000);
            pomCycles.setTotalBreakTime((int) totalCycleBreakTimeInMillis / 1000);
            pomCycles.setCyclesCompleted(cyclesCompleted);
            cyclesDatabase.cyclesDao().updatePomCycles(pomCycles);
            break;
        }
      }
    };

    queryAllCyclesFromDatabaseRunnableAndRetrieveTotalTimes = new Runnable() {
      @Override
      public void run() {
        if (mode==1) cyclesList = cyclesDatabase.cyclesDao().loadAllCycles();
        if (mode==3) pomCyclesList = cyclesDatabase.cyclesDao().loadAllPomCycles();
        runOnUiThread(retrieveTotalCycleTimesFromDatabaseObjectRunnable);
      }
    };

  retrieveTotalCycleTimesFromDatabaseObjectRunnable = new Runnable() {
      @Override
      public void run() {
        int totalSetTime = 0;
        int totalBreakTime = 0;
        if (!isNewCycle) {
          if (mode==1) {
            cycles = cyclesList.get(positionOfSelectedCycle);
            totalSetTime = cycles.getTotalSetTime();
            totalBreakTime = cycles.getTotalBreakTime();
            cyclesCompleted = cycles.getCyclesCompleted();
          }
          if (mode==3) {
            pomCycles = pomCyclesList.get(positionOfSelectedCycle);
            totalSetTime = pomCycles.getTotalWorkTime();
            totalBreakTime = pomCycles.getTotalBreakTime();
            cyclesCompleted = pomCycles.getCyclesCompleted();
          }
        }
        totalCycleSetTimeInMillis = totalSetTime*1000;
        totalCycleBreakTimeInMillis = totalBreakTime*1000;
        total_set_time.setText(convertSeconds(totalSetTime));
        total_break_time.setText(convertSeconds(totalBreakTime));
        cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(cyclesCompleted)));
      }
    };

    queryAndSortAllCyclesFromDatabaseRunnable = new Runnable() {
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
//            Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_anim);
//            anim.setDuration(1000);
//            savedCycleRecycler.startAnimation(anim);
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

    saveCyclesASyncRunnable = new Runnable() {
      @Override
      public void run() {
        //Sets up Strings to save into database.
        gson = new Gson();
        workoutString = "";
        roundTypeString = "";
        pomString = "";

        int cycleID = 0;
        if (mode==1) {
          //If coming from FAB button, create a new instance of Cycles. If coming from a position in our database, get the instance of Cycles in that position.
          if (isNewCycle) cycles = new Cycles(); else if (cyclesList.size()>0) {
            cycleID = cyclesList.get(positionOfSelectedCycle).getId();
            cycles = cyclesDatabase.cyclesDao().loadSingleCycle(cycleID).get(0);
          }
          ///*----All of this occurs whether we are saving a new cycle, or launching a cycle currently in the database.----*///
          //Converting our String array of rounds in a cycle to a single String so it can be stored in our database. Saving "Count Down" values regardless of how we're counting, as we want them present when toggling between count up/count down.
          workoutString = gson.toJson(workoutTime);
          workoutString = friendlyString(workoutString);
          roundTypeString = gson.toJson(typeOfRound);
          roundTypeString = friendlyString(roundTypeString);
          //If round list is blank, setString will remain at "", in which case we do not save or update. This is determined after we convert via Json above.
          if (!workoutString.equals("")) {
            //Adding and inserting into database.
            cycles.setWorkoutRounds(workoutString);
            cycles.setRoundType(roundTypeString);
            //Setting most recent time accessed for sort mode.
            cycles.setTimeAccessed(System.currentTimeMillis());
            cycles.setItemCount(workoutTime.size());
            //If cycle is new, add an initial creation time and populate total times + completed cycle rows to 0.
            if (isNewCycle) {
              //Only setting timeAdded for NEW cycle. We want our (sort by date) to use the initial time/date.
              cycles.setTimeAdded(System.currentTimeMillis());
              cycles.setTotalSetTime(0);
              cycles.setTotalBreakTime(0);
              cycles.setCyclesCompleted(0);
              //If cycle is new, set title to given, or to current date/time if none given. If cycle is NOT new, only set to what has been entered (or keep old one if unchanged).
              if (cycleTitle.isEmpty()) cycleTitle = date;
              cycles.setTitle(cycleTitle);
              //If cycle is new, insert a new row.
              cyclesDatabase.cyclesDao().insertCycle(cycles);
              //Adding to adapter list.
              editCycleArrayLists(ADDING_CYCLE);
            } else {
              cycles.setTitle(cycleTitle);
              //If cycle is old, update current row.
              cyclesDatabase.cyclesDao().updateCycles(cycles);
              //Editing adapter list.
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
            pomCycles.setTimeAccessed(System.currentTimeMillis());;
            if (isNewCycle) {
              pomCycles.setTimeAdded(System.currentTimeMillis());
              if (cycleTitle.isEmpty()) cycleTitle = date;
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
      }
    };

    deleteTotalCycleTimesASyncRunnable = new Runnable() {
      @Override
      public void run() {
        if (mode==1) cyclesDatabase.cyclesDao().deleteTotalTimesCycle();
        if (mode==3) cyclesDatabase.cyclesDao().deleteTotalTimesPom();

        runOnUiThread(()->{
          deleteCyclePopupWindow.dismiss();
          totalCycleSetTimeInMillis = 0;
          totalCycleBreakTimeInMillis = 0;
          cyclesCompleted = 0;
          roundedValueForTotalTimes = 0;
          resettingTotalTime = true;

          total_set_time.setText("0");
          total_break_time.setText("0");
          cycles_completed.setText(getString(R.string.cycles_done, "0"));
          replaceCycleListWithEmptyTextViewIfNoCyclesExist();
        });
      }
    };

    deleteSingleCycleASyncRunnable = new Runnable() {
      @Override
      public void run() {
        if ((mode==1 && cyclesList.size()==0 || (mode==3 && pomCyclesList.size()==0))) {
          runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Nothing saved!", Toast.LENGTH_SHORT).show());
          return;
        }
        int cycleID;
        if (mode==1) {
          cyclesList = cyclesDatabase.cyclesDao().loadAllCycles();
          cycleID = cyclesList.get(positionOfSelectedCycle).getId();
          cycles = cyclesDatabase.cyclesDao().loadSingleCycle(cycleID).get(0);
          cyclesDatabase.cyclesDao().deleteCycle(cycles);
          runOnUiThread(()-> {
            savedCycleAdapter.notifyDataSetChanged();
          });
        }
        if (mode==3) {
          pomCyclesList = cyclesDatabase.cyclesDao().loadAllPomCycles();
          cycleID = pomCyclesList.get(positionOfSelectedCycle).getId();
          pomCycles = cyclesDatabase.cyclesDao().loadSinglePomCycle(cycleID).get(0);
          cyclesDatabase.cyclesDao().deletePomCycle(pomCycles);
          runOnUiThread(()-> {
            savedPomCycleAdapter.notifyDataSetChanged();
          });
        }
        runOnUiThread(()-> replaceCycleListWithEmptyTextViewIfNoCyclesExist());
      }
    };

    deleteAllCyclesASyncRunnable = new Runnable() {
      @Override
      public void run() {
        if (mode==1) {
          if (cyclesList.size()>0) {
            cyclesList = cyclesDatabase.cyclesDao().loadAllCycles();
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
            pomCyclesList = cyclesDatabase.cyclesDao().loadAllPomCycles();
            cyclesDatabase.cyclesDao().deleteAllPomCycles();
            runOnUiThread(()->{
              pomArray.clear();
              pomTitleArray.clear();
              savedPomCycleAdapter.notifyDataSetChanged();
            });
          }
        }
        runOnUiThread(()-> replaceCycleListWithEmptyTextViewIfNoCyclesExist());
      }
    };
  }

  public void resumeOrResetCycleFromAdapterList(int resumeOrReset){
    if (resumeOrReset==RESUMING_CYCLE_FROM_ADAPTER) {
      progressBar.setProgress(currentProgressBarValue);
      timerPopUpWindow.showAtLocation(cl, Gravity.NO_GRAVITY, 0, 0);
      //Sets paused boolean to true, so next timer click will resume.
      timerIsPaused = true;
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
      roundedValueForTotalTimes = 0;
    }
  }

  public void moveSortCheckmark() {
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

  public static class dismissReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      int notificationId = intent.getExtras().getInt("My ID");
      notificationDismissed = true;
    }
  }

  private PendingIntent createOnDismissedIntent(Context context, int notificationId) {
    Intent intent = new Intent(context, dismissReceiver.class);
    intent.putExtra("My ID", notificationId);

    PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),notificationId, intent, 0);

    return pendingIntent;
  }

  //Todo: breakMillis is ZERO, that's why.
  public void instantiateNotifications() {
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
      builder = new Notification.Builder(this, "1");
    } else {
      builder = new Notification.Builder(this);
    }

    builder.setSmallIcon(R.drawable.start_cycle);
    builder.setAutoCancel(false);
    builder.setPriority(Notification.PRIORITY_DEFAULT);
    builder.setDeleteIntent(createOnDismissedIntent(this, 0));

    notificationManagerCompat = NotificationManagerCompat.from(this);
  }

  public String setNotificationHeader(String mode, String roundType) {
    return (getString(R.string.notification_text_header, mode, roundType));
  }

  public String setNotificationBody(int roundsLeft, int startRounds, long timeLeft) {
    String currentRound = String.valueOf(startRounds-roundsLeft + 1);
    String totalRounds = String.valueOf(startRounds);
    long remainder = timeLeft%1000;
    String timeRemaining = convertTimeToStringWithFullMinuteAndSecondValuesWithoutSpaces((timeLeft + 1000) / 1000);

    return getString(R.string.notification_text, currentRound, totalRounds, timeRemaining);
  }

  public void setNotificationValues() {
    String headerOne = "";
    String headerTwo = "";
    String bodyOne = "";
    String bodyTwo = "";

    if (typeOfRound.size() > 0) {
      if (typeOfRound.get(currentRound) == 1 || typeOfRound.get(currentRound) == 2) {
        headerOne = setNotificationHeader("Workout", "Set");
        bodyOne = setNotificationBody(numberOfRoundsLeft, startRounds, setMillis);
      } else {
        headerOne = setNotificationHeader("Workout", "Break");
        bodyOne = setNotificationBody(numberOfRoundsLeft, startRounds, breakMillis);
      }
    }

    switch (pomDotCounter) {
      case 0: case 2: case 4: case 6:
        headerTwo = setNotificationHeader("Pomodoro", "Work");
        bodyTwo = setNotificationBody( pomDotCounter + 1, 8, pomMillis);
        break;
      case 1: case 3: case 5: case 7:
        headerTwo = setNotificationHeader("Pomodoro", "Break");
        bodyTwo = setNotificationBody(pomDotCounter + 1, 8, pomMillis);
        break;
    }

    if (objectAnimator.isStarted() && !objectAnimatorPom.isStarted()) {
      builder.setStyle(new Notification.InboxStyle()
              .addLine(headerOne)
              .addLine(bodyOne)
      );
    }

    if (!objectAnimator.isStarted() && objectAnimatorPom.isStarted()) {
      builder.setStyle(new Notification.InboxStyle()
              .addLine(headerTwo)
              .addLine(bodyTwo)
      );
    }

    if (objectAnimator.isStarted() && objectAnimatorPom.isStarted()) {
      builder.setStyle(new Notification.InboxStyle()
              .addLine(headerOne + getString(R.string.bunch_of_spaces_2) + headerTwo)
              .addLine(bodyOne + getString(R.string.bunch_of_spaces) + bodyTwo)
      );
    }

    notificationManagerCompat.notify(1, builder.build());
  }

  public void activateResumeOrResetOptionForCycle() {
    if (mode==1) {
      //Only shows restart/resume options of a cycle has been started.
      if (objectAnimator.isStarted() || startRounds-numberOfRoundsLeft>0) {
        if (isNewCycle) positionOfSelectedCycle = workoutCyclesArray.size()-1;
        savedCycleAdapter.showActiveCycleLayout(positionOfSelectedCycle, startRounds-numberOfRoundsLeft);
      }
      //If between rounds, post runnable for next round without starting timer or object animator.
      if (!objectAnimator.isStarted()) mHandler.post(postRoundRunnableForFirstMode);

      prefEdit.putInt("savedProgressBarValueForModeOne", currentProgressBarValue);
      prefEdit.putString("timeLeftValueForModeOne", timeLeft.getText().toString());
      prefEdit.putInt("positionOfSelectedCycleForModeOne", positionOfSelectedCycle);
      prefEdit.putBoolean("modeOneTimerPaused", timerIsPaused);
      prefEdit.putBoolean("modeOneTimerEnded", timerEnded);
      prefEdit.putBoolean("modeOneTimerDisabled", timerDisabled);
    }

    if (mode==3) {
      if (objectAnimatorPom.isStarted() || pomDotCounter > 0) {
        if (isNewCycle) positionOfSelectedCycle = 7;
        savedPomCycleAdapter.showActiveCycleLayout(positionOfSelectedCycle, pomDotCounter);
      }
      if (!objectAnimatorPom.isStarted()) mHandler.post(postRoundRunnableForThirdMode);

      prefEdit.putInt("savedProgressBarValueForModeThree", currentProgressBarValue);
      prefEdit.putString("timeLeftValueForModeThree", timeLeft.getText().toString());
      prefEdit.putInt("positionOfSelectedCycleForModeThree", positionOfSelectedCycle);
      prefEdit.putBoolean("modeThreeTimerPaused", timerIsPaused);
      prefEdit.putBoolean("modeThreeTimerEnded", timerEnded);
      prefEdit.putBoolean("modeThreeTimerDisabled", timerDisabled);
    }
    prefEdit.apply();
  }

  public int convertScalablePixelsToDensity(float pixels) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixels, getResources().getDisplayMetrics());
  }

  public int convertDensityPixelsToScalable(float pixels) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, pixels, getResources().getDisplayMetrics());
  }

  public void replaceCycleListWithEmptyTextViewIfNoCyclesExist() {
    if (mode==1) if (workoutCyclesArray.size()!=0) emptyCycleList.setVisibility(View.GONE); else emptyCycleList.setVisibility(View.VISIBLE);
    if (mode==3) if (pomArray.size()!=0) emptyCycleList.setVisibility(View.GONE); else emptyCycleList.setVisibility(View.VISIBLE);
  }

  //Resets row selection and editText/textView values.
  public void resetRows() {
    if (mode==1) {
      //Default selection of Set.
      firstRowHighlighted = true;
      secondRowHighlighted = false;
      rowSelect(s1, firstRowTextView, firstRowEditMinutes, firstRowEditSeconds, firstRowEditColon, firstRowAddButton, firstRowSubtractButton, Color.GREEN);
      rowSelect(s2, secondRowTextView, secondRowEditMinutes, secondRowEditSeconds, secondRowEditColon, secondRowAddButton, secondRowSubtractButton, Color.WHITE);
      //Our editText fields have listeners attached which call setEditValues(), which set our edit values AND setValue/breakValue vars to the values within the editText box itself. Here, we use 0:30 for both.
      firstRowEditMinutes.setText(String.valueOf(0));
      firstRowEditSeconds.setText(elongateEditSeconds(30));
      secondRowEditMinutes.setText(String.valueOf(0));
      secondRowEditSeconds.setText(elongateEditSeconds(30));
      firstRowTextView.setText(convertTimeToStringWithFullMinuteAndSecondValues(setValue));
      secondRowTextView.setText(convertTimeToStringWithFullMinuteAndSecondValues(breakValue));
    }
    else if (mode==3) {
      rowSelect(s1, firstRowTextView, firstRowEditMinutes, firstRowEditSeconds, firstRowEditColon, firstRowAddButton, firstRowSubtractButton, Color.WHITE);
      rowSelect(s2, secondRowTextView, secondRowEditMinutes, secondRowEditSeconds, secondRowEditColon, secondRowAddButton, secondRowSubtractButton, Color.WHITE);
      rowSelect(s3, thirdRowEditTextView, thirdRowEditMinutes, thirdRowEditSeconds, thirdRowEditColon, thirdRowAddButton, thirdRowSubtractButton, Color.WHITE);
    }
  }

  //Colors rows.
  public void rowSelect(TextView header, TextView textVal, EditText editOne, EditText editTwo, TextView editSep, ImageView plus, ImageView minus, int color) {
    header.setTextColor(color);
    textVal.setTextColor(color);
    editOne.setTextColor(color);
    editTwo.setTextColor(color);
    editSep.setTextColor(color);
    plus.setColorFilter(color);
    minus.setColorFilter(color);
  }

  //Fades action bar buttons in/out depending on whether we are editing cycles or not.
  public void fadeEditCycleButtonsIn(int typeOfFade) {
    //Clearing all animations. If we don't, their alphas tend to get reset.
    if (typeOfFade!=FADE_IN_EDIT_CYCLE) {
      edit_highlighted_cycle.clearAnimation();
      delete_highlighted_cycle.clearAnimation();
      cancelHighlight.clearAnimation();
      appHeader.clearAnimation();
      sortButton.clearAnimation();
    }
    if (typeOfFade==FADE_IN_HIGHLIGHT_MODE) {
      //Boolean used to keep launchCycles() from calling populateRoundLists(), which replace our current timer array list w/ one fetched from DB.
      edit_highlighted_cycle.startAnimation(fadeIn);
      delete_highlighted_cycle.startAnimation(fadeIn);
      cancelHighlight.startAnimation(fadeIn);
      appHeader.startAnimation(fadeOut);
      sortButton.startAnimation(fadeOut);
      //Disabling sort button since it is faded out and still active.
      sortButton.setEnabled(false);
      //Enabling edit/delete buttons.
      edit_highlighted_cycle.setEnabled(true);
      delete_highlighted_cycle.setEnabled(true);
      //Views for not-in-edit-mode.
    }
    if (typeOfFade==FADE_OUT_HIGHLIGHT_MODE) {
      //Boolean used to keep launchCycles() from calling populateRoundLists(), which replace our current timer array list w/ one fetched from DB.
      edit_highlighted_cycle.startAnimation(fadeOut);
      delete_highlighted_cycle.startAnimation(fadeOut);
      cancelHighlight.startAnimation(fadeOut);
      appHeader.startAnimation(fadeIn);
      sortButton.startAnimation(fadeIn);
      //Re-enabling sort button now that edit mode has exited.
      sortButton.setEnabled(true);
      //Disabling edit/delete buttons.
      edit_highlighted_cycle.setEnabled(false);
      delete_highlighted_cycle.setEnabled(false);
    }
    if (typeOfFade==FADE_OUT_EDIT_CYCLE) {
      sortButton.setVisibility(View.VISIBLE);
      delete_highlighted_cycle.setVisibility(View.INVISIBLE);
      delete_highlighted_cycle.setEnabled(false);
    }
  }

  //Clears the String values of timer Arrays passed to adapter.
  public void clearRoundAdapterArrays() {
    convertedWorkoutTime.clear();
    roundHolderOne.clear();
    roundHolderTwo.clear();
    typeHolderOne.clear();
    typeHolderTwo.clear();

    workoutTime.clear();
    typeOfRound.clear();
    pomValuesTime.clear();

    convertedPomList.clear();
  }

  //Calls runnables to change set, break and pom values. Sets a handler to increase change rate based on click length. Sets min/max values.
  public void setIncrements(MotionEvent event, Runnable runnable) {
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        //Handler must not be instantiated before this, otherwise the runnable will execute it on every touch (i.e. even on "action_up" removal.
        mHandler.postDelayed(runnable, 25);
        mHandler.postDelayed(valueSpeed, 25);

        break;
      case MotionEvent.ACTION_UP:
        mHandler.removeCallbacksAndMessages(null);
        incrementTimer = 10;
    }
  }

  //Passes current editText values into variables, and then sets min/max bounds on them.
  public void setEditValues() {
    if (mode==1) {
      editSetMinutes = convertEditTextToLong(firstRowEditMinutes);
      editSetSeconds = convertEditTextToLong(firstRowEditSeconds);
      if (editSetSeconds > 59) {
        editSetMinutes += 1;
        editSetSeconds = editSetSeconds - 60;
      }
      if (editSetMinutes >= 5) editSetMinutes = 5;
      if (editSetMinutes <= 0) editSetMinutes = 0;
      if (editSetSeconds <= 0 || editSetMinutes == 5) editSetSeconds = 0;
      if (editSetSeconds < 5 && editSetMinutes == 0) editSetSeconds = 0;
      //Sets our timer values to the values in editText.
      setValue = (int) ((editSetMinutes * 60) + editSetSeconds);
      firstRowTextView.setText(convertTimeToStringWithFullMinuteAndSecondValues(setValue));

      editBreakMinutes = convertEditTextToLong(secondRowEditMinutes);
      editBreakSeconds = convertEditTextToLong(secondRowEditSeconds);
      if (editBreakSeconds > 59) {
        editBreakMinutes += 1;
        editBreakSeconds = editBreakSeconds - 60;
      }
      if (editBreakMinutes >= 5) editBreakMinutes = 5;
      if (editBreakMinutes <= 0) editBreakMinutes = 0;
      if (editBreakSeconds <=0 || editBreakMinutes == 5) editBreakSeconds = 0;
      if (editBreakSeconds < 5 && editBreakMinutes == 0) editBreakSeconds = 0;

      breakValue = (int) ((editBreakMinutes * 60) + editBreakSeconds);
      secondRowTextView.setText(convertTimeToStringWithFullMinuteAndSecondValues(breakValue));
      //Sets value of editBreakMinutes to either breakValue, or breakOnlyValue, depending on which mode we're on.

      toastBounds(5, 300, setValue);
      toastBounds(5, 300, breakValue);
      if (setValue < 5) setValue = 5;
      if (breakValue < 5) breakValue = 5;
      if (setValue > 300) setValue = 300;
      if (breakValue > 300) breakValue = 300;
    } else if (mode==3) {
      editPomMinutesOne = convertEditTextToLong(firstRowEditMinutes);
      editPomSecondsOne = convertEditTextToLong(firstRowEditSeconds);
      editPomMinutesTwo = convertEditTextToLong(secondRowEditMinutes);
      editPomSecondsTwo = convertEditTextToLong(secondRowEditSeconds);
      editPomMinutesThree = convertEditTextToLong(thirdRowEditMinutes);
      editPomSecondsThree = convertEditTextToLong(thirdRowEditSeconds);
    }
    setTimerValueBounds();
  }

  //Sets min/max bounds on timer values. MUST be separate from setEditValues() so it can be called w/ in our +/- increment runnable and not b0rk the values.
  public void setTimerValueBounds() {
    switch (mode) {
      case 1:
        toastBounds(5, 300, setValue);
        toastBounds(5, 300, breakValue);
        if (setValue < 5) setValue = 5;
        if (breakValue < 5) breakValue = 5;
        if (setValue > 300) setValue = 300;
        if (breakValue > 300) breakValue = 300;
        break;
      case 3:
        toastBounds(900, 5400, pomValue1);
        toastBounds(180, 600, pomValue2);
        toastBounds(600, 1800, pomValue3);
        if (pomValue1 > 5400) pomValue1 = 5400;
        if (pomValue1 < 900) pomValue1 = 900;
        if (pomValue2 > 600) pomValue2 = 600;
        if (pomValue2 < 180) pomValue2 = 180;
        if (pomValue3 < 600) pomValue3 = 600;
        if (pomValue3 > 1800) pomValue3 = 1800;
        break;
    }
  }

  //Converts and sets our minute and second edit values from raw time values.
  public void convertEditTime(boolean setEditViews) {
    //Used to turn off editText listener's execution of setEditValues(), which overrides setValue and breakValue when trying to change them via +/- runnables, which call this function.
    activeEditListener = false;
    if (mode==1) {
      editSetSeconds = setValue % 60;
      editSetMinutes = setValue / 60;
      editBreakSeconds = breakValue % 60;
      editBreakMinutes = breakValue / 60;
      if (setEditViews) {
        firstRowEditMinutes.setText(String.valueOf(editSetMinutes));
        firstRowEditSeconds.setText(elongateEditSeconds(editSetSeconds));
        secondRowEditMinutes.setText(String.valueOf(editBreakMinutes));
        secondRowEditSeconds.setText(elongateEditSeconds(editBreakSeconds));
      }

    } else if (mode==3) {
      editPomSecondsOne = pomValue1 % 60;
      editPomMinutesOne = pomValue1 / 60;
      editPomSecondsTwo = pomValue2 % 60;
      editPomMinutesTwo = pomValue2 / 60;
      editPomSecondsThree = pomValue3 % 60;
      editPomMinutesThree = pomValue3 / 60;
      if (setEditViews) {
        firstRowEditMinutes.setText(String.valueOf(editPomMinutesOne));
        secondRowEditMinutes.setText(String.valueOf(editPomMinutesTwo));
        thirdRowEditMinutes.setText(String.valueOf(editPomMinutesThree));
        firstRowEditSeconds.setText(elongateEditSeconds(editPomSecondsOne));
        secondRowEditSeconds.setText(elongateEditSeconds(editPomSecondsTwo));
        thirdRowEditSeconds.setText(elongateEditSeconds(editPomSecondsThree));
      }
    }
    activeEditListener = true;
  }

  public long convertEditTextToLong(EditText editVar) {
    if (!editVar.getText().toString().equals("")) {
      return Long.parseLong(editVar.getText().toString());
    } else return 0;
  }

  //Function for switching between textView and editText in add/sub menu.
  public void editAndTextSwitch(boolean removeTextView, int viewRemoved) {
    convertEditTime(false);
    //If moving from textView -> editText.
    if (removeTextView) {
      if (viewRemoved == 1) {
        firstRowTextView.setVisibility(View.INVISIBLE);
        firstRowEditMinutes.setVisibility(View.VISIBLE);
        firstRowEditSeconds.setVisibility(View.VISIBLE);
        firstRowEditColon.setVisibility(View.VISIBLE);
        if (secondRowEditMinutes.isShown() || secondRowEditSeconds.isShown()) {
          secondRowTextView.setVisibility(View.VISIBLE);
          secondRowEditMinutes.setVisibility(View.GONE);
          secondRowEditSeconds.setVisibility(View.GONE);
          secondRowEditColon.setVisibility(View.GONE);
        }
        if (mode==3) {
          if (thirdRowEditMinutes.isShown()) {
            thirdRowEditMinutes.setVisibility(View.GONE);
            thirdRowEditSeconds.setVisibility(View.GONE);
            thirdRowEditColon.setVisibility(View.GONE);
            thirdRowEditTextView.setVisibility(View.VISIBLE);
          }
        }
        //Used to request focus, and then show the soft keyboard. Using x-axis values to determine whether we focus on minutes or seconds.
        if (editTextViewPosX<=75) {
          firstRowEditMinutes.requestFocus();
          inputMethodManager.showSoftInput(firstRowEditMinutes,  0);
        } else {
          firstRowEditSeconds.requestFocus();
          inputMethodManager.showSoftInput(firstRowEditSeconds, 0);
        }
      } else if (viewRemoved == 2) {
        secondRowTextView.setVisibility(View.INVISIBLE);
        secondRowEditMinutes.setVisibility(View.VISIBLE);
        secondRowEditSeconds.setVisibility(View.VISIBLE);
        secondRowEditColon.setVisibility(View.VISIBLE);
        if (firstRowEditMinutes.isShown() || firstRowEditSeconds.isShown()) {
          firstRowTextView.setVisibility(View.VISIBLE);
          firstRowEditMinutes.setVisibility(View.GONE);
          firstRowEditSeconds.setVisibility(View.GONE);
          firstRowEditColon.setVisibility(View.GONE);
        }
        if (mode==3) {
          if (thirdRowEditMinutes.isShown()) {
            thirdRowEditMinutes.setVisibility(View.GONE);
            thirdRowEditSeconds.setVisibility(View.GONE);
            thirdRowEditColon.setVisibility(View.GONE);
            thirdRowEditTextView.setVisibility(View.VISIBLE);
          }
        }
        if (editTextViewPosX<=75) {
          secondRowEditMinutes.requestFocus();
          inputMethodManager.showSoftInput(secondRowEditMinutes, 0);
        } else {
          secondRowEditSeconds.requestFocus();
          inputMethodManager.showSoftInput(secondRowEditSeconds, 0);
        }
      } else if (viewRemoved == 3) {
        if (firstRowEditMinutes.isShown()) {
          firstRowEditMinutes.setVisibility(View.GONE);
          firstRowEditSeconds.setVisibility(View.GONE);
          firstRowEditColon.setVisibility(View.GONE);
          firstRowTextView.setVisibility(View.VISIBLE);
        }
        if (secondRowEditMinutes.isShown()) {
          secondRowEditMinutes.setVisibility(View.GONE);
          secondRowEditSeconds.setVisibility(View.GONE);
          secondRowEditColon.setVisibility(View.GONE);
          secondRowTextView.setVisibility(View.VISIBLE);
        }
        thirdRowEditTextView.setVisibility(View.INVISIBLE);
        thirdRowEditMinutes.setVisibility(View.VISIBLE);
        thirdRowEditSeconds.setVisibility(View.VISIBLE);
        thirdRowEditColon.setVisibility(View.VISIBLE);
      }
      if (editTextViewPosX<=75) {
        thirdRowEditMinutes.requestFocus();
        inputMethodManager.showSoftInput(thirdRowEditMinutes, 0);
      } else {
        thirdRowEditSeconds.requestFocus();
        inputMethodManager.showSoftInput(thirdRowEditSeconds, 0);
      }
    } else {
      //If moving from editText -> textView.
      switch (mode) {
        case 1:
          if (viewRemoved == 1) {
            //If first is shown, second and separator are also shown.
            if (firstRowEditMinutes.isShown()) {
              firstRowEditMinutes.setVisibility(View.GONE);
              firstRowEditColon.setVisibility(View.GONE);
              firstRowEditSeconds.setVisibility(View.GONE);
              firstRowTextView.setVisibility(View.VISIBLE);
            }
          } else if (viewRemoved == 2) {
            if (secondRowEditMinutes.isShown()) {
              secondRowEditMinutes.setVisibility(View.GONE);
              secondRowEditColon.setVisibility(View.GONE);
              secondRowEditSeconds.setVisibility(View.GONE);
              secondRowTextView.setVisibility(View.VISIBLE);
            }
          }
          break;
        case 3:
          if (viewRemoved == 1) {
            if (firstRowEditMinutes.isShown()) {
              firstRowEditMinutes.setVisibility(View.GONE);
              firstRowEditSeconds.setVisibility(View.GONE);
              firstRowEditColon.setVisibility(View.GONE);
              firstRowTextView.setVisibility(View.VISIBLE);
            }
          } else if (viewRemoved == 2) {
            if (secondRowEditMinutes.isShown()) {
              secondRowEditMinutes.setVisibility(View.GONE);
              secondRowEditSeconds.setVisibility(View.GONE);
              secondRowEditColon.setVisibility(View.GONE);
              secondRowTextView.setVisibility(View.VISIBLE);
            }
          } else if (viewRemoved == 3) {
            if (thirdRowEditMinutes.isShown()) {
              thirdRowEditMinutes.setVisibility(View.GONE);
              thirdRowEditSeconds.setVisibility(View.GONE);
              thirdRowEditColon.setVisibility(View.GONE);
              thirdRowEditTextView.setVisibility(View.VISIBLE);
            }
          }
          break;
      }
    }
  }

  public void toastBounds(long min, long max, long value) {
    if (value < min) minReached = true;
    if (value > max) maxReached = true;
  }

  //Creates fade effect if reaching min/max of timer values.
  public void fadeCap(TextView textView) {
    if (minReached || maxReached) {
      minReached = false;
      maxReached = false;
      Animation fadeCap = new AlphaAnimation(1.0f, 0.3f);
      fadeCap.setDuration(350);
      textView.setAnimation(fadeCap);
    }
  }

  public String elongateEditSeconds(long seconds) {
    String temp = String.valueOf(seconds);
    if (temp.length()<2) temp = "0" + temp;
    return temp;
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
      if (totalSeconds != 5) return String.valueOf(totalSeconds);
      else return "5";
    }
  }
  //Conversion of Long-String for add/subtract popUp textViews. Slightly different format than our timer textViews.
  public String convertTimeToStringWithFullMinuteAndSecondValues(long totalSeconds) {
    DecimalFormat df = new DecimalFormat("00");
    long minutes;
    long remainingSeconds;
    minutes = totalSeconds / 60;

    remainingSeconds = totalSeconds % 60;
    if (totalSeconds >= 60) {
      String formattedSeconds = df.format(remainingSeconds);
      if (formattedSeconds.length() > 2) formattedSeconds = "0" + formattedSeconds;
      if (mode==1 || totalSeconds>=600) return (minutes + " : " + formattedSeconds);
      else return ("0" + minutes + " : " + formattedSeconds);
    } else {
      String totalStringSeconds = String.valueOf(totalSeconds);
      if (totalStringSeconds.length() < 2) totalStringSeconds = "0" + totalStringSeconds;
      if (totalSeconds < 5) return ("0 : 05");
      else return "0 : " + totalStringSeconds;
    }
  }

  public String convertTimeToStringWithFullMinuteAndSecondValuesWithoutSpaces(long totalSeconds) {
    DecimalFormat df = new DecimalFormat("00");
    long minutes;
    long remainingSeconds;
    minutes = totalSeconds / 60;

    remainingSeconds = totalSeconds % 60;
    if (totalSeconds >= 60) {
      String formattedSeconds = df.format(remainingSeconds);
      if (formattedSeconds.length() > 2) formattedSeconds = "0" + formattedSeconds;
      if (mode==1 || totalSeconds>=600) return (minutes + ":" + formattedSeconds);
      else return ("0" + minutes + ":" + formattedSeconds);
    } else {
      String totalStringSeconds = String.valueOf(totalSeconds);
      if (totalStringSeconds.length() < 2) totalStringSeconds = "0" + totalStringSeconds;
      return "0:" + totalStringSeconds;
    }
  }

  //Sets all of our editTexts to Invisible.
  public void removeEditTimerViews(boolean reinstateText) {
    firstRowEditMinutes.setVisibility(View.GONE);
    firstRowEditColon.setVisibility(View.GONE);
    firstRowEditMinutes.setVisibility(View.GONE);
    firstRowEditSeconds.setVisibility(View.GONE);
    secondRowEditMinutes.setVisibility(View.GONE);
    secondRowEditColon.setVisibility(View.GONE);
    secondRowEditSeconds.setVisibility(View.GONE);
    thirdRowEditMinutes.setVisibility(View.GONE);
    thirdRowEditColon.setVisibility(View.GONE);
    thirdRowEditSeconds.setVisibility(View.GONE);

    //Replaces editTexts w/ textViews if desired.
    if (reinstateText) {
      if (mode==1 || mode==3) firstRowTextView.setVisibility(View.VISIBLE);
      if (mode==3) thirdRowEditTextView.setVisibility(View.VISIBLE);
      secondRowTextView.setVisibility(View.VISIBLE);
    }
  }

  public void defaultEditRoundViews() {
    //Instance of layout objects we can set programmatically based on which mode we're on.
    ConstraintLayout.LayoutParams s2ParamsAdd = (ConstraintLayout.LayoutParams) secondRowAddButton.getLayoutParams();
    ConstraintLayout.LayoutParams s2ParamsSub = (ConstraintLayout.LayoutParams) secondRowSubtractButton.getLayoutParams();
    ConstraintLayout.LayoutParams addParams = (ConstraintLayout.LayoutParams) addRoundToCycleButton.getLayoutParams();
    ConstraintLayout.LayoutParams subParams = (ConstraintLayout.LayoutParams) SubtractRoundFromCycleButton.getLayoutParams();
    ConstraintLayout.LayoutParams roundRecyclerLayoutParams = (ConstraintLayout.LayoutParams) roundRecyclerLayout.getLayoutParams();

    convertEditTime(true);
    timeLeft.setText(timeLeftValueHolder);
    switch (mode) {
      case 1:
        roundRecyclerLayoutParams.height = convertScalablePixelsToDensity(260);
        //All shared visibilities between modes 1 and 2.
        s2.setVisibility(View.VISIBLE);
        s3.setVisibility(View.GONE);
        thirdRowEditTextView.setVisibility(View.INVISIBLE);
        thirdRowAddButton.setVisibility(View.INVISIBLE);
        thirdRowSubtractButton.setVisibility(View.INVISIBLE);
        secondRowAddButton.setVisibility(View.VISIBLE);
        secondRowSubtractButton.setVisibility(View.VISIBLE);
        toggleInfinityRoundsForSets.setVisibility(View.VISIBLE);
        toggleInfinityRoundsForBreaks.setVisibility(View.VISIBLE);
        secondRowTextView.setVisibility(View.VISIBLE);
        //Shared String between modes 1 and 2.
        s2.setText(R.string.break_time);
        s1.setVisibility(View.VISIBLE);
        firstRowTextView.setVisibility(View.VISIBLE);
        firstRowAddButton.setVisibility(View.VISIBLE);
        firstRowSubtractButton.setVisibility(View.VISIBLE);
        //Strings and values exclusive to mode 1.
        s1.setText(R.string.set_time);
        firstRowTextView.setText(convertTimeToStringWithFullMinuteAndSecondValues(setValue));
        secondRowTextView.setText(convertTimeToStringWithFullMinuteAndSecondValues(breakValue));
        addParams.bottomMargin = convertScalablePixelsToDensity(32);
        subParams.bottomMargin = convertScalablePixelsToDensity(32);
        roundRecyclerLayoutParams.bottomMargin = convertScalablePixelsToDensity(18);
        break;
      case 3:
        roundRecyclerLayoutParams.height = convertScalablePixelsToDensity(240);
        //Visibilities and values exclusive to mode 3.
        s1.setVisibility(View.VISIBLE);
        s3.setVisibility(View.VISIBLE);
        firstRowTextView.setVisibility(View.VISIBLE);
        firstRowAddButton.setVisibility(View.VISIBLE);
        firstRowSubtractButton.setVisibility(View.VISIBLE);
        secondRowTextView.setVisibility(View.VISIBLE);
        secondRowAddButton.setVisibility(View.VISIBLE);
        secondRowSubtractButton.setVisibility(View.VISIBLE);
        thirdRowEditTextView.setVisibility(View.VISIBLE);
        thirdRowAddButton.setVisibility(View.VISIBLE);
        thirdRowSubtractButton.setVisibility(View.VISIBLE);
        toggleInfinityRoundsForSets.setVisibility(View.GONE);
        toggleInfinityRoundsForBreaks.setVisibility(View.GONE);
        s1.setText(R.string.work_time);
        s2.setText(R.string.small_break);
        s3.setText(R.string.long_break);
        firstRowTextView.setText(convertTimeToStringWithFullMinuteAndSecondValues(pomValue1));
        secondRowTextView.setText(convertTimeToStringWithFullMinuteAndSecondValues(pomValue2));
        thirdRowEditTextView.setText(convertTimeToStringWithFullMinuteAndSecondValues(pomValue3));
        addParams.bottomMargin = convertScalablePixelsToDensity(20);
        subParams.bottomMargin = convertScalablePixelsToDensity(20);
        roundRecyclerLayoutParams.bottomMargin = convertScalablePixelsToDensity(10);
        break;
    }
  }

  public void adjustCustom(boolean adding) {
    //Hides soft keyboard by using a token of the current editCycleView.
    inputMethodManager.hideSoftInputFromWindow(editCyclesPopupView.getWindowToken(), 0);
    if (adding) {
      //Enables save button now that cycle values have changed, or disables it if rounds @ 0.
      if (!save_edit_cycle.isEnabled()) save_edit_cycle.setEnabled(true);
      if (save_edit_cycle.getAlpha()!=1) save_edit_cycle.setAlpha(1.0f);
      //Converts whatever we've entered as Strings in editText to long values for timer, and caps their values. Only necessary when adding a round.
      setEditValues();
      if (mode==1) {
        if (workoutTime.size()<16) {
          //If Sets are highlighted green, check if its infinity mode is also highlighted. Use 1/2 for yes/no.
          if (s1.getCurrentTextColor()==Color.GREEN)
            if (toggleInfinityRoundsForSets.getAlpha()!=1.0f) roundType = 1; else roundType = 2;
          //If Breaks are highlighted red, check if its infinity mode is also highlighted. Use 3/4 for yes/no.
          if (s2.getCurrentTextColor()==Color.RED)
            if (toggleInfinityRoundsForBreaks.getAlpha()!=1.0f) roundType = 3; else roundType = 4;
          //Adds OR replaces (depending on if a round is selected) values in both Integer (timer) Array and String (display) lists.
          switch (roundType) {
            case 1:
              addOrReplaceRounds(setValue, roundIsSelected);
              break;
            case 3:
              addOrReplaceRounds(breakValue, roundIsSelected);
              break;
            case 2: case 4:
              addOrReplaceRounds(0, roundIsSelected);
              break;
            default:
              //Returns from method so we don't add a roundType entry to our list, and the list stays in sync w/ the rounds we are actually adding.
              Toast.makeText(getApplicationContext(), "Nada for now!", Toast.LENGTH_SHORT).show();
              return;
          }
        } else{
          Toast.makeText(getApplicationContext(), "Full!", Toast.LENGTH_SHORT).show();
        }
      }
      if (mode==3) {
        if (pomValuesTime.size()==0) {
          for (int i = 0; i < 3; i++) {
            pomValuesTime.add(pomValue1 * 1000);
            pomValuesTime.add(pomValue2 * 1000);
          }
          pomValuesTime.add(pomValue1 * 1000);
          pomValuesTime.add(pomValue3 * 1000);
          for (int j=0; j<pomValuesTime.size(); j++) convertedPomList.add(convertSeconds(pomValuesTime.get(j)/1000));

          cycleRoundsAdapter.setPomFade(true);
          cycleRoundsAdapter.notifyDataSetChanged();
        } else {
          Toast.makeText(getApplicationContext(), "Pomodoro cycle already loaded!", Toast.LENGTH_SHORT).show();
        }
      }
    } else {
      if (mode==1) {
        if (workoutTime.size()>1) {
          if (!save_edit_cycle.isEnabled()) save_edit_cycle.setEnabled(true);
          if (save_edit_cycle.getAlpha()!=1) save_edit_cycle.setAlpha(1.0f);
        } else {
          if (save_edit_cycle.isEnabled()) save_edit_cycle.setEnabled(false);
          if (save_edit_cycle.getAlpha()==1) save_edit_cycle.setAlpha(0.3f);
        }
        //removeRound is called at end of fade set below. Here, we overwrite that and remove it beforehand if user clicks before fade is done.
        if (subtractedRoundIsFading) removeRound();

        if (workoutTime.size()>0) {
          //If round is not selected, default subtraction to latest round entry. Otherwise, keep the selected position.
          if (!roundIsSelected) roundSelectedPosition = workoutTime.size()-1;
          if (roundSelectedPosition<=7) {
            //Sets fade positions for rounds. Most recent for subtraction, and -1 (out of bounds) for addition.
            cycleRoundsAdapter.setFadePositions(roundSelectedPosition, -1);
            cycleRoundsAdapter.notifyDataSetChanged();
          } else {
            //Sets fade positions for rounds. Most recent for subtraction, and -1 (out of bounds) for addition.
            cycleRoundsAdapterTwo.setFadePositions(roundSelectedPosition-8, -1);
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
          SubtractRoundFromCycleButton.setClickable(false);
        } else Toast.makeText(getApplicationContext(), "No Pomodoro cycle to clear!", Toast.LENGTH_SHORT).show();
      }
    }
    //Resets round selection boolean.
    if (roundIsSelected) consolidateRoundAdapterLists = true;
    roundIsSelected = false;
  }

  public void addOrReplaceRounds(int integerValue, boolean replacingValue) {
    if (mode==1) {
      //If adding a round.
      if (!replacingValue) {
        workoutTime.add(integerValue * 1000);
        convertedWorkoutTime.add(convertSeconds(integerValue));
        typeOfRound.add(roundType);
        //Adds and sends to adapter the newest addition round position to fade.
        if (workoutTime.size()<=8) {
          roundHolderOne.add(convertedWorkoutTime.get(convertedWorkoutTime.size()-1));
          typeHolderOne.add(typeOfRound.get(typeOfRound.size()-1));
          cycleRoundsAdapter.setFadePositions(-1, workoutTime.size()-1);
          cycleRoundsAdapter.notifyDataSetChanged();
        } else {
          roundHolderTwo.add(convertedWorkoutTime.get(convertedWorkoutTime.size()-1));
          typeHolderTwo.add(typeOfRound.get(typeOfRound.size()-1));
          cycleRoundsAdapterTwo.setFadePositions(-1, workoutTime.size()-9);
          cycleRoundsAdapterTwo.notifyDataSetChanged();
        }
        //If moving from one list to two, set its visibility and change layout params. Only necessary if adding a round.
        if (workoutTime.size()==9) {
          roundRecyclerTwo.setVisibility(View.VISIBLE);
          recyclerLayoutOne.leftMargin = 5;
          roundListDivider.setVisibility(View.VISIBLE);
        }
        //if replacing a round. Done via add button. Subtract will always subtract.
      } else {
        //Replaces and sends to adapter that replaced position to fade.
        workoutTime.set(roundSelectedPosition, integerValue*1000);
        convertedWorkoutTime.set(roundSelectedPosition, convertSeconds(integerValue));
        typeOfRound.set(roundSelectedPosition, roundType);
        if (roundSelectedPosition<=7) {
          roundHolderOne.set(roundSelectedPosition, convertedWorkoutTime.get(roundSelectedPosition));
          typeHolderOne.set(roundSelectedPosition, typeOfRound.get(roundSelectedPosition));
          cycleRoundsAdapter.setFadePositions(-1, roundSelectedPosition);
          cycleRoundsAdapter.notifyDataSetChanged();
        } else {

          //Since our workOutTime lists are independent of adapter and run from (up to) 0-15, we change the value of roundSelectedPosition back to original.
          roundHolderTwo.set(roundSelectedPosition-8, convertedWorkoutTime.get(roundSelectedPosition));
          typeHolderTwo.set(roundSelectedPosition-8, typeOfRound.get(roundSelectedPosition));
          cycleRoundsAdapterTwo.setFadePositions(-1, roundSelectedPosition-8);
          cycleRoundsAdapterTwo.notifyDataSetChanged();
        }
      }
    }
  }

  public void removeRound () {
    //Rounds only get removed once fade is finished, which we receive status of from callback in adapter.
    if (mode==1) {
      if (workoutTime.size()>0) {
        //If round is not selected, default subtraction to latest round entry. Otherwise, keep the selected position. Needs to be declared here and in adjustCustom() to avoid occasional index errors w/ fase clicking.
        if (!roundIsSelected) roundSelectedPosition = workoutTime.size()-1;
        if (workoutTime.size()-1>=roundSelectedPosition) {
          //If workoutTime list has 8 or less items, or a round is selected and its position is in that first 8 items, remove item from first adapter.
          if (workoutTime.size()<=8 || (roundIsSelected && roundSelectedPosition<=7)) {
            if (roundHolderOne.size()-1>=roundSelectedPosition) {
              roundHolderOne.remove(roundSelectedPosition);
              typeHolderOne.remove(roundSelectedPosition);
              //Sets fade positions for rounds. -1 (out of bounds) for both, since this adapter refresh simply calls the post-fade listing of current rounds.
              cycleRoundsAdapter.setFadePositions(-1, -1);
              cycleRoundsAdapter.notifyDataSetChanged();
            }
          } else {
            if (roundHolderTwo.size()-1>=roundSelectedPosition-8) {
              roundHolderTwo.remove(roundSelectedPosition-8);
              typeHolderTwo.remove(roundSelectedPosition-8);
              //Sets fade positions for rounds. -1 (out of bounds) for both, since this adapter refresh simply calls the post-fade listing of current rounds.
              cycleRoundsAdapterTwo.setFadePositions(-1,  -1);
              cycleRoundsAdapterTwo.notifyDataSetChanged();
            }
          }

          typeOfRound.remove(roundSelectedPosition);
          workoutTime.remove(roundSelectedPosition);
          convertedWorkoutTime.remove(roundSelectedPosition);
          //If moving from two lists to one, set its visibility and change layout params.
          if (workoutTime.size()==8) {
            roundRecyclerTwo.setVisibility(View.GONE);
            recyclerLayoutOne.leftMargin = 240;
            roundListDivider.setVisibility(View.GONE);
          }
          //Once a round has been removed (and shown as such) in our recyclerView, we always allow for a new fade animation (for the next one).
          subtractedRoundIsFading = false;
        }
      } else Toast.makeText(getApplicationContext(), "Empty!", Toast.LENGTH_SHORT).show();
    }
  }

  public String friendlyString(String altString) {
    altString = altString.replace("\"", "");
    altString = altString.replace("]", "");
    altString = altString.replace("[", "");
    altString = altString.replace(",", " - ");
    return altString;
  }

  public void clearAndRepopulateCycleAdapterListsFromDatabaseObject(boolean forAllModes) {
    if (mode==1 || forAllModes) {
      workoutCyclesArray.clear();
      typeOfRoundArray.clear();
      workoutTitleArray.clear();
      for (int i=0; i<cyclesList.size(); i++) {
        //Adds the concatenated timer String used in each cycle (e.g. XX - XX - XX) to the String Array that was pass into our cycle list's adapter.
        workoutCyclesArray.add(cyclesList.get(i).getWorkoutRounds());
        //Retrieves title for use when editing a cycle.
        workoutTitleArray.add(cyclesList.get(i).getTitle());
        //Adds concatenated roundType String used in each cycle.
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

  //Since we are only updating our adapter's lists, we do not need to reference variables not shown in list (i.e. total times/total cycles). We will only update these in database if they change.
  public void editCycleArrayLists(int action) {
    if (action == ADDING_CYCLE) {
      if (mode==1) {
        //If we are adding a new cycle, no need to query the DB for values after save. Just use what has been passed into them from Arrays. This will add them as the correct last position.
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
        //If we have the values in our workOutTime and typeOFRound arrays already, simply pass them into cycleList's adapter arrays.
        workoutTitleArray.set(positionOfSelectedCycle, cycleTitle);
        workoutCyclesArray.set(positionOfSelectedCycle, workoutString);
        typeOfRoundArray.set(positionOfSelectedCycle, roundTypeString);
      }
      if (mode==3) {
        pomTitleArray.set(positionOfSelectedCycle, cycleTitle);
        pomArray.set(positionOfSelectedCycle, pomString);
      }
    } else if (action == DELETING_CYCLE) {
      if (mode==1) {
        workoutTitleArray.remove(positionOfSelectedCycle);
        typeOfRoundArray.remove(positionOfSelectedCycle);
        workoutCyclesArray.remove(positionOfSelectedCycle);
      }
      if (mode==3) {
        pomTitleArray.remove(positionOfSelectedCycle);
        pomArray.remove(positionOfSelectedCycle);
      }
    }
  }

  //Populates round list from single cycle.
  public void populateRoundList() {
    switch (mode) {
      case 1:
        //Clears the two lists of actual timer values we are populating.
        workoutTime.clear();
        typeOfRound.clear();

        if (workoutCyclesArray.size()-1>=positionOfSelectedCycle) {
          //Populating our current cycle's list of rounds via Integer Arrays directly with adapter String list values, instead of querying them from the database. This is used whenever we do not need a db query, such as editing or adding a new cycle.
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
          for (int i=0; i<8; i++) if (i%2!=0) pomValuesTime.add(8000); else pomValuesTime.add(10000);
          //          for (int i=0; i<fetchedPomCycle.length; i++) pomValuesTime.add(Integer.parseInt(fetchedPomCycle[i]));
        }
        break;
    }
  }

  public void launchTimerCycle(boolean saveToDB) {
    //Todo: Watch this. Meant to always reset current timer obj / obj animator if launching new cycle.
    resetTimer();
    if (isNewCycle || currentlyEditingACycle) resetTimer();
    populateTimerUI();
    AsyncTask.execute(queryAllCyclesFromDatabaseRunnableAndRetrieveTotalTimes);
    //Used to toggle views/updates on Main for visually smooth transitions between popUps.
    makeCycleAdapterVisible = true;
    //If trying to add new cycle and rounds are at 0, pop a toast and exit method. Otherwise, set a title and proceed to intents.
    if ((mode==1 && workoutTime.size()==0) || (mode==3 && pomValuesTime.size()==0)) {
      Toast.makeText(getApplicationContext(), "Cycle cannot be empty!", Toast.LENGTH_SHORT).show();
      return;
    }
    if (isNewCycle || saveToDB) AsyncTask.execute(saveCyclesASyncRunnable);
    editCyclesPopupWindow.dismiss();
    timerPopUpWindow.showAtLocation(cl, Gravity.NO_GRAVITY, 0, 0);
  }

  public void instantiateAndStartObjectAnimator(long duration) {
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

  //Controls each mode's object animator. Starts new or resumes current one.
  public void startObjectAnimator() {

    switch (mode) {
      case 1:
        if (typeOfRound.get(currentRound).equals(1)) {
          //If progress bar is at max value, round has not begun.
          if (currentProgressBarValue==maxProgress) {
            //Starts object animator.
            instantiateAndStartObjectAnimator(setMillis);
            //Used for pause/resume toggle.
            timerIsPaused = false;
            //Used in pause/resume. If timer HAS ended, that method calls reset. Otherwise, it pauses or resumes timer.
            timerEnded = false;
          } else {
            setMillis = setMillisUntilFinished;
            if (objectAnimator != null) objectAnimator.resume();
          }
        } else if (typeOfRound.get(currentRound).equals(3)) {
          //Todo: HERE. Progress is at 0 so breakMillis is set to breakMillisUntilFinished, which is also 0.
          if (currentProgressBarValue==maxProgress) {
            //Used for pause/resume and fading text in (i.e. timeLeft or timePaused).
            timerIsPaused = false;
            //Starts object animator.
            instantiateAndStartObjectAnimator(breakMillis);
          } else {
            breakMillis = breakMillisUntilFinished;
            if (objectAnimator != null) objectAnimator.resume();
          }
        }
        break;
      case 3:
        if (currentProgressBarValue==maxProgress){
          //Ensures any features meant for running timer cannot be executed here.
          timerIsPaused = false;
          pomMillis = pomValuesTime.get(pomDotCounter);
          //Starts object animator.
          instantiateAndStartObjectAnimator(pomMillis);
          timerEnded = false;
        } else {
          pomMillis = pomMillisUntilFinished;
          if (objectAnimatorPom != null) objectAnimatorPom.resume();
        }
        break;
    }
  }


  public void startSetTimer() {
    setInitialTextSizeForRounds(setMillis);

    timer = new CountDownTimer(setMillis, 50) {
      @Override
      public void onTick(long millisUntilFinished) {

        if (!notificationDismissed) {
          setNotificationValues();
        }

        currentProgressBarValue = (int) objectAnimator.getAnimatedValue();
        setMillis = millisUntilFinished;
        total_set_time.setText(stringValueOfTotalCycleTime(1));
        timeLeft.setText(convertSeconds((setMillis + 1000) / 1000));

        if (textSizeIncreased && mode==1) {
          if (setMillis < 59000) {
            changeTextSize(valueAnimatorUp, timeLeft);
            textSizeIncreased = false;
          }
        }
        if (setMillis < 500) timerDisabled = true;
        dotDraws.reDraw();
      }

      @Override
      public void onFinish() {
        nextRound(false);
      }
    }.start();
  }

  public void startBreakTimer() {
    setInitialTextSizeForRounds(breakMillis);

    timer = new CountDownTimer(breakMillis, 50) {
      @Override
      public void onTick(long millisUntilFinished) {

        if (!notificationDismissed) {
          setNotificationValues();
        }

        currentProgressBarValue = (int) objectAnimator.getAnimatedValue();
        breakMillis = millisUntilFinished;
        total_break_time.setText(stringValueOfTotalCycleTime(3));
        timeLeft.setText(convertSeconds((millisUntilFinished + 1000) / 1000));

        if (textSizeIncreased && mode==1) {
          if (breakMillis < 59000) {
            changeTextSize(valueAnimatorUp, timeLeft);
            textSizeIncreased = false;
          }
        }

        if (breakMillis < 500) timerDisabled = true;
        dotDraws.reDraw();
      }

      @Override
      public void onFinish() {
        nextRound(false);
      }
    }.start();
  }

  public void startPomTimer() {
    setInitialTextSizeForRounds(pomMillis);

    timer = new CountDownTimer(pomMillis, 50) {
      @Override
      public void onTick(long millisUntilFinished) {
        if (!notificationDismissed) {
          setNotificationValues();
        }

        currentProgressBarValue = (int) objectAnimatorPom.getAnimatedValue();
        pomMillis = millisUntilFinished;

        if (textSizeIncreased && mode==3) {
          if (pomMillis < 59000) {
            changeTextSize(valueAnimatorUp, timeLeft);
            textSizeIncreased = false;
          }
        }

        timeLeft.setText(convertSeconds((pomMillis + 1000) / 1000));
        if (pomMillis < 500) timerDisabled = true;
        dotDraws.reDraw();
      }

      @Override
      public void onFinish() {
        nextRound(false);
      }
    }.start();
  }

  public String stringValueOfTotalCycleTime(int roundType) {
    String addedTime = "";
    if (mode==1) {
      switch (roundType) {
        case 1:
          if (resettingTotalTime) {
            roundedValueForTotalTimes = 1000 - (setMillis%1000);
            cycleSetTimeForSingleRoundInMillis = roundedValueForTotalTimes;
            resettingTotalTime = false;
          }
          cycleSetTimeForSingleRoundInMillis +=50;
          addedTime = convertSeconds((totalCycleSetTimeInMillis + cycleSetTimeForSingleRoundInMillis) / 1000);
          break;
        case 3:
          if (resettingTotalTime) {
            roundedValueForTotalTimes = 1000 - (breakMillis%1000);
            cycleBreakTimeForSingleRoundInMillis = roundedValueForTotalTimes;
            resettingTotalTime = false;
          }
          cycleBreakTimeForSingleRoundInMillis+=50;
          addedTime = convertSeconds((totalCycleBreakTimeInMillis + cycleBreakTimeForSingleRoundInMillis) / 1000);
          break;
        case 2:
          if (resettingTotalTime) {
            countUpMillisSets = 0;
            resettingTotalTime = false;
          }
          cycleSetTimeForSingleRoundInMillis = countUpMillisSets;
          addedTime = convertSeconds((totalCycleSetTimeInMillis + cycleSetTimeForSingleRoundInMillis) / 1000);
          break;
        case 4:
          if (resettingTotalTime) {
            countUpMillisBreaks = 0;
            resettingTotalTime = false;
          }
          cycleBreakTimeForSingleRoundInMillis = countUpMillisBreaks;
          addedTime = convertSeconds((totalCycleBreakTimeInMillis + cycleBreakTimeForSingleRoundInMillis) / 1000);
          break;
      }
    }

    if (mode==3) {
      switch (pomDotCounter) {
        case 0: case 2: case 4: case 6:
          if (resettingTotalTime) {
            roundedValueForTotalTimes = 1000 - (setMillis%1000);
            cycleSetTimeForSingleRoundInMillis = roundedValueForTotalTimes;
            resettingTotalTime = false;
          }
          cycleSetTimeForSingleRoundInMillis+=50;
          addedTime = convertSeconds((totalCycleSetTimeInMillis + cycleSetTimeForSingleRoundInMillis) / 1000);
          break;
        case 1: case 3: case 5: case 7:
          if (resettingTotalTime) {
            roundedValueForTotalTimes = 1000 - (breakMillis%1000);
            cycleBreakTimeForSingleRoundInMillis = roundedValueForTotalTimes;
            resettingTotalTime = false;
          }
          cycleBreakTimeForSingleRoundInMillis+=50;
          addedTime = convertSeconds((totalCycleBreakTimeInMillis + cycleBreakTimeForSingleRoundInMillis) / 1000);
          break;
      }
    }
    return addedTime;
  }

  public void addAndRoundDownTotalCycleTimeFromPreviousRounds(boolean roundSecondsUp) {
    if (mode==1) {
      switch (typeOfRound.get(currentRound)) {
        case 1: case 2:
          totalCycleSetTimeInMillis = totalCycleSetTimeInMillis + cycleSetTimeForSingleRoundInMillis;
          long setRemainder = totalCycleSetTimeInMillis%1000;
          if (!roundSecondsUp) totalCycleSetTimeInMillis = totalCycleSetTimeInMillis - setRemainder;
          else totalCycleSetTimeInMillis = totalCycleSetTimeInMillis + (1000 - setRemainder);
          cycleSetTimeForSingleRoundInMillis = 0;
          break;
        case 3: case 4:
          totalCycleBreakTimeInMillis = totalCycleBreakTimeInMillis + cycleBreakTimeForSingleRoundInMillis;
          long breakRemainder = totalCycleBreakTimeInMillis%1000;
          if (!roundSecondsUp) totalCycleBreakTimeInMillis = totalCycleBreakTimeInMillis - breakRemainder;
          else totalCycleBreakTimeInMillis = totalCycleBreakTimeInMillis + (1000 - breakRemainder);
          cycleBreakTimeForSingleRoundInMillis = 0;
          break;
      }
    }
    if (mode==3) {
      switch (pomDotCounter) {
        case 0: case 2: case 4: case 6:
          totalCycleSetTimeInMillis = totalCycleSetTimeInMillis + cycleSetTimeForSingleRoundInMillis;
          long setRemainder = totalCycleSetTimeInMillis%1000;
          if (!roundSecondsUp) totalCycleSetTimeInMillis = totalCycleSetTimeInMillis - setRemainder;
          else totalCycleSetTimeInMillis = totalCycleSetTimeInMillis + (1000 - setRemainder);
          break;
        case 1: case 3: case 5: case 7:
          totalCycleBreakTimeInMillis = totalCycleBreakTimeInMillis + cycleBreakTimeForSingleRoundInMillis;
          long breakRemainder = totalCycleBreakTimeInMillis%1000;
          if (!roundSecondsUp) totalCycleBreakTimeInMillis = totalCycleBreakTimeInMillis - breakRemainder;
          else totalCycleBreakTimeInMillis = totalCycleBreakTimeInMillis + (1000 - breakRemainder);
          break;
      }
    }
  }

  //Set to true if we want to run the animation instantly. False if it is timer dependant, since we do not want it triggering on the wrong prog/timer.
  private void animateEnding() {
    endAnimation = new AlphaAnimation(1.0f, 0.0f);
    endAnimation.setDuration(300);
    endAnimation.setStartOffset(0);
    endAnimation.setRepeatMode(Animation.REVERSE);
    endAnimation.setRepeatCount(Animation.INFINITE);
    progressBar.startAnimation(endAnimation);
    timeLeft.startAnimation(endAnimation);
  }

  //Sets text size at round start. textSizeIncreased is set to true if timer is >=60 seconds, so the text size can be reduced mid-timer as it drops below.
  public void setInitialTextSizeForRounds(long millis) {
    if (millis>=59000) {
      if (mode==1 || mode==3) {
        timeLeft.setTextSize(70f);
        textSizeIncreased = true;
      }
    } else {
      timeLeft.setTextSize(90f);
      if (mode==4) textSizeIncreased = true;

    }
  }
  public void changeTextSize(ValueAnimator va, TextView textView) {
    sizeAnimator = va;
    sizeAnimator.addUpdateListener(animation -> {
      float sizeChange = (float) va.getAnimatedValue();
      textView.setTextSize(sizeChange);
    });
    sizeAnimator.setRepeatCount(0);
    sizeAnimator.start();
  }

  //Used in count up mode to animate text size changes in our runnables.
  public void reduceTextSizeForInfinityRounds(long millis) {
    if (textSizeReduced) {
      if (millis >=60000) {
        changeTextSize(valueAnimatorDown, timeLeft);
        textSizeReduced = false;
      }
    }
  }

  public void newLap() {
    if (empty_laps.getVisibility()==View.VISIBLE) empty_laps.setVisibility(View.INVISIBLE);
    double newSeconds = msReset / 60;
    double newMinutes = newSeconds / 60;

    double savedMinutes = 0;
    double savedSeconds = 0;
    double savedMs = 0;

    String[] holder = null;
    if (!stopWatchIsPaused) {
      if (savedLapList.size() > 0) {
        holder = (savedLapList.get(savedLapList.size() - 1).split(":", 3));
        savedMinutes = newMinutes + Integer.parseInt(holder[0]);
        savedSeconds = newSeconds + Integer.parseInt(holder[1]);
        savedMs = newMsConvert + Integer.parseInt(holder[2]);

        if (savedMs > 99) {
          savedMs = savedMs - 100;
          savedSeconds += 1;
        }
        if (savedSeconds > 99) {
          savedSeconds = savedSeconds - 100;
          savedMinutes += 1;
        }
        savedEntries = String.format(Locale.getDefault(), "%02d:%02d:%02d", (int) savedMinutes, (int) savedSeconds, (int) savedMs);
      } else
        savedEntries = String.format(Locale.getDefault(), "%02d:%02d:%02d", (int) minutes, (int) seconds, savedMsConvert);

      newEntries = String.format(Locale.getDefault(), "%02d:%02d:%02d", (int) newMinutes, (int) newSeconds, newMsConvert);

      currentLapList.add(newEntries);
      savedLapList.add(savedEntries);
      lapLayout.scrollToPosition(savedLapList.size() - 1);
      lapAdapter.notifyDataSetChanged();

      lapsNumber++;
      cycles_completed.setText(getString(R.string.laps_completed, String.valueOf(lapsNumber)));

      msReset = 0;
      msConvert2 = 0;
    }
  }

  public void nextRound(boolean endingEarly) {
    //Fade effect to smooth out progressBar and timer text after animation.
    progressBar.startAnimation(fadeProgressOut);
    timeLeft.startAnimation(fadeProgressOut);
    //Retains our progressBar's value between modes. Also determines whether we are starting or resuming an object animator (in startObjectAnimator()).
    currentProgressBarValue = 10000;
    //If no rounds left, remove our endFade runnable, reset timer, and return before executing anything else. The button tied to this method will be disabled until the proper rounded subtraction can occur.
    if (mode==1) {
      if (numberOfRoundsLeft==0) {
        //Triggers in same runnable that knocks our round count down - so it must be canceled here.
        mHandler.removeCallbacks(endFade);
        resetTimer();
        return;
      }
      ///---Executes for all Modes---///
      //Always starting new rounds active, so reset button will not be available.
      reset.setVisibility(View.INVISIBLE);
      //Disables button that calls this method so it doesn't execute twice.
      next_round.setEnabled(false);
      //Disables pause/resume button.
      timerDisabled = true;
      //Resets default base (30 sec) for count-up rounds.
      currentProgressBarValueForInfinityRounds = 30000;
      //Fade out effect for dots so they always end their fade @ 105 alpha (same alpha they retain once completed).
      mHandler.post(endFade);

      //If skipping round manually, cancel timer and objectAnimator.
      if (endingEarly) {
        if (timer != null) timer.cancel();
        if (objectAnimator != null) objectAnimator.cancel();
        progressBar.setProgress(0);
        addAndRoundDownTotalCycleTimeFromPreviousRounds(false);
      } else addAndRoundDownTotalCycleTimeFromPreviousRounds(true);
      AsyncTask.execute(updateTotalTimesInDatabaseRunnable);

      switch (typeOfRound.get(currentRound)) {
        case 1:
          timeLeft.setText("0");
          total_set_time.setText(convertSeconds(totalCycleSetTimeInMillis/1000));
          break;
        case 2:
          mHandler.removeCallbacks(infinityRunnableForSets);
          total_set_time.setText(convertSeconds(totalCycleSetTimeInMillis/1000));
          break;
        case 3:
          timeLeft.setText("0");
          total_break_time.setText(convertSeconds(totalCycleBreakTimeInMillis/1000));
          break;
        case 4:
          mHandler.removeCallbacks(infinityRunnableForBreaks);
          total_break_time.setText(convertSeconds(totalCycleBreakTimeInMillis/1000));
          break;
      }
      //Subtracts from rounds remaining.
      numberOfRoundsLeft--;
      //Iterates up in our current round count. This is used to determine which type of round will execute next (below).
      currentRound++;
      mHandler.postDelayed(postRoundRunnableForFirstMode, 750);
      //Ensures subsequent rounds will start automatically.
      beginTimerForNextRound = true;
    }
    if (mode==3) {
      timeLeft.setText("0");
      mHandler.post(endFade);
      pomDotCounter++;
      //If skipping round manually, cancel timer and objectAnimator.
      if (endingEarly) {
        if (timer != null) timer.cancel();
        if (objectAnimatorPom != null) objectAnimatorPom.cancel();
        progressBar.setProgress(0);
        addAndRoundDownTotalCycleTimeFromPreviousRounds(false);
      } else addAndRoundDownTotalCycleTimeFromPreviousRounds(true);
      mHandler.postDelayed(postRoundRunnableForThirdMode, 750);
    }
    beginTimerForNextRound = true;
  }

  public void pauseAndResumeTimer(int pausing) {
    if (!timerDisabled) {
      if (fadeInObj != null) fadeInObj.cancel();
      if (fadeOutObj != null) fadeOutObj.cancel();
      switch (mode) {
        case 1:
          if (!timerEnded) {
            if (pausing == PAUSING_TIMER) {
              timerIsPaused = true;
              if (timer != null) timer.cancel();
              if (objectAnimator != null) objectAnimator.pause();
              reset.setVisibility(View.VISIBLE);

              switch (typeOfRound.get(currentRound)) {
                case 1:
                  setMillisUntilFinished = setMillis;
                  break;
                case 2:
                  countUpMillisHolder = countUpMillisSets;
                  mHandler.removeCallbacks(infinityRunnableForSets);
                  break;
                case 3:
                  breakMillisUntilFinished = breakMillis;
                  break;
                case 4:
                  countUpMillisHolder = countUpMillisBreaks;
                  mHandler.removeCallbacks(infinityRunnableForBreaks);
                  break;
              }
            } else if (pausing == RESUMING_TIMER) {
              reset.setVisibility(View.INVISIBLE);
              timerIsPaused = false;

              switch (typeOfRound.get(currentRound)) {
                case 1:
                  startObjectAnimator();
                  startSetTimer();
                  break;
                case 2:
                  //Todo: Watch currentProgressBarValue here and case 4.
                  //Uses the current time as a base for our count-up rounds.
                  defaultProgressBarDurationForInfinityRounds = System.currentTimeMillis();
                  if (currentProgressBarValue==maxProgress) instantiateAndStartObjectAnimator(currentProgressBarValueForInfinityRounds); else if (objectAnimator!=null) objectAnimator.resume();
                  countUpMillisSets = countUpMillisHolder;
                  mHandler.post(infinityRunnableForSets);
                  break;
                case 3:
                  startObjectAnimator();
                  startBreakTimer();
                  break;
                case 4:
                  defaultProgressBarDurationForInfinityRounds = System.currentTimeMillis();
                  if (currentProgressBarValue==maxProgress) instantiateAndStartObjectAnimator(5000); else if (objectAnimator!=null) objectAnimator.resume();
                  countUpMillisBreaks = countUpMillisHolder;
                  mHandler.post(infinityRunnableForBreaks);
                  break;
              }
            }
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
              reset.setVisibility(View.INVISIBLE);
              timerIsPaused = false;
              startObjectAnimator();
              startPomTimer();
            }
          } else resetTimer();
          break;
        case 4:
          if (fadeInObj != null) fadeInObj.cancel();
          if (pausing == RESUMING_TIMER) {
            reset.setVisibility(View.INVISIBLE);
            stopWatchIsPaused = false;
            new_lap.setAlpha(1.0f);
            new_lap.setEnabled(true);
            //Main runnable for Stopwatch.
            mHandler.post(stopWatchRunnable);
          } else if (pausing == PAUSING_TIMER) {
            reset.setVisibility(View.VISIBLE);
            mHandler.removeCallbacksAndMessages(null);
            stopWatchIsPaused = true;
            new_lap.setAlpha(0.3f);
            new_lap.setEnabled(false);
            break;
          }
      }
    }
  }

  public void populateTimerUI() {
    lapListCanvas.setMode(mode);
    beginTimerForNextRound = true;
    cycles_completed.setText(R.string.cycles_done);
    cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(cyclesCompleted)));

    blankCanvas.setVisibility(View.GONE);
    cycle_title_textView.setText(cycleTitle);
    dotDraws.resetDotAlpha();
    //Default views for Timer.
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
    cycle_title_layout.topMargin = convertDensityPixelsToScalable(30);
    completedLapsParam.topMargin = convertDensityPixelsToScalable(24);

    //Setting values based on first round in cycle. Might make this is a global method.
    switch (mode) {
      case 1:
        //Resets base progressBar duration for count-up rounds.
        currentProgressBarValueForInfinityRounds = 30000;
        //Sets the long value in our "count up" rounds back to 0.
        for (int i=0; i<workoutTime.size(); i++) {
          if (typeOfRound.get(i)==2 || typeOfRound.get(i)==4) workoutTime.set(i, 0);
        }
        //Sets timer values and round counts. populateTimerUI() is called at app startup and when resetting timer, so this handles both.
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
              timeLeft.setText("0");
              setInitialTextSizeForRounds(0);
              break;
          }
        }
        break;
      case 3:
        //Here is where we set the initial millis Value  of first pomMillis. Set again on change on our value runnables.
        if (pomValuesTime.size() > 0) {
          pomMillis = pomValuesTime.get(0);
          timeLeft.setText(convertSeconds((pomMillis + 999) / 1000));
          dotDraws.pomDraw(pomDotCounter,pomValuesTime);
          //Sets initial text size.
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
        completedLapsParam.topMargin = 0;
        cycle_title_layout.topMargin = -25;

        timerDisabled = false;
        timerPopUpWindow.showAtLocation(cl, Gravity.NO_GRAVITY, 0, 0);
        cycle_title_textView.setVisibility(View.INVISIBLE);
        if (stopWatchIsPaused) reset.setVisibility(View.VISIBLE); else reset.setVisibility(View.INVISIBLE);
        blankCanvas.setVisibility(View.VISIBLE);
        break;
    }
  }

  public void resetTimer() {
    timerIsPaused = true;
    timerEnded = false;
    reset.setVisibility(View.INVISIBLE);
    progressBar.setProgress(10000);
    if (timer != null) timer.cancel();
    if (endAnimation!=null) endAnimation.cancel();
    next_round.setEnabled(true);
    currentProgressBarValue = 10000;
    addAndRoundDownTotalCycleTimeFromPreviousRounds(false);
    AsyncTask.execute(updateTotalTimesInDatabaseRunnable);
    switch (mode) {
      case 1:
        //Resetting millis values of count up mode to 0.
        countUpMillisSets = 0;
        countUpMillisBreaks = 0;
        countUpMillisHolder = 0;
        //Removing timer callbacks for count up mode.
        mHandler.removeCallbacks(infinityRunnableForSets);
        mHandler.removeCallbacks(infinityRunnableForBreaks);
        //Setting total number of rounds.
        startRounds = workoutTime.size();
        numberOfRoundsLeft = startRounds;
        //Resets current round counter.
        currentRound = 0;
        if (objectAnimator != null) objectAnimator.cancel();
        break;
      case 3:
        pomDotCounter = 0;
        if (objectAnimatorPom != null) objectAnimatorPom.cancel();
        break;
      case 4:
        ms = 0;
        msConvert = 0;
        msConvert2 = 0;
        msDisplay = 0;
        msReset = 0;
        seconds = 0;
        minutes = 0;
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
        break;
    }
    //Base of 0 values for stopwatch means we don't want anything populated when resetting.
    if (mode!=4) populateTimerUI();
  }
}