package com.example.tragic.irate.simple.stopwatch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tragic.irate.simple.stopwatch.Database.Cycles;
import com.example.tragic.irate.simple.stopwatch.Database.CyclesBO;
import com.example.tragic.irate.simple.stopwatch.Database.CyclesDatabase;
import com.example.tragic.irate.simple.stopwatch.Database.PomCycles;
import com.google.android.material.tabs.TabLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"depreciation"})
public class MainActivity extends AppCompatActivity implements SavedCycleAdapter.onCycleClickListener, SavedCycleAdapter.onDeleteCycleListener {

  ConstraintLayout cl;
  SharedPreferences sharedPreferences;
  SharedPreferences.Editor prefEdit;
  boolean defaultMenu = true;
  View mainView;
  TextView save_cycles;
  TextView update_cycles;

  DotDraws dotDraws;
  ImageButton fab;
  EditText edit_header;
  Button confirm_header_save;
  Button cancel_header_save;

  ImageView sortCheckmark;

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

  CycleRoundsAdapter cycleRoundsAdapter;
  RecyclerView savedCycleRecycler;
  SavedCycleAdapter savedCycleAdapter;
  View deleteCyclePopupView;
  View sortCyclePopupView;
  View savedCyclePopupView;
  View cycleLabelView;
  View editCyclesPopupView;

  PopupWindow sortPopupWindow;
  PopupWindow savedCyclePopupWindow;
  PopupWindow labelSavePopupWindow;
  PopupWindow deleteAllPopupWindow;
  PopupWindow editCyclesPopupWindow;

  TextView sortRecent;
  TextView sortNotRecent;
  TextView sortHigh;
  TextView sortLow;
  TextView delete_all_text;
  Button delete_all_confirm;
  Button delete_all_cancel;

  int mode = 1;
  int sortMode = 1;
  int sortModeBO = 1;
  int sortModePom = 1;
  int receivedPos;
  int customID;
  int breaksOnlyID;
  int pomID;

  TextView s1;
  TextView s2;
  TextView s3;
  EditText first_value_edit;
  EditText first_value_edit_two;
  TextView first_value_sep;
  TextView first_value_textView;
  EditText second_value_edit;
  EditText second_value_edit_two;
  TextView second_value_sep;
  TextView second_value_textView;
  EditText third_value_edit;
  EditText third_value_edit_two;
  TextView third_value_sep;
  TextView third_value_textView;
  ImageView plus_first_value;
  ImageView minus_first_value;
  ImageView plus_second_value;
  ImageView minus_second_value;
  ImageButton plus_third_value;
  ImageButton minus_third_value;
  Button add_cycle;
  Button sub_cycle;
  ImageButton upDown_arrow_one;
  ImageButton upDown_arrow_two;
  View top_anchor;
//  ImageView infinity_one;
//  ImageView infinity_two;
  boolean infinity_mode_one;
  boolean infinity_mode_two;
  Button start_timer;

  ArrayList<Integer> customSetTime;
  ArrayList<Integer> customBreakTime;
  ArrayList<Integer> breaksOnlyTime;
  ArrayList<Integer> pomValuesTime;
  ArrayList<Integer> customSetTimeUP;
  ArrayList<Integer> customBreakTimeUP;
  ArrayList<Integer> breaksOnlyTimeUP;
  ArrayList<String> convertedSetsList;
  ArrayList<String> convertedBreaksList;
  ArrayList<String> convertedBreaksOnlyList;

  int setValue;
  int breakValue;
  int breaksOnlyValue;
  int pomValue1;
  int pomValue2;
  int pomValue3;
  //Edits must be longs because we divide w/ remainders on them.
  long editSetSeconds;
  long editSetMinutes;
  long editBreakMinutes;
  long editBreakSeconds;
  long editPomMinutesOne;
  long editPomSecondsOne;
  long editPomMinutesTwo;
  long editPomSecondsTwo;
  long editPomMinutesThree;
  long editPomSecondsThree;
  boolean incrementValues;
  int incrementTimer = 10;
  boolean minReached;
  boolean maxReached;

  Handler mHandler;
  Runnable valueSpeed;
  Runnable changeFirstValue;
  Runnable changeSecondValue;
  Runnable changeThirdValue;
  boolean editListener;

  boolean setsAreCountingUp;
  boolean breaksAreCountingUp;
  boolean breaksOnlyAreCountingUp;
  int COUNTING_DOWN = 1;
  int COUNTING_UP = 2;


  //Todo: Use infinite symbol in adapter list instead and keep timer values visible. Toggle will switch from infinite to whichever count down values we have.
  //Todo: Two digits in MM of add/sub slightly overlap ":" due to larger textViews.

  //Todo: Preset timer selections.
  //Todo: Database saves for count up mode.
  //Todo: Save completed cycles in sharedPref? If so, remember in nextCountUpRound() as well.
  //Todo: No rounds added defaults to a default Cycle instead of staying blank.
  //Todo: TDEE in sep popup w/ tabs.
  //Todo: Variable set count-up timer, for use w/ TDEE.
  //Todo: Variable set only mode? Again, for TDEE.
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

  @Override
  public void onCycleDelete(int position) {

  }

  @Override
  public void onCycleClick(int position) {

  }

//    @Override
//    public void onCycleDelete(int position) {
//        AsyncTask.execute(()->{
//            //Initial query, applying to all retrievals.
//            queryCycles();
//            switch (mode) {
//                case 1:
//                    Cycles removedCycle = cyclesList.get(position);
//                    cyclesDatabase.cyclesDao().deleteCycle(removedCycle);
//                    //Second query, retrieving the new, post-modified entity.
//                    queryCycles();
//
//                    runOnUiThread(() -> {
//                        setsArray.clear();
//                        breaksArray.clear();
//                        for (int i=0; i<cyclesList.size(); i++) {
//                            setsArray.add(cyclesList.get(i).getSets());
//                            breaksArray.add(cyclesList.get(i).getBreaks());
//                            customTitleArray.add(cyclesList.get(i).getTitle());
//                        }
//                        savedCycleAdapter.notifyDataSetChanged();
//                    });
//                    break;
//                case 2:
//                    CyclesBO removedBOCycle = cyclesBOList.get(position);
//                    cyclesDatabase.cyclesDao().deleteBOCycle(removedBOCycle);
//                    queryCycles();
//
//                    runOnUiThread(() -> {
//                        breaksOnlyArray.clear();
//                        for (int i=0; i<cyclesBOList.size(); i++) {
//                            breaksOnlyArray.add(cyclesBOList.get(i).getBreaksOnly());
//                            breaksOnlyTitleArray.add(cyclesList.get(i).getTitle());
//                        }
//                        savedCycleAdapter.notifyDataSetChanged();
//                    });
//                    break;
//                case 3:
//                    int deletedID = pomCyclesList.get(position).getId();
//
//                    PomCycles removedPom = pomCyclesList.get(position);
//                    cyclesDatabase.cyclesDao().deletePomCycle(removedPom);
//                    queryCycles();
//
//                    runOnUiThread(()-> {
//                        pomArray.remove(position);
//                        savedCycleAdapter.notifyDataSetChanged();
//                    });
//                    break;
//            }
//            saveArrays();
//        });
//        Toast.makeText(getApplicationContext(), "Cycle deleted!", Toast.LENGTH_SHORT).show();
//    }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    return false;
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
    return true;
  }

//    @SuppressLint("NonConstantResourceId")
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
////        if (savedCyclePopupWindow!=null &&savedCyclePopupWindow.isShowing()) return false;
//        AtomicBoolean cyclesExist = new AtomicBoolean(false);
//        switch (item.getItemId()) {
//            case R.id.saved_cycle_list:
//                AsyncTask.execute(() -> {
//                    queryCycles();
//                    clearArrays(false);
//                    switch (mode) {
//                        case 1:
//                            savedCycleAdapter.setView(1);
//                            if (cyclesList.size()>0) {
//                                for (int i=0; i<cyclesList.size(); i++) {
//                                    setsArray.add(cyclesList.get(i).getSets());
//                                    breaksArray.add(cyclesList.get(i).getBreaks());
//                                    customTitleArray.add(cyclesList.get(i).getTitle());
//                                }
//                                cyclesExist.set(true);
//                            }
//                            break;
//                        case 2:
//                            savedCycleAdapter.setView(2);
//                            if (cyclesBOList.size()>0) {
//                                for (int i=0; i<cyclesBOList.size(); i++) {
//                                    breaksOnlyTitleArray.add(cyclesBOList.get(i).getTitle());
//                                    breaksOnlyArray.add(cyclesBOList.get(i).getBreaksOnly());
//                                }
//                                cyclesExist.set(true);
//                            }
//                            break;
//                        case 3:
//                            savedCycleAdapter.setView(3);
//                            if (pomCyclesList.size()>0) {
//                                for (int i=0; i<pomCyclesList.size(); i++) {
//                                    pomArray.add(pomCyclesList.get(i).getFullCycle());
//                                    pomCyclesTitleArray.add(pomCyclesList.get(i).getTitle());
//                                }
//                                cyclesExist.set(true);
//                            }
//                            break;
//                    }
//
//                    runOnUiThread(()-> {
//                        if (cyclesExist.get()) {
//                            save_cycles.setText(R.string.sort_cycles);
//                            //Focusable must be false for save/sort switch function to work, otherwise window will steal focus from button.
//                            savedCyclePopupWindow.showAtLocation(mainView, Gravity.CENTER, 0, 100);
//                            savedCycleAdapter.notifyDataSetChanged();
//                            switchMenu();
//                        } else {
//                            Toast.makeText(getApplicationContext(), "Nothing saved!", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                });
//                break;
//
//            case R.id.delete_all_cycles:
//                AsyncTask.execute(() -> {
//                    queryCycles();
//                    if ((mode==1 && cyclesList.size()==0)|| (mode==2 && cyclesBOList.size()==0)){
//                        runOnUiThread(()-> {
//                            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Nothing saved!", Toast.LENGTH_SHORT).show());
//                        });
//                    } else {
//                        runOnUiThread(()-> {
//                            if (mode==1) delete_all_text.setText(R.string.delete_all_custom); else delete_all_text.setText(R.string.delete_all_BO);
//                            deleteAllPopupWindow.showAtLocation(deleteCyclePopupView, Gravity.CENTER, 0, 0);
//                        });
//                        ;
//                    }
//                });
//        }
//        return super.onOptionsItemSelected(item);
//    }

  @SuppressLint({"UseCompatLoadingForDrawables", "ClickableViewAccessibility", "CommitPrefEdits", "CutPasteId"})
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mainView = findViewById(R.id.main_layout);
    AsyncTask.execute(() -> cyclesDatabase = CyclesDatabase.getDatabase(getApplicationContext()));

    TabLayout tabLayout = findViewById(R.id.tabLayout);
    tabLayout.addTab(tabLayout.newTab().setText("Sets+"));
    tabLayout.addTab(tabLayout.newTab().setText("Breaks"));
    tabLayout.addTab(tabLayout.newTab().setText("Pomodoro"));
    tabLayout.addTab(tabLayout.newTab().setText("Stopwatch"));

    fab = findViewById(R.id.fab);
    savedCycleRecycler = findViewById(R.id.cycle_list_recycler);
