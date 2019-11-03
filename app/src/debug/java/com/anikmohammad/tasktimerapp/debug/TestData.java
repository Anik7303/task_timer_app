package com.anikmohammad.tasktimerapp.debug;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.anikmohammad.tasktimerapp.TasksContract;
import com.anikmohammad.tasktimerapp.TimingsContract;

import java.util.GregorianCalendar;

public class TestData {
    public static void generateTestData(ContentResolver contentResolver) {
        final int SECS_IN_DAY = 86400;
        final int LOWER_BOUND = 100;
        final int UPPER_BOUND = 500;
        final int MAX_DURATION = SECS_IN_DAY / 6;

        // get a list of task ID's from the database
        String[] projection = {TasksContract.Columns._ID};
        Cursor cursor = contentResolver.query(
                TasksContract.CONTENT_URI,
                projection,
                null,
                null,
                null
        );

        if(cursor != null && cursor.moveToFirst()) {
            do {
                long taskId = cursor.getLong(cursor.getColumnIndex(TasksContract.Columns._ID));
                int loopCount = LOWER_BOUND + getRandomInt(UPPER_BOUND);
                for(int i = 0; i < loopCount; i++) {
                    long startTime = getRandomDate();
                    long duration = (long) getRandomInt(MAX_DURATION);
                    TestTiming TestTiming = new TestTiming(taskId, startTime, duration);
                    saveCurrentTiming(contentResolver, TestTiming);
                }
            }while(cursor.moveToNext());
            cursor.close();
        }
    }

    private static int getRandomInt(int max) {
        return (int) Math.round(Math.random() * max);
    }

    private static long getRandomDate() {
        // Set the range of years
        int startYear = 2017;
        int endYear = 2019;

        int sec = getRandomInt(59);
        int min = getRandomInt(59);
        int hour = getRandomInt(23);
        int month = getRandomInt(11);
        int year = startYear + getRandomInt(endYear - startYear);

        GregorianCalendar gc = new GregorianCalendar(year, month, 1);
        int day = 1 + getRandomInt(gc.getActualMaximum(GregorianCalendar.DAY_OF_MONTH) - 1);
        gc = new GregorianCalendar(year, month, day, hour, min, sec);

        return gc.getTimeInMillis();
    }

    private static void saveCurrentTiming(ContentResolver contentResolver, TestTiming currentTiming) {
        // Save the timing data
        ContentValues values = new ContentValues();
        values.put(TimingsContract.Columns.TIMING_TASK_ID, currentTiming.taskId);
        values.put(TimingsContract.Columns.TIMING_START_TIME, currentTiming.startTime);
        values.put(TimingsContract.Columns.TIMING_DURATION, currentTiming.duration);

        // update timing database
        contentResolver.insert(TimingsContract.CONTENT_URI, values);
    }
}
