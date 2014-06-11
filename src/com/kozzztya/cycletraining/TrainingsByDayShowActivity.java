package com.kozzztya.cycletraining;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.kozzztya.cycletraining.db.entities.SetView;
import com.kozzztya.cycletraining.db.entities.TrainingView;
import com.kozzztya.cycletraining.db.helpers.SetsHelper;
import com.kozzztya.cycletraining.db.helpers.TrainingsHelper;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;

public class TrainingsByDayShowActivity extends Activity {

    private TrainingsHelper trainingsHelper;
    private SetsHelper setsHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trainings_by_day);

        //Получение даты выбранного дня тренировок
        Bundle extras = getIntent().getExtras();
        Date dayOfTrainings = new Date(extras.getLong("dayOfTrainings"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        trainingsHelper = new TrainingsHelper(this);
        setsHelper = new SetsHelper(this);

        //Коллекция для хранения тренировок и их подходов
        LinkedHashMap<TrainingView, List<SetView>> trainingSets = new LinkedHashMap<>();

        //Получаем с базы коллекцию тренировок за день
        String where = TrainingsHelper.COLUMN_DATE + " = '" + dateFormat.format(dayOfTrainings) + "'";
        String orderBy = TrainingsHelper.COLUMN_DATE;
        List<TrainingView> trainingsByWeek = trainingsHelper.selectView(where, null, null, orderBy);

        //Получаем для каждой тренировки подходы
        for (TrainingView t : trainingsByWeek) {
            where = SetsHelper.COLUMN_TRAINING + " = " + t.getId();
            orderBy = SetsHelper.COLUMN_ID;
            List<SetView> sets = setsHelper.selectView(where, null, null, orderBy);

            trainingSets.put(t, sets);
        }

        Log.v("my", trainingSets.toString());
    }
}