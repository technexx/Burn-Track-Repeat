package com.example.tragic.irate.simple.stopwatch;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.TypedArrayUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tragic.irate.simple.stopwatch.Database.Cycles;
import com.example.tragic.irate.simple.stopwatch.Database.CyclesBO;
import com.example.tragic.irate.simple.stopwatch.Database.CyclesDatabase;
import com.example.tragic.irate.simple.stopwatch.Database.PomCycles;
import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class TimerInterface extends AppCompatActivity implements DotDraws.sendAlpha {

  ProgressBar progressBar;
  ;
  ImageView stopWatchView;
  TextView timeLeft;
  TextView timePaused;
  TextView msTime;
  TextView msTimePaused;
  CountDownTimer timer;
  TextView reset;
  ObjectAnimator objectAnimator;
  Animation endAnimation;
  public Handler mHandler;
  TextView lastTextView;

  String cycle_title;
  int overSeconds;
  TextView overtime;

  TextView cycle_header_text;
  TextView cycles_completed;
  Button cycle_reset;
  ImageButton new_lap;
  ImageButton next_round;
  ImageButton reset_total_times;
  TextView total_set_header;
  TextView total_break_header;
  TextView total_set_time;
  TextView total_break_time;

  int PAUSING_TIMER = 1;
  int RESUMING_TIMER = 2;
  int RESETTING_TIMER = 3;
  int RESTARTING_TIMER = 4;
  int DELETING_CYCLE = 1;
  int DELETING_TIMES = 2;
  int movingBOCycle;

  long setMillis;
  long totalSetMillis;
  long tempSetMillis;
  long setMillisHolder;
  long permSetMillis;

  long breakMillis;
  long breaksOnlyMillis;
  long totalBreakMillis;
  long breakMillisHolder;
  long tempBreakMillis;
  long permBreakMillis;

  long pomMillis;

  int maxProgress = 10000;
  int customProgressPause = 10000;
  int breaksOnlyProgressPause = 10000;
  int pomProgressPause = 10000;
  long setMillisUntilFinished;
  long breakMillisUntilFinished;
  long breaksOnlyMillisUntilFinished;
  long pomMillisUntilFinished;
  Runnable endFade;
  Runnable ot;
  int pomDotCounter = 1;

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
  String resetEntries;
  int newMsConvert;
  int savedMsConvert;
  ArrayList<String> currentLapList;
  ArrayList<String> savedLapList;
  Runnable stopWatchRunnable;

  int customCyclesDone;
  int breaksOnlyCyclesDone;
  int pomCyclesDone;
  int lapsNumber;

  int numberOfSets;
  int numberOfBreaks;
  int numberOfBreaksOnly;
  int startSets;
  int startBreaksOnly;

  boolean modeOneTimerEnded;
  boolean modeTwoTimerEnded;
  boolean modeThreeTimerEnded;
  boolean onBreak;
  boolean timerDisabled;
  boolean boTimerDisabled;
  boolean pomTimerDisabled;
  boolean customHalted = true;
  boolean breaksOnlyHalted = true;
  boolean pomHalted = true;
  boolean stopwatchHalted = true;

  ObjectAnimator fadeInObj;
  ObjectAnimator fadeOutObj;
  RecyclerView lapRecycler;
  LapAdapter lapAdapter;
  LinearLayoutManager lapLayout;

  DotDraws dotDraws;
  int mode = 1;
  ValueAnimator sizeAnimator;
  ValueAnimator valueAnimatorDown;
  ValueAnimator valueAnimatorUp;
  AlphaAnimation fadeIn;
  AlphaAnimation fadeOut;
  boolean textSizeIncreased;
  //Always true initially, since infinity mode starts at 0.
  boolean textSizeReduced = true;

  ArrayList<String> setsArray;
  ArrayList<String> breaksArray;
  ArrayList<String> breaksOnlyArray;
  ArrayList<String> pomArray;
  ArrayList<Integer> customSetTime;
  ArrayList<Integer> customBreakTime;
  ArrayList<Integer> breaksOnlyTime;
  ArrayList<Integer> pomValuesTime;
  ArrayList<Integer> zeroArraySets;
  ArrayList<Integer> zeroArrayBreaks;

  boolean activePomCycle;
  boolean setBegun;
  boolean breakBegun;
  boolean breakOnlyBegun;
  boolean pomBegun;
  boolean isOvertimeRunning;

  SharedPreferences sharedPreferences;
  SharedPreferences.Editor prefEdit;
  int receivedAlpha;
  int fadeVar;
  MaterialButton pauseResumeButton;

  int countUpMillisSets;
  int countUpMillisBreaks;
  int countUpMillisBO;
  public Runnable secondsUpSetRunnable;
  public Runnable secondsUpBreakRunnable;
  public Runnable secondsUpBORunnable;
  boolean setsAreCountingUp;
  boolean breaksAreCountingUp;
  boolean breaksOnlyAreCountingUp;

  ImageButton exit_timer;

  ConstraintLayout timerInterface;
  View deleteCyclePopupView;
  PopupWindow deleteCyclePopupWindow;
  Button confirm_delete;
  Button cancel_delete;
  TextView delete_text;

  CyclesDatabase cyclesDatabase;
  List<Cycles> cyclesList;
  List<CyclesBO> cyclesBOList;
  List<PomCycles> pomCyclesList;
  Cycles cycles;
  CyclesBO cyclesBO;
  PomCycles pomCycles;
  int passedID;
  boolean isNewCycle;
  SavedCycleAdapter savedCycleAdapter;
  ArrayList<Integer> infinityArrayOne;
  ArrayList<Integer> infinityArrayTwo;
  ArrayList<Integer> infinityArrayThree;

  Runnable toggleNextRoundRunnable;
  boolean nextRoundToggleIsActive;

  @Override
  public void onBackPressed() {
    exitTimer();
//    moveTaskToBack(true);
  }

  @Override
  public boolean onCreateOptionsMenu(final Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.timer_options_menu, menu);
    return true;
  }

  @SuppressLint("NonConstantResourceId")
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      //Launches popup to confirm/cancel cycle deletion.
      case R.id.delete_single_cycle:
        delete_text.setText(R.string.confirm_single_delete);
        deleteCyclePopupWindow.showAtLocation(timerInterface, Gravity.CENTER_HORIZONTAL, 0, -100);
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void sendAlphaValue(int alpha) {
    receivedAlpha = alpha;
  }

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.timer_interface);

    //Object to access our layout.
    timerInterface = new ConstraintLayout(this);
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
    pomValuesTime = new ArrayList<>();
    infinityArrayOne = new ArrayList<>();
    infinityArrayTwo = new ArrayList<>();
    infinityArrayThree = new ArrayList<>();

    reset = findViewById(R.id.reset);
    cycle_header_text = findViewById(R.id.cycle_header_text);

    cycles_completed = findViewById(R.id.cycles_completed);
    next_round = findViewById(R.id.next_round);
    new_lap = findViewById(R.id.new_lap);
    total_set_header = findViewById(R.id.total_set_header);
    total_break_header = findViewById(R.id.total_break_header);
    total_set_time = findViewById(R.id.total_set_time);
    total_break_time = findViewById(R.id.total_break_time);
    total_set_header.setText(R.string.total_sets);
    total_break_header.setText(R.string.total_breaks);
    total_set_time.setText("0");
    total_break_time.setText("0");

    progressBar = findViewById(R.id.progressBar);
    stopWatchView = findViewById(R.id.stopWatchView);
    timeLeft = findViewById(R.id.timeLeft);
    timePaused = findViewById(R.id.timePaused);
    msTime = findViewById(R.id.msTime);
    msTimePaused = findViewById(R.id.msTimePaused);
    dotDraws = findViewById(R.id.dotdraws);
    lapRecycler = findViewById(R.id.lap_recycler);
    overtime = findViewById(R.id.overtime);
    pauseResumeButton = findViewById(R.id.pauseResumeButton);
    pauseResumeButton.setBackgroundColor(Color.argb(0, 0, 0, 0));
    pauseResumeButton.setRippleColor(null);
    exit_timer = findViewById(R.id.exit_timer);
    reset_total_times = findViewById(R.id.reset_total_times);

    stopWatchView.setVisibility(View.GONE);
    lapRecycler.setVisibility(View.GONE);
    overtime.setVisibility(View.INVISIBLE);
    new_lap.setVisibility(View.INVISIBLE);

    cycles_completed.setText(R.string.cycles_done);
    cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));
    lastTextView = timePaused;

    savedCycleAdapter = new SavedCycleAdapter();
    LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    deleteCyclePopupView = inflater.inflate(R.layout.delete_cycles_popup, null);
    deleteCyclePopupWindow = new PopupWindow(deleteCyclePopupView, 750, 375, true);
    deleteCyclePopupWindow.setAnimationStyle(R.style.WindowAnimation);
    delete_text = deleteCyclePopupView.findViewById(R.id.delete_text);
    confirm_delete = deleteCyclePopupView.findViewById(R.id.confirm_yes);
    cancel_delete = deleteCyclePopupView.findViewById(R.id.confirm_no);

    fadeIn = new AlphaAnimation(0.0f, 1.0f);
    fadeOut = new AlphaAnimation(1.0f, 0.0f);
    fadeIn.setDuration(1000);
    fadeOut.setDuration(1000);
    fadeIn.setFillAfter(true);
    fadeOut.setFillAfter(true);

    sharedPreferences = getApplicationContext().getSharedPreferences("pref", 0);
    prefEdit = sharedPreferences.edit();

    //Receives lists passed in from Main.
    Intent intent = getIntent();
    if (intent != null) {
      isNewCycle = intent.getBooleanExtra("isNewCycle", false);
      mode = intent.getIntExtra("mode", 0);
      cycle_title = intent.getStringExtra("cycleTitle");
      //The primary key ID of our current cycle row.
      passedID = intent.getIntExtra("passedID", 0);
      //Infinity toggle lists passed in from Main, for the purpose of passing back since they are not saved.
      infinityArrayOne = intent.getIntegerArrayListExtra("infiniteOne");
      infinityArrayTwo = intent.getIntegerArrayListExtra("infiniteTwo");
      infinityArrayThree = intent.getIntegerArrayListExtra("infiniteThree");
    }
    cycle_header_text.setText(cycle_title);

    //Retrieves our list of Timer values for each round in the cycle.
    ConstraintLayout.LayoutParams resetParam = (ConstraintLayout.LayoutParams) reset_total_times.getLayoutParams();
    switch (mode) {
      case 1:
        customSetTime = intent.getIntegerArrayListExtra("setList");
        customBreakTime = intent.getIntegerArrayListExtra("breakList");
        setsAreCountingUp = intent.getBooleanExtra("setsAreCountingUp", false);
        breaksAreCountingUp = intent.getBooleanExtra("breaksAreCountingUp", false);
        //Setting count up or count down modes.
        dotDraws.countingUpSets(setsAreCountingUp);
        dotDraws.countingUpBreaks(breaksAreCountingUp);
        //Since we begin on Sets, we are getting the millis value of our first round.
        setMillis = customSetTime.get(0);
        //Populates an array of zeros for use in "count up" mode. Will always sync in size w/ its counterpart.
        for (int i = 0; i < customSetTime.size(); i++) {
          zeroArraySets.add(0); zeroArrayBreaks.add(0);
        }
        break;
      case 2:
        breaksOnlyTime = intent.getIntegerArrayListExtra("breakOnlyList");
        breaksOnlyAreCountingUp = intent.getBooleanExtra("breaksOnlyAreCountingUp", false);
        dotDraws.countingUpBreaksOnly(breaksOnlyAreCountingUp);
        breaksOnlyMillis = breaksOnlyTime.get(0);
        //Populates an array of zeros for use in "count up" mode. Will always sync in size w/ its counterpart.
        for (int i = 0; i < breaksOnlyTime.size(); i++) zeroArrayBreaks.add(0);
        //Removes set counter and repositions reset button.
        total_set_time.setVisibility(View.GONE);
        total_set_header.setVisibility(View.GONE);
        resetParam.rightMargin = 60;
        break;
      case 3:
        pomValuesTime = intent.getIntegerArrayListExtra("pomList");
        //Replaces "total set time" w/ "work time".
        total_set_header.setText(R.string.total_work);
        break;
    }

    /////---------Testing pom round iterations---------------/////////
