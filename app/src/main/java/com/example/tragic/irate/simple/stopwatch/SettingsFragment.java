package com.example.tragic.irate.simple.stopwatch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.transition.TransitionInflater;
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

        //Todo: Callback to Main and pass boolean in.
        prefOne.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean clickedValue = (boolean) newValue;
            prefEdit.putBoolean("firstSettingBoolean", clickedValue);
            prefEdit.apply();
//            Toast.makeText(getContext(), String.valueOf(clickedValue), Toast.LENGTH_SHORT ).show();

            Bundle bundle = new Bundle();
            bundle.putBoolean("firstSettingBool", clickedValue);
            return true;
        });

        prefOne.setOnPreferenceClickListener(v-> {
            return true;
        });
    }
}
