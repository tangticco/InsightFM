package edu.fandm.ztang.insightfm;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import edu.fandm.ztang.insightfm.Models.InsightDatabaseModel;
import edu.fandm.ztang.insightfm.Models.InsightSingletonDatabase;

public class SearchActivity extends AppCompatActivity {

    //search engine database variables
    private InsightSingletonDatabase mDatabase;
    private Context mContext = this;

    //variables for searching result
    private ArrayList<String> searchResults;
    private  ArrayList<Integer> searchResultsAccessCodes;


    //customized gesture detectors
    private final GestureDetector searchListGDT = new GestureDetector(new SearchResultGestureListener());
    private final GestureDetector searchBarGDT = new GestureDetector(new SearchBarGestureListener());

    //UI instances
    private EditText searchBar;
    private ListView searchResultListView;

    //Voice recognition variables
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private ImageButton mSpeakBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        //Set up the navigation tool bar
        //TODO implement the toolbar (back to main activity)
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get an instance of the database
        mDatabase = InsightSingletonDatabase.getInstance(mContext);


        //////////////////////////////
        //set up the search bar
        searchBar = (EditText)findViewById(R.id.searchbar);
        searchBar.setOnFocusChangeListener(new SearchBarFocusChangeListener());
        searchBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return searchBarGDT.onTouchEvent(event);
            }
        });

        //////////////////////////////
        //set up search result listview
        searchResultListView = (ListView)findViewById(R.id.searchResult);
        searchResultListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                //handle gesture event on listview
                return searchListGDT.onTouchEvent(event);
            }
        });
        //Automatically hide the keyboard when the user scoll the list view
        searchResultListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                InputMethodManager im = (InputMethodManager)getSystemService(mContext.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(searchResultListView.getWindowToken(), 0);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        //set up search result click listner
        searchResultListView.setOnItemClickListener(new SearchResultListOnItemClicked());


        //initialize voice input recognition
        mSpeakBtn = (ImageButton) findViewById(R.id.btnSpeak);
        mSpeakBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });


    }
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
                    searchBar.setText(result.get(0));
                }
                break;
            }

        }
    }




    ////////////////////////////////
    /////Listener class
    ////////////////////////////////
    /**
     * A TextWatcher class for searchbar
     */
    class SearchBarTextChangedListener implements TextWatcher {

        //Called everytime when the user type new things in to the search bar
        public void afterTextChanged(Editable s) {

            //find the ListView
            searchResultListView = (ListView)findViewById(R.id.searchResult);

            String searchWord = s.toString();
            searchResults = new ArrayList<>();
            searchResultsAccessCodes = new ArrayList<>();

            if(searchWord.replaceAll("\\s","").equals("")){
                searchResults.add("There is no such course");
                searchResultsAccessCodes.add(-1);
            }else{
                searchResultsAccessCodes = mDatabase.searchInfor(searchWord);
                if(searchResultsAccessCodes.isEmpty()){
                    searchResults.add("There is no result");
                    searchResultsAccessCodes.add(-1);
                }else{
                    searchResults = mDatabase.getSearchResultList();
                }
            }


            final ArrayAdapter adapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, searchResults);
            searchResultListView.setAdapter(adapter);




        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            searchResultListView = (ListView)findViewById(R.id.searchResult);
            searchResultListView.setVisibility(View.VISIBLE);
        }


    }

    /**
     * A Focus listener class for searchbar
     */
    class SearchBarFocusChangeListener implements View.OnFocusChangeListener{
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            searchResultListView = (ListView)findViewById(R.id.searchResult);
            EditText searchBar = (EditText)findViewById(R.id.searchbar);
            if(hasFocus){
                searchBar.addTextChangedListener(new SearchBarTextChangedListener());
                searchResultListView.setVisibility(View.VISIBLE);
            }else {
                Toast.makeText(getApplicationContext(), "lost the focus", Toast.LENGTH_LONG).show();
                searchResultListView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * A item click listener class for search result listview
     */
    class SearchResultListOnItemClicked implements AdapterView.OnItemClickListener{
        /**
         * Callback method to be invoked when an item in this AdapterView has
         * been clicked.
         * <p>
         * Implementers can call getItemAtPosition(position) if they need
         * to access the data associated with the selected item.
         *
         * @param parent The AdapterView where the click happened.
         * @param view The view within the AdapterView that was clicked (this
         *            will be a view provided by the adapter)
         * @param position The position of the view in the adapter.
         * @param id The row id of the item that was clicked.
         */
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            if(searchResultsAccessCodes.get(position) != -1){
                Intent intent = new Intent(mContext, ExtendedInfoWindowActivity.class);

                //pass information to the extended info window activity
                InsightDatabaseModel.FMResource clickedItem = mDatabase.getSearchResultItem(position);
                Bundle b = new Bundle();
                b.putInt("INFOID", searchResultsAccessCodes.get(position));
                b.putInt("Position", position);
                b.putString("ClassType", clickedItem.getClassType() ); //TODO change it later
                intent.putExtras(b);


                startActivity(intent);
            }
        }
    }

    /**
     * A gesture listener search result listview class
     */
    private class SearchResultGestureListener extends GestureDetector.SimpleOnGestureListener{
        private final int SWIPE_MIN_DISTANCE = 120;
        private final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                // Right to left, your code here
                return true;
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) >  SWIPE_THRESHOLD_VELOCITY) {
                searchResultListView = (ListView)findViewById(R.id.searchResult);
                searchResultListView.setVisibility(View.GONE);

                return true;
            }
            if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                // Bottom to top, your code here
                return true;
            } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                // Top to bottom, your code here
                return true;
            }
            return false;
        }

    }

    /**
     * A gesture listener search bar class
     */
    private class SearchBarGestureListener extends GestureDetector.SimpleOnGestureListener{
        private final int SWIPE_MIN_DISTANCE = 120;
        private final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                // Right to left, your code here
                return true;
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) >  SWIPE_THRESHOLD_VELOCITY) {
                searchBar.setText("");
                return true;
            }
            if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                // Bottom to top, your code here
                return true;
            } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                // Top to bottom, your code here
                return true;
            }
            return false;
        }

    }

}
