package org.goodev.material.ui;

import android.os.Bundle;
import android.widget.Toast;

import org.goodev.material.R;
import org.goodev.material.api.Api;
import org.goodev.material.api.ErrorCallback;
import org.goodev.material.api.JsoupUtil;
import org.goodev.material.model.Post;
import org.goodev.material.util.UI;
import org.goodev.utils.Utils;
import org.goodev.widget.BaseAdapter;
import org.goodev.widget.DividerItemDecoration;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A placeholder fragment containing a simple view.
 */
public class CollectionPostsFragment extends RecyclerFragment<Post> {

    public static CollectionPostsFragment newIns(String type) {
        CollectionPostsFragment fragment = new CollectionPostsFragment();
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
        if (!Utils.hasInternet(getActivity())) {
            Toast.makeText(getActivity(), R.string.check_network, Toast.LENGTH_SHORT).show();
            return;
        }

        Api.getApiService().getCollectionPosts(mType, mCurrentPage)
                .map(mu -> JsoupUtil.streams(mu))
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
