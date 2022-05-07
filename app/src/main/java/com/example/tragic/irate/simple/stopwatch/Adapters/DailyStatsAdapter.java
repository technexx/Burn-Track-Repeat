package com.example.tragic.irate.simple.stopwatch.Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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
    ActivityViewHolder holder;
    List<String> mActivities;
    List<Long> mSetTimes;
    List<Long> mBreakTimes;
    List<Double> mCaloriesBurned;
    LongToStringConverters longToStringConverters = new LongToStringConverters();

    boolean mEditModeIsActive;
    int mPositionSelectedToEdit;

    tdeeEditedItemIsSelected mTdeeEditedItemIsSelected;

    int EDITING_SETS = 0;
    int EDITING_BREAKS = 1;

    public interface tdeeEditedItemIsSelected {
        void tdeeEditItemSelected (int typeOfEdit, int position);
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

        return holder = new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        ActivityViewHolder activityViewHolder = (ActivityViewHolder) holder;

        activityViewHolder.setTimeTextView.setOnClickListener(v-> {
            mTdeeEditedItemIsSelected.tdeeEditItemSelected(EDITING_SETS, position);
        });

        activityViewHolder.breakTimeTextView.setOnClickListener(v-> {
            mTdeeEditedItemIsSelected.tdeeEditItemSelected(EDITING_BREAKS, position);
        });

        if (position==0) {
            activityViewHolder.activityTextView.setText(mContext.getString(R.string.activity_text_header));
            activityViewHolder.setTimeTextView.setText(mContext.getString(R.string.set_time_text_header));
            activityViewHolder.breakTimeTextView.setText(mContext.getString(R.string.break_time_text_header));
            activityViewHolder.caloriesBurnedTextView.setText(mContext.getString(R.string.calories_burned_text_header));

            activityViewHolder.activityTextView.setTypeface(Typeface.DEFAULT_BOLD);
            activityViewHolder.setTimeTextView.setTypeface(Typeface.DEFAULT_BOLD);
            activityViewHolder.breakTimeTextView.setTypeface(Typeface.DEFAULT_BOLD);
            activityViewHolder.caloriesBurnedTextView.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            activityViewHolder.activityTextView.setText(mActivities.get(position-1));
            activityViewHolder.setTimeTextView.setText(longToStringConverters.convertSeconds(mSetTimes.get(position+-1)));
            activityViewHolder.breakTimeTextView.setText(longToStringConverters.convertSeconds(mBreakTimes.get(position-1)));
            activityViewHolder.caloriesBurnedTextView.setText(formatCalorieString(mCaloriesBurned.get(position-1)));

            activityViewHolder.activityTextView.setTypeface(Typeface.DEFAULT);
            activityViewHolder.setTimeTextView.setTypeface(Typeface.DEFAULT);
            activityViewHolder.breakTimeTextView.setTypeface(Typeface.DEFAULT);
            activityViewHolder.caloriesBurnedTextView.setTypeface(Typeface.DEFAULT);
        }

        if (mEditModeIsActive) {
            activityViewHolder.fullView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.tdee_edit_border));
        } else {
            activityViewHolder.fullView.setBackground(null);
        }
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

    private void toggleTextAndEditViewsOnClick(int position) {
        if (mEditModeIsActive) {
            if (position>0) {
                mPositionSelectedToEdit = position;
                notifyDataSetChanged();
            }
        }
    }

    public class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView activityTextView;
        TextView setTimeTextView;
        TextView breakTimeTextView;
        TextView caloriesBurnedTextView;

        View fullView;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            activityTextView = itemView.findViewById(R.id.activity_in_daily_stats_textView);
            setTimeTextView = itemView.findViewById(R.id.set_time_in_daily_stats_textView);
            breakTimeTextView = itemView.findViewById(R.id.break_time_in_daily_stats_textView);
            caloriesBurnedTextView = itemView.findViewById(R.id.calories_burned_in_daily_stats_textView);

            fullView = itemView;
        }
    }

    private String formatCalorieString(double calories) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(calories);
    }
}