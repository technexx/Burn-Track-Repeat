package com.example.tragic.irate.simple.stopwatch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.transition.TransitionInflater;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;


public class SettingsFragment extends PreferenceFragmentCompat {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor prefEdit;

    int SOUND_SETTINGS = 1;
    int COLOR_SETTINGS = 2;
    int ABOUT_SETTINGS = 3;

    onChangedSettings mOnChangedSettings;

    public interface onChangedSettings {
        void settingsData(int settingNumber, boolean turnedOn);
    }

    public void sendSettingsData(onChangedSettings xOnChangedSettings) {
        this.mOnChangedSettings = xOnChangedSettings;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_fragment_layout, rootKey);
        sharedPreferences = getContext().getSharedPreferences("settingsPref", 0);
        prefEdit = sharedPreferences.edit();

        Preference soundPref = findPreference("soundPref");
        Preference colorPref = findPreference("colorPref");
        Preference aboutPref = findPreference("aboutPref");

        soundPref.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean clickedValue = (boolean) newValue;
            prefEdit.putBoolean("soundPrefBoolean", clickedValue);
            prefEdit.apply();

            mOnChangedSettings.settingsData(SOUND_SETTINGS, clickedValue);

            return true;
        });

        colorPref.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean clickedValue = (boolean) newValue;
            prefEdit.putBoolean("colorPrefBoolean", clickedValue);
            prefEdit.apply();

            mOnChangedSettings.settingsData(COLOR_SETTINGS, clickedValue);

            return true;
        });

        aboutPref.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean clickedValue = (boolean) newValue;
            prefEdit.putBoolean("aboutPrefBoolean", clickedValue);
            prefEdit.apply();

            mOnChangedSettings.settingsData(ABOUT_SETTINGS, clickedValue);

            return true;
        });

    }
}
