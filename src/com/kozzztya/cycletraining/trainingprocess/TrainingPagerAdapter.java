package com.kozzztya.cycletraining.trainingprocess;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.db.entities.TrainingView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class TrainingPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragmentPages;

    public TrainingPagerAdapter(FragmentManager fm, LinkedHashMap<TrainingView, List<Set>> trainingsSets) {
        super(fm);

        mFragmentPages = new ArrayList<>();
        for (TrainingView training : trainingsSets.keySet()) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(SetsListFragment.ARG_TRAINING, training);
            bundle.putParcelableArrayList(SetsListFragment.ARG_SETS,
                    (ArrayList<Set>) trainingsSets.get(training));

            Fragment setsDataFragment = new SetsListFragment();
            setsDataFragment.setArguments(bundle);
            mFragmentPages.add(setsDataFragment);
        }
    }

    @Override
    public int getCount() {
        return mFragmentPages.size();
    }

    @Override
    public Fragment getItem(int pos) {
        return mFragmentPages.get(pos);
    }

}