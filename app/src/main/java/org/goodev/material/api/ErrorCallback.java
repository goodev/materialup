package org.goodev.material.api;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;

import org.goodev.material.util.UI;

import rx.functions.Action1;

/**
 * Created by ADMIN on 2015/1/1.
 */
public class ErrorCallback implements Action1<Throwable> {
    private Context mContext;
    private Dialog mDialog;

    public ErrorCallback(Context context) {
        mContext = context;
    }

    public ErrorCallback(Context context, Dialog dialog) {
        mContext = context;
        mDialog = dialog;
    }

    @Override
    public void call(Throwable throwable) {
        Log.e("err", "error" + throwable);
        if (mContext != null) {
            UI.toastError(mContext, throwable);
        }
        if (mDialog != null) {
            UI.dismissDialog(mDialog);
        }
    }
}
