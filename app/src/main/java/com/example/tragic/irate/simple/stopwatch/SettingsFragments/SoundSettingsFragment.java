package com.example.tragic.irate.simple.stopwatch.SettingsFragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.tragic.irate.simple.stopwatch.R;

import java.util.Arrays;
import java.util.List;

public class SoundSettingsFragment extends PreferenceFragmentCompat {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor prefEdit;

    onChangedSoundForSets mOnChangedSoundForSets;
    onChangedSoundForBreaks mOnChangeSoundForBreaks;

    public interface onChangedSoundForSets {
        void changeSetSound(int typeOfSetting);
    }

    public interface onChangedSoundForBreaks {
        void changeBreakSounds(int typeOfSetting);
    }

    public void soundSettingForSets(onChangedSoundForSets xonChangedSoundForSets) {
        this.mOnChangedSoundForSets = xonChangedSoundForSets;
    }

    public void soundSettingForBreaks(onChangedSoundForBreaks xOnChangedSoundForBreaks) {
        this.mOnChangeSoundForBreaks = xOnChangedSoundForBreaks;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.sounds_settings_layout, rootKey);

        SharedPreferences prefShared = PreferenceManager.getDefaultSharedPreferences(getContext());

        String defaultSoundSettingForSets = prefShared.getString("soundSettingForSets", "");
        String defaultSoundSettingForBreaks = prefShared.getString("soundSettingForBreaks", "");

        ListPreference soundPreference = (ListPreference) findPreference("soundSettingForSets");
        ListPreference breakPreference = (ListPreference) findPreference("soundSettingForBreaks");

        CharSequence[] soundEntryListForSets = soundPreference.getEntries();
        CharSequence[] soundEntryListForBreaks = breakPreference.getEntries();

        int defaultSetNumericValue = assignSoundSettingNumericValue(defaultSoundSettingForSets);
        int defaultBreakNumericValue = assignSoundSettingNumericValue(defaultSoundSettingForBreaks);

        String defaultSetString = (String) soundEntryListForSets[defaultSetNumericValue-1];
        String defaultBreakString = (String) soundEntryListForBreaks[defaultBreakNumericValue-1];

        soundPreference.setSummary(defaultSetString);
        breakPreference.setSummary(defaultBreakString);

        soundPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            int settingsValue = assignSoundSettingNumericValue((String) newValue);
            mOnChangedSoundForSets.changeSetSound(settingsValue);

            String entryString = (String) soundEntryListForSets[settingsValue-1];
            soundPreference.setSummary(entryString);
            return true;
        });

        breakPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            int breaksValue = assignSoundSettingNumericValue((String) newValue);
            mOnChangeSoundForBreaks.changeBreakSounds(breaksValue);

            String entryString = (String) soundEntryListForBreaks[breaksValue-1];
            soundPreference.setSummary(entryString);
            return true;
        });
    }

    public int assignSoundSettingNumericValue(String setting) {
        int assignedValue = 0;
        if (setting.equals("silent")) {
            assignedValue = 1;
        }
        if (setting.equals("vibrate_once")) {
            assignedValue = 2;
        }
        if (setting.equals("vibrate_twice")) {
            assignedValue = 3;
        }
        if (setting.equals("vibrate_thrice")) {
            assignedValue = 4;
        }
        if (setting.equals("use_ringtone")) {
            assignedValue = 5;
        }
        return assignedValue;
    }
}
