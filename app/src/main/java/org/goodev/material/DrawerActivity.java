package org.goodev.material;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import butterknife.Bind;

/**
 * Created by yfcheng on 2015/11/30.
 */
public class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String DRAWER_ID = "drawer_id";
    @Nullable
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;
    @Bind(R.id.nav_view)
    NavigationView navView;

    int mPreDrawerId = R.id.nav_home;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mPreDrawerId = savedInstanceState.getInt(DRAWER_ID, mPreDrawerId);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(DRAWER_ID, mPreDrawerId);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (mPreDrawerId == id) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        mPreDrawerId = id;

        if (id == R.id.nav_inspiration) {
            // Handle the camera action
        } else if (id == R.id.nav_resources) {

        } else if (id == R.id.nav_freebies) {

        } else if (id == R.id.nav_market) {

        } else if (id == R.id.nav_collections) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_about) {

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getBaseLayout());
    }

    @LayoutRes
    public int getBaseLayout() {
        return R.layout.activity_drawer_with_tabs;
    }

    public void setBaseContentView(int layoutResID) {
        LayoutInflater.from(this).inflate(layoutResID, (ViewGroup) findViewById(R.id.rootLayout));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
