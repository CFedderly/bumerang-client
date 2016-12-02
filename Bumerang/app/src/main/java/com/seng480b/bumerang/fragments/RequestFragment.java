package com.seng480b.bumerang.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.seng480b.bumerang.R;
import com.seng480b.bumerang.activities.HomeActivity;
import com.seng480b.bumerang.interfaces.AsyncTaskHandler;
import com.seng480b.bumerang.models.Request;
import com.seng480b.bumerang.utils.OfferUtility;
import com.seng480b.bumerang.utils.ProfileUtility;

public class RequestFragment extends Fragment implements AsyncTaskHandler {
    private Activity activity;
    private Request request;
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
        Toolbar actionBar = (Toolbar) activity.findViewById(R.id.toolbar);
        if (actionBar != null) {
            actionBar.setNavigationIcon(R.drawable.ic_menu_back);
            actionBar.setTitle(getIdForRequestType());
            actionBar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BrowseFragment browseFragment = new BrowseFragment();
                    getFragmentManager().beginTransaction()
                            .replace(R.id.mainFrame, browseFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }

        // hide the tabs
        ViewPager viewPager = (ViewPager) activity.findViewById(R.id.container);
        TabLayout tabLayout = (TabLayout) activity.findViewById(R.id.tabs);
        viewPager.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStop() {
        ((HomeActivity) activity).setupToolbar();
        super.onStop();
    }

    public void setRequest(Request request) {
        this.request = request;
        int userId = request.getUserId();
        ProfileUtility.storeRecentUserFromUserId(userId);
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
