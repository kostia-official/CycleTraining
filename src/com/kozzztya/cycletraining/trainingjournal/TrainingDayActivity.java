package com.kozzztya.cycletraining.trainingjournal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import com.kozzztya.cycletraining.MyActionBarActivity;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.adapters.TrainingDayListAdapter;
import com.kozzztya.cycletraining.db.OnDBChangeListener;
import com.kozzztya.cycletraining.db.datasources.SetsDS;
import com.kozzztya.cycletraining.db.datasources.TrainingsDS;
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.db.entities.TrainingView;
import com.kozzztya.cycletraining.trainingcreate.TrainingCreateActivity;
import com.kozzztya.cycletraining.trainingprocess.TrainingProcessActivity;
import com.kozzztya.cycletraining.utils.DateUtils;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;

public class TrainingDayActivity extends MyActionBarActivity implements OnItemClickListener,
        OnItemLongClickListener, OnDBChangeListener {

    private TrainingsDS trainingsDS;
    private SetsDS setsDS;
    private Date dayOfTrainings;
    private TrainingDayListAdapter listAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trainings_by_day);

        Bundle extras = getIntent().getExtras();
        dayOfTrainings = new Date(extras.getLong("dayOfTraining"));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(DateUtils.getDayOfWeekName(dayOfTrainings, this));
        actionBar.setSubtitle(dateFormat.format(dayOfTrainings));

        trainingsDS = new TrainingsDS(this);
        setsDS = new SetsDS(this);
    }

    @Override
    protected void onStart() {
        showTrainingDay();
        super.onStart();
    }

    private void showTrainingDay() {
        //Collection for trainings and their sets
        LinkedHashMap<TrainingView, List<Set>> trainingsSets = new LinkedHashMap<>();

        //Select trainings by day
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String where = TrainingsDS.COLUMN_DATE + " = '" + dateFormat.format(dayOfTrainings) + "'";
        String orderBy = TrainingsDS.COLUMN_DATE;
        List<TrainingView> trainingsByWeek = trainingsDS.selectView(where, null, null, orderBy);

        //Select sets of training
        for (TrainingView t : trainingsByWeek) {
            where = SetsDS.COLUMN_TRAINING + " = " + t.getId();
            List<Set> sets = setsDS.select(where, null, null, null);

            trainingsSets.put(t, sets);
        }

        listAdapter = new TrainingDayListAdapter(this, trainingsSets);
        ListView listView = (ListView) findViewById(R.id.listViewTrainingsSets);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getApplicationContext(), TrainingProcessActivity.class);
        intent.putExtra("dayOfTraining", dayOfTrainings.getTime());
        intent.putExtra("chosenTrainingId", listAdapter.getItem(position).getId());
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        TrainingView training = listAdapter.getItem(position);

        TrainingHandler trainingHandler = new TrainingHandler(this, training);
        trainingHandler.setOnDBChangeListener(this);
        trainingHandler.showMainDialog();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.training_day, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent intent = new Intent(this, TrainingCreateActivity.class);
                intent.putExtra("beginDate", dayOfTrainings);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDBChange() {
        showTrainingDay();
    }
}