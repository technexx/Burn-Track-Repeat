package com.example.tragic.irate.simple.stopwatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
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
  boolean mBreaksOnly;
  onCycleClickListener mOnCycleClickListener;
  onDeleteCycleListener mOnDeleteCycleListener;
  onHighlightListener mOnHighlightListener;
  public static final int SETS_AND_BREAKS = 0;
  public static final int BREAKS_ONLY = 1;
  public static final int POMODORO = 2;
  int mChosenView;
  boolean mHighlightMode;
  List<String> mPositionList;

  public interface onCycleClickListener {
    void onCycleClick (int position);
  }

  public interface onDeleteCycleListener {
    void onCycleDelete (int position);
  }

  public interface onHighlightListener {
    void onCycleHighlight (List<String> listOfPositions);
  }

  public void setItemClick(onCycleClickListener xOnCycleClickListener) {
    this.mOnCycleClickListener = xOnCycleClickListener;
  }

  public void setDeleteCycle(onDeleteCycleListener xOnDeleteCycleListener) {
    this.mOnDeleteCycleListener = xOnDeleteCycleListener;
  }

  public void setHighlight(onHighlightListener xOnHighlightListener) {
    this.mOnHighlightListener = xOnHighlightListener;
  }

  public SavedCycleAdapter (Context context, ArrayList<String> setsList, ArrayList<String> breaksList, ArrayList<String> breaksOnlyList, ArrayList<String> pomList, ArrayList<String> title, ArrayList<String> breaksOnlyTitle, ArrayList<String> pomTitle) {
    this.mContext = context; mSetsList = setsList; mBreaksList = breaksList; mBreaksOnlyList = breaksOnlyList; this.mPomList = pomList; this.mTitle = title; this.mBreaksOnlyTitle = breaksOnlyTitle; this.mPomTitle = pomTitle;
    //Must be instantiated here so it does not loop and reset in onBindView.
    mPositionList = new ArrayList<>();
  }

  public void setView(int chosenView) {
    this.mChosenView = chosenView;
  }

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    if (viewType == SETS_AND_BREAKS) {
      View view = LayoutInflater.from(context).inflate(R.layout.saved_cycles_views, parent, false);
      return new CustomHolder(view);
    } else if (viewType == BREAKS_ONLY){
      View view = LayoutInflater.from(context).inflate(R.layout.saved_cycles_breaks_only_views, parent, false);
      return new BreaksOnlyHolder(view);
    } else if (viewType == POMODORO) {
      View view = LayoutInflater.from(context).inflate(R.layout.saved_pom_views, parent, false);
      return new PomHolder(view);
    } else return null;
  }

  //Todo: Remember, this entire method executes as many times as there are position returns, but to ensure we don't run out of bounds we need to limit the mPositionList comparison to its size.
  @SuppressLint("ClickableViewAccessibility")
  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    //Used to store highlighted positions that we callback to Main to delete.
    if (holder instanceof CustomHolder) {
      CustomHolder customHolder = (CustomHolder) holder;
      customHolder.customName.setText(mTitle.get(position));
      customHolder.customSet.setText(convertTime(mSetsList).get(position));
      customHolder.customBreak.setText(convertTime(mBreaksList).get(position));

      customHolder.fullView.setOnClickListener(v -> {
        boolean changed = false;
        //If not in highlight mode, launch our timer activity from cycle clicked on. Otherwise, clicking on any given cycle highlights it.
        if (!mHighlightMode) mOnCycleClickListener.onCycleClick(position); else {
          ArrayList<String> tempList = new ArrayList<>(mPositionList);

          //Iterate through every cycle in list.
          for (int i=0; i<mSetsList.size(); i++) {
            //Using tempList for stable loop since mPositionList changes.
            for (int j=0; j<tempList.size(); j++) {
              //If our cycle position matches a value in our "highlighted positions list", we un-highlight it, and remove it from our list.
              if (String.valueOf(position).contains(tempList.get(j))) {
                customHolder.fullView.setBackgroundColor(Color.BLACK);
                //Adds the position at its identical index for easy removal access
                mPositionList.remove(String.valueOf(position));
                //Since we want a single highlight toggle per click, our boolean set to true will preclude the addition of a highlight below.
                changed = true;
              }
            }
          }
          //If we have not toggle our highlight off above, toggle it on below.
          if (!changed) {
            if (!mPositionList.contains(String.valueOf(position))) {
              mPositionList.add(String.valueOf(position));
              customHolder.fullView.setBackgroundColor(Color.GRAY);
            }
          }
          Log.i("testSize", "mPosition list is " + mPositionList);
          //Callback to send position list (Using Strings to make removing values easier) back to Main.
          mOnHighlightListener.onCycleHighlight(mPositionList);
        }
      });

      //Todo: Add button(s) in supportActionBar for deletions - should be a "Back" arrow to cancel highlights and set mHighlightMode to FALSE again.
      // Callback selected positions->get IDs therefrom in Main.
      //Highlight cycle on long click and make visible action bar buttons. Sets mHighlightMode to true so no cycles can be launched in timer.
      customHolder.fullView.setOnLongClickListener(v-> {
        if (!mHighlightMode) {
          mPositionList.add(String.valueOf(position));
          customHolder.fullView.setBackgroundColor(Color.GRAY);
          mHighlightMode = true;
        }
        return true;
      });

    } else if (holder instanceof BreaksOnlyHolder) {
      BreaksOnlyHolder breaksOnlyHolder = (BreaksOnlyHolder) holder;
      breaksOnlyHolder.breaksOnlyName.setText(mBreaksOnlyTitle.get(position));
      breaksOnlyHolder.breaksOnlyBreak.setText(convertTime(mBreaksOnlyList).get(position));

      breaksOnlyHolder.fullView.setOnClickListener(v -> {
        mOnCycleClickListener.onCycleClick(position);
      });

    } else if (holder instanceof PomHolder) {
      PomHolder pomHolder = (PomHolder) holder;
      pomHolder.pomName.setText(mPomTitle.get(position));

      String tempPom = mPomList.get(position);
      tempPom = tempPom.replace("-", mContext.getString(R.string.bullet));
      Spannable pomSpan = new SpannableString(tempPom);

      //Sets green/red alternating colors using text char indices.
      int moving = 0;
      for (int i=0; i<8; i++) {
        if (pomSpan.length()>=moving+2){
          if (i%2==0) pomSpan.setSpan(new ForegroundColorSpan(Color.GREEN), moving, moving+2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
          else pomSpan.setSpan(new ForegroundColorSpan(Color.RED), moving, moving+2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
          moving+=5;
        }
      }
      pomHolder.pomView.setText(pomSpan);

      pomHolder.fullView.setOnClickListener(v-> {
        mOnCycleClickListener.onCycleClick(position);
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

    @SuppressLint("ResourceAsColor")
    public CustomHolder(@NonNull View itemView) {
      super(itemView) ;
      customName = itemView.findViewById(R.id.custom_name_header);
      customSet = itemView.findViewById(R.id.saved_custom_set_view);
      customBreak = itemView.findViewById(R.id.saved_custom_break_view);
      fullView = itemView;
    }
  }

  public class BreaksOnlyHolder extends RecyclerView.ViewHolder {
    public TextView breaksOnlyName;
    public TextView breaksOnlyBreak;
    public View fullView;

    public BreaksOnlyHolder(@NonNull View itemView) {
      super(itemView);
      breaksOnlyName = itemView.findViewById(R.id.breaks_only_header);
      breaksOnlyBreak = itemView.findViewById(R.id.saved_breaks_only_view);
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
      }
      finalSplit = String.valueOf(newString);
      finalSplit = finalSplit.replace("[", "");
      finalSplit = finalSplit.replace("]", "");
      finalSplit = finalSplit.replace(",", " " + mContext.getString( R.string.bullet));
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
    } else if (totalSeconds != 5) return String.valueOf(totalSeconds);
    else return "05";
  }
}