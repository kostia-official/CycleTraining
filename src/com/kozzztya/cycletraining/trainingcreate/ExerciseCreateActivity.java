package com.kozzztya.cycletraining.trainingcreate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.datasources.ExerciseTypesDS;
import com.kozzztya.cycletraining.db.datasources.ExercisesDS;
import com.kozzztya.cycletraining.db.datasources.MusclesDS;
import com.kozzztya.cycletraining.db.entities.Exercise;
import com.kozzztya.cycletraining.db.entities.ExerciseType;
import com.kozzztya.cycletraining.db.entities.Muscle;

import java.util.List;


public class ExerciseCreateActivity extends Activity {

    private Spinner spinnerMuscles;
    private Spinner spinnerType;
    private EditText editTextName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_create);

        spinnerMuscles = (Spinner) findViewById(R.id.spinnerMuscles);
        spinnerType = (Spinner) findViewById(R.id.spinnerTypes);
        editTextName = (EditText) findViewById(R.id.name);

        fillSpinners();
    }

    private void fillSpinners() {
        List<Muscle> muscles = new MusclesDS(this).select(null, null, null, null);
        ArrayAdapter<Muscle> adapterMuscles = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, muscles);
        adapterMuscles.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMuscles.setAdapter(adapterMuscles);

        List<ExerciseType> types = new ExerciseTypesDS(this).select(null, null, null, null);
        ArrayAdapter<ExerciseType> adapterTypes = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, types);
        adapterTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapterTypes);
    }

    public void onClick(View view) {
        ExercisesDS exercisesDS = new ExercisesDS(this);

        if (editTextName.getText().length() == 0) {
            editTextName.setError(getString(R.string.error_input));
            return;
        }

        Muscle muscle = (Muscle) spinnerMuscles.getSelectedItem();
        ExerciseType type = (ExerciseType) spinnerType.getSelectedItem();

        Exercise exercise = new Exercise();
        exercise.setName(editTextName.getText().toString());
        exercise.setMuscle(muscle.getId());
        exercise.setExerciseType(type.getId());
        exercise.setId(exercisesDS.insert(exercise));

        Intent intent = new Intent(this, TrainingCreateActivity.class);
        intent.putExtra("exercise", exercise);
        setResult(RESULT_OK, intent);
        finish();
    }
}