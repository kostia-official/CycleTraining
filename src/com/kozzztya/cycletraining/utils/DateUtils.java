package com.kozzztya.cycletraining.utils;

import android.content.Context;

import com.kozzztya.cycletraining.R;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtils {

    /*
     * Field numbers indicating the training status.
     * Depends on the date.
     */
    public static final int STATUS_MISSED = 0;
    public static final int STATUS_DONE = 1;
    public static final int STATUS_IN_PLANS = 2;

    /**
     * Calculate the trainings dates.
     *
     * @param i         training number in mesocycle.
     * @param count     count of trainings in week.
     * @param beginDate begin date of mesocycle.
     */
    public static long calcTrainingDate(int i, int count, Date beginDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(beginDate);
        calendar.add(Calendar.DATE, 7 * (i / count));
        if (i % count != 0) {
            if (calendar.get(Calendar.DAY_OF_WEEK) > 5)
                calendar.add(Calendar.DATE, 3);
            else
                calendar.add(Calendar.DATE, 4);
        }
        return calendar.getTimeInMillis();
    }

    /**
     * Get week day name from resources.
     */
    public static String getDayOfWeekName(Date date, Context context) {
        String[] daysOfWeek = context.getResources().getStringArray(R.array.days_of_week);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeekNum = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return daysOfWeek[dayOfWeekNum];
    }

    /**
     * {@code Date.valueOf} parser without throws.
     *
     * @param dateString the string representation of a date in SQL format - " {@code yyyy-MM-dd}".
     * @return {@code null} - when the date can't be parsed.
     */
    public static Date safeParse(String dateString) {
        try {
            return Date.valueOf(dateString);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Determine the status of training by date.
     *
     * @param date   training date.
     * @param isDone is training done.
     * @return training status constant.
     */
    public static int getTrainingStatus(Date date, boolean isDone) {
        if (isDone) {
            return STATUS_DONE;
        } else {
            Date yesterday = new Date(addDays(-1));
            if (date.before(yesterday))
                return STATUS_MISSED;
        }
        return STATUS_IN_PLANS;
    }

    /**
     * Return date in the format preferable in an SQL query
     *
     * @param date the date to format.
     * @return the formatted date.
     */
    public static String sqlFormat(Object date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return "'" + dateFormat.format(date) + "'";
    }

    /**
     * Add given days to the current date.
     *
     * @param days the amount to add to the date.
     * @return the changed date.
     */
    public static long addDays(int days) {
        return addDays(System.currentTimeMillis(), days);
    }

    /**
     * Add given days to the date.
     *
     * @param date the date to modify.
     * @param days the amount to add to the date.
     * @return the changed date.
     */
    public static long addDays(long date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        calendar.add(Calendar.DATE, days);
        return calendar.getTimeInMillis();
    }
}