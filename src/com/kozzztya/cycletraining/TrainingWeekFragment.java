package com.kozzztya.cycletraining;


import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import com.kozzztya.cycletraining.adapters.TrainingWeekExpListAdapter;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.datasources.TrainingsDataSource;
import com.kozzztya.cycletraining.db.entities.TrainingView;
import com.kozzztya.cycletraining.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;


public class TrainingWeekFragment extends Fragment implements OnGroupClickListener, OnChildClickListener,
        OnItemLongClickListener {

    private TrainingWeekExpListAdapter expListAdapter;

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
        TrainingsDataSource trainingsDataSource = DBHelper.getInstance(getActivity()).getTrainingsDataSource();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        int firstDayOfWeek = Preferences.getFirstDayOfWeek(getActivity());
        //Calc number of current day in week
        int dayNum = (calendar.get(Calendar.DAY_OF_WEEK) - firstDayOfWeek + 7) % 7;

        //Rewind date to start of week
        calendar.add(Calendar.DATE, -dayNum);
        String where = TrainingsDataSource.COLUMN_DATE + " >= '" + dateFormat.format(calendar.getTimeInMillis());
        //Rewind date to end of week
        calendar.add(Calendar.DATE, 6);
        where += "' AND " + TrainingsDataSource.COLUMN_DATE + " <= '" + dateFormat.format(calendar.getTimeInMillis()) + "'";
        String orderBy = TrainingsDataSource.COLUMN_DATE;
        //Select trainings by week
        List<TrainingView> trainingsByWeek = trainingsDataSource.selectView(where, null, null, orderBy);

        //Collection for day of week name and trainings
        LinkedHashMap<String, List<TrainingView>> dayTrainings = new LinkedHashMap<>();

        //Put trainings by days of week
        for (TrainingView t : trainingsByWeek) {
            String dayOfWeek = DateUtils.getDayOfWeekName(t.getDate(), getActivity());
            if (!dayTrainings.containsKey(dayOfWeek)) {
                List<TrainingView> trainingsByDay = new ArrayList<>();
                dayTrainings.put(dayOfWeek, trainingsByDay);
            }
            dayTrainings.get(dayOfWeek).add(t);
        }

        expListAdapter = new TrainingWeekExpListAdapter(getActivity(), dayTrainings);
        ExpandableListView expList = (ExpandableListView) getView().findViewById(R.id.expandableListView);
        expList.setAdapter(expListAdapter);
        expList.setOnItemLongClickListener(this);
        expList.setOnGroupClickListener(this);
        expList.setOnChildClickListener(this);

        //If day of training not done expand it
        for (int i = 0; i < expListAdapter.getGroupCount(); i++) {
            if (!expListAdapter.isGroupDone(i))
                expList.expandGroup(i);
        }
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        TrainingView training = expListAdapter.getChild(groupPosition, childPosition);
        long dayOfTrainings = training.getDate().getTime();
        Intent intent = new Intent(getActivity(), TrainingProcessActivity.class);
        intent.putExtra("dayOfTrainings", dayOfTrainings);
        intent.putExtra("exerciseNum", childPosition);
        startActivity(intent);
        return true;
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        TrainingView training = expListAdapter.getChild(groupPosition, 0);
        long dayOfTrainings = training.getDate().getTime();
        Intent intent = new Intent(getActivity(), TrainingDayActivity.class);
        intent.putExtra("dayOfTrainings", dayOfTrainings);
        startActivity(intent);
        return true;
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        int itemType = ExpandableListView.getPackedPositionType(id);
        if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            int groupPos = ExpandableListView.getPackedPositionGroup(id);
            int childPos = ExpandableListView.getPackedPositionChild(id);
            TrainingView training = expListAdapter.getChild(groupPos, childPos);

            TrainingHandler trainingHandler = new TrainingHandler(getActivity(), training);
            trainingHandler.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    showTrainingWeek();
                }
            });
            trainingHandler.show();
            return true;
        }
        return false;
    }

}