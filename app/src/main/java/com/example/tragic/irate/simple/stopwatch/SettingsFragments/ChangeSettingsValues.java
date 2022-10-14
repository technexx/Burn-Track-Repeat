package com.example.tragic.irate.simple.stopwatch.SettingsFragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.tragic.irate.simple.stopwatch.R;
import com.example.tragic.irate.simple.stopwatch.SettingsFragments.SoundSettingsFragment;

public class ChangeSettingsValues {
    Context mContext;

    public ChangeSettingsValues(Context context) {
        this.mContext = context;
    }

    public int assignSoundSettingNumericValue(String setting) {
        int assignedValue = 0;

        if (setting.equals("silent")) {
            assignedValue = 0;
        }
        if (setting.equals("vibrate_once")) {
            assignedValue = 1;
        }
        if (setting.equals("vibrate_twice")) {
            assignedValue = 2;
        }
        if (setting.equals("vibrate_thrice")) {
            assignedValue = 3;
        }
        if (setting.equals("use_ringtone")) {
            assignedValue = 4;
        }
        return assignedValue;
    }

    public boolean assignFinalRoundSwitchValue(boolean setting) {
        return setting;
    }

    public int assignColorSettingNumericValue(String setting) {
        int assignedValue = 0;

        if (setting.equals("green_setting")) {
            assignedValue = 0;
        }
        if (setting.equals("red_setting")) {
            assignedValue = 1;
        }
        if (setting.equals("blue_setting")) {
            assignedValue = 2;
        }
        if (setting.equals("yellow_setting")) {
            assignedValue = 3;
        }
        if (setting.equals("magenta_setting")) {
            assignedValue = 4;
        }
        if (setting.equals("cyan_setting")) {
            assignedValue = 5;
        }
        return assignedValue;
    }

    public long[] getVibrationSetting(int settingNumber) {
        long[] chosenVibration = new long[]{0, 300, 300};

        if (settingNumber==2) {
            chosenVibration = new long[]{0, 300, 150, 300, 150};
        }
        if (settingNumber==3) {
            chosenVibration = new long[]{0, 300, 300, 300, 300, 300, 300};
        }

        return chosenVibration;
    }

    public int assignColor(int setting) {
        int color = 0;

        if (setting==0) color = Color.GREEN;
        if (setting==1) color = Color.RED;
        if (setting==2) color = ContextCompat.getColor(mContext, R.color.light_blue);
        if (setting==3) color = Color.YELLOW;
        if (setting==4) color = Color.MAGENTA;
        if (setting==5) color = Color.CYAN;

        return color;
    }

    public int assignColorAsIntValue(int setting) {
        int color = 0;

        if (setting==0) color = R.color.android_green;
        if (setting==1) color = R.color.android_red;
        if (setting==2) color = R.color.android_blue;
        if (setting==3) color = R.color.android_yellow;
        if (setting==4) color = R.color.android_magenta;
        if (setting==5) color = R.color.android_cyan;

        return color;
    }

}