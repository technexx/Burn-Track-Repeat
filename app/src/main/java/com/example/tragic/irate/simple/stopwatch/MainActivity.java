package com.example.tragic.irate.simple.stopwatch;

import androidx.appcompat.app.AppCompatActivity;

import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    CircularProgressBar circle;
    CircularProgressBar circlePause;
    CircularProgressBar circleRestart;
    CountDownTimer timer;
    Button reset;
    DecimalFormat df;

    boolean timerStarted;
    boolean timerPaused;
    int timerDuration = 5000;
    int actualTimer = 5;
    int currentTime = 0;
    double remainingTime = 0;
    long pausedMillis = 5000;
    boolean timerEnded;
    int defaultSpinner = 5;

    Animation anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner1 = findViewById(R.id.spin1);
        Spinner spinner2 = findViewById(R.id.spin2);
        Spinner spinner3 = findViewById(R.id.spin3);

        List<Long> spinList1 = new ArrayList<>();
        List<Long> spinList2 = new ArrayList<>();
        List<Long> spinList3 = new ArrayList<>();

        reset = findViewById(R.id.reset);

        circle = findViewById(R.id.circle2);
        circlePause = findViewById(R.id.circle_pause);
        circleRestart = findViewById(R.id.circle_restart);
        circlePause.setVisibility(View.INVISIBLE);
        circleRestart.setVisibility(View.INVISIBLE);
        reset.setVisibility(View.INVISIBLE);

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

        long timer1 = (long) spinner1.getSelectedItem();
        long timer2 = (long) spinner2.getSelectedItem();
        long timer3 = (long) spinner3.getSelectedItem();

        df = new DecimalFormat("");
        anim = new AlphaAnimation(1.0f, 0.0f);
        circle.setProgress(100);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Long temp = (Long) parent.getItemAtPosition(position) * 1000;
                timerDuration = temp.intValue();
                defaultSpinner = temp.intValue();

                actualTimer = timerDuration / 1000;
                circle.setTitle(String.valueOf(actualTimer));
                circlePause.setTitle(String.valueOf(actualTimer));
                pausedMillis = temp;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        circle.setOnClickListener(v -> {
            startTimer();
        });

        circleRestart.setOnClickListener(v-> {
            startTimer();
        });

        circlePause.setOnClickListener(v-> {
            //Resuming
            circlePause.setVisibility(View.INVISIBLE);
            circle.setVisibility(View.VISIBLE);
            circle.setProgress((int) remainingTime);
            circle.animateProgressTo((int) remainingTime, 0, (int) pausedMillis, new CircularProgressBar.ProgressAnimationListener() {
                @Override
                public void onAnimationStart() {
                    double temp = ( (double) (currentTime*1000)/ timerDuration) * 100;
                    remainingTime = Double.parseDouble(df.format(temp));
                }

                @Override
                public void onAnimationProgress(int progress) {
                }

                @Override
                public void onAnimationFinish() {
                }
            });
            timerPaused = false;
            setTimer();
        });

        reset.setOnClickListener(v -> {
            resetTimer();
        });
    }

    public void startTimer() {
        if (!timerStarted){
            circle.setVisibility(View.VISIBLE);
            circlePause.setVisibility(View.INVISIBLE);
            circleRestart.setVisibility(View.INVISIBLE);

            circle.animateProgressTo(100, 0, timerDuration, new CircularProgressBar.ProgressAnimationListener() {
                @Override
                public void onAnimationStart() {
                    circle.setProgress(100);
                    circle.setTitle(String.valueOf(actualTimer));
                    setTimer();
                }

                @Override
                public void onAnimationProgress(int progress) {
                }

                @Override
                public void onAnimationFinish() {
                    if (timerStarted) {
                        circle.setSubTitle("Done");
                    }
                }
            });
            timerStarted = true;
        } else if (timerEnded) {
            resetTimer();
            anim.cancel();
            timerEnded = false;
            timerStarted = false;
        } else {
            //Pausing
            //remainingTime is percentage value left.
            double temp = ( (double) (currentTime*1000)/ timerDuration) * 100;
            remainingTime = Double.parseDouble(df.format(temp));

            reset.setVisibility(View.VISIBLE);
            circle.setVisibility(View.INVISIBLE);
            circlePause.setVisibility(View.VISIBLE);
            circlePause.setProgress((int) remainingTime);
            circlePause.setSubTitle("Paused");
            timerPaused = true;
            setTimer();
        }
    }

    public void setTimer() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 1000, 0, 1000, 0, 1000};

        if (!timerStarted) {
            pausedMillis = defaultSpinner;
        }

        if (!timerPaused) {
            timer = new CountDownTimer(pausedMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    pausedMillis = millisUntilFinished;
                    currentTime = (int) millisUntilFinished /1000;
                    circle.setTitle(String.valueOf(currentTime));
                    circlePause.setTitle(String.valueOf(currentTime));
                }
                @Override
                public void onFinish() {
                    anim = new AlphaAnimation(1.0f, 0.0f);
                    anim.setDuration(300); //You can manage the blinking time with this parameter
                    anim.setStartOffset(20);
                    anim.setRepeatMode(Animation.REVERSE);
                    anim.setRepeatCount(Animation.INFINITE);

                    circle.startAnimation(anim);
                    timerEnded = true;

//                    if (Build.VERSION.SDK_INT >= 26) {
//                        vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
//                    } else {
//                        vibrator.vibrate(pattern, 0);
//                    }
                }
            }.start();
        } else {
            timer.cancel();
        }
    }

    public void resetTimer() {
        circle.setVisibility(View.INVISIBLE);
        circlePause.setVisibility(View.INVISIBLE);
        circleRestart.setVisibility(View.VISIBLE);

        circleRestart.setTitle(String.valueOf(defaultSpinner/1000));
        circleRestart.setProgress(100);

        timerStarted = false;
        timerEnded = false;
        timerPaused = false;
        anim.cancel();
    }
}