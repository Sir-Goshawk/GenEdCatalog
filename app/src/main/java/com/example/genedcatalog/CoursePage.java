package com.example.genedcatalog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


public class CoursePage extends AppCompatActivity {

    private static final String TAG = "CoursePage";

    private LinearLayout linkedSectionLayout;

    private LinearLayout selectedSectionLayout;

    private Intent courseInfo;

    private ArrayList<String> courseSectionsList;

    protected void onCreate(final Bundle savedInstanceState) {
//        Log.i(TAG, "Creating");
        //The "super" call is required for all activities
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_page);

        //The vertical linear layout that will display a selected lecture section
        selectedSectionLayout = findViewById(R.id.SelectedSectionDisplay);

        //The vertical linear layout that will display the linked sections to the lecture selected
        linkedSectionLayout = findViewById(R.id.LinkedSecionList);

        courseInfo = getIntent();

        //TextViews for the course details
        TextView courseName = findViewById(R.id.CourseNameHolder);
        TextView creditHour = findViewById(R.id.CreditHourHolder);
        TextView courseCode = findViewById(R.id.CourseCodeHolder);
        TextView courseTerm = findViewById(R.id.TermHolder);
        TextView GenEdHolder = findViewById(R.id.GenEdHolder);
        TextView courseDescriptionHolder = findViewById(R.id.CourseDescriptionHolder);

        //Setting the text for the textviews
        courseName.setText(courseInfo.getStringExtra("courseName"));
        courseCode.setText(courseInfo.getStringExtra("courseCredit"));
        creditHour.setText(courseInfo.getStringExtra("courseCode"));
        GenEdHolder.setText(courseInfo.getStringExtra("courseGenEdInfo"));
        courseDescriptionHolder.setText(courseInfo.getStringExtra("courseDescription"));

        //Adding course sections retrieved from main list into a new array to access the sections to population the chunks later
        courseSectionsList.add(courseInfo.getStringExtra("courseSection"));

        //Populating course section chunks--NEED API FIRST BC THIS USES IT
//        for (int i = 0; i < courseSectionsList.size(); i++) {
//            addChunkBanner(null, null, ...............);
//        }



        //USED TO MAKE SURE UI WORKED
//        addChunkLinked(linkedChunk, "AL3", 98532, "Lecture", "MWF 3-4PM", "First, Last");
    }

    /**
     * This fills linked sections for the selected lecture,
     * and adds the selected lecture chunk to the top
     * @param selectedChunk
     * @param linkedChunk
     * @param sectionName
     * @param sectionType
     * @param sectionCode
     * @param CRNCode
     * @param instructorName
     * @param MeetingTime
     */
    private void addChunkBanner(final View selectedChunk, final View linkedChunk,
                                final String sectionName, final String sectionType, final int sectionCode,
                                final int CRNCode, final String instructorName, final String MeetingTime) {
        //Different containers and their contents to be filled;
        TextView sectionNameHolder = selectedChunk.findViewById(R.id.SectionName);
        TextView sectionTypeHolder = selectedChunk.findViewById(R.id.sectionType);
        TextView sectionCodeHolder = selectedChunk.findViewById(R.id.SectionCode);
        TextView CRNHolder = selectedChunk.findViewById(R.id.CRNHolder);
        TextView instructorNameHolder = selectedChunk.findViewById(R.id.InstructorHolder);
        TextView meetingTimeHolder = selectedChunk.findViewById(R.id.MeetingTimeHolder);
        Button selectButton = selectedChunk.findViewById(R.id.SelectSection);
        Button deselectButton = selectedChunk.findViewById(R.id.DeselectSection);

        //Set the TextViews with the section attributes
        sectionNameHolder.setText(sectionName);
        sectionTypeHolder.setText(sectionType);
        sectionCodeHolder.setText(sectionCode);
        CRNHolder.setText(CRNCode);
        instructorNameHolder.setText(instructorName);
        meetingTimeHolder.setText(MeetingTime);

        //Initially, select button is visible, the deselect button is gone
        selectButton.setVisibility(View.VISIBLE);
        deselectButton.setVisibility(View.GONE);

        //If a section is selected...
        selectButton.setOnClickListener(unused -> {
            selectedCourse(selectedChunk, linkedChunk, sectionName, sectionType,
                    sectionCode, CRNCode, instructorName, MeetingTime);
            deselectButton.setVisibility(View.VISIBLE);
        });

        //USED TO MAKE SURE UI WORKED
//        selectedCourse(linkedChunk, "ADD", 531975, "Discussion/Recitation", "11-12AM TT", "first, last");


        //??????
        deselectButton.setOnClickListener(unused -> {
//            selectedSectionLayout.removeView(selectedChunk);
        });
    }

    private void addChunkLinked(final View linkedChunk, final String sectionName,
                                final String sectionType, final int sectionCode, final int CRNCode,
                                final String instructorName, final String MeetingTime) {
        //Different containers and their contents to be filled;
        TextView sectionNameHolder = linkedChunk.findViewById(R.id.SectionName);
        TextView sectionTypeHolder = linkedChunk.findViewById(R.id.sectionType);
        TextView sectionCodeHolder = linkedChunk.findViewById(R.id.SectionCode);
        TextView CRNHolder = linkedChunk.findViewById(R.id.CRNHolder);
        TextView instructorNameHolder = linkedChunk.findViewById(R.id.InstructorHolder);
        TextView meetingTimeHolder = linkedChunk.findViewById(R.id.MeetingTimeHolder);
        Button selectButton = linkedChunk.findViewById(R.id.SelectSection);
        Button deselectButton = linkedChunk.findViewById(R.id.DeselectSection);

        //Set the TextViews with the section attributes
        sectionNameHolder.setText(sectionName);
        sectionTypeHolder.setText(sectionType);
        sectionCodeHolder.setText(sectionCode);
        CRNHolder.setText(CRNCode);
        instructorNameHolder.setText(instructorName);
        meetingTimeHolder.setText(MeetingTime);

        //Initially, select button is visible, the deselect button is gone
        selectButton.setVisibility(View.VISIBLE);
        deselectButton.setVisibility(View.INVISIBLE);

        //If the section type is a lecture, then there's no need to deselect
//        if (sectionType.equals("Lecture")) {
//            deselectButton.setVisibility(View.GONE);
//        }

        //If the user selects a linked section, add it into the selected section layout
        selectButton.setOnClickListener(unused -> {
            selectedSectionLayout.addView(linkedChunk);
            deselectButton.setVisibility(View.VISIBLE);
        });
        Log.d("Called", "" + linkedSectionLayout.getChildCount());
    }

    private void selectedCourse(final View selectedSection, final View linkedSection, final String sectionName,
                                final String sectionType, final int sectionCode, final int CRNCode,
                                final String instructorName, final String MeetingTime) {
        //clear the current linked list
        linkedSectionLayout.setVisibility(View.INVISIBLE);

        //if a lecture section is selected then you do not have to display the banner
        TextView nonSelectedLectureHolder = findViewById(R.id.NonSelectedSection);
        nonSelectedLectureHolder.setVisibility(View.INVISIBLE);

        //Add the selected lecture chunk to the selected section banner
        selectedSectionLayout.addView(selectedSection);

        //Add new chunks of linked discussion sections
        View linkedChunk = getLayoutInflater().inflate(R.layout.chunk_section, linkedSectionLayout, false);
        addChunkLinked(linkedChunk, sectionName, sectionType, sectionCode, CRNCode, instructorName, MeetingTime);
    }
}
