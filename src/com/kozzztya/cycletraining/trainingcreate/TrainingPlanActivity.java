package com.kozzztya.cycletraining.trainingcreate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.kozzztya.cycletraining.MainActivity;
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
import com.kozzztya.cycletraining.utils.SetUtils;

import java.util.LinkedHashMap;
import java.util.List;

//TODO Use ViewPager

public class TrainingPlanActivity extends MyActionBarActivity implements OnClickListener {

    private static final String TAG = "log" + TrainingPlanActivity.class.getSimpleName();

    public static final String KEY_MESOCYCLE = "mesocycle";

    private MesocyclesDS mMesocyclesDS;
    private Mesocycle mMesocycle;

    private DBHelper mDBHelper;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_plan);

        mDBHelper = DBHelper.getInstance(this);
        mMesocyclesDS = new MesocyclesDS(mDBHelper);

        if (savedInstanceState != null) {
            //Restore data from saved instant state
            retrieveData(savedInstanceState);
        } else {
            //Retrieve data from intent
            retrieveData(getIntent().getExtras());
        }

        setTitles();
        buildTable();
    }

    private void retrieveData(Bundle bundle) {
        if (bundle != null) {
            mMesocycle = bundle.getParcelable(KEY_MESOCYCLE);
        }
    }

    private void setTitles() {
        TrainingJournalDS trainingJournalDS = new TrainingJournalDS(mDBHelper);
        String selection = TrainingJournalDS.COLUMN_MESOCYCLE + " = " + mMesocycle.getId();
        TrainingJournalView tj = trainingJournalDS.getEntityView(selection, null, null, null);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(tj.getExerciseName());
        actionBar.setSubtitle(tj.getProgramName() + ", " + getString(R.string.rm) + ": " + SetUtils.weightFormat(mMesocycle.getRm()));
    }

    private void buildTable() {
        TrainingsDS trainingsDS = new TrainingsDS(mDBHelper);
        SetsDS setsDS = new SetsDS(mDBHelper);

        //Select trainings by mMesocycle
        String where = TrainingsDS.COLUMN_MESOCYCLE + " = " + mMesocycle.getId();
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
        mMesocycle.setActive(true);
        mMesocyclesDS.update(mMesocycle);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_MESOCYCLE, mMesocycle);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        //Delete mMesocycle if user don't confirm it
        if (!mMesocycle.isActive()) {
            mMesocyclesDS.delete(mMesocycle.getId());
        }
        super.onDestroy();
    }
}