package com.kozzztya.cycletraining.utils;

public class RMCalc {
    public static float maxRM(float weight, int reps) {
        return weight / (1.0278f - (0.0278f * reps));
    }

    public static float maxRM(float weight, int reps, float factor) {
        return maxRM(weight, reps) * factor;
    }
}
