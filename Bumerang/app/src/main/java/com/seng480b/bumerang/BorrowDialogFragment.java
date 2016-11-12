package com.seng480b.bumerang;


import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;


public class BorrowDialogFragment extends DialogFragment {
    private View rootView;
    private Offer offer;
    private static final String offerUrl = BuildConfig.SERVER_URL + "/offer/";

    //private Button cancelButton, acceptButton;
    public BorrowDialogFragment() {

    }

    // Sets the offer object and caches responding user
    public void setOfferObj(Offer offer) {
        this.offer = offer;
        int userId = offer.getOfferProfile().getUserId();
        ProfileUtility.storeRecentUserFromUserId(userId);
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

                //Show dialog box with responders phone number in it.
                new AlertDialog.Builder(getContext())
                        .setTitle("Accept Lend Offer")
                        .setMessage(UserDataCache.getRecentUser().getFirstName() +
                                "'s phone number: " +
                                UserDataCache.getRecentUser().getPhoneNumber())
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dismiss();
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

        // Fill text fields with proper user info.
        itemName.setText(offer.getRequestInfo().getTitle());
        itemExp.setText("Will expire in " + offer.getRequestInfo().getMinutesUntilExpiry() + " minutes.");
        lenderName.setText(UserDataCache.getRecentUser().getFirstName());
        // Don't set phone text here! Have another dialog box pop up after they have accepted.
        //phone.setText(UserDataCache.getRecentUser().getPhoneNumber());
        profile_picture.setProfileId(String.valueOf(UserDataCache.getRecentUser().getFacebookId()));
        // TODO: CODE BELOW SITUATION
        // Change status of the request to matched
        // By sending to server via async
        // new acceptOffer.execute(offerUrl, offer);

    }
    /** cancel (x) button **/
    public void dismiss(View view){
        getDialog().dismiss();
    }

}