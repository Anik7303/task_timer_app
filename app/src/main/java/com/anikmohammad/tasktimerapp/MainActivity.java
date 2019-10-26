package com.anikmohammad.tasktimerapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity implements CursorRecyclerViewAdapter.OnTaskClickListener, AddEditActivityFragment.OnSaveClicked, AppDialog.DialogEvents {
    private static final String TAG = "MainActivity";

    // Whether or not the activity is in two pane mode
    private boolean mTwoPane = false;

    private static final int DIALOG_ID_DELETE_TASK = 1;
    private static final int DIALOG_ID_CANCEL_EDIT = 2;

    private AlertDialog mDialog = null; // module scope because we need to dismiss it in onStop
                                        // e.g. when orientation changes to avoid memory leaks

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_name);
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
                // create new task
                taskEditRequest(null);
                break;
            case R.id.mainmenu_show_durations:
                // show task run durations
                break;
            case R.id.mainmenu_settings:
                // go into the apps settings
                break;
            case R.id.mainmenu_about_app:
                // display the app's about dialog
                showAboutDialog();
                break;
            case R.id.mainmenu_generate_data:
                // this is a functionality only available in debug mode.
                // it generates some dummy test data.
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @SuppressLint("SetTextI18n")
    public void showAboutDialog() {
        @SuppressWarnings("InflateParams")
        View messageView = getLayoutInflater().inflate(R.layout.content_about, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(messageView)
                .setTitle(R.string.app_name)
                .setIcon(R.mipmap.ic_launcher);

        mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(true);

        TextView tv = messageView.findViewById(R.id.about_version);
        tv.setText("v" + BuildConfig.VERSION_NAME);

        mDialog.show();
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
        args.putInt(AppDialog.DIALOG_ID, DIALOG_ID_DELETE_TASK);
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
        switch(dialogId) {
            case DIALOG_ID_DELETE_TASK:
                final long taskId = args.getLong(TasksContract.Columns._ID);
                if(BuildConfig.DEBUG && taskId == 0) throw new AssertionError("Task Id is zero");

                String selection = TasksContract.Columns._ID + " = ?";
                String[] selectionArgs = {String.valueOf(taskId)};
                getContentResolver().delete(
                        TasksContract.CONTENT_URI,
                        selection,
                        selectionArgs
                );
                break;

            case DIALOG_ID_CANCEL_EDIT:
                // no action required
                break;

            default:
                throw new IllegalArgumentException(TAG + "onPositiveDialogResult called with unknown dialogId: " + dialogId);
        }
    }

    @Override
    public void onNegativeDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onNegativeDialogResult: called");
        switch(dialogId) {
            case DIALOG_ID_DELETE_TASK:
                // no action required
                break;

            case DIALOG_ID_CANCEL_EDIT:
//                // close the app without saving the edited data
//                finish();
                // Close the fragment without saving the edited data and stopping the app from closing
                AddEditActivityFragment fragment = (AddEditActivityFragment) getSupportFragmentManager().findFragmentById(R.id.task_detail_container);
                if(fragment != null) {
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
                break;

            default:
                throw new IllegalArgumentException(TAG + "onPositiveDialogResult called with unknown dialogId: " + dialogId);
        }
    }

    @Override
    public void onDialogCancelled(int dialogId) {
        Log.d(TAG, "onDialogCancelled: called");
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: called");
        AddEditActivityFragment fragment = (AddEditActivityFragment) getSupportFragmentManager().findFragmentById(R.id.task_detail_container);
        if(fragment == null || fragment.canClose()) {
            super.onBackPressed();
        } else {
            // show dialogue to get confirmation to quit editing
            AppDialog dialog = new AppDialog();
            Bundle args = new Bundle();
            args.putInt(AppDialog.DIALOG_ID, DIALOG_ID_CANCEL_EDIT);
            args.putString(AppDialog.DIALOG_TITLE, "Quit?");
            args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.cancelEditDiag_message));
            args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.cancelEditDiag_positive_caption);
            args.putInt(AppDialog.DIALOG_NEGATIVE_RID, R.string.cancelEditDiag_negative_caption);
            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), null);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
}
