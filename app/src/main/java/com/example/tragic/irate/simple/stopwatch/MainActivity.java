package com.example.tragic.irate.simple.stopwatch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TableRow;
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
import java.util.Timer;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings({"depreciation"})
public class MainActivity extends AppCompatActivity implements SavedCycleAdapter.onCycleClickListener, SavedCycleAdapter.onHighlightListener, CycleRoundsAdapter.onFadeFinished, CycleRoundsAdapterTwo.onFadeFinished {

  ConstraintLayout cl;
  SharedPreferences sharedPreferences;
  SharedPreferences.Editor prefEdit;
  View tabView;
  View mainView;
  Gson gson;

  ImageButton fab;
  ImageButton stopwatch;
  ImageView sortCheckmark;
  TextView emptyCycleList;

  CyclesDatabase cyclesDatabase;
  Cycles cycles;
  CyclesBO cyclesBO;
  PomCycles pomCycles;
  List<Cycles> cyclesList;
  List<CyclesBO> cyclesBOList;
  List<PomCycles> pomCyclesList;
  boolean isNewCycle;

  RecyclerView roundRecycler;
  RecyclerView roundRecyclerTwo;
  RecyclerView savedCycleRecycler;
  CycleRoundsAdapter cycleRoundsAdapter;
  CycleRoundsAdapterTwo cycleRoundsAdapterTwo;
  View roundListDivider;
  SavedCycleAdapter savedCycleAdapter;
  View deleteCyclePopupView;
  View sortCyclePopupView;
  View savedCyclePopupView;
  View editCyclesPopupView;
  View settingsPopupView;
  boolean currentCycleEmpty;

  PopupWindow sortPopupWindow;
  PopupWindow savedCyclePopupWindow;
  PopupWindow deleteCyclePopupWindow;
  PopupWindow editCyclesPopupWindow;
  PopupWindow settingsPopupWindow;

  TextView sortAlphaStart;
  TextView sortAlphaEnd;
  TextView sortRecent;
  TextView sortNotRecent;
  TextView sortHigh;
  TextView sortLow;
  TextView delete_all_text;
  Button delete_all_confirm;
  Button delete_all_cancel;
  TextView appHeader;
  ImageButton edit_highlighted_cycle;
  ImageButton save_highlighted_cycle;
  ImageButton delete_highlighted_cycle;
  ImageButton cancelHighlight;
  TextView sort_text;
  ImageView global_settings;

  int mode = 1;
  int savedMode;
  int sortMode = 1;
  int sortModePom = 1;
  int sortHolder = 1;
  int receivedPos;
  int retrievedID;
  String cycleTitle = "";
  List<String> receivedHighlightPositions;

  TextView cycle_name_text;
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
  ImageView setsInfinity;
  ImageView breaksInfinity;
  View top_anchor;
  ImageButton start_timer;

  ArrayList<Integer> workoutTime;
  ArrayList<String> convertedWorkoutTime;
  ArrayList<String> workoutCyclesArray;
  ArrayList<String> workoutTitle;
  ArrayList<String> workoutTitleArray;
  ArrayList<Integer> pomValuesTime;
  ArrayList<String> convertedPomList;
  ArrayList<String> pomTitleArray;
  ArrayList<String> pomArray;
  String workoutString;
  String roundTypeString;
  String pomString;

  ArrayList<Integer> typeOfRound;
  ArrayList<String> typeOfRoundArray;
  boolean setsSelected = true;
  boolean breaksSelected;
  int roundType;
  ArrayList<String> roundHolderOne;
  ArrayList<String> roundHolderTwo;
  ArrayList<Integer> typeHolderOne;
  ArrayList<Integer> typeHolderTwo;

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
  float editTextViewPosX;
  boolean firstRowHighlighted;
  boolean secondRowHighlighted;
  boolean thirdRowHighlighted;

  Handler mHandler;
  Runnable valueSpeed;
  Runnable changeFirstValue;
  Runnable changeSecondValue;
  Runnable changeThirdValue;
  Runnable adjustRoundDelay;

  boolean editListener;
  InputMethodManager inputMethodManager;

  boolean onNewCycle;
  boolean editingCycle;

  AlphaAnimation fadeIn;
  AlphaAnimation fadeOut;
  Intent intent;
  View roundView;
  TextView round_count;
  TextView round_value;
  ConstraintLayout.LayoutParams recyclerLayoutOne;
  ConstraintLayout.LayoutParams recyclerLayoutTwo;
  ConstraintLayout roundRecyclerLayout;
  int FADE_IN_HIGHLIGHT_MODE = 1;
  int FADE_OUT_HIGHLIGHT_MODE = 2;
  int FADE_IN_EDIT_CYCLE = 3;
  int FADE_OUT_EDIT_CYCLE = 4;

  String timeCompare;
  String typeCompare;
  String titleCompare;
  boolean roundIsFading;

  //Todo: Add fade/ripple effects to buttons and other stuff that would like it.
  //Todo: Option to set "base" progressBar for count-up (options section in menu?). Simply change progressBarValueHolder.
  //Todo: Auto save feature (mainly for total times) when force-closing app. Best way may simply be to use sharedPref and constantly update it.
  //Todo: Possible drag/drop switch for round order.
  //Todo: Highlight sets/breaks and have a single set of up/down and +/- buttons for whichever is selected.
  //Todo: Save total sets/breaks and completed by day option?
  //Todo: Add fades to adapterView lists (i.e. like Google's stopwatch).
  //Todo: Letter -> Number soft kb is a bit choppy.
  //Todo: For performance: minimize db calls (e.g. if a list has already been saved and you just need an adapter populated, simply use new array lists).
  //Todo: Make sure when using intents, especially from Timer -> Main, that they're sent every time we exit the class (e.g. deleting the current cycle, onBackPressed, exitTimer(), etc.)

  //Todo: editCycle popUp precludes action bar button use at the moment because it retains app focus. We can't remove that without borking other stuff (e.g. soft keyboard use).
  //Todo: Could long svg files be a lag contributor?
  //Todo: Load/draw canvas in aSync for performance?
  //Todo: TDEE in sep popup w/ tabs.
  //Todo: Make sure sort checkmark positions work on different size screens.
  //Todo: Fade animation for all menus that don't have them yet (e.g. onOptions).
  //Todo: Add taskbar notification for timers.
  //Todo: Add color scheme options.
  //Todo: All DB calls in aSync.
  //Todo: Rename app, of course.
  //Todo: Add onOptionsSelected dots for About, etc.
  //Todo: Repository for db. Look at Executor/other alternate thread methods. Would be MUCH more streamlined on all db calls, but might also bork order of operations when we need to call other stuff under UI thread right after.
  //Todo: Make sure number pad is dismissed when switching to stopwatch mode.
  //Todo: IMPORTANT: Resolve landscape mode vs. portrait. Set to portrait-only in manifest at present. Likely need a second layout for landscape mode. Also check that lifecycle is stable.
  //Todo: Test everything 10x.

   //Todo: REMEMBER, always call queryCycles() to get a cyclesList reference, otherwise it won't sync w/ the current sort mode.
   //Todo: REMINDER, Try next app w/ Kotlin.

  @Override
  public void onBackPressed() {
    //Minimizes activity.
    moveTaskToBack(true);
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
    //Sets "highlight mode" actionBar buttons to Visible if entering mode (i.e. selecting first item).
    if (addButtons) {
      //If at least one item in our cycle list, enable the edit button.
      edit_highlighted_cycle.setEnabled(listOfPositions.size() <= 1);
      //Fading app name text + sort button out, edit and delete buttons in.
      fadeEditCycleButtonsIn(FADE_IN_HIGHLIGHT_MODE);
    }
  }

  //This callback method works for both round adapters.
  @Override
  public void fadeHasFinished() {
    //When adapter fade on round has finished, execute method to remove the round from adapter list/holders and refresh the adapter display. If we click to remove another round before fade is done, fade gets cancelled, restarted on next position, and this method is also called to remove previous round.
    removeRound();
    //When fade animation for removing Pomodoro cycle is finished in adapter, its listener calls back here where we remove the cycle's values and update adapter w/ empty list.
    if (mode==3) {
      pomValuesTime.clear();
      convertedPomList.clear();
      cycleRoundsAdapter.notifyDataSetChanged();
    }
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
                } else {
                  delete_all_text.setText(R.string.delete_all_cycles);
                  deleteCyclePopupWindow.showAtLocation(cl, Gravity.CENTER, 0, 0);
                }
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
    tabView = findViewById(R.id.tabLayout);

