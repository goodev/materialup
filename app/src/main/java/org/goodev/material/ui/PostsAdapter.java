package org.goodev.material.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.goodev.material.databinding.NativeAdItemBinding;
import org.goodev.material.databinding.PostItemBinding;
import org.goodev.material.databinding.PostItemNoUserBinding;
import org.goodev.material.model.Post;
import org.goodev.material.util.Launcher;
import org.goodev.material.util.UI;
import org.goodev.ui.Ads;
import org.goodev.ui.AdsAdapter;
import org.goodev.widget.BaseAdapter;

/**
 * Created by yfcheng on 2015/11/27.
 */
public class PostsAdapter extends BaseAdapter<Post> {
    /**
     * if is the same user, do not launch UserActivity again.
     */
    private String mUserId;

    public PostsAdapter(Activity context) {
        this(context, null);
    }

    public PostsAdapter(Activity context, String userId) {
        super(context);
        mUserId = userId;
    }

    @Override
    public int getItemViewType(int position) {
        if (mHasLoading && mDataList.size() == position) {
            return TYPE_LOADING;
        }

        Post data = getItem(position);
        if (Ads.isAd(data.id)) {
            return TYPE_AD;
        }
        if (!data.hasName()) {
            return TYPE_DATA2;
        }
        return TYPE_DATA;
    }

    @Override
    protected void onBindContentViewHolder(RecyclerView.ViewHolder holder, int position) {
        Post p = getItem(position);
        if (holder instanceof PostViewHolder) {
            ((PostViewHolder) holder).binding.setPost(p);
        } else if (holder instanceof PostViewHolder2) {
            ((PostViewHolder2) holder).binding.setPost(p);
        } else if (holder instanceof AdsAdapter.AdViewHolder) {
            NativeAdItemBinding binding = ((AdsAdapter.AdViewHolder) holder).binding;
            binding.setData(Ads.toAd(p));
            binding.shotGif.setVisibility(View.VISIBLE);
        }
//        FrescoUtils.setShotUrl(h.binding.imageView, p.getImageUrl(), p.getTeaserUrl());
    }

    @Override
    public long getContentItemId(int position) {
        return mDataList.get(position).id;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (viewType == TYPE_AD) {
            NativeAdItemBinding binding = NativeAdItemBinding.inflate(inflater, parent, false);
            return new AdsAdapter.AdViewHolder(binding, mContext);
        } else if (viewType == TYPE_DATA2) {
            PostItemNoUserBinding binding = PostItemNoUserBinding.inflate(inflater, parent, false);
            return new PostViewHolder2(binding, mContext);
        }
        PostItemBinding binding = PostItemBinding.inflate(inflater, parent, false);
        return new PostViewHolder(binding, mContext);
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        private final PostItemBinding binding;
        private ProgressDialog dialog;

        public PostViewHolder(PostItemBinding binding, final Activity context) {
            super(binding.getRoot());
            this.binding = binding;
            binding.source.setOnClickListener(v -> {
                if (binding.getPost().id == UI.DROIDDDLE_ID) {
                    Launcher.openPlayStore(context, UI.DROIDDDLE);
                } else {
                    String url = binding.getPost().redirect;
                    Launcher.launchUrl(context, url);
                }

            });
            View.OnClickListener launchPost = v -> {
                if (binding.getPost().id == UI.DROIDDDLE_ID) {
                    Launcher.openPlayStore(context, UI.DROIDDDLE);
                } else {
                    Launcher.openPost(context, binding.getPost(), binding.imageView);
                }
            };
            binding.getRoot().setOnClickListener(launchPost);

            View.OnClickListener launchUser = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Launcher.launchUser(context, binding.getPost().userUrl, binding.getPost().avatarUrl, mUserId, binding.userImage);

                }
            };

            binding.userImage.setOnClickListener(launchUser);
            binding.updater.setOnClickListener(launchUser);
        }
    }

    public class PostViewHolder2 extends RecyclerView.ViewHolder {
        private final PostItemNoUserBinding binding;
        private ProgressDialog dialog;

        public PostViewHolder2(PostItemNoUserBinding binding, final Activity context) {
            super(binding.getRoot());
            this.binding = binding;
            binding.source.setOnClickListener(v -> {
                if (binding.getPost().id == UI.DROIDDDLE_ID) {
                    Launcher.openPlayStore(context, UI.DROIDDDLE);
                } else {
                    String url = binding.getPost().redirect;
                    Launcher.launchUrl(context, url);
                }

            });
            View.OnClickListener launchPost = v -> {
                if (binding.getPost().id == UI.DROIDDDLE_ID) {
                    Launcher.openPlayStore(context, UI.DROIDDDLE);
                } else {
                    Launcher.openPost(context, binding.getPost(), binding.imageView);
                }
            };
            binding.getRoot().setOnClickListener(launchPost);

        }
    }
}
