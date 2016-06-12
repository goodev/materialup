package org.goodev.material;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;

import org.goodev.material.ui.HomePagerAdapter;
import org.goodev.material.ui.SortPagerAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;

/**
 * Created by yfcheng on 2015/11/30.
 */
public abstract class TabsActivity extends DrawerActivity {


    @Nullable
    @Bind(R.id.fab)
    FloatingActionButton mFab;

    @Bind(R.id.tabs)
    TabLayout mTabLayout;
    @Bind(R.id.viewPager)
    ViewPager mViewPager;
    @Bind(R.id.refreshButton)
    View mRefreshButton;
    @Bind(R.id.searchButton)
    View mSearchButton;
    @Bind(R.id.spinner)
    AppCompatSpinner mSpinner;
    @Bind(R.id.siteSpinner)
    AppCompatSpinner mSiteSpinner;
    String mSortType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        updatePageAdapter();
    }

    public void updatePageAdapter() {
        PagerAdapter adapter = getAdapter();
        if (adapter instanceof SortPagerAdapter) {
            ((SortPagerAdapter) adapter).setSort(mSortType);
        }
        if (adapter instanceof HomePagerAdapter) {
            mRefreshButton.setVisibility(View.VISIBLE);
            mSpinner.setVisibility(View.GONE);
        } else {
            mRefreshButton.setVisibility(View.GONE);
            mSpinner.setVisibility(View.VISIBLE);
        }
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Nullable
    @OnClick({R.id.fab, R.id.refreshButton, R.id.searchButton})
    abstract void onFabClicked(View view);

    @Nullable
    @OnItemSelected(R.id.spinner)
    abstract void onItemSelected(int pos);

    @Nullable
    @OnItemSelected(R.id.siteSpinner)
    abstract void onSwitchSite(int pos);

    protected abstract PagerAdapter getAdapter();
}
