package edu.fandm.ztang.insightfm;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

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

import edu.fandm.ztang.insightfm.Models.InsightDatabaseModel;
import edu.fandm.ztang.insightfm.Models.InsightSingletonDatabase;

public class ExtendedInfoWindowActivity extends AppCompatActivity {

    private InsightSingletonDatabase mDatabase;

    private int inforID;
    private String classType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //retrieve the class type and info ID
        Bundle b = getIntent().getExtras();
        if(b != null){
            setContentView(R.layout.activity_extended_info_window);


            //get an instance of the database
            mDatabase = InsightSingletonDatabase.getInstance(this);

            inforID = b.getInt("INFOID");
            classType = b.getString("ClassType");

            //set the info window's characteristic to the class type
            TextView classTypeTextView = (TextView)findViewById(R.id.classType);
            classTypeTextView.setText(classType);

            TextView infoTitle = (TextView)findViewById(R.id.infoTitle);
            infoTitle.setText(mDatabase.getCourse(inforID).getTitle());

            //Fetch data from the website
            new InternetRequest().execute("https://www.fandm.edu/computer-science/courses");

        }else{
            setContentView(R.layout.activity_error);
        }
    }

    private class InternetRequest  extends AsyncTask<String, Void, String>{

        protected String doInBackground(String... urls) {
            String output = "";

            if (classType.equals("Course")){


                output = fetchCourseData();

            }else if(classType.equals("Department")){
                output = fetchDepartmentData();
            }

            return output;
        }


        protected void onPostExecute(String result) {

            TextView infoDescriptionView = (TextView)findViewById(R.id.infoDes);
            infoDescriptionView.setMovementMethod(new ScrollingMovementMethod());
            infoDescriptionView.setText(result);
        }

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

            InsightDatabaseModel.Building currentBuilding = mDatabase.getBuilding(inforID);
            String infoTitle = currentBuilding.getResourceTitle();


            String url = "https://www.fandm.edu/map/stager-hall";


            if(currentBuilding.getBuildingDescription() == null){
                try{
                    Document doc = Jsoup.connect(url).get();

                    Element mainBody = doc.select("[data-slug = 'stager-hall:body']").first();
                    Element mainContent = mainBody.getElementsByTag("p").first();

                    output = mainContent.text();
                    currentBuilding.setBuildingDescription(output);

                    Elements links = mainBody.getElementsByTag("a");

                }catch (IOException e){
                    e.printStackTrace();
                }
            }else{
                output = currentBuilding.getBuildingDescription();

            }

            return output;
        }

        private String fetchDepartmentData(){

            //TODO implement this
            String output = "";

            InsightDatabaseModel.Department currentDepartment = mDatabase.getDepartment(inforID);
            String infoTitle = currentDepartment.getResourceTitle();
            String url = "https://www.fandm.edu/" + mDatabase.getDepartURL(currentDepartment.getDepartmentName()) + "/courses";


//            if(currentDepartment.getDepartmentName()== null){
//                try{
//                    Document doc = Jsoup.connect(url).get();
//                    Elements columns = doc.getElementsByClass("apos-rich-text");
//
//                    for(Element body: columns){
//                        if(body.text().contains(infoTitle)){
//                            Elements columnLists = body.getElementsByTag("p");
//
//                            for(Element listItem: columnLists){
//                                if(listItem.text().contains(String.valueOf(courseID)) && listItem.text().contains(infoTitle)){
//                                    Element des = listItem.nextElementSibling();
//                                    output = des.text();
//
//                                    //store the course description to the local database
//                                    currentCourse.setCourseDescription(output);
//                                }
//                            }
//                        };
//                    }
//
//                }catch (IOException e){
//                    e.printStackTrace();
//                }
//            }else{
//                output = currentCourse.getCourseDescription();
//                Log.d("Progess: ", "Data already stored");
//            }

            return output;
        }

        private String fetchDataTest(){
            String output = "";


            String url = "https://www.fandm.edu/computer-science/courses";



            try{
                Document doc = Jsoup.connect(url).get();
                Elements columns = doc.getElementsByAttributeValue("data-nav", "Curriculum Overview");

                for(Element body: columns){
                    Elements contents = body.getElementsByClass("apos-rich-text");
                    for(Element content: contents){
                        if(content.hasText()){
                            output += content.text();
                        }
                    }
                }

            }catch (IOException e){
                e.printStackTrace();
            }


            return output;

        }

    }

}
