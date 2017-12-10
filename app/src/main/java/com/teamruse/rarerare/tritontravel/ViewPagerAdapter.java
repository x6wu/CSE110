package com.teamruse.rarerare.tritontravel;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

/**
 * Created by JingJing on 12/7/17.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG="FragPagerAdapter_tag";

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0)
        {
            fragment = new SavedStops();
            Log.d(TAG, "new SavedStops");
        }
        else if (position == 1)
        {
            fragment = new SavedRoutes();
            Log.d(TAG, "new SaveRoutes");
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0)
        {
            title = "Locations";
        }
        else if (position == 1)
        {
            title = "Routes";
        }

        return title;
    }
}