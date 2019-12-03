package com.example.genedcatalog;

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

    protected void onCreate(final Bundle savedInstanceState) {
        Log.i(TAG, "Creating");
        // The "super" call is required for all activities
        super.onCreate(savedInstanceState);

        TextView courseName = findViewById(R.id.CourseNameHolder);
        TextView creditHour = findViewById(R.id.CreditHourHolder);
        TextView courseCode = findViewById(R.id.CourseCodeHolder);
        TextView courseTerm = findViewById(R.id.TermHolder);

        linkedSectionList = findViewById(R.id.LinkedSecionList);

        //The Vertical Linear Layout that will display a selected Lecture Section
        selectedSectionLayout = findViewById(R.id.SelectedSectionDisplay);

        View linkedChunk = getLayoutInflater().inflate(R.layout.chunk_course, linkedSectionList, false);

        /*
        TextView sectionName = linkedChunk.findViewById(R.id.SectionName);
        TextView CRN = linkedChunk.findViewById(R.id.CRNHolder);
        TextView meetingTime = linkedChunk.findViewById(R.id.MeetingTimeHolder);
        TextView instructorName = linkedChunk.findViewById(R.id.InstructorHolder);
        Button selectButton = linkedChunk.findViewById(R.id.SelectCourse);

        selectButton.setOnClickListener(unused -> {
            selectedCourse(linkedChunk);
        });
         */
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
        Button selectButton = linkedChunk.findViewById(R.id.SelectCourse);
        Button deselectButton = linkedChunk.findViewById(R.id.DeselectSection);

        //Set the TextViews with the section attributes
        sectionNameHolder.setText(sectionName);
        CRNHolder.setText(CRNCode);
        sectionTypeHolder.setText(sectionType);
        meetingTimeHolder.setText(MeetingTime);
        instructorNameHolder.setText(instructorName);

        //If the section type is a lecture, then there's no need to deselect
        if (sectionType.equals("lecture")) {
            deselectButton.setVisibility(View.GONE);
        }

        //If the user select the said lecture section, then start a link section event
        selectButton.setOnClickListener(unused -> {
            selectedCourse(linkedChunk);
        });
    }

    private void selectedCourse(final View linkedChunk) {
        //clear the current linked list
        linkedSectionList.setVisibility(View.INVISIBLE);

        //if a lecture section is selected then you do not have to display the banner
        TextView nonSelectedLectureHolder = linkedChunk.findViewById(R.id.NonSelectedSection);
        nonSelectedLectureHolder.setVisibility(View.GONE);

        //Add the selected lecture chunk to the selected section banner

        //add new chunks of linked discussion sections
        addChunkLinked(linkedChunk, "HIST 143", 481236, "Discussion/Recition", "4-5 PM", "Last, First");
    }
}
