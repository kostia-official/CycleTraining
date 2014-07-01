package com.kozzztya.cycletraining;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import com.kozzztya.cycletraining.adapters.TrainingsByWeekExpListAdapter;
import com.kozzztya.cycletraining.db.entities.TrainingView;
import com.kozzztya.cycletraining.db.helpers.TrainingsHelper;
import com.kozzztya.cycletraining.utils.MyDateUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;


public class TrainingWeekFragment extends Fragment implements OnGroupClickListener, OnChildClickListener {

    private TrainingsByWeekExpListAdapter expListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.training_week_fragment, container, false);
    }

    @Override
    public void onStart() {
        showTrainingWeek();
        super.onStart();
    }

    public void showTrainingWeek() {
        TrainingsHelper trainingsHelper = new TrainingsHelper(getActivity());
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        //Берём с настроек первый день недели
        int firstDayOfWeek = Preferences.getFirstDayOfWeek(getActivity());
        //Получаем номер текущего дня недели
        int dayNum = (calendar.get(Calendar.DAY_OF_WEEK) - firstDayOfWeek + 7) % 7;
        Log.v("my", "firstDayOfWeek: " + firstDayOfWeek + " dayNum: " + dayNum);
        //Перематываем дату на начало недели
        calendar.add(Calendar.DATE, -dayNum);
        String where = TrainingsHelper.COLUMN_DATE + " >= '" + dateFormat.format(calendar.getTimeInMillis());
        //Перематываем дату на конец недели
        calendar.add(Calendar.DATE, 6);
        where += "' AND " + TrainingsHelper.COLUMN_DATE + " <= '" + dateFormat.format(calendar.getTimeInMillis()) + "'";
        String orderBy = TrainingsHelper.COLUMN_DATE;
        //Считывание тренировок за неделю
        List<TrainingView> trainingsByWeek = trainingsHelper.selectView(where, null, null, orderBy);

        //Коллекция для хранения тренировок по дням недели
        LinkedHashMap<String, List<TrainingView>> dayGroups = new LinkedHashMap<>();

        //Раскладывание тренировок по дням недели
        for (TrainingView t : trainingsByWeek) {
            String dayOfWeek = MyDateUtils.getDayOfWeekName(t.getDate(), getActivity());
            if (!dayGroups.containsKey(dayOfWeek)) {
                List<TrainingView> trainingsByDay = new ArrayList<>();
                dayGroups.put(dayOfWeek, trainingsByDay);
            }
            dayGroups.get(dayOfWeek).add(t);
        }

        expListAdapter = new TrainingsByWeekExpListAdapter(getActivity(), dayGroups);
        ExpandableListView expList = (ExpandableListView) getView().findViewById(R.id.expandableListView);
        expList.setAdapter(expListAdapter);
        expList.setOnGroupClickListener(this);
        expList.setOnChildClickListener(this);

        //Если день тренировок выполнен, сворачиваем его
        for (int i = 0; i < expListAdapter.getGroupCount(); i++) {
            if (!expListAdapter.isGroupDone(i))
                expList.expandGroup(i);
        }
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        TrainingView training = (TrainingView) expListAdapter.getChild(groupPosition, childPosition);
        long dayOfTrainings = training.getDate().getTime();
        Intent intent = new Intent(getActivity(), TrainingProcessActivity.class);
        intent.putExtra("dayOfTrainings", dayOfTrainings);
        intent.putExtra("exerciseNum", childPosition);
        startActivity(intent);
        return true;
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        TrainingView training = (TrainingView) expListAdapter.getChild(groupPosition, 0);
        long dayOfTrainings = training.getDate().getTime();
        Intent intent = new Intent(getActivity(), TrainingsDayActivity.class);
        intent.putExtra("dayOfTrainings", dayOfTrainings);
        startActivity(intent);
        return true;
    }
}