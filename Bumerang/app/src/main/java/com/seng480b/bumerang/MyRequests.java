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
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MyRequests extends ListFragment implements OnItemClickListener {

    private static final String requestUrl = BuildConfig.SERVER_URL + "/requests/user/";
    private ViewPager mViewPager;
    private ListView mListView;
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
        return inflater.inflate(R.layout.fragment_myrequests_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((Home) activity ).setActionBarTitle("My Requests");

        mListView = getListView();
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
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) { }

    private void populateBrowse() {
        Request.RequestType requestType = Browse.getCurrentRequestType(mViewPager);
        if (requestType != null) {
            if (UserDataCache.hasProfile()) {
                String myRequestUrl = requestUrl + UserDataCache.getCurrentUser().getUserId();
                new GetRequestsTask().execute(myRequestUrl);
            }
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
            if (result != null) {
                final ArrayList<Request> requests = Request.getListOfRequestsFromJSON(result);
                MyRequestAdapter mAdapter = new MyRequestAdapter(activity, requests);
                getListView().setAdapter(mAdapter);
                getListView().setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String result = checkForResponse(requests.get(position).getReqId());
                        if (result != null) {
                            Offer offer = new Offer(result, requests.get(position));
                            Log.d("DEBUG", "Request id: "+Integer.toString(offer.getRequest_ID()));

                            BorrowDialogFragment more_info_dialog = new BorrowDialogFragment();
                            more_info_dialog.setOfferObj(offer);

                            FragmentManager fm = getFragmentManager();
                            more_info_dialog.show(fm, "Sample Fragment");
                        }
                    }
                });
            }
        }

        public String checkForResponse(int requestId) {
            String offerUrl =  BuildConfig.SERVER_URL + "/offer/";
            // Holds the result of the server query
            if (UserDataCache.hasProfile()) {
                String myRequestUrl = offerUrl + Integer.toString(requestId);
                try {
                   return Connectivity.makeHttpGetRequest(myRequestUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("ERROR", "Unable to retrieve requests");
                    cancel(true);
                }
            }
            return null;
        }
    }
}
