package com.kozzztya.cycletraining.db.entities;

import java.sql.Date;

public class Training implements DBEntity {
    private long id;
    private Date date;
    private long cycle;
    private String comment;
    private int priority;
    private boolean done;

    public Training() {
    }

    public Training(long id, Date date, long cycle, String comment, int priority, boolean done) {
        this.id = id;
        this.date = date;
        this.cycle = cycle;
        this.comment = comment;
        this.priority = priority;
        this.done = done;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getCycle() {
        return cycle;
    }

    public void setCycle(long cycle) {
        this.cycle = cycle;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
