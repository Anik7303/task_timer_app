package com.anikmohammad.tasktimerapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.security.InvalidParameterException;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        CursorRecyclerViewAdapter.OnTaskClickListener {
    private static final String TAG = "MainActivityFragment";

    private static final int LOADER_ID = 0;
    private CursorRecyclerViewAdapter mAdapter = null;

    private Timing mCurrentTiming = null;

    public MainActivityFragment() {
        Log.d(TAG, "MainActivityFragment: starts");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: called");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: starts");
        super.onActivityCreated(savedInstanceState);

        Activity activity = getActivity();
        if(activity != null && !(activity instanceof CursorRecyclerViewAdapter.OnTaskClickListener)) {
            throw new ClassCastException(activity.getClass().getSimpleName() + " must implement CursorRecyclerViewAdapter.OnTaskClickListener interface");
        }

        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);
        setTimingText(mCurrentTiming);
    }

    @Override
    public void onEditButtonClick(@NonNull Task task) {
        Log.d(TAG, "onEditButtonClick: called");
        CursorRecyclerViewAdapter.OnTaskClickListener listener = (CursorRecyclerViewAdapter.OnTaskClickListener) getActivity();
        if(listener != null) {
            listener.onEditButtonClick(task);
        }
    }

    @Override
    public void onDeleteButtonClick(@NonNull Task task) {
        Log.d(TAG, "onDeleteButtonClick: called");
        CursorRecyclerViewAdapter.OnTaskClickListener listener = (CursorRecyclerViewAdapter.OnTaskClickListener) getActivity();
        if(listener != null) {
            listener.onDeleteButtonClick(task);
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onTaskLongClick(@NonNull Task task) {
        Log.d(TAG, "onTaskLongClick: called");
        if(mCurrentTiming != null) {
            if(task.getId() == mCurrentTiming.getTask().getId()) {
                //The current task was tapped a second time, so stop timing
                saveTiming(mCurrentTiming);
                mCurrentTiming = null;
                setTimingText(null);
            } else {
                // a new task is being timed, so stop the old one and start timing the new task
                saveTiming(mCurrentTiming);
                mCurrentTiming = new Timing(task);
                setTimingText(mCurrentTiming);
            }
        } else {
            // no task being timed at the moment, so start timing a new task
            mCurrentTiming = new Timing(task);
            setTimingText(mCurrentTiming);
        }
    }

    private void saveTiming(Timing currentTiming) {
        Log.d(TAG, "saveTiming: called with task: " + currentTiming.getTask().getName());

        // if we have an open timing, then set the duration
        currentTiming.setDuration();

        ContentValues values = new ContentValues();
        values.put(TimingsContract.Columns.TIMING_TASK_ID, currentTiming.getTask().getId());
        values.put(TimingsContract.Columns.TIMING_START_TIME, currentTiming.getStartTime());
        values.put(TimingsContract.Columns.TIMING_DURATION, currentTiming.getDuration());

        // update table in database
        //noinspection ConstantConditions
        getActivity().getContentResolver().insert(TimingsContract.CONTENT_URI, values);
    }

    private void setTimingText(Timing currentTiming) {
        @SuppressWarnings("ConstantConditions")
        TextView textView = getActivity().findViewById(R.id.current_task);
        if(currentTiming != null) {
            textView.setText(getString(R.string.timing_task_name, currentTiming.getTask().getName()));
        } else {
            textView.setText(R.string.no_task_selected);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.task_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if(mAdapter == null) {
            mAdapter = new CursorRecyclerViewAdapter(null, this);
        }

        recyclerView.setAdapter(mAdapter);
        Log.d(TAG, "onCreateView: returing");
        return view;
    }

    @SuppressWarnings("ConstantConditions")
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(TAG, "onCreateLoader: starts with id: " + id);
        //noinspection SwitchStatementWithTooFewBranches
        switch (id) {
            case LOADER_ID:
                String[] projection = {
                        TasksContract.Columns._ID,
                        TasksContract.Columns.TASK_NAME,
                        TasksContract.Columns.TASK_DESCRIPTION,
                        TasksContract.Columns.TASK_SORTORDER
                };
                String sortOrder = TasksContract.Columns.TASK_SORTORDER + ", " + TasksContract.Columns.TASK_NAME + " COLLATE NOCASE";
                return new CursorLoader(
                        getContext(),
                        TasksContract.CONTENT_URI,
                        projection,
                        null,
                        null,
                        sortOrder
                );
            default:
                throw new InvalidParameterException(TAG + " onCreateLoader() with invalid id : " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished: starts");
        mAdapter.swapCursor(data);
        Log.d(TAG, "onLoadFinished: data count: " + mAdapter.getItemCount());
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: starts");
        mAdapter.swapCursor(null);
    }
}
