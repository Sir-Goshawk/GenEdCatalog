package com.example.genedcatalog;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class MainList extends AppCompatActivity {

    private static final String TAG = "MainList";

    protected void onCreate(final Bundle savedInstanceState) {
        Log.i(TAG, "Creating");
        // The "super" call is required for all activities
        super.onCreate(savedInstanceState);

        //The Vertical Linear Layout that will hold course chunks
        LinearLayout courseLists = findViewById(R.id.CourseChunks);

        //The actual chunk that will be filled with course information and added to the courseList
        View courseChunk = getLayoutInflater().inflate(R.layout.chunk_course, courseLists, false);

        //Different containers and their contents to be filled;
        TextView courseNameTitle = courseChunk.findViewById(R.id.CourseName);
        TextView courseCode = courseChunk.findViewById(R.id.CourseCode);
        TextView courseGenEdinfo = courseChunk.findViewById(R.id.AttributeHolder);
        TextView courseDescription = courseChunk.findViewById(R.id.CourseDescription);
        TextView courseCredit = courseChunk.findViewById(R.id.CreditHolder);
    }

    private void testAPI() {
    }
}
