package com.kozzztya.cycletraining.statistic;

import android.os.Bundle;

import com.kozzztya.cycletraining.MyActionBarActivity;

public class StatisticShowActivity extends MyActionBarActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // During initial setup, plug in fragment
            StatisticShowFragment fragment = new StatisticShowFragment();
            // Pass intent extras to the fragment
            fragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, fragment)
                    .commit();
        }
    }
}