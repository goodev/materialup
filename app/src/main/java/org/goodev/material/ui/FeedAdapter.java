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

package org.goodev.material.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.goodev.material.R;
import org.goodev.material.api.DataLoadingSubject;
import org.goodev.material.databinding.HitItemBinding;
import org.goodev.material.model.Hit;
import org.goodev.material.util.AnimUtils;
import org.goodev.material.util.Launcher;
import org.goodev.material.util.ViewUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Adapter for the main screen grid of items
 */
public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final float DUPE_WEIGHT_BOOST = 0.4f;

    private static final int TYPE_DESIGNER_NEWS_STORY = 0;
    private static final int TYPE_LOADING_MORE = -1;

    // we need to hold on to an activity ref for the shared element transitions :/
    private final Activity host;
    private final LayoutInflater layoutInflater;
    private
    @Nullable
    DataLoadingSubject dataLoading;
    private final int columns;
    private final ColorDrawable[] shotLoadingPlaceholders;

    private List<Hit> items;

    public FeedAdapter(Activity hostActivity,
                       DataLoadingSubject dataLoading,
                       int columns) {
        this.host = hostActivity;
        this.dataLoading = dataLoading;
        this.columns = columns;
        layoutInflater = LayoutInflater.from(host);
        items = new ArrayList<>();
        setHasStableIds(true);
        TypedArray placeholderColors = hostActivity.getResources()
                .obtainTypedArray(R.array.loading_placeholders);
        shotLoadingPlaceholders = new ColorDrawable[placeholderColors.length()];
        for (int i = 0; i < placeholderColors.length(); i++) {
            shotLoadingPlaceholders[i] = new ColorDrawable(
                    placeholderColors.getColor(i, Color.DKGRAY));
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_DESIGNER_NEWS_STORY:
                LayoutInflater inflater = LayoutInflater.from(host);
                HitItemBinding binding = HitItemBinding.inflate(inflater, parent, false);
                return new PostViewHolder(binding, host);
            case TYPE_LOADING_MORE:
                return new LoadingMoreHolder(
                        layoutInflater.inflate(R.layout.infinite_loading, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_DESIGNER_NEWS_STORY:
                PostViewHolder h = (PostViewHolder) holder;
                Hit p = getItem(position);
                h.binding.setData(p);
                break;
            case TYPE_LOADING_MORE:
                bindLoadingViewHolder((LoadingMoreHolder) holder);
                break;
        }
    }

    private void bindLoadingViewHolder(LoadingMoreHolder holder) {
        // only show the infinite load progress spinner if there are already items in the
        // grid i.e. it's not the first item & data is being loaded
        holder.progress.setVisibility((holder.getAdapterPosition() > 0
                && dataLoading.isDataLoading()) ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int getItemViewType(int position) {
        if (position < getDataItemCount()
                && getDataItemCount() > 0) {
            return TYPE_DESIGNER_NEWS_STORY;
        }
        return TYPE_LOADING_MORE;
    }

    private Hit getItem(int position) {
        return items.get(position);
    }

    public int getItemColumnSpan(int position) {
        switch (getItemViewType(position)) {
            case TYPE_LOADING_MORE:
                return columns;
            default:
                return 1;
        }
    }

    private void add(Hit item) {
        items.add(item);
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void addAll(Collection<Hit> newItems) {
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    public void addAndResort(Collection<Hit> newItems) {
        // de-dupe results as the same item can be returned by multiple feeds
        boolean add = true;
        for (Hit newItem : newItems) {
            int count = getDataItemCount();
            for (int i = 0; i < count; i++) {
                Hit existingItem = getItem(i);
                if (existingItem.equals(newItem)) {
                    // if we find a dupe mark the weight boost field on the first-in, but don't add
                    // the dupe. We use the fact that an item comes from multiple sources to indicate it
                    // is more important and sort it higher
//                    existingItem.weightBoost = DUPE_WEIGHT_BOOST;
                    add = false;
                    break;
                }
            }
            if (add) {
                add(newItem);
                add = true;
            }
        }
    }


    @Override
    public long getItemId(int position) {
        if (getItemViewType(position) == TYPE_LOADING_MORE) {
            return -1L;
        }
        return getItem(position).id;
    }

    @Override
    public int getItemCount() {
        // include loading footer
        return getDataItemCount() + 1;
    }

    /**
     * The shared element transition to dribbble shots & dn stories can intersect with the FAB.
     * This can cause a strange layers-passing-through-each-other effect, especially on return.
     * In this situation, hide the FAB on exit and re-show it on return.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setGridItemContentTransitions(View gridItem) {
        if (!ViewUtils.viewsIntersect(gridItem, host.findViewById(R.id.fab))) return;

        final TransitionInflater ti = TransitionInflater.from(host);
        host.getWindow().setExitTransition(
                ti.inflateTransition(R.transition.home_content_item_exit));
        final Transition reenter = ti.inflateTransition(R.transition.home_content_item_reenter);
        // we only want this content transition in certain cases so clear it out after it's done.
        reenter.addListener(new AnimUtils.TransitionListenerAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                host.getWindow().setExitTransition(null);
                host.getWindow().setReenterTransition(null);
            }
        });
        host.getWindow().setReenterTransition(reenter);
    }

    public int getDataItemCount() {
        return items.size();
    }


    /* protected */ class LoadingMoreHolder extends RecyclerView.ViewHolder {

        ProgressBar progress;

        public LoadingMoreHolder(View itemView) {
            super(itemView);
            progress = (ProgressBar) itemView;
        }

    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        private final HitItemBinding binding;
        private ProgressDialog dialog;

        public PostViewHolder(HitItemBinding binding, final Activity context) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v -> {
                Launcher.openPost(context, binding.getData().toPost(), binding.imageView, true);
            });

        }
    }

}
