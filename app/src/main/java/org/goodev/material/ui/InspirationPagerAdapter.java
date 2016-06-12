package org.goodev.material.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.goodev.material.R;

/**
 * Created by ADMIN on 2015/12/5.
 */
public class InspirationPagerAdapter extends SortPagerAdapter {

    public static final String CAT_INSPIRATION = "inspiration";
    public static final String animation = "animation";
    public static final String application = "application";
    public static final String icon = "icon";
    public static final String illustration = "illustration";
    public static final String ui = "interface";
    public static final String landing = "landing";
    public static final String website = "website";

    Context mContext;

    public InspirationPagerAdapter(Context ctx, FragmentManager fm) {
        super(fm);
        mContext = ctx;
    }

    //    @Override
    public long getItemId(int position) {
        return getPageTitle(position).hashCode();
    }

    @Override
    public Fragment getItem(int position) {
        String sub = getSub(position);
        return CategoryFragment.newIns(mSort, CAT_INSPIRATION, sub);
    }

    private String getSub(int position) {
        switch (position) {
            case 0:
                return animation;
            case 1:
                return application;
            case 2:
                return icon;
            case 3:
                return illustration;
            case 4:
                return ui;
            case 5:
                return landing;
            case 6:
                return website;
        }
        return animation;
    }

    @Override
    public int getCount() {
        return 7;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.animations);
            case 1:
                return mContext.getString(R.string.apps);
            case 2:
                return mContext.getString(R.string.icons);
            case 3:
                return mContext.getString(R.string.illustrations);
            case 4:
                return mContext.getString(R.string.ui);
            case 5:
                return mContext.getString(R.string.landing);
            case 6:
                return mContext.getString(R.string.website);
        }
        return super.getPageTitle(position);
    }
}
