package com.kozzztya.cycletraining.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Purpose extends Entity {
    private long id;
    private String name;

    public Purpose() {
    }

    public Purpose(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Purpose(Parcel parcel) {
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

    public static final Parcelable.Creator<Purpose> CREATOR = new Parcelable.Creator<Purpose>() {

        public Purpose createFromParcel(Parcel in) {
            return new Purpose(in);
        }

        public Purpose[] newArray(int size) {
            return new Purpose[size];
        }
    };
}
