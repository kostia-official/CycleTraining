package com.kozzztya.cycletraining.db.entities;

public class ExercisesMuscle implements DBEntity {

    private long id;
    private long muscle;
    private long exercise;

    public ExercisesMuscle() {
    }

    public ExercisesMuscle(long id, long muscle, long exercise) {
        this.id = id;
        this.muscle = muscle;
        this.exercise = exercise;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public long getMuscle() {
        return muscle;
    }

    public void setMuscle(long muscle) {
        this.muscle = muscle;
    }

    public long getExercise() {
        return exercise;
    }

    public void setExercise(long exercise) {
        this.exercise = exercise;
    }
}
