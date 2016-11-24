package com.seng480b.bumerang.fragments;


import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
import com.seng480b.bumerang.R;
import com.seng480b.bumerang.models.Offer;


public class PhoneNumberDialogFragment extends DialogFragment {
    private View rootView;
    private Offer offer;

    //private Button cancelButton, acceptButton;
    public PhoneNumberDialogFragment() {}

    // Sets the offer object
    public void setOfferObj(Offer offer) {
        this.offer = offer;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_phone_dialog, container, false);
        getDialog().setTitle("-MORE INFORMATION- DIALOG");

        ImageButton cancelButton = (ImageButton)rootView.findViewById(R.id.buttonPhoneDismiss);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(v);
            }
        });

        Button phoneNumber = (Button) rootView.findViewById(R.id.buttonPhoneMsg);
        phoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setData(Uri.parse("sms:" + offer.getOfferProfile().getPhoneNumber()));
                // Line below is for future reference if we want to pre-set a generic message.
                //smsIntent.putExtra("sms_body", "Body of message");
                startActivity(smsIntent);
            }
        });
        phoneNumber.setPaintFlags(phoneNumber.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        populateViews();


        return rootView;
    }

    public void populateViews(){
        TextView itemName = (TextView) rootView.findViewById(R.id.item_wanted);
        TextView itemExp = (TextView) rootView.findViewById(R.id.time_left);
        TextView lenderName = (TextView) rootView.findViewById(R.id.phoneTextDisplay);
        Button phone = (Button) rootView.findViewById(R.id.buttonPhoneMsg);

        ProfilePictureView profile_picture = (ProfilePictureView) rootView.findViewById(R.id.user_image);

        // Fill text fields with proper user info.
        itemName.setText(offer.getRequest().getTitle());
        itemExp.setText("Expires in " + offer.getRequest().getMinutesUntilExpiry() + " minutes.");
        phone.setText(offer.getOfferProfile().getPhoneNumber());
        lenderName.setText("Contact: " + offer.getOfferProfile().getFirstName());
        profile_picture.setProfileId(String.valueOf(offer.getOfferProfile().getFacebookId()));

    }

    /** cancel (x) button **/
    public void dismiss(View view){
        getDialog().dismiss();
    }
}