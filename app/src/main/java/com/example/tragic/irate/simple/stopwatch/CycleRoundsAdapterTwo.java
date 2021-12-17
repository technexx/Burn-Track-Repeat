package com.example.tragic.irate.simple.stopwatch;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class CycleRoundsAdapterTwo extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;
    ArrayList<String> mWorkOutList;
    ArrayList<Integer> mTypeOfRound;
    int positionCount;
    int mPosAddHolder;
    int mPosSubHolder;
    Animation animateIn;
    Animation animateOut;
    onFadeFinished mOnFadeFinished;
    onRoundSelectedSecondAdapter mOnRoundSelectedSecondAdapter;

    boolean mRunRoundAnimation;
    boolean mRoundSelected;
    int mPositionOfSelectedRound;

    ChangeSettingsValues changeSettingsValues = new ChangeSettingsValues();
    int SET_COLOR;
    int BREAK_COLOR;

    public interface onFadeFinished {
        void subtractionFadeHasFinished();
    }

    public interface onRoundSelectedSecondAdapter {
        void roundSelectedSecondAdapter(boolean selected, int position);
    }

    public void fadeFinished(onFadeFinished xOnFadeFinished) {
        this.mOnFadeFinished = xOnFadeFinished;
    }

    public void selectedRoundSecondAdapter(onRoundSelectedSecondAdapter xOnRoundSelectedSecondAdapter) {
        this.mOnRoundSelectedSecondAdapter = xOnRoundSelectedSecondAdapter;
    }

    public void changeColorSetting(int typeOFRound, int settingNumber) {
        int color = changeSettingsValues.assignColor(settingNumber);

        if (typeOFRound==1) SET_COLOR = color;
        if (typeOFRound==2) BREAK_COLOR = color;
    }


    public CycleRoundsAdapterTwo(Context context, ArrayList<String> workoutList, ArrayList<Integer> typeOfRound) {
        this.mContext = context; this.mWorkOutList = workoutList; mTypeOfRound = typeOfRound;
        animateIn = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
        animateOut = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_out_right);
        animateIn.setDuration(300);
        animateOut.setDuration(300);

        //Used to trigger callback to Main when animation has finished.
        animateOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                mOnFadeFinished.subtractionFadeHasFinished();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    //Receives position from Main to fade (or not), and also sets our fade animation boolean to true.
    public void setFadeInPosition(int add) {
        mPosAddHolder = add; mPosSubHolder = -1; mRunRoundAnimation = true;
    }

    public void setFadeOutPosition(int subtract){
        mPosSubHolder = subtract; mPosAddHolder = -1; mRunRoundAnimation = true;
    }

    public void isRoundCurrentlySelected(boolean selected) {
        mRoundSelected = selected;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.mode_one_rounds_second_adapter, parent, false);
        return new ModeOneRounds(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //Casts our custom recyclerView to generic recyclerView class.
        CycleRoundsAdapterTwo.ModeOneRounds modeOneRounds = (CycleRoundsAdapterTwo.ModeOneRounds) holder;
        modeOneRounds.selection_bullet.setVisibility(View.INVISIBLE);

        //If a round has been selected (and boolean set to true), set only that position's bullet to visible.
        if (mRoundSelected) if (position==mPositionOfSelectedRound) modeOneRounds.selection_bullet.setVisibility(View.VISIBLE);

        modeOneRounds.fullView.setOnClickListener(v -> {
            //Toggles bullet appearance next to each round, and de-selects a round if another is selected.
            if (!mRoundSelected) {
                modeOneRounds.selection_bullet.setVisibility(View.VISIBLE);
                //This var is used to make visible the correct bullet position when we re-draw this adapter's list.
                mPositionOfSelectedRound = position;
                //Passes position to Main activity. +8 since we use the total workout list (up to 16 rounds/positions) as a conditional in Main.
                mOnRoundSelectedSecondAdapter.roundSelectedSecondAdapter(true, position+8);
                //Since we need to remove the previous bullet when selecting a new round, we need to re-draw the list.
                notifyDataSetChanged();
                //If position we are clicking on shows a bullet, remove it.
            } else {
                mOnRoundSelectedSecondAdapter.roundSelectedSecondAdapter(false, 0);
                modeOneRounds.selection_bullet.setVisibility(View.INVISIBLE);
            }
        });

        //Sets color, visibility, and textViews for sets, breaks, and their infinity modes.
        switch (mTypeOfRound.get(position)) {
            case 1:
                modeOneRounds.infinity_rounds.setVisibility(View.INVISIBLE);
                modeOneRounds.workout_rounds.setVisibility(View.VISIBLE);
                modeOneRounds.workout_rounds.setTextColor(SET_COLOR);
                break;
            case 2:
                modeOneRounds.infinity_rounds.setVisibility(View.VISIBLE);
                modeOneRounds.workout_rounds.setVisibility(View.INVISIBLE);
                modeOneRounds.infinity_rounds.setColorFilter(SET_COLOR);
                break;
            case 3:
                modeOneRounds.infinity_rounds.setVisibility(View.INVISIBLE);
                modeOneRounds.workout_rounds.setVisibility(View.VISIBLE);
                modeOneRounds.workout_rounds.setTextColor(BREAK_COLOR);
                break;
            case 4:
                modeOneRounds.workout_rounds.setVisibility(View.INVISIBLE);
                modeOneRounds.infinity_rounds.setVisibility(View.VISIBLE);
                modeOneRounds.infinity_rounds.setColorFilter(BREAK_COLOR);
                break;
        }

        //Only runs fade animation if adding/subtracting rounds.
        if (mRunRoundAnimation) {
            if (mPosAddHolder>=0 || mPosSubHolder >=0) {
                //Sets animation of round number.
                setAnimation(modeOneRounds.round_count, position);
                //Animates round value (either infinity or timer value).
                if (mTypeOfRound.get(position)==1 || mTypeOfRound.get(position)==3) setAnimation(modeOneRounds.workout_rounds, position);
                else setAnimationTwo(modeOneRounds.infinity_rounds, position);
            }
        }
        if (position==mWorkOutList.size()-1) mRunRoundAnimation = false;

        //For moment, using "09" on first round of this adapter, and setting "0" to same color as background. Trouble aligning otherwise.
        if (position==0) {
            Spannable spannable = new SpannableString("09 -");
            spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#37474F")), 0, 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            modeOneRounds.round_count.setText(spannable);
        } else modeOneRounds.round_count.setText(holder.itemView.getContext().getString(R.string.round_numbers, String.valueOf(position + 9)));
        modeOneRounds.workout_rounds.setText(appendSeconds(mWorkOutList.get(position)));
        //Last adapter position has been iterated through, and we set our fade animation boolean back to false.
    }

    @Override
    public int getItemCount() {
        return mWorkOutList.size();
    }

    public class ModeOneRounds extends RecyclerView.ViewHolder {
        public TextView round_count;
        public TextView workout_rounds;
        public ImageView infinity_rounds;
        public ImageView selection_bullet;
        public View fullView;

        public ModeOneRounds(@NonNull View itemView) {
            super(itemView);
            round_count = itemView.findViewById(R.id.round_count);
            workout_rounds = itemView.findViewById(R.id.workout_rounds);
            infinity_rounds = itemView.findViewById(R.id.round_infinity);
            selection_bullet = itemView.findViewById(R.id.selection_bullet);
            fullView = itemView;
        }
    }

    public void setAnimation(TextView textView, int position) {
        if (position==mPosAddHolder) {
            textView.clearAnimation();
            textView.startAnimation(animateIn);
            positionCount++;
        } else if (position==mPosSubHolder) {
            textView.clearAnimation();
            textView.startAnimation(animateOut);
            positionCount++;
        }
    }

    public void setAnimationTwo(ImageView imageView, int position) {
        if (position==mPosAddHolder) {
            imageView.clearAnimation();
            imageView.startAnimation(animateIn);
        } else if (position==mPosSubHolder) {
            imageView.clearAnimation();
            imageView.startAnimation(animateOut);
        }
    }

    public String appendSeconds(String seconds) {
        if (seconds.length()==1) seconds = "0:0" + seconds;
        else if (seconds.length()==2) seconds = "0:" + seconds;
        return seconds;
    }
}
