package com.seng480b.bumerang;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CreateRequest extends Fragment {
    @Override
    // Fragment Cancel = new Browse();
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_create_request, container, false);
    }
/*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_create_request);
    }
*/
    public void cancelClicked(View view) {
        // go back to the home page fragment
    }

    public void createClicked(View view) {
        //FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        //ft.replace(R.id.mainFrame,);
        //ft.commit();Intent create = new Intent(super.getActivity(), MyRequests.class );
        // Pull the information from the various fields of the form
        // push the collected information to the next activity
    }
}
