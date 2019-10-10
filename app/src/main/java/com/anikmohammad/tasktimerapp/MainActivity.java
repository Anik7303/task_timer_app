package com.anikmohammad.tasktimerapp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements CursorRecyclerViewAdapter.OnTaskClickListener {
    private static final String TAG = "MainActivity";

    // Whether or not the activity is in two pane mode
    private boolean mTwoPane = false;

    private static final String ADD_EDIT_FRAGMENT = "AddEditFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(findViewById(R.id.task_detail_container) != null) {
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

        switch(item.getItemId()) {
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
        if(mTwoPane) {
            Log.d(TAG, "taskEditRequest: in two-pane mode (tablet)");


            AddEditActivityFragment fragment = new AddEditActivityFragment();

            Bundle arguments = new Bundle();
            arguments.putSerializable(Task.class.getSimpleName(), task);
            fragment.setArguments(arguments);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.task_detail_container, fragment);
            fragmentTransaction.commit();
        } else {
            Log.d(TAG, "taskEditRequest: in single-pane mode (phone)");
            Intent detailIntent = new Intent(this, AddEditActivity.class);
            if(task != null) {
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
        String selection = TasksContract.Columns._ID + " = ?";
        String[] selectionArgs = {String.valueOf(task.getId())};
        getContentResolver().delete(
                TasksContract.CONTENT_URI,
                selection,
                selectionArgs
        );
    }
}
