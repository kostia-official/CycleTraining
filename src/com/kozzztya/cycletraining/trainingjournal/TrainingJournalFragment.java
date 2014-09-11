package com.kozzztya.cycletraining.trainingjournal;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;

import com.kozzztya.cycletraining.Preferences;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.custom.ExpandableListFragment;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.OnDBChangeListener;
import com.kozzztya.cycletraining.db.datasources.TrainingsDS;
import com.kozzztya.cycletraining.db.entities.TrainingView;
import com.kozzztya.cycletraining.utils.DateUtils;
import com.kozzztya.cycletraining.utils.StyleUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

import static android.content.SharedPreferences.OnSharedPreferenceChangeListener;

public class TrainingJournalFragment extends ExpandableListFragment implements OnGroupClickListener,
        OnItemLongClickListener, OnDBChangeListener, OnSharedPreferenceChangeListener {

    private static final String TAG = "log" + TrainingJournalFragment.class.getSimpleName();

    private TrainingJournalAdapter mExpListAdapter;
    private Preferences mPreferences;
    private DBHelper mDBHelper;
    private TrainingsDS mTrainingsDS;

    private TrainingWeekCallbacks mCallbacks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(getString(R.string.app_name));

        setHasOptionsMenu(true);
        mDBHelper = DBHelper.getInstance(getActivity());
        mDBHelper.registerOnDBChangeListener(this);
        mTrainingsDS = new TrainingsDS(mDBHelper);

        mPreferences = new Preferences(getActivity());
        mPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpExpListView();
        showTrainingWeek();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (TrainingWeekCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement "
                    + TrainingWeekCallbacks.class.getSimpleName());
        }
    }

    private void setUpExpListView() {
        ExpandableListView expListView = getExpandableListView();
        StyleUtils.setExpListViewCardStyle(expListView, getActivity());
        expListView.setOnItemLongClickListener(this);
        expListView.setOnGroupClickListener(this);
        expListView.setGroupIndicator(null);
    }

    public void showTrainingWeek() {
        Calendar calendar = Calendar.getInstance();

        int firstDayOfWeek = mPreferences.getFirstDayOfWeek();
        //Calc number of current day in week
        int dayNum = (calendar.get(Calendar.DAY_OF_WEEK) - firstDayOfWeek + 7) % 7;

        //Rewind date to start of week
        calendar.add(Calendar.DATE, -dayNum);
        String where = TrainingsDS.COLUMN_DATE + " >= " + DateUtils.sqlFormat(calendar.getTimeInMillis());
        //Rewind date to end of week
        calendar.add(Calendar.DATE, 6);
        where += " AND " + TrainingsDS.COLUMN_DATE + " <= " + DateUtils.sqlFormat(calendar.getTimeInMillis());
        String orderBy = TrainingsDS.COLUMN_DATE + ", " + TrainingsDS.COLUMN_PRIORITY;

        //Select trainings by week
        List<TrainingView> trainingsByWeek = mTrainingsDS.selectView(where, null, null, orderBy);

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

        mExpListAdapter = new TrainingJournalAdapter(getActivity(), dayTrainings);
        setListAdapter(mExpListAdapter);

        //If day of training not done expand it
        ExpandableListView expListView = getExpandableListView();
        int groupCount = mExpListAdapter.getGroupCount();
        for (int i = 0; i < groupCount; i++) {
            if (!mExpListAdapter.isGroupDone(i)) {
                expListView.expandGroup(i);
            }
        }
    }

    /**
     * On training click
     */
    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        //Selected training
        TrainingView training = mExpListAdapter.getChild(groupPosition, childPosition);
        //Trainings by day
        List<TrainingView> trainings = mExpListAdapter.getChildrenOfGroup(groupPosition);
        int trainingStatus = DateUtils.getTrainingStatus(training.getDate(), training.isDone());
        if (trainingStatus == DateUtils.STATUS_MISSED) {
            TrainingHandler trainingHandler = new TrainingHandler(getActivity(), training);
            trainingHandler.showMissedDialog(trainings);
        } else {
            //Start training
            mCallbacks.onTrainingProcessStart(trainings, training.getId());
        }
        return true;
    }

    /**
     * On day of training click
     */
    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, final int groupPosition, long id) {
        TrainingView training = mExpListAdapter.getChild(groupPosition, 0);
        mCallbacks.onTrainingDayStart(training.getDate().getTime());
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
            TrainingView training = mExpListAdapter.getChild(groupPos, childPos);

            TrainingHandler trainingHandler = new TrainingHandler(getActivity(), training);
            trainingHandler.showMainDialog();
            return true;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.training_journal, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_calendar) {
            mCallbacks.onCalendarShow();
            item.setChecked(true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDBChange() {
        showTrainingWeek();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        showTrainingWeek();
    }

    @Override
    public void onDestroy() {
        mPreferences.unregisterOnSharedPreferenceChangeListener(this);
        mDBHelper.unregisterOnDBChangeListener(this);
        super.onDestroy();
    }

    public interface TrainingWeekCallbacks {
        public void onTrainingProcessStart(List<TrainingView> trainings, long chosenTrainingId);

        public void onTrainingDayStart(long trainingDay);

        public void onCalendarShow();
    }
}