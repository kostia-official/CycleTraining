package com.kozzztya.cycletraining.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Date;

public class Training extends Entity {

    private Date mDate;
    private long mMesocycle;
    private String mComment;
    private boolean mDone;
    private int mPriority;

    public Training() {
    }

    public Training(long id, Date date, long mesocycle, String comment, boolean done, int priority) {
        mId = id;
        mDate = date;
        mMesocycle = mesocycle;
        mComment = comment;
        mDone = done;
        mPriority = priority;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public long getMesocycle() {
        return mMesocycle;
    }

    public void setMesocycle(long mesocycle) {
        mMesocycle = mesocycle;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        mComment = comment;
    }

    public boolean isDone() {
        return mDone;
    }

    public void setDone(boolean done) {
        mDone = done;
    }

    public int getPriority() {
        return mPriority;
    }

    public void setPriority(int priority) {
        mPriority = priority;
    }

    @Override
    public String toString() {
        return mDate.toString();
    }

    public Training(Parcel parcel) {
        readFromParcel(parcel);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeSerializable(mDate);
        dest.writeLong(mMesocycle);
        dest.writeString(mComment);
        dest.writeInt(mDone ? 1 : 0);
        dest.writeInt(mPriority);
    }

    @Override
    protected void readFromParcel(Parcel parcel) {
        mId = parcel.readLong();
        mDate = (Date) parcel.readSerializable();
        mMesocycle = parcel.readLong();
        mComment = parcel.readString();
        mDone = parcel.readInt() != 0;
        mPriority = parcel.readInt();
    }

    public static final Parcelable.Creator<Training> CREATOR = new Parcelable.Creator<Training>() {

        public Training createFromParcel(Parcel in) {
            return new Training(in);
        }

        public Training[] newArray(int size) {
            return new Training[size];
        }
    };
}
