package com.example.tragic.irate.simple.stopwatch.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

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
    List<Double> mCaloriesBurned;
    int mItemCount;

    int HEADER_VIEW = 0;
    int MAIN_VIEW = 1;
    int FOOTER_VIEW = 2;

    int REGULAR_TEXT = 0;
    int BOLD_TEXT = 1;

    boolean mEditModeIsActive;
    boolean mRowIsSelectedForEditing;

    int mAddingOrEditingActivity;
    int ADDING_ACTIVITY = 0;
    int EDITING_ACTIVITY = 1;

    tdeeEditedItemIsSelected mTdeeEditedItemIsSelected;
    tdeeActivityAddition mTdeeActivityAddition;

    Animation slideInFromLeft;
    Animation slideOutFromLeft;

    boolean animateButtonSliding;

    public interface tdeeEditedItemIsSelected {
        void activityEditItemSelected (int position);
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

    public DailyStatsAdapter(Context context, List<String> activities, List<Long> setTimes, List<Double> caloriesBurned) {
        this.mContext = context; this.mActivities = activities; this.mSetTimes = setTimes; this.mCaloriesBurned = caloriesBurned;
        setAnimations();
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
            mMainViewHolder.fullView.setOnClickListener(v-> {
                mMainViewHolder = (MainViewHolder) holder;

                if (mEditModeIsActive) {
                    setAddingOrEditingActivityVariable(EDITING_ACTIVITY);
                    mTdeeEditedItemIsSelected.activityEditItemSelected(position-1);
                    toggleRowSelectionForEditing();
                }
            });

        } else if (holder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) holder;

            footViewHolder.addActivity.startAnimation(slideInFromLeft);

            footViewHolder.addActivity.setOnClickListener(v-> {
                setAddingOrEditingActivityVariable(ADDING_ACTIVITY);
                mTdeeActivityAddition.onAddingActivity(position);
            });
        }
    }

    private void setAddingOrEditingActivityVariable(int addingOrEditing) {
        this.mAddingOrEditingActivity = addingOrEditing;
    }

    public int getAddingOrEditingActivityVariable() {
        return mAddingOrEditingActivity;
    }

    private void toggleRowSelectionForEditing() {
        mRowIsSelectedForEditing = !mRowIsSelectedForEditing;
    }

    private void setDefaultMainHolderViewsAndBackgrounds() {
        mMainViewHolder.fullView.setBackground(null);
    }

    private void setMainHolderEditModeViews() {
        if (mEditModeIsActive) {
            mMainViewHolder.fullView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.stat_edit_row_border));
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
        mEditModeIsActive = false;
        animateButtonSliding = false;
    }

    public void toggleEditMode() {
        mEditModeIsActive = !mEditModeIsActive;
        animateButtonSliding= true;
        notifyDataSetChanged();
    }

    private void populateHeaderRowViews() {
        mHeaderViewHolder.activityHeaderTextView.setText(R.string.activity_text_header);
        mHeaderViewHolder.setTimeHeaderTextView.setText(R.string.set_time_text_header);
        mHeaderViewHolder.caloriesBurnedHeaderTextView.setText(R.string.calories_burned_text_header);
    }

    private void populateMainRowViews(int position) {
        //Returns on last row in edit mode so we don't try to pull textViews from footer.
        if (position==mItemCount-1 && mEditModeIsActive) {
            return;
        }
        mMainViewHolder.activityTextView.setText(mActivities.get(position-1));
        mMainViewHolder.setTimeTextView.setText(longToStringConverters.convertSecondsForStatDisplay(mSetTimes.get(position-1)));
        mMainViewHolder.caloriesBurnedTextView.setText(formatCalorieString(mCaloriesBurned.get(position-1)));
    }

    private void setHolderViewTextStyles(int textStyle) {
        if (textStyle==BOLD_TEXT) {
            mHeaderViewHolder.activityHeaderTextView.setTypeface(Typeface.DEFAULT_BOLD);
            mHeaderViewHolder.setTimeHeaderTextView.setTypeface(Typeface.DEFAULT_BOLD);
            mHeaderViewHolder.caloriesBurnedHeaderTextView.setTypeface(Typeface.DEFAULT_BOLD);

        } else if (textStyle==REGULAR_TEXT){
            mMainViewHolder.activityTextView.setTypeface(Typeface.DEFAULT);
            mMainViewHolder.setTimeTextView.setTypeface(Typeface.DEFAULT);
            mMainViewHolder.caloriesBurnedTextView.setTypeface(Typeface.DEFAULT);
        }
    }

    private void setAnimations() {
        slideInFromLeft = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_from_left);
        slideInFromLeft.setDuration(200);
        slideInFromLeft.setFillAfter(true);

        slideOutFromLeft = AnimationUtils.loadAnimation(mContext, R.anim.slide_out_from_left);
        slideOutFromLeft.setDuration(200);
        slideOutFromLeft.setFillAfter(true);
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView activityHeaderTextView;
        TextView setTimeHeaderTextView;;
        TextView caloriesBurnedHeaderTextView;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            activityHeaderTextView =  itemView.findViewById(R.id.activity_in_daily_stats_header);
            setTimeHeaderTextView =  itemView.findViewById(R.id.set_time_in_daily_stats_header);
            caloriesBurnedHeaderTextView =  itemView.findViewById(R.id.calories_burned_in_daily_stats_header);
        }
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {
        TextView activityTextView;
        TextView setTimeTextView;
        TextView caloriesBurnedTextView;

        View fullView;

        public MainViewHolder (@NonNull View itemView) {
            super(itemView);
            activityTextView = itemView.findViewById(R.id.activity_in_daily_stats_textView);
            setTimeTextView = itemView.findViewById(R.id.set_time_in_daily_stats_textView);
            caloriesBurnedTextView = itemView.findViewById(R.id.calories_burned_in_daily_stats_textView);

            fullView = itemView;
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
        DecimalFormat df = new DecimalFormat("#");
        return df.format(calories);
    }
}