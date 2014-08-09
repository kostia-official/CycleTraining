package com.kozzztya.cycletraining.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

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
    public int getWeeks() {
        return super.getWeeks();
    }

    @Override
    public void setWeeks(int weeks) {
        super.setWeeks(weeks);
    }

    @Override
    public String toString() {
        if (getTrainingsInWeek() == 1) {
            return getName() + " (" + getWeeks() + ")";
        }
        return getName() + " (" + getTrainingsInWeek() + "x" + getWeeks() + ")";
    }

    public ProgramView(Parcel parcel) {
        readFromParcel(parcel);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getId());
        dest.writeString(getName());
        dest.writeLong(getPurpose());
        dest.writeInt(getWeeks());
        dest.writeLong(getMesocycle());
        dest.writeLong(trainingsInWeek);
    }

    @Override
    protected void readFromParcel(Parcel parcel) {
        setId(parcel.readLong());
        setName(parcel.readString());
        setPurpose(parcel.readLong());
        setWeeks(parcel.readInt());
        setMesocycle(parcel.readLong());
        trainingsInWeek = parcel.readInt();
    }

    public static final Parcelable.Creator<ProgramView> CREATOR = new Parcelable.Creator<ProgramView>() {

        public ProgramView createFromParcel(Parcel in) {
            return new ProgramView(in);
        }

        public ProgramView[] newArray(int size) {
            return new ProgramView[size];
        }
    };

}
