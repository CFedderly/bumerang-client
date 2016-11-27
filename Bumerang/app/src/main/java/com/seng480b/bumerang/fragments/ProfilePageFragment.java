package com.seng480b.bumerang.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
import com.seng480b.bumerang.R;
import com.seng480b.bumerang.utils.caching.UserDataCache;
import com.seng480b.bumerang.utils.Utility;
import com.seng480b.bumerang.activities.HomeActivity;
import com.seng480b.bumerang.models.Profile;


public class ProfilePageFragment extends Fragment {
    private View myInflatedView;
    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;
    private int width;
    private int height;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_profile_page, container,false);
        /** set the action bar title **/
        ((HomeActivity)getActivity()).setActionBarTitle("Profile");

        /** make the tabs invisible **/
        ViewPager mViewPager = (ViewPager) getActivity().findViewById(R.id.container);
        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);
        mViewPager.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
        if (UserDataCache.hasProfile()) {
            updateProfileInfo();
        }
        setUpEditButton();
        setupKarmaButton();
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
            profileNumber.setText(Utility.formatPhoneNumber(currProfile.getPhoneNumber()));
            profileTags.setText(currProfile.getTags());
            profileBio.setText(currProfile.getDescription());
            profilePicture.setProfileId(String.valueOf(currProfile.getFacebookId()));
            profileName.setText(currProfile.getFirstName() + " " + currProfile.getLastName());
        }
    }

    public void setUpEditButton(){
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

    public void setupKarmaButton(){
        //This is getting the size of the phone so that the popup is displayed in the middle of the page
        Display display =getActivity().getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
        Button karmaButton = (Button) myInflatedView.findViewById(R.id.profileButtonKarmaHelp);

        karmaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutInflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.fragment_profile_karma_explanation,null);

                popupWindow = new PopupWindow(container, 750, 640,true);
                popupWindow.showAtLocation(myInflatedView, Gravity.NO_GRAVITY, width/6, height/3);
                myInflatedView.setAlpha(0.5F);

                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener(){
                    @Override
                    public void onDismiss() {
                        myInflatedView.setAlpha(1F);
                    }
                });

                //This makes it so that if you click on the popup it will also disappear
                container.setOnTouchListener(new View.OnTouchListener(){
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        myInflatedView.setAlpha(1F);
                        popupWindow.dismiss();
                        return true;
                    }
                });

            }

        });
    }
}
