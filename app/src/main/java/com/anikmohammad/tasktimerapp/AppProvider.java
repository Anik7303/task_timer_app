package com.anikmohammad.tasktimerapp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * {@link AppProvider} this is the only class that has access to {@link AppDatabase}
 */
public class AppProvider extends ContentProvider {
    private static final String TAG = "AppProvider";

    private AppDatabase mOpenHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static final String CONTENT_AUTHORITY = "com.anikmohammad.tasktimerapp.provider";
    public static final Uri CONTENT_AUTHORITY_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final int TASKS = 100;
    private static final int TASKS_ID = 101;

    private static final int TIMINGS = 200;
    private static final int TIMINGS_ID = 201;

    private static final int TASK_TIMINGS = 300;
    private static final int TASK_TIMINGS_ID = 301;

    private static final int TASK_DURATIONS = 400;
    private static final int TASK_DURATIONS_ID = 401;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        // content://com.anikmohammad.tasktimer.provider/Tasks
        matcher.addURI(CONTENT_AUTHORITY, TasksContract.TABLE_NAME, TASKS);
        // content://com.anikmohammad.tasktimerapp.provider/Tasks/1
        matcher.addURI(CONTENT_AUTHORITY, TasksContract.TABLE_NAME + "/#", TASKS_ID);

