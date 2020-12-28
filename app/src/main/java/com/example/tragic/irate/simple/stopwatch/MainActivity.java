package com.example.tragic.irate.simple.stopwatch;

import androidx.appcompat.app.AppCompatActivity;

import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
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
    CountDownTimer timer;
    boolean timerStarted;
    boolean timerPaused;
    int timerDuration = 5000;
    int actualTimer = 5;
    int currentTime = 0;
    double remainingTime = 0;
    long pausedMillis = 5000;

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

        Button reset = findViewById(R.id.reset);

        circle = findViewById(R.id.circle2);
        circlePause = findViewById(R.id.circle_pause);
        circlePause.setVisibility(View.INVISIBLE);

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

        DecimalFormat df = new DecimalFormat("");

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Long temp = (Long) parent.getItemAtPosition(position) * 1000;
                timerDuration = temp.intValue();
                actualTimer = timerDuration / 1000;
                circle.setTitle(String.valueOf(actualTimer));
                circlePause.setTitle(String.valueOf(actualTimer));
                pausedMillis = temp;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        circle.setProgress(100);
//        circle.setTitleColor(R.color.white);
//        circle.setSubTitleColor(R.color.white);
//        circle.setShadow(R.color.black);
//        circlePause.setSubTitle("Seconds");

        circle.setOnClickListener(v -> {
            if (!timerStarted){
                circle.animateProgressTo(100, 0, timerDuration, new CircularProgressBar.ProgressAnimationListener() {
                    @Override
                    public void onAnimationStart() {
                        setTimer();
                    }

                    @Override
                    public void onAnimationProgress(int progress) {
                    }

                    @Override
                    public void onAnimationFinish() {
                        if (timerStarted) {
                            circle.setSubTitle("Done");
                            long[] pattern = {0, 1000, 0, 1000, 0, 1000};
//                            if (Build.VERSION.SDK_INT >= 26) {
//                                vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
//                            } else {
//                                vibrator.vibrate(pattern, 0);
//                            }
                        }
                    }
                });
                timerStarted = true;
            } else {
                //Pausing
                //remainingTime is percentage value left.
                double temp = ( (double) (currentTime*1000)/ timerDuration) * 100;
                remainingTime = Double.parseDouble(df.format(temp));
                circlePause.setSubTitle("Paused");

                circle.setVisibility(View.INVISIBLE);
                circlePause.setVisibility(View.VISIBLE);
                circlePause.setProgress((int) remainingTime);
                timerPaused = true;
                setTimer();
            }
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
                    //Continuously setting (each draw tick) time to stationary value (hacky workaround for progressbar class).
//                            circle.setProgress((int) remainingTime);
                }

                @Override
                public void onAnimationFinish() {
//                            circle.setProgress((int) remainingTime);
                }
            });
            timerPaused = false;
            setTimer();
        });

        reset.setOnClickListener(v -> {
            resetTimer();
        });
    }

    public void setTimer() {
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
                }
            }.start();
        } else {
            timer.cancel();
        }
    }

    public void resetTimer() {
        actualTimer = timerDuration / 1000;
        circle.setTitle(String.valueOf(actualTimer));
        circle.setProgress(100);
    }
}