package com.seng480b.bumerang.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import static android.R.attr.statusBarColor;
import static com.seng480b.bumerang.utils.Utility.getCurrentRequestType;
import static com.seng480b.bumerang.utils.Utility.longToast;

public class MyRequestsFragment extends ListFragment implements OnItemClickListener, AsyncTaskHandler {
    private ViewPager mViewPager;
    private Activity activity;
    private ProgressBar progressBar;
    private TextView textView;
    private ListView listView;
    private RequestUtility.GetRequestsTask requestsTask;
    private int selectedPosition;
    private ActionMode mActionMode;

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

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position;
                if (mActionMode != null) {
                    return false;
                }

                mActionMode = getActivity().startActionMode(mActionModeCallback);
                return true;
            }
        });

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
            MyRequestAdapter listAdapter = new MyRequestAdapter(activity, requests);
            listView.setAdapter(listAdapter);
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


    private void deleteRequestDialog(final int position) {
        // build dialog to confirm deletion
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.delete_request_title)
                .setMessage(R.string.delete_request_message)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteRequest(position);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void deleteRequest(int position) {
        Request request = (Request) listView.getAdapter().getItem(position);
        DeleteRequestAsyncTaskHandler deleteRequestAsyncTaskHandler = new DeleteRequestAsyncTaskHandler(this.listView);
        deleteRequestAsyncTaskHandler.deleteRequest(request);
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

    private class DeleteRequestAsyncTaskHandler implements AsyncTaskHandler {
        private ListView listView;
        private Request request;
        private RequestUtility requestUtility;
        private RequestUtility.DeleteRequestTask deleteRequestTask;

        DeleteRequestAsyncTaskHandler(ListView listView) {
            this.listView = listView;
        }

        @Override
        public void beforeAsyncTask() {

        }

        @Override
        public void afterAsyncTask(String result) {
            if (result == null) {
                longToast(getContext(), R.string.delete_request_fail_message);
            } else {
                longToast(getContext(), R.string.delete_request_success_message);
                MyRequestAdapter deleteAdapter = (MyRequestAdapter) listView.getAdapter();
                deleteAdapter.remove(request);
                deleteAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public boolean isAsyncTaskRunning() {
            if (deleteRequestTask == null) {
                return false;
            }
            return deleteRequestTask.getStatus() != RequestUtility.DeleteRequestTask.Status.FINISHED;
        }

        public void deleteRequest(Request request) {
            requestUtility = new RequestUtility<>(this);
            this.request = request;
            deleteRequestTask = requestUtility.deleteRequest(getContext(), request.getRequestId());
        }
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
                    final int position = i;
                    view.setOnLongClickListener(new View.OnLongClickListener() {
                        private int listPosition = position;
                        @Override
                        public boolean onLongClick(View v) {
                            selectedPosition = listPosition;
                            if (mActionMode != null) {
                                return false;
                            }

                            mActionMode = getActivity().startActionMode(mActionModeCallback);
                            return true;
                        }
                    });
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
                    phoneNumberDialog.show(fm, "Phone number Dialog");
                // else go to the more info dialog
                }else{
                    moreInfoDialog.setOfferObj(offer);
                    FragmentManager fm = getFragmentManager();
                    moreInfoDialog.show(fm, "Accept offer Dialog");
                }

            }
        }
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        private int statusBarColor;
        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //hold current color of status bar
                statusBarColor = getActivity().getWindow().getStatusBarColor();
                //set your gray color
                getActivity().getWindow().setStatusBarColor(0xFF555555);
            }
            // Hide the action bar
            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
            toolbar.setVisibility(View.GONE);
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.my_request_menu, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.my_request_delete:
                    deleteRequestDialog(selectedPosition);
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //return to "old" color of status bar
                getActivity().getWindow().setStatusBarColor(statusBarColor);
            }
            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
            toolbar.setVisibility(View.VISIBLE);
        }
    };
}
