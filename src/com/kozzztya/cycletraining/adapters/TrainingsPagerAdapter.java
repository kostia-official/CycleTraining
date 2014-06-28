package com.kozzztya.cycletraining.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.SetsDataFragment;
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.db.entities.TrainingView;

import java.util.LinkedHashMap;
import java.util.List;

public class TrainingsPagerAdapter extends FragmentPagerAdapter {

    private final LinkedHashMap<TrainingView, List<Set>> trainingsSets;

    public TrainingsPagerAdapter(FragmentManager fm, LinkedHashMap<TrainingView, List<Set>> trainingsSets) {
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
        return new SetsDataFragment(getSets(pos));
    }

}