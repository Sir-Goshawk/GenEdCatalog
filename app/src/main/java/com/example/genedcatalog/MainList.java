package com.example.genedcatalog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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

public class MainList extends AppCompatActivity {

    //LinearLayout that populates the courses to display
    private LinearLayout courseLists;

    //This app now only display
    private String urlBASEstart = "http://courses.illinois.edu/cisapp/explorer/schedule/2020/spring/";

    private String urlBASEend = ".xml";

    private ArrayList<Course> coursesToAdd = new ArrayList<>();

    /**
     * What shows up when the app first opens
     * @param savedInstanceState ?
     */
    protected void onCreate(final Bundle savedInstanceState) {
//        Log.d("MainList mine", "creating");
        //The "super" call is required for all activities
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_list);
        //The vertical linear layout that will hold course chunks
        courseLists = findViewById(R.id.CourseChunks);

        //Spinner that selects which subject to display from
        Spinner subjectChoices = findViewById(R.id.SubjectChoice);
        subjectChoices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(final AdapterView<?> parent, final View view,
                                       final int position, final long id) {
                //Once a new subject is selected, it clears the current subject courses from view and empties the courseToAdd ArrayList
                courseLists.removeAllViews();
                coursesToAdd.clear();
                //App then sends an API reqeust to server, with the given url
                requestAPI(urlBASEstart + getResources().getStringArray(R.array.CourseSubjectCode)[position] + urlBASEend);
            }
            public void onNothingSelected(final AdapterView<?> parent) {
            }
        });
