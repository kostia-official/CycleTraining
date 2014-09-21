package com.kozzztya.cycletraining.trainingjournal;

import android.os.Bundle;

import com.kozzztya.cycletraining.MyActionBarActivity;

public class TrainingSortActivity extends MyActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // During initial setup, plug in fragment
            TrainingSortFragment fragment = new TrainingSortFragment();
            // Pass intent extras to the fragment
            fragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, fragment)
                    .commit();
        }
    }
}