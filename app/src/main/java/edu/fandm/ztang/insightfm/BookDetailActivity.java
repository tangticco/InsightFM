package edu.fandm.ztang.insightfm;

import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.fandm.ztang.insightfm.Models.InsightDatabaseModel;

public class BookDetailActivity extends BaseActivity {

    TextView bookTitle;
    TextView bookCourse;
    TextView bookSellPrice;
    TextView bookOrigPrice;
    TextView bookCondition;
    TextView bookAuthor;
    TextView bookISBN;
    TextView bookDescrip;


    InsightDatabaseModel.Sellingitem selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        updateProfile();



        selectedItem = mDataBase.getSelectedItem();


        //hooked up UI
        bookTitle = (TextView)findViewById(R.id.bookTitle_place);
        bookCourse = (TextView)findViewById(R.id.bookCourse_place);
        bookSellPrice = (TextView)findViewById(R.id.sellPrice_place);
        bookOrigPrice = (TextView)findViewById(R.id.origPrice_place);
        bookCondition = (TextView)findViewById(R.id.itemCondition_place);
        bookAuthor= (TextView)findViewById(R.id.itemAuthor_place);
        bookISBN = (TextView)findViewById(R.id.itemISBN_place);
        bookDescrip= (TextView)findViewById(R.id.itemDes_place);

        bookTitle.setText(selectedItem.getBelongedBook().getBookTitle());
        bookCourse.setText(mDataBase.getCourse(selectedItem.getBelongedBook().getCourseInfoID()).getTitle());
        bookSellPrice.setText(selectedItem.getSellingPrice().toString());
        bookOrigPrice.setText(selectedItem.getOriginalPrice().toString());
        bookAuthor.setText(selectedItem.getBelongedBook().getBookAuthor());
        bookISBN.setText(selectedItem.getBelongedBook().getISBN());
        bookDescrip.setText(selectedItem.getItemDescription());
        if(selectedItem.getItemCondition() <=5 && selectedItem.getItemCondition() >= 0){
            bookCondition.setText(selectedItem.getItemCondition().toString());
        }else{
            bookCondition.setText("Unknown");
        }

    }

    public void contact(View v){



        if(v.getId() == R.id.contact_by_email){
            final String subject = "About Textbook Sale";
            final String[] addressed = new String[1];

            DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference("Users").child(selectedItem.getUserKey());
            currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String email = dataSnapshot.getValue(InsightDatabaseModel.User.class).getUserEmail();
                    if(email != null && !email.equals("")){
                        addressed[0] = email;

                        composeEmail(addressed, subject);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }else if(v.getId() == R.id.contact_by_phone){

            DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference("Users").child(selectedItem.getUserKey());
            currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String phoneNum = dataSnapshot.getValue(InsightDatabaseModel.User.class).getUserPhoneNumber();
                    if(phoneNum != null && !phoneNum.equals("")){
                        dialPhoneNumber(phoneNum);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

}
