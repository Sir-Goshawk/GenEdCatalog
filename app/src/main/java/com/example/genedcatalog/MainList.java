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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainList extends AppCompatActivity {

    private static final String TAG = "MainList";

    private LinearLayout courseLists;

    private JSONObject toReturn;

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

        //Main title view
        TextView mainTitleHolder = findViewById(R.id.MainTitle);

        //The vertical linear layout that will hold course chunks
        courseLists = findViewById(R.id.CourseChunks);

        //The actual chunk that will be filled with course information and added to the course list
        View courseChunk = getLayoutInflater().inflate(R.layout.chunk_course, courseLists, false);

        //USED TO MAKE SURE UI WORKED: Testing to see if a course chunk will add to the list
//         requestAPI("http://courses.illinois.edu/cisapp/explorer/schedule/2020.xml");
//         requestAPI("http://courses.illinois.edu/cisapi/gened.xml");
        RequestQueue queue = Volley.newRequestQueue(this);

        //Request a string response from the provided URL
//        String url = "http://courses.illinois.edu/cisapp/explorer/schedule/2012/spring/CS/125.xml";
        String url = "https://courses.illinois.edu/cisapi/term/2011.xml";
//        String url = "http://courses.illinois.edu/cisapi/catalog/2012/spring?mode=summary";

        ArrayList<String> subject = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject fall2020 = XML.toJSONObject(response);
//                Log.d("mine", ""+fall2020.getJSONObject("ns2:course").getJSONObject("sections").getJSONArray("section").getJSONObject(1).get("href"));
            } catch (JSONException e) {
                Log.d("mine", url + e);
            }
        }, error -> {
            int  statusCode = error.networkResponse.statusCode;
            Log.d("mine", url + " " + error + "code " + statusCode);});
        queue.add(stringRequest);
//        requestAPI(url, new JSONObject());
    }

    //FOLLOWING TWO FUNCTIONS TESTED TO MAKE SURE UI WORKED (kinda...)
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

    private String removeBackSlash(String toEdit) {
        for (int i = 0; i < toEdit.length(); i++) {
            if (toEdit.charAt(i) == '\\') {
                toEdit = toEdit.substring(0, i) + toEdit.substring(i + 1);
            }
        }
//        Log.d("mine", "remove is"  +toEdit);
        return toEdit;
    }

    private void requestAPI(final String url, final JSONObject lastResponse) {
        if (lastResponse == null) {
            return;
        }
        try {
            if (lastResponse.get("ns2:course") != null) {
                courseJSONObject(lastResponse);
            }
        } catch (JSONException e) {
            Log.d("mine", "error " + e);
        }
//        RequestFuture<String> test = RequestFuture.newFuture();
        //Instantiate the RequestQueue--gets the Course Explorer API
        RequestQueue queue = Volley.newRequestQueue(this);

        //Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject fall2020 = XML.toJSONObject(response);
            Log.d("mine", "resonse " + fall2020.toString(2));
            } catch (JSONException e) {
                Log.d("mine", "error " + e);
            }
        }, error -> { });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        Log.d("mine", "returned " +toReturn);
        return;
    }

    private void courseJSONObject(final JSONObject courseJSON) {
        Log.d("mine", "called " + courseJSON);
    }
}
