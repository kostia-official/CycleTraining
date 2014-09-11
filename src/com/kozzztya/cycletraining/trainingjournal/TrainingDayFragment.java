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
import com.kozzztya.cycletraining.db.datasources.TrainingsDS;
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.db.entities.TrainingView;
import com.kozzztya.cycletraining.utils.DateUtils;
import com.kozzztya.cycletraining.utils.StyleUtils;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;

public class TrainingDayFragment extends ListFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener, OnDBChangeListener,
        AdapterView.OnItemLongClickListener, MyHorizontalScrollView.OnScrollViewClickListener {

    private static final String TAG = "log" + TrainingDayFragment.class.getSimpleName();

    public static final String KEY_TRAINING_DAY = "trainingDay";

    private SetsDS mSetsDS;
    private DBHelper mDBHelper;

    private Date mTrainingDay;
    private List<TrainingView> mTrainings;

    private TrainingDayListAdapter mListAdapter;
    private Preferences mPreferences;

    private TrainingDayCallbacks mCallbacks;
    private TrainingsDS mTrainingsDS;

    public TrainingDayFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

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
        mTrainingsDS = new TrainingsDS(mDBHelper);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listView = getListView();
        StyleUtils.setListViewCardStyle(listView, getActivity());
        listView.setOnItemLongClickListener(this);

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

        String where = TrainingsDS.COLUMN_DATE + " = " + DateUtils.sqlFormat(mTrainingDay);
        mTrainings = mTrainingsDS.selectView(where, null, null, TrainingsDS.COLUMN_PRIORITY);

        //Select sets of training
        for (TrainingView t : mTrainings) {
            where = SetsDS.COLUMN_TRAINING + " = " + t.getId();
            List<Set> sets = mSetsDS.select(where, null, null, null);

            trainingsSets.put(t, sets);
        }

        mListAdapter = new TrainingDayListAdapter(getActivity(), trainingsSets);
        mListAdapter.setOnScrollViewClickListener(this);
        setListAdapter(mListAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mCallbacks.onTrainingProcessStart(mTrainings, mListAdapter.getItem(position).getId());
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        showTrainingHandlerDialog(position);
        return true;
    }

    @Override
    public void onScrollViewClick(View view, int position) {
        mCallbacks.onTrainingProcessStart(mTrainings, mListAdapter.getItem(position).getId());
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
                mCallbacks.onTrainingSort(mTrainings);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void retrieveData(Bundle bundle) {
        if (bundle != null) {
            mTrainingDay = new Date(bundle.getLong(KEY_TRAINING_DAY));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(KEY_TRAINING_DAY, mTrainingDay.getTime());
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
