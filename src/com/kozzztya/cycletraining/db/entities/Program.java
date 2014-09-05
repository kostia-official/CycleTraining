package com.kozzztya.cycletraining.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Program extends Entity {

    private String mName;
    private long mPurpose;
    private int mWeeks;
    private long mMesocycle;

    public Program() {
    }

    public Program(long id, String name, long purpose, int weeks, long mesocycle) {
        mId = id;
        mName = name;
        mPurpose = purpose;
        mWeeks = weeks;
        mMesocycle = mesocycle;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public long getPurpose() {
        return mPurpose;
    }

    public void setPurpose(long purpose) {
        mPurpose = purpose;
    }

    public int getWeeks() {
        return mWeeks;
    }

    public void setWeeks(int weeks) {
        mWeeks = weeks;
    }

    public long getMesocycle() {
        return mMesocycle;
    }

    public void setMesocycle(long mesocycle) {
        mMesocycle = mesocycle;
    }

    @Override
    public String toString() {
        return mName + " (" + mWeeks + ")";
    }

    public Program(Parcel parcel) {
        readFromParcel(parcel);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mName);
        dest.writeLong(mPurpose);
        dest.writeInt(mWeeks);
        dest.writeLong(mMesocycle);
    }

    @Override
    protected void readFromParcel(Parcel parcel) {
        mId = parcel.readLong();
        mName = parcel.readString();
        mPurpose = parcel.readLong();
        mWeeks = parcel.readInt();
        mMesocycle = parcel.readLong();
    }

    public static final Parcelable.Creator<Program> CREATOR = new Parcelable.Creator<Program>() {

        public Program createFromParcel(Parcel in) {
            return new Program(in);
        }

        public Program[] newArray(int size) {
            return new Program[size];
        }
    };

}