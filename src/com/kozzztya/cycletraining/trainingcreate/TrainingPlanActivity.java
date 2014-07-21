package com.kozzztya.cycletraining.trainingcreate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import com.kozzztya.cycletraining.Preferences;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.adapters.TrainingPlanListAdapter;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.datasources.MesocyclesDataSource;
import com.kozzztya.cycletraining.db.datasources.SetsDataSource;
import com.kozzztya.cycletraining.db.datasources.TrainingsDataSource;
import com.kozzztya.cycletraining.db.entities.Mesocycle;
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.db.entities.Training;
import com.kozzztya.cycletraining.db.entities.TrainingJournalView;
import com.kozzztya.cycletraining.trainingjournal.TrainingJournalActivity;
import com.kozzztya.cycletraining.utils.SetUtils;

import java.util.LinkedHashMap;
import java.util.List;

public class TrainingPlanActivity extends ActionBarActivity implements OnClickListener {

    private MesocyclesDataSource mesocyclesDataSource;
    private Mesocycle mesocycle;
    private long mesocycleId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_plan);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        Bundle extras = getIntent().getExtras();
        DBHelper dbHelper = DBHelper.getInstance(this);

        if (extras != null) {
            long trainingJournalId = extras.getLong("training_journal_id");
            TrainingJournalView tj = dbHelper.getTrainingJournalDataSource().getEntityView(trainingJournalId);

            mesocyclesDataSource = dbHelper.getMesocyclesDataSource();
            mesocycleId = tj.getMesocycle();
            mesocycle = mesocyclesDataSource.getEntity(mesocycleId);

            actionBar.setTitle(tj.getProgram());
            actionBar.setSubtitle(tj.getExercise() + ", " + getString(R.string.rm) + ": " + SetUtils.weightFormat(mesocycle.getRm()));

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
        TrainingPlanListAdapter trainingPlanListAdapter = new TrainingPlanListAdapter(this, trainingsSets);
        listViewTrainings.setAdapter(trainingPlanListAdapter);
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