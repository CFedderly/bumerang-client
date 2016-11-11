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



public class DetailFragment extends DialogFragment {
    private static final String offerUrl = BuildConfig.SERVER_URL + "/offer/";
    View rootView;
    Request request;
    //private Button cancelButton, acceptButton;
    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * will set the request and cache the requester as the recentUser
     * @param req
     */
   public void setRequest(Request req){
        this.request = req;
        int userId = request.getUserId();
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
                // profileId, the id of the user responding to the request.
                String profileId = String.valueOf(UserDataCache.getCurrentUser().getUserId());
                String requester = "Nobody";
                String result = null;
                try {
                    //borrowId, the id the request being responded to.
                    String borrowId = String.valueOf(UserDataCache.getRecentUser().getUserId());
                    requester =  String.valueOf(UserDataCache.getRecentUser().getFirstName());
                    result = new OfferUtility.CreateOfferTask().execute(
                            offerUrl,
                            profileId,
                            borrowId
                    ).get();
                } catch (Exception e) {
                    // TODO: this is a hacky solution, will need real error handling
                }
                if(result != null){
                    Toast.makeText(getActivity(), "You've got "+ requester +" covered!", Toast.LENGTH_LONG).show();
                    dismiss(v);
                }else{
                    Toast.makeText(getActivity(), "Oops something went wrong", Toast.LENGTH_LONG).show();
                }
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
        ProfilePictureView profilePicture = (ProfilePictureView) rootView.findViewById(R.id.user_image);

        itemName.setText(request.getTitle());
        itemExp.setText("Will expire in " + request.getMinutesUntilExpiry() + " minutes.");
        itemDesc.setText(request.getDescription());
        userName.setText(UserDataCache.getRecentUser().getFirstName());
        profilePicture.setProfileId(String.valueOf(UserDataCache.getRecentUser().getFacebookId()));

    }

    /** cancel (x) button **/
    public void dismiss(View view){
        getDialog().dismiss();
    }

}