//        LinearLayoutManager lm2 = new LinearLayoutManager(getApplicationContext());
//        savedCycleAdapter = new SavedCycleAdapter();
//        savedCycleRecycler.setAdapter(savedCycleAdapter);
//        savedCycleRecycler.setLayoutManager(lm2);
//        savedCycleAdapter.setItemClick(MainActivity.this);
//        savedCycleAdapter.setDeleteCycle(MainActivity.this);

    LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    savedCyclePopupView = inflater.inflate(R.layout.saved_cycles_layout, null);
    deleteCyclePopupView = inflater.inflate(R.layout.delete_cycles_popup, null);
    sortCyclePopupView = inflater.inflate(R.layout.sort_popup, null);
    cycleLabelView = inflater.inflate(R.layout.label_cycle_popup, null);
    editCyclesPopupView = inflater.inflate(R.layout.editing_cycles, null);

    savedCyclePopupWindow = new PopupWindow(savedCyclePopupView, 800, 1200, true);
    deleteAllPopupWindow = new PopupWindow(deleteCyclePopupView, 750, 375, true);
    labelSavePopupWindow = new PopupWindow(cycleLabelView, 800, 400, true);
    sortPopupWindow = new PopupWindow(sortCyclePopupView, 400, 375, true);
    editCyclesPopupWindow = new PopupWindow(editCyclesPopupView, WindowManager.LayoutParams.MATCH_PARENT, 1430, true);
    savedCyclePopupWindow.setAnimationStyle(R.style.WindowAnimation);
    deleteAllPopupWindow.setAnimationStyle(R.style.WindowAnimation);
    labelSavePopupWindow.setAnimationStyle(R.style.WindowAnimation);
    sortPopupWindow.setAnimationStyle(R.style.WindowAnimation);
    editCyclesPopupWindow.setAnimationStyle(R.style.WindowAnimation);

    cl = findViewById(R.id.main_layout);
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
    third_value_edit = editCyclesPopupView.findViewById(R.id.third_value_edit);
    third_value_edit_two = editCyclesPopupView.findViewById(R.id.third_value_edit_two);
    third_value_sep = editCyclesPopupView.findViewById(R.id.third_value_sep);
    add_cycle = editCyclesPopupView.findViewById(R.id.add_cycle);
    sub_cycle = editCyclesPopupView.findViewById(R.id.subtract_cycle);
    upDown_arrow_one = editCyclesPopupView.findViewById(R.id.s1_up);
    upDown_arrow_two = editCyclesPopupView.findViewById(R.id.s2_up);
    top_anchor = editCyclesPopupView.findViewById(R.id.top_anchor);
//    infinity_one = editCyclesPopupView.findViewById(R.id.infinity_one);
//    infinity_two = editCyclesPopupView.findViewById(R.id.infinity_two);
    start_timer = editCyclesPopupView.findViewById(R.id.start_timer);

    sortRecent = sortCyclePopupView.findViewById(R.id.sort_most_recent);
    sortNotRecent = sortCyclePopupView.findViewById(R.id.sort_least_recent);
    sortHigh = sortCyclePopupView.findViewById(R.id.sort_number_high);
    sortLow = sortCyclePopupView.findViewById(R.id.sort_number_low);
    sortCheckmark = sortCyclePopupView.findViewById(R.id.sortCheckmark);
//    infinity_one.setVisibility(View.INVISIBLE);
//    infinity_two.setVisibility(View.INVISIBLE);

    sortPopupWindow = new PopupWindow(sortCyclePopupView, 400, 375, true);

    getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    getSupportActionBar().setDisplayShowCustomEnabled(true);
    getSupportActionBar().setCustomView(R.layout.custom_bar);

    customSetTime = new ArrayList<>();
    customBreakTime = new ArrayList<>();
    breaksOnlyTime = new ArrayList<>();
    customSetTimeUP = new ArrayList<>();
    customBreakTimeUP = new ArrayList<>();
    breaksOnlyTimeUP = new ArrayList<>();
    pomValuesTime = new ArrayList<>();
    convertedSetsList = new ArrayList<>();
    convertedBreaksList = new ArrayList<>();
    convertedBreaksOnlyList = new ArrayList<>();

    //Database entity lists.
    cyclesList = new ArrayList<>();
    cyclesBOList = new ArrayList<>();
    pomCyclesList = new ArrayList<>();
    cycles = new Cycles();
    cyclesBO = new CyclesBO();
    pomCycles = new PomCycles();

    mHandler = new Handler();
    sharedPreferences = getApplicationContext().getSharedPreferences("pref", 0);
    prefEdit = sharedPreferences.edit();

    setValue = sharedPreferences.getInt("setValue", 30);
    breakValue = sharedPreferences.getInt("breakValue", 30);
    breaksOnlyValue = sharedPreferences.getInt("breakOnlyValue", 30);
    pomValue1 = sharedPreferences.getInt("pomValue1", 1500);
    pomValue2 = sharedPreferences.getInt("pomValue2", 300);
    pomValue3 = sharedPreferences.getInt("pomValue3", 900);
    sortMode = sharedPreferences.getInt("sortMode", 1);
    sortModeBO = sharedPreferences.getInt("sortModeBO", 1);
    sortModePom = sharedPreferences.getInt("sortModePom", 1);
    customID = sharedPreferences.getInt("customID", 0);
    breaksOnlyID = sharedPreferences.getInt("breaksOnlyID", 0);
    pomID = sharedPreferences.getInt("pomID", 0);

    setsAreCountingUp = !sharedPreferences.getBoolean("setCountUpMode", false);
    breaksAreCountingUp = !sharedPreferences.getBoolean("breakCountUpMode", false);

    //Sets all editTexts to GONE, and then populates them + textViews based on mode.
    removeEditViews();
    editCycleViews();
    convertEditTime(true);
    setEditValues();

    TextWatcher textWatcher = new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }
      @Override
      public void afterTextChanged(Editable s) {
        //We set editListener to FALSE to prevent setEditValues() from triggering when not desired. Right now, it's when we are using the +/- runnables to move our time.
        if (editListener) setEditValues();
        Log.i("testWatch", "boolean is " + editListener);
      }
    };
    first_value_edit.addTextChangedListener(textWatcher);
    first_value_edit_two.addTextChangedListener(textWatcher);
    second_value_edit.addTextChangedListener(textWatcher);
    second_value_edit_two.addTextChangedListener(textWatcher);
    third_value_edit.addTextChangedListener(textWatcher);
    third_value_edit_two.addTextChangedListener(textWatcher);

    LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext());
    RecyclerView roundRecycler = editCyclesPopupView.findViewById(R.id.round_list_recycler);
    cycleRoundsAdapter = new CycleRoundsAdapter(convertedSetsList, convertedBreaksList, convertedBreaksOnlyList);
    roundRecycler.setAdapter(cycleRoundsAdapter);
    roundRecycler.setLayoutManager(lm);

    countUpMode(true); countUpMode(false);

    fab.setOnClickListener(v -> {
      editCyclesPopupWindow.showAsDropDown(tabLayout);
    });

    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
      @Override
      public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
          case 0:
            mode = 1;
//                        savedCycleAdapter.setView(1);
            break;
          case 1:
            mode = 2;
//                        savedCycleAdapter.setView(2);
            break;
          case 2:
            mode = 3;
