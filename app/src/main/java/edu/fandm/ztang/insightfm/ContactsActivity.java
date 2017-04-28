package edu.fandm.ztang.insightfm;

import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ContactsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //Update the profile image and related info accoording to currently signed in user
        updateProfile();
    }


    public void contactPhone(View v){
        Button phone = (Button)findViewById(v.getId());
        dialPhoneNumber(phone.getText().toString());
    }






}
