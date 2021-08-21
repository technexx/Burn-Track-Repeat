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
import android.view.MotionEvent;
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

import androidx.annotation.NonNull;
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

public class TimerInterface extends AppCompatActivity implements DotDraws.sendAlpha, LapAdapter.onPositionCallback {

  ProgressBar progressBar;
  ImageView stopWatchView;
  TextView timeLeft;
  TextView msTime;
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
  int DELETING_CYCLE = 1;
  int DELETING_TIMES = 2;

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

  long progressBarValueHolder;
  long pomMillis;

  int maxProgress = 10000;
  int customProgressPause = 10000;
  int pomProgressPause = 10000;
  long setMillisUntilFinished;
  long breakMillisUntilFinished;
  long pomMillisUntilFinished;
  Runnable endFade;
  Runnable ot;
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
  int breaksOnlyCyclesDone;
  int pomCyclesDone;
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
  int mode = 1;
  int savedMode;
  ValueAnimator sizeAnimator;
  ValueAnimator valueAnimatorDown;
  ValueAnimator valueAnimatorUp;
  AlphaAnimation fadeIn;
  AlphaAnimation fadeOut;
  AlphaAnimation fadeProgressIn;
  AlphaAnimation fadeProgressOut;
  boolean textSizeIncreased;
  //Always true initially, since infinity mode starts at 0.
  boolean textSizeReduced = true;

  ArrayList<String> workoutArray;
  ArrayList<Integer> workoutTime;
  ArrayList<Integer> typeOfRound;

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

  SharedPreferences sharedPreferences;
  SharedPreferences.Editor prefEdit;
  int receivedAlpha;
  MaterialButton pauseResumeButton;

  long countUpMillisSets;
  long countUpMillisBreaks;
  public Runnable secondsUpSetRunnable;
  public Runnable secondsUpBreakRunnable;

  ImageButton exit_timer;

  ConstraintLayout timerInterface;
  View deleteCyclePopupView;
  PopupWindow deleteCyclePopupWindow;
  Button confirm_delete;
  Button cancel_delete;
  TextView delete_text;

  CyclesDatabase cyclesDatabase;
  List<Cycles> cyclesList;
  List<PomCycles> pomCyclesList;
  Cycles cycles;
  CyclesBO cyclesBO;
  PomCycles pomCycles;
  int passedID;
  boolean isNewCycle;
  SavedCycleAdapter savedCycleAdapter;

  boolean resetMenu;
  long baseTime;
  long countUpMillisHolder;
  int scrollPosition;

  @Override
  public void onBackPressed() {
    AsyncTask.execute(()-> {
      exitTimer();
    });
  }

  @Override
  public boolean onCreateOptionsMenu(final Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.timer_options_menu, menu);

    //Boolean set to true on first menu click so this doesm't execute on activity's initial instantiation Toggles menu text and visibility of total time counters.
    if (resetMenu) {
      MenuItem counterItem = menu.findItem(R.id.hide_counter);
      if (reset_total_times.getVisibility()==View.VISIBLE) {
        hideCounter(true);
        counterItem.setTitle("Show Counter");
      } else {
        hideCounter(false);
        counterItem.setTitle("Hide Counter");
      }
    }
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
        //Refreshes onCreateOptionsMenu.
      case R.id.hide_counter:
        resetMenu = true;
        invalidateOptionsMenu();
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void sendAlphaValue(int alpha) {
    receivedAlpha = alpha;
  }