//                        savedCycleAdapter.setView(3);
            break;
          case 3:
            mode = 4;
            break;
        }
        //Sets all editTexts to GONE, and then populates them + textViews based on mode.
        removeEditViews();
        editCycleViews();
      }

      @Override
      public void onTabUnselected(TabLayout.Tab tab) {
        //Clears out any active editViews when switching to a new tab.
        switch (tab.getPosition()) {
          case 0:
            break;
          case 1:
            break;
          case 2:
            break;
          case 3:
            break;
        }
      }

      @Override
      public void onTabReselected(TabLayout.Tab tab) {
      }
    });

    //Todo: Triggers textView show on infinity mode.
    //Dismisses editText views if we click within the unpopulated area of popUp.
    editCyclesPopupView.setOnClickListener(v-> {
      removeEditViews();
      editCycleViews();
    });

    upDown_arrow_one.setImageResource(R.drawable.arrow_down);
    upDown_arrow_one.setTag(1);
    upDown_arrow_two.setImageResource(R.drawable.arrow_down);
    upDown_arrow_two.setTag(1);

    upDown_arrow_one.setOnClickListener(v -> {
      countUpMode(true);
    });

    upDown_arrow_two.setOnClickListener(v -> {
      countUpMode(false);
    });

    first_value_textView.setOnClickListener(v -> {
      editAndTextSwitch(true, 1);
    });

    second_value_textView.setOnClickListener(v -> {
      editAndTextSwitch(true, 2);
    });

    third_value_textView.setOnClickListener(v -> {
      editAndTextSwitch(true, 3);
    });

    changeFirstValue = new Runnable() {
      @Override
      public void run() {
        switch (mode) {
          case 1:
            if (incrementValues) setValue += 1; else setValue -= 1;
            prefEdit.putInt("setValue", setValue);
            break;
          case 3:
            if (incrementValues) pomValue1 += 5;
            else pomValue1 -= 5;
            prefEdit.putInt("pomValue1", pomValue1);
            break;
        }
        mHandler.postDelayed(this, incrementTimer * 10);
        setTimerValueBounds();
        fadeCap(first_value_textView);
        editAndTextSwitch(false, 1);
        prefEdit.apply();
        Log.i("testRun", "set value is " + setValue);
      }
    };

    changeSecondValue = new Runnable() {
      @Override
      public void run() {
        switch (mode) {
          case 1:
            if (incrementValues) breakValue += 1;
            else breakValue -= 1;
            prefEdit.putInt("breakValue", breakValue);
            break;
          case 2:
            if (incrementValues) breaksOnlyValue += 1;
            else breaksOnlyValue -= 1;
            prefEdit.putInt("breakOnlyValue", breaksOnlyValue);
            break;
          case 3:
            if (incrementValues) pomValue2 += 5;
            else pomValue2 -= 5;
            prefEdit.putInt("pomValue2", pomValue2);
            break;
        }
        mHandler.postDelayed(this, incrementTimer * 10);
        setTimerValueBounds();
        fadeCap(second_value_textView);
        editAndTextSwitch(false, 2);
        prefEdit.apply();
      }
    };

    changeThirdValue = new Runnable() {
      @Override
      public void run() {
        if (incrementValues) pomValue3 += 5;
        else pomValue3 -= 5;
        mHandler.postDelayed(this, incrementTimer*10);
        setTimerValueBounds();
        fadeCap(third_value_textView);
        editAndTextSwitch(false, 3);
        prefEdit.putInt("pomValue3", pomValue3);
        prefEdit.apply();
      }
    };

    valueSpeed = new Runnable() {
      @Override
      public void run() {
        if (incrementTimer > 1) incrementTimer -= 1;
        mHandler.postDelayed(this, 300);
      }
    };

    plus_first_value.setOnTouchListener((v, event) -> {
      incrementValues = true;
      setIncrements(event, changeFirstValue);
      convertEditTime(true);
      switch (mode) {
        case 1:
          first_value_textView.setText(convertCustomTextView(setValue));
          break;
        case 3:
          first_value_textView.setText(convertCustomTextView(pomValue1));
          break;
      }
      return true;
    });

    minus_first_value.setOnTouchListener((v, event) -> {
      incrementValues = false;
      setIncrements(event, changeFirstValue);
      convertEditTime(true);
      switch (mode) {
        case 1:
          first_value_textView.setText(convertCustomTextView(setValue));
          break;
        case 3:
          first_value_textView.setText(convertCustomTextView(pomValue1));
          break;
      }
      return true;
    });

    plus_second_value.setOnTouchListener((v, event) -> {
      incrementValues = true;
      setIncrements(event, changeSecondValue);
      convertEditTime(true);
      switch (mode) {
        case 1:
          second_value_textView.setText(convertCustomTextView(breakValue));
          break;
        case 2:
          second_value_textView.setText(convertCustomTextView(breaksOnlyValue));
          break;
        case 3:
          second_value_textView.setText(convertCustomTextView(pomValue2));
          break;
      }
      return true;
    });

    minus_second_value.setOnTouchListener((v, event) -> {
      incrementValues = false;
      setIncrements(event, changeSecondValue);
      convertEditTime(true);
      switch (mode) {
        case 1:
          second_value_textView.setText(convertCustomTextView(breakValue));
          break;
        case 2:
          second_value_textView.setText(convertCustomTextView(breaksOnlyValue));
          break;
        case 3:
          second_value_textView.setText(convertCustomTextView(pomValue2));
          break;
      }
      return true;
    });

    plus_third_value.setOnTouchListener((v, event) -> {
      incrementValues = true;
      setIncrements(event, changeThirdValue);
      convertEditTime(true);
      third_value_textView.setText(convertCustomTextView(pomValue3));
      return true;
    });

    minus_third_value.setOnTouchListener((v, event) -> {
      incrementValues = false;
      setIncrements(event, changeThirdValue);
      convertEditTime(true);
      third_value_textView.setText(convertCustomTextView(pomValue3));
      return true;
    });


    add_cycle.setOnClickListener(v -> {
      adjustCustom(true);
    });

    sub_cycle.setOnClickListener(v -> {
      adjustCustom(false);

    });

    start_timer.setOnClickListener(v-> {
      boolean emptyCycle = false;
      Intent intent = new Intent(MainActivity.this, TimerInterface.class);
      intent.putExtra("mode", mode);
      switch (mode) {
        case 1:
          if (!setsAreCountingUp) intent.putIntegerArrayListExtra("setList", customSetTime); else {
            intent.putIntegerArrayListExtra("setList", customSetTimeUP);
            intent.putExtra("setsAreCountingUp", true);
          }
          if (!breaksAreCountingUp) intent.putIntegerArrayListExtra("breakList", customBreakTime); else {
            intent.putIntegerArrayListExtra("breakList", customBreakTimeUP);
            intent.putExtra("breaksAreCountingUp", true);
          }
          if (customSetTime.size()==0) emptyCycle = true;
          break;
        case 2:
          if (!breaksAreCountingUp) intent.putIntegerArrayListExtra("breakOnlyList", breaksOnlyTime); else {
            intent.putIntegerArrayListExtra("breakOnlyList", breaksOnlyTimeUP);
            intent.putExtra("breaksOnlyAreCountingUp", true);
          }
          if (breaksOnlyTime.size()==0) emptyCycle = true;
          break;
        case 3:
          intent.putIntegerArrayListExtra("pomList", pomValuesTime);
          if (pomValuesTime.size()==0) emptyCycle = true;
          break;
      }
      if (!emptyCycle) startActivity(intent); else Toast.makeText(getApplicationContext(), "Cycle cannot be empty!", Toast.LENGTH_SHORT).show();
    });
  }

  //Calls runnables to change set, break and pom values. Sets a handler to increase change rate based on click length. Sets min/max values.
  public void setIncrements(MotionEvent event, Runnable runnable) {
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        //Handler must not be instantiated before this, otherwise the runnable will execute it on every touch (i.e. even on "action_up" removal.
        mHandler.postDelayed(runnable, 25);
        mHandler.postDelayed(valueSpeed, 25);
        break;
      case MotionEvent.ACTION_UP:
        mHandler.removeCallbacksAndMessages(null);
        incrementTimer = 10;
    }
  }

  //Passes current editText values into variables, and then sets min/max bounds on them.
  public void setEditValues() {
    if (mode==1 || mode==2) {
      editSetMinutes = convertEditTextToLong(first_value_edit);
      editSetSeconds = convertEditTextToLong(first_value_edit_two);
      if (editSetSeconds > 59) {
        editSetMinutes += 1;
        editSetSeconds = editSetSeconds - 60;
      }
      if (editSetMinutes >= 5) editSetMinutes = 5;
      if (editSetMinutes <= 0) editSetMinutes = 0;
      if (editSetSeconds <= 0 || editSetMinutes == 5) editSetSeconds = 0;
      if (editSetSeconds < 5 && editSetMinutes == 0) editSetSeconds = 0;
      setValue = (int) ((editSetMinutes * 60) + editSetSeconds);
      first_value_textView.setText(convertCustomTextView(setValue));

      editBreakMinutes = convertEditTextToLong(second_value_edit);
      editBreakSeconds = convertEditTextToLong(second_value_edit_two);
      if (editBreakSeconds > 59) {
        editBreakMinutes += 1;
        editBreakSeconds = editBreakSeconds - 60;
      }
      if (editBreakMinutes >= 5) editBreakMinutes = 5;
      if (editBreakMinutes <= 0) editBreakMinutes = 0;
      if (editBreakSeconds <=0 || editBreakMinutes == 5) editBreakSeconds = 0;
      if (editBreakSeconds < 5 && editBreakMinutes == 0) editBreakSeconds = 0;

      //Sets value of editBreakMinutes to either breakValue, or breakOnlyValue, depending on which mode we're on.
      if (mode == 1) breakValue = (int) ((editBreakMinutes * 60) + editBreakSeconds);
      else if (mode == 2) breaksOnlyValue = (int) ((editBreakMinutes * 60) + editBreakSeconds);
      second_value_textView.setText(convertCustomTextView(breakValue));

      toastBounds(5, 300, setValue);
      toastBounds(5, 300, breakValue);
      toastBounds(5, 300, breaksOnlyValue);
      if (setValue < 5) setValue = 5;
      if (breakValue < 5) breakValue = 5;
      if (breaksOnlyValue < 5) breaksOnlyValue = 5;
      if (setValue > 300) setValue = 300;
      if (breakValue > 300) breakValue = 300;
      if (breaksOnlyValue > 300) breaksOnlyValue = 300;
    } else if (mode==3) {
      editPomMinutesOne = convertEditTextToLong(first_value_edit);
      editPomSecondsOne = convertEditTextToLong(first_value_edit_two);
      editPomMinutesTwo = convertEditTextToLong(second_value_edit);
      editPomSecondsTwo = convertEditTextToLong(second_value_edit_two);
      editPomMinutesThree = convertEditTextToLong(third_value_edit);
      editPomSecondsThree = convertEditTextToLong(third_value_edit_two);

      toastBounds(15, 90, pomValue1);
      toastBounds(3, 10, pomValue2);
      toastBounds(10, 60, pomValue3);
      if (pomValue1 > 90) pomValue1 = 90;
      if (pomValue1 < 15) pomValue1 = 15;
      if (pomValue2 > 10) pomValue2 = 10;
      if (pomValue2 < 3) pomValue2 = 3;
      if (pomValue3 < 10) pomValue3 = 10;
      if (pomValue3 > 60) pomValue3 = 60;
    }
    setTimerValueBounds();
  }

  //Sets min/max bounds on timer values. MUST be separate from setEditValues() so it can be called w/ in our +/- increment runnable and not b0rk the values.
  public void setTimerValueBounds() {
    switch (mode) {
      case 1: case 2:
        toastBounds(5, 300, setValue);
        toastBounds(5, 300, breakValue);
        toastBounds(5, 300, breaksOnlyValue);
        if (setValue < 5) setValue = 5;
        if (breakValue < 5) breakValue = 5;
        if (breaksOnlyValue < 5) breaksOnlyValue = 5;
        if (setValue > 300) setValue = 300;
        if (breakValue > 300) breakValue = 300;
        if (breaksOnlyValue > 300) breaksOnlyValue = 300;
        break;
      case 3:
        toastBounds(900, 5400, pomValue1);
        toastBounds(180, 600, pomValue2);
        toastBounds(600, 1800, pomValue3);
        if (pomValue1 > 5400) pomValue1 = 5400;
        if (pomValue1 < 900) pomValue1 = 900;
        if (pomValue2 > 600) pomValue2 = 600;
        if (pomValue2 < 180) pomValue2 = 180;
        if (pomValue3 < 600) pomValue3 = 600;
        if (pomValue3 > 1800) pomValue3 = 1800;
        break;
    }
  }

  //Converts and sets our minute and second edit values from raw time values.
  public void convertEditTime(boolean setTextViews) {
    //Used to turn off editText listener's execution of setEditValues(), which overrides setValue and breakValue when trying to change them via +/- runnables, which call this function.
    editListener = false;
    if (mode==1 || mode==2) {
      editSetSeconds = setValue % 60;
      editSetMinutes = setValue / 60;
      if (mode == 1) {
        editBreakSeconds = breakValue % 60;
        editBreakMinutes = breakValue / 60;
      } else {
        editBreakSeconds = breaksOnlyValue % 60;
        editBreakMinutes = breaksOnlyValue / 60;
      }
      if (setTextViews) {
        first_value_edit.setText(String.valueOf(editSetMinutes));
        first_value_edit_two.setText(elongateEditSeconds(editSetSeconds));
        second_value_edit.setText(String.valueOf(editBreakMinutes));
        second_value_edit_two.setText(elongateEditSeconds(editBreakSeconds));
      }

    } else if (mode==3) {
      editPomSecondsOne = pomValue1 % 60;
      editPomMinutesOne = pomValue1 / 60;
      editPomSecondsTwo = pomValue2 % 60;
      editPomMinutesTwo = pomValue2 / 60;
      editPomSecondsThree = pomValue3 % 60;
      editPomMinutesThree = pomValue3 / 60;
      if (setTextViews) {
        first_value_edit.setText(String.valueOf(editPomMinutesOne));
        second_value_edit.setText(String.valueOf(editPomMinutesTwo));
        third_value_edit.setText(String.valueOf(editPomMinutesThree));
        first_value_edit_two.setText(elongateEditSeconds(editPomSecondsOne));
        second_value_edit_two.setText(elongateEditSeconds(editPomSecondsTwo));
        third_value_edit_two.setText(elongateEditSeconds(editPomSecondsThree));
      }
    }
    editListener = true;
  }

  public long convertEditTextToLong(EditText editVar) {
    if (!editVar.getText().toString().equals("")) {
      return Long.parseLong(editVar.getText().toString());
    } else return 0;
  }

  //Function for switching between textView and editText in add/sub menu.
  public void editAndTextSwitch(boolean removeTextView, int viewRemoved) {
    convertEditTime(false);
    //If moving from textView -> editText.
    if (removeTextView) {
      if (viewRemoved == 1) {
        if (first_value_textView.isShown()) {
          first_value_textView.setVisibility(View.INVISIBLE);
          first_value_edit.setVisibility(View.VISIBLE);
          first_value_edit_two.setVisibility(View.VISIBLE);
          first_value_sep.setVisibility(View.VISIBLE);
        }
        if (second_value_edit.isShown() || second_value_edit_two.isShown()) {
          second_value_textView.setVisibility(View.VISIBLE);
          second_value_edit.setVisibility(View.GONE);
          second_value_edit_two.setVisibility(View.GONE);
          second_value_sep.setVisibility(View.GONE);
        }
        if (mode==3) {
          if (third_value_edit.isShown()) {
            third_value_edit.setVisibility(View.GONE);
            third_value_edit_two.setVisibility(View.GONE);
            third_value_sep.setVisibility(View.GONE);
            third_value_textView.setVisibility(View.VISIBLE);
          }
        }
      } else if (viewRemoved == 2) {
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
        if (mode==3) {
          if (third_value_edit.isShown()) {
            third_value_edit.setVisibility(View.GONE);
            third_value_edit_two.setVisibility(View.GONE);
            third_value_sep.setVisibility(View.GONE);
            third_value_textView.setVisibility(View.VISIBLE);
          }
        }
      } else if (viewRemoved == 3) {
        if (first_value_edit.isShown()) {
          first_value_edit.setVisibility(View.GONE);
          first_value_edit_two.setVisibility(View.GONE);
          first_value_sep.setVisibility(View.GONE);
          first_value_textView.setVisibility(View.VISIBLE);
        }
        if (second_value_edit.isShown()) {
          second_value_edit.setVisibility(View.GONE);
          second_value_edit_two.setVisibility(View.GONE);
          second_value_sep.setVisibility(View.GONE);
          second_value_textView.setVisibility(View.VISIBLE);
        }
        third_value_textView.setVisibility(View.INVISIBLE);
        third_value_edit.setVisibility(View.VISIBLE);
        third_value_edit_two.setVisibility(View.VISIBLE);
        third_value_sep.setVisibility(View.VISIBLE);
      }

    } else {
      //If moving from editText -> textView.
      switch (mode) {
        case 1:
        case 2:
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
            if (first_value_edit.isShown()) {
              first_value_edit.setVisibility(View.GONE);
              first_value_edit_two.setVisibility(View.GONE);
              first_value_sep.setVisibility(View.GONE);
              first_value_textView.setVisibility(View.VISIBLE);
            }
          } else if (viewRemoved == 2) {
            if (second_value_edit.isShown()) {
              second_value_edit.setVisibility(View.GONE);
              second_value_edit_two.setVisibility(View.GONE);
              second_value_sep.setVisibility(View.GONE);
              second_value_textView.setVisibility(View.VISIBLE);
            }
          } else if (viewRemoved == 3) {
            if (third_value_edit.isShown()) {
              third_value_edit.setVisibility(View.GONE);
              third_value_edit_two.setVisibility(View.GONE);
              third_value_sep.setVisibility(View.GONE);
              third_value_textView.setVisibility(View.VISIBLE);
            }
          }
          break;
      }
    }
  }

  public void toastBounds(long min, long max, long value) {
    if (value < min) minReached = true;
    if (value > max) maxReached = true;
  }

  //Creates fade effect if reaching min/max of timer values.
  public void fadeCap(TextView textView) {
    if (minReached || maxReached) {
      minReached = false;
      maxReached = false;
      Animation fadeCap = new AlphaAnimation(1.0f, 0.3f);
      fadeCap.setDuration(350);
      textView.setAnimation(fadeCap);
    }
  }

  public String elongateEditSeconds(long seconds) {
    String temp = String.valueOf(seconds);
    if (temp.length()<2) temp = "0" + temp;
    return temp;
  }

  //Conversion of Long->String for CIRCLE TIMER textViews.
  private String convertSeconds(long totalSeconds) {
    DecimalFormat df = new DecimalFormat("00");
    long minutes;
    long remainingSeconds;

    if (totalSeconds >= 60) {
      minutes = totalSeconds / 60;
      remainingSeconds = totalSeconds % 60;
      return (minutes + ":" + df.format(remainingSeconds));
    } else {
      if (totalSeconds != 5) return String.valueOf(totalSeconds);
      else return "5";
    }
  }
  //Conversion of Long-String for add/subtract popUp textViews. Slightly different format than our timer textViews.
  public String convertCustomTextView(long totalSeconds) {
    DecimalFormat df = new DecimalFormat("00");
    long minutes;
    long remainingSeconds;
//        if (mode==3) totalSeconds = totalSeconds*60;
    minutes = totalSeconds / 60;

    remainingSeconds = totalSeconds % 60;
    if (totalSeconds >= 60) {
      String formattedSeconds = df.format(remainingSeconds);
      if (formattedSeconds.length() > 2) formattedSeconds = "0" + formattedSeconds;
      return (minutes + " : " + formattedSeconds);
    } else {
      String totalStringSeconds = String.valueOf(totalSeconds);
      if (totalStringSeconds.length() < 2) totalStringSeconds = "0" + totalStringSeconds;
      if (totalSeconds < 5) return ("0 : 05");
      else return "0 : " + totalStringSeconds;
    }
  }

  public void adjustCustom(boolean adding) {
    if (adding) {
      switch (mode) {
        case 1:
          if (customSetTime.size() < 8) {
            customSetTime.add(setValue * 1000);
            customSetTimeUP.add(0);
            customBreakTime.add(breakValue * 1000);
            customBreakTimeUP.add((0));

            convertedSetsList.add(convertSeconds(setValue));
            convertedBreaksList.add(convertSeconds(breakValue));
            setEditValues();
          } else {
            Toast.makeText(getApplicationContext(), "Max rounds reached!", Toast.LENGTH_SHORT).show();
            //Return to save on resources. Also to avoid unnecessary fading in Pom mode.
            return;
          }
          break;
        case 2:
          if (breaksOnlyTime.size() < 8) {
            breaksOnlyTime.add(breaksOnlyValue * 1000);
            breaksOnlyTimeUP.add((0));
            convertedBreaksOnlyList.add(convertSeconds(breaksOnlyValue));
            setEditValues();
          } else {
            Toast.makeText(getApplicationContext(), "Max rounds reached!", Toast.LENGTH_SHORT).show();
            return;
          }
          break;
        case 3:
          if (pomValuesTime.size() == 0) {
            for (int i = 0; i < 3; i++) {
              pomValuesTime.add(pomValue1);
              pomValuesTime.add(pomValue2);
            }
            pomValuesTime.add(pomValue1);
            pomValuesTime.add(pomValue3);
            setEditValues();
          } else {
            Toast.makeText(getApplicationContext(), "Pomodoro cycle already loaded!", Toast.LENGTH_SHORT).show();
            return;
          }
          break;
      }
      //Resetting the "add/subtract fade" alpha to 5 to ensure we always fade in from black.
//            dotDraws.fadeDotDraw(5, true);
    } else {
      switch (mode) {
        case 1:
          if (customSetTime.size() > 0) {
            customSetTime.remove(customSetTime.size() - 1);
            customSetTimeUP.remove(customSetTimeUP.size() - 1);
            customBreakTime.remove(customBreakTime.size() - 1);
            customBreakTimeUP.remove(customBreakTimeUP.size() - 1);

            convertedSetsList.remove(convertedSetsList.size()-1);
            convertedBreaksList.remove(convertedBreaksList.size()-1);
          } else {
            Toast.makeText(getApplicationContext(), "Nothing left to remove!", Toast.LENGTH_SHORT).show();
            return;
          }
          break;
        case 2:
          if (breaksOnlyTime.size() > 0) {
            breaksOnlyTime.remove(breaksOnlyTime.size() - 1);
            breaksOnlyTimeUP.remove(breaksOnlyTimeUP.size() - 1);

            convertedBreaksOnlyList.remove(convertedBreaksOnlyList.size()-1);
          } else {
            Toast.makeText(getApplicationContext(), "Nothing left to remove!", Toast.LENGTH_SHORT).show();
            return;
          }
          break;
        case 3:
          //If a cycle exists, disable the timer because we are removing the cycle via our fadeOutDot runnable which will not complete until the fade is done. Adding a cycle will re-enable the timer through populateCycleUI().
          if (pomValuesTime.size() != 0) pomValuesTime.clear(); else {
            Toast.makeText(getApplicationContext(), "No Pomodoro cycle to clear!", Toast.LENGTH_SHORT).show();
            return;
          }
          break;
      }
    }
    Log.i("testPop", "set list is " + convertedSetsList + " and break is " + convertedBreaksList);
    cycleRoundsAdapter.setMode(mode);
    cycleRoundsAdapter.notifyDataSetChanged();
  }

  public void removeEditViews() {
    first_value_edit.setVisibility(View.GONE);
    first_value_sep.setVisibility(View.GONE);
    first_value_edit.setVisibility(View.GONE);
    first_value_edit_two.setVisibility(View.GONE);
    second_value_edit.setVisibility(View.GONE);
    second_value_sep.setVisibility(View.GONE);
    second_value_edit_two.setVisibility(View.GONE);
    third_value_edit.setVisibility(View.GONE);
    third_value_sep.setVisibility(View.GONE);
    third_value_edit_two.setVisibility(View.GONE);
  }
