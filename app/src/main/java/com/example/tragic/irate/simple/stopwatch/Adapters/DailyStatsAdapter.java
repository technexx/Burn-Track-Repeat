package com.example.tragic.irate.simple.stopwatch.Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
    ActivityViewHolder mActivityViewHolder;
    List<String> mActivities;
    List<Long> mSetTimes;
    List<Long> mBreakTimes;
    List<Double> mCaloriesBurned;
    LongToStringConverters longToStringConverters = new LongToStringConverters();

    boolean mEditModeIsActive;
    int mPositionSelected;

    tdeeEditedItemIsSelected mTdeeEditedItemIsSelected;

    public interface tdeeEditedItemIsSelected {
        void tdeeEditItemSelected (int position);
    }

    public void getSelectedTdeeItemPosition(tdeeEditedItemIsSelected xTdeeEditedItemIsSelected) {
        this.mTdeeEditedItemIsSelected = xTdeeEditedItemIsSelected;
    }

    public DailyStatsAdapter(Context context, List<String> activities, List<Long> setTimes, List<Long> breakTimes, List<Double> caloriesBurned) {
        this.mContext = context; this.mActivities = activities; this.mSetTimes = setTimes; this.mBreakTimes = breakTimes; this.mCaloriesBurned = caloriesBurned;
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.daily_stats_recycler_layout, parent, false);

        return mActivityViewHolder = new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        mActivityViewHolder = holder;

        mActivityViewHolder.setTimeTextView.setOnClickListener(v-> {
            if (mEditModeIsActive) {
                if (position>0) {
                    toggleTextViewsAndEditTextForSetTime();
                }
            }
        });

        if (position==0) {
            mActivityViewHolder.activityTextView.setText(mContext.getString(R.string.activity_text_header));
            mActivityViewHolder.setTimeTextView.setText(mContext.getString(R.string.set_time_text_header));
            mActivityViewHolder.breakTimeTextView.setText(mContext.getString(R.string.break_time_text_header));
            mActivityViewHolder.caloriesBurnedTextView.setText(mContext.getString(R.string.calories_burned_text_header));

            mActivityViewHolder.activityTextView.setTypeface(Typeface.DEFAULT_BOLD);
            mActivityViewHolder.setTimeTextView.setTypeface(Typeface.DEFAULT_BOLD);
            mActivityViewHolder.breakTimeTextView.setTypeface(Typeface.DEFAULT_BOLD);
            mActivityViewHolder.caloriesBurnedTextView.setTypeface(Typeface.DEFAULT_BOLD);

            Log.i("testPos", "activity at position 0 is " + mActivityViewHolder.activityTextView.getText());
        } else {
            mActivityViewHolder.activityTextView.setText(mActivities.get(position-1));
            mActivityViewHolder.setTimeTextView.setText(longToStringConverters.convertSeconds(mSetTimes.get(position+-1)));
            mActivityViewHolder.breakTimeTextView.setText(longToStringConverters.convertSeconds(mBreakTimes.get(position-1)));
            mActivityViewHolder.caloriesBurnedTextView.setText(formatCalorieString(mCaloriesBurned.get(position-1)));

            mActivityViewHolder.activityTextView.setTypeface(Typeface.DEFAULT);
            mActivityViewHolder.setTimeTextView.setTypeface(Typeface.DEFAULT);
            mActivityViewHolder.breakTimeTextView.setTypeface(Typeface.DEFAULT);
            mActivityViewHolder.caloriesBurnedTextView.setTypeface(Typeface.DEFAULT);

            if (mEditModeIsActive) {
                mActivityViewHolder.fullView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.tdee_edit_border));
            } else {
                mActivityViewHolder.fullView.setBackground(null);
            }

            Log.i("testPos", "background is " + mActivityViewHolder.fullView.getBackground());
            Log.i("testPos", "activity at position " + position  + " is " + mActivityViewHolder.activityTextView.getText());
        }

        mActivityViewHolder.setTimeEditText.setVisibility(View.INVISIBLE);
        mActivityViewHolder.breakTimeEditText.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return mActivities.size()+1;
    }

    public void turnOffEditMode() {
        if (mEditModeIsActive) mEditModeIsActive = false;
    }

    public void toggleEditMode() {
        mEditModeIsActive = !mEditModeIsActive;
        notifyDataSetChanged();
    }

    public void toggleTextViewsAndEditTextForSetTime() {
        if (mActivityViewHolder.setTimeTextView.isShown()) {
            mActivityViewHolder.setTimeTextView.setVisibility(View.INVISIBLE);
            mActivityViewHolder.setTimeEditText.setVisibility(View.VISIBLE);
        }
        if (mActivityViewHolder.breakTimeEditText.isShown()) {
            mActivityViewHolder.breakTimeEditText.setVisibility(View.INVISIBLE);
        }
    }

    public class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView activityTextView;
        TextView setTimeTextView;
        TextView breakTimeTextView;
        TextView caloriesBurnedTextView;

        EditText setTimeEditText;
        EditText breakTimeEditText;

        View fullView;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            activityTextView = itemView.findViewById(R.id.activity_in_daily_stats_textView);
            setTimeTextView = itemView.findViewById(R.id.set_time_in_daily_stats_textView);
            breakTimeTextView = itemView.findViewById(R.id.break_time_in_daily_stats_textView);
            caloriesBurnedTextView = itemView.findViewById(R.id.calories_burned_in_daily_stats_textView);

            setTimeEditText = itemView.findViewById(R.id.set_time_in_daily_stats_editText);
            breakTimeEditText = itemView.findViewById(R.id.break_time_in_daily_stats_editText);

            fullView = itemView;
        }
    }

    private String formatCalorieString(double calories) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(calories);
    }
}