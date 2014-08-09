package com.kozzztya.cycletraining.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Set extends Entity {

    private String reps;
    private float weight;
    private String comment;
    private long training;

    public Set() {
    }

    public Set(long id, String reps, float weight, String comment, long training) {
        this.id = id;
        this.reps = reps;
        this.weight = weight;
        this.comment = comment;
        this.training = training;
    }

    public String getReps() {
        return reps;
    }

    public void setReps(String reps) {
        this.reps = reps;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getTraining() {
        return training;
    }

    public void setTraining(long training) {
        this.training = training;
    }

    @Override
    public String toString() {
        return reps + " " + weight;
    }

    public Set(Parcel parcel) {
        readFromParcel(parcel);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(reps);
        dest.writeFloat(weight);
        dest.writeLong(training);
        dest.writeString(comment);
    }

    @Override
    protected void readFromParcel(Parcel parcel) {
        id = parcel.readLong();
        reps = parcel.readString();
        weight = parcel.readFloat();
        training = parcel.readLong();
        comment = parcel.readString();
    }

    public static final Parcelable.Creator<Set> CREATOR = new Parcelable.Creator<Set>() {

        public Set createFromParcel(Parcel in) {
            return new Set(in);
        }

        public Set[] newArray(int size) {
            return new Set[size];
        }
    };

}
