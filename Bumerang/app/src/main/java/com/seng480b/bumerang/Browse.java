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


public class Browse extends ListFragment implements OnItemClickListener {

    public Browse() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browse_list, container, false);

        ((Home)getActivity()).setActionBarTitle("Lend");

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        /* just a test array list */
        ArrayList<DummyRequest> array_of_request_tickets = new ArrayList<>();
        DummyRequest ticket1 = new DummyRequest("Bob", "Pencil");
        DummyRequest ticket2 = new DummyRequest("Jeff", "Mac Charger");
        DummyRequest ticket3 = new DummyRequest("Ned", "HLTX9000 Calculator");
        DummyRequest ticket4 = new DummyRequest("Molly", "Some paper");
        DummyRequest ticket5 = new DummyRequest("Polly", "A sort of adapter thing I dunno");

        array_of_request_tickets.add(ticket1);
        array_of_request_tickets.add(ticket2);
        array_of_request_tickets.add(ticket3);
        array_of_request_tickets.add(ticket4);
        array_of_request_tickets.add(ticket5);
        /* end of testing stuff */

        RequestAdapter adapter = new RequestAdapter(getActivity(),array_of_request_tickets);

        super.onActivityCreated(savedInstanceState);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        DetailFragment details = new DetailFragment();

        FragmentManager fm = getFragmentManager();
        details.show(fm,"Sample Fragment");


    }
}