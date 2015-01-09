package com.kozzztya.cycletraining.statistic;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StatisticShowFragment extends Fragment {

    public static final String KEY_CHART_TYPE = "chartType";
    public static final String KEY_EXERCISE_ID = "exerciseId";
    public static final String KEY_BEGIN_DATE = "beginDate";
    public static final String KEY_RESULT_FUNC = "resultFunc";

    public static final int CHART_TYPE_REPS_WEIGHT = 0;
    public static final int CHART_TYPE_WEIGHT_DATE = 1;
    public static final int CHART_TYPE_WEIGHT_REPS_DATE = 2;

    private int mChartType;
    private long mExerciseId;
    private long mBeginDate;
    private String mResultFunc;

    public StatisticShowFragment() {
    }

    public static Fragment newInstance(int chartType, long exerciseId,
                                       long beginDate, String resultFunc) {
        Bundle args = new Bundle();
        args.putInt(KEY_CHART_TYPE, chartType);
        args.putLong(KEY_EXERCISE_ID, exerciseId);
        args.putLong(KEY_BEGIN_DATE, beginDate);
        args.putString(KEY_RESULT_FUNC, resultFunc);

        Fragment fragment = new StatisticShowFragment();
        fragment.setArguments(args);
        return fragment;
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

        AbstractChart chart = createChart(mChartType);
        return chart.buildChartView(mExerciseId, mResultFunc, mBeginDate);
    }

    private AbstractChart createChart(int chartType) {
        switch (chartType) {
            case CHART_TYPE_REPS_WEIGHT:
                return new RepsWeightChart(getActivity());
            case CHART_TYPE_WEIGHT_DATE:
                return new WeightDateChart(getActivity());
            case CHART_TYPE_WEIGHT_REPS_DATE:
                return new WeightRepsDateChart(getActivity());
            default:
                return null;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_CHART_TYPE, mChartType);
        outState.putLong(KEY_EXERCISE_ID, mExerciseId);
        outState.putLong(KEY_BEGIN_DATE, mBeginDate);
        outState.putString(KEY_RESULT_FUNC, mResultFunc);
        super.onSaveInstanceState(outState);
    }

    private void retrieveData(Bundle bundle) {
        if (bundle != null) {
            mChartType = bundle.getInt(KEY_CHART_TYPE);
            mExerciseId = bundle.getLong(KEY_EXERCISE_ID);
            mBeginDate = bundle.getLong(KEY_BEGIN_DATE);
            mResultFunc = bundle.getString(KEY_RESULT_FUNC);
        }
    }
}
