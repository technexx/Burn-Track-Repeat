package com.example.tragic.irate.simple.stopwatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

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
  public static final int SETS_AND_BREAKS = 0;
  public static final int BREAKS_ONLY = 1;
  public static final int POMODORO = 2;
  int mChosenView;

  public interface onCycleClickListener {
    void onCycleClick (int position);
  }

  public interface onDeleteCycleListener {
    void onCycleDelete (int position);
  }

  public void setItemClick(onCycleClickListener xOnCycleClickListener) {
    this.mOnCycleClickListener = xOnCycleClickListener;
  }

  public void setDeleteCycle(onDeleteCycleListener xOnDeleteCycleListener) {
    this.mOnDeleteCycleListener = xOnDeleteCycleListener;
  }

  public SavedCycleAdapter() {
  }

  public SavedCycleAdapter (Context context, ArrayList<String> setsList, ArrayList<String> breaksList, ArrayList<String> breaksOnlyList, ArrayList<String> title, ArrayList<String> breaksOnlyTitleArray, ArrayList<String> pomList, ArrayList<String> pomTitle) {
    this.mContext = context; mSetsList = setsList; mBreaksList = breaksList; mBreaksOnlyList = breaksOnlyList; this.mTitle = title; this.mBreaksOnlyTitle = breaksOnlyTitleArray; this.mPomList = pomList; this.mPomTitle = pomTitle;
  }

  public void setBreaksOnly(boolean breaksOnly){
    mBreaksOnly = breaksOnly;
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

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    if (holder instanceof CustomHolder) {
      CustomHolder customHolder = (CustomHolder) holder;
      customHolder.customName.setText(mTitle.get(position));
      customHolder.customSet.setText(convertTime(mSetsList).get(position));
      customHolder.customBreak.setText(convertTime(mBreaksList).get(position));

      customHolder.fullView.setOnClickListener(v -> {
        mOnCycleClickListener.onCycleClick(position);
      });
      customHolder.customTrash.setOnClickListener(v-> {
        mOnDeleteCycleListener.onCycleDelete(position);
      });

    } else if (holder instanceof BreaksOnlyHolder) {
      BreaksOnlyHolder breaksOnlyHolder = (BreaksOnlyHolder) holder;
      breaksOnlyHolder.breaksOnlyName.setText(mBreaksOnlyTitle.get(position));
      breaksOnlyHolder.breaksOnlyBreak.setText(convertTime(mBreaksOnlyList).get(position));

      breaksOnlyHolder.fullView.setOnClickListener(v -> {
        mOnCycleClickListener.onCycleClick(position);
      });
      breaksOnlyHolder.breaksOnlyTrash.setOnClickListener(v-> {
        mOnDeleteCycleListener.onCycleDelete(position);
      });

    } else if (holder instanceof PomHolder) {
      PomHolder pomHolder = (PomHolder) holder;
      pomHolder.pomName.setText(mPomTitle.get(position));

      String tempPom = mPomList.get(position);
      tempPom = tempPom.replace("-", mContext.getString(R.string.bullet));
      Spannable pomSpan = new SpannableString(tempPom);

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

      pomHolder.pomTrash.setOnClickListener(v-> {
        mOnDeleteCycleListener.onCycleDelete(position);
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
    public ImageButton customTrash;
    public View fullView;

    @SuppressLint("ResourceAsColor")
    public CustomHolder(@NonNull View itemView) {
      super(itemView) ;
      customName = itemView.findViewById(R.id.custom_name_header);
      customSet = itemView.findViewById(R.id.saved_custom_set_view);
      customBreak = itemView.findViewById(R.id.saved_custom_break_view);
      customTrash = itemView.findViewById(R.id.delete_cycle);
      fullView = itemView;
    }
  }

  public class BreaksOnlyHolder extends RecyclerView.ViewHolder {
    public TextView breaksOnlyName;
    public TextView breaksOnlyBreak;
    public ImageButton breaksOnlyTrash;
    public View fullView;

    public BreaksOnlyHolder(@NonNull View itemView) {
      super(itemView);
      breaksOnlyName = itemView.findViewById(R.id.breaks_only_header);
      breaksOnlyBreak = itemView.findViewById(R.id.saved_breaks_only_view);
      breaksOnlyTrash = itemView.findViewById(R.id.delete_cycle_bo);
      fullView = itemView;
    }
  }

  public class PomHolder extends RecyclerView.ViewHolder {
    public TextView pomName;
    public TextView pomView;
    public ImageButton pomTrash;
    public View fullView;

    public PomHolder(@NonNull View itemView) {
      super(itemView);
      pomName = itemView.findViewById(R.id.pom_header);
      pomView = itemView.findViewById(R.id.pom_view);
      pomTrash = itemView.findViewById(R.id.delete_pom_cycle);
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

    if (totalSeconds >=60) {
      minutes = totalSeconds/60;
      remainingSeconds = totalSeconds % 60;
      return (minutes + ":" + df.format(remainingSeconds));
    } else if (totalSeconds != 5) return String.valueOf(totalSeconds);
    else return "05";
  }
}