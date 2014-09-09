package com.kozzztya.cycletraining.trainingcreate;

import android.content.Intent;
import android.os.Bundle;

import com.kozzztya.cycletraining.MyActionBarActivity;
import com.kozzztya.cycletraining.db.entities.Program;

public class ProgramsActivity extends MyActionBarActivity implements
        ProgramsFragment.ProgramsCallbacks {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            //During initial setup, plug in fragment
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, new ProgramsFragment())
                    .commit();
        }
    }

    @Override
    public void onProgramSelected(Program program) {
        Intent intent = new Intent(this, TrainingCreateActivity.class);
        intent.putExtra(TrainingCreateActivity.KEY_PROGRAM, program);
        setResult(RESULT_OK, intent);
        finish();
    }
}