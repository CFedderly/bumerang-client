package com.seng480b.bumerang.fragments;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
import com.seng480b.bumerang.models.Offer;
import com.seng480b.bumerang.R;
import com.seng480b.bumerang.utils.KarmaUtility;
import com.seng480b.bumerang.utils.Utility;
import com.seng480b.bumerang.models.Request;

import java.util.Locale;



public class BorrowDialogFragment extends DialogFragment {
    private View rootView;
    private Offer offer;
    private static final String prefFileName = "com.bumerang.app";

    //private Button cancelButton, acceptButton;
    public BorrowDialogFragment() {

    }

    // Sets the offer object and caches responding user
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
        rootView = inflater.inflate(R.layout.fragment_borrow_dialog, container, false);
        getDialog().setTitle("-MORE INFORMATION- DIALOG");

        ImageButton cancelButton = (ImageButton)rootView.findViewById(R.id.buttonBorrowDismiss);
        Button acceptButton = (Button)rootView.findViewById(R.id.buttonBorrowAccept);

        //set accept button text depending on if your offer is a lend or a borrow
        if (offer.getRequest().getRequestType() == Request.RequestType.LEND) {
            acceptButton.setText(getString(R.string.button_start_lending));
        } else {
            acceptButton.setText(getString(R.string.button_accept_offer));
        }

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOfferStatus();
                giveKarma();
                showPhoneNumber();
            }
        });

        populateViews();
        return rootView;
    }

    public void populateViews(){
        TextView itemName = (TextView) rootView.findViewById(R.id.item_wanted);
        TextView itemExp = (TextView) rootView.findViewById(R.id.time_left);
        TextView lenderName = (TextView) rootView.findViewById(R.id.borrow_message);

        ProfilePictureView profile_picture = (ProfilePictureView) rootView.findViewById(R.id.user_image);

        // Fill text fields with proper user info.
        itemName.setText(offer.getRequest().getTitle());
        itemExp.setText("Expires in " + offer.getRequest().getMinutesUntilExpiry() + " minutes.");

        if (offer.getRequest().getRequestType() == Request.RequestType.LEND) {
            lenderName.setText(String.format(Locale.getDefault(),
                    getResources().getString(R.string.covered_them_message),
                    offer.getOfferProfile().getFirstName()));
        } else {
            lenderName.setText(String.format(Locale.getDefault(),
                    getResources().getString(R.string.covered_you_message),
                    offer.getOfferProfile().getFirstName()));
        }

        profile_picture.setProfileId(String.valueOf(offer.getOfferProfile().getFacebookId()));
        // TODO: CODE BELOW SITUATION
        // Change status of the request to matched
        // By sending to server via async
        // new acceptOffer.execute(offerUrl, offer);

    }

    public void giveKarma(){
        int karma = new KarmaUtility().giveKarmaForRequest(offer);
        if (karma>0){
            Utility.karmaToast(getActivity(), karma);
        }
    }


    //TODO: this will have to be changed when we change the contact method
    private void showPhoneNumber(){
        PhoneNumberDialogFragment phoneNumberDialog = new PhoneNumberDialogFragment();
        // A more elegant solution will be needed. But for now, get the first offer.
        phoneNumberDialog.setOfferObj(offer);

        FragmentManager fm = getFragmentManager();
        phoneNumberDialog.show(fm, "Sample Fragment");
        dismiss();
    }

    private void setOfferStatus(){
        offer.setStatus(Offer.OfferStatus.FINISHED);
        String status = String.valueOf(offer.getStatus());
        //TODO: setup pending system
        //store a value in the shared preferences to record that the offer has been accepted
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(String.valueOf(offer.getOfferId()),"accepted").apply();
    }
}