package com.example.tragic.irate.simple.stopwatch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.tabs.TabLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button breaks_only;
    ProgressBar progressBar;
    ProgressBar progressBar2;
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

    long breakMillis;
    int breakStart;
    long setMillis;
    int setStart;
    int maxProgress = 10000;
    int customProgressPause = 10000;
    int breaksOnlyPause = 10000;
    int pomProgressPause = 10000;
    long pomMillis;
    long pos;
    long setMillisUntilFinished;
    long breakMillisUntilFinished;
    long pomMillisUntilFinished;

    int firstSpinCount;
    int secondSpinCount;
    int thirdSpinCount;

    long pomMillis1;
    long pomMillis2;
    long pomMillis3;
    int pomDotCounter=1;

    int customCyclesDone;
    int breaksOnlyCyclesDone;
    int pomCyclesDone;

    long numberOfSets;
    long numberOfBreaks;
    long savedBreaks;
    long savedSets;

    boolean customTimerEnded;
    boolean pomTimerEnded;
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
    ArrayList<Long> startBreaksOnlyTime;
    ArrayList<Long> breaksOnlyTime;

    boolean setBegun;
    boolean breakBegun;
    boolean pomBegun;
    boolean drawing = true;
    boolean breaksOnly;

    PopupWindow cyclePopupWindow;
    PopupWindow resetPopUpWindow;

    boolean fadeCustomTimer;
    boolean fadePomTimer;
    float customAlpha;
    float pomAlpha;
    boolean alphaSet;
    ObjectAnimator fadeInObj;
    ObjectAnimator fadeOutObj;

    //Todo: Test skip cycles a bit more. Add second cycles completed.
    //Todo: Separate spinner2 value saves for breaksOnly.
    //Todo: Selecting from spinners does not extend to black layout outside text.
    //Todo: Smooth transition between tab timer textviews.
    //Todo: Smaller click radius for progressBar.
    //Todo: Add taskbar notification for timers.
    //Todo: Add color scheme options.
    //Todo: Less blurry +/- icons.
    //Todo: Rename app, of course.
    //Todo: Add onOptionsSelected dots for About, etc.
    //Todo: Add actual stop watch!

    //Todo: Possible "Saved Presets" SharedPref for Custom in 4th tab or popup window.
    //Todo: Possible: Save sets/breaks completed on any given day. Add weight/resistance to each.
    //Todo: Use separate vars for each Mode, since active timers will need to keep updating each.

