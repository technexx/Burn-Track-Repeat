package burn.track.repeat.SettingsFragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import burn.track.repeat.R;

public class tdeeSettingsFragment extends Fragment {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor prefEdit;
    Handler mHandler;

    Button imperialSettingButton;
    Button metricSettingButton;

    boolean metricMode;
    Spinner gender_spinner;
    Spinner age_spinner;
    Spinner weight_spinner;
    Spinner height_spinner;
    Spinner activity_level_spinner;

    List<String> genderList;
    List<String> ageList;
    List<String> weightList;
    List<String> heightList;
    List<String> activityLevelList;

    ArrayAdapter<String> genderAdapter;
    ArrayAdapter<String> ageAdapter;
    ArrayAdapter<String> weightAdapter;
    ArrayAdapter<String> heightAdapter;
    ArrayAdapter<String> activityLevelAdapter;

    int WEIGHT = 1;
    int HEIGHT = 2;

    TextView bmrTextView;

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        saveSpinnerStatsToSharedPreferences(metricMode);
        saveUpdatedBmrSettings();

//        Log.i("testTdee", "onStop called in tdee frag!");
        super.onStop();
    }

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.tdee_settings_fragment_layout, container, false);
        root.setBackgroundColor(getResources().getColor(R.color.alien_black));

        mHandler = new Handler();

        sharedPreferences = getActivity().getSharedPreferences("pref", 0);
        prefEdit = sharedPreferences.edit();

        gender_spinner = root.findViewById(R.id.gender_spinner);
        age_spinner = root.findViewById(R.id.age_spinner);
        weight_spinner = root.findViewById(R.id.weight_spinner);
        height_spinner = root.findViewById(R.id.height_spinner);
        activity_level_spinner = root.findViewById(R.id.activity_level_spinner);
        bmrTextView = root.findViewById(R.id.user_settings_calories_burned_textView);

        imperialSettingButton = root.findViewById(R.id.imperial_setting);
        metricSettingButton = root.findViewById(R.id.metric_setting);

        metricMode = sharedPreferences.getBoolean("metricMode", false);

        if (metricMode) {
            imperialSettingButton.setAlpha(0.5f);
            metricSettingButton.setAlpha(1.0f);
        } else {
            imperialSettingButton.setAlpha(1.0f);
            metricSettingButton.setAlpha(0.5f);
        }

        imperialSettingButton.setText(R.string.imperial);
        metricSettingButton.setText(R.string.metric);

        genderList = new ArrayList<>();
        ageList = new ArrayList<>();
        weightList = new ArrayList<>();
        heightList = new ArrayList<>();
        activityLevelList = new ArrayList<>();

        populateGenderSpinnerStringList();
        populateAgeSpinnerStringList();
        populateWeightSpinnerStringList();
        populateHeightSpinnerStringList();
        populateActivityLevelStringList();

        genderAdapter = new ArrayAdapter<>(getContext(), R.layout.tdee_settings_spinner_layout, genderList);
        ageAdapter = new ArrayAdapter<>(getContext(), R.layout.tdee_settings_spinner_layout, ageList);
        weightAdapter = new ArrayAdapter<>(getContext(), R.layout.tdee_settings_spinner_layout, weightList);
        heightAdapter = new ArrayAdapter<>(getContext(), R.layout.tdee_settings_spinner_layout, heightList);
        activityLevelAdapter = new ArrayAdapter<>(getContext(), R.layout.tdee_settings_spinner_layout, activityLevelList);

        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        heightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        gender_spinner.setOnItemSelectedListener(spinnerClickListener());
        age_spinner.setOnItemSelectedListener(spinnerClickListener());
        weight_spinner.setOnItemSelectedListener(spinnerClickListener());
        height_spinner.setOnItemSelectedListener(spinnerClickListener());
        activity_level_spinner.setOnItemSelectedListener(spinnerClickListener());

        gender_spinner.setAdapter(genderAdapter);
        age_spinner.setAdapter(ageAdapter);
        weight_spinner.setAdapter(weightAdapter);
        height_spinner.setAdapter(heightAdapter);
        activity_level_spinner.setAdapter(activityLevelAdapter);

        mHandler.postDelayed(()-> {
            retrieveAndSetSpinnerValues(metricMode);
        }, 75);


        imperialSettingButton.setOnClickListener(v -> {
            toggleMetricAndImperial(false);
        });

        metricSettingButton.setOnClickListener(v -> {
            toggleMetricAndImperial(true);
        });

        return root;
    }

    private AdapterView.OnItemSelectedListener spinnerClickListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bmrTextView.setText(calculatedBMRString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    private void toggleMetricAndImperial(boolean selectingMetric) {
        //This conditional prevents switches before stuff is set.
        if (selectingMetric != metricMode) {
            if (selectingMetric) {
                //Going from Imperial to Metric.
                saveSpinnerStatsToSharedPreferences(false);

                metricMode = true;
                imperialSettingButton.setAlpha(0.5f);
                metricSettingButton.setAlpha(1.0f);
            } else {
                //Going from Metric to Imperial.
                saveSpinnerStatsToSharedPreferences(true);

                metricMode = false;
                imperialSettingButton.setAlpha(1.0f);
                metricSettingButton.setAlpha(0.5f);
            }

            clearWeightAndHeightListsAndAdapters();

            populateWeightSpinnerStringList();
            populateHeightSpinnerStringList();

            refreshWeightAndHeightSpinnerAdapters();

            retrieveAndSetSpinnerValues(metricMode);
            bmrTextView.setText(calculatedBMRString());

            prefEdit.putBoolean("metricMode", metricMode);
            prefEdit.apply();
        }
    }

    private void clearWeightAndHeightListsAndAdapters() {
        weightList.clear();
        weightAdapter.clear();
        heightList.clear();
        heightAdapter.clear();
    }

    private void refreshWeightAndHeightSpinnerAdapters() {
        weightAdapter.notifyDataSetChanged();
        heightAdapter.notifyDataSetChanged();
    }

    public void saveSpinnerStatsToSharedPreferences(boolean savingMetric) {
        if (savingMetric) {
            prefEdit.putInt("weightPositionMetric", weight_spinner.getSelectedItemPosition());
            prefEdit.putInt("heightPositionMetric", height_spinner.getSelectedItemPosition());
            prefEdit.putInt("activityLevelPositionMetric", activity_level_spinner.getSelectedItemPosition());

            Log.i("testTdee", "metric saving of spinner positions");

        } else {
            prefEdit.putInt("weightPositionImperial", weight_spinner.getSelectedItemPosition());
            prefEdit.putInt("heightPositionImperial", height_spinner.getSelectedItemPosition());
            prefEdit.putInt("activityLevelPositionImperial", activity_level_spinner.getSelectedItemPosition());

            Log.i("testTdee", "imperial saving of spinner positions");
        }

        prefEdit.putString("tdeeGender", getStringValueFromSpinner(gender_spinner));
        prefEdit.putInt("tdeeAge", getIntegerValueFromFullSpinnerString(age_spinner));
        prefEdit.putInt("tdeeWeight", getIntegerValueFromFullSpinnerString(weight_spinner));
        prefEdit.putInt("tdeeHeight", getIntegerValueFromFullSpinnerString(height_spinner));

        int activityLevelPosition = activity_level_spinner.getSelectedItemPosition();
        String activityLevelString = getActivityLevelString(activityLevelPosition);

        prefEdit.putInt("activityLevelPosition", activityLevelPosition);
        prefEdit.putString("activityLevelString", activityLevelString);

        prefEdit.apply();
    }

    private void saveUpdatedBmrSettings() {
        prefEdit.putInt("savedBmr", calculateBMR());
        prefEdit.apply();
    }

    private void retrieveAndSetSpinnerValues(boolean isMetric) {
        int weightPosition;
        int heightPosition;
        int activityLevelPosition;

        if (isMetric) {
            weightPosition = sharedPreferences.getInt("weightPositionMetric", 25);
            heightPosition = sharedPreferences.getInt("heightPositionMetric", 60);

            Log.i("testTdee", "metric retrieval of spinner positions");

        } else {
            weightPosition = sharedPreferences.getInt("weightPositionImperial", 50);
            heightPosition = sharedPreferences.getInt("heightPositionImperial", 24);

            Log.i("testTdee", "imperial retrieval of spinner positions");

        }

        weight_spinner.setSelection(weightPosition);
        height_spinner.setSelection(heightPosition);

        activityLevelPosition = sharedPreferences.getInt("activityLevelPosition", 3);
        activity_level_spinner.setSelection(activityLevelPosition);
    }

    private int getIntegerValueFromFullSpinnerString(Spinner spinner) {
        String[] splitStringArray = spinner.getSelectedItem().toString().split(" ");
        return Integer.parseInt(splitStringArray[0]);
    }

    private String getStringValueFromSpinner(Spinner spinner) {
        return (String) spinner.getSelectedItem();
    }

    private String calculatedBMRString() {
        return getString(R.string.bmr_value, String.valueOf(calculateBMR()));
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

        if (metricMode) {
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

        int activityLevelPosition = activity_level_spinner.getSelectedItemPosition();
        double activityLevelMultiplier = setActivityLevelMultiplier(activityLevelPosition);

        caloriesBurned = Math.round((int) (caloriesBurned * activityLevelMultiplier));

        return caloriesBurned;
    }

    private String getActivityLevelString(int position) {
        String stringToReturn = "";

        switch (position) {
            case 0:
                stringToReturn = getString(R.string.act_0);
                break;
            case 1:
                stringToReturn = getString(R.string.act_1);
                break;
            case 2:
                stringToReturn = getString(R.string.act_2);
                break;
            case 3:
                stringToReturn = getString(R.string.act_3);
                break;
            case 4:
                stringToReturn = getString(R.string.act_4);
                break;
            case 5:
                stringToReturn = getString(R.string.act_5);
                break;
        }

        return stringToReturn;
    }

    private String getAppendingStringForSpinnerList(int spinnerValue, int typeOfStat) {
        String append = "";

        if (typeOfStat == WEIGHT) {
            if (metricMode) {
                append = "kg";
            } else {
                append = "lb";
            }
        }

        if (typeOfStat == HEIGHT) {
            if (metricMode) {
                append = "cm";
            } else {
                append = "in";
            }
        }

        return getString(R.string.spinner_value, String.valueOf(spinnerValue), append);
    }

    private double setActivityLevelMultiplier(int spinnerPositionSelected) {
        double multiplierToReturn = 0;

        switch (spinnerPositionSelected) {
            case 0:
                multiplierToReturn = 1;
                break;
            case 1:
                multiplierToReturn = 1.2;
                break;
            case 2:
                multiplierToReturn = 1.375;
                break;
            case 3:
                multiplierToReturn = 1.55;
                break;
            case 4:
                multiplierToReturn = 1.725;
                break;
            case 5:
                multiplierToReturn = 1.9;
                break;

        }

        return multiplierToReturn;
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
        if (metricMode) {
            for (int i = 45; i < 151; i++) {
                weightList.add((getAppendingStringForSpinnerList(i, WEIGHT)));
            }
            Log.i("testTdee", "metric population of weight list");
        } else {
            for (int i = 100; i < 301; i++) {
                weightList.add((getAppendingStringForSpinnerList( i, WEIGHT)));
            }
            Log.i("testTdee", "imperial population of weight list");

        }
    }

    private void populateHeightSpinnerStringList() {
        if (metricMode) {
            for (int i = 120; i < 251; i++) {
                heightList.add(getAppendingStringForSpinnerList(i, HEIGHT));
            }
        } else {
            for (int i = 48; i < 100; i++) {
                heightList.add(getAppendingStringForSpinnerList(i, HEIGHT));
            }
        }
    }

    private void populateActivityLevelStringList() {
        activityLevelList.add(getString(R.string.act_0));
        activityLevelList.add(getString(R.string.act_1));
        activityLevelList.add(getString(R.string.act_2));
        activityLevelList.add(getString(R.string.act_3));
        activityLevelList.add(getString(R.string.act_4));
        activityLevelList.add(getString(R.string.act_5));
    }
}