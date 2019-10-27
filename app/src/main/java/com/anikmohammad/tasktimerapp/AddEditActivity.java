package com.anikmohammad.tasktimerapp;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

public class AddEditActivity extends AppCompatActivity implements AddEditActivityFragment.OnSaveClicked, AppDialog.DialogEvents {
    private static final String TAG = "AddEditActivity";
    private static final int DIALOG_ID_CANCEL_EDIT = 1;

    private FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle arguments = getIntent().getExtras();
        AddEditActivityFragment fragment = new AddEditActivityFragment();
        fragment.setArguments(arguments);

        fragmentManager.beginTransaction()
                .replace(R.id.fragment, fragment)
                .commit();

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //noinspection SwitchStatementWithTooFewBranches
        switch(item.getItemId()) {
            case android.R.id.home:
                Log.d(TAG, "onOptionsItemSelected: home button pressed");
                AddEditActivityFragment fragment = (AddEditActivityFragment) fragmentManager.findFragmentById(R.id.fragment);
                if(fragment != null && fragment.canClose()) {
                    return super.onOptionsItemSelected(item);
                } else {
                    showQuitConfirmationDialog();
                    return true; // indicates we are handling this
                }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveClicked() {
        finish();
    }

    @Override
    public void onPositiveDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onPositiveDialogResult: called");
    }

    @Override
    public void onNegativeDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onNegativeDialogResult: called");
        finish();
    }

    @Override
    public void onDialogCancelled(int dialogId) {
        Log.d(TAG, "onDialogCancelled: called");
    }

    @Override
    public void onBackPressed() {
        AddEditActivityFragment fragment = (AddEditActivityFragment) fragmentManager.findFragmentById(R.id.fragment);
        if(fragment != null && fragment.canClose()) {
            super.onBackPressed();
        } else {
            showQuitConfirmationDialog();
        }
    }

    private void showQuitConfirmationDialog() {
        // show dialogue to get confirmation to quit editing
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID, DIALOG_ID_CANCEL_EDIT);
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.cancelEditDiag_message));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.cancelEditDiag_positive_caption);
        args.putInt(AppDialog.DIALOG_NEGATIVE_RID, R.string.cancelEditDiag_negative_caption);

        AppDialog dialog = new AppDialog();
        dialog.setArguments(args);
        dialog.show(fragmentManager, null);
    }
}
