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