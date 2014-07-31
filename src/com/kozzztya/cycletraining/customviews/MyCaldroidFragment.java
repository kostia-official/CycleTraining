package com.kozzztya.cycletraining.customviews;

import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.datasources.TrainingsDS;
import com.kozzztya.cycletraining.db.entities.Training;
import com.kozzztya.cycletraining.db.entities.TrainingView;
import com.kozzztya.cycletraining.utils.DateUtils;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;
import hirondelle.date4j.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MyCaldroidFragment extends CaldroidFragment {

    private final int STATUS_IN_PLANS_COLOR = R.color.light_gray;
    private final int STATUS_DONE_COLOR = R.color.green;
    private final int STATUS_MISSED_COLOR = R.color.red;

    private HashMap<Date, Integer> backgroundForDateMap;

    public void onChangeMonth(int month, int year) {
        initTrainingData();
    }

    private void initTrainingData() {
        CaldroidGridAdapter datesAdapter = getNewDatesGridAdapter(month, year);
        ArrayList<DateTime> datetimeList = datesAdapter.getDatetimeList();

        DBHelper dbHelper = DBHelper.getInstance(getActivity());
        TrainingsDS trainingsDS = new TrainingsDS(dbHelper);

        String where = TrainingsDS.COLUMN_DATE + " >= " + datetimeList.get(0).format("'YYYY-MM-DD'") + " AND " +
                TrainingsDS.COLUMN_DATE + " <= " + datetimeList.get(datetimeList.size() - 1).format("'YYYY-MM-DD'");

        List<TrainingView> trainings = trainingsDS.selectView(where, TrainingsDS.COLUMN_DATE, null, TrainingsDS.COLUMN_DATE);
        backgroundForDateMap = new HashMap<>();

        for (Training t : trainings) {
            switch (DateUtils.getTrainingStatus(t.getDate(), t.isDone())) {
                case DateUtils.STATUS_DONE:
                    backgroundForDateMap.put(t.getDate(), STATUS_DONE_COLOR);
                    break;
                case DateUtils.STATUS_IN_PLANS:
                    backgroundForDateMap.put(t.getDate(), STATUS_IN_PLANS_COLOR);
                    break;
                case DateUtils.STATUS_MISSED:
                    backgroundForDateMap.put(t.getDate(), STATUS_MISSED_COLOR);
                    break;
            }
        }

        setBackgroundResourceForDates(backgroundForDateMap);
    }

    public int getDayStatus(Date date) {
        if (backgroundForDateMap != null) {
            switch (backgroundForDateMap.get(date)) {
                case STATUS_DONE_COLOR:
                    return DateUtils.STATUS_DONE;
                case STATUS_IN_PLANS_COLOR:
                    return DateUtils.STATUS_IN_PLANS;
                case STATUS_MISSED_COLOR:
                    return DateUtils.STATUS_MISSED;
            }
        }
        return DateUtils.STATUS_NONE;
    }

    /**
     * Override to use onChangeMonth from this class,
     * and not from listener
     */
    @Override
    public void setCalendarDateTime(DateTime dateTime) {
        month = dateTime.getMonth();
        year = dateTime.getYear();

        onChangeMonth(month, year);

        refreshView();
    }
}
