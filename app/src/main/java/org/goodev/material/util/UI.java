package org.goodev.material.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.Toast;

import org.goodev.material.App;
import org.goodev.material.R;
import org.goodev.material.api.Api;
import org.goodev.material.model.Post;
import org.goodev.widget.DividerItemDecoration;

import retrofit.HttpException;

/**
 * Created by yfcheng on 2015/11/26.
 */
public class UI {
    public static final String TYPE = "type";

    public static final String ARG_CURRENT_PAGE = "extra_page";
    public static final String ARG_CURRENT_LIST_INDEX = "extra_list_index";
    public static final String ARG_DATA_LIST = "extra_data_list";
    public static final String ARG_MENU_INDEX = "extra_menu_index";
    public static final String DROIDDDLE = "org.goodev.droidddle";
    public static final long DROIDDDLE_ID = -111;


    public static boolean isAppInstalled(Context context, String packageName) {
        if (context == null) {
            return false;
        }
        PackageManager pm = context.getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return installed;
    }

    public static DividerItemDecoration getDividerItemDecoration(Resources resources) {
        Drawable drawable = resources.getDrawable(R.drawable.abc_list_divider_mtrl_alpha);
        int color = resources.getColor(R.color.gray_background);
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        DividerItemDecoration divider = new DividerItemDecoration(drawable);
        int padding = (int) resources.getDimension(R.dimen.keyline_1);
        divider.setPadding(padding, padding);
        return divider;
    }

    public static ProgressDialog showProgressDialog(Context context, String message,
                                                    DialogInterface.OnCancelListener listener) {
        return showProgressDialog(context, null, message, Color.CYAN, listener);
    }

    public static ProgressDialog showProgressDialog(Context context, String message) {
        return showProgressDialog(context, message, null);
    }

    public static ProgressDialog showProgressDialog(Context context, int msg) {
        return showProgressDialog(context, context.getString(msg), null);
    }

    public static ProgressDialog showProgressDialog(Context context, String message, int color,
                                                    DialogInterface.OnCancelListener listener) {
        return showProgressDialog(context, null, message, color, listener);
    }

    public static ProgressDialog showProgressDialog(Context context, String title, String message,
                                                    int color, DialogInterface.OnCancelListener listener) {
//        int width = (int) context.getResources().getDimension(R.dimen.progress_width);
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setMessage(message);
        if (!TextUtils.isEmpty(title)) {
            dialog.setTitle(title);
        }
//        Drawable d = new CircularProgressDrawable(color, width);
//        dialog.setIndeterminateDrawable(d);
        dialog.setCanceledOnTouchOutside(false);
        if (listener != null) {
            dialog.setOnCancelListener(listener);
        }
        dialog.show();

        return dialog;
    }

    public static void dismissDialog(Dialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public static void toastServerErrorMessage(Context context, HttpException error) {
        String msg = Api.getServerErrorMessage(error);
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void toastError(Context context, Throwable error) {
        if (error instanceof HttpException) {
            HttpException retrofitError = (HttpException) error;
            try {
                if (retrofitError.code() == 401) {
//           TODO         OAuthUtils.clearOAuthCredential(context);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            toastServerErrorMessage(context, retrofitError);
        } else {
            showToast(context, R.string.general_error);
        }
    }

    public static void showToast(Context context, int res) {
        Toast.makeText(context, res, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, String res) {
        Toast.makeText(context, res, Toast.LENGTH_LONG).show();
    }


    public static Spanned getParsedText(ColorStateList linkTextColor,
                                        @ColorInt int linkHighlightColor, String description) {
        if (!TextUtils.isEmpty(description)) {
            return HtmlUtils.parseHtml(description, linkTextColor, linkHighlightColor);
        }
        return null;
    }

    public static Spanned getParsedText(String text) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }

        ColorStateList csl = ContextCompat.getColorStateList(App.getIns(), R.color.dribbble_links);
        int color = ContextCompat.getColor(App.getIns(), R.color.dribbble_link_highlight);
        return getParsedText(csl, color, text);
    }


    public static boolean notLogin(Activity activity) {
        if (App.isLogin()) {
            return false;
        }

        Launcher.openLogin(activity);
        return true;
    }


    public static Post getDroidddlePost() {
        //http://assets.materialup.com/uploads/ebc13f4c-2397-4836-a75a-f22281663465/800x600.png
        Post post = new Post();
        post.id = UI.DROIDDDLE_ID;
        post.imageUrl = "http://assets.materialup.com/uploads/ebc13f4c-2397-4836-a75a-f22281663465/800x600.png";
        post.userName = "Brett";
        post.avatarUrl = "https://pbs.twimg.com/profile_images/610846198803529728/Mea3gsRy_normal.jpg";
        post.title = "Droidddle - Material App for dribbble.com. Install it now.";
        post.votes = 30;
        post.userUrl = "/brettbrdls";
        post.source = "android";

        return post;
    }

    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= 21;
    }
}
