package org.goodev.material.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.goodev.material.R;

/**
 * Created by ADMIN on 2015/12/5.
 * TODO 需要特殊处理
 */
public class ResourcePagerAdapter extends SortPagerAdapter {

    public static final String CAT_RESOURCES = "resources";
    public static final String animation = "animation";
    public static final String extension = "extension";
    public static final String framework = "framework";
    public static final String generator = "generator";
    public static final String library = "library";
    public static final String software = "software";
    public static final String article = "article";
    public static final String guide = "guide";
    public static final String tutorial = "tutorial";
    public static final String snippet = "snippet";
    public static final String osapps = "open-source-apps";

    Context mContext;

    public ResourcePagerAdapter(Context ctx, FragmentManager fm) {
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
        return ResourceFragment.newIns(mSort, CAT_RESOURCES, sub);
    }

    private String getSub(int position) {
        switch (position) {
            case 0:
                return extension;
            case 1:
                return framework;
            case 2:
                return generator;
            case 3:
                return library;
            case 4:
                return software;
            case 5:
                return article;
            case 6:
                return guide;
            case 7:
                return tutorial;
            case 8:
                return snippet;
            case 9:
                return osapps;
        }
        return extension;
    }

    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.extension);
            case 1:
                return mContext.getString(R.string.framework);
            case 2:
                return mContext.getString(R.string.generator);
            case 3:
                return mContext.getString(R.string.libraries);
            case 4:
                return mContext.getString(R.string.software);
            case 5:
                return mContext.getString(R.string.article);
            case 6:
                return mContext.getString(R.string.guide);
            case 7:
                return mContext.getString(R.string.tutorial);
            case 8:
                return mContext.getString(R.string.snippet);
            case 9:
                return mContext.getString(R.string.osapps);
        }
        return super.getPageTitle(position);
    }
}
