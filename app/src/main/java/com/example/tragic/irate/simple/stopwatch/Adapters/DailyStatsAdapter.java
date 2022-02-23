package com.example.tragic.irate.simple.stopwatch.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tragic.irate.simple.stopwatch.Database.DayStatClasses.StatsForEachActivity;
import com.example.tragic.irate.simple.stopwatch.R;

import java.util.List;

public class DailyStatsAdapter extends RecyclerView.Adapter<DailyStatsAdapter.ActivityViewHolder> {
    Context mContext;
    List<String> mActivities;
    List<Long> mSetTimes;
    List<Long> mBreakTimes;
    List<Double> mCaloriesBurned;

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

    //Todo: Grid view? Remember, if we separate with hardcoded View lines outside recyclerView they won't adjust with size of number values. Even headers will have trouble. We should prolly go back to having it be part of the recyclerView.
    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        ActivityViewHolder activityViewHolder = (ActivityViewHolder) holder;

        if (position==0) {
            activityViewHolder.activity.setText(mContext.getString(R.string.activity_text_header));
            activityViewHolder.setTime.setText(mContext.getString(R.string.set_time_text_header));
            activityViewHolder.breakTime.setText(mContext.getString(R.string.break_time_text_header));
            activityViewHolder.caloriesBurned.setText(mContext.getString(R.string.calories_burned_text_header));
        } else {
            activityViewHolder.activity.setText(mActivities.get(position-1));
            activityViewHolder.setTime.setText(String.valueOf(mSetTimes.get(position+-1)));
            activityViewHolder.breakTime.setText(String.valueOf(mBreakTimes.get(position-1)));
            activityViewHolder.caloriesBurned.setText(String.valueOf(mCaloriesBurned.get(position-1)));
        }
    }

    @Override
    public int getItemCount() {
        return mActivities.size()+1;
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