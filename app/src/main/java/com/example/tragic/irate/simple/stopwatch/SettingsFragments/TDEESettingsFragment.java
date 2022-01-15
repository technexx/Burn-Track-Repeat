package com.example.tragic.irate.simple.stopwatch.SettingsFragments;

import android.os.Bundle;
import android.util.Log;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.tragic.irate.simple.stopwatch.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TDEESettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.tdee_settings_fragment_layout, rootKey);

        ListPreference agePreference = findPreference("age_setting");
        ListPreference heightPreference = findPreference("height_setting");
        ListPreference weightPreference = findPreference("weight_setting");
        ListPreference activityLevelPreference = findPreference("activity_level_setting");

        ArrayList<String> ageArray = createArrayForListPreference(100);
        CharSequence[] ageCharSequence = ageArray.toArray(new CharSequence[0]);
        agePreference.setEntries(ageCharSequence);
    }

    public ArrayList<String> createArrayForListPreference(int numberOfEntries) {
        ArrayList<String> arrayToCreate = new ArrayList<>();
        for (int i=18; i<numberOfEntries; i++) {
            arrayToCreate.add(String.valueOf(i));
        }

        return arrayToCreate;
    }
}