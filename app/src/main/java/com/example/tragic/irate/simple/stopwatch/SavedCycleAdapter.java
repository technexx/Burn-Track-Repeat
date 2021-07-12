package com.example.tragic.irate.simple.stopwatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
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
  ArrayList<String> mSetsList;
  ArrayList<String> mBreaksList;
  ArrayList<String> mBreaksOnlyList;
  ArrayList<String> mTitle;
  ArrayList<String> mBreaksOnlyTitle;
  ArrayList<String> mPomList;
  ArrayList<String> mPomTitle;
  onCycleClickListener mOnCycleClickListener;
  onHighlightListener mOnHighlightListener;
  onInfinityToggleListener mOnInfinityToggleListener;
  onInfinityToggleListenerTwo mOnInfinityToggleListenerTwo;
  public static final int SETS_AND_BREAKS = 1;
  public static final int BREAKS_ONLY = 2;
  public static final int POMODORO = 3;
  int mChosenView;
  boolean mHighlightDeleted;
  boolean mHighlightMode;
  List<String> mPositionList;
  ArrayList<Integer> mInfinityArrayOne = new ArrayList<>();
  ArrayList<Integer> mInfinityArrayTwo = new ArrayList<>();
  ArrayList<Integer> mInfinityArrayThree = new ArrayList<>();
  float mPosX;
  ArrayList<Integer> mSizeToggle = new ArrayList<>();

  public interface onCycleClickListener {
    void onCycleClick (int position);
  }

  public interface onHighlightListener {
    void onCycleHighlight (List<String> listOfPositions, boolean addButtons);
  }

  public interface onInfinityToggleListener {
    void onInfinityToggle(ArrayList<Integer> toggleSets, ArrayList<Integer> toggleBreaks, int position);
  }

  public interface onInfinityToggleListenerTwo {
    void onInfinityToggleTwo(ArrayList<Integer> toggleBreaksOnly, int position);
  }

  public void setItemClick(onCycleClickListener xOnCycleClickListener) {
    this.mOnCycleClickListener = xOnCycleClickListener;
  }

  public void setHighlight(onHighlightListener xOnHighlightListener) {
    this.mOnHighlightListener = xOnHighlightListener;
  }

  public void setsInfinityToggle(onInfinityToggleListener xOnInfinityToggleListener) {
    this.mOnInfinityToggleListener = xOnInfinityToggleListener;
  }

  public void setsInfinityToggleTwo(onInfinityToggleListenerTwo xOnInfinityToggleListenerTwo) {
    this.mOnInfinityToggleListenerTwo = xOnInfinityToggleListenerTwo;
  }

  public SavedCycleAdapter() {
  }

  //Remember, constructor always called first (i.e. can't instantiate anything here based on something like setList's size, etc.).
  public SavedCycleAdapter (Context context, ArrayList<String> setsList, ArrayList<String> breaksList, ArrayList<String> breaksOnlyList, ArrayList<String> pomList, ArrayList<String> title, ArrayList<String> breaksOnlyTitle, ArrayList<String> pomTitle) {
    this.mContext = context; mSetsList = setsList; mBreaksList = breaksList; mBreaksOnlyList = breaksOnlyList; this.mPomList = pomList; this.mTitle = title; this.mBreaksOnlyTitle = breaksOnlyTitle; this.mPomTitle = pomTitle;
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

  public void receiveInfinityMode(ArrayList<Integer> infinityListOne, ArrayList<Integer> infinityListTwo) {
    mInfinityArrayOne = infinityListOne; mInfinityArrayTwo = infinityListTwo;
  }

  public void receiveInfinityModeTwo(ArrayList<Integer> infinityListThree) {
    mInfinityArrayThree = infinityListThree;
  }

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    if (viewType == SETS_AND_BREAKS) {
      View view = LayoutInflater.from(context).inflate(R.layout.mode_one_cycles, parent, false);
      return new CustomHolder(view);
    } else if (viewType == BREAKS_ONLY){
      View view = LayoutInflater.from(context).inflate(R.layout.mode_two_cycles, parent, false);
      return new BreaksOnlyHolder(view);
    } else if (viewType == POMODORO) {
      View view = LayoutInflater.from(context).inflate(R.layout.mode_three_cycles, parent, false);
      return new PomHolder(view);
    } else return null;
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    //Used to store highlighted positions that we callback to Main to delete.
    if (holder instanceof CustomHolder) {
      CustomHolder customHolder = (CustomHolder) holder;
      customHolder.customName.setText(mTitle.get(position));
      customHolder.customSet.setText(convertTime(mSetsList).get(position));
      customHolder.customBreak.setText(convertTime(mBreaksList).get(position));

      //If creating a new cycle, this ensures the most recently added set/break will have a corresponding infinity entry so we do not try to fetch from an index in that list that does not exist (i.e., the newly added set/break will have a "0", or "off", entry created for it in the infinity list).
      if (mInfinityArrayOne.size()<mSetsList.size()) mInfinityArrayOne.add(0);
      if (mInfinityArrayTwo.size()<mSetsList.size()) mInfinityArrayTwo.add(0);

      //Arrays One/Two correspond to the entire list of saved Sets/Breaks, and have a 0 or 1 value to mark our infinity mode as on/off. Since this is in our bindViewHolder, it will execute for every adapter position we have.
      if (mInfinityArrayOne.get(position)==0)
        customHolder.infinity_green_cycles.setAlpha(0.35f); else customHolder.infinity_green_cycles.setAlpha(1.0f);
      if (mInfinityArrayTwo.get(position)==0)
        customHolder.infinity_red_cycles.setAlpha(0.35f); else customHolder.infinity_red_cycles.setAlpha(1.0f);

      //Toggles infinity mode on/off, sets (replaces) the corresponding position clicked on to that state, and calls back both arrays, along with the position, to our Main activity.
      customHolder.infinity_green_cycles.setOnClickListener(v -> {

        if (customHolder.infinity_green_cycles.getAlpha() == 1.0f) {
          customHolder.infinity_green_cycles.setAlpha(0.35f);
          mInfinityArrayOne.set(position, 0);
        } else {
          customHolder.infinity_green_cycles.setAlpha(1.0f);
          mInfinityArrayOne.set(position, 1);
        }
        mOnInfinityToggleListener.onInfinityToggle(mInfinityArrayOne, mInfinityArrayTwo, position);
      });

      customHolder.infinity_red_cycles.setOnClickListener(v -> {
        if (customHolder.infinity_red_cycles.getAlpha() == 1.0f) {
          customHolder.infinity_red_cycles.setAlpha(0.35f);
          mInfinityArrayTwo.set(position, 0);
        } else {
          customHolder.infinity_red_cycles.setAlpha(1.0f);
          mInfinityArrayTwo.set(position, 1);
        }
        mOnInfinityToggleListener.onInfinityToggle(mInfinityArrayOne, mInfinityArrayTwo, position);
      });

      if (mHighlightDeleted) {
        //Clears highlight list.
        mPositionList.clear();
        //Turns our highlight mode off so single clicks launch a cycle instead of highlight it for deletion.
        mHighlightMode = false;
        //Sets all of our backgrounds to black (unhighlighted).
        for (int i = 0; i < mSetsList.size(); i++) {
          customHolder.fullView.setBackgroundColor(Color.BLACK);
        }
      }

      //Grabs our X-axis value so we can limit the area that onClick triggers.
      //Return values: TRUE consumes (ends) event, so onClick does not trigger. FALSE allows further methods (e.g. onClick) to continue.
      customHolder.fullView.setOnTouchListener((v, event) -> {
        if (event.getAction()==MotionEvent.ACTION_DOWN) {
          mPosX = event.getX();
        }
        return false;
      });

      customHolder.fullView.setOnClickListener(v -> {
        boolean changed = false;
        //If not in highlight mode, launch our timer activity from cycle clicked on. Otherwise, clicking on any given cycle highlights it.
        if (!mHighlightMode) {
          //Excludes the X-axis where we keep our infinity symbols from launching timer cycle.
          if (mPosX<900) mOnCycleClickListener.onCycleClick(position);
        } else {
          ArrayList<String> tempList = new ArrayList<>(mPositionList);
          //Iterate through every cycle in list.
          for (int i = 0; i < mSetsList.size(); i++) {
            //Using tempList for stable loop since mPositionList changes.
            for (int j = 0; j < tempList.size(); j++) {
              //If our cycle position matches a value in our "highlighted positions list", we un-highlight it, and remove it from our list.
              if (String.valueOf(position).contains(tempList.get(j))) {
                customHolder.fullView.setBackgroundColor(Color.BLACK);
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
            customHolder.fullView.setBackgroundColor(Color.GRAY);
          }
          //Callback to send position list (Using Strings to make removing values easier) back to Main.
          mOnHighlightListener.onCycleHighlight(mPositionList, false);
        }
      });

      //Highlight cycle on long click and make visible action bar buttons. Sets mHighlightMode to true so no cycles can be launched in timer.
      customHolder.fullView.setOnLongClickListener(v -> {
        if (!mHighlightMode) {
          mPositionList.add(String.valueOf(position));
          customHolder.fullView.setBackgroundColor(Color.GRAY);
          mHighlightMode = true;
          //Calls back for initial highlighted position, Also to set actionBar views for highlight mode.
          mOnHighlightListener.onCycleHighlight(mPositionList, true);
        }
        return true;
      });

    } else if (holder instanceof BreaksOnlyHolder) {
      BreaksOnlyHolder breaksOnlyHolder = (BreaksOnlyHolder) holder;
      breaksOnlyHolder.breaksOnlyName.setText(mBreaksOnlyTitle.get(position));
      breaksOnlyHolder.breaksOnlyBreak.setText(convertTime(mBreaksOnlyList).get(position));

      if (mInfinityArrayThree.size()>0) if (mInfinityArrayThree.get(position)==0)
        breaksOnlyHolder.infinity_red_cycles.setAlpha(0.35f); else breaksOnlyHolder.infinity_red_cycles.setAlpha(1.0f);

      breaksOnlyHolder.infinity_red_cycles.setOnClickListener(v-> {
        if (breaksOnlyHolder.infinity_red_cycles.getAlpha() == 1.0f) {
          breaksOnlyHolder.infinity_red_cycles.setAlpha(0.35f);
          mInfinityArrayThree.set(position, 0);
        } else {
          breaksOnlyHolder.infinity_red_cycles.setAlpha(1.0f);
          mInfinityArrayThree.set(position, 1);
        }
        mOnInfinityToggleListenerTwo.onInfinityToggleTwo(mInfinityArrayThree, position);
      });

      if (mHighlightDeleted) {
        //Clears highlight list.
        mPositionList.clear();
        //Turns our highlight mode off so single clicks launch a cycle instead of highlight it for deletion.
        mHighlightMode = false;
        //Sets all of our backgrounds to black (unhighlighted).
        for (int i=0; i<mBreaksOnlyList.size(); i++) {
          breaksOnlyHolder.fullView.setBackgroundColor(Color.BLACK);
        }
      }

      breaksOnlyHolder.fullView.setOnTouchListener((v, event) -> {
        if (event.getAction()==MotionEvent.ACTION_DOWN) {
          mPosX = event.getX();
        }
        return false;
      });

      breaksOnlyHolder.fullView.setOnClickListener(v -> {
        boolean changed = false;
        if (!mHighlightMode) {
          if (mPosX<900) mOnCycleClickListener.onCycleClick(position);
        } else {
          ArrayList<String> tempList = new ArrayList<>(mPositionList);
          //Iterate through every cycle in list.
          for (int i=0; i<mBreaksOnlyList.size(); i++) {
            //Using tempList for stable loop since mPositionList changes.
            for (int j=0; j<tempList.size(); j++) {
              //If our cycle position matches a value in our "highlighted positions list", we un-highlight it, and remove it from our list.
              if (String.valueOf(position).contains(tempList.get(j))) {
                breaksOnlyHolder.fullView.setBackgroundColor(Color.BLACK);
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
            breaksOnlyHolder.fullView.setBackgroundColor(Color.GRAY);
          }
          //Callback to send position list (Using Strings to make removing values easier) back to Main.
          mOnHighlightListener.onCycleHighlight(mPositionList, false);
        }
      });

      breaksOnlyHolder.fullView.setOnLongClickListener(v-> {
        if (!mHighlightMode) {
          mPositionList.add(String.valueOf(position));
          breaksOnlyHolder.fullView.setBackgroundColor(Color.GRAY);
          mHighlightMode = true;
          //Calls back for initial highlighted position, Also to set actionBar views for highlight mode.
          mOnHighlightListener.onCycleHighlight(mPositionList, true);
        }
        return true;
      });

    } else if (holder instanceof PomHolder) {
      //Todo: Entries are fine. Display OR constructor values are issue.
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
        for (int i=0; i<mSetsList.size(); i++) {
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
      case 2:
        return BREAKS_ONLY;
      case 3:
        return POMODORO;
    }
    return 0;
  }

  @Override
  public int getItemCount() {
    switch (mChosenView) {
      case 1:
        return mSetsList.size();
      case 2:
        return mBreaksOnlyList.size();
      case 3:
        return mPomList.size();
    }
    return 0;
  }

  public class CustomHolder extends RecyclerView.ViewHolder {
    public TextView customName;
    public TextView customSet;
    public TextView customBreak;
    public View fullView;
    public ImageView infinity_green_cycles;
    public ImageView infinity_red_cycles;

    @SuppressLint("ResourceAsColor")
    public CustomHolder(@NonNull View itemView) {
      super(itemView) ;
      customName = itemView.findViewById(R.id.custom_name_header);
      customSet = itemView.findViewById(R.id.saved_custom_set_view);
      customBreak = itemView.findViewById(R.id.saved_custom_break_view);
      fullView = itemView;
      infinity_green_cycles = itemView.findViewById(R.id.infinity_green_cycles);
      infinity_red_cycles = itemView.findViewById(R.id.infinity_red_cycles);
    }
  }

  public class BreaksOnlyHolder extends RecyclerView.ViewHolder {
    public TextView breaksOnlyName;
    public TextView breaksOnlyBreak;
    public View fullView;
    public ImageView infinity_red_cycles;

    public BreaksOnlyHolder(@NonNull View itemView) {
      super(itemView);
      breaksOnlyName = itemView.findViewById(R.id.breaks_only_header);
      breaksOnlyBreak = itemView.findViewById(R.id.saved_breaks_only_view);
      fullView = itemView;
      infinity_red_cycles = itemView.findViewById(R.id.infinity_red_cycles);
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