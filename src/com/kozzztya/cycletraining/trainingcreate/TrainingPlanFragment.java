package com.kozzztya.cycletraining.trainingcreate;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.DatabaseProvider;
import com.kozzztya.cycletraining.db.Mesocycles;
import com.kozzztya.cycletraining.db.Sets;
import com.kozzztya.cycletraining.db.TrainingJournal;
import com.kozzztya.cycletraining.db.Trainings;
import com.kozzztya.cycletraining.utils.ViewUtils;

public class TrainingPlanFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    private static final String TAG = "log" + TrainingPlanFragment.class.getSimpleName();

    public static final String KEY_MESOCYCLE_URI = "mesocycleUri";

    private static final int LOADER_TRAININGS = -1;
    private static final int LOADER_TRAINING_JOURNAL = -2;
    private static final int LOADER_MESOCYCLE = -3;

    private static final String[] PROJECTION_TRAINING_JOURNAL = new String[]{
            TrainingJournal._ID,
            TrainingJournal.EXERCISE_NAME,
            TrainingJournal.PROGRAM_NAME
    };

    private static final String[] PROJECTION_TRAININGS = new String[]{
            Trainings._ID,
            Trainings.DATE,
            Trainings.IS_DONE
    };

    private static final String[] PROJECTION_SETS = new String[]{
            Sets._ID,
            Sets.WEIGHT,
            Sets.REPS
    };

    private Uri mMesocycleUri;
    private Cursor mMesocycleCursor;

    private TrainingPlanAdapter mAdapter;
    private TrainingPlanCallbacks mCallbacks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewUtils.setListViewCardStyle(getListView(), getActivity());

        if (savedInstanceState != null) {
            // Restore data from saved instant state
            retrieveData(savedInstanceState);
        } else {
            // Retrieve data from intent
            retrieveData(getArguments());
        }

        initLoaders();
    }

    private void initLoaders() {
        mAdapter = new TrainingPlanAdapter(getActivity(), R.layout.training_list_item, null, 0, this);
        setListAdapter(mAdapter);
        setListShown(false);

        getLoaderManager().initLoader(LOADER_TRAININGS, null, this);
        getLoaderManager().initLoader(LOADER_TRAINING_JOURNAL, null, this);
        getLoaderManager().initLoader(LOADER_MESOCYCLE, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection;
        String mesocycleId = mMesocycleUri.getLastPathSegment();
        switch (id) {
            case LOADER_MESOCYCLE:
                return new CursorLoader(getActivity(), mMesocycleUri,
                        Mesocycles.PROJECTION, null, null, null);
            case LOADER_TRAINING_JOURNAL:
                selection = TrainingJournal.MESOCYCLE + "=" + mesocycleId;
                return new CursorLoader(getActivity(), DatabaseProvider.TRAINING_JOURNAL_VIEW_URI,
                        PROJECTION_TRAINING_JOURNAL, selection, null, null);
            case LOADER_TRAININGS:
                selection = Trainings.MESOCYCLE + "=" + mesocycleId;
                return new CursorLoader(getActivity(), DatabaseProvider.TRAININGS_URI,
                        PROJECTION_TRAININGS, selection, null, null);
            default: // Sets of training data loaders
                if (args != null) {
                    selection = Sets.TRAINING + "=" + args.getLong(BaseColumns._ID);
                    return new CursorLoader(getActivity(), DatabaseProvider.SETS_URI,
                            PROJECTION_SETS, selection, null, null);
                }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        switch (id) {
            case LOADER_TRAINING_JOURNAL:
                setTitles(data);
                break;
            case LOADER_MESOCYCLE:
                mMesocycleCursor = data;
                break;
            case LOADER_TRAININGS:
                mAdapter.swapCursor(data);

                if (isResumed()) setListShown(true);
                else setListShownNoAnimation(true);
                break;
            default:
                mAdapter.setSubCursor(id, data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_TRAININGS)
            mAdapter.swapCursor(null);
    }

    private void setTitles(Cursor cursor) {
        if (cursor != null && cursor.moveToNext()) {
            String exercise = cursor.getString(cursor.getColumnIndex(TrainingJournal.EXERCISE_NAME));
            String program = cursor.getString(cursor.getColumnIndex(TrainingJournal.PROGRAM_NAME));

            ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
            actionBar.setTitle(exercise);
            actionBar.setSubtitle(program);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.done, menu);
        View actionView = MenuItemCompat.getActionView(menu.findItem(R.id.action_done));
        actionView.setOnClickListener(this);
    }

    /**
     * On done menu item click
     */
    @Override
    public void onClick(View view) {
        if (mMesocycleCursor != null && mMesocycleCursor.moveToNext()) {
            ContentValues values = new ContentValues();
            DatabaseUtils.cursorRowToContentValues(mMesocycleCursor, values);

            // Make confirmed mesocycle active
            values.put(Mesocycles.IS_ACTIVE, 1);
            getActivity().getContentResolver().update(mMesocycleUri, values, null, null);
        }

        mCallbacks.onTrainingPlanConfirmed();
    }

    private void retrieveData(Bundle bundle) {
        if (bundle != null) {
            mMesocycleUri = bundle.getParcelable(KEY_MESOCYCLE_URI);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_MESOCYCLE_URI, mMesocycleUri);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        if (mMesocycleCursor != null && mMesocycleCursor.moveToNext()) {
            boolean isActive = mMesocycleCursor.getInt(mMesocycleCursor.getColumnIndex(
                    Mesocycles.IS_ACTIVE)) != 0;

            // Delete mesocycle if user don't confirm it
            if (!isActive)
                getActivity().getContentResolver().delete(mMesocycleUri, null, null);
        }

        super.onDestroy();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (TrainingPlanCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement "
                    + TrainingPlanCallbacks.class.getSimpleName());
        }
    }

    public static interface TrainingPlanCallbacks {
        void onTrainingPlanConfirmed();
    }
}
