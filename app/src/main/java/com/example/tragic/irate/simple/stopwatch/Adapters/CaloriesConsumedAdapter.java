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

    HeaderViewHolder mHeaderViewHolder;
    MainViewHolder mMainViewHolder;

    List<String> mFoodEaten;
    List<Double> mCaloriesConsumed;
    int mItemCount;

    int HEADER_VIEW = 0;
    int MAIN_VIEW = 1;
    int FOOTER_VIEW = 2;

    int REGULAR_TEXT = 0;
    int BOLD_TEXT = 1;

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

    public CaloriesConsumedAdapter(Context context, List<String> foodEaten, List<Double> caloriesConsumed) {
        this.mContext = context; this.mFoodEaten = foodEaten; this.mCaloriesConsumed = caloriesConsumed;
        setAnimations();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view;

        if (viewType==HEADER_VIEW) {
            view = inflater.inflate(R.layout.calories_consumed_recycler_header_layout, parent, false);
            return new HeaderViewHolder(view);
        } else if (viewType==FOOTER_VIEW) {
            view = inflater.inflate(R.layout.calories_consumed_footer_layout, parent, false);
            return new FootViewHolder(view);
        }

        view = inflater.inflate(R.layout.calories_consumed_recycler_layout, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CaloriesConsumedAdapter.HeaderViewHolder) {
            mHeaderViewHolder = (HeaderViewHolder) holder;

            populateHeaderRowViews();
            setHolderViewTextStyles(BOLD_TEXT);

        } else if (holder instanceof CaloriesConsumedAdapter.MainViewHolder) {
            mMainViewHolder = (MainViewHolder) holder;

            populateMainRowViews(position);
            setDefaultMainHolderViewsAndBackgrounds();
            setMainHolderEditModeViews();
            setHolderViewTextStyles(REGULAR_TEXT);

            mMainViewHolder.fullView.setOnClickListener(v-> {
                mMainViewHolder = (MainViewHolder) holder;

                if (mEditModeIsActive) {
                    setAddingOrEditingFoodVariable(EDITING_FOOD);
                    mCaloriesConsumedEdit.editCaloriesConsumedRowSelected(position-1);
                    toggleRowSelectionForEditing();
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

    private void setMainHolderEditModeViews() {
        if (mEditModeIsActive) {
            mMainViewHolder.fullView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.stat_edit_row_border));
        }
    }

    @Override
    public int getItemCount() {
        if (!mEditModeIsActive) {
            return mItemCount = mFoodEaten.size()+1;
        } else {
            return mItemCount = mFoodEaten.size()+2;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position==0) {
            return HEADER_VIEW;
            //<= because header takes up first position and doesn't pull from list.
        } else if (position <= mFoodEaten.size() || !mEditModeIsActive){
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
        mHeaderViewHolder.foodEatenTextView.setText(R.string.food_name_header);
        mHeaderViewHolder.caloriesConsumedTextView.setText(R.string.calories_burned_text_header);
    }

    private void populateMainRowViews(int position) {
        //Returns on last row in edit mode so we don't try to pull textViews from footer.
        if (position==mItemCount-1 && mEditModeIsActive) {
            return;
        }
        mMainViewHolder.foodEatenTextView.setText(mFoodEaten.get(position-1));
        mMainViewHolder.caloriesConsumedTextView.setText(formatCalorieString(mCaloriesConsumed.get(position-1)));
    }

    private void setHolderViewTextStyles(int textStyle) {
        if (textStyle==BOLD_TEXT) {
            mHeaderViewHolder.foodEatenTextView.setTypeface(Typeface.DEFAULT_BOLD);
            mHeaderViewHolder.caloriesConsumedTextView.setTypeface(Typeface.DEFAULT_BOLD);

        } else if (textStyle==REGULAR_TEXT){
            mMainViewHolder.foodEatenTextView.setTypeface(Typeface.DEFAULT);
            mMainViewHolder.caloriesConsumedTextView.setTypeface(Typeface.DEFAULT);
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
        TextView foodEatenTextView;
        TextView caloriesConsumedTextView;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            foodEatenTextView =  itemView.findViewById(R.id.food_eaten_header);
            caloriesConsumedTextView =  itemView.findViewById(R.id.calories_consumed_by_food_header);
        }
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

    private String formatCalorieString(double calories) {
        DecimalFormat df = new DecimalFormat("#");
        df.setRoundingMode(RoundingMode.DOWN);
        return df.format(calories);
    }
}
