package com.kozzztya.cycletraining.db.entities;

public class MesocycleView extends Mesocycle {
    private String exercise;
    private int trainingsInWeek;

    public MesocycleView() {
    }

    public MesocycleView(long id, float rm, boolean active, String description, String exercise, int trainingsInWeek) {
        super(id, rm, active, description);
        this.exercise = exercise;
        this.trainingsInWeek = trainingsInWeek;
    }

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    public int getTrainingsInWeek() {
        return trainingsInWeek;
    }

    public void setTrainingsInWeek(int trainingsInWeek) {
        this.trainingsInWeek = trainingsInWeek;
    }
}
