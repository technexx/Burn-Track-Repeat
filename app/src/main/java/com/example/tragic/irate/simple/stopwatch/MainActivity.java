package com.example.tragic.irate.simple.stopwatch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView heading;
    ProgressBar progressBar;
    TextView timeLeft;
    CountDownTimer timer;
    TextView reset;
    ObjectAnimator objectAnimator;
    Animation endAnimation;

    TextView s1;
    TextView s2;
    TextView s3;
    TextView blank_spinner;
    TextView cycles_completed;
    TextView cycle_reset;
    TextView skip;
    Spinner spinner1;
    Spinner spinner2;
    Spinner spinner3;
    ImageButton plus_sign;
    ImageButton minus_sign;

    ArrayAdapter<String> spinAdapter1;
    ArrayAdapter<String> spinAdapter2;
    ArrayAdapter<String> spinAdapter3;
    ArrayAdapter<Long> pomAdapter1;
    ArrayAdapter<Long> pomAdapter2;
    ArrayAdapter<Long> pomAdapter3;

    List<Long> spinList1;
    List<Long> spinList2;
    List<Long> spinList3;
    List<String> spinListString1;
    List<String> spinListString2;
    List<String> spinListString3;

    List<Integer> modeOneSpins;
    List<Integer> modeTwoSpins;
    List<Integer> modeThreeSpins;
    List<Integer> modeFourSpins;

    int breakCounter;
    int setCounter;
    int maxProgress = 10000;

    long breakMillis;
    int breakStart;
    long setMillis;
    int setStart;
    double setPause = 10000;
    double breakPause = 10000;

    long savedStrictMillis;
    long savedRelaxedMillis;
    long savedCustomMillis;
    long savedPomMillis;

    double savedStrictProgress;
    double savedRelaxedProgress;
    double savedCustomProgress;
    double savedPomProgress;

    long savedStrictDuration;
    long savedRelaxedDuration;
    long savedCustomDuration;
    long savedPomDuration;

    boolean hardReset;
    int firstSpinCount;
    int secondSpinCount;
    int thirdSpinCount;

    long pomMillis1;
    long pomMillis2;
    long pomMillis3;
    int pomStage=1;
    int pomDotCounter=1;

    int strictCyclesDone;
    int relaxedCyclesDone;
    int customCyclesDone;
    int pomCyclesDone;

    long numberOfSets;
    long numberOfBreaks;
    long savedBreaks;
    long savedSets;

    boolean timerEnded;
    boolean paused;
    boolean onBreak;
    boolean relaxedEnded;
    boolean pomEnded;
    boolean timerDisabled;

    DotDraws dotDraws;
    int fadeDone;
    int mode=1;
    ValueAnimator valueAnimatorDown;
    ValueAnimator valueAnimatorUp;

    ArrayList<String> customSets;
    ArrayList<String> customBreaks;
    ArrayList<Long> customSetTime;
    ArrayList<Long> customBreakTime;
    ArrayList<String> startCustomSets;
    ArrayList<String> startCustomBreaks;
    ArrayList<Long> startCustomSetTime;
    ArrayList<Long> startCustomBreakTime;

    //Todo: Hard reset -> Tab switch - > Back to Strict recalls previously paused values.
    //Todo: Prog bar and duration fix for re-tabbing to Relaxed Mode.
    //Todo: timeLeft text syncing issues on pause/resume.
    //Todo: Dot draws rely on numberOf vars, save or don't reset those.
    //Todo: Selecting from spinners does not extend to black layout outside text.
    //Todo: Add taskbar notification for timers.
    //Todo: Rename app, of course.
    //Todo: Possible sqlite db for saved presets.
    //Todo: Add onOptionsSelected dots for About, etc.
    //Todo: Add actual stop watch!

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        reset.setVisibility(View.INVISIBLE);
        blank_spinner.setVisibility(View.GONE);
        plus_sign.setVisibility(View.GONE);
        minus_sign.setVisibility(View.GONE);

        progressBar = findViewById(R.id.progressBar);
        timeLeft = findViewById(R.id.timeLeft);
        dotDraws = findViewById(R.id.dotdraws);
        heading = findViewById(R.id.heading);
        heading.setText(R.string.strict);
        timeLeft.setTextSize(90f);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Strict"));
        tabLayout.addTab(tabLayout.newTab().setText("Relaxed"));
        tabLayout.addTab(tabLayout.newTab().setText("Custom"));
        tabLayout.addTab(tabLayout.newTab().setText("Pomodoro"));

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
        modeTwoSpins = new ArrayList<>();
        modeThreeSpins = new ArrayList<>();
        modeFourSpins = new ArrayList<>();
        customSets = new ArrayList<>();
        customBreaks = new ArrayList<>();
        customSetTime = new ArrayList<>();
        customBreakTime = new ArrayList<>();
        startCustomSets = new ArrayList<>();
        startCustomBreaks = new ArrayList<>();
        startCustomSetTime = new ArrayList<>();
        startCustomBreakTime = new ArrayList<>();

        Handler handler = new Handler();

        modeOneSpins.add(0, spinner1.getSelectedItemPosition());
        modeOneSpins.add(1, spinner2.getSelectedItemPosition());
        modeOneSpins.add(2, spinner3.getSelectedItemPosition());
        modeTwoSpins.add(0, spinner1.getSelectedItemPosition());
        modeTwoSpins.add(1, spinner2.getSelectedItemPosition());
        modeTwoSpins.add(2, spinner3.getSelectedItemPosition());
        modeThreeSpins.add(0, spinner1.getSelectedItemPosition());
        modeThreeSpins.add(1, spinner2.getSelectedItemPosition());
        modeThreeSpins.add(2, spinner3.getSelectedItemPosition());
        modeFourSpins.add(0, spinner1.getSelectedItemPosition());
        modeFourSpins.add(1, spinner2.getSelectedItemPosition());
        modeFourSpins.add(2, spinner3.getSelectedItemPosition());

        valueAnimatorDown = new ValueAnimator().ofFloat(90f, 70f);
        valueAnimatorDown.setDuration(500);
        valueAnimatorUp = new ValueAnimator().ofFloat(70f, 90f);
        valueAnimatorUp.setDuration(500);

        progressMode(setMillis, setPause);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        heading.setText(R.string.strict);
                        spinner1.setVisibility(View.VISIBLE);
                        s1.setVisibility(View.VISIBLE);
                        blank_spinner.setVisibility(View.GONE);

                        mode=1;
                        setSavedProgress(true);
                        changeTextSize (valueAnimatorUp);
                        retrieveSpins(modeOneSpins, false);

                        dotDraws.setMode(1);
                        handler.postDelayed(() -> dotDraws.newDraw(savedSets, savedBreaks, 0, 0, 0), 50);
                        cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(strictCyclesDone)));
                        progressBar.setProgress((int) setPause);
                        break;
                    case 1:
                        heading.setText(R.string.relaxed);
                        spinner1.setVisibility(View.GONE);
                        blank_spinner.setVisibility(View.VISIBLE);
                        retrieveSpins(modeTwoSpins, false);

                        mode = 2;
                        setSavedProgress(false);
                        breakCounter = -1;
                        changeTextSize (valueAnimatorUp);
                        dotDraws.setMode(2);

                        handler.postDelayed(() -> {
                            dotDraws.newDraw(savedSets, savedBreaks, 0, 0, 0);
                        }, 50);
                        cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(relaxedCyclesDone)));
                        break;
                    case 2:
                        mode=3;
                        setSavedProgress(true);
                        changeTextSize(valueAnimatorUp);
                        heading.setText(R.string.variable);
                        retrieveSpins(modeThreeSpins, false);

                        dotDraws.setMode(3);
                        dotDraws.setTime(customSets);
                        dotDraws.breakTime(customBreaks);
                        handler.postDelayed(() -> {
                            dotDraws.newDraw(savedSets, savedBreaks, 0, 0, 0);
                        }, 50);

                        cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));
                        break;
                    case 3:
                        mode=4;
                        setSavedProgress(true);
                        changeTextSize(valueAnimatorDown);
                        heading.setText(R.string.pomodoro);
                        retrieveSpins(modeFourSpins, true);

                        dotDraws.setMode(4);
                        dotDraws.pomDraw(1, 0);
                        cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(pomCyclesDone)));
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                firstSpinCount = 0;
                secondSpinCount = 0;
                thirdSpinCount = 0;
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        for (long i=0; i<300; i+=5) {
            spinList1.add(i+5);
            spinListString1.add(convertSeconds(i+5));
            spinList2.add(i+5);
            spinListString2.add(convertSeconds(i+5));
        }
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

        spinAdapter1 = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner, spinListString1);
        spinAdapter2 = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner, spinListString2);
        spinAdapter3 = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner, spinListString3);

        pomAdapter1 = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner, pomList1);
        pomAdapter2 = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner, pomList2);
        pomAdapter3 = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner, pomList3);

        spinner1.setAdapter(spinAdapter1);
        spinner2.setAdapter(spinAdapter2);
        spinner3.setAdapter(spinAdapter3);

        //Default spinner values for Strict.
        spinner1.setSelection(3);
        spinner2.setSelection(1);
        spinner3.setSelection(2);

        //Default spinner values for Relaxed and Custom.
        modeOneSpins.set(0, 5);
        modeTwoSpins.set(1, 5);
        modeTwoSpins.set(1, 5);
        modeTwoSpins.set(2, 2);
        modeThreeSpins.set(0, 5);
        modeThreeSpins.set(1, 5);

        //Default list values for Custom.
        for (int i=0; i<3; i++) {
            customSets.add(spinListString1.get(5));
            customBreaks.add(spinListString2.get(8));
            startCustomSets.add(spinListString1.get(5));
            startCustomBreaks.add(spinListString2.get(8));
            customSetTime.add(spinList1.get(5) * 1000);
            customBreakTime.add(spinList2.get(8) * 1000);
            startCustomSetTime.add(spinList1.get(5) * 1000);
            startCustomBreakTime.add(spinList2.get(8) * 1000);
        }

        cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(strictCyclesDone)));

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                firstSpinCount++;
                if (mode == 4) {
                    long pos = pomList1.get(position);
                    pomMillis1 = pos*60000;
                    setStart = (int) pomMillis1;
                    setPause = pomMillis1;
                } else {
                    long pos = spinList1.get(position);
                    setStart = (int) pos*1000;
                    if (setMillis == 0 ) {
                        setMillis = pos*1000;
                        if (mode == 3) setMillis = customSetTime.get(customSetTime.size()-1);
                    }
                }
                if (firstSpinCount >1) hardReset = true;
                saveSpins();
                resetTimer(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                secondSpinCount++;
                if (mode == 4) {
                    long pos = pomList2.get(position);
                    pomMillis2 = pos*60000;
                } else {
                    long pos = spinList2.get(position);
                    breakStart = (int) pos*1000;
                    if (breakMillis == 0) {
                        breakMillis = pos*1000;
                        if (mode == 3) breakMillis = customBreakTime.get(customBreakTime.size()-1);
                    }
                }
                if (secondSpinCount >1) hardReset = true;
                saveSpins();
                resetTimer(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                thirdSpinCount++;
                long pos = spinList3.get(position);
                if (mode !=3) {
                    numberOfSets = spinList3.get(position);
                }
                //These will always start equal.
                numberOfBreaks = numberOfSets;
                //(saved) vars for static reference.
                savedSets = numberOfSets;
                savedBreaks = numberOfBreaks;

                if (mode == 4) {
                    long pos2 = pomList3.get(position);
                    pomMillis3 = pos2*60000;
                }
                if (thirdSpinCount >1) hardReset = true;
                saveSpins();
                resetTimer(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        progressBar.setOnClickListener(v-> {
            if (!timerDisabled) {
                if (mode == 2) {
                    onBreak = true;
                }
                if (mode == 4) {
                    onBreak = false;
                }
                //Switches to saved time from start time after initial clicks.
                if (onBreak) {
                    breakCounter++;
                } else {
                    setCounter++;
                }
                //Resuming from a pause.
                if (!paused) {
                    if (onBreak) {
                        breakStart();
                    } else {
                        setStart();
                    }
                    reset.setVisibility(View.INVISIBLE);
                    paused = true;
                    //Pausing
                } else if (!timerEnded) {
                    if (relaxedEnded) {
                        timeLeft.setText(convertSeconds(spinList2.get(modeTwoSpins.get(1))));
                        progressBar.setProgress(10000);
                        endAnimation.cancel();
                        relaxedEnded = false;
                        breakPause = maxProgress;
                    }
                    if (pomEnded) {
                        timeLeft.setText(convertSeconds(spinList1.get(modeFourSpins.get(1))));
                        progressBar.setProgress(10000);
                        endAnimation.cancel();
                        pomEnded = false;
                    }
                    timer.cancel();
                    objectAnimator.cancel();
                    reset.setVisibility(View.VISIBLE);
                    paused = false;
                } else {
                    hardReset = true;
                    resetTimer(true);
                }
                if (endAnimation != null) endAnimation.cancel();
            } else {
                Toast.makeText(getApplicationContext(), "What are we timing?", Toast.LENGTH_SHORT).show();
            }

        });

        cycle_reset.setOnClickListener(v -> {
            switch (mode) {
                case 1:
                    strictCyclesDone = 0; break;
                case 2:
                    relaxedCyclesDone = 0; break;
                case 3:
                    customCyclesDone = 0; break;
                case 4:
                    pomCyclesDone = 0;
            }
            cycles_completed.setText(getString(R.string.cycles_done, "0"));
        });

        skip.setOnClickListener(v -> {
            if (mode !=4) {
                numberOfSets--;
                numberOfBreaks--;
                if (timer != null) {
                    timer.cancel();
                }
                if (objectAnimator != null) {
                    objectAnimator.cancel();
                }
                if (numberOfSets >0 && numberOfBreaks >0) {
                    setCounter = 0;
                    breakCounter = 0;
                    setMillis = setStart;
                    breakMillis = breakStart;
                    paused = false;
                    progressBar.setProgress(10000);

                    if (mode !=2) {
                        timeLeft.setText(convertSeconds((setStart+999)/1000));
                    } else {
                        timeLeft.setText(convertSeconds((breakStart+999)/1000));
                    }
                    if (mode == 3) {
                        adjustCustom(false);
                    }
                    paused = false;
                    resetTimer(false);
                } else if (numberOfSets == 0 && numberOfBreaks == 0){
                    switch (mode) {
                        case 1:
                            strictCyclesDone++;
                            cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(strictCyclesDone)));
                            break;
                        case 2:
                            relaxedCyclesDone++;
                            cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(relaxedCyclesDone)));
                            break;
                        case 3:
                            customCyclesDone++;
                            cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));
                    }
                    endAnimation();
                    progressBar.setProgress(0);
                    timeLeft.setText("0");
                    timerEnded = true;
                    paused = true;
                }
                if (numberOfSets >=0 && numberOfBreaks >=0) {
                    dotDraws.newDraw(savedSets, savedBreaks, savedSets- (numberOfSets -1), savedBreaks- (numberOfBreaks-1), 0);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Cannot skip Pomodoro cycles!", Toast.LENGTH_SHORT).show();
            }
        });

        plus_sign.setOnClickListener(v -> {
            adjustCustom(true);
        });

        minus_sign.setOnClickListener(v -> {
            adjustCustom(false);
        });

        reset.setOnClickListener(v -> {
            hardReset = true;
            resetTimer(true);
        });
    }

    public void setStart() {
        objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", (int) setPause, 0);
        objectAnimator.setInterpolator(new LinearInterpolator());

        //Using setMillis as countdown var for all stages of Pomodoro.
        if (mode==4 && setCounter <1) {
            switch (pomStage) {
                //if (!paused)
                case 1:
                    setMillis = pomMillis1;
                    objectAnimator.setDuration(pomMillis1);
                    break;
                case 2:
                    setMillis = pomMillis2;
                    objectAnimator.setDuration(pomMillis2);
                    break;
                case 3:
                    setMillis = pomMillis3;
                    objectAnimator.setDuration(pomMillis3);
            }
        }

        if (setCounter > 1) {
            setSavedProgress(true);
            objectAnimator.cancel();
            objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", (int) (setPause), 0);
            objectAnimator.setInterpolator(new LinearInterpolator());
            progressBar.setProgress((int) setPause);
        }
        objectAnimator.setDuration(setMillis);
        objectAnimator.start();

        fadeDone = 1;
        //This now retains the saved millis value.
        timer = new CountDownTimer(setMillis, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressMode(setMillis, setPause);

                setPause = (long) ((int) objectAnimator.getAnimatedValue());
                setMillis = millisUntilFinished;
                timeLeft.setText(convertSeconds((millisUntilFinished + 999)/1000));

                boolean fadePaused = false;

                if (mode !=4) {
                    if (fadeDone == 1) {
                        if (!fadePaused) {
                            dotDraws.newDraw(savedSets, savedBreaks, savedSets- (numberOfSets -1), savedBreaks- (numberOfBreaks-1), fadeDone);
                            fadePaused = true;
                        } else {
                            //fadeDone value does not matter here.
                            dotDraws.newDraw(savedSets, savedBreaks, savedSets- numberOfSets, savedBreaks-numberOfBreaks, 1 );
                            fadePaused = false;
                        }
                    }
                } else {
                    //For Pomodoro.
                    dotDraws.pomDraw(pomDotCounter, 1);
                }
            }

            @Override
            public void onFinish() {
                fadeDone = 2;
                timerEnded = true;
                numberOfSets--;
                timeLeft.setText("0");

                resetTimer(false);
                //Ensures first click of next break triggers pause.
                paused = true;

                if (mode !=4) {
                    breakPause = maxProgress;
                    breakMillis = breakStart;
                    breakStart();
                    onBreak = true;
                } else {
                    if (pomDotCounter <7) {
                        if (pomStage == 1) {
                            pomStage = 2;
                        } else {
                            pomStage = 1;
                        }
                    } else {
                        pomStage = 3;
                    }
                    pomDotCounter++;
                    if (pomDotCounter <8) {
                        setStart();
                    } else {
                        pomEnded = true;
                        pomDotCounter = 1;
                        pomCyclesDone +=1;
                        endAnimation();
                        cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(pomCyclesDone)));
                    }
                }
                if (mode == 3) {
                    dotDraws.setTime(startCustomSets);
                    dotDraws.breakTime(startCustomBreaks);
                }

