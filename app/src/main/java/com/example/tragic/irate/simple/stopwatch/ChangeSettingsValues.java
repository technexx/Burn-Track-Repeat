package com.example.tragic.irate.simple.stopwatch;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.tragic.irate.simple.stopwatch.SettingsFragments.SoundSettingsFragment;

public class ChangeSettingsValues {

    int SOUND_SETTINGS = 1;
    int COLOR_SETTINGS = 2;
    int ABOUT_SETTINGS = 3;

    int mSettingToChange;
    int mValueOfSetting;

    public ChangeSettingsValues() {
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
        return assignedValue;
    }

    public long[] getVibrationSetting(int settingNumber) {
        long[] chosenVibration = new long[0];

        if (settingNumber==2) {
            chosenVibration = new long[]{0, 300, 300};

        }
        if (settingNumber==3) {
            chosenVibration = new long[]{0, 300, 150, 300, 150};
        }
        if (settingNumber==4) {
            chosenVibration = new long[]{0, 300, 300, 300, 300, 300, 300};
        }

        return chosenVibration;
    }

}