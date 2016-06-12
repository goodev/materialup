package org.goodev.material.ui;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseIntArray;

import org.goodev.material.R;
import org.goodev.material.model.User;

/**
 * Created by ADMIN on 2015/12/4.
 */
public class UserPagerAdapter extends FragmentStatePagerAdapter {
    public static final int UPVOTED = 0;
    public static final int CREATED = 1;
    public static final int SHOWCASED = 2;
    public static final int COLLECTIONS = 3;
    public static final int FOLLOWERS = 4;
    public static final int FOLLOWING = 5;

    private Activity mActivity;
    private User mUser;
    private String mId;
    private SparseIntArray mArray;

    public UserPagerAdapter(Activity context, User user, FragmentManager fm) {
        super(fm);
        mActivity = context;
        mUser = user;
        mArray = new SparseIntArray(6);
        mId = User.getUserId(mUser.path);
        int key = 0;
        if (user.getUpvoted() > 0) {
            mArray.append(key, UPVOTED);
            key++;
        }
        if (user.getCreated() > 0) {
            mArray.append(key, CREATED);
            key++;
        }
        if (user.getShowcased() > 0) {
            mArray.append(key, SHOWCASED);
            key++;
        }
        if (user.getCollections() > 0) {
            mArray.append(key, COLLECTIONS);
            key++;
        }
        if (user.getFollowers() > 0) {
            mArray.append(key, FOLLOWERS);
            key++;
        }
        if (user.getFollowing() > 0) {
            mArray.append(key, FOLLOWING);
            key++;
        }
    }

    @Override
    public Fragment getItem(int position) {
        int type = mArray.get(position);
        switch (type) {
            case UPVOTED:
            case CREATED:
            case SHOWCASED:
                return UserPostsFragment.newIns(mId, type);
            case COLLECTIONS:
                return CollectionsFragment.newIns(mId, type);
            case FOLLOWERS:
            case FOLLOWING:
                return UsersFragment.newIns(mId, type);
        }
        return UserPostsFragment.newIns(mId, mArray.get(position));
    }

    @Override
    public int getCount() {
        return mArray.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (mArray.get(position)) {
            case UPVOTED:
                return mActivity.getString(R.string.upvoted, mUser.getUpvoted());
            case CREATED:
                return mActivity.getString(R.string.created, mUser.getCreated());
            case SHOWCASED:
                return mActivity.getString(R.string.showcased, mUser.getShowcased());
            case COLLECTIONS:
                return mActivity.getString(R.string.collections, mUser.getCollections());
            case FOLLOWERS:
                return mActivity.getString(R.string.followers, mUser.getFollowers());
            case FOLLOWING:
                return mActivity.getString(R.string.following, mUser.getFollowing());
        }
        return null;
    }
}