package com.example.tragic.irate.simple.stopwatch;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;


public class SettingsFragment extends PreferenceFragmentCompat {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor prefEdit;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_fragment_layout, rootKey);
        sharedPreferences = getContext().getSharedPreferences("settingsPref", 0);
        prefEdit = sharedPreferences.edit();

        Preference prefOne = findPreference("test_one");

        prefOne.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean clickedValue = (boolean) newValue;
            prefEdit.putBoolean("firstSettingBoolean", clickedValue);
            prefEdit.apply();
            Toast.makeText(getContext(), String.valueOf(clickedValue), Toast.LENGTH_SHORT ).show();
            return true;
        });

        prefOne.setOnPreferenceClickListener(v-> {
            prefEdit.putString("firstSettingSwitch", "One has been clicked!");
//            Toast.makeText(getContext(), "First switch clicked!", Toast.LENGTH_SHORT ).show();

            return true;
        });
    }
}
