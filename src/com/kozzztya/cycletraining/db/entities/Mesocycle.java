package com.kozzztya.cycletraining.db.entities;

public class Mesocycle implements Entity {
    private long id;
    private float rm;
    private boolean active;
    private String description;

    public Mesocycle() {
    }

    public Mesocycle(long id, float rm, boolean active, String comment) {
        this.id = id;
        this.rm = rm;
        this.active = active;
        this.description = comment;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
