package com.example.tragic.irate.simple.stopwatch.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tragic.irate.simple.stopwatch.R;
import com.example.tragic.irate.simple.stopwatch.SettingsFragments.ChangeSettingsValues;

import java.util.ArrayList;
import java.util.List;

public class DotsAdapter extends RecyclerView.Adapter<DotsAdapter.DotsViewHolder> {
    Context mContext;
    ChangeSettingsValues changeSettingsValues;
    GradientDrawable dotsBorder;

    List<String> mCyclesRoundsAsStringsList;
    List<Integer> mRoundTypeList;
    List<Integer> mCharactersInCyclesRoundsList;

    int mCyclesRoundCount;
    int mCycleRoundsLeft;

    float mAlpha;
    float savedModeOneAlpha;
    boolean mFadeUp;

    int SET_COLOR;
    int BREAK_COLOR;

    sendDotAlpha mSendDotAlpha;

    Typeface narrowFont;
    Typeface narrowFontBold;
    Typeface bigShouldersFont;
    Typeface ignotum;
    Typeface sixCaps;

    public DotsAdapter(Context context, List<String> cycleRoundsAsStringList, List<Integer> typeOfRoundList) {
        this.mContext = context; this.mCyclesRoundsAsStringsList = cycleRoundsAsStringList; this.mRoundTypeList = typeOfRoundList;
        instantiateMiscObjects();
        instantiateLists();
    }

    public void onAlphaSend(sendDotAlpha xSendDotAlpha)  {
        this.mSendDotAlpha = xSendDotAlpha;
    }

    public void setDotAlpha(float alpha) {
        this.mAlpha = alpha;
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
    public int getItemCount() {
        return mCyclesRoundsAsStringsList.size();
    }

    //Todo: For Pom as well.
    @Override
    public void onBindViewHolder(@NonNull DotsViewHolder holder, int position) {
        holder.roundText.setText(trimTwoDigitString(mCyclesRoundsAsStringsList.get(position)));
        holder.roundText.setTextSize(textSizeForEachRound(mCharactersInCyclesRoundsList.get(position)));

        dotsBorder =  (GradientDrawable) ContextCompat.getDrawable(mContext, R.drawable.dots_border);

        if (mRoundTypeList.get(position) == 1) {
            dotsBorder.setColor(SET_COLOR);
            dotsBorder.setStroke(3, Color.WHITE);
        }

        if (mRoundTypeList.get(position) == 2) {
            dotsBorder.setColor(0);
            dotsBorder.setStroke(3, SET_COLOR);
        }

        if (mRoundTypeList.get(position) == 3) {
            dotsBorder.setColor(BREAK_COLOR);
            dotsBorder.setStroke(3, Color.WHITE);
        }

        if (mRoundTypeList.get(position) == 4) {
            dotsBorder.setColor(0);
            dotsBorder.setStroke(3, BREAK_COLOR);
        }
        holder.fullView.setBackground(dotsBorder);

        if (mCyclesRoundCount - mCycleRoundsLeft == position) {
            fadeAlpha();
            holder.fullView.setAlpha(mAlpha);
        } else if (mCycleRoundsLeft + position < mCyclesRoundCount) {
            holder.fullView.setAlpha(0.3f);
        } else {
            holder.fullView.setAlpha(1.0f);
        }
    }

    public interface sendDotAlpha {
        void sendAlphaValue(float alpha);
    }

    public class DotsViewHolder extends RecyclerView.ViewHolder {
        public View fullView;
        public TextView roundText;
        public ImageView roundImageView;

        public DotsViewHolder(@NonNull View itemView) {
            super(itemView);
            roundText = itemView.findViewById(R.id.round_string_textView);
            roundImageView = itemView.findViewById(R.id.round_string_imageView);
            fullView = itemView;
        }
    }

    private float textSizeForEachRound(int numberOfRoundChars) {
        int floatToReturn = 0;

        if (numberOfRoundChars == 1) {
            floatToReturn = 42;
        }
        if (numberOfRoundChars == 2) {
            floatToReturn = 34;
        }
        if (numberOfRoundChars == 4) {
            floatToReturn = 20;
        }
        if (numberOfRoundChars == 5) {
            floatToReturn = 16;
        }

        return floatToReturn;
    }

    private String trimTwoDigitString(String timeString) {
        String stringToReturn = timeString;

        if (timeString.length()==2 && timeString.substring(0, 1).equals("0")) {
            stringToReturn = timeString.substring(1);
        }
        Log.i("testSize", "timer substring is " + timeString.substring(0, 1));


        return stringToReturn;
    }

    public void setCycleRoundsAsStringsList(List<String> cyclesRoundsAsStringList) {
        this.mCyclesRoundsAsStringsList = cyclesRoundsAsStringList;
        Log.i("testRounds", "round list being updated as " + mCyclesRoundsAsStringsList);
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

    public void changeColorSetting(int typeOFRound, int settingNumber) {
        if (typeOFRound==1) SET_COLOR = changeSettingsValues.assignColor(settingNumber);
        if (typeOFRound==2) BREAK_COLOR = changeSettingsValues.assignColor(settingNumber);
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

    public void saveModeOneAlpha() {
        savedModeOneAlpha = mAlpha;
    }

    public void setModeOneAlpha() {
        mAlpha = savedModeOneAlpha;
    }

    public void resetModeOneAlpha() {
        savedModeOneAlpha = 255;
    }

    private void instantiateLists() {
        mCyclesRoundsAsStringsList = new ArrayList<>();
        mRoundTypeList = new ArrayList<>();
    }

    private void instantiateMiscObjects() {
        changeSettingsValues = new ChangeSettingsValues();

        dotsBorder =  (GradientDrawable) ContextCompat.getDrawable(mContext, R.drawable.dots_border);

//        narrowFont = ResourcesCompat.getFont(mContext, R.font.archivo_narrow);
//        narrowFontBold = ResourcesCompat.getFont(mContext, R.font.archivo_narrow_bold);
//        bigShouldersFont = ResourcesCompat.getFont(mContext, R.font.big_shoulders_text_bold);
//        ignotum = ResourcesCompat.getFont(mContext, R.font.ignotum);
//        sixCaps = ResourcesCompat.getFont(mContext, R.font.sixcaps);

//        testFont = ResourcesCompat.getFont(mContext, R.font.sixcaps);
    }
}