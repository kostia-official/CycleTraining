package com.kozzztya.cycletraining;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ExpandableListView;
import com.kozzztya.cycletraining.adapters.TrainingsSetsExpListAdapter;
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.db.entities.TrainingView;
import com.kozzztya.cycletraining.db.helpers.SetsHelper;
import com.kozzztya.cycletraining.db.helpers.TrainingsHelper;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;

public class TrainingsByDayShowActivity extends ActionBarActivity {

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

        expList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            public boolean onChildClick(ExpandableListView parent, View view,
                                        int groupPos, int childPos, long id) {
                Intent intent = new Intent(getApplicationContext(), TrainingProcessActivity.class);
                intent.putExtra("dayOfTrainings", dayOfTrainings.getTime());
                startActivity(intent);
                return true;
            }
        });

        //Разворачиваем все списки
        for (int i = 0; i < expListAdapter.getGroupCount(); i++) {
            expList.expandGroup(i);
        }
        super.onStart();
    }
}