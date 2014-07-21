package com.kozzztya.cycletraining.db.entities;

public class Program implements Entity {
    private long id;
    private String name;
    private long purpose;
    private int weeks;
    private long mesocycle;

    public Program() {
    }

    public Program(long id, String name, long purpose, int weeks, long mesocycle) {
        this.id = id;
        this.name = name;
        this.purpose = purpose;
        this.weeks = weeks;
        this.mesocycle = mesocycle;
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

    public long getPurpose() {
        return purpose;
    }

    public void setPurpose(long purpose) {
        this.purpose = purpose;
    }

    public int getWeeks() {
        return weeks;
    }

    public void setWeeks(int weeks) {
        this.weeks = weeks;
    }

    public long getMesocycle() {
        return mesocycle;
    }

    public void setMesocycle(long mesocycle) {
        this.mesocycle = mesocycle;
    }

    @Override
    public String toString() {
        return name + " (" + weeks + ")";
    }
}