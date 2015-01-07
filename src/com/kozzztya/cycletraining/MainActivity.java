package com.kozzztya.cycletraining;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.kozzztya.cycletraining.statistic.StatisticCreateFragment;
import com.kozzztya.cycletraining.statistic.StatisticShowFragment;
import com.kozzztya.cycletraining.trainingcreate.ExercisesFragment;
import com.kozzztya.cycletraining.trainingcreate.ProgramsFragment;
import com.kozzztya.cycletraining.trainingcreate.TrainingCreateFragment;
import com.kozzztya.cycletraining.trainingcreate.TrainingPlanFragment;
import com.kozzztya.cycletraining.trainingjournal.TrainingCalendarFragment;
import com.kozzztya.cycletraining.trainingjournal.TrainingDayFragment;
import com.kozzztya.cycletraining.trainingjournal.TrainingSortFragment;
import com.kozzztya.cycletraining.trainingjournal.TrainingWeekFragment;
import com.kozzztya.cycletraining.trainingprocess.TrainingProcessFragment;


public class MainActivity extends ActionBarActivity implements TrainingWeekFragment.TrainingWeekCallbacks,
        TrainingCreateFragment.TrainingCreateCallbacks, StatisticCreateFragment.StatisticCreateCallbacks,
        NavigationDrawerFragment.NavigationDrawerCallbacks, TrainingDayFragment.TrainingDayCallbacks,
        TrainingCalendarFragment.TrainingCalendarCallbacks, TrainingPlanFragment.TrainingPlanCallbacks,
        ExercisesFragment.ExercisesCallbacks, ProgramsFragment.ProgramsCallbacks,
        FragmentManager.OnBackStackChangedListener {

    private static final String TAG = "log" + MainActivity.class.getSimpleName();

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        getNavigationDrawerFragment().setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startFragment(new SettingsFragment());
                return true;
            case R.id.action_help:
                return true;
            case R.id.action_about:
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    /**
     * Quick access to NavigationDrawerFragment
     */
    public NavigationDrawerFragment getNavigationDrawerFragment() {
        return (NavigationDrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navigation_drawer);
    }

    @Override
    public void onTrainingCreated(Uri mesocycleUri) {
        Fragment fragment = TrainingPlanFragment.newInstance(mesocycleUri);
        startFragment(fragment);
    }

    @Override
    public void onExerciseRequest(int requestCode) {
        Fragment trainingCreateFragment = getSupportFragmentManager().findFragmentByTag(
                TrainingCreateFragment.class.getSimpleName());
        Fragment exercisesFragment = new ExercisesFragment();

        exercisesFragment.setTargetFragment(trainingCreateFragment, requestCode);

        startFragment(exercisesFragment);
    }

    @Override
    public void onExerciseSelected(Uri exerciseUri) {
        Fragment exercisesFragment = getSupportFragmentManager().findFragmentByTag(
                ExercisesFragment.class.getSimpleName());
        Intent intent = new Intent().putExtra(TrainingCreateFragment.KEY_EXERCISE_URI, exerciseUri);
        exercisesFragment.getTargetFragment().onActivityResult(
                exercisesFragment.getTargetRequestCode(), Activity.RESULT_OK, intent);
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onProgramRequest(int requestCode) {
        Fragment trainingCreateFragment = getSupportFragmentManager().findFragmentByTag(
                TrainingCreateFragment.class.getSimpleName());
        Fragment programsFragment = new ProgramsFragment();

        programsFragment.setTargetFragment(trainingCreateFragment, requestCode);

        startFragment(programsFragment);
    }

    @Override
    public void onProgramSelected(Uri programUri) {
        Fragment programsFragment = getSupportFragmentManager().findFragmentByTag(
                ProgramsFragment.class.getSimpleName());
        Intent intent = new Intent().putExtra(TrainingCreateFragment.KEY_PROGRAM_URI, programUri);
        programsFragment.getTargetFragment().onActivityResult(
                programsFragment.getTargetRequestCode(), Activity.RESULT_OK, intent);
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onTrainingCreate(long trainingDay) {
        Fragment fragment = TrainingCreateFragment.newInstance(trainingDay);
        startFragment(fragment);
    }

    @Override
    public void onTrainingSort(long trainingDay) {
        Fragment fragment = TrainingSortFragment.newInstance(trainingDay);
        startFragment(fragment);
    }

    @Override
    public void onTrainingSelected(long trainingDay, int trainingPosition) {
        Fragment fragment = TrainingProcessFragment.newInstance(trainingDay, trainingPosition);
        startFragment(fragment);
    }

    @Override
    public void onTrainingDaySelected(long trainingDay) {
        Fragment fragment = TrainingDayFragment.newInstance(trainingDay);
        startFragment(fragment);
    }

    @Override
    public void onCalendarShow() {
        Fragment fragment = TrainingCalendarFragment.newInstance(
                new Preferences(this).getFirstDayOfWeek());
        startFragment(fragment);
    }

    @Override
    public void onSelectCalendarDate(long trainingDay) {
        Fragment fragment = TrainingDayFragment.newInstance(trainingDay);
        startFragment(fragment);
    }

    @Override
    public void onStatisticShow(long exerciseId, String resultFunc, String values, String period) {
        Fragment fragment = StatisticShowFragment.newInstance(
                exerciseId, resultFunc, values, period);
        startFragment(fragment);
    }

    @Override
    public void onTrainingPlanConfirmed() {
        // Pop back stack to TrainingDayFragment.
        if (!getSupportFragmentManager().popBackStackImmediate(
                TrainingDayFragment.class.getSimpleName(), 0)) {
            // Go to the back stack root and open TrainingWeekFragment.
            getSupportFragmentManager().popBackStack(null, 0);
            replaceFragment(new TrainingWeekFragment());
        }
    }

    /**
     * Fully set new title
     */
    @Override
    public void setTitle(CharSequence title) {
        mToolbar.setSubtitle(null);
        mToolbar.setTitle(title);
    }

    @Override
    public void setTitle(int titleId) {
        setTitle(getString(titleId));
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    /**
     * Replace a fragment by drawer navigation.
     */
    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment, fragment.getClass().getSimpleName())
                .commit();
    }

    /**
     * Start a fragment and add it to back stack.
     */
    public void startFragment(Fragment fragment) {
        String fragmentName = fragment.getClass().getSimpleName();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment, fragmentName)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(fragmentName)
                .commit();
    }

    /**
     * On Navigate Up go to the back stack.
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Show Drawer only on the back stack root
     */
    @Override
    public void onBackStackChanged() {
        NavigationDrawerFragment navigationDrawerFragment = getNavigationDrawerFragment();

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            navigationDrawerFragment.showDrawer();
        } else if (!navigationDrawerFragment.isHidden()) {
            navigationDrawerFragment.hideDrawer();
        }
    }
}
