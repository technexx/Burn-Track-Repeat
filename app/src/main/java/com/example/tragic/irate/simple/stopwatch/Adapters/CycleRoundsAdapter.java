package com.example.tragic.irate.simple.stopwatch.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tragic.irate.simple.stopwatch.SettingsFragments.ChangeSettingsValues;
import com.example.tragic.irate.simple.stopwatch.R;

import java.util.ArrayList;

public class CycleRoundsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  Context mContext;
  onFadeFinished mOnFadeFinished;
  onRoundSelected mOnRoundSelected;
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
  boolean mRunRoundAnimation;
  boolean mPomFadingIn;
  boolean mFadePom;

  boolean mRoundSelected;
  int mPositionOfSelectedRound;

  ChangeSettingsValues changeSettingsValues;
  int SET_COLOR;
  int BREAK_COLOR;
  int WORK_COLOR;
  int MINI_BREAK_COLOR;
  int FULL_BREAK_COLOR;

  int mScreenHeight;

  public interface onFadeFinished {
    void subtractionFadeHasFinished();
  }

  public interface onRoundSelected {
    void roundSelected(boolean selected, int position);
  }

  public void fadeFinished(onFadeFinished xOnFadeFinished) {
    this.mOnFadeFinished = xOnFadeFinished;
  }

  public void selectedRound(onRoundSelected xOnRoundSelected) {
    this.mOnRoundSelected = xOnRoundSelected;
  }

  public void setColorSettingsFromMainActivity(int roundCategory, int settingNumber) {
    int color = changeSettingsValues.assignColor(settingNumber);

    if (roundCategory==1) SET_COLOR = color;
    if (roundCategory==2) BREAK_COLOR = color;
    if (roundCategory==3) WORK_COLOR = color;
    if (roundCategory==4) MINI_BREAK_COLOR = color;
    if (roundCategory==5) FULL_BREAK_COLOR = color;
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
        mOnFadeFinished.subtractionFadeHasFinished();
      }
      @Override
      public void onAnimationRepeat(Animation animation) {
      }
    });

    changeSettingsValues  = new ChangeSettingsValues(mContext);
  }

  public void setScreenHeight(int height) {
    this.mScreenHeight = height;
  }

  public void setMode(int mode) {
    mMode = mode;
    animateIn.setStartOffset(0);
  }

  //Receives position from Main to fade (or not), and also sets our fade animation boolean to true.
  public void setFadeInPosition(int add) {
    mPosAddHolder = add; mPosSubHolder = -1; mRunRoundAnimation = true;
  }

  public void setFadeOutPosition(int subtract){
    mPosSubHolder = subtract; mPosAddHolder = -1; mRunRoundAnimation = true;
  }

  public void disablePomFade() {
    mFadePom = false;
  }

  public void setPomFade(boolean fadingIn) {
    this.mPomFadingIn = fadingIn;
    //Determines whether we want an active animation. Since animateOut's listener clears our adapter lists at fade's end, we don't want it triggered unless we are actively adding/removing a list.
    mFadePom = true;
  }

  public void setIsRoundCurrentlySelectedBoolean(boolean selected) {
    mRoundSelected = selected;
  }

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    View view;

    if (viewType == MODE_ONE) {
      if (mScreenHeight <= 1920) {
        view = LayoutInflater.from(context).inflate(R.layout.mode_one_rounds_h1920, parent, false);
      } else {
        view = LayoutInflater.from(context).inflate(R.layout.mode_one_rounds, parent, false);
      }
      return new ModeOneRounds(view);
    } else if (viewType == MODE_THREE) {
      if (mScreenHeight <= 1920) {
        view = LayoutInflater.from(context).inflate(R.layout.mode_three_rounds_h1920, parent, false);
      } else {
        view = LayoutInflater.from(context).inflate(R.layout.mode_three_rounds, parent, false);
      }
      return new ModeThreeRounds(view);
    } else {
      return null;
    }
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    if (holder instanceof ModeOneRounds) {
      //Casts our custom recyclerView to generic recyclerView class.
      ModeOneRounds modeOneRounds = (ModeOneRounds) holder;
      modeOneRounds.selection_bullet.setVisibility(View.INVISIBLE);

      if (mRoundSelected){
        if (position==mPositionOfSelectedRound) {
          modeOneRounds.selection_bullet.setVisibility(View.VISIBLE);
        }
      }

      modeOneRounds.fullView.setOnClickListener(v -> {
        if (!mRoundSelected || position!=mPositionOfSelectedRound) {
          modeOneRounds.selection_bullet.setVisibility(View.VISIBLE);
          //This var is used to make visible the correct bullet position when we re-draw this adapter's list.
          mPositionOfSelectedRound = position;
          //Passes position to Main activity.
          mOnRoundSelected.roundSelected(true, position);
          //Since we need to remove the previous bullet when selecting a new round, we need to re-draw the list.
          notifyDataSetChanged();
          //If position we are clicking on shows a bullet, remove it.
        } else {
          mOnRoundSelected.roundSelected(false, 0);
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
        //Animates round number.
        if (mPosAddHolder >=0 || mPosSubHolder >=0) {
          setAnimationOne(modeOneRounds.round_count, position);
          //Animates round value (either infinity or timer value).
          if (mTypeOfRound.get(position)==1 || mTypeOfRound.get(position)==3) {
            setAnimationOne(modeOneRounds.workout_rounds, position);
          } else {
            setAnimationTwo(modeOneRounds.infinity_rounds, position);
          }
        }
      }

      if (position<=8) {
        modeOneRounds.round_count.setText(holder.itemView.getContext().getString(R.string.round_numbers, "0" + (position + 1)));
      } else {
        modeOneRounds.round_count.setText(holder.itemView.getContext().getString(R.string.round_numbers, String.valueOf(position + 1)));
      }
      modeOneRounds.workout_rounds.setText(appendSeconds(mWorkOutList.get(position)));
      //Last adapter position has been iterated through, and we set our fade animation boolean back to false.
      if (position==mWorkOutList.size()-1) mRunRoundAnimation = false;

    } else if (holder instanceof ModeThreeRounds) {
      ModeThreeRounds modeThreeRounds = (ModeThreeRounds) holder;
      modeThreeRounds.round_pomodoro.setText(mPomList.get(position));

      setAnimationThree(modeThreeRounds.round_pomodoro, position);
      //Sets work texts to green and break to red.
      if (position != 7) {
        if (position % 2 == 0){
          modeThreeRounds.round_pomodoro.setTextColor(WORK_COLOR);
        }
        else {
          modeThreeRounds.round_pomodoro.setTextColor(MINI_BREAK_COLOR);
        }
      } else {
        modeThreeRounds.round_pomodoro.setTextColor(FULL_BREAK_COLOR);
      }
    }
  }

  @Override
  public int getItemCount() {
    switch (mMode) {
      case 1:
        return mWorkOutList.size();
      case 3:
        Log.i("testMode", "pom list is " + mPomList);
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
  public void setAnimationOne(TextView textView, int position) {
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
        Log.i("testAnim", "setAnimationThree running");
      }
    }
  }

  public String appendSeconds(String seconds) {
    if (seconds.length()==1) seconds = "0:0" + seconds;
    else if (seconds.length()==2) seconds = "0:" + seconds;
    return seconds;
  }
}