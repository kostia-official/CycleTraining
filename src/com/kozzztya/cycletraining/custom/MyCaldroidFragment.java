package com.kozzztya.cycletraining.custom;

import android.os.Bundle;
import android.view.View;

import com.kozzztya.cycletraining.R;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.datasources.TrainingsDS;
import com.kozzztya.cycletraining.db.entities.Training;
import com.kozzztya.cycletraining.db.entities.TrainingView;
import com.kozzztya.cycletraining.utils.DateUtils;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import hirondelle.date4j.DateTime;

//TODO Use another calendar lib

public class MyCaldroidFragment extends CaldroidFragment {

    private final int STATUS_IN_PLANS_COLOR = R.color.light_gray;
    private final int STATUS_DONE_COLOR = R.color.green;
    private final int STATUS_MISSED_COLOR = R.color.red;

    private HashMap<Date, Integer> mBackgroundForDateMap;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindData();
    }

    private void bindData() {
        CaldroidGridAdapter datesAdapter = getNewDatesGridAdapter(month, year);
        ArrayList<DateTime> datetimeList = datesAdapter.getDatetimeList();

        DBHelper dbHelper = DBHelper.getInstance(getActivity());
        TrainingsDS trainingsDS = new TrainingsDS(dbHelper);

        String where = TrainingsDS.COLUMN_DATE + " >= " + datetimeList.get(0).format("'YYYY-MM-DD'") + " AND " +
                TrainingsDS.COLUMN_DATE + " <= " + datetimeList.get(datetimeList.size() - 1).format("'YYYY-MM-DD'");

        List<TrainingView> trainings = trainingsDS.selectView(where, TrainingsDS.COLUMN_DATE, null, TrainingsDS.COLUMN_DATE);
        mBackgroundForDateMap = new HashMap<>();

        for (Training t : trainings) {
            switch (DateUtils.getTrainingStatus(t.getDate(), t.isDone())) {
                case DateUtils.STATUS_DONE:
                    mBackgroundForDateMap.put(t.getDate(), STATUS_DONE_COLOR);
                    break;
                case DateUtils.STATUS_IN_PLANS:
                    mBackgroundForDateMap.put(t.getDate(), STATUS_IN_PLANS_COLOR);
                    break;
                case DateUtils.STATUS_MISSED:
                    mBackgroundForDateMap.put(t.getDate(), STATUS_MISSED_COLOR);
                    break;
            }
        }

        setBackgroundResourceForDates(mBackgroundForDateMap);
    }

    public int getDayStatus(Date date) {
        if (mBackgroundForDateMap != null) {
            switch (mBackgroundForDateMap.get(date)) {
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
     * On calendar date change bind new data
     */
    @Override
    public void setCalendarDateTime(DateTime dateTime) {
        super.setCalendarDateTime(dateTime);
        bindData();
    }
}
