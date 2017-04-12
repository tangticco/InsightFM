package edu.fandm.ztang.insightfm.Models;

import android.content.Context;
import android.content.res.AssetManager;
import android.provider.MediaStore;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by zhuofantang on 4/5/17.
 *
 * This model is to used to parse information
 */

public class InsightDatabaseModel {

    ///////////////////////
    ///FM Related Classes
    //////////////////////

    public static class FMResource{
        String classType;
        String resourceTitle;

        public void setClassType(String classType) {
            this.classType = classType;
        }

        public void setResourceTitle(String resourceTitle) {
            this.resourceTitle = resourceTitle;
        }

        public String getClassType() {
            return classType;
        }

        public String getResourceTitle() {
            return resourceTitle;
        }
    }

    /**
     * A class that implement the information structure of a building
     */
    public static class Building extends FMResource{
        //Building class attributes
        private String buildingName;
        private String buildingDescription;
        private LatLng buildingLocation;

        //Building class values
        private ArrayList<Floor> floors;
        private ArrayList<Department> departments;


        public Building(String BUILDINGNAME){
            buildingName = BUILDINGNAME;
            floors = new ArrayList<>();
            departments = new ArrayList<>();
            this.setClassType("Building");
            this.setResourceTitle(buildingName);
        }

        ////////////////////////////////////
        //Getter methods
        ////////////////////////////////////
        public String getBuildingName() {
            return buildingName;
        }

        public ArrayList<Floor> getFloors() {
            return floors;
        }

        public LatLng getBuildingLocation() {
            return buildingLocation;
        }

        public String getBuildingDescription() {
            return buildingDescription;
        }

        public ArrayList<Department> getDepartments() {
            return departments;
        }

        ////////////////////////////////////
        //Setter methods
        ////////////////////////////////////
        public void addFloor(Floor newFloor){
            floors.add(newFloor);
        }

        public void addDepartment(Department newDeparment){
            departments.add(newDeparment);
        }

        public void setBuildingLocation(LatLng buildingLocation) {
            this.buildingLocation = buildingLocation;
        }

        public void setBuildingDescription(String buildingDescription) {
            this.buildingDescription = buildingDescription;
        }
    }

    /**
     * A class that implement the information structure of a floor
     */
    public  static class Floor extends FMResource{

        //reference to the building
        private Building floorBuilding;

        //Floor's own properties
        private ArrayList<Classroom> classrooms;
        private ArrayList<Office> offices;
        private String floorName;


        public Floor(String FLOORNAME){
            floorName = FLOORNAME;
            classrooms = new ArrayList<>();
            offices = new ArrayList<>();
            this.setClassType("Floor");
            this.setResourceTitle(getFloorBuilding().getBuildingName() + floorName);
        }

        public String getFloorName() {
            return floorName;
        }

        public Building getFloorBuilding() {
            return floorBuilding;
        }

        public ArrayList<Classroom> getClassrooms() {
            return classrooms;
        }

        public ArrayList<Office> getOffices() {
            return offices;
        }

        public void addClassroom(Classroom newClassroom){
            classrooms.add(newClassroom);
        }

        public void addOffice(Office newOffice){
            offices.add(newOffice);
        }

        public void addBuilding(Building refBuilding){
            floorBuilding = refBuilding;
        }


        //TODO add more getter methods
    }

    /**
     * A class that implement the information structure of a classroom
     */
    public  static class Classroom extends FMResource{

        //Reference to the Building and Floor
        private Building classRoomBuilding;
        private Floor classRoomFloor;

        ArrayList<Course> courses;
        int roomNumber;


        public Classroom(int ROOMNUMBER){
            roomNumber = ROOMNUMBER;
            courses = new ArrayList<>();
            setClassType("Classroom");
            this.setResourceTitle(String.valueOf(roomNumber));
        }



    }

    /**
     * A class that implement the information structure of a office
     */
    public  static class Office extends FMResource{

        //Reference to the office
        private Building officeBuilding;
        private Floor officeFloor;

        //Office class attributes
        int roomNumber;
        Instructor officeInstructor;

        public Office(int ROOMNUMBER){

            //set office class attributes
            roomNumber = ROOMNUMBER;

            //set super-class attributes
            setClassType("Office");
            setResourceTitle(String.valueOf(roomNumber));
        }

        ////////////////////////////////////
        //Getter methods
        ////////////////////////////////////
        public Building getOfficeBuilding() {
            return officeBuilding;
        }

        public Floor getOfficeFloor() {
            return officeFloor;
        }

        public Instructor getOfficeInstructor() {
            return officeInstructor;
        }

        public int getRoomNumber() {
            return roomNumber;
        }

        ////////////////////////////////////
        //Setter methods
        ////////////////////////////////////
        public void setOfficeBuilding(Building officeBuilding) {
            this.officeBuilding = officeBuilding;
        }

        public void setOfficeFloor(Floor officeFloor) {
            this.officeFloor = officeFloor;
        }

        public void setOfficeInstructor(Instructor officeInstructor) {
            this.officeInstructor = officeInstructor;
        }
    }


    /**
     * A class that implement the information structure of a department
     */
    public static class Department extends FMResource{

        //Department references
        private Building departmentBuilding;

        //Department attributes
        private String departmentName;
        private String departmentDescription;

        //Department Values
        private ArrayList<Course> courseOffering;
        private ArrayList<Instructor> departmentInstructors;


        public Department(String departname){

            //set department class attributes
            this.departmentName = departname;

            //initialize department class values
            courseOffering = new ArrayList<>();
            departmentInstructors = new ArrayList<>();

            //set super class attributes
            this.setClassType("Department");
            setResourceTitle(departmentName);
        }

