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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

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

        CircularProgressBar circle = findViewById(R.id.circle2);

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

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                circle.setTitle(String.valueOf(parent.getItemAtPosition(position)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        circle.setTitleColor(R.color.black);
        circle.setSubTitleColor(R.color.black);
        circle.setShadow(R.color.black);
        circle.setSubTitle("Really, nothing.");

        circle.setOnClickListener(v -> {
            circle.animateProgressTo(0, 100, 5000, new CircularProgressBar.ProgressAnimationListener() {
                @Override
                public void onAnimationStart() {

                }

                @Override
                public void onAnimationProgress(int progress) {
                    circle.setTitle("Moving" + progress);
                }
                @Override
                public void onAnimationFinish() {
                    circle.setSubTitle("Done");
                }
            });
        });

        new CountDownTimer(timer1, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

            }
        };

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

    }
}