package com.kozzztya.cycletraining.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Date;

public class TrainingJournalView extends TrainingJournal {

    private String programName;
    private String exerciseName;

    public TrainingJournalView() {
    }

    public TrainingJournalView(long id, long program, long mesocycle, long exercise, Date beginDate, String programName, String exerciseName) {
        super(id, program, mesocycle, exercise, beginDate);
        this.programName = programName;
        this.exerciseName = exerciseName;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public TrainingJournalView(Parcel parcel) {
        readFromParcel(parcel);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getId());
        dest.writeLong(getProgram());
        dest.writeLong(getMesocycle());
        dest.writeLong(getExercise());
        dest.writeSerializable(getBeginDate());
        dest.writeString(programName);
        dest.writeString(exerciseName);
    }

    @Override
    protected void readFromParcel(Parcel parcel) {
        setId(parcel.readLong());
        setMesocycle(parcel.readLong());
        setBeginDate((Date) parcel.readSerializable());
        programName = parcel.readString();
        exerciseName = parcel.readString();
    }

    public static final Parcelable.Creator<TrainingJournalView> CREATOR = new Parcelable.Creator<TrainingJournalView>() {

        public TrainingJournalView createFromParcel(Parcel in) {
            return new TrainingJournalView(in);
        }

        public TrainingJournalView[] newArray(int size) {
            return new TrainingJournalView[size];
        }
    };

    @Override
    public String toString() {
        return exerciseName;
    }
}
