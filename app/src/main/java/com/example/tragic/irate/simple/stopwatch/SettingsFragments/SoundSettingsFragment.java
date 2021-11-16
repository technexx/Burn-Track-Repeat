package com.example.tragic.irate.simple.stopwatch.SettingsFragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.tragic.irate.simple.stopwatch.R;

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

    int SILENT = 1;
    int VIBRATE_ONCE = 21;
    int VIBRATE_TWICE = 22;
    int VIBRATE_THRICE = 23;
    int PLAY_RINGTONE = 3;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.sounds_settings_layout, rootKey);
        sharedPreferences = getContext().getSharedPreferences("settingsPref", 0);
        prefEdit = sharedPreferences.edit();

        Preference silent = findPreference("silent");
        Preference vibrate_once = findPreference("vibrate_once");
        Preference vibrate_twice = findPreference("vibrate_twice");
        Preference vibrate_thrice = findPreference("vibrate_thrice");
        Preference use_ringtone = findPreference("use_ringtone");

//        silent.setOnPreferenceChangeListener((preference, newValue) -> {
//            mOnChangedSound.changeSound(1);
//            return true;
//        });

    }
}