//
//  @SuppressLint("ClickableViewAccessibility")
//  public void toggleCountUpViews(boolean onSets, boolean countingUp) {
//    removeEditViews();
//    if (countingUp) {
//      if (onSets) {
//        first_value_textView.setVisibility(View.INVISIBLE);
//        infinity_one.setVisibility(View.VISIBLE);
//        plus_first_value.setEnabled(false);
//        minus_first_value.setEnabled(false);
//        infinity_mode_one = true;
//      } else {
//        second_value_textView.setVisibility(View.INVISIBLE);
//        infinity_two.setVisibility(View.VISIBLE);
//        plus_second_value.setEnabled(false);
//        minus_second_value.setEnabled(false);
//        infinity_mode_two = true;
//      }
//    } else {
//      if (onSets) {
//        first_value_textView.setVisibility(View.VISIBLE);
//        infinity_one.setVisibility(View.INVISIBLE);
//        plus_first_value.setEnabled(true);
//        minus_first_value.setEnabled(true);
//        infinity_mode_one = false;
//      } else {
//        second_value_textView.setVisibility(View.VISIBLE);
//        infinity_two.setVisibility(View.INVISIBLE);
//        plus_second_value.setEnabled(true);
//        minus_second_value.setEnabled(true);
//        infinity_mode_two = false;
//      }
//    }
//  }

  public void editCycleViews() {
    //Instance of layout objects we can set programatically based on which mode we're on.
    ConstraintLayout.LayoutParams s2ParamsAdd = (ConstraintLayout.LayoutParams) plus_second_value.getLayoutParams();
    ConstraintLayout.LayoutParams s2ParamsSub = (ConstraintLayout.LayoutParams) minus_second_value.getLayoutParams();
    ConstraintLayout.LayoutParams addParams = (ConstraintLayout.LayoutParams) add_cycle.getLayoutParams();
    ConstraintLayout.LayoutParams subParams = (ConstraintLayout.LayoutParams) sub_cycle.getLayoutParams();

    convertEditTime(true);
    switch (mode) {
      case 1: case 2:
        s2.setVisibility(View.VISIBLE);
        s3.setVisibility(View.GONE);
        third_value_textView.setVisibility(View.INVISIBLE);
        plus_third_value.setVisibility(View.INVISIBLE);
        minus_third_value.setVisibility(View.INVISIBLE);
        plus_second_value.setVisibility(View.VISIBLE);
        minus_second_value.setVisibility(View.VISIBLE);
        upDown_arrow_one.setVisibility(View.VISIBLE);
        upDown_arrow_two.setVisibility(View.VISIBLE);
        if (!infinity_mode_two) {
          second_value_textView.setVisibility(View.VISIBLE);
          s2.setText(R.string.break_time);
        }
        if (mode==1) {
          if (!infinity_mode_one) {
            first_value_textView.setVisibility(View.VISIBLE);
            first_value_textView.setText(convertCustomTextView(setValue));
          }
          s1.setVisibility(View.VISIBLE);
          s1.setText(R.string.set_time);
          plus_first_value.setVisibility(View.VISIBLE);
          minus_first_value.setVisibility(View.VISIBLE);
          s1.setText(R.string.set_time);
          if (!infinity_mode_two) second_value_textView.setText(convertCustomTextView(breakValue));
        } else {
          s1.setVisibility(View.INVISIBLE);
          first_value_textView.setVisibility(View.INVISIBLE);
          plus_first_value.setVisibility(View.INVISIBLE);
          minus_first_value.setVisibility(View.INVISIBLE);
          second_value_textView.setText(convertCustomTextView(breaksOnlyValue));
        }
        addParams.topToBottom = R.id.s2;
        addParams.topMargin = 60;
        subParams.topToBottom = R.id.s2;
        subParams.topMargin = 60;
        break;
      case 3:
        s1.setVisibility(View.VISIBLE);
        s3.setVisibility(View.VISIBLE);
        first_value_textView.setVisibility(View.VISIBLE);
        plus_first_value.setVisibility(View.VISIBLE);
        minus_first_value.setVisibility(View.VISIBLE);
        plus_second_value.setVisibility(View.VISIBLE);
        minus_second_value.setVisibility(View.VISIBLE);
        third_value_textView.setVisibility(View.VISIBLE);
        plus_third_value.setVisibility(View.VISIBLE);
        minus_third_value.setVisibility(View.VISIBLE);
        upDown_arrow_one.setVisibility(View.GONE);
        upDown_arrow_two.setVisibility(View.GONE);
        s1.setText(R.string.work_time);
        s2.setText(R.string.small_break);
        s3.setText(R.string.long_break);
        first_value_textView.setText(convertCustomTextView(pomValue1));
        second_value_textView.setText(convertCustomTextView(pomValue2));
        third_value_textView.setText(convertCustomTextView(pomValue3));
        addParams.topToBottom = R.id.s3;
        addParams.topMargin = 60;
        subParams.topToBottom = R.id.s3;
        subParams.topMargin = 60;
        break;
    }
    if (mode==2) {
      s2ParamsAdd.topToBottom = R.id.top_anchor;
      s2ParamsSub.topToBottom = R.id.top_anchor;
    } else {
      s2ParamsAdd.topToBottom = R.id.plus_first_value;
      s2ParamsSub.topToBottom = R.id.plus_first_value;
    }
    add_cycle.requestLayout();
    sub_cycle.requestLayout();
    plus_first_value.requestLayout();
    minus_first_value.requestLayout();
    plus_second_value.requestLayout();
    minus_second_value.requestLayout();
  }

  public void countUpMode(boolean onSet) {
    if (mode==1) {
      if (onSet) {
        //Moving to COUNT UP mode.
        if (!setsAreCountingUp) {
          setsAreCountingUp = true;
          upDown_arrow_one.setTag(COUNTING_UP);
          upDown_arrow_one.setImageResource(R.drawable.arrow_up);;
          prefEdit.putBoolean("setCountUpMode", true);
          cycleRoundsAdapter.countingUpSets(true);
        } else {
          //Moving to COUNT DOWN mode.
          setsAreCountingUp = false;
          upDown_arrow_one.setTag(COUNTING_DOWN);
          upDown_arrow_one.setImageResource(R.drawable.arrow_down);
          upDown_arrow_one.setTag(1);
          prefEdit.putBoolean("setCountUpMode", false);
          cycleRoundsAdapter.countingUpSets(false);
        }
      } else {
        //Moving to COUNT UP mode.
        if (!breaksAreCountingUp) {
          breaksAreCountingUp = true;
          upDown_arrow_two.setTag(COUNTING_UP);
          upDown_arrow_two.setImageResource(R.drawable.arrow_up);
          prefEdit.putBoolean("breakCountUpMode", true);
          cycleRoundsAdapter.countingUpBreaks(true);
        } else {
          //Moving to COUNT DOWN mode.
          breaksAreCountingUp = false;
          upDown_arrow_two.setTag(COUNTING_DOWN);
          upDown_arrow_two.setImageResource(R.drawable.arrow_down);
          prefEdit.putBoolean("breakCountUpMode", false);
          cycleRoundsAdapter.countingUpBreaks(false);
        }
      }
    } else if (mode==2) {
      //Moving to COUNT UP mode.
      if (!breaksOnlyAreCountingUp) {
        breaksOnlyAreCountingUp = true;
        upDown_arrow_one.setTag(COUNTING_UP);
        upDown_arrow_one.setImageResource(R.drawable.arrow_up);
        prefEdit.putBoolean("breakOnlyCountUpMode", true);
        cycleRoundsAdapter.countingUpBreaks(true);
      } else {
        breaksOnlyAreCountingUp = false;
        upDown_arrow_one.setTag(COUNTING_DOWN);
        upDown_arrow_one.setImageResource(R.drawable.arrow_down);
        prefEdit.putBoolean("breakOnlyCountUpMode", false);
        cycleRoundsAdapter.countingUpBreaks(false);
      }
    }
    prefEdit.apply();
    cycleRoundsAdapter.notifyDataSetChanged();
  }

