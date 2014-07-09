package com.kozzztya.cycletraining;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.kozzztya.cycletraining.adapters.MesocycleListAdapter;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.datasources.ExercisesDataSource;
import com.kozzztya.cycletraining.db.datasources.MesocyclesDataSource;
import com.kozzztya.cycletraining.db.datasources.SetsDataSource;
import com.kozzztya.cycletraining.db.datasources.TrainingsDataSource;
import com.kozzztya.cycletraining.db.entities.Exercise;
import com.kozzztya.cycletraining.db.entities.Mesocycle;
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.db.entities.Training;
import com.kozzztya.cycletraining.utils.RMUtils;

import java.util.LinkedHashMap;
import java.util.List;

public class MesocycleShowActivity extends ActionBarActivity implements OnClickListener {

    private MesocyclesDataSource mesocyclesDataSource;
    private long mesocycleId;
    private Mesocycle mesocycle;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mesocycle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            mesocycleId = extras.getLong("mesocycleId");
            mesocyclesDataSource = DBHelper.getInstance(this).getMesocyclesDataSource();

            mesocycle = mesocyclesDataSource.getEntity(mesocycleId);

            EditText editTextRM = (EditText) findViewById(R.id.editTextRM);
            editTextRM.setText(RMUtils.weightFormat(mesocycle.getRm()));
            editTextRM.setKeyListener(null);

            ExercisesDataSource exercisesDataSource = DBHelper.getInstance(this).getExercisesDataSource();
            Exercise exercise = exercisesDataSource.getEntity(mesocycle.getExercise());
            setTitle(exercise.getName());

            Button buttonConfirm = (Button) findViewById(R.id.buttonConfirmMesocycle);
            buttonConfirm.setOnClickListener(this);

            buildTable();
        } else {
            finish();
        }
    }

    private void buildTable() {
        TrainingsDataSource trainingsDataSource = DBHelper.getInstance(this).getTrainingsDataSource();
        SetsDataSource setsDataSource = DBHelper.getInstance(this).getSetsDataSource();

        //Select trainings by mesocycle
        String where = TrainingsDataSource.COLUMN_MESOCYCLE + " = " + mesocycleId;
        List<Training> trainings = trainingsDataSource.select(where, null, null, null);

        //Collection for trainings and their sets
        LinkedHashMap<Training, List<Set>> trainingsSets = new LinkedHashMap<>();

        //Select sets of training
        for (Training t : trainings) {
            where = SetsDataSource.COLUMN_TRAINING + " = " + t.getId();
            List<Set> sets = setsDataSource.select(where, null, null, null);

            trainingsSets.put(t, sets);
        }

        ListView listViewTrainings = (ListView) findViewById(R.id.listViewTrainings);
        MesocycleListAdapter mesocycleListAdapter = new MesocycleListAdapter(this, trainingsSets);
        listViewTrainings.setAdapter(mesocycleListAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonConfirmMesocycle:
                mesocycle.setActive(true);
                mesocyclesDataSource.update(mesocycle);

                Intent intent = new Intent(this, TrainingJournalActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (!mesocycle.isActive()) {
            mesocyclesDataSource.delete(mesocycleId);
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsActivity = new Intent(this, Preferences.class);
                startActivity(settingsActivity);
                return true;
            case R.id.action_help:
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}