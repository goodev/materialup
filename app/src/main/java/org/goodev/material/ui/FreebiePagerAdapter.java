package org.goodev.material.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.goodev.material.R;

/**
 * Created by yfcheng on 2015/12/7.
 */
public class FreebiePagerAdapter extends SortPagerAdapter {

    public static final String CAT_FREEBIES = "freebies";
    public static final String CAT_MARKET = "market";
    public static final String kit = "kit";
    public static final String icon = "icons";
    public static final String illustration = "illustration";
    public static final String theme = "theme";
    public static final String mockup = "mockup";
    public static final String template = "template";

    Context mContext;
    String mCat;

    public FreebiePagerAdapter(Context ctx, String cat, FragmentManager fm) {
        super(fm);
        mContext = ctx;
        mCat = cat;
    }

    //    @Override
    public long getItemId(int position) {
        return getPageTitle(position).hashCode();
    }

    @Override
    public Fragment getItem(int position) {
        String sub = getSub(position);
        return CategoryFragment.newIns(mSort, mCat, sub);
    }

    private String getSub(int position) {
        switch (position) {
            case 0:
                return kit;
            case 1:
                return theme;
            case 2:
                return icon;
            case 3:
                return illustration;
            case 4:
                return mockup;
            case 5:
                return template;
        }
        return kit;
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.kit);
            case 1:
                return mContext.getString(R.string.theme);
            case 2:
                return mContext.getString(R.string.icons);
            case 3:
                return mContext.getString(R.string.illustrations);
            case 4:
                return mContext.getString(R.string.mockup);
            case 5:
                return mContext.getString(R.string.template);
        }
        return super.getPageTitle(position);
    }
}
