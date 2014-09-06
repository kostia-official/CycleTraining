package com.kozzztya.cycletraining.trainingjournal;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
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

public class TrainingSortActivity extends MyActionBarActivity implements DragSortListView.DropListener, View.OnClickListener {

    public static final String TRAINING_LIST = "trainingList";

    private ArrayList<TrainingView> mTrainingList;
    private ArrayAdapter<TrainingView> mTrainingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_drag_sort);

        if (savedInstanceState != null) {
            //Restore data from saved instant state
            retrieveData(savedInstanceState);
        } else {
            //Retrieve data from intent
            retrieveData(getIntent().getExtras());
        }

        initDragSortListView();
    }

    private void retrieveData(Bundle bundle) {
        if (bundle != null) {
            mTrainingList = bundle.getParcelableArrayList(TRAINING_LIST);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(TRAINING_LIST, mTrainingList);
        super.onSaveInstanceState(outState);
    }

    private void initDragSortListView() {
        DragSortListView dragSortListView = (DragSortListView) findViewById(R.id.drag_sort_listview);
        mTrainingAdapter = new ArrayAdapter<>(this, R.layout.drag_sort_list_item, R.id.textViewTrainingTitle, mTrainingList);
        dragSortListView.setAdapter(mTrainingAdapter);
        dragSortListView.setDropListener(this);
        dragSortListView.setDragEnabled(true);

        DragSortController controller = new DragSortController(dragSortListView);
        controller.setRemoveEnabled(false);
        controller.setSortEnabled(true);
        controller.setDragInitMode(DragSortController.ON_DOWN);
        controller.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        dragSortListView.setFloatViewManager(controller);
        dragSortListView.setOnTouchListener(controller);

        //Normalize training priority
        int count = mTrainingAdapter.getCount();
        for (int i = 0; i < count; i++) {
            mTrainingAdapter.getItem(i).setPriority(i);
        }
    }

    @Override
    public void drop(int from, int to) {
        if (from != to) {
            //Change sql priority value
            mTrainingAdapter.getItem(from).setPriority(to);
            mTrainingAdapter.getItem(to).setPriority(from);

            //Change listview order
            TrainingView item = mTrainingAdapter.getItem(from);
            mTrainingAdapter.remove(item);
            mTrainingAdapter.insert(item, to);
        }
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
        DBHelper dbHelper = DBHelper.getInstance(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        //Get day of training for sort
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mTrainingList.get(0).getDate());
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        for (Training t : mTrainingList) {
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