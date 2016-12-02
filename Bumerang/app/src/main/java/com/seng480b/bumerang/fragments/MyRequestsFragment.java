package com.seng480b.bumerang.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;

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
import com.seng480b.bumerang.utils.caching.UserDataCache;
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private void populateBrowse() {
        showProgressBar();
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
            ListView listView = getListView();
            MyRequestAdapter mAdapter = new MyRequestAdapter(activity, requests);
            listView.setAdapter(mAdapter);
            GetOffersAsyncTaskHandler offerHandler = new GetOffersAsyncTaskHandler(listView, getContext());
            offerHandler.getOffers(requests);
        } else {
            showEmptyMessage();
        }
    }

    @Override
    public boolean isAsyncTaskRunning() {
        if (requestsTask == null) {
            return false;
        }
        return (requestsTask.getStatus() != RequestUtility.GetRequestsTask.Status.FINISHED);
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

    private class GetOffersAsyncTaskHandler implements AsyncTaskHandler {
        private Context context;
        private ListView listView;
        private OfferUtility.GetOfferTask getOfferTask;

        GetOffersAsyncTaskHandler(ListView listView, Context context) {
            this.context = context;
            this.listView = listView;
        }

        public void getOffers(ArrayList<Request> requests) {
            OfferUtility offerUtility = new OfferUtility<>(this);
            getOfferTask = offerUtility.getOffers(context, requests);
        }

        private Offer idInOffersList(ArrayList<Offer> offers, int id) {
            for (Offer offer : offers) {
                if (offer.getRequest().getRequestId() == id) {
                    return offer;
                }
            }
            return null;
        }

        @Override
        public void beforeAsyncTask() {

        }

        @Override
        public void afterAsyncTask(String result) {
            if (result == null || result.equals("")) {
                return;
            }
            ArrayList<Offer> offers = Offer.getListOfOffersFromJSON(result);
            if (offers.size() == 0) {
                return;
            }

            for (int i = 0; i < listView.getChildCount(); i++) {
                View view = listView.getChildAt(i);
                Request request = (Request) listView.getItemAtPosition(i);
                int requestId = request.getRequestId();
                final Offer offer = idInOffersList(offers, requestId);
                if (offer != null) {
                    ImageButton replyButton = (ImageButton) view.findViewById(R.id.buttonReplyWarning);
                    replyButton.setVisibility(View.VISIBLE);
                    replyButton.setFocusable(false);
                    replyButton.setClickable(false);
                    replyButton.setOnClickListener(new ReplyOnClickListener(offer));
                    view.setOnClickListener(new ReplyOnClickListener(offer));
                }
            }
        }

        @Override
        public boolean isAsyncTaskRunning() {
            if (getOfferTask == null) {
                return false;
            }
            return getOfferTask.getStatus() != OfferUtility.GetOfferTask.Status.FINISHED;
        }

        private class ReplyOnClickListener implements View.OnClickListener {
            private Offer offer;

            ReplyOnClickListener(Offer offer) {
                this.offer = offer;
            }

            @Override
            public void onClick(View v) {
                BorrowDialogFragment moreInfoDialog = new BorrowDialogFragment();
                PhoneNumberDialogFragment phoneNumberDialog = new PhoneNumberDialogFragment();

                //TODO: A more elegant solution will be needed. But for now, get the first offer.
                // if the offer has already been accepted then you go to the phone number dialog
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                if (prefs.contains(String.valueOf(offer.getOfferId()))) {
                    phoneNumberDialog.setOfferObj(offer);
                    FragmentManager fm = getFragmentManager();
                    phoneNumberDialog.show(fm, "Sample Fragment");
                // else go to the more info dialog
                }else{
                    moreInfoDialog.setOfferObj(offer);
                    FragmentManager fm = getFragmentManager();
                    moreInfoDialog.show(fm, "Sample Fragment");
                }

            }
        }
    }
}
