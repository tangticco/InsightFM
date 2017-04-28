package edu.fandm.ztang.insightfm.Models;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

import edu.fandm.ztang.insightfm.R;

/**
 * Created by zhuofantang on 4/27/17.
 */

public class ExtendedInfoCourseSellingItemCustomAdapter extends BaseAdapter{


    private ArrayList<InsightDatabaseModel.Sellingitem> sellingitems;
    private static LayoutInflater inflater=null;
    private InsightSingletonDatabase mDatabase;



    public ExtendedInfoCourseSellingItemCustomAdapter(Context context, ArrayList<InsightDatabaseModel.Sellingitem> sellingitems) {

        this.sellingitems = sellingitems;
        inflater = LayoutInflater.from(context);
        mDatabase = InsightSingletonDatabase.getInstance(context);
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return sellingitems.size();
    }

    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        if(convertView == null){
            vi = inflater.inflate(R.layout.booklisting_row, null);
        }

        InsightDatabaseModel.Sellingitem currentItem = sellingitems.get(position);
        TextView bookTitleTextView = (TextView)vi.findViewById(R.id.booktitle_row);
        final TextView bookSellerTextView = (TextView)vi.findViewById(R.id.bookSeller_row);
        TextView bookSellingPriceTextView = (TextView)vi.findViewById(R.id.sellingPrice_row);
        TextView bookOriginalPriceTextView = (TextView)vi.findViewById(R.id.origPrice_row);



        bookTitleTextView.setText(currentItem.getBelongedBook().getBookTitle());

        bookSellingPriceTextView.setText(String.valueOf(currentItem.getSellingPrice()));
        bookOriginalPriceTextView.setText(String.valueOf(currentItem.originalPrice));


        DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference("Users").child(currentItem.getUserKey());
        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bookSellerTextView.setText(dataSnapshot.getValue(InsightDatabaseModel.User.class).getUserDisplayedName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return vi;

    }


}
