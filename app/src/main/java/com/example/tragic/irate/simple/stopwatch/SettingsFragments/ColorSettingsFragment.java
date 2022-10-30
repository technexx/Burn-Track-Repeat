package com.example.tragic.irate.simple.stopwatch.SettingsFragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.tragic.irate.simple.stopwatch.R;


public class ColorSettingsFragment extends PreferenceFragmentCompat {

    int SET_SETTING = 1;
    int BREAK_SETTING = 2;
    int WORK_SETTING = 3;
    int MINI_BREAK_SETTING = 4;
    int FULL_BREAK_SETTING = 5;

    ChangeSettingsValues changeSettingsValues;
    onChangedColorSetting mOnChangedColorSetting;

    ListPreference setPreference;
    ListPreference breakPreference;
    ListPreference workPreference;
    ListPreference miniBreakPreference;
    ListPreference fullBreakPreference;

    String defaultColorSettingForSets;
    String defaultColorSettingForBreaks;
    String defaultColorSettingForWork;
    String defaultColorSettingForMiniBreaks;
    String defaultColorSettingForFullBreak;

    public interface onChangedColorSetting {
        void changeColorSetting(int mode, int typeOFRound, int settingNumber);
    }

    public void colorSetting(onChangedColorSetting xOnChangeColor) {
        this.mOnChangedColorSetting = xOnChangeColor;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(getResources().getColor(R.color.darker_grey));

        return view;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.color_settings_fragment_layout, rootKey);
        changeSettingsValues = new ChangeSettingsValues(getContext());

        SharedPreferences prefShared = PreferenceManager.getDefaultSharedPreferences(getContext());
//        SharedPreferences prefShared = getActivity().getApplicationContext().getSharedPreferences("sharedPrefForSettings", 0);

        setPreference = findPreference("listPrefColorSettingForSets");
        breakPreference = findPreference("listPrefColorSettingForBreaks");
        workPreference = findPreference("listPrefColorSettingForWork");
        miniBreakPreference =  findPreference("listPrefColorSettingForMiniBreaks");
        fullBreakPreference = findPreference("listPrefColorSettingForFullBreak");

        defaultColorSettingForSets = prefShared.getString("colorSettingForSets", "green_setting");
        defaultColorSettingForBreaks = prefShared.getString("colorSettingForBreaks", "red_setting");

        defaultColorSettingForWork = prefShared.getString("colorSettingForWork", "green_setting");
        defaultColorSettingForMiniBreaks = prefShared.getString("colorSettingForMiniBreaks", "red_setting");
        defaultColorSettingForFullBreak = prefShared.getString("colorSettingForFullBreak", "cyan_setting");

        CharSequence[] colorEntryListForSets = setPreference.getEntries();
        CharSequence[] colorEntryListForBreaks = breakPreference.getEntries();
        CharSequence[] colorEntryListForWork = workPreference.getEntries();
        CharSequence[] colorEntryListForMiniBreaks = miniBreakPreference.getEntries();
        CharSequence[] colorEntryListForMFullBreak = fullBreakPreference.getEntries();

        int defaultSetNumericValue = changeSettingsValues.assignColorSettingNumericValue(defaultColorSettingForSets);
        int defaultBreakNumericValue = changeSettingsValues.assignColorSettingNumericValue(defaultColorSettingForBreaks);
        int defaultWorkNumericValue = changeSettingsValues.assignColorSettingNumericValue(defaultColorSettingForWork);
        int defaultMiniBreakNumericValue = changeSettingsValues.assignColorSettingNumericValue(defaultColorSettingForMiniBreaks);
        int defaultFullBreakNumericValue = changeSettingsValues.assignColorSettingNumericValue(defaultColorSettingForFullBreak);

        String defaultSetString = summaryTextChange(colorEntryListForSets, defaultSetNumericValue);
        String defaultBreakSetting = summaryTextChange(colorEntryListForBreaks, defaultBreakNumericValue);
        String defaultWorkString = summaryTextChange(colorEntryListForWork, defaultWorkNumericValue);
        String defaultMiniBreakString = summaryTextChange(colorEntryListForMiniBreaks, defaultMiniBreakNumericValue);
        String defaultFullBreakString = summaryTextChange(colorEntryListForMFullBreak, defaultFullBreakNumericValue);

        setPreference.setSummary(defaultSetString);
        breakPreference.setSummary(defaultBreakSetting);
        workPreference.setSummary(defaultWorkString);
        miniBreakPreference.setSummary(defaultMiniBreakString);
        fullBreakPreference.setSummary(defaultFullBreakString);

        setDefaultColorSettingsIfNoneSelected();

        setPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            int setValue = convertColorSettingObjectToInteger(newValue);
            mOnChangedColorSetting.changeColorSetting(1, SET_SETTING, setValue);

            String entryString = summaryTextChange(colorEntryListForSets, setValue);
            setPreference.setSummary(entryString);
            return true;
        });

        breakPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            int breakValue = convertColorSettingObjectToInteger(newValue);
            mOnChangedColorSetting.changeColorSetting(1, BREAK_SETTING, breakValue);

            String entryString = summaryTextChange(colorEntryListForBreaks, breakValue);
            breakPreference.setSummary(entryString);
            return true;
        });

        workPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            int workValue = convertColorSettingObjectToInteger(newValue);
            mOnChangedColorSetting.changeColorSetting(3, WORK_SETTING, workValue);

            String entryString = summaryTextChange(colorEntryListForWork, workValue);
            workPreference.setSummary(entryString);
            return true;
        });

        miniBreakPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            int miniBreakValue = convertColorSettingObjectToInteger(newValue);
            mOnChangedColorSetting.changeColorSetting(3, MINI_BREAK_SETTING, miniBreakValue);

            String entryString = summaryTextChange(colorEntryListForMiniBreaks, miniBreakValue);
            miniBreakPreference.setSummary(entryString);
            return true;
        });

        fullBreakPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            int fullBreakValue = convertColorSettingObjectToInteger(newValue);
            mOnChangedColorSetting.changeColorSetting(3, FULL_BREAK_SETTING, fullBreakValue);

            String entryString = summaryTextChange(colorEntryListForMFullBreak, fullBreakValue);
            fullBreakPreference.setSummary(entryString);
            return true;
        });
    }

    private void setDefaultColorSettingsIfNoneSelected() {
        String[] colorStringArray = getResources().getStringArray(R.array.color_setting_options);

        if (defaultColorSettingForSets.equals("")) {
            setPreference.setSummary(colorStringArray[0]);
        }

        if (defaultColorSettingForBreaks.equals("")) {
            breakPreference.setSummary(colorStringArray[1]);
        }

        if (defaultColorSettingForWork.equals("")) {
            workPreference.setSummary(colorStringArray[0]);
        }

        if (defaultColorSettingForMiniBreaks.equals("")) {
            miniBreakPreference.setSummary(colorStringArray[1]);
        }

        if (defaultColorSettingForFullBreak.equals("")) {
            fullBreakPreference.setSummary(colorStringArray[5]);
        }
    }

    private int convertColorSettingObjectToInteger(Object newVar) {
        return changeSettingsValues.assignColorSettingNumericValue((String) newVar);
    }

    private String summaryTextChange(CharSequence[] settingsList, int entry) {
        return (String) settingsList[entry];
    }
}