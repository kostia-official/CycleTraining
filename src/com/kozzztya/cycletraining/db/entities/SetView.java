package com.kozzztya.cycletraining.db.entities;

public class SetView {
    private long mesocycle;
    private long cycle;
    private long training;
    private long id;
    private int reps;
    private float weight;
    private String comment;

    public SetView() {
    }

    public SetView(long mesocycle, long cycle, long training, long id, int reps, float weight, String comment) {
        this.mesocycle = mesocycle;
        this.cycle = cycle;
        this.training = training;
        this.id = id;
        this.reps = reps;
        this.weight = weight;
        this.comment = comment;
    }

    public long getMesocycle() {
        return mesocycle;
    }

    public void setMesocycle(long mesocycle) {
        this.mesocycle = mesocycle;
    }

    public long getCycle() {
        return cycle;
    }

    public void setCycle(long cycle) {
        this.cycle = cycle;
    }

    public long getTraining() {
        return training;
    }

    public void setTraining(long training) {
        this.training = training;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
