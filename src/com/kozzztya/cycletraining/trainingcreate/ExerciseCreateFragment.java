package com.kozzztya.cycletraining.trainingcreate;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.*;
import android.widget.EditText;
import android.widget.Spinner;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.DatabaseProvider;
import com.kozzztya.cycletraining.db.ExerciseTypes;
import com.kozzztya.cycletraining.db.Exercises;
import com.kozzztya.cycletraining.db.Muscles;
import com.kozzztya.cycletraining.utils.ViewUtils;

public class ExerciseCreateFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_MUSCLES = 0;
    private static final int LOADER_EXERCISE_TYPES = 1;

    private Spinner mSpinnerMuscles;
    private Spinner mSpinnerExerciseTypes;

    private EditText mEditTextName;
    private SimpleCursorAdapter mAdapterMuscles;

    private SimpleCursorAdapter mAdapterExerciseTypes;
    private OnExerciseAddedListener mCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(getString(R.string.action_exercise_create));
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.exercise_create, container, false);
        mSpinnerMuscles = (Spinner) view.findViewById(R.id.spinnerMuscles);
        mSpinnerExerciseTypes = (Spinner) view.findViewById(R.id.spinnerTypes);
        mEditTextName = (EditText) view.findViewById(R.id.name);

        initLoader();
        return view;
    }

    private void initLoader() {
        String[] from = new String[]{Muscles.DISPLAY_NAME};
        mAdapterMuscles = ViewUtils.getSimpleSpinnerCursorAdapter(from, getActivity());
        mSpinnerMuscles.setAdapter(mAdapterMuscles);

        getLoaderManager().initLoader(LOADER_MUSCLES, null, this);

        from = new String[]{ExerciseTypes.DISPLAY_NAME};
        mAdapterExerciseTypes = ViewUtils.getSimpleSpinnerCursorAdapter(from, getActivity());
        mSpinnerExerciseTypes.setAdapter(mAdapterExerciseTypes);

        getLoaderManager().initLoader(LOADER_EXERCISE_TYPES, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_MUSCLES:
                return new CursorLoader(getActivity(), DatabaseProvider.MUSCLES_URI,
                        Muscles.PROJECTION, null, null, null);
            case LOADER_EXERCISE_TYPES:
                return new CursorLoader(getActivity(), DatabaseProvider.EXERCISE_TYPES_URI,
                        ExerciseTypes.PROJECTION, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        switch (id) {
            case LOADER_MUSCLES:
                mAdapterMuscles.swapCursor(data);
                break;
            case LOADER_EXERCISE_TYPES:
                mAdapterExerciseTypes.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        int id = loader.getId();
        switch (id) {
            case LOADER_MUSCLES:
                mAdapterMuscles.swapCursor(null);
                break;
            case LOADER_EXERCISE_TYPES:
                mAdapterExerciseTypes.swapCursor(null);
                break;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnExerciseAddedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + mCallback.getClass().getSimpleName());
        }
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
        if (mEditTextName.getText().length() == 0) {
            mEditTextName.setError(getString(R.string.error_input));
            return;
        }

        ContentValues values = new ContentValues();
        values.put(Exercises.DISPLAY_NAME, mEditTextName.getText().toString());
        values.put(Exercises.MUSCLE, mSpinnerMuscles.getSelectedItemId());
        values.put(Exercises.EXERCISE_TYPE, mSpinnerExerciseTypes.getSelectedItemId());

        Uri exerciseUri = getActivity().getContentResolver()
                .insert(DatabaseProvider.EXERCISES_URI, values);

        mCallback.onExerciseCreated(exerciseUri);
    }

    public interface OnExerciseAddedListener {
        public void onExerciseCreated(Uri exercise);
    }
}