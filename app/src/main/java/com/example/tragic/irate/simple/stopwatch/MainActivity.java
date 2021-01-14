package com.example.tragic.irate.simple.stopwatch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    Spinner spinner1;
    Spinner spinner2;
    Spinner spinner3;
    TextView blank_spinner;

    ArrayAdapter<String> spinAdapter1;
    ArrayAdapter<String> spinAdapter2;
    ArrayAdapter<String> spinAdapter3;
    ArrayAdapter<Long> pomAdapter1;
    ArrayAdapter<Long> pomAdapter2;
    ArrayAdapter<Long> pomAdapter3;

    List<String> spinListString1;
    List<String> spinListString2;
    List<String> spinListString3;

    int breakCounter;
    int setCounter;
    long timerValue;
    int maxProgress = 10000;

    long breakMillis;
    int breakStart;
    double breakPause;
    long setMillis;
    int setStart;
    double setPause;

    long pomMillis1;
    long pomMillis2;
    long pomMillis3;
    int pomStage=1;
    int pomDotCounter=1;

    long numberOfSets;
    long numberOfBreaks;
    long savedBreaks;
    long savedSets;

    boolean timerEnded;
    boolean paused;
    boolean onBreak;

    DotDraws dotDraws;
    int fadeDone;
    int mode=1;

    //Todo: Add taskbar notification for timers.
    //Todo: Add option for varying set/break combos.
    //Todo: Add "variable" mode for work/break w/ presets.
    //Todo: Add "Pomodoro Technique" w/ variations.
    //Todo: Click range of progress bar extends outside circle.

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
        blank_spinner = findViewById(R.id.blank_spinner);
        reset = findViewById(R.id.reset);
        reset.setVisibility(View.INVISIBLE);
        blank_spinner.setVisibility(View.GONE);

        progressBar = findViewById(R.id.progressBar);
        timeLeft = findViewById(R.id.timeLeft);
        dotDraws = findViewById(R.id.dotdraws);
        heading = findViewById(R.id.heading);
        heading.setText(R.string.strict);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Strict"));
        tabLayout.addTab(tabLayout.newTab().setText("Relaxed"));
        tabLayout.addTab(tabLayout.newTab().setText("Custom"));
        tabLayout.addTab(tabLayout.newTab().setText("Pomodoro"));

        List<Long> spinList1 = new ArrayList<>();
        List<Long> spinList2 = new ArrayList<>();
        List<Long> spinList3 = new ArrayList<>();
        spinListString1 = new ArrayList<>();
        spinListString2 = new ArrayList<>();
        spinListString3 = new ArrayList<>();
        List<Long> pomList1 = new ArrayList<>();
        List<Long> pomList2 = new ArrayList<>();
        List<Long> pomList3 = new ArrayList<>();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        mode=1;
                        heading.setText(R.string.strict);
                        spinner1.setVisibility(View.VISIBLE);
                        s1.setVisibility(View.VISIBLE);
                        blank_spinner.setVisibility(View.GONE);

                        spinner1.setAdapter(spinAdapter1);
                        spinner2.setAdapter(spinAdapter2);
                        spinner3.setAdapter(spinAdapter3);

                        dotDraws.setMode(1);
                        dotDraws.newDraw(savedSets, savedBreaks, 0, 0, 0);
                        break;
                    case 1:
                        mode = 2;
                        heading.setText(R.string.relaxed);
                        spinner1.setVisibility(View.GONE);
                        blank_spinner.setVisibility(View.VISIBLE);

                        spinner1.setAdapter(spinAdapter1);
                        spinner2.setAdapter(spinAdapter2);
                        spinner3.setAdapter(spinAdapter3);

                        dotDraws.setMode(2);
                        dotDraws.newDraw(savedSets, savedBreaks, 0, 0, 0);
                        break;
                    case 2:
                        mode=3;
                        heading.setText(R.string.variable);

                        spinner1.setAdapter(spinAdapter1);
                        spinner2.setAdapter(spinAdapter2);
                        spinner3.setAdapter(spinAdapter3);
                        break;
                    case 3:
                        mode=4;
                        heading.setText(R.string.pomodoro);

                        spinner1.setAdapter(pomAdapter1);
                        spinner2.setAdapter(pomAdapter2);
                        spinner3.setAdapter(pomAdapter3);

                        dotDraws.setMode(4);
                        dotDraws.pomDraw(1, 0);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                resetTimer(true);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        for (long i=0; i<300; i+=5) {
            spinList1.add(i+5);
            spinListString1.add(convertSeconds(i+5));
        }
        for (long i=0; i<180; i+=5) {
            spinList2.add(i+5);
            spinListString2.add(convertSeconds(i+5));
        }
        for (long i=0; i<10; i++) {
            spinList3.add(i+1);
            spinListString3.add(convertSeconds(i+1));
        }
        //15-90 minute work time.
        for (long i=10; i<90; i+=5) {
            pomList1.add(i+5);
        }
        for (long i=3; i<6; i++) {
            pomList2.add(i);
        }
        //15-30 minute break time.
        for (long i=10; i<30; i+=5) {
            pomList3.add(i+5);
        }

        spinAdapter1 = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner, spinListString1);
        spinAdapter2 = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner, spinListString2);
        spinAdapter3 = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner, spinListString3);
        pomAdapter1 = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner, pomList1);
        pomAdapter2 = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner, pomList2);
        pomAdapter3 = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner, pomList3);

        spinner1.setSelection(0);
        spinner2.setSelection(1);
        spinner3.setSelection(2);

        spinner1.setAdapter(spinAdapter1);
        spinner2.setAdapter(spinAdapter2);
        spinner3.setAdapter(spinAdapter3);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mode == 4) {
                    long pos2 = pomList1.get(position);
                    pomMillis1 = pos2*60000;
                    setStart = (int) pomMillis1;
                    setPause = pomMillis1;
                } else {
                    //Used as a global changing variable for set time.
                    long pos = spinList1.get(position);
                    setMillis = pos*1000;
                    //Retains default spinner value for set time.
                    setStart = (int) setMillis;
                    //Retains remaining value a`fter every set timer pause.
                    setPause = setMillis;
                }
                resetTimer(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Used as a global changing variable for break time.
                long pos = spinList2.get(position);
                breakMillis = pos*1000;
                //Retains default spinner value for break time.
                breakStart = (int) breakMillis;
                //Retains remaining value after every break timer pause.
                breakPause = breakMillis;

                if (mode == 4) {
                    long pos2 = pomList2.get(position);
                    pomMillis2 = pos2*60000;
                }
                resetTimer(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                long pos = spinList3.get(position);
                numberOfSets = spinList3.get(position);
                //These will always start equal.
                numberOfBreaks = numberOfSets;
                //(saved) vars for static reference.
                savedSets = numberOfSets; savedBreaks = numberOfBreaks;

                if (mode == 4) {
                    long pos2 = pomList3.get(position);
                    pomMillis3 = pos2*60000;
                }
                resetTimer(true);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        progressBar.setOnClickListener(v-> {
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
                reset.setVisibility(View.VISIBLE);
                timer.cancel();
                objectAnimator.cancel();
                paused = false;
            } else {
                //A complete cycle of sets and breaks have been completed.
                objectAnimator.cancel();
                resetTimer(true);
            }
        });

        reset.setOnClickListener(v -> {
            resetTimer(true);
        });
    }

    public void setStart() {
        objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", maxProgress, 0);
        objectAnimator.setInterpolator(new LinearInterpolator());

//        setMillis = 2000;
//        pomMillis1 = 2000;
//        pomMillis2 = 3000;
//        pomMillis3 = 4000;

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
            objectAnimator.cancel();
            objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", (int) (setPause), 0);
            objectAnimator.setInterpolator(new LinearInterpolator());
        }
        objectAnimator.setDuration(setMillis);
        objectAnimator.start();

        fadeDone = 1;
        timer = new CountDownTimer(setMillis, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                setPause = (long) ((int) objectAnimator.getAnimatedValue());
                setMillis = millisUntilFinished;

                timerValue =  ((millisUntilFinished+1000) / 1000);
                timeLeft.setText(convertSeconds(timerValue));

                boolean fadePaused = false;

                if (mode !=4) {
                    if (fadeDone == 1) {
                        if (!fadePaused) {
                            dotDraws.newDraw(savedSets, savedBreaks, savedSets- (numberOfSets-1), savedBreaks-numberOfBreaks, 1); fadePaused = true;
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

                Log.i("counter", "pomCounter is " + pomDotCounter);
                if (mode !=4) {
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
                    setStart();
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

        if (breakCounter >= 1) {
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
                breakPause = (long) ((int) objectAnimator.getAnimatedValue());
                breakMillis = millisUntilFinished;

                timerValue =  ((millisUntilFinished+1000) / 1000);
                timeLeft.setText(convertSeconds(timerValue));

                boolean fadePaused = false;
                if (fadeDone == 3) {
                    if (!fadePaused) {
                        dotDraws.newDraw(savedSets, savedBreaks, savedSets- numberOfSets, savedBreaks- (numberOfBreaks-1), fadeDone); fadePaused = true;
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
                timeLeft.setText("0");
                //Ensures first click of next break triggers pause.
                paused = true;

                Log.i("number", String.valueOf(numberOfSets));
                if (numberOfBreaks >0) {
                    resetTimer(false);
                    if (mode !=2) {
                        setStart();
                        onBreak = false;
                    } else {
                        breakStart();
                    }
                } else {
                    progressBar.setAnimation(endAnimation);
                    timeLeft.setAnimation(endAnimation);

                    endAnimation = new AlphaAnimation(1.0f, 0.0f);
                    endAnimation.setDuration(300);
                    endAnimation.setStartOffset(20);
                    endAnimation.setRepeatMode(Animation.REVERSE);
                    endAnimation.setRepeatCount(Animation.INFINITE);

                    dotDraws.newDraw(savedSets, savedBreaks, savedSets- numberOfSets, savedBreaks-numberOfBreaks, fadeDone);
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
        progressBar.setProgress(10000);
        timerEnded = false;
        //Executes when a single SET or BREAK is done.
        if (!complete) {
            if (onBreak) {
                breakMillis = breakStart;
                timeLeft.setText(convertSeconds(breakStart/1000));
                breakCounter = 0;
            } else {
                setMillis = setStart;
                timeLeft.setText(convertSeconds(setStart/1000));
                setCounter = 0;
            }
        } else {
            //Executes whenever we need to reset our timers.
            timeLeft.setText(convertSeconds(setStart/1000));
            dotDraws.newDraw(savedSets, savedBreaks, 0, 0, 0);

            breakCounter = 0;
            setCounter = 0;
            setMillis = setStart;
            breakMillis = breakStart;
            numberOfSets = savedSets;
            numberOfBreaks = savedBreaks;
            paused = false;

            switch (mode) {
                case 1:
                    dotDraws.setMode(1);
                    onBreak = false;
                    break;
                case 2:
                    timeLeft.setText(convertSeconds(breakStart/1000));
                    dotDraws.setMode(2);
                    breakCounter = -1;
                    onBreak = true;
                    break;
                case 3:
                    break;
                case 4:
                    timeLeft.setText(convertSeconds(pomMillis1/1000));
                    dotDraws.setMode(4);
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

            if (timer != null) {
                timer.cancel();
            }
            if (objectAnimator != null) {
                objectAnimator.cancel();
            }
            if (endAnimation != null) {
                endAnimation.cancel();
            }
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

}