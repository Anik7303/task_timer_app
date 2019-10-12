package com.anikmohammad.tasktimerapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity implements CursorRecyclerViewAdapter.OnTaskClickListener, AddEditActivityFragment.OnSaveClicked, AppDialog.DialogEvents {
    private static final String TAG = "MainActivity";

    // Whether or not the activity is in two pane mode
    private boolean mTwoPane = false;

    private static final int DIALOG_ID = 1;

    private static final String ADD_EDIT_FRAGMENT = "AddEditFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.task_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts (res/values-land and res/values-sw600dp).
            // If this view is present than the activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu; this adds items to the action bar if needed
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.mainmenu_add_task:
                taskEditRequest(null);
                break;
            case R.id.mainmenu_show_durations:
                break;
            case R.id.mainmenu_settings:
                break;
            case R.id.mainmenu_about_app:
                break;
            case R.id.mainmenu_generate_data:
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void taskEditRequest(Task task) {
        Log.d(TAG, "taskEditRequest: starts");
        if (mTwoPane) {
            Log.d(TAG, "taskEditRequest: in two-pane mode (tablet)");


            AddEditActivityFragment fragment = new AddEditActivityFragment();

            Bundle arguments = new Bundle();
            arguments.putSerializable(Task.class.getSimpleName(), task);
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.task_detail_container, fragment)
                    .commit();
        } else {
            Log.d(TAG, "taskEditRequest: in single-pane mode (phone)");
            Intent detailIntent = new Intent(this, AddEditActivity.class);
            if (task != null) {
                detailIntent.putExtra(Task.class.getSimpleName(), task);
            }
            startActivity(detailIntent);
        }
    }

    @Override
    public void onEditButtonClick(Task task) {
        taskEditRequest(task);
    }

    @Override
    public void onDeleteButtonClick(Task task) {
        AppDialog dialog = new AppDialog();
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID, DIALOG_ID);
        args.putString(AppDialog.DIALOG_TITLE, "");
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.deldiag_message, task.getId(), task.getName()));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.deldiag_positive_caption);
        args.putLong(TasksContract.Columns._ID, task.getId());
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onSaveClicked() {
        Log.d(TAG, "onSaveClicked: starts");
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.task_detail_container);
        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .remove(fragment)
                    .commit();
        }
    }

    @Override
    public void onPositiveDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onPositiveDialogResult: called");
        if(dialogId == DIALOG_ID) {
            final long taskId = args.getLong(TasksContract.Columns._ID);
            if(BuildConfig.DEBUG && taskId == 0) throw new AssertionError("Task Id is zero");

            String selection = TasksContract.Columns._ID + " = ?";
            String[] selectionArgs = {String.valueOf(taskId)};
            getContentResolver().delete(
                    TasksContract.CONTENT_URI,
                    selection,
                    selectionArgs
            );
        }
    }

    @Override
    public void onNegativeDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onNegativeDialogResult: called");
    }

    @Override
    public void onDialogCancelled(int dialogId) {
        Log.d(TAG, "onDialogCancelled: called");
    }
}
