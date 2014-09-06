package com.kozzztya.cycletraining.statistic;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.kozzztya.cycletraining.MyActionBarActivity;
import com.kozzztya.cycletraining.R;

import java.util.Calendar;

public class StatisticShowActivity extends MyActionBarActivity {

    public static final String KEY_EXERCISE_ID = "exerciseId";
    public static final String KEY_RESULT_FUNC = "resultFunc";
    public static final String KEY_VALUES = "values";
    public static final String KEY_PERIOD = "period";
    private long mExerciseId;
    private String mResultFunc;
    private String mValues;
    private String mPeriod;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistic_show);

        if (savedInstanceState != null) {
            //Restore data from saved instant state
            retrieveData(savedInstanceState);
        } else {
            //Retrieve data from intent
            retrieveData(getIntent().getExtras());
        }

        if (!handleChartData()) {
            finish();
            Toast.makeText(this, getString(R.string.toast_chart_error), Toast.LENGTH_LONG).show();
        }
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
    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong(KEY_EXERCISE_ID, mExerciseId);
        outState.putString(KEY_RESULT_FUNC, mResultFunc);
        outState.putString(KEY_VALUES, mValues);
        outState.putString(KEY_PERIOD, mPeriod);
        super.onSaveInstanceState(outState);
    }

    private boolean handleChartData() {
        getSupportActionBar().setTitle(mValues);
        getSupportActionBar().setSubtitle(mResultFunc + " " + getString(R.string.result));

        //Find out sql function
        if (mResultFunc.equals(getString(R.string.result_avg))) {
            mResultFunc = "avg";
        } else if (mResultFunc.equals(getString(R.string.result_max))) {
            mResultFunc = "max";
        } else {
            return false;
        }

        //Find out begin date of chosen mPeriod
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
            return false;
        }

        //Find out chart values
        AbstractChart chart;
        if (mValues.equals(getString(R.string.weight_reps_date))) {
            chart = new WeightRepsDateChart(this);
        } else if (mValues.equals(getString(R.string.weight_date))) {
            chart = new WeightDateChart(this);
        } else if (mValues.equals(getString(R.string.reps_weight))) {
            chart = new RepsWeightChart(this);
        } else {
            return false;
        }

        View chartView = chart.buildChartView(mExerciseId, mResultFunc, minPeriod);
        if (chartView != null) {
            FrameLayout layout = (FrameLayout) findViewById(R.id.chart);
            layout.addView(chartView);
            return true;
        }

        return false;
    }
}