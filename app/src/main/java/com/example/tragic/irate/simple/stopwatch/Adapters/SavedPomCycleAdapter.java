package com.example.tragic.irate.simple.stopwatch.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tragic.irate.simple.stopwatch.SettingsFragments.ChangeSettingsValues;
import com.example.tragic.irate.simple.stopwatch.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SavedPomCycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    ArrayList<String> mPomList;
    ArrayList<String> mPomTitle;

    onPauseOrResumeListener mOnPauseOrResumeListener;
    onCycleClickListener mOnCycleClickListener;
    onHighlightListener mOnHighlightListener;
    onResumeOrResetCycle mOnResumeOrResetCycle;

    Spannable pomSpan;
    CharSequence permSpan;
    boolean mHighlightDeleted;
    boolean mHighlightMode;

    List<Integer> mHighlightPositionList;
    int RESUMING_CYCLE_FROM_TIMER = 1;
    int RESETTING_CYCLE_FROM_TIMER = 2;

    boolean mTimerPaused;
    boolean mActiveCycle;
    boolean isConfirmStringVisible;
    int mPositionOfActiveCycle;
    int mNumberOfRoundsCompleted;

    ChangeSettingsValues changeSettingsValues;
    int WORK_COLOR;
    int BREAK_COLOR;
    int REST_COLOR;

    int fullViewBackGroundColor;
    int completedRoundColor;
    int highlightColor;
    boolean mRowClickingIsDisabled;

    public interface onPauseOrResumeListener {
        void onPauseOrResume(boolean timerIsPaused);
    }

    public void setTimerPauseOrResume(onPauseOrResumeListener xOnPauseOrResumeListener) {
        this.mOnPauseOrResumeListener = xOnPauseOrResumeListener;
    }

    public interface onCycleClickListener {
        void onCycleClick (int position);
    }

    public interface onHighlightListener {
        void onCycleHighlight (List<Integer> listOfPositions, boolean addButtons);
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

    public SavedPomCycleAdapter(Context context, ArrayList<String> pomList, ArrayList<String> pomTitle) {
        this.mContext = context; this.mPomList = pomList; this.mPomTitle = pomTitle;
        //Must be instantiated here so it does not loop and reset in onBindView.
        mHighlightPositionList = new ArrayList<>();

        changeSettingsValues = new ChangeSettingsValues(mContext);

        fullViewBackGroundColor = ContextCompat.getColor(mContext, R.color.night_shadow);
        highlightColor = ContextCompat.getColor(mContext, R.color.mid_grey);
        completedRoundColor = ContextCompat.getColor(mContext, R.color.onyx);
    }

    public void setTimerIsPaused(boolean paused) {
        this.mTimerPaused = paused;
    }

    public void exitHighlightMode() {
        mHighlightDeleted = true;
        mHighlightMode = false;
    }

    public void setCycleAsActive() {
        mActiveCycle = true;
    }

    public void removeCycleAsActive() {
        mActiveCycle = false;
    }

    public void setActiveCyclePosition(int position) {
        this.mPositionOfActiveCycle = position;

    }

    public void setNumberOfRoundsCompleted(int number) {
        this.mNumberOfRoundsCompleted = number;
    }

    public void setColorSettingsFromMainActivity(int typeOFRound, int settingNumber) {
        if (typeOFRound==3) WORK_COLOR = changeSettingsValues.assignColor(settingNumber);
        if (typeOFRound==4) BREAK_COLOR = changeSettingsValues.assignColor(settingNumber);
        if (typeOFRound==5) REST_COLOR = changeSettingsValues.assignColor(settingNumber);
    }

    public boolean isCycleActive() {
        return mActiveCycle;
    }

    public boolean isHighlightModeActive() {
        return mHighlightMode;
    }

    public void disableRowClicking() {
        this.mRowClickingIsDisabled = true;
    }

    public void enableRowClicking() {
        this.mRowClickingIsDisabled = false;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.pomodoro_cycles_recycler, parent, false);
        return new PomHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PomHolder pomHolder = (PomHolder) holder;

        if (mRowClickingIsDisabled) {
            pomHolder.fullView.setEnabled(false);
        } else {
            pomHolder.fullView.setEnabled(true);
        }

        pomHolder.pauseOrResume.setVisibility(View.GONE);
        pomHolder.resetCycle.setVisibility(View.GONE);

        if (mHighlightDeleted) {
            mHighlightPositionList.clear();
            mHighlightMode = false;
        }

        pomHolder.pomName.setText(mPomTitle.get(position));

        String bullet = mContext.getString(R.string.bullet);

        String tempPomString = (convertTime(mPomList).get(position));
        String[] tempPomStringArray = tempPomString.split(mContext.getString(R.string.bullet));

        int tempSpace = 0;
        permSpan = "";

        for (int i = 0; i<tempPomStringArray.length; i++) {
            if (i == tempPomStringArray.length - 1) bullet = "";

            pomSpan = new SpannableString(tempPomStringArray[i] + bullet);

            if (i!=0) {
                tempSpace = 2;
            } else {
                tempSpace = 1;
            }

            if (i != tempPomStringArray.length - 1) {
                tempSpace = pomSpan.length() - 2;
            }
            else {
                tempSpace = pomSpan.length();
            }

            if (i!=7) {
                if (i%2==0) {
                    pomSpan.setSpan(new ForegroundColorSpan(WORK_COLOR), 0, tempSpace, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                } else {
                    pomSpan.setSpan(new ForegroundColorSpan(BREAK_COLOR), 0, tempSpace, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                }
            } else {
                pomSpan.setSpan(new ForegroundColorSpan(REST_COLOR), 0, tempSpace, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }

            if (mActiveCycle) {
                if (position == mPositionOfActiveCycle) {
                    if (i <= mNumberOfRoundsCompleted - 1) {
                        pomSpan.setSpan(new ForegroundColorSpan(completedRoundColor), 0, tempSpace, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    }

                    if (i == mNumberOfRoundsCompleted) {
                        pomSpan.setSpan(new StyleSpan(Typeface.ITALIC), 0, tempSpace, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    }
                }
            }
            permSpan = TextUtils.concat(permSpan, pomSpan);
            pomHolder.pomView.setText(permSpan);
        }

        pomHolder.fullView.setOnClickListener(v-> {
            boolean changed = false;
            if (!mHighlightMode) {
                if (mActiveCycle) {
                    if (position == mPositionOfActiveCycle) {
                        mOnResumeOrResetCycle.ResumeOrResetCycle(RESUMING_CYCLE_FROM_TIMER);
                    }
                } else {
                    mOnCycleClickListener.onCycleClick(position);
                }
            } else {
                ArrayList<Integer> tempList = new ArrayList<>(mHighlightPositionList);
                //Iterate through every cycle in list.
                for (int i=0; i<mPomList.size(); i++) {
                    //Using tempList for stable loop since mHighlightPositionList changes.
                    for (int j=0; j<tempList.size(); j++) {
                        if (position==tempList.get(j)) {
                            pomHolder.fullView.setBackgroundColor(fullViewBackGroundColor);
                            mHighlightPositionList.remove(Integer.valueOf(position));
                            //Since we want a single highlight toggle per click, our boolean set to true will preclude the addition of a highlight below.
                            changed = true;
                        }
                    }
                }
                //If we have not toggled our highlight off above, toggle it on below.
                if (!changed) {
                    //Adds the position at its identical index for easy removal access.
                    mHighlightPositionList.add(position);
                    pomHolder.fullView.setBackgroundColor(highlightColor);

                }
                //Callback to send position list (Using Strings to make removing values easier) back to Main.
                mOnHighlightListener.onCycleHighlight(mHighlightPositionList, false);
            }
        });

        pomHolder.fullView.setOnLongClickListener(v-> {
            if (!mHighlightMode && !mActiveCycle) {
                mHighlightPositionList.add(position);
                pomHolder.fullView.setBackgroundColor(highlightColor);
                mHighlightMode = true;
                //Calls back for initial highlighted position, Also to set actionBar views for highlight mode.
                mOnHighlightListener.onCycleHighlight(mHighlightPositionList, true);
            }
            return true;
        });

        if (mActiveCycle) {
            if (!isConfirmStringVisible) {
                pomHolder.resetCycle.setText(R.string.reset);
            }

            if (position==mPositionOfActiveCycle) {
                if (mTimerPaused) {
                    pomHolder.pauseOrResume.setText(R.string.resume);
                } else {
                    pomHolder.pauseOrResume.setText(R.string.pause);
                }

                pomHolder.pauseOrResume.setVisibility(View.VISIBLE);
                pomHolder.resetCycle.setVisibility(View.VISIBLE);

                pomHolder.pauseOrResume.setOnClickListener(v-> {
                    mOnPauseOrResumeListener.onPauseOrResume(mTimerPaused);
                });

                pomHolder.resetCycle.setOnClickListener(v-> {
                    if (pomHolder.resetCycle.getText().equals(mContext.getString(R.string.reset))) {
                        pomHolder.resetCycle.setText(R.string.confirm_cycle_reset);
                        isConfirmStringVisible = true;
                    } else {
                        mOnResumeOrResetCycle.ResumeOrResetCycle(RESETTING_CYCLE_FROM_TIMER);
                    }
                });

                pomHolder.fullView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.cycle_row_edit_border));
            } else {
                pomHolder.fullView.setBackgroundColor(fullViewBackGroundColor);
            }
        } else {
            pomHolder.fullView.setBackgroundColor(fullViewBackGroundColor);
        }
    }

    public void setIsConfirmStringVisible(boolean isVisible) {
        this.isConfirmStringVisible = isVisible;
    }

    @Override
    public int getItemCount() {
        return mPomList.size();
    }

    public class PomHolder extends RecyclerView.ViewHolder {
        public TextView pomName;
        public TextView pomView;
        public View fullView;
        public TextView pauseOrResume;
        public TextView resetCycle;

        public PomHolder(@NonNull View itemView) {
            super(itemView);
            pomName = itemView.findViewById(R.id.pom_header);
            pomView = itemView.findViewById(R.id.pom_view);
            pauseOrResume = itemView.findViewById(R.id.pause_or_resume_cycle_button_for_mode_3);
            fullView = itemView;
            resetCycle = itemView.findViewById(R.id.reset_active_cycle_button_for_mode_3);
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
}
