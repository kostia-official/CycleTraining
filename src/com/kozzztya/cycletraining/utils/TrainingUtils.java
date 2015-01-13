package com.kozzztya.cycletraining.utils;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.kozzztya.cycletraining.MainActivity;
import com.kozzztya.cycletraining.Preferences;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.DatabaseProvider;
import com.kozzztya.cycletraining.db.Mesocycles;
import com.kozzztya.cycletraining.db.Trainings;
import com.kozzztya.cycletraining.trainingcreate.TrainingPlanFragment;
import com.kozzztya.cycletraining.trainingjournal.TrainingCalendarFragment;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class TrainingUtils {

    private static final String TAG = "log" + TrainingUtils.class.getSimpleName();

    private Context mContext;
    private ContentResolver mContentResolver;

    public TrainingUtils(Context context) {
        mContext = context;
        mContentResolver = mContext.getContentResolver();
    }

    /**
     * Show the dialog with actions of training.
     *
     * @param trainingValues The data of selected training.
     */
    public void showActionsDialog(final ContentValues trainingValues) {
        new AlertDialog.Builder(mContext)
                .setItems(R.array.training_actions, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        long mesocycleId = trainingValues.getAsLong(Trainings.MESOCYCLE);
                        Date trainingDate = Date.valueOf(trainingValues.getAsString(Trainings.DATE));

                        switch (which) {
                            case 0:
                                showDeleteDialog(mesocycleId);
                                break;
                            case 1:
                                showTrainingPlan(mesocycleId);
                                break;
                            case 2:
                                showMoveDialog(mesocycleId, trainingDate);
                                break;
                        }
                    }
                })
                .show();
    }

    /**
     * Show the dialog for new training date choosing.
     *
     * @param mesocycleId The mesocycle of the training.
     * @param oldDate     The old date of the training.
     */
    public void showMoveDialog(final long mesocycleId, final Date oldDate) {
        final TrainingCalendarFragment dialogCaldroidFragment = new TrainingCalendarFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CaldroidFragment.DIALOG_TITLE, mContext.getString(R.string.date_dialog_title));
        bundle.putInt(CaldroidFragment.START_DAY_OF_WEEK, new Preferences(mContext).getFirstDayOfWeek());
        dialogCaldroidFragment.setArguments(bundle);

        dialogCaldroidFragment.setCaldroidListener(new CaldroidListener() {
            @Override
            public void onSelectDate(java.util.Date date, View view) {
                move(mesocycleId, oldDate, new Date(date.getTime()));
                dialogCaldroidFragment.dismiss();
            }
        });

        dialogCaldroidFragment.show(((FragmentActivity) mContext).getSupportFragmentManager(),
                TrainingCalendarFragment.class.getSimpleName());
    }

    /**
     * Show dialog with different variants deleting of the training.
     *
     * @param mesocycleId The mesocycle of the training.
     */
    public void showDeleteDialog(final long mesocycleId) {
        new AlertDialog.Builder(mContext)
                .setTitle(R.string.on_delete_title)
                .setItems(R.array.delete_actions, new OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0)
                                    fullDelete(mesocycleId);
                                else if (which == 1)
                                    deleteOnlyUndone(mesocycleId);
                            }
                        }
                )
                .show();
    }

    /**
     * Delete all trainings of mesocycle.
     */
    public void fullDelete(long mesocycleId) {
        // Cascade delete trainings of mesocycle.
        mContentResolver.delete(parseMesocycleUri(mesocycleId), null, null);

        // Reload view data.
        mContentResolver.notifyChange(DatabaseProvider.TRAININGS_VIEW_URI, null);
    }

    /**
     * Delete only undone trainings.
     *
     * @param mesocycleId The mesocycle of the training.
     */
    public void deleteOnlyUndone(long mesocycleId) {
        String where = Trainings.IS_DONE + " = 0 AND " +
                Trainings.MESOCYCLE + " = " + mesocycleId;

        mContentResolver.delete(DatabaseProvider.TRAININGS_URI, where, null);

        // Reload view data.
        mContentResolver.notifyChange(DatabaseProvider.TRAININGS_VIEW_URI, null);
    }

    /**
     * Move training to another day of the week.
     *
     * @param mesocycleId The mesocycle of the training.
     * @param oldDate     The old date of the training.
     * @param newDate     The new date of the training.
     */
    public void move(long mesocycleId, Date oldDate, Date newDate) {
        String[] projection = new String[]{Trainings._ID};                          // Select all trainings
        String selection = Trainings.DATE + " >= " + DateUtils.sqlFormat(oldDate) + // from old date
                " AND " + Trainings.MESOCYCLE + " = " + mesocycleId;                // with chosen mesocycle.
        Cursor trainingsCursor = mContentResolver.query(DatabaseProvider.TRAININGS_URI,
                projection, selection, null, null);

        // Select how many trainings in week has current mesocycle.
        projection = new String[]{Mesocycles._ID, Mesocycles.TRAININGS_IN_WEEK};
        Cursor mesocycleCursor = mContentResolver.query(parseMesocycleUri(mesocycleId), projection, null, null, null);
        int trainingsInWeek = mesocycleCursor.moveToFirst() ? mesocycleCursor.getInt(
                mesocycleCursor.getColumnIndex(Mesocycles.TRAININGS_IN_WEEK)) : 1;
        mesocycleCursor.close();

        if (trainingsCursor != null && trainingsCursor.moveToFirst()) {
            do {
                // Calc the new trainings dates.
                long trainingId = trainingsCursor.getLong(trainingsCursor.getColumnIndex(
                        Trainings._ID));
                long trainingDate = DateUtils.calcTrainingDate(trainingsCursor.getPosition(),
                        trainingsInWeek, newDate);

                ContentValues values = new ContentValues();
                values.put(Trainings.DATE, new SimpleDateFormat("yyyy-MM-dd").format(trainingDate));

                Uri trainingUri = DatabaseProvider.uriParse(Trainings.TABLE_NAME, trainingId);
                mContentResolver.update(trainingUri, values, null, null);

                // Reload view data.
                mContentResolver.notifyChange(DatabaseProvider.TRAININGS_VIEW_URI, null);
            } while (trainingsCursor.moveToNext());
            trainingsCursor.close();
        }
    }

    /**
     * Show full training plan of the training.
     *
     * @param mesocycleId The mesocycle of the training.
     */
    public void showTrainingPlan(long mesocycleId) {
        Uri mesocycleUri = DatabaseProvider.uriParse(Mesocycles.TABLE_NAME, mesocycleId);

        Fragment fragment = TrainingPlanFragment.newInstance(mesocycleUri);
        ((MainActivity) mContext).startFragment(fragment);
    }

    /**
     * Quick parse of mesocycleUri.
     */
    private Uri parseMesocycleUri(long mesocycleId) {
        return DatabaseProvider.uriParse(Mesocycles.TABLE_NAME, mesocycleId);
    }
}
