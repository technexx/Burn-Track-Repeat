package com.example.tragic.irate.simple.stopwatch.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tragic.irate.simple.stopwatch.SettingsFragments.ChangeSettingsValues;
import com.example.tragic.irate.simple.stopwatch.R;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SavedCycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  Context mContext;

  ArrayList<String> mWorkoutList;
  ArrayList<String> mTypeOfRound;
  ArrayList<String> mWorkoutTitle;
  List<Boolean> mTdeeActivityExistsInCycleList;
  List<Boolean> mActiveTdeeModeBooleanList;
  ArrayList<String> mWorkoutActivityString;

  onPauseOrResumeListener mOnPauseOrResumeListener;
  onCycleClickListener mOnCycleClickListener;
  onHighlightListener mOnHighlightListener;
  onResumeOrResetCycle mOnResumeOrResetCycle;
  onTdeeModeToggle mOnTdeeModeToggle;

  int RESUMING_CYCLE_FROM_TIMER = 1;
  int RESETTING_CYCLE_FROM_TIMER = 2;

  boolean mHighlightDeleted;
  boolean mHighlightMode;

  List<Integer> mHighlightPositionList;
  CharSequence permSpan;
  Spannable span;

  boolean mTimerPaused;
  boolean mActiveCycle;
  int mPositionOfActiveCycle;
  int mNumberOfRoundsCompleted;

  ChangeSettingsValues changeSettingsValues;
  int SET_COLOR;
  int BREAK_COLOR;

  int mScreenHeight;

  int mThemeMode;
  int DAY_MODE = 0;
  int NIGHT_MODE = 1;

  int fullViewBackgroundColor;

  public interface onPauseOrResumeListener {
    void onPauseOrResume(boolean timerIsPaused);
  }

  public void setTimerPauseOrResume(onPauseOrResumeListener xOnPauseOrResumeListener) {
    this.mOnPauseOrResumeListener = xOnPauseOrResumeListener;
  }

  public interface onCycleClickListener {
    void onCycleClick (int position);
  }

  public void setItemClick(onCycleClickListener xOnCycleClickListener) {
    this.mOnCycleClickListener = xOnCycleClickListener;
  }

  public interface onHighlightListener {
    void onCycleHighlight (List<Integer> listOfPositions, boolean addButtons);
  }

  public void setHighlight(onHighlightListener xOnHighlightListener) {
    this.mOnHighlightListener = xOnHighlightListener;
  }

  public interface onResumeOrResetCycle {
    void ResumeOrResetCycle(int resumingOrResetting);
  }

  public void setResumeOrResetCycle(onResumeOrResetCycle xOnResumeOrResetCycle) {
    this.mOnResumeOrResetCycle = xOnResumeOrResetCycle;
  }

  public interface onTdeeModeToggle {
    void toggleTdeeMode(int positionToToggle);
  }

  public void setTdeeToggle(onTdeeModeToggle xOnTdeeModeToggle) {
    this.mOnTdeeModeToggle = xOnTdeeModeToggle;
  }

  public void setScreenHeight(int height) {
    this.mScreenHeight = height;
  }

  public void setDayOrNightMode(int themeMode) {
    this.mThemeMode = themeMode;
  }

  public void setColorSettingsFromMainActivity(int typeOFRound, int settingNumber) {
    if (typeOFRound==1) SET_COLOR = changeSettingsValues.assignColor(settingNumber);
    if (typeOFRound==2) BREAK_COLOR = changeSettingsValues.assignColor(settingNumber);
  }

  public boolean isCycleActive() {
    return mActiveCycle;
  }

  public boolean isHighlightModeActive() {
    return mHighlightMode;
  }

  //Remember, constructor always called first (i.e. can't instantiate anything here based on something like setList's size, etc.).
  public SavedCycleAdapter (Context context, ArrayList<String> workoutList, ArrayList<String> typeOfRound, ArrayList<String> workoutTitle, ArrayList<Boolean> tdeeActivityExistsInCycleList, ArrayList<Boolean> activeTdeeModeBooleanList, ArrayList<String> workOutActivityString) {
    this.mContext = context; mWorkoutList = workoutList; this.mTypeOfRound = typeOfRound; this.mWorkoutTitle = workoutTitle; this.mTdeeActivityExistsInCycleList = tdeeActivityExistsInCycleList; this.mActiveTdeeModeBooleanList = activeTdeeModeBooleanList; this.mWorkoutActivityString = workOutActivityString;
    //Must be instantiated here so it does not loop and reset in onBindView.
    mHighlightPositionList = new ArrayList<>();
    //Resets our cancel so bindView does not continuously call black backgrounds.
    mHighlightDeleted = false;

    changeSettingsValues = new ChangeSettingsValues(mContext);

    fullViewBackgroundColor = ContextCompat.getColor(mContext, R.color.night_shadow);
  }

  public void setTimerIsPaused(boolean paused) {
    this.mTimerPaused = paused;
  }

  public void exitHighlightMode() {
    mHighlightDeleted = true;
    mHighlightMode = false;
  }

  public void setCycleAsActive() {
    mActiveCycle = true;
  }

  public void removeCycleAsActive() {
    mActiveCycle = false;
  }

  public void setActiveCyclePosition(int position) {
    this.mPositionOfActiveCycle = position;
  }

  public void setNumberOfRoundsCompleted(int number) {
    this.mNumberOfRoundsCompleted = number;
  }

  public void modifyActiveTdeeModeToggleList(int positionToToggle) {
    if (mActiveTdeeModeBooleanList.get(positionToToggle)) {
      mActiveTdeeModeBooleanList.set(positionToToggle, false);
    } else {
      mActiveTdeeModeBooleanList.set(positionToToggle, true);
    }
  }

  public boolean getBooleanDeterminingIfCycleHasActivity(int position) {
    return mTdeeActivityExistsInCycleList.get(position);
  }

  //This is accessed by Main when launching timer to determine whether we are tracking.
  public boolean getBooleanDeterminingIfWeAreTrackingActivity(int position) {
    return mActiveTdeeModeBooleanList.get(position);
  }

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    View view;

    if (mScreenHeight <=1980) {
      view = LayoutInflater.from(context).inflate(R.layout.workout_cycles_recycler_h1920, parent, false);
    } else {
      view = LayoutInflater.from(context).inflate(R.layout.workout_cycles_recycler, parent, false);
    }

    return new WorkoutHolder(view);
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    WorkoutHolder workoutHolder = (WorkoutHolder) holder;

    workoutHolder.pauseOrResume.setVisibility(View.GONE);
    workoutHolder.resetCycle.setVisibility(View.GONE);

    if (mHighlightDeleted) {
      mHighlightPositionList.clear();
      mHighlightMode = false;
    }

    if (mThemeMode == DAY_MODE) {
      workoutHolder.tdeeActivityStringToggleTextView.setTextColor(Color.BLACK);
    }
    if (mThemeMode == NIGHT_MODE) {
      workoutHolder.tdeeActivityStringToggleTextView.setTextColor(Color.WHITE);
    }

    if (mTdeeActivityExistsInCycleList.get(position)) {
      workoutHolder.tdeeActivityStringToggleTextView.setText(mWorkoutActivityString.get(position));
      workoutHolder.tdeeActivityStringToggleTextView.setVisibility(View.VISIBLE);
    } else {
      workoutHolder.tdeeActivityStringToggleTextView.setVisibility(View.GONE);
    }


    if (mActiveTdeeModeBooleanList.get(position)) {
      workoutHolder.tdeeActivityStringToggleTextView.setAlpha(1.0f);
//      workoutHolder.workoutName.setAlpha(0.3f);
    } else {
      workoutHolder.tdeeActivityStringToggleTextView.setAlpha(0.3f);
//      workoutHolder.workoutName.setAlpha(1.0f);
    }

    workoutHolder.workoutName.setText(mWorkoutTitle.get(position));
    workoutHolder.workOutCycle.setTextColor(ContextCompat.getColor(mContext, R.color.white));

    permSpan = "";
    String bullet = mContext.getString(R.string.bullet);

    String tempWorkoutString = convertTime(mWorkoutList).get(position);
    tempWorkoutString = tempWorkoutString.replace(" ", "");

    String tempTypeString = mTypeOfRound.get(position);
    String[] tempWorkoutArray = tempWorkoutString.split(bullet);
    String[] tempTypeArray = tempTypeString.split(" - ");

    int tempSpace = 0;

    for (int j = 0; j < tempTypeArray.length; j++) {
      if (j == tempTypeArray.length - 1) {
        bullet = "";
      }

      if (tempTypeArray[j].contains("1") || tempTypeArray[j].contains("3")) {
        span = new SpannableString(tempWorkoutArray[j] + " " + bullet + " ");
      } else {
        if (j==0) {
          span = new SpannableString(tempWorkoutArray[j] +  mContext.getString(R.string.hollow_up_arrow) + " " + bullet + " ");
        } else {
          span = new SpannableString(tempWorkoutArray[j] + mContext.getString(R.string.hollow_up_arrow) + " " + bullet + " ");
        }
      }

      if (j != tempTypeArray.length - 1) {
        tempSpace = span.length() - 3;
      } else {
        tempSpace = span.length() - 2;
      }

      if (tempTypeArray[j].contains("1") || tempTypeArray[j].contains("2")) {
        span.setSpan(new ForegroundColorSpan(SET_COLOR), 0, tempSpace, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
      } else {
        span.setSpan(new ForegroundColorSpan(BREAK_COLOR), 0, tempSpace, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
      }

      if (mActiveCycle) {
        if (position==mPositionOfActiveCycle) {
          if (j<=mNumberOfRoundsCompleted-1) {
            span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.mid_grey)), 0, tempSpace, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
          }
          if (j==mNumberOfRoundsCompleted) {
            span.setSpan(new StyleSpan(Typeface.ITALIC), 0, tempSpace, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
          }
        }
      }
      permSpan = TextUtils.concat(permSpan, span);
    }

    boolean spacingChanged = false;

    if (tempTypeArray.length >= 9) {
      for (int k = 0; k < tempTypeArray.length; k++) {
        if (k >= 8) {
          if (!spacingChanged) {
            if (tempTypeArray[k].contains("2") || tempTypeArray[k].contains("4")) {
              k = tempTypeArray.length;
            } else {
              workoutHolder.workOutCycle.setLineSpacing(0, 1.3f);
              spacingChanged = true;
            }
          }
        }
      }
    }

    workoutHolder.workOutCycle.setText(permSpan);

    workoutHolder.tdeeActivityStringToggleTextView.setOnClickListener(v-> {
      if (!mActiveCycle && !mHighlightMode) {
        mOnTdeeModeToggle.toggleTdeeMode(position);
      }
    });

    workoutHolder.fullView.setOnClickListener(v -> {
      boolean changed = false;
      //If not in highlight mode, launch our timer activity from cycle clicked on. Otherwise, clicking on any given cycle highlights it.
      if (!mHighlightMode) {
        if (mActiveCycle) {
          if (position == mPositionOfActiveCycle) {
            mOnResumeOrResetCycle.ResumeOrResetCycle(RESUMING_CYCLE_FROM_TIMER);
          }
        } else {
          mOnCycleClickListener.onCycleClick(position);
        }
        workoutHolder.fullView.setBackgroundColor(fullViewBackgroundColor);
      } else {
        ArrayList<Integer> tempList = new ArrayList<>(mHighlightPositionList);
        for (int i = 0; i < mWorkoutList.size(); i++) {
          //Using tempList for stable loop since mHighlightPositionList changes.
          for (int j = 0; j < tempList.size(); j++) {
            //If our cycle position matches a value in our "highlighted positions list", we un-highlight it, and remove it from our list.
            if (position==tempList.get(j)) {
              workoutHolder.fullView.setBackgroundColor(fullViewBackgroundColor);
              mHighlightPositionList.remove(Integer.valueOf(position));
              //Since we want a single highlight toggle per click, our boolean set to true will preclude the addition of a highlight below.
              changed = true;
            }
          }
        }
        if (!changed) {
          mHighlightPositionList.add(position);
          workoutHolder.fullView.setBackgroundColor(Color.GRAY);
        }
        mOnHighlightListener.onCycleHighlight(mHighlightPositionList, false);
      }
    });

    workoutHolder.fullView.setOnLongClickListener(v -> {
      if (!mHighlightMode && !mActiveCycle) {
        //Adds position of clicked item to position list.
        mHighlightPositionList.add(position);
        workoutHolder.fullView.setBackgroundColor(Color.GRAY);

        mHighlightMode = true;
        mOnHighlightListener.onCycleHighlight(mHighlightPositionList, true);
      }
      return true;
    });

    if (mActiveCycle) {
      workoutHolder.resetCycle.setText(R.string.reset);

      if (position==mPositionOfActiveCycle) {
        if (mTimerPaused) {
          workoutHolder.pauseOrResume.setText(R.string.resume);
        } else {
          workoutHolder.pauseOrResume.setText(R.string.pause);
        }

//        workoutHolder.pauseOrResume.setVisibility(View.VISIBLE);
//        workoutHolder.resetCycle.setVisibility(View.VISIBLE);

        workoutHolder.fullView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.cycle_row_edit_border));

        workoutHolder.pauseOrResume.setOnClickListener(v-> {
          mOnPauseOrResumeListener.onPauseOrResume(mTimerPaused);
        });

        workoutHolder.resetCycle.setOnClickListener(v-> {
          workoutHolder.pauseOrResume.setText(R.string.resume);
          mOnResumeOrResetCycle.ResumeOrResetCycle(RESETTING_CYCLE_FROM_TIMER);
        });

      } else {
        workoutHolder.fullView.setBackgroundColor(fullViewBackgroundColor);
      }
    } else {
      workoutHolder.fullView.setBackgroundColor(fullViewBackgroundColor);
    }
  }

  @Override
  public int getItemCount() {
    return mWorkoutList.size();
  }

  public class WorkoutHolder extends RecyclerView.ViewHolder {
    public View fullView;
    public TextView workoutName;
    public TextView workOutCycle;
    public TextView pauseOrResume;
    public TextView resetCycle;
    public TextView tdeeActivityStringToggleTextView;

    ConstraintLayout.LayoutParams workoutNameLayoutParams;
    ConstraintLayout.LayoutParams workoutCyclesLayoutParams;

    @SuppressLint("ResourceAsColor")
    public WorkoutHolder(@NonNull View itemView) {
      super(itemView);
      fullView = itemView;
      workoutName = itemView.findViewById(R.id.custom_name_header);
      workOutCycle = itemView.findViewById(R.id.saved_custom_set_view);
      pauseOrResume = itemView.findViewById(R.id.pause_or_resume_cycle_button_for_mode_1);
      resetCycle = itemView.findViewById(R.id.reset_active_cycle_button_for_mode_1);
      tdeeActivityStringToggleTextView = itemView.findViewById(R.id.activity_string_textView_for_tracking_cycles);

      workoutNameLayoutParams = (ConstraintLayout.LayoutParams) workoutName.getLayoutParams();
      workoutCyclesLayoutParams = (ConstraintLayout.LayoutParams) workOutCycle.getLayoutParams();
    }
  }

  public ArrayList<String> convertTime(ArrayList<String> time) {
    ArrayList<Long> newLong = new ArrayList<>();
    ArrayList<String> newString = new ArrayList<>();
    String listConv = "";
    String[] newSplit = null;
    String finalSplit = "";
    ArrayList<String> finalList = new ArrayList<>();

    for (int i=0; i<time.size(); i++) {
      //Getting each String entry from String Array.
      listConv = String.valueOf(time.get(i));
      //Splitting into String[] entries.
      newSplit = listConv.split(" - ", 0);

      for (int k=0; k<newSplit.length; k++) {
        //Creating new ArrayList of Long values.
        newLong.add(Long.parseLong(newSplit[k]));
        //Converting each Long value into a String we can display.
        newString.add(convertSeconds(newLong.get(k)));
        //If in Pom mode, set "0" for a time entry that is <10 minutes/4 length (e.g. X:XX), and "1" for >=10 minutes/5 length (e.g. XX:XX). This is so we can properly alternate green/red coloring in onBindView's Spannable.
      }
      finalSplit = String.valueOf(newString);
      finalSplit = finalSplit.replace("[", "");
      finalSplit = finalSplit.replace("]", "");
      finalSplit = finalSplit.replace(",", " " + mContext.getString(R.string.bullet));
      finalList.add(finalSplit);

      newLong = new ArrayList<>();
      newString = new ArrayList<>();
    }
    return finalList;
  }

  public String convertSeconds(long totalSeconds) {
    DecimalFormat df = new DecimalFormat("00");
    long minutes;
    long remainingSeconds;
    totalSeconds = totalSeconds/1000;

    if (totalSeconds >=60) {
      minutes = totalSeconds/60;
      remainingSeconds = totalSeconds % 60;
      return (minutes + ":" + df.format(remainingSeconds));
    } else if (totalSeconds >=10) return "0:" + totalSeconds;
    else return "0:0" + totalSeconds;
  }
}