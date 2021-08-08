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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CycleRoundsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  Context mContext;
  ArrayList<String> mWorkOutList;
  ArrayList<Integer> mTypeOfRound;
  ArrayList<String> mPomList;
  public static final int MODE_ONE = 1;
  public static final int MODE_THREE = 3;
  int mMode = 1;
  List<Integer> mPositionList;
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

  public CycleRoundsAdapter(Context context, ArrayList<String> workoutList, ArrayList<Integer> typeOfRound, ArrayList<String> pomList) {
    this.mContext = context; this.mWorkOutList = workoutList; mTypeOfRound = typeOfRound; mPomList = pomList;
    mPositionList = new ArrayList<>();
    animateIn = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
    animateOut = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_out_right);

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

  public void setMode(int mode) {
    mMode = mode;
  }

  public void setFadePositions(int sub, int add) {
    mPosSubHolder = sub; mPosAddHolder = add;
  }

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    if (viewType == MODE_ONE) {
      View view = LayoutInflater.from(context).inflate(R.layout.mode_one_rounds, parent, false);
      return new ModeOneRounds(view);
    } else if (viewType == MODE_THREE) {
      View view = LayoutInflater.from(context).inflate(R.layout.mode_three_rounds, parent, false);
      return new ModeThreeRounds(view);
    } else return null;
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    if (holder instanceof ModeOneRounds) {
      //Casts our custom recyclerView to generic recyclerView class.
      ModeOneRounds modeOneRounds = (ModeOneRounds) holder;
      mPositionList.add(position);

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

      modeOneRounds.round_count.setText(holder.itemView.getContext().getString(R.string.round_numbers, String.valueOf(position + 1)));
      modeOneRounds.workout_rounds.setText(appendSeconds(mWorkOutList.get(position)));

      if (mTypeOfRound.get(position)==1 || mTypeOfRound.get(position)==3) {
        setAnimation(modeOneRounds.round_count, position);
        setAnimation(modeOneRounds.workout_rounds, position);
      } else setAnimationTwo(modeOneRounds.infinity_rounds, position);

    } else if (holder instanceof ModeThreeRounds) {
      ModeThreeRounds modeThreeRounds = (ModeThreeRounds) holder;
      modeThreeRounds.round_pomodoro.setText(mPomList.get(position));
      //Sets work texts to green and break to red.
      if (position%2==0) modeThreeRounds.round_pomodoro.setTextColor(Color.GREEN); else modeThreeRounds.round_pomodoro.setTextColor(Color.RED);
    }
  }

  @Override
  public int getItemCount() {
    switch (mMode) {
      case 1:
        return mWorkOutList.size();
      case 3:
        return mPomList.size();
      default:
        return 0;
    }
  }

  @Override
  public int getItemViewType(int position) {
    switch (mMode) {
      case 1: return MODE_ONE;
      case 3: return MODE_THREE;
      default: return 0;
    }
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

  public class ModeThreeRounds extends RecyclerView.ViewHolder {
    public TextView round_pomodoro;

    public ModeThreeRounds(@NonNull View itemView) {
      super(itemView);
      round_pomodoro = itemView.findViewById(R.id.round_pomodoro);
    }
  }

  public void setAnimation(TextView textView, int position) {
    if (position==mPosAddHolder) {
      textView.clearAnimation();
      textView.startAnimation(animateIn);
    } else if (position==mPosSubHolder) {
      textView.clearAnimation();
      textView.startAnimation(animateOut);
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