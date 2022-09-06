package com.example.tragic.irate.simple.stopwatch.Miscellaneous;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

public class ScreenRatioLayoutChanger {

    Context mContext;

    public ScreenRatioLayoutChanger (Context context) {
        this.mContext = context;
    }

    public float setScreenRatioBasedLayoutChanges() {
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        float ratio = ((float)metrics.heightPixels / (float)metrics.widthPixels);
        Log.i("testRatio", "ratio is " + ratio);
        return ratio;
    }
}
