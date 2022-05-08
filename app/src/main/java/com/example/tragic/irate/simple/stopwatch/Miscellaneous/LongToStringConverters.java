package com.example.tragic.irate.simple.stopwatch.Miscellaneous;

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

    public String convertSecondsForStatDisplay(long seconds) {
        DecimalFormat dfOneZero = new DecimalFormat("0");
        DecimalFormat dfTwoZeros = new DecimalFormat("00");

        seconds = seconds/1000;
        long minutes = 0;
        long hours = 0;

        if (seconds >=60) {
            minutes = seconds / 60;
        }

        if (minutes>=60) {
            hours = minutes/60;
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
        }

        if (minutes>=60) {
            hours = minutes/60;
        }

        return df.format(hours) + ":" + df.format(minutes) + ":" + df.format(seconds);
    }
}
