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

        //Todo: Create looping method for Strings + ints outside of clickListeners, since we only need them to populate summary?
        SharedPreferences prefShared = PreferenceManager.getDefaultSharedPreferences(getContext());

        ListPreference setPreference = (ListPreference) findPreference("soundSettingForSets");
        ListPreference breakPreference = (ListPreference) findPreference("soundSettingForBreaks");
        ListPreference workPreference = (ListPreference) findPreference("soundSettingForWork");
        ListPreference miniBreakPreference = (ListPreference) findPreference("soundSettingForMiniBreaks");
        ListPreference fullBreakPreference = (ListPreference) findPreference("soundSettingForFullBreak");

        String defaultSoundSettingForSets = prefShared.getString("soundSettingForSets", "");
        String defaultSoundSettingForBreaks = prefShared.getString("soundSettingForBreaks", "");
        String defaultSoundSettingForWork = prefShared.getString("soundSettingForWork", "");
        String defaultSoundSettingForMiniBreaks = prefShared.getString("soundSettingForMiniBreaks", "");
        String defaultSoundSettingForFullBreak = prefShared.getString("soundSettingForFullBreak", "");

        CharSequence[] soundEntryListForSets = setPreference.getEntries();
        CharSequence[] soundEntryListForBreaks = breakPreference.getEntries();
        CharSequence[] soundEntryListForWork = workPreference.getEntries();
        CharSequence[] soundEntryListForMiniBreaks = miniBreakPreference.getEntries();
        CharSequence[] soundEntryListForFullBreak = fullBreakPreference.getEntries();

        int defaultSetNumericValue = assignSoundSettingNumericValue(defaultSoundSettingForSets);
        int defaultBreakNumericValue = assignSoundSettingNumericValue(defaultSoundSettingForBreaks);
        int defaultWorkNumericValue = assignSoundSettingNumericValue(defaultSoundSettingForWork);
        int defaultMiniBreaksNumericValue = assignSoundSettingNumericValue(defaultSoundSettingForMiniBreaks);
        int defaultFullBreakNumericValue = assignSoundSettingNumericValue(defaultSoundSettingForFullBreak);

        String defaultSetString = (String) soundEntryListForSets[defaultSetNumericValue-1];
        String defaultBreakString = (String) soundEntryListForBreaks[defaultBreakNumericValue-1];
        String defaultWorkString = (String) soundEntryListForWork[defaultWorkNumericValue-1];
        String defaultMiniBreaksString = (String) soundEntryListForMiniBreaks[defaultMiniBreaksNumericValue-1];
        String defaultFullBreakString = (String) soundEntryListForFullBreak[defaultFullBreakNumericValue-1];

        setPreference.setSummary(defaultSetString);
        breakPreference.setSummary(defaultBreakString);
        workPreference.setSummary(defaultWorkString);
        miniBreakPreference.setSummary(defaultMiniBreaksString);
        fullBreakPreference.setSummary(defaultFullBreakString);

        setPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            int settingsValue = assignSoundSettingNumericValue((String) newValue);
            mOnChangedSoundForSets.changeSetSound(settingsValue);

            String entryString = (String) soundEntryListForSets[settingsValue-1];
            setPreference.setSummary(entryString);
            return true;
        });

        breakPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            int breaksValue = assignSoundSettingNumericValue((String) newValue);
            mOnChangeSoundForBreaks.changeBreakSounds(breaksValue);

            String entryString = (String) soundEntryListForBreaks[breaksValue-1];
            breakPreference.setSummary(entryString);
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
