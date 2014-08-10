package com.kozzztya.cycletraining.trainingprocess;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.kozzztya.cycletraining.MyActionBarActivity;
import com.kozzztya.cycletraining.Preferences;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.adapters.TrainingPagerAdapter;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.datasources.SetsDS;
import com.kozzztya.cycletraining.db.datasources.TrainingsDS;
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.db.entities.TrainingView;
import com.kozzztya.cycletraining.utils.DateUtils;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class TrainingProcessActivity extends MyActionBarActivity implements OnSharedPreferenceChangeListener {

    private TrainingPagerAdapter trainingPagerAdapter;
    private ViewPager viewPager;

    private TrainingsDS trainingsDS;
    private SetsDS setsDS;

    //Collection for sets of trainings
    private LinkedHashMap<TrainingView, List<Set>> trainingsSets;
    private List<TrainingView> trainingsByDay;

    //Timer fields
    private CountDownTimer timer;
    private boolean isTimerStarted;
    private final long SECOND = 1000;
    private int startTime;
    private MenuItem timerItem;
    private Preferences preferences;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_process);

        dbHelper = DBHelper.getInstance(this);
        trainingsDS = new TrainingsDS(dbHelper);
        setsDS = new SetsDS(dbHelper);

        preferences = new Preferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);

        initTrainingData();
        initTimer();
    }

    private void initTrainingData() {
        Bundle extras = getIntent().getExtras();
        Date dayOfTrainings = new Date(extras.getLong("dayOfTraining"));
        long chosenTrainingId = extras.getLong("chosenTrainingId");

        //Select trainings by chosen day
        String where = TrainingsDS.COLUMN_DATE + " = " + DateUtils.sqlFormat(dayOfTrainings);
        String orderBy = TrainingsDS.COLUMN_PRIORITY;
        trainingsByDay = trainingsDS.selectView(where, null, null, orderBy);
        trainingsSets = new LinkedHashMap<>();

        int chosenTrainingPage = 0;
        for (TrainingView t : trainingsByDay) {
            //Select sets of training
            where = SetsDS.COLUMN_TRAINING + " = " + t.getId();
            List<Set> sets = setsDS.select(where, null, null, null);
            trainingsSets.put(t, sets);

            //Determine chosen training page
            if (t.getId() == chosenTrainingId)
                chosenTrainingPage = trainingsByDay.indexOf(t);
        }

        //Adapter for pages with sets of trainings
        trainingPagerAdapter = new TrainingPagerAdapter(getSupportFragmentManager(), trainingsSets);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(trainingPagerAdapter);
        viewPager.setCurrentItem(chosenTrainingPage);
    }

    private void initTimer() {
        isTimerStarted = false;
        startTime = preferences.getTimerValue();
        timer = new CountDownTimer(startTime * SECOND, SECOND) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerItem.setTitle(String.valueOf(millisUntilFinished / SECOND));
                isTimerStarted = true;
            }

            @Override
            public void onFinish() {
                timerItem.setIcon(R.drawable.ic_action_timer);
                timerItem.setTitle(getString(R.string.action_timer));
                if (preferences.isVibrateTimer()) {
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(SECOND);
                }
                isTimerStarted = false;
            }
        };
    }

    public void doneClick(View view) {
        int i = viewPager.getCurrentItem();
        TrainingView training = trainingsByDay.get(i);

        //Update in DB set info
        List<Set> sets = trainingsSets.get(training);
        for (int j = 0; j < sets.size(); j++) {
            Set s = sets.get(j);

            //Use valueOf to validate number format of reps
            try {
                Integer.parseInt(s.getReps());
            } catch (NumberFormatException e) {
                Toast.makeText(this, String.format(getString(R.string.toast_input), j + 1), Toast.LENGTH_LONG).show();
                return;
            }
            setsDS.update(s);

            //Update in DB training status
            training.setDone(true);
            trainingsDS.update(training);
        }
        dbHelper.notifyDBChanged();

        //If on the last tab
        if (i == trainingPagerAdapter.getCount() - 1)
            finish();
        else
            //Go to the next tab
            viewPager.setCurrentItem(i + 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.training_process, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_timer:
                timerItem = item;
                if (!isTimerStarted) {
                    item.setTitle(String.valueOf(startTime)).setIcon(null);
                    timer.start();
                    isTimerStarted = true;
                } else {
                    item.setTitle(getString(R.string.action_timer))
                            .setIcon(R.drawable.ic_action_timer);
                    timer.cancel();
                    isTimerStarted = false;
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        initTimer();
    }

    @Override
    public void onDestroy() {
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

}