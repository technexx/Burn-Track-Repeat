package com.example.tragic.irate.simple.stopwatch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tragic.irate.simple.stopwatch.Database.CyclesDatabase;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class MainActivity extends AppCompatActivity implements SavedCycleAdapter.onCycleClickListener, SavedCycleAdapter.onDeleteCycleListener, DotDraws.sendPosition, DotDraws.sendAlpha {

    boolean defaultMenu = true;
    View mainView;
    TextView save_cycles;
    TextView update_cycles;
    ProgressBar progressBar;
    ProgressBar progressBar2;
    ImageView stopWatchView;
    TextView timeLeft;
    TextView timeLeft2;
    TextView timePaused;
    TextView timePaused2;
    TextView timeLeft3;
    TextView timePaused3;
    TextView msTime;
    TextView msTimePaused;
    CountDownTimer timer;
    CountDownTimer timer2;
    TextView reset;
    ObjectAnimator objectAnimator;
    ObjectAnimator objectAnimator2;
    Animation endAnimation;
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
    ImageButton left_arrow;
    ImageButton right_arrow;
    EditText edit_header;
    Button confirm_header_save;
    Button cancel_header_save;
    ImageButton delete_sb;
    TextView overtime;

    ImageView sortCheckmark;
    Button skip;
    TextView newLap;

    int PAUSING_TIMER = 1;
    int RESUMING_TIMER = 2;
    int RESETTING_TIMER = 3;
    int RESTARTING_TIMER = 4;
    int SAVING_CYCLES = 1;
    int UPDATING_CYCLES = 2;

    long breakMillis;
    int breakStart;
    long setMillis;
    int setStart;
    int maxProgress = 10000;
    int customProgressPause = 10000;
    int pomProgressPause = 10000;
    long pomMillis;
    long setMillisUntilFinished;
    long breakMillisUntilFinished;
    long pomMillisUntilFinished;
    boolean incrementValues;
    int incrementTimer;
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
    long savedBreaks;
    long savedSets;

    boolean customTimerEnded;
    boolean pomTimerEnded;
    boolean paused;
    boolean onBreak;
    boolean pomEnded;
    boolean timerDisabled;
    boolean pomTimerDisabled;
    boolean customHalted = true;
    boolean pomHalted = true;
    boolean stopwatchHalted = true;

    DotDraws dotDraws;
    int fadeDone;
    int mode=1;
    ValueAnimator sizeAnimator;
    ValueAnimator valueAnimatorDown;
    ValueAnimator valueAnimatorUp;

    ArrayList<String> customTitleArray;
    ArrayList<String> breaksOnlyTitleArray;
    ArrayList<String> pomCyclesTitleArray;
    ArrayList<Long> customSetTime;
    ArrayList<Long> customBreakTime;
    ArrayList<Long> startCustomSetTime;
    ArrayList<Long> startCustomBreakTime;
    ArrayList<Long> startBreaksOnlyTime;
    ArrayList<Long> breaksOnlyTime;
    ArrayList<Long> pomValuesTime;
    String convertedSetList;
    String convertedBreakList;
    String convertedBreakOnlyList;
    String convertedPomList;

    boolean activeCustomCycle;
    boolean activeCustomBOCycle;
    boolean activePomCycle;
    boolean setBegun;
    boolean breakBegun;
    boolean pomBegun;
    boolean breaksOnly;
    boolean breakEnded;
    boolean newBreak;

    PopupWindow sortPopupWindow;
    PopupWindow savedCyclePopupWindow;
    PopupWindow labelSavePopupWindow;
    PopupWindow deleteAllPopupWindow;
    PopupWindow editCyclesPopupWindow;

    boolean fadeCustomTimer;
    boolean fadePomTimer;
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
    View editCyclePopupView;

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
    boolean stopAscent = true;
    boolean minReached;
    boolean maxReached;

    ImageButton fab;

    //Todo: Sep breakOnly timer.
    //Todo: "overtime" seconds are active for set/break, should be for breakOnly.
    //Todo: "cycle completed" as a db entry for each separate cycle.
    //Todo: Add skip to Pom.
    //Todo: Modify boxes for increased dot size.
    //Todo: "Reset" -> "Confirm Reset" does not go back to "Reset" if resuming timer. For reset cycles AND reset timer.

    //Todo: Variable set count-up timer, for use w/ TDEE. Possibly replace empty space in breaksOnly mode.
    //Todo: Variable set only mode? Again, for TDEE.
    //Todo: Option to skip EITHER set or break. Option to undo skip.

    //Todo: Fade animation for all menus that don't have them yet (e.g. onOptions).
    //Todo: Rippling for certain onClicks.
    //Todo: Inconsistencies w/ fading.
    //Todo: Add taskbar notification for timers.
    //Todo: Add color scheme options.
    //Todo: Test all Pom cycles.
    //Todo: All DB calls in aSync.
    //Todo: Rename app, of course.
    //Todo: Add onOptionsSelected dots for About, etc.
    //Todo: Repository for db. Look at Executor/other alternate thread methods. Would be MUCH more streamlined on all db calls, but might also bork order of operations when we need to call other stuff under UI thread right after.
    //Todo: Make sure number pad is dismissed when switching to stopwatch mode.
    //Todo: Make sure canvas'd over clickables in stopwatch mode can't be triggered.
    //Todo: IMPORTANT: Resolve landscape mode vs. portrait. Set to portrait-only in manifest at present. Likely need a second layout for landscape mode.

    //Todo: REMEMBER, All notifyDataSetChanged() has to be called on main UI thread, since that is the one where we created the views it is refreshing.
    //Todo: REMEMBER, always call queryCycles() to get a cyclesList reference, otherwise it won't sync w/ the current sort mode.
    //Todo: REMEMBER, any reference to our GLOBAL instance of a cycles position will retain that position unless changed.
    //Todo: REMINDER, Try next app w/ Kotlin.

    @Override
    public void sendAlphaValue(int alpha) {
        receivedAlpha = alpha;;
    }

    @Override
    public void sendPos(int pos) {
        if (mode==1) {
            receivedPos = pos;
            if (pos <=0) {
                left_arrow.setVisibility(View.INVISIBLE);
                right_arrow.setVisibility(View.INVISIBLE);
                delete_sb.setVisibility(View.INVISIBLE);
            } else {
                left_arrow.setVisibility(View.VISIBLE);
                right_arrow.setVisibility(View.VISIBLE);
                delete_sb.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        motionEvent = event;
        int x = (int) event.getX();
        int y = (int) event.getY();

        //Selects a set/break combo to move.
        if (mode==1) {
            if (event.getAction()==MotionEvent.ACTION_DOWN) {
                dotDraws.selectCycle(x, y, startCustomSetTime.size());
            }
        }

        //This dismisses windows/views when main layout (view) is clicked. We use it instead of using a clickable instance of our layout since that bugs.
        if (savedCyclePopupWindow!=null && savedCyclePopupWindow.isShowing()) {
            savedCyclePopupWindow.dismiss();
            invalidateOptionsMenu();
            defaultMenu = true;
        }
        if (deleteAllPopupWindow!=null & deleteAllPopupWindow.isShowing()) deleteAllPopupWindow.dismiss();
        switch (mode) {
            case 1:
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
                break;
            case 2:
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
                    setEditValueBounds(true);
                    break;
        }
        save_cycles.setText(R.string.save_cycles);
        return false;
    }

    @Override
    public void onCycleClick(int position) {
        setCycle(position, true);
    }

    @Override
    public void onCycleDelete(int position) {
        AsyncTask.execute(()->{
            //Initial query, applying to all retrievals.
            queryCycles();
            switch (mode) {
                case 1:
                    if (!breaksOnly) {
                        int deletedID = cyclesList.get(position).getId();

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
                    } else {
                        int deletedID = cyclesBOList.get(position).getId();

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
                    }
                    break;
                case 2:
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

    //Todo: Consolidate >0 condition.
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
                            if (!breaksOnly) {
                                savedCycleAdapter.setView(1);
                                if (cyclesList.size()>0) {
                                    for (int i=0; i<cyclesList.size(); i++) {
                                        setsArray.add(cyclesList.get(i).getSets());
                                        breaksArray.add(cyclesList.get(i).getBreaks());
                                        customTitleArray.add(cyclesList.get(i).getTitle());
                                    }
                                    cyclesExist.set(true);
                                }
                            } else {
                                savedCycleAdapter.setView(2);
                                if (cyclesBOList.size()>0) {
                                    for (int i=0; i<cyclesBOList.size(); i++) {
                                        breaksOnlyTitleArray.add(cyclesBOList.get(i).getTitle());
                                        breaksOnlyArray.add(cyclesBOList.get(i).getBreaksOnly());
                                    }
                                    cyclesExist.set(true);
                                }
                            }
                            break;
                        case 2:
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
                    if ((!breaksOnly && cyclesList.size()==0)|| (breaksOnly && cyclesBOList.size()==0)){
                        runOnUiThread(()-> {
                            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Nothing saved!", Toast.LENGTH_SHORT).show());
                        });
                    } else {
                        runOnUiThread(()-> {
                            if (!breaksOnly) delete_all_text.setText(R.string.delete_all_custom); else delete_all_text.setText(R.string.delete_all_BO);
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
//        ViewGroup blah = findViewById(R.id.main_layout);

        valueAnimatorDown = new ValueAnimator().ofFloat(90f, 70f);
        valueAnimatorUp = new ValueAnimator().ofFloat(70f, 90f);
        valueAnimatorDown.setDuration(1000);
        valueAnimatorUp.setDuration(1000);

        customSetTime = new ArrayList<>();
        customBreakTime = new ArrayList<>();
        startCustomSetTime = new ArrayList<>();
        startCustomBreakTime = new ArrayList<>();
        breaksOnlyTime = new ArrayList<>();
        startBreaksOnlyTime = new ArrayList<>();
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
        editCyclePopupView = inflater.inflate(R.layout.edit_cycle_popup, null);

        savedCyclePopupWindow = new PopupWindow(savedCyclePopupView, 800, 1200, false);
        deleteAllPopupWindow = new PopupWindow(deleteCyclePopupView, 750, 375, false);
        labelSavePopupWindow = new PopupWindow(cycleLabelView, 800, 400, true);
        sortPopupWindow = new PopupWindow(sortCyclePopupView, 400, 375, true);
        editCyclesPopupWindow = new PopupWindow(editCyclePopupView, WindowManager.LayoutParams.MATCH_PARENT, 415, true);
        savedCyclePopupWindow.setAnimationStyle(R.style.WindowAnimation);
        deleteAllPopupWindow.setAnimationStyle(R.style.WindowAnimation);
        labelSavePopupWindow.setAnimationStyle(R.style.WindowAnimation);
        sortPopupWindow.setAnimationStyle(R.style.WindowAnimation);
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

        s1 = editCyclePopupView.findViewById(R.id.s1);
        s2 = editCyclePopupView.findViewById(R.id.s2);
        s3 = editCyclePopupView.findViewById(R.id.s3);
        first_value_edit = editCyclePopupView.findViewById(R.id.first_value_edit);
        first_value_edit_two = editCyclePopupView.findViewById(R.id.first_value_edit_two);
        first_value_sep = editCyclePopupView.findViewById(R.id.first_value_sep);
        first_value_textView = editCyclePopupView.findViewById(R.id.first_value_textView);
        second_value_edit = editCyclePopupView.findViewById(R.id.second_value_edit);
        second_value_edit_two = editCyclePopupView.findViewById(R.id.second_value_edit_two);
        second_value_sep = editCyclePopupView.findViewById(R.id.second_value_sep);
        second_value_textView = editCyclePopupView.findViewById(R.id.second_value_textView);
        third_value_textView = editCyclePopupView.findViewById(R.id.third_value_textView);
        plus_first_value = editCyclePopupView.findViewById(R.id.plus_first_value);
        minus_first_value = editCyclePopupView.findViewById(R.id.minus_first_value);
        plus_second_value = editCyclePopupView.findViewById(R.id.plus_second_value);
        minus_second_value = editCyclePopupView.findViewById(R.id.minus_second_value);
        plus_third_value = editCyclePopupView.findViewById(R.id.plus_third_value);
        minus_third_value = editCyclePopupView.findViewById(R.id.minus_third_value);
        first_value_single_edit = editCyclePopupView.findViewById(R.id.first_value_single_edit);
        second_value_single_edit = editCyclePopupView.findViewById(R.id.second_value_single_edit);
        third_value_single_edit = editCyclePopupView.findViewById(R.id.third_value_single_edit);
        add_cycle = editCyclePopupView.findViewById(R.id.add_cycle);
        sub_cycle = editCyclePopupView.findViewById(R.id.subtract_cycle);

        reset = findViewById(R.id.reset);
        cycle_header_text = findViewById(R.id.cycle_header_text);

        delete_all_text = deleteCyclePopupView.findViewById(R.id.delete_confirm_text);
        delete_all_confirm = deleteCyclePopupView.findViewById(R.id.confirm_yes);
        delete_all_cancel = deleteCyclePopupView.findViewById(R.id.confirm_no);


        cycles_completed = findViewById(R.id.cycles_completed);
        cycle_reset = findViewById(R.id.cycle_reset);
        skip = findViewById(R.id.skip);
        newLap = findViewById(R.id.new_lap);
        left_arrow = findViewById(R.id.left_arrow);
        right_arrow = findViewById(R.id.right_arrow);
        delete_sb = findViewById(R.id.delete_set_break);
        fab = findViewById(R.id.fab);

        save_cycles = findViewById(R.id.save_cycles);
        update_cycles = findViewById(R.id.update_cycles);
        progressBar = findViewById(R.id.progressBar);
        progressBar2 = findViewById(R.id.progressBar2);
        stopWatchView = findViewById(R.id.stopWatchView);
        timeLeft = findViewById(R.id.timeLeft);
        timeLeft2 = findViewById(R.id.timeLeft2);
        timeLeft3 =findViewById(R.id.timeLeft3);
        timePaused = findViewById(R.id.timePaused);
        timePaused2 = findViewById(R.id.timePaused2);
        timePaused3 = findViewById(R.id.timePaused3);
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
        timeLeft2.setTextSize(70f);
        timePaused2.setTextSize(70f);
        timeLeft3.setTextSize(90f);
        timePaused3.setTextSize(90f);
        skip.setText(R.string.skip_round);
        newLap.setText(R.string.lap);
        cycle_reset.setText(R.string.clear_cycles);
        cycles_completed.setText(R.string.cycles_done);
        cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));

        mHandler = new Handler();

        delete_sb.setVisibility(View.INVISIBLE);
        progressBar2.setVisibility(View.GONE);
        stopWatchView.setVisibility(View.GONE);
        newLap.setVisibility(View.GONE);
        lapRecycler.setVisibility(View.GONE);
        overtime.setVisibility(View.INVISIBLE);

        removeViews();
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
//            mode = sharedPreferences.getInt("currentMode", 1);
        setValue = sharedPreferences.getLong("setValue", 30);
        breakValue = sharedPreferences.getLong("breakValue", 30);
        breaksOnlyValue = sharedPreferences.getLong("breaksOnlyValue", 30);
        pomValue1 = sharedPreferences.getLong("pomValue1", 25);
        pomValue2 = sharedPreferences.getLong("pomValue2", 5);
        pomValue3 = sharedPreferences.getLong("pomValue3", 15);
        //Call this on start to sync edit values w/ retrieved values from SharedPref.
        convertEditTime();

        sortMode = sharedPreferences.getInt("sortMode", 1);
        sortModeBO = sharedPreferences.getInt("sortModeBO", 1);
        sortModePom = sharedPreferences.getInt("sortModePom", 1);
        customID = sharedPreferences.getInt("customID", 0);
        breaksOnlyID = sharedPreferences.getInt("breaksOnlyID", 0);
        pomID = sharedPreferences.getInt("pomID", 0);

        //Todo: custom/break IDs may be obsolete now.
        startCustomSetTime.clear();
        startCustomBreakTime.clear();
        startBreaksOnlyTime.clear();

        String retrievedSetArray = sharedPreferences.getString("setArrays", "");
        String retrievedBreakArray = sharedPreferences.getString("breakArrays", "");
        String retrievedBOArray = sharedPreferences.getString("savedBOArrays", "");
        String retrievedTitle = sharedPreferences.getString("savedTitle", "");

        retrievedSetArray = retrievedSetArray.replace("[", "");
        retrievedSetArray = retrievedSetArray.replace("]", "");
        retrievedBreakArray = retrievedBreakArray.replace("[", "");
        retrievedBreakArray = retrievedBreakArray.replace("]", "");
        retrievedBOArray = retrievedBOArray.replace("[", "");
        retrievedBOArray = retrievedBOArray.replace("]", "");

        String[] convSets = retrievedSetArray.split(",");
        String[] convBreaks = retrievedBreakArray.split(",");
        String[] convBO = retrievedBOArray.split(",");

        if (!retrievedSetArray.equals("")) {
            for (int i=0; i<convSets.length; i++) {
                startCustomSetTime.add(Long.parseLong(convSets[i]));
                startCustomBreakTime.add(Long.parseLong(convBreaks[i]));
            }
        } else setDefaultCustomCycle(false);
        if (!retrievedBOArray.equals("")){
            for (int i=0; i<convBO.length; i++) startBreaksOnlyTime.add(Long.parseLong(convBO[i]));
        }
        else setDefaultCustomCycle(true);

        cycle_header_text.setText(retrievedTitle);
        populateCycleUI();

        savedSets = startCustomSetTime.size();
        savedBreaks = startCustomBreakTime.size();
        numberOfSets = savedSets;
        numberOfBreaks = savedBreaks;

        pomValue1 = 25;
        pomValue2 = 5;
        pomValue3 = 15;
        for (int i=0; i<3; i++) {
            pomValuesTime.add(pomValue1);
            pomValuesTime.add(pomValue2);
        }
        pomValuesTime.add(pomValue1);
        pomValuesTime.add(pomValue3);
        pomMillis = pomValue1*1000*60;
        pomMillis1 = pomValue1*1000*60;

        lapLayout= new LinearLayoutManager(getApplicationContext());
        lapAdapter = new LapAdapter(getApplicationContext(), currentLapList, savedLapList);
        lapRecycler.setAdapter(lapAdapter);
        lapRecycler.setLayoutManager(lapLayout);

        progressBar.setProgress(maxProgress);
        progressBar2.setProgress(maxProgress);

        incrementTimer = 10;
        tabViews();

        timePaused.getAlpha();
        timeLeft.getAlpha();

        if ((!breaksOnly && cyclesList.size()>0)  || (breaksOnly && cyclesBOList.size()>0)){
            canSaveOrUpdate(false);
        } else canSaveOrUpdate(true);

        AsyncTask.execute(() -> {
            cyclesDatabase = CyclesDatabase.getDatabase(getApplicationContext());
        });

        //Todo: Will need new halted vars etc. for breaksOnly mode.
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        mode=1;
                        setBreaksOnlyMode(false);
                        savedCycleAdapter.setView(1);
                        switchTimer(1, customHalted);
                        dotDraws.setMode(1);
                        if (!setBegun) drawDots(0);
                        break;
                    case 1:
                        mode=1;
                        setBreaksOnlyMode(true);
                        savedCycleAdapter.setView(2);
                        switchTimer(1, customHalted);
                        dotDraws.setMode(1);
                        if (!breakBegun) drawDots(0);
                        break;
                    case 2:
                        mode=2;
                        savedCycleAdapter.setView(3);
                        switchTimer(2, pomHalted);
                        dotDraws.setMode(2);
                        dotDraws.pomDraw(1, 0, pomValuesTime);
                        break;
                    case 3:
                        mode=3;
                        switchTimer(3, stopwatchHalted);
                        lapRecycler.setVisibility(View.VISIBLE);
                        break;
                }
                prefEdit.apply();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        left_arrow.setVisibility(View.INVISIBLE);
                        right_arrow.setVisibility(View.INVISIBLE);
                        if (customHalted) {
                            fadeOutText(timePaused); lastTextView = timePaused;
                        } else {
                            fadeOutText(timeLeft); lastTextView = timeLeft;
                        }
                        break;
                    case 1:
                        if (pomHalted) {
                            fadeOutText(timePaused2); lastTextView = timePaused2;
                        } else {
                            fadeOutText(timeLeft2); lastTextView = timeLeft2;
                        }
                        break;
                    case 2:
                        if (stopwatchHalted) {
                            fadeOutText(timePaused3);
//                            fadeOutText(msTimePaused);
                        } else {
                            fadeOutText(timeLeft3);
//                            fadeOutText(msTime);
                        }
                        lapRecycler.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        fab.setOnClickListener(v-> {
            if (mode==1) {
                if (!breaksOnly) editCyclesPopupWindow.setHeight(380); else editCyclesPopupWindow.setHeight(275);
            } else editCyclesPopupWindow.setHeight(455);
            editCyclesPopupWindow.showAsDropDown((View) tabLayout);
        });

        delete_all_confirm.setOnClickListener(v-> {
            AsyncTask.execute(() -> {
                switch (mode) {
                    case 1:
                        if (!breaksOnly) {
                            cyclesDatabase.cyclesDao().deleteAll();
                            prefEdit.putInt("customID", 0);
                        } else {
                            cyclesDatabase.cyclesDao().deleteAllBO();
                            prefEdit.putInt("breaksOnlyID", 0);
                        }
                        break;
                    case 2:
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

        first_value_textView.setOnClickListener(v-> {
            switch (mode) {
                case 1:
                    if (!breaksOnly) {
                        if (first_value_textView.isShown()) {
                            first_value_textView.setVisibility(View.GONE);
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
                    }
                    break;
                case 2:
                    if (first_value_textView.isShown()) {
                        first_value_textView.setVisibility(View.GONE);
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
            }
        });

        second_value_textView.setOnClickListener(v-> {
            switch (mode) {
                case 1:
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
                case 2:
                    if (first_value_single_edit.isShown()) {
                        first_value_textView.setVisibility(View.VISIBLE);
                        first_value_single_edit.setVisibility(View.GONE);
                    }
                    if (second_value_textView.isShown()) {
                        second_value_single_edit.setVisibility(View.VISIBLE);
                        second_value_textView.setVisibility(View.GONE);
                    }
                    if (third_value_single_edit.isShown()){
                        third_value_single_edit.setVisibility(View.GONE);
                        third_value_textView.setVisibility(View.VISIBLE);
                    }
                    break;
            }

        });

        third_value_textView.setOnClickListener(v->{
            switch (mode) {
                case 1:
                    if (first_value_edit.isShown() || first_value_edit_two.isShown()) {
                        first_value_edit.setVisibility(View.GONE);
                        first_value_edit_two.setVisibility(View.GONE);
                        first_value_sep.setVisibility(View.GONE);
                    }
                    if (second_value_edit.isShown() || second_value_edit_two.isShown()) {
                        second_value_edit.setVisibility(View.GONE);
                        second_value_edit_two.setVisibility(View.GONE);
                        second_value_sep.setVisibility(View.GONE);
                    }
                     break;
                case 2:
                    if (first_value_single_edit.isShown()) {
                        first_value_single_edit.setVisibility(View.GONE);
                        first_value_textView.setVisibility(View.VISIBLE);
                    }
                    if (second_value_edit.isShown()){
                        second_value_single_edit.setVisibility(View.GONE);
                        second_value_textView.setVisibility(View.VISIBLE);
                    }
                    if (third_value_textView.isShown()) {
                        third_value_textView.setVisibility(View.GONE);
                        third_value_single_edit.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        });

        changeFirstValue = new Runnable() {
            @Override
            public void run() {
                switch (mode) {
                    case 1:
                        if (incrementValues) setValue+=1; else setValue -=1;
                        break;
                    case 2:
                        if (incrementValues) pomValue1+=1; else pomValue1 -=1;
                        pomMillis1 = (pomValue1*1000) * 60;
                        break;
                }
                prefEdit.apply();
                mHandler.postDelayed(this, incrementTimer*10);
                setTimerValueBounds();
                fadeCap(first_value_textView);
            }
        };

        changeSecondValue = new Runnable() {
            @Override
            public void run() {
                switch (mode) {
                    case 1:
                        if (!breaksOnly) {
                            if (incrementValues) breakValue+=1; else breakValue -=1;
                        } else{
                            if (incrementValues) breaksOnlyValue+=1; else breaksOnlyValue -=1;
                        }
                        break;
                    case 2:
                        if (incrementValues) pomValue2+=1; else pomValue2 -=1;
                        pomMillis2 = (pomValue2*1000) * 60;
                        break;
                }
                prefEdit.apply();
                mHandler.postDelayed(this, incrementTimer*10);
                setTimerValueBounds();
                fadeCap(second_value_textView);
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
                    if (!breaksOnly) first_value_textView.setText(convertCustomTextView(setValue));
                    break;
                case 2:
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
                    if (!breaksOnly) first_value_textView.setText(convertCustomTextView(setValue));
                    break;
                case 2:
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
                    if (!breaksOnly) second_value_textView.setText(convertCustomTextView(breakValue)); else second_value_textView.setText(convertCustomTextView(breaksOnlyValue));
                    break;
                case 2:
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
                    if (!breaksOnly) second_value_textView.setText(convertCustomTextView(breakValue)); else second_value_textView.setText(convertCustomTextView(breaksOnlyValue));
                    break;
                case 2:
                    second_value_single_edit.setText(String.valueOf(pomValue2));
                    second_value_textView.setText(String.valueOf(pomValue2));
                    break;
            }
            return true;
        });

        plus_third_value.setOnTouchListener((v, event) -> {
            setIncrements(event, changeThirdValue);
            incrementValues = true;
            switch (mode) {
                case 1:
//                    if (event.getAction()==MotionEvent.ACTION_DOWN) adjustCustom(true);  break;
                case 2:
                    third_value_single_edit.setText(String.valueOf(pomValue3));
                    third_value_textView.setText(String.valueOf(pomValue3));
                    break;
            }
            return true;
        });

        minus_third_value.setOnTouchListener((v, event) -> {
            incrementValues = false;
            setIncrements(event, changeThirdValue);
            switch (mode) {
                case 2:
                    third_value_single_edit.setText(String.valueOf(pomValue3));
                    third_value_textView.setText(String.valueOf(pomValue3));
                    break;
            }
            return true;
        });

        left_arrow.setOnClickListener(v-> {
            if (!breaksOnly) {
                if (startCustomSetTime.size()>=2 && receivedPos>0) {

                    long holder = startCustomSetTime.get(receivedPos-1);
                    startCustomSetTime.set(receivedPos-1, startCustomSetTime.get(receivedPos));
                    startCustomSetTime.set(receivedPos, holder);
                    customSetTime.set(receivedPos-1, customSetTime.get(receivedPos));
                    customSetTime.set(receivedPos, holder);

                    holder = startCustomBreakTime.get(receivedPos-1);
                    startCustomBreakTime.set(receivedPos-1, startCustomBreakTime.get(receivedPos));
                    startCustomBreakTime.set(receivedPos, holder);
                    customBreakTime.set(receivedPos-1, customBreakTime.get(receivedPos));
                    customBreakTime.set(receivedPos, holder);

                    dotDraws.setTime(customSetTime);
                    dotDraws.breakTime(customBreakTime);
                    drawDots(0);
                    receivedPos -=1;
                    dotDraws.setCycle(receivedPos);
                }
            } else {
                if (startBreaksOnlyTime.size()>=2 && receivedPos>0) {
                    long holder = startBreaksOnlyTime.get(receivedPos-1);
                    startBreaksOnlyTime.set(receivedPos-1, startBreaksOnlyTime.get(receivedPos));
                    startBreaksOnlyTime.set(receivedPos, holder);
                    breaksOnlyTime.set(receivedPos-1, breaksOnlyTime.get(receivedPos));
                    breaksOnlyTime.set(receivedPos, holder);

                    dotDraws.breakTime(breaksOnlyTime);
                    drawDots(0);
                    receivedPos -=1;
                    dotDraws.setCycle(receivedPos);
                }
            }
        });

        right_arrow.setOnClickListener(v-> {
            if (!breaksOnly) {
                if (startCustomSetTime.size()-1 > receivedPos && receivedPos>=0) {

                    long holder = startCustomSetTime.get(receivedPos+1);
                    startCustomSetTime.set(receivedPos+1, startCustomSetTime.get(receivedPos));
                    startCustomSetTime.set(receivedPos, holder);
                    customSetTime.set(receivedPos+1, customSetTime.get(receivedPos));
                    customSetTime.set(receivedPos, holder);

                    holder = startCustomBreakTime.get(receivedPos+1);
                    startCustomBreakTime.set(receivedPos+1, startCustomBreakTime.get(receivedPos));
                    startCustomBreakTime.set(receivedPos, holder);
                    customBreakTime.set(receivedPos+1, customBreakTime.get(receivedPos));
                    customBreakTime.set(receivedPos, holder);

                    dotDraws.setTime(customSetTime);
                    dotDraws.breakTime(customBreakTime);
                    drawDots(0);
                    receivedPos +=1;
                    dotDraws.setCycle(receivedPos);
                }
            } else {
                if (startBreaksOnlyTime.size()-1 > receivedPos && receivedPos>=0) {

                    long holder = startBreaksOnlyTime.get(receivedPos+1);
                    startBreaksOnlyTime.set(receivedPos+1, startBreaksOnlyTime.get(receivedPos));
                    startBreaksOnlyTime.set(receivedPos, holder);
                    breaksOnlyTime.set(receivedPos+1, breaksOnlyTime.get(receivedPos));
                    breaksOnlyTime.set(receivedPos, holder);

                    dotDraws.breakTime(breaksOnlyTime);
                    drawDots(0);
                    receivedPos +=1;
                    dotDraws.setCycle(receivedPos);
                }
            }
        });

        add_cycle.setOnClickListener(v-> {
            adjustCustom(true);
        });

        sub_cycle.setOnClickListener(v-> {
            adjustCustom(false);
        });

        delete_sb.setOnClickListener(v->{
            startCustomSetTime.remove(receivedPos);
            customSetTime.remove(receivedPos);
            if (!breaksOnly) {
                startCustomBreakTime.remove(receivedPos);
                customBreakTime.remove(receivedPos);
                dotDraws.setTime(customSetTime);
                dotDraws.breakTime(customBreakTime);
            } else {
                startBreaksOnlyTime.remove(receivedPos);
                breaksOnlyTime.remove(receivedPos);
                dotDraws.breakTime(breaksOnlyTime);
            }
            savedSets-=1;
            numberOfSets-=1;
            savedBreaks-=1;
            numberOfBreaks-=1;
            drawDots(0);
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
                    if (!breakEnded) {
                        if (!customHalted) {
                            pauseAndResumeTimer(PAUSING_TIMER);
                        } else {
                            pauseAndResumeTimer(RESUMING_TIMER);
                        }
                    } else if (!newBreak) pauseAndResumeTimer(RESETTING_TIMER); else pauseAndResumeTimer(RESTARTING_TIMER);
                    break;
                case 2:
                    if (!pomHalted) {
                        pauseAndResumeTimer(PAUSING_TIMER);
                    } else {
                        pauseAndResumeTimer(RESUMING_TIMER);
                    }
                    break;
                case 3:
                    pauseAndResumeTimer(0);
            }
        });

        cycle_reset.setOnClickListener(v -> {
            boolean clearIt = false;
            if (cycle_reset.getText().equals(getString(R.string.clear_cycles))) {
                switch (mode) {
                    case 1:
                        if (!breaksOnly){
                            if (customCyclesDone>0) clearIt = true;
                        } else if (breaksOnlyCyclesDone>0) clearIt = true;
                        break;
                    case 2:
                        if (pomCyclesDone>0) clearIt = true;
                        break;
                }
                if (clearIt) cycle_reset.setText(R.string.confirm_cycle_reset);
            } else if (cycle_reset.getText().equals(getString(R.string.confirm_cycle_reset))) {
                switch (mode) {
                    case 1:
                        if (!breaksOnly) customCyclesDone = 0; else breaksOnlyCyclesDone = 0;
                        break;
                    case 2:
                        pomCyclesDone = 0;
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
                                customSetTime.remove(0);
                                numberOfSets--;
                            }
                            if (customBreakTime.size() >0 && customBreakTime.size() != customSetTime.size()) {
                                customBreakTime.remove(0);
                                numberOfBreaks--;
                                onBreak = false;
                                oldCycle = customCyclesDone;
                            }
                            if (customSetTime.size() >0) setNewText(timePaused, timePaused, (customSetTime.get(0)+999)/1000);;
                        } else {
                            if (breaksOnlyTime.size() >0) {
                                breaksOnlyTime.remove(0);
                                numberOfBreaks--;
                                oldCycle2 = breaksOnlyCyclesDone;
                                breakBegun = false;
                            }
                            if (breaksOnlyTime.size() >0) {
                                setNewText(timeLeft, timeLeft,(breaksOnlyTime.get(0) +999) / 1000);
                                setNewText(timePaused, timePaused, (breaksOnlyTime.get(0) +999) / 1000);
                            }
                        }
                        dotDraws.setAlpha();
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
                        drawDots(0);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Cannot skip Pomodoro cycles!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        reset.setOnClickListener(v -> {
            if (mode!=2) {
                resetTimer();
            } else {
                if (reset.getText().equals(getString(R.string.reset))) {
                    reset.setText(R.string.confirm_cycle_reset);
                } else {
                    reset.setText(R.string.reset);
                    resetTimer();
                }
            }
        });

        newLap.setOnClickListener(v -> {
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
    }

    public void startObjectAnimator() {
        switch (mode) {
            case 1:
                fadeDone = 1;
                if (!onBreak) {
                    if (!setBegun) {
                        if (customSetTime.size()>0) setMillis = customSetTime.get(0);
                        objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", (int) customProgressPause, 0);
                        objectAnimator.setInterpolator(new LinearInterpolator());
                        objectAnimator.setDuration(setMillis);
                        objectAnimator.start();
                        setBegun = true;
                        activeCustomCycle = true;
                    } else {
                        setMillis = setMillisUntilFinished;
                        if (objectAnimator!=null) objectAnimator.resume();
                    }
                } else {
                    fadeDone = 2;
                    if (!breakBegun) {
                        if (!breaksOnly) {
                            if (customBreakTime.size()>0) breakMillis = customBreakTime.get(0);
                        } else {
                            if (breaksOnlyTime.size()>0) breakMillis = breaksOnlyTime.get(0);
                        }
                        objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", (int) customProgressPause, 0);
                        objectAnimator.setInterpolator(new LinearInterpolator());
                        objectAnimator.setDuration(breakMillis);
                        objectAnimator.start();
                        breakBegun = true;
                        activeCustomBOCycle = true;
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
                    activePomCycle = true;
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
                setNewText(timeLeft, timeLeft,(setMillis + 999)/1000);
                timer = new CountDownTimer(setMillis, 50) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        customProgressPause = (int) objectAnimator.getAnimatedValue();
                        setMillis = millisUntilFinished;

                        if (fadeCustomTimer) {
                            if (customAlpha<0.25) timeLeft.setAlpha(customAlpha+=0.04);
                            else if (customAlpha<0.5) timeLeft.setAlpha(customAlpha+=.08);
                            else if (customAlpha<1) timeLeft.setAlpha(customAlpha+=.12);
                            else if (customAlpha>=1) fadeCustomTimer = false;
                        }

                        timeLeft.setText(convertSeconds((setMillis + 999)/1000));
                        drawDots(1);
                    }

                    @Override
                    public void onFinish() {
                        stopAscent = false;
                        onBreak = true;
                        fadeDone = 0;
                        timeLeft.setText("0");
                        customProgressPause = maxProgress;

                        setBegun = false;
                        customTimerEnded = false;
                        fadeCustomTimer = false;
                        endAnimation();
                        dotDraws.setTime(startCustomSetTime);
                        dotDraws.breakTime(startCustomBreakTime);

                        endFade = new Runnable() {
                            @Override
                            public void run() {
                                drawDots(1);
                                if (receivedAlpha<=100) stopAscent = true;

                                if (stopAscent){
                                    removeSetOrBreak(false);
                                    mHandler.removeCallbacks(this);
                                }
                                if (!stopAscent) mHandler.postDelayed(this, 50);
                            }
                        };
                        mHandler.post(endFade);

                        mHandler.postDelayed(() -> {
                            stopAscent = true;
                            startObjectAnimator();
                            breakStart();
                            endAnimation.cancel();
                            timerDisabled = false;
                            dotDraws.setAlpha();
                        },750);
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

                        if (fadePomTimer) {
                            if (pomAlpha<0.25) timeLeft2.setAlpha(pomAlpha+=0.04);
                            else if (pomAlpha<0.5) timeLeft2.setAlpha(pomAlpha+=.08);
                            else if (pomAlpha<1) timeLeft2.setAlpha(pomAlpha+=.12);
                            else if (pomAlpha>=1) fadePomTimer = false;
                        }
                        if (pomAlpha >=1) fadePomTimer = false;

                        timeLeft2.setText(convertSeconds((pomMillis+999)/1000));
                        dotDraws.pomDraw(pomDotCounter, 1, pomValuesTime);
                    }

                    @Override
                    public void onFinish() {
                        pomBegun = false;
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
                drawDots(2);
            }

            @Override
            public void onFinish() {
                stopAscent = false;
                breakBegun = false;
//                numberOfBreaks--;
                timeLeft.setText("0");
                endAnimation();

                endFade = new Runnable() {
                    @Override
                    public void run() {
                        drawDots(2);
                        if (receivedAlpha<=100) stopAscent = true;

                        if (stopAscent){
                            removeSetOrBreak(true);
                            mHandler.removeCallbacks(this);
                        }
                        if (!stopAscent) mHandler.postDelayed(this, 50);
                    }
                };
                mHandler.post(endFade);

                overtime.setVisibility(View.VISIBLE);
                ot = new Runnable() {
                    @Override
                    public void run() {
                        overSeconds +=1;
                        overtime.setText(getString(R.string.overtime, String.valueOf(overSeconds)));
                        mHandler.postDelayed(this, 1000);
                    }
                };
                mHandler.post(ot);

                if (numberOfBreaks >0) {
                    customProgressPause = maxProgress;
                    customTimerEnded = false;

                    mHandler.postDelayed(() -> {
                        stopAscent = true;
                        if (!breaksOnly) {
                            startTimer();
                            onBreak = false;
                        } else {
                            breakEnded = true;
                        }
                        timerDisabled = false;
                        dotDraws.setAlpha();
                    },750);
                } else {
                    if (!breaksOnly) {
                    customCyclesDone++;
                    cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));
                } else {
                    breaksOnlyCyclesDone++;
                    cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(breaksOnlyCyclesDone)));
                }
                    drawDots(0);
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

    public void setBreaksOnlyMode(boolean onlyBreaks) {
//        if (savedCyclePopupWindow!=null && savedCyclePopupWindow.isShowing()) {
//            savedCyclePopupWindow.dismiss();
//            invalidateOptionsMenu();
//            defaultMenu = true;
//            return;
//        }
        receivedPos = -1;
        if (onlyBreaks) {
            second_value_textView.setText(convertCustomTextView(breaksOnlyValue));
            breaksOnly = true;
            setBegun = true;
            onBreak = true;
            dotDraws.breaksOnly(true);
            cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(breaksOnlyCyclesDone)));
            prefEdit.putBoolean("currentBreaksOnly", true);
            canSaveOrUpdate(canSaveOrUpdateBreaksOnly);
        } else {
            first_value_textView.setText(convertCustomTextView(setValue));
            second_value_textView.setText(convertCustomTextView(breakValue));
            plus_first_value.setEnabled(true);
            minus_first_value.setEnabled(true);
            first_value_edit.setEnabled(true);
            first_value_edit_two.setEnabled(true);
            first_value_edit.setEnabled(true);
            first_value_edit_two.setEnabled(true);
            breaksOnly = false;
            setBegun = false;

            dotDraws.breaksOnly(false);
            cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));
            prefEdit.putBoolean("currentBreaksOnly", false);
            canSaveOrUpdate(canSaveOrUpdateCustom);
        }
//        activeTimerViews(true);
        drawDots(1);
        populateCycleUI();
        prefEdit.apply();
    }

    public void adjustCustom(boolean adding) {
        //Converts editText to long and then convert+sets these values to setValue/breakValue depending on which editTexts are being used.
        if (first_value_edit.isShown()) setEditValueBounds(true);
        if (second_value_edit.isShown()) setEditValueBounds(false);

        if (adding) {
            switch (mode) {
                case 1:
                    if (!breaksOnly) {
                        if (customSetTime.size() < 8 && customSetTime.size() >= 0) {
                            startCustomSetTime.add(setValue * 1000);
                            startCustomBreakTime.add(breakValue * 1000);
                            canSaveOrUpdate(true);
                            populateCycleUI();
                        } else Toast.makeText(getApplicationContext(), "Max rounds reached!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (breaksOnlyTime.size() < 8 && breaksOnlyTime.size() >= 0) {
                            startBreaksOnlyTime.add(breaksOnlyValue * 1000);
                            canSaveOrUpdate(true);
                            populateCycleUI();
                        } else Toast.makeText(getApplicationContext(), "Max rounds reached!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    if (pomValuesTime.size()==0) {
                        for (int i=0; i<3; i++)  {
                            pomValuesTime.add(pomValue1); pomValuesTime.add(pomValue2);
                        }
                        pomValuesTime.add(pomValue1);
                        pomValuesTime.add(pomValue3);
                        populateCycleUI();
                    } else Toast.makeText(getApplicationContext(), "Pomodoro cycle already loaded!", Toast.LENGTH_SHORT).show();
            }
        } else {
            switch (mode) {
                case 1:
                    if (!breaksOnly) {
                        if (customSetTime.size() > 0 && startCustomSetTime.size() > 0) {
                            startCustomSetTime.remove(startCustomSetTime.size() - 1);
                            startCustomBreakTime.remove(startCustomBreakTime.size() - 1);
                            canSaveOrUpdate(true);
                            populateCycleUI();
                        } else Toast.makeText(getApplicationContext(), "Nothing left to remove!", Toast.LENGTH_SHORT).show();
                        //Used w/ arrows to switch set/break places.
                        if (customSetTime.size() - 1 < receivedPos) receivedPos = customSetTime.size() - 1;
                    } else {
                        if (breaksOnlyTime.size() > 0 && startBreaksOnlyTime.size() > 0) {
                            startBreaksOnlyTime.remove(startBreaksOnlyTime.size() - 1);
                            //Used w/ arrows to switch  break places.
                            if (breaksOnlyTime.size() - 1 < receivedPos) receivedPos = breaksOnlyTime.size() - 1;
                            canSaveOrUpdate(true);
                            populateCycleUI();
                        } else Toast.makeText(getApplicationContext(), "Nothing left to remove!", Toast.LENGTH_SHORT).show();
                    }
                    if (receivedPos >=0) dotDraws.setCycle(receivedPos);
                    break;
                case 2:
                    if (pomValuesTime.size()!=0) {
                        pomValuesTime.clear();
                        populateCycleUI();
                    } else Toast.makeText(getApplicationContext(), "No Pomodoro cycle to clear!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        saveArrays();
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
        sizeAnimator = va;
        sizeAnimator.addUpdateListener(animation -> {
            float sizeChange = (float) va.getAnimatedValue();
            textView.setTextSize(sizeChange);
            textViewPaused.setTextSize(sizeChange);
        });
        sizeAnimator.setRepeatCount(0);
        sizeAnimator.start();
    }

    public void setNewText(TextView oldTextView, TextView currentTextView, long newTime) {
        boolean fadeTime = false;
        String oldText = (String) oldTextView.getText();
        if (!oldText.equals("") && !oldText.equals("?")) {
            oldText = oldText.replace(":", "");
            long oldTime = Long.parseLong(oldText);
            if (oldTime<60 && newTime>=60) {
                fadeTime = true;
                switch (mode) {
                    case 1:
                        changeTextSize(valueAnimatorDown, timeLeft, timePaused); break;
                    case 2:
                        changeTextSize(valueAnimatorDown, timeLeft2, timePaused2); break;
                    case 3:
                        changeTextSize(valueAnimatorDown, timeLeft3,  timePaused3);
                }
            } else if (oldTime>=60 && newTime<60) {
                fadeTime = true;
                switch (mode) {
                    case 1:
                        changeTextSize(valueAnimatorUp, timeLeft, timePaused); break;
                    case 2:
                        changeTextSize(valueAnimatorUp, timeLeft2, timePaused2); break;
                    case 3:
                        changeTextSize(valueAnimatorUp, timeLeft3,  timePaused3);
                }
            }
        }
        if (fadeTime) {
            switch (mode) {
                case 1:
                    if (customHalted) fadeTextIn(timePaused); else fadeTextIn(timeLeft); break;
                case 2:
                    if (pomHalted) fadeTextIn(timePaused2); else fadeTextIn(timeLeft2); break;
                case 3:
                    if (stopwatchHalted) fadeTextIn(timePaused3); else fadeTextIn(timeLeft3);
            }
        }
        currentTextView.setText(convertSeconds(newTime));
    }

    public void fadeTextIn(TextView textView) {
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

    //Todo: Removed defunct loadIDCycle from here. Replace w/ setCycle() @ true.
    public void switchTimer(int mode, boolean halted) {
        removeViews();
        tabViews();
        switch (mode) {
            case 1:
                if (!breaksOnly) {
                    savedCycleAdapter.setView(1);
                    onBreak = false;
                    cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));
                } else {
                    savedCycleAdapter.setView(2);
                    onBreak = true;
                    cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(breaksOnlyCyclesDone)));
                }
                if (setMillisUntilFinished==0) setMillisUntilFinished = setMillis;
                if (breakMillisUntilFinished==0) breakMillisUntilFinished = breakMillis;
                if (halted) {
                    if (!breaksOnly) {
                        setNewText(lastTextView, timePaused, (setMillis + 999)/1000);
                    } else {
                        setNewText(lastTextView, timePaused, (breakMillis + 999)/1000);
                    }
                    fadeTextIn(timePaused);
                    drawDots(1);
                } else {
                    if (!breaksOnly) {
                        setNewText(lastTextView, timeLeft, (setMillis + 999)/1000);
                    } else {
                        setNewText(lastTextView, timeLeft, (breakMillis + 999)/1000);
                    }
                    customAlpha = 0;
                    fadeCustomTimer = true;
                    startObjectAnimator();
                }
                break;
            case 2:
                savedCycleAdapter.setView(3);
                cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(pomCyclesDone)));
                if (pomMillisUntilFinished==0) pomMillisUntilFinished = pomMillis;
                if (halted) {
                    fadeTextIn(timePaused2);
                    dotDraws.pomDraw(pomDotCounter, 1, pomValuesTime);
                    setNewText(lastTextView, timePaused2, (pomMillis + 999)/1000);
                } else {
                    pomAlpha = 0;
                    fadePomTimer = true;
                    startObjectAnimator();
                    setNewText(lastTextView, timeLeft2, (pomMillis + 999)/1000);
                }
                AsyncTask.execute(() -> {
                    queryCycles();
                    runOnUiThread(()-> {
                        if (pomCyclesList.size() > 0) {
                            String title = pomCyclesList.get(0).getTitle();
                            cycle_header_text.setText(title);
                        } else cycle_header_text.setText(R.string.default_title);
                    });
                });
                break;
            case 3:
                //Same animation instance can't be used simultaneously for both TextViews.
                cycles_completed.setText(getString(R.string.laps_completed, String.valueOf(lapsNumber)));
                dotDraws.setMode(3);
                dotDraws.pomDraw(pomDotCounter, 1, pomValuesTime);
                if (stopwatchHalted) {
                    fadeTextIn(timePaused3);
                } else {
                    fadeTextIn(timeLeft3);
                }
        }
        dotDraws.retrieveAlpha();
    }


    public void removeViews() {
        timePaused.setAlpha(0);
        timeLeft.setAlpha(0);
        timePaused2.setAlpha(0);
        timeLeft2.setAlpha(0);
        timeLeft3.setAlpha(0);
        timePaused3.setAlpha(0);
        msTime.setAlpha(0);
        msTimePaused.setAlpha(0);
        overtime.setVisibility(View.INVISIBLE);
    }

    public void clearArrays(boolean populateList) {
        switch (mode) {
            case 1:
                if (!breaksOnly) {
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
                } else {
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
                }
                break;
            case 2:
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
                if (!breaksOnly) {
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
                } else {
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
                }
                break;
            case 2:
                switch (sortModePom) {
                    case 1:
                        pomCyclesList = cyclesDatabase.cyclesDao().loadPomCyclesMostRecent();
                        break;
                    case 2:
                        pomCyclesList = cyclesDatabase.cyclesDao().loadPomCyclesLeastRecent();
                        break;
                }
                break;
        }
    }

    public void canSaveOrUpdate(boolean yesWeCan) {
        switch (mode) {
            case 1:
                if (!breaksOnly) {
                    canSaveOrUpdateCustom = yesWeCan;
                } else {
                    canSaveOrUpdateBreaksOnly = yesWeCan;
                }
                break;
            case 2:
                canSaveOrUpdatePom = yesWeCan;
                break;
        }

        if ( (!breaksOnly && canSaveOrUpdateCustom) || (breaksOnly && canSaveOrUpdateBreaksOnly) || (mode==2 && canSaveOrUpdatePom)) {
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
                sortCheckmark.setY(14);
                switch (mode) {
                    case 1:
                        AsyncTask.execute(() -> {
                            if (!breaksOnly) sortMode = 1; else sortModeBO = 1;
                            queryCycles();
                        });
                        break;
                    case 2:
                        AsyncTask.execute(()-> {
                            sortModePom = 1;
                            queryCycles();
                        });
                        break;
                }
                clearArrays(true);
            });

            sortNotRecent.setOnClickListener(v2 ->{
                sortCheckmark.setY(110);
                switch (mode) {
                    case 1:
                        AsyncTask.execute(() -> {
                            if (!breaksOnly) sortMode = 2; else sortModeBO = 2;
                            queryCycles();
                        });
                        break;
                    case 2:
                        sortModePom = 2;
                        queryCycles();
                        break;
                }
                clearArrays(true);
            });

            sortHigh.setOnClickListener(v3 -> {
                sortCheckmark.setY(206);
                AsyncTask.execute(() -> {
                    if (!breaksOnly) sortMode = 3; else sortModeBO = 3;
                    queryCycles();
                    clearArrays(true);
                });
            });

            sortLow.setOnClickListener(v4 -> {
                sortCheckmark.setY(302);
                AsyncTask.execute(() -> {
                    if (!breaksOnly) sortMode = 4; else sortModeBO = 4;
                    queryCycles();
                    clearArrays(true);
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

    //Actually saves or updates the cycle.
    public void confirmedSaveOrUpdate(int saveOrUpdate) {
        if ((!breaksOnly && startCustomSetTime.size()==0) || (breaksOnly && startBreaksOnlyTime.size()==0) || (mode==2 && pomValuesTime.size()==0)) {
            Toast.makeText(getApplicationContext(), "Nothing to save!", Toast.LENGTH_SHORT).show();;
            return;
        }

        AsyncTask.execute(() -> {
            //Defaulting to unique cycle unless otherwise set by retrieveAndSetCycles();
            duplicateCycle = false;
            boolean changeCycle = false;
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMMM d yyyy - hh:mma", Locale.getDefault());

            //Executes gSon->Json conversion and compares new String to db lists, setting duplicateCycles to TRUE if an instance matches.
            retrieveAndCheckCycles();

            switch (mode) {
                case 1:
                    if (!breaksOnly) {
                        //New instance of the Cycle entity that can be used for insertion. Otherwise, inheriting the instance from onCycleClick callback that uses a specific position to update.
                        //Todo: Watch this. Updating correctly but the retrieval seems wrong. The call of setCycle() with the proper position based on ID happens before this call.
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
                            cycles.setItemCount(startCustomSetTime.size());
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
                    } else {
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
                            cyclesBO.setItemCount(startBreaksOnlyTime.size());
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
                    }
                    break;
                case 2:
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

    //Used to retrieve a single cycle within our database. Calls populateUICycle() which sets the Array values into our timer millis values.
    //When recall is TRUE, retrieves the last used ID instance, when recall is FALSE, Uses a positional input from our saved cycle list.
    public void setCycle(int position, boolean recall) {
        AsyncTask.execute(() -> {
            int posHolder = position;
            switch (mode) {
                case 1:
                    if (!breaksOnly) {
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
                        startCustomSetTime.clear();
                        customBreakTime.clear();
                        startCustomBreakTime.clear();

                        for (int i=0; i<setSplit.length; i++) {
                            customSetTime.add(Long.parseLong(setSplit[i])*1000);
                            startCustomSetTime.add(Long.parseLong(setSplit[i])*1000);
                            customBreakTime.add(Long.parseLong(breakSplit[i])*1000);
                            startCustomBreakTime.add(Long.parseLong(breakSplit[i])*1000);
                        }
                        runOnUiThread(() -> cycle_header_text.setText(cycles.getTitle()));
                    } else {
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
                        startBreaksOnlyTime.clear();
                        breaksOnlyTime.clear();

                        for (int i=0; i<breaksOnlySplit.length; i++) {
                            breaksOnlyTime.add(Long.parseLong(breaksOnlySplit[i])*1000);
                            startBreaksOnlyTime.add(Long.parseLong(breaksOnlySplit[i])*1000);
                        }
                        runOnUiThread(() -> cycle_header_text.setText(cyclesBO.getTitle()));
                    }
                    break;
                case 2:
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

    //All convertedLists are used in save/update method.
    private void retrieveAndCheckCycles() {
        queryCycles();
        Gson gson = new Gson();

        switch (mode) {
            case 1:
                ArrayList<Long> tempSets = new ArrayList<>();
                ArrayList<Long> tempBreaks = new ArrayList<>();
                ArrayList<Long> tempBreaksOnly = new ArrayList<>();

                //Getting current cycle, populated in UI but unsaved.
                if (!breaksOnly) {
                    for (int i=0; i<startCustomSetTime.size(); i++) {
                        tempSets.add(startCustomSetTime.get(i) /1000);
                    }
                    for (int i=0; i<startCustomBreakTime.size(); i++){
                        tempBreaks.add(startCustomBreakTime.get(i)/1000);
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
                } else {
                    for (int i=0; i<startBreaksOnlyTime.size(); i++) {
                        tempBreaksOnly.add(startBreaksOnlyTime.get(i)/1000);
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
                }
                break;
            case 2:
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
                    Log.i("testPom", convertedPomList + " " + cycle_header_text.getText());
                    Log.i("testPom", pomCyclesList.get(0).getFullCycle() + " " + pomCyclesList.get(0).getTitle());
                }
                break;
        }
    }

    //Todo: For Pom.
    public void setDefaultCustomCycle(boolean forBreaksOnly) {
        if (!forBreaksOnly) {
            startCustomSetTime.clear();
            startCustomBreakTime.clear();
            for (int i = 0; i < 3; i++) {
                startCustomSetTime.add((long) 30 * 1000);
                startCustomBreakTime.add((long) 30 * 1000);
            }
            setValue = 30;
            breakValue = 30;
        } else {
            startBreaksOnlyTime.clear();
            for (int i=0; i<3; i++) {
                startBreaksOnlyTime.add((long) 30 * 1000);
            }
            breaksOnlyValue = 30;
        }
    }

    public void drawDots(int fadeVar) {
        dotDraws.newDraw(savedSets, savedBreaks, numberOfSets, numberOfBreaks, fadeVar);
    }

    public void removeSetOrBreak(boolean onBreak) {
        if (!onBreak) {
            numberOfSets--;
            if (customSetTime.size() > 0) {
                customSetTime.remove(0);
            }
            if (customSetTime.size()>0) setMillis = customSetTime.get(0);
        } else {
            numberOfBreaks--;
            if (!breaksOnly) {
                if (customBreakTime.size() >0) {
                    customBreakTime.remove(0);
                }
                if (customBreakTime.size()>0) breakMillis = customBreakTime.get(0);
                dotDraws.setTime(startCustomSetTime);
                dotDraws.breakTime(startCustomBreakTime);
            } else {
                if (breaksOnlyTime.size()>0) {
                    breaksOnlyTime.remove(0);
                }
                if (breaksOnlyTime.size()>0) breakMillis = breaksOnlyTime.get(0);
            }
        }
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
            if (minutes>=10 && timeLeft3.getTextSize() != 70f) timeLeft3.setTextSize(70f);
            return (df.format(minutes) + ":" + df2.format(roundedSeconds));
        } else {
            if (timeLeft3.getTextSize() != 90f) timeLeft3.setTextSize(90f);
            return df.format(seconds);
        }
    }

    //Called on +/- buttons, which use runnables to change set/break value vars. This method a)sets editTexts to these changing values and b)sets the textView to reflect them.
    public void convertEditTime() {
        editSetSeconds = setValue%60;
        editSetMinutes = setValue/60;
        if (!breaksOnly) {
            editBreakSeconds = breakValue%60;
            editBreakMinutes = breakValue/60;
        } else {
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
        prefEdit.putLong("pomValue1", pomValue1);
        prefEdit.putLong("pomValue2", pomValue2);
        prefEdit.putLong("pomValue3", pomValue3);
        prefEdit.apply();
    }

    public void setEditValueBounds(boolean onSets){
        switch (mode) {
            case 1:
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

                    if (!breaksOnly) {
                        breakValue = (editBreakMinutes * 60) + editBreakSeconds;
                    } else {
                        breaksOnlyValue = (editBreakMinutes * 60) + editBreakSeconds;
                    }
                    second_value_textView.setText(convertCustomTextView(breakValue));
                }
                break;
            case 2:
                break;
        }
        setTimerValueBounds();
    }

    //This is called via setIncrements() is is called within plus/minus touch listeners.
    public void setTimerValueBounds() {
        switch (mode) {
            case 1:
                toastBounds(5, 300, setValue); toastBounds(5, 300, breakValue); toastBounds(5, 300, breaksOnlyValue);
                if (setValue<5) setValue = 5; if (breakValue<5) breakValue = 5; if (breaksOnlyValue <5) breaksOnlyValue = 5;
                if (setValue>300) setValue = 300; if (breakValue>300) breakValue =300; if (breaksOnlyValue>300) breaksOnlyValue = 300;
                break;
            case 2:
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

    public void tabViews(){
        switch (mode) {
            case 1:
                first_value_textView.setText(convertCustomTextView(setValue));
                if (!breaksOnly) {
                    second_value_textView.setText(convertCustomTextView(breakValue));
                    first_value_textView.setVisibility(View.VISIBLE);
                    plus_first_value.setVisibility(View.VISIBLE);
                    minus_first_value.setVisibility(View.VISIBLE);
                    s1.setVisibility(View.VISIBLE);
                } else {
                    first_value_textView.setVisibility(View.GONE);
                    plus_first_value.setVisibility(View.GONE);
                    minus_first_value.setVisibility(View.GONE);
                    s1.setVisibility(View.GONE);
                    second_value_textView.setText(convertCustomTextView(breaksOnlyValue));
                }

                progressBar.setVisibility(View.VISIBLE);
                progressBar2.setVisibility(View.INVISIBLE);
                stopWatchView.setVisibility(View.GONE);
                cycle_reset.setVisibility(View.VISIBLE);
                skip.setVisibility(View.VISIBLE);
                s1.setText(R.string.set_time);
                s2.setText(R.string.break_time);
//                s3.setText(R.string.rounds);
                cycle_reset.setText(R.string.clear_cycles);
                s1.setTextSize(22f);
                s2.setTextSize(22f);
                first_value_textView.setTextSize(23f);
                second_value_textView.setTextSize(23f);
                s3.setVisibility(View.GONE);
                third_value_textView.setVisibility(View.GONE);
                third_value_single_edit.setVisibility(View.GONE);
                plus_third_value.setVisibility(View.GONE);
                minus_third_value.setVisibility(View.GONE);
                break;
            case 2:
                first_value_single_edit.setText(convertSeconds(pomValue1));
                second_value_single_edit.setText(convertSeconds(pomValue2));
                third_value_single_edit.setText(convertSeconds(pomValue3));

                first_value_textView.setVisibility(View.VISIBLE);
                plus_first_value.setVisibility(View.VISIBLE);
                minus_first_value.setVisibility(View.VISIBLE);
                s1.setVisibility(View.VISIBLE);

                s3.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                progressBar2.setVisibility(View.VISIBLE);
                stopWatchView.setVisibility(View.GONE);
                cycle_reset.setVisibility(View.VISIBLE);
                skip.setVisibility(View.VISIBLE);
                plus_third_value.setVisibility(View.VISIBLE);
                minus_third_value.setVisibility(View.VISIBLE);

                third_value_textView.setVisibility(View.VISIBLE);
                first_value_textView.setText(String.valueOf(pomValue1));
                second_value_textView.setText(String.valueOf(pomValue2));
                third_value_textView.setText(String.valueOf(pomValue3));

                newLap.setVisibility(View.GONE);
                s1.setText(R.string.work_time);
                s2.setText(R.string.small_break);
                s3.setText(R.string.long_break);
                cycle_reset.setText(R.string.clear_cycles);
                s1.setTextSize(21f);
                s2.setTextSize(21f);
                s3.setTextSize(21f);
                break;
            case 3:
                progressBar.setVisibility(View.INVISIBLE);
                progressBar2.setVisibility(View.INVISIBLE);
                stopWatchView.setVisibility(View.VISIBLE);
                skip.setVisibility(View.GONE);
                cycle_reset.setVisibility(View.GONE);

                newLap.setVisibility(View.VISIBLE);
                cycle_reset.setText(R.string.clear_laps);
                msTime.setAlpha(1);
                msTimePaused.setAlpha(1);

                timeLeft3.setText(displayTime);
                timePaused3.setText(displayTime);
                msTime.setText(displayMs);
                msTimePaused.setText(displayMs);
        }
    }

    public void saveArrays() {
        Gson gson = new Gson();

        String savedSetArrays = "";
        String savedBreakArrays = "";
        String savedBOArrays = "";
        String savedTitle = "";
        if (!edit_header.getText().toString().equals("")) savedTitle = edit_header.getText().toString(); else savedTitle = getString(R.string.default_title);
        if (startCustomSetTime.size()>0){
            savedSetArrays = gson.toJson(startCustomSetTime);
            savedBreakArrays = gson.toJson(startCustomBreakTime);
        }
        if (startBreaksOnlyTime.size()>0) savedBOArrays = gson.toJson(startBreaksOnlyTime);

        prefEdit.putString("setArrays", savedSetArrays);
        prefEdit.putString("breakArrays", savedBreakArrays);
        prefEdit.putString("savedBOArrays", savedBOArrays);
        prefEdit.putString("savedTitle", savedTitle);
        prefEdit.apply();
        Log.i("testFormat", savedTitle);
    }

    public void pauseAndResumeTimer(int pausing) {
        if ( (setMillis <=500 || breakMillis <=500) && numberOfBreaks >0) {
            timerDisabled = true;
        }
        if ( (!timerDisabled && mode==1) || (!pomTimerDisabled && mode==2) ){
            delete_sb.setVisibility(View.INVISIBLE);
            add_cycle.setEnabled(false);
            sub_cycle.setEnabled(false);
            removeViews();
            if (fadeInObj!=null) fadeInObj.cancel();
            if (fadeOutObj!=null) fadeOutObj.cancel();
            switch (mode) {
                case 1:
                    if (!customTimerEnded) {
                        if (!stopAscent) {
                            if (!onBreak) removeSetOrBreak(false); else removeSetOrBreak(true);
                            mHandler.removeCallbacks(endFade);
                            stopAscent = true;
                        }
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
                            if (onBreak) breakStart(); else startTimer();
                            reset.setVisibility(View.INVISIBLE);
                        } else if (pausing == RESETTING_TIMER) {
                            if (endAnimation != null) endAnimation.cancel();
                            mHandler.removeCallbacks(ot);
                            overSeconds = 0;
                            overtime.setVisibility(View.INVISIBLE);
                            progressBar.setProgress(10000);
                            timePaused.setAlpha(1.0f);
                            timePaused.setText(convertSeconds(((breaksOnlyTime.get(0)+999)) /1000));
                            newBreak = true;
                        } else if (pausing == RESTARTING_TIMER) {
                            breakEnded = false;
                            newBreak = false;
                            startObjectAnimator();
                            breakStart();
                            breakMillis = breakStart;
                            timerDisabled = false;
                            fadeCustomTimer = false;
                            timePaused.setAlpha(0f);
                            timeLeft.setAlpha(1.0f);
                        }
                    } else {
                        resetTimer();
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
                case 3:
                    DecimalFormat df2 = new DecimalFormat("00");
                    if (fadeInObj!=null) fadeInObj.cancel();
                    if (stopwatchHalted) {
                        timeLeft3.setAlpha(1);
                        timePaused3.setAlpha(0);
                        msTime.setAlpha(1);
                        msTimePaused.setAlpha(0);
                        stopWatchRunnable = new Runnable() {
                            @Override
                            public void run() {
                                //ms can never be more than 60/sec due to refresh rate.
                                ms+=1;
                                msReset +=1;
                                msConvert+=1;
                                msConvert2+=1;
                                msDisplay+=1;
                                if (msConvert>59) msConvert=0;
                                if (msConvert2>59) msConvert2=0;
                                if (msDisplay>59) msDisplay=0;

                                seconds = ms/60;
                                minutes = seconds/60;

                                newMs = df2.format((msConvert2/60) * 100);
                                savedMs = df2.format((msConvert/60) * 100);
                                newMsConvert = Integer.parseInt(newMs);
                                savedMsConvert = Integer.parseInt(savedMs);

                                //Conversion to long solves +30 ms delay for display.
                                displayMs = df2.format((msDisplay/60) * 100);
                                displayTime = convertStopwatch((long) seconds);

                                timeLeft3.setText(displayTime);
                                msTime.setText(displayMs);
                                mHandler.postDelayed(this, 10);
                            }
                        };
                        mHandler.post(stopWatchRunnable);
                        stopwatchHalted = false;
                        reset.setVisibility(View.INVISIBLE);
                    } else {
                        timeLeft3.setAlpha(0);
                        timePaused3.setAlpha(1);
                        msTime.setAlpha(0);
                        msTimePaused.setAlpha(1);
                        timePaused3.setText(timeLeft3.getText());
                        msTimePaused.setText(msTime.getText());
                        mHandler.removeCallbacksAndMessages(null);
                        stopwatchHalted = true;
                        reset.setVisibility(View.VISIBLE);
                    }
            }
        } else Toast.makeText(getApplicationContext(), "What are we timing?", Toast.LENGTH_SHORT).show();
    }

    public void populateCycleUI() {
        switch (mode) {
            case 1:
                if (!breaksOnly) {
                    if (startCustomSetTime.size()>0) setMillis = startCustomSetTime.get(0);
                    if (startCustomBreakTime.size()>0) {
                        breakMillis = startCustomBreakTime.get(0);
                        timePaused.setText(convertSeconds((setMillis+999)/1000));
                        timeLeft.setText(convertSeconds((setMillis+999)/1000));
                    } else timePaused.setText("?");
                } else {
                    if (startBreaksOnlyTime.size()>0) {
                        breakMillis = startBreaksOnlyTime.get(0);
                        timePaused.setText(convertSeconds((breakMillis+999)/1000));
                        timeLeft.setText(convertSeconds((breakMillis+999)/1000));
                    } else timePaused.setText("?");
                }
                numberOfSets = startCustomSetTime.size();
                if (!breaksOnly) numberOfBreaks = startCustomBreakTime.size();
                else numberOfBreaks = startBreaksOnlyTime.size();

                savedSets = numberOfSets;
                savedBreaks = numberOfBreaks;

                if (!breaksOnly) {
                    customSetTime = new ArrayList<>();
                    customBreakTime = new ArrayList<>();
                } else breaksOnlyTime = new ArrayList<>();

                if (!breaksOnly) {
                    if (startCustomSetTime.size() >0) {
                        for (int i=0; i<startCustomSetTime.size(); i++) {
                            customSetTime.add(startCustomSetTime.get(i));
                            customBreakTime.add(startCustomBreakTime.get(i));
                        }
                        timerDisabled = false;
                    } else timerDisabled = true;
                    dotDraws.breakTime(customBreakTime);
                    third_value_single_edit.setText(String.valueOf(customSetTime.size()));
                    activeCustomCycle = false;
                } else {
                    if (startBreaksOnlyTime.size()>0) {
                        breaksOnlyTime.addAll(startBreaksOnlyTime);
                        timerDisabled = false;
                    } else timerDisabled = true;

                    dotDraws.breakTime(breaksOnlyTime);
                    third_value_single_edit.setText(String.valueOf(breaksOnlyTime.size()));
                    activeCustomBOCycle = false;
                }

                //Always setting this to avoid null errors.
                dotDraws.setTime(customSetTime);
                dotDraws.setAlpha();
                drawDots(0);
                break;
            case 2:
                //Here is where we set the initial millis Value of first pomMillis. Set again on change on our value runnables.
                if (pomValuesTime.size()!=0) pomMillis1 = pomValuesTime.get(0)*1000*60;
                pomMillis = pomMillis1;
                dotDraws.pomDraw(pomDotCounter, 0, pomValuesTime);
                if (pomValuesTime.size()==0) {
                    pomTimerDisabled = true;
                    timePaused2.setText("?");
                } else {
                    pomTimerDisabled = false;
                    timePaused2.setText(convertSeconds((pomMillis+999)/1000));
                }
                dotDraws.setAlpha();
                break;
        }

    }

    public void resetTimer() {
        removeViews();
        //Todo: Separate end animations, especially since adding stopwatch.
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
                populateCycleUI();
                break;
            case 2:
                timePaused2.setAlpha(1);
                pomTimerEnded = false;
                progressBar2.setProgress(10000);
                pomBegun = false;
                pomProgressPause = maxProgress;
                onBreak = false;
                pomHalted = true;
                activePomCycle = false;
                if (timer2 != null) timer2.cancel();
                if (objectAnimator2 != null) objectAnimator2.cancel();
                populateCycleUI();
                break;
            case 3:
                stopwatchHalted = true;
                ms = 0;
                msConvert = 0;
                msConvert2 = 0;
                msDisplay = 0;
                msReset = 0;
                seconds = 0;
                minutes = 0;
                lapsNumber = 0;
                timeLeft3.setAlpha(1);
                msTime.setAlpha(1);
                timeLeft3.setText("0");
                msTime.setText("00");
                cycles_completed.setText(getString(R.string.laps_completed, String.valueOf(0)));
                if (currentLapList.size()>0) currentLapList.clear();
                if (savedLapList.size()>0) savedLapList.clear();
                lapAdapter.notifyDataSetChanged();
                break;
        }
        add_cycle.setBackgroundColor(getResources().getColor(R.color.test_grey));
        sub_cycle.setBackgroundColor(getResources().getColor(R.color.test_grey));
        add_cycle.setEnabled(true);
        sub_cycle.setEnabled(true);
    }
}