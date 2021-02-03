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
    TextView timeLeft2;
    TextView timePaused;
    TextView timePaused2;
    CountDownTimer timer;
    CountDownTimer timer2;
    TextView reset;
    ObjectAnimator objectAnimator;
    ObjectAnimator objectAnimator2;
    Animation endAnimation;
    Handler handler;

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

    int PAUSING_TIMER = 1;
    int RESUMING_TIMER = 2;

    int breakCounter;
    int setCounter;
    int pomSetCounter;
    int maxProgress = 10000;

    long breakMillis;
    int breakStart;
    long setMillis;
    int setStart;
    int setPause = 10000;
    int breakPause = 10000;
    int pomPause = 10000;
    int pomStart;
    long pomMillis;
    long pos;
    long setMillisUntilFinished;
    long pomMillisUntilFinished;

    long savedCustomMillis;
    long savedPomMillis;

    int savedCustomProgress;
    int savedPomProgress;

    int firstSpinCount;
    int secondSpinCount;
    int thirdSpinCount;

    long pomMillis1;
    long pomMillis2;
    long pomMillis3;
    int pomStage=1;
    int pomDotCounter=1;

    int customCyclesDone;
    int pomCyclesDone;

    long numberOfSets;
    long numberOfBreaks;
    long savedBreaks;
    long savedSets;

    boolean timerEnded;
    boolean paused;
    boolean onBreak;
    boolean pomEnded;
    boolean timerDisabled;
    boolean customHalted = true;
    boolean pomHalted = true;

    DotDraws dotDraws;
    int fadeDone;
    int mode=1;
    ValueAnimator valueAnimatorDown;
    ValueAnimator valueAnimatorUp;

    ArrayList<Long> customSetTime;
    ArrayList<Long> customBreakTime;
    ArrayList<Long> startCustomSetTime;
    ArrayList<Long> startCustomBreakTime;

    boolean setBegun;
    boolean breakBegun;
    boolean pomBegun;

    //Todo: Possible "Saved Presets" SharedPref for Custom in 4th tab or popup window.
    //Todo: Use separate vars for each Mode, since active timers will need to keep updating each.

    //Todo: Breaks only option.
    //Todo: Add confirmation to reset Pom and its cycles.
    //Todo: Make sure timers run and recall during tab switches.
    //Todo: Selecting from spinners does not extend to black layout outside text.
    //Todo: Add taskbar notification for timers.
    //Todo: Fast pause/resume sometimes borks timer text.
    //Todo: Less blurry +/- icons.
    //Todo: Rename app, of course.
    //Todo: Possible sqlite db for saved presets.
    //Todo: Add onOptionsSelected dots for About, etc.
    //Todo: Add actual stop watch!
    //Todo: timerEnded var MIGHT cause issues w/ different modes, but might not because of resetTimer().

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

        progressBar = findViewById(R.id.progressBar);
        timeLeft = findViewById(R.id.timeLeft);
        timeLeft2 = findViewById(R.id.timeLeft2);
        timePaused = findViewById(R.id.timePaused);
        timePaused2 = findViewById(R.id.timePaused2);
        dotDraws = findViewById(R.id.dotdraws);
        heading = findViewById(R.id.heading);
        heading.setText(R.string.strict);
        timeLeft.setTextSize(90f);
        timePaused.setTextSize(90f);
        timeLeft2.setTextSize(70f);
        timePaused2.setTextSize(70f);
        cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Custom"));
        tabLayout.addTab(tabLayout.newTab().setText("Pomodoro"));

        handler = new Handler();
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
        customSetTime = new ArrayList<>();
        customBreakTime = new ArrayList<>();
        startCustomSetTime = new ArrayList<>();
        startCustomBreakTime = new ArrayList<>();
        s3.setText(R.string.set_number);
        spinner3.setVisibility(View.GONE);
        timeLeft2.setVisibility(View.GONE);
        timePaused.setVisibility(View.GONE);
        timePaused2.setVisibility(View.GONE);

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

        //Default list values for Custom.
        for (int i=0; i<3; i++) {
            customSetTime.add(spinList1.get(5) * 1000);
            customBreakTime.add(spinList2.get(8) * 1000);
            startCustomSetTime.add(spinList1.get(5) * 1000);
            startCustomBreakTime.add(spinList2.get(8) * 1000);
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

        //Default spinner values for Custom
        spinner1.setSelection(0);
        spinner2.setSelection(0);
        spinner3.setSelection(2);

        //Elements here are retrieved as spinner positions via retrieveSpins(), and therefore must be <= than the number of spinner entries.
        modeTwoSpins.add(0, 2);
        modeTwoSpins.add(1, 2);
        modeTwoSpins.add(2, 2);

        valueAnimatorDown = new ValueAnimator().ofFloat(90f, 70f);
        valueAnimatorDown.setDuration(500);
        valueAnimatorUp = new ValueAnimator().ofFloat(70f, 90f);
        valueAnimatorUp.setDuration(500);

        //Custom defaults
        mode = 1;
        dotDraws.setMode(1);
        savedSets = customSetTime.size();
        savedBreaks = customBreakTime.size();
        numberOfSets = savedSets;
        numberOfBreaks = savedBreaks;

        //Pom defaults
        pomMillis = pomList1.get(2)*60000;

        savedCustomProgress = maxProgress;
        savedPomProgress = maxProgress;
        savedCustomMillis = startCustomSetTime.get(startCustomSetTime.size()-1);
        savedPomMillis = pomMillis;
        resetTimer();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        mode=1;
                        heading.setText(R.string.variable);
                        changeTextSize(valueAnimatorUp);
                        dotDraws.setMode(1);
                        dotDraws.newDraw(savedSets, savedBreaks, 0, 0, 0);
                        cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));

                        retrieveSpins(modeOneSpins, false);
                        paused = customHalted;
                        switchTimer(1, paused);
                        break;
                    case 1:
                        mode=2;
                        heading.setText(R.string.pomodoro);
                        changeTextSize(valueAnimatorDown);
                        dotDraws.setMode(2);
                        dotDraws.pomDraw(1, 0);
                        cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(pomCyclesDone)));

                        retrieveSpins(modeTwoSpins, true);
                        paused = pomHalted;
                        switchTimer(2, paused);
                }
                Log.i("halted", "custom halted is " + customHalted + " and pom halted is " + pomHalted);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        customHalted = paused; break;
                    case 1:
                        pomHalted = paused;
                }
                firstSpinCount = 0;
                secondSpinCount = 0;
                thirdSpinCount = 0;
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                firstSpinCount++;
                if (mode == 1) {
                    if (customSetTime.size() >0) {
                        setMillis = customSetTime.get(customSetTime.size()-1);
                        setStart = (int) setMillis;
                    }
                } else {
                    pos = pomList1.get(position);
                    pomMillis1 = pos*60000;
                    setStart = (int) pomMillis1;
                }
                if (firstSpinCount >1) {
                    saveSpins();
                    resetTimer();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                secondSpinCount++;
                if (mode == 1) {
                    if (customBreakTime.size() >0) {
                        breakMillis = customBreakTime.get(customBreakTime.size()-1);
                    }
                } else {
                    pos = pomList2.get(position);
                    pomMillis2 = pos*60000;
                }
                if (secondSpinCount >1) {
                    saveSpins();
                    resetTimer();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                thirdSpinCount++;
                if (mode == 2) {
                    long pos2 = pomList3.get(position);
                    pomMillis3 = pos2*60000;
                }
                if (thirdSpinCount >1) {
                    saveSpins();
                    resetTimer();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        progressBar.setOnClickListener(v-> {
            if (!paused) {
                pauseAndResumeTimer(PAUSING_TIMER);
            } else {
                pauseAndResumeTimer(RESUMING_TIMER);
            }
        });

        cycle_reset.setOnClickListener(v -> {
            switch (mode) {
                case 1:
                    customCyclesDone = 0; break;
                case 2:
                    pomCyclesDone = 0;
            }
            cycles_completed.setText(getString(R.string.cycles_done, "0"));
        });

        skip.setOnClickListener(v -> {
            if (mode == 1) {
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
                    paused = true;
                    progressBar.setProgress(10000);

                    if (customSetTime.size() >0 && customSetTime.size() == customBreakTime.size()) {
                        customSetTime.remove(customSetTime.size()-1);
                        numberOfSets--;
                    }
                    if (customBreakTime.size() >0 && customBreakTime.size() != customSetTime.size()) {
                        customBreakTime.remove(customBreakTime.size()-1);
                        numberOfBreaks--;
                        onBreak = false;
                    }
//
//                    //Todo: For "breaks only":
                    timeLeft.setText(convertSeconds((customSetTime.get(customSetTime.size()-1)+999)/1000));
//                        timeLeft.setText(convertSeconds((breakStart+999)/1000));
                } else if (numberOfSets == 0 && numberOfBreaks == 0) {
                    customCyclesDone++;
                    cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));
                    endAnimation();
                    progressBar.setProgress(0);
                    timeLeft.setText("0");
                    timerEnded = true;
                    paused = false;
                }
                if (numberOfSets >=0 && numberOfBreaks >=0) {
                    //Beginning set/break count so dots fade instead of disappear.
                    dotDraws.setTime(startCustomSetTime);
                    dotDraws.breakTime(startCustomBreakTime);
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
            resetTimer();
        });
    }

    public void startObjectAnimator() {
        removeViews();
        fadeDone = 1;
        switch (mode) {
            case 1:
                timeLeft.setVisibility(View.VISIBLE);
                if (!setBegun) {
                    objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", (int) maxProgress, 0);
                    if (customSetTime.size()>0) setMillis = customSetTime.get(customSetTime.size() -1);
                    objectAnimator.setInterpolator(new LinearInterpolator());
                    objectAnimator.setDuration(setMillis);
                    objectAnimator.start();
                    setBegun = true;
                } else {
                    if (objectAnimator!=null) objectAnimator.resume();
                }
                break;
            case 2:
                timeLeft2.setVisibility(View.VISIBLE);
                if (!pomBegun) {
                    objectAnimator2 = ObjectAnimator.ofInt(progressBar, "progress", (int) maxProgress, 0);
                    objectAnimator2.setInterpolator(new LinearInterpolator());
                    objectAnimator2.setDuration(pomMillis);
                    objectAnimator2.start();
                    pomBegun = false;
                } else {
                    if (objectAnimator2!=null) objectAnimator2.resume();
                }
                break;
        }
//        dotDraws.setAlpha();
    }

    public void startTimer() {
        if (setBegun) setMillis = setMillisUntilFinished;
        switch (mode) {
            case 1:
                //Todo: Move saved setMillis back into timer @ !notBegun. Might need sep var from setMillis since it is also used above.
                timer = new CountDownTimer(setMillis, 50) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        setPause = (int) objectAnimator.getAnimatedValue();
                        setMillis = millisUntilFinished;
                        timeLeft.setText(convertSeconds((setMillis + 999)/1000));
                        boolean fadePaused = false;

                        if (fadeDone == 1) {
                            if (!fadePaused) {
                                dotDraws.newDraw(savedSets, savedBreaks, savedSets- (numberOfSets -1), savedBreaks- (numberOfBreaks-1), fadeDone);
                                fadePaused = true;
                            } else {
                                //fadeDone value does not matter here.
                                dotDraws.newDraw(savedSets, savedBreaks, savedSets- numberOfSets, savedBreaks-numberOfBreaks, 1);
                                fadePaused = false;
                            }
                        }
                    }

                    @Override
                    public void onFinish() {
                        fadeDone = 2;
                        numberOfSets--;
                        timeLeft2.setText("0");
                        setCounter = 0;
                        //Ensures first click of next break triggers pause.
                        if (customSetTime.size() > 0) {
                            customSetTime.remove(customSetTime.size() - 1);
                        }

                        breakPause = maxProgress;
                        breakMillis = breakStart;
                        onBreak = true;
                        timerEnded = false;
                        endAnimation();
                        handler.postDelayed((Runnable) () -> {
                            breakStart();
                            endAnimation.cancel();
                        },750);
                        endAnimation();
                        dotDraws.setTime(startCustomSetTime);
                        dotDraws.breakTime(startCustomBreakTime);
                    }
                }.start();
                break;
            case 2:
                timer2 = new CountDownTimer(pomMillis, 50) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        pomMillis = (int) objectAnimator2.getAnimatedValue();
//                        saveValues(setMillis, setPause);
                        pomMillis = millisUntilFinished;
                        pomMillisUntilFinished = pomMillis;
                        timeLeft2.setText(convertSeconds((pomMillisUntilFinished + 999)/1000));
                        dotDraws.pomDraw(pomDotCounter, 1);
                    }

                    @Override
                    public void onFinish() {
                        fadeDone = 2;
                        numberOfSets--;
                        timeLeft2.setText("0");
                        setCounter = 0;

                        switch (pomDotCounter) {
                            case 2: case 4: case 6:
                                pomMillis = pomMillis2;
                                break;
                            case 7:
                                pomMillis = pomMillis3;
                        }
                        pomDotCounter++;
                        if (pomDotCounter <8) {
//                            setStart();
                            startObjectAnimator();
                        } else {
                            pomEnded = true;
                            pomDotCounter = 1;
                            pomCyclesDone +=1;
                            endAnimation();
                            cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(pomCyclesDone)));

                            endAnimation();
                            dotDraws.setTime(startCustomSetTime);
                            dotDraws.breakTime(startCustomBreakTime);
                        }
                    }
                }.start();
                break;
        }
    }

    public void breakStart() {
        objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", maxProgress, 0);
        objectAnimator.setInterpolator(new LinearInterpolator());

        if (breakCounter <=1) {
            if (customBreakTime.size()>0) breakMillis = customBreakTime.get(customBreakTime.size() -1);
        }

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
                breakPause = (int) objectAnimator.getAnimatedValue();
                breakMillis = millisUntilFinished;
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
                numberOfBreaks--;
                timeLeft.setText("0");
                //Ensures first click of next break triggers pause.

                    if (customBreakTime.size() >0) {
                        customBreakTime.remove(customBreakTime.size()-1);
                    }
                    dotDraws.setTime(startCustomSetTime);
                    dotDraws.breakTime(startCustomBreakTime);

                endAnimation();
                if (numberOfBreaks >0) {
                    breakCounter = 0;
                    setPause = maxProgress;
                    setMillis = setStart;
                    onBreak = false;
                    timerEnded = false;

                    handler.postDelayed((Runnable) () -> {
//                        setStart();
                        startObjectAnimator();
                        endAnimation.cancel();
                    },750);
                } else {
                    customCyclesDone++;
                    cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));
                    endAnimation();
                    dotDraws.newDraw(savedSets, savedBreaks, savedSets- (numberOfSets -1), savedBreaks- (numberOfBreaks-1), fadeDone);
                    timerEnded = true;
                }

