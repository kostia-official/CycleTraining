package com.kozzztya.cycletraining.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Mesocycle extends Entity {

    private float mRM;
    private boolean mActive;
    private String mDescription;
    private int mTrainingsInWeek;

    public Mesocycle() {
    }

    public Mesocycle(long id, float rm, boolean active, int trainingsInWeek, String description) {
        mId = id;
        mRM = rm;
        mActive = active;
        mTrainingsInWeek = trainingsInWeek;
        mDescription = description;
    }

    public float getRm() {
        return mRM;
    }

    public void setRm(float rm) {
        mRM = rm;
    }

    public boolean isActive() {
        return mActive;
    }

    public void setActive(boolean active) {
        mActive = active;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public int getTrainingsInWeek() {
        return mTrainingsInWeek;
    }

    public void setTrainingsInWeek(int trainingsInWeek) {
        mTrainingsInWeek = trainingsInWeek;
    }

    public Mesocycle(Parcel parcel) {
        readFromParcel(parcel);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeFloat(mRM);
        dest.writeInt(mActive ? 1 : 0);
        dest.writeString(mDescription);
        dest.writeInt(mTrainingsInWeek);
    }

    @Override
    protected void readFromParcel(Parcel parcel) {
        mId = parcel.readLong();
        mRM = parcel.readFloat();
        mActive = parcel.readInt() != 0;
        mDescription = parcel.readString();
        mTrainingsInWeek = parcel.readInt();
    }

    public static final Parcelable.Creator<Mesocycle> CREATOR = new Parcelable.Creator<Mesocycle>() {

        public Mesocycle createFromParcel(Parcel in) {
            return new Mesocycle(in);
        }

        public Mesocycle[] newArray(int size) {
            return new Mesocycle[size];
        }
    };

}
