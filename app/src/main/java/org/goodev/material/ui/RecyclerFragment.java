package org.goodev.material.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.goodev.material.MainActivity;
import org.goodev.material.R;
import org.goodev.material.api.Api;
import org.goodev.material.util.UI;
import org.goodev.ui.StatFragment;
import org.goodev.utils.Utils;
import org.goodev.widget.BaseAdapter;
import org.goodev.widget.DividerItemDecoration;
import org.goodev.widget.EndlessRecyclerView;
import org.goodev.widget.GridSpanSizeLookup;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yfcheng on 2015/11/27.
 */
public abstract class RecyclerFragment<T> extends StatFragment implements EndlessRecyclerView.OnLoadingMoreListener, OnReloadListener {
    @Bind(R.id.recyclerView)
    EndlessRecyclerView mRecyclerView;
    @Bind(R.id.empty)
    TextView mEmpty;
    @Bind(R.id.emptyImage)
    ImageView mEmptyImage;
    @Bind(R.id.progressBar)
    ProgressBar mLoading;
    @Bind(R.id.swipeLayout)
    SwipeRefreshLayout mRefreshLayout;
    int mCurrentPage = 1;
    int mCurrentListPosition;

    ArrayList<T> mDataList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mCurrentListPosition = savedInstanceState.getInt(UI.ARG_CURRENT_LIST_INDEX);
            mCurrentPage = savedInstanceState.getInt(UI.ARG_CURRENT_PAGE, 1);
            Parcelable dataList = savedInstanceState.getParcelable(UI.ARG_DATA_LIST);
            ArrayList<T> datas = Parcels.unwrap(dataList);
            if (datas != null) {
                mDataList.addAll(datas);
            }
        }
    }

    public void checkEmptyOrConnection() {
        if (!Utils.hasInternet(getActivity())) {
            if (getAdapter().getItemCount() == 0) {
                mEmpty.setVisibility(View.GONE);
                mEmptyImage.setVisibility(View.VISIBLE);
                if (UI.isLollipop()) {
                    final AnimatedVectorDrawable avd =
                            (AnimatedVectorDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.avd_no_connection);
                    mEmptyImage.setImageDrawable(avd);
                    avd.start();
                } else {
                    mEmptyImage.setImageResource(R.drawable.no_connection);
                }
            } else {
                Toast.makeText(getActivity(), R.string.check_network, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    public abstract void loadData();

    public abstract BaseAdapter getAdapter();

    public EndlessRecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Context context = getContext();
        int c1 = ContextCompat.getColor(context, R.color.colorPrimary);
        int c2 = ContextCompat.getColor(context, R.color.colorAccent);
        int c3 = ContextCompat.getColor(context, R.color.primary_dark);
        int c4 = ContextCompat.getColor(context, R.color.designer_news);
        mRefreshLayout.setColorSchemeColors(c1, c2, c3, c4);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadFirstPageData();
            }
        });

        EndlessRecyclerView recyclerView = getRecyclerView();
        BaseAdapter adapter = getAdapter();

        int column = getColumnCount();
        RecyclerView.LayoutManager layoutManager;
        if (column >= 2) {
            GridLayoutManager sglManager = new GridLayoutManager(getActivity(), column, GridLayoutManager.VERTICAL, false);
            layoutManager = sglManager;
            sglManager.setSpanSizeLookup(new GridSpanSizeLookup(adapter, column));
        } else {
            LinearLayoutManager llManager = new LinearLayoutManager(getActivity());
            llManager.setOrientation(LinearLayoutManager.VERTICAL);
            layoutManager = llManager;
        }

        getRecyclerView().setLayoutManager(layoutManager);
        //TODO ADS
//        boolean showAds = Pref.isShowHomeAds(getActivity());
//        if (showAds && BuildConfig.IS_PLAY) {
//            Ads.setupAdsApdater(getActivity(), mRecyclerView, mAdapter);
//        } else {
//            mRecyclerView.setAdapter(mAdapter);
//        }

        recyclerView.setAdapter(adapter);
        recyclerView.setEmptyView(mEmpty);
        recyclerView.setLoadingView(mLoading);
        recyclerView.setOnLoadingMoreListener(this);

        DividerItemDecoration divider = getDivider();
        if (divider != null) {
            recyclerView.addItemDecoration(divider);
        } else {
            int p = (int) getResources().getDimension(R.dimen.spacing_micro);
            recyclerView.setPadding(p, p, p, p);
        }
        if (savedInstanceState == null || mDataList.isEmpty()) {
            loadFirstPageData();
        } else {
            adapter.setData(mDataList);
            layoutManager.scrollToPosition(mCurrentListPosition);
        }
    }

    protected int getColumnCount() {
        return getResources().getInteger(R.integer.shot_column);
    }

    protected DividerItemDecoration getDivider() {
        return UI.getDividerItemDecoration(getActivity().getResources());
    }

    public void loadFirstPageData() {
        if (!Utils.hasInternet(getActivity())) {
            checkEmptyOrConnection();
            return;
        }
        mEmptyImage.setVisibility(View.GONE);
        mRecyclerView.setLoading(true, true);
        mCurrentPage = 1;
        loadData();
    }

    public void updateData(List<T> data) {
        mRefreshLayout.setRefreshing(false);
        mRecyclerView.setLoading(false, mCurrentPage == 1);
        for (T t : data) {
            if (mDataList.contains(t)) {
                mDataList.remove(t);
            } else {
                break;
            }
        }
        BaseAdapter mAdapter = getAdapter();
        mAdapter.setLoading(false);
        mRecyclerView.finishLoadingMore(hasNextPage(data));
        if (mCurrentPage == 1) {
            mDataList.clear();
            mAdapter.setData(data);
            mRecyclerView.scrollToPosition(0);
        } else {
            mAdapter.addData(data);
        }

        mDataList.addAll(data);
    }

    public boolean hasNextPage(List<T> data) {
        return Api.hasNextPage(data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int currentIndex = mRecyclerView.findFirstVisibleItemPosition();
        outState.putInt(UI.ARG_CURRENT_LIST_INDEX, currentIndex);
        outState.putInt(UI.ARG_CURRENT_PAGE, mCurrentPage);
        outState.putParcelable(UI.ARG_DATA_LIST, Parcels.wrap(mDataList));
    }

    @Override
    public void onLoadingMore() {
        if (!Utils.hasInternet(getActivity())) {
            Toast.makeText(getActivity(), R.string.check_network, Toast.LENGTH_SHORT).show();
            return;
        }
        getAdapter().setLoading(true);
        mCurrentPage++;
        loadData();
    }

    @Override
    public void isFirstItemFullVisible(boolean firstVisible) {
        mRefreshLayout.setEnabled(firstVisible);
    }

    @Override
    public void onReload() {
        if (getActivity() != null) {
            loadFirstPageData();
        }
    }

    //TODO this method shouldn't be here
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Activity activity = getActivity();
        if (activity instanceof MainActivity) {
            if (isVisibleToUser) {
                ((MainActivity) activity).setReloadListener(this);
            } else {
                ((MainActivity) activity).removeReloadListener(this);
            }
        }
    }
}
