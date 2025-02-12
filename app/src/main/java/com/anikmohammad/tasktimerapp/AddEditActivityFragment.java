package com.anikmohammad.tasktimerapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddEditActivityFragment extends Fragment {
    private static final String TAG = "AddEditActivityFragment";

    enum FragmentMode {ADD, EDIT}

    private FragmentMode mMode;

    private OnSaveClicked mSaveListener = null;

    interface OnSaveClicked {
        void onSaveClicked();
    }

    public AddEditActivityFragment() {
        Log.d(TAG, "AddEditActivityFragment: starts");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "onAttach: starts");
        super.onAttach(context);

        // Activities containing this fragment must implement it's callbacks
        Activity activity = getActivity();
        if (activity != null && !(activity instanceof OnSaveClicked)) {
            throw new ClassCastException(activity.getClass().getSimpleName() + " must implement AddEditActivityFragment.OnSaveClick interface");
        }
        mSaveListener = (OnSaveClicked) activity;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setActionBar(true);
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: starts");
        super.onDetach();
        mSaveListener = null;
        setActionBar(false);
    }

    private void setActionBar(boolean value) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
//        if(activity != null && activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
        if(activity != null) {
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(value);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");
        View view = inflater.inflate(R.layout.fragment_add_edit, container, false);

        final EditText mNameEditText = view.findViewById(R.id.addedit_name);
        final EditText mDescriptionEditText = view.findViewById(R.id.addedit_description);
        final EditText mSortOrderEditText = view.findViewById(R.id.addedit_sortorder);
        final Button mSaveButton = view.findViewById(R.id.addedit_save);

        Bundle arguments = getArguments();

        final Task task;
        if (arguments != null) {
            task = (Task) arguments.getSerializable(Task.class.getSimpleName());
            if (task != null) {
                mNameEditText.setText(task.getName());
                mDescriptionEditText.setText(task.getDescription());
                mSortOrderEditText.setText(String.valueOf(task.getSortOrder()));
                mMode = FragmentMode.EDIT;
            } else {
                mMode = FragmentMode.ADD;
            }
        } else {
            task = null;
            mMode = FragmentMode.ADD;
        }

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                if(activity != null) {
                    ContentResolver contentResolver = getActivity().getContentResolver();
                    int sortOrder;
                    if (mSortOrderEditText.getText().toString().length() > 0) {
                        sortOrder = Integer.parseInt(mSortOrderEditText.getText().toString());
                    } else {
                        sortOrder = 0;
                    }

                    ContentValues values = new ContentValues();

                    switch (mMode) {
                        case ADD:
                            if (mNameEditText.getText().toString().length() > 0) {
                                values.put(TasksContract.Columns.TASK_NAME, mNameEditText.getText().toString());
                                values.put(TasksContract.Columns.TASK_DESCRIPTION, mDescriptionEditText.getText().toString());
                                values.put(TasksContract.Columns.TASK_SORTORDER, sortOrder);
                                contentResolver.insert(TasksContract.CONTENT_URI, values);
                            }
                            break;

                        case EDIT:
                            if(task == null) break;
                            if (!mNameEditText.getText().toString().equals(task.getName())) {
                                values.put(TasksContract.Columns.TASK_NAME, mNameEditText.getText().toString());
                            }
                            if (!mDescriptionEditText.getText().toString().equals(task.getDescription())) {
                                values.put(TasksContract.Columns.TASK_DESCRIPTION, mDescriptionEditText.getText().toString());
                            }
                            if (sortOrder != task.getSortOrder()) {
                                values.put(TasksContract.Columns.TASK_SORTORDER, sortOrder);
                            }
                            if (values.size() > 0) {
                                String selection = TasksContract.Columns._ID + " = ?";
                                String[] selectionArgs = {String.valueOf(task.getId())};
                                contentResolver.update(TasksContract.CONTENT_URI, values, selection, selectionArgs);
                            }
                            break;

                        default:
                            throw new IllegalArgumentException(TAG + "onClick() with unknow FragmentMode: " + mMode.toString());
                    }
                    if (mSaveListener != null) {
                        mSaveListener.onSaveClicked();
                    }
                }
            }
        });

        Log.d(TAG, "onCreateView: ends");
        return view;
    }

    boolean canClose() {
        return false;// TODO finish this method
    }
}
