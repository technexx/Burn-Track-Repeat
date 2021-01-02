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

    ObjectAnimator objectAnimator;
    CountDownTimer timer;
    Button reset;
    DecimalFormat df;

    int timerBegun;
    boolean timerEnded;
    boolean paused;

    int timerDuration;
    int defaultSpinner = 60;
    long selectedMillis;
    double pausedMillis;
    int maxProgress = 10000;

    Animation anim;

    long progressStart = 60000;
    long timerStart = 60000;

    long setDuration;
    long numberOfSets;

    ProgressBar progressBar;
    TextView timeLeft;

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
        anim = new AlphaAnimation(1.0f, 0.0f);

        timeLeft.setText(String.valueOf(defaultSpinner));

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
                Long temp = (Long) parent.getItemAtPosition(position) * 1000;
                timerDuration = temp.intValue();
                defaultSpinner = temp.intValue();

                selectedMillis = temp;
                progressStart = temp;

                timeLeft.setText(String.valueOf(timerStart/1000));
                progressBar.setProgress(10000);
                pausedMillis = selectedMillis;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setDuration = (Long) parent.getItemAtPosition(position) * 1000;
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
                restTimer();
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

    public void restTimer() {
        objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", maxProgress, 0);
        objectAnimator.setInterpolator(new LinearInterpolator());

        if (timerBegun <=2) {
            objectAnimator.setDuration(selectedMillis);
            objectAnimator.start();
        } else {
            objectAnimator.cancel();
            objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", (int) (pausedMillis), 0);
            objectAnimator.setInterpolator(new LinearInterpolator());
            objectAnimator.setDuration(selectedMillis);
            objectAnimator.start();
        }

            timer = new CountDownTimer(selectedMillis, 50) {
                @Override
                public void onTick(long millisUntilFinished) {
                    pausedMillis = (long) ((int) objectAnimator.getAnimatedValue());

                    selectedMillis = millisUntilFinished;
                    timerStart =  ((millisUntilFinished+1000) / 1000);
                    timeLeft.setText(String.valueOf(timerStart));
                    Log.i("value", String.valueOf(objectAnimator.getAnimatedValue()) + "+" + objectAnimator.getAnimatedFraction());
                }

                @Override
                public void onFinish() {
                    anim = new AlphaAnimation(1.0f, 0.0f);
                    anim.setDuration(300);
                    anim.setStartOffset(20);
                    anim.setRepeatMode(Animation.REVERSE);
                    anim.setRepeatCount(Animation.INFINITE);
                    timerEnded = true;

                    //Todo: Replace rest timer reset w/ set timer, then finish w/ reset. Also: Add counter for set/rest times.
                    numberOfSets--;
                    if (numberOfSets >0) {
                        resetTimer();
                    } else {
                        numberOfSets = 0;
                    }

                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    long[] pattern = {0, 1000, 0, 1000, 0, 1000};

//                    if (Build.VERSION.SDK_INT >= 26) {
//                        vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
//                    } else {
//                        vibrator.vibrate(pattern, 0);
//                    }
                }
            }.start();
    }

    public void setTimer() {

    }

    public void resetTimer() {
        timer.cancel();
        timerBegun = 0;
        paused = false;
        timerEnded = false;
        anim.cancel();

        timeLeft.setText(String.valueOf(timerDuration/1000));
        progressBar.setProgress(10000);
        selectedMillis = defaultSpinner;
    }
}