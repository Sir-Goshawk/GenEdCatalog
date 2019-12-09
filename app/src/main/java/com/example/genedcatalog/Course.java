package com.example.genedcatalog;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
/**
 * This is a Java Object that will store all the information of a course
 */
public class Course extends AppCompatActivity {

    private String name;
    private String code;
    private ArrayList<String> genedinfo;
    private String description;
    private int credit;
    private ArrayList<String> courseSections;
    private ArrayList<String> genEdNames;

    public Course(String courseName, String courseCode,
                  ArrayList<String> courseGenEdInfo, String courseDescription,
                  int courseCredit, ArrayList<String> courseSec,
                  ArrayList<String> courseGenEdNames) {
        name = courseName;
        code = courseCode;
        genedinfo = courseGenEdInfo;
        description = courseDescription;
        credit = courseCredit;
        courseSections = courseSec;
        genEdNames = courseGenEdNames;
    }

    public String getName() {
        return name;
    }
    public String getCode() {
        return code;
    }
    public ArrayList<String> getGenEdInfo() { return genedinfo;}
    public String getDescription() {
        return description;
    }
    public String getCredit() {
        return ""+credit;
    }
    public ArrayList<String> getCourseSection() {
        return courseSections;
    }
    public String toString() {
        return "courseName: " + name + "; courseCode: " + code + "; genEd: " + genedinfo + "; description: " + description + "; credit: " + credit + "; sections: " + courseSections.toString()
                + "; genEd" + genEdNames;
    }
    public ArrayList<String> getGenEdNames() {
        return genEdNames;
    }
}
