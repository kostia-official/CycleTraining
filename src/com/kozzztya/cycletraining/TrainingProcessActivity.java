package com.kozzztya.cycletraining;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.kozzztya.cycletraining.adapters.TrainingPagerAdapter;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.datasources.SetsDataSource;
import com.kozzztya.cycletraining.db.datasources.TrainingsDataSource;
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.db.entities.TrainingView;
import com.kozzztya.cycletraining.utils.RMUtils;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;

public class TrainingProcessActivity extends ActionBarActivity {

    private ViewPager viewPager;

    private TrainingsDataSource trainingsDataSource;
    private SetsDataSource setsDataSource;

    //Коллекция тренировок за день
    private List<TrainingView> trainingsByDay;
    //Коллекция тренировок и их подходов
    private LinkedHashMap<TrainingView, List<Set>> trainingsSets;
    private TrainingPagerAdapter trainingPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_process);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        trainingsDataSource = DBHelper.getInstance(this).getTrainingsDataSource();
        setsDataSource = DBHelper.getInstance(this).getSetsDataSource();

        //Получение дня тренировок и выбранной тренировки
        Bundle extras = getIntent().getExtras();
        Date dayOfTrainings = new Date(extras.getLong("dayOfTraining"));
        long chosenTrainingId = extras.getLong("chosenTrainingId");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        trainingsSets = new LinkedHashMap<>();

        //Получаем с базы коллекцию тренировок за день
        String where = TrainingsDataSource.COLUMN_DATE + " = '" + dateFormat.format(dayOfTrainings) + "'";
        String orderBy = TrainingsDataSource.COLUMN_DATE;
        trainingsByDay = trainingsDataSource.selectView(where, null, null, orderBy);


        int chosenTrainingPage = 0;
        for (TrainingView t : trainingsByDay) {
            //Получаем для каждой тренировки подходы
            where = SetsDataSource.COLUMN_TRAINING + " = " + t.getId();
            List<Set> sets = setsDataSource.select(where, null, null, null);
            trainingsSets.put(t, sets);

            if (t.getId() == chosenTrainingId)
                chosenTrainingPage = trainingsByDay.indexOf(t);
        }

        //Адаптер для вкладок с подходами тренировок
        trainingPagerAdapter = new TrainingPagerAdapter(getSupportFragmentManager(), trainingsSets);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(trainingPagerAdapter);
        viewPager.setCurrentItem(chosenTrainingPage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void doneClick(View view) {
        int i = viewPager.getCurrentItem();
        TrainingView training = trainingsByDay.get(i);

        SQLiteDatabase db = DBHelper.getInstance(this).getWritableDatabase();
        db.beginTransaction();
        try {
            //Update in DB set info
            for (Set s : trainingsSets.get(training)) {
                //If reps max in set not specified
                if (s.getReps() == RMUtils.REPS_MAX) {
                    Toast.makeText(this, R.string.toast_input_max, Toast.LENGTH_LONG).show();
                    return;
                }
                setsDataSource.update(s);
            }
            //Update in DB training status
            training.setDone(true);
            trainingsDataSource.update(training);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        //If on the last tab
        if (i == trainingPagerAdapter.getCount() - 1)
            finish();
        else
            //Go to the next tab
            viewPager.setCurrentItem(i + 1);
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