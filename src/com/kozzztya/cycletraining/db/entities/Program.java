package com.kozzztya.cycletraining.db.entities;

public class Program implements DBEntity{
    private long id;
    private String name;
    private int purpose;
    private int weeks;
    private int mesocycle;

    public Program() {
    }

    public Program(long id, String name, int purpose, int weeks, int mesocycle) {
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

    public int getPurpose() {
        return purpose;
    }

    public void setPurpose(int purpose) {
        this.purpose = purpose;
    }

    public int getWeeks() {
        return weeks;
    }

    public void setWeeks(int weeks) {
        this.weeks = weeks;
    }

    public int getMesocycle() {
        return mesocycle;
    }

    public void setMesocycle(int mesocycle) {
        this.mesocycle = mesocycle;
    }

    @Override
    public String toString() {
        return name + " (" + weeks + ")";
    }
}