package com.kozzztya.cycletraining.trainingjournal;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.datasources.TrainingsDS;
import com.kozzztya.cycletraining.db.entities.Training;
import com.kozzztya.cycletraining.db.entities.TrainingView;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import java.util.ArrayList;
import java.util.Calendar;

public class TrainingSortFragment extends ListFragment implements DragSortListView.DropListener,
        View.OnClickListener {

    public static final String TRAININGS = "trainings";

    private ArrayList<TrainingView> mTrainings;
    private ArrayAdapter<TrainingView> mTrainingAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            //Restore data from saved instant state
            retrieveData(savedInstanceState);
        } else {
            //Retrieve data from intent
            retrieveData(getArguments());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.training_drag_sort, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpDragSortListView();
        normalizeTrainingPriority();
    }

    private void setUpDragSortListView() {
        DragSortListView dragSortListView = (DragSortListView) getListView();
        DragSortController controller = new DragSortController(dragSortListView);
        controller.setSortEnabled(true);
        controller.setRemoveEnabled(false);
        controller.setDragInitMode(DragSortController.ON_DOWN);
        controller.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        dragSortListView.setFloatViewManager(controller);
        dragSortListView.setOnTouchListener(controller);
        dragSortListView.setDropListener(this);
        dragSortListView.setDragEnabled(true);

        mTrainingAdapter = new ArrayAdapter<>(getActivity(), R.layout.drag_sort_list_item,
                R.id.textViewTrainingTitle, mTrainings);
        dragSortListView.setAdapter(mTrainingAdapter);
    }

    /**
     * Use list item position as a training priority
     */
    private void normalizeTrainingPriority() {
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

    private void retrieveData(Bundle bundle) {
        if (bundle != null) {
            mTrainings = bundle.getParcelableArrayList(TRAININGS);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(TRAININGS, mTrainings);
        super.onSaveInstanceState(outState);
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
        DBHelper dbHelper = DBHelper.getInstance(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //Get day of training for sort
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mTrainings.get(0).getDate());
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        db.beginTransaction();
        try {
            for (Training t : mTrainings) {
                String query = "UPDATE " + TrainingsDS.TABLE_NAME +
                        //Set priority
                        " SET " + TrainingsDS.COLUMN_PRIORITY + " = " + t.getPriority() +
                        //for each trainings of mesocycle
                        " WHERE " + TrainingsDS.COLUMN_MESOCYCLE + " = " + t.getMesocycle() +
                        //in chosen day of week
                        " AND strftime('%w', " + TrainingsDS.COLUMN_DATE + ") = '" + dayOfWeek + "'";

                db.execSQL(query);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.close();
        dbHelper.notifyDBChanged();
        getActivity().finish();
    }
}