  @Override
  public void positionCallback(int position) {
    scrollPosition = position;
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
    msTime = findViewById(R.id.msTime);
    dotDraws = findViewById(R.id.dotdraws);
    lapRecycler = findViewById(R.id.lap_recycler);
    overtime = findViewById(R.id.overtime);
    pauseResumeButton = findViewById(R.id.pauseResumeButton);
    pauseResumeButton.setBackgroundColor(Color.argb(0, 0, 0, 0));
    pauseResumeButton.setRippleColor(null);
    exit_timer = findViewById(R.id.exit_timer);
    reset_total_times = findViewById(R.id.reset_total_times);
    empty_laps = findViewById(R.id.empty_laps_text);
    if (mode!=4) empty_laps.setVisibility(View.INVISIBLE);

    stopWatchView.setVisibility(View.GONE);
    lapRecycler.setVisibility(View.GONE);
    overtime.setVisibility(View.INVISIBLE);
    new_lap.setVisibility(View.INVISIBLE);

    cycles_completed.setText(R.string.cycles_done);
    cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));

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

    fadeProgressIn = new AlphaAnimation(0.3f, 1.0f);
    fadeProgressOut = new AlphaAnimation(1.0f, 0.3f);
    fadeProgressIn.setDuration(300);
    fadeProgressOut.setDuration(300);

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

    //Receives lists passed in from Main.
    Intent intent = getIntent();
    if (intent != null) {
      isNewCycle = intent.getBooleanExtra("isNewCycle", false);
      mode = intent.getIntExtra("mode", 0);
      //Used as a reference to the mode we launched Stopwatch from, if we did.
      savedMode = intent.getIntExtra("savedMode", 0);
      cycle_title = intent.getStringExtra("cycleTitle");
      //The primary key ID of our current cycle row.
      passedID = intent.getIntExtra("passedID", 0);
    }
    cycle_header_text.setText(cycle_title);

    //Retrieves our list of Timer values for each round in the cycle.
    switch (mode) {
      case 1:
        workoutTime = intent.getIntegerArrayListExtra("workoutTime");
        typeOfRound = intent.getIntegerArrayListExtra("typeOfRound");
        break;
      case 3:
        pomValuesTime = intent.getIntegerArrayListExtra("pomList");
        //Replaces "total set time" w/ "work time".
        total_set_header.setText(R.string.total_work);
        break;
    }

    /////---------Testing pom round iterations---------------/////////
    if (mode==3) for (int i=1; i<9; i++) if (i%2!=0) pomValuesTime.set(i-1, 4000); else pomValuesTime.set(i-1, 6000);

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
          case 3:
            //Todo: Retrieval here.
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

    //Recycler view for our stopwatch laps.
    lapLayout = new LinearLayoutManager(getApplicationContext());
    lapAdapter = new LapAdapter(getApplicationContext(), currentLapList, savedLapList);
    lapRecycler.setAdapter(lapAdapter);
    lapRecycler.setLayoutManager(lapLayout);
    lapAdapter.passPosition(TimerInterface.this);

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
        Log.i("testpos", "received position is " + scrollPosition);
        getVisibleLapPositions();
        if (dy!=0) lapAdapter.listIsScrolling(true);
      }
    });

    //Sets all progress bars to their start value.
    progressBar.setProgress(maxProgress);

    //Populates UI elements at app start.
    populateTimerUI();

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

        //        stopWatchMsCounter = (System.currentTimeMillis() - baseTime);
        //        tempStopwatchMs = totalStopwatchMs + stopWatchMsCounter;
        //        displayTime = convertStopwatch((long)tempStopwatchMs/1000);
        //        timeLeft.setText(displayTime);
        //
        //        //Takes a whole number division result (tempSeconds) from our current stopwatch millis value, multiplies it by 1000, and subtracts that from our current stopwatch millis value to get our ms display value (e.g. 8400 / 1000 = 8 -> 8*1000 = 8000 -> 8400 - 8000 == 400 -> 400/10).
        //        long tempSeconds = tempStopwatchMs/1000;
        //        if (tempSeconds>0) msDisplay = (tempStopwatchMs - (tempSeconds*1000)) / 10; else msDisplay = stopWatchMsCounter/10;
        //        msTime.setText(String.valueOf(msDisplay));
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
          activePomCycle = true;
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

      //Todo: These will always be one behind.
//      int firstPos = lapLayout.findFirstVisibleItemPosition();
//      int lastPos = lapLayout.findLastVisibleItemPosition();
//      lapAdapter.receiveVisiblePositions(firstPos, lastPos);
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
          //tempMillis is used to retain value in active runnables. Here, we set our static totalMillis to the value that has been iterated up to.
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
          cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(pomCyclesDone)));
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
    dotDraws.resetDotAlpha();
    //Setting values based on first round in cycle. Might make this is a global method.
    switch (mode) {
      case 1:
        //Resets base progressBar duration for count-up rounds.
        progressBarValueHolder = 30000;
        //Sets the long value in our "count up" rounds back to 0.
        for (int i=0; i<startRounds; i++) {
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
        timerDisabled = false ;
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
        activePomCycle = false;
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
        pomCycles.setCyclesCompleted(pomCyclesDone);
        cyclesDatabase.cyclesDao().updatePomCycles(pomCycles);
        break;
    }

    //Since we re-create our Main activity and do not used saved preferences for these arrays, we re-send the values back to thr activity so they have them.
    Intent exitIntent = new Intent(TimerInterface.this, MainActivity.class);
    exitIntent.putExtra("mode", mode);
    exitIntent.putExtra("savedMode", savedMode);
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
        case 3:
          cyclesDatabase.cyclesDao().deletePomCycle(pomCycles); break;
      }
      //Kicks us back to Main, since our current cycle has been deleted.
      runOnUiThread(() -> {
        Intent exitIntent = new Intent(TimerInterface.this, MainActivity.class);
        exitIntent.putExtra("mode", mode);
        startActivity(exitIntent);
      });
      //Deletes total set/break/pom times and cycles completed.
    } else if (typeOfDeletion == DELETING_TIMES) {
      switch (mode) {
        case 1:
          cyclesDatabase.cyclesDao().deleteTotalTimesCycle(); break;
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