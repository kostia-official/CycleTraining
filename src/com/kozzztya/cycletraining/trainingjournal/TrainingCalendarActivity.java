package com.kozzztya.cycletraining.trainingjournal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.view.View;

import com.kozzztya.cycletraining.MyActionBarActivity;
import com.kozzztya.cycletraining.Preferences;
import com.kozzztya.cycletraining.custom.MyCaldroidFragment;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.util.Date;

public class TrainingCalendarActivity extends MyActionBarActivity implements
        OnSharedPreferenceChangeListener {

    private Preferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = new Preferences(this);
        mPreferences.registerOnSharedPreferenceChangeListener(this);

        if (savedInstanceState == null)
            createCaldroidFragment();
    }

    private void createCaldroidFragment() {
        MyCaldroidFragment caldroidFragment = new MyCaldroidFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(CaldroidFragment.START_DAY_OF_WEEK, mPreferences.getFirstDayOfWeek());
        caldroidFragment.setArguments(bundle);
        caldroidFragment.setCaldroidListener(mCaldroidListener);

        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, caldroidFragment)
                .commit();
    }

    private CaldroidListener mCaldroidListener = new CaldroidListener() {
        @Override
        public void onSelectDate(Date date, View view) {
            Intent intent = new Intent(TrainingCalendarActivity.this, TrainingDayActivity.class);
            intent.putExtra(TrainingDayFragment.KEY_TRAINING_DAY, date.getTime());
            startActivity(intent);
        }
    };

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        createCaldroidFragment();
    }

    @Override
    public void onDestroy() {
        mPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }
}