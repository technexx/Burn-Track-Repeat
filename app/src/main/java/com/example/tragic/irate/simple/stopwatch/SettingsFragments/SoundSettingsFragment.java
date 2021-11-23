package com.example.tragic.irate.simple.stopwatch.SettingsFragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.tragic.irate.simple.stopwatch.ChangeSettingsValues;
import com.example.tragic.irate.simple.stopwatch.R;

import java.util.Arrays;
import java.util.List;

public class SoundSettingsFragment extends PreferenceFragmentCompat {

    int SET_SETTING = 1;
    int BREAK_SETTING = 2;
    int WORK_SETTING = 3;
    int MINI_BREAK_SETTING = 4;
    int FULL_BREAK_SETTING = 5;

    ChangeSettingsValues changeSettingsValues;
    onChangedSoundSetting mOnChangedSoundSetting;

    public interface onChangedSoundSetting {
        void changeSoundSetting(int typeOfRound, int settingNumber);
    }

    public void soundSetting(onChangedSoundSetting xonChangedSound) {
        this.mOnChangedSoundSetting = xonChangedSound;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.sounds_settings_layout, rootKey);
        changeSettingsValues = new ChangeSettingsValues();

        SharedPreferences prefShared = PreferenceManager.getDefaultSharedPreferences(getContext());

        ListPreference setPreference = (ListPreference) findPreference("soundSettingForSets");
        ListPreference breakPreference = (ListPreference) findPreference("soundSettingForBreaks");
        ListPreference workPreference = (ListPreference) findPreference("soundSettingForWork");
        ListPreference miniBreakPreference = (ListPreference) findPreference("soundSettingForMiniBreaks");
        ListPreference fullBreakPreference = (ListPreference) findPreference("soundSettingForFullBreak");

        String defaultSoundSettingForSets = prefShared.getString("soundSettingForSets", "vibrate_once");
        String defaultSoundSettingForBreaks = prefShared.getString("soundSettingForBreaks", "vibrate_once");
        String defaultSoundSettingForWork = prefShared.getString("soundSettingForWork", "vibrate_once");
        String defaultSoundSettingForMiniBreaks = prefShared.getString("soundSettingForMiniBreaks", "vibrate_once");
        String defaultSoundSettingForFullBreak = prefShared.getString("soundSettingForFullBreak", "vibrate_once");

        CharSequence[] soundEntryListForSets = setPreference.getEntries();
        CharSequence[] soundEntryListForBreaks = breakPreference.getEntries();
        CharSequence[] soundEntryListForWork = workPreference.getEntries();
        CharSequence[] soundEntryListForMiniBreaks = miniBreakPreference.getEntries();
        CharSequence[] soundEntryListForFullBreak = fullBreakPreference.getEntries();

        int defaultSetNumericValue = changeSettingsValues.assignSoundSettingNumericValue(defaultSoundSettingForSets);
        int defaultBreakNumericValue = changeSettingsValues.assignSoundSettingNumericValue(defaultSoundSettingForBreaks);
        int defaultWorkNumericValue = changeSettingsValues.assignSoundSettingNumericValue(defaultSoundSettingForWork);
        int defaultMiniBreaksNumericValue = changeSettingsValues.assignSoundSettingNumericValue(defaultSoundSettingForMiniBreaks);
        int defaultFullBreakNumericValue = changeSettingsValues.assignSoundSettingNumericValue(defaultSoundSettingForFullBreak);

        String defaultSetString = summaryText(soundEntryListForSets, defaultSetNumericValue);
        String defaultBreakString = summaryText(soundEntryListForBreaks, defaultBreakNumericValue);
        String defaultWorkString = summaryText(soundEntryListForWork, defaultWorkNumericValue);
        String defaultMiniBreaksString = summaryText(soundEntryListForMiniBreaks, defaultMiniBreaksNumericValue);
        String defaultFullBreakString = summaryText(soundEntryListForFullBreak, defaultFullBreakNumericValue);

        setPreference.setSummary(defaultSetString);
        breakPreference.setSummary(defaultBreakString);
        workPreference.setSummary(defaultWorkString);
        miniBreakPreference.setSummary(defaultMiniBreaksString);
        fullBreakPreference.setSummary(defaultFullBreakString);


        setPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            int settingsValue = soundSettingVariable(newValue);
            mOnChangedSoundSetting.changeSoundSetting(SET_SETTING, settingsValue);

            String entryString = summaryText(soundEntryListForSets, settingsValue);
            setPreference.setSummary(entryString);
            return true;
        });

        breakPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            int breaksValue = soundSettingVariable(newValue);
            mOnChangedSoundSetting.changeSoundSetting(BREAK_SETTING, breaksValue);

            String entryString = summaryText(soundEntryListForSets, breaksValue);
            breakPreference.setSummary(entryString);
            return true;
        });

        workPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            int workValue = soundSettingVariable(newValue);
            mOnChangedSoundSetting.changeSoundSetting(WORK_SETTING, workValue);

            String entryString = summaryText(soundEntryListForBreaks, workValue);
            workPreference.setSummary(entryString);
            return true;
        });

        miniBreakPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            int miniBreakValue = soundSettingVariable(newValue);
            mOnChangedSoundSetting.changeSoundSetting(MINI_BREAK_SETTING, miniBreakValue);

            String entryString = summaryText(soundEntryListForSets, miniBreakValue);
            miniBreakPreference.setSummary(entryString);
            return true;
        });

        fullBreakPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            int fullBreakValue = soundSettingVariable(newValue);
            mOnChangedSoundSetting.changeSoundSetting(FULL_BREAK_SETTING, fullBreakValue);

            String entryString = summaryText(soundEntryListForSets, fullBreakValue);
            fullBreakPreference.setSummary(entryString);
            return true;
        });
    }

    private int soundSettingVariable(Object newVar) {
        return changeSettingsValues.assignSoundSettingNumericValue((String) newVar);
    }

    private String summaryText(CharSequence[] settingsList, int entry) {
        if (entry>=1) {
            return (String) settingsList[entry-1];
        } else {
            return "";
        }
    }
}