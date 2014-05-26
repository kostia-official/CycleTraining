package com.kozzztya.cycletraining;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableRow;
import android.widget.TextView;
import com.kozzztya.cycletraining.db.entities.Exercise;
import com.kozzztya.cycletraining.db.entities.Mesocycle;
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.db.helpers.ExercisesHelper;
import com.kozzztya.cycletraining.db.helpers.MesocyclesHelper;
import com.kozzztya.cycletraining.db.helpers.SetsHelper;

import java.util.ArrayList;
import java.util.List;

public class MesocycleShowActivity extends Activity {

    private MesocyclesHelper mesocyclesHelper;
    private long mesocycleId;
    private final int TABLE_COLS = 4;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mesocycle);

        Bundle extras = getIntent().getExtras();
        //TODO обработка ошибочных запросов БД
        mesocycleId = extras.getLong("mesocycleId");
        mesocyclesHelper = new MesocyclesHelper(this);
        Mesocycle mesocycle = mesocyclesHelper.getMesocycle(mesocycleId);

        ExercisesHelper exercisesHelper = new ExercisesHelper(this);
        Exercise exercise = exercisesHelper.getExercise(mesocycle.getExercise());
        setTitle(exercise.getName());

        buildTable();
    }

    private void buildTable() {
        SetsHelper setsHelper = new SetsHelper(this);
        List<Set> sets = setsHelper.selectGroupedSets(SetsHelper.COLUMN_MESOCYCLE + " = " + mesocycleId, null);

        for (int i = 0; i < sets.size(); i++) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            List<TextView> textViews = new ArrayList<>();
            for (int j = 0; j < TABLE_COLS; j++) {
                TextView tv = new TextView(this);
                tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(18);
                row.addView(tv);
                textViews.add(tv);
            }
            Set set = sets.get(i);
            textViews.get(0).setText(String.valueOf(i+1));
            textViews.get(1).setText(String.valueOf(set.getId()));
            textViews.get(2).setText(String.valueOf(set.getReps()));
            textViews.get(3).setText(String.valueOf(set.getWeight()));
        }
    }

    @Override
    protected void onDestroy() {
        mesocyclesHelper.delete(mesocycleId);
        super.onDestroy();
    }
}