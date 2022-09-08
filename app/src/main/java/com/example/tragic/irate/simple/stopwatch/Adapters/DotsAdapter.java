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

import com.example.tragic.irate.simple.stopwatch.R;

import java.util.ArrayList;
import java.util.List;

public class DotsAdapter extends RecyclerView.Adapter<DotsAdapter.DotsViewHolder> {
    Context mContext;
    int mMode;

    List<String> mCyclesRoundsAsStringsList;
    List<Integer> mRoundTypeList;
    List<Integer> mCharactersInCyclesRoundsList;
    int mCyclesRoundCount;
    int mCycleRoundsLeft;

    List<String> mPomCycleRoundsAsStringsList;
    List<Integer> mCharactersInPomCyclesRoundsList;
    int mPomDotCounter;

    int mAlpha;
    int savedModeOneAlpha;
    int savedModeThreeAlpha;
    boolean mFadeUp;

    Typeface narrowFont;
    Typeface narrowFontBold;
    Typeface bigShouldersFont;
    Typeface ignotum;
    Typeface sixCaps;
    Typeface testFont;

    public DotsAdapter(Context context, List<String> roundList) {
        this.mContext = context; this.mCyclesRoundsAsStringsList = roundList;
        instantiateMiscObjects();
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

    public void setDotAlpha(int alpha) {
        this.mAlpha = alpha;
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

    @Override
    public void onBindViewHolder(@NonNull DotsViewHolder holder, int position) {
        holder.roundText.setText(mCyclesRoundsAsStringsList.get(position));
        holder.roundText.setTextSize(textSizeForEachRound(mCharactersInCyclesRoundsList.get(position)));

        if (mCharactersInCyclesRoundsList.get(position) >=4) {
            holder.roundText.setTypeface(bigShouldersFont);

//            holder.roundText.setTypeface(sixCaps);
//            holder.roundText.setTextSize(38);

//            holder.roundText.setTypeface(ignotum);
//            holder.roundText.setTextSize(28);
//            holder.roundText.setTypeface(Typeface.DEFAULT_BOLD);
        }
    }

    @Override
    public int getItemCount() {
        return mCyclesRoundsAsStringsList.size();
    }

    public class DotsViewHolder extends RecyclerView.ViewHolder {
        public TextView roundText;

        public DotsViewHolder(@NonNull View itemView) {
            super(itemView);
            roundText = itemView.findViewById(R.id.round_string_textView);
        }
    }

    private void instantiateMiscObjects() {
        narrowFont = ResourcesCompat.getFont(mContext, R.font.archivo_narrow);
        narrowFontBold = ResourcesCompat.getFont(mContext, R.font.archivo_narrow_bold);
        bigShouldersFont = ResourcesCompat.getFont(mContext, R.font.big_shoulders_text_bold);
        ignotum = ResourcesCompat.getFont(mContext, R.font.ignotum);
        sixCaps = ResourcesCompat.getFont(mContext, R.font.sixcaps);

//        testFont = ResourcesCompat.getFont(mContext, R.font.sixcaps);
    }
}