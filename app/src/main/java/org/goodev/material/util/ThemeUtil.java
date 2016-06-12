package org.goodev.material.util;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

public final class ThemeUtil {

    private static final int DEFAULT_COLOR = Color.parseColor("#C2185B");


    private ThemeUtil() {
    }


    public static int getThemeColor(Context context, int id) {
        Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(new int[]{id});
        int result = a.getColor(0, DEFAULT_COLOR);
        a.recycle();
        return result;
    }

    public static boolean getThemeDark(Context context, int id) {
        Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(new int[]{id});
        boolean result = a.getBoolean(0, false);
        a.recycle();
        return result;
    }

    //    public static Drawable getThemeButtonBackground(Context context) {
    //        Theme theme = context.getTheme();
    //        TypedArray a = theme.obtainStyledAttributes(new int[] {
    //            R.attr.button_background
    //        });
    //        Drawable result = a.getDrawable(0);
    //        a.recycle();
    //        return result;
    //    }

    public static Drawable getThemeDrawable(Context context, int res) {
        Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(new int[]{res});
        Drawable result = a.getDrawable(0);
        a.recycle();
        return result;
    }

    // public static int getActivatiedRes(Context context) {
    // int theme = getSelectTheme();
    // switch (theme) {
    // case 0:
    // return R.drawable.fonter_activated_background_holo_light;
    // case 2:
    // return R.drawable.purple_activated_background_holo_light;
    // case 3:
    // return R.drawable.amber_activated_background_holo_light;
    // case 4:
    // return R.drawable.blue_activated_background_holo_light;
    //
    // default:
    // break;
    // }
    // return R.drawable.fonter_activated_background_holo_light;
    //
    // Theme theme = context.getApplicationContext().getTheme();
    // TypedArray a = theme.obtainStyledAttributes(new int[]
    // {R.attr.fonter_activated_indicator});
    //
    // int result = a.getResourceId(0, 0);
    // a.recycle();
    // System.out.println("r........... "+ result
    // +"   "+R.drawable.fonter_activated_background_holo_light);
    // return result;
    // }
}
