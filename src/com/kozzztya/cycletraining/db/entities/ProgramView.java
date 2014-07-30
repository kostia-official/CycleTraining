package com.kozzztya.cycletraining.db.entities;

public class ProgramView extends Program {

    private int trainingsInWeek;

    public ProgramView() {
    }

    public ProgramView(long id, String name, long purpose, int weeks, long mesocycle, int trainingsInWeek) {
        super(id, name, purpose, weeks, mesocycle);
        this.trainingsInWeek = trainingsInWeek;
    }

    public int getTrainingsInWeek() {
        return trainingsInWeek;
    }

    public void setTrainingsInWeek(int trainingsInWeek) {
        this.trainingsInWeek = trainingsInWeek;
    }

    @Override
    public String toString() {
        if (getTrainingsInWeek() == 1) {
            return getName() + " (" + getWeeks() + ")";
        }
        return getName() + " (" + getTrainingsInWeek() + "x" + getWeeks() + ")";
    }
}
