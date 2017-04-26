package edu.fandm.ztang.insightfm;

import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.fandm.ztang.insightfm.Models.InsightDatabaseModel;
import edu.fandm.ztang.insightfm.Models.InsightSingletonDatabase;

public class bookOnSaleActivity extends BaseActivity {


    FirebaseDatabase fireBase;
    InsightSingletonDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_on_sale);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        updateProfile();

        //get instance of the firebase and insightFM database
        mDatabase = InsightSingletonDatabase.getInstance(this);
        fireBase = FirebaseDatabase.getInstance();


        ArrayAdapter<String> adpater = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mDatabase.getAllSellingItemsTitles());
        ListView listingBooks = (ListView)findViewById(R.id.bookListing);
        listingBooks.setAdapter(adpater);
    }


}
