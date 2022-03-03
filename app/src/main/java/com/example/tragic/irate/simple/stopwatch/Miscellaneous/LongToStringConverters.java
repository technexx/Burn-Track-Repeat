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

    public String convertSeconds(long totalSeconds) {
        DecimalFormat df = new DecimalFormat("00");
        long minutes;
        long remainingSeconds;
        totalSeconds = totalSeconds/1000;

        if (totalSeconds >=60) {
            minutes = totalSeconds/60;
            remainingSeconds = totalSeconds % 60;
            return (minutes + ":" + df.format(remainingSeconds));
        } else if (totalSeconds >=10) return "0:" + totalSeconds;
        else return "0:0" + totalSeconds;
    }

    public String convertSecondsTwo(long totalSeconds) {
        DecimalFormat df = new DecimalFormat("00");
        long minutes;
        long remainingSeconds;
        totalSeconds = totalSeconds/1000;

        if (totalSeconds >=60) {
            minutes = totalSeconds/60;
            remainingSeconds = totalSeconds % 60;
            return (minutes + ":" + df.format(remainingSeconds));
        } else {
            if (mTypeOfConversion==PRIMARY_CONVERSION) {
                if (totalSeconds >=10) {
                    return "0:" + totalSeconds;
                } else {
                    return "0:0" + totalSeconds;
                }
            }
            else if (mTypeOfConversion==TOTAL_SECONDS_CONVERSION) {
                return String.valueOf(totalSeconds);
            }
            else if (mTypeOfConversion==TOTAL_SECONDS_CONVERSION_WITH_MINIMUM) {
                if (totalSeconds != 5) {
                    return String.valueOf(totalSeconds);
                } else {
                    return "5";
                }
            } else {
                return String.valueOf(totalSeconds);
            }
        }
    }
}
