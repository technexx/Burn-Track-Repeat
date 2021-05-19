package com.example.tragic.irate.simple.stopwatch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tragic.irate.simple.stopwatch.Database.Cycles;
import com.example.tragic.irate.simple.stopwatch.Database.CyclesBO;
import com.example.tragic.irate.simple.stopwatch.Database.CyclesDatabase;
import com.example.tragic.irate.simple.stopwatch.Database.PomCycles;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings({"depreciation"})
public class MainActivity extends AppCompatActivity implements SavedCycleAdapter.onCycleClickListener, SavedCycleAdapter.onDeleteCycleListener, DotDraws.sendPosition, DotDraws.sendAlpha {

    boolean defaultMenu = true;
    View mainView;
    TextView save_cycles;
    TextView update_cycles;
    ProgressBar progressBar;
    ProgressBar progressBar2;
    ProgressBar progressBar3;
    ImageView stopWatchView;
    TextView timeLeft;
    TextView timeLeft2;
    TextView timeLeft3;
    TextView timePaused;
    TextView timePaused2;
    TextView timePaused3;
    TextView timeLeft4;
    TextView timePaused4;
    TextView msTime;
    TextView msTimePaused;
    CountDownTimer timer;
    CountDownTimer timer2;
    CountDownTimer timer3;
    TextView reset;
    ObjectAnimator objectAnimator;
    ObjectAnimator objectAnimator2;
    ObjectAnimator objectAnimator3;
    Animation endAnimation;
    Animation endAnimation2;
    Animation endAnimation3;
    public Handler mHandler;
    TextView lastTextView;

    TextView s1;
    TextView s2;
    TextView s3;
    long setValue;
    long breakValue;
    long breaksOnlyValue;
    long pomValue1;
    long pomValue2;
    long pomValue3;
    long editSetSeconds;
    long editSetMinutes;
    long editBreakMinutes;
    long editBreakSeconds;
    int overSeconds;

    ImageButton fab;
    ImageButton circle_reset;
    ImageButton new_lap;
    ImageButton next_round;
    TextView cycle_header_text;
    TextView cycles_completed;
    Button cycle_reset;
    EditText first_value_edit;
    EditText first_value_edit_two;
    TextView first_value_sep;
    TextView first_value_textView;
    EditText second_value_edit;
    EditText second_value_edit_two;
    TextView second_value_sep;
    TextView second_value_textView;
    EditText first_value_single_edit;
    EditText second_value_single_edit;
    EditText third_value_single_edit;
    TextView third_value_textView;
    ImageView plus_first_value;
    ImageView minus_first_value;
    ImageView plus_second_value;
    ImageView minus_second_value;
    ImageButton plus_third_value;
    ImageButton minus_third_value;
    Button add_cycle;
    Button sub_cycle;
    Button add_cycle_pom;
    Button sub_cycle_pom;
    ImageButton left_arrow;
    ImageButton right_arrow;
    EditText edit_header;
    Button confirm_header_save;
    Button cancel_header_save;
    TextView overtime;
    ImageButton upDown_arrow_one;
    ImageButton upDown_arrow_two;
    View top_anchor;

    ImageView sortCheckmark;
    Button skip;

    int PAUSING_TIMER = 1;
    int RESUMING_TIMER = 2;
    int RESETTING_TIMER = 3;
    int RESTARTING_TIMER = 4;
    int SAVING_CYCLES = 1;
    int UPDATING_CYCLES = 2;
    int movingBOCycle;

    long setMillis;
    long breakMillis;
    long breakOnlyMillis;
    int maxProgress = 10000;
    int customProgressPause = 10000;
    int breaksOnlyProgressPause = 10000;
    int pomProgressPause = 10000;
    long pomMillis;
    long setMillisUntilFinished;
    long breakMillisUntilFinished;
    long breakOnlyMillisUntilFinished;
    long pomMillisUntilFinished;
    boolean incrementValues;
    int incrementTimer = 10;
    Runnable changeFirstValue;
    Runnable changeSecondValue;
    Runnable changeThirdValue;
    Runnable valueSpeed;
    Runnable endFade;
    Runnable ot;

    long pomMillis1;
    long pomMillis2;
    long pomMillis3;
    int pomDotCounter=1;

    double ms;
    double msConvert;
    double msConvert2;
    double msDisplay;
    double seconds;
    double minutes;
    double msReset;
    String newMs;
    String savedMs;
    String displayMs = "00";
    String displayTime = "0";
    String newEntries;
    String savedEntries;
    String resetEntries;
    int newMsConvert;
    int savedMsConvert;
    ArrayList<String> currentLapList;
    ArrayList<String> savedLapList;
    Runnable stopWatchRunnable;

    int customCyclesDone;
    int breaksOnlyCyclesDone;
    int pomCyclesDone;
    int lapsNumber;

    long numberOfSets;
    long numberOfBreaks;
    long numberOfBreaksOnly;
    long startSets;
    long startBreaksOnly;

    boolean modeOneTimerEnded;
    boolean modeTwoTimerEnded;
    boolean modeThreeTimerEnded;
    boolean onBreak;
    boolean emptyCycle;
    boolean timerDisabled;
    boolean boTimerDisabled;
    boolean pomTimerDisabled;
    boolean customHalted = true;
    boolean breaksOnlyHalted = true;
    boolean pomHalted = true;
    boolean stopwatchHalted = true;

    DotDraws dotDraws;
    int mode=1;
    ValueAnimator sizeAnimator;
    ValueAnimator valueAnimatorDown;
    ValueAnimator valueAnimatorUp;

    ArrayList<String> customTitleArray;
    ArrayList<String> breaksOnlyTitleArray;
    ArrayList<String> pomCyclesTitleArray;
    ArrayList<Long> customSetTime;
    ArrayList<Long> customBreakTime;
    ArrayList<Long> breaksOnlyTime;
    ArrayList<Long> pomValuesTime;
    ArrayList<Long> customSetTimeUP;
    ArrayList<Long> customBreakTimeUP;
    ArrayList<Long> breaksOnlyTimeUP;
    String convertedSetList;
    String convertedBreakList;
    String convertedBreakOnlyList;
    String convertedPomList;

    boolean activePomCycle;
    boolean setBegun;
    boolean breakBegun;
    boolean breakOnlyBegun;
    boolean pomBegun;
    boolean selectingRounds;
    boolean isOvertimeRunning;

    PopupWindow sortPopupWindow;
    PopupWindow savedCyclePopupWindow;
    PopupWindow labelSavePopupWindow;
    PopupWindow deleteAllPopupWindow;
    PopupWindow editCyclesPopupWindow;

    boolean fadeCustomTimer;
    float customAlpha;
    float pomAlpha;
    ObjectAnimator fadeInObj;
    ObjectAnimator fadeOutObj;
    RecyclerView lapRecycler;
    LapAdapter lapAdapter;
    LinearLayoutManager lapLayout;

    CyclesDatabase cyclesDatabase;
    Cycles cycles;
    CyclesBO cyclesBO;
    PomCycles pomCycles;
    List<Cycles> cyclesList;
    List<CyclesBO> cyclesBOList;
    List<PomCycles> pomCyclesList;

    ArrayList<String> setsArray;
    ArrayList<String> breaksArray;
    ArrayList<String> breaksOnlyArray;
    ArrayList<String> pomArray;

    RecyclerView savedCycleRecycler;
    SavedCycleAdapter savedCycleAdapter;

    View deleteCyclePopupView;
    View sortCyclePopupView;
    View savedCyclePopupView;
    View cycleLabelView;
    View editCyclesPopupView;

    TextView sortRecent;
    TextView sortNotRecent;
    TextView sortHigh;
    TextView sortLow;
    TextView delete_all_text;
    Button delete_all_confirm;
    Button delete_all_cancel;

    int sortMode = 1;
    int sortModeBO = 1;
    int sortModePom = 1;
    MaterialButton pauseResumeButton;
    int receivedPos;
    MotionEvent motionEvent;
    int customID;
    int breaksOnlyID;
    int pomID;
    boolean canSaveOrUpdateCustom = true;
    boolean canSaveOrUpdateBreaksOnly = true;
    boolean canSaveOrUpdatePom = true;
    boolean duplicateCycle;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor prefEdit;
    int receivedAlpha;
    boolean minReached;
    boolean maxReached;
    int fadeVar;

    long countUpMillisSets;
    long countUpMillisBreaks;
    long countUpMillisBO;
    int COUNTING_DOWN = 1;
    int COUNtING_UP = 2;
    public Runnable secondsUpSetRunnable;
    public Runnable secondsUpBreakRunnable;
    public Runnable secondsUpBORunnable;
    public Runnable fadeInDot;
    public Runnable fadeOutDot;
    boolean setsAreCountingUp;
    boolean breaksAreCountingUp;
    boolean breaksOnlyAreCountingUp;
    int dotAlpha;

    boolean modeOneBetweenRounds;
    boolean modeTwoBetweenRounds;
    boolean modeThreeBetweenRounds;


    //Todo: Test all db stuff.
    //Todo: "Update" will crash if nothing is saved.

    //Todo: Fade for count up/down mode.

    //Todo: Database saves for count up mode.
    //Todo: Single editText for seconds instead of m:ss?
    //Todo: Save completed cycles in sharedPref? If so, remember in nextCountUpRound() as well.
    //Todo: If keeping short breaksOnly add/sub menu, disable Skip and Reset buttons while open.
    //Todo: No rounds added defaults to a default Cycle instead of staying blank.
    //Todo: TDEE in sep popup w/ tabs.
    //Todo: Variable set count-up timer, for use w/ TDEE.
    //Todo: Variable set only mode? Again, for TDEE.
    //Todo: Option to skip EITHER set or break. Option to undo skip.
    //Todo: Reset vis/not vis depending on mode timer status.
    //Todo: Maybe: "cycle completed" as a db entry for each separate cycle.

    //Todo: Fade animation for all menus that don't have them yet (e.g. onOptions).
    //Todo: Add taskbar notification for timers.
    //Todo: Add color scheme options.
    //Todo: Test all Pom cycles.
    //Todo: All DB calls in aSync.
    //Todo: Rename app, of course.
    //Todo: Add onOptionsSelected dots for About, etc.
    //Todo: Repository for db. Look at Executor/other alternate thread methods. Would be MUCH more streamlined on all db calls, but might also bork order of operations when we need to call other stuff under UI thread right after.
    //Todo: Make sure number pad is dismissed when switching to stopwatch mode.
    //Todo: Make sure canvas'd over clickables in stopwatch mode can't be triggered.
    //Todo: IMPORTANT: Resolve landscape mode vs. portrait. Set to portrait-only in manifest at present. Likely need a second layout for landscape mode. Also check that lifecycle is stable.

    //Todo: REMEMBER, All notifyDataSetChanged() has to be called on main UI thread, since that is the one where we created the views it is refreshing.
    //Todo: REMEMBER, always call queryCycles() to get a cyclesList reference, otherwise it won't sync w/ the current sort mode.
    //Todo: REMEMBER, any reference to our GLOBAL instance of a cycles position will retain that position unless changed.
    //Todo: REMINDER, Try next app w/ Kotlin.

    //Receives the current alpha value of our paint object in dotDraws.
    @Override
    public void sendAlphaValue(int alpha) {
        receivedAlpha = alpha;;
    }

