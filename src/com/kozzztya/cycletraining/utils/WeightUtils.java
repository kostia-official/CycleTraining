package com.kozzztya.cycletraining.utils;

public class WeightUtils {

    public static final int MAX = 0;

    public static float maxRM(float weight, int reps) {
        return weight / (1.0278f - (0.0278f * reps));
    }

    public static float maxRM(float weight, int reps, float factor) {
        return maxRM(weight, reps) * factor;
    }

    public static int roundTo(double i, int v){
        return (int) (Math.round(i/v) * v);
    }
}
