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
import android.content.Context;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tragic.irate.simple.stopwatch.Database.CyclesDatabase;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SavedCycleAdapter.onCycleClickListener{

    Button breaks_only;
    TextView save_cycles;
    ProgressBar progressBar;
    ProgressBar progressBar2;
    ImageView stopWatchButton;
    TextView timeLeft;
    TextView timeLeft2;
    TextView timePaused;
    TextView timePaused2;
    TextView timeLeft3;
    TextView timePaused3;
    TextView secondsLeft;
    TextView msTime;
    TextView msTimePaused;
    CountDownTimer timer;
    CountDownTimer timer2;
    TextView reset;
    ObjectAnimator objectAnimator;
    ObjectAnimator objectAnimator2;
    Animation endAnimation;
    Handler handler;

    TextView s1;
    TextView s2;
    TextView s3;
    TextView blank_spinner;
    TextView cycles_completed;
    TextView cycle_reset;
    TextView skip;
    TextView newLap;
    Spinner spinner1;
    Spinner spinner2;
    Spinner spinner3;
    ImageButton plus_sign;
    ImageButton minus_sign;

    List<Long> spinList1;
    List<Long> spinList2;
    List<Long> spinList3;
    List<String> spinListString1;
    List<String> spinListString2;
    List<String> spinListString3;

    ArrayAdapter<String> spinAdapter1;
    ArrayAdapter<String> spinAdapter2;
    ArrayAdapter<String> spinAdapter3;
    ArrayAdapter<Long> pomAdapter1;
    ArrayAdapter<Long> pomAdapter2;
    ArrayAdapter<Long> pomAdapter3;
    ArrayAdapter<String> dotSpinAdapter;

    List<Integer> modeOneSpins;
    List<Integer> modeOneBreakOnlySpins;
    List<Integer> modeTwoSpins;

    int PAUSING_TIMER = 1;
    int RESUMING_TIMER = 2;

    long breakMillis;
    int breakStart;
    long setMillis;
    int setStart;
    int maxProgress = 10000;
    int customProgressPause = 10000;
    int breaksOnlyPause = 10000;
    int pomProgressPause = 10000;
    long pomMillis;
    long pos;
    long setMillisUntilFinished;
    long breakMillisUntilFinished;
    long pomMillisUntilFinished;

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
    int newMsConvert;
    int savedMsConvert;
    ArrayList<String> currentLapList;
    ArrayList<String> savedLapList;

    int customCyclesDone;
    int breaksOnlyCyclesDone;
    int pomCyclesDone;
    int lapsDone;

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
    ValueAnimator valueAnimatorDown;
    ValueAnimator valueAnimatorUp;

    ArrayList<Long> customSetTime;
    ArrayList<Long> customBreakTime;
    ArrayList<Long> startCustomSetTime;
    ArrayList<Long> startCustomBreakTime;
    ArrayList<Long> startBreaksOnlyTime;
    ArrayList<Long> breaksOnlyTime;

    boolean setBegun;
    boolean breakBegun;
    boolean pomBegun;
    boolean drawing = true;
    boolean breaksOnly;

    PopupWindow cyclePopupWindow;
    PopupWindow resetPopUpWindow;
    PopupWindow savedCyclePopupWindow;

    boolean fadeCustomTimer;
    boolean fadePomTimer;
    float customAlpha;
    float pomAlpha;
    ObjectAnimator fadeInObj;
    ObjectAnimator fadeOutObj;
    RecyclerView lapRecycler;
    LapAdapter lapAdapter;

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
    DotDraws savedDraws;
    boolean disableSavedPopup;

    //Todo: Add update for retrieved row?
    //Todo: Manage save for sets/breaks in progress and w/ 0 sets/breaks in list.
    //Todo: Add fade in/out to breaksOnly.
    //Todo: Reduce font for larger timer numbers in Custom mode.
    //Todo: Smaller click radius for progressBar - it uses square as shape w/ circle drawn within.
    //Todo: Add taskbar notification for timers.
    //Todo: Add color scheme options.
    //Todo: All DB calls in aSync.
    //Todo: Rename app, of course.
    //Todo: Add onOptionsSelected dots for About, etc.

    @Override
    public void onCycleClick(int position) {
        AsyncTask.execute(() -> {
            disableSavedPopup = false;
            customSetTime.clear();
            startCustomSetTime.clear();
            customBreakTime.clear();
            startCustomBreakTime.clear();

            if (!breaksOnly) {
                cyclesList = cyclesDatabase.cyclesDao().loadAllCycles();
                String tempSets = cyclesList.get(position).getSets();
                String tempBreaks = cyclesList.get(position).getBreaks();
                String[] setSplit = tempSets.split(" - ", 0);
                String[] breakSplit = tempBreaks.split(" - ", 0);

                for (int i=0; i<setSplit.length; i++) {
                    customSetTime.add(Long.parseLong(setSplit[i])*1000);
                    startCustomSetTime.add(Long.parseLong(setSplit[i])*1000);
                    customBreakTime.add(Long.parseLong(breakSplit[i])*1000);
                    startCustomBreakTime.add(Long.parseLong(breakSplit[i])*1000);
                }
            } else {
                cyclesBOList = cyclesDatabase.cyclesDao().loadAllBOCycles();
                String tempBreaksOnly = cyclesBOList.get(position).getBreaksOnly();
                String[] breaksOnlySplit = tempBreaksOnly.split(" - ", 0);

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
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saved_cycle_list:
                if (!disableSavedPopup) {
                    AsyncTask.execute(() -> {
                        disableSavedPopup = true;
                        cyclesList = cyclesDatabase.cyclesDao().loadAllCycles();
                        cyclesBOList = cyclesDatabase.cyclesDao().loadAllBOCycles();
                        if (setsArray!=null) setsArray.clear();
                        if (breaksArray!=null) breaksArray.clear();

                        String[] tempSets = null;
                        String[] tempBreaks = null;
                        String[] tempBreaksOnly = null;
                        ArrayList<Integer> setCount = new ArrayList<>();
                        ArrayList<Integer> breakCount = new ArrayList<>();
                        ArrayList<Integer> breaksOnlyCount = new ArrayList<>();

                        if (!breaksOnly) {
                            int columnSize = 0;
                            for (int i=0; i<cyclesList.size(); i++) {
                                if (cyclesList.get(i).getSets()!=null) columnSize+=1;
                            }

                            //Todo: Retrieved as [xx xx xx] Strings from DB, then converted to String ArrayList for adapter. Currently Strings being retrieved in callback as total seconds (i.e. multiples of 5, w/ no punctuation). Need only to change their display in recycler.
                            if (cyclesList.size()>0 && cyclesList.get(0).getSets()!=null) {
                                for (int i=0; i<columnSize; i++) {
                                    //getSets/Breaks returns String [xx, xy, xz] etc.
                                    setsArray.add(cyclesList.get(i).getSets());
                                    breaksArray.add(cyclesList.get(i).getBreaks());
                                    tempSets = cyclesList.get(i).getSets().split(",");
                                    tempBreaks = cyclesList.get(i).getBreaks().split(",");
                                    setCount.add(tempSets.length);
                                    breakCount.add(tempBreaks.length);
                                    Log.i("newSave", "tempSets and breaks are " + Arrays.toString(tempSets) + " and " + Arrays.toString(tempBreaks));
                                }
                                Log.i("newSave", "cycleList entry count is " + cyclesList.size());
                                Log.i("newSave", "number of ROWS are " + setsArray.size() + " and " + breaksArray.size());
                                Log.i("newSave", "setCount and break sizes are " + setCount.size() + " and " + breakCount.size());

                                //Todo: For testing only.
                                savedCycleAdapter = new SavedCycleAdapter(getApplicationContext(), setsArray, breaksArray, breaksOnlyArray, false);
                                savedCycleRecycler.setAdapter(savedCycleAdapter);
                                savedCycleAdapter.setItemClick(MainActivity.this);
                                runOnUiThread(() -> {
                                    savedCyclePopupWindow = new PopupWindow(savedCyclePopupView, 800, 1200, false);
                                    savedCyclePopupWindow.showAtLocation(savedCyclePopupView, Gravity.CENTER, 0, 100);
                                    savedCycleAdapter.notifyDataSetChanged();
                                });
                        } else {
                                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Nothing saved!", Toast.LENGTH_SHORT).show());
                            }
                        } else {
                            if (cyclesBOList.size()>0) {
                                for (int i=0; i<cyclesBOList.size(); i++) {
                                    breaksOnlyArray.add(cyclesBOList.get(i).getBreaksOnly());
                                    tempBreaksOnly = cyclesBOList.get(i).getBreaksOnly().split(",");
                                    breaksOnlyCount.add(tempBreaksOnly.length);
                                }
                                savedCycleAdapter = new SavedCycleAdapter(getApplicationContext(), setsArray, breaksArray, breaksOnlyArray, true);
                                savedCycleRecycler.setAdapter(savedCycleAdapter);
                                savedCycleAdapter.setItemClick(MainActivity.this);
                                runOnUiThread(() -> {
                                    savedCyclePopupWindow = new PopupWindow(savedCyclePopupView, 800, 1200, false);
                                    savedCyclePopupWindow.showAtLocation(savedCyclePopupView, Gravity.CENTER, 0, 100);
                                    savedCycleAdapter.notifyDataSetChanged();
                                });
                            }
                        }
                        runOnUiThread(() -> cl.setOnClickListener(v2-> {
                            savedCyclePopupWindow.dismiss();
                            disableSavedPopup = false;
                        }));
                    });
                }
                break;
                //Todo: Confirmation of delete all.
            case R.id.delete_all_cycles:
                if (!breaksOnly) cyclesDatabase.cyclesDao().deleteAll(); else cyclesDatabase.cyclesDao().deleteAllBO();
                Toast.makeText(getApplicationContext(), "Deleted!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

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
        setsArray = new ArrayList<>();
        breaksArray = new ArrayList<>();

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        savedCyclePopupView = inflater.inflate(R.layout.saved_cycles_layout, null);
        savedCycleRecycler = savedCyclePopupView.findViewById(R.id.cycle_list_recycler);

        LinearLayoutManager lm2 = new LinearLayoutManager(getApplicationContext());
        savedCycleAdapter = new SavedCycleAdapter(getApplicationContext(), setsArray, breaksArray, breaksOnlyArray, false);
        savedCycleRecycler.setAdapter(savedCycleAdapter);
        savedCycleRecycler.setLayoutManager(lm2);
        savedCycleAdapter.setItemClick(MainActivity.this);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_bar);

        s1 = findViewById(R.id.s1);
        s2 = findViewById(R.id.s2);
        s3 = findViewById(R.id.s3);
        spinner1 = findViewById(R.id.spin1);
        spinner2 = findViewById(R.id.spin2);
        spinner3 = findViewById(R.id.spin3);
        blank_spinner = findViewById(R.id.blank_spinner1);
        reset = findViewById(R.id.reset);
        plus_sign = findViewById(R.id.plus_sign);
        minus_sign = findViewById(R.id.minus_sign);
        cycles_completed = findViewById(R.id.cycles_completed);
        cycle_reset = findViewById(R.id.cycle_reset);
        skip = findViewById(R.id.skip);
        newLap = findViewById(R.id.new_lap);

        reset.setVisibility(View.INVISIBLE);
        blank_spinner.setVisibility(View.GONE);

        breaks_only = findViewById(R.id.breaks_only);
        save_cycles = findViewById(R.id.save_cycles);
        progressBar = findViewById(R.id.progressBar);
        progressBar2 = findViewById(R.id.progressBar2);
        stopWatchButton = findViewById(R.id.stopWatchButton);
        timeLeft = findViewById(R.id.timeLeft);
        timeLeft2 = findViewById(R.id.timeLeft2);
        timeLeft3 =findViewById(R.id.timeLeft3);
        timePaused = findViewById(R.id.timePaused);
        timePaused2 = findViewById(R.id.timePaused2);
        timePaused3 = findViewById(R.id.timePaused3);
        msTime = findViewById(R.id.msTime);
        msTimePaused = findViewById(R.id.msTimePaused);
        dotDraws = findViewById(R.id.dotdraws);
        savedDraws = findViewById(R.id.saved_draws);
        lapRecycler = findViewById(R.id.lap_recycler);
        cl = findViewById(R.id.main_layout);

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

        handler = new Handler();
        spinList1 = new ArrayList<>();
        spinList2 = new ArrayList<>();
        spinList3 = new ArrayList<>();
        spinListString1 = new ArrayList<>();
        spinListString2 = new ArrayList<>();
        spinListString3 = new ArrayList<>();
        List<Long> pomList1 = new ArrayList<>();
        List<Long> pomList2 = new ArrayList<>();
        List<Long> pomList3 = new ArrayList<>();
        modeOneSpins = new ArrayList<>();
        modeOneBreakOnlySpins = new ArrayList<>();
        modeTwoSpins = new ArrayList<>();
        customSetTime = new ArrayList<>();
        customBreakTime = new ArrayList<>();
        startCustomSetTime = new ArrayList<>();
        startCustomBreakTime = new ArrayList<>();
        breaksOnlyTime = new ArrayList<>();
        startBreaksOnlyTime = new ArrayList<>();
        currentLapList = new ArrayList<>();
        savedLapList = new ArrayList<>();
        setsArray = new ArrayList<>();
        breaksArray = new ArrayList<>();
        breaksOnlyArray = new ArrayList<>();

        s3.setText(R.string.set_number);
        spinner3.setVisibility(View.GONE);
        progressBar2.setVisibility(View.GONE);
        stopWatchButton.setVisibility(View.GONE);
        newLap.setVisibility(View.GONE);

        removeViews();
        timePaused.setAlpha(1);

        for (long i=0; i<300; i+=5) {
            spinList1.add(i+5);
            spinListString1.add(convertSeconds(i+5));
            spinList2.add(i+5);
            spinListString2.add(convertSeconds(i+5));
        }
        spinListString1.set(0, "05");
        spinListString2.set(0, "05");

        for (long i=0; i<10; i++) {
            spinList3.add(i+1);
            spinListString3.add(convertSeconds(i+1));
        }

        for (long i=10; i<90; i+=5) {
            pomList1.add(i+5);
        }
        for (long i=3; i<6; i++) {
            pomList2.add(i);
        }
        for (long i=10; i<30; i+=5) {
            pomList3.add(i+5);
        }

        //Default list values for Custom.
        for (int i=0; i<3; i++) {
            customSetTime.add(spinList1.get(5) * 1000);
            customBreakTime.add(spinList2.get(8) * 1000);
            startCustomSetTime.add(spinList1.get(5) * 1000);
            startCustomBreakTime.add(spinList2.get(8) * 1000);
            breaksOnlyTime.add(spinList2.get(8) * 1000);
            startBreaksOnlyTime.add(spinList2.get(8) * 1000);
        }

        LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext());
        lapAdapter = new LapAdapter(getApplicationContext(), currentLapList, savedLapList);
        lapRecycler.setAdapter(lapAdapter);
        lapRecycler.setLayoutManager(lm);

        spinAdapter1 = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner, spinListString1);
        spinAdapter2 = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner, spinListString2);
        spinAdapter3 = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner, spinListString3);

        pomAdapter1 = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner, pomList1);
        pomAdapter2 = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner, pomList2);
        pomAdapter3 = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner, pomList3);

        spinner1.setAdapter(spinAdapter1);
        spinner2.setAdapter(spinAdapter2);
        spinner3.setAdapter(spinAdapter3);

        //Default spinner values for Custom
        spinner1.setSelection(0);
        spinner2.setSelection(0);
        modeOneSpins.add(spinner1.getSelectedItemPosition());
        modeOneSpins.add(spinner2.getSelectedItemPosition());

        //Setting default spinner positional value for breaksOnly mode, since we do not begin here w/ spinner values to pass in.
        modeOneBreakOnlySpins.add(8);

        //Elements here are retrieved as spinner positions via retrieveSpins(), and therefore must be <= than the number of spinner entries.
        modeTwoSpins.add(0, 2);
        modeTwoSpins.add(1, 2);
        modeTwoSpins.add(2, 2);

        valueAnimatorDown = new ValueAnimator().ofFloat(90f, 70f);
        valueAnimatorUp = new ValueAnimator().ofFloat(70f, 90f);
        valueAnimatorDown.setDuration(1000);
        valueAnimatorUp.setDuration(1000);

        //Custom defaults
        mode = 1;
        dotDraws.setMode(1);
        savedSets = customSetTime.size();
        savedBreaks = customBreakTime.size();
        numberOfSets = savedSets;
        numberOfBreaks = savedBreaks;

        //Pom defaults
        pomMillis = pomList1.get(2)*60000;

        progressBar.setProgress(maxProgress);
        progressBar2.setProgress(maxProgress);
        resetTimer();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        mode=1;
                        switchTimer(1, customHalted);
                        dotDraws.setMode(1);
                        if (!setBegun) dotDraws.newDraw(savedSets, savedBreaks, 0, 0, 0);
                        if (!breaksOnly) retrieveSpins(modeOneSpins, false); else retrieveSpins(modeOneBreakOnlySpins, false);
                        break;
                    case 1:
                        mode=2;
                        switchTimer(2, pomHalted);
                        retrieveSpins(modeTwoSpins, true);
                        dotDraws.setMode(2);
                        dotDraws.pomDraw(1, 0);
                        break;
                    case 2:
                        mode=3;
                        switchTimer(3, stopwatchHalted);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                firstSpinCount = 0;
                secondSpinCount = 0;
                thirdSpinCount = 0;
                switch (tab.getPosition()) {
                    case 0:
                        if (customHalted) fadeOutText(timePaused); else fadeOutText(timeLeft);
                        break;
                    case 1:
                        if (pomHalted) fadeOutText(timePaused2); else fadeOutText(timeLeft2);
                        if (cyclePopupWindow!=null) cyclePopupWindow.dismiss();
                        if (resetPopUpWindow!=null) resetPopUpWindow.dismiss();
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                firstSpinCount++;
                if (mode == 1) {
                    if (customSetTime.size() >0) {
                        setMillis = customSetTime.get(customSetTime.size()-1);
                        setStart = (int) setMillis;
                    }
                } else {
                    pos = pomList1.get(position);
                    pomMillis1 = pos*60000;
                }
                if (firstSpinCount >1) {
                    saveSpins();
                    resetTimer();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                secondSpinCount++;
                if (mode == 1) {
                    if (!breaksOnly) {
                        if (customBreakTime.size()>0) breakMillis = customBreakTime.get(customBreakTime.size()-1);
                    } else {
                        if (breaksOnlyTime.size()>0) breakMillis = breaksOnlyTime.get(breaksOnlyTime.size()-1);
                    }
                    breakStart = (int) breakMillis;
                } else {
                    pos = pomList2.get(position);
                    pomMillis2 = pos*60000;
                }
                if (secondSpinCount >1) {
                    saveSpins();
                    resetTimer();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                thirdSpinCount++;
                if (mode == 2) {
                    long pos2 = pomList3.get(position);
                    pomMillis3 = pos2*60000;
                }
                if (thirdSpinCount >1) {
                    saveSpins();
                    resetTimer();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        breaks_only.setOnClickListener(v-> {
            if (mode==1) {
                if (!breaksOnly) {
                    setBegun = true;
                    onBreak = true;
                    breaks_only.setBackgroundColor(getResources().getColor(R.color.light_grey));
                    dotDraws.breaksOnly(true);
                    spinner1.setVisibility(View.GONE);
                    blank_spinner.setVisibility(View.VISIBLE);
                    cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(breaksOnlyCyclesDone)));
                    breaksOnly = true;
                    retrieveSpins(modeOneBreakOnlySpins, false);
                } else {
                    setBegun = false;
                    breaks_only.setBackgroundColor(getResources().getColor(R.color.black));
                    dotDraws.breaksOnly(false);
                    spinner1.setVisibility(View.VISIBLE);
                    blank_spinner.setVisibility(View.GONE);
                    cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));
                    breaksOnly = false;
                    retrieveSpins(modeOneSpins, false);
                }
                dotDraws.newDraw(savedSets, savedBreaks, savedSets-(numberOfSets-1), savedBreaks-(numberOfBreaks-1), 1);
                resetTimer();
            }
        });

        save_cycles.setOnClickListener(v->{
            Gson gson = new Gson();
            if ((!breaksOnly && startCustomSetTime.size()==0) || (breaksOnly && startBreaksOnlyTime.size()==0)) {
                Toast.makeText(getApplicationContext(), "Nothing to save!", Toast.LENGTH_SHORT).show();;
                return;
            }

            AsyncTask.execute(() -> {
                cyclesList = cyclesDatabase.cyclesDao().loadAllCycles();
                cyclesBOList = cyclesDatabase.cyclesDao().loadAllBOCycles();
                ArrayList<Long> tempSets = new ArrayList<>();
                ArrayList<Long> tempBreaks = new ArrayList<>();
                ArrayList<Long> tempBreaksOnly = new ArrayList<>();
                String convertedSetList = "";
                String convertedBreakList = "";
                String convertedBreakOnlyList = "";

                if (!breaksOnly) {
                    for (int i=0; i<startCustomSetTime.size(); i++) {
                        tempSets.add(startCustomSetTime.get(i) /1000);
//                        if (tempSets.get(i).equals("5")) tempSets.set(i, "05");
                    }
                    for (int i=0; i<startCustomBreakTime.size(); i++){
                        tempBreaks.add(startCustomBreakTime.get(i)/1000);
//                        if (tempBreaks.get(i).equals("5")) tempBreaks.set(i, "05");
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

                    boolean duplicate = false;
                    int columnSize = 0;
                    for (int i=0; i<cyclesList.size(); i++) {
                        if (cyclesList.get(i).getSets()!=null) columnSize+=1;
                    }
                    if (cyclesList.size() >0 && cyclesList.get(0).getSets()!=null) {
                        for (int i=0; i<columnSize; i++) {
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
//                        if (tempBreaksOnly.get(i).equals("5")) tempBreaks.set(i, "05");
                    }
                    convertedBreakOnlyList = gson.toJson(tempBreaksOnly);
                    convertedBreakOnlyList = convertedBreakOnlyList.replace("\"", "");
                    convertedBreakOnlyList = convertedBreakOnlyList.replace("]", "");
                    convertedBreakOnlyList = convertedBreakOnlyList.replace("[", "");
                    convertedBreakOnlyList = convertedBreakOnlyList.replace(",", " - ");
                    cyclesBO.setBreaksOnly(convertedBreakOnlyList);

                    boolean duplicate = false;
                    //Todo: INSERT adding new row, e.g. if several are populated by !breaksOnly, new entry will follow those w/ new row.
                    if (cyclesBOList.size()>0) {
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
                savedCycleAdapter.notifyDataSetChanged();
            });
        });

        progressBar.setOnClickListener(v-> {
            if (!customHalted) {
                pauseAndResumeTimer(PAUSING_TIMER);
            } else {
                pauseAndResumeTimer(RESUMING_TIMER);
            }

        });

        progressBar2.setOnClickListener(v-> {
            if (!pomHalted) {
                pauseAndResumeTimer(PAUSING_TIMER);
            } else {
                pauseAndResumeTimer(RESUMING_TIMER);
            }
        });

        cycle_reset.setOnClickListener(v -> {
            switch (mode) {
                case 1:if (!breaksOnly) customCyclesDone = 0; else breaksOnlyCyclesDone = 0; break;
                case 2:
                    if (pomCyclesDone >=0) {

                        cycle_reset.setVisibility(View.GONE);
                        LayoutInflater inflater2 = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                        View view = inflater2.inflate(R.layout.pom_reset_popup, null);
                        cyclePopupWindow  = new PopupWindow(view, 150, WindowManager.LayoutParams.WRAP_CONTENT, false);
                        cyclePopupWindow.showAtLocation(view, Gravity.CENTER, 400, 475);

                        TextView confirm_reset = view.findViewById(R.id.pom_reset_text);
                        confirm_reset.setGravity(Gravity.CENTER_HORIZONTAL);
                        confirm_reset.setText(R.string.pom_cycle_reset);

                        cl.setOnClickListener(v2-> {
                            cyclePopupWindow.dismiss();
                            cycle_reset.setVisibility(View.VISIBLE);
                        });

                        confirm_reset.setOnClickListener(v2-> {
                            pomCyclesDone = 0;
                            cycle_reset.setVisibility(View.VISIBLE);
                            cycles_completed.setText(getString(R.string.cycles_done, "0"));
                            cyclePopupWindow.dismiss();;
                        });
                    }
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
                            if (customSetTime.size() >0) timePaused.setText(convertSeconds((customSetTime.get(customSetTime.size()-1)+999)/1000));
                        } else {
                            if (breaksOnlyTime.size() >0) {
                                breaksOnlyTime.remove(breaksOnlyTime.size()-1);
                                numberOfBreaks--;
                                oldCycle2 = breaksOnlyCyclesDone;
                                breakBegun = false;
                            }
                            if (breaksOnlyTime.size() >0) {
                                timeLeft.setText(convertSeconds((breaksOnlyTime.get(breaksOnlyTime.size() - 1) +999) / 1000));
                                timePaused.setText(convertSeconds((breaksOnlyTime.get(breaksOnlyTime.size()-1)+999)/1000));
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

        plus_sign.setOnClickListener(v -> {
            adjustCustom(true);
        });

        minus_sign.setOnClickListener(v -> {
            adjustCustom(false);
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
                });
            }
        });

        stopWatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DecimalFormat df = new DecimalFormat("0");
                DecimalFormat df2 = new DecimalFormat("00");
                if (stopwatchHalted) {
                    final Runnable r = new Runnable() {
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
                            String displayMs = df2.format((msDisplay/60) * 100);
                            String displayTime = convertStopwatch((long) seconds);

                            timeLeft3.setText(displayTime);
                            msTime.setText(displayMs);
                            handler.postDelayed(this, 10);
                        }
                    };
                    handler.post(r);
                    stopwatchHalted = false;
                    reset.setVisibility(View.INVISIBLE);
                } else {
                    handler.removeCallbacksAndMessages(null);
                    stopwatchHalted = true;
                    reset.setVisibility(View.VISIBLE);
                }
            }
        });

        newLap.setOnClickListener(v -> {
            double newSeconds = msReset/60;
            double newMinutes = newSeconds/60;

            double savedMinutes = 0;
            double savedSeconds = 0;
            double savedMs = 0;

            String newEntries = "";
            String savedEntries = "";
            String[] holder = null;
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

            lapAdapter.notifyDataSetChanged();

            msReset = 0;
            msConvert2 = 0;
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
                timeLeft.setText(convertSeconds((setMillis + 999)/1000));
                timer = new CountDownTimer(setMillis, 50) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        customProgressPause = (int) objectAnimator.getAnimatedValue();
                        setMillis = millisUntilFinished;

                        if (fadeCustomTimer) {
                            if (customAlpha<0.25) timeLeft.setAlpha(customAlpha+=0.04);
                            else if (customAlpha<0.5) timeLeft.setAlpha(customAlpha+=.08);
                            else timeLeft.setAlpha(customAlpha+=.12);
                        }
                        if (customAlpha >=1) fadeCustomTimer = false;

                        timeLeft.setText(convertSeconds((setMillis + 999)/1000));
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
                        handler.postDelayed((Runnable) () -> {
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
                            else timeLeft2.setAlpha(pomAlpha+=.12);
                        }
                        if (pomAlpha >=1) fadePomTimer = false;

                        timeLeft2.setText(convertSeconds((pomMillis + 999)/1000));
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

                    handler.postDelayed(() -> {
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

    public String convertSeconds(long totalSeconds) {
        DecimalFormat df = new DecimalFormat("00");
        long minutes;
        long remainingSeconds;

        if (totalSeconds >=60) {
            minutes = totalSeconds/60;
            remainingSeconds = totalSeconds % 60;
            return (minutes + ":" + df.format(remainingSeconds));
        } else if (totalSeconds != 5) return String.valueOf(totalSeconds);
        else return "5";
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
                    customSetTime.add(spinList1.get(spinner1.getSelectedItemPosition()) * 1000);
                    startCustomSetTime.add(spinList1.get(spinner1.getSelectedItemPosition()) * 1000);
                } else {
                    Toast.makeText(getApplicationContext(), "Max rounds reached!", Toast.LENGTH_SHORT).show();
                }
                if (customBreakTime.size() < 10 && customBreakTime.size() >= 0) {
                    customBreakTime.add(spinList2.get(spinner2.getSelectedItemPosition()) * 1000);
                    startCustomBreakTime.add(spinList2.get(spinner2.getSelectedItemPosition()) * 1000);
                }
            } else {
                if (breaksOnlyTime.size() < 10 && breaksOnlyTime.size() >= 0) {
                    breaksOnlyTime.add(spinList2.get(spinner2.getSelectedItemPosition()) * 1000);
                    startBreaksOnlyTime.add(spinList2.get(spinner2.getSelectedItemPosition()) * 1000);
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
        saveSpins();
        dotDraws.newDraw(savedSets, savedBreaks, 0, 0, 0);

        if (!breaksOnly) {
            if (customSetTime.size()>0) {
                timePaused.setText(convertSeconds((customSetTime.get(customSetTime.size() - 1) +999) / 1000));
            } else {
                timePaused.setText("?");
            }
        } else {
            if (breaksOnlyTime.size()>0) {
                timeLeft.setText(convertSeconds((breaksOnlyTime.get(breaksOnlyTime.size() - 1) +999) / 1000));
                timePaused.setText(convertSeconds((breaksOnlyTime.get(breaksOnlyTime.size() - 1) +999) / 1000));
            } else {
                timePaused.setText("?");
            }
        }
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
        va.addUpdateListener(animation -> {
            float sizeChange = (float) va.getAnimatedValue();
            textView.setTextSize(sizeChange);
            textViewPaused.setTextSize(sizeChange);
        });
        va.start();
    }

    public void fadeTextIn(TextView textView) {
        if (fadeInObj!=null) fadeInObj.cancel();
        fadeInObj = ObjectAnimator.ofFloat(textView, "alpha", 0.0f, 1.0f);
        fadeInObj.setDuration(1500);
        fadeInObj.start();
    }

    public void fadeOutText(TextView textView) {
        if (fadeOutObj!=null) fadeOutObj.cancel();
        fadeOutObj = ObjectAnimator.ofFloat(textView, "alpha", 1.0f, 0.0f);
        fadeOutObj.setDuration(1000);
        fadeOutObj.start();
    }

    public void saveSpins() {
        switch (mode) {
            case 1:
                if (!breaksOnly) {
                    if (modeOneSpins.size() >= 2) {
                        modeOneSpins.set(0, spinner1.getSelectedItemPosition());
                        modeOneSpins.set(1, spinner2.getSelectedItemPosition());
                    }
                } else {
                    if (modeOneBreakOnlySpins.size() >= 1) {
                        modeOneBreakOnlySpins.set(0, spinner2.getSelectedItemPosition());
                    }
                }
                break;
            case 2:
                if (modeTwoSpins.size() >= 3) {
                    modeTwoSpins.set(0, spinner1.getSelectedItemPosition());
                    modeTwoSpins.set(1, spinner2.getSelectedItemPosition());
                    modeTwoSpins.set(2, spinner3.getSelectedItemPosition());
                }
        }
        Log.i("spins", "modeOneSpins are " + modeOneSpins);
    }

    public void retrieveSpins(List<Integer> spinList, boolean isPom) {
        if (!isPom) {
            if (!breaksOnly) spinner1.setAdapter(spinAdapter1);
            spinner2.setAdapter(spinAdapter2);
        } else {
            spinner1.setAdapter(pomAdapter1);
            spinner2.setAdapter(pomAdapter2);
            spinner3.setAdapter(pomAdapter3);
        }
        if (spinList.size() != 0) {
            if (isPom) {
                spinner3.setSelection(spinList.get(2));
            } else {
                if (!breaksOnly) {
                    spinner1.setSelection(spinList.get(0));
                    spinner2.setSelection(spinList.get(1));
                } else {
                    spinner2.setSelection(spinList.get(0));
                }
            }
        }
        Log.i("retrieveSpins", "list is " + spinList);

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
                changeTextSize(valueAnimatorUp, timeLeft, timePaused);
                if (halted) {
                    if (!breaksOnly) {
                        timePaused.setText(convertSeconds((setMillis + 999)/1000));
                    } else {
                        timePaused.setText(convertSeconds((breakMillis + 999)/1000));
                    }
                    fadeTextIn(timePaused);
                    dotDraws.newDraw(savedSets, savedBreaks, savedSets- (numberOfSets -1), savedBreaks- (numberOfBreaks-1), 1);
                } else {
                    customAlpha = 0;
                    fadeCustomTimer = true;
                    startObjectAnimator();
                }
                break;
            case 2:
                drawing = false;
                cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(pomCyclesDone)));
                changeTextSize(valueAnimatorDown, timeLeft2, timePaused2);

                if (pomMillisUntilFinished==0) pomMillisUntilFinished = pomMillis;
                if (halted) {
                    timePaused2.setText(convertSeconds((pomMillis + 999)/1000));
                    fadeTextIn(timePaused2);
                    dotDraws.pomDraw(pomDotCounter, 1);
                } else {
                    pomAlpha = 0;
                    fadePomTimer = true;
                    startObjectAnimator();
                }
                break;
            case 3:
                cycles_completed.setText(getString(R.string.laps_completed, String.valueOf(lapsDone)));
                dotDraws.setMode(3);
                dotDraws.pomDraw(pomDotCounter, 1);
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
            }
        } else if ( (!breaksOnly && customSetTime.size()==0) || (breaksOnly && breaksOnlyTime.size()==0)) {
            Toast.makeText(getApplicationContext(), "What are we timing?", Toast.LENGTH_SHORT).show();
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
                stopWatchButton.setVisibility(View.GONE);
                plus_sign.setVisibility(View.VISIBLE);
                minus_sign.setVisibility(View.VISIBLE);
                spinner1.setVisibility(View.VISIBLE);
                spinner2.setVisibility(View.VISIBLE);
                spinner3.setVisibility(View.GONE);

                s1.setVisibility(View.VISIBLE);
                s2.setVisibility(View.VISIBLE);
                s3.setVisibility(View.VISIBLE);
                skip.setVisibility(View.VISIBLE);
                newLap.setVisibility(View.GONE);
                s1.setText(R.string.set_time);
                s2.setText(R.string.break_time);
                s3.setText(R.string.set_number);
                cycle_reset.setText(R.string.clear_cycles);
                params2.width = 150;
                break;
            case 2:
                progressBar.setVisibility(View.INVISIBLE);
                progressBar2.setVisibility(View.VISIBLE);
                stopWatchButton.setVisibility(View.GONE);

                plus_sign.setVisibility(View.GONE);
                minus_sign.setVisibility(View.GONE);
                spinner1.setVisibility(View.VISIBLE);
                spinner2.setVisibility(View.VISIBLE);
                spinner3.setVisibility(View.VISIBLE);
                blank_spinner.setVisibility(View.GONE);
                s1.setVisibility(View.VISIBLE);
                s2.setVisibility(View.VISIBLE);
                s3.setVisibility(View.VISIBLE);
                skip.setVisibility(View.VISIBLE);
                newLap.setVisibility(View.GONE);
                s1.setText(R.string.work_time);
                s2.setText(R.string.small_break);
                s3.setText(R.string.long_break);
                cycle_reset.setText(R.string.clear_cycles);
                params2.width = 150;
                break;
            case 3:
                progressBar.setVisibility(View.INVISIBLE);
                progressBar2.setVisibility(View.INVISIBLE);
                stopWatchButton.setVisibility(View.VISIBLE);
                plus_sign.setVisibility(View.GONE);
                minus_sign.setVisibility(View.GONE);
                spinner1.setVisibility(View.GONE);
                spinner2.setVisibility(View.GONE);
                spinner3.setVisibility(View.GONE);
                blank_spinner.setVisibility(View.GONE);
                s1.setVisibility(View.GONE);
                s2.setVisibility(View.GONE);
                s3.setVisibility(View.GONE);
                skip.setVisibility(View.GONE);
                newLap.setVisibility(View.VISIBLE);
                cycle_reset.setText(R.string.clear_laps);
                params2.width = 200;
                timeLeft3.setAlpha(1);
                msTime.setAlpha(1);
                timeLeft3.setText("0");
                msTime.setText("0");
        }
        cycle_reset.setLayoutParams(params2);
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
                    timeLeft.setText(convertSeconds((setMillis+999)/1000));
                    timePaused.setText(convertSeconds((setMillis+999)/1000));
                }
                if (!breaksOnly) {
                    if (startCustomBreakTime.size()>0) {
                        breakMillis = startCustomBreakTime.get(startCustomBreakTime.size()-1);
                        timeLeft.setText(convertSeconds((setMillis+999)/1000));
                        timePaused.setText(convertSeconds((setMillis+999)/1000));
                    }
                } else {
                    if (startBreaksOnlyTime.size()>0) {
                        breakMillis = startBreaksOnlyTime.get(startBreaksOnlyTime.size()-1);
                        timeLeft.setText(convertSeconds((breakMillis+999)/1000));
                        timePaused.setText(convertSeconds((breakMillis+999)/1000));
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
                } else {
                    if (startBreaksOnlyTime.size()>0) {
                        breaksOnlyTime.addAll(startBreaksOnlyTime);
                        timerDisabled = false;
                    } else {
                        timerDisabled = true;
                    }
                    dotDraws.breakTime(breaksOnlyTime);
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
                timeLeft2.setText(convertSeconds((pomMillis+999)/1000));
                timePaused2.setText(convertSeconds((pomMillis+999)/1000));
                onBreak = false;
                pomHalted = true;
                pomProgressPause = maxProgress;
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
                timeLeft3.setAlpha(1);
                msTime.setAlpha(1);
                timeLeft3.setText("0");
                msTime.setText("00");
                if (currentLapList.size()>0) currentLapList.clear();
                if (savedLapList.size()>0) savedLapList.clear();
                lapAdapter.notifyDataSetChanged();
                break;
        }
    }

}