package com.kozzztya.cycletraining.trainingjournal;

import android.content.Intent;
import android.os.Bundle;
import com.kozzztya.cycletraining.BaseActivity;
import com.kozzztya.cycletraining.MainActivity;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.trainingcreate.TrainingCreateFragment;
import com.kozzztya.cycletraining.trainingprocess.TrainingProcessActivity;

public class TrainingDayActivity extends BaseActivity implements
        TrainingDayFragment.TrainingDayCallbacks {

    private static final String TAG = "log" + TrainingDayActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // During initial setup, plug in fragment
            TrainingDayFragment trainingDayFragment = new TrainingDayFragment();
            // Pass intent extras to the fragment
            trainingDayFragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, trainingDayFragment)
                    .commit();
        }
    }

    @Override
    public void onTrainingAdd(long trainingDay) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(TrainingCreateFragment.KEY_BEGIN_DATE, trainingDay);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @Override
    public void onTrainingSort(long trainingDay) {
        Intent intent = new Intent(this, TrainingSortActivity.class);
        intent.putExtra(TrainingSortFragment.KEY_TRAINING_DAY, trainingDay);
        startActivity(intent);
    }

    @Override
    public void onTrainingProcessStart(long trainingDay, int trainingPosition) {
        Intent intent = new Intent(this, TrainingProcessActivity.class);
        intent.putExtra(TrainingProcessActivity.KEY_TRAINING_DAY, trainingDay);
        intent.putExtra(TrainingProcessActivity.KEY_POSITION, trainingPosition);
        startActivity(intent);
    }
}