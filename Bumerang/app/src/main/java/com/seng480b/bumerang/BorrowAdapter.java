package com.seng480b.bumerang;

/* custom adapter for the class -> DummyRequest <- Adapts the class so objects can be put into list (setListAdapter) */

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;


public class BorrowAdapter extends ArrayAdapter<DummyRequest>  {
    public BorrowAdapter(Context context, ArrayList<DummyRequest> requests) {
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


        //added a button that only appears in the My Requests page -> attaches to each list item
        ImageButton replyWarning = (ImageButton)convertView.findViewById(R.id.buttonReplyWarning);
        replyWarning.setVisibility(View.VISIBLE);

        //this allows both the imagebutton and the row to be clicked (otherwise the row is not clickable)
        replyWarning.setFocusable(false);
        replyWarning.setClickable(false);

        replyWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //does nothing at the moment
            }
        });


        //returns the completed view
        return convertView;
    }


}