package edu.fandm.ztang.insightfm;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import edu.fandm.ztang.insightfm.Models.ExtendedInfoCourseSellingItemCustomAdapter;
import edu.fandm.ztang.insightfm.Models.ExtendedInfoCourseSessionsCustomAdapter;
import edu.fandm.ztang.insightfm.Models.InsightDatabaseModel;
import edu.fandm.ztang.insightfm.Models.InsightSingletonDatabase;

public class ExtendedInfoWindowActivity extends BaseActivity {

    private InsightSingletonDatabase mDatabase;

    private int inforID;    //accesscode of a particular resource arraylist
    private int position;   //index in the searchResultAccessCodesl
    private String classType;


    //firebase related
    FirebaseDatabase firebaseDatabase;



    //UI
    private TextView infoTitle;
    private Button button1;
    private Button button2;
    private Button button3;

    private View first_expandINFO_panel_courseView;
    private View first_expandINFO_panel_buildingView;
    private View first_expandINFO_panel_sessionView;
    private View first_expandINFO_panel_departmentView;
    private View first_expandINFO_panel_instructorView;

    private View second_expandINFO_panel_courseView;
    private View second_expandINFO_panel_buildingView;
    private View second_expandINFO_panel_sessionView;
    private View second_expandINFO_panel_departmentView;
    private View second_expandINFO_panel_instructorView;


    Context mContext = this;

    //map movement related variables


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //retrieve the class type and info ID
        Bundle b = getIntent().getExtras();

        setContentView(R.layout.activity_extended_info_window);


        //get an instance of the database
        mDatabase = InsightSingletonDatabase.getInstance(this);

        firebaseDatabase = FirebaseDatabase.getInstance();

        inforID = b.getInt("INFOID");
        position = b.getInt("Position");
        classType = b.getString("ClassType");



        //set uo info title
        infoTitle= (TextView)findViewById(R.id.infoTitle);
        setInfoTitle();

        //Fetch data from the website
        new InternetRequest().execute("https://www.fandm.edu");

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        updateProfile();


        //hook up UI views

        first_expandINFO_panel_courseView = findViewById(R.id.first_expandINFO_panel_course);
        first_expandINFO_panel_buildingView = findViewById(R.id.first_expandINFO_panel_building);
        first_expandINFO_panel_sessionView = findViewById(R.id.first_expandINFO_panel_session);
        first_expandINFO_panel_departmentView = findViewById(R.id.first_expandINFO_panel_department);
        first_expandINFO_panel_instructorView = findViewById(R.id.first_expandINFO_panel_instructor);

        second_expandINFO_panel_courseView = findViewById(R.id.second_expandINFO_panel_course);
        second_expandINFO_panel_buildingView = findViewById(R.id.second_expandINFO_panel_building);
        second_expandINFO_panel_sessionView = findViewById(R.id.second_expandINFO_panel_session);
        second_expandINFO_panel_departmentView = findViewById(R.id.second_expandINFO_panel_department);
        second_expandINFO_panel_instructorView = findViewById(R.id.second_expandINFO_panel_instructor);

        button1 = (Button)findViewById(R.id.button1);
        button2 = (Button)findViewById(R.id.button2);
        button3 = (Button)findViewById(R.id.button3);

