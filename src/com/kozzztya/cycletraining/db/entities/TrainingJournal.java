package com.kozzztya.cycletraining.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Date;

public class TrainingJournal extends Entity {
    private long id;
    private long program;
    private long mesocycle;
    private long exercise;
    private Date beginDate;

    public TrainingJournal() {
    }

    public TrainingJournal(long id, long program, long mesocycle, long exercise, Date beginDate) {
        this.id = id;
        this.program = program;
        this.mesocycle = mesocycle;
        this.exercise = exercise;
        this.beginDate = beginDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProgram() {
        return program;
    }

    public void setProgram(long program) {
        this.program = program;
    }

    public long getMesocycle() {
        return mesocycle;
    }

    public long getExercise() {
        return exercise;
    }

    public void setExercise(long exercise) {
        this.exercise = exercise;
    }

    public void setMesocycle(long mesocycle) {
        this.mesocycle = mesocycle;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public TrainingJournal(Parcel parcel) {
        readFromParcel(parcel);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(program);
        dest.writeLong(mesocycle);
        dest.writeLong(exercise);
        dest.writeSerializable(beginDate);
    }

    @Override
    protected void readFromParcel(Parcel parcel) {
        id = parcel.readLong();
        program = parcel.readLong();
        mesocycle = parcel.readLong();
        exercise = parcel.readLong();
        beginDate = (Date) parcel.readSerializable();
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
