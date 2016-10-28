package com.seng480b.bumerang;

/* custom adapter for the class -> Request <- Adapts the class so objects can be put into list (setListAdapter) */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


class RequestAdapter extends ArrayAdapter<Request> {

    RequestAdapter(Context context, ArrayList<Request> requests) {
        super(context,0,requests);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        Request request_ticket = getItem(position);

        //check if existing view is being re-used, otherwise inflate the view
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.browse_item, parent, false);
        }

        //look up view for data population
        TextView title = (TextView) convertView.findViewById(R.id.request_title);
        TextView description = (TextView) convertView.findViewById(R.id.request_description);
        TextView user = (TextView) convertView.findViewById(R.id.request_user);

        //populate the data into the template view using data object
        title.setText(request_ticket.getTitle());
        description.setText(request_ticket.getDescription());
        user.setText(request_ticket.getUser());

        return convertView;
    }


}