//        cycle_header_text.setOnClickListener(v-> {
//            confirm_header_
//            save.setText(R.string.update_cycles);
//            labelSavePopupWindow.showAtLocation(mainView, Gravity.CENTER, 0, -200);
//
//            String titleText = cycle_header_text.getText().toString();
//            edit_header.setText(titleText);
//            edit_header.setSelection(titleText.length());
//
//            cancel_header_save.setOnClickListener(v2-> {
//                if (labelSavePopupWindow!=null) labelSavePopupWindow.dismiss();
//            });
//
//            confirm_header_save.setOnClickListener(v2 -> {
//                String newTitle = edit_header.getText().toString();
//                if (!titleText.equals(newTitle)) canSaveOrUpdate(true);
//                cycle_header_text.setText(newTitle);
//            });
//        });

//        save_cycles.setOnClickListener(v->{
//            saveAndUpdateCycles();
//        });
//
//        update_cycles.setOnClickListener(v-> {
//            confirmedSaveOrUpdate(UPDATING_CYCLES);
//        });

//    public void clearArrays(boolean populateList) {
//        switch (mode) {
//            case 1:
//                if (setsArray!=null) setsArray.clear();
//                if (breaksArray!=null) breaksArray.clear();
//                if (customTitleArray!=null) customTitleArray.clear();
//
//                if (populateList) {
//                    for (int i=0; i<cyclesList.size(); i++) {
//                        setsArray.add(cyclesList.get(i).getSets());
//                        breaksArray.add(cyclesList.get(i).getBreaks());
//                        customTitleArray.add(cyclesList.get(i).getTitle());
//                    }
//                    if (setsArray.size()>0) runOnUiThread(() -> { savedCycleAdapter.notifyDataSetChanged();
//                    });
//                }
//                break;
//            case 2:
//                if (breaksOnlyArray!=null) breaksOnlyArray.clear();
//                if (breaksOnlyTitleArray!=null) breaksOnlyTitleArray.clear();
//
//                if (populateList) {
//                    for (int i=0; i<cyclesBOList.size(); i++) {
//                        breaksOnlyArray.add(cyclesBOList.get(i).getBreaksOnly());
//                        breaksOnlyTitleArray.add(cyclesBOList.get(i).getTitle());
//                    }
//                    if (breaksOnlyArray.size()>0) runOnUiThread(() -> { savedCycleAdapter.notifyDataSetChanged();
//                    });
//                }
//                break;
//            case 3:
//                if (pomArray!=null) pomArray.clear();
//                if (populateList) {
//                    for (int i=0; i<pomCyclesList.size(); i++) {
//                        pomArray.add(pomCyclesList.get(0).getFullCycle());
//                        pomCyclesTitleArray.add(pomCyclesList.get(0).getTitle());
//                    }
//                    if (pomArray.size()>0) runOnUiThread(()-> savedCycleAdapter.notifyDataSetChanged());
//                }
//        }
//    }

