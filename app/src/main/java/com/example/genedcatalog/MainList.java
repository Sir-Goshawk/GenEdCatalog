package com.example.genedcatalog;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class MainList extends AppCompatActivity {


    private static final String TAG = "MainList";

    protected void onCreate(final Bundle savedInstanceState) {
        Log.i(TAG, "Creating");
        // The "super" call is required for all activities
        super.onCreate(savedInstanceState);


        TextView mainTitle = findViewById(R.id.MainTitle);
        mainTitle.setText("Welcome to Course Catalog");

        TextView filterName = findViewById(R.id.FilterName);
        filterName.setText("Filter Options: ");

    }
    public MainList() {
//        TextView mainTitle = findView;
    }
}
