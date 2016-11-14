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

    private boolean isMyRequests = false;

    RequestAdapter(Context context, ArrayList<Request> requests, boolean isMyRequests) {
        super(context,0,requests);
        this.isMyRequests = isMyRequests;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        Request request_ticket = getItem(position);

        //check if existing view is being re-used, otherwise inflate the view
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.browse_item, parent, false);
        }

        //set colour of list item depending on if it is a LEND or a BORROW (only in MyRequests)
        if (this.isMyRequests) {
            Request.RequestType type = request_ticket.getRequestType();
            if (type == Request.RequestType.BORROW) {
                convertView.setBackgroundResource(R.drawable.blue_rectangle);
            } else {
                convertView.setBackgroundResource(R.drawable.pink_rectangle);
            }
        }

        //look up view for data population
        TextView title = (TextView) convertView.findViewById(R.id.request_title);
        TextView description = (TextView) convertView.findViewById(R.id.request_description);
        TextView user = (TextView) convertView.findViewById(R.id.request_user);

        //populate the data into the template view using data object
        title.setText(request_ticket.getTitle());
        description.setText(request_ticket.getDescription());
        user.setText(Integer.toString(request_ticket.getUserId()));

        return convertView;
    }


}
