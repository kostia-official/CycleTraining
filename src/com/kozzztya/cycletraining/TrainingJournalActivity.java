package com.kozzztya.cycletraining;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListView;
import com.kozzztya.cycletraining.db.entities.TrainingView;
import com.kozzztya.cycletraining.db.helpers.TrainingsHelper;

import java.text.SimpleDateFormat;
import java.util.*;

public class TrainingJournalActivity extends Activity {

    private Calendar calendar;
    private TrainingsHelper trainingsHelper;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_journal);

        trainingsHelper = new TrainingsHelper(this);
        calendar = new GregorianCalendar(2014, 5, 2);
    }

    @Override
    protected void onStart() {
        showWeek();
        super.onStart();
    }

    private void showWeek() {
        String[] daysOfWeek = getResources().getStringArray(R.array.days_of_week);

        //Получение текущего дня недели
        //TODO Первый день недели брать с настроек
        int dayNum = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //Перематывание даты на понедельник
        calendar.add(Calendar.DATE, -dayNum);
        String where = TrainingsHelper.COLUMN_DATE + " > '" + dateFormat.format(calendar.getTimeInMillis());
        //Перематывание даты на воскресенье
        calendar.add(Calendar.DATE, 6);
        where += "' AND " + TrainingsHelper.COLUMN_DATE + " < '" + dateFormat.format(calendar.getTimeInMillis()) + "'";
        //Считывание тренировок за неделю
        List<TrainingView> trainingsByWeek = trainingsHelper.selectView(where, null, null, null);
        //Тренировки за день
        List<TrainingView> trainingsByDay;
        //Коллекция тренировок за день
        SortedMap<String, List<TrainingView>> dayGroups = new TreeMap<>();
        //Раскладывание тренировок по дням недели
        for (TrainingView t : trainingsByWeek) {
            calendar.setTime(t.getDate());
            String dayOfWeek = daysOfWeek[calendar.get(Calendar.DAY_OF_WEEK) - 2];
            if (!dayGroups.containsKey(dayOfWeek)) {
                trainingsByDay = new ArrayList<>();
                dayGroups.put(dayOfWeek, trainingsByDay);
            }
            dayGroups.get(dayOfWeek).add(t);
        }
        Log.v("my", dayGroups.toString());
        MyExpListAdapter expListAdapter = new MyExpListAdapter(this, dayGroups);
        ExpandableListView expList = (ExpandableListView) findViewById(R.id.expandableListView);
//        expList.setAdapter(expListAdapter);
    }

}