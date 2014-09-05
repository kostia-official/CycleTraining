package com.kozzztya.cycletraining.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Date;

public class TrainingJournal extends Entity {

    private long mProgram;
    private long mMesocycle;
    private long mExercise;
    private Date mBeginDate;

    public TrainingJournal() {
    }

    public TrainingJournal(long id, long program, long mesocycle, long exercise, Date beginDate) {
        mId = id;
        mProgram = program;
        mMesocycle = mesocycle;
        mExercise = exercise;
        mBeginDate = beginDate;
    }

    public long getProgram() {
        return mProgram;
    }

    public void setProgram(long program) {
        mProgram = program;
    }

    public long getMesocycle() {
        return mMesocycle;
    }

    public long getExercise() {
        return mExercise;
    }

    public void setExercise(long exercise) {
        mExercise = exercise;
    }

    public void setMesocycle(long mesocycle) {
        mMesocycle = mesocycle;
    }

    public Date getBeginDate() {
        return mBeginDate;
    }

    public void setBeginDate(Date beginDate) {
        mBeginDate = beginDate;
    }

    public TrainingJournal(Parcel parcel) {
        readFromParcel(parcel);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeLong(mProgram);
        dest.writeLong(mMesocycle);
        dest.writeLong(mExercise);
        dest.writeSerializable(mBeginDate);
    }

    @Override
    protected void readFromParcel(Parcel parcel) {
        mId = parcel.readLong();
        mProgram = parcel.readLong();
        mMesocycle = parcel.readLong();
        mExercise = parcel.readLong();
        mBeginDate = (Date) parcel.readSerializable();
    }

    public static final Parcelable.Creator<TrainingJournal> CREATOR = new Parcelable.Creator<TrainingJournal>() {

        public TrainingJournal createFromParcel(Parcel in) {
            return new TrainingJournal(in);
        }

        public TrainingJournal[] newArray(int size) {
            return new TrainingJournal[size];
        }
    };
}
