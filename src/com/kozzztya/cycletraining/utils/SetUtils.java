package com.kozzztya.cycletraining.utils;

import android.content.Context;
import com.kozzztya.cycletraining.R;

import java.text.DecimalFormat;

public class SetUtils {

    public static final int REPS_MAX = 0;
    public static final int REPS_RO = -1;
    public static final int REPS_F = -2;

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

    public static String repsFormat(int reps, Context context) {
        switch (reps) {
            case REPS_MAX:
                return context.getString(R.string.reps_max);
            case REPS_RO:
                return context.getString(R.string.reps_ro);
            case REPS_F:
                return context.getString(R.string.reps_f);
        }
        return String.valueOf(reps);
    }
}
