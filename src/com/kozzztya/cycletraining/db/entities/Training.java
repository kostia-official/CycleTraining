package com.kozzztya.cycletraining.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Date;

public class Training extends Entity {
    private long id;
    private Date date;
    private long mesocycle;
    private String comment;
    private boolean done;
    private int priority;

    public Training() {
    }

    public Training(long id, Date date, long mesocycle, String comment, boolean done, int priority) {
        this.id = id;
        this.date = date;
        this.mesocycle = mesocycle;
        this.comment = comment;
        this.done = done;
        this.priority = priority;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getMesocycle() {
        return mesocycle;
    }

    public void setMesocycle(long mesocycle) {
        this.mesocycle = mesocycle;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return date.toString();
    }

    public Training(Parcel parcel) {
        readFromParcel(parcel);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeSerializable(date);
        dest.writeLong(mesocycle);
        dest.writeString(comment);
        dest.writeInt(done ? 1 : 0);
        dest.writeInt(priority);
    }

    @Override
    protected void readFromParcel(Parcel parcel) {
        id = parcel.readLong();
        date = (Date) parcel.readSerializable();
        mesocycle = parcel.readLong();
        comment = parcel.readString();
        done = parcel.readInt() != 0;
        priority = parcel.readInt();
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
