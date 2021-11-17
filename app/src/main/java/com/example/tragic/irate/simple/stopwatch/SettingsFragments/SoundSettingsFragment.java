package com.example.tragic.irate.simple.stopwatch.SettingsFragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.tragic.irate.simple.stopwatch.R;

import java.util.Arrays;
import java.util.List;

public class SoundSettingsFragment extends PreferenceFragmentCompat {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor prefEdit;

    onChangedSound mOnChangedSound;

    public interface onChangedSound {
        void changeSound(int typeOfSetting);
    }

    public void setSoundSetting(onChangedSound xOnChangedSound) {
        this.mOnChangedSound = xOnChangedSound;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.sounds_settings_layout, rootKey);

        ListPreference soundPreference = (ListPreference) findPreference("soundSettings");

        soundPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // Casting this to ListPreference would allow us to call getValues() on it, and assign the same conditional as below.
                ListPreference pref = (ListPreference) preference;

                int settingsValue = assignSoundSettingNumericValue((String) newValue);
                mOnChangedSound.changeSound(settingsValue);
                return true;
            }
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
