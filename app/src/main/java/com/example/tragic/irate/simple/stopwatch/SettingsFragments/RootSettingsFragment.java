package com.example.tragic.irate.simple.stopwatch.SettingsFragments;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.example.tragic.irate.simple.stopwatch.R;


public class RootSettingsFragment extends PreferenceFragmentCompat {

    SharedPreferences sharedPreferences;

    int SOUND_SETTINGS = 1;
    int COLOR_SETTINGS = 2;
    int TDEE_SETTINGS = 3;
    int ABOUT_FRAGMENT = 4;

    onChangedSettings mOnChangedSettings;

    public interface onChangedSettings {
        void settingsData(int settingNumber);
    }

    public void sendSettingsData(onChangedSettings xOnChangedSettings) {
        this.mOnChangedSettings = xOnChangedSettings;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(getResources().getColor(R.color.night_shadow));

        return view;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preference_fragment_layout, rootKey);
        sharedPreferences = getContext().getSharedPreferences("settingsPref", 0);

        Preference soundOptionPref = findPreference("soundPref");
        Preference colorPref = findPreference("colorPref");
        Preference tdeePref = findPreference("tdeePref");
        Preference aboutPref = findPreference("aboutPref");

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

        aboutPref.setOnPreferenceClickListener(v-> {
            mOnChangedSettings.settingsData(ABOUT_FRAGMENT);

            return true;
        });

    }
}
