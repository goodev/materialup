package org.goodev.material.ui;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import org.goodev.material.R;
import org.goodev.material.model.User;
import org.goodev.material.util.Launcher;

import java.util.List;

/**
 * Created by yfcheng on 2015/12/9.
 */
public class Dialogs {

    public static AlertDialog getUpvoterDialog(Activity context, List<User> users) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        GridView view = (GridView) LayoutInflater.from(context).inflate(R.layout.grid_view, null);
        builder.setView(view);
        builder.setTitle(R.string.upvoters);
        view.setAdapter(new UpvotersAdapter(context, users));
        final AlertDialog dialog = builder.create();
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = (User) parent.getAdapter().getItem(position);
                Launcher.launchUser(context, user.getPath(), user.getAvatarUrl(), null, null);
                dialog.dismiss();
            }
        });

        dialog.show();

        return dialog;
    }

    public static void showFirstLaunchInfo(Activity context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.notices);
        builder.setMessage(R.string.first_use_message);
        builder.setPositiveButton(android.R.string.ok, null);

        builder.show();
    }
}
