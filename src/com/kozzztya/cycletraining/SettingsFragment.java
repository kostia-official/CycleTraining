package com.kozzztya.cycletraining;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.support.v4.preference.PreferenceFragment;

import com.kozzztya.cycletraining.db.DatabaseHelper;

public class SettingsFragment extends PreferenceFragment implements OnPreferenceClickListener {
    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        addPreferencesFromResource(R.xml.preferences);

        Preference backup = findPreference(Preferences.PREF_KEY_BACKUP);
        backup.setOnPreferenceClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.action_settings);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        DatabaseHelper.getInstance(getActivity()).backup();
        return true;
    }
}