package com.seng480b.bumerang.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seng480b.bumerang.R;
import com.seng480b.bumerang.activities.HomeActivity;
import com.seng480b.bumerang.interfaces.AsyncTaskHandler;
import com.seng480b.bumerang.models.Request;
import com.seng480b.bumerang.utils.OfferUtility;
import com.seng480b.bumerang.utils.ProfileUtility;

public class RequestFragment extends Fragment implements AsyncTaskHandler {
    private static final String TAG = "RequestFragment";
    private Activity activity;
    private Request request;
    private RelativeLayout relativeLayout;
    private OfferUtility.CreateOfferTask createOfferTask;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.activity =(Activity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((HomeActivity) activity).setFabVisibility(false);
        setupToolbar();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_request_detail, container, false);
        relativeLayout = (RelativeLayout) rootView.findViewById(R.id.relative_layout);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateRequestInformation();
    }

    @Override
    public void onStop() {
        ((HomeActivity) activity).setupToolbar();
        ((HomeActivity) activity).setFabVisibility(true);
        super.onStop();
    }

    public void setRequest(Request request) {
        this.request = request;
        int userId = request.getUserId();
        ProfileUtility.storeRecentUserFromUserId(userId);
    }

    private void setupToolbar() {
        Toolbar actionBar = (Toolbar) activity.findViewById(R.id.toolbar);
        if (actionBar != null) {
            actionBar.setNavigationIcon(R.drawable.ic_menu_back);
            actionBar.setTitle(getIdForRequestType());
            actionBar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BrowseRootFragment browseRootFragment = new BrowseRootFragment();
                    ((HomeActivity)activity).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.mainFrame, browseRootFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }
    }


    private void populateRequestInformation() {
        TextView itemName = (TextView) relativeLayout.findViewById(R.id.title);
        TextView itemDescription = (TextView) relativeLayout.findViewById(R.id.description);

        itemName.setText(request.getTitle());
        itemDescription.setText(request.getDescription());
    }

    @Override
    public void beforeAsyncTask() {

    }

    @Override
    public void afterAsyncTask(String result) {

    }

    @Override
    public boolean isAsyncTaskRunning() {
        return false;
    }

    private int getIdForRequestType() {
        switch(request.getRequestType()) {
            case BORROW:
                return R.string.borrow_tab;
            case LEND:
                return R.string.lend_tab;
            default:
                // TODO error handle
                return 0;
        }
    }
}
