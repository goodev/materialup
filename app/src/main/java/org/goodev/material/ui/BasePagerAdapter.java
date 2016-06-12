package org.goodev.material.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by ADMIN on 2015/12/7.
 */
public abstract class BasePagerAdapter extends FragmentStatePagerAdapter {
    Fragment[] mFragment;

    public BasePagerAdapter(FragmentManager fm) {
        super(fm);
        mFragment = new Fragment[getCount()];
    }
}
