package org.goodev.material.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.goodev.material.R;
import org.goodev.material.api.Api;
import org.goodev.material.api.ErrorCallback;
import org.goodev.material.api.JsoupUtil;
import org.goodev.material.databinding.UserItemBinding;
import org.goodev.material.model.User;
import org.goodev.material.util.Launcher;
import org.goodev.material.util.UI;
import org.goodev.utils.Utils;
import org.goodev.widget.BaseAdapter;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yfcheng on 2015/12/5.
 */
public class UsersAdapter extends BaseAdapter<User> {
    /**
     * if is the same user, do not launch UserActivity again.
     */
    private String mUserId;

    public UsersAdapter(Activity context) {
        this(context, null);
    }

    public UsersAdapter(Activity context, String userId) {
        super(context);
        mUserId = userId;
    }

    @Override
    protected void onBindContentViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder h = (MyViewHolder) holder;
        User data = getItem(position);
        h.binding.setData(data);
//        FrescoUtils.setShotUrl(h.binding.imageView, p.getImageUrl(), p.getTeaserUrl());
    }

    @Override
    public long getContentItemId(int position) {
        return mDataList.get(position).getPath().hashCode();
    }

    @Override
    protected RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        UserItemBinding binding = UserItemBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding, mContext);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        final UserItemBinding binding;
        ProgressDialog dialog;

        public MyViewHolder(UserItemBinding binding, final Activity context) {
            super(binding.getRoot());
            this.binding = binding;

            View.OnClickListener launchUser = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Utils.hasInternet(context)) {
                        Toast.makeText(context, R.string.check_network, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String id = User.getUserId(binding.getData().getPath());

                    if (TextUtils.isEmpty(id)) {
                        UI.showToast(context, R.string.can_not_display_this_user);
                        return;
                    }
                    if (id.equals(mUserId)) {
                        return;
                    }
                    dialog = UI.showProgressDialog(context, R.string.loading);
                    Api.getApiService().getUserInfo(id)
                            .map(responseBody -> JsoupUtil.convertToUser(null, responseBody))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(s -> launchUser(context, s), new ErrorCallback(context));
                }

                private void launchUser(Activity context, User user) {
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    Launcher.openUser(context, user, binding.userImage);
                }
            };

            binding.getRoot().setOnClickListener(launchUser);
        }
    }
}
