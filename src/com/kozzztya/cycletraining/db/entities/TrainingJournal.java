package com.kozzztya.cycletraining.db.entities;

import java.sql.Date;

public class TrainingJournal implements Entity {
    private long id;
    private long program;
    private long mesocycle;
    private long exercise;
    private Date beginDate;

    public TrainingJournal() {
    }

    public TrainingJournal(long id, long program, long mesocycle, long exercise, Date beginDate) {
        this.id = id;
        this.program = program;
        this.mesocycle = mesocycle;
        this.exercise = exercise;
        this.beginDate = beginDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProgram() {
        return program;
    }

    public void setProgram(long program) {
        this.program = program;
    }

    public long getMesocycle() {
        return mesocycle;
    }

    public long getExercise() {
        return exercise;
    }

    public void setExercise(long exercise) {
        this.exercise = exercise;
    }

    public void setMesocycle(long mesocycle) {
        this.mesocycle = mesocycle;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }
}
