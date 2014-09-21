package com.kozzztya.cycletraining.statistic;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kozzztya.cycletraining.R;

import java.util.Calendar;

public class StatisticShowFragment extends Fragment {

    public static final String KEY_EXERCISE_ID = "exerciseId";
    public static final String KEY_RESULT_FUNC = "resultFunc";
    public static final String KEY_VALUES = "values";
    public static final String KEY_PERIOD = "period";

    private long mExerciseId;
    private String mResultFunc;
    private String mValues;
    private String mPeriod;

    public StatisticShowFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Restore data from saved instant state
            retrieveData(savedInstanceState);
        } else {
            // Retrieve data from intent
            retrieveData(getArguments());
        }

        setTitles();
        return buildChartView();
    }

    private void setTitles() {
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(mValues);
        actionBar.setSubtitle(mResultFunc + " " + getString(R.string.result));
    }

    private View buildChartView() {
        // Find out sql function
        if (mResultFunc.equals(getString(R.string.result_avg))) {
            mResultFunc = "avg";
        } else if (mResultFunc.equals(getString(R.string.result_max))) {
            mResultFunc = "max";
        } else {
            return null;
        }

        // Find out begin date of chosen mPeriod
        long minPeriod;
        Calendar calendar = Calendar.getInstance();
        if (mPeriod.equals(getString(R.string.period_all))) {
            minPeriod = 0;
        } else if (mPeriod.equals(getString(R.string.period_year))) {
            calendar.add(Calendar.YEAR, -1);
            minPeriod = calendar.getTimeInMillis();
        } else if (mPeriod.equals(getString(R.string.period_half_year))) {
            calendar.add(Calendar.MONTH, -6);
            minPeriod = calendar.getTimeInMillis();
        } else if (mPeriod.equals(getString(R.string.period_three_months))) {
            calendar.add(Calendar.MONTH, -3);
            minPeriod = calendar.getTimeInMillis();
        } else if (mPeriod.equals(getString(R.string.period_month))) {
            calendar.add(Calendar.MONTH, -1);
            minPeriod = calendar.getTimeInMillis();
        } else {
            return null;
        }

        // Find out chart values
        AbstractChart chart;
        if (mValues.equals(getString(R.string.weight_reps_date))) {
            chart = new WeightRepsDateChart(getActivity());
        } else if (mValues.equals(getString(R.string.weight_date))) {
            chart = new WeightDateChart(getActivity());
        } else if (mValues.equals(getString(R.string.reps_weight))) {
            chart = new RepsWeightChart(getActivity());
        } else {
            return null;
        }

        return chart.buildChartView(mExerciseId, mResultFunc, minPeriod);
    }

    private void retrieveData(Bundle bundle) {
        if (bundle != null) {
            mExerciseId = bundle.getLong(KEY_EXERCISE_ID);
            mResultFunc = bundle.getString(KEY_RESULT_FUNC);
            mValues = bundle.getString(KEY_VALUES);
            mPeriod = bundle.getString(KEY_PERIOD);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(KEY_EXERCISE_ID, mExerciseId);
        outState.putString(KEY_RESULT_FUNC, mResultFunc);
        outState.putString(KEY_VALUES, mValues);
        outState.putString(KEY_PERIOD, mPeriod);
        super.onSaveInstanceState(outState);
    }
}
