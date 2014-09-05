package com.kozzztya.cycletraining.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class ExerciseType extends Entity {

    private String mName;
    private String mDescription;

    public ExerciseType() {
    }

    public ExerciseType(long id, String name, String description) {
        mId = id;
        mName = name;
        mDescription = description;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    @Override
    public String toString() {
        return mName;
    }

    public ExerciseType(Parcel parcel) {
        readFromParcel(parcel);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mName);
        dest.writeString(mDescription);
    }

    @Override
    protected void readFromParcel(Parcel parcel) {
        mId = parcel.readLong();
        mName = parcel.readString();
        mDescription = parcel.readString();
    }

    public static final Parcelable.Creator<ExerciseType> CREATOR = new Parcelable.Creator<ExerciseType>() {

        public ExerciseType createFromParcel(Parcel in) {
            return new ExerciseType(in);
        }

        public ExerciseType[] newArray(int size) {
            return new ExerciseType[size];
        }
    };
}
