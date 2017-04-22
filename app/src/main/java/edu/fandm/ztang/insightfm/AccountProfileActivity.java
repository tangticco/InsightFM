package edu.fandm.ztang.insightfm;

import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountProfileActivity extends BaseActivity {
    
    
    EditText userNameEditText;
    EditText userFullNameEditText ;
    EditText userEmailEditText;
    EditText userPhoneEditText ;
    EditText userClassEditText ;
    EditText userMajorEditText;

    //Firebase Auth
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_profile);

        //Linked the navigation listener
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //Update the profile image and related info accoording to currently signed in user
        updateProfile();

        //update account info

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        final FirebaseUser user = mAuth.getCurrentUser();


    }
}
