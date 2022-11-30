package burn.track.repeat.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;


import burn.track.repeat.R;
import burn.track.repeat.SettingsFragments.ChangeSettingsValues;

import java.util.ArrayList;
import java.util.List;

public class PomDotsAdapter extends RecyclerView.Adapter<PomDotsAdapter.PomDotsViewHolder> {
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

    int mScreenHeight;

    Typeface narrowFont;
    Typeface narrowFontBold;

    int mThemeMode;
    int DAY_MODE = 0;
    int NIGHT_MODE = 1;

    public PomDotsAdapter(Context context, List<String> pomCycleRoundsAsStringList) {
        this.mContext = context; this.mPomCycleRoundsAsStringsList = pomCycleRoundsAsStringList;
        instantiateLists();
        instantiateMiscObjects();
    }

    public void onPomAlphaSend(sendPomDotAlpha xSendPomDotAlpha) {
        this.mSendPomDotAlpha = xSendPomDotAlpha;
    }

    public void setPomDotAlpha(float alpha) {
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
    public PomDotsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view;

        if (mScreenHeight<=1920) {
            view = inflater.inflate(R.layout.pom_dots_recycler_views_h1920, parent, false);
        } else {
            view = inflater.inflate(R.layout.pom_dots_recycler_views, parent, false);
        }
        return new PomDotsViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mPomCycleRoundsAsStringsList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull PomDotsViewHolder holder, int position) {
        holder.roundText.setText(trimTwoDigitString(mPomCycleRoundsAsStringsList.get(position)));
        holder.roundText.setTextSize(textSizeForEachRound(mCharactersInPomCyclesRoundsList.get(position)));

        dotsBorder =  (GradientDrawable) ContextCompat.getDrawable(mContext, R.drawable.dots_border);

        if (mCharactersInPomCyclesRoundsList.get(position) == 5) {
            holder.roundText.setTypeface(narrowFont);
        } else {
            holder.roundText.setTypeface(Typeface.DEFAULT);
        }

        holder.fullView.setBackground(dotsBorder);

        switch (position) {
            case 0: case 2: case 4: case 6:
                dotsBorder.setColor(WORK_COLOR);
                break;
            case 1: case 3: case 5:
                dotsBorder.setColor(MINI_BREAK_COLOR);
                break;
            case 7:
                dotsBorder.setColor(FULL_BREAK_COLOR);
                break;
        }

        dotsBorder.setStroke(3, pomDotsStrokeColor(mThemeMode));

        if (mPomDotCounter == position) {
            fadeAlpha();
            holder.fullView.setAlpha(mAlpha);
        } else if (position < mPomDotCounter) {
            holder.fullView.setAlpha(0.3f);
        } else {
            holder.fullView.setAlpha(1.0f);
        }
    }

    private int pomDotsStrokeColor(int theme) {
        int colorToReturn = 0;

        if (theme == DAY_MODE) {
            colorToReturn = Color.BLACK;
        }
        if (theme == NIGHT_MODE) {
            colorToReturn = Color.WHITE;
        }

        return colorToReturn;
    }

    private float textSizeForEachRound(int numberOfRoundChars) {
        int floatToReturn = 21;

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

    public void setPomCycleRoundsAsStringsList(List<String> pomCyclesRoundsAsStringsList) {
        this.mPomCycleRoundsAsStringsList = pomCyclesRoundsAsStringsList;
        setCharactersInPomCyclesRoundsList();
    }

    private void setCharactersInPomCyclesRoundsList() {
        mCharactersInPomCyclesRoundsList = new ArrayList<>();

        for (int i=0; i<mPomCycleRoundsAsStringsList.size(); i++) {
            mCharactersInPomCyclesRoundsList.add(mPomCycleRoundsAsStringsList.get(i).length());
        }
    }

    public void updatePomDotCounter(int pomDotCounter) {
        this.mPomDotCounter = pomDotCounter;
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

    private void fadeAlpha() {
        mSendPomDotAlpha.sendPomAlphaValue(mAlpha);

        if (mAlpha >=1.0f) {
            mAlpha = 1.0f;
            mFadeUp = false;
        }

        if (mAlpha>0.25f && !mFadeUp) {
            mAlpha -= 0.08;
        } else {
            mFadeUp = true;
        }

        if (mFadeUp) {
            mAlpha += 0.08;
        }
    }

    public void setColorSettingsFromMainActivity(int typeOFRound, int settingNumber) {
        if (typeOFRound==3) WORK_COLOR = changeSettingsValues.assignColor(settingNumber);
        if (typeOFRound==4) MINI_BREAK_COLOR = changeSettingsValues.assignColor(settingNumber);
        if (typeOFRound==5) FULL_BREAK_COLOR = changeSettingsValues.assignColor(settingNumber);
    }

    private void instantiateLists() {
        mPomCycleRoundsAsStringsList = new ArrayList<>();
    }

    private void instantiateMiscObjects() {
        changeSettingsValues = new ChangeSettingsValues(mContext);
        dotsBorder =  (GradientDrawable) ContextCompat.getDrawable(mContext, R.drawable.dots_border);

        narrowFont = ResourcesCompat.getFont(mContext, R.font.archivo_narrow);
        narrowFontBold = ResourcesCompat.getFont(mContext, R.font.archivo_narrow_bold);
    }

    public interface sendPomDotAlpha {
        void sendPomAlphaValue(float alpha);
    }

    public class PomDotsViewHolder extends RecyclerView.ViewHolder {
        public View fullView;
        public TextView roundText;
        public ImageView roundImageView;

        public PomDotsViewHolder(@NonNull View itemView) {
            super(itemView);
            roundText = itemView.findViewById(R.id.round_string_textView_for_pom_cycles);
//            roundImageView = itemView.findViewById(R.id.round_string_imageView);
            fullView = itemView;
        }
    }
}