//                    if (Build.VERSION.SDK_INT >= 26) {
//                        vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
//                    } else {
//                        vibrator.vibrate(pattern1, 0);
//                    }
            }
        }.start();
    }

    public void breakStart() {
        objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", maxProgress, 0);
        objectAnimator.setInterpolator(new LinearInterpolator());

        if (mode == 3 && breakCounter <=1) {
            breakMillis = customBreakTime.get(customSetTime.size() -1);
        }

        if (breakCounter >= 1) {
            setSavedProgress(false);
            objectAnimator.cancel();
            objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", (int) (breakPause), 0);
            objectAnimator.setInterpolator(new LinearInterpolator());
        }

        objectAnimator.setDuration(breakMillis);
        objectAnimator.start();
        fadeDone = 3;

        timer = new CountDownTimer(breakMillis, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressMode(breakMillis, breakPause);
                breakPause = (long) ((int) objectAnimator.getAnimatedValue());
                breakMillis = millisUntilFinished;

                progressMode(breakMillis, breakPause);
                timeLeft.setText(convertSeconds((millisUntilFinished +999) / 1000));

                boolean fadePaused = false;
                if (fadeDone == 3) {
                    if (!fadePaused) {
                        dotDraws.newDraw(savedSets, savedBreaks, savedSets- (numberOfSets -1), savedBreaks- (numberOfBreaks-1), fadeDone); fadePaused = true;
                    } else {
                        dotDraws.newDraw(savedSets, savedBreaks, savedSets- numberOfSets, savedBreaks-numberOfBreaks, fadeDone);
                        fadePaused = false;
                    }
                }
            }

            @Override
            public void onFinish() {
                fadeDone = 4;
                timerEnded = true;
                numberOfBreaks--;
                //Ensures first click of next break triggers pause.
                paused = true;

                if (mode == 3) {
                    if (customSets.size() > 0) {
                        customSets.remove(customSets.size() - 1);
                        customSetTime.remove(customSetTime.size() - 1);
                    }
                    if (customBreaks.size() >0) {
                        customBreaks.remove(customBreaks.size() -1);
                        customBreakTime.remove(customBreakTime.size()-1);
                    }
                    dotDraws.setTime(startCustomSets);
                    dotDraws.breakTime(startCustomBreaks);
                    Log.i("customValue", "static set values are " + startCustomSets + " and static break values are " + startCustomBreaks);
                    Log.i("customValue", "set and break counts are " + savedSets + " and" + savedBreaks);
                }

                if (numberOfBreaks >0) {
                    resetTimer(false);
                    if (mode !=2) {
                        setPause = maxProgress;
                        setMillis = setStart;
                        setStart();
                        onBreak = false;
                    } else {
                        breakPause = maxProgress;
                        breakMillis = breakStart;
                        breakStart();
                    }
                } else {
                    switch (mode) {
                        case 1:
                            strictCyclesDone++;
                            cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(strictCyclesDone)));
                            break;
                        case 2:
                            relaxedCyclesDone++;
                            cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(relaxedCyclesDone)));
                            relaxedEnded = true;
                            break;
                        case 3:
                            customCyclesDone++;
                            cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));

                    }
                    endAnimation();
                    timeLeft.setText("0");
                    dotDraws.newDraw(savedSets, savedBreaks, savedSets- (numberOfSets -1), savedBreaks- (numberOfBreaks-1), fadeDone);
                }

