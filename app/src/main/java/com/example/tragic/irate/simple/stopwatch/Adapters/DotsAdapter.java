package com.example.tragic.irate.simple.stopwatch.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tragic.irate.simple.stopwatch.Canvas.DotDraws;
import com.example.tragic.irate.simple.stopwatch.R;
import com.example.tragic.irate.simple.stopwatch.SettingsFragments.ChangeSettingsValues;

import java.util.ArrayList;
import java.util.List;

public class DotsAdapter extends RecyclerView.Adapter<DotsAdapter.DotsViewHolder> {
    Context mContext;
    int mMode;
    ChangeSettingsValues changeSettingsValues;

    List<String> mCyclesRoundsAsStringsList;
    List<Integer> mRoundTypeList;
    List<String> mPomCycleRoundsAsStringsList;
    List<Integer> mCharactersInCyclesRoundsList;
    List<Integer> mCharactersInPomCyclesRoundsList;

    int mCyclesRoundCount;
    int mCycleRoundsLeft;
    int mPomDotCounter;

    float mAlpha;
    float savedModeOneAlpha;
    float savedModeThreeAlpha;
    boolean mFadeUp;

    int SET_COLOR;
    int BREAK_COLOR;
    int WORK_COLOR;
    int MINI_BREAK_COLOR;
    int FULL_BREAK_COLOR;

    Typeface narrowFont;
    Typeface narrowFontBold;
    Typeface bigShouldersFont;
    Typeface ignotum;
    Typeface sixCaps;
    Typeface testFont;

    sendDotAlpha mSendDotAlpha;

    public void onAlphaSend(sendDotAlpha xSendDotAlpha)  {
        this.mSendDotAlpha = xSendDotAlpha;
    }

    public void setDotAlpha(float alpha) {
        this.mAlpha = alpha;
    }

    public DotsAdapter(Context context, List<String> cycleRoundsAsStringList, List<Integer> typeOfRoundList) {
        this.mContext = context; this.mCyclesRoundsAsStringsList = cycleRoundsAsStringList; this.mRoundTypeList = typeOfRoundList;
        instantiateMiscObjects();
        instantiateLists();
    }

    public void setCycleRoundsAsStringsList(List<String> cyclesRoundsAsStringList) {
        this.mCyclesRoundsAsStringsList = cyclesRoundsAsStringList;

        setCharactersInCyclesRoundsList();
    }

    public void setTypeOfRoundList(List<Integer> typeOfRoundsList) {
        this.mRoundTypeList = typeOfRoundsList;
    }

    public void updateCycleRoundCount(int roundCount, int roundsLeft) {
        this.mCyclesRoundCount = roundCount; this.mCycleRoundsLeft = roundsLeft;
    }

    private void setCharactersInCyclesRoundsList() {
        mCharactersInCyclesRoundsList = new ArrayList<>();

        for (int i=0; i<mCyclesRoundsAsStringsList.size(); i++) {
            mCharactersInCyclesRoundsList.add(mCyclesRoundsAsStringsList.get(i).length());
        }
    }

    public void setPomCycleRoundsAsStringsList(List<String> pomCyclesRoundsAsStringsList) {
        this.mPomCycleRoundsAsStringsList = pomCyclesRoundsAsStringsList;
    }

    public void updatePomDotCounter(int pomDotCounter) {
        this.mPomDotCounter = pomDotCounter;
    }

    private void setCharactersInPomCyclesRoundsList() {
        mCharactersInPomCyclesRoundsList = new ArrayList<>();

        for (int i=0; i<mPomCycleRoundsAsStringsList.size(); i++) {
            mCharactersInPomCyclesRoundsList.add(mPomCycleRoundsAsStringsList.get(i).length());
        }
    }

    private float textSizeForEachRound(int numberOfRoundChars) {
        return 40 - (5* (numberOfRoundChars-1));
    }

    public void setMode(int mode) {
        this.mMode = mode;
    }

    public void changeColorSetting(int typeOFRound, int settingNumber) {
        if (typeOFRound==1) SET_COLOR = changeSettingsValues.assignColor(settingNumber);
        if (typeOFRound==2) BREAK_COLOR = changeSettingsValues.assignColor(settingNumber);
        if (typeOFRound==3) WORK_COLOR = changeSettingsValues.assignColor(settingNumber);
        if (typeOFRound==4) MINI_BREAK_COLOR = changeSettingsValues.assignColor(settingNumber);
        if (typeOFRound==5) FULL_BREAK_COLOR = changeSettingsValues.assignColor(settingNumber);
    }

