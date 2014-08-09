package com.kozzztya.cycletraining.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Date;

public class TrainingJournalView extends Entity {
    private long id;
    private long mesocycle;
    private String program;
    private String exercise;
    private Date beginDate;

    public TrainingJournalView() {
    }

    public TrainingJournalView(long id, long mesocycle, String program, String exercise, Date beginDate) {
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

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public long getMesocycle() {
        return mesocycle;
    }

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
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

    public TrainingJournalView(Parcel parcel) {
        readFromParcel(parcel);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(program);
        dest.writeLong(mesocycle);
        dest.writeString(exercise);
        dest.writeSerializable(beginDate);
    }

    @Override
    protected void readFromParcel(Parcel parcel) {
        id = parcel.readLong();
        program = parcel.readString();
        mesocycle = parcel.readLong();
        exercise = parcel.readString();
        beginDate = (Date) parcel.readSerializable();
    }

    public static final Parcelable.Creator<TrainingJournalView> CREATOR = new Parcelable.Creator<TrainingJournalView>() {

        public TrainingJournalView createFromParcel(Parcel in) {
            return new TrainingJournalView(in);
        }

        public TrainingJournalView[] newArray(int size) {
            return new TrainingJournalView[size];
        }
    };
}
