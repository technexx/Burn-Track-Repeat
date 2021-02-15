package com.example.tragic.irate.simple.stopwatch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LapAdapter extends RecyclerView.Adapter<LapAdapter.LapViewHolder> {

    Context mContext;
    List<String> mCurrentLap;
    List<String> mSavedLap;

    public LapAdapter(Context context, List<String> currentLap, List<String> savedLap) {
        mContext = context; mCurrentLap = currentLap; mSavedLap = savedLap;
    }

    @NonNull
    @Override
    public LapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.saved_laps, parent, false);

        LapViewHolder lapViewHolder = new LapViewHolder(view);
        return lapViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LapViewHolder holder, int position) {

    }

    //Todo: Size to match list.
    @Override
    public int getItemCount() {
        return 0;
    }

    public class LapViewHolder extends RecyclerView.ViewHolder {
        public TextView lapNumber;
        public TextView currentLap;
        public TextView savedLapTime;

        public LapViewHolder(@NonNull View itemView) {
            super(itemView);
            lapNumber = itemView.findViewById(R.id.lap_number);
            currentLap = itemView.findViewById(R.id.current_lap);
            savedLapTime = itemView.findViewById(R.id.total_lap_time);
        }
    }
}
