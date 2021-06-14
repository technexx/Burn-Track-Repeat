package com.example.tragic.irate.simple.stopwatch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuItemCompat;
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
public class MainActivity extends AppCompatActivity implements SavedCycleAdapter.onCycleClickListener, SavedCycleAdapter.onHighlightListener, SavedCycleAdapter.onInfinityToggleListener {

  ConstraintLayout cl;
  SharedPreferences sharedPreferences;
  SharedPreferences.Editor prefEdit;
  View mainView;

  ImageButton fab;

  ImageView sortCheckmark;

  CyclesDatabase cyclesDatabase;
  Cycles cycles;
  CyclesBO cyclesBO;
  PomCycles pomCycles;
  List<Cycles> cyclesList;
  List<CyclesBO> cyclesBOList;
  List<PomCycles> pomCyclesList;

  CycleRoundsAdapter cycleRoundsAdapter;
  RecyclerView savedCycleRecycler;
  SavedCycleAdapter savedCycleAdapter;
  View deleteCyclePopupView;
  View sortCyclePopupView;
  View savedCyclePopupView;
  View editCyclesPopupView;
  boolean currentCycleEmpty;

  PopupWindow sortPopupWindow;
  PopupWindow savedCyclePopupWindow;
  PopupWindow deleteCyclePopupWindow;
  PopupWindow editCyclesPopupWindow;

  TextView sortRecent;
  TextView sortNotRecent;
  TextView sortHigh;
  TextView sortLow;
  TextView delete_all_text;
  Button delete_all_confirm;
  Button delete_all_cancel;
  TextView appHeader;
  ImageButton edit_highlighted_cycle;
  ImageButton delete_highlighted_cycle;
  ImageButton cancelHighlight;

  int mode = 1;
  int sortMode = 1;
  int sortModeBO = 1;
  int sortModePom = 1;
  int receivedPos;
  int retrievedID;
  String cycleTitle;
  List<String> receivedHighlightPositions;

  EditText cycle_name_edit;
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
  ImageButton start_timer;

  ArrayList<Integer> customSetTime;
  ArrayList<Integer> customBreakTime;
  ArrayList<Integer> breaksOnlyTime;
  ArrayList<Integer> pomValuesTime;
  ArrayList<String> convertedSetsList;
  ArrayList<String> convertedBreaksList;
  ArrayList<String> convertedBreaksOnlyList;
  ArrayList<String> convertedPomList;
  ArrayList<String> customTitleArray;
  ArrayList<String> breaksOnlyTitleArray;
  ArrayList<String> pomTitleArray;
  ArrayList<String> setsArray;
  ArrayList<String> breaksArray;
  ArrayList<String> breaksOnlyArray;
  ArrayList<String> pomArray;

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
  boolean onNewCycle;
  public ArrayList<Integer> infinityArrayOne;
  public ArrayList<Integer> infinityArrayTwo;
  public ArrayList<Integer> infinityArrayThree;

  //Todo: Pass title into timer.
  //Todo: Soft kb still pushes up tabLayout since it's not part of the popUp.
  //Todo: For now, onBackPressed w/ zero rounds ignores any save/update, retaining original values - should we disallow zero in any case exception initial FAB population?
  //Todo: For performance: minimize db calls (e.g. if a list has already been saved and you just need an adapter populated, simply use new array lists).

  //Todo: Preset timer selections.
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
  public void onBackPressed() {
    if (editCyclesPopupWindow.isShowing()) {
      //If non-database cycle (i.e. FAB launched) is present, save it as a new entry. If a database-saved cycle (i.e. highlight launched) is present, update it in the database. queryCycles() has already been called from the edit_highlighted_text button.
      AsyncTask.execute(()->{
        if (onNewCycle) saveCycles(true); else saveCycles(false);
        //Calling queryCycles() to fetch the latest post-save list our cycles.
        queryCycles();
        //Populates the savedCycleAdapter's array lists., formally updates recyclerView for saved cycles, dismisses edit popUp and then clears arrays for both saved cycles and rounds.
        runOnUiThread(()-> {
          populateCycleList();
          savedCycleAdapter.notifyDataSetChanged();
          editCyclesPopupWindow.dismiss();
          clearTimerArrays();
        });
      });
    }
  }

  //Gets the position clicked on from our saved cycle adapter.
  @Override
  public void onCycleClick(int position) {
    AsyncTask.execute(()-> {
      receivedPos = position;
      launchTimerCycle(false);
    });
  }

  //Receives highlighted positions from our adapter.
  @Override
  public void onCycleHighlight(List<String> listOfPositions, boolean addButtons) {
    //Receives list of cycle positions highlighted.
    receivedHighlightPositions = listOfPositions;
    //Sets "highlight mode" actionBar buttons to Visible if entering mode.
    if (addButtons) {
      edit_highlighted_cycle.setVisibility(View.VISIBLE);
      delete_highlighted_cycle.setVisibility(View.VISIBLE);
      cancelHighlight.setVisibility(View.VISIBLE);
      appHeader.setVisibility(View.INVISIBLE);
      edit_highlighted_cycle.setEnabled(listOfPositions.size() <= 1);
    }
  }

