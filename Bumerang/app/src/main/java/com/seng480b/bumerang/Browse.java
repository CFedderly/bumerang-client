package com.seng480b.bumerang;

import android.annotation.SuppressLint;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentTransaction;
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

        ((Home)getActivity()).setActionBarTitle("Browse");

        /** make the tabs visible **/
        ViewPager mViewPager = (ViewPager) getActivity().findViewById(R.id.container);
        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);
        mViewPager.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {


        /*

        View rootView = inflater.inflate(R.layout.fragment_browse_tabs, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        return rootView;
         */


        /* just a test array list */
        ArrayList<DummyRequest> array_of_request_tickets = new ArrayList<>();
        DummyRequest ticket1 = new DummyRequest("Bob", "Pencil");
        DummyRequest ticket2 = new DummyRequest("Jeff", "Mac Charger");
        DummyRequest ticket3 = new DummyRequest("Ned", "HLTX9000 Calculator");
        DummyRequest ticket4 = new DummyRequest("Molly", "Some paper");
        DummyRequest ticket5 = new DummyRequest("Polly", "A sort of adapter thing I dunno");


        int test = 0;

        if (getArguments() == null){
            Log.d("ARGUMENTS","null value");
        } else {
            test = getArguments().getInt(ARG_SECTION_NUMBER);
        }


        if (test == 1){
            array_of_request_tickets.add(ticket1);
            array_of_request_tickets.add(ticket2);
            array_of_request_tickets.add(ticket3);
            array_of_request_tickets.add(ticket1);
            array_of_request_tickets.add(ticket2);
            array_of_request_tickets.add(ticket3);
            array_of_request_tickets.add(ticket4);

        } else {
            array_of_request_tickets.add(ticket5);
        }

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

}