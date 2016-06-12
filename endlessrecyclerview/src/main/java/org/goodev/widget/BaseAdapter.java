package org.goodev.widget;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by goodev on 2014/12/26.
 */
public abstract class BaseAdapter<E> extends RecyclerView.Adapter {

    public static final int TYPE_DATA = 0;
    public static final int TYPE_DATA2 = 3;
    public static final int TYPE_LOADING = 1;
    public static final int TYPE_AD = 2;

    protected Activity mContext;
    protected List<E> mDataList;
    protected boolean mHasLoading;
    // Allows to remember the last item shown on screen
    protected int lastPosition = -1;
    Animation animation;

    public BaseAdapter(Activity context) {
        mContext = context;
        mDataList = new ArrayList<E>();
        animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_bottom);
    }

    public E getItem(int position) {
        return mDataList.get(position);
    }

    public List<E> getAllItems() {
        return mDataList;
    }

    public void setLoading(boolean loading) {
        if (mHasLoading == loading) {
            return;
        }
        mHasLoading = loading;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder h = (LoadingViewHolder) holder;
            return;
        }
        onBindContentViewHolder(holder, position);
        // Here you apply the animation when the view is bound
        setAnimation(holder.itemView, position);
    }

    private int mDuration = 500;
    private Interpolator mInterpolator = new LinearInterpolator();
    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(viewToAnimate, "translationY", viewToAnimate.getMeasuredHeight(), 0);
            anim.setInterpolator(mInterpolator);
            anim.setDuration(mDuration).start();
            lastPosition = position;
        }else{
            ViewCompat.setTranslationY(viewToAnimate, 0);
        }
    }

    protected abstract void onBindContentViewHolder(RecyclerView.ViewHolder holder, int position);

    @Override
    public int getItemViewType(int position) {
        if (mHasLoading && mDataList.size() == position) {
            return TYPE_LOADING;
        }

        return TYPE_DATA;
    }

    public void addData(List<E> data) {
        for (E t : data) {
            if (mDataList.contains(t)) {
                mDataList.remove(t);
            } else {
                break;
            }
        }
        mDataList.addAll(data);
        notifyDataSetChanged();
    }

    public void addData(E data) {
        mDataList.add(data);
        notifyDataSetChanged();
    }

    public void removeData(E data) {
        mDataList.remove(data);
        notifyDataSetChanged();
    }

    public void removeData(int index) {
        if (index >= 0 && index < mDataList.size()) {
            mDataList.remove(index);
            notifyDataSetChanged();
        }
    }

    public void setData(List<E> shots) {
        mDataList.clear();
        mDataList.addAll(shots);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        if (mHasLoading && mDataList.size() == position) {
            return -1;
        }
        return getContentItemId(position);
    }

    public abstract long getContentItemId(int position);

    @Override
    public int getItemCount() {
        if (mHasLoading) {
            return mDataList.size() + 1;
        }
        return mDataList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_LOADING) {
            final View view = LayoutInflater.from(mContext).inflate(R.layout.loading_view, parent, false);
            return new LoadingViewHolder(view);
        } else {
            RecyclerView.ViewHolder vh = onCreateContentViewHolder(parent, viewType);
            vh.itemView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    ViewCompat.setTranslationY(v, 0);
                }
            });
            return vh;
        }
    }

    protected abstract RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType);

    public void remove(int position) {
        if (position >= 0 && position < mDataList.size()) {
            mDataList.remove(position);
            //            notifyItemRemoved(position);
            notifyDataSetChanged();
        }
    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar mProgressView;

        public LoadingViewHolder(View view) {
            super(view);
            mProgressView = (ProgressBar) view.findViewById(R.id.progress);
        }
    }

}