    //Receives a position of 0-7 from dotDraws, based upon which range of X coordinates we have clicked on (each tied to a specific round).
    @Override
    public void sendPos(int pos) {
        //Only allowing selection of rounds w/ add/subtract popup in view.
        if (editCyclesPopupView.isShown()) {
            if (mode==1 || mode==2) {
                receivedPos = pos;
                //If clicking outside our dot box, dismiss the add/sub popup.
                if (pos <0) {
                    editCyclesPopupWindow.dismiss();
                    selectingRounds = false;
                } else {
                    selectingRounds = true;
                    if (!setsAreCountingUp || !breaksAreCountingUp) {
                        left_arrow.setVisibility(View.VISIBLE);
                        right_arrow.setVisibility(View.VISIBLE);
                    }
                }
                //If not clicking on an existing round (i.e. a blank space in box), remove arrow visibility.
                if (pos<0 || pos==100) {
                    left_arrow.setVisibility(View.INVISIBLE);
                    right_arrow.setVisibility(View.INVISIBLE);
                    circle_reset.setVisibility(View.VISIBLE);
                }
            } else if (mode==3) editCyclesPopupWindow.dismiss();
//            Log.i("testPos", "position is " + receivedPos);
//            Log.i("testPos", "selectingRounds is " + selectingRounds);
//            Log.i("testPos", "list values are " + customSetTime);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        motionEvent = event;
        int x = (int) event.getX();
        int y = (int) event.getY();

        if (editCyclesPopupView.isShown()) {
            if (event.getAction()==MotionEvent.ACTION_DOWN) {
                switch (mode) {
                    case 1:
                        dotDraws.selectCycle(x, y, (int) numberOfSets); break;
                    case 2:
                        dotDraws.selectCycle(x, y, (int) numberOfBreaksOnly); break;
                    case 3:
                        dotDraws.selectCycle(x, y, (int) 8); break;
                }
            }
        }

        //This dismisses windows/views when main layout (view) is clicked. We use it instead of using a clickable instance of our layout since that bugs.
        if (savedCyclePopupWindow!=null && savedCyclePopupWindow.isShowing()) {
            savedCyclePopupWindow.dismiss();
            invalidateOptionsMenu();
            defaultMenu = true;
        }
        if (deleteAllPopupWindow.isShowing()) deleteAllPopupWindow.dismiss();
        switch (mode) {
            case 1: case 2:
                if (first_value_edit.isShown()) {
                    first_value_textView.setVisibility(View.VISIBLE);
                    first_value_edit.setVisibility(View.GONE);
                    first_value_edit_two.setVisibility(View.GONE);
                    first_value_sep.setVisibility(View.GONE);
                    //onTouch is closing editText, converting its values to longs and then setting them to setValue.
                    setEditValueBounds(true);
                }
                if (second_value_edit.isShown()) {
                    second_value_textView.setVisibility(View.VISIBLE);
                    second_value_edit.setVisibility(View.GONE);
                    second_value_edit_two.setVisibility(View.GONE);
                    second_value_sep.setVisibility(View.GONE);
                    //onTouch is closing editText, converting its values to longs and then setting them to breakValue.
                    setEditValueBounds(false);
                }
                if (cycle_reset.getText().equals(getString(R.string.confirm_cycle_reset))) cycle_reset.setText(R.string.clear_cycles);
                break;
            case 3:
                if (first_value_single_edit.isShown()) {
                    first_value_single_edit.setVisibility(View.GONE);
                    first_value_textView.setVisibility(View.VISIBLE);
                }
                if (second_value_single_edit.isShown()) {
                    second_value_single_edit.setVisibility(View.GONE);
                    second_value_textView.setVisibility(View.VISIBLE);
                }
                if (third_value_single_edit.isShown()) {
                    third_value_single_edit.setVisibility(View.GONE);
                    third_value_textView.setVisibility(View.VISIBLE);
                }
                break;
        }
        save_cycles.setText(R.string.save_cycles);
        return false;
    }

    @Override
    public void onCycleClick(int position) {
        selectRound(position, true);
    }

    @Override
    public void onCycleDelete(int position) {
        AsyncTask.execute(()->{
            //Initial query, applying to all retrievals.
            queryCycles();
            switch (mode) {
                case 1:
                    Cycles removedCycle = cyclesList.get(position);
                    cyclesDatabase.cyclesDao().deleteCycle(removedCycle);
                    //Second query, retrieving the new, post-modified entity.
                    queryCycles();

                    runOnUiThread(() -> {
                        setsArray.clear();
                        breaksArray.clear();
                        for (int i=0; i<cyclesList.size(); i++) {
                            setsArray.add(cyclesList.get(i).getSets());
                            breaksArray.add(cyclesList.get(i).getBreaks());
                            customTitleArray.add(cyclesList.get(i).getTitle());
                        }
                        savedCycleAdapter.notifyDataSetChanged();
                    });
                    break;
                case 2:
                    CyclesBO removedBOCycle = cyclesBOList.get(position);
                    cyclesDatabase.cyclesDao().deleteBOCycle(removedBOCycle);
                    queryCycles();

                    runOnUiThread(() -> {
                        breaksOnlyArray.clear();
                        for (int i=0; i<cyclesBOList.size(); i++) {
                            breaksOnlyArray.add(cyclesBOList.get(i).getBreaksOnly());
                            breaksOnlyTitleArray.add(cyclesList.get(i).getTitle());
                        }
                        savedCycleAdapter.notifyDataSetChanged();
                    });
                    break;
                case 3:
                    int deletedID = pomCyclesList.get(position).getId();

                    PomCycles removedPom = pomCyclesList.get(position);
                    cyclesDatabase.cyclesDao().deletePomCycle(removedPom);
                    queryCycles();

                    runOnUiThread(()-> {
                        pomArray.remove(position);
                        savedCycleAdapter.notifyDataSetChanged();
                    });
                    break;
            }
            saveArrays();
        });
        Toast.makeText(getApplicationContext(), "Cycle deleted!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        if (defaultMenu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.options_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        if (!defaultMenu) inflater.inflate(R.menu.revised_options_menu, menu);
        else inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (savedCyclePopupWindow != null && savedCyclePopupWindow.isShowing()) {
            //This is for when the onCreateOptionsMenu is opened.
//            savedCyclePopupWindow.dismiss();
//            invalidateOptionsMenu();
//            defaultMenu = true;
        }
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (savedCyclePopupWindow!=null &&savedCyclePopupWindow.isShowing()) return false;
        AtomicBoolean cyclesExist = new AtomicBoolean(false);
        switch (item.getItemId()) {
            case R.id.saved_cycle_list:
                AsyncTask.execute(() -> {
                    queryCycles();
                    clearArrays(false);
                    switch (mode) {
                        case 1:
                            savedCycleAdapter.setView(1);
                            if (cyclesList.size()>0) {
                                for (int i=0; i<cyclesList.size(); i++) {
                                    setsArray.add(cyclesList.get(i).getSets());
                                    breaksArray.add(cyclesList.get(i).getBreaks());
                                    customTitleArray.add(cyclesList.get(i).getTitle());
                                }
                                cyclesExist.set(true);
                            }
                            break;
                        case 2:
                            savedCycleAdapter.setView(2);
                            if (cyclesBOList.size()>0) {
                                for (int i=0; i<cyclesBOList.size(); i++) {
                                    breaksOnlyTitleArray.add(cyclesBOList.get(i).getTitle());
                                    breaksOnlyArray.add(cyclesBOList.get(i).getBreaksOnly());
                                }
                                cyclesExist.set(true);
                            }
                            break;
                        case 3:
                            savedCycleAdapter.setView(3);
                            if (pomCyclesList.size()>0) {
                                for (int i=0; i<pomCyclesList.size(); i++) {
                                    pomArray.add(pomCyclesList.get(i).getFullCycle());
                                    pomCyclesTitleArray.add(pomCyclesList.get(i).getTitle());
                                }
                                cyclesExist.set(true);
                            }
                            break;
                    }

                    runOnUiThread(()-> {
                        if (cyclesExist.get()) {
                            save_cycles.setText(R.string.sort_cycles);
                            //Focusable must be false for save/sort switch function to work, otherwise window will steal focus from button.
                            savedCyclePopupWindow.showAtLocation(mainView, Gravity.CENTER, 0, 100);
                            savedCycleAdapter.notifyDataSetChanged();
                            switchMenu();
                        } else {
                            Toast.makeText(getApplicationContext(), "Nothing saved!", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
                break;

            case R.id.delete_all_cycles:
                AsyncTask.execute(() -> {
                    queryCycles();
                    if ((mode==1 && cyclesList.size()==0)|| (mode==2 && cyclesBOList.size()==0)){
                        runOnUiThread(()-> {
                            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Nothing saved!", Toast.LENGTH_SHORT).show());
                        });
                    } else {
                        runOnUiThread(()-> {
                            if (mode==1) delete_all_text.setText(R.string.delete_all_custom); else delete_all_text.setText(R.string.delete_all_BO);
                            deleteAllPopupWindow.showAtLocation(deleteCyclePopupView, Gravity.CENTER, 0, 0);
                        });
                        ;
                    }
                });
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "ClickableViewAccessibility", "CommitPrefEdits"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainView = findViewById(R.id.main_layout);

        valueAnimatorDown = new ValueAnimator().ofFloat(90f, 70f);
        valueAnimatorUp = new ValueAnimator().ofFloat(70f, 90f);
        valueAnimatorDown.setDuration(2000);
        valueAnimatorUp.setDuration(2000);

        customSetTime = new ArrayList<>();
        customBreakTime = new ArrayList<>();
        breaksOnlyTime = new ArrayList<>();
        customSetTimeUP = new ArrayList<>();
        customBreakTimeUP = new ArrayList<>();
        breaksOnlyTimeUP = new ArrayList<>();
        currentLapList = new ArrayList<>();
        savedLapList = new ArrayList<>();

        setsArray = new ArrayList<>();
        breaksArray = new ArrayList<>();
        breaksOnlyArray = new ArrayList<>();
        customTitleArray = new ArrayList<>();
        breaksOnlyTitleArray = new ArrayList<>();
        pomArray = new ArrayList<>();
        pomCyclesTitleArray = new ArrayList<>();
        pomValuesTime = new ArrayList<>();

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Sets+"));
        tabLayout.addTab(tabLayout.newTab().setText("Breaks"));
        tabLayout.addTab(tabLayout.newTab().setText("Pomodoro"));
        tabLayout.addTab(tabLayout.newTab().setText("Stopwatch"));

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        savedCyclePopupView = inflater.inflate(R.layout.saved_cycles_layout, null);
        deleteCyclePopupView = inflater.inflate(R.layout.delete_cycles_popup, null);
        sortCyclePopupView = inflater.inflate(R.layout.sort_popup, null);
        cycleLabelView = inflater.inflate(R.layout.label_cycle_popup, null);
        editCyclesPopupView = inflater.inflate(R.layout.edit_cycle_popup, null);

        savedCyclePopupWindow = new PopupWindow(savedCyclePopupView, 800, 1200, true);
        deleteAllPopupWindow = new PopupWindow(deleteCyclePopupView, 750, 375, true);
        labelSavePopupWindow = new PopupWindow(cycleLabelView, 800, 400, true);
        sortPopupWindow = new PopupWindow(sortCyclePopupView, 400, 375, true);
        savedCyclePopupWindow.setAnimationStyle(R.style.WindowAnimation);
        deleteAllPopupWindow.setAnimationStyle(R.style.WindowAnimation);
        labelSavePopupWindow.setAnimationStyle(R.style.WindowAnimation);
        sortPopupWindow.setAnimationStyle(R.style.WindowAnimation);

        //Focus set to false so we can access rest of UI.
        editCyclesPopupWindow = new PopupWindow(editCyclesPopupView, WindowManager.LayoutParams.MATCH_PARENT, 415, true);
        editCyclesPopupWindow .setAnimationStyle(R.style.WindowAnimation);

        savedCycleRecycler = savedCyclePopupView.findViewById(R.id.cycle_list_recycler);
        LinearLayoutManager lm2 = new LinearLayoutManager(getApplicationContext());
        savedCycleAdapter = new SavedCycleAdapter(getApplicationContext(), setsArray, breaksArray, breaksOnlyArray, customTitleArray, breaksOnlyTitleArray, pomArray, pomCyclesTitleArray);
        savedCycleRecycler.setAdapter(savedCycleAdapter);
        savedCycleRecycler.setLayoutManager(lm2);
        savedCycleAdapter.setItemClick(MainActivity.this);
        savedCycleAdapter.setDeleteCycle(MainActivity.this);

        edit_header = cycleLabelView.findViewById(R.id.cycle_name_edit);
        confirm_header_save = cycleLabelView.findViewById(R.id.confirm_save);
        cancel_header_save = cycleLabelView.findViewById(R.id.cancel_save);

        sortRecent = sortCyclePopupView.findViewById(R.id.sort_most_recent);
        sortNotRecent = sortCyclePopupView.findViewById(R.id.sort_least_recent);
        sortHigh = sortCyclePopupView.findViewById(R.id.sort_number_high);
        sortLow = sortCyclePopupView.findViewById(R.id.sort_number_low);
        sortCheckmark = sortCyclePopupView.findViewById(R.id.sortCheckmark);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_bar);

        s1 = editCyclesPopupView.findViewById(R.id.s1);
        s2 = editCyclesPopupView.findViewById(R.id.s2);
        s3 = editCyclesPopupView.findViewById(R.id.s3);
        first_value_edit = editCyclesPopupView.findViewById(R.id.first_value_edit);
        first_value_edit_two = editCyclesPopupView.findViewById(R.id.first_value_edit_two);
        first_value_sep = editCyclesPopupView.findViewById(R.id.first_value_sep);
        first_value_textView = editCyclesPopupView.findViewById(R.id.first_value_textView);
        second_value_edit = editCyclesPopupView.findViewById(R.id.second_value_edit);
        second_value_edit_two = editCyclesPopupView.findViewById(R.id.second_value_edit_two);
        second_value_sep = editCyclesPopupView.findViewById(R.id.second_value_sep);
        second_value_textView = editCyclesPopupView.findViewById(R.id.second_value_textView);
        third_value_textView = editCyclesPopupView.findViewById(R.id.third_value_textView);
        plus_first_value = editCyclesPopupView.findViewById(R.id.plus_first_value);
        minus_first_value = editCyclesPopupView.findViewById(R.id.minus_first_value);
        plus_second_value = editCyclesPopupView.findViewById(R.id.plus_second_value);
        minus_second_value = editCyclesPopupView.findViewById(R.id.minus_second_value);
        plus_third_value = editCyclesPopupView.findViewById(R.id.plus_third_value);
        minus_third_value = editCyclesPopupView.findViewById(R.id.minus_third_value);
        first_value_single_edit = editCyclesPopupView.findViewById(R.id.first_value_single_edit);
        second_value_single_edit = editCyclesPopupView.findViewById(R.id.second_value_single_edit);
        third_value_single_edit = editCyclesPopupView.findViewById(R.id.third_value_single_edit);
        add_cycle = editCyclesPopupView.findViewById(R.id.add_cycle);
        sub_cycle = editCyclesPopupView.findViewById(R.id.subtract_cycle);
        add_cycle_pom = editCyclesPopupView.findViewById(R.id.add_cycle_pom);
        sub_cycle_pom = editCyclesPopupView.findViewById(R.id.subtract_cycle_pom);
        upDown_arrow_one  = editCyclesPopupView.findViewById(R.id.s1_up);
        upDown_arrow_two  = editCyclesPopupView.findViewById(R.id.s2_up);
        top_anchor = editCyclesPopupView.findViewById(R.id.top_anchor);

        reset = findViewById(R.id.reset);
        cycle_header_text = findViewById(R.id.cycle_header_text);

        delete_all_text = deleteCyclePopupView.findViewById(R.id.delete_confirm_text);
        delete_all_confirm = deleteCyclePopupView.findViewById(R.id.confirm_yes);
        delete_all_cancel = deleteCyclePopupView.findViewById(R.id.confirm_no);

        cycles_completed = findViewById(R.id.cycles_completed);
        cycle_reset = findViewById(R.id.cycle_reset);
        skip = findViewById(R.id.skip);
        left_arrow = findViewById(R.id.left_arrow);
        right_arrow = findViewById(R.id.right_arrow);
        fab = findViewById(R.id.fab);
        circle_reset = findViewById(R.id.circle_reset);
        next_round = findViewById(R.id.next_round);
        new_lap = findViewById(R.id.new_lap);

        save_cycles = findViewById(R.id.save_cycles);
        update_cycles = findViewById(R.id.update_cycles);
        progressBar = findViewById(R.id.progressBar);
        progressBar2 = findViewById(R.id.progressBar2);
        progressBar3 = findViewById(R.id.progressBar3);
        stopWatchView = findViewById(R.id.stopWatchView);
        timeLeft = findViewById(R.id.timeLeft);
        timeLeft2 = findViewById(R.id.timeLeft2);
        timeLeft3 = findViewById(R.id.timeLeft3);
        timeLeft4 =findViewById(R.id.timeLeft4);
        timePaused = findViewById(R.id.timePaused);
        timePaused2 = findViewById(R.id.timePaused2);
        timePaused3 = findViewById(R.id.timePaused3);
        timePaused4 = findViewById(R.id.timePaused4);
        msTime = findViewById(R.id.msTime);
        msTimePaused = findViewById(R.id.msTimePaused);
        dotDraws = findViewById(R.id.dotdraws);
        lapRecycler = findViewById(R.id.lap_recycler);
        overtime = findViewById(R.id.overtime);
        pauseResumeButton = findViewById(R.id.pauseResumeButton);
        pauseResumeButton.setBackgroundColor(Color.argb(0, 0, 0, 0));
        pauseResumeButton.setRippleColor(null);

        left_arrow.setVisibility(View.INVISIBLE);
        right_arrow.setVisibility(View.INVISIBLE);
        reset.setVisibility(View.INVISIBLE);
        first_value_edit.setVisibility(View.GONE);
        first_value_sep.setVisibility(View.GONE);
        first_value_edit.setVisibility(View.GONE);
        first_value_edit_two.setVisibility(View.GONE);
        first_value_single_edit.setVisibility(View.GONE);
        second_value_edit.setVisibility(View.GONE);
        second_value_sep.setVisibility(View.GONE);
        second_value_edit_two.setVisibility(View.GONE);
        second_value_single_edit.setVisibility(View.GONE);
        third_value_single_edit.setVisibility(View.GONE);
        progressBar2.setVisibility(View.GONE);
        progressBar3.setVisibility(View.GONE);
        stopWatchView.setVisibility(View.GONE);
        lapRecycler.setVisibility(View.GONE);
        overtime.setVisibility(View.INVISIBLE);
        new_lap.setVisibility(View.INVISIBLE);
        next_round.setVisibility(View.INVISIBLE);
        add_cycle_pom.setVisibility(View.GONE);
        sub_cycle_pom.setVisibility(View.GONE);

        dotDraws.onPositionSelect(MainActivity.this);
        dotDraws.onAlphaSend(MainActivity.this);

        s1.setText(R.string.set_time);
        s2.setText(R.string.break_time);
        s3.setText(R.string.rounds);
        save_cycles.setText(R.string.save_cycles);
        update_cycles.setText(R.string.update_cycles);
        confirm_header_save.setText(R.string.save_cycles);
        reset.setText(R.string.reset);

        timeLeft.setTextSize(90f);
        timePaused.setTextSize(90f);
        timeLeft2.setTextSize(90f);
        timePaused2.setTextSize(90f);
        timeLeft3.setTextSize(70f);
        timePaused3.setTextSize(70f);
        timeLeft4.setTextSize(90f);
        timePaused4.setTextSize(90f);
        skip.setText(R.string.skip_round);
        cycle_reset.setText(R.string.clear_cycles);
        cycles_completed.setText(R.string.cycles_done);
        cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));

        mHandler = new Handler();

        //Sets all timerText alphas 0, so that we can then set to 1 whichever one we want to use.
        removeTimerViews();
        //Since we launch our app to Sets+ default, its paused timer should always default to visible. Same w/ mode being 1.
        timePaused.setAlpha(1);
        mode = 1;
        dotDraws.setMode(1);

        cyclesList = new ArrayList<>();
        cyclesBOList = new ArrayList<>();
        pomCyclesList = new ArrayList<>();
        cycles = new Cycles();
        cyclesBO = new CyclesBO();
        pomCycles = new PomCycles();

        sharedPreferences = getApplicationContext().getSharedPreferences("pref", 0);
        prefEdit = sharedPreferences.edit();
        setValue = sharedPreferences.getLong("setValue", 30);
        breakValue = sharedPreferences.getLong("breakValue", 30);
        breaksOnlyValue = sharedPreferences.getLong("breaksOnlyValue", 30);
        //Call this on start to sync edit values w/ retrieved values from SharedPref.
        convertEditTime();

        sortMode = sharedPreferences.getInt("sortMode", 1);
        sortModeBO = sharedPreferences.getInt("sortModeBO", 1);
        sortModePom = sharedPreferences.getInt("sortModePom", 1);
        customID = sharedPreferences.getInt("customID", 0);
        breaksOnlyID = sharedPreferences.getInt("breaksOnlyID", 0);
        pomID = sharedPreferences.getInt("pomID", 0);

        //Setting count up/down mode to last used, then calling method that sets their values. We use the inverse on our sharedPref retrieval because the method we are calling assumes we are in the previous mode.
        setsAreCountingUp = !sharedPreferences.getBoolean("setCountUpMode", false);
        breaksAreCountingUp = !sharedPreferences.getBoolean("breakCountUpMode", false);
        countUpMode(true); countUpMode(false);

        //Clears all lists so they can be populated using saved data.
        customSetTime.clear();
        customBreakTime.clear();
        breaksOnlyTime.clear();
        customSetTimeUP.clear();
        customBreakTimeUP.clear();
        breaksOnlyTimeUP.clear();

        String retrievedSetArray = sharedPreferences.getString("setArrays", "");
        String retrievedBreakArray = sharedPreferences.getString("breakArrays", "");
        String retrievedPomArray = sharedPreferences.getString("savedPomArrays", "");
        String retrievedBOArray = sharedPreferences.getString("savedBOArrays", "");
        String retrievedTitle = sharedPreferences.getString("savedTitle", "");

        retrievedSetArray = retrievedSetArray.replace("[", "");
        retrievedSetArray = retrievedSetArray.replace("]", "");
        retrievedBreakArray = retrievedBreakArray.replace("[", "");
        retrievedBreakArray = retrievedBreakArray.replace("]", "");
        retrievedPomArray = retrievedPomArray.replace("]", "");
        retrievedPomArray = retrievedPomArray.replace("[", "");
        retrievedBOArray = retrievedBOArray.replace("[", "");
        retrievedBOArray = retrievedBOArray.replace("]", "");

        String[] convSets = retrievedSetArray.split(",");
        String[] convBreaks = retrievedBreakArray.split(",");
        String[] convPom = retrievedPomArray.split(",");
        String[] convBO = retrievedBOArray.split(",");

        //Sets our saved title.
        if (!retrievedTitle.equals("")) cycle_header_text.setText(retrievedTitle); else cycle_header_text.setText(R.string.default_title);

        //Starting list for Sets+, counting down. Recalls previously entered rounds. If no rounds exist, populates using default.
        if (!retrievedSetArray.equals("")) {
            for (int i=0; i<convSets.length; i++) {
                customSetTime.add(Long.parseLong(convSets[i]));
                customBreakTime.add(Long.parseLong(convBreaks[i]));
                customSetTimeUP.add((long) 0);
                customBreakTimeUP.add((long) 0);
            }
        } else setDefaultCustomCycle(false);
        if (!retrievedBOArray.equals("")){
            for (int i=0; i<convBO.length; i++) {
                breaksOnlyTime.add(Long.parseLong(convBO[i]));
                breaksOnlyTimeUP.add((long) 0);
            }
        } else setDefaultCustomCycle(true);

        //Retrieves the last entered Pom cycle, or default if none. Converts default millis to its first round.
        if (!retrievedPomArray.equals("")) {
            for (int i=0; i<8; i++) pomValuesTime.add(Long.parseLong(convPom[i]));
        } else setDefaultPomCycle();
        pomMillis = pomValuesTime.get(0) * 1000 * 60;
        pomMillis1 = pomMillis;
        pomValue1 = pomValuesTime.get(0);
        pomValue2 = pomValuesTime.get(1);
        pomValue3 = pomValuesTime.get(2);

        //Sets number of rounds to size of round list.
        numberOfSets = customSetTime.size();
        numberOfBreaks = customBreakTime.size();
        numberOfBreaksOnly = breaksOnlyTime.size();

        //Sets initial millis values to first round value.
        setMillis = customSetTime.get(0);
        breakOnlyMillis = breaksOnlyTime.get(0);

        //Animation that plays when round completes. Creating one for initial use.
        animateEnding(false);

        //Recycler view for our stopwatch laps.
        lapLayout= new LinearLayoutManager(getApplicationContext());
        lapAdapter = new LapAdapter(getApplicationContext(), currentLapList, savedLapList);
        lapRecycler.setAdapter(lapAdapter);
        lapRecycler.setLayoutManager(lapLayout);

        //Sets all progress bars to their start value.
        progressBar.setProgress(maxProgress);
        progressBar2.setProgress(maxProgress);
        progressBar3.setProgress(maxProgress);

        upDown_arrow_one.setImageResource(R.drawable.arrow_down);
        upDown_arrow_one.setTag(1);
        upDown_arrow_two.setImageResource(R.drawable.arrow_down);
        upDown_arrow_two.setTag(1);

        //Start mode of reset and fab buttons.
        resetAndFabToggle(false, true);

        //Sets xml views.
        tabViews();
        //Populates UI elements at app start.
        populateCycleUI();



        //Used in all timers to smooth out end fade.
        endFade = new Runnable() {
            @Override
            public void run() {
                drawDots(fadeVar);
                if (receivedAlpha<=100) mHandler.removeCallbacks(this); else mHandler.postDelayed(this, 50);
            }
        };

        //Todo: Save should always exist, but Update should not.
        //If one or more cycles exist in our db, gives an option to save or update.
        if ((mode==1 && cyclesList.size()>0)  || (mode==2 && cyclesBOList.size()>0)) canSaveOrUpdate(false); else canSaveOrUpdate(true);

        AsyncTask.execute(() -> cyclesDatabase = CyclesDatabase.getDatabase(getApplicationContext()));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        mode=1;
                        savedCycleAdapter.setView(1);
                        switchTimer(1, customHalted);
                        dotDraws.setMode(1);
                        break;
                    case 1:
                        mode=2;
                        savedCycleAdapter.setView(2);
                        switchTimer(2, breaksOnlyHalted);
                        dotDraws.setMode(2);
                        break;
                    case 2:
                        mode=3;
                        savedCycleAdapter.setView(3);
                        switchTimer(3, pomHalted);
                        dotDraws.setMode(3);
                        break;
                    case 3:
                        mode=4;
                        switchTimer(4, stopwatchHalted);
                        dotDraws.setMode(4);
                        break;
                }
            }

            //Todo: lastTextView is blank except when moving from mode 1-> mode 2. Is
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //Clears out any active editViews when switching to a new tab.
                removeEditViews();
                switch (tab.getPosition()) {
                    case 0:
                        if (customHalted) {
                            fadeOutText(timePaused);
                        } else fadeOutText(timeLeft); lastTextView = timeLeft;
                        break;
                    case 1:
                        if (breaksOnlyHalted) {
                            fadeOutText(timePaused2);
                        } else fadeOutText(timeLeft2); lastTextView = timeLeft2;
                        break;
                    case 2:
                        if (pomHalted) {
                            fadeOutText(timePaused3);
                        } else fadeOutText(timeLeft3); lastTextView = timeLeft3;
                        break;
                    case 3:
                        if (stopwatchHalted) fadeOutText(timePaused4);
                        else fadeOutText(timeLeft4);
                        lapRecycler.setVisibility(View.GONE);
                        break;
                }
                Log.i("testFade", "paused1 value is " + timePaused.getText().toString());
                Log.i("testFade", "last value for ONE is " + lastTextView.getText().toString());
                left_arrow.setVisibility(View.INVISIBLE);
                right_arrow.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        fab.setOnClickListener(v-> {
            if (editCyclesPopupWindow.isShowing()) editCyclesPopupWindow.dismiss(); else {
                switch (mode) {
                    case 1:
                        editCyclesPopupWindow.setHeight(405); break;
                    case 2:
                        editCyclesPopupWindow.setHeight(255); break;
                    case 3:
                        editCyclesPopupWindow.setHeight(380); break;
                }
                editCyclesPopupWindow.showAsDropDown((View) tabLayout);
            }
        });

        delete_all_confirm.setOnClickListener(v-> {
            AsyncTask.execute(() -> {
                switch (mode) {
                    case 1:
                        cyclesDatabase.cyclesDao().deleteAll();
                        prefEdit.putInt("customID", 0);
                        break;
                    case 2:
                        cyclesDatabase.cyclesDao().deleteAllBO();
                        prefEdit.putInt("breaksOnlyID", 0);
                        break;
                    case 3:
                        cyclesDatabase.cyclesDao().deleteAllPomCycles();
                        prefEdit.putInt("pomID", 0);
                        break;
                }
                prefEdit.apply();
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "Deleted!", Toast.LENGTH_SHORT).show();
                    deleteAllPopupWindow.dismiss();
                });
            });
        });

        delete_all_cancel.setOnClickListener(v-> {
            deleteAllPopupWindow.dismiss();
        });

        cycle_header_text.setOnClickListener(v-> {
            confirm_header_save.setText(R.string.update_cycles);
            labelSavePopupWindow.showAtLocation(mainView, Gravity.CENTER, 0, -200);

            String titleText = cycle_header_text.getText().toString();
            edit_header.setText(titleText);
            edit_header.setSelection(titleText.length());

            cancel_header_save.setOnClickListener(v2-> {
                if (labelSavePopupWindow!=null) labelSavePopupWindow.dismiss();
            });

            confirm_header_save.setOnClickListener(v2 -> {
                String newTitle = edit_header.getText().toString();
                if (!titleText.equals(newTitle)) canSaveOrUpdate(true);
                cycle_header_text.setText(newTitle);
            });
        });

        upDown_arrow_one.setOnClickListener(v-> {
            countUpMode(true);
        });

        upDown_arrow_two.setOnClickListener(v -> {
            countUpMode(false);
        });

        first_value_textView.setOnClickListener(v-> {
            editAndTextSwitch(true, 1);
        });

        second_value_textView.setOnClickListener(v-> {
            editAndTextSwitch(true, 2);
        });

        third_value_textView.setOnClickListener(v->{
            editAndTextSwitch(true, 3);
        });

        changeFirstValue = new Runnable() {
            @Override
            public void run() {
                switch (mode) {
                    case 1:
                        if (incrementValues) setValue+=1; else setValue -=1;
                        break;
                    case 3:
                        if (incrementValues) pomValue1+=1; else pomValue1 -=1;
                        pomMillis1 = (pomValue1*1000) * 60;
                        break;
                }
                prefEdit.apply();
                mHandler.postDelayed(this, incrementTimer*10);
                setTimerValueBounds();
                fadeCap(first_value_textView);
                editAndTextSwitch(false, 1);
            }
        };

        changeSecondValue = new Runnable() {
            @Override
            public void run() {
                switch (mode) {
                    case 1:
                        if (incrementValues) breakValue+=1; else breakValue -=1;
                        break;
                    case 2:
                        if (incrementValues) breaksOnlyValue+=1; else breaksOnlyValue -=1;
                        break;
                    case 3:
                        if (incrementValues) pomValue2+=1; else pomValue2 -=1;
                        pomMillis2 = (pomValue2*1000) * 60;
                        break;
                }
                prefEdit.apply();
                mHandler.postDelayed(this, incrementTimer*10);
                setTimerValueBounds();
                fadeCap(second_value_textView);
                editAndTextSwitch(false, 2);
            }
        };

        changeThirdValue = new Runnable() {
            @Override
            public void run() {
                if (incrementValues) pomValue3+=1; else pomValue3 -=1;
                pomMillis3 = (pomValue3*1000) * 60;
                mHandler.postDelayed(this, incrementTimer*10);
                setTimerValueBounds();
                fadeCap(third_value_textView);
                editAndTextSwitch(false, 3);
            }
        };

        valueSpeed = new Runnable() {
            @Override
            public void run() {
                if (incrementTimer>1) incrementTimer-=1;
                mHandler.postDelayed(this, 300);
            }
        };


        plus_first_value.setOnTouchListener((v, event) -> {
            incrementValues = true;
            setIncrements(event, changeFirstValue);
            switch (mode) {
                case 1:
                    convertEditTime();
                    first_value_textView.setText(convertCustomTextView(setValue));
                    break;
                case 3:
                    first_value_single_edit.setText(String.valueOf(pomValue1));
                    first_value_textView.setText(String.valueOf(pomValue1));
                    break;
            }
            return true;
        });

        minus_first_value.setOnTouchListener((v, event) -> {
            incrementValues = false;
            setIncrements(event, changeFirstValue);
            switch (mode) {
                case 1:
                    convertEditTime();
                    first_value_textView.setText(convertCustomTextView(setValue));
                    break;
                case 3:
                    first_value_single_edit.setText(String.valueOf(pomValue1));
                    first_value_textView.setText(String.valueOf(pomValue1));
                    break;
            }
            return true;
        });

        plus_second_value.setOnTouchListener((v, event) -> {
            incrementValues = true;
            setIncrements(event, changeSecondValue);
            switch (mode) {
                case 1:
                    convertEditTime();
                    second_value_textView.setText(convertCustomTextView(breakValue));
                    break;
                case 2:
                    convertEditTime();
                    second_value_textView.setText(convertCustomTextView(breaksOnlyValue));
                    break;
                case 3:
                    second_value_single_edit.setText(String.valueOf(pomValue2));
                    second_value_textView.setText(String.valueOf(pomValue2));
                    break;
            }
            return true;
        });

        minus_second_value.setOnTouchListener((v, event) -> {
            incrementValues = false;
            setIncrements(event, changeSecondValue);
            switch (mode) {
                case 1:
                    convertEditTime();
                    second_value_textView.setText(convertCustomTextView(breakValue));
                    break;
                case 2:
                    convertEditTime();
                    second_value_textView.setText(convertCustomTextView(breaksOnlyValue));
                    break;
                case 3:
                    second_value_single_edit.setText(String.valueOf(pomValue2));
                    second_value_textView.setText(String.valueOf(pomValue2));
                    break;
            }
            return true;
        });

        plus_third_value.setOnTouchListener((v, event) -> {
            setIncrements(event, changeThirdValue);
            incrementValues = true;
            third_value_single_edit.setText(String.valueOf(pomValue3));
            third_value_textView.setText(String.valueOf(pomValue3));
            return true;
        });

        minus_third_value.setOnTouchListener((v, event) -> {
            incrementValues = false;
            setIncrements(event, changeThirdValue);
            third_value_single_edit.setText(String.valueOf(pomValue3));
            third_value_textView.setText(String.valueOf(pomValue3));
            return true;
        });

        left_arrow.setOnClickListener(v-> {
            if (mode==1) {
                if (customSetTime.size()>=2 && receivedPos>0) {
                    long holder = 0;
                    if (!setsAreCountingUp) {
                        holder = customSetTime.get(receivedPos-1);
                        customSetTime.set(receivedPos-1, customSetTime.get(receivedPos));
                        customSetTime.set(receivedPos, holder);
                        setMillis = newMillis(true);
                        setNewText(timePaused, timePaused, (setMillis+999)/1000);
                    } else setNewText(timePaused, timePaused, 0);
                    if (!breaksAreCountingUp) {
                        holder = customBreakTime.get(receivedPos-1);
                        customBreakTime.set(receivedPos-1, customBreakTime.get(receivedPos));
                        customBreakTime.set(receivedPos, holder);
                    }
                    //Moving receivedPos one place left.
                    receivedPos-=1;
                }
            } else if (mode==2) {
                if (breaksOnlyTime.size()>=2 && receivedPos>0) {
                    if (!breaksAreCountingUp) {
                        long holder = breaksOnlyTime.get(receivedPos-1);
                        breaksOnlyTime.set(receivedPos-1, breaksOnlyTime.get(receivedPos));
                        breaksOnlyTime.set(receivedPos, holder);
                        breakOnlyMillis = newMillis(false);
                        setNewText(timePaused2, timePaused2, (breakOnlyMillis+999)/1000);
                        //Moving receivedPos one place right.
                        receivedPos-=1;
                    } else setNewText(timePaused2, timePaused2, 0);
                }
            }
            //Sending new receivedPos var to dotDraws class, which moves our selection box.
            dotDraws.selectRound(receivedPos);
            drawDots(0);
            saveArrays();
        });

        //Switching order of sets/breaks only if in count down mode (where preset values exist).
        right_arrow.setOnClickListener(v-> {
            if (mode==1) {
                if (customSetTime.size()-1 > receivedPos && receivedPos>=0) {
                    long holder = 0;
                    if (!setsAreCountingUp) {
                        holder = customSetTime.get(receivedPos+1);
                        customSetTime.set(receivedPos+1, customSetTime.get(receivedPos));
                        customSetTime.set(receivedPos, holder);
                        setMillis = newMillis(true);
                        setNewText(timePaused, timePaused, (setMillis+999)/1000);
                    } else setNewText(timePaused, timePaused, 0);

                    if (!breaksAreCountingUp) {
                        holder = customBreakTime.get(receivedPos+1);
                        customBreakTime.set(receivedPos+1, customBreakTime.get(receivedPos));
                        customBreakTime.set(receivedPos, holder);
                    }
                    receivedPos +=1;
                }
            } else if (mode==2) {
                if (breaksOnlyTime.size()-1 > receivedPos && receivedPos>=0) {
                    if (!breaksAreCountingUp) {
                        long holder = breaksOnlyTime.get(receivedPos+1);
                        breaksOnlyTime.set(receivedPos+1, breaksOnlyTime.get(receivedPos));
                        breaksOnlyTime.set(receivedPos, holder);
                        breakOnlyMillis = newMillis(false);
                        setNewText(timePaused2, timePaused2, (breakOnlyMillis+999)/1000);
                        receivedPos +=1;
                    } else setNewText(timePaused2, timePaused2, 0);
                }
            }
            drawDots(0);
            dotDraws.selectRound(receivedPos);
            saveArrays();
        });

        add_cycle.setOnClickListener(v-> {
            adjustCustom(true);
        });

        sub_cycle.setOnClickListener(v-> {
            adjustCustom(false);

        });

        add_cycle_pom.setOnClickListener(v-> {
            adjustCustom(true);
        });

        sub_cycle_pom.setOnClickListener(v-> {
            adjustCustom(false);
        });

        save_cycles.setOnClickListener(v->{
            saveAndUpdateCycles();
        });

        update_cycles.setOnClickListener(v-> {
            confirmedSaveOrUpdate(UPDATING_CYCLES);
        });

        pauseResumeButton.setOnClickListener(v-> {
            switch (mode) {
                case 1:
                    if (!customHalted) pauseAndResumeTimer(PAUSING_TIMER); else pauseAndResumeTimer(RESUMING_TIMER);
                    break;
                case 2:
                    //If counting up, use the same pause/resume feature as other modes. If not, iterate through the four pause cycles.
                    if (!breaksOnlyHalted) pauseAndResumeTimer(PAUSING_TIMER); else if (!breaksOnlyAreCountingUp) {
                        switch (movingBOCycle) {
                            case 0:
                                pauseAndResumeTimer(RESUMING_TIMER);
                                break;
                            case 1:
                                pauseAndResumeTimer(RESETTING_TIMER); movingBOCycle++;
                                break;
                            case 2:
                                pauseAndResumeTimer(RESTARTING_TIMER);
                                movingBOCycle = 0;
                                break;
                        }
                    } else pauseAndResumeTimer(RESUMING_TIMER);
                    break;
                case 3:
                    if (!pomHalted) pauseAndResumeTimer(PAUSING_TIMER);
                    else pauseAndResumeTimer(RESUMING_TIMER);
                    break;
                case 4:
                    if (stopwatchHalted) pauseAndResumeTimer(RESUMING_TIMER); else pauseAndResumeTimer(PAUSING_TIMER);
                    break;
            }
        });

        cycle_reset.setOnClickListener(v -> {
            boolean clearIt = false;
            if (cycle_reset.getText().equals(getString(R.string.clear_cycles))) {
                switch (mode) {
                    case 1:
                        if (customCyclesDone>0) clearIt = true;
                        break;
                    case 2:
                        if (breaksOnlyCyclesDone>0) clearIt = true;
                        break;
                    case 3:
                        if (pomCyclesDone>0) clearIt = true;
                        break;
                }
                if (clearIt) cycle_reset.setText(R.string.confirm_cycle_reset);
            } else if (cycle_reset.getText().equals(getString(R.string.confirm_cycle_reset))) {
                switch (mode) {
                    case 1:
                        customCyclesDone = 0;
                        break;
                    case 2:
                        breaksOnlyCyclesDone = 0;
                        break;
                    case 3:
                        pomCyclesDone = 0;
                        break;
                }
                cycle_reset.setText(R.string.clear_cycles);
                cycles_completed.setText(getString(R.string.cycles_done, "0"));
            } else if (cycle_reset.getText().equals(getString(R.string.clear_laps)) && lapsNumber>0) {
                resetEntries = newEntries;
                currentLapList.clear();
                savedLapList.clear();
                lapAdapter.notifyDataSetChanged();
                lapsNumber = 0;
                msReset = 0;
                cycles_completed.setText(getString(R.string.laps_completed, String.valueOf(0)));
            }
        });

        skip.setOnClickListener(v -> {
            skipRound();
        });

        circle_reset.setOnClickListener(v-> {
            if (mode!=3) resetTimer(); else {
                if (reset.getText().equals(getString(R.string.reset))) reset.setText(R.string.confirm_cycle_reset);
                else {
                    reset.setText(R.string.reset);
                    resetTimer();
                }
            }
        });

        new_lap.setOnClickListener(v -> {
            double newSeconds = msReset/60;
            double newMinutes = newSeconds/60;

            double savedMinutes = 0;
            double savedSeconds = 0;
            double savedMs = 0;

            String[] holder = null;
            if (!stopwatchHalted) {
                if (savedLapList.size()>0) {
                    holder = (savedLapList.get(savedLapList.size()-1).split(":", 3));
                    savedMinutes = newMinutes + Integer.parseInt(holder[0]);
                    savedSeconds = newSeconds + Integer.parseInt(holder[1]);
                    savedMs = newMsConvert + Integer.parseInt(holder[2]);

                    if (savedMs>99) {
                        savedMs = savedMs-100;
                        savedSeconds +=1;
                    }
                    if (savedSeconds>99){
                        savedSeconds = savedSeconds-100;
                        savedMinutes +=1;
                    }
                    savedEntries = String.format(Locale.getDefault(), "%02d:%02d:%02d", (int) savedMinutes, (int) savedSeconds, (int) savedMs);
                } else {
                    savedEntries = String.format(Locale.getDefault(), "%02d:%02d:%02d", (int) minutes, (int) seconds, savedMsConvert);
                }

                newEntries = String.format(Locale.getDefault(), "%02d:%02d:%02d", (int) newMinutes, (int) newSeconds, newMsConvert);

                currentLapList.add(newEntries);
                savedLapList.add(savedEntries);
                lapLayout.scrollToPosition(savedLapList.size()-1);
                lapAdapter.notifyDataSetChanged();

                lapsNumber++;
                cycles_completed.setText(getString(R.string.laps_completed, String.valueOf(lapsNumber)));

                msReset = 0;
                msConvert2 = 0;
            }
        });

        next_round.setOnClickListener(v-> {
            if (next_round.getAlpha()==1.0f) {
                nextCountUpRound();
                next_round.setAlpha(0.3f);
                next_round.setEnabled(false);
            }
        });

        fadeInDot = new Runnable() {
            @Override
            public void run() {
                if (dotAlpha+25 <=255) {
                    dotAlpha+=25;
                    mHandler.postDelayed(this, 35);
                    dotDraws.fadeDotDraw(dotAlpha, true);
                } else {
                    add_cycle.setClickable(true);
                    sub_cycle.setClickable(true);
                    mHandler.removeCallbacks(this);
                }
            }
        };

        fadeOutDot = new Runnable() {
            @Override
            public void run() {
                if (dotAlpha-25 >=0) {
                    dotAlpha-=25;
                    mHandler.postDelayed(this, 35);
                    dotDraws.fadeDotDraw(dotAlpha, false);
                } else {
                    if (mode==3){
                        pomValuesTime.clear();

                    } else dotDraws.fadeDotDraw(-1, false);
                    mHandler.removeCallbacks(this);
                }
            }
        };

        secondsUpSetRunnable = new Runnable() {
            @Override
            public void run() {
                countUpMillisSets +=50;
                timeLeft.setText(convertSeconds((countUpMillisSets) /1000));
                timePaused.setText(convertSeconds((countUpMillisSets) /1000));
                customSetTimeUP.set((int) (customSetTimeUP.size()-numberOfSets), countUpMillisSets);
                drawDots(1);
                mHandler.postDelayed(this, 50);
            }
        };

        secondsUpBreakRunnable = new Runnable() {
            @Override
            public void run() {
                countUpMillisBreaks +=50;
                timeLeft.setText(convertSeconds((countUpMillisBreaks) /1000));
                timePaused.setText(convertSeconds((countUpMillisBreaks) /1000));
                customBreakTimeUP.set((int) (customBreakTimeUP.size()-numberOfBreaks), countUpMillisBreaks);
                drawDots(2);
                mHandler.postDelayed(this, 50);
            }
        };

        secondsUpBORunnable = new Runnable() {
            @Override
            public void run() {
                countUpMillisBO +=50;
                timeLeft2.setText(convertSeconds((countUpMillisBO) /1000));
                timePaused2.setText(convertSeconds((countUpMillisBO) /1000));
                breaksOnlyTimeUP.set((int) (breaksOnlyTimeUP.size()-numberOfBreaksOnly), countUpMillisBO);
                drawDots(3);
                mHandler.postDelayed(this, 50);
            }
        };
    }

    public void startObjectAnimator() {
        //Always set to false before new round begins, so fade smooth resets. May cause super rare overlap in fade out dot alphas, but this can be solved by just using separate ascent variables.
        switch (mode) {
            case 1:
                if (!onBreak) {
                    if (!setBegun) {
                        //Used for pause/resume and fading text in (i.e. timeLeft or timePaused).
                        customHalted = false;
                        //Ensures each new dot fade begins @ full alpha.
                        dotDraws.setAlpha();
                        //Returns and sets our setMillis value to the first position in our Array.
                        if (numberOfSets>0) setMillis = newMillis(true);
                        objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", (int) maxProgress, 0);
                        objectAnimator.setInterpolator(new LinearInterpolator());
                        objectAnimator.setDuration(setMillis);
                        objectAnimator.start();
                        setBegun = true;
                        modeOneTimerEnded = false;
                    } else {
                        setMillis = setMillisUntilFinished;
                        if (objectAnimator!=null) objectAnimator.resume();
                    }
                } else {
                    if (!breakBegun) {
                        //Used for pause/resume and fading text in (i.e. timeLeft or timePaused).
                        customHalted = false;
                        dotDraws.setAlpha();
                        //Returns and sets our breakMillis value to the first position in our Array.
                        if (numberOfBreaks>0) breakMillis = newMillis(false);
                        objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", (int) maxProgress, 0);
                        objectAnimator.setInterpolator(new LinearInterpolator());
                        objectAnimator.setDuration(breakMillis);
                        objectAnimator.start();
                        breakBegun = true;
                    } else {
                        breakMillis = breakMillisUntilFinished;
                        if (objectAnimator!=null) objectAnimator.resume();
                    }
                }
                //If true, triggers an animation on tab switch. Used since we disable any mode's animations if not tabbed in (to prevent overlap).
                modeOneBetweenRounds = false;
                break;
            case 2:
                if (!breakOnlyBegun) {
                    //Ensures any features meant for running timer cannot be executed here.
                    breaksOnlyHalted = false;
                    dotDraws.setAlpha();
                    //Returns and sets our breakOnlyMillis value to the first position in our Array.
                    if (breaksOnlyTime.size()>0) breakOnlyMillis = newMillis(true);
                    objectAnimator2 = ObjectAnimator.ofInt(progressBar2, "progress", (int) breaksOnlyProgressPause, 0);
                    objectAnimator2.setInterpolator(new LinearInterpolator());
                    objectAnimator2.setDuration(breakOnlyMillis);
                    objectAnimator2.start();
                    breakOnlyBegun = true;
                    modeTwoTimerEnded = false;
                } else {
                    breakOnlyMillis = breakOnlyMillisUntilFinished;
                    if (objectAnimator2!=null) objectAnimator2.resume();
                }
                modeTwoBetweenRounds = false;
                break;
            case 3:
                if (!pomBegun) {
                    //Ensures any features meant for running timer cannot be executed here.
                    pomHalted = false;
//                    pomMillis = 5000;
                    dotDraws.setAlpha();
                    objectAnimator3 = ObjectAnimator.ofInt(progressBar3, "progress", (int) pomProgressPause, 0);
                    objectAnimator3.setInterpolator(new LinearInterpolator());
                    objectAnimator3.setDuration(pomMillis);
                    objectAnimator3.start();
                    pomBegun = true;
                    activePomCycle = true;
                    modeThreeTimerEnded = false;
                } else {
                    pomMillis = pomMillisUntilFinished;
                    if (objectAnimator3!=null) objectAnimator3.resume();
                }
                modeThreeBetweenRounds = false;
                break;
        }
    }

    public void startSetTimer() {
        AtomicBoolean textSizeReduced = new AtomicBoolean(false);
        if (setMillis >= 59000) textSizeReduced.set(true);

        setNewText(timeLeft, timeLeft,(setMillis + 999)/1000);
        timer = new CountDownTimer(setMillis, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                customProgressPause = (int) objectAnimator.getAnimatedValue();
                setMillis = millisUntilFinished;

                //Used to fade in textView from active timers. Since setting a new textView (as we do every tick) resets the alpha value, we need to continue to adjust the alpha each tick.
                if (fadeCustomTimer) {
                    if (customAlpha<0.25) timeLeft.setAlpha(customAlpha+=0.04);
                    else if (customAlpha<0.5) timeLeft.setAlpha(customAlpha+=.08);
                    else if (customAlpha<1) timeLeft.setAlpha(customAlpha+=.12);
                    else if (customAlpha>=1) fadeCustomTimer = false;
                }

                if (mode==1) {
                    if (textSizeReduced.get()) if (setMillis<59000) {
                        changeTextSize(valueAnimatorUp, timeLeft, timePaused);
                        textSizeReduced.set(false);
                    }
                    //Displays Long->String conversion of time left every tick.
                    timeLeft.setText(convertSeconds((setMillis + 999)/1000));
                    timePaused.setText(convertSeconds((setMillis + 999)/1000));
                    drawDots(1);
                }
                if (setMillis<500) timerDisabled = true;
            }

            @Override
            public void onFinish() {
                //Open to animations during tab switches until next Object Animator begins.
                modeOneBetweenRounds = true;
                //Used for pause/resume and fading text in (i.e. timeLeft or timePaused). We set it here so that when we switch tabs, the correct textView is shown.
                customHalted = true;
                //Used in startObjectAnimator, and dictates if we are on a set or break.
                onBreak = true;
                //Used in startObjectAnimator to determine whether we're using a new animator + millis, or resuming one from a pause.
                setBegun = false;
                //Creates a new alpha animation, and sets this timer's progress bar/text on it.
                if (mode==1) {
                    animateEnding(false);
                    progressBar.setAnimation(endAnimation);
                    timeLeft.setAnimation(endAnimation);
                }

                if (mode==1) {
                    //Smooths out end fade.
                    fadeVar = 1;
                    mHandler.post(endFade);
                }

                timeLeft.setText("0");
                customProgressPause = maxProgress;
                mHandler.postDelayed(() -> {
                    //Re-enabling timer clicks.
                    timerDisabled = false;
                    //Removing the last used set at end of post-delayed runnable to allow time for its dot to fade out via endFade runnable above.
                    numberOfSets--;
                    if (numberOfSets>0) setMillis = customSetTime.get((int) (customSetTime.size()-numberOfSets));
                    endAnimation.cancel();
                    dotDraws.setAlpha();
                    if (!breaksAreCountingUp) {
                        startObjectAnimator();
                        startBreakTimer();
                    } else {
                        progressBar.setProgress(10000);
                        mHandler.post(secondsUpBreakRunnable);
                        next_round.setAlpha(1.0f);
                        next_round.setEnabled(true);
                    }
                },750);
            }
        }.start();
    }

