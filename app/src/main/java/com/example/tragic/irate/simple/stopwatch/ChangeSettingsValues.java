package com.example.tragic.irate.simple.stopwatch;

public class ChangeSettingsValues {

    int SOUND_SETTINGS = 1;
    int COLOR_SETTINGS = 2;
    int ABOUT_SETTINGS = 3;

    int mSettingToChange;
    int mValueOfSetting;
    int SILENT = 1;
    int VIBRATE_ONCE = 21;
    int VIBRATE_TWICE = 22;
    int VIBRATE_THRICE = 23;
    int PLAY_RINGTONE = 3;

    public ChangeSettingsValues(int settingToChange, int valueOfSetting) {
        this.mSettingToChange = settingToChange; this.mValueOfSetting = valueOfSetting;
    }

    public long[] vibrationType(int type) {
        long[] chosenVibration = new long[0];
        if (type==21) chosenVibration = new long[]{0, 750, 300};
        if (type==22) chosenVibration = new long[]{0, 750, 300, 0, 750, 300};
        if (type==23) chosenVibration = new long[]{0, 750, 300, 0, 750, 300, 0, 750, 300};

        return chosenVibration;
    }

}