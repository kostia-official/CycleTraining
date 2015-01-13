package com.kozzztya.cycletraining.trainingjournal;

import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import com.kozzztya.cycletraining.db.DatabaseProvider;
import com.kozzztya.cycletraining.db.Trainings;
import com.kozzztya.cycletraining.utils.DateUtils;
import com.kozzztya.cycletraining.utils.TrainingUtils;
import com.kozzztya.cycletraining.utils.ViewUtils;

import java.sql.Date;
import java.util.Calendar;

import static android.content.SharedPreferences.OnSharedPreferenceChangeListener;

public class TrainingWeekFragment extends ExpandableListFragment implements OnGroupClickListener,
        OnItemLongClickListener, OnSharedPreferenceChangeListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "log" + TrainingWeekFragment.class.getSimpleName();

    /**
     * Group loader id.
     * Children loaders take id from group position.
     */
    private static final int LOADER_WEEKDAYS = -1;

    /**
     * Training day is done only if all trainings are done
     */
    public static final String COLUMN_IS_DAY_DONE = "min(" + Trainings.IS_DONE + ")";

    private static final String[] PROJECTION_WEEKDAYS = new String[]{
            Trainings._ID, Trainings.DATE, COLUMN_IS_DAY_DONE};

    private TrainingWeekTreeAdapter mAdapter;
    private Preferences mPreferences;

    private TrainingWeekCallbacks mCallbacks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        mPreferences = new Preferences(getActivity());
        mPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpExpListView();
        initLoader();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.app_name);
    }

    private void setUpExpListView() {
        ExpandableListView expListView = getExpandableListView();
        ViewUtils.setExpListViewCardStyle(expListView, getActivity());
        expListView.setOnItemLongClickListener(this);
        expListView.setOnGroupClickListener(this);
        expListView.setGroupIndicator(null);
    }

    private void initLoader() {
        mAdapter = new TrainingWeekTreeAdapter(getActivity(), null, R.layout.weekday_group,
                R.layout.weekday_group_expanded, R.layout.child_list_item,
                R.layout.child_list_item_last, this);
        setListAdapter(mAdapter);
        setListShown(false);

        getLoaderManager().initLoader(LOADER_WEEKDAYS, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection;
        if (id == LOADER_WEEKDAYS) { // Group loader
            selection = getSelectionByWeek() + ") GROUP BY (" + Trainings.DATE;
            return new CursorLoader(getActivity(), DatabaseProvider.TRAININGS_VIEW_URI,
                    PROJECTION_WEEKDAYS, selection, null, null);
        } else if (args != null) { // Children loaders
            long weekDay = args.getLong(Trainings.DATE); // Group to child relationship
            selection = Trainings.DATE + "=" + DateUtils.sqlFormat(weekDay);
            return new CursorLoader(getActivity(), DatabaseProvider.TRAININGS_VIEW_URI,
                    Trainings.PROJECTION_VIEW, selection, null, Trainings.PRIORITY);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        if (id == LOADER_WEEKDAYS) {
            mAdapter.setGroupCursor(data);

            expandTrainings();
            if (isResumed()) setListShown(true);
            else setListShownNoAnimation(true);
        } else {
            mAdapter.setChildrenCursor(id, data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_WEEKDAYS) {
            // CursorHelper closes group and children cursors
            mAdapter.setGroupCursor(null);
        }
    }

    /**
     * Get selection of trainings by week
     *
     * @return Selection part of query
     */
    private String getSelectionByWeek() {
        Calendar calendar = Calendar.getInstance();
        int firstDayOfWeek = mPreferences.getFirstDayOfWeek();

        // Calc number of current day in week
        int dayNum = (calendar.get(Calendar.DAY_OF_WEEK) - firstDayOfWeek + 7) % 7;

        // Rewind date to start of week
        calendar.add(Calendar.DATE, -dayNum);
        String selection = Trainings.DATE + " >= " +
                DateUtils.sqlFormat(calendar.getTimeInMillis());

        // Rewind date to end of week
        calendar.add(Calendar.DATE, 6);
        selection += " AND " + Trainings.DATE + " <= " +
                DateUtils.sqlFormat(calendar.getTimeInMillis());

        return selection;
    }

    /**
     * Show trainings of day if they are not done
     */
    private void expandTrainings() {
        ExpandableListView expListView = getExpandableListView();
        int groupCount = mAdapter.getGroupCount();
        for (int i = 0; i < groupCount; i++) {
            if (!mAdapter.isGroupDone(i)) {
                expListView.expandGroup(i);
            }
        }
    }

    /**
     * On training click
     */
    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Cursor cursor = mAdapter.getGroup(groupPosition);
        Date weekDay = DateUtils.safeParse(cursor.getString(
                cursor.getColumnIndex(Trainings.DATE)));
        mCallbacks.onTrainingSelected(weekDay.getTime(), childPosition);
        return true;
    }

    /**
     * On day of training click
     */
    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, final int groupPosition, long id) {
        Cursor cursor = mAdapter.getGroup(groupPosition);
        Date weekDay = DateUtils.safeParse(cursor.getString(
                cursor.getColumnIndex(Trainings.DATE)));
        mCallbacks.onTrainingDaySelected(weekDay.getTime());
        return true;
    }

    /**
     * On training long click show handler dialog
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        int itemType = ExpandableListView.getPackedPositionType(id);
        if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            // Determine unknown group and child position
            long packedPosition = ((ExpandableListView) parent).getExpandableListPosition(position);
            int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
            int childPosition = ExpandableListView.getPackedPositionChild(packedPosition);

            Cursor trainingCursor = mAdapter.getChild(groupPosition, childPosition);
            ContentValues trainingValues = new ContentValues();
            DatabaseUtils.cursorRowToContentValues(trainingCursor, trainingValues);

            TrainingUtils trainingUtils = new TrainingUtils(getActivity());
            trainingUtils.showActionsDialog(trainingValues);
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        getLoaderManager().restartLoader(LOADER_WEEKDAYS, null, this);
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
            mCallbacks = (TrainingWeekCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement "
                    + TrainingWeekCallbacks.class.getSimpleName());
        }
    }

    public interface TrainingWeekCallbacks {
        public void onTrainingSelected(long trainingDay, int trainingPosition);

        public void onTrainingDaySelected(long trainingDay);

        public void onCalendarShow();
    }
}