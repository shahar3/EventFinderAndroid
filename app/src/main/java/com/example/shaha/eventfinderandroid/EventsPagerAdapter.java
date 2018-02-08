package com.example.shaha.eventfinderandroid;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.content.Context;

import com.example.shaha.eventfinderandroid.Fragments.LocalEventsFragment;
import com.example.shaha.eventfinderandroid.Fragments.PersonalAreaFragment;
import com.example.shaha.eventfinderandroid.Fragments.UpcomingEventsFragment;

/**
 * Created by shaha on 13/01/2018.
 */

public class EventsPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;

    public EventsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new PersonalAreaFragment();
            case 1:
                return new UpcomingEventsFragment();
            case 2:
                return new LocalEventsFragment();
            default:
                return new PersonalAreaFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return mContext.getString(R.string.personal_area_page_title);
            case 1:
                return mContext.getString(R.string.upcoming_events_page_title);
            case 2:
                return mContext.getString(R.string.local_events_page_title);
            default:
                return mContext.getString(R.string.personal_area_page_title);
        }
    }
}