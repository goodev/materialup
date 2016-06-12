package org.goodev.material.ui;

import android.os.Bundle;
import android.widget.Toast;

import org.goodev.material.R;
import org.goodev.material.api.Api;
import org.goodev.material.api.ErrorCallback;
import org.goodev.material.api.JsoupUtil;
import org.goodev.material.model.Post;
import org.goodev.material.util.Launcher;
import org.goodev.utils.Utils;
import org.goodev.widget.BaseAdapter;
import org.goodev.widget.DividerItemDecoration;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ADMIN on 2015/12/5.
 */
public class CategoryFragment extends RecyclerFragment<Post> {

    public static CategoryFragment newIns(String sort, String cat, String sub) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString(Launcher.EXTRA_SORT, sort);
        args.putString(Launcher.EXTRA_CAT, cat);
        args.putString(Launcher.EXTRA_SUB, sub);
        fragment.setArguments(args);
        return fragment;
    }

    PostsAdapter mAdapter;
    String mSort;
    String mCat;
    String mSub;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mCat = args.getString(Launcher.EXTRA_CAT);
            mSub = args.getString(Launcher.EXTRA_SUB);
            mSort = args.getString(Launcher.EXTRA_SORT);
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

        Api.getApiService().getCategoryStream(mCat, mSub, mCurrentPage, mSort)
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
