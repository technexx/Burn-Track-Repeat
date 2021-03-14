package com.example.tragic.irate.simple.stopwatch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tragic.irate.simple.stopwatch.Database.CyclesDatabase;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SavedCycleAdapter.onCycleClickListener, SavedCycleAdapter.onDeleteCycleListener{

    Button breaks_only;
    TextView save_cycles;
    ProgressBar progressBar;
    ProgressBar progressBar2;
    ImageView stopWatchView;
    TextView timeLeft;
    TextView timeLeft2;
    TextView timePaused;
    TextView timePaused2;
    TextView timeLeft3;
    TextView timePaused3;
    TextView msTime;
    TextView msTimePaused;
    CountDownTimer timer;
    CountDownTimer timer2;
    TextView reset;
    ObjectAnimator objectAnimator;
    ObjectAnimator objectAnimator2;
    Animation endAnimation;
    public Handler mHandler;
    TextView lastTextView;

    TextView s1;
    TextView s2;
    TextView s3;
    long setValue;
    long breakValue;
    long breaksOnlyValue;
    long pomValue1;
    long pomValue2;
    long pomValue3;
    TextView no_set_header;
    TextView cycles_completed;
    TextView cycle_reset;
    EditText first_value_text;
    EditText second_value_text;
    EditText third_value_text;
    ImageView plus_first_value;
    ImageView minus_first_value;
    ImageView plus_second_value;
    ImageView minus_second_value;
    ImageButton plus_third_value;
    ImageButton minus_third_value;
    Button add_cycle;
    Button sub_cycle;

    ImageView sortCheckmark;
    TextView skip;
    TextView newLap;

    List<Long> setValueSaves;
    List<Long> breakValueSaves;
    List<Long> breaksOnlyValueSaves;
    List<Long> pomOneSaves;
    List<Long> pomTwoSaves;
    List<Long> pomThreeSaves;

    int PAUSING_TIMER = 1;
    int RESUMING_TIMER = 2;

    long breakMillis;
    int breakStart;
    long setMillis;
    int setStart;
    int maxProgress = 10000;
    int customProgressPause = 10000;
    int pomProgressPause = 10000;
    long pomMillis;
    long setMillisUntilFinished;
    long breakMillisUntilFinished;
    long pomMillisUntilFinished;
    boolean incrementValues;
    int incrementTimer;
    Runnable changeFirstValue;
    Runnable changeSecondValue;
    Runnable changeThirdValue;
    Runnable valueSpeed;

    int firstSpinCount;
    int secondSpinCount;
    int thirdSpinCount;

    long pomMillis1;
    long pomMillis2;
    long pomMillis3;
    int pomDotCounter=1;

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

    long numberOfSets;
    long numberOfBreaks;
    long savedBreaks;
    long savedSets;

    boolean customTimerEnded;
    boolean pomTimerEnded;
    boolean paused;
    boolean onBreak;
    boolean pomEnded;
    boolean timerDisabled;
    boolean customHalted = true;
    boolean pomHalted = true;
    boolean stopwatchHalted = true;

    DotDraws dotDraws;
    int fadeDone;
    int mode=1;
    ValueAnimator sizeAnimator;
    ValueAnimator valueAnimatorDown;
    ValueAnimator valueAnimatorUp;

    public ArrayList<Long> customSetTime;
    public ArrayList<Long> customBreakTime;
    public ArrayList<Long> startCustomSetTime;
    public ArrayList<Long> startCustomBreakTime;
    public ArrayList<Long> startBreaksOnlyTime;
    public ArrayList<Long> breaksOnlyTime;

    boolean setBegun;
    boolean breakBegun;
    boolean pomBegun;
    boolean drawing = true;
    boolean breaksOnly;

    PopupWindow cyclePopupWindow;
    PopupWindow resetPopUpWindow;
    PopupWindow savedCyclePopupWindow;
    PopupWindow sortPopupWindow;

    boolean fadeCustomTimer;
    boolean fadePomTimer;
    float customAlpha;
    float pomAlpha;
    ObjectAnimator fadeInObj;
    ObjectAnimator fadeOutObj;
    RecyclerView lapRecycler;
    LapAdapter lapAdapter;
    LinearLayoutManager lapLayout;

    CyclesDatabase cyclesDatabase;
    List<Cycles> cyclesList;
    Cycles cycles;
    List<CyclesBO> cyclesBOList;
    CyclesBO cyclesBO;

    ArrayList<String> setsArray;
    ArrayList<String> breaksArray;
    ArrayList<String> breaksOnlyArray;

    ConstraintLayout cl;
    RecyclerView savedCycleRecycler;
    SavedCycleAdapter savedCycleAdapter;
    View savedCyclePopupView;
    View deleteCyclePopupView;
    View sortCyclePopupView;

    TextView sortRecent;
    TextView sortNotRecent;
    TextView sortHigh;
    TextView sortLow;
    int sortMode = 1;
    int sortModeBO = 1;
    MaterialButton pauseResumeButton;
    int blankCount;

    //Todo: Single break or break/set option, like Stopwatch but counting down on repeat.
    //Todo: EditTexts w/ hints as default view. Make sure minute/hours difference in custom/pom are clear.
    //Todo: Add/Remove cycle elements from individual places.
    //Todo: Rippling for certain onClicks.
    //Todo: Inconsistencies w/ fading.
    //Todo: Add taskbar notification for timers.
    //Todo: Have skip round reset dot alpha.
    //Todo: Add color scheme options.
    //Todo: Test all Pom cycles.
    //Todo: All DB calls in aSync.
    //Todo: Rename app, of course.
    //Todo: Add onOptionsSelected dots for About, etc.
    //Todo: Repository for db. Look at Executor/other alternate thread methods.

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (event.getAction()==MotionEvent.ACTION_DOWN) {
            dotDraws.selectCycle(x, y);
        }
        return false;
    }

    //Remember that any reference to our GLOBAL instance of a cycles position will retain that position unless changed.
    @Override
    public void onCycleClick(int position) {
        AsyncTask.execute(() -> {
            queryCycles();
            if (!breaksOnly) {
                String tempSets = cyclesList.get(position).getSets();
                String tempBreaks = cyclesList.get(position).getBreaks();
                String[] setSplit = tempSets.split(" - ", 0);
                String[] breakSplit = tempBreaks.split(" - ", 0);

                customSetTime.clear();
                startCustomSetTime.clear();
                customBreakTime.clear();
                startCustomBreakTime.clear();

                for (int i=0; i<setSplit.length; i++) {
                    customSetTime.add(Long.parseLong(setSplit[i])*1000);
                    startCustomSetTime.add(Long.parseLong(setSplit[i])*1000);
                    customBreakTime.add(Long.parseLong(breakSplit[i])*1000);
                    startCustomBreakTime.add(Long.parseLong(breakSplit[i])*1000);
                }
            } else {
                String tempBreaksOnly = cyclesBOList.get(position).getBreaksOnly();
                String[] breaksOnlySplit = tempBreaksOnly.split(" - ", 0);

                startBreaksOnlyTime.clear();
                breaksOnlyTime.clear();

                for (int i=0; i<breaksOnlySplit.length; i++) {
                    breaksOnlyTime.add(Long.parseLong(breaksOnlySplit[i])*1000);
                    startBreaksOnlyTime.add(Long.parseLong(breaksOnlySplit[i])*1000);
                }
            }
            runOnUiThread(() -> {
                resetTimer();
                savedCyclePopupWindow.dismiss();
            });
        });
    }

    @Override
    public void onCycleDelete(int position) {
        if (!breaksOnly) {
            AsyncTask.execute(() -> {
                queryCycles();
                Cycles removedCycle = cyclesList.get(position);
                cyclesDatabase.cyclesDao().deleteCycle(removedCycle);
                queryCycles();

                runOnUiThread(() -> {
                    setsArray.clear();
                    breaksArray.clear();
                    for (int i=0; i<cyclesList.size(); i++) {
                        setsArray.add(cyclesList.get(i).getSets());
                        breaksArray.add(cyclesList.get(i).getBreaks());
                    }
                    savedCycleAdapter.notifyDataSetChanged();
                    if (setsArray.size()==0) {
                        savedCyclePopupWindow.dismiss();
                        save_cycles.setText(R.string.save_cycles);
                    }
                });
            });
        } else {
            queryCycles();
            CyclesBO removedBOCycle = cyclesBOList.get(position);
            cyclesDatabase.cyclesDao().deleteBOCycle(removedBOCycle);
            queryCycles();

            runOnUiThread(() -> {
                breaksOnlyArray.clear();
                for (int i=0; i<cyclesBOList.size(); i++) {
                    breaksOnlyArray.add(cyclesBOList.get(i).getBreaksOnly());
                }
                savedCycleAdapter.notifyDataSetChanged();
                if (breaksOnlyArray.size()==0) {
                    savedCyclePopupWindow.dismiss();
                    save_cycles.setText(R.string.save_cycles);
                }
            });
        }
        Toast.makeText(getApplicationContext(), "Cycle deleted!", Toast.LENGTH_SHORT).show();
        cl.setOnClickListener(v-> {
            savedCyclePopupWindow.dismiss();
            save_cycles.setText(R.string.save_cycles);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (savedCyclePopupWindow != null && savedCyclePopupWindow.isShowing()) savedCyclePopupWindow.dismiss();
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (savedCyclePopupWindow!=null &&savedCyclePopupWindow.isShowing()) return false;

        switch (item.getItemId()) {
            case R.id.saved_cycle_list:
                    AsyncTask.execute(() -> {
                        queryCycles();
                        clearArrays(false);

                        if (!breaksOnly) {
                            savedCycleAdapter.setBreaksOnly(false);
                            if (cyclesList.size()>0) {
                                for (int i=0; i<cyclesList.size(); i++) {
                                    //getSets/Breaks returns String [xx, xy, xz] etc.
                                    setsArray.add(cyclesList.get(i).getSets());
                                    breaksArray.add(cyclesList.get(i).getBreaks());
                                }
                                runOnUiThread(() -> {
                                    savedCyclePopupWindow = new PopupWindow(savedCyclePopupView, 800, 1200, false);
                                    savedCyclePopupWindow.setAnimationStyle(R.style.WindowAnimation);
                                    savedCyclePopupWindow.showAtLocation(savedCyclePopupView, Gravity.CENTER, 0, 100);
                                    save_cycles.setText(R.string.sort_cycles);
                                });
                        } else {
                                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Nothing saved!", Toast.LENGTH_SHORT).show());
                            }
                        } else {
                            savedCycleAdapter.setBreaksOnly(true);
                            if (cyclesBOList.size()>0) {
                                for (int i=0; i<cyclesBOList.size(); i++) {
                                    breaksOnlyArray.add(cyclesBOList.get(i).getBreaksOnly());
                                }
                                runOnUiThread(() -> {
                                    savedCyclePopupWindow = new PopupWindow(savedCyclePopupView, 800, 1200, false);
                                    savedCyclePopupWindow.setAnimationStyle(R.style.WindowAnimation);
                                    savedCyclePopupWindow.showAtLocation(savedCyclePopupView, Gravity.CENTER, 0, 100);
                                    save_cycles.setText(R.string.sort_cycles);
                                });
                            } else {
                                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Nothing saved!", Toast.LENGTH_SHORT).show());
                            }
                        }
                        runOnUiThread(() -> cl.setOnClickListener(v2-> {
                            savedCycleAdapter.notifyDataSetChanged();
                            savedCyclePopupWindow.dismiss();
                            save_cycles.setText(R.string.save_cycles);
                        }));
                    });

                break;

            case R.id.delete_all_cycles:
                AsyncTask.execute(() -> {
                    if (!breaksOnly) cyclesDatabase.cyclesDao().deleteAll(); else cyclesDatabase.cyclesDao().deleteAllBO();
                    runOnUiThread(() -> {
//                        deleteAllPopupWindow = new PopupWindow(deleteCyclePopupView, 600, 300, false);
//                        deleteAllPopupWindow.showAtLocation(deleteCyclePopupView, Gravity.CENTER, 0, -450);
                        Toast.makeText(getApplicationContext(), "Deleted!", Toast.LENGTH_SHORT).show();
                    });
                });

        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AsyncTask.execute(() -> {
            cyclesDatabase = CyclesDatabase.getDatabase(getApplicationContext());
            cyclesList = new ArrayList<>();
            cycles = new Cycles();
            cyclesBOList = new ArrayList<>();
            cyclesBO = new CyclesBO();

            cyclesList = cyclesDatabase.cyclesDao().loadAllCycles();
            cyclesBOList = cyclesDatabase.cyclesDao().loadAllBOCycles();
            if (cyclesList.size()>0) {
                for (int i=0; i<cyclesList.size(); i++) {
                    setsArray.add(cyclesList.get(i).getSets());
                    breaksArray.add(cyclesList.get(i).getBreaks());
                }
            }
        });

        valueAnimatorDown = new ValueAnimator().ofFloat(90f, 70f);
        valueAnimatorUp = new ValueAnimator().ofFloat(70f, 90f);
        valueAnimatorDown.setDuration(1000);
        valueAnimatorUp.setDuration(1000);

        setsArray = new ArrayList<>();
        breaksArray = new ArrayList<>();
        breaksOnlyArray = new ArrayList<>();

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        savedCyclePopupView = inflater.inflate(R.layout.saved_cycles_layout, null);
        deleteCyclePopupView = inflater.inflate(R.layout.delete_cycles_popup, null);
        sortCyclePopupView = inflater.inflate(R.layout.sort_popup, null);
        sortRecent = sortCyclePopupView.findViewById(R.id.sort_most_recent);
        sortNotRecent = sortCyclePopupView.findViewById(R.id.sort_least_recent);
        sortHigh = sortCyclePopupView.findViewById(R.id.sort_number_high);
        sortLow = sortCyclePopupView.findViewById(R.id.sort_number_low);
        sortCheckmark = sortCyclePopupView.findViewById(R.id.sortCheckmark);

        savedCycleRecycler = savedCyclePopupView.findViewById(R.id.cycle_list_recycler);
        LinearLayoutManager lm2 = new LinearLayoutManager(getApplicationContext());

        savedCycleAdapter = new SavedCycleAdapter(getApplicationContext(), setsArray, breaksArray, breaksOnlyArray);
        savedCycleRecycler.setAdapter(savedCycleAdapter);
        savedCycleRecycler.setLayoutManager(lm2);
        savedCycleAdapter.setItemClick(MainActivity.this);
        savedCycleAdapter.setDeleteCycle(MainActivity.this);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_bar);

        s1 = findViewById(R.id.s1);
        s2 = findViewById(R.id.s2);
        s3 = findViewById(R.id.s3);
        no_set_header = findViewById(R.id.no_set_header);
        reset = findViewById(R.id.reset);
        first_value_text = findViewById(R.id.first_value_text);
        second_value_text = findViewById(R.id.second_value_text);
        plus_first_value = findViewById(R.id.plus_first_value);
        minus_first_value = findViewById(R.id.minus_first_value);
        plus_second_value = findViewById(R.id.plus_second_value);
        minus_second_value = findViewById(R.id.minus_second_value);
        plus_third_value = findViewById(R.id.plus_third_value);
        minus_third_value = findViewById(R.id.minus_third_value);
        third_value_text = findViewById(R.id.third_value_text);
        add_cycle = findViewById(R.id.add_cycle);
        sub_cycle = findViewById(R.id.subtract_cycle);
        cycles_completed = findViewById(R.id.cycles_completed);
        cycle_reset = findViewById(R.id.cycle_reset);
        skip = findViewById(R.id.skip);
        newLap = findViewById(R.id.new_lap);

        reset.setVisibility(View.INVISIBLE);
        no_set_header.setVisibility(View.GONE);

        breaks_only = findViewById(R.id.breaks_only);
        save_cycles = findViewById(R.id.save_cycles);
        progressBar = findViewById(R.id.progressBar);
        progressBar2 = findViewById(R.id.progressBar2);
        stopWatchView = findViewById(R.id.stopWatchView);
        timeLeft = findViewById(R.id.timeLeft);
        timeLeft2 = findViewById(R.id.timeLeft2);
        timeLeft3 =findViewById(R.id.timeLeft3);
        timePaused = findViewById(R.id.timePaused);
        timePaused2 = findViewById(R.id.timePaused2);
        timePaused3 = findViewById(R.id.timePaused3);
        msTime = findViewById(R.id.msTime);
        msTimePaused = findViewById(R.id.msTimePaused);
        dotDraws = findViewById(R.id.dotdraws);
        lapRecycler = findViewById(R.id.lap_recycler);
        cl = findViewById(R.id.main_layout);
        pauseResumeButton = findViewById(R.id.pauseResumeButton);
        pauseResumeButton.setBackgroundColor(Color.argb(0, 0, 0, 0));
        pauseResumeButton.setRippleColor(null);

        s1.setText(R.string.set_time);
        s2.setText(R.string.break_time);
        s3.setText(R.string.set_number);

        timeLeft.setTextSize(90f);
        timePaused.setTextSize(90f);
        timeLeft2.setTextSize(70f);
        timePaused2.setTextSize(70f);
        timeLeft3.setTextSize(90f);
        timePaused3.setTextSize(90f);
        skip.setText(R.string.skip_round);
        newLap.setText(R.string.lap);
        cycle_reset.setText(R.string.clear_cycles);
        cycles_completed.setText(R.string.cycles_done);
        cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Custom"));
        tabLayout.addTab(tabLayout.newTab().setText("Pomodoro"));
        tabLayout.addTab(tabLayout.newTab().setText("Stopwatch"));

        mHandler = new Handler();
        setValueSaves = new ArrayList<>();
        breakValueSaves = new ArrayList<>();
        breaksOnlyValueSaves = new ArrayList<>();
        pomOneSaves = new ArrayList<>();
        pomTwoSaves = new ArrayList<>();
        pomThreeSaves = new ArrayList<>();
        customSetTime = new ArrayList<>();
        customBreakTime = new ArrayList<>();
        startCustomSetTime = new ArrayList<>();
        startCustomBreakTime = new ArrayList<>();
        breaksOnlyTime = new ArrayList<>();
        startBreaksOnlyTime = new ArrayList<>();
        currentLapList = new ArrayList<>();
        savedLapList = new ArrayList<>();

        no_set_header.setText(R.string.no_sets);
        s3.setText(R.string.set_number);
        progressBar2.setVisibility(View.GONE);
        stopWatchView.setVisibility(View.GONE);
        newLap.setVisibility(View.GONE);
        lapRecycler.setVisibility(View.GONE);

        removeViews();
        timePaused.setAlpha(1);

        //Default list values for Custom.
        for (int i=0; i<3; i++) {
            customSetTime.add((long) 30 * 1000);
            customBreakTime.add((long) 30 * 1000);
            startCustomSetTime.add((long) 30 * 1000);
            startCustomBreakTime.add((long) 30 * 1000);
            breaksOnlyTime.add((long) 30 * 1000);
            startBreaksOnlyTime.add((long) 30 * 1000);
        }
        setValue = 30;
        breakValue = 30;
        breaksOnlyValue = 30;
        pomValue1 = 25;
        pomValue2 = 5;
        pomValue3 = 15;
        setMillis = setValue;
        breakMillis = breakValue;
        first_value_text.setText(convertSeconds(setValue));
        second_value_text.setText(convertSeconds(breakValue));
        third_value_text.setText(String.valueOf(customSetTime.size()));

        lapLayout= new LinearLayoutManager(getApplicationContext());
        lapAdapter = new LapAdapter(getApplicationContext(), currentLapList, savedLapList);
        lapRecycler.setAdapter(lapAdapter);
        lapRecycler.setLayoutManager(lapLayout);

        //Custom defaults
        mode = 1;
        dotDraws.setMode(1);
        savedSets = customSetTime.size();
        savedBreaks = customBreakTime.size();
        numberOfSets = savedSets;
        numberOfBreaks = savedBreaks;

        progressBar.setProgress(maxProgress);
        progressBar2.setProgress(maxProgress);

        //Todo: Third value on Custom should not be EditText.
        resetTimer();
        incrementTimer = 10;

        changeFirstValue = new Runnable() {
            @Override
            public void run() {
                switch (mode) {
                    case 1:
                        if (incrementValues) setValue+=1; else setValue -=1; break;
                    case 2:
                        if (incrementValues) pomValue1+=1; else pomValue1 -=1; break;
                }
                mHandler.postDelayed(this, incrementTimer*10);
            }
        };

        changeSecondValue = new Runnable() {
            @Override
            public void run() {
                switch (mode) {
                    case 1:
                        if (!breaksOnly) {
                            if (incrementValues) breakValue+=1; else breakValue -=1;
                        } else
                        if (incrementValues) breaksOnlyValue+=1; else breaksOnlyValue -=1; break;
                    case 2:
                        if (incrementValues) pomValue2+=1; else pomValue2 -=1; break;
                }
                mHandler.postDelayed(this, incrementTimer*10);
            }
        };

        changeThirdValue = new Runnable() {
            @Override
            public void run() {
                if (incrementValues) pomValue3+=1; else pomValue3 -=1;
                mHandler.postDelayed(this, incrementTimer*10);
            }
        };

        valueSpeed = new Runnable() {
            @Override
            public void run() {
                if (incrementTimer>1) incrementTimer-=1;
                mHandler.postDelayed(this, 300);
            }
        };

        plus_first_value.setOnTouchListener((v, event) -> {
            incrementValues = true;
            setIncrements(event, changeFirstValue);
            switch (mode) {
                case 1: first_value_text.setText(convertSeconds(setValue)); break;
                case 2: first_value_text.setText(convertSeconds(pomValue1)); break;
            }
            return true;
        });

        minus_first_value.setOnTouchListener((v, event) -> {
            incrementValues = false;
            setIncrements(event, changeFirstValue);
            switch (mode) {
                case 1: first_value_text.setText(convertSeconds(setValue)); break;
                case 2: first_value_text.setText(convertSeconds(pomValue1)); break;

            }
            return true;
        });

        plus_second_value.setOnTouchListener((v, event) -> {
            incrementValues = true;
            setIncrements(event, changeSecondValue);
            switch (mode) {
                case 1:
                    if (!breaksOnly) second_value_text.setText(convertSeconds(breakValue)); else second_value_text.setText(convertSeconds(breaksOnlyValue)); break;
                case 2: second_value_text.setText(convertSeconds(pomValue2)); break;

            }
            return true;
        });

        minus_second_value.setOnTouchListener((v, event) -> {
            incrementValues = false;
            setIncrements(event, changeSecondValue);
            switch (mode) {
                case 1:
                    if (!breaksOnly) second_value_text.setText(convertSeconds(breakValue)); else second_value_text.setText(convertSeconds(breaksOnlyValue)); break;
                case 2: second_value_text.setText(convertSeconds(pomValue2)); break;
            }
            return true;
        });

        plus_third_value.setOnTouchListener((v, event) -> {
            switch (mode) {
                case 1:
                    if (event.getAction()==MotionEvent.ACTION_DOWN) adjustCustom(true);  break;
                case 2:
                    incrementValues = true;
                    setIncrements(event, changeThirdValue);
                    third_value_text.setText(convertSeconds(pomValue3));
                    break;
            }
            return true;
        });

        minus_third_value.setOnTouchListener((v, event) -> {
            switch (mode) {
                case 1:
                    if (event.getAction()==MotionEvent.ACTION_DOWN) adjustCustom(false);  break;
                case 2:
                    incrementValues = false;
                    setIncrements(event, changeThirdValue);
                    third_value_text.setText(convertSeconds(pomValue3));
                    break;
            }
            return true;
        });

        add_cycle.setOnClickListener(v-> {

        });

        sub_cycle.setOnClickListener(v-> {

        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        mode=1;
                        switchTimer(1, customHalted);
                        dotDraws.setMode(1);
                        if (!setBegun) dotDraws.newDraw(savedSets, savedBreaks, 0, 0, 0);
                        break;
                    case 1:
                        mode=2;
                        switchTimer(2, pomHalted);
                        dotDraws.setMode(2);
                        dotDraws.pomDraw(1, 0);
                        break;
                    case 2:
                        mode=3;
                        switchTimer(3, stopwatchHalted);
                        lapRecycler.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                firstSpinCount = 0;
                secondSpinCount = 0;
                thirdSpinCount = 0;
                switch (tab.getPosition()) {
                    case 0:
                        if (customHalted) {
                            fadeOutText(timePaused); lastTextView = timePaused;
                        } else {
                            fadeOutText(timeLeft); lastTextView = timeLeft;
                        }
                        break;
                    case 1:
                        if (pomHalted) {
                            fadeOutText(timePaused2); lastTextView = timePaused2;
                        } else {
                            fadeOutText(timeLeft2); lastTextView = timeLeft2;
                        }
                        if (cyclePopupWindow!=null) cyclePopupWindow.dismiss();
                        if (resetPopUpWindow!=null) resetPopUpWindow.dismiss();
                        break;
                    case 2:
                        if (stopwatchHalted) {
                            fadeOutText(timePaused3);
//                            fadeOutText(msTimePaused);
                        } else {
                            fadeOutText(timeLeft3);
//                            fadeOutText(msTime);
                        }
                        lapRecycler.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        breaks_only.setOnClickListener(v-> {
            if (savedCyclePopupWindow!=null && savedCyclePopupWindow.isShowing()) {
                savedCyclePopupWindow.dismiss();
                return;
            }

            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setDuration(400);

            Animation fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setDuration(100);

            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    if (breaksOnly) {
                        s1.setVisibility(View.INVISIBLE);
                        plus_first_value.setVisibility(View.INVISIBLE);
                        minus_first_value.setVisibility(View.INVISIBLE);
                        first_value_text.setVisibility(View.INVISIBLE);
                        no_set_header.setAnimation(fadeIn);
                    }
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            fadeIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    if (breaksOnly) no_set_header.setVisibility(View.VISIBLE); else {
                        no_set_header.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (!breaksOnly) {
                        s1.setVisibility(View.VISIBLE);
                        plus_first_value.setVisibility(View.VISIBLE);
                        minus_first_value.setVisibility(View.VISIBLE);
                        first_value_text.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            if (mode==1) {
                if (!breaksOnly) {
                    breaksOnly = true;
                    s1.setAnimation(fadeOut);
                    first_value_text.setAnimation(fadeOut);
                    plus_first_value.setAnimation(fadeOut);
                    minus_first_value.setAnimation(fadeOut);
                    setBegun = true;
                    onBreak = true;
                    breaks_only.setBackgroundColor(getResources().getColor(R.color.light_grey));
                    dotDraws.breaksOnly(true);
                    cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(breaksOnlyCyclesDone)));
                } else {
                    breaksOnly = false;
                    no_set_header.setAnimation(fadeOut);
                    s1.setAnimation(fadeIn);
                    first_value_text.setAnimation(fadeIn);
                    plus_first_value.setAnimation(fadeIn);
                    minus_first_value.setAnimation(fadeIn);
                    setBegun = false;
                    breaks_only.setBackgroundColor(getResources().getColor(R.color.black));
                    dotDraws.breaksOnly(false);
                    cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));
                }
                dotDraws.newDraw(savedSets, savedBreaks, savedSets-(numberOfSets-1), savedBreaks-(numberOfBreaks-1), 1);
                resetTimer();
            }
        });

        save_cycles.setOnClickListener(v->{
            if (savedCyclePopupWindow!=null && savedCyclePopupWindow.isShowing()){
                sortPopupWindow = new PopupWindow(sortCyclePopupView, 400, 375, true);
                sortPopupWindow.showAtLocation(sortCyclePopupView, Gravity.TOP, 325, 10);

                sortRecent.setOnClickListener(v1 -> {
                    sortCheckmark.setY(14);
                    AsyncTask.execute(() -> {
                        cyclesList = cyclesDatabase.cyclesDao().loadCyclesMostRecent();
                        clearArrays(true);
                        sortMode = 1;
                        sortModeBO = 1;
                    });
                });

                sortNotRecent.setOnClickListener(v2 ->{
                    sortCheckmark.setY(110);
                    AsyncTask.execute(() -> {
                        cyclesList = cyclesDatabase.cyclesDao().loadCycleLeastRecent();
                        clearArrays(true);
                        sortMode = 2;
                        sortModeBO = 2;
                    });
                });

                sortHigh.setOnClickListener(v3 -> {
                    sortCheckmark.setY(206);
                    AsyncTask.execute(() -> {
                        cyclesList = cyclesDatabase.cyclesDao().loadCyclesMostItems();
                        clearArrays(true);
                        sortMode = 3;
                        sortModeBO = 3;
                    });
                });

                sortLow.setOnClickListener(v4 -> {
                    sortCheckmark.setY(302);
                    AsyncTask.execute(() -> {
                        cyclesList = cyclesDatabase.cyclesDao().loadCyclesLeastItems();
                        clearArrays(true);
                        sortMode = 4;
                        sortModeBO = 4;
                    });
                });
                sortCheckmark.setY(0);
            } else {
                Gson gson = new Gson();
                if ((!breaksOnly && startCustomSetTime.size()==0) || (breaksOnly && startBreaksOnlyTime.size()==0)) {
                    Toast.makeText(getApplicationContext(), "Nothing to save!", Toast.LENGTH_SHORT).show();;
                    return;
                }

                AsyncTask.execute(() -> {
                    queryCycles();
                    ArrayList<Long> tempSets = new ArrayList<>();
                    ArrayList<Long> tempBreaks = new ArrayList<>();
                    ArrayList<Long> tempBreaksOnly = new ArrayList<>();
                    String convertedSetList = "";
                    String convertedBreakList = "";
                    String convertedBreakOnlyList = "";

                    if (!breaksOnly) {
                        for (int i=0; i<startCustomSetTime.size(); i++) {
                            tempSets.add(startCustomSetTime.get(i) /1000);
                        }
                        for (int i=0; i<startCustomBreakTime.size(); i++){
                            tempBreaks.add(startCustomBreakTime.get(i)/1000);
                        }
                        convertedSetList = gson.toJson(tempSets);
                        convertedBreakList = gson.toJson(tempBreaks);

                        convertedSetList = convertedSetList.replace("\"", "");
                        convertedSetList = convertedSetList.replace("]", "");
                        convertedSetList = convertedSetList.replace("[", "");
                        convertedSetList = convertedSetList.replace(",", " - ");
                        convertedBreakList = convertedBreakList.replace("\"", "");
                        convertedBreakList = convertedBreakList.replace("]", "");
                        convertedBreakList = convertedBreakList.replace("[", "");
                        convertedBreakList = convertedBreakList.replace(",", " - ");
                        cycles.setSets(convertedSetList);
                        cycles.setBreaks(convertedBreakList);
                        cycles.setTimeAdded(System.currentTimeMillis());
                        cycles.setItemCount(startCustomSetTime.size());

                        boolean duplicate = false;
                        if (cyclesList.size() >0 && cyclesList.get(0).getSets()!=null) {
                            for (int i=0; i<cyclesList.size(); i++) {
                                if (cyclesList.get(i).getSets().equals(convertedSetList) && cyclesList.get(i).getBreaks().equals(convertedBreakList)) {
                                    duplicate = true;
                                    runOnUiThread(() -> {
                                        Toast.makeText(getApplicationContext(), "An identical cycle already exists!", Toast.LENGTH_SHORT).show();
                                    });
                                }
                            }
                            if (!duplicate) {
                                cyclesDatabase.cyclesDao().insertCycle(cycles);
                                runOnUiThread(() -> {
                                    Toast.makeText(getApplicationContext(), "Cycle added!", Toast.LENGTH_SHORT).show();
                                });
//                            queryCycles();
                            }
                        } else {
                            cyclesDatabase.cyclesDao().insertCycle(cycles);
                            runOnUiThread(() -> {
                                Toast.makeText(getApplicationContext(), "Cycle added!", Toast.LENGTH_SHORT).show();
                            });
                        }
                    } else {
                        for (int i=0; i<startBreaksOnlyTime.size(); i++) {
                            tempBreaksOnly.add(startBreaksOnlyTime.get(i)/1000);
                        }
                        convertedBreakOnlyList = gson.toJson(tempBreaksOnly);
                        convertedBreakOnlyList = convertedBreakOnlyList.replace("\"", "");
                        convertedBreakOnlyList = convertedBreakOnlyList.replace("]", "");
                        convertedBreakOnlyList = convertedBreakOnlyList.replace("[", "");
                        convertedBreakOnlyList = convertedBreakOnlyList.replace(",", " - ");
                        cyclesBO.setBreaksOnly(convertedBreakOnlyList);

                        boolean duplicate = false;
                        if (cyclesBOList.size()>0) {
                            int id = cyclesList.get(cyclesList.size()-1).getId();
                            cycles.setId(id+1);
                            for (int i=0; i<cyclesBOList.size(); i++) {
                                if (cyclesBOList.get(i).getBreaksOnly().equals(convertedBreakOnlyList)) {
                                    duplicate = true;
                                    runOnUiThread(() -> {
                                        Toast.makeText(getApplicationContext(), "An identical cycle already exists!", Toast.LENGTH_SHORT).show();
                                    });
                                }
                            }
                            if (!duplicate) {
                                cyclesDatabase.cyclesDao().insertBOCycle(cyclesBO);
                                runOnUiThread(() -> {
                                    Toast.makeText(getApplicationContext(), "Cycle added!", Toast.LENGTH_SHORT).show();
                                });
                            }
                        } else {
                            cyclesDatabase.cyclesDao().insertBOCycle(cyclesBO);
                            runOnUiThread(() -> {
                                Toast.makeText(getApplicationContext(), "Cycle added!", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                });
            }
        });

        pauseResumeButton.setOnClickListener(v-> {
            switch (mode) {
                case 1:
                    if (!customHalted) {
                        pauseAndResumeTimer(PAUSING_TIMER);
                    } else {
                        pauseAndResumeTimer(RESUMING_TIMER);
                    }
                    break;
                case 2:
                    if (!pomHalted) {
                        pauseAndResumeTimer(PAUSING_TIMER);
                    } else {
                        pauseAndResumeTimer(RESUMING_TIMER);
                    }
                    break;
                case 3:
                    pauseAndResumeTimer(0);
            }
        });

        cycle_reset.setOnClickListener(v -> {
            switch (mode) {
                case 1:
                    if (!breaksOnly) clearCycles(customCyclesDone); else clearCycles(breaksOnlyCyclesDone);
                    break;
                case 2:
                    clearCycles(pomCyclesDone);
                    break;
                case 3:
                    resetEntries = newEntries;
                    currentLapList.clear();
                    savedLapList.clear();
                    lapAdapter.notifyDataSetChanged();
                    lapsNumber = 0;
                    msReset = 0;
                    cycles_completed.setText(getString(R.string.laps_completed, String.valueOf(0)));
            }
        });

        skip.setOnClickListener(v -> {
            if (customSetTime.size()>0) {
                customHalted = true;
                int oldCycle = 0;
                int oldCycle2 = 0;
                if (mode == 1) {
                    if (timer != null) {
                        timer.cancel();
                    }
                    if (objectAnimator != null) {
                        objectAnimator.cancel();
                    }
                    if (numberOfSets >0 && numberOfBreaks >0) {
                        setBegun = false;
                        customProgressPause = maxProgress;
                        timePaused.setAlpha(1);
                        timeLeft.setAlpha(0);
                        progressBar.setProgress(10000);

                        if (!breaksOnly) {
                            if (customSetTime.size() >0 && customSetTime.size() == customBreakTime.size()) {
                                customSetTime.remove(customSetTime.size()-1);
                                numberOfSets--;
                            }
                            if (customBreakTime.size() >0 && customBreakTime.size() != customSetTime.size()) {
                                customBreakTime.remove(customBreakTime.size()-1);
                                numberOfBreaks--;
                                onBreak = false;
                                oldCycle = customCyclesDone;
                            }
                            if (customSetTime.size() >0) setNewText(timePaused, timePaused, (customSetTime.get(customSetTime.size()-1)+999)/1000);;
                        } else {
                            if (breaksOnlyTime.size() >0) {
                                breaksOnlyTime.remove(breaksOnlyTime.size()-1);
                                numberOfBreaks--;
                                oldCycle2 = breaksOnlyCyclesDone;
                                breakBegun = false;
                            }
                            if (breaksOnlyTime.size() >0) {
                                setNewText(timeLeft, timeLeft,(breaksOnlyTime.get(breaksOnlyTime.size() - 1) +999) / 1000);
                                setNewText(timePaused, timePaused, (breaksOnlyTime.get(breaksOnlyTime.size() - 1) +999) / 1000);
                            }
                        }
                    }
                    if (numberOfBreaks == 0) {
                        timePaused.setAlpha(0);
                        timeLeft.setAlpha(1);
                        if (!breaksOnly) {
                            if (oldCycle == customCyclesDone) {
                                customCyclesDone++;
                                cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));
                            }
                        } else {
                            if (oldCycle2 == breaksOnlyCyclesDone) {
                                breaksOnlyCyclesDone++;
                                cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(breaksOnlyCyclesDone)));
                            }
                        }
                        Log.i("cycles", "custom are " + customCyclesDone + " and breaksOnly are " + breaksOnlyCyclesDone);
                        endAnimation();
                        progressBar.setProgress(0);
                        timeLeft.setText("0");
                        timePaused.setText("0");
                        customTimerEnded = true;
                        paused = false;
                    }
                    if (numberOfBreaks >=0) {
                        if (!breaksOnly) {
                            dotDraws.breakTime(startCustomBreakTime);
                            dotDraws.setTime(startCustomSetTime);
                        } else{
                            dotDraws.breakTime(startBreaksOnlyTime);
                        }
                        dotDraws.newDraw(savedSets, savedBreaks, savedSets- (numberOfSets -1), savedBreaks- (numberOfBreaks-1), 0);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Cannot skip Pomodoro cycles!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        reset.setOnClickListener(v -> {
            reset.setVisibility(View.GONE);
            if (mode!=2) {
                resetTimer();
            } else {
                LayoutInflater inflater3 = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View view = inflater3.inflate(R.layout.pom_reset_popup, null);
                resetPopUpWindow  = new PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT, 75, false);
                resetPopUpWindow.showAtLocation(view, Gravity.CENTER, 0, 900);

                TextView confirm_reset = view.findViewById(R.id.pom_reset_text);
                confirm_reset.setText(R.string.pom_reset);
                confirm_reset.setOnClickListener(v2-> {
                    resetTimer();
                    resetPopUpWindow.dismiss();
                });

                cl.setOnClickListener(v2-> {
                    resetPopUpWindow.dismiss();
                    reset.setVisibility(View.VISIBLE);
                    save_cycles.setText(R.string.save_cycles);
                });
            }
        });

        newLap.setOnClickListener(v -> {
            double newSeconds = msReset/60;
            double newMinutes = newSeconds/60;

            double savedMinutes = 0;
            double savedSeconds = 0;
            double savedMs = 0;

            String[] holder = null;
            if (!stopwatchHalted) {
                if (savedLapList.size()>0) {
                    holder = (savedLapList.get(savedLapList.size()-1).split(":", 3));
                    savedMinutes = newMinutes + Integer.parseInt(holder[0]);
                    savedSeconds = newSeconds + Integer.parseInt(holder[1]);
                    savedMs = newMsConvert + Integer.parseInt(holder[2]);

                    if (savedMs>99) {
                        savedMs = savedMs-100;
                        savedSeconds +=1;
                    }
                    if (savedSeconds>99){
                        savedSeconds = savedSeconds-100;
                        savedMinutes +=1;
                    }
                    savedEntries = String.format(Locale.getDefault(), "%02d:%02d:%02d", (int) savedMinutes, (int) savedSeconds, (int) savedMs);
                } else {
                    savedEntries = String.format(Locale.getDefault(), "%02d:%02d:%02d", (int) minutes, (int) seconds, savedMsConvert);
                }

                newEntries = String.format(Locale.getDefault(), "%02d:%02d:%02d", (int) newMinutes, (int) newSeconds, newMsConvert);

                currentLapList.add(newEntries);
                savedLapList.add(savedEntries);
                lapLayout.scrollToPosition(savedLapList.size()-1);
                lapAdapter.notifyDataSetChanged();

                lapsNumber++;
                cycles_completed.setText(getString(R.string.laps_completed, String.valueOf(lapsNumber)));

                msReset = 0;
                msConvert2 = 0;
            }
        });
    }

    public void startObjectAnimator() {
        switch (mode) {
            case 1:
                fadeDone = 1;
                if (!onBreak) {
                    if (!setBegun) {
                        if (customSetTime.size()>0) setMillis = customSetTime.get(customSetTime.size() -1);
                        objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", (int) customProgressPause, 0);
                        objectAnimator.setInterpolator(new LinearInterpolator());
                        objectAnimator.setDuration(setMillis);
                        objectAnimator.start();
                    } else {
                        setMillis = setMillisUntilFinished;
                        if (objectAnimator!=null) objectAnimator.resume();
                    }
                } else {
                    fadeDone = 2;
                    if (!breakBegun) {
                        if (!breaksOnly) {
                            if (customBreakTime.size()>0) breakMillis = customBreakTime.get(customBreakTime.size() -1);
                        } else {
                            if (breaksOnlyTime.size()>0) breakMillis = breaksOnlyTime.get(breaksOnlyTime.size()-1);
                        }
                        objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", (int) customProgressPause, 0);
                        objectAnimator.setInterpolator(new LinearInterpolator());
                        objectAnimator.setDuration(breakMillis);
                        objectAnimator.start();
                        breakBegun = true;
                    } else {
                        breakMillis = breakMillisUntilFinished;
                        if (objectAnimator!=null) objectAnimator.resume();
                    }
                }
                break;
            case 2:
                fadeDone = 3;
                if (!pomBegun) {
                    objectAnimator2 = ObjectAnimator.ofInt(progressBar2, "progress", (int) pomProgressPause, 0);
                    objectAnimator2.setInterpolator(new LinearInterpolator());
                    objectAnimator2.setDuration(pomMillis);
                    objectAnimator2.start();
                    pomBegun = true;
                } else {
                    pomMillis = pomMillisUntilFinished;
                    if (objectAnimator2!=null) objectAnimator2.resume();
                }
                break;
        }
    }

    public void startTimer() {
        switch (mode) {
            case 1:
                setBegun = true;
                setNewText(timeLeft, timeLeft,(setMillis + 999)/1000);
                timer = new CountDownTimer(setMillis, 50) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        customProgressPause = (int) objectAnimator.getAnimatedValue();
                        setMillis = millisUntilFinished;

                        if (fadeCustomTimer) {
                            if (customAlpha<0.25) timeLeft.setAlpha(customAlpha+=0.04);
                            else if (customAlpha<0.5) timeLeft.setAlpha(customAlpha+=.08);
                            else if (customAlpha<1) timeLeft.setAlpha(customAlpha+=.12);
                            else if (customAlpha>=1) fadeCustomTimer = false;
                        }

                        timeLeft.setText(convertSeconds((setMillis + 999)/1000));
//                        setNewText(timeLeft, (setMillis + 999)/1000);
                        if (drawing) {
                            dotDraws.newDraw(savedSets, savedBreaks, savedSets- (numberOfSets -1), savedBreaks- (numberOfBreaks-1), 1);
                        }
                    }

                    @Override
                    public void onFinish() {
                        onBreak = true;
                        fadeDone = 0;
                        numberOfSets--;
                        timeLeft.setText("0");
                        customProgressPause = maxProgress;
                        if (customSetTime.size() > 0) {
                            customSetTime.remove(customSetTime.size() - 1);
                        }
                        if (customSetTime.size()>0) setMillis = customSetTime.get(customSetTime.size() -1);

                        setBegun = false;
                        customTimerEnded = false;
                        endAnimation();
                        mHandler.postDelayed((Runnable) () -> {
                            startObjectAnimator();
                            breakStart();
                            endAnimation.cancel();
                            timerDisabled = false;
                        },750);
                        dotDraws.setTime(startCustomSetTime);
                        dotDraws.breakTime(startCustomBreakTime);
                        fadeCustomTimer = false;
                    }
                }.start();
                break;

            case 2:
                pomBegun = true;
                timer2 = new CountDownTimer(pomMillis, 50) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        pomProgressPause = (int) objectAnimator2.getAnimatedValue();
                        pomMillis = millisUntilFinished;

//                        if (fadePomTimer) timeLeft2.setAlpha(pomAlpha+=0.1);
                        if (fadePomTimer) {
                            if (pomAlpha<0.25) timeLeft2.setAlpha(pomAlpha+=0.04);
                            else if (pomAlpha<0.5) timeLeft2.setAlpha(pomAlpha+=.08);
                            else if (pomAlpha<1) timeLeft2.setAlpha(pomAlpha+=.12);
                            else if (pomAlpha>=1) fadePomTimer = false;
                        }
                        if (pomAlpha >=1) fadePomTimer = false;

                        timeLeft2.setText(convertSeconds((pomMillis + 999)/1000));
//                        setNewText(timeLeft2, ((pomMillis + 999)/1000));
                        if (!drawing) {
                            dotDraws.pomDraw(pomDotCounter, 1);
                        }
                    }

                    @Override
                    public void onFinish() {
                        pomBegun = false;
                        numberOfSets--;
                        timeLeft2.setText("0");
                        pomProgressPause = maxProgress;

                        switch (pomDotCounter) {
                            case 2: case 4: case 6:
                                pomMillis = pomMillis2;
                                break;
                            case 7:
                                pomMillis = pomMillis3;
                        }
                        pomDotCounter++;
                        if (pomDotCounter <8) {
                            startObjectAnimator();
                        } else {
                            pomEnded = true;
                            pomDotCounter = 1;
                            pomCyclesDone +=1;
                            endAnimation();
                            cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(pomCyclesDone)));

                            endAnimation();
                            dotDraws.setTime(startCustomSetTime);
                            dotDraws.breakTime(startCustomBreakTime);
                        }
                    }
                }.start();
                break;
        }
    }

    public void breakStart() {
        timer = new CountDownTimer(breakMillis, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                customProgressPause = (int) objectAnimator.getAnimatedValue();
                breakMillis = millisUntilFinished;

                if (fadeCustomTimer) {
                    if (customAlpha<0.25) timeLeft.setAlpha(customAlpha+=0.04);
                    else if (customAlpha<0.5) timeLeft.setAlpha(customAlpha+=.08);
                    else timeLeft.setAlpha(customAlpha+=.12);
                }
                if (customAlpha >=1) fadeCustomTimer = false;

                timeLeft.setText(convertSeconds((millisUntilFinished +999) / 1000));
//                setNewText(timeLeft, (millisUntilFinished +999) / 1000);
                if (drawing) {
                    dotDraws.newDraw(savedSets, savedBreaks, savedSets- (numberOfSets -1), savedBreaks- (numberOfBreaks-1), 2);
                }
            }

            @Override
            public void onFinish() {
                breakBegun = false;
                numberOfBreaks--;
                timeLeft.setText("0");
                if (!breaksOnly) {
                    if (customBreakTime.size() >0) {
                        customBreakTime.remove(customBreakTime.size()-1);
                    }
                    if (customBreakTime.size()>0) breakMillis = customBreakTime.get(customBreakTime.size() -1);
                    dotDraws.setTime(startCustomSetTime);
                    dotDraws.breakTime(startCustomBreakTime);
                } else {
                    if (breaksOnlyTime.size()>0) {
                        breaksOnlyTime.remove(breaksOnlyTime.size()-1);
                    }
                    if (breaksOnlyTime.size()>0) breakMillis = breaksOnlyTime.get(breaksOnlyTime.size() -1);
                    dotDraws.setTime(startBreaksOnlyTime);
                    dotDraws.breakTime(startBreaksOnlyTime);
                }

                endAnimation();
                if (numberOfBreaks >0) {
                    customProgressPause = maxProgress;
                    customTimerEnded = false;

                    mHandler.postDelayed(() -> {
                        startObjectAnimator();
                        if (!breaksOnly) {
                            startTimer();
                            onBreak = false;
                        } else {
                            breakStart();
                        }
                        endAnimation.cancel();
                        timerDisabled = false;
                    },750);
                } else {
                    if (!breaksOnly) {
                        customCyclesDone++;
                        cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));
                    } else {
                        breaksOnlyCyclesDone++;
                        cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(breaksOnlyCyclesDone)));
                    }
                    endAnimation();
                    dotDraws.newDraw(savedSets, savedBreaks, savedSets- (numberOfSets -1), savedBreaks- (numberOfBreaks-1), 0);
                    customTimerEnded = true;

                    setMillis = setStart;
                    breakMillis = breakStart;
                    timerDisabled = false;
                    fadeCustomTimer = false;
                }

