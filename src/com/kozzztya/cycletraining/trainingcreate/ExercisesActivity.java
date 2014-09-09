package com.kozzztya.cycletraining.trainingcreate;

import android.content.Intent;
import android.os.Bundle;

import com.kozzztya.cycletraining.MyActionBarActivity;
import com.kozzztya.cycletraining.db.entities.Exercise;

public class ExercisesActivity extends MyActionBarActivity implements
        ExercisesFragment.ExercisesCallbacks, ExerciseCreateFragment.OnExerciseAddedListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            //During initial setup, plug in fragment
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, new ExercisesFragment())
                    .commit();
        }
    }

    @Override
    public void onExerciseSelected(Exercise exercise) {
        sendResult(exercise);
    }

    @Override
    public void onExerciseCreateRequest() {
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new ExerciseCreateFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onExerciseCreated(Exercise exercise) {
        sendResult(exercise);
    }

    private void sendResult(Exercise exercise) {
        Intent intent = new Intent(this, TrainingCreateActivity.class);
        intent.putExtra(TrainingCreateActivity.KEY_EXERCISE, exercise);
        setResult(RESULT_OK, intent);
        finish();
    }
}