        /*
        matcher.addURI(CONTENT_AUTHORITY, TimingsContract.TABLE_NAME, TIMINGS);
        matcher.addURI(CONTENT_AUTHORITY, TimingsContract.TABLE_NAME + "/#", TIMINGS_ID);

        matcher.addURI(CONTENT_AUTHORITY, DurationsContract.TABLE_NAME, TASK_DURATIONS);
        matcher.addURI(CONTENT_AUTHORITY, DurationsContract.TABLE_NAME + "/#", TASK_DURATIONS_ID);
        */

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = AppDatabase.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.d(TAG, "query: starts with uri: " + uri);
        final int match = sUriMatcher.match(uri);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch(match) {
            case TASKS:
                queryBuilder.setTables(TasksContract.TABLE_NAME);
                break;

            case TASKS_ID:
                queryBuilder.setTables(TasksContract.TABLE_NAME);
                long taskId = TasksContract.getId(uri);
                queryBuilder.appendWhere(TasksContract.Columns._ID + " = " + taskId);
                break;

//            case TIMINGS:
//                queryBuilder.setTables(TimingsContract.TABLE_NAME);
//                break;
//
//            case TIMINGS_ID:
//                queryBuilder.setTables(TimingsContract.TABLE_NAME);
//                long timingId = TimingsContract.getId(uri);
//                queryBuilder.appendWhere(TimingsContract.Columns._ID + " = " + timingId);
//                break;
//
//            case TASK_DURATIONS:
//                queryBuilder.setTables(DurationsContract.TABLE_NAME);
//                break;
//
//            case TASK_DURATIONS_ID:
//                queryBuilder.setTables(DurationsContract.TABLE_NAME);
//                long DurationId = DurationsContract.getId(uri);
//                queryBuilder.appendWhere(DurationsContract.Columns._ID + " = " + DurationId);
//                break;

            default:
                throw new IllegalArgumentException("query() with unknown uri: " + uri);
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        Log.d(TAG, "getType: starts with uri: " + uri);
        final int match = sUriMatcher.match(uri);

        switch(match) {
            case TASKS:
                return TasksContract.CONTENT_TYPE;

            case TASKS_ID:
                return TasksContract.CONTENT_ITEM_TYPE;

//            case TIMINGS:
//                return TimingsContract.CONTENT_TYPE;
//
//            case TIMINGS_ID:
//                return TimingsContract.CONTENT_ITEM_TYPE;
//
//            case TASK_DURATIONS:
//                return DurationsContract.CONTENT_TYPE;
//
//            case TASK_DURATIONS_ID:
//                return DurationsContract.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalStateException("getType() with unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Log.d(TAG, "insert: starts with uri: " + uri);
        final int match = sUriMatcher.match(uri);

        long recordId;
        SQLiteDatabase db;

        switch(match) {
            case TASKS:
                db = mOpenHelper.getWritableDatabase();
                recordId = db.insert(TasksContract.TABLE_NAME, null, values);
                if(recordId >= 0) {
                    Log.d(TAG, "insert: recordId: " + recordId);
                    getContext().getContentResolver().notifyChange(uri, null);
                    return TasksContract.buildUri(recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
                }

//            case TIMINGS:
//                db = mOpenHelper.getWritableDatabase();
//                recordId = db.insert(TimingsContract.TABLE_NAME, null, values);
//                if(recordId >= 0) {
//                    Log.d(TAG, "insert: recordId: " + recordId);
//                    getContext().getContentResolver().notifyChange(uri, null);
//                    return TimingsContract.buildUri(recordId);
//                } else {
//                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
//                }
//
//            case TASK_DURATIONS:
//                db = mOpenHelper.getWritableDatabase();
//                recordId = db.insert(DurationsContract.TABLE_NAME, null, values);
//                if(recordId >= 0) {
//                    Log.d(TAG, "insert: recordId: " + recordId);
//                    getContext().getContentResolver().notifyChange(uri, null);
//                    return DurationsContract.buildUri(recordId);
//                }
//                else {
//                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
//                }

            default:
                throw new IllegalArgumentException("insert() with unknown uri: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "delete: starts with uri: " + uri);

        final int match = sUriMatcher.match(uri);
        int count;
        String selectionCriteria;
        SQLiteDatabase db;

        switch(match) {
            case TASKS:
                db = mOpenHelper.getWritableDatabase();
                count = db.delete(TasksContract.TABLE_NAME, selection, selectionArgs);
                break;

            case TASKS_ID:
                db = mOpenHelper.getWritableDatabase();
                long taskId = TasksContract.getId(uri);
                selectionCriteria = TasksContract.Columns._ID + " = " + taskId;
                if(selection != null && selection.length() > 0) {
                    selectionCriteria = selectionCriteria + "AND (" + selection + ")";
                }
                count = db.delete(TasksContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;

//            case TIMINGS:
//                db = mOpenHelper.getWritableDatabase();
//                count = db.delete(TimingsContract.TABLE_NAME, selection, selectionArgs);
//                break;
//
//            case TIMINGS_ID:
//                db = mOpenHelper.getWritableDatabase();
//                long timingId = TimingsContract.getId(uri);
//                selectionCriteria = TimingsContract.Columns._ID + " = " + timingId;
//                if(selection != null && selection.length() > 0) {
//                    selectionCriteria = selectionCriteria + "AND (" + selection + ")";
//                }
//                count = db.delete(TimingsContract.TABLE_NAME, selectionCriteria, selectionArgs);
//                break;
//
//            case TASK_DURATIONS:
//                db = mOpenHelper.getWritableDatabase();
//                count = db.delete(DurationsContract.TABLE_NAME, selection, selectionArgs);
//                break;
//
//            case TASK_DURATIONS_ID:
//                db = mOpenHelper.getWritableDatabase();
//                long durationId = DurationsContract.getId(uri);
//                selectionCriteria = DurationsContract.Columns._ID + " = " + durationId;
//                if(selection != null && selection.length() > 0) {
//                    selectionCriteria = selectionCriteria + "AND (" + selection + ")";
//                }
//                count = db.delete(DurationsContract.TABLE_NAME, selectionCriteria, selectionArgs);
//                break;

            default:
                throw new IllegalArgumentException("delete() with unknown uri: " + uri);
        }

        Log.d(TAG, "update: count: " + count);
        if(count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        } else {
            throw new android.database.SQLException("Failed to delete with uri: " + uri.toString());
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "update: starts with uri: " + uri);

        final int match = sUriMatcher.match(uri);
        int count;
        String selectionCriteria;
        SQLiteDatabase db;

        switch(match) {
            case TASKS:
                db = mOpenHelper.getWritableDatabase();
                count = db.update(TasksContract.TABLE_NAME, values, selection, selectionArgs);
                break;

            case TASKS_ID:
                db = mOpenHelper.getWritableDatabase();
                long taskId = TasksContract.getId(uri);
                selectionCriteria = TasksContract.Columns._ID + " = " + taskId;
                if(selection != null && selection.length() > 0) {
                    selectionCriteria = selectionCriteria + " AND (" + selection + ")";
                }
                count = db.update(TasksContract.TABLE_NAME, values, selectionCriteria, selectionArgs);
                break;

//            case TIMINGS:
//                db = mOpenHelper.getWritableDatabase();
//                count = db.update(TimingsContract.TABLE_NAME, values, selection, selectionArgs);
//                break;
//
//            case TIMINGS_ID:
//                db = mOpenHelper.getWritableDatabase();
//                long timingId = TimingsContract.getId(uri);
//                selectionCriteria = TimingsContract.Columns._ID + " = " + timingId;
//                if(selection != null && selection.length() > 0) {
//                    selectionCriteria = selectionCriteria + " AND (" + selection + ")";
//                }
//                count = db.update(TimingsContract.TABLE_NAME, values, selectionCriteria, selectionArgs);
//                break;
//
//            case TASK_DURATIONS:
//                db = mOpenHelper.getWritableDatabase();
//                count = db.update(DurationsContract.TABLE_NAME, values, selection, selectionArgs);
//                break;
//
//            case TASK_DURATIONS_ID:
//                db = mOpenHelper.getWritableDatabase();
//                long durationId = DurationsContract.getId(uri);
//                selectionCriteria = DurationsContract.Columns._ID + " = " + durationId;
//                if(selection != null && selection.length() > 0) {
//                    selectionCriteria = selectionCriteria + " AND (" + selection + ")";
//                }
//                count = db.update(DurationsContract.TABLE_NAME, values, selectionCriteria, selectionArgs);
//                break;

            default:
                throw new IllegalArgumentException("update() with unknown uri: " + uri);
        }

        Log.d(TAG, "update: count: " + count);
        if(count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        } else {
            throw new android.database.SQLException("Failed to update with uri: " + uri.toString());
        }
    }
}
