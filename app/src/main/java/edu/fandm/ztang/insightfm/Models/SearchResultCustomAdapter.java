package edu.fandm.ztang.insightfm.Models;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

import edu.fandm.ztang.insightfm.R;

/**
 * Created by zhuofantang on 4/18/17.
 */

public class SearchResultCustomAdapter extends BaseAdapter {


    private ArrayList<String> infoTitles;
    private static LayoutInflater inflater=null;
    ArrayList<Integer> searchCategoryIndex = null;


    public SearchResultCustomAdapter(Context context, ArrayList<String> infoTitles, ArrayList<Integer> searchCategoryIndex) {

        this.infoTitles = infoTitles;
        inflater = LayoutInflater.from(context);
        this.searchCategoryIndex = searchCategoryIndex;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return infoTitles.size();
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
            vi = inflater.inflate(R.layout.row, null);
        }
        
        TextView infoTitle = (TextView)vi.findViewById(R.id.txtTitle);
        infoTitle.setText(infoTitles.get(position));


        ImageView infoIcon = (ImageView)vi.findViewById(R.id.imgIcon);
        if(position < searchCategoryIndex.get(0)){
            infoIcon.setImageResource(R.drawable.ic_business_black_24dp);
        }else if(position < searchCategoryIndex.get(1)){
            infoIcon.setImageResource(R.drawable.ic_computer_black_24dp);
        }else if(position < searchCategoryIndex.get(2)){
            infoIcon.setImageResource(R.drawable.ic_class_black_24dp);
        }else if(position < searchCategoryIndex.get(3)){
            infoIcon.setImageResource(R.drawable.ic_format_list_numbered_black_24dp);
        }else if(position < searchCategoryIndex.get(4)){
            infoIcon.setImageResource(R.drawable.ic_people_black_24dp);
        }

        return vi;

    }


}
