package com.example.tragic.irate.simple.stopwatch.SettingsFragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.tragic.irate.simple.stopwatch.R;


public class RootSettingsFragment extends PreferenceFragmentCompat {

    SharedPreferences sharedPreferences;

    int SOUND_SETTINGS = 1;
    int COLOR_SETTINGS = 2;
    int TDEE_SETTINGS = 3;

    onChangedSettings mOnChangedSettings;

    public interface onChangedSettings {
        void settingsData(int settingNumber);
    }

    public void sendSettingsData(onChangedSettings xOnChangedSettings) {
        this.mOnChangedSettings = xOnChangedSettings;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preference_fragment_layout, rootKey);
        sharedPreferences = getContext().getSharedPreferences("settingsPref", 0);

        Preference soundOptionPref = findPreference("soundPref");
        Preference colorPref = findPreference("colorPref");
        Preference tdeePref = findPreference("tdeePref");

        soundOptionPref.setOnPreferenceClickListener(v-> {
            mOnChangedSettings.settingsData(SOUND_SETTINGS);
            return true;
        });

        colorPref.setOnPreferenceClickListener(v-> {
            mOnChangedSettings.settingsData(COLOR_SETTINGS);

            return true;
        });

        tdeePref.setOnPreferenceClickListener(v-> {
            mOnChangedSettings.settingsData(TDEE_SETTINGS);

            return true;
        });

    }
}
