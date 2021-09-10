package com.example.tragic.irate.simple.stopwatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LapAdapter extends RecyclerView.Adapter<LapAdapter.LapViewHolder> {
  Context mContext;
  List<String> mCurrentLap;
  List<String> mSavedLap;
  int mFirstPos;
  int mLastPos;
  onPositionCallback mOnPositionCallback;

  public LapAdapter(Context context, List<String> currentLap, List<String> savedLap) {
    mContext = context; mCurrentLap = currentLap; mSavedLap = savedLap;
  }

  public interface onPositionCallback {
    void positionCallback(int position);
  }

  public void passPosition (onPositionCallback xOnPositionCallback) {
    this.mOnPositionCallback = xOnPositionCallback;
  }

  //Receives visible positions in its layout.
  public void receiveVisiblePositions(int first, int last) {
    this.mFirstPos = first; this.mLastPos = last;
  }

  //Todo: Drawable gradient animation onScroll?
  @NonNull
  @Override
  public LapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    LayoutInflater inflater = LayoutInflater.from(context);
    View view = inflater.inflate(R.layout.saved_laps, parent, false);

    LapViewHolder lapViewHolder = new LapViewHolder(view);
    return lapViewHolder;
  }

  //This gets called when scrolling w/ out any listener attached.
  @Override
  public void onBindViewHolder(@NonNull LapViewHolder holder, int position) {
    holder.currentLap.setText(mCurrentLap.get(position));
    holder.savedLapTime.setText(mSavedLap.get(position));
    holder.lapNumber.setText("# " + (position+1));
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