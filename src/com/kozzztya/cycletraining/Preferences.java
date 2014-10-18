package com.kozzztya.cycletraining;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

public class Preferences {

    public static final String PREF_KEY_TIMER_VALUE = "pref_key_timer_value";
    public static final String PREF_KEY_VIBRATE_TIMER = "pref_key_is_vibrate_timer";
    public static final String PREF_KEY_FIRST_DAY_OF_WEEK = "pref_key_first_day_of_week";
    public static final String PREF_KEY_IS_FIRST_RUN = "pref_key_is_first_run";
    public static final String PREF_KEY_RESTORE = "pref_key_restore";
    public static final String PREF_KEY_BACKUP = "pref_key_backup";

    private SharedPreferences mPreferences;

    public Preferences(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        mPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        mPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public int getTimerValue() {
        return Integer.valueOf(mPreferences.getString(PREF_KEY_TIMER_VALUE, "90"));
    }

    public boolean isVibrateTimer() {
        return mPreferences.getBoolean(PREF_KEY_VIBRATE_TIMER, false);
    }

    public int getFirstDayOfWeek() {
        return Integer.valueOf(mPreferences.getString(PREF_KEY_FIRST_DAY_OF_WEEK, "2"));
    }

    public boolean isFirstRun() {
        boolean isFirstRun = mPreferences.getBoolean(PREF_KEY_IS_FIRST_RUN, true);
        if (isFirstRun) {
            Editor editor = mPreferences.edit();
            editor.putBoolean(PREF_KEY_IS_FIRST_RUN, false);
            editor.commit();
        }
        return isFirstRun;
    }
}
