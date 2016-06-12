package org.goodev.material.ui;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.goodev.material.util.L;

/**
 * Created by yfcheng on 2015/12/14.
 */
public abstract class SortPagerAdapter extends FragmentStatePagerAdapter {
    String mSort;

    public SortPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setSort(String sort) {
        if ((sort == null && mSort == null) || (sort != null && sort.equals(mSort))) {
            return;
        }
        mSort = sort;
        notifyDataSetChanged();
    }

}
