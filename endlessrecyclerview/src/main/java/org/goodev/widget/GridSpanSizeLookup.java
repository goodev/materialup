package org.goodev.widget;

import android.support.v7.widget.GridLayoutManager;

/**
 * Created by goodev on 2015/1/21.
 */
public class GridSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

    BaseAdapter mAdapter;
    int mCount;

    public GridSpanSizeLookup(BaseAdapter adapter, int count) {
        mAdapter = adapter;
        mCount = count;
    }

    @Override
    public int getSpanSize(int position) {
        if (mAdapter.getItemViewType(position) == BaseAdapter.TYPE_LOADING) {
            return mCount;
        }
        return 1;
    }

    @Override
    public int getSpanIndex(int position, int spanCount) {
        return position % spanCount;
    }
}
