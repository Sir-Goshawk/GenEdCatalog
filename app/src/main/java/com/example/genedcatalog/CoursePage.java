package com.example.genedcatalog;

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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.ArrayList;
import java.util.Iterator;


public class CoursePage extends AppCompatActivity {

    private static final String TAG = "CoursePage";

    private LinearLayout linkedSectionLayout;

    private LinearLayout selectedSectionLayout;

    private Intent courseInfo;

    /**
     * What shows up when the app first opens
     * @param savedInstanceState ?
     */
    protected void onCreate(final Bundle savedInstanceState) {
        Log.i("mine", "Creating");
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
        ArrayList<String> getGenEdNames = courseInfo.getStringArrayListExtra("courseGenEdNames");
        String genEdNames = "";
        for (int i = 0; i < getGenEdNames.size(); i++) {
            genEdNames += getGenEdNames.get(i) +"\n";
        }
        GenEdHolder.setText(genEdNames);
        courseDescriptionHolder.setText(courseInfo.getStringExtra("courseDescription"));

        //Adding course sections retrieved from main list into a new array to access the sections to population the chunks later
        ArrayList<String> getCourseSectionsList = courseInfo.getStringArrayListExtra("courseSection");
        //Populating course section chunks--NEED API FIRST BC THIS USES IT
        for (int i = 0; i < getCourseSectionsList.size(); i++) {
            requestAPI(getCourseSectionsList.get(i));
        }



        //USED TO MAKE SURE UI WORKED
//        addChunkLinked(linkedChunk, "AL3", 98532, "Lecture", "MWF 3-4PM", "First, Last");
    }

