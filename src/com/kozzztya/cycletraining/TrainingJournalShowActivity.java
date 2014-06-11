package com.kozzztya.cycletraining;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import com.kozzztya.cycletraining.db.entities.TrainingView;
import com.kozzztya.cycletraining.db.helpers.TrainingsHelper;
import com.kozzztya.cycletraining.utils.MyDateUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

import static android.widget.ExpandableListView.OnChildClickListener;

public class TrainingJournalShowActivity extends Activity {

    private Calendar calendar;
    private TrainingsHelper trainingsHelper;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_journal);

        trainingsHelper = new TrainingsHelper(this);
        calendar = Calendar.getInstance();
    }

    @Override
    protected void onStart() {
        showWeek();
        super.onStart();
    }

    private void showWeek() {
        //TODO Первый день недели брать с настроек
        //Получение текущего дня недели
        int dayNum = MyDateUtils.dayOfWeekNum(calendar.get(Calendar.DAY_OF_WEEK), 2);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //Перематывание даты на понедельник
        calendar.add(Calendar.DATE, -dayNum);
        String where = TrainingsHelper.COLUMN_DATE + " >= '" + dateFormat.format(calendar.getTimeInMillis());
        //Перематывание даты на воскресенье
        calendar.add(Calendar.DATE, 6);
        where += "' AND " + TrainingsHelper.COLUMN_DATE + " <= '" + dateFormat.format(calendar.getTimeInMillis()) + "'";
        String orderBy = TrainingsHelper.COLUMN_DATE;
        //Считывание тренировок за неделю
        List<TrainingView> trainingsByWeek = trainingsHelper.selectView(where, null, null, orderBy);

        //Коллекция для хранения тренировок по дням недели
        LinkedHashMap<Integer, List<TrainingView>> dayGroups = new LinkedHashMap<>();
        //Раскладывание тренировок по дням недели
        for (TrainingView t : trainingsByWeek) {
            calendar.setTime(t.getDate());
            int dayOfWeek = MyDateUtils.dayOfWeekNum(calendar.get(Calendar.DAY_OF_WEEK), 2);
            if (!dayGroups.containsKey(dayOfWeek)) {
                List<TrainingView> trainingsByDay = new ArrayList<>();
                dayGroups.put(dayOfWeek, trainingsByDay);
            }
            dayGroups.get(dayOfWeek).add(t);
        }

        final TrainingWeekExpListAdapter expListAdapter = new TrainingWeekExpListAdapter(this, dayGroups);
        ExpandableListView expList = (ExpandableListView) findViewById(R.id.expandableListView);
        expList.setAdapter(expListAdapter);

        expList.setOnChildClickListener(new OnChildClickListener() {
            public boolean onChildClick(ExpandableListView parent, View view,
                                        int groupPos, int childPos, long id) {
                TrainingView training = (TrainingView) expListAdapter.getChild(groupPos, childPos);
                long dayOfTrainings = training.getDate().getTime();
                Intent intent = new Intent(getApplicationContext(), TrainingsByDayShowActivity.class);
                intent.putExtra("dayOfTrainings", dayOfTrainings);
                startActivity(intent);
                return true;
            }
        });
    }

}