        ////////////////////////////////////
        //Getter methods
        ////////////////////////////////////
        public String getDepartmentName() {
            return departmentName;
        }

        public String getDepartmentDescription() {
            return departmentDescription;
        }

        public Building getDepartmentBuilding() {
            return departmentBuilding;
        }

        public ArrayList<Course> getCourseOffering() {
            return courseOffering;
        }

        public ArrayList<Instructor> getDepartmentInstructors() {
            return departmentInstructors;
        }

        ////////////////////////////////////
        //Setter methods
        ////////////////////////////////////
        public void setDepartmentBuilding(Building departmentBuilding) {
            this.departmentBuilding = departmentBuilding;
        }

        public void setDepartmentDescription(String departmentDescription) {
            this.departmentDescription = departmentDescription;
        }

        public void addCourse(Course newCourse){
            courseOffering.add(newCourse);
        }

        public void addInstructor(Instructor newInstructor){
            departmentInstructors.add(newInstructor);
        }
    }


    /**
     * Implement a class Course to store information of a course
     */
    public  static class Course extends FMResource {

        //Course Reference
        private Department depart;

        //Course Attributes
        private String subj;
        private int courseNum;
        private String title;
        private String courseDescription;

        //Course values
        private ArrayList<Session> sessions;

        public Course(String Subj, int courseNum, String Title, Department departName){

            //set class attributes
            subj = Subj;
            depart = departName;
            this.courseNum = courseNum;
            title = Title;

            //Initialize course class values
            sessions = new ArrayList<>();

            //set super class attributes
            setClassType("Course");
            this.setResourceTitle(subj + " " + courseNum);
        }

        ////////////////////////////////////
        //Getter methods
        ////////////////////////////////////
        public String getSubj() {
            return subj;
        }

        public int getCourseNum() {
            return courseNum;
        }

        public String getTitle() {
            return title;
        }

        public String getCourseDescription() {
            return courseDescription;
        }

        public Department getDepart() {
            return depart;
        }

        public ArrayList<Session> getSessions() {
            return sessions;
        }

        ////////////////////////////////////
        //Setter methods
        ////////////////////////////////////
        public void setSubj(String subj) {
            this.subj = subj;
        }

        public void setCourseNum(int courseNum) {
            this.courseNum = courseNum;
        }

        public void setTitle(String Title){
            title = Title;
        }

        public void setCourseDescription(String description){
            courseDescription = description;
        }

        public void addSession(Session newSession){
            sessions.add(newSession);
        }



    }

    /**
     * A Class that implement the information structure of a session
     */
    public  static class Session extends FMResource{

        //Session Reference
        private Building building;
        private Course sessionCourse;

        //Session Attributes
        private int crn;
        private String room;
        private String days;
        private String begin;
        private String end;
        private Instructor instructor;

        public Session(int CRN, Building Building, String Room, String Days, String Begin, String End, Instructor Instructor, Course sessionCourse){

            //set session class attributes
            crn = CRN;
            building = Building;
            room = Room;
            days = Days;
            begin = Begin;
            end = End;
            instructor = Instructor;
            this.sessionCourse = sessionCourse;

            //Set super class attributes
            setClassType("Session");
            setResourceTitle(String.valueOf(crn));
        }


        ////////////////////////////////////
        //Setter methods
        ////////////////////////////////////
        public void setCrn(int CRN){
            crn = CRN;
        }

        public void setBuilding(Building Building){
            building = Building;
        }

        public void setRoom(String Room){
            room = Room;
        }

        public void setDays(String Days){
            days = Days;
        }

        public void setBegin(String Begin){
            begin = Begin;
        }

        public void setEnd(String End){
            end = End;
        }

        public void setInstructor(String Instructor){

            instructor = new Instructor(Instructor);
        }

        public void setSessionCourse(Course sessionCourse) {
            this.sessionCourse = sessionCourse;
        }

        ////////////////////////////////////
        //Getter methods
        ////////////////////////////////////
        public int getCrn() {
            return crn;
        }

        public Building getBuilding() {
            return building;
        }

        public String getRoom() {
            return room;
        }

        public String getDays() {
            return days;
        }

        public String getBegin() {
            return begin;
        }

        public String getEnd() {
            return end;
        }

        public Instructor getInstructor() {
            return instructor;
        }

        public Course getSessionCourse() {
            return sessionCourse;
        }
    }

    /**
     * Implement a class Instructor to store information of a instructor
     */
    public  static class Instructor extends FMResource{

        //Instructor reference
        private Office office;

        //Instructor attributes
        private String fullName;
        private String emailAddress;
        private String instructorDescription;

        //Instructor values
        private ArrayList<Course> teachingCourses;

        public Instructor(String FULLNAME){

            //set instructor class attributes
            fullName = FULLNAME;

            //initialize instructor class values
            teachingCourses = new ArrayList<>();

            //set super class attributes
            setClassType("Instructor");
            this.setResourceTitle(fullName);
        }



        ////////////////////////////////////
        //Getter methods
        ////////////////////////////////////
        public String getEmailAddress() {
            return emailAddress;
        }

        public String getFullName() {
            return fullName;
        }

        public String getInstructorDescription() {
            return instructorDescription;
        }

        public Office getOffice() {
            return office;
        }

        public ArrayList<Course> getTeachingCourses() {
            return teachingCourses;
        }

        ////////////////////////////////////
        //Setter methods
        ////////////////////////////////////
        public void setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public void setInstructorDescription(String instructorDescription) {
            this.instructorDescription = instructorDescription;
        }

        public void setOffice(Office office) {
            this.office = office;
        }

        public void addTeachingOffice(Course newCourse){
            teachingCourses.add(newCourse);
        }


    }

}
