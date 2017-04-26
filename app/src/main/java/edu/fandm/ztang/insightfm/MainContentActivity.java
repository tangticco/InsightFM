package edu.fandm.ztang.insightfm;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import edu.fandm.ztang.insightfm.Models.InsightDatabaseModel;
import edu.fandm.ztang.insightfm.Models.InsightSingletonDatabase;
import edu.fandm.ztang.insightfm.Models.PermissionUtils;

public class MainContentActivity extends BaseActivity
        implements  OnMapReadyCallback {


    //Test
    private String TAG = "MainActivity: ";


    //Google map related variables
    private GoogleMap mMap;
    private Map<String, LatLng> mapMarkers = new Hashtable<String, LatLng>();
    private UiSettings mUiSettings;
    private static final int MY_LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int LOCATION_LAYER_PERMISSION_REQUEST_CODE = 2;
    private static final int InternetConnection_REQUEST_CODE = 3;
    private static final int WRITE_TO_EXTERNAL_STORAGE_REQUEST_CODE = 4;
    private boolean mLocationPermissionDenied = false;
    private CheckBox mMyLocationButtonCheckbox;
    private CheckBox mMyLocationLayerCheckbox;


    //search engine database variables
    private InsightSingletonDatabase mDatabase;
    private Context mContext = this;


    //Voice recognition variables
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private ImageButton mSpeakBtn;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_content);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //Update the profile image and related info accoording to currently signed in user
        updateProfile();

        //Initialize a InsightDatabaseModel
        mDatabase = InsightSingletonDatabase.getInstance(mContext);

        //////////////////////////////
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mMyLocationButtonCheckbox = (CheckBox) findViewById(R.id.mylocationbutton_toggle);
        mMyLocationLayerCheckbox = (CheckBox)findViewById(R.id.mylocationlayer_toggle);
        mapMarkers = mDatabase.getMapMarkerLATs();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //initialize voice input recognition
        mSpeakBtn = (ImageButton) findViewById(R.id.speechButton);
        mSpeakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });

    }





    ////////////////////////////////
    /////Map related functions
    ////////////////////////////////

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mUiSettings = mMap.getUiSettings();
        mMap.setMinZoomPreference(16.0f);

        // Add a marker in Sydney and move the camera
        for(InsightDatabaseModel.Building currentBuilding : mDatabase.getBuildings()){
            if(!currentBuilding.getBuildingName().equals("UNK")){
                mMap.addMarker(new MarkerOptions().position(mapMarkers.get(currentBuilding.getBuildingName())).title("Marker of" + currentBuilding.getBuildingName()));
            }

        }

        // Keep the UI Settings state in sync with the checkboxes.
        mUiSettings.setZoomControlsEnabled(isChecked(R.id.zoom_buttons_toggle));
        mUiSettings.setCompassEnabled(isChecked(R.id.compass_toggle));
        mUiSettings.setMyLocationButtonEnabled(isChecked(R.id.mylocationbutton_toggle));
        try{
            mMap.setMyLocationEnabled(isChecked(R.id.mylocationlayer_toggle));
        }catch (SecurityException e){
            e.printStackTrace();
        }

        mUiSettings.setScrollGesturesEnabled(isChecked(R.id.scroll_toggle));
        mUiSettings.setZoomGesturesEnabled(isChecked(R.id.zoom_gestures_toggle));
        mUiSettings.setTiltGesturesEnabled(isChecked(R.id.tilt_toggle));
        mUiSettings.setRotateGesturesEnabled(isChecked(R.id.rotate_toggle));


        mMap.moveCamera(CameraUpdateFactory.newLatLng(mapMarkers.get("STA")));


        if(mDatabase.isMapNeedChange()){
            mapChange();
        }
    }

    private void mapChange(){
        mMap.moveCamera(CameraUpdateFactory.zoomTo(20.0f));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mDatabase.getNewMapLocation()));
        mDatabase.setMapNeedChange(false);
    }

    /**
     * Checks if the map is ready (which depends on whether the Google Play services APK is
     * available. This should be called prior to calling any methods on GoogleMap.
     */
    private boolean checkReady() {
        if (mMap == null) {
            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void setMyLocationButtonEnabled(View v) {
        if (!checkReady()) {
            return;
        }
        // Enables/disables the my location button (this DOES NOT enable/disable the my location
        // dot/chevron on the map). The my location button will never appear if the my location
        // layer is not enabled.
        // First verify that the location permission has been granted.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mUiSettings.setMyLocationButtonEnabled(mMyLocationButtonCheckbox.isChecked());
        } else {
            // Uncheck the box and request missing location permission.
            mMyLocationButtonCheckbox.setChecked(false);
            requestLocationPermission(MY_LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    public void setMyLocationLayerEnabled(View v) {
        if (!checkReady()) {
            return;
        }
        // Enables/disables the my location layer (i.e., the dot/chevron on the map). If enabled, it
        // will also cause the my location button to show (if it is enabled); if disabled, the my
        // location button will never show.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(mMyLocationLayerCheckbox.isChecked());
        } else {
            // Uncheck the box and request missing location permission.
            mMyLocationLayerCheckbox.setChecked(false);
            PermissionUtils.requestPermission(this, LOCATION_LAYER_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, false);
        }
    }

    public void setZoomButtonsEnabled(View v) {
        if (!checkReady()) {
            return;
        }
        // Enables/disables the zoom controls (+/- buttons in the bottom-right of the map for LTR
        // locale or bottom-left for RTL locale).
        mUiSettings.setZoomControlsEnabled(((CheckBox) v).isChecked());
    }

    public void setCompassEnabled(View v) {
        if (!checkReady()) {
            return;
        }
        // Enables/disables the compass (icon in the top-left for LTR locale or top-right for RTL
        // locale that indicates the orientation of the map).
        mUiSettings.setCompassEnabled(((CheckBox) v).isChecked());
    }

    /**
     * Returns whether the checkbox with the given id is checked.
     */
    private boolean isChecked(int id) {
        return ((CheckBox) findViewById(id)).isChecked();
    }

    public void setScrollGesturesEnabled(View v) {
        if (!checkReady()) {
            return;
        }
        // Enables/disables scroll gestures (i.e. panning the map).
        mUiSettings.setScrollGesturesEnabled(((CheckBox) v).isChecked());
    }

    public void setZoomGesturesEnabled(View v) {
        if (!checkReady()) {
            return;
        }
        // Enables/disables zoom gestures (i.e., double tap, pinch & stretch).
        mUiSettings.setZoomGesturesEnabled(((CheckBox) v).isChecked());
    }

    public void setTiltGesturesEnabled(View v) {
        if (!checkReady()) {
            return;
        }
        // Enables/disables tilt gestures.
        mUiSettings.setTiltGesturesEnabled(((CheckBox) v).isChecked());
    }

    public void setRotateGesturesEnabled(View v) {
        if (!checkReady()) {
            return;
        }
        // Enables/disables rotate gestures.
        mUiSettings.setRotateGesturesEnabled(((CheckBox) v).isChecked());
    }

    ////////////////////////////////
    /////Controller class
    ////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////Voice Recognition Functions
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);



                    processSpeechWordResult(result.get(0));

                }
                break;
            }

        }
    }


    public void processSpeechWordResult(String searchWord){

        mDatabase.getSpeechCommand = true;
        String[] splited = searchWord.split("\\s+");

        String commandCategory = splited[0];

        Log.d("Progess: ", "here");
        if(searchWord.contains("search") || searchWord.contains("find")){
            Intent intent = new Intent(this, SearchActivity.class);
            Bundle b = new Bundle();

            b.putString("SearchWord", splited[splited.length-1]);
            intent.putExtras(b);
            startActivity(intent);
            Log.d("Progess: ", "here");
        }else if(searchWord.contains("sell") || searchWord.contains("list")){
            Intent intent = new Intent(this, SellBookActivity.class);
            startActivity(intent);
        }else if(searchWord.contains("open")){
            if(searchWord.contains("sell")){
                Intent intent = new Intent(this, SellBookActivity.class);
                startActivity(intent);
            }else if(searchWord.contains("account")){
                Intent intent = new Intent(this, AccountProfileActivity.class);
                startActivity(intent);
            }else if(searchWord.contains("search")) {
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
            }else if(searchWord.contains("list")){
                Intent intent = new Intent(this, bookOnSaleActivity.class);
                startActivity(intent);
            }
        }

    }



    ////////////////////////////////
    /////Helper functions
    ////////////////////////////////



    /**
     * Get the permission of writing to external storage
     */
    public void getPermissions(){

        String[] perms = new String[]{Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(this, perms, 1);

    }

    /**
     * Requests the fine location permission. If a rationale with an additional explanation should
     * be shown to the user, displays a dialog that triggers the request.
     */
    public void requestLocationPermission(int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Display a dialog with rationale.
            PermissionUtils.RationaleDialog
                    .newInstance(requestCode, false).show(
                    getSupportFragmentManager(), "dialog");
        } else {
            // Location permission has not been granted yet, request it.
            PermissionUtils.requestPermission(this, requestCode,
                    Manifest.permission.ACCESS_FINE_LOCATION, false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == MY_LOCATION_PERMISSION_REQUEST_CODE) {
            // Enable the My Location button if the permission has been granted.
            if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                mUiSettings.setMyLocationButtonEnabled(true);
                mMyLocationButtonCheckbox.setChecked(true);
            } else if (requestCode == LOCATION_LAYER_PERMISSION_REQUEST_CODE) {
                // Enable the My Location layer if the permission has been granted.
                if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    try{
                        mMap.setMyLocationEnabled(true);
                    }catch (SecurityException e){
                        e.printStackTrace();
                    }

                    mMyLocationLayerCheckbox.setChecked(true);
                } else {
                    mLocationPermissionDenied = true;
                }
            }
        }
    }

    @Override
    protected void onResumeFragments(){
        super.onResumeFragments();
        if (mLocationPermissionDenied) {
            PermissionUtils.PermissionDeniedDialog
                    .newInstance(false).show(getSupportFragmentManager(), "dialog");
            mLocationPermissionDenied = false;
        }
    }
}
