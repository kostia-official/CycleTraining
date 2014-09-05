package com.kozzztya.cycletraining.db.entities;

import android.os.Parcel;

public class SetView extends Set {

    private long mMesocycle;

    public SetView() {
    }

    public SetView(long mesocycle, long id, String reps, float weight, String comment, long training) {
        super(id, reps, weight, comment, training);
        mMesocycle = mesocycle;
    }

    public long getMesocycle() {
        return mMesocycle;
    }

    public void setMesocycle(long mesocycle) {
        mMesocycle = mesocycle;
    }

    @Override
    public String toString() {
        return getWeight() + " " + getReps();
    }

    public SetView(Parcel parcel) {
        readFromParcel(parcel);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getId());
        dest.writeString(getReps());
        dest.writeFloat(getWeight());
        dest.writeLong(getTraining());
        dest.writeString(getComment());
        dest.writeLong(mMesocycle);
    }

    @Override
    protected void readFromParcel(Parcel parcel) {
        setId(parcel.readLong());
        setReps(parcel.readString());
        setWeight(parcel.readFloat());
        setTraining(parcel.readLong());
        setComment(parcel.readString());
        mMesocycle = parcel.readLong();
    }
}