    //Todo: For Pom as well.
    @Override
    public void onBindViewHolder(@NonNull DotsViewHolder holder, int position) {
        holder.roundText.setText(mCyclesRoundsAsStringsList.get(position));
        holder.roundText.setTextSize(textSizeForEachRound(mCharactersInCyclesRoundsList.get(position)));

        if (mCharactersInCyclesRoundsList.get(position) >=4) {
            holder.roundText.setTypeface(bigShouldersFont);
        }

//        Log.i("testDots", "updating at position " + position);

        if (mMode == 1) {
            if (mRoundTypeList.get(position) == 1 || mRoundTypeList.get(position) == 2) {
                holder.fullView.setBackgroundColor(SET_COLOR);
                Log.i("testDots", "set color is " + SET_COLOR + " at position " + position);
            }

            if (mRoundTypeList.get(position) == 3 || mRoundTypeList.get(position) == 4) {
                holder.fullView.setBackgroundColor(BREAK_COLOR);
            }

            if (mRoundTypeList.get(position) == 1 || mRoundTypeList.get(position) == 3) {
//                holder.fullView.setBackgroundColor(0);
            }

            if (mRoundTypeList.get(position) == 2 || mRoundTypeList.get(position) == 4) {
                holder.fullView.setBackgroundColor(0);
            }

            if (mCyclesRoundCount - mCycleRoundsLeft == position) {
                fadeAlpha();
                holder.fullView.setAlpha(mAlpha);
            } else if (mCycleRoundsLeft + position < mCyclesRoundCount) {
                holder.fullView.setAlpha(0.3f);
            } else {
                holder.fullView.setAlpha(1.0f);
            }
        }
    }

    public void saveModeOneAlpha() {
        savedModeOneAlpha = mAlpha;
    }

    public void saveModeThreeAlpha() {
        savedModeThreeAlpha = mAlpha;
    }

    public void setModeOneAlpha() {
        mAlpha = savedModeOneAlpha;
    }

    public void setModeThreeAlpha() {
        mAlpha = savedModeThreeAlpha;
    }

    public void resetModeOneAlpha() {
        savedModeOneAlpha = 255;
    }

    public void resetModeThreeAlpha() {
        savedModeThreeAlpha = 255;
    }

    @NonNull
    @Override
    public DotsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dots_recycler_views, parent, false);

        return new DotsViewHolder(view);
    }

    private void fadeAlpha() {
        mSendDotAlpha.sendAlphaValue(mAlpha);

        if (mAlpha >=1.0f) {
            mAlpha = 1.0f;
            mFadeUp = false;
        }

        if (mAlpha>0.25f && !mFadeUp) {
            mAlpha -= 0.05;
        } else {
            mFadeUp = true;
        }

        if (mFadeUp) {
            mAlpha += 0.05;
        }
    }

    @Override
    public int getItemCount() {
//        Log.i("testDots", "string list size is " + mCyclesRoundsAsStringsList.size());
        return mCyclesRoundsAsStringsList.size();
    }

    private void fadeDot(View view) {
        view.setAlpha(mAlpha);

        mSendDotAlpha.sendAlphaValue(mAlpha);

        if (mAlpha >=255) {
            mAlpha = 255;
            mFadeUp = false;
        }
        if (mAlpha>90 && !mFadeUp){
            mAlpha -=15;
        } else {
            mFadeUp = true;
        }
        if (mFadeUp) {
            mAlpha +=15;
        }
    }

    public interface sendDotAlpha {
        void sendAlphaValue(float alpha);
    }

    public class DotsViewHolder extends RecyclerView.ViewHolder {
        public View fullView;
        public TextView roundText;

        public DotsViewHolder(@NonNull View itemView) {
            super(itemView);
            roundText = itemView.findViewById(R.id.round_string_textView);
            fullView = itemView;
        }
    }

    private void instantiateLists() {
        mCyclesRoundsAsStringsList = new ArrayList<>();
        mRoundTypeList = new ArrayList<>();
        mPomCycleRoundsAsStringsList  = new ArrayList<>();
    }

    private void instantiateMiscObjects() {
        changeSettingsValues = new ChangeSettingsValues();

        narrowFont = ResourcesCompat.getFont(mContext, R.font.archivo_narrow);
        narrowFontBold = ResourcesCompat.getFont(mContext, R.font.archivo_narrow_bold);
        bigShouldersFont = ResourcesCompat.getFont(mContext, R.font.big_shoulders_text_bold);
        ignotum = ResourcesCompat.getFont(mContext, R.font.ignotum);
        sixCaps = ResourcesCompat.getFont(mContext, R.font.sixcaps);

//        testFont = ResourcesCompat.getFont(mContext, R.font.sixcaps);
    }

    //            holder.roundText.setTypeface(sixCaps);
//            holder.roundText.setTextSize(38);

//            holder.roundText.setTypeface(ignotum);
//            holder.roundText.setTextSize(28);
//            holder.roundText.setTypeface(Typeface.DEFAULT_BOLD);
}