//                    if (Build.VERSION.SDK_INT >= 26) {
//                        vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
//                    } else {
//                        vibrator.vibrate(pattern1, 0);
//                    }
            }
        }.start();
    }

    public void setIncrements(MotionEvent event, Runnable runnable) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //Handler must not be instantiated before this, otherwise the runnable will execute it on every touch (i.e. even on "action_up" removal.
                mHandler = new Handler();
                mHandler.postDelayed(runnable,50);
                mHandler.postDelayed(valueSpeed, 50);
                break;
            case MotionEvent.ACTION_UP:
                mHandler.removeCallbacksAndMessages(null);
                incrementTimer = 10;
        }
        if (setValue<5) setValue = 5; if (breakValue<5) breakValue = 5; if (breaksOnlyValue <5) breaksOnlyValue = 5;
        if (setValue>300) setValue = 300; if (breakValue>300) breakValue =300; if (breaksOnlyValue>300) breaksOnlyValue = 300;
        if (pomValue1<10) pomValue1 = 10; if (pomValue1>120) pomValue1 = 120;
        if (pomValue2<1) pomValue2 = 1; if (pomValue2>10) pomValue2 = 10;
        if (pomValue3<10) pomValue3 = 10; if (pomValue3>60) pomValue3 = 60;
    }

    private String convertSeconds(long totalSeconds) {
        DecimalFormat df = new DecimalFormat("00");
        long minutes;
        long remainingSeconds;

        if (totalSeconds >=60) {
            minutes = totalSeconds/60;
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
        if (seconds>=60) {
            minutes = seconds/60;
            roundedSeconds = seconds % 60;
            if (minutes>=10 && timeLeft3.getTextSize() != 70f) timeLeft3.setTextSize(70f);
            return (df.format(minutes) + ":" + df2.format(roundedSeconds));
        } else {
            if (timeLeft3.getTextSize() != 90f) timeLeft3.setTextSize(90f);
            return df.format(seconds);
        }
    }

    public void adjustCustom(boolean adding) {
        if (adding) {
            if (!breaksOnly) {
                if (customSetTime.size() < 10 && customSetTime.size() >= 0) {
                    setValue = Long.parseLong(first_value_text.getText().toString());
                    customSetTime.add(setValue * 1000);
                    startCustomSetTime.add(setValue * 1000);
//                    blankCount+=1;
//                    dotDraws.drawBlanks(blankCount);
//                    ArrayList<Long> testArray = new ArrayList<>();
//                    dotDraws.setTime(testArray);
                } else {
                    Toast.makeText(getApplicationContext(), "Max rounds reached!", Toast.LENGTH_SHORT).show();
                }
                if (customBreakTime.size() < 10 && customBreakTime.size() >= 0) {
                    breakValue = Long.parseLong(second_value_text.getText().toString());
                    customBreakTime.add(breakValue * 1000);
                    startCustomBreakTime.add(breakValue * 1000);
                }
            } else {
                if (breaksOnlyTime.size() < 10 && breaksOnlyTime.size() >= 0) {
                    breaksOnlyValue = Long.parseLong(second_value_text.getText().toString());
                    breaksOnlyTime.add(breaksOnlyValue * 1000);
                    startBreaksOnlyTime.add(breaksOnlyValue * 1000);
                } else {
                    Toast.makeText(getApplicationContext(), "Max rounds reached!", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            if (!breaksOnly) {
                if (customSetTime.size() > 0 && startCustomSetTime.size() > 0) {
                    customSetTime.remove(customSetTime.size() - 1);
                    startCustomSetTime.remove(startCustomSetTime.size() - 1);
                } else {
                    Toast.makeText(getApplicationContext(), "Nothing left to remove!", Toast.LENGTH_SHORT).show();
                }
                if (customBreakTime.size() > 0 && startCustomBreakTime.size() > 0) {
                    customBreakTime.remove(customBreakTime.size() - 1);
                    startCustomBreakTime.remove(startCustomBreakTime.size() - 1);
                }
            } else {
                if (breaksOnlyTime.size() > 0 && startBreaksOnlyTime.size() > 0) {
                    breaksOnlyTime.remove(breaksOnlyTime.size() - 1);
                    startBreaksOnlyTime.remove(startBreaksOnlyTime.size() - 1);
                } else {
                    Toast.makeText(getApplicationContext(), "Nothing left to remove!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        resetTimer();
        dotDraws.newDraw(savedSets, savedBreaks, 0, 0, 0);

        if (!breaksOnly) {
            if (customSetTime.size()>0) {
                setNewText(timePaused, timePaused, (customSetTime.get(customSetTime.size() - 1) +999) / 1000);
            } else {
                timePaused.setText("?");
            }
        } else {
            if (breaksOnlyTime.size()>0) {
                setNewText(timePaused, timePaused2, (breaksOnlyTime.get(breaksOnlyTime.size() - 1) +999) / 1000);
            } else {
                timePaused.setText("?");
            }
        }
        if (!breaksOnly) third_value_text.setText(String.valueOf(customSetTime.size())); else third_value_text.setText(String.valueOf(breaksOnlyTime.size()));
    }

    private void endAnimation() {
        endAnimation = new AlphaAnimation(1.0f, 0.0f);
        endAnimation.setDuration(300);
        endAnimation.setStartOffset(20);
        endAnimation.setRepeatMode(Animation.REVERSE);
        endAnimation.setRepeatCount(Animation.INFINITE);

        progressBar.setAnimation(endAnimation);
        timeLeft.setAnimation(endAnimation);
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

    public void setNewText(TextView oldTextView, TextView currentTextView, long newTime) {
        boolean fadeTime = false;
        String oldText = (String) oldTextView.getText();
        if (!oldText.equals("") && !oldText.equals("?")) {
            oldText = oldText.replace(":", "");
            long oldTime = Long.parseLong(oldText);
            if (oldTime<60 && newTime>=60) {
                fadeTime = true;
                switch (mode) {
                    case 1:
                        changeTextSize(valueAnimatorDown, timeLeft, timePaused); break;
                    case 2:
                        changeTextSize(valueAnimatorDown, timeLeft2, timePaused2); break;
                    case 3:
                        changeTextSize(valueAnimatorDown, timeLeft3,  timePaused3);
                }
            } else if (oldTime>=60 && newTime<60) {
                fadeTime = true;
                switch (mode) {
                    case 1:
                        changeTextSize(valueAnimatorUp, timeLeft, timePaused); break;
                    case 2:
                        changeTextSize(valueAnimatorUp, timeLeft2, timePaused2); break;
                    case 3:
                        changeTextSize(valueAnimatorUp, timeLeft3,  timePaused3);
                }
            }
        }
        if (fadeTime) {
            switch (mode) {
                case 1:
                    if (customHalted) fadeTextIn(timePaused); else fadeTextIn(timeLeft); break;
                case 2:
                    if (pomHalted) fadeTextIn(timePaused2); else fadeTextIn(timeLeft2); break;
                case 3:
                    if (stopwatchHalted) fadeTextIn(timePaused3); else fadeTextIn(timeLeft3);
            }
        }
        currentTextView.setText(convertSeconds(newTime));
    }

    public void fadeTextIn(TextView textView) {
        if (fadeInObj!=null) fadeInObj.cancel();
        fadeInObj = ObjectAnimator.ofFloat(textView, "alpha", 0.0f, 1.0f);
        fadeInObj.setDuration(1000);
        fadeInObj.start();
    }

    public void fadeOutText(TextView textView) {
        if (fadeOutObj!=null) fadeOutObj.cancel();
        fadeOutObj = ObjectAnimator.ofFloat(textView, "alpha", 1.0f, 0.0f);
        fadeOutObj.setDuration(750);
        fadeOutObj.start();
    }

    public void switchTimer(int mode, boolean halted) {
        removeViews();
        tabViews();
        switch (mode) {
            case 1:
                drawing = true;
                if (!breaksOnly) {
                    onBreak = false;
                    cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));
                } else {
                    onBreak = true;
                    cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(breaksOnlyCyclesDone)));
                }

                if (setMillisUntilFinished==0) setMillisUntilFinished = setMillis;
                if (breakMillisUntilFinished==0) breakMillisUntilFinished = breakMillis;
                if (halted) {
                    if (!breaksOnly) {
                        setNewText(lastTextView, timePaused, (setMillis + 999)/1000);
                    } else {
                        setNewText(lastTextView, timePaused, (breakMillis + 999)/1000);
                    }
                    fadeTextIn(timePaused);
                    dotDraws.newDraw(savedSets, savedBreaks, savedSets- (numberOfSets -1), savedBreaks- (numberOfBreaks-1), 1);
                } else {
                    if (!breaksOnly) {
                        setNewText(lastTextView, timeLeft, (setMillis + 999)/1000);
                    } else {
                        setNewText(lastTextView, timeLeft, (breakMillis + 999)/1000);
                    }
                    customAlpha = 0;
                    fadeCustomTimer = true;
                    startObjectAnimator();
                }
                break;
            case 2:
                drawing = false;
                cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(pomCyclesDone)));

                if (pomMillisUntilFinished==0) pomMillisUntilFinished = pomMillis;
                if (halted) {
                    fadeTextIn(timePaused2);
                    dotDraws.pomDraw(pomDotCounter, 1);
                    setNewText(lastTextView, timePaused2, (pomMillis + 999)/1000);
                } else {
                    pomAlpha = 0;
                    fadePomTimer = true;
                    startObjectAnimator();
                    setNewText(lastTextView, timeLeft2, (pomMillis + 999)/1000);
                }
                break;
            case 3:
                //Same animation instance can't be used simultaneously for both TextViews.
                cycles_completed.setText(getString(R.string.laps_completed, String.valueOf(lapsNumber)));
                dotDraws.setMode(3);
                dotDraws.pomDraw(pomDotCounter, 1);
                if (stopwatchHalted) {
                    fadeTextIn(timePaused3);
                } else {
                    fadeTextIn(timeLeft3);
                }
        }
        dotDraws.retrieveAlpha();
    }

    public void pauseAndResumeTimer(int pausing) {
        if (setMillis <=500 || breakMillis <=500) {
            timerDisabled = true;
        }
        if (!timerDisabled) {
            removeViews();
            if (fadeInObj!=null) fadeInObj.cancel();
            if (fadeOutObj!=null) fadeOutObj.cancel();
            switch (mode) {
                case 1:
                    if (!customTimerEnded) {
                        if (pausing == PAUSING_TIMER) {
                            timePaused.setAlpha(1);
                            String pausedTime = "";
                            if (timer!=null) timer.cancel();
                            if (objectAnimator!=null) objectAnimator.pause();
                            customHalted = true;

                            if (!onBreak) {
                                setMillisUntilFinished = setMillis;
                                pausedTime = (convertSeconds((setMillisUntilFinished + 999)/1000));
                            } else {
                                breakMillisUntilFinished = breakMillis;
                                pausedTime = (convertSeconds((breakMillisUntilFinished + 999)/1000));
                            }
                            reset.setVisibility(View.VISIBLE);
                            timePaused.setText(pausedTime);
                        } else if (pausing == RESUMING_TIMER){
                            timeLeft.setAlpha(1);
                            customHalted = false;
                            startObjectAnimator();
                            if (onBreak) {
                                breakStart();
                            } else {
                                startTimer();
                            }
                            reset.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        resetTimer();
                        if (endAnimation != null) endAnimation.cancel();
                    }
                    break;
                case 2:
                    if (!pomTimerEnded) {
                        if (pausing == PAUSING_TIMER) {
                            timePaused2.setAlpha(1);
                            pomHalted = true;
                            pomMillisUntilFinished = pomMillis;
                            if (objectAnimator2!=null) objectAnimator2.pause();
                            if (timer2!=null) timer2.cancel();;
                            String pausedTime2 = (convertSeconds((pomMillisUntilFinished + 999)/1000));
                            timePaused2.setText(pausedTime2);
                            reset.setVisibility(View.VISIBLE);
                        } else if (pausing == RESUMING_TIMER){
                            timeLeft2.setAlpha(1);
                            pomHalted = false;
                            startObjectAnimator();
                            startTimer();
                            reset.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        resetTimer();
                        if (endAnimation != null) endAnimation.cancel();
                        reset.setVisibility(View.INVISIBLE);
                    }
                    break;
                case 3:
                    DecimalFormat df2 = new DecimalFormat("00");
                    if (fadeInObj!=null) fadeInObj.cancel();
                    if (stopwatchHalted) {
                        timeLeft3.setAlpha(1);
                        timePaused3.setAlpha(0);
                        msTime.setAlpha(1);
                        msTimePaused.setAlpha(0);
                        stopWatchRunnable = new Runnable() {
                            @Override
                            public void run() {
                                //ms can never be more than 60/sec due to refresh rate.
                                ms+=1;
                                msReset +=1;
                                msConvert+=1;
                                msConvert2+=1;
                                msDisplay+=1;
                                if (msConvert>59) msConvert=0;
                                if (msConvert2>59) msConvert2=0;
                                if (msDisplay>59) msDisplay=0;

                                seconds = ms/60;
                                minutes = seconds/60;

                                newMs = df2.format((msConvert2/60) * 100);
                                savedMs = df2.format((msConvert/60) * 100);
                                newMsConvert = Integer.parseInt(newMs);
                                savedMsConvert = Integer.parseInt(savedMs);

                                //Conversion to long solves +30 ms delay for display.
                                displayMs = df2.format((msDisplay/60) * 100);
                                displayTime = convertStopwatch((long) seconds);

                                timeLeft3.setText(displayTime);
                                msTime.setText(displayMs);
                                mHandler.postDelayed(this, 10);
                            }
                        };
                        mHandler.post(stopWatchRunnable);
                        stopwatchHalted = false;
                        reset.setVisibility(View.INVISIBLE);

                    } else {
                        timeLeft3.setAlpha(0);
                        timePaused3.setAlpha(1);
                        msTime.setAlpha(0);
                        msTimePaused.setAlpha(1);
                        timePaused3.setText(timeLeft3.getText());
                        msTimePaused.setText(msTime.getText());
                        mHandler.removeCallbacksAndMessages(null);
                        stopwatchHalted = true;
                        reset.setVisibility(View.VISIBLE);
                    }
            }
        } else if ( (!breaksOnly && customSetTime.size()==0) || (breaksOnly && breaksOnlyTime.size()==0)) {
            Toast.makeText(getApplicationContext(), "What are we timing?", Toast.LENGTH_SHORT).show();
        }
    }

    public void clearCycles(int cycleCount) {
        if (cycleCount>0) {
            cycle_reset.setVisibility(View.GONE);
            LayoutInflater inflater2 = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = inflater2.inflate(R.layout.pom_reset_popup, null);
            cyclePopupWindow  = new PopupWindow(view, 150, WindowManager.LayoutParams.WRAP_CONTENT, false);
            cyclePopupWindow.showAtLocation(view, Gravity.CENTER, 400, 465);

            TextView confirm_reset = view.findViewById(R.id.pom_reset_text);
            confirm_reset.setGravity(Gravity.CENTER_HORIZONTAL);
            confirm_reset.setText(R.string.pom_cycle_reset);

            cl.setOnClickListener(v2-> {
                cyclePopupWindow.dismiss();
                cycle_reset.setVisibility(View.VISIBLE);
                save_cycles.setText(R.string.save_cycles);
            });

            confirm_reset.setOnClickListener(v2-> {
                if (mode==1) {
                    if (!breaksOnly) customCyclesDone = 0; else breaksOnlyCyclesDone = 0;
                } else if (mode==2) pomCyclesDone = 0;
                cycle_reset.setVisibility(View.VISIBLE);
                cycles_completed.setText(getString(R.string.cycles_done, "0"));
                cyclePopupWindow.dismiss();;
            });
        }
    }

    public void removeViews() {
        timePaused.setAlpha(0);
        timeLeft.setAlpha(0);
        timePaused2.setAlpha(0);
        timeLeft2.setAlpha(0);
        timeLeft3.setAlpha(0);
        timePaused3.setAlpha(0);
        msTime.setAlpha(0);
        msTimePaused.setAlpha(0);
    }

    public void tabViews(){
        ConstraintLayout.LayoutParams params2 = (ConstraintLayout.LayoutParams) cycle_reset.getLayoutParams();
        switch (mode) {
            case 1:
                progressBar.setVisibility(View.VISIBLE);
                progressBar2.setVisibility(View.INVISIBLE);
                stopWatchView.setVisibility(View.GONE);
                plus_third_value.setVisibility(View.VISIBLE);
                minus_third_value.setVisibility(View.VISIBLE);
                if (breaksOnly) no_set_header.setVisibility(View.VISIBLE);
                s2.setVisibility(View.VISIBLE);
                s3.setVisibility(View.VISIBLE);
                if (!breaksOnly) {
                    s1.setVisibility(View.VISIBLE);
                    plus_first_value.setVisibility(View.VISIBLE);
                    minus_first_value.setVisibility(View.VISIBLE);
                    no_set_header.setVisibility(View.GONE);
                    first_value_text.setText(convertSeconds(setValue));
                    second_value_text.setText(convertSeconds(breakValue));
                    third_value_text.setText(String.valueOf(startCustomSetTime.size()));
                } else {
                    s1.setVisibility(View.INVISIBLE);
                    plus_first_value.setVisibility(View.INVISIBLE);
                    minus_first_value.setVisibility(View.INVISIBLE);
                    no_set_header.setVisibility(View.VISIBLE);
                    second_value_text.setText(convertSeconds(breaksOnlyValue));
                    third_value_text.setText(String.valueOf(startBreaksOnlyTime.size()));
                }
                plus_second_value.setVisibility(View.VISIBLE);
                minus_second_value.setVisibility(View.VISIBLE);
                plus_third_value.setVisibility(View.VISIBLE);
                minus_third_value.setVisibility(View.VISIBLE);
                skip.setVisibility(View.VISIBLE);
                newLap.setVisibility(View.GONE);
                s1.setText(R.string.set_time);
                s2.setText(R.string.break_time);
                s3.setText(R.string.set_number);
                cycle_reset.setText(R.string.clear_cycles);
                params2.width = 150;
                first_value_text.setFilters(new InputFilter[]{
                        new NumberFilter(5, 300)
                });
                second_value_text.setFilters(new InputFilter[]{
                        new NumberFilter(5, 300)
                });
                break;
            case 2:
                progressBar.setVisibility(View.INVISIBLE);
                progressBar2.setVisibility(View.VISIBLE);
                stopWatchView.setVisibility(View.GONE);
                no_set_header.setVisibility(View.GONE);
                s1.setVisibility(View.VISIBLE);
                s2.setVisibility(View.VISIBLE);
                s3.setVisibility(View.VISIBLE);
                plus_first_value.setVisibility(View.VISIBLE);
                minus_first_value.setVisibility(View.VISIBLE);
                plus_second_value.setVisibility(View.VISIBLE);
                minus_second_value.setVisibility(View.VISIBLE);
                plus_third_value.setVisibility(View.VISIBLE);
                minus_third_value.setVisibility(View.VISIBLE);
                first_value_text.setText(convertSeconds(pomValue1));
                second_value_text.setText(convertSeconds(pomValue2));
                third_value_text.setText(convertSeconds(pomValue3));
                skip.setVisibility(View.VISIBLE);
                newLap.setVisibility(View.GONE);
                s1.setText(R.string.work_time);
                s2.setText(R.string.small_break);
                s3.setText(R.string.long_break);
                cycle_reset.setText(R.string.clear_cycles);
                params2.width = 150;
                first_value_text.setFilters(new InputFilter[]{
                        new NumberFilter(10, 120)
                });
                second_value_text.setFilters(new InputFilter[]{
                        new NumberFilter(1, 10)
                });
                third_value_text.setFilters(new InputFilter[]{
                        new NumberFilter(10, 60)
                });
                break;
            case 3:
                progressBar.setVisibility(View.INVISIBLE);
                progressBar2.setVisibility(View.INVISIBLE);
                stopWatchView.setVisibility(View.VISIBLE);
                plus_third_value.setVisibility(View.GONE);
                no_set_header.setVisibility(View.GONE);
                s1.setVisibility(View.GONE);
                s2.setVisibility(View.GONE);
                s3.setVisibility(View.GONE);
                plus_first_value.setVisibility(View.GONE);
                minus_first_value.setVisibility(View.GONE);
                plus_second_value.setVisibility(View.GONE);
                minus_second_value.setVisibility(View.GONE);
                plus_third_value.setVisibility(View.GONE);
                minus_third_value.setVisibility(View.GONE);
                skip.setVisibility(View.GONE);
                newLap.setVisibility(View.VISIBLE);
                cycle_reset.setText(R.string.clear_laps);
                params2.width = 200;
                msTime.setAlpha(1);
                msTimePaused.setAlpha(1);

                timeLeft3.setText(displayTime);
                timePaused3.setText(displayTime);
                msTime.setText(displayMs);
                msTimePaused.setText(displayMs);
        }
        cycle_reset.setLayoutParams(params2);
    }

    public void clearArrays(boolean populateList) {
        if (setsArray!=null) setsArray.clear();
        if (breaksArray!=null) breaksArray.clear();
        if (breaksOnlyArray!=null) breaksOnlyArray.clear();

        if (populateList) {
            if (!breaksOnly) {
                for (int i=0; i<cyclesList.size(); i++) {
                    setsArray.add(cyclesList.get(i).getSets());
                    breaksArray.add(cyclesList.get(i).getBreaks());
                }
            } else {
                for (int i=0; i<breaksOnlyArray.size(); i++) {
                    breaksOnlyArray.add(cyclesBOList.get(i).getBreaksOnly());
                }
            }
            runOnUiThread(() -> {
                savedCycleAdapter.notifyDataSetChanged();
            });
        }
    }

    public void queryCycles() {
        if (!breaksOnly) {
            switch (sortMode) {
                case 1:
                    cyclesList = cyclesDatabase.cyclesDao().loadCyclesMostRecent(); sortCheckmark.setY(14); break;
                case 2:
                    cyclesList = cyclesDatabase.cyclesDao().loadCycleLeastRecent(); sortCheckmark.setY(110); break;
                case 3:
                    cyclesList = cyclesDatabase.cyclesDao().loadCyclesMostItems(); sortCheckmark.setY(206); break;
                case 4:
                    cyclesList = cyclesDatabase.cyclesDao().loadCyclesLeastItems(); sortCheckmark.setY(302);  break;
            }
        } else {
            switch (sortModeBO) {
                case 1:
                    cyclesBOList = cyclesDatabase.cyclesDao().loadCyclesMostRecentBO(); sortCheckmark.setY(14); break;
                case 2:
                    cyclesBOList = cyclesDatabase.cyclesDao().loadCycleLeastRecentBO(); sortCheckmark.setY(110); break;
                case 3:
                    cyclesBOList = cyclesDatabase.cyclesDao().loadCyclesMostItemsBO(); sortCheckmark.setY(206); break;
                case 4:
                    cyclesBOList = cyclesDatabase.cyclesDao().loadCyclesLeastItemsBO(); sortCheckmark.setY(302);
            }
        }
    }

    public void resetTimer() {
        removeViews();
        //Todo: Separate end animations, especially since adding stopwatch.
        if (endAnimation != null) endAnimation.cancel();
        switch (mode) {
            case 1:
                timePaused.setAlpha(1);
                progressBar.setProgress(10000);
                customProgressPause = maxProgress;
                customTimerEnded = false;
                setBegun = false;
                breakBegun = false;
                customHalted = true;
                if (!breaksOnly) onBreak = false; else onBreak =true;
                if (timer != null) timer.cancel();
                if (objectAnimator != null) objectAnimator.cancel();

                if (startCustomSetTime.size()>0) {
                    setMillis = startCustomSetTime.get(startCustomSetTime.size()-1);
                    setNewText(timePaused, timePaused, (setMillis+999)/1000);
                    setNewText(timeLeft, timeLeft, (setMillis+999)/1000);
                }
                if (!breaksOnly) {
                    if (startCustomBreakTime.size()>0) {
                        breakMillis = startCustomBreakTime.get(startCustomBreakTime.size()-1);
                        setNewText(timePaused, timePaused,  (setMillis+999)/1000);
                        setNewText(timeLeft, timeLeft, (setMillis+999)/1000);
                    }
                } else {
                    if (startBreaksOnlyTime.size()>0) {
                        breakMillis = startBreaksOnlyTime.get(startBreaksOnlyTime.size()-1);
                        setNewText(timePaused, timePaused,  (breakMillis+999)/1000);
                        setNewText(timeLeft, timeLeft, (breakMillis+999)/1000);
                    }
                }
                numberOfSets = startCustomSetTime.size();
                if (!breaksOnly) numberOfBreaks = startCustomBreakTime.size(); else numberOfBreaks = startBreaksOnlyTime.size();
                savedSets = numberOfSets;
                savedBreaks = numberOfBreaks;

                if (!breaksOnly) {
                    customSetTime = new ArrayList<>();
                    customBreakTime = new ArrayList<>();
                } else {
                    breaksOnlyTime = new ArrayList<>();
                }

                if (!breaksOnly) {
                    if (startCustomSetTime.size() >0) {
                        for (int i=0; i<startCustomSetTime.size(); i++) {
                            customSetTime.add(startCustomSetTime.get(i));
                            customBreakTime.add(startCustomBreakTime.get(i));
                        }
                        timerDisabled = false;
                    } else {
                        timerDisabled = true;
                    }
                    dotDraws.setTime(customSetTime);
                    dotDraws.breakTime(customBreakTime);
                    third_value_text.setText(String.valueOf(customSetTime.size()));
                } else {
                    if (startBreaksOnlyTime.size()>0) {
                        breaksOnlyTime.addAll(startBreaksOnlyTime);
                        timerDisabled = false;
                    } else {
                        timerDisabled = true;
                    }
                    dotDraws.breakTime(breaksOnlyTime);
                    third_value_text.setText(String.valueOf(breaksOnlyTime.size()));
                }
                dotDraws.setAlpha();
                dotDraws.newDraw(savedSets, savedBreaks, 0, 0, 0);
                break;
            case 2:
                pomTimerEnded = false;
                progressBar2.setProgress(10000);
                pomBegun = false;
                pomProgressPause = maxProgress;
                if (timer2 != null) timer2.cancel();
                if (objectAnimator2 != null) objectAnimator2.cancel();
                pomMillis = pomMillis1;
                setNewText(timeLeft2, timeLeft2, (pomMillis + 999)/1000);
                setNewText(timePaused2, timePaused2, (pomMillis + 999)/1000);
                timePaused2.setAlpha(1);
                onBreak = false;
                pomHalted = true;
                pomProgressPause = maxProgress;
                dotDraws.pomDraw(1, 0);
                break;
            case 3:
                stopwatchHalted = true;
                ms = 0;
                msConvert = 0;
                msConvert2 = 0;
                msDisplay = 0;
                msReset = 0;
                seconds = 0;
                minutes = 0;
                lapsNumber = 0;
                timeLeft3.setAlpha(1);
                msTime.setAlpha(1);
                timeLeft3.setText("0");
                msTime.setText("00");
                cycles_completed.setText(getString(R.string.laps_completed, String.valueOf(0)));
                if (currentLapList.size()>0) currentLapList.clear();
                if (savedLapList.size()>0) savedLapList.clear();
                lapAdapter.notifyDataSetChanged();
                break;
        }
    }
}