package edu.fandm.ztang.insightfm.Models;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by zhuofantang on 4/6/17.
 */

public class InsightSingletonDatabase {

    private static InsightSingletonDatabase mDatabase = null;

    //create some tags for testing purpose
    private final static String TAG_ERROR = "Error: ";
    private final static String TAG_PROGRESS = "Progress: ";

    //create some instance for data processing
    private InputStream courseInputStream;
    private BufferedReader courseInputBuffer;
    private Context mContext;

    //Create some instance for data-storing
    private ArrayList<InsightDatabaseModel.Course> courses;
    private ArrayList<InsightDatabaseModel.Building> buildings;
    private ArrayList<InsightDatabaseModel.Floor> floors;
    private ArrayList<InsightDatabaseModel.Office> offices;
    private ArrayList<InsightDatabaseModel.Department> departments;
    private ArrayList<InsightDatabaseModel.Instructor> instructors;

    //Create some instance for data-searching
    private InfoSearchModel.Trie courseTitleTrie;
    private InfoSearchModel.Trie courseDEPTrie;
    private InfoSearchModel.Trie sessionCRNTrie;
    private InfoSearchModel.Trie buildingTrie;
    private InfoSearchModel.Trie instructorTrie;


    //Create some instance for data fetching
    public Map<String, String> departURL;


    /**
     * A private default constructor
     * @param pContext
     */
    private InsightSingletonDatabase(Context pContext){

        mContext = pContext;

        //initialize instances for data-storing
        courses = new ArrayList<>();
        departments = new ArrayList<>();
        buildings = new ArrayList<>();
        floors = new ArrayList<>();
        offices = new ArrayList<>();
        instructors = new ArrayList<>();

        //initialize instances for data-searching
        courseTitleTrie = new InfoSearchModel.Trie(false, -1);
        sessionCRNTrie = new InfoSearchModel.Trie(true, -1);
        courseDEPTrie = new InfoSearchModel.Trie(false, -1);
        buildingTrie = new InfoSearchModel.Trie(false, -1);
        instructorTrie = new InfoSearchModel.Trie(false, -1);

        //create course info database
        getCourseInfo();

        //set up department urls
        setupDepartURL();

        Toast.makeText(mContext, "Database Complete", Toast.LENGTH_SHORT).show();
    }

    /**
     * A singleton method that get a instance from the this model
     * @param pContext the context of the parsing activity (usually the main)
     * @return A InsightSingletonDatabase
     */
    public static InsightSingletonDatabase getInstance(Context pContext){
        if(mDatabase == null){
            mDatabase = new InsightSingletonDatabase(pContext);

            Log.d(TAG_PROGRESS, "Initializing Database");
        }

        return mDatabase;
    }

    public ArrayList<Integer> searchCourse(String courseTitle){

        return courseTitleTrie.searchAllPossibleResult(courseTitle);
    }


    //////////////////////////////
    /////getter methods
    //////////////////////////////
    public InsightDatabaseModel.Building getBuilding(int accessCode){
        return buildings.get(accessCode);
    }

    public InsightDatabaseModel.Course getCourse(int accessCode){
        return courses.get(accessCode);
    }

    public InsightDatabaseModel.Instructor getInstructor(int accessCode){
        return instructors.get(accessCode);
    }

    public InsightDatabaseModel.Department getDepartment(int accessCode){
        return departments.get(accessCode);
    }

    public String getDepartURL(String departmentName){
        return departURL.get(departmentName);
    }

    //TODO implement getter methods for floors, offices, and buildings.



    //////////////////////////////
    /////Helper functions
    //////////////////////////////