    public void startBreakTimer() {
        if (mode==1) {
            AtomicBoolean textSizeReduced = new AtomicBoolean(false);
            if (breakMillis >= 59000) textSizeReduced.set(true);

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

                    //Prevents overlap once timer has begin, since conditional only needs to be true to start the series of ticks.
                    if (mode==1) {
                        if (textSizeReduced.get()) if (breakMillis<59000) {
                            changeTextSize(valueAnimatorUp, timeLeft, timePaused);
                            textSizeReduced.set(false);
                        }
                        timeLeft.setText(convertSeconds((millisUntilFinished +999) / 1000));
                        timePaused.setText(convertSeconds((millisUntilFinished +999) / 1000));
                        drawDots(2);
                    }
                    if (breakMillis<500) timerDisabled = true;
                }

                @Override
                public void onFinish() {
                    modeOneBetweenRounds = true;
                    //Used for pause/resume and fading text in (i.e. timeLeft or timePaused). We set it here so that when we switch tabs, the correct textView is shown.
                    customHalted = true;
                    //Used in startObjectAnimator, and dictates if we are on a set or break.
                    onBreak = false;
                    //Used in startObjectAnimator to determine whether we're using a new animator + millis, or resuming one from a pause.
                    breakBegun = false;
                    timeLeft.setText("0");

                    if (mode==1) {
                        animateEnding(false);
                        progressBar.setAnimation(endAnimation);
                        timeLeft.setAnimation(endAnimation);
                    }
                    if (numberOfBreaks >0) {
                        customProgressPause = maxProgress;
                        //Smooths out end fade.
                        fadeVar = 2;
                        mHandler.post(endFade);

                        mHandler.postDelayed(() -> {
                            //Removing the last used set at end of post-delayed runnable to allow time for its dot to fade out via endFade runnable above.
                            //Must execute here for conditional below to work.
                            numberOfBreaks--;
                            if (numberOfBreaks>0) breakMillis = customBreakTime.get((int) (customBreakTime.size()-numberOfBreaks));
                            //Re-enabling timer clicks. Used regardless of numberOfBreaks.
                            timerDisabled = false;

                            //If numberOfBreaks has not been reduced to 0 within this runnable, begin our next set. Otherwise, do end cycle stuff.
                            if (numberOfBreaks!=0) {
                                if (!setsAreCountingUp) {
                                    startObjectAnimator();
                                    startSetTimer();
                                } else {
                                    progressBar.setProgress(10000);
                                    mHandler.post(secondsUpSetRunnable);
                                    next_round.setAlpha(1.0f);
                                    next_round.setEnabled(true);
                                }
                                endAnimation.cancel();
                            } else {
                                //Used to call resetTimer() in pause/resume method. Separate than our disable method.
                                modeOneTimerEnded = true;
                                customCyclesDone++;
                                drawDots(0);
                                cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));
                            }
                        },750);
                    }
                }
            }.start();
        } else if (mode==2) {
            AtomicBoolean textSizeReduced = new AtomicBoolean(false);
            if (breakOnlyMillis >= 59000) textSizeReduced.set(true);

            timer2 = new CountDownTimer(breakOnlyMillis, 50) {
                @Override
                public void onTick(long millisUntilFinished) {
                    breaksOnlyProgressPause = (int) objectAnimator2.getAnimatedValue();
                    breakOnlyMillis = millisUntilFinished;

                    if (fadeCustomTimer) {
                        if (customAlpha<0.25) timeLeft2.setAlpha(customAlpha+=0.04);
                        else if (customAlpha<0.5) timeLeft2.setAlpha(customAlpha+=.08);
                        else timeLeft2.setAlpha(customAlpha+=.12);
                    }
                    if (customAlpha >=1) fadeCustomTimer = false;

                    if (mode==2) {
                        if (textSizeReduced.get()) if (breakOnlyMillis<59000) {
                            changeTextSize(valueAnimatorUp, timeLeft2, timePaused2);
                            textSizeReduced.set(false);
                        }
                        timeLeft2.setText(convertSeconds((millisUntilFinished +999) / 1000));
                        timePaused2.setText(convertSeconds((millisUntilFinished +999) / 1000));
                        drawDots(3);
                    }
                    if (breakOnlyMillis<500) boTimerDisabled = true;
                }

                @Override
                public void onFinish() {
                    modeTwoBetweenRounds = true;
                    //Ensures any features meant for a running timer cannot be executed here.
                    breaksOnlyHalted = true;
                    breakOnlyBegun = false;
                    timeLeft2.setText("0");

                    if (mode==2) {
                        animateEnding(false);
                        progressBar2.setAnimation(endAnimation2);
                        timeLeft2.setAnimation(endAnimation2);
                    }
                    if (numberOfBreaksOnly>0) {
                        breaksOnlyProgressPause = maxProgress;
                        //Smooths out end fade.
                        //Todo: fadeVar is issue. Likely because break count is lowered and our canvas draws the next one dark. We declare fadeVar here for use w/ endFade.
                        if (mode==2){
                            fadeVar = 3;
                            mHandler.post(endFade);
                        }
                        //Activates RESETTING_TIMER in pauseAndResume. Var gets set to 2 on next click in pauseAndResume, and then resets to 0.
                        movingBOCycle=1;

                        mHandler.postDelayed(() -> {
                            //Removing the last used set at end of post-delayed runnable to allow time for its dot to fade out via endFade runnable above.
                            //Must execute here for conditional below to work.
                            numberOfBreaksOnly--;
                            if (numberOfBreaksOnly>0) breakOnlyMillis = breaksOnlyTime.get((int) (breaksOnlyTime.size()-numberOfBreaksOnly));
                            boTimerDisabled = false;
                            //If numberOfBreaksOnly has been reduced to 0 in this runnable, do end cycle stuff. Since we are pausing between breaks in this mode anyway, we are doing less than the set/break combos.
                            if (numberOfBreaksOnly==0){
                                //Used to call resetTimer() in pause/resume method. Separate than our disable method.
                                modeTwoTimerEnded = true;
                                breaksOnlyCyclesDone++;
                                cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(breaksOnlyCyclesDone)));
                                drawDots(0);
                            }
                        },750);
                    };
                    if (mode==2) overtime.setVisibility(View.VISIBLE);
                    isOvertimeRunning = true;
                    ot = new Runnable() {
                        @Override
                        public void run() {
                            overSeconds +=1;
                            overtime.setText(getString(R.string.overtime, String.valueOf(overSeconds)));
                            mHandler.postDelayed(this, 1000);
                        }
                    };
                    mHandler.post(ot);
                }
            }.start();
        }
    }

    public void startPomTimer() {
        timer3 = new CountDownTimer(pomMillis, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                pomProgressPause = (int) objectAnimator3.getAnimatedValue();
                pomMillis = millisUntilFinished;

                if (fadeCustomTimer) {
                    if (pomAlpha<0.25) timeLeft3.setAlpha(pomAlpha+=0.04);
                    else if (pomAlpha<0.5) timeLeft3.setAlpha(pomAlpha+=.08);
                    else if (pomAlpha<1) timeLeft3.setAlpha(pomAlpha+=.12);
                    else if (pomAlpha>=1) fadeCustomTimer = false;
                }
                if (pomAlpha >=1) fadeCustomTimer = false;

                if (mode==3) {
                    timeLeft3.setText(convertSeconds((pomMillis+999)/1000));
                    drawDots(4);
                }
            }

            @Override
            public void onFinish() {
                modeThreeBetweenRounds = true;
                //Ensures any features meant for a running timer cannot be executed here.
                pomHalted = true;
                pomBegun = false;
                timeLeft3.setText("0");
                pomProgressPause = maxProgress;

                switch (pomDotCounter) {
                    case 1: case 3: case 5: case 7:
                        pomMillis = pomMillis1;
                        break;
                    case 2: case 4: case 6:
                        pomMillis = pomMillis2;
                        break;
                    case 8:
                        pomMillis = pomMillis3;
                        break;
                }

                if (mode==3) {
                    animateEnding(false);
                    progressBar3.setAnimation(endAnimation3);
                    timeLeft3.setAnimation(endAnimation3);
                }
                if (pomDotCounter<9) {
                    //Ensures any features meant for a running timer cannot be executed here.
                    customHalted = true;
                    //Disabling pause/resume clicks until animation finishes.
                    pomTimerDisabled = true;
                    if (mode==3) {
                        //Smooths out end fade.
                        fadeVar = 4;
                        mHandler.post(endFade);
                    }
                    mHandler.postDelayed(() ->{
                        //Counter must increase here for conditional below to work.
                        pomDotCounter++;
                        //Re-enabling timer clicks. Used regardless of number of rounds left.
                        pomTimerDisabled = false;

                        if (pomDotCounter!=9) {
                            startObjectAnimator();
                            startPomTimer();
                            endAnimation.cancel();
                        } else {
                            modeThreeTimerEnded = true;
                            pomCyclesDone +=1;
                            drawDots(0);
                            cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(pomCyclesDone)));
                        }
                    }, 750);
                }
            }
        }.start();
    }

