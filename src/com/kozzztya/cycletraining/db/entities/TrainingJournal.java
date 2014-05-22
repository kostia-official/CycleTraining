package com.kozzztya.cycletraining.db.entities;

import java.sql.Date;

public class TrainingJournal implements DBEntity {
    private long id;
    private long program;
    private long mesocycle;
    private Date beginDate;
    private boolean done;

    public TrainingJournal() {
    }

    public TrainingJournal(long id, long program, long mesocycle, Date beginDate, boolean done) {
        this.id = id;
        this.program = program;
        this.mesocycle = mesocycle;
        this.beginDate = beginDate;
        this.done = done;
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

    public void setMesocycle(long mesocycle) {
        this.mesocycle = mesocycle;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
