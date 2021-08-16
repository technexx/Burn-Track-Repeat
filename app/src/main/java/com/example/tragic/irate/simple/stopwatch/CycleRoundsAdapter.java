package com.example.tragic.irate.simple.stopwatch;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CycleRoundsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  Context mContext;
  View fullView;
  ArrayList<String> mWorkOutList;
  ArrayList<Integer> mTypeOfRound;
  ArrayList<String> mPomList;
  public static final int MODE_ONE = 1;
  public static final int MODE_THREE = 3;
  int mMode = 1;
  int mPosAddHolder;
  int mPosSubHolder;
  Animation animateIn;
  Animation animateOut;
  onFadeFinished mOnFadeFinished;
  boolean mPomFadingIn;
  boolean mFadePom;

  public interface onFadeFinished {
    void fadeHasFinished();
  }

  public void fadeFinished(onFadeFinished xOnFadeFinished) {
    this.mOnFadeFinished = xOnFadeFinished;
  }

  public CycleRoundsAdapter(Context context, ArrayList<String> workoutList, ArrayList<Integer> typeOfRound, ArrayList<String> pomList) {
    this.mContext = context; this.mWorkOutList = workoutList; mTypeOfRound = typeOfRound; mPomList = pomList;
    animateIn = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
    animateOut = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_out_right);
    animateIn.setDuration(300);
    animateOut.setDuration(300);

    //Used to trigger callback to Main when animation has finished. Only needed in Mode 1.
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

  public void disablePomFade() {
    mFadePom = false;
  }

  public void setPomFade(boolean fadingIn) {
    this.mPomFadingIn = fadingIn;
    //Determines whether we want an active animation. Since animateOut's listener clears our adapter lists at fade'a end, we don't want it triggered unless we are actively adding/removing a list.
    mFadePom = true;
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

      //No bullets visible unless a round is selected.
      modeOneRounds.selection_bullet.setVisibility(View.INVISIBLE);
      modeOneRounds.fullView.setOnClickListener(v -> {
        if (modeOneRounds.selection_bullet.getVisibility()==View.INVISIBLE) modeOneRounds.selection_bullet.setVisibility(View.VISIBLE);
        else modeOneRounds.selection_bullet.setVisibility(View.INVISIBLE);
      });

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

      //Animates round number.
      setAnimation(modeOneRounds.round_count, position);
      //Animates round value (either infinity or timer value).
      if (mTypeOfRound.get(position)==1 || mTypeOfRound.get(position)==3) setAnimation(modeOneRounds.workout_rounds, position);
      else setAnimationTwo(modeOneRounds.infinity_rounds, position);

    } else if (holder instanceof ModeThreeRounds) {
      ModeThreeRounds modeThreeRounds = (ModeThreeRounds) holder;
      modeThreeRounds.round_pomodoro.setText(mPomList.get(position));
      //Sets work texts to green and break to red.
      if (position%2==0) modeThreeRounds.round_pomodoro.setTextColor(Color.GREEN); else modeThreeRounds.round_pomodoro.setTextColor(Color.RED);

      setAnimationThree(modeThreeRounds.round_pomodoro, position);
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
    public TextView selection_bullet;
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

  public class ModeThreeRounds extends RecyclerView.ViewHolder {
    public TextView round_pomodoro;
    public View fullView;

    public ModeThreeRounds(@NonNull View itemView) {
      super(itemView);
      round_pomodoro = itemView.findViewById(R.id.round_pomodoro);
      fullView = itemView;
    }
  }

  //Animates each round in or out depending on args received from Main.
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

  public void setAnimationThree(TextView textView, int position) {
    if (mFadePom) {
      if (mPomFadingIn) {
        //Loads new animation for each position, and increases start delay by 100ms for each successive position.
        animateIn = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
        animateIn.setStartOffset(100*position);
        textView.startAnimation(animateIn);
      } else {
        //Fades out all rounds at once when removing cycle.
        textView.startAnimation(animateOut);
      }
    }
  }

  public String appendSeconds(String seconds) {
    if (seconds.length()==1) seconds = "0:0" + seconds;
    else if (seconds.length()==2) seconds = "0:" + seconds;
    return seconds;
  }
}