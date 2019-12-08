package com.example.genedcatalog;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainList extends AppCompatActivity {

    private static final String TAG = "MainList";

    private LinearLayout courseLists;

    //List of courses from API
    private List courseListFromAPI = new ArrayList<Course>();

    private String urlBASEstart = "http://courses.illinois.edu/cisapp/explorer/schedule/2020/spring/";

    private String urlBASEend = ".xml";

    /**
     * What shows up when the app first opens
     * @param savedInstanceState ?
     */
    protected void onCreate(final Bundle savedInstanceState) {
        Log.i(TAG, "creating");
        //The "super" call is required for all activities
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_list);

        //The vertical linear layout that will hold course chunks
        courseLists = findViewById(R.id.CourseChunks);

        Spinner subjectChoices = findViewById(R.id.SubjectChoice);
        subjectChoices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(final AdapterView<?> parent, final View view,
                                       final int position, final long id) {
                courseLists.removeAllViews();
                requestAPI(urlBASEstart + getResources().getStringArray(R.array.CourseSubjectCode)[position] + urlBASEend);
//                //Loop through list of courses retrieved from API to populate course chunks on main page
//                for (int i = 0; i < courseListFromAPI.size(); i++) {
//                    Log.d("mine", "called");
//                    Course course = (Course) courseListFromAPI.get(i);
//                    addChunkCourse(courseChunk, course);
//                }
            }
            public void onNothingSelected(final AdapterView<?> parent) {
            }
        });
    }

    //Function to be used to populate course chunks
    private void addChunkCourse(final Course courseToAdd) {
        //The actual chunk that will be filled with course information and added to the course list
        View courseChunk = getLayoutInflater().inflate(R.layout.chunk_course, courseLists, false);

        //Different containers and their contents to be filled;
        TextView courseNameHolder = courseChunk.findViewById(R.id.CourseName);
        TextView courseCodeHolder = courseChunk.findViewById(R.id.CourseCode);
        TextView courseGenEdinfoHolder = courseChunk.findViewById(R.id.AttributeHolder);
        TextView courseDescriptionHolder = courseChunk.findViewById(R.id.CourseDescription);
        TextView courseCreditHolder = courseChunk.findViewById(R.id.CreditHolder);
        Button courseButton = courseChunk.findViewById(R.id.SelectCourse);

        courseNameHolder.setText(courseToAdd.getName());
        courseCodeHolder.setText(courseToAdd.getCode());
        courseGenEdinfoHolder.setText(courseToAdd.getGenEdInfo());
        courseDescriptionHolder.setText(courseToAdd.getDescription());
        courseCreditHolder.setText(courseToAdd.getCredit());

        courseButton.setOnClickListener(unused -> {
            goToCoursePage(courseToAdd);
        });
        courseLists.addView(courseChunk);
    }

    //Transfers info to course page
    private void goToCoursePage(final Course toAdd) {
        Intent intent = new Intent(this, CoursePage.class);
        intent.putExtra("courseName", toAdd.getName());
        intent.putExtra("courseCredit", toAdd.getCredit());
        intent.putExtra("courseCode", toAdd.getCode());
        intent.putExtra("courseGenEdInfo", toAdd.getGenEdInfo());
        intent.putExtra("courseDescription", toAdd.getDescription());
        for (int i = 0; i < courseListFromAPI.size(); i++) {
            Course course = (Course) courseListFromAPI.get(i);
            ArrayList<String> courseSectionList = course.getCourseSection();
            for (int j = 0; j < courseSectionList.size(); j++) {
                String courseSection = courseSectionList.get(j);
                intent.putExtra("courseSection", courseSection);
            }
        }
        startActivity(intent);
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

                if (firstNode.equals("ns2:term")) {
                    //if it's a termXML
                    JSONArray subjectJSON = requestedJSONObj.getJSONObject(firstNode).getJSONObject("subjects").getJSONArray("subject");
                    for (int i = 0; i < subjectJSON.length(); i++) {
                        requestAPI(subjectJSON.getJSONObject(i).getString("href"));
                    }
                } else if (firstNode.equals("ns2:subject")) {
                    //check if it's subjectXML
                    try {
                        JSONArray courseJSON = requestedJSONObj.getJSONObject(firstNode).getJSONObject("courses").getJSONArray("course");
                        for (int i = 0; i < courseJSON.length(); i++) {
                            JSONObject current = courseJSON.getJSONObject(i);
                            if (current.getString("href").charAt(current.getString("href").length() - 1) == 'l') {
                                requestAPI(current.getString("href"));
                            }
                        }
                    } catch (JSONException e) {
                        Log.d("mine", "rord" + e);
                        if (requestedJSONObj.getJSONObject(firstNode).getJSONObject("courses").getJSONObject("course").getString("href").
                                charAt(requestedJSONObj.getJSONObject(firstNode).getJSONObject("courses").getJSONObject("course").getString("href").length() - 1) == 'l') {
                            requestAPI(requestedJSONObj.getJSONObject(firstNode).getJSONObject("courses").getJSONObject("course").getString("href"));
                        }
                    }
                } else if (firstNode.equals("ns2:course")) {
                    //check base case, when it's a courseXML, add the genEd Attributes to the genedCourses List
                    JSONObject subJSON = requestedJSONObj.getJSONObject(firstNode);
                    try {
                        if (subJSON.getString("genEdCategories") != null) {
                            ArrayList<String> subject = new ArrayList<>();
                            try {
                                for (int i = 0; i < subJSON.getJSONObject("sections").getJSONArray("section").length(); i++) {
                                    subject.add(subJSON.getJSONObject("sections").getJSONArray("section").getJSONObject(i).getString("href"));
                                }
                            } catch (JSONException e) {
                                subject.add(subJSON.getJSONObject("sections").getJSONObject("section").getString("href"));
                            }

                            String genEd = "";
                            try {
                                for (int i = 0; i < subJSON.getJSONObject("genEdCategories").getJSONArray("category").length(); i++) {
                                    genEd += subJSON.getJSONObject("genEdCategories").getJSONArray("category").getJSONObject(i).getString("id") + "\n";
                                }
                            } catch (JSONException e) {
                                genEd += subJSON.getJSONObject("genEdCategories").getJSONObject("category").getString("id") + "\n";
                            }

                            Course toAdd = new Course(subJSON.getString("label"),
                                    subJSON.getString("id"),
                                    genEd,
                                    subJSON.getString("description"),
                                    Integer.parseInt(subJSON.getString("creditHours").substring(0, 1)), subject);
                            addChunkCourse(toAdd);
                        }
                    } catch (JSONException e) {}
                }
            } catch (JSONException e) {
                Log.d("mine", "error is " + e);
            }
        }, error -> { Log.d("mine", "error web " + error); });
        queue.add(stringRequest);
    }
}
