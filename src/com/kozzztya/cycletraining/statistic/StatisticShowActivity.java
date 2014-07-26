package com.kozzztya.cycletraining.statistic;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.kozzztya.cycletraining.MyActionBarActivity;
import com.kozzztya.cycletraining.R;

import java.util.Calendar;

public class StatisticShowActivity extends MyActionBarActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistic_show);
        if (!handleChartData()) {
            finish();
            Toast.makeText(this, getString(R.string.toast_chart_error), Toast.LENGTH_LONG).show();
        }
    }

    private boolean handleChartData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            long exerciseId = extras.getLong("exerciseId");
            String resultFunc = extras.getString("resultFunc");
            String values = extras.getString("values");
            String period = extras.getString("period");

            getSupportActionBar().setTitle(values);
            getSupportActionBar().setSubtitle(resultFunc + " " + getString(R.string.result));

            //Find out sql function
            if (resultFunc.equals(getString(R.string.result_avg))) {
                resultFunc = "avg";
            } else if (resultFunc.equals(getString(R.string.result_max))) {
                resultFunc = "max";
            } else {
                return false;
            }

            //Find out begin date of chosen period
            long minPeriod;
            if (period.equals(getString(R.string.period_all))) {
                minPeriod = 0;
            } else if (period.equals(getString(R.string.period_year))) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.YEAR, -1);
                minPeriod = calendar.getTimeInMillis();
            } else if (period.equals(getString(R.string.period_half_year))) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, -6);
                minPeriod = calendar.getTimeInMillis();
            } else if (period.equals(getString(R.string.period_three_months))) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, -3);
                minPeriod = calendar.getTimeInMillis();
            } else if (period.equals(getString(R.string.period_month))) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, -1);
                minPeriod = calendar.getTimeInMillis();
            } else {
                return false;
            }

            //Find out chart values
            AbstractChart chart;
            if (values.equals(getString(R.string.weight_reps_date))) {
                chart = new WeightRepsDateChart(this);
            } else if (values.equals(getString(R.string.weight_date))) {
                chart = new WeightDateChart(this);
            } else if (values.equals(getString(R.string.reps_weight))) {
                chart = new RepsWeightChart(this);
            } else {
                return false;
            }

            View chartView = chart.buildChartView(exerciseId, resultFunc, minPeriod);
            if (chartView != null) {
                FrameLayout layout = (FrameLayout) findViewById(R.id.chart);
                layout.addView(chartView);
                return true;
            }
        }
        return false;
    }
}