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

    private final LinkedHashMap<TrainingView, List<Set>> trainingsSets;

    public TrainingPagerAdapter(FragmentManager fm, LinkedHashMap<TrainingView, List<Set>> trainingsSets) {
        super(fm);
        this.trainingsSets = trainingsSets;
    }

    private TrainingView getTraining(int pos) {
        return (TrainingView) trainingsSets.keySet().toArray()[pos];
    }

    private List<Set> getSets(int pos) {
        return trainingsSets.get(getTraining(pos));
    }

    @Override
    public int getCount() {
        return trainingsSets.size();
    }

    @Override
    public CharSequence getPageTitle(int pos) {
        return getTraining(pos).getExercise();
    }

    @Override
    public Fragment getItem(int pos) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("training", getTraining(pos));
        bundle.putParcelableArrayList("sets", (ArrayList<Set>) getSets(pos));

        SetsDataFragment setsDataFragment = new SetsDataFragment();
        setsDataFragment.setArguments(bundle);
        return setsDataFragment;
    }

}