package org.goodev.material.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import org.goodev.material.CollectionsActivity;
import org.goodev.material.CreateActivity;
import org.goodev.material.LoginActivity;
import org.goodev.material.PhotoActivity;
import org.goodev.material.PostActivity;
import org.goodev.material.R;
import org.goodev.material.UserActivity;
import org.goodev.material.api.Api;
import org.goodev.material.api.ErrorCallback;
import org.goodev.material.api.JsoupUtil;
import org.goodev.material.api.UrlUtil;
import org.goodev.material.model.Collection;
import org.goodev.material.model.Post;
import org.goodev.material.model.User;
import org.goodev.material.util.customtabs.CustomTabActivityHelper;
import org.goodev.utils.Utils;
import org.parceler.Parcels;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ADMIN on 2015/11/30.
 */
public class Launcher {

    public static final String EXTRA_USER = "extra.user";
    public final static String EXTRA_SHOT = "shot";
    public final static String EXTRA_FULL = "fulldata";
    public final static String EXTRA_ID = "id";
    public final static String EXTRA_URL = "url";
    public final static String EXTRA_PRE_URL = "pre_url";
    public final static String EXTRA_CHECKED = "extra.check";
    public final static String EXTRA_USER_COLLECTIONS = "extrra.uc";
    public final static String EXTRA_MY_COLLECTIONS = "extrra.myc";
    public final static String EXTRA_TITLE = "title";
    public final static String EXTRA_SORT = "extra.sort";
    public final static String EXTRA_CAT = "extra.cat";
    public final static String EXTRA_SUB = "extra.sub";
    public static final String EXTRA_SEARCH = "extra.search";

    public static void openPhoteView(Activity activity, Post shot, View view) {
        Intent intent = new Intent(activity, PhotoActivity.class);

        intent.putExtra(EXTRA_URL, shot.getImageUrl());
        intent.putExtra(EXTRA_TITLE, shot.getTitle());
        intent.putExtra(EXTRA_PRE_URL, shot.getTeaserUrl());
        String name = activity.getString(R.string.transition_shot);
        ViewCompat.setTransitionName(view, name);
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        Pair.create(view, name));

        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    public static void openPost(Activity activity, Post post, View view) {
        openPost(activity, post, view, false);
    }

    public static void openPost(Activity activity, Post post, View view, boolean search) {
        Intent intent = new Intent(activity, PostActivity.class);

        intent.putExtra(EXTRA_SHOT, Parcels.wrap(post));
        intent.putExtra(EXTRA_SEARCH, search);
        String name = activity.getString(R.string.transition_shot);
        ViewCompat.setTransitionName(view, name);
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        Pair.create(view, name),
                        Pair.create(view, activity.getString(R.string
                                .transition_shot_background)));

        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    public static void openUser(Activity activity, User user) {
        Intent intent = new Intent(activity, UserActivity.class);

        intent.putExtra(EXTRA_USER, Parcels.wrap(user));
        ActivityCompat.startActivity(activity, intent, null);
    }

    public static void openUser(Activity activity, User user, View view) {
        Intent intent = new Intent(activity, UserActivity.class);

        intent.putExtra(EXTRA_USER, Parcels.wrap(user));
        String name = activity.getString(R.string.transition_user_avatar);
        ViewCompat.setTransitionName(view, name);
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        Pair.create(view, name));

        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    public static void openCollections(Activity activity, Collection data, View view) {
        Intent intent = new Intent(activity, CollectionsActivity.class);
        intent.putExtra(EXTRA_ID, data.getSlug());
        intent.putExtra(EXTRA_TITLE, data.getName());

        ActivityCompat.startActivity(activity, intent, null);

    }

    public static void openUrl(Activity context, String url) {
        //TODO 如果是dribbble， 并且没有安装 droiddddle， 提示安装
//        if (isDirbbble(url)) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            openUrlWithCustomTab(context, url);
        }
    }

    public static void openPlayStore(Activity context, String pkg) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + pkg)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + pkg)));
        }
    }

    public static void openBrowserUrl(Activity context, String url) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }


    public static boolean isDirbbble(String url) {
        return url.startsWith("https://dribbble.com") ||
                url.startsWith("http://dribbble.com") ||
                url.startsWith("https://www.dribbble.com") ||
                url.startsWith("http://www.dribbble.com");
    }

    public static void openUrlWithCustomTab(Activity context, String url) {
        CustomTabActivityHelper.openCustomTab(
                context,
                new CustomTabsIntent.Builder()
                        .setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .build(),
                Uri.parse(url));
    }

    public static void launchUser(Activity context, String path, String avatar, String currentId, View view) {
        if (!Utils.hasInternet(context)) {
            Toast.makeText(context, R.string.check_network, Toast.LENGTH_SHORT).show();
            return;
        }
        String id = User.getUserId(path);

        if (TextUtils.isEmpty(id)) {
            UI.showToast(context, R.string.can_not_display_this_user);
            return;
        }
        if (id.equals(currentId)) {
            return;
        }
        User user = new User();
        user.setTeaserUrl(avatar);
        final Dialog dialog = UI.showProgressDialog(context, R.string.loading);
        Api.getApiService().getUserInfo(id)
                .map(responseBody -> JsoupUtil.convertToUser(user, responseBody))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    if (view == null) {
                        Launcher.openUser(context, user);
                    } else {
                        Launcher.openUser(context, user, view);
                    }
                }, new ErrorCallback(context));
    }

    public static void launchUrl(Activity context, String url) {
        if (TextUtils.isEmpty(url)) {
            UI.showToast(context, R.string.source_url_not_exist);
            return;
        }
        if (url.startsWith("/")) {
            url = Api.getEndpoint() + url;
        }
        final Dialog dialog = UI.showProgressDialog(context, R.string.loading);
        UrlUtil.getRedirectUrl(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    UI.dismissDialog(dialog);
                    Launcher.openUrl(context, s);
                });
    }


    public static void openLogin(Activity activity) {
        Intent login = new Intent(activity, LoginActivity.class);
        activity.startActivity(login);
    }

    public static void openLogin(Activity activity, int code) {
        Intent login = new Intent(activity, LoginActivity.class);
        activity.startActivityForResult(login, code);
    }

    public static void openPost(Activity activity, Post data) {
        Intent intent = new Intent(activity, PostActivity.class);
        intent.putExtra(EXTRA_SHOT, Parcels.wrap(data));
        intent.putExtra(EXTRA_FULL, true);
        activity.startActivity(intent);
    }

    public static void openCreateActivity(Activity activity, View view) {
        Intent intent = new Intent(activity, CreateActivity.class);
        activity.startActivity(intent);
    }
}
