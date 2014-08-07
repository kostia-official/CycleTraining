package com.kozzztya.cycletraining.customviews;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.widget.Toast;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.utils.FileUtils;

public class RestoreDialogPreference extends DialogPreference {

    private String[] backupFiles;

    public RestoreDialogPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public RestoreDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void showDialog(Bundle state) {
        //Get backup files names
        backupFiles = FileUtils.getDirectoryFileNames(DBHelper.BACKUP_DIR);
        if (backupFiles == null) {
            //Show error message and prevent call super.showDialog()
            Toast.makeText(getContext(), getContext().getString(R.string.toast_no_backup_files), Toast.LENGTH_SHORT).show();
            return;
        }
        super.showDialog(state);
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);
        if (backupFiles != null) {
            builder.setItems(backupFiles, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //Restore selected backup file
                    DBHelper dbHelper = DBHelper.getInstance(getContext());
                    dbHelper.restore(backupFiles[which]);
                    dbHelper.notifyDBChanged();
                }
            });
            builder.setPositiveButton(null, null);
        }
    }
}
