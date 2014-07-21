package com.kozzztya.cycletraining.statistic;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import com.kozzztya.cycletraining.R;


public class StatisticCreateActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistic_create);

        fillExerciseSpinner();
    }

    private void fillExerciseSpinner() {

    }

    public void confirmClick(View view) {

    }
}