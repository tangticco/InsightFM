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


        ArrayList<InsightDatabaseModel.Sellingitem> items = mDatabase.getAllSellingItems();
        ArrayList<String> titles = new ArrayList<>();

        if(items.isEmpty()){
            //Get the allsellingitem arraylist from the server
            final DatabaseReference allSellingItemRef = fireBase.getReference("allSellingItems");
            allSellingItemRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<InsightDatabaseModel.Sellingitem> allitems = new ArrayList<InsightDatabaseModel.Sellingitem>();
                    Log.d("Progess: ", "here");
                    if(dataSnapshot.getValue() != null){
                        for(int i = 0; i < dataSnapshot.getChildrenCount(); i++){
                            InsightDatabaseModel.Sellingitem newSellingitem = (InsightDatabaseModel.Sellingitem)dataSnapshot.child(String.valueOf(i)).getValue();
                            allitems.add(newSellingitem);
                        }
                        mDatabase.setAllSellingItems(allitems);
                        Log.d("Progess: ", "Successfully get data");
                    }else{
                        ArrayList<InsightDatabaseModel.Sellingitem> allSellingItems = new ArrayList<InsightDatabaseModel.Sellingitem>();
                        mDatabase.setAllSellingItems(allSellingItems);

                        Log.d("Progess: ", "Unsuccessfully get data");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            //check again if empty
            if(mDatabase.getAllSellingItems().isEmpty()){
                titles.add("There is no book listing");
            }
        }else{

            for(int i = 0; i < items.size(); i++){
                titles.add(items.get(i).getBelongedBook().getBookTitle());
            }


        }

        



        ArrayAdapter<String> adpater = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, titles);
        ListView listingBooks = (ListView)findViewById(R.id.bookListing);
        listingBooks.setAdapter(adpater);
    }


}
