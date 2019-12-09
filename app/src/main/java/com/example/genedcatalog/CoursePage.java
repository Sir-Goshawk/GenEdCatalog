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

    private LinearLayout lectureSectionLayout;
    
    private LinearLayout linkedBannerSelected;

    private Intent courseInfo;

    //Adding course sections retrieved from main list into a new array to access the sections to population the chunks later
    private ArrayList<String> getCourseSectionsList;

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

        //
        lectureSectionLayout = findViewById(R.id.LectureList);


        linkedBannerSelected = findViewById(R.id.LinkedBannerSelected);

        linkedBannerSelected.setVisibility(View.GONE);

        courseInfo = getIntent();

        getCourseSectionsList = courseInfo.getStringArrayListExtra("courseSection");

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

        //Populating course section chunks--NEED API FIRST BC THIS USES IT
        for (int i = 0; i < getCourseSectionsList.size(); i++) {
            requestAPI(getCourseSectionsList.get(i), "lecture");
        }
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
    private void addChunk(final String sectionName, final String sectionType, final String sectionCode,
                                final String CRNCode, final String instructorName, final String MeetingTime,
                                 final LinearLayout layoutToAdd) {
        View selectedChunk = getLayoutInflater().inflate(R.layout.chunk_section, layoutToAdd, false);
        //Different containers and their contents to be filled;
        TextView sectionTypeHolder = selectedChunk.findViewById(R.id.sectionType);
        TextView sectionCodeHolder = selectedChunk.findViewById(R.id.SectionCode);
        TextView CRNHolder = selectedChunk.findViewById(R.id.CRNHolder);
        TextView instructorNameHolder = selectedChunk.findViewById(R.id.InstructorHolder);
        TextView meetingTimeHolder = selectedChunk.findViewById(R.id.MeetingTimeHolder);
        Button selectButton = selectedChunk.findViewById(R.id.SelectSection);
        Button deselectButton = selectedChunk.findViewById(R.id.DeselectSection);

        //Set the TextViews with the section attributes
        sectionTypeHolder.setText(sectionType);
        sectionCodeHolder.setText(sectionCode);
        CRNHolder.setText(CRNCode);
        instructorNameHolder.setText(instructorName);
        meetingTimeHolder.setText(MeetingTime);

        if (layoutToAdd.equals(selectedSectionLayout) || layoutToAdd.equals(lectureSectionLayout)) {
            //Initially, select button is visible, the deselect button is gone
            if (layoutToAdd.equals(lectureSectionLayout)) {
                selectButton.setVisibility(View.VISIBLE);
                deselectButton.setVisibility(View.GONE);
            } else {
                deselectButton.setVisibility(View.VISIBLE);
                selectButton.setVisibility(View.GONE);
                deselectButton.setOnClickListener(u -> {
                    //remove the selected linked section
                    selectedSectionLayout.removeView(selectedChunk);
                    //clear the linked list for the selected linked section
                    if (selectedSectionLayout.getChildCount() == 0) {
                        TextView nonSelectedLectureHolder = findViewById(R.id.NonSelectedSection);
                        nonSelectedLectureHolder.setVisibility(View.VISIBLE);
                    }
                    linkedSectionLayout.setVisibility(View.GONE);
                    lectureSectionLayout.setVisibility(View.VISIBLE);
                    Log.d("mine deselect", lectureSectionLayout.getVisibility() + "");
                });
            }

            //If a section is selected...
            selectButton.setOnClickListener(unused -> {
                selectedCourse(sectionName, sectionType, sectionCode, CRNCode, instructorName, MeetingTime);
            });

            layoutToAdd.addView(selectedChunk);
        } else {
            //Initially, select button is visible, the deselect button is gone
            deselectButton.setVisibility(View.GONE);

            //If the user selects a linked section, add it into the selected section layout
            selectButton.setOnClickListener(unused -> {
                linkedSectionLayout.removeView(selectedChunk);
                selectedSectionLayout.addView(selectedChunk);
                deselectButton.setVisibility(View.VISIBLE);
                selectButton.setVisibility(View.GONE);
                linkedSectionLayout.setVisibility(View.GONE);
                deselectButton.setOnClickListener(u -> {
                    selectedSectionLayout.removeView(selectedChunk);
                    linkedSectionLayout.setVisibility(View.VISIBLE);
                });
            });
            linkedSectionLayout.addView(selectedChunk);
        }
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
    private void selectedCourse(final String sectionName, final String sectionType, final String sectionCode, final String CRNCode,
                                final String instructorName, final String MeetingTime) {
        //If a lecture section is selected then you do not have to display the banner
        TextView nonSelectedLectureHolder = findViewById(R.id.NonSelectedSection);
        nonSelectedLectureHolder.setVisibility(View.INVISIBLE);

        linkedBannerSelected.setVisibility(View.VISIBLE);

        //Add the selected lecture chunk to the selected section banner
        lectureSectionLayout.setVisibility(View.GONE);
        linkedSectionLayout.setVisibility(View.VISIBLE);
        addChunk(sectionName, sectionType, sectionCode, CRNCode, instructorName, MeetingTime, selectedSectionLayout);
        
        //Add new chunks of linked discussion sections
        for (int i = 0; i < getCourseSectionsList.size(); i++) {
            requestAPI(getCourseSectionsList.get(i), "other");
        }

        Log.d("mine lecture selected", ""+lectureSectionLayout.getVisibility() + linkedSectionLayout.getChildCount());
    }

    private void requestAPI(final String url, final String sectionType) {
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

                String instructor = "";
                try {
                    for (int i = 0; i < requestedJSONObj.getJSONObject("meetings").getJSONObject("meeting").getJSONObject("instructors").getJSONArray("instructor").length(); i++) {
                        instructor += requestedJSONObj.getJSONObject("meetings").getJSONObject("meeting").getJSONObject("instructors").getJSONArray("instructor").getJSONObject(i).getString("content") +"\n";
                    }
                } catch (JSONException e) {
                    try {
                        instructor = requestedJSONObj.getJSONObject("meetings").getJSONObject("meeting").getJSONObject("instructors").getJSONObject("instructor").getString("content");
                    } catch (JSONException p) {
                        instructor = requestedJSONObj.getJSONObject("meetings").getJSONObject("meeting").getString("instructors");
                    }
                }

                if (sectionType.equals("lecture") && (requestedJSONObj.getJSONObject("meetings").getJSONObject("meeting").getJSONObject("type").getString("content").equals("Lecture") ||
                        requestedJSONObj.getJSONObject("meetings").getJSONObject("meeting").getJSONObject("type").getString("content").equals("Lecture-Discussion") ||
                        requestedJSONObj.getJSONObject("meetings").getJSONObject("meeting").getJSONObject("type").getString("content").equals("Online"))) {
                    if (requestedJSONObj.getJSONObject("meetings").getJSONObject("meeting").getJSONObject("type").getString("content").equals("Online")) {
                        addChunk(requestedJSONObj.getJSONObject("parents").getJSONObject("course").getString("content"),
                                requestedJSONObj.getJSONObject("meetings").getJSONObject("meeting").getJSONObject("type").getString("content"),
                                requestedJSONObj.getString("sectionNumber"), requestedJSONObj.getString("id"), instructor, "Online", lectureSectionLayout);
                    } else {
                        addChunk(
                                requestedJSONObj.getJSONObject("parents").getJSONObject("course").getString("content"),
                                requestedJSONObj.getJSONObject("meetings").getJSONObject("meeting").getJSONObject("type").getString("content"),
                                requestedJSONObj.getString("sectionNumber"),
                                requestedJSONObj.getString("id"),
                                instructor,
                                requestedJSONObj.getJSONObject("meetings").getJSONObject("meeting").getString("daysOfTheWeek") +
                                        requestedJSONObj.getJSONObject("meetings").getJSONObject("meeting").getString("start") + " ~ " +
                                        requestedJSONObj.getJSONObject("meetings").getJSONObject("meeting").getString("end"),
                                lectureSectionLayout);
                    }
                }
                if (sectionType.equals("other") && !(requestedJSONObj.getJSONObject("meetings").getJSONObject("meeting").getJSONObject("type").getString("content").equals("Lecture") ||
                        requestedJSONObj.getJSONObject("meetings").getJSONObject("meeting").getJSONObject("type").getString("content").equals("Lecture-Discussion") ||
                        requestedJSONObj.getJSONObject("meetings").getJSONObject("meeting").getJSONObject("type").getString("content").equals("Online"))) {
                    if (requestedJSONObj.getJSONObject("meetings").getJSONObject("meeting").getJSONObject("type").getString("content").equals("Online")) {
                        addChunk(requestedJSONObj.getJSONObject("parents").getJSONObject("course").getString("content"),
                                requestedJSONObj.getJSONObject("meetings").getJSONObject("meeting").getJSONObject("type").getString("content"),
                                requestedJSONObj.getString("sectionNumber"), requestedJSONObj.getString("id"), instructor, "Online", linkedSectionLayout);
                    } else {
                        addChunk(requestedJSONObj.getJSONObject("parents").getJSONObject("course").getString("content"),
                                requestedJSONObj.getJSONObject("meetings").getJSONObject("meeting").getJSONObject("type").getString("content"),
                                requestedJSONObj.getString("sectionNumber"), requestedJSONObj.getString("id"), instructor,
                                requestedJSONObj.getJSONObject("meetings").getJSONObject("meeting").getString("daysOfTheWeek") +
                                        requestedJSONObj.getJSONObject("meetings").getJSONObject("meeting").getString("start") + " ~ " +
                                        requestedJSONObj.getJSONObject("meetings").getJSONObject("meeting").getString("end"), linkedSectionLayout);

                    }
                }
            } catch (JSONException e) {
                Log.d("mine", url + " error is " + e);
            }
        }, error -> { Log.d("mine", "error web " + error); });
        queue.add(stringRequest);
    }
}