    TabLayout tabLayout = findViewById(R.id.tabLayout);
    tabLayout.addTab(tabLayout.newTab().setText("Workouts"));
    tabLayout.addTab(tabLayout.newTab().setText("Pomodoro"));

    gson = new Gson();
    fab = findViewById(R.id.fab);
    stopwatch = findViewById(R.id.stopwatch_button);
    emptyCycleList = findViewById(R.id.empty_cycle_list);
    savedCycleRecycler = findViewById(R.id.cycle_list_recycler);

    LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    savedCyclePopupView = inflater.inflate(R.layout.saved_cycles_layout, null);
    deleteCyclePopupView = inflater.inflate(R.layout.delete_cycles_popup, null);
    sortCyclePopupView = inflater.inflate(R.layout.sort_popup, null);
    editCyclesPopupView = inflater.inflate(R.layout.editing_cycles, null);
    settingsPopupView = inflater.inflate(R.layout.settings_popup, null);

    savedCyclePopupWindow = new PopupWindow(savedCyclePopupView, 800, 1200, true);
    deleteCyclePopupWindow = new PopupWindow(deleteCyclePopupView, 750, 375, true);
    sortPopupWindow = new PopupWindow(sortCyclePopupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
    editCyclesPopupWindow = new PopupWindow(editCyclesPopupView, WindowManager.LayoutParams.MATCH_PARENT, 1430, true);
    settingsPopupWindow = new PopupWindow(settingsPopupView, 700, 1540, true);

    savedCyclePopupWindow.setAnimationStyle(R.style.WindowAnimation);
    deleteCyclePopupWindow.setAnimationStyle(R.style.WindowAnimation);
    sortPopupWindow.setAnimationStyle(R.style.SlideTopAnimation);
    editCyclesPopupWindow.setAnimationStyle(R.style.WindowAnimation);
    settingsPopupWindow.setAnimationStyle(R.style.SlideLeftAnimation);

    cl = new ConstraintLayout(this);
    roundView = inflater.inflate(R.layout.mode_one_rounds, null);
    round_count = roundView.findViewById(R.id.round_count);
    round_value = roundView.findViewById(R.id.workout_rounds);

    cycle_name_text = editCyclesPopupView.findViewById(R.id.cycle_name_text);
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
    setsInfinity = editCyclesPopupView.findViewById(R.id.s1_up);
    breaksInfinity = editCyclesPopupView.findViewById(R.id.s2_up);
    top_anchor = editCyclesPopupView.findViewById(R.id.top_anchor);
    start_timer = editCyclesPopupView.findViewById(R.id.start_timer);
    setsInfinity.setAlpha(0.3f);
    breaksInfinity.setAlpha(0.3f);

    sortAlphaStart = sortCyclePopupView.findViewById(R.id.sort_title_start);
    sortAlphaEnd = sortCyclePopupView.findViewById(R.id.sort_title_end);
    sortRecent = sortCyclePopupView.findViewById(R.id.sort_most_recent);
    sortNotRecent = sortCyclePopupView.findViewById(R.id.sort_least_recent);
    sortHigh = sortCyclePopupView.findViewById(R.id.sort_number_high);
    sortLow = sortCyclePopupView.findViewById(R.id.sort_number_low);
    sortCheckmark = sortCyclePopupView.findViewById(R.id.sortCheckmark);

    delete_all_confirm = deleteCyclePopupView.findViewById(R.id.confirm_yes);
    delete_all_cancel = deleteCyclePopupView.findViewById(R.id.confirm_no);
    delete_all_text = deleteCyclePopupView.findViewById(R.id.delete_text);

    //Custom Action Bar.
    getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    getSupportActionBar().setDisplayShowCustomEnabled(true);
    getSupportActionBar().setCustomView(R.layout.custom_bar);
    appHeader = findViewById(R.id.app_header);
    edit_highlighted_cycle = findViewById(R.id.edit_highlighted_cycle);
    save_highlighted_cycle = findViewById(R.id.save_highlighted_cycle);
    delete_highlighted_cycle = findViewById(R.id.delete_highlighted_cycles);
    cancelHighlight = findViewById(R.id.cancel_highlight);
    sort_text = findViewById(R.id.sort_text);
    global_settings = findViewById(R.id.global_settings);
    save_highlighted_cycle.setVisibility(View.INVISIBLE);
    edit_highlighted_cycle.setVisibility(View.INVISIBLE);
    delete_highlighted_cycle.setVisibility(View.INVISIBLE);
    cancelHighlight.setVisibility(View.INVISIBLE);
    cycle_name_text.setVisibility(View.INVISIBLE);

    workoutTime = new ArrayList<>();
    convertedWorkoutTime = new ArrayList<>();
    workoutTitle = new ArrayList<>();
    workoutCyclesArray = new ArrayList<>();
    typeOfRound = new ArrayList<>();
    typeOfRoundArray = new ArrayList<>();

    roundHolderOne = new ArrayList<>();
    roundHolderTwo = new ArrayList<>();
    typeHolderOne = new ArrayList<>();
    typeHolderTwo = new ArrayList<>();

    //These Integer Lists hold our millis values for each round.
    pomValuesTime = new ArrayList<>();
    //These String Lists hold String conversions (e.g. 1:05) of our Integer lists, used for display purposes in recyclerView via adapter.
    convertedPomList = new ArrayList<>();
    //These String lists hold concatenated Strings of COMPLETE cycles (e.g. [2:00, 4:30., 3:15] etc.), used for storing the cycles in our database.
    pomArray = new ArrayList<>();
    //These String lists hold each cycle's title.
    workoutTitleArray = new ArrayList<>();
    pomTitleArray = new ArrayList<>();
    //If highlighting cycles for deletion, contains all POSITIONS (not IDs) of cycles highlighted.
    receivedHighlightPositions = new ArrayList<>();
    //Database entity lists.
    cyclesList = new ArrayList<>();
    cyclesBOList = new ArrayList<>();
    pomCyclesList = new ArrayList<>();
    cycles = new Cycles();
    cyclesBO = new CyclesBO();
    pomCycles = new PomCycles();

    mHandler = new Handler();
    inputMethodManager =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    sharedPreferences = getApplicationContext().getSharedPreferences("pref", 0);
    prefEdit = sharedPreferences.edit();

    //Default values in WHOLE seconds. Multiplied * 1000 for our millis values.
    setValue = sharedPreferences.getInt("setValue", 30);
    breakValue = sharedPreferences.getInt("breakValue", 30);
    breaksOnlyValue = sharedPreferences.getInt("breakOnlyValue", 30);
    pomValue1 = sharedPreferences.getInt("pomValue1", 1500);
    pomValue2 = sharedPreferences.getInt("pomValue2", 300);
    pomValue3 = sharedPreferences.getInt("pomValue3", 900);
    sortMode = sharedPreferences.getInt("sortMode", 1);
    sortModePom = sharedPreferences.getInt("sortModePom", 1);

    fadeIn = new AlphaAnimation(0.0f, 1.0f);
    fadeOut = new AlphaAnimation(1.0f, 0.0f);
    fadeIn.setDuration(750);
    fadeOut.setDuration(750);
    fadeIn.setFillAfter(true);
    fadeOut.setFillAfter(true);

    //Retrieves checkmark position for sort popup.
    setSortCheckmark();

    //Receives mode from Timer activity and sets the previously used Tab when we are coming back to Main. Default is 1 since modes are 1++, and getTab is mode -1 because we use 0++ indices for each tab.
    intent = getIntent();
    if (intent!= null) {
      //Last mode selected.
      savedMode = intent.getIntExtra("savedMode", 0);
      mode = intent.getIntExtra("mode", 1);
      //If stopwatch is launched, mode is 4. Since we only update our respective adapter lists in modes 1 - 3, we reset the mode to the last one used so that the proper lists can be updated.
      if (mode==4) mode = savedMode;
      TabLayout.Tab tab = null;
      //Sets tab to last one used.
      if (mode==1) tab = tabLayout.getTabAt(0); else if (mode==3) tab = tabLayout.getTabAt(1);
      if (tab != null){
        tab.select();
      }
    }

    AsyncTask.execute(() -> {
      //Loads database of saved cycles.
      cyclesDatabase = CyclesDatabase.getDatabase(getApplicationContext());
      //Calls instance of Cycle entity list based on sort mode.v
      queryCycles();

      //Populates our cycle arrays from the database, so our list of cycles are updated from our adapter and notifyDataSetChanged().
      populateCycleList(true);
      runOnUiThread(()-> {
        checkEmptyCycles();
        //Adapter and Recycler for round views within our editCycles popUp.
        LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext());
        LinearLayoutManager lm2 = new LinearLayoutManager(getApplicationContext());
        LinearLayoutManager lm3 = new LinearLayoutManager(getApplicationContext());

        //Instantiates saved cycle adapter w/ ALL list values, to be populated based on the mode we're on.
        savedCycleAdapter = new SavedCycleAdapter(getApplicationContext(), workoutCyclesArray, typeOfRoundArray, pomArray, workoutTitleArray, pomTitleArray);
        savedCycleRecycler.setAdapter(savedCycleAdapter);
        savedCycleRecycler.setLayoutManager(lm);
        //Instantiating callbacks from adapter.
        savedCycleAdapter.setItemClick(MainActivity.this);
        savedCycleAdapter.setHighlight(MainActivity.this);
        //Setting mode from savedPref so we are on whichever one was previously used.
        savedCycleAdapter.setView(mode);

        //Calling this by default, so any launch of Main will update our cycle list, since populateCycleList(), called after adapter is instantiated, is what populates our arrays.
        savedCycleAdapter.notifyDataSetChanged();

        //Our two cycle round adapters.
        cycleRoundsAdapter = new CycleRoundsAdapter(getApplicationContext(), roundHolderOne, typeHolderOne, convertedPomList);
        cycleRoundsAdapterTwo = new CycleRoundsAdapterTwo(getApplicationContext(), roundHolderTwo, typeHolderTwo);
        cycleRoundsAdapter.fadeFinished(MainActivity.this);
        cycleRoundsAdapterTwo.fadeFinished(MainActivity.this);
        //Only first adapter is used for Pom mode, so only needs to be set here.
        cycleRoundsAdapter.setMode(mode);

        roundListDivider = editCyclesPopupView.findViewById(R.id.round_list_divider);
        roundListDivider.setVisibility(View.GONE);
        roundRecycler = editCyclesPopupView.findViewById(R.id.round_list_recycler);
        roundRecyclerTwo = editCyclesPopupView.findViewById(R.id.round_list_recycler_two);
        roundRecycler.setAdapter(cycleRoundsAdapter);
        roundRecyclerTwo.setAdapter(cycleRoundsAdapterTwo);
        roundRecycler.setLayoutManager(lm2);
        roundRecyclerTwo.setLayoutManager(lm3);
        //Sets round adapter view to correct mode (necessary when coming back via Intent from a timer).

        roundRecyclerLayout = editCyclesPopupView.findViewById(R.id.round_recycler_layout);
        //Rounds begin unpopulated, so remove second recycler view.
        roundRecyclerTwo.setVisibility(View.GONE);
        //Retrieves layout parameters for our recyclerViews. Used to adjust position based on size.
        recyclerLayoutOne = (ConstraintLayout.LayoutParams) roundRecycler.getLayoutParams();
        recyclerLayoutTwo = (ConstraintLayout.LayoutParams) roundRecyclerTwo.getLayoutParams();
        //Using exclusively programmatic layout params for round recyclerViews. Setting defaults. Second will never change.
        recyclerLayoutOne.leftMargin = 240;
        recyclerLayoutTwo.leftMargin = 450;

        //Sets all editTexts to GONE, and then populates them + textViews based on mode.
        removeEditTimerViews(false);
        editCycleViews();
        convertEditTime(true);
        setEditValues();
      });
    });

