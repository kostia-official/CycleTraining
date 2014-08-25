package com.kozzztya.cycletraining.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.db.entities.TrainingView;
import com.kozzztya.cycletraining.trainingprocess.SetsDataFragment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class TrainingPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> fragmentPages;

    public TrainingPagerAdapter(FragmentManager fm, LinkedHashMap<TrainingView, List<Set>> trainingsSets) {
        super(fm);

        fragmentPages = new ArrayList<>();
        for (TrainingView training : trainingsSets.keySet()) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("training", training);
            bundle.putParcelableArrayList("sets", (ArrayList<Set>) trainingsSets.get(training));

            Fragment setsDataFragment = new SetsDataFragment();
            setsDataFragment.setArguments(bundle);
            fragmentPages.add(setsDataFragment);
        }
    }

    @Override
    public int getCount() {
        return fragmentPages.size();
    }

    @Override
    public Fragment getItem(int pos) {
        return fragmentPages.get(pos);
    }

}