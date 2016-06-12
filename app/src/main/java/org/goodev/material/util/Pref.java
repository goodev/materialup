package org.goodev.material.util;

import android.content.Context;
import android.content.SharedPreferences;

import org.goodev.material.App;
import org.goodev.material.BuildConfig;
import org.goodev.material.api.Api;
import org.goodev.material.model.User;

/**
 * Created by yfcheng on 2015/12/4.
 */
public class Pref {

    private static Pref sPref;

    private static final String NAME = "pref";
    private static final String KEY_LOGIN = "logined";
    private SharedPreferences pref;

    private Pref(Context context) {
        pref = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public boolean isLogin() {
        return pref.getBoolean(KEY_LOGIN, false);
    }

    public void setLogin(boolean login) {
        pref.edit().putBoolean(KEY_LOGIN, login).apply();
    }

    public static Pref get(Context context) {
        if (sPref == null) {
            sPref = new Pref(context.getApplicationContext());
        }
        return sPref;
    }

    public static Pref get() {
        Context context = App.getIns();
        return get(context);
    }


    private static final String KEY_AVATAR = "avatar";

    public void setUserAvatar(String url) {
        pref.edit().putString(KEY_AVATAR, url).apply();
    }

    public String getUserAvatar() {
        return pref.getString(KEY_AVATAR, null);
    }


    private static final String KEY_FL = "firstl";

    public boolean isFirstLaunch() {
        return pref.getBoolean(KEY_FL + BuildConfig.VERSION_CODE, true);
    }

    public void setFirstlanch() {
        pref.edit().putBoolean(KEY_FL + BuildConfig.VERSION_CODE, false).apply();
    }

    private static final String KEY_NAME = "username";
    private static final String KEY_PATH = "userurl";
    private static final String KEY_ = "";

    public void setLoginUserInfo(User user) {
        pref.edit().putString(KEY_AVATAR, user.avatarUrl)
                .putString(KEY_NAME, user.fullName)
                .putString(KEY_PATH, user.path)
                .apply();
    }

    public String getUserName() {
        return pref.getString(KEY_NAME, null);
    }

    public String getUserPath() {
        return pref.getString(KEY_PATH, null);
    }

    public void clearUserInfo() {
        pref.edit().remove(KEY_NAME)
                .remove(KEY_PATH).apply();
    }

    private static final String KEY_SITE_INDEX = "site_index";

    public void updateCurrentSite(int index) {
        pref.edit()
                .putInt(KEY_SITE_INDEX, index)
                .apply();
    }

    public int getCurrentSite() {
        return pref.getInt(KEY_SITE_INDEX, Api.MU_INDEX);
    }
}
