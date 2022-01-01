package com.example.tragic.irate.simple.stopwatch;

import android.content.Context;
import android.util.DisplayMetrics;

public class ScreenRatioLayoutChanger {

    Context mContext;

    public ScreenRatioLayoutChanger (Context context) {
        this.mContext = context;
    }

    public float setScreenRatioBasedLayoutChanges() {
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        float ratio = ((float)metrics.heightPixels / (float)metrics.widthPixels);
        return ratio;
    }
}
