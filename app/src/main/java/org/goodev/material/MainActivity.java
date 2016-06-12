package org.goodev.material;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import org.goodev.material.api.Api;
import org.goodev.material.api.ErrorCallback;
import org.goodev.material.api.JsoupUtil;
import org.goodev.material.api.UrlUtil;
import org.goodev.material.ui.CollectionPagerAdapter;
import org.goodev.material.ui.Dialogs;
import org.goodev.material.ui.FreebiePagerAdapter;
import org.goodev.material.ui.HomePagerAdapter;
import org.goodev.material.ui.InspirationPagerAdapter;
import org.goodev.material.ui.OnReloadListener;
import org.goodev.material.ui.ResourcePagerAdapter;
import org.goodev.material.util.AppRater;
import org.goodev.material.util.L;
import org.goodev.material.util.Launcher;
import org.goodev.material.util.Pref;
import org.goodev.material.util.UI;
import org.goodev.material.widget.AbsTitleAdapter;
import org.goodev.ui.Ads;
import org.goodev.ui.AdsActivity;
import org.goodev.utils.Utils;

import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.plugins.RxJavaErrorHandler;
import rx.plugins.RxJavaPlugins;
import rx.schedulers.Schedulers;

public class MainActivity extends TabsActivity {

    private static final int LOGIN_CODE = 111;
    private static final int RC_SEARCH = 112;
    private FragmentStatePagerAdapter mAdapter;
    private Pref mPref;
    private OnReloadListener mReloadListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPref = Pref.get(this);
        mPrefSite = mPref.getCurrentSite();
        Api.resetApiService(mPrefSite);
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        Ads.checkUpdate(this);
        mAdapter = new HomePagerAdapter(this, getSupportFragmentManager());

        RxJavaErrorHandler handler = RxJavaPlugins.getInstance().getErrorHandler();
        if (handler == null) {
            RxJavaPlugins.getInstance().registerErrorHandler(new RxJavaErrorHandler() {
                @Override
                public void handleError(Throwable e) {
                    super.handleError(e);
                    L.e("error", e);
                    e.printStackTrace();
                }
            });
        }

        if (navView != null) {
            View view = navView.getHeaderView(0);
            if (view != null) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (App.isLogin()) {
                            String path = mPref.getUserPath();
                            if (path != null) {
                                Launcher.launchUser(MainActivity.this, path, mPref.getUserAvatar(), null, null);
                            } else {
                                UI.showToast(MainActivity.this, R.string.already_login);
                            }
                        } else {
                            Launcher.openLogin(MainActivity.this, LOGIN_CODE);
                        }
                    }
                });
            }
        }

        new AppRater(this)