    /**
     * The code to fill in the course section chunks.
     * The code to also add the selected lecture chunk to the selected sections layout.
     * @param sectionName the name of the selected course
     * @param sectionType the section type (lecture/discussion/lab)
     * @param sectionCode the code of the selected course (i.e. would be 125 in CS 125)
     * @param CRNCode the id of the selected course
     * @param instructorName the instructor the course
     * @param MeetingTime the times there are class for this course
     */
    private void addChunkBanner(final String sectionName, final String sectionType, final String sectionCode,
                                final String CRNCode, final String instructorName, final String MeetingTime) {
        View selectedChunk = getLayoutInflater().inflate(R.layout.chunk_section, linkedSectionLayout, false);
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
            selectedCourse(sectionName, sectionType, sectionCode, CRNCode, instructorName, MeetingTime);
            deselectButton.setVisibility(View.VISIBLE);
            deselectButton.setOnClickListener(u -> {
                //remove the selected section
                selectedSectionLayout.removeView(selectedChunk);
                //clear the linked list for the selected section
                linkedSectionLayout.setVisibility(View.INVISIBLE);
                if (selectedSectionLayout.getChildCount() == 0) {
                    TextView nonSelectedLectureHolder = findViewById(R.id.NonSelectedSection);
                    nonSelectedLectureHolder.setVisibility(View.VISIBLE);
                }
            });
        });

        Log.d("mine added chunk", sectionName+" - "+sectionType+" - "+sectionCode+" - " + CRNCode+" - "+instructorName+" - "+MeetingTime);
        linkedSectionLayout.addView(selectedChunk);
    }

    /**
     * The code to fill in the linked section chunks.
     * The code to also add the selected linked chunk to the selected sections layout.
     * @param sectionName the name of the linked course
     * @param sectionType the section type (lecture/discussion/lab)
     * @param sectionCode the code of the linked course (i.e. would be 125 in CS 125)
     * @param CRNCode the id of the selected course
     * @param instructorName the instructor the course
     * @param MeetingTime the times there are class for this course
     */
    private void addChunkLinked(final String sectionName, final String sectionType, final String sectionCode, final String CRNCode,
                                final String instructorName, final String MeetingTime) {
        View linkedChunk = getLayoutInflater().inflate(R.layout.chunk_section, linkedSectionLayout, false);
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
            deselectButton.setOnClickListener(u -> {
                //remove the selected linked section
                selectedSectionLayout.removeView(linkedChunk);
                //clear the linked list for the selected linked section
                linkedSectionLayout.setVisibility(View.INVISIBLE);
                if (selectedSectionLayout.getChildCount() == 0) {
                    TextView nonSelectedLectureHolder = findViewById(R.id.NonSelectedSection);
                    nonSelectedLectureHolder.setVisibility(View.VISIBLE);
                }
            });
        });
        Log.d("Called", "" + linkedSectionLayout.getChildCount());
    }

    /**
     * Runs when a course is selected.
     * @param sectionName the name of the selected course
     * @param sectionType the section type (lecture/discussion/lab)
     * @param sectionCode the code of the selected course (i.e. would be 125 in CS 125)
     * @param CRNCode the id of the selected course
     * @param instructorName the instructor the course
     * @param MeetingTime the times there are class for this course
     */
    private void selectedCourse(final String sectionName,
                                final String sectionType, final String sectionCode, final String CRNCode,
                                final String instructorName, final String MeetingTime) {

        View selectedSection = getLayoutInflater().inflate(R.layout.chunk_section, selectedSectionLayout, false);
        //If a lecture section is selected then you do not have to display the banner
        TextView nonSelectedLectureHolder = findViewById(R.id.NonSelectedSection);
        nonSelectedLectureHolder.setVisibility(View.INVISIBLE);

        //Add the selected lecture chunk to the selected section banner
        selectedSectionLayout.addView(selectedSection);

        //Add new chunks of linked discussion sections
        View linkedChunk = getLayoutInflater().inflate(R.layout.chunk_section, linkedSectionLayout, false);
        addChunkLinked(sectionName, sectionType, sectionCode, CRNCode, instructorName, MeetingTime);
    }

    private void requestAPI(final String url) {

        if (url == null) {
            throw new IllegalArgumentException();
        }
        //Instantiate the RequestQueue--gets the Course Explorer API
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject requestedJSONObj = XML.toJSONObject(response);

                //first JSON node of the given lastResponse
                Iterator<String> keys = requestedJSONObj.keys();
                String firstNode = keys.next();
                requestedJSONObj = requestedJSONObj.getJSONObject(firstNode);
//                Log.d("mine", requestedJSONObj.toString(2));

                String instructor = "";
                try {
                    instructor = requestedJSONObj.getJSONObject("meetings").getJSONObject("meeting").getJSONObject("instructors").getJSONObject("instructor").getString("content");
                } catch (JSONException e) {
                    instructor = requestedJSONObj.getJSONObject("meetings").getJSONObject("meeting").getJSONObject("instructors").getString("instructor");
                }

                if (requestedJSONObj.getJSONObject("meetings").getJSONObject("meeting").getJSONObject("type").getString("content").equals("Lecture")) {
                    if (requestedJSONObj.getJSONObject("meetings").getJSONObject("meeting").getJSONObject("type").getString("content").equals("Online")) {
                        addChunkBanner(requestedJSONObj.getJSONObject("parents").getJSONObject("course").getString("content"),
                                requestedJSONObj.getJSONObject("meetings").getJSONObject("meeting").getJSONObject("type").getString("content"),
                                requestedJSONObj.getString("sectionNumber"), requestedJSONObj.getString("id"), instructor, "Online");
                    } else {
                        addChunkBanner(requestedJSONObj.getJSONObject("parents").getJSONObject("course").getString("content"),
                                requestedJSONObj.getJSONObject("meetings").getJSONObject("meeting").getJSONObject("type").getString("content"),
                                requestedJSONObj.getString("sectionNumber"), requestedJSONObj.getString("id"), instructor,
                                requestedJSONObj.getJSONObject("meetings").getJSONObject("meeting").getString("daysOfTheWeek") +
                                        requestedJSONObj.getJSONObject("meetings").getJSONObject("meeting").getString("start") + " ~ " +
                                        requestedJSONObj.getJSONObject("meetings").getJSONObject("meeting").getString("end"));
                    }
                }
            } catch (JSONException e) {
                Log.d("mine", url + " error is " + e);
            }
        }, error -> { Log.d("mine", "error web " + error); });
        queue.add(stringRequest);
    }
}
