package com.kozzztya.cycletraining.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Muscle extends Entity {

    private String name;

    public Muscle() {
    }

    public Muscle(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public Muscle(Parcel parcel) {
        readFromParcel(parcel);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
    }

    @Override
    protected void readFromParcel(Parcel parcel) {
        id = parcel.readLong();
        name = parcel.readString();
    }

    public static final Parcelable.Creator<Muscle> CREATOR = new Parcelable.Creator<Muscle>() {

        public Muscle createFromParcel(Parcel in) {
            return new Muscle(in);
        }

        public Muscle[] newArray(int size) {
            return new Muscle[size];
        }
    };
}
