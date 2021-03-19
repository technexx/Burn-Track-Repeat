package com.example.tragic.irate.simple.stopwatch;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;

public class NumberFilter implements InputFilter {

    public int min;
    public int max;

    public NumberFilter (int minVal, int maxVal) {
        this.min = minVal; this.max = maxVal;
    }

    @Override
    public CharSequence filter(CharSequence source, int i, int i1, Spanned dest, int start, int end) {
        try {
            int input = Integer.parseInt(dest.toString() + source.toString());
            if (inRange(min, max, input))
                return null;
        } catch (NumberFormatException e) {
            Log.e("NFE", "Range inapplicable");
        }
        return "";
    }

    private boolean inRange( int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}