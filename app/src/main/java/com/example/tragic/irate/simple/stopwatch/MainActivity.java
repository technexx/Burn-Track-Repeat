package com.example.tragic.irate.simple.stopwatch;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    DecimalFormat df;

    int timerBegun;
    boolean timerEnded;
    boolean paused;
    int maxProgress = 10000;

    long breakMillis;
    int breakStart;
    double breakPause;

    long setMillis;
    int setStart;
    double setPause;

    long timerStart = 60000;

    long numberOfSets;
    boolean onBreak = true;

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
        timeLeft = findViewById(R.id.testTime);

        df = new DecimalFormat(".######");
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

        spinner1.setSelection(11);
        spinner2.setSelection(5);
        spinner3.setSelection(2);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Used as a global changing variable for break time.
                breakMillis = (Long) parent.getItemAtPosition(position) * 1000;
                //Retains default spinner value for break time.
                breakStart = (int) breakMillis;
                //Retains remaining value after every break timer pause.
                breakPause = breakMillis;
                resetTimer();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Used as a global changing variable for set time.
                setMillis = (Long) parent.getItemAtPosition(position) * 1000;
                //Retains default spinner value for set time.
                setStart = (int) setMillis;
                //Retains remaining value after every set timer pause.
                setPause = setMillis;
                resetTimer();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                numberOfSets = (long) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        progressBar.setOnClickListener(v-> {
            timerBegun++;
            if (!paused) {
                reset.setVisibility(View.INVISIBLE);
                breakStart();
                paused = true;
            } else if (!timerEnded) {
                reset.setVisibility(View.VISIBLE);
                timer.cancel();
                objectAnimator.cancel();
                paused = false;
            } else {
                objectAnimator.cancel();
                resetTimer();
            }
        });

        reset.setOnClickListener(v -> {
            resetTimer();
        });
    }

    //Todo: Create method w/ millis/pause inputs to use on this and setStart.
    public void breakStart() {
        objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", maxProgress, 0);
        objectAnimator.setInterpolator(new LinearInterpolator());

        if (timerBegun <=2) {
            objectAnimator.setDuration(breakMillis);
            objectAnimator.start();
        } else {
            objectAnimator.cancel();
            objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", (int) (breakPause), 0);
            objectAnimator.setInterpolator(new LinearInterpolator());
            objectAnimator.setDuration(breakMillis);
            objectAnimator.start();
        }

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern1 = {0, 1000, 0, 1000, 0, 1000};

            timer = new CountDownTimer(breakMillis, 50) {
                @Override
                public void onTick(long millisUntilFinished) {
                    breakPause = (long) ((int) objectAnimator.getAnimatedValue());
                    breakMillis = millisUntilFinished;

                    timerStart =  ((millisUntilFinished+1000) / 1000);
                    timeLeft.setText(String.valueOf(timerStart));
                }

                @Override
                public void onFinish() {
                    endAnimation = new AlphaAnimation(1.0f, 0.0f);
                    endAnimation.setDuration(300);
                    endAnimation.setStartOffset(20);
                    endAnimation.setRepeatMode(Animation.REVERSE);
                    endAnimation.setRepeatCount(Animation.INFINITE);
                    timerEnded = true;
                    timeLeft.setText("0");

                    if (numberOfSets >0) {
                        resetTimer();
                        setStart();
                        onBreak = false;
                    } else {
                        numberOfSets = 0;
                    }

//                    if (Build.VERSION.SDK_INT >= 26) {
//                        vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
//                    } else {
//                        vibrator.vibrate(pattern1, 0);
//                    }
                }
            }.start();
    }

    public void setStart() {
        objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", maxProgress, 0);
        objectAnimator.setInterpolator(new LinearInterpolator());

        if (timerBegun <=2) {
            objectAnimator.setDuration(setMillis);
            objectAnimator.start();
        } else {
            objectAnimator.cancel();
            objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", (int) (breakPause), 0);
            objectAnimator.setInterpolator(new LinearInterpolator());
            objectAnimator.setDuration(breakMillis);
            objectAnimator.start();
        }

        timer = new CountDownTimer(setMillis, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                setPause = (long) ((int) objectAnimator.getAnimatedValue());
                setMillis = millisUntilFinished;

                timerStart =  ((millisUntilFinished+1000) / 1000);
                timeLeft.setText(String.valueOf(timerStart));
            }

            @Override
            public void onFinish() {
                endAnimation = new AlphaAnimation(1.0f, 0.0f);
                endAnimation.setDuration(300);
                endAnimation.setStartOffset(20);
                endAnimation.setRepeatMode(Animation.REVERSE);
                endAnimation.setRepeatCount(Animation.INFINITE);
                timerEnded = true;
                timeLeft.setText("0");

                //Used ONLY on number of sets.
                numberOfSets--;
                if (numberOfSets >0) {
                    resetTimer();
                    breakStart();
                    onBreak = true;
                } else {
                    numberOfSets = 0;
                }

//                    if (Build.VERSION.SDK_INT >= 26) {
//                        vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
//                    } else {
//                        vibrator.vibrate(pattern1, 0);
//                    }
            }
        }.start();
    }

    public void resetTimer() {
        //Todo: Reset for set timer.
        if (timer != null) {
            timer.cancel();
        }
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        timerBegun = 0;
        paused = false;
        timerEnded = false;
        progressBar.setProgress(10000);

        if (onBreak) {
            breakMillis = breakStart;
            timeLeft.setText(String.valueOf(breakStart/1000));
        } else {
            setMillis = setStart;
            timeLeft.setText(String.valueOf(setStart/1000));
        }
    }
}