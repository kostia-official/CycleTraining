package com.kozzztya.cycletraining.db.entities;

import java.sql.Date;

public class TrainingJournalView implements Entity {
    private long id;
    private long mesocycle;
    private String program;
    private String exercise;
    private Date beginDate;

    public TrainingJournalView() {
    }

    public TrainingJournalView(long id, long mesocycle, String program, String exercise, Date beginDate) {
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

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public long getMesocycle() {
        return mesocycle;
    }

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
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
