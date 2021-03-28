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
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
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
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SavedCycleAdapter.onCycleClickListener, SavedCycleAdapter.onDeleteCycleListener, SavedCycleAdapter.onEditTitleListener, DotDraws.sendPosition {

    View mainView;
    Button breaks_only;
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

    TextView cycle_header_text;
    TextView cycles_completed;
    TextView cycle_reset;
    EditText first_value_edit;
    EditText first_value_edit_two;
    TextView first_value_sep;
    TextView first_value_textView;
    EditText second_value_edit;
    EditText second_value_edit_two;
    TextView second_value_sep;
    TextView second_value_textView;
    EditText third_value_edit;
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

    ImageView sortCheckmark;
    TextView skip;
    TextView newLap;

    List<Long> setValueSaves;
    List<Long> breakValueSaves;
    List<Long> breaksOnlyValueSaves;
    List<Long> pomOneSaves;
    List<Long> pomTwoSaves;
    List<Long> pomThreeSaves;

    int PAUSING_TIMER = 1;
    int RESUMING_TIMER = 2;
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
    boolean customHalted = true;
    boolean pomHalted = true;
    boolean stopwatchHalted = true;

    DotDraws dotDraws;
    int fadeDone;
    int mode=1;
    ValueAnimator sizeAnimator;
    ValueAnimator valueAnimatorDown;
    ValueAnimator valueAnimatorUp;

    public ArrayList<String> customTitleArray;
    public ArrayList<String> breaksOnlyTitleArray;
    public ArrayList<Long> customSetTime;
    public ArrayList<Long> customBreakTime;
    public ArrayList<Long> startCustomSetTime;
    public ArrayList<Long> startCustomBreakTime;
    public ArrayList<Long> startBreaksOnlyTime;
    public ArrayList<Long> breaksOnlyTime;
    String convertedSetList;
    String convertedBreakList;
    String convertedBreakOnlyList;

    boolean setBegun;
    boolean breakBegun;
    boolean pomBegun;
    boolean drawing = true;
    boolean breaksOnly;

    PopupWindow clearCyclePopupWindow;
    PopupWindow resetPopUpWindow;
    PopupWindow sortPopupWindow;
    PopupWindow savedCyclePopupWindow;
    PopupWindow labelSavePopupWindow;
    PopupWindow deleteAllPopupWindow;

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
    boolean existingCycle;
    int lastDatabasePos;

    ArrayList<String> setsArray;
    ArrayList<String> breaksArray;
    ArrayList<String> breaksOnlyArray;

    RecyclerView savedCycleRecycler;
    SavedCycleAdapter savedCycleAdapter;

    View clearCyclePopupView;
    View deleteCyclePopupView;
    View sortCyclePopupView;
    View savedCyclePopupView;
    View cycleLabelView;

    TextView sortRecent;
    TextView sortNotRecent;
    TextView sortHigh;
    TextView sortLow;
    TextView delete_all_text;
    Button delete_all_confirm;
    Button delete_all_cancel;

    int sortMode = 1;
    int sortModeBO = 1;
    MaterialButton pauseResumeButton;
    int receivedPos;
    MotionEvent motionEvent;
    int customID;
    int breaksOnlyID;
    boolean canSaveOrUpdateCustom = true;
    boolean canSaveOrUpdateBreaksOnly = true;
    boolean duplicateCycle;
    boolean appLaunchingCustom = true;
    boolean appLaunchingBO = true;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor prefEdit;

    //Todo: Possibly grey out add/subtract (right side) buttons when cycle is running, until reset is hit.
    //Todo: Possibly switch order of sets/breaks (i.e. first one added is first one executed).
    //Todo: Deactivate Update button if nothing is saved yet.

    //Todo: Breaks only should not start new timer automatically, since there are still untimed sets between them.
    //Todo: Need to figure out how changing pom values affects timer status (i.e. when it's running)
    //Todo: Different layout (e.g. no increment rows) for Pom mode.
    //Todo: Recalled set/breaks should be re-saved instead of creating new saves.
    //Todo: Any popup should disable main view buttons (true focuable?)
    //Todo: Options to delete individual set/break.
    //Todo: First box selection should highlight. Only NOT highlight w/ 1 set/break listed.
    //Todo: Single break or break/set option, like Stopwatch but counting down on repeat.
    //Todo: EditTexts w/ hints as default view. Make sure minute/hours difference in custom/pom are clear.
    //Todo: Add/Remove cycle elements from individual places.

    //Todo: Rippling for certain onClicks.
    //Todo: Inconsistencies w/ fading.
    //Todo: Add taskbar notification for timers.
    //Todo: Have skip round reset dot alpha.
    //Todo: Add color scheme options.
    //Todo: Test all Pom cycles.
    //Todo: All DB calls in aSync.
    //Todo: Rename app, of course.
    //Todo: Add onOptionsSelected dots for About, etc.
    //Todo: Repository for db. Look at Executor/other alternate thread methods.
    //Todo: Make sure number pad is dismissed when switching to stopwatch mode.


    //Todo: REMEMBER, always call queryCycles() to get a cyclesList reference, otherwise it won't sync w/ the current sort mode.
    @Override
    public void sendPos(int pos) {
        if (mode==1) {
            receivedPos = pos;
            if (pos <=0) {
                left_arrow.setVisibility(View.INVISIBLE);
                right_arrow.setVisibility(View.INVISIBLE);
            } else {
                left_arrow.setVisibility(View.VISIBLE);
                right_arrow.setVisibility(View.VISIBLE);
            }
            Log.i("testcb", "pos is " + pos);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        motionEvent = event;
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (event.getAction()==MotionEvent.ACTION_DOWN) {
            dotDraws.selectCycle(x, y, startCustomSetTime.size());
        }
        Log.i("coords", "x and y are " + x + " and " + y);

        //This dismisses windows/views when main layout (view) is clicked. We use it instead of using a clickable instance of our layout since that bugs.
        if (savedCyclePopupWindow!=null && savedCyclePopupWindow.isShowing()) {
            save_cycles.setText(R.string.save_cycles);
            savedCyclePopupWindow.dismiss();
        }
        if (first_value_edit.isShown()) {
            first_value_textView.setVisibility(View.VISIBLE);
            first_value_edit.setVisibility(View.INVISIBLE);
            first_value_edit_two.setVisibility(View.INVISIBLE);
            first_value_sep.setVisibility(View.INVISIBLE);
        }
        if (second_value_edit.isShown()) {
            second_value_textView.setVisibility(View.VISIBLE);
            second_value_edit.setVisibility(View.INVISIBLE);
            second_value_edit_two.setVisibility(View.INVISIBLE);
            second_value_sep.setVisibility(View.INVISIBLE);
        }
        return false;
    }

    @Override
    public void onTitleEdit(String text, int position) {

    }

    //Todo: Remember that any reference to our GLOBAL instance of a cycles position will retain that position unless changed.
    @Override
    public void onCycleClick(int position) {
        setCycle(position);
    }

    @Override
    public void onCycleDelete(int position) {
        if (!breaksOnly) {
            AsyncTask.execute(() -> {
                queryCycles();
                int deletedID = cyclesList.get(position).getId();

                Cycles removedCycle = cyclesList.get(position);
                cyclesDatabase.cyclesDao().deleteCycle(removedCycle);
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

                //Comparing this ID w/ one tied to cycle currently displayed.
                if (deletedID == customID) {
                    customID = 0;
                    prefEdit.putInt("customID", 0);
                    prefEdit.apply();
                    clearArrays(false);
                    runOnUiThread(() -> {
                        setDefaultCustomCycle();
                        resetTimer();
                    });
                }
                Log.i("idcheck", "deleted id is " + deletedID);
            });
        } else {
            queryCycles();
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

            if (deletedID == breaksOnlyID) {
                breaksOnlyID = 0;
                prefEdit.putInt("breaksOnlyID", 0);
                prefEdit.apply();
                runOnUiThread(() -> {
                    setDefaultCustomCycle();
                    resetTimer();
                });
            }
        }
        Toast.makeText(getApplicationContext(), "Cycle deleted!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (savedCyclePopupWindow != null && savedCyclePopupWindow.isShowing()) savedCyclePopupWindow.dismiss();
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (savedCyclePopupWindow!=null &&savedCyclePopupWindow.isShowing()) return false;

        switch (item.getItemId()) {
            case R.id.saved_cycle_list:
                save_cycles.setText(R.string.sort_cycles);
                AsyncTask.execute(() -> {
                    queryCycles();
                    clearArrays(false);
                    Log.i("testingPos", "customID is " + customID);

                    if (!breaksOnly) {
                            savedCycleAdapter.setBreaksOnly(false);
                            if (cyclesList.size()>0) {
                                for (int i=0; i<cyclesList.size(); i++) {
                                    //getSets/Breaks returns String [xx, xy, xz] etc.
                                    setsArray.add(cyclesList.get(i).getSets());
                                    breaksArray.add(cyclesList.get(i).getBreaks());
                                    customTitleArray.add(cyclesList.get(i).getTitle());
                                }
                                runOnUiThread(() -> {
                                    //Focusable must be false for save/sort switch function to work, otherwise window will steal focus from button.
                                    savedCyclePopupWindow.showAtLocation(mainView, Gravity.CENTER, 0, 100);
                                    savedCycleAdapter.notifyDataSetChanged();
                                });
                            } else {
                                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Nothing saved!", Toast.LENGTH_SHORT).show());
                            }
                        } else {
                            savedCycleAdapter.setBreaksOnly(true);
                            if (cyclesBOList.size()>0) {
                                for (int i=0; i<cyclesBOList.size(); i++) {
                                    breaksOnlyTitleArray.add(cyclesBOList.get(i).getTitle());
                                    breaksOnlyArray.add(cyclesBOList.get(i).getBreaksOnly());
                                }
                                runOnUiThread(() -> {
                                    savedCyclePopupWindow.showAtLocation(mainView, Gravity.CENTER, 0, 100);
                                    savedCycleAdapter.notifyDataSetChanged();
                                });
                            } else {
                                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Nothing saved!", Toast.LENGTH_SHORT).show());
                            }
                        }
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

        valueAnimatorDown = new ValueAnimator().ofFloat(90f, 70f);
        valueAnimatorUp = new ValueAnimator().ofFloat(70f, 90f);
        valueAnimatorDown.setDuration(1000);
        valueAnimatorUp.setDuration(1000);

        setsArray = new ArrayList<>();
        breaksArray = new ArrayList<>();
        breaksOnlyArray = new ArrayList<>();
        customTitleArray = new ArrayList<>();
        breaksOnlyTitleArray = new ArrayList<>();

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        savedCyclePopupView = inflater.inflate(R.layout.saved_cycles_layout, null);
        deleteCyclePopupView = inflater.inflate(R.layout.delete_cycles_popup, null);
        sortCyclePopupView = inflater.inflate(R.layout.sort_popup, null);
        cycleLabelView = inflater.inflate(R.layout.label_cycle_popup, null);
        clearCyclePopupView = inflater.inflate(R.layout.pom_reset_popup, null);

        savedCycleRecycler = savedCyclePopupView.findViewById(R.id.cycle_list_recycler);
        LinearLayoutManager lm2 = new LinearLayoutManager(getApplicationContext());
        savedCycleAdapter = new SavedCycleAdapter(getApplicationContext(), setsArray, breaksArray, breaksOnlyArray, customTitleArray, breaksOnlyTitleArray);
        savedCycleRecycler.setAdapter(savedCycleAdapter);
        savedCycleRecycler.setLayoutManager(lm2);
        savedCycleAdapter.setTitleEdit(MainActivity.this);
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

        s1 = findViewById(R.id.s1);
        s2 = findViewById(R.id.s2);
        s3 = findViewById(R.id.s3);
        reset = findViewById(R.id.reset);
        cycle_header_text = findViewById(R.id.cycle_header_text);
        first_value_edit = findViewById(R.id.first_value_edit);
        first_value_edit_two = findViewById(R.id.first_value_edit_two);
        first_value_sep = findViewById(R.id.first_value_sep);
        first_value_textView = findViewById(R.id.first_value_textView);
        second_value_edit = findViewById(R.id.second_value_edit);
        second_value_edit_two = findViewById(R.id.second_value_edit_two);
        second_value_sep = findViewById(R.id.second_value_sep);
        second_value_textView = findViewById(R.id.second_value_textView);
        plus_first_value = findViewById(R.id.plus_first_value);
        minus_first_value = findViewById(R.id.minus_first_value);
        plus_second_value = findViewById(R.id.plus_second_value);
        minus_second_value = findViewById(R.id.minus_second_value);
        plus_third_value = findViewById(R.id.plus_third_value);
        minus_third_value = findViewById(R.id.minus_third_value);
        third_value_edit = findViewById(R.id.third_value_edit);
        delete_all_text = deleteCyclePopupView.findViewById(R.id.delete_confirm_text);
        delete_all_confirm = deleteCyclePopupView.findViewById(R.id.confirm_yes);
        delete_all_cancel = deleteCyclePopupView.findViewById(R.id.confirm_no);

        add_cycle = findViewById(R.id.add_cycle);
        sub_cycle = findViewById(R.id.subtract_cycle);
        cycles_completed = findViewById(R.id.cycles_completed);
        cycle_reset = findViewById(R.id.cycle_reset);
        skip = findViewById(R.id.skip);
        newLap = findViewById(R.id.new_lap);
        left_arrow = findViewById(R.id.left_arrow);
        right_arrow = findViewById(R.id.right_arrow);


        left_arrow.setVisibility(View.INVISIBLE);
        right_arrow.setVisibility(View.INVISIBLE);
        reset.setVisibility(View.INVISIBLE);

        breaks_only = findViewById(R.id.breaks_only);
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
        pauseResumeButton = findViewById(R.id.pauseResumeButton);
        pauseResumeButton.setBackgroundColor(Color.argb(0, 0, 0, 0));
        pauseResumeButton.setRippleColor(null);

        dotDraws.onPositionSelect(MainActivity.this);

        s1.setText(R.string.set_time);
        s2.setText(R.string.break_time);
        s3.setText(R.string.set_number);
        save_cycles.setText(R.string.save_cycles);
        update_cycles.setText(R.string.update_cycles);
        confirm_header_save.setText(R.string.save_cycles);

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

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Custom"));
        tabLayout.addTab(tabLayout.newTab().setText("Pomodoro"));
        tabLayout.addTab(tabLayout.newTab().setText("Stopwatch"));

        mHandler = new Handler();
        setValueSaves = new ArrayList<>();
        breakValueSaves = new ArrayList<>();
        breaksOnlyValueSaves = new ArrayList<>();
        pomOneSaves = new ArrayList<>();
        pomTwoSaves = new ArrayList<>();
        pomThreeSaves = new ArrayList<>();

        customSetTime = new ArrayList<>();
        customBreakTime = new ArrayList<>();
        startCustomSetTime = new ArrayList<>();
        startCustomBreakTime = new ArrayList<>();
        breaksOnlyTime = new ArrayList<>();
        startBreaksOnlyTime = new ArrayList<>();
        currentLapList = new ArrayList<>();
        savedLapList = new ArrayList<>();

        progressBar2.setVisibility(View.GONE);
        stopWatchView.setVisibility(View.GONE);
        newLap.setVisibility(View.GONE);
        lapRecycler.setVisibility(View.GONE);

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

        AsyncTask.execute(() -> {
            mode = sharedPreferences.getInt("currentMode", 1);
            sortMode = sharedPreferences.getInt("sortMode", 1);
            breaksOnly = sharedPreferences.getBoolean("currentBreaksOnly", false);
            customID = sharedPreferences.getInt("customID", 0);
            breaksOnlyID = sharedPreferences.getInt("breaksOnlyID", 0);

            cyclesDatabase = CyclesDatabase.getDatabase(getApplicationContext());
            //All db queries that fetch full lists should through queryCycles() to ensure sort order is correct.
            queryCycles();
            pomCyclesList = cyclesDatabase.cyclesDao().loadAllPomCycles();

            if (!breaksOnly) {
                if (appLaunchingCustom) {
                    if (cyclesList.size()>0 && customID>0) {
                        cyclesList = cyclesDatabase.cyclesDao().loadSingleCycle(customID);
                        setsArray.add(cyclesList.get(0).getSets());
                        breaksArray.add(cyclesList.get(0).getBreaks());
                        setCycle(0);
                    }
                } else setDefaultCustomCycle();
            } else {
                if (appLaunchingBO) {
                    if (cyclesBOList.size()>0 && breaksOnlyID>0) {
                        cyclesBOList = cyclesDatabase.cyclesDao().loadSingleCycleBO(breaksOnlyID);
                        breaksOnlyArray.add(cyclesBOList.get(0).getBreaksOnly());
                        setCycle(0);
                    }
                } else setDefaultCustomCycle();
            }
        });

        if (cyclesList.size()==0){
            setDefaultCustomCycle();
        }

        if (cyclesBOList.size()==0) {
            setDefaultBOCycle();
        }

        //Custom defaults
        savedSets = customSetTime.size();
        savedBreaks = customBreakTime.size();
        numberOfSets = savedSets;
        numberOfBreaks = savedBreaks;

        pomValue1 = 25;
        pomValue2 = 5;
        pomValue3 = 15;
        setMillis = setValue;
        breakMillis = breakValue;

        lapLayout= new LinearLayoutManager(getApplicationContext());
        lapAdapter = new LapAdapter(getApplicationContext(), currentLapList, savedLapList);
        lapRecycler.setAdapter(lapAdapter);
        lapRecycler.setLayoutManager(lapLayout);

        progressBar.setProgress(maxProgress);
        progressBar2.setProgress(maxProgress);

        incrementTimer = 10;
        tabViews();
        resetTimer();
        //This is done because we are calling the method which "switches" from one to the other, and we want the last one used.
        breaksOnly = !breaksOnly;
        setBreaksOnlyMode();

        //Retrieves the most recently viewed cycle.
        if (!breaksOnly && cyclesList.size()>0) setCycle(0);
        if (breaksOnly && cyclesBOList.size()>0) setCycle(0);
        if ((!breaksOnly && cyclesList.size()>0)  || (breaksOnly && cyclesBOList.size()>0)){
            canSaveOrUpdate(false);
        } else canSaveOrUpdate(true);

        savedCyclePopupWindow = new PopupWindow(savedCyclePopupView, 800, 1200, false);
        deleteAllPopupWindow = new PopupWindow(deleteCyclePopupView, 750, 375, false);
        labelSavePopupWindow = new PopupWindow(cycleLabelView, 800, 400, true);
        sortPopupWindow = new PopupWindow(sortCyclePopupView, 400, 375, true);
        clearCyclePopupWindow  = new PopupWindow(clearCyclePopupView, 150, WindowManager.LayoutParams.WRAP_CONTENT, false);
        resetPopUpWindow  = new PopupWindow(clearCyclePopupView, WindowManager.LayoutParams.WRAP_CONTENT, 75, false);
        savedCyclePopupWindow.setAnimationStyle(R.style.WindowAnimation);
        deleteAllPopupWindow.setAnimationStyle(R.style.WindowAnimation);
        labelSavePopupWindow.setAnimationStyle(R.style.WindowAnimation);
        sortPopupWindow.setAnimationStyle(R.style.WindowAnimation);
        clearCyclePopupWindow.setAnimationStyle(R.style.WindowAnimation);
        resetPopUpWindow.setAnimationStyle(R.style.WindowAnimation);

        delete_all_confirm.setOnClickListener(v-> {
            if (!breaksOnly) cyclesDatabase.cyclesDao().deleteAll(); else cyclesDatabase.cyclesDao().deleteAllBO();
            runOnUiThread(() -> {
                Toast.makeText(getApplicationContext(), "Deleted!", Toast.LENGTH_SHORT).show();
                deleteAllPopupWindow.dismiss();
            });
        });

        delete_all_cancel.setOnClickListener(v-> {
            deleteAllPopupWindow.dismiss();
        });

        cycle_header_text.setOnClickListener(v-> {
            confirm_header_save.setText(R.string.update_cycles);
            labelSavePopupWindow.showAtLocation(mainView, Gravity.CENTER, 0, -200);

            String titleText = "";
            if (!breaksOnly) titleText = cycles.getTitle(); else titleText = cyclesBO.getTitle();
            edit_header.setText(titleText);
            edit_header.setSelection(titleText.length());

            cancel_header_save.setOnClickListener(v2-> {
                if (labelSavePopupWindow!=null) labelSavePopupWindow.dismiss();
            });


            String oldTitle = edit_header.getText().toString();
            confirm_header_save.setOnClickListener(v2-> {
                AsyncTask.execute(() -> {
                    String newTitle = edit_header.getText().toString();
                    if (!breaksOnly) cyclesDatabase.cyclesDao().updateCustomTitle(newTitle, customID);
                    else cyclesDatabase.cyclesDao().updateBOTitle(newTitle, breaksOnlyID);
                    runOnUiThread(() -> {
                        if (!oldTitle.equals(newTitle)) {
                            Toast.makeText(getApplicationContext(), "Title updated", Toast.LENGTH_SHORT).show();
                            cycle_header_text.setText(newTitle);
                            canSaveOrUpdate(true);
                        }
                        labelSavePopupWindow.dismiss();
                    });
                });
            });
        });

        first_value_textView.setOnClickListener(v-> {
            if (!breaksOnly) {
                if (first_value_textView.isShown()) {
                    first_value_textView.setVisibility(View.INVISIBLE);
                    first_value_edit.setVisibility(View.VISIBLE);
                    first_value_edit_two.setVisibility(View.VISIBLE);
                    first_value_sep.setVisibility(View.VISIBLE);
                }
                if (second_value_edit.isShown() || second_value_edit_two.isShown()) {
                    second_value_textView.setVisibility(View.VISIBLE);
                    second_value_edit.setVisibility(View.INVISIBLE);
                    second_value_edit_two.setVisibility(View.INVISIBLE);
                    second_value_sep.setVisibility(View.INVISIBLE);
                }
            }
        });

        second_value_textView.setOnClickListener(v-> {
            if (second_value_textView.isShown()) {
                second_value_textView.setVisibility(View.INVISIBLE);
                second_value_edit.setVisibility(View.VISIBLE);
                second_value_edit_two.setVisibility(View.VISIBLE);
                second_value_sep.setVisibility(View.VISIBLE);
            }
            if (first_value_edit.isShown() || first_value_edit_two.isShown()) {
                first_value_textView.setVisibility(View.VISIBLE);
                first_value_edit.setVisibility(View.INVISIBLE);
                first_value_edit_two.setVisibility(View.INVISIBLE);
                first_value_sep.setVisibility(View.INVISIBLE);
            }
        });

        changeFirstValue = new Runnable() {
            @Override
            public void run() {
                switch (mode) {
                    case 1:
                        if (incrementValues) setValue+=1; else setValue -=1; break;
                    case 2:
                        if (incrementValues) pomValue1+=1; else pomValue1 -=1; break;
                }
                mHandler.postDelayed(this, incrementTimer*10);
            }
        };

        changeSecondValue = new Runnable() {
            @Override
            public void run() {
                switch (mode) {
                    case 1:
                        if (!breaksOnly) {
                            if (incrementValues) breakValue+=1; else breakValue -=1;
                        } else
                        if (incrementValues) breaksOnlyValue+=1; else breaksOnlyValue -=1; break;
                    case 2:
                        if (incrementValues) pomValue2+=1; else pomValue2 -=1; break;
                }
                mHandler.postDelayed(this, incrementTimer*10);
            }
        };

        changeThirdValue = new Runnable() {
            @Override
            public void run() {
                if (incrementValues) pomValue3+=1; else pomValue3 -=1;
                mHandler.postDelayed(this, incrementTimer*10);
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
                    break;
                case 2:
                    first_value_textView.setText(convertSeconds(pomValue1));
                    timePaused2.setText(convertSeconds(pomValue1));
                    timeLeft2.setText(convertSeconds(pomValue1));
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
                    break;
                case 2:
                    first_value_edit.setText(convertSeconds(pomValue1));
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
                    if (!breaksOnly) second_value_textView.setText(convertSeconds(breakValue)); else second_value_textView.setText(convertSeconds(breaksOnlyValue));
                    break;
                case 2:
                    second_value_edit.setText(convertSeconds(pomValue2));
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
                    if (!breaksOnly) second_value_textView.setText(convertSeconds(breakValue)); else second_value_textView.setText(convertSeconds(breaksOnlyValue));
                    break;
                case 2:
                    second_value_edit.setText(convertSeconds(pomValue2));
                    break;
            }
            return true;
        });

        plus_third_value.setOnTouchListener((v, event) -> {
            switch (mode) {
                case 1:
//                    if (event.getAction()==MotionEvent.ACTION_DOWN) adjustCustom(true);  break;
                case 2:
                    incrementValues = true;
                    setIncrements(event, changeThirdValue);
                    third_value_edit.setText(convertSeconds(pomValue3));
                    break;
            }
            return true;
        });

        minus_third_value.setOnTouchListener((v, event) -> {
            switch (mode) {
                case 1:
//                    if (event.getAction()==MotionEvent.ACTION_DOWN) adjustCustom(false);  break;
                case 2:
                    incrementValues = false;
                    setIncrements(event, changeThirdValue);
                    third_value_edit.setText(convertSeconds(pomValue3));
                    break;
            }
            return true;
        });

        capEditNumber(first_value_edit, 5);
        capEditNumber(first_value_edit_two, 59);
        capEditNumber(second_value_edit, 5);
        capEditNumber(second_value_edit_two, 59);

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
                    dotDraws.newDraw(savedSets, savedBreaks, 0, 0, 0);
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
                    dotDraws.newDraw(savedSets, savedBreaks, 0, 0, 0);
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
                    dotDraws.newDraw(savedSets, savedBreaks, 0, 0, 0);
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
                    dotDraws.newDraw(savedSets, savedBreaks, 0, 0, 0);
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

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        mode=1;
                        switchTimer(1, customHalted);
                        dotDraws.setMode(1);
                        if (!setBegun) dotDraws.newDraw(savedSets, savedBreaks, 0, 0, 0);
                        prefEdit.putInt("currentMode", 1);
                        break;
                    case 1:
                        mode=2;
                        switchTimer(2, pomHalted);
                        dotDraws.setMode(2);
                        dotDraws.pomDraw(1, 0);
                        prefEdit.putInt("currentMode", 2);
                        break;
                    case 2:
                        mode=3;
                        switchTimer(3, stopwatchHalted);
                        lapRecycler.setVisibility(View.VISIBLE);
                        prefEdit.putInt("currentMode", 3);
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
                        if (clearCyclePopupWindow!=null) clearCyclePopupWindow.dismiss();
                        if (resetPopUpWindow!=null) resetPopUpWindow.dismiss();
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

        breaks_only.setOnClickListener(v-> {
            setBreaksOnlyMode();
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
                    if (!customHalted) {
                        pauseAndResumeTimer(PAUSING_TIMER);
                    } else {
                        pauseAndResumeTimer(RESUMING_TIMER);
                    }
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
            switch (mode) {
                case 1:
                    if (!breaksOnly) clearCycles(customCyclesDone); else clearCycles(breaksOnlyCyclesDone);
                    break;
                case 2:
                    clearCycles(pomCyclesDone);
                    break;
                case 3:
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
                                customSetTime.remove(customSetTime.size()-1);
                                numberOfSets--;
                            }
                            if (customBreakTime.size() >0 && customBreakTime.size() != customSetTime.size()) {
                                customBreakTime.remove(customBreakTime.size()-1);
                                numberOfBreaks--;
                                onBreak = false;
                                oldCycle = customCyclesDone;
                            }
                            if (customSetTime.size() >0) setNewText(timePaused, timePaused, (customSetTime.get(customSetTime.size()-1)+999)/1000);;
                        } else {
                            if (breaksOnlyTime.size() >0) {
                                breaksOnlyTime.remove(breaksOnlyTime.size()-1);
                                numberOfBreaks--;
                                oldCycle2 = breaksOnlyCyclesDone;
                                breakBegun = false;
                            }
                            if (breaksOnlyTime.size() >0) {
                                setNewText(timeLeft, timeLeft,(breaksOnlyTime.get(breaksOnlyTime.size() - 1) +999) / 1000);
                                setNewText(timePaused, timePaused, (breaksOnlyTime.get(breaksOnlyTime.size() - 1) +999) / 1000);
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

        reset.setOnClickListener(v -> {
            reset.setVisibility(View.GONE);
            if (mode!=2) {
                resetTimer();
            } else {
                resetPopUpWindow.showAtLocation(mainView, Gravity.CENTER, 0, 900);

                TextView confirm_reset = clearCyclePopupView.findViewById(R.id.pom_reset_text);
                confirm_reset.setText(R.string.pom_reset);
                confirm_reset.setOnClickListener(v2-> {
                    resetTimer();
                    resetPopUpWindow.dismiss();
                });
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
//                        setNewText(timeLeft, (setMillis + 999)/1000);
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
                        mHandler.postDelayed((Runnable) () -> {
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

                        if (fadePomTimer) {
                            if (pomAlpha<0.25) timeLeft2.setAlpha(pomAlpha+=0.04);
                            else if (pomAlpha<0.5) timeLeft2.setAlpha(pomAlpha+=.08);
                            else if (pomAlpha<1) timeLeft2.setAlpha(pomAlpha+=.12);
                            else if (pomAlpha>=1) fadePomTimer = false;
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
//                setNewText(timeLeft, (millisUntilFinished +999) / 1000);
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

                    mHandler.postDelayed(() -> {
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

    public void setBreaksOnlyMode() {
        if (savedCyclePopupWindow!=null && savedCyclePopupWindow.isShowing()) {
            savedCyclePopupWindow.dismiss();
            return;
        }
        receivedPos = -1;

        Animation fadeIn = new AlphaAnimation(0.3f, 1.0f);
        fadeIn.setDuration(100);

        Animation fadeOut = new AlphaAnimation(1.0f, 0.3f);
        fadeOut.setDuration(100);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                if (breaksOnly) {
                    s1.setAlpha(0.3f);
                    plus_first_value.setAlpha(0.3f);
                    minus_first_value.setAlpha(0.3f);
                    first_value_textView.setAlpha(0.3f);
                    first_value_edit.setVisibility(View.INVISIBLE);
                    first_value_edit_two.setVisibility(View.INVISIBLE);
                    second_value_edit.setVisibility(View.INVISIBLE);
                    second_value_edit_two.setVisibility(View.INVISIBLE);
                    first_value_textView.setVisibility(View.VISIBLE);
                    second_value_textView.setVisibility(View.VISIBLE);
                    first_value_sep.setVisibility(View.INVISIBLE);
                    second_value_sep.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!breaksOnly) {
                    s1.setAlpha(1);
                    plus_first_value.setAlpha(1.0f);
                    minus_first_value.setAlpha(1.0f);
                    first_value_textView.setAlpha(1.0f);
                    first_value_edit.setVisibility(View.INVISIBLE);
                    first_value_edit_two.setVisibility(View.INVISIBLE);
                    second_value_edit.setVisibility(View.INVISIBLE);
                    second_value_edit_two.setVisibility(View.INVISIBLE);
                    first_value_textView.setVisibility(View.VISIBLE);
                    second_value_textView.setVisibility(View.VISIBLE);
                    first_value_sep.setVisibility(View.INVISIBLE);
                    second_value_sep.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });


        if (mode==1) {
            if (!breaksOnly) {
                second_value_textView.setText(convertSeconds(breaksOnlyValue));
                s1.setAnimation(fadeOut);
                plus_first_value.setAnimation(fadeOut);
                minus_first_value.setAnimation(fadeOut);
                plus_first_value.setEnabled(false);
                minus_first_value.setEnabled(false);
                first_value_edit.setEnabled(false);
                first_value_edit_two.setEnabled(false);
                first_value_edit.setEnabled(false);
                first_value_edit_two.setEnabled(false);

                breaksOnly = true;
                setBegun = true;
                onBreak = true;
                breaks_only.setBackgroundColor(getResources().getColor(R.color.light_grey));
                dotDraws.breaksOnly(true);
                cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(breaksOnlyCyclesDone)));
                prefEdit.putBoolean("currentBreaksOnly", true);
                canSaveOrUpdate(canSaveOrUpdateBreaksOnly);
                //Todo: Get title from row w/ customID.
                if (!appLaunchingBO) {
                    AsyncTask.execute(() -> {
                        queryCycles();
                        if (cyclesBOList.size()>0) {
                            String title = cyclesBOList.get(breaksOnlyID).getTitle();
                            runOnUiThread(() -> {
                                cycle_header_text.setText(title);
                            });
                        } else cycle_header_text.setText(R.string.default_title);
                    });
                }
            } else {
                s1.setAnimation(fadeIn);
                first_value_textView.setText(convertSeconds(setValue));
                second_value_textView.setText(convertSeconds(breakValue));
                plus_first_value.setAnimation(fadeIn);
                minus_first_value.setAnimation(fadeIn);
                plus_first_value.setEnabled(true);
                minus_first_value.setEnabled(true);
                first_value_edit.setEnabled(true);
                first_value_edit_two.setEnabled(true);
                first_value_edit.setEnabled(true);
                first_value_edit_two.setEnabled(true);
                breaksOnly = false;
                setBegun = false;
                breaks_only.setBackgroundColor(getResources().getColor(R.color.black));
                dotDraws.breaksOnly(false);
                cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(customCyclesDone)));
                prefEdit.putBoolean("currentBreaksOnly", false);
                canSaveOrUpdate(canSaveOrUpdateCustom);
                if (!appLaunchingCustom) {
                    AsyncTask.execute(() -> {
                        queryCycles();
                        if (cyclesList.size() > 0) {
                            String title = cyclesList.get(customID).getTitle();
                            runOnUiThread(() -> {
                                cycle_header_text.setText(title);
                            });
                        } else cycle_header_text.setText(R.string.default_title);
                    });
                }
            }
            dotDraws.newDraw(savedSets, savedBreaks, savedSets-(numberOfSets-1), savedBreaks-(numberOfBreaks-1), 1);
            resetTimer();
            prefEdit.apply();
        }
    }

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
        if (setValue<5) setValue = 5; if (breakValue<5) breakValue = 5; if (breaksOnlyValue <5) breaksOnlyValue = 5;
        if (setValue>300) setValue = 300; if (breakValue>300) breakValue =300; if (breaksOnlyValue>300) breaksOnlyValue = 300;
        if (pomValue1<10) pomValue1 = 10; if (pomValue1>120) pomValue1 = 120;
        if (pomValue2<1) pomValue2 = 1; if (pomValue2>10) pomValue2 = 10;
        if (pomValue3<10) pomValue3 = 10; if (pomValue3>60) pomValue3 = 60;
    }

    //Todo: setValue is solid w/ +/- buttons. Need to make sure editText value can also transfer. Probably need a listener on editText.
    public void adjustCustom(boolean adding) {
        if (adding) {
            if (!breaksOnly) {
                if (customSetTime.size() < 10 && customSetTime.size() >= 0) {
//                    setValue = convertStringToLong(first_value_edit.getText().toString());
                    customSetTime.add(setValue * 1000);
                    startCustomSetTime.add(setValue * 1000);
                    canSaveOrUpdate(true);
                } else {
                    Toast.makeText(getApplicationContext(), "Max rounds reached!", Toast.LENGTH_SHORT).show();
                }
                if (customBreakTime.size() < 10 && customBreakTime.size() >= 0) {
//                    breakValue = convertStringToLong(second_value_edit.getText().toString());
                    customBreakTime.add(breakValue * 1000);
                    startCustomBreakTime.add(breakValue * 1000);
                    canSaveOrUpdate(true);
                }
            } else {
                if (breaksOnlyTime.size() < 10 && breaksOnlyTime.size() >= 0) {
//                    breaksOnlyValue = convertStringToLong(second_value_edit.getText().toString());
                    breaksOnlyTime.add(breaksOnlyValue * 1000);
                    startBreaksOnlyTime.add(breaksOnlyValue * 1000);
                    canSaveOrUpdate(true);
                } else {
                    Toast.makeText(getApplicationContext(), "Max rounds reached!", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            if (!breaksOnly) {
                if (customSetTime.size() > 0 && startCustomSetTime.size() > 0) {
                    customSetTime.remove(customSetTime.size() - 1);
                    startCustomSetTime.remove(startCustomSetTime.size() - 1);
                    canSaveOrUpdate(true);
                } else {
                    Toast.makeText(getApplicationContext(), "Nothing left to remove!", Toast.LENGTH_SHORT).show();
                }
                if (customBreakTime.size() > 0 && startCustomBreakTime.size() > 0) {
                    customBreakTime.remove(customBreakTime.size() - 1);
                    startCustomBreakTime.remove(startCustomBreakTime.size() - 1);
                    canSaveOrUpdate(true);
                }
                //Used w/ arrows to switch set/break places.
                if (customSetTime.size()-1<receivedPos) receivedPos = customSetTime.size()-1;
            } else {
                if (breaksOnlyTime.size() > 0 && startBreaksOnlyTime.size() > 0) {
                    breaksOnlyTime.remove(breaksOnlyTime.size() - 1);
                    startBreaksOnlyTime.remove(startBreaksOnlyTime.size() - 1);
                    //Used w/ arrows to switch  break places.
                    if (breaksOnlyTime.size()-1<receivedPos) receivedPos = breaksOnlyTime.size()-1;
                    canSaveOrUpdate(true);
                } else {
                    Toast.makeText(getApplicationContext(), "Nothing left to remove!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        resetTimer();
        dotDraws.newDraw(savedSets, savedBreaks, 0, 0, 0);

        if (!breaksOnly) {
            if (customSetTime.size()>0) {
                setNewText(timePaused, timePaused, (customSetTime.get(customSetTime.size() - 1) +999) / 1000);
            } else {
                timePaused.setText("?");
            }
        } else {
            if (breaksOnlyTime.size()>0) {
                setNewText(timePaused, timePaused2, (breaksOnlyTime.get(breaksOnlyTime.size() - 1) +999) / 1000);
            } else {
                timePaused.setText("?");
            }
        }
        if (receivedPos >=0) dotDraws.setCycle(receivedPos);
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

    public void switchTimer(int mode, boolean halted) {
        removeViews();
        tabViews();
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

                if (setMillisUntilFinished==0) setMillisUntilFinished = setMillis;
                if (breakMillisUntilFinished==0) breakMillisUntilFinished = breakMillis;
                if (halted) {
                    if (!breaksOnly) {
                        setNewText(lastTextView, timePaused, (setMillis + 999)/1000);
                    } else {
                        setNewText(lastTextView, timePaused, (breakMillis + 999)/1000);
                    }
                    fadeTextIn(timePaused);
                    dotDraws.newDraw(savedSets, savedBreaks, savedSets- (numberOfSets -1), savedBreaks- (numberOfBreaks-1), 1);
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
                drawing = false;
                cycles_completed.setText(getString(R.string.cycles_done, String.valueOf(pomCyclesDone)));
                if (pomMillisUntilFinished==0) pomMillisUntilFinished = pomMillis;
                if (halted) {
                    fadeTextIn(timePaused2);
                    dotDraws.pomDraw(pomDotCounter, 1);
                    setNewText(lastTextView, timePaused2, (pomMillis + 999)/1000);
                } else {
                    pomAlpha = 0;
                    fadePomTimer = true;
                    startObjectAnimator();
                    setNewText(lastTextView, timeLeft2, (pomMillis + 999)/1000);
                }
                break;
            case 3:
                //Same animation instance can't be used simultaneously for both TextViews.
                cycles_completed.setText(getString(R.string.laps_completed, String.valueOf(lapsNumber)));
                dotDraws.setMode(3);
                dotDraws.pomDraw(pomDotCounter, 1);
                if (stopwatchHalted) {
                    fadeTextIn(timePaused3);
                } else {
                    fadeTextIn(timeLeft3);
                }
        }
        dotDraws.retrieveAlpha();
    }

    public void pauseAndResumeTimer(int pausing) {
        if (setMillis <=500 || breakMillis <=500) {
            timerDisabled = true;
        }
        if (!timerDisabled) {
            add_cycle.setBackgroundColor(getResources().getColor(R.color.Gray));
            sub_cycle.setBackgroundColor(getResources().getColor(R.color.Gray));
            add_cycle.setEnabled(false);
            sub_cycle.setEnabled(false);
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
        } else if ( (!breaksOnly && customSetTime.size()==0) || (breaksOnly && breaksOnlyTime.size()==0)) {
            Toast.makeText(getApplicationContext(), "What are we timing?", Toast.LENGTH_SHORT).show();
        }
    }

    public void clearCycles(int cycleCount) {
        if (cycleCount>0) {
            cycle_reset.setVisibility(View.GONE);
            clearCyclePopupWindow.showAtLocation(mainView, Gravity.CENTER, 400, 465);

            TextView confirm_reset = clearCyclePopupView.findViewById(R.id.pom_reset_text);
            confirm_reset.setGravity(Gravity.CENTER_HORIZONTAL);
            confirm_reset.setText(R.string.pom_cycle_reset);

            confirm_reset.setOnClickListener(v2-> {
                if (mode==1) {
                    if (!breaksOnly) customCyclesDone = 0; else breaksOnlyCyclesDone = 0;
                } else if (mode==2) pomCyclesDone = 0;
                cycle_reset.setVisibility(View.VISIBLE);
                cycles_completed.setText(getString(R.string.cycles_done, "0"));
                clearCyclePopupWindow.dismiss();;
            });
        }
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
    }

    public void tabViews(){
        ConstraintLayout.LayoutParams params2 = (ConstraintLayout.LayoutParams) cycle_reset.getLayoutParams();
        switch (mode) {
            case 1:
                progressBar.setVisibility(View.VISIBLE);
                progressBar2.setVisibility(View.INVISIBLE);
                stopWatchView.setVisibility(View.GONE);
                s1.setVisibility(View.VISIBLE);
                s2.setVisibility(View.VISIBLE);
                s3.setVisibility(View.INVISIBLE);
                first_value_edit.setVisibility(View.INVISIBLE);
                first_value_edit_two.setVisibility(View.INVISIBLE);
                first_value_sep.setVisibility(View.INVISIBLE);
                second_value_edit.setVisibility(View.INVISIBLE);
                second_value_edit_two.setVisibility(View.INVISIBLE);
                second_value_sep.setVisibility(View.INVISIBLE);
//                convertEditTime(setValue, 1);
                convertEditTime();
                if (!breaksOnly) {
//                    s1.setVisibility(View.VISIBLE);
                    first_value_edit.setText(String.valueOf(editSetMinutes));
                    first_value_edit_two.setText(String.valueOf(editSetSeconds));
                } else {
//                    s1.setVisibility(View.INVISIBLE);
//                    plus_first_value.setVisibility(View.INVISIBLE);
//                    minus_first_value.setVisibility(View.INVISIBLE);
                }
                plus_first_value.setVisibility(View.VISIBLE);
                minus_first_value.setVisibility(View.VISIBLE);
                plus_second_value.setVisibility(View.VISIBLE);
                minus_second_value.setVisibility(View.VISIBLE);
                plus_third_value.setVisibility(View.INVISIBLE);
                minus_third_value.setVisibility(View.INVISIBLE);
                third_value_edit.setVisibility(View.INVISIBLE);
                skip.setVisibility(View.VISIBLE);
                newLap.setVisibility(View.GONE);
                s1.setText(R.string.set_time);
                s2.setText(R.string.break_time);
                first_value_textView.setText(convertSeconds(setValue));
                cycle_reset.setText(R.string.clear_cycles);
                params2.width = 150;
//                first_value_edit.setFilters(new InputFilter[]{
//                        new NumberFilter(0, 60)
//                });
                break;
            case 2:
                progressBar.setVisibility(View.INVISIBLE);
                progressBar2.setVisibility(View.VISIBLE);
                stopWatchView.setVisibility(View.GONE);
                s1.setVisibility(View.VISIBLE);
                s2.setVisibility(View.VISIBLE);
                s3.setVisibility(View.VISIBLE);
                plus_first_value.setVisibility(View.VISIBLE);
                minus_first_value.setVisibility(View.VISIBLE);
                plus_second_value.setVisibility(View.VISIBLE);
                minus_second_value.setVisibility(View.VISIBLE);
                plus_third_value.setVisibility(View.VISIBLE);
                minus_third_value.setVisibility(View.VISIBLE);
                first_value_edit.setText(convertSeconds(pomValue1));
                second_value_edit.setText(convertSeconds(pomValue2));
                third_value_edit.setText(convertSeconds(pomValue3));
                skip.setVisibility(View.VISIBLE);
                newLap.setVisibility(View.GONE);
                s1.setText(R.string.work_time);
                s2.setText(R.string.small_break);
                s3.setText(R.string.long_break);
                cycle_reset.setText(R.string.clear_cycles);
                params2.width = 150;
                break;
            case 3:
                progressBar.setVisibility(View.INVISIBLE);
                progressBar2.setVisibility(View.INVISIBLE);
                stopWatchView.setVisibility(View.VISIBLE);
                plus_third_value.setVisibility(View.GONE);
                s1.setVisibility(View.GONE);
                s2.setVisibility(View.GONE);
                s3.setVisibility(View.GONE);
                plus_first_value.setVisibility(View.GONE);
                minus_first_value.setVisibility(View.GONE);
                plus_second_value.setVisibility(View.GONE);
                minus_second_value.setVisibility(View.GONE);
                plus_third_value.setVisibility(View.GONE);
                minus_third_value.setVisibility(View.GONE);
                skip.setVisibility(View.GONE);
                newLap.setVisibility(View.VISIBLE);
                cycle_reset.setText(R.string.clear_laps);
                params2.width = 200;
                msTime.setAlpha(1);
                msTimePaused.setAlpha(1);

                timeLeft3.setText(displayTime);
                timePaused3.setText(displayTime);
                msTime.setText(displayMs);
                msTimePaused.setText(displayMs);
        }
        cycle_reset.setLayoutParams(params2);
    }

    public void clearArrays(boolean populateList) {
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
                if (setsArray.size()>0) savedCycleAdapter.notifyDataSetChanged();
            }
        } else {
            if (breaksOnlyArray!=null) breaksOnlyArray.clear();
            if (breaksOnlyTitleArray!=null) breaksOnlyTitleArray.clear();

            if (populateList) {
                for (int i=0; i<breaksOnlyArray.size(); i++) {
                    breaksOnlyArray.add(cyclesBOList.get(i).getBreaksOnly());
                    breaksOnlyTitleArray.add(cyclesBOList.get(i).getTitle());
                }
                if (breaksOnlyArray.size()>0) savedCycleAdapter.notifyDataSetChanged();
            }
        }
    }

    public void queryCycles() {
        if (!breaksOnly) {
            switch (sortMode) {
                case 1:
                    cyclesList = cyclesDatabase.cyclesDao().loadCyclesMostRecent(); sortCheckmark.setY(14); break;
                case 2:
                    cyclesList = cyclesDatabase.cyclesDao().loadCycleLeastRecent(); sortCheckmark.setY(110); break;
                case 3:
                    cyclesList = cyclesDatabase.cyclesDao().loadCyclesMostItems(); sortCheckmark.setY(206); break;
                case 4:
                    cyclesList = cyclesDatabase.cyclesDao().loadCyclesLeastItems(); sortCheckmark.setY(302);  break;
            }
        } else {
            switch (sortModeBO) {
                case 1:
                    cyclesBOList = cyclesDatabase.cyclesDao().loadCyclesMostRecentBO(); sortCheckmark.setY(14); break;
                case 2:
                    cyclesBOList = cyclesDatabase.cyclesDao().loadCycleLeastRecentBO(); sortCheckmark.setY(110); break;
                case 3:
                    cyclesBOList = cyclesDatabase.cyclesDao().loadCyclesMostItemsBO(); sortCheckmark.setY(206); break;
                case 4:
                    cyclesBOList = cyclesDatabase.cyclesDao().loadCyclesLeastItemsBO(); sortCheckmark.setY(302);
            }
        }
    }

    public void resetTimer() {
        removeViews();
        //Todo: Separate end animations, especially since adding stopwatch.
        if (endAnimation != null) endAnimation.cancel();
        switch (mode) {
            case 1:
                add_cycle.setBackgroundColor(getResources().getColor(R.color.light_grey));
                sub_cycle.setBackgroundColor(getResources().getColor(R.color.light_grey));
                add_cycle.setEnabled(true);
                sub_cycle.setEnabled(true);
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
                }
                if (!breaksOnly) {
                    if (startCustomBreakTime.size()>0) {
                        breakMillis = startCustomBreakTime.get(startCustomBreakTime.size()-1);
                        timePaused.setText(convertSeconds((setMillis+999)/1000));
                        timeLeft2.setText(convertSeconds((setMillis+999)/1000));
                    }
                } else {
                    if (startBreaksOnlyTime.size()>0) {
                        breakMillis = startBreaksOnlyTime.get(startBreaksOnlyTime.size()-1);
                        timePaused2.setText(convertSeconds((breakMillis+999)/1000));
                        timeLeft2.setText(convertSeconds((breakMillis+999)/1000));
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
                    dotDraws.breakTime(customBreakTime);
                    third_value_edit.setText(String.valueOf(customSetTime.size()));
                } else {
                    if (startBreaksOnlyTime.size()>0) {
                        breaksOnlyTime.addAll(startBreaksOnlyTime);
                        timerDisabled = false;
                    } else {
                        timerDisabled = true;
                    }
                    dotDraws.breakTime(breaksOnlyTime);
                    third_value_edit.setText(String.valueOf(breaksOnlyTime.size()));
                }
                //Always setting this to avoid null errors.
                dotDraws.setTime(customSetTime);
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
                pomMillis = pomMillis1;
                setNewText(timeLeft2, timeLeft2, (pomMillis + 999)/1000);
                setNewText(timePaused2, timePaused2, (pomMillis + 999)/1000);
                timePaused2.setAlpha(1);
                onBreak = false;
                pomHalted = true;
                pomProgressPause = maxProgress;
                dotDraws.pomDraw(1, 0);
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
    }

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

    //Returns long of total seconds.
    public long convertStringToLong(String time) {
        if (time.length()>=3) {
            String[] timeString = time.split(":");
            return (Long.parseLong(timeString[0]) * 60) + Long.parseLong(timeString[1]);
        } else return Long.parseLong(time);
    }

    public void convertEditTime() {
        editSetSeconds = setValue%60;
        editSetMinutes = setValue/60;
        if (!breaksOnly) {
            editBreakSeconds = breakValue%60;
            editBreakMinutes = breakValue/60;
            first_value_textView.setText(convertSeconds(setValue));
            if (editSetMinutes!=0) first_value_edit.setText(String.valueOf(editSetMinutes)); else first_value_edit.setText(("0"));
            if (editSetSeconds!=0) first_value_edit_two.setText(String.valueOf(editSetSeconds)); else first_value_edit_two.setText("00");
        } else {
            editBreakSeconds = breaksOnlyValue%60;
            editBreakMinutes = breaksOnlyValue/60;
        }

        second_value_textView.setText(convertSeconds(breakValue));
        if (editBreakMinutes!=0) second_value_edit.setText(String.valueOf(editBreakMinutes)); else second_value_edit.setText(("0"));
        if (editBreakSeconds!=0) second_value_edit_two.setText(String.valueOf(editBreakSeconds)); else second_value_edit_two.setText("00");
    }

    //Todo: This is acting separately from changeFirst/changeSecond methods.
    public void capEditNumber(EditText editText, int cap) {
        editText.addTextChangedListener(new TextWatcher() {
            String oldValue = "";
            int val = 0;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (!first_value_edit.getText().toString().equals("")) {
                    String newValue = first_value_edit.getText().toString();
                    if (!oldValue.equals(newValue)) {
                        oldValue = newValue;
                        val = Integer.parseInt(newValue);
                        if (val>cap) val = cap;
                        editText.setText(String.valueOf(val));
                    }
                }

                setValue = (editSetMinutes * 60) + editSetSeconds;
                breakValue = (editBreakMinutes * 60) + editBreakSeconds;
                breaksOnlyValue = (editBreakMinutes * 60) + editBreakSeconds;
            }
        });
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

    public void canSaveOrUpdate(boolean yesWeCan) {
        if (!breaksOnly) {
            canSaveOrUpdateCustom = yesWeCan;
        } else {
            canSaveOrUpdateBreaksOnly = yesWeCan;
        }
        existingCycle = !yesWeCan;
        if ( (!breaksOnly && canSaveOrUpdateCustom) || (breaksOnly && canSaveOrUpdateBreaksOnly) ) {
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
                AsyncTask.execute(() -> {
                    cyclesList = cyclesDatabase.cyclesDao().loadCyclesMostRecent();
                    clearArrays(true);
                    sortMode = 1;
                    sortModeBO = 1;
                });
            });

            sortNotRecent.setOnClickListener(v2 ->{
                sortCheckmark.setY(110);
                AsyncTask.execute(() -> {
                    cyclesList = cyclesDatabase.cyclesDao().loadCycleLeastRecent();
                    clearArrays(true);
                    sortMode = 2;
                    sortModeBO = 2;
                });
            });

            sortHigh.setOnClickListener(v3 -> {
                sortCheckmark.setY(206);
                AsyncTask.execute(() -> {
                    cyclesList = cyclesDatabase.cyclesDao().loadCyclesMostItems();
                    clearArrays(true);
                    sortMode = 3;
                    sortModeBO = 3;
                });
            });

            sortLow.setOnClickListener(v4 -> {
                sortCheckmark.setY(302);
                AsyncTask.execute(() -> {
                    cyclesList = cyclesDatabase.cyclesDao().loadCyclesLeastItems();
                    clearArrays(true);
                    sortMode = 4;
                    sortModeBO = 4;
                });
            });
            sortCheckmark.setY(0);
            prefEdit.putInt("sortMode", sortMode);
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
        if ((!breaksOnly && startCustomSetTime.size()==0) || (breaksOnly && startBreaksOnlyTime.size()==0)) {
            Toast.makeText(getApplicationContext(), "Nothing to save!", Toast.LENGTH_SHORT).show();;
            return;
        }
        AsyncTask.execute(() -> {
            //Defaulting to unique cycle unless otherwise set by retrieveAndSetCycles();
            duplicateCycle = false;
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMMM d yyyy - hh:mma", Locale.getDefault());

            if (!breaksOnly) {
                //Executes gSon->Json conversion and compares new String to db lists, setting duplicateCycles to TRUE if an instance matches.
                retrieveAndCheckCycles();
                //New instance of the Cycle entity that can be used for insertion. Otherwise, inheriting the instance from onCycleClick callback that uses a specific position to update.
                if (saveOrUpdate == SAVING_CYCLES) cycles = new Cycles(); else cycles = cyclesList.get(cyclesList.size()-1);

                if (!cycle_header_text.getText().toString().isEmpty()) {
                    cycles.setTitle(cycle_header_text.getText().toString());
                } else {
                    String newDate = dateFormat.format(calendar.getTime());
                    cycles.setTitle(newDate);
                }
                customTitleArray.add(cycle_header_text.getText().toString());

                if (duplicateCycle) {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "An identical cycle already exists!", Toast.LENGTH_SHORT).show();
                    });
                }
                if (!duplicateCycle || cyclesList.size()==0) {
                    cycles.setSets(convertedSetList);
                    cycles.setBreaks(convertedBreakList);
                    cycles.setTimeAdded(System.currentTimeMillis());
                    cycles.setItemCount(startCustomSetTime.size());
                    if (saveOrUpdate == SAVING_CYCLES){
                        cyclesDatabase.cyclesDao().insertCycle(cycles);
                        //Re-instantiating cycleList with new row added.
                        queryCycles();
                        //Latest row entry.
                        int lastPos = cyclesList.size()-1;
                        //Getting ID of latest row entry.
                        customID = cyclesList.get(lastPos).getId();
                        //Saving ID to sharedPref.
                        prefEdit.putInt("customID", customID);
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "Cycle added", Toast.LENGTH_SHORT).show();
                        });

                    } else {
                        cyclesDatabase.cyclesDao().updateCycles(cycles);
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "Cycle updated", Toast.LENGTH_SHORT).show();
                        });
                    }
                    runOnUiThread(() -> {
                        if (labelSavePopupWindow!=null) labelSavePopupWindow.dismiss();
                        canSaveOrUpdate(false);
                    });
                }
            } else {
                //New instance of the CycleBO entity that can be used for insertion. Otherwise, inheriting the instance from onCycleClick callback that uses a specific position to update.
                if (saveOrUpdate == SAVING_CYCLES) cyclesBO = new CyclesBO();

                if (!cycle_header_text.getText().toString().isEmpty()) {
                    cyclesBO.setTitle(cycle_header_text.getText().toString());
                } else {
                    String newDate = dateFormat.format(calendar.getTime());
                    cyclesBO.setTitle(newDate);
                }
                breaksOnlyTitleArray.add(cycle_header_text.getText().toString());

                if (duplicateCycle) {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "An identical cycle already exists!", Toast.LENGTH_SHORT).show();
                    });
                }

                if (!duplicateCycle || cyclesBOList.size()==0) {
                    cyclesBO.setBreaksOnly(convertedBreakOnlyList);
                    cyclesBO.setTimeAdded(System.currentTimeMillis());
                    cyclesBO.setItemCount(startBreaksOnlyTime.size());
                    if (saveOrUpdate == SAVING_CYCLES) {
                        cyclesDatabase.cyclesDao().insertBOCycle(cyclesBO);
                        queryCycles();
                        int lastPos = cyclesBOList.size()-1;
                        breaksOnlyID = cyclesBOList.get(lastPos).getId();
                        prefEdit.putInt("breaksOnlyID", breaksOnlyID);
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "Cycle added", Toast.LENGTH_SHORT).show();
                        });
                    } else {;
                        cyclesDatabase.cyclesDao().updateBOCycles(cyclesBO);
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "Cycle updated", Toast.LENGTH_SHORT).show();
                        });
                    }
                    runOnUiThread(() -> {
                        if (labelSavePopupWindow!=null) labelSavePopupWindow.dismiss();
                        canSaveOrUpdate(false);
                    });
                }
            }
            prefEdit.apply();
        });
        cycle_header_text.setText(cycle_header_text.getText().toString());
        //Todo: We may revisit this.
        existingCycle = false;
    }

    //Called when a saved cycle is clicked on.
    public void setCycle(int position) {
        AsyncTask.execute(() -> {
            int posHolder = position;

            //Todo: This needs to not trigger on app start, but it is also used via canSaveOrUpdate() to grey out Save/Update buttons, so use caution.
            //Used in save_cycles button to update our existing row instead of creating a new one.
            existingCycle = true;

            if (!breaksOnly) {
                //Getting a full list of cycles if app is NOT launching for first time, otherwise a single row is retrieved before this method is executed.
                if (!appLaunchingCustom) {
                    queryCycles();
                    appLaunchingCustom = false;
                }

//                customID = cyclesList.get(posHolder).getId();
                //Getting instance of selected position of Cycle list entity. Also used in save_cycles.
                cycles = cyclesList.get(posHolder);
                String tempSets = cyclesList.get(posHolder).getSets();
                String tempBreaks = cyclesList.get(posHolder).getBreaks();
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
                customID = cyclesList.get(posHolder).getId();
                prefEdit.putInt("customID", customID);
                Log.i("idcheck", "current id is " + customID);
            } else {
                if (!appLaunchingBO) {
                    queryCycles();
                    appLaunchingBO = false;
                }
//                breaksOnlyID = cyclesBOList.get(posHolder).getId();
                //Getting instance of selected position of CycleBO list entity. Also used in save_cycles.
                cyclesBO = cyclesBOList.get(posHolder);
                String tempBreaksOnly = cyclesBOList.get(posHolder).getBreaksOnly();
                String[] breaksOnlySplit = tempBreaksOnly.split(" - ", 0);

                startBreaksOnlyTime.clear();
                breaksOnlyTime.clear();

                for (int i=0; i<breaksOnlySplit.length; i++) {
                    breaksOnlyTime.add(Long.parseLong(breaksOnlySplit[i])*1000);
                    startBreaksOnlyTime.add(Long.parseLong(breaksOnlySplit[i])*1000);
                }
                breaksOnlyID = cyclesBOList.get(posHolder).getId();
                prefEdit.putInt("breaksOnlyID", breaksOnlyID);
            }
            runOnUiThread(() -> {
                if (!breaksOnly) cycle_header_text.setText(cycles.getTitle()); else cycle_header_text.setText(cyclesBO.getTitle());
                resetTimer();
                savedCyclePopupWindow.dismiss();
            });
            prefEdit.commit();
        });
    }

    private void retrieveAndCheckCycles() {
        //This retrieves cycleList instance.
        queryCycles();

        Gson gson = new Gson();
        ArrayList<Long> tempSets = new ArrayList<>();
        ArrayList<Long> tempBreaks = new ArrayList<>();
        ArrayList<Long> tempBreaksOnly = new ArrayList<>();

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

            if (cyclesList.size()>0) {
                for (int i=0; i<cyclesList.size(); i++) {
                    if (cyclesList.get(i).getSets().equals(convertedSetList) && cyclesList.get(i).getBreaks().equals(convertedBreakList) && cyclesList.get(i).getTitle().equals(cycle_header_text.getText().toString())) duplicateCycle = true;
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
                    if (cyclesBOList.get(i).getBreaksOnly().equals(convertedBreakOnlyList) && cyclesBOList.get(i).getTitle().equals(cycle_header_text.getText().toString())) duplicateCycle = true;
                }
            }
        }
    }

    public void setDefaultCustomCycle() {
        if (!breaksOnly) {
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
        cycle_header_text.setText(R.string.default_title);
    }

    public void setDefaultBOCycle() {
        for (int i = 0; i < 3; i++) {
            breaksOnlyTime.add((long) 30 * 1000);
            startBreaksOnlyTime.add((long) 30 * 1000);
        }
        breaksOnlyValue = 30;
        cycle_header_text.setText(R.string.default_title);
    }
}