//    if (mode==3) for (int i=1; i<9; i++) if (i%2!=0) pomValuesTime.set(i-1, 4000); else pomValuesTime.set(i-1, 70000);

    toggleNextRoundRunnable = () -> {
      if (nextRoundToggleIsActive) {
        nextRoundToggleIsActive = false;
        next_round.setEnabled(true);
      }
    };

    //Loads database of saved cycles. Since we are on a specific cycle, we can access it via its unique ID here.
    AsyncTask.execute(() -> {
      cyclesDatabase = CyclesDatabase.getDatabase(getApplicationContext());
      //If not a new cycle, retrieve cycle based on its ID and get its total times + cycles completed.
      if (!isNewCycle) {
        switch (mode) {
          case 1:
            cycles = cyclesDatabase.cyclesDao().loadSingleCycle(passedID).get(0);
            totalSetMillis = cycles.getTotalSetTime() * 1000;
            totalBreakMillis = cycles.getTotalBreakTime() * 1000;
            customCyclesDone = cycles.getCyclesCompleted();
            total_set_time.setText(convertSeconds(totalSetMillis / 1000));
            total_break_time.setText(convertSeconds(totalBreakMillis / 1000));
            break;
          case 2:
            cyclesBO = cyclesDatabase.cyclesDao().loadSingleCycleBO(passedID).get(0);
            totalBreakMillis = cyclesBO.getTotalBOTime() * 1000;
            breaksOnlyCyclesDone = cyclesBO.getCyclesCompleted();
            total_break_time.setText(convertSeconds(totalBreakMillis / 1000));
            break;
          case 3:
            pomCycles = cyclesDatabase.cyclesDao().loadSinglePomCycle(passedID).get(0);
            break;
        }
      } else {
        //If a new cycle, retrieve the most recently added db entry (the one we just created in Main), pull its ID, and assign an instance of the db entity class to it so we can save total set/break time and total cycles to it when we exit.
        int id = 0;
        switch (mode) {
          case 1:
            cyclesList = cyclesDatabase.cyclesDao().loadCyclesMostRecent();
            id = cyclesList.get(0).getId();
            cycles = cyclesDatabase.cyclesDao().loadSingleCycle(id).get(0);
            break;
          case 2:
            cyclesBOList = cyclesDatabase.cyclesDao().loadCyclesMostRecentBO();
            id = cyclesBOList.get(0).getId();
            cyclesBO = cyclesDatabase.cyclesDao().loadSingleCycleBO(id).get(0);
            break;
          case 3:
            pomCyclesList = cyclesDatabase.cyclesDao().loadPomCyclesMostRecent();
            id = pomCyclesList.get(0).getId();
            pomCycles = cyclesDatabase.cyclesDao().loadSinglePomCycle(id).get(0);
            break;
        }
      }
    });
    //Draws dot display depending on which more we're on.
    dotDraws.setMode(mode);
    //Implements callback for end-of-round alpha fade on dots.
    dotDraws.onAlphaSend(TimerInterface.this);

    //Sets number of rounds to size of round list.
    numberOfSets = customSetTime.size();
    numberOfBreaks = customBreakTime.size();
    numberOfBreaksOnly = breaksOnlyTime.size();

    //Animation that plays when round completes. Creating one for initial use.
    animateEnding(false);

    //Recycler view for our stopwatch laps.
    lapLayout = new LinearLayoutManager(getApplicationContext());
    lapAdapter = new LapAdapter(getApplicationContext(), currentLapList, savedLapList);
    lapRecycler.setAdapter(lapAdapter);
    lapRecycler.setLayoutManager(lapLayout);

    //Sets all progress bars to their start value.
    progressBar.setProgress(maxProgress);

    //Populates UI elements at app start.
    populateTimerUI();

    //Used in all timers to smooth out end fade. Keeping received
    endFade = new Runnable() {
      @Override
      public void run() {
        drawDots(fadeVar);
        if (receivedAlpha <= 90) {
          mHandler.removeCallbacks(this);
        } else mHandler.postDelayed(this, 50);
      }
    };

    //These three runnables act as our timers for "count up" rounds.
    secondsUpSetRunnable = new Runnable() {
      @Override
      public void run() {
        //Animates text size change when timer gets to 60 seconds.
        animateTextSize(countUpMillisSets);
        countUpMillisSets += 50;
        //Sets the current index of our initial "zero array" to the millis value being counted up.
        zeroArraySets.set((zeroArraySets.size() - numberOfSets), countUpMillisSets);
        timeLeft.setText(convertSeconds((countUpMillisSets) / 1000));
        timePaused.setText(convertSeconds((countUpMillisSets) / 1000));
        drawDots(1);
        //Temporary value for current round, using totalSetMillis which is our permanent value.
        tempSetMillis = totalSetMillis + countUpMillisSets;
        total_set_time.setText(convertSeconds(tempSetMillis / 1000));
        mHandler.postDelayed(this, 50);
      }
    };

    secondsUpBreakRunnable = new Runnable() {
      @Override
      public void run() {
        animateTextSize(countUpMillisBreaks);
        countUpMillisBreaks += 50;
        zeroArrayBreaks.set((zeroArrayBreaks.size() - numberOfBreaks), countUpMillisBreaks);
        timeLeft.setText(convertSeconds((countUpMillisBreaks) / 1000));
        timePaused.setText(convertSeconds((countUpMillisBreaks) / 1000));
        drawDots(2);
        //Temporary value for current round, using totalBreakMillis which is our permanent value.
        tempBreakMillis = totalBreakMillis + countUpMillisBreaks;
        total_break_time.setText(convertSeconds(tempBreakMillis / 1000));
        mHandler.postDelayed(this, 50);
      }
    };

    secondsUpBORunnable = new Runnable() {
      @Override
      public void run() {
        animateTextSize(countUpMillisBO);
        countUpMillisBO += 50;
        zeroArrayBreaks.set((zeroArrayBreaks.size() - numberOfBreaks), countUpMillisBreaks);
        timeLeft.setText(convertSeconds((countUpMillisBO) / 1000));
        timePaused.setText(convertSeconds((countUpMillisBO) / 1000));
        drawDots(3);
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

    //Disables button for 1 second after push. Re-enables it through runnable after that.
    next_round.setOnClickListener(v -> {
      nextRound();
      if (!nextRoundToggleIsActive) {
        next_round.setEnabled(false);
        nextRoundToggleIsActive = true;
        mHandler.postDelayed(toggleNextRoundRunnable, 1000);
      }
    });

    new_lap.setOnClickListener(v -> {
      newLap();
    });

    pauseResumeButton.setOnClickListener(v -> {
      switch (mode) {
        case 1:
          if (!customHalted) pauseAndResumeTimer(PAUSING_TIMER);
          else pauseAndResumeTimer(RESUMING_TIMER);
          break;
        case 2:
          //If counting up, use the same pause/resume feature as other modes. If not, iterate through the four pause cycles.
          if (!breaksOnlyHalted) pauseAndResumeTimer(PAUSING_TIMER);
          else if (!breaksOnlyAreCountingUp) {
            switch (movingBOCycle) {
              case 0:
                pauseAndResumeTimer(RESUMING_TIMER);
                break;
              case 1:
                pauseAndResumeTimer(RESETTING_TIMER);
                movingBOCycle++;
                break;
              case 2:
                pauseAndResumeTimer(RESTARTING_TIMER);
                movingBOCycle = 0;
                break;
            }
          } else pauseAndResumeTimer(RESUMING_TIMER);
          break;
        case 3:
          if (!pomHalted) pauseAndResumeTimer(PAUSING_TIMER);
          else pauseAndResumeTimer(RESUMING_TIMER);
          break;
        case 4:
          if (stopwatchHalted) pauseAndResumeTimer(RESUMING_TIMER);
          else pauseAndResumeTimer(PAUSING_TIMER);
          break;
      }
    });

    exit_timer.setOnClickListener(v -> {
      AsyncTask.execute(()->{
        exitTimer();
      });
    });

    confirm_delete.setOnClickListener(v -> {
      //If popUp text contains delete_total_times or delete_total_laps String, execute that part of the function. Otherwise, we're deleting the current cycle.
      AsyncTask.execute(() -> {
        if (delete_text.getText().equals(getString(R.string.delete_total_times))) deleteCycle(DELETING_TIMES);
        else deleteCycle(DELETING_CYCLE);
      });
    });

    cancel_delete.setOnClickListener(v -> {
      //Removes our delete confirm popUp if we cancel.
      if (deleteCyclePopupWindow.isShowing()) deleteCyclePopupWindow.dismiss();
    });

    reset_total_times.setOnClickListener(v -> {
      deleteTotalTimes();
    });
  }

  public void startObjectAnimator() {
    //Ensures each new dot fade begins @ full alpha.
    dotDraws.setAlpha();
    //Always set to false before new round begins, so fade smooth resets. May cause super rare overlap in fade out dot alphas, but this can be solved by just using separate ascent variables.
    switch (mode) {
      case 1:
        if (!onBreak) {
          if (!setBegun) {
            //Used for pause/resume and fading text in (i.e. timeLeft or timePaused).
            customHalted = false;
            //Returns and sets our setMillis value to the first position in our Array.
            if (numberOfSets > 0) setMillis = newMillis(true);
            objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", (int) maxProgress, 0);
            objectAnimator.setInterpolator(new LinearInterpolator());
            objectAnimator.setDuration(setMillis);
            objectAnimator.start();
            setBegun = true;
            modeOneTimerEnded = false;
            //Unchanging start point of setMillis used to count total set time over multiple rounds.
            permSetMillis = setMillis;
          } else {
            setMillis = setMillisUntilFinished;
            if (objectAnimator != null) objectAnimator.resume();
          }
        } else {
          if (!breakBegun) {
            //Used for pause/resume and fading text in (i.e. timeLeft or timePaused).
            customHalted = false;
            //Returns and sets our breakMillis value to the first position in our Array.
            if (numberOfBreaks > 0) breakMillis = newMillis(false);
            objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", (int) maxProgress, 0);
            objectAnimator.setInterpolator(new LinearInterpolator());
            objectAnimator.setDuration(breakMillis);
            objectAnimator.start();
            breakBegun = true;
            //Unchanging start point of breakMillis used to count total set time over multiple rounds.
            permBreakMillis = breakMillis;
          } else {
            breakMillis = breakMillisUntilFinished;
            if (objectAnimator != null) objectAnimator.resume();
          }
        }
        break;
      case 2:
        if (!breakOnlyBegun) {
          //Ensures any features meant for running timer cannot be executed here.
          breaksOnlyHalted = false;
          //Returns and sets our breaksOnlyMillis value to the first position in our Array.
          if (breaksOnlyTime.size() > 0) breaksOnlyMillis = newMillis(true);
          objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", (int) breaksOnlyProgressPause, 0);
          objectAnimator.setInterpolator(new LinearInterpolator());
          objectAnimator.setDuration(breaksOnlyMillis);
          objectAnimator.start();
          breakOnlyBegun = true;
          modeTwoTimerEnded = false;
          //Unchanging start point of breaksOnlyMillis used to count total set time over multiple rounds.
          permBreakMillis = breaksOnlyMillis;
        } else {
          breaksOnlyMillis = breaksOnlyMillisUntilFinished;
          if (objectAnimator != null) objectAnimator.resume();
        }
        break;
      case 3:
        if (!pomBegun) {
          //Ensures any features meant for running timer cannot be executed here.
          pomHalted = false;
          pomMillis = newMillis(false);
          objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", (int) pomProgressPause, 0);
          objectAnimator.setInterpolator(new LinearInterpolator());
          objectAnimator.setDuration(pomMillis);
          objectAnimator.start();
          pomBegun = true;
          activePomCycle = true;
          modeThreeTimerEnded = false;
          //Unchanging start point of pomMillis used to count total set time over multiple rounds. Using SET and BREAK vars since modes are exclusive, and it saves on variable creation.
          switch (pomDotCounter) {
            case 1: case 3: case 5: case 7:
              permSetMillis = pomMillis;
              break;
            case 2: case 4: case 6: case 8:
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
          changeTextSize(valueAnimatorUp, timeLeft, timePaused);
          textSizeIncreased = false;
        }
        //Displays Long->String conversion of time left every tick.
        timeLeft.setText(convertSeconds((setMillis + 999) / 1000));
        timePaused.setText(convertSeconds((setMillis + 999) / 1000));
        if (setMillis < 500) timerDisabled = true;

        //Sets value to difference between starting millis and current millis (e.g. 45000 left from 50000 start is 5000/5 sec elapsed).
        setMillisHolder = permSetMillis - setMillis;
        //Temporary value for current round, using totalSetMillis which is our permanent value.
        tempSetMillis = totalSetMillis + setMillisHolder;
        total_set_time.setText(convertSeconds(tempSetMillis / 1000));
        drawDots(1);
      }

      @Override
      public void onFinish() {
        //Used for pause/resume and fading text in (i.e. timeLeft or timePaused). We set it here so that when we switch tabs, the correct textView is shown.
        customHalted = true;
        //Used in startObjectAnimator, and dictates if we are on a set or break.
        onBreak = true;
        //Used in startObjectAnimator to determine whether we're using a new animator + millis, or resuming one from a pause.
        setBegun = false;
        //Creates a new alpha animation, and sets this timer's progress bar/text on it.
        animateEnding(true);
        //Smooths out end fade.
        fadeVar = 1;
        mHandler.post(endFade);
        timeLeft.setText("0");
        customProgressPause = maxProgress;

        //Adds current round elapsed millis to saved total.
        totalSetMillis = totalSetMillis + setMillisHolder;
        //Rounds our total millis up to the nearest 1000th and divides to whole int to ensure synchronicity w/ display (e.g. (4950 + 100) / 1000 == 5).
        tempSetMillis = ((totalSetMillis + 100) / 1000) * 1000;
        total_set_time.setText(convertSeconds(tempSetMillis/1000));
        //Fade out effect at end of round, fading in after runnable posts on delay.
        timeLeft.startAnimation(fadeOut);

        mHandler.postDelayed(() -> {
          //Changing fadeVar to one used by BREAKS as soon as new set round is started after runnable delay. This is to keep fading in sync.
          fadeVar = 2;
          timeLeft.startAnimation(fadeIn);
          //Re-enabling timer clicks.
          timerDisabled = false;
          //Removing the last used set at end of post-delayed runnable to allow time for its dot to fade out via endFade runnable above.
          numberOfSets--;
          if (numberOfSets > 0) setMillis = newMillis(true);
          endAnimation.cancel();
          dotDraws.setAlpha();
          if (!breaksAreCountingUp) {
            startObjectAnimator();
            startBreakTimer();
          } else {
            progressBar.setProgress(10000);
            mHandler.post(secondsUpBreakRunnable);
          }
        }, 750);
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
            changeTextSize(valueAnimatorUp, timeLeft, timePaused);
            textSizeIncreased = false;
          }

          timeLeft.setText(convertSeconds((millisUntilFinished + 999) / 1000));
          timePaused.setText(convertSeconds((millisUntilFinished + 999) / 1000));
          drawDots(2);
          if (breakMillis < 500) timerDisabled = true;

          //For "Total Break" times.
          breakMillisHolder = permBreakMillis - breakMillis;
          tempBreakMillis = totalBreakMillis + breakMillisHolder;
          total_break_time.setText(convertSeconds(tempBreakMillis / 1000));
        }

        @Override
        public void onFinish() {
          //Used for pause/resume and fading text in (i.e. timeLeft or timePaused). We set it here so that when we switch tabs, the correct textView is shown.
          customHalted = true;
          //Used in startObjectAnimator, and dictates if we are on a set or break.
          onBreak = false;
          //Used in startObjectAnimator to determine whether we're using a new animator + millis, or resuming one from a pause.
          breakBegun = false;
          timeLeft.setText("0");

          //Sets end animation.
          animateEnding(true);

          if (numberOfBreaks > 0) {
            customProgressPause = maxProgress;
            //Smooths out end fade.
            fadeVar = 2;
            mHandler.post(endFade);

            //Adds current round elapsed millis to saved total.
            totalBreakMillis = totalBreakMillis + breakMillisHolder;
            //Rounds our total millis up to the nearest 1000th and divides to whole int to ensure synchronicity w/ display (e.g. (4950 + 100) / 1000 == 5).
            tempBreakMillis = ((totalBreakMillis + 100) / 1000) * 1000;
            total_break_time.setText(convertSeconds(tempBreakMillis/1000));
            //Fade out effect at end of round, fading in after runnable posts on delay.
            timeLeft.startAnimation(fadeOut);

            mHandler.postDelayed(() -> {
              //Changing fadeVar to one used by SETS as soon as new set round is started after runnable delay. This is to keep fading in sync.
              fadeVar = 1;
              timeLeft.startAnimation(fadeIn);
              //Removing the last used set at end of post-delayed runnable to allow time for its dot to fade out via endFade runnable above.
              //Must execute here for conditional below to work.
              numberOfBreaks--;
              if (numberOfBreaks > 0)
                breakMillis = newMillis(false);
              //Re-enabling timer clicks. Used regardless of numberOfBreaks.
              timerDisabled = false;

              //If numberOfBreaks has not been reduced to 0 within this runnable, begin our next set. Otherwise, do end cycle stuff.
              if (numberOfBreaks != 0) {
                if (!setsAreCountingUp) {
                  startObjectAnimator();
                  startSetTimer();
                } else {
                  progressBar.setProgress(10000);
                  mHandler.post(secondsUpSetRunnable);
                }
                endAnimation.cancel();
              } else {
                //Used to call resetTimer() in pause/resume method. Separate than our disable method.
                modeOneTimerEnded = true;
                customCyclesDone++;
                drawDots(0);
                cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));
              }
            }, 750);
          }
        }
      }.start();
    } else if (mode == 2) {
      setTextSize(breaksOnlyMillis);

      timer = new CountDownTimer(breaksOnlyMillis, 50) {
        @Override
        public void onTick(long millisUntilFinished) {
          breaksOnlyProgressPause = (int) objectAnimator.getAnimatedValue();
          breaksOnlyMillis = millisUntilFinished;

          if (textSizeIncreased) if (breaksOnlyMillis < 59000) {
            changeTextSize(valueAnimatorUp, timeLeft, timePaused);
            textSizeIncreased = false;
          }
          timeLeft.setText(convertSeconds((millisUntilFinished + 999) / 1000));
          timePaused.setText(convertSeconds((millisUntilFinished + 999) / 1000));
          drawDots(3);
          if (breaksOnlyMillis < 500) boTimerDisabled = true;

          breakMillisHolder = permBreakMillis - breaksOnlyMillis;
          tempBreakMillis = totalBreakMillis + breakMillisHolder;
          total_break_time.setText(convertSeconds(tempBreakMillis / 1000));
        }

        @Override
        public void onFinish() {
          //Ensures any features meant for a running timer cannot be executed here.
          breaksOnlyHalted = true;
          breakOnlyBegun = false;
          timeLeft.setText("0");
          animateEnding(true);

          if (numberOfBreaksOnly > 0) {
            breaksOnlyProgressPause = maxProgress;
            //Smooths out end fade.
            if (mode == 2) {
              fadeVar = 3;
              mHandler.post(endFade);
            }
            //Activates RESETTING_TIMER in pauseAndResume. Var gets set to 2 on next click in pauseAndResume, and then resets to 0.
            movingBOCycle = 1;

            totalBreakMillis = totalBreakMillis + breakMillisHolder;
            tempBreakMillis = ((totalBreakMillis + 100) / 1000) * 1000;
            total_break_time.setText(convertSeconds(totalBreakMillis/1000));
            //Fade out effect at end of round, fading in after runnable posts on delay.
            timeLeft.startAnimation(fadeOut);

            mHandler.postDelayed(() -> {
              timeLeft.startAnimation(fadeIn);
              //Removing the last used set at end of post-delayed runnable to allow time for its dot to fade out via endFade runnable above.
              //Must execute here for conditional below to work.
              numberOfBreaksOnly--;
              if (numberOfBreaksOnly > 0) breaksOnlyMillis = newMillis(false);
              boTimerDisabled = false;
              //If numberOfBreaksOnly has been reduced to 0 in this runnable, do end cycle stuff. Since we are pausing between breaks in this mode anyway, we are doing less than the set/break combos.
              if (numberOfBreaksOnly == 0) {
                //Used to call resetTimer() in pause/resume method. Separate than our disable method.
                modeTwoTimerEnded = true;
                breaksOnlyCyclesDone++;
                cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(breaksOnlyCyclesDone)));
                drawDots(0);
              }
            }, 750);
          }

          overtime.setVisibility(View.VISIBLE);
          isOvertimeRunning = true;
          ot = new Runnable() {
            @Override
            public void run() {
              overSeconds += 1;
              overtime.setText(getString(R.string.overtime, String.valueOf(overSeconds)));
              mHandler.postDelayed(this, 1000);
            }
          };
          mHandler.post(ot);
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
          changeTextSize(valueAnimatorUp, timeLeft, timePaused);
          textSizeIncreased = false;
        }

        timeLeft.setText(convertSeconds((pomMillis + 999) / 1000));
        timePaused.setText(convertSeconds((pomMillis + 999) / 1000));
        if (pomMillis<500) pomTimerDisabled = true;

        //Switches total time count depending on which round we're on.
        switch (pomDotCounter) {
          case 1: case 3: case 5: case 7:
            setMillisHolder = permSetMillis - pomMillis;
            tempSetMillis = totalSetMillis + setMillisHolder;
            total_set_time.setText(convertSeconds(tempSetMillis / 1000));
            break;
          case 2: case 4: case 6: case 8:
            breakMillisHolder = permBreakMillis - pomMillis;
            tempBreakMillis = totalBreakMillis + breakMillisHolder;
            total_break_time.setText(convertSeconds(tempBreakMillis / 1000));
            break;
        }
        drawDots(4);
      }

      @Override
      public void onFinish() {
        //Ensures any features meant for a running timer cannot be executed here.
        pomHalted = true;
        pomBegun = false;
        timeLeft.setText("0");
        pomProgressPause = maxProgress;
        animateEnding(true);

        if (pomDotCounter < 9) {
          //Ensures any features meant for a running timer cannot be executed here.
          customHalted = true;
          //Disabling pause/resume clicks until animation finishes.
          pomTimerDisabled = true;
          //Smooths out end fade.
          fadeVar = 4;
          mHandler.post(endFade);

          switch (pomDotCounter) {
            case 1: case 3: case 5: case 7:
              totalSetMillis = totalSetMillis + setMillisHolder;
              tempSetMillis = ((totalSetMillis + 100) / 1000) * 1000;
              total_set_time.setText(convertSeconds(tempSetMillis/1000));
              break;
            case 2: case 4: case 6: case 8:
              totalBreakMillis = totalBreakMillis + breakMillisHolder;
              tempBreakMillis = ((totalBreakMillis + 100) / 1000) * 1000;
              total_break_time.setText(convertSeconds(tempBreakMillis/1000));
              break;
          }
          //Fade out effect at end of round, fading in after runnable posts on delay.
          timeLeft.startAnimation(fadeOut);

          mHandler.postDelayed(() -> {
            timeLeft.startAnimation(fadeIn);
            //Counter must increase here for conditional below to work.
            pomDotCounter++;
            pomMillis = newMillis(false);
            //Re-enabling timer clicks. Used regardless of number of rounds left.
            pomTimerDisabled = false;

            if (pomDotCounter < 9) {
              endAnimation.cancel();
              startObjectAnimator();
              startPomTimer();
            } else {
              modeThreeTimerEnded = true;
              pomCyclesDone += 1;
              drawDots(0);
              cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(pomCyclesDone)));
            }
          }, 750);
        }
      }
    }.start();
  }

  //Set to true if we want to run the animation instantly. False if it is timer dependant, since we do not want it triggering on the wrong prog/timer.
  private void animateEnding(boolean setAnimation) {
    endAnimation = new AlphaAnimation(1.0f, 0.0f);
    endAnimation.setDuration(300);
    endAnimation.setStartOffset(20);
    endAnimation.setRepeatMode(Animation.REVERSE);
    endAnimation.setRepeatCount(Animation.INFINITE);

    if (setAnimation) {
      progressBar.startAnimation(endAnimation);
      timeLeft.startAnimation(endAnimation);
      timePaused.startAnimation(endAnimation);
    }
  }

  public void changeTextSize(ValueAnimator va, TextView textView, TextView textViewPaused) {
    sizeAnimator = va;
    sizeAnimator.addUpdateListener(animation -> {
      float sizeChange = (float) va.getAnimatedValue();
      textView.setTextSize(sizeChange);
      textViewPaused.setTextSize(sizeChange);
    });
    sizeAnimator.setRepeatCount(0);
    sizeAnimator.start();
  }

  //Sets text size at round start. textSizeIncreased is set to true if timer is >=60 seconds, so the text size can be reduced mid-timer as it drops below.
  public void setTextSize(long millis) {
    if (millis>=59000) {
      timePaused.setTextSize(70f);
      timeLeft.setTextSize(70f);
      textSizeIncreased = true;
    } else {
      timePaused.setTextSize(90f);
      timeLeft.setTextSize(90f);
    }
  }

  //Used in count up mode to animate text size changes in our runnables.
  public void animateTextSize(long millis) {
    if (textSizeReduced) {
      if (millis >=60000) {
        changeTextSize(valueAnimatorDown, timeLeft, timePaused);
        textSizeReduced = false;
      }
    }
  }

  //Sets millis values based on which round we are on (list size minus number of non-completed rounds).
  //REMEMBER, this is not a void method and we need to do something with its return. If we do not, whatever method we are currently in will RETURN itself.
  public long newMillis(boolean onSets) {
    switch (mode) {
      case 1:
        if (onSets) return customSetTime.get((int) (customSetTime.size() - numberOfSets));
        else return customBreakTime.get((int) (customBreakTime.size() - numberOfBreaks));
      case 2:
        return breaksOnlyTime.get((int) (breaksOnlyTime.size() - numberOfBreaksOnly));
      case 3:
        return pomValuesTime.get(pomDotCounter - 1);
      default:
        return 0;
    }
  }

  //Ends the current round and moves onto the next one.
  public void nextRound() {
    //Animates timer and progressBar.
    animateEnding(true);
    //Stores cumulative time valuation.
    saveTotalTimes();
    switch (mode) {
      case 1:
        //Since breaks will always be the last round in modes 1 and 2, return without executing anything if there are none left.
        if (numberOfBreaks == 0) return;
        timerDisabled = false;
        if (!onBreak) {
          fadeVar = 1;
          mHandler.post(endFade);
          //onFinish tasks for counting up sets.
          if (!setsAreCountingUp) {
            //Ensures objectAnimator sets millis value to NEW round value.
            setBegun = false;
            //Cancelling timer and animator.
            if (timer != null) timer.cancel();
            if (objectAnimator != null) objectAnimator.cancel();
            //Resetting progressBar values.
            customProgressPause = maxProgress;
//            progressBar.setProgress(10000);
            //onFinish for counting down sets.
          } else {
            timeLeft.setText(convertSeconds((countUpMillisSets) / 1000));
            timePaused.setText(convertSeconds((countUpMillisSets) / 1000));
            mHandler.removeCallbacks(secondsUpSetRunnable);
            //For pause/resume.
            customHalted = false;
          }
          mHandler.postDelayed(() -> {
            //Changing fadeVar to one used by BREAKS as soon as new set round is started after runnable delay. This is to keep fading in sync.
            fadeVar = 2;
            //Iterating down on set numbers.
            setMillis = newMillis(true);
            numberOfSets--;
            endAnimation.cancel();
            onBreak = true;
            mHandler.removeCallbacks(secondsUpSetRunnable);
            countUpMillisSets = 0;

            //If timer is paused and we skip to the next round, begin that round paused. We do this by simply setting the timer textViews to the next type of round (i.e. breaks), and resetting the progressBar.
            if (customHalted) {
              progressBar.setProgress(10000);
              timePaused.setAlpha(1.0f);
              if (!breaksAreCountingUp) {
                timeLeft.setText(convertSeconds((breakMillis) / 1000));
                timePaused.setText(convertSeconds((breakMillis / 1000)));
              } else {
                timeLeft.setText("0");
                timePaused.setText("0");
              }
            } else {
              if (!breaksAreCountingUp) {
                startObjectAnimator();
                startBreakTimer();
              } else {
                mHandler.post(secondsUpBreakRunnable);
              }
            }
          }, 1000);
        } else {
          fadeVar = 2;
          if (!breaksAreCountingUp) {
            mHandler.post(endFade);
            //Ensures objectAnimator sets millis value to NEW round value.
            breakBegun = false;
            //Cancelling timer and animator.
            if (timer != null) timer.cancel();
            if (objectAnimator != null) objectAnimator.cancel();
            //Resetting progressBar values.
            customProgressPause = maxProgress;
          } else {
            timeLeft.setText(convertSeconds((countUpMillisBreaks) / 1000));
            timePaused.setText(convertSeconds((countUpMillisBreaks) / 1000));
            mHandler.removeCallbacks(secondsUpBreakRunnable);
            //For pause/resume and tab switches.
            customHalted = false;
          }
          mHandler.postDelayed(() -> {
            //Changing fadeVar to one used by BREAKS as soon as new set round is started after runnable delay. This is to keep fading in sync.
            fadeVar = 1;
            //Since we are switching back to sets, onBreak is now false.
            onBreak = false;

            if (numberOfBreaks > 0) {
              countUpMillisBreaks = 0;
              endAnimation.cancel();
              breakMillis = newMillis(false);
              numberOfBreaks--;

              //If timer is paused and we skip to the next round, begin that round paused. We do this by simply setting the timer textViews to the next type of round (i.e. sets), and resetting the progressBar.
              if (customHalted) {
                progressBar.setProgress(10000);
                timePaused.setAlpha(1.0f);
                if (!setsAreCountingUp) {
                  timeLeft.setText(convertSeconds((setMillis) / 1000));
                  timePaused.setText(convertSeconds((setMillis / 1000)));
                } else {
                  timeLeft.setText("0");
                  timePaused.setText("0");
                }
              } else {
                if (!setsAreCountingUp) {
                  startObjectAnimator();
                  startSetTimer();
                } else {
                  mHandler.post(secondsUpSetRunnable);
                }
              }
            } else {
              modeOneTimerEnded = true;
              customCyclesDone++;
              cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));
            }
          }, 1000);
        }
        break;
      case 2:
        //Since breaks will always be the last round in modes 1 and 2, return without executing anything if there are none left.
        if (numberOfBreaksOnly == 0) return;

        fadeVar = 3;
        mHandler.removeCallbacks(secondsUpBORunnable);
