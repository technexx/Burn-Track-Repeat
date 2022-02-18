package com.example.tragic.irate.simple.stopwatch.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tragic.irate.simple.stopwatch.R;

import java.util.List;

public class DailyStatsAdapter extends RecyclerView.Adapter<DailyStatsAdapter.ActivityViewHolder> {
    Context mContext;
    List<String> mActivities;
    List<Long> mSetTimes;
    List<Long> mBreakTimes;
    List<Double> mCaloriesBurned;

    //Todo: Each recycler row represents one day, while the lists represent the activities and their respective values for that day (always 1:1). Therefore, these lists should contain ALL day and their values.
    public DailyStatsAdapter(Context context, List<String> activities, List<Long> setTimes, List<Long> breakTimes, List<Double> caloriesBurned) {
        this.mContext = context; this.mActivities = activities; this.mSetTimes = setTimes; this.mBreakTimes = breakTimes; this.mCaloriesBurned = caloriesBurned;
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.daily_stats_recycler_layout, parent, false);

        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mActivities.size();
    }

    public class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView activity;
        TextView setTime;
        TextView breakTime;
        TextView caloriesBurned;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            activity = itemView.findViewById(R.id.activity_in_daily_stats);
            setTime = itemView.findViewById(R.id.set_time_in_daily_stats);
            breakTime = itemView.findViewById(R.id.break_time_in_daily_stats);
            caloriesBurned = itemView.findViewById(R.id.calories_burned_in_daily_stats);
        }
    }
}