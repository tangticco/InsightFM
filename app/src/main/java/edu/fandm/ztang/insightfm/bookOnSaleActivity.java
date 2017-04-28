package edu.fandm.ztang.insightfm;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.fandm.ztang.insightfm.Models.BookOnSaleCustomAdapter;
import edu.fandm.ztang.insightfm.Models.ExtendedInfoCourseSellingItemCustomAdapter;
import edu.fandm.ztang.insightfm.Models.InsightDatabaseModel;
import edu.fandm.ztang.insightfm.Models.InsightSingletonDatabase;

public class bookOnSaleActivity extends BaseActivity  implements AdapterView.OnItemClickListener{


    FirebaseDatabase fireBase;
    InsightSingletonDatabase mDatabase;
    ArrayList<InsightDatabaseModel.Sellingitem> listedItems;

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


        if(!mDatabase.isMyBookListing){
            listedItems = mDatabase.getAllSellingItems();
            BookOnSaleCustomAdapter adapter = new BookOnSaleCustomAdapter(this, mDatabase.getAllSellingItems());
            ListView listingBooks = (ListView)findViewById(R.id.bookListing);
            listingBooks.setAdapter(adapter);
        }else{
            final ArrayList<InsightDatabaseModel.Sellingitem> myBooks = new ArrayList<>();

            ArrayList<String> bookKeys = mDatabase.getCurrentUser().getSellItemsREFS();
            for(int i = 0; i < bookKeys.size(); i++){
                DatabaseReference myBookREF = fireBase.getReference("BookListing").child(bookKeys.get(i));
                myBookREF.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        myBooks.add(dataSnapshot.getValue(InsightDatabaseModel.Sellingitem.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            listedItems = myBooks;
            ExtendedInfoCourseSellingItemCustomAdapter adapter = new ExtendedInfoCourseSellingItemCustomAdapter(this, myBooks);
            ListView listingBooks = (ListView)findViewById(R.id.bookListing);
            listingBooks.setAdapter(adapter);

            mDatabase.isMyBookListing = false;


        }

        ListView listingBooks = (ListView)findViewById(R.id.bookListing);
        listingBooks.setOnItemClickListener(this);

    }



    //Handle the sell book button
    public void sellBook(View v){

        if(mDataBase.isUserSignIn){
            Intent intent = new Intent(this, SellBookActivity.class);
            startActivity(intent);
        }else{
            //if the user is not sign in, redirect him to login in page

            mDataBase.isReuqiredLogin = true;
            Intent intent = new Intent(this, FacebookLoginActivity.class);
            startActivity(intent);
        }


    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mDatabase.setSelectedItem(listedItems.get(position));

        Intent intent = new Intent(this, BookDetailActivity.class);
        startActivity(intent);

    }
}