  @Override
  public void onInfinityToggle(ArrayList<Integer> toggleSets, ArrayList<Integer> toggleBreaks, int position) {
    infinityArrayOne = toggleSets; infinityArrayTwo = toggleBreaks;
    if (infinityArrayOne.get(position)==0) setsAreCountingUp = false;
    else setsAreCountingUp = true;
    if (infinityArrayTwo.get(position)==0) breaksAreCountingUp = false;
    else breaksAreCountingUp = true;
    Log.i("testList", "receivedPos is " + position);
  }

  @Override
  public boolean onCreateOptionsMenu(final Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_options_menu, menu);
    return true;
  }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_cycles:
              AsyncTask.execute(()-> {
                //Get instance of cycles/cyclesBO/pomCycles.
                queryCycles();
                //if instance is empty, pop a Toast and return. Otherwise, show popUp window to confirm cycle deletion.
                runOnUiThread(()-> {
                  if (currentCycleEmpty) {
                    Toast.makeText(getApplicationContext(), "No cycles to delete!", Toast.LENGTH_SHORT).show();
                  } else deleteCyclePopupWindow.showAtLocation(cl, 0, 0, Gravity.CENTER);
                });
              });
              break;
        }
        return super.onOptionsItemSelected(item);
    }

  @SuppressLint({"UseCompatLoadingForDrawables", "ClickableViewAccessibility", "CommitPrefEdits", "CutPasteId"})
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mainView = findViewById(R.id.main_layout);

    TabLayout tabLayout = findViewById(R.id.tabLayout);
    tabLayout.addTab(tabLayout.newTab().setText("Sets+"));
    tabLayout.addTab(tabLayout.newTab().setText("Breaks"));
    tabLayout.addTab(tabLayout.newTab().setText("Pomodoro"));
    tabLayout.addTab(tabLayout.newTab().setText("Stopwatch"));

    fab = findViewById(R.id.fab);
    savedCycleRecycler = findViewById(R.id.cycle_list_recycler);

    LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    savedCyclePopupView = inflater.inflate(R.layout.saved_cycles_layout, null);
    deleteCyclePopupView = inflater.inflate(R.layout.delete_cycles_popup, null);
    sortCyclePopupView = inflater.inflate(R.layout.sort_popup, null);
    editCyclesPopupView = inflater.inflate(R.layout.editing_cycles, null);

    savedCyclePopupWindow = new PopupWindow(savedCyclePopupView, 800, 1200, true);
    deleteCyclePopupWindow = new PopupWindow(deleteCyclePopupView, 750, 375, true);
    sortPopupWindow = new PopupWindow(sortCyclePopupView, 400, 375, true);
    editCyclesPopupWindow = new PopupWindow(editCyclesPopupView, WindowManager.LayoutParams.MATCH_PARENT, 1430, false);

    savedCyclePopupWindow.setAnimationStyle(R.style.WindowAnimation);
    deleteCyclePopupWindow.setAnimationStyle(R.style.WindowAnimation);
    sortPopupWindow.setAnimationStyle(R.style.WindowAnimation);
    editCyclesPopupWindow.setAnimationStyle(R.style.WindowAnimation);

    cl = new ConstraintLayout(this);
    cycle_name_edit = editCyclesPopupView.findViewById(R.id.cycle_name_edit);
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
    start_timer = editCyclesPopupView.findViewById(R.id.start_timer);

    sortRecent = sortCyclePopupView.findViewById(R.id.sort_most_recent);
    sortNotRecent = sortCyclePopupView.findViewById(R.id.sort_least_recent);
    sortHigh = sortCyclePopupView.findViewById(R.id.sort_number_high);
    sortLow = sortCyclePopupView.findViewById(R.id.sort_number_low);
    sortCheckmark = sortCyclePopupView.findViewById(R.id.sortCheckmark);

    delete_all_confirm = deleteCyclePopupView.findViewById(R.id.confirm_yes);
    delete_all_cancel = deleteCyclePopupView.findViewById(R.id.confirm_no);
    delete_all_text = deleteCyclePopupView.findViewById(R.id.delete_text);

    getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    getSupportActionBar().setDisplayShowCustomEnabled(true);
    getSupportActionBar().setCustomView(R.layout.custom_bar);
    appHeader = findViewById(R.id.app_header);
    edit_highlighted_cycle = findViewById(R.id.edit_highlighted_cycle);
    delete_highlighted_cycle = findViewById(R.id.delete_highlighted_cycles);
    cancelHighlight = findViewById(R.id.cancel_highlight);
    edit_highlighted_cycle.setVisibility(View.INVISIBLE);
    delete_highlighted_cycle.setVisibility(View.INVISIBLE);
    cancelHighlight.setVisibility(View.INVISIBLE);

    //These Integer Lists hold our millis values for each round.
    customSetTime = new ArrayList<>();
    customBreakTime = new ArrayList<>();
    breaksOnlyTime = new ArrayList<>();
    pomValuesTime = new ArrayList<>();
    //These String Lists hold String conversions (e.g. 1:05) of our Integer lists, used for display purposes.
    convertedSetsList = new ArrayList<>();
    convertedBreaksList = new ArrayList<>();
    convertedBreaksOnlyList = new ArrayList<>();
    convertedPomList = new ArrayList<>();
    //These String lists hold concatenated Strings of COMPLETE cycles (e.g. 2:00, 4:30, etc.), used for storing the cycles in our database.
    setsArray = new ArrayList<>();
    breaksArray = new ArrayList<>();
    breaksOnlyArray = new ArrayList<>();
    pomArray = new ArrayList<>();
    //These String lists hold each cycle's title.
    customTitleArray = new ArrayList<>();
    breaksOnlyTitleArray = new ArrayList<>();
    pomTitleArray = new ArrayList<>();
    //If highlighting cycles for deletion, contains all POSITIONS (not IDs) of cycles highlighted.
    receivedHighlightPositions = new ArrayList<>();
    infinityArrayOne = new ArrayList<>();
    infinityArrayTwo = new ArrayList<>();

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

    mode = sharedPreferences.getInt("mode", 1);
    setValue = sharedPreferences.getInt("setValue", 30);
    breakValue = sharedPreferences.getInt("breakValue", 30);
    breaksOnlyValue = sharedPreferences.getInt("breakOnlyValue", 30);
    pomValue1 = sharedPreferences.getInt("pomValue1", 1500);
    pomValue2 = sharedPreferences.getInt("pomValue2", 300);
    pomValue3 = sharedPreferences.getInt("pomValue3", 900);
    sortMode = sharedPreferences.getInt("sortMode", 1);
    sortModeBO = sharedPreferences.getInt("sortModeBO", 1);
    sortModePom = sharedPreferences.getInt("sortModePom", 1);

