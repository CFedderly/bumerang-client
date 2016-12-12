package com.seng480b.bumerang.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.seng480b.bumerang.R;
import com.seng480b.bumerang.fragments.BrowseFragment;

public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
    private static final int NUM_PAGES = 2;
    private Context context;

    public SectionsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return BrowseFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getResources().getString(R.string.borrow_tab_title);
            case 1:
                return context.getResources().getString(R.string.lend_tab_title);
        }
        return null;
    }

    @Override
    public int getItemPosition(Object object) {
        if (object instanceof BrowseFragment) {
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }
}