//    @Override
//    public boolean onCreateOptionsMenu(final Menu menu) {
//        MenuInflater inflater = getMenuInflater();ac
//        inflater.inflate(R.menu.options_menu, menu);
//        return true;
//    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.breaks_only:

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_bar);

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

        breaks_only = findViewById(R.id.breaks_only);
        progressBar = findViewById(R.id.progressBar);
        progressBar2 = findViewById(R.id.progressBar2);
        timeLeft = findViewById(R.id.timeLeft);
        timeLeft2 = findViewById(R.id.timeLeft2);
        timePaused = findViewById(R.id.timePaused);
        timePaused2 = findViewById(R.id.timePaused2);
        dotDraws = findViewById(R.id.dotdraws);
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
        breaksOnlyTime = new ArrayList<>();
        startBreaksOnlyTime = new ArrayList<>();
        s3.setText(R.string.set_number);
        spinner3.setVisibility(View.GONE);
        timeLeft.setAlpha(0);
        timePaused2.setAlpha(0);
        timeLeft2.setAlpha(0);
        progressBar2.setVisibility(View.GONE);

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
            breaksOnlyTime.add(spinList2.get(8) * 1000);
            startBreaksOnlyTime.add(spinList2.get(8) * 1000);
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
        valueAnimatorUp = new ValueAnimator().ofFloat(70f, 90f);
        valueAnimatorDown.setDuration(1000);
        valueAnimatorUp.setDuration(1000);

        //Custom defaults
        mode = 1;
        dotDraws.setMode(1);
        savedSets = customSetTime.size();
        savedBreaks = customBreakTime.size();
        numberOfSets = savedSets;
        numberOfBreaks = savedBreaks;

        //Pom defaults
        pomMillis = pomList1.get(2)*60000;

        progressBar.setProgress(maxProgress);
        progressBar2.setProgress(maxProgress);
        resetTimer();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        mode=1;
                        switchTimer(1, customHalted);
                        changeTextSize(valueAnimatorUp, timeLeft, timePaused);
                        dotDraws.setMode(1);
                        if (!setBegun) dotDraws.newDraw(savedSets, savedBreaks, 0, 0, 0);
                        retrieveSpins(modeOneSpins, false);
                        break;
                    case 1:
                        mode=2;
                        switchTimer(2, pomHalted);
                        retrieveSpins(modeTwoSpins, true);
                        changeTextSize(valueAnimatorDown, timeLeft2, timePaused2);
                        dotDraws.setMode(2);
                        dotDraws.pomDraw(1, 0);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                firstSpinCount = 0;
                secondSpinCount = 0;
                thirdSpinCount = 0;
                switch (tab.getPosition()) {
                    case 0:
                        if (customHalted) fadeOutText(timePaused); else fadeOutText(timeLeft);
                        break;
                    case 1:
                        if (pomHalted) fadeOutText(timePaused2); else fadeOutText(timeLeft2);
                        if (cyclePopupWindow!=null) cyclePopupWindow.dismiss();
                        if (resetPopUpWindow!=null) resetPopUpWindow.dismiss();
                        break;
                }
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
                    if (!breaksOnly) {
                        if (customBreakTime.size()>0) breakMillis = customBreakTime.get(customBreakTime.size()-1);
                    } else {
                        if (breaksOnlyTime.size()>0) breakMillis = breaksOnlyTime.get(breaksOnlyTime.size()-1);
                    }
                    breakStart = (int) breakMillis;
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

        breaks_only.setOnClickListener(v-> {
            if (!breaksOnly) {
                setBegun = true;
                breaksOnly = true;
                onBreak = true;
                breaks_only.setBackgroundColor(getResources().getColor(R.color.light_grey));
                dotDraws.breaksOnly(true);
                spinner1.setVisibility(View.GONE);
                blank_spinner.setVisibility(View.VISIBLE);
                if (breaksOnlyTime.size()>0) {
                    timeLeft.setText(convertSeconds((breaksOnlyTime.get(breaksOnlyTime.size() - 1) +999) / 1000));
                    timePaused.setText(convertSeconds((breaksOnlyTime.get(breaksOnlyTime.size() - 1) +999) / 1000));
                }
                cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(breaksOnlyCyclesDone)));
            } else {
                setBegun = false;
                breaksOnly = false;
                breaks_only.setBackgroundColor(getResources().getColor(R.color.black));
                dotDraws.breaksOnly(false);
                spinner1.setVisibility(View.VISIBLE);
                blank_spinner.setVisibility(View.GONE);
                if (customBreakTime.size()>0) {
                    timeLeft.setText(convertSeconds((customBreakTime.get(customBreakTime.size() - 1) +999) / 1000));
                    timePaused.setText(convertSeconds((customBreakTime.get(customBreakTime.size() - 1) +999) / 1000));
                }
                cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));
            }
            if (mode==1) {
                dotDraws.newDraw(savedSets, savedBreaks, savedSets-(numberOfSets-1), savedBreaks-(numberOfBreaks-1), 1);
            }
            resetTimer();
        });

        progressBar.setOnClickListener(v-> {
            if (!customHalted) {
                pauseAndResumeTimer(PAUSING_TIMER);
            } else {
                pauseAndResumeTimer(RESUMING_TIMER);
            }

        });

        progressBar2.setOnClickListener(v-> {
            if (!pomHalted) {
                pauseAndResumeTimer(PAUSING_TIMER);
            } else {
                pauseAndResumeTimer(RESUMING_TIMER);
            }
        });

        cycle_reset.setOnClickListener(v -> {
            switch (mode) {
                case 1:if (!breaksOnly) customCyclesDone = 0; else breaksOnlyCyclesDone = 0; break;
                case 2:
                    if (pomCyclesDone >=0) {

                        cycle_reset.setVisibility(View.GONE);
                        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                        View view = inflater.inflate(R.layout.pom_reset_popup, null);
                        cyclePopupWindow  = new PopupWindow(view, 150, WindowManager.LayoutParams.WRAP_CONTENT, false);
                        cyclePopupWindow.showAtLocation(view, Gravity.CENTER, 400, 475);

                        TextView confirm_reset = view.findViewById(R.id.pom_reset_text);
                        confirm_reset.setGravity(Gravity.CENTER_HORIZONTAL);
                        confirm_reset.setText(R.string.pom_cycle_reset);

                        ConstraintLayout cl = findViewById(R.id.main_layout);
                        cl.setOnClickListener(v2-> {
                            cyclePopupWindow.dismiss();
                            cycle_reset.setVisibility(View.VISIBLE);
                        });

                        confirm_reset.setOnClickListener(v2-> {
                            pomCyclesDone = 0;
                            cycle_reset.setVisibility(View.VISIBLE);
                            cycles_completed.setText(getString(R.string.cycles_done, "0"));
                            cyclePopupWindow.dismiss();;
                        });
                    }
            }
        });

        skip.setOnClickListener(v -> {
            if (customSetTime.size()>0) {
                customHalted = true;
                int oldCycle = 0;
                int oldCycle2 = 0;
                if (mode == 1) {
                    if (timer != null) {
                        timer.cancel();
                    }
                    if (objectAnimator != null) {
                        objectAnimator.cancel();
                    }
                    if (numberOfSets >0 && numberOfBreaks >0) {
                        setBegun = false;
                        customProgressPause = maxProgress;
                        timePaused.setAlpha(1);
                        timeLeft.setAlpha(0);
                        progressBar.setProgress(10000);

                        if (!breaksOnly) {
                            if (customSetTime.size() >0 && customSetTime.size() == customBreakTime.size()) {
                                customSetTime.remove(customSetTime.size()-1);
                                numberOfSets--;
                            }
                            if (customBreakTime.size() >0 && customBreakTime.size() != customSetTime.size()) {
                                customBreakTime.remove(customBreakTime.size()-1);
                                numberOfBreaks--;
                                onBreak = false;
                                oldCycle = customCyclesDone;
                            }
                            if (customSetTime.size() >0) timePaused.setText(convertSeconds((customSetTime.get(customSetTime.size()-1)+999)/1000));
                        } else {
                            if (breaksOnlyTime.size() >0) {
                                breaksOnlyTime.remove(breaksOnlyTime.size()-1);
                                numberOfBreaks--;
                                oldCycle2 = breaksOnlyCyclesDone;
                                breakBegun = false;
                            }
                            if (breaksOnlyTime.size() >0) {
                                timeLeft.setText(convertSeconds((breaksOnlyTime.get(breaksOnlyTime.size() - 1) +999) / 1000));
                                timePaused.setText(convertSeconds((breaksOnlyTime.get(breaksOnlyTime.size()-1)+999)/1000));
                            }
                        }
                    }
                    if (numberOfBreaks == 0) {
                        timePaused.setAlpha(0);
                        timeLeft.setAlpha(1);
                        if (!breaksOnly) {
                            if (oldCycle == customCyclesDone) {
                                customCyclesDone++;
                                cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));
                            }
                        } else {
                            if (oldCycle2 == breaksOnlyCyclesDone) {
                                breaksOnlyCyclesDone++;
                                cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(breaksOnlyCyclesDone)));
                            }
                        }
                        Log.i("cycles", "custom are " + customCyclesDone + " and breaksOnly are " + breaksOnlyCyclesDone);
                        endAnimation();
                        progressBar.setProgress(0);
                        timeLeft.setText("0");
                        timePaused.setText("0");
                        customTimerEnded = true;
                        paused = false;
                    }
                    if (numberOfBreaks >=0) {
                        if (!breaksOnly) {
                            dotDraws.breakTime(startCustomBreakTime);
                            dotDraws.setTime(startCustomSetTime);
                        } else{
                            dotDraws.breakTime(startBreaksOnlyTime);
                        }
                        dotDraws.newDraw(savedSets, savedBreaks, savedSets- (numberOfSets -1), savedBreaks- (numberOfBreaks-1), 0);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Cannot skip Pomodoro cycles!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        plus_sign.setOnClickListener(v -> {
            adjustCustom(true);
        });

        minus_sign.setOnClickListener(v -> {
            adjustCustom(false);
        });

        reset.setOnClickListener(v -> {
            reset.setVisibility(View.GONE);
            if (mode!=2) {
                resetTimer();
            } else {
                LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View view = inflater.inflate(R.layout.pom_reset_popup, null);
                resetPopUpWindow  = new PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT, 75, false);
                resetPopUpWindow.showAtLocation(view, Gravity.CENTER, 0, 900);

                TextView confirm_reset = view.findViewById(R.id.pom_reset_text);
                confirm_reset.setText(R.string.pom_reset);
                confirm_reset.setOnClickListener(v2-> {
                    resetTimer();
                    resetPopUpWindow.dismiss();
                });

                ConstraintLayout cl = findViewById(R.id.main_layout);
                cl.setOnClickListener(v2-> {
                    resetPopUpWindow.dismiss();
                    reset.setVisibility(View.VISIBLE);
                });
            }

        });
    }

    public void startObjectAnimator() {
        switch (mode) {
            case 1:
                fadeDone = 1;
                if (!onBreak) {
                    if (!setBegun) {
                        if (customSetTime.size()>0) setMillis = customSetTime.get(customSetTime.size() -1);
                        objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", (int) customProgressPause, 0);
                        objectAnimator.setInterpolator(new LinearInterpolator());
                        objectAnimator.setDuration(setMillis);
                        objectAnimator.start();
                    } else {
                        setMillis = setMillisUntilFinished;
                        if (objectAnimator!=null) objectAnimator.resume();
                    }
                } else {
                    fadeDone = 2;
                    if (!breakBegun) {
                        if (!breaksOnly) {
                            if (customBreakTime.size()>0) breakMillis = customBreakTime.get(customBreakTime.size() -1);
                        } else {
                            if (breaksOnlyTime.size()>0) breakMillis = breaksOnlyTime.get(breaksOnlyTime.size()-1);
                        }
                        objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", (int) customProgressPause, 0);
                        objectAnimator.setInterpolator(new LinearInterpolator());
                        objectAnimator.setDuration(breakMillis);
                        objectAnimator.start();
                        breakBegun = true;
                    } else {
                        breakMillis = breakMillisUntilFinished;
                        if (objectAnimator!=null) objectAnimator.resume();
                    }
                }
                break;
            case 2:
                fadeDone = 3;
                if (!pomBegun) {
                    objectAnimator2 = ObjectAnimator.ofInt(progressBar2, "progress", (int) pomProgressPause, 0);
                    objectAnimator2.setInterpolator(new LinearInterpolator());
                    objectAnimator2.setDuration(pomMillis);
                    objectAnimator2.start();
                    pomBegun = true;
                } else {
                    pomMillis = pomMillisUntilFinished;
                    if (objectAnimator2!=null) objectAnimator2.resume();
                }
                break;
        }
    }

    public void startTimer() {
        switch (mode) {
            case 1:
                setBegun = true;
                timeLeft.setText(convertSeconds((setMillis + 999)/1000));
                timer = new CountDownTimer(setMillis, 50) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        customProgressPause = (int) objectAnimator.getAnimatedValue();
                        setMillis = millisUntilFinished;

                        if (fadeCustomTimer) {
                            if (customAlpha<0.25) timeLeft.setAlpha(customAlpha+=0.04);
                            else if (customAlpha<0.5) timeLeft.setAlpha(customAlpha+=.08);
                            else timeLeft.setAlpha(customAlpha+=.12);
                        }
                        if (customAlpha >=1) fadeCustomTimer = false;

                        timeLeft.setText(convertSeconds((setMillis + 999)/1000));
                        if (drawing) {
                            dotDraws.newDraw(savedSets, savedBreaks, savedSets- (numberOfSets -1), savedBreaks- (numberOfBreaks-1), 1);
                        }
                    }

                    @Override
                    public void onFinish() {
                        onBreak = true;
                        fadeDone = 0;
                        numberOfSets--;
                        timeLeft.setText("0");
                        customProgressPause = maxProgress;
                        if (customSetTime.size() > 0) {
                            customSetTime.remove(customSetTime.size() - 1);
                        }
                        if (customSetTime.size()>0) setMillis = customSetTime.get(customSetTime.size() -1);

                        setBegun = false;
                        customTimerEnded = false;
                        endAnimation();
                        handler.postDelayed((Runnable) () -> {
                            startObjectAnimator();
                            breakStart();
                            endAnimation.cancel();
                            timerDisabled = false;
                        },750);
                        dotDraws.setTime(startCustomSetTime);
                        dotDraws.breakTime(startCustomBreakTime);
                        fadeCustomTimer = false;
                    }
                }.start();
                break;

            case 2:
                pomBegun = true;
                timer2 = new CountDownTimer(pomMillis, 50) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        pomProgressPause = (int) objectAnimator2.getAnimatedValue();
                        pomMillis = millisUntilFinished;

