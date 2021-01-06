package com.example.tragic.irate.simple.stopwatch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Context;
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
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ProgressBar progressBar;
    TextView timeLeft;
    ObjectAnimator objectAnimator;
    Animation endAnimation;
    CountDownTimer timer;
    Button reset;

    int breakCounter;
    int setCounter;
    long timerStart;
    int maxProgress = 10000;

    long breakMillis;
    int breakStart;
    double breakPause;
    long setMillis;
    int setStart;
    double setPause;
    long numberOfSets;
    long numberOfBreaks;

    boolean timerEnded;
    boolean paused;
    boolean onBreak;

    DotDraws dotDraws;
    long savedBreaks;
    long savedSets;

    //Todo: Click range of progress bar extends outside circle.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner1 = findViewById(R.id.spin1);
        Spinner spinner2 = findViewById(R.id.spin2);
        Spinner spinner3 = findViewById(R.id.spin3);
        reset = findViewById(R.id.reset);
        reset.setVisibility(View.INVISIBLE);

        progressBar = findViewById(R.id.progressBar);
        timeLeft = findViewById(R.id.timeLeft);

        endAnimation = new AlphaAnimation(1.0f, 0.0f);

        List<Long> spinList1 = new ArrayList<>();
        List<Long> spinList2 = new ArrayList<>();
        List<Long> spinList3 = new ArrayList<>();

        for (long i=0; i<300; i+=5) {
            spinList1.add(i+5);
        }
        for (long i=0; i<120; i+=5) {
            spinList2.add(i+5);
        }
        for (long i=0; i<10; i++) {
            spinList3.add(i+1);
        }

        ArrayAdapter<Long> spinAdapter1 = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner, spinList1);
        ArrayAdapter<Long> spinAdapter2 = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner, spinList2);
        ArrayAdapter<Long> spinAdapter3 = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner, spinList3);

        spinner1.setAdapter(spinAdapter1);
        spinner2.setAdapter(spinAdapter2);
        spinner3.setAdapter(spinAdapter3);

        spinner1.setSelection(0);
        spinner2.setSelection(1);
        spinner3.setSelection(2);

        dotDraws = findViewById(R.id.dotdraws);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Used as a global changing variable for set time.
                setMillis = (Long) parent.getItemAtPosition(position) * 1000;
                //Retains default spinner value for set time.
                setStart = (int) setMillis;
                //Retains remaining value after every set timer pause.
                setPause = setMillis;
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
                breakMillis = (Long) parent.getItemAtPosition(position) * 1000;
                //Retains default spinner value for break time.
                breakStart = (int) breakMillis;
                //Retains remaining value after every break timer pause.
                breakPause = breakMillis;
                resetTimer(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                numberOfSets = (long) parent.getItemAtPosition(position);
                numberOfBreaks = numberOfSets;
                resetTimer(true);
                savedSets = numberOfSets; savedBreaks = numberOfBreaks;
                dotDraws.newDraw(savedSets, savedBreaks, 0, 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        progressBar.setOnClickListener(v-> {
            if (onBreak) {
                breakCounter++;
            } else {
                setCounter++;
            }
            if (!paused) {
                if (onBreak) {
                    breakStart();
                } else {
                    setStart();
                }
                reset.setVisibility(View.INVISIBLE);
                paused = true;
            } else if (!timerEnded) {
                //In the middle of either a Set or Break countdown.
                reset.setVisibility(View.VISIBLE);
                timer.cancel();
                objectAnimator.cancel();
                paused = false;
            } else {
                //A cycle of either Set or Break has completed.
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

        if (setCounter > 2) {
            objectAnimator.cancel();
            objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", (int) (setPause), 0);
            objectAnimator.setInterpolator(new LinearInterpolator());
        }
        objectAnimator.setDuration(setMillis);
        objectAnimator.start();

        timer = new CountDownTimer(setMillis, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                setPause = (long) ((int) objectAnimator.getAnimatedValue());
                setMillis = millisUntilFinished;

                timerStart =  ((millisUntilFinished+1000) / 1000);
                timeLeft.setText(convertSeconds(timerStart));
            }

            @Override
            public void onFinish() {
                timerEnded = true;
                timeLeft.setText("0");

                numberOfSets--;
                breakStart();
                resetTimer(false);
                //Used to switch between break and set methods.
                onBreak = true;
                //Ensures first click triggers pause.
                paused = true;
                dotDraws.newDraw(savedSets, savedBreaks, savedBreaks-numberOfSets, savedBreaks-numberOfBreaks);

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

            timer = new CountDownTimer(breakMillis, 50) {
                @Override
                public void onTick(long millisUntilFinished) {
                    breakPause = (long) ((int) objectAnimator.getAnimatedValue());
                    breakMillis = millisUntilFinished;

                    timerStart =  ((millisUntilFinished+1000) / 1000);
                    timeLeft.setText(convertSeconds(timerStart));
                }

                @Override
                public void onFinish() {
                    timerEnded = true;

                    numberOfBreaks--;
                    if (numberOfSets >0) {
                        resetTimer(false);
                        setStart();
                        //Used to switch between break and set methods.
                        onBreak = false;
                    } else {
                        progressBar.setAnimation(endAnimation);
                        timeLeft.setAnimation(endAnimation);

                        endAnimation = new AlphaAnimation(1.0f, 0.0f);
                        endAnimation.setDuration(300);
                        endAnimation.setStartOffset(20);
                        endAnimation.setRepeatMode(Animation.REVERSE);
                        endAnimation.setRepeatCount(Animation.INFINITE);
                        timeLeft.setText("0");
                    }
                    dotDraws.newDraw(savedSets, savedBreaks, savedSets-numberOfSets, savedBreaks-numberOfBreaks);

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
        paused = false;
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
            timeLeft.setText(convertSeconds(setStart/1000));

            breakCounter = 0;
            setCounter = 0;
            setMillis = setStart;
            breakMillis = breakStart;
            onBreak = false;

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

        long minutes = 0;
        long remainingSeconds = 0;
        if (totalSeconds >=60) {
            minutes = totalSeconds/60;
            remainingSeconds = totalSeconds % 60;
            return (minutes + ":" + df.format(remainingSeconds));
        } else {
            return String.valueOf(totalSeconds);
        }
    }
}