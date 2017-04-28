package edu.fandm.ztang.insightfm;

import android.content.Context;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

import edu.fandm.ztang.insightfm.Models.InsightDatabaseModel;
import edu.fandm.ztang.insightfm.Models.InsightSingletonDatabase;

public class SellBookActivity extends BaseActivity {


    //User entered value
    String bookTitle = "";
    String bookAuthor = "";
    String bookISBN = "";
    int bookCourseID = -1;
    double sellPrice = -1;
    double origPrice = -1;
    int itemCondition = -1;
    String itemDescription = "";


    //InsightFM Database
    InsightSingletonDatabase mDatabase;



    //Firebase Auth
    private FirebaseAuth mAuth;

    //Firebase Database
    FirebaseDatabase fireBase;


    //UI
    ListView course_searchResultListview;
    EditText searchCourseBar;


    Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_book);

        mDatabase = InsightSingletonDatabase.getInstance(this);
        mAuth = FirebaseAuth.getInstance();
        fireBase = FirebaseDatabase.getInstance();


        //Update UI
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        updateProfile();


        course_searchResultListview = (ListView)findViewById(R.id.course_listview);
        searchCourseBar = (EditText)findViewById(R.id.course_edittext);
        searchCourseBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    searchCourseBar.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                            if(s.toString().equals("")){

                            }else{
                                course_searchResultListview.setVisibility(View.VISIBLE);
                                final ArrayList<Integer> infoIDList = mDatabase.searchCourse(s.toString());
                                ArrayList<String> searchResultStringList = new ArrayList<String>();
                                for(int i = 0; i < infoIDList.size(); i++){
                                    searchResultStringList.add(mDatabase.getCourse(infoIDList.get(i)).getResourceTitle());
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, searchResultStringList);
                                course_searchResultListview.setAdapter(adapter);
                                course_searchResultListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        bookCourseID = infoIDList.get(position);
                                        InsightDatabaseModel.Course selectedCourse = mDatabase.getCourse(bookCourseID);
                                        searchCourseBar.setText(selectedCourse.getTitle());
                                        course_searchResultListview.setVisibility(View.GONE);
                                    }
                                });

                            }

                        }
                    });
                }
            }
        });
    }


    /**
     * Sumbit the book details
     * @param v
     */
    public void sumbit(View v){

        boolean infoComplete = true;

        //get the book title
        EditText bookTitleEditText = (EditText)findViewById(R.id.bookTitle_edittext);
        bookTitle = bookTitleEditText.getText().toString();

        if(bookTitle == null || bookTitle.replaceAll("\\s", "").equals("")){

            //Handle situation when book title is null
            infoComplete = false;

        }


        InsightDatabaseModel.Course selectedCourse = mDatabase.getCourse(bookCourseID);

        if(bookCourseID == -1){

            //Handle situation when course is not selected
            infoComplete = false;
        }


        EditText sellPriceEditText = (EditText)findViewById(R.id.sellPrice_edittext);
        sellPrice = Double.valueOf(sellPriceEditText.getText().toString());

        if(sellPrice < 0){

            infoComplete = false;
        }

        EditText origPriceEditText = (EditText)findViewById(R.id.origPrice_edittext);
        origPrice = Double.valueOf(origPriceEditText.getText().toString());

        if(origPrice < 0){

            infoComplete = false;
        }

        EditText itemConditionEditText = (EditText)findViewById(R.id.itemCondition_edittext);
        itemCondition = Integer.valueOf(itemConditionEditText.getText().toString());

        if(itemCondition < 0){
            infoComplete = false;
        }

        EditText itemDescriptionEditText = (EditText)findViewById(R.id.itemDes_edittext);
        itemDescription = itemDescriptionEditText.getText().toString();

        if(itemDescription == null || itemDescription.replaceAll("\\s", "").equals("")){

            infoComplete = false;
        }


        //get other type-ins
        EditText itemAuthor = (EditText)findViewById(R.id.itemAuthor_edittext);
        bookAuthor = itemAuthor.getText().toString();
        if(bookAuthor == null || bookAuthor.replaceAll("\\s", "").equals("")){
            bookAuthor ="";
        }

        EditText itemISBN = (EditText)findViewById(R.id.itemISBN_edittext);
        bookISBN = itemISBN.getText().toString();
        if(bookISBN == null ||  bookISBN.replaceAll("\\s", "").equals("")){
            bookISBN ="";
        }

        if(infoComplete == true){

            //Create the new book and new selling item
            InsightDatabaseModel.Book newBook = new InsightDatabaseModel.Book(bookTitle, selectedCourse.getInfoID());
            newBook.setBookAuthor(bookAuthor);
            newBook.setISBN(bookISBN);
            final InsightDatabaseModel.Sellingitem newItem  = new InsightDatabaseModel.Sellingitem(mDatabase.getCurrentUser().getUserID(), sellPrice, origPrice, itemCondition, itemDescription, newBook);

            //Add this to the local database and update firebase
            mDatabase.addNewSellingItem(newItem);
            final DatabaseReference allSellingItemRef = fireBase.getReference("BookListing");
            DatabaseReference newItemPost = allSellingItemRef.push();
            newItemPost.setValue(newItem);

            //added the listed item to the user profile
            mDatabase.getCurrentUser().addNewSellingItemREF(newItemPost.getKey());
            mDatabase.updateUserToFireBase();


            Toast.makeText(mContext, "Your Items Was Listed!", Toast.LENGTH_SHORT).show();
        }


    }
}
