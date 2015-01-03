package com.kozzztya.cycletraining.trainingcreate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.kozzztya.cycletraining.BaseActivity;
import com.kozzztya.cycletraining.R;

public class ExercisesActivity extends BaseActivity implements
        ExercisesFragment.ExercisesCallbacks, ExerciseCreateFragment.OnExerciseAddedListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // During initial setup, plug in fragment
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, new ExercisesFragment())
                    .commit();
        }
    }

    @Override
    public void onExerciseSelected(Uri exerciseUri) {
        sendResult(exerciseUri);
    }

    @Override
    public void onExerciseCreateRequest() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, new ExerciseCreateFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onExerciseCreated(Uri exerciseUri) {
        sendResult(exerciseUri);
    }

    private void sendResult(Uri exerciseUri) {
        Intent intent = new Intent();
        intent.putExtra(TrainingCreateFragment.KEY_EXERCISE_URI, exerciseUri);
        setResult(RESULT_OK, intent);
        finish();
    }
}