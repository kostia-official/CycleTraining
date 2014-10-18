package com.kozzztya.cycletraining;

import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import com.kozzztya.cycletraining.db.DatabaseHelper;

public class MyPreferenceActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            getActionBar().setDisplayHomeAsUpEnabled(true);

        Preference backup = findPreference(Preferences.PREF_KEY_BACKUP);
        backup.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                DatabaseHelper.getInstance(getApplicationContext()).backup();
                return true;
            }
        });
    }

    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return true;
    }
}