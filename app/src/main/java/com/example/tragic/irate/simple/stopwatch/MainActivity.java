package com.example.tragic.irate.simple.stopwatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableRow;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings({"depreciation"})
public class MainActivity extends AppCompatActivity implements SavedCycleAdapter.onCycleClickListener, SavedCycleAdapter.onHighlightListener, SavedPomCycleAdapter.onCycleClickListener, SavedPomCycleAdapter.onHighlightListener, CycleRoundsAdapter.onFadeFinished, CycleRoundsAdapterTwo.onFadeFinished, CycleRoundsAdapter.onRoundSelected, CycleRoundsAdapterTwo.onRoundSelected, DotDraws.sendAlpha, LapAdapter.onPositionCallback {

  ConstraintLayout cl;
  SharedPreferences sharedPreferences;
  SharedPreferences.Editor prefEdit;
  View tabView;
  View mainView;
  Gson gson;

  ImageButton fab;
  ImageButton stopwatch;
  ImageView sortCheckmark;
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
  boolean saveHasOccurred;

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
  TextView sort_text;
  ImageView global_settings;
  TextView save_edit_cycle;
  TextView delete_edit_cycle;

  int mode = 1;
  int savedMode;
  int sortMode = 1;
  int sortModePom = 1;
  int sortHolder = 1;
  int receivedPos;
  int retrievedID;
  String cycleTitle = "";
  List<String> receivedHighlightPositions;

  TextView cycle_name_text;
  EditText cycle_name_edit;
  TextView s1;
  TextView s2;
  TextView s3;
  EditText first_value_edit;
  EditText first_value_edit_two;
  TextView first_value_sep;
  TextView first_value_textView;
  EditText second_value_edit;
  EditText second_value_edit_two;
  TextView second_value_sep;
  TextView second_value_textView;
  EditText third_value_edit;
  EditText third_value_edit_two;
  TextView third_value_sep;
  TextView third_value_textView;
  ImageView plus_first_value;
  ImageView minus_first_value;
  ImageView plus_second_value;
  ImageView minus_second_value;
  ImageButton plus_third_value;
  ImageButton minus_third_value;
  Button add_cycle;
  Button sub_cycle;
  ImageView setsInfinity;
  ImageView breaksInfinity;
  View top_anchor;
  ImageButton start_timer;

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
  boolean editingCycle;
  InputMethodManager inputMethodManager;

  AlphaAnimation fadeIn;
  AlphaAnimation fadeOut;
  View roundView;
  TextView round_count;
  TextView round_value;
  ConstraintLayout.LayoutParams recyclerLayoutOne;
  ConstraintLayout.LayoutParams recyclerLayoutTwo;
  int FADE_IN_HIGHLIGHT_MODE = 1;
  int FADE_OUT_HIGHLIGHT_MODE = 2;
  int FADE_IN_EDIT_CYCLE = 3;
  int FADE_OUT_EDIT_CYCLE = 4;

  boolean roundIsFading;
  boolean roundIsSelected;
  List<Integer> totalSetMillisArray;
  List<Integer> totalBreakMillisArray;
  List<Integer> totalCycleCountArray;
  int roundSelectedPosition;
  float popUpDensityPixelsHeight;
  float popUpDensityPixelWidth;

  PopupWindow timerPopUpWindow;
  View timerPopUpView;

  ProgressBar progressBar;
  ImageView stopWatchView;
  TextView timeLeft;
  TextView msTime;
  CountDownTimer timer;
  TextView reset;
  ObjectAnimator objectAnimator;
  Animation endAnimation;

  TextView overtime;

  TextView cycle_header_text;
  TextView cycles_completed;
  ImageButton new_lap;
  ImageButton next_round;
  ImageButton reset_total_times;
  TextView total_set_header;
  TextView total_break_header;
  TextView total_set_time;
  TextView total_break_time;
  TextView empty_laps;
  TextView delete_text;

  int PAUSING_TIMER = 1;
  int RESUMING_TIMER = 2;

  long setMillis;
  long totalSetMillis;
  long tempSetMillis;
  long setMillisHolder;
  long permSetMillis;

  long breakMillis;
  long totalBreakMillis;
  long breakMillisHolder;
  long tempBreakMillis;
  long permBreakMillis;

  long progressBarValueHolder;
  long pomMillis;

  int maxProgress = 10000;
  int customProgressPause = 10000;
  int pomProgressPause = 10000;
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

  int customCyclesDone;
  int lapsNumber;

  int startRounds;
  int numberOfRoundsLeft;
  int currentRound;

  boolean timerEnded;
  boolean timerDisabled;
  boolean timerIsPaused = true;

  ObjectAnimator fadeInObj;
  ObjectAnimator fadeOutObj;
  RecyclerView lapRecycler;
  LapAdapter lapAdapter;
  LinearLayoutManager lapLayout;

  DotDraws dotDraws;
  ValueAnimator sizeAnimator;
  ValueAnimator valueAnimatorDown;
  ValueAnimator valueAnimatorUp;
  AlphaAnimation fadeProgressIn;
  AlphaAnimation fadeProgressOut;
  boolean textSizeIncreased;
  //Always true initially, since infinity mode starts at 0.
  boolean textSizeReduced = true;

  ArrayList<String> workoutArray;

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
  public Runnable secondsUpSetRunnable;
  public Runnable secondsUpBreakRunnable;

  long baseTime;
  long countUpMillisHolder;
  int scrollPosition;
  boolean launchingTimer;

  //Todo: Need separate total times + cycles arrays for Pom since we are relying on these lists instead of db queries.
  //Todo: Timers + dots active when exiting Timer. Need to auto-cancel on popUp dismissal.
  //Todo: Test delete on edit popUp for both modes.
  //Todo: Remember, db calls are really only needed on app launch and sort.
  //Todo: Intro splash screen, perhaps w/ logo. Smooths opening while app loads.
  //Todo: We had a flashing progressBar w/ full time (should always be 0) at some point. Couldn't replicate.
  //Todo More stats? E.g. total sets/breaks, total partial sets/breaks, etc.
  //Todo: Some other indication in edit mode that a cycle is part of db and not new.
  //Todo: On edited cycles, show textView instead of editText first.
  //Todo: Use empty view space in edit mode for cycle stats (e.g. rounds completed, total times, etc.).
  //Todo: Need lap fades to remain while scrolling.
  //Todo: Add fade/ripple effects to buttons and other stuff that would like it.
  //Todo: Option to set "base" progressBar for count-up (options section in menu?). Simply change progressBarValueHolder.
  //Todo: Save total sets/breaks and completed by day option?
  //Todo: Letter -> Number soft kb is a bit choppy.
  //Todo: Infinity mode for Pom?

  //Todo: editText round box diff. sizes in emulator. Need to work on layout in general.
  //Todo: Minimize aSync threads for performance.
  //Todo: Could long svg files be a lag contributor?
  //Todo: Load/draw canvas in aSync for performance?
  //Todo: TDEE in sep popup w/ tabs.
  //Todo: Make sure sort checkmark positions work on different size screens.
  //Todo: Fade animation for all menus that don't have them yet (e.g. onOptions).
  //Todo: Add taskbar notification for timers.
  //Todo: Add color scheme options.
  //Todo: All DB calls in aSync.
  //Todo: Rename app, of course.
  //Todo: Add onOptionsSelected dots for About, etc.
  //Todo: Repository for db. Look at Executor/other alternate thread methods. Would be MUCH more streamlined on all db calls, but might also bork order of operations when we need to call other stuff under UI thread right after.
  //Todo: Make sure number pad is dismissed when switching to stopwatch mode.
  //Todo: IMPORTANT: Resolve landscape mode vs. portrait. Set to portrait-only in manifest at present. Likely need a second layout for landscape mode. Also check that lifecycle is stable.
  //Todo: Test layouts w/ emulator.
  //Todo: Test everything 10x.

   //Todo: REMEMBER, always call queryCycles() to get a cyclesList reference, otherwise it won't sync w/ the current sort mode.
   //Todo: REMINDER, Try next app w/ Kotlin.

  @Override
  public void onBackPressed() {
    //Used to minimize activity. Will only be called if no popUps have focus.
    moveTaskToBack(false);
  }

  @Override
  public void sendAlphaValue(int alpha) {
    receivedAlpha = alpha;
  }

  @Override
  public void positionCallback(int position) {
    scrollPosition = position;
  }

  //Gets the position clicked on from our saved cycle adapter.
  @Override
  public void onCycleClick(int position) {
    AsyncTask.execute(()-> {
      receivedPos = position;
      //Retrieves timer value lists from cycle adapter list by parsing its Strings, rather than querying database.
      populateRoundList();
      //If clicking on a cycle to launch, it will always be an existing one, and we do not want to call a save method since it is unedited.
      launchTimerCycle(false, false);
    });
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
  public void fadeHasFinished() {
    //When adapter fade on round has finished, execute method to remove the round from adapter list/holders and refresh the adapter display. If we click to remove another round before fade is done, fade gets cancelled, restarted on next position, and this method is also called to remove previous round.
    removeRound();
    //When fade animation for removing Pomodoro cycle is finished in adapter, its listener calls back here where we remove the cycle's values and update adapter w/ empty list.
    if (mode==3) {
      pomValuesTime.clear();
      convertedPomList.clear();
      cycleRoundsAdapter.notifyDataSetChanged();
      cycleRoundsAdapter.disablePomFade();
      sub_cycle.setClickable(true);
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

    LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    savedCyclePopupView = inflater.inflate(R.layout.saved_cycles_layout, null);
    deleteCyclePopupView = inflater.inflate(R.layout.delete_cycles_popup, null);
    sortCyclePopupView = inflater.inflate(R.layout.sort_popup, null);
    editCyclesPopupView = inflater.inflate(R.layout.editing_cycles, null);
    settingsPopupView = inflater.inflate(R.layout.settings_popup, null);
    timerPopUpView = inflater.inflate(R.layout.timer_popup, null);

    popUpDensityPixelsHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 475, getResources().getDisplayMetrics());
    popUpDensityPixelWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());

    savedCyclePopupWindow = new PopupWindow(savedCyclePopupView, 800, 1200, true);
    deleteCyclePopupWindow = new PopupWindow(deleteCyclePopupView, 750, 375, true);
    sortPopupWindow = new PopupWindow(sortCyclePopupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
    editCyclesPopupWindow = new PopupWindow(editCyclesPopupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true);
    settingsPopupWindow = new PopupWindow(settingsPopupView, 700, 1540, true);
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

    cycle_name_text = editCyclesPopupView.findViewById(R.id.cycle_name_text);
    cycle_name_edit = editCyclesPopupView.findViewById(R.id.cycle_name_edit);
    s1 = editCyclesPopupView.findViewById(R.id.s1);
    s2 = editCyclesPopupView.findViewById(R.id.s2);
    s3 = editCyclesPopupView.findViewById(R.id.s3);
    first_value_edit = editCyclesPopupView.findViewById(R.id.first_value_edit);
    first_value_edit_two = editCyclesPopupView.findViewById(R.id.first_value_edit_two);
    first_value_sep = editCyclesPopupView.findViewById(R.id.first_value_sep);
    first_value_textView = editCyclesPopupView.findViewById(R.id.first_value_textView);
    second_value_edit = editCyclesPopupView.findViewById(R.id.second_value_edit);
    second_value_edit_two = editCyclesPopupView.findViewById(R.id.second_value_edit_two);
    second_value_sep = editCyclesPopupView.findViewById(R.id.second_value_sep);
    second_value_textView = editCyclesPopupView.findViewById(R.id.second_value_textView);
    third_value_textView = editCyclesPopupView.findViewById(R.id.third_value_textView);
    plus_first_value = editCyclesPopupView.findViewById(R.id.plus_first_value);
    minus_first_value = editCyclesPopupView.findViewById(R.id.minus_first_value);
    plus_second_value = editCyclesPopupView.findViewById(R.id.plus_second_value);
    minus_second_value = editCyclesPopupView.findViewById(R.id.minus_second_value);
    plus_third_value = editCyclesPopupView.findViewById(R.id.plus_third_value);
    minus_third_value = editCyclesPopupView.findViewById(R.id.minus_third_value);
    third_value_edit = editCyclesPopupView.findViewById(R.id.third_value_edit);
    third_value_edit_two = editCyclesPopupView.findViewById(R.id.third_value_edit_two);
    third_value_sep = editCyclesPopupView.findViewById(R.id.third_value_sep);
    add_cycle = editCyclesPopupView.findViewById(R.id.add_cycle);
    sub_cycle = editCyclesPopupView.findViewById(R.id.subtract_cycle);
    setsInfinity = editCyclesPopupView.findViewById(R.id.s1_up);
    breaksInfinity = editCyclesPopupView.findViewById(R.id.s2_up);
    top_anchor = editCyclesPopupView.findViewById(R.id.top_anchor);
    start_timer = editCyclesPopupView.findViewById(R.id.start_timer);
    save_edit_cycle = editCyclesPopupView.findViewById(R.id.save_edit_cycle);
    delete_edit_cycle = editCyclesPopupView.findViewById(R.id.delete_edit_cycle);
    setsInfinity.setAlpha(0.3f);
    breaksInfinity.setAlpha(0.3f);

    sortAlphaStart = sortCyclePopupView.findViewById(R.id.sort_title_start);
    sortAlphaEnd = sortCyclePopupView.findViewById(R.id.sort_title_end);
    sortRecent = sortCyclePopupView.findViewById(R.id.sort_most_recent);
    sortNotRecent = sortCyclePopupView.findViewById(R.id.sort_least_recent);
    sortHigh = sortCyclePopupView.findViewById(R.id.sort_number_high);
    sortLow = sortCyclePopupView.findViewById(R.id.sort_number_low);
    sortCheckmark = sortCyclePopupView.findViewById(R.id.sortCheckmark);

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
    sort_text = findViewById(R.id.sort_text);
    global_settings = findViewById(R.id.global_settings);
    edit_highlighted_cycle.setVisibility(View.INVISIBLE);
    delete_highlighted_cycle.setVisibility(View.INVISIBLE);
    cancelHighlight.setVisibility(View.INVISIBLE);
    cycle_name_text.setVisibility(View.INVISIBLE);

    workoutTime = new ArrayList<>();
    convertedWorkoutTime = new ArrayList<>();
    workoutTitle = new ArrayList<>();
    workoutCyclesArray = new ArrayList<>();
    typeOfRound = new ArrayList<>();
    typeOfRoundArray = new ArrayList<>();
    totalSetMillisArray = new ArrayList<>();
    totalBreakMillisArray = new ArrayList<>();
    totalCycleCountArray = new ArrayList<>();

    roundHolderOne = new ArrayList<>();
    roundHolderTwo = new ArrayList<>();
    typeHolderOne = new ArrayList<>();
    typeHolderTwo = new ArrayList<>();

    //These Integer Lists hold our millis values for each round.
    pomValuesTime = new ArrayList<>();
    //These String Lists hold String conversions (e.g. 1:05) of our Integer lists, used for display purposes in recyclerView via adapter.
    convertedPomList = new ArrayList<>();
    //These String lists hold concatenated Strings of COMPLETE cycles (e.g. [2:00, 4:30., 3:15] etc.), used for storing the cycles in our database.
    pomArray = new ArrayList<>();
    //These String lists hold each cycle's title.
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

    fadeIn = new AlphaAnimation(0.0f, 1.0f);
    fadeOut = new AlphaAnimation(1.0f, 0.0f);
    fadeIn.setDuration(750);
    fadeOut.setDuration(750);
    fadeIn.setFillAfter(true);
    fadeOut.setFillAfter(true);

    ///////////////////////////////////////////////////////////////////////////
    mHandler = new Handler();

    valueAnimatorDown = new ValueAnimator().ofFloat(90f, 70f);
    valueAnimatorUp = new ValueAnimator().ofFloat(70f, 90f);
    valueAnimatorDown.setDuration(2000);
    valueAnimatorUp.setDuration(2000);

    workoutArray = new ArrayList<>();
    workoutTime = new ArrayList<>();
    typeOfRound = new ArrayList<>();

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
    pomValuesTime = new ArrayList<>();

    reset = timerPopUpView.findViewById(R.id.reset);
    cycle_header_text = timerPopUpView.findViewById(R.id.cycle_header_text);

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
    overtime = timerPopUpView.findViewById(R.id.overtime);
    pauseResumeButton = timerPopUpView.findViewById(R.id.pauseResumeButton);
    pauseResumeButton.setBackgroundColor(Color.argb(0, 0, 0, 0));
    pauseResumeButton.setRippleColor(null);
    reset_total_times = timerPopUpView.findViewById(R.id.reset_total_times);
    empty_laps = timerPopUpView.findViewById(R.id.empty_laps_text);
    if (mode!=4) empty_laps.setVisibility(View.INVISIBLE);

    stopWatchView.setVisibility(View.GONE);
    savedPomCycleRecycler.setVisibility(View.GONE);
    lapRecycler.setVisibility(View.GONE);
    overtime.setVisibility(View.INVISIBLE);
    new_lap.setVisibility(View.INVISIBLE);

    cycles_completed.setText(R.string.cycles_done);
    cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));

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

