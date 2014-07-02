package com.kozzztya.cycletraining.utils;

import android.content.Context;
import com.kozzztya.cycletraining.R;

import java.sql.Date;
import java.util.Calendar;

public class MyDateUtils {

    /**
     * Field numbers indicating the training status
     * Depends on the date
     */
    public static final int STATUS_NOT_DONE = 0;
    public static final int STATUS_DONE = 1;
    public static final int STATUS_IN_PLANS = 2;

    /**
     * Calculate training date
     *
     * @param i         Training number
     * @param n         Trainings in week
     * @param beginDate Trainings begin date
     */
    public static long calcTrainingDate(int i, int n, Date beginDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(beginDate);
        calendar.add(Calendar.DATE, 7 * (i / n));
        if (i % n != 0) {
            if (calendar.get(Calendar.DAY_OF_WEEK) > 5)
                calendar.add(Calendar.DATE, 3);
            else
                calendar.add(Calendar.DATE, 4);
        }
        return calendar.getTimeInMillis();
    }

    /**
     *  Get week day name from resources
     */
    public static String getDayOfWeekName(Date date, Context context) {
        String[] daysOfWeek = context.getResources().getStringArray(R.array.days_of_week);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeekNum = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return daysOfWeek[dayOfWeekNum];
    }

    /**
     * {@code Date.valueOf} parser without throws
     */
    public static Date safeParse(String dateString) {
        try {
            return Date.valueOf(dateString);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Determine the status of training by date
     *
     * @param date   Training date
     * @param isDone Is training done
     * @return Training status constant
     */
    public static int trainingStatus(Date date, boolean isDone) {
        if (isDone) {
            return STATUS_DONE;
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -1);
            if (date.after(calendar.getTime()))
                return STATUS_IN_PLANS;
        }
        return STATUS_NOT_DONE;
    }
}