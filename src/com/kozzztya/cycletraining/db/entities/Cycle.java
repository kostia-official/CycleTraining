package com.kozzztya.cycletraining.db.entities;

public class Cycle {
    private long id;
    private int interval;
    private long mesocycle;

    public Cycle() {
    }

    public Cycle(long id, int interval, long mesocycle) {
        this.id = id;
        this.interval = interval;
        this.mesocycle = mesocycle;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMesocycle() {
        return mesocycle;
    }

    public void setMesocycle(long mesocycle) {
        this.mesocycle = mesocycle;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}
