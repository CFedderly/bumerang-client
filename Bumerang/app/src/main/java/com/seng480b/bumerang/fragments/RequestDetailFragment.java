package com.seng480b.bumerang.fragments;

import android.support.annotation.StyleRes;
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
import com.seng480b.bumerang.interfaces.AsyncTaskHandler;
import com.seng480b.bumerang.utils.UserDataCache;
import com.seng480b.bumerang.models.Request;
import com.seng480b.bumerang.utils.OfferUtility;
import com.seng480b.bumerang.utils.ProfileUtility;
import static com.seng480b.bumerang.utils.Utility.*;

import java.util.Locale;


public class RequestDetailFragment extends DialogFragment implements AsyncTaskHandler {
    View rootView;
    Request request;
    private OfferUtility.CreateOfferTask createOfferTask;

    public RequestDetailFragment() {
        // Required empty public constructor
    }

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
        final OfferUtility offerUtility = new OfferUtility<>(this);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // profileId, the id of the user responding to the request.
                String profileId = String.valueOf(UserDataCache.getCurrentUser().getUserId());

                //borrowId, the id the request being responded to.
                String borrowId = String.valueOf(request.getRequestId());

                createOfferTask = offerUtility.createOffer(getContext(), profileId, borrowId);
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
        itemExp.setText("Expires in " + request.getMinutesUntilExpiry() + " minutes.");
        itemDesc.setText(request.getDescription());
        userName.setText(UserDataCache.getRecentUser().getFirstName());
        profilePicture.setProfileId(String.valueOf(UserDataCache.getRecentUser().getFacebookId()));

    }

    @Override
    public void setStyle(int style, @StyleRes int theme) {
        super.setStyle(style, theme);
    }

    @Override
    public void beforeAsyncTask() {

    }

    @Override
    public void afterAsyncTask(String result) {
        String requester =  String.valueOf(UserDataCache.getRecentUser().getFirstName());
        if (result == null || result.equals("")) {
            longToast(getActivity(), R.string.error_message);
        } else {
            String message;
            if (request.getRequestType() == Request.RequestType.LEND ) {
                message = String.format(Locale.getDefault(), getResources().getString(R.string.covered_you_message), requester);
            } else {
                message = String.format(Locale.getDefault(), getResources().getString(R.string.covered_them_message), requester);
            }
            longToast(getActivity(), message);
            dismiss();
        }
    }

    @Override
    public boolean isAsyncTaskRunning() {
        if (createOfferTask == null) {
            return false;
        }
        return createOfferTask.getStatus() != OfferUtility.CreateOfferTask.Status.FINISHED;
    }
}