//                .setAppTitle(getString(R.string.app_name))
                .init();
        showInfoOnFirstLaunch();

        checkLogin();

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                App app = (App) getApplicationContext();
                String name = mAdapter.getClass().getSimpleName() + ":" + mAdapter.getPageTitle(position);
                app.getStats().track(name);
                if (mAdapter instanceof HomePagerAdapter) {
                    if (position == HomePagerAdapter.NOTIFICATION) {
                        Api.getApiService().patchNotifications()
                                .delay(5, TimeUnit.SECONDS)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(collect -> {
                                }
                                        , new ErrorCallback(MainActivity.this));
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

//        ImageView logo = (ImageView) findViewById(R.id.logo);
//        Drawable drawable = logo.getDrawable();
//        if (drawable != null && drawable instanceof Animatable) {
//            ((Animatable) drawable).start();
//        }

        setTitle(null);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        String name = mPref.getUserName();
        if (name != null) {
            View header = navView.getHeaderView(0);
            TextView nameView = (TextView) header.findViewById(R.id.userName);
            nameView.setText(name);

            SimpleDraweeView view = (SimpleDraweeView) header.findViewById(R.id.userImage);
            try {
                view.setImageURI(Uri.parse(mPref.getUserAvatar()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (Build.VERSION.SDK_INT >= 23) {
            AbsTitleAdapter spinnerAdapter = new AbsTitleAdapter(this);
            spinnerAdapter.addItems(getResources().getStringArray(R.array.siteEntries));
            mSiteSpinner.setAdapter(spinnerAdapter);
        }
        mSiteSpinner.setSelection(mPrefSite);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_CODE && resultCode == RESULT_OK) {
            checkLogin();
        } else if (requestCode == RC_SEARCH) {
            if (mSearchButton != null) {
                mSearchButton.setAlpha(1f);
            }
        }
    }

    private void checkLogin() {
        if (!Utils.hasInternet(this)) {
            Toast.makeText(this, R.string.check_network, Toast.LENGTH_SHORT).show();
            return;
        }

        Api.getApiService().getLoginUserInfo().map(response -> JsoupUtil.isLogin(response))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(login -> setLoginStatus(login), new ErrorCallback(this));
    }

    private void showInfoOnFirstLaunch() {
        if (mPref.isFirstLaunch()) {
            mPref.setFirstlanch();
            Dialogs.showFirstLaunchInfo(this);
        }
    }

    public void setReloadListener(OnReloadListener listener) {
        mReloadListener = listener;
    }

    public void removeReloadListener(OnReloadListener listener) {
        if (mReloadListener == listener) {
            mReloadListener = null;
        }
    }

    @Nullable
    @Override
    void onFabClicked(View view) {
        App app = (App) getApplicationContext();
        switch (view.getId()) {
            case R.id.refreshButton:
                app.getStats().track("bar", "refresh");
                if (mReloadListener != null) {
                    mReloadListener.onReload();
                }
                break;
            case R.id.fab:
                app.getStats().track("bar", "create");
                Launcher.openCreateActivity(this, view);
                break;
            case R.id.searchButton:
                app.getStats().track("bar", "search");
                int[] loc = new int[2];
                mSearchButton.getLocationOnScreen(loc);
                startActivityForResult(SearchActivity.createStartIntent(this, loc[0], loc[0] +
                        (mSearchButton.getWidth() / 2)), RC_SEARCH, ActivityOptionsCompat
                        .makeSceneTransitionAnimation(this).toBundle());
                mSearchButton.setAlpha(0f);
                break;
        }
    }

    @Nullable
    @Override
    void onItemSelected(int pos) {
        mSortType = pos == 0 ? UrlUtil.HOME_TYPE_LATEST : UrlUtil.HOME_TYPE_POPULAR;
        App app = (App) getApplicationContext();
        app.getStats().track("sort", String.valueOf(pos));
        handleNavItem(mPreDrawerId);
//        if (mAdapter instanceof SortPagerAdapter) {
//            ((SortPagerAdapter) mAdapter).setSort(mSortType);
//        }
    }

    private int mPrefSite;
    @Nullable
    @Override
    void onSwitchSite(int pos) {
        Api.resetApiService(pos);
        if (mPrefSite == pos) {
            return;
        }
        mPrefSite = pos;
        mPref.updateCurrentSite(pos);
        handleNavItem(mPreDrawerId);
    }

    public String getSortType() {
        return mSortType;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        App app = (App) getApplicationContext();
        app.getStats().track("drawer", item.getTitle().toString());
        if (mPreDrawerId == id && !isOther(id)) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        if (!isOther(id)) {
            mPreDrawerId = id;
        }
        handleNavItem(id);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean isOther(int id) {
        return id == R.id.nav_share || id == R.id.nav_about || id == R.id.nav_feedback || id == R.id.nav_app;
    }

    private void handleNavItem(int id) {
        if (id == R.id.nav_inspiration) {
            mAdapter = new InspirationPagerAdapter(this, getSupportFragmentManager());
            updatePageAdapter();
        } else if (id == R.id.nav_home) {
            mAdapter = new HomePagerAdapter(this, getSupportFragmentManager());
            updatePageAdapter();
        } else if (id == R.id.nav_resources) {
            mAdapter = new ResourcePagerAdapter(this, getSupportFragmentManager());
            updatePageAdapter();
        } else if (id == R.id.nav_freebies) {
            mAdapter = new FreebiePagerAdapter(this, FreebiePagerAdapter.CAT_FREEBIES, getSupportFragmentManager());
            updatePageAdapter();
        } else if (id == R.id.nav_market) {
            mAdapter = new FreebiePagerAdapter(this, FreebiePagerAdapter.CAT_MARKET, getSupportFragmentManager());
            updatePageAdapter();
        } else if (id == R.id.nav_collections) {
            mAdapter = new CollectionPagerAdapter(this, App.isLogin(), getSupportFragmentManager());
            updatePageAdapter();
        } else if (id == R.id.nav_share) {
            share();
        } else if (id == R.id.nav_feedback) {
            feedback();
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
        } else if (id == R.id.nav_app) {
            Intent intent = new Intent(this, AdsActivity.class);
            startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());

        }
    }

    private void feedback() {
        final String appPackageName = BuildConfig.APPLICATION_ID; // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    private void share() {
        ShareCompat.IntentBuilder.from(this)
                .setText(getString(R.string.share_text))
                .setType("text/plain")
                .setSubject(getString(R.string.share_subject))
//                .setStream(Uri.parse("android.resource://drawable/image_name"))
                .startChooser();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(UI.ARG_MENU_INDEX, mPreDrawerId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            int id = savedInstanceState.getInt(UI.ARG_MENU_INDEX);
            if (id != mPreDrawerId) {
                mPreDrawerId = id;
                handleNavItem(id);
            }
        }
    }

    private void setLoginStatus(Boolean login) {
        App.setLoginStatus(login);
        boolean lg = login != null && login;
        if (mAdapter instanceof HomePagerAdapter) {
            mAdapter.notifyDataSetChanged();
            mTabLayout.setupWithViewPager(mViewPager);
        }
        if (!lg) {
            showLoginTips();
        }
        mPref.setLogin(lg);
    }

    private void showLoginTips() {
        Snackbar sb = Snackbar.make(mViewPager, R.string.login_tips, Snackbar.LENGTH_INDEFINITE);
        sb.setAction(R.string.login, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Launcher.openLogin(MainActivity.this, LOGIN_CODE);
            }
        });
        sb.show();
    }

    @Override
    protected PagerAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
