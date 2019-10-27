package com.anikmohammad.tasktimerapp;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "MainActivityFragment";

    private static final int LOADER_ID = 0;
    private CursorRecyclerViewAdapter mAdapter;

    public MainActivityFragment() {
        Log.d(TAG, "MainActivityFragment: starts");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: starts");
        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.task_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new CursorRecyclerViewAdapter(null, (CursorRecyclerViewAdapter.OnTaskClickListener) getActivity());
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