//                        if (fadePomTimer) timeLeft2.setAlpha(pomAlpha+=0.1);
                        if (fadePomTimer) {
                            if (pomAlpha<0.25) timeLeft2.setAlpha(pomAlpha+=0.04);
                            else if (pomAlpha<0.5) timeLeft2.setAlpha(pomAlpha+=.08);
                            else timeLeft2.setAlpha(pomAlpha+=.12);
                        }
                        if (pomAlpha >=1) fadePomTimer = false;

                        timeLeft2.setText(convertSeconds((pomMillis + 999)/1000));
                        if (!drawing) {
                            dotDraws.pomDraw(pomDotCounter, 1);
                        }
                    }

                    @Override
                    public void onFinish() {
                        pomBegun = false;
                        numberOfSets--;
                        timeLeft2.setText("0");
                        pomProgressPause = maxProgress;

                        switch (pomDotCounter) {
                            case 2: case 4: case 6:
                                pomMillis = pomMillis2;
                                break;
                            case 7:
                                pomMillis = pomMillis3;
                        }
                        pomDotCounter++;
                        if (pomDotCounter <8) {
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
        timer = new CountDownTimer(breakMillis, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                customProgressPause = (int) objectAnimator.getAnimatedValue();
                breakMillis = millisUntilFinished;

                if (fadeCustomTimer) {
                    if (customAlpha<0.25) timeLeft.setAlpha(customAlpha+=0.04);
                    else if (customAlpha<0.5) timeLeft.setAlpha(customAlpha+=.08);
                    else timeLeft.setAlpha(customAlpha+=.12);
                }
                if (customAlpha >=1) fadeCustomTimer = false;

                timeLeft.setText(convertSeconds((millisUntilFinished +999) / 1000));
                if (drawing) {
                    dotDraws.newDraw(savedSets, savedBreaks, savedSets- (numberOfSets -1), savedBreaks- (numberOfBreaks-1), 2);
                }
            }

            @Override
            public void onFinish() {
                breakBegun = false;
                numberOfBreaks--;
                timeLeft.setText("0");
                if (!breaksOnly) {
                    if (customBreakTime.size() >0) {
                        customBreakTime.remove(customBreakTime.size()-1);
                    }
                    if (customBreakTime.size()>0) breakMillis = customBreakTime.get(customBreakTime.size() -1);
                    dotDraws.setTime(startCustomSetTime);
                    dotDraws.breakTime(startCustomBreakTime);
                } else {
                    if (breaksOnlyTime.size()>0) {
                        breaksOnlyTime.remove(breaksOnlyTime.size()-1);
                    }
                    if (breaksOnlyTime.size()>0) breakMillis = breaksOnlyTime.get(breaksOnlyTime.size() -1);
                    dotDraws.setTime(startBreaksOnlyTime);
                    dotDraws.breakTime(startBreaksOnlyTime);
                }

                endAnimation();
                if (numberOfBreaks >0) {
                    customProgressPause = maxProgress;
                    customTimerEnded = false;

                    handler.postDelayed(() -> {
                        startObjectAnimator();
                        if (!breaksOnly) {
                            startTimer();
                            onBreak = false;
                        } else {
                            breakStart();
                        }
                        endAnimation.cancel();
                        timerDisabled = false;
                    },750);
                } else {
                    if (!breaksOnly) {
                        customCyclesDone++;
                        cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));
                    } else {
                        breaksOnlyCyclesDone++;
                        cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(breaksOnlyCyclesDone)));
                    }
                    endAnimation();
                    dotDraws.newDraw(savedSets, savedBreaks, savedSets- (numberOfSets -1), savedBreaks- (numberOfBreaks-1), 0);
                    customTimerEnded = true;

                    setMillis = setStart;
                    breakMillis = breakStart;
                    timerDisabled = false;
                    fadeCustomTimer = false;
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
            if (!breaksOnly) {
                if (customSetTime.size() < 10 && customSetTime.size() >= 0) {
                    customSetTime.add(spinList1.get(spinner1.getSelectedItemPosition()) * 1000);
                    startCustomSetTime.add(spinList1.get(spinner1.getSelectedItemPosition()) * 1000);
                } else {
                    Toast.makeText(getApplicationContext(), "Max rounds reached!", Toast.LENGTH_SHORT).show();
                }
                if (customBreakTime.size() < 10 && customBreakTime.size() >= 0) {
                    customBreakTime.add(spinList2.get(spinner2.getSelectedItemPosition()) * 1000);
                    startCustomBreakTime.add(spinList2.get(spinner2.getSelectedItemPosition()) * 1000);
                }
            } else {
                if (breaksOnlyTime.size() < 10 && breaksOnlyTime.size() >= 0) {
                    breaksOnlyTime.add(spinList2.get(spinner2.getSelectedItemPosition()) * 1000);
                    startBreaksOnlyTime.add(spinList2.get(spinner2.getSelectedItemPosition()) * 1000);
                } else {
                    Toast.makeText(getApplicationContext(), "Max rounds reached!", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            if (!breaksOnly) {
                if (customSetTime.size() > 0 && startCustomSetTime.size() > 0) {
                    customSetTime.remove(customSetTime.size() - 1);
                    startCustomSetTime.remove(startCustomSetTime.size() - 1);
                } else {
                    Toast.makeText(getApplicationContext(), "Nothing left to remove!", Toast.LENGTH_SHORT).show();
                }
                if (customBreakTime.size() > 0 && startCustomBreakTime.size() > 0) {
                    customBreakTime.remove(customBreakTime.size() - 1);
                    startCustomBreakTime.remove(startCustomBreakTime.size() - 1);
                }
            } else {
                if (breaksOnlyTime.size() > 0 && startBreaksOnlyTime.size() > 0) {
                    breaksOnlyTime.remove(breaksOnlyTime.size() - 1);
                    startBreaksOnlyTime.remove(startBreaksOnlyTime.size() - 1);
                } else {
                    Toast.makeText(getApplicationContext(), "Nothing left to remove!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        resetTimer();
        saveSpins();
        dotDraws.newDraw(savedSets, savedBreaks, 0, 0, 0);

        if (!breaksOnly) {
            if (customSetTime.size()>0) {
                timePaused.setText(convertSeconds((customSetTime.get(customSetTime.size() - 1) +999) / 1000));
            } else {
                timePaused.setText("?");
            }
        } else {
            if (breaksOnlyTime.size()>0) {
                timeLeft.setText(convertSeconds((breaksOnlyTime.get(breaksOnlyTime.size() - 1) +999) / 1000));
                timePaused.setText(convertSeconds((breaksOnlyTime.get(breaksOnlyTime.size() - 1) +999) / 1000));
            } else {
                timePaused.setText("?");
            }
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

    public void changeTextSize(ValueAnimator va, TextView textView, TextView textViewPaused) {
        va.addUpdateListener(animation -> {
            float sizeChange = (float) va.getAnimatedValue();
            textView.setTextSize(sizeChange);
            textViewPaused.setTextSize(sizeChange);
        });
        va.start();
    }

    public void fadeTextIn(TextView textView) {
        textView.setVisibility(View.VISIBLE);
        if (fadeInObj!=null) fadeInObj.cancel();
        fadeInObj = ObjectAnimator.ofFloat(textView, "alpha", 0.0f, 1.0f);
        fadeInObj.setDuration(1500);
        fadeInObj.start();
    }

    public void fadeOutText(TextView textView) {
        if (fadeOutObj!=null) fadeOutObj.cancel();
        fadeOutObj = ObjectAnimator.ofFloat(textView, "alpha", 1.0f, 0.0f);
        fadeOutObj.setDuration(1000);
        fadeOutObj.start();
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

    public void switchTimer(int mode, boolean halted) {
        removeViews();
        switch (mode) {
            case 1:
                drawing = true;
                if (!breaksOnly) {
                    onBreak = false;
                    cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));
                } else {
                    onBreak = true;
                    cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(breaksOnlyCyclesDone)));
                }
                progressBar.setVisibility(View.VISIBLE);
                progressBar2.setVisibility(View.INVISIBLE);
                plus_sign.setVisibility(View.VISIBLE);
                minus_sign.setVisibility(View.VISIBLE);
                spinner3.setVisibility(View.GONE);
                s1.setText(R.string.set_time);
                s2.setText(R.string.break_time);
                if (setMillisUntilFinished==0) setMillisUntilFinished = setMillis;
                if (breakMillisUntilFinished==0) breakMillisUntilFinished = breakMillis;
                if (halted) {
                    if (!breaksOnly) {
                        timePaused.setText(convertSeconds((setMillis + 999)/1000));
                    } else {
                        timePaused.setText(convertSeconds((breakMillis + 999)/1000));
                    }
                    fadeTextIn(timePaused);
                    dotDraws.newDraw(savedSets, savedBreaks, savedSets- (numberOfSets -1), savedBreaks- (numberOfBreaks-1), 1);
                } else {
                    customAlpha = 0;
                    fadeCustomTimer = true;
                    startObjectAnimator();
                }
                break;
            case 2:
                drawing = false;
                cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(pomCyclesDone)));
                progressBar2.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                plus_sign.setVisibility(View.GONE);
                minus_sign.setVisibility(View.GONE);
                spinner3.setVisibility(View.VISIBLE);
                spinner1.setVisibility(View.VISIBLE);
                blank_spinner.setVisibility(View.GONE);
                s1.setText(R.string.work_time);
                s2.setText(R.string.small_break);
                s3.setText(R.string.long_break);
                if (pomMillisUntilFinished==0) pomMillisUntilFinished = pomMillis;
                if (halted) {
                    timePaused2.setText(convertSeconds((pomMillis + 999)/1000));
                    fadeTextIn(timePaused2);
                    dotDraws.pomDraw(pomDotCounter, 1);
                } else {
                    pomAlpha = 0;
                    fadePomTimer = true;
                    startObjectAnimator();
                }
                break;
        }
        dotDraws.retrieveAlpha();
    }

    public void removeViews() {
        timePaused.setAlpha(0);
        timeLeft.setAlpha(0);
        timePaused2.setAlpha(0);
        timeLeft2.setAlpha(0);
    }

    public void pauseAndResumeTimer(int pausing) {
        if (setMillis <=500 || breakMillis <=500) {
            timerDisabled = true;
        }
        if (!timerDisabled) {
            removeViews();
            if (fadeInObj!=null) fadeInObj.cancel();
            if (fadeOutObj!=null) fadeOutObj.cancel();
            switch (mode) {
                case 1:
                    if (!customTimerEnded) {
                        if (pausing == PAUSING_TIMER) {
                            timePaused.setAlpha(1);
                            String pausedTime = "";
                            if (timer!=null) timer.cancel();
                            if (objectAnimator!=null) objectAnimator.pause();
                            customHalted = true;

                            if (!onBreak) {
                                setMillisUntilFinished = setMillis;
                                pausedTime = (convertSeconds((setMillisUntilFinished + 999)/1000));
                            } else {
                                breakMillisUntilFinished = breakMillis;
                                pausedTime = (convertSeconds((breakMillisUntilFinished + 999)/1000));
                            }
                            reset.setVisibility(View.VISIBLE);
                            timePaused.setText(pausedTime);
                        } else if (pausing == RESUMING_TIMER){
                            timeLeft.setAlpha(1);
                            customHalted = false;
                            startObjectAnimator();
                            if (onBreak) {
                                breakStart();
                            } else {
                                startTimer();
                            }
                            reset.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        resetTimer();
                        if (endAnimation != null) endAnimation.cancel();
                    }
                    break;
                case 2:
                    if (!pomTimerEnded) {
                        if (pausing == PAUSING_TIMER) {
                            timePaused2.setAlpha(1);
                            pomHalted = true;
                            pomMillisUntilFinished = pomMillis;
                            if (objectAnimator2!=null) objectAnimator2.pause();
                            if (timer2!=null) timer2.cancel();;
                            String pausedTime2 = (convertSeconds((pomMillisUntilFinished + 999)/1000));
                            timePaused2.setText(pausedTime2);
                            reset.setVisibility(View.VISIBLE);
                        } else if (pausing == RESUMING_TIMER){
                            timeLeft2.setAlpha(1);
                            pomHalted = false;
                            startObjectAnimator();
                            startTimer();
                            reset.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        resetTimer();
                        if (endAnimation != null) endAnimation.cancel();
                        reset.setVisibility(View.INVISIBLE);
                    }
                    break;
            }
        } else if ( (!breaksOnly && customSetTime.size()==0) || (breaksOnly && breaksOnlyTime.size()==0)) {
            Toast.makeText(getApplicationContext(), "What are we timing?", Toast.LENGTH_SHORT).show();
        }
    }

    public void resetTimer() {
        removeViews();
        //Todo: Separate end animations.
        if (endAnimation != null) endAnimation.cancel();

        switch (mode) {
            case 1:
                timePaused.setAlpha(1);
                progressBar.setProgress(10000);
                customProgressPause = maxProgress;
                customTimerEnded = false;
                setBegun = false;
                breakBegun = false;
                customHalted = true;
                if (!breaksOnly) onBreak = false; else onBreak =true;

                if (timer != null) timer.cancel();
                if (objectAnimator != null) objectAnimator.cancel();

                if (startCustomSetTime.size()>0) {
                    setMillis = startCustomSetTime.get(startCustomSetTime.size()-1);
                    timePaused.setText(convertSeconds((setMillis+999)/1000));
                }
                if (!breaksOnly) {
                    if (startCustomBreakTime.size()>0) {
                        breakMillis = startCustomBreakTime.get(startCustomBreakTime.size()-1);
                        timePaused.setText(convertSeconds((setMillis+999)/1000));
                    }
                } else {
                    if (startBreaksOnlyTime.size()>0) {
                        breakMillis = startBreaksOnlyTime.get(startBreaksOnlyTime.size()-1);
                        timePaused.setText(convertSeconds((breakMillis+999)/1000));
                    }
                }
                numberOfSets = startCustomSetTime.size();
                if (!breaksOnly) numberOfBreaks = startCustomBreakTime.size(); else numberOfBreaks = startBreaksOnlyTime.size();
                savedSets = numberOfSets;
                savedBreaks = numberOfBreaks;

                if (!breaksOnly) {
                    customSetTime = new ArrayList<>();
                    customBreakTime = new ArrayList<>();
                } else {
                    breaksOnlyTime = new ArrayList<>();
                }

                if (!breaksOnly) {
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
                } else {
                    if (startBreaksOnlyTime.size()>0) {
                        breaksOnlyTime.addAll(startBreaksOnlyTime);
                        timerDisabled = false;
                    } else {
                        timerDisabled = true;
                    }
                    dotDraws.breakTime(breaksOnlyTime);
                }
                dotDraws.setAlpha();
                dotDraws.newDraw(savedSets, savedBreaks, 0, 0, 0);
                break;
            case 2:
                pomTimerEnded = false;
                progressBar2.setProgress(10000);
                pomBegun = false;
                pomProgressPause = maxProgress;
                if (timer2 != null) timer2.cancel();
                if (objectAnimator2 != null) objectAnimator2.cancel();
//                timeLeft2.setAlpha(1);
//                timePaused2.setAlpha(1);
                pomMillis = pomMillis1;
                timeLeft2.setText(convertSeconds((pomMillis+999)/1000));
                timePaused2.setText(convertSeconds((pomMillis+999)/1000));
                onBreak = false;
                pomHalted = true;
                pomProgressPause = maxProgress;
                break;
        }
    }
}