package org.goodev.material.ui;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.facebook.drawee.view.SimpleDraweeView;

import org.goodev.material.R;
import org.goodev.material.model.User;

import java.util.List;

/**
 * Created by yfcheng on 2015/12/9.
 */
public class UpvotersAdapter extends BaseAdapter {

    private final Activity context;
    private final List<User> users;
    private final LayoutInflater mInflater;

    public UpvotersAdapter(Activity context, List<User> users) {
        this.context = context;
        this.users = users;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.upvoter_item, parent, false);
        }
        SimpleDraweeView view = (SimpleDraweeView) convertView;
        view.setImageURI(Uri.parse(users.get(position).getAvatarUrl()));
        return view;
    }
}
