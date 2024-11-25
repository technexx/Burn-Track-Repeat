package burn.track.repeat.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import burn.track.repeat.R;
import burn.track.repeat.SettingsFragments.ChangeSettingsValues;

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

    int mScreenHeight;

    Typeface narrowFont;
    Typeface narrowFontBold;

    int mThemeMode;
    int DAY_MODE = 0;
    int NIGHT_MODE = 1;

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

    public void setScreenHeight(int height) {
        this.mScreenHeight = height;
    }

    public void setDayOrNightMode(int themeMode) {
        this.mThemeMode = themeMode;
    }

    @NonNull
    @Override
    public DotsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view;

        if (mScreenHeight<=1920) {
            view = inflater.inflate(R.layout.dots_recycler_views_h1920, parent, false);
        } else {
            view = inflater.inflate(R.layout.dots_recycler_views, parent, false);
        }

        return new DotsViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mCyclesRoundsAsStringsList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull DotsViewHolder holder, int position) {
        holder.roundText.setText(trimTwoDigitString(mCyclesRoundsAsStringsList.get(position)));

        holder.roundText.setTextSize(textSizeForEachRound(mCharactersInCyclesRoundsList.get(position)));

        GridLayoutManager.LayoutParams fullViewLayoutParams = (GridLayoutManager.LayoutParams) holder.fullView.getLayoutParams();
        ConstraintLayout.LayoutParams textLayoutParams = (ConstraintLayout.LayoutParams) holder.roundText.getLayoutParams();

        dotsBorder =  (GradientDrawable) ContextCompat.getDrawable(mContext, R.drawable.dots_border);
        holder.fullView.setBackground(dotsBorder);

        if (mCharactersInCyclesRoundsList.get(position) == 5) {
            holder.roundText.setTypeface(narrowFont);
            textLayoutParams.topMargin = 6;
        } else {
            holder.roundText.setTypeface(Typeface.DEFAULT);
            textLayoutParams.topMargin = 0;
        }

        if (mRoundTypeList.get(position) == 1 || mRoundTypeList.get(position) == 3) {
            holder.roundText.setTextColor(Color.BLACK);
        } else {
            holder.roundText.setTextColor(dotTextColorForInfinityRounds(mThemeMode));
        }

        if (mScreenHeight <= 1920) {
            if (mCyclesRoundsAsStringsList.size()<=8) {
                fullViewLayoutParams.topMargin = dpConv(14);
            } else {
                fullViewLayoutParams.topMargin = dpConv(10);
            }
        } else {
            if (mCyclesRoundsAsStringsList.size()<=8) {
                fullViewLayoutParams.topMargin = dpConv(18);
            } else {
                fullViewLayoutParams.topMargin = dpConv(8);
            }
        }

        if (mRoundTypeList.get(position) == 1) {
            dotsBorder.setColor(SET_COLOR);
            dotsBorder.setStroke(3, dotColorStrokeForNonInfinityRounds(mThemeMode));
        }

        if (mRoundTypeList.get(position) == 2) {
            dotsBorder.setColor(0);
            dotsBorder.setStroke(3, dotColorStrokeForSetInfinityRounds(mThemeMode));
        }

        if (mRoundTypeList.get(position) == 3) {
            dotsBorder.setColor(BREAK_COLOR);
            dotsBorder.setStroke(3, dotColorStrokeForNonInfinityRounds(mThemeMode));
        }

        if (mRoundTypeList.get(position) == 4) {
            dotsBorder.setColor(0);
            dotsBorder.setStroke(3, dotColorStrokeForBreakInfinityRounds(mThemeMode));
        }

        if (mCyclesRoundCount - mCycleRoundsLeft == position) {
            fadeAlpha();
            holder.fullView.setAlpha(mAlpha);
        } else if (mCycleRoundsLeft + position < mCyclesRoundCount) {
            holder.fullView.setAlpha(0.36f);
        } else {
            holder.fullView.setAlpha(1.0f);
        }
    }

    private int dotTextColorForInfinityRounds(int theme) {
        int colorToReturn = 0;

        if (theme == DAY_MODE) {
            colorToReturn = Color.BLACK;
        }
        if (theme == NIGHT_MODE) {
            colorToReturn = Color.WHITE;
        }

        return colorToReturn;
    }

    private int dotColorStrokeForNonInfinityRounds(int theme) {
        int colorToReturn = 0;

        if (theme == DAY_MODE) {
            colorToReturn = Color.BLACK;
        }
        if (theme == NIGHT_MODE) {
            colorToReturn = Color.WHITE;
        }

        return colorToReturn;
    }

    private int dotColorStrokeForSetInfinityRounds(int theme) {
        int colorToReturn = 0;

        if (theme == DAY_MODE) {
            colorToReturn = SET_COLOR;
        }
        if (theme == NIGHT_MODE) {
            colorToReturn = SET_COLOR;
        }

        return colorToReturn;
    }

    private int dotColorStrokeForBreakInfinityRounds(int theme) {
        int colorToReturn = 0;

        if (theme == DAY_MODE) {
            colorToReturn = BREAK_COLOR;
        }
        if (theme == NIGHT_MODE) {
            colorToReturn = BREAK_COLOR;
        }

        return colorToReturn;
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
            roundText = itemView.findViewById(R.id.round_string_textView_for_cycles);
            fullView = itemView;
        }
    }

    private float textSizeForEachRound(int numberOfRoundChars) {
        int floatToReturn = 0;
        if (numberOfRoundChars == 1) {
            if (mScreenHeight <= 1920) {
                floatToReturn = 28;
            } else {
                floatToReturn = 34;
            }
        }
        if (numberOfRoundChars == 2) {
            if (mScreenHeight <= 1920) {
                floatToReturn = 26;
            } else {
                floatToReturn = 32;
            }
        }
        if (numberOfRoundChars == 4) {
            floatToReturn = 21;
        }
        if (numberOfRoundChars == 5) {
            floatToReturn = 19;
        }

        return floatToReturn;
    }

    private String trimTwoDigitString(String timeString) {
        String stringToReturn = timeString;

        if (timeString.length()==2 && timeString.substring(0, 1).equals("0")) {
            stringToReturn = timeString.substring(1);
        }

        return stringToReturn;
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

    public void setColorSettingsFromMainActivity(int typeOFRound, int settingNumber) {
        if (typeOFRound==1) SET_COLOR = changeSettingsValues.assignColor(settingNumber);
        if (typeOFRound==2) BREAK_COLOR = changeSettingsValues.assignColor(settingNumber);
    }

    private void fadeAlpha() {
        mSendDotAlpha.sendAlphaValue(mAlpha);

        if (mAlpha >=1.0f) {
            mAlpha = 1.0f;
            mFadeUp = false;
        }

        if (mAlpha>0.3f && !mFadeUp) {
            mAlpha -= 0.08;
        } else {
            mFadeUp = true;
        }

        if (mFadeUp) {
            mAlpha += 0.08;
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

    private int dpConv(float pixels) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixels, mContext.getResources().getDisplayMetrics());
    }

    private void instantiateLists() {
        mCyclesRoundsAsStringsList = new ArrayList<>();
        mRoundTypeList = new ArrayList<>();
    }

    private void instantiateMiscObjects() {
        changeSettingsValues = new ChangeSettingsValues(mContext);
        dotsBorder =  (GradientDrawable) ContextCompat.getDrawable(mContext, R.drawable.dots_border);

        narrowFont = ResourcesCompat.getFont(mContext, R.font.archivo_narrow);
        narrowFontBold = ResourcesCompat.getFont(mContext, R.font.archivo_narrow_bold);
    }
}