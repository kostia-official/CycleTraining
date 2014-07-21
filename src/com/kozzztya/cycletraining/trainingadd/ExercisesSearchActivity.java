package com.kozzztya.cycletraining.trainingadd;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.customviews.HintSpinner;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.datasources.ExerciseTypesDataSource;
import com.kozzztya.cycletraining.db.datasources.ExercisesDataSource;
import com.kozzztya.cycletraining.db.datasources.MusclesDataSource;
import com.kozzztya.cycletraining.db.entities.Exercise;
import com.kozzztya.cycletraining.db.entities.ExerciseType;
import com.kozzztya.cycletraining.db.entities.Muscle;

import java.util.List;

public class ExercisesSearchActivity extends ActionBarActivity implements OnItemClickListener, OnItemSelectedListener {

    private DBHelper dbHelper;

    private HintSpinner spinnerMuscles;
    private HintSpinner spinnerType;
    private ArrayAdapter adapterExercises;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercises_search);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(getString(R.string.exercise_search));

        spinnerMuscles = (HintSpinner) findViewById(R.id.spinnerMuscles);
        spinnerType = (HintSpinner) findViewById(R.id.spinnerType);

        dbHelper = DBHelper.getInstance(this);

        fillData();
    }

    public void fillData() {
        ExercisesDataSource exercisesDataSource = dbHelper.getExercisesDataSource();
        MusclesDataSource musclesDataSource = dbHelper.getMusclesDataSource();
        ExerciseTypesDataSource exerciseTypesDataSource = dbHelper.getExerciseTypesDataSource();

        ListView listViewExercises = (ListView) findViewById(R.id.listViewExercises);

        List<Exercise> exercises = exercisesDataSource.select(null, null, null, null);
        adapterExercises = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, exercises);
        listViewExercises.setAdapter(adapterExercises);
        listViewExercises.setOnItemClickListener(this);

        List<Muscle> muscles = musclesDataSource.select(null, null, null, null);
        ArrayAdapter<Muscle> adapterMuscles = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, muscles);
        adapterMuscles.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerMuscles.setAdapter(adapterMuscles);
        spinnerMuscles.setOnItemSelectedListener(this);

        List<ExerciseType> exerciseTypes = exerciseTypesDataSource.select(null, null, null, null);
        ArrayAdapter<ExerciseType> adapterTypes = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, exerciseTypes);
        adapterTypes.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerType.setAdapter(adapterTypes);
        spinnerType.setOnItemSelectedListener(this);
    }

    private void search() {
        //TODO Use Filter
        ExercisesDataSource exercisesDataSource = dbHelper.getExercisesDataSource();
        String selection = "";
        if (spinnerMuscles.getSelectedItemPosition() >= 0)
            selection += ExercisesDataSource.COLUMN_MUSCLE + " = " +
                    ((Muscle) spinnerMuscles.getSelectedItem()).getId() + " AND ";

        if (spinnerType.getSelectedItemPosition() >= 0)
            selection += ExercisesDataSource.COLUMN_EXERCISE_TYPE + " = " +
                    ((ExerciseType) spinnerType.getSelectedItem()).getId() + " AND ";

        //Delete last AND
        selection = selection.substring(0, selection.length() - 5);

        List<Exercise> exercises = exercisesDataSource.select(selection, null, null, null);
        adapterExercises.clear();
        adapterExercises.addAll(exercises);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, TrainingCreateActivity.class);
        intent.putExtra("exercise", (Exercise) parent.getItemAtPosition(position));
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_reset:
                fillData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        search();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //do nothing
    }
}