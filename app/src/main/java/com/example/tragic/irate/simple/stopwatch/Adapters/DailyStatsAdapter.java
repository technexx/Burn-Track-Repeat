package com.example.tragic.irate.simple.stopwatch.Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tragic.irate.simple.stopwatch.Database.DayStatClasses.StatsForEachActivity;
import com.example.tragic.irate.simple.stopwatch.Miscellaneous.LongToStringConverters;
import com.example.tragic.irate.simple.stopwatch.R;

import java.text.DecimalFormat;
import java.util.List;

public class DailyStatsAdapter extends RecyclerView.Adapter<DailyStatsAdapter.ActivityViewHolder> {
    Context mContext;
    List<String> mActivities;
    List<Long> mSetTimes;
    List<Long> mBreakTimes;
    List<Double> mCaloriesBurned;
    LongToStringConverters longToStringConverters = new LongToStringConverters();

    boolean mEditModeIsActive;
    int mPositionSelected;

    tdeeRowIsSelected mTdeeRowIsSelected;

    public interface tdeeRowIsSelected {
        void tdeeRowSelection (int position);
    }

    public void getSelectedTdeeRowPosition(tdeeRowIsSelected xTdeeRowIsSelected) {
        this.mTdeeRowIsSelected = xTdeeRowIsSelected;
    }

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
        ActivityViewHolder activityViewHolder = (ActivityViewHolder) holder;

        activityViewHolder.fullView.setOnClickListener(v-> {
            //Start at 1 because of header, and return 0 for it because that's our first list position.
            if (position>0) {
                mTdeeRowIsSelected.tdeeRowSelection(position-1);
            }
        });

        if (position==0) {
            activityViewHolder.activity.setText(mContext.getString(R.string.activity_text_header));
            activityViewHolder.setTime.setText(mContext.getString(R.string.set_time_text_header));
            activityViewHolder.breakTime.setText(mContext.getString(R.string.break_time_text_header));
            activityViewHolder.caloriesBurned.setText(mContext.getString(R.string.calories_burned_text_header));

            activityViewHolder.activity.setTypeface(Typeface.DEFAULT_BOLD);
            activityViewHolder.setTime.setTypeface(Typeface.DEFAULT_BOLD);
            activityViewHolder.breakTime.setTypeface(Typeface.DEFAULT_BOLD);
            activityViewHolder.caloriesBurned.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            activityViewHolder.activity.setText(mActivities.get(position-1));
            activityViewHolder.setTime.setText(longToStringConverters.convertSeconds(mSetTimes.get(position+-1)));
            activityViewHolder.breakTime.setText(longToStringConverters.convertSeconds(mBreakTimes.get(position-1)));
            activityViewHolder.caloriesBurned.setText(formatCalorieString(mCaloriesBurned.get(position-1)));

            activityViewHolder.activity.setTypeface(Typeface.DEFAULT);
            activityViewHolder.setTime.setTypeface(Typeface.DEFAULT);
            activityViewHolder.breakTime.setTypeface(Typeface.DEFAULT);
            activityViewHolder.caloriesBurned.setTypeface(Typeface.DEFAULT);

            if (mEditModeIsActive) {
                activityViewHolder.fullView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.tdee_edit_border));
            } else {
                activityViewHolder.fullView.setBackground(null);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mActivities.size()+1;
    }

    public void toggleEditMode() {
        mEditModeIsActive = !mEditModeIsActive;
        notifyDataSetChanged();
    }

    public class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView activity;
        TextView setTime;
        TextView breakTime;
        TextView caloriesBurned;
        View fullView;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            activity = itemView.findViewById(R.id.activity_in_daily_stats);
            setTime = itemView.findViewById(R.id.set_time_in_daily_stats);
            breakTime = itemView.findViewById(R.id.break_time_in_daily_stats);
            caloriesBurned = itemView.findViewById(R.id.calories_burned_in_daily_stats);
            fullView = itemView;
        }
    }

    private String formatCalorieString(double calories) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(calories);
    }
}