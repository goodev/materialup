package org.goodev.material.api;

import android.app.Dialog;
import android.content.Context;
import android.widget.Toast;

import org.goodev.material.util.UI;

import rx.functions.Action1;

/**
 * Created by ADMIN on 2015/1/1.
 */
public class SucessCallback<E> implements Action1<E> {
    private Context mContext;
    private int mResId;
    private Dialog mDialog;

    public SucessCallback(Context context, int res) {
        mContext = context;
        mResId = res;
    }

    public SucessCallback(Context context, int res, Dialog dialog) {
        mContext = context;
        mResId = res;
        mDialog = dialog;
    }

    @Override
    public void call(E o) {
        UI.dismissDialog(mDialog);
        Toast.makeText(mContext, mResId, Toast.LENGTH_LONG).show();
    }
}
