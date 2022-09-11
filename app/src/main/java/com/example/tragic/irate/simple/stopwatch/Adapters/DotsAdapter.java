package com.example.tragic.irate.simple.stopwatch.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
    GradientDrawable dotsBorder;

    Typeface narrowFont;
    Typeface narrowFontBold;
    Typeface bigShouldersFont;
    Typeface ignotum;
    Typeface sixCaps;
    Typeface testFont;

    sendDotAlpha mSendDotAlpha;

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

    //Todo: For Pom as well.
    @Override
    public void onBindViewHolder(@NonNull DotsViewHolder holder, int position) {
        holder.roundText.setText(trimTwoDigitString(mCyclesRoundsAsStringsList.get(position)));
        holder.roundText.setTextSize(textSizeForEachRound(mCharactersInCyclesRoundsList.get(position)));

//        Log.i("testRounds", "round list is " + mCyclesRoundsAsStringsList);

        if (mMode == 1) {
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

//            Log.i("testFade", "rounds left are " + mCycleRoundsLeft);
//            Log.i("testFade", "round count is " + mCyclesRoundCount);
        }
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

    public interface sendDotAlpha {
        void sendAlphaValue(float alpha);
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
    public int getItemCount() {
        return mCyclesRoundsAsStringsList.size();
    }

    public class DotsViewHolder extends RecyclerView.ViewHolder {
        public View fullView;
        public TextView roundText;
        public ImageView roundImageView;

        public DotsViewHolder(@NonNull View itemView) {
            super(itemView);
            roundText = itemView.findViewById(R.id.round_string_textView);
//            roundImageView = itemView.findViewById(R.id.round_string_imageView);
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

        dotsBorder =  (GradientDrawable) ContextCompat.getDrawable(mContext, R.drawable.dots_border);

//        narrowFont = ResourcesCompat.getFont(mContext, R.font.archivo_narrow);
//        narrowFontBold = ResourcesCompat.getFont(mContext, R.font.archivo_narrow_bold);
//        bigShouldersFont = ResourcesCompat.getFont(mContext, R.font.big_shoulders_text_bold);
//        ignotum = ResourcesCompat.getFont(mContext, R.font.ignotum);
//        sixCaps = ResourcesCompat.getFont(mContext, R.font.sixcaps);

//        testFont = ResourcesCompat.getFont(mContext, R.font.sixcaps);
    }

    //            holder.roundText.setTypeface(sixCaps);
//            holder.roundText.setTextSize(38);

//            holder.roundText.setTypeface(ignotum);
//            holder.roundText.setTextSize(28);
//            holder.roundText.setTypeface(Typeface.DEFAULT_BOLD);
//               holder.fullView.getBackground().setColorFilter(ContextCompat.getColor(mContext, SET_COLOR), PorterDuff.Mode.SRC_OVER);
}