//                    if (Build.VERSION.SDK_INT >= 26) {
//                        vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
//                    } else {
//                        vibrator.vibrate(pattern1, 0);
//                    }
            }
        }.start();
    }

    public String convertSeconds(long totalSeconds) {
        DecimalFormat df = new DecimalFormat("00");
        long minutes;
        long remainingSeconds;

        if (totalSeconds >=60) {
            minutes = totalSeconds/60;
            remainingSeconds = totalSeconds % 60;
            return (minutes + ":" + df.format(remainingSeconds));
        } else if (totalSeconds != 5) return String.valueOf(totalSeconds);
        else return "5";
    }

    public void adjustCustom(boolean adding) {
        if (adding) {
            if (customSetTime.size() < 10 && customSetTime.size() >= 0) {
                customSetTime.add(spinList1.get(spinner1.getSelectedItemPosition()) * 1000);
                startCustomSetTime.add(spinList1.get(spinner1.getSelectedItemPosition()) * 1000);
            } else {
                Toast.makeText(getApplicationContext(), "Max sets reached!", Toast.LENGTH_SHORT).show();
            }
            if (customBreakTime.size() < 10 && customBreakTime.size() >= 0) {
                customBreakTime.add(spinList2.get(spinner2.getSelectedItemPosition()) * 1000);
                startCustomBreakTime.add(spinList2.get(spinner2.getSelectedItemPosition()) * 1000);
            }
        } else {
            if (customSetTime.size() > 0 && customSetTime.size() > 0) {
                customSetTime.remove(customSetTime.size() - 1);
                startCustomSetTime.remove(startCustomSetTime.size() - 1);
            } else {
                Toast.makeText(getApplicationContext(), "Nothing left to remove!", Toast.LENGTH_SHORT).show();
            }
            if (customBreakTime.size() > 0 && customBreakTime.size() > 0) {
                customBreakTime.remove(customBreakTime.size() - 1);
                startCustomBreakTime.remove(startCustomBreakTime.size() - 1);

            }
        }
        Log.i("custom", "set start is " + startCustomSetTime + " and remaining sets are " + customSetTime);

        resetTimer();
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

    public void saveSpins() {
        switch (mode) {
            case 1:
                if (modeOneSpins.size() >=3) {
                    modeOneSpins.set(0, spinner1.getSelectedItemPosition());
                    modeOneSpins.set(1, spinner2.getSelectedItemPosition());
                    modeOneSpins.set(2, (int) customSetTime.size());              }
                break;
            case 2:
                if (modeTwoSpins.size() >=3) {
                    modeTwoSpins.set(0, spinner1.getSelectedItemPosition());
                    modeTwoSpins.set(1, spinner2.getSelectedItemPosition());
                    modeTwoSpins.set(2, spinner3.getSelectedItemPosition());
                }
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
            spinner3.setSelection(spinList.get(2));
        }
    }

    //Todo: Tabbing back resets the timer/progressBar after onClick.
    public void switchTimer(int mode, boolean halted) {
        removeViews();
        switch (mode) {
            case 1:
                plus_sign.setVisibility(View.VISIBLE);
                minus_sign.setVisibility(View.VISIBLE);
                spinner3.setVisibility(View.GONE);
                s1.setText(R.string.set_time);
                s2.setText(R.string.break_time);
                if (setMillisUntilFinished==0) setMillisUntilFinished = setMillis;
                if (halted) {
                    timePaused.setVisibility(View.VISIBLE);
                    timePaused.setText(convertSeconds((setMillis + 999)/1000));
                } else {
                    startObjectAnimator();
                    timeLeft.setVisibility(View.VISIBLE);
                    timeLeft.setText(convertSeconds((setMillis + 999)/1000));
                }
                break;

            case 2:
                plus_sign.setVisibility(View.GONE);
                minus_sign.setVisibility(View.GONE);
                spinner3.setVisibility(View.VISIBLE);
                s1.setText(R.string.work_time);
                s2.setText(R.string.small_break);
                s3.setText(R.string.long_break);
                if (pomMillisUntilFinished==0) pomMillisUntilFinished = pomMillis;
                if (halted) {
                    timePaused2.setVisibility(View.VISIBLE);
                    timePaused2.setText(convertSeconds((pomMillisUntilFinished + 999)/1000));

                } else {
                    startObjectAnimator();
                    timeLeft2.setVisibility(View.VISIBLE);
                    timeLeft2.setText(convertSeconds((pomMillisUntilFinished + 999)/1000));
                }
                break;
        }
    }

    public void removeViews() {
        timePaused.setVisibility(View.GONE);
        timePaused2.setVisibility(View.GONE);
        timeLeft.setVisibility(View.GONE);
        timeLeft2.setVisibility(View.GONE);
    }

    public void pauseAndResumeTimer(int pausing) {
        if (onBreak) {
            breakCounter++;
        } else {
            if (mode==1) setCounter++; else pomSetCounter++;
        }

        if (!timerDisabled) {
            if (!timerEnded) {
                if (pausing == PAUSING_TIMER) {
                    switch (mode) {
                        case 1:
                            setMillisUntilFinished = setMillis;
                            if (objectAnimator!=null) objectAnimator.pause();
                            if (timer!=null) timer.cancel();
                            String pausedTime = (convertSeconds((setMillisUntilFinished + 999)/1000));
                            timePaused.setVisibility(View.VISIBLE);
                            timePaused.setText(pausedTime);
                            break;
                        case 2:
                            if (objectAnimator2!=null) objectAnimator2.pause();
                            if (timer2!=null) timer2.cancel();;
                            String pausedTime2 = (convertSeconds((pomMillisUntilFinished + 999)/1000));
                            timePaused2.setVisibility(View.VISIBLE);
                            timePaused2.setText(pausedTime2);
                            break;
                    }
                    reset.setVisibility(View.VISIBLE);
                    paused = true;
                } else if (pausing == RESUMING_TIMER) {
                    startTimer();
                    if (onBreak) {
                        breakStart();
                    } else {
                        startObjectAnimator();
                    }
                    reset.setVisibility(View.INVISIBLE);
                    paused = false;
                }
            } else {
                resetTimer();
                if (endAnimation != null) endAnimation.cancel();
            }
        } else {
            Toast.makeText(getApplicationContext(), "What are we timing?", Toast.LENGTH_SHORT).show();
        }
    }

    public void resetTimer() {
        removeViews();
        timerEnded = false;
        setPause = setStart;
        breakPause = breakStart;

        progressBar.setProgress(10000);
        numberOfSets = savedSets;
        numberOfBreaks = savedBreaks;
        paused = true;

        if (endAnimation != null) endAnimation.cancel();

        switch (mode) {
            case 1:
                setBegun = false;
                if (timer != null) timer.cancel();
                if (objectAnimator != null) objectAnimator.cancel();
                if (startCustomSetTime.size() >0 && startCustomBreakTime.size() >0) {
                    setMillis = startCustomSetTime.get(startCustomSetTime.size()-1);
                    breakMillis = startCustomBreakTime.get(startCustomBreakTime.size()-1);
                    timeLeft.setText(convertSeconds((setMillis+999)/1000));
                    timeLeft2.setText(convertSeconds((setMillis+999)/1000));
                }
                timeLeft.setVisibility(View.VISIBLE);
                onBreak = false;

                numberOfSets = startCustomSetTime.size();
                numberOfBreaks = startCustomBreakTime.size();
                savedSets = numberOfSets;
                savedBreaks = numberOfBreaks;

                customSetTime = new ArrayList<>();
                customBreakTime = new ArrayList<>();

                if (startCustomSetTime.size() >0) {
                    for (int i=0; i<startCustomSetTime.size(); i++) {
                        customSetTime.add(startCustomSetTime.get(i));
                        customBreakTime.add(startCustomBreakTime.get(i));
                    }
                    timerDisabled = false;
                } else {
                    timerDisabled = true;
                }
                dotDraws.setTime(customSetTime);
                dotDraws.breakTime(customBreakTime);
                setCounter = 0;
                breakCounter = 0;

                savedCustomMillis = setMillis;
                break;
            case 2:
                if (timer2 != null) timer2.cancel();
                if (objectAnimator2 != null) objectAnimator2.cancel();
                timeLeft2.setVisibility(View.VISIBLE);
                timeLeft2.setText(convertSeconds((pomMillis1+999)/1000));
                timePaused2.setText(convertSeconds((pomMillis1+999)/1000));
                pomSetCounter =0;
                onBreak = false;
                savedPomMillis = pomMillis1;
                savedPomProgress = 10000;
        }
        dotDraws.newDraw(savedSets, savedBreaks, 0, 0, 0);
    }
}