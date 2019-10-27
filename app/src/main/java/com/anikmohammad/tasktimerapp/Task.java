package com.anikmohammad.tasktimerapp;

import java.io.Serializable;

class Task implements Serializable {
    public static final long serialVersionUID = 20190917L;

    private long mId;
    private final String mName;
    private final String mDescription;
    private final int mSortOrder;

    Task(long id, String name, String description, int sortOrder) {
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

    String getDescription() {
        return mDescription;
    }

    int getSortOrder() {
        return mSortOrder;
    }

    public void setId(long id) {
        this.mId = id;
    }

    @SuppressWarnings("NullableProblems")
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
