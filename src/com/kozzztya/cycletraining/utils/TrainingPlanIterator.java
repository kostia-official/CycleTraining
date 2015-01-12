package com.kozzztya.cycletraining.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.kozzztya.cycletraining.db.DatabaseHelper;
import com.kozzztya.cycletraining.db.DatabaseProvider;
import com.kozzztya.cycletraining.db.Mesocycles;
import com.kozzztya.cycletraining.db.Sets;
import com.kozzztya.cycletraining.db.Trainings;

public abstract class TrainingPlanIterator {

    private Context mContext;
    private ContentResolver mContentResolver;

    public TrainingPlanIterator(Context context) {
        mContext = context;
        mContentResolver = mContext.getContentResolver();
    }

    /**
     * Bypass training plan tables: mesocycles, trainings, sets.
     *
     * @param mesocycleId   The Mesocycle of the training for iterating.
     * @param isIterateSets Iterate training plan without sets.
     */
    public void iterate(long mesocycleId, boolean isIterateSets) {
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext).getWritableDatabase();
        db.beginTransaction();
        try {
            Uri mesocycleUri = DatabaseProvider.uriParse(Mesocycles.TABLE_NAME, mesocycleId);
            Cursor mesocycleCursor = mContentResolver.query(mesocycleUri, Mesocycles.PROJECTION, null, null, null);

            onMesocycleIterate(mesocycleCursor);

            Cursor trainingsCursor = mContentResolver.query(DatabaseProvider.TRAININGS_URI,
                    Trainings.PROJECTION, Trainings.MESOCYCLE + "=" + mesocycleId, null, null);
            if (trainingsCursor != null && trainingsCursor.moveToFirst()) {
                do {
                    long trainingId = trainingsCursor.getLong(
                            trainingsCursor.getColumnIndex(Trainings._ID));

                    onTrainingIterate(trainingsCursor, mesocycleCursor);

                    if (isIterateSets) {
                        Cursor setsCursor = mContentResolver.query(DatabaseProvider.SETS_URI,
                                Sets.PROJECTION, Sets.TRAINING + "=" + trainingId, null, null);
                        if (setsCursor != null && setsCursor.moveToFirst()) {
                            do {
                                onSetIterate(setsCursor, trainingsCursor, mesocycleCursor);
                            } while (setsCursor.moveToNext());
                            setsCursor.close();
                        }
                    }
                } while (trainingsCursor.moveToNext());
                trainingsCursor.close();
            }
            mesocycleCursor.close();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    protected abstract void onMesocycleIterate(Cursor mesocycleCursor);

    protected abstract void onTrainingIterate(Cursor trainingsCursor, Cursor mesocycleCursor);

    protected void onSetIterate(Cursor setsCursor, Cursor trainingsCursor, Cursor mesocycleCursor) {

    }
}
