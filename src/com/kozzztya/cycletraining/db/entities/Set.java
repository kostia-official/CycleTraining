package com.kozzztya.cycletraining.db.entities;

public class Set implements Entity {
    private long id;
    private String reps;
    private float weight;
    private String comment;
    private long training;

    public Set() {
    }

    public Set(long id, String reps, float weight, String comment, long training) {
        this.id = id;
        this.reps = reps;
        this.weight = weight;
        this.comment = comment;
        this.training = training;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getReps() {
        return reps;
    }

    public void setReps(String reps) {
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

    public long getTraining() {
        return training;
    }

    public void setTraining(long training) {
        this.training = training;
    }

    @Override
    public String toString() {
        return reps + " " + weight;
    }
}
