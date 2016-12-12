package com.seng480b.bumerang.fragments;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
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
    private static final String TAG = "BrowseFragment";
    private Activity activity;
    private ProgressBar progressBar;
    private TextView textView;
    private ListView listView;
    private RequestUtility.GetRequestsTask requestsTask;
    private GetUserOffersAsyncTaskHandler offerHandler;

    public BrowseFragment() {
        // Required empty public constructor
    }

    public static BrowseFragment newInstance(int sectionNumber) {
        BrowseFragment fragment = new BrowseFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
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

        ((HomeActivity) activity).setActionBarTitle("Browse");
        listView = getListView();

        final RequestUtility requestUtility = new RequestUtility<>(this);

        // Start task to get offers from the logged in user
        offerHandler = new GetUserOffersAsyncTaskHandler(getContext());

        getRequestsAndOffers(requestUtility);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) { }

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
            final ArrayList<Request> reqList = Request.filterRequestsByType(requests,
                    Utility.getCurrentRequestType(getArguments().getInt(ARG_SECTION_NUMBER)));
            if (reqList.size() > 0) {
                BrowseAdapter mAdapter = new BrowseAdapter(activity, reqList);
                listView.setAdapter(mAdapter);
                listView.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (offerHandler.isOfferTaskFinished()) {
                            Request request = reqList.get(position);
                            RequestFragment requestFragment = new RequestFragment();
                            requestFragment.setRequest(request);
                            Log.d(TAG, "Replacing with RequestFragment");
                            ((HomeActivity)activity).getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.mainFrame, requestFragment)
                                    .addToBackStack(null)
                                    .commit();
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

    private void getRequestsAndOffers(RequestUtility requestUtility) {
        if (isAsyncTaskRunning()) {
            showProgressBar();
        } else {
            hideProgressBar();
            requestsTask = requestUtility.getRequestsByRecent(getContext());
        }
        if (!offerHandler.isAsyncTaskRunning()) {
            offerHandler.startGetOffersTask(UserDataCache.getCurrentUser().getUserId());
        }
    }

    private void showEmptyMessage() {
        textView.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        Request.RequestType requestType = Utility.getCurrentRequestType(getArguments().getInt(ARG_SECTION_NUMBER));
        if (requestType != null) {
            switch (requestType) {
                case BORROW:
                    textView.setText(R.string.empty_needs_message);
                    break;
                case LEND:
                    textView.setText(R.string.empty_has_message);
                    break;
                default:
                    Log.e(TAG, "Invalid request type");
            }
        } else {
            Log.e(TAG, "Request type is null");
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
            getOfferTask = null;
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