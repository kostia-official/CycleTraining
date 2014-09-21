package com.kozzztya.cycletraining.trainingprocess;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.kozzztya.cycletraining.db.DatabaseProvider;
import com.kozzztya.cycletraining.db.Trainings;

public class TrainingPagerAdapter extends FragmentPagerAdapter {

    private Cursor mTrainingsCursor;

    public TrainingPagerAdapter(FragmentManager fm, Cursor trainingsCursor) {
        super(fm);
        mTrainingsCursor = trainingsCursor;
    }

    @Override
    public int getCount() {
        return mTrainingsCursor != null ? mTrainingsCursor.getCount() : 0;
    }

    @Override
    public TrainingSetsFragment getItem(int position) {
        mTrainingsCursor.moveToPosition(position);

        long trainingId = mTrainingsCursor.getLong(mTrainingsCursor.getColumnIndex(
                Trainings._ID));
        Uri trainingUri = DatabaseProvider.uriParse(Trainings.TABLE_NAME, trainingId);

        Bundle bundle = new Bundle();
        bundle.putParcelable(TrainingSetsFragment.KEY_TRAINING_URI, trainingUri);

        TrainingSetsFragment fragment = new TrainingSetsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public void swapCursor(Cursor trainingsCursor) {
        mTrainingsCursor = trainingsCursor;
        notifyDataSetChanged();
    }
}