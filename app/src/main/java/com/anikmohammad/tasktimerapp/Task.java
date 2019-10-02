package com.anikmohammad.tasktimerapp;

import java.io.Serializable;

class Task implements Serializable {
    public static final long serialVersionUID = 20190917L;

    public long mId;
    public final String mName;
    public final String mDescription;
    public final int mSortOrder;

    public Task(long id, String name, String description, int sortOrder) {
        this.mId = id;
        this.mName = name;
        this.mDescription = description;
        this.mSortOrder = sortOrder;
    }

    public long getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getSortOrder() {
        return mSortOrder;
    }

    public void setId(long id) {
        this.mId = id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "mId=" + mId +
                ", mName='" + mName + '\'' +
                ", mDescription='" + mDescription + '\'' +
                ", mSortOrder=" + mSortOrder +
                '}';
    }
}
