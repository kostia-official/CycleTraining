package com.kozzztya.cycletraining.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Exercise extends Entity {

    private String mName;
    private long mExerciseType;
    private String mDescription;
    private long mMuscle;

    public Exercise() {
    }

    public Exercise(long id, String name, long exerciseType, long muscle, String description) {
        mId = id;
        mName = name;
        mExerciseType = exerciseType;
        mMuscle = muscle;
        mDescription = description;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public long getExerciseType() {
        return mExerciseType;
    }

    public void setExerciseType(long exerciseType) {
        mExerciseType = exerciseType;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public long getMuscle() {
        return mMuscle;
    }

    public void setMuscle(long muscle) {
        mMuscle = muscle;
    }

    @Override
    public String toString() {
        return mName;
    }

    public Exercise(Parcel parcel) {
        readFromParcel(parcel);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mName);
        dest.writeLong(mExerciseType);
        dest.writeString(mDescription);
        dest.writeLong(mMuscle);
    }

    @Override
    protected void readFromParcel(Parcel parcel) {
        mId = parcel.readLong();
        mName = parcel.readString();
        mExerciseType = parcel.readLong();
        mDescription = parcel.readString();
        mMuscle = parcel.readLong();
    }

    public static final Parcelable.Creator<Exercise> CREATOR = new Parcelable.Creator<Exercise>() {

        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };

}
