package com.kozzztya.cycletraining.trainingcreate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import com.kozzztya.cycletraining.MyActionBarActivity;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.DBHelper;
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

public class TrainingPlanActivity extends MyActionBarActivity implements OnClickListener {

    private MesocyclesDS mesocyclesDS;
    private Mesocycle mesocycle;
    private long mesocycleId;
    private DBHelper dbHelper;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_plan);

        dbHelper = DBHelper.getInstance(this);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            mesocycleId = extras.getLong("mesocycleId");
            mesocyclesDS = new MesocyclesDS(dbHelper);
            mesocycle = mesocyclesDS.getEntity(mesocycleId);

            TrainingJournalDS trainingJournalDS = new TrainingJournalDS(dbHelper);
            String selection = TrainingJournalDS.COLUMN_MESOCYCLE + " = " + mesocycleId;
            TrainingJournalView tj = trainingJournalDS.getEntityView(selection, null, null, null);

            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(tj.getExerciseName());
            actionBar.setSubtitle(tj.getProgramName() + ", " + getString(R.string.rm) + ": " + SetUtils.weightFormat(mesocycle.getRm()));

            buildTable();
        } else {
            finish();
        }
    }

    private void buildTable() {
        TrainingsDS trainingsDS = new TrainingsDS(dbHelper);
        SetsDS setsDS = new SetsDS(dbHelper);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done, menu);
        View actionView = MenuItemCompat.getActionView(menu.findItem(R.id.action_done));
        actionView.setOnClickListener(this);
        return true;
    }

    /**
     * On done menu item click
     */
    @Override
    public void onClick(View view) {
        //Training Journal show only active mesocycles
        mesocycle.setActive(true);
        mesocyclesDS.update(mesocycle);

        startActivity(new Intent(this, TrainingJournalActivity.class));
    }

    @Override
    protected void onDestroy() {
        //Delete mesocycle if user don't confirm it
        if (!mesocycle.isActive()) {
            mesocyclesDS.delete(mesocycleId);
        }
        super.onDestroy();
    }
}