package com.kozzztya.cycletraining.trainingjournal;

import android.app.AlertDialog;
import android.content.*;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import com.kozzztya.cycletraining.Preferences;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.DatabaseProvider;
import com.kozzztya.cycletraining.db.Mesocycles;
import com.kozzztya.cycletraining.db.Trainings;
import com.kozzztya.cycletraining.trainingcreate.TrainingPlanActivity;
import com.kozzztya.cycletraining.trainingcreate.TrainingPlanFragment;
import com.kozzztya.cycletraining.utils.DateUtils;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class TrainingHandler {

    private static final String TAG = "log" + TrainingHandler.class.getSimpleName();
    private Uri mMesocycleUri;

    private Context mContext;
    private ContentValues mTrainingValues;
    private final ContentResolver mContentResolver;

    public TrainingHandler(Context context, ContentValues trainingValues) {
        mContext = context;
        mTrainingValues = trainingValues;
        mContentResolver = mContext.getContentResolver();

        long mesocycleId = mTrainingValues.getAsLong(Trainings.MESOCYCLE);
        mMesocycleUri = DatabaseProvider.uriParse(Mesocycles.TABLE_NAME, mesocycleId);
    }

    public void showMainDialog() {
        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setItems(R.array.training_actions, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                showDeleteDialog();
                                break;
                            case 1:
                                showMesocycle();
                                break;
                            case 2:
                                showMoveDialog();
                                break;
                        }

                    }
                })
                .create();

        dialog.show();
    }

    public void showMoveDialog() {
        final TrainingCalendarFragment dialogCaldroidFragment = new TrainingCalendarFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CaldroidFragment.DIALOG_TITLE, mContext.getString(R.string.date_dialog_title));
        bundle.putInt(CaldroidFragment.START_DAY_OF_WEEK, new Preferences(mContext).getFirstDayOfWeek());
        dialogCaldroidFragment.setArguments(bundle);

        dialogCaldroidFragment.setCaldroidListener(new CaldroidListener() {
            @Override
            public void onSelectDate(java.util.Date date, View view) {
                move(date.getTime());
                dialogCaldroidFragment.dismiss();
            }
        });

        dialogCaldroidFragment.show(((FragmentActivity) mContext).getSupportFragmentManager(),
                TrainingCalendarFragment.class.getSimpleName());
    }

    public void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setTitle(R.string.on_delete_title)
                .setItems(R.array.delete_actions, new OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0)
                                    fullDelete();
                                else if (which == 1)
                                    deleteOnlyUndone();
                            }
                        }
                );
        builder.show();
    }

    /**
     * Delete all trainings of mesocycle
     */
    public void fullDelete() {
        // Cascade delete trainings of mesocycle
        mContentResolver.delete(mMesocycleUri, null, null);
        // Reload trainings data
        mContentResolver.notifyChange(DatabaseProvider.TRAININGS_URI, null);
    }

    public void deleteOnlyUndone() {
        long mesocycleId = mTrainingValues.getAsLong(Trainings.MESOCYCLE);
        String where = Trainings.IS_DONE + " = 0 AND " +
                Trainings.MESOCYCLE + " = " + mesocycleId;

        mContentResolver.delete(DatabaseProvider.TRAININGS_URI, where, null);
    }

    public void move(long newDate) {
        Date date = Date.valueOf(mTrainingValues.getAsString(Trainings.DATE));
        String[] projection = new String[]{Trainings._ID};
        String selection = Trainings.DATE + " >= " + DateUtils.sqlFormat(date) + " AND " +
                Trainings.MESOCYCLE + " = " + mMesocycleUri.getLastPathSegment();
        Cursor trainingsCursor = mContentResolver.query(DatabaseProvider.TRAININGS_URI,
                projection, selection, null, null);

        projection = new String[]{Mesocycles._ID, Mesocycles.TRAININGS_IN_WEEK};
        Cursor mesocycleCursor = mContentResolver.query(mMesocycleUri, projection, null, null, null);
        int trainingsInWeek = mesocycleCursor.moveToFirst() ?
                mesocycleCursor.getInt(mesocycleCursor.getColumnIndex(Mesocycles.TRAININGS_IN_WEEK)) : 1;
        mesocycleCursor.close();

        if (trainingsCursor != null && trainingsCursor.moveToFirst()) {
            do {
                long trainingId = trainingsCursor.getLong(trainingsCursor.getColumnIndex(
                        Trainings._ID));
                long trainingDate = DateUtils.calcTrainingDate(trainingsCursor.getPosition(),
                        trainingsInWeek, new Date(newDate));

                ContentValues values = new ContentValues();
                values.put(Trainings.DATE, new SimpleDateFormat("yyyy-MM-dd").format(trainingDate));

                Uri trainingUri = DatabaseProvider.uriParse(Trainings.TABLE_NAME, trainingId);
                mContentResolver.update(trainingUri, values, null, null);
            } while (trainingsCursor.moveToNext());
            trainingsCursor.close();
        }
    }

    public void showMesocycle() {
        long mesocycleId = mTrainingValues.getAsLong(Trainings.MESOCYCLE);
        Uri mesocycleUri = DatabaseProvider.uriParse(Mesocycles.TABLE_NAME, mesocycleId);
        Intent intent = new Intent(mContext, TrainingPlanActivity.class);
        intent.putExtra(TrainingPlanFragment.KEY_MESOCYCLE_URI, mesocycleUri);
        mContext.startActivity(intent);
    }

    public void setTrainingValues(ContentValues trainingValues) {
        mTrainingValues = trainingValues;
    }
}
