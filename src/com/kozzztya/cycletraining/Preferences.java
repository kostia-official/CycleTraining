package com.kozzztya.cycletraining;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MenuItem;

public class Preferences extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;

    }

    public static int getFirstDayOfWeek(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.valueOf(sharedPrefs.getString("pref_key_first_day_of_week", "2"));
    }

    public static boolean isFirstRun(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isFirstRun = sharedPrefs.getBoolean("FIRSTRUN", true);
        if (isFirstRun) {
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean("FIRSTRUN", false);
            editor.commit();
        }
        return isFirstRun;
    }
}
