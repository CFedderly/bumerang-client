package com.seng480b.bumerang.fragments;

import android.content.DialogInterface;
import android.support.annotation.StyleRes;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
import com.seng480b.bumerang.R;
import com.seng480b.bumerang.interfaces.AsyncTaskHandler;
import com.seng480b.bumerang.models.Offer;
import com.seng480b.bumerang.utils.caching.UserDataCache;
import com.seng480b.bumerang.models.Request;
import com.seng480b.bumerang.utils.OfferUtility;
import com.seng480b.bumerang.utils.ProfileUtility;
import com.seng480b.bumerang.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import static com.seng480b.bumerang.utils.Utility.*;

import java.util.ArrayList;
import java.util.Locale;


public class RequestDetailFragment extends DialogFragment implements AsyncTaskHandler {
    View rootView;
    Request request;
    private OfferUtility.CreateOfferTask createOfferTask;

    public RequestDetailFragment() {
    }

    public void setRequest(Request req) {
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

        ImageButton cancelButton = (ImageButton) rootView.findViewById(R.id.buttonDetailDismiss);
        Button acceptButton = (Button) rootView.findViewById(R.id.buttonDetailAccept);
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

                // The user id of the requesting user
                int requesterId = request.getUserId();

                // The id the request being responded to.
                int borrowId = request.getRequestId();
                String borrowIdString = String.valueOf(borrowId);

                if (checkOfferValidity(requesterId, borrowId, true)) {
                    String profileIdString = String.valueOf(UserDataCache.getCurrentUser().getUserId());
                    createOfferTask = offerUtility.createOffer(getContext(), profileIdString, borrowIdString);
                } else {
                    dismiss();
                }
            }
        });

        populateViews();

        return rootView;
    }

    public void populateViews() {
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

    private boolean checkOfferValidity(int profileId, int requestId, boolean setErrorToast) {
        if (isOwnRequest(profileId)) {
            if (setErrorToast) {
                Utility.longToast(getContext(), R.string.unable_to_accept_own_request);
            }
            return false;
        } else if (alreadyAcceptedRequest(requestId)) {
            if (setErrorToast) {
                Utility.longToast(getContext(), R.string.unable_to_accept_request_again);
            }
            return false;
        }
        return true;
    }

    private boolean isOwnRequest(int profileId) {
        return UserDataCache.getCurrentUser().getUserId() == profileId;
    }

    private boolean alreadyAcceptedRequest(int requestId) {
        ArrayList<Offer> offers = UserDataCache.getOffers();
        if (offers == null) {
            return false;
        }
        for (Offer offer : offers) {
            if (requestId == offer.getRequest().getRequestId()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void beforeAsyncTask() {

    }

    @Override
    public void afterAsyncTask(String result) {
        String requester = String.valueOf(UserDataCache.getRecentUser().getFirstName());
        if (result == null || result.equals("")) {
            longToast(getActivity(), R.string.error_message);
        } else {
            int messageId;
            switch (request.getRequestType()) {
                case BORROW:
                    messageId = R.string.covered_them_message;
                    break;
                case LEND:
                    messageId = R.string.covered_you_message;
                    break;
                default:
                    Log.e("ERROR", "Invalid request type when accepting offer");
                    longToast(getActivity(), R.string.error_message);
                    dismiss();
                    return;
            }
            // Create local copy of Offer
            Offer offer = createOfferFromResult(result);
            UserDataCache.addOffer(offer);
            String title = String.format(Locale.getDefault(), getResources().getString(messageId), requester);
            String message = String.format(Locale.getDefault(), getResources().getString(R.string.responded_message), requester);
            // Starting work here
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(title).setMessage(message);
            builder.setPositiveButton(R.string.dialog_got_it, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dismiss();
        }
    }

    private Offer createOfferFromResult(String result) {
        try {
            JSONObject json = new JSONObject(result);
            int id = json.getInt("id");
            return new Offer(UserDataCache.getCurrentUser(), request, id);
        } catch (JSONException e) {
            Log.e("ERROR", "Unable to create offer object from JSON string");
            e.printStackTrace();
            return null;
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
