package com.kozzztya.cycletraining.trainingjournal;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kozzztya.cycletraining.Preferences;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.custom.MyHorizontalScrollView;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.OnDBChangeListener;
import com.kozzztya.cycletraining.db.datasources.SetsDS;
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.db.entities.TrainingView;
import com.kozzztya.cycletraining.utils.DateUtils;
import com.kozzztya.cycletraining.utils.StyleUtils;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class TrainingDayFragment extends ListFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener, OnDBChangeListener,
        AdapterView.OnItemLongClickListener, MyHorizontalScrollView.OnScrollViewClickListener {

    public static final String KEY_TRAININGS = "trainings";

    private SetsDS mSetsDS;
    private DBHelper mDBHelper;

    private Date mTrainingDay;
    private List<TrainingView> mTrainingsByDay;

    private TrainingDayListAdapter mListAdapter;
    private Preferences mPreferences;

    private TrainingDayCallbacks mCallbacks;

    public static TrainingDayFragment newInstance(List<TrainingView> trainings) {
        TrainingDayFragment trainingDayFragment = new TrainingDayFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(KEY_TRAININGS, (ArrayList<TrainingView>) trainings);
        trainingDayFragment.setArguments(args);

        return trainingDayFragment;
    }

    public TrainingDayFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            //Restore data from saved instant state
            retrieveData(savedInstanceState);
        } else {
            //Retrieve data from intent
            retrieveData(getArguments());
        }

        mPreferences = new Preferences(getActivity());
        mPreferences.registerOnSharedPreferenceChangeListener(this);

        mDBHelper = DBHelper.getInstance(getActivity());
        mDBHelper.registerOnDBChangeListener(this);
        mSetsDS = new SetsDS(mDBHelper);

        showTrainingDay();
        setTitles();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (TrainingDayCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement "
                    + TrainingDayCallbacks.class.getSimpleName());
        }
    }

    private void setTitles() {
        String dayOfWeekName = DateUtils.getDayOfWeekName(mTrainingDay, getActivity());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(dayOfWeekName);
        actionBar.setSubtitle(dateFormat.format(mTrainingDay));
    }

    private void showTrainingDay() {
        //Collection for trainings and their sets
        LinkedHashMap<TrainingView, List<Set>> trainingsSets = new LinkedHashMap<>();

        //Determine day of trainings
        mTrainingDay = mTrainingsByDay.get(0).getDate();

        //Select sets of training
        for (TrainingView t : mTrainingsByDay) {
            String where = SetsDS.COLUMN_TRAINING + " = " + t.getId();
            List<Set> sets = mSetsDS.select(where, null, null, null);

            trainingsSets.put(t, sets);
        }

        mListAdapter = new TrainingDayListAdapter(getActivity(), trainingsSets);
        mListAdapter.setOnScrollViewClickListener(this);
        setListAdapter(mListAdapter);

        ListView listView = getListView();
        StyleUtils.setListViewCardStyle(listView, getActivity());
        listView.setOnItemLongClickListener(this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mCallbacks.onTrainingProcessStart(mTrainingsByDay, mListAdapter.getItem(position).getId());
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        showTrainingHandlerDialog(position);
        return true;
    }

    @Override
    public void onScrollViewClick(View view, int position) {
        mCallbacks.onTrainingProcessStart(mTrainingsByDay, mListAdapter.getItem(position).getId());
    }

    @Override
    public void onScrollViewLongClick(View view, int position) {
        showTrainingHandlerDialog(position);
    }

    public void showTrainingHandlerDialog(int position) {
        TrainingView training = mListAdapter.getItem(position);
        TrainingHandler trainingHandler = new TrainingHandler(getActivity(), training);
        trainingHandler.showMainDialog();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.training_day, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                mCallbacks.onTrainingAdd(mTrainingDay.getTime());
                return true;
            case R.id.action_sort:
                mCallbacks.onTrainingSort(mTrainingsByDay);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void retrieveData(Bundle bundle) {
        if (bundle != null) {
            mTrainingsByDay = bundle.getParcelableArrayList(KEY_TRAININGS);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_TRAININGS, (ArrayList<TrainingView>) mTrainingsByDay);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        showTrainingDay();
    }

    @Override
    public void onDBChange() {
        showTrainingDay();
    }

    @Override
    public void onDestroy() {
        mPreferences.unregisterOnSharedPreferenceChangeListener(this);
        mDBHelper.unregisterOnDBChangeListener(this);
        super.onDestroy();
    }

    public interface TrainingDayCallbacks {
        public void onTrainingAdd(long date);

        public void onTrainingSort(List<TrainingView> trainings);

        public void onTrainingProcessStart(List<TrainingView> trainings, long chosenTrainingId);
    }
}
