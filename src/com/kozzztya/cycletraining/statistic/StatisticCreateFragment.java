package com.kozzztya.cycletraining.statistic;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.datasources.TrainingJournalDS;
import com.kozzztya.cycletraining.db.entities.TrainingJournalView;

import java.util.List;

public class StatisticCreateFragment extends Fragment implements View.OnClickListener {

    private Spinner mSpinnerExercises;
    private StatisticCreateCallbacks mCallbacks;

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

        bindData();
        return view;
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

    private void bindData() {
        DBHelper dbHelper = DBHelper.getInstance(getActivity());
        TrainingJournalDS trainingJournalDS = new TrainingJournalDS(dbHelper);

        //Select exercises that used in training journal
        List<TrainingJournalView> exercises = trainingJournalDS.selectView(null, null, null,
                TrainingJournalDS.COLUMN_EXERCISE);

        ArrayAdapter<TrainingJournalView> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, exercises);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerExercises.setAdapter(adapter);
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
    public void onClick(View v) {
        TrainingJournalView trainingJournal = (TrainingJournalView) mSpinnerExercises.getSelectedItem();

        if (trainingJournal != null) {
            Spinner spinnerValue = (Spinner) getView().findViewById(R.id.spinnerValue);
            Spinner spinnerСriterion = (Spinner) getView().findViewById(R.id.spinnerСriterion);
            Spinner spinnerPeriod = (Spinner) getView().findViewById(R.id.spinnerPeriod);

            long exerciseId = trainingJournal.getExercise();
            String resultFunc = (String) spinnerValue.getSelectedItem();
            String values = (String) spinnerСriterion.getSelectedItem();
            String period = (String) spinnerPeriod.getSelectedItem();

            mCallbacks.onStatisticShow(exerciseId, resultFunc, values, period);
        } else {
            Toast.makeText(getActivity(), getString(R.string.toast_chart_error), Toast.LENGTH_LONG).show();
        }
    }

    public static interface StatisticCreateCallbacks {
        void onStatisticShow(long exerciseId, String resultFunc, String values, String period);
    }
}
