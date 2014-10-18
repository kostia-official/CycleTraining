package com.kozzztya.cycletraining.custom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.widget.Toast;
import com.kozzztya.cycletraining.Preferences;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.DatabaseHelper;
import com.kozzztya.cycletraining.utils.FileUtils;

public class RestoreDialogPreference extends DialogPreference {

    private String[] mBackupFiles;

    public RestoreDialogPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public RestoreDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void showDialog(Bundle state) {
        // Get backup files names
        mBackupFiles = FileUtils.getDirectoryFileNames(DatabaseHelper.BACKUP_DIR);
        if (mBackupFiles == null) {
            // Show error message and prevent call super.showDialog()
            Toast.makeText(getContext(), getContext().getString(R.string.toast_no_backup_files), Toast.LENGTH_SHORT).show();
            return;
        }
        super.showDialog(state);
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);
        if (mBackupFiles != null) {
            builder.setItems(mBackupFiles, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Restore selected backup file
                    DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getContext());
                    String restoredFile = mBackupFiles[which];
                    databaseHelper.restore(restoredFile);

                    // Notify onSharedPreferenceChanged
                    PreferenceManager.getDefaultSharedPreferences(getContext())
                            .edit().putString(Preferences.PREF_KEY_RESTORE, restoredFile)
                            .commit();
                }
            });
            builder.setPositiveButton(null, null);
        }
    }
}
