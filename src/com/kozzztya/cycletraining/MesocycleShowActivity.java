package com.kozzztya.cycletraining;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.datasources.ExercisesDataSource;
import com.kozzztya.cycletraining.db.datasources.MesocyclesDataSource;
import com.kozzztya.cycletraining.db.datasources.SetsDataSource;
import com.kozzztya.cycletraining.db.entities.Exercise;
import com.kozzztya.cycletraining.db.entities.Mesocycle;
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.utils.RMUtils;

import java.util.ArrayList;
import java.util.List;

public class MesocycleShowActivity extends ActionBarActivity implements OnClickListener {

    private MesocyclesDataSource mesocyclesDataSource;
    private long mesocycleId;
    private Mesocycle mesocycle;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mesocycle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Bundle extras = getIntent().getExtras();

        mesocycleId = extras.getLong("mesocycleId");
        mesocyclesDataSource = DBHelper.getInstance(this).getMesocyclesDataSource();

        mesocycle = mesocyclesDataSource.getEntity(mesocycleId);

        EditText editTextRM = (EditText) findViewById(R.id.editTextRM);
        editTextRM.setText(String.format("%.2f", mesocycle.getRm()));
        editTextRM.setKeyListener(null);

        ExercisesDataSource exercisesDataSource = DBHelper.getInstance(this).getExercisesDataSource();
        Exercise exercise = exercisesDataSource.getEntity(mesocycle.getExercise());
        setTitle(exercise.getName());

        Button buttonConfirm = (Button) findViewById(R.id.buttonConfirmMesocycle);
        buttonConfirm.setOnClickListener(this);

        buildTable();
    }

    private void buildTable() {
        SetsDataSource setsDataSource = DBHelper.getInstance(this).getSetsDataSource();
        List<Set> sets = setsDataSource.selectGroupedSets(SetsDataSource.COLUMN_MESOCYCLE + " = " + mesocycleId, null);
        TableLayout layout = (TableLayout) findViewById(R.id.tableLayoutMesocycle);

        for (int i = 0; i < sets.size(); i++) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            List<EditText> editTexts = new ArrayList<>();
            for (int j = 0; j < 4; j++) {
                EditText editText = new EditText(this);
                editText.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT, 1));
                editText.setGravity(Gravity.CENTER);
                editText.setTextSize(14);
                editText.setKeyListener(null);
                row.addView(editText);
                editTexts.add(editText);
            }
            Set set = sets.get(i);
            editTexts.get(0).setText(String.valueOf(i + 1));
            editTexts.get(1).setText(String.valueOf(set.getId()));

            //Reps display
            if (set.getReps() == RMUtils.REPS_MAX)
                editTexts.get(2).setText(getString(R.string.max));
            else
                editTexts.get(2).setText(String.valueOf(set.getReps()));

            editTexts.get(3).setText(String.valueOf(set.getWeight()));

            layout.addView(row);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonConfirmMesocycle:
                mesocycle.setActive(true);
                mesocyclesDataSource.update(mesocycle);

                Intent intent = new Intent(this, TrainingJournalActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (!mesocycle.isActive()) {
            mesocyclesDataSource.delete(mesocycleId);
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsActivity = new Intent(this, Preferences.class);
                startActivity(settingsActivity);
                return true;
            case R.id.action_help:
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}