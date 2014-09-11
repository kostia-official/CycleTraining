package com.kozzztya.cycletraining.trainingjournal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.kozzztya.cycletraining.MyActionBarActivity;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.entities.TrainingView;
import com.kozzztya.cycletraining.trainingcreate.TrainingCreateActivity;
import com.kozzztya.cycletraining.trainingcreate.TrainingCreateFragment;
import com.kozzztya.cycletraining.trainingprocess.TrainingProcessActivity;

import java.util.ArrayList;
import java.util.List;

public class TrainingDayActivity extends MyActionBarActivity implements
        TrainingDayFragment.TrainingDayCallbacks {

    private static final String TAG = "log" + TrainingDayActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            //During initial setup, plug in fragment
            TrainingDayFragment trainingDayFragment = new TrainingDayFragment();
            //Pass intent extras to the fragment
            trainingDayFragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, trainingDayFragment)
                    .commit();
        }
    }

    @Override
    public void onTrainingAdd(long date) {
        Intent intent = new Intent(this, TrainingCreateActivity.class);
        intent.putExtra(TrainingCreateFragment.KEY_BEGIN_DATE, date);
        startActivity(intent);
    }

    @Override
    public void onTrainingSort(List<TrainingView> trainings) {
        if (trainings.size() > 1) {
            Intent intent = new Intent(this, TrainingSortActivity.class);
            intent.putParcelableArrayListExtra(TrainingSortFragment.TRAININGS,
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
}