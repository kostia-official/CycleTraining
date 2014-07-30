package com.kozzztya.cycletraining.trainingcreate;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import com.kozzztya.cycletraining.MyActionBarActivity;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.adapters.MuscleExercisesAdapter;
import com.kozzztya.cycletraining.db.datasources.ExercisesDS;
import com.kozzztya.cycletraining.db.datasources.MusclesDS;
import com.kozzztya.cycletraining.db.entities.Exercise;
import com.kozzztya.cycletraining.db.entities.Muscle;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ExercisesSearchActivity extends MyActionBarActivity implements OnChildClickListener {

    private MuscleExercisesAdapter muscleExercisesAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercises_search);

        getSupportActionBar().setTitle(getString(R.string.exercises));

        fillData();
    }

    public void fillData() {
        ExercisesDS exercisesDS = new ExercisesDS(this);
        MusclesDS musclesDS = new MusclesDS(this);

        List<Exercise> exercises = exercisesDS.select(null, null, null, null);
        List<Muscle> muscles = musclesDS.select(null, null, null, null);
        //Exercises grouped by muscle
        LinkedHashMap<Muscle, List<Exercise>> muscleExercises = new LinkedHashMap<>();

        for (Exercise exercise : exercises) {
            for (Muscle muscle : muscles) {
                if (exercise.getMuscle() == muscle.getId()) {
                    if (!muscleExercises.containsKey(muscle)) {
                        muscleExercises.put(muscle, new ArrayList<Exercise>());
                    }
                    muscleExercises.get(muscle).add(exercise);
                }
            }
        }

        ExpandableListView expListExercises = (ExpandableListView) findViewById(R.id.expListExercises);
        muscleExercisesAdapter = new MuscleExercisesAdapter(this, muscleExercises);
        expListExercises.setAdapter(muscleExercisesAdapter);
        expListExercises.setOnChildClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reset:
                fillData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Exercise exercise = muscleExercisesAdapter.getChild(groupPosition, childPosition);

        Intent intent = new Intent(this, TrainingCreateActivity.class);
        intent.putExtra("exercise", exercise);
        setResult(RESULT_OK, intent);
        finish();
        return true;
    }
}