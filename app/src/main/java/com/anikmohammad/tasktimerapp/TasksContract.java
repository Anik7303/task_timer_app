package com.anikmohammad.tasktimerapp;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.anikmohammad.tasktimerapp.AppProvider.CONTENT_AUTHORITY;
import static com.anikmohammad.tasktimerapp.AppProvider.CONTENT_AUTHORITY_URI;

/**
 * Contract class for the Tasks Table
 */
public class TasksContract {
    private static final String TAG = "TasksContract";

    public static final String TABLE_NAME = "Tasks";
    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME);
    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;

    /**
     * Get the uri for database query, insert, update or delete functionality
     *
     * @param id the task id
     * @return returns the required uri
     */
    static Uri buildUri(long id) {
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    /**
     * Extracts the id from the provided uri
     *
     * @param uri the uri that contains the required id
     * @return returns the required id extracted form the provided uri
     */
    static long getId(Uri uri) {
        return ContentUris.parseId(uri);
    }

    /**
     * Columns class contains all the Columns int the {@value TABLE_NAME} table
     */
    public static class Columns {
        public static final String _ID = BaseColumns._ID;
        public static final String TASK_NAME = "Name";
        public static final String TASK_DESCRIPTION = "Description";
        public static final String TASK_SORTORDER = "SortOrder";

        private Columns() {
            // making constructor private to prevent instantiation
        }
    }
}
