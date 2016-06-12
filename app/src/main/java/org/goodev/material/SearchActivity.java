/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.goodev.material;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SearchView;

import org.goodev.material.api.SearchDataManager;
import org.goodev.material.model.Hit;
import org.goodev.material.ui.FeedAdapter;
import org.goodev.material.util.ImeUtils;
import org.goodev.material.util.UI;
import org.goodev.material.util.ViewUtils;
import org.goodev.material.widget.BaselineGridTextView;
import org.goodev.material.widget.InfiniteScrollListener;

import java.util.List;

import butterknife.Bind;
import butterknife.BindDimen;
import butterknife.BindInt;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends AppCompatActivity {

    public static final String EXTRA_MENU_LEFT = "EXTRA_MENU_LEFT";
    public static final String EXTRA_MENU_CENTER_X = "EXTRA_MENU_CENTER_X";
    public static final String EXTRA_QUERY = "EXTRA_QUERY";
    public static final String EXTRA_SAVE_DRIBBBLE = "EXTRA_SAVE_DRIBBBLE";
    public static final String EXTRA_SAVE_DESIGNER_NEWS = "EXTRA_SAVE_DESIGNER_NEWS";
    public static final int RESULT_CODE_SAVE = 7;

    @Bind(R.id.searchback)
    ImageButton searchBack;
    @Bind(R.id.searchback_container)
    ViewGroup searchBackContainer;
    @Bind(R.id.search_view)
    SearchView searchView;
    @Bind(R.id.search_background)
    View searchBackground;
    @Bind(android.R.id.empty)
    ProgressBar progress;
    @Bind(R.id.search_results)
    RecyclerView results;
    @Bind(R.id.container)
    ViewGroup container;
    @Bind(R.id.search_toolbar)
    ViewGroup searchToolbar;
    @Bind(R.id.results_container)
    ViewGroup resultsContainer;
    @Bind(R.id.scrim)
    View scrim;
    @Bind(R.id.results_scrim)
    View resultsScrim;
    private BaselineGridTextView noResults;
    @BindInt(R.integer.shot_column)
    int columns;
    @BindDimen(R.dimen.z_app_bar)
    float appBarElevation;
    private Transition auto;

    private int searchBackDistanceX;
    private int searchIconCenterX;
    private SearchDataManager dataManager;
    private FeedAdapter adapter;

    public static Intent createStartIntent(Context context, int menuIconLeft, int menuIconCenterX) {
        Intent starter = new Intent(context, SearchActivity.class);
        starter.putExtra(EXTRA_MENU_LEFT, menuIconLeft);
        starter.putExtra(EXTRA_MENU_CENTER_X, menuIconCenterX);
        return starter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setupSearchView();
        if (UI.isLollipop()) {
            auto = TransitionInflater.from(this).inflateTransition(R.transition.auto);
        }

        dataManager = new SearchDataManager(this) {
            @Override
            public void onDataLoaded(List<Hit> data) {
                if (data != null && data.size() > 0) {
                    if (results.getVisibility() != View.VISIBLE) {
                        if (UI.isLollipop()) {
                            TransitionManager.beginDelayedTransition(container, auto);
                        }
                        progress.setVisibility(View.GONE);
                        results.setVisibility(View.VISIBLE);
                    }
                    adapter.addAll(data);
                    if (dataManager.getPage() == 0) {
                        results.scrollToPosition(0);
                    }
                } else if (adapter.getItemCount() == 0) {
                    if (UI.isLollipop()) {
                        TransitionManager.beginDelayedTransition(container, auto);
                    }
                    progress.setVisibility(View.GONE);
                    setNoResultsVisibility(View.VISIBLE);
                } else {
                    adapter.notifyDataSetChanged();
                    Snackbar.make(results, R.string.no_more_posts, Snackbar.LENGTH_LONG).show();
                }
            }
        };
        adapter = new FeedAdapter(this, dataManager, columns);
        results.setAdapter(adapter);
        GridLayoutManager layoutManager = new GridLayoutManager(this, columns);
//        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                return adapter.getItemColumnSpan(position);
//            }
//        });
        results.setLayoutManager(layoutManager);
        results.addOnScrollListener(new InfiniteScrollListener(layoutManager, dataManager) {
            @Override
            public void onLoadMore() {
                dataManager.loadMore();
            }
        });
        results.setHasFixedSize(true);
        if (UI.isLollipop()) {
            results.addOnScrollListener(gridScroll);
        }

        // extract the search icon's location passed from the launching activity, minus 4dp to
        // compensate for different paddings in the views
        searchBackDistanceX = getIntent().getIntExtra(EXTRA_MENU_LEFT, 0) - (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        searchIconCenterX = getIntent().getIntExtra(EXTRA_MENU_CENTER_X, 0);

        int interpolator = UI.isLollipop() ? android.R.interpolator.fast_out_slow_in : android.R.interpolator.accelerate_decelerate;
        int backInterpolator = UI.isLollipop() ? android.R.interpolator.linear_out_slow_in : android.R.interpolator.accelerate_decelerate;
        // translate icon to match the launching screen then animate back into position
        searchBackContainer.setTranslationX(searchBackDistanceX);
        searchBackContainer.animate()
                .translationX(0f)
                .setDuration(650L)
                .setInterpolator(AnimationUtils.loadInterpolator(this,
                        interpolator));
        if (UI.isLollipop()) {
            // transform from search icon to back icon
            AnimatedVectorDrawable searchToBack = (AnimatedVectorDrawable) ContextCompat
                    .getDrawable(this, R.drawable.avd_search_to_back);
            searchBack.setImageDrawable(searchToBack);
            searchToBack.start();
        } else {
            searchBack.setVisibility(View.INVISIBLE);
            searchBack.setImageResource(R.drawable.ic_arrow_back_padded);
        }
        // for some reason the animation doesn't always finish (leaving a part arrow!?) so after
        // the animation set a static drawable. Also animation callbacks weren't added until API23
        // so using post delayed :(
        // TODO fix properly!!
        searchBack.postDelayed(new Runnable() {
            @Override
            public void run() {
                searchBack.setImageDrawable(ContextCompat.getDrawable(SearchActivity.this,
                        R.drawable.ic_arrow_back_padded));
            }
        }, 600);

        // fade in the other search chrome
        searchBackground.animate()
                .alpha(1f)
                .setDuration(300L)
                .setInterpolator(AnimationUtils.loadInterpolator(this,
                        backInterpolator));
        searchView.animate()
                .alpha(1f)
                .setStartDelay(400L)
                .setDuration(400L)
                .setInterpolator(AnimationUtils.loadInterpolator(this,
                        backInterpolator))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        searchView.requestFocus();
                        ImeUtils.showIme(searchView);
                    }
                });

        if (UI.isLollipop()) {
            // animate in a scrim over the content behind
            scrim.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public boolean onPreDraw() {
                    scrim.getViewTreeObserver().removeOnPreDrawListener(this);
                    AnimatorSet showScrim = new AnimatorSet();
                    showScrim.playTogether(
                            ViewAnimationUtils.createCircularReveal(
                                    scrim,
                                    searchIconCenterX,
                                    searchBackground.getBottom(),
                                    0,
                                    (float) Math.hypot(searchBackDistanceX, scrim.getHeight()
                                            - searchBackground.getBottom())),
                            ObjectAnimator.ofArgb(
                                    scrim,
                                    ViewUtils.BACKGROUND_COLOR,
                                    Color.TRANSPARENT,
                                    ContextCompat.getColor(SearchActivity.this, R.color.scrim)));
                    showScrim.setDuration(400L);
                    showScrim.setInterpolator(AnimationUtils.loadInterpolator(SearchActivity.this,
                            backInterpolator));
                    showScrim.start();
                    return false;
                }
            });
        }

        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.hasExtra(SearchManager.QUERY)) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (!TextUtils.isEmpty(query)) {
                searchView.setQuery(query, false);
                searchFor(query);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (UI.isLollipop()) {
            dismiss();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        // needed to suppress the default window animation when closing the activity
        overridePendingTransition(0, 0);
        super.onPause();
    }

    @OnClick({R.id.scrim, R.id.searchback})
    protected void dismiss() {
        int interpolator = UI.isLollipop() ? android.R.interpolator.fast_out_slow_in : android.R.interpolator.accelerate_decelerate;
        int backInterpolator = UI.isLollipop() ? android.R.interpolator.fast_out_linear_in : android.R.interpolator.accelerate_decelerate;
        // translate the icon to match position in the launching activity
        searchBackContainer.animate()
                .translationX(searchBackDistanceX)
                .setDuration(600L)
                .setInterpolator(AnimationUtils.loadInterpolator(this,
                        interpolator))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        supportFinishAfterTransition();
                    }
                })
                .start();
        if (UI.isLollipop()) {

            // transform from back icon to search icon
            AnimatedVectorDrawable backToSearch = (AnimatedVectorDrawable) ContextCompat
                    .getDrawable(this, R.drawable.avd_back_to_search);
            searchBack.setImageDrawable(backToSearch);
            // clear the background else the touch ripple moves with the translation which looks bad
            searchBack.setBackground(null);
            backToSearch.start();
        }
        // fade out the other search chrome
        searchView.animate()
                .alpha(0f)
                .setStartDelay(0L)
                .setDuration(120L)
                .setInterpolator(AnimationUtils.loadInterpolator(this,
                        backInterpolator))
                .setListener(null)
                .start();
        searchBackground.animate()
                .alpha(0f)
                .setStartDelay(300L)
                .setDuration(160L)
                .setInterpolator(AnimationUtils.loadInterpolator(this,
                        backInterpolator))
                .setListener(null)
                .start();
        if (UI.isLollipop()) {
            if (searchToolbar.getZ() != 0f) {
                searchToolbar.animate()
                        .z(0f)
                        .setDuration(600L)
                        .setInterpolator(AnimationUtils.loadInterpolator(this,
                                backInterpolator))
                        .start();
            }

        }

        // if we're showing search results, circular hide them
        if (UI.isLollipop() && resultsContainer.getHeight() > 0) {
            Animator closeResults = ViewAnimationUtils.createCircularReveal(
                    resultsContainer,
                    searchIconCenterX,
                    0,
                    (float) Math.hypot(searchIconCenterX, resultsContainer.getHeight()),
                    0f);
            closeResults.setDuration(500L);
            closeResults.setInterpolator(AnimationUtils.loadInterpolator(SearchActivity.this,
                    android.R.interpolator.fast_out_slow_in));
            closeResults.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    resultsContainer.setVisibility(View.INVISIBLE);
                }
            });
            closeResults.start();
        }

        // fade out the scrim
        scrim.animate()
                .alpha(0f)
                .setDuration(400L)
                .setInterpolator(AnimationUtils.loadInterpolator(this,
                        backInterpolator))
                .setListener(null)
                .start();
    }

    @OnClick(R.id.results_scrim)
    protected void hideSaveConfimation() {

    }

    private void setupSearchView() {
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        // hint, inputType & ime options seem to be ignored from XML! Set in code
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        searchView.setImeOptions(searchView.getImeOptions() | EditorInfo.IME_ACTION_SEARCH |
                EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_FLAG_NO_FULLSCREEN);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchFor(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (TextUtils.isEmpty(query)) {
                    clearResults();
                }
                return true;
            }
        });

    }

    private void clearResults() {
        adapter.clear();
        dataManager.clear();
        if (UI.isLollipop()) {
            TransitionManager.beginDelayedTransition(container, auto);
        }
        results.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        resultsScrim.setVisibility(View.GONE);
        setNoResultsVisibility(View.GONE);
    }

    private void setNoResultsVisibility(int visibility) {
        if (visibility == View.VISIBLE) {
            if (noResults == null) {
                noResults = (BaselineGridTextView) ((ViewStub)
                        findViewById(R.id.stub_no_search_results)).inflate();
                noResults.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        searchView.setQuery("", false);
                        searchView.requestFocus();
                        ImeUtils.showIme(searchView);
                    }
                });
            }
            String message = String.format(getString(R
                    .string.no_search_results), searchView.getQuery().toString());
            SpannableStringBuilder ssb = new SpannableStringBuilder(message);
            ssb.setSpan(new StyleSpan(Typeface.ITALIC),
                    message.indexOf('“') + 1,
                    message.length() - 1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            noResults.setText(ssb);
        }
        if (noResults != null) {
            noResults.setVisibility(visibility);
        }
    }

    private void searchFor(String query) {
        clearResults();
        progress.setVisibility(View.VISIBLE);
        ImeUtils.hideIme(searchView);
        searchView.clearFocus();
        dataManager.searchFor(query);
    }

    private int gridScrollY = 0;
    private RecyclerView.OnScrollListener gridScroll = new RecyclerView.OnScrollListener() {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            gridScrollY += dy;
            if (gridScrollY > 0 && searchToolbar.getTranslationZ() != appBarElevation) {
                searchToolbar.animate()
                        .translationZ(appBarElevation)
                        .setDuration(300L)
                        .setInterpolator(AnimationUtils.loadInterpolator(SearchActivity.this,
                                android.R.interpolator.fast_out_slow_in))
                        .start();
            } else if (gridScrollY == 0 && searchToolbar.getTranslationZ() != 0) {
                searchToolbar.animate()
                        .translationZ(0f)
                        .setDuration(300L)
                        .setInterpolator(AnimationUtils.loadInterpolator(SearchActivity.this,
                                android.R.interpolator.fast_out_slow_in))
                        .start();
            }
        }
    };
}
