package com.kozzztya.cycletraining.trainingjournal;


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
import android.widget.TextView;
import com.kozzztya.cycletraining.Preferences;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.adapters.TrainingWeekExpListAdapter;
import com.kozzztya.cycletraining.db.OnDBChangeListener;
import com.kozzztya.cycletraining.db.datasources.TrainingsDS;
import com.kozzztya.cycletraining.db.entities.TrainingView;
import com.kozzztya.cycletraining.trainingprocess.TrainingProcessActivity;
import com.kozzztya.cycletraining.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;


public class TrainingWeekFragment extends Fragment implements OnGroupClickListener, OnChildClickListener,
        OnItemLongClickListener, OnDBChangeListener {

    private TrainingWeekExpListAdapter expListAdapter;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.training_week_fragment, container, false);
        return view;
    }

    @Override
    public void onStart() {
        showTrainingWeek();
        super.onStart();
    }

    public void showTrainingWeek() {
        TrainingsDS trainingsDS = new TrainingsDS(getActivity());
        Calendar calendar = Calendar.getInstance();

        int firstDayOfWeek = Preferences.getFirstDayOfWeek(getActivity());
        //Calc number of current day in week
        int dayNum = (calendar.get(Calendar.DAY_OF_WEEK) - firstDayOfWeek + 7) % 7;

        //Rewind date to start of week
        calendar.add(Calendar.DATE, -dayNum);
        String where = TrainingsDS.COLUMN_DATE + " >= " + DateUtils.sqlFormat(calendar.getTimeInMillis());
        //Rewind date to end of week
        calendar.add(Calendar.DATE, 6);
        where += " AND " + TrainingsDS.COLUMN_DATE + " <= " + DateUtils.sqlFormat(calendar.getTimeInMillis());
        String orderBy = TrainingsDS.COLUMN_DATE;

        //Select trainings by week
        List<TrainingView> trainingsByWeek = trainingsDS.selectView(where, null, null, orderBy);

        //If this week user have no training show message
        if (trainingsByWeek.size() == 0) {
            TextView textViewNone = (TextView) view.findViewById(R.id.textViewNone);
            textViewNone.setVisibility(View.VISIBLE);
            return;
        }

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
        ExpandableListView expList = (ExpandableListView) view.findViewById(R.id.expandableListView);
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

    /**
     * On training click
     */
    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        TrainingView training = expListAdapter.getChild(groupPosition, childPosition);
        int trainingStatus = DateUtils.getTrainingStatus(training.getDate(), training.isDone());
        if (trainingStatus == DateUtils.STATUS_MISSED) {
            TrainingHandler trainingHandler = new TrainingHandler(getActivity(), training);
            trainingHandler.setOnDBChangeListener(this);
            trainingHandler.showMissedDialog();
        } else {
            //Start training
            long dayOfTrainings = training.getDate().getTime();
            Intent intent = new Intent(getActivity(), TrainingProcessActivity.class);
            intent.putExtra("dayOfTraining", dayOfTrainings);
            intent.putExtra("chosenTrainingId", training.getId());
            startActivity(intent);
        }
        return true;
    }


    /**
     * On day of training click
     */
    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, final int groupPosition, long id) {
        TrainingView training = expListAdapter.getChild(groupPosition, 0);
        long dayOfTrainings = training.getDate().getTime();
        Intent intent = new Intent(getActivity(), TrainingDayActivity.class);
        intent.putExtra("dayOfTraining", dayOfTrainings);
        startActivity(intent);
        return true;
    }

    /**
     * On training long click show handler dialog
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        int itemType = ExpandableListView.getPackedPositionType(id);
        if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            int groupPos = ExpandableListView.getPackedPositionGroup(id);
            int childPos = ExpandableListView.getPackedPositionChild(id);
            TrainingView training = expListAdapter.getChild(groupPos, childPos);

            TrainingHandler trainingHandler = new TrainingHandler(getActivity(), training);
            trainingHandler.setOnDBChangeListener(this);
            trainingHandler.showMainDialog();
            return true;
        }
        return false;
    }

    @Override
    public void onDBChange() {
        showTrainingWeek();
    }
}