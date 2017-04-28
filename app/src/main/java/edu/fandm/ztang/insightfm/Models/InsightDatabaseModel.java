package edu.fandm.ztang.insightfm.Models;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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




    ////////////////////////////////////
    ///FM Related Classes
    ////////////////////////////////////

    /**
     * This is a super class for FM resources like Office, Instructors, etc.
     */
    public static class FMResource{

        //Super-Variables pre-defined for all sub-classes
        String classType;
        String resourceTitle ;
        LatLng recourseLocation;
        int infoID;


        public FMResource(){
            classType = "FMResource";
            resourceTitle = "FMResource";
            recourseLocation = new LatLng(40.046096674063996,-76.31949618458748);   //default location of the resource which is the location of stager hall
            infoID = -1;    //default infoID for non-end entries
        }


        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Setter methods
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////

        /**
         * Set the class type of specific child classes
         * @param classType
         */
        public void setClassType(String classType) {
            this.classType = classType;
        }

        /**
         * Set the resource title for specific child class. This title should be a understandable title for that specific resource
         * @param resourceTitle
         */
        public void setResourceTitle(String resourceTitle) {
            this.resourceTitle = resourceTitle;
        }

        /**
         * Set the reource location for that specific resource
         * @param recourseLocation
         */
        public void setRecourseLocation(LatLng recourseLocation) {
            this.recourseLocation = recourseLocation;
        }

        /**
         * Set the infoID (index in the storage list) for that specific resource
         * Default is -1 (for non-end entry, non-existent entry)
         * @param infoID
         */
        public void setInfoID(int infoID) {
            this.infoID = infoID;
        }



        ////////////////////////////////////
        //Getter methods
        ////////////////////////////////////

        /**
         * Get the class type of this resource
         * @return class type in string
         */
        public String getClassType() {
            return classType;
        }

        /**
         * Get the resource title of that resource
         * @return resource title
         */
        public String getResourceTitle() {
            return resourceTitle;
        }

        /**
         * Return the location of the resource
         * @return location of the resource
         */
        public LatLng getRecourseLocation() {
            return recourseLocation;
        }

        /**
         * Return the infoID (index of storage list) of that specific resource
         * @return
         */
        public int getInfoID() {
            return infoID;
        }


    }

    /**
     * A class that implement the information structure of a building
     */
    public static class Building extends FMResource{


        //Building class attributes
        private String buildingName;
        private String buildingDescription;

        //Building class values
        private ArrayList<Floor> floors;
        private ArrayList<Department> departments;


        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Default constructor
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////

        public Building(){
            super();
            floors = new ArrayList<>();
            departments = new ArrayList<>();
            this.setClassType("Building");
        }


        public Building(String BUILDINGNAME){
            super();
            buildingName = BUILDINGNAME;
            floors = new ArrayList<>();
            departments = new ArrayList<>();
            this.setClassType("Building");
            this.setResourceTitle(buildingName);
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Getter methods
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        public String getBuildingName() {
            return buildingName;
        }

        public String getBuildingDescription() {
            return buildingDescription;
        }

        public ArrayList<Floor> getFloors() {
            return floors;
        }

        public ArrayList<Department> getDepartments() {
            return departments;
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Setter methods
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        public void setFloors(ArrayList<Floor> floors) {
            this.floors = floors;
        }

        public void setDepartments(ArrayList<Department> departments) {
            this.departments = departments;
        }

        public void addFloor(Floor newFloor){
            floors.add(newFloor);
        }

        public void addDepartment(Department newDeparment){
            departments.add(newDeparment);
        }

        public void setBuildingName(String buildingName) {
            this.buildingName = buildingName;
            this.setResourceTitle(buildingName);
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


        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////Default Constructors
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        public Floor(){
            super();
            classrooms = new ArrayList<>();
            offices = new ArrayList<>();
            this.setClassType("Floor");
        }


        public Floor(String FLOORNAME){
            super();
            floorName = FLOORNAME;
            classrooms = new ArrayList<>();
            offices = new ArrayList<>();
            this.setClassType("Floor");
            this.setResourceTitle(getFloorBuilding().getBuildingName() + floorName);
        }


        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////Getter Methods
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////Setter Methods
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        public void addClassroom(Classroom newClassroom){
            classrooms.add(newClassroom);
        }

        public void addOffice(Office newOffice){
            offices.add(newOffice);
        }

        public void addBuilding(Building refBuilding){
            floorBuilding = refBuilding;
        }

        public void setFloorBuilding(Building floorBuilding) {
            this.floorBuilding = floorBuilding;
            this.setResourceTitle(getFloorBuilding().getBuildingName() + floorName);
        }

        public void setFloorName(String floorName) {
            this.floorName = floorName;
            this.setResourceTitle(getFloorBuilding().getBuildingName() + floorName);
        }

        public void setClassrooms(ArrayList<Classroom> classrooms) {
            this.classrooms = classrooms;
        }

        public void setOffices(ArrayList<Office> offices) {
            this.offices = offices;
        }
    }

    /**
     * A class that implement the information structure of a classroom
     */
    public  static class Classroom extends FMResource{

        //Reference to the Building and Floor
        private Building classRoomBuilding;
        private Floor classRoomFloor;

        ArrayList<Course> courses;
        String roomNumber;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /////Default Constructors
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        public Classroom(){
            super();
            courses = new ArrayList<>();
            setClassType("Classroom");
        }

        public Classroom(String ROOMNUMBER){
            super();
            roomNumber = ROOMNUMBER;
            courses = new ArrayList<>();
            setClassType("Classroom");
            this.setResourceTitle(String.valueOf(roomNumber));
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /////Getter Methods
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        public Building getClassRoomBuilding() {
            return classRoomBuilding;
        }

        public Floor getClassRoomFloor() {
            return classRoomFloor;
        }

        public ArrayList<Course> getCourses() {
            return courses;
        }

        public String getRoomNumber() {
            return roomNumber;
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /////Setter Methods
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


        public void setClassRoomBuilding(Building classRoomBuilding) {
            this.classRoomBuilding = classRoomBuilding;
        }

        public void setClassRoomFloor(Floor classRoomFloor) {
            this.classRoomFloor = classRoomFloor;
        }

        public void setCourses(ArrayList<Course> courses) {
            this.courses = courses;
        }

        public void addCourse(Course newCourse){
            this.courses.add(newCourse);
        }

        public void setRoomNumber(String roomNumber) {
            this.roomNumber = roomNumber;
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
        String roomNumber;
        Instructor officeInstructor;

        public Office(){
            super();
        }

        public Office(String ROOMNUMBER){
            super();
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

        public String getRoomNumber() {
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

        public void setRoomNumber(String roomNumber) {
            this.roomNumber = roomNumber;
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
        private Building belongedBuilding;

        //Course Attributes
        private String subj;
        private int courseNum;
        private String title;
        private String courseDescription;

        //Course values
        private ArrayList<Session> sessions;
        private ArrayList<Sellingitem> sellingitems;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /////Default Constructors
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        public Course(){
            super();

            //Initialize course class values
            sessions = new ArrayList<>();

            //set super class attributes
            setClassType("Course");
        }

        public Course(String Subj, int courseNum, String Title, Department departName, Building belongedBuilding){
            super();

            //set class attributes
            this.subj = Subj;
            this.courseNum = courseNum;
            this.title = Title;
            this.depart = departName;
            this.belongedBuilding = belongedBuilding;

            //Initialize course class values
            sessions = new ArrayList<>();
            sellingitems = new ArrayList<>();

            //set super class attributes
            setClassType("Course");
            this.setResourceTitle(subj + " " + courseNum);
        }

        ////////////////////////////////////
        //Getter methods
        ////////////////////////////////////
        public Department getDepart() {
            return depart;
        }

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

        public ArrayList<Session> getSessions() {
            return sessions;
        }

        public ArrayList<Sellingitem> getSellingitems() {
            return sellingitems;
        }

        public Building getBelongedBuilding() {
            return belongedBuilding;
        }

        ////////////////////////////////////
        //Setter methods
        ////////////////////////////////////

        public void setDepart(Department depart) {
            this.depart = depart;
        }

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

        public void setSessions(ArrayList<Session> sessions) {
            this.sessions = sessions;
        }

        public void addSession(Session newSession){
            sessions.add(newSession);
        }

        public void setSellingitems(ArrayList<Sellingitem> sellingitems) {
            this.sellingitems = sellingitems;
        }

        public void addSellingitem(Sellingitem newItem){
            sellingitems.add(newItem);
        }

        public void setBelongedBuilding(Building belongedBuilding) {
            this.belongedBuilding = belongedBuilding;
        }
    }

    /**
     * A Class that implement the information structure of a session
     */
    public static class Session extends FMResource{

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

        public Session(){
            super();
            //Set super class attributes
            setClassType("Session");
        }


        public Session(int CRN, Building Building, String Room, String Days, String Begin, String End, Instructor Instructor, Course sessionCourse){
            super();

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


        ///////////////////////////////////////////////////////////
        //Getter methods
        ///////////////////////////////////////////////////////////
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


        ///////////////////////////////////////////////////////////
        //Setter methods
        ///////////////////////////////////////////////////////////
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
    }

    /**
     * Implement a class Instructor to store information of a instructor
     */
    public static class Instructor extends FMResource{

        //Instructor reference
        private Office office;

        //Instructor attributes
        private String fullName;
        private String emailAddress;
        private String phoneNumber;
        private String instructorDescription;

        //Instructor values
        private ArrayList<Course> teachingCourses;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /////Default Constructors
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////

        public Instructor(){
            super();

            //initialize instructor class values
            teachingCourses = new ArrayList<>();

            //set super class attributes
            setClassType("Instructor");
        }

        public Instructor(String FULLNAME){
            super();

            //set instructor class attributes
            fullName = FULLNAME;

            //initialize instructor class values
            teachingCourses = new ArrayList<>();

            //set super class attributes
            setClassType("Instructor");
            this.setResourceTitle(fullName);
        }



        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Getter methods
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        public Office getOffice() {
            return office;
        }

        public String getFullName() {
            return fullName;
        }

        public String getEmailAddress() {
            return emailAddress;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getInstructorDescription() {
            return instructorDescription;
        }

        public ArrayList<Course> getTeachingCourses() {
            return teachingCourses;
        }

        ////////////////////////////////////
        //Setter methods
        ////////////////////////////////////
        public void setOffice(Office office) {
            this.office = office;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public void setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public void setInstructorDescription(String instructorDescription) {
            this.instructorDescription = instructorDescription;
        }

        public void addTeachingOffice(Course newCourse){
            teachingCourses.add(newCourse);
        }

        public void setTeachingCourses(ArrayList<Course> teachingCourses) {
            this.teachingCourses = teachingCourses;
        }
    }



    ////////////////////////////////////
    ////Selling Related Class
    ////////////////////////////////////
    public static class Book{

        //Book attributes
        private String bookTitle;
        private String bookAuthor;
        private String ISBN;



        //Seller info and price
        private ArrayList<Sellingitem> onsaleItems;


        //Book Reference
        private int courseInfoID;
        private Course requiredCourse;
        private Instructor requiredInstructor;

        public Book(){

        }

        public Book(String bookTitle, int courseInfoID){
            this.bookTitle = bookTitle;
            this.courseInfoID = courseInfoID;
            bookAuthor = "";
            ISBN = "";

            onsaleItems = new ArrayList<>();
            requiredInstructor = null;
        }


        public ArrayList<Sellingitem> getOnsaleItems() {
            return onsaleItems;
        }

        public String getBookAuthor() {
            return bookAuthor;
        }

        public String getBookTitle() {
            return bookTitle;
        }

        public String getISBN() {
            return ISBN;
        }

        public int getCourseInfoID() {
            return courseInfoID;
        }

        public Instructor getRequiredInstructor() {
            return requiredInstructor;
        }

        public void setBookAuthor(String bookAuthor) {
            this.bookAuthor = bookAuthor;
        }

        public void setBookTitle(String bookTitle) {
            this.bookTitle = bookTitle;
        }

        public void setISBN(String ISBN) {
            this.ISBN = ISBN;
        }

        public void setOnsaleItems(ArrayList<Sellingitem> onsaleItems) {
            this.onsaleItems = onsaleItems;
        }

        public void setCourseInfoID(int courseInfoID) {
            this.courseInfoID = courseInfoID;
        }

        public void setRequiredInstructor(Instructor requiredInstructor) {
            this.requiredInstructor = requiredInstructor;
        }
    }

    public static class Sellingitem{

        //Item Attributes
        String userKey;
        Double sellingPrice;
        Double originalPrice;
        Integer itemCondition; // 1-5, new to very used
        String itemDescription;


        //Item reference
        Book belongedBook;

        ////////////////////////////////////
        ///Default Constructor
        ////////////////////////////////////

        public Sellingitem(){

        }

        public Sellingitem(String userKey, Double sellingPrice, Double originalPrice, Integer itemCondition, String itemDescription, Book belongedBook){
            this.userKey = userKey;
            this.sellingPrice = sellingPrice;
            this.originalPrice = originalPrice;
            this.itemCondition = itemCondition;
            this.itemDescription = itemDescription;
            this.belongedBook = belongedBook;
        }


        ////////////////////////////////////////////////////////////////////////
        /////Getter Methods
        ////////////////////////////////////////////////////////////////////////


        public String getUserKey() {
            return userKey;
        }

        public Double getSellingPrice() {
            return sellingPrice;
        }

        public Double getOriginalPrice() {
            return originalPrice;
        }

        public Integer getItemCondition() {
            return itemCondition;
        }

        public String getItemDescription() {
            return itemDescription;
        }

        public Book getBelongedBook() {
            return belongedBook;
        }


        ////////////////////////////////////////////////////////////////////////
        ////Setter Methods
        ////////////////////////////////////////////////////////////////////////


        public void setUserKey(String userKey) {
            this.userKey = userKey;
        }

        public void setSellingPrice(Double sellingPrice) {
            this.sellingPrice = sellingPrice;
        }

        public void setOriginalPrice(Double originalPrice) {
            this.originalPrice = originalPrice;
        }

        public void setItemCondition(Integer itemCondition) {
            this.itemCondition = itemCondition;
        }

        public void setItemDescription(String itemDescription) {
            this.itemDescription = itemDescription;
        }


        public void setBelongedBook(Book belongedBook) {
            this.belongedBook = belongedBook;
        }
    }

    ////////////////////////////////////
    ////User Related Class
    ////////////////////////////////////

    public static class User{

        //User Profile
        String userDisplayedName;
        String userEmail;
        String userPhotoUri;

        String userPhoneNumber;
        String userClass;
        String userMajor;
        String userFullName;
        String userID;

        //User Attributes
        ArrayList<String> sellItemsREFS;


        ////////////////////////////////////////////////////////////////////////
        ////Default Constructor
        ////////////////////////////////////////////////////////////////////////
        public User(){
            sellItemsREFS = new ArrayList<>();
        }

        public User(String userDisplayedName, String userEmail, String userPhotoUri, String userID){
            this.userDisplayedName = userDisplayedName;
            this.userEmail = userEmail;
            this.userPhotoUri = userPhotoUri;
            this.userID = userID;

            userPhoneNumber = "";
            userClass = "";
            userMajor = "";
            userFullName = "";

            sellItemsREFS = new ArrayList<>();
        }

        ////////////////////////////////////////////////////////////////////////
        //////Getter Methods
        ////////////////////////////////////////////////////////////////////////


        public String getUserDisplayedName() {
            return userDisplayedName;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public String getUserPhotoUri() {
            return userPhotoUri;
        }

        public String getUserID() {
            return userID;
        }

        public ArrayList<String> getSellItemsREFS() {
            return sellItemsREFS;
        }

        public String getUserPhoneNumber() {
            return userPhoneNumber;
        }

        public String getUserClass() {
            return userClass;
        }

        public String getUserMajor() {
            return userMajor;
        }

        public String getUserFullName() {
            return userFullName;
        }

        ////////////////////////////////////////////////////////////////////////
        /////Setter methods
        ////////////////////////////////////////////////////////////////////////


        public void setUserDisplayedName(String userDisplayedName) {
            this.userDisplayedName = userDisplayedName;
        }

        public void setUserEmail(String userEmail) {
            this.userEmail = userEmail;
        }

        public void setUserPhotoUri(String userPhotoUri) {
            this.userPhotoUri = userPhotoUri;
        }

        public void setUserID(String userID) {
            this.userID = userID;
        }

        public void setSellItemsREFS(ArrayList<String> sellItemsREFS) {
            this.sellItemsREFS = sellItemsREFS;
        }

        public void addNewSellingItemREF(String newSellingItemREF){
            sellItemsREFS.add(newSellingItemREF);
        }

        public void setUserPhoneNumber(String userPhoneNumber) {
            this.userPhoneNumber = userPhoneNumber;
        }

        public void setUserClass(String userClass) {
            this.userClass = userClass;
        }

        public void setUserMajor(String userMajor) {
            this.userMajor = userMajor;
        }

        public void setUserFullName(String userFullName) {
            this.userFullName = userFullName;
        }
    }



}
