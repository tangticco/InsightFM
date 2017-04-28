package edu.fandm.ztang.insightfm;

import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.fandm.ztang.insightfm.Models.InsightDatabaseModel;

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


    //User
    InsightDatabaseModel.User currentUser;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_profile);

        //Linked the navigation listener
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //Update the profile image and related info accoording to currently signed in user
        updateProfile();

        //get the current user
        currentUser = mDataBase.getCurrentUser();

        userNameEditText = (EditText) findViewById(R.id.userName_edit);
        userFullNameEditText = (EditText) findViewById(R.id.userFullName_edit);
        userEmailEditText = (EditText) findViewById(R.id.userEmail_edit);
        userPhoneEditText = (EditText) findViewById(R.id.userPhone_edit);
        userClassEditText = (EditText) findViewById(R.id.userClass_edit);
        userMajorEditText = (EditText) findViewById(R.id.userMajor_edit);

        if(currentUser.getUserDisplayedName() != null){
            userNameEditText.setHint(currentUser.getUserDisplayedName());
        }

        if(currentUser.getUserEmail() != null){
            userEmailEditText.setHint(currentUser.getUserEmail());
        }




    }

    public void submitChange(View v){

        if(userNameEditText.getText().toString() != null && userNameEditText.getText().toString() != "" ){
            currentUser.setUserDisplayedName(userNameEditText.getText().toString());
        }

        if(userFullNameEditText.getText().toString() != null && userFullNameEditText.getText().toString() != ""){
            currentUser.setUserFullName(userFullNameEditText.getText().toString());
        }

        if(userEmailEditText.getText().toString() != null && userEmailEditText.getText().toString() != ""){
            currentUser.setUserEmail(userEmailEditText.getText().toString());
        }

        if(userPhoneEditText.getText().toString() != null && userPhoneEditText.getText().toString() != ""){
            currentUser.setUserPhoneNumber(userPhoneEditText.getText().toString());
        }

        if(userClassEditText.getText().toString() != null && userClassEditText.getText().toString() != ""){
            currentUser.setUserClass(userClassEditText.getText().toString());
        }

        if(userMajorEditText.getText().toString() != null && userMajorEditText.getText().toString() != ""){
            currentUser.setUserMajor(userMajorEditText.getText().toString());
        }



        mDataBase.updateUserToFireBase();




    }
}
