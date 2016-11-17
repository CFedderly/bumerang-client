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

import java.io.IOException;
import java.util.ArrayList;

import static com.seng480b.bumerang.Utility.*;

public class Browse extends ListFragment implements OnItemClickListener {

    private static final String requestUrl = BuildConfig.SERVER_URL + "/requests/recent/100";
    private ViewPager viewPager;
    private Activity activity;

    public Browse() {
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
        return inflater.inflate(R.layout.fragment_browse_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((Home)getActivity()).setActionBarTitle("Browse");

        // make the tabs visible
        viewPager = (ViewPager) activity.findViewById(R.id.container);
        populateBrowse();
        // add a listener to reload the browse page once the tab is switched
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                populateBrowse();
            }
        });

        TabLayout tabLayout = (TabLayout) activity.findViewById(R.id.tabs);
        viewPager.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) { }

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Browse newInstance(int sectionNumber) {
        Browse fragment = new Browse();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    private void populateBrowse() {
        Request.RequestType requestType = getCurrentRequestType(viewPager);
        if (requestType != null) {
            new GetRequestsTask().execute(requestUrl);
        } else {
            longToast(getActivity(), R.string.unable_to_display_requests);
        }
    }

    public static Request.RequestType getCurrentRequestType(ViewPager pager) {
        int currentTab = pager.getCurrentItem();
        Request.RequestType requestType;
        Log.d("DEBUG", "Current tab: " + currentTab);
        switch(currentTab) {
            case 0:
                requestType = Request.RequestType.BORROW;
                break;
            case 1:
                requestType = Request.RequestType.LEND;
                break;
            default:
                requestType = null;
        }
        return requestType;
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
                final ArrayList<Request> reqList = Request.filterRequestsByType(requests, getCurrentRequestType(viewPager));
                RequestAdapter mAdapter = new RequestAdapter(activity,
                        Request.filterRequestsByType(requests, getCurrentRequestType(viewPager)));
                getListView().setAdapter(mAdapter);
                getListView().setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Request req = reqList.get(position);
                        DetailFragment details = new DetailFragment();
                        details.setRequest(req);

                        FragmentManager fm = getFragmentManager();
                        details.show(fm,"Sample Fragment");
                    }
                });
            }
        }
    }
}