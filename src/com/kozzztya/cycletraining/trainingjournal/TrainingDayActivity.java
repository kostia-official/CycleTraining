package com.kozzztya.cycletraining.trainingjournal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;
import com.kozzztya.cycletraining.MyActionBarActivity;
import com.kozzztya.cycletraining.Preferences;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.adapters.TrainingDayListAdapter;
import com.kozzztya.cycletraining.db.DBHelper;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static com.kozzztya.cycletraining.customviews.MyHorizontalScrollView.OnScrollViewClickListener;

public class TrainingDayActivity extends MyActionBarActivity implements OnItemClickListener,
        OnItemLongClickListener, OnDBChangeListener, OnSharedPreferenceChangeListener, OnScrollViewClickListener {

    private TrainingsDS trainingsDS;
    private SetsDS setsDS;
    private DBHelper dbHelper;

    private Date dayOfTrainings;
    private TrainingDayListAdapter listAdapter;
    private Preferences preferences;
    private List<TrainingView> trainingsByDay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trainings_by_day);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            dayOfTrainings = new Date(extras.getLong("dayOfTraining"));
            String dayOfWeekName = DateUtils.getDayOfWeekName(dayOfTrainings, this);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(dayOfWeekName);
            actionBar.setSubtitle(dateFormat.format(dayOfTrainings));

            preferences = new Preferences(this);
            preferences.registerOnSharedPreferenceChangeListener(this);

            dbHelper = DBHelper.getInstance(this);
            dbHelper.registerOnDBChangeListener(this);
            trainingsDS = new TrainingsDS(dbHelper);
            setsDS = new SetsDS(dbHelper);

            showTrainingDay();
        } else {
            finish();
        }
    }

    private void showTrainingDay() {
        //Collection for trainings and their sets
        LinkedHashMap<TrainingView, List<Set>> trainingsSets = new LinkedHashMap<>();

        //Select trainings by day
        String where = TrainingsDS.COLUMN_DATE + " = " + DateUtils.sqlFormat(dayOfTrainings);
        trainingsByDay = trainingsDS.selectView(where, null, null, TrainingsDS.COLUMN_PRIORITY);

        //Select sets of training
        for (TrainingView t : trainingsByDay) {
            where = SetsDS.COLUMN_TRAINING + " = " + t.getId();
            List<Set> sets = setsDS.select(where, null, null, null);

            trainingsSets.put(t, sets);
        }

        listAdapter = new TrainingDayListAdapter(this, trainingsSets);
        ListView listView = (ListView) findViewById(R.id.listViewTrainingsSets);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        listAdapter.setOnScrollViewClickListener(this);
    }

    public void showTrainingHandlerDialog(int position) {
        TrainingView training = listAdapter.getItem(position);
        TrainingHandler trainingHandler = new TrainingHandler(this, training);
        trainingHandler.showMainDialog();
    }

    public void startTrainingProcess(int position) {
        Intent intent = new Intent(getApplicationContext(), TrainingProcessActivity.class);
        intent.putExtra("dayOfTraining", dayOfTrainings.getTime());
        intent.putExtra("chosenTrainingId", listAdapter.getItem(position).getId());
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startTrainingProcess(position);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        showTrainingHandlerDialog(position);
        return true;
    }

    @Override
    public void onScrollViewClick(View view, int position) {
        startTrainingProcess(position);
    }

    @Override
    public void onScrollViewLongClick(View view, int position) {
        showTrainingHandlerDialog(position);
        listAdapter.getView(position, null, null).setPressed(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.training_day, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.action_add:
                intent.setClass(this, TrainingCreateActivity.class);
                intent.putExtra("beginDate", dayOfTrainings);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            case R.id.action_sort:
                if (trainingsByDay.size() > 1) {
                    intent.setClass(this, TrainingSortActivity.class);
                    intent.putParcelableArrayListExtra("trainingsByDay", (ArrayList<TrainingView>) trainingsByDay);
                    startActivity(intent);
                } else {
                    //To sort user need at least two workouts
                    Toast.makeText(this, R.string.toast_sort_error, Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDBChange() {
        showTrainingDay();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        showTrainingDay();
    }

    @Override
    public void onDestroy() {
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        dbHelper.unregisterOnDBChangeListener(this);
        super.onDestroy();
    }
}