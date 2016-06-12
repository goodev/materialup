package org.goodev.material.ui;

import android.os.Bundle;
import android.widget.Toast;

import org.goodev.material.R;
import org.goodev.material.api.Api;
import org.goodev.material.api.ErrorCallback;
import org.goodev.material.model.Collection;
import org.goodev.material.util.Launcher;
import org.goodev.utils.Utils;
import org.goodev.widget.BaseAdapter;
import org.goodev.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yfcheng on 2015/12/9.
 */
public class SelectCollectionFragment extends RecyclerFragment<Collection> {

    public static SelectCollectionFragment newIns(long id) {
        SelectCollectionFragment fragment = new SelectCollectionFragment();
        Bundle args = new Bundle();
        args.putLong(Launcher.EXTRA_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    SelectCollectionAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long postId = getArguments().getLong(Launcher.EXTRA_ID);
        mAdapter = new SelectCollectionAdapter(getActivity(), postId);
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

        //TODO need fix
        if (mAdapter.getItemCount() > 0) {
            updateData(new ArrayList<>());
            return;
        }

        Observable<List<Collection>> observable = null;
        observable = Api.getApiService().getMyCollections(mCurrentPage);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(muResponse -> updateData(muResponse)
                        , new ErrorCallback(getActivity()));
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
