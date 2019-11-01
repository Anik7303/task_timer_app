package com.anikmohammad.tasktimerapp;

import android.util.Log;

import java.io.Serializable;
import java.util.Date;

class Timing implements Serializable {
    private static final String TAG = Timing.class.getSimpleName();
    private static final long serialVersionUID = 20191031L;
    
    private long m_Id;
    private Task mTask;
    private long mStartTime;
    private long mDuration;

    public Timing(Task task) {
        mTask = task;
        Date currentTime = new Date();
        // set mStartTime to now and the mDuration to zero for a new object
        mStartTime = currentTime.getTime() / 1000; // working with seconds
        mDuration = 0;
    }

    long getId() {
        return m_Id;
    }

    void setId(long id) {
        m_Id = id;
    }

    Task getTask() {
        return mTask;
    }

    void setTask(Task task) {
        mTask = task;
    }

    long getStartTime() {
        return mStartTime;
    }

    void setStartTime(long startTime) {
        mStartTime = startTime;
    }

    long getDuration() {
        return mDuration;
    }

    void setDuration() {
        // set mDuration to current time minus mStartTime
        Date currentTime = new Date();
        mDuration = (currentTime.getTime() / 1000) - mDuration; // working with seconds
        Log.d(TAG, mTask.getName() + " -> start time: " + mStartTime + " | duration: " + mDuration);
    }
}
