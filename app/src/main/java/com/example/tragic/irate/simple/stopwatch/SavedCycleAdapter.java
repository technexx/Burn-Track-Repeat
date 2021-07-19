package com.example.tragic.irate.simple.stopwatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SavedCycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  Context mContext;
  ArrayList<String> mWorkoutList;
  ArrayList<String> mRoundType;
  ArrayList<String> mWorkoutTitle;
  ArrayList<String> mPomList;
  ArrayList<String> mPomTitle;
  onCycleClickListener mOnCycleClickListener;
  onHighlightListener mOnHighlightListener;
  public static final int SETS_AND_BREAKS = 1;
  public static final int POMODORO = 3;
  int mChosenView;
  boolean mHighlightDeleted;
  boolean mHighlightMode;
  List<String> mPositionList;
  ArrayList<Integer> mSizeToggle = new ArrayList<>();
  CharSequence permSpan;

  public interface onCycleClickListener {
    void onCycleClick (int position);
  }

  public interface onHighlightListener {
    void onCycleHighlight (List<String> listOfPositions, boolean addButtons);
  }

  public void setItemClick(onCycleClickListener xOnCycleClickListener) {
    this.mOnCycleClickListener = xOnCycleClickListener;
  }

  public void setHighlight(onHighlightListener xOnHighlightListener) {
    this.mOnHighlightListener = xOnHighlightListener;
  }

  public SavedCycleAdapter() {
  }

  //Remember, constructor always called first (i.e. can't instantiate anything here based on something like setList's size, etc.).
  public SavedCycleAdapter (Context context, ArrayList<String> workoutList, ArrayList<String> roundType, ArrayList<String> pomList, ArrayList<String> workoutTitle, ArrayList<String> pomTitle) {
    this.mContext = context; mWorkoutList = workoutList; this.mRoundType = roundType; this.mPomList = pomList; this.mWorkoutTitle = workoutTitle; this.mPomTitle = pomTitle;
    //Must be instantiated here so it does not loop and reset in onBindView.
    mPositionList = new ArrayList<>();
    //Resets our cancel so bindView does not continuously call black backgrounds.
    mHighlightDeleted = false;
    //Populates a toggle list for Pom's spannable colors so we can simply replace them at will w/ out resetting the list. This should only be called in our initial adapter instantiation.
    if (mSizeToggle.size()==0) for (int i=0; i<8; i++) mSizeToggle.add(0);
  }

  public void setView(int chosenView) {
    this.mChosenView = chosenView;
  }

  public void removeHighlight(boolean cancelMode) {
    //If boolean is false, highlight has simply been deleted and we clear the highlight list while turning all backgrounds black.
    mHighlightDeleted = true;
    //If boolean is true, we have canceled the highlight process entirely, which does the above but also removes the Trash/Back buttons (done in Main) and sets the next row click to launch a timer instead of highlight (done here).
    if (cancelMode) mHighlightMode = false;
  }

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    if (viewType == SETS_AND_BREAKS) {
      View view = LayoutInflater.from(context).inflate(R.layout.mode_one_cycles, parent, false);
      return new WorkoutHolder(view);
    } else if (viewType == POMODORO) {
      View view = LayoutInflater.from(context).inflate(R.layout.mode_three_cycles, parent, false);
      return new PomHolder(view);
    } else return null;
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    //Used to store highlighted positions that we callback to Main to delete.
    if (holder instanceof WorkoutHolder) {
      WorkoutHolder workoutHolder = (WorkoutHolder) holder;
      workoutHolder.workoutName.setText(mWorkoutTitle.get(position));

      //Clearing Spannable object, since it will re-populate for every position passed in through this method.
      permSpan = "";
      //Retrieves the concatenated String of workout TIMES from current position.
      String tempWorkoutString = convertTime(mWorkoutList).get(position);
      //Retrieves the concatenated String of ROUND TYPES from current position.
      String tempTypeString = mRoundType.get(position);
      //Splits concatenated workout String into String Array.
      String[] tempWorkoutArray = tempWorkoutString.split(mContext.getString(R.string.bullet));
      //Splits concatenated round type String into String Array.
      String[] tempTypeArray = tempTypeString.split(" - ");
      //Spannable object that will correspond to each object in workout array.
      Spannable span;
      //Var used to determine spannable spacing.
      int tempSpace = 0;
      //Iterates through the length of our split roundType array, which will always correspond to the length of our split workout array.
      for (int j=0; j<tempTypeArray.length; j++) {
        //For each object in the roundType array, we create a new spannable object from its corresponding item in the workout array. For every position except the last, add a bullet separator and avoid coloring the last two spaces (for white bullet).
        if (j<tempTypeArray.length-1) {
          span = new SpannableString( tempWorkoutArray[j] + mContext.getString(R.string.bullet));
          tempSpace = span.length()-2;
        }
        else {
          span = new SpannableString(tempWorkoutArray[j]);
          tempSpace = span.length();
        }
        //If our roundType object contains a 1 or 2, it refers to a SET, and we set its corresponding workout object to green. Otherwise, it refers to a BREAK, and we set its color to red.
        if (tempTypeArray[j].contains("1") || tempTypeArray[j].contains("2")) span.setSpan(new ForegroundColorSpan(Color.GREEN), 0, tempSpace, Spanned.SPAN_INCLUSIVE_INCLUSIVE); else span.setSpan(new ForegroundColorSpan(Color.RED), 0, tempSpace, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        //Within this loop, we update our permSpan charSequence with the new workout Spannable object.
        permSpan = TextUtils.concat(permSpan, span);
      }
      workoutHolder.workOutCycle.setText(permSpan);

      if (mHighlightDeleted) {
        //Clears highlight list.
        mPositionList.clear();
        //Turns our highlight mode off so single clicks launch a cycle instead of highlight it for deletion.
        mHighlightMode = false;
        //Sets all of our backgrounds to black (unhighlighted).
        for (int i = 0; i < mWorkoutList.size(); i++) {
          workoutHolder.fullView.setBackgroundColor(Color.BLACK);
        }
      }

      workoutHolder.fullView.setOnClickListener(v -> {
        boolean changed = false;
        //If not in highlight mode, launch our timer activity from cycle clicked on. Otherwise, clicking on any given cycle highlights it.
        if (!mHighlightMode) mOnCycleClickListener.onCycleClick(position); else {
          ArrayList<String> tempList = new ArrayList<>(mPositionList);
          //Iterate through every cycle in list.
          for (int i = 0; i < mWorkoutList.size(); i++) {
            //Using tempList for stable loop since mPositionList changes.
            for (int j = 0; j < tempList.size(); j++) {
              //If our cycle position matches a value in our "highlighted positions list", we un-highlight it, and remove it from our list.
              if (String.valueOf(position).contains(tempList.get(j))) {
                workoutHolder.fullView.setBackgroundColor(Color.BLACK);
                mPositionList.remove(String.valueOf(position));
                //Since we want a single highlight toggle per click, our boolean set to true will preclude the addition of a highlight below.
                changed = true;
              }
            }
          }
          //If we have not toggled our highlight off above, toggle it on below.
          if (!changed) {
            //Adds the position at its identical index for easy removal access.
            mPositionList.add(String.valueOf(position));
            workoutHolder.fullView.setBackgroundColor(Color.GRAY);
          }
          //Callback to send position list (Using Strings to make removing values easier) back to Main.
          mOnHighlightListener.onCycleHighlight(mPositionList, false);
        }
      });

      //Highlight cycle on long click and make visible action bar buttons. Sets mHighlightMode to true so no cycles can be launched in timer.
      workoutHolder.fullView.setOnLongClickListener(v -> {
        if (!mHighlightMode) {
          mPositionList.add(String.valueOf(position));
          workoutHolder.fullView.setBackgroundColor(Color.GRAY);
          mHighlightMode = true;
          //Calls back for initial highlighted position, Also to set actionBar views for highlight mode.
          mOnHighlightListener.onCycleHighlight(mPositionList, true);
        }
        return true;
      });

    } else if (holder instanceof PomHolder) {
      PomHolder pomHolder = (PomHolder) holder;
      pomHolder.pomName.setText(mPomTitle.get(position));

      String tempPom = (convertTime(mPomList).get(position));
      tempPom = tempPom.replace("-", mContext.getString(R.string.bullet));
      Spannable pomSpan = new SpannableString(tempPom);

      //Sets green/red alternating colors using text char indices.
      int moving = 0;
      int rangeStart = 0;
      int rangeEnd = 4;
      for (int i=0; i<8; i++) {
        if (mSizeToggle.get(i)==1) rangeEnd = 5;
        if (i%2==0) pomSpan.setSpan(new ForegroundColorSpan(Color.GREEN), moving + rangeStart, moving + rangeEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        else pomSpan.setSpan(new ForegroundColorSpan(Color.RED), moving + rangeStart, moving + rangeEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        if (mSizeToggle.get(i)==1) moving+=8; else moving+=7;
      }
      pomHolder.pomView.setText(pomSpan);

      if (mHighlightDeleted) {
        //Clears highlight list.
        mPositionList.clear();
        //Turns our highlight mode off so single clicks launch a cycle instead of highlight it for deletion.
        mHighlightMode = false;
        //Sets all of our backgrounds to black (unhighlighted).
        for (int i=0; i<mPomList.size(); i++) {
          pomHolder.fullView.setBackgroundColor(Color.BLACK);
        }
      }

      pomHolder.fullView.setOnClickListener(v-> {
        boolean changed = false;
        if (!mHighlightMode) mOnCycleClickListener.onCycleClick(position); else {
          ArrayList<String> tempList = new ArrayList<>(mPositionList);

          //Iterate through every cycle in list.
          for (int i=0; i<mPomList.size(); i++) {
            //Using tempList for stable loop since mPositionList changes.
            for (int j=0; j<tempList.size(); j++) {
              //If our cycle position matches a value in our "highlighted positions list", we un-highlight it, and remove it from our list.
              if (String.valueOf(position).contains(tempList.get(j))) {
                pomHolder.fullView.setBackgroundColor(Color.BLACK);
                mPositionList.remove(String.valueOf(position));
                //Since we want a single highlight toggle per click, our boolean set to true will preclude the addition of a highlight below.
                changed = true;
              }
            }
          }
          //If we have not toggled our highlight off above, toggle it on below.
          if (!changed) {
            //Adds the position at its identical index for easy removal access.
            mPositionList.add(String.valueOf(position));
            pomHolder.fullView.setBackgroundColor(Color.GRAY);
          }
          //Callback to send position list (Using Strings to make removing values easier) back to Main.
          mOnHighlightListener.onCycleHighlight(mPositionList, false);
        }
      });

      pomHolder.fullView.setOnLongClickListener(v-> {
        if (!mHighlightMode) {
          mPositionList.add(String.valueOf(position));
          pomHolder.fullView.setBackgroundColor(Color.GRAY);
          mHighlightMode = true;
          //Calls back for initial highlighted position, Also to set actionBar views for highlight mode.
          mOnHighlightListener.onCycleHighlight(mPositionList, true);
        }
        return true;
      });
    }
  }

  @Override
  public int getItemViewType(int position) {
    switch (mChosenView) {
      case 1:
        return SETS_AND_BREAKS;
      case 3:
        return POMODORO;
    }
    return 0;
  }

  @Override
  public int getItemCount() {
    switch (mChosenView) {
      case 1:
        return mWorkoutList.size();
      case 3:
        return mPomList.size();
    }
    return 0;
  }

  public class WorkoutHolder extends RecyclerView.ViewHolder {
    public TextView workoutName;
    public TextView workOutCycle;
    public View fullView;

    @SuppressLint("ResourceAsColor")
    public WorkoutHolder(@NonNull View itemView) {
      super(itemView) ;
      workoutName = itemView.findViewById(R.id.custom_name_header);
      workOutCycle = itemView.findViewById(R.id.saved_custom_set_view);
      fullView = itemView;
    }
  }

  public class PomHolder extends RecyclerView.ViewHolder {
    public TextView pomName;
    public TextView pomView;
    public View fullView;

    public PomHolder(@NonNull View itemView) {
      super(itemView);
      pomName = itemView.findViewById(R.id.pom_header);
      pomView = itemView.findViewById(R.id.pom_view);
      fullView = itemView;
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
        if (mChosenView==POMODORO) if ((newString.get(k)).length()==4) mSizeToggle.set(k, 0); else mSizeToggle.set(k, 1);
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