package com.example.tragic.irate.simple.stopwatch.SettingsFragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.tragic.irate.simple.stopwatch.R;


public class RootSettingsFragment extends PreferenceFragmentCompat {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor prefEdit;

    int SOUND_SETTINGS = 1;
    int COLOR_SETTINGS = 2;
    int ABOUT_SETTINGS = 3;

    onChangedSettings mOnChangedSettings;

    public interface onChangedSettings {
        void settingsData(int settingNumber);
    }

    public void sendSettingsData(onChangedSettings xOnChangedSettings) {
        this.mOnChangedSettings = xOnChangedSettings;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_settings_layout, rootKey);
        sharedPreferences = getContext().getSharedPreferences("settingsPref", 0);
        prefEdit = sharedPreferences.edit();

        Preference soundOptionPref = findPreference("soundOptions");
        Preference soundPref = findPreference("soundPref");
        Preference colorPref = findPreference("colorPref");
        Preference aboutPref = findPreference("aboutPref");

//        soundOptionPref.setOnPreferenceClickListener(v->{
//            mOnChangedSettings.settingsData(1);
//            return true;
//        });

//        soundPref.setOnPreferenceChangeListener((preference, newValue) -> {
//            boolean clickedValue = (boolean) newValue;
//            prefEdit.putBoolean("soundPrefBoolean", clickedValue);
//            prefEdit.apply();
//
//            mOnChangedSettings.settingsData(SOUND_SETTINGS);
//
//            return true;
//        });

        soundOptionPref.setOnPreferenceClickListener(v-> {
            mOnChangedSettings.settingsData(SOUND_SETTINGS);
            return true;
        });

        colorPref.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean clickedValue = (boolean) newValue;
            prefEdit.putBoolean("colorPrefBoolean", clickedValue);
            prefEdit.apply();

            mOnChangedSettings.settingsData(COLOR_SETTINGS);

            return true;
        });

        aboutPref.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean clickedValue = (boolean) newValue;
            prefEdit.putBoolean("aboutPrefBoolean", clickedValue);
            prefEdit.apply();

            mOnChangedSettings.settingsData(ABOUT_SETTINGS);

            return true;
        });

    }
}
