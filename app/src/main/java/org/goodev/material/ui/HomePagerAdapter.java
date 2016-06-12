package org.goodev.material.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.goodev.material.App;
import org.goodev.material.R;
import org.goodev.material.api.UrlUtil;

/**
 * Created by yfcheng on 2015/11/26.
 */
public class HomePagerAdapter extends FragmentStatePagerAdapter {
    public static final int NOTIFICATION = 2;
    Context mContext;

    public HomePagerAdapter(Context ctx, FragmentManager fm) {
        super(fm);
        mContext = ctx;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return PostsFragment.newIns(UrlUtil.HOME_TYPE_LATEST);
//            case 1:
//                return PostsFragment.newIns(UrlUtil.HOME_TYPE_POPULAR);
            case 1:
                return NotificationFragment.newIns();
        }
        return null;
    }

    //    @Override
    public long getItemId(int position) {
        return getPageTitle(position).hashCode();
    }

    @Override
    public int getCount() {
        return App.isLogin() ? 3-1 : 2-1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.latest);
//            case 1:
//                return mContext.getString(R.string.popular);
            case 1:
                return mContext.getString(R.string.notification);
        }
        return super.getPageTitle(position);
    }
}
