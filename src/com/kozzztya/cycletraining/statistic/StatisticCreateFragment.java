package com.kozzztya.cycletraining.statistic;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.kozzztya.cycletraining.MainActivity;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.DatabaseProvider;
import com.kozzztya.cycletraining.db.TrainingJournal;

import java.util.Calendar;

public class StatisticCreateFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private StatisticCreateCallbacks mCallbacks;
    private SimpleCursorAdapter mAdapter;
    private Spinner mSpinnerExercises;
    private Spinner mSpinnerResult;
    private Spinner mSpinnerValue;
    private Spinner mSpinnerPeriod;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.statistic_create, container, false);
        mSpinnerExercises = (Spinner) view.findViewById(R.id.spinnerExercise);
        mSpinnerResult = (Spinner) view.findViewById(R.id.spinnerResult);
        mSpinnerValue = (Spinner) view.findViewById(R.id.spinnerValue);
        mSpinnerPeriod = (Spinner) view.findViewById(R.id.spinnerPeriod);

        initLoader();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.statistic);
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
        String selection = TrainingJournal._ID + "!=0) GROUP BY (" + TrainingJournal.EXERCISE;
        return new CursorLoader(getActivity(), DatabaseProvider.TRAINING_JOURNAL_VIEW_URI,
                projection, selection, null, null);
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
     * On done menu item click.
     */
    public void doneClick() {
        Cursor exercisesCursor = (Cursor) mSpinnerExercises.getSelectedItem();
        if (exercisesCursor != null) {
            String exerciseName = exercisesCursor.getString(exercisesCursor.getColumnIndex(TrainingJournal.EXERCISE_NAME));
            String resultFuncName = (String) mSpinnerResult.getSelectedItem();
            String weightStr = getResources().getString(R.string.weight).toLowerCase();
            setResultTitles(exerciseName, resultFuncName + " " + weightStr);

            long exerciseId = exercisesCursor.getLong(exercisesCursor.getColumnIndex(TrainingJournal.EXERCISE));
            int chartType = mSpinnerValue.getSelectedItemPosition();
            long beginDate = getBeginDate();
            String resultFunc = getResultFunc(resultFuncName);

            mCallbacks.onStatisticShow(chartType, exerciseId, beginDate, resultFunc);
        } else {
            Toast.makeText(getActivity(), getString(R.string.toast_chart_error), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Set titles for result chart.
     */
    private void setResultTitles(String title, String subTitle) {
        Toolbar toolbar = ((MainActivity) getActivity()).getToolbar();
        toolbar.setTitle(title);
        toolbar.setSubtitle(subTitle);
    }

    /**
     * Define the begin date of statistics.
     *
     * @return the begin date in ms.
     */
    private long getBeginDate() {
        String period = (String) mSpinnerPeriod.getSelectedItem();
        Calendar calendar = Calendar.getInstance();

        if (period.equals(getString(R.string.period_year))) {
            calendar.add(Calendar.YEAR, -1);
            return calendar.getTimeInMillis();
        } else if (period.equals(getString(R.string.period_half_year))) {
            calendar.add(Calendar.MONTH, -6);
            return calendar.getTimeInMillis();
        } else if (period.equals(getString(R.string.period_three_months))) {
            calendar.add(Calendar.MONTH, -3);
            return calendar.getTimeInMillis();
        } else if (period.equals(getString(R.string.period_month))) {
            calendar.add(Calendar.MONTH, -1);
            return calendar.getTimeInMillis();
        }
        return 0;
    }

    /**
     * Define a function for the results of statistics.
     *
     * @return an SQL function.
     */
    private String getResultFunc(String resultFuncName) {
        if (resultFuncName.equals(getString(R.string.result_avg))) {
            return "avg";
        } else if (resultFuncName.equals(getString(R.string.result_max))) {
            return "max";
        }
        return null;
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
        void onStatisticShow(int chartType, long exerciseId, long beginDate, String resultFunc);
    }
}
