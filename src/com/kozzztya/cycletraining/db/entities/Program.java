package com.kozzztya.cycletraining.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Program extends Entity {

    private String name;
    private long purpose;
    private int weeks;
    private long mesocycle;

    public Program() {
    }

    public Program(long id, String name, long purpose, int weeks, long mesocycle) {
        this.id = id;
        this.name = name;
        this.purpose = purpose;
        this.weeks = weeks;
        this.mesocycle = mesocycle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPurpose() {
        return purpose;
    }

    public void setPurpose(long purpose) {
        this.purpose = purpose;
    }

    public int getWeeks() {
        return weeks;
    }

    public void setWeeks(int weeks) {
        this.weeks = weeks;
    }

    public long getMesocycle() {
        return mesocycle;
    }

    public void setMesocycle(long mesocycle) {
        this.mesocycle = mesocycle;
    }

    @Override
    public String toString() {
        return name + " (" + weeks + ")";
    }

    public Program(Parcel parcel) {
        readFromParcel(parcel);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeLong(purpose);
        dest.writeInt(weeks);
        dest.writeLong(mesocycle);
    }

    @Override
    protected void readFromParcel(Parcel parcel) {
        id = parcel.readLong();
        name = parcel.readString();
        purpose = parcel.readLong();
        weeks = parcel.readInt();
        mesocycle = parcel.readLong();
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