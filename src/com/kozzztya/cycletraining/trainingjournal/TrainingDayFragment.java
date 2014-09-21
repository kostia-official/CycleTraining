package com.kozzztya.cycletraining.trainingjournal;

import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.kozzztya.cycletraining.Preferences;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.DatabaseProvider;
import com.kozzztya.cycletraining.db.Sets;
import com.kozzztya.cycletraining.db.Trainings;
import com.kozzztya.cycletraining.utils.DateUtils;
import com.kozzztya.cycletraining.utils.ViewUtils;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class TrainingDayFragment extends ListFragment implements AdapterView.OnItemLongClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "log" + TrainingDayFragment.class.getSimpleName();

    public static final String KEY_TRAINING_DAY = "trainingDay";

    private static final int LOADER_TRAININGS = -1;

    private static final String[] PROJECTION_SETS = new String[]{
            Sets._ID,
            Sets.WEIGHT,
            Sets.REPS
    };

    private Date mTrainingDay;

    private TrainingDayAdapter mAdapter;
    private TrainingDayCallbacks mCallbacks;
    private Preferences mPreferences;

    public TrainingDayFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            // Restore data from saved instant state
            retrieveData(savedInstanceState);
        } else {
            // Retrieve data from intent
            retrieveData(getArguments());
        }

        mPreferences = new Preferences(getActivity());
        mPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listView = getListView();
        ViewUtils.setListViewCardStyle(listView, getActivity());
        listView.setOnItemLongClickListener(this);

        initLoader();
        setTitles();
    }

    private void initLoader() {
        mAdapter = new TrainingDayAdapter(getActivity(), R.layout.training_list_item, null, 0, this);
        setListAdapter(mAdapter);
        setListShown(false);

        getLoaderManager().initLoader(LOADER_TRAININGS, null, this);
    }

    private void setTitles() {
        String dayOfWeekName = DateUtils.getDayOfWeekName(mTrainingDay, getActivity());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(dayOfWeekName);
        actionBar.setSubtitle(dateFormat.format(mTrainingDay));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection;
        if (id == LOADER_TRAININGS) {
            selection = Trainings.DATE + "=" + DateUtils.sqlFormat(mTrainingDay);
            return new CursorLoader(getActivity(), DatabaseProvider.TRAININGS_VIEW_URI,
                    Trainings.PROJECTION_VIEW, selection, null, Trainings.PRIORITY);
        } else if (args != null) { // Sets of training data loaders
            selection = Sets.TRAINING + "=" + args.getLong(BaseColumns._ID);
            return new CursorLoader(getActivity(), DatabaseProvider.SETS_URI,
                    PROJECTION_SETS, selection, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        if (id == LOADER_TRAININGS) {
            mAdapter.swapCursor(data);

            if (isResumed()) setListShown(true);
            else setListShownNoAnimation(true);
        } else {
            mAdapter.setSubCursor(id, data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_TRAININGS)
            mAdapter.swapCursor(null);
    }

    /**
     * On training click
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mCallbacks.onTrainingProcessStart(mTrainingDay.getTime(), position);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        showTrainingHandlerDialog(position);
        return true;
    }

    /**
     * Show dialog with delete, move and other operations on training
     *
     * @param position Position of training in cursor adapter
     */
    public void showTrainingHandlerDialog(int position) {
        ContentValues trainingValues = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(
                (Cursor) mAdapter.getItem(position),
                trainingValues);

        TrainingHandler trainingHandler = new TrainingHandler(getActivity(), trainingValues);
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
                trainingsSort(mTrainingDay.getTime());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sort trainings in selected training day
     *
     * @param date Date of training day
     */
    private void trainingsSort(long date) {
        // To sort user need at least two workouts
        if (mAdapter.getCount() > 1) {
            mCallbacks.onTrainingSort(date);
        } else {
            Toast.makeText(getActivity(), R.string.toast_sort_error, Toast.LENGTH_SHORT).show();
        }
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
        getLoaderManager().restartLoader(LOADER_TRAININGS, null, this);
    }

    @Override
    public void onDestroy() {
        mPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
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

    public interface TrainingDayCallbacks {
        public void onTrainingAdd(long trainingDay);

        public void onTrainingSort(long trainingDay);

        public void onTrainingProcessStart(long trainingDay, int trainingPosition);
    }
}
