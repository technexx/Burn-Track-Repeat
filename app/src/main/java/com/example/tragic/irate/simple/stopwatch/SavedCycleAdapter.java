package com.example.tragic.irate.simple.stopwatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
  Spannable span;
  ImageSpan imageSpan;

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
      //Var used to determine spannable spacing.
      int tempSpace = 0;
      //Bullet string. Cleared if on final round.
      String bullet = mContext.getString(R.string.bullet);
      //Iterates through the length of our split roundType array, which will always correspond to the length of our split workout array.
      for (int j=0; j<tempTypeArray.length; j++) {
        //If we are on the last round object, clear our bullet String so the view does not end with one.
        if (j==tempTypeArray.length-1) bullet = "";

        //If round is counting up, create a Spannable w/ the count-down time of the round. Otherwise, create a new Spannable w/ a placeholder for an ImageSpan.
        if (tempTypeArray[j].contains("1") || (tempTypeArray[j].contains("3"))) {
          span = new SpannableString( tempWorkoutArray[j] + bullet);
          //tempSpace is used as the "end" mark of our Spannable object manipulation. We set it to 2 spaces less than the span's length so we leave the bullet occupying the last places [space + bullet] alone).
          tempSpace = span.length()-2;
        } else {
          span = new SpannableString("   " + bullet);
          //Our ImageSpan is set (below) on indices 1 and 2, so we set tempSpace to 2 to cover its entirety (i.e. changing its color/size).
          tempSpace = 2;
          //If roundType is 2 (sets), use green infinity drawable for ImageSpan. If roundType is 4 (breaks), use red.
          if (tempTypeArray[j].contains("2")) imageSpan = new ImageSpan(mContext, R.drawable.infinity_small_green);
          else imageSpan = new ImageSpan(mContext, R.drawable.infinity_small_red);
        }

        //If our roundType object contains a 1 or 2, it refers to a SET, and we set its corresponding workout object to green. Otherwise, it refers to a BREAK, and we set its color to red.
        if (tempTypeArray[j].contains("1") || tempTypeArray[j].contains("2")) {
          span.setSpan(new ForegroundColorSpan(Color.GREEN), 0, tempSpace, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        } else span.setSpan(new ForegroundColorSpan(Color.RED), 0, tempSpace, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        if (tempTypeArray[j].contains("2") || tempTypeArray[j].contains("4")) {
          //If using infinity drawable, increase its size.
          span.setSpan(new AbsoluteSizeSpan(18, true),0, tempSpace, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
          //Setting our Spannable, which can be concatenated w/ permSpan object in TextUtils below, to our imageSpan. We run from index 1-2 inclusive because 0 is used as an empty separator space (see: original Spannable span creation above).
          span.setSpan(imageSpan, 1, 2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }

        //Within this loop, we update our permSpan charSequence with the new workout Spannable object.
        permSpan = TextUtils.concat(permSpan, span);
      }
      workoutHolder.workOutCycle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
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
    public ImageView infinityRounds;
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