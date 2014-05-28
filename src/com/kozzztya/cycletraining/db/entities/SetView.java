package com.kozzztya.cycletraining.db.entities;

public class SetView extends Set {
    private long mesocycle;
    private long cycle;

    public SetView() {
    }

    public SetView(long mesocycle, long cycle, long id, int reps, float weight, String comment, long training) {
        super(id, reps, weight, comment, training);
        this.mesocycle = mesocycle;
        this.cycle = cycle;
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

}
