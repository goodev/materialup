package org.goodev.material;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.goodev.material.api.Api;
import org.goodev.material.api.ErrorCallback;
import org.goodev.material.databinding.ActivityUserBinding;
import org.goodev.material.model.Follow;
import org.goodev.material.model.User;
import org.goodev.material.ui.UserPagerAdapter;
import org.goodev.material.util.L;
import org.goodev.material.util.Launcher;
import org.goodev.material.util.Pref;
import org.goodev.material.util.UI;
import org.goodev.utils.Utils;
import org.parceler.Parcels;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    private UserPagerAdapter mAdapter;
    private ViewPager mViewPager;

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    private User mUser;
    private ActivityUserBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUserBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_user);
        User user = Parcels.unwrap(getIntent().getParcelableExtra(Launcher.EXTRA_USER));
        binding.setUser(user);
        mUser = user;
        mBinding = binding;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        binding.collapsingToolbar.setTitle("");
        binding.toolbar.setTitle("");
        Pref pref = Pref.get(getApplicationContext());
        if (user.path.equals(pref.getUserPath())) {
            binding.menuFollow.setVisibility(View.INVISIBLE);
        } else {
            updateFollowBtn(binding.menuFollow, mUser.isFollowed());
            binding.menuFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleFollow();
                }
            });
        }
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSupportNavigateUp();
            }
        });

        binding.appbar.addOnOffsetChangedListener(this);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mAdapter = new UserPagerAdapter(this, mUser, getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        View.OnClickListener socialListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = null;
                switch (v.getId()) {
                    case R.id.website:
                        url = mUser.website;
                        break;
                    case R.id.dribbble:
                        url = mUser.dribbble;
                        break;
                    case R.id.twitter:
                        url = mUser.twitter;
                        break;
                    case R.id.google:
                        url = mUser.google;
                        break;
                    case R.id.behance:
                        url = mUser.behance;
                        break;
                    case R.id.codepen:
                        url = mUser.codepen;
                        break;
                    case R.id.github:
                        url = mUser.github;
                        break;
                }
                if (!TextUtils.isEmpty(url)) {
                    Launcher.openUrl(UserActivity.this, url);
                }
            }
        };
        binding.website.setOnClickListener(socialListener);
        binding.dribbble.setOnClickListener(socialListener);
        binding.twitter.setOnClickListener(socialListener);
        binding.github.setOnClickListener(socialListener);
        binding.google.setOnClickListener(socialListener);
        binding.behance.setOnClickListener(socialListener);
        binding.codepen.setOnClickListener(socialListener);

//        binding.userLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                binding.userLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                ViewGroup.LayoutParams params = mBinding.collapsingToolbar.getLayoutParams();
//                params.height = mBinding.userLayout.getMeasuredHeight();
//
//
//            }
//        });
    }


    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
    }

    private void handleFollow() {
        if (!Utils.hasInternet(this)) {
            Toast.makeText(this, R.string.check_network, Toast.LENGTH_SHORT).show();
            return;
        }
        if (UI.notLogin(this)) {
            return;
        }
        String path = User.getUserId(mUser.getPath());
        if (mUser.isFollowed()) {
            Api.getApiService().unfollow(path)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(follow -> {
                        updateFollowStatus(follow);
                    }, new ErrorCallback(this));

        } else {
            Api.getApiService().follow(path)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(follow -> {
                        updateFollowStatus(follow);
                    }, new ErrorCallback(this));

        }
    }

    private void updateFollowStatus(Follow follow) {
        mUser.setFollowed(follow.following);
        if (UI.isLollipop()) {
            getContentTransitionManager().beginDelayedTransition(mBinding.toolbar);
        }
        updateFollowBtn(mBinding.menuFollow, follow.following);
    }

    private void updateFollowBtn(Button btn, boolean following) {
        btn.setText(following ? R.string.action_following : R.string.action_follow);
        btn.setSelected(following);
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_user, menu);
//        MenuItem follow = menu.findItem(R.id.action_follow);
//        follow.setTitle(mUser.followed ? R.string.action_following : R.string.action_follow);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            return onSupportNavigateUp();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!mIsTheTitleVisible) {
                mBinding.collapsingToolbar.setTitle(mUser.getFullName());
                mBinding.toolbar.setTitle(mUser.getFullName());
//                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                mBinding.collapsingToolbar.setTitle("");
                mBinding.toolbar.setTitle("");
//                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
//                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
//                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_user, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.about_0));
            return rootView;
        }
    }

}