    /**
     * A private helper to create the database and search engine
     */
    private void getCourseInfo(){

        int currentDepartIndex = 0;
        int currentCourseIndex = 0;
        int currentSessionIndex = 0;
        int currentBuildingIndex = 0;
        int currentInstructorIndex = 0;


        try{
            courseInputStream = mContext.getAssets().open("courses.txt");
            courseInputBuffer = new BufferedReader(new InputStreamReader(courseInputStream));

            String line  = courseInputBuffer.readLine();
            line = courseInputBuffer.readLine();

            while(line != null){
                String[] newCourseInfoList = line.split("\t");

                //Check if the building is already in the trie
                InsightDatabaseModel.Building currentBuilding;
                InfoSearchModel.TrieNode tempNode = buildingTrie.searchNode(newCourseInfoList[4]);
                if( tempNode ==  null){
                    currentBuilding = new InsightDatabaseModel.Building(newCourseInfoList[4]);
                    buildingTrie.insert(newCourseInfoList[4], currentBuildingIndex);
                    buildings.add(currentBuilding);
                    currentBuildingIndex += 1;
                }else{
                    currentBuilding = buildings.get(tempNode.getInfoID());
                }

                //check if the department is already in the trie
                InsightDatabaseModel.Department currentDepartment;
                tempNode = courseDEPTrie.searchNode(newCourseInfoList[1]);
                if(tempNode == null){
                    currentDepartment = new InsightDatabaseModel.Department(newCourseInfoList[1]);
                    courseDEPTrie.insert(newCourseInfoList[1], currentDepartIndex);
                    departments.add(currentDepartment);
                    currentDepartIndex += 1;
                }else{
                    currentDepartment = departments.get(tempNode.getInfoID());
                }


                //check if the course is already in the database
                InsightDatabaseModel.Course currentCourse;
                tempNode = courseTitleTrie.searchNode(newCourseInfoList[3]);
                if(tempNode == null){
                    currentCourse = new InsightDatabaseModel.Course(newCourseInfoList[1], Integer.valueOf(newCourseInfoList[2]), newCourseInfoList[3], currentDepartment);
                    courseTitleTrie.insert(newCourseInfoList[3], currentCourseIndex);
                    courses.add(currentCourse);
                    currentCourseIndex += 1;
                }else{
                    currentCourse = courses.get(tempNode.getInfoID());
                }

                //check if the instructor is already in the database
                InsightDatabaseModel.Instructor currentInstructor;
                tempNode = instructorTrie.searchNode(newCourseInfoList[9]);
                if(tempNode == null){
                    currentInstructor = new InsightDatabaseModel.Instructor(newCourseInfoList[9]);
                    instructorTrie.insert(newCourseInfoList[9], currentInstructorIndex);
                    instructors.add(currentInstructor);
                    currentInstructorIndex += 1;
                }else{
                    currentInstructor = instructors.get(tempNode.getInfoID());
                }

                //the course, building and instructor is alread in the database, add a new session
                InsightDatabaseModel.Session newSession = new InsightDatabaseModel.Session(Integer.valueOf(newCourseInfoList[0]), currentBuilding, newCourseInfoList[5], newCourseInfoList[6], newCourseInfoList[7], newCourseInfoList[8], currentInstructor, currentCourse);
                sessionCRNTrie.insert(newCourseInfoList[0], currentSessionIndex);
                currentSessionIndex += 1;


                line = courseInputBuffer.readLine();
            }

        }catch (IOException e){
            Log.d(TAG_ERROR, "There is no course file");
        }
    }

    private void setupDepartURL(){

        departURL = new Hashtable<String, String>();
        //Create a department url
        departURL.put("AFS", "africana-studies");
        departURL.put("AMS", "american-studies");
        departURL.put("ANT", "anthropology");
        departURL.put("ARB", "arabic");
        departURL.put("ART", "art");
        departURL.put("PHY", "physics");
        departURL.put("BIO", "biology");
        departURL.put("CHM", "chemistry");
        departURL.put("CPS", "computer-science");
        departURL.put("BFB", "bfb");
        departURL.put("BOS", "business");
        departURL.put("CHN", "chinese");
        departURL.put("CLS", "classics");
        departURL.put("LIT", "comparative-literary-studies");
        departURL.put("ENV", "earth-environment");
        departURL.put("ECO", "economics");
        departURL.put("ENG", "english");
        departURL.put("FRN", "french");
        departURL.put("GER", "german");
        departURL.put("GOV", "government");
        departURL.put("JST", "hebrew");
        departURL.put("HIS", "history");
        departURL.put("IST", "international-studies");
        departURL.put("ITA", "italian");
        departURL.put("JPN", "japanese");
        departURL.put("JST", "judaic-studies");
        departURL.put("MAT", "mathematics");
        departURL.put("MUS", "music");
        departURL.put("PHI", "philosophy");
        departURL.put("PSY", "psychology");
        departURL.put("PBH", "public-health");
        departURL.put("RST", "religious-studies");
        departURL.put("RUS", "russian");
        departURL.put("STS", "sts");
        departURL.put("SPM", "spm");
        departURL.put("SOC", "sociology");
        departURL.put("SPA", "spanish");
        departURL.put("TDF", "tdf");
        departURL.put("WGS", "wgs");
    }
}
