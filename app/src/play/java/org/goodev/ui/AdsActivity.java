package org.goodev.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import org.goodev.material.R;

import butterknife.Bind;
import butterknife.ButterKnife;


public class AdsActivity extends AppCompatActivity {

    @Bind(R.id.container)
    FrameLayout mContainer;

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.progress)
    ProgressBar mProgressBar;

    AdsAdapter mAdsAdapter;
    private RecyclerView.AdapterDataObserver mObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            if (mAdsAdapter.getItemCount() > 0) {
                mProgressBar.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView.LayoutManager layout = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layout);
        mRecyclerView.setHasFixedSize(false);
        mAdsAdapter = new AdsAdapter(this);
        mRecyclerView.setAdapter(mAdsAdapter);
        mAdsAdapter.registerAdapterDataObserver(mObserver);
        Ads.setSupportAds(this, mAdsAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Ads.onBackKey(this);
    }

    @Override
    protected void onDestroy() {
        mAdsAdapter.unregisterAdapterDataObserver(mObserver);
        super.onDestroy();
    }

}
