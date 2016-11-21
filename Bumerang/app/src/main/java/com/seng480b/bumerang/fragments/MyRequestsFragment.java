package com.seng480b.bumerang.fragments;

import android.app.Activity;
import android.content.Context;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seng480b.bumerang.interfaces.AsyncTaskHandler;
import com.seng480b.bumerang.models.Offer;
import com.seng480b.bumerang.utils.OfferUtility;
import com.seng480b.bumerang.R;
import com.seng480b.bumerang.models.Request;
import com.seng480b.bumerang.utils.RequestUtility;
import com.seng480b.bumerang.utils.UserDataCache;
import com.seng480b.bumerang.activities.HomeActivity;
import com.seng480b.bumerang.adapters.MyRequestAdapter;
import com.seng480b.bumerang.utils.Utility;

import java.util.ArrayList;

public class MyRequestsFragment extends ListFragment implements OnItemClickListener, AsyncTaskHandler {
    private ViewPager mViewPager;
    private Activity activity;
    private ProgressBar progressBar;
    private TextView textView;
    private ListView listView;
    private RequestUtility.GetRequestsTask requestsTask;

    public MyRequestsFragment() {
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
        RelativeLayout rl = (RelativeLayout) inflater.inflate(R.layout.fragment_myrequests_list, container, false);
        progressBar = (ProgressBar) rl.findViewById(R.id.progress_bar);
        textView = (TextView) rl.findViewById(R.id.empty_list);
        return rl;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((HomeActivity) activity).setActionBarTitle("My Requests");

        // make tabs invisible
        mViewPager = (ViewPager) activity.findViewById(R.id.container);
        TabLayout tabLayout = (TabLayout) activity.findViewById(R.id.tabs);
        mViewPager.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
        listView = getListView();

        populateBrowse();

        if (isAsyncTaskRunning()) {
            showProgressBar();
        } else if (requestsTask == null) {
            hideAllOnError();
        } else {
            hideProgressBar();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) { }

    private void populateBrowse() {

        RequestUtility requestUtility = new RequestUtility<>(this);
        Request.RequestType requestType = Utility.getCurrentRequestType(mViewPager);
        if (requestType != null) {
            if (UserDataCache.hasProfile()) {
                requestsTask = requestUtility.getRequestsFromUser(getContext(),
                        UserDataCache.getCurrentUser().getUserId());
            }
        } else {
            hideAllOnError();
        }
    }

    @Override
    public void beforeAsyncTask() {
        showProgressBar();
    }

    @Override
    public void afterAsyncTask(String result) {
        hideProgressBar();
        if (result == null) {
            hideAllOnError();
            return;
        }
        if (!result.equals("")) {
            final ArrayList<Request> requests = Request.getListOfRequestsFromJSON(result);
            MyRequestAdapter mAdapter = new MyRequestAdapter(activity, requests);
            getListView().setAdapter(mAdapter);
            getListView().setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String offerResult = null;
                    try {
                        offerResult = OfferUtility.getOffers(getContext(), requests.get(position).getRequestId());
                    } catch (Exception e) {
                        Log.e("Error:", "Unable to retrieve offer information!");
                    }
                    if (offerResult != null) {
                        final ArrayList<Offer> offers = Offer.getListOfOffersFromJSON(offerResult);
                        // Ensure there are offers available to proceed, else don't do anything.
                        if (offers.size() != 0) {
                            Log.d("DEBUG", "Offer from id: " + Integer.toString(offers.get(0).getOfferProfile().getUserId()));
                            // Offer has offer_profile and request associated with it.
                            BorrowDialogFragment moreInfoDialog = new BorrowDialogFragment();
                            // A more elegant solution will be needed. But for now, get the first offer.
                            moreInfoDialog.setOfferObj(offers.get(0));

                            FragmentManager fm = getFragmentManager();
                            moreInfoDialog.show(fm, "Sample Fragment");
                        }
                    }
                }
            });
        } else {
            showEmptyMessage();
        }
    }

    @Override
    public boolean isAsyncTaskRunning() {
        return (requestsTask.getStatus() == RequestUtility.GetRequestsTask.Status.FINISHED);
    }

    private void hideAllOnError() {
        Utility.showRequestErrorDialog(getContext());
        textView.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void showEmptyMessage() {
        textView.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
    }
}
