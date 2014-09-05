package com.kozzztya.cycletraining.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Date;

public class TrainingJournalView extends TrainingJournal {

    private String mProgramName;
    private String mExerciseName;

    public TrainingJournalView() {
    }

    public TrainingJournalView(long id, long program, long mesocycle, long exercise, Date beginDate, String programName, String exerciseName) {
        super(id, program, mesocycle, exercise, beginDate);
        this.mProgramName = programName;
        this.mExerciseName = exerciseName;
    }

    public String getProgramName() {
        return mProgramName;
    }

    public void setProgramName(String programName) {
        mProgramName = programName;
    }

    public String getExerciseName() {
        return mExerciseName;
    }

    public void setExerciseName(String exerciseName) {
        mExerciseName = exerciseName;
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
        dest.writeString(mProgramName);
        dest.writeString(mExerciseName);
    }

    @Override
    protected void readFromParcel(Parcel parcel) {
        setId(parcel.readLong());
        setMesocycle(parcel.readLong());
        setBeginDate((Date) parcel.readSerializable());
        mProgramName = parcel.readString();
        mExerciseName = parcel.readString();
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
        return mExerciseName;
    }
}
