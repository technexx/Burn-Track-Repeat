package com.example.tragic.irate.simple.stopwatch.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
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

    PopupWindow confirmDeletionPopUpWindow;

    tdeeEditedItemIsSelected mTdeeEditedItemIsSelected;
    tdeeActivityAddition mTdeeActivityAddition;
    tdeeActivityDeletion mTdeeActivityDeletion;

    ImageButton confirmActivityDeletionButton;
    ImageButton cancelActivityDeletionButton;

    Animation slideInFromLeft;
    Animation slideOutFromLeft;
    Animation slideInFromRight;
    Animation slideOutFromRight;
    Animation fadeIn;
    Animation fadeOut;

    boolean animateButtonSliding;

    public interface tdeeEditedItemIsSelected {
        void tdeeEditItemSelected (int position);
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

    public DailyStatsAdapter(Context context, List<String> activities, List<Long> setTimes, List<Double> caloriesBurned) {
        this.mContext = context; this.mActivities = activities; this.mSetTimes = setTimes; this.mCaloriesBurned = caloriesBurned;

        instantiateDeletePopUp();
        setAnimations();
    }

    public void instantiateDeletePopUp() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View deletePopUpView = inflater.inflate(R.layout.delete_tdee_popup, null);
        confirmDeletionPopUpWindow = new PopupWindow(deletePopUpView, 250, WindowManager.LayoutParams.WRAP_CONTENT, true);
        confirmDeletionPopUpWindow.setAnimationStyle(R.style.SlideFromRightAnimation);

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
            mMainViewHolder.fullView.setOnClickListener(v-> {
                mMainViewHolder = (MainViewHolder) holder;

                if (mEditModeIsActive) {
                    mTdeeEditedItemIsSelected.tdeeEditItemSelected(position-1);
                    toggleRowSelectionForEditing();
                    toggleHighlightOfSelectedRow(position-1);
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

            footViewHolder.addActivity.startAnimation(slideInFromLeft);

            footViewHolder.addActivity.setOnClickListener(v-> {
                mTdeeActivityAddition.onAddingActivity(position);
            });
        }
    }

    private void toggleRowSelectionForEditing() {
        mRowIsSelectedForEditing = !mRowIsSelectedForEditing;
    }

    private void toggleHighlightOfSelectedRow(int position) {
        if (!mRowIsSelectedForEditing) {
            mMainViewHolder.fullView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.dark_grey));
        } else {
            mMainViewHolder.fullView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.test_grey));
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

    private void setDefaultMainHolderViewsAndBackgrounds() {
        mMainViewHolder.fullView.setBackground(null);
        if (animateButtonSliding) {
            mMainViewHolder.deleteActivity.startAnimation(slideOutFromRight);
        } else {
            mMainViewHolder.deleteActivity.setVisibility(View.INVISIBLE);
        }
    }

    private void setMainHolderEditModeViews() {
        if (mEditModeIsActive) {
            mMainViewHolder.fullView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.stat_edit_row_border));
            mMainViewHolder.deleteActivity.startAnimation(slideInFromRight);
        }
    }
    private void setHolderViewTextStyles(int textStyle) {
        if (textStyle==BOLD_TEXT) {
            mHeaderViewHolder.activityHeaderTextView.setTextSize(18);
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
        slideInFromRight = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_from_right);
        slideInFromRight.setDuration(200);
        slideInFromRight.setFillAfter(true);

        slideOutFromRight = AnimationUtils.loadAnimation(mContext, R.anim.slide_out_from_right);
        slideOutFromRight.setDuration(200);
        slideOutFromRight.setFillAfter(true);

        slideInFromLeft = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_from_left);
        slideInFromLeft.setDuration(200);
        slideInFromLeft.setFillAfter(true);

        slideOutFromLeft = AnimationUtils.loadAnimation(mContext, R.anim.slide_out_from_left);
        slideOutFromLeft.setDuration(200);
        slideOutFromLeft.setFillAfter(true);

        fadeIn =  AnimationUtils.loadAnimation(mContext, R.anim.fade_in_anim);
        fadeIn.setDuration(400);
        fadeOut =  AnimationUtils.loadAnimation(mContext, R.anim.fade_out_anim);
        fadeOut.setDuration(400);
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
        ImageButton deleteActivity;

        View fullView;
        View endConstraint;

        public MainViewHolder (@NonNull View itemView) {
            super(itemView);
            activityTextView = itemView.findViewById(R.id.activity_in_daily_stats_textView);
            setTimeTextView = itemView.findViewById(R.id.set_time_in_daily_stats_textView);
            caloriesBurnedTextView = itemView.findViewById(R.id.calories_burned_in_daily_stats_textView);
            deleteActivity = itemView.findViewById(R.id.delete_activity_in_edit_stats);

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
        DecimalFormat df = new DecimalFormat("#");
        return df.format(calories);
    }
}