    //Listens to all editTexts for changes.
    TextWatcher editTextWatcher = new TextWatcher() {
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

    //Watches editText title box and passes its value into the String that gets saved/updated in database.
    TextWatcher titleTextWatcher = new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }
      @Override
      public void afterTextChanged(Editable s) {
        cycleTitle = cycle_name_edit.getText().toString();
      }
    };

    cycle_name_edit.addTextChangedListener(titleTextWatcher);
    first_value_edit.addTextChangedListener(editTextWatcher);
    first_value_edit_two.addTextChangedListener(editTextWatcher);
    second_value_edit.addTextChangedListener(editTextWatcher);
    second_value_edit_two.addTextChangedListener(editTextWatcher);
    third_value_edit.addTextChangedListener(editTextWatcher);
    third_value_edit_two.addTextChangedListener(editTextWatcher);

    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
      @Override
      public void onTabSelected(TabLayout.Tab tab) {
          //Running on another thread because we're fetching DB values and we want to keep the UI in sync.
          AsyncTask.execute(()-> {
              switch (tab.getPosition()) {
                  case 0:
                      mode = 1;
                      //Sets the recyclerView classes for each mode adapters.
                      savedCycleAdapter.setView(1);
                      cycleRoundsAdapter.setMode(1);
                      break;
                  case 1:
                      mode = 3;
                      savedCycleAdapter.setView(3);
                      cycleRoundsAdapter.setMode(3);
                      break;
              }
              queryCycles();
              //UI views change, so running on main thread.
            runOnUiThread(()-> {
                  //Sets all editTexts to GONE, and then populates them + textViews based on mode.
                  removeEditTimerViews(false);
                  editCycleViews();
                  populateCycleList(true);
                  //Toggles "empty cycle" text if adapter list is empty.
                  checkEmptyCycles();
                  savedCycleAdapter.notifyDataSetChanged();
                  if (mode==1) {
                      sortHigh.setVisibility(View.VISIBLE);
                      sortLow.setVisibility(View.VISIBLE);
                  } else {
                      sortHigh.setVisibility(View.GONE);
                      sortLow.setVisibility(View.GONE);
                  }
              });
          });
      }

      @Override
      public void onTabUnselected(TabLayout.Tab tab) {
        //Dismisses editCycle popup when switching tabs.
        if (editCyclesPopupWindow.isShowing()) editCyclesPopupWindow.dismiss();
        //Turning highlight mode off since we are moving to a new tab.
        savedCycleAdapter.removeHighlight(true);
      }

      @Override
      public void onTabReselected(TabLayout.Tab tab) {
      }
    });

    //Caps and sets editText values when clicking outside (exiting) the editText box.
    editCyclesPopupView.setOnClickListener(v-> {
      convertEditTime(true);
    });

    //Sets a listener on our editCycles popup.
    editCyclesPopupView.setOnTouchListener((v, event) -> {
      //Dismisses editText views if we click within the unpopulated area of popUp. Replaces them w/ textViews.
      removeEditTimerViews(true);
      //Toggles coloring and row selection.
      if (event.getAction()==MotionEvent.ACTION_DOWN) {
        float xPos = event.getX();
        float yPos = event.getY();
        if (xPos<=350) {
          //Sets highlights and infinity modes for first row.
          if (yPos>=150 && yPos<=275) {
            if (!firstRowHighlighted) {
              breaksSelected = false;
              setsSelected = true;
              firstRowHighlighted = true;
              secondRowHighlighted = false;
              //If first row is highlighted, second row should un-highlight.
              breaksInfinity.setAlpha(0.3f);
              rowSelect(s1, first_value_textView, first_value_edit, first_value_edit_two, first_value_sep, plus_first_value, minus_first_value, Color.GREEN);
              rowSelect(s2, second_value_textView, second_value_edit, second_value_edit_two, second_value_sep, plus_second_value, minus_second_value, Color.WHITE);
            }
          }
          //Sets highlights and infinity modes for first row.
          if (yPos>=300 && yPos<=425) {
            if (!secondRowHighlighted) {
              setsSelected = false;
              breaksSelected = true;
              secondRowHighlighted = true;
              firstRowHighlighted = false;
              setsInfinity.setAlpha(0.3f);
              rowSelect(s2, second_value_textView, second_value_edit, second_value_edit_two, second_value_sep, plus_second_value, minus_second_value, Color.RED);
              rowSelect(s1, first_value_textView, first_value_edit, first_value_edit_two, first_value_sep, plus_first_value, minus_first_value, Color.WHITE);
            }
          }
        }
        //Hides soft keyboard by using a token of the current editCycleView.
        inputMethodManager.hideSoftInputFromWindow(editCyclesPopupView.getWindowToken(), 0);
      }
      return false;
    });

    //Caps and sets editText values. Only spot that takes focus outside of the view itself (above). Needs to be onTouch to register first click, and false so event is not consumed.
    cycle_name_edit.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction()==MotionEvent.ACTION_DOWN) {
          convertEditTime(true);
        }
        return false;
      }
    });

    global_settings.setOnClickListener(v-> {
      settingsPopupWindow.showAtLocation(cl, Gravity.NO_GRAVITY, 0, 240);
    });

      //Brings up editCycle popUp to create new Cycle.
    fab.setOnClickListener(v -> {
      resetRows();
      //Clears round adapter arrays so they can be freshly populated.
      clearRoundAdapterArrays();
      //Clears the two lists of actual timer values we are populating.
      workoutTime.clear();
      typeOfRound.clear();
      //Updates round adapters so lists show as cleared.
      cycleRoundsAdapter.notifyDataSetChanged();
      cycleRoundsAdapterTwo.notifyDataSetChanged();
      //Removed round divider.
      roundListDivider.setVisibility(View.GONE);
      //Brings up menu to add/subtract rounds to new cycle.
      editCyclesPopupWindow.showAsDropDown(tabLayout);
      //Boolean set to true indicates a new, non-database-saved cycle is populating our editCyclesPopup. This is primarily used for onBackPressed, to determine whether to save it as a new entry, or update its current position in the database.
      onNewCycle = true;
    });

    stopwatch.setOnClickListener(v-> {
      intent = new Intent(MainActivity.this, TimerInterface.class);
      //Retaining whichever cycle mode we're on when launching the stopwatch. This is necessary for the correct adapter refresh when coming back to Main.
      intent.putExtra("savedMode", mode);
      intent.putExtra("mode", 4);
      startActivity(intent);
    });

    //Showing sort popup window.
    sort_text.setOnClickListener(v-> {
      sortPopupWindow.showAsDropDown(cl, 800, 0, Gravity.END);
    });

    //Uses single view for all sort buttons. Queries the appropriate cycle sort via the DAO and sets checkmark.
    View.OnClickListener sortListener = view -> {
      //Must run this first to get sort mode.
      //Casting View used by listener to textView, which we then check against its String value.
      TextView textButton = (TextView) view;
      //Handles checkmark views.
      if (textButton.getText().toString().equals("Added: Most Recent")) sortHolder = 1;
      if (textButton.getText().toString().equals("Added: Least Recent")) sortHolder = 2;
      if (textButton.getText().toString().equals("Title: A - Z")) sortHolder = 3;
      if (textButton.getText().toString().equals("Title: Z - A")) sortHolder = 4;
      if (textButton.getText().toString().equals("Round Count: Most")) sortHolder = 5;
      if (textButton.getText().toString().equals("Round Count: Least")) sortHolder = 6;
      //Assigns one of our sort modes to the sort style depending on which timer mode we're on.
      if (mode==1) sortMode = sortHolder; else if (mode==3) sortModePom = sortHolder;

      AsyncTask.execute(()-> {
        queryCycles();
        runOnUiThread(()-> {
          //Populates adapter arrays.
          populateCycleList(true);
          //Refreshes adapter.
          savedCycleAdapter.notifyDataSetChanged();
          //Sets checkmark view.
          setSortCheckmark();
          sortPopupWindow.dismiss();
        });
        //Saves sort mode so it defaults to chosen whenever we create this activity.
        prefEdit.putInt("sortMode", sortMode);
        prefEdit.putInt("sortModePom", sortModePom);
        prefEdit.apply();
      });
    };
    sortAlphaStart.setOnClickListener(sortListener);
    sortAlphaEnd.setOnClickListener(sortListener);
    sortRecent.setOnClickListener(sortListener);
    sortNotRecent.setOnClickListener(sortListener);
    sortHigh.setOnClickListener(sortListener);
    sortLow.setOnClickListener(sortListener);

    //Because this window steals focus from our activity so it can use the soft keyboard, we are using this listener to perform the functions our onBackPressed override would normally handle when the popUp is active.
    editCyclesPopupWindow.setOnDismissListener(() -> {
      //If closing edit cycle popUp after editing a cycle, do the following.
      if (editingCycle) {
        savedCycleAdapter.removeHighlight(true);
        //If all fetched values equal current values (i.e. no changes have been made), exit method.
        if (gson.toJson(workoutTime).equals(timeCompare) && gson.toJson(typeOfRound).equals(typeCompare) && cycleTitle.equals(titleCompare)) {
          //Calls notify so highlight removal is shown.
          savedCycleAdapter.notifyDataSetChanged();
          return;
        }
        AsyncTask.execute(()->{
          saveCycles(false);
          queryCycles();
          populateCycleList(true);
          runOnUiThread(()-> {
            Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();
            //Removes highlight when exiting edit mode.
            //Calls method that sets views for our edit cycles mode.
            fadeEditCycleButtonsIn(FADE_OUT_EDIT_CYCLE);
            savedCycleAdapter.notifyDataSetChanged();
          });
        });
        //If closing edit cycle popUp after adding a new cycle, do the following.
      } else {
        AsyncTask.execute(()-> {
          //If at least one round exists, insert cycle list in database.
          if ((mode==1 && workoutTime.size()>0) || (mode==3 && pomValuesTime.size()>0)) {
            saveCycles(true);
            queryCycles();
            populateCycleList(true);
            runOnUiThread(()-> {
              Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();
              //Updates our cycle list.
              savedCycleAdapter.notifyDataSetChanged();
            });
          }
        });
      }
    });

    ////--ActionBar Item onClicks START--////
    edit_highlighted_cycle.setOnClickListener(v-> {
      fadeEditCycleButtonsIn(FADE_IN_EDIT_CYCLE);
      //Displays edit cycles popUp.
      editCyclesPopupWindow.showAsDropDown(tabLayout);
      AsyncTask.execute(()-> {
        //Button is only active if list contains exactly ONE position (i.e. only one cycle is selected). Here, we set our retrieved position (same as if we simply clicked a cycle to launch) to the one passed in from our highlight.
        receivedPos = Integer.parseInt(receivedHighlightPositions.get(0));
        //Uses this single position to retrieve cycle and populate timer arrays.
        retrieveRoundList();
        //Clears old array values.
        clearRoundAdapterArrays();
        switch (mode) {
          case 1:
            //Populating String ArrayLists used to display rounds in editCycle popUp.
            for (int i=0; i<workoutTime.size(); i++) {
              //Aggregate list of rounds. Necessary since adjustCustom() uses it.
              convertedWorkoutTime.add(convertSeconds(workoutTime.get(i)/1000));
              //If 8 or less rounds, add to first round adapter's String Array (and roundType).
              if (i<=7) {
                roundHolderOne.add(convertSeconds(workoutTime.get(i)/1000));
                typeHolderOne.add(typeOfRound.get(i));
              }
              //If 9 or more rounds, add to second round adapter's String Array (and roundType).
              if (i>=8) {
                roundHolderTwo.add(convertSeconds(workoutTime.get(i)/1000));
                typeHolderTwo.add(typeOfRound.get(i));
              }
            }
            //Title has already been retrieved via retrieveRoundList().
            break;
          case 3:
            for (int i=0; i<pomValuesTime.size(); i++) convertedPomList.add(convertSeconds(pomValuesTime.get(i)/1000));
            break;
        }
        runOnUiThread(()-> {
          //Changes view layout depending on +/- 8 round count.
          if (mode==1) {
            if (workoutTime.size()<=8) {
              roundRecyclerTwo.setVisibility(View.GONE);
              recyclerLayoutOne.leftMargin = 240;
              roundListDivider.setVisibility(View.GONE);
            } else {
              roundRecyclerTwo.setVisibility(View.VISIBLE);
              recyclerLayoutOne.leftMargin = 5;
              roundListDivider.setVisibility(View.VISIBLE);
            }
          }
          cycle_name_text.setVisibility(View.INVISIBLE);
          cycle_name_edit.setVisibility(View.VISIBLE);
          cycle_name_text.setText(cycleTitle);
          //Setting editText title.
          cycle_name_edit.setText(cycleTitle);
          //Updating adapter views.
          cycleRoundsAdapter.notifyDataSetChanged();
          cycleRoundsAdapterTwo.notifyDataSetChanged();
          //Removing highlights.
          savedCycleAdapter.removeHighlight(true);
          //Boolean set to false indicates that a current database-saved cycle is populating our editCyclesPopup.
          onNewCycle = false;
        });
      });
    });

    //Turns off our cycle highlight mode from adapter.
    cancelHighlight.setOnClickListener(v-> {
      savedCycleAdapter.removeHighlight(true);
      savedCycleAdapter.notifyDataSetChanged();
      fadeEditCycleButtonsIn(FADE_OUT_HIGHLIGHT_MODE);
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
          populateCycleList(true);
          savedCycleAdapter.notifyDataSetChanged();
          receivedHighlightPositions = tempPos;
          //If there are no cycles left, cancel highlight mode. If there are any left, simply remove all highlights.
          if (receivedHighlightPositions.size()>0) savedCycleAdapter.removeHighlight(false); else {
            cancelHighlight.setVisibility(View.INVISIBLE);
            edit_highlighted_cycle.setVisibility(View.INVISIBLE);
            delete_highlighted_cycle.setVisibility(View.INVISIBLE);
            appHeader.setVisibility(View.VISIBLE);
            savedCycleAdapter.removeHighlight(true);
            Toast.makeText(getApplicationContext(), "Deleted!", Toast.LENGTH_SHORT).show();
          }
        });
      });
    });

    //When in highlight edit mode, clicking on the textView will remove it, replace it w/ editText field, give that field focus and bring up the soft keyboard.
    cycle_name_text.setOnClickListener(v-> {
      cycle_name_text.setVisibility(View.INVISIBLE);
      cycle_name_edit.setVisibility(View.VISIBLE);
      cycle_name_edit.requestFocus();
      inputMethodManager.showSoftInput(cycle_name_edit, 0);
    });

    //Selects all text when long clicking in editText.
    cycle_name_edit.setOnLongClickListener(v-> {
      cycle_name_edit.selectAll();
      return true;
    });

    ////--ActionBar Item onClicks END--////

    ////--EditCycles Menu Item onClicks START--////
    //Launched from editCyclePopUp and calls TimerInterface. Boolean is set to FALSE by default (launching an existing cycle, used primarily in editing), and set to TRUE from the Fab button.
    start_timer.setOnClickListener(v-> {
      AsyncTask.execute(()-> {
        launchTimerCycle(onNewCycle);
      });
    });

    setsInfinity.setOnClickListener(v -> {
      if (setsSelected) if (setsInfinity.getAlpha()==1.0f) setsInfinity.setAlpha(0.3f); else setsInfinity.setAlpha(1.0f);
    });

    breaksInfinity.setOnClickListener(v -> {
      if (breaksSelected) if (breaksInfinity.getAlpha()==1.0f) breaksInfinity.setAlpha(0.3f); else breaksInfinity.setAlpha(1.0f);
    });

    //For moment, using arrows next to sets and breaks to determine which type of round we're adding.
    add_cycle.setOnClickListener(v -> {
      adjustCustom(true);
    });

    sub_cycle.setOnClickListener(v -> {
      adjustCustom(false);
    });

    //Grabs the x-axis value of textView, and uses that determine whether we replace it with the minutes or seconds editText.
    first_value_textView.setOnTouchListener((v, event) -> {
      if (event.getAction()==MotionEvent.ACTION_DOWN) {
        editTextViewPosX = event.getX();
        editAndTextSwitch(true, 1);
      }
      return true;
    });

    second_value_textView.setOnTouchListener((v, event) -> {
      if (event.getAction()==MotionEvent.ACTION_DOWN) {
        editTextViewPosX = event.getX();
        editAndTextSwitch(true, 2);
      }
      return true;
    });

    third_value_textView.setOnTouchListener((v, event) -> {
      if (event.getAction()==MotionEvent.ACTION_DOWN) {
        editTextViewPosX = event.getX();
        editAndTextSwitch(true, 3);
      }
      return true;
    });

    adjustRoundDelay = new Runnable() {
      @Override
      public void run() {
        add_cycle.setClickable(true);
        sub_cycle.setClickable(true);
      }
    };

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
            break;
          case 3:
            if (incrementValues) pomValue2 += 5;
            else pomValue2 -= 5;
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
        runOnUiThread(()->{
          //Clears adapter arrays and populates recyclerView with (nothing) since arrays are now empty. Also called notifyDataSetChanged().
          populateCycleList(true);
          savedCycleAdapter.notifyDataSetChanged();
        });
      });
    });

    delete_all_cancel.setOnClickListener(v-> {
      //Removes confirmation window.
      if (deleteCyclePopupWindow.isShowing()) deleteCyclePopupWindow.dismiss();
    });
  }

  public void checkEmptyCycles() {
    if (mode==1) {
      if (cyclesList.size()!=0) {
        savedCycleRecycler.setVisibility(View.VISIBLE);
        emptyCycleList.setVisibility(View.GONE);
      } else {
        savedCycleRecycler.setVisibility(View.GONE);
        emptyCycleList.setVisibility(View.VISIBLE);
      }
      //Todo: We're going to change adapter/recycler for this.
    } else if (mode==3) {
      if (pomCyclesList.size()!=0) {
        savedCycleRecycler.setVisibility(View.VISIBLE);
        emptyCycleList.setVisibility(View.GONE);
      } else {
        savedCycleRecycler.setVisibility(View.GONE);
        emptyCycleList.setVisibility(View.VISIBLE);
      }
    }
  }

  //Resets row selection and editText/textView values.
  public void resetRows() {
    if (mode==1) {
      rowSelect(s1, first_value_textView, first_value_edit, first_value_edit_two, first_value_sep, plus_first_value, minus_first_value, Color.GREEN);
      //Our editText fields have listeners attached which call setEditValues(), which set our edit values AND setValue/breakValue vars to the values within the editText box itself. Here, we use 0:30 for both.
      first_value_edit.setText(String.valueOf(0));
      first_value_edit_two.setText(elongateEditSeconds(30));
      second_value_edit.setText(String.valueOf(0));
      second_value_edit_two.setText(elongateEditSeconds(30));
      first_value_textView.setText(convertCustomTextView(setValue));
      second_value_textView.setText(convertCustomTextView(breakValue));
    }
    else if (mode==3) {
      rowSelect(s1, first_value_textView, first_value_edit, first_value_edit_two, first_value_sep, plus_first_value, minus_first_value, Color.GREEN);
      rowSelect(s2, second_value_textView, second_value_edit, second_value_edit_two, second_value_sep, plus_second_value, minus_second_value, Color.RED);
      rowSelect(s3, third_value_textView, third_value_edit, third_value_edit_two, third_value_sep, plus_third_value, minus_third_value, R.color.teal_200);
    }
  }

  //Colors rows.
  public void rowSelect(TextView header, TextView textVal, EditText editOne, EditText editTwo, TextView editSep, ImageView plus, ImageView minus, int color) {
    header.setTextColor(color);
    textVal.setTextColor(color);
    editOne.setTextColor(color);
    editTwo.setTextColor(color);
    editSep.setTextColor(color);
    plus.setColorFilter(color);
    minus.setColorFilter(color);
  }

  //Fades action bar buttons in/out depending on whether we are editing cycles or not.
  public void fadeEditCycleButtonsIn(int typeOfFade) {
    //Clearing all animations. If we don't, their alphas tend to get reset.
    edit_highlighted_cycle.clearAnimation();
    delete_highlighted_cycle.clearAnimation();
    cancelHighlight.clearAnimation();
    appHeader.clearAnimation();
    sort_text.clearAnimation();
    //Only needs to be true if editor is active, which is only the case where it is set to true below.
    editingCycle = false;
    if (typeOfFade==FADE_IN_HIGHLIGHT_MODE) {
      //Boolean used to keep launchCycles() from calling retrieveRoundLists(), which replace our current timer array list w/ one fetched from DB.
      edit_highlighted_cycle.startAnimation(fadeIn);
      delete_highlighted_cycle.startAnimation(fadeIn);
      cancelHighlight.startAnimation(fadeIn);
      appHeader.startAnimation(fadeOut);
      sort_text.startAnimation(fadeOut);
      //Disabling sort button since it is faded out and still active.
      sort_text.setEnabled(false);
      //Enabling edit/delete buttons.
      edit_highlighted_cycle.setEnabled(true);
      delete_highlighted_cycle.setEnabled(true);
      //Views for not-in-edit-mode.
    }
    if (typeOfFade==FADE_OUT_HIGHLIGHT_MODE) {
      //Boolean used to keep launchCycles() from calling retrieveRoundLists(), which replace our current timer array list w/ one fetched from DB.
      edit_highlighted_cycle.startAnimation(fadeOut);
      delete_highlighted_cycle.startAnimation(fadeOut);
      cancelHighlight.startAnimation(fadeOut);
      appHeader.startAnimation(fadeIn);
      sort_text.startAnimation(fadeIn);
      //Re-enabling sort button now that edit mode has exited.
      sort_text.setEnabled(true);
      //Disabling edit/delete buttons.
      edit_highlighted_cycle.setEnabled(false);
      delete_highlighted_cycle.setEnabled(false);
    }
    if (typeOfFade==FADE_IN_EDIT_CYCLE) {
      //Set to false by default, and true only with this conditional.
      editingCycle = true;
      delete_highlighted_cycle.setEnabled(true);
      edit_highlighted_cycle.setVisibility(View.GONE);
      sort_text.setVisibility(View.GONE);
    }
    if (typeOfFade==FADE_OUT_EDIT_CYCLE) {
      sort_text.setVisibility(View.VISIBLE);
      delete_highlighted_cycle.setVisibility(View.GONE);
      delete_highlighted_cycle.setEnabled(false);
    }
  }

  public void setSortCheckmark() {
    switch (sortHolder) {
      case 1:
        sortCheckmark.setY(14); break;
      case 2:
        sortCheckmark.setY(110); break;
      case 3:
        sortCheckmark.setY(206); break;
      case 4:
        sortCheckmark.setY(302); break;
      case 5:
        sortCheckmark.setY(398); break;
      case 6:
        sortCheckmark.setY(494); break;
    }
  }

  //Clears both timer millis arrays and their converted String arrays.
  public void clearRoundAdapterArrays() {
    convertedWorkoutTime.clear();
    roundHolderOne.clear();
    roundHolderTwo.clear();
    typeHolderOne.clear();
    typeHolderTwo.clear();

    pomValuesTime.clear();
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
    if (mode==1) {
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
      //Sets our timer values to the values in editText.
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
    }
    setTimerValueBounds();
  }

  //Sets min/max bounds on timer values. MUST be separate from setEditValues() so it can be called w/ in our +/- increment runnable and not b0rk the values.
  public void setTimerValueBounds() {
    switch (mode) {
      case 1:
        toastBounds(5, 300, setValue);
        toastBounds(5, 300, breakValue);
        if (setValue < 5) setValue = 5;
        if (breakValue < 5) breakValue = 5;
        if (setValue > 300) setValue = 300;
        if (breakValue > 300) breakValue = 300;
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
  public void convertEditTime(boolean setEditViews) {
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
      if (setEditViews) {
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
      if (setEditViews) {
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
        first_value_textView.setVisibility(View.INVISIBLE);
        first_value_edit.setVisibility(View.VISIBLE);
        first_value_edit_two.setVisibility(View.VISIBLE);
        first_value_sep.setVisibility(View.VISIBLE);
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
        //Used to request focus, and then show the soft keyboard. Using x-axis values to determine whether we focus on minutes or seconds.
        if (editTextViewPosX<=75) {
          first_value_edit.requestFocus();
          inputMethodManager.showSoftInput(first_value_edit, 0);
        } else {
          first_value_edit_two.requestFocus();
          inputMethodManager.showSoftInput(first_value_edit_two, 0);
        }
      } else if (viewRemoved == 2) {
        second_value_textView.setVisibility(View.INVISIBLE);
        second_value_edit.setVisibility(View.VISIBLE);
        second_value_edit_two.setVisibility(View.VISIBLE);
        second_value_sep.setVisibility(View.VISIBLE);
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
        if (editTextViewPosX<=75) {
          second_value_edit.requestFocus();
          inputMethodManager.showSoftInput(second_value_edit, 0);
        } else {
          second_value_edit_two.requestFocus();
          inputMethodManager.showSoftInput(second_value_edit_two, 0);
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
      if (editTextViewPosX<=75) {
        third_value_edit.requestFocus();
        inputMethodManager.showSoftInput(third_value_edit, 0);
      } else {
        third_value_edit_two.requestFocus();
        inputMethodManager.showSoftInput(third_value_edit_two, 0);
      }
    } else {
      //If moving from editText -> textView.
      switch (mode) {
        case 1:
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

  //Sets all of our editTexts to Invisible.
  public void removeEditTimerViews(boolean reinstateText) {
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

    //Replaces editTexts w/ textViews if desired.
    if (reinstateText) {
      if (mode==1 || mode==3) first_value_textView.setVisibility(View.VISIBLE);
      if (mode==3) third_value_textView.setVisibility(View.VISIBLE);
      second_value_textView.setVisibility(View.VISIBLE);
    }
  }

  public void editCycleViews() {
    //Instance of layout objects we can set programmatically based on which mode we're on.
    ConstraintLayout.LayoutParams s2ParamsAdd = (ConstraintLayout.LayoutParams) plus_second_value.getLayoutParams();
    ConstraintLayout.LayoutParams s2ParamsSub = (ConstraintLayout.LayoutParams) minus_second_value.getLayoutParams();
    ConstraintLayout.LayoutParams addParams = (ConstraintLayout.LayoutParams) add_cycle.getLayoutParams();
    ConstraintLayout.LayoutParams subParams = (ConstraintLayout.LayoutParams) sub_cycle.getLayoutParams();

    convertEditTime(true);
    switch (mode) {
      case 1:
        //All shared visibilities between modes 1 and 2.
        s2.setVisibility(View.VISIBLE);
        s3.setVisibility(View.GONE);
        third_value_textView.setVisibility(View.INVISIBLE);
        plus_third_value.setVisibility(View.INVISIBLE);
        minus_third_value.setVisibility(View.INVISIBLE);
        plus_second_value.setVisibility(View.VISIBLE);
        minus_second_value.setVisibility(View.VISIBLE);
        setsInfinity.setVisibility(View.VISIBLE);
        breaksInfinity.setVisibility(View.VISIBLE);
        second_value_textView.setVisibility(View.VISIBLE);
        //Shared String between modes 1 and 2.
        s2.setText(R.string.break_time);
        s1.setVisibility(View.VISIBLE);
        first_value_textView.setVisibility(View.VISIBLE);
        plus_first_value.setVisibility(View.VISIBLE);
        minus_first_value.setVisibility(View.VISIBLE);
        //Strings and values exclusive to mode 1.
        s1.setText(R.string.set_time);
        first_value_textView.setText(convertCustomTextView(setValue));
        second_value_textView.setText(convertCustomTextView(breakValue));
        //If in mode 1 or 2, constraining our add/remove buttons to the "s2" line of objects.
        addParams.topToBottom = R.id.s2;
        addParams.topMargin = 30;
        subParams.topToBottom = R.id.s2;
        subParams.topMargin = 30;
        break;
      case 3:
        //Visibilities and values exclusive to mode 3.
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
        setsInfinity.setVisibility(View.GONE);
        breaksInfinity.setVisibility(View.GONE);
        s1.setText(R.string.work_time);
        s2.setText(R.string.small_break);
        s3.setText(R.string.long_break);
        first_value_textView.setText(convertCustomTextView(pomValue1));
        second_value_textView.setText(convertCustomTextView(pomValue2));
        third_value_textView.setText(convertCustomTextView(pomValue3));
        //If in mode 3, constraining our add/remove buttons to the "s3" line of objects.
        addParams.topToBottom = R.id.s3;
        addParams.topMargin = 30;
        subParams.topToBottom = R.id.s3;
        subParams.topMargin = 30;
        break;
    }
    //If in mode 2, constraining views for a more compact interface.
    if (mode==2) {
      s2ParamsAdd.topToBottom = R.id.top_anchor;
      s2ParamsSub.topToBottom = R.id.top_anchor;
    } else {
      s2ParamsAdd.topToBottom = R.id.plus_first_value;
      s2ParamsSub.topToBottom = R.id.plus_first_value;
    }
  }

  public void adjustCustom(boolean adding) {
    //Hides soft keyboard by using a token of the current editCycleView.
    inputMethodManager.hideSoftInputFromWindow(editCyclesPopupView.getWindowToken(), 0);
    if (adding) {
      //Converts whatever we've entered as Strings in editText to long values for timer, and caps their values. Only necessary when adding a round.
      setEditValues();
      if (mode==1) {
        if (workoutTime.size()<16) {
          //If Sets are highlighted green, check if its infinity mode is also highlighted. Use 1/2 for yes/no.
          if (s1.getCurrentTextColor()==Color.GREEN) {
            if (setsInfinity.getAlpha()!=1.0f) roundType = 1; else roundType = 2;
          }
          //If Breaks are highlighted red, check if its infinity mode is also highlighted. Use 3/4 for yes/no.
          if (s2.getCurrentTextColor()==Color.RED) {
            if (breaksInfinity.getAlpha()!=1.0f) roundType = 3; else roundType = 4;
          }
          //ConvertedWorkoutTime still used because we add converted values from different variables (set/breaks).
          switch (roundType) {
            case 1:
              workoutTime.add(setValue * 1000);
              convertedWorkoutTime.add(convertSeconds(setValue));
              break;
            case 3:
              workoutTime.add(breakValue * 1000);
              convertedWorkoutTime.add(convertSeconds(breakValue));
              break;
            case 2: case 4:
              workoutTime.add(0);
              convertedWorkoutTime.add("0");
              break;
            default:
              //Returns from method so we don't add a roundType entry to our list, and the list stays in sync w/ the rounds we are actually adding.
              Toast.makeText(getApplicationContext(), "Nada for now!", Toast.LENGTH_SHORT).show();
              return;
          }
          //Adds 1-4 for type of round anytime we're adding a round.
          typeOfRound.add(roundType);
          //If total rounds are <=8, also add them to our holder lists (used for separating viewHolders into two columns.
          if (workoutTime.size()<=8) {
            roundHolderOne.add(convertedWorkoutTime.get(convertedWorkoutTime.size()-1));
            typeHolderOne.add(typeOfRound.get(typeOfRound.size()-1));
            if (!roundIsFading) {
              //Sets fade positions for rounds. Most recent for addition, and -1 (out of bounds) for subtraction.
              cycleRoundsAdapter.setFadePositions(-1, workoutTime.size()-1);
            }
            cycleRoundsAdapter.notifyDataSetChanged();
          } else {
            roundHolderTwo.add(convertedWorkoutTime.get(convertedWorkoutTime.size()-1));
            typeHolderTwo.add(typeOfRound.get(typeOfRound.size()-1));
            if (!roundIsFading) {
              //Sets fade positions for rounds. Most recent for addition, and -1 (out of bounds) for subtraction.
              cycleRoundsAdapterTwo.setFadePositions(-1, workoutTime.size()-9);
            }
            cycleRoundsAdapterTwo.notifyDataSetChanged();
          }
          //If moving from one list to two, set its visibility and change layout params.
          if (workoutTime.size()==9) {
            roundRecyclerTwo.setVisibility(View.VISIBLE);
            recyclerLayoutOne.leftMargin = 5;
            roundListDivider.setVisibility(View.VISIBLE);
          }
        } else Toast.makeText(getApplicationContext(), "Full!", Toast.LENGTH_SHORT).show();
      }
      if (mode==3) {
        if (pomValuesTime.size()==0) {
          for (int i = 0; i < 3; i++) {
            pomValuesTime.add(pomValue1 * 1000);
            pomValuesTime.add(pomValue2 * 1000);
          }
          pomValuesTime.add(pomValue1 * 1000);
          pomValuesTime.add(pomValue3 * 1000);
          for (int j=0; j<pomValuesTime.size(); j++)  convertedPomList.add(convertSeconds(pomValuesTime.get(j)/1000));

          cycleRoundsAdapter.setPomFade(true);
          cycleRoundsAdapter.notifyDataSetChanged();
        } else {
          Toast.makeText(getApplicationContext(), "Pomodoro cycle already loaded!", Toast.LENGTH_SHORT).show();
          return;
        }
      }
    } else {
      if (mode==1) {
        //If clicking to subtract round while a fading animation for a previous removal is active, call method to cancel animation and remove current round. Otherwise, end of animation will trigger a callback in adapter that will call this same method. Either way, round is removed.
        if (roundIsFading) removeRound();

        //If at least one round is present, call fade animation in adapter.
        if (workoutTime.size()>0) {
          if (workoutTime.size()<=8) {
            //Sets fade positions for rounds. Most recent for subtraction, and -1 (out of bounds) for addition.
            cycleRoundsAdapter.setFadePositions(workoutTime.size()-1, -1);
            cycleRoundsAdapter.notifyDataSetChanged();
            roundIsFading = true;
          } else {
            //Sets fade positions for rounds. Most recent for subtraction, and -1 (out of bounds) for addition.
            cycleRoundsAdapterTwo.setFadePositions(workoutTime.size()-9, -1);
            cycleRoundsAdapterTwo.notifyDataSetChanged();
            roundIsFading = true;
          }
        } else Toast.makeText(getApplicationContext(), "Empty!", Toast.LENGTH_SHORT).show();
      }
      if (mode==3) {
        //If a cycle exists, disable the timer because we are removing the cycle via our fadeOutDot runnable which will not complete until the fade is done. Adding a cycle will re-enable the timer through populateTimerUI().
        if (pomValuesTime.size() != 0) {
          cycleRoundsAdapter.setPomFade(false);
          cycleRoundsAdapter.notifyDataSetChanged();
        } else {
          Toast.makeText(getApplicationContext(), "No Pomodoro cycle to clear!", Toast.LENGTH_SHORT).show();
          return;
        }
      }
    }
  }

  public void removeRound () {
    //Cancels animation if we click to remove a round while removal animation for previous one is active.
    cycleRoundsAdapter.endFade();
    //Rounds only get removed once fade is finished, which we receive status of from callback in adapter.
    if (mode==1) {
      typeOfRound.remove(typeOfRound.size()-1);
      workoutTime.remove(workoutTime.size()-1);
      convertedWorkoutTime.remove(convertedWorkoutTime.size()-1);
      //Removes rounds from our holder lists. Uses <=7 conditional since they are removed first above.
      if (workoutTime.size()<=7) {
        roundHolderOne.remove(roundHolderOne.size()-1);
        typeHolderOne.remove(typeHolderOne.size()-1);
        //Sets fade positions for rounds. -1 (out of bounds) for both, since this adapter refresh simply calls the post-fade listing of current rounds.
        cycleRoundsAdapter.setFadePositions(-1, -1);
        cycleRoundsAdapter.notifyDataSetChanged();
      } else {
        roundHolderTwo.remove(roundHolderTwo.size()-1);
        typeHolderTwo.remove(typeHolderTwo.size()-1);
        //Sets fade positions for rounds. -1 (out of bounds) for both, since this adapter refresh simply calls the post-fade listing of current rounds.
        cycleRoundsAdapterTwo.setFadePositions(-1,  -1);
        cycleRoundsAdapterTwo.notifyDataSetChanged();
      }
      //If moving from two lists to one, set its visibility and change layout params.
      if (workoutTime.size()==8) {
        roundRecyclerTwo.setVisibility(View.GONE);
        recyclerLayoutOne.leftMargin = 240;
        roundListDivider.setVisibility(View.GONE);
      }
      //Once a round has been removed (and shown as such) in our recyclerView, we always allow for a new fade animation (for the next one).
      roundIsFading = false;
    }
  }

  public String friendlyString(String altString) {
    altString = altString.replace("\"", "");
    altString = altString.replace("]", "");
    altString = altString.replace("[", "");
    altString = altString.replace(",", " - ");
    return altString;
  }

  //Queries database for all cycles, using last chosen sort order.
  public void queryCycles() {
    switch (mode) {
      case 1:
        switch (sortMode) {
          case 1: cyclesList = cyclesDatabase.cyclesDao().loadCyclesMostRecent(); break;
          case 2: cyclesList = cyclesDatabase.cyclesDao().loadCycleLeastRecent(); break;
          case 3: cyclesList = cyclesDatabase.cyclesDao().loadCyclesAlphaStart(); break;
          case 4: cyclesList = cyclesDatabase.cyclesDao().loadCyclesAlphaEnd(); break;
          case 5: cyclesList = cyclesDatabase.cyclesDao().loadCyclesMostItems(); break;
          case 6: cyclesList = cyclesDatabase.cyclesDao().loadCyclesLeastItems(); break;
        }
        break;
      case 3:
        switch (sortModePom) {
          case 1: pomCyclesList = cyclesDatabase.cyclesDao().loadPomCyclesMostRecent(); break;
          case 2: pomCyclesList = cyclesDatabase.cyclesDao().loadPomCyclesLeastRecent(); break;
          case 3: pomCyclesList = cyclesDatabase.cyclesDao().loadPomAlphaStart(); break;
          case 4: pomCyclesList = cyclesDatabase.cyclesDao().loadPomAlphaEnd(); break;
        }
        break;
    }
  }

  //Clears STRING arrays, used to populate adapter views, and re-populates them with database values.
  //Remember, if the database has changed we need to call queryCycles() before this or new values will not be retrieved.
  //Todo: Can probably nix false boolean, since we're going with full db queries, especially since we want to call the proper sorting method on each adapter refresh.
  public void populateCycleList(boolean queryList) {
    switch (mode) {
      case 1:
        //If we need to fetch values from database, do this.
        if (queryList) {
          workoutCyclesArray.clear();
          typeOfRoundArray.clear();
          workoutTitleArray.clear();
          for (int i=0; i<cyclesList.size(); i++) {
            //Adds the concatenated timer String used in each cycle (e.g. XX - XX - XX) to the String Array that was pass into our cycle list's adapter.
            workoutCyclesArray.add(cyclesList.get(i).getWorkoutRounds());
            //Retrieves title for use when editing a cycle.
            workoutTitleArray.add(cyclesList.get(i).getTitle());
            //Adds concatenated roundType String used in each cycle.
            typeOfRoundArray.add(cyclesList.get(i).getRoundType());
          }
        } else {
          if (editingCycle) {
            //If we have the values in our workOutTime and typeOFRound arrays already, simply pass them into cycleList's adapter arrays.
            workoutTitleArray.set(receivedPos, cycleTitle);
            workoutCyclesArray.set(receivedPos, workoutString);
            typeOfRoundArray.set(receivedPos, roundTypeString);
          } else {
            //If we are adding a new cycle, no need to query the DB for values after save. Just use what has been passed into them from Arrays. This will add them as the correct last position.
            workoutTitleArray.add(cycleTitle);
            typeOfRoundArray.add(roundTypeString);
            workoutCyclesArray.add(workoutString);
          }
        }
        break;
      case 3:
        pomArray.clear();
        for (int i=0; i<pomCyclesList.size(); i++) {
          pomArray.add(pomCyclesList.get(i).getFullCycle());
          pomTitleArray.add(pomCyclesList.get(i).getTitle());
        }
        break;
    }
  }

  //Populates round list from single cycle.
  public void retrieveRoundList() {
    //Resetting comparison vars.
    timeCompare = "";
    typeCompare = "";
    titleCompare = "";
    //Clears the two lists of actual timer values we are populating.
    workoutTime.clear();
    typeOfRound.clear();
    //Calling cycleList instance based on sort mode.
    queryCycles();
    switch (mode) {
      case 1:
        //Getting instance of Cycles at selected position, creating a String Array from its concatenated String, and creating an Integer Array from that for use in our Timer and adapter displays.
        Cycles cycles = cyclesList.get(receivedPos);
        String[] tempSets = cycles.getWorkoutRounds().split(" - ");
        for (int i=0; i<tempSets.length; i++) workoutTime.add(Integer.parseInt(tempSets[i]));
        String[] tempRoundTypes = cycles.getRoundType().split(" - ");
        for (int j=0; j<tempRoundTypes.length; j++) typeOfRound.add(Integer.parseInt(tempRoundTypes[j]));

        //Primary key ID of position selected.
        retrievedID = cyclesList.get(receivedPos).getId();
        cycleTitle = cycles.getTitle();

        //Retrieved cycle values from database. Used to compare to ones when in edit mode when determining whether we save/update db in (a) exiting edit back to main or (b) launching cycle.
        timeCompare = gson.toJson(workoutTime);
        typeCompare = gson.toJson(typeOfRound);
        titleCompare = cycleTitle;
        break;
      case 3:
        PomCycles pomCycles = pomCyclesList.get(receivedPos);
        pomValuesTime.clear();
        String[] tempPom = pomCycles.getFullCycle().split(" - ");
        for (int i=0; i<tempPom.length; i++) pomValuesTime.add(Integer.parseInt(tempPom[i]));
        retrievedID = pomCyclesList.get(receivedPos).getId();

        timeCompare = gson.toJson(pomValuesTime);
        titleCompare = cycleTitle;
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
      if ((mode==1 && workoutTime.size()==0) || (mode==3 && pomValuesTime.size()==0)) {
        runOnUiThread(()-> Toast.makeText(getApplicationContext(), "Cycle cannot be empty!", Toast.LENGTH_SHORT).show());
        return;
      }
      //Since this is a new Cycle, we automatically save it to database.
      saveCycles(true);
      //If selecting an existing cycle, call its info and set timer value arrays. Also, pass in FALSE to saveCycles.
    } else {
      //Only calls retrieveRoundList() if NOT editing, since it also clears our round lists and we need them retained.
      if (!editingCycle) retrieveRoundList();
      //Updates any changes made to cycles.
      saveCycles(false);
    }

    //For both NEW and RETRIEVED cycles, we send the following intents to TimerInterface.
    switch (mode) {
      case 1:
        intent.putIntegerArrayListExtra("workoutTime", workoutTime);
        intent.putIntegerArrayListExtra("typeOfRound", typeOfRound);
        break;
      case 3:
        intent.putIntegerArrayListExtra("pomList", pomValuesTime);
        break;
    }
    //Sends the title.
    intent.putExtra("cycleTitle", cycleTitle);
    //Mode used for type of timer.
    intent.putExtra("mode", mode);
    //If cycle is new, tell Timer so that it doesn't try to fetch an ID. Also, don't send an ID that will default to 0 since nothing is received in onCycleClick().
    if (newCycle) intent.putExtra("isNewCycle", true); else {
      intent.putExtra("isNewCycle", false);
      //Sends the current cycle's database position so we can delete it from the Timer class if desired.
      intent.putExtra("passedID", retrievedID);
    }
    //Starts Timer class.
    startActivity(intent);
  }

  private void saveCycles(boolean newCycle) {
    //We will have at least one cycle populated when this method completes, so we set our views for it.
    runOnUiThread(()->{
      if (savedCycleRecycler.getVisibility()!=View.VISIBLE) {
        savedCycleRecycler.setVisibility(View.VISIBLE);
        emptyCycleList.setVisibility(View.GONE);
      }
    });
    //sets boolean used in launchCycles.
    isNewCycle = newCycle;
    //Gets current date for use in empty titles.
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMMM d yyyy - hh:mma", Locale.getDefault());
    String date = dateFormat.format(calendar.getTime());

    //Sets up Strings to save into database.
    Gson gson = new Gson();
    workoutString = "";
    roundTypeString = "";
    pomString = "";
    int cycleID = 0;
    switch (mode) {
      case 1:
        //If coming from FAB button, create a new instance of Cycles. If coming from a position in our database, get the instance of Cycles in that position.
        if (newCycle) cycles = new Cycles(); else if (cyclesList.size()>0) {
          cycleID = cyclesList.get(receivedPos).getId();
          cycles = cyclesDatabase.cyclesDao().loadSingleCycle(cycleID).get(0);
        }
        ///*----All of this occurs whether we are saving a new cycle, or launching a cycle currently in the database.----*///
        //Converting our String array of rounds in a cycle to a single String so it can be stored in our database. Saving "Count Down" values regardless of how we're counting, as we want them present when toggling between count up/count down.
        workoutString = gson.toJson(workoutTime);
        workoutString = friendlyString(workoutString);
        roundTypeString = gson.toJson(typeOfRound);
        roundTypeString = friendlyString(roundTypeString);
        //If round list is blank, setString will remain at "", in which case we do not save or update. This is determined after we convert via Json above.
        if (!workoutString.equals("")) {
          //Adding and inserting into database.
          cycles.setWorkoutRounds(workoutString);
          cycles.setRoundType(roundTypeString);
          //Setting most recent time accessed for sort mode.
          cycles.setTimeAccessed(System.currentTimeMillis());
          cycles.setItemCount(workoutTime.size());
          //If cycle is new, add an initial creation time and populate total times + completed cycle rows to 0.
          if (newCycle) {
            //Only setting timeAdded for NEW cycle. We want our (sort by date) to use the initial time/date.
            cycles.setTimeAdded(System.currentTimeMillis());
            cycles.setTotalSetTime(0);
            cycles.setTotalBreakTime(0);
            cycles.setCyclesCompleted(0);
            //If cycle is new, set title to given, or to current date/time if none given. If cycle is NOT new, only set to what has been entered (or keep old one if unchanged).
            if (cycleTitle.isEmpty()) cycleTitle = date;
            cycles.setTitle(cycleTitle);
            //If cycle is new, insert a new row.
            cyclesDatabase.cyclesDao().insertCycle(cycles);
          } else {
            cycles.setTitle(cycleTitle);
            //If cycle is old, update current row.
            cyclesDatabase.cyclesDao().updateCycles(cycles);
          }
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
          pomCycles.setTimeAccessed(System.currentTimeMillis());;
          if (newCycle) {
            pomCycles.setTimeAdded(System.currentTimeMillis());
            if (!cycleTitle.isEmpty()) pomCycles.setTitle(cycleTitle); else pomCycles.setTitle(date);
            cyclesDatabase.cyclesDao().insertPomCycle(pomCycles);
          } else {
            if (!cycleTitle.isEmpty()) pomCycles.setTitle(cycleTitle);
            cyclesDatabase.cyclesDao().updatePomCycles(pomCycles);
          }
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
      case 3:
        if (!deleteAll) {
          cycleID = pomCyclesList.get(receivedPos).getId();
          pomCycles = cyclesDatabase.cyclesDao().loadSinglePomCycle(cycleID).get(0);
          cyclesDatabase.cyclesDao().deletePomCycle(pomCycles);
        } else if (pomCyclesList.size()>0) cyclesDatabase.cyclesDao().deleteAllPomCycles(); else emptyCycle = true;
        break;
    }
    queryCycles();
    runOnUiThread(()-> {
      //Sets textView for empty cycle list to visible and recyclerView to gone.
      checkEmptyCycles();
    });
    if (emptyCycle){
      runOnUiThread(() -> {
        Toast.makeText(getApplicationContext(), "Nothing saved!", Toast.LENGTH_SHORT).show();
      });
    }
  }
}