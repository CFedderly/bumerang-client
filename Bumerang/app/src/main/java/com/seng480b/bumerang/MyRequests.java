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

import java.io.IOException;
import java.util.ArrayList;

public class MyRequests extends ListFragment implements OnItemClickListener {

    private static final String requestUrl = BuildConfig.SERVER_URL + "/requests/user/";
    private static final String offerUrl = BuildConfig.SERVER_URL + "/offer/ids/";
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
                        Log.d("Request id for offer: ", Integer.toString(requests.get(position).getRequestId()));
                        String myOfferUrl = offerUrl + requests.get(position).getRequestId();
                        String offerResult = null;
                        try {
                            offerResult = new getOfferTask.offerTask().execute(myOfferUrl).get();
                        } catch (Exception e) {
                            // Error performing get request. display error
                            Log.e("Error:", "Unable to retrieve offer information!");
                        }
                        if (offerResult != null) {
                            final ArrayList<Offer> offers = Offer.getListOfOffersFromJSON(offerResult);
                            // Ensure there are offers available to proceed, else don't do anything.
                            if (offers.size() != 0) {
                                Log.d("DEBUG", "Offer from id: " + Integer.toString(offers.get(0).getOfferProfile().getUserId()));
                                // Offer has offer_profile and request associated with it.
                                BorrowDialogFragment more_info_dialog = new BorrowDialogFragment();
                                // A more elegant solution will be needed. But for now, get the first offer.
                                more_info_dialog.setOfferObj(offers.get(0));

                                FragmentManager fm = getFragmentManager();
                                more_info_dialog.show(fm, "Sample Fragment");
                            }
                        }
                    }
                });
            }
        }
    }
}