    //Retrieves checkmark position for sort popup.
    setSortCheckmark();

    AsyncTask.execute(() -> {
      //Loads database of saved cycles.
      cyclesDatabase = CyclesDatabase.getDatabase(getApplicationContext());
      //Calls instance of Cycle entity list based on sort mode.
      queryCycles(true, false);

      //Populates our cycle arrays from the database, so our list of cycles are updated from our adapter and notifyDataSetChanged().
      runOnUiThread(()-> {
        checkEmptyCycles();
        //Adapter and Recycler for round views within our editCycles popUp.
        LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext());
        LinearLayoutManager lm2 = new LinearLayoutManager(getApplicationContext());
        LinearLayoutManager lm3 = new LinearLayoutManager(getApplicationContext());
        LinearLayoutManager lm4 = new LinearLayoutManager(getApplicationContext());

        //Instantiates saved cycle adapter w/ ALL list values, to be populated based on the mode we're on.
        savedCycleAdapter = new SavedCycleAdapter(getApplicationContext(), workoutCyclesArray, typeOfRoundArray, workoutTitleArray);
        savedCycleRecycler.setAdapter(savedCycleAdapter);
        savedCycleRecycler.setLayoutManager(lm);
        //Instantiating callbacks from adapter.
        savedCycleAdapter.setItemClick(MainActivity.this);
        savedCycleAdapter.setHighlight(MainActivity.this);

        savedPomCycleAdapter = new SavedPomCycleAdapter(getApplicationContext(), pomArray, pomTitleArray);
        savedPomCycleRecycler.setAdapter(savedPomCycleAdapter);
        savedPomCycleRecycler.setLayoutManager(lm4);
        //Instantiating callbacks from adapter.
        savedPomCycleAdapter.setItemClick(MainActivity.this);
        savedPomCycleAdapter.setHighlight(MainActivity.this);

        //Calling this by default, so any launch of Main will update our cycle list, since populateCycleList(), called after adapter is instantiated, is what populates our arrays.
        savedCycleAdapter.notifyDataSetChanged();

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
//        recyclerLayoutTwo.leftMargin = 450;

        Animation slide_left = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_in_left);
        slide_left.setDuration(400);
        savedCycleRecycler.startAnimation(slide_left);

        //Sets all editTexts to GONE, and then populates them + textViews based on mode.
        removeEditTimerViews(false);
        defaultEditRoundViews();
        convertEditTime(true);
        setEditValues();
      });
    });

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
        cycleTitle = cycle_name_edit.getText().toString();
        //If round list in current mode is at least 1 and title has been changed, enable save button.
        if ((mode==1 && workoutTime.size()>0) || ((mode==3 && pomValuesTime.size()>0))) {
          if (!save_edit_cycle.isEnabled()) save_edit_cycle.setEnabled(true);
          if (save_edit_cycle.getAlpha()!=1) save_edit_cycle.setAlpha(1.0f);
        }
      }
    };

    cycle_name_edit.addTextChangedListener(titleTextWatcher);
    first_value_edit.addTextChangedListener(editTextWatcher);
    first_value_edit_two.addTextChangedListener(editTextWatcher);
    second_value_edit.addTextChangedListener(editTextWatcher);
    second_value_edit_two.addTextChangedListener(editTextWatcher);
    third_value_edit.addTextChangedListener(editTextWatcher);
    third_value_edit_two.addTextChangedListener(editTextWatcher);

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
        //Sets all editTexts to GONE, and then populates them + textViews based on mode.
        removeEditTimerViews(false);
        defaultEditRoundViews();
        //Toggles "empty cycle" text if adapter list is empty.
        checkEmptyCycles();
        if (mode==1) {
          sortHigh.setVisibility(View.VISIBLE);
          sortLow.setVisibility(View.VISIBLE);
          savedCycleRecycler.setVisibility(View.VISIBLE);
          savedPomCycleRecycler.setVisibility(View.GONE);
        } else {
          sortHigh.setVisibility(View.GONE);
          sortLow.setVisibility(View.GONE);
          //Since mode 3 only uses one adapter layout, set it here.
          roundRecyclerTwo.setVisibility(View.GONE);
          recyclerLayoutOne.leftMargin = 240;
          roundListDivider.setVisibility(View.GONE);
          savedCycleRecycler.setVisibility(View.GONE);
          savedPomCycleRecycler.setVisibility(View.VISIBLE);
        }
      }

      @Override
      public void onTabUnselected(TabLayout.Tab tab) {
        //since modes use same String, clear it between tab switches.
        cycleTitle = "";
        //If in highlight mode (most easily denoted by enabled state of sort_text), exit out its view since we are switching tabs.
        if (!sort_text.isEnabled()) fadeEditCycleButtonsIn(FADE_OUT_HIGHLIGHT_MODE);
        //Dismisses editCycle popup when switching tabs.
        if (editCyclesPopupWindow.isShowing()) editCyclesPopupWindow.dismiss();
        //Turning highlight mode off since we are moving to a new tab.
        savedCycleAdapter.removeHighlight(true);
        savedPomCycleAdapter.removeHighlight(true);
        //Resets callback vars for clicked positions and highlighted positions when switching tabs.
        receivedPos = 0;
        receivedHighlightPositions.clear();
      }

      @Override
      public void onTabReselected(TabLayout.Tab tab) {
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
          breaksInfinity.setAlpha(0.3f);
          rowSelect(s1, first_value_textView, first_value_edit, first_value_edit_two, first_value_sep, plus_first_value, minus_first_value, Color.GREEN);
          rowSelect(s2, second_value_textView, second_value_edit, second_value_edit_two, second_value_sep, plus_second_value, minus_second_value, Color.WHITE);
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
          setsInfinity.setAlpha(0.3f);
          rowSelect(s2, second_value_textView, second_value_edit, second_value_edit_two, second_value_sep, plus_second_value, minus_second_value, Color.RED);
          rowSelect(s1, first_value_textView, first_value_edit, first_value_edit_two, first_value_sep, plus_first_value, minus_first_value, Color.WHITE);
        }
      }
    });

    //Caps and sets editText values. Only spot that takes focus outside of the view itself (above). Needs to be onTouch to register first click, and false so event is not consumed.
    cycle_name_edit.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction()==MotionEvent.ACTION_DOWN) {
          convertEditTime(true);
        }
        return false;
      }
    });

    global_settings.setOnClickListener(v-> {
      settingsPopupWindow.showAtLocation(cl, Gravity.NO_GRAVITY, 0, 240);
    });

      //Brings up editCycle popUp to create new Cycle.
    fab.setOnClickListener(v -> {
      //Default row selection.
      resetRows();
      //Brings up menu to add/subtract rounds to new cycle.
      editCyclesPopupWindow.showAsDropDown(tabLayout);
      fab.setEnabled(false);
      //Used when deciding whether to save a new cycle or retrieve/update a current one. FAB will always create a new one.
      isNewCycle = true;
      //Default disabled state of edited cycle save, if nothing has changed.
      save_edit_cycle.setEnabled(false);
      save_edit_cycle.setAlpha(0.3f);
      //Clears round adapter arrays so they can be freshly populated.
      clearRoundAdapterArrays();
    });

    stopwatch.setOnClickListener(v-> {
      mode = 4;
      dotDraws.setMode(4);
      populateTimerUI();
      timerPopUpWindow.showAtLocation(cl, Gravity.NO_GRAVITY, 0, 0);
    });

    //Showing sort popup window.
    sort_text.setOnClickListener(v-> {
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

      AsyncTask.execute(()-> {
        queryCycles(false, true);
        runOnUiThread(()-> {
          //Refreshes adapter.
          savedCycleAdapter.notifyDataSetChanged();
          //Sets checkmark view.
          setSortCheckmark();
          sortPopupWindow.dismiss();
        });
        //Saves sort mode so it defaults to chosen whenever we create this activity.
        prefEdit.putInt("sortMode", sortMode);
        prefEdit.putInt("sortModePom", sortModePom);
        prefEdit.apply();
      });
    };
    sortAlphaStart.setOnClickListener(sortListener);
    sortAlphaEnd.setOnClickListener(sortListener);
    sortRecent.setOnClickListener(sortListener);
    sortNotRecent.setOnClickListener(sortListener);
    sortHigh.setOnClickListener(sortListener);
    sortLow.setOnClickListener(sortListener);

    //Exiting timer popup always brings us back to popup-less Main, so change views accordingly.
    timerPopUpWindow.setOnDismissListener(() -> {
      //Since we don't update saved cycle list when launching timer (for aesthetic purposes), we do it here on exiting timer.
      if (mode==1) {
        savedCycleRecycler.setVisibility(View.VISIBLE);
        savedCycleAdapter.notifyDataSetChanged();
      } else {
        savedPomCycleRecycler.setVisibility(View.VISIBLE);
        savedPomCycleAdapter.notifyDataSetChanged();
      }

      launchingTimer = false;
      checkEmptyCycles();
      cl.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
      //Saves total times + cycle count.
      AsyncTask.execute(()->{
        exitTimer();
      });
    });

     editCyclesPopupView.setOnClickListener(v-> {
      //Caps and sets editText values when clicking outside (exiting) the editText box.
      convertEditTime(true);
      //Dismisses editText views if we click within the unpopulated area of popUp. Replaces them w/ textViews.
      removeEditTimerViews(true);
      //Hides soft keyboard by using a token of the current editCycleView.
      inputMethodManager.hideSoftInputFromWindow(editCyclesPopupView.getWindowToken(), 0);
    });

    //Once editPopUp is on screen, change Main's background color to match it and remove Main's recyclerView view. This prevents the soft keyb
    // oard's "tear" through Main's layout from being visible.
    editCyclesPopupView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
      @Override
      public void onSystemUiVisibilityChange(int visibility) {
        savedCycleRecycler.setVisibility(View.GONE);
        emptyCycleList.setVisibility(View.GONE);
        cl.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.dark_grey));
      }
    });

    //Because this window steals focus from our activity so it can use the soft keyboard, we are using this listener to perform the functions our onBackPressed override would normally handle when the popUp is active.
    editCyclesPopupWindow.setOnDismissListener(() -> {
      checkEmptyCycles();
      //Resets Main's recyclerView visibility if we are not launching timer for smoother transition between popups.
      if (!launchingTimer) {
        if (mode==1) {
          savedCycleAdapter.notifyDataSetChanged();
          savedCycleRecycler.setVisibility(View.VISIBLE);
        }
        if (mode==3) {
          savedPomCycleAdapter.notifyDataSetChanged();
          savedPomCycleRecycler.setVisibility(View.VISIBLE);
        }
      }
      if (saveHasOccurred) {
        saveHasOccurred = false;
        if (mode==1) savedCycleAdapter.notifyDataSetChanged(); if (mode==3) savedPomCycleAdapter.notifyDataSetChanged();
      }
      //Color reset to black, also for smooth transition to timer. Grey only necessary to prevent soft kb tearing.
      cl.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
      //Re-enables FAB button (disabled to prevent overlap when edit popup is active).
      fab.setEnabled(true);
      //If closing edit cycle popUp after editing a cycle, do the following.
      if (editingCycle) {
        savedCycleAdapter.removeHighlight(true);
        //Calls method that sets views for our edit cycles mode.
        fadeEditCycleButtonsIn(FADE_OUT_EDIT_CYCLE);
      }

      //Updates round adapters so lists show as cleared.
      cycleRoundsAdapter.notifyDataSetChanged();
      cycleRoundsAdapterTwo.notifyDataSetChanged();
      //Removed round divider.
      roundListDivider.setVisibility(View.GONE);
    });

    ////--ActionBar Item onClicks START--////
    edit_highlighted_cycle.setOnClickListener(v-> {
      //Default row selection.
      resetRows();
      //Used when deciding whether to save a new cycle or retrieve/update a current one. Editing will always pull an existing one.
      isNewCycle = false;
      fadeEditCycleButtonsIn(FADE_IN_EDIT_CYCLE);
      //Displays edit cycles popUp.
      editCyclesPopupWindow.showAsDropDown(tabLayout);

      //Button is only active if list contains exactly ONE position (i.e. only one cycle is selected). Here, we set our retrieved position (same as if we simply clicked a cycle to launch) to the one passed in from our highlight.
      receivedPos = Integer.parseInt(receivedHighlightPositions.get(0));
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
        cycle_name_text.setVisibility(View.INVISIBLE);
        cycle_name_edit.setVisibility(View.VISIBLE);
        cycle_name_text.setText(cycleTitle);
        //Setting editText title.
        cycle_name_edit.setText(cycleTitle);
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
      AsyncTask.execute(()-> {
        //First query to get current list of rows.
        for (int i=0; i<receivedHighlightPositions.size(); i++) {
          receivedPos = Integer.parseInt(receivedHighlightPositions.get(i));
          //Using each received position, deletes the cycle from the database.
          deleteCycle(false);
          //Using each received position, deletes the cycle's values from its adapter arrays.
          editCycleList(DELETING_CYCLE);
        }
        runOnUiThread(() -> {
          //Since we have deleted every position in selected list, clear the list.
          receivedHighlightPositions.clear();
          //If there are no cycles left, cancel highlight mode. If there are any left, simply remove all highlights.
          if (receivedHighlightPositions.size()>0) savedCycleAdapter.removeHighlight(false); else {
            cancelHighlight.setVisibility(View.INVISIBLE);
            edit_highlighted_cycle.setVisibility(View.INVISIBLE);
            delete_highlighted_cycle.setVisibility(View.INVISIBLE);
            appHeader.setVisibility(View.VISIBLE);

            if (mode==1) savedCycleAdapter.removeHighlight(true);
            if (mode==3) savedPomCycleAdapter.removeHighlight(true);
            Toast.makeText(getApplicationContext(), "Deleted!", Toast.LENGTH_SHORT).show();
          }
        });
      });
    });

    delete_edit_cycle.setOnClickListener(v-> {
      //If on a saved edit cycle, delete it from database. Otherwise, simply clear the adapter array lists since nothing is actually saved.
      if (!isNewCycle) {
        AsyncTask.execute(()-> {
          //Position of the
          receivedPos = Integer.parseInt(receivedHighlightPositions.get(0));
          deleteCycle(false);
          editCycleList(DELETING_CYCLE);
        });
      } else {
        clearRoundAdapterArrays();
      }
      editCyclesPopupWindow.dismiss();
      Toast.makeText(getApplicationContext(), "Deleted!", Toast.LENGTH_SHORT).show();
    });

    save_edit_cycle.setOnClickListener(v-> {
      AsyncTask.execute(()->{
        saveCycles(isNewCycle);
        if (isNewCycle) editCycleList(ADDING_CYCLE); else editCycleList(EDITING_CYCLE);
        //Used to call notifyDataSetChanged() in SavedCycleAdapter. No need if db has not been changed.
        saveHasOccurred = true;
        runOnUiThread(()-> {
          //Disables save button. Any change in rounds or title will re-activate it.
          save_edit_cycle.setEnabled(false);
          save_edit_cycle.setAlpha(0.3f);
          Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();
        });
      });
    });

    //When in highlight edit mode, clicking on the textView will remove it, replace it w/ editText field, give that field focus and bring up the soft keyboard.
    cycle_name_text.setOnClickListener(v-> {
      cycle_name_text.setVisibility(View.INVISIBLE);
      cycle_name_edit.setVisibility(View.VISIBLE);
      cycle_name_edit.requestFocus();
      inputMethodManager.showSoftInput(cycle_name_edit, 0);
    });

    //Selects all text when long clicking in editText.
    cycle_name_edit.setOnLongClickListener(v-> {
      cycle_name_edit.selectAll();
      return true;
    });

    ////--ActionBar Item onClicks END--////

    ////--EditCycles Menu Item onClicks START--////
    start_timer.setOnClickListener(v-> {
      AsyncTask.execute(()-> {
        //Launched from editCyclePopUp and calls TimerInterface. First input controls whether it is a new cycle, and the second will always be true since a cycle launch should automatically save/update it in database.
        launchTimerCycle(isNewCycle, true);
      });
    });

    setsInfinity.setOnClickListener(v -> {
      if (setsSelected) if (setsInfinity.getAlpha()==1.0f) setsInfinity.setAlpha(0.3f); else setsInfinity.setAlpha(1.0f);
    });

    breaksInfinity.setOnClickListener(v -> {
      if (breaksSelected) if (breaksInfinity.getAlpha()==1.0f) breaksInfinity.setAlpha(0.3f); else breaksInfinity.setAlpha(1.0f);
    });

    //For moment, using arrows next to sets and breaks to determine which type of round we're adding.
    add_cycle.setOnClickListener(v -> {
      adjustCustom(true);
    });

    sub_cycle.setOnClickListener(v -> {
      adjustCustom(false);
    });

    //Grabs the x-axis value of textView, and uses that determine whether we replace it with the minutes or seconds editText.
    first_value_textView.setOnTouchListener((v, event) -> {
      if (event.getAction()==MotionEvent.ACTION_DOWN) {
        editTextViewPosX = event.getX();
        editAndTextSwitch(true, 1);
      }
      return true;
    });

    second_value_textView.setOnTouchListener((v, event) -> {
      if (event.getAction()==MotionEvent.ACTION_DOWN) {
        editTextViewPosX = event.getX();
        editAndTextSwitch(true, 2);
      }
      return true;
    });

    third_value_textView.setOnTouchListener((v, event) -> {
      if (event.getAction()==MotionEvent.ACTION_DOWN) {
        editTextViewPosX = event.getX();
        editAndTextSwitch(true, 3);
      }
      return true;
    });

    adjustRoundDelay = new Runnable() {
      @Override
      public void run() {
        add_cycle.setClickable(true);
        sub_cycle.setClickable(true);
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
        fadeCap(first_value_textView);
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
        fadeCap(second_value_textView);
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
        fadeCap(third_value_textView);
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

    plus_first_value.setOnTouchListener((v, event) -> {
      incrementValues = true;
      setIncrements(event, changeFirstValue);
      convertEditTime(true);
      switch (mode) {
        case 1:
          first_value_textView.setText(convertCustomTextView(setValue));
          break;
        case 3:
          first_value_textView.setText(convertCustomTextView(pomValue1));
          break;
      }
      return true;
    });

    minus_first_value.setOnTouchListener((v, event) -> {
      incrementValues = false;
      setIncrements(event, changeFirstValue);
      convertEditTime(true);
      switch (mode) {
        case 1:
          first_value_textView.setText(convertCustomTextView(setValue));
          break;
        case 3:
          first_value_textView.setText(convertCustomTextView(pomValue1));
          break;
      }
      return true;
    });

    plus_second_value.setOnTouchListener((v, event) -> {
      incrementValues = true;
      setIncrements(event, changeSecondValue);
      convertEditTime(true);
      switch (mode) {
        case 1:
          second_value_textView.setText(convertCustomTextView(breakValue));
          break;
        case 3:
          second_value_textView.setText(convertCustomTextView(pomValue2));
          break;
      }
      return true;
    });

    minus_second_value.setOnTouchListener((v, event) -> {
      incrementValues = false;
      setIncrements(event, changeSecondValue);
      convertEditTime(true);
      switch (mode) {
        case 1:
          second_value_textView.setText(convertCustomTextView(breakValue));
          break;
        case 3:
          second_value_textView.setText(convertCustomTextView(pomValue2));
          break;
      }
      return true;
    });

    plus_third_value.setOnTouchListener((v, event) -> {
      incrementValues = true;
      setIncrements(event, changeThirdValue);
      convertEditTime(true);
      third_value_textView.setText(convertCustomTextView(pomValue3));
      return true;
    });

    minus_third_value.setOnTouchListener((v, event) -> {
      incrementValues = false;
      setIncrements(event, changeThirdValue);
      convertEditTime(true);
      third_value_textView.setText(convertCustomTextView(pomValue3));
      return true;
    });
    ////--EditCycles Menu Item OnClicks END--////


    delete_all_confirm.setOnClickListener(v-> {
      //Removes confirmation window.
      if (deleteCyclePopupWindow.isShowing()) deleteCyclePopupWindow.dismiss();
      AsyncTask.execute(() -> {
        //Deletes all cycles.
        deleteCycle(true);
      });
    });

    delete_all_cancel.setOnClickListener(v-> {
      //Removes confirmation window.
      if (deleteCyclePopupWindow.isShowing()) deleteCyclePopupWindow.dismiss();
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

    /////---------Testing pom round iterations---------------/////////
    if (mode==3) for (int i=1; i<9; i++) if (i%2!=0) pomValuesTime.set(i-1, 4000); else pomValuesTime.set(i-1, 6000);

    //Draws dot display depending on which more we're on.
    dotDraws.setMode(mode);
    //Implements callback for end-of-round alpha fade on dots.
    dotDraws.onAlphaSend(MainActivity.this);

    //Recycler view for our stopwatch laps.
    lapLayout = new LinearLayoutManager(getApplicationContext());
    lapAdapter = new LapAdapter(getApplicationContext(), currentLapList, savedLapList);
    lapRecycler.setAdapter(lapAdapter);
    lapRecycler.setLayoutManager(lapLayout);
    lapAdapter.passPosition(MainActivity.this);

    //Listener that executes method that retrieves visible positions from recyclerView and passes them into adapter for use in fading effect.
    lapRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
      }

      //This listens everywhere. It's bindView that restricts its refresh to top 2/bottom 2 rows.
      @Override
      public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        getVisibleLapPositions();
        if (dy!=0) lapAdapter.listIsScrolling(true);
      }
    });

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
    secondsUpSetRunnable = new Runnable() {
      @Override
      public void run() {
        //Animates text size change when timer gets to 60 seconds.
        animateTextSize(countUpMillisSets);
        //Subtracting the current time from the base (start) time which was set in our pauseResume() method.
        countUpMillisSets = (int) (countUpMillisHolder) +  (System.currentTimeMillis() - baseTime);
        //Subtracts the elapsed millis value from base 30000 used for count-up rounds.
        progressBarValueHolder = maxProgress - countUpMillisBreaks;
        timeLeft.setText(convertSeconds((countUpMillisSets) / 1000));
        //Updates workoutTime list w/ millis values for round counting up, and passes those into dotDraws so the dot text also iterates up.
        workoutTime.set(workoutTime.size() - numberOfRoundsLeft, (int) countUpMillisSets);
        dotDraws.updateWorkoutTimes(workoutTime, typeOfRound);
        //Temporary value for current round, using totalSetMillis which is our permanent value.
        tempSetMillis = totalSetMillis + countUpMillisSets;
        total_set_time.setText(convertSeconds(tempSetMillis / 1000));
        //Once progressBar value hits 0, animate bar/text, reset bar's progress value to max, and restart the objectAnimator that uses it.
        if (progressBar.getProgress()<=0) {
          progressBar.startAnimation(fadeProgressOut);
          timeLeft.startAnimation(fadeProgressOut);
          objectAnimator.start();
        }
        mHandler.postDelayed(this, 50);
      }
    };

    secondsUpBreakRunnable = new Runnable() {
      @Override
      public void run() {
        animateTextSize(countUpMillisBreaks);
        //Subtracting the current time from the base (start) time which was set in our pauseResume() method, then adding it to the saved value of our countUpMillis.
        countUpMillisBreaks = (int) (countUpMillisHolder) +  (System.currentTimeMillis() - baseTime);
        //Subtracts the elapsed millis value from base 30000 used for count-up rounds.
        progressBarValueHolder = maxProgress - countUpMillisBreaks;
        timeLeft.setText(convertSeconds((countUpMillisBreaks) / 1000));
        //Updates workoutTime list w/ millis values for round counting up, and passes those into dotDraws so the dot text also iterates up.
        workoutTime.set(workoutTime.size() - numberOfRoundsLeft, (int) countUpMillisBreaks);
        dotDraws.updateWorkoutTimes(workoutTime, typeOfRound);
        //Temporary value for current round, using totalBreakMillis which is our permanent value.
        tempBreakMillis = totalBreakMillis + countUpMillisBreaks;
        total_break_time.setText(convertSeconds(tempBreakMillis / 1000));
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
        displayTime = convertStopwatch((long) seconds);

        timeLeft.setText(displayTime);
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
      if (!timerIsPaused) pauseAndResumeTimer(PAUSING_TIMER); else pauseAndResumeTimer(RESUMING_TIMER);
    });

    delete_all_cancel.setOnClickListener(v -> {
      //Removes our delete confirm popUp if we cancel.
      if (deleteCyclePopupWindow.isShowing()) deleteCyclePopupWindow.dismiss();
    });

    reset_total_times.setOnClickListener(v -> {
      deleteTotalTimes();
    });
  }

  public int setDensityPixels(float pixels) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixels, getResources().getDisplayMetrics());
  }

  //Todo: This check needs to rely on arrayLists, not cycleList instance.
  public void checkEmptyCycles() {
    if (mode==1) {
      if (workoutCyclesArray.size()!=0) emptyCycleList.setVisibility(View.GONE); else emptyCycleList.setVisibility(View.VISIBLE);
    }
    if (mode==3) {
      if (pomArray.size()!=0) emptyCycleList.setVisibility(View.GONE); else emptyCycleList.setVisibility(View.VISIBLE);
    }
  }

  //Resets row selection and editText/textView values.
  public void resetRows() {
    if (mode==1) {
      //Default selection of Set.
      firstRowHighlighted = true;
      secondRowHighlighted = false;
      rowSelect(s1, first_value_textView, first_value_edit, first_value_edit_two, first_value_sep, plus_first_value, minus_first_value, Color.GREEN);
      rowSelect(s2, second_value_textView, second_value_edit, second_value_edit_two, second_value_sep, plus_second_value, minus_second_value, Color.WHITE);
      //Our editText fields have listeners attached which call setEditValues(), which set our edit values AND setValue/breakValue vars to the values within the editText box itself. Here, we use 0:30 for both.
      first_value_edit.setText(String.valueOf(0));
      first_value_edit_two.setText(elongateEditSeconds(30));
      second_value_edit.setText(String.valueOf(0));
      second_value_edit_two.setText(elongateEditSeconds(30));
      first_value_textView.setText(convertCustomTextView(setValue));
      second_value_textView.setText(convertCustomTextView(breakValue));
    }
    else if (mode==3) {
      rowSelect(s1, first_value_textView, first_value_edit, first_value_edit_two, first_value_sep, plus_first_value, minus_first_value, Color.WHITE);
      rowSelect(s2, second_value_textView, second_value_edit, second_value_edit_two, second_value_sep, plus_second_value, minus_second_value, Color.WHITE);
      rowSelect(s3, third_value_textView, third_value_edit, third_value_edit_two, third_value_sep, plus_third_value, minus_third_value, Color.WHITE);
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
    edit_highlighted_cycle.clearAnimation();
    delete_highlighted_cycle.clearAnimation();
    cancelHighlight.clearAnimation();
    appHeader.clearAnimation();
    sort_text.clearAnimation();
    //Only needs to be true if editor is active, which is only the case where it is set to true below.
    editingCycle = false;
    if (typeOfFade==FADE_IN_HIGHLIGHT_MODE) {
      //Boolean used to keep launchCycles() from calling populateRoundLists(), which replace our current timer array list w/ one fetched from DB.
      edit_highlighted_cycle.startAnimation(fadeIn);
      delete_highlighted_cycle.startAnimation(fadeIn);
      cancelHighlight.startAnimation(fadeIn);
      appHeader.startAnimation(fadeOut);
      sort_text.startAnimation(fadeOut);
      //Disabling sort button since it is faded out and still active.
      sort_text.setEnabled(false);
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
      sort_text.startAnimation(fadeIn);
      //Re-enabling sort button now that edit mode has exited.
      sort_text.setEnabled(true);
      //Disabling edit/delete buttons.
      edit_highlighted_cycle.setEnabled(false);
      delete_highlighted_cycle.setEnabled(false);
    }
    if (typeOfFade==FADE_IN_EDIT_CYCLE) {
      //Set to false by default, and true only with this conditional.
      editingCycle = true;
      delete_highlighted_cycle.setEnabled(true);
      edit_highlighted_cycle.setVisibility(View.GONE);
      sort_text.setVisibility(View.GONE);
    }
    if (typeOfFade==FADE_OUT_EDIT_CYCLE) {
      sort_text.setVisibility(View.VISIBLE);
      delete_highlighted_cycle.setVisibility(View.GONE);
      delete_highlighted_cycle.setEnabled(false);
    }
  }

  public void setSortCheckmark() {
    switch (sortHolder) {
      case 1:
        sortCheckmark.setY(14); break;
      case 2:
        sortCheckmark.setY(110); break;
      case 3:
        sortCheckmark.setY(206); break;
      case 4:
        sortCheckmark.setY(302); break;
      case 5:
        sortCheckmark.setY(398); break;
      case 6:
        sortCheckmark.setY(494); break;
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
      editSetMinutes = convertEditTextToLong(first_value_edit);
      editSetSeconds = convertEditTextToLong(first_value_edit_two);
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
      first_value_textView.setText(convertCustomTextView(setValue));

      editBreakMinutes = convertEditTextToLong(second_value_edit);
      editBreakSeconds = convertEditTextToLong(second_value_edit_two);
      if (editBreakSeconds > 59) {
        editBreakMinutes += 1;
        editBreakSeconds = editBreakSeconds - 60;
      }
      if (editBreakMinutes >= 5) editBreakMinutes = 5;
      if (editBreakMinutes <= 0) editBreakMinutes = 0;
      if (editBreakSeconds <=0 || editBreakMinutes == 5) editBreakSeconds = 0;
      if (editBreakSeconds < 5 && editBreakMinutes == 0) editBreakSeconds = 0;

      breakValue = (int) ((editBreakMinutes * 60) + editBreakSeconds);
      second_value_textView.setText(convertCustomTextView(breakValue));
      //Sets value of editBreakMinutes to either breakValue, or breakOnlyValue, depending on which mode we're on.

      toastBounds(5, 300, setValue);
      toastBounds(5, 300, breakValue);
      if (setValue < 5) setValue = 5;
      if (breakValue < 5) breakValue = 5;
      if (setValue > 300) setValue = 300;
      if (breakValue > 300) breakValue = 300;
    } else if (mode==3) {
      editPomMinutesOne = convertEditTextToLong(first_value_edit);
      editPomSecondsOne = convertEditTextToLong(first_value_edit_two);
      editPomMinutesTwo = convertEditTextToLong(second_value_edit);
      editPomSecondsTwo = convertEditTextToLong(second_value_edit_two);
      editPomMinutesThree = convertEditTextToLong(third_value_edit);
      editPomSecondsThree = convertEditTextToLong(third_value_edit_two);
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
        first_value_edit.setText(String.valueOf(editSetMinutes));
        first_value_edit_two.setText(elongateEditSeconds(editSetSeconds));
        second_value_edit.setText(String.valueOf(editBreakMinutes));
        second_value_edit_two.setText(elongateEditSeconds(editBreakSeconds));
      }

    } else if (mode==3) {
      editPomSecondsOne = pomValue1 % 60;
      editPomMinutesOne = pomValue1 / 60;
      editPomSecondsTwo = pomValue2 % 60;
      editPomMinutesTwo = pomValue2 / 60;
      editPomSecondsThree = pomValue3 % 60;
      editPomMinutesThree = pomValue3 / 60;
      if (setEditViews) {
        first_value_edit.setText(String.valueOf(editPomMinutesOne));
        second_value_edit.setText(String.valueOf(editPomMinutesTwo));
        third_value_edit.setText(String.valueOf(editPomMinutesThree));
        first_value_edit_two.setText(elongateEditSeconds(editPomSecondsOne));
        second_value_edit_two.setText(elongateEditSeconds(editPomSecondsTwo));
        third_value_edit_two.setText(elongateEditSeconds(editPomSecondsThree));
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
        first_value_textView.setVisibility(View.INVISIBLE);
        first_value_edit.setVisibility(View.VISIBLE);
        first_value_edit_two.setVisibility(View.VISIBLE);
        first_value_sep.setVisibility(View.VISIBLE);
        if (second_value_edit.isShown() || second_value_edit_two.isShown()) {
          second_value_textView.setVisibility(View.VISIBLE);
          second_value_edit.setVisibility(View.GONE);
          second_value_edit_two.setVisibility(View.GONE);
          second_value_sep.setVisibility(View.GONE);
        }
        if (mode==3) {
          if (third_value_edit.isShown()) {
            third_value_edit.setVisibility(View.GONE);
            third_value_edit_two.setVisibility(View.GONE);
            third_value_sep.setVisibility(View.GONE);
            third_value_textView.setVisibility(View.VISIBLE);
          }
        }
        //Used to request focus, and then show the soft keyboard. Using x-axis values to determine whether we focus on minutes or seconds.
        if (editTextViewPosX<=75) {
          first_value_edit.requestFocus();
          inputMethodManager.showSoftInput(first_value_edit,  0);
        } else {
          first_value_edit_two.requestFocus();
          inputMethodManager.showSoftInput(first_value_edit_two, 0);
        }
      } else if (viewRemoved == 2) {
        second_value_textView.setVisibility(View.INVISIBLE);
        second_value_edit.setVisibility(View.VISIBLE);
        second_value_edit_two.setVisibility(View.VISIBLE);
        second_value_sep.setVisibility(View.VISIBLE);
        if (first_value_edit.isShown() || first_value_edit_two.isShown()) {
          first_value_textView.setVisibility(View.VISIBLE);
          first_value_edit.setVisibility(View.GONE);
          first_value_edit_two.setVisibility(View.GONE);
          first_value_sep.setVisibility(View.GONE);
        }
        if (mode==3) {
          if (third_value_edit.isShown()) {
            third_value_edit.setVisibility(View.GONE);
            third_value_edit_two.setVisibility(View.GONE);
            third_value_sep.setVisibility(View.GONE);
            third_value_textView.setVisibility(View.VISIBLE);
          }
        }
        if (editTextViewPosX<=75) {
          second_value_edit.requestFocus();
          inputMethodManager.showSoftInput(second_value_edit, 0);
        } else {
          second_value_edit_two.requestFocus();
          inputMethodManager.showSoftInput(second_value_edit_two, 0);
        }
      } else if (viewRemoved == 3) {
        if (first_value_edit.isShown()) {
          first_value_edit.setVisibility(View.GONE);
          first_value_edit_two.setVisibility(View.GONE);
          first_value_sep.setVisibility(View.GONE);
          first_value_textView.setVisibility(View.VISIBLE);
        }
        if (second_value_edit.isShown()) {
          second_value_edit.setVisibility(View.GONE);
          second_value_edit_two.setVisibility(View.GONE);
          second_value_sep.setVisibility(View.GONE);
          second_value_textView.setVisibility(View.VISIBLE);
        }
        third_value_textView.setVisibility(View.INVISIBLE);
        third_value_edit.setVisibility(View.VISIBLE);
        third_value_edit_two.setVisibility(View.VISIBLE);
        third_value_sep.setVisibility(View.VISIBLE);
      }
      if (editTextViewPosX<=75) {
        third_value_edit.requestFocus();
        inputMethodManager.showSoftInput(third_value_edit, 0);
      } else {
        third_value_edit_two.requestFocus();
        inputMethodManager.showSoftInput(third_value_edit_two, 0);
      }
    } else {
      //If moving from editText -> textView.
      switch (mode) {
        case 1:
          if (viewRemoved == 1) {
            //If first is shown, second and separator are also shown.
            if (first_value_edit.isShown()) {
              first_value_edit.setVisibility(View.GONE);
              first_value_sep.setVisibility(View.GONE);
              first_value_edit_two.setVisibility(View.GONE);
              first_value_textView.setVisibility(View.VISIBLE);
            }
          } else if (viewRemoved == 2) {
            if (second_value_edit.isShown()) {
              second_value_edit.setVisibility(View.GONE);
              second_value_sep.setVisibility(View.GONE);
              second_value_edit_two.setVisibility(View.GONE);
              second_value_textView.setVisibility(View.VISIBLE);
            }
          }
          break;
        case 3:
          if (viewRemoved == 1) {
            if (first_value_edit.isShown()) {
              first_value_edit.setVisibility(View.GONE);
              first_value_edit_two.setVisibility(View.GONE);
              first_value_sep.setVisibility(View.GONE);
              first_value_textView.setVisibility(View.VISIBLE);
            }
          } else if (viewRemoved == 2) {
            if (second_value_edit.isShown()) {
              second_value_edit.setVisibility(View.GONE);
              second_value_edit_two.setVisibility(View.GONE);
              second_value_sep.setVisibility(View.GONE);
              second_value_textView.setVisibility(View.VISIBLE);
            }
          } else if (viewRemoved == 3) {
            if (third_value_edit.isShown()) {
              third_value_edit.setVisibility(View.GONE);
              third_value_edit_two.setVisibility(View.GONE);
              third_value_sep.setVisibility(View.GONE);
              third_value_textView.setVisibility(View.VISIBLE);
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
  public String convertCustomTextView(long totalSeconds) {
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

  //Sets all of our editTexts to Invisible.
  public void removeEditTimerViews(boolean reinstateText) {
    first_value_edit.setVisibility(View.GONE);
    first_value_sep.setVisibility(View.GONE);
    first_value_edit.setVisibility(View.GONE);
    first_value_edit_two.setVisibility(View.GONE);
    second_value_edit.setVisibility(View.GONE);
    second_value_sep.setVisibility(View.GONE);
    second_value_edit_two.setVisibility(View.GONE);
    third_value_edit.setVisibility(View.GONE);
    third_value_sep.setVisibility(View.GONE);
    third_value_edit_two.setVisibility(View.GONE);

    //Replaces editTexts w/ textViews if desired.
    if (reinstateText) {
      if (mode==1 || mode==3) first_value_textView.setVisibility(View.VISIBLE);
      if (mode==3) third_value_textView.setVisibility(View.VISIBLE);
      second_value_textView.setVisibility(View.VISIBLE);
    }
  }

  public void defaultEditRoundViews() {
    //Instance of layout objects we can set programmatically based on which mode we're on.
    ConstraintLayout.LayoutParams s1ParamsAdd = (ConstraintLayout.LayoutParams) plus_first_value.getLayoutParams();
    ConstraintLayout.LayoutParams s1ParamsSub = (ConstraintLayout.LayoutParams) minus_first_value.getLayoutParams();
    ConstraintLayout.LayoutParams s2ParamsAdd = (ConstraintLayout.LayoutParams) plus_second_value.getLayoutParams();
    ConstraintLayout.LayoutParams s2ParamsSub = (ConstraintLayout.LayoutParams) minus_second_value.getLayoutParams();
    ConstraintLayout.LayoutParams addParams = (ConstraintLayout.LayoutParams) add_cycle.getLayoutParams();
    ConstraintLayout.LayoutParams subParams = (ConstraintLayout.LayoutParams) sub_cycle.getLayoutParams();

    convertEditTime(true);
    switch (mode) {
      case 1:
        //All shared visibilities between modes 1 and 2.
        s2.setVisibility(View.VISIBLE);
        s3.setVisibility(View.GONE);
        third_value_textView.setVisibility(View.INVISIBLE);
        plus_third_value.setVisibility(View.INVISIBLE);
        minus_third_value.setVisibility(View.INVISIBLE);
        plus_second_value.setVisibility(View.VISIBLE);
        minus_second_value.setVisibility(View.VISIBLE);
        setsInfinity.setVisibility(View.VISIBLE);
        breaksInfinity.setVisibility(View.VISIBLE);
        second_value_textView.setVisibility(View.VISIBLE);
        //Shared String between modes 1 and 2.
        s2.setText(R.string.break_time);
        s1.setVisibility(View.VISIBLE);
        first_value_textView.setVisibility(View.VISIBLE);
        plus_first_value.setVisibility(View.VISIBLE);
        minus_first_value.setVisibility(View.VISIBLE);
        //Strings and values exclusive to mode 1.
        s1.setText(R.string.set_time);
        first_value_textView.setText(convertCustomTextView(setValue));
        second_value_textView.setText(convertCustomTextView(breakValue));
        //If in mode 1 or 2, constraining our add/remove buttons to the "s2" line of objects.
        addParams.topToBottom = R.id.s2;
        addParams.topMargin = 60;
        subParams.topToBottom = R.id.s2;
        subParams.topMargin = 60;
//        s1ParamsAdd.topMargin = 100;
//        s1ParamsSub.topMargin = 100;
        break;
      case 3:
        //Visibilities and values exclusive to mode 3.
        s1.setVisibility(View.VISIBLE);
        s3.setVisibility(View.VISIBLE);
        first_value_textView.setVisibility(View.VISIBLE);
        plus_first_value.setVisibility(View.VISIBLE);
        minus_first_value.setVisibility(View.VISIBLE);
        plus_second_value.setVisibility(View.VISIBLE);
        minus_second_value.setVisibility(View.VISIBLE);
        third_value_textView.setVisibility(View.VISIBLE);
        plus_third_value.setVisibility(View.VISIBLE);
        minus_third_value.setVisibility(View.VISIBLE);
        setsInfinity.setVisibility(View.GONE);
        breaksInfinity.setVisibility(View.GONE);
        s1.setText(R.string.work_time);
        s2.setText(R.string.small_break);
        s3.setText(R.string.long_break);
        first_value_textView.setText(convertCustomTextView(pomValue1));
        second_value_textView.setText(convertCustomTextView(pomValue2));
        third_value_textView.setText(convertCustomTextView(pomValue3));
        //If in mode 3, constraining our add/remove buttons to the "s3" line of objects.
        addParams.topToBottom = R.id.s3;
        addParams.topMargin = 60;
        subParams.topToBottom = R.id.s3;
        subParams.topMargin = 60;
        break;
    }
    //If in mode 2, constraining views for a more compact interface.
    if (mode==2) {
      s2ParamsAdd.topToBottom = R.id.top_anchor;
      s2ParamsSub.topToBottom = R.id.top_anchor;
    } else {
      s2ParamsAdd.topToBottom = R.id.plus_first_value;
      s2ParamsSub.topToBottom = R.id.plus_first_value;
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
            if (setsInfinity.getAlpha()!=1.0f) roundType = 1; else roundType = 2;
          //If Breaks are highlighted red, check if its infinity mode is also highlighted. Use 3/4 for yes/no.
          if (s2.getCurrentTextColor()==Color.RED)
            if (breaksInfinity.getAlpha()!=1.0f) roundType = 3; else roundType = 4;
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
        if (roundIsFading) removeRound();
        if (workoutTime.size()>0) {
          if (workoutTime.size()<=8) {
            //Sets fade positions for rounds. Most recent for subtraction, and -1 (out of bounds) for addition.
            cycleRoundsAdapter.setFadePositions(workoutTime.size()-1, -1);
            cycleRoundsAdapter.notifyDataSetChanged();
          } else {
            //Sets fade positions for rounds. Most recent for subtraction, and -1 (out of bounds) for addition.
            cycleRoundsAdapterTwo.setFadePositions(workoutTime.size()-9, -1);
            cycleRoundsAdapterTwo.notifyDataSetChanged();
          }
          roundIsFading = true;
        } else {
          Toast toast = Toast.makeText(getApplicationContext(), "No rounds to clear!", Toast.LENGTH_SHORT);
          toast.show();
        }
      } else if (mode==3) {
        if (pomValuesTime.size() != 0) {
          cycleRoundsAdapter.setPomFade(false);
          cycleRoundsAdapter.notifyDataSetChanged();
          sub_cycle.setClickable(false);
        } else Toast.makeText(getApplicationContext(), "No Pomodoro cycle to clear!", Toast.LENGTH_SHORT).show();
      }
    }
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
          if (!roundIsFading) cycleRoundsAdapter.setFadePositions(-1, workoutTime.size()-1);
          cycleRoundsAdapter.notifyDataSetChanged();
        } else {
          roundHolderTwo.add(convertedWorkoutTime.get(convertedWorkoutTime.size()-1));
          typeHolderTwo.add(typeOfRound.get(typeOfRound.size()-1));
          if (!roundIsFading) cycleRoundsAdapterTwo.setFadePositions(-1, workoutTime.size()-9);
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
        if (workoutTime.size()<=8) {
          roundHolderOne.set(roundSelectedPosition, convertedWorkoutTime.get(roundSelectedPosition));
          typeHolderOne.set(roundSelectedPosition, typeOfRound.get(roundSelectedPosition));
          if (!roundIsFading) cycleRoundsAdapter.setFadePositions(-1, roundSelectedPosition);
          cycleRoundsAdapter.notifyDataSetChanged();
        } else {
          roundHolderTwo.set(roundSelectedPosition, convertedWorkoutTime.get(convertedWorkoutTime.size()-1));
          typeHolderTwo.set(roundSelectedPosition, typeOfRound.get(typeOfRound.size()-1));
          if (!roundIsFading) cycleRoundsAdapterTwo.setFadePositions(-1, roundSelectedPosition);
          cycleRoundsAdapterTwo.notifyDataSetChanged();
        }
        //Resets round selection boolean.
        roundIsSelected = false;
      }
    }
  }

  public void removeRound () {
    //Cancels animation if we click to remove a round while removal animation for previous one is active.
//    cycleRoundsAdapter.endFade();
    //Rounds only get removed once fade is finished, which we receive status of from callback in adapter.
    if (mode==1) {
      if (workoutTime.size()>0) {
        typeOfRound.remove(typeOfRound.size()-1);
        workoutTime.remove(workoutTime.size()-1);
        convertedWorkoutTime.remove(convertedWorkoutTime.size()-1);
        //Removes rounds from our holder lists. Uses <=7 conditional since they are removed first above.
        if (workoutTime.size()<=7) {
          roundHolderOne.remove(roundHolderOne.size()-1);
          typeHolderOne.remove(typeHolderOne.size()-1);
          //Sets fade positions for rounds. -1 (out of bounds) for both, since this adapter refresh simply calls the post-fade listing of current rounds.
          cycleRoundsAdapter.setFadePositions(-1, -1);
          cycleRoundsAdapter.notifyDataSetChanged();
        } else {
          roundHolderTwo.remove(roundHolderTwo.size()-1);
          typeHolderTwo.remove(typeHolderTwo.size()-1);
          //Sets fade positions for rounds. -1 (out of bounds) for both, since this adapter refresh simply calls the post-fade listing of current rounds.
          cycleRoundsAdapterTwo.setFadePositions(-1,  -1);
          cycleRoundsAdapterTwo.notifyDataSetChanged();
        }
        //If moving from two lists to one, set its visibility and change layout params.
        if (workoutTime.size()==8) {
          roundRecyclerTwo.setVisibility(View.GONE);
          recyclerLayoutOne.leftMargin = 240;
          roundListDivider.setVisibility(View.GONE);
        }
        //Once a round has been removed (and shown as such) in our recyclerView, we always allow for a new fade animation (for the next one).
        roundIsFading = false;
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

  //Queries database for all cycles, using last chosen sort order.
  //Clears adapter arrays and re-populates them with database values. Since this relies on a database query we ONLY call it when:
  //(A)We launch the app for the first time, and (B)We SORT our list, which requires a reshuffling which is easier to do w/ Room commands.
  //All other updates and retrievals can be done directly through arrayLists and their item positions.
  public void queryCycles(boolean queryAll, boolean sort) {

    if (mode==1 || queryAll) {
      if (sort) {
        switch (sortMode) {
          case 1: cyclesList = cyclesDatabase.cyclesDao().loadCyclesMostRecent(); break;
          case 2: cyclesList = cyclesDatabase.cyclesDao().loadCycleLeastRecent(); break;
          case 3: cyclesList = cyclesDatabase.cyclesDao().loadCyclesAlphaStart(); break;
          case 4: cyclesList = cyclesDatabase.cyclesDao().loadCyclesAlphaEnd(); break;
          case 5: cyclesList = cyclesDatabase.cyclesDao().loadCyclesMostItems(); break;
          case 6: cyclesList = cyclesDatabase.cyclesDao().loadCyclesLeastItems(); break;
        }
      } else cyclesList = cyclesDatabase.cyclesDao().loadAllCycles();

      workoutCyclesArray.clear();
      typeOfRoundArray.clear();
      workoutTitleArray.clear();
      totalSetMillisArray.clear();
      totalBreakMillisArray.clear();
      totalCycleCountArray.clear();
      for (int i=0; i<cyclesList.size(); i++) {
        //Adds the concatenated timer String used in each cycle (e.g. XX - XX - XX) to the String Array that was pass into our cycle list's adapter.
        workoutCyclesArray.add(cyclesList.get(i).getWorkoutRounds());
        //Retrieves title for use when editing a cycle.
        workoutTitleArray.add(cyclesList.get(i).getTitle());
        //Adds concatenated roundType String used in each cycle.
        typeOfRoundArray.add(cyclesList.get(i).getRoundType());

        totalSetMillisArray.add(cyclesList.get(i).getTotalSetTime());
        totalBreakMillisArray.add(cyclesList.get(i).getTotalBreakTime());
        totalCycleCountArray.add(cyclesList.get(i).getCyclesCompleted());
      }
    }
    if (mode==3 || queryAll) {
      if (sort) {
        switch (sortModePom) {
          case 1: pomCyclesList = cyclesDatabase.cyclesDao().loadPomCyclesMostRecent(); break;
          case 2: pomCyclesList = cyclesDatabase.cyclesDao().loadPomCyclesLeastRecent(); break;
          case 3: pomCyclesList = cyclesDatabase.cyclesDao().loadPomAlphaStart(); break;
          case 4: pomCyclesList = cyclesDatabase.cyclesDao().loadPomAlphaEnd(); break;
        }
      } else pomCyclesList = cyclesDatabase.cyclesDao().loadAllPomCycles();

      pomArray.clear();
      pomTitleArray.clear();
      for (int i=0; i<pomCyclesList.size(); i++) {
        pomArray.add(pomCyclesList.get(i).getFullCycle());
        pomTitleArray.add(pomCyclesList.get(i).getTitle());

        totalSetMillisArray.add(pomCyclesList.get(i).getTotalWorkTime());
        totalBreakMillisArray.add(pomCyclesList.get(i).getTotalBreakTime());
        totalCycleCountArray.add(pomCyclesList.get(i).getCyclesCompleted());
      }
    }
  }

  //Since we are only updating our adapter's lists, we do not need to reference variables not shown in list (i.e. total times/total cycles). We will only update these in database if they change.
  public void editCycleList(int action) {
    if (action == ADDING_CYCLE) {
      if (mode==1) {
        //If we are adding a new cycle, no need to query the DB for values after save. Just use what has been passed into them from Arrays. This will add them as the correct last position.
        workoutTitleArray.add(cycleTitle);
        typeOfRoundArray.add(roundTypeString);
        workoutCyclesArray.add(workoutString);

        //Adding another array item so lists reflect size of database. Counts are 0 because no times have been logged for latest added cycle/
        totalSetMillisArray.add(0);
        totalBreakMillisArray.add(0);
        totalCycleCountArray.add(0);
      }
      if (mode==3) {
        pomTitleArray.add(cycleTitle);
        pomArray.add(pomString);
      }
    }
    else if (action == EDITING_CYCLE) {
      if (mode==1) {
        //If we have the values in our workOutTime and typeOFRound arrays already, simply pass them into cycleList's adapter arrays.
        workoutTitleArray.set(receivedPos, cycleTitle);
        workoutCyclesArray.set(receivedPos, workoutString);
        typeOfRoundArray.set(receivedPos, roundTypeString);
      }
      if (mode==3) {
        pomTitleArray.set(receivedPos, cycleTitle);
        pomArray.set(receivedPos, pomString);
      }
    } else if (action == DELETING_CYCLE) {
      if (mode==1) {
        workoutTitleArray.remove(receivedPos);
        typeOfRoundArray.remove(receivedPos);
        workoutCyclesArray.remove(receivedPos);
      }
      if (mode==3) {
        pomTitleArray.remove(receivedPos);
        pomArray.remove(receivedPos);
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
        //Populating our current cycle's list of rounds via Integer Arrays directly with adapter String list values, instead of querying them from the database. This is used whenever we do not need a db query, such as editing or adding a new cycle.
        String[] fetchedRounds = workoutCyclesArray.get(receivedPos).split(" - ");
        String[] fetchedRoundType = typeOfRoundArray.get(receivedPos).split(" - ");

        for (int i=0; i<fetchedRounds.length; i++) workoutTime.add(Integer.parseInt(fetchedRounds[i]));
        for (int j=0; j<fetchedRoundType.length; j++) typeOfRound.add(Integer.parseInt(fetchedRoundType[j]));
        cycleTitle = workoutTitleArray.get(receivedPos);
        break;
      case 3:
        pomValuesTime.clear();
        String[] fetchedPomCycle = pomArray.get(receivedPos).split(" - ");
        for (int i=0; i<fetchedPomCycle.length; i++) pomValuesTime.add(Integer.parseInt(fetchedPomCycle[i]));
        cycleTitle = pomTitleArray.get(receivedPos);
        break;
    }
  }

  public void launchTimerCycle(boolean newCycle, boolean saveToDB) {
    //Used to toggle views/updates on Main for visually smooth transitions between popUps.
    launchingTimer = true;
    //Used for primary key ID of database position, passed into Timer class so we can delete the selected cycle.
    // If we are launching a new cycle, set a conditional for an empty title, a conditional for an empty list, and run the saveCycles() method to automatically save it in our database. For both new and old cycles, send all necessary intents to our Timer class.
    if (newCycle) {
      //If trying to add new cycle and rounds are at 0, pop a toast and exit method. Otherwise, set a title and proceed to intents.
      if ((mode==1 && workoutTime.size()==0) || (mode==3 && pomValuesTime.size()==0)) {
        runOnUiThread(()-> Toast.makeText(getApplicationContext(), "Cycle cannot be empty!", Toast.LENGTH_SHORT).show());
        return;
      }
      //Since this is a new Cycle, we automatically save it to database.
      saveCycles(true);
    } else {
      //Only calls populateRoundList() if NOT editing, since it also clears our round lists and we need them retained.
      if (!editingCycle) populateRoundList();
      //Updates changes made to cycle if we are launching it.
      if (saveToDB) saveCycles(false);
    }
    //Todo: editCycles can't launch this window. Set it as a popup from w/ in edit popup?
    mHandler.postDelayed(()-> {
      timerPopUpWindow.showAtLocation(cl, Gravity.NO_GRAVITY, 0, 0);
      editCyclesPopupWindow.dismiss();
      populateTimerUI();
    },10);
  }

  private void saveCycles(boolean newCycle) {
    //sets boolean used in launchCycles.
    isNewCycle = newCycle;
    //Gets current date for use in empty titles.
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMMM d yyyy - hh:mma", Locale.getDefault());
    String date = dateFormat.format(calendar.getTime());

    //Sets up Strings to save into database.
    Gson gson = new Gson();
    workoutString = "";
    roundTypeString = "";
    pomString = "";

    int cycleID = 0;
    switch (mode) {
      case 1:
        //If coming from FAB button, create a new instance of Cycles. If coming from a position in our database, get the instance of Cycles in that position.
        if (newCycle) cycles = new Cycles(); else if (cyclesList.size()>0) {
          cycleID = cyclesList.get(receivedPos).getId();
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
          if (newCycle) {
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
            editCycleList(ADDING_CYCLE);
          } else {
            cycles.setTitle(cycleTitle);
            //If cycle is old, update current row.
            cyclesDatabase.cyclesDao().updateCycles(cycles);
            //Editing adapter list.
            editCycleList(EDITING_CYCLE);
          }
        }
        break;
      case 3:
        //If coming from FAB button, create a new instance of PomCycles. If coming from a position in our database, get the instance of PomCycles in that position.
        if (newCycle) pomCycles = new PomCycles(); else if (pomCyclesList.size()>0) {
          cycleID = pomCyclesList.get(receivedPos).getId();
          pomCycles = cyclesDatabase.cyclesDao().loadSinglePomCycle(cycleID).get(0);
        }
        pomString = gson.toJson(pomValuesTime);
        pomString = friendlyString(pomString);

        if (!pomString.equals("")) {
          pomCycles.setFullCycle(pomString);
          pomCycles.setTimeAccessed(System.currentTimeMillis());;
          if (newCycle) {
            pomCycles.setTimeAdded(System.currentTimeMillis());
            if (cycleTitle.isEmpty()) cycleTitle = date;
            pomCycles.setTitle(cycleTitle);
            cyclesDatabase.cyclesDao().insertPomCycle(pomCycles);
            //Adding to adapter list.
            editCycleList(ADDING_CYCLE);
          } else {
            pomCycles.setTitle(cycleTitle);
            cyclesDatabase.cyclesDao().updatePomCycles(pomCycles);
            editCycleList(EDITING_CYCLE);
          }
        }
        break;
    }
  }

  private void deleteCycle(boolean deleteAll) {
    queryCycles(false, false);
    int cycleID;
    boolean emptyCycle = false;
    switch (mode) {
      case 1:
        if (!deleteAll) {
          cycleID = cyclesList.get(receivedPos).getId();
          cycles = cyclesDatabase.cyclesDao().loadSingleCycle(cycleID).get(0);
          cyclesDatabase.cyclesDao().deleteCycle(cycles);
          runOnUiThread(()-> {
            savedCycleAdapter.notifyDataSetChanged();
          });
        } else if (cyclesList.size()>0) {
          cyclesDatabase.cyclesDao().deleteAllCycles();
          runOnUiThread(()->{
            //Clears adapter arrays and populates recyclerView with (nothing) since arrays are now empty. Also called notifyDataSetChanged().
            workoutCyclesArray.clear();
            typeOfRoundArray.clear();
            workoutTitleArray.clear();
            totalSetMillisArray.clear();
            totalBreakMillisArray.clear();
            totalCycleCountArray.clear();
            savedCycleAdapter.notifyDataSetChanged();
          });
        } else emptyCycle = true;
        break;
      case 3:
        if (!deleteAll) {
          cycleID = pomCyclesList.get(receivedPos).getId();
          pomCycles = cyclesDatabase.cyclesDao().loadSinglePomCycle(cycleID).get(0);
          cyclesDatabase.cyclesDao().deletePomCycle(pomCycles);
          runOnUiThread(()-> {
            savedPomCycleAdapter.notifyDataSetChanged();
          });
        } else if (pomCyclesList.size()>0) {
          cyclesDatabase.cyclesDao().deleteAllPomCycles();
          runOnUiThread(()->{
            //Todo: When we have them, total time + cycles arrays get cleared here.
            pomArray.clear();
            pomTitleArray.clear();
            savedPomCycleAdapter.notifyDataSetChanged();
          });
        } else emptyCycle = true;
        break;
    }
    runOnUiThread(()-> {
      //Sets textView for empty cycle list to visible and recyclerView to gone.
      checkEmptyCycles();
    });
    if (emptyCycle){
      runOnUiThread(() -> {
        Toast.makeText(getApplicationContext(), "Nothing saved!", Toast.LENGTH_SHORT).show();
      });
    }
  }

  ///////////////////////////////////////////////////////////////////////////

  public void defineObjectAnimator(long duration) {
    objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", (int) maxProgress, 0);
    objectAnimator.setInterpolator(new LinearInterpolator());
    objectAnimator.setDuration(duration);
    objectAnimator.start();
  }

  public void startObjectAnimator() {
    switch (mode) {
      case 1:
        customProgressPause = maxProgress;
        if (typeOfRound.get(currentRound).equals(1)) {
          //If progress bar is at max value, round has not begun.
          if (progressBar.getProgress()==maxProgress) {
            //Starts object animator.
            defineObjectAnimator(setMillis);
            //Used for pause/resume toggle.
            timerIsPaused = false;
            //Used in pause/resume. If timer HAS ended, that method calls reset. Otherwise, it pauses or resumes timer.
            timerEnded = false;
            //Unchanging start point of setMillis used to count total set time over multiple rounds.
            permSetMillis = setMillis;
          } else {
            setMillis = setMillisUntilFinished;
            if (objectAnimator != null) objectAnimator.resume();
          }
        } else if (typeOfRound.get(currentRound).equals(3)) {
          if (progressBar.getProgress()==maxProgress) {
            //Used for pause/resume and fading text in (i.e. timeLeft or timePaused).
            timerIsPaused = false;
            //Starts object animator.
            defineObjectAnimator(breakMillis);
            //Unchanging start point of breakMillis used to count total set time over multiple rounds.
            permBreakMillis = breakMillis;
          } else {
            breakMillis = breakMillisUntilFinished;
            if (objectAnimator != null) objectAnimator.resume();
          }
        }
        break;
      case 3:
        if (progressBar.getProgress()==maxProgress){
          //Ensures any features meant for running timer cannot be executed here.
          timerIsPaused = false;
          pomMillis = pomValuesTime.get(pomDotCounter);
          //Starts object animator.
          defineObjectAnimator(pomMillis);
          timerEnded = false;
          //Unchanging start point of pomMillis used to count total set time over multiple rounds. Using SET and BREAK vars since modes are exclusive, and it saves on variable creation.
          switch (pomDotCounter) {
            case 0: case 2: case 4: case 6:
              permSetMillis = pomMillis;
              break;
            case 1: case 3: case 5: case 7:
              permBreakMillis = pomMillis;
              break;
          }
        } else {
          pomMillis = pomMillisUntilFinished;
          if (objectAnimator != null) objectAnimator.resume();
        }
        break;
    }
  }

  public void startSetTimer() {
    setTextSize(setMillis);

    timer = new CountDownTimer(setMillis, 50) {
      @Override
      public void onTick(long millisUntilFinished) {
        customProgressPause = (int) objectAnimator.getAnimatedValue();
        setMillis = millisUntilFinished;

        //If timer began @ >=60 seconds and is now less than, enlarge text size to fill progressBar.
        if (textSizeIncreased) if (setMillis < 59000) {
          textSizeIncreased = false;
        }
        //Displays Long->String conversion of time left every tick.
        timeLeft.setText(convertSeconds((setMillis + 999) / 1000));
        if (setMillis < 500) timerDisabled = true;

        //Sets value to difference between starting millis and current millis (e.g. 45000 left from 50000 start is 5000/5 sec elapsed).
        setMillisHolder = permSetMillis - setMillis;
        //Temporary value for current round, using totalSetMillis which is our permanent value.
        tempSetMillis = totalSetMillis + setMillisHolder;
        total_set_time.setText(convertSeconds(tempSetMillis / 1000));
        //Refreshes Canvas so dots fade.
        dotDraws.reDraw();
      }

      @Override
      public void onFinish() {
        nextRound(false);
      }
    }.start();
  }

  public void startBreakTimer() {
    if (mode == 1) {
      setTextSize(breakMillis);

      timer = new CountDownTimer(breakMillis, 50) {
        @Override
        public void onTick(long millisUntilFinished) {
          customProgressPause = (int) objectAnimator.getAnimatedValue();
          breakMillis = millisUntilFinished;

          if (textSizeIncreased) if (breakMillis < 59000) {
            textSizeIncreased = false;
          }

          timeLeft.setText(convertSeconds((millisUntilFinished + 999) / 1000));
          if (breakMillis < 500) timerDisabled = true;

          //For "Total Break" times.
          breakMillisHolder = permBreakMillis - breakMillis;
          tempBreakMillis = totalBreakMillis + breakMillisHolder;
          total_break_time.setText(convertSeconds(tempBreakMillis / 1000));

          //Refreshes Canvas so dots fade.
          dotDraws.reDraw();
        }

        @Override
        public void onFinish() {
          nextRound(false);
        }
      }.start();
    }
  }

  public void startPomTimer() {
    setTextSize(pomMillis);

    timer = new CountDownTimer(pomMillis, 50) {
      @Override
      public void onTick(long millisUntilFinished) {
        pomProgressPause = (int) objectAnimator.getAnimatedValue();
        pomMillis = millisUntilFinished;

        if (textSizeIncreased) if (pomMillis < 59000) {
          changeTextSize(valueAnimatorUp, timeLeft);
          textSizeIncreased = false;
        }

        timeLeft.setText(convertSeconds((pomMillis + 999) / 1000));
        if (pomMillis < 500) timerDisabled = true;

        //Switches total time count depending on which round we're on.
        switch (pomDotCounter) {
          case 0:
          case 2:
          case 4:
          case 6:
            setMillisHolder = permSetMillis - pomMillis;
            tempSetMillis = totalSetMillis + setMillisHolder;
            total_set_time.setText(convertSeconds(tempSetMillis / 1000));
            break;
          case 1:
          case 3:
          case 5:
          case 7:
            breakMillisHolder = permBreakMillis - pomMillis;
            tempBreakMillis = totalBreakMillis + breakMillisHolder;
            total_break_time.setText(convertSeconds(tempBreakMillis / 1000));
            break;
        }
        //Refreshes Canvas so dots fade.
        dotDraws.reDraw();
      }

      @Override
      public void onFinish() {
        nextRound(false);
      }
    }.start();
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

  public void changeTextSize(ValueAnimator va, TextView textView) {
    sizeAnimator = va;
    sizeAnimator.addUpdateListener(animation -> {
      float sizeChange = (float) va.getAnimatedValue();
      textView.setTextSize(sizeChange);
    });
    sizeAnimator.setRepeatCount(0);
    sizeAnimator.start();
  }

  //Sets text size at round start. textSizeIncreased is set to true if timer is >=60 seconds, so the text size can be reduced mid-timer as it drops below.
  public void setTextSize(long millis) {
    if (millis>=59000) {
      timeLeft.setTextSize(70f);
      textSizeIncreased = true;
    } else {
      timeLeft.setTextSize(90f);
    }
  }

  //Used in count up mode to animate text size changes in our runnables.
  public void animateTextSize(long millis) {
    if (textSizeReduced) {
      if (millis >=60000) {
        changeTextSize(valueAnimatorDown, timeLeft);
        textSizeReduced = false;
      }
    }
  }

  public void hideCounter(boolean hiding) {
    if (hiding) {
      total_set_header.setVisibility(View.GONE);
      total_set_time.setVisibility(View.GONE);
      total_break_header.setVisibility(View.GONE);
      total_break_time.setVisibility(View.GONE);
      reset_total_times.setVisibility(View.GONE);
    } else {
      total_set_header.setVisibility(View.VISIBLE);
      total_set_time.setVisibility(View.VISIBLE);
      total_break_header.setVisibility(View.VISIBLE);
      total_break_time.setVisibility(View.VISIBLE);
      reset_total_times.setVisibility(View.VISIBLE);
    }
  }

  public String convertStopwatch(long seconds) {
    long minutes;
    long roundedSeconds;
    DecimalFormat df = new DecimalFormat("0");
    DecimalFormat df2 = new DecimalFormat("00");
    if (seconds >= 60) {
      minutes = seconds / 60;
      roundedSeconds = seconds % 60;
      if (minutes >= 10 && timeLeft.getTextSize() != 70f) timeLeft.setTextSize(70f);
      return (df.format(minutes) + ":" + df2.format(roundedSeconds));
    } else {
      if (timeLeft.getTextSize() != 90f) timeLeft.setTextSize(90f);
      return df.format(seconds);
    }
  }

  //Retrieves visible positions from recyclerView and passes them into adapter for use in fading effect.
  public void getVisibleLapPositions() {
    int first = lapLayout.findFirstVisibleItemPosition();
    int last = lapLayout.findLastVisibleItemPosition();
    lapAdapter.receiveVisiblePositions(first, last);
  }

  public void newLap() {
    if (empty_laps.getVisibility()==View.VISIBLE) empty_laps.setVisibility(View.INVISIBLE);
    double newSeconds = msReset / 60;
    double newMinutes = newSeconds / 60;

    double savedMinutes = 0;
    double savedSeconds = 0;
    double savedMs = 0;

    String[] holder = null;
    if (!timerIsPaused) {
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
    //Todo: If we want end of cycle to avoid blue progressBar entirely, we need to change this for last round execution.
    //Fade effect to smooth out progressBar and timer text after animation.
    progressBar.startAnimation(fadeProgressOut);
    timeLeft.startAnimation(fadeProgressOut);
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
      progressBarValueHolder = 30000;
      //Fade out effect for dots so they always end their fade @ 105 alpha (same alpha they retain once completed).
      mHandler.post(endFade);
      //Saves total set/break times.
      saveTotalTimes();
      //If skipping round manually, cancel timer and objectAnimator.
      if (endingEarly) {
        if (timer != null) timer.cancel();
        if (objectAnimator != null) objectAnimator.cancel();
        progressBar.setProgress(0);
      }
      ///------------------------////////

      switch (typeOfRound.get(currentRound)) {
        case 1:
          //End of round, setting textView to 0.
          timeLeft.setText("0");
          //tempMillis is used to retain value in active runnables. Here, we set our static totalMillis to the value that has been iterated up to.
          totalSetMillis = tempSetMillis;
          //Rounds our total millis up to the nearest 1000th and divides to whole int to ensure synchronicity w/ display (e.g. (4950 + 100) / 1000 == 5).
          tempSetMillis = ((totalSetMillis + 100) / 1000) * 1000;
          total_set_time.setText(convertSeconds(tempSetMillis/1000));
          break;
        case 3:
          //End of round, setting textView to 0.
          timeLeft.setText("0");
          //tempMillis is used ]to retain value in active runnables. Here, we set our static totalMillis to the value that has been iterated up to.
          totalBreakMillis = tempBreakMillis;
          //Rounds our total millis up to the nearest 1000th and divides to whole int to ensure synchronicity w/ display (e.g. (4950 + 100) / 1000 == 5).
          tempBreakMillis = ((totalBreakMillis + 100) / 1000) * 1000;
          total_break_time.setText(convertSeconds(tempBreakMillis/1000));
          break;
        case 2:
          totalSetMillis = tempSetMillis;
          //Infinite round has ended, so we cancel the runnable
          mHandler.removeCallbacks(secondsUpSetRunnable);
          total_set_time.setText(convertSeconds(tempSetMillis/1000));
          break;
        case 4:
          totalBreakMillis = tempBreakMillis;
          //Infinite round has ended, so we cancel the runnable
          mHandler.removeCallbacks(secondsUpBreakRunnable);
          total_break_time.setText(convertSeconds(tempBreakMillis/1000));
          break;
      }

      mHandler.postDelayed(() -> {
        //Subtracts from rounds remaining.
        numberOfRoundsLeft--;
        //Iterates up in our current round count. This is used to determine which type of round will execute next (below).
        currentRound++;
        //Updates dotDraws class w/ round count.
        dotDraws.updateWorkoutRoundCount(startRounds, numberOfRoundsLeft);
        //Resets the alpha value we use to fade dots back to 255 (fully opaque).
        dotDraws.resetDotAlpha();
        //Resetting values for count-up modes. Simpler to keep them out of switch statement.
        countUpMillisSets = 0;
        countUpMillisBreaks = 0;
        countUpMillisHolder = 0;
        baseTime = System.currentTimeMillis();
        //Next round begins active by default, so we set our paused mode to false.
        timerIsPaused = false;
        //Executes next round based on which type is indicated in our typeOfRound list.
        if (numberOfRoundsLeft>0) {
          switch (typeOfRound.get(currentRound)) {
            case 1:
              setMillis = workoutTime.get(workoutTime.size() - numberOfRoundsLeft);
              timeLeft.setText(convertSeconds((setMillis + 999) / 1000));
              startObjectAnimator();
              startSetTimer();
              break;
            case 3:
              breakMillis = workoutTime.get(workoutTime.size() - numberOfRoundsLeft);
              timeLeft.setText(convertSeconds((breakMillis + 999) / 1000));
              startObjectAnimator();
              startBreakTimer();
              break;
            case 2:
              timeLeft.setText("0");
              defineObjectAnimator(30000);
              mHandler.post(secondsUpSetRunnable);
              break;
            case 4:
              timeLeft.setText("0");
              defineObjectAnimator(30000);
              mHandler.post(secondsUpBreakRunnable);
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
          customCyclesDone++;
          cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));
        }
        //Re-enables timer clicks, which are disabled for a brief period right before and after round timer ends.
        timerDisabled = false;
        //Re-enables the button that calls this method, now that it has completed.
        next_round.setEnabled(true);
      },750);
    }

    if (mode==3) {
      timeLeft.setText("0");
      mHandler.post(endFade);
      //If skipping round manually, cancel timer and objectAnimator.
      if (endingEarly) {
        if (timer != null) timer.cancel();
        if (objectAnimator != null) objectAnimator.cancel();
        progressBar.setProgress(0);
      }

      mHandler.postDelayed(()-> {
        pomDotCounter++;
        dotDraws.pomDraw(pomDotCounter, pomValuesTime);
        dotDraws.resetDotAlpha();

        if (pomDotCounter<=7) {
          pomMillis = pomValuesTime.get(pomDotCounter);
          timeLeft.setText(convertSeconds((pomMillis) / 1000));
          startObjectAnimator();
          startPomTimer();
        } else {
          //Continuous animation for end of cycle.
          animateEnding();
          progressBar.setProgress(0);
          timerEnded = true;
          cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));
        }
        timerDisabled = false;
        next_round.setEnabled(true);
      },750);
    }
  }

  public void pauseAndResumeTimer(int pausing) {
    //disabledTimer booleans are to prevent ANY action being taken.
    if (!timerDisabled) {
      if (fadeInObj != null) fadeInObj.cancel();
      if (fadeOutObj != null) fadeOutObj.cancel();
      switch (mode) {
        case 1:
          if (!timerEnded) {
            if (pausing == PAUSING_TIMER) {
              //Boolean that determines whether we are pausing or resuming timer.
              timerIsPaused = true;
              //Cancels timer object (need to recreate) and pauses object animator.
              if (timer != null) timer.cancel();
              if (objectAnimator != null) objectAnimator.pause();
              reset.setVisibility(View.VISIBLE);

              switch (typeOfRound.get(currentRound)) {
                case 1:
                  setMillisUntilFinished = setMillis;
                  break;
                case 3:
                  breakMillisUntilFinished = breakMillis;
                  break;
                case 2:
                  countUpMillisHolder = countUpMillisSets;
                  mHandler.removeCallbacks(secondsUpSetRunnable);
                  break;
                case 4:
                  countUpMillisHolder = countUpMillisBreaks;
                  mHandler.removeCallbacks(secondsUpBreakRunnable);
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
                case 3:
                  startObjectAnimator();
                  startBreakTimer();
                  break;
                case 2:
                  //Uses the current time as a base for our count-up rounds.
                  baseTime = System.currentTimeMillis();
                  if (progressBar.getProgress()==maxProgress) defineObjectAnimator(progressBarValueHolder); else if (objectAnimator!=null) objectAnimator.resume();
                  countUpMillisSets = countUpMillisHolder;
                  mHandler.post(secondsUpSetRunnable);
                  break;
                case 4:
                  baseTime = System.currentTimeMillis();
                  if (progressBar.getProgress()==maxProgress) defineObjectAnimator(5000); else if (objectAnimator!=null) objectAnimator.resume();
                  countUpMillisBreaks = countUpMillisHolder;
                  mHandler.post(secondsUpBreakRunnable);
                  break;
              }
            }
          } else {
            //If cycle has run its course, reset the cycle and re-enable the next_round button.
            resetTimer();
          }
          break;
        case 3:
          if (reset.getText().equals(getString(R.string.confirm_cycle_reset)))
            reset.setText(R.string.reset);
          if (!timerEnded) {
            if (pausing == PAUSING_TIMER) {
              timerIsPaused = true;
              pomMillisUntilFinished = pomMillis;
              if (objectAnimator != null) objectAnimator.pause();
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
            timerIsPaused = false;
            new_lap.setAlpha(1.0f);
            new_lap.setEnabled(true);
            //Main runnable for Stopwatch.
            mHandler.post(stopWatchRunnable);
          } else if (pausing == PAUSING_TIMER) {
            reset.setVisibility(View.VISIBLE);
            mHandler.removeCallbacksAndMessages(null);
            timerIsPaused = true;
            new_lap.setAlpha(0.3f);
            new_lap.setEnabled(false);
            break;
          }
      }
    }
  }

  public void populateTimerUI() {
    cycle_header_text.setText(cycleTitle);
    dotDraws.resetDotAlpha();
    //Default views for Timer.
    total_set_header.setVisibility(View.VISIBLE);
    total_set_time.setVisibility(View.VISIBLE);
    total_break_header.setVisibility(View.VISIBLE);
    total_break_time.setVisibility(View.VISIBLE);
    lapRecycler.setVisibility(View.INVISIBLE);
    next_round.setVisibility(View.VISIBLE);
    reset_total_times.setVisibility(View.VISIBLE);
    new_lap.setVisibility(View.INVISIBLE);

    //Setting values based on first round in cycle. Might make this is a global method.
    switch (mode) {
      case 1:
        //Resets base progressBar duration for count-up rounds.
        progressBarValueHolder = 30000;
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
              setTextSize(setMillis);
              break;
            case 3:
              breakMillis = workoutTime.get(0);
              timeLeft.setText(convertSeconds((breakMillis + 999) / 1000));
              setTextSize(breakMillis);
              break;
            case 2:
            case 4:
              timeLeft.setText("0");
              setTextSize(0);
              break;
          }
        }
        break;
      case 3:
        //Here is where we set the initial millis Value of first pomMillis. Set again on change on our value runnables.
        if (pomValuesTime.size() > 0) {
          pomMillis = pomValuesTime.get(0);
          timeLeft.setText(convertSeconds((pomMillis + 999) / 1000));
          dotDraws.pomDraw(pomDotCounter,pomValuesTime);
          //Sets initial text size.
          setTextSize(pomMillis);
        }
        break;
      case 4:
        timerDisabled = false;
        //Views for stopwatch.
        total_set_header.setVisibility(View.INVISIBLE);
        total_set_time.setVisibility(View.INVISIBLE);
        total_break_header.setVisibility(View.INVISIBLE);
        total_break_time.setVisibility(View.INVISIBLE);
        lapRecycler.setVisibility(View.VISIBLE);
        next_round.setVisibility(View.INVISIBLE);
        reset_total_times.setVisibility(View.GONE);
        new_lap.setVisibility(View.VISIBLE);
        timeLeft.setText("0");
        cycles_completed.setText(getString(R.string.laps_completed, "0"));
        //Modifies top layout.
        ConstraintLayout.LayoutParams completedLapsParam = (ConstraintLayout.LayoutParams) cycles_completed.getLayoutParams();
        ConstraintLayout.LayoutParams lapRecyclerParams = (ConstraintLayout.LayoutParams) lapRecycler.getLayoutParams();
        completedLapsParam.topMargin = 0;
        lapRecyclerParams.topMargin = 60;
        //Stopwatch always starts at 0.
        setTextSize(0);
        break;
    }
  }

  public void resetTimer() {
    timerIsPaused = true;
    timerEnded = false;
    reset.setVisibility(View.INVISIBLE);
    progressBar.setProgress(10000);
    if (timer != null) timer.cancel();
    if (objectAnimator != null) objectAnimator.cancel();
    if (endAnimation!=null) endAnimation.cancel();
    next_round.setEnabled(true);
    //Stores cumulative time valuation.
    saveTotalTimes();
    switch (mode) {
      case 1:
        customProgressPause = maxProgress;
        //Resetting millis values of count up mode to 0.
        countUpMillisSets = 0;
        countUpMillisBreaks = 0;
        countUpMillisHolder = 0;
        //Removing timer callbacks for count up mode.
        mHandler.removeCallbacks(secondsUpSetRunnable);
        mHandler.removeCallbacks(secondsUpBreakRunnable);
        //Setting total number of rounds.
        startRounds = workoutTime.size();
        numberOfRoundsLeft = startRounds;
        //Resets current round counter.
        currentRound = 0;
        break;
      case 3:
        pomDotCounter = 0;
        pomProgressPause = maxProgress;
        if (objectAnimator != null) objectAnimator.cancel();
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
        lapAdapter.notifyDataSetChanged();
        empty_laps.setVisibility(View.VISIBLE);
        break;
    }
    populateTimerUI();
  }

  public void saveTotalTimes() {
    switch (mode) {
      //Re-using set/break vars for work/break in Pom mode since modes are exclusive.
      case 1: case 3:
        //Sets our total millis to the temp value iterated up in our runnable.
        totalSetMillis = tempSetMillis;
        //Sets our temp value, which will be picked up again in our runnable next round, to the new total rounded up to nearest 1000th. These expressions seem redundant, but are necessary since our timers update continuously.
        tempSetMillis = ((totalSetMillis + 100) / 1000) * 1000;
        total_set_time.setText(convertSeconds(tempSetMillis/1000));
        //Sets our totalMillis to the new, revised and rounded tempMillis so when it's used again, it syncs up.
        totalSetMillis = tempSetMillis;

        //Sets our total millis to the temp value iterated up in our runnable.
        totalBreakMillis = tempBreakMillis;
        //Sets our temp value, which will be picked up again in our runnable next round, to the new total rounded up to nearest 1000th. These expressions seem redundant, but are necessary since our timers update continuously.
        tempBreakMillis = ((totalBreakMillis + 100) / 1000) * 1000;
        total_break_time.setText(convertSeconds(tempBreakMillis/1000));
        //Sets our totalMillis to the new, revised and rounded tempMillis so when it's used again, it syncs up.
        totalBreakMillis = tempBreakMillis;
        break;
    }
  }

  //Contains all the stuff we want done when we exit our timer. Called in both onBackPressed and our exitTimer button.
  public void exitTimer() {
    //Saves total elapsed time for various rounds, as well as completed cycles. tempMillis vars are used since these are the ones that hold a constant reference to our values. In Main, we have inserted "0" values for new db entries, so we can simply use an update method here.
    switch (mode) {
      case 1:
        cycles.setTotalSetTime((int) tempSetMillis / 1000);
        cycles.setTotalBreakTime((int) tempBreakMillis / 1000);
        cycles.setCyclesCompleted(customCyclesDone);
        cyclesDatabase.cyclesDao().updateCycles(cycles);
        break;
      case 3:
        pomCycles.setTotalWorkTime((int) tempSetMillis / 1000);
        pomCycles.setTotalBreakTime((int) tempBreakMillis / 1000);
        pomCycles.setCyclesCompleted(customCyclesDone);
        cyclesDatabase.cyclesDao().updatePomCycles(pomCycles);
        break;
    }
  }

  public void deleteTotalTimes() {
    if (mode!=4) delete_text.setText(R.string.delete_total_times); else delete_text.setText(R.string.delete_total_laps);
    deleteCyclePopupWindow.showAtLocation(cl, Gravity.CENTER_HORIZONTAL, 0, -100);
  }

  //Todo: Redundantly named method formerly in Timer class. Need altered one for deleting total times/cycles.
//  public void deleteCycle(int typeOfDeletion) {
//    //Deletes the currently displayed cycle.
//    if (typeOfDeletion == DELETING_CYCLE) {
//      switch (mode) {
//        case 1:
//          cyclesDatabase.cyclesDao().deleteCycle(cycles); break;
//        case 3:
//          cyclesDatabase.cyclesDao().deletePomCycle(pomCycles); break;
//      }
//
//      //Deletes total set/break/pom times and cycles completed.
//    } else if (typeOfDeletion == DELETING_TIMES) {
//      switch (mode) {
//        case 1:
//          cyclesDatabase.cyclesDao().deleteTotalTimesCycle(); break;
//        case 3:
//          cyclesDatabase.cyclesDao().deleteTotalTimesPom();  break;
//      }
//
//      runOnUiThread(() -> {
//        deleteCyclePopupWindow.dismiss();
//        //Resets all total times to 0.
//        totalSetMillis = 0;
//        tempSetMillis = 0;
//        totalBreakMillis = 0;
//        tempBreakMillis = 0;
//
//        permSetMillis = ((setMillis+100) / 1000) * 1000;
//        permBreakMillis = ((breakMillis+100) / 1000) * 1000;
//        customCyclesDone = 0;
//
//        total_set_time.setText("0");
//        total_break_time.setText("0");
//        cycles_completed.setText(getString(R.string.cycles_done, "0"));
//      });
//    }
//  }
}