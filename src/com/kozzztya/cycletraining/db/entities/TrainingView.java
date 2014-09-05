package com.kozzztya.cycletraining.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Date;

public class TrainingView extends Training {

    private String mExercise;

    public TrainingView() {
    }

    public TrainingView(long id, Date date, long mesocycle, String comment, boolean done, int order, String exercise) {
        super(id, date, mesocycle, comment, done, order);
        mExercise = exercise;
    }

    public String getExercise() {
        return mExercise;
    }

    public void setExercise(String exercise) {
        this.mExercise = exercise;
    }

    @Override
    public String toString() {
        return getExercise();
    }

    public TrainingView(Parcel parcel) {
        readFromParcel(parcel);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getId());
        dest.writeSerializable(getDate());
        dest.writeLong(getMesocycle());
        dest.writeString(getComment());
        dest.writeInt(isDone() ? 1 : 0);
        dest.writeInt(getPriority());
        dest.writeString(mExercise);
    }

    @Override
    protected void readFromParcel(Parcel parcel) {
        setId(parcel.readLong());
        setDate((Date) parcel.readSerializable());
        setMesocycle(parcel.readLong());
        setComment(parcel.readString());
        setDone(parcel.readInt() != 0);
        setPriority(parcel.readInt());
        mExercise = parcel.readString();
    }

    public static final Parcelable.Creator<TrainingView> CREATOR = new Parcelable.Creator<TrainingView>() {

        public TrainingView createFromParcel(Parcel in) {
            return new TrainingView(in);
        }

        public TrainingView[] newArray(int size) {
            return new TrainingView[size];
        }
    };

}
