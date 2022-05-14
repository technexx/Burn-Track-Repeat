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
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
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
    LongToStringConverters longToStringConverters = new LongToStringConverters();

    List<String> mActivities;
    List<Long> mSetTimes;
    List<Long> mBreakTimes;
    List<Double> mCaloriesBurned;
    int mItemCount;

    int REGULAR_TEXT = 0;
    int BOLD_TEXT = 1;

    boolean mEditModeIsActive;
    int EDITING_SETS = 0;
    int EDITING_BREAKS = 1;

    PopupWindow confirmPopUp;
    boolean deletionConfirmationIsVisible;

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

//        LayoutInflater inflater = (LayoutInflater) mContext.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.daily_stats_recycler_layout, parent, false);

//        View addTDEEPopUpView = inflater.inflate(R.layout.add_tdee_popup, null);
//        confirmPopUp = new PopupWindow(addTDEEPopUpView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true);

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
                mHolder.breakTimeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.teal_200));
                mHolder.setTimeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                mTdeeEditedItemIsSelected.tdeeEditItemSelected(EDITING_BREAKS, position-1);
            }
        });

        mHolder.breakTimeTextView.setVisibility(View.GONE);

        mHolder.setTimeTextView.setBackground(null);
        mHolder.breakTimeTextView.setBackground(null);
        mHolder.setTimeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        mHolder.breakTimeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        mHolder.addActivity.setVisibility(View.GONE);
        mHolder.deleteActivity.setVisibility(View.INVISIBLE);

        if (position==0) {
            setHolderViewTextStyles(BOLD_TEXT);
            setHolderTextViews(position);
        } else {
            if (position < mActivities.size()) {
                setHolderViewTextStyles(REGULAR_TEXT);
                setHolderTextViews(position);
            }
            setHolderEditModeViews(position);
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


    private void setHolderTextViews(int position) {
        if (position==0) {
            mHolder.activityTextView.setText(mContext.getString(R.string.activity_text_header));
            mHolder.setTimeTextView.setText(mContext.getString(R.string.set_time_text_header));
            mHolder.breakTimeTextView.setText(mContext.getString(R.string.break_time_text_header));
            mHolder.caloriesBurnedTextView.setText(mContext.getString(R.string.calories_burned_text_header));
        } else {
            mHolder.activityTextView.setText(mActivities.get(position-1));
            mHolder.setTimeTextView.setText(longToStringConverters.convertSecondsForStatDisplay(mSetTimes.get(position-1)));
            mHolder.breakTimeTextView.setText(longToStringConverters.convertSecondsForStatDisplay(mBreakTimes.get(position-1)));
            mHolder.caloriesBurnedTextView.setText(formatCalorieString(mCaloriesBurned.get(position-1)));
        }
    }

    private void setHolderEditModeViews(int position) {
        if (!mEditModeIsActive) {
            mHolder.addActivity.setVisibility(View.GONE);
        } else {
            if (position < mActivities.size()) {
                mHolder.setTimeTextView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.tdee_edit_border));
                mHolder.breakTimeTextView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.tdee_edit_border));
                mHolder.deleteActivity.setVisibility(View.VISIBLE);
            } else if (position < mItemCount){
                mHolder.addActivity.setVisibility(View.VISIBLE);

                mHolder.addActivity.setOnClickListener(v-> {
                    mTdeeActivityAddition.onAddingActivity(position);
                });

                mHolder.deleteActivity.setOnClickListener(v-> {
                    mTdeeActivityDeletion.onDeletingActivity(position);
                });
            }
        }
    }
    private void setHolderViewTextStyles(int textStyle) {
        if (textStyle==BOLD_TEXT) {
            mHolder.activityTextView.setTextSize(18);
            mHolder.activityTextView.setTypeface(Typeface.DEFAULT_BOLD);
            mHolder.setTimeTextView.setTypeface(Typeface.DEFAULT_BOLD);
            mHolder.breakTimeTextView.setTypeface(Typeface.DEFAULT_BOLD);
            mHolder.caloriesBurnedTextView.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            mHolder.activityTextView.setTypeface(Typeface.DEFAULT);
            mHolder.setTimeTextView.setTypeface(Typeface.DEFAULT);
            mHolder.breakTimeTextView.setTypeface(Typeface.DEFAULT);
            mHolder.caloriesBurnedTextView.setTypeface(Typeface.DEFAULT);
        }
    }

    public class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView activityTextView;
        TextView setTimeTextView;
        TextView breakTimeTextView;
        TextView caloriesBurnedTextView;

        ImageButton addActivity;
        ImageButton deleteActivity;

        View fullView;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            activityTextView = itemView.findViewById(R.id.activity_in_daily_stats_textView);
            setTimeTextView = itemView.findViewById(R.id.set_time_in_daily_stats_textView);
            breakTimeTextView = itemView.findViewById(R.id.break_time_in_daily_stats_textView);
            caloriesBurnedTextView = itemView.findViewById(R.id.calories_burned_in_daily_stats_textView);

            addActivity = itemView.findViewById(R.id.add_activity_in_edit_stats);
            deleteActivity = itemView.findViewById(R.id.delete_activity_in_edit_stats);

            fullView = itemView;
        }
    }

    private String formatCalorieString(double calories) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(calories);
    }
}