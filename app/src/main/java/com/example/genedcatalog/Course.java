package com.example.genedcatalog;

import java.util.ArrayList;

/**
 * This is a Java Object that will store all the information of a course
 */
public class Course {

    private String name;
    private String code;
    private String genedinfo;
    private String description;
    private int credit;

    private ArrayList<String> courseSections;

    public Course(String courseName, String courseCode, String courseGenEdInfo, String courseDescription, int courseCredit, ArrayList<String> courseSec) {
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
    public String getCode() {
        return code;
    }
    public String getGenEdInfo() {
        return genedinfo;
    }
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
        return "courseName: " + name + "; courseCode: " + code + "; genEd: " + genedinfo + "; description: " + description + "; credit: " + credit + "; sections: " + courseSections.toString();
    }
}
