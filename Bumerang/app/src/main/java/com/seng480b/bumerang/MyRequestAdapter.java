package com.seng480b.bumerang;

/* custom adapter for the class -> Request <- Adapts the class so objects can be put into list (setListAdapter) */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;


class MyRequestAdapter extends ArrayAdapter<Request> {

    MyRequestAdapter(Context context, ArrayList<Request> requests) {
        super(context,0,requests);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        Request requestTicket = getItem(position);

        //check if existing view is being re-used, otherwise inflate the view
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.browse_item, parent, false);
        }
        //look up view for data population
        TextView title = (TextView) convertView.findViewById(R.id.request_title);
        TextView description = (TextView) convertView.findViewById(R.id.request_description);
        TextView user = (TextView) convertView.findViewById(R.id.request_user);

        //populate the data into the template view using data object
        title.setText(requestTicket.getTitle());
        description.setText(requestTicket.getDescription());
        user.setText(Integer.toString(requestTicket.getUserId()));

        //added a button that only appears in the My Requests page -> attaches to each list item
        ImageButton replyWarning = (ImageButton) convertView.findViewById(R.id.buttonReplyWarning);
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

        return convertView;
    }


}