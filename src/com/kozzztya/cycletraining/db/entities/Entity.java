package com.kozzztya.cycletraining.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class Entity implements Parcelable {

    protected long mId;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected abstract void readFromParcel(Parcel parcel);
}
