package org.goodev.material.ui;

import android.os.Bundle;
import android.widget.Toast;

import org.goodev.material.R;
import org.goodev.material.api.Api;
import org.goodev.material.api.ApiService;
import org.goodev.material.api.ErrorCallback;
import org.goodev.material.api.JsoupUtil;
import org.goodev.material.model.Collection;
import org.goodev.material.model.MuResponse;
import org.goodev.material.util.Launcher;
import org.goodev.material.util.UI;
import org.goodev.utils.Utils;
import org.goodev.widget.BaseAdapter;
import org.goodev.widget.DividerItemDecoration;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yfcheng on 2015/12/5.
 */
public class CollectionsFragment extends RecyclerFragment<Collection> {

    public static CollectionsFragment newIns(String id, int type) {
        CollectionsFragment fragment = new CollectionsFragment();
        Bundle args = new Bundle();
        args.putInt(UI.TYPE, type);
        args.putString(Launcher.EXTRA_ID, id);
        args.putBoolean(Launcher.EXTRA_USER_COLLECTIONS, true);
        fragment.setArguments(args);
        return fragment;
    }

    public static CollectionsFragment newIns(String cat, String sub) {
        CollectionsFragment fragment = new CollectionsFragment();
        Bundle args = new Bundle();
        args.putString(Launcher.EXTRA_CAT, cat);
        args.putString(Launcher.EXTRA_SUB, sub);
        args.putBoolean(Launcher.EXTRA_USER_COLLECTIONS, false);
        fragment.setArguments(args);
        return fragment;
    }

    CollectionsAdapter mAdapter;
    String mId;
    int mType;
    boolean mUserCollections;

    String mCat;
    String mSub;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mId = args.getString(Launcher.EXTRA_ID);
            mType = args.getInt(UI.TYPE, UserPagerAdapter.COLLECTIONS);
            mUserCollections = args.getBoolean(Launcher.EXTRA_USER_COLLECTIONS);
            mCat = args.getString(Launcher.EXTRA_CAT, null);
            mSub = args.getString(Launcher.EXTRA_SUB, null);
        }
        mAdapter = new CollectionsAdapter(getActivity(), mId);
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

        ApiService apiService = Api.getApiService();
        Observable<MuResponse> observable = null;
        if (mUserCollections) {
            observable = apiService.getUserPosts(mId, Api.getUsersContentType(mType), mCurrentPage);
        } else if (mSub == null) {
            observable = apiService.getFeaturedCollection(mCurrentPage);
        } else {
            observable = apiService.getCollections(mSub, mCurrentPage);
        }
        observable
                .map(mu -> JsoupUtil.getCollections(mu))
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
