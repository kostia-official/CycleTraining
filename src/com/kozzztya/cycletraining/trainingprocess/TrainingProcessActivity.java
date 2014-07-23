package com.kozzztya.cycletraining.trainingprocess;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;
import com.kozzztya.cycletraining.MyActionBarActivity;
import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.adapters.TrainingPagerAdapter;
import com.kozzztya.cycletraining.db.datasources.SetsDS;
import com.kozzztya.cycletraining.db.datasources.TrainingsDS;
import com.kozzztya.cycletraining.db.entities.Set;
import com.kozzztya.cycletraining.db.entities.TrainingView;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;

public class TrainingProcessActivity extends MyActionBarActivity {

    private ViewPager viewPager;

    private TrainingsDS trainingsDS;
    private SetsDS setsDS;

    //Collection for sets on training
    private LinkedHashMap<TrainingView, List<Set>> trainingsSets;
    private List<TrainingView> trainingsByDay;
    private TrainingPagerAdapter trainingPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_process);

        trainingsDS = new TrainingsDS(this);
        setsDS = new SetsDS(this);

        //Получение дня тренировок и выбранной тренировки
        Bundle extras = getIntent().getExtras();
        Date dayOfTrainings = new Date(extras.getLong("dayOfTraining"));
        long chosenTrainingId = extras.getLong("chosenTrainingId");

        //Select trainings by day
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String where = TrainingsDS.COLUMN_DATE + " = '" + dateFormat.format(dayOfTrainings) + "'";
        String orderBy = TrainingsDS.COLUMN_DATE;
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

        //Адаптер для вкладок с подходами тренировок
        trainingPagerAdapter = new TrainingPagerAdapter(getSupportFragmentManager(), trainingsSets);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(trainingPagerAdapter);
        viewPager.setCurrentItem(chosenTrainingPage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.training_process, menu);
        return super.onCreateOptionsMenu(menu);
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

        //If on the last tab
        if (i == trainingPagerAdapter.getCount() - 1)
            finish();
        else
            //Go to the next tab
            viewPager.setCurrentItem(i + 1);
    }

}