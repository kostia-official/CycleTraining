package com.kozzztya.cycletraining;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.kozzztya.cycletraining.adapters.TrainingsPagerAdapter;
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.db.entities.TrainingView;
import com.kozzztya.cycletraining.db.helpers.SetsHelper;
import com.kozzztya.cycletraining.db.helpers.TrainingsHelper;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;

public class TrainingProcessActivity extends ActionBarActivity {

    private ViewPager viewPager;

    private TrainingsHelper trainingsHelper;
    private SetsHelper setsHelper;

    //Коллекция тренировок за день
    private List<TrainingView> trainingsByDay;
    //Коллекция тренировок и их подходов
    private LinkedHashMap<TrainingView, List<Set>> trainingsSets;
    private TrainingsPagerAdapter trainingsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_process);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        trainingsHelper = new TrainingsHelper(this);
        setsHelper = new SetsHelper(this);

        //Получение даты выбранного дня тренировок
        Bundle extras = getIntent().getExtras();
        Date dayOfTrainings = new Date(extras.getLong("dayOfTrainings"));
        int exerciseNum = extras.getInt("exerciseNum");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        trainingsSets = new LinkedHashMap<>();

        //Получаем с базы коллекцию тренировок за день
        String where = TrainingsHelper.COLUMN_DATE + " = '" + dateFormat.format(dayOfTrainings) + "'";
        String orderBy = TrainingsHelper.COLUMN_DATE;
        trainingsByDay = trainingsHelper.selectView(where, null, null, orderBy);

        //Получаем для каждой тренировки подходы
        for (TrainingView t : trainingsByDay) {
            where = SetsHelper.COLUMN_TRAINING + " = " + t.getId();
            List<Set> sets = setsHelper.select(where, null, null, null);

            trainingsSets.put(t, sets);
        }

        //Адаптер для вкладок с подходами тренировок
        trainingsPagerAdapter = new TrainingsPagerAdapter(getSupportFragmentManager(), trainingsSets);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(trainingsPagerAdapter);
        viewPager.setCurrentItem(exerciseNum);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void doneClick(View view) {
        int i = viewPager.getCurrentItem();

        //Изменяем в БД, что тренировка выполнена
        TrainingView training = trainingsByDay.get(i);
        training.setDone(true);
        trainingsHelper.update(training);

        //Изменяем в БД данные о подходах
        for (Set s : trainingsSets.get(training)) {
            setsHelper.update(s);
        }

        //Пока не достигли последней вкладки
        if (i < trainingsPagerAdapter.getCount() - 1)
            //Переходим на следующую вкладку
            viewPager.setCurrentItem(i + 1);
        else
            finish();
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