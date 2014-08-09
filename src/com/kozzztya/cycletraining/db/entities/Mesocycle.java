package com.kozzztya.cycletraining.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Mesocycle extends Entity {

    private float rm;
    private boolean active;
    private String description;
    private int trainingsInWeek;

    public Mesocycle() {
    }

    public Mesocycle(long id, float rm, boolean active, int trainingsInWeek, String description) {
        this.id = id;
        this.rm = rm;
        this.active = active;
        this.trainingsInWeek = trainingsInWeek;
        this.description = description;
    }

    public float getRm() {
        return rm;
    }

    public void setRm(float rm) {
        this.rm = rm;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTrainingsInWeek() {
        return trainingsInWeek;
    }

    public void setTrainingsInWeek(int trainingsInWeek) {
        this.trainingsInWeek = trainingsInWeek;
    }

    public Mesocycle(Parcel parcel) {
        readFromParcel(parcel);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeFloat(rm);
        dest.writeInt(active ? 1 : 0);
        dest.writeString(description);
        dest.writeInt(trainingsInWeek);
    }

    @Override
    protected void readFromParcel(Parcel parcel) {
        id = parcel.readLong();
        rm = parcel.readFloat();
        active = parcel.readInt() != 0;
        description = parcel.readString();
        trainingsInWeek = parcel.readInt();
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
