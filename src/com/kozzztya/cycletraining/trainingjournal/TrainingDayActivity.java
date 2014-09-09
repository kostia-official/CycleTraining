package com.kozzztya.cycletraining.trainingjournal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.kozzztya.cycletraining.MyActionBarActivity;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.entities.TrainingView;
import com.kozzztya.cycletraining.trainingcreate.TrainingCreateActivity;
import com.kozzztya.cycletraining.trainingprocess.TrainingProcessActivity;

import java.util.ArrayList;
import java.util.List;

public class TrainingDayActivity extends MyActionBarActivity implements
        TrainingDayFragment.TrainingDayCallbacks {

    private static final String TAG = "log" + TrainingDayActivity.class.getSimpleName();

    public static final String KEY_TRAININGS = "trainings";

    private List<TrainingView> mTrainingsByDay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            //Restore data from saved instant state
            retrieveData(savedInstanceState);
        } else {
            //Retrieve data from intent
            retrieveData(getIntent().getExtras());

            // During initial setup, plug in fragment.
            TrainingDayFragment trainingDayFragment = TrainingDayFragment.newInstance(mTrainingsByDay);
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, trainingDayFragment)
                    .commit();
        }
    }

    @Override
    public void onTrainingAdd(long date) {
        Intent intent = new Intent(this, TrainingCreateActivity.class);
        intent.putExtra(TrainingCreateActivity.KEY_BEGIN_DATE, date);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @Override
    public void onTrainingSort(List<TrainingView> trainings) {
        if (trainings.size() > 1) {
            Intent intent = new Intent(this, TrainingSortActivity.class);
            intent.putParcelableArrayListExtra(TrainingSortActivity.TRAINING_LIST,
                    (ArrayList<TrainingView>) trainings);
            startActivity(intent);
        } else {
            //To sort user need at least two workouts
            Toast.makeText(this, R.string.toast_sort_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTrainingProcessStart(List<TrainingView> trainings, long chosenTrainingId) {
        Intent intent = new Intent(this, TrainingProcessActivity.class);
        intent.putParcelableArrayListExtra(TrainingProcessActivity.KEY_TRAININGS,
                (ArrayList<TrainingView>) trainings);
        intent.putExtra(TrainingProcessActivity.KEY_CHOSEN_TRAINING_ID, chosenTrainingId);
        startActivity(intent);
    }

    private void retrieveData(Bundle bundle) {
        if (bundle != null) {
            mTrainingsByDay = bundle.getParcelableArrayList(KEY_TRAININGS);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_TRAININGS, (ArrayList<TrainingView>) mTrainingsByDay);
        super.onSaveInstanceState(outState);
    }
}