package com.kozzztya.cycletraining.trainingcreate;

import android.content.Intent;
import android.os.Bundle;

import com.kozzztya.cycletraining.MainActivity;
import com.kozzztya.cycletraining.MyActionBarActivity;

public class TrainingPlanActivity extends MyActionBarActivity implements
        TrainingPlanFragment.TrainingPlanCallbacks {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // During initial setup, plug in fragment
            TrainingPlanFragment trainingPlanFragment = new TrainingPlanFragment();
            // Pass intent extras to the fragment
            trainingPlanFragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, trainingPlanFragment)
                    .commit();
        }
    }

    @Override
    public void onTrainingPlanConfirmed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}