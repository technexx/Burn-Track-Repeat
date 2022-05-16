package com.example.tragic.irate.simple.stopwatch.Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
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

public class DailyStatsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    HeaderViewHolder mHeaderViewHolder;
    MainViewHolder mMainViewHolder;
    LongToStringConverters longToStringConverters = new LongToStringConverters();

    List<String> mActivities;
    List<Long> mSetTimes;
    List<Long> mBreakTimes;
    List<Double> mCaloriesBurned;
    int mItemCount;
    int mPosition;

    int HEADER_VIEW = 0;
    int MAIN_VIEW = 1;
    int FOOTER_VIEW = 2;

    int REGULAR_TEXT = 0;
    int BOLD_TEXT = 1;

    boolean mEditModeIsActive;
    int EDITING_SETS = 0;
    int EDITING_BREAKS = 1;

    PopupWindow confirmDeletionPopUpWindow;

    tdeeEditedItemIsSelected mTdeeEditedItemIsSelected;
    tdeeActivityAddition mTdeeActivityAddition;
    tdeeActivityDeletion mTdeeActivityDeletion;

    ImageButton confirmActivityDeletionButton;
    ImageButton cancelActivityDeletionButton;

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

        instantiateDeletePopUp();
    }

    public void instantiateDeletePopUp() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View deletePopUpView = inflater.inflate(R.layout.delete_tdee_popup, null);
        confirmDeletionPopUpWindow = new PopupWindow(deletePopUpView, 250, WindowManager.LayoutParams.WRAP_CONTENT, true);

        confirmActivityDeletionButton = deletePopUpView.findViewById(R.id.confirm_activity_delete);
        cancelActivityDeletionButton = deletePopUpView.findViewById(R.id.cancel_activity_delete);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view;

        if (viewType==HEADER_VIEW) {
            view = inflater.inflate(R.layout.daily_stats_recycler_header_layout, parent, false);
            return new HeaderViewHolder(view);
        } else if (viewType==FOOTER_VIEW) {
            view = inflater.inflate(R.layout.daily_stats_recycler_footer_layout, parent, false);
            return new FootViewHolder(view);
        }

        view = inflater.inflate(R.layout.daily_stats_recycler_layout, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            mHeaderViewHolder = (HeaderViewHolder) holder;

            populateHeaderRowViews();
            setHolderViewTextStyles(BOLD_TEXT);
        } else if (holder instanceof MainViewHolder) {
            mMainViewHolder = (MainViewHolder) holder;

            populateMainRowViews(position);
            setDefaultMainHolderViewsAndBackgrounds();
            setMainHolderEditModeViews();
            setHolderViewTextStyles(REGULAR_TEXT);

            //mMainViewHolder instance set on onClick is set on first adapter population. It does NOT get refreshed with views in adapter, which is why we need new instances if we're going to use it as a global.
            mMainViewHolder.setTimeTextView.setOnClickListener(v-> {
                mMainViewHolder = (MainViewHolder) holder;

                if (mEditModeIsActive) {
                    mMainViewHolder.setTimeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.teal_200));
                    mMainViewHolder.breakTimeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                    mTdeeEditedItemIsSelected.tdeeEditItemSelected(EDITING_SETS, position-1);
                }
            });

            mMainViewHolder.breakTimeTextView.setOnClickListener(v-> {
                mMainViewHolder = (MainViewHolder) holder;

                if (mEditModeIsActive) {
                    mMainViewHolder.breakTimeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.teal_200));
                    mMainViewHolder.setTimeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                    mTdeeEditedItemIsSelected.tdeeEditItemSelected(EDITING_BREAKS, position-1);
                }
            });

            mMainViewHolder.deleteActivity.setOnClickListener(v-> {
                mMainViewHolder = (MainViewHolder) holder;

                confirmDeletionPopUpWindow.showAsDropDown(mMainViewHolder.endConstraint, 0, 0, Gravity.CENTER);
            });

            confirmActivityDeletionButton.setOnClickListener(v-> {
                mTdeeActivityDeletion.onDeletingActivity(position-1);
                confirmDeletionPopUpWindow.dismiss();
            });

            cancelActivityDeletionButton.setOnClickListener(v-> {
                confirmDeletionPopUpWindow.dismiss();
            });
        } else if (holder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) holder;

            footViewHolder.addActivity.setOnClickListener(v-> {
                mTdeeActivityAddition.onAddingActivity(position);
            });
        }
    }

    @Override
    public int getItemCount() {
        if (!mEditModeIsActive) {
            return mItemCount = mActivities.size()+1;
        } else {
            return mItemCount = mActivities.size()+2;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position==0) {
            return HEADER_VIEW;
            //<= because header takes up first position and doesn't pull from list.
        } else if (position <= mActivities.size() || !mEditModeIsActive){
            return MAIN_VIEW;
        } else {
            return FOOTER_VIEW;
        }
    }



    public void turnOffEditMode() {
        if (mEditModeIsActive) mEditModeIsActive = false;
    }

    public void toggleEditMode() {
        mEditModeIsActive = !mEditModeIsActive;
        notifyDataSetChanged();
    }

    private void populateHeaderRowViews() {
        mHeaderViewHolder.activityHeaderTextView.setText(R.string.activity_text_header);
        mHeaderViewHolder.setTimeHeaderTextView.setText(R.string.set_time_text_header);
        mHeaderViewHolder.breakTimeHeaderTextView.setText(R.string.break_time_text_header);
        mHeaderViewHolder.caloriesBurnedHeaderTextView.setText(R.string.calories_burned_text_header);
    }

    private void populateMainRowViews(int position) {
        //Returns on last row in edit mode so we don't try to pull textViews from footer.
        if (position==mItemCount-1 && mEditModeIsActive) {
            return;
        }
        mMainViewHolder.activityTextView.setText(mActivities.get(position-1));
        mMainViewHolder.setTimeTextView.setText(longToStringConverters.convertSecondsForStatDisplay(mSetTimes.get(position-1)));
        mMainViewHolder.breakTimeTextView.setText(longToStringConverters.convertSecondsForStatDisplay(mBreakTimes.get(position-1)));
        mMainViewHolder.caloriesBurnedTextView.setText(formatCalorieString(mCaloriesBurned.get(position-1)));
    }

    private void setDefaultMainHolderViewsAndBackgrounds() {
        mMainViewHolder.breakTimeTextView.setVisibility(View.GONE);
        mMainViewHolder.setTimeTextView.setBackground(null);
        mMainViewHolder.breakTimeTextView.setBackground(null);
        mMainViewHolder.setTimeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        mMainViewHolder.breakTimeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.white));

        mMainViewHolder.deleteActivity.setVisibility(View.INVISIBLE);
        mMainViewHolder.deleteActivityConfirm.setVisibility(View.GONE);
        mMainViewHolder.deleteActivityCancel.setVisibility(View.GONE);
    }

    private void setMainHolderEditModeViews() {
        if (mEditModeIsActive) {
            mMainViewHolder.setTimeTextView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.tdee_edit_border));
            mMainViewHolder.breakTimeTextView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.tdee_edit_border));
            mMainViewHolder.deleteActivity.setVisibility(View.VISIBLE);
        }
    }
    private void setHolderViewTextStyles(int textStyle) {
        if (textStyle==BOLD_TEXT) {
            mHeaderViewHolder.activityHeaderTextView.setTextSize(18);
            mHeaderViewHolder.activityHeaderTextView.setTypeface(Typeface.DEFAULT_BOLD);
            mHeaderViewHolder.setTimeHeaderTextView.setTypeface(Typeface.DEFAULT_BOLD);
            mHeaderViewHolder.breakTimeHeaderTextView.setTypeface(Typeface.DEFAULT_BOLD);
            mHeaderViewHolder.caloriesBurnedHeaderTextView.setTypeface(Typeface.DEFAULT_BOLD);

            mHeaderViewHolder.breakTimeHeaderTextView.setVisibility(View.INVISIBLE);
        } else if (textStyle==REGULAR_TEXT){
            mMainViewHolder.activityTextView.setTypeface(Typeface.DEFAULT);
            mMainViewHolder.setTimeTextView.setTypeface(Typeface.DEFAULT);
            mMainViewHolder.breakTimeTextView.setTypeface(Typeface.DEFAULT);
            mMainViewHolder.caloriesBurnedTextView.setTypeface(Typeface.DEFAULT);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView activityHeaderTextView;
        TextView setTimeHeaderTextView;;
        TextView breakTimeHeaderTextView;
        TextView caloriesBurnedHeaderTextView;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            activityHeaderTextView =  itemView.findViewById(R.id.activity_in_daily_stats_header);
            setTimeHeaderTextView =  itemView.findViewById(R.id.set_time_in_daily_stats_header);
            breakTimeHeaderTextView =  itemView.findViewById(R.id.break_time_in_daily_stats_header);
            caloriesBurnedHeaderTextView =  itemView.findViewById(R.id.calories_burned_in_daily_stats_header);
        }
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {
        TextView activityTextView;
        TextView setTimeTextView;
        TextView breakTimeTextView;
        TextView caloriesBurnedTextView;

        ImageButton deleteActivity;
        ImageButton deleteActivityConfirm;
        ImageButton deleteActivityCancel;

        View fullView;
        View endConstraint;

        public MainViewHolder (@NonNull View itemView) {
            super(itemView);
            activityTextView = itemView.findViewById(R.id.activity_in_daily_stats_textView);
            setTimeTextView = itemView.findViewById(R.id.set_time_in_daily_stats_textView);
            breakTimeTextView = itemView.findViewById(R.id.break_time_in_daily_stats_textView);
            caloriesBurnedTextView = itemView.findViewById(R.id.calories_burned_in_daily_stats_textView);

            deleteActivity = itemView.findViewById(R.id.delete_activity_in_edit_stats);
            deleteActivityConfirm = itemView.findViewById(R.id.confirm_activity_delete);
            deleteActivityCancel = itemView.findViewById(R.id.cancel_activity_delete);

            fullView = itemView;
            endConstraint = itemView.findViewById(R.id.end_constraint);
        }
    }

    public class FootViewHolder extends RecyclerView.ViewHolder {
        ImageButton addActivity;

        public FootViewHolder(@NonNull View itemView) {
            super(itemView);
            addActivity = itemView.findViewById(R.id.add_activity_in_edit_stats);
        }
    }

    private String formatCalorieString(double calories) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(calories);
    }
}