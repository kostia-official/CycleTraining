package com.kozzztya.cycletraining.statistic;

import android.os.Bundle;
import com.kozzztya.cycletraining.BaseActivity;
import com.kozzztya.cycletraining.R;

public class StatisticShowActivity extends BaseActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // During initial setup, plug in fragment
            StatisticShowFragment fragment = new StatisticShowFragment();
            // Pass intent extras to the fragment
            fragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, fragment)
                    .commit();
        }
    }
}