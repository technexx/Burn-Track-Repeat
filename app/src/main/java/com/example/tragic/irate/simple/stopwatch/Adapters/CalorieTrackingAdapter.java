package com.example.tragic.irate.simple.stopwatch.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tragic.irate.simple.stopwatch.R;

import java.util.List;

public class CalorieTrackingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;

    List<String> mFoodEaten;
    List<Double> mFoodPortion;
    List<Double> mCaloriesConsumed;
    int mItemCount;

    int HEADER_VIEW = 0;
    int MAIN_VIEW = 1;
    int FOOTER_VIEW = 2;

    int REGULAR_TEXT = 0;
    int BOLD_TEXT = 1;

    boolean mEditModeIsActive;
    boolean mRowIsSelectedForEditing;

    Animation slideInFromLeft;
    Animation slideOutFromLeft;

    boolean animateButtonSliding;

    public CalorieTrackingAdapter(Context context, List<String> foodEaten, List<Double> foodPortion, List<Double> caloriesConsumed) {
        this.mContext = context; this.mFoodEaten = foodEaten; this.mFoodPortion = foodPortion; this.mCaloriesConsumed = caloriesConsumed;
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
        TextView foodPortionTextView;;
        TextView caloriesConsumedTextView;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            foodEatenTextView =  itemView.findViewById(R.id.food_eaten_header);
            foodPortionTextView =  itemView.findViewById(R.id.food_portion_header);
            caloriesConsumedTextView =  itemView.findViewById(R.id.calories_consumed_by_food_header);
        }
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {
        TextView foodEatenTextView;
        TextView foodPortionTextView;;
        TextView caloriesConsumedTextView;

        View fullView;

        public MainViewHolder (@NonNull View itemView) {
            super(itemView);
            foodEatenTextView =  itemView.findViewById(R.id.food_eaten_textView);
            foodPortionTextView =  itemView.findViewById(R.id.food_portion_textView);
            caloriesConsumedTextView =  itemView.findViewById(R.id.calories_consumed_textView);

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
}
