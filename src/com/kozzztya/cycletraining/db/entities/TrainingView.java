package com.kozzztya.cycletraining.db.entities;

import java.sql.Date;

public class TrainingView extends Training {
    private long mesocycle;
    private String exercise;

    public TrainingView() {
    }

    public TrainingView(long id, Date date, long cycle, String comment, int priority,
                        boolean done, long mesocycle, String exercise) {
        super(id, date, cycle, comment, priority, done);
        this.mesocycle = mesocycle;
        this.exercise = exercise;
    }

    public long getMesocycle() {
        return mesocycle;
    }

    public void setMesocycle(long mesocycle) {
        this.mesocycle = mesocycle;
    }

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }
}
