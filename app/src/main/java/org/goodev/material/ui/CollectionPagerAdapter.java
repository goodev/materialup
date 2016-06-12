package org.goodev.material.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.goodev.material.R;

/**
 * Created by yfcheng on 2015/12/7.
 */
public class CollectionPagerAdapter extends FragmentStatePagerAdapter {

    public static final String CAT_COLLECTIONS = "collections";
    public static final String following = "following";
    public static final String my_collections = "my_collections";

    Context mContext;
    boolean mLogin;

    public CollectionPagerAdapter(Context ctx, boolean login, FragmentManager fm) {
        super(fm);
        mContext = ctx;
        mLogin = login;
    }

    //    @Override
    public long getItemId(int position) {
        return getPageTitle(position).hashCode();
    }

    @Override
    public Fragment getItem(int position) {
        String sub = getSub(position);
        return CollectionsFragment.newIns(CAT_COLLECTIONS, sub);
    }

    private String getSub(int position) {
        switch (position) {
            case 0:
                return null;
            case 1:
                return my_collections;
            case 2:
                return following;
        }
        return null;
    }

    @Override
    public int getCount() {
        return mLogin ? 3 : 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.featured);
            case 1:
                return mContext.getString(R.string.my_collections);
            case 2:
                return mContext.getString(R.string.following_collections);
        }
        return super.getPageTitle(position);
    }
}
