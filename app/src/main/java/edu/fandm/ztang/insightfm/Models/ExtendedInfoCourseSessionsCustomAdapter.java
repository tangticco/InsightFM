package edu.fandm.ztang.insightfm.Models;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import edu.fandm.ztang.insightfm.R;

/**
 * Created by zhuofantang on 4/27/17.
 */

public class ExtendedInfoCourseSessionsCustomAdapter extends BaseAdapter {

    private ArrayList<InsightDatabaseModel.Session> sessions;
    private static LayoutInflater inflater=null;
    ArrayList<Integer> searchCategoryIndex = null;

    public ExtendedInfoCourseSessionsCustomAdapter(Context context, ArrayList<InsightDatabaseModel.Session> sessions) {

        this.sessions = sessions;
        inflater = LayoutInflater.from(context);
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return sessions.size();
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
            vi = inflater.inflate(R.layout.sessions_row, null);
        }

        InsightDatabaseModel.Session currentSession = sessions.get(position);
        Log.d("Progess: ", String.valueOf(currentSession.getCrn()));


        TextView crnTextView = (TextView)vi.findViewById(R.id.session_crn);
        TextView daysTextView = (TextView)vi.findViewById(R.id.session_days);
        TextView timeTextView = (TextView)vi.findViewById(R.id.session_time);
        TextView roomTextView = (TextView)vi.findViewById(R.id.session_room);
        TextView instructorTextView = (TextView)vi.findViewById(R.id.session_instructor);

        crnTextView.setText(String.valueOf(currentSession.getCrn()));
        daysTextView.setText(currentSession.getDays());
        timeTextView.setText(currentSession.getBegin() + " " + currentSession.getEnd());
        roomTextView.setText(currentSession.getBuilding().getBuildingName() + currentSession.getRoom());
        instructorTextView.setText(currentSession.getInstructor().getFullName());



        return vi;

    }





}



