package com.example.tragic.irate.simple.stopwatch.Adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tragic.irate.simple.stopwatch.R;
import com.example.tragic.irate.simple.stopwatch.SettingsFragments.ChangeSettingsValues;

import java.util.ArrayList;
import java.util.List;

public class PomDotsAdapter extends RecyclerView.Adapter {
    Context mContext;
    ChangeSettingsValues changeSettingsValues;
    GradientDrawable dotsBorder;

    List<String> mPomCycleRoundsAsStringsList;
    List<Integer> mCharactersInPomCyclesRoundsList;
    int mPomDotCounter;

    float mAlpha;
    float savedModeThreeAlpha;
    boolean mFadeUp;

    int WORK_COLOR;
    int MINI_BREAK_COLOR;
    int FULL_BREAK_COLOR;

    sendPomDotAlpha mSendPomDotAlpha;

    public PomDotsAdapter(Context context, List<String> pomCycleRoundsAsStringList) {
        this.mContext = context; this.mPomCycleRoundsAsStringsList = pomCycleRoundsAsStringList;
    }

    public void onPomAlphaSend(sendPomDotAlpha xSendPomDotAlpha) {
        this.mSendPomDotAlpha = xSendPomDotAlpha;
    }

    public void setPomDotAlpha(float alpha) {
        this.mAlpha = alpha;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dots_recycler_views, parent, false);

        return new PomDotsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
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

    public void saveModeThreeAlpha() {
        savedModeThreeAlpha = mAlpha;
    }

    public void setModeThreeAlpha() {
        mAlpha = savedModeThreeAlpha;
    }

    public void resetModeThreeAlpha() {
        savedModeThreeAlpha = 255;
    }

    public void changeColorSetting(int typeOFRound, int settingNumber) {
        if (typeOFRound==3) WORK_COLOR = changeSettingsValues.assignColor(settingNumber);
        if (typeOFRound==4) MINI_BREAK_COLOR = changeSettingsValues.assignColor(settingNumber);
        if (typeOFRound==5) FULL_BREAK_COLOR = changeSettingsValues.assignColor(settingNumber);
    }

    public interface sendPomDotAlpha {
        void sendPomAlphaValue(float alpha);
    }

    public class PomDotsViewHolder extends RecyclerView.ViewHolder {

        public PomDotsViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
