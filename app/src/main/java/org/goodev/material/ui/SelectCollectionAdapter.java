package org.goodev.material.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.goodev.material.databinding.SelectCollectionItemBinding;
import org.goodev.material.model.Collection;
import org.goodev.material.util.L;
import org.goodev.material.util.Launcher;
import org.goodev.widget.BaseAdapter;

import java.util.Arrays;

/**
 * Created by yfcheng on 2015/12/9.
 */
public class SelectCollectionAdapter extends BaseAdapter<Collection> {

    long mPostId;

    public SelectCollectionAdapter(Activity context, long pid) {
        super(context);
        mPostId = pid;
    }


    @Override
    protected void onBindContentViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder h = (MyViewHolder) holder;
        Collection p = getItem(position);
        h.binding.setData(p);
        boolean selected = isInCollection(p, mPostId);
        h.binding.name.setChecked(selected);
//        FrescoUtils.setShotUrl(h.binding.imageView, p.getImageUrl(), p.getTeaserUrl());
    }

    private boolean isInCollection(Collection p, long pid) {
        if (p.getPostIds() == null) {
            return false;
        }
        long[] pids = p.getPostIds();
        for (int i = 0; i < pids.length; i++) {
            if (pid == pids[i]) {
                return true;
            }
        }
        return false;
    }

    @Override
    public long getContentItemId(int position) {
        return mDataList.get(position).getPath().hashCode();
    }

    @Override
    protected RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        SelectCollectionItemBinding binding = SelectCollectionItemBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding, mContext);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        final SelectCollectionItemBinding binding;

        public MyViewHolder(SelectCollectionItemBinding binding, final Activity context) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v -> {
                Collection c = binding.getData();
                Intent data = new Intent();
                data.putExtra(Launcher.EXTRA_ID, c.getId());
                data.putExtra(Launcher.EXTRA_CHECKED, binding.name.isChecked());
                context.setResult(Activity.RESULT_OK, data);
                context.finish();
            });

        }
    }
}