//    setsAreCountingUp = !sharedPreferences.getBoolean("setCountUpMode", false);
//    breaksAreCountingUp = !sharedPreferences.getBoolean("breakCountUpMode", false);


    mHandler.postDelayed(() -> {
      AsyncTask.execute(() -> {
        //Loads database of saved cycles.
        cyclesDatabase = CyclesDatabase.getDatabase(getApplicationContext());
        //Instantiates cycleList object based on sort order. For app launch, this is defaulting to "1", or "most recent."
        queryCycles();
        //Populates our cycle arrays from the database, so our list of cycles are updated from our adapter and notifyDataSetChanged().
        populateCycleList();
        runOnUiThread(()-> {
          //Instantiates saved cycle adapter w/ ALL list values, to be populated based on the mode we're on.
          LinearLayoutManager lm2 = new LinearLayoutManager(getApplicationContext());
          savedCycleAdapter = new SavedCycleAdapter(getApplicationContext(), setsArray, breaksArray, breaksOnlyArray, pomArray, customTitleArray, breaksOnlyTitleArray, pomTitleArray);
          savedCycleRecycler.setAdapter(savedCycleAdapter);
          savedCycleRecycler.setLayoutManager(lm2);
          //Instantiating callbacks from adapter.
          savedCycleAdapter.setItemClick(MainActivity.this);
          savedCycleAdapter.setHighlight(MainActivity.this);
          savedCycleAdapter.setInfinityToggle(MainActivity.this);
          //Setting mode from savedPref so we are on whichever one was previously used.
          savedCycleAdapter.setView(mode);

          //FOR REFERENCE: Main (intent-> Timer) ---> Timer (intent->Main) && (callback to adapter for saved toggles).
          //Receives infinity arrays from Timer class, which are the same ones we passed into Timer when launching it from Main. This is done to prevent them being lost when our activity is recreated.
          Intent intent = getIntent();
          if (intent!=null) {
            infinityArrayOne = intent.getIntegerArrayListExtra("infiniteOne");
            if (infinityArrayOne == null) infinityArrayOne = new ArrayList<>();
            infinityArrayTwo = intent.getIntegerArrayListExtra("infiniteTwo");
            if (infinityArrayTwo == null) infinityArrayTwo = new ArrayList<>();
          }
          if (infinityArrayOne.isEmpty()) {
            for (int i=0; i<setsArray.size(); i++) {
              infinityArrayOne.add(0);
              infinityArrayTwo.add(0);
            }
          }

          if (infinityArrayOne.get(receivedPos)==0) setsAreCountingUp = false; else setsAreCountingUp = true;
          if (infinityArrayTwo.get(receivedPos)==0) breaksAreCountingUp = false; else breaksAreCountingUp = true;
          //Sends infinity arrays to our saved cycle adapter.
          savedCycleAdapter.receiveInfinityMode(infinityArrayOne, infinityArrayTwo);

          //Calling this by default, so any launch of Main will update our cycle list, since populateCycleList(), called after adapter is instantiated, is what populates our arrays.
          savedCycleAdapter.notifyDataSetChanged();
        });
      });
    },50);

    //Adapter and Recycler for round views within our editCycles popUp.
    LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext());
    RecyclerView roundRecycler = editCyclesPopupView.findViewById(R.id.round_list_recycler);
    cycleRoundsAdapter = new CycleRoundsAdapter(convertedSetsList, convertedBreaksList, convertedBreaksOnlyList, convertedPomList);
    roundRecycler.setAdapter(cycleRoundsAdapter);
    roundRecycler.setLayoutManager(lm);

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
      }
    };
    first_value_edit.addTextChangedListener(textWatcher);
    first_value_edit_two.addTextChangedListener(textWatcher);
    second_value_edit.addTextChangedListener(textWatcher);
    second_value_edit_two.addTextChangedListener(textWatcher);
    third_value_edit.addTextChangedListener(textWatcher);
    third_value_edit_two.addTextChangedListener(textWatcher);

    countUpMode(true); countUpMode(false);

    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
      @Override
      public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
          case 0:
            mode = 1;
            //Sets the recyclerView classes for each mode on both adapters.
            savedCycleAdapter.setView(1);
            cycleRoundsAdapter.setMode(1);
            break;
          case 1:
            mode = 2;
            savedCycleAdapter.setView(2);
            cycleRoundsAdapter.setMode(2);
            break;
          case 2:
            mode = 3;
            savedCycleAdapter.setView(3);
            cycleRoundsAdapter.setMode(3);
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
        //Dismisses editCycle popupp when switching tabs.
        if (editCyclesPopupWindow.isShowing()) editCyclesPopupWindow.dismiss();
        //Repopulates savedCycle recyclerView when switching tabs.
        savedCycleAdapter.notifyDataSetChanged();
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


    //Brings up editCycle popUp to create new Cycle.
    fab.setOnClickListener(v -> {
      //Clears timer arrays so they can be freshly populated.
      clearTimerArrays();
      //Brings up menu to add/subtract rounds to new cycle.
      editCyclesPopupWindow.showAsDropDown(tabLayout);
      //Boolean set to true indicates a new, non-database-saved cycle is populating our editCyclesPopup. This is primarily used for onBackPressed, to determine whether to save it as a new entry, or update its current position in the database.
      onNewCycle = true;
    });

    ////--ActionBar Item onClicks START--////
    edit_highlighted_cycle.setOnClickListener(v-> {
      editCyclesPopupWindow.showAsDropDown(tabLayout);
      AsyncTask.execute(()-> {
        queryCycles();
        //Button is only active if list contains exactly ONE position (i.e. only one cycle is selected). Here, we set our retrieved position (same as if we simply clicked a cycle to launch) to the one passed in from our highlight.
        receivedPos = Integer.parseInt(receivedHighlightPositions.get(0));
        //Uses this single position to retrieve cycle and populate timer arrays.
        retrieveCycle();
        //Our convertedXX lists are used to populate the recyclerView we use in our editCycles popUp. We retrieve their values here from the database entry received above.
        switch (mode) {
          case 1:
            for (int i=0; i<customSetTime.size(); i++) convertedSetsList.add(convertSeconds(customSetTime.get(i)/1000));
            for (int i=0; i<customBreakTime.size(); i++) convertedBreaksList.add(convertSeconds(customBreakTime.get(i)/1000));
            break;
          case 2:
            for (int i=0; i<breaksOnlyTime.size(); i++) convertedBreaksOnlyList.add(convertSeconds(breaksOnlyTime.get(i)/1000));
            break;
          case 3:
            for (int i=0; i<pomValuesTime.size(); i++) convertedPomList.add(convertSeconds(pomValuesTime.get(i)/1000));
            break;
        }
        runOnUiThread(()-> {
          //Setting editText title.
          cycle_name_edit.setText(cycleTitle);
          //Updating adapter views.
          cycleRoundsAdapter.notifyDataSetChanged();
          //Removing highlights.
          savedCycleAdapter.removeHighlight(true);
          //Boolean set to false indicates that a current database-saved cycle is populating our editCyclesPopup.
          onNewCycle = false;
        });
      });
    });

    delete_highlighted_cycle.setOnClickListener(v-> {
      AsyncTask.execute(()-> {
        ArrayList<String> tempPos = new ArrayList<>(receivedHighlightPositions);
        queryCycles();
        for (int i=0; i<receivedHighlightPositions.size(); i++) {
          receivedPos = Integer.parseInt(receivedHighlightPositions.get(i));
          deleteCycle(false);
          tempPos.remove(String.valueOf(receivedPos));
        }
        //Calls new database entries and updates our adapter's recyclerView.
        runOnUiThread(() -> {
          queryCycles();
          populateCycleList();
          savedCycleAdapter.notifyDataSetChanged();
          receivedHighlightPositions = tempPos;
          //If there are no cycles left, cancel highlight mode. If there are any left, simply remove all highlights.
          if (receivedHighlightPositions.size()>0) savedCycleAdapter.removeHighlight(false); else {
            cancelHighlight.setVisibility(View.INVISIBLE);
            edit_highlighted_cycle.setVisibility(View.INVISIBLE);
            delete_highlighted_cycle.setVisibility(View.INVISIBLE);
            appHeader.setVisibility(View.VISIBLE);
            savedCycleAdapter.removeHighlight(true);
          }
        });
      });
    });

    //Turns off our cycle highlight mode from adapter.
    cancelHighlight.setOnClickListener(v-> {
      cancelHighlight.setVisibility(View.INVISIBLE);
      delete_highlighted_cycle.setVisibility(View.INVISIBLE);
      appHeader.setVisibility(View.VISIBLE);
      savedCycleAdapter.removeHighlight(true);
      savedCycleAdapter.notifyDataSetChanged();
    });
    ////--ActionBar Item onClicks END--////

    ////--EditCycles Menu Item onClicks START--////
    //Dismisses editText views if we click within the unpopulated area of popUp.
    editCyclesPopupView.setOnClickListener(v-> {
      removeEditViews();
      editCycleViews();
    });

    //Launched from editCyclePopUp and calls TimerInterface w/ new cycle info.
    start_timer.setOnClickListener(v-> {
      AsyncTask.execute(()-> {
        launchTimerCycle(true);
      });
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
    ////--EditCycles Menu Item OnClicks END--////


    delete_all_confirm.setOnClickListener(v-> {
      //Removes confirmation window.
      if (deleteCyclePopupWindow.isShowing()) deleteCyclePopupWindow.dismiss();
      AsyncTask.execute(() -> {
        //Retrieved current database values.
        queryCycles();
        //Deletes all cycles.
        deleteCycle(true);
        //Mew instance (which will be empty) of whichever Cycles entity we're on.
        queryCycles();
        //Clears adapter arrays and populates recyclerView with (nothing) since arrays are now empty. Also called notifyDataSetChanged().
        populateCycleList();
        savedCycleAdapter.notifyDataSetChanged();
      });
    });

    delete_all_cancel.setOnClickListener(v-> {
      //Removes confirmation window.
      if (deleteCyclePopupWindow.isShowing()) deleteCyclePopupWindow.dismiss();
    });
  }

  //Clears both timer millis arrays and their converted String arrays.
  public void clearTimerArrays() {
    customSetTime.clear();
    customBreakTime.clear();
    breaksOnlyTime.clear();
    pomValuesTime.clear();
    convertedSetsList.clear();
    convertedBreaksList.clear();
    convertedBreaksOnlyList.clear();
    convertedPomList.clear();
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
      if (mode == 1) {
        breakValue = (int) ((editBreakMinutes * 60) + editBreakSeconds);
        second_value_textView.setText(convertCustomTextView(breakValue));
      }
      else if (mode == 2) {
        breaksOnlyValue = (int) ((editBreakMinutes * 60) + editBreakSeconds);
        second_value_textView.setText(convertCustomTextView(breaksOnlyValue));
      }

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
        second_value_textView.setVisibility(View.VISIBLE);
        s2.setText(R.string.break_time);
        if (mode==1) {
          s1.setVisibility(View.VISIBLE);
          s1.setText(R.string.set_time);
          plus_first_value.setVisibility(View.VISIBLE);
          minus_first_value.setVisibility(View.VISIBLE);
          s1.setText(R.string.set_time);
        } else {
          s1.setVisibility(View.INVISIBLE);
          first_value_textView.setVisibility(View.INVISIBLE);
          plus_first_value.setVisibility(View.INVISIBLE);
          minus_first_value.setVisibility(View.INVISIBLE);
          upDown_arrow_two.setVisibility(View.GONE);
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

  public void adjustCustom(boolean adding) {
    if (adding) {
      switch (mode) {
        case 1:
          if (customSetTime.size() < 8) {
            customSetTime.add(setValue * 1000);
            customBreakTime.add(breakValue * 1000);
            //String array that is used to convert to a single gSon String to store in database.
            convertedSetsList.add(convertSeconds(setValue));
            convertedBreaksList.add(convertSeconds(breakValue));
          } else {
            Toast.makeText(getApplicationContext(), "Max rounds reached!", Toast.LENGTH_SHORT).show();
            //Return to save on resources. Also to avoid unnecessary fading in Pom mode.
            return;
          }
          break;
        case 2:
          if (breaksOnlyTime.size() < 8) {
            breaksOnlyTime.add(breaksOnlyValue * 1000);
            //String array that is used to convert to a single gSon String to store in database.
            convertedBreaksOnlyList.add(convertSeconds(breaksOnlyValue));
          } else {
            Toast.makeText(getApplicationContext(), "Max rounds reached!", Toast.LENGTH_SHORT).show();
            return;
          }
          break;
        case 3:
          if (pomValuesTime.size() == 0) {
            for (int i = 0; i < 3; i++) {
              pomValuesTime.add(pomValue1 * 1000);
              pomValuesTime.add(pomValue2 * 1000);
            }
            pomValuesTime.add(pomValue1* 1000);
            pomValuesTime.add(pomValue3 * 1000);
            for (int j=0; j<pomValuesTime.size(); j++)  convertedPomList.add(convertSeconds(pomValuesTime.get(j)/1000));
          } else {
            Toast.makeText(getApplicationContext(), "Pomodoro cycle already loaded!", Toast.LENGTH_SHORT).show();
            return;
          }
          break;
      }
      setEditValues();
    } else {
      switch (mode) {
        case 1:
          if (customSetTime.size() > 0) {
            customSetTime.remove(customSetTime.size() - 1);
            customBreakTime.remove(customBreakTime.size() - 1);

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

            convertedBreaksOnlyList.remove(convertedBreaksOnlyList.size()-1);
          } else {
            Toast.makeText(getApplicationContext(), "Nothing left to remove!", Toast.LENGTH_SHORT).show();
            return;
          }
          break;
        case 3:
          //If a cycle exists, disable the timer because we are removing the cycle via our fadeOutDot runnable which will not complete until the fade is done. Adding a cycle will re-enable the timer through populateTimerUI().
          if (pomValuesTime.size() != 0) {
            pomValuesTime.clear();
            convertedPomList.clear();
          } else {
            Toast.makeText(getApplicationContext(), "No Pomodoro cycle to clear!", Toast.LENGTH_SHORT).show();
            return;
          }
          break;
      }
    }
    cycleRoundsAdapter.notifyDataSetChanged();
  }

  public String friendlyString(String altString) {
    altString = altString.replace("\"", "");
    altString = altString.replace("]", "");
    altString = altString.replace("[", "");
    altString = altString.replace(",", " - ");
    return altString;
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
        if (cyclesList.size()==0) currentCycleEmpty = true; else currentCycleEmpty = false;
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
        if (cyclesBOList.size()==0) currentCycleEmpty = true; else currentCycleEmpty = false;
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
        if (pomCyclesList.size()==0) currentCycleEmpty = true; else currentCycleEmpty = false;
        break;
    }
  }

  //Clears STRING arrays, used to populate adapter views, and re-populates them with database values.
  //Remember, if the database has changed we need to call queryCycles() before this or new values will not be retrieved.
  public void populateCycleList() {
    switch (mode) {
      case 1:
        setsArray.clear();
        breaksArray.clear();
        customTitleArray.clear();
        for (int i=0; i<cyclesList.size(); i++) {
          setsArray.add(cyclesList.get(i).getSets());
          breaksArray.add(cyclesList.get(i).getBreaks());
          customTitleArray.add(cyclesList.get(i).getTitle());
        }
        break;
      case 2:
        breaksOnlyArray.clear();
        if (breaksOnlyTitleArray!=null) breaksOnlyTitleArray.clear();
        for (int i=0; i<cyclesBOList.size(); i++) {
          breaksOnlyArray.add(cyclesBOList.get(i).getBreaksOnly());
          breaksOnlyTitleArray.add(cyclesBOList.get(i).getTitle());
        }
        break;
      case 3:
        pomArray.clear();
        for (int i=0; i<pomCyclesList.size(); i++) {
          pomArray.add(pomCyclesList.get(0).getFullCycle());
          pomTitleArray.add(pomCyclesList.get(0).getTitle());
        }
    }
  }

  //Gets instance of our database entity class based on which mode we're in. Converts its String into Integers and populates our timer arrays with them.
  public void retrieveCycle() {
    //Calling cycleList instance based on sort mode.
    queryCycles();
    //Clears old array values.
    clearTimerArrays();
    switch (mode) {
      case 1:
        Cycles cycles = cyclesList.get(receivedPos);
        String[] tempSets = cycles.getSets().split(" - ");
        for (int i=0; i<tempSets.length; i++) customSetTime.add(Integer.parseInt(tempSets[i]));

        String[] tempBreaks = cycles.getBreaks().split(" - ");
        for (int i=0; i<tempBreaks.length; i++) customBreakTime.add(Integer.parseInt(tempBreaks[i]));
        retrievedID = cyclesList.get(0).getId();
        cycleTitle = cycles.getTitle();
        break;
      case 2:
        CyclesBO cyclesBO = cyclesBOList.get(receivedPos);
        String[] tempBreaksOnly = cyclesBO.getBreaksOnly().split(" - ");
        for (int i=0; i<tempBreaksOnly.length; i++) breaksOnlyTime.add(Integer.parseInt(tempBreaksOnly[i]));
        retrievedID = cyclesBOList.get(0).getId();
        cycleTitle = cyclesBO.getTitle();
        break;
      case 3:
        PomCycles pomCycles = pomCyclesList.get(receivedPos);
        pomValuesTime.clear();
        String[] tempPom = pomCycles.getFullCycle().split(" - ");
        for (int i=0; i<tempPom.length; i++) pomValuesTime.add(Integer.parseInt(tempPom[i]));
        retrievedID = pomCyclesList.get(0).getId();
        cycleTitle = pomCycles.getTitle();
        break;
    }
  }

  public void launchTimerCycle(boolean newCycle) {
    //Used for primary key ID of database position, passed into Timer class so we can delete the selected cycle.
    Intent intent = new Intent(MainActivity.this, TimerInterface.class);
    // If we are launching a new cycle, set a conditional for an empty title, a conditional for an empty list, and run the saveCycles() method to automatically save it in our database. For both new and old cycles, send all necessary intents to our Timer class.
    if (newCycle) {
      //Gets current date for use as title in new cycles where title is left empty.
      Calendar calendar = Calendar.getInstance();
      SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMMM d yyyy - hh:mma", Locale.getDefault());
      String date = dateFormat.format(calendar.getTime());

      //If trying to add new cycle and rounds are at 0, pop a toast and exit method. Otherwise, set a title and proceed to intents.
      if ((mode==1 && customSetTime.size()==0) || (mode==2 && breaksOnlyTime.size()==0) || (mode==3 && pomValuesTime.size()==0)) {
        runOnUiThread(()-> Toast.makeText(getApplicationContext(), "Cycle cannot be empty!", Toast.LENGTH_SHORT).show());
        return;
      }
      if (cycle_name_edit.getText().toString().isEmpty()) intent.putExtra("cyclesTitle", date);
      else intent.putExtra("cyclesTitle", cycle_name_edit.getText().toString());
      //Since this is a new Cycle, we automatically save it to database.
      saveCycles(true);
      //Updates the adapter display of saved cycles, since we are adding to it.
      runOnUiThread(() -> populateCycleList());
      //If selecting an existing cycle, call its info and set timer value arrays.
    } else retrieveCycle();

    //For both NEW and RETRIEVED cycles, we send the following intents to TimerInterface.
    switch (mode) {
      case 1:
        intent.putIntegerArrayListExtra("setList", customSetTime);
        intent.putExtra("setsAreCountingUp", setsAreCountingUp);
        intent.putIntegerArrayListExtra("breakList", customBreakTime);
        intent.putExtra("breaksAreCountingUp", breaksAreCountingUp);
        break;
      case 2:
        intent.putIntegerArrayListExtra("breakOnlyList", breaksOnlyTime);
        intent.putExtra("breaksOnlyAreCountingUp", breaksOnlyAreCountingUp);
        break;
      case 3:
        intent.putIntegerArrayListExtra("pomList", pomValuesTime);
        break;
    }
    //Mode used for type of timer.
    intent.putExtra("mode", mode);
    //Sends the current cycle's database position so we can delete it from the Timer class if desired.
    intent.putExtra("passedID", retrievedID);
    //Sends infinity toggle arrays to Timer, so they can be sent back to Main when we come back. This is necessary because we are not using Shared Preferences, and our Main activity is set to recreate itself when exiting from Timer.
    intent.putIntegerArrayListExtra("infiniteOne", infinityArrayOne);
    intent.putIntegerArrayListExtra("infiniteTwo", infinityArrayTwo);
    //Starts Timer class.
    startActivity(intent);
  }

  private void saveCycles(boolean newCycle) {
    //Gets current date for use in empty titles.
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMMM d yyyy - hh:mma", Locale.getDefault());
    String date = dateFormat.format(calendar.getTime());

    //Sets up Strings to save into database.
    Gson gson = new Gson();
    String setString = "";
    String breakString  = "";
    String breakOnlyString = "";
    String pomString = "";
    String cycle_name = cycle_name_edit.getText().toString();
    int cycleID = 0;

    switch (mode) {
      case 1:
        //If coming from FAB button, create a new instance of Cycles. If coming from a position in our database, get the instance of Cycles in that position.
        if (newCycle) cycles = new Cycles(); else if (cyclesList.size()>0) {
          cycleID = cyclesList.get(receivedPos).getId();
          cycles = cyclesDatabase.cyclesDao().loadSingleCycle(cycleID).get(0);
        }
        //Converting our String array of rounds in a cycle to a single String so it can be stored in our database. Saving "Count Down" values regardless of how we're counting, as we want them present when toggling between count up/count down.
        setString = gson.toJson(customSetTime);
        setString = friendlyString(setString);
        breakString = gson.toJson(customBreakTime);
        breakString = friendlyString(breakString);
        //If round list is blank, setString will remain at "", in which case we do not save or update. This is determined after we convert via Json above.
        if (!setString.equals("")) {
          //Adding and inserting into database.
          cycles.setSets(setString);
          cycles.setBreaks(breakString);
          //Only setting timeAdded for NEW cycle. We want our (sort by date) to use the initial time/date.
          if (newCycle) cycles.setTimeAdded(System.currentTimeMillis());
          cycles.setItemCount(customSetTime.size());
          if (!cycle_name.isEmpty()) cycles.setTitle(cycle_name); else cycles.setTitle(date);
          if (newCycle) cyclesDatabase.cyclesDao().insertCycle(cycles); cyclesDatabase.cyclesDao().updateCycles(cycles);
        }
        break;
      case 2:
        //If coming from FAB button, create a new instance of CyclesBO. If coming from a position in our database, get the instance of CyclesBO in that position.
        if (newCycle) cyclesBO = new CyclesBO(); else if (cyclesBOList.size()>0) {
          cycleID = cyclesBOList.get(receivedPos).getId();
          cyclesBO = cyclesDatabase.cyclesDao().loadSingleCycleBO(cycleID).get(0);
        }
        breakOnlyString = gson.toJson(breaksOnlyTime);
        breakOnlyString = friendlyString(breakOnlyString);

        if (!breakOnlyString.equals("")) {
          cyclesBO.setBreaksOnly(breakOnlyString);
          if (newCycle) cyclesBO.setTimeAdded(System.currentTimeMillis());
          cyclesBO.setItemCount(breaksOnlyTime.size());
          if (!cycle_name.isEmpty()) cyclesBO.setTitle(cycle_name); else cyclesBO.setTitle(date);
          if (newCycle) cyclesDatabase.cyclesDao().insertBOCycle(cyclesBO); else cyclesDatabase.cyclesDao().updateBOCycles(cyclesBO);
        }
        break;
      case 3:
        //If coming from FAB button, create a new instance of PomCycles. If coming from a position in our database, get the instance of PomCycles in that position.
        if (newCycle) pomCycles = new PomCycles(); else if (pomCyclesList.size()>0) {
          cycleID = pomCyclesList.get(receivedPos).getId();
          pomCycles = cyclesDatabase.cyclesDao().loadSinglePomCycle(cycleID).get(0);
        }
        pomString = gson.toJson(pomValuesTime);
        pomString = friendlyString(pomString);

        if (!pomString.equals("")) {
          pomCycles.setFullCycle(pomString);
          if (newCycle) pomCycles.setTimeAdded(System.currentTimeMillis());
          if (!cycle_name.isEmpty()) pomCycles.setTitle(cycle_name); else pomCycles.setTitle(date);
          if (newCycle) cyclesDatabase.cyclesDao().insertPomCycle(pomCycles); else cyclesDatabase.cyclesDao().updatePomCycles(pomCycles);
        }
        break;
    }
  }

  private void deleteCycle(boolean deleteAll) {
    int cycleID;
    boolean emptyCycle = false;
    switch (mode) {
      case 1:
        if (!deleteAll) {
          cycleID = cyclesList.get(receivedPos).getId();
          cycles = cyclesDatabase.cyclesDao().loadSingleCycle(cycleID).get(0);
          cyclesDatabase.cyclesDao().deleteCycle(cycles);
        } else if (cyclesList.size()>0) cyclesDatabase.cyclesDao().deleteAllCycles(); else emptyCycle = true;
        break;
      case 2:
        if (!deleteAll) {
          cycleID = cyclesBOList.get(receivedPos).getId();
          cyclesBO = cyclesDatabase.cyclesDao().loadSingleCycleBO(cycleID).get(0);
          cyclesDatabase.cyclesDao().deleteBOCycle(cyclesBO);
        } else if (cyclesBOList.size()>0) cyclesDatabase.cyclesDao().deleteAllBOCycles(); else emptyCycle = true;
        break;
      case 3:
        if (!deleteAll) {
          cycleID = pomCyclesList.get(receivedPos).getId();
          pomCycles = cyclesDatabase.cyclesDao().loadSinglePomCycle(cycleID).get(0);
          cyclesDatabase.cyclesDao().deletePomCycle(pomCycles);
        } else if (pomCyclesList.size()>0) cyclesDatabase.cyclesDao().deleteAllPomCycles(); else emptyCycle = true;
        break;
    }
    if (emptyCycle){
      runOnUiThread(() -> {
        Toast.makeText(getApplicationContext(), "Nothing saved!", Toast.LENGTH_SHORT).show();
      });
    }
  }
}