//    public void queryCycles() {
//        switch (mode) {
//            case 1:
//                switch (sortMode) {
//                    case 1:
//                        cyclesList = cyclesDatabase.cyclesDao().loadCyclesMostRecent();
//                        sortCheckmark.setY(14);
//                        break;
//                    case 2:
//                        cyclesList = cyclesDatabase.cyclesDao().loadCycleLeastRecent();
//                        sortCheckmark.setY(110);
//                        break;
//                    case 3:
//                        cyclesList = cyclesDatabase.cyclesDao().loadCyclesMostItems();
//                        sortCheckmark.setY(206);
//                        break;
//                    case 4:
//                        cyclesList = cyclesDatabase.cyclesDao().loadCyclesLeastItems();
//                        sortCheckmark.setY(302);
//                        break;
//                }
//                break;
//            case 2:
//                switch (sortModeBO) {
//                    case 1:
//                        cyclesBOList = cyclesDatabase.cyclesDao().loadCyclesMostRecentBO();
//                        sortCheckmark.setY(14);
//                        break;
//                    case 2:
//                        cyclesBOList = cyclesDatabase.cyclesDao().loadCycleLeastRecentBO();
//                        sortCheckmark.setY(110);
//                        break;
//                    case 3:
//                        cyclesBOList = cyclesDatabase.cyclesDao().loadCyclesMostItemsBO();
//                        sortCheckmark.setY(206);
//                        break;
//                    case 4:
//                        cyclesBOList = cyclesDatabase.cyclesDao().loadCyclesLeastItemsBO();
//                        sortCheckmark.setY(302);
//                }
//                break;
//            case 3:
//                switch (sortModePom) {
//                    case 1:
//                        pomCyclesList = cyclesDatabase.cyclesDao().loadPomCyclesMostRecent();
//                        sortCheckmark.setY(14);
//                        break;
//                    case 2:
//                        pomCyclesList = cyclesDatabase.cyclesDao().loadPomCyclesLeastRecent();
//                        sortCheckmark.setY(110);
//                        break;
//                }
//                break;
//        }
//    }
//
//    public void canSaveOrUpdate(boolean yesWeCan) {
//        switch (mode) {
//            case 1:
//                canSaveOrUpdateCustom = yesWeCan; break;
//            case 2:
//                canSaveOrUpdateBreaksOnly = yesWeCan; break;
//            case 3:
//                canSaveOrUpdatePom = yesWeCan; break;
//        }
//
//        if ( (mode==1 && canSaveOrUpdateCustom) || (mode==2 && canSaveOrUpdateBreaksOnly) || (mode==3 && canSaveOrUpdatePom)) {
//            save_cycles.setTextColor(getResources().getColor(R.color.white));
//            update_cycles.setTextColor(getResources().getColor(R.color.white));
//            save_cycles.setEnabled(true);
//            update_cycles.setEnabled(true);
//        } else {
//            save_cycles.setTextColor(getResources().getColor(R.color.test_grey));
//            update_cycles.setTextColor(getResources().getColor(R.color.test_grey));
//            save_cycles.setEnabled(false);
//            update_cycles.setEnabled(false);
//        }
//    }
//
//    //Launches either Sort option, or popUp window w/ option to save.
//    public void saveAndUpdateCycles() {
//        if (savedCyclePopupWindow!=null && savedCyclePopupWindow.isShowing()){
//            sortPopupWindow.showAtLocation(mainView, Gravity.TOP, 325, 10);
//
//            sortRecent.setOnClickListener(v1 -> {
//                AsyncTask.execute(() -> {
//                    switch (mode) {
//                        case 1:
//                            sortMode = 1; break;
//                        case 2:
//                            sortModeBO = 1; break;
//                        case 3:
//                            sortModePom = 1; break;
//                    }
//                    queryCycles();
//                    runOnUiThread(()-> {
//                        sortCheckmark.setY(14);
//                        clearArrays(true);
//                    });
//                });
//            });
//
//            sortNotRecent.setOnClickListener(v2 ->{
//                AsyncTask.execute(() -> {
//                    switch (mode) {
//                        case 1:
//                            sortMode = 1; break;
//                        case 2:
//                            sortModeBO = 1; break;
//                        case 3:
//                            sortModePom = 1; break;
//                    }
//                    queryCycles();
//                    runOnUiThread(()-> {
//                        sortCheckmark.setY(110);
//                        clearArrays(true);
//                    });
//                });
//            });
//
//            sortHigh.setOnClickListener(v3 -> {
//                AsyncTask.execute(() -> {
//                    switch (mode) {
//                        case 1:
//                            sortMode = 1; break;
//                        case 2:
//                            sortModeBO = 1; break;
//                    }
//                    queryCycles();
//                    runOnUiThread(()-> {
//                        sortCheckmark.setY(206);
//                        clearArrays(true);
//                    });
//                });
//            });
//
//            sortLow.setOnClickListener(v4 -> {
//                AsyncTask.execute(() -> {
//                    switch (mode) {
//                        case 1:
//                            sortMode = 1; break;
//                        case 2:
//                            sortModeBO = 1; break;
//                    }
//                    queryCycles();
//                    runOnUiThread(()-> {
//                        sortCheckmark.setY(302);
//                        clearArrays(true);
//                    });
//                });
//            });
//            sortCheckmark.setY(0);
//            prefEdit.putInt("sortMode", sortMode);
//            prefEdit.putInt("sortModeBO", sortModeBO);
//            prefEdit.putInt("sortModePom", sortModePom);
//            prefEdit.apply();
//        } else {
//            labelSavePopupWindow.showAtLocation(mainView, Gravity.CENTER, 0, 0);
//            edit_header.setText("");
//
//            cancel_header_save.setOnClickListener(v2-> {
//                if (labelSavePopupWindow!=null) labelSavePopupWindow.dismiss();
//            });
//
//            confirm_header_save.setOnClickListener(v2-> {
//                confirm_header_save.setText(R.string.save_cycles);
//                confirmedSaveOrUpdate(SAVING_CYCLES);
//            });
//        }
//    }

