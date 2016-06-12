package org.goodev.material.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.goodev.material.databinding.ResourceItemBinding;
import org.goodev.material.model.Post;
import org.goodev.material.util.Launcher;
import org.goodev.widget.BaseAdapter;

/**
 * Created by yfcheng on 2015/12/7.
 */
public class ResourcesAdapter extends BaseAdapter<Post> {
    /**
     * if is the same user, do not launch UserActivity again.
     */
    private String mUserId;

    public ResourcesAdapter(Activity context) {
        this(context, null);
    }

    public ResourcesAdapter(Activity context, String userId) {
        super(context);
        mUserId = userId;
    }

    @Override
    protected void onBindContentViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder h = (MyViewHolder) holder;
        Post p = getItem(position);
        h.binding.setPost(p);
    }

    @Override
    public long getContentItemId(int position) {
        return mDataList.get(position).id;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ResourceItemBinding binding = ResourceItemBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding, mContext);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        final ResourceItemBinding binding;
        ProgressDialog dialog;

        public MyViewHolder(ResourceItemBinding binding, final Activity context) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v -> {
                Launcher.openPost(context, binding.getPost(), binding.imageView);
            });

            binding.source.setOnClickListener(v -> {
                String url = binding.getPost().redirect;
                Launcher.launchUrl(context, url);
            });

        }
    }
}
