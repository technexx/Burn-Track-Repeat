package com.example.tragic.irate.simple.stopwatch.Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
    ActivityViewHolder mHolder;
    int mItemCount;

    List<String> mActivities;
    List<Long> mSetTimes;
    List<Long> mBreakTimes;
    List<Double> mCaloriesBurned;
    LongToStringConverters longToStringConverters = new LongToStringConverters();

    boolean mEditModeIsActive;
    int EDITING_SETS = 0;
    int EDITING_BREAKS = 1;

    tdeeEditedItemIsSelected mTdeeEditedItemIsSelected;
    tdeeActivityAddition mTdeeActivityAddition;
    tdeeActivityDeletion mTdeeActivityDeletion;

    public interface tdeeEditedItemIsSelected {
        void tdeeEditItemSelected (int typeOfEdit, int position);
    }

    public void getSelectedTdeeItemPosition(tdeeEditedItemIsSelected xTdeeEditedItemIsSelected) {
        this.mTdeeEditedItemIsSelected = xTdeeEditedItemIsSelected;
    }

    public interface tdeeActivityAddition {
        void onAddingActivity(int position);
    }

    public void addActivityToDailyStats(tdeeActivityAddition xTdeeActivityAddition) {
        this.mTdeeActivityAddition = xTdeeActivityAddition;
    }

    public interface tdeeActivityDeletion {
        void onDeletingActivity(int position);
    }

    public void deleteActivityFromDailyStats(tdeeActivityDeletion xTdeeActivityDeletion) {
        this.mTdeeActivityDeletion = xTdeeActivityDeletion;
    }

    public DailyStatsAdapter(Context context, List<String> activities, List<Long> setTimes, List<Long> breakTimes, List<Double> caloriesBurned) {
        this.mContext = context; this.mActivities = activities; this.mSetTimes = setTimes; this.mBreakTimes = breakTimes; this.mCaloriesBurned = caloriesBurned;
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.daily_stats_recycler_layout, parent, false);

        return mHolder = new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        ActivityViewHolder activityViewHolder = (ActivityViewHolder) holder;
        mHolder = activityViewHolder;

        mHolder.setTimeTextView.setOnClickListener(v-> {
            if (mEditModeIsActive && position>0) {
                mHolder.setTimeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.teal_200));
                mHolder.breakTimeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                mTdeeEditedItemIsSelected.tdeeEditItemSelected(EDITING_SETS, position-1);
            }
        });

        mHolder.breakTimeTextView.setOnClickListener(v-> {
            if (mEditModeIsActive && position>0) {
                mTdeeEditedItemIsSelected.tdeeEditItemSelected(EDITING_BREAKS, position-1);
                mHolder.breakTimeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.teal_200));
                mHolder.setTimeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            }
        });

        mHolder.setTimeTextView.setBackground(null);
        mHolder.breakTimeTextView.setBackground(null);
        mHolder.setTimeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        mHolder.breakTimeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        mHolder.addActivity.setVisibility(View.GONE);

        if (position==0) {
            mHolder.activityTextView.setText(mContext.getString(R.string.activity_text_header));
            mHolder.setTimeTextView.setText(mContext.getString(R.string.set_time_text_header));
            mHolder.breakTimeTextView.setText(mContext.getString(R.string.break_time_text_header));
            mHolder.caloriesBurnedTextView.setText(mContext.getString(R.string.calories_burned_text_header));

            mHolder.activityTextView.setTypeface(Typeface.DEFAULT_BOLD);
            mHolder.setTimeTextView.setTypeface(Typeface.DEFAULT_BOLD);
            mHolder.breakTimeTextView.setTypeface(Typeface.DEFAULT_BOLD);
            mHolder.caloriesBurnedTextView.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            if (position < mActivities.size()) {
                mHolder.activityTextView.setTypeface(Typeface.DEFAULT);
                mHolder.setTimeTextView.setTypeface(Typeface.DEFAULT);
                mHolder.breakTimeTextView.setTypeface(Typeface.DEFAULT);
                mHolder.caloriesBurnedTextView.setTypeface(Typeface.DEFAULT);

                mHolder.activityTextView.setText(mActivities.get(position-1));
                mHolder.setTimeTextView.setText(longToStringConverters.convertSecondsForStatDisplay(mSetTimes.get(position-1)));
                mHolder.breakTimeTextView.setText(longToStringConverters.convertSecondsForStatDisplay(mBreakTimes.get(position-1)));
                mHolder.caloriesBurnedTextView.setText(formatCalorieString(mCaloriesBurned.get(position-1)));
            }

            if (!mEditModeIsActive) {
                mHolder.addActivity.setVisibility(View.GONE);
            } else {
                if (position < mActivities.size()) {
                    mHolder.setTimeTextView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.tdee_edit_border));
                    mHolder.breakTimeTextView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.tdee_edit_border));
                } else if (position < mItemCount){
                    mHolder.addActivity.setVisibility(View.VISIBLE);
                    mHolder.addActivity.setOnClickListener(v-> {
                        mTdeeActivityAddition.onAddingActivity(position);
                    });
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mItemCount = mActivities.size()+1;
    }

    public void turnOffEditMode() {
        if (mEditModeIsActive) mEditModeIsActive = false;
    }

    public void toggleEditMode() {
        mEditModeIsActive = !mEditModeIsActive;
        notifyDataSetChanged();
    }

    public class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView activityTextView;
        TextView setTimeTextView;
        TextView breakTimeTextView;
        TextView caloriesBurnedTextView;
        ImageButton addActivity;

        View fullView;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            activityTextView = itemView.findViewById(R.id.activity_in_daily_stats_textView);
            setTimeTextView = itemView.findViewById(R.id.set_time_in_daily_stats_textView);
            breakTimeTextView = itemView.findViewById(R.id.break_time_in_daily_stats_textView);
            caloriesBurnedTextView = itemView.findViewById(R.id.calories_burned_in_daily_stats_textView);
            addActivity = itemView.findViewById(R.id.add_activity_in_edit_stats);

            fullView = itemView;
        }
    }

    private String formatCalorieString(double calories) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(calories);
    }
}