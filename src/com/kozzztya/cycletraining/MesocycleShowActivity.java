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
import com.kozzztya.cycletraining.db.entities.Exercise;
import com.kozzztya.cycletraining.db.entities.Mesocycle;
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.db.helpers.ExercisesHelper;
import com.kozzztya.cycletraining.db.helpers.MesocyclesHelper;
import com.kozzztya.cycletraining.db.helpers.SetsHelper;

import java.util.ArrayList;
import java.util.List;

public class MesocycleShowActivity extends ActionBarActivity implements OnClickListener {

    private MesocyclesHelper mesocyclesHelper;
    private long mesocycleId;
    private Mesocycle mesocycle;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mesocycle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Bundle extras = getIntent().getExtras();
        //TODO обработка ошибочных запросов БД
        mesocycleId = extras.getLong("mesocycleId");
        mesocyclesHelper = new MesocyclesHelper(this);

        mesocycle = mesocyclesHelper.getEntity(mesocycleId);

        EditText editTextRM = (EditText) findViewById(R.id.editTextRM);
        editTextRM.setText(String.format("%.2f", mesocycle.getRm()));
        editTextRM.setKeyListener(null);

        ExercisesHelper exercisesHelper = new ExercisesHelper(this);
        Exercise exercise = exercisesHelper.getEntity(mesocycle.getExercise());
        setTitle(exercise.getName());

        Button buttonConfirm = (Button) findViewById(R.id.buttonConfirmMesocycle);
        buttonConfirm.setOnClickListener(this);

        buildTable();
    }

    private void buildTable() {
        SetsHelper setsHelper = new SetsHelper(this);
        List<Set> sets = setsHelper.selectGroupedSets(SetsHelper.COLUMN_MESOCYCLE + " = " + mesocycleId, null);
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
            editTexts.get(2).setText(String.valueOf(set.getReps()));
            //TODO Округлять вес в зависимости от настроек
            editTexts.get(3).setText(String.valueOf((int)set.getWeight()));
            layout.addView(row);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonConfirmMesocycle:
                mesocycle.setActive(true);
                mesocyclesHelper.update(mesocycle);

                Intent intent = new Intent(this, TrainingJournalActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (!mesocycle.isActive()) {
            mesocyclesHelper.delete(mesocycleId);
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
            case R.id.action_calendar:
                finish();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}