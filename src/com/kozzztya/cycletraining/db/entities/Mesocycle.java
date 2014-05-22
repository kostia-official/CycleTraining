package com.kozzztya.cycletraining.db.entities;

public class Mesocycle implements DBEntity{
    private long id;
    private float rm;
    private long exercise;

    public Mesocycle() {
    }

    public Mesocycle(long id, float rm, long exercise) {
        this.id = id;
        this.rm = rm;
        this.exercise = exercise;
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
}
