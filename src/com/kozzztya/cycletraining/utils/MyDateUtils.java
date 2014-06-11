package com.kozzztya.cycletraining.utils;

import java.sql.Date;
import java.util.Calendar;

public class MyDateUtils {

    /**
     * Расчёт даты тренировки
     *
     * @param n         Тренировок в неделю
     * @param i         Номер тренировки
     * @param beginDate Дата начала тренировок
     */
    public static long calcTrainingsDate(int n, int i, Date beginDate) {
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
     * Расчёт даты тренировки
     *
     * @param dayOfWeekNum   Номер дня недели, возвращаемый классом Calendar
     * @param firstDayOfWeek Первый день недели (1 - воскресенье, 2 - понедельник)
     * @return Номер дня недели (0-6)
     */
    public static int dayOfWeekNum(int dayOfWeekNum, int firstDayOfWeek) {
        return (dayOfWeekNum - firstDayOfWeek + 7) % 7;
    }
}
