package org.goodev.widget;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.GridLayoutAnimationController;
import android.widget.ProgressBar;

import org.goodev.utils.Utils;

/**
 * Created by ADMIN on 2015/1/1.
 */
public class EndlessRecyclerView extends RecyclerView {
    public interface OnLoadingMoreListener {
        void onLoadingMore();

        void isFirstItemFullVisible(boolean firstVisible);
    }

    final AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            checkIfEmpty();
        }
    };
    //EmptyView like listView ---------
    View mEmptyView;
    ProgressBar mLoadingView;
    private boolean mIsLoadingMore;
    private boolean mHasMoreData = false;
    private OnLoadingMoreListener mMoreListener;

    public EndlessRecyclerView(Context context) {
        super(context);
    }

    public EndlessRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EndlessRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    void checkIfEmpty() {
        if (mEmptyView != null) {
            boolean empty = getAdapter() == null || getAdapter().getItemCount() == 0;
            mEmptyView.setVisibility(!empty ? GONE : VISIBLE);
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }
    }

    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
        checkIfEmpty();
    }

    public void setLoadingView(ProgressBar loadingView) {
        mLoadingView = loadingView;
    }

    private void hideLoading() {
        ViewCompat.setAlpha(this, 0f);
        ViewCompat.setTranslationY(this, getMeasuredHeight() / 2);
        ViewCompat.animate(mLoadingView).alpha(0f).translationYBy(-mLoadingView.getHeight()).setDuration(400)
                .scaleX(0.6f)
                .scaleY(0.6f)
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(View view) {
                        super.onAnimationEnd(view);
                        view.setVisibility(GONE);
                        ViewCompat.setAlpha(view, 1f);
                        ViewCompat.setTranslationY(view, 0);
                        ViewCompat.setScaleX(view, 1f);
                        ViewCompat.setScaleY(view, 1f);
                    }
                })
                .start();
        ViewCompat.animate(this).alpha(1f).translationY(0).setDuration(500)
                .setStartDelay(200).setListener(new ViewPropertyAnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(View view) {
                super.onAnimationEnd(view);
                ViewCompat.setAlpha(view, 1f);
                ViewCompat.setTranslationY(view, 0);
            }
        }).start();
    }

    public void setLoading(boolean loading, boolean first) {
        if (mLoadingView != null) {
            if (loading) {
                if (mEmptyView != null)
                    mEmptyView.setVisibility(GONE);
                mLoadingView.setVisibility(VISIBLE);
            } else if (mLoadingView.getVisibility() == VISIBLE) {
                if (first) {
                    hideLoading();
                } else {
                    mLoadingView.setVisibility(GONE);
                }
            }
        }
    }

    @Override
    protected void attachLayoutAnimationParameters(View child, ViewGroup.LayoutParams params, int index, int count) {

        if (getAdapter() != null && false) {
            LayoutManager manager = getLayoutManager();

            GridLayoutAnimationController.AnimationParameters animationParams =
                    (GridLayoutAnimationController.AnimationParameters) params.layoutAnimationParameters;

            if (animationParams == null) {
                animationParams = new GridLayoutAnimationController.AnimationParameters();
                params.layoutAnimationParameters = animationParams;
            }

            int columns = 1;
            if (manager instanceof GridLayoutManager) {
                columns = ((GridLayoutManager) manager).getSpanCount();
            }

            animationParams.count = count;
            animationParams.index = index;
            animationParams.columnsCount = columns;
            animationParams.rowsCount = count / columns;

            final int invertedIndex = count - 1 - index;
            animationParams.column = columns - 1 - (invertedIndex % columns);
            animationParams.row = animationParams.rowsCount - 1 - invertedIndex / columns;

        } else {
            super.attachLayoutAnimationParameters(child, params, index, count);
        }
    }

    /**
     * if layout manager do not have this method , will return 0
     *
     * @return
     */
    public int findFirstVisibleItemPosition() {
        LayoutManager manager = getLayoutManager();

        int firstVisibleItems = 0;
        if (manager instanceof LinearLayoutManager) {
            firstVisibleItems = ((LinearLayoutManager) manager).findFirstVisibleItemPosition();
        } else if (manager instanceof GridLayoutManager) {
            firstVisibleItems = ((GridLayoutManager) manager).findFirstVisibleItemPosition();
        } else if (manager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager sg = ((StaggeredGridLayoutManager) manager);
            int[] items = new int[sg.getSpanCount()];
            items = ((StaggeredGridLayoutManager) manager).findFirstVisibleItemPositions(items);
            firstVisibleItems = items[0];
        }

        return firstVisibleItems;
    }

    public void checkLoadingMore() {
        boolean firstVisible = false;

        LayoutManager manager = getLayoutManager();

        int visibleItemCount = manager.getChildCount();
        int totalItemCount = manager.getItemCount();
        int firstVisibleItems = 0;
        if (manager instanceof LinearLayoutManager) {
            firstVisibleItems = ((LinearLayoutManager) manager).findFirstVisibleItemPosition();
        } else if (manager instanceof GridLayoutManager) {
            firstVisibleItems = ((GridLayoutManager) manager).findFirstVisibleItemPosition();
        } else if (manager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager sg = ((StaggeredGridLayoutManager) manager);
            int[] items = new int[sg.getSpanCount()];
            items = ((StaggeredGridLayoutManager) manager).findFirstVisibleItemPositions(items);
            firstVisibleItems = items[0];
        }

        //        boolean topOfFirstItemVisible = manager.getChildAt(0).getTop() >= mFirstTop;
        // enabling or disabling the refresh layout
        if (firstVisibleItems == 0) {
            View child = manager.getChildAt(0);
            if (child != null) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                firstVisible = manager.getChildAt(0).getTop() >= (getPaddingTop() + lp.topMargin);
            }
        }
        //        firstVisible = firstVisibleItems == 0 && topOfFirstItemVisible;

        mMoreListener.isFirstItemFullVisible(firstVisible);
        if (!mIsLoadingMore && mHasMoreData && totalItemCount > 10) {
            if ((visibleItemCount + firstVisibleItems) >= totalItemCount - 1) {
                if(!Utils.hasInternet(getContext())){
                    return;
                }
                mIsLoadingMore = true;
                mMoreListener.onLoadingMore();
            }
        }
    }

    public void finishLoadingMore(boolean hasMoreData) {
        mIsLoadingMore = false;
        mHasMoreData = hasMoreData;
    }

    public OnLoadingMoreListener getOnLoadingMoreListener() {
        return mMoreListener;
    }

    public void setOnLoadingMoreListener(OnLoadingMoreListener listener) {
        mMoreListener = listener;
        addOnScrollListener(new RecyclerScrollListener());
    }

    private class RecyclerScrollListener extends OnScrollListener {
        private int mScrolledY;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            EndlessRecyclerView rv = (EndlessRecyclerView) recyclerView;
            if (mMoreListener != null) {
                checkLoadingMore();

            }
        }
    }
}
