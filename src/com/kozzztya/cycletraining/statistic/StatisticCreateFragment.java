package com.kozzztya.cycletraining.statistic;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.*;
import android.widget.Spinner;
import android.widget.Toast;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.DatabaseProvider;
import com.kozzztya.cycletraining.db.TrainingJournal;

public class StatisticCreateFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private StatisticCreateCallbacks mCallbacks;
    private SimpleCursorAdapter mAdapter;
    private Spinner mSpinnerExercises;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(getString(R.string.statistic));
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.statistic_create, container, false);
        mSpinnerExercises = (Spinner) view.findViewById(R.id.spinnerExercise);

        initLoader();
        return view;
    }

    private void initLoader() {
        String[] from = new String[]{TrainingJournal.EXERCISE_NAME};
        int[] to = new int[]{android.R.id.text1};
        mAdapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_spinner_item, null, from, to, 0);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerExercises.setAdapter(mAdapter);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{TrainingJournal._ID, TrainingJournal.EXERCISE,
                TrainingJournal.EXERCISE_NAME};
        return new CursorLoader(getActivity(), DatabaseProvider.TRAINING_JOURNAL_VIEW_URI,
                projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
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
        Cursor cursor = (Cursor) mSpinnerExercises.getSelectedItem();

        if (cursor != null) {
            Spinner spinnerValue = (Spinner) getView().findViewById(R.id.spinnerValue);
            Spinner spinnerСriterion = (Spinner) getView().findViewById(R.id.spinnerСriterion);
            Spinner spinnerPeriod = (Spinner) getView().findViewById(R.id.spinnerPeriod);

            long exerciseId = cursor.getColumnIndexOrThrow(TrainingJournal.EXERCISE);
            String resultFunc = (String) spinnerValue.getSelectedItem();
            String values = (String) spinnerСriterion.getSelectedItem();
            String period = (String) spinnerPeriod.getSelectedItem();

            mCallbacks.onStatisticShow(exerciseId, resultFunc, values, period);
        } else {
            Toast.makeText(getActivity(), getString(R.string.toast_chart_error), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (StatisticCreateCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement "
                    + StatisticCreateCallbacks.class.getSimpleName());
        }
    }

    public static interface StatisticCreateCallbacks {
        void onStatisticShow(long exerciseId, String resultFunc, String values, String period);
    }
}
