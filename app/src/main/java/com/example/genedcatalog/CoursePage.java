package com.example.genedcatalog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class CoursePage extends AppCompatActivity {

    private static final String TAG = "CoursePage";

    private LinearLayout linkedSectionList;

    private LinearLayout selectedSectionLayout;

    private Intent courseInfo;

    protected void onCreate(final Bundle savedInstanceState) {
//        Log.i(TAG, "Creating");
        // The "super" call is required for all activities
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_page);


        linkedSectionList = findViewById(R.id.LinkedSecionList);

        //The Vertical Linear Layout that will display a selected Lecture Section
        selectedSectionLayout = findViewById(R.id.SelectedSectionDisplay);

        courseInfo = getIntent();


        TextView courseName = findViewById(R.id.CourseNameHolder);
        TextView creditHour = findViewById(R.id.CreditHourHolder);
        TextView courseCode = findViewById(R.id.CourseCodeHolder);
        TextView courseTerm = findViewById(R.id.TermHolder);
        TextView GenEdHolder = findViewById(R.id.GenEdHolder);

        courseName.setText(courseInfo.getStringExtra("courseName"));
        creditHour.setText(courseInfo.getStringExtra("courseCode"));
        courseCode.setText(courseInfo.getStringExtra("courseCredit"));
        GenEdHolder.setText(courseInfo.getStringExtra("courseGenEdInfo"));


        View linkedChunk = getLayoutInflater().inflate(R.layout.chunk_section, linkedSectionList, false);

        addChunkLinked(linkedChunk, "AL3", 98532, "Lecture", "MWF 3-4PM", "First, Last");

    }

    private void addChunkLinked(final View linkedChunk, final String sectionName,
                                final int CRNCode, final String sectionType,
                                final String MeetingTime, final String instructorName) {
        //Different containers and their contents to be filled;
        TextView sectionNameHolder = linkedChunk.findViewById(R.id.SectionName);
        TextView CRNHolder = linkedChunk.findViewById(R.id.CRNHolder);
        TextView sectionTypeHolder = linkedChunk.findViewById(R.id.sectionType);
        TextView meetingTimeHolder = linkedChunk.findViewById(R.id.MeetingTimeHolder);
        TextView instructorNameHolder = linkedChunk.findViewById(R.id.InstructorHolder);
        Button selectButton = linkedChunk.findViewById(R.id.SelectSection);
        Button deselectButton = linkedChunk.findViewById(R.id.DeselectSection);

        //Set the TextViews with the section attributes
        sectionNameHolder.setText(sectionName);
        CRNHolder.setText("" + CRNCode);
        sectionTypeHolder.setText(sectionType);
        meetingTimeHolder.setText(MeetingTime);
        instructorNameHolder.setText(instructorName);

        //If the section type is a lecture, then there's no need to deselect
        if (sectionType.equals("Lecture")) {
            deselectButton.setVisibility(View.GONE);
        }

        //If the user select the said lecture section, then start a link section event
        selectButton.setOnClickListener(unused -> {
            selectedCourse(linkedChunk, sectionName, CRNCode, sectionType, MeetingTime, instructorName);
        });

        linkedSectionList.addView(linkedChunk);
    }

    private void selectedCourse(final View linkChunk, final String sectionName,
                                final int CRNCode, final String sectionType,
                                final String MeetingTime, final String instructorName) {
        //clear the current linked list
        linkedSectionList.setVisibility(View.INVISIBLE);

        //if a lecture section is selected then you do not have to display the banner
        TextView nonSelectedLectureHolder = findViewById(R.id.NonSelectedSection);
        nonSelectedLectureHolder.setVisibility(View.GONE);

        //Add the selected lecture chunk to the selected section banner

        //add new chunks of linked discussion sections
        View selectedChunk = getLayoutInflater().inflate(R.layout.chunk_section, linkedSectionList, false);
        addChunkBanner(selectedChunk, linkChunk, sectionName, CRNCode, sectionType, MeetingTime, instructorName);
    }

    private void addChunkBanner(final View selectedChunk, final View linkedChunk,
                                final String sectionName, final int CRNCode, final String sectionType,
                                final String MeetingTime, final String instructorName) {
        //Different containers and their contents to be filled;
        TextView sectionNameHolder = selectedChunk.findViewById(R.id.SectionName);
        TextView CRNHolder = selectedChunk.findViewById(R.id.CRNHolder);
        TextView sectionTypeHolder = selectedChunk.findViewById(R.id.sectionType);
        TextView meetingTimeHolder = selectedChunk.findViewById(R.id.MeetingTimeHolder);
        TextView instructorNameHolder = selectedChunk.findViewById(R.id.InstructorHolder);
        Button selectButton = selectedChunk.findViewById(R.id.SelectSection);
        Button deselectButton = selectedChunk.findViewById(R.id.DeselectSection);

        //Set the TextViews with the section attributes
        sectionNameHolder.setText(sectionName);
        CRNHolder.setText("" + CRNCode);
        sectionTypeHolder.setText(sectionType);
        meetingTimeHolder.setText(MeetingTime);
        instructorNameHolder.setText(instructorName);

        //If the section type is a lecture, then there's no need to deselect
        selectButton.setVisibility(View.GONE);

        deselectButton.setOnClickListener(unused -> {
            selectedCourse(linkedChunk, "ADD", 531975, "Discussion/Recitation", "11-12AM TT", "first, last");
        });

        selectedSectionLayout.addView(selectedChunk);


    }
}