//    //Actually saves or updates the cycle.
//    public void confirmedSaveOrUpdate(int saveOrUpdate) {
//        if ((mode==1 && customSetTime.size()==0) || (mode==2 && breaksOnlyTime.size()==0) || (mode==3 && pomValuesTime.size()==0)) {
//            Toast.makeText(getApplicationContext(), "Nothing to save!", Toast.LENGTH_SHORT).show();;
//            return;
//        }
//
//        AsyncTask.execute(() -> {
//            //Defaulting to unique cycle unless otherwise set by retrieveAndselectRounds();
//            duplicateCycle = false;
//            boolean changeCycle = false;
//            Calendar calendar = Calendar.getInstance();
//            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMMM d yyyy - hh:mma", Locale.getDefault());
//
//            //Executes gSon->Json conversion and compares new String to db lists, setting duplicateCycles to TRUE if an instance matches.
//            retrieveAndCheckCycles();
//
//            switch (mode) {
//                case 1:
//                    //New instance of the Cycle entity that can be used for insertion. Otherwise, inheriting the instance from onCycleClick callback that uses a specific position to update.
//                    if (saveOrUpdate == SAVING_CYCLES) cycles = new Cycles(); else cycles = cyclesList.get(cyclesList.size()-1);
//
//                    if (!cycle_header_text.getText().toString().isEmpty()) {
//                        cycles.setTitle(cycle_header_text.getText().toString());
//                    } else {
//                        String newDate = dateFormat.format(calendar.getTime());
//                        cycles.setTitle(newDate);
//                    }
//                    customTitleArray.add(cycle_header_text.getText().toString());
//
//                    //If cycle is not duplicate OR size is 0 (nothing to duplicate), we proceed with insert/update.
//                    if (!duplicateCycle || cyclesList.size()==0) {
//                        cycles.setSets(convertedSetList);
//                        cycles.setBreaks(convertedBreakList);
//                        cycles.setTimeAdded(System.currentTimeMillis());
//                        cycles.setItemCount(customSetTime.size());
//                        if (saveOrUpdate == SAVING_CYCLES){
//                            cyclesDatabase.cyclesDao().insertCycle(cycles);
//                            //Re-instantiating cycleList with new row added.
//                            queryCycles();
//                            //Getting ID of latest row entry.
//                            customID = cyclesList.get(0).getId();
//                            //Saving ID to sharedPref.
//                            prefEdit.putInt("customID", customID);
//                        } else cyclesDatabase.cyclesDao().updateCycles(cycles);
//                        runOnUiThread(() -> {
//                            if (labelSavePopupWindow!=null) labelSavePopupWindow.dismiss();
//                            canSaveOrUpdate(false);
//                        });
//                        changeCycle = true;
//                    }
//                    break;
//                case 2:
//                    //New instance of the CycleBO entity that can be used for insertion. Otherwise, inheriting the instance from onCycleClick callback that uses a specific position to update.
//                    if (saveOrUpdate == SAVING_CYCLES) cyclesBO = new CyclesBO(); else cyclesBO = cyclesBOList.get(cyclesBOList.size()-1);
//
//                    if (!cycle_header_text.getText().toString().isEmpty()) {
//                        cyclesBO.setTitle(cycle_header_text.getText().toString());
//                    } else {
//                        String newDate = dateFormat.format(calendar.getTime());
//                        cyclesBO.setTitle(newDate);
//                    }
//                    breaksOnlyTitleArray.add(cycle_header_text.getText().toString());
//
//                    if (!duplicateCycle || cyclesBOList.size()==0) {
//                        cyclesBO.setBreaksOnly(convertedBreakOnlyList);
//                        cyclesBO.setTimeAdded(System.currentTimeMillis());
//                        cyclesBO.setItemCount(breaksOnlyTime.size());
//                        if (saveOrUpdate == SAVING_CYCLES) {
//                            cyclesDatabase.cyclesDao().insertBOCycle(cyclesBO);
//                            queryCycles();
//                            breaksOnlyID = cyclesBOList.get(0).getId();
//                            prefEdit.putInt("breaksOnlyID", breaksOnlyID);
//                        } else cyclesDatabase.cyclesDao().updateBOCycles(cyclesBO);
//                        runOnUiThread(() -> {
//                            if (labelSavePopupWindow!=null) labelSavePopupWindow.dismiss();
//                            canSaveOrUpdate(false);
//                        });
//                        changeCycle = true;
//                    }
//                    break;
//                case 3:
//                    if (saveOrUpdate == SAVING_CYCLES) pomCycles = new PomCycles(); else pomCycles = pomCyclesList.get(pomCyclesList.size()-1);
//
//                    if (!cycle_header_text.getText().toString().isEmpty()) {
//                        pomCycles.setTitle(cycle_header_text.getText().toString());
//                    } else {
//                        String newDate = dateFormat.format(calendar.getTime());
//                        pomCycles.setTitle(newDate);
//                    }
//                    pomCyclesTitleArray.add(cycle_header_text.getText().toString());
//
//                    if (!duplicateCycle && pomCyclesList.size()==0) {
//                        pomCycles.setFullCycle(convertedPomList);
//                        pomCycles.setTimeAdded(System.currentTimeMillis());
//                        if (saveOrUpdate == SAVING_CYCLES) {
//                            cyclesDatabase.cyclesDao().insertPomCycle(pomCycles);
//                            queryCycles();
//                            pomID = pomCyclesList.get(0).getId();
//                            prefEdit.putInt("pomID", pomID);
//                        } else cyclesDatabase.cyclesDao().updatePomCycles(pomCycles);
//                        runOnUiThread(() -> {
//                            if (labelSavePopupWindow!=null) labelSavePopupWindow.dismiss();
//                            canSaveOrUpdate(false);
//                        });
//                        changeCycle = true;
//                    }
//            }
//            prefEdit.apply();
//            boolean finalChangeCycle = changeCycle;
//            runOnUiThread(()-> {
//                if (duplicateCycle) Toast.makeText(getApplicationContext(), "An identical cycle already exists!", Toast.LENGTH_SHORT).show();
//                else if (saveOrUpdate == SAVING_CYCLES && finalChangeCycle) Toast.makeText(getApplicationContext(), "Cycle added", Toast.LENGTH_SHORT).show();
//                else if (saveOrUpdate == UPDATING_CYCLES && finalChangeCycle) Toast.makeText(getApplicationContext(), "Cycle updated", Toast.LENGTH_SHORT).show();
//                cycle_header_text.setText(cycle_header_text.getText().toString());
//            });
//        });
//    }

