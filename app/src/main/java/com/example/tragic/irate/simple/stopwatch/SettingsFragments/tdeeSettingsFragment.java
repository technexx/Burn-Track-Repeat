package com.example.tragic.irate.simple.stopwatch.SettingsFragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.tragic.irate.simple.stopwatch.R;

import java.util.ArrayList;
import java.util.List;

public class tdeeSettingsFragment extends Fragment {

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        String ageVal;
        String weightVal;
        String heightVal;

        int ageConv;
        int weightConv;
        int heightConv;

        boolean isMetric = false;
        double impMale ;
        double impFemale;
        double metMale;
        double metFemale;

        View root = inflater.inflate(R.layout.tdee_settings_fragment_layout, container, false);

        Bundle args = getArguments();
        if (args != null) {
            isMetric = args.getBoolean("isMetric");
        }

        final Spinner gender_spinner = root.findViewById(R.id.gender_spinner);
        final Spinner age_spinner = root.findViewById(R.id.age_spinner);
        final Spinner weight_spinner = root.findViewById(R.id.weight_spinner);
        final Spinner height_spinner = root.findViewById(R.id.height_spinner);
        final TextView bmr = root.findViewById(R.id.bmr);

        final Button imperialSettingButton = root.findViewById(R.id.imperial_setting);
        final Button metricSettingButton = root.findViewById(R.id.metric_setting);
        final Button saveTdeeSettingsButton = root.findViewById(R.id.save_tdee_settings_button);

        imperialSettingButton.setText(R.string.imperial);
        metricSettingButton.setText(R.string.metric);
        saveTdeeSettingsButton.setText(R.string.save);

        final List<String> gender = new ArrayList<>();
        final List<String> age = new ArrayList<>();
        List<String> weight = new ArrayList<>();
        final List<String> height = new ArrayList<>();

        gender.add(getString(R.string.male));
        gender.add(getString(R.string.female));

        for (int i=18; i<101; i++) {
            age.add(i + " " + "years");
        }

        if (isMetric) {
            for (int i=1; i<151; i++){
                weight.add((i) + " "  + "kg");
            }
            for (int i=1; i<201; i++) {
                height.add(i + " " + "cm");
            }
        } else {
            for (int i=1; i<301; i++) {
                weight.add(i + " " + "lb");
            }
            for (int i=1; i<101; i++) {
                height.add(i + " " + "inches");
            }
        }

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(getContext(), R.layout.tdee_settings_spinner_layout, gender);
        ArrayAdapter<String> ageAdapter = new ArrayAdapter<>(getContext(), R.layout.tdee_settings_spinner_layout, age);
        ArrayAdapter<String> weightAdapter = new ArrayAdapter<>(getContext(), R.layout.tdee_settings_spinner_layout, weight);
        ArrayAdapter<String> heightAdapter = new ArrayAdapter<>(getContext(), R.layout.tdee_settings_spinner_layout, height);

        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        heightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        gender_spinner.setAdapter(genderAdapter);
        age_spinner.setAdapter(ageAdapter);
        weight_spinner.setAdapter(weightAdapter);
        height_spinner.setAdapter(heightAdapter);

        age_spinner.setSelection(17);
        weight_spinner.setSelection(149);
        height_spinner.setSelection(59);


        ageVal = age_spinner.getSelectedItem().toString();
        weightVal = weight_spinner.getSelectedItem().toString();
        heightVal = height_spinner.getSelectedItem().toString();

        String[] conv1 = ageVal.split(" ", 2);
        String[] conv2 = weightVal.split(" ", 2);
        String[] conv3 = heightVal.split(" ", 2);

        String conv4 = conv1[0];
        String conv5 = conv2[0];
        String conv6 = conv3[0];

        ageConv = Integer.parseInt(conv4);
        weightConv = Integer.parseInt(conv5);
        heightConv = Integer.parseInt(conv6);

        impMale = ( 66 + (6.2 * weightConv) + (12.7 * heightConv) - (6.76 * ageConv));
        metMale = ( 66 + (13.7 * weightConv) + (5 * heightConv) - (6.76 * ageConv));
        impFemale = ( 655.1 + (4.35 * weightConv) + (4.7 * heightConv) - (4.7 * ageConv));
        metFemale = ( 655.1 + (9.6 * weightConv) + (1.8 * heightConv) - (4.7 * ageConv));

        double calories = 0;

        if (isMetric) {
            if (gender_spinner.getSelectedItemPosition() == 0) {
                calories = metMale;
            } else {
                calories = metFemale;
            }
        } else {
            if (gender_spinner.getSelectedItemPosition() == 0) {
                calories = impMale;
            } else {
                calories = impFemale;
            }
        }

        bmr.setText(getString(R.string.two_part, String.valueOf(Math.round(calories)), getString(R.string.calories)));

        imperialSettingButton.setOnClickListener(v-> {

        });

        metricSettingButton.setOnClickListener(v-> {

        });

        saveTdeeSettingsButton.setOnClickListener(v-> {
            //Todo: Imperial/Metric buttons at top of Fragment. SharedPref here that will be accessed in each cycle.
        });

        return root;
    }
}
