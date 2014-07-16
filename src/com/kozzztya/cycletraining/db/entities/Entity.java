package com.kozzztya.cycletraining.db.entities;

import java.io.Serializable;

public interface Entity extends Serializable {
    public long getId();

    public void setId(long id);
}