//      breaksOnlyHalted = false;
        breaksOnlyMillis = newMillis(false);
        numberOfBreaksOnly--;
        boTimerDisabled = false;

        mHandler.postDelayed(() -> {
          //We always want the next round in breaksOnly to start paused, so we do not execute any timers or animators here.
          if (numberOfBreaksOnly > 0) {
            endAnimation.cancel();
            countUpMillisBO = 0;
            if (breaksOnlyHalted) {
              progressBar.setProgress(10000);
              timePaused.setAlpha(1.0f);
              if (!breaksOnlyAreCountingUp) {
                timeLeft.setText(convertSeconds((breaksOnlyMillis) / 1000));
                timePaused.setText(convertSeconds((breaksOnlyMillis / 1000)));
              } else {
                timeLeft.setText("0");
                timePaused.setText("0");
              }
            }
          } else {
            modeTwoTimerEnded = true;
            breaksOnlyCyclesDone++;
            cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(breaksOnlyCyclesDone)));
          }
        }, 1000);
        break;
      case 3:
        fadeVar = 4;
        mHandler.post(endFade);
        if (timer != null) timer.cancel();
        if (objectAnimator != null) objectAnimator.cancel();
        pomProgressPause = maxProgress;
//        //Ensures objectAnimator sets millis value to NEW round value.
        pomBegun = false;

        mHandler.postDelayed(() -> {
          pomDotCounter++;
          pomMillis = newMillis(false);
          endAnimation.cancel();
          pomTimerDisabled = false;

          if (pomHalted) {
            timeLeft.setText(convertSeconds((pomMillis) / 1000));
            timePaused.setText(convertSeconds((pomMillis / 1000)));
          } else {
            startObjectAnimator();
            startPomTimer();
          }
        },1000);
        break;
    }
  }

  public void drawDots(int fadeVar) {
    switch (mode) {
      case 1:
        if (!setsAreCountingUp) dotDraws.setTime(customSetTime);
        else dotDraws.setTime(zeroArraySets);
        if (!breaksAreCountingUp) dotDraws.breakTime(customBreakTime);
        else dotDraws.breakTime(zeroArrayBreaks);
        dotDraws.customDrawSet(startSets, numberOfSets, fadeVar);
        dotDraws.customDrawBreak(startSets, numberOfBreaks);
        break;
      case 2:
        if (!breaksOnlyAreCountingUp) dotDraws.breakOnlyTime(breaksOnlyTime);
        else dotDraws.breakOnlyTime(zeroArrayBreaks);
        dotDraws.breaksOnlyDraw(startBreaksOnly, numberOfBreaksOnly, fadeVar);
        break;
      case 3:
        dotDraws.pomDraw(pomDotCounter, pomValuesTime, fadeVar);
        break;
    }
  }

  //This works for Pom. Just *60 to have the value reflect each minute's seconds.
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

  public void newLap() {
    double newSeconds = msReset / 60;
    double newMinutes = newSeconds / 60;

    double savedMinutes = 0;
    double savedSeconds = 0;
    double savedMs = 0;

    String[] holder = null;
    if (!stopwatchHalted) {
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

  public void pauseAndResumeTimer(int pausing) {
    //disabledTimer booleans are to prevent ANY action being taken.
    if ((!timerDisabled && mode == 1) || (!boTimerDisabled && mode == 2) || (!pomTimerDisabled && mode == 3) || mode == 4) {
      if (fadeInObj != null) fadeInObj.cancel();
      if (fadeOutObj != null) fadeOutObj.cancel();
      switch (mode) {
        case 1:
          if (!modeOneTimerEnded) {
            if (pausing == PAUSING_TIMER) {
              customHalted = true;
              timePaused.setAlpha(1);
              if (timer != null) timer.cancel();
              if (objectAnimator != null) objectAnimator.pause();
              //If sets are counting down, do the following.
              if (!setsAreCountingUp) {
                if (!onBreak) {
                  setMillisUntilFinished = setMillis;
                  timePaused.setText(convertSeconds((setMillis + 999) / 1000));
                  //If on Breaks Mode and sets are counting down, while breaks are also counting down, do the following.
                } else if (!breaksAreCountingUp) {
                  breakMillisUntilFinished = breakMillis;
                  timePaused.setText(convertSeconds((breakMillis + 999) / 1000));
                  //If in Breaks Mode and sets are counting down, but breaks are counting up, do the following.
                } else mHandler.removeCallbacks(secondsUpBreakRunnable);
              } else {
                if (!onBreak) mHandler.removeCallbacks(secondsUpSetRunnable);
                else if (!breaksAreCountingUp) {
                  breakMillisUntilFinished = breakMillis;
                  timePaused.setText(convertSeconds((breakMillis + 999) / 1000));
                } else mHandler.removeCallbacks(secondsUpBreakRunnable);
              }
              reset.setVisibility(View.VISIBLE);
            } else if (pausing == RESUMING_TIMER) {
              reset.setVisibility(View.INVISIBLE);
              customHalted = false;
              timePaused.setAlpha(0);
              if (!setsAreCountingUp) {
                //If on Sets Mode and sets are counting down, do the following.
                if (!onBreak) {
                  startObjectAnimator();
                  startSetTimer();
                  //If on Breaks Mode and sets are counting down, while breaks are also counting down, do the following.
                } else if (!breaksAreCountingUp) {
                  startObjectAnimator();
                  startBreakTimer();
                } else mHandler.post(secondsUpBreakRunnable);
              } else {
                if (!onBreak) {
                  mHandler.post(secondsUpSetRunnable);
                } else {
                  if (!breaksAreCountingUp) {
                    startObjectAnimator();
                    startBreakTimer();
                  } else {
                    mHandler.post(secondsUpBreakRunnable);
                  }
                }
              }
            }
          } else {
            //If cycle has run its course, reset the cycle and re-enable the next_round button.
            resetTimer();
          }
          break;
        case 2:
          if (!modeTwoTimerEnded) {
            if (pausing == PAUSING_TIMER) {
              breaksOnlyHalted = true;
              timePaused.setAlpha(1);
              if (timer != null) timer.cancel();
              if (objectAnimator != null) objectAnimator.pause();
              if (!breaksOnlyAreCountingUp) {
                breaksOnlyMillisUntilFinished = breaksOnlyMillis;
                timePaused.setText(convertSeconds((breaksOnlyMillis + 999) / 1000));
              } else mHandler.removeCallbacks(secondsUpBORunnable);
              reset.setVisibility(View.VISIBLE);
            } else if (pausing == RESUMING_TIMER) {
              reset.setVisibility(View.INVISIBLE);
              timeLeft.setAlpha(1);
              breaksOnlyHalted = false;
              if (!breaksOnlyAreCountingUp) {
                startObjectAnimator();
                startBreakTimer();
              } else {
                mHandler.post(secondsUpBORunnable);
              }
            } else if (pausing == RESETTING_TIMER) {
              if (endAnimation != null) endAnimation.cancel();
              isOvertimeRunning = false;
              mHandler.removeCallbacks(ot);
              overSeconds = 0;
              overtime.setVisibility(View.INVISIBLE);
              progressBar.setProgress(10000);
              timePaused.setAlpha(1.0f);
              timeLeft.setText(convertSeconds((breaksOnlyMillis + 999) / 1000));
              timePaused.setText(convertSeconds((breaksOnlyMillis + 999) / 1000));
            } else if (pausing == RESTARTING_TIMER) {
              startObjectAnimator();
              startBreakTimer();
              boTimerDisabled = false;
              timePaused.setAlpha(0f);
              timeLeft.setAlpha(1.0f);
            }
          } else {
            resetTimer();
          }
          break;
        case 3:
          if (reset.getText().equals(getString(R.string.confirm_cycle_reset)))
            reset.setText(R.string.reset);
          if (!modeThreeTimerEnded) {
            if (pausing == PAUSING_TIMER) {
              timePaused.setAlpha(1);
              pomHalted = true;
              pomMillisUntilFinished = pomMillis;
              if (objectAnimator != null) objectAnimator.pause();
              if (timer != null) timer.cancel();
              String pausedTime2 = (convertSeconds((pomMillisUntilFinished + 999) / 1000));
              timePaused.setText(pausedTime2);
              reset.setVisibility(View.VISIBLE);
            } else if (pausing == RESUMING_TIMER) {
              reset.setVisibility(View.INVISIBLE);
              timePaused.setAlpha(0);
              timeLeft.setAlpha(1);
              pomHalted = false;
              startObjectAnimator();
              startPomTimer();
            }
          } else resetTimer();
          break;
        case 4:
          if (fadeInObj != null) fadeInObj.cancel();
          if (pausing == RESUMING_TIMER) {
            reset.setVisibility(View.INVISIBLE);
            timeLeft.setAlpha(1);
            timePaused.setAlpha(0);
            msTime.setAlpha(1);
            msTimePaused.setAlpha(0);
            stopwatchHalted = false;
            new_lap.setAlpha(1.0f);
            new_lap.setEnabled(true);
            //Main runnable for Stopwatch.
            mHandler.post(stopWatchRunnable);
          } else if (pausing == PAUSING_TIMER) {
            reset.setVisibility(View.VISIBLE);
            timeLeft.setAlpha(0);
            timePaused.setAlpha(1);
            msTime.setAlpha(0);
            msTimePaused.setAlpha(1);
            timePaused.setText(timeLeft.getText());
            msTimePaused.setText(msTime.getText());
            mHandler.removeCallbacksAndMessages(null);
            stopwatchHalted = true;
            new_lap.setAlpha(0.3f);
            new_lap.setEnabled(false);
          }
      }
    }
  }

  public void populateTimerUI() {
    switch (mode) {
      case 1:
        if (customSetTime.size() > 0) {
          setMillis = customSetTime.get(0);
          breakMillis = customBreakTime.get(0);
        }
        if (!setsAreCountingUp) {
          if (customSetTime.size() > 0) {
            timePaused.setText(convertSeconds((setMillis + 999) / 1000));
            timeLeft.setText(convertSeconds((setMillis + 999) / 1000));
          }
        } else {
          timeLeft.setText("0");
          timePaused.setText("0");
        }
        //startSets is our starting number of sets AND breaks, since they will always be the same.
        //numberOfSets/numberOfBreaks are the REMAINING ones, will not always be the same.
        startSets = customSetTime.size();
        numberOfSets = customSetTime.size();
        numberOfBreaks = customBreakTime.size();
        //Used to ensure our list size for breaksOnly is correctly passed in to drawDots on app launch.
        startBreaksOnly = breaksOnlyTime.size();
        //Sets initial text size.
        setTextSize(setMillis);
        break;
      case 2:
        if (breaksOnlyTime.size() > 0) breaksOnlyMillis = breaksOnlyTime.get(0);
        if (!breaksOnlyAreCountingUp) {
          if (breaksOnlyTime.size() > 0) {
            timePaused.setText(convertSeconds((breaksOnlyMillis + 999) / 1000));
            timeLeft.setText(convertSeconds((breaksOnlyMillis + 999) / 1000));
          }
        } else {
          timeLeft.setText("0");
          timePaused.setText("0");
        }
        startBreaksOnly = breaksOnlyTime.size();
        numberOfBreaksOnly = breaksOnlyTime.size();
        //Sets initial text size.
        setTextSize(breaksOnlyMillis);
        break;
      case 3:
        //Here is where we set the initial millis Value of first pomMillis. Set again on change on our value runnables.
        if (pomValuesTime.size() > 0) {
          pomMillis = pomValuesTime.get(0);
          timePaused.setText(convertSeconds((pomMillis + 999) / 1000));
          timeLeft.setText(convertSeconds((pomMillis + 999) / 1000));
          pomTimerDisabled = false;
          //Sets initial text size.
          setTextSize(pomMillis);
        }
        break;
      case 4:
        total_set_header.setVisibility(View.INVISIBLE);
        total_set_time.setVisibility(View.INVISIBLE);
        total_break_header.setVisibility(View.INVISIBLE);
        total_break_time.setVisibility(View.INVISIBLE);
        lapRecycler.setVisibility(View.VISIBLE);
        next_round.setVisibility(View.INVISIBLE);
        reset_total_times.setVisibility(View.GONE);
        new_lap.setVisibility(View.VISIBLE);
        timePaused.setText("0");
        timeLeft.setText("0");
        cycles_completed.setText(getString(R.string.laps_completed, "0"));
        //Modifies top layout.
        ConstraintLayout.LayoutParams completedLapsParam = (ConstraintLayout.LayoutParams) cycles_completed.getLayoutParams();
        ConstraintLayout.LayoutParams lapRecyclerParams = (ConstraintLayout.LayoutParams) lapRecycler.getLayoutParams();
        completedLapsParam.topMargin = 0;
        lapRecyclerParams.topMargin = 60;
    }
    dotDraws.setAlpha();
    drawDots(0);
  }

  public void resetTimer() {
    progressBar.setProgress(10000);
    timePaused.setAlpha(1);
    if (timer != null) timer.cancel();
    if (objectAnimator != null) objectAnimator.cancel();
    endAnimation.cancel();
    //Stores cumulative time valuation.
    saveTotalTimes();
    switch (mode) {
      case 1:
        customProgressPause = maxProgress;
        modeOneTimerEnded = false;
        setBegun = false;
        breakBegun = false;
        customHalted = true;
        onBreak = false;
        //Resetting millis values of count up mode to 0.
        countUpMillisSets = 0;
        countUpMillisBreaks = 0;
        //Removing timer callbacks for count up mode.
        mHandler.removeCallbacks(secondsUpSetRunnable);
        mHandler.removeCallbacks(secondsUpBreakRunnable);
        break;
      case 2:
        breaksOnlyProgressPause = maxProgress;
        modeTwoTimerEnded = false;
        breakOnlyBegun = false;
        breaksOnlyHalted = true;
        mHandler.removeCallbacks(secondsUpBORunnable);
        break;
      case 3:
        pomDotCounter = 1;
        modeThreeTimerEnded = false;
        pomBegun = false;
        pomProgressPause = maxProgress;
        pomHalted = true;
        activePomCycle = false;
        if (objectAnimator != null) objectAnimator.cancel();
        break;
      case 4:
        stopwatchHalted = true;
        ms = 0;
        msConvert = 0;
        msConvert2 = 0;
        msDisplay = 0;
        msReset = 0;
        seconds = 0;
        minutes = 0;
        timeLeft.setAlpha(1);
        timeLeft.setText("0");
        timePaused.setText("0");
        msTime.setAlpha(0);
        msTimePaused.setAlpha(1);
        msTime.setText("00");
        msTimePaused.setText("00");
        if (currentLapList.size() > 0) currentLapList.clear();
        if (savedLapList.size() > 0) savedLapList.clear();
        lapsNumber = 0;
        cycles_completed.setText(getString(R.string.laps_completed, "0"));
        lapAdapter.notifyDataSetChanged();
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
      case 2:
        totalBreakMillis = tempBreakMillis;
        tempBreakMillis = ((totalBreakMillis + 100) / 1000) * 1000;
        total_break_time.setText(convertSeconds(tempBreakMillis/1000));
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
      case 2:
        cyclesBO.setTotalBOTime((int) tempBreakMillis / 1000);
        cyclesBO.setCyclesCompleted(breaksOnlyCyclesDone);
        cyclesDatabase.cyclesDao().updateBOCycles(cyclesBO);
        break;
      case 3:
        pomCycles.setTotalWorkTime((int) tempSetMillis / 1000);
        pomCycles.setTotalBreakTime((int) tempBreakMillis / 1000);
        pomCycles.setCyclesCompleted(pomCyclesDone);
        cyclesDatabase.cyclesDao().updatePomCycles(pomCycles);
        break;
    }

    //Since we re-create our Main activity and do not used saved preferences for these arrays, we re-send the values back to thr activity so they have them.
    Intent exitIntent = new Intent(TimerInterface.this, MainActivity.class);
    if (mode == 1) {
      exitIntent.putIntegerArrayListExtra("infiniteOne", infinityArrayOne);
      exitIntent.putIntegerArrayListExtra("infiniteTwo", infinityArrayTwo);
    } else if (mode == 2) exitIntent.putIntegerArrayListExtra("infiniteThree", infinityArrayThree);
    exitIntent.putExtra("mode", mode);
    startActivity(exitIntent);
  }

  public void deleteTotalTimes() {
    if (mode!=4) delete_text.setText(R.string.delete_total_times); else delete_text.setText(R.string.delete_total_laps);
    deleteCyclePopupWindow.showAtLocation(timerInterface, Gravity.CENTER_HORIZONTAL, 0, -100);
  }

  public void deleteCycle(int typeOfDeletion) {
    //Deletes the currently displayed cycle.
    if (typeOfDeletion == DELETING_CYCLE) {
      switch (mode) {
        case 1:
          cyclesDatabase.cyclesDao().deleteCycle(cycles); break;
        case 2:
          cyclesDatabase.cyclesDao().deleteBOCycle(cyclesBO); break;
        case 3:
          cyclesDatabase.cyclesDao().deletePomCycle(pomCycles); break;
      }
      //Kicks us back to Main, since our current cycle has been deleted.
      runOnUiThread(() -> {
        Intent exitIntent = new Intent(TimerInterface.this, MainActivity.class);
        if (mode == 1) {
          exitIntent.putIntegerArrayListExtra("infiniteOne", infinityArrayOne);
          exitIntent.putIntegerArrayListExtra("infiniteTwo", infinityArrayTwo);
        } else if (mode == 2)
          exitIntent.putIntegerArrayListExtra("infiniteThree", infinityArrayThree);
        exitIntent.putExtra("mode", mode);
        startActivity(exitIntent);
      });
      //Deletes total set/break/pom times and cycles completed.
    } else if (typeOfDeletion == DELETING_TIMES) {
      switch (mode) {
        case 1:
          cyclesDatabase.cyclesDao().deleteTotalTimesCycle(); break;
        case 2:
          cyclesDatabase.cyclesDao().deleteTotalTimesCycleBO(); break;
        case 3:
          cyclesDatabase.cyclesDao().deleteTotalTimesPom();  break;
      }

      runOnUiThread(() -> {
        deleteCyclePopupWindow.dismiss();
        //Resets all total times to 0.
        totalSetMillis = 0;
        tempSetMillis = 0;
        totalBreakMillis = 0;
        tempBreakMillis = 0;

        if (mode==1) {
          permSetMillis = ((setMillis+100) / 1000) * 1000;
          permBreakMillis = ((breakMillis+100) / 1000) * 1000;
          customCyclesDone = 0;
        } else if (mode==2) {
          permBreakMillis = ((breaksOnlyMillis+100) / 1000) * 1000;
          breaksOnlyCyclesDone = 0;
        } else if (mode==3) {
          pomCyclesDone = 0;
        }
        total_set_time.setText("0");
        total_break_time.setText("0");
        cycles_completed.setText(getString(R.string.cycles_done, "0"));
      });
    }
  }
}