package com.seng480b.bumerang;


import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;



public class MyRequests extends ListFragment implements OnItemClickListener {

    public MyRequests() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myrequests_list, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        /* just a test array list */
        ArrayList<DummyRequest> array_of_request_tickets = new ArrayList<>();
        DummyRequest ticket1 = new DummyRequest("you", "Charger");
        DummyRequest ticket2 = new DummyRequest("you", "Pants");

        array_of_request_tickets.add(ticket1);
        array_of_request_tickets.add(ticket2);
        /* end of testing stuff */

        BorrowAdapter adapter = new BorrowAdapter(getActivity(), array_of_request_tickets);

        super.onActivityCreated(savedInstanceState);
        setListAdapter(adapter);

        getListView().setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        BorrowDialogFragment more_info_dialog = new BorrowDialogFragment();

        FragmentManager fm = getFragmentManager();
        more_info_dialog.show(fm, "Sample Fragment");


    }
}
