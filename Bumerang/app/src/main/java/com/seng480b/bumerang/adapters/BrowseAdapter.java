package com.seng480b.bumerang.adapters;

/* custom adapter for the class -> Request <- Adapts the class so objects can be put into list (setListAdapter) */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.seng480b.bumerang.R;
import com.seng480b.bumerang.models.Request;

import java.util.ArrayList;


public class BrowseAdapter extends ArrayAdapter<Request> {


    public BrowseAdapter(Context context, ArrayList<Request> requests) {
        super(context,0,requests);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        Request requestTicket = getItem(position);

        //check if existing view is being re-used, otherwise inflate the view
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.browse_item, parent, false);
        }

        //set colour of list item to -pink- or -blue-
        Request.RequestType type = requestTicket.getRequestType();
        if (type == Request.RequestType.BORROW) {
            convertView.setBackgroundResource(R.drawable.pink_rectangle);
        } else {
            convertView.setBackgroundResource(R.drawable.blue_rectangle);
        }

        //look up view for data population
        TextView title = (TextView) convertView.findViewById(R.id.request_title);
        TextView description = (TextView) convertView.findViewById(R.id.request_description);
        TextView user = (TextView) convertView.findViewById(R.id.request_user);

        //populate the data into the template view using data object
        title.setText(requestTicket.getTitle());
        description.setText(requestTicket.getDescription());
        user.setText(Integer.toString(requestTicket.getUserId()));

        return convertView;
    }


}
