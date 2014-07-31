package com.kozzztya.cycletraining.trainingjournal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
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

public class TrainingCalendarFragment extends Fragment implements OnSharedPreferenceChangeListener {

    private Preferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        preferences = new Preferences(getActivity());

        createCaldroid();
        return inflater.inflate(R.layout.training_calendar_fragment, container, false);
    }

    private void createCaldroid() {
        MyCaldroidFragment caldroidFragment = new MyCaldroidFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(CaldroidFragment.START_DAY_OF_WEEK, preferences.getFirstDayOfWeek());
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //createCaldroid();
    }

    @Override
    public void onStart() {
        super.onStart();
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }
}