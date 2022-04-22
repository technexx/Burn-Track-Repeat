package com.example.tragic.irate.simple.stopwatch.SettingsFragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.AutoScrollHelper;
import androidx.fragment.app.Fragment;

import com.example.tragic.irate.simple.stopwatch.R;

import java.util.ArrayList;
import java.util.List;

public class tdeeSettingsFragment extends Fragment {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor prefEdit;

    Button imperialSettingButton;
    Button metricSettingButton;

    private boolean isMetric;
    Spinner gender_spinner;
    Spinner age_spinner;
    Spinner weight_spinner;
    Spinner height_spinner;

    List<String> genderList;
    List<String> ageList;
    List<String> weightList;
    List<String> heightList;

    ArrayAdapter<String> genderAdapter;
    ArrayAdapter<String> ageAdapter;
    ArrayAdapter<String> weightAdapter;
    ArrayAdapter<String> heightAdapter;

    int WEIGHT = 1;
    int HEIGHT = 2;

    TextView bmrTextView;

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.tdee_settings_fragment_layout, container, false);

        sharedPreferences = getActivity().getSharedPreferences("pref", 0);
        prefEdit = sharedPreferences.edit();

        gender_spinner = root.findViewById(R.id.gender_spinner);
        age_spinner = root.findViewById(R.id.age_spinner);
        weight_spinner = root.findViewById(R.id.weight_spinner);
        height_spinner = root.findViewById(R.id.height_spinner);
        bmrTextView = root.findViewById(R.id.bmr);

        imperialSettingButton = root.findViewById(R.id.imperial_setting);
        metricSettingButton = root.findViewById(R.id.metric_setting);
        Button saveTdeeSettingsButton = root.findViewById(R.id.save_tdee_settings_button);

        imperialSettingButton.setText(R.string.imperial);
        metricSettingButton.setText(R.string.metric);
        saveTdeeSettingsButton.setText(R.string.update);

        genderList = new ArrayList<>();
        ageList = new ArrayList<>();
        weightList = new ArrayList<>();
        heightList = new ArrayList<>();

        populateAllSpinnerStringLists();

        genderAdapter = new ArrayAdapter<>(getContext(), R.layout.tdee_settings_spinner_layout, genderList);
        ageAdapter = new ArrayAdapter<>(getContext(), R.layout.tdee_settings_spinner_layout, ageList);
        weightAdapter = new ArrayAdapter<>(getContext(), R.layout.tdee_settings_spinner_layout, weightList);
        heightAdapter = new ArrayAdapter<>(getContext(), R.layout.tdee_settings_spinner_layout, heightList);

        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        heightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        gender_spinner.setAdapter(genderAdapter);
        age_spinner.setAdapter(ageAdapter);
        weight_spinner.setAdapter(weightAdapter);
        height_spinner.setAdapter(heightAdapter);

        retrieveAndSetSpinnerValues();
        bmrTextView.setText(calculatedBMRString());

        imperialSettingButton.setOnClickListener(v -> {
            toggleMetricAndImperial(false);
        });

        metricSettingButton.setOnClickListener(v -> {
            toggleMetricAndImperial(true);
        });

        //Todo: BMR should calculate on any spinner selection, not just "Save". Need listeners on all Spinners AND a conditional for imperial/metric. Other option: "Save" becomes "Update", moved underneath "Daily BMR", and updates on that click.
        saveTdeeSettingsButton.setOnClickListener(v -> {
            saveSpinnerStatsToSharedPreferences();

            bmrTextView.setText(calculatedBMRString());
            Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT).show();
        });

        return root;
    }

    private String calculatedBMRString() {
        return getString(R.string.bmr_value, String.valueOf(calculateBMR()));
    }

    private void toggleMetricAndImperial(boolean onMetric) {
        if (onMetric) {
            isMetric = true;
            imperialSettingButton.setAlpha(0.5f);
            metricSettingButton.setAlpha(1.0f);
        } else {
            isMetric = false;
            imperialSettingButton.setAlpha(1.0f);
            metricSettingButton.setAlpha(0.5f);
        }

        clearAndRepopulateWeightAndHeighteSpinnerAdapters();
        clearAndRepopulateWeightAndHeightSpinnerList();
        refreshWeightAndHeightSpinnerAdapters();
    }

    private void saveSpinnerStatsToSharedPreferences() {
        prefEdit.putBoolean("isMetric", isMetric);
        prefEdit.putString("tdeeGender", getStringValueFromSpinner(gender_spinner));
        prefEdit.putInt("tdeeAge", getIntegerValueFromFullSpinnerString(age_spinner));
        prefEdit.putInt("tdeeWeight", getIntegerValueFromFullSpinnerString(weight_spinner));
        prefEdit.putInt("tdeeHeight", getIntegerValueFromFullSpinnerString(height_spinner));

        prefEdit.putInt("genderPosition", gender_spinner.getSelectedItemPosition());
        prefEdit.putInt("agePosition", age_spinner.getSelectedItemPosition());
        prefEdit.putInt("weightPosition", weight_spinner.getSelectedItemPosition());
        prefEdit.putInt("heightPosition", height_spinner.getSelectedItemPosition());

        prefEdit.apply();
    }

    private void retrieveAndSetSpinnerValues() {
        boolean metricPosition = sharedPreferences.getBoolean("isMetric", false);
        int genderPosition = sharedPreferences.getInt("genderPosition", 0);
        int agePosition = sharedPreferences.getInt("agePosition", 0);
        int weightPosition = sharedPreferences.getInt("weightPosition", 0);
        int heightPosition = sharedPreferences.getInt("heightPosition", 0);

        gender_spinner.setSelection(genderPosition);
        age_spinner.setSelection(agePosition);
        weight_spinner.setSelection(weightPosition);
        height_spinner.setSelection(heightPosition);
        toggleMetricAndImperial(metricPosition);
    }

    private int getIntegerValueFromFullSpinnerString(Spinner spinner) {
        String[] splitStringArray = spinner.getSelectedItem().toString().split(" ");
        return Integer.parseInt(splitStringArray[0]);
    }

    private String getStringValueFromSpinner(Spinner spinner) {
        return (String) spinner.getSelectedItem();
    }

    private void clearAndRepopulateWeightAndHeightSpinnerList() {
        weightList.clear();
        weightAdapter.clear();
        heightList.clear();
        heightAdapter.clear();

        populateWeightSpinnerStringList();
        populateHeightSpinnerStringList();
    }

    private void clearAndRepopulateWeightAndHeighteSpinnerAdapters() {
        weightAdapter.addAll(weightList);
        heightAdapter.addAll(heightList);
    }

    private void refreshWeightAndHeightSpinnerAdapters() {
        weightAdapter.notifyDataSetChanged();
        heightAdapter.notifyDataSetChanged();
    }

    private int calculateBMR() {
        int caloriesBurned = 0;

        String ageVal = age_spinner.getSelectedItem().toString();
        String weightVal = weight_spinner.getSelectedItem().toString();
        String heightVal = height_spinner.getSelectedItem().toString();

        String[] conv1 = ageVal.split(" ", 2);
        String[] conv2 = weightVal.split(" ", 2);
        String[] conv3 = heightVal.split(" ", 2);

        String conv4 = conv1[0];
        String conv5 = conv2[0];
        String conv6 = conv3[0];

        int ageConv = Integer.parseInt(conv4);
        int weightConv = Integer.parseInt(conv5);
        int heightConv = Integer.parseInt(conv6);

        double impMale = (66 + (6.2 * weightConv) + (12.7 * heightConv) - (6.76 * ageConv));
        double metMale = (66 + (13.7 * weightConv) + (5 * heightConv) - (6.76 * ageConv));
        double impFemale = (655.1 + (4.35 * weightConv) + (4.7 * heightConv) - (4.7 * ageConv));
        double metFemale = (655.1 + (9.6 * weightConv) + (1.8 * heightConv) - (4.7 * ageConv));

        if (isMetric) {
            if (gender_spinner.getSelectedItemPosition() == 0) {
                caloriesBurned = (int) Math.round(metMale);
            } else {
                caloriesBurned = (int) Math.round(metFemale);
            }
        } else {
            if (gender_spinner.getSelectedItemPosition() == 0) {
                caloriesBurned = (int) Math.round(impMale);
            } else {
                caloriesBurned = (int) Math.round(impFemale);
            }
        }

        return caloriesBurned;
    }

    private String getAppendingStringForSpinnerList(int spinnerValue, int typeOfStat) {
        String append = "";

        if (typeOfStat == WEIGHT) {
            if (isMetric) {
                append = "kg";
            } else {
                append = "lb";
            }
        }

        if (typeOfStat == HEIGHT) {
            if (isMetric) {
                append = "in";
            } else {
                append = "cm";
            }
        }

        return getString(R.string.spinner_value, String.valueOf(spinnerValue), append);
    }

    private void populateAllSpinnerStringLists() {
        populateGenderSpinnerStringList();
        populateAgeSpinnerStringList();
        populateWeightSpinnerStringList();
        populateHeightSpinnerStringList();
    }

    private void populateGenderSpinnerStringList() {
        genderList.add(getString(R.string.male));
        genderList.add(getString(R.string.female));
    }

    private void populateAgeSpinnerStringList() {

        for (int i = 18; i < 101; i++) {
            ageList.add(i + " " + "years");
        }
    }

    private void populateWeightSpinnerStringList() {
        if (isMetric) {
            for (int i = 45; i < 151; i++) {
                weightList.add((getAppendingStringForSpinnerList(i, WEIGHT)));
            }
        } else {
            for (int i = 100; i < 301; i++) {
                weightList.add((getAppendingStringForSpinnerList( i, WEIGHT)));
            }
        }
    }

    private void populateHeightSpinnerStringList() {
        if (isMetric) {
            for (int i = 120; i < 251; i++) {
                heightList.add(getAppendingStringForSpinnerList(i, HEIGHT));
            }
        } else {
            for (int i = 48; i<100; i++) {
                heightList.add(getAppendingStringForSpinnerList(i, HEIGHT));
            }
        }
    }

    private void logIntegerConvertedSpinnerValues() {
        Log.i("testSpinner", "Age spinner is " + getIntegerValueFromFullSpinnerString(age_spinner));
        Log.i("testSpinner", "Weight spinner is " + getIntegerValueFromFullSpinnerString(weight_spinner));
        Log.i("testSpinner", "Height spinner is " + getIntegerValueFromFullSpinnerString(height_spinner));

    }
}
