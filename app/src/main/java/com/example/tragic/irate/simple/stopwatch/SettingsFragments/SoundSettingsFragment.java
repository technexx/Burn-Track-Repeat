package com.example.tragic.irate.simple.stopwatch.SettingsFragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.example.tragic.irate.simple.stopwatch.ChangeSettingsValues;
import com.example.tragic.irate.simple.stopwatch.R;

public class SoundSettingsFragment extends PreferenceFragmentCompat {

    int SET_SETTING = 1;
    int BREAK_SETTING = 2;
    int LAST_ROUND_SETTING = 3;
    int WORK_SETTING = 4;
    int MINI_BREAK_SETTING = 5;
    int FULL_BREAK_SETTING = 6;

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
        setPreferencesFromResource(R.xml.sounds_settings_fragment_layout, rootKey);
        changeSettingsValues = new ChangeSettingsValues();

        SharedPreferences prefShared = PreferenceManager.getDefaultSharedPreferences(getContext());

        ListPreference setPreference = (ListPreference) findPreference("soundSettingForSets");
        ListPreference breakPreference = (ListPreference) findPreference("soundSettingForBreaks");
        SwitchPreference lastRoundPreference = (SwitchPreference) findPreference("soundSettingForLastRound");

        ListPreference workPreference = (ListPreference) findPreference("soundSettingForWork");
        ListPreference miniBreakPreference = (ListPreference) findPreference("soundSettingForMiniBreaks");
        SwitchPreference fullBreakPreference = (SwitchPreference) findPreference("soundSettingForFullBreak");

        String defaultSoundSettingForSets = prefShared.getString("soundSettingForSets", "vibrate_once");
        String defaultSoundSettingForBreaks = prefShared.getString("soundSettingForBreaks", "vibrate_once");

        String defaultSoundSettingForWork = prefShared.getString("soundSettingForWork", "vibrate_once");
        String defaultSoundSettingForMiniBreaks = prefShared.getString("soundSettingForMiniBreaks", "vibrate_once");

        CharSequence[] soundEntryListForSets = setPreference.getEntries();
        CharSequence[] soundEntryListForBreaks = breakPreference.getEntries();
        CharSequence[] soundEntryListForWork = workPreference.getEntries();
        CharSequence[] soundEntryListForMiniBreaks = miniBreakPreference.getEntries();

        int defaultSetNumericValue = changeSettingsValues.assignSoundSettingNumericValue(defaultSoundSettingForSets);
        int defaultBreakNumericValue = changeSettingsValues.assignSoundSettingNumericValue(defaultSoundSettingForBreaks);
        int defaultWorkNumericValue = changeSettingsValues.assignSoundSettingNumericValue(defaultSoundSettingForWork);
        int defaultMiniBreaksNumericValue = changeSettingsValues.assignSoundSettingNumericValue(defaultSoundSettingForMiniBreaks);

        String defaultSetString = summaryTextChange(soundEntryListForSets, defaultSetNumericValue);
        String defaultBreakString = summaryTextChange(soundEntryListForBreaks, defaultBreakNumericValue);
        String defaultWorkString = summaryTextChange(soundEntryListForWork, defaultWorkNumericValue);
        String defaultMiniBreaksString = summaryTextChange(soundEntryListForMiniBreaks, defaultMiniBreaksNumericValue);

        setPreference.setSummary(defaultSetString);
        breakPreference.setSummary(defaultBreakString);
        workPreference.setSummary(defaultWorkString);
        miniBreakPreference.setSummary(defaultMiniBreaksString);


        setPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            int setValue = convertSoundSettingObjectToInteger(newValue);
            mOnChangedSoundSetting.changeSoundSetting(SET_SETTING, setValue);

            String entryString = summaryTextChange(soundEntryListForSets, setValue);
            setPreference.setSummary(entryString);
            return true;
        });

        breakPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            int breaksValue = convertSoundSettingObjectToInteger(newValue);
            mOnChangedSoundSetting.changeSoundSetting(BREAK_SETTING, breaksValue);

            String entryString = summaryTextChange(soundEntryListForBreaks, breaksValue);
            breakPreference.setSummary(entryString);
            return true;
        });

        lastRoundPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean isInfinityEnabled = (boolean) newValue;
            int infinityInteger = 0;
            if (isInfinityEnabled) infinityInteger = 1;
            mOnChangedSoundSetting.changeSoundSetting(LAST_ROUND_SETTING, infinityInteger);
            return true;
        });

        workPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            int workValue = convertSoundSettingObjectToInteger(newValue);
            mOnChangedSoundSetting.changeSoundSetting(WORK_SETTING, workValue);

            String entryString = summaryTextChange(soundEntryListForWork, workValue);
            workPreference.setSummary(entryString);
            return true;
        });

        miniBreakPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            int miniBreakValue = convertSoundSettingObjectToInteger(newValue);
            mOnChangedSoundSetting.changeSoundSetting(MINI_BREAK_SETTING, miniBreakValue);

            String entryString = summaryTextChange(soundEntryListForMiniBreaks, miniBreakValue);
            miniBreakPreference.setSummary(entryString);
            return true;
        });

        fullBreakPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean isInfinityEnabled = (boolean) newValue;
            int infinityInteger = 0;
            if (isInfinityEnabled) infinityInteger = 1;
            mOnChangedSoundSetting.changeSoundSetting(FULL_BREAK_SETTING, infinityInteger);
            return true;
        });
    }

    private int convertSoundSettingObjectToInteger(Object newVar) {
        return changeSettingsValues.assignSoundSettingNumericValue((String) newVar);
    }

    private String summaryTextChange(CharSequence[] settingsList, int entry) {
        if (entry>=1) {
            return (String) settingsList[entry-1];
        } else {
            return "";
        }
    }
}