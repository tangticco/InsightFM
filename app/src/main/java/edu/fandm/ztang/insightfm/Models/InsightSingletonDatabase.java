package edu.fandm.ztang.insightfm.Models;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.jsoup.helper.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.lang.Object;

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
    private ArrayList<InsightDatabaseModel.Session> sessions;

    //Create some instance for google map related variables
    private Map<String, LatLng>  mapMarkers = new Hashtable<String, LatLng>();
    private Map<String, String>  mapMarkersFullName = new Hashtable<>();


    //Create some instance for data-searching
    private InfoSearchModel.Trie courseTitleTrie;
    private InfoSearchModel.Trie courseDEPTrie;
    private InfoSearchModel.Trie sessionCRNTrie;
    private InfoSearchModel.Trie buildingTrie;
    private InfoSearchModel.Trie instructorTrie;
    ArrayList<Integer> inforIDlist;
    ArrayList<String> wordList;

    //create some instance for data searching
    private ArrayList<Integer> searchCategoryIndex;


    //Create some instance for data fetching
    public Map<String, String> departURL;


    //Variables for map changes
    private boolean mapNeedChange = false;
    private LatLng newMapLocation;


    //user-related variable
    private InsightDatabaseModel.User currentUser;


    //Book Selling related variables
    private ArrayList<InsightDatabaseModel.Sellingitem> allSellingItems;


    public boolean getSpeechCommand = false;


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
        sessions = new ArrayList<>();

        //initialize instances for data-searching
        courseTitleTrie = new InfoSearchModel.Trie(false, -1);
        sessionCRNTrie = new InfoSearchModel.Trie(true, -1);
        courseDEPTrie = new InfoSearchModel.Trie(false, -1);
        buildingTrie = new InfoSearchModel.Trie(false, -1);
        instructorTrie = new InfoSearchModel.Trie(false, -1);
        wordList = new ArrayList<>();
        inforIDlist = new ArrayList<>();
        searchCategoryIndex = new ArrayList<>();



        //initialize mapMarkerLat;
        setMapLAT();
        setMapMarkersFullName();

        //create course info database
        getCourseInfo();

        //get the word list
        getWordList();

        //set up department urls
        setupDepartURL();

        //Initialize Selling related variables
        allSellingItems = new ArrayList<>();

        Toast.makeText(mContext, "Database Complete", Toast.LENGTH_SHORT).show();
    }

    /**
     * Blank constructor for Firebase use
     */
    public InsightSingletonDatabase(){

    }

    /**
     * A singleton method that get a instance from the this model
     * @param pContext the context of the parsing activity (usually the main)
     * @return A InsightSingletonDatabase
     */
    public static InsightSingletonDatabase getInstance(Context pContext){
        if(mDatabase == null){
            mDatabase = new InsightSingletonDatabase(pContext);
        }
        return mDatabase;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////Searching methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public ArrayList<Integer> searchInfor(String searchWord){
        inforIDlist = new ArrayList<>();
        searchCategoryIndex = new ArrayList<>();

        ArrayList<Integer> buildingResult = buildingTrie.searchAllPossibleResult(searchWord);
        ArrayList<Integer> departmentResult = courseDEPTrie.searchAllPossibleResult(searchWord);
        ArrayList<Integer> courseResult = courseTitleTrie.searchAllPossibleResult(searchWord);
        ArrayList<Integer> sessionResult = sessionCRNTrie.searchAllPossibleResult(searchWord);
        ArrayList<Integer> instructorResult = instructorTrie.searchAllPossibleResult(searchWord);


        int currentIndex = 0;
        if(!buildingResult.isEmpty()){
            inforIDlist.addAll(buildingResult);
            currentIndex = currentIndex + buildingResult.size();
            searchCategoryIndex.add(currentIndex);
        }else{
            searchCategoryIndex.add(0);
        }

        if(!departmentResult.isEmpty()){
            inforIDlist.addAll(departmentResult);
            currentIndex =currentIndex +departmentResult.size();
            searchCategoryIndex.add(currentIndex);
        }else{
            searchCategoryIndex.add(0);
        }

        if(!courseResult.isEmpty()){
            inforIDlist.addAll(courseResult);
            currentIndex = currentIndex +courseResult.size();
            searchCategoryIndex.add(currentIndex);
        }else{
            searchCategoryIndex.add(0);
        }

        if(!sessionResult.isEmpty()){
            inforIDlist.addAll(sessionResult);
            currentIndex = currentIndex +sessionResult.size();
            searchCategoryIndex.add(currentIndex );
        }else{
            searchCategoryIndex.add(0);
        }

        if(!instructorResult.isEmpty()){
            inforIDlist.addAll(instructorResult);
            currentIndex = currentIndex +instructorResult.size();
            searchCategoryIndex.add(currentIndex );
        }else{
            searchCategoryIndex.add(0);
        }

        return inforIDlist;

    }

    public ArrayList<Integer> fuzzysearchInfor(String searchWord){

        ArrayList<String> searchWords = getFuzzywords(2, searchWord);

        inforIDlist = new ArrayList<>();
        searchCategoryIndex = new ArrayList<>();

        ArrayList<Integer> buildingResult = new ArrayList<>();
        ArrayList<Integer> departmentResult = new ArrayList<>();
        ArrayList<Integer> courseResult = new ArrayList<>();
        ArrayList<Integer> sessionResult = new ArrayList<>();
        ArrayList<Integer> instructorResult = new ArrayList<>();


        for (int i = 0; i < searchWords.size(); i++){
            buildingResult.addAll(buildingTrie.searchAllPossibleResult(searchWord));
            departmentResult.addAll(courseDEPTrie.searchAllPossibleResult(searchWord));
            courseResult.addAll(courseTitleTrie.searchAllPossibleResult(searchWord));
            sessionResult.addAll(sessionCRNTrie.searchAllPossibleResult(searchWord));
            instructorResult.addAll(instructorTrie.searchAllPossibleResult(searchWord));
        }


        int currentIndex = 0;
        if(!buildingResult.isEmpty()){
            inforIDlist.addAll(buildingResult);
            currentIndex = currentIndex + buildingResult.size();
            searchCategoryIndex.add(currentIndex);
        }else{
            searchCategoryIndex.add(0);
        }

        if(!departmentResult.isEmpty()){
            inforIDlist.addAll(departmentResult);
            currentIndex =currentIndex +departmentResult.size();
            searchCategoryIndex.add(currentIndex);
        }else{
            searchCategoryIndex.add(0);
        }

        if(!courseResult.isEmpty()){
            inforIDlist.addAll(courseResult);
            currentIndex = currentIndex +courseResult.size();
            searchCategoryIndex.add(currentIndex);
        }else{
            searchCategoryIndex.add(0);
        }

        if(!sessionResult.isEmpty()){
            inforIDlist.addAll(sessionResult);
            currentIndex = currentIndex +sessionResult.size();
            searchCategoryIndex.add(currentIndex );
        }else{
            searchCategoryIndex.add(0);
        }

        if(!instructorResult.isEmpty()){
            inforIDlist.addAll(instructorResult);
            currentIndex = currentIndex +instructorResult.size();
            searchCategoryIndex.add(currentIndex );
        }else{
            searchCategoryIndex.add(0);
        }

        return inforIDlist;

    }

    public ArrayList<Integer> searchCourse(String searchWord){
        inforIDlist = new ArrayList<>();
        ArrayList<Integer> courseResult = courseTitleTrie.searchAllPossibleResult(searchWord);
        if(!courseResult.isEmpty()){
            inforIDlist.addAll(courseResult);
        }else{
            inforIDlist.add(-1);
        }

        return inforIDlist;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////getter methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public ArrayList<InsightDatabaseModel.Building> getBuildings() {
        return buildings;
    }

    public InsightDatabaseModel.Building getBuilding(int infoID){
        return buildings.get(infoID);
    }

    public InsightDatabaseModel.Course getCourse(int infoID){
        return courses.get(infoID);
    }

    public InsightDatabaseModel.Instructor getInstructor(int infoID){
        return instructors.get(infoID);
    }

    public InsightDatabaseModel.Department getDepartment(int infoID){
        return departments.get(infoID);
    }

    public InsightDatabaseModel.Session getSession(int infoID){
        return sessions.get(infoID);
    }

    public String getDepartURL(String departmentName){
        return departURL.get(departmentName);
    }

    public InsightDatabaseModel.User getCurrentUser() {
        return currentUser;
    }

    public ArrayList<String> getSearchResultList(){


        ArrayList<String> resultStrings = new ArrayList<>();

        for(int currentIndex = 0; currentIndex < inforIDlist.size(); currentIndex++ ){
            if(currentIndex < searchCategoryIndex.get(0)){
                resultStrings.add(buildings.get(inforIDlist.get(currentIndex)).getResourceTitle());
            }else if(currentIndex < searchCategoryIndex.get(1)){
                resultStrings.add(departments.get(inforIDlist.get(currentIndex)).getResourceTitle());
            }else if(currentIndex < searchCategoryIndex.get(2)){
                resultStrings.add(courses.get(inforIDlist.get(currentIndex)).getResourceTitle() + ": " + courses.get(inforIDlist.get(currentIndex)).getTitle());
            }else if(currentIndex < searchCategoryIndex.get(3)){
                resultStrings.add(sessions.get(inforIDlist.get(currentIndex)).getResourceTitle());
            }else if(currentIndex < searchCategoryIndex.get(4)){
                resultStrings.add(instructors.get(inforIDlist.get(currentIndex)).getResourceTitle());
            }
        }


        return resultStrings;
    }

    public ArrayList<Integer> getSearchCategoryIndex() {
        return searchCategoryIndex;
    }

    public InsightDatabaseModel.FMResource getSearchResultItem(int currentIndex){

            if(currentIndex < searchCategoryIndex.get(0)){
                return buildings.get(inforIDlist.get(currentIndex));
            }else if(currentIndex < searchCategoryIndex.get(1)){
                return departments.get(inforIDlist.get(currentIndex));
            }else if(currentIndex < searchCategoryIndex.get(2)){
                return courses.get(inforIDlist.get(currentIndex));
            }else if(currentIndex < searchCategoryIndex.get(3)){
                return sessions.get(inforIDlist.get(currentIndex));
            }else{
                return instructors.get(inforIDlist.get(currentIndex));
            }
    }

    public ArrayList<InsightDatabaseModel.Sellingitem> getAllSellingItems() {

        Log.d("get method", String.valueOf(this.allSellingItems.size()));


        return this.allSellingItems;
    }

    public ArrayList<String> getAllSellingItemsTitles(){
        ArrayList<String> titles = new ArrayList<>();
        for(int i = 0; i< allSellingItems.size(); i++){
            titles.add(allSellingItems.get(i).getBelongedBook().getBookTitle());

            //test
            Log.d("progess: ", allSellingItems.get(i).getBelongedBook().getBookTitle());
        }

        return titles;
    }

    ////////////Map related
    public Boolean isMapNeedChange(){
        return mapNeedChange;
    }

    public LatLng getNewMapLocation() {
        return newMapLocation;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////Setter Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
                LatLng currentLocation = new LatLng(40.046096674063996,-76.31949618458748); //default location, Stager

                //Check if the building is already in the trie
                InsightDatabaseModel.Building currentBuilding;
                InfoSearchModel.TrieNode tempNode = buildingTrie.searchNode(newCourseInfoList[4]);
                if( tempNode ==  null){
                    currentBuilding = new InsightDatabaseModel.Building(newCourseInfoList[4]);

                    //check if there is corresponding location for the building
                    if(mapMarkers.get(newCourseInfoList[4]) != null){
                        currentLocation = mapMarkers.get(newCourseInfoList[4]);
                        currentBuilding.setRecourseLocation(currentLocation);
                    }
                    currentBuilding.setInfoID(currentBuildingIndex);
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
                    currentDepartment.setInfoID(currentDepartIndex);
                    currentDepartment.setRecourseLocation(currentLocation);
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
                    currentCourse = new InsightDatabaseModel.Course(newCourseInfoList[1], Integer.valueOf(newCourseInfoList[2]), newCourseInfoList[3], currentDepartment, currentBuilding);
                    currentCourse.setInfoID(currentCourseIndex);
                    currentCourse.setRecourseLocation(currentLocation);
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
                    currentInstructor.setInfoID(currentInstructorIndex);
                    currentInstructor.setRecourseLocation(currentLocation);
                    instructorTrie.insert(newCourseInfoList[9], currentInstructorIndex);
                    instructors.add(currentInstructor);
                    currentInstructorIndex += 1;
                }else{
                    currentInstructor = instructors.get(tempNode.getInfoID());
                }

                //the course, building and instructor is alread in the database, add a new session
                InsightDatabaseModel.Session newSession = new InsightDatabaseModel.Session(Integer.valueOf(newCourseInfoList[0]), currentBuilding, newCourseInfoList[5], newCourseInfoList[6], newCourseInfoList[7], newCourseInfoList[8], currentInstructor, currentCourse);
                newSession.setInfoID(currentSessionIndex);
                newSession.setRecourseLocation(currentLocation);
                sessions.add(newSession);
                sessionCRNTrie.insert(newCourseInfoList[0], currentSessionIndex);
                currentSessionIndex += 1;


                line = courseInputBuffer.readLine();
            }

        }catch (IOException e){
            Log.d(TAG_ERROR, "There is no course file");
        }
    }

    private void getWordList(){

        try{
            InputStream wordInputStream = mContext.getAssets().open("courses.txt");
            BufferedReader wordInputBuffer = new BufferedReader(new InputStreamReader(wordInputStream));

            String word = wordInputBuffer.readLine();
            wordList.add(word);

            while(word != null){
                word = wordInputBuffer.readLine();
                wordList.add(word);
            }


        }catch (IOException e){
            e.printStackTrace();
        }

    }

    private ArrayList<String> getFuzzywords(int threshold, String searchWord){

        ArrayList<String> fuzzyWords = new ArrayList<>();

        for(int i = 0; i < wordList.size(); i++){
            if(computeLevenshteinDistance(searchWord, wordList.get(i)) <= threshold){
                fuzzyWords.add(wordList.get(i));
            }
        }

        return fuzzyWords;
    }

    public int computeLevenshteinDistance(CharSequence lhs, CharSequence rhs) {
        int[][] distance = new int[lhs.length() + 1][rhs.length() + 1];

        for (int i = 0; i <= lhs.length(); i++)
            distance[i][0] = i;
        for (int j = 1; j <= rhs.length(); j++)
            distance[0][j] = j;

        for (int i = 1; i <= lhs.length(); i++)
            for (int j = 1; j <= rhs.length(); j++)
                distance[i][j] = minimum(
                        distance[i - 1][j] + 1,
                        distance[i][j - 1] + 1,
                        distance[i - 1][j - 1] + ((lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1));

        return distance[lhs.length()][rhs.length()];
    }

    private static int minimum(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    public Map<String, LatLng> getMapMarkerLATs(){
        return mapMarkers;
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

    private void setMapLAT(){

        LatLng ADA_LAT = new LatLng(40.046112078131216,-76.31941705942154);
        LatLng BAR_LAT = new LatLng(40.046917990535796,-76.31917834281921);
        LatLng GOE_LAT = new LatLng(40.04497145443324,-76.32026731967926);
        LatLng HAC_LAT = new LatLng(40.04809450914126,-76.32054224610329);
        LatLng HAR_LAT = new LatLng(40.04785838574442,-76.31999775767326);
        LatLng HER_LAT = new LatLng(40.04395299145577,-76.3206535577774);
        LatLng HUE_LAT = new LatLng(40.044478651666594,-76.32042825222015);
        LatLng KAU_LAT = new LatLng(40.04779678820205,-76.32056638598442);
        LatLng KEI_LAT = new LatLng(40.04557923960607,-76.31954848766327);
        LatLng LSP_LAT = new LatLng(40.04919298549468,-76.32019221782684);
        LatLng ORT_LAT = new LatLng(40.04828135403209,-76.31578803062439);
        LatLng ROS_LAT = new LatLng(40.047593679210486,-76.31902545690536);
        LatLng SCC_LAT = new LatLng(40.04737603359178,-76.31957799196243);
        LatLng STA_LAT = new LatLng(40.046096674063996,-76.31949618458748);
        LatLng WRH_LAT = new LatLng(40.04853595503695,-76.32047116756439);

        //TODO complete other buildings



        mapMarkers.put("ADA", ADA_LAT);
        mapMarkers.put("BAR", BAR_LAT);
        mapMarkers.put("GOE", GOE_LAT);
        mapMarkers.put("HAC", HAC_LAT);
        mapMarkers.put("HAR", HAR_LAT);
        mapMarkers.put("HER", HER_LAT);
        mapMarkers.put("HUE", HUE_LAT);
        mapMarkers.put("KAU", KAU_LAT);
        mapMarkers.put("KEI", KEI_LAT);
        mapMarkers.put("LSP", LSP_LAT);
        mapMarkers.put("ORT", ORT_LAT);
        mapMarkers.put("ROS", ROS_LAT);
        mapMarkers.put("SCC", SCC_LAT);
        mapMarkers.put("STA", STA_LAT);
        mapMarkers.put("WRH", WRH_LAT);
    }

    private void setMapMarkersFullName(){

        mapMarkersFullName.put("ADA200",	"Adams Auditorium, located in Hackman Hall");
        mapMarkersFullName.put("APP",	"Appel");
        mapMarkersFullName.put("BARGAU",	"Barshinger Center, Gault Room");
        mapMarkersFullName.put("BARSTA",	"Barshinger Center, Stage");
        mapMarkersFullName.put("BON",	"Bonchek College House");
        mapMarkersFullName.put("BRO",	"Brooks College House");
        mapMarkersFullName.put("GOE",	"Goethean Hall");
        mapMarkersFullName.put( "HAC",	"Hackman Hall");
        mapMarkersFullName.put( "HAR",	"Harris Center");
        mapMarkersFullName.put("HER",	"Herman Arts");
        mapMarkersFullName.put("JIC",	"Joseph International Center");
        mapMarkersFullName.put("KAU",	"Kaufman Lecture Hall");
        mapMarkersFullName.put("KEI",	"Keiper Liberal Arts"); //URL checked
        mapMarkersFullName.put("KLE",	"Klehr Center");
        mapMarkersFullName.put( "LSP",	"Barshinger Life Sciences & Philosophy Building");
        mapMarkersFullName.put("NEW",	"New College House");
        mapMarkersFullName.put( "ORT",	"Other Room Theatre (715 North Pine Street)");
        mapMarkersFullName.put("ROS",	"Roschel Performing Arts Center");
        mapMarkersFullName.put("SCC",	"Steinman College Center");
        mapMarkersFullName.put("SFL",	"Shadek-Fackenthal Library");
        mapMarkersFullName.put("STA",	"Stager Hall"); //URL checked
        mapMarkersFullName.put("WAR",	"Ware College House");
        mapMarkersFullName.put("WEI",	"Weis College House");
        mapMarkersFullName.put("WOH",	"Wohlsen Center");
        mapMarkersFullName.put("WRH",	"Writer's House");

    }

    public void setMapNeedChange(boolean mapNeedChange) {
        this.mapNeedChange = mapNeedChange;
    }

    public void setNewMapLocation(LatLng newMapLocation) {
        this.newMapLocation = newMapLocation;
    }

    public void setCurrentUser(InsightDatabaseModel.User currentUser) {
        this.currentUser = currentUser;
    }

    public void setAllSellingItems(ArrayList<InsightDatabaseModel.Sellingitem> allSellingItems) {
        this.allSellingItems = allSellingItems;
    }

    public void addNewSellingItem(InsightDatabaseModel.Sellingitem newItem){
        this.allSellingItems.add(newItem);
        Log.d("Set method", String.valueOf(this.allSellingItems.size()));
    }
}
