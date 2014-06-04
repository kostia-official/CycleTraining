package com.kozzztya.cycletraining.db.entities;

public class Mesocycle implements DBEntity{
    private long id;
    private float rm;
    private long exercise;
    private boolean active;
    private int trainingsInWeek;

    public Mesocycle() {
    }

    public Mesocycle(long id, float rm, long exercise, boolean active, int trainingsInWeek) {
        this.id = id;
        this.rm = rm;
        this.exercise = exercise;
        this.active = active;
        this.trainingsInWeek = trainingsInWeek;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public float getRm() {
        return rm;
    }

    public void setRm(float rm) {
        this.rm = rm;
    }

    public long getExercise() {
        return exercise;
    }

    public void setExercise(long exercise) {
        this.exercise = exercise;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getTrainingsInWeek() {
        return trainingsInWeek;
    }

    public void setTrainingsInWeek(int trainingsInWeek) {
        this.trainingsInWeek = trainingsInWeek;
    }
}
