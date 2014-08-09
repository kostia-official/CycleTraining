package com.kozzztya.cycletraining.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class ExerciseType extends Entity {

    private String name;
    private String description;

    public ExerciseType() {
    }

    public ExerciseType(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return name;
    }

    public ExerciseType(Parcel parcel) {
        readFromParcel(parcel);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(description);
    }

    @Override
    protected void readFromParcel(Parcel parcel) {
        id = parcel.readLong();
        name = parcel.readString();
        description = parcel.readString();
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
