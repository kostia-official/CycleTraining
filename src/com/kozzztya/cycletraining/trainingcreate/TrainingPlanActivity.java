package com.kozzztya.cycletraining.trainingcreate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import com.kozzztya.cycletraining.Preferences;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.adapters.TrainingPlanListAdapter;
import com.kozzztya.cycletraining.db.datasources.MesocyclesDS;
import com.kozzztya.cycletraining.db.datasources.SetsDS;
import com.kozzztya.cycletraining.db.datasources.TrainingJournalDS;
import com.kozzztya.cycletraining.db.datasources.TrainingsDS;
import com.kozzztya.cycletraining.db.entities.Mesocycle;
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.db.entities.Training;
import com.kozzztya.cycletraining.db.entities.TrainingJournalView;
import com.kozzztya.cycletraining.trainingjournal.TrainingJournalActivity;
import com.kozzztya.cycletraining.utils.SetUtils;

import java.util.LinkedHashMap;
import java.util.List;

public class TrainingPlanActivity extends ActionBarActivity implements OnClickListener {

    private MesocyclesDS mesocyclesDS;
    private Mesocycle mesocycle;
    private long mesocycleId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_plan);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            mesocycleId = extras.getLong("mesocycleId");
            mesocyclesDS = new MesocyclesDS(this);
            mesocycle = mesocyclesDS.getEntity(mesocycleId);

            TrainingJournalDS trainingJournalDS = new TrainingJournalDS(this);
            String selection = TrainingJournalDS.COLUMN_MESOCYCLE + " = " + mesocycleId;
            TrainingJournalView tj = trainingJournalDS.getEntityView(selection, null, null, null);

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
        TrainingsDS trainingsDS = new TrainingsDS(this);
        SetsDS setsDS = new SetsDS(this);

        //Select trainings by mesocycle
        String where = TrainingsDS.COLUMN_MESOCYCLE + " = " + mesocycleId;
        List<Training> trainings = trainingsDS.select(where, null, null, null);

        //Collection for trainings and their sets
        LinkedHashMap<Training, List<Set>> trainingsSets = new LinkedHashMap<>();

        //Select sets of training
        for (Training t : trainings) {
            where = SetsDS.COLUMN_TRAINING + " = " + t.getId();
            List<Set> sets = setsDS.select(where, null, null, null);

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
                mesocyclesDS.update(mesocycle);

                Intent intent = new Intent(this, TrainingJournalActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (!mesocycle.isActive()) {
            mesocyclesDS.delete(mesocycleId);
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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