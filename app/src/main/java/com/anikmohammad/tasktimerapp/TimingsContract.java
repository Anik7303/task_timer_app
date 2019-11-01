package com.anikmohammad.tasktimerapp;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.anikmohammad.tasktimerapp.AppProvider.CONTENT_AUTHORITY;
import static com.anikmohammad.tasktimerapp.AppProvider.CONTENT_AUTHORITY_URI;

/**
 * Contract class for the Timings table
 */
public class TimingsContract {
    static final String TABLE_NAME = "Timings";

    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME);

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;

    /**
     * Get the uri for database query, insert, update or delete functionality
     *
     * @param id the timing id
     * @return returns the required uri
     */
    public static Uri buildUri(long id) {
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    /**
     * Extracts the id from the provided uri
     *
     * @param uri the uri that contains the required id
     * @return returns the required id extracted form the provided uri
     */
    public static long getId(Uri uri) {
        return ContentUris.parseId(uri);
    }

    /**
     * Columns class contains all the Columns int the {@value TABLE_NAME} table
     */
    public static class Columns {
        public static final String _ID = BaseColumns._ID;
        public static final String TIMING_TASK_ID = "TaskId";
        public static final String TIMING_START_TIME = "StartTime";
        public static final String TIMING_DURATION = "Duration";

        private Columns() {
            // making constructor private to prevent instantiation
        }
    }
}
