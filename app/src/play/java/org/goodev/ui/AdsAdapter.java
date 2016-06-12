package org.goodev.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adxmi.customizedad.ContentAdModel;

import org.goodev.material.databinding.NativeAdItemBinding;
import org.goodev.widget.BaseAdapter;

/**
 * Created by yfcheng on 2015/12/25.
 */
public class AdsAdapter extends BaseAdapter<ContentAdModel> {
    public AdsAdapter(Activity context) {
        super(context);
    }

    @Override
    protected void onBindContentViewHolder(RecyclerView.ViewHolder holder, int position) {
        AdViewHolder h = (AdViewHolder) holder;
        ContentAdModel p = getItem(position);
        h.binding.setData(p);
    }

    @Override
    public long getContentItemId(int position) {
        return mDataList.get(position).getId().hashCode();
    }

    @Override
    protected RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        NativeAdItemBinding binding = NativeAdItemBinding.inflate(inflater, parent, false);
        return new AdViewHolder(binding, mContext);
    }

    public static class AdViewHolder extends RecyclerView.ViewHolder {
        public final NativeAdItemBinding binding;
        private ProgressDialog dialog;

        public AdViewHolder(NativeAdItemBinding binding, final Activity context) {
            super(binding.getRoot());
            this.binding = binding;
            binding.shotGif.setVisibility(View.INVISIBLE);

            View.OnClickListener launchUser = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Ads.onClickAd(context, binding.getData().getId());
                }
            };

            binding.getRoot().setOnClickListener(launchUser);
        }
    }
}
