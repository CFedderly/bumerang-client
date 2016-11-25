package com.seng480b.bumerang.fragments;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
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

import com.seng480b.bumerang.activities.HomeActivity;
import com.seng480b.bumerang.R;
import com.seng480b.bumerang.interfaces.AsyncTaskHandler;
import com.seng480b.bumerang.models.Offer;
import com.seng480b.bumerang.models.Request;
import com.seng480b.bumerang.adapters.BrowseAdapter;
import com.seng480b.bumerang.utils.OfferUtility;
import com.seng480b.bumerang.utils.RequestUtility;
import com.seng480b.bumerang.utils.caching.UserDataCache;
import com.seng480b.bumerang.utils.Utility;

import java.util.ArrayList;

public class BrowseFragment extends ListFragment implements OnItemClickListener, AsyncTaskHandler {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private ViewPager viewPager;
    private Activity activity;
    private ProgressBar progressBar;
    private TextView textView;
    private ListView listView;
    private RequestUtility.GetRequestsTask requestsTask;
    private GetUserOffersAsyncTaskHandler offerHandler;

    public BrowseFragment() {
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RelativeLayout rl = (RelativeLayout) inflater.inflate(R.layout.fragment_browse_list, container, false);
        progressBar = (ProgressBar) rl.findViewById(R.id.progress_bar);
        textView = (TextView) rl.findViewById(R.id.empty_list);
        return rl;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((HomeActivity)getActivity()).setActionBarTitle("Browse");

        // make the tabs visible
        viewPager = (ViewPager) activity.findViewById(R.id.container);
        TabLayout tabLayout = (TabLayout) activity.findViewById(R.id.tabs);
        viewPager.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
        listView = getListView();

        final RequestUtility requestUtility = new RequestUtility<>(this);
        requestsTask = requestUtility.getRequestsByRecent(getContext());

        // Start task to get offers from the logged in user
        offerHandler = new GetUserOffersAsyncTaskHandler(getContext());
        offerHandler.startGetOffersTask(UserDataCache.getCurrentUser().getUserId());

        if (isAsyncTaskRunning()) {
            showProgressBar();
        } else {
            hideProgressBar();
        }

        // add a listener to reload the browse page once the tab is switched
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                Log.d("DEBUG", "onPageSelected " + position);
                if (!isAsyncTaskRunning()) {
                    requestsTask = requestUtility.getRequestsByRecent(getContext());
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) { }

    public static BrowseFragment newInstance(int sectionNumber) {
        BrowseFragment fragment = new BrowseFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean isAsyncTaskRunning() {
        if (requestsTask == null) {
            return false;
        } else if (requestsTask.getStatus() == RequestUtility.GetRequestsTask.Status.FINISHED) {
            return false;
        }
        return true;
    }

    @Override
    public void beforeAsyncTask() {
        showProgressBar();
    }

    @Override
    public void afterAsyncTask(String result) {
        requestsTask = null;
        hideProgressBar();
        if (result == null) {
            Utility.showRequestErrorDialog(getContext());
            return;
        }
        if (!result.equals("")) {
            final ArrayList<Request> requests = Request.getListOfRequestsFromJSON(result);
            final ArrayList<Request> reqList = Request.filterRequestsByType(requests, Utility.getCurrentRequestType(viewPager));
            if (reqList.size() > 0) {
                BrowseAdapter mAdapter = new BrowseAdapter(activity, reqList);
                getListView().setAdapter(mAdapter);
                getListView().setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (offerHandler.isOfferTaskFinished()) {
                            Request req = reqList.get(position);
                            RequestDetailFragment details = new RequestDetailFragment();
                            details.setRequest(req);
                            FragmentManager fm = getFragmentManager();
                            details.show(fm, "Sample Fragment");
                        } else {
                            Utility.longToast(getContext(), R.string.unable_to_display_request_detail);
                        }
                    }
                });
            } else {
                showEmptyMessage();
            }
        } else {
            showEmptyMessage();
        }
    }

    private void showEmptyMessage() {
        textView.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        switch (Utility.getCurrentRequestType(viewPager)) {
            case BORROW:
                textView.setText(R.string.empty_needs_message);
                break;
            case LEND:
                textView.setText(R.string.empty_has_message);
                break;
            default:
                Log.e("ERROR", "Invalid request type");
        }
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

    private class GetUserOffersAsyncTaskHandler implements AsyncTaskHandler {
        private Context context;
        private OfferUtility.GetOfferTask getOfferTask;

        GetUserOffersAsyncTaskHandler(Context context) {
            this.context = context;
        }

        void startGetOffersTask(int profileId) {
            OfferUtility offerUtility = new OfferUtility<>(this);
            getOfferTask = offerUtility.getOffersByUser(context, profileId);
        }

        boolean isOfferTaskFinished() {
            return !isAsyncTaskRunning();
        }

        @Override
        public void beforeAsyncTask() {

        }

        @Override
        public void afterAsyncTask(String result) {
            ArrayList<Offer> offers;
            if (result == null || result.equals("")) {
                UserDataCache.setOffers(null);
                return;
            }
            offers = Offer.getListOfOffersFromJSON(result);
            if (offers.size() == 0) {
                offers = null;
            }
            UserDataCache.setOffers(offers);
        }

        @Override
        public boolean isAsyncTaskRunning() {
            if (getOfferTask == null) {
                return false;
            }
            return getOfferTask.getStatus() != OfferUtility.GetOfferTask.Status.FINISHED ;
        }
    }

}