package com.kozzztya.cycletraining.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Exercise extends Entity {

    private String name;
    private long exerciseType;
    private String description;
    private long muscle;

    public Exercise() {
    }

    public Exercise(long id, String name, long exerciseType, long muscle, String description) {
        this.id = id;
        this.name = name;
        this.exerciseType = exerciseType;
        this.muscle = muscle;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(long exerciseType) {
        this.exerciseType = exerciseType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getMuscle() {
        return muscle;
    }

    public void setMuscle(long muscle) {
        this.muscle = muscle;
    }

    @Override
    public String toString() {
        return name;
    }

    public Exercise(Parcel parcel) {
        readFromParcel(parcel);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeLong(exerciseType);
        dest.writeString(description);
        dest.writeLong(muscle);
    }

    @Override
    protected void readFromParcel(Parcel parcel) {
        id = parcel.readLong();
        name = parcel.readString();
        exerciseType = parcel.readLong();
        description = parcel.readString();
        muscle = parcel.readLong();
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
