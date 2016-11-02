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
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyRequests extends ListFragment implements OnItemClickListener {

    public MyRequests() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myrequests_list, container, false);

        ((Home)getActivity()).setActionBarTitle("My Requests");

        /** make the tabs invisible **/
        ViewPager mViewPager = (ViewPager) getActivity().findViewById(R.id.container);
        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);
        mViewPager.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        /* just a test array list */
        ArrayList<Request> arrayOfRequestTickets = new ArrayList<>();

        Request ticket1 = new Request("Study Buddy", "Someone help me with Math 101!", 1, 22, 400,
                Request.RequestType.BORROW);
        Request ticket2 = new Request("Calculator", "Sharp DAL EL-510RN", 1, 22, 400,
                Request.RequestType.LEND);

        arrayOfRequestTickets.add(ticket1);
        arrayOfRequestTickets.add(ticket2);
        /* end of testing stuff */

        MyRequestAdapter adapter = new MyRequestAdapter(getActivity(), arrayOfRequestTickets);

        super.onActivityCreated(savedInstanceState);
        setListAdapter(adapter);

        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        JSONObject obj = new JSONObject();
        try{
            obj.put("Lender", "Daniel");
            obj.put("Item", "Pencil");
            obj.put("Exp", "5:30");
            obj.put("Phone_No", "(250) 123-4567");
        } catch (JSONException e){
            e.printStackTrace();
        }

        BorrowDialogFragment more_info_dialog = new BorrowDialogFragment(obj);

        FragmentManager fm = getFragmentManager();
        more_info_dialog.show(fm, "Sample Fragment");


    }
}
