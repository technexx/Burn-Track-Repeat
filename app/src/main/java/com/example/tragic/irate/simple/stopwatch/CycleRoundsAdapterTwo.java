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

    public interface onFadeFinished {
        void fadeHasFinished();
    }

    public void fadeFinished(onFadeFinished xOnFadeFinished) {
        this.mOnFadeFinished = xOnFadeFinished;
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
                mOnFadeFinished.fadeHasFinished();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    public void setFadePositions(int sub, int add) {
        mPosSubHolder = sub; mPosAddHolder = add;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.mode_one_rounds, parent, false);
        return new ModeOneRounds(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //Casts our custom recyclerView to generic recyclerView class.
        CycleRoundsAdapterTwo.ModeOneRounds modeOneRounds = (CycleRoundsAdapterTwo.ModeOneRounds) holder;

        //Sets color, visibility, and textViews for sets, breaks, and their infinity modes.
        switch (mTypeOfRound.get(position)) {
            case 1:
                modeOneRounds.infinity_rounds.setVisibility(View.INVISIBLE);
                modeOneRounds.workout_rounds.setVisibility(View.VISIBLE);
                modeOneRounds.workout_rounds.setTextColor(Color.GREEN);
                break;
            case 2:
                modeOneRounds.infinity_rounds.setVisibility(View.VISIBLE);
                modeOneRounds.workout_rounds.setVisibility(View.INVISIBLE);
                modeOneRounds.infinity_rounds.setColorFilter(Color.GREEN);
                break;
            case 3:
                modeOneRounds.infinity_rounds.setVisibility(View.INVISIBLE);
                modeOneRounds.workout_rounds.setVisibility(View.VISIBLE);
                modeOneRounds.workout_rounds.setTextColor(Color.RED);
                break;
            case 4:
                modeOneRounds.workout_rounds.setVisibility(View.INVISIBLE);
                modeOneRounds.infinity_rounds.setVisibility(View.VISIBLE);
                modeOneRounds.infinity_rounds.setColorFilter(Color.RED);
                break;
        }

        //For moment, using "09" on first round of this adapter, and setting "0" to same color as background. Trouble aligning otherwise.
        if (position==0) {
            Spannable spannable = new SpannableString("09 -");
            spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#37474F")), 0, 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            modeOneRounds.round_count.setText(spannable);
        } else modeOneRounds.round_count.setText(holder.itemView.getContext().getString(R.string.round_numbers, String.valueOf(position + 9)));
        modeOneRounds.workout_rounds.setText(appendSeconds(mWorkOutList.get(position)));
        //Sets animation of round number.
        setAnimation(modeOneRounds.round_count, position);
        //Animates round value (either infinity or timer value).
        if (mTypeOfRound.get(position)==1 || mTypeOfRound.get(position)==3) setAnimation(modeOneRounds.workout_rounds, position);
        else setAnimationTwo(modeOneRounds.infinity_rounds, position);
    }

    @Override
    public int getItemCount() {
        return mWorkOutList.size();
    }

    public class ModeOneRounds extends RecyclerView.ViewHolder {
        public TextView round_count;
        public TextView workout_rounds;
        public ImageView infinity_rounds;

        public ModeOneRounds(@NonNull View itemView) {
            super(itemView);
            round_count = itemView.findViewById(R.id.round_count);
            workout_rounds = itemView.findViewById(R.id.workout_rounds);
            infinity_rounds = itemView.findViewById(R.id.round_infinity);
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
