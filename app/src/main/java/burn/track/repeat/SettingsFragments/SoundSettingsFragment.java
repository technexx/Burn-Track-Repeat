package burn.track.repeat.SettingsFragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import burn.track.repeat.R;


public class SoundSettingsFragment extends PreferenceFragmentCompat {

    int SET_SETTING = 1;
    int BREAK_SETTING = 2;
    int LAST_ROUND_SETTING = 3;
    int WORK_SETTING = 4;
    int MINI_BREAK_SETTING = 5;
    int FULL_BREAK_SETTING = 6;

    ChangeSettingsValues changeSettingsValues;
    onChangedSoundSetting mOnChangedSoundSetting;

    ListPreference setPreference;
    ListPreference breakPreference;
    SwitchPreference preferenceForLastCycleRound;

    ListPreference workPreference;
    ListPreference miniBreakPreference;
    SwitchPreference preferenceForRestRound;

    String defaultSoundSettingForSets;
    String defaultSoundSettingForBreaks;
    String defaultSoundSettingForWork;
    String defaultSoundSettingForMiniBreaks;

    public interface onChangedSoundSetting {
        void changeSoundSetting(int typeOfRound, int settingNumber);
    }

    public void soundSetting(onChangedSoundSetting xonChangedSound) {
        this.mOnChangedSoundSetting = xonChangedSound;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(getResources().getColor(R.color.alien_black));

        return view;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.sounds_settings_fragment_layout, rootKey);
        changeSettingsValues = new ChangeSettingsValues(getContext());

        SharedPreferences prefShared = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences.Editor prefEdit = prefShared.edit();

        setPreference = findPreference("listPrefSoundSettingForSets");
        breakPreference = findPreference("listPrefSoundSettingForBreaks");
        preferenceForLastCycleRound = findPreference("listPrefSoundSettingForLastCycleRound");

        workPreference = findPreference("listPrefSoundSettingForWork");
        miniBreakPreference = findPreference("listPrefSoundSettingForMiniBreaks");
        preferenceForRestRound = findPreference("listPrefSoundSettingForLastCycleRound");

        defaultSoundSettingForSets = prefShared.getString("soundSettingForSets", "vibrate_once");
        defaultSoundSettingForBreaks = prefShared.getString("soundSettingForBreaks", "vibrate_twice");
        boolean defaultSoundSettingForFinalCyclesRound = prefShared.getBoolean("soundSettingForFinalCyclesRound", true);

        defaultSoundSettingForWork = prefShared.getString("soundSettingForWork", "vibrate_once");
        defaultSoundSettingForMiniBreaks = prefShared.getString("soundSettingForMiniBreaks", "vibrate_twice");
        boolean defaultSoundSettingForRestRound = prefShared.getBoolean("soundSettingForRestRound", true);

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

        setDefaultSoundSettingsIfNoneSelected();

        setPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            int setValue = convertSoundSettingObjectToInteger(newValue);
            mOnChangedSoundSetting.changeSoundSetting(SET_SETTING, setValue);

            String entryString = summaryTextChange(soundEntryListForSets, setValue);
            setPreference.setSummary(entryString);

            prefEdit.putString("soundSettingForSets", entryValuesKey(setPreference.getEntryValues(), setValue));
            prefEdit.apply();

            return true;
        });

        breakPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            int breaksValue = convertSoundSettingObjectToInteger(newValue);
            mOnChangedSoundSetting.changeSoundSetting(BREAK_SETTING, breaksValue);

            String entryString = summaryTextChange(soundEntryListForBreaks, breaksValue);
            breakPreference.setSummary(entryString);

            prefEdit.putString("soundSettingForBreaks", entryValuesKey(breakPreference.getEntryValues(), breaksValue));
            prefEdit.apply();

            return true;
        });

        preferenceForLastCycleRound.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean isInfinityEnabled = (boolean) newValue;
            int infinityInteger = 0;
            if (isInfinityEnabled) infinityInteger = 1;

            mOnChangedSoundSetting.changeSoundSetting(LAST_ROUND_SETTING, infinityInteger);

            prefEdit.putBoolean("soundSettingForLastRound", isInfinityEnabled);
            prefEdit.apply();

            return true;
        });

        workPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            int workValue = convertSoundSettingObjectToInteger(newValue);
            mOnChangedSoundSetting.changeSoundSetting(WORK_SETTING, workValue);

            String entryString = summaryTextChange(soundEntryListForWork, workValue);
            workPreference.setSummary(entryString);

            prefEdit.putString("soundSettingForWork", entryValuesKey(workPreference.getEntryValues(), workValue));
            prefEdit.apply();

            return true;
        });

        miniBreakPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            int miniBreakValue = convertSoundSettingObjectToInteger(newValue);
            mOnChangedSoundSetting.changeSoundSetting(MINI_BREAK_SETTING, miniBreakValue);

            String entryString = summaryTextChange(soundEntryListForMiniBreaks, miniBreakValue);
            miniBreakPreference.setSummary(entryString);

            prefEdit.putString("soundSettingForMiniBreaks", entryValuesKey(miniBreakPreference.getEntryValues(), miniBreakValue));
            prefEdit.apply();

            return true;
        });

        preferenceForRestRound.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean isInfinityEnabled = (boolean) newValue;
            int infinityInteger = 0;

            if (isInfinityEnabled) infinityInteger = 1;
            mOnChangedSoundSetting.changeSoundSetting(FULL_BREAK_SETTING, infinityInteger);

            prefEdit.putBoolean("soundSettingForRestRound", isInfinityEnabled);
            prefEdit.apply();

            return true;
        });
    }

    private void setDefaultSoundSettingsIfNoneSelected() {
        String[] soundStringArray = getResources().getStringArray(R.array.sound_setting_options);

        if (defaultSoundSettingForSets.equals("")) {
            setPreference.setSummary(soundStringArray[1]);
        }

        if (defaultSoundSettingForBreaks.equals("")) {
            breakPreference.setSummary(soundStringArray[2]);
        }

        if (defaultSoundSettingForWork.equals("")) {
            workPreference.setSummary(soundStringArray[1]);
        }

        if (defaultSoundSettingForMiniBreaks.equals("")) {
            miniBreakPreference.setSummary(soundStringArray[2]);
        }
    }

    private int convertSoundSettingObjectToInteger(Object newVar) {
        return changeSettingsValues.assignSoundSettingNumericValue((String) newVar);
    }

    private String summaryTextChange(CharSequence[] settingsList, int entry) {
        return (String) settingsList[entry];
    }

    private String entryValuesKey(CharSequence[] keyList, int entry) {
        return (String) keyList[entry];
    }
}