package com.kozzztya.cycletraining;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

public class Preferences {

    private SharedPreferences preferences;

    public Preferences(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        preferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public int getTimerValue() {
        return Integer.valueOf(preferences.getString("pref_key_timer_value", "90"));
    }

    public boolean isVibrateTimer() {
        return preferences.getBoolean("pref_key_is_vibrate_timer", false);
    }

    public int getFirstDayOfWeek() {
        return Integer.valueOf(preferences.getString("pref_key_first_day_of_week", "2"));
    }

    public boolean isFirstRun() {
        boolean isFirstRun = preferences.getBoolean("FIRSTRUN", true);
        if (isFirstRun) {
            Editor editor = preferences.edit();
            editor.putBoolean("FIRSTRUN", false);
            editor.commit();
        }
        return isFirstRun;
    }
}
