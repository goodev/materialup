package org.goodev.material.ui;

import android.os.Bundle;
import android.widget.Toast;

import org.goodev.material.R;
import org.goodev.material.api.Api;
import org.goodev.material.api.ErrorCallback;
import org.goodev.material.api.JsoupUtil;
import org.goodev.material.model.Post;
import org.goodev.material.util.Launcher;
import org.goodev.material.util.UI;
import org.goodev.utils.Utils;
import org.goodev.widget.BaseAdapter;
import org.goodev.widget.DividerItemDecoration;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yfcheng on 2015/12/5.
 */
public class UserPostsFragment extends RecyclerFragment<Post> {

    public static UserPostsFragment newIns(String id, int type) {
        UserPostsFragment fragment = new UserPostsFragment();
        Bundle args = new Bundle();
        args.putInt(UI.TYPE, type);
        args.putString(Launcher.EXTRA_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    PostsAdapter mAdapter;
    int mType;
    String mId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mId = args.getString(Launcher.EXTRA_ID);
            mType = args.getInt(UI.TYPE, UserPagerAdapter.UPVOTED);
        }
        mAdapter = new PostsAdapter(getActivity(), mId);
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

        Api.getApiService().getUserPosts(mId, Api.getUsersContentType(mType), mCurrentPage)
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