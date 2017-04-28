package edu.fandm.ztang.insightfm.Models;

import android.content.Context;
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

import java.util.ArrayList;

import edu.fandm.ztang.insightfm.R;

/**
 * Created by zhuofantang on 4/28/17.
 */

public class BookOnSaleCustomAdapter extends BaseAdapter {

    private ArrayList<InsightDatabaseModel.Sellingitem> sellingitems;
    private static LayoutInflater inflater=null;
    private InsightSingletonDatabase mDatabase;



    public BookOnSaleCustomAdapter(Context context, ArrayList<InsightDatabaseModel.Sellingitem> sellingitems) {

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
            vi = inflater.inflate(R.layout.book_on_sale_row, null);
        }

        InsightDatabaseModel.Sellingitem currentItem = sellingitems.get(position);
        TextView bookTitleTextView = (TextView)vi.findViewById(R.id.booktitle_row);
        final TextView bookCourseTextView = (TextView)vi.findViewById(R.id.bookCourse_row);
        TextView bookSellingPriceTextView = (TextView)vi.findViewById(R.id.sellingPrice_row);
        TextView bookOriginalPriceTextView = (TextView)vi.findViewById(R.id.origPrice_row);



        bookTitleTextView.setText(currentItem.getBelongedBook().getBookTitle());
        bookCourseTextView.setText(mDatabase.getCourse(currentItem.getBelongedBook().getCourseInfoID()).getTitle());
        bookSellingPriceTextView.setText(String.valueOf(currentItem.getSellingPrice()));
        bookOriginalPriceTextView.setText(String.valueOf(currentItem.originalPrice));




        return vi;

    }
}
