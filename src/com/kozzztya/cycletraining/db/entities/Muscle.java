package com.kozzztya.cycletraining.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Muscle extends Entity {

    private String mName;

    public Muscle() {
    }

    public Muscle(long id, String name) {
        mId = id;
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public String toString() {
        return mName;
    }

    public Muscle(Parcel parcel) {
        readFromParcel(parcel);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mName);
    }

    @Override
    protected void readFromParcel(Parcel parcel) {
        mId = parcel.readLong();
        mName = parcel.readString();
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
