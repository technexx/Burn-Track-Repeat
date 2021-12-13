package com.example.tragic.irate.simple.stopwatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SavedCycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  Context mContext;

  ArrayList<String> mWorkoutList;
  ArrayList<String> mRoundType;
  ArrayList<String> mWorkoutTitle;

  onCycleClickListener mOnCycleClickListener;
  onHighlightListener mOnHighlightListener;
  onResumeOrResetCycle mOnResumeOrResetCycle;

  int RESUMING_CYCLE_FROM_TIMER = 1;
  int RESETTING_CYCLE_FROM_TIMER = 2;

  boolean mHighlightDeleted;
  boolean mHighlightMode;

  List<String> mPositionList;
  CharSequence permSpan;
  Spannable span;
  ImageSpan imageSpan;

  boolean mActiveCycle;
  int mPositionOfActiveCycle;
  int mNumberOfRoundsCompleted;

  ChangeSettingsValues changeSettingsValues = new ChangeSettingsValues();
  int SET_COLOR;
  int BREAK_COLOR;

  public void changeColorSetting(int typeOFRound, int settingNumber) {
    if (typeOFRound==1) SET_COLOR = changeSettingsValues.assignColor(settingNumber);
    if (typeOFRound==2) BREAK_COLOR = changeSettingsValues.assignColor(settingNumber);
  }

  public boolean isCycleActive() {
    return mActiveCycle;
  }

  public boolean isCycleHighlighted() {
    return mHighlightMode;
  }

  public interface onCycleClickListener {
    void onCycleClick (int position);
  }

  public interface onHighlightListener {
    void onCycleHighlight (List<String> listOfPositions, boolean addButtons);
  }

  public interface onResumeOrResetCycle{
    void ResumeOrResetCycle(int resumingOrResetting);
  }

  public void setItemClick(onCycleClickListener xOnCycleClickListener) {
    this.mOnCycleClickListener = xOnCycleClickListener;
  }

  public void setHighlight(onHighlightListener xOnHighlightListener) {
    this.mOnHighlightListener = xOnHighlightListener;
  }

  public void setResumeOrResetCycle(onResumeOrResetCycle xOnResumeOrResetCycle) {
    this.mOnResumeOrResetCycle = xOnResumeOrResetCycle;
  }

  //Remember, constructor always called first (i.e. can't instantiate anything here based on something like setList's size, etc.).
  public SavedCycleAdapter (Context context, ArrayList<String> workoutList, ArrayList<String> roundType, ArrayList<String> workoutTitle) {
    this.mContext = context; mWorkoutList = workoutList; this.mRoundType = roundType; this.mWorkoutTitle = workoutTitle;
    //Must be instantiated here so it does not loop and reset in onBindView.
    mPositionList = new ArrayList<>();
    //Resets our cancel so bindView does not continuously call black backgrounds.
    mHighlightDeleted = false;
  }

  public void removeHighlight(boolean cancelMode) {
    //If boolean is false, highlight has simply been deleted and we clear the highlight list while turning all backgrounds black.
    mHighlightDeleted = true;
    //If boolean is true, we have canceled the highlight process entirely, which does the above but also removes the Trash/Back buttons (done in Main) and sets the next row click to launch a timer instead of highlight (done here).
    if (cancelMode) mHighlightMode = false;
  }

  public void showActiveCycleLayout(int positionOfCycle, int numberOfRoundsCompleted) {
    mActiveCycle = true; mPositionOfActiveCycle = positionOfCycle; mNumberOfRoundsCompleted = numberOfRoundsCompleted;
  }

  public void removeActiveCycleLayout() {
    mActiveCycle = false;
  }

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    View view = LayoutInflater.from(context).inflate(R.layout.mode_one_cycles, parent, false);
    return new WorkoutHolder(view);
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    //Used to store highlighted positions that we callback to Main to delete.
    WorkoutHolder workoutHolder = (WorkoutHolder) holder;

    workoutHolder.resumeCycle.setVisibility(View.GONE);
    workoutHolder.resetCycle.setVisibility(View.GONE);
    //Adds text to resume/reset underneath active cycle.
    if (mActiveCycle) {
      if (position==mPositionOfActiveCycle) {
        workoutHolder.resumeCycle.setVisibility(View.VISIBLE);
        workoutHolder.resetCycle.setVisibility(View.VISIBLE);
        workoutHolder.resumeCycle.setOnClickListener(v-> mOnResumeOrResetCycle.ResumeOrResetCycle(RESUMING_CYCLE_FROM_TIMER));
        workoutHolder.resetCycle.setOnClickListener(v-> mOnResumeOrResetCycle.ResumeOrResetCycle(RESETTING_CYCLE_FROM_TIMER));
      }
    }

    workoutHolder.workoutName.setText(mWorkoutTitle.get(position));
    //Clearing Spannable object, since it will re-populate for every position passed in through this method.
    permSpan = "";
    //Retrieves the concatenated String of workout TIMES from current position.
    String tempWorkoutString = convertTime(mWorkoutList).get(position);
    //Retrieves the concatenated String of ROUND TYPES from current position.
    String tempTypeString = mRoundType.get(position);
    //Splits concatenated workout String into String Array.
    String[] tempWorkoutArray = tempWorkoutString.split(mContext.getString(R.string.bullet));
    //Splits concatenated round type String into String Array.
    String[] tempTypeArray = tempTypeString.split(" - ");

    //Var used to determine spannable spacing.
    int tempSpace = 0;
    //Bullet string. Cleared if on final round.
    String bullet = mContext.getString(R.string.bullet);
    //Iterates through the length of our split roundType array, which will always correspond to the length of our split workout array.
    for (int j = 0; j < tempTypeArray.length; j++) {
      //If we are on the last round object, clear our bullet String so the view does not end with one.
      if (j == tempTypeArray.length - 1) bullet = "";
      //If round is counting up, create a Spannable w/ the count-down time of the round. Otherwise, create a new Spannable w/ a placeholder for an ImageSpan.
      if (tempTypeArray[j].contains("1") || (tempTypeArray[j].contains("3"))) {
        span = new SpannableString(tempWorkoutArray[j] + bullet);
        //tempSpace is used as the "end" mark of our Spannable object manipulation. We set it to 2 spaces less than the span's length so we leave the bullet occupying the last places [space + bullet] alone). If on LAST spannable, use the full length so we do not fall short in coloring, since there is no space+bullet after.
        if (j != tempTypeArray.length - 1) tempSpace = span.length() - 2;
        else tempSpace = span.length();
      } else {
        //Uses slightly different spacing for first round entry.
        if (j != 0) {
          span = new SpannableString("   " + bullet);
          //Our ImageSpan is set (below) on indices 1 and 2, so we set tempSpace to 2 to cover its entirety (i.e. changing its color/size).
          tempSpace = 2;
        } else {
          span = new SpannableString("  " + bullet);
          tempSpace = 1;
        }

        //If roundType is 2 (sets), use green infinity drawable for ImageSpan. If roundType is 4 (breaks), use red.
        if (tempTypeArray[j].contains("2")) {
          imageSpan = setColorOnInfinityImageSpan(SET_COLOR, R.drawable.infinity_small_green);
        } else {
          imageSpan = setColorOnInfinityImageSpan(BREAK_COLOR, R.drawable.infinity_small_red);
        }
      }

      //If our roundType object contains a 1 or 2, it refers to a SET, and we set its corresponding workout object to green. Otherwise, it refers to a BREAK, and we set its color to red.
      if (tempTypeArray[j].contains("1") || tempTypeArray[j].contains("2")) {
        span.setSpan(new ForegroundColorSpan(SET_COLOR), 0, tempSpace, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
      } else
        span.setSpan(new ForegroundColorSpan(BREAK_COLOR), 0, tempSpace, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

      if (tempTypeArray[j].contains("2") || tempTypeArray[j].contains("4")) {
        //If using infinity drawable, increase its size.
        span.setSpan(new AbsoluteSizeSpan(18, true), 0, tempSpace, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        //Setting our Spannable, which can be concatenated w/ permSpan object in TextUtils below, to our imageSpan. We run from index 1-2 inclusive because 0 is used as an empty separator space (see: original Spannable span creation above).
        //Using different placement of image in spannable for first round, since we do not want it spaced out (i.e. indented).
        if (j != 0) {
          span.setSpan(imageSpan, 1, 2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
        else {
          span.setSpan(imageSpan, 0, 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
      }

      //If a cycle is active, change color of completed rounds to "greyed out" version of original color.
      if (mActiveCycle) {
        if (position==mPositionOfActiveCycle) {
          if (j<=mNumberOfRoundsCompleted-1) {
            span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.test_grey)), 0, tempSpace, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
          }
        }
      }
      //Within this loop, we update our permSpan charSequence with the new workout Spannable object.
      permSpan = TextUtils.concat(permSpan, span);
    }

    //Adds extra spacing between textView lines if second line has no infinity symbols. These symbols' imageViews create an automatic spacing greater than just text.
    boolean spacingChanged = false;
    if (tempTypeArray.length >= 9) {
      for (int k = 0; k < tempTypeArray.length; k++) {
        if (k >= 8) {
          if (!spacingChanged) {
            if (tempTypeArray[k].contains("2") || tempTypeArray[k].contains("4")) {
              k = tempTypeArray.length;
            } else {
              workoutHolder.workOutCycle.setLineSpacing(0, 1.3f);
              spacingChanged = true;
            }
          }
        }
      }
    }

    workoutHolder.workOutCycle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
    workoutHolder.workOutCycle.setText(permSpan);

    if (mHighlightDeleted) {
      //Clears highlight list.
      mPositionList.clear();
      //Turns our highlight mode off so single clicks launch a cycle instead of highlight it for deletion.
      mHighlightMode = false;
      //Sets all of our backgrounds to black (unhighlighted).
      workoutHolder.fullView.setBackgroundColor(Color.BLACK);
    }

    workoutHolder.fullView.setOnClickListener(v -> {
      boolean changed = false;
      //If not in highlight mode, launch our timer activity from cycle clicked on. Otherwise, clicking on any given cycle highlights it.
      if (!mHighlightMode) mOnCycleClickListener.onCycleClick(position);
      else {
        ArrayList<String> tempList = new ArrayList<>(mPositionList);
        //Iterate through every cycle in list.
        for (int i = 0; i < mWorkoutList.size(); i++) {
          //Using tempList for stable loop since mPositionList changes.
          for (int j = 0; j < tempList.size(); j++) {
            //If our cycle position matches a value in our "highlighted positions list", we un-highlight it, and remove it from our list.
            if (String.valueOf(position).contains(tempList.get(j))) {
              workoutHolder.fullView.setBackgroundColor(Color.BLACK);
              mPositionList.remove(String.valueOf(position));
              //Since we want a single highlight toggle per click, our boolean set to true will preclude the addition of a highlight below.
              changed = true;
            }
          }
        }
        //If we have not toggled our highlight off above, toggle it on below.
        if (!changed) {
          //Adds the position at its identical index for easy removal access.
          mPositionList.add(String.valueOf(position));
          workoutHolder.fullView.setBackgroundColor(Color.GRAY);
        }
        //Callback to send position list (Using Strings to make removing values easier) back to Main.
        mOnHighlightListener.onCycleHighlight(mPositionList, false);
      }
    });

    //Highlight cycle on long click and make visible action bar buttons. Sets mHighlightMode to true so no cycles can be launched in timer.
    workoutHolder.fullView.setOnLongClickListener(v -> {
      if (!mHighlightMode) {
        //Adds position of clicked item to position list.
        mPositionList.add(String.valueOf(position));
        //Sets background of list item to gray, to indicate it is selected.
        workoutHolder.fullView.setBackgroundColor(Color.GRAY);
        //Sets highlight mode to true, since at least one item is selected.
        mHighlightMode = true;
        //Calls back for initial highlighted position, Also to set actionBar views for highlight mode.
        mOnHighlightListener.onCycleHighlight(mPositionList, true);
      }
      return true;
    });
  }

  @Override
  public int getItemCount() {
    return mWorkoutList.size();
  }

  public class WorkoutHolder extends RecyclerView.ViewHolder {
    public TextView workoutName;
    public TextView workOutCycle;
    public View fullView;
    public TextView resumeCycle;
    public TextView resetCycle;

    @SuppressLint("ResourceAsColor")
    public WorkoutHolder(@NonNull View itemView) {
      super(itemView) ;
      workoutName = itemView.findViewById(R.id.custom_name_header);
      workOutCycle = itemView.findViewById(R.id.saved_custom_set_view);
      fullView = itemView;
      resumeCycle = itemView.findViewById(R.id.resume_active_cycle_button_for_mode_1);
      resetCycle = itemView.findViewById(R.id.reset_active_cycle_button_for_mode_1);
    }
  }

  public ArrayList<String> convertTime(ArrayList<String> time) {
    ArrayList<Long> newLong = new ArrayList<>();
    ArrayList<String> newString = new ArrayList<>();
    String listConv = "";
    String[] newSplit = null;
    String finalSplit = "";
    ArrayList<String> finalList = new ArrayList<>();

    for (int i=0; i<time.size(); i++) {
      //Getting each String entry from String Array.
      listConv = String.valueOf(time.get(i));
      //Splitting into String[] entries.
      newSplit = listConv.split(" - ", 0);

      for (int k=0; k<newSplit.length; k++) {
        //Creating new ArrayList of Long values.
        newLong.add(Long.parseLong(newSplit[k]));
        //Converting each Long value into a String we can display.
        newString.add(convertSeconds(newLong.get(k)));
        //If in Pom mode, set "0" for a time entry that is <10 minutes/4 length (e.g. X:XX), and "1" for >=10 minutes/5 length (e.g. XX:XX). This is so we can properly alternate green/red coloring in onBindView's Spannable.
      }
      finalSplit = String.valueOf(newString);
      finalSplit = finalSplit.replace("[", "");
      finalSplit = finalSplit.replace("]", "");
      finalSplit = finalSplit.replace(",", " " + mContext.getString(R.string.bullet));
      finalList.add(finalSplit);

      newLong = new ArrayList<>();
      newString = new ArrayList<>();
    }
    return finalList;
  }

  public String convertSeconds(long totalSeconds) {
    DecimalFormat df = new DecimalFormat("00");
    long minutes;
    long remainingSeconds;
    totalSeconds = totalSeconds/1000;

    if (totalSeconds >=60) {
      minutes = totalSeconds/60;
      remainingSeconds = totalSeconds % 60;
      return (minutes + ":" + df.format(remainingSeconds));
    } else if (totalSeconds >=10) return "0:" + totalSeconds;
    else return "0:0" + totalSeconds;
  }

  public ImageSpan setColorOnInfinityImageSpan(int color, int drawable) {
    ImageSpan imageSpan = new ImageSpan(mContext, drawable);
    Drawable tempDrawable = imageSpan.getDrawable();

    tempDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    imageSpan = new ImageSpan(tempDrawable);

    return imageSpan;
  }

}