package org.goodev.material.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import org.goodev.material.R;
import org.goodev.material.api.Api;
import org.goodev.material.api.ErrorCallback;
import org.goodev.material.api.JsoupUtil;
import org.goodev.material.databinding.NotificationItemBinding;
import org.goodev.material.model.Notification;
import org.goodev.material.model.Post;
import org.goodev.material.model.User;
import org.goodev.material.util.L;
import org.goodev.material.util.Launcher;
import org.goodev.material.util.UI;
import org.goodev.utils.Utils;
import org.goodev.widget.BaseAdapter;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yfcheng on 2015/12/22.
 */
public class NotificationAdapter extends BaseAdapter<Notification> {


    public NotificationAdapter(Activity context) {
        super(context);
    }


    @Override
    protected void onBindContentViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder h = (MyViewHolder) holder;
        Notification p = getItem(position);
        h.binding.setData(p);
//        FrescoUtils.setShotUrl(h.binding.imageView, p.getImageUrl(), p.getTeaserUrl());
    }

    @Override
    public long getContentItemId(int position) {
        return position;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        NotificationItemBinding binding = NotificationItemBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding, mContext);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        final NotificationItemBinding binding;
        ProgressDialog dialog;

        public MyViewHolder(NotificationItemBinding binding, final Activity context) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v -> {
                Notification c = binding.getData();
                if (!Utils.hasInternet(context)) {
                    Toast.makeText(context, R.string.check_network, Toast.LENGTH_SHORT).show();
                    return;
                }
                String id = null;
                int start = c.postPath.lastIndexOf("/");
                int end = c.postPath.lastIndexOf("#");
                if (end == -1) {
                    end = c.postPath.length();
                }
                try {
                    id = c.postPath.substring(start + 1, end);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (TextUtils.isEmpty(id)) {
                    UI.showToast(context, R.string.can_not_display_this);
                    return;
                }

                dialog = UI.showProgressDialog(context, R.string.loading);
                if (c.postPath.startsWith("/posts")) {
                    Api.getApiService().getPostSidebar(id)
                            .map(responseBody -> JsoupUtil.getPostContent1(null, responseBody))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(s -> launchPost(context, s), new ErrorCallback(context));
                } else if (c.postPath.startsWith("/users")) {
                    Api.getApiService().getUserInfo(id)
                            .map(responseBody -> JsoupUtil.convertToUser(null, responseBody))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(s -> launchUser(context, s), new ErrorCallback(context));
                } else {
                    UI.showToast(context, R.string.can_not_display_this);
                }
            });

        }

        private void launchPost(Activity context, Post data) {
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }
            Launcher.openPost(context, data);
        }

        private void launchUser(Activity context, User user) {
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }
            Launcher.openUser(context, user, binding.userImage);
        }

    }
}
