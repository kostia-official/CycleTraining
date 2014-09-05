package com.kozzztya.cycletraining.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Purpose extends Entity {

    private String mName;

    public Purpose() {
    }

    public Purpose(long id, String name) {
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

    public Purpose(Parcel parcel) {
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

    public static final Parcelable.Creator<Purpose> CREATOR = new Parcelable.Creator<Purpose>() {

        public Purpose createFromParcel(Parcel in) {
            return new Purpose(in);
        }

        public Purpose[] newArray(int size) {
            return new Purpose[size];
        }
    };
}
