package com.example.tragic.irate.simple.stopwatch;

import androidx.appcompat.app.AppCompatActivity;

import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    CircularProgressBar circle;
    CountDownTimer timer;
    boolean timerActive;
    int timerDuration = 5000;
    int actualTimer = 5;
    int currentTime = 0;
    double remainingTime = 0;

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

        circle = findViewById(R.id.circle2);

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

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Long temp = (Long) parent.getItemAtPosition(position) * 1000;
                timerDuration = temp.intValue();
                actualTimer = timerDuration / 1000;
                circle.setTitle(String.valueOf(actualTimer));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        circle.setTitleColor(R.color.white);
//        circle.setSubTitleColor(R.color.white);
//        circle.setShadow(R.color.black);
        circle.setSubTitle("Seconds");

        circle.setOnClickListener(v -> {
            if (!timerActive){
                circle.animateProgressTo(100, 0, timerDuration, new CircularProgressBar.ProgressAnimationListener() {
                    @Override
                    public void onAnimationStart() {
                        setTimer();
                        circle.setSubTitle("Seconds");
                    }

                    @Override
                    public void onAnimationProgress(int progress) {
                    }

                    @Override
                    public void onAnimationFinish() {
                        circle.setSubTitle("Done");
                    }
                });
                timerActive = true;
            } else {
                circle.animateProgressTo(100, 0, timerDuration, new CircularProgressBar.ProgressAnimationListener() {
                    @Override
                    public void onAnimationStart() {
                        setTimer();
                        double temp = ( (double) (currentTime*1000)/ timerDuration) * 100;
                        remainingTime = Double.parseDouble(df.format(temp));
                        circle.setSubTitle("Stopped");
                    }

                    @Override
                    public void onAnimationProgress(int progress) {
                        circle.setProgress((int) remainingTime);
                    }

                    @Override
                    public void onAnimationFinish() {
                        circle.setProgress((int) remainingTime);
                    }
                });
                timerActive = false;
            }
        });
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void setTimer() {
        if (!timerActive) {
            timer = new CountDownTimer(timerDuration + 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    currentTime = (int) millisUntilFinished /1000;
                    circle.setTitle(String.valueOf(currentTime));
                    if (millisUntilFinished <1000) {
                        timerActive = false;
                    }
                }
                @Override
                public void onFinish() {
                }
            }.start();
        } else {
            timer.cancel();
            circle.setTitle(String.valueOf(currentTime));
        }
    }
}