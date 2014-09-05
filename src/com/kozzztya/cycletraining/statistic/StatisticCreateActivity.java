package com.kozzztya.cycletraining.statistic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.kozzztya.cycletraining.DrawerActivity;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.datasources.TrainingJournalDS;
import com.kozzztya.cycletraining.db.entities.TrainingJournalView;

import java.util.List;


public class StatisticCreateActivity extends DrawerActivity implements View.OnClickListener {

    private Spinner mSpinnerExercises;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.statistic_create);

        mSpinnerExercises = (Spinner) findViewById(R.id.spinnerExercise);
    }

    @Override
    protected void onStart() {
        fillExerciseSpinner();
        super.onStart();
    }

    private void fillExerciseSpinner() {
        DBHelper dbHelper = DBHelper.getInstance(this);
        TrainingJournalDS trainingJournalDS = new TrainingJournalDS(dbHelper);

        //Select exercises that used in training journal
        List<TrainingJournalView> exercises = trainingJournalDS.selectView(null, null, null, TrainingJournalDS.COLUMN_EXERCISE);

        ArrayAdapter<TrainingJournalView> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, exercises);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerExercises.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done, menu);
        View actionView = MenuItemCompat.getActionView(menu.findItem(R.id.action_done));
        actionView.setOnClickListener(this);
        return true;
    }

    /**
     * On done menu item click
     */
    @Override
    public void onClick(View v) {
        TrainingJournalView trainingJournal = (TrainingJournalView) mSpinnerExercises.getSelectedItem();

        if (trainingJournal != null) {
            Spinner spinnerValue = (Spinner) findViewById(R.id.spinnerValue);
            Spinner spinnerСriterion = (Spinner) findViewById(R.id.spinnerСriterion);
            Spinner spinnerPeriod = (Spinner) findViewById(R.id.spinnerPeriod);

            long exerciseId = trainingJournal.getExercise();
            String resultFunc = (String) spinnerValue.getSelectedItem();
            String values = (String) spinnerСriterion.getSelectedItem();
            String period = (String) spinnerPeriod.getSelectedItem();

            Intent intent = new Intent(this, StatisticShowActivity.class);
            intent.putExtra("exerciseId", exerciseId);
            intent.putExtra("resultFunc", resultFunc);
            intent.putExtra("values", values);
            intent.putExtra("period", period);
            startActivity(intent);
        } else {
            Toast.makeText(this, getString(R.string.toast_chart_error), Toast.LENGTH_LONG).show();
        }
    }
}