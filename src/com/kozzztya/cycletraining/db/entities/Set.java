package com.kozzztya.cycletraining.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Set extends Entity {

    private String mReps;
    private float mWeight;
    private String mComment;
    private long mTraining;

    public Set() {
    }

    public Set(long id, String reps, float weight, String comment, long training) {
        mId = id;
        mReps = reps;
        mWeight = weight;
        mComment = comment;
        mTraining = training;
    }

    public String getReps() {
        return mReps;
    }

    public void setReps(String reps) {
        mReps = reps;
    }

    public float getWeight() {
        return mWeight;
    }

    public void setWeight(float weight) {
        mWeight = weight;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        this.mComment = comment;
    }

    public long getTraining() {
        return mTraining;
    }

    public void setTraining(long training) {
        this.mTraining = training;
    }

    @Override
    public String toString() {
        return mReps + " " + mWeight;
    }

    public Set(Parcel parcel) {
        readFromParcel(parcel);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mReps);
        dest.writeFloat(mWeight);
        dest.writeLong(mTraining);
        dest.writeString(mComment);
    }

    @Override
    protected void readFromParcel(Parcel parcel) {
        mId = parcel.readLong();
        mReps = parcel.readString();
        mWeight = parcel.readFloat();
        mTraining = parcel.readLong();
        mComment = parcel.readString();
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