//        Log.d("mine added course end", ""+coursesToAdd.size());

        //Spinner that selects which particular genEd does the user want to see
        Spinner genEdChoices = findViewById(R.id.FilterChoice);
        genEdChoices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(final AdapterView<?> parent, final View view,
                                       final int position, final long id) {
                //if the position is zero, or the "All" filter is selected, then there's no need to filter
                if (position == 0) {
                    //It sets all the courses as visible
                    for (int i = 0; i < courseLists.getChildCount(); i++) {
                        courseLists.getChildAt(i).setVisibility(View.VISIBLE);
                    }
                } else  {
                    //Else it filters the courses
                    filterGenEds(getResources().getStringArray(R.array.CourseTypes)[position - 1]);
                }
            }
            public void onNothingSelected(final AdapterView<?> parent) {
            }
        });
    }

    /**
     * Function to be used to populate course chunks.
     * @param courseToAdd the Course object to get the information from
     */
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
        //courseGenEdinfoHolder will be given an ArrayList, so it needs to be formatted
        String genEdInfo = "";
        for (int i = 0; i < courseToAdd.getGenEdInfo().size(); i++) {
            if (courseToAdd.getGenEdInfo().get(i).charAt(0) != '1') {
                genEdInfo += courseToAdd.getGenEdInfo().get(i) + "\n";
            }
        }
        courseGenEdinfoHolder.setText(genEdInfo);
        courseDescriptionHolder.setText(courseToAdd.getDescription());
        courseCreditHolder.setText(courseToAdd.getCredit());

        //If the selection button is pressed, then it will jump to the course page
        courseButton.setOnClickListener(unused -> {
            goToCoursePage(courseToAdd);
        });

        //The chunk is added to the courseList
        courseLists.addView(courseChunk);
    }

    /**
     * Transfers info to course page, adds infomation about the Course with intent.
     * @param toAdd the Course object to sent to CoursePage
     */
    private void goToCoursePage(final Course toAdd) {
        Intent intent = new Intent(this, CoursePage.class);
//        Log.d("mine coursePage", toAdd.toString());
        intent.putExtra("courseName", toAdd.getName());
        intent.putExtra("courseCredit", toAdd.getCredit());
        intent.putExtra("courseCode", toAdd.getCode());
        intent.putExtra("courseGenEdInfo", toAdd.getGenEdInfo());
        intent.putExtra("courseDescription", toAdd.getDescription());
        intent.putExtra("courseSection", toAdd.getCourseSection());
        intent.putExtra("courseGenEdNames", toAdd.getGenEdNames());
        startActivity(intent);
    }

    /**
     * This function filters which courses to display via genEd attribute.
     * @param chosenGenEd the genEd to show
     */
    private void filterGenEds(final String chosenGenEd) {
        //This loops through the courses displayed on screen
        for (int i = 0; i < coursesToAdd.size(); i++) {
            //if the course at courseToAdd(same position with courseList) has the said genEd attribute, then it sets the course to visible
            if (coursesToAdd.get(i).getGenEdInfo().contains(chosenGenEd)) {
                courseLists.getChildAt(i).setVisibility(View.VISIBLE);
            } else {
                //else it sets to gone, since it doesn't have the genEd attribute
                courseLists.getChildAt(i).setVisibility(View.GONE);
            }
            Log.d("mine fikter" , coursesToAdd.get(i).getGenEdInfo().toString());
        }

    }

    /**
     * This function calls to request information from server, it does this recrusively
     * @param url the url of the said info
     */
    private void requestAPI(final String url) {
        //If the url is null or empty, then throw a error since it's asking for nothing
        if (url == null || url.length() == 0) {
            throw new IllegalArgumentException();
        }
        //Instantiate the RequestQueue--gets the Course Explorer API
        RequestQueue queue = Volley.newRequestQueue(this);

        //starts a string request
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            Log.d("mine requested", url);
            //Everything is in a try-catch block because it may run into a JSONException
            try {
                //This converts the given String response, in a XML format, into a JSONObject
                JSONObject requestedJSONObj = XML.toJSONObject(response);

                //first JSON node of the given requestedJSONObj
                Iterator<String> keys = requestedJSONObj.keys();
                String firstNode = keys.next();
                //The subJSONObject that is nested within the requestedJSONObj
                requestedJSONObj = requestedJSONObj.getJSONObject(firstNode);
//                Log.d("mine", url);

                if (firstNode.equals("ns2:term")) {
                    //if it's a termXML, the first type of XML to request from server
//                    try {
                        //If there are many courses in the subject, the JSON element "subjects" will contain a JSONArray called "subject"
                        JSONArray subjectJSON = requestedJSONObj.getJSONObject("subjects").getJSONArray("subject");
                        //Now it lkoops through the array and get every subject url within
                        for (int i = 0; i < subjectJSON.length(); i++) {
                            requestAPI(subjectJSON.getJSONObject(i).getString("href"));
                        }
//                    } catch (JSONException e) {}
                } else if (firstNode.equals("ns2:subject")) {
                    //check if it's subjectXML, the second type of XML to request from server
                    try {
                        //If there's many courses within the subject, the JSON element "courses" will contain a JSONArray called "course"
                        JSONArray courseJSONArray = requestedJSONObj.getJSONObject("courses").getJSONArray("course");
                        //Now it lkoops through the array and get every course url within
                        for (int i = 0; i < courseJSONArray.length(); i++) {
                            requestAPI(courseJSONArray.getJSONObject(i).getString("href"));
                        }
                    } catch (JSONException e) {
                        //If there's only one course then the API only gets one url
                        requestAPI(requestedJSONObj.getJSONObject("courses").getJSONObject("course").getString("href"));
                    }
                } else if (firstNode.equals("ns2:course")) {
                    //check base case, when it's a courseXML, add the genEd Attributes to the genedCourses List
                    try {
                        //If the course has "genEdCategories", if it does not, then there's no need to request further infomation
                        //"section" ArrayList contains all the sections within the course
                        ArrayList<String> sections = new ArrayList<>();
                        getValues(requestedJSONObj, "sections", "sections", "section", sections);
//                        try {
//                            //If there's many sections within the course, the JSON element "sections" will contain a JSONArray called "section"
//                            for (int i = 0; i < requestedJSONObj.getJSONObject("sections").getJSONArray("section").length(); i++) {
//                                //Now it loops through the array and get every section url within
//                                sections.add(requestedJSONObj.getJSONObject("sections").getJSONArray("section").getJSONObject(i).getString("href"));
//                            }
//                        } catch (JSONException e) {
//                            //If there's only one section then the API only gets one url
//                            sections.add(requestedJSONObj.getJSONObject("sections").getJSONObject("section").getString("href"));
//                        }

                        //"genEd" ArrayList will contain all the short-hand code from genEds, it will be used for filtering
                        ArrayList<String> genEd = new ArrayList<>();
                        //"genEdNames" ArrayList will contain all the names from genEds, it will be used for display
                        ArrayList<String> genEdNames = new ArrayList<>();
                        try {
                            //If there's many genEd attributes within the course, the JSON element "genEdCategories" will contain a JSONArray called "category"
                            for (int i = 0; i < requestedJSONObj.getJSONObject("genEdCategories").getJSONArray("category").length(); i++) {
                                //Now it loops through the array and add every genEd attribute to "genEd"
                                genEd.add(requestedJSONObj.getJSONObject("genEdCategories").getJSONArray("category").getJSONObject(i).getString("id"));
                                genEd.add(requestedJSONObj.getJSONObject("genEdCategories").getJSONArray("category").getJSONObject(i).getJSONObject("ns2:genEdAttributes").getJSONObject("genEdAttribute").getString("code"));
                                //Now it loops through the array and add every genEdNames attribute to "genEdNames" within a JSONObject "genEdAttributes"
                                genEdNames.add(requestedJSONObj.getJSONObject("genEdCategories").getJSONArray("category").getJSONObject(i).getJSONObject("ns2:genEdAttributes").getJSONObject("genEdAttribute").getString("content"));
                            }
                        } catch (JSONException e) {
                            //If there's only one genEd Attribute then "genEd" and "genEdNames" only adds one object
                            genEd.add(requestedJSONObj.getJSONObject("genEdCategories").getJSONObject("category").getString("id"));
                            genEdNames.add(requestedJSONObj.getJSONObject("genEdCategories").getJSONObject("category").getJSONObject("ns2:genEdAttributes").getJSONObject("genEdAttribute").getString("content"));
                            genEd.add(requestedJSONObj.getJSONObject("genEdCategories").getJSONObject("category").getJSONObject("ns2:genEdAttributes").getJSONObject("genEdAttribute").getString("code"));
                        }

                        //Creates a new Course Object called "toAdd", with the courseName: requestedJSONObj.getString("label"); courseCode: requestedJSONObj.getString("id";
                        //                                                                  courseGenEdInfo: genEd; courseDescription: requestedJSONObj.getString("description");
                        //                                                                  courseCredit: requestedJSONObj.getString("creditHours"); courseSec: sections; courseGenEdNames: genEdName
                        Course toAdd = new Course(requestedJSONObj.getString("label"),
                                requestedJSONObj.getString("id"),
                                genEd,
                                requestedJSONObj.getString("description"),
                                requestedJSONObj.getString("creditHours").substring(0, 1),
                                sections,
                                genEdNames
                        );
                        //This addes a course chunk to display via addChunkCourse
                        addChunkCourse(toAdd);
                        //This adds a course to the courseToAdd
                        coursesToAdd.add(toAdd);

                        //If the subject has no courses with genEds, or the server hasn't publish the information yet, it will display a textview and ask the user to change a selection for subject
                        TextView errorMessage = new TextView(this);
                        errorMessage.setText("Looks like there aren't any published courses that fulfills GenEd Requirements\nplease try another subject");
                        errorMessage.setGravity(Gravity.CENTER);
                        if (courseLists.getChildCount() == 0) {
                            //If the courseList isn't populated, then the message displays
                            courseLists.addView(errorMessage);
                        } else {
                            //If there are elements, then the message goes away
                            courseLists.removeView(errorMessage);
                        }
                    } catch (JSONException e) {}
                }
            } catch (JSONException e) {
                Log.d("mine", "error is " + e);
            }
        }, error -> { Log.d("mine", "error web " + error); });

        //Sends a request to server
        queue.add(stringRequest);
    }

    private void getValues(final JSONObject requestedJSONObj, final String type, final String firstName, final String secondName, final ArrayList given) {
        try {
            try {
                //If there's many sections within the course, the JSON element "sections" will contain a JSONArray called "section"
                for (int i = 0; i < requestedJSONObj.getJSONObject(firstName).getJSONArray(secondName).length(); i++) {
                    //Now it loops through the array and get every section url within
                    given.add(requestedJSONObj.getJSONObject(firstName).getJSONArray(secondName).getJSONObject(i).getString("href"));
                }
            } catch (JSONException e) {
                //If there's only one section then the API only gets one url
                given.add(requestedJSONObj.getJSONObject(firstName).getJSONObject(secondName).getString("href"));
            }
        } catch (JSONException e) { }
    }
}
