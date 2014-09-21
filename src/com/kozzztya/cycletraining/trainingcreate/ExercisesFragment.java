package com.kozzztya.cycletraining.trainingcreate;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.kozzztya.cycletraining.MySimpleCursorTreeAdapter;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.custom.ExpandableListFragment;
import com.kozzztya.cycletraining.db.DatabaseProvider;
import com.kozzztya.cycletraining.db.Exercises;
import com.kozzztya.cycletraining.db.Muscles;
import com.kozzztya.cycletraining.utils.ViewUtils;

public class ExercisesFragment extends ExpandableListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "log" + ExercisesFragment.class.getSimpleName();

    //Loaders of programs get id from purpose row id
    private static final int LOADER_MUSCLES = -1;

    private static final String[] PROJECTION_EXERCISES = new String[]
            {Exercises._ID, Exercises.DISPLAY_NAME};

    private MySimpleCursorTreeAdapter mAdapter;
    private ExercisesCallbacks mCallbacks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewUtils.setExpListViewCardStyle(getExpandableListView(), getActivity());

        String[] groupFrom = new String[]{Muscles.DISPLAY_NAME};
        int[] groupTo = new int[]{R.id.title};
        String[] childFrom = new String[]{Exercises.DISPLAY_NAME};
        int[] childTo = new int[]{R.id.title};

        mAdapter = new MySimpleCursorTreeAdapter(getActivity(), null,
                R.layout.group_list_item, R.layout.group_list_item_expanded, groupFrom, groupTo,
                R.layout.child_list_item, R.layout.child_list_item_last, childFrom, childTo, this);
        setListAdapter(mAdapter);
        setListShown(false);

        Loader<Cursor> loader = getLoaderManager().getLoader(LOADER_MUSCLES);
        if (loader != null && !loader.isReset()) {
            getLoaderManager().restartLoader(LOADER_MUSCLES, null, this);
        } else {
            getLoaderManager().initLoader(LOADER_MUSCLES, null, this);
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_MUSCLES) {
            return new CursorLoader(getActivity(), DatabaseProvider.MUSCLES_URI,
                    Muscles.PROJECTION, null, null, null);
        } else {
            if (args != null) {
                long muscleId = args.getLong(Muscles._ID);
                String selection = Exercises.MUSCLE + "=" + muscleId;
                return new CursorLoader(getActivity(), DatabaseProvider.EXERCISES_URI,
                        PROJECTION_EXERCISES, selection, null, null);
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        if (id == LOADER_MUSCLES) {
            mAdapter.setGroupCursor(data);

            if (isResumed()) setListShown(true);
            else setListShownNoAnimation(true);
        } else {
            mAdapter.setChildrenCursor(id, data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_MUSCLES) {
            //CursorHelper closes group and children cursors
            mAdapter.setGroupCursor(null);
        }
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                int childPosition, long id) {
        Uri exerciseUri = DatabaseProvider.uriParse(Exercises.TABLE_NAME, id);
        mCallbacks.onExerciseSelected(exerciseUri);
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.exercises, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                mCallbacks.onExerciseCreateRequest();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (ExercisesCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement "
                    + ExercisesCallbacks.class.getSimpleName());
        }
    }

    public interface ExercisesCallbacks {
        public void onExerciseSelected(Uri exerciseUri);

        public void onExerciseCreateRequest();
    }
}
