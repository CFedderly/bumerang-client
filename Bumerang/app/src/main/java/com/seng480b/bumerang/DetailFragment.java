package com.seng480b.bumerang;



import android.support.v4.app.DialogFragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONException;
import org.json.JSONObject;


public class DetailFragment extends DialogFragment {
    private static final String offerUrl = BuildConfig.SERVER_URL + "/offer/";
    View rootView;
    JSONObject reqObj;
    //private Button cancelButton, acceptButton;
    public DetailFragment() {
        // Required empty public constructor
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
        rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        getDialog().setTitle("-MORE INFORMATION- DIALOG");

        ImageButton cancelButton = (ImageButton)rootView.findViewById(R.id.buttonDetailDismiss);
        Button acceptButton = (Button)rootView.findViewById(R.id.buttonDetailAccept);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(v);
            }
        });
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dismiss(v);
            }
        });

        populateViews();

        return rootView;
    }

    public void populateViews(){
        TextView itemName = (TextView) rootView.findViewById(R.id.request_title);
        TextView itemExp = (TextView) rootView.findViewById(R.id.time_left);
        TextView itemDesc = (TextView) rootView.findViewById(R.id.item_desc);
        TextView userName = (TextView) rootView.findViewById(R.id.user_name);

        ProfilePictureView profile_picture = (ProfilePictureView) rootView.findViewById(R.id.user_image);

        String exp_time = "there is no time limit.";

        try{
            itemName.setText(reqObj.getString("Item"));

            exp_time = ""+reqObj.get("Exp");
            userName.setText(reqObj.getString("Name"));
            profile_picture.setProfileId(reqObj.getString("FB_id"));
            itemDesc.setText(reqObj.getString("Description"));
        }catch (JSONException e) {
            e.printStackTrace();
        }
        itemExp.setText(exp_time);

    }


    /** cancel (x) button **/
    public void dismiss(View view){
        getDialog().dismiss();
    }

}
