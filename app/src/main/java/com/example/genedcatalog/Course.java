package com.example.genedcatalog;

import java.util.ArrayList;

/**
 * This is a Java Object that will store all the information of a course
 */
public class Course {

    private String name;
    private int code;
    private String genedinfo;
    private String description;
    private int credit;

    private ArrayList<String> courseSections;

    public Course(String courseName, int courseCode, String courseGenEdInfo, String courseDescription, int courseCredit, ArrayList<String> courseSec) {
        name = courseName;
        code = courseCode;
        genedinfo = courseGenEdInfo;
        description = courseDescription;
        credit = courseCredit;
        courseSections = courseSec;
    }

    public String getName() {
        return name;
    }
    public int getCode() {
        return code;
    }
    public String getGenEdInfo() {
        return genedinfo;
    }
    public String getDescription() {
        return description;
    }
    public int getCredit() {
        return credit;
    }
    public ArrayList<String> getCourseSection() {
        return courseSections;
    }
}
