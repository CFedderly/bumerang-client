package com.seng480b.bumerang;

/* custom adapter for the class -> DummyRequest <- Adapts the class so objects can be put into list (setListAdapter) */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class RequestAdapter extends ArrayAdapter<DummyRequest> {
    public RequestAdapter(Context context, ArrayList<DummyRequest> requests) {
        super(context,0,requests);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        //get data item for this position
        DummyRequest request_ticket = getItem(position);
        //check if existing view is being re-used, otherwise inflate the view
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.browse_item, parent, false);
        }
        //look up view for data population..
        TextView borrower = (TextView) convertView.findViewById(R.id.name_of_borrower);
        TextView item = (TextView) convertView.findViewById(R.id.item_needed);
        //populate the data into the template view using data object..
        borrower.setText(request_ticket.getNameOfBorrower());
        item.setText(request_ticket.getTitleOfRequest());
        //returns the completed view
        return convertView;
    }


}
