package com.anikmohammad.tasktimerapp;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AddEditActivity extends AppCompatActivity implements AddEditActivityFragment.OnSaveClicked {
    private static final String TAG = "AddEditActivity";

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

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, fragment)
                .commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onSaveClicked() {
        finish();
    }
}
