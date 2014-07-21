package com.kozzztya.cycletraining.db.entities;

public class SetView extends Set {
    private long mesocycle;

    public SetView() {
    }

    public SetView(long mesocycle, long id, String reps, float weight, String comment, long training) {
        super(id, reps, weight, comment, training);
        this.mesocycle = mesocycle;
    }

    public long getMesocycle() {
        return mesocycle;
    }

    public void setMesocycle(long mesocycle) {
        this.mesocycle = mesocycle;
    }

    @Override
    public String toString() {
        return getWeight() + " " + getReps();
    }
}
