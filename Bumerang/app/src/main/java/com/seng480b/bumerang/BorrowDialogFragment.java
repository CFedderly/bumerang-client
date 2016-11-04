package com.seng480b.bumerang;


import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.*;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;


public class BorrowDialogFragment extends DialogFragment {

    private JSONObject reqObj;
    private View rootView;

    //private Button cancelButton, acceptButton;
    public BorrowDialogFragment() {
    }

    public void sendInfo(JSONObject obj){
        this.reqObj = obj;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_borrow_dialog, container, false);
        getDialog().setTitle("-MORE INFORMATION- DIALOG");

        ImageButton cancelButton = (ImageButton)rootView.findViewById(R.id.buttonBorrowDismiss);
        Button acceptButton = (Button)rootView.findViewById(R.id.buttonBorrowAccept);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(v);
            }
        });
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //alert box -> change strings 'yes' and 'undo' to res/values/strings
                new AlertDialog.Builder(getContext())
                        .setTitle("Accept Lend Offer")
                        .setMessage("Now get in contact.")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setNegativeButton("undo", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();


                dismiss(v);
            }
        });

        populateViews();


        return rootView;
    }

    public void populateViews(){
        TextView itemName = (TextView) rootView.findViewById(R.id.item_wanted);
        TextView itemExp = (TextView) rootView.findViewById(R.id.time_left);
        TextView lenderName = (TextView) rootView.findViewById(R.id.borrow_message);
        TextView phone = (TextView) rootView.findViewById(R.id.phone);

        ProfilePictureView profile_picture = (ProfilePictureView) rootView.findViewById(R.id.user_image);


        String exp_time = "there is no time limit.";
        String lender_message = "someone's got you covered!";

        try{
            itemName.setText(reqObj.getString("Item"));
            exp_time = ""+reqObj.get("Exp");
            lender_message = " "+reqObj.getString("Lender")+"'s got you covered! ";
            phone.setText(reqObj.getString("Phone_No"));
            profile_picture.setProfileId(reqObj.getString("FB_id"));
        }catch (JSONException e){
            e.printStackTrace();
        }
        lenderName.setText(lender_message);
        itemExp.setText(exp_time);

    }
    /** cancel (x) button **/
    public void dismiss(View view){
        getDialog().dismiss();
    }

}