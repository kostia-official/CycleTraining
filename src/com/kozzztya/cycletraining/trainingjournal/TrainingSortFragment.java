package com.kozzztya.cycletraining.trainingjournal;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.DatabaseProvider;
import com.kozzztya.cycletraining.db.Trainings;
import com.kozzztya.cycletraining.utils.DateUtils;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.SimpleDragSortCursorAdapter;

import java.util.Calendar;
import java.util.Date;

public class TrainingSortFragment extends ListFragment implements DragSortListView.DropListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String KEY_TRAINING_DAY = "trainingDay";

    private static final int LOADER_TRAININGS = 0;

    private static final String[] PROJECTION_TRAININGS = new String[]{
            Trainings._ID,
            Trainings.EXERCISE,
            Trainings.MESOCYCLE
    };

    private SimpleDragSortCursorAdapter mAdapter;
    private Date mTrainingDay;

    public TrainingSortFragment() {
    }

    /**
     * Initializes the fragment's arguments, and returns the new instance to the client.
     *
     * @param trainingDay The date in milliseconds of training day selected for exercise sorting.
     */
    public static Fragment newInstance(long trainingDay) {
        Bundle args = new Bundle();
        args.putLong(KEY_TRAINING_DAY, trainingDay);

        Fragment fragment = new TrainingSortFragment();
        fragment.setArguments(args);
        return fragment;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.training_drag_sort, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpDragSortListView();
        initLoader();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.sort);
    }

    private void initLoader() {
        String[] from = new String[]{Trainings.EXERCISE};
        int[] to = new int[]{R.id.title};
        mAdapter = new SimpleDragSortCursorAdapter(getActivity(), R.layout.drag_sort_list_item,
                null, from, to, 0);
        setListAdapter(mAdapter);

        getLoaderManager().initLoader(LOADER_TRAININGS, null, this);
    }

    private void setUpDragSortListView() {
        DragSortListView dragSortListView = (DragSortListView) getListView();
        DragSortController controller = new DragSortController(dragSortListView);
        controller.setSortEnabled(true);
        controller.setRemoveEnabled(false);
        controller.setDragInitMode(DragSortController.ON_DOWN);
        controller.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        dragSortListView.setFloatViewManager(controller);
        dragSortListView.setOnTouchListener(controller);
        dragSortListView.setDragEnabled(true);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = Trainings.DATE + "=" + DateUtils.sqlFormat(mTrainingDay);
        return new CursorLoader(getActivity(), DatabaseProvider.TRAININGS_VIEW_URI,
                PROJECTION_TRAININGS, selection, null, Trainings.PRIORITY);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.done, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            doneClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * On done menu item click
     */
    public void doneClick() {
        // Get day of training for sort
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mTrainingDay);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        Cursor cursor = mAdapter.getCursor();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int position = mAdapter.getListPosition(cursor.getPosition());
                long mesocycleId = cursor.getLong(cursor.getColumnIndex(Trainings.MESOCYCLE));
                ContentValues values = new ContentValues();
                values.put(Trainings.PRIORITY, position);

                // Change priority for each trainings of mesocycle in chosen day of week
                String where = Trainings.MESOCYCLE + "=" + mesocycleId +
                        " AND strftime('%w', " + Trainings.DATE + ") = '" + dayOfWeek + "'";
                getActivity().getContentResolver().update(
                        DatabaseProvider.TRAININGS_URI, values, where, null);
            } while (cursor.moveToNext());
        }

        // Notify table view that data was updated
        getActivity().getContentResolver().notifyChange(DatabaseProvider.TRAININGS_VIEW_URI, null);

        getFragmentManager().popBackStack();
    }

    @Override
    public void drop(int from, int to) {
        mAdapter.drop(from, to);
    }
}
