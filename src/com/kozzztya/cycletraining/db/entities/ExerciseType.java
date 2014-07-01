package com.kozzztya.cycletraining.db.entities;

import com.kozzztya.cycletraining.db.entities.DBEntity;

public class ExerciseType implements DBEntity {
    private long id;
    private String name;
    private String description;

    public ExerciseType() {
    }

    public ExerciseType(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
