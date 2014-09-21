package com.kozzztya.cycletraining.trainingcreate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.kozzztya.cycletraining.MyActionBarActivity;

public class ProgramsActivity extends MyActionBarActivity implements
        ProgramsFragment.ProgramsCallbacks {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // During initial setup, plug in fragment
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, new ProgramsFragment())
                    .commit();
        }
    }

    @Override
    public void onProgramSelected(Uri programUri) {
        Intent intent = new Intent();
        intent.putExtra(TrainingCreateFragment.KEY_PROGRAM_URI, programUri);
        setResult(RESULT_OK, intent);
        finish();
    }
}