package com.seng480b.bumerang;

import android.support.v4.app.Fragment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;

public class ProfilePage extends Fragment {
    private View myInflatedView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_profile_page, container,false);
        /** set the action bar title **/
        ((Home)getActivity()).setActionBarTitle("Profile");

        /** make the tabs invisible **/
        ViewPager mViewPager = (ViewPager) getActivity().findViewById(R.id.container);
        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);
        mViewPager.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
        if (UserDataCache.hasProfile()) {
            updateProfileInfo();
        }
        setUpButton();
        return myInflatedView;
    }

    //grabs profile info from db
    //currently grabs some data from fb
    public void updateProfileInfo() {
        if (UserDataCache.hasProfile()) {
            Profile currProfile = UserDataCache.getCurrentUser();

            // Populate the profile fields
            TextView profileName = (TextView) myInflatedView.findViewById(R.id.profileName);
            TextView profileBio = (TextView) myInflatedView.findViewById(R.id.profileBio);
            TextView profileTags = (TextView) myInflatedView.findViewById(R.id.profileTags);
            TextView profileNumber = (TextView) myInflatedView.findViewById(R.id.profilePhoneNumber);
            TextView profileKarma = (TextView) myInflatedView.findViewById(R.id.profileCarma);
            ProfilePictureView profilePicture = (ProfilePictureView) myInflatedView.findViewById(R.id.profilePicture);

            profileKarma.setText(String.valueOf(currProfile.getKarma()));
            profileNumber.setText(currProfile.getPhoneNumber());
            profileTags.setText(currProfile.getTags());
            profileBio.setText(currProfile.getDescription());
            profilePicture.setProfileId(String.valueOf(currProfile.getFacebookId()));
            profileName.setText(currProfile.getFirstName() + " " + currProfile.getLastName());
        }
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
