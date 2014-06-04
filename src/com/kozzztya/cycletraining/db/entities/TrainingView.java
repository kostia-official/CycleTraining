package com.kozzztya.cycletraining.db.entities;

import java.sql.Date;

public class TrainingView extends Training {
    private String exercise;

    public TrainingView() {
    }

    public TrainingView(long id, Date date, long mesocycle, String comment, int priority,
                        boolean done, String exercise) {
        super(id, date, mesocycle, comment, priority, done);
        this.exercise = exercise;
    }

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }
}
