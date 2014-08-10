package com.kozzztya.cycletraining.trainingjournal;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import com.kozzztya.cycletraining.MyActionBarActivity;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.datasources.TrainingsDS;
import com.kozzztya.cycletraining.db.entities.Training;
import com.kozzztya.cycletraining.db.entities.TrainingView;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import java.util.ArrayList;
import java.util.Calendar;

public class TrainingSortActivity extends MyActionBarActivity implements DragSortListView.DropListener {

    private ArrayList<TrainingView> trainingsByDay;
    private ArrayAdapter<TrainingView> trainingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_sort);
        getSupportActionBar().setTitle(R.string.action_sort);

        retrieveExtras();
        initDragSortListView();
    }

    private void retrieveExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            trainingsByDay = extras.getParcelableArrayList("trainingsByDay");
        } else {
            finish();
        }
    }

    private void initDragSortListView() {
        DragSortListView dragSortListView = (DragSortListView) findViewById(R.id.drag_sort_listview);
        trainingAdapter = new ArrayAdapter<>(this, R.layout.drag_sort_list_item, R.id.textViewTrainingTitle, trainingsByDay);
        dragSortListView.setAdapter(trainingAdapter);
        dragSortListView.setDropListener(this);
        dragSortListView.setDragEnabled(true);

        DragSortController controller = new DragSortController(dragSortListView);
        controller.setRemoveEnabled(false);
        controller.setSortEnabled(true);
        controller.setDragInitMode(DragSortController.ON_DOWN);
        controller.setBackgroundColor(getResources().getColor(R.color.light_gray));

        dragSortListView.setFloatViewManager(controller);
        dragSortListView.setOnTouchListener(controller);

        //Normalize training priority
        int count = trainingAdapter.getCount();
        for (int i = 0; i < count; i++) {
            trainingAdapter.getItem(i).setPriority(i);
        }
    }

    @Override
    public void drop(int from, int to) {
        if (from != to) {
            //Change sql priority value
            trainingAdapter.getItem(from).setPriority(to);
            trainingAdapter.getItem(to).setPriority(from);

            //Change listview order
            TrainingView item = trainingAdapter.getItem(from);
            trainingAdapter.remove(item);
            trainingAdapter.insert(item, to);
        }
    }

    public void onDone(View view) {
        DBHelper dbHelper = DBHelper.getInstance(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        //Get day of training for sort
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(trainingsByDay.get(0).getDate());
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        for (Training t : trainingsByDay) {
            String query = "UPDATE " + TrainingsDS.TABLE_NAME +
                    " SET " + TrainingsDS.COLUMN_PRIORITY + " = " + t.getPriority() +             //Set priority
                    " WHERE " + TrainingsDS.COLUMN_MESOCYCLE + " = " + t.getMesocycle() +         //for each trainings of mesocycle
                    " AND strftime('%w', " + TrainingsDS.COLUMN_DATE + ") = '" + dayOfWeek + "'"; //in chosen day of week

            database.execSQL(query);
        }

        database.close();
        dbHelper.notifyDBChanged();
        finish();
    }
}