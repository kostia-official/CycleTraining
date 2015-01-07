package com.kozzztya.cycletraining.trainingprocess;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.kozzztya.cycletraining.db.Trainings;

import java.util.ArrayList;
import java.util.List;

public class TrainingPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;
    private Cursor mCursor;

    public TrainingPagerAdapter(FragmentManager fm, Cursor cursor) {
        super(fm);
        mCursor = cursor;
        initFragments(cursor);
    }

    /**
     * Initialize fragments, passing them training id
     *
     * @param cursor The cursor with workout data
     */
    protected void initFragments(Cursor cursor) {
        fragments = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                long trainingId = cursor.getLong(cursor.getColumnIndex(Trainings._ID));
                fragments.add(TrainingSetsFragment.newInstance(trainingId));
            } while (cursor.moveToNext());
        }
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    /**
     * Swap the cursor and update the data
     *
     * @param cursor The new cursor
     */
    public void swapCursor(Cursor cursor) {
        if (cursor != mCursor) {
            initFragments(cursor);
            notifyDataSetChanged();
        }
    }
}