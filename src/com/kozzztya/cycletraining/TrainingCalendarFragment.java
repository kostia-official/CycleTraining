package com.kozzztya.cycletraining;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.kozzztya.cycletraining.db.DBHelper;
import com.kozzztya.cycletraining.db.entities.Training;
import com.kozzztya.cycletraining.db.datasources.TrainingsDataSource;
import com.kozzztya.cycletraining.utils.MyDateUtils;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;
import com.roomorama.caldroid.CaldroidListener;
import hirondelle.date4j.DateTime;

import java.util.*;


class TrainingCalendarFragment extends Fragment {

    private CaldroidFragment caldroidFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.training_calendar_fragment, container, false);
    }

    @Override
    public void onStart() {
        caldroidFragment = new CaldroidFragment();
        caldroidFragment.setCaldroidListener(caldroidListener);
        Bundle args = new Bundle();
        args.putInt(CaldroidFragment.START_DAY_OF_WEEK, Preferences.getFirstDayOfWeek(getActivity()));
        caldroidFragment.setArguments(args);

        FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();

        super.onStart();
    }

    private CaldroidListener caldroidListener = new CaldroidListener() {
        @Override
        public void onSelectDate(Date date, View view) {
            long dayOfTrainings = date.getTime();
            Intent intent = new Intent(getActivity(), TrainingsDayActivity.class);
            intent.putExtra("dayOfTrainings", dayOfTrainings);
            startActivity(intent);
        }

        @Override
        public void onChangeMonth(int month, int year) {
            CaldroidGridAdapter datesAdapter = caldroidFragment.getNewDatesGridAdapter(month, year);
            ArrayList<DateTime> datetimeList = datesAdapter.getDatetimeList();

            TrainingsDataSource trainingsDataSource = DBHelper.getInstance(getActivity()).getTrainingsDataSource();

            String where = TrainingsDataSource.COLUMN_DATE + " >= " + datetimeList.get(0).format("'YYYY-MM-DD'") + " AND " +
                    TrainingsDataSource.COLUMN_DATE + " <= " + datetimeList.get(datetimeList.size() - 1).format("'YYYY-MM-DD'");

            List<Training> trainings = trainingsDataSource.select(where, TrainingsDataSource.COLUMN_DATE, null, TrainingsDataSource.COLUMN_DATE);
            HashMap<Date, Integer> backgroundForDateMap = new HashMap<>();

            for (Training t : trainings) {
                switch (MyDateUtils.trainingStatus(t.getDate(), t.isDone())) {
                    case MyDateUtils.STATUS_DONE:
                        backgroundForDateMap.put(t.getDate(), R.color.green);
                        break;
                    case MyDateUtils.STATUS_IN_PLANS:
                        backgroundForDateMap.put(t.getDate(), R.color.grey);
                        break;
                    case MyDateUtils.STATUS_NOT_DONE:
                        backgroundForDateMap.put(t.getDate(), R.color.red);
                        break;
                }
            }

            caldroidFragment.setBackgroundResourceForDates(backgroundForDateMap);
        }
    };

}