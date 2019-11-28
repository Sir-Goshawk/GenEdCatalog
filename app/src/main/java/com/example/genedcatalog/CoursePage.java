package com.example.genedcatalog;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class CoursePage extends AppCompatActivity {

    private static final String TAG = "CoursePage";

    protected void onCreate(final Bundle savedInstanceState) {
        Log.i(TAG, "Creating");
        // The "super" call is required for all activities
        super.onCreate(savedInstanceState);

        TextView courseName = findViewById(R.id.CourseNameHolder);
        TextView creditHour = findViewById(R.id.CreditHourHolder);
        TextView courseCode = findViewById(R.id.CourseCodeHolder);
        TextView courseTerm = findViewById(R.id.TermHolder);

        LinearLayout linkedSectionList = findViewById(R.id.LinkedSecionList);


        View linkedChunk = getLayoutInflater().inflate(R.layout.chunk_course, linkedSectionList, false);

        TextView sectionName = linkedChunk.findViewById(R.id.SectionName);
        TextView CRN = linkedChunk.findViewById(R.id.CRNHolder);
        TextView meetingTime = linkedChunk.findViewById(R.id.MeetingTimeHolder);
        TextView instructorName = linkedChunk.findViewById(R.id.InstructorHolder);

    }
}
