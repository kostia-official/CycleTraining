package com.kozzztya.cycletraining.db.entities;

public class Program implements Entity {
    private long id;
    private String name;
    private long purpose;
    private int weeks;
    private int trainingsInWeek;
    private long mesocycle;

    public Program() {
    }

    public Program(long id, String name, long purpose, int weeks, int trainingsInWeek, long mesocycle) {
        this.id = id;
        this.name = name;
        this.purpose = purpose;
        this.weeks = weeks;
        this.trainingsInWeek = trainingsInWeek;
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

    public int getTrainingsInWeek() {
        return trainingsInWeek;
    }

    public void setTrainingsInWeek(int trainingsInWeek) {
        this.trainingsInWeek = trainingsInWeek;
    }

    public long getMesocycle() {
        return mesocycle;
    }

    public void setMesocycle(long mesocycle) {
        this.mesocycle = mesocycle;
    }

    @Override
    public String toString() {
        if (trainingsInWeek == 1) {
            return name + " (" + weeks + ")";
        }
        return name + " (" + trainingsInWeek + "x" + weeks + ")";
    }
}