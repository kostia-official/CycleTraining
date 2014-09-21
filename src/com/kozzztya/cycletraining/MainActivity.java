package com.kozzztya.cycletraining;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import com.espian.showcaseview.ShowcaseView;
import com.espian.showcaseview.actionbar.reflection.BaseReflector;
import com.espian.showcaseview.targets.ViewTarget;
import com.kozzztya.cycletraining.statistic.StatisticCreateFragment;
import com.kozzztya.cycletraining.statistic.StatisticShowActivity;
import com.kozzztya.cycletraining.statistic.StatisticShowFragment;
import com.kozzztya.cycletraining.trainingcreate.*;
import com.kozzztya.cycletraining.trainingjournal.TrainingCalendarActivity;
import com.kozzztya.cycletraining.trainingjournal.TrainingDayActivity;
import com.kozzztya.cycletraining.trainingjournal.TrainingDayFragment;
import com.kozzztya.cycletraining.trainingjournal.TrainingWeekFragment;
import com.kozzztya.cycletraining.trainingprocess.TrainingProcessActivity;

import java.sql.Date;

public class MainActivity extends MyActionBarActivity implements TrainingWeekFragment.TrainingWeekCallbacks,
        TrainingCreateFragment.TrainingCreateCallbacks, StatisticCreateFragment.StatisticCreateCallbacks,
        NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final String TAG = "log" + MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        if (new Preferences(this).isFirstRun()) {
            BaseReflector reflector = BaseReflector.getReflectorForActivity(this);
            View homeButton = reflector.getHomeButton();
            ShowcaseView.insertShowcaseView(new ViewTarget(homeButton), this,
                    R.string.showcase_title, R.string.showcase_first_start_text);
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        switch (position) {
            case 0:
                replaceFragment(new TrainingCreateFragment());
                break;
            case 1:
                replaceFragment(new TrainingWeekFragment());
                break;
            case 2:
                replaceFragment(new StatisticCreateFragment());
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey(TrainingCreateFragment.KEY_BEGIN_DATE)) {
                TrainingCreateFragment fragment = new TrainingCreateFragment();

                Date date = new Date(extras.getLong(TrainingCreateFragment.KEY_BEGIN_DATE));
                Bundle bundle = new Bundle();
                bundle.putLong(TrainingCreateFragment.KEY_BEGIN_DATE, date.getTime());
                fragment.setArguments(bundle);

                replaceFragment(fragment);
            }
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment, fragment.getClass().getSimpleName())
                .commit();
    }

    @Override
    public void onTrainingCreated(Uri mesocycleUri) {
        Intent intent = new Intent(this, TrainingPlanActivity.class);
        intent.putExtra(TrainingPlanFragment.KEY_MESOCYCLE_URI, mesocycleUri);
        startActivity(intent);
    }

    @Override
    public void onExerciseRequest(int requestCode) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(TrainingCreateFragment.class.getSimpleName());

        if (fragment != null) {
            Intent intent = new Intent(this, ExercisesActivity.class);
            fragment.startActivityForResult(intent, requestCode);
        }
    }

    @Override
    public void onProgramRequest(int requestCode) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(TrainingCreateFragment.class.getSimpleName());

        if (fragment != null) {
            Intent intent = new Intent(this, ProgramsActivity.class);
            fragment.startActivityForResult(intent, requestCode);
        }
    }

    @Override
    public void onTrainingProcessStart(long trainingDay, int trainingPosition) {
        Intent intent = new Intent(this, TrainingProcessActivity.class);
        intent.putExtra(TrainingProcessActivity.KEY_TRAINING_DAY, trainingDay);
        intent.putExtra(TrainingProcessActivity.KEY_POSITION, trainingPosition);
        startActivity(intent);
    }

    @Override
    public void onTrainingDayStart(long trainingDay) {
        Intent intent = new Intent(this, TrainingDayActivity.class);
        intent.putExtra(TrainingDayFragment.KEY_TRAINING_DAY, trainingDay);
        startActivity(intent);
    }

    @Override
    public void onCalendarShow() {
        Intent intent = new Intent(this, TrainingCalendarActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStatisticShow(long exerciseId, String resultFunc, String values, String period) {
        Intent intent = new Intent(this, StatisticShowActivity.class);
        intent.putExtra(StatisticShowFragment.KEY_EXERCISE_ID, exerciseId);
        intent.putExtra(StatisticShowFragment.KEY_RESULT_FUNC, resultFunc);
        intent.putExtra(StatisticShowFragment.KEY_VALUES, values);
        intent.putExtra(StatisticShowFragment.KEY_PERIOD, period);
        startActivity(intent);
    }
}
