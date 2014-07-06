package com.kozzztya.cycletraining.utils;

import java.text.DecimalFormat;

public class RMUtils {

    public static final int REPS_MAX = 0;

    public static float maxRM(float weight, int reps) {
        return weight / (1.0278f - (0.0278f * reps));
    }

    public static float maxRM(float weight, int reps, float factor) {
        return maxRM(weight, reps) * factor;
    }

    public static float roundTo(float weight, float roundValue) {
        return Math.round(weight / roundValue) * roundValue;
    }

    public static String weightFormat(float weight) {
        return new DecimalFormat("#.##").format(weight);
    }
}