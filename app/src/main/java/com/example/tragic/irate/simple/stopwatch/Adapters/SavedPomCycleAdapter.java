package com.example.tragic.irate.simple.stopwatch.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
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

    onCycleClickListener mOnCycleClickListener;
    onHighlightListener mOnHighlightListener;
    onResumeOrResetCycle mOnResumeOrResetCycle;

    Spannable pomSpan;
    boolean mHighlightDeleted;
    boolean mHighlightMode;

    List<Integer> mHighlightPositionList;
    ArrayList<Integer> mSizeToggle = new ArrayList<>();
    int RESUMING_CYCLE_FROM_TIMER = 1;
    int RESETTING_CYCLE_FROM_TIMER = 2;

    boolean mActiveCycle;
    int mPositionOfActiveCycle;
    int mNumberOfRoundsCompleted;

    ChangeSettingsValues changeSettingsValues;
    int WORK_COLOR;
    int BREAK_COLOR;
    int REST_COLOR;

    int fullViewBackGroundColor;

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
        //Populates a toggle list for Pom's spannable colors so we can simply replace them at will w/ out resetting the list. This should only be called in our initial adapter instantiation.
        if (mSizeToggle.size()==0) for (int i=0; i<8; i++) mSizeToggle.add(0);
        //Must be instantiated here so it does not loop and reset in onBindView.
        mHighlightPositionList = new ArrayList<>();

        changeSettingsValues = new ChangeSettingsValues(mContext);

        fullViewBackGroundColor = ContextCompat.getColor(mContext, R.color.night_shadow);
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
        pomHolder.resetCycle.setVisibility(View.GONE);

        if (mActiveCycle) {
            if (position==mPositionOfActiveCycle) {
                pomHolder.resetCycle.setVisibility(View.VISIBLE);

                pomHolder.resetCycle.setOnClickListener(v-> {
                    mOnResumeOrResetCycle.ResumeOrResetCycle(RESETTING_CYCLE_FROM_TIMER);
                });

                pomHolder.fullView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.cycle_row_edit_border));
            } else {
                pomHolder.fullView.setBackgroundColor(fullViewBackGroundColor);
            }
        } else {
            pomHolder.fullView.setBackgroundColor(fullViewBackGroundColor);
        }

        if (mHighlightDeleted) {
            //Clears highlight list.
            mHighlightPositionList.clear();
            //Turns our highlight mode off so single clicks launch a cycle instead of highlight it for deletion.
            mHighlightMode = false;

//            pomHolder.fullView.setBackgroundColor(Color.BLACK);
        }

        pomHolder.pomName.setText(mPomTitle.get(position));
        String tempPom = (convertTime(mPomList).get(position));

        tempPom = tempPom.replace("-", mContext.getString(R.string.bullet));
        tempPom = tempPom + "     ";
        pomSpan = new SpannableString(tempPom);

        mSizeToggle = retrievedRoundSizeFromConcatenatedString(tempPom, " " + mContext.getString(R.string.bullet) + " ");

        int moving = 0;
        int rangeStart = 0;
        int rangeEnd = 4;

        for (int i=0; i<8; i++) {
            if (mSizeToggle.get(i)==5) rangeEnd = 5;
            if (moving+rangeEnd<=pomSpan.length()) {
                if (i!=7) {
                    if (i%2==0) {
                        pomSpan.setSpan(new ForegroundColorSpan(WORK_COLOR), moving + rangeStart, moving + rangeEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    }
                    else {
                        pomSpan.setSpan(new ForegroundColorSpan(BREAK_COLOR), moving + rangeStart, moving + rangeEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    }
                } else {
                    pomSpan.setSpan(new ForegroundColorSpan(REST_COLOR), moving + rangeStart, moving + rangeEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                }
            }
            if (mActiveCycle) {
                if (position==mPositionOfActiveCycle) {
                    if (i<=mNumberOfRoundsCompleted-1) {
                        pomSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.test_grey)), moving + rangeStart, moving + rangeEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    }
                }
            }
            if (i!=7) if (mSizeToggle.get(i)==4) moving+=7; else moving+=8;
        }
        pomHolder.pomView.setText(pomSpan);

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
                pomHolder.fullView.setBackgroundColor(Color.BLACK);
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
                    pomHolder.fullView.setBackgroundColor(Color.GRAY);

                }
                //Callback to send position list (Using Strings to make removing values easier) back to Main.
                mOnHighlightListener.onCycleHighlight(mHighlightPositionList, false);
            }
        });

        pomHolder.fullView.setOnLongClickListener(v-> {
            if (!mHighlightMode && !mActiveCycle) {
                mHighlightPositionList.add(position);
                pomHolder.fullView.setBackgroundColor(Color.GRAY);
                mHighlightMode = true;
                //Calls back for initial highlighted position, Also to set actionBar views for highlight mode.
                mOnHighlightListener.onCycleHighlight(mHighlightPositionList, true);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return mPomList.size();
    }

    public class PomHolder extends RecyclerView.ViewHolder {
        public TextView pomName;
        public TextView pomView;
        public View fullView;
        public TextView resetCycle;

        public PomHolder(@NonNull View itemView) {
            super(itemView);
            pomName = itemView.findViewById(R.id.pom_header);
            pomView = itemView.findViewById(R.id.pom_view);
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

    public ArrayList<Integer> retrievedRoundSizeFromConcatenatedString(String stringToSeparate, String charToSplit) {
        ArrayList<Integer> arrayToPopulate = new ArrayList<>();

        String[] pulledValue = stringToSeparate.split(charToSplit);
        for (int i=0; i<pulledValue.length; i++) {
            arrayToPopulate.add(pulledValue[i].length());
        }

        return arrayToPopulate;
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
