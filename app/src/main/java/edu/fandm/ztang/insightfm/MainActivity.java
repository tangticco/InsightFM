package edu.fandm.ztang.insightfm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.gesture.Gesture;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import edu.fandm.ztang.insightfm.Models.InsightDatabaseModel;
import edu.fandm.ztang.insightfm.Models.InsightSingletonDatabase;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    //Google map related variables
    private GoogleMap mMap;
    private Map<String, LatLng>  mapMarkers = new Hashtable<String, LatLng>();


    //search engine database variables
    private InsightSingletonDatabase mDatabase;
    private Context mContext = this;

    
    //variables for searching result
    private ArrayList<String> searchResults;
    private  ArrayList<Integer> searchResultsAccessCodes;

    private final GestureDetector searchListGDT = new GestureDetector(new SearchResultGestureListener());
    private final GestureDetector searchBarGDT = new GestureDetector(new SearchBarGestureListener());



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get netword permission
        ConnectivityManager check = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = check.getAllNetworkInfo();
        for (int i = 0; i<info.length; i++){
            if (info[i].getState() == NetworkInfo.State.CONNECTED){
                Toast.makeText(mContext, "Internet is connected", Toast.LENGTH_SHORT).show();
            }
        }


        //Initialize a InsightDatabaseModel
        mDatabase = InsightSingletonDatabase.getInstance(mContext);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapMarkers = mDatabase.getMapMarkerLATs();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(16.0f);

        // Add a marker in Sydney and move the camera
        for(InsightDatabaseModel.Building currentBuilding : mDatabase.getBuildings()){
            if(!currentBuilding.getBuildingName().equals("UNK")){
                mMap.addMarker(new MarkerOptions().position(mapMarkers.get(currentBuilding.getBuildingName())).title("Marker of" + currentBuilding.getBuildingName()));
            }

        }


        mMap.moveCamera(CameraUpdateFactory.newLatLng(mapMarkers.get("STA")));

    }

    ////////////////////////////////
    /////Controller class
    ////////////////////////////////
    public void showSearchBar(View v){
        //set up the listener for textEdit

        //set up the listener for textEdit
        final EditText searchBar = (EditText)findViewById(R.id.searchbar);
        searchBar.setOnFocusChangeListener(new SearchBarFocusChangeListener());
        searchBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return searchBarGDT.onTouchEvent(event);
            }
        });



        final ListView searchResultListView = (ListView)findViewById(R.id.searchResult);
        searchResultListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                return searchListGDT.onTouchEvent(event);
            }
        });

        //Automatically hide the keyboard when the user scoll the list view
        searchResultListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                InputMethodManager im = (InputMethodManager)getSystemService(mContext.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(searchResultListView.getWindowToken(), 0);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        //set up search result click listner
        searchResultListView.setOnItemClickListener(new SearchResultListOnItemClicked());


        searchBar.setVisibility(View.VISIBLE);
        v.setVisibility(View.GONE);
    }


    ////////////////////////////////
    /////Listener class
    ////////////////////////////////
    /**
     * A TextWatcher class for searchbar
     */
    class SearchBarTextChangedListener implements TextWatcher{

        //Called everytime when the user type new things in to the search bar
        public void afterTextChanged(Editable s) {

            //find the ListView
            final ListView searchResultListView = (ListView)findViewById(R.id.searchResult);

            String searchWord = s.toString();
            searchResults = new ArrayList<>();
            searchResultsAccessCodes = new ArrayList<>();

            if(searchWord.replaceAll("\\s","").equals("")){
                searchResults.add("There is no such course");
                searchResultsAccessCodes.add(-1);
            }else{
                searchResultsAccessCodes = mDatabase.searchCourse(searchWord);
                if(searchResultsAccessCodes.isEmpty()){
                    searchResults.add("There is no result");
                    searchResultsAccessCodes.add(-1);
                }else{

                    for(int i = 0; i < searchResultsAccessCodes.size(); i++){
                        searchResults.add(mDatabase.getCourse(searchResultsAccessCodes.get(i)).getTitle());
                    }


                }
            }


            final ArrayAdapter adapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, searchResults);
            searchResultListView.setAdapter(adapter);




        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            final ListView searchResultListView = (ListView)findViewById(R.id.searchResult);
            searchResultListView.setVisibility(View.VISIBLE);
        }


    }

    /**
     * A Focus listener class for searchbar
     */
    class SearchBarFocusChangeListener implements View.OnFocusChangeListener{
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            ListView searchResult = (ListView)findViewById(R.id.searchResult);
            EditText searchBar = (EditText)findViewById(R.id.searchbar);
            if(hasFocus){
                Toast.makeText(getApplicationContext(), "got the focus", Toast.LENGTH_LONG).show();
                searchBar.addTextChangedListener(new SearchBarTextChangedListener());

                //TODO make this animation better
//                Animation a = AnimationUtils.loadAnimation(mContext, R.anim.slideup);
//                searchBar.startAnimation(a);
                searchResult.setVisibility(View.VISIBLE);
            }else {
                Toast.makeText(getApplicationContext(), "lost the focus", Toast.LENGTH_LONG).show();
                searchResult.setVisibility(View.GONE);
            }
        }
    }

    /**
     * A item click listener class
     */
    class SearchResultListOnItemClicked implements AdapterView.OnItemClickListener{
        /**
         * Callback method to be invoked when an item in this AdapterView has
         * been clicked.
         * <p>
         * Implementers can call getItemAtPosition(position) if they need
         * to access the data associated with the selected item.
         *
         * @param parent The AdapterView where the click happened.
         * @param view The view within the AdapterView that was clicked (this
         *            will be a view provided by the adapter)
         * @param position The position of the view in the adapter.
         * @param id The row id of the item that was clicked.
         */
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            if(searchResultsAccessCodes.get(position) != -1){
                Intent intent = new Intent(mContext, ExtendedInfoWindowActivity.class);

                //pass information to the extended info window activity
                Bundle b = new Bundle();
                b.putInt("INFOID", searchResultsAccessCodes.get(position));
                b.putString("ClassType",  "Course"); //TODO change it later
                intent.putExtras(b);


                startActivity(intent);
            }
        }
    }

    /**
     * A searchresult listview gesture listener class
     */
    private class SearchResultGestureListener extends GestureDetector.SimpleOnGestureListener{
        private final int SWIPE_MIN_DISTANCE = 120;
        private final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                // Right to left, your code here
                return true;
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) >  SWIPE_THRESHOLD_VELOCITY) {
                ListView searchResult = (ListView)findViewById(R.id.searchResult);
                Toast.makeText(getApplicationContext(), "Searchresult byebye", Toast.LENGTH_LONG).show();
                searchResult.setVisibility(View.GONE);

                return true;
            }
            if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                // Bottom to top, your code here
                return true;
            } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                // Top to bottom, your code here
                return true;
            }
            return false;
        }

    }

    private class SearchBarGestureListener extends GestureDetector.SimpleOnGestureListener{
        private final int SWIPE_MIN_DISTANCE = 120;
        private final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                // Right to left, your code here
                return true;
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) >  SWIPE_THRESHOLD_VELOCITY) {

                EditText searchBar = (EditText)findViewById(R.id.searchbar);
                searchBar.setVisibility(View.GONE);
                Button showSearchBarButton = (Button)findViewById(R.id.searchButton);
                showSearchBarButton.setVisibility(View.VISIBLE);

                return true;
            }
            if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                // Bottom to top, your code here
                return true;
            } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                // Top to bottom, your code here
                return true;
            }
            return false;
        }

    }


    ////////////////////////////////
    /////Helper functions
    ////////////////////////////////

    /**
     * Get the permission of writing to external storage
     */
    public void getPermissions(){

        String[] perms = new String[]{Manifest.permission.INTERNET};
        ActivityCompat.requestPermissions(this, perms, 1);

    }

}
