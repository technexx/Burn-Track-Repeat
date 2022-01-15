package com.example.tragic.irate.simple.stopwatch.SettingsFragments;

import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.tragic.irate.simple.stopwatch.R;

import java.util.ArrayList;

public class TDEESettingsFragment_EXILED extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.tdee_settings_fragment_layout_exiled, rootKey);

        ListPreference agePreference = findPreference("age_setting");
        ListPreference heightPreference = findPreference("height_setting");
        ListPreference weightPreference = findPreference("weight_setting");

        //Todo: Length of these is only 1 as well.
        ArrayList<String> ageArray = createArrayForListPreference(18, 100);
        CharSequence[] ageCharSequence = ageArray.toArray(new CharSequence[0]);
        agePreference.setEntries(ageCharSequence);

        ArrayList<String> heightArray = createArrayForListPreference(48, 100);
        CharSequence[] heightCharSequence = heightArray.toArray(new CharSequence[0]);
        heightPreference.setEntries(heightCharSequence);

        ArrayList<String> weightArray = createArrayForListPreference(100, 301);
        CharSequence[] weightCharSequence = weightArray.toArray(new CharSequence[0]);
        weightPreference.setEntries(weightCharSequence);
    }

    public ArrayList<String> createArrayForListPreference(int startNumber, int endNumber) {
        ArrayList<String> arrayToCreate = new ArrayList<>();
        for (int i=startNumber; i<endNumber; i++) {
            arrayToCreate.add(String.valueOf(i));
        }

        return arrayToCreate;
    }
}