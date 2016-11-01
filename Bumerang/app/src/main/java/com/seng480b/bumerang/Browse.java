package com.seng480b.bumerang;

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

import java.util.ArrayList;

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

        ArrayList<Request> arrayOfRequestTickets = new ArrayList<>();
        // TODO: Pull requests from the DB
        RequestAdapter adapter = new RequestAdapter(getActivity().getApplicationContext(), arrayOfRequestTickets);
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