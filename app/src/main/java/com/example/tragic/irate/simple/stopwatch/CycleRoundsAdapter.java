package com.example.tragic.irate.simple.stopwatch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CycleRoundsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  Context mContext;
  ArrayList<String> mSetsList;
  ArrayList<String> mBreaksList;
  ArrayList<String> mBreaksOnlyList;
  public static final int MODE_ONE = 1;
  public static final int MODE_TWO = 2;
  public static final int MODE_THREE = 3;
  int mMode;
  public boolean mSetsUp;
  public boolean mBreaksUp;

  public CycleRoundsAdapter(ArrayList<String> setsList, ArrayList<String> breaksList, ArrayList<String> breaksOnlyList) {
    this.mSetsList = setsList; this.mBreaksList = breaksList; this.mBreaksOnlyList = breaksOnlyList;
  }

  public void setMode(int mode) {
    mMode = mode;
  }

  public void countingUpSets(boolean setsUp) {
    mSetsUp = setsUp;
  }

  public void countingUpBreaks(boolean breaksUp) {
    mBreaksUp = breaksUp;
  }

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    if (viewType == MODE_ONE) {
      View view = LayoutInflater.from(context).inflate(R.layout.mode_one_rounds, parent, false);
      return new ModeOneRounds(view);
    } else if (viewType == MODE_TWO){
      View view = LayoutInflater.from(context).inflate(R.layout.mode_two_rounds, parent, false);
      return new ModeTwoRounds(view);
    } else if (viewType == MODE_THREE) {
      View view = LayoutInflater.from(context).inflate(R.layout.mode_three_rounds, parent, false);
      return new ModeThreeRounds(view);
    } else return null;
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    if (holder instanceof ModeOneRounds) {
      ModeOneRounds modeOneRounds = (ModeOneRounds) holder;
      if (!mSetsUp) {
        modeOneRounds.round_sets.setText(mSetsList.get(position));
        modeOneRounds.round_sets.setBackgroundResource(0);
      } else {
        modeOneRounds.round_sets.setText("");
        modeOneRounds.round_sets.setBackgroundResource(R.drawable.infinity_icon_small);
      }

      modeOneRounds.round_breaks.setText(mBreaksList.get(position));
    } else if (holder instanceof ModeTwoRounds) {
      ModeTwoRounds modeTwoRounds = (ModeTwoRounds) holder;
      modeTwoRounds.round_breaksOnly.setText(mBreaksOnlyList.get(position));
    }
  }

  @Override
  public int getItemCount() {
    switch (mMode) {
      case 1:
        return mSetsList.size();
      case 2:
        return mBreaksOnlyList.size();
      case 3: return 1;
      default: return 0;
    }
  }

  @Override
  public int getItemViewType(int position) {
    switch (mMode) {
      case 1: return MODE_ONE;
      case 2: return MODE_TWO;
      case 3: return MODE_THREE;
      default: return 0;
    }
  }

  public class ModeOneRounds extends RecyclerView.ViewHolder {
    public TextView round_sets;
    public TextView round_breaks;

    public ModeOneRounds(@NonNull View itemView) {
      super(itemView);
      round_sets = itemView.findViewById(R.id.round_sets);
      round_breaks = itemView.findViewById(R.id.round_breaks);
    }
  }

  public class ModeTwoRounds extends RecyclerView.ViewHolder {
    public TextView round_breaksOnly;

    public ModeTwoRounds(@NonNull View itemView) {
      super(itemView);
      round_breaksOnly = itemView.findViewById(R.id.round_breaksOnly);
    }
  }

  public class ModeThreeRounds extends RecyclerView.ViewHolder {
    public TextView round_pomodoro;

    public ModeThreeRounds(@NonNull View itemView) {
      super(itemView);
      round_pomodoro = itemView.findViewById(R.id.round_pomodoro);
    }
  }
}