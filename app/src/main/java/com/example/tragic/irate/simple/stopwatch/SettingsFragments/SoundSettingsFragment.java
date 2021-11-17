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

                if (newValue.equals("silent")) {
                    mOnChangedSound.changeSound(1);
                }
                if (newValue.equals("vibrate_once")) {
                    mOnChangedSound.changeSound(2);
                }
                if (newValue.equals("vibrate_twice")) {
                    mOnChangedSound.changeSound(3);
                }
                if (newValue.equals("vibrate_thrice")) {
                    mOnChangedSound.changeSound(4);
                }
                if (newValue.equals("use_ringtone")) {
                    mOnChangedSound.changeSound(5);
                }
                return true;
            }
        });
    }
}