//      if (Build.VERSION.SDK_INT >= 26) {
//      vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
//      } else {
//      vibrator.vibrate(pattern1, 0);
//      }

    public void adjustCustom(boolean adding) {
        //Converts editText to long and then convert+sets these values to setValue/breakValue depending on which editTexts are being used.
        if (first_value_edit.isShown()) setEditValueBounds(true);
        if (second_value_edit.isShown()) setEditValueBounds(false);
        canSaveOrUpdate(true);
        if (adding) {
            switch (mode) {
                case 1:
                    if (customSetTime.size() <8) {
                        customSetTime.add(setValue * 1000);
                        customSetTimeUP.add((long) 0);
                        customBreakTime.add(breakValue * 1000);
                        customBreakTimeUP.add((long) 0);
                        canSaveOrUpdate(true);
                        emptyCycle = false;
                    } else {
                        Toast.makeText(getApplicationContext(), "Max rounds reached!", Toast.LENGTH_SHORT).show();
                        //Return to save on resources. Also to avoid unnecessary fading in Pom mode.
                        return;
                    }
                    break;
                case 2:
                    if (breaksOnlyTime.size() < 8) {
                        breaksOnlyTime.add(breaksOnlyValue * 1000);
                        breaksOnlyTimeUP.add((long) 0);
                        emptyCycle = false;
                    } else {
                        Toast.makeText(getApplicationContext(), "Max rounds reached!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    break;
                case 3:
                    if (pomValuesTime.size()==0) {
                        for (int i=0; i<3; i++)  {
                            pomValuesTime.add(pomValue1); pomValuesTime.add(pomValue2);
                        }
                        pomValuesTime.add(pomValue1);
                        pomValuesTime.add(pomValue3);
                        emptyCycle = false;
                        //Necessary for Pomodoro mode, since we disable it in subtraction for the sake of fading out the dot text.
                        timerDisabled = false;
                    } else {
                        Toast.makeText(getApplicationContext(), "Pomodoro cycle already loaded!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    break;
            }
            dotAlpha = 5;
            //Resetting the "add/subtract fade" alpha to 5 to ensure we always fade in from black.
            dotDraws.fadeDotDraw(5, true);
            mHandler.post(fadeInDot);
            //emptyCycle toggles to true if we switch tab into a timer w/ no rounds. This turns it false if we've added one.
            emptyCycle = false;
        } else {
            //Using a specific position to delete round if one is selected, otherwise deleting the most recently added.
            if (selectingRounds) {
                deleteSelectedRound();
                selectingRounds = false;
            } else {
                switch (mode) {
                    case 1:
                        if (customSetTime.size()>0) {
                            customSetTime.remove(customSetTime.size() - 1);
                            customSetTimeUP.remove(customSetTimeUP.size() -1);
                            customBreakTime.remove(customBreakTime.size() - 1);
                            customBreakTimeUP.remove(customBreakTimeUP.size() -1);
                            canSaveOrUpdate(true);
                            if (customSetTime.size()==0) {
                                timePaused.setTextSize(90f);
                                timePaused.setText("?");
                                emptyCycle = true;
                                drawDots(0);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Nothing left to remove!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        break;
                    case 2:
                        if (breaksOnlyTime.size()>0) {
                            breaksOnlyTime.remove(breaksOnlyTime.size() - 1);
                            breaksOnlyTimeUP.remove(breaksOnlyTimeUP.size()-1);
                            canSaveOrUpdate(true);
                            if (breaksOnlyTime.size()==0) {
                                emptyCycle = true;
                                timePaused.setTextSize(90f);
                                timePaused2.setText("?");
                                drawDots(0);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Nothing left to remove!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        break;
                    case 3:
                        //If a cycle exists, disable the timer because we are removing the cycle via our fadeOutDot runnable which will not complete until the fade is done. Adding a cycle will re-enable the timer through populateCycleUI().
                        if (pomValuesTime.size()!=0) pomTimerDisabled = true; else {
                            emptyCycle = true;
                            timePaused3.setTextSize(90f);
                            timePaused3.setText("?");
                            Toast.makeText(getApplicationContext(), "No Pomodoro cycle to clear!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        break;
                }
            }
            dotAlpha = 255;
            dotDraws.fadeDotDraw(255, false);
            mHandler.post(fadeOutDot);
        }
        populateCycleUI();
        saveArrays();
    }

    public void skipRound() {
        if (customSetTime.size()>0) {
            int oldCycle = 0;
            int oldCycle2 = 0;
            int oldCycle3 = 0;
            switch (mode) {
                case 1:
                    //Setting timer to Paused mode.
                    customHalted = true;
                    if (timer != null) timer.cancel();
                    if (objectAnimator != null) objectAnimator.cancel();

                    //If not on last round, reset progress bar.
                    if (numberOfSets >0 && numberOfBreaks >0) {
                        setBegun = false;
                        customProgressPause = maxProgress;
                        timePaused.setAlpha(1);
                        timeLeft.setAlpha(0);
                        progressBar.setProgress(10000);
                    }
                    //If not on last round (i.e. set OR break has >=2 rounds left), remove set and/or break.
                    if (numberOfSets >0 && numberOfSets == numberOfBreaks) numberOfSets--;
                    if (numberOfBreaks >0 && numberOfBreaks != numberOfSets) {
                        numberOfBreaks--;
                        onBreak = false;
                        oldCycle = customCyclesDone;
                    }
                    //If, AFTER removing the last round, we still have a set/break combo remaining, we set a new textView w/ new millis value.
                    if (numberOfSets >0) {
                        if (!setsAreCountingUp) setNewText(timePaused, timePaused, (newMillis(true)+999)/1000);
                        else {
                            setNewText(timePaused, timePaused, 0);
                            setNewText(timeLeft, timeLeft, 0);
                        }
                    }

                    //If we have reduced the last round's break count (above) to 0, we have skipped the last round in this cycle. Setting our end animation, and the timer text to "0". Essentially duplicating a naturally ended cycle.
                    if (numberOfBreaks == 0) {
                        timePaused.setAlpha(0);
                        timeLeft.setAlpha(1);
                        if (oldCycle == customCyclesDone) {
                            customCyclesDone++;
                            cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));
                        }
                        if (!modeOneTimerEnded) animateEnding(true);
                        progressBar.setProgress(0);
                        timeLeft.setText("0");
                        timePaused.setText("0");
                        modeOneTimerEnded = true;
                    }
                    if (setsAreCountingUp) {
                        mHandler.removeCallbacks(secondsUpSetRunnable);
                        countUpMillisSets = 0;
                    }
                    if (breaksOnlyAreCountingUp) {
                        mHandler.removeCallbacks(secondsUpBreakRunnable);
                        countUpMillisBreaks = 0;
                    }
                    break;
                case 2:
                    breaksOnlyHalted = true;
                    if (timer2 != null) timer2.cancel();
                    if (objectAnimator2 != null) objectAnimator2.cancel();

                    if (numberOfBreaksOnly >0) {
                        numberOfBreaksOnly--;
                        oldCycle2 = breaksOnlyCyclesDone;
                        breakOnlyBegun = false;
                    }
                    if (numberOfBreaksOnly >0) {
                        if (!breaksOnlyAreCountingUp) setNewText(timePaused2, timePaused2, (newMillis(false) +999) / 1000);
                        else {
                            setNewText(timeLeft2, timeLeft2, 0);
                            setNewText(timePaused2, timePaused2, 0);
                        }
                    }

                    if (numberOfBreaksOnly==0) {
                        timePaused2.setAlpha(0);
                        timeLeft2.setAlpha(1);
                        if (oldCycle2 == breaksOnlyCyclesDone) {
                            breaksOnlyCyclesDone++;
                            cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(breaksOnlyCyclesDone)));
                        }
                        if (!modeTwoTimerEnded) animateEnding(true);
                        progressBar2.setProgress(0);
                        timeLeft2.setText("0");
                        timePaused2.setText("0");
                        modeTwoTimerEnded = true;
                    }
                    if (breaksOnlyAreCountingUp) {
                        mHandler.removeCallbacks(secondsUpBORunnable);
                        countUpMillisBO = 0;
                    }
                    break;
                case 3:
                    pomHalted = true;
                    if (timer3!=null) timer3.cancel();

                    if (objectAnimator3!=null) objectAnimator3.cancel();

                    if (pomDotCounter<9) {
                        pomDotCounter++;
                        oldCycle3 = pomCyclesDone;
                        pomBegun = false;
                    }
                    if (pomDotCounter<9) setNewText(timePaused3, timePaused3,(newMillis(false) +999) / 1000);

                    if (pomDotCounter==9) {
                        timePaused3.setAlpha(0);
                        timeLeft3.setAlpha(1);
                        if (oldCycle3 == pomCyclesDone) {
                            pomCyclesDone++;
                            cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(pomCyclesDone)));
                        }
                        if (!modeThreeTimerEnded) animateEnding(true);
                        progressBar3.setProgress(0);
                        timeLeft3.setText("0");
                        timePaused3.setText("0");
                        modeThreeTimerEnded = true;
                    }
                    break;
            }
        }
        resetAndFabToggle(true, false);
        dotDraws.setAlpha();
        drawDots(0);
    }

    public void switchTimer(int mode, boolean halted) {
        //Sets views for respective tab each time one is switched.
        removeTimerViews(); tabViews();
        //If a given timer is halted, sets the reset button to visible.
        if (halted) resetAndFabToggle(true, false);
        if ((mode==1 && !setBegun) || (mode==2 && !breakOnlyBegun) || (mode==3 && !pomBegun)) {
            resetAndFabToggle(false, true);
        } else if (!halted) resetAndFabToggle(false, false);

        fab.setVisibility(View.VISIBLE);
        new_lap.setVisibility(View.INVISIBLE);
        upDown_arrow_one.setVisibility(View.VISIBLE);
        upDown_arrow_two.setVisibility(View.VISIBLE);
        if (editCyclesPopupWindow.isShowing()) editCyclesPopupWindow.dismiss();

        Log.i("testFade", "switching in value is " + lastTextView.getText().toString());
        Log.i("testFade", "switching value 1 " + timePaused.getText().toString());
        Log.i("testFade", "switching value 2 " + timePaused2.getText().toString());
        Log.i("testFade", "switching value 3 " + timePaused3.getText().toString());

        switch (mode) {
            case 1:
                cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));
                if (setMillisUntilFinished==0) setMillisUntilFinished = setMillis;
                if (breakMillisUntilFinished==0) breakMillisUntilFinished = breakMillis;
                if (halted) {
                    setNewText(lastTextView, timePaused, (setMillis + 999)/1000);
                    if (setMillis<100) timePaused.setText("0");
                } else {
                    setNewText(lastTextView, timeLeft, (setMillis + 999)/1000);
                    customAlpha = 0;
                    fadeCustomTimer = true;
                    startObjectAnimator();
                }
                savedCycleAdapter.setView(1);
                //The current values to be added in our add/subtract menu.
                first_value_textView.setText(convertCustomTextView(setValue));
                second_value_textView.setText(convertCustomTextView(breakValue));
                cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));
                canSaveOrUpdate(canSaveOrUpdateCustom);
                if (customSetTime.size() >0) emptyCycle = false; else emptyCycle = true;
                if (modeOneBetweenRounds) animateEnding(true);
                if (endAnimation2!=null && !endAnimation2.hasEnded()) endAnimation2.cancel();

                //Since our count up/down toggle switches from its boolean's previous state, we set it to its opposite here so that the tab switch calls the current count up/down state.
                setsAreCountingUp = !setsAreCountingUp; breaksAreCountingUp = !breaksAreCountingUp;
                countUpMode(true); countUpMode(false);
                break;
            case 2:
                upDown_arrow_two.setVisibility(View.INVISIBLE);
                if (breakOnlyMillisUntilFinished==0) breakOnlyMillisUntilFinished = breakOnlyMillis;
                cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(breaksOnlyCyclesDone)));
                if (halted) {
                    setNewText(lastTextView, timePaused2, (breakOnlyMillis + 999)/1000);
                    if (breakOnlyMillis<100)  timePaused2.setText("0");
                } else {
                    setNewText(lastTextView, timeLeft2, (breakOnlyMillis + 999)/1000);
                    customAlpha = 0;
                    fadeCustomTimer = true;
                    startObjectAnimator();
                }
                savedCycleAdapter.setView(2);
                second_value_textView.setText(convertCustomTextView(breaksOnlyValue));
                cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(breaksOnlyCyclesDone)));
                canSaveOrUpdate(canSaveOrUpdateBreaksOnly);
                if (breaksOnlyTime.size()>0) emptyCycle = false; else emptyCycle = true;
                //Begins end cycle animation if cycle has ended on the mode being switched to. This animation does not trigger if we are in a different mode, because of the overlap that animates the current (non-ended) mode.
                if (modeTwoBetweenRounds) animateEnding(false);

                breaksOnlyAreCountingUp = !breaksOnlyAreCountingUp;
                countUpMode(false);
                break;
            case 3:
                upDown_arrow_two.setVisibility(View.INVISIBLE);
                upDown_arrow_one.setVisibility(View.INVISIBLE);
                savedCycleAdapter.setView(3);
                cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(pomCyclesDone)));
                if (pomMillisUntilFinished==0) pomMillisUntilFinished = pomMillis;
                if (halted) {
                    fadeInText(timePaused3);
                    setNewText(lastTextView, timePaused3, (pomMillis + 999)/1000);
                    if (pomMillis<100) timePaused3.setText("0");
                } else {
                    pomAlpha = 0;
                    fadeCustomTimer = true;
                    startObjectAnimator();
                    setNewText(lastTextView, timeLeft3, (pomMillis + 999)/1000);
                }
                first_value_textView.setText(String.valueOf(pomValue1));
                second_value_textView.setText(String.valueOf(pomValue2));
                third_value_textView.setText(String.valueOf(pomValue3));
                if (pomValuesTime.size()>0) emptyCycle = false; else emptyCycle = true;
                if (modeThreeBetweenRounds) animateEnding(true);
                break;
            case 4:
                //Same animation instance can't be used simultaneously for both TextViews.
                cycles_completed.setText(getString(R.string.laps_completed, String.valueOf(lapsNumber)));
                if (stopwatchHalted) fadeInText(timePaused4);
                else fadeInText(timeLeft4);
                fab.setVisibility(View.INVISIBLE);
                next_round.setVisibility(View.INVISIBLE);
                new_lap.setVisibility(View.VISIBLE);
                if (halted) {
                    new_lap.setAlpha(0.3f);
                    new_lap.setEnabled(false);
                } else {
                    new_lap.setAlpha(1.0f);
                    new_lap.setEnabled(true);
                }
                break;
        }
        dotDraws.setAlpha();
        drawDots(0);
        lastTextView = timePaused;
    }

    //Set to true if we want to run the animation instantly. False if it is timer dependant, since we do not want it triggering on the wrong prog/timer.
    private void animateEnding(boolean setAnimation) {
        endAnimation = new AlphaAnimation(1.0f, 0.0f);
        endAnimation.setDuration(300);
        endAnimation.setStartOffset(20);
        endAnimation.setRepeatMode(Animation.REVERSE);
        endAnimation.setRepeatCount(Animation.INFINITE);

        endAnimation2 = new AlphaAnimation(1.0f, 0.0f);
        endAnimation2.setDuration(300);
        endAnimation2.setStartOffset(20);
        endAnimation2.setRepeatMode(Animation.REVERSE);
        endAnimation2.setRepeatCount(Animation.INFINITE);

        endAnimation3 = new AlphaAnimation(1.0f, 0.0f);
        endAnimation3.setDuration(300);
        endAnimation3.setStartOffset(20);
        endAnimation3.setRepeatMode(Animation.REVERSE);
        endAnimation3.setRepeatCount(Animation.INFINITE);

        if (setAnimation) {
            switch (mode) {
                case 1:
                    if (mode==1) {
                        progressBar.setAnimation(endAnimation);
                        timeLeft.setAnimation(endAnimation);
                    }
                    break;
                case 2:
                    if (mode==2) {
                        progressBar2.setAnimation(endAnimation2);
                        timeLeft2.setAnimation(endAnimation2);
                    }
                    break;
                case 3:
                    if (mode==3) {
                        progressBar3.setAnimation(endAnimation3);
                        timeLeft3.setAnimation(endAnimation3);
                    }
                    break;
            }
        }

    }

    public void changeTextSize(ValueAnimator va, TextView textView, TextView textViewPaused) {
        sizeAnimator = va;
        sizeAnimator.addUpdateListener(animation -> {
            float sizeChange = (float) va.getAnimatedValue();
            textView.setTextSize(sizeChange);
            textViewPaused.setTextSize(sizeChange);
        });
        sizeAnimator.setRepeatCount(0);
        sizeAnimator.start();
    }

    public void setNewText(TextView oldTextView, TextView newTextView, long newTime) {
        boolean fadeTime = false;
        //Retrieves the text String from the previous and current timer's textView and converted it into a long.
        String oldText = (String) oldTextView.getText();
        long oldTime = 0;
        if  (!oldText.equals("") && !oldText.equals("?")) {
            oldText = oldText.replace(":", "");
            oldTime = Long.parseLong(oldText);
        }
        //Compares the previous timer's long value with the one we are switching to. If different, we fade in the new text.
        if (oldTime!=newTime) fadeTime = true;

        if (!oldText.equals("") && !oldText.equals("?")) {
            //Converts the last and current timer's textViews into longs, then compares them. If one is <60 seconds and one is <60 seconds, we change the size of the current textView so it fills our progressBar circle better.
            if (oldTime<60 && newTime>=60) {
                switch (mode) {
                    case 1:
                        changeTextSize(valueAnimatorDown, timeLeft, timePaused); break;
                    case 2:
                        changeTextSize(valueAnimatorDown, timeLeft2, timePaused2); break;
                    case 3:
                        changeTextSize(valueAnimatorDown, timeLeft3, timePaused3); break;
                    case 4:
                        changeTextSize(valueAnimatorDown, timeLeft4,  timePaused4); break;
                }
            } else if (oldTime>=60 && newTime<60) {
                switch (mode) {
                    case 1:
                        changeTextSize(valueAnimatorUp, timeLeft, timePaused); break;
                    case 2:
                        changeTextSize(valueAnimatorUp, timeLeft2, timePaused2); break;
                    case 3:
                        changeTextSize(valueAnimatorUp, timeLeft3, timePaused3); break;
                    case 4:
                        changeTextSize(valueAnimatorUp, timeLeft4,  timePaused4); break;
                }
            }
        }
        //If the timer is HALTED, use our Value Animator. If not, we have a separate method within the timer. This is necessary because our active timer textView is constantly updating, and thus overwrites any new alpha we are trying to set.
        if (fadeTime) {
            switch (mode) {
                case 1:
                    if (customHalted) fadeInText(timePaused); else fadeInText(timeLeft); break;
                case 2:
                    if (breaksOnlyHalted) fadeInText(timePaused2); else fadeInText(timeLeft2); break;
                case 3:
                    if (pomHalted) fadeInText(timePaused3); else fadeInText(timeLeft3); break;
                case 4:
                    if (stopwatchHalted) fadeInText(timePaused4); else fadeInText(timeLeft4); break;
            }
        } else {
            switch (mode) {
                case 1:
                    if (customHalted) timePaused.setAlpha(1); break;
                case 2:
                    if (breaksOnlyHalted) timePaused2.setAlpha(1); break;
                case 3:
                    if (pomHalted) timePaused3.setAlpha(1); break;
                case 4:
                    if (stopwatchHalted) timePaused4.setAlpha(1); break;
            }
        }
        newTextView.setText(convertSeconds(newTime));
    }

    public void fadeInText(TextView textView) {
        if (fadeInObj!=null) fadeInObj.cancel();
        fadeInObj = ObjectAnimator.ofFloat(textView, "alpha", 0.0f, 1.0f);
        fadeInObj.setDuration(1000);
        fadeInObj.start();
    }

    public void fadeOutText(TextView textView) {
        if (fadeOutObj!=null) fadeOutObj.cancel();
        fadeOutObj = ObjectAnimator.ofFloat(textView, "alpha", 1.0f, 0.0f);
        fadeOutObj.setDuration(750);
        fadeOutObj.start();
    }

    public void editAndTextSwitch(boolean removeTextView, int viewRemoved) {
        if (removeTextView) {
            if (viewRemoved == 1) {
                switch (mode) {
                    case 1:
                        if (first_value_textView.isShown()) {
                            first_value_textView.setVisibility(View.INVISIBLE);
                            first_value_edit.setVisibility(View.VISIBLE);
                            first_value_edit_two.setVisibility(View.VISIBLE);
                            first_value_sep.setVisibility(View.VISIBLE);
                            convertEditTime();
                        }
                        if (second_value_edit.isShown() || second_value_edit_two.isShown()) {
                            second_value_textView.setVisibility(View.VISIBLE);
                            second_value_edit.setVisibility(View.GONE);
                            second_value_edit_two.setVisibility(View.GONE);
                            second_value_sep.setVisibility(View.GONE);
                        }
                        break;
                    case 3:
                        if (first_value_textView.isShown()) {
                            first_value_textView.setVisibility(View.INVISIBLE);
                            first_value_single_edit.setVisibility(View.VISIBLE);
                        }
                        if (second_value_single_edit.isShown()) {
                            second_value_single_edit.setVisibility(View.GONE);
                            second_value_textView.setVisibility(View.VISIBLE);
                        }
                        if (third_value_single_edit.isShown()){
                            third_value_single_edit.setVisibility(View.GONE);
                            third_value_textView.setVisibility(View.VISIBLE);
                        }
                        break;
                }
            } else if (viewRemoved == 2) {
                switch (mode) {
                    case 1: case 2:
                        if (second_value_textView.isShown()) {
                            second_value_textView.setVisibility(View.INVISIBLE);
                            second_value_edit.setVisibility(View.VISIBLE);
                            second_value_edit_two.setVisibility(View.VISIBLE);
                            second_value_sep.setVisibility(View.VISIBLE);
                        }
                        if (first_value_edit.isShown() || first_value_edit_two.isShown()) {
                            first_value_textView.setVisibility(View.VISIBLE);
                            first_value_edit.setVisibility(View.GONE);
                            first_value_edit_two.setVisibility(View.GONE);
                            first_value_sep.setVisibility(View.GONE);
                        }
                        convertEditTime();
                        break;
                    case 3:
                        if (first_value_single_edit.isShown()) {
                            first_value_textView.setVisibility(View.VISIBLE);
                            first_value_single_edit.setVisibility(View.GONE);
                        }
                        if (second_value_textView.isShown()) {
                            second_value_single_edit.setVisibility(View.VISIBLE);
                            second_value_textView.setVisibility(View.INVISIBLE);
                        }
                        if (third_value_single_edit.isShown()){
                            third_value_single_edit.setVisibility(View.GONE);
                            third_value_textView.setVisibility(View.VISIBLE);
                        }
                        break;
                }
            } else if (viewRemoved == 3) {
                if (first_value_single_edit.isShown()) {
                    first_value_single_edit.setVisibility(View.GONE);
                    first_value_textView.setVisibility(View.VISIBLE);
                }
                if (second_value_edit.isShown()){
                    second_value_single_edit.setVisibility(View.GONE);
                    second_value_textView.setVisibility(View.VISIBLE);
                }
                third_value_textView.setVisibility(View.INVISIBLE);
                third_value_single_edit.setVisibility(View.VISIBLE);
            }

        } else {
            switch (mode) {
                case 1: case 2:
                    if (viewRemoved == 1) {
                        //If first is shown, second and separator are also shown.
                        if (first_value_edit.isShown()) {
                            first_value_edit.setVisibility(View.GONE);
                            first_value_sep.setVisibility(View.GONE);
                            first_value_edit_two.setVisibility(View.GONE);
                            first_value_textView.setVisibility(View.VISIBLE);
                        }
                    } else if (viewRemoved == 2) {
                        if (second_value_edit.isShown()) {
                            second_value_edit.setVisibility(View.GONE);
                            second_value_sep.setVisibility(View.GONE);
                            second_value_edit_two.setVisibility(View.GONE);
                            second_value_textView.setVisibility(View.VISIBLE);
                        }
                    }
                    break;
                case 3:
                    if (viewRemoved == 1) {
                        if (first_value_single_edit.isShown()) {
                            first_value_single_edit.setVisibility(View.GONE);
                            first_value_textView.setVisibility(View.VISIBLE);
                        }
                    } else if (viewRemoved == 2) {
                        if (second_value_single_edit.isShown()) {
                            second_value_single_edit.setVisibility(View.GONE);
                            second_value_textView.setVisibility(View.VISIBLE);
                        }
                    } else if (viewRemoved == 3) {
                        if (third_value_single_edit.isShown()) {
                            third_value_single_edit.setVisibility(View.GONE);
                            third_value_textView.setVisibility(View.VISIBLE);
                        }
                    }
                    break;
            }
        }
    }

    public void clearArrays(boolean populateList) {
        switch (mode) {
            case 1:
                if (setsArray!=null) setsArray.clear();
                if (breaksArray!=null) breaksArray.clear();
                if (customTitleArray!=null) customTitleArray.clear();

                if (populateList) {
                    for (int i=0; i<cyclesList.size(); i++) {
                        setsArray.add(cyclesList.get(i).getSets());
                        breaksArray.add(cyclesList.get(i).getBreaks());
                        customTitleArray.add(cyclesList.get(i).getTitle());
                    }
                    if (setsArray.size()>0) runOnUiThread(() -> { savedCycleAdapter.notifyDataSetChanged();
                    });
                }
                break;
            case 2:
                if (breaksOnlyArray!=null) breaksOnlyArray.clear();
                if (breaksOnlyTitleArray!=null) breaksOnlyTitleArray.clear();

                if (populateList) {
                    for (int i=0; i<cyclesBOList.size(); i++) {
                        breaksOnlyArray.add(cyclesBOList.get(i).getBreaksOnly());
                        breaksOnlyTitleArray.add(cyclesBOList.get(i).getTitle());
                    }
                    if (breaksOnlyArray.size()>0) runOnUiThread(() -> { savedCycleAdapter.notifyDataSetChanged();
                    });
                }
                break;
            case 3:
                if (pomArray!=null) pomArray.clear();
                if (populateList) {
                    for (int i=0; i<pomCyclesList.size(); i++) {
                        pomArray.add(pomCyclesList.get(0).getFullCycle());
                        pomCyclesTitleArray.add(pomCyclesList.get(0).getTitle());
                    }
                    if (pomArray.size()>0) runOnUiThread(()-> savedCycleAdapter.notifyDataSetChanged());
                }
        }
    }

    public void queryCycles() {
        switch (mode) {
            case 1:
                switch (sortMode) {
                    case 1:
                        cyclesList = cyclesDatabase.cyclesDao().loadCyclesMostRecent();
                        sortCheckmark.setY(14);
                        break;
                    case 2:
                        cyclesList = cyclesDatabase.cyclesDao().loadCycleLeastRecent();
                        sortCheckmark.setY(110);
                        break;
                    case 3:
                        cyclesList = cyclesDatabase.cyclesDao().loadCyclesMostItems();
                        sortCheckmark.setY(206);
                        break;
                    case 4:
                        cyclesList = cyclesDatabase.cyclesDao().loadCyclesLeastItems();
                        sortCheckmark.setY(302);
                        break;
                }
                break;
            case 2:
                switch (sortModeBO) {
                    case 1:
                        cyclesBOList = cyclesDatabase.cyclesDao().loadCyclesMostRecentBO();
                        sortCheckmark.setY(14);
                        break;
                    case 2:
                        cyclesBOList = cyclesDatabase.cyclesDao().loadCycleLeastRecentBO();
                        sortCheckmark.setY(110);
                        break;
                    case 3:
                        cyclesBOList = cyclesDatabase.cyclesDao().loadCyclesMostItemsBO();
                        sortCheckmark.setY(206);
                        break;
                    case 4:
                        cyclesBOList = cyclesDatabase.cyclesDao().loadCyclesLeastItemsBO();
                        sortCheckmark.setY(302);
                }
                break;
            case 3:
                switch (sortModePom) {
                    case 1:
                        pomCyclesList = cyclesDatabase.cyclesDao().loadPomCyclesMostRecent();
                        sortCheckmark.setY(14);
                        break;
                    case 2:
                        pomCyclesList = cyclesDatabase.cyclesDao().loadPomCyclesLeastRecent();
                        sortCheckmark.setY(110);
                        break;
                }
                break;
        }
    }

    public void canSaveOrUpdate(boolean yesWeCan) {
        switch (mode) {
            case 1:
                canSaveOrUpdateCustom = yesWeCan; break;
            case 2:
                canSaveOrUpdateBreaksOnly = yesWeCan; break;
            case 3:
                canSaveOrUpdatePom = yesWeCan; break;
        }

        if ( (mode==1 && canSaveOrUpdateCustom) || (mode==2 && canSaveOrUpdateBreaksOnly) || (mode==3 && canSaveOrUpdatePom)) {
            save_cycles.setTextColor(getResources().getColor(R.color.white));
            update_cycles.setTextColor(getResources().getColor(R.color.white));
            save_cycles.setEnabled(true);
            update_cycles.setEnabled(true);
        } else {
            save_cycles.setTextColor(getResources().getColor(R.color.test_grey));
            update_cycles.setTextColor(getResources().getColor(R.color.test_grey));
            save_cycles.setEnabled(false);
            update_cycles.setEnabled(false);
        }
    }

    //Launches either Sort option, or popUp window w/ option to save.
    public void saveAndUpdateCycles() {
        if (savedCyclePopupWindow!=null && savedCyclePopupWindow.isShowing()){
            sortPopupWindow.showAtLocation(mainView, Gravity.TOP, 325, 10);

            sortRecent.setOnClickListener(v1 -> {
                AsyncTask.execute(() -> {
                    switch (mode) {
                        case 1:
                            sortMode = 1; break;
                        case 2:
                            sortModeBO = 1; break;
                        case 3:
                            sortModePom = 1; break;
                    }
                    queryCycles();
                    runOnUiThread(()-> {
                        sortCheckmark.setY(14);
                        clearArrays(true);
                    });
                });
            });

            sortNotRecent.setOnClickListener(v2 ->{
                AsyncTask.execute(() -> {
                    switch (mode) {
                        case 1:
                            sortMode = 1; break;
                        case 2:
                            sortModeBO = 1; break;
                        case 3:
                            sortModePom = 1; break;
                    }
                    queryCycles();
                    runOnUiThread(()-> {
                        sortCheckmark.setY(110);
                        clearArrays(true);
                    });
                });
            });

            sortHigh.setOnClickListener(v3 -> {
                AsyncTask.execute(() -> {
                    switch (mode) {
                        case 1:
                            sortMode = 1; break;
                        case 2:
                            sortModeBO = 1; break;
                    }
                    queryCycles();
                    runOnUiThread(()-> {
                        sortCheckmark.setY(206);
                        clearArrays(true);
                    });
                });
            });

            sortLow.setOnClickListener(v4 -> {
                AsyncTask.execute(() -> {
                    switch (mode) {
                        case 1:
                            sortMode = 1; break;
                        case 2:
                            sortModeBO = 1; break;
                    }
                    queryCycles();
                    runOnUiThread(()-> {
                        sortCheckmark.setY(302);
                        clearArrays(true);
                    });
                });
            });
            sortCheckmark.setY(0);
            prefEdit.putInt("sortMode", sortMode);
            prefEdit.putInt("sortModeBO", sortModeBO);
            prefEdit.putInt("sortModePom", sortModePom);
            prefEdit.apply();
        } else {
            labelSavePopupWindow.showAtLocation(mainView, Gravity.CENTER, 0, 0);
            edit_header.setText("");

            cancel_header_save.setOnClickListener(v2-> {
                if (labelSavePopupWindow!=null) labelSavePopupWindow.dismiss();
            });

            confirm_header_save.setOnClickListener(v2-> {
                confirm_header_save.setText(R.string.save_cycles);
                confirmedSaveOrUpdate(SAVING_CYCLES);
            });
        }
    }

    //Todo: Change for counting up.
    //Actually saves or updates the cycle.
    public void confirmedSaveOrUpdate(int saveOrUpdate) {
        if ((mode==1 && customSetTime.size()==0) || (mode==2 && breaksOnlyTime.size()==0) || (mode==3 && pomValuesTime.size()==0)) {
            Toast.makeText(getApplicationContext(), "Nothing to save!", Toast.LENGTH_SHORT).show();;
            return;
        }

        AsyncTask.execute(() -> {
            //Defaulting to unique cycle unless otherwise set by retrieveAndselectRounds();
            duplicateCycle = false;
            boolean changeCycle = false;
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMMM d yyyy - hh:mma", Locale.getDefault());

            //Executes gSon->Json conversion and compares new String to db lists, setting duplicateCycles to TRUE if an instance matches.
            retrieveAndCheckCycles();

            switch (mode) {
                case 1:
                    //New instance of the Cycle entity that can be used for insertion. Otherwise, inheriting the instance from onCycleClick callback that uses a specific position to update.
                    if (saveOrUpdate == SAVING_CYCLES) cycles = new Cycles(); else cycles = cyclesList.get(cyclesList.size()-1);

                    if (!cycle_header_text.getText().toString().isEmpty()) {
                        cycles.setTitle(cycle_header_text.getText().toString());
                    } else {
                        String newDate = dateFormat.format(calendar.getTime());
                        cycles.setTitle(newDate);
                    }
                    customTitleArray.add(cycle_header_text.getText().toString());

                    //If cycle is not duplicate OR size is 0 (nothing to duplicate), we proceed with insert/update.
                    if (!duplicateCycle || cyclesList.size()==0) {
                        cycles.setSets(convertedSetList);
                        cycles.setBreaks(convertedBreakList);
                        cycles.setTimeAdded(System.currentTimeMillis());
                        //Todo: Can simply disable cycle count for counting up.
                        cycles.setItemCount(customSetTime.size());
                        if (saveOrUpdate == SAVING_CYCLES){
                            cyclesDatabase.cyclesDao().insertCycle(cycles);
                            //Re-instantiating cycleList with new row added.
                            queryCycles();
                            //Getting ID of latest row entry.
                            customID = cyclesList.get(0).getId();
                            //Saving ID to sharedPref.
                            prefEdit.putInt("customID", customID);
                        } else cyclesDatabase.cyclesDao().updateCycles(cycles);
                        runOnUiThread(() -> {
                            if (labelSavePopupWindow!=null) labelSavePopupWindow.dismiss();
                            canSaveOrUpdate(false);
                        });
                        changeCycle = true;
                    }
                    break;
                case 2:
                    //New instance of the CycleBO entity that can be used for insertion. Otherwise, inheriting the instance from onCycleClick callback that uses a specific position to update.
                    if (saveOrUpdate == SAVING_CYCLES) cyclesBO = new CyclesBO(); else cyclesBO = cyclesBOList.get(cyclesBOList.size()-1);

                    if (!cycle_header_text.getText().toString().isEmpty()) {
                        cyclesBO.setTitle(cycle_header_text.getText().toString());
                    } else {
                        String newDate = dateFormat.format(calendar.getTime());
                        cyclesBO.setTitle(newDate);
                    }
                    breaksOnlyTitleArray.add(cycle_header_text.getText().toString());

                    if (!duplicateCycle || cyclesBOList.size()==0) {
                        cyclesBO.setBreaksOnly(convertedBreakOnlyList);
                        cyclesBO.setTimeAdded(System.currentTimeMillis());
                        cyclesBO.setItemCount(breaksOnlyTime.size());
                        if (saveOrUpdate == SAVING_CYCLES) {
                            cyclesDatabase.cyclesDao().insertBOCycle(cyclesBO);
                            queryCycles();
                            breaksOnlyID = cyclesBOList.get(0).getId();
                            prefEdit.putInt("breaksOnlyID", breaksOnlyID);
                        } else cyclesDatabase.cyclesDao().updateBOCycles(cyclesBO);
                        runOnUiThread(() -> {
                            if (labelSavePopupWindow!=null) labelSavePopupWindow.dismiss();
                            canSaveOrUpdate(false);
                        });
                        changeCycle = true;
                    }
                    break;
                case 3:
                    if (saveOrUpdate == SAVING_CYCLES) pomCycles = new PomCycles(); else pomCycles = pomCyclesList.get(pomCyclesList.size()-1);

                    if (!cycle_header_text.getText().toString().isEmpty()) {
                        pomCycles.setTitle(cycle_header_text.getText().toString());
                    } else {
                        String newDate = dateFormat.format(calendar.getTime());
                        pomCycles.setTitle(newDate);
                    }
                    pomCyclesTitleArray.add(cycle_header_text.getText().toString());

                    if (!duplicateCycle && pomCyclesList.size()==0) {
                        pomCycles.setFullCycle(convertedPomList);
                        pomCycles.setTimeAdded(System.currentTimeMillis());
                        if (saveOrUpdate == SAVING_CYCLES) {
                            cyclesDatabase.cyclesDao().insertPomCycle(pomCycles);
                            queryCycles();
                            pomID = pomCyclesList.get(0).getId();
                            prefEdit.putInt("pomID", pomID);
                        } else cyclesDatabase.cyclesDao().updatePomCycles(pomCycles);
                        runOnUiThread(() -> {
                            if (labelSavePopupWindow!=null) labelSavePopupWindow.dismiss();
                            canSaveOrUpdate(false);
                        });
                        changeCycle = true;
                    }
            }
            prefEdit.apply();
            boolean finalChangeCycle = changeCycle;
            runOnUiThread(()-> {
                if (duplicateCycle) Toast.makeText(getApplicationContext(), "An identical cycle already exists!", Toast.LENGTH_SHORT).show();
                else if (saveOrUpdate == SAVING_CYCLES && finalChangeCycle) Toast.makeText(getApplicationContext(), "Cycle added", Toast.LENGTH_SHORT).show();
                else if (saveOrUpdate == UPDATING_CYCLES && finalChangeCycle) Toast.makeText(getApplicationContext(), "Cycle updated", Toast.LENGTH_SHORT).show();
                cycle_header_text.setText(cycle_header_text.getText().toString());
            });
        });
    }

    //Todo: Change for counting up.
    //Used to retrieve a single cycle within our database. Calls populateUICycle() which sets the Array values into our timer millis values.
    //When recall is TRUE, retrieves the last used ID instance, when recall is FALSE, Uses a positional input from our saved cycle list.
    public void selectRound(int position, boolean recall) {
        AsyncTask.execute(() -> {
            int posHolder = position;
            switch (mode) {
                case 1:
                    String tempSets = "";
                    String tempBreaks = "";
                    if (recall) {
                        cycles = cyclesList.get(customID);
                        tempSets = cyclesList.get(0).getSets();
                        tempBreaks = cyclesList.get(0).getBreaks();
                    } else {
                        queryCycles();
                        cycles = cyclesList.get(posHolder);
                        tempSets = cyclesList.get(posHolder).getSets();
                        tempBreaks = cyclesList.get(posHolder).getBreaks();
                        customID = cyclesList.get(posHolder).getId();
                        prefEdit.putInt("customID", customID);
                    }

                    String[] setSplit = tempSets.split(" - ", 0);
                    String[] breakSplit = tempBreaks.split(" - ", 0);
                    customSetTime.clear();
                    customBreakTime.clear();

                    for (int i=0; i<setSplit.length; i++) {
                        customSetTime.add(Long.parseLong(setSplit[i])*1000);
                        customBreakTime.add(Long.parseLong(breakSplit[i])*1000);
                    }
                    runOnUiThread(() -> cycle_header_text.setText(cycles.getTitle()));
                    break;
                case 2:
                    String tempBreaksOnly = "";
                    if (recall) {
                        cyclesBO = cyclesBOList.get(breaksOnlyID);
                        tempBreaksOnly = cyclesBOList.get(0).getBreaksOnly();
                    } else {
                        queryCycles();
                        cyclesBO = cyclesBOList.get(posHolder);
                        tempBreaksOnly = cyclesBOList.get(posHolder).getBreaksOnly();
                        breaksOnlyID = cyclesBOList.get(posHolder).getId();
                        prefEdit.putInt("breaksOnlyID", breaksOnlyID);
                    }

                    String[] breaksOnlySplit = tempBreaksOnly.split(" - ", 0);
                    breaksOnlyTime.clear();

                    for (int i=0; i<breaksOnlySplit.length; i++) {
                        breaksOnlyTime.add(Long.parseLong(breaksOnlySplit[i])*1000);
                    }
                    runOnUiThread(() -> cycle_header_text.setText(cyclesBO.getTitle()));
                    break;
                case 3:
                    String tempPom = "";
                    if (recall) {
                        pomCycles = pomCyclesList.get(pomID);
                        tempPom = pomCyclesList.get(0).getFullCycle();
                    } else {
                        queryCycles();
                        pomCycles = pomCyclesList.get(posHolder);
                        tempPom = pomCyclesList.get(posHolder).getFullCycle();
                        pomID = pomCyclesList.get(posHolder).getId();
                        prefEdit.putInt("pomID", pomID);
                    }

                    String[] pomSplit = tempPom.split("-", 0);
                    pomValuesTime.clear();

                    for (int i=0; i<pomSplit.length; i++) pomValuesTime.add(Long.parseLong(pomSplit[i]));
                    runOnUiThread(() -> cycle_header_text.setText(pomCycles.getTitle()));
                    break;
            }

            runOnUiThread(() -> {
                saveArrays();
                resetTimer();
                savedCyclePopupWindow.dismiss();
                invalidateOptionsMenu();
                defaultMenu = true;
            });
            prefEdit.apply();
        });
    }

    //Todo: Change for counting up.
    //All convertedLists are used in save/update method.
    private void retrieveAndCheckCycles() {
        queryCycles();
        Gson gson = new Gson();
        ArrayList<Long> tempSets = new ArrayList<>();
        ArrayList<Long> tempBreaks = new ArrayList<>();
        ArrayList<Long> tempBreaksOnly = new ArrayList<>();

        switch (mode) {
            case 1:
                for (int i=0; i<customSetTime.size(); i++) {
                    tempSets.add(customSetTime.get(i) /1000);
                }
                for (int i=0; i<customBreakTime.size(); i++){
                    tempBreaks.add(customBreakTime.get(i)/1000);
                }
                convertedSetList = gson.toJson(tempSets);
                convertedBreakList = gson.toJson(tempBreaks);

                convertedSetList = convertedSetList.replace("\"", "");
                convertedSetList = convertedSetList.replace("]", "");
                convertedSetList = convertedSetList.replace("[", "");
                convertedSetList = convertedSetList.replace(",", " - ");
                convertedBreakList = convertedBreakList.replace("\"", "");
                convertedBreakList = convertedBreakList.replace("]", "");
                convertedBreakList = convertedBreakList.replace("[", "");
                convertedBreakList = convertedBreakList.replace(",", " - ");

                boolean endLoop = false;
                if (cyclesList.size()>0) {
                    for (int i=0; i<cyclesList.size(); i++) {
                        if (!endLoop) {
                            if (cyclesList.get(i).getSets().equals(convertedSetList) && cyclesList.get(i).getBreaks().equals(convertedBreakList) && cyclesList.get(i).getTitle().equals(cycle_header_text.getText().toString())) {
                                duplicateCycle = true; endLoop = true;
                            }
                        }
                        duplicateCycle = endLoop;
                    }
                }
                break;
            case 2:
                for (int i=0; i<breaksOnlyTime.size(); i++) {
                    tempBreaksOnly.add(breaksOnlyTime.get(i)/1000);
                }
                convertedBreakOnlyList = gson.toJson(tempBreaksOnly);
                convertedBreakOnlyList = convertedBreakOnlyList.replace("\"", "");
                convertedBreakOnlyList = convertedBreakOnlyList.replace("]", "");
                convertedBreakOnlyList = convertedBreakOnlyList.replace("[", "");
                convertedBreakOnlyList = convertedBreakOnlyList.replace(",", " - ");

                if (cyclesBOList.size()>0) {
                    for (int i=0; i<cyclesBOList.size(); i++) {
                        if (cyclesBOList.get(i).getBreaksOnly().equals(convertedBreakOnlyList) && cyclesBOList.get(i).getTitle().equals(cycle_header_text.getText().toString()))
                            duplicateCycle = true;
                    }
                }
                break;
            case 3:
                convertedPomList = gson.toJson(pomValuesTime);

                String[] pomSplit = convertedPomList.split(",", 0);
                convertedPomList = "";
                for (int i=0; i<pomSplit.length; i++) {
                    if (pomSplit[i].length()<2) pomSplit[i] = "0" + pomSplit[i];
                }

                convertedPomList = Arrays.toString(pomSplit);
                convertedPomList = convertedPomList.replace("]", "");
                convertedPomList = convertedPomList.replace("[", "");
                convertedPomList = convertedPomList.replace(",", " -");

                if (pomCyclesList.size()>0) {
                    for (int i=0; i<pomCyclesList.size(); i++) {
                        if (pomCyclesList.get(i).getFullCycle().equals(convertedPomList) && pomCyclesList.get(i).getTitle().equals(cycle_header_text.getText().toString()))
                            duplicateCycle = true;
                    }
                }
                break;
        }
    }

    public void setDefaultCustomCycle(boolean forBreaksOnly) {
        if (!forBreaksOnly) {
            customSetTime.clear();
            customBreakTime.clear();
            for (int i = 0; i < 3; i++) {
                customSetTime.add((long) 30 * 1000);
                customBreakTime.add((long) 30 * 1000);
                customSetTimeUP.add((long) 0);
                customBreakTimeUP.add((long) 0);
            }
            setValue = 30;
            breakValue = 30;
        } else {
            breaksOnlyTime.clear();
            for (int i=0; i<3; i++) {
                breaksOnlyTime.add((long) 30 * 1000);
                breaksOnlyTimeUP.add((long) 0);
            }
            breaksOnlyValue = 30;
            startBreaksOnly = breaksOnlyTime.size();
        }
    }

    public void setDefaultPomCycle() {
        pomValuesTime.clear();
        pomValue1 = 25; pomValue2 = 5; pomValue3 = 15;
        for (int i=0; i<3; i++) {
            pomValuesTime.add(pomValue1);
            pomValuesTime.add(pomValue2);
        }
        pomValuesTime.add(pomValue1);
        pomValuesTime.add(pomValue3);
        pomMillis = pomValue1*1000*60;
        pomMillis1 = pomValue1*1000*60;
    }

    //Removes a set or break and calls function to update the millis value.
    public void removeSetOrBreak(boolean onSet) {
        switch (mode) {
            case 1:
                if (onSet) {
                    setMillis = newMillis(true);
                    numberOfSets--;
                } else {
                    breakMillis = newMillis(false);
                    numberOfBreaks--;
                }
                break;
            case 2:
                breakOnlyMillis = newMillis(false);
                numberOfBreaksOnly--;
                break;
        }
        drawDots(0);
    }

    //This works for Pom. Just *60 to have the value reflect each minute's seconds.
    private String convertSeconds(long totalSeconds) {
        DecimalFormat df = new DecimalFormat("00");
        long minutes;
        long remainingSeconds;

        if (totalSeconds >=60) {
            minutes = totalSeconds/60;
            remainingSeconds = totalSeconds % 60;
            return (minutes + ":" + df.format(remainingSeconds));
        } else {
            if (totalSeconds != 5) return String.valueOf(totalSeconds);
            else return "5";
        }
    }

    public String convertCustomTextView(long totalSeconds) {
        DecimalFormat df = new DecimalFormat("00");
        long minutes;
        long remainingSeconds;

        if (totalSeconds >=60) {
            minutes = totalSeconds/60;
            remainingSeconds = totalSeconds % 60;
            String formattedSeconds = df.format(remainingSeconds);
            if (formattedSeconds.length()>2) formattedSeconds = "0" + formattedSeconds;
            return (minutes + " : " + formattedSeconds);
        } else {
            String totalStringSeconds = String.valueOf(totalSeconds);
            if (totalStringSeconds.length()<2) totalStringSeconds = "0" + totalStringSeconds;
            if (totalSeconds<5) return ("0 : 05");
            else return "0 : " + totalStringSeconds;
        }
    }

    public String convertStopwatch(long seconds) {
        long minutes;
        long roundedSeconds;
        DecimalFormat df = new DecimalFormat("0");
        DecimalFormat df2 = new DecimalFormat("00");
        if (seconds>=60) {
            minutes = seconds/60;
            roundedSeconds = seconds % 60;
            if (minutes>=10 && timeLeft4.getTextSize() != 70f) timeLeft4.setTextSize(70f);
            return (df.format(minutes) + ":" + df2.format(roundedSeconds));
        } else {
            if (timeLeft4.getTextSize() != 90f) timeLeft4.setTextSize(90f);
            return df.format(seconds);
        }
    }

    //Called on +/- buttons, which use runnables to change set/break value vars. This method a)sets editTexts to these changing values and b)sets the textView to reflect them.
    public void convertEditTime() {
        editSetSeconds = setValue%60;
        editSetMinutes = setValue/60;
        if (mode==1) {
            editBreakSeconds = breakValue%60;
            editBreakMinutes = breakValue/60;
        } else if (mode==2) {
            editBreakSeconds = breaksOnlyValue%60;
            editBreakMinutes = breaksOnlyValue/60;
        }

        String fvSec = String.valueOf(editSetSeconds);
        String svSec = String.valueOf(editBreakSeconds);
        if (fvSec.length()<2) fvSec = "0" + fvSec;
        if (svSec.length()<2) svSec = "0" + svSec;
        first_value_edit.setText(String.valueOf(editSetMinutes));
        first_value_edit_two.setText(fvSec);
        second_value_edit.setText(String.valueOf(editBreakMinutes));
        second_value_edit_two.setText(svSec);
    }

    public void switchMenu() {
        Runnable r = () -> {
            defaultMenu = false;
            invalidateOptionsMenu();
        };
        mHandler.postDelayed(r, 50);
    }

    //Calls runnables to change set, break and pom values. Sets a handler to increase change rate based on click length. Sets min/max values.
    public void setIncrements(MotionEvent event, Runnable runnable) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //Handler must not be instantiated before this, otherwise the runnable will execute it on every touch (i.e. even on "action_up" removal.
                mHandler = new Handler();
                mHandler.postDelayed(runnable,25);
                mHandler.postDelayed(valueSpeed, 25);
                break;
            case MotionEvent.ACTION_UP:
                mHandler.removeCallbacksAndMessages(null);
                incrementTimer = 10;
        }

        prefEdit.putLong("setValue", setValue);
        prefEdit.putLong("breakValue", breakValue);
        prefEdit.putLong("breaksOnlyValue", breaksOnlyValue);
        prefEdit.apply();
    }

    public void setEditValueBounds(boolean onSets){
        if (onSets) {
            editSetMinutes = Integer.parseInt(first_value_edit.getText().toString());
            editSetSeconds = Integer.parseInt(first_value_edit_two.getText().toString());

            if (editSetSeconds>59) {
                editSetMinutes+=1; editSetSeconds = editSetSeconds - 60;
            }
            if (editSetMinutes>5) editSetMinutes = 5;
            if (editSetMinutes<0) editSetMinutes = 0;
            if (editSetSeconds<0 && editSetMinutes>0) editSetSeconds = 0;
            if (editSetSeconds<5 && editSetMinutes==0) editSetSeconds = 0;

            setValue = (editSetMinutes * 60) + editSetSeconds;
            first_value_textView.setText(convertCustomTextView(setValue));
        } else {
            editBreakMinutes = Integer.parseInt(second_value_edit.getText().toString());
            editBreakSeconds = Integer.parseInt(second_value_edit_two.getText().toString());

            if (editBreakSeconds>59) {
                editBreakMinutes+=1; editBreakSeconds = editBreakSeconds - 60;
            }
            if (editBreakMinutes>5) editBreakMinutes = 5;
            if (editBreakMinutes<0) editBreakMinutes = 0;
            if (editBreakSeconds<0 && editBreakMinutes>0) editBreakSeconds = 0;
            if (editBreakSeconds<5 && editBreakMinutes==0) editBreakSeconds = 5;

            if (mode==1) breakValue = (editBreakMinutes * 60) + editBreakSeconds;
            else if (mode==2) breaksOnlyValue = (editBreakMinutes * 60) + editBreakSeconds;
            second_value_textView.setText(convertCustomTextView(breakValue));
        }
        setTimerValueBounds();
    }

    //This is called via setIncrements() is is called within plus/minus touch listeners.
    public void setTimerValueBounds() {
        switch (mode) {
            case 1: case 2:
                toastBounds(5, 300, setValue); toastBounds(5, 300, breakValue); toastBounds(5, 300, breaksOnlyValue);
                if (setValue<5) setValue = 5; if (breakValue<5) breakValue = 5; if (breaksOnlyValue <5) breaksOnlyValue = 5;
                if (setValue>300) setValue = 300; if (breakValue>300) breakValue =300; if (breaksOnlyValue>300) breaksOnlyValue = 300;
                break;
            case 3:
                toastBounds(15, 90, pomValue1); toastBounds(3, 10, pomValue2); toastBounds(10, 60, pomValue3);
                if (pomValue1>90) pomValue1=90; if (pomValue1<15) pomValue1 = 15;
                if (pomValue2>10) pomValue2=10; if (pomValue2<3) pomValue2=3;
                if (pomValue3<10) pomValue3 = 10; if (pomValue3>60) pomValue3 = 60;
                break;
        }
    }

    public void toastBounds(long min, long max, long value) {
        if (value<min) minReached = true;
        if (value>max) maxReached = true;;
    }

    public void fadeCap(TextView textView) {
        if (minReached || maxReached) {
            minReached = false; maxReached = false;
            Animation fadeCap = new AlphaAnimation(1.0f, 0.3f);
            fadeCap.setDuration(350);
            textView.setAnimation(fadeCap);
        }
    }

    public void saveArrays() {
        Gson gson = new Gson();
        String savedSetArrays = "";
        String savedBreakArrays = "";
        String savedBOArrays = "";
        String savedPomArrays = "";
        String savedTitle = "";
        if (!edit_header.getText().toString().equals("")) savedTitle = edit_header.getText().toString(); else savedTitle = getString(R.string.default_title);
        if (customSetTime.size()>0){
            savedSetArrays = gson.toJson(customSetTime);
            savedBreakArrays = gson.toJson(customBreakTime);
        }
        if (breaksOnlyTime.size()>0) savedBOArrays = gson.toJson(breaksOnlyTime);
        savedPomArrays = gson.toJson((pomValuesTime));

        prefEdit.putString("setArrays", savedSetArrays);
        prefEdit.putString("breakArrays", savedBreakArrays);
        prefEdit.putString("savedBOArrays", savedBOArrays);
        prefEdit.putString("savedPomArrays", savedPomArrays);
        prefEdit.putString("savedTitle", savedTitle);
        prefEdit.apply();
    }

    //Sets millis values based on which round we are on (list size minus number of non-completed rounds).
    //REMEMBER, this is not a void method and we need to do something with its return. If we do not, whatever method we are currently in will RETURN itself.
    public long newMillis(boolean onSets) {
        switch (mode) {
            case 1:
                if (onSets) return customSetTime.get((int) (customSetTime.size()-numberOfSets));
                else return customBreakTime.get((int) (customBreakTime.size()-numberOfBreaks));
            case 2:
                return breaksOnlyTime.get((int) (breaksOnlyTime.size()-numberOfBreaksOnly));
            case 3:
                return pomValuesTime.get(pomDotCounter-1) * 1000 * 60;
            default: return 0;
        }
    }

    public void resetAndFabToggle(boolean resetOn, boolean fabOn) {
        if (resetOn) {
            circle_reset.setAlpha(1.0f); circle_reset.setEnabled(true);
        } else {
            circle_reset.setAlpha(0.3f); circle_reset.setEnabled(false);
        }
        if (fabOn) {
            fab.setAlpha(1.0f); fab.setEnabled(true); add_cycle.setEnabled(true); sub_cycle.setEnabled(true);
        } else {
            fab.setAlpha(0.3f); fab.setEnabled(false); add_cycle.setEnabled(false); sub_cycle.setEnabled(false);
        }
    }

    public void nextCountUpRound() {
        //Setting text here because ending a runnable manipulating them seems to cause threading issues when we want to call an animator on them immediately after.
        timeLeft.setText(convertSeconds((countUpMillisSets) /1000));
        timePaused.setText(convertSeconds((countUpMillisSets) /1000));
        animateEnding(true);
        if (mode==1) {
            if (!onBreak) {
                mHandler.removeCallbacks(secondsUpSetRunnable);
                //For pause/resume and tab switches.
                customHalted = false;
                mHandler.postDelayed(() -> {
                    removeSetOrBreak(true);
                    endAnimation.cancel();
                    onBreak = true;
                    mHandler.removeCallbacks(secondsUpSetRunnable);
                    countUpMillisSets = 0;
                    if (!breaksAreCountingUp) {
                        startObjectAnimator();
                        startBreakTimer();
                    } else {
                        mHandler.post(secondsUpBreakRunnable);
                        next_round.setAlpha(1.0f);
                        next_round.setEnabled(true);
                    }
                }, 1000);
                fadeVar = 1;
            } else {
                if (numberOfBreaks==1) {
                    timeLeft.setText(convertSeconds((countUpMillisBreaks) /1000));
                    timePaused.setText(convertSeconds((countUpMillisBreaks) /1000));
                }
                mHandler.removeCallbacks(secondsUpBreakRunnable);
                //For pause/resume and tab switches.
                customHalted = false;
                mHandler.postDelayed(() -> {
                    removeSetOrBreak(false);
                    onBreak = false;
                    if (numberOfBreaks>0) {
                        countUpMillisBreaks = 0;
                        if (!setsAreCountingUp) {
                            startObjectAnimator();
                            startSetTimer();
                        } else {
                            mHandler.post(secondsUpSetRunnable);
                            next_round.setAlpha(1.0f);
                            next_round.setEnabled(true);
                        }
                        endAnimation.cancel();
                    } else {
                        modeOneTimerEnded = true;
                        customCyclesDone++;
                        cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));
                    }
                }, 1000);
                fadeVar = 2;
            }
        } else if (mode==2) {
            if (numberOfBreaksOnly==1) {
                timeLeft2.setText(convertSeconds((countUpMillisBO) /1000));
                timePaused2.setText(convertSeconds((countUpMillisBO) /1000));
            }
            mHandler.removeCallbacks(secondsUpBORunnable);
            breaksOnlyHalted = false;
            mHandler.postDelayed(() -> {
                removeSetOrBreak(false);
                if (numberOfBreaksOnly>0) {
                    countUpMillisBO = 0;
                    progressBar2.setProgress(10000);
                    mHandler.post(secondsUpBORunnable);
                    next_round.setAlpha(1.0f);
                    next_round.setEnabled(true);
                    endAnimation.cancel();
                } else {
                    modeTwoTimerEnded = true;
                    breaksOnlyCyclesDone++;
                    cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(breaksOnlyCyclesDone)));
                }
            }, 1000);
            fadeVar = 3;
        }
        mHandler.post(endFade);
        dotDraws.setAlpha();
    }

    public void countUpMode(boolean onSet) {
        if (mode==1) {
            if (onSet) {
                //Moving to COUNT UP mode.
                if (!setsAreCountingUp) {
                    setsAreCountingUp = true;
                    upDown_arrow_one.setTag(COUNtING_UP);
                    upDown_arrow_one.setImageResource(R.drawable.arrow_up);
                    dotDraws.countingUpSets(true);
                    dotDraws.setTime(customSetTimeUP);
                    dotDraws.customDrawSet(customSetTimeUP.size(), numberOfSets, 1);
                    timeLeft.setText("0");
                    timePaused.setText("0");
                    prefEdit.putBoolean("setCountUpMode", true);
                } else {
                    //Moving to COUNT DOWN mode.
                    setsAreCountingUp = false;
                    upDown_arrow_one.setTag(COUNTING_DOWN);
                    upDown_arrow_one.setImageResource(R.drawable.arrow_down);
                    upDown_arrow_one.setTag(1);
                    dotDraws.countingUpSets(false);
                    dotDraws.setTime(customSetTime);
                    dotDraws.customDrawSet(customSetTime.size(), numberOfSets, 1);
                    timePaused.setText(convertSeconds((setMillis+999)/1000));
                    timeLeft.setText(convertSeconds((setMillis+999)/1000));
                    prefEdit.putBoolean("setCountUpMode", false);
                }
            } else {
                //Moving to COUNT UP mode.
                if (!breaksAreCountingUp) {
                    breaksAreCountingUp = true;
                    upDown_arrow_two.setTag(COUNtING_UP);
                    upDown_arrow_two.setImageResource(R.drawable.arrow_up);
                    dotDraws.countingUpBreaks(true);
                    dotDraws.breakTime(customBreakTimeUP);
                    dotDraws.customDrawBreak(customBreakTimeUP.size(), numberOfBreaks);
                    prefEdit.putBoolean("breakCountUpMode", true);
                } else {
                    //Moving to COUNT DOWN mode.
                    breaksAreCountingUp = false;
                    upDown_arrow_two.setTag(COUNTING_DOWN);
                    upDown_arrow_two.setImageResource(R.drawable.arrow_down);
                    dotDraws.countingUpBreaks(false);
                    dotDraws.breakTime(customBreakTime);
                    dotDraws.customDrawBreak(customBreakTime.size(), numberOfBreaks);
                    prefEdit.putBoolean("breakCountUpMode", false);
                }
            }
        } else if (mode==2) {
            //Moving to COUNT UP mode.
            if (!breaksOnlyAreCountingUp) {
                breaksOnlyAreCountingUp = true;
                upDown_arrow_one.setTag(COUNtING_UP);
                upDown_arrow_one.setImageResource(R.drawable.arrow_up);
                dotDraws.countingUpBreaksOnly(true);
                dotDraws.breakOnlyTime(breaksOnlyTimeUP);
                dotDraws.breaksOnlyDraw(breaksOnlyTimeUP.size(), numberOfBreaksOnly, 3);
                timeLeft2.setText("0");
                timePaused2.setText("0");
                prefEdit.putBoolean("breakOnlyCountUpMode", true);
            } else {
                breaksOnlyAreCountingUp = false;
                upDown_arrow_one.setTag(COUNTING_DOWN);
                upDown_arrow_one.setImageResource(R.drawable.arrow_down);
                dotDraws.countingUpBreaksOnly(false);
                dotDraws.breakOnlyTime(breaksOnlyTime);
                dotDraws.breaksOnlyDraw(breaksOnlyTime.size(), numberOfBreaksOnly, 3);
                timePaused2.setText(convertSeconds((breakOnlyMillis+999)/1000));
                timeLeft2.setText(convertSeconds((breakOnlyMillis+999)/1000));
                prefEdit.putBoolean("breakOnlyCountUpMode", false);
            }
        }
        prefEdit.apply();
        //Sets fadeVar to 0 so our first dot doesn't fade.
        drawDots(0);
    }

    public void drawDots(int fadeVar) {
        switch (mode) {
            case 1:
                if (!setsAreCountingUp) dotDraws.setTime(customSetTime);
                else dotDraws.setTime(customSetTimeUP);
                if (!breaksAreCountingUp) dotDraws.breakTime(customBreakTime);
                else dotDraws.breakTime(customBreakTimeUP);
                dotDraws.customDrawSet(startSets, numberOfSets, fadeVar);
                dotDraws.customDrawBreak(startSets, numberOfBreaks);
                break;
            case 2:
                if (!breaksOnlyAreCountingUp) dotDraws.breakOnlyTime(breaksOnlyTime); else dotDraws.breakOnlyTime(breaksOnlyTimeUP);
                dotDraws.breaksOnlyDraw(startBreaksOnly, numberOfBreaksOnly, fadeVar);
                break;
            case 3:
                dotDraws.pomDraw(pomDotCounter, pomValuesTime, fadeVar);
                break;
        }
    }

    //receivedPos is taken from dotDraws using the sendPos callback, called from onTouchEvent when it uses setCycle. It returns 0-7 based on which round has been selected.
    public void deleteSelectedRound() {
        if (mode==1) {
            customSetTime.remove(receivedPos);
            customBreakTime.remove(receivedPos);
            customSetTimeUP.remove(receivedPos);
            customBreakTimeUP.remove(receivedPos);
            numberOfSets-=1;
            numberOfBreaks-=1;
            if (!setsAreCountingUp) dotDraws.setTime(customSetTime); else dotDraws.setTime(customSetTimeUP);
            if (!breaksAreCountingUp) dotDraws.breakTime(customBreakTime); else dotDraws.breakTime(customBreakTimeUP);
//            if (numberOfSets==0) {
//                delete_sb.setAlpha(0.3f);
//                delete_sb.setEnabled(false);
//            }
        } else if (mode==2){
            breaksOnlyTime.remove(receivedPos);
            breaksOnlyTimeUP.remove(receivedPos);
            numberOfBreaksOnly-=1;
            if (!breaksOnlyAreCountingUp) dotDraws.breakOnlyTime(breaksOnlyTime); else dotDraws.breakOnlyTime(breaksOnlyTimeUP);
//            if (numberOfBreaksOnly==0) {
//                delete_sb.setAlpha(0.3f);
//                delete_sb.setEnabled(false);
//            }
        }
        canSaveOrUpdate(true);
    }

    public void removeTimerViews() {
        timeLeft.setAlpha(0);
        timeLeft2.setAlpha(0);
        timeLeft3.setAlpha(0);
        timeLeft4.setAlpha(0);
        timePaused.setAlpha(0);
        timePaused2.setAlpha(0);
        timePaused3.setAlpha(0);
        timePaused4.setAlpha(0);
        msTime.setAlpha(0);
        msTimePaused.setAlpha(0);
        overtime.setVisibility(View.INVISIBLE);
    }

    public void removeEditViews() {
        if (mode==1 || mode==2) {
            first_value_edit.setVisibility(View.GONE);
            first_value_edit_two.setVisibility(View.GONE);
            first_value_sep.setVisibility(View.GONE);
            second_value_edit.setVisibility(View.GONE);
            second_value_edit_two.setVisibility(View.GONE);
            second_value_sep.setVisibility(View.GONE);
        } else if (mode==3) {
            first_value_single_edit.setVisibility(View.GONE);
            second_value_single_edit.setVisibility(View.GONE);
            third_value_single_edit.setVisibility(View.GONE);
        }
    }

    public void tabViews(){
        switch (mode) {
            case 1: case 2:
                first_value_textView.setText(convertCustomTextView(setValue));
                if (mode==1) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar2.setVisibility(View.INVISIBLE);
                    s1.setVisibility(View.VISIBLE);
                    overtime.setVisibility(View.GONE);
                    first_value_textView.setVisibility(View.VISIBLE);
                    plus_first_value.setVisibility(View.VISIBLE);
                    minus_first_value.setVisibility(View.VISIBLE);
                    top_anchor.setVisibility(View.VISIBLE);
                    second_value_textView.setText(convertCustomTextView(breakValue));
                } else if (mode==2) {
                    progressBar.setVisibility(View.INVISIBLE);
                    progressBar2.setVisibility(View.VISIBLE);
                    first_value_textView.setVisibility(View.INVISIBLE);
                    second_value_textView.setVisibility(View.VISIBLE);
                    plus_first_value.setVisibility(View.INVISIBLE);
                    minus_first_value.setVisibility(View.INVISIBLE);
                    s1.setVisibility(View.INVISIBLE);
                    top_anchor.setVisibility(View.GONE);
                    second_value_textView.setText(convertCustomTextView(breaksOnlyValue));
                    if (isOvertimeRunning) overtime.setVisibility(View.VISIBLE); else overtime.setVisibility(View.INVISIBLE);
                }
                progressBar3.setVisibility(View.INVISIBLE);
                stopWatchView.setVisibility(View.GONE);
                cycle_reset.setVisibility(View.VISIBLE);
                skip.setVisibility(View.VISIBLE);
                s1.setText(R.string.set_time);
                s2.setText(R.string.break_time);
                cycle_reset.setText(R.string.clear_cycles);
                s3.setVisibility(View.INVISIBLE);
                third_value_textView.setVisibility(View.INVISIBLE);
                third_value_single_edit.setVisibility(View.INVISIBLE);
                plus_third_value.setVisibility(View.INVISIBLE);
                minus_third_value.setVisibility(View.INVISIBLE);
                cycle_header_text.setVisibility(View.VISIBLE);
                add_cycle.setVisibility(View.VISIBLE);
                sub_cycle.setVisibility(View.VISIBLE);
                add_cycle_pom.setVisibility(View.GONE);
                sub_cycle_pom.setVisibility(View.GONE);
                break;
            case 3:
                first_value_single_edit.setText(convertSeconds(pomValue1));
                second_value_single_edit.setText(convertSeconds(pomValue2));
                third_value_single_edit.setText(convertSeconds(pomValue3));

                first_value_textView.setVisibility(View.VISIBLE);
                plus_first_value.setVisibility(View.VISIBLE);
                minus_first_value.setVisibility(View.VISIBLE);
                s1.setVisibility(View.VISIBLE);

                s3.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                progressBar3.setVisibility(View.VISIBLE);
                stopWatchView.setVisibility(View.GONE);
                cycle_reset.setVisibility(View.VISIBLE);
                skip.setVisibility(View.VISIBLE);
                plus_third_value.setVisibility(View.VISIBLE);
                minus_third_value.setVisibility(View.VISIBLE);
                overtime.setVisibility(View.GONE);
                cycle_header_text.setVisibility(View.VISIBLE);
                add_cycle.setVisibility(View.GONE);
                sub_cycle.setVisibility(View.GONE);
                add_cycle_pom.setVisibility(View.VISIBLE);
                sub_cycle_pom.setVisibility(View.VISIBLE);
                top_anchor.setVisibility(View.VISIBLE);

                third_value_textView.setVisibility(View.VISIBLE);
                first_value_textView.setText(String.valueOf(pomValue1));
                second_value_textView.setText(String.valueOf(pomValue2));
                third_value_textView.setText(String.valueOf(pomValue3));

                s1.setText(R.string.work_time);
                s2.setText(R.string.small_break);
                s3.setText(R.string.long_break);
                cycle_reset.setText(R.string.clear_cycles);
//
                break;
            case 4:
                progressBar.setVisibility(View.INVISIBLE);
                progressBar2.setVisibility(View.INVISIBLE);
                progressBar3.setVisibility(View.INVISIBLE);
                stopWatchView.setVisibility(View.VISIBLE);
                skip.setVisibility(View.GONE);
                cycle_reset.setVisibility(View.GONE);
                overtime.setVisibility(View.GONE);
                cycle_header_text.setVisibility(View.INVISIBLE);

                lapRecycler.setVisibility(View.VISIBLE);
                cycle_reset.setText(R.string.clear_laps);
                if (!stopwatchHalted) msTime.setAlpha(1); else msTimePaused.setAlpha(1);

                timeLeft4.setText(displayTime);
                timePaused4.setText(displayTime);
                msTime.setText(displayMs);
                msTimePaused.setText(displayMs);
        }
    }

    public void pauseAndResumeTimer(int pausing) {
        //Dismisses our add/subtract round menu and arrows once timer has begun.
        if (editCyclesPopupWindow.isShowing()) editCyclesPopupWindow.dismiss();
        //Toasts empty cycle message and exits out of method.
        if (emptyCycle) {
            Toast.makeText(getApplicationContext(), "What are we timing?", Toast.LENGTH_SHORT).show();
            return; }
        //Controls the alpha/enabled status of reset and FAB buttons.
        if (pausing==PAUSING_TIMER) resetAndFabToggle(true, false); else resetAndFabToggle(false, false);
        if (mode == 1) {
            if ((!onBreak && setsAreCountingUp) || (onBreak && breaksAreCountingUp)) {
                next_round.setVisibility(View.VISIBLE);
                fab.setVisibility(View.INVISIBLE);
            }
        }
        if (mode == 2) {
            if (breaksOnlyAreCountingUp) {
                next_round.setVisibility(View.VISIBLE);
                fab.setVisibility(View.INVISIBLE);
            }
        }
        //disabledTimer booleans are to prevent ANY action being taken.
        if ((!timerDisabled && mode == 1) || (!boTimerDisabled && mode == 2) || (!pomTimerDisabled && mode == 3) || mode==4) {
            add_cycle.setEnabled(false);
            sub_cycle.setEnabled(false);
            removeTimerViews();
            if (fadeInObj != null) fadeInObj.cancel();
            if (fadeOutObj != null) fadeOutObj.cancel();
            switch (mode) {
                case 1:
                    if (!modeOneTimerEnded) {
                        if (pausing == PAUSING_TIMER) {
                            customHalted = true;
                            timePaused.setAlpha(1);
                            if (timer != null) timer.cancel();
                            if (objectAnimator != null) objectAnimator.pause();
                            //If sets are counting down, do the following.
                            if (!setsAreCountingUp) {
                                if (!onBreak) {
                                    setMillisUntilFinished = setMillis;
                                    timePaused.setText(convertSeconds((setMillis + 999) / 1000));
                                    //If on Breaks Mode and sets are counting down, while breaks are also counting down, do the following.
                                } else if (!breaksAreCountingUp) {
                                    breakMillisUntilFinished = breakMillis;
                                    timePaused.setText(convertSeconds((breakMillis + 999) / 1000));
                                    //If in Breaks Mode and sets are counting down, but breaks are counting up, do the following.
                                } else mHandler.removeCallbacks(secondsUpBreakRunnable);
                            } else {
                                if (!onBreak) mHandler.removeCallbacks(secondsUpSetRunnable);
                                else if (!breaksAreCountingUp) {
                                    breakMillisUntilFinished = breakMillis;
                                    timePaused.setText(convertSeconds((breakMillis + 999) / 1000));
                                } else mHandler.removeCallbacks(secondsUpBreakRunnable);
                            }
                        } else if (pausing == RESUMING_TIMER) {
                            if (!circle_reset.isShown()) {
                                circle_reset.setVisibility(View.VISIBLE);
                                circle_reset.setAlpha(0.3f);
                            }
                             customHalted = false;
                            timeLeft.setAlpha(1);
                            if (!setsAreCountingUp) {
                                //If on Sets Mode and sets are counting down, do the following.
                                if (!onBreak) {
                                    startObjectAnimator();
                                    startSetTimer();
                                    //If on Breaks Mode and sets are counting down, while breaks are also counting down, do the following.
                                } else if (!breaksAreCountingUp) {
                                    startObjectAnimator();
                                    startBreakTimer();
                                } else mHandler.post(secondsUpBreakRunnable);
                            } else {
                                if (!onBreak) {
                                    mHandler.post(secondsUpSetRunnable);
                                    next_round.setAlpha(1.0f);
                                    next_round.setEnabled(true);
                                } else {
                                    if (!breaksAreCountingUp) {
                                        startObjectAnimator();
                                        startBreakTimer();
                                    } else {
                                        mHandler.post(secondsUpBreakRunnable);
                                        next_round.setAlpha(1.0f);
                                        next_round.setEnabled(true);
                                    }
                                }
                            }
                        }
                    } else resetTimer();
                    break;
                case 2:
                    if (!modeTwoTimerEnded) {
                        if (pausing == PAUSING_TIMER) {
                            breaksOnlyHalted = true;
                            timePaused2.setAlpha(1);
                            if (timer2 != null) timer2.cancel();
                            if (objectAnimator2 != null) objectAnimator2.pause();
                            if (!breaksOnlyAreCountingUp) {
                                breakOnlyMillisUntilFinished = breakOnlyMillis;
                                timePaused2.setText(convertSeconds((breakOnlyMillis + 999) / 1000));
                            } else mHandler.removeCallbacks(secondsUpBORunnable);
                        } else if (pausing == RESUMING_TIMER) {
                            if (!circle_reset.isShown()) {
                                circle_reset.setVisibility(View.VISIBLE);
                                circle_reset.setAlpha(0.3f);
                            }
                            timeLeft2.setAlpha(1);
                            breaksOnlyHalted = false;
                            if (!breaksOnlyAreCountingUp) {
                                startObjectAnimator();
                                startBreakTimer();
                            } else {
                                mHandler.post(secondsUpBORunnable);
                                next_round.setAlpha(1.0f);
                                next_round.setEnabled(true);
                            }
                        } else if (pausing == RESETTING_TIMER) {
                            if (endAnimation2 != null) endAnimation2.cancel();
                            isOvertimeRunning = false;
                            mHandler.removeCallbacks(ot);
                            overSeconds = 0;
                            overtime.setVisibility(View.INVISIBLE);
                            progressBar2.setProgress(10000);
                            timePaused2.setAlpha(1.0f);
                            timeLeft2.setText(convertSeconds((breakOnlyMillis + 999) / 1000));
                            timePaused2.setText(convertSeconds((breakOnlyMillis + 999) / 1000));
                        } else if (pausing == RESTARTING_TIMER) {
                            startObjectAnimator();
                            startBreakTimer();
                            boTimerDisabled = false;
                            timePaused2.setAlpha(0f);
                            timeLeft2.setAlpha(1.0f);
                        }
                    } else resetTimer();
                    break;
                case 3:
                    if (reset.getText().equals(getString(R.string.confirm_cycle_reset))) reset.setText(R.string.reset);
                    if (!modeThreeTimerEnded) {
                        if (pausing == PAUSING_TIMER) {
                            timePaused3.setAlpha(1);
                            pomHalted = true;
                            pomMillisUntilFinished = pomMillis;
                            if (objectAnimator3 != null) objectAnimator3.pause();
                            if (timer3 != null) timer3.cancel();
                            ;
                            String pausedTime2 = (convertSeconds((pomMillisUntilFinished + 999) / 1000));
                            timePaused3.setText(pausedTime2);
                        } else if (pausing == RESUMING_TIMER) {
                            timeLeft3.setAlpha(1);
                            pomHalted = false;
                            startObjectAnimator();
                            startPomTimer();
                        }
                    } else resetTimer();
                    break;
                case 4:
                    DecimalFormat df2 = new DecimalFormat("00");
                    if (fadeInObj != null) fadeInObj.cancel();
                    if (pausing == RESUMING_TIMER) {
                        timeLeft4.setAlpha(1);
                        timePaused4.setAlpha(0);
                        msTime.setAlpha(1);
                        msTimePaused.setAlpha(0);
                        stopWatchRunnable = new Runnable() {
                            @Override
                            public void run() {
                                //ms can never be more than 60/sec due to refresh rate.
                                ms += 1;
                                msReset += 1;
                                msConvert += 1;
                                msConvert2 += 1;
                                msDisplay += 1;
                                if (msConvert > 59) msConvert = 0;
                                if (msConvert2 > 59) msConvert2 = 0;
                                if (msDisplay > 59) msDisplay = 0;

                                seconds = ms / 60;
                                minutes = seconds / 60;

                                newMs = df2.format((msConvert2 / 60) * 100);
                                savedMs = df2.format((msConvert / 60) * 100);
                                newMsConvert = Integer.parseInt(newMs);
                                savedMsConvert = Integer.parseInt(savedMs);

                                //Conversion to long solves +30 ms delay for display.
                                displayMs = df2.format((msDisplay / 60) * 100);
                                displayTime = convertStopwatch((long) seconds);

                                timeLeft4.setText(displayTime);
                                msTime.setText(displayMs);
                                mHandler.postDelayed(this, 10);
                            }
                        };
                        mHandler.post(stopWatchRunnable);
                        stopwatchHalted = false;
                        new_lap.setAlpha(1.0f);
                        new_lap.setEnabled(true);
                    } else if (pausing == PAUSING_TIMER) {
                        timeLeft4.setAlpha(0);
                        timePaused4.setAlpha(1);
                        msTime.setAlpha(0);
                        msTimePaused.setAlpha(1);
                        timePaused4.setText(timeLeft4.getText());
                        msTimePaused.setText(msTime.getText());
                        mHandler.removeCallbacksAndMessages(null);
                        stopwatchHalted = true;
                        new_lap.setAlpha(0.3f);
                        new_lap.setEnabled(false);
                    }
            }
        }
    }

    public void populateCycleUI() {
        switch (mode) {
            case 1:
                if (customSetTime.size()>0) {
                    setMillis = customSetTime.get(0);
                    breakMillis = customBreakTime.get(0);
                }
                if (!setsAreCountingUp) {
                    if (customSetTime.size()>0) {
                        timePaused.setText(convertSeconds((setMillis+999)/1000));
                        timeLeft.setText(convertSeconds((setMillis+999)/1000));
                    }
                    if (setMillis>=60000) {
                        timePaused.setTextSize(70f);
                        timeLeft.setTextSize(70f);
                    } else {
                        timePaused.setTextSize(90f);
                        timeLeft.setTextSize(90f);
                    }
                } else {
                    timeLeft.setText("0");
                    timePaused.setText("0");
                }

                //startSets is our starting number of sets AND breaks, since they will always be the same.
                //numberOfSets/numberOfBreaks are the REMAINING ones, will not always be the same.
                startSets = customSetTime.size();
                numberOfSets = customSetTime.size();
                numberOfBreaks = customBreakTime.size();
                //Used to ensure our list size for breaksOnly is correctly passed in to drawDots on app launch.
                startBreaksOnly = breaksOnlyTime.size();
                break;
            case 2:
                if (breaksOnlyTime.size()>0) breakOnlyMillis = breaksOnlyTime.get(0);
                if (!breaksOnlyAreCountingUp) {
                    if (breaksOnlyTime.size()>0) {
                        timePaused2.setText(convertSeconds((breakOnlyMillis+999)/1000));
                        timeLeft2.setText(convertSeconds((breakOnlyMillis+999)/1000));
                    }
                    if (breakOnlyMillis>=60000) {
                        timePaused2.setTextSize(70f);
                        timeLeft2.setTextSize(70f);
                    }
                } else {
                    timeLeft2.setText("0");
                    timePaused2.setText("0");
                }
                startBreaksOnly = breaksOnlyTime.size();
                numberOfBreaksOnly = breaksOnlyTime.size();
                break;
            case 3:
                //Here is where we set the initial millis Value of first pomMillis. Set again on change on our value runnables.
                if (pomValuesTime.size()>0) {
                    pomMillis1 = pomValuesTime.get(0)*1000*60;
                    pomMillis = pomMillis1;
                    timePaused3.setText(convertSeconds((pomMillis+999)/1000));
                    pomTimerDisabled = false;
                }
                break;
        }
        next_round.setVisibility(View.INVISIBLE);
        fab.setVisibility(View.VISIBLE);
        dotDraws.setAlpha();
        drawDots(0);
    }

    public void resetTimer() {
        removeTimerViews();
        switch (mode) {
            case 1:
                progressBar.setProgress(10000);
                if (timer != null) timer.cancel();
                if (objectAnimator != null) objectAnimator.cancel();
                customProgressPause = maxProgress;
                modeOneTimerEnded = false;
                setBegun = false;
                breakBegun = false;
                customHalted = true;
                onBreak = false;
                timePaused.setAlpha(1);
                resetAndFabToggle(false, true);
                //Sets all longs in list to "0" since we are restarting a Count Up cycle.
                for (int i=0; i<customSetTimeUP.size(); i++) customSetTimeUP.set(i, (long) 0);
                for (int i=0; i<customBreakTimeUP.size(); i++) customBreakTimeUP.set(i, (long) 0);
                //Resetting millis values of count up mode to 0.
                countUpMillisSets = 0;
                countUpMillisBreaks = 0;
                //Removing timer callbacks for count up mode.
                mHandler.removeCallbacks(secondsUpSetRunnable);
                mHandler.removeCallbacks(secondsUpBreakRunnable);
                break;
            case 2:
                timePaused2.setAlpha(1);
                progressBar2.setProgress(10000);
                if (timer2 != null) timer2.cancel();
                if (objectAnimator2 != null) objectAnimator2.cancel();
                breaksOnlyProgressPause = maxProgress;
                modeTwoTimerEnded = false;
                breakOnlyBegun = false;
                breaksOnlyHalted = true;
                resetAndFabToggle(false, true);
                mHandler.removeCallbacks(secondsUpBORunnable);
                for (int i=0; i<breaksOnlyTimeUP.size(); i++) breaksOnlyTimeUP.set(i, (long) 0);
                break;
            case 3:
                timePaused3.setAlpha(1);
                pomDotCounter = 1;
                progressBar3.setProgress(10000);
                modeThreeTimerEnded = false;
                pomBegun = false;
                pomProgressPause = maxProgress;
                pomHalted = true;
                activePomCycle = false;
                if (timer3 != null) timer3.cancel();
                if (objectAnimator3 != null) objectAnimator3.cancel();
                resetAndFabToggle(false, true);
                break;
            case 4:
                stopwatchHalted = true;
                ms = 0;
                msConvert = 0;
                msConvert2 = 0;
                msDisplay = 0;
                msReset = 0;
                seconds = 0;
                minutes = 0;
                lapsNumber = 0;
                timeLeft4.setAlpha(1);
                msTime.setAlpha(1);
                timeLeft4.setText("0");
                msTime.setText("00");
                cycles_completed.setText(getString(R.string.laps_completed, String.valueOf(0)));
                if (currentLapList.size()>0) currentLapList.clear();
                if (savedLapList.size()>0) savedLapList.clear();
                lapAdapter.notifyDataSetChanged();
                resetAndFabToggle(false, false);
                break;
        }
        add_cycle.setEnabled(true);
        sub_cycle.setEnabled(true);
        populateCycleUI();

        endAnimation.cancel();
        endAnimation2.cancel();
        endAnimation3.cancel();
    }
}