//    //Used to retrieve a single cycle within our database. Calls populateUICycle() which sets the Array values into our timer millis values.
//    //When recall is TRUE, retrieves the last used ID instance, when recall is FALSE, Uses a positional input from our saved cycle list.
//    public void selectRound(int position, boolean recall) {
//        AsyncTask.execute(() -> {
//            switch (mode) {
//                case 1:
//                    String tempSets = "";
//                    String tempBreaks = "";
//                    if (recall) {
//                        cycles = cyclesList.get(position);
//                        tempSets = cyclesList.get(0).getSets();
//                        tempBreaks = cyclesList.get(0).getBreaks();
//                    } else {
//                        queryCycles();
//                        cycles = cyclesList.get(position);
//                        tempSets = cyclesList.get(position).getSets();
//                        tempBreaks = cyclesList.get(position).getBreaks();
//                        customID = cyclesList.get(position).getId();
//                        prefEdit.putInt("customID", customID);
//                    }
//
//                    String[] setSplit = tempSets.split(" - ", 0);
//                    String[] breakSplit = tempBreaks.split(" - ", 0);
//                    customSetTime.clear();
//                    customBreakTime.clear();
//
//                    for (int i=0; i<setSplit.length; i++) {
//                        customSetTime.add(Long.parseLong(setSplit[i])*1000);
//                        customBreakTime.add(Long.parseLong(breakSplit[i])*1000);
//                    }
//                    runOnUiThread(() -> cycle_header_text.setText(cycles.getTitle()));
//                    break;
//                case 2:
//                    String tempBreaksOnly = "";
//                    if (recall) {
//                        cyclesBO = cyclesBOList.get(position);
//                        tempBreaksOnly = cyclesBOList.get(0).getBreaksOnly();
//                    } else {
//                        queryCycles();
//                        cyclesBO = cyclesBOList.get(position);
//                        tempBreaksOnly = cyclesBOList.get(position).getBreaksOnly();
//                        breaksOnlyID = cyclesBOList.get(position).getId();
//                        prefEdit.putInt("breaksOnlyID", breaksOnlyID);
//                    }
//
//                    String[] breaksOnlySplit = tempBreaksOnly.split(" - ", 0);
//                    breaksOnlyTime.clear();
//
//                    for (int i=0; i<breaksOnlySplit.length; i++) {
//                        breaksOnlyTime.add(Long.parseLong(breaksOnlySplit[i])*1000);
//                    }
//                    runOnUiThread(() -> cycle_header_text.setText(cyclesBO.getTitle()));
//                    break;
//                case 3:
//                    String tempPom = "";
//                    if (recall) {
//                        pomCycles = pomCyclesList.get(position);
//                        tempPom = pomCyclesList.get(0).getFullCycle();
//                    } else {
//                        queryCycles();
//                        pomCycles = pomCyclesList.get(position);
//                        tempPom = pomCyclesList.get(position).getFullCycle();
//                        pomID = pomCyclesList.get(position).getId();
//                        prefEdit.putInt("pomID", pomID);
//                    }
//
//                    String[] pomSplit = tempPom.split("-", 0);
//                    pomValuesTime.clear();
//
//                    for (int i=0; i<pomSplit.length; i++) pomValuesTime.add(Long.parseLong(pomSplit[i]));
//                    runOnUiThread(() -> cycle_header_text.setText(pomCycles.getTitle()));
//                    break;
//            }
//
//            runOnUiThread(() -> {
//                saveArrays();
//                resetTimer();
//                savedCyclePopupWindow.dismiss();
//                invalidateOptionsMenu();
//                defaultMenu = true;
//            });
//            prefEdit.apply();
//        });
//    }

//    //All convertedLists are used in save/update method.
//    private void retrieveAndCheckCycles() {
//        queryCycles();
//        Gson gson = new Gson();
//        ArrayList<Long> tempSets = new ArrayList<>();
//        ArrayList<Long> tempBreaks = new ArrayList<>();
//        ArrayList<Long> tempBreaksOnly = new ArrayList<>();
//
//        switch (mode) {
//            //Getting an instance of our current cycles' array list.
//            case 1:
//                for (int i=0; i<customSetTime.size(); i++) {
//                    tempSets.add(customSetTime.get(i) /1000);
//                }
//                for (int i=0; i<customBreakTime.size(); i++){
//                    tempBreaks.add(customBreakTime.get(i)/1000);
//                }
//                //Editing our displayed cycles' string array to sync with the retrieved format of our database's saved cycles.
//                convertedSetList = gson.toJson(tempSets);
//                convertedBreakList = gson.toJson(tempBreaks);
//                convertedSetList = convertedSetList.replace("\"", "");
//                convertedSetList = convertedSetList.replace("]", "");
//                convertedSetList = convertedSetList.replace("[", "");
//                convertedSetList = convertedSetList.replace(",", " - ");
//                convertedBreakList = convertedBreakList.replace("\"", "");
//                convertedBreakList = convertedBreakList.replace("]", "");
//                convertedBreakList = convertedBreakList.replace("[", "");
//                convertedBreakList = convertedBreakList.replace(",", " - ");
//
//                //If our displayed cycles and title match any database entry EXACTLY, we set duplicateCycle to true.
//                boolean endLoop = false;
//                if (cyclesList.size()>0) {
//                    for (int i=0; i<cyclesList.size(); i++) {
//                        if (!endLoop) {
//                            if (cyclesList.get(i).getSets().equals(convertedSetList) && cyclesList.get(i).getBreaks().equals(convertedBreakList) && cyclesList.get(i).getTitle().equals(cycle_header_text.getText().toString())) {
//                                duplicateCycle = true; endLoop = true;
//                            }
//                        }
//                        duplicateCycle = endLoop;
//                    }
//                }
//                break;
//            case 2:
//                for (int i=0; i<breaksOnlyTime.size(); i++) {
//                    tempBreaksOnly.add(breaksOnlyTime.get(i)/1000);
//                }
//                convertedBreakOnlyList = gson.toJson(tempBreaksOnly);
//                convertedBreakOnlyList = convertedBreakOnlyList.replace("\"", "");
//                convertedBreakOnlyList = convertedBreakOnlyList.replace("]", "");
//                convertedBreakOnlyList = convertedBreakOnlyList.replace("[", "");
//                convertedBreakOnlyList = convertedBreakOnlyList.replace(",", " - ");
//
//                if (cyclesBOList.size()>0) {
//                    for (int i=0; i<cyclesBOList.size(); i++) {
//                        if (cyclesBOList.get(i).getBreaksOnly().equals(convertedBreakOnlyList) && cyclesBOList.get(i).getTitle().equals(cycle_header_text.getText().toString()))
//                            duplicateCycle = true;
//                    }
//                }
//                break;
//            case 3:
//                convertedPomList = gson.toJson(pomValuesTime);
//
//                String[] pomSplit = convertedPomList.split(",", 0);
//                convertedPomList = "";
//                for (int i=0; i<pomSplit.length; i++) {
//                    if (pomSplit[i].length()<2) pomSplit[i] = "0" + pomSplit[i];
//                }
//
//                convertedPomList = Arrays.toString(pomSplit);
//                convertedPomList = convertedPomList.replace("]", "");
//                convertedPomList = convertedPomList.replace("[", "");
//                convertedPomList = convertedPomList.replace(",", " -");
//
//                if (pomCyclesList.size()>0) {
//                    for (int i=0; i<pomCyclesList.size(); i++) {
//                        if (pomCyclesList.get(i).getFullCycle().equals(convertedPomList) && pomCyclesList.get(i).getTitle().equals(cycle_header_text.getText().toString()))
//                            duplicateCycle = true;
//                    }
//                }
//                break;
//        }
//    }
//
//    //Removes a set or break and calls function to update the millis value.
//    public void removeSetOrBreak(boolean onSet) {
//        switch (mode) {
//            case 1:
//                if (onSet) {
//                    setMillis = newMillis(true);
//                    numberOfSets--;
//                } else {
//                    breakMillis = newMillis(false);
//                    numberOfBreaks--;
//                }
//                break;
//            case 2:
//                breakOnlyMillis = newMillis(false);
//                numberOfBreaksOnly--;
//                break;
//        }
//        drawDots(0);
//    }
//
//    public void switchMenu() {
//        Runnable r = () -> {
//            defaultMenu = false;
//            invalidateOptionsMenu();
//        };
//        mHandler.postDelayed(r, 50);
//    }


//    public void saveArrays() {
//        Gson gson = new Gson();
//        String savedSetArrays = "";
//        String savedBreakArrays = "";
//        String savedBOArrays = "";
//        String savedPomArrays = "";
//        String savedTitle = "";
//        if (!edit_header.getText().toString().equals("")) savedTitle = edit_header.getText().toString(); else savedTitle = getString(R.string.default_title);
//        if (customSetTime.size()>0){
//            savedSetArrays = gson.toJson(customSetTime);
//            savedBreakArrays = gson.toJson(customBreakTime);
//        }
//        if (breaksOnlyTime.size()>0) savedBOArrays = gson.toJson(breaksOnlyTime);
//        savedPomArrays = gson.toJson((pomValuesTime));
//
//        prefEdit.putString("setArrays", savedSetArrays);
//        prefEdit.putString("breakArrays", savedBreakArrays);
//        prefEdit.putString("savedBOArrays", savedBOArrays);
//        prefEdit.putString("savedPomArrays", savedPomArrays);
//        prefEdit.putString("savedTitle", savedTitle);
//        prefEdit.apply();
//    }
//

//    //receivedPos is taken from dotDraws using the sendPos callback, called from onTouchEvent when it uses setCycle. It returns 0-7 based on which round has been selected.
//    public void deleteSelectedRound() {
//        if (mode==1) {
//            customSetTime.remove(receivedPos);
//            customBreakTime.remove(receivedPos);
//            customSetTimeUP.remove(receivedPos);
//            customBreakTimeUP.remove(receivedPos);
//            numberOfSets-=1;
//            numberOfBreaks-=1;
//            if (!setsAreCountingUp) dotDraws.setTime(customSetTime); else dotDraws.setTime(customSetTimeUP);
//            if (!breaksAreCountingUp) dotDraws.breakTime(customBreakTime); else dotDraws.breakTime(customBreakTimeUP);
////            if (numberOfSets==0) {
////                delete_sb.setAlpha(0.3f);
////                delete_sb.setEnabled(false);
////            }
//        } else if (mode==2){
//            breaksOnlyTime.remove(receivedPos);
//            breaksOnlyTimeUP.remove(receivedPos);
//            numberOfBreaksOnly-=1;
//            if (!breaksOnlyAreCountingUp) dotDraws.breakOnlyTime(breaksOnlyTime); else dotDraws.breakOnlyTime(breaksOnlyTimeUP);
////            if (numberOfBreaksOnly==0) {
////                delete_sb.setAlpha(0.3f);
////                delete_sb.setEnabled(false);
////            }
//        }
//        canSaveOrUpdate(true);
//    }



}