//                    if (Build.VERSION.SDK_INT >= 26) {
//                        vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
//                    } else {
//                        vibrator.vibrate(pattern1, 0);
//                    }
            }
        }.start();
    }

    public void resetTimer(boolean complete) {
        Handler handler = new Handler();
        timerEnded = false;
        if (!complete) {
            if (onBreak) {
//                breakMillis = breakStart;
                timeLeft.setText(convertSeconds((breakStart+999)/1000));
                breakCounter = 0;
            } else {
//                setMillis = setStart;
                timeLeft.setText(convertSeconds((setStart+999)/1000));
                setCounter = 0;
            }
        } else {
            progressBar.setProgress(10000);

            handler.postDelayed((Runnable) () -> dotDraws.newDraw(savedSets, savedBreaks, 0, 0, 0), 50);
            spinner1.setVisibility(View.VISIBLE);
            spinner3.setVisibility(View.VISIBLE);
            blank_spinner.setVisibility(View.GONE);
            plus_sign.setVisibility(View.GONE);
            minus_sign.setVisibility(View.GONE);

            if (hardReset) {
                setPause = maxProgress;
                breakPause = maxProgress;
                setMillis = setStart;
                breakMillis = breakStart;
                progressBar.setProgress(10000);
                timeLeft.setText(convertSeconds((setStart+999)/1000));
                if (!onBreak) {
                    setCounter = 0;
                    progressMode(setStart, 10000);
                } else {
                    breakCounter = 0;
                    progressMode(breakStart, 10000);
                }

                hardReset = false;
            } else {
                if (mode==2) {
                    progressBar.setProgress((int) breakPause);
                } else {
                    progressBar.setProgress((int) setPause);
                    timeLeft.setText(convertSeconds((setMillis+999) /1000));
                }
            }
            numberOfSets = savedSets;
            numberOfBreaks = savedBreaks;
            paused = false;

            switch (mode) {
                case 1:
                    onBreak = false;
                    break;
                case 2:
                    timeLeft.setText(convertSeconds((breakStart+999)/1000));
                    breakCounter = -1;
                    spinner1.setVisibility(View.GONE);
                    blank_spinner.setVisibility(View.VISIBLE);
                    onBreak = true;
                    break;
                case 3:
                    if (customSetTime.size() > 0 && customBreakTime.size() >0) {
                        setMillis = customSetTime.get(customSetTime.size()-1);
                        breakMillis = customBreakTime.get(customBreakTime.size()-1);
                        timeLeft.setText(convertSeconds((setMillis+999)/1000));
                    }

                    spinner3.setVisibility(View.GONE);
                    plus_sign.setVisibility(View.VISIBLE);
                    minus_sign.setVisibility(View.VISIBLE);
                    onBreak = false;

                    numberOfSets = startCustomSets.size();
                    numberOfBreaks = startCustomBreaks.size();
                    savedSets = numberOfSets;
                    savedBreaks = numberOfBreaks;

                    customSets = new ArrayList<>();
                    customBreaks = new ArrayList<>();
                    customSetTime = new ArrayList<>();
                    customBreakTime = new ArrayList<>();
                    if (startCustomSets.size() >0) {
                        for (int i=0; i<startCustomSets.size(); i++) {
                            customSets.add(startCustomSets.get(i));
                            customSetTime.add(startCustomSetTime.get(i));
                            customBreaks.add(startCustomBreaks.get(i));
                            customBreakTime.add(startCustomBreakTime.get(i));
                        }
                        timerDisabled = false;
                    } else {
                        timerDisabled = true;
                        reset.setVisibility(View.GONE);
                    }

                    dotDraws.setTime(customSets);
                    dotDraws.breakTime(customBreaks);

                    break;
                case 4:
                    timeLeft.setText(convertSeconds((pomMillis1+999)/1000));
                    setCounter = -1;
                    onBreak = false;
            }

            if (mode !=4) {
                s1.setText(R.string.set_time);
                s2.setText(R.string.break_time);
                s3.setText(R.string.set_number);
            } else {
                s1.setText(R.string.work_time);
                s2.setText(R.string.small_break);
                s3.setText(R.string.long_break);
            }

            if (timer != null) timer.cancel();
            if (objectAnimator != null) objectAnimator.cancel();
            if (endAnimation != null) endAnimation.cancel();
        }
    }

    public String convertSeconds(long totalSeconds) {
        DecimalFormat df = new DecimalFormat("00");
        long minutes;
        long remainingSeconds;

        if (totalSeconds >=60) {
            minutes = totalSeconds/60;
            remainingSeconds = totalSeconds % 60;
            return (minutes + ":" + df.format(remainingSeconds));
        } else {
            return String.valueOf(totalSeconds);
        }
    }

    public void saveSpins() {
        switch (mode) {
            case 1:
                modeOneSpins.set(0, spinner1.getSelectedItemPosition());
                modeOneSpins.set(1, spinner2.getSelectedItemPosition());
                modeOneSpins.set(2, spinner3.getSelectedItemPosition());
                break;
            case 2:
                modeTwoSpins.set(0, spinner1.getSelectedItemPosition());
                modeTwoSpins.set(1, spinner2.getSelectedItemPosition());
                modeTwoSpins.set(2, spinner3.getSelectedItemPosition());
                break;
            case 3:
                modeThreeSpins.set(0, spinner1.getSelectedItemPosition());
                modeThreeSpins.set(1, spinner2.getSelectedItemPosition());
                modeThreeSpins.set(2, (int) customSets.size());
                break;
            case 4:
                modeFourSpins.set(0, spinner1.getSelectedItemPosition());
                modeFourSpins.set(1, spinner2.getSelectedItemPosition());
                modeFourSpins.set(2, spinner3.getSelectedItemPosition());
        }
    }

    public void retrieveSpins(List<Integer> spinList, boolean isPom) {
        if (!isPom) {
            spinner1.setAdapter(spinAdapter1);
            spinner2.setAdapter(spinAdapter2);
            spinner3.setAdapter(spinAdapter3);
        } else {
            spinner1.setAdapter(pomAdapter1);
            spinner2.setAdapter(pomAdapter2);
            spinner3.setAdapter(pomAdapter3);
        }

        if (spinList.size() != 0) {
            spinner1.setSelection(spinList.get(0));
            spinner2.setSelection(spinList.get(1));
            if (mode != 3) {
                spinner3.setSelection(spinList.get(2));
            } else {
                savedSets = modeThreeSpins.get(2);
            }
        }
    }

    public void adjustCustom(boolean adding) {
        if (adding) {
            if (customSets.size() < 10 && customSetTime.size() >= 0) {
                customSets.add(spinListString1.get(spinner1.getSelectedItemPosition()));
                customSetTime.add(spinList1.get(spinner1.getSelectedItemPosition()) * 1000);
                startCustomSets.add(spinListString1.get(spinner1.getSelectedItemPosition()));
                startCustomSetTime.add(spinList1.get(spinner1.getSelectedItemPosition()) * 1000);

                timeLeft.setText(convertSeconds(customSetTime.get((customSetTime.size() - 1+999)) / 1000));
            } else {
                Toast.makeText(getApplicationContext(), "Max sets reached!", Toast.LENGTH_SHORT).show();
            }
            if (customBreaks.size() < 10 && customBreakTime.size() >= 0) {
                customBreaks.add(spinListString2.get(spinner2.getSelectedItemPosition()));
                customBreakTime.add(spinList2.get(spinner2.getSelectedItemPosition()) * 1000);
                startCustomBreaks.add(spinListString2.get(spinner2.getSelectedItemPosition()));
                startCustomBreakTime.add(spinList2.get(spinner2.getSelectedItemPosition()) * 1000);
            }
        } else {
            if (customSets.size() > 0 && customSetTime.size() > 0) {
                customSets.remove(customSets.size() - 1);
                customSetTime.remove(customSetTime.size() - 1);
                startCustomSets.remove(startCustomSets.size() - 1);
                startCustomSetTime.remove(startCustomSetTime.size() - 1);
            } else {
                Toast.makeText(getApplicationContext(), "Nothing left to remove!", Toast.LENGTH_SHORT).show();
            }
            if (customBreaks.size() > 0 && customBreakTime.size() > 0) {
                customBreaks.remove(customBreaks.size() - 1);
                customBreakTime.remove(customBreakTime.size() - 1);
                startCustomBreaks.remove(startCustomBreaks.size() - 1);
                startCustomBreakTime.remove(startCustomBreakTime.size() - 1);

            }
        }
        Log.i("customSize", "set sizes are " + customSets.size() + " and " + startCustomSets.size());
        Log.i("customSize", "break sizes are " + customBreaks.size() + " and " + startCustomBreaks.size());

        resetTimer(true);
        saveSpins();
        dotDraws.newDraw(savedSets, savedBreaks, 0, 0, 0);

        if (customSetTime.size() > 0) {
            timeLeft.setText(convertSeconds((customSetTime.get(customSetTime.size() - 1) +999) / 1000));
        } else {
            timeLeft.setText("?");
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

    public void changeTextSize(ValueAnimator va) {
        va.addUpdateListener(animation -> {
            float sizeChange = (float) va.getAnimatedValue();
            timeLeft.setTextSize(sizeChange);
        });
        va.start();
    }

    public void progressMode(long millis, double progress) {
        switch (mode) {
            case 1:
                savedStrictMillis = millis;;
                savedStrictProgress = progress;
                break;
            case 2:
                savedRelaxedMillis = millis;
                savedRelaxedProgress = progress;
                break;
            case 3:
                savedCustomMillis = millis;
                savedCustomProgress = progress;
                break;
            case 4:
                savedPomMillis = millis;
                savedPomProgress = progress;
        }
    }

    public void setSavedProgress(boolean onSet) {
        if (onSet) {
            switch (mode) {
                case 1: setPause = savedStrictProgress; setMillis = savedStrictMillis; break;
                case 2: setPause = savedRelaxedProgress; setMillis = savedRelaxedMillis; break;
                case 3: setPause = savedCustomProgress; setMillis = savedCustomMillis; break;
                case 4: setPause = savedPomProgress; setMillis = savedPomMillis;
            }
        } else {
            switch (mode) {
                case 1: breakPause = savedStrictProgress; breakMillis = savedStrictMillis; break;
                case 2: breakPause = savedRelaxedProgress; breakMillis = savedRelaxedMillis; break;
                case 3: breakPause = savedCustomProgress; breakMillis = savedCustomMillis; break;
                case 4: breakPause = savedPomProgress; breakMillis = savedPomMillis;
            }
        }
    }
}