package com.kozzztya.cycletraining;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.support.v4.preference.PreferenceFragment;
import android.widget.Toast;

import com.kozzztya.cycletraining.utils.DatabaseBackupUtils;

import java.io.IOException;

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
        if (preference.getKey().equals(Preferences.PREF_KEY_BACKUP)) {
            backup();
            return true;
        }
        return false;
    }

    /**
     * DB backup with result toast.
     */
    private void backup() {
        String backupResult;
        try {
            DatabaseBackupUtils.backupDatabase(getActivity());
            backupResult = getString(R.string.toast_backup_successful);
        } catch (IOException e) {
            e.printStackTrace();
            backupResult = getString(R.string.toast_backup_failed);
        }

        Toast.makeText(getActivity(), backupResult, Toast.LENGTH_SHORT).show();
    }
}