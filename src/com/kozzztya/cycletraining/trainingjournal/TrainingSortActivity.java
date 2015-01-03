package com.kozzztya.cycletraining.trainingjournal;

import android.os.Bundle;
import com.kozzztya.cycletraining.BaseActivity;
import com.kozzztya.cycletraining.R;

public class TrainingSortActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // During initial setup, plug in fragment
            TrainingSortFragment fragment = new TrainingSortFragment();
            // Pass intent extras to the fragment
            fragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, fragment)
                    .commit();
        }
    }
}