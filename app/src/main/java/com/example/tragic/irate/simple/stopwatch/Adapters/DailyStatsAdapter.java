package com.example.tragic.irate.simple.stopwatch.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tragic.irate.simple.stopwatch.Miscellaneous.LongToStringConverters;
import com.example.tragic.irate.simple.stopwatch.R;
import com.google.gson.TypeAdapterFactory;

import java.lang.reflect.Type;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

public class DailyStatsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    MainViewHolder mMainViewHolder;
    LongToStringConverters longToStringConverters = new LongToStringConverters();

    List<String> mActivities;
    List<Long> mSetTimes;
    List<Double> mCaloriesBurned;
    int mItemCount;
    int MAIN_VIEW = 1;
    int FOOTER_VIEW = 2;

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

    int mPhoneHeight;

    Typeface openSans;
    Typeface roboto;
    Typeface robotoMedium;

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
        instantiateFonts();
    }

    public void setScreenHeight(int height) {
        this.mPhoneHeight = height;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view;

        if (viewType==FOOTER_VIEW) {
            view = inflater.inflate(R.layout.daily_stats_recycler_footer_layout, parent, false);
            return new FootViewHolder(view);
        }

        if (mPhoneHeight <= 1920) {
            view = inflater.inflate(R.layout.daily_stats_recycler_layout_h1920, parent, false);
        } else {
            view = inflater.inflate(R.layout.daily_stats_recycler_layout, parent, false);
        }

        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MainViewHolder) {
            mMainViewHolder = (MainViewHolder) holder;

            populateMainRowViews(position);
            setDefaultMainHolderViewsAndBackgrounds();
            setMainHolderEditModeViews(position);

            //mMainViewHolder instance set on onClick is set on first adapter population. It does NOT get refreshed with views in adapter, which is why we need new instances if we're going to use it as a global.
            mMainViewHolder.fullView.setOnClickListener(v-> {
                mMainViewHolder = (MainViewHolder) holder;

                if (position > 0) {
                    if (mEditModeIsActive) {
                        setAddingOrEditingActivityVariable(EDITING_ACTIVITY);
                        mTdeeEditedItemIsSelected.activityEditItemSelected(position-1);
                        toggleRowSelectionForEditing();
                    }
                }
            });
        } else if (holder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) holder;

            if (mPhoneHeight <= 1920) {
                footViewHolder.addActivity.setBackground(ContextCompat.getDrawable(mContext, R.drawable.add_32));
            } else {
                footViewHolder.addActivity.setBackground(ContextCompat.getDrawable(mContext, R.drawable.add_40));            }

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
        //<= because header takes up first position and doesn't pull from list.
        if (position <= mActivities.size() || !mEditModeIsActive){
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

    private void setDefaultMainHolderViewsAndBackgrounds() {
//        mMainViewHolder.fullView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.matte_black));
        mMainViewHolder.fullView.setBackground(null);
    }

    private void setMainHolderEditModeViews(int position) {
        if (mEditModeIsActive) {
            if (position > 0) {
                mMainViewHolder.fullView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.stat_edit_row_border));
            }
        }
    }

    private void populateMainRowViews(int position) {
        //Returns on last row in edit mode so we don't try to pull textViews from footer.
        if (position==mItemCount-1 && mEditModeIsActive) {
            return;
        }

        if (position == 0) {
            if (mPhoneHeight <= 1920) {
                mMainViewHolder.activityTextView.setTextSize(17);
                mMainViewHolder.setTimeTextView.setTextSize(17);
                mMainViewHolder.caloriesBurnedTextView.setTextSize(17);
            } else {
                mMainViewHolder.activityTextView.setTextSize(20);
                mMainViewHolder.setTimeTextView.setTextSize(20);
                mMainViewHolder.caloriesBurnedTextView.setTextSize(20);
            }

            mMainViewHolder.activityTextView.setText(R.string.activity_text_header);
            mMainViewHolder.setTimeTextView.setText(R.string.set_time_text_header);
            mMainViewHolder.caloriesBurnedTextView.setText(R.string.calories_burned_text_header);

            mMainViewHolder.activityTextView.setTypeface(Typeface.DEFAULT_BOLD);
            mMainViewHolder.setTimeTextView.setTypeface(Typeface.DEFAULT_BOLD);
            mMainViewHolder.caloriesBurnedTextView.setTypeface(Typeface.DEFAULT_BOLD);

            mMainViewHolder.activityTextView.setTextColor(ContextCompat.getColor(mContext, R.color.circular_progress_default_subtitle));
            mMainViewHolder.setTimeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.circular_progress_default_subtitle));
            mMainViewHolder.caloriesBurnedTextView.setTextColor(ContextCompat.getColor(mContext, R.color.circular_progress_default_subtitle));
        } else {
            if (mPhoneHeight <= 1920 ) {
                mMainViewHolder.activityTextView.setTextSize(15);
                mMainViewHolder.setTimeTextView.setTextSize(17);
                mMainViewHolder.caloriesBurnedTextView.setTextSize(17);
            } else {
                mMainViewHolder.activityTextView.setTextSize(17);
                mMainViewHolder.setTimeTextView.setTextSize(19);
                mMainViewHolder.caloriesBurnedTextView.setTextSize(19);
            }

            mMainViewHolder.activityTextView.setTypeface(robotoMedium);
            mMainViewHolder.setTimeTextView.setTypeface(robotoMedium);
            mMainViewHolder.caloriesBurnedTextView.setTypeface(robotoMedium);

            Log.i("testDelete", "activity list size is " + mActivities.size());
            Log.i("testDelete", "calorie list size is " + mCaloriesBurned.size());
            Log.i("testDelete", "position is " + position);
            //Invalid item position 6(offset:6).state:7

            //Todo: Positions return very funky post-delete. They went 4-9 with no 0-3 (before revert).
            //Todo: Try not using global mItemCount.

            mMainViewHolder.activityTextView.setText(mActivities.get(position-1));
            mMainViewHolder.setTimeTextView.setText(longToStringConverters.convertMillisToHourBasedString(mSetTimes.get(position-1)));
            mMainViewHolder.caloriesBurnedTextView.setText(formatCalorieString(mCaloriesBurned.get(position-1)));

            mMainViewHolder.activityTextView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            mMainViewHolder.setTimeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            mMainViewHolder.caloriesBurnedTextView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
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

    private void instantiateFonts() {
        openSans = Typeface.createFromAsset(mContext.getAssets(),"fonts/open_sans.ttf");
        roboto = Typeface.createFromAsset(mContext.getAssets(),"fonts/roboto.ttf");
        robotoMedium = Typeface.createFromAsset(mContext.getAssets(),"fonts/roboto_medium.ttf");
    }

    //This doesn't round because input isn't a decimal (e.g. "0" is "0" until it is "1").
    private String formatCalorieString(double calories) {
        DecimalFormat df = new DecimalFormat("#");
        df.setRoundingMode(RoundingMode.DOWN);
        return df.format(calories);
    }
}