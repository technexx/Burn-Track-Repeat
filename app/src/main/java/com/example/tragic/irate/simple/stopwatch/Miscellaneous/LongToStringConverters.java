package com.example.tragic.irate.simple.stopwatch.Miscellaneous;

import android.util.Log;

import com.google.gson.internal.$Gson$Preconditions;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class LongToStringConverters {

    public String convertMillisToHourBasedStringForTimer(long millis) {
        DecimalFormat dfOneZero = new DecimalFormat("0");
        DecimalFormat dfTwoZeros = new DecimalFormat("00");
        dfOneZero.setRoundingMode(RoundingMode.DOWN);
        dfTwoZeros.setRoundingMode(RoundingMode.DOWN);

        long seconds= 0;

        seconds = millis/999;
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

    public String convertMillisToHourBasedString(long millis) {
        DecimalFormat dfOneZero = new DecimalFormat("0");
        DecimalFormat dfTwoZeros = new DecimalFormat("00");
        dfOneZero.setRoundingMode(RoundingMode.DOWN);
        dfTwoZeros.setRoundingMode(RoundingMode.DOWN);

        long seconds= 0;

        seconds = millis/1000;
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

        Log.i("testStop", "minutes are " + minutes + " and hours are " + hours);
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
        return df.format(hours) + ":" + df.format(minutes) + ":" + df.format(seconds);
    }
}
