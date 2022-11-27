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

import com.example.tragic.irate.simple.stopwatch.R;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

public class CaloriesConsumedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;

    MainViewHolder mMainViewHolder;

    List<String> mFoodEaten;
    List<Double> mCaloriesConsumed;
    int mItemCount;

    int MAIN_VIEW = 1;
    int FOOTER_VIEW = 2;


    boolean mEditModeIsActive;
    boolean mRowIsSelectedForEditing;

    int mAddingOrEditingFood;
    int ADDING_FOOD = 0;
    int EDITING_FOOD = 1;

    Animation slideInFromLeft;
    Animation slideOutFromLeft;

    boolean animateButtonSliding;

    caloriesConsumedEdit mCaloriesConsumedEdit;
    caloriesConsumedAddition mCaloriesConsumedAddition;

    int mScreenHeight;

    Typeface openSans;
    Typeface roboto;
    Typeface robotoMedium;

    public interface caloriesConsumedEdit {
        void editCaloriesConsumedRowSelected(int position);
    }

    public void editConsumedCalories(caloriesConsumedEdit xCaloriesConsumedEdit) {
        this.mCaloriesConsumedEdit = xCaloriesConsumedEdit;
    }

    public interface caloriesConsumedAddition {
        void onAddingFood();
    }

    public void addCaloriesToStats(caloriesConsumedAddition xCaloriesConsumedAddition) {
        this.mCaloriesConsumedAddition = xCaloriesConsumedAddition;
    }

    public void setScreenHeight(int height) {
        this.mScreenHeight = height;
    }

    public CaloriesConsumedAdapter(Context context, List<String> foodEaten, List<Double> caloriesConsumed) {
        this.mContext = context; this.mFoodEaten = foodEaten; this.mCaloriesConsumed = caloriesConsumed;
        setAnimations();
        instantiateFonts();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view;

        if (viewType==FOOTER_VIEW) {
            view = inflater.inflate(R.layout.calories_consumed_footer_layout, parent, false);
            return new FootViewHolder(view);
        }

        if (mScreenHeight <= 1920) {
            view = inflater.inflate(R.layout.calories_consumed_recycler_layout_h1920, parent, false);
        } else {
            view = inflater.inflate(R.layout.calories_consumed_recycler_layout, parent, false);
        }

        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof CaloriesConsumedAdapter.MainViewHolder) {
            mMainViewHolder = (MainViewHolder) holder;

            populateMainRowViews(position);
            setDefaultMainHolderViewsAndBackgrounds();
            setMainHolderEditModeViews(position);

            mMainViewHolder.fullView.setOnClickListener(v-> {
                mMainViewHolder = (MainViewHolder) holder;

                if (position > 0) {
                    if (mEditModeIsActive) {
                        setAddingOrEditingFoodVariable(EDITING_FOOD);
                        mCaloriesConsumedEdit.editCaloriesConsumedRowSelected(position-1);
                        toggleRowSelectionForEditing();
                    }
                }
            });

        } else if (holder instanceof CaloriesConsumedAdapter.FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) holder;

            footViewHolder.addActivity.startAnimation(slideInFromLeft);

            footViewHolder.addActivity.setOnClickListener(v-> {
                setAddingOrEditingFoodVariable(ADDING_FOOD);
                mCaloriesConsumedAddition.onAddingFood();
            });
        }
    }

    private void setAddingOrEditingFoodVariable(int addingOrEditing) {
        this.mAddingOrEditingFood = addingOrEditing;
    }

    public int getAddingOrEditingFoodVariable() {
        return mAddingOrEditingFood;
    }

    private void toggleRowSelectionForEditing() {
        mRowIsSelectedForEditing = !mRowIsSelectedForEditing;
    }

    private void setDefaultMainHolderViewsAndBackgrounds() {
        mMainViewHolder.fullView.setBackground(null);
    }

    private void setMainHolderEditModeViews(int position) {
        if (mEditModeIsActive) {
            if (position > 0) {
                mMainViewHolder.fullView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.stat_edit_row_border));
            }
        }
    }

    @Override
    public int getItemCount() {
        int listSize = mFoodEaten.size();

        if (!mEditModeIsActive) {
            mItemCount = listSize += 1;
            return mItemCount;
        } else {
            mItemCount = listSize += 2;
            return mItemCount;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position <= mFoodEaten.size() || !mEditModeIsActive){
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
    }

    private void populateMainRowViews(int position) {
        //Returns on last row in edit mode so we don't try to pull textViews from footer.
        if (position==mItemCount-1 && mEditModeIsActive) {
            return;
        }

        if (position == 0) {
            mMainViewHolder.foodEatenTextView.setText(R.string.food_name_header);
            mMainViewHolder.caloriesConsumedTextView.setText(R.string.calories_burned_text_header);

            mMainViewHolder.foodEatenTextView.setTypeface(Typeface.DEFAULT_BOLD);
            mMainViewHolder.caloriesConsumedTextView.setTypeface(Typeface.DEFAULT_BOLD);

            mMainViewHolder.foodEatenTextView.setTextColor(ContextCompat.getColor(mContext, R.color.circular_progress_default_subtitle));
            mMainViewHolder.caloriesConsumedTextView.setTextColor(ContextCompat.getColor(mContext, R.color.circular_progress_default_subtitle));

            if (mScreenHeight <= 1920) {
                mMainViewHolder.foodEatenTextView.setTextSize(17);
                mMainViewHolder.caloriesConsumedTextView.setTextSize(17);
            } else {
                mMainViewHolder.foodEatenTextView.setTextSize(20);
                mMainViewHolder.caloriesConsumedTextView.setTextSize(20);
            }
        } else {
            mMainViewHolder.foodEatenTextView.setTypeface(robotoMedium);
            mMainViewHolder.caloriesConsumedTextView.setTypeface(robotoMedium);

            mMainViewHolder.foodEatenTextView.setText(mFoodEaten.get(position-1));
            mMainViewHolder.caloriesConsumedTextView.setText(formatCalorieString(mCaloriesConsumed.get(position-1)));

            if (mScreenHeight <= 1920) {
                mMainViewHolder.foodEatenTextView.setTextSize(16);
                mMainViewHolder.caloriesConsumedTextView.setTextSize(17);
            } else {
                mMainViewHolder.foodEatenTextView.setTextSize(19);
                mMainViewHolder.caloriesConsumedTextView.setTextSize(20);
            }
        }

        mMainViewHolder.foodEatenTextView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        mMainViewHolder.caloriesConsumedTextView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
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
        TextView foodEatenTextView;
        TextView caloriesConsumedTextView;

        View fullView;

        public MainViewHolder (@NonNull View itemView) {
            super(itemView);
            foodEatenTextView =  itemView.findViewById(R.id.food_eaten_textView);
            caloriesConsumedTextView =  itemView.findViewById(R.id.calories_consumed_textView);

            fullView = itemView;
        }
    }

    public class FootViewHolder extends RecyclerView.ViewHolder {
        ImageButton addActivity;

        public FootViewHolder(@NonNull View itemView) {
            super(itemView);
            addActivity = itemView.findViewById(R.id.add_food_in_edit_stats);
        }
    }

    private void instantiateFonts() {
        openSans = Typeface.createFromAsset(mContext.getAssets(),"fonts/open_sans.ttf");
        roboto = Typeface.createFromAsset(mContext.getAssets(),"fonts/roboto.ttf");
        robotoMedium = Typeface.createFromAsset(mContext.getAssets(),"fonts/roboto_medium.ttf");
    }

    private String formatCalorieString(double calories) {
        DecimalFormat df = new DecimalFormat("#");
        df.setRoundingMode(RoundingMode.DOWN);
        return df.format(calories);
    }
}
