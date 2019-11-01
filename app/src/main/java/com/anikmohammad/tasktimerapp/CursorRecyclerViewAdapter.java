package com.anikmohammad.tasktimerapp;

import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class CursorRecyclerViewAdapter extends
        RecyclerView.Adapter<CursorRecyclerViewAdapter.TaskViewHolder> {
    private static final String TAG = "CursorRecyclerViewAdapt";
    private Cursor mCursor;
    private OnTaskClickListener mListener;

    interface OnTaskClickListener {
        void onEditButtonClick(@NonNull Task task);
        void onDeleteButtonClick(@NonNull Task task);
        void onTaskLongClick(@NonNull Task task);
    }

    CursorRecyclerViewAdapter(Cursor cursor, OnTaskClickListener listener) {
        this.mCursor = cursor;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public CursorRecyclerViewAdapter.TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: starts");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CursorRecyclerViewAdapter.TaskViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called with position: " + position);
        if (mCursor != null && mCursor.getCount() > 0) {
            if (!mCursor.moveToPosition(position)) {
                throw new IllegalArgumentException(TAG + " onBindViewHolder(): cannot move cursor to position: " + position);
            }

            final Task task = new Task(
                    mCursor.getLong(mCursor.getColumnIndex(TasksContract.Columns._ID)),
                    mCursor.getString(mCursor.getColumnIndex(TasksContract.Columns.TASK_NAME)),
                    mCursor.getString(mCursor.getColumnIndex(TasksContract.Columns.TASK_DESCRIPTION)),
                    mCursor.getInt(mCursor.getColumnIndex(TasksContract.Columns.TASK_SORTORDER))
            );

            View.OnClickListener buttonListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: starts");

                    switch (v.getId()) {
                        case R.id.tli_edit_button:
                            if (mListener != null) {
                                mListener.onEditButtonClick(task);
                            }
                            break;
                        case R.id.tli_delete_button:
                            if (mListener != null) {
                                mListener.onDeleteButtonClick(task);
                            }
                            break;
                        default:
                            Log.d(TAG, "onClick: unknown view id: " + v.getId());
                    }
                }
            };

            View.OnLongClickListener buttonLongListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(mListener != null) {
                        mListener.onTaskLongClick(task);
                        return true;
                    }
                    return false;
                }
            };

            holder.name.setText(task.getName());
            holder.description.setText(task.getDescription());
            holder.editButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.editButton.setOnClickListener(buttonListener);
            holder.deleteButton.setOnClickListener(buttonListener);
            holder.itemView.setOnLongClickListener(buttonLongListener);
        }
    }

    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    /**
     * Swap in a new {@link Cursor}, returning a new {@link Cursor}
     *
     * @param newCursor the cursor to be used
     * @return returns the previously set cursor, if the swap is successful
     */
    @SuppressWarnings("UnusedReturnValue")
    Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }

        Cursor oldCursor = mCursor;
        mCursor = newCursor;

        if (newCursor != null) {
            // inform the observers of the new cursor
            notifyDataSetChanged();
        } else {
            // inform the observers about the lack of dataset
            notifyItemRangeRemoved(0, getItemCount());
        }

        return oldCursor;
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView description;
        private ImageButton editButton;
        private ImageButton deleteButton;
        private View itemView;

        TaskViewHolder(View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.tli_name);
            this.description = itemView.findViewById(R.id.tli_description);
            this.editButton = itemView.findViewById(R.id.tli_edit_button);
            this.deleteButton = itemView.findViewById(R.id.tli_delete_button);
            this.itemView = itemView;
        }
    }
}
