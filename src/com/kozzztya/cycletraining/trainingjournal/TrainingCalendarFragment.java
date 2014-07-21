package com.kozzztya.cycletraining.trainingjournal;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.kozzztya.cycletraining.Preferences;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.customviews.MyCaldroidFragment;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.util.Date;


public class TrainingCalendarFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.training_calendar_fragment, container, false);
    }

    @Override
    public void onStart() {
        createCaldroid();
        super.onStart();
    }

    private void createCaldroid() {
        MyCaldroidFragment caldroidFragment = new MyCaldroidFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(CaldroidFragment.START_DAY_OF_WEEK, Preferences.getFirstDayOfWeek(getActivity()));
        caldroidFragment.setArguments(bundle);

        caldroidFragment.setCaldroidListener(caldroidListener);

        FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar, caldroidFragment);
        t.commit();
    }

    private CaldroidListener caldroidListener = new CaldroidListener() {
        @Override
        public void onSelectDate(Date date, View view) {
            long dayOfTrainings = date.getTime();
            Intent intent = new Intent(getActivity(), TrainingDayActivity.class);
            intent.putExtra("dayOfTraining", dayOfTrainings);
            startActivity(intent);
        }

        @Override
        public void onLongClickDate(Date date, View view) {

        }
    };

}