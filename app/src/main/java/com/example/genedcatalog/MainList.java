package com.example.genedcatalog;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainList extends AppCompatActivity {

    private static final String TAG = "MainList";

    private LinearLayout courseLists;

    protected void onCreate(final Bundle savedInstanceState) {
        Log.i(TAG, "creating");
        // The "super" call is required for all activities
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_list);

        //The Vertical Linear Layout that will hold course chunks
        courseLists = findViewById(R.id.CourseChunks);

        //The actual chunk that will be filled with course information and added to the courseList
        View courseChunk = getLayoutInflater().inflate(R.layout.chunk_course, courseLists, false);

        addChunkCourse(courseChunk,"History Class", "HIST141", "HP \nWW",
                "THis is a description", 3);

        TextView mainTitleHolder = findViewById(R.id.MainTitle);

        // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this);
//            String url ="http://courses.illinois.edu/cisapp/explorer/schedule/:year";
            String url ="http://courses.illinois.edu/cisapp/explorer/schedule.xml";
//            String url ="http://courses.illinois.edu/cisapp/explorer/schedule/:year/:semester/:subjectCode";
//            String url ="http://www.google.com";

        Log.d("mine", "Message is calle");
        // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            Log.d("mine", "the response is "+response);
//                            mainTitleHolder.setText("Response is: "+ response.substring(0,50));
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("mine", "the error is "+error);
//                    mainTitleHolder.setText("That didn't work!");
                }
            });

        // Add the request to the RequestQueue.
            queue.add(stringRequest);
    }

    private void goToCoursePage(final String courseName, final String courseCode,
                                final String courseGenEdInfo, final String courseDescription,
                                final int courseCredit) {
        Intent intent = new Intent(this, CoursePage.class);
        intent.putExtra("courseName", courseName);
        intent.putExtra("courseCode", courseCode);
        intent.putExtra("courseGenEdInfo", courseGenEdInfo);
        intent.putExtra("courseDescription", courseDescription);
        intent.putExtra("courseCredit", courseCredit + "");
        startActivity(intent);
    }

    private void addChunkCourse(final View courseChunk, final String courseName,
                                final String courseCode, final String courseGenEdInfo,
                                final String courseDescription, final int courseCredit) {

        //Different containers and their contents to be filled;
        TextView courseNameHolder = courseChunk.findViewById(R.id.CourseName);
        TextView courseCodeHolder = courseChunk.findViewById(R.id.CourseCode);
        TextView courseGenEdinfoHolder = courseChunk.findViewById(R.id.AttributeHolder);
        TextView courseDescriptionHolder = courseChunk.findViewById(R.id.CourseDescription);
        TextView courseCreditHolder = courseChunk.findViewById(R.id.CreditHolder);
        Button courseButton = courseChunk.findViewById(R.id.SelectCourse);

        courseNameHolder.setText(courseName);
        courseCodeHolder.setText(courseCode);
        courseGenEdinfoHolder.setText(courseGenEdInfo);
        courseDescriptionHolder.setText(courseDescription);
        courseCreditHolder.setText("" + courseCredit);

        courseButton.setOnClickListener(unused -> {
            goToCoursePage(courseName, courseCode, courseGenEdInfo, courseDescription, courseCredit);
        });
        courseLists.addView(courseChunk);
    }
}
