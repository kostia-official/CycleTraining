package com.kozzztya.cycletraining.statistic;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.Spinner;
import com.kozzztya.cycletraining.DrawerActivity;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.datasources.ExercisesDS;
import com.kozzztya.cycletraining.db.datasources.TrainingJournalDS;


public class StatisticCreateActivity extends DrawerActivity {

    private Spinner spinnerExercises;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.statistic_create);

        spinnerExercises = (Spinner) findViewById(R.id.spinnerExercise);
        setTitle(getString(R.string.statistic));
    }

    @Override
    protected void onStart() {
        fillExerciseSpinner();
        super.onStart();
    }

    private void fillExerciseSpinner() {
        //Select exercises that used in training journal
        SQLiteDatabase db = DBHelper.getInstance(this).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT e._id as _id, " + ExercisesDS.COLUMN_NAME + " from " + ExercisesDS.TABLE_NAME +
                " e, " + TrainingJournalDS.TABLE_NAME + " tj \n" +
                "WHERE tj." + TrainingJournalDS.COLUMN_EXERCISE + " = e._id GROUP BY " + ExercisesDS.COLUMN_NAME, null);

        String[] adapterCols = new String[]{ExercisesDS.COLUMN_NAME, "_id"};
        int[] adapterRowViews = new int[]{android.R.id.text1};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item,
                cursor, adapterCols, adapterRowViews, 0);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExercises.setAdapter(adapter);
    }

    public void confirmClick(View view) {
        Cursor cursor = (Cursor) spinnerExercises.getSelectedItem();

        if (cursor != null) {
            Spinner spinnerValue = (Spinner) findViewById(R.id.spinnerValue);
            Spinner spinnerСriterion = (Spinner) findViewById(R.id.spinnerСriterion);
            Spinner spinnerPeriod = (Spinner) findViewById(R.id.spinnerPeriod);

            long exerciseId = cursor.getLong(cursor.getColumnIndex("_id"));
            String resultFunc = (String) spinnerValue.getSelectedItem();
            String values = (String) spinnerСriterion.getSelectedItem();
            String period = (String) spinnerPeriod.getSelectedItem();

            Intent intent = new Intent(this, StatisticShowActivity.class);
            intent.putExtra("exerciseId", exerciseId);
            intent.putExtra("resultFunc", resultFunc);
            intent.putExtra("values", values);
            intent.putExtra("period", period);
            startActivity(intent);
        }
    }
}