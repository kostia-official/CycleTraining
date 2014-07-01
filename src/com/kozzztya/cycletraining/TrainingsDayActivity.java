package com.kozzztya.cycletraining;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import com.kozzztya.cycletraining.adapters.TrainingsSetsExpListAdapter;
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.db.entities.TrainingView;
import com.kozzztya.cycletraining.db.helpers.SetsHelper;
import com.kozzztya.cycletraining.db.helpers.TrainingsHelper;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;

import static android.widget.ExpandableListView.*;

public class TrainingsDayActivity extends ActionBarActivity implements OnGroupClickListener, OnChildClickListener{

    private TrainingsHelper trainingsHelper;
    private SetsHelper setsHelper;
    private Date dayOfTrainings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trainings_by_day);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Получение даты выбранного дня тренировок
        Bundle extras = getIntent().getExtras();
        dayOfTrainings = new Date(extras.getLong("dayOfTrainings"));

        trainingsHelper = new TrainingsHelper(this);
        setsHelper = new SetsHelper(this);


    }

    @Override
    protected void onStart() {
        //Коллекция для хранения тренировок и их подходов
        LinkedHashMap<TrainingView, List<Set>> trainingsSets = new LinkedHashMap<>();

        //Получаем с базы коллекцию тренировок за день
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String where = TrainingsHelper.COLUMN_DATE + " = '" + dateFormat.format(dayOfTrainings) + "'";
        String orderBy = TrainingsHelper.COLUMN_DATE;
        List<TrainingView> trainingsByWeek = trainingsHelper.selectView(where, null, null, orderBy);

        //Получаем для каждой тренировки подходы
        for (TrainingView t : trainingsByWeek) {
            where = SetsHelper.COLUMN_TRAINING + " = " + t.getId();
            List<Set> sets = setsHelper.selectGroupedSets(where, null);

            trainingsSets.put(t, sets);
        }

        TrainingsSetsExpListAdapter expListAdapter = new TrainingsSetsExpListAdapter(this, trainingsSets);
        ExpandableListView expList = (ExpandableListView) findViewById(R.id.expandableListViewTrainingsSets);
        expList.setAdapter(expListAdapter);

        expList.setOnGroupClickListener(this);
        expList.setOnChildClickListener(this);

        super.onStart();
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        Intent intent = new Intent(getApplicationContext(), TrainingProcessActivity.class);
        intent.putExtra("dayOfTrainings", dayOfTrainings.getTime());
        intent.putExtra("exerciseNum", groupPosition);
        startActivity(intent);
        return true;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        return onGroupClick(parent, v, groupPosition, id);
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
            case R.id.action_calendar:
                finish();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}