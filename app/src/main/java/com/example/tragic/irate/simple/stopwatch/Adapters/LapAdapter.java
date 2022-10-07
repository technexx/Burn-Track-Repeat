package com.example.tragic.irate.simple.stopwatch.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tragic.irate.simple.stopwatch.R;

import java.util.ArrayList;
import java.util.List;

public class LapAdapter extends RecyclerView.Adapter<LapAdapter.LapViewHolder> {
  Context mContext;
  List<String> mCurrentLap;
  List<String> mSavedLap;
  Animation anim;

  int mOnScreenItemCount;
  boolean mHaveWeBegunScrolling;

  public LapAdapter(Context context, List<String> currentLap, List<String> savedLap) {
    mContext = context; mCurrentLap = currentLap; mSavedLap = savedLap;
    anim = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_from_top_for_laps);
    anim.setDuration(500);
  }

  @NonNull
  @Override
  public LapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    LayoutInflater inflater = LayoutInflater.from(context);
    View view = inflater.inflate(R.layout.lap_recycler_layout, parent, false);

    return new LapViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull LapViewHolder holder, int position) {
    holder.currentLap.setText(mCurrentLap.get(position));
    holder.savedLapTime.setText(mSavedLap.get(position));

    if (position<9) {
      holder.lapNumber.setText("# 0" + (position+1));
    } else {
      holder.lapNumber.setText("# " + (position+1));
    }

    if (!mHaveWeBegunScrolling) {
      holder.fullView.startAnimation(anim);
    }
  }

  public void setHaveWeBegunScrolling(boolean haveWeBegun) {
    this.mHaveWeBegunScrolling = haveWeBegun;
  }

  public boolean getHaveWeBegunScrolling() {
    return mHaveWeBegunScrolling;
  }

  @Override
  public int getItemCount() {
    return mSavedLap.size();
  }

  public class LapViewHolder extends RecyclerView.ViewHolder {
    public TextView lapNumber;
    public TextView currentLap;
    public TextView savedLapTime;
    public View fullView;

    public LapViewHolder(@NonNull View itemView) {
      super(itemView);
      lapNumber = itemView.findViewById(R.id.lap_number);
      currentLap = itemView.findViewById(R.id.current_lap);
      savedLapTime = itemView.findViewById(R.id.saved_laps);
      fullView = itemView;
    }
  }
}