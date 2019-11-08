package com.example.groupproject.ui.relations;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.groupproject.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_following, R.string.tab_text_followers, R.string.tab_text_requests};
    private final Context mContext;
    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        /**
         * Instantiates the fragment for the given page.
         * @param position Position on tab list of item
         * @return Fragment inflated fragment with contents
         */
        // getItem is called to

        switch(position){
            case 0:
                return new FollowingFragment();
            case 1:
                return new FollowersFragment();
            case 2:
                return new RequestsFragment();
        }
        // Default case;
        return GeneralizedFragment.newInstance(position + 1);

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        /**
         * Returns title of page in position
         * @param position - Index of page on tab list.
         * @return String Name of the page in position
         */
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        /**
         * Returns total number of pages in tab list
         * @return int Number of pages
         */
        return TAB_TITLES.length;
    }
}