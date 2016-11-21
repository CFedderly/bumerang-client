package com.seng480b.bumerang.adapters;

/* custom adapter for the class -> Request <- Adapts the class so objects can be put into list (setListAdapter) */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.seng480b.bumerang.R;
import com.seng480b.bumerang.interfaces.AsyncTaskHandler;
import com.seng480b.bumerang.models.Request;
import com.seng480b.bumerang.utils.RequestUtility;

import java.util.ArrayList;
import java.util.Locale;

import static com.seng480b.bumerang.utils.Utility.longToast;


public class MyRequestAdapter extends ArrayAdapter<Request> implements AsyncTaskHandler {
    private ArrayList<Request> requests;
    private RequestUtility.DeleteRequestTask deleteRequestTask;
    private int currentPosition;

    public MyRequestAdapter(Context context, ArrayList<Request> requests) {
        super(context,0,requests);
        this.requests = requests;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        Request request = getItem(position);

        //check if existing view is being re-used, otherwise inflate the view
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.browse_item, parent, false);
        }

        //set colour of list item depending on if it is a LEND or a BORROW (only in MyRequests)
        Request.RequestType type = request.getRequestType();
        if (type == Request.RequestType.BORROW) {
            convertView.setBackgroundResource(R.drawable.blue_rectangle);
        } else {
            convertView.setBackgroundResource(R.drawable.pink_rectangle);
        }

        //look up view for data population
        TextView title = (TextView) convertView.findViewById(R.id.request_title);
        TextView description = (TextView) convertView.findViewById(R.id.request_description);
        TextView user = (TextView) convertView.findViewById(R.id.request_user);

        //populate the data into the template view using data object
        title.setText(request.getTitle());
        description.setText(request.getDescription());
        user.setText(String.format(Locale.getDefault(), "%d", request.getUserId()));

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

        // overflow menu button to hold "delete" option
        ImageButton overflowMenu = (ImageButton) convertView.findViewById(R.id.overflow_menu_button);
        overflowMenu.setVisibility(View.VISIBLE);
        overflowMenu.setClickable(true);

        overflowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getContext(), v);
                popupMenu.getMenuInflater().inflate(R.menu.request_overflow_menu, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.request_overflow_delete:
                                deleteRequestDialog();
                                return true;
                            default:
                                break;
                        }
                        return false;
                    }
                });
            }

            private void deleteRequestDialog() {
                // build dialog to confirm deletion
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.delete_request_title)
                        .setMessage(R.string.delete_request_message)
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deleteRequest(position);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        return convertView;
    }

    private void deleteRequest(int position) {
        Request request = getItem(position);
        currentPosition = position;
        RequestUtility requestUtility = new RequestUtility<>(this);
        deleteRequestTask = requestUtility.deleteRequest(getContext(), request.getRequestId());
    }

    @Override
    public void beforeAsyncTask() {

    }

    @Override
    public void afterAsyncTask(String result) {
        if (result == null) {
            longToast(getContext(), R.string.delete_request_fail_message);
        } else {
            longToast(getContext(), R.string.delete_request_success_message);
            requests.remove(currentPosition);
            notifyDataSetChanged();
        }
    }

    @Override
    public boolean isAsyncTaskRunning() {
        return deleteRequestTask.getStatus() == RequestUtility.DeleteRequestTask.Status.FINISHED;
    }
}