package com.example.tragic.irate.simple.stopwatch.Miscellaneous;

import android.content.Context;

import com.example.tragic.irate.simple.stopwatch.R;

import java.util.ArrayList;
import java.util.List;

public class TDEEChosenActivitySpinnerValues {

    Context mContext;
    public ArrayList<String> category_list;
    public ArrayList<String[]> subCategoryListOfStringArrays;
    public ArrayList<String[]> subValueListOfStringArrays;

    String[] bicycling;
    String[] conditioning;
    String[] dancing;
    String[] fishing;
    String[] home;
    String[] garden;
    String[] misc;
    String[] music;
    String[] occupational;
    String[] running;
    String[] self_care;
    String[] sexual;
    String[] sports;
    String[] walking;
    String[] water;
    String[] winter;

    String[] bicycling_values;
    String[] conditioning_values;
    String[] dancing_values;
    String[] fishing_values;
    String[] home_values;
    String[] garden_values;
    String[] misc_values;
    String[] music_values;
    String[] occupational_values;
    String[] running_values;
    String[] self_care_values;
    String[] sexual_values;
    String[] sports_values;
    String[] walking_values;
    String[] water_values;
    String[] winter_values;

    public TDEEChosenActivitySpinnerValues(Context context) {
        this.mContext = context;
        populateCategoryList();
        populateSubCategoryLists();
    }

    private void populateCategoryList() {
        category_list = new ArrayList<>();

        category_list.add(mContext.getString(R.string.bicycling));
        category_list.add(mContext.getString(R.string.conditioning));
        category_list.add(mContext.getString(R.string.dancing));
        category_list.add(mContext.getString(R.string.fishing));
        category_list.add(mContext.getString(R.string.home));
        category_list.add(mContext.getString(R.string.garden));
        category_list.add(mContext.getString(R.string.misc));
        category_list.add(mContext.getString(R.string.music));
        category_list.add(mContext.getString(R.string.occupational));
        category_list.add(mContext.getString(R.string.running));
        category_list.add(mContext.getString(R.string.self_care));
        category_list.add(mContext.getString(R.string.sexual));
        category_list.add(mContext.getString(R.string.sports));
        category_list.add(mContext.getString(R.string.walking));
        category_list.add(mContext.getString(R.string.water));
        category_list.add(mContext.getString(R.string.winter));
    }

    private void populateSubCategoryLists() {
        bicycling = mContext.getResources().getStringArray(R.array.bicycling);
        conditioning = mContext.getResources().getStringArray(R.array.conditioning);
        dancing = mContext.getResources().getStringArray(R.array.dancing);
        fishing = mContext.getResources().getStringArray(R.array.fishing);
        home = mContext.getResources().getStringArray(R.array.home);
        garden  = mContext.getResources().getStringArray(R.array.garden);
        misc = mContext.getResources().getStringArray(R.array.misc);
        music = mContext.getResources().getStringArray(R.array.music);
        occupational = mContext.getResources().getStringArray(R.array.occupation);
        running = mContext.getResources().getStringArray(R.array.running);
        self_care = mContext.getResources().getStringArray(R.array.self_care);
        sexual = mContext.getResources().getStringArray(R.array.sexual_activity);
        sports = mContext.getResources().getStringArray(R.array.sports);
        walking = mContext.getResources().getStringArray(R.array.walking);
        water = mContext.getResources().getStringArray(R.array.water);
        winter = mContext.getResources().getStringArray(R.array.winter);

        bicycling_values = mContext.getResources().getStringArray(R.array.bicycle_values);
        conditioning_values = mContext.getResources().getStringArray(R.array.conditioning_values);
        dancing_values = mContext.getResources().getStringArray(R.array.dancing_values);
        fishing_values = mContext.getResources().getStringArray(R.array.fishing_values);
        home_values = mContext.getResources().getStringArray(R.array.home_values);
        garden_values = mContext.getResources().getStringArray(R.array.garden_values);
        misc_values = mContext.getResources().getStringArray(R.array.misc_values);
        music_values = mContext.getResources().getStringArray(R.array.music_values);
        occupational_values = mContext.getResources().getStringArray(R.array.occupation_values);
        running_values = mContext.getResources().getStringArray(R.array.running_values);
        self_care_values = mContext.getResources().getStringArray(R.array.self_care_values);
        sexual_values = mContext.getResources().getStringArray(R.array.sex_values);
        sports_values = mContext.getResources().getStringArray(R.array.sports_values);
        walking_values = mContext.getResources().getStringArray(R.array.walking_values);
        water_values = mContext.getResources().getStringArray(R.array.walking_values);
        winter_values = mContext.getResources().getStringArray(R.array.winter_values);

        subCategoryListOfStringArrays = new ArrayList<>();

        subCategoryListOfStringArrays.add(bicycling);
        subCategoryListOfStringArrays.add(conditioning);
        subCategoryListOfStringArrays.add(dancing);
        subCategoryListOfStringArrays.add(fishing);
        subCategoryListOfStringArrays.add(home);
        subCategoryListOfStringArrays.add(garden);
        subCategoryListOfStringArrays.add(misc);
        subCategoryListOfStringArrays.add(music);
        subCategoryListOfStringArrays.add(occupational);
        subCategoryListOfStringArrays.add(running);
        subCategoryListOfStringArrays.add(self_care);
        subCategoryListOfStringArrays.add(sexual);
        subCategoryListOfStringArrays.add(sports);
        subCategoryListOfStringArrays.add(walking);
        subCategoryListOfStringArrays.add(water);
        subCategoryListOfStringArrays.add(winter);

        subValueListOfStringArrays = new ArrayList<>();

        subValueListOfStringArrays.add(bicycling_values);
        subValueListOfStringArrays.add(conditioning_values);
        subValueListOfStringArrays.add(dancing_values);
        subValueListOfStringArrays.add(fishing_values);
        subValueListOfStringArrays.add(home_values);
        subValueListOfStringArrays.add(garden_values);
        subValueListOfStringArrays.add(misc_values);
        subValueListOfStringArrays.add(music_values);
        subValueListOfStringArrays.add(occupational_values);
        subValueListOfStringArrays.add(running_values);
        subValueListOfStringArrays.add(self_care_values);
        subValueListOfStringArrays.add(sexual_values);
        subValueListOfStringArrays.add(sports_values);
        subValueListOfStringArrays.add(walking_values);
        subValueListOfStringArrays.add(water_values);
        subValueListOfStringArrays.add(winter_values);
    }
}
