package edu.fandm.ztang.insightfm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.params.Face;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import edu.fandm.ztang.insightfm.Models.InsightDatabaseModel;
import edu.fandm.ztang.insightfm.Models.InsightSingletonDatabase;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @VisibleForTesting
    public ProgressDialog mProgressDialog;


    //test
    private String TAG = "Base: ";

    //InsigtFM Database
    InsightSingletonDatabase mDataBase;


    //Firebase Auth
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        mDataBase = InsightSingletonDatabase.getInstance(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }


    /**
     * Update the profile image and related info accoording to currently signed in user
     */
    public void updateProfile(){
        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        final FirebaseUser user = mAuth.getCurrentUser();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);


        //get the user's public profile
        View headerLayout = navigationView.getHeaderView(0);
        final TextView userName = (TextView)headerLayout.findViewById(R.id.userName);
        final TextView userEmail = (TextView)headerLayout.findViewById(R.id.userEmailTitle);
        final ImageView userProfileImage = (ImageView)headerLayout.findViewById(R.id.userProfileImage);

        if(mDataBase.isUserSignIn){
            userName.setText(mDataBase.getCurrentUser().getUserDisplayedName());
            userEmail.setText(mDataBase.getCurrentUser().getUserEmail());

            Thread downloadThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL aURL = new URL(mDataBase.getCurrentUser().getUserPhotoUri());
                        URLConnection conn = aURL.openConnection();
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        BufferedInputStream bis = new BufferedInputStream(is);
                        final Bitmap bm = BitmapFactory.decodeStream(bis);
                        bis.close();
                        is.close();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                userProfileImage.setImageBitmap(bm);
                            }
                        });

                    } catch (IOException e) {
                        Log.e(TAG, "Error getting bitmap", e);
                    }
                }
            });
            downloadThread.start();
        }else{

            //check if the current user is not write into the local database
            if (user != null) {
                // User is signed in

                //update local database indicator
                mDataBase.isUserSignIn = true;

                final String userID = user.getUid();
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference currentUserInDatabase = firebaseDatabase.getReference("Users").child(userID);
                //check if the user is already in the firebase database
                currentUserInDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            mDataBase.setCurrentUser(dataSnapshot.getValue(InsightDatabaseModel.User.class));


                            Log.d("User Written", "Success");
                        }else{
                            String photoURI = "http://i2.muimg.com/567571/1c07fb459f845b8c.png";
                            if(user.getPhotoUrl() != null){
                                photoURI = user.getPhotoUrl().toString();
                            }

                            InsightDatabaseModel.User newUser = new InsightDatabaseModel.User(user.getDisplayName(), user.getEmail(), photoURI, user.getUid());
                            DatabaseReference FirebaseDataBaseRef = FirebaseDatabase.getInstance().getReference();
                            FirebaseDataBaseRef.child("Users").child(userID).setValue(newUser);
                            mDataBase.setCurrentUser(newUser);


                            Log.d("User updated: ", "Updated");
                        }



                        userName.setText(mDataBase.getCurrentUser().getUserDisplayedName());
                        userEmail.setText(mDataBase.getCurrentUser().getUserEmail());




                        Thread downloadThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    String photoURI = "http://i2.muimg.com/567571/1c07fb459f845b8c.png";
                                    if(user.getPhotoUrl() != null){
                                        photoURI = user.getPhotoUrl().toString();
                                    }
                                    URL aURL = new URL(photoURI);
                                    URLConnection conn = aURL.openConnection();
                                    conn.connect();
                                    InputStream is = conn.getInputStream();
                                    BufferedInputStream bis = new BufferedInputStream(is);
                                    final Bitmap bm = BitmapFactory.decodeStream(bis);
                                    bis.close();
                                    is.close();

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            userProfileImage.setImageBitmap(bm);
                                        }
                                    });

                                } catch (IOException e) {
                                    Log.e(TAG, "Error getting bitmap", e);
                                }
                            }
                        });
                        downloadThread.start();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }else{

                //User is not login

            }
        }

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_content, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_search) {

            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);


        } else if (id == R.id.nav_map) {

            Intent intent = new Intent(this, MainContentActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        } else if (id == R.id.nav_book_onSale) {

            Intent intent = new Intent(this, bookOnSaleActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_sell_book) {

            if(mDataBase.isUserSignIn){
                Intent intent = new Intent(this, SellBookActivity.class);
                startActivity(intent);
            }else{
                //if the user is not sign in, redirect him to login in page

                mDataBase.isReuqiredLogin = true;
                Intent intent = new Intent(this, FacebookLoginActivity.class);
                startActivity(intent);
            }


        } else if (id == R.id.nav_contact) {
            Intent intent = new Intent(this, ContactsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void newUserSignIn(View v){
        Intent intent = new Intent(this, EmailPasswordActivity.class);
        startActivity(intent);
    }


    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    public void composeEmail(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}