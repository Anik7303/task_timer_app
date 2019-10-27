package com.anikmohammad.tasktimerapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class AppDatabase extends SQLiteOpenHelper {
    private static final String TAG = "AppDatabase";

    private static final String DATABASE_NAME = "TaskTimer.db";
    private static final int DATABASE_VERSION = 1;

    private static AppDatabase mInstance = null;

    private AppDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "AppDatabase: constructor called");
        // Implementing AppDatabase as a singleton database
    }

    /**
     * Returns a singleton {@link SQLiteOpenHelper} object
     *
     * @param context Apps Context
     * @return {@link SQLiteOpenHelper} Object
     */
    static AppDatabase getInstance(Context context) {
        Log.d(TAG, "getInstance: getting instance");
        if (mInstance == null) {
            Log.d(TAG, "getInstance: creating new instance");
            mInstance = new AppDatabase(context);
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: starts");

        String sSql = "CREATE TABLE IF NOT EXISTS " + TasksContract.TABLE_NAME + " ("
                + TasksContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + TasksContract.Columns.TASK_NAME + " TEXT NOT NULL, "
                + TasksContract.Columns.TASK_DESCRIPTION + " TEXT, "
                + TasksContract.Columns.TASK_SORTORDER + " INTEGER);";

        Log.d(TAG, "onCreate: sSql: " + sSql);
        db.execSQL(sSql);

        Log.d(TAG, "onCreate: ends");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO complete this function
        //noinspection SwitchStatementWithTooFewBranches
        switch (oldVersion) {
            case 1:
                // logic from database version 1
                break;
            default:
                throw new IllegalStateException("onUpgrade with unknown newVersion: " + newVersion);
        }
    }
}