        updateUI();
    }

    private class InternetRequest  extends AsyncTask<String, Void, String>{

        protected String doInBackground(String... urls) {
            String output = "";


            if (classType.equals("Course")){

                output = fetchCourseData();

            }else if(classType.equals("Department")){

                output = fetchDepartmentData();

            }else if(classType.equals("Building")){

                output = fetchBuildingData();

            }else if(classType.equals("Instructor")){

                output = fetchInstructorData();

            }else if(classType.equals("Session")){

                inforID = mDatabase.getSession(inforID).getSessionCourse().getInfoID();
                output = fetchCourseData();

            }



            return output;
        }


        protected void onPostExecute(String result) {

            TextView infoDescriptionView = (TextView)findViewById(R.id.infoDes);
            infoDescriptionView.setMovementMethod(new ScrollingMovementMethod());
            infoDescriptionView.setText(result);
        }



    }

    ////////////////////////////////////////////
    ///////Controllers
    ////////////////////////////////////////////
    public void gotoLocation(View v){

        mDatabase.setMapNeedChange(true);   //set mapNeedChange to true
        mDatabase.setNewMapLocation(mDatabase.getSearchResultItem(position).getRecourseLocation());  //send the new location

        //back to main activity
        Intent intent = new Intent(ExtendedInfoWindowActivity.this, MainContentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }


    ////////////////////////////////////////////
    ///////Helper functions
    ////////////////////////////////////////////
    private String fetchCourseData(){
        String output = "";

        InsightDatabaseModel.Course currentCourse = mDatabase.getCourse(inforID);
        String infoTitle = currentCourse.getTitle();
        String courseID = String.valueOf(currentCourse.getCourseNum());

        String url = "https://www.fandm.edu/" + mDatabase.getDepartURL(currentCourse.getDepart().getDepartmentName()) + "/courses";


        if(currentCourse.getCourseDescription() == null){
            try{
                Document doc = Jsoup.connect(url).get();
                Elements columns = doc.getElementsByClass("apos-rich-text");

                for(Element body: columns){
                    if(body.text().contains(infoTitle)){
                        Elements columnLists = body.getElementsByTag("p");

                        for(Element listItem: columnLists){
                            if(listItem.text().contains(String.valueOf(courseID)) && listItem.text().contains(infoTitle)){
                                Element des = listItem.nextElementSibling();
                                output = des.text();

                                //store the course description to the local database
                                currentCourse.setCourseDescription(output);
                            }
                        }
                    };
                }

            }catch (IOException e){
                e.printStackTrace();
            }
        }else{
            output = currentCourse.getCourseDescription();
        }

        return output;
    }

    private String fetchBuildingData(){
        String output = "";

        InsightDatabaseModel.Building currentBuilding = mDatabase.getBuilding(1); //TODO should change to infoID later
        String infoTitle = currentBuilding.getResourceTitle();


        String url = "https://www.fandm.edu/map/stager-hall";


        if(currentBuilding.getBuildingDescription() == null){
            try{
                Document doc = Jsoup.connect(url).get();

                Element mainBody = doc.select("div.fm-content-container").first();
                Element mainContent = mainBody.getElementsByTag("p").first();

                output = mainContent.text();

                //testing message
                Log.d("Fetch result is: ", output);


                currentBuilding.setBuildingDescription(output);

                //Elements links = mainBody.getElementsByTag("a");

            }catch (IOException e){
                e.printStackTrace();
            }
        }else{
            output = currentBuilding.getBuildingDescription();

        }

        return output;
    }

    private String fetchDepartmentData(){


        String output = "";

        InsightDatabaseModel.Department currentDepartment = mDatabase.getDepartment(inforID);
        String infoTitle = currentDepartment.getResourceTitle();
        String url = "https://www.fandm.edu/" + mDatabase.getDepartURL(currentDepartment.getDepartmentName()) + "/courses";

        if(currentDepartment.getDepartmentDescription()== null){
            try{
                Document doc = Jsoup.connect(url).get();
                Element mainContent = doc.select("div.main-content").first();
                Elements overview = mainContent.select("div.apos-rich-text");

                for(Element part : overview){
                    if (part.getElementsByTag("p") != null){
                        Elements contents = part.getElementsByTag("p");
                        for(Element content: contents){
                            output += content.text() + "\n" +"\n";
                        }
                        break;
                    }

                }


                output = overview.text();

                currentDepartment.setDepartmentDescription(output);



            }catch (IOException e){
                e.printStackTrace();
            }
        }else{
            output = currentDepartment.getDepartmentDescription();
            Log.d("Progess: ", "Data already stored");
        }

        return output;
    }

    private String fetchInstructorData(){
        String output = "";

        InsightDatabaseModel.Instructor currentInstructor = mDatabase.getInstructor(inforID); //TODO replace it with infoID later
        String infoTitle = currentInstructor.getResourceTitle();
        String url = "https://www.fandm.edu/directory/jing-hu";

        if(currentInstructor.getInstructorDescription()== null){
            try{
                Document doc = Jsoup.connect(url).get();

                Element body = doc.select("div.main-content").first();


                //get contact info
                Element profile = body.getElementsByClass("card-profile-details").first();
                Elements contacts = profile.getElementsByTag("p");
                int i  = 0;
                for(Element contact : contacts){
                    if (i == 0){
                        currentInstructor.setPhoneNumber(contact.text());

                        //test
                        Log.d("Progress: ", contact.text());
                    }else if(i==1){
                        currentInstructor.setEmailAddress(contact.text());

                        //test
                        Log.d("Progress: ", contact.text());
                    }else if(i == 2){
                        InsightDatabaseModel.Office newOffice = new InsightDatabaseModel.Office(contact.text());
                        currentInstructor.setOffice(newOffice);

                        //test
                        Log.d("Progress: ", contact.text());
                    }
                    i++;
                }


                //get main contents
                Element mainContent = body.select("div.fm-content-container").first();

                //test
                Log.d("Progress: ", mainContent.text());


                Elements contents = mainContent.getElementsByTag("p");
                for(Element part : contents){
                    //TODO format the out put text
                    output += part.text();

                }

                currentInstructor.setInstructorDescription(output);



            }catch (IOException e){
                e.printStackTrace();
            }
        }else{
            output = currentInstructor.getInstructorDescription();
            Log.d("Progess: ", "Data already stored");
        }


        return output;

    }

    private void setInfoTitle(){
        if (classType.equals("Course")){

            infoTitle.setText(mDatabase.getCourse(inforID).getTitle());

        }else if(classType.equals("Department")){

            infoTitle.setText(mDatabase.getDepartment(inforID).getDepartmentName());

        }else if(classType.equals("Building")){

            infoTitle.setText(mDatabase.getBuilding(inforID).getBuildingName());

        }else if(classType.equals("Instructor")){

            infoTitle.setText(mDatabase.getInstructor(inforID).getFullName());

        }else if(classType.equals("Session")){

            infoTitle.setText(mDatabase.getSession(inforID).getSessionCourse().getTitle());

        }
    }


    ////////////////////////////////////////////
    //////Update UI functions
    ////////////////////////////////////////////
    private void updateUI(){



        if (classType.equals("Course")){

            updateCourseUI();


        }else if(classType.equals("Department")){


        }else if(classType.equals("Building")){



        }else if(classType.equals("Instructor")){



        }else if(classType.equals("Session")){



        }

    }

    private void updateCourseUI(){

        //hook uo UI
        first_expandINFO_panel_courseView.setVisibility(View.VISIBLE);
        second_expandINFO_panel_courseView.setVisibility(View.VISIBLE);


        //set up the session list
        InsightDatabaseModel.Course currentCourse = mDatabase.getCourse(inforID);
        ArrayList<InsightDatabaseModel.Session> sessions = currentCourse.getSessions();
        ListView sessionListview = (ListView)findViewById(R.id.sessionList_listview);
        ExtendedInfoCourseSessionsCustomAdapter adapter = new ExtendedInfoCourseSessionsCustomAdapter(mContext, sessions);
        sessionListview.setAdapter(adapter);


        //set up the book list
        ArrayList<InsightDatabaseModel.Sellingitem> currentBookSellingItems = currentCourse.getSellingitems();

        ExtendedInfoCourseSellingItemCustomAdapter adapter1 = new ExtendedInfoCourseSellingItemCustomAdapter(this, currentBookSellingItems);
        ListView sellItemsListView = (ListView)findViewById(R.id.book_listview);
        sellItemsListView.setAdapter(adapter1);

    }

    private void updateBuildingUI(){
        first_expandINFO_panel_buildingView.setVisibility(View.VISIBLE);
        second_expandINFO_panel_buildingView.setVisibility(View.VISIBLE);
    }

    private void updateDepartmentUI(){
        first_expandINFO_panel_departmentView.setVisibility(View.VISIBLE);
        second_expandINFO_panel_departmentView.setVisibility(View.VISIBLE);

    }

    private void updateSessionUI(){
        first_expandINFO_panel_sessionView.setVisibility(View.VISIBLE);
        second_expandINFO_panel_sessionView.setVisibility(View.VISIBLE);
    }

    private void updateInstructorUI(){
        first_expandINFO_panel_instructorView.setVisibility(View.VISIBLE);
        second_expandINFO_panel_instructorView.setVisibility(View.VISIBLE);
    }

    private void updateButtonUI(){


        if (classType.equals("Course")){

            InsightDatabaseModel.Course currentCourse = mDatabase.getCourse(inforID);
            //button 1
            button1.setText("Building");
            final InsightDatabaseModel.Building currentBuilding = currentCourse.getBelongedBuilding();

            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, SearchActivity.class);
                    Bundle b = new Bundle();
                    b.putString("SearchWord", currentBuilding.getResourceTitle());
                    intent.putExtras(b);
                    startActivity(intent);
                }
            });


            //button 2
            button2.setText("Instructor");

            //button 3
            button3.setText("Sessions");




        }else if(classType.equals("Department")){

            //button 1
            button1.setText("Building");

            //button 2
            button2.setText("Courses");

            //button 3
            button3.setText("Instructors");

        }else if(classType.equals("Building")){

            //button 1
            button1.setText("Floors");

            //button 2
            button2.setText("Departments");

            //button 3
            button3.setText("Courses");


        }else if(classType.equals("Instructor")){

            //button 1
            button1.setText("Building");

            //button 2
            button2.setText("Courses");

            //button 3
            button3.setText("Contacts");

        }else if(classType.equals("Session")){



        }
    }



    //test
    private String fetchDataTest(){
        String output = "";


        String url = "https://www.fandm.edu/map";



        try{
            Document doc = Jsoup.connect(url).get();
            Elements columns = doc.getElementsByTag("a");

            for(Element body: columns){

                String newURL = body.absUrl("href");

                output += newURL + "\n";


            }

        }catch (IOException e){
            e.printStackTrace();
        }


        return output;

    }





}
