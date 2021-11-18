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

    public void setSoundSetting(int setting) {
        this.mValueOfSetting = setting;
    }

    public long[] getVibrationSetting() {
        long[] chosenVibration = new long[0];

        if (mValueOfSetting==2) {
            chosenVibration = new long[]{0, 300, 300};

        }
        if (mValueOfSetting==3) {
            chosenVibration = new long[]{0, 300, 300, 0, 500, 300};
        }
        if (mValueOfSetting==4) {
            chosenVibration = new long[]{0, 300, 300, 0, 300, 300, 0, 300, 300};
        }

        return chosenVibration;
    }

}