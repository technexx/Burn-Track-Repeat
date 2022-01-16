package com.example.tragic.irate.simple.stopwatch.SettingsFragments;

import android.content.Context;
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

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.AutoScrollHelper;
import androidx.fragment.app.Fragment;

import com.example.tragic.irate.simple.stopwatch.R;

import java.util.ArrayList;
import java.util.List;

public class tdeeSettingsFragment extends Fragment {

    private boolean isMetric;
    Spinner gender_spinner;
    Spinner age_spinner;
    Spinner weight_spinner;
    Spinner height_spinner;

    List<String> genderList;
    List<String> ageList;
    List<String> weightList;
    List<String> heightList;

    int AGE = 0;
    int WEIGHT = 1;
    int HEIGHT = 2;

    TextView bmrTextView;

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.tdee_settings_fragment_layout, container, false);

        gender_spinner = root.findViewById(R.id.gender_spinner);
        age_spinner = root.findViewById(R.id.age_spinner);
        weight_spinner = root.findViewById(R.id.weight_spinner);
        height_spinner = root.findViewById(R.id.height_spinner);
        bmrTextView = root.findViewById(R.id.bmr);

        Button imperialSettingButton = root.findViewById(R.id.imperial_setting);
        Button metricSettingButton = root.findViewById(R.id.metric_setting);
        Button saveTdeeSettingsButton = root.findViewById(R.id.save_tdee_settings_button);

        imperialSettingButton.setText(R.string.imperial);
        metricSettingButton.setText(R.string.metric);
        saveTdeeSettingsButton.setText(R.string.save);

        genderList = new ArrayList<>();
        ageList = new ArrayList<>();
        weightList = new ArrayList<>();
        heightList = new ArrayList<>();

        populateSpinnerListsWithStrings();

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(getContext(), R.layout.tdee_settings_spinner_layout, genderList);
        ArrayAdapter<String> ageAdapter = new ArrayAdapter<>(getContext(), R.layout.tdee_settings_spinner_layout, ageList);
        ArrayAdapter<String> weightAdapter = new ArrayAdapter<>(getContext(), R.layout.tdee_settings_spinner_layout, weightList);
        ArrayAdapter<String> heightAdapter = new ArrayAdapter<>(getContext(), R.layout.tdee_settings_spinner_layout, heightList);

        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        heightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        gender_spinner.setAdapter(genderAdapter);
        age_spinner.setAdapter(ageAdapter);
        weight_spinner.setAdapter(weightAdapter);
        height_spinner.setAdapter(heightAdapter);


        imperialSettingButton.setOnClickListener(v -> {
            //Todo: (A) age/weight etc. String List gets set w/ proper append and (B) Adapter list gets cleared/readded and (C) notifyDataSet is called or adapter is re-instantiated.
        });

        metricSettingButton.setOnClickListener(v -> {

        });

        saveTdeeSettingsButton.setOnClickListener(v -> {
            //Todo: SharedPref here that will be accessed in each cycle.
            bmrTextView.setText(getString(R.string.bmr_value, String.valueOf(calculateBMR())));
        });

        return root;
    }

    public int getIntegerValueFromSpinner(Spinner spinner) {
        return (int) spinner.getSelectedItem();
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

    public String getAppendingStringForSpinnerList(int spinnerValue, int typeOfStat) {
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

    public void populateSpinnerListsWithStrings() {
        genderList.add(getString(R.string.male));
        genderList.add(getString(R.string.female));

        for (int i = 18; i < 101; i++) {
            ageList.add(i + " " + "years");
        }

        if (isMetric) {
            for (int i = 45; i < 151; i++) {
                weightList.add((getAppendingStringForSpinnerList(i, WEIGHT)));
            }
            for (int i = 121; i < 250; i++) {
                heightList.add(getAppendingStringForSpinnerList(i, HEIGHT));
            }
        } else {
            for (int i = 101; i < 300; i++) {
                weightList.add((getAppendingStringForSpinnerList( i, WEIGHT)));
            }
            for (int i = 48; i<99; i++) {
                heightList.add(getAppendingStringForSpinnerList(i, HEIGHT));
            }
        }
    }
}
