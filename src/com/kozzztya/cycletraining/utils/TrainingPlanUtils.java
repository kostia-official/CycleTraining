package com.kozzztya.cycletraining.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;

import com.kozzztya.cycletraining.db.DatabaseProvider;
import com.kozzztya.cycletraining.db.Mesocycles;
import com.kozzztya.cycletraining.db.Sets;
import com.kozzztya.cycletraining.db.Trainings;

import java.sql.Date;

public class TrainingPlanUtils extends TrainingPlanIterator {

    private Context mContext;
    private ContentResolver mContentResolver;

    private long mNewMesocycleId;
    private long mNewTrainingId;

    private float mRM;
    private float mRoundValue;
    private Date mBeginDate;

    public TrainingPlanUtils(Context context) {
        super(context);
        mContext = context;
        mContentResolver = mContext.getContentResolver();
    }

    public long copyMesocycle(long oldMesocycleId, float rm, float roundValue, Date beginDate) {
        mNewMesocycleId = -1;
        mRM = rm;
        mRoundValue = roundValue;
        mBeginDate = beginDate;

        iterate(oldMesocycleId, true);

        return mNewMesocycleId;
    }

    @Override
    protected void onMesocycleIterate(Cursor mesocycleCursor) {
        // Copy mesocycle data
        ContentValues mesocycleValues = new ContentValues();
        mesocycleCursor.moveToFirst();
        DatabaseUtils.cursorRowToContentValues(mesocycleCursor, mesocycleValues);

        // Insert new mesocycle
        mesocycleValues.remove(Mesocycles._ID);
        Uri newMesocycleUri = mContentResolver.insert(DatabaseProvider.MESOCYCLES_URI, mesocycleValues);
        mNewMesocycleId = Long.valueOf(newMesocycleUri.getLastPathSegment());
    }

    @Override
    protected void onTrainingIterate(Cursor trainingsCursor, Cursor mesocycleCursor) {
        // Copy training data
        ContentValues trainingValues = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(trainingsCursor, trainingValues);

        //Generate new training date
        Date trainingDate = new Date(DateUtils.calcTrainingDate(
                trainingsCursor.getPosition(),
                mesocycleCursor.getInt(mesocycleCursor.getColumnIndex(Mesocycles.TRAININGS_IN_WEEK)),
                mBeginDate));
        trainingValues.put(Trainings.DATE, String.valueOf(trainingDate));

        // Insert new training
        trainingValues.remove(Trainings._ID);
        trainingValues.put(Trainings.MESOCYCLE, mNewMesocycleId);
        Uri trainingUri = mContentResolver.insert(DatabaseProvider.TRAININGS_URI, trainingValues);
        mNewTrainingId = Long.valueOf(trainingUri.getLastPathSegment());
    }

    @Override
    protected void onSetIterate(Cursor setsCursor, Cursor trainingsCursor, Cursor mesocycleCursor) {
        // Copy set data
        ContentValues setsValues = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(setsCursor, setsValues);

        // Calc set weight
        setsValues.put(Sets.WEIGHT, SetUtils.roundTo(
                setsCursor.getFloat(setsCursor.getColumnIndex(Sets.WEIGHT)) * mRM,
                mRoundValue));

        // Insert new set
        setsValues.remove(Sets._ID);
        setsValues.put(Sets.TRAINING, mNewTrainingId);
        mContentResolver.insert(DatabaseProvider.SETS_URI, setsValues);
    }
}
