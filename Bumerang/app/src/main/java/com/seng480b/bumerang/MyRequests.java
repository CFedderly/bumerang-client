package com.seng480b.bumerang;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MyRequests extends ListFragment implements OnItemClickListener {

    private static final String requestUrl = BuildConfig.SERVER_URL +
            "/requests/user/" + UserDataCache.getCurrentUser().getUserId();
    private ViewPager mViewPager;
    private Activity activity;

    public MyRequests() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            this.activity =(Activity) context;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myrequests_list, container, false);

        ((Home) activity ).setActionBarTitle("My Requests");

        /** make the tabs invisible **/
        mViewPager = (ViewPager) activity.findViewById(R.id.container);
        populateBrowse();
        // add a listener to reload the browse page once the tab is switched
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                populateBrowse();
            }
        });
        TabLayout tabLayout = (TabLayout) activity.findViewById(R.id.tabs);
        mViewPager.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) { }

    private void populateBrowse() {
        Request.RequestType requestType = Browse.getCurrentRequestType(mViewPager);
        if (requestType != null) {
            new GetRequestsTask().execute(requestUrl);
        } else {
            Toast.makeText(activity, R.string.unable_to_display_requests, Toast.LENGTH_LONG).show();
        }
    }

    private class GetRequestsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                return Connectivity.makeHttpGetRequest(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("ERROR", "Unable to retrieve requests");
                cancel(true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            ArrayList<Request> requests = Request.getListOfRequestsFromJSON(result);
            // TODO: uncomment this once there are borrow/lend tabs on the myrequest page
            /*RequestAdapter mAdapter = new RequestAdapter(activity,
                    Request.filterRequestsByType(requests, Browse.getCurrentRequestType(mViewPager)));*/
            RequestAdapter mAdapter = new RequestAdapter(activity, requests);
            getListView().setAdapter(mAdapter);
            getListView().setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    BorrowDialogFragment more_info_dialog = new BorrowDialogFragment();
                    FragmentManager fm = getFragmentManager();
                    more_info_dialog.show(fm, "Sample Fragment");
                }
            });
        }
    }
}
