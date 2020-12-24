package com.example.tragic.irate.simple.stopwatch;

import androidx.appcompat.app.AppCompatActivity;

import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.WindowManager;
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
//        long timer2 = Long.parseLong((String) spinner2.getSelectedItem());
//        long timer3 = Long.parseLong((String) spinner3.getSelectedItem());


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