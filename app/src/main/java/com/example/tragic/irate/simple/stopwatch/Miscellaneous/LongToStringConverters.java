package com.example.tragic.irate.simple.stopwatch.Miscellaneous;

import android.util.Log;

import com.google.gson.internal.$Gson$Preconditions;

import java.text.DecimalFormat;

public class LongToStringConverters {
    int mTypeOfConversion;
    int PRIMARY_CONVERSION = 0;
    int TOTAL_SECONDS_CONVERSION = 1;
    int TOTAL_SECONDS_CONVERSION_WITH_MINIMUM = 2;

    public LongToStringConverters() {
    }

    public LongToStringConverters(int typeOfConversion) {
        this.mTypeOfConversion = typeOfConversion;
    }

    //Todo: 99 seconds displays as 1:99 using this.
    public String convertSecondsForStatDisplay(long seconds) {
        DecimalFormat dfOneZero = new DecimalFormat("0");
        DecimalFormat dfTwoZeros = new DecimalFormat("00");

        seconds = seconds/1000;
        long minutes = 0;
        long hours = 0;

        if (seconds >=60) {
            minutes = seconds / 60;
            seconds = seconds % 60;
        }

        if (minutes>=60) {
            hours = minutes/60;
            minutes = minutes % 60;
        }

        if (hours==0) {
            return dfOneZero.format(minutes) + ":" + dfTwoZeros.format(seconds);
        } else {
            return dfOneZero.format(hours) + ":" + dfTwoZeros.format(minutes) + ":" + dfTwoZeros.format(seconds);
        }
    }

    public String convertSecondsForEditPopUp(long seconds) {
        DecimalFormat df = new DecimalFormat("00");

        seconds = seconds/1000;
        long minutes = 0;
        long hours = 0;

        if (seconds >=60) {
            minutes = seconds / 60;
            seconds = seconds % 60;
        }

        if (minutes>=60) {
            hours = minutes/60;
            minutes = minutes % 60;
        }

        Log.i("testTime", "seconds are " + seconds);
        Log.i("testTime", "minutes are " + minutes);
        Log.i("testTime", "hours are " + hours);

        return df.format(hours) + ":" + df.format(minutes) + ":" + df.format(seconds);
    }

    public long getConvertedSeconds(long seconds) {
        if (seconds >=60) {
            seconds = seconds % 60;
        }
        return seconds;
    }
}
