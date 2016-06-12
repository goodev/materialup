package org.goodev.material.ui;

import android.os.Bundle;

import org.goodev.material.api.Api;
import org.goodev.material.api.JsoupUtil;
import org.goodev.material.model.Post;
import org.goodev.material.util.L;
import org.goodev.material.util.UI;
import org.goodev.ui.Ads;
import org.goodev.widget.BaseAdapter;
import org.goodev.widget.DividerItemDecoration;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yfcheng on 2015/11/26.
 */
public class PostsFragment extends RecyclerFragment<Post> {

    public static PostsFragment newIns(String type) {
        PostsFragment fragment = new PostsFragment();
        Bundle args = new Bundle();
        args.putString(UI.TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    PostsAdapter mAdapter;
    String mType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mType = args.getString(UI.TYPE);
        }
        mAdapter = new PostsAdapter(getActivity());
    }

    protected DividerItemDecoration getDivider() {
        return null;
    }

    @Override
    public void loadData() {
        Api.getApiService().getHomeStream(mCurrentPage, mType)
                .map(mu -> JsoupUtil.streams(mu))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(muResponse -> updateData(muResponse)
                        , throwable -> handleError(throwable));
    }

    private void handleError(Throwable throwable) {
        throwable.printStackTrace();
        L.e(throwable);
        checkEmptyOrConnection();
    }

    @Override
    public void updateData(List<Post> data) {
        if (mType == null && mCurrentPage == 1 && !UI.isAppInstalled(getActivity(), UI.DROIDDDLE)) {
            data.add(1, UI.getDroidddlePost());
        }

        if (mCurrentPage >= 1) {
            Ads.addAdToShot(data, mAdapter.getAllItems());
        }


        super.updateData(data);
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
