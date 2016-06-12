package org.goodev.material.ui;

import android.os.Bundle;
import android.widget.Toast;

import org.goodev.material.R;
import org.goodev.material.api.Api;
import org.goodev.material.api.ErrorCallback;
import org.goodev.material.api.JsoupUtil;
import org.goodev.material.model.Notification;
import org.goodev.utils.Utils;
import org.goodev.widget.BaseAdapter;
import org.goodev.widget.DividerItemDecoration;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yfcheng on 2015/12/22.
 */
public class NotificationFragment extends RecyclerFragment<Notification> {

    public static NotificationFragment newIns() {
        NotificationFragment fragment = new NotificationFragment();
        return fragment;
    }

    NotificationAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new NotificationAdapter(getActivity());
    }

    protected DividerItemDecoration getDivider() {
        return null;
    }

    @Override
    public void loadData() {
        if (!Utils.hasInternet(getActivity())) {
            Toast.makeText(getActivity(), R.string.check_network, Toast.LENGTH_SHORT).show();
            return;
        }

        Observable<List<Notification>> observable = null;
        observable = Api.getApiService().getNotifications().map(mu -> JsoupUtil.notifications(mu));
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(muResponse -> updateData(muResponse)
                        , new ErrorCallback(getActivity()));
    }

    @Override
    public boolean hasNextPage(List<Notification> data) {
        return false;
    }

    @Override
    public BaseAdapter getAdapter() {
        return mAdapter;
    }

    private void reloadData() {
        mCurrentPage = 1;
        //        mRecyclerView.setLoading(true);
//        mRefreshLayout.setRefreshing(true);
        loadData();
    }


}
