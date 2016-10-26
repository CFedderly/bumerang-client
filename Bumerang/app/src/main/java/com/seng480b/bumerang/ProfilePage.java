package com.seng480b.bumerang;


import android.support.v4.app.Fragment;

import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;

public class ProfilePage extends Fragment {
    private View myInflatedView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_profile_page, container,false);
        updateProfileInfo();
        setUpButton();
        return myInflatedView;
    }



    //grabs profile info from db
    //currently grabs some data from fb
    public void updateProfileInfo() {
        String user_id;
        String user_name;
        //these are just temp
        //grab them from the db later
        String user_tags = "Mac_Book, Power_Cable, Pencil, Calculator, Paper";
        String user_bio = "I am realy awesome, please let me borrow your things!";
        String user_number = "1-111-111-1111";
        String user_carma = "1.3";

        //TEMP! grabbing info from facebook
        Profile profile = Profile.getCurrentProfile();
        user_id = profile.getId();
        user_name = profile.getName();
        Log.d("user id ", user_id);
        //********* put db requests here *************
        Log.d("user name ", user_name);
        //********* put db requests here *************
        //TODO grab from database: name,bio,tags,carma,phone number



        //********* assign values to the views *************
        TextView profile_name = (TextView) myInflatedView.findViewById(R.id.profileName);
        TextView profile_bio = (TextView) myInflatedView.findViewById(R.id.profileBio);
        TextView profile_tags = (TextView) myInflatedView.findViewById(R.id.profileTags);
        TextView profile_number = (TextView) myInflatedView.findViewById(R.id.profilePhoneNumber);
        TextView profile_carma = (TextView) myInflatedView.findViewById(R.id.profileCarma);
        ProfilePictureView profile_picture = (ProfilePictureView) myInflatedView.findViewById(R.id.profilePicture);

        profile_carma.setText(user_carma);
        profile_number.setText(user_number);
        profile_tags.setText(user_tags);
        profile_bio.setText(user_bio);
        profile_picture.setProfileId(profile.getId());
        profile_name.setText(user_name);

        ((Home)getActivity()).setActionBarTitle("Profile");

        /** make the tabs invisible **/
        ViewPager mViewPager = (ViewPager) getActivity().findViewById(R.id.container);
        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);
        mViewPager.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);


    }

    public void setUpButton(){
        Button editButton = (Button) myInflatedView.findViewById(R.id.profileButtonEdit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new EditProfileFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.mainFrame, fragment );
                transaction.addToBackStack(null);
                transaction.commit();
            }

        });

    }

}
