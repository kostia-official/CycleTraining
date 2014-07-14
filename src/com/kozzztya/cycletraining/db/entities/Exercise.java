package com.kozzztya.cycletraining.db.entities;

public class Exercise implements Entity {
    private long id;
    private String name;
    private long exerciseType;
    private String description;
    private long muscle;

    public Exercise() {
    }

    public Exercise(long id, String name, long exerciseType, long muscle, String description) {
        this.id = id;
        this.name = name;
        this.exerciseType = exerciseType;
        this.muscle = muscle;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(long exerciseType) {
        this.exerciseType = exerciseType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return name;
    }

    public long getMuscle() {
        return muscle;
    }

    public void setMuscle(long muscle) {
        this.muscle = muscle;
    }
}
